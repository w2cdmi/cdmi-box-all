
var fileClient = require("../../../../module/file.js");
var enterpriseClient = require("../../../../module/enterprise.js");
var nameArry = [];
var currpage = [];
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
        nodes: [],
        nameParentArry: []
    },


    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        console.log(nameArry);
        var page = this;
        var name = decodeURIComponent(options.name);
        var obj = {
            parent: options.nodeId,
            name: name
        }

        var objs = {
            currpageID: options.nodeId,
            name: name
        }
        currpage.push(objs);
        nameArry.push(obj)
        page.setData({
            ownerId: options.ownerId,
            parentId: options.nodeId,
            // nodeId: options.nodeId,
            name: nameArry[0].name
        })
    },

    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady: function () {

    },
    onHide: function () {
    },
    onUnload:function(){
        nameArry = [];
        currpage = [];
    },
    /**
     * 生命周期函数--监听页面显示
     */
    onShow: function () {
        var page = this;
        var limit = page.data.limit;
        var offset = page.data.offset;
        var ownerId = page.data.ownerId;
        var parentId = page.data.sonId ? page.data.sonId:page.data.parentId;
        getCoopNodes(ownerId, parentId, limit, offset, page)
        this.setData({ nameParentArry: [] })
    },
    // 目录或文件点击
    coopNodeItemClick: function (e) {
        var page = this;
        var datasetNodeInfo = e.currentTarget.dataset.nodeInfo;
        var limit = page.data.limit;
        var offset = page.data.offset;
        var ownerId = page.data.ownerId;
        var parentId = datasetNodeInfo.id;
        if (datasetNodeInfo.type == 0) {

            //当前页面的页数记录
            var objs = {
                currpageID: parentId,
                name: datasetNodeInfo.name
            }
            currpage.push(objs);

            var obj = {
                son: parentId,
                parent: datasetNodeInfo.parent,
                name: datasetNodeInfo.name
            }
            nameArry.push(obj)
            page.setData({
                sonId: obj.son,
                parentId: nameArry[nameArry.length - 1].parent,
                name: nameArry[nameArry.length - 1].name
            })
            getCoopNodes(ownerId, parentId, limit, offset, page)
        }else{
            getApp().globalData.imgUrls = page.data.previewImageUrls;
            fileClient.openFile(datasetNodeInfo,null)
        }
    },
    // 返回上级目录
    returnPrevFolder: function (e) {
        var page = this;
        var parentId = e.currentTarget.dataset.parent;
        var name = e.currentTarget.dataset.name;;
        var limit = page.data.limit;
        var offset = page.data.offset;
        var ownerId = page.data.ownerId;
        nameArry.pop();
        currpage.pop();
        if (currpage.length == 0) {
            wx.navigateBack({
                delta: 1
            })
        } else {
            page.setData({
                parentId: nameArry[nameArry.length - 1].parent,
                name: nameArry[nameArry.length - 1].name
            })
            getCoopNodes(ownerId, parentId, limit, offset, page)
        }
    }
})

function getCoopNodes(ownerId, parentId, limit, offset, page) {
    enterpriseClient.getCoopNodes(ownerId, parentId, limit, offset, function (data) {
        var fileList = fileClient.convertFileList(data.files);
        var folderList = fileClient.convertFolderList(data.folders);
        var nodes = folderList.concat(fileList);
        page.data.previewImageUrls = fileList.previewImageUrls; 
        page.setData({
            nodes: nodes,
        })
    })
}