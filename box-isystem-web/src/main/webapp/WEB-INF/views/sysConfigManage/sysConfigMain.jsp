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
<div class="body">
	<div class="body-con clearfix system-con">
    	<div class="tab-menu">
        	<div class="tab-menu-con">
            	<ul class="nav nav-tabs">
            		<li class="active"><a href="#none" onClick="openInframe(this, '${ctx}/sysconfig/syslog/loglanguage','systemFrame')"><spring:message code="log.language.title"/></a></li>
                	<li><a href="#none" onClick="openInframe(this, '${ctx}/sysconfig/syslog/load','systemFrame')"><spring:message code="sysconfig.syslog"/> </a></li>
                	<li><a href="#none" onClick="openInframe(this, '${ctx}/sysconfig/logagentconfig/-1','systemFrame')"><spring:message code="syscofig.log.pigeonhole.parameter"/> </a></li>
                	<li ><a href="#none" onClick="openInframe(this, '${ctx}/sysconfig/direct/load','systemFrame')"><spring:message code="sysconfig.amylose"/></a></li>
                	<li ><a href="#none" onClick="openInframe(this, '${ctx}/sysconfig/messageConfig','systemFrame')"><spring:message code="sysconfig.message.config"/></a></li>
                	<li ><a href="#none" onClick="openInframe(this, '${ctx}/sysconfig/lockConfig','systemFrame')"><spring:message code="sysconfig.lock.config"/></a></li>
                	<li ><a href="#none" onClick="openInframe(this, '${ctx}/statisticsmanage/statistics/list','systemFrame')"><spring:message code="statistics.title.manager"/></a></li>
                </ul>
            </div>
        </div>
       	<iframe id="systemFrame" src="${ctx}/sysconfig/syslog/loglanguage" scrolling="no" frameborder="0"></iframe>
    </div>
</div>
<%@ include file="../common/footer.jsp"%>
</body>
</html>
<script type="text/javascript">
$(document).ready(function() {
	navMenuSelected("configManageLinkId");
	loadSysSetting("${ctx}");
});
</script>