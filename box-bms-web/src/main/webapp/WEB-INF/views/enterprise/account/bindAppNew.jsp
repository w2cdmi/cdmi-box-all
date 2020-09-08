<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cse" uri="http://cse.huawei.com/custom-function-taglib"%> 

<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title></title>
<%@ include file="../../common/common.jsp"%> 
<style>
.form-horizontal .hidden-input{ display:none;}
</style>
</head>
<body>
<div class="pop-content">
	<div class="form-con">
   	<form class="form-horizontal" id="bindAppForm" name="bindAppForm">
   			<div class="control-group">
		       <label class="control-label" for=""><em>*</em><spring:message code="enterpriseList.catch.app"/>：</label>
		       <div class="controls" >
			       <select class="span4" id="authAppIds" name="authAppIds">
		               <option value="0"> <spring:message code="user.tag.select"/> </option>
		                <c:forEach items="${excludeAppList}" var="authApp">
		                  	<option value="<c:out value='${authApp.authAppId}'/>"><c:out value='${authApp.authAppId}'/></option>
						</c:forEach>
					</select>
					<span class="validate-con bottom"><div></div></span>
		       </div>
   			</div>
			<div class="control-group">
	               <label class="control-label" for="input"><em>&nbsp;&nbsp;</em><spring:message code="enterprise.max.members"/>：</label>
	               <div class="controls" >
			            <label class="checkbox inline"><input type="checkbox" checked="checked" id="maxMemberCheckBox" name="maxMemberCheckBox"/><spring:message code="basicConfig.limit"/></label>
			            <input class="span3 hidden-input" type="text" id="maxMember" name="maxMember" value="" />
			            <span class="validate-con bottom"><div></div></span>
	               </div>   
		    </div>
			<div class="control-group">
	               <label class="control-label" for="input"><em>&nbsp;&nbsp;</em><spring:message code="enterprise.max.teamspaces"/>：</label>
	               <div class="controls" >
			            <label class="checkbox inline"><input type="checkbox" checked="checked" id="maxTeamSpacesCheckBox" name="maxTeamSpacesCheckBox"/><spring:message code="basicConfig.limit"/></label>
			            <input class="span3 hidden-input" type="text" id="maxTeamspace" name="maxTeamspace" value="" />
			            <span class="validate-con bottom"><div></div></span>
	               </div>   
		    </div>
			<div class="control-group">
	               <label class="control-label" for="input"><em>&nbsp;&nbsp;</em><spring:message code="enterprise.max.space"/>(GB)：</label>
	               <div class="controls" >
			            <label class="checkbox inline"><input type="checkbox" checked="checked" id="maxSpaceCheckBox" name="maxSpaceCheckBox"/><spring:message code="basicConfig.limit"/></label>
			            <input class="span3 hidden-input" type="text" id="maxSpace" name="maxSpace" value="" />
			            <span class="validate-con bottom"><div></div></span>
	               </div>   
		    </div>
			<div class="control-group">
	               <label class="control-label" for="input"><em>&nbsp;&nbsp;</em><spring:message code="file.previewable"/>：</label>
	               <div class="controls" >
			            <label class="checkbox inline"><input type="checkbox" id="filePreviewableCheckBox" name="filePreviewableCheckBox"/><spring:message code="log.save.yes"/></label>
			            <input class="span3" type="hidden" id="filePreviewable" name="filePreviewable" value="0" />
	               </div>   
		    </div>
		    <input type="hidden" id="enterpriseId" name="enterpriseId" value="<c:out value='${enterpriseId}'/>" />
		    <input type="hidden" id="token" name="token" value="<c:out value='${token}'/>"/>
	</form>
	
    </div>
</div>
</body>
<script type="text/javascript">
$(document).ready(function() {
	$("#bindAppForm").validate({ 
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
			   },
			   authAppIds: {
				   required:true,
				   authAppIdCheck :true
			   }
		   },
		   messages : {
			   authAppIds : {
				   authAppIdCheck : '<spring:message  code="enterprise.choose.app"/>'
				}
			},
		ignore: ".hidden-input",
		onkeyup:function(element) {$(element).valid()},
		focusCleanup:true,
		onfocusout:false
    }); 
	$.validator.addMethod(
			"authAppIdCheck", 
			function(value, element) {
				if(value == 0){
					return false;
				}
				return true;
			}
	);
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
	
});

$("#maxTeamSpacesCheckBox").click(function(){
	if(this.checked){
		$('#maxTeamspace').addClass("hidden-input")
		.val("0")
		.next().find("> div > span").remove();
	}else{
		$('#maxTeamspace').removeClass("hidden-input");
		$('#maxTeamspace').val("");
	}
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
});

$("#maxMemberCheckBox").click(function(){ 
	if(this.checked){
		$('#maxMember').addClass("hidden-input")
		.val("0")
		.next().find("> div > span").remove();
	}else{ 
		$('#maxMember').removeClass("hidden-input");
		$('#maxMember').val("");
	}
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
});

$("#maxSpaceCheckBox").click(function(){ 
	if(this.checked){
		$('#maxSpace').addClass("hidden-input")
		.val("0")
		.next().find("> div > span").remove();
	}else{ 
		$('#maxSpace').removeClass("hidden-input");
		$('#maxSpace').val("");
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
	$("#maxTeamspace").val($("#maxTeamspace").val() == 0 ? -1 : $("#maxTeamspace").val());
	$("#maxMember").val($("#maxMember").val() == 0 ? -1 : $("#maxMember").val());
	$("#maxSpace").val($("#maxSpace").val() == 0 ? -1 : $("#maxSpace").val());
}

function bindApp(){
	if(!$("#bindAppForm").valid()) {
        return false;
    }
	setParameter();
	$.ajax({
		type: "POST",
		url:"${ctx}/enterprise/account/bindApp",
		data:$('#bindAppForm').serialize(),
		error: function(request) {
			handlePrompt("error",'<spring:message code="selectAdmin.bindFailed"/>');
		},
		success: function() {
			top.ymPrompt.close();
			top.handlePrompt("success",'<spring:message code="selectAdmin.bindSucceed"/>');
		}
	});
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
}
</script>
</html>
