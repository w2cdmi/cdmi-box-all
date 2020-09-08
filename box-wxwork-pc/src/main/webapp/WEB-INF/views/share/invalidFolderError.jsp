<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="javax.sound.midi.SysexMessage" %>
<%@page import="pw.cdmi.box.disk.utils.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ page import="pw.cdmi.box.disk.utils.*" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<% request.setAttribute("token", CSRFTokenManager.getTokenForSession(session)); %>
<!DOCTYPE html>
<html>
<head>
	<link rel="stylesheet" href="https://at.alicdn.com/t/font_234130_nem7eskcrkpdgqfr.css">
	<link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/share/linkIndex.css?v=${version}"/>
	<link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/linkMain.css?v=${version}"/>
	<link rel="stylesheet" type="text/css" href="${ctx}/static/css/default/magic-input.min.css"/>
	<script src="${ctx}/static/components/schedule.js?v=${version}"></script>
	<title>无效的目录</title>
</head>
<body>

<div class="body">
	<div class="body-con clearfix body-con-no-menu">
		<div class="page-error">
			<h2>目录不存在</h2>
		</div>
    </div>
</div>
</body>
</html>