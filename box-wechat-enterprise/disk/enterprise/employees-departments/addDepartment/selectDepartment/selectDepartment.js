var enterpriseClient = require("../../../../module/enterprise.js");
var config = require("../../../../config.js");
var crumbs = [];
var currentDeptId = 0;

Page({

    /**
     * 页面的初始数据
     */
    data: {
        isShow: false,
        department: [],
        currentDepartment: {},
        departmentIndex: -1,
        deptId: -1,
        currentDeptID: "",
    },

    searchDepartment: function (e) {
        var value = e.detail.value;
        // var data = {
        //     deptId: this.data.currentDeptID,
        //     searchName: value
        // };
        var page = this;

        // getDepartmentByParentId(0, page);

        // 提供搜索企业名
        enterpriseClient.getDepartmentByDeptId(value, function (data) {
            for (var i = 0; i < data.length; i++) {
                data[i].avatar = config.host + "/ecm/api/v2/users/getAuthUserImage/" + data[i].cloudUserId + "?authorization=" + getApp().globalData.token;
            }
            page.setData({
                isShow: true,
                department: data
            });
        })
    },

    setDepartmentIndex: function (e) {
        var index = e.currentTarget.dataset.index;
        var item = e.currentTarget.dataset.item;
        this.setData({
            deptId: item.userId,
            departmentIndex: index,
        })
    },

    setDepartment: function () {
        var department = this.data.currentDepartment;
        var deptId = this.data.deptId;
        var employeeId = this.data.employeeId;
        // 设置用户部门
        enterpriseClient.addDepartmentEmployment(deptId, employeeId, function (res) {
            var res = res;
            wx.redirectTo({
                url: '/disk/enterprise/employees-departments/addDepartment/addEmployee?employeeId=' + employeeId + '&deptId=' + deptId,
            });
        })
    },

    back: function (e) {
        wx.navigateBack({
            delta: 1
        })
    },

    // // 点击导航
    // clickCrumb: function (e) {
    //     var crumb = e.currentTarget.dataset.crumb;
    //     currentDeptId = crumb.deptId;

    //     var index = e.currentTarget.dataset.crumb.index;
    //     crumbs = crumbs.splice(0, index);
    //     this.setData({
    //         crumbs: crumbs,
    //         currentDeptId: currentDeptId
    //     });

    //     var page = this;
    //     getDepartmentByParentId(currentDeptId, page);
    // },

    // // 点击部门
    // onClickDepartment: function (e) {
    //     var page = this;
    //     var deptId = e.currentTarget.dataset.deptId;
    //     var name = e.currentTarget.dataset.name;

    //     currentDeptId = deptId;
    //     var crumb = {
    //         'index': crumbs.length + 1,
    //         'name': name,
    //         'deptId': deptId
    //     };
    //     crumbs.push(crumb);
    //     page.setData({
    //         crumbs: crumbs,
    //         currentDeptId: currentDeptId
    //     });

    //     getDepartmentByParentId(deptId, page);
    // },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        this.setData({
            employeeId: options.employeeId
        });

        // //crumb
        // crumbs = [];
        // var page = this;
        // var crumb = {
        //     'index': crumbs.length + 1,
        //     "name": getApp().globalData.enterpriseName,
        //     "deptId": 0
        // }
        // currentDeptId = 0;
        // crumbs.push(crumb);
        // page.setData({
        //     crumbs: crumbs
        // });

    }
})

function getDepartmentByParentId(deptId, page) {
    var depts = [];
    var employees = [];
    enterpriseClient.getDepartmentByDeptId(deptId, function (data) {
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