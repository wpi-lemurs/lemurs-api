// src/pages/LemursDashboardIframe.jsx
import React from "react";

export default function LemursDashboardIframe() {
  return (
    <div style={{ height: "100vh" }}>
      <iframe
        title="LEMURS RADAR"
        src="/dash/lemurs/"
        style={{ width: "100%", height: "100%", border: 0 }}
        loading="lazy"
        allowFullScreen
      />
    </div>
  );
}
