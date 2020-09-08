var config = require("/disk/config.js");
var robot = require("/disk/module/robot.js");

var times = 0;  //一次登录等待的次数（计算时间，一次登录等待十秒）
var interval;   //等待登录成功计时器
var __params;   //记录登录时传入的参数
var isLogining = false;    //是否已经在登录

/**
 * 检查Session是否过期，过期后重新生成。
 */
function invokeAfterLogin(callback) {
    //等待登录成功回调用方法
    waitAndInvoke(callback);
}

/**
 * 判断token是否存在
 */
function tokenIsExist() {
    var app = getApp();

    //App还未生成，返回true 全局变量globalData还未生成
    if (app == undefined || app.globalData == undefined) {
        return false
    }

    var token = app.globalData.token;
    //Token不存在
    if (token == undefined || token == "") {
        return false;
    }

    return true;
}

//token是否有效
function isValid() {
    //已过有效期
    var token = getApp().globalData.token;
    var expire = getApp().globalData.expire;
    if (token == undefined || token == '' || expire == undefined || expire == 0) {
        return false
    }
    var now = new Date().getTime();
    if (expire > now) {
        return true;
    } else {
        return false;
    }
}

//等待登录方法
function waitAndInvoke(callback) {
    var times = 0;
    clearInterval(interval);
    interval = setInterval(function () {
        //等待登录成功执行回掉方法
        if (tokenIsExist() && isValid()) {
            if (typeof (callback) == 'function') {
                callback();
                clearInterval(interval);
            }
        }

        if (times == 80) {
            console.log("登录失败！！");
            clearInterval(interval);
        }
        times++;
    }, 100);
}

/**
 * 登录方法
 */
function login(params) {
    if (isLogining) {
        return;
    } else {
        isLogining = true;
    }

    if (tokenIsExist() && isValid()) {
        isLogining = false;
        return;
    }

    wx.showLoading({
        title: '登录中...',
        mask: true
    });

    //保存登录参数
    if (params) {
        __params = params;
    }

    login2Server();
}

/**
 * 登录服务器
 * userInfo: 从微信获取的用户信息
 * __params： 服务器登录信息，enterpriseId等信息
 */
function login2Server() {
    wx.login({
        success: function (res) {
            var code = res.code;
            if (code) {
                // userInfo.code = code;
                var data = {};
                wx.getUserInfo({
                    success: function (res) {
                        data = {
                            appId: config.appId,
                            encryptedData: res.encryptedData,
                            iv: res.iv,
                            code: code,
                            mpId: config.mpId
                        };
                        //合并用户传递的参数
                        if (__params) {
                            Object.assign(data, __params);
                        }
                        getApp().globalData.avatarUrl = res.userInfo.avatarUrl;

                        //向后台发送请求，由后台获取微信用户的ID，后台检查用户是否注册；
                        //如果没有注册，则给用户注册一个个人帐号。
                        wx.request({
                            url: config.host + '/ecm/api/v2/token/wxmp',
                            method: "POST",
                            data: data,
                            header: {
                                'content-type': 'application/json'
                            },
                            success: function (result) {
                                if (result.data.errorMessage == 'User is disabled.') {
                                    wx.showToast({
                                        title: '账号不可用',
                                        icon: 'none',
                                        duration: 1000
                                    })
                                    return;
                                }

                                if (result.data.token != undefined) {
                                    getApp().globalData.token = result.data.token;
                                    var timeout = result.data.timeout * 1000;
                                    getApp().globalData.expire = new Date().getTime() + timeout; //过期时间为当前时间 + timeout（单位：秒）
                                    getApp().globalData.enterpriseId = result.data.enterpriseId;
                                    if (result.data.enterpriseId == 0) {
                                        getApp().globalData.enterpriseName = '个人账号';
                                    } else {
                                        getApp().globalData.enterpriseName = result.data.enterpriseName;
                                    }
                                    getApp().globalData.userId = result.data.userId;
                                    getApp().globalData.userName = result.data.alias;
                                    getApp().globalData.cloudUserId = result.data.cloudUserId;
                                    getApp().globalData.accountType = result.data.type;
                                    getApp().globalData.accountId = result.data.accountId;
                                    if (result.data.isAdmin != undefined && result.data.isAdmin == 1) {
                                        getApp().globalData.isAdmin = true;
                                    } else {
                                        getApp().globalData.isAdmin = false;
                                    }

                                    if (result.data.enterpriseList != undefined) {
                                        getApp().globalData.enterpriseList = result.data.enterpriseList;
                                    }

                                    //启动巡检机器人备份状态
                                    robot.openRobotCheck();
                                    //设置定时器，自动执行登录，防止会话超时。
                                    setTimeout(login, timeout);
                                    getApp().globalData.isJumpEenterpriseListPage = true;   //可以跳转

                                    wx.hideLoading();
                                    isLogining = false;
                                    return;
                                }
                                //存在多个企业，需要提醒用户选择企业进入
                                if (result.data.enterpriseList != undefined) {
                                    getApp().globalData.enterpriseList = result.data.enterpriseList;
                                    if (getApp().globalData.isJumpEenterpriseListPage) {
                                        wx.navigateTo({
                                            url: '/disk/enterprise/enterpriselist',
                                        })
                                    }
                                    wx.hideLoading();
                                    clearInterval(interval);    //清楚等待登录方法
                                    isLogining = false;
                                    getApp().globalData.isJumpEenterpriseListPage = false;
                                    return;
                                }

                            },
                            fail: function (res) {
                                wx.hideLoading();
                                isLogining = false;
                                clearInterval(interval);
                            }
                        })
                    },
                    fail: function () {
                        wx.hideLoading();
                        isLogining = false;
                        clearInterval(interval);
                        wx.navigateTo({
                            url: '/disk/authAndLogin',
                        })
                    },
                    timeout: function () {
                        wx.hideLoading();
                        isLogining = false;
                        clearInterval(interval);
                        wx.showToast({
                            title: '获取用户信息，连接超时！',
                            icon: 'none'
                        })
                        wx.navigateTo({
                            url: '/disk/authAndLogin',
                        })
                    }
                });
            } else {
                wx.showToast({
                    title: '获取code失败！',
                    icon: 'none'
                })
            }
        },
        fail: function (res) {
            wx.hideLoading();
            clearInterval(interval);
            isLogining = false;

            wx.showModal({
                title: '提示',
                content: '登录服务器失败，请稍后重试！',
                showCancel: false
            });
        },
        timeout: function () {
            wx.hideLoading();
            isLogining = false;
            clearInterval(interval);
            wx.showToast({
                title: '获取用户信息，连接超时！',
                icon: 'none'
            })
            wx.navigateTo({
                url: '/disk/authAndLogin',
            })
        }
    });
}

//注意：在登录前设置，不初始化企业列表。
function initGlobalData() {
    if (getApp().globalData.innerAudioContext != '') {
        var innerAudioContext = getApp().globalData.innerAudioContext;
        innerAudioContext.destroy();
    }
    var globalData = {
        token: '', //
        enterpriseId: 0, //当前企业ID
        enterpriseName: '',//当前登录的企业名称
        userId: '', //当前登录用户ID
        userName: '',   //用户名字
        acountType: '',  //账号类型
        cloudUserId: '', //当前登录用户的CloudUserId
        expire: 0, //会话到期时间
        refreshToken: '',//
        enterpriseList: getApp().globalData.enterpriseList, //多个企业时，选择列表
        IMAccount: '', //IM账号
        IMToken: '',  //im用户token
        avatarUrl: '',   //微信头像
        imgUrls: [],   //目录下图片数组
        musicList: '',   //音乐列表
        musicIndex: -1,   //当前音乐播放索引
        innerAudioContext: '',  //音乐播放器对象
        isShowMusicPanel: false,  //是否显示音乐播放器
        indexParam: getApp().globalData.indexParam,   // 1：最近浏览，2：我的分享，3：他人分享
        isOpenRobotCheck: getApp().globalData.isOpenRobotCheck, //  false：未启动巡检，true：已经启动巡检
        isOpenRobot: false,  //  false：未启动，true：已经启动
        isShowChatButton: false,   //是否显示聊天菜单按钮
        isJumpEenterpriseListPage: true,
        tempFiles: [],       //上传图片临时路径集合
        tempFile: {},        //上传视频临时路径
        crumbs: [],          //记录选择的面包屑
        isAdmin: false,         //是否为企业管理员
        deptCrumbs: [],     //部门面包屑
        systemName: '文件宝-'
    }

    getApp().globalData = globalData;
}

module.exports = {
    login,
    invokeAfterLogin,
    initGlobalData
};
