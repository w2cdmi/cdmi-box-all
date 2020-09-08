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
			<span class="logo-name">文件宝</span>
		</div>
		<div class="fillBackground"></div>
		<div class="sed-out-link" style="width: 500px;height: 400px;margin: auto; background: white;display: inline-block; position: fixed;left: 0;right: 0; top: 0;bottom: 0">
			<div class="sed-out-link-details">提取码验证</div>
			<div class="sed-out-link-mailbox">
				<div class="sed-out-link-mailbox-middle">
					<input type="text" class="link-mailbox-input" placeholder="请输入提取码" id="accessCode" name="accessCode" />
					<div class="link-dynamic-code-icon"></div>
				</div>
			</div>
			<div class="sed-out-link-tail">
				<div class="determine-sign-in" onclick="doSubmit()">确定</div>
				<input type="hidden" id="linkCode" value="${linkCode}" name="linkCode">
                <input type="hidden" id="token" value="${token}" name="token">
			</div>
		</div>
		<div class="weui-footer footer-layout">
			<p class="weui-footer__links">
				<a href="https://www.filepro.cn" class="weui-footer__link">华一云网</a>
			</p>
			<p class="weui-footer__text">Copyright © 2017-2018 filepro.cn</p>
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
			accessCode : $("#accessCode").val().replace("amp", ""),
			linkCode : $("#linkCode").val(),
			token : $("#token").val(),
			captcha: '',
			mail: ''
		}
		$.ajax({
			type : "POST",
			url : '/p/inputAccessCode',
			data :  parameter, 
			beforeSend: function(){},
			error : function(request) {
				$("#accessCode").val("");
				$.Tost("提取码错误","forbidden");
			},
			success : function(data) {
				window.location.reload();
			}
		});
		return false;
	}
</script>
</html>
