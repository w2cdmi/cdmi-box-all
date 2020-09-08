var fileClient = require("../module/file.js");
var utils = require("../module/utils.js");

//将最近浏览记录转换为内部数据
function convertRecentRecord(data, page) {
    var files = [];
    var previewImageUrlPromises = [];
    var tempFiles = fileClient.convertFileList(data.files);   //获取图片下载地址
    page.data.previewImageUrls = tempFiles.previewImageUrls;    //不分页
    for (var i = 0; i < data.files.length; i++) {
        var row = data.files[i];
        var file = {};
        file.name = row.name;
        file.type = row.type;
        file.nodeId = row.id;
        file.ownerId = row.ownedBy;
        file.ownedBy = row.ownedBy;
        file.id = row.id;
        if (row.thumbnailUrlList.length == 0) {
            file.icon = utils.getImgSrc(row);
            file.shareIcon = utils.getImgSrcOfShareCard(row.name);
        } else {
            file.icon = utils.replacePortInDownloadUrl(row.thumbnailUrlList[0].thumbnailUrl);
            file.shareIcon = utils.replacePortInDownloadUrl(row.thumbnailUrlList[1].thumbnailUrl);
        }
        file.fileSize = utils.formatFileSize(row.size);
        file.modifiedTime = utils.formatNewestTime(row.modifiedAt);
        file.size = row.size;
        file.linkCode = row.linkCode;
        files.push(file);

    }

    return files;
}

//将最近分享记录（我分享的、分享给我的，都是同一格式）转换为内部数据
function convertShareRecord(data) {
    var files = [];
    for (var i = 0; i < data.contents.length; i++) {
        var row = data.contents[i];
        var file = {};
        file.name = row.name;
        if (typeof (row.originalType) != 'undefined') {
            file.type = row.originalType;
        } else {
            file.type = row.type;
        }
        if (typeof (row.originalNodeId) != 'undefined') {
            file.nodeId = row.originalNodeId;
        } else {
            file.nodeId = row.nodeId;
        }
        if (typeof (row.originalOwnerId) != 'undefined') {
            file.ownerId = row.originalOwnerId;
        } else {
            file.ownerId = row.ownerId;//兼容转发记录, 优先使用原始值
        }
        if (typeof (row.thumbnailUrlList) != 'undefined' && row.thumbnailUrlList.length != 0) {
            file.icon = utils.replacePortInDownloadUrl(row.thumbnailUrlList[0].thumbnailUrl);
            file.shareIcon = utils.replacePortInDownloadUrl(row.thumbnailUrlList[1].thumbnailUrl);
        } else {
            file.icon = utils.getImgSrc(row);
            file.shareIcon = utils.getImgSrcOfShareCard(file.name);
        }
        file.modifiedTime = utils.formatNewestTime(row.modifiedAt);
        file.ownerName = row.ownerName;
        file.linkCode = row.linkCode;
        file.shareType = row.shareType;
        file.createId = row.modifiedBy;
        file.desc = row.ownerName;
        file.iNodeId = row.iNodeId;
        file.sharedUserId = row.sharedUserId;

        //数据中包括外链和共享信息，共享设置分享类型为share，不能再次外发
        if (row.iNodeId == -1 && row.shareType == 'link') {
            file.icon = "/disk/images/icon/batch-file-icon.png";
        } else {
            file.shareType = "share";
        }

        files.push(file);
    }

    return files;
}

module.exports = {
    convertRecentRecord,
    convertShareRecord
}