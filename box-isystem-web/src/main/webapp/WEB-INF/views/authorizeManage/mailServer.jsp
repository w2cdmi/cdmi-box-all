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
<div class="sys-content">
	<div class="alert"><i class="icon-lightbulb icon-orange"></i><spring:message code="authorize.mailserver.describe"/></div>
	<div class="form-horizontal form-con clearfix">
   	<form id="mailServerForm" name="mailServerForm" class="form-horizontal" method="post" action="${ctx}/sysconfig/mailserver/save">
        <input type="hidden" id="enableAuth" name="enableAuth" value="${cse:htmlEscape(mailServer.enableAuth)}" />
        <input type="hidden" id="mailSecurity" name="mailSecurity"  <c:choose><c:when test="${empty mailServer.mailSecurity}">value="tls"</c:when><c:otherwise> value="<c:out value='${mailServer.mailSecurity}'/>"</c:otherwise></c:choose>/> 
        <div class="control-group">
            <label class="control-label" for="input"><em>*</em><spring:message code="authorize.mailServer"/>:</label>
            <div class="controls">
                <input class="span4" type="text" id="server" name="server" value="${cse:htmlEscape(serverStr)}" />
                <span class="validate-con"><div></div></span>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="input"><em>*</em><spring:message code="authorize.mailport"/>:</label>
            <div class="controls">
                <input class="span4" type="text" id="port" name="port" value="${cse:htmlEscape(mailServer.port)}" />
                <span class="validate-con"><div></div></span>
            </div>
        </div>
		<div class="control-group">
            <label class="control-label" for="input"><em>*</em><spring:message code="authorize.senderMail"/>:</label>
            <div class="controls">
                <input class="span4" type="text" id="senderMail" name="senderMail" value="${cse:htmlEscape(mailServer.senderMail)}" />
                <span class="validate-con"><div></div></span>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="input"><em>*</em><spring:message code="authorize.senderName"/>:</label>
            <div class="controls">
                <input class="span4" type="text" id="senderName" name="senderName" value="${cse:htmlEscape(senderName)}" />
                <span class="validate-con"><div></div></span>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="input"><spring:message code="authorize.testMail"/>:</label>
            <div class="controls">
                <input class="span4" type="text" id="testMail" name="testMail" value="${cse:htmlEscape(mailServer.testMail)}" />
                <span class="validate-con"><div></div></span>
            </div>
        </div>
        <div class="control-group">
        	<div class="controls">
            	<label class="radio inline"><input type="radio" id="chkEnableIPSec" name="chkEnableIPSec"  onclick="forbiddenIPSec()" <c:if test="${! empty mailServer && mailServer.mailSecurity=='false'}">checked="checked"</c:if> /><spring:message code="mailServer.forbiddenIPSec"/> </label> 
            	<label class="radio inline"><input type="radio" id="SSL" onclick="openSSL()" title='<spring:message code="mailServer.chkEnableSSL"/>' name="chkEnableIPSec"  <c:if test="${! empty mailServer && mailServer.mailSecurity=='ssl'}">checked="checked"</c:if> /><spring:message code="mailServer.ssl"/>
            	<div class="help-inline"><a class="inline" href="javascript:void(0)" onclick="showMoive()"><i class="icon-question-sign icon-green"></i></a></div>
            	 </label>
            	<label class="radio inline"><input type="radio" id="TLS" onclick="openTLS()" title='<spring:message code="mailServer.chkEnableTLS"/>' name="chkEnableIPSec" <c:if test="${empty mailServer || mailServer.mailSecurity=='tls'}">checked="checked"</c:if>  /><spring:message code="mailServer.tls"/> </label>
            	<span class="help-block"><spring:message code="authorize.mailserver.sll.describe"/></span>
            </div>
        </div>
        <div class="control-group">
        	<div class="controls">
            	<label class="checkbox"><input type="checkbox" id="chkEnableAuth" name="chkEnableAuth" <c:if test="${mailServer.enableAuth}">checked="checked"</c:if> /><spring:message code="authorize.mailserver.sll.using"/></label>
            </div>
        </div>
        <div class="control-group" id="authUsernameDiv" <c:if test="${!mailServer.enableAuth}">style="display:none"</c:if>>
            <label class="control-label" for="input"><spring:message code="authorize.authUsername"/></label>
            <div class="controls">
                <input class="span4" type="text" id="authUsername" name="authUsername" value="${cse:htmlEscape(authUserName)}" />
                <span class="validate-con"><div></div></span>
            </div>
        </div>
        <div class="control-group" id="authPasswordDiv" <c:if test="${!mailServer.enableAuth}">style="display:none"</c:if>>
            <label class="control-label" for="input"><spring:message code="authorize.authPassword"/></label>
            <div class="controls">
                <input class="span4" type="password" id="authPassword" onfocus="focusCompare()" onblur="compare()" name="authPassword" value="${cse:htmlEscape(authUserPwd)}" autocomplete="off"/>
                <span class="validate-con"><div></div></span>
                <input type="hidden" id="hiddenPwd" name="hiddenPwd" value="${cse:htmlEscape(authUserPwd)}" />
            </div>
        </div>
        <div class="control-group">
            <div class="controls">
            	<button id="submit_btn" type="button" onclick="saveMailServer()" class="btn btn-primary"><spring:message code="common.save"/></button>
            	<button id="test_btn" type="button" class="btn" onclick="sendTestMail()"><spring:message code="common.testConnection"/></button>
            </div>
        </div>
        <input type="hidden" name="token" value="${cse:htmlEscape(token)}"/>
	</form>
	</div>
</div>
</body>
<script type="text/javascript">
$(document).ready(function() {
	    
		$("#mailServerForm").validate({ 
			rules: { 
				   server:{
					   required:true, 
					   maxlength:[255]
				   },
				   port: { 
				   	   required:true, 
				       digits:true,
				       min:1,
				       max:65535
				   },
				   senderMail: {
					   required:true, 
					   email:true,
					   maxlength:[255]
				   },
				   senderName: {
					   required:true, 
					   maxlength:[255]
				   },
				   testMail: {
					   required:false, 
					   email:true,
					   maxlength:[255]
				   },
				   authUsername: {
					   maxlength:[255]
				   },
				   authPassword: {
					   maxlength:[64]
				   }
			}
	    }); 
		var pageH = $("body").outerHeight();
		top.iframeAdaptHeight(pageH);
		$("button").tooltip({ container:"body", placement:"top", delay: { show: 100, hide: 0 }, animation: false });
});

function showMoive(){
	var url = '${ctx}/static/help/openSSL.html';
	 if('<spring:message code="common.language1"/>' == 'en'){
	  url = '${ctx}/static/help/en/openSSL.html';
	 }
	top.ymPrompt.win({
		message:url,
		width:430,height:440,
		title:'<spring:message code="common.guide"/>', iframe:true,
		btn: [['<spring:message code="button.replay"/>', "replay", false,"focusBtn"],['<spring:message code="button.summary"/>', "summary", false]],
		  handler : function(tp) {
		   if(tp == "replay"){
		    top.ymPrompt.getPage().contentWindow.replay();
		   }else if(tp == "summary"){
		    top.ymPrompt.getPage().contentWindow.summary();
		   }
		  }
	});
	top.ymPrompt_addModalFocus("#focusBtn");
}

function saveMailServer() {
	if(!$("#mailServerForm").valid()) {
        return false;
    }   
	if($("#enableAuth").val() == null || $("#enableAuth").val() == "")
	{
		$("#enableAuth").val("false");
		$("#authUsername").val("");
	    $("#authPassword").val("");
	}
		if($("#mailSecurity").val() == null || $("#mailSecurity").val() == "")
	{
		$("#mailSecurity").val("false");
	}
	
 
	var result = false;
	$.ajax({
        type: "POST",
        async:false,
        url:"${ctx}/authorize/mailserver/save",
        data:$('#mailServerForm').serialize(),
        error: function(request) {
        	top.handlePrompt("error",'<spring:message code="common.saveFail"/>');
        },
        success: function() {
        	top.handlePrompt("success",'<spring:message code="common.saveSuccess"/>');
        	result = true;
        	window.location.reload();
        }
    });
	return result;
}
function forbiddenIPSec(){  
   	top.ymPrompt.confirmInfo( {
	   	title:'<spring:message code="common.warning"/>',
	   	message :'<spring:message code ="mailServer.warning.message"/>',
	   	width:450,
	   	closeTxt:'<spring:message code="common.close"/>',
	  	handler : function(tp) {
	   		if(tp == "ok"){
	   			$("#chkEnableIPSec").get(0).checked = true;
	   			$("#mailSecurity").val("false");
	   			if($("#port").val() == 465){
	   				$("#port").val(25);
	   			}
	  		}else{
	   			$("#chkEnableIPSec").get(0).checked = false; 
	   			$("#TLS").get(0).checked = true; 
	   			$("#mailSecurity").val("tls");
	   			if($("#port").val() == 25){
	   				$("#port").val(465);
	   			}
	   		}
	 	},
	   	btn: [['<spring:message code="common.OK"/>', "ok"],['<spring:message code="common.cancel"/>', "cancel"]]
  	});        
}

function openSSL(){ 
	top.ymPrompt.confirmInfo( {
	   	title:'<spring:message code="common.warning"/>',
	   	message :'<spring:message code ="mailServer.sslwarning.message"/>',
	   	width:450,
	   	closeTxt:'<spring:message code="common.close"/>',
	  	handler : function(tp) {
	   		if(tp == "ok"){   			
	   			$("#SSL").get(0).checked = true; 
	   			$("#mailSecurity").val("ssl");	   			
	   			if($("#port").val() == 25){
	   				$("#port").val(465);
	   			}
	  		}else{ 
	  			$("#TLS").get(0).checked = true;
	  			$("#mailSecurity").val("tls");
	  			if($("#port").val() == 25){
	   				$("#port").val(465);
	   			}
	   		}
	 	},
	   	btn: [['<spring:message code="common.OK"/>', "ok"],['<spring:message code="common.cancel"/>', "cancel"]]
  	});
}
function openTLS()
{
	$("#mailSecurity").val("tls");
	if($("#port").val() == 25){
		$("#port").val(465);
	}
}
$("#chkEnableAuth").click(function(){ 
	if(this.checked){
		$("#enableAuth").val("true");
		$("#authPassword").val($("#hiddenPwd").val());
		$('#authUsernameDiv').show();
		$('#authPasswordDiv').show();
	}else{ 
		$("#enableAuth").val("false");
		$("#authPassword").val("");
		$('#authUsernameDiv').hide();
		$('#authPasswordDiv').hide();
	}
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
});

function compare(){
	if($("#authPassword").val() == ""){
		$("#authPassword").val($("#hiddenPwd").val());
	}
}

function focusCompare(){
	if($("#authPassword").val() == $("#hiddenPwd").val()){
		$("#authPassword").val("");
	}
}

function sendTestMail() {
    if(!$("#mailServerForm").valid()) {
        return false;
    } 
    if($("#testMail").val() == null || $("#testMail").val() == "")
	{
		top.handlePrompt("error",'<spring:message code="authorize.please.entry.testMaile"/>');
		$("#testMail").focus();
		return false;
	}  
    if($("#enableAuth").val() == null || $("#enableAuth").val() == "")
	{
		$("#enableAuth").val("false");
	}
if($("#mailSecurity").val() == null || $("#mailSecurity").val() == "")
	{
		$("#mailSecurity").val("false");
	}

    
	var result = false;
	$.ajax({
        type: "POST",
        async:false,
        url:"${ctx}/authorize/mailserver/testMail",
        data:$('#mailServerForm').serialize(),
        error: function(request) {
        	handlePrompt("error",'<spring:message code="authorize.testSendMailFail"/>');
        },
        success: function() {
        	top.ymPrompt.close();
        	top.handlePrompt("success",'<spring:message code="authorize.testSendMailSuccess"/>');
        }
    });
	
	return result;
}

function sendMail(tp) {
	if (tp == 'yes') {
		top.ymPrompt.getPage().contentWindow.submitTestMail();
	} else {
		top.ymPrompt.close();
	}
}
</script>
</html>
