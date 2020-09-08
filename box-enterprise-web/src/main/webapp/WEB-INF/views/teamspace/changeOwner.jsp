<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="../common/common.jsp" %>

    <link href="${ctx}/static/autocomplete/themes/base/jquery.ui.all.css" rel="stylesheet" type="text/css">
    <script src="${ctx}/static/autocomplete/ui/jquery.ui.core.js" type="text/javascript"></script>
    <script src="${ctx}/static/autocomplete/ui/jquery.ui.widget.js" type="text/javascript"></script>
    <script src="${ctx}/static/autocomplete/ui/jquery.ui.position.js" type="text/javascript"></script>
    <script src="${ctx}/static/autocomplete/ui/jquery.ui.menu.js" type="text/javascript"></script>
    <script src="${ctx}/static/autocomplete/ui/jquery.ui.autocomplete.js" type="text/javascript"></script>
</head>
<body>
<div class="pop-content team-change-con">
    <input type="hidden" id="teamId" name="teamId" value="<c:out value='${teamId}'/>"/>
    <div id="loginName"><span class="uneditable-input"></span></div>
    <input type="hidden" id="teamMemberId" name="teamMemberId" value=""/>
    <input type="hidden" id="cloudUserId" name="cloudUserId" value="0"/>
    <spring:message code='teamSpace.changeOwer.content'/>
    <div id="inviteCollaborator" class="user-form">
        <input id="messageAddr" type="text" maxlength="100"/>
        <div class="prompt-con"><spring:message code='inviteShare.searchUser'/></div>
        <div id="showUser">
            <span class="uneditable-input"></span>
            <button onclick="deleteUser()" type="button" class="btn"
                    title="<spring:message code='teamSpace.label.delete' />"><i class="icon-cancel"></i></button>
        </div>
        <div id="loadingDiv" class="loading-div"></div>
    </div>
    <div class="alert alert-danger" id="warnMessage"><strong><spring:message code='common.tip'/></strong><spring:message
            code='teamSpace.changeOwer.errorPrompt'/></div>
</div>
<script type="text/javascript">
    var ownerId = "<c:out value='${ownerId}'/>";
    var iNodeId = "<c:out value='${folderId}'/>";

    $(document).ready(function () {
        /*tip*/
        $("#messageAddr").keydown(function (event) {
            if (event.keyCode == 13) {
                searchMessageTo();
            } else if (event.keyCode != 38 && event.keyCode != 40) {
                availableTags = [];
            }
        })
        init();
    });

    function init() {
        $("#loginName").hide();
        $("#showUser").hide();
        $(".prompt-con").click(function () {
            $("#messageAddr").focus();
        })
        $("#messageAddr").focus(function () {
            $(".prompt-con").hide();
        }).blur(function () {
            if ($(this).val().trim() == "" && $(this).css("display") != "none") {
                $(".prompt-con").show();
            }
        })
    }

    function deleteUser() {
        $("#loginName").find("span").text("");
        $("#showUser").hide().find("span").text("");
        $("#messageAddr").show().focus();
        $("#warnMessage").hide();
    }

    var availableTags = [];
    function searchMessageTo() {
        var searchWord = $("#messageAddr").val();
        if (searchWord.length <= 1) {
            return;
        }
        var searchSpiner = new Spinner(optsSmallSpinner).spin($("#loadingDiv").get(0)); //查询等待动画
        var params = {
            "ownerId": "<c:out value='${ownerId}'/>",
            "folderId": "<c:out value='${folderId}'/>",
            "userName": searchWord,
            "token": "<c:out value='${token}'/>"
        };

        var list;
        $.ajax({
            type: "POST",
            data: params,
            url: "${ctx}/share/listUser",
            error: function (request) {
                searchSpiner.stop();
                handlePrompt("error", '<spring:message code="inviteShare.listUserFail"/>');
                $("#messageAddr").focus();
            },
            success: function (data) {
                searchSpiner.stop();
                if (typeof(data) == 'string' && data.indexOf('<html>') != -1) {
                    window.location.href = "${ctx}/logout";
                    return;
                }
                availableTags = data;
                if (availableTags.length == 0) {
                    handlePrompt("error", "<spring:message code='teamSpace.error.NoSuchUser'/>");
                    return;
                }
                if (availableTags.length == 1) {
                    $("#messageAddr").val("");
                    addMessageTo(availableTags[0].loginName, availableTags[0].type, availableTags[0].name, availableTags[0].department, availableTags[0].cloudUserId);
                    return;
                }
                $("#messageAddr").bind("keydown", function (event) {
                    if (event.keyCode === $.ui.keyCode.TAB &&
                        $(this).data("ui-autocomplete").menu.active) {
                        event.preventDefault();
                    }
                }).autocomplete({
                    disabled: true,
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

                        addMessageTo(ui.item.loginName, ui.item.type, ui.item.name, ui.item.department, ui.item.cloudUserId);
                        return false;
                    }
                }).data("ui-autocomplete")._renderItem = function (ul, item) {
                    return $("<li>")
                        .append("<a><strong>" + item.name + "</strong> (" + item.loginName + ") " + "<br>" + item.department + "</a>")
                        .appendTo(ul);
                };

                $("#messageAddr").autocomplete("enable");
                $("#messageAddr").autocomplete("search", $("#messageAddr").val());
            }
        });
    }
    function split(val) {
        return val.split(/,\s*/);
    }
    function extractLast(term) {
        return split(term).pop();
    }

    function addMessageTo(userNum, userType, userName, department, cloudUsersId) {
        var loginName = '<shiro:principal property="loginName"/>';
        if (userNum == loginName) {
            handlePrompt("error", '<spring:message code="teamSpace.errorMsg.noChangeSelf"/>');
            $("#messageAddr").focus();
            return;
        }

        if (department != null && department != undefined) {
            if (department.length > 50) {
                department = department.substring(0, 35) + "...";
            }
        }
        else {
            department = "";
        }
        $("#loginName").find("span").html(userNum);
        $("#showUser").show().find("span").html(userName + "  " + department);
        $("#messageAddr").hide();
        var allMessageToStr = new Array();
        allMessageToStr.push($("#loginName").find("span").text());
        var params = {
            "teamId": $("#teamId").val(),
            "loginName": "" + allMessageToStr,
            "date": new Date(),
            "token": "<c:out value='${token}'/>"
        };

        $.ajax({
            type: "POST",
            url: "${ctx}/teamspace/member/isHaveMemberMgr",
            data: params,
            error: function (request) {
                handlePrompt("error", "<spring:message code='operation.failed'/>");
            },
            success: function (data) {
                $("#teamMemberId").val(data);

                if (data == 0) {
                    if (cloudUsersId > 0) {
                        $("#cloudUserId").val(cloudUsersId);
                    } else {
                        $("#cloudUserId").val(0);
                    }
                    $("#warnMessage").show();
                }

            }
        });
    }

    function submitChangeOwner() {
        if ($("#loginName").find("span").text() == "") {
            handlePrompt("error", "<spring:message code='teamSpace.change.isNullMsg'/>");
            $("#messageAddr").focus();
            return false;
        }

        ymPrompt.confirmInfo({
            title: "<spring:message code='teamSpace.title.ok'/>",
            maskAlphaColor: "gray",
            message: "<spring:message code='teamSpace.msg.changeOwnerClew'/><br/><spring:message code='teamSpace.msg.ischangOwener'/>",
            icoCls: "ymPrompt_confirm",
            width: 400,
            height: 220,
            handler: function (tp) {
                if (tp == 'ok') {
                    submit();
                }
            }
        });
    }

    function submit() {
        var allMessageToStr = new Array();
        allMessageToStr.push($("#loginName").find("span").text());
        var params = {
            "teamId": $("#teamId").val(),
            "userName": "" + allMessageToStr,
            "token": "<c:out value='${token}'/>"
        };
        $.ajax({
            type: "POST",
            url: "${ctx}/teamspace/changeOwner",
            data: params,
            error: function (request) {
                var status = request.status;
                if (status == 403) {
                    handlePrompt("error", "<spring:message code='teamSpace.error.forbiddenChangeOwner'/>");
                } else {
                    handlePrompt("error", "<spring:message code='operation.failed'/>");
                }
                top.listTeam(1);
            },
            success: function () {
                if (typeof(data) == 'string' && data.indexOf('<html>') != -1) {
                    window.location.href = "${ctx}/logout";
                    return;
                }
                top.ymPrompt.close();
                top.handlePrompt("success", "<spring:message code='operation.success'/>");
                top.listTeam(1);
            }
        });
    }


</script>
</body>
</html>
