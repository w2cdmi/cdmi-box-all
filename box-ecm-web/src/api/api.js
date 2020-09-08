/**
 * 导出所有模块需要用到接口
 * 一级属性：模块名
 * 一级属性中的方法：当前模块需要用的接口
 * @type {Object}
 */
import ajax from "./ajax";
let api = {
  userLogin (params = {}) {
    if (process.env.NODE_ENV === "development") {
      ajax.call(
        this,
        "post",
        "/ecm/api/v2/token",
        "",
        params.data,
        params.success,
        params.error
      );
    } else {
      ajax.call(
        this,
        "post",
        "/ecm/api/v2/token/wxwork",
        "",
        params.data,
        params.success,
        params.error
      );
    }
  },
  userBytoken (params = {}) {
    ajax.call(
      this,
      "GET",
      "/ecm/api/v2/users/me",
      params.token,
      params.data,
      params.success,
      params.error
    );
  },
  exitLogin (params = {}) {
    ajax.call(
      this,
      "delete",
      "/ecm/api/v2/token",
      params.token,
      params.data,
      params.success,
      params.error
    );
  }
};
export default api;
