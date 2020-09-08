// disk/enterprise/employees-departments/addDepartment/addDepartment.js
var enterpriseClient = require("../../../module/enterprise.js");
var config = require("../../../config.js");
var crumbs = [];
var currentDeptId = 0;

Page({

    /**
     * 页面的初始数据
     */
    data: {
        employeeId: 0,
        selectedDepartment: -1,
        enterpriseName: "搜索部门",
        crumbs: '',
        departments: [],
        deptId: '',
    },

    onSelectDepartment: function (e) {
        var index = e.currentTarget.dataset.index;
        var item = e.currentTarget.dataset.item;
        this.setData({
            selectedDepartment: index,
            deptId: item.userId
        })
    },

    toSearchDepartment: function () {
        wx.navigateTo({
            url: '/disk/enterprise/employees-departments/addDepartment/selectDepartment/selectDepartment?employeeId=' + this.data.employeeId,
        })
    },

    toRoleSet: function () {
        var page = this;
        if (page.data.selectedDepartment !== -1) {
            // 设置用户部门
            wx.navigateTo({
                url: '/disk/enterprise/employees-departments/addDepartment/addEmployee?employeeId=' + page.data.employeeId + '&deptId=' + page.data.deptId,
            });
        } else {
            wx.showToast({
                title: '请选择部门！',
            })
        }
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {

        this.setData({
            employeeId: options.employeeId
        });

        //crumb
        crumbs = [];
        var page = this;
        var crumb = {
            'index': crumbs.length + 1,
            "name": getApp().globalData.enterpriseName,
            "deptId": 0
        }
        currentDeptId = 0;
        crumbs.push(crumb);
        page.setData({
            crumbs: crumbs
        });
        getDepartmentByParentId(0, page);

    },
    // 点击导航
    clickCrumb: function (e) {
        var crumb = e.currentTarget.dataset.crumb;
        currentDeptId = crumb.deptId;

        var index = e.currentTarget.dataset.crumb.index;
        crumbs = crumbs.splice(0, index);
        this.setData({
            crumbs: crumbs,
            currentDeptId: currentDeptId,
            selectedDepartment: -1
        });

        var page = this;
        getDepartmentByParentId(currentDeptId, page);
    },
    // 点击部门
    onClickDepartment: function (e) {
        if (e.currentTarget.dataset.item.subDepts !== 0) {
            var page = this;
            var deptId = e.currentTarget.dataset.deptId;
            var name = e.currentTarget.dataset.name;

            currentDeptId = deptId;
            var crumb = {
                'index': crumbs.length + 1,
                'name': name,
                'deptId': deptId
            };
            crumbs.push(crumb);
            page.setData({
                crumbs: crumbs,
                currentDeptId: currentDeptId,
                selectedDepartment: -1
            });

            getDepartmentByParentId(deptId, page);
        }
    }
})

function getDepartmentByParentId(deptId, page) {
    var depts = [];
    var employees = [];
    enterpriseClient.getDeptAndEmployees(deptId, function (data) {
        for (var i = 0; i < data.length; i++) {
            if (data[i].type == 'department') {
                data[i].icon = "/disk/images/department-avatar.png";
                depts.push(data[i]);
            } else {
                data[i].icon = config.host + "/ecm/api/v2/users/getAuthUserImage/" + data[i].id + "?authorization=" + getApp().globalData.token;
                employees.push(data[i]);
            }
        }
        page.setData({
            departments: depts,
            employees: employees
        });
    });
}