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
			<h5 class="pull-left" style="margin: 3px 0 0 4px;"><a href="${ctx}/app/appmanage/authapp/list">${appId}</a>&nbsp;>&nbsp;<spring:message code="appSysConfig.app.config"/></h5>	
		</div>
		<ul class="nav nav-tabs clearfix">
	    	<li><a class="return" href="${ctx}/app/basicconfig/config/${appId}"><spring:message code="appSysConfig.basicconfig"/> </a></li>
	    	<c:if test="${appType == 1}">
	        <li><a class="return" href="${ctx}/app/logo/config/${appId}"><spring:message code="appSysConfig.logo.config"/> </a></li>
	         </c:if>
	    	<c:if test="${appType == 1}">
	    	<li><a class="return" href="${ctx}/app/clientManage/config/${appId}"><spring:message code="appSysConfig.clientManage"/></a></li>
	    	 </c:if>
	    	 <li class="active"><a href="${ctx}/admin/declaration/config/${appId}"><spring:message code="conceal.declaration"/> </a></li>
	     	<c:if test="${appType == 1}">
	     	<li><a class="return" href="${ctx}/app/backup/config/${appId}"><spring:message code="appSysConfig.backupManage"/> </a></li>
	     	</c:if>
	    </ul>
	    <div class="table-con">
			<table class="table table-bordered table-striped">
				<thead>
		            <tr>
		                <th width="15%"><spring:message code="common.type"/></th>
		                <th width="15%"><spring:message code="appList.appId"/></th>
		                <th><spring:message code="conceal.declaration"/></th>
		                <th width="15%"	><spring:message code="common.updateTime"/></th>
		                <th width="15%"><spring:message code="authorize.operation"/></th>
		            </tr>
		       </thead>
		       <tbody>
		          <c:forEach items="${declarationList}" var="declaration">
		          <c:if test="${declaration.clientType != 'Pccloud' && declaration.clientType !=null}">
		            <tr>
		                <td title="${cse:htmlEscape(declaration.clientType)}">${cse:htmlEscape(declaration.clientType)}</td>
		                <td title="${cse:htmlEscape(declaration.appId)}">${cse:htmlEscape(declaration.appId)}</td>
		                <td title="${cse:htmlEscape(declaration.declaration)}">
			                <c:if test="${null == declaration.declaration || declaration.declaration.isEmpty()}">
			                	-
			                </c:if>
			                <c:if test="${null != declaration.declaration || !declaration.declaration.isEmpty()}">
			                	${cse:htmlEscape(declaration.declaration)}
			                </c:if>
		                </td>
		                <td>
		                	<c:if test="${null == declaration.createAt}">
		                	</c:if>
		                	<c:if test="${null != declaration.createAt}">
		                		<fmt:formatDate value="${declaration.createAt}" pattern="yyyy-MM-dd HH:mm:ss"/>
		                	</c:if>
		                </td>
		                <td >
			                <div class="pull-left">
							<button type="button" class="btn btn-small btn-primary"
								onClick="uploadClient('${declaration.clientType}','${declaration.appId }')">
								<spring:message code="conceal.declaration.update"/>
							</button>
							</div>
						</td>
		            </tr>
		            </c:if>
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

function uploadClient(type,appId){
	top.ymPrompt.win({
		message:'${ctx}/admin/declaration/update/'+type+'/'+appId,width:750,height:510,
		title:'<spring:message code="conceal.declaration"/>', iframe:true,
		btn:[['<spring:message code="common.save"/>','yes',false,"btn-focus"],['<spring:message code="common.cancel"/>','no',true,"btn-cancel"]],handler:goUpdate
	});
	top.ymPrompt_addModalFocus("#btn-focus");
}

function goUpdate(tp) {
	if (tp == 'yes') {
		top.ymPrompt.getPage().contentWindow.submitUpdateDeclaration();
	} else {
		top.ymPrompt.close();
	}
}

</script>
</html>
