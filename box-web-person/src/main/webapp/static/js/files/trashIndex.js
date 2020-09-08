var order = "DESC";
var orderField = "modifiedAt";
var _pageNumber = 1
var pageSize = 10;
var pagination
$(function() {
	console.log(ctx);
    var menubar = $("#menubar").Menubar()
	menubar.init();
	menubar.onsearchfile = function (docType) {
		console.log(docType);
		_docType = docType
		_isSearch = true
		console.log('${ctx}');
		gotoPage(ctx+'/folder?rootNode=0&docType='+ docType)

		// datagridLoad()
	}
	pagination = $('#pagination').Pagination()
	pagination.init()
	pagination.onPageChange = function(pageNumber) {
		_pageNumber = pageNumber
		var offset = (_pageNumber - 1) * pageSize;
		trashFileListInit(offset, pageSize);
	}

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
		trashFileListInit();
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

		trashFileListInit();
	});
	$('#sort_button').popover($("#sort_popover"), true, "left", function(t) {});
	$("#dateSort").addClass("sort-desc");
	trashFileListInit();
	$("#table_popover dt").mousedown(function() {
		var ids = selectedRow.attr("ids");
		if($(this).attr("command") == "1") {
			restore(ids);
		}
		if($(this).attr("command") == "2") {
			deleteTrashFile(ids);
		}
	})
})
var selectedRow = null;
function worker_Click(e) {
	e.stopPropagation()
}
function trashFileListInit(offset, limit) {
	var _loading = $.Tost('数据加载中...').show();
    var params={
        offset: offset || 0,
        limit: limit || pageSize,
        order: [{ field: 'type' , direction: "ASC" },{ field: orderField, direction: order }],
        thumbnail: [{ width: 96, height: 96 }, { width: 250, height: 200 }]
    }
    $.ajax({
		type: "POST",
		url: host + "/ufm/api/v2/trash/" + curUserId,
        data : JSON.stringify(params),
		error: handleError,
		success: function(data) {
			_loading.hide();
            pagination.setTotalSize(data.totalCount)
            pagination.setTotalPages(Math.ceil(data.totalCount / pageSize) == "0" ? "1" : Math.ceil(data.totalCount / pageSize))

            var fileList = data.folders.concat(data.files);
			var $list = $("#trashFileList");
			var $template = $("#trashFileTemplate");
			$list.children().remove();
            if (fileList.length == 0) {
                $('.notfind').show();
            } else {
				$('.notfind').hide();
				$.Tost('数据加载中...').hide();
            }

			for(var i in fileList) {
				var item = fileList[i];
				item.modifiedAt = getFormatDate(new Date(item.modifiedAt),
					"yyyy/MM/dd");
				item.imgClass = getImgHtml(item.type, item.name);
				item.iconimg = getFileIconClass(item.name);
				if(item.type === 0) {
					item.iconimg = 'ico-folder';
				}
				if(item.size == 0) {
					item.size = "";
				} else {
					item.size = formatFileSize(item.size);
					if(typeof(item.thumbnailUrlList) != "undefined" && item.thumbnailUrlList.length > 0) {
						item.imgPath = item.thumbnailUrlList[0].thumbnailUrl;
					}
				}
				$template.template(item).appendTo($list);
				$('#trashFileList').find('a#worker').on('click', worker_Click)

				//设置数据\n
				var $row = $("#trashFile_" + item.id);
				$row.data("node", item);

				// new Hammer($row[0]).on('press', press);
			}
			$("#trashFileList tr").mouseenter(function() {
				selectedRow = $(this);
				//alert(selectedRow)
			})
			$("table tbody td a[id='worker']").each(function() {
				$(this).popover($("#table_popover"), true, "right", function(t) {});
			})
			var width = window.screen.width;
			$('.file').width(width);
			$('#trashFileList li').width(width + $('.line-buttons').width());
			// $('.file').addLineScrollAnimate();
		},
		complete: function() {
			$('.load').css('display', 'none');
		}
	});
}

function restore(ids) {
	$.Confirm("回收站的文件恢复到原路径，确定恢复吗？", function() {
		$.ajax({
			type: "PUT",
            url: host + "/ufm/api/v2/trash/" + curUserId + "/" + ids,
            data : JSON.stringify({
                autoRename:true
            }),
			error: handleError,
			success: function(data) {
				$.Tost("恢复成功");
				trashFileListInit();
			}
		});
	});
	
}

function trashIdexlinebButtonShare(nodeId) {
	restore(nodeId);
}

function trashIdexlinebButtonLink(nodeId) {
	deleteTrashFile(nodeId);
}

function deleteTrashFile(ids) {
    $.Confirm("确定要删除此文件吗？", function() {
        $.ajax({
            type : "DELETE",
            url: host + "/ufm/api/v2/trash/" + curUserId + "/" + ids,
            error: handleError,
            success: function(data) {
                $.Tost("删除成功");
                trashFileListInit();
            }
        });
	})
}

function clearTrash() {
	$.Confirm("清空之后文件将无法恢复 确定要清空回收站吗？", function() {
		$.ajax({
            type : "DELETE",
            url: host + "/ufm/api/v2/trash/" + curUserId,
			error: handleError,
			success: function(data) {
                $.Tost("清空回收站成功");
                trashFileListInit();
				$("#trashFileList").children().remove();
				$('.inbox-catalong-blank').css('display', 'block');
				$('.notfind').show();
			}
		});
	});
}