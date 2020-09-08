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
   	<form id="modifyEmailForm" class="form-horizontal">
   	     <div class="control-group">
            <label class="control-label" for="input"><em>*</em><spring:message code="anon.pwd"/></label>
            <div class="controls">
                <input class="span4" type="password" id="password" name="password" value="" autocomplete="off"/>
                <span class="validate-con bottom"><div></div></span>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="input"><em>*</em><spring:message code="common.email.admin"/>:</label>
            <div class="controls">
                <input class="span4" type="text" id="email" name="email" value="${cse:htmlEscape(email)}" />
                <span class="validate-con bottom"><div></div></span>
                <span class="help-block"><spring:message code="common.email.describe"/></span>
            </div>
        </div>
         <input type="hidden" name="token" value="${cse:htmlEscape(token)}"/>
	</form>
    </div>
</div>
<script type="text/javascript">
$(document).ready(function() {
	$("#modifyEmailForm").validate({ 
		rules: { 
			   email: { 
				   required:true,
				   isValidEmail:true,
				   maxlength:[255]
			   },
			   password: { 
				   required:true,
				   isValidEmailConfirmPwd:true
			   }
		}
    }); 
	$("#email").keydown(function(event) {
		if (event.keyCode == 13) {
			submitModify();
			if(window.event){
				window.event.cancelBubble = true;
				window.event.returnValue = false;
			}else{
				event.stopPropagation();
				event.preventDefault();
			}
		}
	})
});
function submitModify() {
	if(!$("#modifyEmailForm").valid()) {
        return false;
    }
	$.ajax({
        type: "POST",
        url:"${ctx}/account/setemail",
        data:$('#modifyEmailForm').serialize(),
        error: function(request) {
        	handlePrompt("error",'<spring:message code="common.modifyFail"/>');
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
