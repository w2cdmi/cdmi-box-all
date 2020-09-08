// disk/enterprise/employees-departments/person-details/person-details.js
var enterpriseClient = require("../../../module/enterprise.js");
var config = require("../../../config.js");

Page({

    data: {
        employeeId: 0,
        employee: {},
        hasJob: true,
        job: [],
        roles: [],
        isShowDelete: false,
        canDeletePerson: 'yes',
    },

    showDelete: function () {
        this.setData({
            isShowDelete: true
        })
    },

    gotoPersonState: function () {
        var employeeId = this.data.employeeId;
        var canDeletePerson = this.data.canDeletePerson;
        wx.navigateTo({
            url: '/disk/enterprise/employees-departments/person-details/personState/personState?employeeId=' + employeeId + '&canDeletePerson=' + canDeletePerson,
        })
    },

    deleteDepartment: function (e) {
        var item = e.currentTarget.dataset.item;
        var employeeId = this.data.employeeId;
        var enterpriseId = getApp().globalData.enterpriseId;
        var role = item.role === 0 ? '普通成员' : '主管';
        var page = this;
        wx.showModal({
            title: '',
            content: '确定要删除' + item.name + '-' + role + '吗？',
            success: function (res) {
                enterpriseClient.deleteDepartmentMember(enterpriseId, item.departmentId, employeeId, (data) => {
                    var res = data;
                    getEmployeeInfo({ page: this });
                });
            },
            fail: function () {
                wx.showToast({
                    title: '请重试'
                })
            }
        })
    },

    hideDelete: function () {
        this.setData({
            isShowDelete: false
        })
    },

    // 新增职务页面
    addDepartment: function () {
        var page = this;
        wx.navigateTo({
            url: '/disk/enterprise/employees-departments/addDepartment/addDepartment?employeeId=' + page.data.employeeId,
        });
    },

    onLoad: function (options) {
        var employeeId = options.employeeId;
        this.setData({
            employeeId: employeeId
        });
    },

    onShow: function () {
        getEmployeeInfo({page: this});
    },
})

function getEmployeeInfo({ page }) {
    enterpriseClient.getEmploye(page.data.employeeId, function (data) {
        data.icon = config.host + "/ecm/api/v2/users/getAuthUserImage/" + data.cloudUserId + "?authorization=" + getApp().globalData.token;
        var employee = {
            name: data.name,
            sex: "男",
            email: '',
            mobile: data.mobile,
            avatarUrl: data.icon,
            state: data.status === -2 ? '离职' : '在职' // 判断在职/离职状态
        }
        page.setData({
            employee: employee,
            job: data.depts
        });
    });

}