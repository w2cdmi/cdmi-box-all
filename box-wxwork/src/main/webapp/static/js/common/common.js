function pushHistory() {
    window.addEventListener("popstate", function(e) {
        console.log(self.location)
        if(!self.location.href.endsWith("##")) {
            console.log("reload !!!")
            self.location.reload();
        }
    }, false);

    var state = {
        title : "",
        url : "##"
    };
    window.history.replaceState(state, "", "##");
}

/*媒介根元素*/
window.addEventListener(('orientationchange' in window ? 'orientationchange' : 'resize'), (function() {
    function c() {
      var d = document.documentElement;
      var cw = d.clientWidth || 750;
      d.style.fontSize = (20 * (cw / 375)) > 40 ? 40 + 'px' : (20 * (cw / 375)) + 'px';
    }
    c();
    return c;
  })(), false);

/*增强Template7功能：JQuery对象（Template7脚本）直接替换变量，并生成新的JQuery对象。*/
if($){
    $.fn.template = function (data) {
        if (this._compile === undefined) {
            this._compile = $.t7.compile(this.html());
        }

        return $(this._compile(data));
    };
}

String.prototype.replaceAll  = function(s1,s2){    
    return this.replace(new RegExp(s1,"gm"),s2);    
};


$(function () {
    //如果没有从会话中获取到corp，尝试从当前cookie中获取
    if(corpId === undefined || corpId === null || corpId === '') {
        corpId = getCookie("corpId");
    }

    //初始化AJAX请求，401错误，主动跳转到登录页面。
    initAjax();

    // pushHistory();
});

function initAjax() {
    $.ajaxSetup({
        beforeSend: function(xhr) {
            xhr.setRequestHeader("Authorization", userToken);
            xhr.setRequestHeader("Content-Type","application/json");
        },
        complete: function(xhr, status) {
            //401为未登录或会话超时错误，跳转到log
            if(xhr.status === 401) {
                window.location = ctx + '/login';
            }
        }
    });
}

function initWwJsJdkAndInvoke(callback){
    //无法获取corpId, 没有必要初始化WxWork JS SDK。
    if(corpId === undefined || corpId === null || corpId === "") {
        return;
    }

    $.ajax({
        type: "GET",
        data: {
            url:location.href.split('#')[0]
        },
        url: host + "/ecm/api/v2/wxOauth2/getWxWorkJsApiTicket?corpId=" + corpId,
        error: function (request) {
            //			$.toast("JS-SDK初始化失败");
        },
        success: function (data) {
            if (data != null) {
                wx.config({
                    debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
                    appId: data.appId, // 必填，企业微信的cropID
                    timestamp: data.timestamp, // 必填，生成签名的时间戳
                    nonceStr: data.noncestr, // 必填，生成签名的随机串
                    signature: data.signature,// 必填，签名
                    jsApiList: ["chooseImage", "previewImage", "uploadImage", "downloadImage","onMenuShareAppMessage",
                        "onMenuShareWechat","showOptionMenu","showMenuItems","showAllNonBaseMenuItem","hideOptionMenu",
                        "hideMenuItems","hideAllNonBaseMenuItem","previewFile"] // 必填，需要使用的JS接口列表
                });
                wx.error(function(res){
                    // alert("wx.config failed." + res);
                });
                wx.ready(
                    function () {
                        if(typeof callback === "function") {
                            callback();
                        }
                    }
                );
            }
        }
    });
}

function gotoPage(url,callback) {
    //判断是否有正在执行上传的文件
    var tempUploadingFiles = $(".file-uploading");
    if (tempUploadingFiles.length > 0) {
        $.confirm("正在上传文件, 跳转页面将会中止上传", function () {
            window.location.href = url;
        });
    } else {
        window.location.href = url;
    }
    if(callback){
        callback()
    }
}

function goBack() {
    history.go(-1);
}

function getCookie(name, defaultValue){
    var arr = document.cookie.match(new RegExp("(^| )" + name + "=([^;]*)(;|$)"));
    if(arr != null) {
        return decodeURIComponent(arr[2]);
    } else if(defaultValue != undefined) {
        return defaultValue;
    }

    return null;
}

function setCookie(name, value) {
    var exp = new Date("December 31, 9998");
    document.cookie = name + "=" + escape(value) + ";expires=" + exp.toGMTString();
}

function delCookie(name){
    var exp = new Date();
    exp.setTime(exp.getTime() - 1);
    var cval=getCookie(name);
    if(cval!=null) document.cookie= name + "="+cval+";expires="+exp.toGMTString();
}

function getStorage(name, defaultValue){
    var value = localStorage.getItem(name);
    if(value != undefined && value != null) {
        return value;
    } else if(defaultValue != undefined) {
        return defaultValue;
    }

    return null;
}

function setStorage(name, value) {
    localStorage.setItem(name, value);
}

function removeStorage(name){
    localStorage.removeItem(name);
}

/**  
*转换日期对象为日期字符串  
* @param date 日期对象  
*
* @return 符合要求的日期字符串  
*/  
function getSmpFormatDate(date) {
  var pattern = "yyyy-MM-dd hh:mm:ss";
  return getFormatDate(date, pattern);
}

/**  
*转换日期对象为日期字符串  
* @param l long值  
* @param pattern 格式字符串,例如：yyyy-MM-dd hh:mm:ss  
* @return 符合要求的日期字符串  
*/  
function getFormatDate(date, pattern) {
  if (date == undefined) {
      date = new Date();
  }
  if (typeof date == 'number') {
  	date = new Date(date);
  }
  if (pattern == undefined) {
      pattern = "yyyy-MM-dd hh:mm:ss";
  }
  return date.format(pattern);
}

//扩展Date的format方法 
Date.prototype.format = function (format) {
  var o = {
      "M+": this.getMonth() + 1,
      "d+": this.getDate(),
      "h+": this.getHours(),
      "m+": this.getMinutes(),
      "s+": this.getSeconds(),
      "q+": Math.floor((this.getMonth() + 3) / 3),
      "S": this.getMilliseconds()
  }
  if (/(y+)/.test(format)) {
      format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
  }
  for (var k in o) {
      if (new RegExp("(" + k + ")").test(format)) {
          format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
      }
  }
  return format;
}

function formatFileSize(size) {
    var sizeStr;
    if (size < 1024) {
        sizeStr = size + "B";
    }
    else if (size >= 1024 && size < 1024 * 1024) {
        sizeStr = (size / 1024).toFixed() + "KB";
    }
    else if (size >= 1024 * 1024 && size < 1024 * 1024 * 1024) {
        sizeStr = (size / 1024 / 1024).toFixed(2) + "MB";
    }
    else {
        sizeStr = (size / 1024 / 1024 / 1024).toFixed(2) + "GB";
    }

    return sizeStr;
}

function formatDateSize(size) {
    var sizeStr;
    if (size < 60) {
        sizeStr = size + "s";
    }
    else if (size >= 60 && size < 60 * 60) {
        sizeStr = (size / 60).toFixed(2) + "m";
    }
    else if (size >= 60 * 60 && size < 60 * 60 * 60) {
        sizeStr = (size / 60 / 60).toFixed(2) + "h";
    }
    else {
        sizeStr = (size / 60 / 60 / 60).toFixed(2) + "d";
    }

    return sizeStr;
}

function getLocalTime(serverTime){
	var d = new Date(serverTime);
	return getSmpFormatDate(d);	
}

function startUpload(){
	bartimer = window.setInterval(function(){setProgress()},100);
	isStart = false;
}

function setProgress(){
	var topCurrentWidth = $('.midd').width()+topUnit;
	if((parseInt($('.midd').width()+topUnit)) > topTotalWidth){
		$(".pre").html("完成");
		clearInterval(bartimer);
		return;
	}
	$('.midd').width(topCurrentWidth);
	$(".pre").html(parseInt(topCurrentWidth/topTotalWidth*100) + "%");
	var currentWidth = $('#xjdt').width()+unit;
	$('#xjdt').width(currentWidth);
}

function setRootCookie(name,value) {
	delCookie(name);
    var exp  = new Date("December 31, 9998");
    document.cookie = name + "="+ escape (value) + ";path=/;expires=" + exp.toGMTString();
}

function getRootCookie(name){
    var arr = document.cookie.match(new RegExp("(^| )"+name+"=([^;]*)(;|$)"));
    if(arr != null) {
    	return unescape(arr[2]);
    }
    return null;
} 

function getImgHtml(type, fileName, shareStatus, secret) {
    if (type != 1){
        if(shareStatus){
        	return "folder-share-icon";
        }
        if(secret) {
            return "folder-secret"
        }

        // if(!acl){
        //     return "folder-forbid";
        // }

        return "folder-icon";
    }

    return "file-" + recognizedFileType(fileName);
}

function getImgHtmlOther(type, fileName, shareStatus) {
    if (type != 1){
        if(shareStatus){
            return "folder-share-icon-other";
        }
        return "folder-icon-other";
    }

    return "file-" + recognizedFileType(fileName) + "-other";
}

function getImgSrc(type, fileName) {
    if (type == 0 || type == -5 ){
        return "/static/skins/default/img/icon/folder-icon.png";
    }

    return "/static/skins/default/img/icon/file-" + recognizedFileType(fileName) + ".png";
}

/**/
function recognizedFileType(name) {
    var index = name.lastIndexOf(".");
    if (index !== -1) {
        var fileType = name.substring(index + 1).toLowerCase();
        if (fileType === "doc" || fileType === "ppt" || fileType === "xls" || fileType === "docx" || fileType === "pptx" || fileType === "xlsx" ||
            fileType === "rar" || fileType === "mp3" || fileType === "txt" || fileType === "pdf" || fileType === "jpg" || fileType === "png" ||
            fileType === "gif" || fileType === "avi" || fileType === "exe" || fileType === "jpeg" || fileType === "mp4") {
            return fileType;
        }
    }

    return "undefined";
}

function handleError(xhr) {
    var status = xhr.status;
    var response = JSON.parse(xhr.responseText);

    if (response.code === "SameParentConflict") {
        $.toast("相同目录不能进行操作", "cancel")
    } else if (response.code === "NoSuchItem") {
        $.toast("文件或文件夹不存在", "cancel")
    } else if (response.code === "NoSuchFile") {
        $.toast("文件不存在", "cancel")
    } else if (response.code === "NoSuchFolder") {
        $.toast("文件夹不存在", "cancel")
    } else if (response.code === "NoSuchParent") {
        $.toast("父目录不存在", "cancel")
    } else if (response.code === "NoSuchSource") {
        $.toast("源文件或文件夹不存在", "cancel")
    } else if (response.code === "NoSuchDest") {
        $.toast("目标文件或文件夹不存在", "cancel")
    } else if (response.code === "Forbidden") {
        $.toast("您没有权限进行该操作", "cancel")
    } else if (response.code === "InvalidParameter") {
        $.toast("请求参数错误", "cancel")
    } else if (response.code === "LinkExistedConflict") {
        $.toast("外链已存在", "cancel")
    } else if (response.code === "LinkExpired") {
        $.toast("外链已过期", "cancel")
    } else if (response.code === "LinkNotEffective") {
        $.toast("外链未生效", "cancel")
    } else if (response.code === "NoSuchLink") {
        $.toast("外链不存在", "cancel")
    } else if (response.code === "NoSuchUser") {
        $.toast("用户不存在", "cancel")
    } else if (response.code === "SubFolderConflict") {
        $.toast("不能移动子目录下", "cancel")
    } else if (response.code === "SameNodeConflict") {
        //复制或移动时，目标节点和源节点相同冲突
        $.toast("目标文件夹与源文件夹相同", "cancel")
    } else if (response.code === "SameParentConflict") {
        //复制或移动时，目标节点是源节点的父文件夹
        $.toast("目标文件夹已在该目录下", "cancel")
    } else if (response.code === "UserLocked") {
        $.toast("用户被锁定", "cancel")
    } else if (response.code === "ExistMemberConflict") {
        $.toast("成员已存在", "cancel")
    } else if (response.code === "ExistTeamspaceConflict") {
        $.toast("协作空间已存在", "cancel")
    } else if (response.code === "ExceedMaxLinkNum") {
        $.toast("外链数超过最大限制", "cancel")
    } else if (response.code === "InvalidFileType") {
        $.toast("不支持的文件类型", "cancel")
    } else if (response.code === "ExceedQuota") {
        $.toast("空间容量不足", "cancel")
    } else if (response.code === "ExceedUserAvailableSpace") {
        $.toast("空间容量不足", "cancel")
    } else if (response.code === "UploadSizeTooLarge") {
        $.toast("上传文件大小超过限制", "cancel")
    } else if (response.code === "UploadSizeTooLarge") {
        $.toast("上传文件大小超过限制", "cancel")
    } else if (response.code === "ExsitShortcut") {
        //此消息可以不显示
        // $.toast("快捷目录已经存在")
    } else if(response.code === "PreviewNotSupported"){
        $.toast("此文件不支持预览")
    } else {
        if (status === 400 && response === 'Forbidden') {
            $.toast("您没有权限进行该操作", "cancel");
        }else  if (status === 403) {
            $.toast("您没有权限进行该操作", "cancel");
        } else if (status === 404) {
            $.toast("文件或文件夹不存在", "cancel");
        } else {
            $.toast("操作失败", "cancel");
        }
    }
}

function getAclByRole(roleName){
	var acl="";
	switch (roleName) {
	case "previewer":
		acl="预览"
		break;
	case "uploader":
		acl="预览 上传"
		break;
	case "viewer":
		acl="预览 下载"
		break;
	case "downLoader":
		acl="预览 下载"
		break;
	case "uploadAndView":
		acl="预览 上传 下载"
		break;
	case "editor":
		acl="预览 上传 下载"
		break;
	case "auther":
		acl="拥有者"
		break;

	default:
		break;
	}
	return acl;
}
// 文档
function isFilePreviewable(fileName) {
    var index = fileName.lastIndexOf(".");
    if (index !== -1) {
        var fileType = fileName.substring(index + 1).toLowerCase();
        if (fileType === "doc" || fileType === "ppt" || fileType === "xls" || fileType === "docx" || fileType === "pptx" || fileType === "xlsx" ||
            fileType === "txt" || fileType === "pdf") {
            return true
        }
    }
}
// 图片
function isImg(imgName) {
    var index = imgName.lastIndexOf(".");
    if (index !== -1) {
        var fileType = imgName.substring(index + 1).toLowerCase();
        if (fileType === "png" || fileType === "jpg" || fileType === "jpeg" || fileType === "bmp") {
            return true
        }
    }
}
// 视频
function isVideo(videoName) {
    var index = videoName.lastIndexOf(".");
    if (index !== -1) {
        var fileType = videoName.substring(index + 1).toLowerCase();
        if (fileType === "mp4" || fileType === "webm") {
            return "video/" + fileType;
        }

        if(fileType === "ogg" || fileType === "ogm" || fileType === "ogv") {
            return "video/ogg";
        }
    }

    return null;
}
function footAddLayel(li) {
    $(li).find("i").removeClass("add_operation_img")
    $("#add_layel").show()
    $("#add_layel").click(function () {
        $("#add_layel").hide()
        $("#add_operation").find("i").addClass("add_operation_img")
    })

}
function cancelLayel() {
    $("#add_layel").show()
    $("#add_operation").find("i").addClass("add_operation_img")
}

function layelTitle(clazz, ownerName, time) {
    var $title = $(".weui-actionsheet__title");

    $title.prepend("<i class=" + clazz + "></i>");
    if(ownerName == undefined){
        $title.append("<div><span>来源收件箱</span><span>|</span><span>" + time + "</span></div>")
        return;
    }
    if(time !== undefined && time !== null && time !== "") {
        $title.append("<div><span>" + ownerName + "</span><span>|</span><span>" + time + "</span></div>")
    } else{
        $title.append("<div><span>" + ownerName + "</span></div>")
    }
}

// 最近浏览操作
function showActionSheet(e) {
    e.stopPropagation()
    var $target = $(e.currentTarget);
    var node = $target.data("node");
    console.log(node)
    var actions=[];
    if(curUserId==node.ownedBy){
        actions.push(
            {
                text: "共享",
                className: "color-warning",
                onClick: function() {
                    showShareDialog(node);
                }
            }
        )
    }
    actions.push({
        text: "外发",
        className: "color-warning",
        onClick: function() {
            showLinkDialog(node);
        }
    });
    actions.push({
        text: "查看详情",
        className: "color-warning",
        onClick: function() {
            gotoPage(ctx+"/files/gotoFileInfo/"+ node.ownedBy + "/" + node.id + "?type=" + node.type + "&fileName=" + node.name);
        }
    });
    actions.push({
        text: "删除",
        className: 'color-danger',
        onClick: function() {
            deleteRecent(node);
        }
    })
    $.actions({
        title:node.name,
        onClose: function() {
        },
        actions: actions
    });
    layelTitle(node.imgClass,node.menderName,node.modifiedAt)

}
// 快捷目录操作
function shortSheet(e) {
    e.stopPropagation();
    var node = $(e.currentTarget).data("node")
    console.log(node)
    $.actions({
        title:node.nodeName,
        onClose: function() {
        },
        actions: [
            {
                text: "移除",
                className: 'color-danger',
                onClick: function() {
                    deleteSortcutFolder(node);
                }
            }
        ]
    });

    layelTitle(node.imgClass, node.ownerName, node.modifiedAt)
}

function creatFolder() {
    $("#createFolderId").click(function(){
        if(typeof(listFile) === "function"){
            newFolderDialog(listFile);
        }else{
            newFolderDialog();
        }
    });
}
function previewFile(node) {
    $.ajax({
        type: "GET",
        async: false,
        url: "/ufm/api/v2/files/" + ownerId + "/" + node.id + "/preview",
        error: function (request) {
        },
        success: function (data) {
            window.open(data.url,"_blank")
        }
    });
}
function parseQueryString() {
    var url = location.search; //获取url中"?"符后的字串
    var theRequest = {};
    if (url.indexOf("?") !== -1) {
        var str = url.substr(1);
        strs = str.split("&");
        for(var i = 0; i < strs.length; i ++) {
            theRequest[strs[i].split("=")[0]]=decodeURI(strs[i].split("=")[1]);
        }
    }
    return theRequest;
}

// 获取企业文库

function bindEnterpriseLibraryUrl() {
    var params = {
        "type": 4,
        "userId": curUserId,
    };
    $.ajax({
        type: "POST",
        url: host + "/ufm/api/v2/teamspaces/items",
        data: JSON.stringify(params),
        error: function (xhr, status, error) {
            $.toast("查询企业文库失败。", "cancel");
        },
        success: function (data) {
            var space = data.memberships[0].teamspace;
            $("#goToBussiness").click(function () {
                gotoPage(ctx + "/teamspace/file/" + space.id)
            })
        }
    });
}

// 首页共享和外发操作
function indexShareOpperation(th) {
        var node = $(th).parent().data("node")
        showShareDialog(node)
}
function indexShareLinkOpperation(th) {
        var node = $(th).parent().data("node")
        showLinkDialog(node)
}
function sortShowHide() {
    $("#folderOrder").click(function (e) {
        e.stopPropagation()
        $("#sortRadio").toggle()
    })
    $("body").click(function () {
        $("#sortRadio").hide()
    })
}

function getNodePermissionAndInvoke(ownerId, nodeId, userId, callback) {
    var url = host + "/ufm/api/v2/permissions/" + ownerId + "/" + nodeId + "/" + userId;
    $.ajax({
        type: "GET",
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader("Authorization", userToken);
        },
        error: function (xhr, status, error) {
            console.error(error);
        },
        success: function (data) {
            if(typeof callback === "function") {
                callback(data.permissions);
            }
        }
    });
}

function getLinkPermission(ownerId, nodeId, linkCode, accessCode) {
    var permission = null;
    $.ajax({
        type: "GET",
        url:  host + "/ufm/api/v2/permissions/" + ownerId + "/" + nodeId,
        async: false,
        beforeSend: function (xhr) {
            var token = "link," + linkCode;
            if(accessCode !== undefined && accessCode !== null && accessCode !== "") {
                token += ("," + accessCode);
            }

            xhr.setRequestHeader("Authorization", token);
        },
        error: function (data) {
        },
        success: function (data) {
            if (typeof(data) == 'string' && data.indexOf('<html>') != -1) {
                window.location.href = ctx + "/logout";
                return;
            }
            permission = data.permissions;
        }
    });

    return permission;
}

// 显示暂无文件
function showNotFile() {
    var html="<div class='blank-file-list'>"
    html += "<i class='all-not-file'>"
    html += "<img src='"+ctx+"/static/skins/default/img/not-recent1x.png'/>"
    html += "</i>"
    html += "<p class='all-not-file-title'>暂无文件</p>"
    html += "</div>"
    $("#fileList").prepend(html)
}

// 预览
function previewAllFiles(ownerId,nodeId){
    var url = null
    $.ajax({
        type: "GET",
        async: false,
        url: host + "/ufm/api/v2/files/" + ownerId + "/" + nodeId + "/preview",
        error: handleError,
        success: function(data) {
            url = data.url
        }
    });
    return url
}

function imgClick(t) {
    var pswpElement = document.querySelectorAll('.pswp')[0];
    var index = $(t).children().find(".fileImg").attr("data-index")
    var items = [];
    // var images = new image()
    var getItems = function () {
        var aDiv = $("#fileList");
        for (var i = 0; i < aDiv.children().find(".fileImg").length; i++) {
            var img = aDiv.children().find(".fileImg");
            var item = {
                src: $(img[i]).attr("src"),
                w: $(img[i]).width(),
                h: $(img[i]).height()
            };
            items.push(item);
        }
    };

    getItems();
    var options = {
        index: parseInt(index),
        bgOpacity:0.85,
        tapToClose:true
    };

    var gallery = new PhotoSwipe( pswpElement, PhotoSwipeUI_Default, items, options);
    gallery.init();
}

function videoPreview(ownerId,nodeId,videoable) {
    $.ajax({
        type: "GET",
        async: false,
        url: host + "/ufm/api/v2/files/"+ownerId+"/"+ nodeId +"/preview",
        error: handleError,
        success: function (data) {
            var url = data.url
            $("video").attr("src",url)
            $("video").attr("type",videoable)
            $(".playvideo").show()
            $("video").get(0).play()
            zymedia('video',{autoplay: true});
            $("#modelView").click(function () {
                $(".playvideo").hide()
                $("video").get(0).currentTime = 0
                $("video").get(0).pause()
            })

        }
    })
}

/**
 * [isMobile 判断平台]
 * @param test: 0:iPhone    1:Android
 */
function ismobile(test){
    var u = navigator.userAgent, app = navigator.appVersion;
    if(/AppleWebKit.*Mobile/i.test(navigator.userAgent) || (/MIDP|SymbianOS|NOKIA|SAMSUNG|LG|NEC|TCL|Alcatel|BIRD|DBTEL|Dopod|PHILIPS|HAIER|LENOVO|MOT-|Nokia|SonyEricsson|SIE-|Amoi|ZTE/.test(navigator.userAgent))){
        if(window.location.href.indexOf("?mobile")<0){
            try{
                if(/iPhone|mac|iPod|iPad/i.test(navigator.userAgent)){
                    return '0';
                }else{
                    return '1';
                }
            }catch(e){}
        }
    }else if( u.indexOf('iPad') > -1){
        return '0';
    }else{
        return '1';
    }
};