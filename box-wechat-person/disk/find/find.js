// disk/find/index.js
var linkClient = require("../module/link.js");
var utils = require("../module/utils.js");
var config = require("../config.js");

var sliderWidth = 34; // 需要设置slider的宽度，用于计算中间位置
var windowWidth = getApp().globalData.windowWidth;
var recommendPaging = {};//推荐分页
var myPostPaging = {};//我的发布 分页

Page({
    /**
     * 页面的初始数据
     */
    data: {
        tabs: ["推荐", "我的发布"],
        activeIndex: 0, //默认的选中栏
        addBtn: false,  //默认不显示发布按钮
        style: {
            sliderOffset: 0,
            sliderLeft: 0,
            navPosition: "fixed",
            marginTop: 50,
            navbarWidth: 0.46,  //导航条宽度百分比,与他相关的属性会自动计算
        },
        paging: {
            isShowLoadMore: false, //是否显示加载更多
            loadMoreIcon: true
        },
        isShowBlankPage: true,
        datasA: [],
        datasB: [],
        avatorImg: {
            host: config.host,
            token: ''
        }
    },
    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        var page = this;
        var navbarItemWidth = windowWidth * page.data.style.navbarWidth / 2;//导航栏实际宽度
        initPagingData();//初始化分页参数
        page.setData({
            ['style.sliderLeft']: (navbarItemWidth - sliderWidth) / 2,//左边距
            ['style.sliderOffset']: navbarItemWidth * page.data.activeIndex,
        });
        //切换tab
        if (options.sliderOffset != undefined && options.activeIndex != undefined) {
            page.setData({
                sliderOffset: options.sliderOffset,
                activeIndex: options.activeIndex,
                addBtn: options.activeIndex == 1 ? true : false,//是否显示添加按钮
            });
            changeLoadMoreIconStatus(page);
        }

        //新增帖子
        if (options.from !== undefined && options.from != '' && options.from == 'addpost') {
            var navbarItemWidth = windowWidth * page.data.style.navbarWidth / 2;//导航栏实际宽度
            page.setData({
                sliderOffset: navbarItemWidth * 1,
                activeIndex: 1,
                addBtn: true,//是否显示添加按钮
            });
        }

        if (page.data.activeIndex == 0) {
            toGetFindRecommendList(page);
        } else {
            toGetFindMyPostList(page);
        }
    },
    onShow: function () {
        var page = this;
        page.setData({
            ['avatorImg.token']: getApp().globalData.token
        })
    },
  
    //tab切换
    tabClick: function (e) {
        wx.redirectTo({
            url: '/disk/find/find?sliderOffset=' + e.currentTarget.offsetLeft + "&activeIndex=" + e.currentTarget.id,
        })
    },

    //显示菜单
    onShowMenu: function () {
        this.setData({
            isShowMenu: "true"
        })
    },

    onUploadVideo: function (e) {
        var page = this;
        var tempFile = e.detail;

        getApp().globalData.tempFile = tempFile;
        wx.navigateTo({
            url: '/disk/widget/selectFolder?jumpType=' + "uploadVideo",
        })
    },
    showCreateFolder: function () {
        wx.showToast({
            title: '请您进入文件目录创建文件夹！',
            icon: 'none'
        })
    },

    //点击卡片跳转详情页面
    fileItemClick: function (e) {
        var page = this;
        var linkCode = e.currentTarget.dataset.fileinfo.linkCode;

        if (linkCode == undefined || linkCode == "") {
            wx.showToast({
                title: '获取链接失败',
                icon: 'none'
            })
        }
        wx.navigateTo({
            url: '/disk/shares/sharefile?linkCode=' + linkCode + "&forwardId=" + getApp().globalData.cloudUserId,
        })
    },
    //删除我的发布
    deleteMypost: function (e) {
        var nodeId = e.currentTarget.dataset.fileinfo.id;
        var dataId = e.currentTarget.dataset.id;
        var page = this;
        toDeleteMyPost(page, nodeId, dataId);
    },

    //下拉加载
    onPullDownRefresh: function () {
        var page = this;
        var activeIndex = page.data.activeIndex;

        initPagingData(activeIndex);//初始化分页参数
        changeThePullDownStyle(page, "change");//改变下拉时的样式
        if (activeIndex == 0) {
            toGetFindRecommendList(page);
        } else {
            toGetFindMyPostList(page);
        }
    },

    //上拉分页
    onReachBottom: function () {
        var page = this;
        var isHasMore = page.data.activeIndex == 0 ? recommendPaging.isHasMore : myPostPaging.isHasMore;

        if (isHasMore === false) {
            this.setData({
                ['paging.loadMoreIcon']: false
            });
            return;
        }

        wx.setStorageSync('isShowLoading', false)
        if (page.data.activeIndex == 0) {
            recommendPagingFun(page);
        } else {
            myPostPagingFun(page);
        }
    },

    //打开手机右上角的转发事件
    onShareAppMessage: function (e) {
        var page = this;
        var params = e.target.dataset.fileinfo;
        if (params.oneINodeName.indexOf(".") == -1) {
            params.imageUrl = '/disk/images/shares/share-card-folder.png'
        } else if (params.imageUrl === undefined) {
            params.imageUrl = utils.getImgSrcOfShareCard(params.oneINodeName);//获取主图
        }
        if (e.from == "menu") {
            wx.showToast({
                title: '点击页面按钮分享哦！',
            })
            return;
        }
        return {
            title: params.description,
            path: '/disk/shares/sharefile?linkCode=' + params.linkCode + '&forwardId=' + params.ownerId + '&scene=1000',
            imageUrl: params.coverImg,
            success: function (res) {
                wx.showToast({
                    title: '分享成功',
                })
            },
        }
    },
});

//获取推荐信息
function toGetFindRecommendList(page, limit = 10, offset = 0) {
    linkClient.getFindRecommendList(limit, offset, function (data) {
        if (data.length == 10) {
            page.setData({
                ['paging.isShowLoadMore']: true
            })
        }
        var newData = getImgurl(data, page);//获取主图链接
        page.setData({
            datasA: newData
        });
        changeThePullDownStyle(page, "recover");//恢复下拉时的样
    })
}

//获取我的发布信息
function toGetFindMyPostList(page, limit = 10, offset = 0) {
    linkClient.getFindMyPostList(limit, offset, function (data) {
        if (data.length == 10) {
            page.setData({
                ['paging.isShowLoadMore']: true
            })
        }
        var newData = getImgurl(data, page);//获取主图链接
        page.setData({
            datasB: newData
        });
        changeThePullDownStyle(page, "recover");//恢复下拉时的样式
    })
}

//删除帖子操作
function toDeleteMyPost(page, nodeId, dataId) {
    linkClient.deleteMyPost(nodeId, function (data) {
        wx.showToast({
            title: '删除成功！',
        })
        page.data.datasB.splice(dataId, 1);
        page.setData({
            datasB: page.data.datasB
        });
    })
}

//获取主图的图片链接
function getImgurl(data, page) {
    for (var i = 0; i < data.length; i++) {
        data[i].avatorImg = config.host + "/ecm/api/v2/users/getAuthUserImage/" + data[i].accountId + "/" + data[i].ownerId + "?authorization=" + getApp().globalData.token;
        if (data[i].imageUrl && data[i].imageUrl.length > 0) {
            data[i].coverImg = data[i].imageUrl + "/thumbnail?minHeight=452&minWidth=668";
        } else if (data[i].oneINodeName && data[i].oneINodeName.length > 0) {
            var index = data[i].oneINodeName.lastIndexOf(".");
            var fileType = data[i].oneINodeName.substring(index + 1).toLowerCase();
            if (index == -1 || fileType=="inbox") {
                data[i].coverImg = utils.getImgSrc({ type: -1 }, true);//大图片
            } else {
                data[i].coverImg = utils.getImgSrc({ name: data[i].oneINodeName }, true);//大图片
            }
        } else {
            page.setData({
                isShowBlankPage: true
            });
        }
    }
    return data;
}

//改变下拉时的样式
function changeThePullDownStyle(page, param) {
    if (param == "change") {
        page.setData({
            ["style.navPosition"]: "relative",
            ["style.marginTop"]: 0,
        })
    } else {
        page.setData({
            ["style.navPosition"]: "fixed",
            ["style.marginTop"]: 50,
        })
        wx.stopPullDownRefresh()
    }
}

//推荐页 分页
function recommendPagingFun(page) {
    recommendPaging.offset = recommendPaging.offset + 10;
    linkClient.getFindRecommendList(10, recommendPaging.offset, function (data) {
        if (data == undefined || data == '' || data.length == 0) {
            recommendPaging.isHasMore = false;
            changeLoadMoreIconStatus(page);
            return;
        }

        var newData = getImgurl(data, page);//获取主图链接
        page.setData({
            datasA: page.data.datasA.concat(newData)
        });
        wx.setStorageSync('isShowLoading', true)
    })
}

//我的发布 分页
function myPostPagingFun(page) {
    myPostPaging.offset = myPostPaging.offset + 10;
    linkClient.getFindMyPostList(10, myPostPaging.offset, function (data) {
        if (data == undefined || data == '' || data.length == 0) {
            myPostPaging.isHasMore = false;
            changeLoadMoreIconStatus(page);
            return;
        }
        var newData = getImgurl(data, page);//获取主图链接
        page.setData({
            datasB: page.data.datasB.concat(newData)
        });
        wx.setStorageSync('isShowLoading', true)
    })
}

//初始化 分页参数
//@params activeIndex 可选 无参数时全部初始化
function initPagingData(activeIndex) {
    if (activeIndex == 0 || activeIndex == undefined) {
        recommendPaging = { offset: 0, isHasMore: true };
    }
    if (activeIndex == 1 || activeIndex == undefined) {
        myPostPaging = { offset: 0, isHasMore: true };
    }
}

//改变 加载更多 图标    `
function changeLoadMoreIconStatus(page) {
    var isHasMore = page.data.activeIndex == 0 ? recommendPaging.isHasMore : myPostPaging.isHasMore;
    var datasLength = page.data.activeIndex == 0 ? page.data.datasA.length : page.data.datasB.length;
    page.setData({
        ['paging.loadMoreIcon']: isHasMore  //变为  “无更多数据” || “加载中”
    });
    page.setData({
        ['paging.isShowLoadMore']: datasLength >= 10 ? true : false  //是否显示 图标
    })
}
