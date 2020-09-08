<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%
    request.setAttribute("token",
            CSRFTokenManager.getTokenForSession(session));
%>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="../common/common.jsp" %>
    <%@ include file="../common/commonForRole.jsp" %>
    <script src="${ctx}/static/js/public/JQbox-hw-grid.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/JQbox-hw-page.js" type="text/javascript"></script>
</head>
<body>
<div class="pop-content">
    <div class="pop-grant-authority">
        <div class="file-name clearfix" id="linkFileName"><span class="file-icon fileImg-folder"></span> <span
                class="name-txt" title="<c:out value='${fileName}'/>"><c:out value='${fileName}'/></span></div>
        <div class="member-list-header"><strong id="memberListTitle"></strong></div>
        <div class="member-list">
            <div id="enCharSearch" class="en-char-search"></div>
            <div id="memberList"></div>
            <div id="memberListNoAuth"></div>
            <div id="memberListPageBox"></div>
        </div>
        <div id="memberTypeCon" class="member-type dropup row-fluid">
            <a class="btn dropdown-toggle span12" data-toggle="dropdown"><span class="caret"></span><strong
                    id="selectedAuth"><spring:message code="teamSpace.option.editor"/></strong></a>
            <ul class="dropdown-menu">
                <c:forEach items="${systemRoles}" var="systemRole">
                    <li><a id="memberType_<c:out value='${systemRole.name}'/>"
                           onclick="selectSomeOne(this,'<c:out value="${systemRole.name}"/>');"/></a></li>
                </c:forEach>
            </ul>
            <input type="hidden" id="txtSlctAuthType" value="editor"/>
        </div>
        <div id="isavileble" style="text-align: center;display:none "><input id="isshow" type="checkbox"
                                                                             style="margin: 4px"
                                                                             onclick="modifyNodeIsVisible()">
            <spring:message code="Othermembers.notvisible"/></div>

        <div id="goGrantCon">
            <button class="btn btn-link" type="button" onclick="switchViewTo('notGranted');"><spring:message
                    code="teamSpace.label.addMoreMember"/></button>
        </div>

        <div id="submitBtnCon" class="btn-con">
            <button id="submitBtn" type="button" class="btn btn-primary" onclick="submitMember()"><spring:message
                    code="teamSpace.button.btnAdd"/></button>
            <button type="button" class="btn" onclick="switchViewTo('granted')"><spring:message
                    code="button.cancel"/></button>
        </div>
        <div id="manageBtnCon" class="btn-con">
            <button id="" type="button" class="btn btn-primary" onclick="top.ymPrompt.close()"><spring:message
                    code="button.close"/></button>
        </div>
    </div>
</div>

<script type="text/javascript">
    var currentPage = 1;
    var teamId = "<c:out value='${teamId}'/>";
    var ownerId = "<c:out value='${ownerId}'/>";
    var nodeId = "<c:out value='${nodeId}'/>";
    var opts_viewGrid = null;
    var opts_viewGrid_noAuth = null;
    var opts_page = null;
    var listMode = "granted";
    var myKeyWord = "";
    var accountRoleData = null;

    var headData = {
        "username": {"width": "30%"},
        "desc": {"width": "32%"},
        "role": {"width": "32%"},
        "handler": {"width": ""}
    };
    var headData_noAuth = {
        "username": {"title": '', "width": "35%"},
        "userDesc": {"title": '', "width": ""}
    };

    $(function () {

        enCharSearch();
        init();

        switchViewTo('granted');
        initAccountRoleList();

        initIsshow();
    });
    function enCharSearch(keyWord) {
        $("#enCharSearch").html("");
        if (keyWord == "undefined" || keyWord == null) myKeyWord = "";
        if (keyWord == "") {
            $("#enCharSearch").append('<a class="active" href="javascript:void(0)" onclick="searchBy(this);"><spring:message code="teamSpace.label.showAll"/></a>');
        } else {
            $("#enCharSearch").append('<a href="javascript:void(0)" onclick="searchBy(this);"><spring:message code="teamSpace.label.showAll"/></a>');
        }

        for (var i = 0; i < 26; i++) {
            var s = String.fromCharCode(65 + i);
            if (s == keyWord) {
                $("#enCharSearch").append('<a class="active" href="javascript:void(0)" onclick="searchBy(this,\'' + s + '\');">' + s + '</a>');
            } else {
                $("#enCharSearch").append('<a href="javascript:void(0)" onclick="searchBy(this,\'' + s + '\');">' + s + '</a>');
            }
        }
    }

    function init() {
        $("#memberTypeCon > a, #memberTypeCon ul li > a").tooltip({
            container: "body",
            placement: "top",
            delay: {show: 100, hide: 0},
            animation: false
        });

        var operTeamRole = "<c:out value='${memberInfo.teamRole}'/>";

        opts_viewGrid = $("#memberList").comboTableGrid({
            headData: headData,
            border: false,
            hideHeader: true,
            splitRow: false,
            miniPadding: true,
            stripe: true,
            dataId: "id",
            ItemOp: "user-defined",
            height: 275
        });
        opts_viewGrid_noAuth = $("#memberListNoAuth").comboTableGrid({
            headData: headData_noAuth,
            border: false,
            hideHeader: true,
            splitRow: false,
            miniPadding: true,
            stripe: true,
            checkBox: true,
            dataId: "id",
            ItemOp: "user-defined",
            height: 250
        });

        $.fn.comboTableGrid.setItemOp = function (tableData, rowData, tdItem, colIndex) {
            switch (colIndex) {
                case "username":
                    try {
                        var alink, userType = rowData.userType;
                        if (userType == "group") {
                            alink = "<i class='icon-users icon-orange'></i>";
                        } else {
                            alink = "<i class='icon-user'></i> ";
                        }
                        if (userType == "system") {
                            tdItem.find("p").text('<spring:message code="teamspace.user.system"/>');
                        }
                        tdItem.find("p").prepend(alink);
                    } catch (e) {
                    }
                    break;
                case "role":
                    try {
                        var alink, userRole = rowData.role, roleText, id = rowData.id, userId = rowData.userId, trId,
                            dropClass;

                        roleText = roleMsgs[userRole];
                        trId = tdItem.parent().attr("id").substring(20);
                        dropClass = "";
                        if (trId > 4) {
                            dropClass = "dropup";
                        }
                        alink = "<span class=\"dropdown " + dropClass + "\">" +
                            "<a href=\"javascript:void()\" onclick=\"dropAuthType(this, " + id + ", " + userId + ")\" class=\"dropdown-toggle\" data-toggle=\"dropdown\">" + roleText + "<i class=\"icon-caret-down icon-gray\"></i></a>" +
                            "<ul class=\"dropdown-menu pull-right\"></ul>" +
                            "</span>";

                        tdItem.removeAttr("title").find("p").html('').css("overflow", "visible").append(alink);
                    } catch (e) {
                    }
                    break;
                case "handler":
                    try {
                        var alink, id = rowData.id;

                        alink = "<a onclick=\"deleteMember(" + id + ")\"><i class=\"icon-delete-alt icon-gray\"></i></a>";
                        tdItem.find("p").append(alink);
                    } catch (e) {
                    }
                    break;
                default :
                    break;
            }
        };

        opts_page = $("#memberListPageBox").comboPage({
            style: "page table-page",
            pageSkip: false,
            lang: '<spring:message code="common.language1"/>'
        });
        $.fn.comboPage.pageSkip = function (opts, _idMap, curPage) {
            initDataList(curPage);
        };

    }

    function initDataList(curPage) {
        if (listMode == "granted") {
            var url = "${ctx}/teamspace/file/grantAuthority/<c:out value='${ownerId}'/>/<c:out value='${nodeId}'/>";
            var params = {
                "page": curPage,
                "pageSize": 10,
                "keyword": myKeyWord,
                "token": "<c:out value='${token}'/>"
            };
            $.ajax({
                type: "POST",
                url: url,
                data: params,
                error: function (request) {
                    handlePrompt("error", '<spring:message code="inviteShare.listUserFail"/>', '', '5');
                },
                success: function (data) {
                    currentPage = data.number;
                    $("#memberList").setTableGridData(data.content, opts_viewGrid);
                    $("#memberListPageBox").setPageData(opts_page, data.number, data.size, data.totalElements);
                }
            });
        } else {
            var url = "${ctx}/teamspace/file/unGrantAuthority/<c:out value='${ownerId}'/>/<c:out value='${nodeId}'/>";
            var params = {
                "page": curPage,
                "pageSize": 10,
                "keyword": myKeyWord,
                "token": "<c:out value='${token}'/>"
            };
            $.ajax({
                type: "POST",
                url: url,
                data: params,
                error: function (request) {
                    handlePrompt("error", '<spring:message code="inviteShare.listUserFail"/>', '', '5');
                },
                success: function (data) {
                    currentPage = data.number;
                    $("#memberListNoAuth").setTableGridData(data.content, opts_viewGrid_noAuth);
                    $("#memberListPageBox").setPageData(opts_page, data.number, data.size, data.totalElements);
                }
            });
        }
    }


    function initIsshow() {

        var url = "${ctx}/teamspace/file/getIsVisibleNodeACL/<c:out value='${ownerId}'/>/<c:out value='${nodeId}'/>";
        var data = {
            ownerId: ownerId,
            nodeId: nodeId,
            authType: $("#txtSlctAuthType").val(),
            token: "<c:out value='${token}'/>"
        }

        $.ajax({
            type: "GET",
            url: url,
            data: data,
            error: function (request) {
                if (request.status == 403) {
                    handlePrompt("error", "<spring:message code='error.forbid'/>");
                    return;
                }
                handlePrompt("error", "<spring:message code='teamSpace.error.authority'/>");
            },
            success: function (data) {
                if (data == 'true') {
                    $("#isshow").attr("checked", "checked");
                } else if (data == false) {
                }
            }
        });
    }

    function searchBy(that, keyWord) {
        if ($(that).hasClass("active")) {
            return;
        }
        myKeyWord = keyWord;
        if (keyWord == "undefined" || keyWord == null) myKeyWord = "";
        $(that).parent().find(".active").removeClass("active").end().end().addClass("active");
        initDataList(1);

    }

    function switchViewTo(vMode) {
        listMode = vMode;
        $("#enCharSearch").html("");
        myKeyWord = "";
        initDataList(1);
        if (vMode == "notGranted") {
            $("#memberListTitle").html("<spring:message code='teamSpace.label.notGranted' />");
            $("#memberListNoAuth").showTableGridLoading();
            $("#isavileble").hide();
            $("#memberListNoAuth, #memberTypeCon, #submitBtnCon, #enCharSearch").show();
            $("#memberList, #goGrantCon, #manageBtnCon").hide();
            enCharSearch('');
        } else {
            $("#memberListTitle").html("<spring:message code='teamSpace.label.hasGranted' />");
            $("#memberList").showTableGridLoading();
            $("#isavileble").show();
            $("#memberListNoAuth, #memberTypeCon, #submitBtnCon, #enCharSearch").hide();
            $("#memberList, #goGrantCon, #manageBtnCon").show();
            enCharSearch('');
        }
    }


    function submitMember() {
        if (members == "") {
            return false;
        }
        var members = $("#memberListNoAuth").getTableGridSelected().join(";");

        var url = "${ctx}/teamspace/file/grantAuthToFolder";
        var data = {
            users: members,
            ownerId: ownerId,
            nodeId: nodeId,
            authType: $("#txtSlctAuthType").val(),
            token: "<c:out value='${token}'/>"
        }

        $.ajax({
            type: "POST",
            url: url,
            data: data,
            error: function (request) {
                if (request.status == 403) {
                    handlePrompt("error", "<spring:message code='error.forbid'/>");
                    return;
                }
                handlePrompt("error", "<spring:message code='teamSpace.error.authority'/>");
            },
            success: function (data) {
                if (typeof(data) == 'string' && data.indexOf('<html>') != -1) {
                    window.location.href = "${ctx}/logout";
                    return;
                }
                if (data == "OK") {
                    handlePrompt("success", '<spring:message code="teamSpace.info.successfullyAuthority"/>');
                } else if (data == "P_OK") {
                    handlePrompt("error", "<spring:message code='teamSpace.error.partlyAuthority'/>");
                }
                switchViewTo("granted");
            }
        });
    }

    function selectSomeOne(that, val) {
        $("#selectedAuth").html($(that).html());
        $("#txtSlctAuthType").val(val);
    }

    function dropAuthType(that, id, teamMemberId) {
        var dropSlct, popDiv = "";
        dropSlct = [{value: "editor", name: "<spring:message code='teamSpace.option.editor'/>"},
            {value: "viewer", name: "<spring:message code='teamSpace.option.viewer'/>"}];

        for (var i = 0; i < accountRoleData.length; i++) {
            popDiv += "<li><a href='javascript:void(0)' onclick='updateAuthType(" + id + ", " + teamMemberId + ",\"" + accountRoleData[i].name + "\",this)'>" + roleMsgs[accountRoleData[i].name] + "</a></li>";
        }
        $(that).next().html("").append($(popDiv));
    }

    function updateAuthType(aclId, teamMemberId, authType, that) {
        var url = "${ctx}/teamspace/file/changeFolderAuth";
        var data = {
            ownerId: ownerId,
            nodeId: nodeId,
            aclId: aclId,
            userId: teamMemberId,
            authType: authType,
            token: "<c:out value='${token}'/>"
        };

        $.ajax({
            type: "POST",
            url: url,
            data: data,
            error: function (request) {
                handlePrompt("error",
                    "<spring:message code='operation.failed'/>");
            },
            success: function (data) {
                if (typeof(data) == 'string' && data.indexOf('<html>') != -1) {
                    window.location.href = "${ctx}/logout";
                    return;
                }
                $(that).parent().parent().prev().html($(that).html());
                handlePrompt("success", "<spring:message code='operation.success'/>");
                initDataList(currentPage);
            }
        });
    }

    function modifyNodeIsVisible() {
        var url = "${ctx}/teamspace/file/modifyNodeIsVisible/" + ownerId + "/" + nodeId;
        var isshow;
        if ($("#isshow").is(':checked')) {
            isshow = 1;
        } else {
            isshow = 0;
        }

        var data = {
            isavalible: isshow,
            token: "<c:out value='${token}'/>"
        };

        $.ajax({
            type: "POST",
            url: url,
            data: data,
            error: function (request) {
                handlePrompt("error",
                    "<spring:message code='operation.failed'/>");
            },
            success: function (data) {
            }
        });
    }

    function deleteMember(aclId) {
        var url = "${ctx}/teamspace/file/deleteFolderAuth";
        var data = {
            ownerId: ownerId,
            aclId: aclId,
            token: "<c:out value='${token}'/>"
        };
        $.ajax({
            type: "POST",
            url: url,
            data: data,
            async: false,
            error: function (request) {
                handlePrompt("error", "<spring:message code='operation.failed'/>");
            },
            success: function (data) {
                if (typeof(data) == 'string' && data.indexOf('<html>') != -1) {
                    window.location.href = "${ctx}/logout";
                    return;
                }
                if (data == "OK") {
                    initDataList(1);
                    handlePrompt("success", "<spring:message code='operation.success'/>");
                } else if (data == "Forbidden") {
                    handlePrompt("error", "<spring:message code='teamSpace.error.Forbidden'/>");
                } else if (data == "NoFound") {
                    handlePrompt("error", "<spring:message code='teamMemberships.error.NoFound'/>");
                } else {
                    handlePrompt("error", "<spring:message code='operation.failed'/>");
                }
            }
        });
    }
    function initAccountRoleList() {
        var url = "${ctx}/accountrole/list";
        var roles;
        $.ajax({
            type: "GET",
            url: url,
            error: function (request) {
                roles = null;
            },
            success: function (data) {
                accountRoleData = data;
                if (data.length > 0) {
                    for (var i = 0; i < data.length; i++) {
                        $("#memberType_" + data[i].name).html(roleMsgs[data[i].name]);
                        $("#memberType_" + data[i].name).attr("title", setAuthorityHint(accountRoleData[i]));
                    }
                    $("#selectedAuth").html(roleMsgs[data[0].name])
                    $("#txtSlctAuthType").val(data[0].name);
                }
            }
        });
        return roles;
    }
</script>
</body>
</html>
