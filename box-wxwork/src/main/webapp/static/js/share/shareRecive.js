
var isDesc = true;
var pageSize = getCookie("fileListPageSize", 40);
var orderField = getCookie("orderField", "modifiedAt");
var teamId;
var __page = 1;
var __loadmore = false;

$(function() {
    $('.new-share-recive').click(function() {
        $.prompt({
            text: "不超过255个字符，前后两端不能出现特殊字符",
            title: "新建收件箱",
            onOK: function(text) {
                createFolder(text);
            },
            onCancel: function() {},
        });
    });

    initInboxFileList();
});

function initInboxFileList(){
    $.ajax({
        type: "GET",
        url: host + "/ufm/api/v2/folders/" + curUserId + "/getInboxFolder",
        error: function(xhr, status, error){
            var responseObj = $.parseJSON(xhr.responseText);
            switch(responseObj.code) {
                case "Forbidden" || "SecurityMatrixForbidden":
                    $.toast("您没有权限进行该操作", "forbidden");
                    break;
                case "ExceedUserMaxNodeNum":
                    $.toast("文件总数超过限制", "cancel");
                    break;
                case "RepeatNameConflict":
                    $.toast("已存在相同的文件夹","cancel");
                    break;
                default:
                    $.toast(responseObj.message, "cancel");
            }
        },
        success: function(data) {
            parentId = data.id;
            listFile();
        }
    });
}

function listFile() {
    $("#folders").empty();

    var url = host + "/ufm/api/v2/folders/" + curUserId + "/" + parentId + "/items";
    var params = {
        limit: pageSize,
        offset: (__page-1)*pageSize,
        order: [{ field: 'type' , direction: "ASC" },{ field: orderField, direction: order }],
        thumbnail: [{ width: 96, height: 96 }, { width: 250, height: 200 }]
    };
    $.ajax({
        type: "POST",
        url: url,
        data: JSON.stringify(params),
        error: function(xhr, status, error){
        },
        success: function(data) {
            var fileList = data.folders;
            __page = 1;
            __loadmore = data.totalCount > __page * pageSize;
            fillFolderDiv(fileList);
        }
    });
}

function addBackgroundPictures() {
    if($('#folders div').length == 0) {
        showNotQulickFolder()
    } else {
        $('#box').css('background', '')
    }
}

function fillFolderDiv(data) {
    var $template = $("#receiveFolderItemTemplate");
    for(var i = 0; i < data.length; i++) {
        data[i].divClass = getImgHtml(data[i].type, data[i].name);
        data[i].createdAt = getFormatDate(new Date(data[i].createdAt), "yyyy-MM-dd");
        $template.template(data[i]).appendTo($("#folders"));

        var $row = $("#inbox_" + data[i].id)
        var $swipeRow = $("#inboxs_" + data[i].id)
        $row.data("node", data[i]);
        $swipeRow.data("node",data[i])
        $row.on('click', onPress);

    }

    // 增加滑动
    $('.weui-cell_swiped').swipeout();

    addBackgroundPictures()
}

function createFolder(newName) {
    var regEn = /[`~!@#$%^&*()_+<>?:"{},.\/;'[\]]/im;
    var regCn = /[·！#￥（——）：；“”‘、，|《。》？、【】[\]]/im;
    var lastname = newName.charAt(newName.length-1);
    var firstname = newName.charAt(0);
    if(regEn.test(lastname) || regCn.test(lastname)) {
        $.alert("最后一个字符不能以特殊符号结束");
        return false;
    }else if(newName == "" || newName == null){
        $.alert("名字不能为空");
    } else if(regEn.test(firstname) || regCn.test(firstname)) {
        $.alert("第一个字符不能以特殊符号开头");
        return false;
    }

    var parameter = {
        name: newName,
        parent: parentId
    };

    $.ajax({
        type: "POST",
        url: host + "/ufm/api/v2/folders/" + curUserId,
        data: JSON.stringify(parameter),
        error: function(xhr, status, error){
            var responseObj = $.parseJSON(xhr.responseText);
            switch(responseObj.code) {
                case "Forbidden" || "SecurityMatrixForbidden":
                    $.toast("您没有权限进行该操作", "forbidden");
                    break;
                case "ExceedUserMaxNodeNum":
                    $.toast("文件总数超过限制", "cancel");
                    break;
                case "RepeatNameConflict":
                    $.toast("已存在相同的文件夹","cancel");
                    break
                default:
                    $.toast(responseObj.message, "cancel");
            }
        },
        success: function(data) {
            listFile();
        }
    });
}

function shareByLink(o) {
    var node = $(o).parent().data("node");
    gotoPage(ctx + "/share/reciveFolder/" + node.ownedBy + "/" + node.id);
}

function onPress(e) {
    e.stopPropagation();
    var $target = $(e.currentTarget);
    var node = $target.data("node");
    var actions = [];
    actions.push({
        text: "发送",
        className: "color-primary",
        onClick: function() {
            gotoPage(ctx + "/share/reciveFolder/" + node.ownedBy + "/" + node.id);
        }
    });
/*
    actions.push({
        text: "另存为到其他空间",
        className: "color-primary",
        onClick: function() {
            save2PersonalFile(node);
        }
    });
*/
    actions.push({
        text: "删除",
        className: "color-primary",
        onClick: function() {
            deleteReceiveFolder(node);
        }
    });
    $.actions({
        title: node.name,
        actions: actions
    });
    $(".weui-actionsheet__title").prepend("<i class="+node.divClass+"></i>")
    $(".weui-actionsheet__title").append("<div><span>"+node.menderName+"</span><span>|</span><span>"+node.createdAt+"</span></div>")
}

function deleteReceiveFolderByLineScroll(o) {
    var node = $(o).parent().data("node");
    deleteReceiveFolder(node);
}

function deleteReceiveFolder(node) {
    $.ajax({
        type: "DELETE",
        url: host + "/ufm/api/v2/nodes/" + node.ownedBy + "/" + node.id,
        error: function(xhr, status, error){
            $.toast('操作失败', 'forbidden');
        },
        success: function(data) {
            $.toast("删除成功");
            listFile();
        }
    });
}

function save2PersonalFile(node) {
    /* 在当前空间内移动，所以传入当前使用的ownerId*/
    var folderChooser = $("#folderChooserDialog").FolderChooser({
        ownerId: curUserId,
        exclude: function (r) {
            return r !== undefined && r.ownedBy === ownerId && r.id === parentId;
        },
        callback: function (ownerId, folderId) {
            var params = {
                destParent: folderId,
                destOwnerId: ownerId,
                autoRename: true
            };
            var url;
            if (node.type === 1) {
                url = host + "/ufm/api/v2/files/" + node.ownedBy + "/" + node.id + "/copy"
            } else {
                url = host + "/ufm/api/v2/folders/" + node.ownedBy + "/" + node.id + "/copy"
            }
            $.ajax({
                type: "PUT",
                url: url,
                data: JSON.stringify(params),
                error: function (xhr, status, error) {
                    $.toast("操作失败");
                },
                success: function (data) {
                    $.toast("另存成功");
                }
            });
        }
    });

    //加载数据
    folderChooser.showDialog();
}
// 显示暂无收件箱
function showNotQulickFolder() {
    var html=""
    html += "<i class='index-not-qulick' style='width: 5rem;margin: 6rem auto 0'>"
    html += "<img src='"+ctx+"/static/skins/default/img/not-qulick-folder1.png'/>"
    html += "</i>"
    html = html+ "<p class='index-not-qulick-title index-not-qulick-title-first'>您还没有收集到任何文件，收集流程：</p>"
    html = html+ "<p class='index-not-qulick-title'>1.点击下方新建收件箱</p>"
    html = html+ "<p class='index-not-qulick-title'>2.点击[发送链接]将创建的收件箱发送给对方</p>"
    html = html+ "<p class='index-not-qulick-title'>3.别人点开您的收件箱上传文件</p>"
    html = html+ "<p class='index-not-qulick-title index-not-qulick-title-last'>4.在此处查看</p>"
    $("#folders").prepend(html)
}