<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="cse"
           uri="http://cse.huawei.com/custom-function-taglib" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%
    request.setAttribute("token", CSRFTokenManager.getTokenForSession(session));
%>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="../common/common.jsp" %>
    <%@ include file="../files/UploadMessages.jsp" %>
    <%@ include file="../files/commonForFiles.jsp" %>
    <%@ include file="../files/commonForCreateAndRenameNode.jsp" %>
    <%@ include file="../files/commonForUploadFiles.jsp" %>
    <link href="${ctx}/static/skins/default/css/layout.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/static/skins/default/css/filelabel.css" rel="stylesheet" type="text/css"/>

    <script src="${ctx}/static/js/public/JQbox-hw-files.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/JQbox-hw-page.js" type="text/javascript"></script>
</head>
<body>
<!-- <div id="pageLoadingContainer" style="z-index: 10000; position: fixed; left: 0; top: 0; right: 0; bottom: 0; background: #fff;"></div>
<script type="text/javascript">new Spinner(optsBigSpinner).spin(document.getElementById("pageLoadingContainer"));</script>
 --><%@ include file="../common/header.jsp" %>

<div class="public-bar-con">
    <div class="public-layout">
        <div class="public-bar clearfix" id="publicBar">
            <div class="pull-left btn-toolbar">
                <div id="hoverDiv" class="pull-left">
                    <span id="uploadBtnBox" class="pull-left upload-btn-box">
                        <Button class="fileinput-buttonDirforIE" onclick="handleDropMenu()" style="width:99px">
                            <i class="new-icon-toolbar-upload-blue"></i><spring:message code='button.upload'/>
                        </Button>
                    </span>
                    <span id="uploadBtnBoxForJS" class="pull-left upload-btn-box">
                        <span id="spanUpload" class="fileinput-buttonDir" onclick="handleDropMenu()">
                            <span><i class="new-icon-toolbar-upload-blue"></i> <spring:message code='button.upload'/></span>
                        </span>
                    </span>
                </div>

                <button id="newFolderBtn" class="btn btn-primary" type="button" onClick="createFolder()">
                    <i class="new-icon-toolbar-create-folder"></i><spring:message code='button.newFolder'/>
                </button>
                <%--
                <c:if test="${memberInfo.teamRole == 'admin' || memberInfo.teamRole == 'manager' }">
                    <button id="toMemberMgrBtn" class="btn" type="button" onClick="toMemberMgr();">
                        <i class="icon-ownner"></i>
                        <spring:message code='teamSpace.title.memberMgr'/>
                    </button>
                </c:if>
                <c:if test="${memberInfo.teamRole == 'member' }">
                    <button id="toMemberMgrBtn" class="btn" type="button" onClick="toMemberMgr();">
                        <i class="icon-ownner"></i>
                        <spring:message code='teamSpace.title.viewMember'/>
                    </button>
                </c:if>
                --%>
            </div>

            <div class="btn-trash pull-right split-line">
                <a href="${ctx}/teamspace/trash/<c:out value='${teamId}'/>#1" id="btnTrash" class="btn btn-link" title="<spring:message code='button.recycler'/>"><i class="icon-trash"></i></a>
            </div>

            <div class="range-type pull-right split-line">
                <div class="btn-group" data-toggle="buttons-radio">
                    <button id="viewTypeBtnList" onClick="viewTypeSwitch('list')" type="button" class="btn btn-small btn-link" title="<spring:message code='file.tips.list'/>"><i class="icon-list"></i></button>
                    <button id="viewTypeBtnThumbnail" onClick="viewTypeSwitch('thumbnail')" type="button" class="btn btn-small btn-link" title="<spring:message code='file.tips.thumbnail'/>"><i class="icon-th"></i></button>
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

<div class="breadcrumb">
    <div id="test" style="display: none" height="0px"></div>
    <div class="breadcrumb-con clearfix">
        <div class="pull-left folderTreeCon" style="display: none;">
            <input type="hidden" id="teamName" name="teamName" value="<c:out value='${teamName}'/>"/>
            <input type="hidden" id="docTypeName" name="docTypeName" value="<c:out value='${docTypeName}'/>"/>
            <button id="folderTree" type="button" class="btn" title="<spring:message code="file.tips.folder"/>" onclick="initTeamSpaceTree()"><i class="icon-sitemap"></i></button>
            <ul id="treeArea" class="ztree"></ul>
        </div>
        <div class="form-search">
            <div class="input-append">
                <input type="text" class="search-query" id="searchBar" maxlength=246 placeholder="<spring:message code='file.tips.search'/>"/>
                <button class="btn" type="button" onclick="doSearch(1)"><i class="icon-search"></i></button>
            </div>
            <!-- 搜索 -->
            <div style="display: inline-block;">
                <a class="high-search highSearch" onclick="showHighSearchView()">&nbsp;<spring:message code='fl.highsearch.view.title'/></a>
            </div>
        </div>
        <ul id="breadcrumbCon">
        </ul>
    </div>
</div>

<form id="downloadForm" action="" method="get" style="display: none" target=""></form>

<div class="body">
    <div class="body-con clearfix">
        <div class="pull-left clearfix">
            <div id="public">

                <%-- 高级搜索 --%>
                <div class="high-search-container clearfix" style="position: relative;">
                    <div class="high-search-view">
                        <div class="search-top">
                            <div class="input-append search-util fl">
                                <b><spring:message code='fl.highsearch.view.filename'/>：</b>&nbsp;
                                <input type="text" name="highSearchFilename" id="highSearchFilename" class="input-medium keyword search-query" placeholder="<spring:message code='fl.highsearch.view.search.condition.tip'/>">
                                <a class="btn start-search startSearch" onclick="submitHighSearch()"><span class="pipe">|</span><spring:message code='fl.title.begin.highsearch'/></a>
                            </div>
                            <div class="inline-block fr">
                                <a class="exit-search" onclick="exitHighSearch()"><spring:message code='fl.title.exit.highsearch'/></a>
                            </div>
                            <div style="clear: both;"></div>
                        </div>

                        <div class="factors">
                            <b><spring:message code='fl.highsearch.view.search.condition'/>：</b>&nbsp;
                            <div class="inline-block factor-list list-item high-search-type"></div>
                        </div>

                        <ul class="search-selects">
                            <%-- 文件类型 --%>
                            <li class="type search-type">
                                <b class="space"><spring:message code='fl.highsearch.view.type.tag'/></b>&nbsp;
                                <ul class="inline-block search-type-list list-item">
                                    <li class="search-type-item"></li>
                                </ul>
                            </li>

                            <%-- 标签 --%>
                            <li class="labels search-label">
                                <b class="space"><spring:message code='fl.highsearch.view.filelabel.tip'/></b>&nbsp;
                                <div class="expanded-labels inline-block" style="vertical-align: top;">
                                    <div class="collapsed-label-list inline-block list-item enterprise-label-list" style="width:95%;"></div>

                                    <a class="" style="float: right;display: inline;" onclick="queryHighSearchLabels()">
                                        <span title="<spring:message code='fl.bind.view.changepage.btn'/>"><spring:message code='fl.bind.view.changepage.btn'/></span>
                                    </a>
                                </div>
                            </li>
                        </ul>
                    </div>
                </div>

                <div class="files-block clearfix" style="padding-top:0px;margin-top:50px;">
                    <div id="fileListNull" class="file-catalog-null"><spring:message code="file.tips.emptyFolder"/></div>
                    <div id="fileList"></div>
                    <div id="fileListPageBox"></div>

                    <div id="noPermission" class="page-error" style="display: none;">
                        <h3><spring:message code="teamSpace.msg.noPermission"/></h3>
                        <p><spring:message code="teamSpace.msg.contactAdmin"/></p>
                    </div>
                </div>

            </div>
        </div>
    </div>
    <%@ include file="./docfullscreen.jsp" %>
</div>

<%@ include file="../common/footer.jsp" %>
<script type="text/javascript">
    var canPreview =<%=PreviewUtils.isPreview()%>;
    var reqProtocol = "<%=request.getSession().getAttribute("reqProtocol")%>";
    var ownerId = "<c:out value='${teamId}'/>";
    var curUserId = <shiro:principal property="cloudUserId"/>;
    var opts_viewGrid = null;
    var opts_page = null;
    var viewMode = "file";
    var currentPage = 1;
    var catalogParentId = <c:out value='${parentId}'/>;
    var keyword = null;
    var catalogData = null;
    var isSessionTimeout = false;
    var isLinkHidden = <c:out value='${linkHidden}'/>;
    var encode = '<spring:message code="common.language1" />';
    var tempDestOwnerID = null;
    var permissionFlag = null;
    var orderField = getCookie("orderField");
    orderField = orderField == null ? "modifiedAt" : orderField;
    var isOrderField = false;
    var orderFieldArr = new Array("modifiedAt", "name", "size");
    var teamspaceRole = '${memberInfo.teamRole}'; // 空间角色
    var viewType = 2; // 视图模式
    var noPermission = [];
    var docTypeMap = new HashMap(); // doctype
    var failTip = "<spring:message code='operation.failed'/>";

    for (var i = 0; i < orderFieldArr.length; i++) {
        if (orderFieldArr[i] == orderField) {
            isOrderField = true;
            break;
        }
    }
    if (!isOrderField) {
        orderField = "modifiedAt";
    }

    var isDesc = getCookie("isDesc");
    isDesc = isDesc == null ? "true" : isDesc;

    var listViewType = getCookie("listViewType");
    listViewType = listViewType == null ? "list" : listViewType;

    var listFileWidth = ["42.5%", "0%", "30%", "10%", "17.5%"];
    var searchFileWidth = ["24.5%", "33%", "15%", "10%", "17.5%"];
    var headData = {
        "name": {"width": listFileWidth[0]},
        "path": {"width": listFileWidth[1]},
        "modifiedByName": {"width": listFileWidth[2]},
        "size": {"width": listFileWidth[3]},
        "modifiedAt": {"width": listFileWidth[4]}
    };

    var M = {
        m_open: {title: "<spring:message code='button.open'/>", className: "icon-folder-open"},
        m_grant: {title: "<spring:message code='teamSpace.button.btnGrantAuthority'/>", className: "icon-author"},
        m_link: {title: "<spring:message code='button.shareLink'/>", className: "icon-link"},
        m_viewImage: {title: "<spring:message code='button.view'/>", className: "icon-suitcase"}, <%-- 預覽 --%>
        m_move: {title: "<spring:message code='button.copyOrMove'/>", className: "icon-copy"},
        /* m_online_edit: {title: "<spring:message code='button.onlineEdit'/>", className: "icon-pencil"}, */ <%-- 在线编辑 --%>
        m_delete: {title: "<spring:message code='button.delete'/>", className: "icon-cancel"},
        m_rename: {title: "<spring:message code='button.rename'/>", className: "icon-pencil"},
        m_download: {title: "<spring:message code='button.download'/>", className: "icon-download"},
        m_versionList: {title: "<spring:message code='button.listVersion'/>", className: "icon-info"},
        m_saveToMe: {title: '<spring:message code="button.saveToMe"/>', className: "icon-save"},
        m_sendEmail: {title: "<spring:message code='button.sendEmail'/>", className: "icon-email"},
        m_favorite: {
            title: "<spring:message code='button.favorite'/>",
            className: "icon-favorite"
        }            <%-- 添加到收藏夹 --%>
    }

    var showPc = false;

    $(function () {
        beforeInit();
        createMenu();
        queryHighSearchLabels();
        inintHighSearchDocType();

        init();
        userCustomInit();
        processHash();
        // 图片默认以缩略图显示
        if ($("#witchSelect").attr("value") == 2) {
            viewTypeSwitch('thumbnail');
        } else {
            viewTypeSwitch('list');
        }

        window.onhashchange = processHash;

        if ("${modelDocType}" == "") {
            navMenuSelected("navAllFile");
            $("#teamSpaceDoctypetree").css("background-position", "-10px 0");
        } else {
            //navMenuSelected("docType${modelDocType}");
            menutree = "docType${modelDocType}";
        }
        navMenuSelected("navTeamSpace");

        $('div.high-search-type').on('click', 'i.icon-custom-remove', removeSelectedItem);
    });

    var vlabelIds;
    var vdocType;
    function processHash() {
        var hash = window.location.hash;
        if (hash.indexOf('#') != -1) {
            var m = hash.substring(hash.indexOf("#") + 1);
            if (!m || m == 'none') {
                return;
            }
            var orgStr = m;
            m = m.split('/');
            viewMode = m[0];
            currentPage = m[1];
            var arrLen = m.length;

            if (viewMode == "file") {
                catalogParentId = m[2];
            } else if (viewMode == "highSearch") {
                keyword = m[2];
                vdocType = m[3];
                vlabelIds = m[4];
                $("#searchBar").val("");
            } else {
                if (m.length > 2) {
                    keyword = m[2];
                } else {
                    keyword = orgStr.substring(viewMode.length + currentPage.length + 2);
                }
                $("#searchBar").val(keyword);
                $('#highSearchFilename').val("");
            }
        } else {
            viewMode = "file";
            currentPage = 1;
            catalogParentId = 0;
        }

        switch (viewMode) {
            case 'file':
                listFile(currentPage, catalogParentId);
                break;
            case 'highSearch':
                doSearch(currentPage, keyword, vdocType, vlabelIds);
                break;
            default:
                doSearch(currentPage, keyword);
        }
    }

    function changeHash(viewMode, curPage, parentId, docType, labelIds) {
        var pageHash = "#" + viewMode + "/" + curPage + "/" + parentId;

        if (viewMode == 'highSearch') {
            if (docType || labelIds) {
                pageHash += "/";
                if (docType) {
                    pageHash += docType;
                }

                pageHash += "/";
                if (labelIds) {
                    pageHash += labelIds;
                }
            }
        }

        location.hash = pageHash;
    }

    function init() {
        loadSysSetting("${ctx}", '<spring:message code="common.language1" />');

        initViewType(listViewType);
        opts_viewGrid = $("#fileList").comboGrid({
            headData: headData,
            dataId: "id",
            keyDelete: true,
            keyF2: true,
            viewType: listViewType,
            isContainFilelabel: true
        });
        initSearchEvent();
        sendUpLoad();
        opts_page = $("#fileListPageBox").comboPage({
            showPageSet: true, lang: '<spring:message code="common.language1"/>'
        });
        $.fn.comboPage.pageSkip = function (opts, _idMap, curPage) {
            if (viewMode == "file") {
                changeHash(viewMode, curPage, catalogParentId);
            } else if (viewMode == "highSearch") {
                changeHash(viewMode, curPage, keyword, vdocType, vlabelIds);
            } else {
                changeHash(viewMode, curPage, keyword);
            }
        };

        $.fn.comboPage.setPerPageNum = function (opts, _idMap, num) {
            setRootCookie("fileListPerPageNum", num);
            if (viewMode == "file") {
                listFile(currentPage, catalogParentId);
            } else if (viewMode == "highSearch") {
                doSearch(currentPage, keyword, vdocType, vlabelIds);
            } else {
                doSearch(currentPage, keyword);
            }
        };

        $.fn.comboGrid.setItemOp = function (rowData, colTag, colIndex) {
            var Permission;
            switch (colIndex) {
                case 0:
                    try {
                        var alink, version, dataHandle, shareHandle, virusHandle, linkHandle, syncHandle,
                            itemVal = colTag.find("span").text();
                        dataHandle = $("<span class='file-handle'></span>");
                        colTag.find("span").remove();

                        if (rowData.type == 0) {
                            Permission = getNodePermission(rowData.id);
                            var p = 0;
                            for (var i in Permission) {
                                //console.debug(i);
                                p = p + Permission[i];
                            }
                            if (p == 0) {
                                alink = "<a id='" + rowData.id + "' onclick=''></a>";
                                colTag.append(alink);
                                if (listViewType == "list") {
                                    $("#file-icon_" + rowData.id).css("background-position", "-235px -150px");
                                } else {
                                    $("#file-icon_" + rowData.id).css("background-position", "-135px -110px");
                                }
                                noPermission.push("file-icon_" + rowData.id);
                                var parentli = $("#file-icon_" + rowData.id).parent();
                                parentli.setControl(false);
                            } else {
                                alink = "<a id='" + rowData.id + "' onclick='openFolder(" + rowData.id + ")'></a>";
                                colTag.append(alink);
                            }
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
                            var fileItemDiv = $('<div class="file-info"></div>');
                            var fileLinkDiv = $('<div class="file-alink"></div>');
                            var fileMarksDiv = $('<div class = "marks"></div>');
                            var filelableListDiv = $('<div class="label-list" fid = "' + rowData.id + '"></div>');

                            fileLinkDiv.append(alink);
                            if (rowData.type == 1 && rowData.versions > 1) {
                                version = "<i class='file-version' title='<spring:message code='file.tips.fileVersion'/>' onclick='enterVersionList(" + rowData.id + ")'>V" + rowData.versions + "</i>";
                                fileLinkDiv.append(version);
                            }

                            if (rowData.fileLabels) {
                                for (var index in rowData.fileLabels) {
                                    var tempFl = rowData.fileLabels[index];
                                    var tempLabelSpan = '<span class="label" flid = "' + tempFl.id + '" data-original-title = "' + tempFl.labelName + '">'
                                        + getSpecialLengthString(tempFl.labelName, 5) +
                                        '<a class="delMark del-mark float-clear" onclick=unbindFileLabel(this)><span class = "icon-custom-del-mark"></span></a></span>';

                                    filelableListDiv.append(tempLabelSpan);
                                }
                            }

                            fileMarksDiv.append(filelableListDiv);
                            var filelableAddTag = '<a class="float-clear label-bind" style = "float:none;" onclick="showBindFileLabelUI(' + rowData.id
                                + ', ' + rowData.createdBy + ')"><span class="icon-custom-mark"></span></a>';
                            fileMarksDiv.append(filelableAddTag);

                            fileItemDiv.append(fileLinkDiv);
                            fileItemDiv.append(fileMarksDiv);
                            colTag.append(fileItemDiv);

                            if (listViewType && listViewType == 'thumbnail') {
                                fileItemDiv.closest("ul").closest("li").removeClass("file-item");
                                fileMarksDiv.hide();
                            } else {
                                fileItemDiv.closest("ul").closest("li").addClass("file-item");
                                fileMarksDiv.show();
                            }

                            $('div.label-list').on('mouseover mouseout', 'span.label', function (event) {
                                if (event.type == "mouseover") {
                                    if (teamspaceRole == 'admin' || teamspaceRole == 'manager' || rowData.createdBy == curUserId) {
                                        $(this).find("a.delMark").show();
                                    }

                                } else if (event.type == "mouseout") {
                                    $(this).find("a.delMark").hide();
                                }
                            });
                        }
                        $("#" + rowData.id).text(itemVal);
                        $("#" + rowData.id).attr("title", itemVal);

                        var isVirus = false;
                        if (rowData.virusStatus == 1) {
                            isVirus = true;
                            virusHandle = "<i class='icon-virus no-set' title='<spring:message code='button.virus'/>'></i>";
                        }

                        if (rowData.linkStatus == '' || rowData.linkStatus == null) {
                            linkHandle = "<i class='icon-link no-set' title='<spring:message code='button.shareLink'/>' onclick='shareLink(" + rowData.id + ")'></i>";
                        } else {
                            linkHandle = "<i class='icon-link' title='<spring:message code='button.shareLink'/>' onclick='shareLink(" + rowData.id + ")'></i>";
                        }

                        if (isVirus) {
                            dataHandle.append(virusHandle);
                        }

                        if (!isLinkHidden) {
                            dataHandle.append(linkHandle);
                        }

                        //添加预览图标
                        if (isViewType(rowData)) {
                            viewHandle = "";
                            var fileName = '"' + rowData.name + '"';
                            viewHandle = "<i class='icon-suitcase no-set' title='<spring:message code='button.view'/>' onclick='viewImg(" + rowData.id + "," + fileName + " )'></i>";
                            dataHandle.append(viewHandle);
                        } else {
                            viewHandle = "";
                        }

                        colTag.append(version);
                        colTag.append(dataHandle);
                    } catch (e) {
                    }
                    break;
                case 1:
                    var itemVal = colTag.find("span").text();
                    colTag.find("span").attr("title", itemVal);
                    break;
                case 2:
                    if (rowData.modifiedByName == undefined) {
                        colTag.find("span").text("");
                    } else {
                        colTag.find("span").text("<spring:message code='teamSpace.label.modifiedByName'/> " + rowData.modifiedByName);
                        colTag.find("span").attr("title", colTag.find("span").text());
                    }
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
                for (var key in M) M[key].divider = false;

                if (allSelectData.length == 0) {

                } else if (allSelectData.length == 1) {
                    var item = allSelectData[0];
                    var nodePermission = getNodePermission(item.id);
                    var p = 0;
                    for (var i in nodePermission) {
                        p = p + nodePermission[i];
                    }
                    if (item.type == 0) {
                        menuData = {
                            "m_open": M.m_open,
                            "m_link": M.m_link,
                            "m_grant": M.m_grant,
                            "m_move": M.m_move,
                            "m_delete": M.m_delete,
                            "m_rename": M.m_rename,
                            "m_saveToMe": M.m_saveToMe,
                            "m_sendEmail": M.m_sendEmail,
                            "m_favorite": M.m_favorite
                        };
                        if (p == 0) {
                            delete menuData.m_open;
                        }
                        if (nodePermission["authorize"] == 0) {
                            delete menuData.m_grant;
                        }

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
                            "m_viewImage": M.m_viewImage,
                            "m_download": M.m_download,
                            "m_move": M.m_move, /* "m_online_edit": M.m_online_edit, */
                            "m_delete": M.m_delete,
                            "m_rename": M.m_rename,
                            "m_saveToMe": M.m_saveToMe,
                            "m_versionList": M.m_versionList,
                            "m_sendEmail": M.m_sendEmail,
                            "m_favorite": M.m_favorite
                        };
                        if (nodePermission["edit"] == 0) {
                            delete menuData.m_move;
                            /* delete menuData.m_online_edit; */
                            delete menuData.m_rename;
                            delete menuData.m_delete;
                        }
                        /* if (!isOnlineEditable(item))
                         {
                         delete menuData.m_online_edit;
                         } */
                        if (nodePermission["download"] == 0) {
                            delete menuData.m_download;
                            delete menuData.m_saveToMe;
                        }
                        if (nodePermission["publishLink"] == 0) {
                            delete menuData.m_link;
                        }
                        // 是否预览
                        if (!isViewType(item)) {
                            delete menuData.m_viewImage;
                        }
                    }
                    if (isLinkHidden) {
                        delete menuData.m_link;
                    }
                } else {
                    menuData = {
                        "m_move": M.m_move,
                        "m_delete": M.m_delete,
                        "m_saveToMe": M.m_saveToMe,
                        "m_sendEmail": M.m_sendEmail
                    };
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
                if (dataList[0].type == 0) {
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
                title: title, message: "<spring:message code='file.info.sureToMoveWithAcl'/>", handler: function (tp) {
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
            if (rowData.type == 0) {
                openFolder(rowData.id);
            } else if ("img" == _getStandardType(rowData.name)) {
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
    function viewTypeSwitch(type) {
        $("#fileList").switchViewType(type, opts_viewGrid);
        for (var i = 0; i < noPermission.length; i++) {
            if (type == "list") {
                $("#" + noPermission[i]).css("background-position", "-235px -150px");
            } else {
                $("#" + noPermission[i]).css("background-position", "-135px -110px");
            }
        }
        listViewType = type;
        setCookie("listViewType", listViewType);
    }

    function initListButton(allSelectData) {
        var listButtonData = null;
        if (allSelectData.length == 0) {
            $("#listHandler").remove();
        } else if (allSelectData.length == 1) {
            var nodePermission = getNodePermission(allSelectData[0].id);
            if (allSelectData[0].type == 0) {
                listButtonData = {
                    "m_link": M.m_link,
                    "m_grant": M.m_grant,
                    "m_move": M.m_move,
                    "m_delete": M.m_delete,
                    "m_rename": M.m_rename,
                    "m_saveToMe": M.m_saveToMe,
                    "m_sendEmail": M.m_sendEmail,
                    "m_favorite": M.m_favorite
                };

                if (nodePermission["authorize"] == 0) {
                    delete listButtonData.m_grant;
                }
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
            } else {
                listButtonData = {
                    "m_link": M.m_link,
                    "m_viewImage": M.m_viewImage,
                    "m_download": M.m_download,
                    "m_move": M.m_move, /* "m_online_edit":M.m_online_edit, */
                    "m_delete": M.m_delete,
                    "m_rename": M.m_rename,
                    "m_saveToMe": M.m_saveToMe,
                    "m_versionList": M.m_versionList,
                    "m_sendEmail": M.m_sendEmail,
                    "m_favorite": M.m_favorite
                };
                if (nodePermission["edit"] == 0) {
                    delete listButtonData.m_move;
                    /* delete listButtonData.m_online_edit; */
                    delete listButtonData.m_rename;
                    delete listButtonData.m_delete;
                }
                /* if(!isOnlineEditable(allSelectData[0]))
                 {
                 delete listButtonData.m_online_edit;
                 } */
                if (nodePermission["download"] == 0) {
                    delete listButtonData.m_saveToMe;
                    delete listButtonData.m_download;
                }
                if (nodePermission["publishLink"] == 0) {
                    delete listButtonData.m_link;
                }
                // 是否预览
                if (!isViewType(allSelectData[0])) {
                    delete listButtonData.m_viewImage;
                }
            }

            if (isLinkHidden) {
                delete listButtonData.m_link;
            }
        } else {
            listButtonData = {
                "m_move": M.m_move,
                "m_delete": M.m_delete,
                "m_saveToMe": M.m_saveToMe,
                "m_sendEmail": M.m_sendEmail
            };
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
        $("#publicBar").append(listBtnDiv);
        var index = 0;
        for (var key in listButtonData) {
            var menuItem = $('<button onclick="executeHandler(\'' + key + '\')" class="btn btn-link" type="button" id="listBtn_' + key + '"><i class="' + listButtonData[key].className + '"></i> ' + listButtonData[key].title + '</button>');
            listBtnDiv.append(menuItem);
            index++;
        }

        if (index < 3) {
            return;
        }
        var moreBtnGroup = $('<div class="dropdown" id="moreBtnGroup">' +
            '<a class="btn btn-link" data-toggle="dropdown"><i class="icon-more"></i><spring:message code="common.more"/></a>' +
            '<ul class="dropdown-menu" id="moreBtnList"></ul>' +
            '</div>');
        listBtnDiv.append(moreBtnGroup);
        for (var key in listButtonData) {
            var moreBtnItem = $('<li><a onclick="executeHandler(\'' + key + '\');" id="moreListBtn_' + key + '"><i class="' + listButtonData[key].className + '"></i> ' + listButtonData[key].title + '</a></li>');
            $("#moreBtnList").append(moreBtnItem);
        }

        var len = $("#listHandler").find(">button").length, maxW = $("#listHandler").width();
        for (var n = 0; n < len; n++) {
            if (n < 1) {
                $("#moreBtnList").find("li").eq(n).hide();
            } else {
                $("#listHandler").find(">button").eq(n).hide();
                $("#moreBtnList").find("li").eq(n).show();
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
                /* } else if ("m_online_edit" == btnType) {
                 onlineEdit(); */
            } else if ("m_delete" == btnType) {
                deleteNodes();
            } else if ("m_versionList" == btnType) {
                enterVersionList();
            } else if ("m_grant" == btnType) {
                grantAuthority();
            } else if ("m_saveToMe" == btnType) {
                saveToMe();
            } else if ("m_sendEmail" == btnType) {
                sendEmail();
            } else if ("m_favorite" == btnType) {
                var dataList = $("#fileList").getGridSelectedData(catalogData,
                    opts_viewGrid);
                if (dataList.length == 0) {
                    return;
                } else if (dataList.length == 1) {
                    var iNodeId = dataList[0].id;
                    var ownedBy = dataList[0].ownedBy;
                    var parentId = dataList[0].parentId
                    var nodeType = dataList[0].type
                    var name = dataList[0].name
                    createFavorite("teamspace", ownedBy, iNodeId, nodeType, name, parentId);
                }
            } else if ("m_viewImage" == btnType) {
                viewImg();
            }
        } catch (e) {
            return;
        }
    }

    function openFolder(id) {
        if (id == null || id == "") {
            var objData = $("#fileList").getGridSelectedData(catalogData, opts_viewGrid);
            id = objData[0].id;
        }
        new Spinner(optsMiddleSpinner).spin($("#" + id).parents(".rowli").find("> div").get(0));

        var nodePermission = getNodePermission(id);
        if (nodePermission["browse"] != 1) {
            handlePrompt("error", "<spring:message code='error.forbid'/>");
            return;
        }
        changeHash("file", 1, id);
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
            url: "${ctx}/files/getDownloadUrl/" + ownerId + "/" + id + "?" + Math.random(),
            error: function (request) {
                downloadFileErrorHandler(request);
                if (viewMode == "file") {
                    listFile(currentPage, catalogParentId);
                } else {
                    doSearch();
                }
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
            url: "${ctx}/files/getUrlAndBrowse/" + ownerId + "/" + id + "?" + Math.random(),
            error: function (request) {
                downloadFileErrorHandler(request);
                if (viewMode == "file") {
                    listFile(currentPage, catalogParentId);
                } else {
                    doSearch();
                }
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
            window.open("${ctx}/files/gotoPreview/" + ownerId + "/" + fileId + "?parentPageType=teamspace");
        } else {
            downloadFile(fileId, nodePermission);
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
                    var responseObj = $.parseJSON(request.responseText);
                    if (responseObj.code == "VirusForbidden") {
                        top.handlePrompt("error", "<spring:message code='file.errorMsg.operationNotAllowedForVirusDetected'/>");
                        return;
                    }
                    top.handlePrompt("error", "<spring:message code='operation.failed'/>");
                }
            },
            success: function (data) {
                top.inLayerLoading("<spring:message code='common.task.doing'/>", "loading-bar");
                top.asyncListen("move", ownerId, targetId, data);
            }
        });
    }

    function shareMyFolder(nodeIdTmp) {
        var inodeId = nodeIdTmp;
        if (inodeId == undefined) {
            var node = $("#fileList").getGridSelectedData(catalogData, opts_viewGrid);
            inodeId = node[0].id;
        }

        var url = '${ctx}/share/folder/' + ownerId + '/' + inodeId;
        top.ymPrompt.win({
            message: url,
            width: 650,
            height: 375,
            title: "<spring:message code='file.title.share'/>",
            iframe: true,
            handler: shareHandle
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

    //#####################################
    var flag = "0";
    var url = "";
    var curPage = 1;
    var curTotalPage = 1;
    var curNodeId;
    var cutOwnerId;
    function viewImg(nodeIdTmp, flag, fileName) {
        var inodeId = nodeIdTmp;

        if (inodeId == undefined) {
            var node = $("#fileList").getGridSelectedData(catalogData, opts_viewGrid);
            inodeId = node[0].id;
        }
        curNodeId = inodeId;
        cutOwnerId = ownerId;

        $.ajax({
            type: "GET",
            async: false,
            url: "${ctx}/views/getViewFlag/" + ownerId + "/" + inodeId + "?" + Math.random(),
            success: function (data) {
                var data = $.parseJSON(data);
                flag = data.viewFlag;
                if (data.isSizeLarge) {
                    ymPrompt.alert({
                        title: fileName,
                        message: "<spring:message code='preview.isSizeLarge'/>",
                    });
                    return;
                }
                url = '${ctx}/views/viewInfo/' + ownerId + '/' + inodeId + '/' + flag;
                if (parseInt(flag) != 2) {
                    ymPrompt.alert({
                        title: fileName,
                        message: "<spring:message code='preview.getPageView'/>",
                    });
                }
            }
        });
        if (parseInt(flag) == 2) {
            $.ajax({
                type: "GET",
                async: false,
                url: "${ctx}/views/viewMetaInfo/" + ownerId + "/" + inodeId + "/" + "1",
                error: function (request) {
                    doDownLoadLinkError(request);
                },
                success: function (data) {

                    var previewUrl = data.url;
                    curPage = data.curPage;
                    curTotalPage = data.totalPage;
                    $("#doc_view_current_page").val(data.curPage);
                    $("#doc_view_totap_page").html(data.totalPage);
                    document.getElementById("doc_ppt_img").src = previewUrl;
                    $("#index_layer2").css("display", "block");
                    $("#filedoc").css("display", "block");

                }
            });
        }
    }
    //#####################################


    function linkHandle(tp) {
        if (tp == "close") {
            top.ymPrompt.getPage().contentWindow.callBackDelLink();
        }
        $("body").css("overflow", "scroll");
        top.ymPrompt.close();
        if (viewMode == "file") {
            listFile(currentPage, catalogParentId);
        } else {
            doSearch();
        }
    }

    function copyAndMove() {
        tempDestOwnerID = ownerId;
        var url = "${ctx}/teamspace/file/copyAndMove/" + ownerId + "?startPoint=teamspace&endPoint=teamspace";
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
        $("#btnMove").attr("title", "<spring:message code='file.info.moveAclTips'/>");
    }

    function doCopyAndMove(tp) {
        var idArray = $("#fileList").getGridSelectedId();
        if (tp == 'copy' || tp == 'move') {
            top.ymPrompt.getPage().contentWindow.submitCopyAndMove(tp, ownerId, idArray);
        } else if (tp == 'newFolder') {
            top.ymPrompt.getPage().contentWindow.checkCreateFolder();
        } else {
            top.ymPrompt.close();
        }
    }

    function saveToMe() {
        tempDestOwnerID = curUserId;
        var url = "${ctx}/nodes/copyAndMove/" + ownerId + "?startPoint=teamspace&endPoint=operative";
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

    function sendEmail() {
        var teamId = "<c:out value='${teamId}'/>";
        var url = "${ctx}/teamspace/sendEmail/" + teamId;
        top.ymPrompt.win({
            message: url,
            width: 600,
            height: 280,
            title: '<spring:message code="teamSpace.title.sendEmail"/>',
            iframe: true,
            btn: [['<spring:message code="button.send"/>', 'sendEmail', false, "btn-focus"], ['<spring:message code="button.cancel"/>', 'no', true, "btn-cancel"]],
            handler: doSendEmail
        });
        top.ymPrompt_addModalFocus("#btn-focus");
    }

    function doSendEmail(tp) {
        var idArray = $("#fileList").getGridSelectedId();
        var teamId = "<c:out value='${teamId}'/>";
        if (tp == 'sendEmail') {
            top.ymPrompt.getPage().contentWindow.submitSentEmail(tp, ownerId, teamId, idArray);
        }
        else {
            top.ymPrompt.close();
        }
    }

    function doSaveToMe(tp) {
        var idArray = $("#fileList").getGridSelectedId();
        if (tp == 'copy') {
            var objData = $("#fileList").getGridSelectedData(catalogData, opts_viewGrid);
            top.ymPrompt.getPage().contentWindow.submitCopyAndMove(tp, ownerId, idArray);
        } else if (tp == 'newFolder') {
            top.ymPrompt.getPage().contentWindow.createFolder();
        } else {
            top.ymPrompt.close();
        }
    }

    function enterVersionList(nodeId) {
        var id = nodeId;
        if (nodeId == "" || nodeId == null) {
            var node = $("#fileList").getGridSelectedData(catalogData, opts_viewGrid);
            id = node[0].id;
        }

        var url = "${ctx}/files/listVersion/" + ownerId + "/" + id + "?parentPageType=teamspace";
        top.ymPrompt.win({
            title: "<spring:message code='file.title.versionList'/>",
            message: url,
            width: 650,
            height: 390,
            iframe: true,
            btn: [["<spring:message code='button.close'/>", "no", true, "btn-cancel"]]
        });
    }

    function gotoShareMyFolderError(status) {
        if (status == 404) {
            handlePrompt("error", "<spring:message code='error.notfound'/>");
            ymPrompt.close();
            if (viewMode == "file") {
                listFile(currentPage, catalogParentId);
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
                            ymPrompt.getPage().contentWindow.handlePrompt("error", "<spring:message code='error.forbid'/>", '', 5);
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
                        url = "${ctx}/nodes/renameCopy/" + srcOwnerId + "?startPoint=teamspace&endPoint=teamspace";
                    } else {
                        url = "${ctx}/nodes/renameMove/" + srcOwnerId + "?startPoint=teamspace&endPoint=teamspace";
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


    String.prototype.endsWith = function (suffix) {
        return this.indexOf(suffix, this.length - suffix.length) !== -1;
    }


    /* // 判断文件是否可以在线编辑
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
     */

    // 在线编辑
    /* function onlineEdit() {
     var file = $("#fileList").getGridSelectedData(catalogData, opts_viewGrid)[0];
     var parentId = file.parentId, fileId = file.id;
     $('<form action="${ctx}/files/onlineEdit/' + ownerId +  "/" + parentId + '/' + fileId + '" target="_blank"></form>').appendTo('body').submit().remove();
     } */


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
        var p = 0;
        for (var i in permissionFlag) {
            //console.debug(i);
            p = p + permissionFlag[i];
        }
        if (p == 0) {
            alert("<spring:message code='error.forbid'/>");
            //	handlePrompt("error","<spring:message code='error.forbid'/>");
            window.location = window.location.protocol + "//" + window.location.host + "${ctx}/teamspace";
        }

        var pagePerDis = getCookie("fileListPerPageNum");
        pagePerDis = (pagePerDis == null || pagePerDis == '' || pagePerDis == undefined) ? 40 : pagePerDis;
        viewMode = "file";
        var url = "${ctx}/folders/list";
        var params = {
            "ownerId": ownerId,
            "parentId": parentId,
            "pageNumber": curPage,
            "pageSize": pagePerDis,
            "orderField": orderField,
            "desc": isDesc,
            "token": "<c:out value='${token}'/>"
        };

        $("#teamId").attr("value", ownerId);
        if ('' != '${modelDocType}') {

            url = "${ctx}/folders/listByDocType";
            params = {
                "ownerId": ownerId,
                "docType": '${modelDocType}',
                "pageNumber": curPage,
                "pageSize": pagePerDis,
                "orderField": orderField,
                "desc": isDesc,
                "token": "<c:out value='${token}'/>"
            };

            $("#newFolderBtn").remove();
            $(".upload-btn-box").remove();
            $("#witchSelect").attr("value", '${modelDocType}');
            if ('${modelDocType}' == '2') {

                $("#viewTypeBtnThumbnail").addClass("active");
                $("#viewTypeBtnList").removeClass("active");
            }
            else {
                $("#viewTypeBtnList").addClass("active");
                $("#viewTypeBtnThumbnail").removeClass("active");
            }


        } else {
            $("#witchSelect").attr("value", "0"); //回复默认值

            $("#viewTypeBtnList").addClass("active");
            $("#viewTypeBtnThumbnail").removeClass("active");
        }

        if (permissionFlag != null && permissionFlag["browse"] == 1) {
            $.ajax({
                type: "POST",
                url: url,
                data: params,
                error: function (request) {
                    if (request.status == 404) {
                        ymPrompt.alert({
                            title: "<spring:message code='common.tip'/>",
                            message: "<spring:message code='error.notfound'/>",
                            handler: function () {
                                window.location = window.location.protocol + "//" + window.location.host + "${ctx}";
                            }
                        });

                    } else if (request.status == 403) {
                        ymPrompt.alert({
                            title: "<spring:message code='common.tip'/>",
                            message: "<spring:message code='error.forbid'/>",
                            handler: function () {
                                window.location = window.location.protocol + "//" + window.location.host + "${ctx}/teamspace";
                            }
                        });

                    } else {
                        handlePrompt("error", "<spring:message code='file.errorMsg.listFileFailed'/>");
                    }
                },
                success: function (data) {
                    //高亮团队空间菜单当文件列表属于团队空间菜单下时
                    $(".current").parent()[0].id === "userDoctypetree" || $(".current").parent()[0].id === "teamSpaceDoctypetree" ? "" : navMenuSelected("navTeamSpace");

                    $("#noPermission").hide();
                    catalogData = data.content;

                    if (catalogData.length == 0 && curPage != 1) {
                        curPage--;
                        changeHash("file", curPage, parentId);
                        return;
                    }

                    headData.name.width = listFileWidth[0];
                    headData.path.width = listFileWidth[1];
                    headData.modifiedByName.width = listFileWidth[2];
                    headData.size.width = listFileWidth[3];
                    headData.modifiedAt.width = listFileWidth[4];

                    $("#fileList").setGridData(catalogData, opts_viewGrid);
                    $("#fileListPageBox").setPageData(opts_page, data.number, data.size, data.totalElements);
                    buttonInit();
                    if ('' != '${modelDocType}') {
                        createBreadcrumbForDoctypeTeamSpace(ownerId);
                    } else {
                        createBreadcrumbForTeamSpace(parentId, ownerId);
                    }
                    if (catalogData.length == 0) {
                        $("#fileListNull").show();
                    }

                    catalogParentId = parentId;
                    comboxRemoveLoading("pageLoadingContainer");
                }
            });
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


    function listMembers() {
        $.ajax({
            type: "GET",
            url: "${ctx}/teamspace/searchMembers",
            data: {},
            error: function (request) {
            },
            success: function (data) {
                var members = data.content;
                var cnt = "";
                for (var i = 0; i < members.length; i++) {
                    cnt += "<div class='margin_top_10'>";
                    if (members[i].resourceType == 0) {
                        cnt += "<div class='member_ico'>&nbsp;</div>";
                    } else if (members[i].resourceType == 1) {
                        cnt += "<div class='member_ico_owner'>&nbsp;</div>";
                    } else {
                        cnt += "<div class='member_ico_normal'>&nbsp;</div>";
                    }
                    cnt += "<span class='memberName'>&nbsp;" + members[i].name + "&nbsp;&nbsp; </span><span class='memberId'>(" + members[i].loginName + ")</span></div>";
                }

                $("#teamMembers").html(cnt);
            }
        });
    }

    function toMemberMgr() {
        var url = "${ctx}/teamspace/member/" + ownerId;
        $.ajax({
            type: "GET",
            url: url,
            error: function (request) {
                handlePrompt("error", "<spring:message code='teamSpace.error.Forbidden'/>");
            },
            success: function (data) {
                if (typeof(data) == 'string' && data.indexOf('<html>') != -1) {
                    window.location.href = "${ctx}/logout";
                    return;
                }
                var memberinfo = data;
                var title = "";
                var btn = [];
                var height = 0;
                if (memberinfo.teamRole == 'admin' || memberinfo.teamRole == 'manager') {
                    title = '<spring:message code="teamSpace.title.memberMgr"/>';
                } else {
                    title = '<spring:message code="teamSpace.title.viewMember"/>';
                }
                var url = '${ctx}/teamspace/member/openMemberMgr/' + ownerId;
                top.ymPrompt.win({
                    message: url,
                    width: 720,
                    height: 450,
                    title: title,
                    iframe: true
                });
            }
        });

    }

    function grantAuthority() {
        var node = $("#fileList").getGridSelectedData(catalogData, opts_viewGrid);

        var url = "${ctx}/teamspace/member/" + ownerId;
        $.ajax({
            type: "GET",
            url: url,
            error: function (request) {
                handlePrompt("error", "<spring:message code='teamSpace.error.Forbidden'/>");
            },
            success: function (data) {
                if (typeof(data) == 'string' && data.indexOf('<html>') != -1) {
                    window.location.href = "${ctx}/logout";
                    return;
                }
                var url = '${ctx}/teamspace/file/grantAuthority/' + ownerId + '/' + node[0].id;
                top.ymPrompt.win({
                    title: "<spring:message code='teamSpace.title.grantAuthority'/>",
                    width: 720,
                    height: 550,
                    message: url,
                    iframe: true
                });
            }
        });
    }

    function initSearchEvent() {
        $("#searchBar").keydown(function () {
            var evt = arguments[0] || window.event;
            if (evt.keyCode == 13) {
                doSearch(1);
                if (window.event) {
                    window.event.cancelBubble = true;
                    window.event.returnValue = false;
                } else {
                    evt.stopPropagation();
                    evt.preventDefault();
                }
            }
        });
    }

    // 新增检索条件：labelIds, docType
    function doSearch(curPage, keyword, docType, labelId) {
        if (labelId || docType) {
            viewMode = "highSearch";
        } else {
            viewMode = "search";
        }

        if (curPage == undefined) {
            curPage = currentPage;
        }

        var currSearchType = -1;
        if (viewMode == "highSearch") {
            keyword = keyword ? keyword.trim() : "";
            currSearchType = 1;
        } else {
            if (keyword == undefined) {
                keyword = $("#searchBar").val().trim();
            }
            if (keyword == "" || keyword == null) {
                $("#searchBar").val("");
                return;
            }
            currSearchType = 0;
        }

        var searchSpiner = new Spinner(optsSmallSpinner).spin($("#searchBar").parent().get(0));

        var pagePerDis = getCookie("fileListPerPageNum");
        pagePerDis = (pagePerDis == null || pagePerDis == '' || pagePerDis == undefined) ? 40 : pagePerDis;
        var requestDocType = obtainRequestParam('teamDocType');
        if (requestDocType) {
            docType = requestDocType;
        }

        var url = "${ctx}/nodes/search";
        var params = {
            "ownerId": ownerId,
            "name": keyword,
            "pageNumber": curPage,
            "pageSize": pagePerDis,
            "orderField": orderField,
            "desc": isDesc,
            "token": "<c:out value='${token}'/>",
            "labelIds": labelId,
            "docType": docType,
            "searchType": currSearchType
        };

        var isSwitchSelect = -1 != $("#witchSelect").attr("value") && 0 != $("#witchSelect").attr("value");
        var isHighsearchAndCustomDefineDoctype = currSearchType && docType && docType > 0 && isCustomDefineDoctype(docType);
        if (isSwitchSelect || isHighsearchAndCustomDefineDoctype) {
            url = "${ctx}/nodes/searchByDocType";
            var tpdocType = '';
            if (isSwitchSelect) {
                tpdocType = $("#witchSelect").attr("value");
            } else if (isHighsearchAndCustomDefineDoctype) {
                tpdocType = docType;
            }

            var params = {
                "ownerId": ownerId,
                "docType": tpdocType,
                "name": keyword,
                "pageNumber": curPage,
                "orderField": orderField,
                "pageSize": pagePerDis,
                "desc": isDesc,
                "token": "<c:out value='${token}'/>",
                "labelIds": labelId,
                "searchType": currSearchType
            };
        }

        $.ajax({
            type: "POST",
            url: url,
            data: params,
            error: function (request) {
                var status = request.status;
                if (status == 403) {
                    handlePrompt("error", "<spring:message code='error.forbid'/>");
                } else {
                    handlePrompt("error", "<spring:message code='operation.failed'/>");
                }
                searchSpiner.stop();
            },
            success: function (data) {
                searchSpiner.stop();
                catalogData = data.content;

                if (catalogData.length == 0 && curPage != 1) {
                    curPage--;
                    changeHash(viewMode, curPage, keyword);
                    return;
                }

                headData.name.width = searchFileWidth[0];
                headData.path.width = searchFileWidth[1];
                headData.modifiedByName.width = searchFileWidth[2];
                headData.size.width = searchFileWidth[3];
                headData.modifiedAt.width = searchFileWidth[4];

                $("#fileList").setGridData(catalogData, opts_viewGrid);
                $("#fileListPageBox").setPageData(opts_page, data.number, data.size, data.totalElements);
                changeHash(viewMode, curPage, keyword, docType, labelId);
                buttonInit();
                var beingShowNums = 0;
                if (data.totalElements) {
                    beingShowNums = data.totalElements;
                } else if (data.numberOfElements) {
                    beingShowNums = data.numberOfElements;
                }

                createBreadcrumbForTeamSpace(0, ownerId, beingShowNums);
                comboxRemoveLoading("pageLoadingContainer");
            }
        });

    }

    function buttonInit() {
        if (viewMode == "file") {
            $(".btn-trash").hide();
            $("#newFolderBtn").hide();
            $(".upload-btn-box").css({"overflow": "hidden", "width": "0", "height": "0"});
            if (!permissionFlag) {
                return;
            }

            if (permissionFlag["authorize"] == 1) {
                $(".btn-trash").show();
            }
            if (permissionFlag["edit"] == 1) {
                $("#newFolderBtn").show();
            }
            if (permissionFlag["upload"] == 1) {
                $(".upload-btn-box").removeAttr("style");
            }

        } else {
            $(".btn-trash").show();
            $("#newFolderBtn").hide();
            $(".upload-btn-box").css({"overflow": "hidden", "width": "0", "height": "0"});
        }
    }

    function gotoSpaceError(exceptionName) {
        if (exceptionName == "Forbidden") {
            handlePrompt("error", "<spring:message code='teamSpace.error.Forbidden'/>");
        } else if (exceptionName == "NoSuchTeamSpace") {
            handlePrompt("error", "<spring:message code='teamSpace.error.NoFound'/>");
        }
        ymPrompt.close();
    }

    /******************************************************************************************文件夹上传1.0***************************************************************************************************/

    //构造文件夹上传下拉菜单
    function createMenu() {
        if (isFirFoxBrowser() || (!$.support.leadingWhitespace || navigator.userAgent.indexOf("MSIE 10.0") > 0) || navigator.userAgent.indexOf("rv:11.0") > 0) {
            $("#spanUpload").removeClass("fileinput-buttonDir");
            $("#spanUpload").addClass("fileinput-button");
        }
        var menu = $('<ul id="menuID" class="dropdown-menu1">' + '</ul>');
        var menuItem2 = $('<li><span id="uploadMenu2"><span class="uploadBtn btn fileinput-button"style="width: 45px;padding: 2px;text-align:left"><span><spring:message code='button.uploadDir'/></span><form id="dirform" style="padding-top:2px"><input id="dirUpload0" title="<spring:message code='button.uploadDir.inputTitle'/>" type="file" name="files[]" multiple="multiple" webkitdirectory/></form></span></span></li>');
        var ffMenuItem = $('<li><span id="ffUplodSpan"><span class="uploadBtn btn fileinput-button"style="width: 45px;padding: 2px;text-align:left"><span><spring:message code='button.uploadDir'/></span><form id="dirform" style="padding-top:2px"><input id="dirUpload1" title="<spring:message code='button.uploadDir.inputTitle'/>" type="file" name="files[]" multiple="multiple" /></form></span></span></li>');
        var menuItem4_chro = $('<li><span class="uploadBtn btn fileinput-button"style="width: 45px;padding: 2px;text-align:left"><span><span><spring:message code='button.uploadFile'/></span> <input id="fileUpload" title="<spring:message code='button.upload.inputTitle'/>" type="file" name="files[]" multiple /></span></span></li>');

        var menuItem3 = $('<li id="btnLi"><button id="uploadfile" class="uploadDirBtn"><spring:message code='button.uploadDir'/></button></li>');
        if (navigator.userAgent.indexOf("MSIE 10.0") > 0 || navigator.userAgent.indexOf("rv:11.0") > 0) {
            var menuItem4 = $('<li><span class="uploadBtn btn fileinput-button"style="width: 80px;padding: 2px;left:-6px"><span id="fileUpload2"><input id="fileUpload" type="file" name="files[]" multiple /></span><spring:message code='button.uploadFile'/></span></li>');

        } else if (isFirFoxBrowser()) {
            var menuItem4 = $('<li><span class="uploadBtn btn fileinput-button"style="width: 80px;padding: 2px;left:-6px"><span id="fileUpload2"><input id="fileUpload" type="file" name="files[]" multiple /></span><spring:message code='button.uploadFile'/></span></li>');

        } else {

            var menuItem4 = $('<li><span class="uploadBtn btn fileinput-button"style="width: 80px;padding: 2px;left:2px"><span id="fileUpload2"><input id="fileUpload" type="file" name="files[]" multiple /></span></span></li>');
        }

        menuItem3.on("click", uploadDir);
        if (!isFirFoxBrowser() && !(!$.support.leadingWhitespace || navigator.userAgent.indexOf("MSIE 9.0") > 0)) {
            $("#uploadBtnBoxForJS").append(menu);
        } else if (isFirFoxBrowser()) {
            $("#uploadBtnBoxForJS").append(menu);
            $("#menuID").css("height", "22px");
        } else {
            $("#uploadBtnBox").append(menu);
        }

        if (isChromeBrowser()) {
            $("#menuID").append(menuItem4_chro);
            $("#menuID").append(menuItem2);
            $("#dirUpload0").on("click", function (e) {
                unSupportAlert(e);
            });
        } else if (isFirFoxBrowser()) {
            $("#menuID").append(menuItem4);
            //$("#menuID").append(ffMenuItem);
            //Message for not support currently
            $("#dirUpload1").on("click", function (e) {
                unSupportAlert(e);
            });
        } else {
            $("#menuID").append(menuItem4);
            $("#menuID").append(menuItem3);
        }

        $("#menuID").css("display", "none");
        $("#hoverDiv").hover(
            function () {
            },
            function () {
                $("#menuID").css("display", "none");
            }
        );
    }

    function unSupportAlert(e) {
        //firefox
        if (isFirFoxBrowser()) {
            alert("Not Support folder upload currently!");
            if (e && e.preventDefault) {
                e.preventDefault();
            }
        }
        //chrome
        if (isChromeBrowser) {
            var tester = document.createElement('input');
            tester.type = 'file';
            if (!('multiple' in tester && 'webkitdirectory' in tester)) {
                alert("Not Support folder upload for this edition,please upgrade!");
                if (e && e.preventDefault) {
                    e.preventDefault();
                }
            }
        }
    }
</script>
</body>

<script type="text/javascript">
    // FOR FILE
    var files;
    var interval = setInterval("refreshProcess()", 500);
    var uploadFailFile = new Array();
    var uploadComplete = new Array();
    var uploadDelete = new Array();
    var uploadFolderFail = new HashMap();
    var uploadProgress = new HashMap();
    var uploadStatusMap = new HashMap();
    var uploadRateMap = new HashMap();
    var uploadFinishMap = new HashMap();
    var isFolderIsUploading = false;
    var intervalReset = false;

    // FOR SEARCH
    var transDocTypeDocument = "<spring:message code='ufm.DocType.document'/>",
        transDocTypePicture = "<spring:message code='ufm.DocType.picture'/>",
        transDocTypeAudio = "<spring:message code='ufm.DocType.audio'/>",
        transDocTypeVideo = "<spring:message code='ufm.DocType.video'/>",
        transDocTypeOther = "<spring:message code='ufm.DocType.other'/>";
    var vcancelTip = "<spring:message code='button.cancel'/>",
        vbindfilelabelTitle = '<spring:message code="fl.title.bindfilelabel"/>',
        vunbindTipTitle = "<spring:message code='fl.view.unbind.tip'/>",
        vunbindDeleteMessage = "<spring:message code='fl.view.confirm.delete'/>",
        vokTip = "<spring:message code='button.ok'/>",
        vsuccessTip = "<spring:message code='operation.success'/>",
        verrorTip = "<spring:message code='operation.failed'/>",
        vqueryHighSearchLabelsLabels = "<spring:message code='fl.view.no.labels'/>",
        vflBindNoPermission = "<spring:message code='fl.bind.noPermission'/>";
    var vtoken = '${token}';

    // FOR TEAMSPACE
    var verrorUploadfolderNull = "<spring:message code='error.uploadfolder.null'/>",
        verrorUploadfolderFileLimit = "<spring:message code='error.uploadfolder.file.limit'/>",
        verrorUploadfolderFileFail = "<spring:message code='error.uploadfolder.file.fail'/>",
        verrorUploadfolderLimit = "<spring:message code='error.uploadfolder.limit'/>",
        vuploadifyMsgComplete = "<spring:message code='uploadify.msg.complete'/>",
        vfileTitleUploadFailed = "<spring:message code='file.title.uploadFailed'/>",
        vcommonTip = "<spring:message code='common.tip'/>";
    var vreqServer = "${pageContext.request.getServerName()}",
        vreqPort = window.location.port,
        vreqProtcol = window.location.protocol,
        vdomainName = "${pageContext.request.getHeader('Host')}";
    if (!vreqPort) {
        vreqPort = "80";
        if (vreqProtcol == "https:") {
            vreqPort = "443";
        }
    }

    function checkUploadComplete() {
        if (uploadComplete.length == 0) {
            $("#uploadModal").hide();
            for (var i = 0; i < uploadDelete.length; i++) {
                var element = document.getElementById(uploadDelete[i]);
                $(element).remove();
            }

            var lalala = uploadFolderFail.values();
            if (uploadFolderFail.size() != 0 || uploadErrorFiles.length > 0) {
                var fileList = "";
                for (var i = 0; i < lalala.length; i++) {
                    if (lalala[i] != "" && lalala[i] != null) {
                        fileList += lalala[i] + "<br />";
                    }
                }
                for (var i = 0; i < uploadErrorFiles.length; i++) {
                    fileList += uploadErrorFiles[i] + "<br />";
                }
                $("#uploadFileFailTitle").html("<spring:message code='file.errorMsg.uploadErrors' arguments='" + uploadFolderFail.size() + "'/>");
                $("#uploadFileFailList").html(fileList);
                $("#uploadFileFail").modal('show');
                uploadFolderFail = new HashMap();
            }
            intervalReset = true;
            uploadDelete = [];
        }

        uploadStatusMap = new HashMap();

        uploadRateMap = new HashMap();

        uploadFinishMap = new HashMap();

        isFolderIsUploading = false;
        listFile(currentPage, catalogParentId);
    }
</script>
<%--文件标签相关 --%>
<script src="${ctx}/static/js/files/spaceDetail.js" type="text/javascript"></script>
<script src="${ctx}/static/js/filelabel/filelabel.admin.js" type="text/javascript"></script>
<script src="${ctx}/static/js/filelabel/filelabel.search.js" type="text/javascript"></script>
<script type="text/javascript">
    var filelabelSearch = new FilelabelAdmin(ownerId, 2, '${token}', curUserId);
    var currFlPageNum = 0, currFlPageSize = 10;
    var isFirstLoadEnterpriseFls = true;
</script>
</html>
