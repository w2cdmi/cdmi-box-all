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
    <script src="${ctx}/static/js/public/JQbox-hw-files.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/JQbox-hw-page.js" type="text/javascript"></script>
</head>
<body>
<div id="pageLoadingContainer" style="z-index:10000; position:fixed; left:0; top:0; right:0; bottom:0; background:#fff;"></div>
<script type="text/javascript">new Spinner(optsBigSpinner).spin(document.getElementById("pageLoadingContainer"));</script>
<%@ include file="../common/header.jsp" %>

<div class="public-bar-con">
    <div class="public-layout">
        <div class="public-bar clearfix" id="publicBar">
            <div class="pull-left btn-toolbar">
                <button id="newTeamBtn" class="btn btn-primary" type="button" onClick="openCreateTeam()">
                    <i class="icon-create-teamspace"></i><spring:message code="teamSpace.button.create"/>
                </button>
            </div>
        </div>
    </div>
</div>

<div class="breadcrumb">
    <div class="breadcrumb-con clearfix">
        <ul id="breadcrumbCon">
            <li class="active"><span><spring:message code="teamSpace.title.teamSpace"/></span></li>
        </ul>
    </div>
</div>

<div class="body">
    <div class="body-con clearfix">
        <div class="files-block clearfix">
            <div id="fileListNull" class="teamspace-null">
                <spring:message code="teamSpace.ListIsNull"/><br/><br/>
                <button type="button" class="btn btn-large btn-primary" onclick="openCreateTeam()">
                    <spring:message code="teamSpace.button.create"/>
                </button>
            </div>
            <div id="fileList"></div>
            <div id="fileListPageBox"></div>
        </div>
    </div>
</div>
<%@ include file="../common/footer.jsp" %>
<script type="text/javascript">
    var opts_viewGrid = null;
    var opts_page = null;
    var listViewType = "thumbnail";
    var currentPage = 1;
    var catalogData = null;
    var isLinkHiden = <c:out value='${linkHidden}'/>;
    var M = {
        m_open: {title: "<spring:message code='button.open'/>", className: "icon-folder-open"},
        m_create: {title: "<spring:message code='teamSpace.title.createTeam'/>", className: "icon-add-group"},
        m_memberManagement: {
            title: "<spring:message code='teamSpace.button.btnMemberMgr'/>",
            className: "icon-add-member"
        },
        m_memberView: {title: "<spring:message code='teamSpace.title.viewMember'/>", className: "icon-ownner"},
        m_changeOwner: {title: "<spring:message code='teamSpace.title.changeowner'/>", className: "icon-pencil"},
        m_exit: {title: "<spring:message code='teamSpace.button.exit'/>", className: "icon-signout"},
        m_detail: {title: "<spring:message code='teamSpace.title.viewDetail'/>", className: "icon-info"},
        m_delete: {title: "<spring:message code='teamSpace.button.btnTeamDismiss'/>", className: "icon-cancel"},
        m_listLinks: {title: "<spring:message code='share.menu.shareLinks'/>", className: "icon-info"}
    };
    var headData = {
        "name": {"width": "20%"},
        "description": {"width": "30%"},
        "curNumbers": {"width": "30%"},
        "ownedByUserName": {"width": ""}
    };

    $(document).ready(function () {
        loadSysSetting("${ctx}", '<spring:message code="common.language1" />');

        navMenuSelected("navTeamSpace");

        init();

        processHash();
        window.onhashchange = processHash;

        $(window).bind("resize", autoGridTeamList)
    });

    function processHash() {
        var hash = window.location.hash;
        if (hash.indexOf('#') != -1) {
            var m = hash.substring(hash.indexOf("#") + 1);
            if (!m || m == 'none') {
                return;
            }
            currentPage = m;
        }

        listTeam(currentPage);
    }

    function changeHash(curPage) {
        location.hash = "#" + curPage;
    }

    function autoGridTeamList() {
        if ($(window).width() < 1360) {
            $("#fileList").find("> ul > li").css({"width": "31%", "margin-left": "0", "margin-right": "2%"});
        } else {
            $("#fileList").find("> ul > li").css({"width": "22.7%", "margin-left": "0", "margin-right": "2%"});
        }
    }

    function init() {
        opts_viewGrid = $("#fileList").comboGrid({
            headData: headData,
            dataId: "id",
            viewType: listViewType,
            style: "team-list",
            dragHandler: false,
            multiSelect: false
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
            listTeam(currentPage);
        };

        $.fn.comboGrid.setItemOp = function (rowData, colTag, colIndex) {
            switch (colIndex) {
                case 0:
                    try {
                        var alink = "<a id='" + rowData.id + "' onclick='teamDetailList(" + rowData.id + ");'></a>",
                            itemVal = colTag.find("span").text();
                        colTag.find("span").remove();

                        colTag.append(alink);
                        $("#" + rowData.id).text(itemVal);
                        $("#" + rowData.id).attr("title", itemVal);
                    } catch (e) {
                    }
                    break;
                case 1:
                    var itemVal = colTag.find("span").text();
                    colTag.addClass("paddingB10").find("span").attr("title", itemVal);
                    break;
                case 2:
                    colTag.find("span").text("<spring:message code='teamSpace.label.curMember' />: " + rowData.curNumbers);
                    break;
                case 3:
                    var name = (rowData.ownedByUserName == null || rowData.ownedByUserName == '' || rowData.ownedByUserName == undefined) ? "<spring:message code='teamspace.NoSuchOwner' />" : rowData.ownedByUserName
                    colTag.find("span").html("<spring:message code='teamSpace.label.owner' />: " + name);
                    colTag.find("span").attr("title", colTag.find("span").text());
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
                    menuData = {"m_create": M.m_create}
                } else {
                    var item = allSelectData[0];
                    menuData = {
                        "m_open": M.m_open,
                        "m_memberManagement": M.m_memberManagement,
                        "m_memberView": M.m_memberView,
                        "m_changeOwner": M.m_changeOwner,
                        "m_detail": M.m_detail,
                        "m_exit": M.m_exit,
                        "m_delete": M.m_delete,
                        "m_listLinks": M.m_listLinks
                    };
                    if (isLinkHiden) {
                        delete menuData.m_listLinks;
                    }
                    if (item.roleType == 0) {
                        delete menuData.m_exit;
                        delete menuData.m_memberView;
                        menuData.m_open.divider = true;
                        menuData.m_changeOwner.divider = true;
                    } else if (item.roleType == 1) {
                        delete menuData.m_memberView;
                        delete menuData.m_changeOwner;
                        delete menuData.m_delete;
                        menuData.m_open.divider = true;
                        menuData.m_memberManagement.divider = true;
                    } else {
                        delete menuData.m_listLinks;
                        delete menuData.m_memberManagement;
                        delete menuData.m_changeOwner;
                        delete menuData.m_delete;
                        menuData.m_open.divider = true;
                        menuData.m_memberView.divider = true;
                    }
                }
                return menuData;
            };

            initListButton(allSelectData);


        };

        $.fn.comboGrid.dbTrOp = function (rowData) {
            teamDetailList(rowData.id);
        };

        $.fn.gridMenuItemOp = function (btnType) {
            executeHandler(btnType);
        };
    }

    function initListButton(allSelectData) {
        var listButtonData = null;
        if (allSelectData.length == 0) {
            $("#listHandler").remove();
        } else {
            var item = allSelectData[0];
            listButtonData = {
                "m_memberManagement": M.m_memberManagement,
                "m_memberView": M.m_memberView,
                "m_changeOwner": M.m_changeOwner,
                "m_detail": M.m_detail,
                "m_exit": M.m_exit,
                "m_delete": M.m_delete,
                "m_listLinks": M.m_listLinks
            };
            if (isLinkHiden) {
                delete listButtonData.m_listLinks;
            }
            if (item.roleType == 0) {
                delete listButtonData.m_exit;
                delete listButtonData.m_memberView;
            } else if (item.roleType == 1) {
                delete listButtonData.m_changeOwner;
                delete listButtonData.m_memberView;
                delete listButtonData.m_delete;
            } else {
                delete listButtonData.m_listLinks;
                delete listButtonData.m_memberManagement;
                delete listButtonData.m_changeOwner;
                delete listButtonData.m_delete;
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
            var menuItem = $('<button onclick="executeHandler(\'' + key + '\')" class="btn btn-link" type="button" id="listBtn_' + key + '"><i class="' + listButtonData[key].className + '"></i> ' + listButtonData[key].title + '</button>');
            listBtnDiv.append(menuItem);
        }
    }

    function listTeam(curPage) {
        var pagePerDis = getCookie("fileListPerPageNum");
        pagePerDis = (pagePerDis == null || pagePerDis == '' || pagePerDis == undefined) ? 40 : pagePerDis;

        var url = "${ctx}/teamspace/teamSpaceList";
        var params = {
            "pageNumber": curPage,
            "pageSize": pagePerDis,
            "token": "<c:out value='${token}'/>"
        };
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
                    changeHash(curPage);
                    return;
                }
                setCookie("teamListPage", curPage);
                $("#fileList").setGridData(catalogData, opts_viewGrid);
                $("#fileListPageBox").setPageData(opts_page, data.number, data.size, data.totalElements);

                if (catalogData.length == 0) {
                    $("#fileListNull").show();
                }

                autoGridTeamList();

                comboxRemoveLoading("pageLoadingContainer");
            }
        });
    }

    function executeHandler(btnType) {
        try {
            if ("m_open" == btnType) {
                teamDetailList();
            } else if ("m_create" == btnType) {
                openCreateTeam();
            } else if ("m_memberManagement" == btnType || "m_memberView" == btnType) {
                openMemberMgr();
            } else if ("m_changeOwner" == btnType) {
                openChangeOwner();
            } else if ("m_detail" == btnType) {
                openGetOrEditTeam();
            } else if ("m_exit" == btnType) {
                openExitTeam();
            } else if ("m_delete" == btnType) {
                openDelete();
            } else if ("m_listLinks" == btnType) {
                teamLinksList();
            }

        } catch (e) {
            return;
        }
    }

    function openCreateTeam() {
        var url = '${ctx}/teamspace/openAddTeamSpace';
        top.ymPrompt.win({
            message: url,
            width: 550,
            height: 330,
            title: '<spring:message code="teamSpace.title.createTeam"/>',
            iframe: true,
            btn: [['<spring:message code="button.ok"/>', 'ok', false, "btn-focus"], ['<spring:message code="button.cancel"/>', 'no', true, "btn-cancel"]],
            handler: createTeamSpace
        });
        top.ymPrompt_addModalFocus("#btn-focus");
    }

    function createTeamSpace(tp) {
        if (tp == 'ok') {
            top.ymPrompt.getPage().contentWindow.submitTeamSpace();
        } else {
            top.ymPrompt.close();
        }
    }

    function openMemberMgr(teamId) {

        if (teamId == undefined || teamId == null || teamId == "") {
            var objData = $("#fileList").getGridSelectedData(catalogData, opts_viewGrid);
            teamId = objData[0].id;
        }
        var url = "${ctx}/teamspace/member/" + teamId;
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
                top.ymPrompt.win({
                    message: '${ctx}/teamspace/member/openMemberMgr/' + teamId,
                    width: 720,
                    height: 450,
                    title: title,
                    iframe: true
                });
            }
        });
    }

    function openExitTeam() {
        var objData = $("#fileList").getGridSelectedData(catalogData, opts_viewGrid);
        ymPrompt.confirmInfo({
            title: '<spring:message code="teamSpace.title.exit"/>',
            message: "<spring:message code='teamSpace.info.exit'/>",
            btn: [["<spring:message code='button.ok'/>", "ok", true, "btn-focus"], ["<spring:message code='button.cancel'/>", "no", true, "btn-cancel"]],
            handler: function (tp) {
                if (tp == 'ok') {
                    $.ajax({
                        type: "POST",
                        url: "${ctx}/teamspace/exitTeamSpace",
                        data: {
                            "teamMembershipsId": objData[0].memberId,
                            "teamId": objData[0].id,
                            'token': "<c:out value='${token}'/>"
                        },
                        error: function (request) {
                            handlePrompt("error", "<spring:message code='operation.failed'/>");
                        },
                        success: function (data) {
                            if (typeof(data) == 'string' && data.indexOf('<html>') != -1) {
                                window.location.href = "${ctx}/logout";
                                return;
                            }
                            if (data == "OK") {
                                ymPrompt.close();
                                handlePrompt("success", "<spring:message code='operation.success'/>");
                                listTeam(1);
                            } else if (data == "Forbidden") {
                                handlePrompt("error", "<spring:message code='teamSpace.error.ForbiddenExit'/>");
                            } else if (data == "NoFound") {
                                handlePrompt("error", "<spring:message code='teamMemberships.error.NoFound'/>");
                            } else {
                                handlePrompt("error", "<spring:message code='operation.failed'/>");
                            }
                        }
                    });
                }
            }
        });
        top.ymPrompt_addModalFocus("#btn-focus");

    }

    function openDelete() {
        var objData = $("#fileList").getGridSelectedData(catalogData, opts_viewGrid);
        var url = '${ctx}/teamspace/openDeleteTeamSpace?teamId=' + objData[0].id;
        top.ymPrompt.win({
            message: url,
            width: 520,
            height: 340,
            title: '<spring:message code="teamSpace.title.teamDismiss"/>',
            iframe: true,
            btn: [['<spring:message code="button.ok"/>', 'ok', false, "btn-focus"], ['<spring:message code="button.cancel"/>', 'no', true, "btn-cancel"]],
            handler: deleteTeamSpace
        });
        top.ymPrompt_addModalFocus("#btn-focus");
    }

    function deleteTeamSpace(tp) {
        if (tp == 'ok') {

            top.ymPrompt.getPage().contentWindow.submitUnlay();
        }
    }

    function openChangeOwner(teamId) {
        if (teamId == undefined || teamId == null || teamId == "") {
            var objData = $("#fileList").getGridSelectedData(catalogData, opts_viewGrid);
            teamId = objData[0].id;
        }
        var url = '${ctx}/teamspace/openChangeOwner?teamId=' + teamId;
        top.ymPrompt.win({
            message: url,
            width: 670,
            height: 350,
            title: '<spring:message code="teamSpace.title.changeowner"/>',
            iframe: true,
            btn: [['<spring:message code="button.ok"/>', 'ok', false, "btn-focus"], ['<spring:message code="button.cancel"/>', 'no', true, "btn-cancel"]],
            handler: questionChangeOwner
        });
        top.ymPrompt_addModalFocus("#btn-focus");
    }

    function questionChangeOwner(tp) {
        if (tp == 'ok') {
            top.ymPrompt.getPage().contentWindow.submitChangeOwner();
        }
    }

    function openGetOrEditTeam() {
        var objData = $("#fileList").getGridSelectedData(catalogData, opts_viewGrid);
        if (objData[0].roleType == 0) {
            openEditTeam(objData[0].id);
        } else {
            openTeamInfo(objData[0].id);
        }
    }

    function openEditTeam(teamId) {
        var objData = $("#fileList").getGridSelectedData(catalogData, opts_viewGrid);
        var url = '${ctx}/teamspace/openEditTeamSpace?teamId=' + objData[0].id;
        top.ymPrompt.win({
            message: url,
            width: 600,
            height: 500,
            title: '<spring:message code="teamSpace.title.teamSpaceInfo"/>',
            iframe: true,
            btn: [['<spring:message code="button.modify"/>', 'ok', false, "btn-focus"], ['<spring:message code="button.cancel"/>', 'no', true, "btn-cancel"]],
            handler: modifyTeamSpace
        });
        top.ymPrompt_addModalFocus("#btn-focus");
    }

    function openTeamInfo(teamId) {
        var objData = $("#fileList").getGridSelectedData(catalogData, opts_viewGrid);
        var url = '${ctx}/teamspace/openGetTeamInfo?teamId=' + objData[0].id;
        top.ymPrompt.win({
            message: url,
            width: 600,
            height: 500,
            title: '<spring:message code="teamSpace.title.teamSpaceInfo"/>',
            iframe: true,
            btn: [['<spring:message code="button.close"/>', 'ok', false, "btn-focus"]],
            handler: closeSpaceView
        });
        top.ymPrompt_addModalFocus("#btn-focus");
    }

    function closeSpaceView() {
        top.ymPrompt.close();
    }


    function modifyTeamSpace(tp) {
        if (tp == 'ok') {
            top.ymPrompt.getPage().contentWindow.submitTeamSpace();
        }
    }

    function teamDetailList(teamId) {
        if (teamId == undefined || teamId == null || teamId == "") {
            var objData = $("#fileList").getGridSelectedData(catalogData, opts_viewGrid);
            teamId = objData[0].id;
        }
        var url = "${ctx}/teamspace/file/" + teamId;
        jQuery('<form action="' + url + '" method="get">' + '</form>')
            .appendTo('body').submit().remove();
    }
    function teamLinksList(teamId) {
        if (teamId == undefined || teamId == null || teamId == "") {
            var objData = $("#fileList").getGridSelectedData(catalogData, opts_viewGrid);
            teamId = objData[0].id;
        }
        var url = "${ctx}/sharedlinks/teamSpace/" + teamId;
        jQuery('<form action="' + url + '" method="get">' + '</form>')
            .appendTo('body').submit().remove();
    }

    function formatQuota(size, u) {
        try {
            size = Number(size);
        } catch (e) {
            return '-';
        }
        u = u ? u : 0;
        var sizeUnit = ['MB', 'GB', 'TB'];
        if (u < 0 || u >= sizeUnit.length) {
            return '0MB';
        }
        if (size < 1) {
            return this.formatFileSize(size * 1024, --u);
        } else if (size < 1024) {
            if (sizeUnit[u] == 'MB') {
                return size + 'MB';
            }
            return size.toFixed(2) + sizeUnit[u];
        } else {
            return this.formatFileSize(size / 1024, ++u);
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

</script>

</body>
</html>