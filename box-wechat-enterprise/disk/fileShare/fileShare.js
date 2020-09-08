// disk/fileShare/fileShare.js
var File = require("../module/file.js");
var utils = require("../module/utils.js");
var messageClient = require("../module/message.js");
var link = require("../module/link.js");

// 滑动的起始坐标
var startX = 0;
var startY = 0;

Page({

    /**
     * 页面的初始数据
     */
    data: {
        userAvatarUrl: '',  // 消息的头像
        commonsCount: 0,    // 消息条数
        myShare: [],
        otherShare: [],
        share: 'me',    // 'me'为自己的分享，'other'为他人分享
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
    },

    /**
     * 生命周期函数--监听页面显示
     */
    onShow: function () {
        // 返回页面再次检测点赞或评论消息
        var page = this;
        getUnreadMessages(page);
        getMyShare(page);
        getOtherShare(page);
    },

    navigateToComms: function () {
        this.setData({
            commonsCount: 0,
            mCount: 0
        });
        wx.navigateTo({
            url: '/disk/comments/indexToComments/viewComments'
        })
    },

    //打开手机右上角的转发事件
    onShareAppMessage: function (e) {
        var item = e.target.dataset.item;

        var shareIcon = item.shareIcon;
        if (item.shareIcon === "/disk/images/shares/share-card-undefined.png") {
            shareIcon = "/disk/images/shares/share-card-folder.png"
        }

        return {
            title: item.name,

            path: '/disk/shares/sharefile?enterpriseId=' + getApp().globalData.enterpriseId + "&linkCode=" + item.linkCode + "&forwardId=" + getApp().globalData.cloudUserId + '&scene=1000',
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


        // var icon, url;
        // if (fileInfo.linkCode != undefined && fileInfo.linkCode != "") {
        //     if (fileInfo.iNodeId == -1) {
        //         icon = "images/shares/share-card-batch.png";
        //     } else {
        //         if (fileInfo.type < 1) { //文件夹
        //             icon = "images/shares/share-card-folder.png";
        //         } else { //文件
        //             icon = fileInfo.shareIcon;
        //         }
        //     }
        //     url = '/disk/index?linkCode=' + fileInfo.linkCode + "&forwardId=" + getApp().globalData.cloudUserId + "&enterpriseId=" + getApp().globalData.enterpriseId;
        // } else {
        //     link.createDefaultLink(fileInfo.ownerId, fileInfo.nodeId, function (data) {
        //         console.log("创建成功！");
        //     });

        //     if (fileInfo.type < 1) { //文件夹
        //         icon = "images/shares/share-card-folder.png";
        //     } else { //文件
        //         icon = fileInfo.shareIcon;
        //     }
        //     url = '/disk/index?ownerId=' + fileInfo.ownerId + "&nodeId=" + fileInfo.nodeId + "&enterpriseId=" + getApp().globalData.enterpriseId;
        // }

        // return {
        //     title: fileInfo.name,
        //     path: url,
        //     imageUrl: icon,
        //     success: function (res) {
        //         console.log("转发成功")
        //         // 转发成功
        //     },
        //     fail: function (res) {
        //         console.log("转发失败")
        //         // 转发失败
        //     }, complete: function () {
        //         console.log("转发结束")
        //     }
        // }
    },


    goToShareList: function (e) {
        var share = e.currentTarget.dataset.share;
        var myShare = this.data.myShare;
        var otherShare = this.data.otherShare;

        if ((myShare.length !== 0 && share === 'me') || (otherShare.length !== 0 && share === 'other')) {
            wx.navigateTo({
                url: '/disk/recentlyViewed/recentlyViewed?shareType=' + share,
            });
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
    deleteShortcutFolder: function (e) {
        var page = this;
        var item = e.currentTarget.dataset.item;
        var share = this.data.share;

        var ownerId = item.ownerId;
        var rowId = item.id;
        var param = { ownerId, rowId };

        wx.showModal({
            cancelText: "取消",
            confirmText: "删除",
            title: '提示',
            content: '您确认删除？',
            success: (res) => {
                if (res.confirm) {
                    var data = {
                        linkCode: item.linkCode,
                        iNodeId: item.iNodeId,
                        sharedUserId: item.sharedUserId,
                        ownerId: item.ownerId,
                        createdBy: item.createId
                    };
                    link.deleteBatchLink(data, function () {
                        page.setData({
                            otherShare: [],
                            myShare: [],
                            offset: 0
                        });
                        if (share === 'me') {
                            getMyShare(page);
                        } else {
                            getOtherShare(page);
                        }
                    });
                }
            }
        });
    },

    // 查看未读信息
    goToUnreadMessage: function (e) {
        this.setData({
            commonsCount: 0
        });
        wx.navigateTo({
            url: '/disk/comments/indexToComments/viewComments'
        })

    },

    fileItemClick: function (e) {
        var linkCode = e.currentTarget.dataset.item.linkCode;
        if (linkCode == undefined || linkCode == "") {
            wx.showToast({
                title: '获取链接失败',
                icon: 'none'
            });
        }

        var isHaveTheFile = false;
        wx.navigateTo({
            url: '/disk/shares/sharefile?linkCode=' + linkCode + "&forwardId=" + getApp().globalData.cloudUserId,
        })

    },
})

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

function getOtherShare(page) {
    File.listShareToMe((data) => {
        var files = translateShareRecord(data);
        page.setData({
            otherShare: files
        });
    }, 0, 3);
}

function getMyShare(page) {
    File.listMySharetTo((data) => {
        var files = translateShareRecord(data);
        page.setData({
            myShare: files
        });
    }, 0, 3);
}

function getUnreadMessages(page) {
    //获取当前用户文件的·评论数量
    var status = 'UNREAD';// UNREAD  表示获取 “未读” 评论数量
    messageClient.theCountOfCommentMessages(status, (res) => {
        if (res.count !== undefined && res.count > 0) {
            page.setData({
                mCount: res.count,
                commonsCount: res.count,
                userAvatarUrl: res.users[0].headImageUrl  //默认去最新，第一条
            })
        } else {
            page.setData({
                commonsCount: 0
            })
        }
    });
}
