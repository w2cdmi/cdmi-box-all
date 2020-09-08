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
	<div class="form-horizontal form-con clearfix">
   	<form id="logLanguageForm" class="form-horizontal" method="post">
        <div class="control-group">
            <label class="control-label" for="input"><spring:message code="log.language.languageLable"/></label>
            <div class="controls">
                <select class="span4" id="protocolType" name="language">
					<option value="zh" <c:if test="${!(empty logLanguage.language)&&logLanguage.language == 'zh'}">selected="selected"</c:if>><spring:message code="common.simplified.chinese"/></option>
					<option value="en" <c:if test="${(empty logLanguage.language)||logLanguage.language == 'en'}">selected="selected"</c:if>>English</option>
				</select>
            </div>
        </div>
        <div class="control-group">
        	<label class="control-label" for="input"><spring:message code="log.language.logEnable"/></label>
        	<div class="controls">
        		<label class="radio inline" for="input"><input type="radio"   name="config" value="1" <c:if test="${logLanguage.config==1}">checked="checked"</c:if> /><spring:message code="log.language.logEnable.yes"/></label>
        		<label class="radio inline" for="input"><input type="radio"   name="config" value="0" <c:if test="${logLanguage.config==0}">checked="checked"</c:if> /><spring:message code="log.language.logEnable.no"/></label>
        	</div>
        </div>
        <div class="control-group">
            <div class="controls">
            	<button id="submit_btn" type="button" onClick="saveLogLanguage()" class="btn btn-primary"><spring:message code="common.save"/></button>
            </div>
        </div>
        <input type="hidden" name="token" value="${cse:htmlEscape(token)}"/>
	</form>
</div>
</div>
<script type="text/javascript">
$(document).ready(function() {
	$("#logLanguageForm").validate({ 
		rules: { 
			   protocolType:{
			       notNull:true
			   }
		}
 }); 
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
});
function saveLogLanguage(){
	if(!$("#logLanguageForm").valid()) {
        return false;
    }  
	$.ajax({
		type: "POST",
		url:"${ctx}/sysconfig/syslog/savelogLanguage",
		data:$('#logLanguageForm').serialize(),
			error: function(request) {
				top.handlePrompt("error",'<spring:message code="common.saveFail"/>');
			},
			success: function() {
				top.handlePrompt("success",'<spring:message code="common.saveSuccess"/>');
			}
	});
}

</script>
</body>
</html>
