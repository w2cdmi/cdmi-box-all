"use strict";
const config = require("../config");
let serverConfig = {};
// process.env.NODE_ENV = "'production'";
let servermode = process.env.servermode;
const publicPath = () => {
  if (process.env.NODE_ENV === "production") {
    if (process.env.servermode) {
      return config.build.assetsPublicPathServer;
    } else {
      console.log("publicPath", config.build.assetsPublicPath);
      return config.build.assetsPublicPath;
    }
  } else {
    return config.dev.assetsPublicPath;
  }
};
if (servermode === "filepro") {
  console.log("build in filepro");
  serverConfig = {
    suiteId: "ww2e8f675c0308dbb3",
    wwAppId: "wwff314b9b8085f16c",
    appId: "FilePro",
    host: "https://www.filepro.cn/"
  };
} else if (process.env.servermode === "jmapi") {
  console.log("build in jmapi");
  serverConfig = {
    suiteId: "tje32d93de35487681",
    wwAppId: "wwc7342fa63c523b9a",
    appId: "StorBox",
    host: "http://www.jmapi.cn/"
  };
} else if (process.env.servermode === "storbox") {
  console.log("build in storbox");
  serverConfig = {
    suiteId: "tj3d3cd3b4e4ffcde0",
    wwAppId: "wwba09b5d7931f8d7e",
    appId: "StorBox",
    host: "https://www.storbox.cn/"
  };
} else {
  console.log("build in local");
  serverConfig = {
    suiteId: "tje32d93de35487681",
    appId: "wwc7342fa63c523b9a",
    host: "http://114.115.212.88/"
  };
}
serverConfig.path = publicPath();
module.exports = {
  NODE_ENV: "\"production\"",
  serverConfig: JSON.stringify(serverConfig)
};
