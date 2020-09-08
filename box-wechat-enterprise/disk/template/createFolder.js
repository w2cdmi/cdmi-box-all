var File = require("../module/file.js");

var newFolderName = "";
var newFileSuffix = "";

function inputChange(e) {
    newFolderName = e.detail.value;
}

//显示修改对话框
function showUpdateFolderPanel(page, folderName, fileSuffix) {
    newFolderName = folderName;
    newFileSuffix = fileSuffix;
    page.setData({
        showModal: true,
        isUpdate: true
    });
}

function onCreateFolderCancel() {
    newFolderName = "";
    newFileSuffix = "";
    this.setData({
        newFolderName: "",
        showModal: false
    })
}

function onCreateFolderConfirm(e) {
    newFolderName = newFolderName.trim();
    if (newFolderName == ""){
        wx.showToast({
            title: '文件夹名称不能为空',
            icon: 'none'
        })
        return;
    }

    var regEn = /[\/:*"?<>|\\]/im;
    for (var i = 0; i < newFolderName.length; i++){
        var newFolderNameChar = newFolderName.charAt(i);
        if (regEn.test(newFolderNameChar)){
            wx.showToast({
                title: '文件夹名不包含特殊符号',
                icon: 'none'
            });
            return;
        }
    }

    var page = this;
    var isUpdate = e.currentTarget.dataset.isUpdate;
    if (typeof (page.data.crumbs) == 'undefined' || page.data.crumbs == "" || page.data.crumbs.length == 0) {
        newFolderName = "";
        wx.showToast({
            title: '操作失败',
            icon: 'none'
        })
        return;
    }
    var parentFileInfo = page.data.crumbs[page.data.crumbs.length - 1];
    if (typeof (isUpdate) != 'undefined' && isUpdate) {
        var ownerId = e.currentTarget.dataset.ownerId;
        var nodeId = e.currentTarget.dataset.nodeId;
        newFolderName = newFolderName + newFileSuffix;
        File.updateFileName(ownerId, nodeId, newFolderName, function (data) {
            newFolderName = "";
            page.setData({
                newFolderName: "",
                showModal: false
            })
            wx.showToast({
                title: '更名成功！',
            });
            page.lsOfFolderForClickCrum(getApp().globalData.token, parentFileInfo.ownerId, parentFileInfo.nodeId);
        });
    } else {
        File.createFolder(newFolderName, parentFileInfo.ownerId, parentFileInfo.nodeId, function (data) {
            newFolderName = "";
            page.setData({
                newFolderName: "",
                showModal: false
            })
            wx.showToast({
                title: '创建成功！',
            });
            page.lsOfFolderForClickCrum(getApp().globalData.token, parentFileInfo.ownerId, parentFileInfo.nodeId);
        });
    }
}

module.exports = {
    inputChange,
    showUpdateFolderPanel,
    onCreateFolderCancel,
    onCreateFolderConfirm
};