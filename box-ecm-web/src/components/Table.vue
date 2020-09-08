<template>
  <div class="Table">
    <div class="showandtool">
      <div class="left">
        <p v-show="title" class="title">{{titlename}}
          <span v-show="people" class="departspace">已使用</span>
        </p>
        <p v-show="people">
          <span class="department">{{departmentName}}
            <span>({{TotalCount}}人)</span>
          </span>
          <!-- <span class="departspace">暂未开通部门空间</span> -->
        </p>
      </div>
      <div class="right" v-show="showbtn">
        <el-button @click="addmember">添加成员</el-button>
        <el-button @click="changeName">修改部门名称</el-button>
        <el-button @click="addDepart">添加子部门</el-button>
      </div>
    </div>
    <el-table highlight-current-row :data="Tabledata" stripe border @row-click="rowclick" @selection-change="handleSelectionChange">
      <el-table-column v-if="checkbox" type="selection" width="55">
      </el-table-column>
      <template v-for="(item,index) in tabledataHeader">
        <el-table-column :prop="item.prop" :label="item.label" :key="index">
        </el-table-column>
      </template>
    </el-table>
    <!-- <div class=" paginationblock">
      <el-pagination v-show="pagination" @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="currentPage" :page-size=limit layout="total, prev, pager, next" :total=TotalCount>
      </el-pagination>
    </div> -->
  </div>
</template>

<script>
export default {
  props: {
    departmentName: {
      type: String,
      default: "全体员工"
    },
    titlename: {
      type: String,
      default: "标题"
    },
    title: {
      type: Boolean,
      default: true
    },
    people: {
      type: Boolean,
      default: true
    },
    showbtn: {
      type: Boolean,
      default: true
    },
    checkbox: {
      type: Boolean,
      default: false
    },
    pagination: {
      type: Boolean,
      default: true
    },
    Tabledata: {
      type: Array,
      validator: function(value) {
        return [];
      }
    },
    tabledataHeader: {
      type: Array,
      validator: function(value) {
        return [];
      }
    },
    TotalCount: {
      type: [String, Number]
    },
    currentPage: {
      type: Number
    },
    limit: {
      type: Number
    }
  },
  data() {
    return {};
  },
  methods: {
    show(data) {},
    addDepart() {
      this.$emit("addDepart");
    },
    changeName() {
      this.$emit("changeName");
    },
    addmember() {
      // this.$router.push({
      //   path: `/DepartmentAndStaffManagement/add/Member`
      // });
      this.$emit("addmember");
    },
    rowclick(row, event, column) {
      this.$emit("rowclick", row);
    },
    handleCurrentChange(val) {
      this.$emit("handleCurrentChange", val);
    },
    handleSizeChange(val) {
      this.$emit("handleSizeChange", val);
    },
    handleSelectionChange(val) {
      console.log(val)
      this.$emit("handleSelectionChange", val);
    }
  }
};
</script>
<style lang="less">
.Table {
  height: 100%;
  .paginationblock {
    position: absolute;
    bottom: 50px;
    left: 40%;
  }
  .el-table {
    margin-top: 20px;
    border: 0;
    .el-table__row {
      cursor: pointer;
    }
  }
  .el-table--border td,
  .el-table--border th {
    border-right: 0;
  }
  .el-table__body tr.hover-row > td {
    background: #eeeeee !important;
  }
  .el-table th.is-leaf {
    background: #fafafa;
  }
  .el-table td,
  .el-table th.is-leaf {
    border: 0;
  }
  .el-table--border::after,
  .el-table--group::after,
  .el-table::before {
    background: transparent;
  }
  .showandtool {
    overflow: hidden;
    .left {
      float: left;
      .title {
        font-size: 18px;
        color: #333333;
      }
      .department {
        font-size: 18px;
        color: #333333;
        span {
          margin-left: 10px;
        }
      }
      .departspace {
        margin-left: 20px;
        font-size: 14px;
        color: #999999;
      }
    }
    .right {
      float: right;
      .el-button {
        background: #fafafa;
        border: 1px solid #eaeaea;
        color: #333333;
        &:hover {
          background: #f5f5f5;
        }
      }
    }
  }
}
</style>
