var utils = require("utils.js");

var NORMAL_STATE = 0;   //音乐列表文件正常状态
var PLAY_STATE = 1;     //音乐列表文件播放状态
var STOP_STATE = 2;     //音乐列表文件暂停状态
var BAD_STATE = 3;      //音乐列表文件错误状态

var DELETE_STATE = 4;   //删除文件状态
var CLEAR_STATE = 5;    //清楚文件列表

var PLAY = "play"       //播放按钮
var STOP = "stop"       //暂停按钮

function musicListInit(){
    var musics = getMusicListByStorage();
    if (typeof (musics) == 'undefined' || musics == '' || musics.length == 0){
        return;
    }
    getApp().globalData.musicList = musics;
    for (var i = 0; i < musics.length; i++) {
        var music = musics[i];
        if (PLAY_STATE == music.state || STOP_STATE == music.state){
            getApp().globalData.musicIndex = i;
        }
    }
}

//将MP3文件放到本地缓存
function addMusicToStorage(ownerId, inodeId, fileName, state) {
    var currentMusic = {};
    currentMusic.ownerId = ownerId;
    currentMusic.inodeId = inodeId;
    currentMusic.fileName = fileName;
    currentMusic.state = state;
    //获取本地缓存音乐列表
    var musics = getMusicListByStorage();
    if (typeof (musics) == 'undefined' || musics == "") {
        musics = [];
    }else{
        for (var i = 0; i < musics.length; i++) {
            var music = musics[i];
            if (music.ownerId == ownerId && music.inodeId == inodeId) {
                return;     //已经存在列表中，就不再重复添加
            }
        }
    }
    musics.push(currentMusic);

    if (PLAY_STATE == state) {
        getApp().globalData.musicIndex = musics.length -1;
    }

    setMusicListToStorage(musics);
    //更新播放列表
    getApp().globalData.musicList = musics;
}

function setMusicState(ownerId, inodeId, state){
    var musics = getMusicListByStorage();
    if (typeof (musics) == 'undefined' || musics == "" || musics.length == 0) {
        return;
    }
    for (var i = 0; i < musics.length; i++){
        var music = musics[i];
        //设置之前音乐文件为播放状态，改为等待播放状态
        if (PLAY_STATE == state && PLAY_STATE == music.state){
            musics[i].state = NORMAL_STATE;
        }
        //找到要设置状态的音乐文件，设置状态
        if (music.ownerId == ownerId && music.inodeId == inodeId){
            music.state = state;
            musics[i] = music;
            //当要设置为播放状态时，将播放索引修改
            if (PLAY_STATE == state) {
                getApp().globalData.musicIndex = i;
            }
        }
    }

    setMusicListToStorage(musics);
    //更新播放列表
    getApp().globalData.musicList = musics;
}

function deleteMusic(ownerId, inodeId) {
    var musics = getMusicListByStorage();
    if (typeof (musics) == 'undefined' || musics == "" || musics.length == 0) {
        return;
    }
    for (var i = 0; i < musics.length; i++) {
        var music = musics[i];
        //找到要设置状态的音乐文件，设置状态
        if (music.ownerId == ownerId && music.inodeId == inodeId) {
            musics.splice(i, 1);
        }
    }

    setMusicListToStorage(musics);
    //更新播放列表
    getApp().globalData.musicList = musics;
}

function clearMusicList() {
    var musics = []

    setMusicListToStorage(musics);
    //更新播放列表
    getApp().globalData.musicList = musics;
}

function getMusicListByStorage(){
    var key = getApp().globalData.enterpriseId + "_" + getApp().globalData.cloudUserId;
    return wx.getStorageSync(key);
}

function setMusicListToStorage(musics){
    var key = getApp().globalData.enterpriseId + "_" + getApp().globalData.cloudUserId;
    wx.setStorageSync(key, musics);
}

function getMusicDate(time){
    if (typeof (time) == 'undefined' || time == "" || time == 0){
        return "00:00";
    }
    var min = parseInt(time / 60);
    var mm = parseInt(time % 60);
    if(min < 10){
        min = "0" + min;
    }
    if(mm < 10){
        mm = "0" + mm;
    }
    return min + ":" + mm;
}

module.exports = {
    NORMAL_STATE,
    PLAY_STATE,
    STOP_STATE,
    BAD_STATE,
    DELETE_STATE,
    CLEAR_STATE,
    PLAY,
    STOP,
    musicListInit,
    addMusicToStorage,
    setMusicState,
    getMusicDate,
    getMusicListByStorage,
    setMusicListToStorage,
    deleteMusic,
    clearMusicList
}