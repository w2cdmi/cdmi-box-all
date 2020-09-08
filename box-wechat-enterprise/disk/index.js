var session = require("../session.js");
var File = require("module/file.js");
var utils = require("module/utils.js");
var shortcutFolderClient = require("module/shortcutFolder.js");
var Robot = require("module/robot.js");
var enterprise = require("module/enterprise.js");
var link = require("module/link.js");
var Message = require("module/message.js");

var isRefreshPage = true;

Page({
    data: {
        previewImageUrls: [],   //当前页面所有图片预览地址
        folderList: [],         //快捷目录
        isBackupsing: false,    // 备份状态
        isRefreshPage: true,    //是否刷新页面
        isShowMusicPlay: false  //是否显示音乐播放界面
    },
    onLoad: function (options) {
        var page = this;

        // 获取fileList 组件
        this.recentlyViewed = this.selectComponent("#recentlyViewed");
    },
    onShow: function () {

        var page = this;
        
        const backgroundAudioManager = wx.getBackgroundAudioManager();
        var musicName = backgroundAudioManager.title;
        if (musicName != undefined && musicName != ""){
            page.setData({
                isShowMusicPlay: true,
                musicName: backgroundAudioManager.title
            });
        }else{
            page.setData({
                isShowMusicPlay: false,
                musicName: ""
            });
        }
        //是否刷新页面
        // if (!this.data.isRefreshPage) {
        //     return;
        // }

        if (getApp().globalData.token != "" && getApp().globalData.enterpriseId == 0) {
            // session.initGlobalData();
            wx.showModal({
                cancelText: "创建",
                confirmText: "跳转",
                title: "提示",
                content: "您当前使用的是个人账号，您可以选择跳转个人文件宝，也可以选择创建企业",
                success: function (msg) {
                    if (msg.confirm) {
                        wx.navigateToMiniProgram({
                            appId: 'wxfbc7b65a9edb00f9',
                            path: 'disk/index',
                            extraData: {},
                            envVersion: 'release',
                            success(res) {
                                console.log("跳转个人文件宝");
                            },
                            fail(res) {
                                wx.showToast({
                                    title: '跳转失败！',
                                    duration: 1000
                                })
                            }
                        });
                    } else {
                        wx.navigateTo({
                            url: '/disk/enterprise/registered/registered?delta=' + 1,
                        });
                    }
                }
            });
            return;
        }

        session.login();
        session.invokeAfterLogin(function () {
            //设置页面抬头
            var enterpriseName = getApp().globalData.enterpriseName;
            if (typeof (enterpriseName) != 'undefined' && enterpriseName != '') {
                wx.setNavigationBarTitle({ title: getApp().globalData.enterpriseName });
            }

            // 获取快捷文件夹
            var userId = getApp().globalData.userId;
            if (userId) {
                getShortcutFolder(page);
            }

            // 获取最近浏览列表
            getRecentFileList(page);
            // 获取机器人备份
            getRobotStatus(page);
            getUnReadMessages(page);
            page.data.isRefreshPage = false;
        });
    },
    //跳转到个人文件页面
    navigatePersonSpace: function () {
        wx.navigateTo({
            url: "/disk/widget/filelist?ownerId=" + getApp().globalData.cloudUserId + '&nodeId=0&name=' + encodeURIComponent("个人文件"),
        });
    },
    //跳转到部门空间列表页面
    navigateDepartmentSpace: function () {
        wx.navigateTo({
            url: "/disk/cloud/teamspacelist?type=1",
        })
    },
    //跳转到协作空间页面
    navigateTeamSpace: function () {
        wx.navigateTo({
            url: "/disk/cloud/teamspacelist?type=0",
        })
    },
    // 跳转到微信备份
    goToBackups: function () {
        wx.navigateTo({
            url: '/disk/backup/index',
        })
    },
    // 跳转到微信备份中页面
    goToBackupsing: function () {
        wx.navigateTo({
            url: '/disk/weChatBackup/weChatBackup',
        })
        this.data.isRefreshPage = true;
    },
    // 跳转到最近浏览
    goToRecentlyViewed: function () {
        var length = this.data.data.files.length;
        if (length !== 0) {
            wx.navigateTo({
                url: '/disk/recentlyViewed/recentlyViewed',
            })
        }
    },
    //显示菜单
    onShowMenu: function () {
        this.setData({
            isShowMenu: "true"
        })
    },
    //点击菜单上传图片
    onUploadImage: function (e) {
        var page = this;
        var tempFiles = e.detail;

        getApp().globalData.tempFiles = tempFiles;
        wx.navigateTo({
            url: '/disk/widget/selectFolder?jumpType=' + "uploadImage",
        })
    },
    //点击菜单上传视频
    onUploadVideo: function (e) {
        var page = this;
        var tempFile = e.detail;

        getApp().globalData.tempFile = tempFile;
        wx.navigateTo({
            url: '/disk/widget/selectFolder?jumpType=' + "uploadVideo",
        })
    },
    //点击菜单创建文件夹
    showCreateFolder: function () {
        wx.showToast({
            title: '请您进入文件目录创建文件夹！',
            icon: 'none'
        })
    },
    //侧滑-手指触摸动作开始 记录当前是哪个fileList
    getSymbol: function (e) {
        var detail = e.detail.currentTarget;
        var symbol = e.detail.currentTarget.dataset.symbol;

        this.setData({
            checkSymbol: symbol
        })
    },
    // 最近浏览删除
    deleteBrowseRecords: function (e) {
        var page = this;
        var item = e.detail.currentTarget.dataset.item;

        wx.showModal({
            title: '提示',
            content: '您确认删除？',
            success: (res) => {
                if (res.confirm) {
                    var nodeId = item.nodeId;
                    var ownerId = item.ownerId;
                    File.deleteBrowseRecord(ownerId, nodeId, (res) => {
                        getRecentFileList(page);
                        wx.showToast({
                            title: '删除成功',
                        });
                    });
                }
            }
        });
    },
    //快捷文件夹删除
    deleteShortcutFolder: function (e) {
        var page = this;
        var item = e.detail.currentTarget.dataset.item;
        var ownerId = item.ownerId;
        var rowId = item.id;
        var param = { ownerId, rowId };
        wx.showModal({
            title: '提示',
            content: '您确认删除？',
            success: (res) => {
                if (res.confirm) {
                    shortcutFolderClient.deleteShortcutFolder(param, (res) => {
                        var data = res;
                        getShortcutFolder(page);
                        wx.showToast({
                            title: '删除成功',
                        });
                    });
                }
            }
        });
    },
    /**
     * 点击文件或文件夹  预览文件
     * 
     * 文件通过fileList模板返回：dataset.fileinfo   
     * 文件夹返回：dataset.item
     */
    fileItemClick: function (e) {
        // 获取组件中的data-里的数据
        var dataset = e.detail.currentTarget.dataset;
        var item = e.detail.currentTarget.dataset.item;
        var page = this;

        // 预览文件
        var file = {
            id: item.id,
            ownedBy: item.ownedBy,
            size: item.size,
            name: item.name,
        };

        getApp().globalData.imgUrls = page.data.previewImageUrls;
        File.openFile(file);
    },
    //点击文件或文件夹 快捷文件夹
    folderItemClick: function (e) {
        var dataset = e.detail.currentTarget.dataset;
        var item = e.detail.currentTarget.dataset.item;

        var ownerId = item.ownerId;
        var nodeId = item.nodeId;
        wx.navigateTo({
            url: '/disk/widget/filelist?ownerId=' + ownerId + '&nodeId=' + nodeId + "&name=" + encodeURIComponent(dataset.item.nodeName)
        });
    },
    //打开手机右上角的转发事件
    onShareAppMessage: function (e) {

        var fileInfo = e.target.dataset.item;
        //只考虑快捷目录分享
        link.createDefaultLink(fileInfo.ownerId, fileInfo.nodeId, function (data) {
            console.log("创建成功！");
        });
        var icon = "images/shares/share-card-folder.png";
        var url = '/disk/shares/sharefile?ownerId=' + fileInfo.ownerId + "&nodeId=" + fileInfo.nodeId + "&enterpriseId=" + getApp().globalData.enterpriseId + '&scene=1000';

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
    goToPlayMusic: function(){
        wx.navigateTo({
            url: '/disk/template/shareMusic',
        })
    },
    navigateToComms: function () {
        this.setData({
            commonsCount: 0,
            mCount: 0
        });
        wx.navigateTo({
            url: '/disk/comments/indexToComments/viewComments'
        })
    }
});

//获取最近浏览的数据
function getRecentFileList(page) {
    File.listLastReadFile(getApp().globalData.token, getApp().globalData.cloudUserId, function (data) {
        var files = translateRecentRecord(data, page);
        var data = {};

        files.splice(3, files.length - 3);  //只保留三个数据
        data.files = files;
        page.setData({
            data: data,
            isClearOldDate: false
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

// 获取快捷文件夹目录
function getShortcutFolder(page) {
    var userId = getApp().globalData.cloudUserId;
    shortcutFolderClient.getShortcutFolder(userId, (res) => {
        if (res.length === 0) {
            page.setData({
                folderList: []
            });
        }
        for (let i = 0; i < res.length; i++) {
            let data = res[i];
            data.icon = '/disk/images/icon/folder-icon.png';
            data.name = data.nodeName;
            if (data.type === 1) {
                data.ownerName = '个人文件';
            } else {
                data.ownerName = data.ownerName;
            }
            page.setData({
                folderList: res
            });
        }
    })
}
function getRobotStatus(page) {
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
