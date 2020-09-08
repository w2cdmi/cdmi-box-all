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
<%@ include file="../common/common.jsp"%>
</head>

<body>
<%@ include file="../common/header.jsp"%>
<div class="body"  style="min-height: 267px;">
	<div class="body-con clearfix system-con">
    	<div class="tab-menu">
        	<div class="tab-menu-con">
            	<ul class="nav nav-tabs">
                	<li class="active"><a href="#none" id="copyPolicyListId" onClick="openInframe(this, '${ctx}/mirror/copyPolicy/list','systemFrame')"><spring:message code="copyPolicy.policy.title"/></a></li>
                	<li ><a href="#none" id="morrirId" onClick="openInframe(this, '${ctx}/mirror/copyTask/list/-1/policy','systemFrame')"><spring:message code="copyPolicy.mirror.title"/></a></li>
                </ul>
            </div>
        </div>
       	<iframe id="systemFrame" src="${ctx}/mirror/copyPolicy/list" scrolling="no" frameborder="0"></iframe>
    </div>
</div>
<%@ include file="../common/footer.jsp"%>
</body>
</html>
<script type="text/javascript">
$(document).ready(function() {
	navMenuSelected("copyPolicyId");
	loadSysSetting("${ctx}");
});
</script>
