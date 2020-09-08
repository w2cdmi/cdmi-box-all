<template>
  <header>
    <div class="logo"><img src="../assets/logo.png" alt=""></div>
    <div class="systemTitle">
      <span>后台管理系统ECM</span>
    </div>
    <div class="people">
      <div class="imgs">
        <img :src="host + '/enterprise/userimage/getUserImage/'+cloudUserId" alt="nickname" srcset="">
      </div>
      <div class="peoplemasg">
        <el-dropdown trigger="click" @command="exit">
          <span class="menu el-dropdown-link">{{userName}}
            <i class="el-icon-arrow-down el-icon--right"></i>
          </span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="1">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </div>
  </header>
</template>
<script>
import { mapGetters } from "vuex";
import set from "@/config/settings.js";
export default {
  data() {
    return {
      host: set.gbs.servermode.host,
      aa: this.$store.getters.getUserinfo,
      cloudUserId: this.$store.getters.getUserinfo.cloudUserId
    };
  },
  computed: {
    userName: {
      get() {
        let userName = this.$store.getters.getUserinfo;
        return userName.alias || userName.name || "userName";
      }
    }
  },
  methods: {
    exit(command) {
      if (command === "1") {
        this.$store.dispatch("exitLogin", { self: this }).then(res => {
          this.$router.push({ path: "/", query: { exit: +new Date() } });
          this.$message({
            message: "退出成功",
            type: "success"
          });
        });
      }
    }
  }
};
</script>

<style lang="less" scoped>
header {
  font-size: 18px;
  width: 100%;
  height: 60px;
  line-height: 60px;
  background: #fff;
  overflow: hidden;
  position: absolute;
  z-index: 100;
  top: 0;
  left: 0;
  .logo {
    float: left;
    width: 210px;
    img {
      margin: 15px auto;
      display: block;
    }
  }
  .systemTitle {
    height: 60px;
    float: left;
    margin-left: 15px;
    font-size: 20px;
    color: #040404;
    span {
      padding-left: 20px;
    }
  }
  .people {
    float: right;
    margin-right: 50px;
    .imgs {
      border-radius: 50%;
      width: 30px;
      height: 30px;
      overflow: hidden;
      float: left;
      margin: 15px auto;
      img {
        width: 100%;
        height: 100%;
        display: block;
        line-height: 60px;
      }
    }
    .peoplemasg {
      float: right;
      margin-left: 14px;
      cursor: pointer;
      outline: none;
    }
  }
}
.el-popper {
  margin: 0;
  width: 200px;
  top: 55px !important;
  .el-dropdown-menu__item:hover {
    background: #f5f5f5;
    color: #000;
  }
}
</style>
