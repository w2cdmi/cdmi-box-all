// disk/inbox/file/file.js

var Inbox = require("../../module/inbox.js");
var File = require("../../module/file.js");

var crumbs = [];
var fileList = [];
var canHasMore = true;
var LIMIT = 10;

Page({

    /**
     * 页面的初始数据
     */
    data: {
        offset: 0,
        crumbs: [],
        fileList: [],
        previewImageUrls: []
    },
    // 继续收集
    connection: function () {
    },
    // 预览
    preViewImageOrVideo: function (e) {
        var item = e.currentTarget.dataset.item;

        getApp().globalData.imgUrls = this.data.previewImageUrls;
        File.openFile(item);
    },
    setModalStatus: function (e) {
        var page = this;
        var folderItem = e.currentTarget.dataset.folderItem;
        if (folderItem != undefined) {
            page.setData({
                folderItem: folderItem
            });
        }

        var animation = wx.createAnimation({
            duration: 200,
            timingFunction: "linear",
            delay: 0
        })
        this.animation = animation
        animation.translateY(300).step()
        this.setData({
            animationData: animation.export()
        })
        this.setData({
            showModalStatus: true
        });
        setTimeout(function () {
            animation.translateY(0).step()
            this.setData({
                animationData: animation
            })
        }.bind(this), 200)
    },

    cancelModalStatus: function () {
        this.setData({
            showModalStatus: false
        });
    },

    // 保存文件
    saveFileItem: function () {
        var folderItem = this.data.folderItem;
        var ownerId = this.data.ownerId;
        this.setData({
            showModalStatus: false
        });
        // 获取文件的ID，传入存文件的页面
        wx.navigateTo({
            url: '/disk/save/saveToPersion?inodeId=' + folderItem.id + '&ownerId=' + ownerId,
        })
    },
    // 删除文件
    deleteFileItem: function () {
        var folderItem = this.data.folderItem;
        var ownerId = this.data.ownerId;
        var folderId = this.data.folderId;
        var page = this;
        this.setData({
            showModalStatus: false,
        });
        wx.showModal({
            content: '确定删除该文件吗',
            success: (res) => {
                if (res.confirm) {
                    Inbox.deleteFile(ownerId, folderItem.id, (res) => {
                        var data = res;
                        this.setData({
                            offset: 0
                        })
                        fileList = [];
                        getFileList(page, ownerId, folderId);
                    });
                }
            }
        })
    },

    getMoreList: function () {
        // 禁止快速多次下拉刷新
        if (canHasMore) {
            canHasMore = false;
            var page = this;
            var ownerId = this.data.ownerId;
            var folderId = this.data.folderId;
            getFileList(page, ownerId, folderId);
            setTimeout(() => {
                canHasMore = true;
            }, 800);
        }
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        var folderName = options.folderName;
        var ownerId = options.ownerId;
        var folderId = options.folderId;
        var creater = options.creater;
        var linkCode = options.linkCode;
        var offset = options.offset;
        fileList = [];
        this.data.previewImageUrls = [];
        this.setData({
            folderName: folderName,
            ownerId: ownerId,
            folderId: folderId,
            offset: 0,
            fileList: [],
            creater: creater,
            linkCode: linkCode
        });
        var page = this;
        wx.setStorageSync('isPagination', false);
        getFileList(page, ownerId, folderId);
    },

    /**
     * 生命周期函数--监听页面显示
     */
    onShow: function () {
        var linkCode = '';
        Inbox.getInboxLink(this.data.ownerId, this.data.folderId, (link) => {
            if (link.links.length < 1) {  // 无link
                Inbox.setInboxLink(this.data.ownerId, this.data.folderId, (linkInfo) => {
                    linkCode = linkInfo.id;
                    this.setData({
                        linkCode: linkCode
                    });
                });
            } else {
                linkCode = link.links[0].id;
                this.setData({
                    linkCode: linkCode
                });
            }
        });
    },

    /**
    * 生命周期函数--监听页面卸载
    */
    onUnload: function () {
        wx.setStorageSync('isPagination', false);
    },

    /**
 * 用户点击右上角分享
 */
    onShareAppMessage: function (res) {
        var folderId = this.data.folderId;
        var creater = this.data.creater;
        var ownerId = this.data.ownerId;
        var linkCode = this.data.linkCode;
        var folderName = this.data.folderName;
        this.setData({
            showModalStatus: false,
        });
        return {
            title: creater + '的收件箱：' + folderName,
            path: '/disk/inbox/upload/upload?folderId=' + folderId + '&creater=' + creater + '&folderName=' + folderName + '&ownerId=' + ownerId + '&linkCode=' + linkCode + '&scene=1000',
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

function getFileList(page, ownerId, folderId) {
    if (page.data.offset > 0) {
        wx.setStorageSync('isPagination', true);
    }
    var param = { ownerId, folderId, offset: page.data.offset, LIMIT};
    Inbox.getFolderList(param, (res) => {
        var data = res;
        if (data.files.length !== 0) {
            var files = File.convertFileList(data.files);
            page.data.previewImageUrls = page.data.previewImageUrls.concat(files.previewImageUrls);

            if (files.length !== 0) {
                fileList = fileList.length !== 0 ? fileList.concat(files) : files;
                page.setData({
                    offset: page.data.offset + 10,
                    fileList: fileList
                })
            }
        } else if (data.files.length === 0 && page.data.offset === 0) {
            page.setData({
                showBlank: true
            })
        }
    })
}