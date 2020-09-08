<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<%@ include file="../common/include.jsp"%>
<link rel="stylesheet" type="text/css"
	href="${ctx}/static/skins/default/css/share/inputMailAccessCode.css" />
</head>

<body>
	<div class="box">
		<div class="link-header">
			<div class="logo logo-layout"></div>
		</div>
		<div class="fillBackground"></div>
		<div class="sed-out-link">
			<div class="sed-out-link-details">提取码验证</div>
			<div class="sed-out-link-mailbox">
				<div class="sed-out-link-mailbox-middle">
					<input type="text" class="link-mailbox-input" placeholder="请输入提取码"
						id="accessCode" name="accessCode" />
					<div class="link-dynamic-code-icon"></div>
				</div>
			</div>
			<div class="sed-out-link-tail">
				<div class="determine-sign-in" onclick="doSubmit()">确定</div>
				<input type="hidden" id="linkCode" value="${linkCode}"
					name="linkCode"> <input type="hidden" id="token"
					value="${token}" name="token">
			</div>
		</div>
		<div class="weui-footer footer-layout">
			<p class="weui-footer__text"><spring:message code='corpright'/></p>
		</div>
	</div>


</body>

<script type="text/javascript">
	function doSubmit() {
		if($("#accessCode").val().trim()==""){
			$.toast("提取码不能为空","forbidden");
			return;
		}
		$("#accessCode").val($("#accessCode").val().trim());
		var parameter = {
			accessCode : $("#accessCode").val(),
			linkCode : $("#linkCode").val(),
			token : $("#token").val(),
			mail:"",
            captcha:""
		}
		$.ajax({
			type : "POST",
			url : '/p/inputAccessCode',//此处使用/p绝对路径，不使用ctx路径，相关的路径转换由nginx负责完成
			beforeSend: function(xhr) {

            },
			data : parameter,
			error : function(request) {
				$("#accessCode").val("");
				$.toast("提取码错误","forbidden");
			},
			success : function(data) {
				window.location.reload();
			}
		});
		return false;
	}
</script>
</html>
