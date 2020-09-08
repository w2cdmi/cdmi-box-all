var enterpriseClient = require("../../module/enterprise.js");
var config = require("../../config.js");
let httpclient = require("../../module/httpclient.js");
var session = require("../../../session.js");

// 设置http头
var header = {
    Authorization: getApp().globalData.token,
    'content-type': 'application/json'
}

var enterpriseId = 0;
var enterpriseName = "";
var deptId = 0;

Page({

    /**
     * 页面的初始数据
     */
    data: {
        count: 60,
        marginLeft: 23,
        agree: "/disk/images/tick.png",
        isAgree: true
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        enterpriseId = options.enterpriseId;
        enterpriseName = decodeURIComponent(options.enterpriseName);
        deptId = options.deptId;
        if (enterpriseId == undefined || enterpriseName == undefined || deptId == undefined || enterpriseId == 0 || enterpriseName == "") {
            wx.navigateBack({
                delta: 1
            });
        }
        wx.setNavigationBarTitle({ title: enterpriseName });
        this.setData({
            enterpriseName: enterpriseName
        });

        this.setData({
            backIndexBtn: true
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
        enterpriseClient.isExistEmploye(enterpriseId, function(isExist){
            if (isExist){
                session.initGlobalData();
                session.login({
                    enterpriseId: enterpriseId
                });
                wx.reLaunch({
                    url: '/disk/index',
                })
            }
        });
    },
    isAgree: function () {
        var page = this;
        this.setData({
            isAgree: !page.data.isAgree,
        })
        if (this.data.isAgree) {
            this.setData({
                agree: "/disk/images/tick.png",
            })
        } else {
            this.setData({
                agree: "/disk/images/tick_no.png",
            })
        }
    },
    //离开文本框保存电话号码
    savePhoneNumber: function (e) {
        this.setData({
            phoneNumber: e.detail.value,
        });
    },
    saveUserName: function (e) {
        this.setData({
            userName: e.detail.value,
        });
    },
    // 倒计时
    getValidCode: function () {
        if (this.data.phoneNumber && this.data.count == 60) {
            this.setData({
                marginLeft: "65"
            })
            this.tick()
        } else if (!this.data.phoneNumber) {
            wx.showToast({
                title: '请填写电话号码',
                icon: 'loading',
                duration: 1000
            })
        }
    },
    //当焦点离开"验证码"，将 验证码 设置进入data
    setIdentifyCode: function (e) {
        let identifyCode = e.detail.value
        this.setData({
            identifyCode: identifyCode
        })
    },
    // 获取验证码
    getIdentifyingCode: function () {
        if (this.data.phoneNumber && this.data.count == 60) {
            var data = {
                mobile: this.data.phoneNumber
            }

            enterpriseClient.getIdentifyingCode(data, (dataCode) => {
                var data = dataCode;
            });

            this.tick();
        } else if (!this.data.phoneNumber) {
            wx.showToast({
                title: '请填写电话号码',
                icon: 'none',
                duration: 1000
            })
        }
    },
    tick: function () {
        var vm = this
        if (vm.data.count > 0) {
            vm.setData({
                count: vm.data.count - 1
            });
            setTimeout(function () {
                return vm.tick()
            }, 1000)
        } else {
            vm.setData({
                count: 60,
                marginLeft: 23,
            });
        }
    },
    //获取微信 手机号码
    getPhoneNumber: function (e) {
        let page = this;
        var data = {
            iv: e.detail.iv,
            encryptedData: e.detail.encryptedData
        }
        wx.login({
            success: function (res) {
                data.code = res.code;
                data.mpId = config.mpId;
                httpclient.post(config.host + '/ecm/api/v2/wxOauth2/phone', data, header, (PhoneInfo) => {
                    var usePhone = PhoneInfo.phoneNumber;
                    //点击企业注册 用
                    page.setData({
                        phoneNumber: usePhone
                    })
                });

            },
            fail: function (res) {
                wx.navigateTo({
                    url: '/disk/exception/refuseAuthInfo',
                })
            }
        })
    },
    onEnterpriseLogin: function (e) {
        var page = this;
        if (page.data.userName == undefined || page.data.userName == "" || page.data.phoneNumber == 0 || page.data.phoneNumber == undefined) {
            wx.showToast({
                title: '名称或手机号不能为空',
                icon: 'none',
                duration: 1000
            })
            return;
        }

        // 测试验证码是否正确
        var data = {
            mobile: page.data.phoneNumber,
            code: page.data.identifyCode
        }
        if (page.data.identifyCode == undefined || page.data.identifyCode == "") {
            wx.showToast({
                title: '验证码为空！',
                icon: 'none',
                duration: 1000
            })
            return;
        }
        // enterpriseClient.validateCode(data, (dataCode) => {
        //     var data = dataCode;
        // });

        enterpriseClient.createEmployees(enterpriseId, page.data.userName, page.data.phoneNumber, page.data.identifyCode, deptId);
    }
})
