<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="../common/common.jsp" %>
    <link href="${ctx}/static/skins/default/css/layout.css" rel="stylesheet" type="text/css"/>
</head>
<body>

<%@ include file="../common/header.jsp" %>

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

<%@ include file="../common/footer.jsp" %>

<script type="text/javascript">
    $(function () {
        loadSysSetting("${ctx}", '<spring:message code="common.language1" />');
    })
    function goBack() {
        history.go(-1);
    }
</script>
</body>
</html>