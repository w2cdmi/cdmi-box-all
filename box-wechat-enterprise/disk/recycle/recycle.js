var session = require("../../session.js");
var File = require("../module/file.js");
var utils = require("../module/utils.js");
var config = require("../config.js");
var menu = require("../template/menu.js");

var app = getApp();
var sliderWidth = 96; // 需要设置slider的宽度，用于计算中间位置

var isShowDeleteModel = false;

Page({
    data: {
        files: [],
        folders: [],
        isClearOldDate: true,    //清除老数据
        showModalStatus: false,
        check: false,
        confim: false,
        deletearr: [],
        checklists: [],
        checknum: '',
        scrollLeft: 0,
        depart: true
    },
    onLoad: function (options) {
        wx.setNavigationBarTitle({
            title: '回收站',
        })
    },
    onShow: function () {
        var page = this;
        getRecycleFileList(page);
    },

    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady: function () {
    },

    scrollToLeft: function (e) {
        var self = this;
        var scollId = e.currentTarget.dataset.id;
        var items = e.currentTarget.dataset.fileinfo;
        var nodeId = e.currentTarget.dataset.fileinfo.nodeId;
        var ownerId = e.currentTarget.dataset.fileinfo.ownerId;
        self.setData({
            depart: false
        })

        // if (isShowDeleteModel) {
        //     return;
        // } else {
        //     isShowDeleteModel = true;
        // }
        wx.showModal({
            cancelText: "不恢复",
            confirmText: "恢复",
            title: '提示',
            content: '是否将本文件从回收站中恢复？',
            success: function (res) {
                if (res.confirm) {
                    File.trash(app.globalData.token, items.ownerId, items.nodeId, function (data) {
                        wx.showToast({
                            title: '恢复成功',
                        })
                        getRecycleFileList(self);
                    });

                    self.setData(
                        {
                            isShowDeleteModel: false,
                            showModalStatus: false
                        }
                    );
                }
                if (res.cancel) {
                    self.setData({
                        scrollLeft: 0,
                        isShowDeleteModel: false,
                        depart: true
                    })
                }
            }
        })
    },

    deleteBrowseRecords: function (ownerId, nodeId, callback) {
        File.deleteBrowseRecord(ownerId, nodeId, callback);
    },
    /**
     * 页面相关事件处理函数--监听用户下拉动作
     */
    onPullDownRefresh: function () {
        // wx.startPullDownRefresh();
        // wx.stopPullDownRefresh();
    },
    clickitem: function (e) {
        this.setData(
            {
                showModalStatus: true,
                dataItem: e.currentTarget.dataset.item
            }
        );
    },
    checkitem: function (e) {
        var checks = e.currentTarget.dataset.check;
        var self = this;
        self.data.checknum = '';
        var item_nodeId = e.target.dataset.id;
        var arr = self.data.checklists;

        var obj = {
            checked: true,
            nodeId: checks.nodeId,
            ownerId: checks.ownerId,
        }
        var has = false;
        arr.map((item, index) => {
            if (item_nodeId === item.nodeId) {
                item.checked = !item.checked
                has = true;
                arr.splice(index, 1)
            }

        })
        if (!has) {
            arr.push({
                checked: false,
                nodeId: checks.nodeId,
                ownerId: checks.ownerId,
            })
        }
        var checknum = self.data.checklists.length;
        self.setData({
            checklists: arr,
            checknum: checknum
        })

        self.setData({
            deletearr: arr,
        })
        if (arr.length > 0) {
            self.setData(
                {
                    confim: true
                }
            )
        };
        if (arr.length === 0) {
            self.setData(
                {
                    confim: false
                }
            )
        }
        if (self.data.checknum === 0) {
            self.setData(
                {
                    confim: false
                }
            )
        }

    },
    confim: function () {
        var datalist = this.data.checklists;
        var self = this;
        self.setData(
            {
                check: false,
                checknum: '0',
                confim: false

            }
        )
        var deletelist = {};
        for (var i = 0; i < datalist.length; i++) {
            deletelist = datalist[i]
            delete datalist[i].checked;
        }

        wx.showModal({
            cancelText: "取消",
            confirmText: "删除",
            title: '提示',
            content: '是否删除？',
            success: function (res) {
                if (res.confirm) {
                    for (var i = 0; i < datalist.length; i++) {
                        deletelist = datalist[i]
                        delete datalist[i].checked;
                        File.trashclean(app.globalData.token, deletelist.ownerId, deletelist.nodeId, function (data) {
                            wx.showToast({
                                title: '删除成功'
                            })

                            getRecycleFileList(self);
                        });
                    }
                } else if (res.cancel) {
                    isShowDeleteModel = false;
                }
                self.setData(
                    {
                        check: false,
                        checknum: '0',
                        confim: false

                    }
                )
            }
        })
    },
    checkboxChange: function (e) {
    },
    checkall: function () {
    },
    deleteitem: function (e) {
        this.data.checknum = '';
        this.setData(
            {
                check: true,
                checknum: '0',
                deletearr: [],
                checklists: []

            }
        )
        console.log(this.data.deletearr);
    },
    closecheck: function () {
        this.setData(
            {
                check: false,
                checknum: '0',
                confim: false,
                deletearr: [],
                checklists: []
            }
        )
        console.log(this.data.deletearr);
    },
    menurestore: function () {
        var self = this;
        this.setData({
            showModalStatus: false 
        })
        wx.showModal({
            cancelText: "取消",
            confirmText: "恢复",
            title: '提示',
            content: '是否将本文件从回收站中恢复？',
            success: function (res) {
                if (res.confirm) {
                    File.trash(app.globalData.token, self.data.dataItem.ownerId, self.data.dataItem.nodeId, function (data) {
                        wx.showToast({
                            title: '恢复成功'
                        })
                        getRecycleFileList(self);
                    });
                } else if (res.cancel) {
                    isShowDeleteModel = false;
                }
            }
        })

    },
    menudelete: function () {
        this.setData({
            showModalStatus: false
        });
        var self = this;
        wx.showModal({
            cancelText: "删除",
            confirmText: "删除",
            title: '提示',
            content: '是否删除？',
            success: function (res) {
                if (res.confirm) {
                    File.trashclean(app.globalData.token, self.data.dataItem.ownerId, self.data.dataItem.nodeId, function () {
                        wx.showToast({
                            title: '删除成功'
                        })
                        getRecycleFileList(self);

                    });
                } else if (res.cancel) {
                    isShowDeleteModel = false;
                }
                self.setData(
                    {
                        check: false,
                        checknum: '0'
                    }
                )
            }
        })
    },
    menuclose() {
        this.setData(
            {
                showModalStatus: false
            }
        );
    },
    setModalStatus: function (e) {
        var animation = wx.createAnimation({
            duration: 200,
            timingFunction: "linear",
            delay: 0
        })
        this.animation = animation
        animation.translateY(300).step()
        this.setData({
            animationData: animation.export()
        })
        if (e.currentTarget.dataset.status == 1) {
            this.setData(
                {
                    showModalStatus: true
                }
            );
        }
        setTimeout(function () {
            animation.translateY(0).step()
            this.setData({
                animationData: animation
            })
            if (e.currentTarget.dataset.status == 0) {
                this.setData(
                    {
                        showModalStatus: false
                    }
                );
            }
        }.bind(this), 200)
    }
});

//获取回收站的数据
function getRecycleFileList(page) {
    initFileList(page);
    if (app.globalData.token == '' || app.globalData.cloudUserId == '') {
        session.login();
        return;
    }
    File.recycleFile(app.globalData.token, app.globalData.cloudUserId, function (data) {
        var files = translateRecentRecord(data);
        var data = {};
        data.files = files;
        page.setData({
            data: data,
            isClearOldDate: false
        });
    });
}

function initFileList(page) {
    var data = {};
    var files = [];
    data.files = files;
    page.setData({
        data: data,
        isClearOldDate: true,
        activeIndex: getApp().globalData.indexParam
    });
}

//回收站
function translateRecentRecord(data) {
    var files = data.folders.concat(data.files);
    var fileslist = [];
    var imgUrls = [];
    for (var i = 0; i < files.length; i++) {
        var row = files[i];
        var file = {};
        file.name = row.name;
        file.type = row.type;
        file.nodeId = row.id;
        file.ownerId = row.ownedBy;

        if (row.thumbnailUrlList != undefined && row.thumbnailUrlList.length > 0) {
            file.icon = row.thumbnailUrlList[0].thumbnailUrl;
        } else {
            file.icon = utils.getImgSrc(row);
        }
        file.fileSize = utils.formatFileSize(row.size);
        file.modifiedTime = utils.formatNewestTime(row.modifiedAt);
        file.size = row.size;
        fileslist.push(file);
    }

    return fileslist;
}
