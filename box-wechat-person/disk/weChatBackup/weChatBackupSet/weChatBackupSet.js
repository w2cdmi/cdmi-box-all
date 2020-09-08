var robot = require("../../module/robot.js")
Page({

    /**
     * 页面的初始数据
     */
    data: {
        wechatBackupDataChoose: "",
        isShowBackupGroup: false,
        isShowDataType: false,
        listGroupsNameData: [], //当前活跃群组数据
        perDataType: [
            { name: "图片" },
            { name: "视频" },
            { name: "文件" }
        ],
        groupDataType: [
            { name: "图片" },
            { name: "视频" },
            { name: "文件" }
        ]
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        var page = this;
        page.setData({
            wechatBackupDataChoose: "chooseBackupGroup",
            isShowBackupGroup: false,
            isShowDataType: true
        })
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
        var newPerDataType = page.data.perDataType;
        var newGroupDataType = page.data.groupDataType;
        listGroupsName(page);
        var personDataConfig = wx.getStorageSync("personDataConfig")[0].config
        var groupDataConfig = wx.getStorageSync("groupDataConfig")[0].config
        if (personDataConfig.image) {
            newPerDataType[0].checked = true;
            newPerDataType[0].value = 1
        } else {
            newPerDataType[0].checked = false;
            newPerDataType[0].value = 0
        }

        if (personDataConfig.video) {
            newPerDataType[1].checked = true;
            newPerDataType[1].value = 1
        } else {
            newPerDataType[1].checked = false;
            newPerDataType[1].value = 0
        }

        if (personDataConfig.file) {
            newPerDataType[2].checked = true
            newPerDataType[2].value = 1
        } else {
            newPerDataType[2].checked = false;
            newPerDataType[2].value = 0
        }

        if (groupDataConfig.image) {
            newGroupDataType[0].checked = true
            newGroupDataType[0].value = 1
        } else {
            newGroupDataType[0].checked = false
            newGroupDataType[0].value = 0
        }

        if (groupDataConfig.video) {
            newGroupDataType[1].checked = true
            newGroupDataType[1].value = 1
        } else {
            newGroupDataType[1].checked = false
            newGroupDataType[1].value = 0
        }

        if (groupDataConfig.file) {
            newGroupDataType[2].checked = true
            newGroupDataType[2].value = 1
        } else {
            newGroupDataType[2].checked = false
            newGroupDataType[2].value = 0
        }

        page.setData({
            perDataType: newPerDataType,
            groupDataType: newGroupDataType
        })
    },
    //tab切换事件
    clickTab: function (e) {
        var page = this;
        var wechatGroupDataType = e.target.dataset.type;
        if (wechatGroupDataType == "chooseBackupGroup") {
            page.setData({
                isShowBackupGroup: false,
                isShowDataType: true,
                wechatBackupDataChoose: "chooseBackupGroup"
            })
        } else if (wechatGroupDataType == "chooseDataType") {
            page.setData({
                isShowBackupGroup: true,
                isShowDataType: false,
                wechatBackupDataChoose: "chooseDataType"
            })
        }
    },
    // 选择备份群check事件
    listenCheckboxChange: function (e) {
        var wxGroupName = e.detail.value
        wx.setStorageSync("addWxGroupData", wxGroupName)
    },
    // 选择个人数据类型check事件
    listenPerDataCheckChange: function (e) {
        var page = this;
        var value = "";
        var config = {};
        var newPerDataType = page.data.perDataType;
        var currentTargetItem = e.currentTarget.dataset.item;
        if (currentTargetItem.checked == false && currentTargetItem.name == "图片") {
            newPerDataType[0].checked = true;
            newPerDataType[0].value = 1
        } else if (currentTargetItem.checked == true && currentTargetItem.name == "图片") {
            newPerDataType[0].checked = false
            newPerDataType[0].value = 0
        }
        if (currentTargetItem.checked == false && currentTargetItem.name == "视频") {
            newPerDataType[1].checked = true
            newPerDataType[1].value = 1
        } else if (currentTargetItem.checked == true && currentTargetItem.name == "视频") {
            newPerDataType[1].checked = false
            newPerDataType[1].value = 0
        }
        if (currentTargetItem.checked == false && currentTargetItem.name == "文件") {
            newPerDataType[2].checked = true
            newPerDataType[2].value = 1
        } else if (currentTargetItem.checked == true && currentTargetItem.name == "文件") {
            newPerDataType[2].checked = false
            newPerDataType[2].value = 0
        }
        console.log(newPerDataType)
        page.setData({
            perDataType: newPerDataType,
        })
        for (let i = 0; i < newPerDataType.length; i++) {
            config.image = newPerDataType[0].value
            config.video = newPerDataType[1].value
            config.file = newPerDataType[2].value
        }
        console.log(config)
        updateWxRobotConfig(config, 1, page)
    },
    //选择群组聊天数据类型
    listenGroupDataCheckChange: function (e) {
        var page = this;
        var value = "";
        var config = {};
        var newGroupDataType = page.data.groupDataType;
        var currentTargetItem = e.currentTarget.dataset.item;
        if (currentTargetItem.checked == false && currentTargetItem.name == "图片") {
            newGroupDataType[0].checked = true;
            newGroupDataType[0].value = 1
        } else if (currentTargetItem.checked == true && currentTargetItem.name == "图片") {
            newGroupDataType[0].checked = false
            newGroupDataType[0].value = 0
        }
        if (currentTargetItem.checked == false && currentTargetItem.name == "视频") {
            newGroupDataType[1].checked = true
            newGroupDataType[1].value = 1
        } else if (currentTargetItem.checked == true && currentTargetItem.name == "视频") {
            newGroupDataType[1].checked = false
            newGroupDataType[1].value = 0
        }
        if (currentTargetItem.checked == false && currentTargetItem.name == "文件") {
            newGroupDataType[2].checked = true
            newGroupDataType[2].value = 1
        } else if (currentTargetItem.checked == true && currentTargetItem.name == "文件") {
            newGroupDataType[2].checked = false
            newGroupDataType[2].value = 0
        }
        page.setData({
            groupDataType: newGroupDataType
        })
        for (let i = 0; i < newGroupDataType.length; i++) {
            config.image = newGroupDataType[0].value
            config.video = newGroupDataType[1].value
            config.file = newGroupDataType[2].value
        }
        updateWxRobotConfig(config, 2, page)
    },
    //设置用户白名单确认事件
    confirmAddWxBackupGroup: function () {
        var page = this;
        addWhiteConfig(page)
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

    }
})
// 备份群组
function listGroupsName(page) {
    var getOpenRobotData = getApp().globalData.getOpenRobotData;
    var wxUin = getOpenRobotData.wxUin;
    robot.listGroupsName(wxUin, function (data) {
        var dataArry = [];
        var listRobotConfigData = wx.getStorageSync("listRobotConfigData")
        for (let i = 0; i < data.length; i++) {
            dataArry.push({ name: data[i], index: i })
        }
        for (let i = 0; i < dataArry.length; i++) {
            var backupGroupData = dataArry[i];
            var backupGroupDataName = dataArry[i].name;
            for (let j = 0; j < listRobotConfigData.length; j++) {
                var activeConfigData = listRobotConfigData[j];
                var activeConfigDataName = listRobotConfigData[j].name;
                if (backupGroupDataName == activeConfigDataName) {
                    backupGroupData.checked = true;
                    break;
                }
            }
        }
        page.setData({
            listGroupsNameData: dataArry
        })
    })
}
// 设置用户白名单
function addWhiteConfig(page) {
    var getOpenRobotData = getApp().globalData.getOpenRobotData;
    var addWxGroupData = wx.getStorageSync("addWxGroupData");
    var robotId = getOpenRobotData.id
    robot.addWhiteConfig(robotId, addWxGroupData, function (data) {
        wx.navigateBack({
            delta: 1
        })
    })
}

// 设置个人和群组数据类型
function updateWxRobotConfig(config, type, page) {
    var getOpenRobotData = getApp().globalData.getOpenRobotData;
    var robotId = getOpenRobotData.id
    wx.setStorageSync("isShowLoading", false)
    robot.updateWxRobotConfig(robotId, config, type, function (data) {

    })
}