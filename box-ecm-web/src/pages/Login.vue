<template>
  <div class="main">
    <inputBox v-if="showInputBox" />
  </div>
</template>
<script>
import { appid } from "../common/Global";
import { mapGetters, mapActions } from "vuex";
import Api from "../api/api";
import Set from "../config/settings.js";
const appId = Set.gbs.servermode.appId;
export default {
  data() {
    return {
      enterpriseName: "",
      usrName: ""
    };
  },
  computed: {
    showInputBox: {
      get() {
        return process.env.NODE_ENV == "development";
      }
    }
  },
  methods: {},
  components: {
    inputBox: {
      template: `
      <div class="code">
            <input  type="text" placeholder="请输入企业名称" value="聚数科技">
            <div class="weui-cell">
              <div class="weui-cell__hd"><label class="weui-label">用户名</label></div>
              <div class="weui-cell__bd">
              <input class="weui-input" type="text" name="username" id="username" placeholder="请输入用户名" value="DuPan">
            </div>
            </div>
              <div class="weui-cell">
              <div class="weui-cell__hd"><label class="weui-label">密码</label></div>
              <div class="weui-cell__bd">
              <input class="weui-input" type="password" id="password" name="password" placeholder="请输入密码" value="pas@123a">
            </div>
            </div>
            <button @click='login'>login</button>
        </div>
      `,
      methods: {
        login() {
          const that = this;
          Api.userLogin.call(this, {
            data: {
              appId: "StorBox",
              loginName: "LiuYongHua",
              password: "LiuYongHua@123",
              enterpriseName: "聚数科技"
            },
            success(data) {
              that.$router.push("/DepartmentAndStaffManagement");
              that.$store.dispatch("loginSuccess", {
                self: that,
                data: data
              });
              that.$route;
            },
            error(err) {
              that.$store.dispatch("loginFail", that);
            }
          });
        }
      }
    }
  },
  created() {
    let urltoken = this.$route.query.token;
    if (urltoken) {
      this.$store
        .dispatch("loginBytoken", {
          self: this,
          obj: { token: urltoken }
        })
        .then(res => {
          console.log("res", res);
          if (res.isAdmin === 1) {
            this.$router.push("/DepartmentAndStaffManagement");
          } else {
            this.$message.error({
              type: "error",
              message: "你不是管理员，无权登录",
              duration: 0,
              showClose: true
            });
          }
        })
        .catch(err => {
          console.log("err", err);
        });
    } else {
      // urltoken = 1;
      if (process.env.NODE_ENV !== "development") {
        let auth_code = this.$route.query.auth_code;
        if (auth_code) {
          this.$store
            .dispatch("loginUser", {
              self: this,
              obj: {
                authCode: this.$route.query.auth_code,
                appId: appId
              }
            })
            .then(res => {
              if (res.isAdmin === 1) {
                this.$router.push("/DepartmentAndStaffManagement");
              } else {
                this.$message.error({
                  type: "error",
                  message: "你不是管理员，无权登录",
                  duration: 0,
                  showClose: true
                });
              }
            })
            .catch(err => {});
        } else {
          let that = this;
          setTimeout(function(params) {
            that.$store.dispatch("goToWxLogin");
          }, 1000);
        }
      }
    }
  },
  methods: {}
};
</script>
<style lang="less" scoped>
.main {
  width: 100%;
  height: 100%;
  background-color: #fff;
  display: block;
  .code {
    vertical-align: middle;
    border: 1px solid #ccc;
    transform: translateY(50%);
    margin: 0 auto;
    width: 400px;
    height: 400px;
  }
}
</style>
