<%@ page contentType="text/html;charset=UTF-8" %>
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
    function doDownLoadLinkError(request) {
        switch (request.responseText) {
            case "BadRequest":
                handlePrompt("error", '<spring:message code="link.view.BadRquest"/>');
                break;
            case "NoSuchLink":
            case "NoSuchItem":
                handlePrompt("error", '<spring:message code="link.view.NoSuchItems"/>');
                break;
            case "Forbidden":
            case "Unauthorized":
                handlePrompt("error", '<spring:message code="error.forbid"/>');
                break;
            case "LinkNotEffective":
                handlePrompt("error", '<spring:message code="link.view.NotEffective"/>');
                break;
            case "LinkExpired":
                handlePrompt("error", '<spring:message code="link.view.Expired"/>');
                break;
            case "FileScanning":
                handlePrompt("error", '<spring:message code='file.errorMsg.fileNotReady'/>');
                break;
            case "ScannedForbidden":
                handlePrompt("error", '<spring:message code='file.errorMsg.downloadNotAllowed'/>');
                break;
            case "SecurityMatrixForbidden":
                handlePrompt("error", "<spring:message code='error.forbid'/>");
                break;
            default:
                handlePrompt("error", '<spring:message code="link.view.NoSuchItems"/>');
                window.location.href = "";
        }
    }
</script>