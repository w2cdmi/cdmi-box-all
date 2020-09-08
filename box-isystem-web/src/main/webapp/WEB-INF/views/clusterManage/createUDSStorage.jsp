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
			<form class="form-horizontal" id="creatStorageForm">
				<div class="control-group">
					<label class="control-label" for=""><em>*</em>
					<spring:message code="storage.class.provider" />:</label>
					<div class="controls">
						<select id="provider" class="span4" name="provider">
							<option value="QYOS">青云对象存储</option>
							<option value="HWOBS">华为OBS存储</option>
							<option value="ALIOSS">阿里标准云存储</option>
							<option value="ALIAI">阿里AI云存储</option>
							<option value="TENTOBS">腾讯云存储</option>
						</select> <span class="validate-con bottom"><div></div></span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for=""><em>*</em>
					<spring:message code="clusterManage.domainName" />:</label>
					<div class="controls">
						<input type="text" id="domain" class="span4" name="domain"
							placeholder='<spring:message code="cluster.uds.domain"/>' /> <span
							class="validate-con bottom"><div></div></span> <span
							class="help-block"><spring:message
								code="cluster.user.dns.assa.uds" /></span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for=""><em>*</em>
					<spring:message code="clusterManage.httpPort" />:</label>
					<div class="controls">
						<input type="text" id="port" class="span4" name="port" value="80" />
						<span class="validate-con bottom"><div></div></span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for=""><em>*</em>
					<spring:message code="clusterManage.httpsPort" />:</label>
					<div class="controls">
						<input type="text" id="httpsport" class="span4" name="httpsport"
							value="443" /> <span class="validate-con bottom"><div></div></span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for=""><em>*</em>
					<spring:message code="clusterManage.accessKey" />:</label>
					<div class="controls">
						<input type="text" id="accessKey" class="span4" name="accessKey" />
						<span class="validate-con bottom"><div></div></span> <span
							class="help-block"><spring:message
								code="cluster.user.dns.assa.uds.aksk" /></span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for=""><em>*</em>
					<spring:message code="clusterManage.secretKey" />:</label>
					<div class="controls">
						<input type="password" id="secretKey" class="span4"
							name="secretKey" autocomplete="off" /> <span
							class="validate-con bottom"><div></div></span>
					</div>
				</div>
				<input type="hidden" id="dcId" name="dcId"
					value="${cse:htmlEscape(dcId)}" /> <input type="hidden" id="token"
					name="token" value="${cse:htmlEscape(token)}" />
			</form>
		</div>
	</div>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#creatStorageForm").validate({
				rules : {
					provider : {
						required : true
					},
					domain : {
						required : true,
						maxlength : [ 255 ]
					},
					port : {
						required : false,
						digits : true,
						min : 1,
						max : 65535
					},
					httpsport : {
						required : false,
						digits : true,
						min : 1,
						max : 65535
					},
					accessKey : {
						required : true,
						maxlength : [ 64 ]
					},
					secretKey : {
						required : true,
						maxlength : [ 64 ]
					}
				}
			});

			if (!placeholderSupport()) {
				placeholderCompatible();
			}
			;
		});
		function submitStorage() {
			if (!$("#creatStorageForm").valid()) {
				return false;
			}
			top.ymPrompt_disableModalbtn("#btn-focus");
			inLayerLoading(
					'<spring:message code="cluster.createStorage.loading"/>',
					"loading-bar");
			$
					.ajax({
						type : "POST",
						url : "${ctx}/cluster/dcdetailmanage/createUDSStorage",
						data : $('#creatStorageForm').serialize(),
						error : function(request) {
							top.ymPrompt_enableModalbtn("#btn-focus");
							unLayerLoading();
							handlePrompt("error",
									'<spring:message code="common.createFail"/>');
						},
						success : function() {
							unLayerLoading();
							top.ymPrompt.close();
							top
									.handlePrompt("success",
											'<spring:message code="common.createSuccess"/>');
							top.window.frames[0].location = "${ctx}/cluster/dcdetailmanage/${cse:htmlEscape(dcId)}";
						}
					});
		}
	</script>
</body>
</html>
