var utils = require("../module/utils.js");
var File = require("../module/file.js");

var isShowMenuPanel = true;
var menuHeight = 275; //排除音乐按钮高度
var location = '';    //菜单所在页面

function initMenu(page, locationParm) {
    if (locationParm != undefined && locationParm != "") {
        location = locationParm;
    } else {
        location = "";
    }

    var musicList = getApp().globalData.musicList;
    var isShowMusicButton = false;
    if (musicList != '' && musicList.length > 0) {
        isShowMusicButton = true;
        menuHeight = 325;
    } else {
        menuHeight = 275;
    }
    //检查机器人状态
    var isOpenRobot = getApp().globalData.isOpenRobot;
    var userIcon = "../images/menu/backup.png";
    if (isOpenRobot) {
        userIcon = getApp().globalData.avatarUrl;
    }
    //获取当前页面面包屑
    var crumbs = page.data.crumbs;
    var isShowUpButton = true;
    if (typeof (crumbs) == 'undefined' || crumbs.length == 0) {
        isShowUpButton = false;
        menuHeight = menuHeight - 50;
    }

    //显示聊天操作菜单;  注意:在聊天界面添加菜单时候，添加该参数，其他页面调用不加该参数
    var isShowChatButton = getApp().globalData.isShowChatButton;
    if (isShowChatButton) {
        menuHeight = menuHeight + 100;
    }
    var isTeamspace = false;
    if (location == 'teamspace') {
        isTeamspace = true;
        menuHeight = menuHeight + 50;
    }

    page.setData({
        isShowMusicButton: isShowMusicButton,
        isOpenRobot: isOpenRobot,
        userIcon: userIcon,
        isShowUpButton: isShowUpButton,
        isShowChatButton: isShowChatButton,
        isTeamspace: isTeamspace
    });
}

function switchMenuPanel() {
    var page = this;
    initMenu(page, location);

    if (isShowMenuPanel) {
        menuPanelHideAnimation(page);
    } else {
        menuPanelShowAnimation(page)
    }
}

function hideMenuPanel() {
    var page = this;
    menuPanelHideAnimation(page);
}

function menuPanelShowAnimation(page) {
    isShowMenuPanel = true;
    page.setData({
        isShowMenuPanel: isShowMenuPanel,
    });
}

function menuPanelHideAnimation(page) {
    isShowMenuPanel = false;
    page.setData({
        isShowMenuPanel: isShowMenuPanel,
    });
}

function chooseUploadImage() {
    var page = this;
    menuPanelHideAnimation(page);

    wx.chooseImage({
        success: function (res) {
            var tempFiles = res.tempFiles;


            if (typeof (tempFiles) == 'undefined' || tempFiles.length == 0) {
                return;
            }

            //获取上传目录
            var crumbs = page.data.crumbs;
            if (typeof (crumbs) == 'undefined' || crumbs == "" || crumbs.length == 0) {
                getApp().globalData.tempFiles = tempFiles;
                wx.navigateTo({
                    url: '/disk/widget/selectFolder?jumpType=' + "uploadImage",
                })
                return;
            } else {
                var parentFileInfo = crumbs[crumbs.length - 1];

                var index = 0; //从第一个图片开始上传
                uploadImage(tempFiles, index, parentFileInfo, page);
            }
        }
    })
}

function chooseUploadVedio() {
    var page = this;
    isShowMenuPanel = false;
    menuPanelHideAnimation(page);

    wx.chooseVideo({
        sourceType: ['album', 'camera'],
        compressed: true,
        maxDuration: 60,
        camera: 'back',
        success: function (res) {
            var tempFile = res;

            var crumbs = page.data.crumbs;
            if (typeof (crumbs) == 'undefined' || crumbs == "" || crumbs.length == 0) {
                getApp().globalData.tempFile = tempFile;
                wx.navigateTo({
                    url: '/disk/widget/selectFolder?jumpType=' + "uploadVideo",
                })
                return;
            } else {
                var parentFileInfo = crumbs[crumbs.length - 1];
                uploadVideo(tempFile, parentFileInfo, page);
            }
        }
    })
}

function switchMusicPanel() {
    var page = this;
    isShowMenuPanel = true;
    menuPanelHideAnimation(page);

    if (getApp().globalData.isShowMusicPanel) {
        getApp().globalData.isShowMusicPanel = false;
    } else {
        getApp().globalData.isShowMusicPanel = true;
    }
    this.setData({
        isShowMusicPanel: getApp().globalData.isShowMusicPanel
    });
}

function showCreateFolderPanel() {
    var page = this;
    menuPanelHideAnimation(page);
    page.setData({
        showModal: true,
        isUpdate: false
    });
}

function backupIntroduction() {
    var page = this;
    isShowMenuPanel = true;
    menuPanelHideAnimation(page);
    wx.navigateTo({
        url: '/disk/backup/index',
    })
}
function newteamspace() {
    var page = this;
    wx.navigateTo({
        url: '/disk/cloud/teamspacelist',
    })
}


function uploadImage(tempFiles, index, parentFileInfo, page) {
    if (index > tempFiles.length - 1) {
        return;
    }
    var ext = tempFiles[index].path.substring(tempFiles[index].path.lastIndexOf('.'), tempFiles[index].path.length).toLowerCase();
    var fileName = (new Date()).getTime() + ext;
    // 存到收件箱上传页面全局变量，添加名字 判断currentFileList

    File.getPreUploadFileUrl(parentFileInfo, fileName, tempFiles[index].size, function (data) {
        var inodeId = data.fileId;
        var uploadUrl = data.uploadUrl + "?objectLength=" + tempFiles[index].size;

        var url = utils.replacePortInDownloadUrl(uploadUrl);
        const uploadTask = wx.uploadFile({
            url: url, //仅为示例，非真实的接口地址
            filePath: tempFiles[index].path,
            name: fileName,
            success: function (res) {
                page.setData({
                    percent: 0
                });
                console.log("上传成功");
                page.lsOfFolderForClickCrum(getApp().globalData.token, parentFileInfo.ownerId, parentFileInfo.nodeId);
            },
            fail: function () {
                page.setData({
                    percent: 0
                });
                console.log("上传失败");
            },
            complete: function (res) {
                console.log('上传完成：', res);
            }
        });
        uploadTask.onProgressUpdate((res) => {
            var fileList = '';
            if (page.data.uploadingList) {
                var uploadingList = page.data.uploadingList;
                uploadingList[0].name = fileName;
                page.setData({
                    uploadingList: uploadingList,  // 修改收件箱的文件名
                });
            }

            page.setData({
                isShowUplodProgress: true,
                currentIndex: 0, // 收件箱当前上传文件
                percent: res.progress
            });
            if (res.progress == 100) {
                index += 1;
                if (index >= tempFiles.length) {
                    page.setData({
                        isShowUplodProgress: false,
                    });
                }

                if (page.data.uploadingList && page.data.uploadingList.length > 0) {
                    var uploadingList = page.data.uploadingList;
                    var fileList = page.data.fileList;
                    var previewImageUrls = page.data.previewImageUrls;
                    var currData = uploadingList.shift();
                    fileList.unshift(currData);
                    previewImageUrls.unshift(currData.icon);
                    page.setData({
                        uploadingList: uploadingList,
                        fileList: fileList,
                        offset: page.data.offset + 1,
                        previewImageUrls: previewImageUrls
                    });
                    if (uploadingList.length > 0) {
                        if (uploadingList[0].path) {
                            uploadImage(uploadingList, 0, parentFileInfo, page); return;
                        } else {
                            uploadVideo(uploadingList[0], parentFileInfo, page); return;
                        }
                    }
                }
                uploadImage(tempFiles, index, parentFileInfo, page);
            }
        })
    })
}

function uploadVideo(tempFile, parentFileInfo, page) {

    if (page.data.uploadingList != undefined) {
        // 存到收件箱上传页面全局变量，添加名字
        if (page.data.uploadingList[0] && !page.data.uploadingList[0].tempFilePath) {
            return;
        }
        if (page.data.uploadingList[0] && page.data.uploadingList[0].tempFilePath) {
            var ext = page.data.uploadingList[0].tempFilePath.substring(page.data.uploadingList[0].tempFilePath.lastIndexOf('.'), page.data.uploadingList[0].tempFilePath.length).toLowerCase();
            var vedioName = (new Date()).getTime() + ext;
            page.data.uploadingList[0].name = vedioName;
            tempFile = page.data.uploadingList[0]
        }
    } else {
        var ext = tempFile.tempFilePath.substring(tempFile.tempFilePath.lastIndexOf('.'), tempFile.tempFilePath.length).toLowerCase();
        var vedioName = (new Date()).getTime() + ext;
    }

    File.getPreUploadFileUrl(parentFileInfo, vedioName, tempFile.size, function (data) {
        var inodeId = data.fileId;
        var uploadUrl = data.uploadUrl + "?objectLength=" + tempFile.size;

        var url = utils.replacePortInDownloadUrl(uploadUrl);
        const uploadTask = wx.uploadFile({
            url: url, //仅为示例，非真实的接口地址
            filePath: tempFile.tempFilePath,
            name: vedioName,
            success: function (res) {
                page.setData({
                    percent: 0
                });
                page.lsOfFolderForClickCrum(getApp().globalData.token, parentFileInfo.ownerId, parentFileInfo.nodeId, page);
            },
            fail: function () {
                page.setData({
                    percent: 0
                });
                console.log("上传失败");
            }
        });
        uploadTask.onProgressUpdate((res) => {
            if (page.data.uploadingList) {
                var uploadingList = page.data.uploadingList;
                uploadingList[0].name = vedioName;
                page.setData({
                    uploadingList: uploadingList,  // 修改收件箱的文件名
                });
            }

            page.setData({
                isShowUplodProgress: true,
                currentIndex: 0, // 收件箱当前上传文件
                percent: res.progress
            });

            if (res.progress == 100) {
                if (page.data.uploadingList != undefined && page.data.uploadingList.length > 0) {
                    var uploadingList = page.data.uploadingList;
                    var fileList = page.data.fileList;
                    var currData = uploadingList.shift();
                    fileList.unshift(currData);
                    page.setData({
                        uploadingList: uploadingList,
                        fileList: fileList,
                        offset: page.data.offset + 1
                    });
                    if (uploadingList.length > 0) {
                        if (uploadingList[0].path) {
                            uploadImage(uploadingList, 0, parentFileInfo, page); return;
                        } else {
                            uploadVideo(uploadingList[0], parentFileInfo, page); return;
                        }
                    }
                }
                page.setData({
                    isShowUplodProgress: false,
                });
            }
        })
    })
}

module.exports = {
    isShowMenuPanel,
    initMenu,
    switchMenuPanel,
    hideMenuPanel,
    menuPanelShowAnimation,
    menuPanelHideAnimation,
    chooseUploadImage,
    uploadImage,
    chooseUploadVedio,
    uploadVideo,
    switchMusicPanel,
    showCreateFolderPanel,
    backupIntroduction,
    newteamspace
};