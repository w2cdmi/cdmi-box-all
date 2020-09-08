<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="../../common/common.jsp" %>
    <script src="${ctx}/static/js/public/validate/jquery.validate.min.js" type="text/javascript"></script>
    <%@ include file="../../common/messages.jsp" %>

</head>
<body>
<div class="pop-content add-group-con">
    <form class="form-horizontal" id="creatGroupForm">
        <div class="control-group">
            <label class="control-label" for=""><em>*</em><spring:message code='common.field.name'/>: </label>
            <div class="controls">
                <input type="text" class="span4" maxlength="255" id="name" name="name"/>
                <span class="validate-con bottom"><div></div></span>
            </div>
        </div>
        <div class="control-group" style="display:none">
            <label class="control-label" for=""><spring:message code='group.field.label.type'/>: </label>
            <div class="controls">
                <label class="radio inline"><input type="radio" name="type" value="private" checked/><spring:message
                        code='group.type.private'/></label>
                <label class="radio inline"><input type="radio" name="type" value="public"/><spring:message
                        code='group.type.public'/></label>
            </div>
        </div>
        <form:errors path="*"/>
        <div class="control-group">
            <label class="control-label" for=""><spring:message code='group.field.label.description'/>: </label>
            <div class="controls">
                <textarea class="span4 H80" id="description" name="description" maxlength="1023" rows="4"></textarea>
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
                var pattern = /^[^.·!#\/<>\\%?'"&,;]{2,255}$/;
                if (pattern.test(value)) {
                    return true;
                } else {
                    return false;
                }
            }
        );

        $.validator.addMethod(
            "isNameNotExist",
            function (value, element, param) {
                value = value.trim();
                var ret = false;
                $.ajax({
                    type: "POST",
                    async: false,
                    url: "${ctx}/user/group/checkSameName?" + new Date().toString(),
                    data: $("#creatGroupForm").serialize(),
                    success: function (data) {
                        if (typeof(data) == 'string' && data.indexOf('<html>') != -1) {
                            window.location.href = "${ctx}/logout";
                            return;
                        }
                        ret = data;
                    }
                });
                return !ret;
            }
        );

        $("#creatGroupForm").validate({
            rules: {
                name: {
                    required: true,
                    rangelength: [2, 255],
                    isValidFileName: true,
                    isNameNotExist: true
                },
                description: {
                    maxlength: [1023]
                }
            },
            messages: {
                name: {
                    required: "<spring:message code='file.errorMsg.nameRequired'/>",
                    isValidFileName: "<spring:message code='group.error.name.tips'/>",
                    rangelength: "<spring:message code='group.add.length.range'/>",
                    isNameNotExist: "<spring:message code='group.name.exist'/>"
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

    function submitGroup() {
        $("#name").val($("#name").val().trim());
        if (!$("#creatGroupForm").valid()) {
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
            type: "POST",
            url: "${ctx}/user/group/createGroup",
            data: $("#creatGroupForm").serialize(),
            error: function (request) {
                var _exception = request.responseText;
                if (_exception == "Unauthorized") {
                    handlePrompt("error", "<spring:message code='group.error.operation.fail'/>");
                } else if (_exception == "InvalidParameter") {
                    handlePrompt("error", "<spring:message code='group.error.operation.fail'/>");
                    $("#name").val("").focus();
                    top.ymPrompt_enableModalbtn("#btn-focus");
                } else if (_exception == "Forbidden") {
                    handlePrompt("error", "<spring:message code='group.error.forbidden'/>");
                } else if (_exception == "InvalidSpaceStatus") {
                    handlePrompt("error", "<spring:message code='group.error.user.forbidden'/>");
                } else {
                    handlePrompt("error", "<spring:message code='group.error.operation.fail'/>");
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
        top.window.frames[0].location = "${ctx}/user/group";
    }
</script>
</body>
</html>
