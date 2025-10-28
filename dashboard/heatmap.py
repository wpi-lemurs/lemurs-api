import pandas as pd
import numpy as np
import plotly.graph_objects as go
from dash import dcc, html
from plotly_calplot import calplot


def build_individual_heatmap(survey_dates, table, status):
    # Prepare data for the calendar heatmap
    df = pd.DataFrame(survey_dates)
    df["timestamp"] = pd.to_datetime(df["timestamp"]).dt.date
    df["count"] = 1  # Add a count column for aggregation
    df = df.groupby("timestamp").sum().reset_index()

    # Create Bucket to limit colors to three levels. Darkest color is anything above 3. 
    def bucketize(count):
        if count == 1:
            return 1
        elif count == 2:
            return 2
        elif count >= 3:
            return 3
        return 0
    
    df["bucket"] = df["count"].apply(bucketize)
    
    # Create the calendar heatmap
    calplot_fig = calplot(
        df,
        x="timestamp",
        y="bucket",
        title=f"Survey Activity",
        gap=1,
        month_lines=True,
        month_lines_width=3,
        colorscale=[(0.00, "#c3edff"), (0.33, "#c3edff"), 
                    (0.33,"#579cac"), (0.66, "#579cac"),
                    (0.66, "#1c5f6e"), (1.00, "#1c5f6e")]
    )

    # Adjust figure size to create squares
    calplot_fig.update_layout(
        width=1600,
        height=250   
    )

    # Add start date and end date highlights
    start_date = df["timestamp"].min()
    end_date = df["timestamp"].max()

    for mark in [start_date, end_date]:
        # Find the position of this date on the x-axis
        x_vals = calplot_fig.data[0].x  # all day labels
        if mark in x_vals:
            idx = list(x_vals).index(mark)
            # Rectangle spanning ±0.5 around that cell
            calplot_fig.add_shape(
                type="rect",
                xref="x", yref="y",
                x0=idx - 0.5, x1=idx + 0.5,
                y0=-0.5, y1=0.5,  # since heatmap is one row tall
                line=dict(color="gold", width=3),
                fillcolor="rgba(0,0,0,0)",
                layer="above"
            )

    calendar_plot = dcc.Graph(figure=calplot_fig)
    return (html.Div([table, calendar_plot]), status, "")

# Weekly Calendar function, does not work properly, can delete if no valuable nuggets can be extracted. 
def build_calendar_plot(survey_dates, table, status):

    if not survey_dates:
        return (html.Div([table, html.Div("No survey dates found.")]), status, "")

    # --- Prepare dataframe ---
    df = pd.DataFrame(survey_dates)
    df["timestamp"] = pd.to_datetime(df["timestamp"]).dt.date
    df["count"] = 1
    df = df.groupby("timestamp").sum().reset_index()

    # Add weekday + week number for calendar layout
    df["dow"] = pd.to_datetime(df["timestamp"]).dt.weekday  # 0=Mon, 6=Sun
    df["week"] = pd.to_datetime(df["timestamp"]).dt.isocalendar().week

    # Pivot into a calendar-like grid
    heatmap_data = df.pivot(index="week", columns="dow", values="count")
    # Reorder columns Sunday-Monday
    all_days = [6, 0, 1, 2, 3, 4, 5]  # Sunday first
    heatmap_data = heatmap_data.reindex(columns=all_days)

    # Build custom heatmap 
    fig = go.Figure(data=go.Heatmap(
        z=heatmap_data.values,
        x=["Sun","Mon","Tue","Wed","Thu","Fri","Sat"],
        y=heatmap_data.index,
        colorscale="Viridis",        # color-blind-friendly, may need to create our own. 
        hoverongaps=False,
        text=heatmap_data.values,    # show Survey count on hover
        hovertemplate="Surveys Completed: %{z}<extra></extra>"
    ))

    # # This adds survey numbers to each cell, but think we only display this by color intensity. 
    # for week_idx, week in enumerate(heatmap_data.index):
    #     for day_idx, val in enumerate(heatmap_data.loc[week]):
    #         if not np.isnan(val):
    #             fig.add_annotation(
    #                 x=["Sun","Mon","Tue","Wed","Thu","Fri","Sat"][day_idx],
    #                 y=week,
    #                 text=str(int(val)),
    #                 showarrow=False,
    #                 font=dict(color="white", size=10)
    #             )

    # Highlight start/end dates (Not fully working properly)
    if not df.empty:
        start_date = df["timestamp"].min()
        end_date = df["timestamp"].max()
        # loop twice, once for start and once for end with the appropriate colors
        for mark, color in [(start_date, "black"), (end_date, "gold")]:
            week = pd.to_datetime(mark).isocalendar().week
            dow = pd.to_datetime(mark).weekday()  # 0=Mon, 6=Sun
            dow_map = {6:0, 0:1, 1:2, 2:3, 3:4, 4:5, 5:6}
            x_idx = dow_map[dow]
            fig.add_shape(
                type="rect",
                xref="x", yref="y",
                x0=x_idx-0.5, x1=x_idx+0.5,
                y0=week-0.5, y1=week+0.5,
                line=dict(color= color, width=3)
            )

    # Layout adjustments (square cells, Sunday-Saturday on top)
    fig.update_layout(
        title="Survey Activity Calendar Heatmap",
        xaxis=dict(side="top"),
        yaxis=dict(autorange="reversed", title="Week #"),
        height=600
    )

    calendar_plot = dcc.Graph(figure=fig)
    return (html.Div([table, calendar_plot]), status, "")