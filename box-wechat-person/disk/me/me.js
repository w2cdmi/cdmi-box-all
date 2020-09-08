// disk/me/me.js
var util = require("../module/utils.js");
var user = require("../module/user.js");
var config = require("../config.js");
var httpclient = require("../module/httpclient.js");
var session = require("../../session.js");

var isFirstLoad = true;

Page({

    /**
     * 页面的初始数据
     */
    data: {
        enterpriseName: '',  //企业名称
        storageSize: '',
        spaceUsed: 0,
        spaceSize: 0
    },
    // 跳转到收件箱
    toInbox: function () {
        wx.navigateTo({
            url: '../inbox/inbox',
        })
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        isFirstLoad = true;
    },

// 跳转到收件箱
    toInbox: function () {
        wx.navigateTo({
            url: '../inbox/inbox',
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
        var page = this;
        session.login({
            enterpriseId: 0
        });
        session.invokeAfterLogin(function () {
            page.setData({
                enterpriseName: getApp().globalData.enterpriseName,
                accountType: getApp().globalData.accountType,
                accountTypeName: util.formatAccountType(getApp().globalData.accountType),
                avatarUrl: getApp().globalData.avatarUrl,
                nick: getApp().globalData.userName,
                spaceUsed: 0,
                spaceSize: 0,
                isAdmin: getApp().globalData.isAdmin
            });

            var userId = getApp().globalData.userId;
            user.getUserInfo(getApp().globalData.userId, function(data){
                var spaceUsed = (data.spaceUsed / data.spaceQuota) * 100;
                if (spaceUsed > 100){
                    spaceUsed = 100;
                }
                var spaceSize = util.formatFileSize(data.spaceUsed) + "/" + util.formatFileSize(data.spaceQuota);
                if (spaceSize == "/"){
                    spaceSize = 0;
                }
                page.setData({
                    spaceUsed: spaceUsed,
                    spaceSize: spaceSize
                });
            });

            user.getUserVipInfo(function (data) {
                var expireDate = "";
                if (data.expireDate != undefined){
                    expireDate = util.getFormatDate(data.expireDate, "yyyy-MM-dd");
                }
                page.setData({
                    expireDate: expireDate
                });
            });

            var res = wx.getStorageInfoSync();
            if (res.keys.length == 0){
                page.setData({
                    storageSize: 0
                });
            }else{
                page.setData({
                    storageSize: res.currentSize
                });
            }

            isFirstLoad = false;
        });
    },

    onShowMenu: function () {
        this.setData({
            isShowMenu: "true"
        })
    },

    onUploadImage: function (e) {
        var page = this;
        var tempFiles = e.detail;

        getApp().globalData.tempFiles = tempFiles;
        wx.navigateTo({
            url: '/disk/widget/selectFolder?jumpType=' + "uploadImage",
        })
    },
    onUploadVideo: function (e) {
        var page = this;
        var tempFile = e.detail;

        getApp().globalData.tempFile = tempFile;
        wx.navigateTo({
            url: '/disk/widget/selectFolder?jumpType=' + "uploadVideo",
        })
    },
    showCreateFolder: function () {
        wx.showToast({
            title: '请您进入文件目录创建文件夹！',
            icon: 'none'
        })
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

    switchEnterprise: function () {
        var enterpriseList = getApp().globalData.enterpriseList;
        if (enterpriseList != '' && enterpriseList.length > 0){
            wx.navigateTo({
                url: '/disk/enterprise/enterpriselist',
            })
        }else{
            wx.showModal({
                title: '提示',
                content: '对不起，你只有一个账号',
                showCancel: false
            })
        }
    },
    bindtoEnterpriseManage:function(){
        wx.navigateTo({
            url: '../enterprise/management',
        })
    },
    openVIP:function(){
        var url = config.host + "/ecm/api/v2/tempAccount/up2VIP";
        var data = {"orderId":1};
        var header = {
            'Authorization': getApp().globalData.token
        }
        httpclient.post(url, data, header);
    },
    gotoBuyPage:function(){
        wx.navigateTo({
            url: '../buy/buyPersonStorage',
        });
        isFirstLoad = true;
    },
    gotorecycle: function () {
        wx.navigateTo({
            url: '../recycle/recycle',
        });
    },
    gotoShareStatistics: function(){
        wx.navigateTo({
            url: 'share/shareStatistics',
        })
    },
    jumpToEnterprise: function(){
        wx.navigateToMiniProgram({
            appId: 'wx46ebf294739d7146',
            path: 'disk/index',
            extraData: {},
            envVersion: 'release',
            success(res) {
                console.log("跳转到企业文件宝");
            },
            fail(res) {
                wx.showToast({
                    title: '跳转失败！',
                    duration: 1000
                })
            }
        })
    },
    jumpToProtocol: function(){
        wx.navigateTo({
            url: '/disk/enterprise/protocol/protocol',
        })
    }
})