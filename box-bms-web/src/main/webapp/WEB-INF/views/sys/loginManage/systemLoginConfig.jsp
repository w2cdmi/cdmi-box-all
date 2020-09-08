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
   	<form id="systemLoginConfigFrom" class="form-horizontal" method="post">
	    <div class="control-group">
	          <label class="control-label" for="input"><em>*</em><spring:message code="loginAlam.threshold.label"/></label>
		      <div class="controls">
		            <input class="span4" type="text" id="threshold" name="threshold" value="${threshold}" />
		            <span class="validate-con"><div></div></span> 
		      </div>  
		                                
	    </div>
        <div class="control-group">
            <label class="control-label" for="input"><spring:message code="loginAlam.interval.label"/>:</label>
            <div class="controls">
                <select class="span4" id="interval" name="interval">
					<option value="1" <c:if test="${interval == 1}">selected="selected"</c:if>><spring:message code="loginAlam.interval.option.min_1"/></option>
					<option value="5" <c:if test="${interval == 5}">selected="selected"</c:if>><spring:message code="loginAlam.interval.option.min_5"/></option>
                    <option value="15" <c:if test="${interval == 15}">selected="selected"</c:if>><spring:message code="loginAlam.interval.option.min_15"/></option>
					<option value="30" <c:if test="${interval == 30}">selected="selected"</c:if>><spring:message code="loginAlam.interval.option.min_30"/></option>
                    <option value="60" <c:if test="${interval == 60}">selected="selected"</c:if>><spring:message code="loginAlam.interval.option.min_60"/></option>

				</select>
            </div>
        </div>
        <div class="control-group">
            <div class="controls">
            	<button id="submit_btn" type="button" onClick="saveSystemConfig()" class="btn btn-primary"><spring:message code="common.save"/></button>
            </div>
        </div>
        <input type="hidden" id="token" name="token" value="<c:out value='${token}'/>"/>	
	</form>
	</div>
</div>
<script type="text/javascript">
$(document).ready(function() {
	$("#systemLoginConfigFrom").validate({ 
		rules: { 
			       threshold:{
				   	   required:true, 
				       digits:true,
				       min:1,
				       max:9999999
			   }
		}
    }); 
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
});

function saveSystemConfig()
{
	if(!$("#systemLoginConfigFrom").valid()) {
        return false;
    } 
	var interval = $('#interval option:selected').val();
	$.ajax({
		type: "POST",
		url:"${ctx}/sys/loginAlam/systemConfig/save",
		data : $('#systemLoginConfigFrom').serialize(),
		success: function(request) {
			top.handlePrompt("success",'<spring:message code="common.saveSuccess"/>');
		},
		error: function(request) {
			top.handlePrompt("error",'<spring:message code="common.saveFail"/>');
		}
		
	});
}

</script>