<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page import="pw.cdmi.box.uam.util.CSRFTokenManager"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%
request.setAttribute("token", CSRFTokenManager.getTokenForSession(session));
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title></title>
<%@ include file="../../common/common.jsp"%>
</head>
<body>
<div class="sys-content sys-content-en">
	<div class="clearfix control-group">
		<a  class="return btn btn-small pull-right" href="${ctx}/app/appmanage/authapp/list"><i class="icon-backward"></i>&nbsp;<spring:message code="common.back"/></a>
		<h5 class="pull-left" style="margin: 3px 0 0 4px;"><a href="${ctx}/app/appmanage/authapp/list">${appId}</a>&nbsp;>&nbsp;<spring:message code="appSysConfig.app.config"/></h5>	
	</div>
	<ul class="nav nav-tabs">
    	<li><a class="return" href="${ctx}/app/basicconfig/config/${appId}"><spring:message code="appSysConfig.basicconfig"/></a></li>
    	<c:if test="${appType == 1}">
        <li><a class="return" href="${ctx}/app/logo/config/${appId}"><spring:message code="appSysConfig.logo.config"/> </a></li>
         </c:if>
    	<c:if test="${appType == 1}">
    	<li><a class="return" href="${ctx}/app/clientManage/config/${appId}"><spring:message code="appSysConfig.clientManage"/></a></li>
    	 </c:if>
	 <li><a class="return" href="${ctx}/admin/declaration/config/${appId}"><spring:message code="conceal.declaration"/> </a></li>
     	<c:if test="${appType == 1}">
     	<li class="active"><a class="return" href="${ctx}/app/backup/config/${appId}"><spring:message code="appSysConfig.backupManage"/> </a></li>
     	</c:if>
    </ul>
	
   	<form id="backupForm" class="form-horizontal" enctype="multipart/form-data" method="post" action="">
   	<div class="form-con">
		<input type="hidden" id="appId" name="appId" value="${appId}" />       
        <div class="control-group">
            <label class="control-label" for="input"><spring:message code="backupManage.rule.white"/>:</label>
            <div class="controls">
                <textarea class="span8" rows="2" type="text" id="whiteRule" name="whiteRule" value="" placeholder='<spring:message code="backupManage.rule.whiteInfo"/>'>${whiteRule}</textarea>
                <span class="validate-con"><div></div></span>
            </div>
        </div>

        <div class="control-group">
            <div class="controls">
            	<button id="submit_btn" type="button" onclick="saveWhiteRule()" class="btn btn-primary"><spring:message code="common.save"/></button>
            </div>
        </div>
    </div>
    <div class="form-con">
        <div class="control-group">
            <label class="control-label" for="input"><spring:message code="backupManage.rule.black"/>:</label>
            <div class="controls">
                <textarea class="span8" rows="2" type="text" id="blackRule" name="blackRule" value="" placeholder='<spring:message code="backupManage.rule.blackInfo"/>'>${blackRule}</textarea>
                <span class="validate-con"><div></div></span>
            </div>
        </div>
        <div class="control-group">
            <div class="controls">
            	<button id="submit_btn" type="button" onclick="saveBlackRule()" class="btn btn-primary"><spring:message code="common.save"/></button>
            </div>
        </div>
        <input type="hidden" id="token" name="token" value="${token}"/>
    </div>
    </form>
</div>
<script type="text/javascript">
var saveState = "${saveState}";
$(document).ready(function() {
	if(saveState == "success"){
		top.handlePrompt("success",'<spring:message code="common.saveSuccess"/>');
	}else if(saveState == "fail"){
		top.handlePrompt("error",'<spring:message code="common.saveFail"/>');
	}
	
	$("#backupForm").validate({ 
		rules: { 
			   whiteRule:{
			       maxlength:[2048]
			   },
			   blackRule:{
			       maxlength:[2048]
			   }
		}
 	}); 
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
	
	if(!placeholderSupport()){
		placeholderCompatible();
	};
});

function saveWhiteRule(){
	if(!$("#backupForm").valid()) {
        return false;
    } 
	$.ajax({
		type: "POST",
		url:"${ctx}/app/backup/white/save",
		data:$('#backupForm').serialize(),
		error: function(request) {
			top.handlePrompt("error",'<spring:message code="common.saveFail"/>');
		},
		success: function() {
			top.handlePrompt("success",'<spring:message code="common.saveSuccess"/>');
		}
	});
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
}
function saveBlackRule(){
	if(!$("#backupForm").valid()) {
        return false;
    } 
	$.ajax({
		type: "POST",
		url:"${ctx}/app/backup/black/save",
		data:$('#backupForm').serialize(),
		error: function(request) {
			top.handlePrompt("error",'<spring:message code="common.saveFail"/>');
		},
		success: function() {
			top.handlePrompt("success",'<spring:message code="common.saveSuccess"/>');
		}
	});
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
}
</script>
</body>
</html>
