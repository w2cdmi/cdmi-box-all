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
    <style>
        .circle {
            border-radius: 50%;
            height: 60px;
            border: 1px solid #dfe0e1;
            text-align: center;
            /* 宽度和高度需要相等 */
        }

        .stepLabel {
            text-align: center;
            horiz-align: center;
        }

        .stepNow {
            background-image: url("${pageContext.request.contextPath}/static/skins/default/img/edit.png");
            border: medium solid #0e90d2;
            background-size: cover;
        }

        .stepOver {
            background-image: url("${pageContext.request.contextPath}/static/skins/default/img/right.png");
            border: medium solid #00B83F;
            background-size: cover;
        }

        .stepLine {
            border: 1px solid #dfe0e1;
            width: 30%;
            margin: 30px 5px;
        }

        .onway {
            border: 1px solid #0e90d2;
        }

        input {
            width: 100%;
            height: 40px;
            font-size: medium;
            padding-left: 10px;
            margin-top: 20px;
            border-radius: 10px;
            border: 1px solid #dfe0e1;
        }

        .inputNow {
            border: 1px solid #0e90d2;
        }

        button {
            width: 100%;
            height: 40px;
            font-size: medium;
            padding-left: 10px;
            margin-top: 20px;
            border: 1px solid #dfe0e1;
        }

        #flow > div, hr {
            display: inline-block
        }

        #flow > div {
            width: 10%;
        }

        #flow {
            height: 30%;
            width: 80%;
            margin: 0 auto;
            overflow: hidden;
            padding-top: 40px;
        }

        .stepContent {
            width: 30%;
            margin: 0 auto;
            display: none;
        }
    </style>
</head>
<body style="background-color: #edfbfb">
<div class="header" style="position: relative">
    <div class="header-con">
        <div class="logo" id="logoBlock"><img/></div>
    </div>
</div>
<div style="width: 60%;margin:0 auto ">
    <div class="alert" style="font-size: medium;margin-top: 20px;">
        <i class="icon-lightbulb"></i><spring:message code="cloudapp.forget.password.notice"/>
    </div>
    <div style="height: 600px;background-color: whitesmoke;">
        <input type="hidden" id="token" value="${token}"/>
        <div style="text-align: center;font-size:large;padding-top: 20px;">
            <spring:message code="anon.forgotpwd"/>
        </div>
        <div style="width: 80%;margin: 0 auto;">
            <hr style="border-top:1px dashed;width: 100%;"/>
        </div>
        <div id="flow">
            <div>
                <div class="circle"><h1>1</h1></div>
                <div class="stepLabel"><spring:message code="cloudapp.forget.password.account"/></div>
            </div>
            <hr class="stepLine"/>
            <div>
                <div class="circle"><h1>2</h1></div>
                <div class="stepLabel"><spring:message code="cloudapp.forget.password.security"/></div>
            </div>
            <hr class="stepLine"/>
            <div>
                <div class="circle"><h1>3</h1></div>
                <div class="stepLabel" style="color: #c09853"><spring:message
                        code="cloudapp.forget.password.reset"/></div>
            </div>
        </div>
        <div class="stepContent" id="step1">
            <input placeholder='<spring:message code="cloudapp.forget.password.input.phone" />' id="loginName"><br/>
            <input placeholder='<spring:message code="cloudapp.forget.password.input.code" />' style="width: 50%"
                   id="captcha">
            <img title='<spring:message code="cloudapp.forget.password.get.code" />' style="width: 40%;height: 40px;"
                 id="img_captcha" onclick="loadimage();" src="${pageContext.request.contextPath}/verifycode">
            <button onclick="checkAccountInfo()" type="button" class="btn btn-primary"><spring:message
                    code="cloudapp.forget.password.next"/></button>
        </div>
        <div class="stepContent" id="step2">
            <select id="verifyType" onchange="selectChange()" style="height: 40px;border-radius: 10px;width: 100%">
                <option label='<spring:message code="initPwd.lable.email" />' value="email"></option>
                <option label='<spring:message code="cloudapp.forget.password.message" />' value="phone"></option>
            </select>
            <input id="loginInput" value="" type="hidden"/>
            <input id="checkInput" placeholder='<spring:message code="cloudapp.forget.password.mailorphone" />'/>
            <button class="btn btn-primary" onclick="sendIdentifyCode()"><spring:message
                    code="cloudapp.forget.password.sendmessage"/></button>
            <br/>
            <div id="smsDiv" hidden="hidden">
                <input type="text" required="required" id="identifyCode"
                       placeholder='<spring:message code="cloudapp.forget.password.messagecode"/>'/>
                <button onclick="checkIdentifyCode()" type="button" class="btn btn-primary"><spring:message
                        code="cloudapp.forget.password.next"/></button>
            </div>
        </div>
        <div class="stepContent" id="step3">
            <input type="text" required="required"
                   placeholder='<spring:message code="cloudapp.forget.password.newpassword" />'>
            <input type="text" required="required"
                   placeholder='<spring:message code="cloudapp.forget.password.repeat.password" />'>
            <br/>
            <button onclick="activeNextTab()" type="button" class="btn btn-primary"><spring:message
                    code="cloudapp.forget.password.click.modify"/></button>
        </div>
    </div>
</div>
</body>
<script type="text/javascript">

    var wait = 60;
    $(function () {
        activeNextTab(0);
        $("input").focus(function () {
            $("input").removeClass("inputNow");
            $(this).addClass("inputNow");
        });
    });
    function loadimage() {
        $("#img_captcha").attr("src", "${ctx}/verifycode?" + Math.random());
        $("#captcha").val("");
        $("#captcha").focus();
    }
    function activeNextTab(index) {
        var step = $(".circle:eq(" + index + ")");
        if (!step) {
            return;
        }
        if (index > 0) {
            $(".circle:eq(" + (index - 1) + ")").removeClass("stepNow");
            $(".circle:eq(" + (index - 1) + ")").addClass("stepOver");
        }
        step.empty();
        step.addClass("stepNow");
        if (index < 2) {
            $(".stepLine:eq(" + index + ")").addClass("onway");
        }
        $(".stepContent").hide();
        $(".stepContent:eq(" + index + ")").show();
    }
    function checkAccountInfo() {
        var loginName = $("#loginName").val();
        var token = $("#token").val();
        var captcha = $("#captcha").val();
        if (loginName == null || loginName.length == 0 || null == token) {
            top.handlePrompt("error", '<spring:message code="cloudapp.forget.password.not.null" />');
            return;
        }
        $("#loginInput").val(loginName);
        $.ajax({
            type: "GET",
            async: false,
            url: "${ctx}/syscommon/validResetCaptcha",
            data: {"token": token, "loginName": loginName, "captcha": captcha},
            error: function () {
                top.handlePrompt("error", '<spring:message code="cloudapp.forget.password.account.code.error" />');
                $("#loginName").focus();
                loadimage();
            },
            success: function () {
                activeNextTab(1);
            }
        });
    }
    function selectChange() {
        var type = $("#verifyType").val();
        if (type == "email") {
            $("#smsDiv").hide();
        }
    }
    function sendIdentifyCode() {
        var loginName = $("#loginName").val();
        var token = $("#token").val();
        var option = $("#verifyType").val();
        var inputValue = $("#checkInput").val();
        $.ajax({
            type: "POST",
            url: "${ctx}/syscommon/sendIdentifyCode",
            data: {"loginName": loginName, "token": token, "type": option, "inputValue": inputValue},
            error: function (request) {
                top.handlePrompt("error", "<spring:message code='common.validate.error'/>");
            },
            success: function () {
                if (option == "email") {
                    top.ymPrompt.alert({
                        title: '<spring:message code="common.title.info"/>',
                        message: '<spring:message code="anon.forgetPwdMsg" />',
                        handler: backLogin
                    });
                    setTimeout(function () {
                        ymPrompt.doHandler('ok')
                    }, 10000);
                } else {
                    top.ymPrompt.alert({
                        title: '<spring:message code="common.title.info"/>',
                        message: '<spring:message code="cloudapp.forget.password.modify.success"/>',
                        handler: backLogin
                    });
                    setTimeout(function () {
                        ymPrompt.doHandler('ok')
                    }, 10000);
                    if (option == "phone") {
                        $("#smsDiv").show();
                    }
                }

            }
        });
    }
    function checkIdentifyCode() {
        var identifyCode = $("#identifyCode").val();
        if (identifyCode == null || identifyCode.length == 0) {
            return;
        }
        $.ajax({
            type: "GET",
            async: false,
            url: "${ctx}/syscommon/resetPassword",
            data: {"token": token, "loginName": loginName, "captcha": captcha},
            error: function () {
                handlePrompt("error", '<spring:message code="cloudapp.forget.password.account.code.error" />', 1, 1, null, "loginBlock");
            },
            success: function (data) {
                activeNextTab(2);
            }
        });
    }
    function time(o) {
        if (wait == 0) {
            o.removeAttribute("disabled");
            o.value = '<spring:message code="cloudapp.forget.password.get.code" />';
            wait = 60;
        } else {
            o.setAttribute("disabled", true);
            o.value = '<spring:message code="cloudapp.forget.password.send.repeat" />' + "(" + wait + ")";
            wait--;
            setTimeout(function () {
                    time(o)
                },
                1000)
        }
    }


    $(document).ready(function () {
        loadSysSetting("${ctx}", '<spring:message code="common.language1" />');
        $("#forgetPwdForm").validate({
            rules: {
                contactPhone: {
                    required: true,
                    contactPhoneCheck: true,
                    minlength: [11],
                    maxlength: [11]
                },
                identifyCode: {
                    required: true,
                    minlength: [6],
                },
                newPassword: {
                    required: true,
                    isValidPwd: true,
                    minlength: [8]
                },
                confirmPassword: {
                    required: true,
                    equalTo: "#newPassword"
                }
            },
            messages: {
                contactPhone: {
                    contactPhoneCheck: '<spring:message code="cloudapp.forget.password.phonecheck.value" />'
                },
                confirmPassword: {
                    required: "<spring:message code='common.validate.notempty'/>",
                    equalTo: "<spring:message code='common.validate.equalTo'/>"
                }
            },
        });


    });

    $.validator.addMethod(
        "contactPhoneCheck",
        function (value, element) {
            var pattern = /^[0-9 +-]*$/;
            if (!pattern.test(value)) {
                return false;
            }
            return true;
        }
    );


    $.validator.addMethod(
        "isValidPwd",
        function (element) {
            var value = $("#newPassword").val();
            var re = new RegExp("[a-zA-Z]");
            var len = re.test(value);
            re = new RegExp("[0-9]");
            len = re.test(value);
            re = new RegExp("((?=[\x21-\x7e]+)[^A-Za-z0-9])");
            len = re.test(value);
            if (len) {
                return true;
            }
            return false;
        },
        $.validator.format("<spring:message code='common.account.password.rule'/>")
    );
    function checkIdentifyCode() {
        var loginName = $("#loginName").val();
        var token = $("#token").val();
        var identifyCode = $("#identifyCode").val();
        var inputValue = $("#checkInput").val();
        $.ajax({
            type: "POST",
            url: "${ctx}/syscommon/setPasswordByPhone",
            data: {"contactPhone": inputValue, "loginName": loginName, "identifyCode": identifyCode, "token": token},
            success: function () {
                top.ymPrompt.alert({
                    title: '<spring:message code="common.title.info"/>',
                    message: '<spring:message code="cloudapp.forget.password.modify.success"/>',
                    handler: backLogin
                });
                setTimeout(function () {
                    ymPrompt.doHandler('ok')
                }, 10000);
            }
        });
    }
    function backLogin() {
        window.location = "${ctx}/login";
    }
</script>
</html>
