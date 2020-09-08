<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<%@ page import="org.apache.shiro.authc.AuthenticationException"%>
<%@ page import="org.apache.shiro.authc.ExcessiveAttemptsException"%>
<%@ page import="org.apache.shiro.authc.IncorrectCredentialsException"%>
<%@ page import="org.apache.shiro.web.util.SavedRequest"%>
<%@ page import="org.springframework.util.SerializationUtils"%>
<%@ page import="java.net.URLEncoder"%>
<%@ page import="java.io.ByteArrayOutputStream"%>
<%@ page import="java.io.IOException"%>
<%@ page import="java.io.ObjectOutputStream"%>
<%@ page import="com.thoughtworks.xstream.core.util.Base64Encoder"%>
<%@ page import="org.apache.commons.lang.StringUtils"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="pragma" content="no-cache"> 
<meta http-equiv="cache-control" content="no-cache"> 
<meta http-equiv="expires" content="0">
<meta http-equiv="X-UA-Compatible" content="IE=10" />
<meta http-equiv="X-UA-Compatible" content="IE=9" />
<meta http-equiv="X-UA-Compatible" content="IE=8" />
<title><spring:message code="main.title" /></title>
<link rel="shortcut icon" type="image/x-icon" href="${ctx}/static/skins/default/img/temp/logo.ico">
<link href="${ctx}/static/skins/default/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/static/skins/default/css/public.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/static/skins/default/css/login.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/static/skins/default/ymPrompt/ymPrompt.css" rel="stylesheet" type="text/css" />

<script src="${ctx}/static/js/public/jquery-1.10.2.min.js" type="text/javascript"></script>
<script src="${ctx}/static/js/public/bootstrap.min.js" type="text/javascript"></script>
<script src="${ctx}/static/js/public/JQbox-hw-switch.js" type="text/javascript"></script>
<script src="${ctx}/static/js/public/common.js" type="text/javascript"></script>
<script src="${ctx}/static/js/public/uaredirect.js" type="text/javascript"></script>
<script src="${ctx}/static/js/public/ymPrompt.source.js" type="text/javascript"></script>
<script src="${ctx}/static/js/public/spinLoading/spin.min.js" type="text/javascript"></script>
<%
String saveRequestStr="";
if(null!=request)
{
    String savedRequestStrObject = request.getSession().getAttribute("savedRequestStr")==null?"":request.getSession().getAttribute("savedRequestStr").toString();
    if(StringUtils.isNotBlank(savedRequestStrObject))
    {
        saveRequestStr=org.springframework.web.util.HtmlUtils.htmlEscape(savedRequestStrObject);
    }
}
Boolean isLocalAuth = true;

%>



<script type="text/javascript">

$(function(){
	if (window.parent.length > 0) {
		 window.top.location = "${ctx}/login";
	}
	
	var u = navigator.userAgent;
	var isAndroid = u.indexOf('Android')>-1||u.indexOf('Adr')>-1;
	var isIOS = !!u.match(/\(i[^;]+;(U;)?CPU.+Mac OS X/);
	if(isAndroid){
		uaredirect("${ctx}/static/clientDownload/androidphone_client.html");
	}else if(isIOS){
		uaredirect("${ctx}/static/clientDownload/ios_client.html");
	}
	
	if (document.all)
	{
		document.execCommand("ClearAuthenticationCache","false");
	}
	else
	{
		var xmlhttp = new XMLHttpRequest();
		xmlhttp.open("GET", "${ctx}/logout", false, "logout", "logout");
		xmlhttp.send(null);
	}
	
	
	loadSysSetting("${ctx}",'<spring:message code="common.language1" />');
	
	var imgData = ["${ctx}/static/skins/default/img/login-switch/s1.png", 
					"${ctx}/static/skins/default/img/login-switch/s2.png", 
					"${ctx}/static/skins/default/img/login-switch/s3.png", 
					"${ctx}/static/skins/default/img/login-switch/s4.png" ];
	if('<spring:message code="common.language1"/>'=="en"){
		imgData = ["${ctx}/static/skins/default/img/login-switch/s1-en.png", 
					"${ctx}/static/skins/default/img/login-switch/s2-en.png", 
					"${ctx}/static/skins/default/img/login-switch/s3-en.png", 
					"${ctx}/static/skins/default/img/login-switch/s4-en.png" ];
	}
	$("#banner").comboSwitch({
		data : imgData,
		posStart : 1600,
		posEnd : -800,
		speed : 5000,
		timer : 1000
	});
})

</script>
<style type="text/css">
.CAprompt{padding-top:25px; font-size:12px; color:#444;}
.CAhandle{padding-top:15px; color:#444;}
.CAhandle a{display:inline-block;}
</style>
</head>
<body>

<div class="header">
    <div class="header-con">
		<div class="logo" id="logoBlock"><img /></div>
    </div>
</div>

<div class="body">
	<div class="body-con clearfix" >
    	<div class="login-block">
        	<div id="loginBlock" class="login-form">
            	<h3><spring:message code="login.userlogin" /></h3>
            	<form class="form-con" action="${ctx}/login" method="post">
                    
                    <div class="name"><input type="text" id="username" name="username" maxlength="127" placeholder='<spring:message code="login.account.info" />' /></div>
                    <div class="password">
                    <input type="password" id="password" name="password" maxlength="127" autocomplete="off" placeholder='<spring:message code="login.password" />' />
                    </div>
                    <input type="hidden" id="savedShrioStr" name="savedShrioStr" value="<%=saveRequestStr%>"/>
                    <div class="login-submit row-fluid">
                        <button type="submit" class="btn btn-large btn-primary span12"><spring:message code="login.submit" /></button>
                    </div>
                    <div class="CAprompt"><strong><spring:message code="head.tip.inform" /></strong><spring:message code="head.tip.inform.help" /></div>
                    <div class="CAhandle">
                    	【 <a href="${ctx}/static/help/CAguide/yunpan.rar"><spring:message code="head.tip.down" /></a> 】
                    	【 <a href="javascript:void(0)" onclick="CAguide()"><spring:message code="head.tip.down.help" /></a> 】
                    </div>
            	</form>
            </div>
        </div>
    	<div id="banner"></div>
    </div>
</div>

<%@ include file="../common/footer.jsp"%>

</body>
<script type="text/javascript">
		$(document).ready(function() {
			var errorMsg=null;
			 <%
			    String error = (String)request.getAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);
			    if(error!=null)
			    {   
					if(error.contains(".LockedAccountException")){
					%>
					errorMsg='<spring:message code="login.locked" />';
					<%
					}
					if(error.contains(".IncorrectCredentialsException"))
					{
					%>
					errorMsg='<spring:message code="login.errorMsg.notExists" />';
					<%
					}
					if(error.contains(".AccountException"))
					{
					%>
					errorMsg='<spring:message code="login.errorMsg.idivalUser" />'
					<%
					}
					if(error.contains(".DisabledAccountException"))
					{
					%>
					errorMsg='<spring:message code="login.fail.network.forbid" />'
					<%
					}
					if(error.contains(".SecurityMartixException"))
					{
					%>
					errorMsg='<spring:message code="login.fail.security.forbidden" />'
					<%
					}
			    }
				%>
			if(errorMsg!=null)
				{
					handlePrompt("error",errorMsg,45,310,null,"loginBlock");
				}
		});
		
		function gotoForgetPwd(){
			window.location = "${ctx}/syscommon/enterforget";
		}
		
		function CAguide(){
			var guideUrl ="${ctx}/static/help/CAguide/guide.html";
			ymPrompt.win({message:guideUrl,width:650,height:500,title:"<spring:message code='head.tip.down.help' />",iframe:true});
		}
	</script>
</html>