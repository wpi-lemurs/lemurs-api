import os
from typing import Optional, Dict, List, Any
from dotenv import load_dotenv

import dash
from dash import Dash, html, dcc, Input, Output, State, dash_table
import pandas as pd
from plotly_calplot import calplot
from heatmap import build_individual_heatmap

from datetime import datetime
from zoneinfo import ZoneInfo

import psycopg2
from psycopg2.extras import RealDictCursor

# --- Database Configuration ---
load_dotenv()
DB_HOST = os.getenv("DB_HOST", "localhost")
DB_PORT = int(os.getenv("DB_PORT", "5432"))
DB_NAME = os.getenv("DB_NAME", "lemurs")
DB_USER = os.getenv("DB_USER")
DB_PASSWORD = os.getenv("DB_PASSWORD")

# --- Table Names ---
USERS_TABLE = os.getenv("USERS_TABLE", "umass_id")
START_DATE_TABLE = os.getenv("START_DATE_TABLE", "progress")
SURVEY_TABLE = os.getenv("SURVEY_TABLE", "survey_response")
DANGER_ALERT_TABLE = os.getenv("DANGER_ALERT_TABLE", "danger_alert")
ANSWER_TABLE = os.getenv("ANSWER_TABLE", "answer")


def get_connection():
    """Establishes a connection to the PostgreSQL database."""
    return psycopg2.connect(
        host=DB_HOST, port=DB_PORT, dbname=DB_NAME, user=DB_USER,
        password=DB_PASSWORD, connect_timeout=5,
    )


def fetch_user_by_id(user_id: int) -> Optional[Dict[str, Any]]:
    """
    Return a single user row by ID (adjust the WHERE column to match your schema).
    """
    sql = f"""
        SELECT 
            u.umass_id,
            sr.timestamp AS started,
            (
                SELECT MAX(da.created_at)
                FROM {DANGER_ALERT_TABLE} AS da
                JOIN {ANSWER_TABLE} AS a ON da.answer_id = a.id
                JOIN {SURVEY_TABLE} AS sr2 ON a.survey_response_id = sr2.id
                WHERE sr2.app_user_id = u.app_user_id
            ) AS latest_danger_alert
        FROM {USERS_TABLE} AS u
        NATURAL JOIN {SURVEY_TABLE} AS sr
        WHERE u.app_user_id = %s
    """
    try:
        with get_connection() as conn:
            with conn.cursor(cursor_factory=RealDictCursor) as cur:
                cur.execute(sql, (user_id,))
                row = cur.fetchone()
                return dict(row) if row is not None else None
    except Exception as e:
        return {"__error__": f"{type(e).__name__}: {e}"}


# --- Database Fetch Functions ---
def fetch_all_users() -> List[Dict[str, Any]]:
    """Return ALL users with their start date, danger alert status, and surveys in the last 7 days."""
    sql = f"""
    SELECT
        u.app_user_id,
        u.umass_id,
        MIN(p.started) AS started,
        MAX(da.created_at) AS latest_danger_alert,
        (
        SELECT COUNT(*)
        FROM {SURVEY_TABLE} sr2
        WHERE sr2.app_user_id = u.app_user_id
            AND sr2.timestamp >= NOW() - INTERVAL '7 days'
        )::int AS surveys_last_7d

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
        with get_connection() as conn, conn.cursor(cursor_factory=RealDictCursor) as cur:
            cur.execute(sql)
            return [dict(r) for r in cur.fetchall()]
    except Exception as e:
        return [{"__error__": f"{type(e).__name__}: {e}"}]


def fetch_user_by_id(user_id: int) -> Optional[Dict[str, Any]]:
    """
    Return a single user row by ID (adjust the WHERE column to match your schema).
    """
    sql = f"""
        SELECT 
            u.umass_id,
            sr.timestamp AS started,
            (
                SELECT MAX(da.created_at)
                FROM {DANGER_ALERT_TABLE} AS da
                JOIN {ANSWER_TABLE} AS a ON da.answer_id = a.id
                JOIN {SURVEY_TABLE} AS sr2 ON a.survey_response_id = sr2.id
                WHERE sr2.app_user_id = u.app_user_id
            ) AS latest_danger_alert
        FROM {USERS_TABLE} AS u
        NATURAL JOIN {SURVEY_TABLE} AS sr
        WHERE u.app_user_id = %s
    """
    try:
        with get_connection() as conn:
            with conn.cursor(cursor_factory=RealDictCursor) as cur:
                cur.execute(sql, (user_id,))
                row = cur.fetchone()
                return dict(row) if row is not None else None
    except Exception as e:
        return {"__error__": f"{type(e).__name__}: {e}"}


def fetch_dates_by_id(user_id: int) -> List[Dict[str, Any]]:
    """Return a single user's survey timestamps by app_user_id."""
    sql = f"SELECT timestamp FROM {SURVEY_TABLE} WHERE app_user_id = %s"
    try:
        with get_connection() as conn, conn.cursor(cursor_factory=RealDictCursor) as cur:
            cur.execute(sql, (user_id,))
            return [dict(r) for r in cur.fetchall()]
    except Exception as e:
        return [{"__error__": f"{type(e).__name__}: {e}"}]


def fetch_danger_reason_by_user_id(user_id: int) -> List[Dict[str, Any]]:
    """
    Fetch danger alert details for a specific user from the view.
    Displays the related question, user's answer, timestamp, and alert message.
    """
    sql = """
        SELECT question, answer, created_at, alert_message
        FROM all_danger_alert
        WHERE app_user_id = %s
        ORDER BY created_at DESC
    """
    try:
        with get_connection() as conn, conn.cursor(cursor_factory=RealDictCursor) as cur:
            cur.execute(sql, (user_id,))
            rows = cur.fetchall()
            return [dict(r) for r in rows]
    except Exception as e:
        return [{"__error__": f"{type(e).__name__}: {e}"}]


def process_records_for_display(records: List[Dict[str, Any]]):
    """Formats records and creates column definitions for the DataTable."""
    if not records or "__error__" in records[0]:
        return [], []

    if "__error__" in records[0]:
        # Show first error if we bubbled one up
        return html.Div(f"Error querying the database: {records[0]['__error__']}")

    # Format any datetime values, and additionally, input a placeholder for those without danger alerts.
    for r in records:
        if r.get("started") and isinstance(r["started"], datetime):
            utc_time = r["started"].replace(tzinfo=ZoneInfo("UTC"))
            est_time = utc_time.astimezone(ZoneInfo("America/New_York"))
            r["started"] = est_time.strftime("%m-%d-%Y, %I:%M %p %Z").lstrip("0")
        if "latest_danger_alert" in r and isinstance(r["latest_danger_alert"], datetime):
            utc_time = r["latest_danger_alert"].replace(tzinfo=ZoneInfo("UTC"))
            est_time = utc_time.astimezone(ZoneInfo("America/New_York"))
            r["latest_danger_alert"] = est_time.strftime("%m-%d-%Y, %I:%M %p %Z").lstrip("0")
        # Check for null danger alert and set to "None"
        if "latest_danger_alert" in r and r["latest_danger_alert"] is None:
            r["latest_danger_alert"] = "None"

        # Display how many surveys completed out of 15 in last 7 days
        if "surveys_last_7d" in r and r["surveys_last_7d"] is not None:
            r["surveys_last_7d"] = f"{int(r['surveys_last_7d'])}/15"

    columns = [{"name": "Received Danger Alert?" if k == "has_danger_alert" else
    f"{k} (MM-DD-YY)" if k == "started" else k,
                "id": k} for k in records[0].keys()]

    return records, columns


# --- Dash App Initialization ---
app = Dash(__name__, requests_pathname_prefix='/dashboard/')
app.title = "Lemurs RADAR"

app.layout = html.Div(
    style={"fontFamily": "Roboto", "padding": "0 16px 16px 16px"},
    children=[
        html.H1("LEMURS RADAR", style={"position": "sticky", "top": 0, "padding": "10px 16px",
                                       "background": "#1c5f6e", "color": "white", "margin": "0 -16px", "zIndex": 1000}),
        html.P("Enter an app_user_id to search, or click a table row to see details for a specific user.",
               style={"color": "#555", "marginTop": "1rem"}),
        html.Div(
            style={"display": "flex", "gap": "8px", "alignItems": "center", "marginTop": "16px"},
            children=[
                dcc.Input(id="user-id-input", type="number", placeholder="app_user_id (optional)", debounce=True,
                          style={"flex": "0 0 200px", "height": "36px", "padding": "0 8px"}),
                html.Button("Load / Show All", id="load-btn", n_clicks=0, style={"height": "36px",
                                                                                 "padding": "0 14px",
                                                                                 "border": "1px solid #ccc",
                                                                                 "borderRadius": "8px",
                                                                                 "cursor": "pointer",
                                                                                 "background": "#f7f7f7"}),
            ]
        ),
        html.Div(id="status", style={"marginTop": "12px", "color": "#666"}),
        html.Hr(style={"margin": "16px 0"}),
        dcc.Loading(id="loading", type="circle", children=[
            dash_table.DataTable(
                id="users-table", columns=[], data=[], page_size=20,
                style_table={"overflowX": "auto", "position": "relative", "zIndex": "0"},
                style_cell={"padding": "8px", "border": "1px solid #eee", "fontFamily": "system-ui",
                            "fontSize": "14px", "textAlign": "left", "minWidth": "120px",
                            "whiteSpace": "normal", "cursor": "pointer"},
                style_header={"backgroundColor": "#fafafa", "fontWeight": "600", "border": "1px solid #ddd",
                              "zIndex": "0"},
                style_data_conditional=[{'if': {'row_index': 'odd'}, 'backgroundColor': 'rgb(248, 248, 248)'}],
            ),
            html.Div(id="plot-container")
        ]),
        html.Div(id="debug",
                 style={"whiteSpace": "pre-wrap", "fontSize": "12px", "color": "#999", "marginTop": "12px"}),
    ]
)


@app.callback(
    Output("users-table", "data"),
    Output("users-table", "columns"),
    Output("plot-container", "children"),
    Output("status", "children"),
    Output("debug", "children"),
    Input("load-btn", "n_clicks"),
    Input("users-table", "active_cell"),
    State("user-id-input", "value"),
    State("users-table", "data"),
)
def update_display(load_clicks, active_cell, user_id_from_input, table_data):
    """Main callback to handle searching, loading all users, and row clicks."""
    ctx = dash.callback_context
    triggered_id = ctx.triggered[0]["prop_id"].split(".")[0] if ctx.triggered else "initial_load"

    user_id_to_fetch = None

    if triggered_id == "load-btn" and user_id_from_input:
        user_id_to_fetch = user_id_from_input
    elif triggered_id == "users-table" and active_cell and table_data:
        user_id_to_fetch = table_data[active_cell["row"]].get("app_user_id")

    # --- SHOW SINGLE USER ---
    if user_id_to_fetch:
        try:
            user_id = int(user_id_to_fetch)
        except (ValueError, TypeError):
            return dash.no_update, dash.no_update, "", "Invalid app_user_id.", "ID must be an integer."

        record = fetch_user_by_id(user_id)
        if record is None:
            return [], [], "", f"No user found for app_user_id={user_id}.", ""
        if "__error__" in record:
            return [], [], "", "Error fetching user.", f"Details: {record['__error__']}"

        data, columns = process_records_for_display([record])
        status = f"Displaying details for app_user_id={user_id}."

        plot_content, debug_msg = "", ""
        survey_dates = fetch_dates_by_id(user_id)
        if not survey_dates:
            plot_content = html.P("No survey activity found for this user.", style={"marginTop": "16px"})
        elif "__error__" in survey_dates[0]:
            debug_msg = f"Error fetching survey dates: {survey_dates[0]['__error__']}"
        else:
            plot_content, _, debug_msg = build_individual_heatmap(survey_dates=survey_dates, table=None, status=None)

        danger_details = fetch_danger_reason_by_user_id(user_id)
        danger_display = html.Div()

        if not danger_details:
            danger_display = html.P("No danger alert records found.", style={"marginTop": "16px", "color": "#666"})
        elif "__error__" in danger_details[0]:
            debug_msg += f"\nError fetching danger details: {danger_details[0]['__error__']}"
        else:
            rows = []
            for d in danger_details:
                created = d.get("created_at", "N/A")
                if isinstance(created, datetime):
                    utc_time = created.replace(tzinfo=ZoneInfo("UTC"))
                    est_time = utc_time.astimezone(ZoneInfo("America/New_York"))
                    created = est_time.strftime("%m-%d-%Y, %I:%M %p %Z").lstrip("0")
                answer = d.get("answer", "N/A")
                alert_mess = d.get("alert_message", "N/A")
                alert_mess = alert_mess.replace("{score}",
                                                answer if answer.isnumeric() else "1" if answer.lower() == "yes" else "0")
                rows.append(html.Tr([
                    html.Td(created),
                    html.Td(d.get("question", "N/A")),
                    html.Td(answer),
                    html.Td(alert_mess),
                ]))

            danger_display = html.Div([
                html.H4("Danger Alert Details", style={"marginTop": "20px", "color": "#b22222"}),
                html.Table(
                    [
                        html.Thead(html.Tr([
                            html.Th("Created At"), html.Th("Question"),
                            html.Th("Answer"), html.Th("Alert Message")
                        ])),
                        html.Tbody(rows)
                    ],
                    style={"width": "100%", "borderCollapse": "collapse", "marginTop": "10px"},
                ),
            ])

        plot_section = html.Div([plot_content, danger_display])
        return data, columns, plot_section, status, debug_msg

    # --- SHOW ALL USERS (DEFAULT on initial load or "Load" with empty input) ---
    rows = fetch_all_users()
    if not rows or "__error__" in rows[0]:
        detail = rows[0]['__error__'] if rows else "No data."
        return [], [], "", "Error fetching users.", f"Details: {detail}"

    data, columns = process_records_for_display(rows)
    status = f"Loaded {len(data)} user(s). Click a row for details."
    return data, columns, "", status, ""


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5433, debug=True)
