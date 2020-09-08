<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter" %>
<%@ page import="pw.cdmi.box.disk.user.service.LoginRule" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=10"/>
    <meta http-equiv="X-UA-Compatible" content="IE=9"/>
    <meta http-equiv="X-UA-Compatible" content="IE=8"/>
    <META HTTP-EQUIV="Expires" CONTENT="0">
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <META HTTP-EQUIV="Cache-control" content="no-cache, no-store, must-revalidate">
    <META HTTP-EQUIV="Cache" CONTENT="no-cache">
    <title><spring:message code="main.title"/></title>
    
    <link rel="shortcut icon" type="image/x-icon" href="${ctx}/static/skins/default/img/temp/logo.ico">
    <link href="${ctx}/static/skins/default/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/static/skins/default/css/public.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/static/skins/default/css/login.css" rel="stylesheet" type="text/css"/>

    <script src="${ctx}/static/js/public/jquery-1.10.2.min.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/bootstrap.min.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/uaredirect.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/JQbox-hw-switch.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/common.js" type="text/javascript"></script>
    <script src="https://res.wx.qq.com/connect/zh_CN/htmledition/js/wxLogin.js"></script>
    <script src="http://rescdn.qqmail.com/node/ww/wwopenmng/js/sso/wwLogin-1.0.0.js"></script>

    <%
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        String saveRequestStr = "";
        if (null != request) {
            String savedRequestStrObject = request.getSession().getAttribute("savedRequestStr") == null ? "" : request.getSession().getAttribute("savedRequestStr").toString();
            if (StringUtils.isNotBlank(savedRequestStrObject)) {
                saveRequestStr = org.springframework.web.util.HtmlUtils.htmlEscape(savedRequestStrObject);
            }
        }
        boolean isLocalAuth = LoginRule.forgetPwd();

        String wxAuthUrl = java.net.URLEncoder.encode("https://www.storbox.cn/login/wx","UTF-8");
        String wwAuthUrl = "https://open.work.weixin.qq.com/wwopen/sso/3rd_qrConnect?appid=wwba09b5d7931f8d7e&redirect_uri=" + java.net.URLEncoder.encode("https://www.storbox.cn/login/wxwork","UTF-8") + "&state=0&usertype=member";
    %>

    <script type="text/javascript">
        $(function () {
            if (window.parent.length > 0) {
                window.top.location = "${ctx}/login";
            }

//            var u = navigator.userAgent;
//            var isAndroid = u.indexOf('Android') > -1 || u.indexOf('Adr') > -1;
//            var isIOS = !!u.match(/\(i[^;]+;(U;)?CPU.+Mac OS X/);
//            if (isAndroid) {
//                uaredirect("${ctx}/static/clientDownload/androidphone_client.html");
//            } else if (isIOS) {
//                uaredirect("${ctx}/static/clientDownload/ios_client.html");
//            }

            if (document.all) {
                document.execCommand("ClearAuthenticationCache", "false");
                delRootCookie("fileListPerPageNum");
            } else {
                <%--var xmlhttp = new XMLHttpRequest();--%>
                <%--xmlhttp.open("GET", "${ctx}/logout", false);--%>
                <%--xmlhttp.send(null);--%>
            }

            loadSysSetting("${ctx}", '<spring:message code="common.language1" />');

            // var imgData = ["${ctx}/static/skins/default/img/login-switch/s1.png",
            // 				"${ctx}/static/skins/default/img/login-switch/s2.png",
            // 				"${ctx}/static/skins/default/img/login-switch/s3.png",
            // 				"${ctx}/static/skins/default/img/login-switch/s4.png" ];
            // if ('<spring:message code="common.language1"/>'=="en") {
            // 	imgData = ["${ctx}/static/skins/default/img/login-switch/s1-en.png",
            // 				"${ctx}/static/skins/default/img/login-switch/s2-en.png",
            // 				"${ctx}/static/skins/default/img/login-switch/s3-en.png",
            // 				"${ctx}/static/skins/default/img/login-switch/s4-en.png" ];
            // }
            // $("#banner").comboSwitch({
            // 	data : imgData,
            // 	posStart : 1600,
            // 	posEnd : -800,
            // 	speed : 5000,
            // 	timer : 1000
            // });

            $('.nav-tabs a').click(function (e) {
                e.preventDefault();
                $(this).tab('show');

            });
        });
    </script>
    <style type="text/css">
    	body {
    overflow-x: hidden;
    overflow-y: scroll;
}

.background {
    width: 100%;
    height: 720px;
}

.background > img {
    position: absolute;
    top: 178px;
    left: 250px;
    right: 250px;
    height: 530px;
}

.body {
    position: absolute;
    top: 50%;
    left: 50%;
    width: 1020px;
    margin: -280px 0 0 -510px;
    font-size: 12px;
    border-radius: 2px;
}
.login-header{
	width: 100%;
	position: absolute;
	height: 80px;
	top: 0;
}
.login-header-middle{
	position: absolute;
	top: 0;
	left: 50%;
	margin: 0 0 0 -530px;
	width: 1020px;
	height: 40px;
	padding-top: 20px;
	padding-bottom: 20px;
}
.login-block {
    position: relative;
    top: -20px;
    float: right;
    margin-top: 60px;
    width: 350px;
    min-height: 460px;
    overflow: hidden;
    background-color: #fafafa;
}
.login-block > ul.login-nav {
    padding: 0px;
    margin: 0px;
    border-bottom: 0px;
    overflow: hidden;
}

.login-block > ul.login-nav > li {
    float: left;
}
.login-block > ul.login-nav > li > a {
    width: 175px;
    height: 52px;
    line-height: 52px;
    border: 0px;
    border-radius: 0px;
   /* background-color: #cbe2fb;*/
    color: #5d5c5c;
    font-size: 14px;
    text-align: center;
    padding: 0px;
    margin-right: 0px;
    display: block;
    text-decoration: none;
}
.login-block > ul.login-nav > li > a:hover{
	color: #f78127;
}
.login-content {
    border: 1px solid #bbb;
    border-radius: 2px;
}
.input-style{
	width: 251px;
	margin: 15px 0 10px 50px;
}
.assist_fn{
	margin:5px 0 15px 50px;
	
}
.assist_fn span{
	display: flex;
}
.login-banner{
	 position: absolute;
    left: 50px;
    top: 40px;
    width: 500px;
    height: 489px;
}
.login-banner .banner-text{
	font-size: 26px;
	height: 54px;
	line-height: 54px;
	width: 100%;
}

.login-banner .banner-text:nth-child(2){
	font-weight: 600;
}

.banner-line{
	height: 103px;
	width: 100%;
}

.banner-line-boy{
	float: left;
	height: 5px;
	width: 230px;
	margin-top: 49px;
	background: #2e90e5;
	display: inline;
}

.banner-text-large{
	height: 104px;
	font-size:34px;
	line-height: 104px;
}

.banner-button{
	width: 100%;
	height: 60px;
	background: #2e90e5;
	color: #FFFFFF;
	line-height: 60px;
	text-align: center;
	margin-top: 33px;
	font-size: 33px;
	border-radius: 4px;
}
.tab-content {
    width: 380px;
    height: 450px;
}
.login-bottom {
    width: 378px;
    margin: 20px auto;
}
.wxwork-login-button {
    width: 378px;
    background-color: #0099FF;
    cursor: pointer;
}
/*.banner-style{
	width: 170px;
	float: left;
	line-height: 50px;
	overflow: hidden;
	margin-left: 26px;
}*/
/*.nav-client li{
	width: 200px;
	float: left;
}*/
    </style>
</head>
<body>
<div class="background">
    <img src="${ctx}/static/skins/default/img/login/bg.png">
</div>
<div class="login-header">
	<div class="login-header-middle">
		<div class="login-header-icon">
			<img src="static/skins/default/img/logo.png"/>
			<span class="login-header-text">闪电云</span>
		</div>
	</div>
</div>
<div class="body" style="min-height: 500px;">
    <div class="login-block" id="loginBlock">
        <div class="login-content">
            <ul class="login-nav">
                <%--
                            <li class="active"><a href="#account_login" aria-controls="account_login" role="tab" data-toggle="tab">账号密码登陆</a></li>
                            <li><a onclick="wxLogin()" href="#qrcode_login" aria-controls="qrcode_login" role="tab" data-toggle="tab"><i class="icons-login_block icon-account_login_code"></i>微信登录</a></li>
                --%>
            </ul>

            <div class="tab-content">
                <%--
                            <div role="tabpanel" id="account_login" class="tab-pane fade in active" >
                                <form class="form-con" action="${ctx}/login" method="post">
                                    <div class="input-style input-prepend name">
                                        <span class="add-on"><i class="icon-user icon-darkgray"></i></span>
                                        <input class="form-control" value="${enterpriseName}" type="text" id="enterpriseName" name="enterpriseName" maxlength=255 placeholder='<spring:message code="login.account.enterprise" />' />
                                    </div>
                                    <div class="input-style input-prepend name">
                                        <span class="add-on"><i class="icon-user icon-darkgray"></i></span>
                                        <input class="form-control" value="" type="text" id="username" name="username" maxlength=255 placeholder='<spring:message code="login.account.info" />' />
                                    </div>
                                    <div class="input-style input-append password">
                                        <span class="add-on"><i class="icon-lock icon-darkgray"></i></span>
                                        <input class="form-control" value="" type="password" id="password" name="password" maxlength="127" autocomplete="off" placeholder='<spring:message code="login.password" />' />
                                    </div>
                                    <input type="hidden" id="verycodeId" name="verycodeId" value="${verycodeId}"/>
                                    <div style="margin-left: 50px;" id="verify_code" class="input-group verify-code" style="display: none">
                                        <p class="verify_code_tip">请输入验证码</p>
                                        <span><img id="img_captcha" onclick="javascript:loadimage();" src="#"></span>
                                        <p class="verify_code_click">点击图片刷新</p>
                                        <input type="text" id="captcha" name="captcha"  maxlength="4" class="input-medium" placeholder='<spring:message code="verifycode.info"/>' />
                                    </div>
                                    <div class="assist_fn">
                                        <span>
                                            <input type="checkbox" id="rememberMe" name="rememberMe"/>记住我
                                        </span>
                                        <div class="forget-pwd" style="float: right;">
                                            <% if (isLocalAuth) { %>
                                            <a href="javascript:void(0);" onclick="gotoForgetPwd()"><spring:message code="anon.forgotpwd" /></a>
                                            <% } %>
                                        </div>
                                    </div>
                                    <input type="hidden" id="savedShrioStr" name="savedShrioStr" value="<%=saveRequestStr%>"/>
                                    <div class="login-submit row-fluid">
                                        <button style="margin-left: 110px;" type="submit" class="btn btn-large btn-primary pull-left"><spring:message code="login.submit" /></button>
                                    </div>
                                    <!--<div class="forget-pwd">
                                        <br/>
                                        <a style="margin-left: 50px;" href="${ctx}/syscommon/registerEnterprise"><spring:message code="register_guide" /></a>
                                    </div>-->
                                </form>
                            </div>
                --%>

                <div role="tabpanel" id="qrcode_login" class="fade in" >
                    <div style="margin-left: 25px;" class="qrcode" id="qrCodeDiv">
                    </div>
                    <div style="display: none;" class="qrcode_help">
                        <div><a href="" data-toggle="tooltip" >如何使用？</a></div>
                        <div class="around_box">使用手机APP扫描二维码登陆</div>
                    </div>
                </div>
            </div>
        </div>
        <div class="login-bottom">
            <div><span>已有企业微信：</span></div>
            <a href="<%=wwAuthUrl%>"><div class="wxwork-login-button"><img src="${ctx}/static/skins/default/img/wxwork_login_blue_middle.png"></div></a>
        </div>
    </div>

    <div class="banner login-banner">
        <!--<img src="${ctx}/static/skins/default/img/login/you_tu.png">-->
        <div class="banner-text banner-style">Information Age</div>
        <div class="banner-text banner-style">Master Data Management</div>
        <div class="banner-line">
        	<span class="banner-line-boy"></span>
        </div>
        <div class="banner-text-large banner-style">信息时代.数据管理专家</div>
        <div class="banner-text banner-style">用数据创造未来价值</div>
        <div class="banner-button banner-style">立即体验</div>
    </div>
</div>


<!--<div class="login-link">
	<ul class="nav-client">
            <li>
                <i class="icons-login_block icon-client_pc"></i>
                <span>下载PC客户端</span>
            </li>
            <li>
                <i class="icons-login_block icon-client_android"></i>
                <span>下载Android版</span>
            </li>
            <li>
                <i class="icons-login_block icon-client_iphone"></i>
                <span>下载iPhone版</span>
            </li>
        </ul>
</div>
<div class="loading-details">
	<div class="loading-details-header">闪电云不仅仅是云盘</div>
	
</div>-->
<%@ include file="../common/footer.jsp" %>
<div class="">
	
</div>
</body>
<script type="text/javascript">
    $(document).ready(function () {
        var errorMsg = null;
        <%
        String error = (String)request.getAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);
        if(error!=null) {
            if(error.contains(".LockedAccountException")) {
            %>
                errorMsg='<spring:message code="login.locked.prefix" />'+ ${lockWaitTip} +'<spring:message code="login.locked.suffix" />';
            <%
            }
            if(error.contains(".NoSuchEnterpriseException")) {
            %>
                errorMsg='<spring:message code="login.errorMsg.noEnterpriseExist" />';
            <%
            }
            if(error.contains(".IncorrectCredentialsException")||error.contains(".ADLoginAuthFailedException")) {
            %>
                errorMsg='<spring:message code="login.errorMsg.notExists" />';
            <%
            }
            if(error.contains(".AccountException")) {
            %>
                errorMsg='<spring:message code="login.errorMsg.idivalUser" />';
            <%
            }
            if(error.contains(".DisabledAccountException")) {
            %>
                errorMsg='<spring:message code="login.fail.network.forbid" />';
            <%
            }
            if(error.contains(".SecurityMartixException")) {
            %>
                errorMsg='<spring:message code="login.fail.security.forbidden" />';
            <%
            }
            if(error.contains(".NoCaptchaException")) {
            %>
                errorMsg='<spring:message code="verifycode.error.empty" />';
            <%
            }
            if(error.contains(".InvalidCaptchaException")) {
            %>
                errorMsg='<spring:message code="verifycode.error.invalid" />';
            <%
            }
        }
        %>

        /*
        if("${verycodeId}"){
            loadimage();
            document.getElementById("verify_code").style.display="";
        } else{
            document.getElementById("verify_code").style.display="none";
        }
        if(errorMsg!=null){
            if("${verycodeId}"){
                handlePrompt("error",errorMsg,49,205,null,"loginBlock");
            }else{
                handlePrompt("error",errorMsg,49,157,null,"loginBlock");
            }
        }
        */

        wxLogin();
    });

	function wwLogin(){
		//显示企业微信登录二维码
        var obj = new WxLogin({
            id:"qrCodeDiv",
            appid: "wx5a7777253cebf0a5",
            scope: "snsapi_login",
            redirect_uri: "<%=wwAuthUrl%>",
            state: "0",
            style: "black"
        });
	}

    function wxLogin(){
        //显示微信登录二维码
        var obj = new WxLogin({
            id:"qrCodeDiv",
            appid: "wx5a7777253cebf0a5",
            scope: "snsapi_login",
            redirect_uri: "<%=wxAuthUrl%>",
            state: "0",
            style: "black"
        });
    }

    function gotoForgetPwd(){
        window.location = "${ctx}/syscommon/enterforget";
    }

    function loadimage(){
        $("#img_captcha").attr("src","${ctx}/system/verifycode?uuid=" + "${verycodeId}&t=" + Math.random());
        $("#img_captcha").attr("title",'<spring:message code="anon.toChangecaptcha" />');
    }

    function checkName(){
        var username =  $("#username").val().replace(/^\s+|\s+$/g,"");
        if(username==null||username.length==0){
            return;
        }
        var url = "${ctx}/syscommon/checkName?username="+username;
        $.ajax({
            type: "GET",
            async: false,
            url:url,
            error: function() {
                handlePrompt("error",'<spring:message code="login.errorMsg.userNameInMoreEnterprise" />',49,157,null,"loginBlock");
                $("#username").val("");
            }
        });
    }
</script>
</html>