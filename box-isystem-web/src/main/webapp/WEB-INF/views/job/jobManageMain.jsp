<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="Cache-Control" content="no-cache" />
<meta http-equiv="Pragma" content="no-cache" />
<title><spring:message code="main.title" /></title>
<%@ include file="../common/common.jsp"%>
</head>

<body>
<%@ include file="../common/header.jsp"%>
<div class="body"  style="min-height: 267px;">
	<div class="body-con clearfix system-con">
    	<div class="tab-menu">
        	<div class="tab-menu-con">
            	<ul class="nav nav-tabs">
                	<li class="active"><a href="#none" id="innerJobManageLinkId" onClick="openInframe(this, '${ctx}/job/enterList','systemFrame')"><spring:message code="manage.title.job"/></a></li>
                </ul>
            </div>
        </div>
       	<iframe id="systemFrame" src="${ctx}/job/enterList" scrolling="no" frameborder="0"></iframe>
    </div>
</div>
<%@ include file="../common/footer.jsp"%>
</body>
</html>
<script type="text/javascript">
$(document).ready(function() {
	navMenuSelected("jobManageLinkId");
});
</script>
