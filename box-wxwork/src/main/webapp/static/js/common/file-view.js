/*分页相关定义*/
var pageSize = getCookie("fileListPageSize", 40);
/*排序字段，["modifiedAt", "name", "size"]*/
var orderField = getCookie("orderField", "modifiedAt");
/*排序方式*/
var order = getCookie("order", "DESC");
/*文件列表显示方式：列表或缩略图*/
var listViewType = getCookie("listViewType", "list");
/* 正在加载 */
var __loading = false;

var __page = 1;
var __loadmore = false;
var num = -1;
/*
  js中使用的parentId在include.jsp中定义。
 */
/*
此方法为文件列表的初始化方法，各个界面应该调用此方法。
 */
function init() {
    //排序字段
    var $nameSort = $("#nameSort");
    var $dateSort = $("#dateSort")
    if (orderField == null || orderField == 'modifiedAt') {
        if (order == null || order == "DESC") {
            $dateSort.find(".all-sort-img i").addClass("sort-desc");
        } else {
            $dateSort.find(".all-sort-img i").addClass("sort-asc");
        }
        orderField = "modifiedAt";
    } else {
        if (order == null || order == "ASC") {
            $nameSort.find(".all-sort-img i").addClass("sort-asc");
        } else {
            $nameSort.find(".all-sort-img i").addClass("sort-desc");
        }
    }

    $dateSort.on("tap", function(e) {
        e.stopPropagation();
        $("#sortRadio").find(".all-sort-img i").removeClass("sort-asc")
        $("#sortRadio").find(".all-sort-img i").removeClass("sort-desc")
            if(order=="DESC"){
                order = "ASC";
                $(this).find(".all-sort-img i").addClass("sort-asc")
            }else{
                order = "DESC";
                $(this).find(".all-sort-img i").addClass("sort-desc")
            }
        setCookie("order", order);
        orderField = "modifiedAt";
        setCookie("orderField", orderField);

        listFile(parentId, 1);
    });
    $nameSort.on("tap", function(e) {
        e.stopPropagation();
        $("#sortRadio").find(".all-sort-img i").removeClass("sort-asc")
        $("#sortRadio").find(".all-sort-img i").removeClass("sort-desc")
        if(order=="DESC"){
            order = "ASC";
            $(this).find(".all-sort-img i").addClass("sort-asc")
        }else{
            order = "DESC";
            $(this).find(".all-sort-img i").addClass("sort-desc")
        }
        setCookie("order", order);
        orderField = "name";
        setCookie("orderField", orderField);
        listFile(parentId, 1);
    });

    //文件列表显示方式
    if (listViewType == "list") {
        $("#viewTypeBtnList").addClass("active");
    } else {
        $("#viewTypeBtnThumbnail").addClass("active");
    }

    //为面包屑增加滑动效果
    $("#directory").addTouchScrollAction();

    //为搜索对话框绑定事件
    $("#searchFileInput").on('keypress',function(e) {
        console.log(e)
        var keycode = e.keyCode;
        var keyword = $("#searchFileInput").val();
        if(keycode=='13' && keyword !== "" && keyword.trim() != "") {
            e.preventDefault();
            //请求搜索接口
            doSearch(keyword.trim());
        }
 });

    //下拉刷新
    var $listWrapper = $("#fileListWrapper");
    $listWrapper.pullToRefresh().on("pull-to-refresh", function() {
        //console.log("pulltorefresh triggered...");
        listFile(parentId,1);
        setTimeout(function() {
            $("#fileListWrapper").pullToRefreshDone();
        }, 200);
    });

    //上滑加载
    $listWrapper.infinite().on("infinite", function() {
        console.log(123);
        if(__loading) return;

        if(__loadmore) {
            __loading = true;
            $.showLoading();
            listFile(parentId, ++__page);
            setTimeout(function() {
                __loading = false;
                $.hideLoading();
            }, 200);
        }
    });
    listFile(parentId, 1);
}

function listFile(folderId, page, success) {
    parentId = folderId || parentId;
    __page = page || 1;
    var permission = getNodePermission(parentId,ownerId);

    var url = host + "/ufm/api/v2/folders/"+ownerId+"/"+parentId+"/items";
    var flieparams={
        limit: pageSize,
        offset: (__page-1)*pageSize,
        order: [{ field: 'type' , direction: "ASC" },{ field: orderField, direction: order }],
        thumbnail: [{ width: 96, height: 96 }]
    }
    if (permission != null && permission["browse"] == 1) {
        $.ajax({
            type: "POST",
            url: url,
            data: JSON.stringify(flieparams),
            error: handleError,
            success: function (data) {
                var fileList = data.folders.concat(data.files);
                __page = page;
                __loadmore = data.totalCount > __page * pageSize;
                var $list = $("#fileList");
                var $template = $("#fileTemplate");

                //加载第一页，清除以前的记录
                if(__page == 1) {
                    //class中带有file-uploading的表示正在上传，刷新时不清除该文件
                    $list.children("div:not([class *= 'file-uploading'])").remove();

                    //表示"文件列表为空的"的div
                    $list.children("div.blank-file-list").remove();
                }

                if (fileList.length === 0 && $list.children().length === 0) {
                    showNotFile()
                } else {
                    $list.parent().css('background', '')
                }
                for (var i in fileList) {
                    var item = fileList[i];
                    if(item.type==1){
                    	item.size = formatFileSize(item.size);
                    	if(typeof(item.thumbnailUrlList)!="undefined" && item.thumbnailUrlList.length>0){
                        	item.imgPath = item.thumbnailUrlList[0].thumbnailUrl;
                        }
                        if(isImg(item.name)){
                            num++
                            var index = item.thumbnailUrlList[0].thumbnailUrl.lastIndexOf("/");
                            var imgSrc = item.thumbnailUrlList[0].thumbnailUrl.substring(0,index)
                            item.imgSrc = imgSrc
                            item.num = num
                        }
                    }else{
                    	item.size = "";
                    }
                    if(permission.download==1){
                    	item.download = 1;
                    }
                    if(item.type != -7){
                        item.swipeClass="weui-cell_swiped"
                    }
                    item.modifiedAt = getFormatDate(new Date(item.modifiedAt), "yyyy-MM-dd");
                    item.divClass = getImgHtml(item.type, item.name, item.isShare, item.isSecret);
                    $template.template(item).appendTo($list);

                    //设置数据
                    var $row = $("#file_" + item.id);
                    var $oneRow = $("#files_" + item.id)
                    $row.data("node", item);
                    $oneRow.data("node", item)
                    //增加长按事件
                   $row.on('click', onPress);
                }
                num = -1;
                $('.weui-cell_swiped').swipeout()
            },complete:function(){
                if(success){
                    success()
                }
            	$('.load').css('display','none');
            }
        });
    } else {
        $.toast("您没有权限进行该操作", "cancel");
    }
}

function openFolder(node) {
	$('.load').css('display','block');
    parentId = node.id;
    var nodePermission = getNodePermission(parentId,ownerId);
    if (nodePermission["browse"] != 1) {
        $.alert("您没有访问该文件夹的权限!");
        return;
    }
    if(curUserId == node.ownedBy){
        gotoPage(ctx+"/folder?rootNode="+parentId)
    }else {
        gotoPage(ctx + "/teamspace/file/" + node.ownedBy +"?parentId=" + parentId);
    }
    // gotoPage(ctx + "/shared/list/" + node.ownedBy +"/" + parentId+"?name="+node.name+"&nodeId="+parentId);
        // gotoPage(ctx+"/folder?rootNode="+parentId,function () {
        //     $("#fileList").children().remove();
        //     $("#directory").append("<p class='bread-arrow-right'></p><div onclick=\"jumpFolder(this," + node.id + ");\"><span class='bread-crumb-span'>"+node.name+"</span></div>");
        //     listFile(parentId, 1);
        // })


}

/*面包屑标签点击跳转*/
function jumpFolder(th, folderId) {
    // debugger;
    $(th).nextAll(["div"]).remove();
    if(folderId==0){
        parentId = folderId
        listFile(folderId, 1);
    }else{
        var node =JSON.parse($(th).attr("data-info"))
        openFolder(node)
    }

    // parentId = folderId;
    // listFile(folderId, 1);
}

/* 文件列表中的对象操作 */
function optionInode(t) {
    var node = $(t).data("node");
    var ownerId = node.ownedBy;
    var nodeId = node.id
    if (node.type <= 0 ) {
        openFolder(node);
    } else {
        var previewable = isFilePreviewable(node.name);
        var imgable = isImg(node.name);
        var videoable = isVideo(node.name);
        var pla=ismobile(1);
        if(previewable){
            gotoPage(ctx+'/files/gotoPreview/'+node.ownedBy+'/'+node.id)
        }else if(imgable){
            imgClick(t)
        }else if(videoable && pla=="1"){
            videoPreview(ownerId,nodeId,videoable)
        }else{
            downloadFile(node);
        }

    }
}
function jumpFolderFromSearch(node) {
    var types = parseQueryString();
    console.log(types)
    if(types.type == -1){
        gotoPage(ctx+"/folder?rootNode="+node.id)
    }else{
        gotoPage(ctx + "/teamspace/file/" + node.ownedBy +"?parentId=" + node.id);
    }
    parentId = node.id;
    // listFile(node.id, 1);
}

/* 搜索列表中的对象操作 */
function optionInodeFromSearch(t) {
    var node = $(t).data("node");
    closeSearchFileDialog();
    if (node.type == 0) {
        jumpFolderFromSearch(node);
    } else {
        var previewable = isFilePreviewable(node.name)
        if(previewable){
            previewFile(node)
        }else{
            downloadFile(node);
        }
    }
}

function showSearchFileDialog() {
    $("#searchFileDialog").show();
    $('.weui-icon-search').hide();
    //$("#searchFileInput").focus();
    $("#searchClear").trigger("click");
//  $('#searchFileInput').css('line-height',$(#searchFileInput).parent().parent().parent().height()+'px');
//  $('#searchClose').css('line-height',$(#searchFileInput).parent().parent().parent().height()+'px');
		$('#searchFileInput').focus(function(){
			$('.weui-icon-search').hide();
		});
		$('#searchFileInput').blur(function(){
			if($('#searchFileInput').val()==''){
				$('.weui-icon-search').show();
			}else{
				$('.weui-icon-search').hide();
			}
		})
}

function closeSearchFileDialog() {
    $("#searchFileList").empty()
    $("#searchFileDialog").hide();
}

function doSearch(keyword, folderId) {
    folderId = folderId || ownerId;
    var url = host + "/ufm/api/v2/nodes/" + folderId + "/search";
    var params = {
        name: keyword,
        order: [{ field: 'type' , direction: 'ASC' },{ field: 'modifiedAt' , direction: "DESC" }],
        thumbnail: [{ width: 96, height: 96 }]
    };

    $.ajax({
        type: "POST",
        url: url,
        data: JSON.stringify(params),
        error: handleError,
        success: function (data) {
            var fileList = data.folders.concat(data.files);
            if(fileList.length==0){
                $.toast("暂无相关的文件","text")
            }
            $("#searchChooseFile").hide();
            $("#titleList").show();
            var $list = $("#searchFileList");
            var $template = $("#searchFileTemplate");
            $list.children().remove();

            for (var i in fileList) {
                var item = fileList[i];
                item.size = formatFileSize(item.size);
                item.modifiedAt = getFormatDate(new Date(item.modifiedAt), "yyyy-MM-dd");
                item.divClass = getImgHtml(item.type, item.name);
                $template.template(item).appendTo($list);

                //设置数据
                var $row = $("#searchFile_" + item.id);
                $row.data("node", item);

                //增加长按事件
                // new Hammer($row[0]).on('press', onPress);
            }

        }
    });
}

function newFolderDialog(callback) {
    $.prompt({
        text: "不超过255个字符，前后两端不能出现特殊字符",
        title: "输入文件夹名",
        onOK: function (text) {
            createFolders(text, callback);
        },
        onCancel: function () {
        },
        input: ''
    });
    $('#weui-prompt-input').click(function(){
    	$('#weui-prompt-input').val('');
    });
	$('#weui-prompt-input').blur(function(){
		if($('#weui-prompt-input').val()==""){
			$('#weui-prompt-input').attr('placeholder','请输入文件夹名称');
		}
	});
    $('#weui-prompt-input').focus(function(){
        $('#weui-prompt-input').attr('placeholder','请输入文件夹名称');
    });
}

function createFolders(newName, callback) {
    console.log(newName)
    var parameters = {
        parent: parentId,
        name: newName
    };
    var regEn = /[`~!@#$%^&*()_+<>?:"{},.\/;'[\]]/im;
    var regCn = /[·！#￥（——）：；“”‘、，|《。》？、【】[\]]/im;
    var lastname = newName.charAt(newName.length-1);
    var firstname = newName.charAt(0);
    if(regEn.test(lastname) || regCn.test(lastname)) {
        $.alert("最后一个字符不能以特殊符号结束");
        return;
    } else if(regEn.test(firstname) || regCn.test(firstname)) {
        $.alert("第一个字符不能以特殊符号开头");
        return;
    }
    $.ajax({
        type: "POST",
        url: host + "/ufm/api/v2/folders/"+ownerId,
        data: JSON.stringify(parameters),
        error: function (xhr, status, error) {
            var responseObj = $.parseJSON(request.responseText);
            switch (responseObj.code) {
                case "Forbidden" || "SecurityMatrixForbidden":
                    $.alert("您没有权限进行该操作！");
                    break;
                case "ExceedUserMaxNodeNum":
                    $.toast("文件总数超过限制", "cancel");
                    break;
                case "RepeatNameConflict":
                	$.toast("已存在相同文件名","cancel");
                	break;
                default:
                    $.toast("操作失败", "cancel");
            }
        },
        success: function () {
            if (typeof callback == "function") {
                callback(parentId, 1);
            }
        }
    });
}

function downloadFile(node) {
    var nodePermission = getNodePermission(node.id,ownerId);
    if (nodePermission == undefined || nodePermission == null || nodePermission["download"] != 1) {
        $.toast("您没有权限进行该操作", "forbidden");
        return;
    }

    $.ajax({
        type: "GET",
        async: false,
        url: host + "/ufm/api/v2/files/"+ownerId+"/"+node.id+"/preview",
        error: handleError,
        success: function (data) {
            $("#downloadFile").attr("href",data.url);
            document.getElementById("downloadFile").click();
            
            var downloadedFiles = localStorage.getItem("downloadedFiles");
            if (downloadedFiles!=null) {
            	tempFileInfo = {"name":node.name,"state":"complete","size":node.size,"complete":100,"createdAt":getFormatDate(new Date(),"yyyy/MM/dd")};
            	downloadedFiles = JSON.parse(downloadedFiles);
            	downloadedFiles.push(tempFileInfo);
			}else{
				tempFileInfo = [{"name":node.name,"state":"complete","size":node.size,"complete":100,"createdAt":getFormatDate(new Date(),"yyyy/MM/dd")}];
				var v1 = JSON.stringify(tempFileInfo);
				downloadedFiles = JSON.parse(v1);
			}
            localStorage.setItem("downloadedFiles",JSON.stringify(downloadedFiles));
        }
    });
}

function downloadFileByNodeId(nodeId) {
    if(ownerId !=curUserId){
        var nodePermission = getNodePermission(nodeId,ownerId);
        if (nodePermission == undefined || nodePermission == null || nodePermission["download"] != 1) {
            $.toast("您没有权限进行该操作", "forbidden");
            return;
        }
    }
    $.ajax({
        type: "GET",
        async: false,
        url: host + "/ufm/api/v2/files/"+ownerId+"/"+nodeId+"/UrlAndBrowse",
        error: handleError,
        success: function (data) {
            $("#downloadFile").attr("href",data.downloadUrl);
            document.getElementById("downloadFile").click();
        }
    });
}

function deleteFile(node) {
    if(node.ownedBy != curUserId) {
        var nodePermission = getNodePermission(node.id, node.ownedBy);
        if (nodePermission["delete"] != 1) {
            $.alert('您没有权限操作！');
            return;
        }
    }
    $.confirm(
        "确定删除此文件吗？已删除文件可在回收站中进行恢复","删除",
        function(){
                $.ajax({
                    type: "DELETE",
                    url: host + "/ufm/api/v2/nodes/"+ownerId+"/"+node.id,
                    data: "{}",
                    error: function (xhr, status, error) {
                        var status = xhr.status;
                        if (status === 403) {
                            $.toast("您没有权限进行该操作", "forbidden");
                        } else {
                            $.toast("操作失败，请重试", "forbidden");
                        }
                    },
                    success: function (data) {
                        $.toast("删除成功", "success");
                        listFile(parentId, 1);
                    }
                });

        })
}

function getNodePermission(parentId,ownerId) {
    var permission = null;
    var url = host + "/ufm/api/v2/permissions/"+ ownerId +"/" + parentId + "/" +curUserId;

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
function getNodePermissionTeam(parentId,ownerId) {
    var permission = null;
    var url = host + "/ufm/api/v2/permissions/"+ ownerId +"/" + parentId + "/" +curUserId;
    $.ajax({
        type: "GET",
        url: url,
        async: false,
        beforeSend: function(xhr) {
            xhr.setRequestHeader("Authorization", userToken);
            xhr.setRequestHeader("Content-Type","application/json");
        },
        error: function (xhr, status, error) {
        },
        success: function (data) {
            permission = data.permissions;
        }
    });
    return permission;
}
function renameDialog(node){
    if(node.ownedBy != curUserId){
        var nodePermission = getNodePermission(node.id,node.ownedBy);
        if(nodePermission["authorize"] != 1){
            $.alert('您没有权限操作！');
            return;
        }
    }
	var name = node.name;
	if(node.type == 1){
		var index = node.name.lastIndexOf(".");
		if(index != -1){
			name = (node.name).substring(0,index);
		}
	}
    $.prompt({
        text: "不超过255个字符，前后两端不能出现特殊字符",
        title: "请输入文件夹名",
        onOK: function(text) {
            renameNode(node,text);
        },
        onCancel: function() {
        },
        input: name
    });
}

function renameNode(node,newName){
	if(newName==null || newName==""){
		$.toast("名字不能为空", "forbidden");
	}
    var regEn = /[`~!@#$%^&*()_+<>?:"{},.\/;'[\]]/im;
    var regCn = /[·！#￥（——）：；“”‘、，|《。》？、【】[\]]/im;
    var lastname = newName.charAt(newName.length-1);
    var firstname = newName.charAt(0);
    if(regEn.test(lastname) || regCn.test(lastname)) {
        $.alert("最后一个字符不能以特殊符号结束");
        return false;
    } else if(regEn.test(firstname) || regCn.test(firstname)) {
        $.alert("第一个字符不能以特殊符号开头");
        return false;
    }
	if(node.type == 1){
		var index = node.name.lastIndexOf(".");
		if(index != -1){
			newName = newName + node.name.substring(index);
		}
	}
    var parameters={
        name:newName,
    };
        $.ajax({
            type: "PUT",
            url: host + "/ufm/api/v2/nodes/"+node.ownedBy+"/"+node.id,
            data: JSON.stringify(parameters),
            error: handleError,
            success: function () {
                $.toast("修改成功");
                listFile(parentId, 1);
            }
        });


}
function saveRename() {
    
}

function setReceiveDirectory(node) {
	  gotoPage(ctx + "/share/reciveFolder/" +node.ownedBy +"/"+ node.id);
}
// 滑动外发
function swipeLinkDialog (th) {
    var ownedBy = $(th).parent().attr("ownedBy");
    var nodeId = $(th).parent().attr("nodeId");
    if(ownedBy != curUserId) {
        var nodePermission = getNodePermission(nodeId, ownedBy);
        if (nodePermission["publishLink"] != 1) {
            $.alert('您没有外发权限');
            return;
        }
    }
     gotoPage(ctx + "/share/link/" + ownedBy  + "/" + nodeId );
}
function showLinkDialog(node){
    if(node.ownedBy != curUserId) {
        var nodePermission = getNodePermission(node.id, node.ownedBy);
        if (nodePermission["publishLink"] != 1) {
            $.alert('您没有外发权限');
            return;
        }
    }
    gotoPage(ctx + "/share/link/" + node.ownedBy  + "/" + node.id );


}
function showLinkDialogInButtonEvent(e){
    var node = $(e).parent().prev().data("node");
    var nodePermission = getNodePermission(node.id,node.ownedBy);
    if(nodePermission["publishLink"] != 1){
    	$.alert('您没有外发权限');
		return;
	}
	showLinkDialog(node);
}
function showShareDialog(node){
	  gotoPage(ctx + "/share/folder/" + node.ownedBy  + "/" + node.id );
}
// 滑动共享
function swipeShareDialog(th){
    var ownedBy = $(th).parent().attr("ownedBy");
    var nodeId = $(th).parent().attr("nodeId");
    gotoPage(ctx + "/share/folder/" + ownedBy  + "/" + nodeId );
}
function showShareDialogInButtonEvent(e){
    var node = $(e).parent().prev().data("node");
    gotoPage(ctx + "/share/folder/" + node.ownedBy + "/" + node.id);
}

function showMoveToDialog(node,value,name) {
    getFileFolderInfo(node);
    /* 在当前空间内移动，所以传入当前使用的ownerId*/
    var folderChooser = $("#copyToFolderChooserDialog").FolderChooser({
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
                        listFile(parentId, 1);
                    }
                });
            }
        }
    );

    //加载数据,并显示
    folderChooser.showDialog();
}
function getFileFolderInfo(node) {
    var divClass = getImgHtml(node.type, node.name);
    $("#filesIcon").addClass(divClass)
    $("#fileFolderName").html(node.name)
    $("#fileFolderOwnerName").html(node.menderName)
    $("#fileFolderTime").html(node.modifiedAt)
}
function showCopyToDialogTeam(node) {
    console.log(node)
    getFileFolderInfo(node);
    var folderChooser = $("#copyToFolderChooserDialog").FolderChooser({
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
                        $.toast("另存成功");
                    }
                });
            }
        }
    );

    //加载数据,并显示
    folderChooser.showDialog();
}

function showFileProperties(node) {
    var nodePermission = getNodePermission(node.id,node.ownedBy);
    if(nodePermission["download"] != 1){
        $.alert('您没有权限进行操作！');
        return;
    }
	 gotoPage(ctx + '/files/gotoFileInfo/' + node.ownedBy + "/" + node.id + "?type=" + node.type + "&fileName=" + node.name)
}

function addShortcutFolder(node) {
    console.log(node)
    $.ajax({
        type: "POST",
        data: JSON.stringify({
            createBy:curUserId,
            ownerId:node.ownedBy,
            nodeId:node.id,
            type:1
        }),
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

function deleteShortcutFolder(node) {
	 $.ajax({
	        type: "POST",
	        data: {folderType:type,tag:tag},
	        url:  ctx + "/folders/" + ownerId + "/shortcut/create",
	        error: function (xhr, status, error) {
	            $.toast('操作失败', 'forbidden');
	        },
	        success: function (data) {
	          $.toast("操作成功");
	        }
	    });
}





