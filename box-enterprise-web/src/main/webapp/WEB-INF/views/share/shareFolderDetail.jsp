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
</head>
<body>

<%@ include file="linkHeader.jsp" %>

<div class="body">
    <div class="body-con clearfix no-breadcrumb">

        <div class="link-page-view">
            <div class="file-info clearfix">
                <div class="pull-left">
                    <div class="file-icon" id="fileOrFolderType"></div>
                    <div class="box"></div>
                </div>
                <div class="pull-right">
                    <h3><c:out value="${iNodeName}"/></h3>
                    <p><spring:message code="link.label.shareUser"/>: <span id=""><c:out
                            value="${shareUserName}"/></span></p>
                    <p><spring:message code="link.label.shareTime"/>: <span id="linkCreateTime"></span></p>
                    <p><spring:message code="link.label.updatedTime"/>: <span id="linkUpdatedTime"></span></p>
                    <button type="button" id="open-button" class="btn btn-large btn-primary" onclick="openFolder()">
                        <spring:message code="button.open"/></button>
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
    var nodeType = <c:out value="${iNodeData.type}"/>;
    var catalogData = null;
    var orderField = "modifiedAt";

    $(function () {
        var iNodeName = '<c:out value="${iNodeName}"/>';
        if (nodeType == 0) {
            $("#fileOrFolderType").addClass("fileImg-folder");
        } else if (nodeType == -3) {
            $("#fileOrFolderType").addClass("fileImg-folder-computer");
        } else if (nodeType == -2) {
            $("#fileOrFolderType").addClass("fileImg-folder-disk");
        }
        $("#saveToMe-button").hide();
        $("#linkCreateTime").text(getLocalTime(parseInt('${linkCreateTime}')));
        $("#linkUpdatedTime").text(getLocalTime(parseInt('${linkUpdatedTime}')));
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
        if (isLoginUser == "true" && nodeType == 0) {
            $("#saveToMe-button").show();
        }
    }

    function openFolder() {
        window.location.href = "${ctx}/shared/list/" + ownerId + "/" + fileId;
    }

    function copyAndMove() {
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
</script>
</body>
</html>