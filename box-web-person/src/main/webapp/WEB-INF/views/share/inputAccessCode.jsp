<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
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
			<span class="logo-name">企业文件宝</span>
		</div>
		<div class="fillBackground"></div>
		<div class="sed-out-link">
			<div class="sed-out-link-details">提取码验证</div>
			<div class="sed-out-link-mailbox">
				<div class="sed-out-link-mailbox-middle">
					<input type="text" class="link-mailbox-input" placeholder="请输入提取码"
						id="acessCode" name="acessCode" />
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
			<p class="weui-footer__links">
				<a href="https://www.filepro.cn/wxwork" class="weui-footer__link">华一云网</a>
			</p>
			<p class="weui-footer__text">Copyright © 2017-2018 filepro.cn</p>
		</div>
	</div>


</body>

<script type="text/javascript">
	function doSubmit() {
		if($("#acessCode").val().trim()==""){
			$.toast("提取码不能为空","forbidden");
			return;
		}
		$("#acessCode").val($("#acessCode").val().trim());
		var parameter = {
			acessCode : $("#acessCode").val().replace("amp", ""),
			linkCode : $("#linkCode").val(),
			token : $("#token").val()
		}
		$.ajax({
			type : "POST",
			url : '${ctx}/share/inputAccessCode',
			data : parameter,
			error : function(request) {
				$("#acessCode").val("");
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
