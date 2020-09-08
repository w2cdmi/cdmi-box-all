<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
    <title><spring:message code="main.title"/></title>
    <%@ include file="../common/include.jsp" %>
</head>
<body ontouchstart>
<div class="box">
    <%@ include file="../common/footer3.jsp" %>
</div>
</body>
</html>