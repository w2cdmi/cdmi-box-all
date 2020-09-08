var fileService = require("../module/file.js");
var utils = require("../module/utils.js");


//排除文件列表高度（px）
var layoutHeight = 227;
//面包屑高度（px）
var crumbsHeight = 31;
//列表高度
var listHeight = 0;
//需要保存的文件信息
var saveFileOwnerId;
var saveFileInodeId;
var saveFileType;   //保存文件类型（文件夹、文件）
var multiple = []; //保存多个文件
var linkCode = "";
//保存面包屑
var crumbs = [];
//记录当前文件夹信息
var currentOwnerId = 0;
var currentNodeId = 0;

var newFolderName = "";
var type = "copy";      //默认为复制

var rootCrumbOwnerId = 0;

//点击菜单按钮（个人文件、协作空间等）
function onClickMenu(e) {
    var page = this;
    //初始化数据
    page.setData({
        listHeight: listHeight + 30,
        crumbsHeight: 1
    });
    currentOwnerId = 0;
    currentNodeId = 0;

    switch (e.currentTarget.dataset.type) {
        case "personalFolders":
            crumbs = [];
            page.getPersonalFolders();
            break;
        case "departments":
            crumbs = [];
            page.getDepartments();
            break;
        case "teamSpaces":
            crumbs = [];
            page.getTeamSpaces();
            break;
        default:
            crumbs = [];
            page.getEnterpriseFolders();
            break;
    }
}
//获取个人文件
function getPersonalFolders() {
    var selectType = "personalFolders";
    var page = this;
    page.setData({
        selectType: selectType
    });
    getFolders("个人文件", getApp().globalData.cloudUserId, 0, page);
}
//获取所有部门空间
function getDepartments() {
    var selectType = "departments";
    crumbs = [];
    this.setData({
        crumbs: crumbs,
        selectType: selectType,
        isTeamSpace: true
    });

    var page = this;
    var offset = 0;   //初始值0
    var limit = 100;   //加载数量
    var token = getApp().globalData.token;
    var cloudUserId = getApp().globalData.cloudUserId;
    fileService.listDeptSpace(token, cloudUserId, offset, limit, function (data) {
        var imageIcon = "/disk/images/depfile.png";
        page.setData({
            teamSpaces: utils.translate(data),
            imageIcon: imageIcon
        });
    })
}
//获取所有协作空间
function getTeamSpaces() {
    var selectType = "teamSpaces";
    crumbs = [];
    this.setData({
        crumbs: crumbs,
        selectType: selectType,
        isTeamSpace: true
    });

    var page = this;
    var offset = 0;   //初始值0
    var limit = 100;   //加载数量
    var token = getApp().globalData.token;
    var cloudUserId = getApp().globalData.cloudUserId;
    fileService.listTeamSpace(token, cloudUserId, offset, limit, function (data) {
        var imageIcon = "/disk/images/teamfile.png";
        page.setData({
            teamSpaces: utils.translate(data),
            imageIcon: imageIcon
        });
    })
}
//获取企业文库下文件夹
function getEnterpriseFolders() {
    var selectType = "enterpriseFolders";
    this.setData({
        crumbs: crumbs,
        selectType: selectType,
        folders: ""
    });

    var page = this;
    var offset = 0;   //初始值0
    var limit = 100;   //加载数量
    fileService.listEnterpriseSpace(getApp().globalData.token, getApp().globalData.cloudUserId, offset, limit, function (data) {
        var team = data.memberships[0].teamspace;
        getFolders(team.name, team.id, 0, page);
    });
}
//点击文件夹
function openFolder(e) {
    var folderName = e.currentTarget.dataset.folderName;
    var ownerId = e.currentTarget.dataset.ownerId;
    var nodeId = e.currentTarget.dataset.nodeId;
    var page = this;
    // initPagingData(page);
    //不为临时账号才改变布局
    if (getApp().globalData.accountType != 0 && getApp().globalData.accountType < 100) {
        page.setData({
            listHeight: listHeight,
            crumbsHeight: 31
        });
    }
    getFolders(folderName, ownerId, nodeId, page);
}
//点击协作空间
function openTeamSpace(e) {
    var teamSpaceName = e.currentTarget.dataset.spaceName;
    var ownerId = e.currentTarget.dataset.ownerId;
    var nodeId = 0;
    var page = this;

    page.setData({
        listHeight: listHeight,
        crumbsHeight: 31
    });
    getFolders(teamSpaceName, ownerId, nodeId, page);
}
//点击面包屑
function clickCrumb(e) {
    var folderName = e.currentTarget.dataset.crumb.name;
    var ownerId = e.currentTarget.dataset.crumb.ownerId;
    var nodeId = e.currentTarget.dataset.crumb.nodeId;
    var page = this;
    // initPagingData(page);
    var index = e.currentTarget.dataset.crumb.index;
    crumbs = crumbs.splice(0, index);

    getFolders(folderName, ownerId, nodeId, page);
}

//获取某一个目录下的所有文件夹和文件
function getFolders(folderName, ownerId, folderId, page) {
    fileService.lsOfFolderNoPaging(getApp().globalData.token, ownerId, folderId, function (data) {
        var folders = fileService.convertFolderList(data.folders, page.data.isSelectMultipleFile);
        var files = fileService.convertFileList(data.files, page.data.isSelectMultipleFile);
        //修改当前文件夹信息
        currentOwnerId = ownerId;
        currentNodeId = folderId;
        //增加面包屑节点, 刷新界面('个人文件'不显示)
        if (folderName == '个人文件' && ownerId == getApp().globalData.cloudUserId && folderId == 0) {
            rootCrumbOwnerId = ownerId;
        } else if (folderName == '企业文库' && ownerId != getApp().globalData.cloudUserId && folderId == 0) {
            rootCrumbOwnerId = ownerId;
        } else {
            crumbs.push({
                index: crumbs.length,
                name: folderName,
                ownerId: ownerId,
                nodeId: folderId
            });
        }
        var res = wx.getSystemInfoSync();
        if (res.platform == 'ios') {
            page.setData({
                crumbs: crumbs
            });
            //刷新文件
            setTimeout(function () {
                page.setData({
                    crumbs: crumbs,
                    currentNodeId: folderId,
                    folders: folders,
                    files: files,
                    isTeamSpace: false
                });
            }, 10)
        } else {
            page.setData({
                crumbs: crumbs,
                currentNodeId: folderId,
                folders: folders,
                files: files,
                isTeamSpace: false
            });
        }
    })
}
//点击创建文件夹，判断是否可以创建文件夹，弹出创建文件夹界面
function onCreateFolder() {
    //判断是否在文件夹目录下
    if (currentOwnerId == 0 && currentNodeId == 0) {
        wx.showToast({
            title: '不在文件夹目录下，不能新建文件夹',
        })
        return;
    }
    this.setData({
        newFolderName: "",
        showModal: true
    })
}
//点击取消创建文件夹
function onCreateFolderCancel() {
    newFolderName = "";
    this.setData({
        newFolderName: "",
        showModal: false
    })
}
//确认创建文件夹
function onCreateFolderConfirm() {
    if (newFolderName == "") {
        wx.showToast({
            title: '文件夹名称不能为空',
            icon: 'none'
        })
        return;
    }
    var page = this;
    fileService.createFolder(newFolderName, currentOwnerId, currentNodeId, function (data) {
        newFolderName = "";
        page.setData({
            newFolderName: "",
            showModal: false
        })
        wx.showToast({
            title: '创建成功！',
        });
        fileService.lsOfFolder(getApp().globalData.token, currentOwnerId, currentNodeId, function (data) {
            var folders = fileService.convertFolderList(data.folders);
            //刷新文件
            page.setData({
                currentNodeId: currentNodeId,
                folders: folders,
                isDataNull: false
            });
        });
    });
}
//点击转存按钮
function onConfirmSave() {
    if (saveFileOwnerId == 0 && saveFileInodeId == 0) {
        wx.showToast({
            title: '转存文件信息获取失败！',
        })
        wx.navigateBack({
            delta: 1,
        })
    }

    if (currentOwnerId == 0 && currentNodeId == 0) {
        wx.showModal({
            title: '提示',
            content: '您没有选择转存文件夹，请选择..',
        })
        return;
    }
    switch (type) {
        case "copy":
            copyFileTo();
            break;
        case "move":
            moveFileTo();
            break;
        case "uploadImage":
            jumpUploadImagePage();
            break;
        case "uploadVideo":
            jumpUploadVideoPage();
            break;
        case "multipleCopy":
            multipleCopy();
            break;
        default:
            console.log("err")
            break;
    }
}
//多个复制
function multipleCopy() {
    fileService.multipleCopy({
        data: multiple,
        destOwnerId: currentOwnerId,
        destParent: currentNodeId,
        linkCode: linkCode,
        success() {
            wx.showModal({
                title: '提示',
                content: '转存成功',
                showCancel: false,
                success: function (res) {
                    if (res.confirm) {
                        wx.navigateBack({
                            delta: 1
                        })
                    }
                }
            })
        }
    })
}
function copyFileTo() {
    if (saveFileType == 1) {
        fileService.copyFileToOther(saveFileOwnerId, saveFileInodeId, currentOwnerId, currentNodeId, function (data) {
            wx.showModal({
                title: '提示',
                content: '转存成功',
                showCancel: false,
                success: function (res) {
                    if (res.confirm) {
                        wx.navigateBack({
                            delta: 1
                        })
                    }
                }
            })
        });
    } else {
        fileService.copyFolderToOther(saveFileOwnerId, saveFileInodeId, currentOwnerId, currentNodeId, function (data) {
            wx.showModal({
                title: '提示',
                content: '转存成功',
                showCancel: false,
                success: function (res) {
                    if (res.confirm) {
                        wx.navigateBack({
                            delta: 1
                        })
                    }
                }
            })
        });
    }
}

function moveFileTo() {
    if (saveFileType == 1) {
        fileService.moveFileToOther(saveFileOwnerId, saveFileInodeId, currentOwnerId, currentNodeId, function (data) {
            wx.showModal({
                title: '提示',
                content: '移动成功',
                showCancel: false,
                success: function (res) {
                    if (res.confirm) {
                        wx.navigateBack({
                            delta: 1
                        })
                    }
                }
            })
        });
    } else {
        fileService.moveFolderToOther(saveFileOwnerId, saveFileInodeId, currentOwnerId, currentNodeId, function (data) {
            wx.showModal({
                title: '提示',
                content: '移动成功',
                showCancel: false,
                success: function (res) {
                    if (res.confirm) {
                        wx.navigateBack({
                            delta: 1
                        })
                    }
                }
            })
        });
    }
}

function jumpUploadImagePage() {
    getApp().globalData.crumbs = updateCrumbs();
    wx.redirectTo({
        url: '/disk/widget/filelist?jumpType=' + type,
    })
}

function jumpUploadVideoPage() {
    getApp().globalData.crumbs = updateCrumbs();
    wx.redirectTo({
        url: '/disk/widget/filelist?jumpType=' + type,
    })
}
//更新
function updateCrumbs() {
    var tempCrumbs = []
    if (getApp().globalData.accountType == 0 || getApp().globalData.accountType > 100) {
        //临时账号修改个人文件索引为0
        if (crumbs.length > 0) {
            crumbs[0].index = 0;
        }
        tempCrumbs = crumbs;
    } else {
        if (crumbs.length > 0) {
            //是否选择为个人文件，个人文件设置首个面包屑为个人文件
            if (crumbs[0].ownerId == getApp().globalData.cloudUserId) {
                tempCrumbs.push({
                    index: 0,
                    name: "个人文件",
                    ownerId: getApp().globalData.cloudUserId,
                    nodeId: 0
                });
                for (var i = 0; i < crumbs.length; i++) {
                    var crumb = crumbs[i];
                    crumb.index = i + 1;
                    tempCrumbs.push(crumb);
                }
            } else {
                tempCrumbs = crumbs;
            }
        } else {
            //选择个人文件跟目录
            if (rootCrumbOwnerId == 0) {
                wx.showToast({
                    title: '请选择一个目录',
                    icon: 'none'
                })
            } else if (rootCrumbOwnerId == getApp().globalData.cloudUserId) {
                tempCrumbs.push({
                    index: 0,
                    name: "个人文件",
                    ownerId: getApp().globalData.cloudUserId,
                    nodeId: 0
                });
            } else {
                tempCrumbs.push({
                    index: 0,
                    name: "企业文库",
                    ownerId: rootCrumbOwnerId,
                    nodeId: 0
                });
            }
        }
    }
    return tempCrumbs;
}

//设置保存文件的信息
function saveFileInit(file, saveType, Code) {
    if (Object.prototype.toString.call(file) === "[object Array]") {
        multiple = file;
        linkCode = Code;
    } else {
        saveFileOwnerId = file.saveFileOwnerId;
        saveFileInodeId = file.saveFileInodeId;
        saveFileType = file.saveFileType;
    }
    if (typeof (saveType) != 'undefined') {
        type = saveType;
    }
}
//页面布局
//layoutHeight: 排除文件列表外的所有高度
function pageLayoutInit(layoutHeight, page) {
    wx.getSystemInfo({
        success: function (res) {
            listHeight = res.windowHeight - layoutHeight - 16;
            page.setData({
                listHeight: listHeight
            });
        },
    })
}
//初始化保存目录菜单
function dirMenuInit(page, useType) {
    var isSelectMultipleFile = false;
    if (typeof (useType) != 'undefined') {
        type = useType;
        if (type == 'selectMultipleFile') {
            isSelectMultipleFile = true;
        }
    }
    page.setData({
        isSelectMultipleFile: isSelectMultipleFile
    });

    //临时账号没有存储空间，需要开通会员
    if (getApp().globalData.accountType == 0 || getApp().globalData.accountType > 100) {
        //开通会员不需要显示目录菜单
        var isShowMenu = false;
        crumbs = [];
        crumbs.push({
            index: 1,
            name: "个人文件",
            ownerId: getApp().globalData.cloudUserId,
            nodeId: 0
        });
        page.setData({
            isShowMenu: isShowMenu,
            listHeight: listHeight + 30
        });
        page.getPersonalFolders("个人文件", 0);
    } else {
        var isShowMenu = true;
        //默认选中个人文件
        var selectType = "personalFolders";
        var isDataNull = true;
        page.setData({
            isShowMenu: isShowMenu,
            selectType: selectType,
            isDataNull: isDataNull,
            listHeight: listHeight + 30,
            crumbsHeight: 1
        });
        crumbs = [];
        getFolders("个人文件", getApp().globalData.cloudUserId, 0, page);
    }
}
//记录创建文件夹输入的名字
function inputChange(e) {
    newFolderName = e.detail.value;
}

//获取选择的文件
function checkbox(e) {
    var page = this;
    var checkedList = getApp().globalData.checkedList;
    var file = e.currentTarget.dataset.item;
    var checkFile = {
        ownerId: file.ownedBy,
        id: file.id,
        name: file.name,
        icon: file.icon
    };
    var ext = file.name.substring(file.name.lastIndexOf('.') + 1, file.name.length).toLowerCase();
    if (checkedList == undefined || checkedList.length == 0) {
        checkedList = [];
        checkedList.push(checkFile);
        getApp().globalData.checkedList = checkedList;
    } else {
        for (var i = 0; i < checkedList.length; i++) {
            if (checkedList[i].ownerId == file.ownedBy && checkedList[i].id == file.id) {
                checkedList.splice(i, 1);
                getApp().globalData.checkedList = checkedList;
                return;
            }
        }
        if (checkedList.length >= 9) {
            wx.showToast({
                title: '文件不能超过9个',
                icon: 'none',
                duration: 500
            });
            var crumb = crumbs[crumbs.length - 1];

            setTimeout(function () {
                getFolders(crumb.folderName, crumb.ownerId, crumb.nodeId, page);
            }, 500);
        } else {
            checkedList.push(checkFile);
            getApp().globalData.checkedList = checkedList;
        }
    }
    
    if (ext == 'jpg' || ext == 'png' || ext == 'gif' || ext == 'bmp' || ext == 'jpeg' || ext == 'jpeg2000'
        || ext == 'tiff' || ext == "mp4" || ext == "ogm" || ext == "ogv" || ext == "webm") {
        return;
    }
    // /disk/images / icon / folder - icon.png
    var iconArr = checkFile.icon.split('icon');
    if (iconArr.length < 3) {
        var iconUrl = iconArr[0] + 'icon/batch-share' + iconArr[1];
    } else {//文件夹类型
        var iconUrl = iconArr[0] + 'icon/batch-share' + iconArr[1] + "icon" + iconArr[2];
    }
    checkFile.icon = iconUrl;
}
//确认选择，返回上一级目录
function sureSelect() {
    wx.navigateBack({
        delta: 1
    })
}

module.exports = {
    onClickMenu,
    getPersonalFolders,
    getDepartments,
    getTeamSpaces,
    getEnterpriseFolders,
    openFolder,
    openTeamSpace,
    clickCrumb,
    onCreateFolder,
    onCreateFolderCancel,
    onCreateFolderConfirm,
    onConfirmSave,
    inputChange,
    saveFileInit,
    pageLayoutInit,
    dirMenuInit,
    checkbox,
    sureSelect,
};