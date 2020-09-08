<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%
    request.setAttribute("token", CSRFTokenManager.getTokenForSession(session));
%>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="../common/common.jsp" %>
</head>

<body>
<div class="pop-content">
    <div id="memberEmail">
        <span class="help-block"><spring:message code='teamSpace.title.tips'/></span>
        <div id="emailMessage" class="controls row-fluid">
            <textarea id="messageText" class="span12" rows="5" maxlength=2000
                      placeholder="<spring:message code='teamSpace.addMessage'/>"
                      onkeyup='doMaxLength(this)'></textarea>
        </div>
    </div>
</div>
</body>
<script type="text/javascript">
    var teamId = "<c:out value='${teamId}'/>";

    function submitSentEmail(tp, ownerId, teamSpaceId, idArray) {
        var ids = "";
        for (var i = 0; i < idArray.length; i++) {
            var id = idArray[i];
            if (i == 0) {
                ids = idArray[i];
            } else {
                ids = ids + ',' + idArray[i];
            }
        }
        var spaceId = teamId;
        if (teamSpaceId != undefined) {
            spaceId = teamSpaceId;
        }
        var message = $("#messageText").val();
        var requestUrl = "${ctx}/teamspace/member/sendEmail/" + spaceId;
        var params = {
            "ids": ids,
            "message": message,
            "token": "<c:out value='${token}'/>"
        };
        $.ajax({
            type: "POST",
            url: requestUrl,
            data: params,
            error: function (request) {
                top.ymPrompt.close();
                top.handlePrompt("error", '<spring:message code="teamSpace.sendEmail.error"/>');
            },
            success: function (data) {
                top.ymPrompt.close();
                top.handlePrompt("success", '<spring:message code="teamSpace.sendEmail.ok"/>');
            }
        });
    }
</script>
</html>
