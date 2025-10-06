// setupProxy.js
const { createProxyMiddleware } = require('http-proxy-middleware');

module.exports = function(app) {
  app.use(
    '/dash/lemurs',
    createProxyMiddleware({
      target: 'http://127.0.0.1:5433',
      changeOrigin: true,
      xfwd: true, // adds X-Forwarded-* headers for ProxyFix
      // keep the path as-is so Dash sees /dash/lemurs/...
      pathRewrite: { }, 
      // Optional but helpful if you ever front this behind another prefix:
      onProxyReq: (proxyReq, req) => {
        proxyReq.setHeader('X-Forwarded-Prefix', '/dash/lemurs');
      },
    })
  );
};
