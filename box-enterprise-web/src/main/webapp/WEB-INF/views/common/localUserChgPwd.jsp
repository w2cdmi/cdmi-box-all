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
        <form class="form-horizontal" id="modifyPwdForm">
            <div class="control-group">
                <label class="control-label" for="input"><em>*</em><spring:message code='common.account.password.old'/>:</label>
                <div class="controls">
                    <input class="span4" type="password" id="oldPassword" name="oldPassword" value=""
                           autocomplete="off"/>
                    <span class="validate-con bottom"><div></div></span>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label" for="input"><em>*</em><spring:message code='common.account.password.new'/>:</label>
                <div class="controls">
                    <input class="span4" type="password" id="password" name="password" value="" autocomplete="off"/>
                    <span class="validate-con bottom"><div></div></span>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label" for="input"><em>*</em><spring:message
                        code='common.account.password.confirm'/>:</label>
                <div class="controls">
                    <input class="span4" type="password" id="confirmPassword" name="confirmPassword" value=""
                           autocomplete="off"/>
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
        equalTo: "<spring:message code='common.validate.equalTo'/>"
    });
    $.validator.addMethod(
        "isValidPwd",
        function (value, element, param) {
            var ret = false;
            $.ajax({
                type: "POST",
                async: false,
                url: "${ctx}/syscommon/validpwd",
                data: $("#modifyPwdForm").serialize(),
                success: function (data) {
                    ret = true;
                },
                error: function (request) {
                    if (request.responseText == 1) {
                        handlePrompt("error", "<spring:message code='common.account.password.rule1'/>");
                    } else if (request.responseText == 2) {
                        handlePrompt("error", "<spring:message code='common.account.password.rule2'/>");
                    } else {
                        handlePrompt("error", "<spring:message code='common.account.password.rule3'/>");
                    }

                }
            });
            return ret;
        },
        $.validator.format("<spring:message code='common.account.password.error.format'/>")
    );
    $.validator.addMethod(
        "isValidOldPwd",
        function (value, element, param) {
            var ret = false;
            $.ajax({
                type: "POST",
                async: false,
                url: "${ctx}/syscommon/validOldpwd",
                data: $("#modifyPwdForm").serialize(),
                success: function (data) {
                    ret = true;
                }
            });
            return ret;
        },
        $.validator.format("<spring:message code='common.account.password.rule'/>")
    );
    $(document).ready(function () {
        $("#modifyPwdForm").validate({
            rules: {
                oldPassword: {
                    required: true
                    //isValidOldPwd:true
                },
                password: {
                    required: true,
                    isValidPwd: true
                },
                confirmPassword: {
                    required: true,
                    equalTo: "#password"
                }
            },
            onfocusout: false
        });
    });
    function submitModifyPwd() {
        var oldP = $.trim($("#oldPassword").val());
        $("#oldPassword").val(oldP);
        if (!$("#modifyPwdForm").valid()) {
            return false;
        }
        $.ajax({
            type: "POST",
            url: "${ctx}/user/changePwd",
            data: $('#modifyPwdForm').serialize(),
            error: function (request) {
                switch (request.responseText) {
                    case "OldPasswordErrorException":
                        handlePrompt("error", "<spring:message code='common.account.password.error'/>");
                        $("#oldPassword").val("");
                        $("#password").val("");
                        $("#confirmPassword").val("");
                        break;
                    case "PasswordSameException":
                        handlePrompt("error", "<spring:message code='common.account.password.same'/>");
                        $("#oldPassword").val("");
                        $("#password").val("");
                        $("#confirmPassword").val("");
                        break;
                    case "UserLockedException":
                        handlePrompt("error", "<spring:message code='common.account.password.islock'/>");
                        $("#oldPassword").val("");
                        $("#password").val("");
                        $("#confirmPassword").val("");
                        break;
                    default:
                        handlePrompt("error", "<spring:message code='common.oper.fail'/>");
                        $("#oldPassword").val("");
                        $("#password").val("");
                        $("#confirmPassword").val("");
                        break;
                }
            },
            success: function (data) {
                if (typeof(data) == 'string' && data.indexOf('<html>') != -1) {
                    window.location.reload();
                    return;
                }
                top.ymPrompt.close();
                top.handlePrompt("success", "<spring:message code='common.oper.success'/>");
            }
        });
    }
</script>
</body>
</html>
