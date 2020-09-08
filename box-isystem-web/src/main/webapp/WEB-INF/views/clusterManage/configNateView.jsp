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
	<div class="pop-content sys-content-en">
		<div class="form-con">
			<form class="form-horizontal" id="setNateForm">
				<input type="hidden" id="status" name="status" value="true" />
				<div class="control-group">
					<label class="control-label" for=""><em>*</em>
					<spring:message code="clusterManage.uasNode.nat.config" />:</label>
					<div class="controls">
						<input type="text" id="natAddr" name="natAddr" class="span3" />
						<span class="validate-con bottom"><div></div></span>
					</div>
					<input type="hidden" id="managerIp" name="managerIp" value="${cse:htmlEscape(uasNode.managerIp)}">
					<input type="hidden" id="token" name="token" value="${cse:htmlEscape(token)}">
				
				</div>
			</form>
		</div>
	</div>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#setNateForm").validate({
				rules : {
					natAddr : {
						required : true,
						rangelength : [ 0, 128 ]
					}
				}
			});

			if (!placeholderSupport()) {
				placeholderCompatible();
			}
			;
		});
		function submitNate() {
			if (!$("#setNateForm").valid()) {
				return false;
			}
			$.ajax({
				type : "POST",
				url : "${ctx}/cluster/uasNode/update",
				data : $('#setNateForm').serialize(),
				error : function(request) {
					handlePrompt("error",
							'<spring:message code="common.saveFail"/>', null,
							10);
				},
				success : function() {
					top.ymPrompt.close();
					top.handlePrompt("success",
							'<spring:message code="common.saveSuccess"/>');
					top.document.getElementById("uasNodeMenuId").click();
				}
			});
		}
	</script>
</body>
</html>
