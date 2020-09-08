// disk/find/addpost/addpost.js
var linkClient = require("../../module/link.js");

var postDescription = '';

Page({

    /**
     * 页面的初始数据
     */
    data: {
        checkedList: [],
        isShowAddBtn: false,
        hiddenDEL: true, //隐藏删除按钮
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        //初始化选中文件
        getApp().globalData.checkedList = [];
    },

    /**
     * 生命周期函数--监听页面显示
     */
    onShow: function () {
        var checkedList = getApp().globalData.checkedList;
        var isShowDotted = true;
        if (checkedList == undefined || checkedList.length == 0) {
            isShowDotted = true;
        } else {
            isShowDotted = false;
            this.setData({
                checkedList: checkedList,
                isShowDotted: isShowDotted,
            });
        }
        //选择的文件达到9个隐藏添加按钮
        if (checkedList.length > 8) {
            this.setData({
                isShowAddBtn: true
            });
        }
    },
    onUnload: function () {
        postDescription = '';
    },

    inputChange: function (e) {
        postDescription = e.detail.value;
    },
    //点击确认按钮
    releasePost: function (e) {
        var page = this;
        var checkedList = getApp().globalData.checkedList;
        if (checkedList == undefined || checkedList.length == 0) {
            wx.showToast({
                title: '请选择文件',
                icon: 'none'
            })
            return;
        }
        if (postDescription == undefined || postDescription.trim() == '') {
            wx.showToast({
                title: '分享描述不能为空',
                icon: 'none'
            })
            return;
        }
        var tempCheckedList = [];
        for (var i = 0; i < checkedList.length; i++) {
            let checked = {};
            checked.ownedBy = checkedList[i].ownerId;
            checked.id = checkedList[i].id;
            tempCheckedList.push(checked);
        }
        //给服务器传的参数
        var params = {
            nodeList: tempCheckedList,
            description: postDescription,
            linkName: postDescription
        }
        this.setData({
            description: params.description
        });
        linkClient.createFindLink(params, function (data) {
            wx.showToast({
                title: '发布成功！',
            });

            wx.reLaunch({
                url: "/disk/find/find?from=addpost"//跳转到发现页面的我的发布
            })
        });
    },
    //删除已选中的文件
    deleteChecked: function (e) {
        var page = this;
        var ownerId = e.currentTarget.dataset.ownerId;
        var id = e.currentTarget.dataset.id;
        var checkedList = getApp().globalData.checkedList;
        if (checkedList == undefined || checkedList.length == 0) {
            wx.showToast({
                title: '删除失败',
                icon: 'none'
            })
            return;
        }
        for (var i = 0; i < checkedList.length; i++) {
            if (checkedList[i].ownedBy == ownerId && checkedList[i].id == id) {
                checkedList.splice(i, 1);
                getApp().globalData.checkList = checkedList;

                var isShowDotted = false;
                if (checkedList.length == 0) {
                    isShowDotted = true;
                }
                if (checkedList.length < 9) {
                    this.setData({
                        isShowAddBtn: false
                    });
                }
                page.setData({
                    checkedList: checkedList,
                    isShowDotted: isShowDotted
                });
                return;
            }
        }
    },
    jumpSelectFile: function () {
        wx.navigateTo({
            url: '/disk/widget/selectFolder?jumpType=selectMultipleFile',
        })
    },
})
