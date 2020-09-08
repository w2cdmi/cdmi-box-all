// disk/batchShare/showCode/showCode.js
Page({

  /**
   * 页面的初始数据
   */
  data: {
      codeArr:[]
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
      var code = options.code;
      var codeArr=code.split('');
      this.setData({
          code: code,
          codeArr: codeArr
      })
  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {
  
  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {
  
  },
  
  onExtractionCode: function (e) {
      var code = e.currentTarget.dataset.code + "";
      wx.setClipboardData({
          data: code,
          success: function (res) {
              wx.showToast({
                  title: '复制成功',
                  duration: 500
              });
          }
      })
  },

})