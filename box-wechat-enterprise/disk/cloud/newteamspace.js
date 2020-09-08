// disk/cloud/newteamspace.js
var session = require("../../session.js");
var File = require("../module/file.js");
var utils = require("../module/utils.js");
var config = require("../config.js");
var app = getApp();

var teamId = 0;
var deorquits =0;
Page({

    /**
     * 页面的初始数据
     */
    data: {
        name: '',
        change: true,
        showname: true,
        teamspacelist: [],
        scrollHeight: (wx.getSystemInfoSync().windowHeight - 205) + "px",
        ifteam: false,
        ids: '',
        addmember: true,
        deletebtn: true
    },
    bindblur: function (e) {
        this.setData({
            name: e.detail.value
        })
    },
    changename: function () {
        this.setData({
            change: false
        })
    },
    showinput: function () {
        this.setData({
            showname: true,
            change: false
        })
    },
    switchChange: function (e) {
        var check = e.detail.value
        if (check === true) {
        } else {
        }
    },
    addmember: function () {
        wx.navigateTo({
            url: '/disk/cloud/addmember?ids=' + teamId,
        })
    },
    newteamspace: function () {
        var self = this;
        if (self.data.name == undefined || self.data.name == "" || self.data.name.trim() == ""){
            wx.showToast({
                title: '空间名不能为空',
                icon: 'none',
                duration: 1000
            })
            return;
        }
        if (self.data.teamspacelist == undefined || self.data.teamspacelist.length == 0) {
            File.teamspaces(app.globalData.token, self.data.name, function (data) {
                wx.showToast({
                    title: '创建成功',
                });
                wx.navigateBack({
                    delta: 1
                })
            });
        } else {
            File.teamspaces(app.globalData.token, self.data.name, function (data) {
                var ownedBy = data.id;
                var teamlist = self.data.teamspacelist;
                for (var i = 0; i < teamlist.length; i++) {
                    delete teamlist[i].name;
                    delete teamlist[i].icon;
                }

                File.addmember(app.globalData.token, teamlist, ownedBy, function (data) {
                    wx.showToast({
                        title: '添加成功',
                    });
                    wx.navigateTo({
                        url: '/disk/cloud/teamspacelist',
                    })

                });

            });
        }

    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        wx.removeStorageSync('teamspacemember');
        teamId = options.ids;
        deorquits = options.deorquit;
        if (deorquits === '0') {
            this.setData({
                addmember: false,
                deletebtn: false
            })
        }
        if (teamId === undefined) {
        } else {
            this.setData({
                ifteam: true
            })
            
            this.setData({
                scrollHeight: (wx.getSystemInfoSync().windowHeight - 50) + "px",
            });
            return;

        }
    },

    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady: function () {
        wx.removeStorageSync('teamspacemember');
        wx.removeStorageSync('memberships');
    },

    /**
     * 生命周期函数--监听页面显示
     */
    onShow: function () {
        var selfs = this;
        if(teamId != undefined){
            getTeamMember(teamId, selfs);
        } else {
            var memberlist = wx.getStorageSync('teamspacemember');
            selfs.setData({
                teamspacelist: memberlist
            })
        }

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

    },
    deleteMember: function(e){
        var userId = e.currentTarget.dataset.userId;
        var selfs = this;
        wx.showModal({
            cancelText: "取消",
            confirmText: "删除",
            title: '提示',
            content: '是否删除？',
            success: function (res) {
                if (res.confirm) {
                    File.deleteMember(userId, teamId, function () {
                        getTeamMember(teamId, selfs);
                    });
                }
            }
        })
        
        
    }
})

function getTeamMember(teamId, selfs){
    File.memberteamlist(app.globalData.token, teamId, function (data) {
        for (let i = 0; i < data.memberships.length; i++) {
            if (data.memberships[i].member.type == 'department') {
                data.memberships[i].member.icon = "/disk/images/department-avatar.png";
            } else {
                data.memberships[i].member.icon = config.host + "/ecm/api/v2/users/getAuthUserImage/" + data.memberships[i].member.id + "?authorization=" + getApp().globalData.token;
            }
        }
        selfs.setData({
            teamspacelist: data.memberships,
            ifteam:true

        })
    })
}
