var orderField = "modifiedAt";
var order = "DESC";
var desc = true;

$(function() {
    sortShowHide()
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

        trashFileListInit();
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
        trashFileListInit();
    });
	trashFileListInit();
})

function trashFileListInit() {
	var params={
        order: [{ field: 'type' , direction: "ASC" },{ field: orderField, direction: order }],
        thumbnail: [{ width: 96, height: 96 }, { width: 250, height: 200 }]
	}
	$.ajax({
		type : "POST",
		url : host + "/ufm/api/v2/trash/"+ownerId,
		data : JSON.stringify(params),
		error : handleError,
		success : function(data) {
			var fileList = data.folders.concat(data.files);
			var $list = $("#trashFileList");
			var $template = $("#trashFileTemplate");
			$list.children().remove();
			
			if(fileList.length>0){
				$('.inbox-catalong-blank').css('display','none');				
			}else{
				$('.inbox-catalong-blank').css('display','block');
			}
			
			for ( var i in fileList) {
				var item = fileList[i];
				item.modifiedAt = getFormatDate(new Date(item.modifiedAt), "yyyy-MM-dd");
				item.imgClass = getImgHtml(item.type, item.name);
				item.size = formatFileSize(item.size)
				$template.template(item).appendTo($list);
                // 增加滑动
                $('.weui-cell_swiped').swipeout()
				//设置数据\n
                var $row = $("#trashFile_" + item.id);
                $row.data("node", item);

                $row.on('click', press);
			}
		},
		complete:function(){
        	$('.load').css('display','none');
        }
	});
}

function press(e){
	var $target = $(e.currentTarget);
    var node = $target.data("node");
    
    onPress(node.id,node);
}

function onPress(nodeId,node){
    var actions=[];

    actions.push({
        text: "恢复",
        className: "color-primary",
        onClick: function() {
        	restore(nodeId);
        }
    });
    
    actions.push({
        text: "永久删除",
        className: "color-primary",
        onClick: function() {
        	deleteTrashFile(nodeId);
        }
    });
    
    $.actions({
        title: node.name,
        actions:  actions
    });
    layelTitle(node.imgClass,node.ownerName,node.modifiedAt)
}

function restore(ids){
    $.confirm(
        "确定要恢复此文件吗？",
        function(){
            $.ajax({
                type : "PUT",
                url : host + "/ufm/api/v2/trash/"+ownerId+"/"+ids,
                data : JSON.stringify({
                    autoRename:true
                }),
                error : handleError,
                success : function(data) {
                    $.toast("恢复成功");
                    trashFileListInit();
                }
            });
		})
}
function trashIdexlinebButtonShare(nodeId){
	restore(nodeId);
}
function trashIdexlinebButtonLink(nodeId){
	deleteTrashFile(nodeId);
}
function deleteTrashFile(ids){
    $.confirm(
        "确定要删除此文件吗？",
        function(){
            $.ajax({
                type : "DELETE",
                url : host + "/ufm/api/v2/trash/"+ownerId+"/"+ids,
                error : handleError,
                success : function(data) {
                    $.toast("删除成功");
                    trashFileListInit();
                }
            });
		})
}

function clearTrash(){
	$.confirm(
		"清空之后文件将无法恢复 确定要清空回收站吗？",
		function(){
			$.ajax({
				type : "DELETE",
				url : host + "/ufm/api/v2/trash/"+ownerId,
				error : handleError,
				success : function(data) {
					$.toast("清空回收站成功");
					$("#trashFileList").children().remove();
					$('.inbox-catalong-blank').css('display','block');
				}
			});
		}
		
	)
}
