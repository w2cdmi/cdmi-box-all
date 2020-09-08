<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="pw.cdmi.box.disk.utils.CSRFTokenManager" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%
    request.setAttribute("token", CSRFTokenManager.getTokenForSession(session));
%>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="./common.jsp" %>
</head>
<body>
<div class="header">
    <div class="header-con">
        <div class="logo pull-left"><a href="#" id="logoBackgroudId"><img
                src="${ctx}/static/skins/default/img/logo.png"/><span><spring:message code="main.title"/></span></a>
        </div>
        <div class="header-R pull-right clearfix">
            <ul class="clearfix pull-right">
                <li class="pull-left dropdown">
                    <a class="dropdown-toggle" href="#" id="nav-account" data-toggle="dropdown"><strong><shiro:principal
                            property="name"/></strong> <i class="icon-caret-down"></i></a>
                    <ul class="dropdown-menu">
                        <li><a href="${ctx}/logout?token=<c:out value='${token}'/>"><i class="icon-signout"></i>
                            <spring:message code="user.settings.logout"/></a></li>
                    </ul>
                </li>
            </ul>
        </div>
    </div>
</div>

<div class="body">
    <div class="sys-content body-con">
        <div class="form-horizontal form-con  clearfix">
            <form class="form-horizontal" id="modifyPwdForm">
                <div class="alert alert-error input-medium controls" id="errorTip" style="display:none">
                    <button class="close" data-dismiss="alert">×</button>
                    <spring:message code="common.oper.fail"/>
                </div>
                <h4><spring:message code="common.account.change.password"/></h4>
                <div class="control-group">
                    <label class="control-label" for="oldPassword"><em>*</em><spring:message
                            code="common.account.password.old"/>:</label>
                    <div class="controls">
                        <input class="span4" type="password" id="oldPassword" name="oldPassword" value=""
                               autocomplete="off"/>
                        <span class="validate-con"><div></div></span>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="password"><em>*</em><spring:message
                            code="common.account.password.new"/>:</label>
                    <div class="controls">
                        <input class="span4" type="password" id="password" name="password" value="" autocomplete="off"/>
                        <span class="validate-con"><div></div></span>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="confirmPassword"><em>*</em><spring:message
                            code="common.account.password.confirm"/>:</label>
                    <div class="controls">
                        <input class="span4" type="password" id="confirmPassword" name="confirmPassword" value=""
                               autocomplete="off"/>
                        <span class="validate-con"><div></div></span>
                    </div>
                </div>

                <div class="control-group">
                    <label class="control-label" for="email"><em>*</em><spring:message
                            code="initPwd.lable.email"/>:</label>
                    <div class="controls">
                        <input class="span4" type="text" id="email" name="email" value="<c:out value='${email}'/>"/>
                        <span class="validate-con"><div></div></span>
                    </div>
                </div>
                <div class="control-group">
                    <div class="controls">
                        <button id="chgPassword_btn" type="button" class="btn btn-primary" onclick="submitModify()">
                            <spring:message code="common.OK"/></button>
                    </div>
                </div>
                <div class="control-group">
                    <span class="validate-con"><div id="showPwdLevel"></div></span>
                </div>
                <input type="hidden" id="token" name="token" value="<c:out value='${token}'/>"/>
            </form>
        </div>
    </div>
</div>

<div class="footer">
    <div class="footer-con">
        <p><span class="logo-small" id="copyRightId"><spring:message code="corpright"/></span></p>
    </div>
</div>
</body>
<script type="text/javascript">
    jQuery.extend(jQuery.validator.messages, {
        required: "<spring:message code='common.validate.notempty'/>",
        equalTo: "<spring:message code='common.validate.equalTo'/>"
    });

    jQuery.extend(jQuery.validator.defaults, {
        ignore: "",
        errorElement: "span",
        wrapper: "span",
        errorPlacement: function (error, element) {
            error.appendTo(element.next().find(" > div"));
        },
        onkeyup: false,
        focusCleanup: true,
        onfocusout: function (element) {
            $(element).valid()
        }
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

        var flag = ${needDeclaration};
        var PwdLevelDiv = '${pwdLevel}';
        if (PwdLevelDiv > 0) {
            $("#showPwdLevel").css("color", "red");
            $("#showPwdLevel").css("width", 500);
            showPwdLevelDiv(PwdLevelDiv);
        }

        if (flag) {
            showDeclaration();
        }
        $("#modifyPwdForm").validate({
            rules: {
                oldPassword: {
                    required: true,
                    isValidOldPwd: false
                },
                password: {
                    required: true,
                    isValidPwd: true
                },
                confirmPassword: {
                    required: true,
                    equalTo: "#password"
                },
                email: {
                    required: true,
                    maxlength: [255],
                    isValidEmail: true
                }
            }
        });

    });


    function showDeclaration() {
        top.ymPrompt.win({
            message: '${ctx}/syscommon/declaration',
            width: 600,
            height: 400,
            title: '<spring:message code="declaration.sign.title"/>',
            iframe: true,
            btn: [['<spring:message code="agree.declaration"/>', 'yes', false, "btnSignDeclaration"], ['<spring:message code="disagree.declaration"/>', 'no', true, "btnSignDeclarationCancel"]],
            handler: doSignDeclaration
        });
        top.ymPrompt_addModalFocus("#btnSignDeclaration");
    }
    function showPwdLevelDiv(val) {
        if (val == "1") {
            $("#showPwdLevel").html("<spring:message code='common.account.password.rule1'/>");
        } else if (val == "2") {
            $("#showPwdLevel").html("<spring:message code='common.account.password.rule2'/>");
        } else {
            $("#showPwdLevel").html("<spring:message code='common.account.password.rule3'/>");
        }
    }
    function doSignDeclaration(tp) {
        if (tp == 'yes') {
            top.ymPrompt.getPage().contentWindow.signDeclaration();
        } else {
            top.ymPrompt.close();
            window.location.href = "${ctx}/logout";
        }
    }

    function submitModify() {
        if (!$("#modifyPwdForm").valid()) {
            return false;
        }
        $.ajax({
            type: "POST",
            url: "${ctx}/user/firstLoginInit",
            data: $('#modifyPwdForm').serialize(),
            error: function (request) {
                switch (request.responseText) {
                    case "PasswordInvalidException":
                        handlePrompt("error", '<spring:message code="common.account.password.rule"/>');
                        $("#oldPassword").val("");
                        $("#password").val("");
                        $("#confirmPassword").val("");
                        break;
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

                    case "EmailChangeConflictException":
                        handlePrompt("error", "<spring:message code='common.account.email.conflict'/>");
                        $("#oldPassword").val("");
                        $("#password").val("");
                        $("#confirmPassword").val("");
                        $("#email").val("");
                        break;
                    default:
                        handlePrompt("error", "<spring:message code='common.oper.fail'/>");
                        $("#oldPassword").val("");
                        $("#password").val("");
                        $("#confirmPassword").val("");
                        break;
                }
            },
            success: function () {
                top.handlePrompt("success", '<spring:message code="common.oper.success"/>');
                window.location = "${ctx}/";
            }
        });
    }
    $.validator.addMethod("isValidEmail", function (element) {
        var reg_em = /^(\w)+(\.\w+)*@(\w)+((\.\w+)+)$/;
        var value = $("#email").val();
        if (!reg_em.test(value)) {
            return false;
        }
        ;
        return true;
    }, '<spring:message code="请输入正确的电子邮件"/>');

</script>
</html>
