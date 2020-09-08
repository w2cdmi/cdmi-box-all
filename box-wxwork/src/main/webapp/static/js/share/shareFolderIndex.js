var catalogData = null;
var keyword = null;
var files;
var flag = "0";
var url = "";
var curTotalPage = 1;
var curNodeId;
var nodeId
var currentPage = "shareFolderIndex";
$(function() {
    sortShowHide()
    changeBreadcrumb(parentId)
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
	createSimpleBreadcrumb = createShareFolderBreadcrumb;

	
	// jumpFolder = jumpFolderForShare;
	var urlParameter = parseQueryString()
	nodeId = urlParameter.nodeId
	// $("#jumpFolders").text(jumpName)
    clickJump(nodeId)
    var fileList = $("#fileListWrapper").FileList({
        ownerId:ownerId,
        categoryOrigin:-1
    })
    fileList.showListInit()
});

function clickJump(nodeId) {
    $("#jumpFolders").click(function () {
        jumpFolderForShare($(this),nodeId)
    })
}

/* 从搜索结果中跳转时， 查询所有的父目录，构造面包屑导航 */
function changeBreadcrumb(fileId) {
    var breadcrumbItem = "";
    var url = host+"/ufm/api/v2/nodes/"+ownerId+"/"+fileId+"/path";
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
                    breadcrumbItem =" <p class='bread-arrow-right'></p><div id='jump_"+ data[i].id +"' data-info='"+ JSON.stringify(data[i]) +"' onclick=\"jumpFolder(this,"+ data[i].id +");\">"+data[i].name+"&nbsp;</div>";
                    $directory.append(breadcrumbItem);
                }
            }
        },
        error: function(xhr, status, error){
            $.toast('获取目录失败', 'forbidden');
        }
    });
}

function viewImg(nodeIdTmp, fileName) {
	var inodeId = nodeIdTmp;
	var flag;
	if (inodeId == undefined) {
		var node = $("#fileList").getGridSelectedData(catalogData,
				opts_viewGrid);
		inodeId = node[0].id;
		fileName = node[0].name;
	}
	curNodeId = inodeId;
	cutOwnerId = ownerId;
	$.ajax({
		type : "GET",
		async : false,
		url : ctx + "/views/getViewFlag/" + ownerId + "/" + inodeId + "?"
				+ Math.random(),
		success : function(data) {
			var data = $.parseJSON(data);
			flag = data.viewFlag;
			if (data.isSizeLarge) {
				ymPrompt.alert({
					title : fileName,
					message : "<spring:message code='preview.isSizeLarge'/>",
				});
				return;
			}
			url = ctx + '/views/viewInfo/' + ownerId + '/' + inodeId + '/'
					+ flag;
			if (parseInt(flag) != 2) {
				ymPrompt.alert({
					title : fileName,
					message : "<spring:message code='preview.getPageView'/>",
				});
			}
		}
	});

	if (parseInt(flag) == 2) {

		$.ajax({
			type : "GET",
			async : false,
			url : ctx + "/views/viewMetaInfo/" + ownerId + "/" + inodeId + "/"
					+ "1",
			error : function(request) {

			},
			success : function(data) {

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
	if (viewMode == "file") {
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
				type : "GET",
				url : host + "/ufm/api/v2/files/"+ownerId+"/"+nodeId+"/versions?offset=0&limit=10",
				cache : false,
				async : false,
				success : function(data) {
					var fileList = data.versions;
					if (fileList.length > 0) {
						var html = "";
						$("#fileVersionList").html(html);
						for (var i = 0; i < fileList.length; i++) {
							html += "<li id='versionFile_"
									+ fileList[i].id
									+ "'><i><div class='version-icon'></div>"
									+ parseInt(fileList.length - i)
									+ "</i>"
									+ "<span>"
									+ getFormatDate(new Date(
											fileList[i].modifiedAt))
									+ "</span><p></p>"
									+ "<h1>"
									+ formatFileSize(fileList[i].size)
									+ "</h1><h3 onclick=\"downloadFileByNodeId('"
									+ fileList[i].id + "')\">下载</h3>";
							if (i != 0) {
								html += "<h4></h4><h3 onclick=\"deleteFileByNodeId('"
										+ fileList[i].id
										+ "','"
										+ nodeId
										+ "')\">删除</h3><h4></h4><h3 onclick=\"restoreVersion('"
										+ fileList[i].id
										+ "','"
										+ nodeId
										+ "')\">恢复</h3>"
							}
							html += "</li>";

						}
						$("#fileVersionList").html(html);
					}
				},
				error : function() {
					$.toast('获取版本文件失败', 'forbidden');
				}
			});
}
// topNodeId 这个文件最高版本的nodeId
function deleteFileByNodeId(nodeId, topNodeId) {
	$.ajax({
		type : "POST",
		url : ctx + "/nodes/delete",
		data : {
			'ownerId' : ownerId,
			'ids' : nodeId,
			'token' : token
		},
		error : function(data) {
			var status = data.status;
			if (status == 403) {
				$.toast("您没有权限进行该操作", "forbidden");
			} else {
				$.toast("操作失败，请重试", "forbidden");
			}
		},
		success : function(data) {
			fillVersionFileList(topNodeId);
		}
	});
}
// topNodeId 这个文件最高版本的nodeId
function restoreVersion(nodeId, topNodeId) {
	$.ajax({
		type : "POST",
		url : ctx + "/files/restoreVersion",
		data : {
			'ownerId' : ownerId,
			'nodeId' : nodeId,
			'token' : token
		},
		error : function(data) {
			var status = data.status;
			if (status == 403) {
				$.toast("您没有权限进行该操作", "forbidden");
			} else {
				$.toast("操作失败，请重试", "forbidden");
			}
		},
		success : function(data) {
			fillVersionFileList(topNodeId);
		}
	});
}

function shareHandle(tp) {
	if (viewMode == "file") {
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
		"secretLevel" : secretLevel
	};
	$.ajax({
		type : "POST",
		url : url,
		data : params,
		error : function(data) {
		},
		success : function(data) {
			permission = data;
		}
	});
}

function setLink(node) {
	var iNodeId = node.id;
	var ownerId = node.ownedBy;
	var defaultlinKset = {
		accessCodeMode : "static",
		accessCode : "",
		download : false,
		preview : false,
		upload : true,
		identities : "",
		token : token,
	}
	$.ajax({
		type : "POST",
		url : ctx + "/share/setlink/" + ownerId + "/" + iNodeId,
		data : defaultlinKset,
		error : function(request) {
			$.toast('操作失败', 'forbidden');
		},
		success : function(data) {
			$.toast("操作成功");
		}
	});
}


function createShareFolderBreadcrumb (catalogParentId, ownerId) {
    if(shareRootId == catalogParentId) {
        return $("#directory").children().first().html();
    }

    var breadcrumbItem = "";
    var params = {
        "rootId": shareRootId
    };
    // var url = ctx + "/share/getPaths";
    var url = host + "/ufm/api/v2/nodes/" + ownerId + "/" + catalogParentId + "/path";
    $.ajax({
        type: "GET",
        url: url,
        cache: false,
        async: false,
        data: params,
        timeout: 180000,
        success: function (data) {
            if (data.length > 0) {
                for (var i = 0; i < data.length - 1; i++) {
                    breadcrumbItem += data[i].name + "&nbsp;>&nbsp;";
                }
                breadcrumbItem += data[data.length - 1].name;
            }},
        error: function(xhr, status, error){
            $.toast('获取目录失败', 'forbidden');
        }
    });
    return breadcrumbItem;
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
        success: function (data) {
            var breadcrumbItem = "<div onclick=\"gotoPage('"+ctx+"/shared')\">收到的共享&nbsp;</div>";
            $("#directory").find("> div").remove();
            
            for (var i = data.length - 1; i >= 0; i--) {
            	breadcrumbItem +=" <div onclick=\"jumpFolder(this,"+ data[i].id +");\">"+data[i].name+"&nbsp;</div>";
            }
            $("#directory").append(breadcrumbItem);
        }
    });
}


function save2PersonalFile(node){
    getFileFolderInfo(node)
	/* 在当前空间内移动，所以传入当前使用的ownerId*/
    var folderChooser = $("#shareToFolderChooserDialog").FolderChooser({
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

function asyncListen(srcOwnerId, selectFolder, taskId) {
    $.ajax({
        type: "GET",
        url: ctx+"/nodes/listen?taskId=" + taskId + "&" + new Date().toString(),
        error: function (xhr, status, error) {
        	  $.toast("转存失败,文件可能已删除!", "cancel", function(toast) {
                  console.log(toast);
                });
        },
        success: function (data, textStatus, jqXHR) {
        	
            switch (data) {
                case "NotFound":
                	  $.toast("转存成功", function() {
                          console.log('close');
                        });
                    break;
                case "Doing":
                    setTimeout(function () {
                        asyncListen(srcOwnerId, selectFolder, taskId);
                    }, 1500);
                    break;
                case "SubFolderConflict":
                    break;
                case "Forbidden":
                	 $.toast("权限不足!","forbidden");
                    break;
                case "NoSuchSource":
                case "NoSuchDest":
                	$.toast("目标文件不存在!","forbidden");
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

function save2PersonalFileOnScroll(id){
	var node = $("#file_"+id).data("node");
	save2PersonalFile(node);
}

// function jumpFolderForShare(th, folderId) {
// 	if($("#folderChooser").css("display")=="none"){
// 		$(th).nextAll(["div"]).remove();
// 		parentId = folderId;
// 		listFile(folderId, 1);
// 	}else{
// 		return;
// 	}
// }

function openFolder(node) {
    $('.load').css('display','block');
    parentId = node.id;
    var nodePermission = getNodePermission(parentId,ownerId);
    if (nodePermission["browse"] != 1) {
        $.alert("您没有访问该文件夹的权限!");
        return;
    }
    gotoPage(ctx + "/shared/list/" + node.ownedBy +"/" + parentId+"?nodeId="+parentId);
}