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
<div class="sys-content sys-content-en">
	<div class="alert"><i class="icon-lightbulb icon-orange"></i><spring:message code="sysconfig.message.retention.days.tips"/></div>
	<div class="form-horizontal form-con clearfix">
   	<form id="messageConfigForm" class="form-horizontal" method="post">
   	    <div class="alert alert-error input-medium controls" id="errorTip" style="display:none">
			<button class="close" data-dismiss="alert">×</button><spring:message code="common.saveFail"/>
		</div>
        <div class="control-group">
            <label class="control-label" for="input"><em>*</em><spring:message code="sysconfig.message.retention.days"/></label>
            <div class="controls">
                <input class="span1" type="text" id="msgRetentionDays" name=value value="${cse:htmlEscape(messageConfig.value)}" />
                <span class="validate-con"><div></div></span>
            </div>
        </div>
        
        <div class="control-group">
            <div class="controls">
            	<button id="submit_btn" type="button" onClick="setMsgRetentionDay()" class="btn btn-primary"><spring:message code="common.save"/></button>
            </div>
        </div>
        <input type="hidden" name="id" value="message.retention.days" />
        
        <input type="hidden" name="token" value="${cse:htmlEscape(token)}"/>
        
	</form>
</div>
</div>
<script type="text/javascript">
	$.validator.addMethod(
			   "int", 
			   function(value, element) {   
				  var validName = /^[0-9]{1}[0-9]*$/;   
	           return validName.test(value);
		       }, 
		       $.validator.format('<spring:message code="common.validate.int"/>')
	); 
	$(document).ready(function() {
		$("#messageConfigForm").validate({ 
			rules: { 
				   value: { 
					   required:true,
					   int:true, 
				       min:1,
				       max:30
				   }
			}
	 	}); 
	});
	
	function setMsgRetentionDay(){
		
		if(!$("#messageConfigForm").valid()) {
	        return false;
	    } 
		$.ajax({
			type: "POST",
			url:"${ctx}/sysconfig/set",
			data:$("#messageConfigForm").serialize(),
				error: function(request) {
					if (request.responseText == "InParamterException") {
						top.handlePrompt("error",
								'<spring:message code="message.common.validate.int"/>');
					}else{
						top.handlePrompt("error",'<spring:message code="common.saveFail"/>');
					}
				},
				success: function() {
					top.handlePrompt("success",'<spring:message code="common.saveSuccess"/>');
				}
		});
	}

</script>
</body>
