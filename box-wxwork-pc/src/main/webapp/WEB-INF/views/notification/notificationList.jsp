<!-- <%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="../common/include.jsp" %>
    <link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/notification/notificationList.css"/>
    <title>发现</title>
</head>
<body ontouchstart>
<div class="box">
	<ul class="find-middle">
		<li class="find-lnbox" onclick="gotoPage('${ctx}/share/shareLinks')">
			<div class="find-img"><img src="${ctx}/static/skins/default/img/find-lnbox.png"/></div>
			<div class="find-content">
				<h1>收件箱</h1>
				<p>您收到的文件将放在这里</p>
			</div>
			<div class="find-number">
				<span>99+</span>
			</div>
		</li>
		<li class="find-share" onclick="gotoPage('${ctx}/shared')">
			<div class="find-img"><img src="${ctx}/static/skins/default/img/find-share.png"/></div>
			<div class="find-content">
				<h1>收到的共享</h1>
				<p>您收到的共享将放在这里</p>
			</div>
			<div class="find-number">
				<span>50</span>
			</div>
		</li>
		<li class="find-examine" onclick="gotoPage('${ctx}/share/linkApproveList')">
			<div class="find-img"><img src="${ctx}/static/skins/default/img/find-examine.png"/></div>
			<div class="find-content">
				<h1>审批</h1>
				<p>您需要审批的外发文件将放在这里</p>
			</div>
			<div class="find-number">
				<span>13</span>
			</div>
		</li>
	</ul>
    <%@ include file="../common/footer3.jsp" %>
</div>
</body>
</html> -->
