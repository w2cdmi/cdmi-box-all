<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title><spring:message code='main.title'/></title>
    <meta name="viewport" content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no" />
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
    </style>
</head>

<body ontouchstart>
<div class="page-error all-page-error">
    <div class="error-top">
        <img src="${ctx}/static/skins/default/img/error_img.png"/>
        <p>服务器发生错误，请稍候再试</p>
    </div>
</div>
</body>
</html>