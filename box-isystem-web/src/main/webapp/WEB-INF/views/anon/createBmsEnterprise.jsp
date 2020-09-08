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
	<div class="sys-content">
		<div class="form-horizontal form-con clearfix">
			<form class="form-horizontal" id="creatEnterpriseForm"
				name="creatEnterpriseForm">
				<input type="hidden" id="id" name="id" class="span4"
							value="${enterprise.id}" />
				<div class="control-group">
					<label class="control-label" for=""><em>*</em> <spring:message
							code="enterpriseList.name" />:</label>
					<div class="controls">
						<input type="text" id="name" name="name" class="span4"
							value="${enterprise.name}" /> <span class="validate-con bottom"></span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for=""><em>*</em> <spring:message
							code="enterpriseList.domainName" />:</label>
					<div class="controls">
						<input type="text" id="domainName" name="domainName" class="span4"
							value="${enterprise.domainName}" /> <span
							class="validate-con bottom"></span>
						<%-- <span class="help-block" ><spring:message code="enterprise.create.title.domain"/></span> --%>
					</div>
				</div>
				<%-- <div class="control-group">
					<label class="control-label" for=""><em>*</em> <spring:message
							code="enterpriseList.contactEmail" />:</label>
					<div class="controls">
						<input type="text" id="contactEmail" name="contactEmail"
							class="span4" value="${enterprise.contactEmail}" /> <span
							class="validate-con bottom"></span>
						<span class="help-block" ><spring:message code="enterprise.create.title.email"/></span>
					</div>
				</div> --%>

				<div class="control-group">
					<div class="controls">
						<button id="submit_btn" type="button"
							onclick="submitCreateEnterprise()" class="btn btn-primary">
							<spring:message code="common.save" />
						</button>
					</div>
				</div>
				<%-- <div class="control-group">
					<div class="controls">
						<button id="next_btn" type="button"
							onclick="openInframe(this, '${ctx}/systeminit/license/config','systemFrame')"
							class="btn btn-primary">
							<spring:message code="isystem.init.config.next" />
						</button>
					</div>
				</div> --%>
			</form>
		</div>
	</div>
	<script type="text/javascript">
		$(document)
				.ready(
						function() {
							$("#creatEnterpriseForm")
									.validate(
											{
												rules : {
													name : {
														required : true,
														maxlength : [ 255 ]
													},
													domainName : {
														required : true,
														domainNameCheck : true,
														maxlength : [ 64 ]
													},
													contactEmail : {
														required : true,
														isValidEmail : true,
														maxlength : [ 64 ]
													},
													contactPerson : {
														maxlength : [ 255 ]
													},
													contactPhone : {
														contactPhoneCheck : true,
														maxlength : [ 255 ]
													}
												},
												messages : {
													contactPhone : {
														contactPhoneCheck : '<spring:message  code="enterpriseList.contactPhone.rule"/>'
													}
												},
											});
							$.validator.addMethod("contactPhoneCheck",
									function(value, element) {
										var pattern = /^[0-9 +-]*$/;
										if (!pattern.test(value)) {
											return false;
										}
										return true;
									});

							$.validator
									.addMethod(
											"domainNameCheck",
											function(value, element) {
												var pattern1 = /^[a-zA-Z0-9-_]*$/;
												if (!pattern1.test(value)) {
													return false;
												}
												return true;
											},
											$.validator
													.format('<spring:message code="domainname.key.rule"/>'));
						});

		function submitCreateEnterprise() {
			if (!$("#creatEnterpriseForm").valid()) {
				return false;
			}
			$.ajax({
				type : "POST",
				url : "${ctx}/systeminit/enterprise/save",
				data : $('#creatEnterpriseForm').serialize(),
				error : function(request) {
					errorPrompt(request);
				},
				success : function(data) {
					top.handlePrompt("success",
							'<spring:message code="common.createSuccess"/>');
				}
			});

		}

		function errorPrompt(request) {
			handlePrompt("error", '<spring:message code="common.createFail"/>');
		}
	</script>
</body>
</html>
