// disk/inbox/inbox.js
var Inbox = require("../module/inbox.js");
var Menu = require("../template/menu.js");
var Util = require('../module/utils.js');
var File = require('../module/file.js');
var linkClient = require("../module/link.js");

var folderList = [];
var LIMIT = 10;

Page({

    /**
     * 页面的初始数据
     */
    data: {
        folderName: '',
        folderItem: '',
        showBlank: false,   // 显示空白页
        ownerId: '',
        offset: 0,  // 初始获取数据位置
        folderList: [],
        sendDisabled: false,
        viewHeight: 0,
    },

    gotoFileList: function (e) {
        var folderItem = e.currentTarget.dataset.folderItem;
        wx.navigateTo({
            url: '/disk/inbox/inboxFile/inboxFile?folderId=' + folderItem.id + '&creater=' + folderItem.menderName + '&folderName=' + folderItem.name + '&ownerId=' + this.data.ownerId + '&linkCode=' + this.data.linkCode,
        })
    },

    getMoreList: function () {
        var page = this;
        var ownerId = this.data.ownerId;
        getFolderList(page, ownerId);
    },

    // 唤起发送弹出层
    setModalStatus: function (e) {
        var page = this;
        var folderItem = e.currentTarget.dataset.folderItem;
        var linkCode = '';
        Inbox.getInboxLink(this.data.ownerId, folderItem.id, (link) => {
            if (link.links.length < 1) {  // 无link
                Inbox.setInboxLink(this.data.ownerId, folderItem.id, (linkInfo) => {
                    linkCode = linkInfo.id;
                    page.setData({
                        linkCode: linkCode
                    });
                });
            } else {
                linkCode = link.links[0].id;
                page.setData({
                    linkCode: linkCode
                });
            }
        });
        if (folderItem != undefined) {
            page.setData({
                folderItem: folderItem,
            });
        }


        var animation = wx.createAnimation({
            duration: 200,
            timingFunction: "linear",
            delay: 0
        });
        this.animation = animation
        animation.translateY(300).step()
        this.setData({
            animationData: animation.export()
        });
        this.setData({
            showModalStatus: true
        });
        setTimeout(function () {
            animation.translateY(0).step()
            this.setData({
                animationData: animation
            });
        }.bind(this), 200);
    },

    cancelModalStatus: function () {
        this.setData({
            showModalStatus: false
        });
    },

    // 删除收件箱
    deleteFolderItem: function () {
        var page = this;
        var folderItem = this.data.folderItem;
        var folderId = folderItem.id;
        var ownerId = page.data.ownerId;

        this.setData({
            showModalStatus: false
        });
        wx.showModal({
            content: '确定删除该收件箱吗',
            success: (res) => {
                if (res.confirm) {
                    Inbox.deleteFolderName(ownerId, folderId, function (deleteRes) {
                        var data = deleteRes;
                        page.setData({
                            offset: 0
                        })
                        getFolderList(page, ownerId);
                    });
                }
            }
        })
    },

    _onCreateFolderCancel: function () {
        this.createFolder.hideModal();
        this.createFolder.clearInput();
    },

    // 新建收件箱
    _onCreateFolderConfirm: function () {
        var folderName = this.createFolder.getInputValue();
        this.createFolder.clearInput();

        var ownerId = this.data.ownerId;
        if (folderName === '') {
            wx.showToast({
                title: '请输入收件箱名称',
                icon: 'none'
            });
            return;
        }

        if ((/[^\a-\z\A-\Z0-9\u4E00-\u9FA5]/g).test(folderName)) {
            this.createFolder.showErrorModal();
            return;
        }

        var data = {
            name: folderName,
            parent: this.data.folderId
        }
        Inbox.addFolderName(ownerId, data, (res) => {
            this.setData({
                offset: 0,
                showBlank: false,
            })
            folderList = [];
            getFolderList(this, ownerId);
        });
        this.createFolder.hideModal();
    },

    addFolder: function () {
        this.createFolder.showInputModal();
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        wx.getSystemInfo({
            success: (res) => {
                if (res.brand === 'HUAWEI') {
                    this.setData({
                        viewHeight: res.windowHeight * 2 - 100
                    })
                } else {
                    this.setData({
                        viewHeight: res.windowHeight * 2 -100
                    })
                }
            },
        })
    },

    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady: function () {
        // 获得创建文件夹的组件
        this.createFolder = this.selectComponent("#createFolder");
    },

    /**
     * 生命周期函数--监听页面显示
     */
    onShow: function () {
        var ownerId = getApp().globalData.cloudUserId;
        this.setData({
            ownerId: ownerId,
            offset: 0,
        })
        folderList = [];
        var data = {};
        var page = this;
        getFolderList(page, ownerId);
    },

    /**
     * 用户点击右上角分享
     */
    onShareAppMessage: function (res) {
        var folderItem = this.data.folderItem;
        this.setData({
            showModalStatus: false,
        });
        return {
            title: folderItem.menderName + '邀请你上传文件到《' + folderItem.name + '》',
            path: '/disk/inbox/upload/upload?folderId=' + folderItem.id + '&creater=' + folderItem.menderName + '&folderName=' + folderItem.name + '&ownerId=' + this.data.ownerId + '&linkCode=' + this.data.linkCode + '&scene=1000',
            imageUrl: '/disk/images/shares/share-card-folder.png',
            success: function (res) {
                console.log("转发成功");
            },
            fail: function (res) {
                console.log("转发失败");
            }, complete: function () {
                console.log("转发结束");
            }
        }
    }
})

function getFolderList(page, ownerId) {
    Inbox.getInboxFolder(ownerId, function (res) {
        var folderId = res.id
        if (page.data.offset === 0) {
            folderList = [];
        }
        var param = { ownerId, folderId, offset: page.data.offset, LIMIT};
        Inbox.getFolderList(param, function (res) {
            var folders = res.folders;

            for (var i = 0; i < folders.length; i++) {
                folders[i].date = Util.getFormatDate(folders[i].createdAt);
                folderList.push(folders[i]);
            }
            page.setData({
                folderList: folderList,
                folderId: folderId,
                offset: page.data.offset + LIMIT
            })
            if (folderList.length === 0) {
                page.setData({
                    showBlank: true
                })
            }
        });
    });
}