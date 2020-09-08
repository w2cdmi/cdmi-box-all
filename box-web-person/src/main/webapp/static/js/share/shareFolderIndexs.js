var catalogData = null;
var keyword = null;
var files;
var flag = "0";
var url = "";
var curTotalPage = 1;
var curNodeId;
var currentPage = "shareFolderIndex";
$(function() {
	createBreadcrumb();
	shareFolderInit();
	shareListFile();

	// 推出查看版本
	$(".version-info-tail").click(function() {
		$(".version-info").hide();
		$("#fileVersionList").html("");
	});

	$('.version-info').click(function() {
		$('.version-info').css('display', 'none');
		$('.version-info-content').click(function(e) {
			e.stopPropagation();
		});
	});
	//重写显示文件路径
	createSimpleBreadcrumb = createSimpleShareBreadcrumb;
	//重写文件刷新方法
	listFile = shareListFile;

	jumpFolder = jumpFolderForShare;
});

function shareFolderInit() {
	//排序字段
	var $dateSort = $("#dateSort");
	var $nameSort = $("#nameSort");
	if(orderField == null || orderField == 'modifiedAt') {
		if(isDesc == null || isDesc == 'true') {
			$dateSort.addClass("sort-desc");
		} else {
			$dateSort.addClass("sort-asc");
		}
		orderField = "modifiedAt";
	} else {
		if(isDesc == null || isDesc == 'true') {
			$nameSort.addClass("sort-desc");
		} else {
			$nameSort.addClass("sort-asc");
		}
	}

	$dateSort.on("click", function() {
		$("#nameSort").removeClass();
		var $this = $(this);
		if($this.hasClass("sort-desc")) {
			$this.removeClass("sort-desc").addClass("sort-asc");
			isDesc = "false";
		} else {
			$this.removeClass("sort-asc").addClass("sort-desc");
			isDesc = "true";
		}
		setCookie("isDesc", isDesc);

		orderField = "modifiedAt";
		setCookie("orderField", "modifiedAt");

		shareListFile(parentId, 1);
	});

	$nameSort.on("click", function() {
		$("#dateSort").removeClass();
		var $this = $(this);
		if($this.hasClass("sort-desc")) {
			$this.removeClass("sort-desc").addClass("sort-asc");
			isDesc = "false";
		} else {
			$this.removeClass("sort-asc").addClass("sort-desc");
			isDesc = "true";
		}

		setCookie("isDesc", isDesc);

		orderField = "name";
		setCookie("orderField", orderField);

		shareListFile(parentId, 1);
	});

	//文件列表显示方式
	if(listViewType == "list") {
		$("#viewTypeBtnList").addClass("active");
	} else {
		$("#viewTypeBtnThumbnail").addClass("active");
	}

	//为面包屑增加滑动效果
	$("#directory").addTouchScrollAction();

	//为搜索对话框绑定事件
	$('#searchFileInput').bind('input propertychange', function(e) {
		var keyword = $("#searchFileInput").val();
		if(keyword !== "" && keyword.trim() != "") {
			doSearch(keyword.trim());
		}
	});

// 	//下拉刷新
// 	var $listWrapper = $("#fileListWrapper");
// 	$listWrapper.pullToRefresh().on("pull-to-refresh", function() {
// 		//console.log("pulltorefresh triggered...");
// 		shareListFile();
// 		setTimeout(function() {
// 			$("#fileListWrapper").pullToRefreshDone();
// 		}, 200);
// 	});

// 	//上滑加载
// 	$listWrapper.infinite().on("infinite", function() {
// 		//console.log("loadmore triggered...");
// 		if(__loading) return;

// 		if(__loadmore) {
// 			__loading = true;
// 			$.showLoading();
// 			shareListFile(parentId, ++__page);
// 			setTimeout(function() {
// 				__loading = false;
// 				$.hideLoading();
// 			}, 200);
// 		}
// 	});
}

function onPress(e) {
	var $target = $(e.target);
	var node = $target.data("node");

	var actions = [];

	actions.push({
		text: "重命名",
		className: "color-primary",
		onClick: function() {
			renameDialog(node);
		}
	});

	actions.push({
		text: "另存为到其他空间",
		className: "color-primary",
		onClick: function() {
			save2PersonalFile(node);
		}
	});

	actions.push({
		text: "删除",
		className: "color-primary",
		onClick: function() {
			deleteFileForShare(node);
		}
	});
	if(node.type == 1) {
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
	$.actions({
		title: "选择操作",
		actions: actions
	});
}

/* 从搜索结果中跳转时， 查询所有的父目录，构造面包屑导航 */
function changeBreadcrumb(fileId) {
	var breadcrumbItem = "";
	var url = "folders/getPaths/" + ownerId + "/" + fileId;
	$.ajax({
		type: "GET",
		url: url,
		cache: false,
		async: false,
		success: function(data) {
			var $directory = $("#directory");
			$directory.find("div:gt(0)").remove();
			if(data.length > 0) {
				for(var i = 0; i < data.length; i++) {
					breadcrumbItem = " <div onclick=\"jumpFolder(this," +
						data[i].id + ");\">" + data[i].name +
						"&nbsp;</div>";
					$directory.append(breadcrumbItem);
				}
			}
		},
		error: function() {
			$.toast('获取目录失败', 'forbidden');
		}
	});
}

function viewImg(nodeIdTmp, fileName) {
	var inodeId = nodeIdTmp;
	var flag;
	if(inodeId == undefined) {
		var node = $("#fileList").getGridSelectedData(catalogData,
			opts_viewGrid);
		inodeId = node[0].id;
		fileName = node[0].name;
	}
	curNodeId = inodeId;
	cutOwnerId = ownerId;
	$.ajax({
		type: "GET",
		async: false,
		url: ctx + "/views/getViewFlag/" + ownerId + "/" + inodeId + "?" +
			Math.random(),
		success: function(data) {
			var data = $.parseJSON(data);
			flag = data.viewFlag;
			if(data.isSizeLarge) {
				ymPrompt.alert({
					title: fileName,
					message: "<spring:message code='preview.isSizeLarge'/>",
				});
				return;
			}
			url = ctx + '/views/viewInfo/' + ownerId + '/' + inodeId + '/' +
				flag;
			if(parseInt(flag) != 2) {
				ymPrompt.alert({
					title: fileName,
					message: "<spring:message code='preview.getPageView'/>",
				});
			}
		}
	});

	if(parseInt(flag) == 2) {

		$.ajax({
			type: "GET",
			async: false,
			url: ctx + "/views/viewMetaInfo/" + ownerId + "/" + inodeId + "/" +
				"1",
			error: function(request) {
				doDownLoadLinkError(request);
			},
			success: function(data) {

				var previewUrl = data.url;
				currentPage = data.curPage;
				curTotalPage = data.totalPage;
				$("#doc_view_current_page").val(data.curPage);
				$("#doc_view_totap_page").html(data.totalPage);
				document.getElementById("doc_ppt_img").src = previewUrl;
				$("#index_layer2").css("display", "block");
				$("#filedoc").css("display", "block");

			}
		});
	}
}

function linkHandle() {
	$("body").css("overflow", "scroll");
	top.ymPrompt.close();
	if(viewMode == "file") {
		shareListFile(currentPage, parentId);
	} else {
		doSearch();
	}
}

function optionVersionFile(node) {
	$(".version-info").show();

	$("#fileName").html(node.name);
	$("#fileImage").addClass(getImgHtml(node.type, node.name));

	fillVersionFileList(node.id);
}

function fillVersionFileList(nodeId) {
	$.ajax({
		type: "POST",
		url: ctx + "/files/listVersion",
		data: {
			token: token,
			ownerId: ownerId,
			nodeId: nodeId,
			pageNumber: 1,
			desc: true
		},
		cache: false,
		async: false,
		success: function(data) {
			var fileList = data.content;
			if(fileList.length > 0) {
				var html = "";
				$("#fileVersionList").html(html);
				for(var i = 0; i < fileList.length; i++) {
					html += "<li id='versionFile_" + fileList[i].id + "'><i><div class='version-icon'></div>" +
						parseInt(fileList.length - i) +
						"</i>" +
						"<span>" +
						getFormatDate(new Date(fileList[i].modifiedAt)) +
						"</span><p></p>" +
						"<h1>" +
						formatFileSize(fileList[i].size) +
						"</h1><h3 onclick=\"downloadFileByNodeId('" + fileList[i].ownedBy + "','" + fileList[i].id + "')\">下载</h3>";
					if(i != 0) {
						html += "<h4></h4><h3 onclick=\"deleteFileByNodeId('" +
							fileList[i].id +
							"','" +
							nodeId +
							"')\">删除</h3><h4></h4><h3 onclick=\"restoreVersion('" +
							fileList[i].id +
							"','" +
							nodeId +
							"')\">恢复</h3>"
					}
					html += "</li>";

				}
				$("#fileVersionList").html(html);
			}
		},
		error: function() {
			$.toast('获取版本文件失败', 'forbidden');
		}
	});
}
// topNodeId 这个文件最高版本的nodeId
function deleteFileByNodeId(nodeId, topNodeId) {
	$.ajax({
		type: "POST",
		url: ctx + "/nodes/delete",
		data: {
			'ownerId': ownerId,
			'ids': nodeId,
			'token': token
		},
		error: function(data) {
			var status = data.status;
			if(status == 403) {
				$.toast("您没有权限进行该操作", "forbidden");
			} else {
				$.toast("操作失败，请重试", "forbidden");
			}
		},
		success: function(data) {
			fillVersionFileList(topNodeId);
		}
	});
}
// topNodeId 这个文件最高版本的nodeId
function restoreVersion(nodeId, topNodeId) {
	$.ajax({
		type: "POST",
		url: ctx + "/files/restoreVersion",
		data: {
			'ownerId': ownerId,
			'nodeId': nodeId,
			'token': token
		},
		error: function(data) {
			var status = data.status;
			if(status == 403) {
				$.toast("您没有权限进行该操作", "forbidden");
			} else {
				$.toast("操作失败，请重试", "forbidden");
			}
		},
		success: function(data) {
			fillVersionFileList(topNodeId);
		}
	});
}

function shareHandle(tp) {
	if(viewMode == "file") {
		shareListFile(currentPage, parentId);
	} else {
		doSearch();
	}
}

function saveSecretLevel() {
	var item = $("#itemData").data("item");
	var url = ctx + "/files/updateSecretLevel/" + item.ownedBy + "/" + item.id;
	var secretLevel = jQuery(
		'input[type="radio"][name="fileSecretLevel"]:checked').val(); // 获取一组radio被选中项的值
	var params = {
		"secretLevel": secretLevel
	};
	$.ajax({
		type: "POST",
		url: url,
		data: params,
		error: function(data) {},
		success: function(data) {
			permission = data;
		}
	});
}

function setLink(node) {
	var iNodeId = node.id;
	var ownerId = node.ownedBy;
	var defaultlinKset = {
		accessCodeMode: "static",
		accessCode: "",
		download: false,
		preview: false,
		upload: true,
		identities: "",
		token: token,
	}
	$.ajax({
		type: "POST",
		url: ctx + "/share/setlink/" + ownerId + "/" + iNodeId,
		data: defaultlinKset,
		error: function(request) {
			alert('操作失败', 'forbidden');
		},
		success: function(data) {
			alert("操作成功");
		}
	});
}

function copyAndMove() {
	var url = "${ctx}/nodes/copyAndMove/" + ownerId +
		"?startPoint=operative&endPoint=operative";
	gotoPage(url);
}

function doCopyAndMove(tp) {
	var idArray = $("#fileList").getGridSelectedId();
	if(tp == 'copy' || tp == 'move') {
		if(isInMigrationFolder && departureOwnerId) {
			top.ymPrompt.getPage().contentWindow.submitCopyAndMove(tp,
				departureOwnerId, idArray);
		} else
			top.ymPrompt.getPage().contentWindow.submitCopyAndMove(tp, ownerId,
				idArray);
	} else if(tp == 'newFolder') {
		top.ymPrompt.getPage().contentWindow.createFolder();
	} else {
		top.ymPrompt.close();
		window.location.reload();
	}
}

function shareListFile(folderId, page) {
	parentId = folderId || parentId;
	__page = page || 1;

	permissionFlag = getNodePermission(parentId);
	if(permissionFlag == null || permissionFlag["browse"] == 0) {
		$.toast("没有权限");
		return;
	}
	if(permissionFlag["upload"] == 0) {
		console.log(123123);
		$("#header").next().css("display", "none");
		$("#header").css("display", "none");
		$("#header").parent().css("height", "4rem");
		$("#fileListWrapper").css("top", "3.58rem");
		$('#upload_buttons').css('display', 'none');
	} else {
		$("#header").next().css("display", "block");
		$("#header").css("display", "block");
		$("#header").parent().css("height", "7.6rem");
		$("#fileListWrapper").css("top", "7.58rem");
	}

	var url = ctx + "/shared/listsub";
	var params = {
		"ownerId": ownerId,
		"parentId": parentId,
		"pageNumber": __page,
		"pageSize": pageSize,
		"orderField": orderField,
		"desc": isDesc,
		"token": token
	};

	$.ajax({
		type: "POST",
		url: url,
		data: params,
		error: handleError,
		success: function(data) {
			var fileList = data.content;
			__page = data.number;
			__loadmore = __page < data.totalPages;
			var $list = $("#fileList");
			var $template = $("#fileTemplate");

			// 加载第一页，清除以前的记录
			if(__page == 1) {
				$list.children().remove();
			}
			if(fileList.length == 0) {
				$list.parent().css("background", "url('" + ctx + "/static/skins/default/img/iconblack_17.png')no-repeat center center");
				$list.parent().css("background-size", "5rem 5rem");
			} else {
				$list.parent().css('background', '');
			}
			for(var i in fileList) {
				var item = fileList[i];
				if(item.type == 1) {
					item.size = formatFileSize(item.size);
				} else {
					item.size = "";
				}
				item.modifiedAt = getFormatDate(new Date(item.modifiedAt),
					"yyyy/MM/dd");
				item.divClass = getImgHtml(item.type, item.name);
				$template.template(item).appendTo($list);

				// 设置数据
				var $row = $("#file_" + item.id);
				$row.data("node", item);

				// 增加长按事件
				new Hammer($row[0]).on('press', onPress);
			}

			// 增加左滑显示按钮效果
			$list.addLineScrollAnimate();
		},
		complete: function() {
			$('.load').css('display', 'none');
		}
	});
}


function createBreadcrumb() {
	var params = {
		"ownerId": ownerId,
		"inodeId": parentId,
		"parentId": shareRootId
	};
	var url = ctx + "/share/getPaths";
	$.ajax({
		type: "GET",
		url: url,
		cache: false,
		async: true,
		data: params,
		timeout: 180000,
		success: function(data) {
			var breadcrumbItem = "<div onclick=\"gotoPage('" + ctx + "/shared')\">收到的共享&nbsp;</div>";
			$("#directory").find("> div").remove();

			for(var i = data.length - 1; i >= 0; i--) {
				breadcrumbItem += " <div onclick=\"jumpFolder(this," + data[i].id + ");\">" + data[i].name + "&nbsp;</div>";
			}
			$("#directory").append(breadcrumbItem);
		}
	});
}

//返回简单目录
function createSimpleShareBreadcrumb(catalogParentId, ownerId) {
	var breadcrumbItem = $("#directory").children().first().html();
	var params = {
		"ownerId": ownerId,
		"inodeId": parentId,
		"parentId": shareRootId
	};
	var url = ctx + "/share/getPaths";
	$.ajax({
		type: "GET",
		url: url,
		cache: false,
		async: false,
		data: params,
		timeout: 180000,
		success: function(data) {
			if(data.length > 0) {
				for(var i = 0; i < data.length; i++) {
					breadcrumbItem += "&nbsp;>&nbsp;" + data[i].name;
				}
			}
		},
		error: function() {
			$.toast('获取目录失败', 'forbidden');
		}
	});
	return breadcrumbItem;
}

function save2PersonalFile(node) {
	/* 在当前空间内移动，所以传入当前使用的ownerId*/
	var folderChooser = new FolderChooser(curOwnerId);
	folderChooser.show(function(folderId) {
		var params = {
			"destOwnerId": curOwnerId,
			"ids": node.id,
			"parentId": folderId,
			"token": token
		};
		$.ajax({
			type: "POST",
			url: ctx + "/nodes/copy/" + ownerId,
			data: params,
			error: function() {
				$.toast("操作失败");
			},
			success: function(data) {
				asyncListen(curOwnerId, folderId, data);
			}
		});
	});
}

function asyncListen(srcOwnerId, selectFolder, taskId) {
	$.ajax({
		type: "GET",
		url: ctx + "/nodes/listen?taskId=" + taskId + "&" + new Date().toString(),
		error: function(XMLHttpRequest) {
			$.toast("转存失败,文件可能已删除!", "cancel", function(toast) {
				console.log(toast);
			});
		},
		success: function(data, textStatus, jqXHR) {

			switch(data) {
				case "NotFound":
					$.toast("转存成功", function() {
						console.log('close');
					});
					break;
				case "Doing":
					setTimeout(function() {
						asyncListen(srcOwnerId, selectFolder, taskId);
					}, 1500);
					break;
				case "SubFolderConflict":
					break;
				case "Forbidden":
					$.toast("权限不足!", "forbidden");
					break;
				case "NoSuchSource":
				case "NoSuchDest":
					$.toast("目标文件不存在!", "forbidden");
					break;
				default:
					$.toast("操作失败!", "cancel", function(toast) {
						console.log(toast);
					});
					break;
			}
		}
	});
}

function save2PersonalFileOnScroll(id) {
	var node = $("#file_" + id).data("node");
	save2PersonalFile(node);
}

function jumpFolderForShare(th, folderId) {
	if($("#folderChooser").css("display") == "none") {
		$(th).nextAll(["div"]).remove();
		parentId = folderId;
		listFile(folderId, 1);
	} else {
		return;
	}
}

function deleteFileForShare(node) {
	$.ajax({
		type: "POST",
		url: ctx + "/nodes/delete",
		data: {
			'ownerId': node.ownedBy,
			'ids': node.id,
			'token': token
		},
		error: function(data) {
			var status = data.status;
			if(status == 403) {
				$.toast("您没有权限进行该操作", "forbidden");
			} else {
				$.toast("操作失败，请重试", "forbidden");
			}
		},
		success: function(data) {
			listFile(parentId, 1);
		}
	});
}