<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html>
<head>
    <META HTTP-EQUIV="Expires" CONTENT="0">
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <META HTTP-EQUIV="Cache-control"
          CONTENT="no-cache, no-store, must-revalidate">
    <META HTTP-EQUIV="Cache" CONTENT="no-cache">
    <style type="text/css">
        html, body {
            margin: 0;
            padding: 0;
        }

        #loading {
            position: absolute;
            top: 0;
            left: 0;
            bottom: 0;
            right: 0;
            background: #f3f3f3;
        }
    </style>
    <script src="${ctx}/static/js/public/jquery-1.10.2.min.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/spinLoading/spin.min.js" type="text/javascript"></script>
    <script type="text/javascript">
        $(document).ready(function () {
            var optsBigSpinner = {
                lines: 15,
                length: 8,
                width: 4,
                radius: 12,
                color: '#333',
                speed: 1.6,
                trail: 50
            }
            spinner = new Spinner(optsBigSpinner).spin($("#loading").get(0));
            location.href = "${ctx}/login/authforword";
        })
    </script>
</head>
<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);
%>
<body>
<div id="loading"></div>
</body>
</html>