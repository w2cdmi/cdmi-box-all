const path = require("path");
const webpack = require("webpack");
const AssetsPlugin = require("assets-webpack-plugin");
const UglifyJsPlugin = require("uglifyjs-webpack-plugin");
const config = require("../config");
function resolve (dir) {
  return path.join(__dirname, "..", dir);
}

module.exports = {
  context: path.resolve(__dirname, "../"),
  entry: {
    vendors: ["babel-polyfill","vue", "axios", "vue-router", "element-ui","vuex"]
  },
  output: {
    path: path.join(__dirname, "../dll"), // 动态链接库输出的文件名称
    filename: "static/js/[name].dll.[hash:8].js", // 动态链接库输出路径
    libraryTarget: "var", // 链接库输出方式 默认'var'形式赋给变量
    library: "_dll_[name]_[hash:8]" // 全局变量名称 导出库将被以var的形式赋给这个全局变量 通过这个变量获取到里面模块
  },
  // manifest是描述文件
  plugins: [
    new webpack.DllPlugin({
      path: path.join(__dirname, "../dll", "[name].manifest.json"),
      name: "_dll_[name]_[hash:8]",
      context: process.cwd()
    }),
    new AssetsPlugin({
      filename: "vendor.config.json",
      path: path.resolve(__dirname, "../dll")
    }),
    new UglifyJsPlugin({
      uglifyOptions: {
        compress: {
          warnings: false,
          drop_debugger: true,
          drop_console: true
        }
      },
      sourceMap: config.build.productionSourceMap,
      parallel: true
    }),
  ]
};
