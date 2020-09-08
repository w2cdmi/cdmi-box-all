<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="Cache-Control" content="no-cache" />
<meta http-equiv="Pragma" content="no-cache" />
<title></title>
<%@ include file="../common/common.jsp"%>
</head>
<body>
<div class="header">
    <div class="header-con">
        <div class="logo pull-left"><a href="#"  id="logoBackgroudId"><spring:message code="main.title" /></a></div>
           <div class="header-R pull-right clearfix">
        	<ul class="clearfix pull-right">
            </ul>
        </div>
    </div>
</div>

<div class="body">
	<div class="sys-content">
	<h4><spring:message code="login.forgetPWD"/> </h4>
	<div class="form-horizontal form-con clearfix">
		<form class="form-horizontal" id="forgetPwdForm">
   
        <div class="alert alert-error input-medium controls" id="errorTip" style="display:none">
			<button class="close" data-dismiss="alert">×</button><spring:message code="anon.forgetPwdError" />
		</div>
        <div class="control-group">
            <label class="control-label" for="input"><em>*</em><spring:message code="login.username" /></label>
            <div class="controls">
                <input class="span4" type="text" id="loginName" name="loginName" value="" />
                <span class="validate-con"><div></div></span>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="input"><em>*</em><spring:message code="user.email"/></label>
            <div class="controls">
                <input class="span4" type="text" id="email" name="email" value="" />
                <span class="validate-con"><div></div></span>
            </div>
        </div>
        
        <div class="control-group">
            <label class="control-label" for="input"><em>*</em><spring:message code="anon.captcha" /></label>
			<div class="controls">
				<input type="text" id="captcha" name="captcha" class="span4 required" size="4" maxlength="4"/>
				<span class="validate-con"><div></div></span>
				<div><img title='<spring:message code="anon.toChangecaptcha" />' id="img_captcha" onclick="javascript:loadimage();" src="${ctx}/verifycode">(<spring:message code="anon.cantSeeCaptcha" />&nbsp;<a href="javascript:void(0)" onclick="javascript:loadimage()"><spring:message code="anon.changeCaptcha" /></a>)
			    </div>
			</div>
        </div>
        <div class="control-group">
            <div class="controls">
            	<button id="chgPassword_btn" type="button" class="btn btn-primary" onclick="generateLink()"><spring:message code="common.OK" /></button>
            	<button id="chgPassword_btn" type="button" class="btn" onclick="backLogin()"><spring:message code="common.back" /></button>
            </div>
        </div>
        <input type="hidden" id="token" name="token" value="<c:out value='${token}'/>"/>
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
$.validator.addMethod(
		   "isLoginName", 
		   function(value, element) {   
	           var validName = /^[a-zA-Z]{1}[a-zA-Z0-9]+$/;   
	           return validName.test(value);   
	       }, 
	       $.validator.format('<spring:message code="athorize.login.username.validator"/>')
); 
$(document).ready(function() {
	loadSysSetting("${ctx}");
	$("#forgetPwdForm").validate({ 
		rules: { 
			   loginName:{
				   rangelength:[4,60],
				   isLoginName:true,
				   required:true
			   },
			   email: {
				   required:true, 
				   isValidEmail:true,
				   maxlength:[255]
			   },
			   captcha: { 
				   required:true
			   }
		}
   }); 
});
	
function generateLink() {
	if(!$("#forgetPwdForm").valid()) {
        return false;
    }
	$.ajax({
        type: "POST",
        url:"${ctx}/syscommon/sendlink",
        data:$('#forgetPwdForm').serialize(),
        error: function(request) {
        	$("#captcha").val("");
        	$("#img_captcha").attr("src","${ctx}/verifycode?" + Math.random());
        	top.handlePrompt("error","<spring:message code='anon.forgetPwdError' />");
        },
        success: function() {
        	top.ymPrompt.alert({title:'<spring:message code="common.title.info"/>',message:'<spring:message code="anon.forgetPwdMsg" />',handler:backLogin});
        	setTimeout(function(){ymPrompt.doHandler('ok')},3000);
        }
    });
}
function backLogin()
{
    window.location = "${ctx}/login";
}

function loadimage()
{
    $("#img_captcha").attr("src","${ctx}/verifycode?" + Math.random());
    $("#captcha").val("");
    $("#captcha").focus();
}
</script>
</html>
