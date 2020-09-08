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
    <link href="${ctx}/static/zTree/zTreeStyle.css" rel="stylesheet" type="text/css"/>
    <script src="${ctx}/static/zTree/jquery.ztree.core-3.5.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/JQbox-hw-files.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/JQbox-hw-page.js" type="text/javascript"></script>
</head>

<body>
<div id="pageLoadingContainer"
     style="z-index:10000; position:fixed; left:0; top:0; right:0; bottom:0; background:#fff;"></div>
<script type="text/javascript">new Spinner(optsBigSpinner).spin(document.getElementById("pageLoadingContainer"));</script>
<%@ include file="../common/header.jsp" %>

<div class="breadcrumb">
    <div class="breadcrumb-con clearfix">
        <div class="pull-left folderTreeCon" style="display: none;">
            <input type="hidden" id="teamName" name="teamName" value="<c:out value='${teamName}'/>"/>
            <button id="folderTree" class="btn" type="button" title="<spring:message code='file.tips.folder'/>"
                    onclick="initTeamSpaceTree()"><i class="icon-sitemap"></i></button>
            <div id="treeArea" class="ztree"></div>
        </div>
        <ul id="breadcrumbCon">
        </ul>
    </div>
</div>

<div class="body">
    <div class="body-con clearfix">
        <div class="pull-left clearfix">
            <div class="public-bar-con">
                <div class="public-layout">
                    <div class="public-bar clearfix" id="publicBar">
                        <div class="sort-type pull-right">
                            <div class="dropdown" id="taxisDropDown">
                                <a class="btn btn-small btn-link" href="#"><spring:message code='common.label.sort'/>:
                                    <span id="sortOrderName"></span><i id="sortArray"></i></a>
                                <ul class="dropdown-menu pull-right" id="sortUl">
                                    <li><a href="javascript:sortByDate();" id="dateSortLink" isDesc=""><spring:message
                                            code='common.field.date'/></a></li>
                                    <li><a href="javascript:sortByFileName();" id="nameSortLink"
                                           isDesc=""><spring:message code='common.field.name'/></a></li>
                                    <li><a href="javascript:sortBySize();" id="sizeSortLink" isDesc=""><spring:message
                                            code='common.field.size'/></a></li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="files-block clearfix">
                <div id="fileList"></div>
                <div id="fileListNull" class="trash-null"><spring:message code='file.tips.emptyRecycler'/></a></div>
                <div id="fileListPageBox"></div>
            </div>

        </div>
    </div>
</div>
<%@ include file="../common/footer.jsp" %>

<script type="text/javascript">

    var teamId = "<c:out value='${teamId}'/>";
    var memberId = "<c:out value='${memberId}'/>";
    var opts_viewGrid = null;
    var opts_page = null;
    var listViewType = "list";
    var currentPage = 1;
    var catalogData = null;
    var viewMode = "recycler";

    var orderField = getCookie("orderField");
    orderField = orderField == null ? "modifiedAt" : orderField;

    var isDesc = getCookie("isDesc");
    isDesc = isDesc == null ? "true" : isDesc;

    var headData = {
        "name": {"width": "25%"},
        "path": {"width": "32.5%"},
        "modifiedByName": {"width": "15%"},
        "size": {"width": "10%"},
        "modifiedAt": {"width": "17.5%"}
    };
    var M = {
        m_cleanAll: {title: "<spring:message code='button.cleanAll'/>", className: "icon-trash-clear"},
        m_restoreAll: {title: "<spring:message code='button.restoreAll'/>", className: "icon-undo"},
        m_delete: {title: "<spring:message code='button.delete'/>", className: "icon-cancel"},
        m_restore: {title: "<spring:message code='button.restore'/>", className: "icon-undo"}
    };

    $(function () {
        loadSysSetting("${ctx}", '<spring:message code="common.language1" />');

        navMenuSelected("navTeamSpace");

        userCustomInit();

        init();

        window.onload = processHash;
        window.onhashchange = processHash;
    });

    var processHash = function () {
        var m = window.location.hash.split("#");
        m = m[1];
        if (m) {
            m = m.split("/");
            currentPage = m[0];
        }
        listRecycler(currentPage);
    }

    function changeHash(curPage) {
        location.hash = "#" + curPage;
    }

    function init() {

        initViewType(listViewType);

        opts_viewGrid = $("#fileList").comboGrid({
            headData: headData,
            dataId: "id",
            dragHandler: false,
            keyDelete: true,
            viewType: listViewType
        });

        opts_page = $("#fileListPageBox").comboPage({
            showPageSet: true,
            lang: '<spring:message code="common.language1"/>'
        });

        $.fn.comboPage.pageSkip = function (opts, _idMap, curPage) {
            changeHash(curPage);
        };

        $.fn.comboPage.setPerPageNum = function (opts, _idMap, num) {
            setRootCookie("fileListPerPageNum", num);
            listRecycler(currentPage);
        };

        $.fn.comboGrid.setItemOp = function (rowData, colTag, colIndex) {
            switch (colIndex) {
                case 0:
                    var itemVal = colTag.find("span").text();
                    colTag.find("span").attr("title", itemVal);
                    break;
                case 1:
                    var itemVal = colTag.find("span").text();
                    colTag.find("span").attr("title", itemVal);
                    break;
                case 2:
                    colTag.find("span").text("<spring:message code='teamSpace.label.modifiedByName'/> " + rowData.modifiedByName);
                    break;
                case 3:
                    if (rowData.type == "0") {
                        colTag.find("span").text("");
                    } else {
                        colTag.find("span").text(formatFileSize(rowData.size));
                    }
                    break;
                case 4:
                    colTag.find("span").text(getLocalTime(rowData.modifiedAt));
                    break;
                default :
                    break;
            }
        };

        $.fn.comboGrid.selectOp = function (allSelectData) {

            $.fn.comboGrid.initGridMenu = function () {
                var menuData = [];
                if (allSelectData.length != 0) {
                    menuData = {"m_delete": M.m_delete, "m_restore": M.m_restore};
                }
                return menuData;
            };

            initListButton(allSelectData, false);


        };

        $.fn.comboGrid.keyDelete = function () {
            deleteSelected();
        };

        $.fn.gridMenuItemOp = function (btnType) {
            executeHandler(btnType);
        };

    }

    function listRecycler(curPage) {
        hideAllBtns();

        var pagePerDis = getCookie("fileListPerPageNum");
        pagePerDis = (pagePerDis == null || pagePerDis == '' || pagePerDis == undefined) ? 40 : pagePerDis;

        var url = "${ctx}/trash/list";

        var params = {
            "ownerId": teamId,
            "pageSize": pagePerDis,
            "pageNumber": curPage,
            "orderField": orderField,
            "desc": isDesc,
            "token": "<c:out value='${token}'/>"
        };

        $.ajax({
            type: "POST",
            url: url,
            data: params,
            error: function (request) {
                if (request.status == 403) {
                    handlePrompt("error", "<spring:message code='error.forbid'/>");
                } else {
                    handlePrompt("error", "<spring:message code='file.errorMsg.listFileFailed'/>");
                }
            },
            success: function (data) {
                if (typeof(data) == 'string' && data.indexOf('<html>') != -1) {
                    window.location.href = "${ctx}/logout";
                    return;
                }
                catalogData = data.content;

                if (catalogData.length == 0 && curPage != 1) {
                    curPage--;
                    listRecycler(curPage);
                    changeHash(curPage);
                    return;
                }

                $("#fileList").setGridData(catalogData, opts_viewGrid);
                $("#fileListPageBox").setPageData(opts_page, data.number, data.size, data.totalElements);

                initListButton(data.content, true);

                if (catalogData.length == 0) {
                    $("#fileListNull").show();
                }
                comboxRemoveLoading("pageLoadingContainer");
            }
        });

        createBreadcrumbForTeamSpace("-1", teamId);
    }

    function initViewType(type) {
        if (type == "list") {
            $("#viewTypeBtnList").addClass("active");
        } else {
            $("#viewTypeBtnThumbnail").addClass("active");
        }
    }

    function initListButton(data, isList) {
        var listButtonData = null;

        if (isList) {
            if (data.length == 0) {
                $("#listHandler").remove();
            } else {
                listButtonData = {"m_cleanAll": M.m_cleanAll, "m_restoreAll": M.m_restoreAll};
            }
        } else {
            if (data.length == 0) {
                listButtonData = {"m_cleanAll": M.m_cleanAll, "m_restoreAll": M.m_restoreAll};
            } else {
                listButtonData = {
                    "m_cleanAll": M.m_cleanAll,
                    "m_restoreAll": M.m_restoreAll,
                    "m_delete": M.m_delete,
                    "m_restore": M.m_restore
                };
            }

        }


        var listBtnDiv = $('<div id="listHandler" class="list-handler pull-left"></div>');
        if ($("#listHandler").get(0)) {
            $("#listHandler").remove();
        }
        if (listButtonData == null) {
            return;
        }
        $("#publicBar").append(listBtnDiv);
        for (var key in listButtonData) {
            var menuItem = $('<button onclick="executeHandler(\'' + key + '\')" class="btn" type="button" id="listBtn_' + key + '"><i class="' + listButtonData[key].className + '"></i> ' + listButtonData[key].title + '</button>');
            listBtnDiv.append(menuItem);
        }
    }

    function executeHandler(btnType) {
        try {
            if ("m_cleanAll" == btnType) {
                cleanAll();
            } else if ("m_restoreAll" == btnType) {
                restoreAll();
            } else if ("m_delete" == btnType) {
                deleteSelected();
            } else if ("m_restore" == btnType) {
                restoreSelected();
            }
        } catch (e) {
            return;
        }
    }

    function deleteSelected() {
        var selectedId = $("#fileList").getGridSelectedId();
        var ids = '';
        for (var i = 0; i < selectedId.length; i++) {
            if (i == 0) {
                ids = selectedId[i];
            } else {
                ids = ids + ',' + selectedId[i];
            }
        }
        ymPrompt.confirmInfo({
            title: "<spring:message code='file.title.deleteItems'/>",
            message: "<spring:message code='file.info.delete.completely'/>",
            icoCls: "ymPrompt_error",
            handler: function (tp) {
                if (tp == 'ok') {
                    inLayerLoading("<spring:message code='common.task.doing'/>", "loading-bar");
                    $.ajax({
                        type: "POST",
                        url: "${ctx}/trash/delete",
                        data: {
                            "ownerId": teamId,
                            "ids": ids,
                            "token": "<c:out value='${token}'/>"
                        },
                        error: function () {
                            unLayerLoading();
                            handlePrompt("error", "<spring:message code='operation.failed'/>");
                            listRecycler(currentPage);
                        },
                        success: function () {
                            unLayerLoading();
                            handlePrompt("success", "<spring:message code='operation.success'/>");
                            if (typeof(data) == 'string' && data.indexOf('<html>') != -1) {
                                window.location.href = "${ctx}/logout";
                                return;
                            }
                            listRecycler(currentPage);
                        }
                    });
                }
            }
        });
    }

    function restoreSelected() {
        var selectedId = $("#fileList").getGridSelectedId();
        var ids = '';
        for (var i = 0; i < selectedId.length; i++) {
            if (i == 0) {
                ids = selectedId[i];
            } else {
                ids = ids + ',' + selectedId[i];
            }
        }
        inLayerLoading("<spring:message code='common.task.doing'/>", "loading-bar");
        $.ajax({
            type: "POST",
            url: "${ctx}/trash/restore",
            data: {
                "ownerId": teamId,
                "ids": ids,
                "token": "<c:out value='${token}'/>"
            },
            error: function (request) {
                unLayerLoading();
                top.ymPrompt.close();

                if (request.responseText != null && request.responseText != "") {
                    changeParentFolder(request.responseText);
                }
                else {
                    handlePrompt("error", "<spring:message code='operation.failed'/>");
                    listRecycler(currentPage);
                }
            },
            success: function () {
                unLayerLoading();
                handlePrompt("success", "<spring:message code='operation.success'/>");

                if (typeof(data) == 'string' && data.indexOf('<html>') != -1) {
                    window.location.href = "${ctx}/logout";
                    return;
                }

                listRecycler(currentPage);
            }
        });
    }

    function cleanAll() {
        ymPrompt.confirmInfo({
            title: "<spring:message code='file.title.deleteAll'/>",
            message: "<spring:message code='file.info.deleteAll.completely'/>",
            icoCls: "ymPrompt_error",
            handler: function (tp) {
                if (tp == 'ok') {
                    $.ajax({
                        type: "POST",
                        data: {token: "<c:out value='${token}'/>"},
                        url: "${ctx}/trash/clean/" + teamId,
                        error: function () {
                            handlePrompt("error", "<spring:message code='operation.failed'/>");
                            listRecycler(currentPage);
                        },
                        success: function (data, textStatus, jqXHR) {
                            inLayerLoading("<spring:message code='common.task.doing'/>", "loading-bar");
                            if (typeof(data) == 'string' && data.indexOf('<html>') != -1) {
                                window.location.href = "${ctx}/logout";
                                return;
                            }
                            asyncClean(data);
                        }
                    });
                }
            }
        });
    }

    function asyncClean(taskId) {
        $.ajax({
            type: "GET",
            url: "${ctx}/nodes/listen?taskId=" + taskId + "&" + new Date().toString(),
            error: function (jqXHR) {
                unLayerLoading();
                handlePrompt("error", "<spring:message code='operation.failed'/>");
            },
            success: function (data, textStatus, jqXHR) {
                switch (data) {
                    case "Doing":
                        setTimeout(function () {
                            asyncClean(taskId);
                        }, 1500);
                        break;
                    case "NotFound":
                        unLayerLoading();
                        top.ymPrompt.close();
                        handlePrompt("success", "<spring:message code='operation.success'/>");
                        changeHash(1);
                        listRecycler(1);
                        break;
                    case "Forbidden":
                        unLayerLoading();
                        handlePrompt("error", "<spring:message code='error.forbid'/>");
                    default:
                        unLayerLoading();
                        top.ymPrompt.close();
                        handlePrompt("error", "<spring:message code='operation.failed'/>");
                        changeHash(1);
                        listRecycler(1);
                }
            }
        });
    }

    function restoreAll() {
        ymPrompt.confirmInfo({
            title: "<spring:message code='file.title.restoreAll'/>",
            message: "<spring:message code='file.info.restoreAll'/>",
            icoCls: "ymPrompt_error",
            handler: function (tp) {
                if (tp == 'ok') {
                    $.ajax({
                        type: "POST",
                        data: {token: "<c:out value='${token}'/>"},
                        url: "${ctx}/trash/restoreAll/" + teamId,
                        error: function () {
                            handlePrompt("error", "<spring:message code='operation.failed'/>");
                            listRecycler(currentPage);
                        },
                        success: function (data) {
                            inLayerLoading("<spring:message code='common.task.doing'/>", "loading-bar");
                            asyncRestoreAll(data);
                        }
                    });
                }
            }
        });
    }

    function asyncRestoreAll(taskId) {
        $.ajax({
            type: "GET",
            url: "${ctx}/nodes/listen?taskId=" + taskId + "&" + new Date().toString(),
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                unLayerLoading();
                if (XMLHttpRequest.status == 409) {
                    changeParentFolder(XMLHttpRequest.responseText);
                } else {
                    handlePrompt("error", "<spring:message code='operation.failed'/>");
                }
                changeHash(1);
                listRecycler(1);
            },
            success: function (data, textStatus, jqXHR) {
                switch (data) {
                    case "NotFound":
                        unLayerLoading();
                        top.ymPrompt.close();
                        handlePrompt("success", "<spring:message code='operation.success'/>");
                        changeHash(1);
                        listRecycler(1);
                        break;
                    case "Doing":
                        setTimeout(function () {
                            asyncRestoreAll(taskId);
                        }, 1500);
                        break;
                    case "Forbidden":
                        unLayerLoading();
                        handlePrompt("error", "<spring:message code='error.forbid'/>");
                        break;
                    default:
                        unLayerLoading();
                        handlePrompt("error", "<spring:message code='operation.failed'/>");
                        break;
                }
            }
        });
    }

    function hideAllBtns() {
        $("#restoreBtn,#cleanBtn,#cleanAllBtn,#restoreAllBtn").hide();
    }

    function changeParentFolder(noParentIds) {
        var url = "${ctx}/teamspace/trash/changeParent/" + teamId;
        top.ymPrompt.win({
            message: url,
            width: 600,
            height: 500,
            title: "<spring:message code='file.title.restore'/>",
            iframe: true,
            btn: [["<spring:message code='button.newFolder'/>", 'newFolder', false, "ymNewFolder"], ["<spring:message code='button.restore'/>", "yes", false, "btn-focus"], ["<spring:message code='button.cancel'/>", "no", true, "btn-cancel"]],
            handler: function (tp) {
                if (tp == "yes") {
                    top.ymPrompt.getPage().contentWindow.doRestore(teamId, noParentIds);
                } else if (tp == 'newFolder') {
                    top.ymPrompt.getPage().contentWindow.checkCreateFolder();
                } else {
                    top.ymPrompt.close();
                }
            }
        });
        top.ymPrompt_addModalFocus("#btn-focus");
        $("#ymNewFolder").css({"margin-left": "15px", "float": "left"}).parent().css({"padding-right": "65px"});
    }

</script>

</body>
</html>