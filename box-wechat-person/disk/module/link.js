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
    var data = {}
    return httpclient.get(config.host + '/ufm/api/v2/links/program/' + ownerId + '/' + nodeId, data, header, callback);
}

function getLinkInfo(linkCode, callback) {
    var header = {
        Authorization: linkCode,
    };
    return httpclient.post(config.host + '/ufm/api/v2/links/getLinkOnlyByLinkCode', null, header, callback);
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

//创建看点帖子
function createFindLink(data, callback) {
    var header = {
        Authorization: getApp().globalData.token,
    };
    return httpclient.post(config.host + '/ufm/api/v2/publish', data, header, callback);
}

//获取 看点 的推荐列表
function getFindRecommendList(limit, offset, callback) {
    var header = {
        Authorization: getApp().globalData.token,
    };
    var data = {};
    var limit = limit ? limit : 100;
    var offset = offset ? offset : 0;

    return httpclient.get(config.host + '/ufm/api/v2/publish/recommendation?limit=' + limit + "&offset=" + offset,data, header, callback);
}

//获取 看点 的我的发布
function getFindMyPostList(limit, offset, callback) {
    var header = {
        Authorization: getApp().globalData.token,
    };
    var data={};
    var limit = limit ? limit : 100;
    var offset = offset ? offset : 0;

    return httpclient.get(config.host + '/ufm/api/v2/publish/mine?limit=' + limit + "&offset=" + offset, data, header, callback);
}

//删除 看点 的 我的发布
function deleteMyPost(nodeId, callback) {
    var header = {
        Authorization: getApp().globalData.token,
    };
    var data={};
    return httpclient.remove(config.host + '/ufm/api/v2/publish/' + nodeId, data, header, callback);
}

module.exports = {
    createDefaultLink,
    createBatchFileLink,
    deleteBatchLink,
    getLinkInfo,
    getLinkCode,
    saveLinkCode,
    createFindLink,
    getFindRecommendList,
    getFindMyPostList,
    deleteMyPost
};
