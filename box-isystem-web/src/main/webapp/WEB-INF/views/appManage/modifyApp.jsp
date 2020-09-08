<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cse" uri="http://cse.huawei.com/custom-function-taglib"%>  
<%@ page import="com.huawei.sharedrive.isystem.util.CSRFTokenManager"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%
request.setAttribute("token", CSRFTokenManager.getTokenForSession(session));
%>
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
   	<form class="form-horizontal" id="modifyAppForm" name="modifyAppForm">
	        <div class="control-group">
	        	<label class="control-label" for=""><em>*</em><spring:message code="app.ID"/>:</label>
	            <div class="controls">
	                <span class="uneditable-input span4">${cse:htmlEscape(authApp.authAppId)}</span>
	            </div>
	        </div>
	        <div class="control-group">
	        	<label class="control-label" for=""><spring:message code="app.certification.addr"/>:</label>
	            <div class="controls">
	                <input type="text" id="authUrl" name="authUrl" value="${cse:htmlEscape(authApp.authUrl)}" class="span4" />
	                <span class="validate-con bottom"><div></div></span>
	            </div>
	        </div>
	      
	        <div class="control-group">
	        	<label class="control-label" for=""><spring:message code="app.cdn"/> :</label>
	            <div class="controls">
	            	<label class="checkbox">
	                	<input type="checkbox" id="enableNearestStore" <c:if test="${authApp.nearestStore == 1}">checked="checked"</c:if> />
<spring:message code="app.cdn.enable"/>
	                </label>
	            </div>
	        </div>
	        
	        <div class="control-group">
	        	<label class="control-label" for=""><spring:message code="app.qos.port"/> :</label>
	            <div class="controls">
	            	<input type="text" id="qosPort" name="qosPort"  value="${cse:htmlEscape(authApp.qosPort)}" class="span1"  maxlength="5"
	            	/>
	                <span class="validate-con bottom"><div></div></span>
	            </div>
	        </div>
        </div>
        <input name="authAppId" id="authAppId" type="hidden" value="${cse:htmlEscape(authApp.authAppId)}">
        <input type="hidden" name="token" value="${token}"/>
        <input type="hidden" id="nearestStore" name="nearestStore" value="${cse:htmlEscape(authApp.nearestStore)}"/>
	</form>
    </div>
</div>
<script type="text/javascript">
$(document).ready(function() {
		$("#modifyAppForm").validate({ 
			rules: { 
				   authAppId:{
					   required:true, 
					   rangelength:[2,60]
				   },
				   authUrl:{
					   maxlength:[64]
				   },
				   qosPort:{
					   required:false,
					   min:0,
					   max:65535
				   }
			}
	    }); 
		$("label").tooltip({ container:"body", placement:"top", delay: { show: 100, hide: 0 }, animation: false });
		
		
});

function submitModifyApp() {
	if(!$("#modifyAppForm").valid()) {
        return false;
    }  
	$.ajax({
        type: "POST",
        url:"${ctx}/appmanage/authapp/modify",
        data:$('#modifyAppForm').serialize(),
        error: function(request) {
        	handlePrompt("error",'<spring:message code="common.modifyFail"/>');
        },
        success: function() {
        	top.ymPrompt.close();
        	top.handlePrompt("success",'<spring:message code="common.modifySuccess"/>');
        	top.document.getElementById("appManager").click();
        }
    });
}

$("#enableNearestStore").click(function(){ 
	if(this.checked){
		$("#nearestStore").val("1");
	}else{ 
		$("#nearestStore").val("0");
	}
})

</script>
</body>
</html>
