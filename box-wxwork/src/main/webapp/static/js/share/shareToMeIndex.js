var orderField = "modifiedAt";
var order = "DESC";
var isDesc = null;

$(function() {
	$("#dateSort").click(function() {
		$(".sort-button").children().removeClass("sort-asc");
		$(".sort-button").children().removeClass("sort-desc");

		if(orderField == "modifiedAt") {
			if(order == "DESC") {
                order = "ASC";
				$("#dateSort").addClass("sort-asc");
			} else {
                order = "DESC";
				$("#dateSort").addClass("sort-desc");
			}
		} else {
			orderField = "modifiedAt";
            order = "DESC";
			$("#dateSort").addClass("sort-desc");
		}
		shareToMeFileListInit();
	});

	$("#nameSort").click(function() {
		$(".sort-button").children().removeClass("sort-asc");
		$(".sort-button").children().removeClass("sort-desc");

		if(orderField == "name") {
			if(order == "DESC") {
                order = "ASC";
				$("#nameSort").addClass("sort-asc");
			} else {
                order = "DESC";
				$("#nameSort").addClass("sort-desc");
			}
		} else {
			orderField = "name";
            order = "DESC";
			$("#nameSort").addClass("sort-desc");
		}
		shareToMeFileListInit();
	});

	$("#dateSort").addClass("sort-desc");
	shareToMeFileListInit();
})

function addblanbackground() {
	if($('#shareToMeFileList >div').length == 0) {
		$('#box').css('background', 'url(' + ctx + '/static/skins/default/img/iconblack_17.png)no-repeat center 10rem');
		$('#box').css('background-size', '7rem 8rem');
	} else {
		$('#box').css('background', '');
	}
}

function shareToMeFileListInit() {
	$.ajax({
		type: "POST",
		url: host + "/ufm/api/v2/shares/received",
		data: JSON.stringify({
            order: [{ field: 'type' , direction: "ASC" },{ field: orderField, direction: order }],
            thumbnail: [{ width: 96, height: 96 }, { width: 250, height: 200 }]
        }),
		error: handleError,
		success: function(data) {
			var fileList = data.contents;
			var $list = $("#shareToMeFileList");
			var $template = $("#shareToMeFileTemplate");
			$list.children().remove();

			for(var i in fileList) {
				var item = fileList[i];
				item.nodeId = item.nodeId || item.iNodeId;
				if(typeof(item.thumbnailUrlList)!="undefined" && item.thumbnailUrlList.length>0){
                	item.imgPath = item.thumbnailUrlList[1].thumbnailUrl;
                }
				item.modifiedAt = getFormatDate(new Date(item.modifiedAt), "yyyy-MM-dd");
				item.imgClass = getImgHtml(item.type, item.name);
				$template.template(item).appendTo($list);
                // 增加滑动
                $('.weui-cell_swiped').swipeout();
				// 设置数据
				var $row = $("#shareToMeFile_" + item.ownerId + "_" + item.nodeId);
                var $allRow = $("#shareToMeFiles_" + item.ownerId + "_" + item.nodeId);
				$row.data("node", item);
                $allRow.data("node", item);
				// 增加长按事件
				$row.on('click', onPress);
			}
		},
		complete: function() {
			$('.load').css('display', 'none');
			addblanbackground()
		}

	});
}

function showActionSheet() {
	$.actions({
		title: "选择操作",
		onClose: function() {
			console.log("close");
		},
		actions: [{
				text: "预览",
				className: "color-primary",
				onClick: function() {
					$.alert("预览成功");
				}
			},

			{
				text: "转存",
				className: 'color-danger',
				onClick: function() {
					$.alert("转存失败");
				}
			},
			{
				text: "退出",
				className: 'color-danger',
				onClick: function() {
					$.alert("退出失败");
				}
			},
		]
	});
}

function onPress(e) {
	e.stopPropagation()
	var $target = $(e.currentTarget);
	var node = $target.data("node");
	var actions = [];

	actions.push({
		text: "另存为...",
		className: "color-primary",
		onClick: function() {
			save2PersonalFile(node);
		}
	});

	actions.push({
		text: "退出共享",
		className: "color-primary",
		onClick: function() {
			deleteShareFile(node);
			addblanbackground()
		}
	});

	$.actions({
		title: node.name,
		actions: actions
	});
    layelTitle(node.imgClass,node.ownerName,node.modifiedAt)
}

function save2PersonalFileForLineScroll(o) {
	var node = $(o).parent().prev().data("node");
	save2PersonalFile(node);
}

function deleteShareFileForLineScroll(o) {
	var node = $(o).parent().parent().data("node");
	deleteShareFile(node);
	addblanbackground()
}

function save2PersonalFile(node) {
	node.menderName = node.ownerName;
    getFileFolderInfo(node)
	/* 在当前空间内移动，所以传入当前使用的ownerId*/
    var folderChooser = $("#copyToFolderChooserDialog").FolderChooser({
            exclude: function (r) {
                return r !== undefined && r.ownedBy === ownerId && r.id === parentId;
            },
            callback: function (ownerId, folderId) {
                var url;
                if (node.type === 1) {
                    url = host + "/ufm/api/v2/files/" + node.ownerId + "/" + node.nodeId + "/copy"
                } else {
                    url = host + "/ufm/api/v2/folders/" + node.ownerId + "/" + node.nodeId + "/copy"
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

function deleteShareFile(node) {
	$.confirm(
            "确定退出共享吗？",
            function(){
                $.ajax({
                    type: "DELETE",
                    async: false,
                    url: host + "/ufm/api/v2/shareships/"+node.ownerId+"/"+node.nodeId+"?userId="+node.sharedUserId+"&type="+node.sharedUserType,
                    error: function(xhr, status, error) {
                        $.toast("退出失败");
                    },
                    success: function() {
                        $.toast("退出成功");
                        shareToMeFileListInit();
                    }
                });
            })
}

function openShareFile(e) {
	var node = $(e).data("node");
    if (node.type <= 0) {
        gotoPage(ctx + "/shared/list/" + node.ownerId + "/" + node.nodeId + "?name=" + node.name + "&nodeId=" + node.nodeId);
    } else {
        var previewable = isFilePreviewable(node.name)
        if (previewable) {
            gotoPage(ctx + '/files/gotoPreview/' + node.ownerId + '/' + node.nodeId)
        } else {
            downloadShareFile(node);
        }

    }
}

function downloadShareFile(node) {
	var permission = getNodePermissions(node.nodeId,node.ownerId);
	if(typeof(permission.download)=="undefined" || permission.download!=1){
		$.toast("权限不够","forbidden");
		return;
	}
	$.ajax({
		type: "GET",
		async: false,
		url: host + "/ufm/api/v2/files/"+node.ownerId+"/"+node.nodeId+"/UrlAndBrowse",
		error: function(xhr, status, error) {
			$.toast("获取下载地址失败");
		},
		success: function(data) {
			$("#downloadFile").attr("href", data.downloadUrl);
			document.getElementById("downloadFile").click();
		}
	});
}

function getNodePermissions(parentId,ownerId) {
    var permission = null;
    var url = host + "/ufm/api/v2/permissions/"+ ownerId +"/" + parentId + "/" +curUserId;
    $.ajax({
        type: "GET",
        url: url,
        async: false,
        error: function (xhr, status, error) {
        	$.toast("获取权限失败");
        },
        success: function (data) {
            permission = data.permissions;
        }
    });
    return permission;
}

function jumpFolder(th, folderId) {
	return;
}
