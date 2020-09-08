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
   	<form id="modifyNameForm" class="form-horizontal">
   	    <div class="alert alert-error input-medium controls" id="errorTip" style="display:none">
			<button class="close" data-dismiss="alert">×</button><spring:message code="authorize.resetPwdError" />
		</div>
        <div class="control-group">
            <label class="control-label" for="input"><em>*</em><spring:message code="login.username" /></label>
            <div class="controls">
                <input class="span4" type="text" id="name" name="name" value="${cse:htmlEscape(account.name)}" />
                <span class="validate-con bottom"><div></div></span>
            </div>
        </div>
        <input type="hidden" name="token" value="${cse:htmlEscape(token)}"/>
	</form>
    </div>
</div>
<script type="text/javascript">
$.validator.addMethod(
		"isNameChange", 
		function(value, element, param) {   
			value = $.trim(value);
			return value != param;
		}, 
	    $.validator.format('<spring:message code="authorize.user.name.same"/>')
);  
$(document).ready(function() {
	$("#modifyNameForm").validate({ 
		rules: { 
			   name:{
				   required:true, 
				   rangelength:[2,60],
				   isNameChange:"${cse:htmlEscape(account.name)}"
			   }
		}
    }); 
});
function submitModify() {
	if(!$("#modifyNameForm").valid()) {
        return false;
    }
	
	var vname = $('#name').val();
	$.ajax({
        type: "POST",
        url:"${ctx}/authorize/user/modifyName",
        data:$('#modifyNameForm').serialize(),
        error: function(request) {
        	switch(request.responseText)
			{
				case "userNameNotChange":
					handlePrompt("error",'<spring:message code="authorize.user.name.same"/>');
					break;
				default:
				 	handlePrompt("error",'<spring:message code="authorize.resetUsernameError"/>');
				    break;
			}
        },
        success: function() {
        	top.ymPrompt.close();
        	top.updateUsernameCallback(vname);
        	top.handlePrompt("success",'<spring:message code="authorize.resetUsername.seccess"/>');
        }
    });
}

</script>
</body>
</html>
