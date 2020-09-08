<template>
  <div>
    <el-tree class="tree" empty-text="没有数据" :data="treelists" default-expand-all node-key="id" ref="tree" highlight-current :expand-on-click-node='false' @node-click="handleNodeClick">
      <div class="custom-tree-node" slot-scope="{ node, data }">
        <div class="tree-label-name">{{node.label}}</div>
        <div class="menu" v-if="node.id!==1">
          <el-popover placement="bottom-end" @show='()=>show(data)' width="80" trigger="hover" v-if="getEdit">
            <div slot="reference" class="moremenu">
              
            </div>
            <div class="menu-popover" style="width:80px;margin-top:-10px;">
              <div>
                <span class="menu-popover-item" @click="changeName(data)">修改名称</span>
              </div>
              <div>
                <span class="menu-popover-item" @click="adddePartment(data)">添加子部门</span>
              </div>
              <div>
                <span class="menu-popover-item" @click="del(data)">删除</span>
              </div>
            </div>
          </el-popover>
        </div>
      </div>
    </el-tree>

  </div>
</template>
<script>
import { mapGetters, mapActions } from "vuex";
import utils from "@/common/js/util.js";
let id = 1000;

export default {
  data() {
    return {
      treelist: [],
      currentNode: {},
      id: "",
      modifyName: "",
      dataItem: {}
    };
  },
  props: {
    treelists: {
      type: Array,
      validator: function() {
        return [];
      }
    }
  },
  computed: {
    getEdit: {
      get() {
        return this.$store.getters.getEdit;
      }
    }
  },
  components: {},
  created() {},
  methods: {
    show(data) {},
    handleNodeClick(e) {
      this.currentNode = {
        id: e.id || e.ID,
        label: e.label || "",
        children: e.children || [],
        $treeNodeId: e.$treeNodeId,
        iscom: e.iscom || false
      };
      this.$emit("handleNodeClick", this.currentNode);
    },
    currentNodeInfo() {
      // return this.currentNode;
    },
    butModifyName(boo) {
      // Boolean
      this.changeNameDialog = false;
      // this.$emit("changeName", { name: name, data: this.dataItem });
      if (boo) {
        let name = this.modifyName;
        this.dataItem = {};
        this.modifyName = "";
      } else {
        this.dataItem = {};
        this.modifyName = "";
      }
    },
    changeName(data) {
      this.$emit("changeName", data);
    },
    adddePartment(data) {
      this.$emit("adddePartment", data);
    },
    del(data) {
      this.$emit("del", data);
    }
  }
};
</script>
<style lang="less">
.tree {
  background-color: transparent !important;
  // background-color:#fafafa;
  color: rgba(51, 51, 51, 1);
}
.el-popover {
  top: -1px;
  right: 5px;
}
.tree-label-name {
  display: block;
  max-width: 200px;
  height: 28px;
  line-height: 28px;
}
.moremenu {
  position: absolute;
  right: 12px;
  top: 0;
  width: 10px;
  height: 14px;
  margin-top: 7px;
  background: url("../assets/more.png") no-repeat -5px center;
}
.el-popover {
  min-width: auto;
}
.custom-tree-node {
  float: right;
}
.el-tree-node__expand-icon {
  margin-left: 20px;
}
.el-tree-node__content:hover {
  background: rgba(238, 238, 238, 1);
}
.el-tree--highlight-current .el-tree-node.is-current > .el-tree-node__content {
  background: rgba(238, 238, 238, 1);
}
.el-tree-node__content {
  padding: 1px 20px 1px 0px;
  font-size: 14px;
  height: 28px;
  line-height: 28px;
  position: relative;
  .el-tree-node__expand-icon {
    float: left;
  }
  .custom-tree-node {
    float: left;
    display: block;
    width: 100%;
    height: 28px;
    line-height: 28px;
  }
}
</style>

<style lang="less" scoped>
.menu {
  // transform: rotate(90deg);
  float: right;
  .menu-popover {
    width: 120px;
    display: block;
    height: 200px;
    text-align: center;
  }
}
.menu-popover-item {
  margin: 6px 0;
  display: block;
  float: left;
  // width: 100%;
  cursor: pointer;
}
</style>
