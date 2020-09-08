// disk/inbox/upload/upload.js

// var menu = require("../../template/menu.js");
var Inbox = require("../../module/inbox.js");
var Util = require("../../module/utils.js");
var File = require("../../module/file.js");
var Menu = require("../../template/menu.js");

var fileList = [];
var previewList = [];   // 预览列表
var LIMIT = 10;
Page({

    /**
     * 页面的初始数据
     */
    data: {
        senterName: '',
        folderName: '',
        showFileList: false,
        currentFileList: [],
        isShowUplodProgress: true,
        scrollViewHeight: 0,
        fileList: [],
        scrollViewHeight: 0,
        crumbs: [{
            ownerId: '',
            nodeId: '',
            linkCode: ''
        }],
        offset: 0,
        previewList: [],
        uploadingList: [],
        backIndexBtn: false
    },

    // 预览
    preViewImageOrVideo: function (e) {
        var item = e.currentTarget.dataset.item;
        getApp().globalData.imgUrls = this.data.previewImageUrls;

        if (item.createdAt) {
            File.openFile(item);
        } else if (item.icon && item.icon !== '/disk/images/icon/file-mp4.png') {
            wx.previewImage({
                current: item.icon,
                urls: getApp().globalData.imgUrls,
            })
        } else {
            wx.navigateTo({
                url: '/disk/widget/video?path=' + item.tempFilePath,
            })
        }
    },

    chooseImage: function () {
        this.setData({
            currentFileList: [] // 收件箱        
        })

        wx.chooseImage({
            success: (res) => {
                var tempFiles = res.tempFiles;
                for (var i = 0; i < tempFiles.length; i++) {
                    tempFiles[i].fileSize = Util.formatFileSize(tempFiles[i].size);
                    tempFiles[i].icon = tempFiles[i].path
                }
                // 存到收件箱上传页面全局变量
                this.setData({
                    currentFileList: tempFiles
                });
                // 设置上传中文件列表
                this.getUploadingList();

                var parentFileInfo = this.data.crumbs[this.data.crumbs.length - 1];
                var index = 0; //从第一个图片开始上传
                var page = this;
                var uploadingList = this.data.uploadingList;
                if (uploadingList.length === tempFiles.length) {
                    Menu.uploadImage(this.data.uploadingList, index, parentFileInfo, page);
                }
            },
        })
    },

    getMoreList: function () {
        var page = this;
        getUploadList(page);
    },

    chooseVideo: function () {
        this.setData({
            currentFileList: [] // 收件箱        
        })

        wx.chooseVideo({
            success: (res) => {
                var tempFile = res;
                tempFile.fileSize = Util.formatFileSize(tempFile.size);
                // 存到收件箱上传页面全局变量
                this.setData({
                    currentFileList: [tempFile]
                });
                // 设置上传中文件列表
                this.getUploadingList();

                var parentFileInfo = this.data.crumbs[this.data.crumbs.length - 1];
                var page = this;
                var uploadingList = this.data.uploadingList
                if (uploadingList.length === 1) {
                    Menu.uploadVideo(this.data.uploadingList, parentFileInfo, page);
                }
            },
        })

    },

    getUploadingList: function () {
        var currentFileList = this.data.currentFileList;
        var uploadingList = this.data.uploadingList;
        uploadingList = uploadingList.concat(currentFileList);
        this.setData({
            uploadingList: uploadingList
        });
        if (uploadingList.length === 0 && this.data.fileList.length === 0) {
            this.setData({
                showFileList: false,
            })
        } else {
            this.setData({
                showFileList: true,
            })
        }
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

    // 文件预上传
    preUpload: function () {
        Inbox.preUpload(page.data.ownerId, (res) => {
            console.log('res:', res);
            var data = res;
        });
    },

    // 发送文件名
    setFolderItem: function () {
        var folderItem = this.data.folderItem;
    },
    // 删除文件名
    deleteFolderItem: function () {
        var folderItem = this.data.folderItem;

    },
    onCreateFolderCancel: function () {
        this.setData({
            showPopup: false
        })
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        var scene = options.scene;
        if (scene != undefined && scene != ""){
            this.setData({
                backIndexBtn: true
            });
        }

        var folderId = options.folderId;
        var creater = options.creater;
        var folderName = options.folderName;
        var ownerId = options.ownerId;
        var linkCode = options.linkCode;

        fileList = []
        this.setData({
            folderId: folderId,
            senterName: creater,
            folderName: folderName,
            crumbs: [{
                ownerId: ownerId,
                nodeId: folderId,
                linkCode: linkCode
            }],
            fileList: [],
            previewImageUrls: []
        })

        var page = this;

        // 获取用户已经上传的文件
        getUploadList(page);
        // 获取屏幕高度
        wx.getSystemInfo({
            success: (res) => {
                if (res.brand === 'HUAWEI') {
                    this.setData({
                        viewBrand: 'HUAWEI'
                    });
                } else {
                    this.setData({
                        viewBrand: 'other'
                    });
                }
            },
        })
    },

    // 不能删除的函数
    lsOfFolderForClickCrum: function () {
    },

})

function getUploadList(page) {
    var offset = page.data.offset;
    var ownerId = page.data.crumbs[0].ownerId;
    var folderId = page.data.folderId;
    var linkCode = page.data.crumbs[0].linkCode;
    var param = { ownerId, folderId, offset, LIMIT, linkCode };
    // previewList = page.data.previewList;
    var previewImageUrls = page.data.previewImageUrls;
    Inbox.getFolderList(param, (res) => {
        var files = res.files;
        if (files.length !== 0) {
            var files = File.convertFileList(files);
            previewImageUrls = previewImageUrls.concat(files.previewImageUrls);

            var currPreviewList = [];
            for (var i = 0; i < files.length; i++) {
                if (files[i].icon) {
                    currPreviewList[i] = files[i].icon;
                }
            }
            fileList = fileList.length !== 0 ? fileList.concat(files) : files;
            // previewList = previewList.length === 0 ? currPreviewList : previewList.concat(currPreviewList);
            page.setData({
                offset: page.data.offset + 10,
                fileList: fileList,
                // previewList: previewList
                previewImageUrls: previewImageUrls,
                showFileList: true
            });
        } else if (files.length === 0 && page.data.offset === 0) {
            page.setData({
                showFileList: false
            })
        }
    });
}