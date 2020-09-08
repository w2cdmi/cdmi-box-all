// disk/me/share/profitDetail/profitDetail.js
// disk/me/share/shareDetail/shareDetail.js
var showDetail = require('../../../module/shareDetail.js');
var config = require("../../../config.js");
var util = require("../../../module/utils.js")

var detailDate = [];
Page({

    /**
     * 页面的初始数据
     */
    data: {
        offset: 0,
        profitDetail: [],
        isShowBlankPage: false
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        var page = this;
        getDetailDate(page);
    },

    getMoreList: function () {
        var page = this;
        getDetailDate(page);
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
        detailDate = [];
        var page = this;
        wx.getSystemInfo({
            success: function (res) {
                page.setData({
                    windowHeight: res.windowHeight,
                })
            }
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
        var page = this;
        getDetailDate(page);
    }
})

function getDetailDate(page) {
    showDetail.profitDetail(page.data.offset, function (data) {
        if (data.length != 0) {
            for (let i = 0; i < data.length; i++) {
                data[i].date = util.getFormatDate(data[i].createAt);
                data[i].status = data[i].status === 1 ? '打款中' : '已付款';
                data[i].proportions = data[i].proportions * 100 + '%';
            }
            var profitDetail = page.data.profitDetail;
            page.setData({
                offset: page.data.offset + 10,
                profitDetail: profitDetail.length != 0 ? profitDetail.concat(data) : data
            });
        }else{
            page.setData({
                isShowBlankPage: true
            });
        }
    });
}