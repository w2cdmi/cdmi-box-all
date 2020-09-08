<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title><spring:message code="main.title"/></title>
    <%@ include file="../common/common.jsp" %>
    <script src="${ctx}/static/js/public/validate/jquery.validate.min.js" type="text/javascript"></script>
    <%@ include file="../common/messages.jsp" %>
</head>

<body>

<%@ include file="linkHeader.jsp" %>

<div class="body">
    <div class="body-con clearfix body-con-no-menu">
        <div id="loginBlock" class="link-page-code">
            <h3><spring:message code="link.inputAccessCode"/></h3>
            <div class="form-con">
                <form class=" form-horizontal" id="mailForm" name="mailForm" action="sendAccessCode" method="post">
                    <input type="hidden" id="linkCode" name="linkCode" value="<c:out value='${linkCode}'/>"/>
                    <div class="control-group">
                        <label class="control-label" for=""><em>*</em><spring:message code="user.phone"/>: </label>
                        <div class="controls">
                            <input type="text" id="mail" name="mail" class="span3 required" maxlength="255"/>
                            <span class="validate-con bottom inline-span3"><div></div></span>
                            <button type="button" onclick="sendAccessCode()" class="btn"><spring:message
                                    code="button.obtain"/></button>
                            <span class="help-block"><spring:message code="input.rightPhoneobtainCode"/></span>
                        </div>
                    </div>
                </form>
                <form class=" form-horizontal" id="accessCodeForm" name="accessCodeForm"
                      action="${ctx}/share/inputAccessCode" method="post">
                    <input type="hidden" id="linkCode" name="linkCode" value="<c:out value='${linkCode}'/>"/>
                    <div class="control-group">
                        <label class="control-label" for=""><em>*</em><spring:message code="link.label.accessCode"/>:
                        </label>
                        <div class="controls">
                            <input type="text" id="acessCode" name="acessCode" class="span3 required" maxlength="20"/>
                            <span class="validate-con"><div></div></span>
                        </div>
                    </div>
                    <div class="control-group">
                        <label for="captcha" class="control-label"><em>*</em><spring:message code="link.label.captcha"/>:
                        </label>
                        <div class="controls">
                            <input type="text" id="captcha" name="captcha" class="span3 required" size="4"
                                   maxlength="4"/>
                            <span class="validate-con"><div></div></span>
                        </div>
                    </div>
                    <div class="control-group">
                        <div class="controls">
                            <label for="codeImg" class="field"></label>
                            <img title='<spring:message code="link.toChangeCaptcha" />' id="img_captcha"
                                 onclick="javascript:loadimage();" src="${ctx}/verifycode">(<spring:message
                                code="link.notSeeCaptcha"/><a href="javascript:void(0)"
                                                              onclick="javascript:loadimage()"><spring:message
                                code="link.changeCaptcha"/></a>)
                        </div>
                    </div>
                    <div class="control-group">
                        <div class="controls">
                            <button type="submit" onclick="return doSubmit();" class="btn btn-large btn-primary">
                                <spring:message code="button.ok"/></button>
                        </div>
                    </div>
                    <input type="hidden" id="token" name="token" value="<c:out value='${token}'/>"/>

                </form>
            </div>
        </div>
    </div>
</div>

<%@ include file="../common/footer.jsp" %>

</body>
<script type="text/javascript">

    function doSubmit() {
        if (!$("#accessCodeForm").valid()) {
            return false;
        }
        $("#acessCode").val($("#acessCode").val().trim());
        $.ajax({
            type: "POST",
            url: '${ctx}/share/inputAccessCode',
            data: $("#accessCodeForm").serialize(),
            error: function (request) {
                loadimage();
                if (request.status == 404) {
                    handlePrompt("error", '<spring:message code="link.view.NoSuchItems" />');
                } else if (request.status == 405) {// jump login
                    top.location.reload();
                } else {
                    handlePrompt("error", '<spring:message code="link.codeError" />');
                }
            },
            success: function (data) {
                top.location.reload();
            }
        });
        return false;
    }
    function sendAccessCode() {
        if (!$("#mailForm").valid()) {
            return false;
        }
        var mail = $("#mail").val().trim();
        $.ajax({
            type: "POST",
            url: '${ctx}/share/sendAccessCode',
            data: {
                'mail': mail,
                'linkCode': "<c:out value='${linkCode}'/>",
                'token': "<c:out value='${token}'/>"
            },
            error: function (request) {
                if (request.status == 405) {// jump login
                    top.location.reload();
                } else {
                    $("#mail").val("").focus();
                    handlePrompt("error", '<spring:message code="user.phoneAuthError" />');
                }
            },
            success: function (data) {
                handlePrompt("success", '<spring:message code="operation.success" />');
            }
        });
    }
    $.validator.addMethod(
        "isCaptcha",
        function (value, element) {
            return value != null && value != "" && value.length == 4;
        },
        $.validator.format("<spring:message code='link.invalidCaptchaLen' />")
    );
    $.validator.addMethod(
        "isAcessCode",
        function (value, element) {
            return value != null && value != "";
        }
    );

    $.validator.addMethod(
        "isMail",
        function (value, element) {
            if (value == null || value == "") {
                return false;
            }
            var pattern = /^[0-9]{1,11}$/;
            if (!pattern.test(value) || value.length != 11) {
                return false;
            }
            return true;
        },
        $.validator.format("<spring:message code='user.phoneNumberFormatError' />")
    );

    $(document).ready(function () {
        loadSysSetting("${ctx}", '<spring:message code="common.language1" />');
        $("#mailForm").validate({
            rules: {
                mail: {
                    required: true,
                    isMail: true
                }
            },
            onkeyup: false,
            onfocusout: false
        });
        $("#mail").blur(function () {
            $("#mail").val($("#mail").val().trim());
        });
        $("#accessCodeForm").validate({
            rules: {
                acessCode: {
                    required: true,
                    isAcessCode: true
                },
                captcha: {
                    required: true,
                    isCaptcha: true
                }
            },
            onkeyup: false,
            onfocusout: false
        });
        $("#acessCode").blur(function () {
            $("#acessCode").val($("#acessCode").val().trim());
        });
    });

    function loadimage() {
        $("#img_captcha").attr("src", "${ctx}/verifycode?" + Math.random());
        $("#captcha").val("").focus();
    }
</script>
</html>
