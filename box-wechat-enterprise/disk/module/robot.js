var config = require("../config.js");

var httpclient = require("httpclient.js");

function getRobotStatus(data, callback) {
    var header = {
        Authorization: getApp().globalData.token,
    };

    wx.request({
        url: config.host + "/ecm/api/v2/wx/robot/checkRobotStatus",
        method: 'GET',
        header: {
            'content-type': 'application/json',
            'Authorization': getApp().globalData.token
        },
        success: function (data) {
            if (typeof (callback) == 'function') {
                callback(data);
            }
        },
        fail: function (res) {
            console.log("get robot state fail");
        }
    });
}

function openRobotCheck() {
    if (!getApp().globalData.isOpenRobotCheck) {
        //循环获取机器人状态
        setInterval(function () {
            getRobotStatus({}, function (data) {
                if (typeof (data.data) == "object") {
                    getApp().globalData.getOpenRobotData = data.data;
                    getApp().globalData.isOpenRobot = true;
                } else {
                    getApp().globalData.isOpenRobot = false;
                }
            });
        }, 30000);
        getApp().globalData.isOpenRobotCheck = true;
    }
}
// 停止机器人
function stopRobot(robotId, callback) {
    var header = {
        Authorization: getApp().globalData.token
    };
    var data = robotId
    httpclient.post(config.host + "/ecm/api/v2/wx/robot/stopRobot", data, header, callback);
}
// 已配置
function listRobotConfig(robotId, callback) {
    var header = {
        Authorization: getApp().globalData.token
    };
    var data = {

    }
    httpclient.get(config.host + "/ecm/api/v2/wx/robot/listConfigByRobotId?robotId=" + robotId, data, header, callback);
}

// 获取微信当前活跃群组
function listGroupsName(wxUin, callback) {
    var header = {
        Authorization: getApp().globalData.token
    };
    var data = {

    }
    httpclient.post(config.host + '/ecm/api/v2/wx/robot/listWxRobotGroups?uin=' + wxUin, data, header, callback);
}

// 添加白名单
function addWhiteConfig(robotId, name, callback) {
    var header = {
        Authorization: getApp().globalData.token
    };
    var data = name
    httpclient.put(config.host + "/ecm/api/v2/wx/robot/whiteList?robotId=" + robotId, data, header, callback);
}
// 更新个人和群组配置
function updateWxRobotConfig(robotId, configValue, type, callback) {
    var header = {
        Authorization: getApp().globalData.token
    };
    var data = {
        robotId: robotId,
        type: type,
        config: configValue
    }
    httpclient.put(config.host + "/ecm/api/v2/wx/robot/wxRobotConfig", data, header, callback);
}
module.exports = {
    getRobotStatus,
    openRobotCheck,
    stopRobot,
    listRobotConfig,
    listGroupsName,
    addWhiteConfig,
    updateWxRobotConfig
};