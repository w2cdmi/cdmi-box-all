var config = require("../config.js");
var session = require("../../session.js");
var httpclient = require("httpclient.js");

// 获取快捷文件夹的所有目录
function getShortcutFolder(userId, callback) {
    var header = {
        Authorization: getApp().globalData.token,
        'content-type': 'application/json'
    };
    var data = {
        'order': [{ field: "type", direction: "ASC" }, { field: "modifiedAt", direction: "DESC" }],
    }
    return httpclient.post(config.host + '/ufm/api/v2/folders/' + userId + '/shortcut/list', data, header, callback);
}

// 删除快捷文件夹
function deleteShortcutFolder(param, callback) {
    var { ownerId, rowId } = param
    var header = {
        Authorization: getApp().globalData.token,
        'content-type': 'application/json'
    };

    return httpclient.remove(config.host + '/ufm/api/v2/folders/' + ownerId + '/shortcut/' + rowId, {}, header, callback);
}

module.exports = {
    getShortcutFolder,
    deleteShortcutFolder
};
