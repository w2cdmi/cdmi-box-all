var files = null;
var uploadingParentId = null;
var nodePermission = {};
var __page = 1;

/**
 上传进度信息
 */
function ProgressInfo(files) {
    files = files || [];

    //文件数量
    this.fileCount = files.length || 0;

    //上传失败数量
    this.errorCount = 0;

    //上传文件总大小
    this.totalSize = 0;

    //已上传总大小
    this.loadSize = 0;

    //进度
    this.progress = 0; // 0 - 1

    //剩余时间
    this.leftTime = 0;

    //传输速度
    this.speed = 0;

    //累计总大小
    for(var i = 0 ; i < files.length; i++) {
        this.totalSize += files[i].size || 0;
    }

    //每个上传文件的进度
    this.items = [];
    for(i = 0 ; i < files.length; i++) {
        var item = {};
        item.id = files[i].id || files[i].name;
        item.name = files[i].name;
        item.size = files[i].size;
        item.loaded = 0;
        item.progress = 0;
        item.speed = 0;
        item.state = 0; //0: 未开始, 1: 上传中, 2: 完成, -1: 发生错误, -2：用户取消

        this.items.push(item);
    }
}

//单个失败更新单个进度
ProgressInfo.prototype.error = function(id) {
    var item = {};

    this.totalSize = 0;
    this.loadSize = 0;
    for(var i = 0; i < this.items.length; i++) {
        if(this.items[i].id === id) {
            if(this.items[i].state > -1) {
                //失败数量+1，总数-1
                this.errorCount++;
                this.fileCount--;
            }
            this.items[i].state = -1;
            item = this.items[i];
        } else {
            //重新计算，排除其他已经报错的信息
            if(this.items[i].state > -1) {
                this.totalSize += this.items[i].size;
                this.loadSize += this.items[i].loaded;
            }
        }
    }

    //重新计算相关指标
    this.calculate();

    return item;
};

//更新单个进度
ProgressInfo.prototype.update = function(id, loaded) {
    var old = this.loadSize;
    var item = {};

    for(var i = 0; i < this.items.length; i++) {
        if(this.items[i].id === id) {
            this.loadSize += (loaded - this.items[i].loaded);
            this.items[i].speed = (loaded - this.items[i].loaded) * 10;
            this.items[i].loaded = loaded;
            this.items[i].progress = this.items[i].loaded / this.items[i].size;

            //
            if(this.items[i].loaded >= this.items[i].size) {
                this.items[i].progress = 1;
                this.items[i].state = 2;
            } else if(this.items[i].loaded > 0) {
                this.items[i].state = 1;
            }

            item = this.items[i];
            break;
        }
    }

    //刷新速度
    this.speed = (this.loadSize - old) * 10 * this.fileCount; //每秒值，此方法每个文件每100毫秒调用一次

    //重新计算相关指标
    this.calculate();

    return item;
};

//更新单个进度
ProgressInfo.prototype.calculate  = function(id, loaded) {
    //重新计算总进度
    if(this.totalSize > 0) {
        this.progress = this.loadSize / this.totalSize;
    } else {
        this.progress = 0;
    }

    //
    if(this.progress < 1) {
        //未完成，计划预计剩余时间
        if(this.speed > 0) {
            this.leftTime = (this.totalSize - this.loadSize) / this.speed;
        } else {
            this.leftTime = 0;
        }
    } else {
        this.speed = 0;
        this.leftTime = 0;
        this.progress = 1;
    }
};

//上传完成
ProgressInfo.prototype.isCompleted = function() {
    return this.loadSize >= this.totalSize;
};

/* 上传文件管理器
*   state: 0，//0, 未开始； 1，上传中； 2，成功。 -1，失败; -2, 用户取消
* */
function UploadFileManager(files) {
    var s = localStorage.getItem("uploadFileList");

    if (s !== undefined && s !== null && s !== "") {
        this.uploadFiles = JSON.parse(s);
    } else {
        this.uploadFiles = [];
    }
    console.log(this.uploadFiles);

    if(files !== undefined && files !== null) {
        for(var i = 0; i < files.length; i++) {
            var file = {};
            file.id = files[i].id;
            file.name = files[i].name;
            file.size = files[i].size;
            file.lastModified = files[i].lastModified;
            file.state = 0; //未开始上传

            this.uploadFiles.push(file);
        }
        localStorage.setItem("uploadFileList", JSON.stringify(this.uploadFiles));
    }
}

//获取上传文件列表
UploadFileManager.prototype.getUploadFiles = function () {
    return this.uploadFiles;
};

//上传文件是否已经存在
UploadFileManager.prototype.contains = function (task) {
    var taskList = this.getUploadFiles();
    for(var i = 0; i < taskList.length; i++) {
        var t = taskList[i];
        if(t.id === task.id) {
            return true;
        }
    }

    return false;
};

//更新文件状态
UploadFileManager.prototype.updateState = function (id, state) {
    for(var i = 0; i < this.uploadFiles.length; i++) {
        if(this.uploadFiles[i].id === id) {
            this.uploadFiles[i].state = state;
            break;
        }
    }
};

//上传完成，更新所有的文件状态，并保存到storage中
UploadFileManager.prototype.finish = function () {
    for(var i = 0; i < this.uploadFiles.length; i++) {
        //结束时， 将“上传中”的文件，都置为“上传失败”
        if(this.uploadFiles[i].state === 0 || this.uploadFiles[i].state === 1) {
            this.uploadFiles[i].state = -1;
        }
    }

    localStorage.setItem("uploadFileList", JSON.stringify(this.uploadFiles));
    console.debug(localStorage.getItem("uploadFileList"));
};

//上传完成，更新所有的文件状态，并保存到storage中
UploadFileManager.prototype.cancel = function () {
    for(var i = 0; i < this.uploadFiles.length; i++) {
        //取消时， 将“未开始/上传中”的文件，都置为“用户取消”
        if(this.uploadFiles[i].state === 0 || this.uploadFiles[i].state === 1) {
            this.uploadFiles[i].state = -2;
        }
    }

    localStorage.setItem("uploadFileList", JSON.stringify(this.uploadFiles));
};

//清空上传文件列表
UploadFileManager.prototype.clear = function () {
    this.uploadFiles = [];
    localStorage.setItem("uploadFileList", JSON.stringify(this.uploadFiles));
};

//总体进度条
function EntireProgressBar() {
    this.id = new Date().getTime();
}

//显示进度条
EntireProgressBar.prototype.show = function (e) {
    var $barList = $("#entireProgressBarList");
    $("#entireProgressBarTemplate").template({id: this.id}).appendTo($barList);
    $barList.show();
};

//更新进度
EntireProgressBar.prototype.updateProgress = function (e) {
    var $bar = $("#entireProgressBar_" + this.id);
    var p = (e.summary.progress*100).toFixed(0);
    $bar.find(".progress").html(p + "%").css("width", p + "%");
    $bar.find(".speed").html(formatFileSize(e.summary.speed.toFixed(0)) + "/s");
    $bar.find(".left-time").html(formatDateSize(e.summary.leftTime.toFixed(0)));
};

//更新进度
EntireProgressBar.prototype.error = function (e) {
    var $bar = $("#entireProgressBar_" + this.id);
    $bar.find(".info").addClass("error").html("文件上传发生错误");
};

//进度条完成
EntireProgressBar.prototype.finish = function (e) {
    $("#entireProgressBar_" + this.id).remove();
    var $barList = $("#entireProgressBarList");
    if($barList.children().size() === 0) {
        $barList.hide();
    }
};

//单个文件进度条
function IndividualProgressBar() {
}

//显示进度条
IndividualProgressBar.prototype.show = function (e) {
    var $list = $("#fileList");
    //表示"文件列表为空的"的div
    $list.children("div.blank-file-list").remove();

    var $template = $("#uploadFileProgressTemplate");
    var items = e.summary.items;
    for (var i = 0; i < items.length; i++) {
        var item = items[i];

        item.menderName = curUserName;//使用当前登录用户名
        item.ownedBy = item.ownerId;
        item.fileSize = formatFileSize(item.size);
        item.modifiedAt = getFormatDate(item.lastModified, "yyyy-MM-dd");
        item.divClass = getImgHtml(item.type, item.name);
        var $row = $template.template(item);
        $row.prependTo($list);

        if(item.error) {
            $row.find(".info").html("上传失败, " + item.error);

            //删除正在加载class，刷新列表时，此文件被清除
            $row.removeClass("file-uploading");
        }
        //
        // $("#file_" + item.id).data("node", item);
        // $("#files_" + item.id).data("node", item);
    }
};

//更新进度
IndividualProgressBar.prototype.updateProgress = function (e) {
    var p = (e.file.progress * 100).toFixed(0);
    var $file = $("#files_" + e.file.id);

    $file.find(".progress").html(p + "%");
    $file.find(".speed").html("(" +  formatFileSize(e.file.speed) + "/s)");
    if(p < 100) {
        $file.find(".bar").css("width", p + "%");
        $file.find(".info").html("正在上传");
    } else {
        $file.find(".bar").css("width", p + "%").hide();
        $file.find(".info").html("上传完成");
        $file.css("background", "white");

        //删除正在加载class，刷新列表时，此文件被清除
        $file.parent('.file-uploading').removeClass("file-uploading");

        // //增加事件处理
        // $("#file_" + e.file.id).on('click', onPress);
        // $file.parent('.weui-cell_swiped').swipeout();
    }
};

//更新进度
IndividualProgressBar.prototype.error = function (e) {
    if(e.file.error) {
        $("#files_" + e.file.id).find(".info").html("上传失败, " + e.file.error);
    } else {
        $("#files_" + e.file.id).find(".info").html("上传失败");
    }
};

//进度条完成
IndividualProgressBar.prototype.finish = function (e) {
};

/* 上传任务进度管理器*/
function UploadProgressIndicator(files, callback) {
    //生成id，用于
    for(var i = 0; i < files.length; i++) {
        files[i].id = new Date().getTime() + "_" + i;
    }
    this.progressInfo = new ProgressInfo(files);
    this.uploadFileManager = new UploadFileManager(files);

    //如果存在uploadFileList，使用单独文件进度条
    if($("#fileList").length > 0) {
        this.progressBar = new IndividualProgressBar();
    } else {
        this.progressBar = new EntireProgressBar();
    }

    //上传完成后的回调
    this.callback = callback;
}

//文件上传进度处理
UploadProgressIndicator.prototype.processor = function (file) {
    var id = file.id || file.name;
    var indicator = this;

    return function (e) {
        indicator.updateProgress(id, e);
    }
};

//文件上传进度处理
UploadProgressIndicator.prototype.error = function (file) {
    var id = file.id || file.name;

    //更新进度信息
    var item = this.progressInfo.error(id);
    item.error = file.error;

    //更新文件列表中的传输状态
    this.uploadFileManager.updateState(id, -1);

    //刷新进度条
    this.progressBar.error({summary: this.progressInfo, file: item});

    //已经全部上传完成
    if(this.progressInfo.isCompleted()) {
        this.finishWithError();
    }
};

UploadProgressIndicator.prototype.updateProgress = function (id, e) {
    console.log(e);
    //更新进度信息
    var item = this.progressInfo.update(id, e.loaded);

    //更新文件列表中的传输状态
    this.uploadFileManager.updateState(id, item.state);

    //刷新进度条
    this.progressBar.updateProgress({summary: this.progressInfo, file: item});

    //已经全部上传完成
    if(this.progressInfo.isCompleted()) {
        this.finish();
    }
};

//显示进度条
UploadProgressIndicator.prototype.show = function () {
    this.progressBar.show({summary: this.progressInfo});
};

UploadProgressIndicator.prototype.finish = function () {
    //更新上传文件列表
    this.uploadFileManager.finish();

   //隐藏进度条
    this.progressBar.finish();

    $.toast("上传完成");

    //回调
    if(typeof this.callback === "function") {
        this.callback();
    }
};

UploadProgressIndicator.prototype.finishWithError = function () {
    //更新上传文件列表
    this.uploadFileManager.finish();

   //隐藏进度条
    this.progressBar.finish();

    $.toptip("上传发生错误", 'warning');

    //回调
    // if(typeof this.callback === "function") {
    //     this.callback();
    // }
};

$(document).ready(function () {
    //初始化WxJsSdk
    initWwJsJdkAndInvoke();

    //todo: 获取上传权限
});

function clickUpload(){
    $("#fileUpload").click();
}

function selectTargetFolder() {
    files = document.querySelector("#fileUpload").files;

    console.log(files);
    if (files.length === 0) {
        return;
    }

    var $fileViewer = $("#fileListWrapper");
    if($fileViewer.length > 0) {
        //如果存在fileListWrapper对象，说明已经进入了空间内，直接上传到当前目录
        console.log("直接上传到当前目录：" + ownerId + "/" + parentId);
        uploadFile(ownerId, parentId, true);
    } else {
        //已经选择的文件信息
        $("#selectedFilesLabel").html(joinFileName(files));
        $("#selectedFilesLabelLength").html(joinFileLength(files))

        var folderChooser = $("#folderChooserDialog").FolderChooser({
            callback: function (ownerId, nodeId) {
                uploadFile(ownerId, nodeId, false);
            }
        });

        //加载数据
        folderChooser.showDialog();
    }
}
function joinFileName(files) {
    var n = "";
    for (var i = 0; i < files.length; i++) {
        n= n + files[i].name + ", ";
    }
    return n
}
function joinFileLength(files) {
    var s = "";
    s = s + "等" + files.length + "个文件";
    return s;
}

function uploadFile(ownerId, parentId, refresh){
    var uploadProgressIndicator;
    if(refresh) {
        var o = ownerId, p = parentId;
        uploadProgressIndicator = new UploadProgressIndicator(files, function () {
            //不主动刷新文件列表，让用户手工下拉刷新。
            // listFile();
        });
    } else {
        uploadProgressIndicator = new UploadProgressIndicator(files);
    }

    // 遍历文件列表，插入到表单数据中
    for (var i = 0; i < files.length; i++) {
        files[i].ownerId = ownerId;
        files[i].parentId = parentId;
        executeUpload(files[i], uploadProgressIndicator);
    }

    uploadProgressIndicator.show();
}

function executeUpload(file, indicator) {
    $.ajax({
        url: host + "/ufm/api/v2/files/" + file.ownerId,
        type: "PUT",
        async: false,
        data: JSON.stringify({
            "parent": file.parentId,
            "name": file.name,
            "size": file.size
        }),
        success: function (data, textStatus, jqXHR) {
            if(data.code === "ExceedUserAvailableSpace") {
                file.error = "空间不足";
                indicator.error(file);
                return;
            }

            var uploadUrl = data.uploadUrl + "?objectLength=" + file.size;
            file.nodeId = data.fileId;
            var formData = new FormData();
            formData.append(file.name, file);
            $.ajax({
                url: uploadUrl,
                type: "POST",
                beforeSend: function (xhr) {
                    xhr.setRequestHeader("Authorization", userToken);
                },
                //async: false,
                data: formData,
                processData: false,	// 告诉jQuery不要去处理发送的数据
                contentType: false, // 告诉jQuery不要去设置Content-Type请求头
                xhr: function(){
                    var xhr = new window.XMLHttpRequest();
                    xhr.upload.addEventListener("progress", indicator.processor(file), false);
                    return xhr; //xhr对象返回给jQuery使用
                },
                error: function () {
                    indicator.error(file)
                }
            });
        },
        error: function (xhr, status, error) {
            indicator.error(file)
        }
    });
}

var __images_count = 0; //本次上传的图片总数
var __images_success = 0; //上传成功数量
var __images_error = 0; //上传失败数量
function uploadPhoto() {
    openCamera();
    //todo: 检查操作权限
/*
    if (nodePermission["upload"] != 1) {
        $.alert("您没有上传的权限！")
    } else {
        openCamera();
    }
*/
}

//拍照或者选择照片
function openCamera() {
    __images_count = 0;
    __images_success = 0;
    __images_error = 0;

    wx.chooseImage({
        count: 9, // 默认9，这里每次只处理一张照片
        sizeType: ['original', 'compressed'], 	// 可以指定是原图还是压缩图，默认二者都有
        sourceType: ['camera'], 		// 可以指定来源是相机，默认二者都有
        success: function (res) {
            // 返回选定照片的本地ID列表，localId可以作为img标签的src属性显示图片
            selectTargetFolderAndUploadImages(res.localIds);
        },
        error: function (data) {
            $.toptip("选择图片失败", "warning");
        }
    });
}

//选择上传目录，然后上传图片
function selectTargetFolderAndUploadImages(localIds) {
    var $fileViewer = $("#fileListWrapper");
    if($fileViewer.length > 0) {
        //如果存在fileListWrapper对象，说明已经进入了空间内，直接上传到当前目录
        console.log("直接上传到当前目录：" + ownerId + "/" + parentId);
        uploadImages(ownerId, parentId, localIds, true);
    } else {
        //已经选择的文件信息
        $("#selectedFilesLabel").html(localIds.length + "张图片被选中");

        var folderChooser = $("#folderChooserDialog").FolderChooser({
            callback: function (ownerId, nodeId) {
                uploadImages(ownerId, nodeId, localIds, false);
            }
        });

        //加载数据
        folderChooser.showDialog();
    }
}


//上传图片到存储
function uploadImages(ownerId, parentId, images, refresh) {
    __images_count = images.length;
    for(var i = 0; i < images.length; i++) {
        wx.uploadImage({
            localId: images[i], // 需要上传的图片的本地ID，由chooseImage接口获得
            isShowProgressTips: 1, // 默认为1，显示进度提示
            success: function (res) {
                var serverId = res.serverId; // 返回图片的服务器端ID
                postPullImageRequest(ownerId, parentId, serverId);
            }, error: function (res) {
                increaseErrorCount();
            }
        });
    }
}

function postPullImageRequest(ownerId, parentId, serverId) {
    var fileName =  new Date().getTime() + ".jpg";
    var parameter = {
        "ownerId": ownerId,
        "fileName": fileName,
        "parentId": parentId,
        "serverId": serverId,
        "corpId": corpId,
        "token": token
    };

    $.ajax({
        url: ctx + "/api/v2/jsSDK/uploadPhoto",
        type: "POST",
        data: JSON.stringify(parameter),
        success: function () {
            increaseSuccessCount();
        },
        error: function () {
            increaseErrorCount();
        }
    });
}

function increaseErrorCount() {
    if(__images_count > 0) {
        __images_error++;

        toastIfFinished();
    }
}

function increaseSuccessCount() {
    if(__images_count > 0) {
        __images_success++;

        //上传完成
        toastIfFinished();
    }
}

function toastIfFinished() {
    if(__images_success + __images_error === __images_count) {
        if(__images_error > 0) {
            $.toptip("图片上传完成，失败：" + __images_error);
        } else {
            $.toast("图片上传成功");
        }

        //重置
        __images_count = 0;
        __images_success = 0;
        __images_error = 0;
    }
}