<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="pw.cdmi.box.disk.utils.CSRFTokenManager" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="cse" uri="http://cse.huawei.com/custom-function-taglib" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%
    request.setAttribute("token", CSRFTokenManager.getTokenForSession(session));
%>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="./common.jsp" %>
</head>
<body>

<div>
    <textarea style="width:530px; height:250px; text-align:left" id="declaration"
              name="declaration">${concealDeclare.declaration}</textarea>
</div>
</body>
<script type="text/javascript">
    var id = "${cse:htmlEscape(concealDeclare.id)}"
    var params = {
        "id": id,
        "token": "<c:out value='${token}'/>"
    };

    function signDeclaration() {

        $.ajax({
            type: "POST",
            data: params,
            url: "${ctx}/syscommon/sign",
            error: function (request) {
                handlePrompt("error", '<spring:message code="declaration.sign.fail" />');
            },
            success: function () {
                top.ymPrompt.close();
                top.handlePrompt("success", '<spring:message code="declaration.sign.success"/>');
                top.document.getElementById("navAllFile").click();
            }
        });
    }
</script>
</html>
