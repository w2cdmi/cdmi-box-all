// disk/batchShare/batchShare.js
var linkClient = require("../module/link.js");

var batchShareName = '';

Page({
    /**
     * 页面的初始数据
     */
    data: {
        checkedList: [],
        isShowDotted: true,
        shareType: 'open',      //open: 公开  code: 提取码
        validityDate: '0',      //有效时间  1: 一天  7: 七天   0: 永久
        allowSave: '1',          //允许保存  1: true  0: false
        isShowAddBtn: false,         //是否隐藏添加按钮
        validityDateTxT: "永久",
        isShowShareBtn: true,
        hiddenDEL: true, //隐藏删除按钮
        isOperable: true, //是否可操作
        isSharing: false,
    },
    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        //初始化选中文件
        getApp().globalData.checkedList = [];      
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
        var checkedList = getApp().globalData.checkedList;
        var isShowDotted = true;
        if (checkedList == undefined || checkedList.length == 0) {
            isShowDotted = true;
        } else {
            isShowDotted = false;
            this.setData({
                checkedList: checkedList,
                isShowDotted: isShowDotted
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
        batchShareName = '';
        getApp().globalData.checkedList = [];
    },
    jumpSelectFile: function () {
        wx.navigateTo({
            url: '/disk/widget/selectFolder?jumpType=selectMultipleFile',
        })
    },
    //点击确认按钮
    onCreateBatchFileLink: function (e) {
        var page = this;
        var checkedList = getApp().globalData.checkedList;
        if (checkedList == undefined || checkedList.length == 0) {
            wx.showToast({
                title: '请选择文件',
                icon: 'none'
            })
            return;
        }
        if (batchShareName == undefined || batchShareName.trim() == '') {
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

        //是否允许转存       true: 不能转存。 false: 能转存
        var disdump = true;
        if (this.data.allowSave == 1) {
            disdump = false;
        }
        var params = {
            role: 'downLoader',
            accessCodeMode: 'static',
            needLogin: true,
            nodeList: tempCheckedList,
            linkName: batchShareName,
            disdump: disdump
        }
        this.setData({
            linkName: params.linkName
        });
        if (this.data.validityDate != undefined && this.data.validityDate != "" && this.data.validityDate != 0) {
            var date = new Date();
            var expireTime = expireDate(this.data.validityDate);
            params.effectiveAt = date.getTime();
            params.expireAt = expireTime;
        }
        //如果选择提取码方式，则改变请求参数
        if (this.data.shareType == 'code') {
            params.role = "viewer";
            params.plainAccessCode = this.data.extractionCode;
        }

        linkClient.createBatchFileLink(params, function (data) {
            wx.showToast({
                title: '创建成功！',
            });

            page.setData({
                delParams: {
                    iNodeId: data.iNodeId,
                    linkCode: data.id,
                    ownerId: data.ownedBy,
                    sharedUserId: 0,
                    createdBy: data.createdBy
                }
            });
            var arr = Object.keys(data);
            if (arr.length > 0) {
                var params = {
                    forwardId: data.createdBy,//ownerID
                    linkCode: data.id,//链接ID
                    plainAccessCode: data.plainAccessCode //提取码
                }
                page.setShareModalStatus(e);//打开分享抽屉
                page.setData({
                    params: params, //[提取码]跳转需要给sharefile.wxml传递的参数
                    hiddenDEL: false,  //隐藏删除按钮不让分享功能截图显示
                    isOperable: false
                });
            }
        });
    },
    //复制验证码
    onExtractionCode: function (e) {
        var code = e.currentTarget.dataset.code + "";
        wx.setClipboardData({
            data: code,
            success: function (res) {
                wx.showToast({
                    title: '复制成功',
                    duration: 500
                });
            }
        })
    },
    //单击  分享形式  的状态
    onShareType: function (e) {
        var types = e.currentTarget.dataset.type;
        this.setData({
            shareType: types
        });
        if (types == "code") {
            this.randomNum();
        }
    },
    //六位随机数
    randomNum: function () {
        var Num = "";
        for (var i = 0; i < 6; i++) {
            Num += Math.floor(Math.random() * 10);
        }
        this.setData({
            extractionCode: Num
        })
    },
    // 有效期 状态
    onValidityDate: function (e) {
        var date = e.currentTarget.dataset.date;
        var validityDateTxT = "";
        if (date == 1) {
            validityDateTxT = "一天";
        } else if (date == 7) {
            validityDateTxT = "七天";
        } else {
            validityDateTxT = "永久";
        }

        this.setData({
            validityDate: date,
            validityDateTxT: validityDateTxT
        });
    },
    //转存状态
    onAllowSave: function (e) {
        var allow = e.currentTarget.dataset.allow;
        this.setData({
            allowSave: allow,
        });
    },
    inputChange: function (e) {
        batchShareName = e.detail.value;
    },
    //textarea 获取焦点事件
    hiddenDrawerScreen: function (e) {
        this.setData({ showModalStatus: false })
    },
    deleteChecked: function (e) {
        if (!this.data.isOperable) {
            return;
        }
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
    //打开手机右上角的转发事件
    onShareAppMessage: function (e) {
        var page = this;
        var params = this.data.params;
        this.setData({
            isSharing: true
        })
        sleep(200);//线程睡眠
        if (e.from == "menu") {
            wx.showToast({
                title: '点击页面按钮分享哦！',
            })
            return;
        }
        // /disk/images/shares/share-card-batch.png  固定的图标 TODO:暂时设置为空，此改动为了动态的显示卡片
        // page.data.linkName    标题
            return {
                title: page.data.linkName,
                path: '/disk/shares/sharefile?linkCode=' + params.linkCode + '&forwardId=' + params.forwardId + "&type=" + page.data.shareType + '&scene=1000' + "&enterpriseId=" + getApp().globalData.enterpriseId,
                imageUrl: "",
                success: function (res) {
                    wx.navigateBack({
                        delta: 1
                    });

                console.log("转发成功")
                // 转发成功	
            },
            complete: function () {
                page.setData({
                    isSharing: false
                })
            }
        }

    },
    //扩展选项-抽屉层
    setPlugModalStatus: function (e) {
        if (!this.data.isOperable) {
            return;
        }
        var animation = createAnimation(this);
        this.setData({
            animationData: animation.export()
        })
        var status = e.currentTarget.dataset.status;
        if (status == 1) {
            this.setData({ showModalStatus: true });
        }
        setTimeout(function () {
            animation.translateY(0).step()

            this.setData({ animationData: animation })
            if (status == 0) {
                this.setData({ showModalStatus: false });
            }
        }.bind(this), 200)
    },
    //分享-抽屉层
    setShareModalStatus: function (e) {
        var animation = createAnimation(this);
        this.setData({
            ShareAnimationData: animation.export()
        })
        var status = e.currentTarget.dataset.status;
        if (status == 1) {
            this.setData({ showShareModal: true });
        }
        setTimeout(function () {
            animation.translateY(0).step()
            this.setData({ ShareAnimationData: animation })
            if (status == 0) {
                this.setData({ showShareModal: false });
            }
        }.bind(this), 200)
    },
    //分享抽屉层[取消]按钮
    delLink: function (e) {
        var page = this;
        this.setShareModalStatus(e);
        linkClient.deleteBatchLink(this.data.delParams, (res) => {
            console.log("刚生成的链接，已经删除");
            wx.showToast({
                title: '链接已删除',
            })
            page.setData({
                isOperable: true,
                hiddenDEL: true
            })
        });
    }
})


//计算几天后时间
function expireDate(n) {
    var date = new Date();
    date.getTime()
    //n代表天数,加号表示未来n天的此刻时间,减号表示过去n天的此刻时间
    var milliseconds = date.getTime() + 1000 * 60 * 60 * 24 * n;
    //getTime()方法返回Date对象的毫秒数,但是这个毫秒数不再是Date类型了,而是number类型,所以需要重新转换为Date对象,方便格式化
    return milliseconds;
}
//创建动画
function createAnimation(page) {
    var animation = wx.createAnimation({
        duration: 200,
        timingFunction: "linear",
        delay: 0
    })
    page.animation = animation
    animation.translateY(300).step()
    return animation;
}

function sleep(numberMillis) {
    var now = new Date();
    var exitTime = now.getTime() + numberMillis;
    while (true) {
        now = new Date();
        if (now.getTime() > exitTime)
            return;
    }
}
