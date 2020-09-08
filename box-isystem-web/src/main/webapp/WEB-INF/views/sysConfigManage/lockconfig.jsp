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
			<spring:message code="sysconfig.modify.lock.config" />
		</div>
		<div class="form-horizontal form-con clearfix">
			<form id="lockform" class="form-horizontal" method="post">
				<div class="alert alert-error input-medium controls" id="errorTip"
					style="display:none">
					<button class="close" data-dismiss="alert">×</button>
					<spring:message code="common.saveFail" />
				</div>
				<div class="control-group">
					<label class="control-label" for="input"><em>*</em>
					<spring:message code="sysconfig.lock.config.count" /></label>
					<div class="controls">
						<input class="span4" type="text" id="count" name="count"
							value="${cse:htmlEscape(count)}" /> <span class="validate-con"><div></div></span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="input"><em>*</em>
					<spring:message code="sysconfig.lock.config.time" /></label>
					<div class="controls">
						<input class="span4" type="text" id="time" name="time"
							value="${cse:htmlEscape(time)}" /> <span class="validate-con"><div></div></span>
					</div>
				</div>
				<div class="control-group">
					<div class="controls">
						<button id="submit_btn" type="button"
							onClick="savelockconfig()" class="btn btn-primary">
							<spring:message code="common.save" />
						</button>
					</div>
				</div>
			</form>
		</div>
	</div>
	<script type="text/javascript">
	$.validator.addMethod(
		   "isvalidnum", 
		   function(value, element) {   
	           var validName = /^[1-9]{1}[0-9]*$/;   
	           return validName.test(value);   
	       }, 
	       $.validator.format('<spring:message code="sysconfg.lock.config.check"/>')
); 
		$(document).ready(function() {
			var pageH = $("body").outerHeight();
			top.iframeAdaptHeight(pageH);

			$("#lockform").validate({
				rules : {
					count : {
						required : true,
						isvalidnum : true,
						range : [3,30]
					},
					time : {
						required : true,
						isvalidnum : true,
						range : [1,24 * 60 * 60]
					}
				}
			});

		});
		function savelockconfig() {
			if (!$("#lockform").valid()) {
				return false;
			}
			var lockcount = $("#count").val();
			var locktime = $("#time").val();

			$.ajax({
				type : "POST",
				url : "${ctx}/authorize/lockConfig/save",
				data : {
					count : lockcount,
					time : locktime,
					token:'${cse:htmlEscape(token)}'
				},
				error : function(request) {
					if (request.responseText == "InParamterException") {
						top.handlePrompt("error",
								'<spring:message code="lockform.para.error"/>');
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