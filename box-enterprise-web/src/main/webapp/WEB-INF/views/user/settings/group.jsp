<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="pw.cdmi.box.disk.utils.CSRFTokenManager" %>
<%@ page import="pw.cdmi.box.disk.oauth2.domain.UserToken" %>
<%@ page import="org.apache.shiro.SecurityUtils" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%
    request.setAttribute("token",
            CSRFTokenManager.getTokenForSession(session));
%>
<%
    request.setAttribute("loginId", ((UserToken) SecurityUtils
            .getSubject().getPrincipal()).getId());
%>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="../../common/common.jsp" %>
    <script src="${ctx}/static/js/public/JQbox-hw-grid.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/JQbox-hw-page.js" type="text/javascript"></script>
</head>
<body>
<input type="hidden" id="tempNum"/>
<div id="pageLoadingContainer"
     style="z-index:10000; position:fixed; left:0; top:0; right:0; bottom:0; background:#fff;"></div>

<div class="sys-content">
    <div class="handle-con">
        <button type="button" class="btn btn-primary"
                onclick="openCreateGroup()" id="createSyncBtn"><i class="icon-create-group"></i><spring:message
                code='group.title.create'/></button>

        <div class="input-append pull-right">
            <input type="text" id="keyword" name="keyword"
                   class="span3 search-query" value="<c:out value='${filter}'/>"
                   placeholder='<spring:message code="group.title.name"/>'/>
            <button type="button" class="btn" id="searchButton">
                <i class="icon-search"></i>
            </button>
        </div>
        <select class="span2 pull-right" id="userType" name="userType" style="display:none">
            <option value="all" selected="selected"><spring:message code='group.field.label.select.type'/></option>
            <option value="public"><spring:message code='group.type.public'/></option>
            <option value="private"><spring:message code='group.type.private'/></option>
        </select>
    </div>

    <div class="table-con">
        <div id="groupList"></div>
        <div id="groupListPageBox"></div>
    </div>
</div>
</body>
<script type="text/javascript">
    var searchSpinner = new Spinner(optsBigSpinner).spin(document
        .getElementById("pageLoadingContainer"));
    var currentPage = 1;
    var opts_viewGrid = null;
    var navId = "";
    var opts_page = null;
    var headData = {
        "name": {
            "title": '<spring:message code="group.column.name" />',
            "width": "11%"
        },
        "description": {
            "title": '<spring:message code="group.column.description" />',
            "width": "12%"
        },
        "type": {
            "title": '<spring:message code="group.column.type" />',
            "width": ""
        },
        "memberNums": {
            "title": '<spring:message code="group.column.memberNum" />',
            "width": "18%"
        },
        "ownedByUserName": {
            "title": '<spring:message code="group.column.owner" />',
            "width": ""
        },
        "operation": {
            "title": '<spring:message code="group.column.operate" />',
            "width": "36%"
        }
    };

    $(document).ready(function () {
        var isSign = ${needDeclaration};
        if (isSign) {
            showDeclaration();
        }
        init();
        $("#searchButton").on('click', function () {
            initDataList(currentPage);
        });
        window.onload = processHash;
        window.onhashchange = processHash;
    });

    var processHash = function () {
        var hash = window.location.hash;
        if (hash.indexOf('#') != -1) {
            var m = hash.split('#');
            m = m[0];
            if (m) {
                if (m == 'none') {
                    return;
                }
                var orgStr = m;
                m = m.split('/');
                currentPage = m[0];
            }

        } else {
            currentPage = 1;
        }
        initDataList(currentPage);
    }

    function changeHash(curPage) {
        location.hash = '#' + curPage;
    }

    function getLoading() {
        return document.getElementById("pageLoadingContainer");
    }

    function init() {
        opts_viewGrid = $("#groupList").comboTableGrid({
            headData: headData,
            ItemOp: "user-defined",
            dataId: "id",
            taxisFlag: true
        });
        opts_page = $("#groupListPageBox").comboPage({
            lang: '<spring:message code="common.language1"/>',
            style: "page table-page"
        });

        initSearchEvent()
        $.fn.comboPage.pageSkip = function (opts, _idMap, curPage) {
            initDataList(curPage);
        };

        $.fn.comboTableGrid.setItemOp = function (tableData, rowData, tdItem,
                                                  colIndex) {
            switch (colIndex) {
                case "name":
                    var groupType = rowData.group.type;
                    var imgs = "";
                    if (groupType == "private") {
                        imgs = "<i class='icon-users icon-orange'></i> ";
                    }
                    if (groupType == "public") {
                        imgs = "<i class='icon-users'></i> ";
                    }
                    tdItem.find("p").append(rowData.group.name);
                    var text = tdItem.find("p").text();
                    tdItem.find("p").prepend(imgs);
                    tdItem.find("p").attr("title", text);
                    break;
                case "description":
                    tdItem.find("p").append(rowData.group.description);
                    var text = tdItem.find("p").text();
                    tdItem.find("p").attr("title", text);
                    tdItem.find("p").attr("width", text.length);
                    break;
                case "type":
                    if (rowData.group.type == "private") {
                        tdItem.find("p").append('<spring:message code="group.type.private" />').parent().attr("title", '<spring:message code="group.type.private" />');
                    } else if (rowData.group.type == "public") {
                        tdItem.find("p").append('<spring:message code="group.type.public" />').parent().attr("title", '<spring:message code="group.type.public" />');
                    }
                    break;
                case "memberNums":
                    tdItem.find("p").append(rowData.group.memberNums).parent()
                        .attr("title", rowData.group.memberNums);
                    break;
                case "ownedByUserName":
                    tdItem.find("p").append(rowData.group.ownedByUserName)
                        .parent()
                        .attr("title", rowData.group.ownedByUserName);
                    break;
                case "operation":
                    var name = rowData.group.name;
                    if (rowData.groupRole == "admin") {
                        var btns = '<input class="btn btn-small" type="button" value="<spring:message code='group.button.memberMgr' />" onClick="openGroupMemberMgr('
                            + rowData.group.id
                            + ')"/> <input class="btn btn-small" type="button" value="<spring:message code='group.button.modify' />" onClick="openGroupInfo('
                            + rowData.group.id
                            + ')"/> <input class="btn btn-small" type="button" value="<spring:message code='group.button.dissolution' />" onClick="deleteGroup('
                            + rowData.group.id + ')"/>';
                    }
                    if (rowData.groupRole == "member") {
                        var btns = '<input class="btn btn-small" type="button" value="<spring:message code='group.button.lookMgr' />" onClick="openGroupMemberMgr('
                            + rowData.group.id
                            + ')"/> <input class="btn btn-small" type="button" value="<spring:message code='group.button.exit' />" onClick="deleteMember('
                            + rowData.group.id + ')"/>';
                    }
                    if (rowData.groupRole == "manager") {
                        var btns = '<input class="btn btn-small" type="button" value="<spring:message code='group.button.memberMgr' />" onClick="openGroupMemberMgr('
                            + rowData.group.id
                            + ')"/> <input class="btn btn-small" type="button" value="<spring:message code='group.button.exit' />" onClick="deleteMember('
                            + rowData.group.id + ')"/>';
                    }
                    tdItem.find("p").html(btns);
                    break;
                default:
                    break;
            }
        };
    }

    function refreshCurPage() {
        initDataList(currentPage);
    }

    function initSearchEvent() {
        $("#keyword").keydown(function () {
            var evt = arguments[0] || window.event;
            if (evt.keyCode == 13) {
                $("#searchButton").click();
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

    function initDataList(curPage) {
        var pagePerDis = 20;
        var url = "${ctx}/user/group/listUserGroups";
        var keyword = $("#keyword").val();
        var userType = $("#userType").find("option:selected").val();
        var params = {
            "page": curPage,
            "keyword": keyword,
            "type": userType,
            "limit": pagePerDis,
            "token": "<c:out value='${token}'/>"
        };
        $.ajax({
            type: "POST",
            url: url,
            data: params,
            error: function (request) {
                spinnerFading();
                _statusText = request.statusText;
                if (_statusText == "Unauthorized") {
                    handlePrompt("error", "<spring:message code='group.error.operation.fail'/>");
                } else if (_statusText == "InvalidParameter") {
                    handlePrompt("error", "<spring:message code='group.error.operation.fail'/>");
                } else if (_statusText == "Forbidden") {
                    handlePrompt("error", "<spring:message code='group.error.forbidden'/>");
                } else if (_statusText == "InvalidSpaceStatus") {
                    handlePrompt("error", "<spring:message code='group.error.user.forbidden'/>");
                } else {
                    handlePrompt("error", "<spring:message code='group.error.operation.fail'/>");
                }
            },
            success: function (data) {
                spinnerFading();
                catalogData = data.data;
                currentPage = data.page;
                if (catalogData.length == 0 && curPage != 1) {
                    curPage--;
                    changeHash(curPage);
                    return;
                }
                $("#groupList").setTableGridData(catalogData, opts_viewGrid);
                $("#groupListPageBox").setPageData(opts_page, data.page,
                    data.numOfPage, data.totalNums);
                var pageH = $("body").outerHeight();
                top.iframeAdaptHeight(pageH);
            }
        });
    }

    function spinnerFading() {
        searchSpinner.stop();
        $("#pageLoadingContainer").css({
            "z-index": "-10000"
        });
    }

    function openCreateGroup() {
        var url = "${ctx}/user/group/addGroup";
        top.ymPrompt.win({
            message: url,
            width: 600,
            height: 330,
            title: '<spring:message code="group.title.create"/>',
            iframe: true,
            btn: [
                ['<spring:message code="button.ok"/>', 'ok', false,
                    "btn-focus"],
                ['<spring:message code="button.cancel"/>', 'no', true,
                    "btn-cancel"]],
            handler: createGroup
        });
        top.ymPrompt_addModalFocus("#btn-focus");
    }

    function createGroup(tp) {
        if (tp == 'ok') {
            top.ymPrompt.getPage().contentWindow.submitGroup();
        } else {
            top.ymPrompt.close();
        }
    }

    function openGroupInfo(id) {
        var url = "${ctx}/user/group/listGroupInfo/" + id;
        top.ymPrompt.win({
            message: url,
            width: 650,
            height: 300,
            title: '<spring:message code="group.title.modify"/>',
            iframe: true,
            btn: [
                ['<spring:message code="button.ok"/>', 'ok', false,
                    "btn-focus"],
                ['<spring:message code="button.cancel"/>', 'no', true,
                    "btn-cancel"]],
            handler: modifyGroup
        });
        top.ymPrompt_addModalFocus("#btn-focus");
    }

    function modifyGroup(tp) {
        if (tp == 'ok') {
            top.ymPrompt.getPage().contentWindow.submitGroup();
        } else {
            top.ymPrompt.close();
        }
    }

    function openGroupMemberMgr(id) {
        var url = "${ctx}/group/memberships/openGroupMemberMgr/" + id;
        top.ymPrompt.win({
            message: url,
            width: 720,
            height: 450,
            title: '<spring:message code="teamSpace.title.memberMgr"/>',
            iframe: true
        });
        top.ymPrompt_addModalFocus("#btn-focus");
    }

    function deleteGroup(id) {
        top.ymPrompt.confirmInfo({
            title: '<spring:message code="group.title.dissolution"/>',
            message: '<spring:message code="group.field.dissolution"/>' + '<br/>' + '<spring:message code="group.field.dissolution.tips"/>',
            width: 450,
            closeTxt: '<spring:message code="common.close"/>',
            handler: function (tp) {
                if (tp == "ok") {
                    executeDeleteGroup(id);
                }
            },
            btn: [['<spring:message code="button.ok"/>', "ok"],
                ['<spring:message code="button.cancel"/>', "cancel"]]
        });
    }

    function executeDeleteGroup(id) {
        $.ajax({
            type: "POST",
            data: {token: "<c:out value='${token}'/>"},
            url: "${ctx}/user/group/deleteGroup/" + id,
            error: function (request) {
                _statusText = request.statusText;
                if (_statusText == "Unauthorized") {
                    handlePrompt("error", "<spring:message code='group.error.operation.fail'/>");
                } else if (_statusText == "NoSuchGroup") {
                    handlePrompt("error", "<spring:message code='group.error.group.noexist'/>");
                } else if (_statusText == "Forbidden") {
                    handlePrompt("error", "<spring:message code='group.error.forbidden'/>");
                } else {
                    handlePrompt("error", "<spring:message code='group.error.operation.fail'/>");
                }
            },
            success: function () {
                top.handlePrompt("success", '<spring:message code="group.success.operation"/>');
                initDataList(currentPage);
            }
        });
    }

    function deleteMember(id) {
        top.ymPrompt.confirmInfo({
            title: '<spring:message code="group.title.exit"/>',
            message: '<spring:message code="group.field.exit"/>',
            width: 450,
            closeTxt: '<spring:message code="common.close"/>',
            handler: function (tp) {
                if (tp == "ok") {
                    executeDeleteMember(id);
                }
            },
            btn: [['<spring:message code="button.ok"/>', "ok"],
                ['<spring:message code="button.cancel"/>', "cancel"]]
        });
    }

    function executeDeleteMember(groupId) {
        var url = "${ctx}/group/memberships/deleteMember";
        var data = {
            groupId: groupId,
            userId: "<c:out value='${user.cloudUserId}'/>"
        };
        $.ajax({
            type: "GET",
            url: url,
            data: data,
            async: false,
            error: function (request) {
                handlePrompt("error",
                    "<spring:message code='operation.failed'/>");
            },
            success: function (data) {
                if (data == "Delete Itself") {
                    initDataList(currentPage);
                }

            }
        });
    }

    function sendAdminMgrMail(groupId) {
        var url = "${ctx}/group/memberships/sendAdminMgrMail";
        var data = {
            groupId: groupId
        };
        $.ajax({
            type: "POST",
            url: url,
            data: data
        });
    }

    function showDeclaration() {
        top.ymPrompt.win({
            message: '${ctx}/syscommon/declaration',
            width: 600,
            height: 400,
            title: '<spring:message code="declaration.sign.title"/>',
            iframe: true,
            btn: [['<spring:message code="agree.declaration"/>', 'yes', false, "btnSignDeclaration"], ['<spring:message code="disagree.declaration"/>', 'no', true, "btnSignDeclarationCancel"]],
            handler: doSignDeclaration
        });
        top.ymPrompt_addModalFocus("#btnSignDeclaration");
    }

    function doSignDeclaration(tp) {
        if (tp == 'yes') {
            top.ymPrompt.getPage().contentWindow.signDeclaration();
        } else {
            top.ymPrompt.close();
            window.location.href = "${ctx}/logout";
        }
    }

</script>
</html>
