<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
<%@ include file="../common/common.jsp"%>
</head>
<body>


<div class="body">
	<div class="body-con clearfix">
		<div class="page-error">
		    <h3><spring:message code="error.invalidRequest"/></h3>
		</div>
    </div>
</div>
</body>
</html>