<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <%@ include file="./WEB-INF/views/common/include.jsp" %>
    <%
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        String saveRequestStr = "";
        if (null != request) {
            String savedRequestStrObject = request.getSession().getAttribute("savedRequestStr") == null ? "" : request.getSession().getAttribute("savedRequestStr").toString();
            if (StringUtils.isNotBlank(savedRequestStrObject)) {
                saveRequestStr = org.springframework.web.util.HtmlUtils.htmlEscape(savedRequestStrObject);
            }
        }
    %>
    <title>Login</title>
</head>

<body ontouchstart>

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
                <input class="weui-input" type="text" name="username" id="username" placeholder="请输入用户名" value="DuPan">
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
        <input type="hidden" id="savedShrioStr" name="savedShrioStr" value="<%=saveRequestStr%>"/>
    </form>
</div>

<div class="weui-footer">
    <p class="weui-footer__links">
        <a href="https://www.filepro.cn" class="weui-footer__link">华一云网</a>
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