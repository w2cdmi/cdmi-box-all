<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="../common/common.jsp" %>
    <%@ include file="shareByMeSort.jsp" %>
    <link href="${ctx}/static/skins/default/css/layout.css" rel="stylesheet" type="text/css"/>
    <script src="${ctx}/static/js/public/JQbox-hw-files.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/JQbox-hw-page.js" type="text/javascript"></script>
</head>
<body>
<div id="pageLoadingContainer"
     style="z-index:10000; position:fixed; left:0; top:0; right:0; bottom:0; background:#fff;"></div>
<script type="text/javascript">new Spinner(optsBigSpinner).spin(document.getElementById("pageLoadingContainer"));</script>

<%@ include file="../common/header.jsp" %>

<div class="body">
    <div class="body-con clearfix">
        <div class="tab-menu">
            <div class="tab-menu-con">
                <ul class="nav nav-tabs">
                    <li><a href="${ctx}/myShares/"><spring:message code="share.menu.shareByMe"/></a></li>
                    <li class="active"><a href="${ctx}/sharedlinks/"><spring:message code="share.menu.shareLinks"/></a>
                    </li>
                </ul>
            </div>
        </div>
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
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="files-block clearfix">
                <div id="fileListNull" class="shareby-null"><spring:message code="shareLinks.MySharedEmpty"/></div>
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
    var opts_page = null;
    var viewMode = "file";
    var listViewType = getRootCookie("linkListViewType");
    if (null == listViewType || undefined == listViewType) {
        listViewType = "list";
    }

    var orderField = "name";

    var currentPage = 1;
    var catalogParentId = 0;
    var catalogData = null;
    var headData = {
        "name": {"width": "20%"},
        "path": {"width": "35%"},
        "linkCount": {"width": "15%"},
        "modifiedAt": {"width": "30%"}
    };
    var M = {
        m_cancelLink: {title: '<spring:message code="shareLinks.deleteShare"/>', className: "icon-signout"},
        m_viewLink: {title: "<spring:message code='shareLinks.viewLinks'/>", className: "icon-link"}
    };

    $(function () {
        navMenuSelected("navShareByMe");
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
                m = m.split('/');
                currentPage = m[0];
                catalogParentId = m[1];
            }

        }
        else {
            catalogParentId = 0;
        }
        initDataList(currentPage, catalogParentId);
    }

    function changeHash(curPage, parentId) {
        location.hash = '#' + curPage + '/' + parentId;
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
                initDataList(currentPage, catalogParentId);
            } else {
                doSearch(currentPage, keyword);
            }
        };
        $.fn.comboGrid.setItemOp = function (rowData, colTag, colIndex) {
            switch (colIndex) {
                case 0:
                    try {
                        var itemVal = colTag.find("span").text();
                        colTag.find("span").attr("title", itemVal);
                    } catch (e) {
                    }
                    break;
                case 1:
                    $.ajax({
                        type: "GET",
                        url: "${ctx}/path/" + rowData.ownedBy + "/" + rowData.id,
                        error: function (request) {
                            handlePrompt("error", '<spring:message code="shareLinks.listFilesFail"/>');
                        },
                        success: function (data) {
                            colTag.find("span").text(data);
                            colTag.find("span").attr("title", data);
                        }
                    });
                    break;
                case 2:
                    try {
                        var linkHandle, itemVal = colTag.find("span").text();
                        dataHandle = $("<span></span>");
                        colTag.find("span").remove();
                        linkHandle = "<a class='btn btn-link' onclick='shareLink(" + rowData.ownedBy + "," + rowData.id + ")'> " + rowData.linkCount + " <spring:message code='sharelinks.info.linkNum'/></a>";
                        dataHandle.append(linkHandle);
                        colTag.append(dataHandle);
                    } catch (e) {
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
                if (allSelectData.length == 0) {
                } else if (allSelectData.length == 1) {
                    menuData = {"m_cancelLink": M.m_cancelLink, "m_viewLink": M.m_viewLink};
                } else {
                    menuData = {"m_cancelLink": M.m_cancelLink};
                }
                return menuData;
            };

            initListButton(allSelectData);
        };

        $.fn.gridMenuItemOp = function (btnType) {
            executeHandler(btnType);
        };
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
        setRootCookie("linkListViewType", listViewType);
    }
    function initDataList(curPage, parentId) {
        var pagePerDis = getCookie("fileListPerPageNum");
        if (null == pagePerDis || pagePerDis == undefined || pagePerDis == "") {
            pagePerDis = 40;
        }
        viewMode = "file";
        $.ajax({
            type: "POST",
            url: "${ctx}/sharedlinks/list",
            data: {
                "ownerId": curUserId,
                "pageNumber": curPage,
                "orderField": orderField,
                "desc": isDesc,
                "pageSize": pagePerDis,
                "token": "<c:out value='${token}'/>"
            },
            error: function (request) {
                handlePrompt("error", '<spring:message code="shareLinks.listFilesFail"/>');
            },
            success: function (data) {
                catalogData = data.content;

                if (catalogData.length == 0 && curPage != 1) {
                    curPage--;
                    changeHash(curPage, parentId);
                    return;
                }

                $("#fileList").setGridData(catalogData, opts_viewGrid);
                $("#fileListPageBox").setPageData(opts_page, data.number, data.size, data.totalElements);
                buttonInit();
                $("#tipInfo").html('<spring:message code="sharelinks.info.send" arguments="' + data.totalElements + '" />');

                comboxRemoveLoading("pageLoadingContainer");
            }
        });

    }

    function initListButton(allSelectData) {
        var listButtonData = null;
        if (allSelectData.length == 0) {
            $("#listHandler").remove();
            $("#tipInfo").show();
        } else if (allSelectData.length == 1) {
            listButtonData = {"m_cancelLink": M.m_cancelLink, "m_viewLink": M.m_viewLink};
        } else {
            listButtonData = {"m_cancelLink": M.m_cancelLink};
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
            if ("m_cancelLink" == btnType) {
                cancelLink();
            } else if ("m_viewLink" == btnType) {
                shareLink();
            }
        } catch (e) {
            return;
        }
    }

    function shareLink(ownedByTmp, nodeIdTmp) {
        $("body").css("overflow-y", "hidden");
        var ownerId = ownedByTmp;
        var inodeId = nodeIdTmp;

        if (ownerId == undefined) {
            var node = $("#fileList").getGridSelectedData(catalogData, opts_viewGrid);
            ownerId = node[0].ownedBy;
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
                $("body").css("overflow-y", "scroll");
            }
        });
    }

    function linkHandle() {
        top.ymPrompt.close();
        if (viewMode == "file") {
            initDataList(currentPage, catalogParentId);
        } else {
            doSearch();
        }

        $("body").css("overflow-y", "scroll");
    }

    function cancelLink() {
        var objData = $("#fileList").getGridSelectedData(catalogData, opts_viewGrid);
        ymPrompt.confirmInfo({
            title: '<spring:message code="shareLinks.deleteShare"/>',
            message: '<spring:message code="shareLinks.cancelShareConfirm"/>', handler: function (tp) {
                if (tp == 'ok') {
                    var url = "${ctx}/sharedlinks/deleteOne";
                    var len = objData.length;
                    var ownerIds = "";
                    var inodeIds = "";
                    if (len > 1) {
                        url = "${ctx}/sharedlinks/deleteMul";
                        for (var i = 0; i < len; i++) {
                            if (i != 0) {
                                inodeIds = inodeIds + ",";
                            }
                            inodeIds = inodeIds + objData[i].id;
                        }
                    } else {
                        inodeIds = objData[0].id;
                    }
                    $.ajax({
                        type: "POST",
                        url: url,
                        data: {
                            "ownerId": curUserId,
                            "iNodeId": inodeIds,
                            "token": "<c:out value='${token}'/>"
                        },
                        error: function (request) {
                            doErrorWhenMulDel(request);
                        },
                        success: function () {
                            handlePrompt("success", '<spring:message code="operation.success"/>');
                            initDataList(currentPage, catalogParentId);
                        }
                    });
                }
            }
        });
    }

    function doErrorWhenMulDel(request) {
        switch (request.responseText) {
            case "BadRequest":
                handlePrompt("error", '<spring:message code="shareLinks.error.BadRquest"/>');
                break;
            case "Forbidden":
                handlePrompt("error", '<spring:message code="error.forbid"/>', '', '5');
                break;
            case "NoSuchLink":
            case "NoSuchItem":
                handlePrompt("error", '<spring:message code="shareLinks.error.NoSuchItems"/>');
                initDataList(currentPage, catalogParentId);
                break;
            default:
                handlePrompt("error", '<spring:message code="operation.failed"/>');
        }
    }

    function refreshData(tp) {
        top.ymPrompt.close();
        changeHash(currentPage, catalogParentId);
    }

    function buttonInit() {
        $("#listHandler").remove();
        $("#uploadBtn, #newFolder").show();
    }

    function listData() {
        initDataList(currentPage, catalogParentId);

    }

    function gotoShareMyFolderError(status) {
        top.ymPrompt.close();
        if (status == 404) {
            handlePrompt("error", "<spring:message code='error.notfound'/>");

            if (viewMode == "file") {
                initDataList(currentPage, catalogParentId);
            } else {
                doSearch();
            }
        } else if (status == 403) {
            handlePrompt("error", "<spring:message code='error.forbid'/>");
        }
    }

</script>
</body>
</html>