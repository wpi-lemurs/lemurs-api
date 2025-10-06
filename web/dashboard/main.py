import os
from typing import Optional, Dict, List, Any
from datetime import datetime
from zoneinfo import ZoneInfo

from heatmap import build_individual_heatmap
import psycopg2
from psycopg2.extras import RealDictCursor

from dash import Dash, html, dcc, Input, Output, State, dash_table
from werkzeug.middleware.proxy_fix import ProxyFix

import pandas as pd

DB_HOST = os.getenv("DB_HOST", "localhost")
DB_PORT = int(os.getenv("DB_PORT", "5432"))
DB_NAME = os.getenv("DB_NAME", "lemurs")
DB_USER = os.getenv("DB_USER", "lemurs")
DB_PASSWORD = os.getenv("DB_PASSWORD", "x3a-aJs-12M-Bah")

# Table names
USERS_TABLE = os.getenv("USERS_TABLE", "umass_id")
START_DATE_TABLE = os.getenv("START_DATE_TABLE", "progress")
SURVEY_TABLE = os.getenv("SURVEY_TABLE", "survey_response")
DANGER_ALERT_TABLE = os.getenv("DANGER_ALERT_TABLE", "danger_alert")
ANSWER_TABLE = os.getenv("ANSWER_TABLE", "answer")

IS_DEV = os.getenv("DEV", "0") == "1"

def get_connection():
    return psycopg2.connect(
        host=DB_HOST,
        port=DB_PORT,
        dbname=DB_NAME,
        user=DB_USER,
        password=DB_PASSWORD,
        connect_timeout=5,
    )

def fetch_user_by_id(user_id: int) -> Optional[Dict[str, Any]]:
    """
    Return a single user row by ID (adjust the WHERE column to match your schema).
    """
    sql = (
        f"SELECT {USERS_TABLE}.umass_id, {START_DATE_TABLE}.started "
        f"FROM {USERS_TABLE} NATURAL JOIN {START_DATE_TABLE} "
        f"WHERE {USERS_TABLE}.app_user_id = %s"
    )
    try:
        with get_connection() as conn:
            with conn.cursor(cursor_factory=RealDictCursor) as cur:
                cur.execute(sql, (user_id,))
                row = cur.fetchone()
                return dict(row) if row is not None else None
    except Exception as e:
        return {"__error__": f"{type(e).__name__}: {e}"}

def fetch_dates_by_id(user_id: int) -> Optional[Dict[str, Any]]:
    """
    Return a single user's start date by ID.
    """
    sql = (
        f"SELECT {USERS_TABLE}.umass_id, {SURVEY_TABLE}.timestamp "
        f"FROM {USERS_TABLE} NATURAL JOIN {SURVEY_TABLE} "
        f"WHERE {USERS_TABLE}.app_user_id = %s"
    )
    try:
        with get_connection() as conn:
            with conn.cursor(cursor_factory=RealDictCursor) as cur:
                cur.execute(sql, (user_id,))
                rows = cur.fetchall()
                return [dict(r) for r in rows]
    except Exception as e:
        return {"__error__": f"{type(e).__name__}: {e}"}

def fetch_all_users() -> List[Dict[str, Any]]:
    """
    Return ALL users with their (earliest) start date and a flag indicating
    whether they have at least one danger alert.
    """
    sql = f"""
    SELECT
        u.app_user_id,
        u.umass_id,
        MIN(p.started) AS started,
        CASE WHEN COUNT(DISTINCT da.answer_id) > 0 THEN TRUE ELSE FALSE END AS has_danger_alert
    FROM {USERS_TABLE} AS u
    LEFT JOIN {START_DATE_TABLE} AS p
        ON u.app_user_id = p.app_user_id
    LEFT JOIN {SURVEY_TABLE} AS sr
        ON u.app_user_id = sr.app_user_id
    LEFT JOIN {ANSWER_TABLE} AS a
        ON sr.id = a.survey_response_id
    LEFT JOIN {DANGER_ALERT_TABLE} AS da
        ON a.id = da.answer_id
    GROUP BY u.app_user_id, u.umass_id
    ORDER BY u.app_user_id
    """
    try:
        with get_connection() as conn:
            with conn.cursor(cursor_factory=RealDictCursor) as cur:
                cur.execute(sql)
                rows = cur.fetchall()
                return [dict(r) for r in rows]
    except Exception as e:
        return [{"__error__": f"{type(e).__name__}: {e}"}]



def render_records_table(records: List[Dict[str, Any]]):
    if not records:
        return html.Div("No rows found.")

    if "__error__" in records[0]:
        # Show first error if we bubbled one up
        return html.Div(f"Error querying the database: {records[0]['__error__']}")
    
    # Format any datetime values 
    for r in records:
        if "started" in r and isinstance(r["started"], datetime):
            utc_time = r["started"].replace(tzinfo=ZoneInfo("UTC"))
            est_time = utc_time.astimezone(ZoneInfo("America/New_York"))
            r["started"] = est_time.strftime("%m-%d-%Y, %I:%M %p %Z").lstrip("0")


    # Build columns from keys (kept stable order) and append (MM-DD-YY) to started 
    columns = [{"name": f"{k} (MM-DD-YY)" if k == "started" else ("Received Danger Alert?" if k == "has_danger_alert" else k),
    "id": k} for k in records[0].keys()]
    return dash_table.DataTable(
        id="users-table",
        columns=columns,
        data=records,
        page_size=20,
        style_table={"overflowX": "auto"},
        style_cell={
            "padding": "8px",
            "border": "1px solid #eee",
            "fontFamily": "system-ui, -apple-system, Segoe UI, Roboto, Helvetica, Arial, sans-serif",
            "fontSize": "14px",
            "textAlign": "left",
            "minWidth": "120px",
            "width": "120px",
            "maxWidth": "300px",
            "whiteSpace": "normal",
        },
        style_header={
            "backgroundColor": "#fafafa",
            "fontWeight": "600",
            "border": "1px solid #ddd",
        },
    )

app = Dash(
    __name__,
    requests_pathname_prefix="/dash/lemurs/",  # <- NEW
    routes_pathname_prefix="/dash/lemurs/",    # <- NEW
)
app.title = "Lemurs RADAR"

print("Prefix settings:", app.config["requests_pathname_prefix"], app.config["routes_pathname_prefix"])

server = app.server

@server.after_request
def add_iframe_headers(resp):
    if IS_DEV:
        resp.headers["Content-Security-Policy"] = "frame-ancestors 'self' http://localhost:3000"
        # Drop X-Frame-Options in dev to avoid cross-origin block if you don't proxy.
        # If you DO proxy (recommended), SAMEORIGIN is fine since origin is localhost:3000.
        resp.headers["X-Frame-Options"] = "SAMEORIGIN"
    else:
        resp.headers["Content-Security-Policy"] = "frame-ancestors 'self' https://lemurs-dev.wpi.edu"
        resp.headers["X-Frame-Options"] = "SAMEORIGIN"
    return resp

server.wsgi_app = ProxyFix(  # type: ignore[attr-defined]
    server.wsgi_app,
    x_for=1, x_proto=1, x_host=1, x_port=1, x_prefix=1
)

app.layout = html.Div(
    style={"fontFamily": "Roboto"},
    children=[
        html.H1(
            "LEMURS RADAR",
            style={"position": "sticky", "top": 0, "padding": "10px 16px",
                   "background": "#1c5f6e", "color": "white"}
        ),
        html.P(
            "Leave the ID empty and click Load to fetch the entire table, "
            "or enter an umass_id to fetch just one row.",
            style={"color": "#555", "marginTop": "0.25rem"}
        ),
        html.Div(
            style={"display": "flex", "gap": "8px", "alignItems": "center", "marginTop": "16px"},
            children=[
                dcc.Input(
                    id="user-id-input",
                    type="number",
                    placeholder="umass_id (optional)",
                    debounce=True,
                    style={"flex": "0 0 200px", "height": "36px", "padding": "0 8px"}
                ),
                html.Button(
                    "Load",
                    id="load-btn",
                    n_clicks=0,
                    style={
                        "height": "36px", "padding": "0 14px",
                        "border": "1px solid #ccc", "borderRadius": "8px",
                        "cursor": "pointer", "background": "#f7f7f7"
                    }
                ),
            ]
        ),
        html.Div(id="status", style={"marginTop": "12px", "color": "#666"}),
        html.Hr(style={"margin": "16px 0"}),
        html.Div(id="result-area"),
        html.Div(id="debug", style={"whiteSpace": "pre-wrap", "fontSize": "12px",
                                    "color": "#999", "marginTop": "12px"}),
    ]
)

@app.callback(
    Output("result-area", "children"),
    Output("status", "children"),
    Output("debug", "children"),
    Input("load-btn", "n_clicks"),
    State("user-id-input", "value"),
    prevent_initial_call=True,
)
def on_load_click(n_clicks: int, user_id_value):
    if user_id_value in (None, ""):
        rows = fetch_all_users()
        if rows and "__error__" in rows[0]:
            return (
                html.Div("Error querying the database."),
                "Check your tunnel/credentials and table names.",
                f"Details: {rows[0]['__error__']}"
            )
        table = render_records_table(rows)
        status = f"Loaded {len(rows)} row(s) from {USERS_TABLE} and {START_DATE_TABLE}."
        return (table, status, "")

    try:
        user_id = int(user_id_value)
    except Exception:
        return (html.Div("User umass_id must be an integer."), "Invalid umass_id.", "")

    record = fetch_user_by_id(user_id)
    if record is None:
        return (html.Div("No user found."), f"Query ran for umass_id={user_id}.", "")
    elif "__error__" in record:
        return (
            html.Div("Error querying the database."),
            "Check your tunnel/credentials and table names.",
            f"Details: {record['__error__']}"
        )
    else:
        table = render_records_table([record])
        status = f"Found record for umass_id={user_id}."

        # Fetch survey dates for the calendar heatmap
        survey_dates = fetch_dates_by_id(user_id)
        if survey_dates and "__error__" in survey_dates[0]:
            return (
                table,
                status,
                f"Error fetching survey dates: {survey_dates[0]['__error__']}"
            )
        elif survey_dates:
            return build_individual_heatmap(survey_dates, table, status)        
        else:
            return (html.Div([table, html.Div("No survey dates found.")]), status, "")

if __name__ == "__main__":
    # Keep it localhost; the web server will reverse-proxy to this
    app.run(host="127.0.0.1", port=5433, debug=True)
