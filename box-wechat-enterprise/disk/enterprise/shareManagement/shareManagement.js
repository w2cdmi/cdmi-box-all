// disk/enterprise/shareManagement/shareManagement.js
Page({

  /**
   * 页面的初始数据
   */
  data: {
      canUpload: false,
      canShare: false,
      canVerify: false,
  },
  canUpload: function (e) {
    this.setData({
        canUpload: e.detail.value
    })
  },

  canShare: function (e) {
      this.setData({
          canShare: e.detail.value
      })
  },

  canVerify: function (e) {
      this.setData({
          canVerify: e.detail.value
      })
  }
})