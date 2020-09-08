var config = require("../config.js");
var httpclient = require("httpclient.js");
var app = require("../../app.js");

var host = config.host;

//接受消息通知的条数
function theCountOfCommentMessages(status,callback) {
    var header = {
        Authorization: getApp().globalData.token,
    };
    var userId = getApp().globalData.cloudUserId;
    return httpclient.get(config.host + '/msm/messages/v1/' + userId +'/amount?status='+status, null, header, callback);
}

//获取消息通知列表，评论列表
function viewCommentsList(params, callback) {
    var header = {
        Authorization: getApp().globalData.token,
    };
    var userId = getApp().globalData.cloudUserId;
    if (params.status == "READED") {
        return httpclient.get(config.host + '/msm/messages/v1/messages/readed?&userId=' + userId + '&cursor=' + params.cursor + '&maxcount=' + params.maxcount, null, header, callback);
    } else {
        return httpclient.get(config.host + '/msm/messages/v1/messages/unread?userId=' + userId, null, header, callback);
    }

}

function getLinkCodeByObjectId(objectId, callback){
    var header = {
        Authorization: getApp().globalData.token,
    };
    return httpclient.get(config.host + '/msm/comments/v1/comment/' + objectId, null, header, callback);
}


module.exports = {
    theCountOfCommentMessages,
    viewCommentsList,
    getLinkCodeByObjectId
};