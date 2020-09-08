<template>
  <el-row ref="CONTAINER" class="container">
    <Head ref="Head" :id="'Head-'+this" />
    <el-col :span="24" class="main">
      <aside class="aside-menu">
        <!-- <el-menu :default-active="$route.path" class="el-menu-vertical-demo showafter" router>
          <template v-for="(item,index) in $router.options.routes" v-if="!item.hidden">
            <el-submenu :index="index+''" v-if="!item.leaf" :key="index">
              <template slot="title">
                <i :class="item.iconCls"></i>
                {{item.name}}
              </template>
              <el-menu-item v-for="child in item.children" :index="child.path" :key="child.path" v-if="!child.hidden">
                <span ref="Name">{{child.name}}</span>
              </el-menu-item>
            </el-submenu>
            <el-menu-item v-if="item.leaf&&item.children.length>0" :index="item.children[0].path" :key="index">
              <i :class="item.iconCls"></i>{{item.children[0].name}}
            </el-menu-item>
          </template>
        </el-menu> -->
        <div v-for="(item,index) in $router.options.routes" v-if="!item.hidden" :key="index">
          <div>
            <div class="menu-title">{{item.name}}</div>
            <div class="menu-item" :class="idx===isActive?'menu-item-active':''" v-for="(child,idx) in item.children" :index="child.path" :key="child.path" v-if="!child.hidden" @click="routes(child.path)">
              <span>{{child.name}}</span>
            </div>
          </div>
        </div>
      </aside>
      <section class="content-container">
        <div class="grid-content bg-purple-light">
          <!-- <keep-alive> -->
          <router-view></router-view>
          <!-- </keep-alive> -->
        </div>
      </section>
    </el-col>
  </el-row>
</template>

<script>
import {
  savaToLocal,
  loadFromlLocal,
  savaToSession,
  loadFromlSession
} from "@/common/js/store";
import { mapGetters, mapActions } from "vuex";
import utils from "@/common/js/util.js";
import Head from "@/components/Head.vue";
import TREE from "@/components/Tree.vue";
import item from "@/components/Mtree.vue";
import Menu from "@/components/Menu.vue";
function isactive(ary, path) {
  for (let i = 0, len = ary.length; i < len; i++) {
    if (!ary[i].hidden) {
      let Clen = 0;
      if (ary[i].children) {
        Clen = ary[i].children.length || 0;
      }
      for (let j = 0; j < Clen; j++) {
        let item = ary[i].children[j];
        if (path.indexOf(item.path) > -1) {
          return j;
          break;
        }
      }
    }
  }
  return false;
}
export default {
  data() {
    return {
      menuData: {
        title: "账号管理",
        id: "1",
        children: [
          {
            title: "部门与员工管理",
            id: "2"
          },
          {
            title: "离职管理",
            id: "3"
          },
          {
            title: "应用权限管理",
            id: "4"
          },
          {
            title: "容量配置",
            id: "5"
          }
        ]
      },
      uniqueOpened: true,
      data: {
        title: "111",
        children: [
          {
            title: "111 - 1"
          },
          {
            title: "111 - 2",
            children: [{ title: "111 - 2 - 1" }]
          }
        ]
      },
      curpath: this.$route.path,
      isActive: null
    };
  },
  computed: {},
  watch: {
    $route(n, o) {
      let routes = this.$router.options.routes;
      this.isActive = isactive.call(this, routes, n.path);
    }
  },
  created() {
    let routes = this.$router.options.routes;
    this.isActive = isactive.call(this, routes, this.$route.path);
    this.$store.dispatch("localToken");
    this.$store.dispatch("verifyToken");
    this.$store
      .dispatch("getDepart", {
        self: this,
        obj: {}
      })
      .then(res => {
        // console.log("HOME", utils.parseTree(res));
      })
      .catch(err => {
        console.log(err);
      });
    // setTimeout(() => {
    //   this.$nextTick(() => {
    //     var height = this.$refs.CONTAINER.$el.offsetHeight;
    //     // console.log(height);
    //     let Head = this.$refs.Head;
    //     // console.log(Head);
    //     Head.$el.setAttribute("id", "H-" + Head._uid);
    //   }, 2000);
    // });
  },
  methods() {
    setTimeout(() => {
      // this.$nextTick(() => {
      var height = this.$refs.CONTAINER.$el.offsetHeight;
      console.log(height);
    }, 200);
    // });
  },
  components: {
    Head,
    TREE,
    item,
    Menu
  },
  methods: {
    routes(path) {
      this.$router.push(path);
      this.curpath = this.$route.path;
    }
  },
  mounted() {}
};
</script>

<style lang="less">
.container {
  position: absolute !important;
  top: 0;
  bottom: 0px;
  width: 100%;
  font-family: 微软雅黑;
  min-width: 1400px;
  .header {
    background: #1f2d3d;
    color: #c0ccda;
    .userinfo {
      text-align: right;
      padding-right: 35px;
      .userinfo-inner {
        color: #c0ccda;
        cursor: pointer;
        img {
          width: 40px;
          height: 40px;
          border-radius: 20px;
          margin: 10px 0px 10px 10px;
          float: right;
        }
      }
    }
  }
  .main {
    display: -webkit-box;
    display: -ms-flexbox;
    position: absolute;
    top: 60px;
    bottom: 0;
    left: 0;
    right: 0;
    height: 100%;
    .aside-menu {
      width: 200px;
      height: 100%;
      border: 0;
      background-color: #fff;
      // $textindent=40px;
      .menu-title {
        float: left;
        text-indent: 40px;
        width: 100%;
        height: 50px;
        line-height: 50px;
        color: #999999;
        font-size: 12px;
        cursor: pointer;
      }
      .menu-item {
        float: left;
        box-sizing: border-box;
        height: 50px;
        line-height: 50px;
        cursor: pointer;
        width: 100%;
        > span {
          text-indent: 40px;
          width: 100%;
          display: inline-block;
          height: 22px;
          line-height: 22px;
          box-sizing: border-box;
        }
        &:hover {
          span {
            border-right: 2px solid #ea5036;
            color: #ea5036;
          }
        }
      }
      .menu-item-active {
        color: #ea5036;
        span {
          border-right: 2px solid #ea5036;
        }
      }
    }
  }
}
.content-container {
  /* width: 100%; */
  padding: 40px;
  position: absolute;
  left: 200px;
  right: 0;
  top: 0;
  bottom: 100px;
  overflow: hidden;
  box-sizing: border-box;
  .grid-content {
    height: 100%;
    background: #fff;
    padding: 20px;
    /* overflow-y: auto; */
    position: relative;
    left: 0;
    right: 0;
    top: 0;
    bottom: 0;
    .panal {
      position: absolute;
      left: 0;
      right: 0;
      bottom: 0;
      top: 0;
      .table-block {
        padding: 17px 17px 0;
        position: absolute;
        left: 0;
        right: 0;
        bottom: 50px;
        top: 0;
        overflow: auto;
      }
    }
  }
  .content-wrapper {
    box-sizing: border-box;
    height: 100%;
  }
}
.el-button {
  background: #fafafa;
  border: 1px solid #eaeaea;
  color: #333333;
  &:focus,
  &:hover {
    background: #f5f5f5;
    color: #333333;
    border: 1px solid #eaeaea;
  }
}
.autho-main {
  height: 100%;
}
.autho-header {
  height: 35px;
}
.autho-table {
  width: 100%;
  position: absolute;
  left: 0;
  right: 0;
  bottom: 50px;
  top: 50px;
  overflow: auto;
}
.paginationblock {
  position: fixed;
  bottom: 60px;
  left: 45%;
}
</style>