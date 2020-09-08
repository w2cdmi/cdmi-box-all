/*显示相关定义*/
var viewType = 2; // 视图模式

var keyword = null;
var noPermission = [];
var currentPage = "spaceDetail";
var pageNumber = 1;
var pageSize = 1000;

$(function() {
	init();

	listFile();
	changeBreadcrumb();
	//退出查看版本
	$(".version-info-tail").click(function() {
		$(".version-info").hide();
		$("#fileVersionList").html("");
	});
	//重写创建面包屑方法
	createSimpleBreadcrumb = createSimpleBreadcrumbForTeamspace;
});

function switchViewType(type) {
	for(var i = 0; i < noPermission.length; i++) {
		if(type == "list") {
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
	var $target = $(e.target);
	var node = $target.data("node");
	var actions = [];

	actions.push({
		text: "另存为到其他空间",
		className: "color-primary",
		onClick: function() {
			showCopyToMeDialog(node);
		}
	});

	actions.push({
		text: "删除",
		className: "color-primary",
		onClick: function() {
			deleteFile(node);
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

/*从搜索结果中跳转时， 查询所有的父目录，构造面包屑导航 */
function changeBreadcrumb() {
	var breadcrumbItem = "";
	var url = ctx + "/teamspace/file/getPaths/" + ownerId + "/" + parentId;
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
					breadcrumbItem = " <div onclick=\"jumpFolder(this," + data[i].id + ");\">" + data[i].name + "&nbsp;</div>";
					$directory.append(breadcrumbItem);
				}
			}
		},
		error: function() {
			$.toast('获取目录失败', 'forbidden');
		}
	});
}

function showCopyToMeDialog(node) {
	//todo: 使用新的folderChooser
	var folderChooser = new FolderChooser();
	folderChooser.show(function(folderId) {
		var params = {
			"destOwnerId": curUserId,
			"ids": node.id,
			"parentId": folderId,
			"startPoint": "teamspace",
			"endPoint": "operative",
			"token": token
		};
		$.ajax({
			type: "POST",
			url: ctx + "/nodes/copy/" + node.ownedBy,
			data: params,
			error: function() {
				$.toast("转存失败");
			},
			success: function(data) {
				$.toast("操作成功");
			}
		});
	});
}

function saveToPersonal(nodeId) {
	var node = $("#file_" + nodeId).data("node");
	showCopyToMeDialog(node);
}

function optionVersionFile(node) {
	$(".version-info").show();

	$("#versionFileName").html(node.name);
	$("#versionFileImage").addClass(getImgHtml(node.type, node.name));

	fillVersionFileList(node.id);
}

function fillVersionFileList(nodeId) {
	$.ajax({
		type: "POST",
        url: host + "/ufm/api/v2/files/" + ownerId + "/" + nodeId + "/versions?offset=" + ((pageNumber - 1) * pageSize) + "&limit=" + pageSize,
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
					html += "<li id='versionFile_" + fileList[i].id + "'><i><div class='version-icon'></div>" + parseInt(fileList.length - i) + "</i>" +
						"<span>" + getFormatDate(new Date(fileList[i].modifiedAt)) + "</span><p></p>" +
						"<h1>" + formatFileSize(fileList[i].size) + "</h1><h3 onclick=\"downloadFileByNodeId('" + fileList[i].ownedBy + "', '" + fileList[i].id + "')\">下载</h3>";
					if(i != 0) {
						html += "<h4></h4><h3 onclick=\"deleteFileByNodeId('" + fileList[i].id + "','" + nodeId + "')\">删除</h3><h4></h4><h3 onclick=\"restoreVersion('" + fileList[i].id + "','" + nodeId + "')\">恢复</h3>"
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

//返回简单目录
function createSimpleBreadcrumbForTeamspace(catalogParentId, ownerId) {
	var breadcrumbItem = $("#directory").children().first().html();
	var url = ctx + "/teamspace/file/getPaths/" + ownerId + "/" + catalogParentId;
	$.ajax({
		type: "GET",
		url: url,
		cache: false,
		async: false,
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