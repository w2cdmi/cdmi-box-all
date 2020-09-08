// disk/enterprise/registered.js
var config = require("../../config.js");
var session = require("../../../session.js");
var httpclient = require("../../module/httpclient.js");
var enterpriseClient = require("../../module/enterprise.js");

var delta = 2;

//设置http头
var header = {
    Authorization: getApp().globalData.token,
    'content-type': 'application/json'
}
Page({

    /**
     * 页面的初始数据
     */
    data: {
        count: 60,
        agree: "/disk/images/tick.png",
        isAgree: true, //同意用户
        beginCountdown: false
    },

    onLoad: function (options) {
        wx.setNavigationBarTitle({
            title: '企业注册',
        })
        if (options.delta != undefined && options.delta != ""){
            delta = options.delta;
        }
    },

    toProtocol: function () {
        wx.navigateTo({
            url: '/disk/enterprise/protocol/protocol',
        })
    },

    //获取微信 手机号码
    getPhoneNumber: function (e) {
        if (e.detail.errMsg === 'getPhoneNumber:ok') {
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
                        if (usePhone == undefined || usePhone == ""){
                            wx.showToast({
                                title: '获取电话失败,请重试！',
                                icon: 'none'
                            })
                        }else{
                            //点击企业注册
                            page.setData({
                                phone: usePhone
                            })
                        }
                    });
                },
                fail: function (res) {
                }
            })
        }
    },
    //当焦点离开"企业名"，将 企业名 设置进入data
    setEnterpriseName: function (e) {
        let eName = e.detail.value;
        if ((/[^\a-\z\A-\Z0-9\u4E00-\u9FA5]/g).test(eName)) {
            wx.showModal({
                title: '错误',
                content: '企业名不能有符号',
            })
        }
        this.setData({
            eName: eName
        })
    },
    //当焦点离开"手机号"，将 手机号 设置进入data
    setPhoneNumber: function (e) {
        let ePhone = e.detail.value;
        if (ePhone == '' || ePhone == null) {
            wx.showToast({
                title: '号码为空',
                icon: "none"
            })
        } else {
            if (!(/^1[3|4|5|8|7|9][0-9]\d{8}$/.test(ePhone))) {
                wx.showToast({
                    title: '号码不正确',
                })
            }
        }
        this.setData({
            phone: ePhone
        })

    },
    //当焦点离开"验证码"，将 验证码 设置进入data
    setIdentifyCode: function (e) {
        let identifyCode = e.detail.value
        this.setData({
            identifyCode: identifyCode
        })
    },
    //点击注册企业  按钮 
    registerEnterprise: function (e) {
        var page = this;
        setTimeout(function () {
            page.setData({
                count: 60,
                beginCountdown: false
            })
            if ((/[^\a-\z\A-\Z0-9\u4E00-\u9FA5]/g).test(page.data.eName)) {
                wx.showModal({
                    title: '错误',
                    content: '企业名不能有符号',
                })
                return;
            }

            if (!page.data.isAgree) {
                wx.showToast({
                    title: '未同意协议！',
                })
                return;
            }
            if (page.data.eName == undefined || page.data.eName == "" || page.data.phone == undefined || page.data.phone == "") {
                wx.showToast({
                    title: '企业名称或电话为空！',
                    icon: 'none',
                    duration: 1000
                })
                return;
            }
            // if (!(/^1[3|4|5|8|9][0-9]\d{4,8}$/.test(page.data.phone))) {
            //     wx.showToast({
            //         title: '号码不正确',
            //     })
            //     return;
            // }

            // 测试验证码是否正确
            var data = {
                mobile: page.data.phone,
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


            if (page.data.eName && page.data.phone) {
                wx.showLoading({
                    title: '注册中...',
                    mask: true
                })
                var data = {
                    phone: page.data.phone,
                    checkCode: page.data.identifyCode,
                    name: page.data.eName
                }
                wx.login({
                    success: function (res) {
                        data.code = res.code;
                        wx.getUserInfo({
                            success: function (res) {
                                data.encryptedData = res.encryptedData;
                                data.iv = res.iv;
                                data.mpId = config.mpId;
                                //请求后台，创建账号
                                wx.request({
                                    url: config.host + '/ecm/api/v2/wxmp/authCode/registerbyWxmp',
                                    method: "POST",
                                    data: data,
                                    header: {
                                        'content-type': 'application/json'
                                    },
                                    success: function (result) {
                                        if (result.statusCode == 500) {
                                            wx.hideLoading();
                                            wx.showModal({
                                                title: '提示',
                                                content: '注册失败！！',
                                                showCancel: false
                                            })
                                        }
                                        if (result.statusCode == 409) {
                                            wx.hideLoading();
                                            wx.showModal({
                                                title: '提示',
                                                content: '企业已存在！！',
                                                showCancel: false
                                            })
                                        }
                                        if (result.statusCode == 200) {
                                            wx.hideLoading();
                                            if (result.data.code == "CheckCodeDisabled"){
                                                wx.showToast({
                                                    title: '验证码错误',
                                                    icon: 'none',
                                                    duration: 500
                                                })
                                            }else{
                                                wx.showLoading({
                                                    title: '登录中...',
                                                    mask: true
                                                });
                                                session.initGlobalData();
                                                session.login({
                                                    enterpriseId: result.data.id
                                                });
                                                setTimeout(function(){
                                                    wx.navigateBack({
                                                        delta: delta
                                                    })
                                                },200);
                                            }
                                        }
                                    }
                                })
                            },
                            fail: function (res) {
                                wx.hideLoading();
                                wx.openSetting({
                                    success: function (res) {
                                        if (!res.authSetting["scope.userInfo"]) {
                                            login();
                                        }
                                    }
                                })
                            }
                        })
                    }
                })
            } else if (!page.data.eName) {
                wx.showModal({
                    title: '提示',
                    content: '企业名不能为空',
                })
            } else {
                wx.showModal({
                    title: '提示',
                    content: '手机号没获取或获取失败，请重试',
                })
            }
        }, 200); 
 
    },

    //是否同意用户协议 动态显示协议图标
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

    // 获取验证码
    getIdentifyingCode: function () {
        var page = this;
        page.setData({
            beginCountdown: true
        });
        setTimeout(function () {
            if (page.data.phone == '' || page.data.phone == null) {
                wx.showToast({
                    title: '号码为空',
                    icon: "none"
                })
                return;
            }
            if (!(/^1[3|4|5|8|7|9][0-9]\d{4,8}$/.test(page.data.phone))) {
                wx.showToast({
                    title: '号码不正确',
                    icon: "none"
                })
                return;
            }
            if (page.data.phone && page.data.count == 60) {
                var data = {
                    mobile: page.data.phone
                }
                enterpriseClient.getIdentifyingCode(data, (dataCode) => {
                    var data = dataCode;
                });
                page.tick();
            } else if (!page.data.phone) {
                wx.showToast({
                    title: '请填写电话号码',
                    icon: 'none',
                    duration: 1000
                })
            }
        }, 200);
    },
    tick: function () {
        var vm = this
        if (vm.data.count > 0 && vm.data.beginCountdown) {
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
    gotoProtocol: function () {
        wx.navigateTo({
            url: "/disk/enterprise/protocol/protocol"
        })
    },
    jumpToEnterpriselist: function (e) {
        wx.redirectTo({
            url: '../enterpriselist',
        })
    },
    jumpToPerson: function (e) {
        wx.navigateToMiniProgram({
            appId: 'wxfbc7b65a9edb00f9',
            path: 'disk/index',
            extraData: {},
            envVersion: 'release',
            success(res) {
                console.log("跳转个人文件宝");
            },
            fail(res) {
                wx.showToast({
                    title: '跳转失败！',
                    duration: 1000
                })
            }
        });
    }
})
