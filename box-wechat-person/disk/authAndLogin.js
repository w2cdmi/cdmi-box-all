var session = require("../session.js");
var config = require("config.js");

Page({

    /**
     * 页面的初始数据
     */
    data: {

    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {

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

    },
    getUserInfo: function (e) {
        if (e.detail == undefined || e.detail.errMsg != 'getUserInfo:ok') {
            // wx.showToast({
            //     title: '获取登录信息失败！',
            //     icon: 'none'
            // })
            return;
        }
        //返回微信用户信息
        var res = e.detail;
        var data = {
            appId: config.appId,
            encryptedData: res.encryptedData,
            iv: res.iv,
            mpId: config.mpId
        };
        getApp().globalData.avatarUrl = res.userInfo.avatarUrl;
        wx.navigateBack({
            delta: 1
        })
        // session.login2Server(data);
    },
    exitLogin: function(){
        wx.reLaunch({
            url: '/disk/authAndLogin',
        })
        setTimeout(function(){
            wx.navigateBack({
                delta: -1
            })
        }, 200);
    }
})