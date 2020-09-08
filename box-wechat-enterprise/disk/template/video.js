var isAutoCloseMusic = false;   //记录后台是否播放音乐

/**
 * 初始化视频播放
 * path: 播放视频地址
 * page： 播放页面
 * isFullScreen: 是否全屏播放
 */
function videoInit(path, page, isFullScreen) {
    var res = wx.getSystemInfoSync();
    var isCache = false;
    if (res.platform == 'ios') {
        isCache = true;
    }
    page.setData({
        isCache: isCache
    });

    isAutoCloseMusic = false;
    if (getApp().globalData.innerAudioContext != undefined && getApp().globalData.innerAudioContext != '') {
        var innerAudioContext = getApp().globalData.innerAudioContext;
        var isStop = innerAudioContext.paused;
        if (!isStop) {
            isAutoCloseMusic = true;
            innerAudioContext.pause();
        }
    }

    page.setData({
        path: decodeURIComponent(path),
        directionSize: 0
    });

    var videoContext = wx.createVideoContext('video');
    if (isFullScreen == undefined || isFullScreen == true){
        videoContext.requestFullScreen();
    }

    setDirection(videoContext, page);
}

//根据重力旋转播放屏幕
function setDirection(videoContext, page) {
    // 0为竖屏，1为横屏
    let lastState = 0;
    let lastTime = Date.now();

    wx.startAccelerometer();

    wx.onAccelerometerChange((res) => {
        const now = Date.now();

        // 500ms检测一次
        if (now - lastTime < 500) {
            return;
        }
        lastTime = now;

        let nowState;

        // 57.3 = 180 / Math.PI
        const Roll = Math.atan2(-res.x, Math.sqrt(res.y * res.y + res.z * res.z)) * 57.3;
        const Pitch = Math.atan2(res.y, res.z) * 57.3;

        // 横屏状态
        if (Roll > 50) {
            if ((Pitch > -180 && Pitch < -60) || (Pitch > 130)) {
                nowState = 1;
            } else {
                nowState = lastState;
            }

        } else if ((Roll > 0 && Roll < 30) || (Roll < 0 && Roll > -30)) {
            let absPitch = Math.abs(Pitch);

            // 如果手机平躺，保持原状态不变，40容错率
            if ((absPitch > 140 || absPitch < 40)) {
                nowState = lastState;
            } else if (Pitch < 0) { /*收集竖向正立的情况*/
                nowState = 0;
            } else {
                nowState = lastState;
            }
        } else {
            nowState = lastState;
        }

        // 状态变化时，触发
        if (nowState !== lastState) {
            lastState = nowState;
            if (nowState === 0) {
                page.setData({
                    directionSize: 0
                })
            } else {
                page.setData({
                    directionSize: 90
                })
            }
            setTimeout(function () {
                videoContext.exitFullScreen();
                videoContext.requestFullScreen();
            }, 200);
        }
    });
}
//在页面onUnload方法中，添加该方法，播放视频前如果播放音乐，退出视频播放则继续播放
function handlePlayMusic() {
    if (isAutoCloseMusic) {
        var innerAudioContext = getApp().globalData.innerAudioContext;
        innerAudioContext.play();
    }
}

module.exports = {
    videoInit,
    handlePlayMusic
};