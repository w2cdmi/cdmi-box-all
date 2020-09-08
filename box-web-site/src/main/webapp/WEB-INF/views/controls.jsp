<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page
	import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<%@ page import="org.apache.shiro.authc.ExcessiveAttemptsException"%>
<%@ page import="org.apache.shiro.authc.IncorrectCredentialsException"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html>
<head>
<META HTTP-EQUIV="Expires" CONTENT="0">
<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
<META HTTP-EQUIV="Cache-control"
		CONTENT="no-cache, no-store, must-revalidate">
<META HTTP-EQUIV="Cache" CONTENT="no-cache">
<title><spring:message code="main.title" /></title>
<script src="//w3-beta.huawei.com/res/ws/js/jquery-1.10.2.js"
	type="text/javascript"></script>
<link href="//w3-beta.huawei.com/res/ws/css/controls1.10.2.css"
	rel="stylesheet" type="text/css">
<script src="//w3.huawei.com/res/ws/js/controls1.10.2.js"
	type="text/javascript"></script>
<link rel="shortcut icon" type="image/x-icon"
	href="${ctx}/static/skins/default/img/temp/logo.ico">
<script type="text/javascript">
	var nn = {};
	nn.idCallbacka = function(data) {
		alert("uid:" + data["uid"] + "\n" + "ucn:" + data["ucn"] + "\n"
				+ "dept:" + data["dept"]);
	};
	nn.check = function() {

	};
	$(document).ready(function(){
		$("#demo").keydown(function(e){
			if(e.which ==13){
				alert("enter");
			}
		});
	});
</script>
</head>
<% 
response.setHeader("Cache-Control","no-cache, no-store, must-revalidate");
response.setHeader("Pragma","no-cache");
response.setDateHeader("Expires",0);
%>

<body>
	<br>
	<br>
	<div id="demo" contenteditable="true"
		style="border:1px solid #666666;width:600px;height:150px;padding: 3px;"
		autocomplete="off" dataCallback="nn.idCallbacka"
		class="id_ldap invalid_input" rows="4" multi="true" keytype="all"
		type="text" url="ws/IdServlet" name="aotoA4" lang="eng">
		</div>
	<button type="button" onclick="nn.check()">Check</button>
</body>
</html>
