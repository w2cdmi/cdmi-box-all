<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
<%@ include file="../common/common.jsp"%>
</head>
<body>
<div class="header">
    <div class="header-con">
    	<div class="logo" id="logoBlock"><img src="${ctx}/static/skins/default/img/logo.png" /><span><spring:message code="main.title" /></span></div>
    </div>
</div>
    	
<div class="body">
	<div class="sys-content body-con clearfix system-con">
	<h4><spring:message code="anon.pwd.forget" /></h4>
	<div class="form-horizontal form-con clearfix">
		<form class="form-horizontal" id="forgetPwdForm">
        <div class="alert alert-error input-medium controls" id="errorTip" style="display:none">
			<button class="close" data-dismiss="alert">×</button><spring:message code="anon.forgetPwdError" />
		</div>
        <div class="control-group">
            <label class="control-label" for="input"><em>*</em><spring:message code="login.username" />:</label>
            <div class="controls">
                <input class="span4" type="text" id="loginName" name="loginName" value="" />
                <span class="validate-con"><div></div></span>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="input"><em>*</em><spring:message code="anon.user.email" /></label>
            <div class="controls">
                <input class="span4" type="text" id="email" name="email" value="" />
                <span class="validate-con"><div></div></span>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="input"><em>*</em><spring:message code="anon.captcha" />:</label>
			<div class="controls">
				<input type="text" id="captcha" name="captcha" class="input-medium required" size="4" maxlength="4"/>
				<img title='<spring:message code="anon.toChangecaptcha" />' id="img_captcha" onclick="javascript:loadimage();" src="${ctx}/verifycode">(<spring:message code="anon.cantSeeCaptcha" /><a href="javascript:void(0)" onclick="javascript:loadimage()"><spring:message code="anon.changeCaptcha" /></a>)
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
$(document).ready(function() {
	$("#forgetPwdForm").validate({ 
		rules: { 
			   loginName:{
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
        	top.handlePrompt("error",'<spring:message code="anon.forgetPwdError" />');
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
