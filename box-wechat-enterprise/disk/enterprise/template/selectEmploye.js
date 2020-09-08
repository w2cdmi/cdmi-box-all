// disk/enterprise/template/selectEmploye.js

var enterpriseClient = require("../../module/enterprise.js");
var config = require("../../config.js");

var crumbs = [];
var currentDeptId = 0;

var type = "setManager";  //页面确认操作方法
Page({

    /**
     * 页面的初始数据
     */
    data: {
        departmentIndex: -1,
        showSelectDepartment: false,
        employee: [],
        managerIndex: -1,
        // 部门筛选
        currentDepartment: "上海分公司",
        // 横向排列的已选中的公司名
        crumbs: [],
        departments: [],
        preCrumbs: [],  // 点击取消选择，之后再进来显示原来的部门列表
        preDepartments: [],
        departmentName: "", // 部门名字,
        deptId: -1, // 要设置部门主管的部门id
        managerId: '', // 选中的员工,
        isKnowledger: false, // 是否是知识管理员
        confirmChooseType: "setManager", //确认选择类型
        hiddenView: true
    },

    setManagerIndex: function (e) {
        var index = e.currentTarget.dataset.index;
        var item = e.currentTarget.dataset.item;
        this.setData({
            managerIndex: index,
            manager: item,
        });
    },


    toSelectEmployee: function (e) {
        var departmentName = this.data.departmentName;
        var confirmChooseType = this.data.confirmChooseType
        wx.navigateTo({
            url: '/disk/enterprise/selectEmployee/selectEmployee?deptId=' + this.data.deptId + "&currentDeptID=" + crumbs[crumbs.length - 1].deptId + "&departmentName=" + departmentName + "&confirmChooseType=" + confirmChooseType,
        })
    },

    setManager: function () {
        var manager = this.data.manager;
        var deptId = this.data.deptId;
        var name = this.data.departmentName;
        var data = {
            employeeId: manager.id
        };

        if (this.data.isKnowledger) {
            // 设置知识管理员
            enterpriseClient.setManager(deptId, data, function (res) {
                var res = res;
                wx.showToast({
                    title: '成功将' + manager.alias + '设置为部门知识管理员！',
                    success: wx.navigateTo({
                        url: '/disk/enterprise/reselection/knowledger?id=' + manager.id + "&departmentName=" + name + "&deptId=" + deptId,
                    })
                })
            });
        } else {
            var data = {
                departmentId: deptId,
                role: 1,
                enterpriseUserId: manager.id,
                enterpriseId: getApp().globalData.enterpriseId
            }
            enterpriseClient.addDepartmentMember(data, function (res) {
                var res = res;
                wx.showToast({
                    title: '成功将' + manager.alias + '设置为部门主管！',
                    duration: 500
                })
                setTimeout(function () {
                    success: wx.navigateBack({
                        delta: 2
                    })
                    wx.navigateTo({
                        url: '/disk/enterprise/reselection/manager?id=' + manager.id + "&departmentName=" + name + "&deptId=" + deptId,
                    })
                }, 500);
            });
        }
    },

    // 确认单个移交协作空间权限的方法
    setTransferAuth: function () {
        var ownerId = this.data.manager.cloudUserId;
        var teamId = wx.getStorageSync("transferCoopspaceId");
        var enterpriseUserId = wx.getStorageSync("employeCloudUserId");
        enterpriseClient.transferCoopAuth(ownerId, teamId, function (data) {
            wx.showToast({
                title: '移交成功',
                success: function () {
                    wx.removeStorageSync("transferCoopspaceId");
                    setTimeout(function () {
                        wx.navigateBack({
                            delta: 1
                        })
                    }, 300);
                },
                duration: 300
            })
        })
    },
    //确认批量移交协作空间权限的方法
    setBatchTransferAuth: function () {
        var destUserId = this.data.manager.cloudUserId;
        var teams = wx.getStorageSync("checkData");
        enterpriseClient.batchAuthTransfer(destUserId, teams, function(data){
            wx.showToast({
                title: '移交成功',
                success: function () {
                    wx.removeStorageSync("checkData");
                    setTimeout(function () {
                        wx.navigateBack({
                            delta: 1
                        })
                    }, 300);
                },
                duration: 300
            })
        })
    },

    // 确认移交离职人员单个文档的方法
    setTransferDocument: function () {
        var destOwnerId = this.data.manager.cloudUserId;
        var getStorageNodes = wx.getStorageSync("documentObject");
        var enterpriseUserId = wx.getStorageSync("employeCloudUserId")
        var ownerId = getStorageNodes.ownerId;
        var destParent = getStorageNodes.destParent;
        var managerAlias = this.data.manager.alias;
        var srcNodes = [{
            id: getStorageNodes.nodeId,
            ownedBy: getStorageNodes.ownerId,
            createdBy: getStorageNodes.ownerId,
            name: getStorageNodes.name,
            size: getStorageNodes.size,
            modifiedAt: getStorageNodes.modifiedAt,
            type: getStorageNodes.type
        }];
        enterpriseClient.transferDocument(ownerId, destOwnerId, destParent, enterpriseUserId, srcNodes, function (data) {
            wx.showToast({
                title: '移交成功',
                success: function () {
                    wx.removeStorageSync("documentObject")
                    setTimeout(function () {
                        wx.redirectTo({
                            url: "/disk/enterprise/leaveManagement/transfer/transferFinish/transferFinish?managerAlias=" + managerAlias + "&transferBoundType=0"
                        })
                    }, 300);
                },
                duration: 300
            })
        })
    },
    // 确认文档批量移交
    transferMoreDocument: function () {
        var destOwnerId = this.data.manager.cloudUserId;
        var ownerId = wx.getStorageSync("leaveEmployeOwnerId");
        var enterpriseUserId = wx.getStorageSync("employeCloudUserId");
        var managerAlias = this.data.manager.alias;
        var destParent = 0;
        var srcNodes = wx.getStorageSync("checkData");
        enterpriseClient.transferDocument(ownerId, destOwnerId, destParent, enterpriseUserId, srcNodes, function (data) {
            wx.showToast({
                title: '移交成功',
                success: function () {
                    wx.removeStorageSync("documentObject")
                    setTimeout(function () {
                        wx.redirectTo({
                            url: "/disk/enterprise/leaveManagement/transfer/transferFinish/transferFinish?managerAlias=" + managerAlias + "&transferBoundType=0"
                        })
                    }, 300);
                },
                duration: 300
            })
        })
    },

    showSelectDepartment: function () {
        this.setData({
            showSelectDepartment: true
        })
    },

    // 隐藏筛选部门界面，不做改动
    cancelhideSelectDepartment: function () {
        var preCrumbs = this.data.preCrumbs;
        var preDepartments = this.data.preDepartments
        if (!preCrumbs) {
            preCrumbs = this.data.crumbs;
        }

        this.setData({
            showSelectDepartment: false,
            crumbs: preCrumbs,
            departments: preDepartments
        });
    },

    hideSelectDepartment: function () {
        this.setData({
            showSelectDepartment: false,
            preCrumbs: this.data.crumbs,
            preDepartments: this.data.departments
        });
        var data = {
            deptId: crumbs[crumbs.length - 1].deptId,
        };
        var page = this;
        enterpriseClient.selectEmployee(data, function (res) {
            var resData = res.content;
            for (var i = 0; i < resData.length; i++) {
                resData[i].avatar = config.host + "/ecm/api/v2/users/getAuthUserImage/" + resData[i].cloudUserId + "?authorization=" + getApp().globalData.token;
            }
            page.setData({
                employee: resData,
            });
        })
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        if (options.type) {
            type = options.type;
        }
        this.setData({
            confirmChooseType: type
        })
        if (type !== "setManager") {
            wx.setNavigationBarTitle({
                title: '设置移交接收人员'
            });
            this.setData({
                hiddenView: false
            })
        }
        // 是知识管理员的页面
        if (options.knowledger) {
            this.setDate({
                isKnowledger: true
            });
        }
        crumbs = [];
        var name = getApp().globalData.enterpriseName;
        var crumb = {
            'index': crumbs.length + 1,
            "name": name.length > 5 ? name.slice(0, 5) + '...' : name,
            "deptId": 0
        }
        currentDeptId = 0;
        var page = this;
        crumbs.push(crumb);
        page.setData({
            currentDepartment: name,
            crumbs: crumbs,
            deptId: options.deptId,
            departmentName: options.departmentName
        });
        enterpriseClient.getDepartmentByParentId(0, function (data) {
            page.setData({
                departments: data
            });
        });


        // 进入页面，获取当前部门列表下的员工信息
        var data = {
            deptId: crumbs[crumbs.length - 1].deptId,
        };
        var page = this;
        enterpriseClient.selectEmployee(data, function (res) {
            var resData = res.content;
            for (var i = 0; i < resData.length; i++) {
                resData[i].avatar = config.host + "/ecm/api/v2/users/getAuthUserImage/" + resData[i].cloudUserId + "?authorization=" + getApp().globalData.token;
            }
            page.setData({
                employee: resData,
            });
        })

    },
    // 选中的部门列表筛选（横向）
    crumbDepartment: function (e) {
        var crumb = e.currentTarget.dataset.crumb;
        currentDeptId = crumb.deptId;

        var index = e.currentTarget.dataset.crumb.index;
        crumbs = crumbs.splice(0, index);
        this.setData({
            crumbs: crumbs,
            currentDeptId: currentDeptId
        });

        var page = this;
        getDepartmentByParentId(currentDeptId, page);

    },
    // 未选的公司列表筛选（竖向）
    selectDepartment: function (e) {
        var index = e.currentTarget.dataset.index;
        var deptId = e.currentTarget.dataset.deptId;
        var name = e.currentTarget.dataset.name;
        var page = this;

        var crumb = {
            'index': crumbs.length + 1,
            'name': name.length > 5 ? name.slice(0, 5) + '...' : name,
            'deptId': deptId
        };
        crumbs.push(crumb);
        var currentDeptId = deptId;

        page.setData({
            currentDepartment: name,
            crumbs: crumbs,
            currentDeptId: currentDeptId,
            department2Index: index
        });
        
        var currentDepartment = e.currentTarget.dataset.index;
        // 获取部门列表
        getDepartmentByParentId(currentDeptId, page);
        this.setData({
            department2Index: -1
        });
    },

    sureSelectEmploye: function () {
        var page = this;
        if (page.data.managerIndex === -1) {
            wx.showToast({
                title: '请选择成员',
                icon: 'none'
            });
            return;
        }
        switch (type) {
            case "setManager":
                page.setManager();
                break;
            case "transferAuth":
                page.setTransferAuth();
                break;
            case "batchTransferAuth":
                page.setBatchTransferAuth();
                break;
            case "transferDocument":
                page.setTransferDocument();
                break;
            case "transferMoreDocument":
                page.transferMoreDocument();
                break;
        }
    }
})

function getDepartmentByParentId(deptId, page) {
    enterpriseClient.getDepartmentByParentId(deptId, function (data) {
        page.setData({
            departments: data
        });
    });
}
