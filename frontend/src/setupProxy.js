const { createProxyMiddleware } = require('http-proxy-middleware');

module.exports = function (app) {
  app.use(
    '/api',
    createProxyMiddleware({
      target: 'http://localhost:5000',
      headers: {
        "X-AUTH-NAME": "Spring",
        "X-AUTH-TOKEN": "3oo6h3a2n1h6q733ca"
      },
      changeOrigin: true,
    })
  );
};