const { createProxyMiddleware } = require("http-proxy-middleware");

console.log("setupProxy.js is being loaded...");  // add this line

module.exports = function (app) {
  console.log("setupProxy.js is configuring proxy...");  // add this too
  app.use(
    "/dash/lemurs",
    createProxyMiddleware({
      target: "http://127.0.0.1:5433",
      changeOrigin: true,
      ws: true,
      logLevel: "debug",
    })
  );
};
