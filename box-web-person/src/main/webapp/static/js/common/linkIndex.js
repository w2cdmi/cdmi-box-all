// 外链文件

var linkCode = ""

/**
 * 删除所有外链
 * @param _ownerId
 * @param _nodeId
 * @param _callback
 */
function batchDeleteLink(row){
    var linkCodes=getSelectSpan();
    if(linkCodes==""){
        $.Alert("文件没有链接");
    }else{
        $.Confirm("你确认删除所有记录？",function (onOk) {

            $.ajax({
                type: "DELETE",
                url: host + "/ufm/api/v2/links/"+ row.ownedBy +"/"+ row.id,
                error: function(request) {
                        $.Alert("删除失败");
                },
                success: function() {
                    $("#linkDiv").empty();
                }
            });
        });
    }

                    
}
function getLinkData(){
    var linkCodes=getSelectSpan();
    var linkCodeArr=linkCodes.split(",");
    var data="";
    for(var i=0;i<1;i++){
        if(linkCodeArr[i]!=""){
            data=$("#"+linkCodeArr[i]).data("data");
        }
    }
    return data;

}
function getSelectSpan(){
    var linkids="";
    $("span[name='selectSpan']").each(function(){
        if($(this).attr("class")=="spanSelect"){
            linkids=linkids+$(this).attr("value")+",";
        }
    });
    return linkids;
}
function getRandomNum(lbound, ubound) {
    return (Math.floor(Math.random() * (ubound - lbound)) + lbound);
}
function showMenuItems(){
	var data=getLinkData();
	if(data==""){
	    $.toast("选择链接", function() {
	    });
	    return;
	}
	wx.onMenuShareAppMessage({
	    title: '${name}', // 分享标题
	    desc: userName+'给你发送了一个文件!', // 分享描述
	    link: data.url, // 分享链接
	    imgUrl: imgPathUrl,
	    success: function () {
	    },
	    cancel: function () {
	    }
});

      if(wx.onMenuShareWechat!=undefined){
          wx.onMenuShareWechat({
            title: '${name}', // 分享标题
            desc:userName +'给你发送了一个文件!', // 分享描述
            link: data.url, // 分享链接
            imgUrl: imgPathUrl,
            success: function () {
            },
            cancel: function () {
            }
        });
      }
        wx.hideMenuItems({
            menuList: ["menuItem:setFont", "menuItem:refresh", "menuItem:favorite","menuItem:copyUrl","menuItem:openWithSafari"] // 要隐藏的菜单项，所有menu项见附录3
        }); 
}

function fillLinkDiv(data,i){
    var clipboard = new Clipboard('#plainAccessCode'+data.id, {
        // 通过target指定要复印的节点
        text: function() {
            return data.plainAccessCode;
        }
    });
    clipboard.on('success', function(e) {
        $.toast("提取成功", function() {
        });
    })
    $('#linkDiv .weui-flex').click(function(){
        $(this).siblings('#linkDiv .weui-flex').find('.puttinglink-title').children('span').removeClass('spanSelect');
        $(this).find('.puttinglink-title').children('span').addClass('spanSelect');
        showMenuItems();
    });
    $('#linkDiv .weui-flex').eq(0).find('.puttinglink-title').children('span').addClass('spanSelect');
    showMenuItems();
    
}
function getRandomChar(number, chars, other) {
    var numberChars = "0123456789";
    var lowerAndUpperChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    var otherChars = "!@#$^*-+.";
    var charSet = "";
    if(number == true)
        charSet += numberChars;
    if(chars == true)
        charSet += lowerAndUpperChars;
    if(other == true)
        charSet += otherChars;
    return charSet.charAt(getRandomNum(0, charSet.length));
}
//获取日期表达式
function parseExpireString(effectiveAt,expireAt){
    var datetimeString="";
    if((expireAt-effectiveAt)+60000>=(1000 * 60 * 60 * 24)){
        datetimeString="链接" + Math.ceil((expireAt-effectiveAt)/(1000 * 60 * 60 * 24))+"天后失效";
    }else if((expireAt-effectiveAt)<(1000 * 60 * 60 * 24)&&(expireAt-effectiveAt)>(1000 * 60 * 60)){
        datetimeString="链接" + Math.ceil((expireAt-effectiveAt)/(1000 * 60 * 60 ))+"小时后失效";
    }else if((expireAt-effectiveAt)<(1000 * 60 * 60 )&&(expireAt-effectiveAt)>(1000 * 60 )){
        datetimeString="链接" + Math.ceil((expireAt-effectiveAt)/(1000 * 60 ))+"分钟后失效";
    }else{
        datetimeString="链接已失效";
        // $('#linkDiv').remove();
    }
    return datetimeString;
}
function getAccessCode(length) {
    var rc = getRandomChar(true, false, false);
    rc = rc + getRandomChar(false, true, false);
    rc = rc + getRandomChar(false, false, true);
    
    for(var idx = 3; idx < length; idx++) {
        rc = rc + getRandomChar(true, true, true);
    }
    
    var arr_str = rc.split("");
    for(var i = 0; i < 50; i++) {
        var idx1 = getRandomNum(0, length);
        var idx2 = getRandomNum(0, length);
        
        if(idx1 == idx2) {
            continue;
        }
        
        var tempChar = arr_str[idx1];
        arr_str[idx1] = arr_str[idx2];
        arr_str[idx2] = tempChar;
    }
    
    return arr_str.join("");
}
// 刷新
function refreshAccessCode() {
    $("#accessCode").empty();
    $("#accessCode").text(getAccessCode(8));
}
function gotoCreatePage(){
    $("#linkFileListDiv").hide();
    $("#addLinkFileDiv").show();
    refreshAccessCode();
    if($("#download").val()=="off"){
        $("#downloadSwitch").click();
    }

}
// 判断是否有外链加载
function getLink(row, success){
    $("#linkDiv .weui-flex").empty();
    $.ajax({
        type: "GET",
        url: host +"/ufm/api/v2/links/"+ row.ownedBy + "/" + row.id,
        error: handleError,
        success: function(data) {
            var links = data.links;
            var $list = $("#linkDiv");
            var $template = $("#linkTemplate");
            if(links.length>0){
                $("#linkFileListDiv").show();
                $("#addLinkFileDiv").hide();
            }else{
                gotoCreatePage();
            }
            
            for (var i = 0; i < links.length; i++) {
                console.log(links[i]);
                links[i].expireString=parseExpireString(links[i].effectiveAt,links[i].expireAt);
                if(links[i].expireString!="链接已失效"){
                    links[i].index = i + 1;
                    links[i].effectiveAt = formatDateTime(links[i].effectiveAt);
                    $template.template(links[i]).appendTo($list);
                    $("#"+links[i].id).data("data",links[i]);
                    fillLinkDiv(links[i],i);
                }
               
            }
            $(".deleteDiv").click(function(event){
                 event.stopPropagation();
            }) 

            if(success){
            	success()
            }
            // linkDialog.show()
            // tost.hide()
        }
    });
}
// 有效期
function getExpirTime() {
    var timeText = $("#validityChoose").val();
    var date = new Date();
    var timestamp = new Date().getTime();
    if(timeText == "永久有效") {
        return "";
    }
    if(timeText == "一周") {
        var ms = 7 * (1000 * 60 * 60 * 24)
        var newDate = new Date(date.getTime() + ms);
        return newDate;
    } else if(timeText == "一天") {
        var ms = 1 * (1000 * 60 * 60 * 24)
        var newDate = new Date(date.getTime() + ms);
        return newDate;
    } else if(timeText == "一个月") {
        var ms = 30 * (1000 * 60 * 60 * 24)
        var newDate = new Date(date.getTime() + ms);
        return newDate;
    } else {
        var timeText = timeText;
        timestamp = new Date(timeText.replaceAll("-", "/"));
        return timestamp;
    }
}
// 设置外链
function setLink(row, success) {
    var accessCodeMode = $("input[name='accessCodeMode']:checked").val();
    var role;
    if($("#download").val() == "on") {
        role = "viewer";
    } else {
        role = "previewer";
    }
    var accessCode = $("#accessCode").val();
    if(accessCodeMode == "staticMode") {
        accessCodeMode = "static";
        accessCode = "";
    } else if(accessCodeMode == "randomMode") {
        accessCodeMode = "static";
        accessCode = $("#accessCode").text();
    } else {
        accessCodeMode = "mail";
    }
    var timeText = $("#validityChoose").val();
    var expireAt = "";
    if(timeText != "") {
        expireAt = getExpirTime();
    }
    var defaultlinKset = {
        accessCodeMode : accessCodeMode,
        plainAccessCode : accessCode,
        role : role,
    }
    if(timeText != "永久有效") {
        defaultlinKset.expireAt = expireAt.getTime();
        var effectiveAt = new Date().getTime();
        if(effectiveAt > defaultlinKset.expireAt){
            $.Alert("有效日期不能晚于当前日期!");
            return false;
        }
    }
    var url = host + "/ufm/api/v2/links/" + row.ownedBy + "/" + row.id;
    if(linkCode != null && linkCode != "") {
        url = ctx+"/share/updateLink/" + row.ownedBy + "/" + row.id + "?linkCode=" + linkCode;
    }
    $.ajax({
        type : "POST",
        url : url,
        data : JSON.stringify(defaultlinKset),
        error : function(request) {
              $.Alert("单个文件链接不能超过三个","forbidden");

        },
        success : function(data) {
            // $.Alert("创建链接成功");
            
            //链接列表中添加新增链接
            var $list = $("#linkDiv");
            var $template = $("#linkTemplate");
            if(data != null){
                $("#linkFileListDiv").show();
                $("#addLinkFileDiv").hide();
            }
            data.effectiveAt=formatDateTime(new Date().getTime());
            data.expireString=parseExpireString(new Date().getTime(),data.expireAt);
            $template.template(data).appendTo($list);
            $("#"+data.id).data("data",data);
            fillLinkDiv(data,0);
            
            $("#linkDiv").find("div").last().click();
            
            $(".deleteDiv").click(function(event){
                 event.stopPropagation();
            })

            if(success){
            	success()
            }
        }
    });
}

function initClipboard() {
    var clipboard = new Clipboard('#btn_copy', {
        text : function() {
            return $("#accessCode").text();
        }
    });
    clipboard.on('success', function(e) {
        // e.stopPropagation()
        $.Tost("复制成功").show().autoHide(1000);
    });
    clipboard.on('error', function(e) {
        $.Tost("复制失败！请手动复制").show().autoHide(1000);
    });

    var clipboards = new Clipboard('.copy_btn');
    clipboards.on('success', function(e) {
        $.Tost("复制成功").show().autoHide(1000);
    });
    clipboards.on('error', function(e) {
        $.Tost("复制失败！请手动复制","forbidden");
    });
}
function formatDateTime(inputTime) {
    var date = new Date(inputTime);
    var y = date.getFullYear();
    var m = date.getMonth() + 1;
    m = m < 10 ? ('0' + m) : m;
    var d = date.getDate();
    d = d < 10 ? ('0' + d) : d;
    var h = date.getHours();
    h = h < 10 ? ('0' + h) : h;
    var minute = date.getMinutes();
    var second = date.getSeconds();
    minute = minute < 10 ? ('0' + minute) : minute;
    second = second < 10 ? ('0' + second) : second;
    return y + '-' + m + '-' + d + ' ' + h + ':' + minute;
};