// disk/enterprise/employees-departments/person-details/personState/personState.js
var enterpriseClient = require("../../../../module/enterprise.js");

Page({

    /**
     * 页面的初始数据
     */
    data: {
        state: ['在职', '离职'],
        personState: '在职',
        employeeId: '',
    },

    toggleState: function (e) {
        var item = e.currentTarget.dataset.item;
        var page = this;
        var employeeId = page.data.employeeId;
        var personState = page.data.personState;
        var canDeletePerson = page.data.canDeletePerson;
        if (personState === item) return;
        if (item === '离职') {
            wx.showModal({
                content: '是否确定该成员为离职状态',
                success: function (res) {
                    if (res.confirm) {
                        if (canDeletePerson === 'no') {
                            wx.showModal({
                                title: '错误',                                
                                content: '不能将部门管理员设置为离职',
                            });
                        } else {
                            page.setData({
                                personState: '离职'
                            });
                            enterpriseClient.toggleState(employeeId, function () {
                                wx.showModal({
                                    content: '已设置该成员为离职状态',
                                })
                                wx.navigateBack({
                                    delta: 2
                                })
                            });
                        }
                    }
                }
            })
            return;
        }
    },
    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        this.setData({
            employeeId: options.employeeId,
            canDeletePerson: options.canDeletePerson
        })
    }
})