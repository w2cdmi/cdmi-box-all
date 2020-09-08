<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cse" uri="http://cse.huawei.com/custom-function-taglib"%>  
<%@ page import="pw.cdmi.box.uam.util.CSRFTokenManager"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%
request.setAttribute("token", CSRFTokenManager.getTokenForSession(session));
%>
<!DOCTYPE html>
<html>
<head>
<%@ include file="../common/common.jsp"%>
</head>
<body>
<div class="sys-content sys-content-en">
	<div class="alert"><i class="icon-lightbulb"></i><spring:message code="announcement.config.manage.description"/></div>
	<div class="form-horizontal form-con clearfix">
   	<form id="announcementConfigForm" class="form-horizontal" method="post">
        <div class="control-group">
                <label class="control-label" for="input"><em>*</em><spring:message code="announcement.message.saving.times"/></label>
	            <div class="controls">
	                <input class="span4" type="text" id="messageSavingTimes" name="messageSavingTimes" value="<c:out value='${config.messageSavingTimes}'/>" />
	                <span class="validate-con"><div></div></span> 
	            </div>  
	                                
        </div>
        <div class="control-group">
            <div class="controls">
            	<button id="submit_btn" type="button" onClick="saveConfig()" class="btn btn-primary"><spring:message code="common.save"/></button>
            </div>
        </div>
        <input type="hidden" id="token" name="token" value="${token}"/>
	</form>
</div>
</div>
<script type="text/javascript">
$(document).ready(function() {
	$("#announcementConfigForm").validate({ 
		rules: { 
				messageSavingTimes:{
				   required:true, 
				   digits:true,
				   min:1,
				   max:30,
				   maxlength:[2]
			   }
		}
    }); 
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
});
function saveConfig(){
	if(!$("#announcementConfigForm").valid()) {
        return false;
    }  
	$.ajax({
		type: "POST",
		url:"${ctx}/announcement/saveConfig",
		data:$('#announcementConfigForm').serialize(),
		error: function(request) {
			top.handlePrompt("error",'<spring:message code="common.saveFail"/>');
		},
		success: function() {
			top.handlePrompt("success",'<spring:message code="common.saveSuccess"/>');
			window.location.reload();
		}
	});
	
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
}
</script>
</body>
</html>
