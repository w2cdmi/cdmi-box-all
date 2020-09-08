// var session = require("../../session.js");

const wxRequest = (url, params) => {
    if (wx.getStorageSync('isShowLoading') !== false) {
        wx.showLoading({
            title: '加载中',
            icon: 'loading',
            mask: true
        })
    }
    wx.setStorageSync('isShowLoading', true);

    wx.request({
        url: url,
        method: params.method || 'GET',
        data: params.data || {},
        header: params.header || {
            'content-type': 'application/json'
        },
        success: (res) => {
            //只要与服务器正常交互，就会回调此方法，哪怕返回4XX、5XX错误
            wx.hideLoading();

            exceptionHandle(res);

            //返回200，才认为调用成功  201:表示创建成功
            if (res.statusCode == 200 || res.statusCode == 201) {
                //返回的信息中没有错误指示
                if (res.data.type == undefined || res.data.type != "error") {
                    (typeof params.success == 'function') && params.success(res.data);
                }
            }

            if (res.statusCode == 404) {
                if (typeof(params.fail) == 'function') {
                    params.fail(res.data);
                    return;
                }
                if (typeof (res.data) != 'undefined' && res.data != "") {
                    if (res.data.code == "NoSuchFile" || res.data.code == "NoSuchItem") {
                        wx.showToast({
                            title: '文件已不存在！',
                        })
                    } else if (res.data.code === 'NoSuchLink') {
                        wx.showModal({
                            title: '提示',
                            content: '链接已失效',
                            showCancel: false,
                            success: (res) => {
                                if (res.confirm) {
                                    wx.navigateBack({
                                        delta: 1
                                    });
                                }
                            }
                        });
                        return 'INBoxisnotExist';
                    }
                    if (res.data.code === "LinkExpired") {
                        wx.redirectTo({
                            url: '/disk/shares/shareLinkFailure',
                        })
                    }
                    if (res.data.code === 'NoSuchFolder') {
                        wx.showModal({
                            title: '提示',
                            content: '文件夹已经不存在，请删除',
                            showCancel: false,
                            success: (res) => {
                                if (res.confirm) {
                                    wx.navigateBack({
                                        delta: 1
                                    });
                                }
                            }
                        });

                    }
                }
            }

            if (res.statusCode == 403) {
                wx.showModal({
                    title: '提示',
                    content: '文件访问权限不足',
                    showCancel: false
                });
            }

            if (res.statusCode == 409) {
                if (typeof (res.data) != 'undefined' && res.data != "") {
                    if (res.data.code == "RepeatNameConflict") {
                        wx.showModal({
                            title: '提示',
                            content: '文件夹已经存在',
                            showCancel: false
                        })
                    } else if (res.data.code == "ExsitShortcut") {
                        wx.showModal({
                            title: '提示',
                            content: '快捷目录已存在',
                            showCancel: false
                        })
                    }
                }
            }

            if (res.statusCode == 401) {
                session.login({
                    enterpriseId: getApp().globalData.enterpriseId,
                });
                //等待登录成功后，执行
                session.invokeAfterLogin(function () {
                    wx.navigateBack({
                        delta: 0
                    });
                });
            }

            // if (res.statusCode == 400) {
            //     wx.showToast({
            //         title: '参数异常！',
            //         icon: 'none',
            //         duration: 1000
            //     });
            // }

            // if (res.statusCode == 405) {
            //     wx.showToast({
            //         title: '请求方式错误！',
            //         icon: 'none',
            //         duration: 1000
            //     });
            // }

            // if (res.statusCode == 500) {
            //     wx.showToast({
            //         title: '操作失败！',
            //         icon: 'none',
            //         duration: 1000
            //     });
            // }
            wx.hideLoading();
            return res;
        },
        fail: (res) => {

            wx.hideLoading();
            params.fail && params.fail(res);
            session.login({
                enterpriseId: 0
            });
        },
        complete: (res) => {
            params.complete && params.complete(res);
        }
    });
}

const wxRequestSync = (url, params) => {

    wx.request({
        url: url,
        method: params.method || 'GET',
        data: params.data || {},
        header: params.header || {
            'content-type': 'application/json'
        },
        success: (res) => {

            //返回200，才认为调用成功  201:表示创建成功
            if (res.statusCode == 200 || res.statusCode == 201) {
                //返回的信息中没有错误指示
                if (res.data.type == undefined || res.data.type != "error") {
                    (typeof params.success == 'function') && params.success(res.data);
                }
            }

            if (res.statusCode == 404) {
                if (typeof (res.data) != 'undefined' && res.data != "") {
                    if (res.data.code == "NoSuchFile") {
                    }
                }
            }

            if (res.statusCode == 403) {
                wx.showModal({
                    title: '提示',
                    content: '文件访问权限不足',
                    showCancel: false
                });
            }

            if (res.statusCode == 409) {
                if (typeof (res.data) != 'undefined' && res.data != "") {
                    if (res.data.code == "RepeatNameConflict") {
                        wx.showModal({
                            title: '提示',
                            content: '文件夹已经存在',
                            showCancel: false
                        })
                    }
                }
            }

            if (res.statusCode == 401) {
                session.login({
                    enterpriseId: getApp().globalData.enterpriseId,
                });
            }
            return res;
        },
        fail: (res) => {
            params.fail && params.fail(res);
        },
        complete: (res) => {
            params.complete && params.complete(res);
        }
    });
}

function put(url, data, header, success) {
    wxRequest(url, {
        data: data,
        header: header,
        method: "PUT",
        success: success
    });
}

function get(url, data, header, success) {
    return wxRequest(url, {
        "data": data,
        "header": header,
        "method": "GET",
        success: success
    });
}

function getSync(url, data, header, success) {
    return wxRequestSync(url, {
        "data": data,
        "header": header,
        "method": "GET",
        success: success
    });
}

function post(url, data, header, success, fail) {
    wxRequest(url, {
        data: data,
        header: header,
        method: "POST",
        success: success,
        fail: fail
    });
}

function remove(url, data, header, success) {
    wxRequest(url, {
        data: data,
        header: header,
        method: "DELETE",
        success: success
    });
}

//系统常见异常
function exceptionHandle(res) {
    if (res.data.code === "SameParentConflict") {
        wx.showModal({
            title: '提示',
            content: '相同目录不能进行操作',
            showCancel: false
        });
    } else if (res.data.code === "NoSuchParent") {
        wx.showModal({
            title: '提示',
            content: '父目录不存在',
            showCancel: false
        });
    } else if (res.data.code === "NoSuchSource") {
        wx.showModal({
            title: '提示',
            content: '源文件或文件夹不存在',
            showCancel: false
        });
    } else if (res.data.code === "NoSuchDest") {
        wx.showModal({
            title: '提示',
            content: '目标文件或文件夹不存在',
            showCancel: false
        });
    } else if (res.data.code === "SubFolderConflict") {
        wx.showModal({
            title: '提示',
            content: '不能移动子目录下',
            showCancel: false
        });
    } else if (res.data.code === "SameNodeConflict") {
        wx.showModal({
            title: '提示',
            content: '目标文件夹与源文件夹相同',
            showCancel: false
        });
    } else if (res.data.code === "SameParentConflict") {
        wx.showModal({
            title: '提示',
            content: '目标文件夹已在该目录下',
            showCancel: false
        });
    } else if (res.data.code === "UserLocked") {
        wx.showModal({
            title: '提示',
            content: '用户被锁定',
            showCancel: false
        });
    } else if (res.data.code === "ExistMemberConflict") {
        wx.showModal({
            title: '提示',
            content: '成员已存在',
            showCancel: false
        });
    } else if (res.data.code === "ExistTeamspaceConflict") {
        wx.showModal({
            title: '提示',
            content: '协作空间已存在',
            showCancel: false
        });
    } else if (res.data.code === "ExceedQuota") {
        wx.showModal({
            title: '提示',
            content: '空间容量不足',
            showCancel: false
        });
    } else if (res.data.code === "ExceedUserAvailableSpace") {
        wx.showModal({
            title: '提示',
            content: '空间容量不足',
            showCancel: false
        });
    } else if (res.data.code === "UploadSizeTooLarge") {
        wx.showModal({
            title: '提示',
            content: '上传文件大小超过限制',
            showCancel: false
        });
    } else if (res.data.code === "ExsitShortcut") {
        wx.showModal({
            title: '提示',
            content: '快捷目录已经存在',
            showCancel: false
        });
    }
}

module.exports = {
    put,
    get,
    getSync,
    post,
    remove
};