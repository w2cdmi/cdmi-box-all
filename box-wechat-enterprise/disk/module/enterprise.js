var config = require("../config.js");
var session = require("../../session.js");
var httpclient = require("httpclient.js");

function getUserPhone(data, callback) {
    var header = {
        Authorization: getApp().globalData.token,
        'content-type': 'application/json'
    };
    wx.login({
        success: function (res) {
            data.code = res.code;
            return httpclient.post(config.host + '/ecm/api/v2/wxOauth2/phone', data, header, callback);
        }
    })
}

// 修改在职离职状态
function toggleState(employeeId, callback) {
    var header = {
        Authorization: getApp().globalData.token,
        'content-type': 'application/json'
    };
    return httpclient.put(config.host + '/ecm/api/v2/enterprise/employees/' + employeeId + '/dismiss', {}, header, callback);
}

// 搜索员工
function selectEmployee(data, callback) {
    var header = {
        Authorization: getApp().globalData.token,
        'content-type': 'application/json'
    };
    return httpclient.get(config.host + '/ecm/api/v2/enterprise/employees', data, header, callback);
}
//获取我的企业列表
function getEnterpriseList(callback) {
    var header = {
        Authorization: getApp().globalData.token
    };
    wx.login({
        success: function (res) {
            var data = {};
            data.code = res.code;
            wx.getUserInfo({
                success: function (res) {
                    data.encryptedData = res.encryptedData;
                    data.iv = res.iv;
                    data.mpId = config.mpId;
                    httpclient.post(config.host + '/ecm/api/v2/enterprise/enterpriseList', data, header, callback);
                },
                fail: function (res) {
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
}

// 增加部门员工
/**
 *var data = {
 *     departmentId: page.data.deptId,
 *     role: roleIndex,
 *     enterpriseUserId: page.data.employeeId,
 *     enterpriseId: getApp().globalData.enterpriseId
 *}
 */
function addDepartmentMember(data, callback) {
    var header = {
        Authorization: getApp().globalData.token,
        'content-type': 'application/json'
    };
    return httpclient.post(config.host + '/ecm/api/v2/enterprise/user_dept', data, header, callback);
}

// 删除部门成员
function deleteDepartmentMember(enterpriseId, deptId, employeeId, callback) {
    var header = {
        Authorization: getApp().globalData.token,
    };
    var data = {
        departmentId: deptId,
        enterpriseUserId: employeeId,
        enterpriseId: enterpriseId
    }
    return httpclient.remove(config.host + '/ecm/api/v2/enterprise/user_dept', data, header, callback);
}

// 获取员工的部门信息
// function getDepartment(employeId, enterpriseId, callback) {
//     var header = {
//         Authorization: getApp().globalData.token
//     }
//     var data = {
//         enterpriseUserId: employeId,
//         enterpriseId: enterpriseId
//     };
//     return httpclient.get(config.host + '/ecm/api/v2/enterprise/user_dept', data, header, callback);
// }

//修改员工部门
function updateDepartmentEmployment(srcDeptId, destDeptId, employeId, callback) {
    var header = {
        'Authorization': getApp().globalData.token,
        'content-type': 'application/json'
    };
    var data = {
        srcDeptId: srcDeptId,
        departmentId: destDeptId,
        enterpriseUserId: employeId
    };
    return httpclient.put(config.host + '/ecm/api/v2/enterprise/user_dept', data, header, callback);
}

//新增员工的部门（暂未修改，有问题）
function addDepartmentEmployment(deptId, employeId, callback) {
    var header = {
        'Authorization': getApp().globalData.token,
        'content-type': 'application/json'
    };
    var data = {
        departmentId: deptId,
        enterpriseUserId: employeId
    };
    return httpclient.post(config.host + '/ecm/api/v2/enterprise/user_dept', data, header, callback);
}

// 获取部门id
function getManager(deptId, callback) {
    var header = {
        Authorization: getApp().globalData.token,
        'content-type': 'application/json'
    };
    return httpclient.get(config.host + '/ecm/api/v2/enterprise/depts/' + deptId + '/director', {}, header, callback);
}


// 获取，核对短信验证码
function getIdentifyingCode(data, callback) {
    var header = {
        Authorization: getApp().globalData.token,
        'content-type': 'application/json'
    };
    return httpclient.post(config.host + '/msm/messages/v1/sms/checkcode', data, header, callback);
}
// 核对短信验证码
function validateCode(data, callback) {
    var header = {
        Authorization: getApp().globalData.token,
        'content-type': 'application/json'
    };
    return httpclient.put(config.host + '/msm/messages/v1/sms/checkcode', data, header, callback);
}

function registerEnterprise(data, callback) {
    var header = {
        Authorization: getApp().globalData.token,
        'content-type': 'application/json'
    };
    wx.login({
        success: function (res) {
            data.code = res.code;
            wx.getUserInfo({
                success: function (res) {
                    data.encryptedData = res.encryptedData;
                    data.iv = res.iv;
                    data.mpId = config.mpId;
                    return httpclient.post(config.host + '/ecm/api/v2/wxmp/authCode/registerbyWxmp', data, header, callback);
                },
                fail: function (res) {
                    wx.navigateTo({
                        url: '/disk/exception/refuseAuthInfo',
                    })
                }
            })
        }
    });
}

function getDeptAndEmployees(deptId, callback) {
    var header = {
        Authorization: getApp().globalData.token,
        'content-type': 'application/x-www-form-urlencoded'
    };
    var data = {
        deptId: deptId
    }
    return httpclient.get(config.host + '/ecm/api/v2/enterprise/depts_employees', data, header, callback);
}

//创建部门
function createDept(data, callback) {
    var header = {
        'Authorization': getApp().globalData.token,
        'content-type': 'application/x-www-form-urlencoded'
    };
    return httpclient.post(config.host + '/ecm/api/v2/enterprise/depts', data, header, callback);
}

function deleteDept(deptId, callback) {
    var header = {
        'Authorization': getApp().globalData.token
    };
    var data = {};
    return httpclient.remove(config.host + '/ecm/api/v2/enterprise/depts/' + deptId, data, header, callback);
}
//新增员工
function createEmployees(enterpriseId, name, phone, checkcode, deptId) {
    wx.showLoading({
        title: '生成员工账号',
        mask: true
    })
    wx.login({
        success: function (res) {
            var code = res.code;
            if (code) {
                //获取用户信息
                wx.getUserInfo({
                    success: function (res) {
                        var userInfo = res.userInfo;
                        var data = {
                            code: code,
                            encryptedData: res.encryptedData,
                            iv: res.iv,
                            enterpriseId: enterpriseId,
                            name: name,
                            phone: phone,
                            deptId: deptId,
                            mpId: config.mpId,
                            checkCode: checkcode
                        };

                        //请求后台，创建账号
                        wx.request({
                            url: config.host + '/ecm/api/v2/wxmp/authCode/registerUser',
                            method: "POST",
                            data: data,
                            header: {
                                'content-type': 'application/json',
                                'x-device-sn': '123', //能否获取手机相关信息？
                                'x-device-type': 'web',
                                'x-device-os': 'mp',
                                'x-device-name': 'mp',
                                'x-client-version': 'v0.1'
                            },
                            success: function (result) {
                                if (result.statusCode == 500) {
                                    wx.hideLoading();
                                    wx.showModal({
                                        title: '提示',
                                        content: '生成员工账号失败！！',
                                        showCancel: false
                                    })
                                }
                                if (result.statusCode == 409) {
                                    wx.hideLoading();
                                    wx.showModal({
                                        title: '提示',
                                        content: '员工名字已存在！！',
                                        showCancel: false
                                    })
                                }
                                if (result.statusCode == 200) {
                                    wx.hideLoading();
                                    if (result.data.code == "CheckCodeDisabled") {
                                        wx.showToast({
                                            title: '验证码错误',
                                            icon: 'none',
                                            duration: 500
                                        })
                                    } else {
                                        session.initGlobalData();
                                        session.login({
                                            enterpriseId: enterpriseId
                                        });
                                        wx.reLaunch({
                                            url: '/disk/index',
                                        })
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
                });
            } else {
                console.log('获取用户登录态失败！' + res.errMsg)
            }
        }
    });
}
//删除员工
function deleteEmploy(employId, callback) {
    var header = {
        Authorization: getApp().globalData.token
    };
    var data = {}
    return httpclient.remove(config.host + '/ecm/api/v2/enterprise/employees/' + employId, data, header, callback);
}
//设置部门文档外发审核
function setDocumentAudit(deptId, approve, callback) {
    var header = {
        Authorization: getApp().globalData.token
    };
    var data = {}
    return httpclient.put(config.host + '/ecm/api/v2/enterprise/depts/' + deptId + '/approve?approve=' + approve, data, header, callback);
}

//获取员工信息
function getEmploye(employeId, callback) {
    var header = {
        Authorization: getApp().globalData.token
    };
    var data = {};
    return httpclient.get(config.host + '/ecm/api/v2/enterprise/employees/' + employeId, data, header, callback);
}

//获取某个部门下的部门列表
function getDepartmentByParentId(parentId, callback) {
    var header = {
        Authorization: getApp().globalData.token
    };
    var data = {
        deptId: parentId
    };
    return httpclient.get(config.host + '/ecm/api/v2/enterprise/depts', data, header, callback);
}

//判断是否存在改员工
function isExistEmploye(enterpriseId, callback) {
    var header = {
        'Authorization': getApp().globalData.token
    };
    wx.showLoading({
        title: '加载中',
        mask: true
    })
    wx.login({
        success: function (res) {
            var code = res.code;
            if (code) {
                //获取用户信息
                wx.getUserInfo({
                    success: function (res) {
                        var userInfo = res.userInfo;
                        var data = {
                            code: code,
                            encryptedData: res.encryptedData,
                            iv: res.iv,
                            enterpriseId: enterpriseId,
                            mpId: config.mpId
                        };

                        //请求后台，创建账号
                        wx.request({
                            url: config.host + '/ecm/api/v2/enterprise/employee/uinonId',
                            method: "POST",
                            data: data,
                            header: {
                                'content-type': 'application/json',
                                'x-device-sn': '123', //能否获取手机相关信息？
                                'x-device-type': 'web',
                                'x-device-os': 'mp',
                                'x-device-name': 'mp',
                                'x-client-version': 'v0.1'
                            },
                            success: function (result) {
                                if (result.statusCode == 500) {
                                    wx.hideLoading();
                                    wx.showModal({
                                        title: '提示',
                                        content: '检测是否存在账号失败！！',
                                        showCancel: false
                                    })
                                }
                                if (result.statusCode == 200) {
                                    wx.hideLoading();
                                    if (typeof (callback) == 'function') {
                                        callback(result.data);
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
                                    wx.navigateBack({
                                        delta: 1
                                    })
                                }
                            }
                        })
                    }
                });
            } else {
                console.log('获取用户登录态失败！' + res.errMsg)
            }
        }
    });
}

// 根据部门名字查询某个部门
function getDepartmentByDeptId(name, callback) {
    var header = {
        Authorization: getApp().globalData.token
    };
    var data = {
        search: name
    };
    return httpclient.get(config.host + '/ecm/api/v2/enterprise/depts/search', data, header, callback);
}

/**
 * 获取离职员工列表
 * status：-2 表示离职状态
 */
function getLeaveEmployeList(callback) {
    var header = {
        Authorization: getApp().globalData.token
    };
    return httpclient.get(config.host + '/ecm/api/v2/enterprise/employees?status=-2&deptId=-1', null, header, callback);
}


/**
 * 获取离职用户的协作空间
 * userId 表示用户的id
 */
function getAuthTransferList(userId, callback) {
    var header = {
        Authorization: getApp().globalData.token
    };
    var data = {
        userId: userId
    }
    return httpclient.post(config.host + '/ufm/api/v2/migration/teamspaces/' + userId + '/spaces', data, header, callback)
}

/**
 * 获取离职员工文件列表
 */
function getCoopNodes(ownerId, folderId, limit, offset, callback) {
    var header = {
        Authorization: getApp().globalData.token
    };
    var data = {
        limit: limit,
        offset: offset,
        order: [{ field: "type", direction: "ASC" }, { field: "modifiedAt", direction: "DESC" }],
        thumbnail: [{ width: 120, height: 120 }, { width: 500, height: 500 }]
    }
    return httpclient.post(config.host + '/ufm/api/v2/migration/folders/' + ownerId + '/' + folderId + '/items', data, header, callback)
}

/**
 * 移交协作空间权限
 */
function transferCoopAuth(ownerBy, teamId, callback) {
    var header = {
        Authorization: getApp().globalData.token
    };
    var data = {
        ownerBy: ownerBy,
        createdBy: ownerBy
    }
    return httpclient.put(config.host + '/ufm/api/v2/migration/teamspaces/' + teamId, data, header, callback)
}

/**
 * 批量移交权限空间
 */
function batchAuthTransfer(destUserId, teams, callback) {
    var header = {
        Authorization: getApp().globalData.token
    };
    var data = {
        destUserId: destUserId,
        teams: teams
    }
    return httpclient.put(config.host + '/ufm/api/v2/migration/teamspaces/batch', data, header, callback)
}

/**
 * 移交文档
 */
function transferDocument(ownerId, destOwnerId, destParent, enterpriseUserId, srcNodes, callback) {
    var header = {
        Authorization: getApp().globalData.token
    };
    var data = {
        destOwnerId: destOwnerId,
        destParent: destParent,
        srcNodes: srcNodes,
        enterpriseUserId: enterpriseUserId
    };
    return httpclient.post(config.host + '/ufm/api/v2/migration/nodes/' + ownerId, data, header, callback)
}

/**
 * 删除文件
 */
function deleteNodes(ownerId, nodeData, callback) {
    var header = {
        Authorization: getApp().globalData.token
    };
    var data = nodeData;
    return httpclient.remove(config.host + '/ufm/api/v2/migration/folders/' + ownerId, data, header, callback)
}
/**
 * 检测离职用户是否还有文件
 */
function testUserLeaveStatus(userId, callback) {
    var header = {
        Authorization: getApp().globalData.token
    };
    var data = {}
    return httpclient.get(config.host + '/ufm/api/v2/migration/' + userId + '/fileCheck', data, header, callback)
}
/**
 * 删除用户
 */
function deleteLeaveEmployee(employeeId, callback) {
    var header = {
        Authorization: getApp().globalData.token
    };
    var data = {}
    return httpclient.put(config.host + '/ecm/api/v2/enterprise/employees/' + employeeId + '/handover', data, header, callback)
}
module.exports = {
    getUserPhone,
    registerEnterprise,
    getDeptAndEmployees,
    createEmployees,
    createDept,
    deleteDept,
    deleteEmploy,
    getIdentifyingCode,
    validateCode,
    setDocumentAudit,
    getEmploye,
    getDepartmentByParentId,
    updateDepartmentEmployment,
    addDepartmentEmployment,
    isExistEmploye,
    selectEmployee,
    addDepartmentMember,
    getManager,
    getDepartmentByDeptId,
    getEnterpriseList,
    deleteDepartmentMember,
    toggleState,
    getLeaveEmployeList,
    getAuthTransferList,
    getCoopNodes,
    transferCoopAuth,
    batchAuthTransfer,
    transferDocument,
    deleteNodes,
    testUserLeaveStatus,
    deleteLeaveEmployee
};
