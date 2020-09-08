// disk/me/share/shareStatistics.js
var ambassadors = require("../../module/ambassadors.js");

var isFirstLoad = true;

Page({

    /**
     * 页面的初始数据
     */
    data: {
        countInvitByMe: 0,
        countTodayInvitByMe: 0,
        proportions: 0,
        shareLevel: 1,
        currentLevelName: '',
        allProfit: 0,
        todayProfit: 0,
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        isFirstLoad = true;
    },

    //  分享总人数详情
    gotoPersonDetail: function () {
        wx.navigateTo({
            url: '/disk/me/share/personDetail/personDetail',
        })
    },
    // 分享总收益详情
    gotoProfitDetail: function (e) {
        wx.navigateTo({
            url: '/disk/me/share/profitDetail/profitDetail',
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
        if (!isFirstLoad){
            return;
        }

        // 获取分享大使数据
        var page = this;
        getAmbassadorsInfo(page);

        var avatarUrl = getApp().globalData.avatarUrl;
        var userName = getApp().globalData.userName;
        // var avatarUrl = "/disk/images/depfile.png";
        // var username = "名字"
        this.setData({
            avatarUrl: avatarUrl,
            username: userName
        });
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

    },
    jumpToDetail: function () {
        wx.navigateTo({
            url: 'statisticsDetail?proportions=' + this.data.proportions + "&shareLevel=" + this.data.shareLevel + "&currentLevelName=" + this.data.currentLevelName,
        })
    }
})

function getAmbassadorsInfo(page) {
    ambassadors.getAmbassadorsInfo(function (data) {
        page.setData({
            countInvitByMe: data.countInvitByMe,
            countTodayInvitByMe: data.countTodayInvitByMe,
            countTotalProfits: data.countTotalProfits,
            countTodayProfits: data.countTodayProfits,
            shareLevel: data.shareLevel
        });
        var shareLevel = data.shareLevel;
        if (shareLevel > 0) {
            ambassadors.getAmbassadorsLevelById(shareLevel, function (data) {
                page.setData({
                    proportions: (data.proportions * 100),
                    startRange: data.startRange,
                    endRange: data.endRange,
                    currentLevelName: data.description
                });
            });
            ambassadors.getAmbassadorsLevelById(shareLevel + 1, function (data) {
                page.setData({
                    nextLevelName: data.description
                });
            });
        }
        isFirstLoad = false;
    });

}
