<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="../common/common.jsp" %>
    <%@ include file="../files/commonForCreateAndRenameNode.jsp" %>
    <%@ include file="shareIndexSort.jsp" %>
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
        <div class="form-search">
            <div class="input-append">
                <input type="text" class="search-query" id="searchBar" maxlength=255
                       placeholder="<spring:message code='shareindex.info.search'/>"/>
                <button class="btn" type="button" onclick="doSearch(1)">
                    <li class="icon-search"></li>
                </button>
            </div>
        </div>
        <ul id="breadcrumbCon" class="pull-left share-breadcrumb">

        </ul>
    </div>
</div>
<div class="body">
    <div class="body-con clearfix">
        <div class="pull-left clearfix">
            <div class="public-bar-con">
                <div class="public-layout">
                    <div class="public-bar clearfix" id="publicBar">
                        <div id="tipInfo" class="tip-info pull-left"></div>
                        <div class="range-type pull-right split-line">
                            <div class="btn-group" data-toggle="buttons-radio">
                                <button id="viewTypeBtnList" onClick="viewType('list')" type="button"
                                        class="btn btn-small btn-link" title='<spring:message code="file.tips.list"/>'>
                                    <i class="icon-list"></i></button>
                                <button id="viewTypeBtnThumbnail" onClick="viewType('thumbnail')" type="button"
                                        class="btn btn-small btn-link"
                                        title='<spring:message code="file.tips.thumbnail"/>'><i class="icon-th"></i>
                                </button>
                            </div>
                        </div>
                        <div class="sort-type pull-right">
                            <div class="dropdown" id="taxisDropDown">
                                <a class="btn btn-small btn-link" href="#"><spring:message code="common.label.sort"/>:
                                    <span id="sortOrderName"></span><i id="sortArray"></i></a>
                                <ul class="dropdown-menu" id="sortUl">
                                    <li><a href="javascript:sortByFileName();" id="nameSort"><spring:message
                                            code="common.field.name"/></a></li>
                                    <li><a href="javascript:sortByOwner();" id="ownerSort"><spring:message
                                            code="shareIndex.sort.shareUser"/></a></li>
                                    <li><a href="javascript:sortByDate();" id="shareDateSort"><spring:message
                                            code="shareIndex.sort.sharedTime"/></a></li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="files-block clearfix">
                <div id="fileListNull" class="shareto-null"><spring:message code="shareIndex.SharedItemsEmpty"/></div>
                <div id="fileList"></div>

                <div id="fileListPageBox"></div>
            </div>

        </div>

    </div>
</div>

<%@ include file="../common/footer.jsp" %>

<script type="text/javascript">
    var curUserId = <shiro:principal property="cloudUserId"/>;
    var opts_viewGrid = null;
    var canPreview =<%=PreviewUtils.isPreview()%>;
    var opts_page = null;
    var viewMode = "file";
    var listViewType = getRootCookie("ShareListViewType");
    if (null == listViewType || undefined == listViewType) {
        listViewType = "list";
    }
    var currentPage = 1;
    var catalogData = null;
    var permissionFlag = null;
    var isLinkHidden = <c:out value='${linkHidden}'/>;

    var isEditHidden = true;

    var catalogParentId = null;
    var ownerId = null;

    var headData = {
        "name": {"width": "49.5%"},
        "ownerName": {"width": "23%"},
        "size": {"width": "10%"},
        "modifiedAt": {"width": "17.5%"}
    };
    var M = {
        m_open: {title: '<spring:message code="button.open"/>', className: "icon-folder-open"},
        m_saveToMe: {title: '<spring:message code="button.saveToMe"/>', className: "icon-save"},
        m_cancelShare: {title: '<spring:message code="shareIndex.label.cancelShare"/>', className: "icon-signout"},
        m_delete: {title: "<spring:message code='button.delete'/>", className: "icon-cancel"}, <%-- 删除 --%>
        m_rename: {title: "<spring:message code='button.rename'/>", className: "icon-pencil"}, <%-- 重命名 --%>
        m_download: {title: '<spring:message code="button.download"/>', className: "icon-download"},
        m_versionList: {title: '<spring:message code="button.listVersion"/>', className: "icon-info"},
        m_link: {title: "<spring:message code='button.shareLink'/>", className: "icon-link"}, <%-- 分享链接 --%>
        m_favorite: {title: "<spring:message code='button.favorite'/>", className: "icon-favorite"}
    };

    $(function () {
        navMenuSelected("navShareToMe");
        init();
        userCustomInit();
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
                var orgStr = m;
                m = m.split('/');
                viewMode = m[0];
                currentPage = m[1];
                if (viewMode == "file") {
                    catalogParentId = m[2];
                } else {
                    keyword = orgStr.substring(viewMode.length + currentPage.length + 2);
                    $("#searchBar").val(keyword);
                }
            }

        } else {
            viewMode = "file";
            currentPage = 1;
            catalogParentId = 0;
        }

        if (viewMode == "file") {
            initDataList(currentPage, catalogParentId);
        } else {
            doSearch(currentPage, keyword);
        }
    }

    function changeHash(viewMode, curPage, parentId) {
        location.hash = '#' + viewMode + "/" + curPage + '/' + parentId;
    }

    function init() {
        loadSysSetting("${ctx}", '<spring:message code="common.language1" />');
        initViewType(listViewType);
        opts_viewGrid = $("#fileList").comboGrid({
            headData: headData,
            dataId: "id",
            dragHandler: false,
            viewType: listViewType
        });

        initSearchEvent();

        opts_page = $("#fileListPageBox").comboPage({
            showPageSet: true,
            lang: '<spring:message code="common.language1"/>'
        });
        $.fn.comboPage.pageSkip = function (opts, _idMap, curPage) {
            if (viewMode == "file") {
                changeHash(viewMode, curPage, catalogParentId);
            } else {
                changeHash(viewMode, curPage, keyword);
            }
        };
        $.fn.comboPage.setPerPageNum = function (opts, _idMap, num) {
            setRootCookie("fileListPerPageNum", num);
            if (viewMode == "file") {
                initDataList(currentPage, catalogParentId);
            } else {
                doSearch(currentPage, keyword);
            }
        };
        $.fn.comboGrid.setItemOp = function (rowData, colTag, colIndex) {
            switch (colIndex) {
                case 0:
                    try {
                        var alink, itemVal = colTag.find("span").text();
                        colTag.find("span").remove();
                        if (isFolderType(rowData.type)) {
                            alink = "<a id='" + rowData.id + "' href=\"${ctx}/shared/list/" + rowData.ownerId + "/" + rowData.iNodeId + "\"></a>";
                        } else {
                            var fileType = _getStandardType(rowData.name);
                            var videoType = rowData.name.substring(rowData.name.lastIndexOf(".") + 1).toLowerCase();
                            if (fileType == "img") {
                                alink = "<a id='" + rowData.id + "' rel='lightbox|/|${ctx}/files/getUrlAndBrowse/" + rowData.ownerId + "/" + rowData.iNodeId + "?" + Math.random() + "|/|" + rowData.name + "|/|" + rowData.ownerId + "/" + rowData.iNodeId + "' onclick='downloadImgFile($(this)," + rowData.ownerId + "," + rowData.iNodeId + ")'></a>";
                            } else if (fileType == "video" && videoType == "mp4") {
                                alink = "<a id='" + rowData.id + "' onclick='playVideo(" + rowData.id + "," + rowData.ownerId + "," + rowData.iNodeId + ")'></a>";
                            } else if (canPreview && rowData.previewable) {
                                alink = "<a id='" + rowData.id + "' onclick='previewFile(" + rowData.iNodeId + "," + rowData.ownerId + ")' title='" + rowData.name + "'>" + rowData.name + "</a>";
                            } else {
                                alink = "<a id='" + rowData.id + "' onclick=\"downloadFile2(" + rowData.ownerId + "," + rowData.iNodeId + ")\"></a>";
                                if (rowData.version != "1") {
                                    alink = alink + "<i data-original-title=\"<spring:message code='file.tips.fileVersion'/>\" class=\"file-version\" title=\"<spring:message code='file.tips.fileVersion'/>\" onclick='enterVersionList(" + rowData.ownerId + "," + rowData.iNodeId + ")'>V" + rowData.version + "</i>";
                                }
                            }
                        }
                        colTag.append(alink);
                        $("#" + rowData.id).text(itemVal);
                        $("#" + rowData.id).attr("title", itemVal);

                    } catch (e) {
                    }
                    break;

                case 1:
                    try {
                        colTag.find("span").html('<spring:message code="shareIndex.sort.shareUser"/> ' + rowData.ownerName);
                        var itemVal = colTag.find("span").text();
                        colTag.find("span").attr("title", itemVal);
                    } catch (e) {
                    }
                    break;
                case 2:
                    if (isFolderType(rowData.type)) {
                        colTag.find("span").text("");
                    } else {
                        colTag.find("span").text(formatFileSize(rowData.size));
                    }
                    break;
                case 3:
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
                    var nodePermission = getNodePermission(item.ownerId, item.iNodeId);
                    if (isFolderType(item.type)) {
                        menuData = {
                            "m_open": M.m_open,
                            "m_delete": M.m_delete,
                            "m_rename": M.m_rename,
                            "m_saveToMe": M.m_saveToMe,
                            "m_link": M.m_link,
                            "m_favorite": M.m_favorite,
                            "m_cancelShare": M.m_cancelShare
                        };

                        if (nodePermission["edit"] == 0) {
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
                            "m_download": M.m_download,
                            "m_versionList": M.m_versionList,
                            "m_delete": M.m_delete,
                            "m_rename": M.m_rename,
                            "m_saveToMe": M.m_saveToMe,
                            "m_link": M.m_link,
                            "m_favorite": M.m_favorite,
                            "m_cancelShare": M.m_cancelShare
                        };
                        if (nodePermission["edit"] == 0) {
                            delete menuData.m_rename;
                            delete menuData.m_delete;
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
                    if (isEditHidden) {
                        delete menuData.m_rename;
                        delete menuData.m_delete;
                        delete menuData.m_link;
                    }
                    if (isBackupFolderType(allSelectData[0].type)) {
                        delete menuData.m_saveToMe;
                        delete menuData.m_delete;
                        delete menuData.m_favorite;
                    }
                } else {
                    menuData = {"m_cancelShare": M.m_cancelShare};
                }
                return menuData;
            };

            initListButton(allSelectData);
        };

        $.fn.comboGrid.dragDelete = function () {
            var dataList = $("#fileList").getGridSelectedData(catalogData, opts_viewGrid);

            if (dataList.length == 1) {
                var nodePermission = getNodePermission(dataList[0].ownerId, dataList[0].iNodeId);
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

        $.fn.comboGrid.keyDelete = function () {
            var dataList = $("#fileList").getGridSelectedData(catalogData, opts_viewGrid);

            if (dataList.length == 1) {
                var nodePermission = getNodePermission(dataList[0].ownerId, dataList[0].iNodeId);
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
                ownerId = dataList[0].ownerId;
                catalogParentId = dataList[0].parentId;
                var nodePermission = getNodePermission(dataList[0].ownerId, dataList[0].iNodeId);
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

        $.fn.comboGrid.dbTrOp = function (rowData) {
            if (isFolderType(rowData.type)) {
                openFolder2(rowData.ownerId, rowData.iNodeId);
            } else if (rowData.type == 1) {
                if ("img" == _getStandardType(rowData.name)) {
                    $("#" + rowData.id).lightbox();
                } else {
                    downloadFile2(rowData.ownerId, rowData.iNodeId);
                }
            }
        };

        $.fn.gridMenuItemOp = function (btnType) {
            executeHandler(btnType);
        };

        $.fn.lightbox.downloadImg = function (id) {
            var idData = id.split("/"),
                myOwnerId = idData[0],
                myiNodeId = idData[1];
            downloadFile2(myOwnerId, myiNodeId);
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
            var nodePermission = getNodePermission(allSelectData[0].ownerId, allSelectData[0].iNodeId);
            if (isFolderType(allSelectData[0].type)) {
                listButtonData = {
                    "m_delete": M.m_delete,
                    "m_rename": M.m_rename,
                    "m_link": M.m_link,
                    "m_saveToMe": M.m_saveToMe,
                    "m_favorite": M.m_favorite,
                    "m_cancelShare": M.m_cancelShare
                };
                if (nodePermission["edit"] == 0) {
                    delete listButtonData.m_rename;
                    delete listButtonData.m_delete;
                }
                if (nodePermission["download"] == 0) {
                    delete listButtonData.m_saveToMe;
                }
                if (nodePermission["publishLink"] == 0) {
                    delete listButtonData.m_link;
                }
            } else {
                listButtonData = {
                    "m_download": M.m_download,
                    "m_versionList": M.m_versionList,
                    "m_delete": M.m_delete,
                    "m_rename": M.m_rename,
                    "m_link": M.m_link,
                    "m_saveToMe": M.m_saveToMe,
                    "m_favorite": M.m_favorite,
                    "m_cancelShare": M.m_cancelShare
                };
                if (nodePermission["edit"] == 0) {
                    delete listButtonData.m_rename;
                    delete listButtonData.m_delete;
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
            if (isEditHidden) {
                delete listButtonData.m_rename;
                delete listButtonData.m_delete;
                delete listButtonData.m_link;
            }
            if (isBackupFolderType(allSelectData[0].type)) {
                delete listButtonData.m_saveToMe;
                delete listButtonData.m_delete;
                delete listButtonData.m_favorite;
            }
        } else {
            listButtonData = {"m_cancelShare": M.m_cancelShare};
        }


        var listBtnDiv = $('<div id="listHandler" class="list-handler pull-left"></div>');
        if ($("#listHandler").get(0)) {
            $("#listHandler").remove();
        }
        if (listButtonData == null) {
            return;
        }
        $("#publicBar").append(listBtnDiv).find("#tipInfo").hide();
        var index = 0;
        for (var key in listButtonData) {
            var menuItem = $('<button onclick="executeHandler(\'' + key + '\')" class="btn btn-link" type="button" id="listBtn_' + key + '"><i class="' + listButtonData[key].className + '"></i> ' + listButtonData[key].title + '</button>');
            listBtnDiv.append(menuItem);
            index++;
        }

        if (index < 5) {
            return;
        }
        var moreBtnGroup = $('<div class="dropdown" id="moreBtnGroup">' +
            '<a class="btn" data-toggle="dropdown"><i class="icon-more"></i><spring:message code="common.more"/></a>' +
            '<ul class="dropdown-menu" id="moreBtnList"></ul>' +
            '</div>');
        listBtnDiv.append(moreBtnGroup);
        for (var key in listButtonData) {
            var moreBtnItem = $('<li><a href="javascript:executeHandler(\'' + key + '\');" id="moreListBtn_' + key + '"><i class="' + listButtonData[key].className + '"></i> ' + listButtonData[key].title + '</a></li>');
            $("#moreBtnList").append(moreBtnItem);
        }

        var len = $("#listHandler").find(">button").length, maxW = $("#listHandler").width();
        if (allSelectData.length > 1) {
            $("#moreBtnGroup").css("display", "none");
        } else {
            for (var n = 4; n < len; n++) {
                $("#listHandler").find(">button").eq(n).hide();
                $("#moreBtnList").find("li").eq(n).show();
            }
            for (var n = 0; n < 4; n++) {
                $("#moreBtnList").find("li").eq(n).hide();
            }
        }
    }

    function executeHandler(btnType) {
        try {
            if ("m_open" == btnType) {
                openFolder();
            } else if ("m_rename" == btnType) {
                var dataList = $("#fileList").getGridSelectedData(catalogData, opts_viewGrid);

                if (dataList.length == 1) {
                    ownerId = dataList[0].ownerId;
                    catalogParentId = dataList[0].parentId;
                }
                renameNode();
            } else if ("m_delete" == btnType) {
                deleteNodes();
            } else if ("m_download" == btnType) {
                downloadFile();
            } else if ("m_versionList" == btnType) {
                enterVersionList();


            } else if ("m_cancelShare" == btnType) {
                cancelShare();
            } else if ("m_link" == btnType) {
                shareLink();
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
                    var parentId = dataList[0].parentId
                    var nodeType = dataList[0].type
                    var name = dataList[0].name
                    var id = dataList[0].id
                    createFavorite("share", ownedBy, iNodeId, nodeType, name, parentId, id);

                }
            }
        } catch (e) {
            return;
        }
    }

    function openFolder() {
        var objData = $("#fileList").getGridSelectedData(catalogData, opts_viewGrid);
        window.location = "${ctx}/shared/list/" + objData[0].ownerId + "/" + objData[0].iNodeId;
    }

    function openFolder2(_ownerId, _inodeId) {
        window.location = "${ctx}/shared/list/" + _ownerId + "/" + _inodeId;
    }

    function downloadFile() {
        var objData = $("#fileList").getGridSelectedData(catalogData, opts_viewGrid);
        $.ajax({
            type: "GET",
            async: false,
            url: "${ctx}/files/getDownloadUrl/" + objData[0].ownerId + "/" + objData[0].iNodeId + "?" + Math.random(),
            error: function (request) {
                doErrorWhenGetDownloadUrl(request);
            },
            success: function (data) {
                jQuery('<form action="' + data + '" method="get"></form>').appendTo('body').submit().remove();
            }
        });
    }

    function downloadFile2(_ownerId, _fileId, nodePermission) {
        if (nodePermission == undefined || nodePermission == null) {
            nodePermission = getNodePermission(_ownerId, _fileId);
        }
        if (nodePermission["download"] != 1) {
            handlePrompt("error", "<spring:message code='error.forbid'/>");
            return;
        }

        $.ajax({
            type: "GET",
            async: false,
            url: "${ctx}/files/getDownloadUrl/" + _ownerId + "/" + _fileId + "?" + Math.random(),
            error: function (request) {
                doErrorWhenGetDownloadUrl(request);
            },
            success: function (data) {
                jQuery('<form action="' + data + '" method="get"></form>').appendTo('body').submit().remove();
            }
        });
    }

    function downloadImgFile(that, _ownerId, _fileId) {
        var nodePermission = getNodePermission(_ownerId, _fileId);
        if (nodePermission["download"] != 1) {
            handlePrompt("error", "<spring:message code='error.forbid'/>");
            return;
        }
        that.lightbox();
    }

    function playVideo(_id, _ownerId, _fileId) {
        var nodePermission = getNodePermission(_ownerId, _fileId);
        if (nodePermission["download"] != 1) {
            handlePrompt("error", "<spring:message code='error.forbid'/>");
            return;
        }
        $.ajax({
            type: "GET",
            async: false,
            url: "${ctx}/files/getUrlAndBrowse/" + _ownerId + "/" + _fileId + "?" + Math.random(),
            error: function (request) {
                doErrorWhenGetDownloadUrl(request);
            },
            success: function (data) {
                $("#playVideoModal").modal('show');
                $("#playVideoModal").find("h3").text($("#" + _id).text());
                $("#playVideoModal").find("video").attr("src", data + "?mime=video/mp4");
            }
        });

        $("#playVideoModal").on("hide", function () {
            $("#playVideoModal").find("video").attr("src", "");
        })
    }

    function previewFile(fileId, ownerId) {
        var nodePermission = getNodePermission(ownerId, fileId);
        if (nodePermission["preview"] == 1) {
            window.open("${ctx}/files/gotoPreview/" + ownerId + "/" + fileId + "?parentPageType=receiveShare");
        } else {
            downloadFile2(_ownerId, fileId, nodePermission);
        }
    }


    function shareLink(ownerIdTmp, nodeIdTmp) {
        $("body").css("overflow", "hidden");
        var ownerId = ownerIdTmp;
        var inodeId = nodeIdTmp;
        if (ownerIdTmp == undefined) {
            var objData = $("#fileList").getGridSelectedData(catalogData, opts_viewGrid);
            ownerId = objData[0].ownerId;
            inodeId = objData[0].iNodeId;
        }
        var nodePermission = getNodePermission(ownerId, inodeId);
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
        if (tp == "close") {
            top.ymPrompt.getPage().contentWindow.callBackDelLink();
        }
        $("body").css("overflow", "scroll");
        top.ymPrompt.close();
        if (viewMode == "file") {
            initDataList(currentPage, catalogParentId);
        } else {
            doSearch();
        }
    }

    function saveToMe() {
        var url = "${ctx}/nodes/copyAndMove/" + curUserId;
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
            var objData = $("#fileList").getGridSelectedData(catalogData, opts_viewGrid);
            var idArray = [];
            idArray[0] = objData[0].iNodeId;
            top.ymPrompt.getPage().contentWindow.submitCopyAndMove(tp, objData[0].ownerId, idArray);
        } else if (tp == 'newFolder') {
            top.ymPrompt.getPage().contentWindow.createFolder();
        } else {
            top.ymPrompt.close();
        }
    }

    function enterVersionList(ownerId, iNodeId) {
        var url = "";
        if (ownerId != undefined) {
            var url = "${ctx}/files/listVersion/" + ownerId + "/" + iNodeId + "?parentPageType=receiveShare";

        } else {
            var node = $("#fileList").getGridSelectedData(catalogData, opts_viewGrid);
            url = "${ctx}/files/listVersion/" + node[0].ownerId + "/" + node[0].iNodeId + "?parentPageType=receiveShare";

        }
        top.ymPrompt.win({
            message: url,
            width: 600,
            height: 500,
            title: '<spring:message code="shareIndex.versionsList"/>',
            iframe: true,
            btn: [['<spring:message code="button.close"/>', 'no', true, "btn-cancel"]]
        });
        top.ymPrompt_addModalFocus("#btn-focus");
    }

    function gotoShareMyFolderError(status) {
        if (status == 404) {
            handlePrompt("error", "<spring:message code='shareIndex.error.ListVersionList'/>");
            ymPrompt.close();
            if (viewMode == "file") {
                initDataList(currentPage, catalogParentId);
            } else {
                doSearch();
            }
        } else if (status == 403) {
            handlePrompt("error", "<spring:message code='error.forbid'/>");
            ymPrompt.close();
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
                        if (viewMode == "file") {
                            initDataList(currentPage, catalogParentId);
                        } else {
                            doSearch();
                        }
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
                        ymPrompt.getPage().contentWindow.handlePrompt("error", "<spring:message code='error.forbid'/>");
                        break;
                    case "NoSuchSource":
                    case "NoSuchDest":
                        unLayerLoading();
                        top.ymPrompt.close();
                        handlePrompt("error", "<spring:message code='error.notfound'/>");
                        if (viewMode == "file") {
                            initDataList(currentPage, catalogParentId);
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
            message: '<spring:message code="link.task.renameFail"/>', handler: function (tp) {
                if (tp == 'ok') {
                    $.ajax({
                        type: "POST",
                        url: "${ctx}/nodes/renameCopy/" + srcOwnerId,
                        data: {
                            'destOwnerId': curUserId,
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

    function deleteNodes() {
        var title;
        var dataList = $("#fileList").getGridSelectedData(catalogData, opts_viewGrid);
        if (dataList.length != 1) {
            return;
        }
        if (isFolderType(dataList[0].type == 0)) {
            title = "<spring:message code='file.title.deleteFolder'/>"
        } else {
            title = "<spring:message code='file.title.deleteFile'/>"
        }

        var iNodeId = dataList[0].iNodeId;
        var ownedBy = dataList[0].ownerId;

        ymPrompt.confirmInfo({
            title: title,
            message: "<spring:message code='file.info.sureToDelete'/>",
            btn: [["<spring:message code='button.ok'/>", "ok", true, "btn-focus"], ["<spring:message code='button.cancel'/>", "no", true, "btn-cancel"]],
            handler: function (tp) {
                if (tp == 'ok') {
                    $.ajax({
                        type: "POST",
                        url: "${ctx}/nodes/delete",
                        data: {'ownerId': ownedBy, 'ids': iNodeId, 'token': "<c:out value='${token}'/>"},
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
                            initDataList(currentPage, catalogParentId);
                        } else {
                            doSearch();
                        }
                        break;
                    case "NoSuchSource":
                        unLayerLoading();
                        handlePrompt("error", "<spring:message code='error.notfound'/>");
                        if (viewMode == "file") {
                            initDataList(currentPage, catalogParentId);
                        } else {
                            doSearch();
                        }
                        break;
                    case "Forbidden":
                        unLayerLoading();
                        handlePrompt("error", "<spring:message code='error.forbid'/>");
                    case "SystemException":
                        unLayerLoading();
                        handlePrompt("error", "<spring:message code='operation.failed'/>");
                }

            }
        });
    }

    function doErrorWhenGetDownloadUrl(request) {
        var responseObj = $.parseJSON(request.responseText);
        switch (responseObj.code) {
            case "NoSuchFile":
                handlePrompt("error", '<spring:message code="shareIndex.error.NoSuchFile"/>');
                refreshList();
                break;
            case "NoSuchItem":
                handlePrompt("error", '<spring:message code="shareIndex.error.NoSuchItems"/>');
                refreshList();
                break;
            case "FileScanning":
                handlePrompt("error", '<spring:message code='file.errorMsg.fileNotReady'/>');
                break;
            case "ScannedForbidden":
                handlePrompt("error", '<spring:message code='file.errorMsg.downloadNotAllowed'/>');
                break;
            case "BadRequest":
                handlePrompt("error", '<spring:message code="shareIndex.error.BadRquest"/>');
                break;
            case "Forbidden":
                handlePrompt("error", '<spring:message code="error.forbid"/>');
                refreshList();
                break;
            case "SecurityMatrixForbidden":
                handlePrompt("error", "<spring:message code='error.forbid'/>");
                break;
            default:
                handlePrompt("error", '<spring:message code="shareIndex.error.InnerError"/>');
                break;
        }
    }

    function createBreadcrumb() {
        var breadcrumbItem;
        $("#breadcrumbCon").find("> li, p").remove();
        if (viewMode == "search") {
            breadcrumbItem = $("<li><a href='${ctx}/shared'><span>" + '<spring:message code="share.menu.shareToMe"/>' + "</span></a></li><p class='pull-left'>" + '<spring:message code="shareindex.searchResult1"/>' + "<strong id='resultCount'></strong>" + '<spring:message code="shareindex.searchResult2"/>' + "</p>");
            $("#breadcrumbCon").append(breadcrumbItem);
            return;
        }
        breadcrumbItem = $("<li class=\"active\"><span>" + '<spring:message code="share.menu.shareToMe"/>' + "</span></li>");
        $("#breadcrumbCon").append(breadcrumbItem);
    }


    function cancelShare() {
        var objData = $("#fileList").getGridSelectedData(catalogData, opts_viewGrid);
        var len = objData.length;
        var ownerIds = "";
        var inodeIds = "";
        if (len > 1) {
            var url = "${ctx}/shared/mulDelete";
            var isDeleteUser = 0;
            var isDeleteGroup = 0;
            var groupIds = "";
            var userIds = "";
            var groupNames = "";
            var userNames = "";
            for (var i = 0; i < len; i++) {
                if (i != 0) {
                    if (objData[i].sharedUserType == 1 && groupIds != "") {
                        groupIds = groupIds + ",";
                        userIds = userIds + ",";
                        groupNames = groupNames + "&";
                        userNames = userNames + "&";
                    }
                    else if (objData[i].sharedUserType == 0 && ownerIds != "") {
                        ownerIds = ownerIds + ",";
                        inodeIds = inodeIds + ",";
                    }
                }
                if (objData[i].sharedUserType == 1) {
                    groupIds = groupIds + objData[i].sharedUserId;
                    userIds = userIds + curUserId;
                    groupNames = groupNames + objData[i].sharedUserName;
                    userNames = userNames + objData[i].ownerName;
                    isDeleteGroup = 1;
                } else {
                    ownerIds = ownerIds + objData[i].ownerId;
                    inodeIds = inodeIds + objData[i].iNodeId;
                    isDeleteUser = 1;
                }
            }
            if (isDeleteUser == 1 && isDeleteGroup == 0) {
                ymPrompt.confirmInfo({
                    title: '<spring:message code="shareIndex.label.cancelShare"/>',
                    message: '<spring:message code="shareIndex.cancelShareConfirm"/>', handler: function (tp) {
                        if (tp == 'ok') {
                            $.ajax({
                                type: "POST",
                                url: url,
                                data: {
                                    ownerId: ownerIds,
                                    iNodeId: inodeIds,
                                    "token": "<c:out value='${token}'/>"
                                },
                                error: function (request) {
                                    doErrorWhenMulDel(request);
                                },
                                success: function () {
                                    handlePrompt("success", '<spring:message code="operation.success"/>');
                                    initDataList(currentPage, top.catalogParentId);
                                }
                            });
                        }
                    }
                });
            } else if (isDeleteUser == 0 && isDeleteGroup == 1) {
                doDeleteMultGroupMember(groupIds, userIds, groupNames, userNames);
            } else if (isDeleteUser == 1 && isDeleteGroup == 1) {
                ymPrompt.confirmInfo({
                    title: '<spring:message code="shareIndex.label.cancelShare"/>',
                    message: '<spring:message code="shareIndex.cancelShareConfirm"/>', handler: function (tp) {
                        if (tp == 'ok') {
                            $.ajax({
                                type: "POST",
                                url: url,
                                data: {
                                    ownerId: ownerIds,
                                    iNodeId: inodeIds,
                                    "token": "<c:out value='${token}'/>"
                                },
                                error: function (request) {
                                    doErrorWhenMulDel(request);
                                    doDeleteMultGroupMember(groupIds, userIds, groupNames, userNames);
                                },
                                success: function () {
                                    handlePrompt("success", '<spring:message code="operation.success"/>');
                                    doDeleteMultGroupMember(groupIds, userIds, groupNames, userNames);
                                }
                            });
                        }
                    }
                });
            }
        } else {
            var sharedUserType = objData[0].sharedUserType;
            if (sharedUserType == 1) {
                ymPrompt.confirmInfo({
                    width: 500,
                    height: 220,
                    title: "<spring:message code='shareIndex.label.cancelShare'/>",
                    message: "<spring:message code='group.share.tips' arguments='"+objData[0].ownerName+"%"+objData[0].sharedUserName+"' argumentSeparator="%"/>",
                    btn: [["<spring:message code='group.button.exist'/>", "ok", true, "btnGroupExist"], ["<spring:message code='group.button.cancle'/>", "no", true, "btnGroupCancle"]],
                    handler: function (tp) {
                        if (tp == 'ok') {
                            var groupId = objData[0].sharedUserId;
                            var userId = curUserId;
                            deleteGroupMember(groupId, userId);
                        }
                    }
                });
                top.ymPrompt_addModalFocus("#btnGroupCancle");
                return;
            } else {
                var url = "${ctx}/shared/delete";
                ymPrompt.confirmInfo({
                    title: '<spring:message code="shareIndex.label.cancelShare"/>',
                    message: '<spring:message code="shareIndex.cancelShareConfirm"/>', handler: function (tp) {
                        if (tp == 'ok') {
                            ownerIds = objData[0].ownerId;
                            inodeIds = objData[0].iNodeId;
                            $.ajax({
                                type: "POST",
                                url: url,
                                data: {
                                    ownerId: ownerIds,
                                    iNodeId: inodeIds,
                                    "token": "<c:out value='${token}'/>"
                                },
                                error: function (request) {
                                    doErrorWhenMulDel(request);
                                },
                                success: function () {
                                    handlePrompt("success", '<spring:message code="operation.success"/>');
                                    initDataList(currentPage, top.catalogParentId);
                                }
                            });
                        }
                    }
                });
            }
        }
    }

    function doErrorWhenMulDel(request) {
        switch (request.responseText) {
            case "BadRquestException":
                handlePrompt("error", '<spring:message code="shareIndex.error.BadRquest"/>');
                break;
            case "Forbidden":
                handlePrompt("error", '<spring:message code="shareIndex.error.Forbidden"/>');
                refreshList();
                break;
            case "NoSuchItemsException":
                handlePrompt("error", '<spring:message code="share.error.NoSuchItems"/>');
                refreshList();
                break;
            default:
                handlePrompt("error", '<spring:message code="operation.failed"/>');
        }
    }


    function refreshData(tp) {
        top.ymPrompt.close();
        changeHash(viewMode, currentPage, catalogParentId);
    }

    function refreshList() {
        if (viewMode == "file") {
            changeHash(viewMode, currentPage, catalogParentId);
        } else {
            if (keyword == undefined) {
                keyword = $("#searchBar").val();
            }
            keyword = keyword == null ? "" : keyword.trim();
            changeHash(viewMode, currentPage, keyword);
        }
    }

    function initDataList(curPage, parentId) {
        var pagePerDis = getCookie("fileListPerPageNum");
        if (null == pagePerDis || pagePerDis == undefined || pagePerDis == "") {
            pagePerDis = 40;
        }
        viewMode = "file";
        $.ajax({
            type: "POST",
            url: "${ctx}/shared/list",
            data: {
                "pageNumber": curPage,
                "orderField": orderField,
                "desc": isDesc,
                "pageSize": pagePerDis,
                "token": "<c:out value='${token}'/>"
            },
            error: function (request) {

                comboxRemoveLoading("pageLoadingContainer");
                handlePrompt("error", '<spring:message code="shareIndex.listFilesFail"/>');
            },
            success: function (data) {
                catalogData = data.content;

                if (catalogData.length == 0 && curPage != 1) {
                    curPage--;
                    changeHash(viewMode, curPage, parentId);
                    return;
                }

                $("#fileList").setGridData(catalogData, opts_viewGrid);
                $("#fileListPageBox").setPageData(opts_page, data.number, data.size, data.totalElements);
                buttonInit();
                $("#tipInfo").html('<spring:message code="share.info.receive" arguments="' + data.totalElements + '" />');

                comboxRemoveLoading("pageLoadingContainer");
            }
        });
        createBreadcrumb();

    }

    function getNodePermission(_ownerId, _parentId) {
        var permission = null;
        var url = "${ctx}/teamspace/file/nodePermission?" + Math.random();
        var params = {
            "ownerId": _ownerId,
            "nodeId": _parentId
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

    function buttonInit() {
        $("#listHandler").remove();
        $("#uploadBtn, #newFolder").show();
    }

    function initSearchEvent() {

        $("#searchBar").keydown(function () {
            var evt = arguments[0] || window.event;
            if (evt.keyCode == 13) {
                doSearch();
            }
        });
    }

    function doSearch(curPage, keyword) {
        viewMode = "search";

        if (curPage == undefined) {
            curPage = currentPage;
        }
        if (keyword == undefined) {
            keyword = $("#searchBar").val();
        }
        keyword = keyword == null ? "" : keyword.trim();
        if (keyword == "") {
            $("#searchBar").val("");
            return;
        }

        var pagePerDis = getCookie("fileListPerPageNum");
        if (null == pagePerDis || pagePerDis == undefined || pagePerDis == "") {
            pagePerDis = 40;
        }
        var searchSpiner = new Spinner(optsSmallSpinner).spin($("#searchBar").parent().get(0));

        var url = "${ctx}/shared/list";
        var params = {
            "name": keyword,
            "pageNumber": currentPage,
            "orderField": orderField,
            "desc": isDesc,
            "pageSize": pagePerDis,
            "token": "<c:out value='${token}'/>"
        };

        $.ajax({
            type: "POST",
            url: url,
            data: params,
            success: function (data) {
                searchSpiner.stop();
                $("#resultCount").html(data.totalElements);
                catalogData = data.content;

                if (catalogData.length == 0 && curPage != 1) {
                    curPage--;
                    changeHash(viewMode, curPage, keyword);
                    return;
                }

                $("#fileList").setGridData(catalogData, opts_viewGrid);
                $("#fileListPageBox").setPageData(opts_page, data.number, data.size, data.totalElements);
                changeHash(viewMode, curPage, keyword);
                buttonInit();

                comboxRemoveLoading("pageLoadingContainer");
            }
        });
        createBreadcrumb();
    }

    function listData() {
        if ("file" == viewMode) {
            initDataList(currentPage, catalogParentId);
        } else if ("search" == viewMode) {
            doSearch();
        }
    }

    function doDeleteMultGroupMember(groupIds, userIds, groupNames, userNames) {
        ymPrompt.confirmInfo({
            width: 500,
            height: 220,
            title: "<spring:message code='shareIndex.label.cancelShare'/>",
            message: "<spring:message code='group.share.tips' arguments='"+userNames+"%"+groupNames+"' argumentSeparator="%"/>",
            btn: [["<spring:message code='group.button.exist'/>", "ok", true, "btnGroupExist"], ["<spring:message code='group.button.cancle'/>", "no", true, "btnGroupCancle"]],
            handler: function (tp) {
                if (tp == 'ok') {
                    var groupId = groupIds;
                    var userId = userIds;
                    deleteMultGroupMember(groupId, userId);
                } else {
                    initDataList(currentPage, top.catalogParentId);
                }
            }
        });
        top.ymPrompt_addModalFocus("#btnGroupCancle");
    }

    function deleteMultGroupMember(groupId, userId) {
        var url = "${ctx}/group/member/multDelMember";
        $.ajax({
            type: "POST",
            url: url,
            data: {
                groupIds: groupId,
                userIds: userId,
                "token": "<c:out value='${token}'/>"
            },
            error: function (request) {
                switch (request.responseText) {
                    case "NoSuchGroup":
                        top.ymPrompt.close();
                        handlePrompt("error", "<spring:message code='group.error.notexist'/>");
                        refreshList();
                        break;
                    case "Forbidden":
                        top.ymPrompt.close();
                        handlePrompt("error", "<spring:message code='group.error.forbidden'/>");
                        refreshList();
                        break;
                    default:
                        top.ymPrompt.close();
                        handlePrompt("error", "<spring:message code='operation.failed'/>");
                        break;
                }
            },
            success: function (data) {
                if (data.G_OWNER == 1) {
                    handlePrompt("error", "<spring:message code='group.operation.owner'/>");
                } else {
                    handlePrompt("success", "<spring:message code='group.operation.ok'/>");
                }
                initDataList(currentPage, top.catalogParentId);
            }
        });
    }

    function deleteGroupMember(groupId, userId) {
        var url = "${ctx}/group/member/delete";
        $.ajax({
            type: "POST",
            url: url,
            data: {
                groupId: groupId,
                userId: userId,
                "token": "<c:out value='${token}'/>"
            },
            error: function (request) {
                switch (request.responseText) {
                    case "NoSuchGroup":
                        top.ymPrompt.close();
                        handlePrompt("error", "<spring:message code='group.error.notexist'/>");
                        refreshList();
                        break;
                    case "Forbidden":
                        top.ymPrompt.close();
                        handlePrompt("error", "<spring:message code='group.error.forbidden'/>");
                        refreshList();
                        break;
                    default:
                        top.ymPrompt.close();
                        handlePrompt("error", "<spring:message code='operation.failed'/>");
                        break;
                }
            },
            success: function (data) {
                if (data.G_OWNER == 1) {
                    handlePrompt("error", "<spring:message code='group.operation.owner'/>");
                } else {
                    handlePrompt("success", "<spring:message code='group.operation.ok'/>");
                }
                initDataList(currentPage, top.catalogParentId);
            }
        });
    }

    function isFolderType(type) {
        return 0 == type || -2 == type || -3 == type;
    }

    function isBackupFolderType(type) {
        return -2 == type || -3 == type;
    }
</script>
</body>
</html>