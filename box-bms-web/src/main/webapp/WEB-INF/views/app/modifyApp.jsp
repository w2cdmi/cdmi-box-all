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
   	<form class="form-horizontal" id="modifyAppForm" name="modifyAppForm">
	        <div class="control-group">
	        	<label class="control-label" for=""><em></em><spring:message code="appList.appId"/>：</label>
	            <div class="controls">
	                <span class="uneditable-input span4">${cse:htmlEscape(authApp.authAppId)}</span>
	            </div>
	        </div>
            <div class="control-group">
                <label class="control-label" for=""><em>*</em><spring:message code="appCode.accessCode"/>：</label>
                <div class="controls">
                    <input type="text" id="ufmAccessKeyId" name="ufmAccessKeyId" value="<c:out value='${authApp.ufmAccessKeyId}'/>" class="span4" />
                    <span class="validate-con bottom inline-span4"><div></div></span>
                    <div class="help-inline"><a href="javascript:void(0)" onclick="showMoive()" ><i class="icon-question-sign icon-green"></i></a></div>
                </div>
            </div>
	        <div class="control-group">
	        	<label class="control-label" for=""><em>*</em><spring:message code="appCode.accessKey"/>：</label>
	            <div class="controls">
	                <input type="password" id="ufmSecretKey" name="ufmSecretKey" value="<c:out value='${authApp.ufmSecretKey}'/>" autocomplete="off" class="span4" />
	                <span class="validate-con bottom"><div></div></span>
	            </div>
	        </div>
	        <div class="control-group">
	        	<label class="control-label" for=""><spring:message code="common.description"/>：</label>
	            <div class="controls">
	                <textarea id="description" name="description" rows="3" maxlength="255" cols="20"  class="span4"><c:out value='${authApp.description}'/></textarea>
	                <span class="validate-con bottom"><div></div></span>
	            </div>
	        </div>
        </div>
        <input name="authAppId" id="authAppId" type="hidden" value="<c:out value='${authApp.authAppId}'/>">
        <input name="type" id="type" type="hidden" value="<c:out value='${authApp.type}'/>">
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
		$("#modifyAppForm").validate({ 
			rules: { 
				   ufmAccessKeyId: { 
					   isKey:true,
					   required:true, 
					   maxlength:[60]
				   },
				   ufmSecretKey: { 
				       isSecretKey:true,
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

function submitModifyApp() {
	if(!$("#modifyAppForm").valid()) {
        return false;
    }  
	$.ajax({
        type: "POST",
        url:"${ctx}/sys/appmanage/authapp/modify",
        data:$('#modifyAppForm').serialize(),
        error: function(request) {
        	handlePrompt("error",'<spring:message code="common.modifyFail"/>');
        },
        success: function() {
        	top.ymPrompt.close();
        	top.handlePrompt("success",'<spring:message code="common.modifySuccess"/>');
        	top.document.getElementById("innerAppManageLinkId").click();
        }
    });
}

function showMoive(){
	top.ymPrompt.resizeWin(660,600);
	var url = '${ctx}/static/help/modifyApp.html';
	if('<spring:message code="main.language"/>' == 'en'){
		url = '${ctx}/static/help/en/modifyApp.html';
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
				top.ymPrompt.resizeWin(660,400);
			}
		}
	});
	ymPrompt_addModalFocus("#focusBtn");
	}
</script>
</body>
</html>
