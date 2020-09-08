<template>
  <div class="Choose">
    <div class="choosepeople" style="border:0;" v-if="choosepeople">
      <div style="width:100%;">
        <div>
          <div>
            <span class="choosetitle">请选择管理成员</span>
          </div>
          <div class="checkpe">
            <div class="block" style="width:100%">
              <el-cascader placeholder='全体成员' :props="{value:'id',children:'children'}" change-on-select expand-trigger="click" :options="hoverlists" v-model="checkedD" @change="handleChange">
              </el-cascader>
            </div>
          </div>
          <!-- <div class="search">
                <el-input v-model="searchMsg" placeholder="请输入要搜索的内容"></el-input>
              </div> -->
        </div>
        <div class="lists" v-loading="staffListLoading">
          <div v-for="(item,index) in staffList" :key="index" @click="checkStaffItem(index)">
            <span>{{item.alias}}</span>
            <span v-if="item.checked" style="float:right;">
              <i class="el-icon-check"></i>
            </span>
          </div>
        </div>
      </div>
    </div>
    <div class="choosepeople">
      <span class="choosetitle">选择管理的部门</span>
      <div class="chooselist">
        <el-tree v-if="!searchMsg" ref="tree" v-loading="TreeLoading" class="tree" empty-text="没有数据" :data="treelists" default-expand-all node-key="id" highlight-current :expand-on-click-node='false' @node-click="handleNodeClick">
          <div style="float:right;width:100%;" class="custom-tree-node" slot-scope="{ node, data }">
            <span>{{node.label}}</span>
            <span style="float:right;" v-if="data.checked" class="menu">
              <i class="el-icon-check"></i>
            </span>
          </div>
        </el-tree>
      </div>
    </div>
    <span v-if="choosepeople">
      管理成员仅能选择一人，管理部门可多选
    </span>
  </div>
</template>
<script>
import utils from "@/common/js/util.js";
import { mapGetters } from "vuex";
export default {
  props: {
    choosepeople: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      chooseChecked: {
        index: 0,
        type: 0
      },
      chooselist: [],
      treelist: [],
      treelists: [],
      hoverlists: [],
      showcommanditem: {},
      searchList: [],
      searchMsg: "",
      options: [],
      checkedD: [],
      staffList: [],
      checkedIDS: {
        left: "",
        right: []
      },
      TreeLoading: true,
      staffListLoading: true
    };
  },
  created() {
    this.getAllpersonal();
    this.getAllDepart();
  },
  computed: {},
  methods: {
    getAllDepart() {
      this.$store
        .dispatch("getDepart", {
          self: this,
          obj: {}
        })
        .then(res => {
          let departmentList = utils.parseTree(res);
          this.hoverlists = utils.delChildren(departmentList);
          let treelists = utils.addCompany(departmentList, {
            label: this.$store.state.user.userInfo.enterpriseName || ""
          });
          this.TreeLoading = false;
          this.treelists = treelists;
        })
        .catch(err => {});
    },
    getAllpersonal() {
      this.$store
        .dispatch("getAllpeople", {
          self: this,
          obj: {
            page: 1,
            pagesize: 1000
          }
        })
        .then(res => {
          this.staffListLoading = false;
          res.content.map((item, index) => {
            item.checked = false;
          });
          this.staffList = res.content;
        })
        .catch(err => {
          console.error("err", err);
        });
    },
    changeData() {
      this.$emit("changeData", this.checkedIDS);
    },
    checkStaffItem(index) {
      this.staffList.map((item, idx) => {
        item.checked = false;
        if (idx === index) {
          item.checked = true;
          this.$set(this.staffList, idx, item);
          this.checkedIDS.left = item.id;
          this.changeData();
        }
      });
    },
    handleChange(e) {
      let id = this.checkedD.slice(-1);
      this.$store
        .dispatch("getDepartEmployees", {
          self: this,
          obj: { deptId: id, page: 1, pagesize: 1000 }
        })
        .then(res => {
          res.content.map((item, index) => {
            item.checked = false;
            this.staffList.splice(index, 1, item);
          });
          this.staffList = res.content;
        })
        .catch(err => {
          console.log(1);
        });
    },
    handleNodeClick(item) {
      //树形的点击事件
      let that = this;
      let id = item.id || item.ID;
      let treelist = this.treelists;
      utils.checkedTreeitem(treelist, id);
      let indexOf = this.checkedIDS.right.indexOf(id);
      if (indexOf >= 0) {
        this.checkedIDS.right.splice(indexOf, 1);
      } else {
        this.checkedIDS.right.push(id);
      }
      this.changeData();
    },
    handleSerachItem(index) {
      //搜索列表的点击事件
      let that = this;
      this.searchList[index].checked = !this.searchList[index].checked;
      this.searchList[index].type = "serach";
      this.showCharacterName(this.searchList[index]);
      let id = this.searchList[index].id;
      let ischecked = false;
      let chooselist = this.chooselist;
      for (let i = 0, len = chooselist.length; i < len; i++) {
        let item = chooselist[i];
        if (item.ID === id) {
          if (!this.searchList[index].checked) that.deletecommand(i);
          ischecked = true;
          break;
        }
      }
      !ischecked && this.chooselist.push(this.searchList[index]);
    }
  }
};
</script>
<style lang="less">
.menu {
  float: right;
}
.bl1 {
  border-left: 1px solid red;
}
.el-dialog__body {
  padding: 10px 20px;
}
.Choose {
  height: 410px;
  border-bottom: 1px solid #eaeaea;
  & > div {
    height: 100%;
    width: 50%;
    float: left;
    padding: 0 20px 0 20px;
    box-sizing: border-box;
  }
  .choosetree {
    .el-input {
      margin-bottom: 25px;
      input {
        background: #f5f5f5;
        border: 0;
      }
    }
    .el-tree--highlight-current
      .el-tree-node.is-current
      > .el-tree-node__content {
      background: #fff;
      border-radius: 4px;
      position: relative;
    }
  }
  .choosepeople {
    border-left: 1px solid #eaeaea;
    overflow-y: auto;
    .choosetitle {
      color: #999999;
    }
    .chooselist {
      width: 100%;
      p {
        cursor: pointer;
      }
      P + P {
        padding-top: 10px;
      }
      .delete {
        display: block;
        text-align: right;
        font-size: 26px;
        font-weight: bolder;
        color: #d8d8d8;
        cursor: pointer;
      }
      .el-dropdown-link {
        cursor: pointer;
      }
    }
  }
}
.checkpe {
  width: 100px;
  float: left;
}
.search {
  width: 160px;
  float: right;
}
.lists {
  padding: 5px 0;
  width: 100%;
  height: 300px;
  overflow-y: auto;
  float: left;
  > div {
    padding: 2px 0;
    cursor: pointer;
    &:hover {
      background-color: #eaeaee;
    }
  }
}
</style>
