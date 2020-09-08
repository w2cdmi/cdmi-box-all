/*显示相关定义*/
var viewType = 2; // 视图模式

var keyword = null;
var noPermission = [];

function getRootInbox(){
    $.ajax({
        type: "GET",
        url: host + "/ufm/api/v2/folders/"+curUserId+"/getInboxFolder",
        async:false,
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
            rootInboxId=data.id;
        }
    });

}


$(function () {
    getRootInbox();
    init();
    sortShowHide()
    changeBreadcrumb();
});

/* */
function onPress(e) {
    var $target = $(e.currentTarget);
    e.stopPropagation();
    var node = $target.data("node");
    var actions = [];
    
    actions.push({
        text: "另存为...",
        className: "color-primary",
        onClick: function () {
            showCopyToMeDialog(node);
        }
    });

    actions.push({
        text: "删除",
        className: "color-primary",
        onClick: function () {
            deleteFile(node);
        }
    });

    $.actions({
        title: node.name,
        actions: actions
    });
    $(".weui-actionsheet__title").prepend("<i class=" + node.divClass + "></i>")
    $(".weui-actionsheet__title").append("<div><span>" + node.modifiedAt + "</span></div>")
}

/*构造面包屑导航 */
function changeBreadcrumb() {
    var breadcrumbItem = "";
    var url = host + "/ufm/api/v2/nodes/" + ownerId + "/" + parentId + "/path";
    $.ajax({
        type: "GET",
        url: url,
        cache: false,
        async: false,
        success: function (data) {
            var $directory = $("#directory");
            $directory.find("div:gt(0)").remove();
            if (data.length > 0) {
                for (var i = 0; i < data.length; i++) {
                    breadcrumbItem =" <div>"+data[i].name+"&nbsp;</div>";
                    $directory.append(breadcrumbItem);
                }
            }
        },
        error: function(xhr, status, error){
            $.toast('获取目录失败', 'forbidden');
        }
    });
}

function showCopyToMeDialog(node) {
    var folderChooser = $("#folderChooserDialog").FolderChooser({
            exclude: function (r) {
                return r !== undefined && r.ownedBy === ownerId && r.id === parentId;
            },
            callback: function (ownerId, folderId) {
                var url;
                if (node.type === 1) {
                    url = host + "/ufm/api/v2/files/" + node.ownedBy + "/" + node.id + "/copy"
                } else {
                    url = host + "/ufm/api/v2/folders/" + node.ownedBy + "/" + node.id + "/copy"
                }

                var params = {
                    destParent: folderId,
                    destOwnerId: ownerId,
                    autoRename: true
                };

                $.ajax({
                    type: "PUT",
                    url: url,
                    data: JSON.stringify(params),
                    error: handleError,
                    success: function (data) {
                        $("#folderChooser").hide();
                        $.toast("另存成功");
                    }
                });
            }
        }
    );
    //加载数据
    folderChooser.showDialog();
}

function saveToPersonal(nodeId){
	var node = $("#file_" + nodeId).data("node");
	showCopyToMeDialog(node);
}

function sendLinkAgain(ownerId, folderId) {
    gotoPage(ctx + "/share/reciveFolder/" + ownerId + "/" + folderId);
}
