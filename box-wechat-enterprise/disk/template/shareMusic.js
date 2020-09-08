// disk/template/shareMusic.js
var musicService = require("../module/music.js");
var utils = require("../module/utils.js");

var musicSize = 0;

Page({

    /**
     * 页面的初始数据
     */
    data: {
        url: "",        //音频播放地址
        playButtonImg: "../images/shareMusic/stop.png",
        musicSize: "0MB",
        musicTime: "00:00",
        playorpause: "play",
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        var page = this;
        page.data.url = decodeURIComponent(options.url);
        if (page.data.url == 'undefined'){
            return;
        }
        musicSize = utils.formatFileSize(options.size);
        var fileName = options.name;

        //获取音乐的大小和名字
        page.setData({
            musicName: fileName,
            musicSize: musicSize
        });
        const backgroundAudioManager = wx.getBackgroundAudioManager(); //实例化一个接口

        backgroundAudioManager.title = fileName;
        backgroundAudioManager.src = page.data.url;
        backgroundAudioManager.onPlay((res) => {
            //用微信接口获取音乐当前播放的时间
            var currentTime = backgroundAudioManager.currentTime;
            backgroundAudioManager.onTimeUpdate((res) => {
                var currentTime = backgroundAudioManager.currentTime;
                var musicTime = musicService.getMusicDate(currentTime);
                page.setData({
                    musicTime: musicTime
                });
            })
            setTimeout(function(){
                var musicTimes = backgroundAudioManager.duration;
                turntableStartAnimation(page, musicTimes);
                poleStartAnimation(page);
            },300);
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
        if(page.data.url != 'undefined'){
            return;
        }
        const backgroundAudioManager = wx.getBackgroundAudioManager();
        page.setData({
            musicName: backgroundAudioManager.title,
            musicSize: musicSize
        });
        backgroundAudioManager.onTimeUpdate((res) => {
            var currentTime = backgroundAudioManager.currentTime;
            var musicTime = musicService.getMusicDate(currentTime);
            page.setData({
                musicTime: musicTime
            });
        })
        setTimeout(function () {
            var musicTimes = backgroundAudioManager.duration;
            turntableStartAnimation(page, musicTimes);
            poleStartAnimation(page);
        }, 300);
    },

    /**
     * 生命周期函数--监听页面隐藏
     */
    onHide: function () {
        // const backgroundAudioManager = wx.getBackgroundAudioManager();
        // backgroundAudioManager.stop();
    },

    /**
     * 生命周期函数--监听页面卸载
     */
    onUnload: function () {
        // const backgroundAudioManager = wx.getBackgroundAudioManager();
        // backgroundAudioManager.stop();
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

    },

    playVoice: function (e) {
        var page = this;
        const backgroundAudioManager = wx.getBackgroundAudioManager()
        // 设置了 src 之后会自动播放
        var status = e.currentTarget.dataset.playorpause;
        if (status == "pause") {
            this.setData({
                playorpause: "play",
                playButtonImg: "../images/shareMusic/stop.png",
            })
            backgroundAudioManager.play();
        } else {
            this.setData({
                playorpause: "pause",
                playButtonImg: "../images/shareMusic/play.png",
            })
            backgroundAudioManager.pause();
            turntableStopAnimation(page);
            poleStopAnimation(page);
        }
    }
})

function turntableStartAnimation(page, musicTimes) {
    if (musicTimes == undefined || musicTimes == 0){
        musicTimes = 10;
    }
    musicTimes = musicTimes * 1000;
    var rotate = musicTimes/50;
    var turntable = wx.createAnimation({
        duration: musicTimes,
        timingFunction: 'linear',
    })
    turntable.rotate(rotate).step();
    page.setData({
        turntable: turntable.export()
    });
}

function turntableStopAnimation(page) {
    var turntable = wx.createAnimation({
        duration: 500,
        timingFunction: 'linear',
    })
    turntable.step();
    page.setData({
        turntable: turntable.export()
    });
}

function poleStartAnimation(page) {
    var pole = wx.createAnimation({
        duration: 500,
        timingFunction: 'linear',
    })
    pole.rotate(8).step();
    page.setData({
        pole: pole.export()
    });
}

function poleStopAnimation(page) {
    var pole = wx.createAnimation({
        duration: 500,
        timingFunction: 'linear',
    })
    pole.rotate(0).step();
    page.setData({
        pole: pole.export()
    });
}