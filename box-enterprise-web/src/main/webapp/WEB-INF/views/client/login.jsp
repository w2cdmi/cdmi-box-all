<%@ page contentType="text/html;charset=UTF-8" %>
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
    <link rel="stylesheet" href="${ctx}/static/skins/default/css/loginNew.css" type="text/css"/>
    <script src="${ctx}/static/js/public/jquery-1.10.2.min.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/bootstrap.min.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/uaredirect.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/JQbox-hw-switch.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/common.js" type="text/javascript"></script>
    <script src="https://res.wx.qq.com/connect/zh_CN/htmledition/js/wxLogin.js"></script>
</head>
<body>
<!--头部-->
		<div class="login-header">
			<div class="pub-width login-logo">
				<a class="login-logo-bg" href="#"></a>
				<a class="login-logo-title" href="#">闪电云</a>
			</div>
		</div>
		<!--中间内容-->
		<div class="login-content">
			
			<!--中间左边信息-->
			<div class="login-midd-content pub-width">
				<div class="midd-left">
					<div class="midd-left-con">
						<p class="midd-text">Cooperate with WeChat</p>
						<p class="midd-text midd-text-upp">experience a relax and efficient work</p>
						<div class="midd-line">
							<span></span>
						</div>
						<p class="midd-text midd-special">携手企业微信 · 轻松高效工作</p>
						<p class="midd-text">致力于打造企业级云盘一流服务</p>
						<div class="midd-exper">
							<a href="https://www.storbox.cn/register/wxwork">立即体验</a>
						</div>
					</div>
				</div>
				<!--中间登录界面-->
				<div class="midd-right" id="loginBlock">
					<div role="tabpanel" id="qrcode_login" class="fade in midd-login">
						<div class="wx-img" id="qrCodeDiv">
						</div>
						<div class="bus-login-button">
							<a href="https://open.work.weixin.qq.com/wwopen/sso/3rd_qrConnect?appid=${wwAppId}&redirect_uri=${wwRedirectUrl}&state=0&usertype=member">
								<img src="${ctx}/static/skins/default/img/buslogin_01.png" alt="" />
								<span>企业微信登录</span>
							</a>
							
						</div>
					</div>
				</div>
			</div>
		</div>
</body>
<script type="text/javascript">
    $(document).ready(function () {
        wxLogin();
    });

    function wxLogin(){
        //显示微信登录二维码
        var obj = new WxLogin({
            id:"qrCodeDiv",
            appid: "${wxAppId}",
            scope: "snsapi_login",
            redirect_uri: "${wxRedirectUrl}",
            state: "0",
            style: "black"
        });
    }
</script>
</html>