<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html>

<head>
    <meta http-equiv="Access-Control-Allow-Origin" content="*">
    <%@ include file="../../common/include.jsp" %>
    <title>我外发的文件</title>
    <link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/main.css"/>
    <link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/share/linkListlndex.css"/>
    <link rel="stylesheet" href="${ctx}/static/skins/default/css/user/account.css">
    <script src="${ctx}/static/components/Components.js"></script>
    <script src="${ctx}/static/components/Menubar.js"></script>
    <script src="${ctx}/static/components/Pagination.js"></script>
</head>
<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);
%>
<body>
<%@ include file="../../common/menubar.jsp" %>


<div class="user-all-content">
    <div class="user-setting-content">
        <div class="user-header">
            <p>用户信息</p>
        </div>
        <div class="user-setting-contents">
            <div class="user-detail-info-left">
                <form action="">
                    <div class="user-login-name user-common-style">
                        <p>
                            <span><spring:message code="common.field.username"/>:</span>
                            <span title="<c:out value='${user.loginName}'/>"><c:out value='${user.loginName}'/></span>
                        </p>
                    </div>
                    <div class="user-common-style">
                        <p>
                            <span><spring:message code="common.field.realname"/>:</span>
                            <span title="<c:out value='${user.name}'/>"><c:out value='${user.name}'/></span>
                        </p>
                    </div>
                    <div class="user-common-style">
                        <p>
                            <span><spring:message code="common.field.email"/>:</span>
                            <span title="<c:out value='${user.email}'/>"><c:out value='${user.email}'/></span>
                        </p>
                    </div>
                    <div class="user-common-style">
                        <p>
                            <span><spring:message code="group.field.label.description"/>:</span>
                            <span title="<c:out value='${user.department}'/>"><c:out value='${user.department}'/></span>
                        </p>
                    </div>
                    <div class="user-common-style">
                        <p>
                            <span><spring:message code="user.createdAt"/>:</span>
                            <span id="createdAt"></span>
                        </p>
                    </div>
                    <div class="user-common-style">
                        <p>
                            <span><spring:message code="user.fileCount"/>:</span>
                            <span><c:out value='${user.fileCount}'/></span>
                        </p>
                    </div>
                    <div class="user-common-style">
                        <c:if test="${user.maxVersions==-1}">
                            <p>
                                <span>文件版本数:</span>
                                <span><spring:message code='user.settings.version.unlimit'/></span>
                            </p>
                        </c:if>
                        <c:if test="${user.maxVersions!=-1}">
                            <p>
                                <span>文件版本数:</span>
                                <span><c:out value='${user.maxVersions}'/></span>
                            </p>
                        </c:if>
                    </div>
                    <div class="user-common-style">
                        <c:if test="${!empty bindUrl}">
                            <p>
                                <span>微信账号:</span>
                                <span>您还没有绑定微信账户,</span>
                                <span><a class="bind-wx" href="${bindUrl}">立即绑定</a></span>
                            </p>
                        </c:if>
                        <c:if test="${empty bindUrl}">
                            <p>
                                <span>微信账号:</span>
                                <span>已绑定</span>
                            </p>
                        </c:if>
                    </div>
                </form>
            </div>
            <div class="user-detail-info-right">
                <c:if test="${!userImage}">
                    <i><img src="${ctx}/static/skins/default/img/user-logo.png" alt=""></i>
                </c:if>
                <c:if test="${userImage}">
                    <i><img src="${ctx}/userimage/getLogo" alt=""></i>
                </c:if>

            </div>
        </div>

    </div>
</div>

<script type="text/javascript">
    $(document).ready(function () {
        <%--var isSign = ${needDeclaration};--%>
        <%--if(isSign){--%>
            <%--showDeclaration();--%>
        <%--}--%>

        var createdAt = new Date(${user.createdAt.time});
        $("#createdAt").html(getSmpFormatDate(createdAt));


        $("span").tooltip({container: "body", placement: "bottom", delay: {show: 400, hide: 0}, animation: false});
        /*
            $('#crop-avatar').buildCropAvatar();
        */

        $("#selectImage").click(function () {
            $("#photoFile").click();
        });
    });

    function enterModifyLogo() {

        $('#crop-avatar-image').click();
        <%--top.ymPrompt.win({message:'${ctx}/userimage/goChangeLogo',width:450,height:250,title:'<spring:message code="user.image.upload"/>', iframe:true,btn:[['<spring:message code="button.upload"/>','yes',false,"btnModifyLogo"],['<spring:message code="teamSpace.button.btnCancel"/>','no',true,"btnModifyCancel"]],handler:doSubmitModifyLogo});--%>
        <%--top.ymPrompt_addModalFocus("#btnModifyLogo");--%>
    }

    function doSubmitModifyLogo(tp) {
        if (tp == 'yes') {
            top.ymPrompt.getPage().contentWindow.submitModifyLogo();
        } else {
            top.ymPrompt.close();
        }
    }

    function enterModifyPwdPage() {
        top.ymPrompt.win({message: '${ctx}/user/goChangePwd', width: 550, height: 300, title: '<spring:message code="common.account.change.password"/>', iframe: true, btn: [['<spring:message code="teamSpace.button.edit"/>', 'yes', false, "btnModifyPwd"], ['<spring:message code="teamSpace.button.btnCancel"/>', 'no', true, "btnModifyCancel"]], handler: doSubmitModifyPwd});
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
        top.ymPrompt.win({message: '${ctx}/user/goChangeEmail', width: 550, height: 250, title: '<spring:message code="common.account.change.mail"/>', iframe: true, btn: [['<spring:message code="teamSpace.button.edit"/>', 'yes', false, "btnModifyEmail"], ['<spring:message code="teamSpace.button.btnCancel"/>', 'no', true, "btnModifyCancel"]], handler: doSubmitModifyEmail});
        top.ymPrompt_addModalFocus("#btnModifyEmail");
    }

    function doSubmitModifyEmail(tp) {
        if (tp == 'yes') {
            top.ymPrompt.getPage().contentWindow.submitModifyEmail();
        } else {
            top.ymPrompt.close();
        }
    }

    function showDeclaration() {
        top.ymPrompt.win({message: '${ctx}/syscommon/declaration', width: 600, height: 400, title: '<spring:message code="declaration.sign.title"/>', iframe: true, btn: [['<spring:message code="agree.declaration"/>', 'yes', false, "btnSignDeclaration"], ['<spring:message code="disagree.declaration"/>', 'no', true, "btnSignDeclarationCancel"]], handler: doSignDeclaration});
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
