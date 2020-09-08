var fileService = require("../module/file.js");
var utils = require("../module/utils.js");
var selectFolder = require("../template/selectFolder.js");

var saveFileInfo = {};

Page({

    /**
     * 页面的初始数据
     */
    data: {
        showModal: false,
        newFolderName: "",
        currentOwnerId: 0,
        currentNodeId: 0,
        crumbs: []
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        var page = this;
        //排除文件列表高度（px）
        var layoutHeight = 227;
        selectFolder.pageLayoutInit(layoutHeight, page);
        //初始保存文件信息
        saveFileInfo.saveFileOwnerId = options.ownerId;
        saveFileInfo.saveFileInodeId = options.inodeId;


        page['onClickMenu'] = selectFolder.onClickMenu;
        page['getPersonalFolders'] = selectFolder.getPersonalFolders;
        page['getDepartments'] = selectFolder.getDepartments;
        page['getTeamSpaces'] = selectFolder.getTeamSpaces;
        page['getEnterpriseFolders'] = selectFolder.getEnterpriseFolders;
        page['openFolder'] = selectFolder.openFolder;
        page['openTeamSpace'] = selectFolder.openTeamSpace;
        page['clickCrumb'] = selectFolder.clickCrumb;
        page['onCreateFolder'] = selectFolder.onCreateFolder;
        page['onCreateFolderCancel'] = selectFolder.onCreateFolderCancel;
        page['onCreateFolderConfirm'] = selectFolder.onCreateFolderConfirm;
        page['onConfirmSave'] = selectFolder.onConfirmSave;
        page['inputChange'] = selectFolder.inputChange;
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
        wx.setNavigationBarTitle({ title: "移动到其他目录" });
        var page = this;
        fileService.getNodeInfo(saveFileInfo.saveFileOwnerId, saveFileInfo.saveFileInodeId, function (info) {
            saveFileInfo.saveFileType = info.type;
            //初始化保存文件信息
            selectFolder.saveFileInit(saveFileInfo, "move");

            info.modifiedTime = utils.formatDate(info.modifiedAt);
            info.fileSize = utils.formatFileSize(info.size);
            if (typeof (info.thumbnailUrl) != 'undefined' && info.thumbnailUrl != "" && utils.isShowThumbnail(info.name)) {
                info.iconPath = utils.replacePortInDownloadUrl(info.thumbnailUrl);
            } else {
                info.iconPath = utils.getImgSrc(info);
            }
            //设置数据
            page.setData({
                fileInfo: info
            });
        });

        selectFolder.dirMenuInit(page);
    }
})