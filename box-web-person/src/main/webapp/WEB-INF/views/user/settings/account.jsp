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
<%--<div class="sys-content">--%>
    <%--<div class="form-horizontal form-con clearfix">--%>
        <%--<div class="form-left">--%>
            <%--<div class="control-group">--%>
                <%--<label class="control-label"><spring:message code="common.field.username"/>: </label>--%>
                <%--<div class="controls">--%>
                    <%--<span class="uneditable-input" title="<c:out value='${user.loginName}'/>"><c:out value='${user.loginName}'/></span>--%>
                <%--</div>--%>
            <%--</div>--%>

            <%--<div class="control-group">--%>
                <%--<label class="control-label"><spring:message code="common.field.realname"/>: </label>--%>
                <%--<div class="controls">--%>
                    <%--<span class="uneditable-input" title="<c:out value='${user.name}'/>"><c:out value='${user.name}'/></span>--%>
                <%--</div>--%>
            <%--</div>--%>

            <%--<div class="control-group">--%>
                <%--<label class="control-label" for="existEmail"><spring:message code="common.field.email"/>: </label>--%>
                <%--<div class="controls">--%>
                    <%--<span class="uneditable-input" title="<c:out value='${user.email}'/>"><c:out value='${user.email}'/></span>--%>
                <%--</div>--%>
                <%--<span style="display: none"><input id="existEmail" type="text" value="<c:out value='${user.email}'/>"></span>--%>
            <%--</div>--%>

            <%--<div class="control-group">--%>
                <%--<label class="control-label"><spring:message code="group.field.label.description"/>: </label>--%>
                <%--<div class="controls">--%>
                    <%--<span class="uneditable-input" title="<c:out value='${user.department}'/>"><c:out value='${user.department}'/></span>--%>
                <%--</div>--%>
            <%--</div>--%>

            <%--<c:if test="${isLocalAuth}">--%>
                <%--<div class="control-group">--%>
                    <%--<label class="control-label"></label>--%>
                    <%--<div class="controls">--%>
                        <%--<button type="button" class="btn" onClick="enterModifyPwdPage()"><spring:message code="common.account.change.password"/></button>--%>
                        <%--<button type="button" class="btn" onClick="enterModifyEmailPage()"><spring:message code="common.account.change.mail"/></button>--%>
                    <%--</div>--%>
                <%--</div>--%>
            <%--</c:if>--%>

        <%--</div>--%>
        <%--<div class="form-right">--%>
            <%--<div class="user-setting-logo">--%>

                <%--<div class="container" id="crop-avatar" style="width: 110px;">--%>

                    <%--<!-- Current avatar -->--%>
                    <%--<div class="avatar-view" title="修改头像">--%>
                        <%--<c:if test="${!userImage}">--%>
                            <%--<img id="crop-avatar-image" style=" width: 105px; height: 105px;border-radius: 0px" alt="logo" src="${ctx}/static/skins/default/img/user-logo.png"/>--%>
                        <%--</c:if>--%>
                        <%--<c:if test="${userImage}">--%>
                            <%--<img id="crop-avatar-image" style="border-radius: 0px" alt="logo" src="${ctx}/userimage/getLogo" width="105" height="105" style="width: 105px;height: 105px"/>--%>
                        <%--</c:if>--%>
                    <%--</div>--%>

                    <%--<!-- Cropping modal -->--%>
                    <%--<div class="modal fade" id="avatar-modal" aria-hidden="true" aria-labelledby="avatar-modal-label" role="dialog" tabindex="-1" style="width: 830px; z-index:9999;display: none;">--%>
                        <%--<div class="modal-dialog modal-lg">--%>
                            <%--<div class="modal-content">--%>
                                <%--<form class="avatar-form" action="${ctx}/userimage/changeLogo" enctype="multipart/form-data" method="post">--%>
                                    <%--<div class="modal-header">--%>
                                        <%--<button type="button" class="close" data-dismiss="modal">&times;</button>--%>
                                        <%--<h4 class="modal-title" id="avatar-modal-label">上传头像</h4>--%>
                                    <%--</div>--%>
                                    <%--<div class="modal-body" style="max-height: 550px;">--%>
                                        <%--<div class="avatar-body">--%>

                                            <%--<!-- Upload image and data -->--%>
                                            <%--<div class="avatar-upload" hidden>--%>
                                                <%--<input type="hidden" class="avatar-src" name="avatar_src">--%>
                                                <%--<input type="hidden" class="avatar-data" name="avatar_data">--%>
                                                <%--<input type="file" class="avatar-input" name="photoFile" id="photoFile" style="margin-left: 0px;">--%>
                                            <%--</div>--%>

                                            <%--<!-- Crop and preview -->--%>
                                            <%--<div class="row" style="margin-left: 0px">--%>
                                                <%--<div class="col-md-6" style="width: 550px;float: left;">--%>
                                                    <%--<div class="avatar-wrapper">--%>
                                                        <%--<div id="selectImage" style="width:200px;height:50px;margin:0 auto;background:#ea5036;margin-top:157px;border-radius: 5px;">--%>
                                                            <%--<b style="color:#fff;font-size:20px;line-height:50px">请选择图片</b>--%>
                                                        <%--</div>--%>
                                                    <%--</div>--%>
                                                <%--</div>--%>
                                                <%--<div class="col-md-3" style="width: 200px;float: left;margin-left: 10px">--%>
                                                    <%--<div class="avatar-preview preview-lg"></div>--%>
                                                    <%--<!--    <div class="avatar-preview preview-md"></div>--%>
                                                       <%--<div class="avatar-preview preview-sm"></div> -->--%>
                                                <%--</div>--%>
                                            <%--</div>--%>

                                            <%--<div class="row avatar-btns" style="margin-left: 0px">--%>
                                                <%--<div class="col-md-9" style="width: 550px;float: left;">--%>
                                                    <%--<div class="btn-group">--%>
                                                        <%--<button type="button" class="btn btn-primary" data-method="rotate" data-option="-90" title="Rotate -90 degrees">向左旋转</button>--%>
                                                    <%--</div>--%>
                                                    <%--<div class="btn-group">--%>
                                                        <%--<button type="button" class="btn btn-primary" data-method="rotate" data-option="90" title="Rotate 90 degrees">向右旋转</button>--%>
                                                    <%--</div>--%>
                                                <%--</div>--%>
                                                <%--<div class="col-md-3" style="width: 200px;float: left;margin-left: 10px">--%>
                                                    <%--<button type="submit" class="btn btn-primary btn-block avatar-save">上传</button>--%>
                                                <%--</div>--%>
                                            <%--</div>--%>
                                        <%--</div>--%>
                                    <%--</div>--%>
                                    <%--<!-- <div class="modal-footer">--%>
                                      <%--<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>--%>
                                    <%--</div> -->--%>
                                <%--</form>--%>
                            <%--</div>--%>
                        <%--</div>--%>
                    <%--</div><!-- /.modal -->--%>

                    <%--<!-- Loading state -->--%>
                    <%--<div class="loading" aria-label="Loading" role="img" tabindex="-1"></div>--%>
                <%--</div>--%>
            <%--</div>--%>
            <%--<div class="user-setting-btn">--%>
                <%--<a href="javascript:enterModifyLogo()"><spring:message code="common.user.image.upload"/></a>--%>
            <%--</div>--%>
        <%--</div>--%>
    <%--</div>--%>

    <%--<hr/>--%>

    <%--<div class="form-horizontal form-con clearfix">--%>
        <%--<div class="form-left">--%>
            <%--<div class="control-group">--%>
                <%--<label class="control-label"><spring:message code="user.createdAt"/>: </label>--%>
                <%--<div class="controls">--%>
                    <%--<span id="createdAt" class="uneditable-input"></span>--%>
                <%--</div>--%>
            <%--</div>--%>

            <%--<div class="control-group">--%>
                <%--<label class="control-label"><spring:message code="user.fileCount"/>: </label>--%>
                <%--<div class="controls">--%>
                    <%--<span class="uneditable-input"><c:out value='${user.fileCount}'/></span>--%>
                <%--</div>--%>
            <%--</div>--%>

            <%--<div class="control-group">--%>
                <%--<label class="control-label"><spring:message code='user.settings.maxVersions'/>: </label>--%>
                <%--<div class="controls">--%>
                    <%--<c:if test="${user.maxVersions==-1}">--%>
                        <%--<span class="uneditable-input"><spring:message code='user.settings.version.unlimit'/></span>--%>
                    <%--</c:if>--%>
                    <%--<c:if test="${user.maxVersions!=-1}">--%>
                        <%--<span class="uneditable-input"><c:out value='${user.maxVersions}'/></span>--%>
                    <%--</c:if>--%>
                <%--</div>--%>
            <%--</div>--%>

        <%--</div>--%>
    <%--</div>--%>

    <%--<div class="form-horizontal form-con clearfix">--%>
        <%--<div class="form-left">--%>
            <%--<c:if test="${!empty bindUrl}">--%>
                <%--<div class="control-group">--%>
                    <%--<label class="control-label" style="line-height:28px;">您还没有绑定微信账户: </label>--%>
                    <%--<div class="controls">--%>
                        <%--<a href="${bindUrl}" target="_blank"><img src="${ctx}/static/skins/default/img/bind-wx-account.png"/></a>--%>
                    <%--</div>--%>
                <%--</div>--%>
            <%--</c:if>--%>
            <%--<c:if test="${empty bindUrl}">--%>
                <%--<div class="control-group">--%>
                    <%--<label class="control-label">微信账户: </label>--%>
                    <%--<div class="controls">--%>
                        <%--<span class="uneditable-input">已绑定</span>--%>
                    <%--</div>--%>
                <%--</div>--%>
            <%--</c:if>--%>
        <%--</div>--%>
    <%--</div>--%>
<%--</div>--%>


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
