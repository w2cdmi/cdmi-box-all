var config = require("../config.js");
var httpclient = require("httpclient.js");

function createDefaultLink(ownerId, nodeId, callback) {
    var header = {
        Authorization: getApp().globalData.token,
    };
    var data = {
        isProgram: true
    }
    return httpclient.put(config.host + '/ufm/api/v2/links/program/' + ownerId + '/' + nodeId, data, header, callback);
}

function createBatchFileLink(data, callback) {
    var header = {
        Authorization: getApp().globalData.token,
    };
    return httpclient.post(config.host + '/ufm/api/v2/links/nodes', data, header, callback);
}

function deleteBatchLink(data, callback) {
    var header = {
        Authorization: getApp().globalData.token,
    };
    return httpclient.remove(config.host + '/ufm/api/v2/shareships/linkShare?type=user', data, header, callback);
}

function getLinkCode(ownerId, nodeId, callback) {
    var header = {
        Authorization: getApp().globalData.token,
    };
    var data = {
        isProgram: true
    }
    return httpclient.get(config.host + '/ufm/api/v2/links/program/' + ownerId + '/' + nodeId + '?isProgram=true', data, header, callback);
}

function getLinkInfo(linkCode, successCallback, failCallback) {
    var header = {
        Authorization: linkCode,
    };
    return httpclient.post(config.host + '/ufm/api/v2/links/getLinkOnlyByLinkCode', null, header, successCallback, failCallback);
}

//增加转发记录
function saveLinkCode(ownerId, linkCode, forwardId) {
    var header = {
        Authorization: getApp().globalData.token,
    };
    var data = {
        forwardId: forwardId,
        toId: getApp().globalData.cloudUserId
    };

    return httpclient.put(config.host + '/ufm/api/v2/forward/' + ownerId + '/' + linkCode, data, header, null);
}

module.exports = {
    createDefaultLink,
    createBatchFileLink,
    deleteBatchLink,
    getLinkInfo,
    getLinkCode,
    saveLinkCode
};
