<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="cse" uri="http://cse.huawei.com/custom-function-taglib" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html>
<head>
    <%@ include file="../common/common.jsp" %>
    <style type="text/css">
        body {
            padding: 50px 50px;
            background: #f6f6f6;
        }

        h2 {
            font-weight: normal;
            color: #444;
        }

        p {
            color: #666;
            margin: 20px 0 10px;
        }

        ul li {
            color: #666;
            text-indent: 2em;
        }
    </style>
</head>

<body>
<h2><spring:message code="browser.versiontips.title"/></h2>
<p><spring:message code="browser.versiontips.tips"/></p>
<ul>
    <li><spring:message code="browser.versiontips.change"/></li>
    <li><spring:message code="browser.versiontips.upgrade"/></li>
    <li></li>

</ul>
</body>
</html>
