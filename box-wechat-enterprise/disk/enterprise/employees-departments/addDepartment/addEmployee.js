// disk/enterprise/employees-departments/addDepartment/addEmployee.js
var enterpriseClient = require("../../../module/enterprise.js");

Page({

    /**
     * 页面的初始数据
     */
    data: {
        employeeId: 0,
        roles: [
            // '普通成员', '知识专员', '主管'
            '普通成员', '主管'
        ],
        selectedRole: 0,
        selectedItem: '',
        deptId: -1,
    },

    onSelectRole: function (e) {
        var index = e.currentTarget.dataset.index;
        var item = e.currentTarget.dataset.item;
        this.setData({
            selectedRole: index,
            selectedItem: item
        })
    },

    completed: function (e) {
        var roleIndex = this.data.selectedRole;
        var roles = this.data.roles;
        if (roleIndex === -1) {
            wx.showToast({
                title: '请选择职务！',
            })
        } else {
            var page = this;

            var data = {
                departmentId: page.data.deptId,
                role: roleIndex,
                enterpriseUserId: page.data.employeeId,
                enterpriseId: getApp().globalData.enterpriseId
            }
            enterpriseClient.addDepartmentMember(data, function (data) {
                wx.navigateBack({
                    delta: 2
                })
            });
        }
    },

    deleteDepartment: function () {
        var employeeId = this.data.employeeId;
        var deptId = this.data.deptId;
        var enterpriseId = getApp().globalData.enterpriseId;
        enterpriseClient.deleteDepartmentMember(enterpriseId, deptId, employeeId, function (data) {
            var res = data;
        })
    },


    onLoad: function (options) {
        this.setData({
            employeeId: options.employeeId,
            deptId: options.deptId
        })
    }
})
