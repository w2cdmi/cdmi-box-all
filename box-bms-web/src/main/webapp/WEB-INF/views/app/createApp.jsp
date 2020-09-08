<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cse" uri="http://cse.huawei.com/custom-function-taglib"%> 
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
<%@ include file="../common/common.jsp"%>
</head>
<body>
<div class="pop-content">
	<div class="form-con">
   	<form class="form-horizontal" id="creatAppForm" name="creatAppForm">
	        <div class="control-group">
	        	<label class="control-label" for=""><em>*</em><spring:message code="appList.appId"/>：</label>
	            <div class="controls">
	                <input type="text" id="authAppId" name="authAppId" class="span4" />
	                <span class="validate-con bottom"><div></div></span>
	            </div>
	        </div>
            <div class="control-group">
                <label class="control-label" for=""><em>*</em><spring:message code="appCode.accessCode"/>：</label>
                <div class="controls">
                    <input type="text" id="ufmAccessKeyId" name="ufmAccessKeyId"  class="span4" />
                    <span class="validate-con bottom inline-span4"><div></div></span>
                    <div class="help-inline"><a href="javascript:void(0)" onclick="showMoive()" ><i class="icon-question-sign icon-green"></i></a></div>
                </div>
                
            </div>
	        <div class="control-group">
	        	<label class="control-label" for=""><em>*</em><spring:message code="appCode.accessKey"/>：</label>
	            <div class="controls">
	                <input type="password" id="ufmSecretKey" name="ufmSecretKey" autocomplete="off"  class="span4" />
	                <span class="validate-con bottom"><div></div></span>
	            </div>
	        </div>
	        <div class="control-group">
	        	<label class="control-label" for=""><em>*</em><spring:message code="common.type"/>：</label>
	            <div class="controls controls-row">
	                <select id="type" name="type">
	                    <option value="1"><spring:message code="appList.defaultWebApp"/></option>
	                    <option value="2"><spring:message code="appList.otherApp"/></option>
	                </select>
	                <div class="help-inline"><a href="javascript:void(0)" onclick="showMoiveApp()" ><i class="icon-question-sign icon-green"></i></a></div>
	                <span class="validate-con bottom"><div></div></span>
	            </div>
	        </div>
	        <div class="control-group">
	        	<label class="control-label" for=""><spring:message code="common.description"/>：</label>
	            <div class="controls">
	                <textarea id="description" name="description" rows="3" maxlength="255" cols="20"  class="span4"></textarea>
	                <span class="validate-con bottom"><div></div></span>
	            </div>
	        </div>
	        <input type="hidden" id="token" name="token" value="${token}"/>	
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

$.validator.addMethod(
		   "isKey", 
		   function(value, element) {   
	           var validName = /^[a-zA-Z0-9*]+$/;   
	           return validName.test(value);   
	       }, 
	       $.validator.format('<spring:message code="app.key.rule"/>')
); 

$(document).ready(function() {
		$("#creatAppForm").validate({ 
			rules: { 
				 authAppId:{
					   required:true, 
					   rangelength:[4,20],
					   isAppName:true
				   },
				   ufmAccessKeyId: { 
					   isKey:true,
					   required:true, 
					   maxlength:[60]
				   },
				   ufmSecretKey: { 
					   isKey:true,
					   required:true, 
					   maxlength:[60]
				   },
				   description:{
					   maxlength:[255]
				   }
			}
	    }); 
		$("label").tooltip({ container:"body", placement:"top", delay: { show: 100, hide: 0 }, animation: false });
});

function submitCreateApp() {
	$("#authAppId").val($.trim($("#authAppId").val()));
	$("#ufmAccessKeyId").val($.trim($("#ufmAccessKeyId").val()));
	$("#ufmSecretKey").val($.trim($("#ufmSecretKey").val()));
	if(!$("#creatAppForm").valid()) {
        return false;
    }  
	$.ajax({
        type: "POST",
        url:"${ctx}/sys/appmanage/authapp/create",
        data:$('#creatAppForm').serialize(),
        error: function(request) {
        	handlePrompt("error",'<spring:message code="common.createFail"/>');
        	switch(request.responseText)
			{
				case "InvalidParamException":
					handlePrompt("error",'<spring:message code="createApp.defaultExist"/>');
					break;
				case "MethodNotAllowedException":
					handlePrompt("error",'<spring:message code="createApp.maxNumber"/>');
					break;
				default:
				 	handlePrompt("error",'<spring:message code="common.createFail"/>');
				    break;
			}
        },
        success: function() {
        	top.ymPrompt.close();
        	top.handlePrompt("success",'<spring:message code="common.createSuccess"/>');
        	top.document.getElementById("innerAppManageLinkId").click();
        }
    });
    
}
function showMoive(){
	top.ymPrompt.resizeWin(670,600);
	var url = '${ctx}/static/help/createApp.html';
	if('<spring:message code="main.language"/>' == 'en'){
		url = '${ctx}/static/help/en/createApp.html';
	}
	ymPrompt.win({
		message:url,
		width:430,height:435,
		maskAlphaColor: '#eee',
		title:'<spring:message code="common.guide"/>', iframe:true, 
		btn: [['<spring:message code="button.replay"/>', "replay", false, "focusBtn"],['<spring:message code="button.summary"/>', "summary", false]],
		handler : function(tp) {
			if(tp == "replay"){
				ymPrompt.getPage().contentWindow.replay();
			}else if(tp == "summary"){
				ymPrompt.getPage().contentWindow.summary();
			}else{
				top.ymPrompt.resizeWin(670,425);
			}
		}
	});
	ymPrompt_addModalFocus("#focusBtn");
	}
function showMoiveApp(){
	top.ymPrompt.resizeWin(670,600);
	var urlApp = '${ctx}/static/help/createApplication.html';
	if('<spring:message code="main.language"/>' == 'en'){
		urlApp = '${ctx}/static/help/en/createApplication.html';
	}
	ymPrompt.win({
		message:urlApp,
		width:430,height:435,
		maskAlphaColor: '#eee',
		title:'<spring:message code="common.guide"/>', iframe:true, 
		btn: [['<spring:message code="button.replay"/>', "replay", false, "focusBtn"],['<spring:message code="button.summary"/>', "summary", false]],
		handler : function(tp) {
			if(tp == "replay"){
				ymPrompt.getPage().contentWindow.replay();
			}else if(tp == "summary"){
				ymPrompt.getPage().contentWindow.summary();
			}else{
				top.ymPrompt.resizeWin(670,425);
			}
		}
	});
	ymPrompt_addModalFocus("#focusBtn");
	}
</script>
</body>
</html>
