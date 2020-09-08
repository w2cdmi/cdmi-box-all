import * as types from "./mutations_types";
import Api from "../../api/api.js";
import { savaToLocal, loadFromlLocal } from "../../common/js/store";
const env = process.env;
const HOST = env.serverConfig.host;

export default {
  exitLogin: ({ dispatch }, { self, obj = {} }) => {
    return new Promise((resolve, reject) => {
      Api.exitLogin.call(self, {
        data: "",
        success (data) {
          resolve("exit success");
          dispatch("removeUserUnfo");
        },
        error (err) {
          reject(err);
          dispatch("removeUserUnfo");
        }
      });
    });
  },
  loginUser: ({ dispatch }, { self, obj = {} }) => {
    return new Promise((resolve, reject) => {
      Api.userLogin.call(self, {
        data: {
          authCode: obj.authCode,
          appId: obj.appId
        },
        success (data) {
          dispatch("loginSuccess", {
            self: self,
            data: data
          });
          resolve(data);
        },
        error (err) {
          reject(err);
          dispatch("loginFail", self);
        }
      });
    });
  },
  loginBytoken: ({ dispatch }, { self, obj = {} }) => {
    return new Promise((resolve, reject) => {
      Api.userBytoken.call(self, {
        token: obj.token,
        success (data) {
          data.token = obj.token;
          dispatch("loginSuccess", {
            self: self,
            data: data
          });
          resolve(data);
        },
        error (err) {
          reject(err);
          dispatch("loginFail", self);
        }
      });
    });
  },
  loginSuccess ({ dispatch }, options) {
    options.data.timeout = +new Date() + options.data.timeout * 1000;
    savaToLocal("user", "userinfo", options.data);
    dispatch("updateUser", options.data);
    options.self.$message({ type: "success", message: "登录成功!" });
  },
  loginFail ({ dispatch }, self) {
    dispatch("removeUserUnfo");
    self
      .$confirm("登录失败！", "提示", {
        confirmButtonText: "重新登录",
        type: "error",
        center: true
      })
      .then(() => {
        dispatch("goToWxLogin");
      })
      .catch(() => {
        self.$message({ type: "info", message: "取消重新登录" });
      });
  },
  localToken ({ dispatch }) {
    let data = loadFromlLocal("user", "userinfo");
    dispatch("updateUser", data);
  },
  verifyToken: ({ dispatch }, self) => {},
  goToWxLogin () {
    //   env.serverConfig.host +
    // http%3A%2F%2Fwww.jmapi.cn
    let url =
      "https://open.work.weixin.qq.com/wwopen/sso/3rd_qrConnect?appid=" +
      env.serverConfig.wwAppId +
      "&redirect_uri=" +
      HOST +
      env.serverConfig.path +
      "%2F%23%2Flogin%3Fqr%3Dww%26type%3Dperson&state=0&usertype=member";
    window.location.href = url;
  },
  updateUser: ({ commit }, data) => {
    commit(types.SET_USER_INFO, data);
  },
  removeUserUnfo: ({ commit }) => {
    commit(types.REMOVE_USER_INFO);
  }
};
