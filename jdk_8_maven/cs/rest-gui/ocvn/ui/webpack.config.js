var config = require('./webpack.dev.config.js');
var webpack = require('webpack');
config.entry = "./index.jsx";
config.output.filename = "index.min.js";
delete config.output.publicPath;
delete config.devtool;
config.plugins = config.plugins.filter(function(plugin){
  return !(plugin instanceof webpack.HotModuleReplacementPlugin);
}).concat([
  new webpack.DefinePlugin({
    "process.env": {
      NODE_ENV: JSON.stringify("production")
    }
  }),
  new webpack.optimize.DedupePlugin(),
  new webpack.optimize.UglifyJsPlugin({
    sourceMap: false
  })
]);

module.exports = config;
