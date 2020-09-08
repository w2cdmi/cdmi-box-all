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
   	<form class="form-horizontal" id="creatAdminForm" name="creatAdminForm">
	        <div class="control-group">
	        	<label class="control-label" for=""><em>*</em><spring:message code="authorize.label.username"/></label>
	            <div class="controls">
	                <input type="text" id="loginName" name="loginName" class="span4" />
	                <span class="validate-con bottom"><div></div></span>
	            </div>
	        </div>
	        <div class="control-group">
	        	<label class="control-label" for=""><em>*</em><spring:message code="authorize.label.name"/></label>
	            <div class="controls">
	                <input type="text" id="name" name="name"  class="span4" />
	                <span class="validate-con bottom"><div></div></span>
	            </div>
	        </div>
	        <div class="control-group">
	        	<label class="control-label" for=""><em>*</em><spring:message code="authorize.label.mail"/></label>
	            <div class="controls">
	                <input type="text" id="email" name="email" class="span4" />
	                <span class="validate-con bottom"><div></div></span>
	            </div>
	        </div>
	        <div class="control-group">
	            <label class="control-label" for="input"><spring:message code="athorize.createUser.pwd"/></label>
	            <div class="controls">
	                <input class="span4" type="password" id="password" name="password" value="" autocomplete="off"/>
	                <span class="validate-con bottom"><div></div></span>
	            </div>
	        </div>
			<div class="control-group">
	            <label class="control-label" for="input"><spring:message code="authorize.user.pwd.confirm"/></label>
	            <div class="controls">
	                <input class="span4" type="password" id="confirmPassword" name="confirmPassword" value="" autocomplete="off"/>
	                <span class="validate-con bottom"><div></div></span>
	            </div>
	        </div>
	        <div class="control-group">
	            <label class="control-label" for=""><spring:message code="common.role"/></label>
	            <div class="controls list-checkbox-auth">
		            <label class="checkbox inline" title='<spring:message code="authorize.createAdmin.configauthorize"/>'>
				      <input name="roles" id="clusterRole" type="checkbox" value="CLUSTER_MANAGER"> <spring:message code="manage.title.cluster"/>
				    </label>
				    <label class="checkbox inline" title='<spring:message code="authorize.createAdmin.lookApp"/>'>
				      <input name="roles" id="appRole"  type="checkbox" value="APP_MANAGER"> <spring:message code="APP_MANAGER"/>
				    </label>
				    <label class="checkbox inline" title='<spring:message code="authorize.createAdmin.config.syslog"/>'>
				      <input name="roles" id="configRole" type="checkbox" value="SYSCONFIG_MANAGER"> <spring:message code="SYSCONFIG_MANAGER"/>
				    </label>
				    <label class="checkbox inline" title='<spring:message code="authorize.createAdmin.look.log"/>'>
				      <input name="roles" id="logRole"  type="checkbox" value="LOG_MANAGER"> <spring:message code="LOG_MANAGER"/>
				    </label>
				    <label class="checkbox inline" title='<spring:message code="authorize.job.manage.description"/>'>
				      <input name="roles" id="jobRole"  type="checkbox" value="JOB_MANAGER"> <spring:message code="JOB_MANAGER"/>
				    </label>
			    </div>
	        </div>
	        	 <input type="hidden" name="token" value="${cse:htmlEscape(token)}"/>
	</form>
	</div>
 </div>
<script type="text/javascript">
$.validator.addMethod(
		   "isLoginName", 
		   function(value, element) {   
	           var validName = /^[a-zA-Z]{1}[a-zA-Z0-9]+$/;   
	           return validName.test(value);   
	       }, 
	       $.validator.format('<spring:message code="athorize.login.username.validator"/>')
);    
$.validator.addMethod(
		   "isValidPwdEnhance", 
		   function(value, element, param) { 
			   if(value == ""){
				   return true;
			   }
			   var ret = false;
			   $.ajax({
			        type: "POST",
			        async: false,
			        url:"${ctx}/syscommon/validpwd",
			        data:$("#creatAdminForm").serialize(),
			        success: function(data) {
			        	ret = true;
			        }
			    });
		       return ret;
	       }, 
	       $.validator.format('<spring:message code="authorize.createAdmin.pwd.validator"/>')
);  
$(document).ready(function() {
		$("#creatAdminForm").validate({ 
			rules: { 
				   loginName:{
					   required:true, 
					   rangelength:[4,60],
					   isLoginName:true
				   },
				   name: { 
					   required:true, 
					   rangelength:[2,60]
				   },
				   email: {
					   required:true, 
					   isValidEmail:true,
					   maxlength:[255]
				   },
				   password: { 
					   isValidPwdEnhance:true
				   },
				   confirmPassword: { 
					   equalTo: "#password"
				   }
			}
	    }); 
		$("label").tooltip({ container:"body", placement:"top", delay: { show: 100, hide: 0 }, animation: false });
});

function submitCreateAdminUser() {
	if(!$("#creatAdminForm").valid()) {
        return false;
    }  
	$.ajax({
        type: "POST",
        url:"${ctx}/authorize/role/createLocal",
        data:$('#creatAdminForm').serialize(),
        error: function(request) {
        	handlePrompt("error",'<spring:message code="common.createFail"/>');
        },
        success: function() {
        	top.ymPrompt.close();
        	top.handlePrompt("success",'<spring:message code="common.createSuccess"/>');
        	top.document.getElementById("listManager").click();
        }
    });
}


</script>
</body>
</html>
