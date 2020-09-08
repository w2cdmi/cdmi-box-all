var file = require("../module/file.js");
var shortcut = require("../module/shortcutFolder.js")
function showOperation(e) {
    // var top = e.detail.y;
    // var left = e.detail.x;
    // //获取屏幕点击位置
    // var top = e.detail.y;
    // var left = e.detail.x;
    // //获取屏幕宽度
    // var window_width = wx.getSystemInfoSync().windowWidth;
    // var window_height = wx.getSystemInfoSync().windowHeight;

    // //获取显示的操作菜单大小；样式设置
    // var menu_width = 128;
    // // var menu_height = 126;
    // var menu_height = 127;

    // if (left + menu_width > window_width) {
    //     left = left - menu_width;
    // }
    // if (top + menu_height > window_height) {
    //     top = top - menu_height;
    // }

    var nodeInfo = e.currentTarget.dataset.folderinfo;
    if (typeof (nodeInfo) == 'undefined') {
        nodeInfo = e.currentTarget.dataset.fileinfo;
    }
    var node = {};
    node.ownerId = nodeInfo.ownedBy;
    node.nodeId = nodeInfo.id;
    node.name = nodeInfo.name;
    node.type = nodeInfo.type;
    this.setData({
        menu_top: top,
        menu_left: left,
        isShowMenu: true,
        node: node
    });
}

function updateNodeName(e) {
    var page = this;
    var newFolderName = e.currentTarget.dataset.name;
    var fileSuffix = "";
    var nodeType = e.currentTarget.dataset.type;
    if (nodeType == 1) {
        var index = newFolderName.lastIndexOf(".");
        if (index != -1) {
            fileSuffix = newFolderName.substring(index);
            newFolderName = newFolderName.substring(0, index);
        }
    }
    page.setData({
        newFolderName: newFolderName,
        isShowMenu: false,
        baseActionsheet: { show: false }
    });
    page.showUpdateFolderPanel(page, newFolderName, fileSuffix);
}
// 设为快捷目录
function setShortcut(e) {
    var page = this;
    var node = e.currentTarget.dataset.node;
    var belongType = "";
    var cloudUserId = getApp().globalData.cloudUserId;
    if (node.ownerId == cloudUserId) {
        belongType = 1
    } else {
        belongType = 2
    }
    page.setData({
        baseActionsheet: { show: false }
    })
    file.setShortcut(node, belongType, function (data) {
        wx.showToast({
            title: '设置成功！',
            duration: 600
        });
        setTimeout(function(){
            page.lsOfFolderForClickCrum(getApp().globalData.token, page.data.crumbs[page.data.crumbs.length - 1].ownerId, page.data.crumbs[page.data.crumbs.length - 1].nodeId);
        },600);
    })
}
// 取消快捷目录
function cancelShortcut(e) {
    var page = this;
    var ownerId = e.currentTarget.dataset.ownerId;
    var rowId = e.currentTarget.dataset.nodeId;
    var param = { ownerId, rowId }
    page.setData({
        baseActionsheet: { show: false }
    })
    shortcut.deleteShortcutFolder(param, function (data) {
        wx.showToast({
            title: '取消成功！',
        });
        page.lsOfFolderForClickCrum(getApp().globalData.token, page.data.crumbs[page.data.crumbs.length - 1].ownerId, page.data.crumbs[page.data.crumbs.length - 1].nodeId);
    })
}
function deleteNode(e) {
    var name = e.currentTarget.dataset.name;
    var page = this;
    this.setData({
        baseActionsheet: { show: false }
    })

    wx.showModal({
        title: '提示',
        content: "确认删除 " + name + " 吗？",
        success: function (res) {
            if (res.confirm) {
                var ownerId = e.currentTarget.dataset.ownerId;
                var nodeId = e.currentTarget.dataset.nodeId;
                var accountType = getApp().globalData.accountType;
                if (accountType == 0 || accountType > 100) {
                    //删除文件并清楚回收站
                    file.deleteFileAndClearTrash(ownerId, nodeId, function () {
                        wx.showToast({
                            title: '删除成功！',
                        });
                        page.setData({
                            isShowMenu: false
                        });
                        page.lsOfFolderForClickCrum(getApp().globalData.token, page.data.crumbs[page.data.crumbs.length - 1].ownerId, page.data.crumbs[page.data.crumbs.length - 1].nodeId);
                    });
                } else {
                    //删除文件
                    file.deleteNode(ownerId, nodeId, function () {
                        wx.showToast({
                            title: '删除成功！',
                        });
                        page.setData({
                            isShowMenu: false
                        });
                        page.lsOfFolderForClickCrum(getApp().globalData.token, page.data.crumbs[page.data.crumbs.length - 1].ownerId, page.data.crumbs[page.data.crumbs.length - 1].nodeId);
                    });
                }
            }
        }
    })
}

function moveTo(e) {
    var ownerId = e.currentTarget.dataset.ownerId;
    var nodeId = e.currentTarget.dataset.nodeId;
    this.setData({
        baseActionsheet: { show: false },
        isFirstLoad: true   //移动文件成功后，刷新页面
    })
    wx.navigateTo({
        url: '/disk/save/moveToOther?ownerId=' + ownerId + '&inodeId=' + nodeId,
    });
}

module.exports = {
    updateNodeName,
    deleteNode,
    showOperation,
    moveTo,
    setShortcut,
    cancelShortcut
};