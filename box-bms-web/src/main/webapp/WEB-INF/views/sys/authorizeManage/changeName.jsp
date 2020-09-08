<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
<%@ include file="../../common/common.jsp"%>
</head>
<body>
<div class="pop-content">
	<div class="form-con">
   	<form id="modifyNameForm" class="form-horizontal">
   	    <div class="alert alert-error input-medium controls" id="errorTip" style="display:none">
			<button class="close" data-dismiss="alert">×</button><spring:message code="authorize.resetPwdError" />
		</div>
        <div class="control-group">
            <label class="control-label" for="input"><em>*</em><spring:message code="login.username" />:</label>
            <div class="controls">
                <input class="span4" type="text" id="name" name="name" value="<c:out value='${account.name}'/>" />
                <span class="validate-con bottom"><div></div></span>
            </div>
        </div>
        <input type="hidden" id="token" name="token" value="<c:out value='${token}'/>"/>	
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
	    $.validator.format('<spring:message code="changeName.usernameUnmodified"/>!')
);  
$(document).ready(function() {
	$("#modifyNameForm").validate({ 
		rules: { 
			   name:{
				   required:true, 
				   rangelength:[2,60],
				   isNameChange:"<c:out value='${account.name}'/>"
			   }
		}
    }); 
});
function submitModify() {
	if(!$("#modifyNameForm").valid()) {
        return false;
    }
	$.ajax({
        type: "POST",
        url:"${ctx}/sys/authorize/user/modifyName",
        data:$('#modifyNameForm').serialize(),
        error: function(request) {
        	switch(request.responseText)
			{
				case "userNameNotChange":
					handlePrompt("error",'<spring:message code="changeName.usernameUnmodified"/>');
					break;
				default:
				 	handlePrompt("error",'<spring:message code="common.modifyFail"/>');
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
