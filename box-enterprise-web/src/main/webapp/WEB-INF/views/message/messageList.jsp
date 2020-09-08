<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%
    request.setAttribute("token", CSRFTokenManager.getTokenForSession(session));
%>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="../common/common.jsp" %>
    <%@ include file="../files/commonForFiles.jsp" %>
    <link href="${ctx}/static/skins/default/css/layout.css" rel="stylesheet" type="text/css"/>
    <script src="${ctx}/static/js/public/JQbox-hw-grid.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/JQbox-hw-page.js" type="text/javascript"></script>
</head>
<body>
<div id="pageLoadingContainer"
     style="z-index:10000; position:fixed; left:0; top:0; right:0; bottom:0; background:#fff;"></div>
<script type="text/javascript">new Spinner(optsBigSpinner).spin(document.getElementById("pageLoadingContainer"));</script>
<%@ include file="../common/header.jsp" %>

<div class="breadcrumb">
    <div class="breadcrumb-con clearfix">
        <ul id="breadcrumbCon">
            <li class="active"><span><spring:message code="message.title"/></span></li>
        </ul>
    </div>
</div>
<div class="body">
    <div class="body-con clearfix">
        <div class="callBoard-con">
            <div id="callBoard"></div>
        </div>
        <div id="messageList"></div>
        <div id="messageListPageBox"></div>
    </div>
</div>
<%@ include file="../common/footer.jsp" %>
<script type="text/javascript">

    var receiverId = <shiro:principal property="cloudUserId"/>;

    var opts_CallBoardGrid = null;
    var opts_MessageGrid = null;
    var opts_page = null;

    var currentPage = 1;

    var fileName = null;
    var fileId = null;
    var ownerId = null;
    var isNull = true;

    var headDataCallBoard = {
        "type": {"width": "10%"},
        "content": {"width": "auto"},
        "handle": {"width": "17%", "cls": "ar"},
        "createdAt": {"width": "23%"}
    };

    var headDataMessage = {
        "type": {"width": "10%"},
        "content": {"width": "auto"},
        "handle": {"width": "17%", "cls": "ar"},
        "createdAt": {"width": "18%"},
        "delete": {"width": "5%"}
    };

    $(document).ready(function () {
        loadSysSetting("${ctx}", '<spring:message code="common.language1" />');

        init();
        listMessage(currentPage);
    });


    function init() {
        opts_CallBoardGrid = $("#callBoard").comboTableGrid({
            headData: headDataCallBoard,
            dataNullTip: "",
            border: false,
            hideHeader: true,
            dataId: "id"
        });
        opts_MessageGrid = $("#messageList").comboTableGrid({
            headData: headDataMessage,
            border: false,
            dataNullTip: '',
            hideHeader: true,
            dataId: "id"
        });

        opts_page = $("#messageListPageBox").comboPage({
            lang: '<spring:message code="common.language1"/>'
        });

        $.fn.comboPage.pageSkip = function (opts, _idMap, curPage) {
            currentPage = curPage;
            listMessage(curPage);
        };

        $.fn.comboTableGrid.setItemOp = function (tableData, rowData, tdItem, colIndex) {
            switch (colIndex) {
                case "type":
                    try {
                        var iconCls = "icon-mess-tips";
                        if (rowData.type == "system") {
                            iconCls = "icon-mess-notice";
                        }

                        var itemVal = getMessageTypeDesc(rowData.type);
                        if (rowData.params.primaryNodeType && rowData.params.primaryNodeType == 5) {
                            itemVal = '<spring:message code="message.category.system" />';
                        }
                        if (rowData.status == "unread") {
                            tdItem.find("p").html('<i class="' + iconCls + '"></i>【' + itemVal + '】');
                        } else {
                            tdItem.find("p").html('<i class="' + iconCls + ' icon-darkgray" ></i>【' + itemVal + '】');
                        }
                        tdItem.attr("title", itemVal);

                    } catch (e) {
                    }
                    break;
                case "content":
                    try {
                        tdItem.find("p").html(rowData.content);
                        tdItem.attr("title", tdItem.find("p").text());
                    } catch (e) {
                    }
                    break;
                case "handle":
                    try {
                        var handlerHtml = generalHandlers(rowData);
                        tdItem.find("p").append(handlerHtml);

                        tdItem.find("#shareHandler" + rowData.id).click(function () {
                            var params = rowData.params;
                            saveToMe(rowData.providerId, params.nodeId, params.nodeName);
                        })

                        tdItem.find("#newFileHandler" + rowData.id).click(function () {
                            var params = rowData.params;
                            saveToMe(params.teamSpaceId, params.nodeId, params.nodeName);
                        })
                    } catch (e) {
                    }
                    break;
                case "createdAt":
                    try {
                        tdItem.find("p").text(getLocalTime(rowData.createdAt));
                        tdItem.attr("title", getLocalTime(rowData.createdAt));
                    } catch (e) {
                    }
                    break;
                case "delete":
                    try {
                        tdItem.find("p").html("<button type='button' id='msgDelete" + rowData.id + "' class='btn btn-link' onclick='deleteMessage(" + rowData.id + ")'><i class='icon-delete-alt icon-gray'></i></button>");
                    } catch (e) {
                    }
                    break;
                default :
                    break;
            }
        };
    }


    function listMessage(curPage) {
        var url = "${ctx}/message/listUserMessage";
        var params = {
            "pageNumber": curPage,
            "pageSize": 20,
            "token": "${token}"
        };
        $.ajax({
            type: "POST",
            url: url,
            data: params,
            error: function (request) {
                if (request.status == 404) {
                    handlePrompt("error", "<spring:message code='error.notfound'/>");
                } else {
                    handlePrompt("error", "<spring:message code='message.errorMsg.listMsgFailed'/>");
                }
                comboxRemoveLoading("pageLoadingContainer");

            },
            success: function (data) {
                if (typeof(data) == 'string' && data.indexOf('<html>') != -1) {
                    window.location.href = "${ctx}/logout";
                    return;
                }

                if (data.content.length == 0 && curPage != 1) {
                    curPage--;
                    listMessage(curPage);
                    return;
                }

                $("#messageList").setTableGridData(data.content, opts_MessageGrid);
                $("#messageListPageBox").setPageData(opts_page, data.number, data.size, data.totalElements);
                comboxRemoveLoading("pageLoadingContainer");

                isNull = data.content.length == 0 ? true : false;

                listSystemMessage();
            }
        });


    }

    function listSystemMessage() {
        url = "${ctx}/message/listSystemMessage";
        $.ajax({
            type: "POST",
            data: {token: "<c:out value='${token}'/>"},
            url: url,
            error: function (request) {
                if (request.status == 404) {
                    handlePrompt("error", "<spring:message code='error.notfound'/>");
                } else {
                    handlePrompt("error", "<spring:message code='message.errorMsg.listMsgFailed'/>");
                }
                comboxRemoveLoading("pageLoadingContainer");

            },
            success: function (data) {
                if (typeof(data) == 'string' && data.indexOf('<html>') != -1) {
                    window.location.href = "${ctx}/logout";
                    return;
                }

                if (isNull) {
                    isNull = data.length == 0 ? true : false
                }
                if (isNull) {
                    $("#messageList_mainTb").append('<tr><td class="ac" colspan="5"><spring:message code="message.null.tips" /></td></tr>');
                }

                if (data.length == 0 && currentPage != 1) {
                    return;
                }
                $("#callBoard").setTableGridData(data, opts_CallBoardGrid);
            }
        });
    }

    function deleteMessage(messageId) {
        $("#msgDelete" + messageId).attr("disabled", "true");

        var url = "${ctx}/message/" + messageId;
        $.ajax({
            type: "POST",
            data: {token: "<c:out value='${token}'/>"},
            url: url,
            error: function (request) {
                if (request.status == 404) {
                    handlePrompt("error", "<spring:message code='error.notfound'/>");
                } else {
                    comboxRemoveLoading("pageLoadingContainer");
                    handlePrompt("error", "<spring:message code='operation.failed'/>");
                }

            },
            success: function (data) {
                if (typeof(data) == 'string' && data.indexOf('<html>') != -1) {
                    window.location.href = "${ctx}/logout";
                    return;
                }
                listMessage(currentPage);

                showUnreadMessageCount();
            }
        });
    }

    function getMessageTypeDesc(type) {
        switch (type) {
            case "share":
            case "deleteShare":
                return '<spring:message code="message.category.share" />';
            case "teamspaceUpload":
            case "teamspaceAddMember":
            case "teamspaceDeleteMember":
            case "leaveTeamspace":
                return '<spring:message code="message.category.team" />';
            case "groupAddMember":
            case "groupDeleteMember":
            case "leaveGroup":
                return '<spring:message code="message.category.group" />';
            case "teamspaceRoleUpdate":
            case "groupRoleUpdate":
                return '<spring:message code="message.category.authority" />';
            case "system":
                return '<spring:message code="message.category.announcement" />';
            default:
                return '<spring:message code="message.category.system" />';
        }
    }

    function generalHandlers(message) {
        var params = message.params;
        var handleHtml;
        switch (message.type) {
            case "share":
                if (params.nodeType == "folder" || params.nodeType == "disk" || params.nodeType == "computer") {
                    if (!params.primaryNodeType || params.primaryNodeType != 5) {
                        handleHtml = "<a class='btn btn-link' onclick='listFolder(" + message.providerId + "," + params.nodeId + ")'><spring:message code='message.operation.view' /></a>";
                    }
                } else {
                    handleHtml = "<a class='btn btn-link' onclick='downloadFile(" + message.providerId + "," + params.nodeId + ")'><spring:message code='message.operation.download' /></a>" +
                        "&nbsp;&nbsp;<a class='btn btn-link' id='shareHandler" + message.id + "'><spring:message code='message.operation.save' /></a>";
                }
                break;
            case "teamspaceUpload":
                handleHtml = "<a class='btn btn-link' onclick='downloadFile(" + params.teamSpaceId + "," + params.nodeId + ")'><spring:message code='message.operation.download' /></a>" +
                    "&nbsp;&nbsp;<a class='btn btn-link' id='newFileHandler" + message.id + "'><spring:message code='message.operation.save' /></a>";
                break;
            case "teamspaceAddMember":
                handleHtml = "<a class='btn btn-link' onclick='enterTeamspace(" + params.teamSpaceId + ")'><spring:message code='message.operation.view' /></a>";
                break;
            case "system":
                handleHtml = "<a class='btn btn-link' onclick='announcementList(" + params.announcementId + ")'><spring:message code='message.operation.view' /></a>";
                break;
            default:
                break;
        }
        return handleHtml;
    }

    function listFolder(providerId, nodeId) {
        window.location = "${ctx}/shared/list/" + providerId + "/" + nodeId;
    }

    function downloadFile(providerId, nodeId) {
        $.ajax({
            type: "GET",
            async: false,
            url: "${ctx}/files/getDownloadUrl/" + providerId + "/" + nodeId + "?" + Math.random(),
            error: function (request) {
                downloadFileErrorHandler(request);
            },
            success: function (data) {
                jQuery('<form action="' + data + '" method="get"></form>').appendTo('body').submit().remove();
            }
        });
    }

    function saveToMe(providerId, nodeId, nodeName) {
        ownerId = providerId;
        fileName = nodeName;
        fileId = nodeId;
        var url = "${ctx}/nodes/copyAndMove/" + receiverId;
        top.ymPrompt.win({
            message: url,
            width: 600,
            height: 500,
            title: '<spring:message code="button.saveToMe"/>',
            iframe: true,
            btn: [["<spring:message code='button.newFolder'/>", 'newFolder', false, "ymNewFolder"], ['<spring:message code="button.save"/>', 'copy', false, "btn-focus"], ['<spring:message code="button.cancel"/>', 'no', true, "btn-cancel"]],
            handler: doSaveToMe
        });
        top.ymPrompt_addModalFocus("#btn-focus");
        $("#ymNewFolder").css({"margin-left": "15px", "float": "left"}).parent().css({"padding-right": "65px"});
    }

    function doSaveToMe(tp) {
        if (tp == 'copy') {
            var idArray = new Array();
            idArray[0] = fileId;
            top.ymPrompt.getPage().contentWindow.submitCopyAndMove(tp, ownerId, idArray);
        } else if (tp == 'newFolder') {
            top.ymPrompt.getPage().contentWindow.createFolder();
        } else {
            top.ymPrompt.close();
        }
    }

    function enterTeamspace(teamspaceId) {
        window.location = "${ctx}/teamspace/file/" + teamspaceId;
    }

    function announcementList(announcementId) {
        var url = "${ctx}/announcement/enter/" + announcementId;
        jQuery('<form action="' + url + '" method="get" target="_blank"></form>').appendTo('body').submit().remove();
    }

    function getNodeName() {
        return fileName;
    }

    function getthumbnailUrl() {
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
            message: '<spring:message code="link.task.renameFail"/>', handler: function (tp) {
                if (tp == 'ok') {
                    $.ajax({
                        type: "POST",
                        url: "${ctx}/nodes/renameCopy/" + srcOwnerId,
                        data: {
                            'destOwnerId': receiverId,
                            'ids': conflictIds,
                            'parentId': selectFolder,
                            'token': "<c:out value='${token}'/>"
                        },
                        error: function (request) {
                            handlePrompt("error", '<spring:message code="link.task.fail"/>');
                        },
                        success: function () {
                            handlePrompt("success", '<spring:message code="link.task.success"/>');
                        }
                    });
                }
            }
        });
    }

</script>

</body>
</html>