<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="../common/common.jsp" %>
    <%@ include file="../common/messages.jsp" %>
    <style>
        * {
            padding: 0;
            margin: 0;
        }

        .header {
            height: 50px;
            background: #2D90E4;
        }

        a {
            text-decoration: none;
        }

        .container {
            margin: 0 auto;
            width: 800px;
            margin-top: 60px;
            background: #ffffff;
            position: relative;
            padding-top: 30px;
            padding-bottom: 70px;
        }

        .bg {
            background: no-repeat center;
            background-size: 540px 370px;
            width: 540px;
            height: 370px;
            display: block;
            margin: 0 auto;
        }

        .text {
            font-size: 20px;
            text-align: center;
            padding-top: 40px;
            position: absolute;
            bottom: 0;
            left: 0;
            right: 0;
        }

        .btn-box {
            padding-top: 30px;
        }

        .btn {
            width: 242px;
            height: 46px;
            line-height: 46px;
            border-radius: 3px;
            background-color: #2D90E5;
            color: #ffffff;
            font-size: 20px;
            display: block;
            margin: 0 auto;
            text-align: center;
        }
    </style>
</head>
<body style="background-color: #edfbfb">
<div class="header" style="position: relative">
    <div class="header-con">
        <div class="logo" id="logoBlock">
            <img src="${ctx}/static/skins/default/img/logo.png"/>
        </div>
    </div>
</div>
<div class="container">
    <img class="bg" src="${ctx}/static/skins/default/img/link_lose.png"/>
    <div class="text"><spring:message code="link.invalid"/></div>
</div>
<div class="btn-box">
    <a class="btn btn-primary" href="${ctx}"><spring:message code="cloudapp.forget.password.return"/></a>
</div>
</body>
</html>
