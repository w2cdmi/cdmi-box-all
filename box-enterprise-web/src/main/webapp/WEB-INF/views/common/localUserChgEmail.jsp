<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title></title>
    <%@ include file="./common.jsp" %>
    <%@ include file="./messages.jsp" %>
</head>
<body>
<div class="pop-content modifiy-con">
    <div class="form-con">
        <form id="modifyEmailForm" class="form-horizontal">
            <div class="control-group">
                <label class="control-label" for="input"><em>*</em><spring:message
                        code='common.label.user.email'/>:</label>
                <div class="controls">
                    <input class="span4" type="text" id="email" name="email" maxlength="127"
                           value="<c:out value='${email}'/>"/>
                    <span class="validate-con bottom"><div></div></span>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label" for="input"><em>*</em><spring:message code='login.password'/>:</label>
                <div class="controls">
                    <input class="span4" type="password" name="password" maxlength="127" autocomplete="off"/>
                    <span class="validate-con bottom"><div></div></span>
                </div>
            </div>
            <input type="hidden" id="token" name="token" value="<c:out value='${token}'/>"/>
        </form>
    </div>
</div>
<script type="text/javascript">
    jQuery.extend(jQuery.validator.messages, {
        required: "<spring:message code='common.validate.notempty'/>",
        maxlength: jQuery.validator.format("<spring:message code='common.validate.maxlength.255'/>"),
    });
    $.validator.addMethod(
        "isValidEmail",
        function (value, element) {
            return /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/.test(value);
        },
        $.validator.format("<spring:message code='common.validate.email'/>")
    );
    $(document).ready(function () {
        $("#modifyEmailForm").validate({
            rules: {
                email: {
                    required: true,
                    isValidEmail: true,
                    minlength: [5],
                    maxlength: [127]
                },
                password: {
                    required: true,
                    minlength: [8],
                    maxlength: [127]
                }
            }
        });
        $("#email").keydown(function (event) {
            if (event.keyCode == 13) {
                submitModifyEmail();
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
    function submitModifyEmail() {
        if (!$("#modifyEmailForm").valid()) {
            return false;
        }
        $.ajax({
            type: "POST",
            url: "${ctx}/user/changeEmail",
            data: $('#modifyEmailForm').serialize(),
            error: function (request) {
                switch (request.responseText) {
                    case "passwordCorrectedException":
                        handlePrompt("error", "<spring:message code='anon.pwd.error'/>");
                        break;
                    case "EmailChangeConflictException":
                        handlePrompt("error", "<spring:message code='email.change.conflict'/>");
                        break;
                    default:
                        handlePrompt("error", "<spring:message code='common.oper.fail'/>");
                        break;
                }
            },
            success: function () {
                top.ymPrompt.close();
                top.handlePrompt("success", "<spring:message code='common.oper.success'/>");

                top.window.parent.frames[0].doChangeEmail($("#email").val());
            }
        });
    }

</script>
</body>
</html>
