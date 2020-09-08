var pageSize = getStorage("fileListPageSize", 40);
var orderField = "modifiedAt";
var order = "DESC";
var desc = true;
var _pageNumber = 1
var pagination
var shareDialog
var __page = 1;
var viewer = null;
var num = -1;
    $(function() {
	$("#orderField_modifiedAt").find('i').css('visibility','visible');
	pagination = $('#pagination').Pagination()
	pagination.init()
	pagination.onPageChange = function(pageNumber) {
		_pageNumber = pageNumber
		shareByMeListInit()
	}

	shareDialog = $('#shareDialog').ShareDialog()
	shareDialog.onSuccess = function() {
		shareByMeListInit()
	}
	shareDialog.init0()

	$("#orderField_modifiedAt").click(function() {
		var ico = $(this).find('i');
		ico.css('visibility','visible');
		$("#orderField_name").find('i').css('visibility','hidden');

		if(orderField == "modifiedAt") {
            if (order == "DESC") {
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
			// $("#dateSort").addClass("sort-desc");
		}
		shareByMeListInit();
	});

	$("#orderField_name").click(function() {
		var ico = $(this).find('i');
		ico.css('visibility','visible');
		$("#orderField_modifiedAt").find('i').css('visibility','hidden');
		if(orderField == "name") {
            if (order == "DESC") {
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
		shareByMeListInit();
	});
	$('#sort_button').popover($("#sort_popover"), true, "left", function(t) {});
	shareByMeListInit();
	$("#table_popover dt").mousedown(function() {
		var ids = selectedRow.attr("ids");
		var ownerIds = selectedRow.attr("ownerIds");
		var types = selectedRow.attr("types");
		if($(this).attr("command") == "1") {
			var deptId = 0
			shareDialog.show0(deptId, {
				id: ids
			},types)
			// gotoPage(ctx + "/share/folder/" + ownerIds  + "/" + ids );
		}
		if($(this).attr("command") == "2") {
			cancelShare(ids)
		}
		if($(this).attr("command") == "3") {
			gotoPage(ctx + "/folder?rootNode=" + ids);
		}

	})
})
var selectedRow = null;
function worker_Click(e) {
	e.stopPropagation()
}

function shareByMeListInit() {
	var _loading = $.Tost('数据加载中...').show()
	var parameter = {
        limit: pageSize,
        offset: (__page - 1) * pageSize,
        order: [{ field: 'type' , direction: "ASC" },{ field: orderField, direction: order }],
        thumbnail: [{ width: 96, height: 96 }, { width: 250, height: 200 }]
    }
	$.ajax({
		type: "POST",
		url: host + "/ufm/api/v2/shares/distributed",
		data: JSON.stringify(parameter),
		error: handleError,
		success: function(data) {
            // pagination.setTotalSize(data.totalElements)
            // pagination.setCurrentPage(_pageNumber)
            // pagination.setTotalPages(data.totalPages)
            // __loadmore = data.totalCount > (__page - 1) * pageSize;
            pagination.setTotalSize(data.totalCount)
            pagination.setCurrentPage(__page)
            pagination.setTotalPages(Math.ceil(data.totalCount/pageSize)==0 ? "1" : Math.ceil(data.totalCount/pageSize))
			var fileList = data.contents;
			var $list = $("#shareByMeFileList");
			var $template = $("#shareByMeFileTemplate");
			if (fileList.length == 0) {
                $('.notfind').show();
            } else {
                $('.notfind').hide();
			}
			$list.children().remove();
			if(fileList.length == 0) {
				$('#box').css('background', 'url(' + ctx + '/static/skins/default/img/iconblack_17.png)no-repeat center center');
				$('#box').css('background-size', '5rem 5rem');
			} else {
				$('#box').css('background', '')
			}
			_loading.hide();
			for(var i in fileList) {
				var item = fileList[i];
				if(item.nodeId != -1) {
					item.iconimg = getFileIconClass(item.name);
					if(item.type <= 0) {
						item.iconimg = 'ico-sharefolder'
					}
					if(item.size == 0) {
						item.size = "";
					} else {
						item.size = formatFileSize(item.size);
						if(typeof(item.thumbnailUrlList) != "undefined" && item.thumbnailUrlList.length > 0) {
							item.imgPath = item.thumbnailUrlList[0].thumbnailUrl;
						}
						if(isImg(item.name)){
						    num++
                            var index = item.thumbnailUrlList[0].thumbnailUrl.lastIndexOf("/");
                            var imgSrc = item.thumbnailUrlList[0].thumbnailUrl.substring(0,index)
                            item.imgSrc = imgSrc
                            item.num = num
                        }
					}
					var shareStatus = 1; //都是共享文件
					item.imgClass = getImgHtml(item.type, item.name, shareStatus);
					item.modifiedAt = getFormatDate(new Date(item.modifiedAt), "yyyy/MM/dd");
					$template.template(item).appendTo($list);
					$('#shareByMeFileList').find('a#worker').on('click', worker_Click)
	
					//设置数据\n
					var $row = $("#shareByMeFile_" + item.nodeId);
					$row.data("node", item);
				}
     			}
     			num = -1
			$("#shareByMeFileList tr").mouseenter(function() {
				selectedRow = $(this);
				var types = selectedRow.attr("types");
				if(types <= 0) {
					$('#gotoinner').show();
				} else {
					$('#gotoinner').hide();
				}
			})
			var width = window.screen.width;
			$('.file-info').width(width);
			$('#shareByMeFileList>li').width(width + $('.line-buttons').width());
			$("table tbody td a[id='worker']").each(function() {
				$(this).popover($("#table_popover"), true, "right", function() {

				});
			})
		},
		complete: function() {
			$('.load').css('display', 'none');
		}
	});
}

function enterFolder(ownerId,nodeId,th) {

	var type = $(th).attr("types");
	var fileName = $(th).attr("fileName");
    var preview = isPreviewable(fileName)
	if(type==1){
		if(preview){
			gotoPageOpen(ctx+'/files/gotoPreview/'+ ownerId +'/'+ nodeId)
		}else{
		    if(isImg(fileName)){
                var dataIndex = $(th).find("img").attr("data-index")
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
                downloadFileByNodeId(ownerId, nodeId,fileName)
            }
		}

	}else{
        gotoPage(ctx + "/folder?rootNode=" + nodeId);
	}


}

function shareByMeIndexviewshare(nodeId) {
	gotoPage(ctx + "/share/folder/" + ownerId + "/" + nodeId);
}

function cancelShare(nodeId) {
	$.Confirm("是否取消对该文件的共享？", function() {
		$.ajax({
            type : "DELETE",
            url: host + "/ufm/api/v2/shareships/" + ownerId + "/" + nodeId,
			error: handleError,
			success: function(data) {
				$.Tost("取消共享成功").show().autoHide(2000);
				shareByMeListInit();
			},
			error: function() {
				$.Alert("取消共享失败");
			}
		});
	});
}