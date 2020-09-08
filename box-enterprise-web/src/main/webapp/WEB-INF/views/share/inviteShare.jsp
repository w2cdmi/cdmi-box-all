<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="../common/common.jsp" %>
    <%@ include file="../common/commonForRole.jsp" %>
    <link href="${ctx}/static/autocomplete/themes/base/jquery.ui.all.css" rel="stylesheet" type="text/css">
    <script src="${ctx}/static/autocomplete/ui/jquery.ui.core.js" type="text/javascript"></script>
    <script src="${ctx}/static/autocomplete/ui/jquery.ui.widget.js" type="text/javascript"></script>
    <script src="${ctx}/static/autocomplete/ui/jquery.ui.position.js" type="text/javascript"></script>
    <script src="${ctx}/static/autocomplete/ui/jquery.ui.menu.js" type="text/javascript"></script>
    <script src="${ctx}/static/autocomplete/ui/jquery.ui.autocomplete.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/JQbox-hw-grid.js" type="text/javascript"></script>
    <script src="${ctx}/static/clipboard/ZeroClipboard.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/organization.js" type="text/javascript"></script>

</head>
<body>
<input type="hidden" value="<c:out value='${mailmsg}'/>" id="mailMsg"/>
<div class="pop-content">
    <span id="enterTempData" style="display:none; height:30px;"></span>
    <div class="pop-share-invite">
        <div class="file-name clearfix" id="linkFileName">
            <span class="file-icon" id="fileOrFolderType"></span> <span class="name-txt"
                                                                        title="<c:out value='${name}'/>"><c:out
                value='${name}'/></span>
            <span id="collaboratorCount" class="collaborator-count"><span
                    id="shareUserCount">&nbsp;</span>&nbsp;<spring:message code="inviteShare.count"/> - <a
                    href="javascript:void(0)" id="copyButton"><spring:message code='link.set.copyUrl'/></a> </span>
            <span id="shareUserString" style="display:none;"></span>
        </div>
        <div id="manageCollaborator" class="collaborator-list">
            <div id="sharedUserList"></div>
        </div>
        <div id="inviteCollaborator" class="user-form">
            <textarea maxlength=2000 id="messageAddr" onpaste="userOnPaste()" onkeyup='doMaxLength(this)'></textarea>
            <c:if test="${shareStatus == 0}">
                <div class="prompt"><spring:message code="inviteShare.invite.first"/></div>
            </c:if>
            <c:if test="${shareStatus == 1}">
                <div class="prompt"><spring:message code="inviteShare.invite.more"/></div>
            </c:if>
            <div class="enterPrompt"><spring:message code="inviteShare.searchUserInfo"/></div>
            <c:if test="${isDepartment == true}">
                <button class="btn btn-mini btn-link" style="float:right" type="button" onclick="addOrganization()">
                    <spring:message code='organization.user.add'/></button>
            </c:if>
        </div>
        <div class="search-loading">
            <div id="loadingDiv" class="loading-div"></div>
        </div>
        <div id="memberTypeCon" class="member-type dropdown row-fluid">
            <a class="btn dropdown-toggle span12" data-toggle="dropdown"><span class="caret"></span><strong
                    id="selectedAuth"></strong></a>
            <ul class="dropdown-menu">
                <c:forEach items="${systemRoles}" var="systemRole">
                    <c:if test="${type == 0}">
                        <li><a id="memberType_<c:out value='${systemRole.name}'/>"
                               onclick="selectSomeOne(this,'<c:out value="${systemRole.name}"/>');"/></a></li>
                    </c:if>
                    <c:if test="${type == 1}">
                        <c:if test="${systemRole.name ne 'uploader' and systemRole.name ne 'uploadAndView' and systemRole.name ne 'editor'}">
                            <li><a id="memberType_<c:out value='${systemRole.name}'/>"
                                   onclick="selectSomeOne(this,'<c:out value="${systemRole.name}"/>');"/></a></li>
                        </c:if>
                    </c:if>
                </c:forEach>
            </ul>
            <input type="hidden" id="txtSlctAuthType" value="viewer"/>
        </div>
        <div id="inviteMessage" class="share-message"><textarea maxlength=2000 id="messageText"
                                                                placeholder='<spring:message code="inviteShare.addMessage"/>'
                                                                onkeyup='doMaxLength(this)'></textarea></div>
        <div id="inviteBtnCon" class="btn-con">
            <button id="submit_btn" type="button" class="btn btn-primary" onclick="shareToOthers()"><spring:message
                    code="button.invite"/></button>
            <button id="" type="button" class="btn" onclick="cancelInvite()"><spring:message
                    code="button.cancel"/></button>
        </div>
        <div id="manageBtnCon" class="btn-con">
            <c:if test="${type == 0}">
                <button id="btnCancelAll" type="button" onclick="cancelShare()" class="btn"><spring:message
                        code="inviteShare.button.cancel.folder"/></button>
            </c:if>
            <c:if test="${type == 1}">
                <button id="btnCancelAll" type="button" onclick="cancelShare()" class="btn"><spring:message
                        code="inviteShare.button.cancel.file"/></button>
            </c:if>
            <button id="" type="button" class="btn btn-primary" onclick="top.ymPrompt.close()"><spring:message
                    code="button.close"/></button>
        </div>
    </div>
    <div class="pop">
        <ul id="menuTree" class="ztree" style="margin-top:0;height: 80%;overflow:auto;">
        </ul>
        <input type="button" style="margin-left: 150px;" value="<spring:message code="button.ok"/>"
               class="btn btn-primary" onclick="addDepartmentToUrl();"/>
        <input type="button" style="margin-left: 80px;" value="<spring:message code="button.cancel"/>" class="btn"
               onclick="closeDiv();"/>
    </div>
</div>
<script type="text/javascript">

    function showNodeTree() {
        var params = {
            "token": token
        };
        $.ajax({
            data: params,
            url: ctx + '/teamspace/member/showDept',
            type: 'GET',
            async: false,
            success: function (msg) {
                var obj = eval("(" + msg + ")");
                $.fn.zTree.init($("#menuTree"), deviceTypeSetting, obj);
            },
            error: function () {
                handlePrompt("error", voptFailed);
            }
        });
    }

    function addDepartmentToUrl() {
        deleteDepartmentUrl();
        if (!allDepartmentToMap.isEmpty()) {
            allDepartmentToMap.each(function (key, value, index) {
                if (value[4] == "department") {
                    addMessageTo(value[1], value[2], 2, value[0], value[3]);
                } else {
                    addMessageTo(value[1], value[2], 0, value[0], value[3]);
                }
            });
        }
        oldMessage = [];
        $(".enterPrompt").hide();
        $('.bgPop,.pop').hide();
    }

    var allDepartmentToMap = new Map();
    var oldMessage = new Array();
    var submitUsername = null;
    var tempUsername = null;
    var allMessageTo = new Array();
    var opts_viewGrid = null;
    var ownerId = "<c:out value='${ownerId}'/>";
    var listViewType = "list";
    var iNodeId = "<c:out value='${folderId}'/>";
    var isShare = "<c:out value='${shareStatus}'/>";
    var objType = '<c:out value="${type}"/>';
    token = "<c:out value='${token}'/>";
    var accountRoleData = null;

    var headData = {
        "sharedUserName": {"title": '<spring:message code="inviteShare.sharedUserName"/>', "width": "30%"},
        "sharedDepartment": {"title": '<spring:message code="common.field.department"/>', "width": "32%"},
        "roleName": {"width": "28%"},
        "removeTag": {"title": '<spring:message code="common.field.operation"/>', "width": "", "cls": "ar"}
    };
    var curPage = 0;
    var mailMsg = "";

    $(document).ready(function () {

        mailMsg = $("#mailMsg").val();
        $("#linkFileName span").tooltip({
            container: "body",
            placement: "bottom",
            delay: {show: 100, hide: 0},
            animation: false
        });
        $("#messageAddr").keydown(function (event) {
            if (event.keyCode == 13) {
                if ($(".ui-autocomplete").get(0) && $(".ui-autocomplete").find(".ui-state-focus").get(0)) {
                    return;
                }
                searchMessageTo();
                if (window.event) {
                    window.event.cancelBubble = true;
                    window.event.returnValue = false;
                } else {
                    event.stopPropagation();
                    event.preventDefault();
                }
            } else if (event.keyCode == 8) {
                if ($(this).val() == "") {
                    var nodeId = $(this).parent().find("div.invite-member:last > a.close").attr("id")
                    $(this).parent().find("div.invite-member:last").remove();
                    var size = allDepartmentToMap.size();
                    allDepartmentToMap.remove(nodeId);
                    var size2 = allDepartmentToMap.size();
                    if (size == size2) {
                        allDepartmentToMap.remove("department" + nodeId);
                    }
                    allMessageTo.pop();
                    var conH = parseInt($(".pop-content").outerHeight() + 90);
                    top.ymPrompt.resizeWin(650, conH);
                }
            }
            else if (event.keyCode != 38 && event.keyCode != 40) {
                availableTags = [];
            }
            $(".enterPrompt").hide();
        });
        $("#messageAddr").keyup(function (event) {
            submitUsername = $("#messageAddr").val();
            if (submitUsername != tempUsername) {
                try {
                    $("#messageAddr").autocomplete("close");
                } catch (e) {
                }
            }
            userInputAutoSize(this);
        })
        init();
        initDataList(1);
        initAccountRoleList();
    });

    function userOnPaste() {
        $(".enterPrompt").hide();
        setTimeout(function () {
            submitUsername = $("#messageAddr").val();
            userInputAutoSize("#messageAddr");
            searchMessageTo();
        }, 0);
    }

    function userInputAutoSize(that) {
        var tempObj = $("#enterTempData"),
            _obj = $(that).parent().find("div.invite-member:last"),
            posCon = $("#inviteCollaborator").offset().left + 5,
            posInput = _obj.get(0) ? (_obj.offset().left + _obj.outerWidth() + 5) : posCon,
            userConW = 525,
            tempW = 0,
            space = userConW - parseInt(posInput - posCon),
            thatParent = $(that).parent().get(0);
        var tempValue = $(that).val().replace(new RegExp(" ", "g"), "&nbsp;");
        tempValue = tempValue.replace(new RegExp("<", "g"), "&lt;");
        tempObj.html(tempValue);
        tempW = tempObj.width();
        if ((tempW + 5) > space || $(that).get(0).scrollHeight > 20) {
            $(that).css({"width": userConW});
            $(that).height(0);
            $(that).css({"height": $(that).get(0).scrollHeight});
            thatParent.scrollTop = thatParent.scrollHeight;
        } else {
            $(that).css({"width": space, "height": 20});
        }

        var conH = parseInt($(".pop-content").outerHeight() + 90);
        top.ymPrompt.resizeWin(650, conH);
    }

    function deleteSharedUser(sharedUserId, sharedUserType) {
        var params = {
            "iNodeId": iNodeId,
            "sharedUserId": sharedUserId,
            "sharedUserType": sharedUserType,
            "token": "<c:out value='${token}'/>"
        };
        $.ajax({
            type: "POST",
            url: "${ctx}/share/deleteSharedUser",
            data: params,
            error: function (request) {
                var responseObj = $.parseJSON(request.responseText);
                switch (responseObj.code) {
                    case "BadRequest":
                        handlePrompt("error", '<spring:message code="share.error.BadRquest"/>');
                        break;
                    case "NoSuchItem":
                        handlePrompt("error", '<spring:message code="share.error.NoSuchItems"/>');
                        break;
                    case "Forbidden":
                    case "SecurityMatrixForbidden":
                        handlePrompt("error", '<spring:message code="share.error.Forbidden"/>');
                        break;
                    default:
                        handlePrompt("error", '<spring:message code="operation.failed"/>');
                        break;
                }
            },
            success: function (data) {
                handlePrompt("success", '<spring:message code="inviteShare.delUserSuccess"/>', '', '5');
                initDataList(1);
                top.shareHandle(true);
            }
        });
    }


    function init() {
        if (isShare == 1) {
            $("#manageCollaborator,#manageBtnCon,#collaboratorCount").show();
            $("#memberTypeCon,#inviteMessage,#inviteBtnCon").hide();
            top.ymPrompt.resizeWin(650, 430);
        } else {
            $("#manageCollaborator,#manageBtnCon,#collaboratorCount").hide();
            $("#memberTypeCon,#inviteMessage,#inviteBtnCon").show();
            top.ymPrompt.resizeWin(650, 375);
        }
        if (objType == '0') {
            $("#fileOrFolderType").addClass("fileImg-folder");
        } else if (objType == '-3') {
            $("#fileOrFolderType").addClass("fileImg-folder-computer");
        } else if (objType == '-2') {
            $("#fileOrFolderType").addClass("fileImg-folder-disk");
        } else {
            var name = '<c:out value="${name}"/>';
            var type = _getStandardType(name);
            if (type == "img") {
                var thumUrl = "url('${thumbnailUrl}') no-repeat center center";
                $("#fileOrFolderType").css("background", thumUrl);
            } else {
                $("#fileOrFolderType").addClass("fileImg-" + type);
            }
        }

        opts_viewGrid = $("#sharedUserList").comboTableGrid({
            headData: headData,
            border: false,
            hideHeader: true,
            miniPadding: true,
            splitRow: false,
            stripe: true,
            dataId: "id",
            ItemOp: "user-defined",
            height: 175
        });
        $.fn.comboTableGrid.setItemOp = function (tableData, rowData, tdItem, colIndex) {
            switch (colIndex) {
                case "sharedUserName":
                    try {
                        if (rowData.sharedUserType == 1) {
                            tdItem.find("p").prepend('<i class="icon-users icon-orange"></i> ');
                        }
                        else {
                            tdItem.find("p").prepend('<i class="icon-user"></i> ');
                        }
                    } catch (e) {
                    }
                    break;
                case "roleName":
                    try {
                        var alink, userRole = rowData.roleName, roleText, userType = rowData.sharedUserType,
                            sharedId = rowData.sharedUserId, trId, dropClass;

                        roleText = roleMsgs[userRole];
                        trId = tdItem.parent().attr("id").substring(20);
                        dropClass = "";
                        if (trId > 4) {
                            dropClass = "dropup";
                        }

                        alink = "<span class=\"dropdown " + dropClass + "\">" +
                            "<a href=\"javascript:void(0)\" onclick=\"dropAuthType(this," + sharedId + "," + userType + ")\" class=\"dropdown-toggle\" data-toggle=\"dropdown\">" + roleText + "<i class=\"icon-caret-down icon-gray\"></i></a>" +
                            "<ul class=\"dropdown-menu pull-right\"></ul>" +
                            "</span>";

                        tdItem.find("p").html('').css("overflow", "visible").append(alink);
                        tdItem.attr("title", "");

                    } catch (e) {
                    }
                    break;
                case "removeTag":
                    try {
                        var alink, userType = rowData.sharedUserType, sharedId = rowData.sharedUserId;
                        alink = "<button class=\"btn btn-mini btn-link\" type=\"button\" onclick=\"deleteSharedUser(" + sharedId + "," + userType + ")\" title=\"<spring:message code='button.remove'/>\"><i class=\"icon-delete-alt icon-gray\"></i></button>";
                        tdItem.find("p").append(alink);
                    } catch (e) {
                    }
                    break;
                default :
                    break;
            }
        };

        $("#messageText").focus(function () {
            if (mailMsg != null && mailMsg != "" && $("#messageText").val() == "") {
                $("#messageText").val(mailMsg).select();
            }
        });

        $("#inviteCollaborator").click(function () {
            $("#messageAddr").focus();
        });
        $("#messageAddr").focus(function () {
            $(".prompt").hide();
            $("#manageBtnCon").hide();
            $("#memberTypeCon,#inviteMessage,#inviteBtnCon").show();
            ymPrompt_enableModalbtn("#submit_btn");

            if ($(this).val() == '' && allMessageTo.length < 1) {
                $(".enterPrompt").show();
            }
            if (isShare == 1) {
                var conH = parseInt($(".pop-content").outerHeight() + 90);
                top.ymPrompt.resizeWin(650, conH);
            }
        }).blur(function () {
            if ($(this).val() == '' && allMessageTo.length < 1) {
                $(".prompt").show();
                $(".enterPrompt").hide();
            }
        })
    }

    function initDataList(curPage) {
        var url = "${ctx}/share/listSharedUser";
        var params = {
            "ownerId": ownerId,
            "iNodeId": iNodeId,
            "pageNumber": curPage,
            "token": "<c:out value='${token}'/>"
        };
        $.ajax({
            type: "POST",
            url: url,
            data: params,
            error: function (request) {
                handlePrompt("error", '<spring:message code="inviteShare.listSharedUserFail"/>', '', '5');
            },
            success: function (data) {
                catalogData = data.content;
                $("#shareUserCount").text(data.content.length);
                if (data.content.length == 0) {
                    $("#btnCancelAll").hide();
                } else {
                    $("#btnCancelAll").show();
                }

                $("#sharedUserList").setTableGridData(catalogData, opts_viewGrid);

                var userListStr = "";
                for (var i = 0; i < data.content.length; i++) {
                    if (i == (data.content.length - 1)) {
                        userListStr += data.content[i].sharedUserLoginName;
                    } else {
                        userListStr += data.content[i].sharedUserLoginName + ";";
                    }
                }
                $("#shareUserString").html(userListStr);

                ZeroClipboard.config({moviePath: '${ctx}/static/clipboard/ZeroClipboard.swf'});
                var client = new ZeroClipboard(document.getElementById("copyButton"));
                client.on("mouseOver", function (client) {
                    client.setText($("#shareUserString").text());
                });
                client.on("complete", function () {
                    handlePrompt("success", '<spring:message code="link.set.copySuccess" />', '', '5');
                });
            }
        });
    }

    function cancelShare() {
        ymPrompt.confirmInfo({
            title: "<spring:message code='inviteShare.button.removeAll'/>",
            message: '<spring:message code="inviteShare.removeAllConfirm"/>',
            maskAlphaColor: "gray",
            handler: function (tp) {
                if (tp == 'ok') {
                    $.ajax({
                        type: "POST",
                        data: {
                            "iNodeId": "<c:out value='${folderId}'/>",
                            "token": "<c:out value='${token}'/>"
                        },
                        url: "${ctx}/share/cancelShare",
                        error: function (request) {
                            var responseObj = $.parseJSON(request.responseText);
                            switch (responseObj.code) {
                                case "BadRequest":
                                    handlePrompt("error", '<spring:message code="share.error.BadRquest"/>');
                                    break;
                                case "NoSuchItem":
                                    handlePrompt("error", '<spring:message code="share.error.NoSuchItems"/>');
                                    break;
                                case "Forbidden":
                                case "SecurityMatrixForbidden":
                                    handlePrompt("error", '<spring:message code="share.error.Forbidden"/>');
                                    break;
                                default:
                                    handlePrompt("error", '<spring:message code="operation.failed"/>');
                                    break;
                            }
                        },
                        success: function () {
                            top.ymPrompt.close();
                            top.handlePrompt("success", '<spring:message code="inviteShare.removeAllSuccess"/>');
                            top.shareHandle(true);
                        }
                    });
                }
            }
        });
    }


    function getFormatDate(date) {
        var tempDate = "";
        if (date == undefined) {
            tempDate = "";
        } else {
            tempDate = new Date(date);
        }
        var pattern = "yyyy-MM-dd hh:mm:ss";
        if (tempDate == "") {
            return "";
        } else {
            return tempDate.format(pattern)
        }
    }

    function selectSomeOne(that, val) {
        $("#selectedAuth").html($(that).html());
        $("#txtSlctAuthType").val(val);
    }


    function dropAuthType(that, shareUserId, userType) {
        var dropSlct, popDiv = "";

        for (var i = 0; i < accountRoleData.length; i++) {
            if (objType == 0) {
                popDiv += "<li><a href='javascript:void(0)' onclick='updateAuthType(" + shareUserId + ", " + userType + ",\"" + accountRoleData[i].name + "\",this)'>" + roleMsgs[accountRoleData[i].name] + "</a></li>";

            } else {
                if (accountRoleData[i].name != 'uploader' && accountRoleData[i].name != 'uploadAndView' && accountRoleData[i].name != 'editor') {
                    popDiv += "<li><a href='javascript:void(0)' onclick='updateAuthType(" + shareUserId + ", " + userType + ",\"" + accountRoleData[i].name + "\",this)'>" + roleMsgs[accountRoleData[i].name] + "</a></li>";
                }
            }
        }
        $(that).next().html("").append($(popDiv));
    }

    function updateAuthType(shareUserId, userType, authType, that) {
        var params = {
            "iNodeId": "<c:out value='${folderId}'/>",
            "userId": shareUserId,
            "userType": userType,
            "authType": authType,
            "token": "<c:out value='${token}'/>"
        };
        isAddSharing = true;
        $.ajax({
            type: "POST",
            data: params,
            url: "${ctx}/share/updateShare",
            error: function (request) {
                var responseObj = $.parseJSON(request.responseText);
                switch (responseObj.code) {
                    case "BadRequest":
                        handlePrompt("error", '<spring:message code="share.error.BadRquest"/>');
                        break;
                    case "NoSuchItem":
                        handlePrompt("error", '<spring:message code="share.error.NoSuchItems"/>');
                        break;
                    case "Forbidden":
                    case "SecurityMatrixForbidden":
                        handlePrompt("error", '<spring:message code="share.error.Forbidden"/>');
                        break;
                    default:
                        handlePrompt("error", '<spring:message code="operation.failed"/>');
                        break;
                }
            },
            success: function () {
                isAddSharing = false;
                $(that).parent().parent().prev().html($(that).html() + "<i class=\"icon-caret-down icon-gray\"></i>");
                handlePrompt("success", "<spring:message code='operation.success'/>");
                initDataList(currentPage);
            }
        });

    }

    var availableTags = [];
    var unAvailableTags = [];
    function searchMessageTo() {
        if ($("#messageAddr").val().length <= 1) {
            return;
        }
        <%-- loading --%>
        var searchSpiner = new Spinner(optsSmallSpinner).spin($("#loadingDiv").get(0));

        availableTags = "";
        var params = {
            "ownerId": "<c:out value='${ownerId}'/>",
            "folderId": "<c:out value='${folderId}'/>",
            "userNames": $("#messageAddr").val(),
            "token": "<c:out value='${token}'/>"
        };

        tempUsername = params.userNames;
        var list;
        $.ajax({
            type: "POST",
            data: params,
            url: "${ctx}/share/listMultiUser",
            error: function (request) {
                searchSpiner.stop();
                handlePrompt("error", '<spring:message code="inviteShare.listUserFail"/>', '', '5');
                $("#messageAddr").focus();
            },
            success: function (data) {
                searchSpiner.stop();
                if (typeof(data) == 'string' && data.indexOf('<html>') != -1) {
                    window.location.href = "${ctx}/logout";
                    return;
                }
                if (tempUsername != submitUsername) {
                    return;
                }
                availableTags = data.successList;
                unAvailableTags = data.failList;
                if (availableTags.length == 0 && unAvailableTags.length == 0) {
                    handlePrompt("error", '<spring:message code="inviteShare.error.empty"/>', '', '5');
                    return;
                }
                if (availableTags.length == 0 && unAvailableTags.length > 0) {
                    handlePrompt("error", '<spring:message code="inviteShare.error.noresult"/>', '', '5');
                    return;
                }
                if (data.single && availableTags.length == 1) {
                    if (availableTags[0].userType == 1) {
                        addMessageTo(availableTags[0].cloudUserId, availableTags[0].loginName, availableTags[0].userType, availableTags[0].label, null);
                    }
                    else {
                        addMessageTo(availableTags[0].cloudUserId, availableTags[0].loginName, availableTags[0].userType, availableTags[0].label, availableTags[0].email);
                    }
                    $("#messageAddr").val("");
                    return;
                }
                if (!data.single && availableTags.length > 0) {
                    $(availableTags).each(function (n, item) {
                        if (item.userType == 1) {
                            addMessageTo(item.cloudUserId, item.loginName, item.userType, item.label, null);
                        }
                        else {
                            addMessageTo(item.cloudUserId, item.loginName, item.userType, item.label, item.email);
                        }

                    });
                    $("#messageAddr").val(unAvailableTags + "");
                    userInputAutoSize("#messageAddr");
                    if (unAvailableTags.length > 0) {
                        handlePrompt("error", '<spring:message code="inviteShare.error.partnoresult"/>', '', '5');
                    }
                    return;
                }
                if (data.single) {
                    $("#messageAddr").bind("keydown", function (event) {
                        if (event.keyCode === $.ui.keyCode.TAB &&
                            $(this).data("ui-autocomplete").menu.active) {
                            event.preventDefault();
                        }
                    }).autocomplete({
                        disabled: true,
                        position: {my: "left top", at: "left bottom", of: "#inviteCollaborator"},
                        minLength: 2,
                        cacheLength: 1,
                        source: function (request, response) {
                            response(availableTags);
                        },
                        focus: function () {
                            return false;
                        },
                        select: function (event, ui) {
                            $(this).val("");
                            if (ui.item.userType == 1) {
                                addMessageTo(ui.item.cloudUserId, ui.item.loginName, ui.item.userType, ui.item.label, null);
                            }
                            else {
                                addMessageTo(ui.item.cloudUserId, ui.item.loginName, ui.item.userType, ui.item.label, ui.item.email);
                            }

                            return false;
                        }
                    }).data("ui-autocomplete")._renderItem = function (ul, item) {
                        if (item.userType == 1) {
                            return $("<li>")
                                .append("<a><i class='icon-users icon-orange'></i><strong>" + item.label + "</strong> (" + item.loginName + ") " + "<br>" + item.department + "</a>")
                                .appendTo(ul);
                        } else {
                            return $("<li>")
                                .append("<a><i class='icon-user'></i><strong>" + item.label + "</strong> (" + item.loginName + ") " + "<br>" + item.department + "</a>")
                                .appendTo(ul);
                        }
                    };

                    $("#messageAddr").autocomplete("enable");
                    $("#messageAddr").autocomplete("search", $("#messageAddr").val());
                }
            }
        });
    }
    function split(val) {
        return val.split(/,\s*/);
    }
    function extractLast(term) {
        return split(term).pop();
    }
    var isAddSharing = false;
    var registerEventError = false;

    function shareToOthers() {
        ymPrompt_disableModalbtn("#submit_btn");
        if (isAddSharing) {
            return;
        }
        if (allMessageTo.length == 0) {
            handlePrompt("error", '<spring:message code="inviteShare.notSetShareUser"/>', '', '5');
            $("#messageAddr").focus();
            ymPrompt_enableModalbtn("#submit_btn");
            return;
        }
        $("#messageAddr").val("");
        var shareToStr = getTrunckData(allMessageTo);

        var msgText = $("#messageText").val();
        if (msgText == '<spring:message code="inviteShare.addMessage"/>') {
            msgText = "";
        }
        if (registerEventError) {
            if (msgText.length > 2000) {
                msgText = msgText.substring(0, 2000);
            }
        }
        var authType = $("#txtSlctAuthType").val();
        var params = {
            "ownerId": "<c:out value='${ownerId}'/>",
            "iNodeId": "<c:out value='${folderId}'/>",
            "shareToStr": "" + shareToStr,
            "message": msgText,
            "authType": authType,
            "token": "<c:out value='${token}'/>"
        };
        isAddSharing = true;
        $.ajax({
            type: "POST",
            data: params,
            url: "${ctx}/share/addShare",
            error: function (request) {
                isAddSharing = false;
                switch (request.responseText) {
                    case "BadRequest":
                        handlePrompt("error", '<spring:message code="share.error.BadRquest"/>');
                        break;
                    case "NoSuchItem":
                        handlePrompt("error", '<spring:message code="share.error.NoSuchItems"/>');
                        break;
                    case "Forbidden":
                    case "SecurityMatrixForbidden":
                        handlePrompt("error", '<spring:message code="share.error.Forbidden"/>');
                        break;
                    default:
                        handlePrompt("error", '<spring:message code="operation.failed"/>');
                        break;
                }
                $("#messageAddr").focus();
                ymPrompt_enableModalbtn("#submit_btn");
            },
            success: function (data) {
                isAddSharing = false;
                if (isShare == 1) {
                    top.ymPrompt.resizeWin(650, 430);
                    $("#memberTypeCon,#inviteMessage,#inviteBtnCon").hide();
                    $("#manageBtnCon").show();
                    $("#messageAddr, #messageText").val("").blur();
                    $("#inviteCollaborator").find(".invite-member").remove();
                    allMessageTo = [];
                    $(".prompt").show();
                    $(".enterPrompt").hide();
                    initDataList(1);
                    if (data == "P_OK") {
                        handlePrompt("success", '<spring:message code="teamSpace.error.addMemberpartly"/>', '', '5');
                    } else {
                        handlePrompt("success", '<spring:message code="operation.success"/>', '', '5');
                    }
                    mailMsg = msgText;
                    top.shareHandle();
                } else {
                    top.ymPrompt.close();
                    if (data == "P_OK") {
                        top.handlePrompt("success", '<spring:message code="teamSpace.error.addMemberpartly"/>');
                    } else {
                        top.handlePrompt("success", '<spring:message code="operation.success"/>');
                    }
                    top.shareHandle();
                }
            }
        });
        allDepartmentToMap = new Map();
    }

    function addMessageTo(userCloudId, userLoginName, userType, userName, userEmail) {
        var loginName = "<c:out value='${loginUserName}'/>";
        var subName = $("#messageAddr").val().trim();
        var itemValue = userType + "[" + userCloudId + "]" + userLoginName + "[" + userEmail + "]";
        var end = userLoginName.indexOf("(")
        var name = userLoginName.substring(0, end);
        if ((name == loginName || userLoginName == loginName) && userType == 0) {
            allDepartmentToMap.remove(userCloudId);
            handlePrompt("error", '<spring:message code="inviteShare.notShareToSelf"/>', '', '5');
            return;
        }

        if ($.inArray(itemValue, allMessageTo) != -1) {
            return;
        }
        var button = $("<a class='close' id='" + userCloudId + "' title=" + '<spring:message code="button.delete"/>' + ">&times;</a>");
        var text = $('<div title="' + userName + '">' + userName + '</div>');
        var dd = $('<div class="invite-member"></div>');
        button.click(function () {
            $(this).parent().remove();
            var nodeid = $(this)[0].id;
            allDepartmentToMap.remove(nodeid);
            userInputAutoSize("#messageAddr");
            var tempArray = new Array();
            var length = allMessageTo.length;
            for (var i = 0; i < length; i++) {
                var temp = allMessageTo.pop();
                if (temp != itemValue) {
                    tempArray.push(temp);
                } else {
                    break;
                }
            }
            allMessageTo = allMessageTo.concat(tempArray);
            var conH = parseInt($(".pop-content").outerHeight() + 90);
            top.ymPrompt.resizeWin(650, conH);

            if ($("#messageAddr").val() == '' && allMessageTo.length < 1) {
                $(".prompt").show();
            }
            window.event.cancelBubble = true;
            window.event.returnValue = false;
        });
        dd.append(text).append(button);
        $("#inviteCollaborator #messageAddr").before(dd);
        allMessageTo.push(itemValue);
        $("#messageAddr").focus();
        userInputAutoSize("#messageAddr");
        var conH = parseInt($(".pop-content").outerHeight() + 90);
        top.ymPrompt.resizeWin(650, conH);
    }

    function cancelInvite() {
        if (isShare == 1) {
            top.ymPrompt.resizeWin(650, 430);
            $("#memberTypeCon,#inviteMessage,#inviteBtnCon,.enterPrompt").hide();
            $("#manageBtnCon, .prompt").show();
            $("#messageAddr, #messageText").val("").blur();
            $("#messageAddr").removeAttr("style");
            $("#inviteCollaborator").find(".invite-member").remove();
            allMessageTo = [];
            allDepartmentToMap = new Map();
        } else {
            top.ymPrompt.close();
        }
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
                    if (objType == 0) {
                        var isSet = false;
                        for (var i = 0; i < data.length; i++) {
                            $("#memberType_" + data[i].name).html(roleMsgs[data[i].name]);
                            $("#memberType_" + data[i].name).attr("title", setAuthorityHint(accountRoleData[i]));
                            if (data[i].name == 'viewer') {
                                $("#selectedAuth").html(roleMsgs[data[i].name])
                                $("#txtSlctAuthType").val(data[i].name);
                                isSet = true;
                            }
                        }
                        if (!isSet) {
                            $("#selectedAuth").html(roleMsgs[data[0].name])
                            $("#txtSlctAuthType").val(data[0].name);
                        }
                    } else {
                        var isSet = false;
                        var seleteIndex = -1;
                        for (var i = 0; i < data.length; i++) {
                            if (data[i].name != 'uploader' && data[i].name != 'uploadAndView' && data[i].name != 'editor') {
                                $("#memberType_" + data[i].name).html(roleMsgs[data[i].name]);
                                $("#memberType_" + data[i].name).attr("title", setAuthorityHint(accountRoleData[i]));
                                if (data[i].name == 'viewer') {
                                    $("#selectedAuth").html(roleMsgs[data[i].name])
                                    $("#txtSlctAuthType").val(data[i].name);
                                    isSet = true;
                                } else if (seleteIndex == -1) {
                                    seleteIndex = i;
                                }
                            }
                        }
                        if (!isSet) {
                            $("#selectedAuth").html(roleMsgs[data[seleteIndex].name])
                            $("#txtSlctAuthType").val(data[seleteIndex].name);
                        }

                    }

                }
            }
        });
        return roles;
    }
</script>
</body>
</html>
