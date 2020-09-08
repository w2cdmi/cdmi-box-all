<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="pw.cdmi.box.uam.util.CSRFTokenManager"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%
request.setAttribute("token", CSRFTokenManager.getTokenForSession(session));
%>
<!DOCTYPE html>
<html>
<head>
<%@ include file="./common.jsp"%>
</head>
<body>
<div class="header">
    <div class="header-con">
        <div class="logo pull-left"><a href="#"  id="logoBackgroudId"><img src="${ctx}/static/skins/default/img/logo.png" /><span><spring:message code="main.title" /></span></a></div>
           <div class="header-R pull-right clearfix">
        	<ul class="clearfix pull-right">
            	<li class="pull-left dropdown">
                	<a class="dropdown-toggle" href="#" id="nav-account" data-toggle="dropdown"><strong><shiro:principal property="name"/></strong> <i class="icon-caret-down"></i></a>
                	<ul class="dropdown-menu">
                        <li><a href="${ctx}/logout?token=<c:out value='${token}'/>"><i class="icon-signout"></i> <spring:message code="common.exit"/></a></li>
                    </ul>
                </li>
            </ul>
        </div>
    </div>
</div>

<div class="body">
	<div class="sys-content body-con">
	<div class="form-horizontal form-con  clearfix">
		<form class="form-horizontal" id="modifyPwdForm">
		<input type="hidden" id="token" name="token" value="<c:out value='${token}'/>"/>
        <div class="alert alert-error input-medium controls" id="errorTip" style="display:none">
			<button class="close" data-dismiss="alert">×</button><spring:message code="initChgPwd.modifyFail"/>
		</div>
		<h4><spring:message code="common.updatePwd"/></h4>
        <div class="control-group">
            <label class="control-label" for="input"><em>*</em><spring:message code="initChgPwd.oldPwd"/>:</label>
            <div class="controls">
                <input class="span4" type="password" id="oldPasswd" name="oldPasswd" value="" autocomplete="off"/>
                <span class="validate-con"><div></div></span>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="input"><em>*</em><spring:message code="authorize.newPwd"/></label>
            <div class="controls">
                <input class="span4" type="password" id="password" name="password" value="" autocomplete="off"/>
                <span class="validate-con"><div></div></span>
            </div>
        </div>
		<div class="control-group">
            <label class="control-label" for="input"><em>*</em><spring:message code="authorize.confirmPwd"/></label>
            <div class="controls">
                <input class="span4" type="password" id="confirmPassword" name="confirmPassword" value="" autocomplete="off"/>
                <span class="validate-con"><div></div></span>
            </div>
        </div>
        <div class="control-group">
            <div class="controls">
            	<button id="chgPassword_btn" type="button" class="btn btn-primary" onclick="submitModify()"><spring:message code="common.modify"/></button>
            </div>
        </div>
	</form>
	</div>
	</div>
</div>

<div class="footer">
	<div class="footer-con">
    	<p><span class="logo-small" id="copyRightId"><spring:message code="corpright" /></span></p>
    </div>
</div>
</body>
<script type="text/javascript">
$(document).ready(function() {
	$("#modifyPwdForm").validate({ 
		rules: { 
			   loginName:{
				   required:true, 
				   rangelength:[4,60],
				   isLoginName:true
			   },
			   oldPasswd: { 
				   required:true,
				   isValidOldPwd:true
			   },
			   password: { 
				   required:true,
				   isValidPwd:true
			   },
			   confirmPassword: { 
				   required:true,
				   equalTo: "#password"
			   }
		}
 }); 
	
});
function submitModify() {
	if(!$("#modifyPwdForm").valid()) {
	     return false;
	}
	$.ajax({
        type: "POST",
        url:"${ctx}/account/initChangePwd",
        data:$('#modifyPwdForm').serialize(),
        error: function(request) {
        	switch(request.responseText)
			{
				case "PasswordInvalidException":
					handlePrompt("error",'<spring:message code="initChgPwd.noneComplex"/>');
					$("#oldPasswd").val("");
					$("#password").val("");
					$("#confirmPassword").val("");
					break;
				case "OldPasswordErrorException":
					handlePrompt("error",'<spring:message code="initChgPwd.errOldPwd"/>');
					$("#oldPasswd").val("");
					$("#password").val("");
					$("#confirmPassword").val("");
					break;
				case "PasswordSameException":
					handlePrompt("error",'<spring:message code="initChgPwd.errOldEqualsNewPwd"/>');
					$("#oldPasswd").val("");
					$("#password").val("");
					$("#confirmPassword").val("");
					break;
				case "UserLockedException":
					handlePrompt("error",'<spring:message code="initChgPwd.pwdisLocked"/>');
					$("#oldPasswd").val("");
					$("#password").val("");
					$("#confirmPassword").val("");
					break;
				default:
				 	handlePrompt("error",'<spring:message code="common.modifyFail"/>');
				 	$("#oldPasswd").val("");
					$("#password").val("");
					$("#confirmPassword").val("");
				    break;
			}
        },
        success: function() {
        	top.handlePrompt("success",'<spring:message code="common.modifySuccess"/>');
        	window.location = "${ctx}/";
        }
    });
}
</script>
</html>
