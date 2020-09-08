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
            <form class="form-con form-horizontal" id="accessCodeForm" name="accessCodeForm">
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
                        <input type="text" id="captcha" name="captcha" class="span3 required" size="4" maxlength="4"/>
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
                        <button type="button" onclick="doSubmit()" class="btn btn-large btn-primary"><spring:message
                                code="button.ok"/></button>
                    </div>
                </div>
                <input type="hidden" id="token" name="token" value="<c:out value='${token}'/>"/>
            </form>
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

    $(document).ready(function () {
        loadSysSetting("${ctx}", '<spring:message code="common.language1" />');
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
