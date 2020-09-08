var config = require("../config.js");
var httpclient = require("httpclient.js");
var utils = require("utils.js");
var musicServicce = require("music.js");

// 获取最新浏览的文件
function listLastReadFile(token, cloudUserId, callback) {
    var header = {
        Authorization: token
    };
    var params = {
        thumbnail: [{ width: 120, height: 120 }, { width: 500, height: 500 }]
    }
    httpclient.post(config.host + '/ufm/api/v2/folders/' + cloudUserId + "/recent", params, header, callback);
};
// 获取我分享出去的文件以及文件夹
function listMySharetTo(callback, offset = 0, limit = 10) {
    var header = {
        Authorization: getApp().globalData.token
    };
    var params = {
        limit: 10,//一次取出几个数据
        offset: offset,//从第几个开始取，第一个开始，就取取2.3个
        shareType: "link",
        order: [{field: 'modifiedAt', direction: "DESC" }],//排序,  TODO:暂时不要删除
        thumbnail: [{ width: 120, height: 120 }, { width: 500, height: 500 }],
    }
    return httpclient.post(config.host + '/ufm/api/v2/shares/distributed', params, header, callback);
};

// 获取他人分享给我的文件以及文件夹
function listShareToMe(callback, offset = 0, limit = 10) {
    var header = {
        Authorization: getApp().globalData.token
    };
    var params = {
        limit: 10, //一次取出几个数据
        order: [{ field: 'modifiedAt', direction: "DESC" }],
        offset: offset, //从第几个开始取，第一个开始，就取取2.3个
        shareType: "link",
        thumbnail: [{ width: 120, height: 120 }, { width: 500, height: 500 }],
    }
    return httpclient.post(config.host + '/ufm/api/v2/shares/received', params, header, callback);
};

// 获取我所拥有的部门文件列表
function listDeptSpace(token, cloudUserId, offset, limit, callback) {
    var header = {
        Authorization: token
    };
    var params = {
        limit: limit,
        offset: offset,
        'type': 1,
        userId: cloudUserId,
    }
    return httpclient.post(config.host + '/ufm/api/v2/teamspaces/items', params, header, callback);
};

// 获取我有权限访问的项目团队空间列表
function listTeamSpace(token, cloudUserId, offset, limit, callback) {
    var params = {
        limit: limit,
        offset: offset,
        'type': 0,
        userId: cloudUserId,
    }
    var header = {
        Authorization: token
    };
    return httpclient.post(config.host + '/ufm/api/v2/teamspaces/items', params, header, callback);
};

// 获取我有权限访问的企业文库空间列表
function listEnterpriseSpace(token, cloudUserId, offset, limit, callback) {
    var header = {
        Authorization: token,
    };
    var params = {
        limit: limit,
        offset: offset,
        'type': 4,
        userId: cloudUserId
    }
    return httpclient.post(config.host + '/ufm/api/v2/teamspaces/items', params, header, callback);
};
// 恢复文件
function trash(token, ownerId, nodeId, callback) {
    var header = {
        Authorization: token,
    };
    var params = {
        autoRename: true
    }
    return httpclient.put(config.host + '/ufm/api/v2/trash/' + ownerId + '/' + nodeId, params, header, callback);
};
// 回收站删除
function trashclean(token, nodeId, ownerId, callback) {
    var header = {
        Authorization: token,
    };
    var params = {
        autoRename: true
    }
    return httpclient.remove(config.host + '/ufm/api/v2/trash/' + nodeId + '/' + ownerId, params, header, callback);
};
// 获取成员列表
function listDepAndUsers(token, deptId, callback) {
    var header = {
        Authorization: token,
    };
    var params = {
        deptId: deptId
    }
    return httpclient.post(config.host + '/ecm/api/v2/users/listDepAndUsers', params, header, callback);
};
// 添加成员
function addmember(token, memberList, teamId, callback) {
    var header = {
        Authorization: token,
    };
    var params = {
        memberList: memberList,
        role: "uploadAndView",
        teamRole: "member"
    }
    return httpclient.post(config.host + '/ufm/api/v2/teamspaces/' + teamId + '/memberships', params, header, callback);
};
// 创建协作空间
function teamspaces(token, name, callback) {
    var header = {
        Authorization: token,
    };
    var params = {
        description: '',
        name: name
    }
    return httpclient.post(config.host + '/ufm/api/v2/teamspaces', params, header, callback);
};
// 协作空间成员管理
function memberteamlist(token, taemId, callback) {
    var header = {
        Authorization: token,
    };
    var params = {
        keyword: ''
    }
    return httpclient.post(config.host + '/ufm/api/v2/teamspaces/' + taemId + '/memberships/items', params, header, callback);
};

function deleteMember(userId, taemId, callback) {
    var header = {
        Authorization: getApp().globalData.token,
    };
    var params = [userId]
    return httpclient.remove(config.host + '/ufm/api/v2/teamspaces/' + taemId + '/memberships/batch', params, header, callback);
}

function deleteTeam(taemId, callback) {
    var header = {
        Authorization: getApp().globalData.token,
    };
    var params = {};
    return httpclient.remove(config.host + '/ufm/api/v2/teamspaces/' + taemId, params, header, callback);
}

//获取文件夹目录信息(ls命令)
function lsOfFolder(token, ownerId, nodeId, callback, offset = 0) {
    var header = {
        Authorization: token,
    };
    var params = {
        limit: 10,
        offset: offset,
        order: [{ field: "type", direction: "ASC" }, { field: "name", direction: "ASC" }],
        thumbnail: [{ width: 120, height: 120 }, { width: 500, height: 500 }, { width:200, height: 200}]
    };
    return httpclient.post(config.host + '/ufm/api/v2/folders/' + ownerId + '/' + nodeId + '/items', params, header, callback);
};
//[不分页]获取文件夹目录信息(ls命令)
function lsOfFolderNoPaging(token, ownerId, nodeId, callback) {
  var header = {
    Authorization: token,
  };
  var params = {
    limit: 100,
    offset: 0,
    order: [{ field: "type", direction: "ASC" }, { field: "name", direction: "ASC" }],
    thumbnail: [{ width: 120, height: 120 }, { width: 500, height: 500 }, { width: 200, height: 200 }]
  };
  return httpclient.post(config.host + '/ufm/api/v2/folders/' + ownerId + '/' + nodeId + '/items', params, header, callback);
};

//文件排序
function oderFiles(param, ownerId, nodeId, callback) {
    var params = {
        offset: 0,
        limit: 100,//没做分页，暂时写100
        order: [{ field: 'type', direction: "ASC" }, { field: param.field, direction: param.direction }],//param.direction
        thumbnail: [{ width: 96, height: 96 }]
    }
    var header = {
        Authorization: getApp().globalData.token,
    };
    httpclient.post(config.host + "/ufm/api/v2/folders/" + ownerId + "/" + nodeId + "/items", params, header, callback);
}
//搜索框
function searchFiles(searchKeyWord,ownerId, callback) {
    var params = {
        name: searchKeyWord,
        order: [{ field: 'modifiedAt', direction: "DESC" }],
        thumbnail: [{ width: 96, height: 96 }]
    };
    var header = {
        Authorization: getApp().globalData.token,
    };
    httpclient.post(config.host + "/ufm/api/v2/nodes/" + ownerId + "/search", params, header, callback);
}

//获取文件详情信息
function getFileDetail(token, ownerId, fileId, callback) {
    var header = {
        Authorization: token,
    };
    return httpclient.get(config.host + '/ufm/api/wxmp/files/' + ownerId + '/' + fileId, null, header, callback);
}

//获取文件下载地址
function getFileDownloadUrl(ownerId, fileId, callback, token) {
    if (token == undefined || token == null || token == ''){
        token = getApp().globalData.token
    }
    var header = {
        Authorization: token,
    };
    if (fileId.linkCode) {  // 上传页面预览视频
        header = {
            Authorization: 'link,' + fileId.linkCode,
        };
        fileId = fileId.inodeId
    }
    return httpclient.get(config.host + '/ufm/api/v2/files/' + ownerId + '/' + fileId + "/preview", null, header, callback);
}

//获取文件下载地址
function getPreImageDownloadUrl(ownerId, fileId, callback) {
    var header = {
        Authorization: getApp().globalData.token,
    };
    return httpclient.getSync(config.host + '/ufm/api/v2/files/' + ownerId + '/' + fileId + "/preview", null, header, callback);
}

//获取文件下载地址，并且不将文件加最近浏览记录
function getPreImageDownloadUrlNotRecord(ownerId, fileId, callback) {
    var header = {
        Authorization: getApp().globalData.token,
    };
    return httpclient.getSync(config.host + '/ufm/api/v2/files/' + ownerId + '/' + fileId + "/preview", null, header, callback);
}

//将文件添加到最近预览中
function addPreviewFileRecord(ownerId, fileId) {
    var header = {
        Authorization: getApp().globalData.token,
    };
    wx.setStorageSync('isShowLoading', false);
    return httpclient.post(config.host + '/ufm/api/v2/folders/' + ownerId + '/recent/create/' + fileId, null, header);
}

// 获取回收站的文件
function recycleFile(token, ownerId, callback) {
    var header = {
        Authorization: token
    };
    var params = {
        thumbnail: [{ width: 120, height: 120 }, { width: 500, height: 500 }]
    }
    httpclient.post(config.host + '/ufm/api/v2/trash/' + ownerId, params, header, callback);
};

// 获取iNode节点信息
function getNodeInfo(ownerId, nodeId, callback) {
    var header = {
        Authorization: getApp().globalData.token,
    };
    var params = {
        width: 120,
        height: 120
    }
    return httpclient.get(config.host + '/ufm/api/wxmp/nodes/' + ownerId + '/' + nodeId, params, header, callback);
}

//外链获取文件
function saveFile(ownerId, nodeId, linkCode, callback) {
    var header = {
        Authorization: `link,${linkCode},`,
    };
    var params = {
        width: 120,
        height: 120
    }
    return httpclient.get(config.host + '/ufm/api/wxmp/nodes/' + ownerId + '/' + nodeId, params, header, callback);
}

/**
 * 下载并打开文件。文件会保存在本地缓存，文件信息会保存在本地存储。
 * meta： 文件信息，包含以下字段：
 *  ownerId:
 *  nodeId:
 *  name:  文件名（如果有重名，可能会自动增加后缀）
 *  size:
 */
function downloadAndOpenFile(token, ownerId, nodeId, meta) {
    //检查文件大小
    var size = meta.size || 0;
    //根据文件类型打开
    var ext = meta.name.substring(meta.name.lastIndexOf('.') + 1, meta.name.length).toLowerCase();
    //   if (ext != "mp4"){
    if (size > 1024 * 1024 * 10) {
        wx.showModal({
            title: '提示',
            content: '暂不支持10MB以上文件的在线预览',
            showCancel: false
        });
        return;
    }
    //   }

    //获取URL
    getFileDownloadUrl(ownerId, nodeId, function (data) {
        if (data.statusCode == 403) {
            wx.showToast({
                title: '没有访问权限'
            });
            return;
        };
        wx.showLoading({
            title: '下载中...',
            mask: true
        })

        //小程序不允许URL中有端口，将端口进行替换
        var url = utils.replacePortInDownloadUrl(data.url);

        //设置保存到本地的文件信息
        meta = meta || {};
        meta.ownerId = ownerId;
        meta.nodeId = nodeId;

        //如果没有提供文件名，就从URL中获取。
        if (meta.name == undefined) {
            meta.name = decodeURIComponent(url.substring(url.lastIndexOf("/") + 1));
        }

        //下载文件
        wx.downloadFile({
            url: url,
            success: function (res) {
                meta.path = res.tempFilePath;
                openLocalFile(meta);
            },
            fail: function (res) {
                wx.showToast({
                    title: '下载失败！',
                })
            }
        });
    });
}

//打开文件
function openFile(file, callback, token) {
    if (token == undefined || token == null || token == ''){
        token = getApp().globalData.token;
    }
    //根据文件类型打开
    var ext = file.name.substring(file.name.lastIndexOf('.') + 1, file.name.length).toLowerCase();
    if (ext == 'jpg' || ext == 'png' || ext == 'gif' || ext == 'bmp' || ext == 'jpeg' || ext == 'jpeg2000' || ext == 'tiff' || ext == 'psd') {
        previewImage(file.ownedBy, file.id, file.name);
    } else if (ext == 'txt' || ext == 'doc' || ext == 'xls' || ext == 'ppt' || ext == 'pdf' || ext == 'docx' || ext == 'xlsx' || ext == 'pptx') {
        // downloadFileAndPreview(file.ownedBy, file.id, file.size, ext);  //小程序自带预览接口
        previewFile(file.ownedBy, file.id, token);      //自己预览接口
    } else if (ext == 'mp4') {
        playVoice(file.ownedBy, file.id, file.linkCode);
    } else if (ext == 'mp3') {
        playMp3(file.ownedBy, file.id, file.size, file.name, token);
        // playOrAddMusic(file.ownedBy, file.id, file.name, callback);
    } else {
        wx.showToast({
            title: '不支持文件类型',
        });
    }
    //分享文件,不添加到最近浏览中
    if (token != undefined && token != "" && token.indexOf('link,') == -1){
        addPreviewFileRecord(file.ownedBy, file.id);
    }
}

function playOrAddMusic(ownerId, inodeId, fileName, callback) {
    var playMusicResponse = {};
    var musicList = getApp().globalData.musicList;
    var innerAudioContext = getApp().globalData.innerAudioContext;

    if (typeof (musicList) == 'undefined' || musicList == "" || musicList.length == 0) {
        musicList = musicServicce.getMusicListByStorage();
    }

    if (typeof (innerAudioContext) == 'undefined' || innerAudioContext == '') {
        innerAudioContext = wx.createInnerAudioContext();
        getApp().globalData.innerAudioContext = innerAudioContext;
    }
    //本地缓存为空，则表示重未加入列表，直接播放
    if (typeof (musicList) == 'undefined' || musicList == "" || musicList.length == 0) {
        musicServicce.addMusicToStorage(ownerId, inodeId, fileName, musicServicce.PLAY_STATE);

        playMusic(ownerId, inodeId, fileName, callback);
    } else {
        //只添加到播放列表
        musicServicce.addMusicToStorage(ownerId, inodeId, fileName, musicServicce.NORMAL_STATE);
        if (typeof (callback) == 'function') {
            playMusicResponse.state = musicServicce.NORMAL_STATE;
            callback(playMusicResponse);
        }
        return;
    }
}

function playMusic(ownerId, inodeId, fileName, callback) {
    var playMusicResponse = {};
    var musicList = getApp().globalData.musicList;
    var innerAudioContext = getApp().globalData.innerAudioContext;

    if (typeof (innerAudioContext) == 'undefined' || innerAudioContext == '') {
        innerAudioContext = wx.createInnerAudioContext();
        getApp().globalData.innerAudioContext = innerAudioContext;
    }

    if (typeof (musicList) != 'undefined' || musicList != "" || musicList.length != 0) {
        //获取URL
        getFileDownloadUrl(ownerId, inodeId, (data) => {

            //小程序不允许URL中有端口，将端口进行替换
            var url = utils.replacePortInDownloadUrl(data.url);

            innerAudioContext.src = url;
            innerAudioContext.autoplay = true;

            if (typeof (callback) == 'function') {
                playMusicResponse.state = musicServicce.PLAY_STATE;
                callback(playMusicResponse);
            }
        });
    } else {
        if (typeof (callback) == 'function') {
            //播放列表错误
            playMusicResponse.state = -1;
            callback(playMusicResponse);
        }
    }
}

function playMp3(ownerId, nodeId, size, name, token){
    getFileDownloadUrl(ownerId, nodeId, (res) => {
        var url = utils.replacePortInDownloadUrl(res.url);
        wx.navigateTo({
            url: '/disk/template/shareMusic?url=' + encodeURIComponent(url) + '&size=' + size + '&name=' + name
        })
    }, token);
}

function previewImage(ownerId, nodeId, name) {
    var imgUrls = getApp().globalData.imgUrls;
    var url = "";
    if (imgUrls == undefined || imgUrls.length == 0) {
        getPreImageDownloadUrl(ownerId, nodeId, function (download) {
            imgUrls[0] = download.url;
            utils.previewImage(imgUrls, url);
        });
    } else {
        for (var i = 0; i < imgUrls.length; i++) {
            if (imgUrls[i].indexOf(encodeURIComponent(name)) != -1) {
                url = imgUrls[i];
                break;
            }
        }

        utils.previewImage(imgUrls, url);
    }
}

//下载文件到本地缓存（10M以内文件）
function downloadFileAndPreview(ownerId, inodeId, size, ext) {
    var flag = utils.isPreviewFileSize(size);
    if (!flag) {
        wx.showModal({
            title: '提示',
            content: '暂不支持10MB以上文件的在线预览',
            showCancel: false
        });
        return;
    } else {
        //获取URL
        getFileDownloadUrl(ownerId, inodeId, function (data) {
            if (data.statusCode == 403) {
                wx.showToast({
                    title: '没有访问权限'
                });
                return;
            };
            wx.showLoading({
                title: '下载中...',
                mask: true
            })

            //小程序不允许URL中有端口，将端口进行替换
            var url = utils.replacePortInDownloadUrl(data.url);

            //下载文件
            wx.downloadFile({
                url: url,
                success: function (res) {
                    var path = res.tempFilePath;
                    openLocalFile(path, ext);
                },
                fail: function (res) {
                    wx.showToast({
                        title: '下载失败！',
                    })
                }
            });
        });
    }
}

//打开本地文件
//path: 本地缓存文件路径 downloadfile方法获取
//ext： 文件类型 
function openLocalFile(path, ext) {
    wx.openDocument({
        filePath: path,
        fileType: ext,
        success: function (data) {
            wx.hideLoading();
        }, fail: function (res) {
            wx.showToast({
                title: '打开文件失败',
            });
        }
    });
}

//播放视频
function playVoice(ownerId, inodeId, linkCode) {
    var param = inodeId;
    if (linkCode) {
        param = { ownerId, inodeId, linkCode };
    }
    //获取URL   
    getFileDownloadUrl(ownerId, param, function (data) {
        if (data.statusCode == 403) {
            wx.showToast({
                title: '没有访问权限'
            });
            return;
        };
        wx.showLoading({
            title: '下载中...',
            mask: true
        })

        //小程序不允许URL中有端口，将端口进行替换
        var url = utils.replacePortInDownloadUrl(data.url);

        wx.hideLoading();
        wx.navigateTo({
            url: '/disk/widget/video?id=' + inodeId + '&path=' + encodeURIComponent(url)
        })
    });
}

function getPreUploadFileUrl(parentFileInfo, name, size, callback) {
    var parentId = parentFileInfo.nodeId;
    var { ownerId, linkCode } = parentFileInfo;
    var header = {
        Authorization: getApp().globalData.token
    };
    var data = {
        name: name,
        size: size,
        parent: parentId
    }

    if (linkCode) {
        header = {
            Authorization: 'link,' + linkCode,
        };
        data = {
            name: name,
            size: size,
            parent: parentId,
            createdBy: getApp().globalData.cloudUserId
        }
    } 

    return httpclient.put(config.host + '/ufm/api/v2/files/' + ownerId, data, header, callback);
}

function uploadFile(url, name, path, callback) {
    var url = utils.replacePortInDownloadUrl(url);
    const uploadTask = wx.uploadFile({
        url: url, //仅为示例，非真实的接口地址
        filePath: path,
        name: name,
        success: function (res) {
            if (typeof (callback) == "function") {
                callback(res);
            }
        }
    });
    uploadTask.onProgressUpdate((res) => {
        console.log('上传进度', res.progress)
        console.log('已经上传的数据长度', res.totalBytesSent)
        console.log('预期需要上传的数据总长度', res.totalBytesExpectedToSend)
    })
}

function createFolder(folderName, ownerId, nodeId, callback) {
    var header = {
        Authorization: getApp().globalData.token,
    };
    var data = {
        name: folderName,
        parent: nodeId
    };
    return httpclient.post(config.host + '/ufm/api/v2/folders/' + ownerId, data, header, callback);
}

function copyFileToOther(ownerId, nodeId, destOwnerId, destParent, callback) {
    var header = {
        Authorization: getApp().globalData.token,
    };
    var data = {
        autoRename: true,
        destOwnerId: destOwnerId,
        destParent: destParent
    }
    return httpclient.put(config.host + '/ufm/api/v2/files/' + ownerId + "/" + nodeId + "/copy", data, header, callback);
}
//移动文件到
function moveFileToOther(ownerId, nodeId, destOwnerId, destParent, callback) {
    var header = {
        Authorization: getApp().globalData.token,
    };
    var data = {
        autoRename: true,
        destOwnerId: destOwnerId,
        destParent: destParent
    }
    return httpclient.put(config.host + '/ufm/api/v2/files/' + ownerId + "/" + nodeId + "/move", data, header, callback);
}

function copyFolderToOther(ownerId, nodeId, destOwnerId, destParent, callback) {
    var header = {
        Authorization: getApp().globalData.token,
    };
    var data = {
        autoRename: true,
        destOwnerId: destOwnerId,
        destParent: destParent
    }
    return httpclient.put(config.host + '/ufm/api/v2/folders/' + ownerId + "/" + nodeId + "/copy", data, header, callback);
}

function moveFolderToOther(ownerId, nodeId, destOwnerId, destParent, callback) {
    var header = {
        Authorization: getApp().globalData.token,
    };
    var data = {
        autoRename: true,
        destOwnerId: destOwnerId,
        destParent: destParent
    }
    return httpclient.put(config.host + '/ufm/api/v2/folders/' + ownerId + "/" + nodeId + "/move", data, header, callback);
}
//数据、是否渲染多选框、是否要更大的图标200*200
function convertFileList(datas, isMultiselect,bigIcon) {
    var files = [];
    var previewImageUrls = [];
    for (var i = 0; i < datas.length; i++) {
        var file = datas[i];
        if (file.thumbnailUrlList == undefined || file.thumbnailUrlList.length == 0) {
          file.icon = utils.getImgSrc(file, bigIcon);
        } else {
            file.icon = utils.replacePortInDownloadUrl(file.thumbnailUrlList[0].thumbnailUrl);
            var ext = file.name.substring(file.name.lastIndexOf('.') + 1, file.name.length).toLowerCase();
            if (ext == 'jpg' || ext == 'png' || ext == 'gif' || ext == 'bmp' || ext == 'jpeg' || ext == 'jpeg2000' || ext == 'tiff' || ext == 'psd'){
                var index = file.icon.lastIndexOf("/");
                var previewImageUrl = file.icon.substring(0, index);
                previewImageUrls.push(previewImageUrl);
            }
        }
        file.fileSize = utils.formatFileSize(datas[i].size);
        file.modifiedTime = utils.formatDate(datas[i].modifiedAt);
        if (isMultiselect != undefined && isMultiselect != "" && isMultiselect) {
            var checkedList = getApp().globalData.checkedList;
            if (checkedList.length > 0) {
                for (var m = 0; m < checkedList.length; m++) {
                    if (checkedList[m].ownerId == datas[i].ownedBy && checkedList[m].id == datas[i].id) {
                        file.checked = true;
                    }
                }
            }
        }
        files.push(file);
    }
    files.previewImageUrls = previewImageUrls;
    return files;
}
//数据、是否渲染多选框、是否要更大的图标200*200
function convertFolderList(data, isMultiselect, bigIcon) {
    var folders = [];
    for (var i = 0; i < data.length; i++) {
        var folder = data[i];
        if (typeof (folder.isListAcl) == 'undefined' || folder.isListAcl) {
          folder.icon = '/disk/images/icon/' + (bigIcon ? 'batch-share/' : '') + 'folder-icon.png';
        } else {
          folder.icon = '/disk/images/icon/' + (bigIcon ? 'batch-share/' : '') + 'folder-forbidden-icon.png';
        }
        folder.modifiedTime = utils.formatDate(folder.modifiedAt);
        if (isMultiselect != undefined && isMultiselect != "" && isMultiselect) {
            var checkedList = getApp().globalData.checkedList;
            if (checkedList.length > 0) {
                for (var m = 0; m < checkedList.length; m++) {
                    if (checkedList[m].ownerId == data[i].ownedBy && checkedList[m].id == data[i].id) {
                        folder.checked = true;
                    }
                }
            }
        }
        folders.push(folder)
    }
    return folders;
}

function deleteNode(ownerId, nodeId, callback) {
    var header = {
        Authorization: getApp().globalData.token,
    };
    var params = {}
    return httpclient.remove(config.host + '/ufm/api/v2/nodes/' + ownerId + "/" + nodeId, params, header, callback);
}

function deleteFileAndClearTrash(ownerId, nodeId, callback) {
    deleteNode(ownerId, nodeId, function () {
        var header = {
            Authorization: getApp().globalData.token,
        };
        var params = {}
        return httpclient.remove(config.host + '/ufm/api/v2/trash/' + ownerId + "/" + nodeId, null, header, callback);
    });
}

function updateFileName(ownerId, nodeId, name, callback) {
    var header = {
        Authorization: getApp().globalData.token,
    };
    var params = {
        name: name
    }
    return httpclient.put(config.host + '/ufm/api/v2/nodes/' + ownerId + "/" + nodeId, params, header, callback);
}

function getPreviewFileUrl(token, ownerId, nodeId, callback) {
    var header = {
        Authorization: token,
    };
    return httpclient.get(config.host + '/ufm/api/v2/files/' + ownerId + "/" + nodeId + "/preview", null, header, callback);
}

function previewFile(ownerId, nodeId, token) {
    getPreviewFileUrl(token, ownerId, nodeId, function (data) {
        wx.navigateTo({
            url: '/disk/widget/preview?url=' + encodeURIComponent(data.url),
        })
    });
}

function deleteBrowseRecord(ownerId, nodeId, callback) {
    var header = {
        Authorization: getApp().globalData.token,
    };
    return httpclient.remove(config.host + '/ufm/api/v2/folders/' + ownerId + "/recent/delete/" + nodeId, null, header, callback);
}


//保存多个文件
function multipleCopy(params) {
    // params, ownerId, token, callback
    let header = {
        Authorization: getApp().globalData.token
    };
    let ownerId = getApp().globalData.cloudUserId;
    let data = {
        autoRename: true,
        destOwnerId: params.destOwnerId,
        destParent: params.destParent,
        srcNodes: params.data,
        link: { linkCode: params.linkCode }
    }
    httpclient.put(config.host + "/ufm/api/v2/folders/" + ownerId + "/batch/copy", data, header, params.success);
}
// 设为快捷目录
function setShortcut(params, callback) {
    var header = {
        Authorization: getApp().globalData.token
    };
    var cloudUserId = getApp().globalData.cloudUserId;
    var data = {
        createBy: cloudUserId,
        ownerId: params.ownerId,
        nodeId: params.nodeId,
        type: 1
    }
    httpclient.post(config.host + "/ufm/api/v2/folders/" + cloudUserId + "/shortcut/create", data, header, callback);
}

module.exports = {
    listLastReadFile,
    listMySharetTo,
    listShareToMe,
    listTeamSpace,
    listEnterpriseSpace,
    listDeptSpace,
    getFileDetail,
    getFileDownloadUrl,
    getPreImageDownloadUrl,
    getPreImageDownloadUrlNotRecord,
    lsOfFolder,
    getNodeInfo,
    downloadAndOpenFile,
    openLocalFile,
    getPreUploadFileUrl,
    uploadFile,
    convertFileList,
    convertFolderList,
    openFile,
    createFolder,
    copyFileToOther,
    copyFolderToOther,
    moveFileToOther,
    moveFolderToOther,
    playMusic,
    deleteNode,
    deleteFileAndClearTrash,
    updateFileName,
    deleteBrowseRecord,
    recycleFile,
    trash,
    trashclean,
    listDepAndUsers,
    teamspaces,
    addmember,
    memberteamlist,
    deleteMember,
    deleteTeam,
    saveFile,
    searchFiles,
    multipleCopy,
    oderFiles,
    setShortcut,
    lsOfFolderNoPaging
};
