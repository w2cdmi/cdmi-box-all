const env = process.env;
env.NODE_ENV === "development"
  ? (env.serverConfig.host = "http://114.115.212.88")
  : (env.serverConfig.host = "");
//  http://114.115.212.88
// http://122.112.251.43
const settings = {
  // 全局设置
  gbs: {
    servermode: env.serverConfig,
    // host:
    //   env.NODE_ENV === "development"
    //     ? "http://114.115.212.88/"
    //     : "https://www.jmapi.cn/",
    // 接口根地址。本地代理到slsadmin.api.sls.com,线上使用的是Nginx代理
    db_prefix: "sls_admin_" // 本地存储的key
  },

  // 回调
  cbs: {
    /**
     * ajax请求成功，返回的状态码不是200时调用
     * @param  {object} err 返回的对象，包含错误码和错误信息
     */
    statusError (err) {
      if (err.status !== 404) {
        this.$message({
          showClose: true,
          message: "返回错误：" + err,
          type: "error"
        });
      }
    },

    /**
     * ajax请求网络出错时调用
     */
    requestError (err) {
      // if (err.response.status === 403) {
      //   savaToLocal("user", "token", false);
      //   this.$router.push({
      //     path: "/login"
      //   });
      //   return;
      // }
      this.$message({
        showClose: true,
        // message: '请求错误：' + err.response.status + ',' + err.response.statusText,
        message:
          "请求错误：" + err.response.status + "," + err.response.statusText,
        type: "error"
      });
    }
  }
};
export default settings;
