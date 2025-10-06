// src/pages/LemursDashboardIframe.jsx
import React, { useContext } from "react";
import { TokenContext } from "../components/token/TokenContext";

// Configure via .env if desired
const DASH_URL = process.env.REACT_APP_DASH_URL || "http://127.0.0.1:5433/";

export default function LemursDashboardIframe() {
  const { token } = useContext(TokenContext);

  return (
    <div className="App">
      <h2 className="card-title">LEMURS Dashboard</h2>
      <br />
      {(token === "") ? (
        <h5 className="card-title">Please sign in to view the LEMURS dashboard.</h5>
      ) : (
        <div style={{ width: "100%", height: "calc(100vh - 120px)" }}>
          <iframe
            title="LEMURS Dash"
            src={DASH_URL}
            style={{ width: "100%", height: "100%", border: 0 }}
            loading="lazy"
            allowFullScreen
            referrerPolicy="no-referrer"
          />
        </div>
      )}
    </div>
  );
}
