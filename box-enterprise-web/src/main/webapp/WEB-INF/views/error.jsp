<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <META HTTP-EQUIV="Expires" CONTENT="0">
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <META HTTP-EQUIV="Cache-control"
          CONTENT="no-cache, no-store, must-revalidate">
    <META HTTP-EQUIV="Cache" CONTENT="no-cache">
    <title><spring:message code="main.title"/></title>
    <link href="${ctx}/static/skins/default/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/static/skins/default/css/public.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/static/skins/default/css/main.css" rel="stylesheet" type="text/css"/>

    <script src="${ctx}/static/js/public/jquery-1.10.2.min.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/bootstrap.min.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/common.js" type="text/javascript"></script>
</head>
<body>
<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);
    String context = request.getContextPath();
    String redirectFlag = (String) request.getAttribute("redirect");
    String redirect = org.springframework.web.util.HtmlUtils.htmlEscape(redirectFlag);
%>

<div class="body">
    <div class="body-con clearfix body-con-no-menu">
        <c:if test="${empty redirect}">
        <div class="page-error">
            <h3><spring:message code="error.invalidRequest"/></h3>
            </c:if>
        </div>
    </div>
</div>
</body>
<script type="text/javascript">
    $(document).ready(function () {
        var errorMsg = "<%=redirect%>";
        if (errorMsg != null && errorMsg != "null") {
            location.href = "<%=context%>/login";
        }
    });
</script>
</html>
