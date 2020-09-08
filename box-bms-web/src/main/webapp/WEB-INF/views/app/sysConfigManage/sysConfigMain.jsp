<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
<%@ include file="../../common/common.jsp"%>
</head>
<body>
<%@ include file="../../common/header.jsp"%>
<div class="body">
	<div class="body-con clearfix system-con">
    	<div class="tab-menu">
        	<div class="tab-menu-con">
            	<ul class="nav nav-tabs">
                	<li class="active"><a href="#none" onClick="openInframe(this, '${ctx}/app/sysconfig/mailserver/load','systemFrame')"><spring:message code="appSysConfig.basicconfig"/> </a></li>
                    <li><a href="#none" onClick="openInframe(this, '${ctx}/app/sysconfig/syslog/load','systemFrame')"><spring:message code="appSysConfig.logo.config"/> </a></li>
                	<li><a href="#none" onClick="openInframe(this, '${ctx}/app/sysconfig/secconfig/load','systemFrame')"><spring:message code="appSysConfig.teamspace"/></a></li>
                	<li><a href="#none" onClick="openInframe(this, '${ctx}/app/sysconfig/clientManage','systemFrame')"><spring:message code="appSysConfig.clientManage"/></a></li>
                </ul>
            </div>
        </div>
       	<iframe id="systemFrame" src="${ctx}/app/sysconfig/basicConfig/load" scrolling="no" frameborder="0"></iframe>
    </div>
</div>
<%@ include file="../../common/footer.jsp"%>
</body>
</html>
<script type="text/javascript">
$(document).ready(function() {
	navMenuSelected("sysConfigManageId");
});
</script>