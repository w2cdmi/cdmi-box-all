var session = require("../session.js");
var File = require("module/file.js");
var utils = require("module/utils.js");
var link = require("module/link.js");
var Message = require("module/message.js");
var shareUtils = require("common/shareUtils.js");
var Robot = require("module/robot.js");

var startX = 0; //开始坐标
var startY = 0;

var forwardId;  //转发id

var isFirstLoad = true;

Page({
    data: {
        commonsCount: 0,
        previewImageUrls: [],
        isTouchMoveIndex: -1,
        isBackupsing: false    // 备份状态
    },
    onLoad: function (options) {
        isFirstLoad = true;
        //如果是分享链接
        var linkCode = options.linkCode;
        forwardId = options.forwardId;
        var ownerId = options.ownerId;
        var nodeId = options.nodeId;
        if (linkCode != undefined && linkCode != '' && forwardId != undefined && forwardId != "") {
            if (forwardId == getApp().globalData.cloudUserId) {
                getApp().globalData.indexParam = 2;     //2:我的分享
            } else {
                getApp().globalData.indexParam = 3;     //3:他人分享
            }
            var type = options.type;
            if (type != undefined && type == "code") {
                wx.navigateTo({
                    url: "/disk/batchShare/extractionEode/extractionEode?type=" + type + "&forWardId=" + forwardId + "&linkCode=" + linkCode + "&plainAccessCode=" + options.plainAccessCode,
                })
            } else {
                wx.navigateTo({
                    url: '/disk/shares/sharefile?linkCode=' + linkCode + '&forwardId=' + forwardId,
                })
            }
        } else if (ownerId != undefined && ownerId != "" && nodeId != undefined && nodeId != "") {
            if (ownerId == getApp().globalData.cloudUserId) {
                getApp().globalData.indexParam = 2;     //2:我的分享
            } else {
                getApp().globalData.indexParam = 3;     //3:他人分享
            }
            forwardId = ownerId;
            wx.navigateTo({
                url: '/disk/shares/sharefile?ownerId=' + ownerId + '&nodeId=' + nodeId + '&scene=1000',
            })
        }
        //收件箱跳转
        var folderId = options.folderId;
        if (folderId != undefined && folderId != "") {
            var creater = options.creater;
            var folderName = options.folderName;
            wx.navigateTo({
                url: '/disk/inbox/upload/upload?folderId=' + folderId + '&creater=' + creater + '&folderName=' + folderName + '&ownerId=' + ownerId + '&linkCode=' + linkCode,
            })
        }
        // var page = this;

        // //评论消息通知
        // page['theCountOfCommentMessages'] = Message.theCountOfCommentMessages;
    },
    onShow: function () {
        var page = this;
        const backgroundAudioManager = wx.getBackgroundAudioManager();
        var musicName = backgroundAudioManager.title;
        if (musicName != undefined && musicName != "") {
            page.setData({
                isShowMusicPlay: true,
                musicName: backgroundAudioManager.title
            });
        } else {
            page.setData({
                isShowMusicPlay: false,
                musicName: ""
            });
        }
        // if (!isFirstLoad) {
        //     return;
        // }


        session.login({
            enterpriseId: 0
        });

        session.invokeAfterLogin(function () {
            getRecentFileList(page);
            getSharedByMeFileList(page);
            getSharedToMeFileList(page);
            getUnReadMessages(page);
            getRobotStatus(page);
            isFirstLoad = false;
        });

    },

    goToPlayMusic: function () {
        wx.navigateTo({
            url: '/disk/template/shareMusic',
        })
    },

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

    gotoRecentBrowse: function () {
        wx.navigateTo({
            url: '/disk/index/recentBrowse',
        })
    },

    gotoShareToMe: function () {
        wx.navigateTo({
            url: '/disk/index/shareToMe',
        })
    },

    gotoShareByMe: function () {
        wx.navigateTo({
            url: '/disk/index/shareByMe',
        })
    },

    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady: function () {
        if (wx.hideShareMenu) {
            wx.hideShareMenu();
        } else {
            // 如果希望用户在最新版本的客户端上体验您的小程序，可以这样子提示
            wx.showModal({
                title: '提示',
                content: '当前微信版本过低，无法使用该功能，请升级到最新微信版本后重试。',
                showCancel: false
            })
        }
    },
    //滚动到底部触发事件,上拉加载更多
    scrollLower: function () {
        if (this.data.pageCount == this.data.currPageCount) { //如果当前页数已经等于总共分页数，则停止
            this.setData({
                loadMoreIcon: true,  //触发到上拉事件，不隐藏加载图标
            })
            return;
        }
        if (canHasMore === false) {
            return;
        } else {
            canHasMore = false;
        }
        wx.setStorageSync('isShowLoading', false);
        var page = this;
        var tabId = this.data.tabId;
        var datas = page.data.data;
        if (tabId == 2) {
            page.setData({
                ByMeSharePageNum: page.data.ByMeSharePageNum + 10, //每次触发上拉事件，把searchPageNum+1  
                currPageCount: page.data.currPageCount + 1, //当前是分页的第几页
                loadMoreIcon: false  //触发到上拉事件，不隐藏加载图标
            });
            File.listMySharetTo(function (data) {
                if (data.contents.length > 0) {
                    var files = translateShareRecord(data);
                    var newData = new Object();
                    newData.files = datas.files.concat(files)
                    setTimeout(function () {
                        page.setData({
                            loadMoreIcon: true,
                            data: newData,
                        })
                    }, 800);
                } else {
                    setTimeout(function () {
                        page.setData({
                            loadMoreIcon: true,
                        })
                    }, 800);
                }
                return;
            }, page.data.ByMeSharePageNum);
        } else if (tabId == 3) {
            page.setData({
                ToMeSharePageNum: page.data.ToMeSharePageNum + 10, //每次触发上拉事件，把searchPageNum+1  
                loadMoreIcon: false  //触发到上拉事件，把isFromSearch设为为false  
            });
            File.listShareToMe(function (data) {
                if (data.contents.length > 0) {
                    var files = translateShareRecord(data);
                    var newData = new Object();
                    newData.files = datas.files.concat(files)
                    setTimeout(function () {
                        page.setData({
                            data: newData,
                            loadMoreIcon: true,
                        })
                    }, 1200);
                } else {
                    setTimeout(function () {
                        page.setData({
                            loadMoreIcon: true,
                        })
                    }, 1200);
                }
                return;
            }, page.data.ToMeSharePageNum);
        } else {//最近浏览不需要分页
            wx.removeStorageSync('isShowLoading');
            return;
        }
        wx.removeStorageSync('isShowLoading');
    },

    // 跳转到微信备份
    goToBackups: function () {
        wx.navigateTo({
            url: '/disk/backup/index',
        })
    },
    // 跳转到微信备份中页面
    goToBackupsing: function () {
        isFirstLoad = true;
        wx.navigateTo({
            url: '/disk/weChatBackup/weChatBackup',
        })
    },

    //侧滑删除
    deleteItem: function (e) {
        var page = this;
        wx.showModal({
            cancelText: "取消",
            confirmText: "删除",
            title: '提示',
            content: '您确认删除？',
            success: function (res) {
                if (res.confirm) {
                    var fileinfo = e.currentTarget.dataset.fileInfo;
                    var index = e.currentTarget.dataset.index;
                    if (index < 3) {
                        var nodeId = fileinfo.nodeId;
                        var ownerId = fileinfo.ownerId;
                        File.deleteBrowseRecord(ownerId, nodeId, (res) => {
                            getRecentFileList(page);
                        });
                    } else if (index < 6) {
                        var data = {
                            linkCode: fileinfo.linkCode,
                            iNodeId: fileinfo.iNodeId,
                            sharedUserId: fileinfo.sharedUserId,
                            ownerId: fileinfo.ownerId,
                            createdBy: fileinfo.createId
                        };
                        link.deleteBatchLink(data, function () {
                            getSharedToMeFileList(page);
                        });
                    } else {
                        var data = {
                            linkCode: fileinfo.linkCode,
                            iNodeId: fileinfo.iNodeId,
                            sharedUserId: 0,
                            ownerId: fileinfo.ownerId,
                            createdBy: fileinfo.createId
                        };
                        link.deleteBatchLink(data, function () {
                            getSharedByMeFileList(page);
                        });
                    }
                } else if (res.cancel) {
                    return;
                }
            }
        })

    },

    //点击文件或文件夹
    fileItemClick: function (e) {
        var page = this;
        var fileInfo = e.currentTarget.dataset.fileInfo;
        var linkCode = fileInfo.linkCode;

        if (linkCode != undefined && linkCode != "") {
            var isHaveTheFile = false;
            wx.navigateTo({
                url: '/disk/shares/sharefile?linkCode=' + linkCode + "&forwardId=" + getApp().globalData.cloudUserId,
            })
            return;
        }

        var file = {
            id: fileInfo.id,
            ownedBy: fileInfo.ownedBy,
            size: fileInfo.size,
            name: fileInfo.name
        };

        //打开文件
        getApp().globalData.imgUrls = page.data.previewImageUrls;
        File.openFile(file);
    },

    //打开手机右上角的转发事件
    onShareAppMessage: function (e) {
        var fileInfo = e.target.dataset.fileInfo;

        var icon, url;
        if (fileInfo.linkCode != undefined && fileInfo.linkCode != "") {
            if (fileInfo.iNodeId == -1) {
                icon = "images/shares/share-card-batch.png";
            } else {
                if (fileInfo.type < 1) { //文件夹
                    icon = "images/shares/share-card-folder.png";
                } else { //文件
                    icon = fileInfo.shareIcon;
                }
            }
            url = '/disk/shares/sharefile?linkCode=' + fileInfo.linkCode + "&forwardId=" + getApp().globalData.cloudUserId + '&scene=1000';
        } else {
            link.createDefaultLink(fileInfo.ownerId, fileInfo.nodeId, function (data) {
                console.log("创建成功！");
            });

            if (fileInfo.type < 1) { //文件夹
                icon = "images/shares/share-card-folder.png";
            } else { //文件
                icon = fileInfo.shareIcon;
            }
            url = '/disk/shares/sharefile?ownerId=' + fileInfo.ownerId + "&nodeId=" + fileInfo.nodeId + '&scene=1000';
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
    navigateToComms: function () {
        this.setData({
            commonsCount: 0,
            mCount: 0
        });
        wx.navigateTo({
            url: '/disk/comments/indexToComments/viewComments'
        })
    },
    //侧滑-手指触摸动作开始 记录起点X坐标
    touchstart: function (e) {
        var page = this;
        // 起始位置
        startX = e.changedTouches[0].clientX;
        startY = e.changedTouches[0].clientY;

        page.setData({
            isTouchMoveIndex: -1
        });
    },
    //侧滑-滑动事件处理
    touchend: function (e) {
        var index = e.currentTarget.dataset.index;//当前索引
        var item = e.currentTarget.dataset.fileInfo;
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
    }
});

//获取最近浏览的数据
function getRecentFileList(page) {
    File.listLastReadFile(getApp().globalData.token, getApp().globalData.cloudUserId, function (data) {
        var files = shareUtils.convertRecentRecord(data, page);
        var recentBrowseData = [];
        if (files != undefined && files.length >= 3) {
            recentBrowseData = files.slice(0, 3);
        } else {
            recentBrowseData = files;
        }
        page.setData({
            recentBrowseData: recentBrowseData,
            isClearOldDate: false,
            currentPageIcon: true //不隐藏分页图标
        });
    });
}
//获取我的分享的数据
function getSharedByMeFileList(page) {
    File.listMySharetTo(function (data) {
        var files = shareUtils.convertShareRecord(data);
        var shareByMeData = [];
        if (files != undefined && files.length >= 3) {
            shareByMeData = files.slice(0, 3);
        } else {
            shareByMeData = files;
        }
        page.setData({
            shareByMeData: shareByMeData,
            isClearOldDate: false
        });
    }, 0, 3); // 0: offset; 3: limit;
}

//获取他人分享的数据
function getSharedToMeFileList(page) {
    File.listShareToMe(function (data) {
        var files = shareUtils.convertShareRecord(data);
        var shareToMeData = [];
        if (files != undefined && files.length >= 3) {
            shareToMeData = files.slice(0, 3);
        } else {
            shareToMeData = files;
        }
        page.setData({
            shareToMeData: shareToMeData,
            isClearOldDate: false
        });
    }, 0, 3);   // 0: offset; 3: limit;
}

function getUnReadMessages(page) {

    //获取当前用户文件的·评论数量
    var status = 'UNREAD';// UNREAD  表示获取 “未读” 评论数量
    Message.theCountOfCommentMessages(status, (res) => {
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
function getRobotStatus(page){
    Robot.getRobotStatus({}, function (data) {
        if (typeof (data.data) == "object") {
            getApp().globalData.getOpenRobotData = data.data;
            getApp().globalData.isOpenRobot = true;
            page.setData({
                avatarUrl: getApp().globalData.avatarUrl,
                isBackupsing: true
            })
        } else {
            getApp().globalData.isOpenRobot = false;
            page.setData({
                isBackupsing: false
            })
        }
    })
}