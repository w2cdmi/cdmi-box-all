var currentPage = 1;
var listViewType = "thumbnail";
var catalogData = null;
$(document).ready(function () {
    processHash();
    window.onhashchange = processHash;
    //增加搜索关闭功能
    $("#searchClose").on("click", function (e) {
        $(e.target).parents(".staff-picker").hide();
    });
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

function listTeam(curPage) {
    var pageSize = getCookie("spaceListSizePerPage", 40);

    var url = host + "/ufm/api/v2/teamspaces/items";
    var params = {
        'type': 0,
        "userId": curUserId,
    };
    $.ajax({
        type: "POST",
        url: url,
        data: JSON.stringify(params),
        error: function (xhr, status, error) {
            $.toast("操作失败", "cancel");
        },
        success: function (data) {
            if (typeof(data) == 'string' && data.indexOf('<html>') != -1) {
                window.location.href = "${ctx}/logout";
                return;
            }
            //清空现有的列表
            var $list = $("#teamSpaceList");
            $list.children().remove();

            catalogData = data.memberships;
            if (catalogData.length == 0) {
                $(".not-space").css('display', 'block')
                if (curPage > 1) {
                    curPage--;
                    changeHash(curPage);
                } else {

                }
                return;
            }
            //TODO: setCookie("teamListPage", curPage);
            var $spaceTemplate = $("#spaceTemplate");
            for (var i in catalogData) {
                var space = catalogData[i].teamspace;

                space.teamRole = catalogData[i].teamRole;
                space.memberType = catalogData[i].member.type;
                space.createdAt = getFormatDate(new Date(space.createdAt), "yyyy-MM-dd");
                if (space.type != 5) {
                    $(".not-space").css('display', 'none')
                    $spaceTemplate.template(space).appendTo($list);

                    //设置数据
                    var $row = $("#space_" + space.id);
                    $row.data("obj", space);
                    //增加点击事件事件
                    $row.on('click', onPress);
                }

            }

        },
        complete: function () {
            $('.load').css('display', 'none');
            spatialBodyBackground();
        }
    });
}

function spatialBodyBackground() {
    if ($('#teamSpaceList>.space-row').length > 0) {
        $('body').css('background-size', '');
    } else {
        $('body').css('background', 'url(' + ctx + '/static/skins/default/img/join-space.png)no-repeat center 10rem');
        $('body').css('background-size', '5rem 5rem');
    }
}

function onPress(e) {
    e.stopPropagation()
    var $target = $(e.currentTarget);
    var node = $target.data("obj");
    var actions = [];
    actions.push({
        text: "查看空间信息",
        className: "color-primary",
        onClick: function () {
            openTeamInfo(node)
        }
    });
    if (node.teamRole == "admin") {
        actions.push({
            text: "修改",
            className: "color-primary",
            onClick: function () {
                openEditTeam(node);
            }
        });
    }
    if (node.teamRole == "admin" || node.teamRole == "manager") {
        actions.push({
            text: "成员管理",
            className: "color-primary",
            onClick: function () {
                openMemberMgr(node);
            }
        });
    }

    if (node.teamRole == "manager" || node.teamRole == "member") {
        actions.push({
            text: "退出空间",
            className: "color-primary",
            onClick: function () {
                openExitTeam(node);
                spatialBodyBackground();
            }
        });
    }

    if (node.teamRole == "admin") {
        actions.push({
            text: "移交空间",
            className: "color-primary",
            onClick: function () {
                openChangeOwner(node);
            }
        });

        actions.push({
            text: "解散空间",
            className: "color-primary",
            onClick: function () {
                openDelete(node);
                spatialBodyBackground();
            }
        });
    }

    $.actions({
        title: node.name,
        onClose: function () {

        },
        actions: actions
    });
    $(".weui-actionsheet__title").prepend("<i><img style='width: 2.1rem;display: inline-block;float: left;' src=" + ctx + "/static/skins/default/img/space-row-icon.png /></i>")
    $(".weui-actionsheet__title").append("<div><span>" + node.ownedByUserName + "</span><span>|</span><span>" + node.createdAt + "</span></div>")
}

function openCreateTeam() {
    gotoPage(ctx + '/teamspace/openAddTeamSpace');
}

function gotoTeamSpace(spaceId) {
    gotoPage(ctx + '/teamspace/file/' + spaceId);
}

function openMemberMgr(space) {
    gotoPage(ctx + '/teamspace/member/openMemberMgr/' + space.id);
}

function openExitTeam(space) {
    if(space.memberType == "department"){
        $.alert("该成员属于部门，不能主动退出空间")
    }else{
        $.confirm("请确认是否要退出该团队空间？", '退出空间',
            function () {
                $.ajax({
                    type: "DELETE",
                    url: host + "/ufm/api/v2/teamspaces/" + space.id + "/memberships/user/" + curUserId,
                    error: function (xhr, status, error) {
                        $.toast("操作失败", "cancel");

                    },
                    success: function (data) {
                        if (typeof(data) == 'string' && data.indexOf('<html>') != -1) {
                            window.location.href = "${ctx}/logout";
                            return;
                        }
                        if (data == "" || data == undefined || data == "OK") {
                            $.toast("退出成功", "success");
                            listTeam(1);
                        } else if (data == "Forbidden") {
                            $.toast("没有操作权限", "forbidden");
                        } else if (data == "NoFound") {
                            $.toast("成员不存在", "cancel");
                        } else {
                            $.toast("操作失败", "cancel");
                        }
                    }
                });
            }
        );
    }

}

function openDelete(space) {
    $.prompt(
        '解散空间会删除空间内所有的数据, 请输入 "yes" 确认解散该空间',
        '解散空间',
        function (input) {
            if (input.toUpperCase() === "YES") {
                $.ajax({
                    type: "DELETE",
                    url: host + "/ufm/api/v2/teamspaces/" + space.id,
                    data: {},
                    error: function (xhr, status, error) {
                        $.toast("操作失败", "cancel");
                    },
                    success: function () {
                        $.toast("操作成功");
                        processHash();
                    }
                });
            }
        }
    );
}

function openEditTeam(space) {
    gotoPage(ctx + '/teamspace/openEditTeamSpace?teamId=' + space.id);
}

function openTeamInfo(space) {
    gotoPage(ctx + '/teamspace/openGetTeamInfo?teamId=' + space.id);
}

function teamLinksList(teamId) {
    if (teamId == undefined || teamId == null || teamId == "") {
        var objData = $("#fileList").getGridSelectedData(catalogData, opts_viewGrid);
        teamId = objData[0].id;
    }
    var url = "${ctx}/sharedlinks/teamSpace/" + teamId;
    $('<form action="' + url + '" method="get">' + '</form>').appendTo('body').submit().remove();
}

function gotoSpaceError(exceptionName) {
    if (exceptionName == "Forbidden") {
        $.toast("<spring:message code='teamSpace.error.Forbidden'/>", "forbidden");
    } else if (exceptionName == "NoSuchTeamSpace") {
        $.toast("<spring:message code='teamSpace.error.NoFound'/>", "cancel");
    }
}

//移交空间
var deptStack = [];

function openChangeOwner(space) {
    deptStack = [];
    $("#selectedMemeber").empty();
    $("#addLeaguer").attr("teamdId", space.id).show();
    $("#addLeaguer").attr("ownedByUserNameId", space.ownedBy);
    $('.box').css('display', 'none');
}

function closeStaffChooser() {
    $('.box').css('display', 'block');
    $('#addLeaguer').css('display', 'none');
}

function closeAddressListChooser() {
    $('#addressListChooser').css('display', 'none');
}

function showAddressListChooser() {
//				$("#addressListChooser").show();
    $("#addLeaguer").hide();
    $("#teamSpaceAddressList").show();
    //            initStaffList(space.id);
    showDepAndUsers(0);
}

function backward() {
    var parentDeptId = $("#parentDeptId").val();
    var historyDepts = parentDeptId.split("|");
    var parentDept = 0;
    if (historyDepts.length > 1) {
        parentDept = historyDepts[historyDepts.length - 2];
    }
    if (parentDept == "0") {
        $('#addLeaguer').show();
        $("#parentDeptId").val(0);
        return;
    }
    if (parentDept == "" || parentDept == undefined) {

    } else {
        parentDept = parentDept.split(",");
    }
    $("#parentDeptId").val(parentDeptId.substring(0, parentDeptId.lastIndexOf("|")));
    parentDeptId = $("#parentDeptId").val();
    $("#parentDeptId").val(parentDeptId.substring(0, parentDeptId.lastIndexOf("|")));
    if (parentDept.length > 1) {
        if (parentDept[1] == "") {
            parentDept[1] = "企业通讯录";
        }
        showDepAndUsers(parentDept[0], parentDept[1]);
    } else {
        showDepAndUsers(0)
    }

}

function setSelectedMember(id, name) {
//				closeAddressListChooser();
    $("#staffPicker").show();
    $('#teamSpaceAddressList').hide();
    $("#selectedMemeber").empty().append('<p><img src="' + ctx + '/userimage/getUserImage/' + id + '"></p><span>' + name + '</span>').data({"loginName": name, "loginNameId": id});
    $("#parentDeptId").val(0);
}

function showDepAndUsers(deptId, deptName) {
    if (deptId == 0) {
        deptName = "企业通讯录";
    }
    var params = {
        deptId: deptId,
    };

    $.ajax({
        type: "POST",
        url: host + '/ecm/api/v2/users/listDepAndUsers',
        data: JSON.stringify(params),
        error: handleError,
        success: function (data) {
            //                    debugger;
            if (data != "[]") {

                if (typeof(deptName) == "undefined") {
                    deptName = "";
                }
                $("#parentDeptId").val($("#parentDeptId").val() + "|" + deptId + "," + deptName);
                $("#department").html(deptName);
                //清空现有的列表
                var $list = $("#shareList");
                $list.empty();
                data = $.parseJSON(data);
                var $memberTemplate = $("#pickerMemberTemplate");
                for (var i = 0; i < data.length; i++) {
                    var staff = data[i];
                    $memberTemplate.template(staff).appendTo($list);

                    //设置数据
                    var $row = $("#space_" + staff.id);
                    $row.data("obj", staff);
                }
            } else {
                $.toast("没有员工", "text");
            }
        }
    });
}

function submitChangeOwner() {
    var $picker = $("#addLeaguer");
    var teamId = $picker.attr("teamdId");
    var loginName = $("#selectedMemeber").data("loginName");
    var loginNameId = $("#selectedMemeber").data("loginNameId");
    var ownedByUserNameId = $picker.attr("ownedByUserNameId")
    if (loginName == null || loginName == "") {
        $.toast("请选择移交人");
        return;
    }
    if (loginNameId == ownedByUserNameId) {
        $.toast("不能移交给自己")
        return;
    }
    $.confirm(
        "变更拥有者后，原拥有者会自动降级为管理员，请谨慎操作。确认要变更吗？",
        '确定',
        function () {

            $.ajax({
                type: "PUT",
                url: host + "/ufm/api/v2/teamspaces/" + teamId + "/memberships/admin/" + loginNameId,
                error: function (xhr, status, error) {
                    $.toast("操作失败", "cancel");
                },
                success: function () {
                    if (typeof(data) == 'string' && data.indexOf('<html>') != -1) {
                        window.location.href = "${ctx}/logout";
                        return;
                    }
                    $picker.hide();
                    listTeam(1);
                    $.toast("移交成功", "success");
                    $('#staffPicker').hide();
                    $('.box').show();
                }
            });
        }
    );
}

function reseletion() {
    deptStack = [];
    $('.box').css('display', 'none');
    $('#staffPicker').hide();
    $('#addLeaguer').show();
}

function changeToListView() {
    $("#teamSpaceList").removeClass("space-thumbnail-view").addClass("space-list-view");
}

function changeToThumbnailView() {
    $("#teamSpaceList").removeClass("space-list-view").addClass("space-thumbnail-view");
}