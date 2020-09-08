<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
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
	<div class="pop-content">
		<div class="form-con">
			<input type="hidden" id="enterpriseId" name="enterpriseId"
				value="${enterpriseId}" />
			<form class="form-horizontal" id="creatAdminForm"
				name="creatAdminForm">
				<div class="control-group">
					<label class="control-label" for=""><em>*</em> <spring:message
							code="authorize.label.username" /></label>
					<div class="controls">
						<c:if test="${account.loginName != null}">
							<input type="text" id="loginName" name="loginName" class="span4"
								value="${account.loginName}" readOnly="readonly" />
						</c:if>
						<c:if test="${account.loginName == null}">
							<input type="text" id="loginName" name="loginName" class="span4" />
						</c:if>
						<span class="validate-con bottom"><div></div></span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for=""><em>*</em> <spring:message
							code="authorize.label.name" /></label>
					<div class="controls">
						<c:if test="${account.name == null}">
							<input type="text" id="name" name="name" class="span4" />
						</c:if>
						<c:if test="${account.name != null}">
							<input type="text" id="name" name="name" class="span4"
								value="${account.name}" />
						</c:if>
						<span class="validate-con bottom"><div></div></span>
					</div>
				</div>
				 <div class="control-group">
					<label class="control-label" for=""><em>*</em> <spring:message
							code="authorize.label.mail" /></label>
					<div class="controls">
						<c:if test="${account.email == null}">
							<input type="text" id="email" name="email" class="span4" />
						</c:if>
						<c:if test="${account.email != null}">
							<input type="text" id="email" name="email" class="span4"
								value="${account.email}" readOnly="readonly" />
						</c:if>
						<span class="validate-con bottom"><div></div></span>
						<%-- <div class="alert">
							<i class="icon-lightbulb icon-orange"></i>
							<spring:message code="license.confirm.cliew" />
						</div> --%>
					</div>
				</div> 

				<div class="control-group">
					<div class="controls">
						<label class="checkbox">
						
							<input type="checkbox" id="isConfigEnterpriseUser" name="isConfigEnterpriseUser"
							value="1" <c:if test="${account.isConfigEnterpriseUser == '1'}">checked="checked"</c:if> /> 
							<spring:message code="isystem.enterprise.user.add" /></label>
					</div>
				</div>
				<%-- <div class="control-group" id="employeeUserNameDiv" <c:if test="${account.isConfigEnterpriseUser != '1'}">style="display:none"</c:if> >
					<label class="control-label" for="input"><spring:message
							code="isystem.enterprise.user.username" /></label>
					<div class="controls">
						<input class="span4" type="text" id="employeeUserName"
							name="employeeUserName" value="${account.employeeUserName}" /> <span class="validate-con"><div></div></span>
					</div>
				</div>
				<div class="control-group" id="employeeNameDiv" <c:if test="${account.isConfigEnterpriseUser != '1'}">style="display:none"</c:if>>
					<label class="control-label" for="input"><spring:message
							code="isystem.enterprise.user.name" /></label>
					<div class="controls">
						<input class="span4" type="text" id="employeeName"
							name="employeeName" value="${account.employeeName}" />
					</div>
				</div>
				<div class="control-group" id="employeeEmailDiv" <c:if test="${account.isConfigEnterpriseUser != '1'}">style="display:none"</c:if>>
					<label class="control-label" for="input"><spring:message
							code="isystem.enterprise.user.email" /></label>
					<div class="controls">
						<input class="span4" type="text" id="employeeEmail"
							name="employeeEmail" value="${account.employeeEmail}" />
					</div>
				</div> --%>
				<%-- <div class="control-group">
	            <label class="control-label" for="input"><spring:message code="athorize.createUser.pwd"/></label>
	            <div class="controls">
	                <input class="span4" type="password" id="password" name="password" value="" autocomplete="off"/>
	                <span class="validate-con bottom"><div></div></span>
	            </div>
	        </div>
			<div class="control-group">
	            <label class="control-label" for="input"><spring:message code="authorize.user.pwd.confirm"/></label>
	            <div class="controls">
	                <input class="span4" type="password" id="confirmPassword" name="confirmPassword" value="" autocomplete="off"/>
	                <span class="validate-con bottom"><div></div></span>
	            </div>
	        </div> 
				<div class="control-group">
					<label class="control-label" for=""><em>*</em>
					<spring:message code="common.role" /></label>
					<div class="controls list-checkbox-auth">
						<label class="radio inline"> <input name="type"
							id="clusterRole" type="radio" value="1"> <spring:message
								code="isystem.system.super.admin" />
						</label> <label class="radio inline"> <input name="type"
							id="appRole" type="radio" value="2"> <spring:message
								code="isystem.system.admin" />
						</label> <label class="radio inline"> <input name="type"
							id="configRole" type="radio" value="3"> <spring:message
								code="isystem.bms.super.admin" />
						</label> <label class="radio inline"> <input name="type"
							id="logRole" type="radio" value="4"> <spring:message
								code="isystem.bms.admin" />
						</label>
					</div>
				</div>
--%>
				<div class="control-group">
					<div class="controls">
						<button id="submit_btn" type="button"
							onclick="submitCreateAdminUser()" class="btn btn-primary" <c:if test="${account.loginName != null}">disabled</c:if>>
							<spring:message code="common.save" />
						</button>
							<button id="configOver_btn" type="button"
								onclick="submitInitConfigver()" class="btn btn-primary" <c:if test="${account.loginName == null}">style="display:none"</c:if>>
								<spring:message code="isystem.init.config.over" />
							</button>
					</div>
				</div>
				<%-- <input type="hidden" name="token" value="${cse:htmlEscape(token)}"/> --%>
			</form>
		</div>
	</div>
	<script type="text/javascript">
		$.validator
				.addMethod(
						"isLoginName",
						function(value, element) {
							var validName = /^[a-zA-Z]{1}[a-zA-Z0-9]+$/;
							return validName.test(value);
						},
						$.validator
								.format('<spring:message code="athorize.login.username.validator"/>'));
		/* $.validator.addMethod(
		 "isValidPwdEnhance", 
		 function(value, element, param) { 
		 if(value == ""){
		 return true;
		 }
		 var ret = false;
		 $.ajax({
		 type: "POST",
		 async: false,
		 url:"${ctx}/syscommon/validpwd",
		 data:$("#creatAdminForm").serialize(),
		 success: function(data) {
		 ret = true;
		 }
		 });
		 return ret;
		 }, 
		 $.validator.format('<spring:message code="authorize.createAdmin.pwd.validator"/>')
		 );  */
		$(document).ready(function() {
			$("#creatAdminForm").validate({
				rules : {
					loginName : {
						required : true,
						rangelength : [ 4, 60 ],
						isLoginName : true
					},
					name : {
						required : true,
						rangelength : [ 2, 60 ]
					},
					email : {
						required : true,
						//isValidEmail : true,
						maxlength : [ 255 ]
					},
					/* password: { 
					   isValidPwdEnhance:true
					}, */
					confirmPassword : {
						equalTo : "#password"
					}
				}
			});
			$("label").tooltip({
				container : "body",
				placement : "top",
				delay : {
					show : 100,
					hide : 0
				},
				animation : false
			});
		});

		function submitCreateAdminUser() {
			if (!$("#creatAdminForm").valid()) {
				return false;
			}
			$.ajax({
				type : "POST",
				url : "${ctx}/systeminit/isystem/admin/save",
				data : $('#creatAdminForm').serialize(),
				error : function(request) {
					handlePrompt("error",
							'<spring:message code="common.createFail"/>');
				},
				success : function() {
					$("#submit_btn").attr("disabled",true);
					$('#configOver_btn').show();
				}
			});
		}
		
		function submitInitConfigver(){
			$.ajax({
				type : "GET",
				url : "${ctx}/systeminit/isystem/config/over",
				success : function(data) {
					parent.window.location = "${ctx}";
				}
			});
		}
/* 
		$("#isConfigEnterpriseUser").click(function() {
			if (this.checked) {
				$('#employeeUserNameDiv').show();
				$('#employeeNameDiv').show();
				$('#employeeEmailDiv').show();
			} else {
				$('#employeeUserNameDiv').hide();
				$('#employeeNameDiv').hide();
				$('#employeeEmailDiv').hide();
			}
			var pageH = $("body").outerHeight();
			top.iframeAdaptHeight(pageH);
		}); */
	</script>
</body>
</html>
