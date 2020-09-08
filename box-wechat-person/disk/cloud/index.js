// disk/cloud/index.js
var file = require("../module/file.js");
var menu = require("../template/menu.js");
var File = require("../module/file.js");
var link = require("../module/link.js");
var utils = require("../module/utils.js");
var musicService = require("../module/music.js");
var operation = require("../template/operation.js");
var createFolder = require("../template/createFolder.js");
var music = require("../template/music.js");
var app = getApp();
//文件操作相关
var ownerId = 0;
var nodeId = 0;
//面包屑
var crumbs = [];
var newFolderName = "";
var session = require("../../session.js");
var canHasMore = true;//是否可以用上拉刷新
var grids = [];
var isShowGrids = false;
var isFirstLoad = true; //默认首次加载
var isFocusTOInput = true;//是否可以再次进入input获取焦点事件
var loadMoreStop = true   //点击文件夹，是否允许上拉数据渲染
Page({
    /**
     * 页面的初始数据
     */
    data: {
        grids: [],
        cells: [],
        scrollTop: 40,
        crumbsShow: '',
        isShowGrids: isShowGrids,
        isShowSortView: true,//隐藏排序VIEW
        desc_img: [[true, false], [true, true]],//[0=>[箭头方向，是否隐藏],1=>[箭头方向，是否隐藏]]
        baseActionsheet: {   //actionsheet
            show: false,
            cancelText: '关闭',
            closeOnClickOverlay: true,
            componentId: 'baseActionsheet'
        },
        loadMoreIcon: true,//是否隐藏加载更多图标
        WXh: wx.getSystemInfoSync().windowHeight, //获取屏幕高度
        filesCount: 0,//文件列表：记录 offset的默认值
        currPageCount: 1,  //当前的分页是第几页
        isUseStorage: false,  //不使用缓存 的数据渲染 页面
        currentPageIcon: true,
        previewImageUrls: [],//预览图片默认为空
        isCurrPagesetData: true,
        scrollHeight: true,   //是否隐藏搜索页面
        searchFolders: null,//搜索结果的默认值
        searchFiles: null,
        isPaging: true,  //是否启用分页
        isSearch: true,
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        isFirstLoad = true;
        var page = this;
        var scene = options.view;
        if (scene == "zhida"){
            page.setData({
                isShowTip: true
            });
        }

        //长按菜单
        page['showOperation'] = operation.showOperation;
        page['updateNodeName'] = operation.updateNodeName;
        page['deleteNode'] = operation.deleteNode;
        page['hideOperation'] = operation.hideOperation;
        page['moveTo'] = operation.moveTo;
        page['setShortcut'] = operation.setShortcut;
        //文件夹
        page['inputChange'] = createFolder.inputChange
        page['showUpdateFolderPanel'] = createFolder.showUpdateFolderPanel
        page['onCreateFolderCancel'] = createFolder.onCreateFolderCancel
        page['onCreateFolderConfirm'] = createFolder.onCreateFolderConfirm

        //功能菜单
        page['switchMenuPanel'] = menu.switchMenuPanel;
        page['chooseUploadImage'] = menu.chooseUploadImage;
        page['chooseUploadVedio'] = menu.chooseUploadVedio;
        page['switchMusicPanel'] = menu.switchMusicPanel;
        page['showCreateFolderPanel'] = menu.showCreateFolderPanel;
        page['backupIntroduction'] = menu.backupIntroduction;
        page['hideMenuPanel'] = menu.hideMenuPanel;
        page['jumpForwardPage'] = menu.jumpForwardPage;
        page['exitBackup'] = menu.exitBackup;

        //音乐播放器
        page['playOrStopMusic'] = music.playOrStopMusic;
        page['lastMusicPlay'] = music.lastMusicPlay;
        page['nextMusicPlay'] = music.nextMusicPlay;
        page['playCurrentMusic'] = music.playCurrentMusic;
        page['deleteMusic'] = music.deleteMusic;
        page['clearMusicList'] = music.clearMusicList;
        page['openMusicList'] = music.openMusicList;
        page['closeMusicList'] = music.closeMusicList;

        //构造第一个面包屑节点
        crumbs = [];
    },

    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady: function () {
        wx.hideShareMenu();  //屏蔽手机右上角的转发功能
    },

    /**
     * 生命周期函数--监听页面显示
     */
    onShow: function () {
        var page = this;
        session.login({
            enterpriseId: 0
        });
        session.invokeAfterLogin(function () {
            if (getApp().globalData.accountType == 0 || getApp().globalData.accountType > 100) {
                if (!isFirstLoad) {
                    return;
                }

                var ownerId = getApp().globalData.cloudUserId
                var nodeId = 0
                //显示个人页面
                page.setData({
                    isShowGrids: false,
                    showModal: false,
                    loadMoreIcon: true,//分页：是否隐藏加载更多图标
                })

                //刷新文件列表
                if (crumbs.length > 0) {
                    var crumb = crumbs[crumbs.length - 1];
                    if (page.data.isHide) {  //如果页面是临时走开 又回来的
                        page.setData({ isUseStorage: true }); //分页：使用缓存里的数据渲染页面，为了点开文件不重新分页
                        page.lsOfFolderForClickCrum(app.globalData.token, crumb.ownerId, crumb.nodeId);
                    } else {
                        page.setData({ isUseStorage: false }); //分页：使用缓存里的数据渲染页面
                        page.lsOfFolderForClickCrum(app.globalData.token, crumb.ownerId, crumb.nodeId);
                    }
                } else {
                    crumbs.push({
                        index: 0,
                        name: "个人文件",
                        ownerId: getApp().globalData.cloudUserId,
                        nodeId: 0
                    });
                    page.setData({
                        crumbs: crumbs
                    });
                    page.lsOfFolderForClickCrum(app.globalData.token, getApp().globalData.cloudUserId, 0);
                }

            } else {
                grids = [{
                    "id": "personalFiles",
                    "icon": '../images/persion-file.png',
                    "text": "个人文件",
                    "description": "个人文件随取随用"
                },
                {
                    "id": "departmentFiles",
                    "icon": '../images/depfile.png',
                    "text": "部门文件",
                    "description": "公司部门文件"
                },
                {
                    "id": "teamSpaceFiles",
                    "icon": '../images/teamfile.png',
                    "text": "协作空间",
                    "description": "团队协作中用到的文件"
                },
                {
                    "id": "enterpriseLibrary",
                    "icon": '../images/library.png',
                    "text": "企业文库",
                    "description": "公司公共资源文件"
                }];

                var isShowGrids = true;
                page.setData({
                    grids: grids,
                    isShowGrids: isShowGrids,
                    crumbs: [],
                });
            }
            //初始化音乐播放器
            music.musicPlayInit(page);
            menu.initMenu(page);
            menu.menuPanelHideAnimation(page);

            page.setData({ isHide: false });  //初始化 判断页面是否跳转的参数
            page.data.isFirstLoad = false;
        });
    },
    closePrductIntroduce: function(){
        this.setData({
            isShowTip: false
        });
    },
    onHide: function () {
        this.setData({ isHide: true });//因为点击文件会进入Hide，为了让页面渲染的数据不重新加载，而删除等操作重新加载
    },
    onclick: function (e) {
        switch (e.currentTarget.id) {
            case "personalFiles":
                wx.navigateTo({
                    url: "/disk/widget/filelist?ownerId=" + getApp().globalData.cloudUserId + '&nodeId=0&name=' + encodeURIComponent("个人文件"),
                });
                break;
            case "departmentFiles":
                wx.navigateTo({
                    url: "./teamspacelist?type=1",
                })
                break;
            case "teamSpaceFiles":
                wx.navigateTo({
                    url: "./teamspacelist?type=0",
                })
                break;
            default:
                this.navigateToFileList();
        }
    },
    onclickPersonFolder: function () {
        wx.navigateTo({
            url: "/disk/widget/filelist?ownerId=" + getApp().globalData.cloudUserId + '&nodeId=0&name=' + encodeURIComponent("个人文件"),
        });
    },
    navigateToFileList: function () {
        if (getApp().globalData.accountType == 0 || getApp().globalData.accountType > 100) {
            wx.showToast({
                title: '暂不开放',
            })
            return;
        }
        var page = this;
        var offset = 0;   //初始值0
        var limit = 100;   //加载数量
        file.listEnterpriseSpace(getApp().globalData.token, getApp().globalData.cloudUserId, offset, limit, function (data) {
            var team = data.memberships[0].teamspace;
            wx.navigateTo({
                url: '/disk/widget/filelist?ownerId=' + team.id + '&nodeId=0&name=' + encodeURIComponent(team.name),
            });
        });
    },

    lsOfFolderForClickCrum: function (token, ownerId, nodeId) {
        var page = this;
        var pageData = wx.getStorageSync('fileInfo');
        if (pageData !== "" && page.data.isUseStorage) {
            page.setData({
                isShowBlankPage: false,
                folders: pageData.folders,
                files: pageData.files,
            });
            page.setData({ isUseStorage: false }); //分页：使用缓存里的数据渲染页面
        } else {
            File.lsOfFolder(token, ownerId, nodeId, function (data) {
                var folders = File.convertFolderList(data.folders);
                var files = File.convertFileList(data.files);
                var pageCount = Math.ceil(data.totalCount / 10);//分页：需要分几页
                //判断是不是空文件夹
                var isShowBlankPage = false;
                if (folders.length == 0 && files.length == 0) {
                    isShowBlankPage = true;
                }
                page.setData({
                    pageCount: pageCount,
                    folders: folders,
                    files: files,
                    isShowBlankPage: isShowBlankPage,
                });
                addPreviewImageUrls(page, files.previewImageUrls);
                initPagingData(page);//初始化分页数据
                wx.removeStorageSync('fileInfo');
            })
        }
    },

    showPnal: function () {
        this.setData({
            showddodal: true,
            showPanl: true
        });
    },
    onShareAppMessage: function (e) { //打开手机右上角的转发事件
        this.setData({
            baseActionsheet: { show: false }
        })
        if (e.from == "button") {
            var fileInfo = e.target.dataset.info;
            var nodeId = fileInfo.id;
            var ownerId = fileInfo.ownedBy;
            var enterpriseId = app.globalData.enterpriseId;

            link.createDefaultLink(ownerId, nodeId, function (data) {
                console.log("创建成功！");
            });

            var url, icon;
            if (fileInfo.type < 1) { //文件夹
                icon = "/disk/images/shares/share-card-folder.png";
            } else { //文件
                if (fileInfo.thumbnailUrlList.length == 0) {
                    icon = utils.getImgSrcOfShareCard(fileInfo.name);
                } else {
                    icon = fileInfo.thumbnailUrlList[1].thumbnailUrl;
                }
            }
            url = '/disk/shares/sharefile?ownerId=' + fileInfo.ownedBy + "&nodeId=" + fileInfo.id + "&enterpriseId=" + getApp().globalData.enterpriseId + '&scene=1000';

            return {
                title: fileInfo.name,
                path: url,
                imageUrl: icon,
                success: function (res) {
                    // 转发成功
                },
                fail: function (res) {
                    // 转发失败
                }
            }
        }
    },
    //除第一个面包屑外的面包屑的点击事件
    onCrumbClick: function (e) {
        var page = this;
        initPagingData(page);//初始化分页数据
        var crumb = e.currentTarget.dataset.crumb;

        page.setData({
            isShowMusicListPanel: false,
            loadMoreIcon: true,//分页：是否隐藏加载更多图标
        });

        //删除点击节点之后的数据
        crumbs = crumbs.splice(0, crumb.index + 1);
        page.setData({
            crumbs: crumbs
        });
        page.lsOfFolderForClickCrum(app.globalData.token, crumb.ownerId, crumb.nodeId);
    },
    //刷新文件
    refreshFile: function (nodeId, types) {
        var folders = this.data.folders;
        var files = this.data.files;
        if (types == 0) {
            for (var i = 0; i < folders.length; i++) {
                if (folders[i].id == nodeId) {
                    var deleNode = folders.splice(i, 1);
                    break;
                }
            }
            this.setData({ folders: folders });
        } else {
            for (var i = 0; i < files.length; i++) {
                if (files[i].id == nodeId) {
                    var deleNode = files.splice(i, 1);
                    break;
                }
            }
            this.setData({ files: files });
        }
    },
    //点击文件夹
    onFolderItemClick: function (e) {
        var page = this;
        page.setData({
            isShowMusicListPanel: false,
        });
        canHasMore === false;
        initPagingData(page);//初始化分页数据

        var folder = e.currentTarget.dataset.folderinfo;
        var folderNodeName = folder.name;
        var folderOwnerId = folder.ownedBy;
        var folderNodeId = folder.id;
        if (typeof (folder.isListAcl) == 'undefined' || folder.isListAcl) {
            //刷新文件列表
            loadMoreStop = false;
            lsOfFolder(app.globalData.token, folderOwnerId, folderNodeId, page, folderNodeName, folderOwnerId, folderNodeId);
        } else {
            wx.showToast({
                title: '没有访问权限'
            });
        }

    },

    //点击文件
    onFileItemClick: function (e) {
        var file = e.currentTarget.dataset.fileinfo;
        var page = this;
        //打开文件
        getApp().globalData.imgUrls = page.data.previewImageUrls;
        File.openFile(file, function (musicState) {
            music.refreshPage(musicState.state, page);
            if (musicState.state == musicService.PLAY_STATE) {
                music.listenPlayState(page);
                getApp().globalData.isShowMusicPanel = true;
                page.setData({
                    isShowMusicPanel: getApp().globalData.isShowMusicPanel
                });
            }
        });
    },
    //搜索框获取到焦点，显示搜索页面
    showSearchPage: function () {
        var data = this.data;

        if (isFocusTOInput) {
            isFocusTOInput = false;
            var beforeSearchInfo = new Object();
            beforeSearchInfo.isShowBlankPage = data.isShowBlankPage;//备份空白页面
            beforeSearchInfo.folders = this.data.folders;
            beforeSearchInfo.files = this.data.files;
            wx.setStorageSync('beforeSearchInfo', beforeSearchInfo);

            this.setData({
                isSearch: false, //不隐藏搜索框
                folders: [],
                files: [],
                isShowBlankPage: false,
                currentPageIcon: true,
                isPaging: false,//搜索不分页
            })
        }
    },
    //【取消】点击事件
    cancelSearch: function (event) {
        var beforeSearchInfo = wx.getStorageSync('beforeSearchInfo');
        isFocusTOInput = true;
        this.setData({
            searchFolders: null,
            searchFiles: null,
            inputVal: '',
            folders: beforeSearchInfo.folders,
            files: beforeSearchInfo.files,
            isShowBlankPage: beforeSearchInfo.isShowBlankPage,
            isSearch: true, //不隐藏搜索框
            isPaging: true,
        })
        wx.removeStorageSync('beforeSearchInfo');//为避免重复储存
    },

    //调用搜索请求
    searchRequest: function (e) {
        var page = this;
        var searchKeyWord = e.detail.value.trim();
        var ownerId = this.data.crumbs[0].ownerId;
        var paramObj = new Object();
        //方法原路径： ../module/file.js->searchFiles(searchWord,params, ownerId, callback)；
        File.searchFiles(searchKeyWord, ownerId, (res) => {
            var folders = File.convertFolderList(res.folders);//文件夾·信息處理
            var files = File.convertFileList(res.files);//文件·信息處理

            var isShowBlankPage = true;
            folders.length == 0 && files.length == 0 ? '' : isShowBlankPage = false;
            page.setData({
                searchFiles: files,
                isShowBlankPage: isShowBlankPage,
            })
            // searchFolders: folders,临时屏蔽，下周在做，这是个BUG
            addPreviewImageUrls(page, files.previewImageUrls);
        });
    },

    //点击[排序图标]：显示排序view,  TODO:默认按照文件夹优先，时间正序排列
    fileSort: function () {
        this.setData({
            isShowSortView: !this.data.isShowSortView,
        })
    },
    //点击【排序按钮的子选项】，执行排序请求  并  重新渲染页面数据
    sortBytypes: function (e) {
        var page = this;
        var i = parseInt(e.currentTarget.dataset.types);//检查是按时间排序（0），还是名字排序（1）
        var params = new Object();
        var descImg = this.data.desc_img;
        i ? params.field = 'name' : params.field = 'modifiedAt';//按哪种方式排序
        params.direction = descImg[i][0] ? 'DESC' : 'ASC';//排序是正序还是倒序
        var crumbs = this.data.crumbs
        //取面包屑最后一个
        var ownerId = crumbs[0].ownerId;
        var nodeId = crumbs[crumbs.length - 1].nodeId;
        File.oderFiles(params, ownerId, nodeId, (res) => {
            page.setData({
                folders: File.convertFolderList(res.folders),//文件夾·信息處理
                files: File.convertFileList(res.files),//文件·信息處理
            })
        });
        descImg[i][0] = !this.data.desc_img[i][0];//改变箭头指向
        descImg[i][1] = !this.data.desc_img[i][1];//是否显示箭头
        i ? descImg[i - 1][1] = true : descImg[i + 1][1] = true;//隐藏另一个箭头的状态
        i ? descImg[i][1] = false : descImg[i][1] = false;//显示本箭头
        this.setData({
            desc_img: descImg,
            isShowSortView: true
        })
    },
    //input文字输入触发事件
    inputTyping: function (e) {
        this.setData({
            inputVal: e.detail.value
        });
    },

    //drawer function
    toggleActionsheet(e) {
        var nodeInfo = e.currentTarget.dataset.folderinfo;
        var info = e.currentTarget.dataset.info;
        if (typeof (nodeInfo) == 'undefined') {
            nodeInfo = e.currentTarget.dataset.fileinfo;
        }
        var node = {};
        node.ownerId = nodeInfo.ownedBy;
        node.nodeId = nodeInfo.id;
        node.name = nodeInfo.name;
        node.type = nodeInfo.type;
        this.setData({
            info: info,
            node: node,
            baseActionsheet: { show: true }
        });
    },

    handleZanActionsheetCancel({ componentId }) {
        this.setData({
            [`${componentId}.show`]: false
        });
    },


    _handleZanActionsheetMaskClick({ currentTarget = {} }) {
        const dataset = currentTarget.dataset || {};
        const { componentId, closeOnClickOverlay } = dataset;

        // 判断是否在点击背景时需要关闭弹层
        if (!closeOnClickOverlay) {
            return;
        }

        resolveCancelClick.call(this, { componentId });
    },

    _handleZanActionsheetCancelBtnClick(e) {
        const componentId = extractComponentId(e);

        resolveCancelClick.call(this, { componentId });
    },

    _handleZanActionsheetBtnClick({ currentTarget = {} }) {
        const dataset = currentTarget.dataset || {};
        const { componentId, index } = dataset;

        if (this.handleZanActionsheetClick) {
            this.handleZanActionsheetClick({ componentId, index });
        } else {
            console.warn('页面缺少 handleZanActionsheetClick 回调函数');
        }
    },

    handleZanActionsheetClick({ componentId, index }) {
        console.log(`item index ${index} clicked`);

        // 如果是分享按钮被点击, 不处理关闭
        if (index === 2) {
            return;
        }

        this.setData({
            [`${componentId}.actions[${index}].loading`]: true
        });

        setTimeout(() => {
            this.setData({
                [`${componentId}.show`]: false,
                [`${componentId}.actions[${index}].loading`]: false
            });
        }, 1500);
    },
    //分页:滚动到底部触发事件,上拉加载更多
    scrollLower: function () {
        if (!this.data.isPaging || this.data.pageCount == this.data.currPageCount) { //如果当前页数已经等于总共分页数，则停止
            this.setData({
                loadMoreIcon: true,
            })
            return;
        }
        if (canHasMore === false) {
          return;
        } else {
          canHasMore = false;
        }
        wx.setStorageSync('isShowLoading', false);
        var page = this;
        var folders = page.data.folders;
        var files = page.data.files;
        page.setData({
            filesCount: page.data.filesCount + 10, //每次触发上拉事件，把offset+10  
            currPageCount: page.data.currPageCount + 1, //当前是分页的第几页
            loadMoreIcon: false,  //触发到上拉事件，不隐藏加载图标
            currentPageIcon: false
        });
        File.lsOfFolder(app.globalData.token, crumbs[0].ownerId, crumbs[crumbs.length - 1].nodeId, function (data) {
            wx.setStorageSync('isPagination', true); //是否是 分页，此参数决定预览图URL列表是否新增
            var dataFolders = File.convertFolderList(data.folders);
            var dataFiles = File.convertFileList(data.files);
            var previewImageUrls = page.data.previewImageUrls.concat(dataFiles.previewImageUrls);
            addPreviewImageUrls(page, previewImageUrls);
            if (dataFolders.length > 0 || dataFiles.length > 0) {
                var newFolders = folders.concat(dataFolders)
                var newFiles = files.concat(dataFiles)
                setTimeout(function () {
                    page.setData({
                        folders: newFolders,
                        files: newFiles,
                        loadMoreIcon: true,
                    });
                }, 500);

                // console.log("|scrollLower：" + canHasMore + ",and 800s->true");
                // console.log("|pageNumber:" + page.data.currPageCount + " - " + page.data.pageCount);
                // console.log("|total: " + data.totalCount + " - acttotal:" + (newFolders.length + newFiles.length));
                //为了用户体验的流畅性，将加载的数据放在[fileInfo缓存]
                var fileInfo = new Object();
                fileInfo.folders = newFolders;
                fileInfo.files = newFiles;
                wx.removeStorageSync('fileInfo');//为避免重复储存
                wx.setStorageSync('fileInfo', fileInfo);
            } else {
                page.setData({
                    loadMoreIcon: true,
                })
            }
            canHasMore = true;//本次请求完毕可进行下次请求
            wx.removeStorageSync('isShowLoading');
            return;
        }, page.data.filesCount);
    },
});
/*查询指定目录下的文件列表(ls命令)
*  @点击文件夹进入 
*/
function lsOfFolder(token, ownerId, nodeId, page, folderNodeName, folderOwnerId, folderNodeId) {
    File.lsOfFolder(token, ownerId, nodeId, function (data) {
        var folders = File.convertFolderList(data.folders);
        var files = File.convertFileList(data.files);
        var pageCount = Math.ceil(data.totalCount / 10);//分页：需要分几页

        //增加面包屑节点, 刷新界面
        crumbs.push({
            index: crumbs.length,
            name: folderNodeName,
            ownerId: folderOwnerId,
            nodeId: folderNodeId
        });
        //如果当前页面数据大于分页数据，则显示分页图标
        if (files.length > 10) {
            page.setData({ currentPageIcon: false });
        } else {
            page.setData({ currentPageIcon: true });
        }

        //判断要不要显示 空文件图标
        var isShowBlankPage = false;
        if (folders.length == 0 && files.length == 0) {
            isShowBlankPage = true;
        }

        page.setData({
            pageCount: pageCount,
            crumbs: crumbs,
            folders: folders,
            files: files,
            isShowBlankPage: isShowBlankPage
        });
        addPreviewImageUrls(page, files.previewImageUrls);
    })
}

function resolveCancelClick({ componentId }) {
    if (this.handleZanActionsheetCancel) {
        this.handleZanActionsheetCancel({ componentId });
    } else {
        console.warn('页面缺少 handleZanActionsheetCancel 回调函数');
    }
}

// 需要在元素上声明 data-component-id
function extractComponentId(event = {}) {
    const { dataset: { componentId } } = event.currentTarget || {};
    return componentId;
}

//初始化分页参数
function initPagingData(page) {
    page.setData({
        currPageCount: 1,  //当前的分页是第几页
        filesCount: 0,//分页：文件列表：记录 offset的默认值
    });
}

//将预览图片列表放到全局变量
function addPreviewImageUrls(page, data) {
    page.setData({
        previewImageUrls: data  //搜索 将分页图片预览链接覆盖
    })
}
