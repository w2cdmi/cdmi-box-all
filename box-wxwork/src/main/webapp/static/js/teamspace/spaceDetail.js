/*显示相关定义*/
var viewType = 2; // 视图模式

var keyword = null;
var noPermission = [];
var currentPage = "spaceDetail";
$(function () {
    // init();
    //退出查看版本
    $(".version-info-tail").click(function(){
        $(".version-info").hide();
        $("#fileVersionList").html("");
    });
    creatFolder()
    sortShowHide()

    //parentId 为字符“0”，所以使用!=比较，不能使用!==比较
    if(parentId != 0){
        changeBreadcrumb(parentId);
    }

    var fileList = $("#fileListWrapper").FileList({
        ownerId:ownerId,
        teamRole:teamRole,
        teamType:teamType
    })
    fileList.showListInit()
});


function switchViewType(type) {
    for (var i = 0; i < noPermission.length; i++) {
        if (type == "list") {
            $("#" + noPermission[i]).css("background-position", "-235px -150px");
        } else {
            $("#" + noPermission[i]).css("background-position", "-135px -110px");
        }
    }
    listViewType = type;
    setCookie("listViewType", listViewType);
}

/* */
function onPress(e) {
    e.stopPropagation()
    console.log(e)
    var $target = $(e.currentTarget);
    var node = $target.data("node");
    var actions = [];
    if(teamType==5){
        actions.push({
            text: "另存为到其他空间",
            className: "color-primary",
            onClick: function () {
                showCopyToMeDialog(node);
            }
        });
        actions.push({
            text: "重命名",
            className: "color-primary",
            onClick: function () {
                renameDialog(node);
            }
        });
        actions.push({
            text: "删除",
            className: "color-primary",
            onClick: function () {
                deleteFile(node);
            }
        });
    }else{
        if (node.type == 0) {
            //普通文件夹

            actions.push({
                text: "文件夹信息",
                className: "color-primary",
                onClick: function() {
                    showFileProperties(node);
                }
            });
            actions.push({
                text: "设为快捷目录",
                className: "color-primary",
                onClick: function () {
                    addShortcutTeamFolder(node);
                }
            });

            if ((teamRole == 'admin' || teamRole == 'manager') && node.parent == 0) {
                actions.push({
                    text: "权限管理",
                    className: "color-primary",
                    onClick: function () {
                        grantAuthority(node);
                    }
                });
            }

        }
        actions.push({
            text: "外发",
            className: "color-primary",
            onClick: function () {
                showLinkDialog(node);
            }
        });
        actions.push({
            text: "重命名",
            className: "color-primary",
            onClick: function () {
                renameDialog(node);
            }
        });
        actions.push({
            text: "删除",
            className: "color-primary",
            onClick: function () {
                deleteFile(node);
            }
        });
        actions.push({
            text: "另存为...",
            className: "color-primary",
            onClick: function () {
                showCopyToMeDialog(node);
            }
        });
        actions.push({
            text: "移动到...",
            className: "color-primary",
            onClick: function () {
                showMoveToDialogForTeamspace(node);
            }
        });


        if(node.type==1){
            actions.push({
                text: "文件信息",
                className: "color-primary",
                onClick: function() {
                    showFileProperties(node);
                }
            });
            actions.push({
                text: "查看版本",
                className: "color-primary",
                onClick: function() {
                    optionVersionFile(node);
                }
            });
        }

    }
    $.actions({
        title: node.name,
        actions: actions
    });
    layelTitle(node.divClass,node.menderName,node.modifiedAt)
}


function addShortcutTeamFolder(node) {
    console.log(node)
	var prameter={
            createBy:curUserId,
            ownerId:node.ownedBy,
            nodeId:node.id,
			type:2
	}
    $.ajax({
        type: "POST",
        data: JSON.stringify(prameter),
        url:  host + "/ufm/api/v2/folders/" + curUserId + "/shortcut/create",
        error: function (xhr, status, error) {
            if(JSON.parse(xhr.responseText).code == "ExsitShortcut"){
                $.toast('快捷目录已存在',"forbidden");
            }else{
                $.toast('操作失败', 'forbidden');
            }
        },
        success: function (data) {
            $.toast("操作成功");
        }
    });
}

/*从搜索结果中跳转时,查询所有的父目录，构造面包屑导航 */
function changeBreadcrumb(folderId) {
    var breadcrumbItem = "";
    var url = host+"/ufm/api/v2/nodes/"+ownerId+"/"+folderId+"/path";
    $.ajax({
        type: "GET",
        url: url,
        cache: false,
        async: false,
        success: function (data) {
            var $directory = $("#directory");
            $directory.find("div:gt(0)").remove();
            if (data.length > 0) {
                $("#folderChooser").hide();
                for (var i = 0; i < data.length; i++) {
                    breadcrumbItem =" <p class='bread-arrow-right'></p><div id='jump_"+ data[i].id +"' data-info='"+ JSON.stringify(data[i]) +"' onclick=\"jumpFolder(this,"+ data[i].id +");\">"+data[i].name+"&nbsp;</div>";
                    $directory.append(breadcrumbItem);
                }
            }else{$("#folderChooser").hide();}
        },
        error: function(xhr, status, error){
            $.toast('获取目录失败', 'forbidden');
        }
    });
}

function showMoveToDialogForTeamspace(node) {
    /* 在当前空间内移动，所以传入当前使用的ownerId*/
    var nodePermission = getNodePermission(node.id, node.ownedBy);
    if(nodePermission["authorize"] != 1) {
        $.alert('您没有移动该文件/文件夹的权限');
        return;
    }
    getFileFolderInfo(node)

    var folderChooser = $("#copyfolderChooserDialog").FolderChooser({
            exclude: function (r) {
                return r !== undefined && r.ownedBy === ownerId && r.id === parentId;
            },
            callback: function (ownerId, folderId) {
                var paramss = {
                    "destParent": folderId,
                    "destOwnerId": ownerId,
                    "autoRename": true
                };

                var url;
                if (node.type === 1) {
                    url = host + "/ufm/api/v2/files/" + node.ownedBy + "/" + node.id + "/move"
                } else {
                    url = host + "/ufm/api/v2/folders/" + node.ownedBy + "/" + node.id + "/move"
                }
                $.ajax({
                    type: "PUT",
                    url: url,
                    data: JSON.stringify(paramss),
                    error: handleError,
                    success: function (data) {
                        $.toast("移动成功");
                        listFile(parentId,1);
                        $("#folderChooser").hide();
                    }
                });
            }
        }
    );
    //加载数据
    folderChooser.showDialog();
}

function showCopyToMeDialog(node) {
    var nodePermission = getNodePermission(node.id, node.ownedBy);
    if(nodePermission["download"] != 1){
        $.alert('您没有转存的权限');
        return;
    }
    getFileFolderInfo(node)
    var folderChooser = $("#copyfolderChooserDialog").FolderChooser({
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

function optionVersionFile(node){
    $(".version-info").show();

    $("#versionFileName").html(node.name);
    $("#versionFileImage").addClass(getImgHtml(node.type, node.name));

    fillVersionFileList(node.id);
}

function grantAuthority(node){
	gotoPage(ctx+'/teamspace/file/grantAuthority/'+ownerId+"/"+node.id);
}

function fillVersionFileList(nodeId){
    $.ajax({
        type: "get",
        url: host + "/ufm/api/v2/files/"+ownerId+"/"+nodeId+"/versions?offset=0&limit=10",
        cache: false,
        async: false,
        success: function (data) {
            var fileList = data.versions;
            if (fileList.length > 0) {
                var html = "";
                $("#fileVersionList").html(html);
                for (var i = 0; i < fileList.length; i++) {
                	console.log(fileList[i].createbyName);
                    html += "<li id='versionFile_"+fileList[i].id+"'><i><div class='version-icon'>版本</div>"+parseInt(fileList.length-i) +"</i>"
                        +"<div class='versionFile-middle'>"
                        +"<div>上传时间:<span>"+ getFormatDate(new Date(fileList[i].modifiedAt)) +"</span></div>"
                        +"<div>上传者:&nbsp;<span>"+fileList[i].createbyName+"</span></div>"
                        +"<div>文件大小:<h1>"+ formatFileSize(fileList[i].size) +"</h1></div>"
                        +"</div>"
                        +"<h3 onclick=\"downloadFileByNodeId('"+ fileList[i].id +"')\">下载</h3>";
                    if (i!=0) {
                        html += "<h3 onclick=\"deleteFileByNodeId('"+fileList[i].id+"','"+nodeId+"')\">删除</h3><h3 onclick=\"restoreVersion('"+ fileList[i].id +"','"+nodeId+"')\">恢复</h3>"
                    }
                    html += "</li>";

                }
                $("#fileVersionList").html(html);
            }
        },
        error: function(xhr, status, error){
            $.toast('获取版本文件失败', 'forbidden');
        }
    });
}

function setFolderIsShow(item, ownerId, nodeId) {
	var permisson = getNodePermission(nodeId);
	var p = 0;
	for ( var i in permisson) {
		p = p + permisson[i];
	}
	if (p == 0) {
		 item.divClass="folder-forbid";
		 var $row = $("#file_" + item.id);
		 $row.children(':first').children(':first').addClass("folder-forbid");
	}
}

function isVisibleNodeACL(item,ownerId,nodeId) {
    var url = ctx + "/teamspace/file/getIsVisibleNodeACL/"+ownerId+"/"+nodeId;
    var data = {
        token: token
    }
    $.ajax({
        type: "GET",
        url: url,
        async:true,
        data: data,
        error: function (xhr, status, error) {
        },
        success: function (data) {
          if(data=='true'){
        	  item.divClass="folder-secret";
        	  var $row = $("#file_" + item.id);
        	  $row.children(':first').children(':first').removeClass('folder-icon').addClass("folder-secret");
          }
        }
    });
}



