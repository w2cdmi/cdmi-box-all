<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>Login</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no"/>
    <link rel="stylesheet" href="${ctx}/static/jquery-weui/lib/weui.min.css">
    <link rel="stylesheet" href="${ctx}/static/jquery-weui/css/jquery-weui.css">
    <link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/header.css"/>
    <link rel="stylesheet" href="${ctx}/static/skins/default/css/main.css">
    <script src="${ctx}/static/jquery-weui/lib/jquery-2.1.4.js"></script>
    <script src="${ctx}/static/jquery-weui/js/jquery-weui.js"></script>
    <script src="${ctx}/static/jquery/validate/jquery.validate.min.js"></script>
    <script src="${ctx}/static/jquery/validate/messages_bs_zh.js"></script>
    <script src="${ctx}/static/jquery/iscroll-probe.js"></script>
    <script src="${ctx}/static/js/common/common.js"></script>
    <%
        request.getSession().setAttribute("host", "http://pan.storbox.cn");
    %>
</head>

<body>

<header class='demos-header'>
    <h1 class="demos-title">企业网盘</h1>
    <p class='demos-sub-title'>华一云网</p>
</header>

<div class="weui-cells weui-cells_form">
    <form action="${ctx}/login" method="post" id="loginForm">
        <div class="weui-cell">
            <div class="weui-cell__hd"><label class="weui-label">企业</label></div>
            <div class="weui-cell__bd">
                <input class="weui-input" type="text" name="enterpriseName" id="enterpriseName" placeholder="请输入企业名称" value="聚数科技">
            </div>
        </div>
        <div class="weui-cell">
            <div class="weui-cell__hd"><label class="weui-label">用户名</label></div>
            <div class="weui-cell__bd">
                <input class="weui-input" type="text" name="username" id="username" placeholder="请输入用户名" value="JiangBo">
            </div>
        </div>
        <div class="weui-cell">
            <div class="weui-cell__hd"><label class="weui-label">密码</label></div>
            <div class="weui-cell__bd">
                <input class="weui-input" type="password" id="password" name="password" placeholder="请输入密码" value="pas@123a">
            </div>
        </div>
        <div class='demos-content-padded'>
            <a href="javascript:;" class="weui-btn weui-btn_primary" onclick="submitForm()">登录</a>
        </div>
    </form>
</div>

<div class="weui-footer">
    <p class="weui-footer__links">
        <a href="https://www.filepro.cn/wxwork" class="weui-footer__link">华一云网</a>
    </p>
    <p class="weui-footer__text">Copyright © 2017-2018 filepro.cn</p>
</div>

<style>
    .weui-footer {
        margin: 25px 0 10px 0;
    }
</style>

<script>
    function submitForm() {
        $("#loginForm").submit();
    }
</script>
</body>
</html>