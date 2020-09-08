<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="cse"
	uri="http://cse.huawei.com/custom-function-taglib"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page import="com.huawei.sharedrive.isystem.util.CSRFTokenManager"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<%
    request.setAttribute("token",
					CSRFTokenManager.getTokenForSession(session));
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="Cache-Control" content="no-cache" />
<meta http-equiv="X-UA-Compatible" content="IE=10" />
<meta http-equiv="X-UA-Compatible" content="IE=9" />
<meta http-equiv="X-UA-Compatible" content="IE=8" />
<META HTTP-EQUIV="Expires" CONTENT="0">
<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
<META HTTP-EQUIV="Cache-control"
	CONTENT="no-cache, no-store, must-revalidate">
<META HTTP-EQUIV="Cache" CONTENT="no-cache">
<title></title>
<link href="${ctx}/static/skins/default/css/bootstrap.min.css"
	rel="stylesheet" type="text/css" />
<link href="${ctx}/static/skins/default/css/public.css" rel="stylesheet"
	type="text/css" />
<link href="${ctx}/static/skins/default/css/main.css" rel="stylesheet"
	type="text/css" />

<script src="${ctx}/static/js/public/jquery-1.10.2.min.js"
	type="text/javascript"></script>
<script src="${ctx}/static/js/public/bootstrap.min.js"
	type="text/javascript"></script>
<script src="${ctx}/static/js/public/common.js" type="text/javascript"></script>
<script src="${ctx}/static/js/public/JQbox-hw-page.js"
	type="text/javascript"></script>
</head>
<body>
	<%
	    response.setHeader("Cache-Control",
						"no-cache, no-store, must-revalidate");
				response.setHeader("Pragma", "no-cache");
				response.setDateHeader("Expires", 0);
	%>
	<div class="sys-content">
		<div class="alert">
			<i class="icon-lightbulb icon-orange"></i>
			<spring:message code="app.manageApp" />
		</div>
		<div class="clearfix">
			<div class="pull-left">
				<button type="button" class="btn btn-primary btn-small"
					onClick="createApp()">
					<i class="icon-add"></i>
					<spring:message code="common.create" />
				</button>
			</div>
		</div>
		<div class="table-con">
			<table class="table table-bordered table-striped">
				<thead>
					<tr>
						<th><spring:message code="app.ID" /></th>
						<th><spring:message code="app.certification.addr" /></th>
						<th style="width:14%;"><spring:message code="app.create.time" /></th>
						<th style="width:14%;"><spring:message
								code="app.modiffy.time" /></th>
						<th style="width:25%;"><spring:message
								code="common.operation" /></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${authAppList}" var="authApp">
						<c:if test="${authApp.createBy == createBy}">
							<tr>
								<td title="<c:out value='${authApp.authAppId}'/>"><c:out
										value='${authApp.authAppId}' /></td>
								<td title="${cse:htmlEscape(authApp.authUrl)}">${cse:htmlEscape(authApp.authUrl)}</td>
								<td><fmt:formatDate value="${authApp.createdAt}"
										pattern="yyyy-MM-dd HH:mm" /></td>
								<td><fmt:formatDate value="${authApp.modifiedAt}"
										pattern="yyyy-MM-dd HH:mm" /></td>
								<td>
									<button class="btn" type="button"
										onClick="modifyApp('<c:out value="${authApp.authAppId}"/>')" />
									<spring:message code="common.modify" />
									</button>
									 <button class="btn" type="button"
										onClick="managementAccessCode('<c:out value="${authApp.authAppId}"/>')" />
									<spring:message code="app.manage.connetCode" />
									</button>  
								</td>
							</tr>
						</c:if>
					</c:forEach>
					<c:if test="${empty authAppList}">
						<tr>
							<td colspan="5" style="text-align:center"><spring:message
									code="app.add.describe" /><a href="javascript:createApp()"><spring:message
										code="common.create" /></a>。</td>
						</tr>
					</c:if>
				</tbody>
			</table>
		</div>
		<div id="myPage"></div>
	</div>
</body>
<script type="text/javascript">
$(document).ready(function() {

	if(!placeholderSupport()){
		placeholderCompatible();
	};
});
$(function(){
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
})
$.fn.comboPage.pageSkip = function(opts, _idMap, curPage){
	$("#page").val(curPage);
	$("#searchForm").submit();
};

$("#checkall").click(function(){ 
	if(this.checked){ 
		$("input[name='checkname']:checkbox").each(function(){
			this.checked=true;
		});
	}else{ 
		$("input[name='checkname']:checkbox").each(function(){
			 this.checked=false;
		});
	}
});
function createApp(){
	top.ymPrompt.win({message:'${ctx}/appmanage/authapp/create',width:600,height:470,title:'<spring:message code="app.create.app"/>', iframe:true,btn:[['<spring:message code="common.create"/>','yes',false,"btn-focus"],['<spring:message code="common.cancel"/>','no',true,"btn-cancel"]],handler:saveApp});
	top.ymPrompt_addModalFocus("#btn-focus");
}

function saveApp(tp) {
	if (tp == 'yes') {
		top.ymPrompt.getPage().contentWindow.submitCreateApp();
	} else {
		top.ymPrompt.close();
	}
}

function modifyApp(id){
	top.ymPrompt.win({message:'${ctx}/appmanage/authapp/modify/?authAppId='+id+'',width:600,height:370,title:'<spring:message code="app.modiffy"/>', iframe:true,btn:[['<spring:message code="common.modify"/>','yes',false,"btnModify"],['<spring:message code="common.cancel"/>','no',true,"btnModifyCancel"]],handler:doModifyApp});
	top.ymPrompt_addModalFocus("#btnModify");
}
function doModifyApp(tp) {
	if (tp == 'yes') {
		top.ymPrompt.getPage().contentWindow.submitModifyApp();
	} else {
		top.ymPrompt.close();
	}
}
function managementAccessCode(id) {
	top.ymPrompt.win({message:'${ctx}/appmanage/appaccesskey/?appId='+id+'',width:1150,height:430,title:'<spring:message code="app.manage.connetCode"/>', iframe:true,btn:[['<spring:message code="common.close"/>','no',true]]});
}
</script>
</html>
