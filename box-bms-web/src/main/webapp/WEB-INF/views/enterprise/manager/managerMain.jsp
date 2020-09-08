<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
<%@ include file="../../common/common.jsp"%>
<style type="text/css">
.tab-menu .nav-tabs li a { overflow: hidden; white-space: nowrap; max-width: 160px; text-overflow: ellipsis; margin-right:0; }
.tab-menu .nav-tabs li { overflow: hidden; margin-right:2px; max-width: 190px; }
</style>
</head>

<body>
<%@ include file="../../common/header.jsp"%>
<div class="body">
	<div class="body-con clearfix system-con">
    	<div class="tab-menu">
        	<div class="tab-menu-con">
            	<ul id="navigationTabsUl" class="nav nav-tabs">
                    <li class="active"><a href="#none" id="enterpriseManageTabId" onClick="openInframe(this, '${ctx}/enterprise/manager/list','systemFrame')" style="max-width:200px;"><spring:message code="enterpriseList.config"/></a></li>
                    <c:forEach items="${authAppListTabs }" var="authApp">
                    	<li><a href="#none" title="<c:out value='${authApp.authAppId }'/><spring:message code='enterprise.app.list'/>" onClick="openInframe(this, '${ctx}/enterprise/account/enterpriseAppList/<c:out value="${authApp.authAppId }"/>','systemFrame')"><c:out value='${authApp.authAppId }'/><spring:message code="enterprise.app.list"/></a></li>
                    </c:forEach>
                </ul>
            </div>
        </div>
       	<iframe id="systemFrame" src="${ctx}/enterprise/manager/list" scrolling="no" frameborder="0"></iframe>
    </div>
</div>
<%@ include file="../../common/footer.jsp"%>
</body>
</html>
<script type="text/javascript">

$(document).ready(function(){
	navMenuSelected("enterpriseManageId");
});

</script>