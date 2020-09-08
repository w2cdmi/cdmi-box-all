var robot = require("../module/robot.js")
Page({

    /**
     * 页面的初始数据
     */
    data: {
        charLt: "<",
        charGt: ">",
        listRobotConfigData: [],
        wxName: ""
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        var page = this
        page.setData({
            wxName: getApp().globalData.getOpenRobotData.wxName
        })
    },

    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady: function () {

    },

    /**
     * 生命周期函数--监听页面显示
     */
    onShow: function () {
        var page = this;
        listRobotConfig(page)
    },

    /**
     * 生命周期函数--监听页面隐藏
     */
    onHide: function () {

    },

    /**
     * 生命周期函数--监听页面卸载
     */
    onUnload: function () {

    },
    //跳转到微信备份策略设置界面
    toWechatBackupSet: function () {
        wx.navigateTo({
            url: '/disk/weChatBackup/weChatBackupSet/weChatBackupSet',
        })
    },
    // 停止机器人
    stopRobot: function () {
        var getOpenRobotData = getApp().globalData.getOpenRobotData;
        var robotId = getOpenRobotData.id;
        wx.showModal({
            title: '提示',
            content: '确定停止微信备份？',
            success: function (res) {
                if (res.confirm) {
                    robot.stopRobot(robotId, function (data) {
                        wx.navigateBack({
                            delta: 1
                        })
                    })
                } 
            }
        })
        
    }
})

function listRobotConfig(page) {
    var getOpenRobotData = getApp().globalData.getOpenRobotData;
    var robotId = getOpenRobotData.id;
    robot.listRobotConfig(robotId, function (data) {
        var newListRobotConfigData = [];
        var groupDataConfig = [];
        var personDataConfig = [];
        for (let i = 0; i < data.length; i++) {
            if (data[i].type == 1) {
                personDataConfig.push(data[i])
            }
            if (data[i].type == 2) {
                groupDataConfig.push(data[i])
            }
            if (data[i].type == 3) {
                newListRobotConfigData.push(data[i])
            }
        }
        wx.setStorageSync("listRobotConfigData", newListRobotConfigData)
        wx.setStorageSync("personDataConfig", personDataConfig)
        wx.setStorageSync("groupDataConfig", groupDataConfig)
        page.setData({
            listRobotConfigData: newListRobotConfigData
        })
    })
}