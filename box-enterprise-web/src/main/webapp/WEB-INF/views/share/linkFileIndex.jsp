<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="../common/common.jsp" %>
    <%@ include file="../files/commonForLinkFiles.jsp" %>
    <script src="${ctx}/static/js/public/JQbox-hw-files.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/JQbox-hw-page.js" type="text/javascript"></script>
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
                    <div class="link-handle-btn">
                        <a id="download-button" class="link-download" onclick="downloadFile()"
                           title='<spring:message code="button.download" />'></a>
                        <a id="saveToMe-button" class="link-save" onclick="copyAndMove()"
                           title='<spring:message code="link.title.saveToMe" />'></a>
                        <a id="MyFavorite-button" class="link-favorite" onclick="addFavorite()"
                           title='<spring:message code="link.title.favorite" />'></a>
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
    var linkCode = '<c:out value="${linkCode}"/>';
    var isLoginUser = '<c:out value="${isLoginUser}"/>';
    var ownerId = '<c:out value="${ownerId}"/>';
    var fileId = '<c:out value="${folderId}"/>';
    var catalogData = null;
    var orderField = "modifiedAt";
    var isNeedVerify = '<c:out value="${isNeedVerify}"/>';
    var permissionFlag = null;
    var previewable = <c:out value="${iNodeData.previewable}"/>;

    $(function () {

        var iNodeName = '<c:out value="${iNodeName}"/>';
        var type = _getStandardType(iNodeName);
        permissionFlag = getNodePermission();
        if (type == "img") {
            var thumUrl = "url('${thumbnailUrl}') no-repeat center center";
            $("#fileOrFolderType").addClass("fileImg-" + type).css("background", thumUrl);
        } else {
            $("#fileOrFolderType").addClass("fileImg-" + type);
        }
        if (canPreview && permissionFlag != null && permissionFlag["preview"] == 1 && previewable) {
            $("#download-button").after('<a id = "download-button" class="link-preview" onclick="previewFile()" title=\'<spring:message code="button.preview" />\'></a>');
        }
        $("#linkCreateTime").text(getLocalTime(parseInt('${linkCreateTime}')));

        $("#iNodeSize").text(formatFileSize("<c:out value='${iNodeSize}'/>"));
        $("#download-button").hide();
        $("#saveToMe-button").hide();
        $("#MyFavorite-button").hide();
        loadSysSetting("${ctx}", '<spring:message code="common.language1" />');
        pageload();

        $(".link-handle-btn a").tooltip({
            container: "body",
            placement: "top",
            delay: {show: 100, hide: 0},
            animation: false
        });
    });

    function getNodeName() {
        return '<c:out value="${iNodeName}"/>';
    }

    function getthumbnailUrl() {
        return '<c:out value="${thumbnailUrl}"/>';
    }

    function pageload() {
        if (permissionFlag != null && permissionFlag["download"] == 1) {
            $("#download-button").show();
        }
        if (isLoginUser == "true") {
            $("#MyFavorite-button").show();
            if (permissionFlag != null && permissionFlag["download"] == 1) {
                $("#saveToMe-button").show();
            }
        }
    }

    function downloadFile() {
        $.ajax({
            type: "GET",
            async: false,
            url: "${ctx}/share/getDownloadUrl/" + fileId + "/" + linkCode + "?" + Math.random(),
            error: function (request) {
                if (request.status == 405) {
                    top.location.reload();
                } else {
                    doDownLoadLinkError(request);
                }
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
        $.ajax({
            type: "GET",
            async: false,
            url: "${ctx}/share/refreshShare" + "?" + Math.random(),
            error: function (request) {
                unLayerLoading();
                top.ymPrompt.close();
                handlePrompt("error", "<spring:message code='operation.failed'/>");
                return;
            },
            success: function (data) {
                if (typeof(data) == 'string' && data.indexOf('<html>') != -1) {
                    top.window.location.reload();
                    return;
                }
            }
        });
        var url = '${ctx}/nodes/copyAndMove/' + curUserId;
        top.ymPrompt.win({
            message: url,
            width: 600,
            height: 500,
            title: '<spring:message code="link.title.saveToMe" />',
            iframe: true,
            btn: [["<spring:message code='button.newFolder'/>", 'newFolder', false, "ymNewFolder"], ['<spring:message code="button.saveToMe" />', 'copy', false, "btn-focus"], ['<spring:message code="button.cancel" />', 'no', true, "btn-cancel"]],
            handler: doCopyAndMove
        });
        top.ymPrompt_addModalFocus("#btn-focus");
        $("#ymNewFolder").css({"margin-left": "15px", "float": "left"}).parent().css({"padding-right": "65px"});
    }

    function addFavorite() {
        var json = {
            "type": "link",
            "name": "<c:out value='${iNodeName}'/>",
            "nodeType": "<c:out value='${iNode.id}'/>",
            "linkCode": "<c:out value='${linkCode}'/>",
            "token": "<c:out value='${token}'/>"
        };

        $.ajax({
            type: "POST",
            url: "${ctx}/favorite/create",
            data: json,
            error: function (request) {
                top.ymPrompt.close();
                if (request.status == 404 || request.responseText == "InvalidParameter" || (request.responseText) == "NoSuchItem") {
                    top.handlePrompt("error",
                        "<spring:message code='link.view.Expired'/>");
                } else if ((request.responseText) == ("ExistFavoriteConflict") || (request.responseText) == "NoSuchFile") {
                    top.handlePrompt("error",
                        "<spring:message code='favorite.add.exist'/>");
                } else {
                    top
                        .handlePrompt("error",
                            "<spring:message code='operation.failed'/>");
                }
            },
            success: function (data) {
                if (typeof (data) == 'string'
                    && data.indexOf('<html>') != -1) {
                    window.location.href = "${ctx}/logout";
                    return;
                }
                top
                    .handlePrompt("success",
                        "<spring:message code='operation.success'/>");
            }
        });
    }


    function doCopyAndMove(tp) {
        var idArray = [fileId];
        if (tp == 'copy') {
            top.ymPrompt.getPage().contentWindow.submitCopyAndMove(tp, ownerId, idArray, linkCode);
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
                        locationUrL(1, ownerId, selectFolder, linkCode);
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
                        top.ymPrompt.close();
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
                            'linkCode': linkCode,
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
        window.open("${ctx}/files/gotoPreview/" + ownerId + "/" + fileId + "?parentPageType=link&linkCode=" + linkCode);
    }

    function getNodePermission() {
        var permission = null;
        var url = "${ctx}/share/nodePermission?" + Math.random();
        var params = {
            "ownerId": ownerId,
            "nodeId": fileId,
            "linkCode": linkCode
        };
        var flag = true;
        $.ajax({
            type: "GET",
            url: url,
            data: params,
            async: false,
            error: function (data) {
            },
            success: function (data) {
                permission = data;
            }
        });
        return permission;
    }
</script>
</body>
</html>