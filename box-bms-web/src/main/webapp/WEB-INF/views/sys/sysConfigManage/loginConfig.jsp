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
	<div class="alert"><i class="icon-lightbulb"></i><spring:message code="loginAlamSetting.lightbulb"/></div>
	<div class="form-horizontal form-con clearfix">
   	<form id="userLoginConfigFrom" class="form-horizontal" method="post">
	    <div class="control-group">
            <label class="control-label" for="input"><em>*</em><spring:message code="loginAlam.lock.time"/>:</label>
            <div class="controls">
                <select class="span4" id="failTimesValue" name="failTimesValue">
                	<option value="-1" <c:if test="${failtimes == null}">selected="selected"</c:if>></option>
					<option value="5" <c:if test="${failtimes == 5}">selected="selected"</c:if>>5</option>
					<option value="10" <c:if test="${failtimes == 10}">selected="selected"</c:if>>10</option>
                    <option value="15" <c:if test="${failtimes == 15}">selected="selected"</c:if>>15</option>
					<option value="20" <c:if test="${failtimes == 20}">selected="selected"</c:if>>20</option>
				</select>
				<span class="help-inline"></span>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="input"><em>*</em><spring:message code="loginAlam.lock.failtimes"/>:</label>
            <div class="controls">
                <select class="span4" id="lockTimeValue" name="lockTimeValue">
                	<option value="-1" <c:if test="${locktime == null}">selected="selected"</c:if>></option>
					<option value="5" <c:if test="${locktime == 5}">selected="selected"</c:if>><spring:message code="loginAlam.interval.option.min_5"/></option>
					<option value="10" <c:if test="${locktime == 10}">selected="selected"</c:if>><spring:message code="loginAlam.interval.option.min_10"/></option>
                    <option value="15" <c:if test="${locktime == 15}">selected="selected"</c:if>><spring:message code="loginAlam.interval.option.min_15"/></option>
					<option value="30" <c:if test="${locktime == 30}">selected="selected"</c:if>><spring:message code="loginAlam.interval.option.min_30"/></option>
                    <option value="60" <c:if test="${locktime == 60}">selected="selected"</c:if>><spring:message code="loginAlam.interval.option.min_60"/></option>
                    <option value="120" <c:if test="${locktime == 120}">selected="selected"</c:if>><spring:message code="loginAlam.interval.option.min_120"/></option>
				</select>
				<span class="help-inline"></span>				
            </div>
        </div>
        <div class="control-group">
            <div class="controls">
            	<button id="submit_btn" type="button" onClick="saveLoginConfig()" class="btn btn-primary"><spring:message code="common.save"/></button>
            </div>
        </div>
        <input type="hidden" id="token" name="token" value="<c:out value='${token}'/>"/>	
	</form>
	</div>
</div>
<script type="text/javascript">
$(document).ready(function() {
	$("#userLoginConfigFrom").validate({ 
		rules: { 
			failTimesValue:{
				   	   required:true, 
				       digits:true,
				       min:5,
				       max:20
			   },
		   lockTimeValue:{
			   	   required:true, 
			       digits:true,
			       min:5,
			       max:120
		   },   
		}
    }); 
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
});

function saveLoginConfig()
{
	if ($("#failTimesValue").val() == -1 || $("#lockTimeValue").val() == -1) {
		top.handlePrompt("error",'<spring:message code="messages.required"/>');
	}
	if(!$("#userLoginConfigFrom").valid()) {		
        return false;
    }
	$.ajax({
		type: "POST",
		url:"${ctx}/sys/sysconfig/loginconfig/save",
		data : $('#userLoginConfigFrom').serialize(),
		success: function(request) {
			top.handlePrompt("success",'<spring:message code="common.saveSuccess"/>');
		},
		error: function(request) {
			switch(request.responseText)
			{
				case "notAllownNull":
					handlePrompt("error",'<spring:message code="clientManage.notNull"/>');
					break;
				case "badResquestParamter":
					handlePrompt("error",'<spring:message code="clientManage.illegalParameter"/>');
					break;
				default:
					handlePrompt("error",'<spring:message code="common.saveFail"/>');
				    break;
			}			
		}
		
	});
}

</script>