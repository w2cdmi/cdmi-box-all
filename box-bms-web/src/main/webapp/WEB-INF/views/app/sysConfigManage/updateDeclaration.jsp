<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cse" uri="http://cse.huawei.com/custom-function-taglib"%>  
<%@ page import="pw.cdmi.box.uam.util.CSRFTokenManager"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%
request.setAttribute("token", CSRFTokenManager.getTokenForSession(session));
%>
<!DOCTYPE html>
<html>
<head>
<%@ include file="../../common/common.jsp"%>
</head>
<body>
<div class="pop-content">
   	<form id="updateDeclaration" class="form-horizontal" enctype="multipart/form-data" method="post" action="${ctx}/admin/declaration/create">
        <input type="hidden" id="appId" name="appId" value="${appId}" />
        <input type="hidden" id="clientType" name="clientType" value="${cse:htmlEscape(type)}" />
        <div class="control-group">
        	<label class="control-label" for="input"><spring:message code="clientManage.type"/></label>
            <div class="controls">${cse:htmlEscape(type)}</div>
        </div>
        <div class="control-group">
        	<label class="control-label" for="input"><em>*</em><spring:message code="conceal.declaration"/>:</label>
            <div class="controls" >
	            <textarea style="width:450px; height:290px; text-align:left" id="declarationTx" name="declarationTx">${declare.declaration}</textarea>
	            <span class="validate-con bottom"><div style="color:red" id="declarationAlert"></div></span>
            </div>
        </div>
        <input type="hidden" id="token" name="token" value="${token}"/>
        <input type="hidden" id="declaration" name="declaration" value="" />
	</form>
</div>
<script type="text/javascript">
var saveState = "${saveState}";
$(document).ready(function() {
	if(saveState == "success"){
		top.ymPrompt.close();
		top.handlePrompt("success",'<spring:message code="conceal.update.success"/>');
    	top.window.frames[0].location = "${ctx}/admin/declaration/config/${appId}";
	}else if(saveState == "fail"){
		top.ymPrompt_enableModalbtn("#btn-focus");
		handlePrompt("error",'<spring:message code="conceal.update.fail"/>');
	}
	$("#updateDeclaration").validate({ 
		rules: { 
			declarationTx:{
				   required:true, 
			       maxlength:[20000]
			   }
		}
 	});
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
});

function getLength(){
	$.ajax({
		type: "POST",
		url:"${ctx}/admin/declaration/getLength",
		async : false,
		data:$('#updateDeclaration').serialize(),
		error: function(request) {
		},
		success: function(data) {
			lengthFlag = data;
		}
	});
}

function submitUpdateDeclaration(){
	if(!$("#updateDeclaration").valid()) {
        return false;
    }
	getLength();
	if(lengthFlag){
		$("#declarationAlert").html('<spring:message code="conceal.input.max.length"/>');
		return;
	}
    
	top.ymPrompt_disableModalbtn("#btn-focus");
	var content = $("#declarationTx").val();
	$("#declaration").val(content);
	$("#updateDeclaration").submit();
	}
</script>
</body>
</html>
