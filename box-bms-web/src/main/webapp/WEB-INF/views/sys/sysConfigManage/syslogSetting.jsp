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
<div class="sys-content">
	<div class="alert"><i class="icon-lightbulb"></i><spring:message code="syslogSetting.lightbulb"/>
<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<spring:message code="syslogSetting.lightbulbStatement"/></div>
	<div class="form-horizontal form-con clearfix">
   	<form id="syslogServerForm" class="form-horizontal" method="post">
   		<input type="hidden" id="sendLocalTimestamp" name="sendLocalTimestamp" value="<c:out value='${sysLogServer.sendLocalTimestamp}'/>" />
        <input type="hidden" id="sendLocalName" name="sendLocalName" value="<c:out value='${sysLogServer.sendLocalName}'/>" />
   	    <div class="alert alert-error input-medium controls" id="errorTip" style="display:none">
			<button class="close" data-dismiss="alert">×</button><spring:message code="common.saveFail"/>
		</div>
        <div class="control-group">
            <label class="control-label" for="input"><spring:message code="syslogSetting.serverAddress"/>:</label>
            <div class="controls">
                <input class="span4" type="text" id="server" name="server" value="<c:out value='${server}'/>" />
                <span class="validate-con"><div></div></span>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="input"><em>*</em><spring:message code="syslogSetting.serverPort"/>:</label>
            <div class="controls">
                <input class="span4" type="text" id="port" name="port" value="<c:out value='${sysLogServer.port}'/>" />
                <span class="validate-con"><div></div></span>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="input"><em>*</em><spring:message code="syslogSetting.protocolType"/>:</label>
            <div class="controls">
                <select class="span4" id="protocolType" name="protocolType">
					<option value="0" <c:if test="${sysLogServer.protocolType == 0}">selected="selected"</c:if>>TCP</option>
					<option value="1" <c:if test="${sysLogServer.protocolType == 1}">selected="selected"</c:if>>UDP</option>
				</select>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="input"><em>*</em><spring:message code="syslogSetting.character"/>:</label>
            <div class="controls">
                <select class="span4" id="charset" name="charset">
                <c:forEach items="${charsets}" var="curChar">
                	<option value="<c:out value='${curChar}'/>" <c:if test="${sysLogServer.charset == curChar}">selected="selected"</c:if>><c:out value='${curChar}'/></option>
                </c:forEach>
				</select>
            </div>
        </div>
        <div class="control-group">
        	<label class="control-label" for="input"><spring:message code="syslogSetting.serverTime"/>:</label>
        	<div class="controls">
        		<input type="checkbox" id="chkEnableLocalTimestamp" title='<spring:message code="syslogSetting.serverTime"/>' name="chkEnableLocalTimestamp" <c:if test="${empty sysLogServer || sysLogServer.sendLocalTimestamp}">checked="checked"</c:if> />
        	</div>
        </div>
        <div class="control-group">
        	<label class="control-label" for="input"><spring:message code="syslogSetting.serverName"/>:</label>
        	<div class="controls">
        		<input type="checkbox" id="chkEnableLocalName" title='<spring:message code="syslogSetting.serverName"/>' name="chkEnableLocalName" <c:if test="${empty sysLogServer || sysLogServer.sendLocalName}">checked="checked"</c:if> />
        	</div>
        </div>
        <div class="control-group">
            <div class="controls">
            	<button id="submit_btn" type="button" onClick="saveSysLogSetting()" class="btn btn-primary"><spring:message code="common.save"/></button>
            	<button id="test_btn" type="button" class="btn" onclick="testConfig()"><spring:message code="common.test"/></button>
            </div>
        </div>
        <input type="hidden" id="token" name="token" value="<c:out value='${token}'/>"/>	
	</form>
</div>
</div>
<script type="text/javascript">
$(document).ready(function() {
	
	$("#syslogServerForm").validate({ 
		rules: { 
			   server:{
			       maxlength:[255]
			   },
			   port: { 
				   required:true, 
			       digits:true,
			       min:1,
			       max:65535
			   }
		}
 	});
 	var proType=  $("#protocolType").val();
 	if(proType == "1")
 	{
 		$('#test_btn').hide();
 	}
 	else
 	{
 		$('#test_btn').show();
 	} 
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
});
function saveSysLogSetting(){
	if(!$("#syslogServerForm").valid()) {
        return false;
    }  
	$.ajax({
		type: "POST",
		url:"${ctx}/sys/sysconfig/syslog/save",
		data:$('#syslogServerForm').serialize(),
			error: function(request) {
				top.handlePrompt("error",'<spring:message code="common.saveFail"/>');
			},
			success: function() {
				top.handlePrompt("success",'<spring:message code="common.saveSuccess"/>');
			}
	});
}
$("#protocolType").change(function () {
	var protocol = $(this).val();
	if(protocol == "0"){
		$('#test_btn').show();
	}else{
		$('#test_btn').hide();
	}
});
$("#chkEnableLocalTimestamp").click(function(){ 
	if(this.checked){
		$("#sendLocalTimestamp").val("true");
	}else{ 
		$("#sendLocalTimestamp").val("false");
	}
});
$("#chkEnableLocalName").click(function(){ 
	if(this.checked){
		$("#sendLocalName").val("true");
	}else{ 
		$("#sendLocalName").val("false");
	}
});
function testConfig(){
	if(!$("#syslogServerForm").valid()) {
        return false;
    }  
	$.ajax({
		type: "POST",
		url:"${ctx}/sys/sysconfig/syslog/test",
		data:$('#syslogServerForm').serialize(),
			error: function(request) {
				top.handlePrompt("error",'<spring:message code="common.testConnectionFail"/>');
			},
			success: function() {
				top.handlePrompt("success",'<spring:message code="common.testConnectionSuccess"/>');
			}
	});
}
</script>
</body>
</html>
