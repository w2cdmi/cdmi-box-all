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
    <link href="${ctx}/static/zTree/zTreeStyle.css" rel="stylesheet" type="text/css"/>
    <script src="${ctx}/static/js/public/JQbox-hw-page.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/JQbox-hw-grid.js" type="text/javascript"></script>
    <script src="${ctx}/static/zTree/jquery.ztree.all-3.5.min.js" type="text/javascript"></script>
    <%@ include file="../common/messages.jsp" %>
    <%@ include file="../files/commonForTreeCreateNode.jsp" %>
</head>

<body>
<input type="hidden" id="page"/>
<div class="pop-content">
    <div class="pop-copyto-teamspace">
        <div class="file-name clearfix" id="linkFileName"><span class="file-icon" id="fileOrFolderType"></span> <span
                class="name-txt" id="fileDesc"></span></div>
        <div class="select-tip" id="selectTIP"><spring:message code="teamSpace.label.selectSpace"/>:</div>
        <div class="teamspace-list" id="spaceTotalMessage">
            <div id="spaceList"></div>
            <div id="myPage"></div>
        </div>
        <div id="spaceInfo">
            <div class="tree-con">
                <ul id="treeArea" class="ztree"></ul>
            </div>
            <div id="manageBtnCon" class="btn-con">
                <button id="ymNewFolder" type="button" class="btn pull-left" onclick="checkCreateFolder()">
                    <spring:message code='button.newFolder'/></button>
                <button id="copyButton" type="button" class="btn btn-primary"><spring:message
                        code="button.ok"/></button>
                <button type="button" class="btn" onclick="top.ymPrompt.close()"><spring:message
                        code="button.cancel"/></button>
                <button id="goReturn" type="button" class="btn" onclick="returnForTurn()"><spring:message
                        code="button.return"/></button>
            </div>
        </div>
    </div>
</div>

</body>
<script type="text/javascript">
    var curUserId = '<shiro:principal property="cloudUserId"/>';
    var opts_viewGrid = null;
    var opts_page = null;
    var catalogData = null;
    var myPage = null;
    var startPoint = "<c:out value='${startPoint}'/>";
    var endPoint = "<c:out value='${endPoint}'/>";

    var headData = {
        "name": {"title": '<spring:message code="teamSpace.label.name"/>', "width": "30%"},
        "description": {"title": '<spring:message code="teamSpace.label.description"/>', "width": "40%"},
        "ownedByUserName": {"title": '<spring:message code="teamSpace.label.owner"/>', "width": ""}
    };

    $(function ($) {
        $("#spaceInfo").hide();
        initData();
        myPage = 1;
        initSpaceList(myPage);
        opts_page = $("#myPage").comboPage({lang: '<spring:message code="common.language1"/>'});
        $.fn.comboPage.pageSkip = function (opts, _idMap, curPage) {
            myPage = curPage;
            initSpaceList(curPage);
        };
    });

    function returnForTurn() {
        $("#teaArea").text("");
        $("#spaceInfo").hide();
        $("#selectTIP").html('<spring:message code="teamSpace.label.selectSpace"/>' + ":");
        $("#spaceTotalMessage").show();
    }
    function checkCreateFolder() {
        var treeObj = $.fn.zTree.getZTreeObj("treeArea");
        var parentNodes = treeObj.getSelectedNodes();
        var treeNode = parentNodes[0];
        var nodePermission = getNodePermission(treeNode.ownedBy, treeNode.id);
        if (!nodePermission || nodePermission["edit"] != 1) {
            handlePrompt("error", "<spring:message code='error.forbid'/>");
            return;
        }
        createFolder();
    }
    function initData() {
        var obj = null;
        var objType = null;
        var desc = "";
        <%-- multi--%>
        var dataList = top.$("#fileList").getGridSelectedData(top.catalogData, top.opts_viewGrid);
        if (dataList.length == 1) {
            obj = dataList[0];
            objType = obj.type;
            desc = obj.name;
        }

        <%-- folder --%>
        if (objType == 0) {
            $("#fileOrFolderType").addClass("fileImg-folder");
        } else
            <%-- file --%>
        if (objType == 1) {
            var type = _getStandardType(obj.name);
            $("#fileOrFolderType").addClass("fileImg-" + type);
        }
            <%-- multi --%>
        else {
            desc = "<spring:message code='copymove.total.select.info1'/>" + dataList.length +
                "<spring:message code='copymove.total.select.info2'/>";
            $("#fileOrFolderType").addClass("fileImg-multi-files");
        }

        $("#fileDesc").html(desc);
        $("#fileDesc").attr("title", $("#fileDesc").text());

        opts_viewGrid = $("#spaceList").comboTableGrid({
            headData: headData,
            border: false,
            splitRow: false,
            miniPadding: true,
            stripe: true,
            dataId: "id",
            ItemOp: "user-defined",
            height: 265
        });

        $.fn.comboTableGrid.setItemOp = function (tableData, rowData, tdItem, colIndex) {
            switch (colIndex) {
                case "name":
                    try {
                        var spaceName = tdItem.find("p").text();
                        var alink = "<a href='javascript:void(0)' onclick='teamDetailTree(" + rowData.id + ")' id='" + rowData.id + "' name='" + rowData.id + "'></a>";
                        tdItem.find("p").html(alink);
                        $("#" + rowData.id).text(spaceName);
                    } catch (e) {

                    }
                    break;
                default :
                    break;
            }
        };
    }

    function initSpaceList(curPage) {
        var url = "${ctx}/teamspace/listAll";
        var params = {
            "pageParam": curPage,
            "orderField": "teamRole",
            "desc": "ASC",
            "token": "<c:out value='${token}'/>"
        };
        $.ajax({
            type: "POST",
            url: url,
            data: params,
            error: function (request) {
                handlePrompt("error", '<spring:message code="teamSpace.ListIsNull"/>', '', '5');
            },
            success: function (data) {
                catalogData = data.content;
                $("#spaceList").setTableGridData(catalogData, opts_viewGrid);
                $("#myPage").setPageData(opts_page, data.number, data.size, data.totalElements);
            }
        });
    }

    function teamDetailTree(teamId) {
        goToSpaceDetail(teamId);
        var setting = {
            async: {
                enable: true,
                url: "${ctx}/folders/listTreeNode/" + teamId,
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
        var zNodes = [{id: 0, name: $("a[name='" + teamId + "']").text(), ownedBy: teamId, open: true, isParent: true}];

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
    }

    function goToSpaceDetail(teamId) {
        $("#spaceTotalMessage").hide();
        $("#spaceInfo").show();

        $("#selectTIP").html('<spring:message code="teamSpace.label.selectFolder"/>' + ":");

        $("#copyButton").unbind('click');
        $("#copyButton").bind('click', function () {
            var tp = "copy";
            var idArray = top.$("#fileList").getGridSelectedId();
            var destOwnerId = teamId;
            var treeObj = $.fn.zTree.getZTreeObj("treeArea");
            var nodes = treeObj.getSelectedNodes();
            var selectFolder = nodes[0].id;
            var ids = "";
            var linkCode = "";

            for (var i = 0; i < idArray.length; i++) {
                var id = idArray[i];
                if (destOwnerId == curUserId && id == selectFolder) {
                    handlePrompt("error", "<spring:message code='file.errorMsg.sameFolder'/>", "", 10);
                    return;
                }
                if (i == 0) {
                    ids = idArray[i];
                } else {
                    ids = ids + ',' + idArray[i];
                }
            }

            var requestUrl = "${ctx}/nodes/copy/" + curUserId;
            var params = {
                "destOwnerId": destOwnerId,
                "ids": ids,
                "parentId": selectFolder,
                "token": "<c:out value='${token}'/>",
                "startPoint": startPoint,
                "endPoint": endPoint
            };

            if (linkCode != "" && linkCode != undefined) {
                params = {
                    "linkCode": linkCode,
                    "destOwnerId": destOwnerId,
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
                    if (request.status == 404) {
                        top.handlePrompt("error", "<spring:message code='error.notfound'/>");
                    } else if (responseObj.code == "ExceedQuota") {
                        top.handlePrompt("error", "<spring:message code='file.errorMsg.exceedSpaceQuota'/>");
                    } else if (responseObj.code == "SecurityMatrixForbidden") {
                        top.handlePrompt("error", "<spring:message code='error.forbid'/>");
                    } else if (responseObj.code == "VirusForbidden") {
                        top.handlePrompt("error", "<spring:message code='file.errorMsg.operationNotAllowedForVirusDetected'/>");
                    } else {
                        top.handlePrompt("error", "<spring:message code='operation.failed'/>");
                    }
                },
                success: function (data) {
                    if (typeof(data) == 'string' && data.indexOf('<html>') != -1) {
                        window.location.href = "${ctx}/logout";
                        return;
                    }
                    top.inLayerLoading("<spring:message code='common.task.doing'/>", "loading-bar");
                    top.asyncListenForSpace(tp, curUserId, destOwnerId, selectFolder, data);
                }
            });
        });
    }

    function getNodePermission(ownerId, parentId) {
        var permission = null;
        var url = "${ctx}/teamspace/file/nodePermission?" + Math.random();
        var params = {
            "ownerId": ownerId,
            "nodeId": parentId,
            "token": "<c:out value='${token}'/>"
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
                if (typeof(data) == 'string' && data.indexOf('<html>') != -1) {
                    window.location.href = "${ctx}/logout";
                    return;
                }
                permission = data;
            }
        });
        return permission;
    }

</script>
</html>
