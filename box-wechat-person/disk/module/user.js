var config = require("../config.js");
var httpclient = require("httpclient.js");

function getUserInfo(userId, callback){
    var header = {
        Authorization: getApp().globalData.token,
    };
    var data = {};
    return httpclient.get(config.host + '/ecm/api/v2/users/' + userId, data, header, callback);
}

function getUserVipInfo(callback){
    var header = {
        Authorization: getApp().globalData.token,
    };
    var data = {};
    return httpclient.get(config.host + '/ecm/api/v2/users/getUserVipInfo', data, header, callback);
}

module.exports = {
    getUserInfo,
    getUserVipInfo
};