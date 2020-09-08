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
    <%@ include file="commonForFiles.jsp" %>
    <link href="${ctx}/static/zTree/zTreeStyle.css" rel="stylesheet" type="text/css"/>
    <script src="${ctx}/static/zTree/jquery.ztree.all-3.5.min.js" type="text/javascript"></script>
    <%@ include file="../common/messages.jsp" %>
    <%@ include file="commonForTreeCreateNode.jsp" %>
</head>
<body>
<div class="pop-content copyAndMove-con">
    <div class="file-name clearfix" id="linkFileName"><span class="file-icon" id="fileOrFolderType"></span> <span
            id="fileDesc"></span></div>
    <ul id="treeArea" class="ztree"></ul>
</div>
<script type="text/javascript">
    var curUserId = '<shiro:principal property="cloudUserId"/>';
    var startPoint = "<c:out value='${startPoint}'/>";
    var endPoint = "<c:out value='${endPoint}'/>";

    var setting = {
        async: {
            enable: true,
            url: "${ctx}/folders/listTreeNode/" + curUserId,
            otherParam: {"token": "<c:out value='${token}'/>"},
            autoParam: ["id", "ownedBy"]
        },
        view: {
            selectedMulti: false
        },
        edit: {
            drag: {
                isMove: false,
                isCopy: false
            },
            enable: true,
            showRemoveBtn: false,
            showRenameBtn: false
        },
        callback: {
            onClick: zTreeOnClick,
            beforeRename: zTreeBeforeRename,
            onRename: zTreeOnRename
        }
    };
    var zNodes = [{
        id: 0,
        name: "<spring:message code='file.index.allFiles'/>",
        ownedBy: curUserId,
        open: true,
        isParent: true
    }];

    $(document).ready(function () {
        var obj = null;
        var objType = null;
        var desc = "";

        if (top.getNodeName && top.getthumbnailUrl) {
            objName = top.getNodeName();
            desc = objName;

            var filetype = _getStandardType(objName);
            if (filetype == "img") {
                $.get("${ctx}/files/getThumbnailUrlSmall/" + top.ownerId + "/" + top.fileId, function (data) {
                    var thumUrl = "url('" + data + "') no-repeat center center";
                    $("#fileOrFolderType").css("background", thumUrl);
                });
            } else {
                $("#fileOrFolderType").addClass("fileImg-" + filetype);
            }
        }
        else {
            var dataList = top.$("#fileList").getGridSelectedData(top.catalogData, top.opts_viewGrid);
            if (dataList.length == 1) {
                obj = dataList[0];
                objType = obj.type;
                desc = obj.name;
            }

            if (objType == 0) {
                $("#fileOrFolderType").addClass("fileImg-folder");
            } else if (objType == 1) {
                var type = _getStandardType(obj.name);
                if (type == "img") {
                    $.get("${ctx}/files/getThumbnailUrlSmall/" + obj.ownedBy + "/" + obj.id, function (data) {
                        var thumUrl = "url('" + data + "') no-repeat center center";
                        $("#fileOrFolderType").css("background", thumUrl);
                    });
                } else {
                    $("#fileOrFolderType").addClass("fileImg-" + type);
                }
            }
            else {
                desc = "<spring:message code='copymove.total.select.info1'/>" + dataList.length +
                    "<spring:message code='copymove.total.select.info2'/>";
                $("#fileOrFolderType").addClass("fileImg-multi-files");

            }
        }
        document.getElementById("fileDesc").innerHTML = desc;
        $.fn.zTree.init($("#treeArea"), setting, zNodes);

        var treeObj = $.fn.zTree.getZTreeObj("treeArea");
        var nodes = treeObj.getNodes();
        if (nodes.length > 0) {
            treeObj.selectNode(nodes[0]);
        }

        $("#treeArea > li > span").click();

        $(document).keydown(function (event) {
            if (event.keyCode == 13) {
                if (window.event) {
                    window.event.cancelBubble = true;
                    window.event.returnValue = false;
                } else {
                    event.stopPropagation();
                    event.preventDefault();
                }
            }
        })

        stopDefaultScroll("treeArea");
    });

    function submitCopyAndMove(tp, ownerId, idArray, linkCode) {
        var treeObj = $.fn.zTree.getZTreeObj("treeArea");
        var nodes = treeObj.getSelectedNodes();

        var selectFolder = nodes[0].id;
        var ids = "";

        for (var i = 0; i < idArray.length; i++) {
            var id = idArray[i];

            if (i == 0) {
                ids = idArray[i];
            } else {
                ids = ids + ',' + idArray[i];
            }
        }
        var requestUrl = "${ctx}/nodes/copy/" + ownerId;
        if (tp == 'move') {
            requestUrl = "${ctx}/nodes/move/" + ownerId;
        }
        var params = {
            "destOwnerId": curUserId,
            "ids": ids,
            "parentId": selectFolder,
            "token": "<c:out value='${token}'/>",
            "startPoint": startPoint,
            "endPoint": endPoint
        };

        if (linkCode != "" && linkCode != undefined) {
            params = {
                "linkCode": linkCode,
                "destOwnerId": curUserId,
                "ids": ids,
                "parentId": selectFolder,
                "token": "<c:out value='${token}'/>",
                "startPoint": startPoint,
                "endPoint": endPoint
            };
        }
        $.ajax({
            type: "POST",
            url: requestUrl,
            data: params,
            error: function (request) {
                top.ymPrompt.close();
                var responseObj = $.parseJSON(request.responseText);
                switch (responseObj.code) {
                    case "NoSuchFile":
                    case "NoSuchFolder":
                    case "NoSuchParent":
                    case "NoSuchSource":
                    case "NoSuchDest":
                    case "NoSuchUser": //Teamspace does not exist
                        top.handlePrompt("error", "<spring:message code='error.notfound'/>");
                        break;
                    case "ExceedQuota":
                        top.handlePrompt("error", "<spring:message code='file.errorMsg.exceedSpaceQuota'/>");
                        break;
                    case "ExceedUserMaxNodeNum":
                        top.handlePrompt("error", "<spring:message code='file.errorMsg.exceedNodeNum'/>");
                        break;
                    case "SecurityMatrixForbidden":
                        top.handlePrompt("error", "<spring:message code='error.forbid'/>");
                        break;
                    case "ExceedUserAvailableSpace":
                        top.handlePrompt("error", "<spring:message code='exceeded.userAvailableSpace'/>");
                        break;
                    case "VirusForbidden":
                        top.handlePrompt("error", "<spring:message code='file.errorMsg.operationNotAllowedForVirusDetected'/>");
                        break;
                    case "ExceedEnterpriseAvailableSpace":
                        top.handlePrompt("error", "<spring:message code='exceeded.enterpriseAvailableSpace'/>");
                        break;
                    default:
                        top.handlePrompt("error", "<spring:message code='operation.failed'/>");
                        break;
                }
            },
            success: function (data) {
                if (typeof(data) == 'string' && data.indexOf('<html>') != -1) {
                    window.location.href = "${ctx}/logout";
                    return;
                }
                top.inLayerLoading("<spring:message code='common.task.doing'/>", "loading-bar");
                top.asyncListen(tp, ownerId, selectFolder, data);
            }
        });
    }

</script>
</body>
</html>
