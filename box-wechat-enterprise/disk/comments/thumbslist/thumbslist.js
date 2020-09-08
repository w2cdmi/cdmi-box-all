var commentClient = require("../../module/comment.js");
var targetType = commentClient.TARGET_FILE;
var LIMIT = 10; // 每次获取的点赞条数
var list = [];
Page({

    /**
     * 页面的初始数据
     */
    data: {
        praiseList: [],
        offset: 0,
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        this.setData({
            targetId: options.linkCode
        })
        var page = this;
        getMore(page);
        wx.getSystemInfo({
            success: (res)=> {
                this.setData({
                    screenHeight: res.screenHeight
                })
            },
        })
    },

    getMoreList: function () {
        var page = this;
        getMore(page);
    },
    /**
     * 生命周期函数--监听页面显示
     */
    onShow: function () {
    },

    /**
     * 生命周期函数--监听页面卸载
     */
    onUnload: function () {

    },
})

function getMore(page) {
    commentClient.praiseList(page.data.targetId, targetType, page.data.offset, LIMIT, function (data) {
        page.setData({
            praiseList: page.data.praiseList.concat(data),
            offset: page.data.offset + 10
        });
    });

}