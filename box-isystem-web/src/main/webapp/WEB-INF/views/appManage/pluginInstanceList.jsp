<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="cse" uri="http://cse.huawei.com/custom-function-taglib"%>  
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page import="com.huawei.sharedrive.isystem.util.CSRFTokenManager"%>
<%@ page import="java.util.Date"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%
request.setAttribute("token", CSRFTokenManager.getTokenForSession(session));
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="Cache-Control" content="no-cache" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="X-UA-Compatible" content="IE=10" />
<meta http-equiv="X-UA-Compatible" content="IE=9" />
<meta http-equiv="X-UA-Compatible" content="IE=8" />
<title></title>
<link href="${ctx}/static/skins/default/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/static/skins/default/css/public.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/static/skins/default/css/main.css" rel="stylesheet" type="text/css" />

<script src="${ctx}/static/js/public/jquery-1.10.2.min.js" type="text/javascript"></script>
<script src="${ctx}/static/js/public/bootstrap.min.js" type="text/javascript"></script>
<script src="${ctx}/static/js/public/common.js" type="text/javascript"></script>
<script src="${ctx}/static/js/public/JQbox-hw-page.js" type="text/javascript"></script>
<%@ include file="../common/common.jsp"%>
</head>
<body>
<div class="sys-content">
	<div class="pull-left">
				<label class="control-label" for=""><h5>
									<a class="return" href="${ctx}/pluginServer/pluginServerCluster/list">${cse:htmlEscape(appId)}</a>&nbsp&gt&nbsp
									<a class="return" href="${ctx}/pluginServer/pluginServerCluster/listPluginServer?appId=${cse:htmlEscape(appId)}">${cse:htmlEscape(name)}</a>&nbsp&gt&nbsp
									<spring:message code="plugin.server.serviceManger"/>
					</h5></label>
	</div>
	<div class="pull-right">
				<a class="return" href="${ctx}/pluginServer/pluginServerCluster/listPluginServer?appId=${cse:htmlEscape(appId)}">&lt;&lt;<spring:message
						code="common.back" /></a>
	</div>
</div>
<div class="sys-content">
    <div class="table-con">
        <table  class="table table-bordered table-striped">
          <thead>
            <tr>
                <th><spring:message code="plugin.service.instance"/></th>
                <th style="width:30%;"><spring:message code="license.ip"/></th>
                <th style="width:30%;"><spring:message code="plugin.lastMonitorTime"/></th>
                <th style="width:10%;"><spring:message code="common.status"/></th>
            </tr>
          </thead>
          <tbody >
         <c:forEach items="${instances}" var="instance">
            <tr >
                <td title="${cse:htmlEscape(instance.name)}">${cse:htmlEscape(instance.name)}</td>
                <td title="${cse:htmlEscape(instance.ip)}">${cse:htmlEscape(instance.ip)}</td>
                <td>
               	 	<fmt:formatDate value="${instance.lastMonitorTime}" pattern="yyyy-MM-dd HH:mm"/>
                </td>
                <td <c:if test="${instance.state>=0}">title="<spring:message code="common.normal"/>"</c:if>
                    <c:if test="${instance.state==-1}"> title="<spring:message code="common.exception"/>"</c:if>>
                	<c:if test="${instance.state>=0}"><img src="${ctx}/static/image/state/status_green.png" alt="<spring:message code="common.normal"/>"/></c:if>
                    <c:if test="${instance.state==-1}"><img src="${ctx}/static/image/state/status_red.png" alt="<spring:message code="common.exception"/>"/></c:if>		
                </td>
            </tr>
            </c:forEach>
          </tbody>
        </table>
    </div>
</div>
</body>
<script type="text/javascript">
$(function(){
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
})

function refreshWindow() {
	window.location.reload();
}
</script>
</html>
