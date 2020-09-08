<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page
	import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<%@ page import="org.apache.shiro.authc.ExcessiveAttemptsException"%>
<%@ page import="org.apache.shiro.authc.IncorrectCredentialsException"%>
<%@ page import="com.huawei.sharedrive.isystem.util.CSRFTokenManager"%>
<%@ page import="com.huawei.sharedrive.isystem.util.custom.ForgetPwdUtils"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<%
    request.setAttribute("token",CSRFTokenManager.getTokenForSession(session));
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=10" />
<meta http-equiv="X-UA-Compatible" content="IE=9" />
<meta http-equiv="X-UA-Compatible" content="IE=8" />
<meta http-equiv="Cache-Control" content="no-cache" />
<meta http-equiv="Pragma" content="no-cache" />
<script type="text/javascript"> 
function disableBack(){
	window.history.forward();
} 
disableBack(); 
window.onload=disableBack; 
window.onpageshow=function(evt){
	if(evt.persisted){
		disableBack();
	}
} 
window.onunload=function(){
	void(0);
} 
</script>
<title><spring:message code="main.title" /></title>
<script type="text/javascript">
if(window.parent.length > 0){
	window.top.location = "${ctx}/login";
}
</script>
<%@ include file="./common/common.jsp"%>
<link href="${ctx}/static/skins/default/css/login.css" rel="stylesheet"
	type="text/css" />
<link rel="shortcut icon" type="image/x-icon"
	href="${ctx}/static/skins/default/img/logo.ico">
</head>
<body>
	<div class="login">
		<div class="top clearfix">
			<div class="logo pull-left">
				<a href="#" ><spring:message
						code="main.title" /></a>
			</div>
			<div class="sysName pull-left">
				<h4>
					<spring:message code="login.title" />
				</h4>
			</div>
		</div>
		<div class="main">
			<form id="loginForm" name="loginForm" action="${ctx}/login"
				method="post" class="form-horizontal">
				<%
				    Object unAuthorized = session.getAttribute("unAuthorized");
							String error = (String) request
									.getAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);
							session.invalidate();
							response.setHeader("Set-Cookie", "");
							if (error != null) {
								if (error
										.equals("com.huawei.sharedrive.isystem.exception.IncorrectCaptchaException")) {
				%>
				<div class="alert alert-error input-medium controls">
					<button class="close" data-dismiss="alert">×</button>
					<spring:message code="login.captcha.error" />
				</div>
				<%
				    } else if (error
										.equals("com.huawei.sharedrive.isystem.exception.UserLockedException")) {
				%>
				<div class="alert alert-error input-medium controls">
					<button class="close" data-dismiss="alert">×</button>
					<spring:message code="login.user.locked" />
				</div>
				<%
				    } else if (error
										.equals("com.huawei.sharedrive.isystem.exception.UserDisabledException")) {
				%>
				<div class="alert alert-error input-medium controls">
					<button class="close" data-dismiss="alert">×</button>
					<spring:message code="login.user.forbidden" />
				</div>
				<%
				    } else {
				%>
				<div class="alert alert-error input-medium controls">
					<button class="close" data-dismiss="alert">×</button>
				<spring:message code="login.userOrPWD.error" />
				</div>
				<%
				    }
							}
							if (unAuthorized != null && unAuthorized.toString().equals("true")) {
				%>
				<div class="alert alert-error input-medium controls">
					<button class="close" data-dismiss="alert">×</button>
					<spring:message code="login.user.unAuthorize" />
				</div>
				<%
				    }
				%>
				<div class="control-group">
					<label class="control-label" for="input"><spring:message
							code="login.username" /></label>
					<div class="controls">
						<input type="text" id="username" name="username" maxlength="60"
							value="<%=request.getParameter("username") == null
					? ""
					: org.springframework.web.util.HtmlUtils.htmlEscape(request
							.getParameter("username"))%>"
							class="input-medium required" />
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="input"><spring:message
							code="login.password" /></label>
					<div class="controls">
					    <input
							type="password" id="password" name="password" value=""
							class="input-medium required" maxlength="20" autocomplete="off"/>
			   <%
                   if(ForgetPwdUtils.enableForget())
                   {
               %>
                   <span class="forget-pwd" onclick="gotoForgetPwd()"><spring:message code="login.forgetPWD" /></span>
                <%	   
                   }
                %>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="input"><spring:message
							code="login.captcha" /></label>
					<div class="controls">
						<input type="text" id="captcha" name="captcha"
							class="input-medium required" size="4" maxlength="4" /> <span
							class="validate-code"> <img
							title='<spring:message code="anon.toChangecaptcha" />'
							id="img_captcha" onclick="javascript:loadimage();" src="#">
						</span>
					</div>
				</div>
				<div class="control-group">
					<div class="controls">
						<button type="button" onClick="submitLogin()"
							class="btn btn-primary">
							<spring:message code="login.submit" />
						</button>
					</div>
				</div>
			</form>
			<div class="copy-right" id="copyRightId">
				<spring:message code="corpright" />
				
			</div>
		</div>
	</div>
</body>
<script type="text/javascript">
		$(document).ready(function() {
			if(window.top != window.self){
				window.top.location="${ctx}/logout?token=${cse:htmlEscape(token)}";
			}
			if (document.all){
				document.execCommand("ClearAuthenticationCache","false");
			}else {
				var xmlhttp = new XMLHttpRequest();
				xmlhttp.open("GET", "${ctx}/logout", false, "logout", "logout");
				xmlhttp.send(null);
			}
			$("#img_captcha").attr("src","${ctx}/verifycode?t=" + Math.random());
			$("#loginForm").validate();
			loadSysSetting("${ctx}");
			enterLogin();
		});
		
		function submitLogin(){
			if($("#username").val().length > 60){
				return;
			}
			if($("#password").val().length > 20){
				top.handlePrompt("error",'<spring:message code="PWD.overLength" />');
				return;
			}
			document.loginForm.submit();
		}
		function enterLogin() {
			$("#captcha").keydown(function(){
			var evt = arguments[0] || window.event;
			if(evt.keyCode == 13){
				submitLogin();
				if(window.event){
					window.event.cancelBubble = true;
					window.event.returnValue = false;
				}else{
					evt.stopPropagation();
					evt.preventDefault();
				}
				}
			});
		}
		
		function gotoForgetPwd(){
			window.location = "${ctx}/syscommon/enterforget";
		}
		
		function loadimage()
		{
		    $("#img_captcha").attr("src","${ctx}/verifycode?t=" + Math.random());
			$("#captcha").val("");
			$("#captcha").focus();
	}
</script>
</html>
