// webpack.config.js
const path = require('path');
const webpack = require('webpack');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const CopyWebpackPlugin = require('copy-webpack-plugin');

// Define your pages here
const external_pages = [
  'about',
  'index',
  'faq',
  'cookiepolicy',
  'termsofservice',
];

const login_pages = [
  'login'
];

const dashboard_pages = [
  'dashboard'
]

const config = {
  entry: {
    login: './src/js/login.js',
    dashboard: './src/js/dashboard.js',
  },
  output: {
    path: path.resolve(__dirname, '../webapp'),
    filename: '[name].bundle.js'
  },
  module: {
    rules: [
      {
        test: /\.js$/,
        exclude: /node_modules/,
        loader: 'babel-loader'
      },
      {
        test: /\.pug$/,
        use: ['html-loader?attributes=false', 'pug-html-loader']
      },
    ]
  },
  plugins: [
    new webpack.ProvidePlugin({
      $: "jquery",
      jQuery: "jquery"
    }),
    new CopyWebpackPlugin([
      {from: 'src/vendor', to: 'vendor'},
      {from: 'src/img', to: 'img'},
      {from: 'src/css', to: 'css'},
      {from: 'src/assets', to: 'assets'},
    ]),
    // Convert all pages from pug to html here
    // List of pages that we have to deal with
    [external_pages, login_pages, dashboard_pages]
      .flat() // merge them into one array
      .map((n) => new HtmlWebpackPlugin({ 
        // Substitute paths
        filename: `${n}.html`,
        template: `src/${n}.pug`,
        inject: false
      }))
  ].flat()
};
module.exports = config;
