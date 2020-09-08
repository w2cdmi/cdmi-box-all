<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="../common/common.jsp" %>
    <%@ include file="../common/messages.jsp" %>
</head>
<body>
<div class="header">
    <div class="header-con">
        <div class="logo" id="logoBlock"><img/></div>
    </div>
</div>
<div class="body">
    <div class="body-con clearfix register-con">
        <h4><spring:message code="anon.resetpwd"/></h4>
        <div class="form-horizontal form-con  clearfix">
            <form class="form-horizontal" id="modifyPwdForm">
                <input type="hidden" id="loginName" name="loginName" value="<c:out value='${loginName}'/>"/>
                <input type="hidden" id="validateKey" name="validateKey" value="<c:out value='${validateKey}'/>"/>
                <div class="control-group">
                    <label class="control-label" for="input"><em>*</em><spring:message code="login.username"/></label>
                    <div class="controls">
                        <input class="span4" type="text" id="name" name="name" value=""/>
                        <span class="validate-con"><div></div></span>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="input"><em>*</em><spring:message
                            code="common.account.password.new"/>:</label>
                    <div class="controls">
                        <input class="span4" type="password" id="password" name="password" value="" autocomplete="off"/>
                        <span class="validate-con"><div></div></span>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="input"><em>*</em><spring:message
                            code="common.account.password.confirm"/>:</label>
                    <div class="controls">
                        <input class="span4" type="password" id="confirmPassword" name="confirmPassword" value=""
                               autocomplete="off"/>
                        <span class="validate-con"><div></div></span>
                    </div>
                </div>
                <div class="control-group">
                    <div class="controls">
                        <button id="chgPassword_btn" type="button" class="btn btn-primary" onclick="submitReset()">
                            <spring:message code="common.OK"/></button>
                        <button id="chgPassword_btn" type="button" class="btn" onclick="backLogin()"><spring:message
                                code="common.back"/></button>
                    </div>
                </div>
                <input type="hidden" id="token" name="token" value="<c:out value='${token}'/>"/>
            </form>
        </div>
    </div>
</div>

</body>
<script type="text/javascript">
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
                }
            });
            return ret;
        },
        $.validator.format("<spring:message code='common.account.password.rule'/>")
    );
    $(document).ready(function () {
        loadSysSetting("${ctx}", '<spring:message code="common.language1" />');
        $("#modifyPwdForm").validate({
            rules: {
                name: {
                    required: true
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
            messages: {
                name: {
                    required: "<spring:message code='common.validate.notempty'/>"
                },
                password: {
                    required: "<spring:message code='common.validate.notempty'/>"
                },
                confirmPassword: {
                    required: "<spring:message code='common.validate.notempty'/>",
                    equalTo: "<spring:message code='common.validate.equalTo'/>"
                }
            },
            onkeyup: function (element) {
                $(element).valid()
            },
            onfocusout: false
        });
    });
    function submitReset() {
        if (!$("#modifyPwdForm").valid()) {
            return false;
        }
        $.ajax({
            type: "POST",
            url: "${ctx}/syscommon/doreset",
            data: $('#modifyPwdForm').serialize(),
            error: function (request) {
                errorMessage(request);
            },
            success: function () {
                top.ymPrompt.alert({
                    title: '<spring:message code="common.title.info"/>',
                    message: '<spring:message code="anon.resetPwdSuccess"/>',
                    handler: backLogin
                });
                setTimeout(function () {
                    ymPrompt.doHandler('ok')
                }, 3000);
            }
        });
    }

    function errorMessage(request) {
        var responseObj = $.parseJSON(request.responseText);
        switch (responseObj.message) {
            case "namePwdSameError":
                handlePrompt("error", '<spring:message code="anon.namePwdSameError"/>');
                break;
            case "nameError":
                handlePrompt("error", '<spring:message code="anon.nameError"/>');
                break;
            default:
                top.handlePrompt("error", '<spring:message code="anon.resetPwdError"/>');
                break;
        }
    }

    function backLogin() {
        window.location = "${ctx}/login";
    }

</script>
</html>
