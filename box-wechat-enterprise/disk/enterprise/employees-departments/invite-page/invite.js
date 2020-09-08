var deptId = 0;

Page({

    /**
     * 页面的初始数据
     */
    data: {

    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        deptId = options.deptId;
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

    //打开手机右上角的转发事件
    onShareAppMessage: function (e) {
        var url = 'disk/enterprise/login/login?enterpriseId=' + getApp().globalData.enterpriseId + "&deptId=" + deptId + "&enterpriseName=" + encodeURIComponent(getApp().globalData.enterpriseName);
        var icon = '/disk/images/invite-colleagues.png'
        if (e.from == "button") {
            return {
                title: getApp().globalData.userName + " 邀请您加入 " + getApp().globalData.enterpriseName,
                path: url,
                imageUrl: icon,
                success: function (res) {
                    console.log("转发成功")
                    // 转发成功
                },
                fail: function (res) {
                    console.log("转发失败")
                    // 转发失败
                }, complete: function () {
                    console.log("转发结束")
                }
            }
        }
    }
})