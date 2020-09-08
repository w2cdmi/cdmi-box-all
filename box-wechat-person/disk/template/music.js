var File = require("../module/file.js");
var musicService = require("../module/music.js");

function playOrStopMusic(){
    var page = this;
    var innerAudioContext = getApp().globalData.innerAudioContext;
    var musicList = getApp().globalData.musicList;
    var index = getApp().globalData.musicIndex;
    if (musicList == "" || musicList.length == 0) {
        initPlayPanel(page);
        wx.showToast({
            title: '音乐列表中没有歌曲',
            icon: 'none'
        });
        return;
    } else if (index == -1) {
        //默认重第一首播放
        index = 0;
        var music = musicList[index];
        musicService.setMusicState(musicService.PLAY_STATE);
    }
    var currentPlayMusic = musicList[index];
    var isStop = innerAudioContext.paused;
    if (isStop) {
        musicService.setMusicState(currentPlayMusic.ownerId, currentPlayMusic.inodeId, musicService.PLAY_STATE);
        innerAudioContext.play();
        page.setData({
            playOrStopButton: musicService.STOP
        });
    } else {
        musicService.setMusicState(currentPlayMusic.ownerId, currentPlayMusic.inodeId, musicService.STOP_STATE)
        innerAudioContext.pause();
        page.setData({
            playOrStopButton: musicService.PLAY
        });
    }
}

function lastMusicPlay() {
    var page = this;
    var index = getApp().globalData.musicIndex - 1; //上一首
    var musics = getApp().globalData.musicList;
    if (index >= 0) {
        var music = musics[index];
        musicService.setMusicState(music.ownerId, music.inodeId, musicService.PLAY_STATE);
        File.playMusic(music.ownerId, music.inodeId, music.fileName, function (musicState) {
            refreshPage(musicState.state, page);
        });
    } else {
        wx.showToast({
            title: '没有上一首',
            icon: 'none'
        })
    }
}

function nextMusicPlay() {
    var page = this;
    var index = getApp().globalData.musicIndex + 1; //下一首
    var musics = getApp().globalData.musicList;
    if (musics.length > index) {
        var music = musics[index];
        musicService.setMusicState(music.ownerId, music.inodeId, musicService.PLAY_STATE);
        File.playMusic(music.ownerId, music.inodeId, music.fileName, function (musicState) {
            refreshPage(musicState.state, page);
        });
    } else {
        wx.showToast({
            title: '没有下一首',
            icon: 'none'
        })
    }
}

function playCurrentMusic(e) {
    var page = this;
    var music = e.currentTarget.dataset.music;
    musicService.setMusicState(music.ownerId, music.inodeId, musicService.PLAY_STATE);
    File.playMusic(music.ownerId, music.inodeId, music.fileName, function (musicState) {
        refreshPage(musicState.state, page);
    });
}

function deleteMusic(e) {
    var music = e.currentTarget.dataset.music;
    var page = this;
    var index = music.id - 1;
    //如果删除的当前播放歌曲，则先播放下一首
    if (index == getApp().globalData.musicIndex) {
        var musics = getApp().globalData.musicList;
        var innerAudioContext = getApp().globalData.innerAudioContext;
        //原本只有一首歌，删除则没有了，不需要播放下一首
        if (typeof (musics) != 'undefined' && musics != '' && musics.length > 0) {
            if (musics.length == 1) {
                if (innerAudioContext != '') {
                    innerAudioContext.stop();
                }
            } else {
                page.nextMusicPlay();
            }
        }
    }
    //删除本地缓存
    musicService.deleteMusic(music.ownerId, music.inodeId);
    //当删除的索引小于正在播放歌曲的索引，则更新播放的索引
    if (index < getApp().globalData.musicIndex) {
        getApp().globalData.musicIndex = getApp().globalData.musicIndex - 1;
    }
    refreshPage(musicService.DELETE_STATE, page);
}

function clearMusicList(e) {
    var page = this;
    wx.showModal({
        title: '提示',
        content: '确认清除播放列表吗？',
        success: function (res) {
            if (res.confirm) {
                var innerAudioContext = getApp().globalData.innerAudioContext;
                if (typeof (innerAudioContext) == 'undefined' || innerAudioContext == "") {
                    return;
                }
                innerAudioContext.stop();
                musicService.clearMusicList();
                refreshPage(musicService.CLEAR_STATE, page);
            }
        }
    })
}

function openMusicList() {
    this.setData({
        isShowMusicListPanel: true
    });
}

function closeMusicList() {
    this.setData({
        isShowMusicListPanel: false
    });
}


function musicPlayInit(page) {
    wx.getSystemInfo({
        success: function (res) {
            var musicInfoWidth = res.windowWidth - 170;
            var progressWidth = musicInfoWidth - 62;
            page.setData({
                isShowMusicPanel: getApp().globalData.isShowMusicPanel,
                isShowMusicListPanel: false,
                musicInfoWidth: musicInfoWidth,
                progressWidth: progressWidth
            });
            initPlayPanel(page);
        },
    })
    //本地缓存加载
    musicService.musicListInit();
    //获取播放列表
    var musicList = getApp().globalData.musicList;
    //判断当前list
    if (musicList == '' || musicList.length == 0 || getApp().globalData.musicIndex == -1) {
        getApp().globalData.isShowMusicPanel = false;
        page.setData({
            isShowMusicPanel: getApp().globalData.isShowMusicPanel
        });
    } else {
        var innerAudioContext = getApp().globalData.innerAudioContext;
        //首次进入列表文件
        if (innerAudioContext == "") {
            innerAudioContext = wx.createInnerAudioContext();
            getApp().globalData.innerAudioContext = innerAudioContext;

            var music = musicList[getApp().globalData.musicIndex];
            File.playMusic(music.ownerId, music.inodeId, music.fileName, function (musicState) {
                if (musicService.STOP_STATE == music.state) {
                    innerAudioContext.pause();
                    refreshPage(music.state, page);
                } else {
                    refreshPage(musicState.state, page);
                }
                listenPlayState(page);
            });
            return;
        }

        listenPlayState(page);
        var isStop = innerAudioContext.paused;
        if (isStop) {
            refreshPage(musicService.STOP_STATE, page);
        } else {
            refreshPage(musicService.PLAY_STATE, page);
        }
    }

}

//音乐界面模板js
function refreshPage(state, page) {
    var musics = getApp().globalData.musicList;
    if (typeof (musics) == 'undefined' || musics.length == 0) {
        initPlayPanel(page);
        page.closeMusicList();
        getApp().globalData.isShowMusicPanel = false;
        page.setData({
            isShowMusicPanel: getApp().globalData.isShowMusicPanel
        });
        return;
    }
    for (var i = 0; i < musics.length; i++) {
        var music = musics[i];
        music.id = i + 1;
        musics[i] = music;
        if (i == getApp().globalData.musicIndex) {
            music.active = true;
        }
    }

    page.setData({
        musicCount: musics.length,
        musics: musics
    });

    var music = musics[getApp().globalData.musicIndex];
    if (musicService.PLAY_STATE == state) {
        page.setData({
            index: getApp().globalData.musicIndex + 1,
            music: music,
            playOrStopButton: musicService.STOP,
        });
    } else if (musicService.STOP_STATE == state) {
        page.setData({
            index: getApp().globalData.musicIndex + 1,
            music: music,
            playOrStopButton: musicService.PLAY,
        });
    } else if (musicService.NORMAL_STATE == state) {
        wx.showToast({
            title: '添加成功',
            icon: 'none'
        })
    } else if (musicService.DELETE_STATE == state) {
        wx.showToast({
            title: '删除成功',
            icon: 'none'
        })
    } else if (musicService.CLEAR_STATE == state) {
        wx.showToast({
            title: '清除成功',
            icon: 'none',
        });
        var music = {};
        page.setData({
            playOrStopButton: musicService.PLAY,
            index: -1,
            music: music
        });
    }

}

function listenPlayState(page) {
    var innerAudioContext = getApp().globalData.innerAudioContext;
    if (innerAudioContext == "") {
        innerAudioContext = wx.createInnerAudioContext();
        getApp().globalData.innerAudioContext = innerAudioContext;
    }

    innerAudioContext.onPlay((res) => {
        page.setData({
            playOrStopButton: musicService.STOP,
        });
    });
    innerAudioContext.onCanplay((res) => {
        page.setData({
            currentTime: musicService.getMusicDate(innerAudioContext.currentTime),
            duration: musicService.getMusicDate(innerAudioContext.duration),
            progress: 0
        });
    })
    innerAudioContext.onError((res) => {
        page.nextMusicPlay();
    })
    innerAudioContext.onTimeUpdate((res) => {
        page.setData({
            currentTime: musicService.getMusicDate(innerAudioContext.currentTime),
            duration: musicService.getMusicDate(innerAudioContext.duration),
            progress: (innerAudioContext.currentTime / innerAudioContext.duration) * 100
        });
    })
    innerAudioContext.onEnded((res) => {
        console.log("当前音乐播放完毕");
        var index = getApp().globalData.musicIndex + 1; //下一首
        var musics = getApp().globalData.musicList;
        if (musics.length > index) {
            var music = musics[index];
            File.playMusic(music.ownerId, music.inodeId, music.fileName, function (musicState) {
                refreshPage(musicState.state, page);
                if (musicState.state == musicService.PLAY_STATE) {
                    listenPlayState(page);
                }
            });
        } else {
            //重新播放
            var music = musics[0];
            File.playMusic(music.ownerId, music.inodeId, music.fileName, function (musicState) {
                refreshPage(musicState.state, page);
                if (musicState.state == musicService.PLAY_STATE) {
                    listenPlayState(page);
                }
            });
        }
    })
}

function initPlayPanel(page) {
    page.setData({
        musicCount: 0,
        musics: [],
        music: {},
        currentTime: "00:00",
        duration: "00:00",
        progress: 0,
        playOrStopButton: musicService.PLAY
    });
}

module.exports = {
    playOrStopMusic,
    lastMusicPlay,
    nextMusicPlay,
    playCurrentMusic,
    deleteMusic,
    clearMusicList,
    openMusicList,
    closeMusicList,
    refreshPage,
    musicPlayInit,
    listenPlayState
};