<template>
  <div style="height:100%;">
    <div class="headmasg">
      <div class="showstatus">成员详情</div>
      <div class="tool">
        <!-- getEdit -->
        <template v-if="getEdit">
          <el-button @click="saveEdit" v-show="editMember">保存</el-button>
          <el-button @click="editItem" v-show="!editMember">编辑</el-button>
          <el-button @click="cancalEdit" v-show="editMember">取消更改</el-button>
          <el-button @click="departureItem">离职</el-button>
        </template>
        <el-button @click="goback">返回</el-button>
      </div>
    </div>
    <div class="peopleinformation">
      <div class="headimg">
        <img :src="host + '/enterprise/userimage/getUserImage/' + personalInfo.cloudUserId" alt="">
      </div>
      <div class="information" v-if="false">
        <el-input v-model="input" placeholder="请输入姓名"></el-input>
        <el-input v-model="input" placeholder="请输入用户名"></el-input>
        <el-input v-model="input" placeholder="请输入手机号"></el-input>
        <el-input v-model="input" placeholder="请输入邮箱"></el-input>
      </div>
      <div class="userInfo" v-if="true">
        <!-- !edit -->
        <p>
          <label for="">姓名：</label>{{personalInfo.name}}</p>
        <p>
          <label for="">用户名：</label>{{personalInfo.alias}}</p>
        <p>
          <label for="">手机号：</label>{{personalInfo.mobile}}</p>
        <p>
          <label for="">邮箱：</label>{{personalInfo.mail}}</p>
      </div>
    </div>
    <div class="peopleinformation">
      <div class="information">
        <div class="title">部门：</div>
        <div class="infoItems" v-if="!edit">
          <div class="item" v-for="(item,idx) in personalInfo.depts" :key="idx">{{item.typeName}}-{{item.characterName}}</div>
        </div>
        <div class="infoItems taglist" v-if="edit">
          <el-tag :key="idx" v-for="(item,idx) in personalInfo.depts" closable :disable-transitions="true" @close="delApplication('d',idx)">
            {{item.typeName}}-{{item.characterName}}
          </el-tag>
        </div>
        <div class="but" v-if="edit">
          <el-button @click="setCurrentDepart">部门设置</el-button>
        </div>
      </div>
    </div>
    <div class="peopleinformation">
      <div class="information">
        <div class="title">应用权限：</div>
        <div class="infoItems">
          <div class="item" v-if="personalInfo.privileges.length>0">
            <span>知识专员--</span>
            <span v-for="(item,index) in personalInfo.privileges" :key="item.id">
              <span>{{item.typeName}}</span>
              <span v-if="personalInfo.privileges.length!==index+1">/</span>
            </span>
          </div>
          <!-- <div class="item" v-if="library">
            <span>企业文库管理员</span>
          </div> -->
        </div>
        <div class="but" v-if="edit">
          <el-button @click="setApplication">管理范围设置</el-button>
        </div>
      </div>
    </div>
    <div class="peopleinformation">
      <div style="margin-left:80px">
        <p>
          <label for="">人员状态：</label>
          <span v-if="personalInfo.status==0">正常</span>
          <span v-if="personalInfo.status==-2">离职</span>
          <span v-if="personalInfo.status==-3">离职（文档已移交）</span>
        </p>
      </div>
    </div>
    <el-dialog class="modify-dialog" width="700px" :title="'修改所在部门'" :visible="dialogFormData.show">
      <div class="SChoose">
        <div class="">
          <!-- <el-input v-model="searchMsg" @change="handlesearch" clearable placeholder="请输入要搜索的内容"></el-input> -->
          <!-- <span class="choosetitle">部门列表</span> -->
          <div v-if="dialogFormData.type==='a'">
            <el-checkbox style="margin-left:25px" v-model="library" @change="dialogtreeclick('library')">企业文库</el-checkbox>
          </div>
          <el-tree v-if="dialogFormData.show" ref="tree" class="tree" empty-text="没有数据" :data="dialogFormData.copyTree" default-expand-all node-key="id" :expand-on-click-node='false' @node-click="dialogtreeclick">
            <div class="custom-tree-node" slot-scope="{ node, data }">
              <span>{{node.label}}</span>
              <span v-if="data.checked" class="menu">
                <i class="el-icon-check"></i>
              </span>
            </div>
          </el-tree>
          <br>
          <!-- <div class="search-lists" v-if="searchMsg">
            <div class="search-item" v-for="(item,index) in searchList" :key="index">
              <div @click="handleSerachItem(index)">{{item.name}}
                <span v-if="item.checked" class="menu" style="margin-right: 20px ">
                  <i class="el-icon-check"></i>
                </span>
              </div>
            </div>
          </div> -->
        </div>
        <div class="">
          <span class="choosetitle">已选择部门及角色</span>
          <div class="chooselist" v-if="dialogFormData.show">
            <p v-for="(item, index) in dialogFormData.checkedList" :key="index" style="float:left;width:100%;">
              <!-- {{item.type}}
              {{item}} -->
              <!-- <template> -->
              <!-- v-if="item.type==1" -->
              <el-col :span="12">
                <span>{{item.label}}</span>
              </el-col>
              <el-col :span="10">
                <span class="el-dropdown-link" v-if="dialogFormData.type === 'a'">知识管理员</span>
                <el-dropdown trigger="click" @command="changeSelect" @visible-change="changeSelectIndex(index)" v-if="dialogFormData.type === 'd'">
                  <!--  @visible-change="showcommand(item,index)" -->
                  <span class="el-dropdown-link">
                    {{item.characterName}}
                    <i class="el-icon-arrow-down el-icon-right"></i>
                  </span>
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item command="0">普通成员</el-dropdown-item>
                    <el-dropdown-item command="3">部门主管</el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
              </el-col>
              <el-col :span="2">
                <span class="delete">
                  <i class="el-icon-close" @click="dialogtreeclick(item)"></i>
                </span>
              </el-col>
              <!-- </template> -->
              <!-- <template v-if="item.type==2">
                <el-col :span="12">
                  <span>{{item.targetName}}</span>
                </el-col>
                <el-col :span="12">
                  <span class="delete" style="float:right;margin-right:12px">
                    <i class="el-icon-close" @click="dialogtreeclick(item)"></i>
                  </span>
                </el-col>
              </template> -->
            </p>
            <!-- <p v-if="library">
              <el-col :span="12">
                <span>企业文库</span>
              </el-col>
              <el-col :span="12">
                <span class="delete" style="float:right;margin-right:12px">
                  <i class="el-icon-close" @click="dialogtreeclick('library')"></i>
                </span>
              </el-col>
            </p> -->
          </div>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="cancelModify">取 消</el-button>
        <el-button type="primary" @click="sureModify">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import set from "@/config/settings.js";
import utils from "@/common/js/util.js";

export default {
  data() {
    return {
      dialogFormVisible: false,
      input: "",
      library: false,
      host: set.gbs.servermode.host,
      editMember: false,
      visible: false,
      radio: false,
      showTreeData: [],
      checkedIds: [],
      onlySelectChange: false,
      checkedList: [],
      changedData: {
        selfDepetar: "",
        ischange: false,
        application: [],
        sureBefore: []
      },
      copyData: {},
      showed: false,
      dialogFormData: {
        show: false,
        copyTree: [],
        checkedList: [],
        selectIndex: 0,
        changedData: {
          sureBefore: [],
          sure: []
        },
        deleteData: [],
        type: ""
      },
      teamspaces: {
        id: "",
        hasin: false,
        library: null,
        libraryId: null
      }
    };
  },
  props: {
    treeData: {
      type: Array,
      validator: function() {
        return [];
      }
    },
    rowdata: {
      type: Object,
      validator: function() {
        return {};
      }
    },
    currentDepartshowTree: {
      type: Array,
      validator: function() {
        return [];
      }
    }
  },
  created() {
    this.copyData = JSON.parse(JSON.stringify(this.rowdata));
    this.getTeamspaces();
  },
  computed: {
    treelist: {
      get() {
        return this.$store.state.department.departmentInfoFromat;
      }
    },
    edit: {
      get() {
        return this.$route.params.id === "add" || this.editMember;
      }
    },
    personalInfo: {
      get() {
        if (!this.copyData.id) {
          this.goback();
        }
        return this.copyData;
      },
      set(val) {
        this.copyData = val;
      }
    },
    deleteDepartmentList: Array,
    getEdit: {
      get() {
        // return this.$store.getters.getEdit;
        return this.$store.getters.getEdit;
      }
    }
  },
  watch: {
    rowdata: function(n, o) {
      this.copyData = Object.assign({}, n);
    }
  },
  methods: {
    getTeamspaces() {
      this.$store
        .dispatch("getTeamspaces", { self: this })
        .then(res => {
          this.teamspaces.id = res.teamSpaces[0].id;
          // console.log(this.teamspacesId);
        })
        .catch(err => {
          console.error(err);
        });
    },
    setCurrentDepart() {
      //设置部门角色
      this.dialogFormData.show = true;
      this.dialogFormData.type = "d";
      let data = [];
      data = JSON.parse(JSON.stringify(this.treeData));
      data.forEach(item => {
        this.dialogFormData.copyTree.push(item);
      });
      let Clists = [];
      this.personalInfo;
      this.personalInfo.depts.forEach(item => {
        let Clist = utils.checkedTreeitem(data, item.departmentId);
        this.showCharacterName(item);
        item.label = Clist.obj.label;
        Clists.push(item);
      });

      this.dialogFormData.checkedList = Clists;
    },
    setApplication() {
      //设置应用管理权限
      this.dialogFormData.show = true;
      this.dialogFormData.type = "a";
      let data = [];
      data = JSON.parse(JSON.stringify(this.treeData));
      data.forEach(item => {
        this.dialogFormData.copyTree.push(item);
      });
      let Clists = [];
      let privileges = JSON.parse(JSON.stringify(this.personalInfo.privileges));
      privileges.forEach(item => {
        let Clist = utils.checkedTreeitem(data, item.targetId);
        this.showCharacterName(item);
        if (Clist.hasOwnProperty("obj")) {
          item.label = Clist.obj.label;
        }
        Clists.push(item);
        if (this.teamspaces.id == item.targetId) {
          // console.log("item", item);
          this.teamspaces.hasin = true;
          this.teamspaces.libraryId = item.id;
          this.library = true;
          item.characterName = item.targetName;
          item.label = item.targetName;
        }
      });

      this.dialogFormData.checkedList = Clists;
      // console.log("cli", Clists);
    },
    dialogtreeclick(e) {
      let library = this.library;
      let id = null;
      let check = null;
      let add = {};
      function show(obj) {
        if (obj.role == 0) obj.character = "普通员工";
        if (obj.role == 2) obj.character = "知识专员";
        if (obj.role == 3) obj.character = "部门主管";
      }
      if (e === "library") {
        id = this.teamspaces.id;
        add = {
          Type: "add",
          type: "2",
          old: this.teamspaces.hasin,
          id: id,
          departmentId: id,
          role: 2,
          character: "",
          character: "1",
          label: "企业文库",
          typeName: "企业文库",
          targetName: "企业文库",
          checked: true,
          targetId: id
        };
        if (this.teamspaces.hasin) {
          if (library) {
          } else {
          }
        }
      } else {
        id = e.targetId || e.id;
        check = utils.checkedTreeitem(this.dialogFormData.copyTree, id, false);
        add = {
          Type: "add",
          type: "add",
          old: null,
          id: id,
          departmentId: id,
          role: null,
          character: "",
          character: "1",
          label: e.label,
          typeName: e.label,
          checked: true,
          targetId: id
        };
      }
      if (this.dialogFormData.type === "a") {
        add.role = 2;
      }
      if (this.dialogFormData.type === "d") {
        add.role = 0;
      }

      let Sindex = 0;
      let sureBefore = this.dialogFormData.changedData.sureBefore;

      if (sureBefore.length > 0) {
        let hasin = false;
        for (let i = 0, len = sureBefore.length; i < len; i++) {
          let item = sureBefore[i];
          // console.log(111, id, item, item.targetId);
          // BUG 有可能是id的问题，有可能是重复数据的问题
          if (id == item.targetId) {
            hasin = true;
            index = i;
            show(add);
            if (e == "library") {
              if (library) {
                item.Type = "";
              } else {
                item.Type = "del";
              }
            } else {
              if (check) {
                item.Type = "add";
              } else {
                item.Type = "del";
              }
            }
            break;
          }
        }
        if (!hasin) {
          sureBefore.push(add);
        } else {
          sureBefore.splice(Sindex, 1);
        }
      } else {
        sureBefore.push(add);
      }
      // console.log("sureBefore", sureBefore);
      let privileges = this.personalInfo.privileges;
      let Hasin = false;

      for (let i = 0, len = privileges.length; i < len; i++) {
        if (id == privileges[i].targetId) {
          Hasin = true;
          add.privilegesId = privileges[i].id;
          break;
        }
      }
      if (Hasin) {
        add.old = true;
      } else {
        add.old = false;
      }

      show(add);
      // console.log("library", library);

      let hasin = false;
      let index = 0;
      this.dialogFormData.checkedList.map((cur, idx) => {
        // console.log(cur, add.id);
        if (cur.targetId == add.id) {
          hasin = true;
          // add.Type = "del";
          index = idx;
        }
      });
      if (!hasin) {
        this.showCharacterName(add);
        // console.log("1111111", add);
        this.dialogFormData.checkedList.push(add);
        // sureBefore.push(add);
      } else {
        this.dialogFormData.checkedList.splice(index, 1);
        // sureBefore.splice(Sindex, 1);
      }
    },
    cancelModify() {
      this.dialogFormData.show = false;
      this.dialogFormData.copyTree = [];
    },
    sureModify() {
      this.dialogFormData.show = false;
      let copyData = this.copyData;
      let checkedList = this.dialogFormData.checkedList;
      // console.log("checkedList", checkedList);
      if (this.dialogFormData.type === "a") {
        copyData.privileges = [];
        copyData.privileges = checkedList;
      }
      if (this.dialogFormData.type === "d") {
        copyData.depts = checkedList;
      }
      let sureBefore = this.dialogFormData.changedData.sureBefore;
      // console.log("sureBefore", sureBefore);
      this.dialogFormData.deleteData = sureBefore;
      this.dialogFormData.changedData.sureBefore = [];
      this.copyData = null;
      this.copyData = copyData;
      this.dialogFormData.copyTree = [];
    },
    changeSelect(e) {
      function show(obj) {
        if (obj.role == 0) obj.characterName = "普通员工";
        if (obj.role == 2) obj.characterName = "知识专员";
        if (obj.role == 3) obj.characterName = "部门主管";
      }
      let index = Number(this.dialogFormData.selectIndex);
      let item = this.dialogFormData.checkedList[index];
      item.role = Number(e);
      show(item);
      item.type = "modify";
      // console.log("1");
      this.$set(this.dialogFormData.checkedList, index, item);
    },
    changeSelectIndex(e) {
      this.dialogFormData.selectIndex = e;
    },
    goback() {
      this.tipEdit("放弃").then(() => {
        this.$router.push("/DepartmentAndStaffManagement");
      });
    },
    saveChange() {},
    showdialogFormVisible() {
      this.dialogFormVisible = true;
    },
    editItem() {
      this.editMember = true;
    },
    handleLibrary() {
      this.library;
    },
    departureItem() {
      //触发离职事件
      this.$confirm("是否离职该员工", "提示", {
        confirmButtonText: "确定",
        type: "warning",
        center: true
      })
        .then(() => {
          this.$emit("departure", this.$route.params.id);
        })
        .catch(() => {});
    },
    tipEdit(tip) {
      let checkedList = this.dialogFormData.checkedList;
      let deleteData = this.dialogFormData.deleteData;
      let len = checkedList.length + deleteData.length;
      return new Promise((resolve, rejecet) => {
        if (len > 0) {
          this.$confirm("还未保存数据，是否" + tip + "修改", "提示", {
            confirmButtonText: "确定",
            type: "error",
            center: true
          })
            .then(() => {
              this.dialogFormData.checkedList = [];
              this.dialogFormData.deleteData = [];
              resolve();
            })
            .catch(() => {
              rejecet();
            });
        } else {
          resolve();
        }
      });
    },
    cancalEdit() {
      this.tipEdit("取消").then(() => {
        this.editMember = false;
        this.copyData = JSON.parse(JSON.stringify(this.rowdata));
      });
    },
    saveEdit() {
      let handleType = this.dialogFormData.type;
      // if (handleType === "d") this.departmentOperation();
      // if (handleType === "a") this.applicationManagementOperations();
      this.delete();
    },
    delete() {
      let deleteData = this.dialogFormData.deleteData;
      let P = [];
      //部门
      console.log("deleteData", deleteData);
      let deleteDepartments = function(params) {
        return new Promise((resolve, rejecet) => {
          this.$store
            .dispatch("deleteDepartments", {
              self: this,
              obj: {
                departmentId: params.departmentId,
                enterpriseId: params.enterpriseId,
                enterpriseUserId: params.enterpriseUserId,
                role: params.role
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
      deleteData.forEach(item => {
        // BUG 删除所在的部门以及角色问题
        // P.push(
        //   deleteDepartments.call(this, {
        //     departmentId: item.departmentId,
        //     enterpriseUserId: enterpriseUserId,
        //     enterpriseId: enterpriseId,
        //     role: Number(item.role)
        //   })
        // );
        //  P.push(del.call(this, item.privilegesId));
      });
    },
    departmentOperation() {
      // 所在部门设置
      let checkedList = this.dialogFormData.checkedList;
      let enterpriseUserId = this.copyData.id;
      let enterpriseId = this.$store.getters.getUserinfo.enterpriseId;
      let P = [];
      //增加所在部门
      //departmentId 部门的id
      // enterpriseId 个人所在企业的id
      // enterpriseUserId 个人id
      // role 在部门中的角色
      let increaseDepartments = function(params) {
        return new Promise((resolve, rejecet) => {
          this.$store
            .dispatch("increaseDepartments", {
              self: this,
              obj: {
                departmentId: params.departmentId,
                enterpriseId: params.enterpriseId,
                enterpriseUserId: params.enterpriseUserId,
                role: params.role
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
      let modifyDepartments = function(params) {
        return new Promise((resolve, rejecet) => {
          this.$store
            .dispatch("modifyDepartments", {
              self: this,
              obj: {
                departmentId: params.departmentId,
                enterpriseId: params.enterpriseId,
                enterpriseUserId: params.enterpriseUserId,
                role: params.role
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

      checkedList.forEach(item => {
        if (item.old) {
          if (item.type === "modify") {
            P.push(
              modifyDepartments.call(this, {
                departmentId: item.departmentId,
                enterpriseUserId: enterpriseUserId,
                enterpriseId: enterpriseId,
                role: Number(item.role)
              })
            );
          }
        }
        if (!item.old) {
          P.push(
            increaseDepartments.call(this, {
              departmentId: item.departmentId,
              enterpriseUserId: enterpriseUserId,
              enterpriseId: enterpriseId,
              role: Number(item.role)
            })
          );
        }
      });

      if (P.length > 0) {
        Promise.all(P)
          .then(res => {
            this.editMember = false;
            this.dialogFormData.checkedList = [];
            this.dialogFormData.deleteData = [];
            this.$emit("sureEdit", this.copyData);
            this.$message({
              type: "success",
              message: "修改成功"
            });
          })
          .catch(err => {
            console.error("err", err);
            this.$message({
              type: "error",
              message: "修改失败,请重试"
            });
          });
      } else {
        this.$message({
          type: "info",
          message: "无修改内容"
        });
      }
    },
    applicationManagementOperations() {
      // 应用权限管理设置

      let checkedList = this.dialogFormData.checkedList;
      let deleteData = this.dialogFormData.deleteData;
      let enterpriseUserId = this.copyData.id;
      let enterpriseId = this.$store.getters.getUserinfo.enterpriseId;
      let P = [];
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
      let modify = function(params) {
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
      checkedList.forEach(item => {
        if (item.old) {
          if (item.type === "modify") {
          }
        }
        if (!item.old) {
          if (item.type == 2) {
            P.push(
              modify.call(this, {
                enterpriseUserId: enterpriseUserId,
                enterpriseId: enterpriseId,
                type: 2,
                targetId: parseInt(this.teamspaces.id),
                role: 2
              })
            );
          } else {
            P.push(
              modify.call(this, {
                enterpriseUserId: enterpriseUserId,
                enterpriseId: enterpriseId,
                type: 1,
                targetId: parseInt(item.departmentId),
                role: 2
              })
            );
          }
        }
      });
      deleteData.forEach(item => {
        // console.log("itemdel", item);
        if (item.old) {
          if (item.type == 2) {
            P.push(del.call(this, this.teamspaces.libraryId));
          } else {
            P.push(del.call(this, item.privilegesId));
          }
        }
      });
      if (P.length > 0) {
        Promise.all(P)
          .then(res => {
            this.editMember = false;
            this.dialogFormData.checkedList = [];
            this.dialogFormData.deleteData = [];
            this.$emit("sureEdit", this.copyData);
            this.$message({
              type: "success",
              message: "修改成功"
            });
          })
          .catch(err => {
            console.error("err", err);
            this.$message({
              type: "error",
              message: "修改失败,请重试"
            });
          });
      } else {
        this.$message({
          type: "info",
          message: "无修改内容"
        });
      }
    },
    showCharacterName(check) {
      //切换选择管理员类型时对应文字
      if (check) {
        switch (check.role) {
          case -1:
            check.characterName = "请选择";
            break;
          case 0:
            check.characterName = "普通成员";
            break;
          case 2:
            check.characterName = "知识专员";
            break;
          case 3:
            check.characterName = "部门主管";
            break;
          default:
            check.characterName = "请选择";
            break;
        }
      } else {
        return;
      }
    },
    handleChangeRole(data) {
      let item = this.checkedList[data.index];
      item.character = Number(data.type);
      this.showCharacterName(item);
      this.checkedList.splice(data.index, 1, item);
      this.changedData.sureBefore.push(data.data);
    },

    delApplication(type, index) {
      console.log(type, index);
      if (type === "a") {
        let item = this.personalInfo.privileges[index];
        if (item.old) {
          item.type = "del";
          this.dialogFormData.deleteData.push(item);
        }
        this.personalInfo.privileges.splice(index, 1);
      }
      if (type === "d") {
        let item = this.personalInfo.depts[index];
        if (item.old) {
          item.type = "del";
          this.dialogFormData.deleteData.push(item);
        }
        this.personalInfo.depts.splice(index, 1);
      }
    }
  }
};
</script>
<style lang="less">
.headmasg {
  height: 45px;
  line-height: 45px;
  margin: 0 25px;
  border-bottom: 1px solid #eaeaea;
  .showstatus {
    float: left;
    font-size: 16px;
    color: #333333;
  }
  .tool {
    float: right;
  }
}
.peopleinformation {
  overflow: hidden;
  margin: 40px 25px;
  padding-bottom: 30px;
  border-bottom: 1px solid #eaeaea;
  max-height: 300px;
  overflow-y: auto;
  .headimg {
    width: 70px;
    height: 70px;
    border-radius: 50%;
    overflow: hidden;
    float: left;
    img {
      width: 100%;
      height: 100%;
      display: block;
    }
  }
  .information {
    padding-left: 100px;
    padding-right: 150px;
    height: 200px;
    margin-left: 50px;
    overflow-y: auto;
  }
  .title {
    float: left;
    width: 80px;
    position: relative;
    left: -80px;
    height: 32px;
    line-height: 32px;
  }
  .infoItems {
    float: left;
    width: 100%;
    margin-left: -80px;
    > .item {
      // margin-top: -10px;
      height: 32px;
      line-height: 32px;
      margin-bottom: 10px;
    }
  }
  .but {
    float: right;
    width: 150px;
    margin-left: -150px;
    position: relative;
    left: 150px;
  }
  .information {
    .taglist {
      min-width: 200px;

      float: left;
      color: #333333;
      > span {
        min-width: 180px;
        display: block;
        clear: both;
        float: left;
        margin-bottom: 10px;
        background-color: #fff;
        border-color: #eaeaea;
        height: 32px;
        line-height: 32px;
        > i {
          margin-top: 8px;
          color: #333333;
          float: right;
          &:hover {
            background-color: #eaeaea;
          }
        }
      }
    }
    .el-input {
      width: 336px;
      display: block;
      input::-webkit-input-placeholder {
        color: #999999;
      }
      input::-moz-placeholder {
        /* Mozilla Firefox 19+ */
        color: #999999;
      }
      input:-moz-placeholder {
        /* Mozilla Firefox 4 to 18 */
        color: #999999;
      }
      input:-ms-input-placeholder {
        /* Internet Explorer 10-11 */
        color: #999999;
      }
    }
    .el-input + .el-input {
      margin-top: 20px;
    }
  }
}
.userInfo {
  float: left;

  p {
    height: 32px;
    line-height: 32px;
  }
  p + p {
    margin-top: 20px;
  }
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
  }
  .SChoose {
    overflow: hidden;
    height: 410px;
    border-bottom: 1px solid #eaeaea;
    padding: 5px 0 0 0;
    > div {
      height: 100%;
      width: 49%;
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
}
.custom-tree-node {
  .menu {
    float: right;
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



