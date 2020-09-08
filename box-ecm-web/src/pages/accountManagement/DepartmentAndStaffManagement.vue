<template>
  <div class="panal">
    <el-col :span="6" style="height:100%;">
      <div class="tree-block" style="background:#fafafa">
        <div style="padding:20px 20px 0;margin-bottom:10px;">
          <el-input placeholder="请输入内容" prefix-icon="el-icon-search" v-model="search" @change="handlesearch" clearable>
          </el-input>
        </div>
        <div style="background:#fafafa;min-height:400px">
          <template v-if='!searchTypes'>
            <Tree v-loading="treeLoading" :treelists='treelists' @changeName="modDepartmentName" @adddePartment="addDepart" @del="deleteDepartment" @handleNodeClick="handleNodeClick" />
          </template>
          <template v-if='searchTypes'>
            <div class="search-lists" style="min-height:400px">
              <dl v-if="searchData.members.length!==0">
                <dt>成员</dt>
                <dd :class="searchData.active===item.id?'is-active':''" v-for="item in searchData.members" :key="item.id" @click="handlecurrent(item)">
                  {{item.alias}}
                </dd>
              </dl>
              <dl v-if="searchData.department.length!==0">
                <dt>
                  <i></i>部门</dt>
                <dd :class="searchData.active===item.ID?'is-active':''" @click="handlecurrent(item)" v-for="item in searchData.department" :key="item.id">
                  {{item.name}}
                </dd>
              </dl>
              <div class="none" v-show="searchData.members.length==0 && searchData.department.length===0" style="min-height:400px">
                <!-- class="none personal" -->
                无符合搜索条件的部门或人
              </div>
            </div>
          </template>
        </div>
      </div>
    </el-col>
    <el-col :span="18" style="height:100%;">
      <transition name="fade" mode="out-in">
        <div style="overflow-y:auot;height:100%;">
          <router-view :rowdata="rowdata" @departure="departure" :treeData="treeData" @cancalEdit="cancalEdit" @deleteDepartmentList="deleteDepartmentList" @sureEdit="sureEdit"></router-view>
        </div>
      </transition>
      <transition name="fade" mode="out-in">
        <template v-if="showTable">
          <div>
            <div class="table-block">
              <div class="Table">
                <div class="showandtool">
                  <div class="left">
                    <p>
                      <span class="department">{{departmentName}}
                        <span>({{total}}人)</span>
                      </span>
                    </p>
                  </div>
                  <div class="right" v-if="getEdit">
                    <!-- getEdit -->
                    <el-button @click="addmember">添加成员</el-button>
                    <el-button @click="modDepartmentName">修改部门名称</el-button>
                    <el-button @click="addDepart">添加子部门</el-button>
                  </div>
                </div>
                <el-table v-loading="tableLoading" :data="personalList" stripe border @row-click="rowclick">
                  <template v-for="(item,index) in head">
                    <!-- || item.prop==='departmentName' -->
                    <el-table-column :prop="item.prop" v-if="item.prop==='departmentName'" :label="item.label" :key="index">
                      <template slot-scope="scope">
                        <div>
                          <p v-for="(cur,idx) in scope.row.showDepartmentName" :key="idx" v-if="idx<3">
                            {{cur}}
                          </p>
                          <el-popover placement="bottom-start" v-if="scope.row.showDepartmentName && scope.row.showDepartmentName.length>3" width="180" trigger="hover">
                            <p slot="reference" style="width:10px">...</p>
                            <div>
                              <p v-for="(cur,idx) in scope.row.showDepartmentName" :key="idx" style="width:180px;overflow:hidden;text-align:center;text-overflow: ellipsis;white-space: nowrap;">
                                {{cur}}
                              </p>
                            </div>
                          </el-popover>
                        </div>
                      </template>
                    </el-table-column>
                    <el-table-column :prop="item.prop" v-else-if="item.prop==='roleNames'" :label="item.label" :key="index">
                      <template slot-scope="scope">
                        <div>
                          <p v-for="(cur,idx) in scope.row.showPrivleges" :key="idx" v-if="idx<3">
                            {{cur}}
                          </p>
                          <el-popover placement="bottom-start" v-if="scope.row.showPrivleges && scope.row.showPrivleges.length>3" width="180" trigger="hover">
                            <p slot="reference" style="width:10px">...</p>
                            <div>
                              <p v-for="(cur,idx) in scope.row.showPrivleges" :key="idx" style="width:180px;overflow:hidden;text-align:center;text-overflow: ellipsis;white-space: nowrap;">
                                {{cur}}
                              </p>
                            </div>
                          </el-popover>
                        </div>
                      </template>
                    </el-table-column>
                    <el-table-column :prop="item.prop" v-else :label="item.label" :key="index"></el-table-column>
                  </template>
                </el-table>
                <!-- <div v-show="total===0" class="none personal"></div> -->
              </div>
            </div>
            <div class="paginationblock" v-show="total>0">
              <el-pagination @current-change="handleCurrentChange " :current-page="current " :page-size="15 " layout="total, prev, pager, next " :total="total">
              </el-pagination>
            </div>
          </div>
        </template>
      </transition>
    </el-col>
    <el-dialog title="新建部门" :visible.sync="addDepartDialog" width="30% ">
      <el-input v-model="addName"></el-input>
      <span slot="footer" class="dialog-footer ">
        <el-button @click="cancalAddDepart(false)">取 消</el-button>
        <el-button type="primary" :disabled='!addName' @click="sureAddDepart(true)">确 定</el-button>
      </span>
    </el-dialog>
    <el-dialog :title="'修改'+NameDialogTip+'部门名称'" :visible.sync="changeNameDialog" width="30% ">
      <el-input v-model="modifyName"></el-input>
      <span slot="footer" class="dialog-footer">
        <el-button @click="butModifyName(false) ">取 消</el-button>
        <el-button type="primary" :disabled='!modifyName' @click="butModifyName(true)">确 定</el-button>
      </span>
    </el-dialog>
    <el-dialog title="提示" class="copylink " :visible.sync="copyLink ">
      <div class="link ">
        <input @click="selectLink " id="link " readOnly="true " type="text " :value="linkA ">
        <div v-clipboard:copy="linkA " v-clipboard:success="onCopy " v-clipboard:error="onError ">复制</div>
      </div>
      <span slot="footer" style="padding:20px 0;text-align:left; ">
        复制链接，发送给同事，对方填写完成后即可
      </span>
    </el-dialog>
  </div>
</template>
<script>
import Tree from "@/components/Tree.vue";
import utils from "@/common/js/util.js";
import Menu from "@/components/Menu.vue";
let searchDepart = function() {
  return new Promise((resolve, reject) => {
    this.$store
      .dispatch("searchDepart", {
        self: this,
        obj: { search: this.search }
      })
      .then(res => {
        resolve(res);
      })
      .catch(err => {
        reject([]);
      });
  });
};
let searchMembers = function(id) {
  return new Promise((resolve, reject) => {
    this.$store
      .dispatch("getDepartEmployees", {
        self: this,
        obj: { search: this.search, deptId: id }
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
      linktoken: "",
      link: "",
      departmentName: "全部成员",
      search: "",
      searchTypes: false,
      searchData: {
        active: "0",
        members: [],
        department: []
      },
      treelists: [],
      treeData: [],
      deleteDepartmentList: [],
      treeLoading: true,
      tableLoading: true,
      staffList: [],
      personalList: [],
      head: [
        {
          prop: "alias",
          label: "用户名"
        },
        {
          prop: "name",
          label: "姓名"
        },
        {
          prop: "departmentName",
          label: "所在的部门"
        },
        {
          prop: "roleNames",
          label: "应用权限"
        },
        {
          prop: "phoneNumber",
          label: "手机号"
        }
      ],
      total: 0,
      current: 1,
      rowdata: {},
      copyRowData: {},
      addDepartDialog: false,
      changeNameDialog: false,
      NameDialogTip: "",
      addName: "",
      modifyName: "",
      copyLink: false
    };
  },
  computed: {
    treelist: {
      get() {
        return this.$store.state.department.departmentInfoFromat;
      }
    },
    showTable: {
      get() {
        let searchTypes = this.searchTypes;
        let type = false;
        if (searchTypes === "none" || searchTypes == "") {
          type = true;
        }
        return this.$route.path === "/DepartmentAndStaffManagement" && type;
      }
    },
    linkA: {
      get() {
        let userName = this.$store.getters.getUserinfo;
        let origin = location.origin;
        let enterpriseId = this.$store.getters.getEnterpriseId;
        return (
          origin + "/ecm/api/v2/enterprise/invitIndex?token=" + this.linktoken
        );
      }
    },
    departmentInfo: {
      get() {
        return this.$store.getters.getDepartmentInfo;
      }
    },
    getEdit: {
      get() {
        return this.$store.getters.getEdit;
      }
    }
  },
  components: {
    Tree,
    Menu
  },
  created() {
    this.init();
  },
  methods: {
    init() {
      Promise.all([this.getDepart(), this.getLists()])
        .then(res => {
          this.formatTable();
        })
        .catch(err => {
          console.error(err);
        });
    },
    onCopy(e) {
      this.$message({
        type: "success",
        message: "复制成功"
      });
    },
    onError() {},
    addmember() {
      this.$store.dispatch("getLink", { self: this }).then(res => {
        this.linktoken = res;
        this.copyLink = true;
      });
    },
    selectLink() {
      document.getElementById("link").select();
    },
    formatTable() {
      let treeData = this.deleteDepartmentList;
      let staffList = this.staffList;
      staffList.map(item => {
        let targetId = [];
        if (
          item.privileges &&
          Object.prototype.toString.call(item.privileges) === "[object Array]"
        ) {
          item.privileges.forEach(cur => {
            targetId.push(cur.targetId);
          });
        }
        item.showDepartmentName = [];
        item.showPrivleges = [];
        if (
          item.depts &&
          Object.prototype.toString.call(item.depts) === "[object Array]"
        ) {
          item.depts.forEach(cur => {
            if (cur.role === 0) cur.characterName = "普通员工";
            if (cur.role == 2) cur.characterName = "知识专员";
            if (cur.role == 3) cur.characterName = "部门主管";

            cur.old = true;
            cur.TYPE = "d";
            cur.id = cur.departmentId;
            cur.typeName = cur.name;
            item.showDepartmentName.push(cur.name + "-" + cur.characterName);

            item.departmentName = item.showDepartmentName.join(" 、");
          });
        }
        if (
          item.privileges &&
          Object.prototype.toString.call(item.privileges) === "[object Array]"
        ) {
          item.privileges.map(cur => {
            if (cur.role == 2) cur.characterName = "知识专员";
            cur.old = true;
            cur.TYPE = "a";
            cur.typeName = cur.targetName;
            item.showPrivleges.push(cur.typeName + "-" + cur.characterName);

            item.roleNames = item.showPrivleges.join(" 、");
          });
        }
      });
      this.personalList = staffList;
    },
    cancalEdit() {
      this.copyRowData.Time = +new Date();
      this.rowdata = JSON.parse(JSON.stringify(this.copyRowData));
    },
    sureEdit(data) {
      this.init();
      this.rowdata = JSON.parse(JSON.stringify(data));
    },
    cancalAddDepart() {
      this.addDepartDialog = false;
      this.addName = "";
    },
    sureAddDepart() {
      let name = this.addName;
      let id = this.searchData.active;
      if (name) {
        this.$store
          .dispatch("addDepart", {
            self: this,
            obj: { parentId: id, name: name }
          })
          .then(res => {
            this.$message("添加部门成功");
            this.getDepart();
          })
          .catch(err => {
            console.error(err);
          });
        this.addDepartDialog = false;
        this.addName = "";
      }
    },
    addDepart(data) {
      //添加部门
      let id = "";
      if (data.ID) this.searchData.active = data.ID;
      id = this.searchData.active;
      if (id) {
        this.addDepartDialog = true;
      } else {
        this.$message("请先点击选择左侧要编辑的部门");
      }
    },
    getDepart() {
      //获取部门事件
      return new Promise((resolve, reject) => {
        this.$store
          .dispatch("getDepart", {
            self: this,
            obj: {}
          })
          .then(res => {
            this.deleteDepartmentList = JSON.parse(JSON.stringify(res));
            let departmentList = utils.parseTree(res);
            this.treeData = departmentList;
            departmentList = utils.addCompany(departmentList, {
              label: this.$store.state.user.userInfo.enterpriseName || ""
            });
            this.treelists = departmentList;
            resolve();
          })
          .catch(err => {
            reject(err);
            this.treeLoading = false;
          })
          .finally(() => {
            this.treeLoading = false;
          });
      });
    },
    rowclick(e) {
      //点击表格行事件
      this.rowdata = e;
      this.copyRowData = e;
      this.$router.push({
        path: `/DepartmentAndStaffManagement/${e.id}/Member`
      });
    },
    handlesearch() {
      //搜索事件
      this.searchTypes = !!this.search;
      if (this.searchTypes) {
        let P = [];
        P.push(searchDepart.call(this));
        P.push(searchMembers.call(this, -1));
        Promise.all(P)
          .then(res => {
            let searchDepart = utils.parseTree(res[0]);
            this.searchData.department = searchDepart;
            this.searchData.members = utils.fromatTree(res[1].content);
            if (this.searchData.members.length > 0) {
              let zmem = res[1].content[0];
              this.searchItem(zmem, "m");
            }
            if (
              this.searchData.members.length === 0 &&
              this.searchData.department.length > 0
            ) {
              let zder = this.searchData.department[0];
              this.searchItem(zder, "d");
            }
            if (
              this.searchData.members.length === 0 &&
              this.searchData.department === 0
            ) {
              this.searchTypes = false;
            }
          })
          .catch(err => {
            this.searchData.members = [];
            this.searchData.department = [];
          });
      } else {
        this.searchData.members = [];
        this.searchData.department = [];
        this.departmentName = "全部成员";
        this.getLists().then(res => {
          this.formatTable();
        });
      }
    },
    searchItem(rowdata, type) {
      //搜索选中
      try {
        if (type === "m") {
          let id = rowdata.id;
          this.searchData.active = id;
          this.rowdata = rowdata;
          this.$router.push(`/DepartmentAndStaffManagement/${id}/Member`);
        } else if (type === "d") {
          let zder = rowdata;
          this.searchData.active = zder.ID;
          this.handleNodeClick(zder);
        } else {
          this.searchData.members = [];
          this.searchData.department = [];
        }
      } catch (err) {
        console.error(err);
      }
    },
    handlecurrent(item) {
      //树选中事件
      this.searchData.active = item.id;
      this.handleNodeClick(item);
    },
    getLists() {
      return new Promise((resolve, reject) => {
        this.$store
          .dispatch("getAllpeople", {
            self: this,
            obj: {
              page: this.current,
              pagesize: 15
            }
          })
          .then(res => {
            res.content.map(item => {
              item.phoneNumber = item.mobile ? item.mobile : "---";
            });
            this.total = res.totalElements;
            this.staffList = res.content;
            this.tableLoading = false;
            resolve();
          })
          .catch(err => {
            this.tableLoading = false;
            reject(err);
          });
      });
    },
    handleNodeClick(data) {
      //树选中请求事件
      this.tableLoading = true;
      if (data.iscom) {
        this.departmentName = "全部成员";
        this.searchData.active = "";
        this.getLists()
          .then(res => {
            this.formatTable();
          })
          .catch(err => {
            console.error("err", err);
          });
      } else {
        this.departmentName = data.label;
        this.getCurrentDepartment(data.id || data.userId);
      }
      this.$router.push("/DepartmentAndStaffManagement");
    },
    getCurrentDepartment(id) {
      //部门节点事件
      this.searchData.active = id;
      this.$store
        .dispatch("getDepartEmployees", {
          self: this,
          obj: { deptId: id, page: 1, pagesize: 15 }
        })
        .then(res => {
          this.searchData.active = id;
          this.total = res.totalElements;
          this.staffList = res.content;
          this.formatTable();
          this.tableLoading = false;
        })
        .catch(err => {});
    },
    handleCurrentChange(pages) {
      //切换页面
      this.current = pages;
      this.tableLoading = true;
      this.getLists()
        .then(res => {
          this.formatTable();
        })
        .catch(err => {})
        .finally(() => {
          this.tableLoading = false;
        });
    },
    deleteDepartment(item) {
      let id = item.id || item.ID;
      if (item.subEmployees > 0) {
        this.$message({
          showClose: true,
          message: item.label + "部门下有成员，不能删除",
          type: "warning",
          duration: 0
        });
        return;
      }
      this.$confirm("是否删除" + item.label, "提示", {
        confirmButtonText: "确定",
        type: "warning",
        center: true
      })
        .then(res => {
          this.$store
            .dispatch("deleteDepartment", {
              self: this,
              obj: { deptId: id }
            })
            .then(res => {
              this.getDepart();
            })
            .catch(err => {
              console.error(err);
            });
        })
        .catch(err => {});
    },
    butModifyName(boo) {
      if (boo) {
        let name = this.modifyName;
        let id = this.searchData.active;
        this.$store
          .dispatch("modDepartmentName", {
            self: this,
            obj: { deptId: id, name: name }
          })
          .then(res => {
            this.dataItem = {};
            this.modifyName = "";
            this.changeNameDialog = false;
            this.getDepart();
          })
          .catch(err => {
            console.error(err);
          });
      } else {
        this.dataItem = {};
        this.modifyName = "";
        this.changeNameDialog = false;
      }
    },
    modDepartmentName(item) {
      let id = "";
      if (item.ID) this.searchData.active = item.ID;
      id = Number(this.searchData.active);
      if (Boolean(id)) {
        this.changeNameDialog = true;
        this.NameDialogTip = item.label || "";
      } else {
        this.$message({
          showClose: true,
          message: "请先选择部门再进行此操作",
          type: "warning"
        });
      }
    },
    departure(id) {
      this.$store
        .dispatch("setDeparture", {
          self: this,
          obj: { employeeid: id }
        })
        .then(res => {
          this.$message({
            message: "设置此员工离职成功",
            type: "success"
          });
        })
        .catch(err => {
          console.error(err);
        });
    }
  }
};
</script>

<style lang='less' scoped>
.paginationblock {
  position: absolute;
  left: 50% !important;
  bottom: 10px;
}
.tree-block {
  background: #fafafa;
  overflow: auto;
  position: absolute;
  top: 0;
  bottom: 0;
  left: 0;
  right: 75%;
}
.table-block {
  left: 25% !important;
}
.search-lists {
  // padding: 0 20px;
  width: 100%;
  float: left;
  .is-active {
    background-color: #eee;
  }
  dl {
    width: 100%;
    float: left;
    margin-bottom: 6px;
    > dt,
    > dd {
      text-indent: 20px;
    }
    > dt {
      color: #999;
    }
    > dd {
      padding: 5px 0;
      width: 100%;
      color: #333;
      &:hover {
        background-color: #eee;
      }
    }
  }
}
.link {
  width: 100%;
  float: left;
  border: 1px solid #eaeaea;
  border-radius: 4px;
  background: #fff;
  height: 30px;
  line-height: 30px;
  margin-bottom: 20px;
  overflow: hidden;
  > input {
    float: left;
    line-height: 30px;
    // padding: 0 10px;
    // background: red;
    width: 85%;
    color: #333;
    border: 0;
    background: #fff;
    height: 30px;
    // text-indent: 10px;
    overflow: hidden;
    box-sizing: border-box;
    padding: 0 10px;
  }

  > div {
    height: 30px;
    line-height: 30px;
    float: right;
    width: 15%;
    max-width: 15%;
    text-align: center;
    background: #ea5036;
    color: #fff;
    cursor: pointer;
  }
}
</style>

<style lang="less">
.none {
  background-repeat: no-repeat;
  width: 100%;
  height: 100%;
  background-position: center;
  text-align: center;
}
.personal {
  background-image: url("../../assets/images/nopersonal.png");
}
.copylink {
  .el-dialog__footer {
    text-align: left !important;
    > span {
      color: #999999;
      font-size: 14px;
    }
  }
}
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

      > p {
        height: 32px;
        line-height: 32px;
      }
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

