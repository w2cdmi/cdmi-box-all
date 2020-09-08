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
var num = -1;
$(function () {
	init();

	listLink();
});

/*
 此方法为文件列表的初始化方法，各个界面应该调用此方法。
 */
function init() {
	//排序字段
	var $nameSort = $("#nameSort");
	var $dateSort = $('#dateSort');
	if (isDesc == null || isDesc == 'true') {
		$nameSort.addClass("sort-desc");
	} else {
		$nameSort.addClass("sort-asc");
	}

	$nameSort.on("click", function() {
		var $this = $(this);
		if($this.hasClass("sort-desc")) {
			$this.removeClass("sort-desc").addClass("sort-asc");
			$this.siblings().removeClass();
			orderField="name";
			isDesc = "false";
		} else {
			$this.removeClass("sort-asc").addClass("sort-desc");
			$this.siblings().removeClass();
			orderField="name";
			isDesc = "true";
		}

		setStorage("isDesc", isDesc);

		listLink(1);
	});
	
	$dateSort.on('click',function(){
		if($(this).hasClass("sort-asc")){
			$(this).removeClass("sort-asc").addClass("sort-desc");
			$(this).siblings().removeClass().addClass("label");
			isDesc = "false";
		}else{
			$(this).removeClass("sort-desc").addClass("sort-asc");
			$(this).siblings().removeClass().addClass("label");
			isDesc = "true";
		}
		setCookie("isDesc", isDesc);
        orderField = "modifiedAt";
        setCookie("orderField", "modifiedAt");
        listLink(1);
	});

	//文件列表显示方式
	if (listViewType == "list") {
		$("#viewTypeBtnList").addClass("active");
	} else {
		$("#viewTypeBtnThumbnail").addClass("active");
	}

	//下拉刷新
	var $listWrapper = $("#linkListWrapper");
	$listWrapper.pullToRefresh().on("pull-to-refresh", function() {
		//console.log("pulltorefresh triggered...");
		listLink();
		setTimeout(function() {
			$("#linkListWrapper").pullToRefreshDone();
		}, 200);
	});

	//上滑加载
	$listWrapper.infinite().on("infinite", function() {
		//console.log("loadmore triggered...");
		if(__loading) return;

		if(__loadmore) {
			__loading = true;
			$.showLoading();
			listLink(++__page);
			setTimeout(function() {
				__loading = false;
				$.hideLoading();
			}, 200);
		}
	});
}

function listLink(page) {
	__page = page || 1;

	var url = host + "/ufm/api/v2/links/items";
	var params = {
        limit: pageSize,
        offset: (__page-1)*pageSize,
        thumbnail: [{ width: 96, height: 96 }]
	};
	$.ajax({
		type: "POST",
		url: url,
		data: JSON.stringify(params),
		error: handleError,
		success: function (data) {
			var fileList = data.folders.concat(data.files);
            __page = page || 1;
            __loadmore = data.totalCount > __page * pageSize;
            __loadmore = data.totalCount > __page * pageSize;
			var $list = $("#fileList");
			var $template = $("#linkTemplate");
			if(fileList.length == 0){
				$('#linkListWrapper').css('background','url('+ctx+'/static/skins/default/img/iconblack_17.png) no-repeat center center');
				$('#linkListWrapper').css('background-size','7rem 8rem')
			}else{
				$('#linkListWrapper').css('background','');
			}
            console.log(__page)
			//加载第一页，清除以前的记录
			if(__page == 1) {
				$list.children().remove();
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
				item.modifiedAt = getFormatDate(new Date(item.modifiedAt), "yyyy-MM-dd");
				item.divClass = getImgHtml(item.type, item.name);
				$template.template(item).appendTo($list).data("node", item);
                // 增加滑动
                $('.weui-cell_swiped').swipeout()
                // 增加点击事件
				var $row = $("#link_" + item.id);
                $row.data("node", item);
                $row.on('click', onPress);
			}
			num = -1
			//增加左滑显示按钮效果
			$list.addLineScrollAnimate();
//			$(".line-scroll-wrapper").each(function(index, item) {
//				new Hammer(item).on('press', showActionSheet);
//			})
		},
		complete:function(){
        	$('.load').css('display','none');
        }
	});
}

/* 列表中的对象操作 */
function optionInode(folderId) {
	gotoPage(ctx+'/folder?rootNode=' + folderId);
}

function onPress(e){
	e.stopPropagation()
	var $target = $(e.currentTarget);
    var node = $target.data("node");
    var actions=[];
    actions.push({
        text: "查看详情",
        className: "color-primary",
        onClick: function() {
            gotoPage(ctx + '/files/gotoFileInfo/' + node.ownedBy + "/" + node.id + "?type=" + node.type + "&fileName=" + node.name);
        }
    });
    actions.push({
        text: "取消外发",
        className: "color-primary",
    		onClick: function() {
				cancelAllLinksOfFile(node.id, node.ownedBy)
			}
    });
    
    $.actions({
        title: node.name,
        actions:  actions
    });

    var $title = $(".weui-actionsheet__title");
    $title.prepend("<i class=" + node.divClass + "></i>");
    $title.append("<div><span>" + node.modifiedAt + "</span></div>");
}

function downloadFile(ownerId, fileId, name,t) {
	var previewable = isFilePreviewable(name);
	var imgable = isImg(name);
	var videoable = isVideo(name);
    var pla=ismobile(1);
	if(previewable){
        gotoPage(ctx+'/files/gotoPreview/'+ ownerId +'/'+ fileId)
	}else if(imgable){
		imgClick(t)
	}else if(videoable && pla== "1"){
		videoPreview(ownerId,fileId,videoable)
	}else{
        $.ajax({
            type: "GET",
            async: false,
            url: host + "/ufm/api/v2/files/"+ownerId+"/"+fileId+"/UrlAndBrowse",
            error: handleError,
            success: function (data) {
                $("#downloadFile").attr("download", name).attr("href", data.downloadUrl);
                document.getElementById("downloadFile").click();
            }
        });
	}

}

function showLinkDialog(e) {
	var node = $(e).parent().parent().data("node");
	gotoPage(ctx + "/share/link/" + node.ownedBy  + "/" + node.id );
}

function showFileProperties(e) {
	var node = $(e).parent().data("node");
	gotoPage(ctx + '/files/gotoFileInfo/' + node.ownedBy + "/" + node.id + "?type=" + node.type + "&fileName=" + node.name);
}
function cancelAllLinksOfFile(objectId, ownerId) {
	$.ajax({
		type: "DELETE",
		url: host + "/ufm/api/v2/links/"+ownerId+"/"+objectId,
		error: handleError,
		success: function (data) {
			$.toast("取消成功");
			listLink(1);
		}
	});
}

function showFilePropertiesForLinkList(o) {
	 var node = $(o).parent().data("node");
	 gotoPage(ctx + '/files/gotoFileInfo/' + node.ownedBy + "/" + node.id + "?type=" + node.type + "&fileName=" + node.name);
}
//function showActionSheet(){
//	$.actions({
//			title: "选择操作",
//			onClose: function() {
//				console.log("close");
//			},
//			actions: [{
//					text: "进入目录",
//					className: "color-primary",
//					onClick: function() {
//						$.alert("发布成功");
//					}
//				},
//				{
//					text: "取消外发",
//					className: "color-warning",
//					onClick: function() {
//						$.alert("你选择了“编辑”");
//					}
//				}
//			]
//		});
//}
