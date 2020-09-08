// disk/enterprise/selectEmployee/selectEmployee.js
var enterpriseClient = require("../../module/enterprise.js");
var config = require("../../config.js");
var type = "setManager";  //页面确认操作方法
Page({

    /**
     * 页面的初始数据
     */
    data: {
        isShow: false,
        employee: [],
        currentEmployee: {},
        managerIndex: -1,
        deptId: -1,
        currentDeptID: "",
    },

    searchEmployee: function (e) {
        var value = e.detail.value;
        var data = {
            deptId: this.data.currentDeptID,
            search: value
        };
        var page = this;
        enterpriseClient.selectEmployee(data, function (data) {
            var data = data.content;
            for (var i = 0; i < data.length; i++) {
                data[i].avatar = config.host + "/ecm/api/v2/users/getAuthUserImage/" + data[i].cloudUserId + "?authorization=" + getApp().globalData.token;
            }
            page.setData({
                isShow: true,
                employee: data
            });
        })
    },

    setManagerIndex: function (e) {
        var index = e.currentTarget.dataset.index;
        var item = e.currentTarget.dataset.item;
        this.setData({
            currentEmployee: item,
            managerIndex: index,
        })
    },

    setManager: function () {
        var manager = this.data.currentEmployee;
        var deptId = this.data.deptId;
        var data = {
            employeeId: manager.id
        };
        var page = this;
        enterpriseClient.setManager(deptId, data, function (res) {
            var res = res;
            wx.showToast({
                title: '成功将' + manager.alias + '设置为部门主管！',
                success: wx.redirectTo({
                    url: '/disk/enterprise/reselection/manager?id=' + manager.id + "&departmentName=" + page.data.departmentName + "&deptId=" + deptId,
                })
            })
        });
    },
    // 确认移交协作空间权限的方法
    setTransferAuth: function () {
        var ownerId = this.data.currentEmployee.cloudUserId;
        var teamId = wx.getStorageSync("transferCoopspaceId");
        var enterpriseUserId = wx.getStorageSync("employeCloudUserId");
        enterpriseClient.transferCoopAuth(ownerId, teamId, function (data) {
            wx.showToast({
                title: '移交成功',
                success: function () {
                    wx.removeStorageSync("transferCoopspaceId");
                    setTimeout(function () {
                        wx.navigateBack({
                            delta: 2
                        })
                    }, 300);
                },
                duration: 300
            })
        })
    },

    //确认批量移交协作空间权限的方法
    setBatchTransferAuth: function () {
        var destUserId = this.data.currentEmployee.cloudUserId;
        var teams = wx.getStorageSync("checkData");
        enterpriseClient.batchAuthTransfer(destUserId, teams, function (data) {
            wx.showToast({
                title: '移交成功',
                success: function () {
                    wx.removeStorageSync("checkData");
                    setTimeout(function () {
                        wx.navigateBack({
                            delta: 2
                        })
                    }, 300);
                },
                duration: 300
            })
        })
    },

    // 确认移交离职人员单个文档的方法
    setTransferDocument: function () {
        var destOwnerId = this.data.currentEmployee.cloudUserId;
        var getStorageNodes = wx.getStorageSync("documentObject");
        var enterpriseUserId = wx.getStorageSync("employeCloudUserId")
        var ownerId = getStorageNodes.ownerId;
        var destParent = getStorageNodes.destParent;
        var managerAlias = this.data.currentEmployee.alias;
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
                            url: "/disk/enterprise/leaveManagement/transfer/transferFinish/transferFinish?managerAlias=" + managerAlias + "&transferBoundType=1"
                        })
                    }, 300);
                },
                duration: 300
            })
        })
    },
    // 确认文档批量移交
    transferMoreDocument: function () {
        var destOwnerId = this.data.currentEmployee.cloudUserId;
        var ownerId = wx.getStorageSync("leaveEmployeOwnerId");
        var enterpriseUserId = wx.getStorageSync("employeCloudUserId");
        var managerAlias = this.data.currentEmployee.alias;
        var destParent = 0;
        var srcNodes = wx.getStorageSync("checkData");
        enterpriseClient.transferDocument(ownerId, destOwnerId, destParent, enterpriseUserId, srcNodes, function (data) {
            wx.showToast({
                title: '移交成功',
                success: function () {
                    wx.removeStorageSync("documentObject")
                    setTimeout(function () {
                        wx.redirectTo({
                            url: "/disk/enterprise/leaveManagement/transfer/transferFinish/transferFinish?managerAlias=" + managerAlias + "&transferBoundType=1"
                        })
                    }, 300);
                },
                duration: 300
            })
        })
    },
    back: function (e) {
        wx.navigateBack({
            delta: 1
        })
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        type = options.confirmChooseType;
        this.setData({
            deptId: options.deptId,
            currentDeptID: options.currentDeptID,
            departmentName: options.departmentName
        })
    },
    sureSelectEmploye: function () {
        var page = this;
        if (this.data.managerIndex === -1) {
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

    }
})