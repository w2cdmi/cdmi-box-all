function uploadImage(tempFiles, index, parentFileInfo, page) {
    if (index > tempFiles.length - 1) {
        return;
    }
    // 名字
    var ext = tempFiles[index].path.substring(tempFiles[index].path.lastIndexOf('.'), tempFiles[index].path.length).toLowerCase();
    var fileName = (new Date()).getTime() + ext;

    File.getPreUploadFileUrl(parentFileInfo, fileName, tempFiles[index].size, function (data) {
        var inodeId = data.fileId;
        var uploadUrl = data.uploadUrl + "?objectLength=" + tempFiles[index].size;

        var url = utils.replacePortInDownloadUrl(uploadUrl);
        const uploadTask = wx.uploadFile({
            url: url, //仅为示例，非真实的接口地址
            filePath: tempFiles[index].path,
            name: fileName,
            success: function (res) {
                page.setData({
                    percent: 0
                });
                console.log("上传成功");
                page.lsOfFolderForClickCrum(getApp().globalData.token, parentFileInfo.ownerId, parentFileInfo.nodeId);
            },
            fail: function () {
                page.setData({
                    percent: 0
                });
                console.log("上传失败");
            },
            complete: function (res) {
                console.log('上传完成：', res);
            }
        });
        uploadTask.onProgressUpdate((res) => {
            if (page.data.uploadingList) {
                var uploadingList = page.data.uploadingList;
                uploadingList[0].name = fileName;
                page.setData({
                    uploadingList: uploadingList,  // 修改收件箱的文件名
                });
            }

            page.setData({
                isShowUplodProgress: true,
                currentIndex: 0, // 收件箱当前上传文件
                percent: res.progress
            });
            if (res.progress == 100) {
                index += 1;
                if (index >= tempFiles.length) {
                    page.setData({
                        isShowUplodProgress: false,
                    });
                }

                if (page.data.uploadingList.length > 0) {
                    var uploadingList = page.data.uploadingList;
                    var fileList = page.data.fileList;
                    var previewImageUrls = page.data.previewImageUrls;
                    var currData = uploadingList.shift();
                    fileList.unshift(currData);
                    previewImageUrls.unshift(currData.icon);
                    page.setData({
                        uploadingList: uploadingList,
                        fileList: fileList,
                        offset: page.data.offset + 1,
                        previewImageUrls: previewImageUrls
                    });
                    if (uploadingList.length > 0) {
                        if (uploadingList[0].path) {
                            uploadImage(uploadingList, 0, parentFileInfo, page); return;
                        } else {
                            uploadVideo(uploadingList[0], parentFileInfo, page); return;
                        }
                    }
                }
                uploadImage(tempFiles, index, parentFileInfo, page);
            }
        })
    })
}

function uploadVideo(tempFile, parentFileInfo, page) {
    var vedioName = (new Date()).getTime() + ".mp4";
    // 存到收件箱上传页面全局变量，添加名字
    if (page.data.uploadingList[0] && !page.data.uploadingList[0].tempFilePath) {
        return;
    }
    if (page.data.uploadingList[0] && page.data.uploadingList[0].tempFilePath) {
        tempFile = page.data.uploadingList[0]
    }
    File.getPreUploadFileUrl(parentFileInfo, vedioName, tempFile.size, function (data) {
        var inodeId = data.fileId;
        var uploadUrl = data.uploadUrl + "?objectLength=" + tempFile.size;

        var url = utils.replacePortInDownloadUrl(uploadUrl);
        const uploadTask = wx.uploadFile({
            url: url, //仅为示例，非真实的接口地址
            filePath: tempFile.tempFilePath,
            name: vedioName,
            success: function (res) {
                page.setData({
                    percent: 0
                });
                page.lsOfFolderForClickCrum(getApp().globalData.token, parentFileInfo.ownerId, parentFileInfo.nodeId, page);
            },
            fail: function () {
                page.setData({
                    percent: 0
                });
                console.log("上传失败");
            }
        });
        uploadTask.onProgressUpdate((res) => {
            if (page.data.uploadingList) {
                var uploadingList = page.data.uploadingList;
                uploadingList[0].name = vedioName;
                page.setData({
                    uploadingList: uploadingList,  // 修改收件箱的文件名
                });
            }

            page.setData({
                isShowUplodProgress: true,
                currentIndex: 0, // 收件箱当前上传文件
                percent: res.progress
            });
            if (res.progress == 100) {
                if (page.data.uploadingList.length > 0) {
                    var uploadingList = page.data.uploadingList;
                    var fileList = page.data.fileList;
                    var currData = uploadingList.shift();
                    fileList.unshift(currData);
                    page.setData({
                        uploadingList: uploadingList,
                        fileList: fileList,
                        offset: page.data.offset + 1
                    });
                    if (uploadingList.length > 0) {
                        if (uploadingList[0].path) {
                            uploadImage(uploadingList, 0, parentFileInfo, page); return;
                        } else {
                            uploadVideo(uploadingList[0], parentFileInfo, page); return;
                        }
                    }
                }

                page.setData({
                    isShowUplodProgress: false,
                });
            }
        })
    })
}

module.exports = {
    uploadImage,
    uploadVideo
}