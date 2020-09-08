var selectFolder = require("../template/selectFolder.js");

Page({

    /**
     * 页面的初始数据
     */
    data: {
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        var page = this;

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
        page['checkbox'] = selectFolder.checkbox;
        page['sureSelect'] = selectFolder.sureSelect;
        page['scrollLower'] = selectFolder.scrollLower;
        page['initPagingData'] = selectFolder.initPagingData;

      
        //排除文件列表高度（px）
        var layoutHeight = 60;
        selectFolder.pageLayoutInit(150, page);

        var jumpType = options.jumpType;
        if (jumpType != undefined && jumpType != "") {
            selectFolder.dirMenuInit(page, jumpType);
        }else{
            selectFolder.dirMenuInit(page);
        }
    }
})