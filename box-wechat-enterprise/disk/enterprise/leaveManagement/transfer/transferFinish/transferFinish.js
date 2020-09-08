Page({

    /**
     * 页面的初始数据
     */
    data: {
        scrollHeight: "",
        windowHeight: wx.getSystemInfoSync().windowHeight,
        finishData: [],
        managerAlias:"",
        leaveEmployeAlias:"",
        transferBoundType:""
    },


    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        var page = this;
        var windowHeight = page.data.windowHeight;
        var scrollHeight = windowHeight - 145
        page.setData({
            scrollHeight: scrollHeight + "px",
            managerAlias: options.managerAlias,
            transferBoundType: options.transferBoundType
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
        var finishData =[];
        var nodeData = wx.getStorageSync("nodeData");
        var leaveEmployeAlias = wx.getStorageSync("leaveEmployeAlias");
        if (nodeData.constructor == Object){
            finishData.push(nodeData);
        } else if (nodeData.constructor == Array){
            finishData = nodeData
        }
        
        page.setData({
            finishData: finishData,
            leaveEmployeAlias: leaveEmployeAlias
        })
    },
    continueTransfer: function () {
        wx.removeStorageSync("nodeData");
        if (this.data.transferBoundType == 0){
            wx.navigateBack({
                delta: 1
            })
        } else if (this.data.transferBoundType == 1){
            wx.navigateBack({
                delta: 2
            })
        }
        
    },
    returnLeaveManage: function () {
        wx.removeStorageSync("nodeData");
        wx.removeStorageSync("leaveEmployeAlias")
        wx.redirectTo({
            url: '../../leaveManagement',
        })
    }
})