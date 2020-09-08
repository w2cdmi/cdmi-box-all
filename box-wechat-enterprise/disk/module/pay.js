var config = require("../config.js");
var httpclient = require("httpclient.js");

var host = config.host;

function getOpenId(code, callback){
    var header = {
        Authorization: getApp().globalData.token,
    };
    var data = {
        code: code,
        mpId: config.mpId
    }
    return httpclient.get(config.host + '/ecm//api/v2/person/acount/openId/', data, header, callback);
}

function previewOrder(data, callback) {
    var header = {
        Authorization: getApp().globalData.token,
    };
    data.openId = "preview";
    return httpclient.post(config.host + '/ecm/api/v2/person/acount/order', data, header, callback);
}

function unifiedorder(data, callback){
    var header = {
        Authorization: getApp().globalData.token,
        'content-type': 'application/json'
    };
    return httpclient.post(config.host + '/ecm/api/v2/person/acount/unifiedorder/', data, header, callback);
}

module.exports = {
    getOpenId,
    previewOrder,
    unifiedorder
};