<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=10"/>
    <meta http-equiv="X-UA-Compatible" content="IE=9"/>
    <meta http-equiv="X-UA-Compatible" content="IE=8"/>
    <META HTTP-EQUIV="Expires" CONTENT="0">
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <META HTTP-EQUIV="Cache-control"
          CONTENT="no-cache, no-store, must-revalidate">
    <META HTTP-EQUIV="Cache" CONTENT="no-cache">
    <title><spring:message code="user.settings.userTitle"/></title>
    <link href="${ctx}/static/skins/default/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/static/skins/default/css/public.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/static/skins/default/css/main.css" rel="stylesheet" type="text/css"/>

    <script src="${ctx}/static/js/public/jquery-1.10.2.min.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/bootstrap.min.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/common.js" type="text/javascript"></script>

</head>
<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);
%>
<body>
<div class="sys-content">
    <div class="form-horizontal form-con clearfix">
        <div class="form-left">
            <div class="control-group">
                <label class="control-label" for="input"><spring:message
                        code="common.field.username"/>: </label>
                <div class="controls">
						<span class="uneditable-input"
                              title="<c:out value='${user.loginName}'/>"><c:out
                                value='${user.loginName}'/></span>
                </div>
            </div>

            <div class="control-group">
                <label class="control-label" for="input"><spring:message
                        code="common.field.realname"/>: </label>
                <div class="controls">
						<span class="uneditable-input"
                              title="<c:out value='${user.name}'/>"><c:out
                                value='${user.name}'/></span>
                </div>
            </div>

            <div class="control-group">
                <label class="control-label" for="input"><spring:message
                        code="common.field.email"/>: </label>
                <div class="controls">
						<span id="emailSpan" class="uneditable-input"
                              title="<c:out value='${user.email}'/>"><c:out
                                value='${user.email}'/></span>
                </div>
                <span style="display: none"><input id="existEmail"
                                                   type="text" value="<c:out value='${user.email}'/>"></span>
            </div>

            <div class="control-group">
                <label class="control-label" for="input"><spring:message
                        code="common.field.mobile"/>: </label>
                <div class="controls">
						<span id="mobileSpan" class="uneditable-input"
                              title="<c:out value='${user.mobile}'/>"><c:out
                                value='${user.mobile}'/></span>
                </div>
                <span style="display: none"><input id="existMobile"
                                                   type="text" value="<c:out value='${user.mobile}'/>"></span>
            </div>

            <div class="control-group">
                <label class="control-label" for="input"><spring:message
                        code="group.field.label.description"/>: </label>
                <div class="controls">
						<span class="uneditable-input"
                              title="<c:out value='${user.department}'/>"><c:out
                                value='${user.department}'/></span>
                </div>
            </div>

            <c:if test="${isLocalAuth}">
                <div class="control-group">
                    <label class="control-label" for="input"></label>
                    <div class="controls">
                        <button type="button" class="btn" onClick="enterModifyPwdPage()">
                            <spring:message code="common.account.change.password"/>
                        </button>
                        <button type="button" class="btn"
                                onClick="enterModifyEmailPage()">
                            <spring:message code="common.account.change.mail"/>
                        </button>
                        <button type="button" class="btn"
                                onClick="enterModifyMobilePage()">
                            <spring:message code="common.account.change.mobile"/>
                        </button>
                    </div>
                </div>
            </c:if>

        </div>
        <div class="form-right">
            <div class="user-setting-logo">
                <c:if test="${!userImage}">
                    <img alt="logo"
                         src="${ctx}/static/skins/default/img/user-logo.png"/>
                </c:if>
                <c:if test="${userImage}">
                    <img alt="logo" src="${ctx}/userimage/getLogo" width="105"
                         height="105" style="width: 105px; height: 105px"/>
                </c:if>

            </div>
            <div class="user-setting-btn">
                <a href="javascript:enterModifyLogo()"><spring:message
                        code="common.user.image.upload"/></a>
            </div>
        </div>
    </div>

    <hr/>

    <div class="form-horizontal form-con clearfix">
        <div class="form-left">
            <div class="control-group">
                <label class="control-label" for="input"><spring:message code="user.createdAt"/>: </label>
                <div class="controls">
                    <span id="createdAt" class="uneditable-input"></span>
                </div>
            </div>

            <div class="control-group">
                <label class="control-label" for="input"><spring:message code="user.fileCount"/>: </label>
                <div class="controls">
                    <span class="uneditable-input"><c:out value='${user.fileCount}'/></span>
                </div>
            </div>

            <div class="control-group">
                <label class="control-label" for="input"><spring:message code='user.settings.maxVersions'/>: </label>
                <div class="controls">
                    <c:if test="${user.maxVersions==-1}">
                        <span class="uneditable-input"><spring:message code='user.settings.version.unlimit'/></span>
                    </c:if>
                    <c:if test="${user.maxVersions!=-1}">
                        <span class="uneditable-input"><c:out value='${user.maxVersions}'/></span>
                    </c:if>
                </div>
            </div>

        </div>
    </div>
</div>

<script type="text/javascript">
    $(document).ready(function () {
        var isSign = ${needDeclaration};
        if (isSign) {
            showDeclaration();
        }
        var createdAt = new Date(${user.createdAt.time});
        $("#createdAt").html(getSmpFormatDate(createdAt));

        <%-- auto adjust page height --%>
        var pageH = $("body").outerHeight() + 200;
        top.iframeAdaptHeight(pageH);

        $("span").tooltip({container: "body", placement: "bottom", delay: {show: 400, hide: 0}, animation: false});
    });

    function enterModifyLogo() {
        top.ymPrompt.win({
            message: '${ctx}/userimage/goChangeLogo',
            width: 450,
            height: 250,
            title: '<spring:message code="user.image.upload"/>',
            iframe: true,
            btn: [['<spring:message code="button.upload"/>', 'yes', false, "btnModifyLogo"], ['<spring:message code="teamSpace.button.btnCancel"/>', 'no', true, "btnModifyCancel"]],
            handler: doSubmitModifyLogo
        });
        top.ymPrompt_addModalFocus("#btnModifyLogo");
    }

    function doSubmitModifyLogo(tp) {
        if (tp == 'yes') {
            top.ymPrompt.getPage().contentWindow.submitModifyLogo();
        } else {
            top.ymPrompt.close();
        }
    }

    function enterModifyPwdPage() {
        top.ymPrompt.win({
            message: '${ctx}/user/goChangePwd',
            width: 550,
            height: 300,
            title: '<spring:message code="common.account.change.password"/>',
            iframe: true,
            btn: [['<spring:message code="teamSpace.button.edit"/>', 'yes', false, "btnModifyPwd"], ['<spring:message code="teamSpace.button.btnCancel"/>', 'no', true, "btnModifyCancel"]],
            handler: doSubmitModifyPwd
        });
        top.ymPrompt_addModalFocus("#btnModifyPwd");
    }

    function doSubmitModifyPwd(tp) {
        if (tp == 'yes') {
            top.ymPrompt.getPage().contentWindow.submitModifyPwd();
        } else {
            top.ymPrompt.close();
        }
    }

    function enterModifyEmailPage() {
        top.ymPrompt.win({
            message: '${ctx}/user/goChangeEmail',
            width: 550,
            height: 250,
            title: '<spring:message code="common.account.change.mail"/>',
            iframe: true,
            btn: [['<spring:message code="teamSpace.button.edit"/>', 'yes', false, "btnModifyEmail"], ['<spring:message code="teamSpace.button.btnCancel"/>', 'no', true, "btnModifyCancel"]],
            handler: doSubmitModifyEmail
        });
        top.ymPrompt_addModalFocus("#btnModifyEmail");
    }

    function doSubmitModifyEmail(tp) {
        if (tp == 'yes') {
            top.ymPrompt.getPage().contentWindow.submitModifyEmail();
        } else {
            top.ymPrompt.close();
        }
    }
    function doChangeEmail(message) {
        $("#existEmail").html(message);
        $("#emailSpan").text(message);
        $("#emailSpan").attr("data-original-title", message);
    }

    function enterModifyMobilePage() {
        top.ymPrompt.win({
            message: '${ctx}/user/goChangeMobile',
            width: 550,
            height: 250,
            title: '<spring:message code="common.account.change.mobile"/>',
            iframe: true,
            btn: [['<spring:message code="teamSpace.button.edit"/>', 'yes', false, "btnModifyMobile"], ['<spring:message code="teamSpace.button.btnCancel"/>', 'no', true, "btnModifyCancel"]],
            handler: doSubmitModifyMobile
        });
        top.ymPrompt_addModalFocus("#btnModifyMobile");
    }

    function doSubmitModifyMobile(tp) {
        if (tp == 'yes') {
            top.ymPrompt.getPage().contentWindow.submitModifyMobile();
        } else {
            top.ymPrompt.close();
        }
    }

    function doChangeMobile(message) {
        $("#existMobile").html(message);
        $("#mobileSpan").text(message);
        $("#mobileSpan").attr("data-original-title", message);
    }


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

    function doSignDeclaration(tp) {
        if (tp == 'yes') {
            top.ymPrompt.getPage().contentWindow.signDeclaration();
        } else {
            top.ymPrompt.close();
            window.location.href = "${ctx}/logout";
        }
    }


</script>
</body>
</html>
