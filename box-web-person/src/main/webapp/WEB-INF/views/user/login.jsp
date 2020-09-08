<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <%@ include file="../common/include.jsp" %>
    <script src="https://res.wx.qq.com/connect/zh_CN/htmledition/js/wxLogin.js"></script>
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
            font-size:16px;
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
            text-align: center;
            margin-top: 18px
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
    </style>
</head>

<body ontouchstart>
<div id="body" class="page-error all-page-error">
    <div class="error-top">
        <c:if test="${empty errorMessage}">
            <p>请扫码登录</p>
        </c:if>
        <c:if test="${!empty errorMessage}">
            <p>${errorMessage}</p>
        </c:if>
    </div>
    <div id="qrcode" class="error-reload">

    </div>
    <c:if test="${!empty errorMessage}">
        <div class="goto-register">
            <a href="${ctx}/user/wxRegister">请前往注册</a>
        </div>
    </c:if>
</div>
<script type="text/javascript">
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
    $(function(){
            wxLogin()
        //个人版页面，直接跳转微信登录
            <%--window.location = "https://open.weixin.qq.com/connect/qrconnect?appid=${wxAppId}&redirect_uri=${wxRedirectUrl}&response_type=code&scope=snsapi_login&state=9527#wechat_redirect";--%>

    });
</script>
</body>
</html>