// disk/enterpriselist.js
var session = require("../../session.js");

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
        this.setData({
            scrollHeight: wx.getSystemInfoSync().windowHeight - 90
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
        wx.setNavigationBarTitle({title: '选择账号'})
        var enterpriseList = getApp().globalData.enterpriseList;
        
        var page = this;
        var enterpriseId = getApp().globalData.enterpriseId;
        setPageData(enterpriseList, enterpriseId, page);

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
        getApp().globalData.isJumpEenterpriseListPage = true;
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

    /**
     * 选择企业登录
     */
    enterpriseListClick: function (e) {
        var enterpriseId = e.currentTarget.dataset.enterprise.id;
        var page = this;
        var enterpriseList = getApp().globalData.enterpriseList;
        setPageData(enterpriseList, enterpriseId, page);

        wx.showModal({
            title: "提示",
            content: "你确定使用该账号登录吗?",
            success: function (msg) {
                if (msg.confirm) {
                    //不切换企业
                    if (enterpriseId == getApp().globalData.enterpriseId && getApp().globalData.token != ''){
                        wx.navigateBack({
                            delta: 1
                        });
                        return;
                    }
                    
                    session.initGlobalData();
                    wx.showLoading({
                        title: '登录中...',
                        mask: true
                    });
                    //携带参数登录
                    session.login({
                        enterpriseId: enterpriseId
                    });
                    //等待登录成功后，执行
                    session.invokeAfterLogin(function () {
                        wx.hideLoading();
                        wx.navigateBack({
                            delta: 1,
                        })
                    });
                }else{
                    var loginEnterpriseId = getApp().globalData.enterpriseId;
                    if (loginEnterpriseId != undefined && loginEnterpriseId != "" && loginEnterpriseId != 0){
                        setPageData(enterpriseList, loginEnterpriseId, page);
                    }
                }
            }
        })
    },
    addEnterprise: function(e){
        wx.redirectTo({
            url: '/disk/enterprise/registered/registered',
        })
    }
})

function setPageData(enterpriseList, enterpriseId, page){
    var tempEnterpriseList = [];
    for (var i = 0; i < enterpriseList.length; i++) {
        if(enterpriseList[i].id == 0){
            continue;
        }
        if (enterpriseList[i].id == enterpriseId) {
            enterpriseList[i].class = "pitchOn";
        } else {
            enterpriseList[i].class = "";
        }
        tempEnterpriseList.push(enterpriseList[i]);
    }
    page.setData({
        enterpriseList: tempEnterpriseList
    });
}

