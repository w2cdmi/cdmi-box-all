/*分页相关定义*/
var pageSize = getStorage("fileListPageSize", 40);
/*排序字段，["modifiedAt", "name", "size"]*/
var orderField = "name";
/*是否倒序*/
var isDesc = getStorage("isDesc", "true");
/*文件列表显示方式：列表或缩略图*/
var listViewType = getStorage("listViewType", "list");
/* 正在加载 */
var __loading = false;

var __page = 1;
var __loadmore = false;

/*显示相关定义*/
var viewType = 2; // 视图模式

var keyword = null;
var noPermission = [];
var ownedBys;
var ids;
// var token = "<c:out value='${token}'/>";
var types;
var fileName = GetQueryString("fileName");
var pagination
if(types == 0) {
	$('.share-homepage-bottom-size div').hide();
} else {
	$('.share-homepage-bottom-size div').show();
}

var roleMsgs = {
	"auther": "<spring:message code='systemRole.title.auther'/>",
	"editor": "<spring:message code='systemRole.title.editor'/>",
	"uploadAndView": "<spring:message code='systemRole.title.uploadAndView'/>",
	"viewer": "<spring:message code='systemRole.title.viewer'/>",
	"uploader": "<spring:message code='systemRole.title.uploader'/>",
	"downloader": "<spring:message code='systemRole.title.downloader'/>",
	"previewer": "<spring:message code='systemRole.title.previewer'/>",
	"lister": "<spring:message code='systemRole.title.lister'/>",
	"prohibitVisitors": "<spring:message code='systemRole.title.prohibitVisitors'/>"
};

function GetQueryString(name) {
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
	var r = window.location.search.substr(1).match(reg);
	if(r != null) return decodeURI(r[2]);
	return null;
}
var dialog = $('#infoDialog').dialog({
	title: "文件详情"
})
dialog.init();
$(function() {
	$("#orderField_modifiedAt").find('i').css('visibility','visible');
	pagination = $('#pagination').Pagination()
	pagination.init()
	pagination.onPageChange = function(pageNumber) {
        __page = pageNumber
		listLink()
	}
	init();
	listLink();
	$("#table_popover dt").mousedown(function() {
		ids = selectedRow.attr("ids");
		ownedBys = selectedRow.attr("ownedBys");
		types = selectedRow.attr("types");
		var names = selectedRow.attr("names");
		if($(this).attr("command") == "1") {
			optionInode(ownedBys,ids);
		}
		if($(this).attr("command") == "2") {
			dialog.show();
		}
		if($(this).attr("command") == "3") {
			cancelAllLinksOfFile(ids, ownedBys)
			listLink();
		}
		/***********************查看详情****************************/
	    $('#fileIco').addClass('ico-folder').css('float','left')
		$('#fileName').html(names);
		$("#fileIcon").removeClass().addClass('folder-icon');

		$.ajax({
			type: "GET",
            url: host + '/ufm/api/v2/folders/' + ownedBys + "/" + ids,
			error: function(request) {
				//$.toast('获取文件外链失败', 'forbidden');
			},
			success: function(data) {
				if(data != "") {
					createLinkItem(data);
				}
			}
		});

		$.ajax({
			type: "POST",
            url: host + '/ufm/api/v2/shareships/' + ownedBys + '/' + ids + '?offset=' + ((__page - 1) * pageSize) + '&limit=' + pageSize,
			error: function(request) {
				//$.toast('获取文件共享失败', 'forbidden');
			},
			success: function(data) {
				if(data.content.length > 0) {
					createShareItem(data.content);
				}
			}
		});
		/***********************查看详情****************************/
	})
});

function overlay() {
	var e1 = document.getElementById('modal-overlay');
	e1.style.visibility = (e1.style.visibility == "visible") ? "hidden" : "visible";
}

/*
 此方法为文件列表的初始化方法，各个界面应该调用此方法。
 */
function init() {
	//排序字段
	var $nameSort = $("#orderField_name");
	if(isDesc == null || isDesc == 'true') {
		$nameSort.find('i').addClass("fa fa-long-arrow-down");
	} else {
		$nameSort.find('i').addClass("fa fa-long-arrow-up");
	}

	$nameSort.on("click", function() {
		var $this = $(this);
		// var ico = $(this).find('i');
		// ico.css('visibility','visible');
		if($this.hasClass("fa fa-long-arrow-down")) {
			$this.removeClass("fa fa-long-arrow-down").addClass("fa fa-long-arrow-up");
			$this.siblings().removeClass();
			orderField = "name";
			isDesc = "false";
		} else {
			$this.removeClass("fa fa-long-arrow-up").addClass("fa fa-long-arrow-down");
			$this.siblings().removeClass();
			orderField = "name";
			isDesc = "true";
		}

		setStorage("isDesc", isDesc);

		listLink(parentId, 1);
	});
	$('#sort_button').popover($("#sort_popover"), true, "left", function(t) {});
	//文件列表显示方式
	if(listViewType == "list") {
		$("#viewTypeBtnList").addClass("active");
	} else {
		$("#viewTypeBtnThumbnail").addClass("active");
	}
}
var selectedRow = null;
function worker_Click(e) {
	e.stopPropagation()
}
function listLink(folderId, page) {
	// __page = page || 1;
	parentId = folderId || 0;
	var url = host + "/ufm/api/v2/links/items";
    var params = {
        limit: pageSize,
        offset: (__page - 1) * pageSize,
        thumbnail: [{width: 96, height: 96}]
    };
	$.ajax({
		type: "POST",
		url: url,
		data: JSON.stringify(params),
		error: handleError,
		success: function(data) {
            var fileList = data.folders.concat(data.files);
			__loadmore = data.totalCount > (__page - 1) * pageSize;
            pagination.setTotalSize(data.totalCount)
            pagination.setCurrentPage(__page)
            pagination.setTotalPages(Math.ceil(data.totalCount/pageSize))

			var $list = $("#linkList");
			var $template = $("#linkTemplate");
            if (fileList.length == 0) {
                $('.notfind').show();
				$('#linkListWrapper').css('background', 'url(' + ctx + '/static/skins/default/img/iconblack_17.png) no-repeat center center');
				$('#linkListWrapper').css('background-size', '5rem 5rem')
			} else {
                $('.notfind').hide();
				$('#linkListWrapper').css('background', '');
			}
			//加载第一页，清除以前的记录
			$list.empty();
			for(var i in fileList) {
				var item = fileList[i];
				if(item.type == 1) {
					item.size = formatFileSize(item.size);
					if(typeof(item.thumbnailUrlList) != "undefined" && item.thumbnailUrlList.length > 0) {
						item.imgPath = item.thumbnailUrlList[1].thumbnailUrl;
					}
				} else {
					item.size = "";
				}
				item.modifiedAt = getFormatDate(new Date(item.modifiedAt), "yyyy/MM/dd");
				item.divClass = getImgHtml(item.type, item.name);
				item.iconimg = getFileIconClass(item.name);
				if(item.type === 0) {
					item.iconimg = 'ico-folder';
				}
				$template.template(item).appendTo($list).data("node", item);
				$('#linkList').find('a#worker').on('click', worker_Click)

				//增加长按事件
				var $row = $("#link_" + item.id);
				$row.data("node", item);
				// new Hammer($row[0]).on('press', onPress);
			}
			$("#linkList tr").mouseenter(function() {
				// selectedRow = null;
				selectedRow = $(this);
				var types = selectedRow.attr("types");
				if(types == 0) {
					// $('#check').hide();
					$('#gotoinner').show();
				} else {
					$('#gotoinner').hide();
					$('#check').show();
				}
			})
			$("table tbody td a[id='worker']").each(function() {
				$(this).popover($("#table_popover"), true, "right", function(t) {});
			})
		},
		complete: function() {
			$('.load').css('display', 'none');
		}
	});
}

/* 列表中的对象操作 */
function optionInode(ownerId, folderId, th) {
	var fileName = $(th).attr("names");
	var type = $(th).attr("types");
    // var preview = isPreviewable(fileName)
    if (type == 1) {
        gotoPageOpen(ctx + '/files/gotoPreview/' + ownerId + '/' + folderId);
	}else{
        gotoPage(ctx + '/folder?rootNode=' + folderId);
	}

}

// function showLinkDialog(e) {
// 	var node = $(e).parent().parent().data("node");
// 	gotoPage(ctx + "/share/link/" + node.ownedBy  + "/" + node.id );
// }

// function showFileProperties(e) {
// 	var node = $(e).parent().data("node");
// 	gotoPage(ctx + '/files/gotoFileInfo/' + node.ownedBy + "/" + node.id + "?type=" + node.type + "&fileName=" + node.name);
// }
function cancelAllLinksOfFile(objectId, ownerId) {
	$.ajax({
        type: "DELETE",
        url: host + "/ufm/api/v2/links/" + ownerId + "/" + objectId,
		error: handleError,
		success: function(data) {
			$.Confirm("是否取消外发？", function() {
				
				$.Tost("取消成功", function() {
					listLink(1);
				}).show().autoHide(2000);
			});
			
			
		}
	});
}

/***********************查看详情****************************/
function fillFileInfo(file) {
	$("#fileIcon").removeClass().addClass(getImgHtml(1, file.name));
	$("#fileName").text(file.name);
	$("#fileSize").text(formatFileSize(file.size));
}

function createShareItem(content) {
	$("#info-shareList").html('');
	for(var i = 0; i < content.length; i++) {
		var item = content[i];
		var html = "<div 'class=cl' style='overflow: hidden;'>";
		html += "<div class='user-img'>";

		html += "<i class='ico-folder'><img src=" + ctx + '/userimage/getUserImage/' + item.sharedUserId + '"/></i>';
		html += "</div>";
		html += "<div class='user-name'>" + item.sharedUserName + "</div>";
		html += "<div class='user-role'>" + getAclByRole(item.roleName) + "</div>";
		html += "</div>";
		$("#info-shareList").append(html).show();
	}
}

function createLinkItem(data) {
	$("#info-linkList").html('');

	for(var i = 0; i < data.length; i++) {
		var item = data[i];

		var html = '<div>'
		html += '<p>' + data[i].url + '</p>'
		html += '<p><span>' + translateAccessMode(item) + '</span> | <span>' + translateRole(item) + '</span> | <span>' + translateExpireDate(item) + '</span></p>'
		html += '</div>';
		$("#info-linkList").append(html).show();
	}
}

function translateRole(item) {
	console.log(item);
	var role = "";
	if(item.download == true) {
		role = role + "下载";
	}
	if(item.preview == true) {
		if(role == "") {
			role = role + "预览";
		} else {
			role = role + ",预览";
		}

	}

	return role;
}

function translateAccessMode(item) {
	if(item.accessCodeMode == "static") {
		if(item.plainAccessCode != undefined && item.plainAccessCode != "") {
			return "提取码访问";
		} else {
			return "匿名访问";
		}
	} else {
		return "动态码访问";
	}
}

function translateExpireDate(item) {
	if(item.expireAt != undefined && item.expireAt != "") {
		var days = Math.floor((item.expireAt - item.effectiveAt) / (24 * 3600 * 1000));
		if(days > 365) {
			return(days / 365) + "年后过期"
		} else if(days >= 30) {
			return(days / 30) + "个月后过期"
		} else {
			return days + "天后过期";
		}
	} else {
		return "永久有效";
	}
}