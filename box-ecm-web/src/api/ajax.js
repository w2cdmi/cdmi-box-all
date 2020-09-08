import Vue from "vue";
import axios from "axios";
import VueAxios from "vue-axios";
import settings from "@/config/settings.js";
Vue.use(VueAxios, axios);

let isv = false;
let isshow = new Map();
let that = "";
function statusCode (response) {
  if (
    Object.prototype.toString.call(response) === "[object Object]" &&
    !response.hasOwnProperty("data")
  ) {
    return "";
  }
  let code = response.data.code;

  if (code === "SameParentConflict") {
    return "相同目录不能进行操作";
  } else if (code === "NoSuchItem") {
    return "文件或文件夹不存在";
  } else if (code === "NoSuchFile") {
    return "文件不存在";
  } else if (code === "NoSuchFolder") {
    return "文件夹不存在";
  } else if (code === "NoSuchParent") {
    return "父目录不存在";
  } else if (code === "NoSuchSource") {
    return "源文件或文件夹不存在";
  } else if (code === "NoSuchDest") {
    return "目标文件或文件夹不存在";
  } else if (code === "Forbidden") {
    return "您没有权限进行该操作";
  } else if (code === "InvalidParameter") {
    return "请求参数错误";
  } else if (code === "LinkExistedConflict") {
    return "外链已存在";
  } else if (code === "LinkExpired") {
    return "外链已过期";
  } else if (code === "LinkNotEffective") {
    return "外链未生效";
  } else if (code === "NoSuchLink") {
    return "外链不存在";
  } else if (code === "NoSuchUser") {
    return "用户不存在";
  } else if (code === "SubFolderConflict") {
    return "不能移动子目录下";
  } else if (code === "SameNodeConflict") {
    // 复制或移动时，目标节点和源节点相同冲突
    return "目标文件夹与源文件夹相同";
  } else if (code === "SameParentConflict") {
    // 复制或移动时，目标节点是源节点的父文件夹
    return "目标文件夹已在该目录下";
  } else if (code === "UserLocked") {
    return "用户被锁定";
  } else if (code === "ExistMemberConflict") {
    return "成员已存在";
  } else if (code === "ExistTeamspaceConflict") {
    return "协作空间已存在";
  } else if (code === "ExceedMaxLinkNum") {
    return "外链数超过最大限制";
  } else if (code === "InvalidFileType") {
    return "不支持的文件类型";
  } else if (code === "ExceedQuota") {
    return "空间容量不足";
  } else if (code === "ExceedUserAvailableSpace") {
    return "空间容量不足";
  } else if (code === "UploadSizeTooLarge") {
    return "上传文件大小超过限制";
  } else if (code === "UploadSizeTooLarge") {
    return "上传文件大小超过限制";
  } else if (code === "ExsitShortcut") {
    // 此消息可以不显示
    return "快捷目录已经存在";
  } else {
    // console.log("response", response);
    // if (
    //   Object.prototype.toString.call(response) === "[object Object]" &&
    //   !response.hasOwnProperty("status")
    // ) {
    if (response.status === 400 && response.data === "Forbidden") {
      return "您没有权限进行该操作";
    } else if (response.status === 403) {
      return "您没有权限进行该操作";
    } else if (response.status === 404) {
      return "文件或文件夹不存在";
    } else if (response.status >= 500) {
      return "服务器错误";
    } else {
      return "操作失败";
    }
    // } else {
    //   return "服务器错误";
    // }
  }
}

/**
 * 封装axios的通用请求
 * @param  {string}   type      get或post
 * @param  {string}   url       请求的接口URL
 * @param  {object}   data      传的参数，没有则传空对象
 * @param  {Function} fn        回调函数
 * @param  {string}   token     token
 */

export default function (type, url, token = "", data, fn, errFn) {
  that = this;
  let config = {
    url: `${settings.gbs.servermode.host}${url}`,
    headers: {
      "Content-Type": "application/json",
      Authorization:
        (this.$store && this.$store.state.user.userInfo.token) || ""
    },
    method: type
  };
  if (token) config.headers.Authorization = token;
  if (type === "get") {
    config = Object.assign({}, config, {
      params: Object.assign({}, data, {})
    });
  } else {
    if (data instanceof Array === true) {
      config = Object.assign({}, config, {
        data: data
      });
    } else {
      config = Object.assign({}, config, {
        data: Object.assign({}, data, {})
      });
    }
  }
  axios(config)
    .then(res => {
      if (res.status === 200) {
        fn && fn(res.data);
      } else {
        if (!res) {
          that.$confirm("服务器错误", "提示", {
            confirmButtonText: "重新登录",
            type: "error",
            center: true
          });
        } else {
          errFn && errFn.call(this);
        }
      }
    })
    .catch(err => {
      var message = statusCode(err.response);
      this.$message({
        showClose: true,
        message: message,
        type: "error"
      });
      if (err) {
        errFn && errFn.call(this, err);
      }
    });
  axios.interceptors.response.use(
    function (response) {
      return response;
    },
    function (error) {
      if (error.response.status === 401) {
        if (!isv) {
          isv = true;
          isshow[error.config.url] = true;
          that
            .$confirm("登录失效", "提示", {
              confirmButtonText: "重新登录",
              type: "error",
              center: true
            })
            .then(() => {
              that.$router.push("/");
            })
            .catch(() => {
              that.$router.push("/");
            });
        }
      }
      if (error.response.status >= 500) {
      }
      return Promise.reject(error);
    }
  );
}
