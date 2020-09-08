<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
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
<div class="pop-content pop-content-en">
	<div class="form-con">
   	<form class="form-horizontal form-horizontal-new" id="creatAppForm" name="creatAppForm">
	        <div class="control-group">
	        	<label class="control-label" for=""><em>*</em><spring:message code="app.ID"/> :</label>
	            <div class="controls">
	                <input type="text" id="authAppId" name="authAppId" class="span4" />
	                <span class="validate-con bottom"><div></div></span>
	            </div>
	        </div>
	        <div class="control-group">
	        	<label class="control-label" for=""><em>*</em><spring:message code="app.certification.addr"/> :</label>
	            <div class="controls">
	                <input type="text" id="authUrl" name="authUrl" class="span4" placeholder='<spring:message code="app.certification.addr.help"/>'/>
	                <span class="validate-con bottom"><div></div></span>
	               	<span class="help-block"> <spring:message code="app.create.cliew"/></span>
	            </div>
	        </div>
	        <div class="control-group">
	        	<label class="control-label" ><spring:message code="app.cdn"/> :</label>
	            <div class="controls">
	            	<label class="checkbox">
	                	<input type="checkbox" id="enableNearestStore" /> <spring:message code="app.cdn.enable"/>
	                </label>
	            </div>
	        </div>
	        <div class="control-group">
	        	<label class="control-label" for=""><spring:message code="app.qos.port"/> :</label>
	            <div class="controls">
	            	<input type="text" id="qosPort" name="qosPort" class="span1" maxlength="5"/>
	                <span class="validate-con bottom"><div></div></span>
	               	<span class="help-block"><spring:message code="app.qos.port.tips"/></span>
	            </div>
	        </div>
        </div>
        <input type="hidden" name="token" value="${token}"/>
        <input type="hidden" id="nearestStore" name="nearestStore" value="0"/>
	</form>
    </div>
</div>
<script type="text/javascript">
$.validator.addMethod(
		   "isAppName", 
		   function(value, element) {   
	           var validName = /^[a-zA-Z]{1}[a-zA-Z0-9]+$/;   
	           return validName.test(value);   
	       }, 
	       $.validator.format('<spring:message code="app.ID.first.rule"/>')
); 

$(document).ready(function() {
	$("#creatAppForm").validate({ 
		rules: { 
			   authAppId:{
				   required:true, 
				   rangelength:[4,20],
				   isAppName:true
			   },
			   authUrl:{
				   maxlength:[64]
			   },
			   qosPort:{
				   required:false, 
				   min:1,
				   max:65536
			   }
		}
    }); 
	$("label").tooltip({ container:"body", placement:"top", delay: { show: 100, hide: 0 }, animation: false });
		
	$("#enableNearestStore").click(function(){ 
		if(this.checked){
			$("#nearestStore").val("1");
		}else{ 
			$("#nearestStore").val("0");
		}
	})
});

function submitCreateApp() {
	if(!$("#creatAppForm").valid()) {
        return false;
    }  
	$.ajax({
        type: "POST",
        url:"${ctx}/appmanage/authapp/create",
        data:$('#creatAppForm').serialize(),
        error: function(request) {
        	handlePrompt("error",'<spring:message code="common.createFail"/>');
        },
        success: function(data) {
        	top.ymPrompt.close();
        	top.handlePrompt("success",'<spring:message code="common.createSuccess"/>');
        	top.document.getElementById("appManager").click();
        	top.ymPrompt.win({message:'${ctx}/appmanage/appaccesskey/firstScanSK?appId='+$("#authAppId").val()+'&akId='+data+'',width:900,height:430,title:'<spring:message code="app.manage.connetCode"/>', iframe:true,btn:[['<spring:message code="common.close"/>','no',true]]});			        	
        }
    });
}

</script>
</body>
</html>
