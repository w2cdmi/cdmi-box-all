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
<div class="sys-content sys-content-en">
	<div class="form-horizontal form-con clearfix">
   	<form id="logConfigFrom" class="form-horizontal" method="post">
        <div class="control-group">
            <label class="control-label" for="input"><spring:message code="log.save.language"/>:</label>
            <div class="controls">
                <select class="span4" id="logLanguage" name="protocolType">
					<option value="zh" <c:if test="${language == 'zh'}">selected="selected"</c:if>><spring:message code="log.save.chinese"/></option>
					<option value="en" <c:if test="${empty language || language == 'en'}">selected="selected"</c:if>><spring:message code="log.save.english"/></option>
				</select>
            </div>
        </div>
        <div class="control-group">
        	<label class="control-label" for="input"><spring:message code="log.save.config"/>:</label>
        	<div class="controls">
        		<label class="radio inline" for="input"><input type="radio" id="isConfigLogYes" title='<spring:message code="log.save.yes"/>' name="isConfigLog"  value ="1"<c:if test="${isConfig==1}">checked="checked"</c:if> />
        		<spring:message code="log.save.yes"/></label>
        		<label class="radio inline" for="input"><input type="radio" id="isConfigLogNo" title='<spring:message code="log.save.no"/>' name="isConfigLog"  value ="0"<c:if test="${empty isConfig || isConfig==0}">checked="checked"</c:if> />
        		<spring:message code="log.save.no"/></label>
        	</div>
        </div>
        <div class="control-group">
            <div class="controls">
            	<button id="submit_btn" type="button" onClick="saveLogConfig()" class="btn btn-primary"><spring:message code="common.save"/></button>
            </div>
        </div>
        <input type="hidden" id="token" name="token" value="<c:out value='${token}'/>"/>	
	</form>
	</div>
</div>
<script type="text/javascript">
function saveLogConfig()
{
	var language = $('#logLanguage option:selected').val();
	var isConfigLog = $("input[type='radio']:checked").val();
	$.ajax({
		type: "POST",
		url:"${ctx}/sys/userconfig/save?isConfigLog="+isConfigLog +"&language=" +language,
		data : $('#logConfigFrom').serialize(),
		success: function(request) {
			top.handlePrompt("success",'<spring:message code="common.saveSuccess"/>');
		},
		error: function(request) {
			top.handlePrompt("error",'<spring:message code="common.saveFail"/>');
		}
		
	});
}

</script>