<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<div id="menubar">
    <div class="cl">
        <span><a href="${ctx}/"><i id="logo"></i></a></span>
        <dl class="cl tab">
            <dt><a href="${ctx}/"  ${page==1?"class=\"active\"":""}>首页</a></dt>
            <dt><a href="${ctx}/price" ${page==2?"class=\"active\"":""}>价格</a></dt>
            <dt><a href="${ctx}/download" ${page==3?"class=\"active\"":""}>产品下载</a></dt>
            <!--dt><a href="/aboutus.html" class="active">联系我们</a></dt-->
        </dl>
        <ul class="cl buttongroup">
            <li><a href="${ctx}/register/wxwork" target="_blank">注册</a></li>
            <li><a href="${ctx}/login" id="login_button" target="_blank">登录</a></li>
        </ul>
    </div>
</div>