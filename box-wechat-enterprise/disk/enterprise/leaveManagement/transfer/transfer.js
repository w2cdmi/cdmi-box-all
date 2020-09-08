var fileClient = require("../../../module/file.js");
var enterpriseClient = require("../../../module/enterprise.js");
var utils = require("../../../module/utils.js");
var app = getApp();

Page({

    /**
     * 页面的初始数据
     */
    data: {
        scrollHeight: "",
        windowHeight: wx.getSystemInfoSync().windowHeight,
        transferChoose: "",
        isShowCoop: false,
        isShowDocument: false,
        coopList: [],
        employeId: "",
        documentId: 0,
        documentNodes: [],
        check: false, //文档移交选择
        authCheck: false, //权限移交选择
        confim: false, // 确认按钮
        authBatchCheck: false, //权限批量移交
        batchCheck: false, // 文档批量移交
        deleteCheck: false, // 文档删除
        checklists: [],
        nodeCheckList: [],
        checknum: '',
        batchDeleteType: "" //确定是批量移交还是删除
    },

    // 切换移交tab
    clickTabTitle: function (e) {
        var page = this;
        var cloudUserId = page.data.employeId;
        var limit = 100;
        var offset = 0;
        var parentId = page.data.documentId;
        var windowHeight = page.data.windowHeight;
        var scrollHeight = (windowHeight - 40) + "px"
        // 移交类型
        var transferType = e.target.dataset.type;
        if (transferType == "authorityTransfer") {
            getAuthList(cloudUserId, page)
            page.setData({
                transferChoose: "authorityTransfer",
                isShowCoop: false,
                isShowDocument: true,
                checklists: [],
                nodeCheckList: [],
                confim: false,
                scrollHeight: scrollHeight
            })
        } else if (transferType == "documentTransfer") {
            getDocumentList(cloudUserId, parentId, limit, offset, page)
            page.setData({
                transferChoose: "documentTransfer",
                isShowDocument: false,
                isShowCoop: true,
                checklists: [],
                nodeCheckList: [],
                confim: false,
                scrollHeight: scrollHeight
            })
        }
    },
    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        var page = this;
        var windowHeight = page.data.windowHeight;
        var scrollHeight = (windowHeight - 40) + "px"
        page.setData({
            transferChoose: "authorityTransfer",
            isShowCoop: false,
            isShowDocument: true,
            employeId: options.employeId,
            scrollHeight: scrollHeight
        })
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
        wx.removeStorageSync("nodeData");
        wx.removeStorageSync("checkData");
        var page = this;
        var cloudUserId = page.data.employeId;
        var limit = 100;
        var offset = 0;
        var parentId = page.data.documentId
        getAuthList(cloudUserId, page)
        getDocumentList(cloudUserId, parentId, limit, offset, page)

    },
    // 弹出权限批量移交选择
    authBatchTransfer: function () {
        var page = this;
        page.setData({
            authBatchCheck: true,
            authCheck: true,
            checklists: [],
            nodeCheckList: [],
            confim: false,
            batchDeleteType: 2
        })
    },

    // 取消权限批量移交
    cancelAuthBatchTransfer: function () {
        var page = this;
        page.setData({
            authBatchCheck: false,
            authCheck: false,
            checklists: [],
            nodeCheckList: [],
            confim: false
        })
    },

    // 弹出批量选择文档
    documentBatchTransfer: function () {
        var page = this;
        page.setData({
            batchCheck: true,
            deleteCheck: false,
            check: true,
            checklists: [],
            nodeCheckList: [],
            confim: false,
            batchDeleteType: 0
        })
        page.onShow()
    },

    // 关闭批量选择
    cancelDocuBatchTransfer: function () {
        var page = this;
        page.setData({
            batchCheck: false,
            check: false,
            checklists: [],
            nodeCheckList: [],
            confim: false
        })
    },
    // 删除文件
    deleteNodes: function () {
        var page = this;
        page.setData({
            check: true,
            batchCheck: false,
            deleteCheck: true,
            checklists: [],
            nodeCheckList: [],
            confim: false,
            batchDeleteType: 1
        })
        page.onShow()
    },
    // 取消删除
    cancelDeleteNodes: function () {
        var page = this;
        page.setData({
            check: false,
            deleteCheck: false,
            checklists: [],
            nodeCheckList: [],
            confim: false
        })
    },
    // 查看协作空间文档
    toCoopFileDetail: function (e) {
        var page = this;
        var dataset = e.currentTarget.dataset
        wx.navigateTo({
            url: 'seeCoopSpace/seeCoopSpace?ownerId=' + dataset.id + '&nodeId=0&name=' + encodeURIComponent(dataset.name),
        })
        page.setData({
            checklists: [],
            nodeCheckList: [],
            confim: false
        })
    },
    // 查看离职人员个人文档
    toDocumentDetail: function (e) {
        var dataset = e.currentTarget.dataset
        var nodes = dataset.nodes
        var page = this;
        page.setData({
            checklists: [],
            nodeCheckList: [],
            confim: false
        })
        if (dataset.type <= 0) {
            wx.navigateTo({
                url: 'seeDocument/seeDocument?ownerId=' + dataset.ownerId + '&nodeId=' + dataset.id + '&parent=' + dataset.parent + '&name=' + encodeURIComponent(dataset.name),
            })
        } else {
            getApp().globalData.imgUrls = page.data.previewImageUrls;
            fileClient.openFile(nodes, null)
        }
    },

    // 文档批量移交选择
    chooseCheck: function (e) {
        var checks = e.currentTarget.dataset.check;
        var page = this;
        page.data.checknum = '';
        var windowHeight = page.data.windowHeight;
        var item_nodeId = e.target.dataset.id;
        var arr = page.data.checklists;
        var nodeCheckList = page.data.nodeCheckList;
        var has = false;
        arr.map((item, index) => {
            if (item_nodeId === item.id) {
                item.checked = !item.checked
                has = true;
                arr.splice(index, 1)
                nodeCheckList.splice(index, 1)
            }

        })
        if (!has) {
            arr.push({
                checked: false,
                id: checks.id,
                ownedBy: checks.ownedBy,
                createdBy: checks.ownedBy,
                name: checks.name,
                type: checks.type

            });
            nodeCheckList.push(checks)
        }
        var checknum = page.data.checklists.length;
        page.setData({
            checklists: arr,
            nodeCheckList: nodeCheckList,
            checknum: checknum
        })
        if (arr.length > 0) {
            var scrollHeight = (windowHeight - 100) + "px"
            page.setData({
                confim: true,
                scrollHeight: scrollHeight
            })
        };
        if (arr.length === 0) {
            var scrollHeight = (windowHeight - 40) + "px"
            page.setData(
                {
                    confim: false,
                    scrollHeight: scrollHeight
                }
            )
        }
        if (page.data.checknum === 0) {
            var scrollHeight = (windowHeight - 40) + "px"
            page.setData(
                {
                    confim: false,
                    scrollHeight: scrollHeight
                }
            )
        }
    },
    // 权限批量移交选择
    authChooseCheck: function (e) {
        var authchecks = e.currentTarget.dataset.check;
        var page = this;
        page.data.checknum = '';
        var windowHeight = page.data.windowHeight;
        var itemTeamId = e.target.dataset.id;
        var arr = page.data.checklists;
        // var nodeCheckList = page.data.nodeCheckList;
        var has = false;
        arr.map((item, index) => {
            if (itemTeamId === item.cloudUserId) {
                item.checked = !item.checked
                has = true;
                arr.splice(index, 1)
                // nodeCheckList.splice(index, 1)
            }

        })
        if (!has) {
            arr.push({
                checked: false,
                cloudUserId: authchecks.id,
                ownerBy: authchecks.ownerBy,
            });
            // nodeCheckList.push(checks)
        }
        var checknum = page.data.checklists.length;
        page.setData({
            checklists: arr,
            // nodeCheckList: nodeCheckList,
            checknum: checknum
        })
        if (arr.length > 0) {
            var scrollHeight = (windowHeight - 100) + "px"
            page.setData({
                confim: true,
                scrollHeight: scrollHeight
            })
        };
        if (arr.length === 0) {
            var scrollHeight = (windowHeight - 40) + "px"
            page.setData(
                {
                    confim: false,
                    scrollHeight: scrollHeight
                }
            )
        }
        if (page.data.checknum === 0) {
            var scrollHeight = (windowHeight - 40) + "px"
            page.setData(
                {
                    confim: false,
                    scrollHeight: scrollHeight
                }
            )
        }
    },
    // 权限单个移交
    authSingleTransfer: function (e) {
        var dataset = e.currentTarget.dataset;
        wx.setStorageSync("transferCoopspaceId", dataset.item.id);
        wx.navigateTo({
            url: '../../template/selectEmploye?type=transferAuth',
        })
    },
    // 文档单个移交
    documentSingleTransfer: function (e) {
        var datasetItem = e.currentTarget.dataset.item;
        wx.setStorageSync("nodeData", datasetItem)
        var documentObject = {
            nodeId: datasetItem.id,
            ownerId: datasetItem.ownedBy,
            destParent: 0,
            name: datasetItem.name,
            size: datasetItem.size,
            modifiedAt: datasetItem.modifiedAt,
            type: datasetItem.type
        }
        wx.setStorageSync("documentObject", documentObject);
        wx.navigateTo({
            url: '../../template/selectEmploye?type=transferDocument',
        })
    },
    // 权限批量移交和文档批量移交和删除确定按钮
    moreConfirm: function () {
        var page = this;
        var checkedList = page.data.checklists;
        var nodeCheckList = page.data.nodeCheckList;
        var newcheckedList = []
        for (let i = 0; i < checkedList.length; i++) {
            delete checkedList[i].checked
            newcheckedList.push(checkedList[i])
        }
        page.setData({
            batchCheck: false,
            deleteCheck: false,
            authBatchCheck: false,
            check: false,
            authCheck: false,
            checknum: '0',
            confim: false
        });
        wx.setStorageSync("nodeData", nodeCheckList);
        wx.setStorageSync("checkData", newcheckedList);
        wx.setStorageSync("leaveEmployeOwnerId", newcheckedList[0].ownedBy)
        if (page.data.batchDeleteType == 0) {
            wx.navigateTo({
                url: '../../template/selectEmploye?type=transferMoreDocument',
            })
        } else if (page.data.batchDeleteType == 1) {
            deleteNodes(newcheckedList[0].ownedBy, newcheckedList, page)
        } else if (page.data.batchDeleteType == 2) {
            wx.navigateTo({
                url: '../../template/selectEmploye?type=batchTransferAuth',
            })
        }

    }

})
// 加载权限移交页面数据
function getAuthList(cloudUserId, page) {
    enterpriseClient.getAuthTransferList(cloudUserId, function (data) {
        page.setData({
            coopList: translate(data, page),
        });
    })
}

//将返回结果统一转换成内部的数据结构
function translate(data, self) {
    var spaceList = [];
    for (var i = 0; i < data.memberships.length; i++) {
        var row = data.memberships[i];
        var space = {};

        space.id = row.teamId;
        space.name = row.teamspace.name;
        space.ownerId = row.teamspace.ownedBy;
        space.ownerBy = row.teamspace.ownerBy;
        spaceList.push(space);
    }

    return spaceList;
}


// 加载文档移交页面数据
function getDocumentList(cloudUserId, parentId, limit, offset, page) {
    enterpriseClient.getCoopNodes(cloudUserId, parentId, limit, offset, function (data) {
        var documentFileList = fileClient.convertFileList(data.files);
        var documentFolderList = fileClient.convertFolderList(data.folders);
        var documentNodes = documentFolderList.concat(documentFileList)
        page.data.previewImageUrls = documentFileList.previewImageUrls;
        page.setData({
            documentNodes: documentNodes
        })
    })
}

// 删除文件的方法

function deleteNodes(ownerId, nodeData, page) {
    enterpriseClient.deleteNodes(ownerId, nodeData, function (data) {
        page.onShow()
    })
}