<template>
  <div class="panal">
    <div class="table-block">
      <div class="title">
        <span>离职管理</span>
      </div>
      <!-- <template>
        <Table :pagination=false :titlename="'离职管理'" :title="true" :people="false" :showbtn="false" :Tabledata="getDepartureList" :tabledataHeader="getDepartureList.head" @rowclick="rowclick" />
      </template> -->
      <el-table :data="getDepartureList" stripe style="width: 100%" v-loading="tableLoading" @row-click="rowclick">
        <el-table-column prop="alias" label="用户名">
        </el-table-column>
        <el-table-column prop="depts" label="部门">
          <template slot-scope="scope">
            <div>
              <span v-for="(item,idx) in scope.row.depts" :key="idx" v-if="idx<3">
                {{item.name}}
              </span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
        </el-table-column>
      </el-table>
    </div>
    <div class="pagination-block" v-show="total>0">
      <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentpages" :page-size="15" layout="total, prev, pager, next" :total="parseInt(total)">
      </el-pagination>
    </div>
  </div>
</template>
<script>
import Table from "@/components/Table.vue";
export default {
  data() {
    return {
      path: "",
      total: "",
      fileCheckitem: {
        cloudUserId: ""
      },
      deleteHandovEremployeesitem: {
        id: ""
      },
      tableLoading: true
    };
  },
  components: {
    Table
  },
  beforeCreate() {},
  created() {
    this.getdeparturelist();
  },
  methods: {
    getdeparturelist() {
      this.$store
        .dispatch("departureManagement", {
          self: this,
          query: {}
        })
        .then(
          data => {
            console.log(data);
            this.total = data.totalElements;
            this.tableLoading = false;
          },
          () => {
            this.tableLoading = false;
          }
        );
    },
    deleteHandovEremployees() {
      this.$confirm("该员工没可移交的权限和文档，系统将自动转为已离职状态", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          this.$store
            .dispatch("deleteHandovEremployees", {
              self: this,
              query: this.deleteHandovEremployeesitem
            })
            .then(data => {
              console.log(data);
              this.$message({
                message: "离职成功",
                type: "success"
              });
              this.getdeparturelist();
            })
            .catch(() => {
              this.$message({
                message: "离职失败",
                type: "warning"
              });
            });
        })
        .catch(() => {});
    },

    rowclick(row) {
      console.log(row);
      this.fileCheckitem.cloudUserId = row.cloudUserId;
      this.$store
        .dispatch("fileCheck", {
          self: this,
          query: this.fileCheckitem
        })
        .then(data => {
          console.log(data);
          if (data === false) {
            this.deleteHandovEremployeesitem.id = row.id;
            this.deleteHandovEremployees();
          } else {
            this.$router.push({
              path: `/handOver/`,
              query: { name: row.name, id: row.cloudUserId }
            });
          }
        })
        .catch(() => {
          this.$message({
            message: "",
            type: "warning"
          });
        });
    },
    handleCurrentpages(pages) {
      //切换页面
      this.current = pages;
      this.getdeparturelist();
    },
    handleSizeChange(val) {
      console.log(`每页 ${val} 条`);
    }
  },
  computed: {
    getDepartureList() {
      let deparTureManagementList = this.$store.state.departure
        .departuremanagementlist;
      if (deparTureManagementList) {
        let newlist = deparTureManagementList;
        let dept = [];
        newlist.forEach(item => {
          if (item.status === -2) {
            item.status = "未移交";
          } else {
            item.status = "移交完成";
          }
        });
        newlist.head = [
          {
            prop: "name",
            label: "名字"
          },
          {
            prop: "depts",
            label: "部门"
          },
          {
            prop: "status",
            label: "状态"
          }
        ];
        return newlist;
      }
    }
  }
};
</script>

<style lang='less' scoped>
.pagination-block {
  position: absolute;
  left: 40%;
  bottom: 11px;
}
.title {
  padding: 15px 20px;
  span {
    font-size: 18px;
    color: #333333;
  }
  button {
    cursor: pointer;
    float: right;
    border: 0;
    background: #f5f5f5;
    padding: 7px 19px;
  }
}
</style>
<style lang="less">
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
</style>



