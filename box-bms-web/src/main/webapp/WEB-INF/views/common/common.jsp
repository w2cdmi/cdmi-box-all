<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="pw.cdmi.box.uam.util.CSRFTokenManager"%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=10" />
<meta http-equiv="X-UA-Compatible" content="IE=9" />
<meta http-equiv="X-UA-Compatible" content="IE=8" />
<META HTTP-EQUIV="Expires" CONTENT="0">
<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
<META HTTP-EQUIV="Cache-control" CONTENT= "no-cache, no-store, must-revalidate">
<META HTTP-EQUIV="Cache" CONTENT="no-cache">
<title><spring:message code="main.title" /></title> 
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
<script src="${ctx}/static/js/public/JQbox-hw-grid.js" type="text/javascript"></script>
<script src="${ctx}/static/js/public/JQbox-hw-page.js" type="text/javascript"></script>
<%@ include file="./messages.jsp"%>
<%
response.setHeader("Cache-Control","no-cache, no-store, must-revalidate");
response.setHeader("Pragma","no-cache");
response.setDateHeader("Expires",0);
request.setAttribute("token", org.springframework.web.util.HtmlUtils.htmlEscape(CSRFTokenManager.getTokenForSession(session)));
%>
<script type="text/javascript">
	ymPrompt.setDefaultCfg({
		closeTxt:'<spring:message code="common.close"/>',
		okTxt:'<spring:message code="common.OK"/>',
		cancelTxt:'<spring:message code="common.cancel"/>'
	})
	
 function isIeBelow11(){
	if(navigator.userAgent.indexOf("MSIE") < 0) {
		return false;
	}else if(navigator.userAgent.indexOf("MSIE 10.0") >= 0) {
		return true;
	}else if(navigator.userAgent.indexOf("MSIE 9.0") >= 0) {
		return true;
	}else if(navigator.userAgent.indexOf("MSIE 8.0") >= 0) {
		return true;
	}else if(navigator.userAgent.indexOf("MSIE 8.0") >= 0) {
		return true;
	}else{
		return false;
	}
}
	
$(function(){
	if('<spring:message code="main.language"/>' == "en"){
		$("head").append('<link href="${ctx}/static/skins/default/css/public_en.css" rel="stylesheet"	type="text/css" />	');
	}
})
</script>
