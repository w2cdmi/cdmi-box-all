<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="pw.cdmi.box.disk.utils.*"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<%
	request.setAttribute("token",
			CSRFTokenManager.getTokenForSession(session));
%>
<!DOCTYPE html>
<html>
<meta name="viewport"
	content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no" />
<link rel="stylesheet" href="${ctx}/static/jquery-weui/lib/weui.min.css">
<link rel="stylesheet"
	href="${ctx}/static/jquery-weui/css/jquery-weui.css">
<link rel="stylesheet" type="text/css"
	href="${ctx}/static/skins/default/css/header.css" />
<link rel="stylesheet" href="${ctx}/static/skins/default/css/main.css">
<script src="${ctx}/static/jquery-weui/lib/jquery-2.1.4.js"></script>
<script src="${ctx}/static/jquery-weui/js/jquery-weui.js"></script>
<script src="${ctx}/static/js/common/common.js"></script>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" type="text/css"
	href="${ctx}/static/skins/default/css/share/inputMailAccessCode.css" />
<title>邮箱验证码</title>
</head>
<body>
	<div class="box">
		<div class="link-header">
			<div class="logo logo-layout"></div>
			<span class="logo-name">企业文件宝</span>
		</div>
		<div class="fillBackground"></div>
		<div class="sed-out-link">
			<div class="sed-out-link-details">请登录邮箱获取验证码</div>
			<input type="hidden" id="linkCode" name="linkCode"
				value="<c:out value='${linkCode}'/>" />
			<div class="sed-out-link-mailbox" style="margin-top:1.5rem;">
				<div class="sed-out-link-mailbox-middle">
					<input type="text" class="link-mailbox-input" placeholder="请输入邮箱"
						id="mail" name="mail" />
					<div class="link-mailbox-icon"></div>
				</div>
			</div>
			<div class="sed-out-link-mailbox">
				<div class="sed-out-link-mailbox-middle">
					<input type="text" class="link-mailbox-input" placeholder="请输入动态码"
						id="acessCode" name="acessCode" />
					<div class="link-dynamic-code-icon"></div>
					<div class="link-dynamic-code-obtain" id="sendButton" onclick="sendAccessCode()">获取动态码</div>
				</div>
			</div>
			<div class="sed-out-link-tail">
				<div class="determine-sign-in" onclick="doSubmit()">确定</div>
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
	var timer;
	var i = 0;
	function doSubmit() {
		var prameter = {
			"acessCode" : $("#acessCode").val(),
			"linkCode" : $("#linkCode").val(),
			"token" : "<c:out value='${token}'/>",
			"captcha" : ""
		}
		$.ajax({
			type : "POST",
			url : '${ctx}/share/inputAccessCode',
			data : prameter,
			error : function(request) {
                $.toast("动态码无效,请重新输入.",3000);
			},
			success : function(data) {
				top.location.reload();
			}
		});
		return false;
	}
	
	function countDown(time){
		if(time<0){
			$('#sendButton').html('获取动态码');
			$('#sendButton').css('background','#4F77AA');
			clearInterval(timer)
		}else{
			$('#sendButton').html(time+"秒后重新获取");
//			$('#sendButton').css('background','#e4e1e1');
		}
	}
	function sendAccessCode() {
			$('#sendButton').attr('disabled','disabled');
			$('#sendButton').css('background','#e4e1e1');
			if($('#sendButton').html()=="获取动态码"){
				var mail = $("#mail").val().trim();
				if(mail == ""){
					$.toast("请先输入邮箱",1000);
					return;
				}
				var prameter = {
					'mail' : mail,
					'linkCode' : "<c:out value='${linkCode}'/>",
					'token' : "<c:out value='${token}'/>",
				}
				$.ajax({
					type : "POST",
					url : '${ctx}/share/sendAccessCode',
					data : prameter,
					error : function(request) {
						$.toast("发送失败");
						$('#sendButton').css('background','#4F77AA');
					},
					success : function(data) {
						clearInterval(timer)
						$.toast("动态码已经发送到邮箱,请查收",3000);
						$('#sendButton').disabled = true;
						i = 60;
						timer = setInterval(function(){
							countDown(i)
							i--
						},1000)
					}
				});
			}else{
				
			}
	}
</script>
</html>
