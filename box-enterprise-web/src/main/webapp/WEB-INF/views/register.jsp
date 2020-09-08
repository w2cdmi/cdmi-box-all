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

    <script src="${ctx}/static/js/public/jquery-1.10.2.min.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/bootstrap.min.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/common.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/uaredirect.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/JQbox-hw-switch.js" type="text/javascript"></script>
    <%
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setHeader("Access-Control-Allow-Origin", "*");
    %>

    <script type="text/javascript">
			
    </script>
</head>
<body class="bodys">
<div class="background backgrounds">
    <img src="${ctx}/static/skins/default/img/login/bg.png">
</div>
<div class="login-header">
	<div class="login-header-middle login-header-middles">
		<div class="login-header-icon">
			<img src="${ctx}/static/skins/default/img/logo.png"/>
			<span class="login-header-text">闪电云盘</span>
		</div>
	</div>
</div>
<div class="register-qrcode body">
	<div class="register-qrcode-left">
		<div class="register-qrcode-header">已安装企业微信用户, 请点击下方链接注册</div>
		<div class="register-qrcode-middle">
			<a target="_blank" href="https://open.work.weixin.qq.com/3rdapp/install?suite_id=${suiteId}&pre_auth_code=${preauthCode}&redirect_uri=${redirectUrl}&state=0">
				<!--<img src="${ctx}/static/skins/default/img/wxwork_register_blue_middle.png">-->
			</a>
		</div>
		<div class="register-explain">企业微信用户注册</div>
	</div>
	<div class="register-qrcode-right">
		<div class="register-qrcode-header">未安装企业微信用户, 请点击下方链接注册</div>
		<div class="register-qrcode-middle">
			<a target="_blank" href="https://open.work.weixin.qq.com/3rdservice/wework/register?register_code=${registerCode}">
				<!--<img src="${ctx}/static/skins/default/img/wxwork_register_white_big.png">-->
			</a>
		</div>
		<div class="register-explain">非企业微信用户注册</div>
	</div>
</div>

<%@ include file="./common/footer.jsp" %>
</body>
</html>