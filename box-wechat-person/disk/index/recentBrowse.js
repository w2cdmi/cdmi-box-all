var fileClient = require("../module/file.js");
var shareUtils = require("../common/shareUtils.js");

// 滑动的起始坐标
var startX = 0;
var startY = 0;

Page({

    /**
     * 页面的初始数据
     */
    data: {
        startX: 0,
        startY: 0
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        wx.setNavigationBarTitle({
            title: '文件宝-最近浏览',
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
        getRecentFileList(page);
    },

    fileItemClick: function(e){
        var page = this;
        var fileInfo = e.currentTarget.dataset.fileInfo;

        var file = {
            id: fileInfo.id,
            ownedBy: fileInfo.ownedBy,
            size: fileInfo.size,
            name: fileInfo.name,
        };

        //打开文件
        getApp().globalData.imgUrls = page.data.previewImageUrls;
        fileClient.openFile(file);
    },

    //侧滑-手指触摸动作开始 记录起点X坐标
    touchstart: function (e) {
        // 起始位置
        startX = e.changedTouches[0].clientX;
        startY = e.changedTouches[0].clientY;

        this.setData({
            isTouchMoveIndex: -1,
        })
    },
    //侧滑-滑动事件处理
    touchend: function (e) {
        var index = e.currentTarget.dataset.index;//当前索引
        var item = e.currentTarget.dataset.fileInfo;
        var share = e.currentTarget.dataset.share;

        var touchEndX = e.changedTouches[0].clientX;//滑动终止x坐标
        var touchEndY = e.changedTouches[0].clientY;//滑动终止y坐标
        //获取滑动角度，Math.atan()返回数字的反正切值
        var angle = 360 * Math.atan((touchEndY - startY) / (touchEndX - startX)) / (2 * Math.PI);

        //滑动超过30度角 return
        if (Math.abs(angle) > 30) return;

        // 滑动距离过小即是点击进入文件夹
        if (((touchEndX - startX) > -50) && ((touchEndX - startX) < 50)) {
            this.fileItemClick(e);
            return;
        }

        if (touchEndX > startX) { //向右滑
            index = -1
        }
        this.setData({
            isTouchMoveIndex: index
        })
    },

    //侧滑删除
    deleteBrowseRecords: function (e) {
        var page = this;
        var fileInfo = e.currentTarget.dataset.fileInfo;

        wx.showModal({
            cancelText: "取消",
            confirmText: "删除",
            title: '提示',
            content: '您确认删除？',
            success: (res) => {
                if (res.confirm) {
                    var nodeId = fileInfo.nodeId;
                    var ownerId = fileInfo.ownerId;
                    fileClient.deleteBrowseRecord(ownerId, nodeId, (res) => {
                        getRecentFileList(page);
                    });
                }
            }
        });
    }
})

//获取最近浏览的数据
function getRecentFileList(page) {
    fileClient.listLastReadFile(getApp().globalData.token, getApp().globalData.cloudUserId, function (data) {
        var files = shareUtils.convertRecentRecord(data, page);
        var data = {};
        
        data.files = files;
        page.setData({
            data: data,
            isClearOldDate: false,
            currentPageIcon: true //不隐藏分页图标
        });
    });
}