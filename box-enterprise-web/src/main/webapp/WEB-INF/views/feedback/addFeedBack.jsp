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
<div class="pop-content" style="height: 270px;">
    <form class="form-horizontal label-w100" id="creatFeedBackForm" name="creatFeedBackForm">
        <div class="control-group">
            <label class="control-label" for=""><em>*</em><b><spring:message code='feedback.create.problemTitle'/>：</b></label>
            <div class="controls">
                <input type="text" class="span4" id="problemTitle" name="problemTitle"/>
                <span class="validate-con bottom"><div></div></span>
            </div>
        </div>

        <div class="control-group">
            <label class="control-label" for=""><b><spring:message code='feedback.create.problemType'/>：</b></label>
            <div class="controls">
                <select name="problemType" class="span4">
                    <option value="0"><spring:message code='feedback.create.suggestion'/></option>
                    <option value="1"><spring:message code='feedback.create.fault'/></option>
                </select>
                <span class="validate-con bottom"><div></div></span>
            </div>
        </div>

        <div class="control-group">
            <label class="control-label" for=""><em>*</em><b><spring:message code='feedback.create.problemDescription'/>：</b></label>
            <div class="controls">
                <textarea class="span4" rows="8" cols="30" name="problemDescription" id="problemDescription"></textarea>
                <span class="validate-con bottom"><div></div></span>
            </div>
        </div>
        <input type="hidden" id="token" name="token" value="${token}"/>
    </form>
</div>
<script type="text/javascript">
    $(document).ready(function () {

        $("#creatFeedBackForm").validate({
            rules: {
                problemTitle: {
                    required: true,
                    rangelength: [1, 255]
                },
                problemDescription: {
                    required: true,
                    rangelength: [1, 1024]
                }

            }
        });

        $("#messageAddr").keydown(
            function (event) {
                if (event.keyCode == 13) {
                    searchMessageTo();
                    return false;
                }
            });

        $("label").tooltip({container: "body", placement: "top", delay: {show: 100, hide: 0}, animation: false});
    });


    function submitFeedBack() {

        if (!$("#creatFeedBackForm").valid()) {
            return false;
        }
        //top.ymPrompt_disableModalbtn("#btn-focus");
        $.ajax({
            type: "POST",
            url: "${ctx}/feedback/createFeedBack",
            data: $("#creatFeedBackForm").serialize(),
            error: function (request) {

                handlePrompt("error", "<spring:message code='feedback.create.fail'/>");

            },
            success: function () {
                top.ymPrompt.close();
                top.handlePrompt("success", "<spring:message code='feedback.create.success'/>");
                top.document.getElementById("navUserFeedBack").click();
            }
        });
    }
</script>
</body>
</html>
