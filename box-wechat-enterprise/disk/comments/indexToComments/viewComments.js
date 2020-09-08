// disk/comments/indextocomments/viewcomments.js
var session = require("../../../session.js");
var Message = require("../../module/message.js");
var Utils = require("../../module/utils.js");
var linkClient = require("../../module/link.js");

Page({

    /**
     * 页面的初始数据
     */
    data: {
        cursor: 0,
        readedMessages: [],
        isMoreMessage: false,
        isFirstLoad: true
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        var page = this;

        //评论消息通知
        page['theCountOfCommentMessages'] = Message.theCountOfCommentMessages;

        //时间戳转换为·最近时间
        page['formatNewestTime'] = Utils.formatNewestTime;
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
        if(!page.data.isFirstLoad){
            return;
        }

        session.login();
        session.invokeAfterLogin(function () {
            page.reuestCommentsMsg('UNREAD');
        });
    },
    /*请求评论的消息
    *@ modul/message/theCountOfCommentMessages(status, callback, params={})
    */
    reuestCommentsMsg: function (status, param = {}) {//TODO:这里暂时显示“已读”消息
        var page = this;
        var params = new Object();
        status.currentTarget == undefined ? status = "UNREAD" : status = "READED";
        if (status == "READED") {
            params.cursor = page.data.cursor;
            if (page.data.cursor == 0){
                page.data.messages = [];
            }else{
                //没有更多不执行查询操作
                if (!page.data.isMoreMessage){
                    return;
                }
            }
            params.maxcount = 5;//一次加载最大的条数
        }else{
            page.setData({
                isMoreMessage: true,
                isFirstLoad: false
            });
        }
        params.status = status;

        Message.viewCommentsList(params, (res) => {
            if (res == undefined || res == "" || res.length == 0){
                page.setData({
                    isMoreMessage: false
                });
                return;
            }

            var messages = new Array(res.length);

            var getLinkPromises = [];
            for (let i = 0; i < res.length; i++) {
                messages[i] = {};
                messages[i].headImageUrl = res[i].eventUser.headImageUrl;
                messages[i].createTime = Utils.formatNewestTime(res[i].createTime);
                //一级评论
                if (res[i].type == "Tenancy_File"){
                    messages[i].text = unescape(res[i].eventContent.text);  //message == "" 表示点赞
                    messages[i].showName = res[i].eventUser.name;
                }else{
                    var con = unescape(res[i].eventContent.text).split("@><@");
                    messages[i].text = con[0];  //为空表示点赞
                    if (con.length >= 2) {
                        messages[i].showName = res[i].eventUser.name + " 回复 " + con[1]
                    } else {
                        messages[i].showName = res[i].eventUser.name
                    }
                }

                //评论对象
                if (res[i].eventObject.type != "Tenancy_File") {
                    var getLinkPromise = new Promise(function (resolve, reject) {
                        Message.getLinkCodeByObjectId(res[i].eventObject.id,function(data){
                            messages[i].linkCode = data.targetId;
                            resolve(data);
                        }, function (data) {
                            var tempData = {};
                            tempData.code = data.code;
                            if (data.code == 'NoSuchLink') {
                                tempData.link = {};
                                tempData.link.alias = "链接已经不存在";
                            } else if (data.code == 'LinkExpired') {
                                tempData.link.alias = "链接已失效";
                            }
                            resolve(tempData);
                        });
                    });
                    getLinkPromises.push(getLinkPromise);
                }else{
                    messages[i].linkCode = res[i].eventObject.id;
                }
            }
            //获取消息信息完成
            Promise.all(getLinkPromises).then(function (data) {
                var messagesPromises = [];
                messages.forEach((message) => {
                    var messagesPromise = new Promise(function (resolve, reject) {
                        linkClient.getLinkInfo(message.linkCode, function (data) {
                            resolve(data);
                        }, function(data){
                            var tempData = {};
                            tempData.code = data.code;
                            if (data.code == 'NoSuchLink'){
                                tempData.link = {};
                                tempData.link.alias = "链接已经不存在";
                            } else if (data.code == 'LinkExpired'){
                                tempData.link = {};
                                tempData.link.alias = "链接已失效";
                            }
                            resolve(tempData);
                        })
                    });
                    messagesPromises.push(messagesPromise);
                });
                
                Promise.all(messagesPromises).then(function (linkInfos) {
                    messages.forEach((message, index) => {
                        message.linkName = linkInfos[index].link.alias;

                        if (linkInfos[index].file != undefined && linkInfos[index].file != "") {
                            //设置分享icon
                            if (linkInfos[index].file.thumbnailUrlList == undefined || linkInfos[index].file.thumbnailUrlList.length == 0) {
                                message.fileImg = Utils.getImgSrcOfShareCard(linkInfos[index].file.name);
                            } else {
                                message.fileImg = linkInfos[index].file.thumbnailUrlList[0].thumbnailUrl;
                            }
                        } else if (linkInfos[index].folder != undefined && linkInfos[index].folder != "") {
                            message.fileImg = "/disk/images/shares/share-card-folder.png";
                        } else if (linkInfos[index].code == 'NoSuchLink' || linkInfos[index].code == 'LinkExpired'){
                            message.fileImg = "/disk/images/icon/file-undefined.png";
                        }else{
                            message.fileImg = "/disk/images/shares/share-card-batch.png";
                        }
                    });

                    if (status == 'READED') {
                        if(messages.length == 0){
                            page.setData({
                                isMoreMessage: false
                            })
                        }
                        if (page.data.messages != undefined && page.data.messages != "") {
                            messages = page.data.messages.concat(messages);
                        }
                        page.data.cursor = page.data.cursor + 1;
                    }

                    page.setData({
                        messages: messages
                    })
                });
            });
        });

    },
    /**
     * 生命周期函数--监听页面隐藏
     */
    onHide: function () {

    },

    /**
     * 生命周期函数--监听页面卸载
     */
    onUnload: function () {

    },

    /**
     * 页面相关事件处理函数--监听用户下拉动作
     */
    onPullDownRefresh: function () {

    },

    /**
     * 页面上拉触底事件的处理函数
     */
    onReachBottom: function () {
        var params = new Object();
        var status = new Object();
        status.currentTarget = "READED"
        this.reuestCommentsMsg(status, params);
    },

    jumpSharefile: function (e) {
        var item = e.currentTarget.dataset.item;

        if(item == undefined || item == "" || item.linkCode == undefined || item.linkCode == ""){
            return;
        }

        wx.navigateTo({
            url: '/disk/shares/sharefile?linkCode=' + item.linkCode,
        })
    }
})



