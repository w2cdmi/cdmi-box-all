var objectUtils = require("../../disk/module/objectUtils.js");

Component({
    options: {
        multipleSlots: true // 在组件定义时的选项中启用多slot支持
    },
    properties: {
        isShowMenu: {
            type: String,
            value: false
        }
    },
    data: {
        isShowMenu: false
    },
    methods: {
        onHideMenu: function (e) {
            this.setData({
                isShowMenu: false
            });
        },
        chooseUploadImage: function (e) {
            var page = this;
            this.onHideMenu();

            wx.chooseImage({
                success: function (res) {
                    var tempFiles = res.tempFiles;

                    if (typeof (tempFiles) == 'undefined' || tempFiles.length == 0) {
                        return;
                    }

                    page.triggerEvent("onUploadImage", tempFiles);
                }
            })
        },
        chooseUploadVedio: function () {
            var page = this;
            this.onHideMenu();

            wx.chooseVideo({
                sourceType: ['album', 'camera'],
                compressed: true,
                maxDuration: 60,
                camera: 'back',
                success: function (res) {
                    var tempFile = res;

                    page.triggerEvent("onUploadVideo", tempFile);
                }
            })
        },
        jumpForwardPage: function() {
            var page = this;
            this.onHideMenu();

            wx.navigateTo({
                url: '/disk/batchShare/batchShare',
            })
        },
        showCreateFolder: function() {
            var page = this;
            this.onHideMenu();

            page.triggerEvent("showCreateFolder");
        }
    }
})