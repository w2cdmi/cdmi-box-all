<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="Pragma" content="no-cache"/>
    <meta http-equiv="Cache-Control" content="no-cache"/>
    <meta http-equiv="Expires" content="0"/>
    <title><spring:message code="main.title"/></title>
    <link href="${ctx}/static/skins/default/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/static/skins/default/css/public.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/static/skins/default/css/main.css" rel="stylesheet" type="text/css"/>
    <%@ include file="../common/common.jsp" %>
    <script src="${ctx}/static/js/public/jquery-1.10.2.min.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/bootstrap.min.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/common.js" type="text/javascript"></script>
</head>
<body>

<%@ include file="linkHeader.jsp" %>

<div class="body">
    <div class="body-con clearfix body-con-no-menu">
        <div class="page-error">
            <h3><spring:message code="link.view.NodeNotExist"/></h3>
            <button class="btn btn-large" onclick="closeSelf()" type="button"><spring:message
                    code="button.close"/></button>
        </div>
    </div>
</div>

<%@ include file="../common/footer.jsp" %>

<script type="text/javascript">

    $(function () {
        loadSysSetting("${ctx}", '<spring:message code="common.language1" />');
        $("#linkHeadInfo").text("");
    });

    function closeSelf() {
        window.opener = null;
        window.open('', '_self');
        window.close();
    }
</script>
</body>
</html>