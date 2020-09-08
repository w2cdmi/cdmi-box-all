// disk/cloud/teamspacelist.js
var file = require("../module/file.js");
var utils = require("../module/utils.js");
var menu = require("../template/menu.js");
var app = getApp();
// quitTeam
var type = 0;   // 1： 部门空间  0： 协作空间

Page({
    /**
     * 页面的初始数据
     */
    data: {
        cells: [],
        scrollTop: 40,
        scrollHeight: 0,
        showModalStatus: false,
        id: '',
        deletespace: '',
        memberspace: '',
        deorquit: '',
        memberquit: ''
    },
    clickitem: function (e) {
        var name = e.currentTarget.dataset.delespace;
        var memberspace = e.currentTarget.dataset.memberspace;
        var deorquits = e.currentTarget.dataset.deorquit;
        var memberquits = e.currentTarget.dataset.item.memberquit;
        if (memberquits == undefined){
            memberquits = '';
        }
        this.setData(
            {
                deletespace: name,
                memberspace: memberspace,
                deorquit: deorquits,
                memberquit: memberquits,
                showModalStatus: true,
                id: e.currentTarget.dataset.item.id
            }
        );
    },
    showmemner: function () {
        var ids = this.data.id;
        var deorquit = this.data.deorquit;
        wx.navigateTo({
            url: '/disk/cloud/newteamspace?ids=' + ids + '&deorquit=' + deorquit
        })
    },
    memnerclose() {
        this.setData(
            {
                showModalStatus: false
            }
        );
    },

    /**
     * 生命周期函数--监听页面加载
     */

    onLoad: function (options) {
        var page = this;
        var offset = 0;   //初始值0
        var limit = 100;   //加载数量
        type = options.type;
        // if (options.type == 0) {
        //     getTeamSpaceList(app.globalData.token, app.globalData.cloudUserId, offset, limit, page);
        // } else if (options.type == 1) {
        //     getDepartmentSpaceList(app.globalData.token, app.globalData.cloudUserId, offset, limit, page);
        // }
        wx.getSystemInfo({
            success: function (res) {
                page.setData({
                    scrollHeight: res.windowHeight
                });
            },
        });
    },

    /**
     * 生命周期函数--监听页面显示
     */
    onShow: function (options) {
        wx.setNavigationBarTitle({ title: getApp().globalData.enterpriseName });
        var page = this;
        var offset = 0;   //初始值0
        var limit = 100;   //加载数量
        if (type == 1) {
            getDepartmentSpaceList(app.globalData.token, app.globalData.cloudUserId, offset, limit, page);
        } else {
            page.setData({
                isTeamspace: true
            });
            getTeamSpaceList(app.globalData.token, app.globalData.cloudUserId, offset, limit, page);
        }
        wx.getSystemInfo({
            success: function (res) {
                page.setData({
                    scrollHeight: res.windowHeight
                });
            },
        });
    },

    newteamspace: function newteamspace() {
        wx.navigateTo({
            url: '/disk/cloud/newteamspace'
        });
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

    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady: function () {
    },

    //点击后，跳转到该空间的文件列表
    onItemClick: function (e) {
        var page = this;
        var dataset = e.currentTarget.dataset;

        wx.navigateTo({
            url: '/disk/widget/filelist?ownerId=' + dataset.id + '&nodeId=0&name=' + encodeURIComponent(dataset.name),
        });
    },
    deleteTeam: function (e) {
        var page = this;
        if (e.currentTarget.dataset.deorquit === '1') {
            wx.showModal({
                cancelText: "取消",
                confirmText: "解散",
                title: '提示',
                content: '是否解散此空间？',
                success: function (res) {
                    if (res.confirm) {
                        file.deleteTeam(page.data.id, function () {
                            var offset = 0;   //初始值0
                            var limit = 100;   //加载数量
                            getTeamSpaceList(app.globalData.token, app.globalData.cloudUserId, offset, limit, page);
                        });
                        wx.showToast({
                            title: '解散成功',
                        });
                    }
                }
            })
        } else {
            wx.showModal({
                cancelText: "取消",
                confirmText: "退出",
                title: '提示',
                content: '是否退出此空间？',
                success: function (res) {
                    if (res.confirm) {
                        file.quitTeam(page.data.id, e.currentTarget.dataset.memberquit, function (data) {
                            var offset = 0;   //初始值0
                            var limit = 100;   //加载数量
                            getTeamSpaceList(app.globalData.token, app.globalData.cloudUserId, offset, limit, page);
                            if(data.coide === '200') {
                                wx.showToast({
                                    title: '退出成功',
                                });
                            }
                        });
                        
                    }
                }
            })
        }
        
        

    }
});

function getDepartmentSpaceList(token, cloudUserId, offset, limit, page) {  //部门文件请求数据的方法
    file.listDeptSpace(token, cloudUserId, offset, limit, function (data) {
        page.setData({
            spaceList: translate(data, page),
            type: 1
        });
    })
}

function getTeamSpaceList(token, cloudUserId, offset, limit, page) {    //团队空间请求数据的方法
    file.listTeamSpace(token, cloudUserId, offset, limit, function (data) {
        page.setData({
            spaceList: translate(data, page),
            type: 0,
            showModalStatus: false
        });
    })
}

//将返回结果统一转换成内部的数据结构
function translate(data, self) {
    var spaceList = [];
    for (var i = 0; i < data.memberships.length; i++) {
        var row = data.memberships[i];
        var space = {};

        space.id = row.teamId;
        space.name = row.teamspace.name;
        space.ownerId = row.teamspace.ownedBy;
        if (row.teamspace.ownedByUserName == undefined) {
            space.ownerName = "";
        } else {
            space.ownerName = row.teamspace.ownedByUserName;
        }
        space.memebers = row.teamspace.curNumbers;
        if (app.globalData.cloudUserId == data.memberships[i].teamspace.ownedBy) {
                space.deletespace= '解散空间',
                    space.memberspace = '成员管理'
                space.deorquit= '1'
        } else {
                space.deletespace= '退出空间',
                space.memberspace= '查看成员'
                space.deorquit= '0',
                space.memberquit = data.memberships[i].member.id
        }
        spaceList.push(space);
    }

    return spaceList;
}
