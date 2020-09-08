var config = require("../config.js");
var httpclient = require("httpclient.js");

function getAmbassadorsInfo(callback) {
    var header = {
        Authorization: getApp().globalData.token,
    };
    var data = {}
    return httpclient.post(config.host + '/ecm/api/v2/wxmp/authCode/info', data, header, callback);
}

function getAmbassadorsLevel(callback) {
    var header = {
        Authorization: getApp().globalData.token,
    };
    var data = {}
    return httpclient.get(config.host + '/ecm/api/v2/shareLevel/items', data, header, callback);
}

function getAmbassadorsLevelById(shareLevel, callback) {
    var header = {
        Authorization: getApp().globalData.token,
    };
    var data = {}
    return httpclient.get(config.host + '/ecm/api/v2/shareLevel/' + shareLevel, data, header, callback);
}

module.exports = {
    getAmbassadorsInfo,
    getAmbassadorsLevel,
    getAmbassadorsLevelById
};