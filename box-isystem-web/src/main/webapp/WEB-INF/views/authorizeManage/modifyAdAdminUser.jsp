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
   	<form class="form-horizontal" id="modifyAdminForm">
        <div class="alert alert-error input-medium controls" id="errorTip" style="display:none">
			<button class="close" data-dismiss="alert">×</button><spring:message code="common.createFail"/>
		</div>
        <div class="control-group">
            <label class="control-label" for=""><em>*</em><spring:message code="authorize.label.username"/></label>
            <div class="controls">
                <span class="uneditable-input span4">${cse:htmlEscape(loginName)}</span>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for=""><spring:message code="common.role"/></label>
            <div class="controls">
	            <label class="checkbox inline" title='<spring:message code="authorize.createAdmin.configauthorize"/>'>
			      <input name="roles" id="clusterRole" type="checkbox" ${clusterChecked == true ? "checked='checked'" : ""} value="CLUSTER_MANAGER"> <spring:message code="manage.title.cluster"/>
			    </label>
	            <label class="checkbox inline" title='<spring:message code="authorize.createAdmin.lookApp"/>'>
			      <input name="roles" id="userRole"  type="checkbox" ${appManageChecked == true ? "checked='checked'" : ""} value="APP_MANAGER"> <spring:message code="APP_MANAGER"/>
			    </label>
	            <label class="checkbox inline" title='<spring:message code="authorize.createAdmin.config.syslog"/>'>
			      <input name="roles" id="configRole" type="checkbox" ${sysconfigChecked == true ? "checked='checked'" : ""} value="SYSCONFIG_MANAGER"> <spring:message code="SYSCONFIG_MANAGER"/>
			    </label>
	            <label class="checkbox inline" title='<spring:message code="authorize.createAdmin.look.log"/>'>
			      <input name="roles" id="logRole"  type="checkbox" ${logManageChecked == true ? "checked='checked'" : ""} value="LOG_MANAGER"> <spring:message code="LOG_MANAGER"/>
			    </label>
			    <label class="checkbox inline" title='<spring:message code="authorize.job.manage.description"/>'>
				  <input name="roles" id="jobRole"  type="checkbox" ${jobManageChecked == true ? "checked='checked'" : ""} value="JOB_MANAGER"> <spring:message code="JOB_MANAGER"/>
				</label>
		    </div>
        </div>
        <input name="id" id="modifyHiddenId" type="hidden" value="${cse:htmlEscape(id)}">
       
        <input type="hidden" name="token" value="${cse:htmlEscape(token)}"/>
	</form>
    </div>
</div>
<script type="text/javascript">
$(document).ready(function() {
	$("#loginForm").validate();
	
	$("label").tooltip({ container:"body", placement:"top", delay: { show: 100, hide: 0 }, animation: false });
});
function submitModifyAdAdminUser() {
	$.ajax({
        type: "POST",
        url:"${ctx}/authorize/role/modify",
        data:$('#modifyAdminForm').serialize(),
        error: function(request) {
        	if("appExist"==request.responseText)
        	{
        		handlePrompt("error",'<spring:message code="authorize.appExistModifyFail"/>');
        	}
        	else 
        	{
        		handlePrompt("error",'<spring:message code="common.modifyFail"/>');
        	}
        	
        },
        success: function() {
        	top.ymPrompt.close();
        	top.handlePrompt("success",'<spring:message code="common.modifySuccess"/>');
        	top.document.getElementById("listManager").click();
        }
    });
}
</script>
</body>
</html>
