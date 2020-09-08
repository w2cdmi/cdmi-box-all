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
<style>
.form-horizontal .hidden-input{ display:none;}
</style>
</head>
<body>
<div class="sys-content sys-content-en">
	<div class="clearfix control-group">
		<a  class="return btn btn-small pull-right" href="${ctx}/app/appmanage/authapp/list"><i class="icon-backward"></i>&nbsp;<spring:message code="common.back"/></a>
		<h5 class="pull-left" style="margin: 3px 0 0 4px;"><a href="${ctx}/app/appmanage/authapp/list"><c:out value='${appId}'/></a>&nbsp;>&nbsp;<spring:message code="appSysConfig.app.config"/></h5>	
	</div>
    <ul class="nav nav-tabs clearfix">
     	<li class="active"><a class="return" href="${ctx}/app/basicconfig/config/<c:out value='${appId}'/>"><spring:message code="appSysConfig.basicconfig"/> </a></li>
     	<c:if test="${appType == 1}">
     	<li><a class="return" href="${ctx}/app/logo/config/<c:out value='${appId}'/>"><spring:message code="appSysConfig.logo.config"/> </a></li>
     	</c:if>
     	<c:if test="${appType == 1}">
     	<li><a class="return" href="${ctx}/app/clientManage/config/<c:out value='${appId}'/>"><spring:message code="appSysConfig.clientManage"/> </a></li>
     	</c:if>
     	<li><a class="return" href="${ctx}/admin/declaration/config/${appId}"><spring:message code="conceal.declaration"/> </a></li>
     	<c:if test="${appType == 1}">
     	<li><a class="return" href="${ctx}/app/backup/config/${appId}"><spring:message code="appSysConfig.backupManage"/> </a></li>
     	</c:if>
     </ul>    	
<div class="form-horizontal form-con clearfix">
   	<form id="basicConfigForm" class="form-horizontal" method="post">
       <div class="control-group">
            <label class="control-label" for="input"><spring:message code="user.manager.labelIsTeam"/>:</label>
        	<div class="controls">
            	<label class="checkbox"><input type="checkbox" id="chkEnableTeamSpace" name="chkEnableTeamSpace" <c:if test="${appBasicConfig.enableTeamSpace}">checked="checked"</c:if> /><spring:message code="user.manager.isCreateTeam"/></label>
            </div>
        </div>
        <div id="maxTeamSpacesDiv" <c:if test="${!appBasicConfig.enableTeamSpace}">style="display:none"</c:if>>
	         <div class="control-group">
	                <label class="control-label" for="input"><em>*</em><spring:message code="basicConfig.TeamSpaces"/></label>
		            <div class="controls">
		            	<label class="checkbox inline"><input type="checkbox" id="teamSpaceQuotaCheckBox" <c:if test="${appBasicConfig.teamSpaceQuota == -1}">checked="checked"</c:if> name="teamSpaceQuotaCheckBox"/><spring:message code="basicConfig.limit"/></label>
		                <input class="span4 ${appBasicConfig.teamSpaceQuota == -1 ? "hidden-input" : ""}" type="text" id="teamSpaceQuotaInput" name="teamSpaceQuotaInput" value="${appBasicConfig.teamSpaceQuota == -1 ? 0 : appBasicConfig.teamSpaceQuota}" />
		                <span class="validate-con"><div></div></span>
		            </div>                         
	        </div>
	        <div class="control-group">
	                <label class="control-label" for="input"><em>*</em><spring:message code="basicConfig.teamSpaceMembers"/></label>
		            <div class="controls">
		                <label class="checkbox inline"><input type="checkbox" <c:if test="${appBasicConfig.teamSpaceMaxMembers == -1}">checked="checked"</c:if> id="teamSpaceMaxMembersCheckBox" name="teamSpaceMaxMembersCheckBox"/><spring:message code="basicConfig.limit"/></label>
		                <input class="span4 ${appBasicConfig.teamSpaceMaxMembers == -1 ? "hidden-input" : ""}" type="text" id="teamSpaceMaxMembersInput" name="teamSpaceMaxMembersInput" value="${appBasicConfig.teamSpaceMaxMembers == -1 ? 0 : appBasicConfig.teamSpaceMaxMembers}" />
		                <span class="validate-con"><div></div></span>
		            </div>          
	        </div> 
	        <div class="control-group">
	               <label class="control-label" for="input"><em>*</em><spring:message code="user.manager.labelTeamNumber"/></label>
	               <div class="controls" >
			            <label class="checkbox inline"><input type="checkbox" <c:if test="${appBasicConfig.maxTeamSpaces == -1}">checked="checked"</c:if>  id="maxTeamSpacesCheckBox" name="maxTeamSpacesCheckBox"/><spring:message code="basicConfig.limit"/></label>
			            <input class="span4 ${appBasicConfig.maxTeamSpaces == -1 ? "hidden-input" : ""}" type="text" id="maxTeamSpacesInput" name="maxTeamSpacesInput" value="${appBasicConfig.maxTeamSpaces == -1 ? 0 : appBasicConfig.maxTeamSpaces}" />
			            <span class="validate-con"><div></div></span>
	               </div>   
		    </div>
        </div>
        <br/>
        <div class="control-group">
                <label class="control-label" for="input"><em>*</em><spring:message code="basicConfig.FileVersions"/></label>
	            <div class="controls">
	            	<label class="checkbox inline"><input type="checkbox"  <c:if test="${appBasicConfig.maxFileVersions == -1}">checked="checked"</c:if> id="maxFileVersionsCheckBox" name="maxFileVersionsCheckBox"/><spring:message code="basicConfig.limit"/></label>
	                <input class="span4 ${appBasicConfig.maxFileVersions == -1 ? "hidden-input" : ""}" type="text" id="maxFileVersionsInput" name="maxFileVersionsInput" value="${appBasicConfig.maxFileVersions == -1 ? 0 : appBasicConfig.maxFileVersions}" />
	                <span class="validate-con"><div></div></span>
	            </div>                         
        </div>
        <div class="control-group">
                <label class="control-label" for="input"><em>*</em><spring:message code="basicConfig.userSpaceQuota"/></label>
	            <div class="controls">
	            	<label class="checkbox inline"><input type="checkbox" <c:if test="${appBasicConfig.userSpaceQuota == -1}">checked="checked"</c:if> id="userSpaceQuotaCheckBox" name="userSpaceQuotaCheckBox"/><spring:message code="basicConfig.limit"/></label>
	                <input class="span4 ${appBasicConfig.userSpaceQuota == -1 ? "hidden-input" : ""}" type="text" id="userSpaceQuotaInput" name="userSpaceQuotaInput" value="${appBasicConfig.userSpaceQuota == -1 ? 0 : appBasicConfig.userSpaceQuota}" />
	                <span class="validate-con"><div></div></span>
	            </div>          
        </div>
        
        <div class="control-group">
                <label class="control-label" for="input"><em>*</em><spring:message code="basicConfig.uploadBandWidth"/>(KB):</label>
	            <div class="controls">
	            	<label class="checkbox inline"><input type="checkbox" <c:if test="${appBasicConfig.uploadBandWidth == -1}">checked="checked"</c:if> id="uploadBandWidthCheckBox" name="uploadBandWidthCheckBox"/><spring:message code="basicConfig.limit"/></label>
	                <input class="span4 ${appBasicConfig.uploadBandWidth == -1 ? "hidden-input" : ""}" type="text" id="uploadBandWidthInput" name="uploadBandWidthInput" value="${appBasicConfig.uploadBandWidth == -1 ? 0 : appBasicConfig.uploadBandWidth}" />
	                <span class="validate-con"><div></div></span>
	            </div>          
        </div>
        <div class="control-group">
                <label class="control-label" for="input"><em>*</em><spring:message code="basicConfig.downloadBandWidth"/>(KB):</label>
	            <div class="controls">
	            	<label class="checkbox inline"><input type="checkbox" <c:if test="${appBasicConfig.downloadBandWidth == -1}">checked="checked"</c:if> id="downloadBandWidthCheckBox" name="downloadBandWidthCheckBox"/><spring:message code="basicConfig.limit"/></label>
	                <input class="span4 ${appBasicConfig.downloadBandWidth == -1 ? "hidden-input" : ""}" type="text" id="downloadBandWidthInput" name="downloadBandWidthInput" value="${appBasicConfig.downloadBandWidth == -1 ? 0 : appBasicConfig.downloadBandWidth}" />
	                <span class="validate-con"><div></div></span>
	            </div>          
        </div> 
        
        <div class="control-group">
                <label class="control-label" for="input"><em>*</em><spring:message code="basicConfig.userDefaultRegion"/></label>
	            <div class="controls">
     				<select class="span4"  name="userDefaultRegion" id="userDefaultRegion">
	                    <c:forEach items="${regionList}" var="region">
	        				<option value="<c:out value='${region.id}'/>" <c:if test="${appBasicConfig.userDefaultRegion == region.id}">selected="selected"</c:if>><c:out value='${region.name}'/></option>
	        			</c:forEach>
	                </select>
	                <span class="validate-con bottom"><div></div></span>
	            </div>          
        </div>  
        <div class="control-group">
            <div class="controls">
            	<button id="submit_btn" type="button" onClick="saveSecurityConfig()" class="btn btn-primary"><spring:message code="common.save"/></button>
            </div>
        </div>
        <input type="hidden" id="teamSpaceQuota" name="teamSpaceQuota"/>
        <input type="hidden" id="teamSpaceMaxMembers" name="teamSpaceMaxMembers"/>
        <input type="hidden" id="maxTeamSpaces" name="maxTeamSpaces"/>
        <input type="hidden" id="maxFileVersions" name="maxFileVersions"/>
        <input type="hidden" id="userSpaceQuota" name="userSpaceQuota"/>
   	    <input type="hidden" id="enableTeamSpace" name="enableTeamSpace" value="<c:out value='${appBasicConfig.enableTeamSpace}'/>" />
   	    <input type="hidden" id="uploadBandWidth" name="uploadBandWidth"/>
        <input type="hidden" id="downloadBandWidth" name="downloadBandWidth"/>
        <input type="hidden" id="appId" name="appId" value="<c:out value='${appId}'/>" />
        <input type="hidden" id="token" name="token" value="<c:out value='${token}'/>"/>
        
	</form>
</div>
</div>
<script type="text/javascript">
$(document).ready(function() {
	$("#basicConfigForm").validate({ 
		rules: { 
			   teamSpaceQuotaInput:{
				   required:true, 
				   digits:true,
			       min:1,
			       max:999999
			   },
			   teamSpaceMaxMembersInput: { 
				   required:true, 
				   digits:true,
			       min:1,
			       max:999999
			   },
			   maxTeamSpacesInput: { 
				   required:true, 
				   digits:true,
			       min:1,
			       max:999999
			   },
			   maxFileVersionsInput: {
				   required:true,
				   digits:true,
			       min:1,
			       max:999999
			   },
			   userSpaceQuotaInput: { 
				   required:true, 
				   digits:true,
			       min:1,
			       max:999999
			   },
			   uploadBandWidthInput: { 
				   required:true, 
				   digits:true,
			       min:100,
			       max:999999
			   },
			   downloadBandWidthInput: { 
				   required:true, 
				   digits:true,
			       min:100,
			       max:999999
			   },
			   userDefaultRegion:{
				   required:true, 
				   digits:true,
			       min:1,
			       max:999999 
			   }
		},
		ignore: ".hidden-input",
		onkeyup:function(element) {$(element).valid()},
		focusCleanup:true,
		onfocusout:false
    }); 
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
	

});


$("#teamSpaceQuotaCheckBox").click(function(){ 
	if(this.checked){
		$('#teamSpaceQuotaInput').addClass("hidden-input")
			.val("0")
			.next().find("> div > span").remove();
		
	}else{ 
		$('#teamSpaceQuotaInput').removeClass("hidden-input");
		$('#teamSpaceQuotaInput').val("");
	}
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
});
$("#teamSpaceMaxMembersCheckBox").click(function(){ 
	if(this.checked){
		$('#teamSpaceMaxMembersInput').addClass("hidden-input")
		.val("0")
		.next().find("> div > span").remove();
	}else{ 
		$('#teamSpaceMaxMembersInput').removeClass("hidden-input");
		$('#teamSpaceMaxMembersInput').val("");
	}
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
});
$("#maxTeamSpacesCheckBox").click(function(){ 
	if(this.checked){
		$('#maxTeamSpacesInput').addClass("hidden-input")
		.val("0")
		.next().find("> div > span").remove();
	}else{ 
		$('#maxTeamSpacesInput').removeClass("hidden-input");
		$('#maxTeamSpacesInput').val("");
	}
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
});
$("#maxFileVersionsCheckBox").click(function(){ 
	if(this.checked){
		$('#maxFileVersionsInput').addClass("hidden-input")
		.val("0")
		.next().find("> div > span").remove();
	}else{ 
		$('#maxFileVersionsInput').removeClass("hidden-input");
		$('#maxFileVersionsInput').val("");
	}
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
});
$("#userSpaceQuotaCheckBox").click(function(){ 
	if(this.checked){
		$('#userSpaceQuotaInput').addClass("hidden-input")
		.val("0")
		.next().find("> div > span").remove();
	}else{ 
		$('#userSpaceQuotaInput').removeClass("hidden-input");
		$('#userSpaceQuotaInput').val("");
	}
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
});
$("#uploadBandWidthCheckBox").click(function(){ 
	if(this.checked){
		$('#uploadBandWidthInput').addClass("hidden-input")
		.val("0")
		.next().find("> div > span").remove();
	}else{ 
		$('#uploadBandWidthInput').removeClass("hidden-input");
		$('#uploadBandWidthInput').val("");
	}
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
});
$("#downloadBandWidthCheckBox").click(function(){ 
	if(this.checked){
		$('#downloadBandWidthInput').addClass("hidden-input")
		.val("0")
		.next().find("> div > span").remove();
	}else{ 
		$('#downloadBandWidthInput').removeClass("hidden-input");
		$('#downloadBandWidthInput').val("");
	}
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
});


$("#chkEnableTeamSpace").click(function(){ 
	if(this.checked){
		$("#enableTeamSpace").val("true");
		$('#maxTeamSpacesDiv').show();
	}else{ 
		$("#enableTeamSpace").val("false");
		$("maxTeamSpaces").val("0");
		$('#maxTeamSpacesDiv').hide();
	}
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
});
function setParameter(){
	$("#teamSpaceQuota").val($("#teamSpaceQuotaInput").val() == 0 ? -1 : $("#teamSpaceQuotaInput").val());
	$("#teamSpaceMaxMembers").val($("#teamSpaceMaxMembersInput").val() == 0 ? -1 : $("#teamSpaceMaxMembersInput").val());
	$("#userSpaceQuota").val($("#userSpaceQuotaInput").val() == 0 ? -1 : $("#userSpaceQuotaInput").val());
	$("#maxTeamSpaces").val($("#maxTeamSpacesInput").val() == 0 ? -1 : $("#maxTeamSpacesInput").val());
	$("#maxFileVersions").val($("#maxFileVersionsInput").val() == 0 ? -1 : $("#maxFileVersionsInput").val());
	$("#uploadBandWidth").val($("#uploadBandWidthInput").val() == 0 ? -1 : $("#uploadBandWidthInput").val());
	$("#downloadBandWidth").val($("#downloadBandWidthInput").val() == 0 ? -1 : $("#downloadBandWidthInput").val());
}

function saveSecurityConfig(){
	if(!$("#basicConfigForm").valid()) {
        return false;
    }  
	setParameter();
	$.ajax({
		type: "POST",
		url:"${ctx}/app/basicconfig/save",
		data:$('#basicConfigForm').serialize(),
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
