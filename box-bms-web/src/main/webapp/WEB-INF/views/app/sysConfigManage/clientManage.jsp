<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cse" uri="http://cse.huawei.com/custom-function-taglib"%>  
<%@ page import="pw.cdmi.box.uam.util.CSRFTokenManager"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%
request.setAttribute("token", CSRFTokenManager.getTokenForSession(session));
%>
<!DOCTYPE html>
<html>
<head>
<%@ include file="../../common/common.jsp"%>
</head>
<body>
<div class="sys-content">
		<div class="clearfix control-group">
			<a  class="return btn btn-small pull-right" href="${ctx}/app/appmanage/authapp/list"><i class="icon-backward"></i>&nbsp;<spring:message code="common.back"/></a>
			<h5 class="pull-left" style="margin: 3px 0 0 4px;"><a href="${ctx}/app/appmanage/authapp/list"><c:out value='${appId}'/></a>&nbsp;>&nbsp;<spring:message code="appSysConfig.app.config"/></h5>	
		</div>
		<ul class="nav nav-tabs clearfix">
	    	<li><a class="return" href="${ctx}/app/basicconfig/config/<c:out value='${appId}'/>"><spring:message code="appSysConfig.basicconfig"/> </a></li>
	    	<c:if test="${appType == 1}">
	        <li><a class="return" href="${ctx}/app/logo/config/<c:out value='${appId}'/>"><spring:message code="appSysConfig.logo.config"/> </a></li>
	         </c:if>
	    	<c:if test="${appType == 1}">
	    	<li class="active"><a class="return" href="${ctx}/app/clientManage/config/<c:out value='${appId}'/>"><spring:message code="appSysConfig.clientManage"/></a></li>
	    	 </c:if>
	    	 <li><a class="return" href="${ctx}/admin/declaration/config/${appId}"><spring:message code="conceal.declaration"/> </a></li>
	     	<c:if test="${appType == 1}">
	     	<li><a class="return" href="${ctx}/app/backup/config/${appId}"><spring:message code="appSysConfig.backupManage"/> </a></li>
	     	</c:if>
	    </ul>
		<div class="clearfix">
			<div class="pull-left">
				<button type="button" class="btn btn-small btn-primary"
					onClick="uploadClient('<c:out value="${appId}"/>')">
					<i class="icon-plus"></i> <spring:message code="teamSpace.label.upload"/>
				</button>
			</div>
	    </div>	
	    <div class="table-con">
			<table class="table table-bordered table-striped">
				<thead>
		            <tr>
		                <th width="20%"><spring:message code="clientManage.packageName"/></th>
		                <th width="10%"><spring:message code="common.type"/></th>
		                <th><spring:message code="clientManage.versionNumber"/></th>
		                <th><spring:message code="clientManage.size"/></th>
		                <th width="13%"	><spring:message code="clientManage.matchingSystem"/></th>
		                <th width="13%"><spring:message code="clientManage.releaseDate"/></th>
		            </tr>
		       </thead>
		       <tbody>
		          <c:forEach items="${clients}" var="client">
		            <tr>
		                <td title="${cse:htmlEscape(client.fileName)}">${cse:htmlEscape(client.fileName)}</td>
		                <td title="${cse:htmlEscape(client.type)}"><c:out value='${client.type}'/></td>
		                <td title="${cse:htmlEscape(client.version)}">${cse:htmlEscape(client.version)}</td>
		                <td title="${cse:byteToMBString(client.size)}">${cse:byteToMBString(client.size)}</td>
		                <td title="${cse:htmlEscape(client.supportSys)}">${cse:htmlEscape(client.supportSys)}</td>
		                <td><fmt:formatDate value="${client.releaseDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
		            </tr>
		            </c:forEach>
		       </tbody>
			</table>
	</div>
</div>
</body>

<script type="text/javascript">
$(document).ready(function() {
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
});

function uploadClient(appId){
	top.ymPrompt.win({
		message:'${ctx}/app/clientManage/goUpload/'+appId,width:680,height:520,
		title:'<spring:message code="clientManage.upload"/>', iframe:true,
		btn:[['<spring:message code="common.save"/>','yes',false,"btn-focus"],['<spring:message code="common.cancel"/>','no',true,"btn-cancel"]],handler:goUpload
	});
	top.ymPrompt_addModalFocus("#btn-focus");
}

function goUpload(tp) {
	if (tp == 'yes') {
		top.ymPrompt.getPage().contentWindow.submitClientUpload();
	} else {
		top.ymPrompt.close();
	}
}

</script>
</html>
