var enterpriseClient = require("../../module/enterprise.js");
var config = require("../../config.js");
Page({

    /**
     * 页面的初始数据
     */
    data: {
        scrollHeight: "",
        windowHeight: wx.getSystemInfoSync().windowHeight,
        leaveEmployeList: []
    },


    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        var page = this;
        var windowHeight = page.data.windowHeight;
        var scrollHeight = (windowHeight - 60) + "px"
        page.setData({
            scrollHeight: scrollHeight
        })
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
        var page = this;
        getLeaveEmploye(page)

    },
    toTransfer: function (e) {
        var page = this;
        var id = e.currentTarget.dataset.id;
        var userId = e.currentTarget.dataset.userId
        wx.setStorageSync("leaveEmployeAlias", e.currentTarget.dataset.alias);
        wx.setStorageSync("employeCloudUserId", userId)
        enterpriseClient.testUserLeaveStatus(id, function (data) {
            if (data) {
                wx.navigateTo({
                    url: 'transfer/transfer?employeId=' + id,
                })
            }else{
                wx.showModal({
                    title: '提示',
                    content: '该员工没可移交的权限和文档，系统将自动转动已离职状态',
                    success: function(res){
                        if(res.confirm){
                            enterpriseClient.deleteLeaveEmployee(userId,function(data){
                                getLeaveEmploye(page)
                            })
                        }
                    }
                })
            }
        })
    }
})
// 加载页面数据方法
function getLeaveEmploye(page) {
    var leaveEmployeList = []
    var departNames = ""
    enterpriseClient.getLeaveEmployeList(function (data) {
        var leaveEmployeData = data.content
        
        for (let i = 0; i < leaveEmployeData.length; i++) {
            var deptName = []
            leaveEmployeData[i].icon = config.host + "/ecm/api/v2/users/getAuthUserImage/" + leaveEmployeData[i].cloudUserId + "?authorization=" + getApp().globalData.token;
            for (let j = 0; j < leaveEmployeData[i].depts.length;j++){
                console.log(leaveEmployeData[i].depts)
                deptName.push(leaveEmployeData[i].depts[j].name)
            }
            leaveEmployeData[i].deptNames = splitArry(deptName)
            leaveEmployeList.push(leaveEmployeData[i])
        }

        page.setData({
            leaveEmployeList: leaveEmployeList
        })
    })
}

function splitArry(arry) {
    var nameArry = arry
    return nameArry.join("/")
}