<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="../common/common.jsp" %>
    <%@ include file="../files/commonForFiles.jsp" %>
    <%@ include file="../files/commonForLinkFiles.jsp" %>
    <%@ include file="../files/UploadMessages.jsp" %>
    <%@ include file="../files/commonForUploadFiles.jsp" %>
    <link href="${ctx}/static/skins/default/css/layout.css" rel="stylesheet" type="text/css"/>
    <script src="${ctx}/static/js/public/JQbox-hw-files.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/JQbox-hw-page.js" type="text/javascript"></script>
</head>
<body>

<%@ include file="linkHeader.jsp" %>

<div class="public-bar-con">
    <div class="public-layout public-layout-no-menu">
        <div class="public-bar clearfix" id="publicBar">
            <div id="hoverDiv" class="pull-left btn-toolbar">
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
                <button id="newFolderBtn" class="btn btn-primary" type="button" onClick="createFolder()" style="display:none;">
                    <i class="new-icon-toolbar-create-folder"></i><spring:message code="button.newFolder"/>
                </button>
            </div>

            <div id="tipInfo" class="tip-info pull-left"><spring:message code="share.info.linkfrom" arguments="${shareUserName}"/></div>
            <div class="range-type pull-right">
                <div class="btn-group" data-toggle="buttons-radio">
                    <button id="viewTypeBtnList" onClick="viewType('list')" type="button" class="btn btn-small btn-link" title='<spring:message code="link.view.list"/>'><i class="icon-list"></i></button>
                    <button id="viewTypeBtnThumbnail" onClick="viewType('thumbnail')" type="button" class="btn btn-small btn-link" title='<spring:message code="file.tips.thumbnail"/>'><i class="icon-th"></i></button>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="breadcrumb">
    <div id="test" style="display:none"></div>
    <div class="breadcrumb-con clearfix breadcrumb-con-no-menu">
        <ul id="breadcrumbCon"></ul>
    </div>
</div>

<div class="body">
    <div class="body-con clearfix body-con-no-menu">
        <div class="pull-left clearfix">
            <div class="files-block clearfix">
                <div id="fileList"></div>
                <div id="fileListNull" class="file-catalog-null"><spring:message code="link.view.curFolderEmpty"/></div>
                <div id="fileListPageBox"></div>
                <div id="noPermission" class="page-error" style="display:none;">
                    <h3><spring:message code="teamSpace.msg.noPermission"/></h3>
                    <p><spring:message code="teamSpace.msg.contactAdmin"/></p>
                </div>
            </div>
        </div>
    </div>

    <%@ include file="../common/footer.jsp" %>

    <script type="text/javascript">
        var reqProtocol = "<%=request.getSession().getAttribute("reqProtocol")%>";
        var canPreview =<%=PreviewUtils.isPreview()%>;
        var curUserId = '<shiro:principal property="cloudUserId"/>';
        var linkCode = '<c:out value="${linkCode}"/>';
        var isLoginUser = '<c:out value="${isLoginUser}"/>';
        var ownerId = '<c:out value="${ownerId}"/>';
        var opts_viewGrid = null;
        var opts_page = null;
        var permissionFlag = null;
        var showPc = false;

        var listViewType = "list";
        var currentPage = 1;
        var catalogParentId = '<c:out value="${parentId}"/>';
        var rootParentId = '<c:out value="${parentId}"/>';
        var grandParentId = rootParentId;
        var catalogData = null;
        var orderField = "modifiedAt";
        var isNeedVerify = '<c:out value="${isNeedVerify}"/>';
        var openFolderSpinner;
        var headData = {
            "name": {"width": "60%"},
            "size": {"width": "20%"},
            "modifiedAt": {"width": "20%"}
        };

        var M = {
            m_open: {title: '<spring:message code="button.open"/>', className: "icon-folder-open"},
            m_download: {title: '<spring:message code="button.download" />', className: "icon-download"},
            m_move: {title: '<spring:message code="button.saveToMe" />', className: "icon-save"}
        };

        //For folder upload features
        var isFolderIsUploading = false;
        var uploadFolderFail = new HashMap();

        $(function () {
            loadSysSetting("${ctx}", '<spring:message code="common.language1" />');
            $(".upload-btn-box").css({"overflow": "hidden", "width": "0", "height": "0"});
            pageload();
        });

        function pageload() {
            init();

            window.onload = processHash;
            window.onhashchange = processHash;
        }

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

            }
            else {
                catalogParentId = '<c:out value="${parentId}"/>';
            }
            listFile(currentPage, catalogParentId);
        }

        function changeHash(curPage, parentId) {
            window.location.hash = '#' + curPage + '/' + parentId;
        }

        function init() {
            $("#tipInfo").find("strong").attr("title", $("#tipInfo").find("strong").text());

            initViewType(listViewType);
            //初始化文件夹上传菜单
            createMenu();
            opts_viewGrid = $("#fileList").comboGrid({
                headData: headData,
                dataId: "id",
                dragHandler: false,
                viewType: listViewType
            });

            opts_page = $("#fileListPageBox").comboPage({lang: '<spring:message code="common.language1"/>'});
            $.fn.comboPage.pageSkip = function (opts, _idMap, curPage) {
                changeHash(curPage, catalogParentId);
            };

            $.fn.comboGrid.setItemOp = function (rowData, colTag, colIndex) {
                switch (colIndex) {
                    case 0:
                        try {
                            var alink;
                            colTag.find("span").remove();
                            if (isFolderType(rowData.type)) {
                                alink = "<a id='" + rowData.id + "' onclick='openFolder(" + rowData.id + ")' title'" + rowData.name + "'>" + rowData.name + "</a>";
                            } else {
                                var fileType = _getStandardType(rowData.name);
                                var videoType = rowData.name.substring(rowData.name.lastIndexOf(".") + 1).toLowerCase();
                                if (fileType == "img") {
                                    alink = "<a id='" + rowData.id + "' rel='lightbox|/|${ctx}/share/getDownloadUrl/" + rowData.id + "/" + linkCode + "?" + Math.random() + "|/|" + rowData.name + "|/|" + rowData.id + "' onclick='downloadImgFile($(this))'  title'" + rowData.name + "'>" + rowData.name + "</a>";
                                } else if (fileType == "video" && videoType == "mp4") {
                                    alink = "<a id='" + rowData.id + "' onclick='playVideo(" + rowData.id + ")' title='" + rowData.name + "'>" + rowData.name + "</a>";
                                } else if (canPreview && rowData.previewable) {
                                    alink = "<a id='" + rowData.id + "' onclick='previewFile(" + rowData.id + "," + rowData.ownedBy + ")' title='" + rowData.name + "'>" + rowData.name + "</a>";
                                } else {
                                    alink = "<a id='" + rowData.id + "' onclick='downloadFile(" + rowData.id + ")'  title'" + rowData.name + "'>" + rowData.name + "</a>";
                                }
                            }
                            colTag.append(alink);

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
                    if (allSelectData.length == 0) {

                    } else if (allSelectData.length == 1) {
                        if (isFolderType(allSelectData[0].type)) {
                            if (isLoginUser == "true" && permissionFlag != null && permissionFlag["download"] == 1) {
                                menuData = {"m_open": M.m_open, "m_move": M.m_move};
                            } else {
                                menuData = {"m_open": M.m_open};
                            }
                        } else {
                            if (isLoginUser == "true" && permissionFlag != null && permissionFlag["download"] == 1) {
                                menuData = {"m_download": M.m_download, "m_move": M.m_move};
                            } else if (permissionFlag != null && permissionFlag["download"] == 1) {
                                menuData = {"m_download": M.m_download};
                            }
                        }
                        if (isBackupFolderType(allSelectData[0].type)) {
                            delete menuData.m_move;
                        }

                    } else {
                        if (isLoginUser == "true" && permissionFlag != null && permissionFlag["download"] == 1) {
                            menuData = {"m_move": M.m_move};
                        }
                    }
                    return menuData;
                };

                initListButton(allSelectData);
            };


            $.fn.comboGrid.dbTrOp = function (rowData) {
                if (isFolderType(rowData.type)) {
                    ownerId = rowData.ownedBy;
                    openFolder(rowData.id);
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

            sendUpLoad("link");
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
        }

        function listFile(curPage, parentId, field) {
            permissionFlag = getNodePermission();
            var url = "${ctx}/share/list";
            if (field == undefined) {
                field = orderField;
            }
            var params = {
                "ownerId": ownerId,
                "parentId": parentId,
                "linkCode": linkCode,
                "pageNumber": curPage,
                "orderField": field,
                "desc": true,
                "token": "<c:out value='${token}'/>"
            };

            $.ajax({
                type: "POST",
                url: url,
                data: params,
                error: function (request) {
                    if (openFolderSpinner) {
                        openFolderSpinner.stop();
                    }
                    doDownLoadLinkError(request);
                },
                success: function (data) {
                    grandParentId = parentId;
                    catalogData = data.content;

                    if (catalogData.length == 0 && curPage != 1) {
                        curPage--;
                        changeHash(curPage, parentId);
                        return;
                    }

                    $("#fileList").setGridData(catalogData, opts_viewGrid);
                    $("#fileListPageBox").setPageData(opts_page, data.number, data.size, data.totalElements);

                    buttonInit();
                }
            });

            createBreadcrumb(parentId, ownerId);

        }

        function createBreadcrumb(catalogParentId, ownerId) {

            var params = {
                "ownerId": ownerId,
                "linkCode": linkCode,
                "inodeId": catalogParentId,
                "parentId": rootParentId
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
                    $("#breadcrumbCon").html("");
                    for (var i = data.length - 1; i >= 0; i--) {
                        if (i == 0) {
                            if (data.length == 1) {
                                breadcrumbItem = $("<li class=\"active\"><span title=\"" + "<spring:message code='link.view.rootFolder' />" + data[i].name + ")\">" + "<spring:message code='link.view.rootFolder' />" + data[i].name + ")</span></li>");
                                $("#breadcrumbCon").append(breadcrumbItem);
                            }
                            else {
                                breadcrumbItem = $("<li class=\"active\"><span>" + data[i].name + "</span></li>");
                                $("#breadcrumbCon").append(breadcrumbItem);
                            }
                        }
                        else if (i == data.length - 1) {
                            breadcrumbItem = $("<li><a href=\"#1/" + data[i].id + "\" id='" +
                                data[i].id + "'><span title=\"" + "<spring:message code='link.view.rootFolder' />" + data[i].name + ")\">" + "<spring:message code='link.view.rootFolder' />" + data[i].name + ")</span></a></li>");
                            $("#breadcrumbCon").append(breadcrumbItem);
                        } else {
                            breadcrumbItem = $("<li><a href=\"#1/" + data[i].id + "\" ><span>" + data[i].name + "</span></a></li>");
                            $("#breadcrumbCon").append(breadcrumbItem);
                        }
                    }

                    $("#breadcrumbCon span").tooltip({
                        container: "body",
                        placement: "top",
                        delay: {show: 100, hide: 0},
                        animation: false
                    });

                    var len = data.length,
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

        function initListButton(allSelectData) {
            var listButtonData = null;
            if (allSelectData.length == 0) {
                $("#listHandler").remove();
                $("#tipInfo").show();
            } else if (allSelectData.length == 1) {
                if (isFolderType(allSelectData[0].type)) {
                    if (isLoginUser == "true" && permissionFlag != null && permissionFlag["download"] == 1) {
                        listButtonData = {"m_move": M.m_move};
                    }
                } else {
                    if (isLoginUser == "true" && permissionFlag != null && permissionFlag["download"] == 1) {
                        listButtonData = {"m_download": M.m_download, "m_move": M.m_move};
                    } else if (permissionFlag != null && permissionFlag["download"] == 1) {
                        listButtonData = {"m_download": M.m_download};
                    }
                }
                if (isBackupFolderType(allSelectData[0].type)) {
                    delete listButtonData.m_move;
                }
            } else {
                if (isLoginUser == "true" && permissionFlag != null && permissionFlag["download"] == 1) {
                    listButtonData = {"m_move": M.m_move};
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
            for (var key in listButtonData) {
                var menuItem = $('<button onclick="executeHandler(\'' + key + '\')" class="btn btn-link" type="button" id="listBtn_' + key + '"><i class="' + listButtonData[key].className + '"></i> ' + listButtonData[key].title + '</button>');
                listBtnDiv.append(menuItem);
            }
        }

        function executeHandler(btnType) {
            try {
                if ("m_open" == btnType) {
                    openFolder();
                } else if ("m_download" == btnType) {
                    downloadFile();
                } else if ("m_move" == btnType) {
                    copyAndMove();
                }
            } catch (e) {
                return;
            }
        }

        function openFolder(id) {
            if (id == null || id == "") {
                var objData = $("#fileList").getGridSelectedData(catalogData, opts_viewGrid);
                ownerId = objData[0].ownedBy;
                id = objData[0].id;
            }
            openFolderSpinner = new Spinner(optsMiddleSpinner).spin($("#" + id).parents(".rowli").find("> div").get(0));
            changeHash(1, id);
        }

        function downloadFile(id) {
            var nodePermission = getNodePermission();
            if (nodePermission == null || nodePermission == 'null') {
                handlePrompt("error", "<spring:message code='link.view.NoSuchItems'/>");
                return;
            }
            if (nodePermission["download"] != 1) {
                handlePrompt("error", "<spring:message code='error.forbid'/>");
                return;
            }

            if (id == null || id == "") {
                var selectedId = $("#fileList").getGridSelectedId();
                id = selectedId[0];
            }

            $.ajax({
                type: "GET",
                async: false,
                url: "${ctx}/share/getDownloadUrl/" + id + "/" + linkCode + "?" + Math.random(),
                error: function (request) {
                    doDownLoadLinkError(request);
                },
                success: function (data) {
                    jQuery('<form action="' + data + '" method="get"></form>').appendTo('body').submit().remove();
                }
            });
        }

        function downloadImgFile(that) {
            var nodePermission = getNodePermission();
            if (nodePermission == null || nodePermission == 'null') {
                handlePrompt("error", "<spring:message code='link.view.NoSuchItems'/>");
                return;
            }
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
            var nodePermission = getNodePermission();
            if (nodePermission == null || nodePermission == 'null') {
                handlePrompt("error", "<spring:message code='link.view.NoSuchItems'/>");
                return;
            }
            if (nodePermission["preview"] != 1) {
                handlePrompt("error", "<spring:message code='error.forbid'/>");
                return;
            }
            $.ajax({
                type: "GET",
                async: false,
                url: "${ctx}/share/getDownloadUrl/" + id + "/" + linkCode + "?" + Math.random(),
                error: function (request) {
                    doDownLoadLinkError(request);
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
            var nodePermission = getNodePermission();
            if (nodePermission == null || nodePermission == 'null') {
                handlePrompt("error", "<spring:message code='link.view.NoSuchItems'/>");
                return;
            }
            if (nodePermission["preview"] != 1) {
                handlePrompt("error", "<spring:message code='error.forbid'/>");
                return;
            }
            window.open("${ctx}/files/gotoPreview/" + ownerId + "/" + fileId + "?parentPageType=link&linkCode=" + linkCode);
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

        function doCopyAndMove(tp) {
            var idArray = $("#fileList").getGridSelectedId();
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
                            listFile(currentPage, catalogParentId);
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
                        case "SameParentConflict":
                            unLayerLoading();
                            ymPrompt.getPage().contentWindow.handlePrompt("error", "<spring:message code='file.errorMsg.parentNotChange'/>", '', 10);
                            break;
                        case "SameNodeConflict":
                            unLayerLoading();
                            ymPrompt.getPage().contentWindow.handlePrompt("error", "<spring:message code='file.errorMsg.sameFolder'/>", '', 10);
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
                            listFile(currentPage, catalogParentId);
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
                title: '<spring:message code="link.task.title" />',
                message: '<spring:message code="link.task.renameFail" />',
                handler: function (tp) {
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

        function buttonInit() {
            $("#listHandler").remove();

            $("#newFolderBtn").hide();
            $(".upload-btn-box").css({"overflow": "hidden", "width": "0", "height": "0"});
            if (!permissionFlag) {
                return;
            }
            $("#tipInfo").show();

            if (permissionFlag != null && permissionFlag["upload"] == 1) {
                $(".upload-btn-box").removeAttr("style");
                $("#tipInfo").hide();
            }
        }
        function getNodePermission() {
            var permission = null;
            var url = "${ctx}/share/nodePermission?" + Math.random();
            var params = {
                "ownerId": ownerId,
                "nodeId": rootParentId,
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
        //构造文件夹上传下拉菜单
        function createMenu() {
            if (isFirFoxBrowser() || (!$.support.leadingWhitespace || navigator.userAgent.indexOf("MSIE 9.0") > 0)) {
                $("#spanUpload").removeClass("fileinput-buttonDir");
                $("#spanUpload").addClass("fileinput-button");
            }
            var menu = $('<ul id="menuID" class="dropdown-menu1">' + '</ul>');
            var menuItem2 = $('<li><span id="uploadMenu2"><span class="uploadBtn btn fileinput-button"style="width: 45px;padding: 2px;text-align:left"><span><spring:message code='button.uploadDir'/></span><form id="dirform" style="padding-top:2px"><input id="dirUpload0" title="<spring:message code='button.uploadDir.inputTitle'/>" type="file" name="files[]" multiple="multiple" webkitdirectory/></form></span></span></li>');
            var ffMenuItem = $('<li><span id="ffUplodSpan"><span class="uploadBtn btn fileinput-button"style="width: 45px;padding: 2px;text-align:left"><span><spring:message code='button.uploadDir'/></span><form id="dirform" style="padding-top:2px"><input id="dirUpload1" title="<spring:message code='button.uploadDir.inputTitle'/>" type="file" name="files[]" multiple="multiple" /></form></span></span></li>');
            var menuItem4_chro = $('<li><span class="uploadBtn btn fileinput-button"style="width: 45px;padding: 2px;text-align:left"><span><span><spring:message code='button.uploadFile'/></span> <input id="fileUpload" title="<spring:message code='button.upload.inputTitle'/>" type="file" name="files[]" multiple /></span></span></li>');

            var menuItem3 = $('<li id="btnLi"><button id="uploadfile" class="uploadDirBtn"><spring:message code='button.uploadDir'/></button></li>');
            var menuItem4 = $('<li><span class="uploadBtn btn fileinput-button"style="width: 80px;padding: 2px;"><span id="fileUpload2"><input id="fileUpload" type="file" name="files[]" multiple /></span></span></li>');

            menuItem3.on("click", uploadDir)
            if (!isFirFoxBrowser() && !(!$.support.leadingWhitespace || navigator.userAgent.indexOf("MSIE 9.0") > 0)) {
                $("#uploadBtnBoxForJS").append(menu);
            }
            else {
                $("#uploadBtnBox").append(menu);
            }

            if (isChromeBrowser()) {
                $("#menuID").append(menuItem4_chro);
                $("#menuID").append(menuItem2);
                $("#dirUpload0").on("click", function (e) {
                    unSupportAlert(e);
                });
            } else if (isFirFoxBrowser()) {
                $("#menuID").append(menuItem4_chro);
                $("#menuID").append(ffMenuItem);
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

        var files;
        //进度条刷新任务
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

        function uploadDir() {
            /**automtically set variables for uploadDir */
            var reqUrl = window.location.href;
            var reqServer = "${pageContext.request.getServerName()}";
            var reqPort = window.location.port;
            var reqProtcol = reqProtocol;
            var isHttps = 0;
            if (!reqPort) {
                reqPort = "80";
                if (reqProtocol == "https") {
                    reqPort = "443";
                }
            }
            if (reqProtocol != "http") {
                isHttps = 1;
            }
            var domainName = "${pageContext.request.getHeader('Host')}";

            if (domainName && !domainName.split(":")[1]) {
                reqServer = domainName;
            }
            /**automtically set variables for uploadDir */

            try {
                var UploadObj = new ActiveXObject("UPLOADPLUGIN.UploadPluginCtrl.1");
                addActiveObj();
            } catch (e) {
                downloadCAB();
                return;
            }

            //addActiveObj();
            var url = "${ctx}/uploadFolder/folderPreupload/" + ownerId + "/" + catalogParentId;
            var fileName;

            if (isFolderIsUploading) {
                handlePrompt("error", "<spring:message code='error.uploadfolder.limit'/>");
                return;
            }
            try {

                //打开打开浏览对话框，选择本地路径.
                var isFolderSelected = Upload.BrowsePath();
                //点击弹出框的取消，退出
                if (isFolderSelected === 0) {
                    return;
                }

                //关闭文件选择框后下拉菜单消失
                $("#menuID").css("display", "none");

                //文件大小检查
                Upload.SetMaxFileSizeLimit(2097152, 2);
                //选择完成后关闭选择框显示

                //点击弹出框的取消，退出
                if (isFolderSelected === 0) {
                    return;
                }

                var filesStr = Upload.GetUploadFiles();
                //文件超过大小
                if (filesStr == null || filesStr == undefined || filesStr.length === 0) {
                    return;
                } else {
                    files = eval("(" + filesStr + ")");

                    //上传空文件夹提示
                    if (files == null || files == undefined || files.length == 0) {
                        handlePrompt("error", "<spring:message code='error.uploadfolder.null'/>");
                        return;
                    }

                    if (files.length > 100) {
                        handlePrompt("error", "<spring:message code='error.uploadfolder.file.limit'/>");
                        return;
                    }
                }

                isFolderIsUploading = true;
                //预上传
                Upload.SetSrvUFM(reqServer, url, reqPort, isHttps);// 调用预上传接口
                //文件上传
                Upload.StartUpload();
            }
            catch (e) {
                handlePrompt("error", "<spring:message code='error.uploadfolder.file.fail'/>");
                return;
            }

            $.each(files, function (index, file) {
                createProcess(file.key, file.file, file.size);
                uploadComplete.push(file.key);
                uploadDelete.push(file.key);
                //uploadErrorFiles.push(file.file);
                uploadRateMap.put(index, 0);
            });
            intervalReset = false;
            //检测上传进度
            refreshProcess();
        }

        function createProcess(key, file, size) {
            var fileName = file.substring(file.lastIndexOf("\\") + 1, file.length);
            $("#uploadModal").show();
            var num = $("#uploadQueue > div").length;
            $("#showUploadTotalNum").text(files.length);

            var context = $("<div/>").addClass("fileUpload-queue-item").appendTo("#uploadQueue");

            $(context).attr("id", key);

            var node = $("<div/>").append($("<span/>").addClass("title").text(fileName)).append($("<span/>").addClass("size").text(" (" + showSize(size) + ") -"))
                .append($("<span/>").addClass("info").text("0%"));

            var nodeprocess = $("<div/>").addClass("progress progress-info")
                .append($("<div/>").addClass("bar"));

            node.appendTo(context);
            nodeprocess.appendTo(context);
        }


        function refreshProcess() {
            if (intervalReset) {
                return;
            }

            if (files == null || files == undefined) {
                return;
            }
            $.each(files, function (index, file) {
                var uploadStatus = Upload.GetUploadStatus(file.key);

                uploadStatusMap.put(index, uploadStatus);

                if (index == 0) {
                    var rate = getRate(file, index);

                    uploadRateMap.put(index, rate);

                }
                else {
                    preIndex = index - 1;
                    var perStatus = uploadStatusMap.get(preIndex);

                    if (perStatus == 2) {
                        var rate = getRate(file, index);
                        uploadRateMap.put(index, rate);
                    }
                }

                uploadStatusCurrent = uploadStatus;

                var onlyKey = file.key;
                if (uploadFinishMap.get(onlyKey) == null) {
                    uploadFinishMap.put(onlyKey, "unfinish");
                }
                var element = document.getElementById(onlyKey);

                //检测文件上传是否出现异常
                if (uploadStatus == 2) {
                    var rateCur = $(element).find(".info").text();
                    rate = 100;
                    $(element).find(".info").text(rate + "%");
                    $(element).find(".bar").css("width", rate + "%");

                    ////console.log("onlykey----"+uploadFinishMap.get(onlyKey));

                    if (uploadFinishMap.get(onlyKey) == "unfinish") {
                        var numStr = $("#showUploadedNum").text();
                        var showUploadedNum = parseInt(numStr) + 1;

                        ////////console.log("index-----"+index+"---showUploadedNum:"+showUploadedNum);
                        $("#showUploadedNum").text(showUploadedNum);
                        uploadFinishMap.put(onlyKey, "finish");

                    }

                    if (rateCur != "100%") {
                        uploadComplete.pop(file.key);
                        $(element).find(".info").text("<spring:message code='uploadify.msg.complete'/>");

                        setTimeout(function () {
                            $(element).fadeOut(300, function () {
                                $(element).remove();
                                //checkUploadComplete();
                            })
                        }, 3000);
                    }
                }

                else if (uploadStatus == 3 || file.size == 0) {
                    var fileName = file.file;
                    $(element).find(".info").addClass("error").text("<spring:message code='file.title.uploadFailed'/>");
                    $(element).find(".bar").css("width", "0%");

                    uploadFolderFail.put(file.key, fileName.substring(fileName.lastIndexOf("\\") + 1, fileName.length));
                    uploadComplete.pop(file.key);

                    setTimeout(function () {
                        $(element).fadeOut(200, function () {
                            $(element).remove();
                            //checkUploadComplete();
                        })
                    }, 1000);

                }
                else {
                    //console.log("reflash rate index-------"+ index);
                    var cruntRate = uploadRateMap.get(index);
                    //console.log("reflash rate index-------"+ index+"-----rate---"+cruntRate);
                    $(element).find(".info").text(Math.round(cruntRate) + "%");
                    $(element).find(".bar").css("width", Math.round(cruntRate) + "%");

                }

            });
            //console.log("----------------" + isUpload);
            var num = $("#uploadQueue > div").length;
            if ((uploadComplete.length == 0) && num == 0) {
                setTimeout(function () {
                    $("#uploadModal").fadeOut(300, function () {
                        $("#showUploadedNum").text("0");
                        $("#uploadModal").hide();
                        checkUploadComplete();
                    })
                }, 2000);
            }
        }

        function getRate(file, index) {
            var step = everyAdd(file.size);

            var preRate = uploadRateMap.get(index);

            var curentRate = step + preRate;
            if (curentRate > 99) {
                curentRate = 99;
            }

            return curentRate;
        }

        function everyAdd(size) {
            if (size > 1024 * 1024 * 1024) {
                return 0.2;
            }
            if (size > 500 * 1024 * 1024) {
                return 3;
            }
            if (size > 100 * 1024 * 1024) {
                return 5;
            }

            return 10;
        }

        function showSize(size) {
            var sizeStr;
            if (size < 1024) {
                sizeStr = (size).toFixed(2) + "B";
            }
            else if (size >= 1024 && size < 1024 * 1024) {
                sizeStr = (size / 1024).toFixed(2) + "KB";
            }
            else if (size >= 1024 * 1024 && size < 1024 * 1024 * 1024) {
                sizeStr = (size / 1024 / 1024).toFixed(2) + "MB";
            }
            else {
                sizeStr = (size / 1024 / 1024 / 1024).toFixed(2) + "GB";
            }

            return sizeStr;
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

        function downloadCAB() {
            try {
                Upload.SetMaxFileSizeLimit(2097152, 2);
            }
            catch (e) {
                var url = "${ctx}/static/help/guide/activeXguide.html";
                ymPrompt.win({
                    message: url,
                    width: 600,
                    height: 180,
                    title: "<spring:message code='common.tip'/>",
                    iframe: true
                });

            }
            return;
        }

        function childrenCallBack() {
            var divhtml = '<OBJECT classid=clsid:AE60D877-3019-4F01-8CBE-017A4B7FF788 codebase="${ctx}/static/js/public/Upload.CAB#version=2,1,2,10" id=Upload style="HEIGHT: 1px; WIDTH: 1px"></OBJECT>';
            var testHtml = document.getElementById("test").innerText;

            if ("" == testHtml) {
                $("#test").html(divhtml);
            }

            //uploadDir();
        }

        function addActiveObj() {
            var divhtml = '<OBJECT classid=clsid:AE60D877-3019-4F01-8CBE-017A4B7FF788 codebase="${ctx}/static/js/public/Upload.CAB#version=2,1,2,10" id=Upload style="HEIGHT: 1px; WIDTH: 1px"></OBJECT>';
            var testHtml = document.getElementById("test").innerText;
            if ("" == testHtml) {
                $("#test").html(divhtml);
            }
        }

        //控制上传文件/文件夹下拉菜单的显隐
        function handleDropMenu() {
            var disPVal = $("#menuID")[0].style.display;
            if (disPVal === "none") {
                $("#menuID").css("display", "block");
            } else {
                $("#menuID").css("display", "none");
            }
        }

    </script>
</body>
</html>
