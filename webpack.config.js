/* eslint-env node */
'use strict'

const autoprefixer = require('autoprefixer')
const HtmlWebpackPlugin = require('html-webpack-plugin')
const path = require('path')
const webpack = require('webpack')

module.exports = {
  context: path.resolve('./src/web'),
  target: 'web',
  entry: ['./index.js'],
  output: {
    path: path.resolve('./target/web'),
    filename: 'assets/app-[hash].js',
    chunkFilename: 'assets/app-[id]-[hash].js',
    publicPath: '/'
  },
  module: {
    loaders: [
      {
        test: /\.js$/,
        exclude: /node_modules/,
        loaders: [
          'babel'
        ]
      },
      {
        test: /\.scss$/,
        loaders: [
          'style',
          'css?modules&localIdentName=[name]__[local]__[hash:base64:5]&camelCase',
          'postcss',
          'sass'
        ]
      }
    ]
  },
  plugins: [
    new webpack.DefinePlugin({
      'process.env.NODE_ENV': JSON.stringify(process.env.NODE_ENV)
    }),
    new HtmlWebpackPlugin({
      template: './index.html.ejs',
      inject: false,
      minify: process.env.NODE_ENV === 'production' ? {
        collapseWhitespace: true,
        minifyCSS: true
      } : false
    }),
    process.env.NODE_ENV === 'production' ? new webpack.optimize.UglifyJsPlugin({
      compress: true,
      output: {
        semicolons: false
      },
      compressor: {
        warnings: false
      }
    }) : null
  ].filter((plugin) => Boolean(plugin)),
  resolve: {
    extensions: ['', '.js', '.jsx']
  },
  postcss: [autoprefixer],
  devtool: process.env.NODE_ENV !== 'production' ? '#inline-sourcemaps' : undefined
}
