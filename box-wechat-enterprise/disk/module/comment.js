var config = require("../config.js");
var httpclient = require("httpclient.js");

const HOST = config.host + "/msm";

//对文件评论、点赞
const TARGET_FILE = 'Tenancy_File';
//对评论评论、点赞
const TARGET_COMMENT = 'Tenancy_Comment';

/**
 * 点赞 为某条信息进行点赞
 * targetId: 点赞对象id
 * ownerId： 对象拥有id
 * targetType: 点赞对象类型
 */
function givePraise(targetId, targetType, ownerId, callback) {
    var header = {
        'Authorization': getApp().globalData.token,
        'content-type': 'application/json'
    }
    var params = {
        "target": {
            "id": targetId,
            "type": targetType,
            "ownerId": ownerId   //对象拥有人id
        },
        "owner": {
            "id": getApp().globalData.cloudUserId,
            "name": getApp().globalData.userName,
            "headImage": getApp().globalData.avatarUrl
        }
    }
    wx.setStorageSync('isShowLoading', false);
    return httpclient.post(HOST + '/praise/v1/test', params, header, callback);
};

//取消点赞
function cancelPraise(targetId, callback) {
    var header = {
        'Authorization': getApp().globalData.token,
        'content-type': 'application/json'
    }
    var data = {};
    wx.setStorageSync('isShowLoading', false);
    return httpclient.remove(HOST + '/praise/v1/' + targetId + "?user_id=" + getApp().globalData.cloudUserId, data, header, callback);
}

//获取某个信息所获得的点赞数
function praiseCount(targetId, targetType, callback) {
    var header = {
        'Authorization': getApp().globalData.token,
        'content-type': 'application/json'
    }
    var data = {};
    wx.setStorageSync('isShowLoading', false);
    return httpclient.get(HOST + '/praise/v1/' + targetId + "/amount?type=" + targetType, data, header, callback);
}

//判断当前用户是否对指定文件点赞
function isPraise(targetId, targetType, callback) {
    var header = {
        'Authorization': getApp().globalData.token,
        'content-type': 'application/json'
    }
    var data = {};
    wx.setStorageSync('isShowLoading', false);
    return httpclient.get(HOST + '/praise/v1/users/' + getApp().globalData.cloudUserId + "/praised/" + targetId + "?type=" + targetType, data, header, callback);
}

/**
 * 获取某个信息所获得的点赞人员列表
 * targetId:   对象id
 * targetType:  对象类型
 * cursor:  分页起始位置
 * count:   每页记录数
 */
function praiseList(targetId, targetType, cursor, count, callback) {
    var header = {
        'Authorization': getApp().globalData.token,
        'content-type': 'application/json'
    }
    wx.setStorageSync('isShowLoading', false);
    return httpclient.get(HOST + '/praise/v1/' + targetId + "/praiser?type=" + targetType + "&cursor=" + cursor + "&maxcount=" + count, null, header, callback);
}

//获取对象的评论总数
function commentsCount(targetId, targetType, callback) {
    var header = {
        'Authorization': getApp().globalData.token,
        'content-type': 'application/json'
    }
    wx.setStorageSync('isShowLoading', false);
    return httpclient.get(HOST + '/comments/v1/' + targetId + "/amount?type=" + targetType, null, header, callback);
}

/**
 * 获取对象评论列表
 * targetId:   对象id
 * targetType:  对象类型
 * cursor:  分页起始位置
 * count:   每页记录数
 */
function commentList(targetId, targetType, cursor, count, callback) {
    var header = {
        'Authorization': getApp().globalData.token,
        'content-type': 'application/json'
    }
    wx.setStorageSync('isShowLoading', false);
    return httpclient.get(HOST + '/comments/v1/' + targetId + "/comment?type=" + targetType + "&cursor=" + cursor + "&maxcount=" + count, null, header, callback);
}

/**
 * 对某条信息进行评论
 * targetId: 点赞对象id
 * targetType: 点赞对象类型
 * ownerId： 对象拥有id
 * content: 评论内容
 */
function commentTarget(targetId, targetType, ownerId, content, callback) {
    var header = {
        'Authorization': getApp().globalData.token,
        'content-type': 'application/json'
    }
    var params = {
        "content": content,
        "target": {
            "id": targetId,
            "type": targetType,
            "ownerId": ownerId
        },
        "owner": {
            "id": getApp().globalData.cloudUserId,
            "name": getApp().globalData.userName,
            "headImage": getApp().globalData.avatarUrl
        }
    }
    wx.setStorageSync('isShowLoading', false);
    return httpclient.post(HOST + '/comments/v1/test', params, header, callback);
}

//查询一个外链的所有评论
function getCommentAndChildrenListForPage(targetId, cursor, maxcount, callback){
    var header = {
        'Authorization': getApp().globalData.token,
        'content-type': 'application/json'
    }
    var params = {
        'type': TARGET_FILE,
        'cursor': cursor,
        'maxcount': maxcount
    };
    wx.setStorageSync('isShowLoading', false);
    return httpclient.get(HOST + '/comments/v1/' + targetId + '/commentAndChildren', params, header, callback);
}

module.exports = {
    TARGET_FILE,
    TARGET_COMMENT,
    givePraise,
    cancelPraise,
    praiseCount,
    isPraise,
    praiseList,
    commentsCount,
    commentList,
    commentTarget,
    getCommentAndChildrenListForPage
}