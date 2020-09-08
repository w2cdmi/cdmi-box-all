/* 判断file（包括文件和目录）显示的图片 */
function getImgSrc(file, bigIcon) {
    //目录
    if (file.type < 1) {
      return "/disk/images/icon/" + (bigIcon ? 'batch-share/' : '') + "folder-icon.png";
    }

    var index = file.name.lastIndexOf(".");
    if (index != -1) {
        var fileType = file.name.substring(index + 1).toLowerCase();
        if (fileType == "txt" || fileType == "pdf") {
          return "/disk/images/icon/" + (bigIcon ? 'batch-share/file-' : 'file-') + fileType + ".png";
        } else if (fileType == "DVDRip" || fileType == "mp4" || fileType == "mkv" || fileType == "avi" || fileType == "rm" || fileType == "rmvb" || fileType == "wmv" || fileType == "3gp") {
          return "/disk/images/icon/" + (bigIcon ? 'batch-share/' : '') + "file-mp4.png";
        } else if (fileType == "jpg" || fileType == 'png' || fileType == 'gif' || fileType == 'bmp' || fileType == 'jpeg' || fileType == 'jpeg2000' || fileType == 'tiff' || fileType == 'psd') {
          return "/disk/images/icon/" + (bigIcon ? 'batch-share/' : '') + "file-jpg.png";
        } else if (fileType == "docx" || fileType == "doc") {
          return "/disk/images/icon/" + (bigIcon ? 'batch-share/' : '') + "file-doc.png";
        } else if (fileType == "xlsx" || fileType == "xls") {
          return "/disk/images/icon/" + (bigIcon ? 'batch-share/' : '') + "file-xls.png";
        } else if (fileType == "pptx" || fileType == "ppt") {
          return "/disk/images/icon/" + (bigIcon ? 'batch-share/' : '') + "file-ppt.png";
        } else if (fileType == "rar" || fileType == "zip" || fileType == "7z" || fileType == "cab" || fileType == "iso"){
          return "/disk/images/icon/" + (bigIcon ? 'batch-share/' : '') + "file-rar.png";
        } else if (fileType == "mp3" || fileType == "wma" || fileType == "wav" || fileType == "mod" || fileType == "ra" || fileType == "cd" || fileType == "md" || fileType == "asf" || fileType == "aac" || fileType == "mp3pro"){
          return "/disk/images/icon/" + (bigIcon ? 'batch-share/' : '') + "file-mp3.png";
        }
    }

    return "/disk/images/icon/" + (bigIcon ? 'batch-share/' : '') + "file-undefined.png";
}

/*判断外发文件的图片格式*/
function getImgSrcOfShareCard(name) {
    //目录
    var index = name.lastIndexOf('.');
    if (index != -1) {
        var fileType = name.substring(index + 1, name.length).toLowerCase();
        if (fileType == "DVDRip" || fileType == "mp4" || fileType == "mkv" || fileType == "avi" || fileType == "rm" || fileType == "rmvb" || fileType == "wmv" || fileType == "3gp") {
            return "/disk/images/shares/share-card-mp4.png";
        } else if (fileType == 'jpg' || fileType == 'png' || fileType == 'gif' || fileType == 'bmp' || fileType == 'jpeg' || fileType == 'jpeg2000' || fileType == 'tiff' || fileType == 'psd') {
            return "/disk/images/shares/share-card-jpg.png";
        } else if (fileType == "docx" || fileType == "doc") {
            return "/disk/images/shares/share-card-doc.png";
        } else if (fileType == "xlsx" || fileType == "xls") {
            return "/disk/images/shares/share-card-xls.png";
        } else if (fileType == "pptx" || fileType == "ppt") {
            return "/disk/images/shares/share-card-ppt.png";
        } else if (fileType == "mp3" || fileType == "wma" || fileType == "wav" || fileType == "mod" || fileType == "ra" || fileType == "cd" || fileType == "md" || fileType == "asf" || fileType == "aac" || fileType == "mp3pro") {
            return "/disk/images/shares/share-card-music.png";
        } else if (fileType == "rar" || fileType == "zip" || fileType == "7z" || fileType == "cab" || fileType == "iso") {
            return "/disk/images/shares/share-card-rar.png";
        } else if (fileType == "txt" || fileType == "pdf") {
            return "/disk/images/shares/share-card-" + fileType + ".png";
        }
    }

    return "/disk/images/shares/share-card-undefined.png";
}

function getWantSizeImg(imgUrl, minHeight, minWidth) {
    var arr = [];
    arr = imgUrl.split("?");
    if (imgUrl == undefined || minHeight == undefined || minWidth == undefined) {
        return "参数有误，请检查！";
    }
    //如果没有后缀
    if (arr.length < 2) {
        var newImgUrl = imgUrl + "?minHeight=" + minHeight + "&minWidth=" + minWidth;
    } else if (arr.length >= 2) {
        arr[1] = "?minHeight=" + minHeight + "&minWidth=" + minWidth;
        var newImgUrl = arr[0] + arr[1];
    }
    return newImgUrl;
}

function isShowThumbnail(name){
    var index = name.lastIndexOf(".");
    if (index != -1) {
        var fileType = name.substring(index + 1).toLowerCase();
        if (fileType == "jpg" || fileType == 'png' || fileType == 'gif' || fileType == 'bmp' || fileType == 'jpeg' || fileType == 'jpeg2000') {
            return true;
        }
    }
    return false;
}

/* 将文件大小转化为带单位的表示 */
function formatFileSize(size) {
    if (size == undefined || size == "" || isNaN(size)) {
        return "0KB";
    }

    if (size < 1024) {
        return "1KB";
    }
    else if (size >= 1024 && size < 1024 * 1024) {
        return (size / 1024).toFixed(2) + "KB";
    }
    else if (size >= 1024 * 1024 && size < 1024 * 1024 * 1024) {
        return (size / 1024 / 1024).toFixed(2) + "MB";
    }
    else {
        return (size / 1024 / 1024 / 1024).toFixed(2) + "GB";
    }
}

/* 将long型的时间转化为带单位的表示 */
// function formatDate(date) {
//   if (date == undefined) {
//     date = new Date();
//   }
//   if (typeof date == 'number') {
//     date = new Date(date);
//   }
//   return date.toLocaleDateString();
// }

/**  
*转换日期对象为日期字符串  
* @param date 日期对象  
*
* @return 符合要求的日期字符串  
*/
function formatDate(date) {
    var pattern = "yyyy-MM-dd";
    return getFormatDate(date, pattern);
}

/**
 * 预览图片链接
 * Question:分页之后，全局变量 预览会变成最后分页的图片链接，我们需要的是页面中所有图片的URI
 */
function preImgAdd(imgsUrl){
    var ImgUrlBefore=wx.getStorageSync('imgUrls');  //保存全局  预览图片  URL列表，下面的方法会覆盖
    getApp().globalData.imgUrls = ImgUrlBefore.concat(ImgUrlBefore);
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
    //暂时使用
    if (date.length < 10) {
        return date;
    }
    if (date.length == 10) {
        return date.replace("/", "-");
    }

    var o = {
        "M+": date.getMonth() + 1,
        "d+": date.getDate(),
        "h+": date.getHours(),
        "m+": date.getMinutes(),
        "s+": date.getSeconds(),
        "q+": Math.floor((date.getMonth() + 3) / 3),
        "S": date.getMilliseconds()
    }
    if (/(y+)/.test(pattern)) {
        pattern = pattern.replace(RegExp.$1, (date.getFullYear() + "").substr(4 - RegExp.$1.length));
    }
    for (var k in o) {
        if (new RegExp("(" + k + ")").test(pattern)) {
            pattern = pattern.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
        }
    }

    return pattern;
}

//使用"SHA-256"加密后再用Base64编码
function sha256AndBase64(s1, s2) {
    return "sha256AndBase64";
    // return s1 + s2;
}

//OSS模块的下载地址中含有端口，小程序不支持端口，将下载地址中的端口号替换为oss，然后由nginx路由到OSS模块
function replacePortInDownloadUrl(url, replacement) {
    // var split = url.split(":");

    // //含有端口号： "https://www.storbox.cn:4434/api/72BBE7171D3C658B52B68F658853987B946AB3854B1301785C85D32D/9eb2f2c488f080ed14f144de50521193/88.png"
    // if (split.length == 3) {
    //     //未指定地址，使用默认oss
    //     replacement = replacement || "oss";

    //     var index = split[2].indexOf("/");
    //     if (index != -1) {
    //         return split[0] + ":" + split[1] + "/" + replacement + split[2].substring(index);
    //     } else {
    //         return split[0] + ":" + split[1] + "/" + replacement;
    //     }
    // }

    return url;
}

//最近预览时间
function formatNewestTime(timestamp) {
    if (timestamp == undefined) {
        return "";
    }

    var date = new Date().getTime() - timestamp;
    if (date < 1000){
        return "1秒前";
    }else if(0 < date && date < 60 * 1000) {
        return Math.floor(date / 1000) + "秒前";
    } else if (date < 60 * 1000 * 60) {
        return Math.floor(date / 1000 / 60) + "分钟前";
    } else if (date < 60 * 1000 * 60 * 24) {
        return Math.floor(date / 60 / 1000 / 60) + "小时前";
    } else if (date < 60 * 1000 * 60 * 24 * 30) {
        return Math.floor(date / 60 / 1000 / 60 / 24) + "天前";
    } else {
        return getFormatDate(timestamp);
    }
}
//将账号类型转换成名字
function formatAccountType(accountType){
    var name = "";
    if (typeof (accountType) == 'undefined' || accountType === ""){
        return name;
    }
    switch (accountType){
        case 0:
            name = "普通";
            break;
        case 1:
            name = "主管";
            break;
        case 2:
            name = "员工";
            break;
        case 3:
            name = "外协";
            break;
        case 101:
            name = "黄金会员";
            break;
        case 102:
            name = "铂金会员";
            break;
        case 103:
            name = "钻石会员";
            break;
    }
 return name;
}
//根据class选择器，获取元素宽高
function getSelectorQuery(className){
    return wx.createSelectorQuery().select(className).boundingClientRect(function (rect) {
        rect.width
        rect.height
    }).exec();
}

//预览图片列表
function previewImage(urls, url){
    if (urls == undefined || urls.length == 0){
        urls = url;
    }
    if(typeof(url) == 'undefined'){
        url = "";
        return;
    }
    wx.previewImage({
        current: url,
        urls: urls
    });
}

//是否支持的文件大小
function isPreviewFileSize(size) {
    if (size < 1024 * 1024 * 10) {
        return true;
    }
    return false;
}

//将返回结果统一转换成内部的数据结构
function translate(data) {
    var spaceList = [];
    for (var i = 0; i < data.memberships.length; i++) {
        var row = data.memberships[i];
        var space = {};

        space.id = row.teamId;
        space.name = row.teamspace.name;
        space.ownerId = row.teamspace.ownedBy;
        space.ownerName = row.teamspace.ownedByUserName;
        space.memebers = row.teamspace.curNumbers;

        spaceList.push(space);
    }

    return spaceList;
}

//字符串 ""
function stringIsNotEmpty(param){
    if (typeof (param) != 'undefined' && param != ''){
        return true;
    }
    return false;
}
//对象为空 {}
function objectIsEmpty(param) {
    if (typeof (param) == 'undefined' || param == '' || param == {}){
        return true;
    }
    return false;
}

module.exports = {
    getImgSrc,
    formatFileSize,
    formatDate,
    sha256AndBase64,
    getImgSrcOfShareCard,
    formatNewestTime,
    replacePortInDownloadUrl,
    getFormatDate,
    formatAccountType,
    getSelectorQuery,
    isShowThumbnail,
    previewImage,
    isPreviewFileSize,
    translate,
    getWantSizeImg
};