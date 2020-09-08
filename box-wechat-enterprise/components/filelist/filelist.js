
// 滑动的起始坐标
var startX = 0;
var startY = 0;

Component({
    properties: {
        dataList: {
            type: Array,
            value: [{
                name: 123
            }]
        },
        // 表示当前是第几个左滑项
        isTouchMoveIndex: {
            type: Number,
            value: -1
        },
        // 传入一个标志，在同一个页面多次使用时，可以进行比对，最好命名为需要使用内容的名字
        symbol: {
            type: String,
            value: ''
        },
        // 与页面初始传入的symbol进行比对
        checkSymbol: {
            type: String,
            value: -1
        },
        showShareBtn: {
            type: Boolean,
            value: false
        },
        showModalBtn: {
            type: Boolean,
            value: false
        },
        isLoadMore: {
            type: Boolean,
            value: false
        },
        hasNoMoreData: {
            type: Boolean,
            value: false
        }
    },
    methods: {
        cantShare: function () {
            wx.showModal({
                title: '提示',
                content: '该文件夹不能分享',
                complete: () => {
                    return;
                }
            });

        },
        // 唤起发送弹出层
        setModalStatus: function (e) {
            var page = this;
            var folderItem = e.currentTarget.dataset.folderItem;
            var linkCode = '';

            if (folderItem != undefined) {
                page.setData({
                    folderItem: folderItem,
                });
            }

            var animation = wx.createAnimation({
                duration: 200,
                timingFunction: "linear",
                delay: 0
            });
            this.animation = animation
            animation.translateY(300).step()
            this.setData({
                animationData: animation.export()
            });
            this.setData({
                showModalStatus: true
            });
            setTimeout(function () {
                animation.translateY(0).step()
                this.setData({
                    animationData: animation
                });
            }.bind(this), 200);
        },
        cancelModalStatus: function () {
            this.setData({
                showModalStatus: false
            });
        },
        _touchstart: function (e) {
            // 起始位置
            startX = e.changedTouches[0].clientX;
            startY = e.changedTouches[0].clientY;

            this.triggerEvent('getSymbol', e); //触发绑定事件
            this.setData({
                isTouchMoveIndex: -1,
            });
        },
        _touchend: function (e) {
            var index = e.currentTarget.dataset.index;//当前索引
            var symbol = e.currentTarget.dataset.symbol;
            var touchEndX = e.changedTouches[0].clientX;//滑动终止x坐标
            var touchEndY = e.changedTouches[0].clientY;//滑动终止y坐标

            //获取滑动角度，Math.atan()返回数字的反正切值
            var angle = 360 * Math.atan((touchEndY - startY) / (touchEndX - startX)) / (2 * Math.PI);

            //滑动超过30度角 return
            if (Math.abs(angle) > 30) return;

            // 滑动距离过小即是点击进入文件夹
            if (((touchEndX - startX) > -50) && ((touchEndX - startX) < 50)) {
                // this.fileItemClick(e);
                this.triggerEvent('fileItemClick', e);
                return;
            }

            if (touchEndX > startX) { //向右滑
                index = -1
            }
            this.setData({
                isTouchMoveIndex: index,
                checkSymbol: symbol
            })
        },
        _deleteBrowseRecords: function (e) {
            this.triggerEvent('deleteEvent', e)
        },
    }
})