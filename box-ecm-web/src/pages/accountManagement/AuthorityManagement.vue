<template>
  <div class="autho-main">
    <el-row style="width:100%;height:100%;">
      <el-col class="el-tabs" :span="24" style="height:100%;">
        <div class="autho-tab" style="height:100%;">
          <div class="autho-header">
            <div class="left-title">应用角色管理</div>
            <div class="autho-tabs">
              <span @click="changetabs('0')" id="tab-first" :class="isActive==='0'?'is-active':''">知识专员</span>
              <span @click="changetabs('1')" id="tab-second" :class="isActive==='1'?'is-active':''">系统管理员</span>
            </div>
            <div class="right-title" v-if="getEdit">
              <!-- <el-button @click="edit">编辑</el-button> -->
              <el-button @click="addNew">新增</el-button>
              <el-button @click="del">删除</el-button>
            </div>
          </div>
          <div class="autho-table">
            <transition name="fade" mode="out-in">
              <div v-if="isActive=='0'" v-loading="loadingTbas1">
                <!-- <Table class="tab" :tabledataHeader='knowledgeTable.head' :Tabledata='knowledgeTable.body' :titlename="''" :checkbox="true" :title="true" :people="false" :showbtn="false" @handleSelectionChange="handleSelectionChange" /> -->
                <el-table ref="multipleTable" class="Table" @current-change="handleSelect()" highlight-current-row :data="knowledgeTable.body" stripe border @selection-change="handleSelectionChange">
                  <!-- <el-table-column type="selection" width="55">
                  </el-table-column> -->
                  <el-table-column type="selection" width="55">
                  </el-table-column>
                  <template v-for="(item,index) in knowledgeTable.head">
                    <el-table-column v-if="item.prop=='targetName'" :prop="item.prop" :label="item.label" :key="index">
                      <template slot-scope="scope">
                        <div>
                          <span v-if="scope.row.targetName.length>0" v-for="(cur,idx) in scope.row.targetName" :key="idx">
                            {{cur}}
                            <template v-if="idx+1!==scope.row.targetName.length">
                              <span>&nbsp;/&nbsp;</span>
                            </template>
                          </span>
                          <span v-if="scope.row.targetName.length===0">---</span>
                        </div>
                      </template>
                    </el-table-column>
                    <el-table-column v-if="item.prop!=='targetName'" :prop="item.prop" :label="item.label" :key="index ">
                    </el-table-column>
                  </template>
                  <el-table-column fixed="right" label="操作" width="100">
                    <template slot-scope="scope">
                      <span @click="editItem(scope.row) ">编辑</span>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </transition>
            <transition name="fade" mode="out-in">
              <div v-if="isActive=='1' ">
                <Table ref="Atable " :tabledataHeader='adminTable.head' :Tabledata='adminTable.body' class="tab " :checkbox="true " :titlename=" '' " :title="true " :people="false " :showbtn="false " @handleSelectionChange="handleSelectionChange " />
                <div class="paginationblock " v-if="adminTable.total>0">
                  <el-pagination @current-change="handleCurrentChange" :current-page="adminTable.current" :page-size="15" layout="total, prev, pager, next" :total=adminTable.total>
                  </el-pagination>
                </div>
              </div>
            </transition>
          </div>
        </div>

      </el-col>
    </el-row>
    <el-dialog :title='"新增" + title' width="700px" :visible.sync="dialogKnowledge">
      <div class="auto-Choose">
        <div class="auto-top">
          <div class="auto-Columns">
            <div style="width:100%;">
              <div style="float:left;width:100%;">
                <div>
                  <span class="choosetitle">请选择管理成员</span>
                </div>
                <div class="auto-checkpe">
                  <div class="block" style="width:100%">
                    <el-cascader placeholder='全体成员' :props="{value:'id',children:'children'}" change-on-select expand-trigger="click" :options="quickSearchData" v-model="quickSearchIds" @change="handleQuickSearch">
                    </el-cascader>
                  </div>
                </div>
                <div class="search">
                  <el-input v-model="searchKeywords" placeholder="请输入要搜索的内容" prefix-icon="el-icon-search" clearable @change="handleSearchKeyWords"></el-input>
                </div>
              </div>
              <div style="float:left;width:100%;" v-loading="quickSearchIdData.loading">
                <div class="auto-lists" v-show="quickSearchIdDataList.length>0">
                  <div v-for="(item,index) in quickSearchIdDataList" :key="index" @click="selectCurrent(index)">
                    <span>{{item.alias}}</span>
                    <span v-if="item.checked" style="float:right;">
                      <i class="el-icon-check"></i>
                    </span>
                  </div>
                </div>
                <div class="lists" v-show="quickSearchIdDataList.length===0">该部门下没有成员</div>
              </div>
            </div>
          </div>
          <div class="auto-Columns">
            <div>
              <el-checkbox style="margin-left:25px;" v-model="library" @change="handleLibrary">企业文库</el-checkbox>
            </div>
            <!-- <span class="choosetitle">选择管理的部门</span> -->
            <div class="chooselist">
              <el-tree ref="tree" v-loading="TreeLoading" class="tree" empty-text="没有数据" :data="treeData" default-expand-all node-key="id" highlight-current :expand-on-click-node='false' @node-click="handleDepartemt">
                <div style="float:right;width:100%;" class="custom-tree-node" slot-scope="{ node, data }">
                  <span>{{node.label}}</span>
                  <span style="float:right;" v-if="data.checked" class="menu">
                    <i class="el-icon-check"></i>
                  </span>
                </div>
              </el-tree>
            </div>
          </div>
        </div>
        <p class="aut-tip">
          管理成员仅能选择一人，管理部门可多选
        </p>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="cancal">取 消</el-button>
        <el-button @click="editsure">确 定</el-button>
      </span>
    </el-dialog>
    <el-dialog :title='"新增" + title ' width="350px" :visible.sync="dialogAdmin">
      <div class="auto-Choose" style="padding:0 10px;height:380px;">
        <div class="auto-Columns" style="border:0;">
          <div style="width:100%;">
            <div style="float:left;width:100%;">
              <!-- <div>
                <span class="auto-choosetitle">请选择管理员</span>
              </div> -->
              <div class="auto-checkpe">
                <div class="block" style="width:100%">
                  <el-cascader placeholder='全体成员' :props="{value:'id',children:'children'}" change-on-select expand-trigger="click" :options="quickSearchData" v-model="quickSearchIds" @change="handleQuickSearch">
                  </el-cascader>
                </div>
              </div>
              <div class="search">
                <el-input v-model="searchKeywords" placeholder="请输入要搜索的内容" prefix-icon="el-icon-search" clearable @change="handleSearchKeyWords"></el-input>
              </div>
            </div>
            <div style="float:left;width:100%;" v-loading="quickSearchIdData.loading">
              <div class="auto-lists" v-show="quickSearchIdDataList.length>0">
                <div v-for="(item,index) in quickSearchIdDataList" :key="index" @click="selectCurrent(index)">
                  <span>{{item.alias}}</span>
                  <span v-if="item.checked" style="float:right;">
                    <i class="el-icon-check"></i>
                  </span>
                </div>
              </div>
              <div class="auto-lists" v-show="quickSearchIdDataList.length===0">该部门下没有成员</div>
            </div>
          </div>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="cancal">取 消</el-button>
        <el-button @click="editsure">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import Table from "@/components/Table.vue";
import utils from "@/common/js/util.js";
let that = null;
export default {
  data() {
    return {
      dialogKnowledge: false,
      dialogAdmin: false,
      isActive: "0",
      TreeLoading: true,
      searchKeywords: "",
      quickSearchIds: [],
      quickSearchData: [],
      quickSearchActiveId: null,
      selectCurrentIds: [],
      selectTreeIds: [],
      treeData: [],
      copyTreeData: [],
      quickSearchIdData: {
        loding: true,
        id: null,
        all: [],
        copyAlldata: []
      },
      quickSearchIdDataList: [],
      knowledgeTable: {
        body: [],
        selectRow: [],
        head: [
          // {
          //   prop: "userName",
          //   label: "用户名"
          // },
          {
            prop: "userName",
            label: "姓名"
          },
          {
            prop: "targetName",
            label: "管理部门"
          },
          {
            prop: "managementSpace",
            label: "管理空间"
          },
          {
            prop: "phone",
            label: "手机号"
          }
        ],
        current: 0,
        total: 0
      },
      adminTable: {
        body: [],
        selectRow: [],
        head: [
          {
            prop: "name",
            label: "用户名"
          },
          {
            prop: "loginName",
            label: "姓名"
          }
        ],
        current: 0,
        total: 0
      },
      newAuthodata: {},
      teamspacesId: "",
      library: false,
      editRow: {
        islibrary: false,
        data: null
      },
      loadingTbas1: true,
      loadingTbas2: true,
      delDepartmentIds: []
    };
  },
  computed: {
    title: {
      get() {
        let isActive = this.isActive;
        let title = "";
        if (isActive == 0) title = "知识专员";
        if (isActive == 1) title = "系统管理员";
        return title;
      }
    },
    editShow: {
      get() {
        return !(
          this.knowledgeTable.selectRow.length > 0 ||
          this.adminTable.selectRow.length > 0
        );
      }
    },
    getEdit: {
      get() {
        return this.$store.getters.getEdit;
      }
    }
  },
  watch: {
    quickSearchActiveId: function(n, o) {
      o = n;
      let quickSearchIdData = that.quickSearchIdData;
      if (n === null) {
        that.quickSearchIdDataList = [];
      } else {
        that.quickSearchIdDataList = [];
        if (quickSearchIdData[n].hasOwnProperty("data")) {
          that.quickSearchIdDataList = quickSearchIdData[n].data;
        } else {
          that.quickSearchIdDataList = quickSearchIdData[n];
        }
      }
    }
  },
  components: {
    Table
  },
  beforeCreate() {
    that = this;
  },
  created() {
    this.getknowledge();
    this.getAllpersonal();
    this.getAllDepart();
    this.getAdminList();
    this.getTeamspaces();
    // await
  },
  methods: {
    handleLibrary(e) {
      this.library = e;
    },
    handleSelect(row) {
      // this.$refs.multipleTable.toggleRowSelection(row);
    },
    async getTeamspaces() {
      // console.log(this.teamspacesId);
      this.$store
        .dispatch("getTeamspaces", { self: this })
        .then(res => {
          this.teamspacesId = res.teamSpaces[0].id;
          // console.log(this.teamspacesId);
        })
        .catch(err => {
          console.error(err);
        });
    },
    async getAllDepart() {
      this.$store
        .dispatch("getDepart", {
          self: this,
          obj: {}
        })
        .then(res => {
          let departmentList = utils.parseTree(res);
          this.quickSearchData = utils.delChildren(departmentList);
          let treeData = utils.addCompany(departmentList, {
            label: this.$store.state.user.userInfo.enterpriseName || ""
          });
          this.TreeLoading = false;
          this.treeData = treeData;
          this.copyTreeData = JSON.parse(JSON.stringify(treeData));
        })
        .catch(err => {
          console.error(err);
        });
    },
    async getknowledge() {
      //获取知识专员
      let offset = this.knowledgeTable.current;
      offset = (offset - 1) * 15;
      if (offset < 10) {
        offset = 0;
      }
      this.$store
        .dispatch("getSpeciaList", {
          self: this,
          obj: {
            role: 2,
            limit: 15,
            offset: offset
          }
        })
        .then(res => {
          if (
            Object.prototype.toString.call(res) === "[object Array]" &&
            res.length > 0
          ) {
            this.knowledgeTable.body = [];
            this.knowledgeTable.total = res.totalElements || 0;
            res.forEach(item => {
              if (!item.hasOwnProperty("targetName")) {
                item.targetName = [];
              }
              if (!item.hasOwnProperty("managementSpace")) {
                item.managementSpace = "---";
              }
              if (!item.hasOwnProperty("phone")) {
                item.phone = "---";
              }
              item.privileges.forEach(cur => {
                if (cur.type == 1) {
                  item.targetName.push(cur.targetName);
                }
                if (cur.type == 2) {
                  item.managementSpace = cur.targetName;
                  item.libraryId = cur.id;
                  item.islibrary = true;
                }
              });
              if (!item.targetName) {
                item.targetName = "---";
              }
              this.knowledgeTable.body.push(item);
            });
          }
          this.loadingTbas1 = false;
        })
        .catch(() => {
          this.loadingTbas1 = false;
        });
    },
    async getAllpersonal() {
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
            if (this.selectCurrentIds.indexOf(item.id) > -1) {
              item.checked = true;
            }
          });
          this.quickSearchIdData.all = res.content;
          this.quickSearchIdData.copyAlldata = JSON.parse(
            JSON.stringify(res.content)
          );

          this.$set(this.quickSearchIdData, "id", "all");
          this.quickSearchActiveId = "all";
          this.loadingTbas2 = false;
        })
        .catch(err => {
          this.loadingTbas2 = false;
          console.error("err", err);
        });
    },
    async getAdminList() {
      let enterpriseId = "";
      enterpriseId = this.$store.getters.getUserinfo.enterpriseId;
      return new Promise((resolve, reject) => {
        this.$store
          .dispatch("getAdminList", { self: this, enterpriseId })
          .then(res => {
            if (
              Object.prototype.toString.call(res) === "[object Array]" &&
              res.length > 0
            ) {
              this.adminTable.total = 0;
              this.adminTable.body = [];
              res.forEach(item => {
                !item.phone && (item.phone = "---");
                this.adminTable.body.push(item);
              });
            }
            resolve(res);
          })
          .catch(err => reject(err));
      });
    },
    handleQuickSearch() {
      let id = this.quickSearchIds.slice(-1).join();
      let quickSearchIdData = this.quickSearchIdData;
      if (
        quickSearchIdData.hasOwnProperty(id) &&
        quickSearchIdData[id].isOk === true
      ) {
        this.quickSearchIdData[id].data.map(item => {
          item.checked = false;
          if (this.selectCurrentIds.indexOf(item.id) > -1) {
            item.checked = true;
          }
        });
        this.quickSearchIdData.id = id;
        this.quickSearchActiveId = id;
      } else {
        if (
          (quickSearchIdData.hasOwnProperty(id) &&
            quickSearchIdData[id].isOk !== "loading") ||
          !quickSearchIdData.hasOwnProperty(id)
        ) {
          this.quickSearchIdData[id] = {
            isOk: "loading",
            data: []
          };
          this.$store
            .dispatch("getDepartEmployees", {
              self: this,
              obj: { deptId: id, page: 1, pagesize: 1000 }
            })
            .then(res => {
              this.quickSearchIdData[id].isOk = true;
              res.content.map((item, index) => {
                if (this.selectCurrentIds.indexOf(item.id) > -1) {
                  item.checked = true;
                }
              });
              this.quickSearchIdData[id].data = res.content;
              this.quickSearchIdData.id = id;
              this.quickSearchActiveId = id;
            })
            .catch(err => {});
        } else {
          this.quickSearchIdData[id].data.map(item => {
            item.checked = false;
            if (this.selectCurrentIds.indexOf(item.id) > -1) {
              item.checked = true;
            }
          });
        }
      }
    },
    selectCurrent(e) {
      let isActive = this.isActive;
      if (isActive == "0") {
        this.quickSearchIdDataList.map(item => {
          item.checked = false;
        });
      }
      let item = this.quickSearchIdDataList[e];
      console.log("quickSearchIdDataList", this.quickSearchIdDataList);
      item.checked = !item.checked;
      let id = item.id;
      console.log("personal", id);
      let selectCurrentIds = this.selectCurrentIds;
      if (item.checked) {
        this.selectCurrentIds.push(id);
      } else {
        let index = selectCurrentIds.indexOf(id);
        this.selectCurrentIds.splice(index, 1);
      }
      // this.selectCurrentIds.push(id);
      this.quickSearchIdDataList.splice(e, 1, item);
    },
    handleSearchKeyWords() {
      let id = -1;
      let searchKeywords = this.searchKeywords;
      this.$set(this.quickSearchIdData, "loading", true);
      this.$store
        .dispatch("getDepartEmployees", {
          self: this,
          obj: { search: searchKeywords, deptId: id }
        })
        .then(res => {
          res.content.map((item, index) => {
            item.checked = false;
            if (this.selectCurrentIds.indexOf(item.id) > -1) {
              item.checked = true;
            }
          });
          this.quickSearchIdDataList = res.content;
          this.$set(this.quickSearchIdData, "loading", false);
        })
        .catch(err => {
          this.$set(this.quickSearchIdData, "loading", false);
        });
    },
    editsure() {
      let enterpriseId = this.$store.getters.getUserinfo.enterpriseId;
      let enterpriseUserId = "";
      let type = this.isActive;
      type = type.toString();
      console.log("run", type);
      let Plist = [];
      let selectTreeIds = null;
      let selectCurrentIds = this.selectCurrentIds;
      enterpriseUserId = this.selectCurrentIds;
      // console.log(
      //   "selectCurrentIds",
      //   selectCurrentIds,
      //   this.selectTreeIds,
      //   this.delDepartmentIds
      // );
      // return;
      switch (type) {
        case "0":
          let del = function(id) {
            return new Promise((resolve, rejecet) => {
              this.$store
                .dispatch("deleteManagement", {
                  self: this,
                  id: id
                })
                .then(res => {
                  resolve(res);
                })
                .catch(err => {
                  rejecet(err);
                });
            });
          };
          let add = function(params) {
            return new Promise((resolve, rejecet) => {
              this.$store
                .dispatch("addSpeciaLists", {
                  self: this,
                  obj: {
                    enterpriseUserId: params.enterpriseUserId,
                    enterpriseId: params.enterpriseId,
                    type: params.type,
                    targetId: parseInt(params.targetId),
                    role: 2
                  }
                })
                .then(res => {
                  resolve(res);
                })
                .catch(err => {
                  rejecet(err);
                });
            });
          };
          selectTreeIds = this.selectTreeIds;
          if (this.library) {
            if (!this.editRow.islibrary) {
              Plist.push(
                add.call(this, {
                  enterpriseUserId: selectCurrentIds[0],
                  enterpriseId,
                  type: 2,
                  targetId: parseInt(this.teamspacesId)
                })
              );
            }
          } else {
            if (this.editRow.islibrary) {
              if (this.editRow.islibrary) {
                Plist.push(del.call(this, this.editRow.data.libraryId));
              }
            }
          }
          selectTreeIds.forEach(item => {
            if (!item.old) {
              let p = add.call(this, {
                enterpriseUserId: selectCurrentIds[0],
                enterpriseId,
                type: 1,
                targetId: parseInt(item.targetId)
              });
              Plist.push(p);
            }
          });
          this.delDepartmentIds.forEach(item => {
            Plist.push(del.call(this, item.id));
          });
          break;
        case "1":
          let loginName = [];
          let allPersonal = this.quickSearchIdData.all;

          selectCurrentIds.forEach(item => {
            for (let i = 0, len = allPersonal.length; i < len; i++) {
              if (item == allPersonal[i].id) {
                loginName.push(allPersonal[i].name);
                break;
              }
            }
          });
          loginName.forEach(item => {
            let p = new Promise((resolve, reject) => {
              this.$store
                .dispatch("addAdmin", {
                  self: this,
                  enterpriseId: parseInt(enterpriseId),
                  loginName: item
                })
                .then(res => {
                  resolve(res);
                })
                .catch(err => {
                  reject(err);
                });
            });
            Plist.push(p);
          });

          break;
        default:
          break;
      }
      if (Plist.length > 0) {
        Promise.all(Plist)
          .then(res => {
            this.$message({
              type: "success",
              message: "新增成功"
            });
            this.dialogKnowledge = false;
            this.selectCurrentIds = [];
            this.selectTreeIds = [];
            this.delDepartmentIds = [];
            if (type == "0") {
              this.getknowledge();
            } else {
              this.getAdminList();
            }
          })
          .catch(err => {
            this.$message.error("新增失败,请点击重试");
          });
      }
    },
    handleDepartemt(e) {
      let treeData = this.treeData;
      let id = e.ID;
      // console.log("id", id);
      let cur = utils.checkedTreeitem(treeData, id);
      let selectTreeIds = this.selectTreeIds;

      let delDepartmentIds = this.delDepartmentIds;
      let idx = 0;
      for (let i = 0, len = selectTreeIds.length; i < len; i++) {
        let item = selectTreeIds[i];
        if (item.targetId === id) {
          if (item.old) {
            idx = i;
            let hasin = false;
            for (let j = 0, Dlen = delDepartmentIds.length; j < Dlen; j++) {
              let cur = delDepartmentIds[j];
              if (cur.targetId == id) {
                delDepartmentIds.splice(j, 1);
                hasin = true;
                break;
              }
            }
            if (!hasin) {
              delDepartmentIds.push({
                targetId: id,
                id: selectTreeIds[idx].id
              });
            }
          }
          break;
        }
      }
      if (cur.checked) {
        selectTreeIds.push({ targetId: id, old: false });
      } else {
        selectTreeIds.splice(idx, 1);
      }
    },
    edit() {
      if (this.isActive == "0") {
        if (this.knowledgeTable.selectRow.length === 0) {
          this.$message({
            message: "请选择需要编辑的人员",
            type: "warning"
          });
        } else if (this.knowledgeTable.selectRow.length >= 2) {
          this.$message({
            message: "只能选择一位员工进行编辑",
            type: "warning"
          });
        } else {
          this.dialogFormVisible = true;
        }
      }
      if (this.isActive == "1") {
        if (this.adminTable.selectRow.length === 0) {
          this.$message({
            message: "请选择需要编辑的人员",
            type: "warning"
          });
        } else if (this.adminTable.selectRow.length >= 2) {
          this.$message({
            message: "只能选择一位员工进行编辑",
            type: "warning"
          });
        } else {
          this.dialogFormVisible = true;
        }
      }
    },
    addNew() {
      let isActive = this.isActive;
      if (isActive == "0") {
        this.dialogKnowledge = true;
      } else if (isActive == "1") {
        this.dialogAdmin = true;
      }
      this.dialogFormVisible = true;
      let allPersonal = utils.MttoClone(this.quickSearchIdData.copyAlldata);
      this.quickSearchIdDataList = allPersonal;
      this.treeData = JSON.parse(JSON.stringify(this.copyTreeData));
      let treeData = this.treeData;
      // this.selectTreeIds.forEach(item => {
      //   utils.checkedTreeitem(treeData, item);
      // });
    },
    cancal() {
      let isActive = this.isActive;
      if (isActive == "0") {
        this.dialogKnowledge = false;
      } else if (isActive == "1") {
        this.dialogAdmin = false;
      }
      this.selectCurrentIds = [];
      this.selectTreeIds = [];
    },
    del() {
      let enterpriseId = this.$store.getters.getUserinfo.enterpriseId;
      let delKnowledge = function(id) {
        return new Promise((resolve, reject) => {
          this.$store
            .dispatch("deleteManagement", {
              self: this,
              id: id
            })
            .then(res => {
              this.knowledgeTable.current = 0;
              this.getknowledge();
              resolve(res);
            })
            .catch(err => {
              reject(err);
            });
        });
      };
      let delAdmin = function(enterpriseId, id) {
        return new Promise((resolve, reject) => {
          this.$store
            .dispatch("delAdmin", {
              self: this,
              enterpriseId: enterpriseId,
              id: id
            })
            .then(res => {
              this.adminTable.current = 0;
              resolve(res);
            })
            .catch(err => {
              reject(err);
            });
        });
      };
      let DELP = [];
      if (this.isActive == "0") {
        if (this.knowledgeTable.selectRow.length === 0) {
          this.$message({
            message: "没有选择要删除的人员",
            type: "warning"
          });
        }
        this.knowledgeTable.selectRow.forEach(item => {
          DELP.push(delKnowledge.call(this, item.id));
        });
        Promise.all(DELP)
          .then(res => {
            this.$message({
              type: "success",
              message: "删除成功"
            });
          })
          .catch(err => {
            this.$message({
              type: "error",
              message: "删除失败"
            });
          })
          .finally(() => {
            DELP = [];
            this.getknowledge();
          });
      }
      if (this.isActive == "1") {
        if (this.adminTable.selectRow.length === 0) {
          this.$message({
            message: "没有选择要删除的人员",
            type: "warning"
          });
        }
        // console.log(this.adminTable.selectRow, this.$store.getters.getUserinfo);
        // return;
        let ids = [];
        this.adminTable.selectRow.forEach(item => {
          // console.log(item.id, this.$store.getters.getUserinfo);
          ids.push(item.id);
        });
        let userId = this.$store.getters.getUserinfo.enterpriseId;
        if (ids.indexOf(userId) > -1) {
          this.$message({
            message: "不能删除自己",
            type: "warning"
          });
          return;
        }
        ids.forEach(id => {
          DELP.push(delAdmin.call(this, enterpriseId, id));
        });
        if (DELP.length > 0) {
          Promise.all(DELP)
            .then(res => {
              this.$message({
                type: "success",
                message: "删除成功"
              });
            })
            .catch(err => {
              this.$message({
                type: "error",
                message: "删除失败"
              });
            })
            .finally(() => {
              DELP = [];
              this.getAdminList();
            });
        }
      }
    },
    handleSelectionChange(e) {
      if (this.isActive == "0") {
        this.knowledgeTable.selectRow = e;
      }
      if (this.isActive == "1") {
        this.adminTable.selectRow = e;
      }
    },
    handleCurrentChange(current) {
      let type = this.isActive;
      if (type == "0") {
        this.knowledgeTable.current = current;
        this.getknowledge();
      }
      if (type == "1") {
        // adimin
        this.adminTable.current = current;
        this.getAdminList();
      }
    },
    changetabs(tabs) {
      this.isActive = tabs;
    },
    editItem(row) {
      console.log("1", row);
      this.editRow.data = row;
      this.dialogKnowledge = true;
      let currentPersonal = null;
      this.selectTreeIds = [];
      // this.selectTreeIds.push(row.enterpriseUserId);
      this.selectCurrentIds.push(row.enterpriseUserId);
      let allPersonal = utils.MttoClone(this.quickSearchIdData.copyAlldata);
      allPersonal.map(item => {
        item.checked = false;
        if (item.id == row.enterpriseUserId) {
          item.checked = true;
          currentPersonal = [item];
        }
      });
      this.quickSearchIdDataList = currentPersonal;
      function checkedTreeitem(treeData, targetId, id) {
        // let id = targetId;
        let cur = utils.checkedTreeitem(treeData, targetId);
        let selectTreeIds = this.selectTreeIds;
        let hasin = false;
        for (let i = 0, len = selectTreeIds.length; i < len; i++) {
          let item = selectTreeIds[i];
          if (item.targetId === targetId) {
            console.log("222222222222", item);
            this.selectTreeIds.splice(i, 1);
            hasin = true;
            break;
          }
        }
        if (!hasin) {
          this.selectTreeIds.push({
            targetId,
            old: true,
            id: id
          });
        }
        // let index = selectTreeIds.indexOf(targetId);
        // if (index > -1) {
        //   this.selectTreeIds.splice(index, 1);
        // } else {
        //   if (targetId) {
        //     this.selectTreeIds.push(targetId);
        //   }
        // }
      }
      let treeData = JSON.parse(JSON.stringify(this.copyTreeData));
      row.privileges.forEach(item => {
        if (item.type === 1) {
          checkedTreeitem.call(
            this,
            treeData,
            item.targetId.toString(),
            item.id
          );
        }
      });
      if (row.islibrary) {
        this.editRow.islibrary = true;
        this.library = true;
      } else {
        this.editRow.islibrary = false;
        this.library = false;
      }
      this.treeData = treeData;
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
// #tab-first {
//   border-right: 0;
//   // border-radius: 5px 0 0 5px;
//   border-radius: 5px;
// }
// #tab-second {
//   border-radius: 0 5px 5px 0;
//   border-left: 0;
// }
.tab {
  position: absolute;
  top: 0;
  left: 0;
}
.autho-tabs {
  width: 300px;
  margin: auto;
  height: 35px;
  line-height: 35px;
  > span {
    float: left;
    border-radius: 5px 0 0 5px;
    // border: 1px #eaeaea solid;
    cursor: pointer;
    border-style: solid;
    border-color: #eaeaea;
    border-width: 1px 0 1px 1px;
    margin: 0;
    padding: 0 10px;
    background: rgba(255, 255, 255, 1);
    &:nth-last-of-type(1) {
      border-width: 1px 1px 1px 0;
      border-radius: 0 5px 5px 0;
    }
    &:hover {
      color: inherit;
      background-color: #f5f5f5;
    }
  }
  .is-active {
    background: rgba(107, 107, 107, 1);
    color: rgba(255, 255, 255, 1) !important;
  }
  .is-active:hover {
    color: rgba(255, 255, 255, 1) !important;
    background: #6b6b6b !important;
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
<style lang="less">
.autho-main {
  .menu {
    float: right;
  }
  .bl1 {
    border-left: 1px solid red;
  }
  .el-dialog__body {
    padding: 0;
  }
  .auto-Choose {
    height: 410px;
    .aut-tip {
      text-indent: 20px;
      color: #999999;
      height: 30px;
      line-height: 30px;
    }
    .auto-top {
      height: 380px;
      width: 100%;
      float: left;
      border-bottom: 1px solid #eaeaea;
    }
    .auto-top > div {
      width: 50%;
      float: left;
      padding: 0 20px 0 20px;
      box-sizing: border-box;
    }
    .auto-choosetree {
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
    .auto-Columns {
      height: 380px;
      &:nth-of-type(2) {
        border-left: 1px solid #eaeaea;
        overflow-y: auto;
      }
      .choosetitle {
        color: #999999;
      }
      .auto-chooselist {
        width: 100%;
        height: 356px;
        overflow-y: auto;
        p {
          cursor: pointer;
        }
        p + p {
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
  .auto-checkpe {
    width: 100px;
    float: left;
  }
  .search {
    width: 168px;
    float: right;
  }
  .auto-lists {
    padding: 5px 0;
    width: 100%;
    height: 320px;
    overflow-y: auto;
    float: left;
    box-sizing: border-box;
    > div {
      padding: 2px 0;
      cursor: pointer;
      &:hover {
        background-color: #eaeaee;
      }
    }
  }
  .el-input--small .el-input__inner {
    height: 28px;
    line-height: 26px;
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
}
</style>

