/* eslint-env node */
'use strict'

const webpack = require('webpack')
const webpackConfigClient = require('./webpack.config.js')
const WebpackDevServer = require('webpack-dev-server')

const port = parseInt(process.env.PORT || '8000')

webpackConfigClient.entry.unshift(`webpack-dev-server/client?http://localhost:${port}/`, 'webpack/hot/dev-server')
webpackConfigClient.plugins.unshift(new webpack.HotModuleReplacementPlugin())

const compiler = webpack(webpackConfigClient)
const server = new WebpackDevServer(compiler, {
  proxy: {
    '/api/*': {
      target: 'http://localhost:8080',
      changeOrigin: true,
      secure: true
    }
  },
  hot: true,
  inline: true,
  historyApiFallback: true,
  // reduce the console noise
  stats: {
    assets: false,
    colors: true,
    version: false,
    modules: false,
    hash: false,
    timings: false,
    chunks: false,
    chunkModules: false,
    reasons: false,
    cached: true,
    chunkOrigins: true,
    children: false
  }
})

const proxy = require('http-proxy').createProxyServer()
proxy.on('error', () => {
  // ignore errors (like websocket connection reset) and just continue
})
server.listeningApp.on('upgrade', (req, socket) => {
  proxy.ws(req, socket, {
    target: 'ws://localhost:8080',
    ws: true,
    changeOrigin: true
  })
})

server.listen(port, (err) => {
  if (err) return console.err(err) // eslint-disable-line no-console
  console.log(`Now listening on http://localhost:${port}`) // eslint-disable-line no-console
})
