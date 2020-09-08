<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
<%@ include file="../common/common.jsp"%>
</head>

<body>
<%@ include file="../common/header.jsp"%>
<div class="body">
	<div class="body-con clearfix system-con">
    	<div class="tab-menu">
        	<div class="tab-menu-con">
            	<ul class="nav nav-tabs">
                    <li class="active"><a href="#none" id="feedBackOpenTabId" onClick="openInframe(this, '${ctx}/feedback/uam/open','feedbackFrame')"><spring:message code="feedback.manage.open"/></a></li>
                	<li ><a href="#none" onClick="openInframe(this, '${ctx}/feedback/uam/close','feedbackFrame')"><spring:message code="feedback.manage.close"/></a></li>
                </ul>
            </div>
        </div>
       	<iframe id="feedbackFrame" src="${ctx}/feedback/uam/open" scrolling="no" frameborder="0" style="height:980px;"></iframe>
    </div>
</div>
<%@ include file="../common/footer.jsp"%>
</body>
</html>
<script type="text/javascript">
$(document).ready(function() {
	navMenuSelected("feedBackManageId");
});
</script>