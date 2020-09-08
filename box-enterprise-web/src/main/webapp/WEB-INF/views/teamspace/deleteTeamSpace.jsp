<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="../common/common.jsp" %>
    <script src="${ctx}/static/js/public/validate/jquery.validate.min.js" type="text/javascript"></script>
    <%@ include file="../common/messages.jsp" %>
</head>
<body>
<div class="pop-content">
    <form class="form-horizontal" id="UnlayResultForm">
        <input type="hidden" id="teamId" name="teamId" value="<c:out value='${teamId}'/>"/>
        <div class="control-group delete-teamspace">
            <label class="control-label" for=""><img
                    src="${ctx}/static/skins/default/img/teamspace/question.png"></label>
            <div class="controls">
                <spring:message code="teamSpace.msg.isContinue"/><br/><br/>
                <spring:message code="teamSpace.msg.pleaseInput1"/>&nbsp;<span
                    style="font-weight:bold; display:inline-block; vertical-align:middle; max-width:150px;overflow:hidden;white-space: nowrap;-o-text-overflow: ellipsis;text-overflow:ellipsis;"
                    class="name-txt" title="<c:out value='${teamName}'/>"><c:out
                    value='${teamName}'/></span>&nbsp;<spring:message code="teamSpace.msg.pleaseInput2"/>
                <br/><br/>
                <input type="text" id="result" name="result" style="width:80%" maxlength="255"/>
                <span class="validate-con bottom"><div></div></span>
            </div>
        </div>
        <input type="hidden" id="token" name="token" value="<c:out value='${token}'/>"/>
    </form>
</div>
<script type="text/javascript">
    $(document).ready(function () {
        $("#result").focus();
        $("#UnlayResultForm").validate({
            rules: {
                result: {
                    required: true
                }
            },
            onkeyup: function (element) {
                $(element).valid()
            },
            onfocusout: false
        });
        $("#result").keydown(function (event) {
            if (event.keyCode == 13) {
                submitUnlay();
                if (window.event) {
                    window.event.cancelBubble = true;
                    window.event.returnValue = false;
                } else {
                    event.stopPropagation();
                    event.preventDefault();
                }
            }
        })
    });

    function submitUnlay() {
        $("#result").val($("#result").val().trim());
        if (!$("#UnlayResultForm").valid()) {
            return false;
        }
        if ($("#result").val().toUpperCase() != 'YES') {
            return false;
        }
        $("#result").blur();
        top.ymPrompt_disableModalbtn("#btn-focus");
        $.ajax({
            type: "POST",
            url: "${ctx}/teamspace/deleteTeamSpace",
            data: $("#UnlayResultForm").serialize(),
            error: function (request) {
                handlePrompt("error", "<spring:message code='operation.failed'/>");
            },
            success: function () {
                top.ymPrompt.close();
                top.handlePrompt("success", "<spring:message code='operation.success'/>");
                top.listTeam(1);
            }
        });
    }
</script>
</body>
</html>
