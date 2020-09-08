<template>
    <div class="Choose">
        <div class="choosetree">
            <el-input v-model="searchMsg" placeholder="请输入要搜索的内容"></el-input>
            <el-tree 
              v-if="!searchMsg"
              ref="tree"
              class="tree" 
              empty-text="没有数据" 
              :data="treelists" 
              default-expand-all 
              node-key="id"
              highlight-current 
              :expand-on-click-node='false'
              @node-click="handleNodeClick">
                <div style="float:right;width:100%;" class="custom-tree-node" slot-scope="{ node, data }">
                    <span>{{node.label}}</span>
                    <span style="float:right;" v-if="data.checked" class="menu"><i class="el-icon-check"></i></span>
                </div>
            </el-tree>
            <br>
            <div class="search-lists" v-if="searchMsg">
              <div class="search-item" v-for="(item,index) in Treedata" :key="index">
                <div @click="handleSerachItem(index)">{{item.label}}<span v-if="item.checked" class="menu" style="margin-right: 20px "><i class="el-icon-check"></i></span></div>
              </div>
            </div>
        </div>
        <div class="choosepeople">
            <span class="choosetitle">已选择部门及角色</span>
            <div class="chooselist">
                <p style="width:100%;float:left;"  @click="choosechecked(index,item)" v-for="(item, index) in chooselist" :key="item.index">
                    <el-col :span="8">
                        <span>{{item.alias}}</span></el-col>
                    <el-col style="float:right;" :span="8">
                        <span v-if="item.checked"  class="delete">
                          <i class="el-icon-check"></i>
                        </span>
                    </el-col>
                </p>
            </div>
        </div>
    </div>
</template>
<script>
import utils from '@/common/js/util.js';
import { mapGetters } from 'vuex';
export default {
  props: {
    Treedata: {
      type: Array,
      validator: function(value) {
        return [];
      }
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
      treelists: this.Treedata,
      showcommanditem: {},
      searchList: [],
      searchMsg: ''
    };
  },
  created() {
    console.log(this.Treedata);
    // // this.treelist = utils.parseTree(listss);
    // console.log(this.$store.state.department.departmentInfoFromat, 1111);
    // // this.searchList = utils.fromatTree(lis);
    // this.$store
    //   .dispatch("getDepart", {
    //     self: this,
    //     obj: {}
    //   })
    //   .then(res => {
    //     this.treelists = utils.parseTree(res);
    //   })
    //   .catch(err => {
    //     console.log(err);
    //   });
  },
  computed: {},
  methods: {
    choosechecked(index, item) {
      this.chooselist.forEach((item, idx) => {
        item.checked = false;
        if (idx === index) {
          item.checked = !item.checked;
          this.chooselist.splice(index, 1, item);
        }
      });
      this.$emit('choosemember', item);
    },
    handleNodeClick(item) {
      //树形的点击事件
      let that = this;
      let id = item.id || item.ID;
      let treelist = this.Treedata;
      let check = utils.checkedTreeitem(treelist, id, true);
      let ischecked = false;
      if (item.type === 'department') {
        this.$store
          .dispatch('getDepartEmployees', { self: this, obj: { deptId: id } })
          .then(res => {
            res.content.map(item => {
              item.checked = false;
            });
            this.chooselist = res.content;
          })
          .catch(err => {
            console.log(1111, err);
          });
      }

      //   let chooselist = this.chooselist;
      //   for (let i = 0, len = chooselist.length; i < len; i++) {
      //     let item = chooselist[i];
      //     if (item.ID === check.obj.ID) {
      //       if (!check.obj.checked) that.deletecommand(i);
      //       ischecked = true;
      //       break;
      //     }
      //   }
      //   !ischecked && this.chooselist.push(check.obj);
    },
    handleSerachItem(index) {
      //搜索列表的点击事件
      let that = this;
      this.searchList[index].checked = !this.searchList[index].checked;
      this.searchList[index].type = 'serach';
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
.custom-tree-node {
  .menu {
    display: block;
    text-align: right;
    font-size: 26px;
    font-weight: bolder;
    color: #d8d8d8;
    cursor: pointer;
  }
}
.bl1 {
  border-left: 1px solid red;
}
.Choose {
  overflow: hidden;
  height: 410px;
  border-bottom: 1px solid #eaeaea;
  & > div {
    height: 100%;
    width: 50%;
    float: left;
    padding: 30px 25px;
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
