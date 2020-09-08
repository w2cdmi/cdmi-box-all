<%@ page language="java" import="java.util.*" pageEncoding="utf8" %>
<!DOCTYPE html>
<html>
<head>
    <META HTTP-EQUIV="Expires" CONTENT="0">
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <META HTTP-EQUIV="Cache-control"
          CONTENT="no-cache, no-store, must-revalidate">
    <META HTTP-EQUIV="Cache" CONTENT="no-cache">
</head>
<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);
%>
<body>

</body>
<script type="text/javascript">
    var protocol="${protocol}";
/*     alert('${forwordUrl}'); */
    window.location.href ="${forwordUrl}";
</script>
</html>