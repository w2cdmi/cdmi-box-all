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
				<div class="control-group">
					<label class="control-label" for=""><em>*</em>
					<spring:message code="clusterManage.uasNode.nat.config" />:</label>
					<div class="controls">
						<input type="text" id="natAddr" name="natAddr" class="span3" />
						<span class="validate-con bottom"><div></div></span>
					</div>
					<input type="hidden" id="name" name="name" value="${cse:htmlEscape(resourceGroupNode.name)}">
					<input type="hidden" id="resourceGroupID" name="resourceGroupID" value="${cse:htmlEscape(resourceGroupNode.resourceGroupID)}">
					<input type="hidden" id="dcId" name="dcId" value="${cse:htmlEscape(resourceGroupNode.resourceGroupID)}">
					<input type="hidden" id="managerIp" name="managerIp" value="${cse:htmlEscape(resourceGroupNode.managerIp)}">
				</div>
				<input type="hidden" id="token" name="token" value="${cse:htmlEscape(token)}"/>	
			</form>
		</div>
	</div>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#setNateForm").validate({
				rules : {
					natAddr : {
						required : true,
						rangelength : [ 1, 50 ]
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
				url : "${ctx}/cluster/dcdetailmanage/updateNate",
				data : $('#setNateForm').serialize(),
				error : function(request) {
					handlePrompt("error",
							'<spring:message code="common.saveFail"/>', null,
							10);
				},
				success : function() {
					unLayerLoading();
					top.ymPrompt.close();
					top.handlePrompt("success",
							'<spring:message code="common.saveSuccess"/>');
					top.window.frames[0].location = "${ctx}/cluster/dcdetailmanage/${cse:htmlEscape(resourceGroupNode.dcId)}";
				}
			});
		}
	</script>
</body>
</html>
