import React from "react";

// If you want, set REACT_APP_DASH_URL in .env; otherwise defaults to local Dash.
const DASH_URL = process.env.REACT_APP_DASH_URL || "http://127.0.0.1:5433/";

export default function LemursDashboardIframe() {
  return (
    <div style={{ width: "100%", height: "100vh" }}>
      <iframe
        title="LEMURS Dash"
        src={DASH_URL}                   // <-- Direct to Dash; no CRA path, no proxy
        style={{ width: "100%", height: "100%", border: 0 }}
        loading="lazy"
        allowFullScreen
        // keep cross-origin iframe simple; no cookies shared/needed for Dash
        referrerPolicy="no-referrer"
      />
    </div>
  );
}
