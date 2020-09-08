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
<%@ include file="../anon/configHeader.jsp"%>
<div class="body">
	<div class="body-con clearfix system-con">
        <div class="breadcrumb">
        	<div class="breadcrumb-con">
        		<p id="breadcrumbText"></p>
        	</div>
        </div>
       	<iframe id="systemFrame" src="" scrolling="no" frameborder="0" style="min-height: 461px;"></iframe>
    </div>
</div>
<%@ include file="../common/footer.jsp"%>
</body>
</html>
<script type="text/javascript">
$(document).ready(function() {
	navMenuSelected("clusterManageLinkId");
	loadSysSetting("${ctx}");
});
</script>