<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ page import="pw.cdmi.box.uam.adminlog.dao.impl.LogLanguageHelper"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
<%@ include file="../../common/common.jsp"%>
</head>
<%
Boolean saveUserOperateLog = LogLanguageHelper.isSaveUserLog();
%>
<body>
<%@ include file="../../common/header.jsp"%>
<div class="body">
	<div class="body-con clearfix system-con">
    	<div class="tab-menu">
        	<div class="tab-menu-con">
            	<ul class="nav nav-tabs">
                    <li class="active"><a href="#none" onClick="openInframe(this, '${ctx}/app/adminlog/log/list','systemFrame')"><spring:message  code="log.manage.operation"  /></a></li>
                    <%
                	if(saveUserOperateLog){
                	%>
                    <li><a href="#none" onClick="openInframe(this, '${ctx}/app/adminlog/log/user','systemFrame')"><spring:message  code="log.manage.business"  /></a></li>
                    <li><a href="#none" onClick="openInframe(this, '${ctx}/app/adminlog/log/accountlog','systemFrame')"><spring:message  code="log.manage.account"  /></a></li>
                	<%}%>
                	<li><a href="#none" onClick="openInframe(this, '${ctx}/enterprise/adminstratorlog/enterprisrlist','systemFrame')"><spring:message  code="enterprise.manager.operation.log"  /></a></li>
                </ul>
            </div>
        </div>
       	<iframe id="systemFrame" src="${ctx}/app/adminlog/log/list" scrolling="no" frameborder="0"></iframe>
    </div>
</div>
<%@ include file="../../common/footer.jsp"%>
</body>
</html>
<script type="text/javascript">
$(document).ready(function() {
	navMenuSelected("appLogManageId");
});
</script>