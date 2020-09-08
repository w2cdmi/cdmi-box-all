var orderField = "modifiedAt";
var order = "DESC";
var _pageNumber = 1
var pageSize = 10;
var pagination
var viewer = null;
var num = -1;
$(function() {
	$("#orderField_modifiedAt").find('i').css('visibility','visible');
	pagination = $('#pagination').Pagination()
	pagination.init()
	pagination.onPageChange = function(pageNumber) {
		_pageNumber = pageNumber
		shareToMeFileListInit()
	}
	$("#orderField_modifiedAt").click(function() {
		var ico = $(this).find('i');
		ico.css('visibility','visible');
		$("#orderField_name").find('i').css('visibility','hidden')

		if(orderField == "modifiedAt") {
            if(order == "DESC") {
                order = "ASC";
				$("#orderField_modifiedAt").children().removeClass("fa fa-long-arrow-down");
				$("#orderField_modifiedAt").children().addClass("fa fa-long-arrow-up");
			} else {
                order = "DESC";
				$("#orderField_modifiedAt").children().removeClass("fa fa-long-arrow-up");
				$("#orderField_modifiedAt").children().addClass("fa fa-long-arrow-down");
			}
		} else {
			orderField = "modifiedAt";
            order = "DESC";
		}
		shareToMeFileListInit();
	});

	$("#orderField_name").click(function() {
		var ico = $(this).find('i');
		ico.css('visibility','visible');
		$("#orderField_modifiedAt").find('i').css('visibility','hidden');

		if(orderField == "name") {
            if(order == "DESC") {
                order = "ASC";
				$("#orderField_name").children().removeClass("fa fa-long-arrow-down");
				$("#orderField_name").children().addClass("fa fa-long-arrow-up");
			} else {
                order = "DESC";
				$("#orderField_name").children().removeClass("fa fa-long-arrow-up");
				$("#orderField_name").children().addClass("fa fa-long-arrow-down");
			}
		} else {
			orderField = "name";
            order = "DESC";
			// $("#nameSort").addClass("sort-desc");
		}
		shareToMeFileListInit();
	});
	$('#sort_button').popover($("#sort_popover"), true, "left", function(t) {});

	// $("#dateSort").addClass("sort-desc");
	shareToMeFileListInit();
    // 复制文件
    function showCopyToDialogTeam(node) {
        var names = judgeNameLength(node);
        var folderChooser = $("#copyToFolderChooserDialog").FolderChooser({
                title:"另存“"+ names +"”到",
                exclude: function (r) {
                    return r !== undefined && r.ownedBy === ownerId && r.id === parentId;
                },
                callback: function (ownerId, folderId) {
                    copyTo(node,ownerId,folderId,function (data) {
                            folderChooser.closeDialog()
                            $.toast("另存成功");
                        shareToMeFileListInit();
                        }
                    )

                }
            }
        );

        //加载数据,并显示
        folderChooser.showDialog();
    }
	$("#table_popover dt").mousedown(function() {
		var ids = selectedRow.attr("id");
		var ownerIds = selectedRow.attr("ownerIds");
		var iNodeIds = selectedRow.attr("iNodeIds");
		var sharedUserId = selectedRow.attr("sharedUserId");
		var sharedUserType = selectedRow.attr("sharedUserType");
		var node = selectedRow.data("node")
		if($(this).attr("command") == "1") {
            showCopyToDialogTeam(node)
			// selectFolderDialog.show0(ownerIds, iNodeIds)
		}
		if($(this).attr("command") == "2") {
			deleteShareFile(ownerIds, iNodeIds, sharedUserId,sharedUserType);
		}
        if($(this).attr("command") == "3") {
            downloadFile(node)
        }
	})
})

var selectedRow = null;
function worker_Click(e) {
	e.stopPropagation()
}
function shareToMeFileListInit(page) {
    _pageNumber = page || _pageNumber;
	var _loading = $.Tost('数据加载中...').show()
	$.ajax({
		type: "POST",
        url: host + "/ufm/api/v2/shares/received",
        data: JSON.stringify({
			offset: (_pageNumber - 1) * pageSize,
			limit: pageSize,
            order: [{field: 'type', direction: "ASC"}, {field: orderField, direction: order}],
            thumbnail: [{width: 96, height: 96}, {width: 250, height: 200}]
        }),
		error: handleError,
		success: function(data) {
			pagination.setTotalSize(data.totalCount)
			pagination.setCurrentPage(_pageNumber)
			pagination.setTotalPages(Math.ceil(data.totalCount / pageSize)==0 ? "1" : Math.ceil(data.totalCount / pageSize));
			var fileList = data.contents;
			var $list = $("#shareToMeFileList");
			var $template = $("#shareToMeFileTemplate");
			$list.children().remove();
			if (fileList.length === 0) {
                $('.notfind').show();
            } else {
                $('.notfind').hide();
			}

			_loading.hide();
			for(var i in fileList) {
				var item = fileList[i];
				// var items = item.push('iconimg')
				if(typeof(item.thumbnailUrlList) != "undefined" && item.thumbnailUrlList.length > 0) {
					item.imgPath = item.thumbnailUrlList[1].thumbnailUrl;
				}
				item.modifiedAt = getFormatDate(new Date(item.modifiedAt),
					"yyyy/MM/dd");
				item.imgClass = getImgHtml(item.type, item.name);
				// type:1为文件，其他都是目录
				if(item.type < 1) {
					item.iconimg = 'ico-folder';
				} else {
                    item.iconimg = getFileIconClass(item.name);
				}
				if(item.size == 0) {
					item.size = "";
				} else {
					item.size = formatFileSize(item.size);
					if(typeof(item.thumbnailUrlList) != "undefined" && item.thumbnailUrlList.length > 0) {
						item.imgPath = item.thumbnailUrlList[0].thumbnailUrl;
					}
                    if(isImg(item.name)){
                        num++;
                        var index = item.thumbnailUrlList[0].thumbnailUrl.lastIndexOf("/");
                        var imgSrc = item.thumbnailUrlList[0].thumbnailUrl.substring(0,index)
                        item.imgSrc = imgSrc;
                        item.num = num
                    }
				}
				$template.template(item).appendTo($list);
				$('#shareToMeFileList').find('a#worker').on('click', worker_Click)

				// 设置数据
				var $row = $("#shareToMeFile_" + item.ownerId + "_" + item.iNodeId);
				$row.data("node", item);
			}
			num = -1;
			$("#shareToMeFileList tr").mouseenter(function() {
				selectedRow = $(this);
			})
			$("table tbody td").find("a[id='worker']").each(function() {
				$(this).popover($("#table_popover"), true, "right", function(t) {
				    // debugger;
                    $("#table_popover").find("dt").hide()
                    var $menu = $("#table_popover").find("dt[role*='" + t.attr("data") + "']");
                    if($menu.length > 0) {
                        $menu.show();
                    } else {
                        //没有相关的菜单项时，不显示菜单
                        $("#table_popover").hide();
                    }
				});
			})
		}

	});
}

function deleteShareFile(ownerId, nodeId, userId, sharedUserType) {
	userId = userId || curUserId
		$.Confirm("确认退出分享？", function() {
			$.ajax({
				type: "DELETE",
				url: host + "/ufm/api/v2/shareships/" + ownerId + "/" + nodeId + "?userId=" + userId + "&type="+sharedUserType,
				error: function() {
					$.Tost("退出失败");
				},
				success: function() {
					$.Tost("退出成功");
					shareToMeFileListInit();
				}
			});
		})
}

function clickSharedItem() {
	var ownerIds = selectedRow.attr("ownerIds");
	var iNodeIds = selectedRow.attr("iNodeIds");
	var types = selectedRow.attr("types");
	var fileName = selectedRow.attr("fileName")
    if (types <= 0) {
        gotoPage(ctx + "/shared/list/" + ownerIds + "/" + iNodeIds);
    } else {
        if(isImg(fileName)){
            var dataIndex = selectedRow.find("img").attr("data-index")
            if(viewer !== null) {
                viewer.destroy();
            }
            viewer = new Viewer(document.getElementById('datagrid'), {
                url: 'data-original',
                shown: function () {
                    viewer.view(dataIndex)
                }
            });
            viewer.show()
        }else{
            downloadShareFile(ownerIds, iNodeIds, fileName);
        }

    }
}

function downloadShareFile(ownerId, nodeId, name) {
    // var permission = getNodePermission(ownerId, nodeId, curUserId);
    // if (typeof(permission.download) == "undefined" || permission.download != 1) {
    //     $.Alert("权限不够", "forbidden");
    //     return;
    // }

    downloadFileByNodeId(ownerId, nodeId, name)
}