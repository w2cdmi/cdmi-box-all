// disk/recentlyViewed/recentlyViewed.js

var File = require("../module/file.js");
var utils = require("../module/utils.js");
var link = require("../module/link.js");

var type = '';

// 滑动的起始坐标
var startX = 0;
var startY = 0;

Page({

    /**
     * 页面的初始数据
     */
    data: {
        data: {
            files: []
        },
        startX: 0,
        startY: 0,
        loadMoreIcon: true, // true为隐藏
        isScrollView: 0,
        activeIndex: 0,
        currentPageIcon: 0,
        offset: 0,
        showShareBtn: true,
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        type = options.shareType;
        var page = this;

        if (type === 'me') {
            // 我的分享
            getMyShare(page);
            wx.setNavigationBarTitle({
                title: "我的分享"
            });
        } else if (type === 'other') {
            // 他人分享
            getOtherShare(page);
            wx.setNavigationBarTitle({
                title: "他人分享"
            });
        } else {
            page.setData({
                showShareBtn: false
            })
            wx.setNavigationBarTitle({
                title: "最近浏览"
            });
            getRecentFileList(page);
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
        var item = e.currentTarget.dataset.item;
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
            isTouchMoveIndex: index,
            share: share
        })
    },

    //侧滑删除
    deleteBrowseRecords: function (e) {
        var page = this;
        var item = e.currentTarget.dataset.item;
        // var share = this.data.share;

        var ownerId = item.ownerId;
        var nodeId = item.nodeId;
        var rowId = item.id;
        var param = { ownerId, rowId };

        wx.showModal({
            cancelText: "取消",
            confirmText: "删除",
            title: '提示',
            content: '您确认删除？',
            success: (res) => {
                if (res.confirm) {
                    page.setData({
                        offset: 0,
                    });
                    if (!type) {
                        File.deleteBrowseRecord(ownerId, nodeId, (res) => {
                            getRecentFileList(page);
                        });
                        return ;
                    }

                    var data = {
                        linkCode: item.linkCode,
                        iNodeId: item.iNodeId,
                        sharedUserId: item.sharedUserId,
                        ownerId: item.ownerId,
                        createdBy: item.createId
                    };
                    link.deleteBatchLink(data, function () {
                        if (type === 'me') {
                            getMyShare(page);
                        } else {
                            getOtherShare(page);
                        }
                    });
                }
            }
        });
    },

    //点击文件或文件夹
    fileItemClick: function (e) {
        var page = this;
        let indexParam = getApp().globalData.indexParam;
        var dataset = e.currentTarget.dataset;
        var nodeIds = dataset.fileinfo.iNodeId;
        var ownerId = dataset.fileinfo.ownerId;

        // 分享，跳转分享页面
        if (type) {
            var linkCode = dataset.fileinfo.linkCode;
            if (linkCode == undefined || linkCode == "") {
                wx.showToast({
                    title: '获取链接失败',
                    icon: 'none'
                })
            }
            var isHaveTheFile = false;
            //方法来自  ：saveFile(ownerId, nodeId, linkCode, callback) { 
            // File.saveFile(ownerId, nodeIds, linkCode, function (data) {
            wx.navigateTo({
                url: '/disk/shares/sharefile?linkCode=' + linkCode + "&forwardId=" + getApp().globalData.cloudUserId,
            })
            return;
        }

        var ownerId = dataset.fileinfo.ownerId;
        var nodeId = dataset.fileinfo.nodeId;
        var fileType = dataset.fileinfo.type;

        if (dataset.fileinfo.iNodeId) {
            nodeId = dataset.fileinfo.iNodeId;
        }
        //文件
        if (fileType < 1) {
            wx.navigateTo({
                url: '/disk/widget/filelist?ownerId=' + ownerId + '&nodeId=' + nodeId + "&name=" + encodeURIComponent(dataset.fileinfo.name)
            });
        } else {
            var file = {
                id: dataset.fileinfo.id,
                ownedBy: dataset.fileinfo.ownedBy,
                size: dataset.fileinfo.size,
                name: dataset.fileinfo.name,
            };

            //打开文件
            getApp().globalData.imgUrls = page.data.previewImageUrls;
            File.openFile(file, function (musicState) {
                music.refreshPage(musicState.state, page);
                if (musicState.state == musicService.PLAY_STATE) {
                    music.listenPlayState(page);
                    getApp().globalData.isShowMusicPanel = true;
                    page.setData({
                        isShowMusicPanel: getApp().globalData.isShowMusicPanel
                    });
                }
            });
        }
    },

    scrollLower: function (e) {
        var page = this;
        if (type === 'me') {
            // 我的分享
            getMyShare(page);
        } else if (type === 'other') {
            // 他人分享
            getOtherShare(page);
        }
    },

    //打开手机右上角的转发事件
    onShareAppMessage: function (e) {
        var item = e.target.dataset.item;
        var shareIcon = item.shareIcon;

        if (item.shareIcon === "/disk/images/shares/share-card-undefined.png") {
            shareIcon = "/disk/images/shares/share-card-folder.png"
        }

        var nodeId = item.nodeId
        if (item.iNodeId) {
            nodeId = item.iNodeId;
        }

        return {
            title: item.nodeName,
            path: '/disk/shares/sharefile?enterpriseId=' + getApp().globalData.enterpriseId + "&linkCode=" + item.linkCode + '&scene=1000',
            imageUrl: shareIcon,
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
})

//获取最近浏览的数据
function getRecentFileList(page) {
    // initFileList(page);
    var globalData = getApp().globalData;
    if (globalData.token == '' || globalData.cloudUserId == '') {
        session.login();
        return;
    }
    File.listLastReadFile(globalData.token, globalData.cloudUserId, function (data) {
        var files = translateRecentRecord(data, page);
        var data = {};

        // 无数据则显示空白页
        if (files.length === 0) {
            page.setData({
                isShowBlankPage: true,
            })
        }

        for (var i = 0; i < files.length; i++) {  //文件列表添加左滑默认为0
            files[i].isTouchMove = false
        }
        data.files = files;
        page.setData({
            data: data,
            isClearOldDate: false,
            currentPageIcon: true //不隐藏分页图标
        });
    });
}

//将最近浏览记录转换为内部数据
function translateRecentRecord(data, page) {
    var files = [];
    var previewImageUrlPromises = [];
    var tempFiles = File.convertFileList(data.files);   //获取图片下载地址
    page.data.previewImageUrls = tempFiles.previewImageUrls;    //不分页
    for (var i = 0; i < data.files.length; i++) {
        var row = data.files[i];
        var file = {};
        file.name = row.name;
        file.type = row.type;
        file.nodeId = row.id;
        file.ownerId = row.ownedBy;
        file.ownedBy = row.ownedBy;
        file.id = row.id;
        if (row.thumbnailUrlList.length == 0) {
            file.icon = utils.getImgSrc(row);
            file.shareIcon = utils.getImgSrcOfShareCard(row.name);
        } else {
            file.icon = utils.replacePortInDownloadUrl(row.thumbnailUrlList[0].thumbnailUrl);
            file.shareIcon = utils.replacePortInDownloadUrl(row.thumbnailUrlList[1].thumbnailUrl);
        }
        file.fileSize = utils.formatFileSize(row.size);
        file.modifiedTime = utils.formatNewestTime(row.modifiedAt);
        file.size = row.size;
        file.linkCode = row.linkCode;
        files.push(file);
    }

    return files;
}


//将最近分享记录（我分享的、分享给我的，都是同一格式）转换为内部数据
function translateShareRecord(data) {
    var files = [];
    for (var i = 0; i < data.contents.length; i++) {
        var row = data.contents[i];
        var file = {};
        file.name = row.name;
        if (typeof (row.originalType) != 'undefined') {
            file.type = row.originalType;
        } else {
            file.type = row.type;
        }
        if (typeof (row.originalNodeId) != 'undefined') {
            file.nodeId = row.originalNodeId;
        } else {
            file.nodeId = row.nodeId;
        }
        if (typeof (row.originalOwnerId) != 'undefined') {
            file.ownerId = row.originalOwnerId;
        } else {
            file.ownerId = row.ownerId;//兼容转发记录, 优先使用原始值
        }
        if (typeof (row.thumbnailUrlList) != 'undefined' && row.thumbnailUrlList.length != 0) {
            file.icon = utils.replacePortInDownloadUrl(row.thumbnailUrlList[0].thumbnailUrl);
            file.shareIcon = utils.replacePortInDownloadUrl(row.thumbnailUrlList[1].thumbnailUrl);
        } else {
            file.icon = utils.getImgSrc(row);
            file.shareIcon = utils.getImgSrcOfShareCard(file.name);
        }
        file.modifiedTime = utils.formatNewestTime(row.modifiedAt);
        file.ownerName = row.ownerName;
        file.linkCode = row.linkCode;
        file.shareType = row.shareType;
        file.createId = row.modifiedBy;
        file.desc = row.ownerName;
        file.iNodeId = row.iNodeId;
        file.sharedUserId = row.sharedUserId;

        //数据中包括外链和共享信息，共享设置分享类型为share，不能再次外发
        if (row.iNodeId == -1 && row.shareType == 'link') {
            file.icon = "/disk/images/icon/batch-file-icon.png";
        } else {
            file.shareType = "share";
        }

        files.push(file);
    }

    return files;
}

// 他人的分享
function getOtherShare(page) {
    var offset = page.data.offset;
    var data = page.data.data;

    File.listShareToMe((res) => {
        var files = translateShareRecord(res);

        // 无数据则显示空白页
        if (offset === 0 && files.length === 0) {
            page.setData({
                isShowBlankPage: true,
            })
        }

        data.files = offset === 0 ? files : data.files.concat(files);
        
        page.setData({
            data: data,
            offset: offset + 10
        });

    }, offset);
}

// 自己的分享
function getMyShare(page) {
    var offset = page.data.offset;
    var data = page.data.data;

    File.listMySharetTo((res) => {
        var files = translateShareRecord(res);

        // 无数据则显示空白页
        if (offset === 0 && files.length === 0) {
            page.setData({
                isShowBlankPage: true,
            })
        }

        data.files = offset === 0 ? files : data.files.concat(files);

        page.setData({
            data: data,
            offset: offset + 10
        });
    }, offset);
}
