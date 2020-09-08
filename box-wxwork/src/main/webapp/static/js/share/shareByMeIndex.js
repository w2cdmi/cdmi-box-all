var orderField = "modifiedAt";
var order = "DESC";
var desc = true;
var num = -1;
$(function() {
	
	$("#dateSort").click(function(){
		$(".sort-button").children().removeClass("sort-asc");
		$(".sort-button").children().removeClass("sort-desc");
		if(orderField=="modifiedAt"){
			if(order=="DESC"){
				order = "ASC";
				$("#dateSort").addClass("sort-asc");
			}else{
				order = "DESC";
				$("#dateSort").addClass("sort-desc");
			}
		}else{
			orderField = "modifiedAt";
            order = "DESC";;
			$("#dateSort").addClass("sort-desc");
		}
		shareByMeListInit();
	});
	
	$("#nameSort").click(function(){
		$(".sort-button").children().removeClass("sort-asc");
		$(".sort-button").children().removeClass("sort-desc");
		
		if(orderField=="name"){
			if(order == "DESC"){
                order = "ASC";
				$("#nameSort").addClass("sort-asc");
			}else{
                order = "DESC";
				$("#nameSort").addClass("sort-desc");
			}
		}else{
			orderField = "name";
            order = "DESC";
			$("#nameSort").addClass("sort-desc");
		}
		shareByMeListInit();
	});
	
	$("#dateSort").addClass("sort-desc");
	shareByMeListInit();
})

function shareByMeListInit() {
	$.ajax({
		type : "POST",
		url : host + "/ufm/api/v2/shares/distributed",
		data : JSON.stringify({
            // pageNumber:1,
            // pageSize:100,
            // orderField:orderField,
            // desc:desc,
            // token:token
            order: [{ field: 'type' , direction: "ASC" },{ field: orderField, direction: order }],
            thumbnail: [{ width: 96, height: 96 }, { width: 250, height: 200 }]
        }),
		error : handleError,
		success : function(data) {
			var fileList = data.contents;
			var $list = $("#fileList");
			var $template = $("#shareByMeFileTemplate");
			$list.children().remove();

			if(data.contents.length==0){
				$('#box').css('background','url('+ctx+'/static/skins/default/img/iconblack_17.png)no-repeat center center');
				$('#box').css('background-size','7rem 8rem');
			}else{
				$('#box').css('background','')
			}
			for ( var i in fileList) {
				var item = fileList[i];
				item.nodeId = item.nodeId || item.iNodeId;

				if(item.nodeId==-1){
					continue;
				}
				if(item.size == 0){
					item.size = "";
				}else{
					item.size = formatFileSize(item.size);
					if(typeof(item.thumbnailUrlList)!="undefined" && item.thumbnailUrlList.length>0){
                    	item.imgPath = item.thumbnailUrlList[1].thumbnailUrl;
                    }
                    if(isImg(item.name)){
                        num++
                        var index = item.thumbnailUrlList[0].thumbnailUrl.lastIndexOf("/");
                        var imgSrc = item.thumbnailUrlList[0].thumbnailUrl.substring(0,index)
                        item.imgSrc = imgSrc
                        item.num = num
                    }
				}
				var shareStatus = 1;	//都是共享文件
				item.imgClass = getImgHtml(item.type, item.name, shareStatus);
				item.modifiedAt = getFormatDate(new Date(item.modifiedAt),"yyyy-MM-dd");
				$template.template(item).appendTo($list);
				// 增加滑动
                $('.weui-cell_swiped').swipeout()

				//设置数据\n
                var $row = $("#shareByMeFile_" + item.nodeId);
                $row.data("node", item);

                $row.on('click', press);
			}
			num = -1
		},
		complete:function(){
			$('.load').css('display','none');
		}
	});
}

function press(e){
	e.stopPropagation()
	var $target = $(e.currentTarget);
    var node = $target.data("node");
    
    onPress(node.nodeId,node);
}

function onPress(nodeId,node){

    var actions=[];

    actions.push({
        text: "查看共享",
        className: "color-primary",
        onClick: function() {
        	gotoPage(ctx + "/share/folder/" + ownerId  + "/" + nodeId );
        }
    });
    actions.push({
        text: "取消共享",
        className: "color-primary",
        onClick: function() {
        	cancelShare(nodeId);
        }
    });
    $.actions({
        title: node.name,
        actions:  actions
    });
    layelTitle(node.imgClass,node.ownerName,node.modifiedAt)
}
function enterFolder(nodeId){
	gotoPage(ctx + "/folder?rootNode=" + nodeId);
}
function shareByMeIndexviewshare (nodeId){
	gotoPage(ctx + "/share/folder/" + ownerId  + "/" + nodeId );
}
function cancelShare(nodeId){
	$.confirm(
		"确认取消共享吗？",
		function(){
			$.ajax({
				type : "DELETE",
				url : host+"/ufm/api/v2/shareships/"+ownerId+"/"+nodeId,
				error : handleError,
				success : function(data) {
						$.toast("取消共享成功");
						shareByMeListInit();
				}
			})
		});
}
function downloadFileByNodeId(nodeId,name,t) {
    var previewable = isFilePreviewable(name);
    var imgable = isImg(name);
    var videoable = isVideo(name)
    var pla=ismobile(1);
    if (previewable) {
        gotoPage(ctx + '/files/gotoPreview/' + ownerId + '/' + nodeId)
    }else if(imgable){
        imgClick(t)
    }else if(videoable && pla=="1"){
    	videoPreview(ownerId,nodeId,videoable)
	}else {
        $.ajax({
            type: "GET",
            async: false,
            url: host + "/ufm/api/v2/files/" + ownerId + "/" + nodeId + "/UrlAndBrowse",
            error: handleError,
            success: function (data) {
                $("#downloadFile").attr("href", data.downloadUrl);
                document.getElementById("downloadFile").click();
            }
        });
    }

}