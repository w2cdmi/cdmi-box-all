$(function () {
    listFolderForRecent()


})
var num = -1
function listFolderForRecent() {
    $.ajax({
        type: "POST",
        async: false,
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

                for ( var i in data.files) {
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
                    $row.on('click', showActionSheet);
                }
                num = -1
            }
            // recentBrowseMore(data.files);
        },complete:function(){
            $('.load').css('display','none');
        }
    });
}
function downloadFileByNodeIdAndOwnerId(nodeId,ownerBy,div) {
    var name = $(div).attr("fileName")
    var previewable = isFilePreviewable(name)
    var imgable = isImg(name)
    if(previewable){
        gotoPage(ctx+'/files/gotoPreview/'+ownerBy+'/'+nodeId)
    }else if(imgable){
        imgClick(div)
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

// 显示暂无最近浏览
function showNotRecent() {
    var html=""
    html += "<i class='two-index-not-recent'>"
    html += "<img src='"+ctx+"/static/skins/default/img/not-recent1x.png'/>"
    html += "</i>"
    html = html+ "<p class='two-index-not-recent-title'>暂无最近浏览文件</p>"
    $("#fileList").prepend(html)
}