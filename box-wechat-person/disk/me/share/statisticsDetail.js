// disk/me/share/statisticsDetail.js
var ambassadors = require("../../module/ambassadors.js");
Page({

    /**
     * 页面的初始数据
     */
    data: {
        shareLevel: 1
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        var page = this;
        var proportions = options.proportions;
        var currentLevelName = options.currentLevelName;
        var shareLevel = options.shareLevel;

        page.setData({
            proportions: proportions,
            currentLevelName: currentLevelName,
            shareLevel: shareLevel
        });

        ambassadors.getAmbassadorsLevel(function (data) {
            if (data == undefined || data.length == 0){
                return;
            }
            var shareLevels = [];
            var shareLevel = {};
            for (var i = 0; i < data.length; i++) {
                shareLevel = data[i];
                shareLevel.proportions = (shareLevel.proportions * 100).toFixed(1);
                shareLevels.push(shareLevel);
            }
            page.setData({
                shareLevels: shareLevels
            })
        });
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

    /**
     * 页面相关事件处理函数--监听用户下拉动作
     */
    onPullDownRefresh: function () {

    },

    /**
     * 页面上拉触底事件的处理函数
     */
    onReachBottom: function () {

    }
})