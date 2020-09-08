<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
    <link href="${ctx}/static/skins/default/css/layout.css" rel="stylesheet" type="text/css"/>
</head>
<body>

<div class="body">
    <div class="body-con clearfix">
        <div class="page-error">
            <c:if test="${exceptionName=='NoSuchTeamSpace' }"><h3><spring:message code="teamSpace.error.NoFound"/></h3>
            </c:if>
            <c:if test="${exceptionName=='Forbidden' }"><h3><spring:message code="teamSpace.error.Forbidden"/></h3>
            </c:if>

            <button class="btn btn-large" onclick="goBack()" type="button"><spring:message code="button.back"/></button>
        </div>
    </div>
</div>

<script type="text/javascript">
    function goBack() {
        history.go(-1);
    }
</script>
</body>
</html>