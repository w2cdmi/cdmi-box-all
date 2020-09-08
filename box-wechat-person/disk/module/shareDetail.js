// 收益明细
var config = require("../config.js");
var httpclient = require("httpclient.js");
var utils = require("utils.js");

function profitDetail(offset, callback) {
    var header = {
        Authorization: getApp().globalData.token,
        'content-type': 'application/json'
    }
    var data = {
        "limit": 10,   // 条数
        "offset": offset // 开始查询的位置
    }
    return httpclient.post(config.host + '/ecm/api/v2/users/profits/items', data, header, callback);

}

function personDetail(offset, callback) {
    var header = {
        Authorization: getApp().globalData.token,
        'content-type': 'application/json'
    }

    wx.login({
        success: function (res) {
            var code = res.code;
            if (code) {
                //获取用户信息
                wx.getUserInfo({
                    success: function (res) {
                        var data = {
                            "mpId": config.mpId,
                            "code": code,
                            "encryptedData": res.encryptedData,
                            "iv": res.iv,
                            "limit": { "offset": offset, "length": 10 },
                            "orderList": [{ "field": "createdAt", "direction": "desc" }]
                        }

                        return httpclient.post(config.host + '/ecm/api/v2/wxmp/authCode/inviterByMe', data, header, callback);

                    }
                });
            }
        }
    });
}

module.exports = {
    personDetail,
    profitDetail
}