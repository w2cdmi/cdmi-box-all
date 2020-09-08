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
	<div class="sys-content sys-content-en">
		<div class="alert">
			<i class="icon-lightbulb icon-orange"></i>
			<spring:message code="sysconfig.linearChain.config" />
		</div>
		<div class="form-horizontal form-con clearfix">
			<form id="directForm" class="form-horizontal" method="post">
				<div class="alert alert-error input-medium controls" id="errorTip"
					style="display:none">
					<button class="close" data-dismiss="alert">×</button>
					<spring:message code="common.saveFail" />
				</div>
				<div class="control-group">
					<label class="control-label" for="input"><em>*</em>
					<spring:message code="sysconfig.linearChain.config.path" /></label>
					<div class="controls">
						<input class="span4" type="text" id="directPath" name="path"
							value="${cse:htmlEscape(directConfig.path)}" /> <span class="validate-con"><div></div></span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="input"><spring:message
							code="sysconfig.secmatrix.config" /></label>
					<div class="controls">
						<input type="checkbox" id="secmatrix" name="secmatrix" />
					</div>
				</div>
				<div class="control-group">
					<div class="controls">
						<button id="submit_btn" type="button"
							onClick="directChainSetting()" class="btn btn-primary">
							<spring:message code="common.save" />
						</button>
					</div>
				</div>
			</form>
		</div>
	</div>
	<script type="text/javascript">
		$(document).ready(function() {
			if ("true" == "${secmatrix.path}") {
				$("#secmatrix").attr("checked", "true");
			}
			var pageH = $("body").outerHeight();
			top.iframeAdaptHeight(pageH);

			$("#directForm").validate({
				rules : {
					path : {
						required : true,
						rangelength : [ 1, 200 ],
					}
				}
			});

		});
		function directChainSetting() {
			if (!$("#directForm").valid()) {
				return false;
			}
			var value = $("#directPath").val();
			var secmatrix = document.getElementById("secmatrix").checked ? "true"
					: "false";

			$.ajax({
				type : "POST",
				url : "${ctx}/sysconfig/direct/save",
				data : {
					path : value,
					secmatrix : secmatrix,
					token:'${cse:htmlEscape(token)}'
				},
				error : function(request) {
					if (request.responseText == "InParamterException") {
						top.handlePrompt("error",
								'<spring:message code="directForm.drect.null"/>');
					} else {
						top.handlePrompt("error",
								'<spring:message code="common.saveFail"/>');
					}
				},
				success : function() {
					top.handlePrompt("success",
							'<spring:message code="common.saveSuccess"/>');
				}
			});
		}
	</script>
</body>