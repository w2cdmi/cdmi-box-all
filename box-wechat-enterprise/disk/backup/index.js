// disk/backup/index.js
Page({

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {
      wx.setNavigationBarTitle({ title: getApp().globalData.systemName + "微信备份说明" });
  }
})