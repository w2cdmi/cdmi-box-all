<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="pw.cdmi.box.disk.utils.CSRFTokenManager" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%
    request.setAttribute("token", CSRFTokenManager.getTokenForSession(session));
%>
<!DOCTYPE html>
<html>
<meta name="viewport" content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no"/>

<link rel="stylesheet" href="https://cdn.bootcss.com/weui/1.1.2/style/weui.min.css">
<link rel="stylesheet" href="https://cdn.bootcss.com/jquery-weui/1.2.0/css/jquery-weui.min.css">
<link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/header.css"/>
<link rel="stylesheet" href="${ctx}/static/skins/default/css/main.css">
<script src="${ctx}/static/jquery-weui/lib/jquery-2.1.4.js"></script>
<script src="https://cdn.bootcss.com/jquery-weui/1.2.0/js/jquery-weui.min.js"></script>

<%--<script src="${ctx}/static/js/common/common.js"></script>--%>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/share/inputMailAccessCode.css"/>
    <title>邮箱验证码</title>
</head>
<body>
<div class="box">
    <div class="link-header">
        <div class="logo logo-layout"></div>
    </div>
    <div class="fillBackground"></div>
    <div class="sed-out-link">
        <div class="sed-out-link-details">请登录邮箱获取验证码</div>
        <input type="hidden" id="linkCode" name="linkCode" value="${linkCode}"/>
        <div class="sed-out-link-mailbox" style="margin-top:1.5rem;">
            <div class="sed-out-link-mailbox-middle">
                <input type="text" class="link-mailbox-input" placeholder="请输入邮箱" id="mail" name="mail"/>
                <div class="link-mailbox-icon"></div>
            </div>
        </div>
        <div class="sed-out-link-mailbox">
            <div class="sed-out-link-mailbox-middle">
                <input type="text" class="link-mailbox-input" placeholder="请输入动态码" id="accessCode" name="accessCode"/>
                <div class="link-dynamic-code-icon"></div>
                <div class="link-dynamic-code-obtain" id="sendButton" onclick="sendAccessCode()">获取动态码</div>
            </div>
        </div>
        <div class="sed-out-link-tail">
            <div class="determine-sign-in" onclick="doSubmit()">确定</div>
        </div>
    </div>
    <div class="weui-footer footer-layout">
        <p class="weui-footer__text"><spring:message code='corpright'/></p>
    </div>
</div>

</body>
<script type="text/javascript">
    var timer;
    var i = 0;

    function doSubmit() {
        var accessCode = $("#accessCode").val().trim();

        if (accessCode === "") {
            $.toast("请输入动态码")
        } else {
            var parameter = {
                "accessCode": accessCode,
                "linkCode": $("#linkCode").val(),
                "token": "${token}",
                "captcha": ""
            };
            $.ajax({
                type: "POST",
                url: '/p/inputAccessCode',
                data: parameter,
                error: function (request) {
                    $.toast("动态码无效,请重新输入.", 3000);
                },
                success: function (data) {
                    top.location.reload();
                }
            });
        }

        return false;
    }

    function countDown(time) {
        var $button = $('#sendButton');
        if (time < 0) {
            $button.html('获取动态码');
            $button.css('background', '#4F77AA');
            clearInterval(timer);
        } else {
            $button.html(time + "秒后重新获取");
        }
    }

    function sendAccessCode() {
        var $button = $('#sendButton');
        if ($button.html() === "获取动态码") {
            var emailReg = /^([a-z0-9_\.-]+)@([\da-z\.-]+)\.([a-z\.]{2,6})$/;
            var emailOther = /^[a-z\d]+(\.[a-z\d]+)*@([\da-z](-[\da-z])?)+(\.{1,2}[a-z]+)+$/;

            var mail = $("#mail").val().trim();
            if (mail === "") {
                $.toast('请输入邮件箱地址, 然后点击"获取动态码"', 3000);
                return;
            }

            if (!emailReg.test(mail) || !emailOther.test(mail)) {
                $.toast("邮箱地址不合法", 3000);
                return;
            }

            $button.attr('disabled', 'disabled');
            $button.css('background', '#e4e1e1');
            $("#accessCode").focus();
            var parameter = {
                'mail': mail,
                "linkCode": $("#linkCode").val(),
                "token": "${token}"
            };
            $.ajax({
                type: "POST",
                url: '/p/sendAccessCode', //此处使用/p绝对路径，不使用ctx路径，相关的路径转换由nginx负责完成
                data: parameter,
                error: function (request) {
                    $.toast("发送失败");
                    $button.css('background', '#4F77AA');
                },
                success: function (data) {
                    clearInterval(timer);
                    $.toast("动态码已经发送到邮箱,请查收", 3000);
                    $button.disabled = true;
                    i = 60;
                    timer = setInterval(function () {
                        countDown(i);
                        i--
                    }, 1000)
                }
            });
        }
    }
</script>
</html>
