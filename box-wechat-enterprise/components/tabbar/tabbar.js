var objectUtils = require("../../disk/module/objectUtils.js");

Component({
    options: {
        multipleSlots: true // 在组件定义时的选项中启用多slot支持
    },
    /**
     * 组件的属性列表
     * 用于组件自定义设置
     */
    properties: {
        selectIndex: {
            type: String,
            value: 0
        },
        mCount: {
            type: String,
            value: 0
        }
    },

    /**
     * 私有数据,组件的初始数据
     * 可用于模版渲染
     */
    data: {
        tabbars: [
            {
                "selectedIconPath": "/disk/images/icon/index-active.png",
                "iconPath": "/disk/images/icon/index.png",
                "pagePath": "/disk/index",
                "text": "快捷"
            }, {
                "selectedIconPath": "/disk/images/icon/tabbar-share-active.png",
                "iconPath": "/disk/images/icon/tabbar-share.png",
                "pagePath": "/disk/library/index",
                "text": "文库"
            }, {
                "selectedIconPath": "/disk/images/icon/add-active.png",
                "iconPath": "/disk/images/icon/add.png",
                "pagePath": ""
            }, {
                "selectedIconPath": "/disk/images/icon/tabbar-find-active.png",
                "iconPath": "/disk/images/icon/tabbar-find.png",
                "pagePath": "/disk/find/find",
                "text": "发现"
            }, {
                "selectedIconPath": "/disk/images/icon/me-active.png",
                "iconPath": "/disk/images/icon/me.png",
                "pagePath": "/disk/me/me",
                "text": "我的"
            }
        ]
    },

    /**
     * 组件的方法列表
     * 更新属性和数据的方法与更新页面数据的方法类似
     */
    methods: {
        onTabItem: function (e) {
            var index = e.currentTarget.dataset.index;
            var item = this.data.tabbars[index];
            if (objectUtils.isEmpty(item)) {
                return;
            }
            if (item.pagePath == "") {
                this.triggerEvent("onShowMenu");
                return;
            }
            wx.reLaunch({
                url: item.pagePath
            })
        },

    }
})