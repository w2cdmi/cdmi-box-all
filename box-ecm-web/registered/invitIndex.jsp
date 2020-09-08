<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cse" uri="http://cse.huawei.com/custom-function-taglib" %>
<%@ page import="com.huawei.sharedrive.uam.util.CSRFTokenManager" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <meta name="renderer" content="webkit">
    <meta name="keywords" content="ECM">
    <meta name="description" content="邀请注册文件企业文件宝">
    <title>邀请注册</title>
    
    <link rel="stylesheet" href="${ctx}/static/inviteregester/css/rest.css">
    <link rel="stylesheet" href="${ctx}/static/inviteregester/css/element-ui-index.css">
    <link rel="stylesheet" href="https://unpkg.com/element-ui/lib/theme-chalk/index.css">
    <link rel="stylesheet" href="${ctx}/static/inviteregester/css/registered.css">
    <script src="https://res.wx.qq.com/connect/zh_CN/htmledition/js/wxLogin.js"></script>
    <script src="${ctx}/static/inviteregester/lib/bluebird.min.js"></script>
    <script src="${ctx}/static/inviteregester/lib/vue.js"></script>
    <script src="${ctx}/static/inviteregester/lib/axios.min.js"></script>
    <script src="${ctx}/static/inviteregester/lib/element-ui.js"></script>
    <style>
      

        body{
            background: #eaeaea;
        }
  
        .el-button:focus, .el-button:hover{
            color: #606266;
            border-color: #dcdfe6;
            background-color: #dcdfe6;
        }
        .laststep .butnext{
            color: #fff;
            background: #09BB07;
        }
        .laststep .butnext .el-button:focus,laststep .butnext .el-button:hover{
            color: #fff;
            border-color: #dcdfe6;
            background: #09BB07;
        }
        .success{
            width: 370px;
        }
        #phonecode,.phonecode{
            width: 210px;
        }
        #qrcode{
            height: 280px;
            /* margin-top: 40px; */
            margin: 40px 0 0 0;
        }
        @media screen and (max-width: 430px) {
            html,body{overflow:hidden;overflow-y:hidden;overflow-x: hidden;}
            .dialog{
                width: 100%;
                position: relative;
            }
            body{
                background-color:#FFF;
            }
            #qrcode{
                /* transform: translate(-50%, -110%); */
            }
            .dialog .form .row .input {
                width: 73%;
            }
            #phonecode{
                width: 130px;
            }
            .phonecode{
                width: 130px;
            }
            .dialog .form .row .el-button{
                padding:0 8px;
            }
            .footer .butnext{
                width: 90%;
            }
        }
    </style>
</head>

<body>
    <div id="app">
        <div>
            <div><span></span></div>
        </div>
        <div class="main dialog" v-loading="loading">
            <div class="header">
                ${enterpriseName}邀请您加入企业
            </div>
            <template v-if="!linkValid">
                <div class="error">
                    <div>
                        <div class="setp1">
                            <div>
                                <span></span>
                                <span></span>
                            </div>
                            <div></div>
                        </div>
                        <div class="setp1">
                            <div>
                                <span></span>
                                <span></span>
                            </div>
                            <div></div>
                        </div>
                        <div class="setp1">
                            <div>
                                <span></span>
                                <span></span>
                            </div>
                            <div></div>
                        </div>
                    </div>
                    <div>
                        <span>链接已失效</span>
                        <span>请联系负责人，发送新链接</span>
                    </div>
                </div>
            </template>
            <template v-if="linkValid">
                <el-steps class="stpes" align-center :active="active" finish-status="success">
                    <el-step title="微信授权"></el-step>
                    <el-step title="员工信息"></el-step>
                    <el-step title="注册完成"></el-step>
                </el-steps>
                <transition name="fade" mode="out-in">
                    <template v-if="active===1">

                    </template>
                </transition>

                <transition name="fade" mode="out-in">
                    <template v-if="active===2">
                        <div class="form" style="height: 354px;">
                            <div class="row">
                                <div class="label">姓名</div>
                                <el-input class="input" placeholder="请输入姓名" v-model="name" @blur="nameVerification"></el-input>
                            </div>
                            <div class="tip">
                                {{nameError}}
                            </div>
                            <div class="row">
                                <div class="label">手机号</div>
                                <el-input class="input" placeholder="请输入手机号" v-model="mobile" max="18" @blur="phoneVerification"></el-input>
                            </div>
                            <div class="tip">{{phoneError}}</div>
                            <div class="row">
                                <div class="label">验证码</div>
                                <div class="input" >
                                    <el-input class="phonecode" id="phonecode" placeholder="请输入验证码" v-model="checkCode" @blur="codeVerification"></el-input>
                                    <el-button style="float: right;" class="button" @click="getCode" :disabled="isgeting">{{getCodeMsg}}</el-button>
                                </div>
                            </div>
                            <div class="tip">
                                {{checkCodeError}}
                            </div>
                            <div class="footer laststep" v-if="active">
                                <el-button :disabled="disabled" @click="next" class="butnext" >{{buttonmsg}}</el-button>
                            </div>
                        </div>
                    </template>
                </transition>
                <transition name="fade" mode="out-in">
                    <template v-if="active===3">
                        <div class="from">
                            <div class="success">
                                <div>
                                    <i class="el-icon-circle-check"></i>
                                </div>
                                <div>
                                    <span>注册完成</span>
                                    <span>恭喜您，员工{{name}}已加入{{ername}}</span>
                                </div>
                            </div>
                            <div class="footer" v-if="active">
                                    <el-button :disabled="disabled" @click="next" class="butnext" >{{buttonmsg}}</el-button>
                            </div>
                        </div>
                    </template>
                </transition>
               
            </template>
        </div>
    </div>
    <div id="qrcode" >
       <span id="qrcodeTip" style="display: none;color: rgb(55, 55, 55);font-size: 12px;">使用微信扫一扫实现登录，不可以长按二维码实现登录</span>
       <div id="qrcodes"></div>
    </div>

    <script type="text/javascript">
        var HTTP = "http://www.jmapi.cn:8085";
        var HTTPSERVER = window.location.origin+"/";
    </script>
    <script>
         function browserRedirect() {
            var sUserAgent = navigator.userAgent.toLowerCase();
            var bIsIpad = sUserAgent.match(/ipad/i) == "ipad";
            var bIsIphoneOs = sUserAgent.match(/iphone os/i) == "iphone os";
            var bIsMidp = sUserAgent.match(/midp/i) == "midp";
            var bIsUc7 = sUserAgent.match(/rv:1.2.3.4/i) == "rv:1.2.3.4";
            var bIsUc = sUserAgent.match(/ucweb/i) == "ucweb";
            var bIsAndroid = sUserAgent.match(/android/i) == "android";
            var bIsCE = sUserAgent.match(/windows ce/i) == "windows ce";
            var bIsWM = sUserAgent.match(/windows mobile/i) == "windows mobile";
            // document.writeln("您的浏览设备为：");
            if (bIsIpad || bIsIphoneOs || bIsMidp || bIsUc7 || bIsUc || bIsAndroid || bIsCE || bIsWM) {
                // document.writeln("phone");
                // console.log('phone',document.getElementById("qrcodeTip"))
                document.getElementById("qrcodeTip").style.display='block';
            } else {
                // document.writeln("pc")
                document.getElementById("qrcodeTip").style.display='none';
                // console.log('pc',document.getElementById("qrcodeTip"));
            }
        }

        var host = HTTPSERVER;

        var getCode = function (mobile) {
            return new Promise(function(resolve, reject) {
                axios({
                    method: 'POST',
                    url: host + 'msm/messages/v1/sms/checkcode',
                    headers: {
                        "Content-Type": "application/json",
                    },
                    data: {
                        "mobile": mobile
                    }
                }).then(function (respone) {
                    resolve(respone)
                }).catch(function (error) {
                    reject(error)
                });

            })
        }


        var registered=function(params){
            return new Promise(function(resolve, reject) {
                axios({
                    method: 'post',
                    url: host + 'ecm/api/v2/enterprise/registerUser',
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization":params.token
                    },
                    data: {
                        appId:params.appId,
                        enterpriseId:params.enterpriseId,
                        deptId:'0',
                        phone:params.phone,
                        checkCode:params.checkCode,
                        name:params.name,
                        code:params.code
                    }
                }).then(function (respone) {
                    resolve(respone)
                }).catch(function (error) {
                    reject(error)
                });

            })
        }

    </script>
    <script>
        var TOKEN = "${token}";
        var enterpriseId="${enterpriseId}";
        var accountId="${accountId}";
        var appId="${appId}";
        var enterpriseName="${enterpriseName}";
        var Timer="";
        window.onload = function () {
             browserRedirect();
            
            var linkValid = TOKEN === 'isInvalid' ? false : true
            var app = new Vue({
                el: '#app',
                data: {
                    message: '',
                    ername: enterpriseName,
                    active: 1,
                    disabled: true,
                    name: "",
                    nameError: "",
                    code: "",
                    mobile: "",
                    phoneError: "",
                    html: "",
                    linkValid: linkValid,
                    loading: false,
                    getCodeMsg: "获取验证码",
                    timer: 60,
                    isgeting: false,
                    checkCode:'',
                    checkCodeError: "",
                },
                computed: {
                    buttonmsg: {
                        get:function() {
                            var text = "下一步";
                            if (this.active === 3) text = '立即登录'
                            return text
                        }
                    }
                },
                created:function() {
                    var query=document.location.search.substring(1).split("&");
                    for(var i=0,len=query.length;i<len;i++){
                        var item=query[i];
                        var arr=item.split("=");
                        if(arr[0]==='code'){
                            this.code=arr[1];
                            this.active=2;
                            this.disabled=false;
                            break;
                        }
                    }
                   
                    if(this.active===2){
                        var timer = window.localStorage.getItem("timer");
                        var Ntimer = +new Date();
                        if (timer - Ntimer > 0) {
                            this.showCodeMsg();
                        }
                    }
                },
                mounted:function(){
                    if(this.active===1){
                        this.getWxCode();
                    }else{
                        document.getElementById('qrcode').style.display = 'none';
                    }
                },
                methods: {
                    registered:function(){
                    },
                    nameVerification:function() {
                        var name = this.name.trim();
                        if (Boolean(name)) {
                            this.nameError = "";
                        } else {
                            this.nameError = "姓名不能为空";
                        }
                    },
                    phoneVerification:function() {
                        var mobile = this.mobile.trim();
                        if (mobile === "") {
                            this.phoneError = "手机号不能为空"
                            return;
                        }
                        var ver = /^1\d{10}$/.test(mobile);
                        if (!ver) {
                            this.phoneError = "手机号格式错误";
                        } else {
                            this.phoneError = ""
                        }
                    },
                    codeVerification:function () {
                        var ver = /^\d{6}$/.test(this.checkCode);
                        if (!ver) {
                            this.checkCodeError = "验证码为6位数字";
                        } else {
                            this.checkCodeError = "";
                        }
                    },
                    showCodeMsg:function () {
                        if (this.isgeting === false) {
                            var that = this;
                            Timer = setInterval(function () {
                                var times = window.localStorage.getItem("timer");
                                var time=Math.floor((times - (+new Date())) / 1000)
                                that.timer = time;
                                that.getCodeMsg = time + "秒后获取";
                                that.isgeting = true;
                                if (time <= 0) {
                                    that.getCodeMsg = "获取验证码";
                                    that.isgeting = false;
                                    clearInterval(Timer);
                                }
                            }, 1000 / 16)
                        }
                    },
                    getCode:function () {
                        this.phoneVerification();
                        if (!this.phoneError) {
                            this.isgeting=false;
                            window.localStorage.setItem("timer", +new Date() + 60 * 1000)
                            this.showCodeMsg();
                            getCode(this.mobile).then(function(res){}).catch(function(err){});
                        }
                    },
                    getWxCode:function (){
                        document.getElementById('qrcode').style.display = 'block';
                            var obj = new WxLogin({
                                id: "qrcodes",
                                appid: "wxf54677c64020f6f1",
                                scope: "snsapi_login",
                                redirect_uri: "https://www.jmapi.cn/ecm/api/v2/enterprise/invitIndex?token="+TOKEN+"&phone=18281575318&token="+TOKEN,
                                state: "0",
                                style: "black",
                                href:'data:text/css;base64,LmltcG93ZXJCb3ggLnFyY29kZSB7d2lkdGg6IDIwMHB4O30NCi5pbXBvd2VyQm94IC50aXRsZSB7ZGlzcGxheTogbm9uZTt9DQouaW1wb3dlckJveCAuaW5mbyB7d2lkdGg6IDIwMHB4O30NCi5zdGF0dXNfaWNvbiB7ZGlzcGxheTpub25lfQ0KLmltcG93ZXJCb3ggLnN0YXR1cyB7dGV4dC1hbGlnbjogY2VudGVyO30='
                                // https://www.jmapi.cn/ecm/static/inviteregester/css/codeStyle.css
                            });
                    },
                    next:function () {
                        var that=this;
                        if(this.active===3){
                            document.location.href=HTTPSERVER;
                        }
                        if(this.active===2){
                            this.nameVerification();
                            this.phoneVerification();
                            this.codeVerification();
                            var verName = !!this.nameError;
                            var verPhone = !!this.phoneError;
                            var verCode = !!this.checkCodeError;
                            if (!verName && !verPhone && !verCode) {
                                registered({
                                    appId:appId,
                                    enterpriseId:enterpriseId,
                                    phone:this.mobile,
                                    name:this.name,
                                    checkCode:this.checkCode,
                                    code:this.code,
                                    token:TOKEN
                                }).then(function(res) {
                                    if(res.code===401){
                                        that.linkValid=false;
                                        return;
                                    }
                                    if(res.data.code==='CheckCodeDisabled'){
                                        that.codeError="验证码错误";
                                    }else{
                                        that.active=3;
                                    }
                                    
                                })
                            }
                        }

                        if(this.active===1){
                            this.active++;
                            document.getElementById('qrcode').style.display = 'none';
                        }

                    }

                }
            })

        }
    </script>
</body>

</html>