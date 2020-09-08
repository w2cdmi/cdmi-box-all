<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.huawei.sharedrive.isystem.util.CSRFTokenManager"%>
<%@ taglib prefix="cse" uri="http://cse.huawei.com/custom-function-taglib"%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=10" />
<meta http-equiv="X-UA-Compatible" content="IE=9" />
<meta http-equiv="X-UA-Compatible" content="IE=8" />
<META HTTP-EQUIV="Expires" CONTENT="0">
<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
<META HTTP-EQUIV="Cache-control" CONTENT= "no-cache, no-store, must-revalidate">
<META HTTP-EQUIV="Cache" CONTENT="no-cache"> 

<link rel="shortcut icon" type="image/x-icon" href="${ctx}/static/skins/default/img/logo.ico">
<link href="${ctx}/static/skins/default/css/bootstrap.min.css"
	rel="stylesheet" type="text/css" />
<link href="${ctx}/static/skins/default/ymPrompt/ymPrompt.css"
	rel="stylesheet" type="text/css" />
<link href="${ctx}/static/skins/default/css/public.css" rel="stylesheet"
	type="text/css" />
<link href="${ctx}/static/skins/default/css/main.css" rel="stylesheet"
	type="text/css" />

<script src="${ctx}/static/js/public/jquery-1.10.2.min.js"
	type="text/javascript"></script>
<script src="${ctx}/static/js/public/validate/jquery.validate.min.js"
	type="text/javascript"></script>

<script src="${ctx}/static/js/public/bootstrap.min.js"
	type="text/javascript"></script>
<script src="${ctx}/static/js/public/ymPrompt.source.js"
	type="text/javascript"></script>
<script src="${ctx}/static/js/public/common.js" type="text/javascript"></script>
<%@ include file="./messages.jsp"%>
<%
response.setHeader("Cache-Control","no-cache, no-store, must-revalidate");
response.setHeader("Pragma","no-cache");
response.setDateHeader("Expires",0);
request.setAttribute("token", CSRFTokenManager.getTokenForSession(session));
%>

<script type="text/javascript">
	ymPrompt.setDefaultCfg({
		closeTxt:'<spring:message code="button.close"/>',
		okTxt:'<spring:message code="button.ok"/>',
		cancelTxt:'<spring:message code="button.cancel"/>'
	})
$(function(){
	if('<spring:message code="common.language1"/>'=='en'){
		$("head").append('<link href="${ctx}/static/skins/default/css/public_en.css" rel="stylesheet" type="text/css" />');
	}
})
</script>