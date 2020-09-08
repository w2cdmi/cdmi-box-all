var currentPageName = "index";
var num = -1
$(document).ready(function(){
	init();
	// 最近浏览
	listFolderForRecent();
	// 快捷目录
	listShortcutFolder();
	// listOfficialTeamSpace();
    // newFolderDialog = newFolderDialogForIndexPage;
});

function init() {
	var $historyFile = $("#historyFile");
	// var $shortcutDirectory = $("#shortcutDirectory");

	//增加滑动浏览
	// $shortcutDirectory.addTouchScrollAction();
    //
	// //增加删除的小图标
	// addDeleteBadgeToShortcutDirectory($shortcutDirectory);

}

function listFolderForRecent() {
	$.ajax({
		type: "POST",
		data: JSON.stringify({thumbnail: [{ width: 96, height: 96 }, { width: 250, height: 200 }]}),
		url: host + "/ufm/api/v2/folders/"+ownerId+"/recent",
		error: function (xhr, status, error) {
		},
		success: function (data) {
            var $template = $("#index_recent");
            $("#fileList").children().remove()
			if(data.files.length == 0){
                showNotRecent()
			}else{

                for ( var i = 0;i<3;i++) {
                    var item = data.files[i];
                    if(isImg(item.name)){
                        num++
                        var index = item.thumbnailUrlList[0].thumbnailUrl.lastIndexOf("/");
                        var imgSrc = item.thumbnailUrlList[0].thumbnailUrl.substring(0,index)
                        item.imgSrc = imgSrc
                        item.num = num
                    }
                    item.modifiedAt = getFormatDate(new Date(item.modifiedAt), "yyyy-MM-dd");
                    item.size=formatFileSize(item.size)
                    item.imgClass = getImgHtml(item.type, item.name);
                    $template.template(item).appendTo($("#fileList"));
                    $('.weui-cell_swiped').swipeout()
                    //设置数据
                    var $row = $("#opperation_" + item.id);
                    var $allRow = $("#opperations_" + item.id);
                    $row.data("node", item);
                    $allRow.data("node", item);
                    //
                    $row.on('click', showActionSheet);
                }
                num = -1
			}
		},complete:function(){
			$('.load').css('display','none');
		}
	});
}

function listShortcutFolder() {
	$.ajax({
		type: "POST",
		data: '{}',
		url: host + "/ufm/api/v2/folders/"+ownerId+"/shortcut/list",
		error: function (xhr, status, error) {

		},
		success: function (data) {
			var data = data.reverse()
            var $template = $("#index_short");
            $("#shortcut_list").children().remove()
			if(data.length == 0){
                showNotQulickFolder()
			}else{
                for ( var i in data) {
                    var item = data[i];
                    // item.modifiedAt = getFormatDate(new Date(item.modifiedAt), "yyyy-MM-dd");
					item.imgClass=getImgHtml(0,item.nodeName)
					if(item.type==1){
						item.ownerName="个人文件"
					}
                    $template.template(item).appendTo($("#shortcut_list"));
                    $('.weui-cell_swiped').swipeout()
                    //设置数据
                    var $row = $("#short_" + item.id);
                    var $oneRow = $("#shorts_" + item.id);
                    var $deleteRow = $("#deleteShort_" + item.id);
                    $row.data("node", item);
                    $oneRow.data("node", item);
                    $deleteRow.data("node", item)
                    $row.on('click', shortSheet);
                    $deleteRow.on('click',function (e) {
                        var node = $(e.currentTarget).data("node")
                        deleteSortcutFolder(node)
                    })
                }
			}
		}
	});
}


function deleteRecent(node){
	$.ajax({
		type: "DELETE",
		url: host + "/ufm/api/v2/folders/" + node.ownedBy + "/recent/delete/" + node.id ,
		error: handleError,
		success: function (data) {
            $.toast('删除成功');
            listFolderForRecent()
		}
	});
}
// 删除快捷目录
function deleteSortcutFolder(node) {
    $.confirm("确认移除快捷目录?", function() {
        $.ajax({
            type: "DELETE",
            url:  host + "/ufm/api/v2/folders/" + node.ownerId + "/shortcut/" + node.id,
            error: function (xhr, status, error) {
                //$.toast('操作失败', 'forbidden');
            },
            success: function (data) {
                $.toast('移除成功');
                listShortcutFolder()
            }
        });
    })
}

function downloadFileByNodeId(nodeId) {
	$.ajax({
		type: "GET",
		async: false,
		url: ctx + "/files/getDownloadUrl/" + ownerId + "/" + nodeId + "?" + Math.random(),
		error: handleError,
		success: function (data) {
			$("#downloadFile").attr("href",data);
			document.getElementById("downloadFile").click();
		}
	});
}

function downloadFileByNodeIdAndOwnerId(nodeId,ownerBy,div) {
	var name = $(div).attr("fileName")
	var previewable = isFilePreviewable(name);
    var imgable = isImg(name);
    var videoable = isVideo(name);
    var pla=ismobile(1);
	if(previewable){
		gotoPage(ctx+'/files/gotoPreview/'+ownerBy+'/'+nodeId)
	}else if(imgable){
        imgClick(div)
    }else if(videoable && pla=="1"){
        videoPreview(ownerBy,nodeId,videoable)
	}else{
		$.ajax({
			type: "GET",
			async: false,
			url: ctx + "/files/history/getDownloadUrl/" + ownerBy + "/" + nodeId + "?" + Math.random(),
			error: function(xhr, status, error){
				switch(xhr.responseText)
				{
					case "NoSuchFile":
						 $.confirm("文件已经不存在,是否删除记录?", "确认删除?", function() {
							  $.ajax({
								type: "DELETE",
								url: host + "/ufm/api/v2/folders/" + ownerBy + "/recent/delete/" +nodeId ,
								error: function(xhr, status, error){
									console.log(data);
								},
								success: function (data) {
									gotoPage(ctx+"/");
								}
							});
						 })
						break;
					default:
						$.toast('操作失败', 'forbidden');
						break;
				}
			},
			success: function (data) {
				$("#downloadFile").attr("href",data);
				document.getElementById("downloadFile").click();
			}
		});
    }
}

function getNodePermission(parentId,ownerBy) {
	var currOwnerId = ownerId;
	if(typeof(ownerBy)!="undefined" && ownerBy !=""){
		currOwnerId = ownerBy;
	}
	var permission = null;
	var url = host + "/ufm/api/v2/permissions/"+ currOwnerId +"/" + parentId + "/" +curUserId;
	$.ajax({
		type: "GET",
		url: url,
        beforeSend: function(xhr) {
            xhr.setRequestHeader("Authorization", userToken);
            xhr.setRequestHeader("Content-Type","application/json");
        },
		async: false,
		error: function (xhr, status, error) {
		},
		success: function (data) {
			permission = data.permissions;
		}
	});
	return permission;
}

// 显示暂无最近浏览
function showNotRecent() {
	var html=""
	html += "<i class='index-not-recent'>"
	html += "<img src='"+ctx+"/static/skins/default/img/not-recent1.png'/>"
	html += "</i>"
	html = html+ "<p class='index-not-recent-title'>暂无最近浏览文件</p>"
	$("#fileList").prepend(html)
}
// 显示暂无快捷目录
function showNotQulickFolder() {
    var html=""
    html += "<i class='index-not-qulick'>"
    html += "<img src='"+ctx+"/static/skins/default/img/not-qulick-folder1.png'/>"
    html += "</i>"
    html = html+ "<p class='index-not-qulick-title index-not-qulick-title-first'>暂无快捷文件夹，设置方法：</p>"
    html = html+ "<p class='index-not-qulick-title index-not-qulick-title-last'>点击文件夹┇选择[设置为快捷文件夹]即可</p>"
    $("#shortcut_list").prepend(html)
}


function QulickFolderenter(type,nodeId,ownerId) {
    var nodePermission = getNodePermission(nodeId,ownerId);
    if (nodePermission["browse"] != 1) {
        $.alert("您没有访问该文件夹的权限!");
        return;
    }
	if(type==1){
        gotoPage(ctx+'/folder?rootNode=' + nodeId)
	}else{
        gotoPage(ctx+'/teamspace/file/'+ ownerId + '?parentId=' + nodeId)
	}
}
$(function(){

});
