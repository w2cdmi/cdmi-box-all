var enterpriseClient = require("../../../../module/enterprise.js");
var fileClient = require("../../../../module/file.js");
var nameArry = [];
Page({

    /**
     * 页面的初始数据
     */
    data: {
        ownerId: "",
        parentId: "",
        nodeId: "",
        name: "",
        limit: 100,
        offset: 0,
        documentNodes: [],
    },


    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        var page = this;
        var name = decodeURIComponent(options.name);
        var obj = {
            parent: options.parent,
            nodeId: options.nodeId,
            name: name
        }
        nameArry.push(obj)
        page.setData({
            ownerId: options.ownerId,
            parentId: options.parent,
            nodeId: options.nodeId,
            name: nameArry[0].name
        })
    },
    onUnload: function () {
        nameArry = []
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
        var page = this;
        var limit = page.data.limit;
        var offset = page.data.offset;
        var ownerId = page.data.ownerId;
        var nodeId = nameArry[nameArry.length - 1].nodeId;
        getDocumentList(ownerId, nodeId, limit, offset, page)
        console.log(nameArry)
    },
    // 目录或文件点击
    nodeItemClick: function (e) {
        var page = this;
        var datasetNodeInfo = e.currentTarget.dataset.nodeInfo;
        var limit = page.data.limit;
        var offset = page.data.offset;
        var ownerId = page.data.ownerId;
        var parentId = datasetNodeInfo.id;
        if (datasetNodeInfo.type == 0) {
            var obj = {
                parent: datasetNodeInfo.parent,
                name: datasetNodeInfo.name,
                nodeId: datasetNodeInfo.id,
            }
            nameArry.push(obj)
            page.setData({
                parentId: nameArry[nameArry.length - 1].parent,
                name: nameArry[nameArry.length - 1].name
            })
            getDocumentList(ownerId, parentId, limit, offset, page)
        } else {
            getApp().globalData.imgUrls = page.data.previewImageUrls;
            fileClient.openFile(datasetNodeInfo, null)
        }
    },
    // 点击返回上级目录
    returnPrevFolder: function (e) {
        console.log(nameArry)
        var page = this;
        var parentId = e.currentTarget.dataset.parent;
        var name = e.currentTarget.dataset.name;
        var limit = page.data.limit;
        var offset = page.data.offset;
        var ownerId = page.data.ownerId;
        nameArry.pop()
        console.log(nameArry)
        if (parentId == 0) {
            wx.navigateBack({
                delta: 1
            })
        } else {
            page.setData({
                parentId: nameArry[nameArry.length - 1].parent,
                name: nameArry[nameArry.length - 1].name
            })
            getDocumentList(ownerId, parentId, limit, offset, page)
        }

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
            url: '../../../template/selectEmploye?type=transferDocument',
        })
    }
})

function getDocumentList(ownerId, parentId, limit, offset, page) {
    enterpriseClient.getCoopNodes(ownerId, parentId, limit, offset, function (data) {
        var documentFileList = fileClient.convertFileList(data.files);
        var documentFolderList = fileClient.convertFolderList(data.folders);
        var documentNodes = documentFolderList.concat(documentFileList);
        page.data.previewImageUrls = documentFileList.previewImageUrls;   
        page.setData({
            documentNodes: documentNodes
        })
    })
}