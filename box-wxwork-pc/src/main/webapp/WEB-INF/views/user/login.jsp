<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <%@ include file="../common/include.jsp" %>
    <title><spring:message code='main.title'/></title>
    <style>
        .all-page-error{
            width:100%;
            margin:auto;
            position:fixed;
            left:0;
            right:0;
            top:0;
            bottom:0;
        }
        .error-top{
            text-align:center;
        }
        .error-top img{
            width:5rem;
            margin-top:7.5rem;
        }
        .error-top p:first-of-type{
            font-size:1rem;
            color:rgba(51,51,51,1);
            margin-top:1.15rem;
            margin-bottom:0.8rem;
        }
        .error-top p:last-of-type{
            color:rgba(153,153,153,1);
            font-size:1rem;
            margin-bottom:9rem;
        }
        .error-reload{
            text-align:center;
        }
        .error-reload a{
            display: inline-block;
            color:#666;
            font-size:0.65rem;
            padding: 0.4rem 1.05rem;
            border: 1px solid #999;
            border-radius: 0.1rem;
        }
        .goto-register{
            margin-top: 18px
        }
        .goto-register p{
            font-size: 16px;
        }
        .goto-register a{
            display: inline-block;
            line-height: 28px;
            padding: 0 20px;
            background: #ea5036;
            text-decoration: none;
            color: #fff;
            font-size: 14px;
            border-radius: 4px
        }
        .error-info p{
            font-size: 16px;margin-top: 70px
        }
    </style>
</head>

<body ontouchstart>
<div id="body" class="page-error all-page-error" style="display: none">
    <div class="error-top">
        <img src="${ctx}/static/skins/default/img/error_img.png"/>
        <p>网络链接失败，请稍候再试</p>
    </div>
    <div class="error-reload">
        <a href="${ctx}/?weixinCorpId=${corpId}">重新加载</a>
    </div>
</div>
<div id="wxLoginBody" style="display: none;text-align: center">
    <div class="error-info">
        <c:if test="${empty errorMessage}">
            <p>请扫码登录</p>
        </c:if>
        <c:if test="${!empty errorMessage}">
            <p style="">该微信号没有绑定企业版微信账号</p>
        </c:if>
    </div>
    <div id="qrcode">

    </div>
    <div class="goto-register">
        <p>点击下方链接使用企业微信登录</p>
        <a href="https://open.work.weixin.qq.com/wwopen/sso/3rd_qrConnect?appid=${wwAppId}&redirect_uri=${wwRedirectUrl}&state=0&usertype=member">企业微信登录</a>
    </div>
</div>
<script src="https://res.wx.qq.com/connect/zh_CN/htmledition/js/wxLogin.js"></script>
<script type="text/javascript">
    $(function(){
        //如果不是在企业微信PC端中，直接跳转到企业微信扫码登录界面
        if(isInPcWxWork()) {
            $("#body").show();
        } else {
            $("#wxLoginBody").show()
            wxLogin()
            <%--window.location = "https://open.work.weixin.qq.com/wwopen/sso/3rd_qrConnect?appid=${wwAppId}&redirect_uri=${wwRedirectUrl}&state=0&usertype=member";--%>
        }
    });

    /*判断当前所在的浏览器是否是企业微信PC端浏览器*/
    function isInPcWxWork() {
        var ua = window.navigator.userAgent;
        return ua.indexOf("wxwork") !== -1 && (ua.indexOf("WindowsWechat") !== -1 || ua.indexOf("Macintosh") !== -1);
    }

    function wxLogin(){
        //显示微信登录二维码
        var obj = new WxLogin({
            id:"qrcode",
            appid: "${wxAppId}",
            scope: "snsapi_login",
            redirect_uri: "${wxRedirectUrl}",
            state: "0",
            style: "black",
            href:"https://www.filepro.cn${ctx}/static/skins/default/css/wxlogin.css"
        });
    }
</script>
</body>
</html>