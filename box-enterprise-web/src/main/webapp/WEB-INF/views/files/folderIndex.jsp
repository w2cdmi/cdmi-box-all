<%@page import="javax.sound.midi.SysexMessage" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="cse" uri="http://cse.huawei.com/custom-function-taglib" %>

<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%
    request.setAttribute("token", CSRFTokenManager.getTokenForSession(session));
%>
<!DOCTYPE html>
<html>
<meta http-equiv="Access-Control-Allow-Origin" content="*">
<head>
    <%@ include file="../common/common.jsp" %>
    <%@ include file="commonForFiles.jsp" %>
    <%@ include file="UploadMessages.jsp" %>
    <%@ include file="commonForCreateAndRenameNode.jsp" %>
    <%@ include file="commonForUploadFiles.jsp" %>

    <link href="${ctx}/static/skins/default/css/layout.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/static/skins/default/css/filelabel.css" rel="stylesheet" type="text/css"/>

    <script src="${ctx}/static/js/public/JQbox-hw-files.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/JQbox-hw-page.js" type="text/javascript"></script>
</head>
<body>
<div id="pageLoadingContainer" style="z-index: 10000; position: fixed; left: 0; top: 0; right: 0; bottom: 0; background: #fff;"></div>
<script type="text/javascript">new Spinner(optsBigSpinner).spin(document.getElementById("pageLoadingContainer"));</script>

<%@ include file="../common/header.jsp" %>
<div class="breadcrumb">
    <div id="test" style="display:none"></div>
    <div class="breadcrumb-con clearfix">
        <div class="pull-left folderTreeCon" style="display: none;">
            <button id="folderTree" type="button" class="btn" title="<spring:message code="file.tips.folder"/>" onclick="initTree()"><i class="icon-sitemap"></i></button>
            <ul id="treeArea" class="ztree"></ul>
        </div>

        <div class="form-search">
            <div class="input-append">
                <input type="text" class="search-query" id="searchBar" maxlength=246 placeholder="<spring:message code='file.tips.search'/>"/>
                <button class="btn" type="button" onclick="doSearch(1)"><i class="icon-search"></i></button>
            </div>
            <%--
            <div style="display: inline-block;">
                <a class="high-search highSearch" onclick="showHighSearchView()">&nbsp;<spring:message code='fl.view.high.search'/></a>
            </div>
            --%>
        </div>

        <ul id="breadcrumbCon"></ul>
    </div>
    <form id="downloadForm" action="" method="get" style="display: none" target=""></form>
	
</div>
<div class="public-bar-con">
    <div class="public-layout">

        <div class="public-bar clearfix" id="publicBar">
            <div class="pull-left btn-toolbar">
                <!-- new Dropdown -->
                <div id="hoverDiv" class="pull-left" style="border: 1px solid #5eb1fb;border-radius: 2px;">
                    <div id="uploadBtnBox" class="pull-left upload-btn-box"><Button class="btn fileinput-buttonDirforIE" onclick="handleDropMenu()" style="width:99px"><i class="new-icon-toolbar-upload-blue"></i><spring:message code='button.upload'/></Button></div>
                    <div id="uploadBtnBoxForJS" class="pull-left upload-btn-box">
                        <span id="spanUpload" class=" fileinput-buttonDir" onclick="handleDropMenu()">
                            <i class="new-icon-toolbar-upload"></i> <span style="color: white;"><spring:message code='button.upload'/></span>
                        </span>
                    </div>
                </div>

                <!-- new Dropdown -->
                <%--
                <div id="hoverDiv" class="pull-left">
                    <span id="uploadBtnBox" class="pull-left upload-btn-box"><input type="file" id="fileUpload" name="fileUpload" multiple="true"/></span>
                    <span id="uploadBtnBoxForJS" class="pull-left upload-btn-box"><span id="spanUpload" class="fileinput-buttonDir"><span><i class="icon-upload icon-white"></i> <spring:message code='button.upload'/></span><input id="fileUpload" type="file" name="files[]" multiple /></span></span>
                </div>
                --%>
                <button id="newFolderBtn" class="btn btn-primary" type="button" onClick="createFolder()" style="margin-left: 10px;">
                    <i class="new-icon-toolbar-create-folder"></i><spring:message code="button.newFolder"/>
                </button>
                <%--
                <!-- 增加 查看文件轉換進度按鈕  修改國際化-->
                <button id="queryConvertBtn" class="btn" type="button" onClick="queryConvert()"><i class="icon-info"></i><spring:message code="button.queryConvert"/></button>
                <!-- 增加 查看文件轉換進度按鈕  修改國際化-->
                --%>

            </div>

            <!-- 分割线  -->
            <div class="range-type pull-right split-line">
                <div class="btn-group" data-toggle="buttons-radio">
                    <button id="viewTypeBtnList" onClick="viewTypeSwitch('list')" type="button" class="btn btn-small btn-link" title="<spring:message code="file.tips.list"/>"><i class="icon-list"></i></button>
                    <button id="viewTypeBtnThumbnail" onClick="viewTypeSwitch('thumbnail')" type="button" class="btn btn-small btn-link" title="<spring:message code="file.tips.thumbnail"/>"><i class="icon-th"></i></button>
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



<div class="body">
    <div class="body-con clearfix">
        <div class="pull-left clearfix">

            <%-- 高级搜索 --%>
            <div class="high-search-container clearfix" style="position: relative;">
                <div class="high-search-view">
                    <div class="search-top">
                        <div class="input-append search-util fl">
                            <b><spring:message code='fl.highsearch.view.filename'/>：</b>&nbsp;
                            <input type="text" name="highSearchFilename" id="highSearchFilename" class="input-medium keyword search-query" placeholder="<spring:message code='fl.highsearch.view.search.condition.tip'/>">
                            <a class="btn start-search startSearch" onclick="submitHighSearch()">
                                <span class="pipe">|</span><spring:message code='fl.title.begin.highsearch'/>
                            </a>
                        </div>
                        <div class="inline-block fr">
                            <a class="exit-search" onclick="exitHighSearch()">
                                <spring:message code='fl.title.exit.highsearch'/>
                            </a>
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
                <div id="fileListNull" class="file-catalog-null">
                    <spring:message code="file.tips.emptyFolder"/>
                </div>
                <div id="fileList"></div>
                <div id="fileListPageBox"></div>
            </div>

        </div>
    </div>
    <%@ include file="./docfullscreen.jsp" %>
</div>

<%@ include file="../common/footer.jsp" %>

<script type="text/javascript">
    // common variable define
    var ownerId = <shiro:principal property="cloudUserId"/>;
    var opts_viewGrid = null;
    var opts_page = null;
    var viewMode = "file";
    var currentPage = 1;
    var catalogParentId = <c:out value='${parentId}'/>;
    var keyword = null;
    var catalogData = null;
    var isSessionTimeout = false;
    var isLinkHidden = <c:out value='${linkHidden}'/>;

    var canPreview =<%=PreviewUtils.isPreview()%>;
    var reqProtocol = "<%=request.getSession().getAttribute("reqProtocol")%>";

    var orderField = getCookie("orderField");
    orderField = orderField == null ? "modifiedAt" : orderField;
    var isOrderField = false;
    var orderFieldArr = new Array("modifiedAt", "name", "size");
    var viewType = "1"; // 视图类型
    var failTip = "<spring:message code='operation.failed'/>";

    // variable define
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
    var docTypeMap = new HashMap(); // doctype
    var isFolderIsUploading = false;
    var intervalReset = false;
    var url = ctx + "/teamspace/openQueryConvert?teamId=" + ownerId + "&spaceType=0";

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
        vqueryHighSearchLabelsLabels = "<spring:message code='fl.view.no.labels'/>";
    var vtoken = '${token}';

    var vversionList = "<spring:message code='file.title.versionList'/>",
        vbuttonClose = "<spring:message code='button.close'/>",
        vinviteShareErrorEmpty = "<spring:message code='inviteShare.error.empty'/>",
        vfileTitleTurnStore = "<spring:message code='file.title.turnStore'/>";
    voperationFailed = "<spring:message code='operation.failed'/>",
        voperationSuccess = "<spring:message code='operation.success'/>",
        vfileErrorMsgParentNotChange = "<spring:message code='file.errorMsg.parentNotChange'/>",
        vfileErrorMsgSameFolder = "<spring:message code='file.errorMsg.sameFolder'/>",
        vfileErrorMsgCanNotCopyToChild = "<spring:message code='file.errorMsg.canNotCopyToChild'/>",
        verrorForbid = "<spring:message code='error.forbid'/>",
        verrorNotfound = "<spring:message code='error.notfound'/>",
        vcommonTaskDoing = "<spring:message code='common.task.doing'/>",
        vfileTitleCopyOrMove = "<spring:message code='file.title.copyOrMove'/>",
        vfileInfoAutoRename = "<spring:message code='file.info.autoRename'/>",
        vbuttonUploadDir = '<spring:message code="button.uploadDir"/>',
        vbuttonUploadDirInputTitle = '<spring:message code="button.uploadDir.inputTitle"/>',
        vbuttonUploadFile = '<spring:message code="button.uploadFile"/>',
        vbuttonUploadInputTitle = '<spring:message code="button.upload.inputTitle"/>',
        vbuttonUploadDir = '<spring:message code="button.uploadDir"/>';

    var verrorUploadfolderNull = "<spring:message code='error.uploadfolder.null'/>",
        verrorUploadfolderFileLimit = "<spring:message code='error.uploadfolder.file.limit'/>",
        verrorUploadfolderFileFail = "<spring:message code='error.uploadfolder.file.fail'/>",
        verrorUploadfolderLimit = "<spring:message code='error.uploadfolder.limit'/>",
        vuploadifyMsgComplete = "<spring:message code='uploadify.msg.complete'/>",
        vfileTitleUploadFailed = "<spring:message code='file.title.uploadFailed'/>",
        vcommonTip = "<spring:message code='common.tip'/>",
        vflBindNoPermission = "<spring:message code='fl.bind.noPermission'/>";

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

<script type="text/javascript">
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
    var M = {
        m_open: {title: "<spring:message code='button.open'/>", className: "icon-folder-open", toolbarClassName:"icon-folder-open"}, <%-- 打开 --%>
        m_share: {title: "<spring:message code='button.share' />", className: "new-icon-contentmenu-share", toolbarClassName:"new-icon-toolbar-share-blue"}, <%-- 共享 --%>
        m_link: {title: "<spring:message code='button.shareLink'/>", className: "new-icon-toolbar-link-blue", toolbarClassName:"new-icon-toolbar-link-blue"}, <%-- 分享链接 --%>
        m_viewImage: {title: "<spring:message code='button.view'/>", className: "icon-suitcase", toolbarClassName:"icon-suitcase"}, <%-- 預覽 --%>
        m_sync: {title: "<spring:message code='button.setSync'/>", className: "icon-sync", toolbarClassName:"icon-sync"}, <%-- 设置同步 --%>
        m_cancelSync: {title: "<spring:message code='button.cancelSync'/>", className: "icon-no-sync", toolbarClassName:"icon-no-sync"}, <%-- 取消同步 --%>
        m_move: {title: "<spring:message code='button.copyOrMove'/>", className: "new-icon-contentmenu-copy", toolbarClassName:"icon-copy"}, <%-- 复制/移动 --%>
        /*  m_online_edit: {title: "<spring:message code='button.onlineEdit'/>", className: "icon-pencil", toolbarClassName:"icon-pencil"}, */ <%--在线编辑 --%>
        m_delete: {title: "<spring:message code='button.delete'/>", className: "new-icon-contentmenu-del", toolbarClassName:"new-icon-toolbar-trash-blue"}, <%-- 删除 --%>
        m_rename: {title: "<spring:message code='button.rename'/>", className: "new-icon-contentmenu-rename", toolbarClassName:"icon-pencil"}, <%-- 重命名 --%>
        m_download: {title: "<spring:message code='button.download'/>", className: "new-icon-contentmenu-downland", toolbarClassName:"new-icon-toolbar-downland-blue"}, <%-- 下载 --%>
        m_versionList: {title: "<spring:message code='button.listVersion'/>", className: "new-icon-contentmenu-view-version", toolbarClassName:"icon-info"}, <%-- 版本列表 --%>
        m_turnStore: {title: "<spring:message code='button.turnStore'/>", className: "new-icon-contentmenu-transfer", toolbarClassName:"icon-send-file"}, <%-- 转存到团队空间 --%>
        m_favorite: {title: "<spring:message code='button.favorite'/>", className: "new-icon-contentmenu-add-favorite", toolbarClassName:"icon-favorite"} <%-- 添加到收藏夹 --%>
    };

    var listFileWidth = ["63.5%", "0%", "19%", "17.5%"];
    var searchFileWidth = ["43.5%", "29%", "10%", "17.5%"];
    var headData = {
        "name": {"width": listFileWidth[0]},
        "path": {"width": listFileWidth[1]},
        "size": {"width": listFileWidth[2]},
        "modifiedAt": {"width": listFileWidth[3]}
    };

    var showPc = true;
    var pcClientDownloadUrl = null;

    $(function () {
        beforeInit();

        loadSysSetting("${ctx}", '<spring:message code="common.language1" />');

        if ("${modelDocType}" == "") {
            navMenuSelected("navAllFile");
            $("#doctypeTree_show").css("background-position", "-10px 0");
        } else {
            menutree = "docType${modelDocType}";
        }

        pcClientDownloadUrl = getClientDownloadURL("PC");

        //create folder upload menu related
        createMenu('<spring:message code="button.uploadFile"/>', '<spring:message code="button.upload.inputTitle"/>', '<spring:message code="button.uploadDir"/>', '<spring:message code="button.uploadDir.inputTitle"/>');
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
        $("#hoverDiv").hover(
            function () {
                $('div.public-bar').css('z-index', 9999);
            },
            function () {
                $('div.public-bar').css('z-index', 0);
            }
        );

        $('div.high-search-type').on('click', 'i.icon-custom-remove', removeSelectedItem);
        window.onhashchange = processHash;
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
            var arrLen = m.length;
            viewMode = m[0];
            currentPage = m[1];
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
                ;
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
        initViewType(listViewType);
//        debugger;
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
            showPageSet: true,
            lang: '<spring:message code="common.language1"/>'
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
            switch (colIndex) {
                case 0:
                    try {
                        var alink, version, dataHandle, shareHandle, linkHandle, syncHandle,
                            itemVal = colTag.find("span").text(), viewHandle;
                        dataHandle = $("<span class='file-handle'></span>");
                        colTag.find("span").remove();
                        if (isFolderType(rowData.type)) {
                            alink = "<a id='" + rowData.id + "' onclick='openFolder(" + rowData.id + ", " + rowData.ownedBy + ","
                                + rowData.createdBy + "," + rowData.type + ",\"" + rowData.name + "\"" + ")' title='" + rowData.name
                                + "'>" + rowData.name + "</a>";
                            colTag.append(alink);
                        } else {
                            var fileType = _getStandardType(rowData.name);
                            var videoType = rowData.name.substring(rowData.name.lastIndexOf(".") + 1).toLowerCase();
                            if (fileType == "img") {
                                alink = "<a id='" + rowData.id + "' rel='lightbox|/|${ctx}/files/getUrlAndBrowse/" + rowData.ownedBy + "/" + rowData.id + "?" + Math.random() + "|/|" + rowData.name + "|/|" + rowData.id + "' onclick='$(this).lightbox()' title='" + rowData.name + "'>" + rowData.name + "</a>";
                            } else if (fileType == "video" && videoType == "mp4") {
                                alink = "<a id='" + rowData.id + "' onclick='playVideo(" + rowData.id + ")' title='" + rowData.name + "'>" + rowData.name + "</a>";
                            } else if (canPreview && rowData.previewable) {
                                alink = "<a id='" + rowData.id + "' onclick='previewFile(" + rowData.id + "," + rowData.ownedBy + ")' title='" + rowData.name + "'>" + rowData.name + "</a>";
                            } else {
                                alink = "<a id='" + rowData.id + "' onclick='downloadFile(" + rowData.id + ")' title='" + rowData.name + "'>" + rowData.name + "</a>";
                            }

                            var fileItemDiv = $('<div class="file-info"></div>');
                            var fileLinkDiv = $('<div class="file-alink"></div>');
                            var fileMarksDiv = $('<div class = "marks"></div>');
                            var filelableListDiv = $('<div class="label-list" fid = "' + rowData.id + '"></div>');

                            fileLinkDiv.append(alink);
                            var currentVersions = rowData.versions;
                            if (rowData.type == 1 && currentVersions > 1) {
                                version = "<i class='file-version' title='<spring:message code='file.tips.fileVersion'/>' onclick='enterVersionList(" + rowData.id + ")'>V" + currentVersions + "</i>";
                                fileLinkDiv.append(version);
                            }

                            if (rowData.fileLabels) {
                                for (var index in rowData.fileLabels) {
                                    var tempFl = rowData.fileLabels[index];
                                    var tempLabelSpan = '<span class="label" flid = "' + tempFl.id + '" data-original-title = "' + tempFl.labelName + '">' + getSpecialLengthString(tempFl.labelName, 5) +
                                        '<a class="delMark del-mark float-clear" onclick=unbindFileLabel(this)><span class = "icon-custom-del-mark"></span></a></span>';

                                    filelableListDiv.append(tempLabelSpan);
                                }
                            }

                            fileMarksDiv.append(filelableListDiv);
                            var filelableAddTag = '<a class="float-clear label-bind" style = "float:none;" onclick="showBindFileLabelUI(' + rowData.id + ')"><span class="icon-custom-mark"></span></a>';
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

                            $('div.label-list').on('mouseover mouseout', "span.label", function (event) {
                                if (event.type == "mouseover") {
                                    $(this).find("a.delMark").show();
                                } else if (event.type == "mouseout") {
                                    $(this).find("a.delMark").hide();
                                }
                            });
                        }

                        if (!isInMigrationFolder && rowData.type != 5) {
                            /** 非迁移文件才允许有以下功能 */
                            var currentVersions = rowData.versions;
                            if (rowData.type == 1 && currentVersions > 1) {
                                version = "<i class='file-version' title='<spring:message code='file.tips.fileVersion'/>' onclick='enterVersionList(" + rowData.id + ")'>V" + currentVersions + "</i>";
                            }
                            if (rowData.shareStatus == 0) {
                                shareHandle = "<i class='icon-share no-set' title='<spring:message code='button.share'/>' onClick='shareMyFolder(" + rowData.id + ")'></i>";
                            } else {
                                shareHandle = "<i class='icon-share' title='<spring:message code='button.share'/>' onClick='shareMyFolder(" + rowData.id + ")'></i>";
                            }
                            if (rowData.linkStatus == '' || rowData.linkStatus == null) {
                                linkHandle = "<i class='icon-link no-set' title='<spring:message code='button.shareLink'/>' onclick='shareLink(" + rowData.id + ")'></i>";
                            } else {
                                linkHandle = "<i class='icon-link' title='<spring:message code='button.shareLink'/>' onclick='shareLink(" + rowData.id + ")'></i>";
                            }
                            if (rowData.parentId == 0) {
                                if (!isBackupFolderType(rowData.type)) {
                                    if (rowData.syncStatus == 0) {
                                        syncHandle = "<i class='icon-no-sync' title='<spring:message code='file.tips.unsync.info'/>' onclick='setSync(" + rowData.id + "," + rowData.type + ")'></i>";
                                    } else {
                                        syncHandle = "<i class='icon-sync no-set' title='<spring:message code='file.tips.sync.info'/>' onclick='cancelSync(" + rowData.id + "," + rowData.type + ")'></i>";
                                    }
                                }
                            }

                            // 是否预览
                            if (isViewType(rowData)) {
                                viewHandle = "";
                                var fileName = '"' + rowData.name + '"';
                                viewHandle = "<i class='icon-suitcase no-set' title='<spring:message code='button.view'/>' onclick='viewImg(" + rowData.id + "," + fileName + " )'></i>";
                                dataHandle.append(viewHandle);
                            } else {
                                viewHandle = "";
                            }
                            viewHandle = "";
                            dataHandle.append(syncHandle);
                            if (!isLinkHidden) {
                                dataHandle.append(linkHandle);
                            }
                            dataHandle.append(shareHandle);
                        }

                        //colTag.append(version);
                        colTag.append(dataHandle);
                    } catch (e) {
                    }
                    break;
                case 1:
                    var pathLink = "<a onclick='toThePath(" + rowData.parentId + ")' title='" + rowData.path + "'>" + rowData.path + "</a>";
                    colTag.find("span").remove();
                    colTag.append(pathLink);
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

            // debugger

            $.fn.comboGrid.initGridMenu = function () {
                var menuData = [];
                for (var key in M) M[key].divider = false;

                if (allSelectData.length == 0) {

                } else if (allSelectData.length == 1) {
                    var item = allSelectData[0];

                    if (item.type != 5) {
                        if (isInMigrationFolder) {
                            if (isFolderType(item.type)) {
                                menuData = {"m_move": M.m_move};
                            } else {
                                menuData = {"m_move": M.m_move, "m_download": M.m_download};
                            }
                        } else {
                            menuData = {
                                "m_open": M.m_open,
                                "m_share": M.m_share,
//                                "m_link": M.m_link,
                                "m_viewImage": M.m_viewImage,
                                "m_download": M.m_download,
                                "m_cancelSync": M.m_cancelSync,
                                "m_sync": M.m_sync,
                                "m_turnStore": M.m_turnStore,
                                "m_move": M.m_move, /* "m_online_edit": M.m_online_edit, */
                                "m_delete": M.m_delete,
                                "m_rename": M.m_rename,
                                "m_versionList": M.m_versionList,
                                "m_favorite": M.m_favorite
                            };

                            if (isFolderType(item.type)) {
                                /* delete menuData.m_online_edit; */
                                delete menuData.m_download;
                                delete menuData.m_versionList;
                                menuData.m_open.divider = true;
                                menuData.m_link.divider = true;
                                menuData.m_rename.divider = true;
                            } else {
                                delete menuData.m_open;
                                /* delete menuData.m_online_edit; */
                                menuData.m_download.divider = true;
                                menuData.m_versionList.divider = true;
                                /* if (!isOnlineEditable(item)) {
                                 delete menuData.m_online_edit;
                                 } */
                            }
                            if (item.parentId == 0) {
                                if (item.syncStatus == 1) {
                                    delete menuData.m_sync;
                                    menuData.m_cancelSync.divider = true;
                                } else {
                                    delete menuData.m_cancelSync;
                                    menuData.m_sync.divider = true;
                                }
                            } else {
                                delete menuData.m_sync;
                                delete menuData.m_cancelSync;
                            }

                            if (isLinkHidden) {
                                delete menuData.m_link;
                            }
                            if (isBackupFolderType(item.type)) {
                                delete menuData.m_turnStore;
                                delete menuData.m_move;
                                /* delete menuData.m_online_edit; */
                                delete menuData.m_delete;
                                delete menuData.m_sync;
                                delete menuData.m_cancelSync;
                                delete menuData.m_favorite;
                            }
                            // 是否预览
                            if (!isViewType(item)) {
                                delete menuData.m_viewImage;
                            }
                        }
                    } else {
                        menuData = {"m_delete": M.m_delete};
                    }
                }
                else {
                    if (!isInMigrationFolder) {
                        menuData = {"m_turnStore": M.m_turnStore, "m_move": M.m_move, "m_delete": M.m_delete};
                    } else {
                        $('#newFolderBtn').hide();
                        menuData = {"m_move": M.m_move};
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
                if (isBackupFolderType(dataList[0].type)) {
                    return;
                }

                if (dataList[0].type == 0) {
                    title = "<spring:message code='file.title.moveFolder'/>";
                } else {
                    title = "<spring:message code='file.title.moveFile'/>";
                }
            } else {
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
            deleteNodes();
        };

        $.fn.comboGrid.keyDelete = function () {
            deleteNodes();
        };

        $.fn.comboGrid.keyRename = function () {
            renameNode();
        };

        $.fn.comboGrid.dbTrOp = function (rowData) {
            if (isFolderType(rowData.type)) {
                if (!isInMigrationFolder) {
                    ownerId = rowData.ownedBy;
                }
                openFolder(rowData.id, rowData.ownedBy, rowData.createdBy, rowData.type, rowData.name);
            } else if ("img" == _getStandardType(rowData.name)) {
                $("#" + rowData.id).lightbox();
            }
        };

        $.fn.gridMenuItemOp = function (btnType) {
            executeHandler(btnType);
        };

        $.fn.lightbox.downloadImg = function (id) {
            downloadFile(id);
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

        $.ajax({
            type: "POST",
            url: "${ctx}/nodes/move/" + ownerId,
            data: {'destOwnerId': ownerId, 'ids': ids, 'parentId': targetId, 'token': "<c:out value='${token}'/>"},
            error: function (request) {
                top.ymPrompt.close();
                if (request.status == 404) {
                    handlePrompt("error", "<spring:message code='error.notfound'/>");
                    if (viewMode == "file") {
                        listFile(currentPage, catalogParentId);
                    } else {
                        doSearch();
                    }
                    ymPrompt.close();
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

    function initViewType(type) {
        if (type == "list") {
            $("#viewTypeBtnList").addClass("active");
        } else {
            $("#viewTypeBtnThumbnail").addClass("active");
        }
    }

    function viewTypeSwitch(type) {
        $("#fileList").switchViewType(type, opts_viewGrid);
        listViewType = type;
        setCookie("listViewType", listViewType);
    }

    function listFile(curPage, parentId) {
        var pagePerDis = getCookie("fileListPerPageNum");
        pagePerDis = (pagePerDis == null || pagePerDis == '' || pagePerDis == undefined) ? 40 : pagePerDis;
        viewMode = "file";
        /** 增加离职接受文件处理 **/
        var pownerId = ownerId;
        if (curPage == 1 && parentId == 0) {
            pownerId = ownerId;
            resetMigrationInfo();
        }

        if (isInMigrationFolder) {
            var tempParentId = getCookie("migrationParantId");
            if (tempParentId == parentId) {
                pownerId = ownerId;
                isMigrationFolder = true;
            } else {
                if (isMigrationFolder) {
                    setCookie("migrationParantId", parentId);
                } else {
                    if (isInMigrationFolder && departureOwnerId) {
                        pownerId = departureOwnerId;
                    }
                }
            }
        }

        var url = "folders/list";
        var params = {
            "ownerId": pownerId,
            "parentId": parentId,
            "pageNumber": curPage,
            "pageSize": pagePerDis,
            "orderField": orderField,
            "desc": isDesc,
            "token": "<c:out value='${token}'/>"
        };

        $("#teamId").attr("value", '${teamId}');
        if ('' != '${modelDocType}') {
            url = "folders/listByDocType";
            params = {
                "ownerId": pownerId,
                "docType": '${modelDocType}',
                "pageNumber": curPage,
                "pageSize": pagePerDis,
                "orderField": orderField,
                "desc": isDesc,
                "token": "<c:out value='${token}'/>"
            };

            $("#newFolderBtn").remove();
            $(".upload-btn-box").remove();
            //$("#uploadBtnBoxForJS").find("*").prop("disabled","disabled")
            //$("#newFolderBtn").prop("disabled","disabled")
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

            $("#witchSelect").val("0"); //回复默认值
            $("#viewTypeBtnList").addClass("active");
            $("#viewTypeBtnThumbnail").removeClass("active");
        }

        $.ajax({
            type: "POST",
            url: url,
            data: params,
            error: function (request) {
                if (request.status == 404) {
                    handlePrompt("error", "<spring:message code='error.notfound'/>");
                    changeHash("file", 1, 0);
                } else if (request.status == 403) {
                    comboxRemoveLoading("pageLoadingContainer");
                    ymPrompt.alert({
                        title: "<spring:message code='common.tip'/>",
                        message: "<spring:message code='error.forbid'/>",
                    });
                } else if (params.desc == true || params.desc == "true") {
                    comboxRemoveLoading("pageLoadingContainer");
                } else {
                    comboxRemoveLoading("pageLoadingContainer");
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
                    changeHash("file", curPage, parentId);
                    return;
                }

                headData.name.width = listFileWidth[0];
                headData.path.width = listFileWidth[1];
                headData.size.width = listFileWidth[2];
                headData.modifiedAt.width = listFileWidth[3];
                $("#fileList").setGridData(catalogData, opts_viewGrid);
                $("#fileListPageBox").setPageData(opts_page, data.number, data.size, data.totalElements);
                buttonInit();
                createBreadcrumb(parentId, pownerId);

                catalogParentId = parentId;
                comboxRemoveLoading("pageLoadingContainer");

                var isShowGuide = getCookie("isShowGuide");
                if (isShowGuide != "neverShow") {
                    var guideUrl = "${ctx}/static/help/guide/guide.html";
                    if ('<spring:message code="common.language1"/>' == "en") {
                        guideUrl = "${ctx}/static/help/guide/guide-en.html";
                    }
                    ymPrompt.win({message: guideUrl, width: 650, height: 500, titleBar: false, iframe: true});
                }
            }
        });
    }

    $("#navAllFile").click(function () {

        if ($.cookie("collspanDoc") == null || $.cookie("collspanDoc") == "null") {

            $.cookie("collspanDoc", "1", {"expires": 365, "path": "/"});
        }
        /* $("#userDoctypetree").find("li").each(function(e){
         if($.cookie("collspanDoc")==1){
         $(this).css("display","none");
         }else if($.cookie("collspanDoc")==0){
         $(this).css("display","block");
         }

         }); */
        if ($.cookie("collspanDoc") == 1) {
            //$.cookie("collspanDoc",null);
            $.cookie("collspanDoc", "0");
        } else if ($.cookie("collspanDoc") == 0) {
            //$.cookie("collspanDoc",null);
            $.cookie("collspanDoc", "1");
        }
    });

    function initListButton(allSelectData) {
        var listButtonData = null;
        if (allSelectData.length == 0) {
            $("#listHandler").remove();
        } else if (allSelectData.length == 1) {
            var item = allSelectData[0];

            if (allSelectData[0].type != 5) {
                if (isInMigrationFolder) {
                    if (isFolderType(item.type)) {
                        listButtonData = {"m_move": M.m_move};
                    } else {
                        listButtonData = {"m_download": M.m_download, "m_move": M.m_move};
                    }
                } else {
                    listButtonData = {
                        "m_share": M.m_share,
                        "m_link": M.m_link,
                        "m_delete": M.m_delete,
                        "m_download": M.m_download,
                        "m_rename": M.m_rename,
                        "m_move": M.m_move,
                        /*"m_online_edit": M.m_online_edit,*/
                        "m_viewImage": M.m_viewImage,
                        "m_cancelSync": M.m_cancelSync,
                        "m_sync": M.m_sync,
                        "m_turnStore": M.m_turnStore,
                        "m_versionList": M.m_versionList,
                        "m_favorite": M.m_favorite
                    };
                    if (isFolderType(allSelectData[0].type)) {
                        delete listButtonData.m_download;
                        delete listButtonData.m_versionList;
                    }
                    if (allSelectData[0].parentId == 0) {
                        if (allSelectData[0].syncStatus == 1) {
                            delete listButtonData.m_sync;
                        } else {
                            delete listButtonData.m_cancelSync;
                        }
                    } else {
                        delete listButtonData.m_sync;
                        delete listButtonData.m_cancelSync;
                    }
                    if (isLinkHidden) {
                        delete listButtonData.m_link;
                    }

                    if (isBackupFolderType(allSelectData[0].type)) {
                        delete listButtonData.m_turnStore;
                        delete listButtonData.m_move;
                        /* delete listButtonData.m_online_edit; */
                        delete listButtonData.m_delete;
                        delete listButtonData.m_sync;
                        delete listButtonData.m_cancelSync;
                        delete listButtonData.m_favorite
                    }

                    if (!isViewType(allSelectData[0])) {
                        delete listButtonData.m_viewImage;
                    }
                }
            } else {
                listButtonData = {"m_delete": M.m_delete};
            }
        } else {
            if (!isInMigrationFolder) {
                listButtonData = {"m_turnStore": M.m_turnStore, "m_move": M.m_move, "m_delete": M.m_delete};
            } else {
                listButtonData = {"m_move": M.m_move};
                $('#newFolderBtn').hide();
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
            var menuItem = $('<button onclick="executeHandler(\'' + key + '\')" class="btn btn-link" type="button" id="listBtn_' + key + '"><i class="' + listButtonData[key].toolbarClassName + '"></i> ' + listButtonData[key].title + '</button>');
            listBtnDiv.append(menuItem);
        }

        var moreBtnGroup = $('<div class="dropdown" id="moreBtnGroup"><a class="btn btn-link" data-toggle="dropdown"><i class="icon-more"></i><spring:message code="common.more"/></a><ul class="dropdown-menu" id="moreBtnList"></ul></div>');

        if ((allSelectData.length == 1 && allSelectData[0].type == 5) || isInMigrationFolder) {
            $('#newFolderBtn').hide();
        } else {
            listBtnDiv.append(moreBtnGroup);
            $('#newFolderBtn').show();
        }
        for (var key in listButtonData) {
            var moreBtnItem = $('<li><a onclick="executeHandler(\'' + key + '\');" id="moreListBtn_' + key + '"><i class="' + listButtonData[key].toolbarClassName + '"></i> ' + listButtonData[key].title + '</a></li>');
            //屏蔽更多里面的，在线编辑链接--开始
            /* if(key == 'm_online_edit')
             {
             continue;
             } */
            //屏蔽更多里面的，在线编辑链接--结束
            $("#moreBtnList").append(moreBtnItem);
        }

        var len = $("#listHandler").find(">button").length, maxW = $("#listHandler").width();
        if (allSelectData.length > 1) {
            $("#moreBtnGroup").css("display", "none");
        } else {
            var showBtnNum = isFolderType(allSelectData[0].type) ? 3 : 4;
            for (var n = showBtnNum; n < len; n++) {
                $("#listHandler").find(">button").eq(n).hide();
                $("#moreBtnList").find("li").eq(n).show();
            }
            for (var n = 0; n < showBtnNum; n++) {
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
            } else if ("m_share" == btnType) {
                shareMyFolder();
            } else if ("m_move" == btnType) {
                copyAndMove();
                /* } else if ("m_online_edit" == btnType) {
                 onlineEdit(); */
            } else if ("m_delete" == btnType) {
                deleteNodes();
            } else if ("m_versionList" == btnType) {
                enterVersionList();
            } else if ("m_sync" == btnType) {
                setSync();
            } else if ("m_cancelSync" == btnType) {
                cancelSync();
            } else if ("m_turnStore" == btnType) {
                turnStore();
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
                    createFavorite("myspace", ownedBy, iNodeId, nodeType, name, parentId);
                }
            } else if ("m_viewImage" == btnType) {
                viewImg();
            }
        } catch (e) {
            return;
        }
    }

    function previewFile(fileId, ownerId) {
        window.open("${ctx}/files/gotoPreview/" + ownerId + "/" + fileId + "?parentPageType=myspace");
    }

    function openFolder(id, pownerId, pcreatedBy, pfileType, pfolderName) {
        if (id == null || id == "") {
            var objData = $("#fileList").getGridSelectedData(catalogData, opts_viewGrid);
            var tfileType = objData[0].type,
                tcreatedBy = objData[0].createdBy,
                townerBy = objData[0].ownedBy;

            id = objData[0].id;
            checkIsInMigrationFolder(tcreatedBy, townerBy, tfileType, objData[0].name);
        } else {

            if (pcreatedBy && pownerId) {
                /** 移交文件夹 */
                checkIsInMigrationFolder(pcreatedBy, pownerId, pfileType, pfolderName);
            } else {
                resetMigrationInfo();
            }
        }

        new Spinner(optsMiddleSpinner).spin($("#" + id).parents(".rowli").find("> div").get(0));
        changeHash("file", 1, id);
    }

    function toThePath(folderId) {
        changeHash("file", 0, folderId);
    }

    function downloadFile(id) {
        if (id == null || id == "") {
            var selectedId = $("#fileList").getGridSelectedId();
            id = selectedId[0];
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

    function playVideo(id) {
        if (id == null || id == "") {
            var selectedId = $("#fileList").getGridSelectedId();
            id = selectedId[0];
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

    function shareMyFolder(nodeIdTmp) {
        var inodeId = nodeIdTmp;
        if (inodeId == undefined) {
            var node = $("#fileList").getGridSelectedData(catalogData, opts_viewGrid);
            inodeId = node[0].id;
            var type = node[0].type;
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

        var url = '${ctx}/share/link/' + ownerId + '/' + inodeId;

        top.ymPrompt.win({
            message: url,
            width: 650,
            height: 350,
            title: "<spring:message code='file.title.shareLink'/>",
            iframe: true,
            handler: function (tp) {
                if (tp == "close") {
                    try {
                        top.ymPrompt.getPage().contentWindow.callBackDelLink();
                    } catch (e) {
                    }

                    $("body").css("overflow", "scroll");
                }
            }
        });
    }

    var flag = "0";
    var url = "";
    var curPage = 1;
    var curTotalPage = 1;
    var curNodeId;
    var cutOwnerId;
    function viewImg(nodeIdTmp, fileName) {
        var inodeId = nodeIdTmp;
        var flag;
        if (inodeId == undefined) {
            var node = $("#fileList").getGridSelectedData(catalogData, opts_viewGrid);
            inodeId = node[0].id;
            fileName = node[0].name;
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

    function linkHandle() {
        $("body").css("overflow", "scroll");
        top.ymPrompt.close();
        if (viewMode == "file") {
            listFile(currentPage, catalogParentId);
        } else {
            doSearch();
        }
    }

    function shareHandle(tp) {
        if (viewMode == "file") {
            listFile(currentPage, catalogParentId);
        } else {
            doSearch();
        }
    }

    function sharelistMyFolder() {
        var node = $("#fileList").getGridSelectedData(catalogData, opts_viewGrid);
        var url = '${ctx}/share/listSharedUser/' + ownerId + '/' + node[0].id;
        top.ymPrompt.win({
            message: url,
            width: 550,
            height: 500,
            title: "<spring:message code='file.title.sharedUserList'/>",
            iframe: true,
            btn: [["<spring:message code='button.close'/>", 'yes', false, "btn-focus"]],
            handler: sharelistHandle
        });
        top.ymPrompt_addModalFocus("#btn-focus");
    }

    function sharelistHandle(tp) {
        top.ymPrompt.close();
    }

    function copyAndMove() {
        var url = "${ctx}/nodes/copyAndMove/" + ownerId + "?startPoint=operative&endPoint=operative";
        if (isInMigrationFolder) {
            top.ymPrompt.win({
                message: url,
                width: 600,
                height: 500,
                title: "<spring:message code='file.title.copyOrMove'/>",
                iframe: {id: "copyAndMoveFrame"},
                btn: [["<spring:message code='button.newFolder'/>", 'newFolder', false, "ymNewFolder"], ["<spring:message code='button.copy'/>", 'copy', false, "btnCopy"], ["<spring:message code='button.cancel'/>", 'no', true, "btnCancel"]],
                handler: doCopyAndMove
            });
            top.ymPrompt_addModalFocus("#btnCopy");
        } else {
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
        }
        $("#ymNewFolder").css({"margin-left": "15px", "float": "left"}).parent().css({"padding-right": "65px"});
        $("#btnMove").attr("title", "<spring:message code='file.info.moveAclTips'/>");
    }

    function doCopyAndMove(tp) {
        var idArray = $("#fileList").getGridSelectedId();
        if (tp == 'copy' || tp == 'move') {
            if (isInMigrationFolder && departureOwnerId) {
                top.ymPrompt.getPage().contentWindow.submitCopyAndMove(tp, departureOwnerId, idArray);
            } else
                top.ymPrompt.getPage().contentWindow.submitCopyAndMove(tp, ownerId, idArray);
        } else if (tp == 'newFolder') {
            top.ymPrompt.getPage().contentWindow.createFolder();
        } else {
            top.ymPrompt.close();
            window.location.reload();
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
            title: "<spring:message code='file.title.copyOrMove'/>",
            message: "<spring:message code='file.info.autoRename'/>",
            handler: function (tp) {
                if (tp == 'ok') {
                    var url;
                    if (type == 'copy') {
                        url = "${ctx}/nodes/renameCopy/" + srcOwnerId + "?startPoint=operative&endPoint=operative";
                    } else {
                        url = "${ctx}/nodes/renameMove/" + srcOwnerId + "?startPoint=operative&endPoint=operative";
                    }
                    $.ajax({
                        type: "POST",
                        url: url,
                        data: {
                            'destOwnerId': ownerId,
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

    //删除节点
    function deleteNodes() {
        var title;
        var dataList = $("#fileList").getGridSelectedData(catalogData, opts_viewGrid);
        if (dataList.length == 0) {
            return;
        } else if (dataList.length == 1) {
            if (isBackupFolderType(dataList[0].type)) {
                return;
            }
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
                        error: function (data) {
                            var status = data.status;
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

    function setSync(nodeId, nodeType) {
        if (nodeId == undefined || nodeType == undefined) {
            var dataList = $("#fileList").getGridSelectedData(catalogData, opts_viewGrid);
            nodeId = dataList[0].id;
            nodeType = dataList[0].type;
        }

        var info = null;
        if (nodeType == 0) {
            info = "<spring:message code='file.info.setSyncFolder'/>";
        } else {
            info = "<spring:message code='file.info.setSyncFile'/>";
        }
        var params = {
            "token": "<c:out value='${token}'/>"
        };
        ymPrompt.confirmInfo({
            title: "<spring:message code='file.title.setSync'/>",
            message: info,
            btn: [["<spring:message code='button.ok'/>", "ok", true, "btn-focus"], ["<spring:message code='button.cancel'/>", "no", true, "btn-cancel"]],
            handler: function (tp) {
                if (tp == 'ok') {
                    $.ajax({
                        type: "POST",
                        url: "${ctx}/nodes/setSync/" + ownerId + "/" + nodeId,
                        data: params,
                        error: function (request) {
                            handlePrompt("error", "<spring:message code='operation.failed'/>");
                        },
                        success: function () {
                            handlePrompt("success", "<spring:message code='operation.success'/>");
                            if (viewMode == "file") {
                                listFile(top.currentPage, top.catalogParentId);
                            } else {
                                doSearch();
                            }
                        }
                    });
                }
            }
        });
        top.ymPrompt_addModalFocus("#btn-focus");

    }

    function cancelSync(nodeId, nodeType) {
        if (nodeId == undefined || nodeType == undefined) {
            var dataList = $("#fileList").getGridSelectedData(catalogData, opts_viewGrid);
            nodeId = dataList[0].id;
            nodeType = dataList[0].type;
        }

        var info = null;
        if (nodeType == 0) {
            info = "<spring:message code='file.info.cancelSyncFolder'/>";
        } else {
            info = "<spring:message code='file.info.cancelSyncFile'/>";
        }
        var params = {
            "token": "${token}"
        };
        ymPrompt.confirmInfo({
            title: "<spring:message code='file.title.cancelSync'/>",
            message: info,
            btn: [["<spring:message code='button.ok'/>", "ok", true, "btn-focus"], ["<spring:message code='button.cancel'/>", "no", true, "btn-cancel"]],
            handler: function (tp) {
                if (tp == 'ok') {
                    $.ajax({
                        type: "POST",
                        data: params,
                        url: "${ctx}/nodes/cancelSync/" + ownerId + "/" + nodeId,
                        error: function (request) {
                            handlePrompt("error", "<spring:message code='operation.failed'/>");
                        },
                        success: function () {
                            handlePrompt("success", "<spring:message code='operation.success'/>");
                            if (viewMode == "file") {
                                listFile(top.currentPage, top.catalogParentId);
                            } else {
                                doSearch();
                            }
                        }
                    });
                }
            }
        });

        top.ymPrompt_addModalFocus("#btn-focus");
    }
</script>
</body>
<%--文件标签相关 --%>
<script src="${ctx}/static/js/files/folderIndex.js" type="text/javascript"></script>
<script src="${ctx}/static/js/filelabel/filelabel.admin.js" type="text/javascript"></script>
<script src="${ctx}/static/js/filelabel/filelabel.search.js" type="text/javascript"></script>
<script type="text/javascript">
    var filelabelSearch = new FilelabelAdmin(ownerId, 1, '${token}', ownerId);
    var currFlPageNum = 0, currFlPageSize = 10;
    var isFirstLoadEnterpriseFls = true;

    /** 处理离职文件夹相关变量 **/
    var isInMigrationFolder = false,
        isMigrationFolder = false,
        departureOwnerId = null;
</script>
</html>