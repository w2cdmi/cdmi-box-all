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
                    <li class="active"><a href="#none" onClick="openInframe(this, '${ctx}/sys/sysconfig/syslog/load','systemFrame')"><spring:message code="sysConfigMain.setLog"/> </a></li>
                	<li><a href="#none" onClick="openInframe(this, '${ctx}/sys/sysconfig/access/load','systemFrame')"><spring:message code="sysConfigMain.access"/></a></li>
                	<li><a href="#none" onClick="openInframe(this, '${ctx}/sys/sysconfig/statistics/accesskey/load','systemFrame')"><spring:message code="sysConfigMain.statisctics"/></a></li>
                	<li><a href="#none" onClick="openInframe(this, '${ctx}/sys/sysconfig/loginconfig/load','systemFrame')"><spring:message code="login.config"/></a></li>
                </ul>
            </div>
        </div>
       	<iframe id="systemFrame" src="${ctx}/sys/sysconfig/syslog/load" scrolling="no" frameborder="0"></iframe>
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