<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="../../common/common.jsp" %>
    <script src="${ctx}/static/js/public/validate/jquery.validate.min.js" type="text/javascript"></script>
    <%@ include file="../../common/messages.jsp" %>
</head>
<body>
<div class="pop-content">
    <form class="form-horizontal" id="editGroupForm">
        <input type="hidden" id="groupId" name="groupId" value="<c:out value='${group.id}'/>"/>
        <div class="control-group">
            <label class="control-label" for=""><em>*</em><spring:message code='common.field.name'/>: </label>
            <div class="controls">
                <input class="span4" type="text" id="name" name="name" maxlength="255"
                       value="<c:out value='${group.name}'/>"/>
                <span class="validate-con bottom"><div></div></span>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for=""><spring:message code='teamSpace.label.description'/>: </label>
            <div class="controls">
                <textarea class="span4 H80" id="description" name="description" maxlength="1023" rows="4"><c:out
                        value='${group.description}'/></textarea>
                <span class="validate-con bottom"><div></div></span>
            </div>
        </div>
        <input type="hidden" id="token" name="token" value="<c:out value='${token}'/>"/>
    </form>
</div>
<script type="text/javascript">
    $(document).ready(function () {
        $("#name").focus();

        $.validator.addMethod(
            "isValidFileName",
            function (value, element) {
                value = value.trim();
                var pattern = /^[^.!#\/<>\\%?'"&,;]{2,255}$/;
                if (pattern.test(value)) {
                    return true;
                } else {
                    return false;
                }
            }
        );

        $("#editGroupForm").validate({
            rules: {
                name: {
                    required: true,
                    rangelength: [2, 255],
                    isValidFileName: true
                },
                description: {
                    maxlength: [1023]
                }
            },
            messages: {
                name: {
                    required: "<spring:message code='file.errorMsg.nameRequired'/>",
                    isValidFileName: "<spring:message code='group.error.name.tips'/>",
                }
            },
            onkeyup: function (element) {
                $(element).valid()
            },
            onfocusout: false
        });

        $("#name").keydown(function (event) {
            if (event.keyCode == 13) {
                submitGroup();
                if (window.event) {
                    window.event.cancelBubble = true;
                    window.event.returnValue = false;
                } else {
                    event.stopPropagation();
                    event.preventDefault();
                }
            }
        })

        $("#description").keydown(function (event) {
            if (event.keyCode == 13) {
                submitGroup();
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

    function checkName(name) {
        var pattern = /^[^.!#\/<>\\%?'"&,;]{2,255}$/;
        if (pattern.test(name)) {
            return true;
        } else {
            return false;
        }
    }

    function submitGroup() {
        $("#name").val($("#name").val().trim());
        if (!checkName($("#name").val())) {
            ymPrompt_disableModalbtn("#btn-focus");
            handlePrompt("error", "<spring:message code='group.error.name.tips'/>");
            return;
        }
        if (!$("#editGroupForm").valid()) {
            return false;
        }

        var desStr = $("#description").val();
        if (desStr.length > 1023) {
            ymPrompt_disableModalbtn("#btn-focus");
            handlePrompt("error", "<spring:message code='group.error.description.length'/>");
            return;
        }
        $("#name").blur();
        top.ymPrompt_disableModalbtn("#btn-focus");
        $.ajax({
            type: "post",
            url: "${ctx}/user/group/editGroupInfo",
            data: $("#editGroupForm").serialize(),
            error: function (request) {
                var statusText = request.statusText;
                if (statusText == "ExceedMaxSpace") {
                    handlePrompt("error", "<spring:message code='teamSpace.error.exceedMaxSpace'/>");
                } else if (statusText == "Forbidden") {
                    handlePrompt("error", "<spring:message code='teamSpace.error.forbiddenAdd'/>");
                } else {
                    handlePrompt("error", "<spring:message code='operation.failed'/>");
                }
            },
            success: function () {
                refreshWindow();
                top.ymPrompt.close();
                top.handlePrompt("success", "<spring:message code='operation.success'/>");
            }
        });
    }

    function refreshWindow() {
        top.window.frames[0].refreshCurPage();
    }
</script>
</body>
</html>
