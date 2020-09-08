<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="../common/common.jsp" %>
    <script src="${ctx}/static/js/public/JQbox-hw-files.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/JQbox-hw-page.js" type="text/javascript"></script>
    <link href="${ctx}/static/skins/default/css/float-webdoc.css" rel="stylesheet" type="text/css"/>
</head>
<body>

<%@ include file="linkHeader.jsp" %>

<div class="body">
    <div class="body-con clearfix no-breadcrumb body-con-no-menu">

        <div class="link-page-view">
            <div class="file-info clearfix">
                <div class="pull-left">
                    <div class="file-icon" id="fileOrFolderType"></div>
                    <div class="box"></div>
                </div>
                <div class="pull-right">
                    <h3><c:out value='${iNodeName}'/></h3>
                    <p><spring:message code="common.field.size"/>: <span id="iNodeSize"></span></p>
                    <p><spring:message code="link.label.shareUser"/>: <span id=""><c:out
                            value='${shareUserName}'/></span></p>
                    <p><spring:message code="link.label.shareTime"/>: <span id="linkCreateTime"></span></p>
                    <p><spring:message code="link.label.updatedTime"/>: <span id="linkUpdatedTime"></span></p>
                    <div>
                        <button type="button" id="download-button" class="btn btn-large btn-primary"
                                onclick="downloadFile()"><spring:message code="button.download"/></button>
                        <button type="button" id="saveToMe-button" class="btn btn-large" onclick="copyAndMove()">
                            <spring:message code="link.title.saveToMe"/></button>
                    </div>
                </div>
            </div>
        </div>

        <div id="fileList"></div>

    </div>
</div>

<%@ include file="../common/footer.jsp" %>

<script type="text/javascript">
    var canPreview =<%=PreviewUtils.isPreview()%>;

    ymPrompt.setDefaultCfg({
        closeTxt: '<spring:message code="button.close"/>',
        okTxt: '<spring:message code="button.ok"/>',
        cancelTxt: '<spring:message code="button.cancel"/>',
        title: '<spring:message code="common.tip"/>'
    });
    var curUserId = '<shiro:principal property="cloudUserId"/>';
    var isLoginUser = '<c:out value="${isLoginUser}"/>';
    var ownerId = '<c:out value="${ownerId}"/>';
    var fileId = '<c:out value="${folderId}"/>';
    var catalogData = null;
    var orderField = "modifiedAt";
    var previewable = <c:out value="${iNodeData.previewable}"/>;

    $(function () {

        var iNodeName = '<c:out value="${iNodeName}"/>';
        var type = _getStandardType(iNodeName);
        if (type == "img") {
            var thumUrl = "url('${thumbnailUrl}') no-repeat center center";
            $("#fileOrFolderType").addClass("fileImg-" + type).css("background", thumUrl);
        } else {
            $("#fileOrFolderType").addClass("fileImg-" + type);
        }
        if (canPreview && previewable) {
            $("#download-button").after('<button type="button" id = "download-button" class="btn btn-large" onclick="previewFile()"><spring:message code="button.preview" /></button>');
        }
        $("#linkCreateTime").text(getLocalTime(parseInt('${linkCreateTime}')));
        $("#linkUpdatedTime").text(getLocalTime(parseInt('${linkUpdatedTime}')));
        $("#iNodeSize").text(formatFileSize("<c:out value='${iNodeSize}'/>"));
        $("#download-button").hide();
        $("#saveToMe-button").hide();
        loadSysSetting("${ctx}", '<spring:message code="common.language1" />');
        pageload();
    });

    function getNodeName() {
        return '<c:out value="${iNodeName}"/>';
    }

    function getthumbnailUrl() {
        return '${thumbnailUrl}';
    }

    function pageload() {
        $("#download-button").show();
        if (isLoginUser == "true") {
            $("#saveToMe-button").show();
        }
    }

    function doDownLoadLinkError(request) {
        var responseObj = $.parseJSON(request.responseText);
        switch (responseObj.code) {
            case "BadRequest":
                handlePrompt("error", '<spring:message code="link.view.BadRquest"/>');
                break;
            case "NoSuchItem":
            case "NoSuchLink":
                handlePrompt("error", '<spring:message code="error.notfound"/>');
                break;
            case "Forbidden":
            case "Unauthorized":
                handlePrompt("error", '<spring:message code="link.view.Forbidden"/>');
                break;
            case "LinkNotEffective":
                handlePrompt("error", '<spring:message code="link.view.NotEffective"/>');
                break;
            case "LinkExpired":
                handlePrompt("error", '<spring:message code="link.view.Expired"/>');
                break;
            case "FileScanning":
                handlePrompt("error", '<spring:message code='file.errorMsg.fileNotReady'/>');
                break;
            case "ScannedForbidden":
                handlePrompt("error", '<spring:message code='file.errorMsg.downloadNotAllowed'/>');
                break;
            case "SecurityMatrixForbidden":
                handlePrompt("error", "<spring:message code='error.forbid'/>");
                break;
            default:
                handlePrompt("error", '<spring:message code="link.view.NoSuchItems"/>');
                break;
        }
    }

    function downloadFile() {
        $.ajax({
            type: "GET",
            async: false,
            url: "${ctx}/files/getDownloadUrl/" + ownerId + "/" + fileId + "?" + Math.random(),
            error: function (request) {
                doDownLoadLinkError(request);
            },
            success: function (data) {
                if (typeof(data) == 'string' && data.indexOf('<html>') != -1) {
                    top.window.location.reload();
                    return;
                }
                jQuery('<form action="' + data + '" method="get"></form>').appendTo('body').submit().remove();
            }
        });
    }

    function copyAndMove() {
        var url = "${ctx}/nodes/copyAndMove/" + curUserId;
        top.ymPrompt.win({
            message: url,
            width: 600,
            height: 500,
            title: '<spring:message code="button.saveToMe"/>',
            iframe: true,
            btn: [["<spring:message code='button.newFolder'/>", 'newFolder', false, "ymNewFolder"], ['<spring:message code="button.save"/>', 'copy', false, "btn-focus"], ['<spring:message code="button.cancel"/>', 'no', true, "btn-cancel"]],
            handler: doCopyAndMove
        });
        top.ymPrompt_addModalFocus("#btn-focus");
        $("#ymNewFolder").css({"margin-left": "15px", "float": "left"}).parent().css({"padding-right": "65px"});
    }

    function doCopyAndMove(tp) {
        var idArray = [fileId];
        if (tp == 'copy') {
            top.ymPrompt.getPage().contentWindow.submitCopyAndMove(tp, ownerId, idArray);
        } else if (tp == 'newFolder') {
            top.ymPrompt.getPage().contentWindow.createFolder();
        } else {
            top.ymPrompt.close();
        }
    }

    function asyncListen(type, srcOwnerId, selectFolder, taskId) {
        $.ajax({
            type: "GET",
            url: "${ctx}/nodes/listen?taskId=" + taskId + "&" + new Date().toString(),
            error: function (XMLHttpRequest) {
                if (XMLHttpRequest.status == 409) {
                    top.ymPrompt.close();
                    unLayerLoading();
                    conflictCopyAndMove(type, srcOwnerId, XMLHttpRequest.responseText, selectFolder);
                } else {
                    unLayerLoading();
                    top.ymPrompt.close();
                    handlePrompt("error", "<spring:message code='operation.failed'/>");
                }
            },
            success: function (data, textStatus, jqXHR) {
                switch (data) {
                    case "NotFound":
                        top.ymPrompt.close();
                        unLayerLoading();
                        handlePrompt("success", "<spring:message code='operation.success'/>");
                        break;
                    case "Doing":
                        setTimeout(function () {
                            asyncListen(type, srcOwnerId, selectFolder, taskId);
                        }, 1500);
                        break;
                    case "SubFolderConflict":
                        unLayerLoading();
                        ymPrompt.getPage().contentWindow.handlePrompt("error", "<spring:message code='file.errorMsg.canNotCopyToChild'/>", '', 10);
                    case "SameParentConflict":
                        unLayerLoading();
                        ymPrompt.getPage().contentWindow.handlePrompt("error", "<spring:message code='file.errorMsg.parentNotChange'/>");
                        break;
                    case "SameNodeConflict":
                        unLayerLoading();
                        ymPrompt.getPage().contentWindow.handlePrompt("error", "<spring:message code='file.errorMsg.sameFolder'/>");
                        break;
                    case "Forbidden":
                        unLayerLoading();
                        handlePrompt("error", "<spring:message code='error.forbid'/>");
                        break;
                    case "NoSuchSource":
                    case "NoSuchDest":
                        unLayerLoading();
                        top.ymPrompt.close();
                        handlePrompt("error", "<spring:message code='error.notfound'/>");
                        if (viewMode == "file") {
                            listFile(currentPage, catalogParentId);
                        } else {
                            doSearch();
                        }
                        break;
                    default:
                        unLayerLoading();
                        top.ymPrompt.close();
                        handlePrompt("error", "<spring:message code='operation.failed'/>");
                        break;
                }
            }
        });
    }

    function conflictCopyAndMove(type, srcOwnerId, conflictIds, selectFolder) {
        ymPrompt.confirmInfo({
            message: '<spring:message code="link.task.renameFail" />', handler: function (tp) {
                if (tp == 'ok') {
                    var url = "${ctx}/nodes/renameCopy/" + srcOwnerId;
                    $.ajax({
                        type: "POST",
                        url: url,
                        data: {
                            'destOwnerId': curUserId,
                            'ids': conflictIds,
                            'parentId': selectFolder,
                            'token': "<c:out value='${token}'/>"
                        },
                        error: function (request) {
                            handlePrompt("error", '<spring:message code="link.task.fail" />');
                        },
                        success: function (data) {
                            inLayerLoading('<spring:message code="common.task.doing" />');
                            asyncListen(type, srcOwnerId, selectFolder, data);
                        }
                    });
                }
            }
        });
    }

    function listFile(curPage, parentId) {
    }

    function previewFile() {
        window.open("${ctx}/files/gotoPreview/" + ownerId + "/" + fileId + "?parentPageType=receiveShare");
    }
</script>
</body>
</html>