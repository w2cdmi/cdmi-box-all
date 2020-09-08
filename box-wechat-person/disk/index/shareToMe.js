var fileClient = require("../module/file.js");
var shareUtils = require("../common/shareUtils.js");
var linkClient = require("../module/link.js");

var isFirstLoad = true;
// 滑动的起始坐标
var startX = 0;
var startY = 0;

Page({

    /**
     * 页面的初始数据
     */
    data: {
	    startX: 0,
        startY: 0,
        offset: 0,
        totalCount: 0
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        isFirstLoad = true;
        wx.setNavigationBarTitle({
            title: '文件宝-他人分享',
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
        if(!isFirstLoad){
            return;
        }

        var page = this;
        getSharedToMeFileList(page);
    },

    fileItemClick: function (e) {
        var page = this;
        var dataset = e.currentTarget.dataset;

        var linkCode = dataset.fileInfo.linkCode;
        if (linkCode == undefined || linkCode == "") {
            wx.showToast({
                title: '获取链接失败',
                icon: 'none'
            })
        }
        wx.navigateTo({
            url: '/disk/shares/sharefile?linkCode=' + linkCode + "&forwardId=" + getApp().globalData.cloudUserId,
        })
    },

    onShareAppMessage: function (e) {
        var fileInfo = e.target.dataset.fileInfo;

        var icon, url;
        if (fileInfo.linkCode != undefined && fileInfo.linkCode != "") {
            if (fileInfo.iNodeId == -1) {
                icon = "/disk/images/shares/share-card-batch.png";
            } else {
                if (fileInfo.type < 1) { //文件夹
                    icon = "/disk/images/shares/share-card-folder.png";
                } else { //文件
                    icon = fileInfo.shareIcon;
                }
            }
            url = '/disk/shares/sharefile?linkCode=' + fileInfo.linkCode + "&forwardId=" + getApp().globalData.cloudUserId;
        } else {
            wx.showToast({
                title: '分享异常！',
            })
        }

        return {
            title: fileInfo.name,
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
                    var fileinfo = e.currentTarget.dataset.fileInfo;
                    var index = e.currentTarget.dataset.index;

                    var data = {
                        linkCode: fileinfo.linkCode,
                        iNodeId: fileinfo.iNodeId,
                        sharedUserId: fileinfo.sharedUserId,
                        ownerId: fileinfo.ownerId,
                        createdBy: fileinfo.createId
                    };

                    linkClient.deleteBatchLink(data, function () {
                        page.data.data.files.splice(index, 1);
                        page.data.offset = page.data.offset - 1;
                        //刷新页面
                        page.setData({
                            data: page.data.data
                        });
                    });
                }
            }
        });
    },
	scrollLower: function (e) {
        var page =  this;
        if (!page.data.currentPageIcon){
            return;
        }
        getSharedToMeFileList(page);
    },
})

//获取他人分享的数据
function getSharedToMeFileList(page) {
if (page.data.offset != 0 && (page.data.offset + 1) > page.data.totalCount){
        return;
    }
    if (page.data.offset == 0 || (page.data.offset + 1) < page.data.totalCount){
        page.setData({
            currentPageIcon: false, //不隐藏分页图标
            loadMoreIcon: false
        });
    }
    wx.setStorageSync("isShowLoading", false);
    fileClient.listShareToMe(function (data) {
        var totalCount = data.totalCount;
        var files = shareUtils.convertShareRecord(data);
	var data = {};
        if(page.data.data != undefined && page.data.data.files != undefined && page.data.data.files.length > 0){
            files = page.data.data.files.concat(files);
        }
        data.files = files;
        page.setData({
            data: data,
            isClearOldDate: false,
		    totalCount: totalCount,
            offset: page.data.offset + 10,
            currentPageIcon: true, //不隐藏分页图标
            loadMoreIcon: true,
            activeIndex: 3
        });

        isFirstLoad = false;
    }, page.data.offset);
}