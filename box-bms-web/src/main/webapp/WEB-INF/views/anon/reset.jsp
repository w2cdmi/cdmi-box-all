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
	<div class="sys-content body-con">
	<div class="form-horizontal form-con  clearfix">
		<form class="form-horizontal" id="modifyPwdForm">
		<input type="hidden" id="token" name="token" value="<c:out value='${token}'/>"/>
        <input type="hidden" id="loginName" name="loginName" value="<c:out value='${loginName}'/>" />
   	    <input type="hidden" id="validateKey" name="validateKey" value="<c:out value='${validateKey}'/>" />
        <div class="control-group">
            <label class="control-label" for="input"><em>*</em><spring:message code="authorize.username"/>:</label>
            <div class="controls">
                <input class="span4" type="text" id="name" name="name" value="" />
                <span class="validate-con"><div></div></span>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="input"><em>*</em><spring:message code="anon.newPwd"/>:</label>
            <div class="controls">
                <input class="span4" type="password" id="password" name="password" value="" autocomplete="off"/>
                <span class="validate-con"><div></div></span>
            </div>
        </div>
		<div class="control-group">
            <label class="control-label" for="input"><em>*</em><spring:message code="anon.confirmNewPwd"/>:</label>
            <div class="controls">
                <input class="span4" type="password" id="confirmPassword" name="confirmPassword" value="" autocomplete="off"/>
                <span class="validate-con"><div></div></span>
            </div>
        </div>
        <div class="control-group">
            <div class="controls">
            	<button id="chgPassword_btn" type="button" class="btn btn-primary" onclick="submitReset()"><spring:message code="common.OK"/></button>
            	<button id="chgPassword_btn" type="button" class="btn" onclick="backLogin()"><spring:message code="common.back"/></button>
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
			   name:{
				   required:true
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
function submitReset() {
	if(!$("#modifyPwdForm").valid()) {
        return false;
    }
	$.ajax({
        type: "POST",
        url:"${ctx}/syscommon/doreset",
        data:$('#modifyPwdForm').serialize(),
        error: function(request) {
        	top.handlePrompt("error",'<spring:message code="anon.resetPwdError"/>');
        },
        success: function() {
        	top.ymPrompt.alert({title:'<spring:message code="common.title.info"/>',message:'<spring:message code="anon.resetPwdSuccess"/>',handler:backLogin});
        	setTimeout(function(){ymPrompt.doHandler('ok')},3000);
        }
    });
}

function backLogin()
{
    window.location = "${ctx}/login";
}

</script>
</html>
