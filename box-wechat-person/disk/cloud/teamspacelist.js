// disk/cloud/teamspacelist.js
var file = require("../module/file.js");
var utils = require("../module/utils.js");
var app = getApp();

Page({
  /**
   * 页面的初始数据
   */
  data: {
    cells: [],
    scrollTop: 40,
    scrollHeight: 0
  },

  /**
   * 生命周期函数--监听页面加载
   */

  onLoad: function (options) {
    var page = this;
    var offset = 0;   //初始值0
    var limit = 100;   //加载数量
    if (options.type == 0) {
      getTeamSpaceList(app.globalData.token, app.globalData.cloudUserId, offset, limit, page);
    } else if (options.type == 1) {
      getDepartmentSpaceList(app.globalData.token, app.globalData.cloudUserId, offset, limit, page);
    }
    wx.getSystemInfo({
        success: function(res) {
            page.setData({
                scrollHeight: res.windowHeight
            });
        },
    })
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

  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {
    wx.hideShareMenu();  //屏蔽手机右上角的转发功能
  },

  //点击后，跳转到该空间的文件列表
  onItemClick: function (e) {
    var page = this;
    var dataset = e.currentTarget.dataset;

    wx.navigateTo({
      url: '/disk/widget/filelist?ownerId=' + dataset.id + '&nodeId=0&name=' + encodeURIComponent(dataset.name),
    });
  }
});

function getDepartmentSpaceList(token, cloudUserId, offset, limit, page) {  //部门文件请求数据的方法
  file.listDeptSpace(token, cloudUserId, offset, limit, function (data) {
    page.setData({
      spaceList: translate(data),
      'type':1
    });
  })
}

function getTeamSpaceList(token, cloudUserId, offset, limit, page) {    //团队空间请求数据的方法
  file.listTeamSpace(token, cloudUserId, offset, limit, function (data) {
    page.setData({
      spaceList: translate(data),
      'type': 0
    });
  })
}

//将返回结果统一转换成内部的数据结构
function translate(data) {
  var spaceList = [];
  for (var i = 0; i < data.memberships.length; i++) {
    var row = data.memberships[i];
    var space = {};

    space.id = row.teamId;
    space.name = row.teamspace.name;
    space.ownerId = row.teamspace.ownedBy;
    space.ownerName = row.teamspace.ownedByUserName;
    space.memebers = row.teamspace.curNumbers;

    spaceList.push(space);
  }

  return spaceList;
}