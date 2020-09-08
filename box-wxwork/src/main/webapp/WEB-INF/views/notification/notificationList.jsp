<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html>
<head>
<%@ include file="../common/include.jsp"%>
<link rel="stylesheet" type="text/css"	href="${ctx}/static/skins/default/css/notification/notificationList.css" />
<title>发现</title>
</head>
<body>
	<div class="box" style="position: fixed;bottom: 3rem;left: 0;right: 0;top: 0;">
		<ul class="find-middle">
			<li class="find-lnbox" onclick="gotoPage('${ctx}/share/shareLinks')">
				<div class="find-img">
					<img src="${ctx}/static/skins/default/img/find-lnbox.png" />
				</div>
				<div class="find-content">
					<h1>收件箱</h1>
					<p>您收到的文件将放在这里</p>
				</div>
			</li>
			<li class="find-share" onclick="gotoPage('${ctx}/shared')">
				<div class="find-img">
					<img src="${ctx}/static/skins/default/img/find-share.png" />
				</div>
				<div class="find-content">
					<h1>收到的共享</h1>
					<p>您收到的共享将放在这里</p>
				</div>
			</li>
			<%--<li class="find-examine"--%>
				<%--onclick="gotoPage('${ctx}/share/linkApproveList')">--%>
				<%--<div class="find-img">--%>
					<%--<img src="${ctx}/static/skins/default/img/find-examine.png" />--%>
				<%--</div>--%>
				<%--<div class="find-content">--%>
					<%--<h1>审批</h1>--%>
					<%--<p>您需要审批的外发文件将放在这里</p>--%>
				<%--</div>--%>
			<%--</li>--%>

			<%--<li class="find-examine" id="audit"--%>
				<%--onclick="gotoPage('${ctx}/share/linkAuditList')"--%>
				<%--style="display: none">--%>
				<%--<div class="find-img">--%>
					<%--<img src="${ctx}/static/skins/default/img/find-examine.png" />--%>
				<%--</div>--%>
				<%--<div class="find-content">--%>
					<%--<h1>审计</h1>--%>
					<%--<p>您需要审批的外发文件将放在这里</p>--%>
				<%--</div>--%>
			<%--</li>--%>

		</ul>
		<%@ include file="../common/footer3.jsp"%>
	</div>

	<script type="text/javascript">
		<%--function checkUserIsSecurityManager(link) {--%>
			<%--$.ajax({--%>
				<%--type : "GET",--%>
				<%--url : host+"/ecm/api/v2/users/checkUserIsSecurityManager",--%>
				<%--error : handleError,--%>
				<%--success : function(data) {--%>
					<%--if(data){--%>
						<%--$("#audit").css("display","block");--%>
					<%--}--%>
				<%--}--%>
			<%--});--%>
		<%--}--%>
	</script>


</body>
</html>
