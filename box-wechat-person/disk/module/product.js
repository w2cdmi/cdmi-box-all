var config = require("../config.js");
var httpclient = require("httpclient.js");

function getProducts(callback){
    var header = {
        'Authorization': getApp().globalData.token,
        'content-type': 'application/json'
    };
    return httpclient.get(config.host + '/ecm/api/v2/products', null, header, callback);
}

function getProductDuration(productId, callback) {
    var header = {
        'Authorization': getApp().globalData.token,
        'content-type': 'application/json'
    };
    return httpclient.get(config.host + '/ecm/api/v2/products/' + productId + "/durationAndPrice", null, header, callback);
}

/* 将文件大小转化为带单位的表示 */
function formatStorageSize(size) {
    if (size == undefined || size == "" || isNaN(size)) {
        return "";
    }
    return size / 1024 / 1024 / 1024;
}

module.exports = {
    getProducts,
    getProductDuration,
    formatStorageSize
};