<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
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
<div class="pop-content">
	<div class="form-con"> 
   	<form class="form-horizontal" id="modifyPwdForm">
        <div class="alert alert-error input-medium controls" id="errorTip" style="display:none">
			<button class="close" data-dismiss="alert">×</button><spring:message code="common.modifyFail"/>！
		</div>
        <div class="control-group">
            <label class="control-label" for="input"><em>*</em><spring:message code="comm.pwd.original"/></label>
            <div class="controls">
                <input class="span4" type="password" id="oldPasswd" name="oldPasswd" value="" autocomplete="off"/>
                <span class="validate-con bottom"><div></div></span>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="input"><em>*</em><spring:message code="common.pwd.new"/></label>
            <div class="controls">
                <input class="span4" type="password" id="password" name="password" value="" autocomplete="off"/>
                <span class="validate-con bottom"><div></div></span>
            </div>
        </div>
		<div class="control-group">
            <label class="control-label" for="input"><em>*</em><spring:message code="anon.confirmNewPwd"/></label>
            <div class="controls">
                <input class="span4" type="password" id="confirmPassword" name="confirmPassword" value="" autocomplete="off"/>
                <span class="validate-con bottom"><div></div></span>
            </div>
        </div>
         <input type="hidden" name="token" value="${cse:htmlEscape(token)}"/>
	</form>
    </div>
</div>
<script type="text/javascript">
$(document).ready(function() {
	$("#modifyPwdForm").validate({ 
		rules: { 
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
function submitModifyPwd() {
	var oldP = $.trim($("#oldPasswd").val());
	$("#oldPasswd").val(oldP);
	if(!$("#modifyPwdForm").valid()) {
        return false;
    }
	$.ajax({
        type: "POST",
        url:"${ctx}/account/changePwd",
        data:$('#modifyPwdForm').serialize(),
        error: function(request) {
        	switch(request.responseText)
			{
				case "PasswordInvalidException":
					handlePrompt("error",'<spring:message code="common.pwd.complex.error"/>');
					$("#oldPasswd").val("");
					$("#password").val("");
					$("#confirmPassword").val("");
					break;
				case "OldPasswordErrorException":
					handlePrompt("error",'<spring:message code="common.originalPWD.error"/>');
					$("#oldPasswd").val("");
					$("#password").val("");
					$("#confirmPassword").val("");
					break;
				case "PasswordSameException":
					handlePrompt("error",'<spring:message code="common.originaPWD.same.error"/>');
					$("#oldPasswd").val("");
					$("#password").val("");
					$("#confirmPassword").val("");
					break;
				case "UserLockedException":
					handlePrompt("error",'<spring:message code="common.pwd.islock"/>');
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
        	top.ymPrompt.close();
        	top.handlePrompt("success",'<spring:message code="common.modifySuccess"/>');
        }
    });
}
</script>
</body>
</html>
