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
    <%@ include file="../files/UploadMessages.jsp" %>
    <%@ include file="../files/commonForCreateAndRenameNode.jsp" %>
    <%@ include file="../files/commonForUploadFiles.jsp" %>
    <link href="${ctx}/static/skins/default/css/layout.css" rel="stylesheet" type="text/css"/>
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
        <ul id="breadcrumbCon" class="share-breadcrumb"></ul>
    </div>
</div>
<div class="body">
    <div class="body-con clearfix">
        <div class="pull-left clearfix">
            <div class="public-bar-con">
                <div class="public-layout">
                    <div class="public-bar clearfix" id="publicBar">
                        <div class="pull-left btn-toolbar">
                            <span id="uploadBtnBox" class="pull-left upload-btn-box"><input type="file" id="fileUpload" name="fileUpload" multiple="true"/></span>
                            <span id="uploadBtnBoxForJS" class="pull-left upload-btn-box">
                                <span class="fileinput-button">
                                    <span><i class="new-icon-toolbar-upload-blue"></i> <spring:message code='button.upload'/></span>
                                    <input id="fileUpload" type="file" name="files[]" multiple/>
                                </span>
                            </span><%-- directory webkitdirectory --%>
                            <button id="newFolderBtn" class="btn btn-primary" type="button" onClick="createFolder()">
                                <i class="new-icon-toolbar-create-folder"></i><spring:message code="button.newFolder"/>
                            </button>
                        </div>

                        <div id="tipInfo" class="tip-info pull-left"><spring:message code="share.info.sharefrom" arguments="${ownerName}"/></div>
                        <div class="range-type pull-right split-line">
                            <div class="btn-group" data-toggle="buttons-radio">
                                <button id="viewTypeBtnList" onClick="viewType('list')" type="button" class="btn btn-small btn-link" title='<spring:message code="file.tips.list"/>'><i class="icon-list"></i></button>
                                <button id="viewTypeBtnThumbnail" onClick="viewType('thumbnail')" type="button" class="btn btn-small btn-link" title='<spring:message code="file.tips.thumbnail"/>'><i class="icon-th"></i></button>
                            </div>
                        </div>
                        <div class="sort-type pull-right">
                            <div class="dropdown" id="taxisDropDown">
                                <a class="btn btn-small btn-link">
                                    <%--<spring:message code="common.label.sort"/>: --%>
                                    <i id="sortArray"></i><span id="sortOrderName"></span>
                                </a>
                                <ul class="dropdown-menu" id="sortUl">
                                    <li><a href="javascript:sortByDate();" id="dateSortLink" isDesc=""><spring:message code="common.field.date"/></a></li>
                                    <li><a href="javascript:sortByFileName();" id="nameSortLink" isDesc=""><spring:message code="common.field.name"/></a></li>
                                    <li><a href="javascript:sortBySize();" id="sizeSortLink" isDesc=""><spring:message code="common.field.size"/></a></li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="files-block clearfix">
                <div id="fileListNull" class="file-catalog-null"><spring:message code="shareIndex.curFolderEmpty"/></div>
                <div id="fileList"></div>
                <div id="fileListPageBox"></div>
                <div id="noPermission" class="page-error" style="display:none;">
                    <h3><spring:message code="shareIndex.msg.noPermission"/></h3>
                    <p><spring:message code="shareIndex.msg.contactSharer"/></p>
                </div>
            </div>
        </div>
    </div>
    <form id="downloadForm" action="" method="get" style="display: none" target=""></form>
    <%@ include file="../common/footer.jsp" %>

    <script type="text/javascript">
        navMenuSelected("navShareToMe");
        var canPreview =<%=PreviewUtils.isPreview()%>;
        var curUserId = '<shiro:principal property="cloudUserId"/>';
        var ownerId = "<c:out value='${ownerId}'/>";
        var ownerName = "<c:out value='${ownerName}'/>";
        var opts_viewGrid = null;
        var opts_page = null;
        var tempDestOwnerID = null;
        var permissionFlag = null;
        var isLinkHidden = <c:out value='${linkHidden}'/>;
        var isFolderIsUploading = false;
        var uploadFolderFail = new HashMap();

        var showPc = false;

        var listViewType = getRootCookie("ShareListViewType");
        if (null == listViewType || undefined == listViewType) {
            listViewType = "list";
        }
        var currentPage = 1;
        var catalogParentId = "<c:out value='${shareRootId}'/>";
        var shareRootId = "<c:out value='${shareRootId}'/>";
        var catalogData = null;
        var tempParentId = null;
        var viewMode = "file";
        var orderField = getRootCookie("ShareOrderField");
        if (orderField == null || orderField == 'ownerName') {
            orderField = "modifiedAt";
        }
        var M = {
            m_open: {title: '<spring:message code="button.open"/>', className: "icon-folder-open"},
            m_download: {title: '<spring:message code="button.download"/>', className: "icon-download"},
            m_link: {title: "<spring:message code='button.shareLink'/>", className: "icon-link"},
            m_move: {title: "<spring:message code='button.copyOrMove'/>", className: "icon-copy"},
            m_online_edit: {title: "<spring:message code='button.onlineEdit'/>", className: "icon-online-edit"},
            m_delete: {title: "<spring:message code='button.delete'/>", className: "icon-cancel"},
            m_rename: {title: "<spring:message code='button.rename'/>", className: "icon-pencil"},
            m_saveToMe: {title: '<spring:message code="button.saveToMe"/>', className: "icon-save", divider: true},
            m_versionList: {title: '<spring:message code="button.listVersion"/>', className: "icon-info"}

        };

        var isDesc = getRootCookie("ShareIsDesc");
        isDesc = isDesc == null ? "true" : isDesc;

        var headData = {
            "name": {"width": "64.5%"},
            "size": {"width": "18%"},
            "modifiedAt": {"width": "17.5%"}
        };
        $(document).ready(function () {
            init();
            userCustomInit();
            createBreadcrumb(catalogParentId, ownerId);

            window.onload = processHash;
            window.onhashchange = processHash;
        });

        var processHash = function () {
            var hash = window.location.hash;
            if (hash.indexOf('#') != -1) {
                var m = hash.split('#');
                m = m[1];
                if (m) {
                    if (m == 'none') {
                        return;
                    }
                    m = m.split('/');
                    currentPage = m[0];
                    catalogParentId = m[1];
                }
            } else {
                currentPage = 1;
                catalogParentId = shareRootId;
            }

            listFile(currentPage, catalogParentId);
        }

        function changeHash(curPage, parentId) {
            location.hash = '#' + curPage + '/' + parentId;
        }

        function createBreadcrumb(catalogParentId, ownerId) {
            var params = {
                "ownerId": ownerId,
                "inodeId": catalogParentId,
                "parentId": shareRootId
            };
            var url = "${ctx}/share/getPaths";
            $.ajax({
                type: "GET",
                url: url,
                cache: false,
                async: true,
                data: params,
                timeout: 180000,
                success: function (data) {
                    var breadcrumbItem;
                    $("#breadcrumbCon").find("> li").remove();
                    $("#breadcrumbCon").append("<li><a href='${ctx}/shared'><span>" + '<spring:message code="share.menu.shareToMe"/>' + "</span></a></li>");
                    for (var i = data.length - 1; i >= 0; i--) {
                        if (i == 0) {
                            breadcrumbItem = $("<li class='active'><span title='" + data[i].name + "'>" + data[i].name + "</span></li>");
                            $("#breadcrumbCon").append(breadcrumbItem);
                        } else {
                            breadcrumbItem = $("<li><a href='#1/" + data[i].id + "'><span title='" + data[i].name + "'>" + data[i].name + "</span></a></li>");
                            $("#breadcrumbCon").append(breadcrumbItem);
                        }
                    }

                    $("#breadcrumbCon span").tooltip({
                        container: "body",
                        placement: "top",
                        delay: {show: 100, hide: 0},
                        animation: false
                    });

                    var len = data.length + 1,
                        maxW = $("#breadcrumbCon").innerWidth();
                    breadcrumbAdapt(len, maxW);
                    $(window).bind("resize", function () {
                        maxW = $("#breadcrumbCon").innerWidth();
                        $("#breadcrumbCon").find("li.over-pass").remove();
                        $("#breadcrumbCon").find("li").show();
                        breadcrumbAdapt(len, maxW);
                    });
                }
            })
        }

        function breadcrumbAdapt(len, maxW) {
            var objW = 0;
            for (var i = 0; i < len; i++) {
                objW += $("#breadcrumbCon").find("li").eq(i).outerWidth();
            }
            if (objW > maxW) {
                var _w = 0, j = 1;
                for (var i = 1; i < len - 1; i++) {
                    _w += $("#breadcrumbCon").find("li").eq(i).width();
                    if (_w > (objW - maxW + 45)) {
                        j = i;
                        for (var n = 1; n < j + 1; n++) {
                            $("#breadcrumbCon").find("li").eq(n).hide();
                        }
                        $("#breadcrumbCon").find("li").eq(0).after('<li class="over-pass"><span>...</span></li>');
                        break;
                    }
                }
            }
        }

        function init() {
            loadSysSetting("${ctx}", '<spring:message code="common.language1" />');
            initViewType(listViewType);

            $("#tipInfo").find("strong").attr("title", $("#tipInfo").find("strong").text());

            sendUpLoad();

            opts_viewGrid = $("#fileList").comboGrid({
                headData: headData,
                dataId: "id",
                keyDelete: true,
                keyF2: true,
                viewType: listViewType
            });

            opts_page = $("#fileListPageBox").comboPage({
                showPageSet: true,
                lang: '<spring:message code="common.language1"/>'
            });
            $.fn.comboPage.pageSkip = function (opts, _idMap, curPage) {
                changeHash(curPage, catalogParentId);
            };
            $.fn.comboPage.setPerPageNum = function (opts, _idMap, num) {
                setRootCookie("fileListPerPageNum", num);
                if (viewMode == "file") {
                    listFile(currentPage, catalogParentId);
                } else {
                    doSearch(currentPage, keyword);
                }
            };
            $.fn.comboGrid.setItemOp = function (rowData, colTag, colIndex) {
                switch (colIndex) {
                    case 0:
                        try {
                            var alink, version, dataHandle, shareHandle, linkHandle, syncHandle,
                                itemVal = colTag.find("span").text();
                            dataHandle = $("<span class='file-handle'></span>");
                            colTag.find("span").remove();
                            if (isFolderType(rowData.type)) {
                                alink = "<a id='" + rowData.id + "' onclick='openFolder(" + rowData.id + ")'></a>";
                            } else {
                                var fileType = _getStandardType(rowData.name);
                                var videoType = rowData.name.substring(rowData.name.lastIndexOf(".") + 1).toLowerCase();
                                if (fileType == "img") {
                                    alink = "<a id='" + rowData.id + "' rel='lightbox|/|${ctx}/files/getUrlAndBrowse/" + ownerId + "/" + rowData.id + "?" + Math.random() + "|/|" + rowData.name + "|/|" + rowData.id + "' onclick='downloadImgFile($(this)," + rowData.id + ")'></a>";
                                } else if (fileType == "video" && videoType == "mp4") {
                                    alink = "<a id='" + rowData.id + "' onclick='playVideo(" + rowData.id + ")'></a>";
                                } else if (canPreview && rowData.previewable) {
                                    alink = "<a id='" + rowData.id + "' onclick='previewFile(" + rowData.id + "," + rowData.ownedBy + ")' title='" + rowData.name + "'>" + rowData.name + "</a>";
                                } else {
                                    alink = "<a id='" + rowData.id + "' onclick='downloadFile(" + rowData.id + ")'></a>";
                                }
                            }
                            colTag.append(alink);
                            $("#" + rowData.id).text(itemVal);
                            $("#" + rowData.id).attr("title", itemVal);

                            if (rowData.type == 1 && rowData.versions > 1) {
                                version = "<i class='file-version' title='<spring:message code='file.tips.fileVersion'/>' onclick='enterVersionList(" + rowData.id + ")'>V" + rowData.versions + "</i>";
                            }
                            if (rowData.linkStatus == '' || rowData.linkStatus == null) {
                                linkHandle = "<i class='icon-link no-set' title='<spring:message code='button.shareLink'/>' onclick='shareLink(" + rowData.id + ")'></i>";
                            } else {
                                linkHandle = "<i class='icon-link' title='<spring:message code='button.shareLink'/>' onclick='shareLink(" + rowData.id + ")'></i>";
                            }

                            if (!isLinkHidden) {
                                dataHandle.append(linkHandle);
                            }

                            colTag.append(version);
                            colTag.append(dataHandle);
                        } catch (e) {
                        }
                        break;
                    case 1:
                        if (isFolderType(rowData.type)) {
                            colTag.find("span").text("");
                        } else {
                            colTag.find("span").text(formatFileSize(rowData.size));
                        }
                        break;
                    case 2:
                        colTag.find("span").text(getLocalTime(rowData.modifiedAt));
                        break;
                    default :
                        break;
                }
            };

            $.fn.comboGrid.selectOp = function (allSelectData) {
                $.fn.comboGrid.initGridMenu = function () {
                    var menuData = [];
                    for (var key in M) M[key].divider = false;

                    if (allSelectData.length == 0) {
                    } else if (allSelectData.length == 1) {
                        var item = allSelectData[0];
                        var nodePermission = getNodePermission(item.id);
                        if (isFolderType(item.type)) {
                            menuData = {
                                "m_open": M.m_open,
                                "m_link": M.m_link,
                                "m_move": M.m_move,
                                "m_delete": M.m_delete,
                                "m_rename": M.m_rename,
                                "m_saveToMe": M.m_saveToMe
                            };

                            if (nodePermission["edit"] == 0) {
                                delete menuData.m_move;
                                delete menuData.m_rename;
                                delete menuData.m_delete;
                            }
                            if (nodePermission["download"] == 0) {
                                delete menuData.m_saveToMe;
                            }
                            if (nodePermission["publishLink"] == 0) {
                                delete menuData.m_link;
                            }

                        } else {
                            menuData = {
                                "m_link": M.m_link,
                                "m_download": M.m_download,
                                "m_move": M.m_move,
                                "m_online_edit": M.m_online_edit,
                                "m_delete": M.m_delete,
                                "m_rename": M.m_rename,
                                "m_saveToMe": M.m_saveToMe,
                                "m_versionList": M.m_versionList
                            };
                            if (nodePermission["edit"] == 0) {
                                delete menuData.m_online_edit;
                                delete menuData.m_move;
                                delete menuData.m_rename;
                                delete menuData.m_delete;
                            }
                            if (!isOnlineEditable(allSelectData[0])) {
                                delete menuData.m_online_edit;
                            }
                            if (nodePermission["download"] == 0) {
                                delete menuData.m_download;
                                delete menuData.m_saveToMe;
                            }
                            if (nodePermission["publishLink"] == 0) {
                                delete menuData.m_link;
                            }
                        }
                        if (isLinkHidden) {
                            delete menuData.m_link;
                        }
                        if (isBackupFolderType(allSelectData[0].type)) {
                            delete menuData.m_move;
                            delete menuData.m_saveToMe;
                            delete menuData.m_delete;
                        }
                    } else {
                        menuData = {"m_move": M.m_move, "m_delete": M.m_delete, "m_saveToMe": M.m_saveToMe};
                        if (permissionFlag["edit"] == 0) {
                            delete menuData.m_move;
                            delete menuData.m_delete;
                        }
                        if (permissionFlag["download"] == 0) {
                            delete menuData.m_saveToMe;
                        }
                    }
                    return menuData;
                };

                initListButton(allSelectData);
            };

            $.fn.comboGrid.dragMove = function (targetId) {
                var title;
                var dataList = $("#fileList").getGridSelectedData(catalogData, opts_viewGrid);

                if (dataList.length == 1) {
                    var nodePermission = getNodePermission(dataList[0].id);
                    if (!nodePermission || nodePermission["edit"] != 1) {
                        return;
                    }
                    if (isFolderType(dataList[0].type)) {
                        title = "<spring:message code='file.title.moveFolder'/>";
                    } else {
                        title = "<spring:message code='file.title.moveFile'/>";
                    }
                } else {
                    if (!permissionFlag || permissionFlag["edit"] != 1) {
                        return;
                    }
                    title = "<spring:message code='file.title.moveItems'/>"
                }

                ymPrompt.confirmInfo({
                    title: title, message: "<spring:message code='file.info.sureToMove'/>", handler: function (tp) {
                        if (tp == 'ok') {
                            dragMove(targetId);
                        }
                    }
                });
            };

            $.fn.comboGrid.dragDelete = function () {
                var dataList = $("#fileList").getGridSelectedData(catalogData, opts_viewGrid);

                if (dataList.length == 1) {
                    var nodePermission = getNodePermission(dataList[0].id);
                    if (!nodePermission || nodePermission["edit"] != 1) {
                        return;
                    }
                } else {
                    if (!permissionFlag || permissionFlag["edit"] != 1) {
                        return;
                    }
                }
                deleteNodes();
            };

            $.fn.comboGrid.dbTrOp = function (rowData) {
                if (isFolderType(rowData.type)) {
                    ownerId = rowData.ownedBy;
                    openFolder(rowData.id);
                } else if ("img" == _getStandardType(rowData.name)) {
                    var nodePermission = getNodePermission(rowData.id);
                    if (nodePermission["download"] != 1) {
                        handlePrompt("error", "<spring:message code='error.forbid'/>");
                        return;
                    }
                    $("#" + rowData.id).lightbox();
                }
            };

            $.fn.comboGrid.keyDelete = function () {
                var dataList = $("#fileList").getGridSelectedData(catalogData, opts_viewGrid);
                if (dataList.length == 1) {
                    var nodePermission = getNodePermission(dataList[0].id);
                    if (!nodePermission || nodePermission["edit"] != 1) {
                        return;
                    }
                } else {
                    if (!permissionFlag || permissionFlag["edit"] != 1) {
                        return;
                    }
                }
                deleteNodes();
            };

            $.fn.comboGrid.keyRename = function () {
                var dataList = $("#fileList").getGridSelectedData(catalogData, opts_viewGrid);
                if (dataList.length == 1) {
                    var nodePermission = getNodePermission(dataList[0].id);
                    if (!nodePermission || nodePermission["edit"] != 1) {
                        return;
                    }
                } else {
                    if (!permissionFlag || permissionFlag["edit"] != 1) {
                        return;
                    }
                }
                renameNode();
            };

            $.fn.gridMenuItemOp = function (btnType) {
                executeHandler(btnType);
            };

            $.fn.lightbox.downloadImg = function (id) {
                downloadFile(id);
            }
        }

        function initViewType(type) {
            if (type == "list") {
                $("#viewTypeBtnList").addClass("active");
            } else {
                $("#viewTypeBtnThumbnail").addClass("active");
            }
        }
        function viewType(type) {
            $("#fileList").switchViewType(type, opts_viewGrid);
            listViewType = type;
            setRootCookie("ShareListViewType", listViewType);
        }

        function initListButton(allSelectData) {
            var listButtonData = null;
            if (allSelectData.length == 0) {
                $("#listHandler").remove();
                $("#tipInfo").show();
            } else if (allSelectData.length == 1) {
                var item = allSelectData[0];
                var nodePermission = getNodePermission(allSelectData[0].id);
                if (isFolderType(allSelectData[0].type)) {
                    listButtonData = {
                        "m_link": M.m_link,
                        "m_move": M.m_move,
                        "m_delete": M.m_delete,
                        "m_rename": M.m_rename,
                        "m_saveToMe": M.m_saveToMe
                    };

                    if ("${memberInfo.teamRole}" != 'admin' && "${memberInfo.teamRole}" != 'manager') {
                        delete listButtonData.m_grant;
                        if (nodePermission["edit"] == 0) {
                            delete listButtonData.m_move;
                            delete listButtonData.m_rename;
                            delete listButtonData.m_delete;
                        }
                        if (nodePermission["download"] == 0) {
                            delete listButtonData.m_saveToMe;
                        }
                        if (nodePermission["publishLink"] == 0) {
                            delete listButtonData.m_link;
                        }
                    }
                } else {
                    listButtonData = {
                        "m_link": M.m_link,
                        "m_download": M.m_download,
                        "m_move": M.m_move,
                        "m_online_edit": M.m_online_edit,
                        "m_delete": M.m_delete,
                        "m_rename": M.m_rename,
                        "m_saveToMe": M.m_saveToMe,
                        "m_versionList": M.m_versionList
                    };
                    if (nodePermission["edit"] == 0) {
                        delete listButtonData.m_move;
                        delete listButtonData.m_rename;
                        delete listButtonData.m_online_edit;
                        delete listButtonData.m_delete;
                    }
                    if (!isOnlineEditable(allSelectData[0])) {
                        delete listButtonData.m_online_edit;
                    }
                    if (nodePermission["download"] == 0) {
                        delete listButtonData.m_saveToMe;
                        delete listButtonData.m_download;
                    }
                    if (nodePermission["publishLink"] == 0) {
                        delete listButtonData.m_link;
                    }
                }

                if (isLinkHidden) {
                    delete listButtonData.m_link;
                }
                if (isBackupFolderType(allSelectData[0].type)) {
                    delete listButtonData.m_saveToMe;
                    delete listButtonData.m_delete;
                    delete listButtonData.m_move;
                }
            } else {
                listButtonData = {"m_move": M.m_move, "m_delete": M.m_delete, "m_saveToMe": M.m_saveToMe,};
                if (permissionFlag["edit"] == 0) {
                    delete listButtonData.m_move;
                    delete listButtonData.m_delete;
                }
                if (permissionFlag["download"] == 0) {
                    delete listButtonData.m_saveToMe;
                }
            }

            var listBtnDiv = $('<div id="listHandler" class="list-handler pull-left"></div>');
            if ($("#listHandler").get(0)) {
                $("#listHandler").remove();
            }
            if (listButtonData == null) {
                return;
            }
            $("#publicBar").append(listBtnDiv).find("#tipInfo").hide();

            var showButtonNum = 4;
            if (permissionFlag["edit"] == 1) {
                showButtonNum--;
            }

            if (permissionFlag["upload"] == 1) {
                showButtonNum--;
            }
            var index = 0;
            for (var key in listButtonData) {
                var menuItem = $('<button onclick="executeHandler(\'' + key + '\')" class="btn btn-link" type="button" id="listBtn_' + key + '"><i class="' + listButtonData[key].className + '"></i> ' + listButtonData[key].title + '</button>');
                listBtnDiv.append(menuItem);
                index++;
            }

            if (index < 3 + (showButtonNum - 2)) {
                return;
            }
            var moreBtnGroup = $('<div class="dropdown" id="moreBtnGroup">' +
                '<a class="btn" data-toggle="dropdown"><i class="icon-more"></i><spring:message code="common.more"/></a>' +
                '<ul class="dropdown-menu" id="moreBtnList"></ul>' +
                '</div>');
            listBtnDiv.append(moreBtnGroup);
            for (var key in listButtonData) {
                var moreBtnItem = $('<li><a onclick="executeHandler(\'' + key + '\');" id="moreListBtn_' + key + '"><i class="' + listButtonData[key].className + '"></i> ' + listButtonData[key].title + '</a></li>');
                $("#moreBtnList").append(moreBtnItem);
            }

            var len = $("#listHandler").find(">button").length, maxW = $("#listHandler").width();
            if (allSelectData.length > 1) {
                $("#moreBtnGroup").css("display", "none");
            } else {
                for (var n = showButtonNum; n < len; n++) {
                    $("#listHandler").find(">button").eq(n).hide();
                    $("#moreBtnList").find("li").eq(n).show();
                }
                for (var n = 0; n < showButtonNum; n++) {
                    $("#moreBtnList").find("li").eq(n).hide();
                }
            }
        }

        function executeHandler(btnType) {
            try {
                if ("m_open" == btnType) {
                    openFolder();
                } else if ("m_download" == btnType) {
                    downloadFile();
                } else if ("m_rename" == btnType) {
                    renameNode();
                } else if ("m_link" == btnType) {
                    shareLink();
                } else if ("m_move" == btnType) {
                    copyAndMove();
                } else if ("m_online_edit" == btnType) {
                    onlineEdit();

                } else if ("m_delete" == btnType) {
                    deleteNodes();
                } else if ("m_versionList" == btnType) {
                    enterVersionList();
                } else if ("m_saveToMe" == btnType) {
                    saveToMe();
                } else if ("m_favorite" == btnType) {
                    var dataList = $("#fileList").getGridSelectedData(catalogData,
                        opts_viewGrid);
                    if (dataList.length == 0) {
                        return;
                    } else if (dataList.length == 1) {
                        var iNodeId = dataList[0].iNodeId;
                        var ownedBy = dataList[0].ownerId;
                        var parentId = dataList[0].parentId;
                        var nodeType = dataList[0].type;
                        var name = dataList[0].name;
                        createFavorite("share", ownedBy, iNodeId, nodeType, name, parentId);
                    }
                }
            } catch (e) {
                return;
            }
        }
        String.prototype.endsWith = function (suffix) {
            return this.indexOf(suffix, this.length - suffix.length) !== -1;
        }


        // 判断文件是否可以在线编辑
        function isOnlineEditable(item) {
            var name = (item.name || "").toLowerCase(), editable = false;
            $.each(['doc', 'docx', 'xls', 'xlsx', 'ppt', 'pptx'], function (index, suffix) {
                if (name.endsWith(suffix)) {
                    editable = true;
                    return false;
                }
            });
            return editable;
        }


        // 在线编辑
        function onlineEdit() {
            var file = $("#fileList").getGridSelectedData(catalogData, opts_viewGrid)[0];
            var parentId = file.parentId, fileId = file.id;
            $('<form action="${ctx}/files/onlineEdit/' + ownerId + "/" + parentId + '/' + fileId + '" target="_blank"></form>').appendTo('body').submit().remove();

        }

        function openFolder(id) {
            if (id == null || id == "") {
                var objData = $("#fileList").getGridSelectedData(catalogData, opts_viewGrid);
                ownerId = objData[0].ownedBy;
                id = objData[0].id;
            }
            new Spinner(optsMiddleSpinner).spin($("#" + id).parents(".rowli").find("> div").get(0));
            changeHash(1, id);
        }


        function downloadFile(id, nodePermission) {
            if (id == null || id == "") {
                var selectedId = $("#fileList").getGridSelectedId();
                id = selectedId[0];
            }

            if (nodePermission == undefined || nodePermission == null) {
                nodePermission = getNodePermission(id);
            }
            if (nodePermission["download"] != 1) {
                handlePrompt("error", "<spring:message code='error.forbid'/>");
                return;
            }
            $.ajax({
                type: "GET",
                async: false,
                url: "${ctx}/files/getDownloadUrl/" + ownerId + "/" + id + "?t=" + new Date().toString(),
                error: function (request) {
                    doErrorWhenGetDownloadUrl(request);
                },
                success: function (data) {
                    $("#downloadForm").attr("action", data);
                    $("#downloadForm").submit();
                }
            });
        }

        function downloadImgFile(that, id) {
            var nodePermission = getNodePermission(id);
            if (nodePermission["download"] != 1) {
                handlePrompt("error", "<spring:message code='error.forbid'/>");
                return;
            }
            that.lightbox();
        }

        function playVideo(id) {
            if (id == null || id == "") {
                var selectedId = $("#fileList").getGridSelectedId();
                id = selectedId[0];
            }
            var nodePermission = getNodePermission(id);
            if (nodePermission["download"] != 1) {
                handlePrompt("error", "<spring:message code='error.forbid'/>");
                return;
            }
            $.ajax({
                type: "GET",
                async: false,
                url: "${ctx}/files/getUrlAndBrowse/" + ownerId + "/" + id + "?t=" + new Date().toString(),
                error: function (request) {
                    doErrorWhenGetDownloadUrl(request);
                },
                success: function (data) {
                    $("#playVideoModal").modal('show');
                    $("#playVideoModal").find("h3").text($("#" + id).text());
                    $("#playVideoModal").find("video").attr("src", data + "?mime=video/mp4");
                }
            });

            $("#playVideoModal").on("hide", function () {
                $("#playVideoModal").find("video").attr("src", "");
            })
        }

        function previewFile(fileId, ownerId) {
            var nodePermission = getNodePermission(fileId);
            if (nodePermission["preview"] == 1) {
                window.open("${ctx}/files/gotoPreview/" + ownerId + "/" + fileId + "?parentPageType=receiveShare");
            } else {
                downloadFile(fileId, nodePermission);
            }
        }

        function doErrorWhenGetDownloadUrl(request) {
            downloadFileErrorHandler(request);
            if (request.status == 404) {
                listFile(currentPage, catalogParentId);
            }

        }
        function dragMove(targetId) {
            var idArray = $("#fileList").getGridSelectedId();
            var ids = "";
            for (var i = 0; i < idArray.length; i++) {
                var id = idArray[i];
                if (i == 0) {
                    ids = idArray[i];
                } else {
                    ids = ids + ',' + idArray[i];
                }
            }
            tempDestOwnerID = ownerId;
            $.ajax({
                type: "POST",
                url: "${ctx}/nodes/move/" + ownerId,
                data: {
                    'destOwnerId': tempDestOwnerID,
                    'ids': ids,
                    'parentId': targetId,
                    'token': "<c:out value='${token}'/>"
                },
                error: function (request) {
                    top.ymPrompt.close();
                    if (request.status == 404) {
                        top.handlePrompt("error", "<spring:message code='error.notfound'/>");
                    } else {
                        top.handlePrompt("error", "<spring:message code='operation.failed'/>");
                    }
                },
                success: function (data) {
                    top.inLayerLoading("<spring:message code='common.task.doing'/>", "loading-bar");
                    top.asyncListen("move", ownerId, targetId, data);
                }
            });
        }


        function shareLink(nodeIdTmp) {
            $("body").css("overflow", "hidden");
            var inodeId = nodeIdTmp;
            if (inodeId == undefined) {
                var node = $("#fileList").getGridSelectedData(catalogData, opts_viewGrid);
                inodeId = node[0].id;
            }
            var nodePermission = getNodePermission(inodeId);
            if (nodePermission["publishLink"] != 1) {
                handlePrompt("error", "<spring:message code='error.forbid'/>");
                return;
            }
            var url = '${ctx}/share/link/' + ownerId + '/' + inodeId;
            top.ymPrompt.win({
                message: url,
                width: 650,
                height: 350,
                title: "<spring:message code='file.title.shareLink'/>",
                iframe: true,
                handler: linkHandle
            });
        }

        function linkHandle(tp) {
            $("body").css("overflow", "scroll");
            top.ymPrompt.close();
            listFile(currentPage, catalogParentId);
        }

        function copyAndMove() {
            tempDestOwnerID = ownerId;
            var url = "${ctx}/shared/copyAndMove/" + ownerId + "/" + shareRootId;
            top.ymPrompt.win({
                message: url,
                width: 600,
                height: 500,
                title: "<spring:message code='file.title.copyOrMove'/>",
                iframe: {id: "copyAndMoveFrame"},
                btn: [["<spring:message code='button.newFolder'/>", 'newFolder', false, "ymNewFolder"], ["<spring:message code='button.copy'/>", 'copy', false, "btnCopy"], ["<spring:message code='button.move'/>", 'move', false, "btnMove"], ["<spring:message code='button.cancel'/>", 'no', true, "btnCancel"]],
                handler: doCopyAndMove
            });
            top.ymPrompt_addModalFocus("#btnCopy,#btnMove");
            $("#ymNewFolder").css({"margin-left": "15px", "float": "left"}).parent().css({"padding-right": "65px"});
        }

        function doCopyAndMove(tp) {
            var idArray = $("#fileList").getGridSelectedId();
            if (tp == 'copy' || tp == 'move') {
                top.ymPrompt.getPage().contentWindow.submitCopyAndMove(tp, ownerId, idArray);
            } else if (tp == 'newFolder') {
                top.ymPrompt.getPage().contentWindow.createFolder();
            } else {
                top.ymPrompt.close();
            }
        }

        function saveToMe() {
            tempDestOwnerID = curUserId;
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

        function enterVersionList() {
            var node = $("#fileList").getGridSelectedData(catalogData, opts_viewGrid);
            var url = "${ctx}/files/listVersion/" + node[0].ownedBy + "/" + node[0].id + "?parentPageType=receiveShare";
            top.ymPrompt.win({
                message: url,
                width: 650,
                height: 450,
                title: '<spring:message code="shareIndex.versionsList"/>',
                iframe: true,
                btn: [['<spring:message code="button.close"/>', "no", true, "btn-cancel"]],
                handler: null
            });
            top.ymPrompt_addModalFocus("#btn-focus");
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
                            if (viewMode == "file") {
                                listFile(currentPage, catalogParentId);
                            } else {
                                doSearch();
                            }
                            break;
                        case "Doing":
                            setTimeout(function () {
                                asyncListen(type, srcOwnerId, selectFolder, taskId);
                            }, 1500);
                            break;
                        case "SameParentConflict":
                            unLayerLoading();
                            if ($("#maskLevel").css("display") == "block") {
                                ymPrompt.getPage().contentWindow.handlePrompt("error", "<spring:message code='file.errorMsg.parentNotChange'/>", '', 10);
                            } else {
                                handlePrompt("error", "<spring:message code='file.errorMsg.parentNotChange'/>");
                            }
                            break;
                        case "SameNodeConflict":
                            unLayerLoading();
                            if ($("#maskLevel").css("display") == "block") {
                                ymPrompt.getPage().contentWindow.handlePrompt("error", "<spring:message code='file.errorMsg.sameFolder'/>", '', 10);
                            } else {
                                handlePrompt("error", "<spring:message code='file.errorMsg.sameFolder'/>");
                            }
                            break;
                        case "SubFolderConflict":
                            unLayerLoading();
                            if ($("#maskLevel").css("display") == "block") {
                                ymPrompt.getPage().contentWindow.handlePrompt("error", "<spring:message code='file.errorMsg.canNotCopyToChild'/>", '', 10);
                            } else {
                                handlePrompt("error", "<spring:message code='file.errorMsg.canNotCopyToChild'/>");
                            }
                            break;
                        case "Forbidden":
                            unLayerLoading();
                            if ($("#maskLevel").css("display") == "block") {
                                ymPrompt.getPage().contentWindow.handlePrompt("error", "<spring:message code='error.forbid'/>");
                            } else {
                                handlePrompt("error", "<spring:message code='error.forbid'/>");
                            }
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
                title: "<spring:message code='file.title.copyOrMove'/>",
                message: "<spring:message code='file.info.autoRename'/>",
                handler: function (tp) {
                    if (tp == 'ok') {
                        var url;
                        if (type == 'copy') {
                            url = "${ctx}/nodes/renameCopy/" + srcOwnerId;
                        } else {
                            url = "${ctx}/nodes/renameMove/" + srcOwnerId;
                        }
                        $.ajax({
                            type: "POST",
                            url: url,
                            data: {
                                'destOwnerId': tempDestOwnerID,
                                'ids': conflictIds,
                                'parentId': selectFolder,
                                'token': "<c:out value='${token}'/>"
                            },
                            error: function (request) {
                                handlePrompt("error", "<spring:message code='operation.failed'/>");
                            },
                            success: function (data) {
                                inLayerLoading("<spring:message code='common.task.doing'/>", "loading-bar");
                                asyncListen(type, srcOwnerId, selectFolder, data);
                            }
                        });
                    }
                }
            });
        }

        function deleteNodes() {
            var title;
            var dataList = $("#fileList").getGridSelectedData(catalogData, opts_viewGrid);
            if (dataList.length == 0) {
                return;
            } else if (dataList.length == 1) {
                if (dataList[0].type == 0) {
                    title = "<spring:message code='file.title.deleteFolder'/>"
                } else {
                    title = "<spring:message code='file.title.deleteFile'/>"
                }
            } else {
                title = "<spring:message code='file.title.deleteItems'/>";
            }
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
                title: title,
                message: "<spring:message code='file.info.sureToDelete'/>",
                btn: [["<spring:message code='button.ok'/>", "ok", true, "btn-focus"], ["<spring:message code='button.cancel'/>", "no", true, "btn-cancel"]],
                handler: function (tp) {
                    if (tp == 'ok') {
                        $.ajax({
                            type: "POST",
                            url: "${ctx}/nodes/delete",
                            data: {'ownerId': ownerId, 'ids': ids, 'token': "<c:out value='${token}'/>"},
                            error: function (request) {
                                var status = request.status;
                                if (status == 403) {
                                    handlePrompt("error", "<spring:message code='error.forbid'/>");
                                } else {
                                    handlePrompt("error", "<spring:message code='operation.failed'/>");
                                }
                            },
                            success: function (data) {
                                inLayerLoading("<spring:message code='common.task.doing'/>", "loading-bar");
                                if (typeof(data) == 'string' && data.indexOf('<html>') != -1) {
                                    window.location.href = "${ctx}/logout";
                                    return;
                                }
                                asyncDelete(data);
                            }
                        });
                    }
                }
            });
            top.ymPrompt_addModalFocus("#btn-focus");
        }
        function asyncDelete(taskId) {
            $.ajax({
                type: "GET",
                url: "${ctx}/nodes/listen?taskId=" + taskId + "&" + new Date().toString(),
                error: function (request) {
                    unLayerLoading();
                    handlePrompt("error", "<spring:message code='operation.failed'/>");
                },
                success: function (data, textStatus, jqXHR) {
                    switch (data) {
                        case "Doing":
                            setTimeout(function () {
                                asyncDelete(taskId);
                            }, 1000);
                            break;
                        case "NotFound":
                            unLayerLoading();
                            handlePrompt("success", "<spring:message code='operation.success'/>");
                            if (viewMode == "file") {
                                listFile(top.currentPage, catalogParentId);
                            } else {
                                doSearch();
                            }
                            break;
                        case "NoSuchSource":
                            unLayerLoading();
                            handlePrompt("error", "<spring:message code='error.notfound'/>");
                            if (viewMode == "file") {
                                listFile(top.currentPage, catalogParentId);
                            } else {
                                doSearch();
                            }
                            break;
                        case "Forbidden":
                            unLayerLoading();
                            handlePrompt("error", "<spring:message code='error.forbid'/>");
                            break;
                        case "SystemException":
                            unLayerLoading();
                            handlePrompt("error", "<spring:message code='operation.failed'/>");
                            break;
                    }

                }
            });
        }

        function listFile(curPage, parentId) {
            permissionFlag = getNodePermission(parentId);
            var pagePerDis = getCookie("fileListPerPageNum");
            if (null == pagePerDis || pagePerDis == undefined || pagePerDis == "") {
                pagePerDis = 40;
            }
            var url = "${ctx}/shared/listsub";
            var params = {
                "ownerId": ownerId,
                "parentId": parentId,
                "pageNumber": curPage,
                "orderField": orderField,
                "pageSize": pagePerDis,
                "desc": isDesc,
                "token": "<c:out value='${token}'/>"
            };
            if (permissionFlag != null && permissionFlag["browse"] == 1) {
                $.ajax({
                    type: "POST",
                    url: url,
                    data: params,
                    error: function (request) {
                        if (request.status == 500) {
                            window.location = window.location.protocol + "//" + window.location.host + "${ctx}" + "/shared";
                        } else if (request.status == 403) {
                            ymPrompt.alert({
                                title: "<spring:message code='common.tip'/>",
                                message: "<spring:message code='error.forbid'/>",
                                handler: function () {
                                    window.location = window.location.protocol + "//" + window.location.host + "${ctx}/shared";
                                }
                            });

                        } else {
                            handlePrompt("error", '<spring:message code="shareIndex.listFilesFail"/>');
                        }
                    },
                    success: function (data) {
                        $("#noPermission").hide();
                        catalogData = data.content;

                        if (catalogData.length == 0 && curPage != 1) {
                            curPage--;
                            changeHash(curPage, parentId);
                            return;
                        }

                        $("#fileList").setGridData(catalogData, opts_viewGrid);
                        $("#fileListPageBox").setPageData(opts_page, data.number, data.size, data.totalElements);
                        buttonInit();

                        comboxRemoveLoading("pageLoadingContainer");
                    }
                });
                createBreadcrumb(parentId, ownerId);
            } else {
                buttonInit();
                $("#fileList").setGridData([], opts_viewGrid);
                $("#fileListPageBox").setPageData(opts_page, 1, pagePerDis, 0);
                $("#fileListNull").hide();
                $("#noPermission").show();
            }

        }

        function getNodePermission(parentId) {
            var permission = null;
            var url = "${ctx}/teamspace/file/nodePermission?" + Math.random();
            var params = {
                "ownerId": ownerId,
                "nodeId": parentId
            };
            var flag = true;
            $.ajax({
                type: "GET",
                url: url,
                data: params,
                async: false,
                error: function (request) {
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

        function setAuthorityHint(data) {
            var str = "";
            if (data != null) {
                if (data["browse"] == 1) {
                    str += "<spring:message code='teamSpace.label.browse'/>,&nbsp;";
                }
                if (data["preview"] == 1) {
                    str += "<spring:message code='teamSpace.label.preview'/>,&nbsp;";
                }
                if (data["upload"] == 1) {
                    str += "<spring:message code='teamSpace.label.upload'/>,&nbsp;";
                }
                if (data["download"] == 1) {
                    str += "<spring:message code='teamSpace.label.download'/>,&nbsp;";
                }
                if (data["edit"] == 1) {
                    str += "<spring:message code='teamSpace.label.edit'/>,&nbsp;";
                }
                if (data["publishLink"] == 1) {
                    str += "<spring:message code='teamSpace.label.publishLink'/>,&nbsp;";
                }
                if (str != "") {
                    str = str.substring(0, str.length - ",&nbsp;".length);
                }
            }
            return str;
        }

        function refreshData(tp) {
            top.ymPrompt.close();
            changeHash(1, catalogParentId);
        }

        function setOrderToCookie() {
            setRootCookie("ShareOrderField", orderField);
            setRootCookie("ShareIsDesc", isDesc);
        }

        function initTree() {
            var setting = {
                async: {
                    enable: true,
                    url: "${ctx}/folders/listTreeNode/<c:out value='${ownerId}'/>",
                    otherParam: {"token": "<c:out value='${token}'/>"},
                    autoParam: ["id", "ownedBy"]
                }
            };
            var zNodes = [{
                id: 0,
                name: '<spring:message code="shareIndex.allFiles"/>',
                ownedBy: "<c:out value='${ownerId}'/>",
                open: true,
                isParent: true
            }];

            $(document).ready(function () {
                $.fn.zTree.init($("#treeArea"), setting, zNodes);
            });
        }
        function buttonInit() {
            if (viewMode == "file") {
                $("#newFolderBtn").hide();
                $(".upload-btn-box").css({"overflow": "hidden", "width": "0", "height": "0"});
                if (!permissionFlag) {
                    return;
                }
                if (permissionFlag["edit"] == 1) {
                    $("#newFolderBtn").show();
                }
                if (permissionFlag["upload"] == 1) {
                    $(".upload-btn-box").removeAttr("style");
                }

            } else {
                $("#newFolderBtn").hide();
                $(".upload-btn-box").css({"overflow": "hidden", "width": "0", "height": "0"});
            }
        }

        function gotoShareMyFolderError(status) {
            if (status == 404) {
                ymPrompt.close();
                handlePrompt("error", "<spring:message code='shareIndex.error.ListVersionList'/>");
                top.listFile(currentPage, catalogParentId);
            } else if (status == 403) {
                handlePrompt("error", "<spring:message code='error.forbid'/>");
                ymPrompt.close();
            }
        }
    </script>
</body>
</html>