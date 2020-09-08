/*分页相关定义*/
var pageSize = getCookie("fileListPageSize", 40);
/*排序字段，["modifiedAt", "name", "size"]*/
var orderField = getCookie("orderField", "modifiedAt");
/*是否倒序*/
var isDesc = getCookie("isDesc", "true");
/*文件列表显示方式：列表或缩略图*/
var listViewType = getCookie("listViewType", "list");
/* 正在加载 */
var __loading = false;

var __page = 1;
var __loadmore = false;

/*
  js中使用的parentId在include.jsp中定义。
 */
/*
此方法为文件列表的初始化方法，各个界面应该调用此方法。
 */
function init() {
    //排序字段
    var $dateSort = $("#dateSort");
    var $nameSort = $("#nameSort");
    if (orderField == null || orderField == 'modifiedAt') {
        if (isDesc == null || isDesc == 'true') {
            $dateSort.addClass("sort-desc");
        } else {
            $dateSort.addClass("sort-asc");
        }
        orderField = "modifiedAt";
    } else {
        if (isDesc == null || isDesc == 'true') {
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

        listFile(parentId, 1);
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
    $('#searchFileInput').bind('input propertychange', function (e) {
            var keyword = $("#searchFileInput").val();
            if(keyword !== "" && keyword.trim() != "") {
                doSearch(keyword.trim());
            }
        }
    );

    //下拉刷新
    var $listWrapper = $("#fileListWrapper");
    $listWrapper.pullToRefresh().on("pull-to-refresh", function() {
        //console.log("pulltorefresh triggered...");
        listFile();
        setTimeout(function() {
            $("#fileListWrapper").pullToRefreshDone();
        }, 200);
    });

    //上滑加载
    $listWrapper.infinite().on("infinite", function() {
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

function listFile(folderId, page) {
    parentId = folderId || parentId;
    __page = page || 1;
    var permission = getNodePermission(parentId);
    var p = 0;
    for (var i in permission) {
        p = p + permission[i];
    }
    if (p == 0) {
        $.toast("您没有权限进行该操作", "cancel");
        return;
    }
		
    var url = ctx + "/folders/list";
    var params = {
        "ownerId": ownerId,
        "parentId": parentId,
        "pageNumber": __page,
        "pageSize": pageSize,
        "orderField": orderField,
        "desc": isDesc,
        "token": token,
    };
    if (permission != null && permission["browse"] == 1) {
        $.ajax({
            type: "POST",
            url: url,
            data: params,
            error: handleError,
            success: function (data) {
            	dataContent=data.content;
                var fileList = data.content;
                __page = data.number;
                __loadmore = __page < data.totalPages;
                var $list = $("#fileList");
                var $template = $("#fileTemplate");

                //加载第一页，清除以前的记录
                if(__page == 1) {
                    $list.children().remove();
                }
                if(fileList.length == 0){
                	$list.parent().css("background","url('"+ctx+"/static/skins/default/img/iconblack_17.png')no-repeat center center");
                	$list.parent().css('background-size','5rem 5rem');
                }else{
           				$list.parent().css('background','')
           			}
                for (var i in fileList) {
                    var item = fileList[i];
                    if(item.type==1){
                    	item.size = formatFileSize(item.size);
                    	if(typeof(item.thumbnailUrlList)!="undefined" && item.thumbnailUrlList.length>0){
                        	item.imgPath = item.thumbnailUrlList[1].thumbnailUrl;
                        }
                    }else{
                    	item.size = "";
                    }
                    if(permission.download==1){
                    	item.download = 1;
                    }
                    item.modifiedAt = getFormatDate(new Date(item.modifiedAt), "yyyy/MM/dd");
                    item.divClass = getImgHtml(item.type, item.name, item.shareStatus);
                    $template.template(item).appendTo($list);
                    //设置数据\n
                    var $row = $("#file_" + item.id);
                    $row.data("node", item);
                    try {
                    	if(teamRole&&item.type==0){
                    		 if(teamRole=='admin'){
                    			  isVisibleNodeACL(item,ownerId,item.id);
                             }else{
                            	　setFolderIsShow(item,ownerId,item.id);
                             }
                    	}
		                    　} catch(err) {}
                    $list.addLineScrollAnimate();
                    //增加长按事件
                    new Hammer($row[0]).on('press', onPress);
                }

               
            },complete:function(){
            	$('.load').css('display','none');
            }
        });
    } else {
        $.toast("您没有权限进行该操作", "cancel");
    }
}

function openFolder(node) {
	$('.load').css('display','block');
	$("#fileList").children().remove();
    parentId = node.id;
    $("#directory").append("<div onclick=\"jumpFolder(this," + node.id + ");\">&nbsp;" + node.name + "&nbsp;</div");

    var nodePermission = getNodePermission(parentId);
    if (nodePermission["browse"] != 1) {
        $.toast("您没有权限进行该操作", "forbidden");
        return;
    }
    if (nodePermission["upload"] != 1) {
        $('#upload_file').hide();
        $.toast("您没有权限进行该操作", "forbidden");
        return;
    }
    listFile(parentId, 1);
}

/*面包屑标签点击跳转*/
function jumpFolder(th, folderId) {
    $(th).nextAll(["div"]).remove();
    parentId = folderId;
    listFile(folderId, 1);
}

/* 文件列表中的对象操作 */
function optionInode(t) {
    var node = $(t).data("node");
    if (node.type == 0) {
        openFolder(node);
    } else {
        previewFile(node);
    }
}

function jumpFolderFromSearch(node) {
    changeBreadcrumb(node.id);
    parentId = node.id;
    listFile(node.id, 1);
}

/* 搜索列表中的对象操作 */
function optionInodeFromSearch(t) {
    var node = $(t).data("node");
    closeSearchFileDialog();
    if (node.type == 0) {
        jumpFolderFromSearch(node);
    } else {
        previewFile(node);
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
    $("#searchFileDialog").hide();
}

function doSearch(keyword) {
    var url = ctx + "/nodes/search";
    var params = {
        "ownerId": ownerId,
        "name": keyword,
        "pageNumber": 1,
        "pageSize": pageSize,
        "orderField": orderField,
        "desc": isDesc,
        "token": token,
        /*
         "labelIds": labelId,
         "docType": docType,
         */
        "searchType": 0
    };

    $.ajax({
        type: "POST",
        url: url,
        data: params,
        error: handleError,
        success: function (data) {
            var fileList = data.content;

            var $list = $("#searchFileList");
            var $template = $("#searchFileTemplate");
            $list.children().remove();

            for (var i in fileList) {
                var item = fileList[i];
                item.size = formatFileSize(item.size);
                item.modifiedAt = getFormatDate(new Date(item.modifiedAt), "yyyy/MM/dd");
                item.divClass = getImgHtml(item.type, item.name);
                $template.template(item).appendTo($list);

                //设置数据
                var $row = $("#searchFile_" + item.id);
                $row.data("node", item);

                //增加长按事件
                //new Hammer($row[0]).on('press', onPress);
            }

            //增加左滑显示按钮效果
            $list.addLineScrollAnimate();
        }
    });
}

function newFolderDialog(callback) {
    $.prompt({
        text: "不超过255个字符，不能出现特殊字符：\\/:*?\"<>|",
        title: "输入文件夹名",
        onOK: function (text) {
            createFolder(text, callback);
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
			$('#weui-prompt-input').attr('placeholder','新文件夹');
		}
	});
}

function createFolder(newName, callback) {
    var parameter = {
        ownerId: ownerId,
        parentId: parentId,
        name: newName,
        token: token
    };
    $.ajax({
        type: "POST",
        url: ctx + "/folders/create",
        data: parameter,
        error: function (request) {
            var responseObj = $.parseJSON(request.responseText);
            switch (responseObj.code) {
                case "Forbidden" || "SecurityMatrixForbidden":
                    $.toast("您没有权限进行该操作", "forbidden");
                    break;
                case "ExceedUserMaxNodeNum":
                    $.toast("文件总数超过限制", "cancel");
                    break;
                case "RepeatNameConflict":
                		$.toast("已存在相同文件名","cancel");
                		break
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

function deleteFile(node) {
	 $.ajax({
         type: "POST",
         url: ctx + "/nodes/delete",
         data: {'ownerId': ownerId, 'ids': node.id, 'token': token},
         error: function (data) {
             var status = data.status;
             if (status == 403) {
            	 $.toast("您没有权限进行该操作", "forbidden");
             } else {
            	 $.toast("操作失败，请重试", "forbidden");
             }
         },
         success: function (data) {
        	 listFile(parentId, 1);
         }
     });
}

function renameDialog(node){
	var name = node.name;
	if(node.type == 1){
		var index = node.name.lastIndexOf(".");
		if(index != -1){
			name = (node.name).substring(0,index);
		}
	}
    $.prompt({
        text: "不超过255个字符，不能出现特殊字符：\\/:*?\"<>|",
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
	if(node.type == 1){
		var index = node.name.lastIndexOf(".");
		if(index != -1){
			newName = newName + node.name.substring(index);
		}
	}
    var parameter={
        ownerId:node.ownedBy,
        parentId:node.parentId,
        nodeId:node.id,
        name:newName,
        token:token
    };
    $.ajax({
        type: "POST",
        url: ctx + "/nodes/rename",
        data: parameter,
        error: handleError,
        success: function () {
        	$.toast("修改成功");
            listFile(parentId, 1);
        }
    });
}

function setReceiveDirectory(node) {
	  gotoPage(ctx + "/share/reciveFolder/" +node.ownedBy +"/"+ node.id);
}

function showLinkDialog(node){
	  gotoPage(ctx + "/share/link/" + node.ownedBy  + "/" + node.id );
}
function showLinkDialogInButtonEvent(e){
    var node = $(e).parent().prev().data("node");
	showLinkDialog(node);
}
function showShareDialog(node){
	  gotoPage(ctx + "/share/folder/" + node.ownedBy  + "/" + node.id );
}

function showShareDialogInButtonEvent(e){
    var node = $(e).parent().prev().data("node");
    gotoPage(ctx + "/share/folder/" + node.ownedBy + "/" + node.id);
}

function showMoveToDialog(node,value,name) {
    /* 在当前空间内移动，所以传入当前使用的ownerId*/
    var folderChooser = new FolderChooser(ownerId,value);
    folderChooser.show(function (folderId) {
        var params = {
            "destOwnerId": ownerId,
            "ids": node.id,
            "parentId": folderId,
            "startPoint": "operative",
            "endPoint": "operative",
            "token": token
        };
        $.ajax({
            type: "POST",
            url: ctx + "/nodes/move/" + ownerId,
            data: params,
            error: handleError,
            success: function (data) {
                console.log(2);
                $.toast("操作成功");
                listFile();
            }
        });
    });
}

function showCopyToDialog(node) {
    /* 在当前空间内操作，所以传入当前使用的ownerId*/
    var folderChooser = new FolderChooser(ownerId);
    folderChooser.show(function (folderId) {
        var params = {
            "destOwnerId": ownerId,
            "ids": node.id,
            "parentId": folderId,
            "token": token
        };
        $.ajax({
            type: "POST",
            url: ctx + "/nodes/copy/" + ownerId,
            data: params,
            error: handleError,
            success: function (data) {
                $.toast("操作成功");
                listFile();
            }
        });
    });
}

function showFileProperties(node) {
	 gotoPage(ctx + '/files/gotoFileInfo/' + node.ownedBy + "/" + node.id + "?type=" + node.type + "&fileName=" + node.name)
}

function addShortcutFolder(node) {
    $.ajax({
        type: "POST",
        data: {nodeId:node.id,type:1},
        url:  ctx + "/folders/" + ownerId + "/shortcut/create",
        error: function (request,textStatus) {
        	switch(request.responseText)
			{
				case "ExsitShortcut":
					 $.toast('快捷目录已存在',"forbidden");
					break;
				default:
				    $.toast('操作失败', 'forbidden');
				    break;
			}
           /* $.toast('操作失败', 'forbidden');*/
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
	        error: function (request) {
	            $.toast('操作失败', 'forbidden');
	        },
	        success: function (data) {
	          $.toast("操作成功");
	        }
	    });
}



