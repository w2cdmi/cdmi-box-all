var video = require("../template/video.js");

Page({
    data: {
        path: '',    //视频播放路径
    },
    onLoad: function (options) {
        var page = this;
        var path = options.path;

        video.videoInit(path, page);
    },
    onReady: function (res) {

    },
    onHide: function () {
        
    },
    onUnload: function(){
        video.handlePlayMusic();
    }
})