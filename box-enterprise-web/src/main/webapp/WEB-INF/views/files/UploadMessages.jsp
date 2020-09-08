<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<HEAD>
    <META HTTP-EQUIV="Expires" CONTENT="0">
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <META HTTP-EQUIV="Cache-control"
          CONTENT="no-cache, no-store, must-revalidate">
    <META HTTP-EQUIV="Cache" CONTENT="no-cache">
</HEAD>
<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);
%>
<script type="text/javascript">
    var existMsg1 = '<spring:message code="uploadify.exist.msg1"/>';
    var existMsg2 = '<spring:message code="uploadify.exist.msg2"/>';
    var existServerMsg1 = '<spring:message code="uploadify.exist.server.msg1"/>';
    var existServerMsg2 = '<spring:message code="uploadify.exist.server.msg2"/>';
    var completeMsg = '<spring:message code="uploadify.msg.complete"/>';
    var cancelMsg = '<spring:message code="uploadify.msg.cancel"/>';
</script>