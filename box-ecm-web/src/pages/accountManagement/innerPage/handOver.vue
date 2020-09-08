<template>
  <div class="autho-main">
    <el-row style="width:100%;height:100%;">
      <el-col class="el-tabs" :span="24">
        <div class="autho-tab">
          <div class="autho-header">
            <div class="left-title">{{name}}离职管理</div>
            <div class="autho-tabs">
              <span @click="changetabs('first')" id="tab-first" :class="isActive==='first'?'is-active':''" class="el-tabs__item">权限移交</span>
              <span @click="changetabs('second')" id="tab-second" :class="isActive==='second'?'is-active':''" class="el-tabs__item">文档移交</span>
            </div>
            <div class="right-title" v-show="tabs === 'first'">
              <el-button v-if="this.teamspacesFile === '1'" @click="edit">移交</el-button>
              <el-button @click="goback()">返回上一级</el-button>
            </div>
            <div class="right-title" v-show="tabs === 'second'">
              <!-- <el-button @click="play()">播放</el-button> -->
              <el-button @click="edit">移交</el-button>
              <el-button @click="nodesdelete()">删除</el-button>
              <el-button @click="goback()">返回上一级</el-button>
            </div>
          </div>
          <div class="autho-table">
            <transition name="fade" mode="out-in">
              <div v-if="isActive==='first'">
                <!-- <div> -->
                <Table v-loading="fristTable" ref="TABLE" v-if="this.teamspacesFile === ''" @rowclick="teamspacesrowclick" class="tab" :Tabledata="getTeamspacesList" :tabledataHeader="getTeamspacesList.head" v-show="isActive==='first'" :titlename="''" :title="true" :people="false" :showbtn="false" />
                <div class=" paginationblock">
                  <el-pagination v-if="this.teamspacesFile === ''" @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="this.currpages" :page-size=this.teamspacerquery.data.limit layout="total, prev, pager, next" :total=parseInt(this.$store.state.departure.teamspacesSpacesListTotalCount)>
                  </el-pagination>
                </div>
                <!-- </div> -->
                <!-- <div> -->
                <Table v-loading="secondTable" ref="TABLE" v-if="this.teamspacesFile === '1'" @rowclick="documentrowclick" :Tabledata="getDocumentHandOver" :tabledataHeader="getDocumentHandOver.head" class="tab" v-show="isActive==='first'" :titlename="''" :title="true" :people="false" :showbtn="false" />
                <div class=" paginationblock">
                  <el-pagination v-if="this.teamspacesFile === '1'" @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="this.currpages" :page-size=this.handOverquery.data.limit layout="total, prev, pager, next" :total=parseInt(this.$store.state.departure.documentHandOverInnerListTotalCount)>
                  </el-pagination>
                </div>
                <!-- </div> -->
              </div>
            </transition>

            <transition name="fade" mode="out-in">
              <div v-if="isActive==='second'" class="fileTable">
                <el-table v-loading="secondTable" ref="TABLE" :data="this.DocumentHandOver" @row-click="documentrowclick" tooltip-effect="dark" style="width: 100%" @selection-change="documentselect">
                  <el-table-column type="selection" width="55">
                  </el-table-column>
                  <el-table-column width="30">
                    <template slot-scope="scope">
                      <img v-show="scope.row.pic" :src="scope.row.pic" style="margin-right:100px;" width="30" height="30" class="head_pic" />
                      <img v-show="scope.row.previewPic" :src="scope.row.previewPic" style="margin-right:100px;" preview="3" width="30" height="30" class="head_pic preview" />
                    </template>
                  </el-table-column>
                  <el-table-column prop="name" label="姓名">
                  </el-table-column>
                  <el-table-column prop="size" label="大小">
                    <template slot-scope="scope">{{ scope.row.size === '0B' ? '--' : scope.row.size}}</template>
                  </el-table-column>
                  <el-table-column prop="status" label="状态">
                  </el-table-column>
                </el-table>
                <div v-show="this.documentpagination" class="paginationblock">
                  <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="this.currpages" :page-size=this.handOverquery.data.limit layout="total, prev, pager, next" :total=this.$store.state.departure.documentHandOverInnerListTotalCount>
                  </el-pagination>
                </div>
              </div>
            </transition>
          </div>
        </div>
      </el-col>
    </el-row>
    <el-dialog title="选择成员所在部门及角色" width="700px" :visible.sync="dialogFormVisible">
      <SelectedMember :Treedata="this.treelists" ref="selectmember" @choosemember="choosemember"></SelectedMember>
      <span slot="footer" class="dialog-footer">
        <el-button @click="close">取 消</el-button>
        <el-button type="primary" @click="confim">确 定</el-button>
      </span>
    </el-dialog>
    <el-dialog title="视频播放" width="700px" style="height: 550px" :visible.sync="showvideo">
      <my-video :sources="this.video.sources" :options="this.video.options"></my-video>
    </el-dialog>
    <!-- <video-player class="video-player-box" ref="videoPlayer" :options="playerOptions" :playsinline="true" customEventName="customstatechangedeventname" @play="onPlayerPlay($event)" @ended="onPlayerEnded($event)" @waiting="onPlayerWaiting($event)" @pause="onPlayerPause($event)" @playing="onPlayerPlaying($event)" @ready="playerReadied">
      </video-player> -->
  </div>
</template>
<script>
import enlargeimg from "enlargeimg";
import util from "@/common/js/util";
import Table from "@/components/Table.vue";
import SelectedMember from "@/components/SelectedMember.vue";
import myVideo from "vue-video";
import { mapState, mapActions, mapGetters } from "vuex";
export default {
  data() {
    return {
      fristTable: true,
      secondTable: true,
      showvideo: false,
      video: {
        sources: [
          {
            src: "http://www.w3cschool.cc/try/demo_source/mov_bbb.mp4",
            type: "video/mp4"
          }
        ],
        options: {
          autoplay: true,
          volume: 0.6,
          poster: "http://covteam.u.qiniudn.com/poster.png"
        }
      },
      screenHeight: document.body.clientWidth,
      pic: "",
      documentpagination: false,
      dialogFormVisible: false, //显示移交模态框
      activeName: "first", //切换板块
      isActive: "first", //默认为1板块
      name: this.$route.query.name, //获取点击的人的名字
      tabs: "first", //获取切换板块的值
      data: [],
      DocumentHandOver: [], //文档列表数据
      TeamspacesList: [], //离职人员列表数据
      handOverquery: {
        //文档数据参数对象
        ownerId: this.$route.query.id,
        folderId: 0,
        data: {
          limit: 15,
          offset: 0,
          order: [
            {
              field: "type",
              direction: "ASC"
            },
            {
              field: "modifiedAt",
              direction: "DESC"
            }
          ],
          thumbnail: [
            {
              width: 96,
              height: 96
            }
          ]
        }
      },
      teamspacerquery: {
        //离职人员参数对象
        userId: this.$route.query.id,
        data: {
          userId: this.$route.query.id,
          limit: 15,
          offset: 0
        }
      },
      filePreview: {
        ownerId: this.$route.query.id,
        fileId: ""
      },
      parent: [], //获取文件夹父级ID
      teamspacesFile: "", //切换文件夹显示,
      teamHandOver: {
        //权限移交参数对象
        teamId: "",
        data: {
          createdBy: "",
          ownerBy: ""
        }
      },
      nodesHandOver: {
        //文档移交参数对象
        ownerId: this.$route.query.id,
        data: {
          enterpriseUserId: "",
          destOwnerId: "",
          destParent: 0
        }
      },
      nodesDelete: {
        //文档删除参数对象
        ownerId: this.$route.query.id
      },
      selectnodes: [],
      treelists: [],
      treelistquery: {
        page: 1,
        pagesize: 1000
      },
      currpages: 1,
      isImg: false,
      fileName: "",
      preview: {

      }
    };
  },
  components: {
    Table,
    SelectedMember,
    enlargeimg,
    myVideo
  },
  created() {
    this.getfolderlist();
    this.getteamspaceslist();
    this.gettreelist();
  },
  mounted() {
    setTimeout(() => {}, 200);

    const that = this;
    window.onresize = () => {
      return (() => {
        window.screenWidth = document.body.clientWidth;
        that.screenHeight = window.screenWidth;
      })();
    };
  },
  methods: {
    play() {
      this.showvideo = true;
    },
    showdialogFormVisible() {
      this.dialogFormVisible = true;
    },
    // 获取文件类型
    isPreviewable(fileName) {
      var index = fileName.lastIndexOf(".");
      if (index != -1) {
        var fileType = fileName.substring(index + 1).toLowerCase();
        if (
          fileType == "doc" ||
          fileType == "ppt" ||
          fileType == "xls" ||
          fileType == "docx" ||
          fileType == "pptx" ||
          fileType == "xlsx" ||
          fileType == "txt" ||
          fileType == "pdf"
        ) {
          return true;
        }
      }
    },
    //文档点击
    documentrowclick(row) {
      this.filePreview.fileId = row.id;
      // this.getpreview();
      if (row.type === 0) {
        this.parent.push(row.parent);
        this.handOverquery.folderId = row.id;
        this.getfolderlist();
      }
      //  else {
      //   let isfile = this.isPreviewable(row.name)
      //   console.log(isfile);
      //   if(isfile === true) {
      //     this.$store
      //   .dispatch("previewfiles", {
      //     self: this,
      //     query:  {
      //   ownerId: row.ownedBy,
      //   nodeId: row.id
      // }
      //   })
      //   .then(data => {
      //     console.log(data);
      //   })
      //   .catch(err => {});
      //     // this.$router.push({
      //     //     path: `/preview/`,
      //     //     query: { ownerId: row.ownedBy, nodeId: row.id }
      //     //   });
      //   }
      //   // let name = row.name.lastIndexOf(".");
      //   // let nameLength = row.name.length;
      //   // this.fileName = row.name.substring(name, nameLength);
      //   // console.log(this.fileName);
      //   return;
      // }
    },
    //部门空间点击
    teamspacesrowclick(row) {
      this.teamHandOver.teamId = row.id;
      this.handOverquery.folderId = 0;
      this.teamspacesFile = "1";
      this.parent.push(row.parent);
      this.handOverquery.ownerId = row.id;
      this.getfolderlist();
    },
    //返回上一级
    goback() {
      let pId = this.parent.pop();
      this.handOverquery.folderId = pId;
      if (this.handOverquery.folderId === undefined) {
        if (this.tabs === "first") {
          this.$message({
            message: "已经到达最外层了",
            type: "warning"
          });
          this.teamspacesFile = "";
        } else {
          this.$message({
            message: "已经到达最外层了",
            type: "warning"
          });
        }
      } else {
        this.getfolderlist();
      }
    },
    //删除文档
    nodesdelete() {
      if (this.selectnodes.length === 0) {
        this.$message({
          type: "warning",
          message: "请至少选择一个文件或文件夹进行删除!"
        });
      } else {
        this.$confirm("是否删除", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        })
          .then(() => {
            this.$store
              .dispatch("nodesDelete", {
                self: this,
                query: this.nodesDelete
              })
              .then(() => {
                this.$message({
                  message: "删除成功",
                  type: "warning"
                });
                this.getfolderlist();
              })
              .catch(() => {
                this.$message({
                  message: "删除失败",
                  type: "warning"
                });
              });
          })
          .catch(() => {
            this.$message({
              type: "info",
              message: "已取消删除"
            });
          });
      }
    },
    //获取文档接口
    getfolderlist() {
      this.$store
        .dispatch("documentHandOverInnerList", {
          self: this,
          query: this.handOverquery
        })
        .then(data => {
          this.DocumentHandOver = data.folders.concat(data.files);
          this.DocumentHandOver.forEach(item => {
            item.size = util.formatFileSize(item.size);
            if (util.isImg(item.name)) {
              var index = item.thumbnailUrlList[0].thumbnailUrl.lastIndexOf(
                "/"
              );
              var previewImageUrl = item.thumbnailUrlList[0].thumbnailUrl.substring(
                0,
                index
              );
              this.isImg = true;
              this.filePreview.fileId = item.id;
              item.previewPic = previewImageUrl;
            } else {
              item.pic = util.getImgSrc(item);
            }
          });
          this.secondTable = false;
        })
        .catch(err => {
          this.secondTable = false;
        });
    },
    //获取部门接口
    getteamspaceslist() {
      this.$store
        .dispatch("teamspacesSpaces", {
          self: this,
          query: this.teamspacerquery
        })
        .then((data) => {
          this.fristTable = false;
        })
        .catch(() => {
          this.fristTable = false;
        });
    },
    //获取部门接口
    gettreelist() {
      this.$store
        .dispatch("getDepart", {
          self: this,
          obj: this.treelistquery
        })
        .then(res => {
          this.treelists = util.parseTree(res);
        })
        .catch(err => {});
    },
    //获取预览地址
    getpreview() {
      this.$store
        .dispatch("filePreview", {
          self: this,
          query: this.filePreview
        })
        .then(data => {
          if (this.isImg === true) {
            this.pic = data.url;
          }
        });
    },
    //弹框关闭
    close() {
      this.dialogFormVisible = false;
    },
    //移交按钮
    edit() {
      if (this.tabs === "second") {
        if (this.selectnodes.length === 0) {
          this.$message({
            type: "warning",
            message: "请至少选择一个人员!"
          });
        } else {
          this.dialogFormVisible = true;
        }
      } else {
        this.dialogFormVisible = true;
      }
    },
    //Tab切换事件
    changetabs(tabs) {
      if (tabs === "second") {
        this.currpages = 1;
        this.handOverquery.data.offset = 0;
        this.documentpagination = true;
        this.handOverquery.folderId = 0;
        this.handOverquery.ownerId = this.$route.query.id;
        this.getfolderlist();
      }
      if (tabs === "first") {
        this.currpages = 1;
        this.teamspacerquery.data.offset = 0;
        this.documentpagination = false;
        this.teamspacesFile = "";
        this.getteamspaceslist();
      }
      this.tabs = tabs;
      this.isActive = tabs;
    },
    //分页页数
    handleCurrentChange(val) {
      this.currpages = val;
      var currpage = val;
      if (this.tabs === "first") {
        this.teamspacerquery.data.offset = (currpage - 1) * 15;
        this.getteamspaceslist();
      }
      if (this.tabs === "second") {
        this.handOverquery.data.offset = (currpage - 1) * 15;
        this.getfolderlist();
      }
    },
    //分页
    handleSizeChange(val) {
      this.teamspacerquery.data.limit = val;
      this.handOverquery.data.limit = val;
    },
    //文档移交多选操作
    documentselect(val) {
      this.selectnodes = val;
      let item = {};
      let srcNodes = [];
      val.forEach(items => {
        item = items;
        //选择文件
        srcNodes.push({
          id: item.id,
          ownedBy: item.ownedBy,
          createdBy: item.createdBy
        });
      });
      this.nodesHandOver.data.srcNodes = srcNodes;
      //删除文件
      let nodesDelete = [];
      nodesDelete.push({
        id: item.id,
        ownedBy: item.ownedBy,
        type: item.type
      });
      this.nodesDelete.data = nodesDelete;
    },
    //选择移交人
    choosemember(item) {
      if (this.tabs === "first") {
        this.teamHandOver.data.createdBy = item.cloudUserId;
        this.teamHandOver.data.ownerBy = item.cloudUserId;
      }
      if (this.tabs === "second") {
        this.nodesHandOver.data.destOwnerId = item.cloudUserId;
        this.nodesHandOver.data.enterpriseUserId = item.id;
      }
    },
    confim() {
      if (this.tabs === "first") {
        this.$confirm("是否移交", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        })
          .then(() => {
            this.$store
              .dispatch("teamspacesPut", {
                self: this,
                query: this.teamHandOver
              })
              .then(() => {
                this.teamspacesFile = "";
                this.getteamspaceslist();
                this.$message({
                  type: "success",
                  message: "移交成功!"
                });
                this.dialogFormVisible = false;
              })
              .catch(() => {
                this.$message({
                  type: "success",
                  message: "移交失败!"
                });
              });
          })
          .catch(() => {
            this.$message({
              type: "info",
              message: "已取消移交"
            });
          });
      }
      if (this.tabs === "second") {
        if (this.nodesHandOver.data.destOwnerId === "") {
          this.$message({
            type: "warning",
            message: "请选择一个人员!"
          });
        } else {
          this.$confirm("是否移交", {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning"
          })
            .then(() => {
              this.$store
                .dispatch("nodesPut", {
                  self: this,
                  query: this.nodesHandOver
                })
                .then(() => {
                  this.getfolderlist();
                  this.$message({
                    type: "success",
                    message: "移交成功!"
                  });
                  this.nodesHandOver.data.srcNodes = null;
                  this.nodesHandOver.data.srcNodes = [];
                  this.gettreelist();
                  this.dialogFormVisible = false;
                })
                .catch(() => {
                  this.$message({
                    type: "info",
                    message: "移交失败!"
                  });
                });
            })
            .catch(() => {
              this.$message({
                type: "info",
                message: "已取消移交"
              });
            });
        }
      }
    },
    // listen event
    onPlayerPlay(player) {
      console.log("player play!", player);
    },
    onPlayerPause(player) {
      console.log("player pause!", player);
    },
    // ...player event
    onPlayerPlaying(player) {
      console.log(player);
    },
    // or listen state event
    playerStateChanged(playerCurrentState) {
      console.log("player current update state", playerCurrentState);
    },
    // player is ready
    playerReadied(player) {
      console.log("the player is readied", player);
    }
  },
  computed: {
    player() {
      return this.$refs.videoPlayer.player;
    },
    videosrc() {
      console.log("111");
      return this.$store.state.departure.filePreview;
    },
    ...mapGetters(["getTeamspacesList", "getDocumentHandOver", "getfileview"])
  }
};
</script>
<style lang="less" scoped>
.fileTable {
  .el-table__row {
    background: red;
    .cell {
      margin-right: 15px !important;
    }
  }
}
</style>

<style lang='less'>
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
.autho-main {
  height: 100%;
}
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
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.5s;
}
.fade-enter,
.fade-leave-to {
  opacity: 0;
}
.el-tabs {
  height: 100%;
  .autho-tab {
    height: 100%;
  }
}
.el-table .cell,
.el-table th div,
.el-table--border td:first-child .cell,
.el-table--border th:first-child .cell {
  padding-left: 0;
}
.el-table {
  .cell {
    .el-checkbox__inner {
      width: 15px;
      height: 15px;
    }
  }
}
.__cov-video-container {
  height: 550px;
}
</style>


