<template>
  <div class="panal">
    <div class="block block-masg" v-loading="infoLoading">
      <div>当前套餐：{{this.enterprisedata.packageName}}，{{this.enterprisedata.packageAccountQuota}} / 人，{{this.enterprisedata.packageAccountNumber}}个账号</div>
      <div>|</div>
      <div>剩余容量：{{this.remaining}}</div>
      <div>总容量：{{this.enterprisedata.usedSpace}} / {{this.enterprisedata.maxSpace}}</div>

      <div style="margin-right: 200px;">账号：{{this.enterprisedata.usedAccountNumber }}个 / {{this.enterprisedata.maxAccountNumber}}个</div>
    </div>
    <div class="block block-oneperson">
      <div class="onepersondiv" v-loading="infoLoading">
        <div class="showperson pl35">
          <div>当前个人容量上限：</div>
          <span>{{this.enterprisedata.defaultAccountSpaceQuota}}</span>
          <button @click="editceiling()">修改上限</button>
        </div>
      </div>
      <div class="onepersondiv" v-loading="infoLoading">
        <div class="pl35">可分配共享容量：{{this.remainingShareSpace}}</div>
      </div>
      <div class="onepersondiv" v-loading="infoLoading">
        <div class="pl35">共享容量：{{this.enterprisedata.usedShareSpace}} / {{this.enterprisedata.maxShareSpace}}</div>
      </div>
    </div>
    <div class="block block-table">
      <div class="title">
        <span>员工配额</span>
        <!-- <button @click="capacityDistribution()">容量分配</button> -->
      </div>
      <div class="table-block">
        <el-table v-loading="tableLoading" ref="singleTable" :data="data" stripe style="width: 100%" highlight-current-row @current-change="handleSelectionChange">
          <el-table-column prop="name" label="用户名">
          </el-table-column>
          <el-table-column prop="alias" label="姓名">
          </el-table-column>
          <el-table-column prop="depts" label="所在部门">
            <template slot-scope="scope">
              <div>
                <span v-for="(item,idx) in scope.row.depts" :key="idx" v-if="idx<3">
                  {{item.name}}
                </span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="容量 (已用/总量)">
            <template slot-scope="scope">
              <span class="spaceUsed">{{scope.row.spaceUsed}}</span> /
              <span>{{scope.row.spaceQuota}}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作">
            <template slot-scope="scope">
              <el-button size="mini" @click="capacityDistribution(scope.row)">容量分配</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination-block" v-show="total>0">
          <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentpages" :page-size="this.pagesize" layout="total, prev, pager, next" :total="total">
          </el-pagination>
        </div>
      </div>
    </div>
    <el-dialog title="容量分配" :visible.sync="allDialogVisible" width="30%" :before-close="handleClose">
      <div class="attenion">当前分配容量不能低于
        <span> {{this.enterprisedata.packageAccountQuota}}</span>，不能高于
        <span> {{this.maxRemainingSpace}}</span>
      </div>
      <div class="masg">当前个人容量上限</div>
      <el-input v-model="personalInput" placeholder="请输入内容" @change="checkVal" maxlength="11"></el-input>
      <div style="margin-top: 20px float: right;">
        <el-radio-group v-model="radio5" size="small" @change="changeradio">
          <el-radio-button label="MB"></el-radio-button>
          <el-radio-button label="GB"></el-radio-button>
        </el-radio-group>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="handleClose">取 消</el-button>
        <el-button type="primary" @click="confim()">确 定</el-button>
      </span>
    </el-dialog>
    <el-dialog title="容量分配" :visible.sync="personalDialogVisible" width="30%" :before-close="handleClose">
      <div class="masg">{{this.row === null ? '' : this.row.alias}}-容量</div>
      <el-input v-model="personalInput" placeholder="请输入内容" @change="checkVal" maxlength="11"></el-input>
      <div style="margin-top: 20px float: right;">
        <el-radio-group v-model="radio5" size="small" @change="changeradio">
          <el-radio-button label="MB"></el-radio-button>
          <el-radio-button label="GB"></el-radio-button>
        </el-radio-group>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="handleClose">取 消</el-button>
        <el-button type="primary" @click="confim()">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template> 
<script>
import util from "@/common/js/util";
export default {
  data() {
    return {
      // allInput: "",
      personalInput: "", //输入的值
      radio5: "GB", //初始显示存贮单位
      allDialogVisible: false, //全部分配框
      personalDialogVisible: false, //个人分配框
      capacity: "",
      data: [], //表格数据
      selectrowarr: [],
      row: "", //选中行
      current: 1, //表格当前页数
      total: 0, //表格总数
      pagesize: 15, //表格条数
      enterprisedata: {}, //公司容量对象
      residualCapacity: "",
      personalQuota: {
        //个人分配接口参数
        data: {
          accountId: "",
          enterpriseUserIds: [],
          quota: 0
        }
      },
      employeeQuota: {
        //全部分配接口参数
        data: {
          accountId: 0,
          defaultAccountSpaceQuota: 0
        }
      },
      personalSpaceUsed: "",
      remaining: "", //剩余容量
      remainingShareSpace: "", //可分配共享容量
      maxRemainingSpace: "", //不能超过此容量
      maxRemainingSpaceNum: "", // 不能超过此容量数值
      packageAccountQuotaNum: "", // 不能低于此容量数值
      remainingNum: "", //不能高于剩余容量数值
      infoLoading: true,
      tableLoading: true
    };
  },
  created() {
    this.init();
  },
  methods: {
    //请求公司容量-----列表数据
    init() {
      this.$store
        .dispatch("getDepartEmployees", {
          self: this,
          obj: { deptId: -1, page: this.current, pagesize: this.pagesize }
        })
        .then(res => {
          res.content.forEach(item => {
            item.spaceUsed = util.formatFileSize(item.spaceUsed);
            item.spaceQuota = util.formatFileSize(item.spaceQuota);
          });
          this.data = res.content;
          this.total = res.totalElements;
          this.tableLoading = false;
        })
        .catch(err => {
          this.tableLoading = false;
        });
      this.$store
        .dispatch("enterprise", {
          self: this,
          query: {}
        })
        .then(res => {
          this.remainingShareSpace = res.maxShareSpace - res.usedShareSpace; //可分配共享容量
          this.remaining = res.maxSpace - res.usedSpace; //剩余容量
          this.remainingNum = this.remaining;
          this.maxRemainingSpace =
            this.remainingShareSpace / res.usedAccountNumber +
            res.packageAccountQuota; //不能超过此容量

          this.maxRemainingSpaceNum = this.maxRemainingSpace; // 不能超过此容量数值
          this.maxRemainingSpace = util.formatFileSize(this.maxRemainingSpace); //不能超过此容量
          this.remainingShareSpace = util.formatFileSize(
            this.remainingShareSpace
          ); //可分配共享容量
          res.maxShareSpace = util.formatFileSize(res.maxShareSpace); //共享容量
          res.usedShareSpace = util.formatFileSize(res.usedShareSpace); //使用过的共享容量
          this.packageAccountQuotaNum = res.packageAccountQuota; // 不能低于此容量数值
          res.packageAccountQuota = util.formatFileSize(
            res.packageAccountQuota
          ); //套餐初始容量
          res.maxSpace = util.formatFileSize(res.maxSpace); //总容量
          res.usedSpace = util.formatFileSize(res.usedSpace); //使用过的总容量
          res.defaultAccountSpaceQuota = util.formatFileSize(
            res.defaultAccountSpaceQuota
          ); //当前个人容量上限
          this.remaining = util.formatFileSize(this.remaining); //剩余容量
          this.enterprisedata = res; //公司容量对象
          this.infoLoading = false;
        })
        .catch(err => {
          this.infoLoading = false;
        });
    },
    changeradio(val) {},
    checkVal(val) {},
    //关闭模态框
    handleClose() {
      this.setCurrent();
      // this.$refs.singleTable.setRow(row);
      this.row = "";
      this.personalInput = "";
      this.allDialogVisible = false;
      this.personalDialogVisible = false;
    },
    //取消选中
    setCurrent() {
      this.$refs.singleTable.setCurrentRow();
    },
    //打开全部分配模态框
    editceiling() {
      this.radio5 = this.enterprisedata.defaultAccountSpaceQuota.substr(
        this.enterprisedata.defaultAccountSpaceQuota.length - 2
      ); //显示全部分配单位
      this.personalInput = this.enterprisedata.defaultAccountSpaceQuota.slice(
        0,
        -2
      ); //显示模态框当前数据
      this.row = "";
      this.allDialogVisible = true;
    },
    //选中的行
    handleSelectionChange(row) {
      this.row = row;
    },
    //个人分配模态框
    capacityDistribution(row) {
      // if (this.row === null || this.row === "") {
      //   this.$message({
      //     type: "warning",
      //     message: "请选择员工"
      //   });
      // } else {
      var spaceUsedUnit = row.spaceUsed.substr(row.spaceUsed.length - 2);
      if (spaceUsedUnit === "B") {
        this.personalSpaceUsed = row.spaceUsed.slice(0, -2);
      }
      if (spaceUsedUnit === "KB") {
        this.personalSpaceUsed = row.spaceUsed.slice(0, -2) * 1024;
      }
      if (spaceUsedUnit === "MB") {
        this.personalSpaceUsed = row.spaceUsed.slice(0, -2) * 1024 * 1024;
      }
      if (spaceUsedUnit === "GB") {
        this.personalSpaceUsed =
          row.spaceUsed.slice(0, -2) * 1024 * 1024 * 1024;
      }
      this.radio5 = row.spaceQuota.substr(row.spaceQuota.length - 2);
      this.personalInput = row.spaceQuota.slice(0, -2);
      this.personalDialogVisible = true;
      // }
    },
    //确定按钮
    confim() {
      if (this.personalInput.length === 11) {
        this.$message({
          message: "输入的值过长",
          type: "warning"
        });
      }
      var inputvalTest = /^\d+(\.\d{1,2})?$/;
      var inputval = "";
      if (this.radio5 === "GB") {
        inputval = this.personalInput * 1024 * 1024 * 1024;
      }
      if (this.radio5 === "MB") {
        inputval = this.personalInput * 1024 * 1024;
      }
      if (this.row === "" || this.row === null) {
        this.employeeQuota.data.accountId = this.enterprisedata.accountId;
        this.employeeQuota.data.defaultAccountSpaceQuota = Math.ceil(inputval);
        if (inputval > this.maxRemainingSpaceNum) {
          this.$alert("分配后总容量，不能高于共享容量", "提示", {
            confirmButtonText: "确定"
          });
        } else if (inputval < this.packageAccountQuotaNum) {
          this.$alert("输入的容量大小不能低于套餐初始配额", "提示", {
            confirmButtonText: "确定"
          });
        } else if (!inputvalTest.test(inputval)) {
          this.$alert("只能输入正确数字（包括小数点后两位）", "提示", {
            confirmButtonText: "确定"
          });
          inputval = "";
        } else {
          this.$store
            .dispatch("setEmployeesSpaceQuota", {
              self: this,
              query: this.employeeQuota
            })
            .then(res => {
              this.init();
              this.$message("分配成功");
              this.row = {};
              this.employeeQuota.data = {};
              this.allDialogVisible = false;
            })
            .catch(err => {
              this.$message("容量不足");
            });
        }
      } else {
        this.personalQuota.data.accountId = this.enterprisedata.accountId;
        this.personalQuota.data.enterpriseUserIds.push(this.row.id);
        this.personalQuota.data.quota = Math.ceil(inputval);
        if (inputval > this.remainingNum) {
          this.$alert("分配后总容量，不能高于剩余共享容量", "提示", {
            confirmButtonText: "确定"
          });
        } else if (inputval < this.packageAccountQuotaNum) {
          this.$alert("输入的容量大小不能低于套餐初始配额", "提示", {
            confirmButtonText: "确定"
          });
        } else if (!inputvalTest.test(inputval)) {
          this.$alert("只能输入正确数字（包括小数点后两位）", "提示", {
            confirmButtonText: "确定"
          });
          inputval = "";
        } else {
          this.$store
            .dispatch("spacequota", {
              self: this,
              query: this.personalQuota
            })
            .then(res => {
              this.init();
              this.$message("分配成功");
              this.row = {};
              this.personalQuota.data.enterpriseUserIds = [];
              this.personalDialogVisible = false;
            })
            .catch(err => {
              this.$message("容量不足");
              this.personalQuota.data.enterpriseUserIds = [];
            });
        }
      }
    },
    //分页
    handleCurrentpages(pages) {
      this.current = pages;
      this.init();
    },
    handleSizeChange(val) {
      console.log(`每页 ${val} 条`);
    }
  }
};
</script>

<style lang='less' scoped>
.content-container .grid-content {
  padding: 0;
  min-width: 1400px;
}
.panal {
  background: #f5f5f5;
}
.table-block {
  top: 60px !important;
}
.block {
  margin-bottom: 45px;
  background: #fff;
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

  .table-block {
    background: #fff;
  }
}
.block-table {
  position: absolute;
  left: 0;
  right: 0;
  top: 285px;
  bottom: 0;
  margin-bottom: 0;
}
.block-masg {
  display: flex;
  justify-content: space-between;
  height: 75px;
  line-height: 75px;
  padding: 0 35px;
  background: #fff;
}
.block-oneperson {
  height: 120px;
  background: #f5f5f5;
  line-height: 120px;
  .onepersondiv {
    background: #fff;
    float: left;
    width: 32%;
    height: 100%;
    span {
      font-size: 23px;
      color: #f97878;
    }
    button {
      cursor: pointer;
      background: #f5f5f5;
      padding: 5px 20px;
      border: 0;
      font-size: 12px !important;
      float: right;
      margin-right: 60px;
      margin-top: 5px;
      color: #333333;
    }
    &:nth-child(1) {
      line-height: 0;
    }
    .showperson {
      line-height: 40px;
      margin-top: 20px;
    }
  }
  .onepersondiv + .onepersondiv {
    margin-left: 2%;
  }
  .pl35 {
    padding-left: 35px;
  }
}
.el-message-box {
  .el-message-box__header {
    background: #f5f5f5;
    .el-message-box__close {
      color: #979797;
      font-size: 21px;
      font-weight: bold;
    }
  }
  .el-message-box__content {
    .el-message-box__message {
      margin-top: 20px;
      p {
        color: #333333;
      }
    }
    .el-input__inner {
      background: #f5f5f5;
      border: 0;
      border-radius: 0;
    }
  }
  .el-message-box__btns {
    button {
      padding: 10px 24px;
      border-radius: 0;
      border: 0;
      background: #f5f5f5;
    }
    button:nth-child(2) {
      background: #ea5036;
      color: #fff;
    }
  }
}
.pagination-block {
  position: fixed;
  left: 45%;
  bottom: 50px;
}
.el-table__row {
  cursor: pointer;
  .cell {
    .spaceUsed {
      width: 80px;
      display: inline-block;
    }
    span + span {
      margin-left: 20px;
    }
  }
}
.el-dialog {
  .attenion {
    span {
      color: red;
    }
  }
  .masg {
    margin: 20px 0;
  }
  .el-dialog__header {
    padding: 0;
    line-height: 40px;
    background: #f5f5f5;
    height: 40px;
    .el-dialog__headerbtn {
      top: 13px;
    }
    .el-dialog__title {
      font-size: 16px;
      margin-left: 20px;
    }
  }
  .el-dialog__body {
    padding: 15px 20px;
    .el-input {
      float: left;
      width: 75%;
      margin-right: 10px;
    }
    .el-radio-group {
      .el-radio-button__orig-radio:checked + .el-radio-button__inner {
        background: #606060;
        border: 1px solid #606060;
        box-shadow: none;
      }
      .el-radio-button {
        .el-radio-button__inner {
          background: #f5f5f5;
          color: #cccccc;
        }
      }
    }
  }
}
</style>