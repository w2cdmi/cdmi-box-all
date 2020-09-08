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
        <form id="modifyMobileForm" class="form-horizontal">
            <div class="control-group">
                <label class="control-label" for="input"><em>*</em><spring:message
                        code='common.label.user.mobile'/>:</label>
                <div class="controls">
                    <input class="span4" type="text" id="mobile" name="mobile" maxlength="11"
                           value="<c:out value='${mobile}'/>"/>
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
        "isValidMobile",
        function (value, element) {
            return /^1(3|4|5|7|8)\d{9}$/.test(value);
        },
        $.validator.format("<spring:message code='common.validate.mobile'/>")
    );
    $(document).ready(function () {
        $("#modifyMobileForm").validate({
            rules: {
                mobile: {
                    required: true,
                    isValidMobile: true,
                    maxlength: [11],
                    minlength: [11]
                },
                password: {
                    required: true,
                    minlength: [8],
                    maxlength: [127]
                }
            }
        });
        $("#mobile").keydown(function (event) {
            if (event.keyCode == 13) {
                submitModifyMobile();
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
    function submitModifyMobile() {
        if (!$("#modifyMobileForm").valid()) {
            return false;
        }
        $.ajax({
            type: "POST",
            url: "${ctx}/user/changeMobile",
            data: $('#modifyMobileForm').serialize(),
            error: function (request) {
                switch (request.responseText) {
                    case "passwordCorrectedException":
                        handlePrompt("error", "<spring:message code='anon.pwd.error'/>");
                        break;
                    case "MobileChangeConflictException":
                        handlePrompt("error", "<spring:message code='mobile.change.conflict'/>");
                        break;
                    default:
                        handlePrompt("error", "<spring:message code='common.oper.fail'/>");
                        break;
                }
            },
            success: function () {
                top.ymPrompt.close();
                top.handlePrompt("success", "<spring:message code='common.oper.success'/>");

                top.window.parent.frames[0].doChangeMobile($("#mobile").val());
            }
        });
    }

</script>
</body>
</html>
