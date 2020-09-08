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
   	<form class="form-horizontal" id="creatAdminForm" name="creatAdminForm">
	        <div class="control-group">
	        	<label class="control-label" for=""><em>*</em><spring:message code="authorize.label.username"/>:</label>
	            <div class="controls">
	                <input type="text" id="loginName" name="loginName" class="span4" />
	                <span class="validate-con bottom"><div></div></span>
	            </div>
	        </div>
	        <div class="control-group">
	        	<label class="control-label" for=""><em>*</em><spring:message code="authorize.label.name"/>:</label>
	            <div class="controls">
	                <input type="text" id="name" name="name"  class="span4" />
	                <span class="validate-con bottom"><div></div></span>
	            </div>
	        </div>
	        <div class="control-group">
	        	<label class="control-label" for=""><em>*</em><spring:message code="authorize.label.mail"/>:</label>
	            <div class="controls">
	                <input type="text" id="email" name="email" class="span4" />
	                <span class="validate-con bottom"><div></div></span>
	            </div>
	        </div>
	        <div class="control-group">
	        	<label class="control-label" for=""><spring:message code="authorize.description"/>:</label>
	            <div class="controls">
	                <textarea id="noteDesc" name="noteDesc" rows="3" cols="20"  class="span4"></textarea>
	                <span class="validate-con bottom"><div></div></span>
	            </div>
	        </div>
	          <div class="control-group">
             <label class="control-label" for=""><em>*</em><spring:message code="authorize.permission"/>:</label>
             <div class="controls">
              <label class="checkbox inline">
				   <input name="roles" id="appRole"  type="checkbox" value="APP_MANAGER"> <spring:message code="APP_MANAGER"/>
			  </label>
			  <label class="checkbox inline">
				   <input name="roles" id="enterpriseRole" type="checkbox" value="ENTERPRISE_BUSINESS_MANAGER"> <spring:message code="ENTERPRISE_MANAGER"/>
			  </label>
			    <label class="checkbox inline">
				   <input name="roles" id="sysConfigRole" type="checkbox" value="SYSTEM_CONFIG"> <spring:message code="SYSTEM_CONFIG"/>
			  </label>
			  <label class="checkbox inline">
				   <input name="roles" id="announcementRole"  type="checkbox" value="ANNOUNCEMENT_MANAGER"> <spring:message code="ANNOUNCEMENT_MANAGER"/>
			  </label>
			   <label class="checkbox inline">
				   <input name="roles" id="statisticalRole" type="checkbox" value="STATISTICS_MANAGER"> <spring:message code="STATISTICS_MANAGER"/>
			  </label>
			  <label class="checkbox inline">
				   <input name="roles" id="jobRole"  type="checkbox" value="JOB_MANAGER"> <spring:message code="JOB_MANAGER"/>
			  </label>
			  <label class="checkbox inline">
				   <input name="roles" id="feedbackRole"  type="checkbox" value="FEEDBACK_MANAGER"> <spring:message code="FEEDBACK_MANAGER"/>
			  </label>
            </div>
            </div>
            <input type="hidden" id="token" name="token" value="<c:out value='${token}'/>"/>	
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
	       $.validator.format('<spring:message code="admin.create.username.validator"/>')
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
				   noteDesc:{
					   maxlength:[255]
				   }
			}
	    }); 
		$("#messageAddr").keydown(
				function(event) {
					if (event.keyCode == 13) {
						searchMessageTo();
						return false;
					}
		});
		
		$("label").tooltip({ container:"body", placement:"top", delay: { show: 100, hide: 0 }, animation: false });
});

function submitCreateAdminUser() {
	
	if(!$("#creatAdminForm").valid()) {
        return false;
    }  
	
	var count = 0;
	$("input[name='roles']:checkbox").each(function(){
		if(this.checked==true)
		{
			count++;
		}
	});
	if(0==count)
    {
	   handlePrompt("error",'<spring:message code="admn.roles.select"/>');
	   return false;	
	}
	
	$.ajax({
        type: "POST",
        url:"${ctx}/sys/authorize/role/createLocal",
        data:$('#creatAdminForm').serialize(),
        error: function(request) {
        	doErr(request);
        },
        success: function() {
        	top.ymPrompt.close();
        	top.handlePrompt("success",'<spring:message code="common.createSuccess"/>');
        	top.document.getElementById("sysUserTabId").click();
        }
    });
}

function doErr(request)
{
	if(409 == request.status)
	{
		handlePrompt("error",'<spring:message code="admin.exist.conflict"/>');
		return;
	}
	if("MailServerNotExist" == request.responseText)
	{
		handlePrompt("error",'<spring:message code="admin.create.err.mail"/>');
		return;
	}
	handlePrompt("error",'<spring:message code="admin.create.err"/>');
}

</script>
</body>
</html>
