// disk/shares/sharefolder.js
/**
 * 此页面只在用户点击“小程序卡片”时（他人分享的目录），才会调用。
 */
var session = require("../../session.js");
var file = require("../module/file.js");
var utils = require("../module/utils.js");
var app = getApp();

var folderName = "";
var ownerId = 0;
var nodeId = 0;
var crumbs = [];
var isInit = false;
var linkCode = "";
var plainAccessCode = "";

Page({

    /**
     * 页面的初始数据
     */
    data: {
        folders: [],
        files: [],
        previewImageUrls: []
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        ownerId = options.ownerId;
        nodeId = options.nodeId;
        linkCode = options.linkCode;
        plainAccessCode = options.plainAccessCode;
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
        crumbs = [];
        if (!this.data.showed) {
            this.setData({
                showed: true
            })
        } else {
            return;
        }
        session.login();

        var page = this;
        //等待登录成功后，执行
        session.invokeAfterLogin(function () {
            // 1. 获取文件信息
            var token = getApp().globalData.token;
            file.getNodeInfo(ownerId, nodeId, function (info) {
                // //设置数据
                info.createdAt = utils.getFormatDate(info.createdAt);
                info.size = utils.formatFileSize(info.size);
                // page.setData({
                //     // crumbs: crumbs,
                //     folders: [info]
                // });
                lsOfFolder(app.globalData.token, info.ownedBy, info.id, page, info.name, info.ownedBy, info.id);

            });


        });
    },

    /**
     * 生命周期函数--监听页面隐藏
     */
    onHide: function () {

    },

    /**
     * 生命周期函数--监听页面卸载
     */
    onUnload: function () {

    },

    /**
     * 页面相关事件处理函数--监听用户下拉动作
     */
    onPullDownRefresh: function () {

    },

    /**
     * 页面上拉触底事件的处理函数
     */
    onReachBottom: function () {
        // /api/v2/folders/{ownerId}/batch/copy
        // srcNodes: [{
        //     ownedBy: '',
        //     id: ''
        // }]
    },
    saveToPerson: function (e) {
        //转到文件列表视图
        let ownerId = e.currentTarget.dataset.ownerId;
        let nodeId = e.currentTarget.dataset.nodeId;
        wx.navigateTo({
            url: '/disk/save/saveToPersion?ownerId=' + ownerId + "&inodeId=" + nodeId,
        })
    },
    onCrumbClick: function (e) { //除第一个面包屑外的面包屑的点击事件
        var page = this;
        var crumb = e.currentTarget.dataset.crumb;

        //删除点击节点之后的数据
        crumbs = crumbs.splice(0, crumb.index + 1);
        page.setData({
            crumbs: crumbs
        });

        page.lsOfFolderForClickCrum(app.globalData.token, crumb.ownerId, crumb.nodeId);
    },

    //点击文件夹
    onFolderItemClick: function (e) {
        var page = this;

        var folder = e.currentTarget.dataset.folderinfo;
        var folderNodeName = folder.name;
        var folderOwnerId = folder.ownedBy;
        var folderNodeId = folder.id;
        if (typeof (folder.isListAcl) == 'undefined' || folder.isListAcl) {
            //刷新文件列表
            lsOfFolder(app.globalData.token, folderOwnerId, folderNodeId, page, folderNodeName, folderOwnerId, folderNodeId);
        } else {
            wx.showToast({
                title: '没有访问权限'
            });
        }
    },

    //点击文件
    onFileItemClick: function (e) {
        var fileInfo = e.currentTarget.dataset.fileinfo;
        getApp().globalData.imgUrls = this.data.previewImageUrls;

        var ext = fileInfo.name.substring(fileInfo.name.lastIndexOf('.') + 1, fileInfo.name.length).toLowerCase();
        var token = "link," + linkCode;
        if (plainAccessCode != undefined && plainAccessCode != ""){
            token = token + "," + plainAccessCode;
        }
        if (ext == 'mp3') {
            file.getFileDownloadUrl(fileInfo.ownedBy, fileInfo.id, (res) => {
                var url = utils.replacePortInDownloadUrl(res.url);
                wx.navigateTo({
                    url: '/disk/template/shareMusic?url=' + encodeURIComponent(url) + '&size=' + fileInfo.size + '&name=' + fileInfo.name
                })
            }, token);
        } else {
            getApp().globalData.imgUrls = this.data.previewImageUrls;
            file.openFile(fileInfo, null, token);
        }
    },
    lsOfFolderForClickCrum: function (token, ownerId, nodeId) {
        var page = this;
        if (linkCode != undefined && linkCode != "") {
            token = "link," + linkCode;
            if (plainAccessCode != undefined && plainAccessCode != "") {
                token = token + "," + plainAccessCode;
            }
        }
        file.lsOfFolder(token, ownerId, nodeId, function (data) {
            var folders = file.convertFolderList(data.folders);
            var files = file.convertFileList(data.files);
            page.data.previewImageUrls = files.previewImageUrls;
            folders.map(item => {
                item.createdAt = utils.getFormatDate(item.createdAt)
            })
            //判断是不是空文件夹
            var isShowBlankPage = false;
            if (folders.length == 0 && files.length == 0) {
                isShowBlankPage = true;
            }

            page.setData({
                folders: folders,
                files: files,
                isShowBlankPage: isShowBlankPage,
                isShowSave: false
            });
        })
    }
});

//查询指定目录下的文件列表(ls命令)
function lsOfFolder(token, ownerId, nodeId, page, folderNodeName, folderOwnerId, folderNodeId) {
    if (linkCode != undefined && linkCode != "") {
        token = "link," + linkCode;
        if (plainAccessCode != undefined && plainAccessCode != "") {
            token = token + "," + plainAccessCode;
        }
    }
    file.lsOfFolder(token, ownerId, nodeId, function (data) {
        var folders = file.convertFolderList(data.folders);
        var files = file.convertFileList(data.files);
        page.data.previewImageUrls = files.previewImageUrls;
        folders.map(item => {
            item.createdAt = utils.getFormatDate(item.createdAt)
        })
        //增加面包屑节点, 刷新界面
        crumbs.push({
            index: crumbs.length,
            name: folderNodeName,
            ownerId: folderOwnerId,
            nodeId: folderNodeId
        });

        //判断是不是空文件夹
        var isShowBlankPage = false;
        if (folders.length == 0 && files.length == 0) {
            isShowBlankPage = true;
        }

        page.setData({
            crumbs: crumbs,
            folders: folders,
            files: files,
            isShowBlankPage: isShowBlankPage,
            isShowSave: false
        });
    })
}
