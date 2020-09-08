<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cse"
	uri="http://cse.huawei.com/custom-function-taglib"%>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title></title>
<%@ include file="../../common/common.jsp"%>
<style>
.form-horizontal .hidden-input {
	display: none;
}
</style>
</head>
<body>
	<div class="pop-content">
		<div class="form-con">
			<form class="form-horizontal" id="modifyAccountForm"
				name="modifyAccountForm">
				<div class="control-group">
					<label class="control-label" for=""><em>*</em>
					<spring:message code="account.current.authAppId" />：</label>
					<div class="controls">
						<span class="uneditable-input span4"><c:out value="${account.authAppId}"/></span> <span
							class="validate-con bottom"><div></div></span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="input"><em>&nbsp;&nbsp;</em>
					<spring:message code="account.max.members" />：</label>
					<div class="controls">
						
						<c:choose>
							<c:when test="${account.maxMember == -1 }">
								  <label class="checkbox inline">
									  <input type="checkbox" checked="checked" id="maxMemberCheckBox" name="maxMemberCheckBox" />
									  <spring:message code="basicConfig.limit" />
								  </label> 
								  <input class="span3 hidden-input" type="text" id="maxMember" name="maxMember" value="" />
							</c:when>
							<c:otherwise>
				           		  <label class="checkbox inline">
									  <input type="checkbox" id="maxMemberCheckBox" name="maxMemberCheckBox" />
									  <spring:message code="basicConfig.limit" />
								  </label> 
								  <input class="span3" type="text" id="maxMember" name="maxMember" value="<c:out value='${account.maxMember}'/>" />
				            </c:otherwise>
						</c:choose>
						<span class="validate-con bottom"><div></div></span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="input"><em>&nbsp;&nbsp;</em>
					<spring:message code="account.max.teamspaces" />：</label>
					<div class="controls">
					    <c:choose>
						  <c:when test="${account.maxTeamspace ==-1}">
					          <label class="checkbox inline">
					          <input type="checkbox" checked="checked" id="maxTeamSpacesCheckBox" name="maxTeamSpacesCheckBox" />
						      <spring:message code="basicConfig.limit" /></label>
						      <input class="span3 hidden-input" type="text" id="maxTeamspace" name="maxTeamspace" value="" />						 
							</c:when>
							<c:otherwise> 
			          		    <label class="checkbox inline">
					            <input type="checkbox" id="maxTeamSpacesCheckBox" name="maxTeamSpacesCheckBox" />
						        <spring:message code="basicConfig.limit" /></label>
						        <input class="span3" type="text" id="maxTeamspace" name="maxTeamspace" value="<c:out value='${account.maxTeamspace}'/>" />		
			           		 </c:otherwise>
						</c:choose>
						
						<span class="validate-con bottom"><div></div></span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="input"><em>&nbsp;&nbsp;</em>
					<spring:message code="account.max.space" />(GB)：</label>
					<div class="controls">
						<c:choose>
							<c:when test="${account.maxSpace ==-1}">
								<label class="checkbox inline">
								<input type="checkbox" checked="checked" id="maxSpaceCheckBox" name="maxSpaceCheckBox" />
						        <spring:message code="basicConfig.limit" /></label> 
					        	<input class="span3 hidden-input" type="text" id="maxSpace"  name="maxSpace" value="" /> 
							</c:when> 
							<c:otherwise>
				         	   <label class="checkbox inline">
				         	   <input type="checkbox" id="maxSpaceCheckBox" name="maxSpaceCheckBox" />
				         	   <spring:message code="basicConfig.limit" /></label>
				         	   <input class="span3" type="text" id="maxSpace" name="maxSpace" value="<c:out value='${account.maxSpace}'/>" /> 
				            </c:otherwise>
						</c:choose>
						<span class="validate-con bottom"><div></div></span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="input"><em>&nbsp;&nbsp;</em>
					<spring:message code="account.file.previewable" />：</label>
					<div class="controls">
						<label class="checkbox inline">
					    <c:choose>
							<c:when test="${account.filePreviewable == true}">	
						    <input type="checkbox" id="filePreviewableCheckBox" name="filePreviewableCheckBox" checked="checked"/>
					         </c:when>
					    <c:otherwise>
					        <input type="checkbox" id="filePreviewableCheckBox" name="filePreviewableCheckBox" />
					    </c:otherwise>
					    </c:choose>
					<spring:message code="log.save.yes" /></label>
						 <input class="span3" type="hidden" id="filePreviewable" name="filePreviewable" value="0" />
					</div>
				</div>
				<input type="hidden" id="enterpriseId" name="enterpriseId"
					value="<c:out value='${enterpriseId}'/>" /> <input type="hidden" id="token"
					name="token" value="<c:out value='${token}'/>" /> <input type="hidden"
					id="accountId" name="accountId" value="<c:out value='${account.accountId}'/>" />
			</form>

		</div>
	</div>
</body>
<script type="text/javascript">
$(document).ready(function() {
	$("#modifyAccountForm").validate({ 
		rules: { 
			   maxMember:{
				   required:true, 
				   digits:true,
			       min:1,
			       max:1000000
			   },
			   maxTeamspace: { 
				   required:true, 
				   digits:true,
			       min:1,
			       max:1000000
			   },
			   maxSpace: { 
				   required:true, 
				   digits:true,
			       min:1,
			       max:1000000
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

$("#maxTeamSpacesCheckBox").click(function(){
	if(this.checked){
		$('#maxTeamspace').addClass("hidden-input")
		.next().find("> div > span").remove();
	}else{
		$('#maxTeamspace').removeClass("hidden-input"); 
	}
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
});

$("#maxMemberCheckBox").click(function(){ 
	if(this.checked){
		$('#maxMember').addClass("hidden-input")
		.next().find("> div > span").remove();
	}else{ 
		$('#maxMember').removeClass("hidden-input");
	}
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
});

$("#maxSpaceCheckBox").click(function(){ 
	if(this.checked){
		$('#maxSpace').addClass("hidden-input")
		.next().find("> div > span").remove();
	}else{ 
		$('#maxSpace').removeClass("hidden-input"); 
	}
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
});

$("#filePreviewableCheckBox").click(function(){
	if(this.checked){
		$("#filePreviewable").val("1");
	}else{
		$("#filePreviewable").val("0");
	}
});

function setParameter(){ 
 	$("#maxTeamspace").val($("#maxTeamSpacesCheckBox").is(":checked") ? -1 : $("#maxTeamspace").val());
	$("#maxMember").val($("#maxMemberCheckBox").is(":checked") ? -1 : $("#maxMember").val());
	$("#maxSpace").val($("#maxSpaceCheckBox").is(":checked") ? -1 : $("#maxSpace").val());
}

function modifyAccount(authApp){
	if(!$("#modifyAccountForm").valid()) {
        return false;
    }
	setParameter();
	$.ajax({
		type: "POST",
		url:"${ctx}/enterprise/account/modifyAccount",
		data:$('#modifyAccountForm').serialize(),
		error: function(request) {
			top.handlePrompt("error",'<spring:message code="selectAdmin.modifyFailed"/>');
		},
		success: function() {
			top.ymPrompt.close();
			top.handlePrompt("success",'<spring:message code="selectAdmin.modifySucceed"/>');
			top.window.frames[0].location = "${ctx}/enterprise/account/enterpriseAppList/"+authApp;
		}
	});
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
}
</script>
</html>
