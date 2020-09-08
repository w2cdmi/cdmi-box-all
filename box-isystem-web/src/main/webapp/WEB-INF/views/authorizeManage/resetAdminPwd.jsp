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
   	    <input type="hidden" name="id" value="${cse:htmlEscape(id)}"/>
        <div class="control-group">
            <label class="control-label" for="input"><em>*</em><spring:message code="authorize.newPwd"/></label>
            <div class="controls">
                <input class="span4" type="password" id="password" name="password" value="" autocomplete="off"/>
                <span class="validate-con bottom"><div></div></span>
            </div>
        </div>
		<div class="control-group">
            <label class="control-label" for="input"><em>*</em><spring:message code="authorize.confirmPwd"/></label>
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
function submitResetAdminPwd() {
	if(!$("#modifyPwdForm").valid()) {
        return false;
    }
	$.ajax({
        type: "POST",
        url:"${ctx}/authorize/role/doResetAdminPwd",
        data:$('#modifyPwdForm').serialize(),
        error: function(request) {
			 handlePrompt("error",'<spring:message code="anon.resetPwdError"/>');
        },
        success: function() {
        	top.ymPrompt.close();
        	top.handlePrompt("success",'<spring:message code="authorize.admin.resetPwdSuccess"/>');
        }
    });
}
</script>
</body>
</html>
