<template>
  <el-dialog class="modify-dialog" width="700px" title="修改" :visible="visible">
    <div class="Choose">
      <div class="">
        <el-input v-model="searchMsg" @change="handlesearch" clearable placeholder="请输入要搜索的内容"></el-input>
        <el-tree v-if="!searchMsg" ref="tree" class="tree" empty-text="没有数据" :data="treeData" default-expand-all node-key="id" :expand-on-click-node='false' @node-click="handleNodeClick">
          <div class="custom-tree-node" slot-scope="{ node, data }">
            <span>{{node.label}}</span>
            <span v-if="data.checked" class="menu">
              <i class="el-icon-check"></i>
            </span>
          </div>
        </el-tree>
        <br>
        <div class="search-lists" v-if="searchMsg">
          <div class="search-item" v-for="(item,index) in searchList" :key="index">
            <div @click="handleSerachItem(index)">{{item.name}}
              <span v-if="item.checked" class="menu" style="margin-right: 20px ">
                <i class="el-icon-check"></i>
              </span>
            </div>
          </div>
        </div>
      </div>
      <div class="">
        <span class="choosetitle">已选择部门及角色</span>
        <div class="chooselist">
          <!-- {{checkedList}} -->
          <p v-for="(item, index) in checkedList" :key="item.index">
            <el-col :span="12">
              <span>{{item.label}}</span>
            </el-col>
            <el-col :span="10">
              <el-dropdown @command="handleCommand" @visible-change="showcommand(item,index)" trigger="click">
                <span class="el-dropdown-link">
                  {{item.characterName}}
                  <i class="el-icon-arrow-down el-icon--right"></i>
                </span>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item command="0">普通成员</el-dropdown-item>
                  <el-dropdown-item command="2">知识专员</el-dropdown-item>
                  <el-dropdown-item command="3">部门主管</el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </el-col>
            <el-col :span="2">
              <span v-if="!onlySelectChange" @click="deletecommand(index,item.ID)" class="delete">
                <i class="el-icon-close"></i>
              </span>
            </el-col>
          </p>
        </div>
      </div>
    </div>
    <span slot="footer" class="dialog-footer">
      <el-button @click="cancal">取 消</el-button>
      <el-button type="primary" @click="sure">确 定</el-button>
    </span>
  </el-dialog>
</template>
<script>
import utils from "@/common/js/util.js";
let searchDepart = function() {
  return new Promise((resolve, reject) => {
    this.$store
      .dispatch("searchDepart", {
        self: this,
        obj: { search: this.searchMsg }
      })
      .then(res => {
        resolve(res);
      })
      .catch(err => {
        reject([]);
      });
  });
};
export default {
  data() {
    return {
      chooseChecked: {
        index: 0,
        type: 0
      },
      chooselist: [],
      treelist: [],
      treelists: [],
      showcommanditem: {},
      searchList: [],
      searchMsg: "",
      changeData: []
    };
  },
  props: {
    visible: {
      type: Boolean
    },
    checkedList: {
      type: Array,
      validator: function() {
        return [];
      }
    },
    treeData: {
      type: [Object, Array],
      required: true
    },
    radio: {
      type: Boolean,
      default: false
    },
    checkedIds: {
      type: Array,
      default: [],
      validator: function() {
        return [];
      }
    },
    onlySelectChange: {
      type: Boolean,
      default: false
    },
    checkedList: {
      type: Array
    }
  },
  watch:{
    
  },
  created() {},
  methods: {
    handleCommand(command) {
      //右侧切换选择管理员类型
      let index = this.chooseChecked.index;
      // console.log("run", this.checkedList[index]);
      let obj = {};
      obj.id = this.checkedList[index].id;
      obj.type = "modify";
      obj.role = command;
      obj.data = {
        role: command
      };
      this.$emit("handleChangeRole", {
        index: index,
        type: command,
        data: obj
      });

      this.changeData.push(obj);
    },
    showcommand(item, index) {
      //右侧切换选择管理员类型获取index
      this.chooseChecked.index = index;
    },
    deletecommand(index, id) {
      //右侧点击删除选中的部门
      // if (this.searchMsg) {
      //   //兼容树形选中，搜索选择
      //   // console.log(1);
      //   this.searchList[index].checked = false;
      // } else {
      //   id && utils.checkedTreeitem(this.treeData, id, this.radio);
      // }
      // this.chooselist[index].character = -1;
      // this.chooselist.splice(index, 1);
      if (!this.onlySelectChange) {
        this.$emit("deleteItemCheckedList", index, id);
      }
    },

    handleNodeClick(item) {
      //树形的点击事件
      // let id = item.id || item.ID;
      // this.addCoolist(id);
      if (!this.onlySelectChange) {
        this.$emit("changeCheckedList", item.id, "");
      }
    },
    handlesearch() {
      searchDepart.call(this).then(res => {
        this.searchList = utils.fromatTree(res);
      });
    },
    handleSerachItem(index) {
      //搜索列表的点击事件
      // let that = this;
      // this.searchList[index].checked = !this.searchList[index].checked;
      // // this.searchList[index].type = "serach";
      // this.showCharacterName(this.searchList[index]);
      // let id = this.searchList[index].id;
      // let ischecked = false;
      // let chooselist = this.chooselist;
      // for (let i = 0, len = chooselist.length; i < len; i++) {
      //   let item = chooselist[i];
      //   if (item.ID === id) {
      //     if (!this.searchList[index].checked) that.deletecommand(i);
      //     ischecked = true;
      //     break;
      //   }
      // }
      // !ischecked && this.chooselist.push(this.searchList[index]);
    },
    cancal() {
      // let treelist = this.treeData;
      // let check = utils.checkedTreeitem(treelist, "none", true);
      // this.chooselist = [];
      this.$emit("cancal");
    },
    sure() {
      // this.chooselist = [];
      // let treelist = this.treeData;
      // let check = utils.checkedTreeitem(treelist, "none", true);
      this.$emit("saveCurrentChange", this.changeData);
      this.changeData = [];
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
.modify-dialog {
  .el-dialog__header {
    padding: 10px 10px 0 20px;
    i {
      display: none;
    }
  }
  .el-dialog__body {
    padding: 0px 0 10px 0;
  }
  .tree {
    max-height: 400px;
    overflow-y: auto;
  }
  .el-dropdown {
    cursor: pointer;
    width: 100px;
    // i {
    //   margin-top: 2px;
    //   float: right;
    // }
  }
}
.Choose {
  overflow: hidden;
  height: 410px;
  border-bottom: 1px solid #eaeaea;
  padding: 20px;
  > div {
    height: 100%;
    width: 49%;
    // padding: 30px 25px;
    box-sizing: border-box;
    &:nth-of-type(1) {
      float: left;
      border-right: 1px solid #eaeaea;
      padding-right: 20px;
    }
    &:nth-of-type(2) {
      float: right;
    }
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
    .choosetitle {
      color: #999999;
    }
    .chooselist {
      .delete {
        display: block;
        text-align: right;
        font-size: 26px;
        font-weight: bolder;
        color: #d8d8d8;
        cursor: pointer;
      }
    }
  }
}
.search-lists {
  float: left;
  width: 100%;
  .search-item {
    text-indent: 20px;
    padding: 2px 0;
    width: 100%;
    &:hover {
      background-color: #eeeeee;
    }
  }
}
</style>

