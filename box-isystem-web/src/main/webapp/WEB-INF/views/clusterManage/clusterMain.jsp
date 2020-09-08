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
            		<li class="active"><a href="#none" id="uasNodeMenuId"  onClick="openInframe(this, '${ctx}/cluster/uasNode/list','systemFrame')"><spring:message code="clusterManage.uasNodeList"/></a></li>
                    <li ><a href="#none" id="regionManageMenuId"  onClick="openInframe(this, '${ctx}/cluster/region/list','systemFrame')"><spring:message code="clusterManage.regionList"/></a></li>
                    <li><a href="#none" id="dnsServerMenuId"  onClick="openInframe(this, '${ctx}/cluster/dnsserver/list','systemFrame')"><spring:message code="clusterManage.dnsConfig"/></a></li>
                    <li><a href="#none" id="intranetMenuId"  onClick="openInframe(this, '${ctx}/cluster/intranet/list','systemFrame')"><spring:message code="clusterManage.intranet"/></a></li>
                </ul>
            </div>
        </div>
       	<iframe id="systemFrame" src="${ctx}/cluster/uasNode/list" scrolling="no" frameborder="0"></iframe>
    </div>
</div>
<%@ include file="../common/footer.jsp"%>
</body>
</html>
<script type="text/javascript">
$(document).ready(function() {
	navMenuSelected("clusterManageLinkId");
	loadSysSetting("${ctx}");
});
</script>