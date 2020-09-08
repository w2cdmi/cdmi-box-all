$(function(){
    getUserSpaceInfo();
    getLocalStorageSize();
});

function getUserSpaceInfo(){
    $.ajax({
        type : "GET",
        url : host + "/ufm/api/v2/users/"+curUserId,
        error : function(){
            $.toast("获取用户存储空间信息失败","cancel");
        },
        success : function(data) {
            if(data.spaceQuota==-1){
                $("#spaceBar").css("width","0%");
                $("#useSpace").html(formatFileSize(data.spaceUsed) + "&nbsp;/&nbsp;无限制");
                $(".totalSize").css("display","none");
            }else{
                if((data.spaceUsed/data.spaceQuota)*100>=100){
                    $("#spaceBar").css("width","100%");
                }
                $("#spaceBar").css("width",(data.spaceUsed/data.spaceQuota)*100 + "%");
                $("#useSpace").html(formatFileSize(data.spaceUsed) + "&nbsp;/&nbsp;" + formatFileSize(data.spaceQuota));
                $(".totalSize").css("display","block");
            }
        },
        complete:function(){
            $('.load').css('display','none');
        }

    });
}
//获取本地存储大小
function getLocalStorageSize(){
    var size = 0;
    for (var i = 0; i < localStorage.length; i++) {
        var key= localStorage.key(i);
        size += localStorage.getItem(key).length;
    }
    if(size != 0){
        $("#localStroageSize").html(formatFileSize(size));
    }else{
        $("#localStroageSize").html("0B");
    }
}

function clearLocalStroage(){
    $.confirm({
        title: '提示',
        text: "清除缓存后，您之前下载的文件碎片以及传输列表的内容都会被清除，但不会影响您已经下载的文件，确定清除本地缓存吗？",
        onOK: function () {
            localStorage.clear();
            getLocalStorageSize();
        }
    });
}