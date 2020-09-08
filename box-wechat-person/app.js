var session = require("./session.js");

App({
  globalData:{
    token:'', //
    enterpriseId: 0, //当前企业ID
    enterpriseName:'',//当前登录的企业名称
    userId: '', //当前登录用户ID
    userName: '',   //用户名字
    acountType: '',  //账号类型
    cloudUserId: '', //当前登录用户的CloudUserId
    expire: 0, //会话到期时间
    refreshToken: '',//
    enterpriseList: [], //多个企业时，选择列表
    IMAccount:'', //IM账号
    IMToken:'',  //im用户token
    avatarUrl:'',   //微信头像
    imgUrls: [],   //目录下图片数组
    musicList:'',   //音乐列表
    musicIndex: -1,   //当前音乐播放索引
    innerAudioContext:'',  //音乐播放器对象
    isShowMusicPanel:false,  //是否显示音乐播放器
    indexParam:1,   // 1：最近浏览，2：我的分享，3：他人分享
    isOpenRobotCheck:false, //  false：未启动巡检，true：已经启动巡检
    getOpenRobotData:{}, // 获取机器人数据
    isOpenRobot: false,  //  false：未启动，true：已经启动
    isShowChatButton: false,   //是否显示聊天菜单按钮
    isShowNetException: false,  //重复出现网络异常
    isJumpEenterpriseListPage: true,    //跳转页面
    tempFiles:[],       //上传图片临时路径集合
    tempFile:{},        //上传视频临时路径
    crumbs: [],         //记录选择的面包屑
    isAdmin: false,     //是否为企业管理员  0： false, 1: true
    deptCrumbs: [],     //部门面包屑
    checkedList: [],      //选中的文件列表
    systemName:'文件宝-',
    windowHeight:wx.getSystemInfoSync().windowHeight,
    windowWidth: wx.getSystemInfoSync().windowWidth,
  },
  onLaunch: function (options) {
  },
  onShow: function () {
    console.log('App Show');
  },
  onHide: function () {
    console.log('App Hide');
  },
  onError: function (msg) {
    console.log(msg)
  }
});
