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
<div class="sys-content sys-content-en">
	<div class="form-horizontal form-con clearfix">
   	<form id="statisticsConfigForm" class="form-horizontal" method="post">
        <div class="control-group">
                <label class="control-label" for="input"><em>*</em><spring:message code="statisticsConfig.accesskey"/></label>
	            <div class="controls">
	                <input class="span4" type="text" id="accessKey" name="accessKey" value="${cse:htmlEscape(statisticsConfig.accessKey)}" />
	                <span class="validate-con"><div></div></span> 
	            </div>  
	                                
        </div>
        <div class="control-group">
                <label class="control-label" for="input"><em>*</em><spring:message code="statisticsConfig.securitykey"/></label>
	            <div class="controls">
	                <input class="span4" type="password" id="secretKey" autocomplete="off" name="secretKey" value="${cse:htmlEscape(statisticsConfig.secretKey)}" />
	                <span class="validate-con"><div></div></span>
	            </div>          
        </div>  
        <div class="control-group">
            <div class="controls">
            	<button id="submit_btn" type="button" onClick="saveStatisticsConfig()" class="btn btn-primary"><spring:message code="common.save"/></button>
            </div>
        </div>
        <input type="hidden" id="token" name="token" value="<c:out value='${token}'/>"/>
	</form>
</div>
</div>
<script type="text/javascript">
$.validator.addMethod(
	     "isKey", 
	     function(value, element) {   
	            var validName = /^(\d|[a-zA-Z])+$/;   
	            return validName.test(value);   
	        }, 
	        $.validator.format('<spring:message code="app.key.rule"/>')
	); 

$.validator.addMethod(
        "isSecretKey", 
        function(value, element) {
        if(value == "*****************************"){
        return true;
        }
        var validName = /^(\d|[a-zA-Z])+$/; 
        return validName.test(value); 
        }, 
        $.validator.format('<spring:message code="app.key.rule"/>')
        ); 

$(document).ready(function() {
	$("#statisticsConfigForm").validate({ 
		rules: { 
			   accessKey:{
				   isKey:true,
				   required:true, 
				   maxlength:[60]
			   },
			   secretKey: { 
			       isSecretKey:true,
				   required:true, 
				   maxlength:[60]
			   }
		}
    }); 
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
});
function saveStatisticsConfig(){
	if(!$("#statisticsConfigForm").valid()) {
        return false;
    }  
	$.ajax({
		type: "POST",
		url:"${ctx}/sys/sysconfig/statistics/accesskey/save",
		data:$('#statisticsConfigForm').serialize(),
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
