<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<%@ page import="org.apache.shiro.authc.ExcessiveAttemptsException"%>
<%@ page import="org.apache.shiro.authc.IncorrectCredentialsException"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="Cache-Control" content="no-cache" />
<meta http-equiv="Pragma" content="no-cache" />
<script type="text/javascript"> 

</script> 
<title><spring:message code="main.title" /></title>
<%@ include file="../common/common.jsp"%>
<script type="text/javascript">
if(window.parent.length > 1){
	window.top.location = "${ctx}/login";
}
</script>
<link rel="shortcut icon" type="image/x-icon" href="${ctx}/static/skins/default/img/logo.ico">
</head>
<body>
<div class="sys-content">
    <div class="alert"><i class="icon-lightbulb icon-orange"></i><spring:message code="license.confirm.cliew"/></div>
	<div class="form-horizontal form-con clearfix">
   	<form id="licenseConfirm" name="licenseConfirm" class="form-horizontal" method="post" action="${ctx}/authorize/confirmLicense">
	<h5><spring:message code="license.node.info"/></h5>
	<div class="form-horizontal form-con clearfix">
		<div class="form-left form-left-en">
	         <div class="control-group">
	            <label for="input" class="control-label"><spring:message code="license.product.name"/></label>
	            <div class="controls">
	                <span class="uneditable-input span4">${cse:htmlEscape(licenseInfo.productName)}</span>
	            </div>
	        </div>
	         <div class="control-group">
	            <label for="input" class="control-label"><spring:message code="license.country"/></label>
	            <div class="controls">
	                <span class="uneditable-input span4">${cse:htmlEscape(licenseInfo.country)}</span>
	            </div>
	        </div>
	         <div class="control-group">
	            <label for="input" class="control-label"><spring:message code="license.part"/></label>
	            <div class="controls">
	                <span class="uneditable-input span4">${cse:htmlEscape(licenseInfo.office)}</span>
	            </div>
	        </div>
	        <div class="control-group">
	            <label for="input" class="control-label"><spring:message code="license.usernumber"/></label>
	            <div class="controls">
	                <span class="uneditable-input span4">${cse:htmlEscape(licenseInfo.users)}</span>
	            </div>
	        </div>
	        <div class="control-group">
	            <label for="input" class="control-label"><spring:message code="license.codeNum"/></label>
	            <div class="controls">
	                <span class="uneditable-input span4">${cse:htmlEscape(licenseInfo.lsn)}</span>
	            </div>
	        </div>
	        <div class="control-group">
	            <label for="input" class="control-label"><spring:message code="license.extend.teamspace"/></label>
	            <div class="controls">
	                <span class="uneditable-input span4">${cse:htmlEscape(licenseInfo.teamSpaceNumber)}</span>
	            </div>
	        </div>
	        <div class="control-group">
			  	<label for="input" class="control-label"><spring:message code="license.ESN.info"/></label>
	            <div class="controls">
	            	<span class="uneditable-input span10 uneditable-input-multi">${cse:htmlEscape(esnString)}</span>
            	</div>
         	</div>
	    </div>
	    
	    <div class="form-right form-right-en">
	        <div class="control-group">
	            <label for="input" class="control-label"><spring:message code="license.version"/></label>
	            <div class="controls">
	                <span class="uneditable-input span4">${cse:htmlEscape(licenseInfo.productVersion)}</span>
	            </div>
	        </div>
	        <div class="control-group">
	            <label for="input" class="control-label"><spring:message code="license.create.time"/></label>
	            <div class="controls">
	                <span class="uneditable-input span4">${cse:htmlEscape(licenseInfo.createTimeStr)}</span>
	            </div>
	        </div>
	        <div class="control-group">
	            <label for="input" class="control-label"><spring:message code="license.deadline"/></label>
	            <div class="controls">
	                <span class="uneditable-input span4">${cse:htmlEscape(licenseInfo.deadline)}</span>
	            </div>
	        </div>
	        <div class="control-group">
	            <label for="input" class="control-label"><spring:message code="license.client"/></label>
	            <div class="controls">
	                <span class="uneditable-input span4">${cse:htmlEscape(licenseInfo.costumer)}</span>
	            </div>
	        </div>
	        <div class="control-group">
	            <label for="input" class="control-label"><spring:message code="license.teamspace"/></label>
	            <div class="controls">
	                <span class="uneditable-input span4">${cse:htmlEscape(defaultTeams)}</span>
	            </div>
	        </div>
        </div>
        <div class="clearfix"></div>
    </div>
    
	<div class="control-group">
		  <label for="input" class="control-label"></label>
            <div class="controls">
            	<span><spring:message code="license.OK.info"/></span>
            </div>
        </div>
		<div class="control-group">
	            <label for="input" class="control-label"></label>
	            <div class="controls">
	                <button id="submit_btn" type="button" class="btn btn-primary" onClick="confirmImport()"><spring:message code="license.confirm.import"/></button>
            		<button id="reset_btn" type="button" class="btn" onClick="cancelImport()"><spring:message code="common.cancel"/></button>
	            </div>
	     </div>
	 </div>
	   <input name="licenseUuid" id="licenseUuid" type="hidden" value="${cse:htmlEscape(licenseUuid)}"/>
	</form>
</div>

</body>
</html>
<script type="text/javascript">
<c:if test="${error == '0'}">
	top.ymPrompt.alert('<spring:message code="license.file.error"/>');
	window.location='${ctx}/authorize/gotoLicense';
</c:if>
<c:if test="${not empty error}">
	$("#submit_btn").attr("disabled", "disabled");
</c:if>

$(function(){
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
})

function confirmImport(){
	$.ajax({
        type: "POST",
        url:"${ctx}/authorize/confirmLicense",
        data:{
        	licenseUuid: $("#licenseUuid").val(),
        	token:"${cse:htmlEscape(token)}"
        },
        success: function(data) {
        	if(data == "success"){
        		top.ymPrompt.alert('<spring:message code="license.import.success.cliew"/>');
        	}else{
        		top.ymPrompt.alert('<spring:message code="license.import.fail.cliew"/>');
        	}
        	window.location='${ctx}/authorize/gotoLicense';
        }
    });
}

function cancelImport(){
	window.location='${ctx}/authorize/gotoLicense';
}

</script>