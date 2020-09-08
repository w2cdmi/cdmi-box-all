<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="../../common/common.jsp" %>
    <link href="${ctx}/static/skins/default/css/layout.css" rel="stylesheet" type="text/css"/>
</head>

<body>
<div id="pageLoadingContainer"
     style="z-index:10000; position:fixed; left:0; top:0; right:0; bottom:0; background:#fff;"></div>
<script type="text/javascript">new Spinner(optsBigSpinner).spin(document.getElementById("pageLoadingContainer"));</script>

<%@ include file="../../common/header.jsp" %>
<div class="body">
    <div class="body-con clearfix">
        <div class="tab-menu">
            <div class="tab-menu-con">
                <ul class="nav nav-tabs">
                    <li class="active"><a href="#none" onClick="openInframe(this, '${ctx}/user/account','systemFrame')"><spring:message
                            code="user.settings.userTitle"/></a></li>
                    <li><a href="#none"
                           onClick="openInframe(this, '${ctx}/user/terminal','systemFrame')"><spring:message
                            code="user.settings.terminal"/></a></li>
                    <li><a href="#none" onClick="openInframe(this, '${ctx}/user/group','systemFrame')"><spring:message
                            code='group.title.manager.group'/></a></li>
                    <li><a href="#none" onClick="openInframe(this, '${ctx}/user/doctype','systemFrame')"><spring:message
                            code='doctype.title.manager.doctype'/></a></li>
                </ul>
            </div>
        </div>
        <iframe id="systemFrame" src="${ctx}/user/account" scrolling="no" frameborder="0"></iframe>
    </div>
</div>
<%@ include file="../../common/footer.jsp" %>
</body>
<script type="text/javascript">
    $(document).ready(function () {
        loadSysSetting("${ctx}", '<spring:message code="common.language1" />');

        comboxRemoveLoading("pageLoadingContainer");
    });
</script>
</html>