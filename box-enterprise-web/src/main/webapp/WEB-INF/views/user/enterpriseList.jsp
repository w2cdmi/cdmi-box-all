<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html>
<head>
    <META HTTP-EQUIV="Expires" CONTENT="0">
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <META HTTP-EQUIV="Cache-control" CONTENT="no-cache, no-store, must-revalidate">
    <META HTTP-EQUIV="Cache" CONTENT="no-cache">
    <style type="text/css">
        * {
            margin: 0;
            padding: 0;
        }

        body {
            font-family: "arial, helvetica, sans-serif";
        }

        .buss-choose {
            position: absolute;
            width: 528px;
            height: 492px;
            top: 0;
            right: 0;
            bottom: 0;
            left: 0;
            margin: auto;
            border: 1px solid #d8d8d8;
            border-radius: 2px;
        }

        .cho-title {
            width: 406px;
            margin: 60px auto 0;
            overflow: hidden;
        }

        .cho-title span {
            width: 118px;
            float: left;
            display: block;
            border: 1px solid #FF842C;
            margin-top: 9px;
        }

        .cho-title p {
            float: left;
            font-size: 16px;
            line-height: 18px;
            margin: 0 10px;
        }

        .all-business {
            margin-top: 90px;
            list-style: none;
        }

        .all-business li {
            line-height: 42px;
        }

        .all-business li label {
            color: #333;
            font-size: 18px;
            cursor: pointer;
            margin-left: 137px;
        }

        .buss-login {
            display: block;
            width: 352px;
            margin: auto;
            background: #FF842C;
            border: none;
            line-height: 46px;
            font-size: 18px;
            color: #fff;
            border-radius: 4px;
            cursor: pointer;
            margin-top: 68px;
        }
    </style>
    <script src="${ctx}/static/js/public/jquery-1.10.2.min.js" type="text/javascript"></script>
</head>
<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", -1);
%>
<body>
<div class="buss-choose" id="loading">
	<div class="cho-title">
		<span></span>
		<p>请选择要登录的企业</p>
		<span></span>
	</div>
    <form action="${ctx}/login/chooseEnterprise?qr=wx" method="POST">
        <input type="hidden" name="wxCode" value="${code}">
        <ul class="all-business">
        	<c:forEach items="${enterpriseList}" var="enterprise" varStatus="status">
            <li>
            	<label><input type="radio" name="enterpriseId" value="${enterprise.id}" <c:if test="${status.first}">checked="checked"</c:if>>&nbsp;&nbsp;&nbsp;${enterprise.name}</label>
            </li>
        </c:forEach>
        </ul>
        <input class="buss-login" type="submit" value="登录">
    </form>

</div>
</body>
</html>