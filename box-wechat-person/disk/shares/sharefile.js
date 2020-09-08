// disk/sharefile.js
/**
 * 此页面只在用户点击“小程序卡片”时（他人分享的文件），才会调用。
 */
var linkClient = require("../module/link.js");
var session = require("../../session.js");
var file = require("../module/file.js");
var utils = require("../module/utils.js");
var commentComponent = require("../template/comment.js");
var videoComponent = require("../template/video.js");

var enterpriseId = "";  //企业id
var ownerId = 0;        //外链文件所属人
var nodeId = 0;         //外链文件编号
var plainAccessCode = '';//提取码

var linkCode = "";      //外链编号
var linkName = "";      //外链名称
var forwardId = "";     //链接转发人

var shareIcon = "/disk/images/shares/share-card-batch.png";          //分享icon

var isFirstLoad = true;     //是否首次显示

Page({

    /**
     * 页面的初始数据
     */
    data: {
        previewImageUrls: []
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        var page = this;
        page.data.previewImageUrls = [];
        isFirstLoad = true;
        /**
         * 三种方式进入该页面
         * 1、通过linkcode、forwardId和enterpriseId进入该页面
         * 2、通过ownerId、nodeId和enterpriseId进入该页面。（ownerId、nodeId去查询linkcode）
         * 3、通过linkcode、frowardId进入页面
         */
        if (options.scene != undefined && options.scene == 1000) {
            this.setData({
                backIndexBtn: true
            })
        }
        linkCode = options.linkCode || "";
        forwardId = options.forwardId || "";
        ownerId = options.ownerId || "";
        nodeId = options.nodeId || "";
        plainAccessCode = options.plainAccessCode || "";

        this.setData({
            linkCode: linkCode
        })

        page['giveOrCancelPraise'] = commentComponent.giveOrCancelPraise;
        page['getMorePraiseList'] = commentComponent.getMorePraiseList;
        page['commentGiveOrCancelPraise'] = commentComponent.commentGiveOrCancelPraise
        page['bindTextInput'] = commentComponent.bindTextInput;
        page['onClickInput'] = commentComponent.onClickInput;
        page['sendContent'] = commentComponent.sendContent;
        page['commentReply'] = commentComponent.commentReply;
        page['replySomeoneComment'] = commentComponent.replySomeoneComment;
        page['moreReplyMessage'] = commentComponent.moreReplyMessage;
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
        if (!isFirstLoad) {
            return;
        }
        var page = this;
        //获取传入的转发人Id，如果没有则是通过传入ownerId和nodeId分享
        if (forwardId == undefined || forwardId == "") {
            forwardId = ownerId;
        }

        session.login({
            enterpriseId: 0,
            inviterCloudUserId: forwardId
        });


        //等待登录成功后，执行;
        session.invokeAfterLogin(function () {

            if (linkCode == undefined || linkCode == "") {
                if (ownerId == undefined || ownerId == "" || nodeId == undefined || nodeId == "") {
                    console.log("parm exception");
                    //退出当前页面
                    wx.navigateBack({
                        delta: 1
                    })
                }
                forwardId = ownerId;

                linkClient.getLinkCode(ownerId, nodeId, function (data) {
                    linkCode = data.id;
                    getLinkInfo(linkCode, page);

                    //2. 保存转发记录(由接收人负责记录)
                    if (forwardId != undefined && forwardId != "" && forwardId !== getApp().globalData.cloudUserId) {
                        linkClient.saveLinkCode(getApp().globalData.cloudUserId, linkCode, forwardId);
                    }
                });
            } else {
                getLinkInfo(linkCode, page);

                //2. 保存转发记录(由接收人负责记录)
                if (forwardId != undefined && forwardId != "" && forwardId !== getApp().globalData.cloudUserId) {
                    linkClient.saveLinkCode(getApp().globalData.cloudUserId, linkCode, forwardId);
                }
            }
        });

    },
    /**
     * 页面上拉触底事件的处理函数
     */
    onReachBottom: function () {
        var page = this;
        commentComponent.getCommentAndChildrenListForPage(page);
    },

    /**
     * 生命周期函数--监听页面卸载
     */
    onUnload: function () {
        videoComponent.handlePlayMusic();
    },

    /**
     * 用户点击右上角分享
     */
    onShareAppMessage: function (options) {
        return {
            title: linkName,
            path: '/disk/shares/sharefile?linkCode=' + linkCode + '&forwardId=' + getApp().globalData.cloudUserId + '&scene=1000',
            imageUrl: shareIcon,
            success: function (res) {
                // 转发成功
            },
            fail: function (res) {
                // 转发失败
            }
        }
    },
    //点击文件
    onNodeItemClick: function (e) {
        var fileInfo = e.currentTarget.dataset.fileInfo;
        if (fileInfo.type < 1) {
            wx.navigateTo({
                url: '/disk/shares/sharefolder?ownerId=' + fileInfo.ownedBy + "&nodeId=" + fileInfo.id + "&linkCode=" + linkCode + '&scene=1000' + "&plainAccessCode=" + plainAccessCode,
            })
            return;
        }

        var ext = fileInfo.name.substring(fileInfo.name.lastIndexOf('.') + 1, fileInfo.name.length).toLowerCase();
        var token = "link," + linkCode;
        if (plainAccessCode != undefined && plainAccessCode != ""){
            token = token + "," + plainAccessCode;
        }
        if (ext == 'mp3') {
            file.getFileDownloadUrl(fileInfo.ownedBy, fileInfo.id, (res) => {
                var url = utils.replacePortInDownloadUrl(res.url);
                wx.navigateTo({
                    url: '/disk/template/shareMusic?url=' + encodeURIComponent(url) + '&size=' + fileInfo.size + '&name=' + fileInfo.name
                })
            }, token);
        } else {
            getApp().globalData.imgUrls = this.data.previewImageUrls;
            file.openFile(fileInfo, null, token);
        }
    },
    onSaveToPerson: function (e) {
        this.setData({
            checkfilemask: false
        });
        if (this.data.nodeList != undefined) {
            if (this.data.nodeList.length == 1) {
                wx.setStorageSync("checkedfile", this.data.nodeList);
            }
        } else {
            wx.setStorageSync("checkedfile", [this.data.node]);
        }
        wx.navigateTo({
            url: '/disk/save/saveToPersion?linkCode=' + linkCode,
        })
    },
    //选择保存文件
    showCheckFileMask: function (e) {
        wx.setStorageSync("checkedfile", []);
        var checkNodeList = this.data.nodeList;
        checkNodeList.forEach((item) => {
            item.checked = false;
        });
        this.setData({
            checkfilemask: true,
            checkNodeList: checkNodeList,
            checkfilenum: 0
        })
    },
    onCancelSave: function (e) {
        wx.setStorageSync("checkedfile", []);
        this.setData({
            checkfilemask: false,
            checkNodeList: [],
            checkfilenum: 0
        })
    },
    checkFile: function (e) {
        var page = this;
        var index = e.currentTarget.dataset.index;
        if (index == undefined) {
            return;
        }
        var checkNodeList = this.data.checkNodeList;
        var checkedFile = checkNodeList[index];
        var checkedFiles = wx.getStorageSync('checkedfile');
        if (checkedFile.checked) {
            checkedFiles.map((item, index) => {
                if (item == checkedFile) {
                    checkedFiles.splice(index, 1);
                }
            });

            checkedFile.checked = false;
            this.data.checkfilenum -= 1;
        } else {
            if (checkedFiles == undefined || checkedFiles.length == 0) {
                checkedFiles = [checkedFile];
            } else {
                checkedFiles.push(checkedFile);
            }

            checkedFile.checked = true;
            this.data.checkfilenum += 1;
        }
        wx.setStorageSync("checkedfile", checkedFiles);

        checkNodeList[index] = checkedFile;
        page.setData({
            checkNodeList: checkNodeList,
            checkfilenum: page.data.checkfilenum
        });
    }
});

function getLinkInfo(linkCode, page) {
    linkClient.getLinkInfo(linkCode, function (data) {
        //外链是否为自己创建
        if (data.link.createdBy == getApp().globalData.cloudUserId){
            plainAccessCode = data.link.plainAccessCode;
        }

        //如果有提取码，进入提取码界面
        if (data.link.plainAccessCode != undefined && data.link.plainAccessCode != "" && plainAccessCode != data.link.plainAccessCode){
            wx.redirectTo({
                url: '/disk/batchShare/extractionEode/extractionEode?forwardId=' + forwardId + "&linkCode=" + linkCode + "&plainAccessCode=" + data.link.plainAccessCode + "&linkName=" + encodeURIComponent(data.link.alias),
            })
            return
        }

        ownerId = data.link.createdBy;
        linkName = data.link.alias;
        commentComponent.init(linkCode, ownerId, page);
        data.link.createdAt = utils.getFormatDate(data.link.createdAt);
        //是否有权限保存
        var isSave = false;
        if (!data.link.disdump) {
            isSave = true;
        }
        page.setData({
            link: data.link,
            isSave: isSave
        });

        /**
         * 显示某一个面板
         * singleFile: 单个文件显示
         * vide: 视频显示
         * multipleFile: 多文件显示
         */
        var showPanel = "";
        // if (!data.hasOwnProperty("subFileList")) {
        if (data.file != undefined && data.file != "") {
            data.subFileList = [data.file];
            //设置分享icon
            if (data.file.thumbnailUrlList == undefined || data.file.thumbnailUrlList.length == 0) {
                shareIcon = utils.getImgSrcOfShareCard(data.file.name);
            } else {
                shareIcon = data.file.thumbnailUrlList[0].thumbnailUrl;
            }
        } else if (data.folder != undefined && data.folder != "") {
            data.subFileList = [data.folder];
            shareIcon = "/disk/images/shares/share-card-folder.png";
        } else {
            shareIcon = "/disk/images/shares/share-card-batch.png";
            data.subFileList = data.link.subFileList;
        }
        // }
        //判断是否返回数据是单个文件还是多个文件，并转换时间格式，文件大小，icon等数据
        if (data.subFileList.length == 0) {
            data.subFileList = [];
        } else if (data.subFileList.length == 1) {
            showPanel = "singleFile";
            var node = data.subFileList[0];
            if (node.type < 1) {
                node = file.convertFolderList(data.subFileList, false, true)[0];
            } else {
                var files = file.convertFileList(data.subFileList, false, true);
                node = files[0];
                page.data.previewImageUrls = files.previewImageUrls;
                if (/\.(mp4)$/i.test(node.name)) {
                    showPanel = "video";
                    file.getFileDownloadUrl(node.ownedBy, node.id, function (data) {
                        videoComponent.videoInit(encodeURIComponent(data.url), page, false);
                    })
                }
            }
            page.setData({
                node: node
            });
        } else {
            showPanel = "multipleFile";
            var tempFiles = [];
            var tempFolder = [];
            data.subFileList.map((item, index) => {
                if (item.type < 1) {
                    tempFolder.push(item);
                    item = file.convertFolderList([item], false, true)[0];
                } else {
                    tempFiles.push(item);
                    item = file.convertFileList([item], false, true)[0];
                }
            });
            wx.setStorageSync('isPagination', false);   //不是分页
            var files = file.convertFileList(tempFiles, false, true);   //生成预览图片地址
            page.data.previewImageUrls = files.previewImageUrls;
            data.subFileList = tempFolder.concat(tempFiles);
            //换成大图标
            data.subFileList.map((item, index) => {
                if (item.thumbnailUrlList.length > 0 && item.type > 0) {
                    var thumbnailUrl = item.thumbnailUrlList[0].thumbnailUrl;
                    data.subFileList[index].icon = utils.getWantSizeImg(thumbnailUrl, 200, 200);
                }
            })
            page.setData({
                nodeList: data.subFileList
            });
        }

        page.setData({
            showPanel: showPanel
        })

        isFirstLoad = false;
    });
}
