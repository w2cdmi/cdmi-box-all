// disk/find/find.js
Page({

  onShowMenu: function () {
      this.setData({
          isShowMenu: "true"
      })
  },

  onUploadImage: function (e) {
      var page = this;
      var tempFiles = e.detail;

      getApp().globalData.tempFiles = tempFiles;
      wx.navigateTo({
          url: '/disk/widget/selectFolder?jumpType=' + "uploadImage",
      })
  },
  onUploadVideo: function (e) {
      var page = this;
      var tempFile = e.detail;

      getApp().globalData.tempFile = tempFile;
      wx.navigateTo({
          url: '/disk/widget/selectFolder?jumpType=' + "uploadVideo",
      })
  },
  showCreateFolder: function () {
      wx.showToast({
          title: '请您进入文件目录创建文件夹！',
          icon: 'none'
      })
  },

  navigateInbox: function(){
    wx.navigateTo({
      url: "/disk/inbox/inbox",
    });
  },

  goToShareByMePage: function(){
      wx.navigateTo({
          url: '/disk/recentlyViewed/recentlyViewed?shareType=me',
      });
  },

  goToShareToMePage: function () {
      wx.navigateTo({
          url: '/disk/recentlyViewed/recentlyViewed?shareType=other',
      });
  }
})