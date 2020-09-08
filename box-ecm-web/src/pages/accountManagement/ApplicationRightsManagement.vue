<template>
  <div class="autho-main">
    <el-row style="width:100%;height:100%;">
      <el-col class="el-tabs" :span="24">
        <div class="autho-tab">
          <div class="autho-header">
            <div class="left-title">应用权限管理</div>
            <div class="autho-tabs">
              <span @click="changetabs('first')" id="tab-first" :class="isActive==='first'?'is-active':''" class="el-tabs__item">知识管理员</span>
              <span @click="changetabs('second')" id="tab-second" :class="isActive==='second'?'is-active':''" class="el-tabs__item">系统管理员</span>
            </div>
            <div class="right-title">
              <!-- <el-button @click="edit">编辑</el-button> -->
              <el-button>新增</el-button>
              <el-button>删除</el-button>
            </div>
          </div>
          <div class="autho-table">
            <transition name="fade" mode="out-in">
              <Table class="tab" v-show="isActive==='first'" :titlename="''" :title="true" :people="false" :showbtn="false" />
            </transition>
            <transition name="fade" mode="out-in">
              <Table class="tab" :checkbox="true" v-show="isActive==='second'" :titlename="''" :title="true" :people="false" :showbtn="false" />
            </transition>
          </div>
        </div>
        <!-- <el-tabs class="autho-tab" v-model="activeName"  @tab-click="handleClick">
            <div class="left-title">权限管理</div>
            <div class="right-title">
              <el-button @click="edit">编辑</el-button>
              <el-button>新增</el-button>
              <el-button>删除</el-button>
            </div>
          <el-tab-pane label="权限移交" name="first" style="width:100%!important">
            <div style="width:100%!important">
              <Table :titlename="''" :title="true" :people="false" :showbtn="false"/>
            </div>
          </el-tab-pane>
          <el-tab-pane label="文档移交" name="second" style="width:100%!important">
             <div style="width:100%!important"  >
              <Table :titlename="''" :title="true" :people="false" :showbtn="false"/>
             </div>
          </el-tab-pane>
        </el-tabs> -->

      </el-col>
    </el-row>
  </div>
</template>
<script>
import Table from "@/components/Table.vue";
export default {
  data() {
    return {
      activeName: "first",
      isActive: "first"
    };
  },
  components: {
    Table
  },
  methods: {
    edit() {},
    changetabs(tabs) {
      this.isActive = tabs;
    }
  }
};
</script>

<style lang='less' >
.has-gutter {
  width: 100% !important;
}
.el-table th,
.el-table tr {
  width: 100% !important;
}
.has-gutter {
  width: 100% !important;
}
.el-table__body,
.el-table__footer,
.el-table__header {
  width: 100% !important;
}
.el-table__body {
  width: 100% !important;
}
.el-button {
  background: #fafafa;
  border: 1px solid #eaeaea;
  color: #333333;
  &:hover {
    background: #f5f5f5;
  }
}
// .autho-main {
//   width: 100%;
//   height: 100%;
//   box-sizing: border-box;
// }
// .autho-head {
//   width: 100%;
//   float: left;
//   height: 40px;
// }
.autho-header {
  width: 100%;
  float: left;
  height: 35px;
  position: relative;
}
.left-title {
  position: absolute;
  left: 0;
  top: -40px;
  top: 0px;
  float: left;
  height: 36px;
  line-height: 36px;
}
.right-title {
  position: absolute;
  right: 0;
  top: 1px;
  float: right;
  height: 36px;
}
#tab-first {
  border-right: 0;
  border-radius: 5px 0 0 5px;
}
#tab-second {
  border-radius: 0 5px 5px 0;
  border-left: 0;
}
.autho-tabs {
  width: 300px;
  margin: auto;
  height: 35px;
  line-height: 35px;
  > span {
    float: left;
  }
  .el-tabs__item {
    margin: 0;
    background: rgba(255, 255, 255, 1);
    border: 1px #eaeaea solid;
  }
  .el-tabs__item:hover {
    color: inherit;
    background-color: #f5f5f5;
  }
  .is-active {
    background: rgba(107, 107, 107, 1);
    color: rgba(255, 255, 255, 1);
  }
  .is-active:hover {
    color: rgba(255, 255, 255, 1) !important;
    background: #6b6b6b !important;
  }
}
.autho-table {
  width: 100%;
  float: left;
  padding-top: 10px;
  position: relative;
  .tab {
    position: absolute;
    left: 0;
    top: 0;
  }
}
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.5s;
}
.fade-enter,
.fade-leave-to {
  opacity: 0;
}
</style>


