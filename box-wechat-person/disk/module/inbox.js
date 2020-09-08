var config = require("../config.js");
var session = require("../../session.js");
var httpclient = require("httpclient.js");

// 获取收件箱列表(根据收件箱的ID查询该收件箱下的文件)
function getFolderList(param, callback) {
    var { ownerId, folderId, offset, LIMIT, linkCode } = param
    var header = {
        Authorization: getApp().globalData.token,
        'content-type': 'application/json'
    };
    var data = {
        'offset': offset,
        'limit': LIMIT,
        'order': [{ field: "type", direction: "ASC" }, { field: "modifiedAt", direction: "DESC" }],
        'thumbnail': [{ width: 96, height: 96 }, { width: 250, height: 200 }]
    }
    if (linkCode) {
        header = {
            Authorization: 'link,' + linkCode,
            'content-type': 'application/json'
        };
        data = {
            'offset': offset,
            'limit': LIMIT,
            'order': [{ field: "type", direction: "ASC" }, { field: "modifiedAt", direction: "DESC" }],
            'thumbnail': [{ width: 96, height: 96 }, { width: 250, height: 200 }],
            'createdBy': getApp().globalData.cloudUserId
        }
    }
    return httpclient.post(config.host + '/ufm/api/v2/folders/' + ownerId + '/' + folderId + '/items', data, header, callback);
}

// 查看收件箱是否存在
function hasInbox(ownerId, folderId, callback) {

}

function preUpload(ownerId) {
    var header = {
        Authorization: getApp().globalData.token,
        'content-type': 'application/json'
    };
    return httpclient.get(config.host + '/ufm/api/v2/files/' + ownerId, {}, header, callback);
}

// 获取收件箱信息
function getInboxFolder(ownerId, callback) {
    var header = {
        Authorization: getApp().globalData.token,
        'content-type': 'application/json'
    };
    return httpclient.get(config.host + '/ufm/api/v2/folders/' + ownerId + '/getInboxFolder', {}, header, callback);
}

// 创建收件箱
function addFolderName(ownerId, data, callback) {
    var header = {
        Authorization: getApp().globalData.token,
        'content-type': 'application/json'
    };
    return httpclient.post(config.host + '/ufm/api/v2/folders/' + ownerId, data, header, callback);
}

// 获取收件箱link
function getInboxLink(ownerId, folderId, callback) {
    var header = {
        Authorization: getApp().globalData.token,
        'content-type': 'application/json'
    };
    return httpclient.get(config.host + '/ufm/api/v2/links/' + ownerId + '/' + folderId, {}, header, callback);
}

function setInboxLink(ownerId, folderId, callback) {
    var header = {
        Authorization: getApp().globalData.token,
        'content-type': 'application/json'
    };
    var data = {
        "accessCodeMode": "static",
        "plainAccessCode": "",
        "role": "uploader"
    }
    return httpclient.post(config.host + '/ufm/api/v2/links/' + ownerId + '/' + folderId, data, header, callback);
}

// 删除收件箱
function deleteFolderName(ownerId, folderId, callback) {
    var header = {
        Authorization: getApp().globalData.token,
        'content-type': 'application/json'
    };

    return httpclient.remove(config.host + '/ufm/api/v2/folders/' + ownerId + '/' + folderId, {}, header, callback);
}

// 删除收到的文件
function deleteFile(ownerId, fileId, callback) {
    var header = {
        Authorization: getApp().globalData.token,
        'content-type': 'application/json'
    };
    return httpclient.remove(config.host + '/ufm/api/v2/files/' + ownerId + '/' + fileId, {}, header, callback);
}

module.exports = {
    getInboxFolder,
    getFolderList,
    addFolderName,
    deleteFolderName,
    deleteFile,
    preUpload,
    getInboxLink,
    setInboxLink,
    hasInbox
};
