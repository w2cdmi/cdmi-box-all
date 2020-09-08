<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cse" uri="http://cse.huawei.com/custom-function-taglib"%>  
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
<%@ include file="../../common/common.jsp"%>
</head>
<body>
<div class="pop-content">
	<div class="form-con">
   	<form class="form-horizontal" id="modifyAdminForm" name="modifyAdminForm">
   	        <input type="hidden" id="id" name="id" value="<c:out value='${admin.id}'/>"  class="span4" />
   	        <input type="hidden" id="loginName" name="loginName" value="<c:out value='${admin.loginName}'/>"  class="span4" />
	        <div class="control-group">
	        	<label class="control-label" for=""><em>*</em><spring:message code="authorize.label.username"/>:</label>
	            <div class="controls">
	            	<span class="uneditable-input span4"><c:out value='${admin.loginName}'/></span>
	            </div>
	        </div>
	        <div class="control-group">
	        	<label class="control-label" for=""><em>*</em><spring:message code="authorize.label.name"/>:</label>
	            <div class="controls">
	                <input type="text" id="name" name="name"  value="<c:out value='${admin.name}'/>" class="span4" />
	                <span class="validate-con bottom"><div></div></span>
	            </div>
	        </div>
	        <div class="control-group">
	        	<label class="control-label" for=""><em>*</em><spring:message code="authorize.label.mail"/>:</label>
	            <div class="controls">
	                <input type="text" id="email" name="email" value="<c:out value='${admin.email}'/>" class="span4" />
	                <span class="validate-con bottom"><div></div></span>
	            </div>
	        </div>
	        <div class="control-group">
	        	<label class="control-label" for=""><spring:message code="common.description"/>:</label>
	            <div class="controls">
	                <textarea id="noteDesc" name="noteDesc" rows="3" cols="20"  class="span4"><c:out value='${admin.noteDesc}'/></textarea>
	            	<span class="validate-con bottom"><div></div></span>
	            </div>
	        </div>
	          <div class="control-group">
             <label class="control-label" for=""><em>*</em><spring:message code="common.role"/>:</label>
	         <div class="controls">
              <label class="checkbox inline">
				   <input name="roles" id="appRole"  type="checkbox" value="APP_MANAGER" ${appmanagerChecked==true? "checked='checked'":"" }> <spring:message code="APP_MANAGER"/>
			  </label>
			  <label class="checkbox inline">
				   <input name="roles" id="enterpriseRole" type="checkbox" value="ENTERPRISE_BUSINESS_MANAGER" ${enterpriseChecked==true? "checked='checked'":"" }> <spring:message code="ENTERPRISE_MANAGER"/>
			  </label>
			    <label class="checkbox inline">
				   <input name="roles" id="sysConfigRole" type="checkbox" value="SYSTEM_CONFIG" ${systemconfigChecked==true? "checked='checked'":"" }> <spring:message code="SYSTEM_CONFIG"/>
			  </label>
			  <label class="checkbox inline">
				  <input name="roles" id="announcementRole"  type="checkbox" ${announcementManageChecked == true ? "checked='checked'" : ""} value="ANNOUNCEMENT_MANAGER"> <spring:message code="ANNOUNCEMENT_MANAGER"/>
			  </label>
			  <label class="checkbox inline">
				   <input name="roles" id="statisticalRole" type="checkbox" value="STATISTICS_MANAGER" ${statisticalChecked==true? "checked='checked'":"" }> <spring:message code="STATISTICS_MANAGER"/>
			  </label>
			  <label class="checkbox inline">
				  <input name="roles" id="jobRole"  type="checkbox" ${jobManageChecked == true ? "checked='checked'" : ""} value="JOB_MANAGER"> <spring:message code="JOB_MANAGER"/>
			  </label>
			  <label class="checkbox inline">
				  <input name="roles" id="feedbackRole"  type="checkbox" ${feedbackManageChecked == true ? "checked='checked'" : ""} value="FEEDBACK_MANAGER"> <spring:message code="FEEDBACK_MANAGER"/>
			  </label>
			  
            </div>
            </div>
            <input type="hidden" id="token" name="token" value="<c:out value='${token}'/>"/>	
	</form>
    </div>
</div>
<script type="text/javascript">
$(document).ready(function() {
		$("#modifyAdminForm").validate({ 
			rules: { 
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
		$("label").tooltip({ container:"body", placement:"top", delay: { show: 100, hide: 0 }, animation: false });
});

function submitModifyAdminUser() {
	if(!$("#modifyAdminForm").valid()) {
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
        url:"${ctx}/sys/authorize/role/modify",
        data:$('#modifyAdminForm').serialize(),
        error: function(request) {
        	handlePrompt("error",'<spring:message code="common.modifyFail"/>');
        },
        success: function() {
        	top.ymPrompt.close();
        	top.handlePrompt("success",'<spring:message code="common.modifySuccess"/>');
        	top.document.getElementById("sysUserTabId").click();
        }
    });
}

</script>
</body>
</html>
