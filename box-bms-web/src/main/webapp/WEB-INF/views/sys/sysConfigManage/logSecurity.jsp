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
<%@ include file="../../common/common.jsp"%>
</head>
<body>
<div class="sys-content">
	<div class="form-horizontal form-con clearfix">
   	<form id="logSecurityConfigForm" class="form-horizontal" method="post">
        <div class="control-group">
            <label class="control-label" for="input"><em>*</em><spring:message code="logSecurity.operateRecord"/></label>
            <div class="controls">
                <label class="radio">
                <input type="radio" id="logSecurityVisible" name="logSecurity"  <c:if test="${logSecurity}">checked="checked"</c:if> value="true" /><spring:message code="logSecurity.visibleTrue"/>
                <span class="help-inline"><spring:message code="logSecurity.hint"/></span>
                </label>                                
            </div>
            <div class="controls">
                <label class="radio">                                
                <input type="radio" id="logSecurityUnVisible" name="logSecurity"  <c:if test="${!logSecurity}">checked="checked"</c:if> value="false" /><spring:message code="logSecurity.visibleFalse"/>
                <span class="help-inline"></span>
                </label>   
            </div>  
        </div>
        <div class="control-group">
            <div class="controls">
            	<button id="submit_btn" type="button" onClick="saveLogSecurityConfig()" class="btn btn-primary"><spring:message code="common.save"/></button>
            </div>
        </div>
        <input type="hidden" id="token" name="token" value="<c:out value='${token}'/>"/>
	</form>
</div>
</div>
<script type="text/javascript">
$(document).ready(function() {
	$("#logSecurityConfigForm").validate({ 
		rules: { 
			   uamUrl:{
				   required:true, 
				   maxlength:[255]
			   },
			   ufmUrl: { 
				   required:true, 
				   maxlength:[255]
			   }
		}
    }); 
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
});
function saveLogSecurityConfig(){
	if(!$("#logSecurityConfigForm").valid()) {
        return false;
    }  
	$.ajax({
		type: "POST",
		url:"${ctx}/sys/sysconfig/secconfig/save",
		data:$('#logSecurityConfigForm').serialize(),
		error: function(request) {
			top.handlePrompt("error",'<spring:message code="common.saveFail"/>');
		},
		success: function() {
			top.handlePrompt("success",'<spring:message code="common.saveSuccess"/>');
		}
	});
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
}
</script>
</body>
</html>
