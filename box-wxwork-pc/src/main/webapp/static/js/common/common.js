/*媒介根元素*/
window.addEventListener(('orientationchange' in window ? 'orientationchange' : 'resize'), (function () {
    function c() {
        var d = document.documentElement;
        var cw = d.clientWidth || 750;
        d.style.fontSize = (20 * (cw / 375)) > 40 ? 40 + 'px' : (20 * (cw / 375)) + 'px';
    }

    c();
    return c;
})(), false);

/*增强Template7功能：JQuery对象（Template7脚本）直接替换变量，并生成新的JQuery对象。*/
$.fn.template = function (data) {
    if (this._compile === undefined) {
        this._compile = $.t7.compile(this.html());
    }

    return $(this._compile(data));
};

String.prototype.replaceAll = function (s1, s2) {
    return this.replace(new RegExp(s1, "gm"), s2);
}

function gotoPage(url) {
    window.location.href = url;
}

function gotoPageOpen(url) {
    window.open(url)
}

function goBack() {
    history.go(-1);
}

function getCookie(name, defaultValue) {
    var arr = document.cookie.match(new RegExp("(^| )" + name + "=([^;]*)(;|$)"));
    if (arr != null) {
        return unescape(arr[2]);
    } else if (defaultValue != undefined) {
        return defaultValue;
    }

    return null;
}

function setCookie(name, value) {
    var exp = new Date("December 31, 9998");
    document.cookie = name + "=" + escape(value) + ";expires=" + exp.toGMTString();
}

function delCookie(name) {
    var exp = new Date();
    exp.setTime(exp.getTime() - 1);
    var cval = getCookie(name);
    if (cval != null) document.cookie = name + "=" + cval + ";expires=" + exp.toGMTString();
}

function getStorage(name, defaultValue) {
    var value = localStorage.getItem(name);
    if (value != undefined && value != null) {
        return value;
    } else if (defaultValue != undefined) {
        return defaultValue;
    }

    return null;
}

function setStorage(name, value) {
    localStorage.setItem(name, value);
}

function removeStorage(name) {
    localStorage.removeItem(name);
}

/**
 *转换日期对象为日期字符串
 * @param date 日期对象
 *
 * @return 符合要求的日期字符串
 */
function getSmpFormatDate(date) {
    var pattern = "yyyy-MM-dd hh:mm:ss";
    return getFormatDate(date, pattern);
}

/**
 *转换日期对象为日期字符串
 * @param l long值
 * @param pattern 格式字符串,例如：yyyy-MM-dd hh:mm:ss
 * @return 符合要求的日期字符串
 */
function getFormatDate(date, pattern) {
    if (date == undefined) {
        date = new Date();
    }
    if (typeof date == 'number') {
        date = new Date(date);
    }
    if (pattern == undefined) {
        pattern = "yyyy-MM-dd hh:mm:ss";
    }
    return date.format(pattern);
}

//扩展Date的format方法 
Date.prototype.format = function (format) {
    var o = {
        "M+": this.getMonth() + 1,
        "d+": this.getDate(),
        "h+": this.getHours(),
        "m+": this.getMinutes(),
        "s+": this.getSeconds(),
        "q+": Math.floor((this.getMonth() + 3) / 3),
        "S": this.getMilliseconds()
    }
    if (/(y+)/.test(format)) {
        format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    }
    for (var k in o) {
        if (new RegExp("(" + k + ")").test(format)) {
            format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
        }
    }
    return format;
}

function formatFileSize(size) {
    if (typeof  size !== "number") {
        return "";
    }

    var sizeStr;
    if (size === 0) {
        sizeStr = "0KB";
    } else if (size < 1024) {
        sizeStr = "1KB";
    } else if (size >= 1024 && size < 1024 * 1024) {
        sizeStr = (size / 1024).toFixed(0) + "KB";
    } else if (size >= 1024 * 1024 && size < 1024 * 1024 * 1024) {
        sizeStr = (size / 1024 / 1024).toFixed(2) + "MB";
    } else {
        sizeStr = (size / 1024 / 1024 / 1024).toFixed(2) + "GB";
    }

    return sizeStr;
}

function formatDateSize(size) {
    var sizeStr;
    if (size < 60) {
        sizeStr = size + "s";
    } else if (size >= 60 && size < 60 * 60) {
        sizeStr = (size / 60).toFixed(2) + "m";
    } else if (size >= 60 * 60 && size < 60 * 60 * 60) {
        sizeStr = (size / 60 / 60).toFixed(2) + "h";
    } else {
        sizeStr = (size / 60 / 60 / 60).toFixed(2) + "d";
    }

    return sizeStr;
}

function getLocalTime(serverTime) {
    var d = new Date(serverTime);
    return getSmpFormatDate(d);
}

function setRootCookie(name, value) {
    delCookie(name);
    var exp = new Date("December 31, 9998");
    document.cookie = name + "=" + escape(value) + ";path=/;expires=" + exp.toGMTString();
}

function getRootCookie(name) {
    var arr = document.cookie.match(new RegExp("(^| )" + name + "=([^;]*)(;|$)"));
    if (arr != null) {
        return unescape(arr[2]);
    }
    return null;
}

function getImgHtml(type, fileName, shareStatus) {
    if (type == 0) {
        if (shareStatus == 1) {
            return "folder-share-icon";
        }
        return "folder-icon";
    }

    var index = fileName.lastIndexOf(".");
    if (index != -1) {
        var fileType = fileName.substring(index + 1).toLowerCase();
        if (fileType == "doc" || fileType == "ppt" || fileType == "xls" || fileType == "docx" || fileType == "pptx" || fileType == "xlsx" ||
            fileType == "rar" || fileType == "mp3" || fileType == "txt" || fileType == "pdf" || fileType == "jpg" || fileType == "png" ||
            fileType == "gif" || fileType == "avi" || fileType == "exe" || fileType == "jpeg" || fileType == "mp4" || fileType == "MP4") {
            return "file-" + fileType;
        }
    }

    return "file-undefined";
}


function getImgSrc(type, fileName) {
    if (type == 0) {
        return "/static/skins/default/img/icon/folder-icon.png";
    }

    var index = fileName.lastIndexOf(".");
    if (index != -1) {
        var fileType = fileName.substring(index + 1).toLowerCase();
        if (fileType == "doc" || fileType == "rar" || fileType == "ppt" || fileType == "xls" ||
            fileType == "mp3" || fileType == "txt" || fileType == "pdf" || fileType == "jpg" || fileType == "png" || fileType == "gif" || fileType == "avi") {
            return "/static/skins/default/img/icon/file-" + fileType + ".png";
        }
    }

    return "/static/skins/default/img/icon/file-undefined.png";
}

function handleError(xhr) {
    var status = xhr.status;
    var response = JSON.parse(xhr.responseText);

    if (response.code === "SameParentConflict") {
        $.Alert("相同目录不能进行操作")
    } else if (response.code === "NoSuchItem") {
        $.Alert("文件或文件夹不存在")
    } else if (response.code === "NoSuchFile") {
        $.Alert("文件不存在")
    } else if (response.code === "NoSuchFolder") {
        $.Alert("文件夹不存在")
    } else if (response.code === "NoSuchParent") {
        $.Alert("父目录不存在")
    } else if (response.code === "NoSuchSource") {
        $.Alert("源文件或文件夹不存在")
    } else if (response.code === "NoSuchDest") {
        $.Alert("目标文件或文件夹不存在")
    } else if (response.code === "Forbidden") {
        $.Alert("您没有权限进行该操作")
    } else if (response.code === "InvalidParameter") {
        $.Alert("请求参数错误")
    } else if (response.code === "LinkExistedConflict") {
        $.Alert("外链已存在")
    } else if (response.code === "LinkExpired") {
        $.Alert("外链已过期")
    } else if (response.code === "LinkNotEffective") {
        $.Alert("外链未生效")
    } else if (response.code === "NoSuchLink") {
        $.Alert("外链不存在")
    } else if (response.code === "NoSuchUser") {
        $.Alert("用户不存在")
    } else if (response.code === "SubFolderConflict") {
        $.Alert("不能移动子目录下")
    } else if (response.code === "SameNodeConflict") {
        //复制或移动时，目标节点和源节点相同冲突
        $.Alert("目标文件夹与源文件夹相同")
    } else if (response.code === "SameParentConflict") {
        //复制或移动时，目标节点是源节点的父文件夹
        $.Alert("目标文件夹已在该目录下")
    } else if (response.code === "UserLocked") {
        $.Alert("用户被锁定")
    } else if (response.code === "ExistMemberConflict") {
        $.Alert("成员已存在")
    } else if (response.code === "ExistTeamspaceConflict") {
        $.Alert("协作空间已存在")
    } else if (response.code === "ExceedMaxLinkNum") {
        $.Alert("外链数超过最大限制")
    } else if (response.code === "InvalidFileType") {
        $.Alert("不支持的文件类型")
    } else if (response.code === "ExceedQuota") {
        $.Alert("空间容量不足")
    } else if (response.code === "ExceedUserAvailableSpace") {
        $.Alert("空间容量不足")
    } else if (response.code === "UploadSizeTooLarge") {
        $.Alert("上传文件大小超过限制")
    } else if (response.code === "UploadSizeTooLarge") {
        $.Alert("上传文件大小超过限制")
    } else if (response.code === "ExsitShortcut") {
        // 此消息可以不显示
        $.Alert("快捷目录已经存在")
    } else {
        if (status === 400 && response === 'Forbidden') {
            $.Alert("您没有权限进行该操作");
        } else if (status === 403) {
            $.Alert("您没有权限进行该操作");
        } else if (status === 404) {
            $.Alert("文件或文件夹不存在");
        } else {
            $.Alert("操作失败");
        }
    }
}

function getAclByRole(roleName) {
    var acl = "";
    switch (roleName) {
        case "previewer":
            acl = "预览"
            break;
        case "uploader":
            acl = "预览 上传"
            break;
        case "viewer":
            acl = "预览 下载"
            break;
        case "downLoader":
            acl = "预览 下载"
            break;
        case "uploadAndView":
            acl = "预览 上传 下载"
            break;
        case "auther":
            acl = "拥有者"
            break;

        default:
            break;
    }
    return acl;
}

/**
 * 文件后缀对应的图标
 */
var iconMap = {
    'txt': 'txt',
    'mp3': 'music',
    'wav': 'music',
    'mp4': 'video',
    'ogg': 'video',
    'ogv': 'video',
    'webm': 'video',
    // 'avi': 'video', //不能在线播放的视频，不显示为video图标
    // 'rm': 'video',
    // 'rmvb': 'video',
    // 'mov': 'video',
    // 'wmv': 'video',
    // 'flv': 'video',
    'doc': 'word',
    'docx': 'word',
    'xls': 'excel',
    'xlsx': 'excel',
    'ppt': 'ppt',
    'pptx': 'ppt',
    'pdf': 'pdf',
    'png': 'img',
    'jpg': 'img',
    'jpeg': 'img',
    'gif': 'img',
    'bmp': 'img',
    'zip': 'zip',
    'rar': 'zip'
}

/**
 * 获取文件图标样式类名
 */
function getFileIconClass(filename) {
    var index = filename.lastIndexOf('.')
    if (index != -1) {
        var ext = filename.substring(index + 1).toLowerCase()
        var fileType = iconMap[ext]
        if (fileType) {
            return 'ico-' + fileType
        }
    }
    return 'ico-none'
}

/**
 * 获取文件图标
 * @param row
 * @returns {string}
 */
function getFileIcon(row) {
    if (row.type <= 0) {
        if (row.isSecret) {
            if (teamRole == 'admin' || teamRole == 'manager') {
                return '<i class="ico-folder-secret"></i>'
            } else {
                if (!row.isListAcl) {
                    return '<i class="ico-folder-forbid"></i>'
                } else {
                    return '<i class="ico-folder"></i>'

                }
            }

        } else {
            return '<i class="' + (row.isShare ? 'ico-sharefolder' : 'ico-folder') + '"></i>'
        }


    }
    if (row.thumbnailUrlList &&
        row.thumbnailUrlList.length > 0 &&
        $.trim(row.thumbnailUrlList[0].thumbnailUrl)) {
        return '<i style="background:url(' + row.thumbnailUrlList[0].thumbnailUrl + ') no-repeat center center;background-size: 32px 32px;"></i>'
    }
    return '<i class="' + getFileIconClass(row.name) + '"></i>'
}

function previewFile(node) {
    var ownerId = node.ownedBy || node.ownerId;

    var nodeId = node.id;

    var preview = isPreviewable(node.name)
    var video = isVideo(node.name)
    var nodePermission = getNodePermission(ownerId, nodeId);
    if (preview) {
        gotoPageOpen(ctx + '/files/gotoPreview/' + ownerId + '/' + nodeId)
    } else if (video) {
        $.ajax({
            type: "GET",
            url: host + "/ufm/api/v2/files/" + ownerId + "/" + nodeId + "/preview",
            error: handleError,
            success: function (data) {

                var videoUrl = data.url

                var videoDialog = $("#videoDialog").dialog({title: node.name}, function () {
                    if (window.myPlayer) {
                        myPlayer.reset()
                    }
                })

                videoDialog.init();
                videoDialog.show();

                if (!window.myPlayer) {
                    window.myPlayer = videojs("my-video");
                }
                // debugger;
                //videoUrl中不包含文件后缀，所以此处需要指定type
                console.log(video)
                window.myPlayer.src({
                    src: videoUrl,
                    type: video
                })
                window.myPlayer.play();
            }
        });
    } else {
        if (nodePermission == undefined || nodePermission == null || nodePermission["download"] != 1) {
            $.Tost("该文件不支持预览，您可以联系管理员给予下载权限").show().autoHide(1000);
            return;
        } else {
            $.Tost("该文件不支持预览!").show().autoHide(1000);
        }


    }
}

function downloadFile(node) {
    var ownerId = node.ownedBy || node.ownerId;
    var nodeId = node.id || node.iNodeId;

    var nodePermission = getNodePermission(ownerId, nodeId);

    if (nodePermission == undefined || nodePermission == null || nodePermission["download"] != 1) {
        $.Alert("您没有下载权限");
        return;
    }

    $.ajax({
        type: "GET",
        url: host + "/ufm/api/v2/files/" + ownerId + "/" + nodeId + "/url",
        error: handleError,
        success: function (data) {

            $("#downloadFile").attr("href", data.downloadUrl);
            document.getElementById("downloadFile").click();

            //save to local storage.
            var downloadedFiles = localStorage.getItem("downloadedFiles");
            if (downloadedFiles != undefined || downloadedFiles != null) {
                downloadedFiles = JSON.parse(downloadedFiles);
            } else {
                downloadedFiles = [];
            }

            var info = {"name": node.name, "state": "complete", "size": node.size, "complete": 100, "createdAt": getFormatDate(new Date(), "yyyy/MM/dd")};
            downloadedFiles.push(info);
            localStorage.setItem("downloadedFiles", JSON.stringify(downloadedFiles));
        }
    });
}

function downloadFileByNodeId(ownerId, nodeId, fileName) {
    var video = isVideo(fileName)
    if (video) {
        $.ajax({
            type: "GET",
            async: false,
            url: host + "/ufm/api/v2/files/" + ownerId + "/" + nodeId + "/preview",
            error: handleError,
            success: function (data) {
                if (fileName !== undefined) {
                    var videoUrl = data.url
                    var videoDialog = $("#videoDialog").dialog({title: fileName}, function () {
                        if (window.myPlayer) {
                            myPlayer.reset()
                        }
                    })

                    videoDialog.init();
                    videoDialog.show();

                    if (!window.myPlayer) {
                        window.myPlayer = videojs("my-video");
                    }
                    // debugger;
                    //videoUrl中不包含文件后缀，所以此处需要指定type
                    console.log(video)
                    window.myPlayer.src({
                        src: videoUrl,
                        type: video
                    })
                    window.myPlayer.play();
                }

            }
        });
    } else {
        $.Tost("该文件不支持预览!").show().autoHide(1000);
    }

}

/**
 * 下载文件
 * @param nodeId
 * @param ownerId
 */
function downloadFileByNodeIdAndOwnerId(ownerId, nodeId, thisdt) {
    var fileName = $(thisdt).attr("title")
    var preview = isPreviewable(fileName)
    var video = isVideo(fileName)
    if (preview) {
        gotoPageOpen(ctx + '/files/gotoPreview/' + ownerId + '/' + nodeId)
    } else if (video) {
        $.ajax({
            type: "GET",
            url: host + "/ufm/api/v2/files/" + ownerId + "/" + nodeId + "/preview",
            error: handleError,
            success: function (data) {
                var videoUrl = data.url

                var videoDialog = $("#videoDialog").dialog({title: fileName}, function () {
                    if (window.myPlayer) {
                        myPlayer.reset()
                    }
                })

                videoDialog.init();
                videoDialog.show();

                if (!window.myPlayer) {
                    window.myPlayer = videojs("my-video");
                }
                // debugger;
                //videoUrl中不包含文件后缀，所以此处需要指定type
                console.log(video)
                window.myPlayer.src({
                    src: videoUrl,
                    type: video
                })
                window.myPlayer.play();
            }
        });
    } else {
        $.Tost("该文件不支持预览!").show().autoHide(1000);
    }
}

function getNodePermission(ownerId, nodeId, userId) {
    var permission = null;
    userId = userId || curUserId;


    var url = host + "/ufm/api/v2/permissions/" + ownerId + "/" + nodeId + "/" + userId;
    $.ajax({
        type: "GET",
        url: url,
        async: false,
        error: function (data) {
        },
        success: function (data) {
            if (typeof(data) == 'string' && data.indexOf('<html>') != -1) {
                window.location.href = ctx + "/logout";
                return;
            }
            permission = data.permissions;
        }
    });

    return permission;
}

/**
 * 创建文件夹
 * @param _ownerId
 * @param _folderId
 * @param _newName
 * @param callback
 */
function createFolder(_ownerId, _folderId, _newName, callback) {
    var _loading = $.Tost('正在创建文件夹...').show()
    var parameter = {
        parent: _folderId,
        name: _newName
    };
    $.ajax({
        type: "POST",
        url: host + "/ufm/api/v2/folders/" + _ownerId,
        data: JSON.stringify(parameter),
        error: function (request) {
            _loading.hide()
            var responseObj = $.parseJSON(request.responseText);
            switch (responseObj.code) {
                case "Forbidden" || "SecurityMatrixForbidden":
                    $.Alert("您没有权限进行该操作", "forbidden");
                    break;
                case "ExceedUserMaxNodeNum":
                    $.Alert("文件总数超过限制", "cancel");
                    break;
                case "RepeatNameConflict":
                    $.Alert("已存在相同文件名", "cancel");
                    break
                default:
                    $.Alert("操作失败", "cancel");
            }
        },
        success: function () {
            _loading.hide()
            if (callback) {
                callback();
            }
        }
    });
}

/**
 * 删除文件或文件夹
 * @param nodeId
 * @param callback
 */
function deleteFile(_ownerId, _nodeId, _callback) {
    // var _loading = $.Tost('删除中...').show()
    $.ajax({
        type: "DELETE",
        url: host + "/ufm/api/v2/nodes/" + _ownerId + "/" + _nodeId,
        data: "{}",
        error: function (data) {
            // _loading.hide()
            var status = data.status;
            if (status == 403) {
                $.Alert("您没有权限进行该操作");
            } else {
                $.Alert("操作失败，请重试");
            }
        },
        success: function (data) {
            $.Tost('删除成功', function () {
                if (_callback) {
                    _callback(data)
                }
            }).show().autoHide(2000)

        }
    });
}

/**
 * 设为快捷目录
 * @param node
 */
function addShortcutFolder(_ownerId, _node) {
    $.ajax({
        type: "POST",
        data: JSON.stringify({
            createBy: curUserId,
            ownerId: _node.ownedBy,
            nodeId: _node.id,
            type: 1
        }),
        url: host + "/ufm/api/v2/folders/" + _ownerId + "/shortcut/create",
        error: function (request, textStatus) {
            var request = jQuery.parseJSON(request.responseText).code;
            if (request == "ExsitShortcut") {
                $.Tost('快捷目录已存在').show().autoHide(1000);
            } else {
                $.Alert('操作失败');
            }
        },
        success: function (data) {
            $.Alert("操作成功");
        }
    });
}

function addShortcutTeamFolder(node) {
    var prameter = {
        createBy: curUserId,
        ownerId: node.ownedBy,
        nodeId: node.id,
        type: 2
    }
    $.ajax({
        type: "POST",
        data: JSON.stringify(prameter),
        url: host + "/ufm/api/v2/folders/" + ownerId + "/shortcut/create",
        error: function (request, textStatus) {
            switch (request.responseText) {
                case "ExsitShortcut":
                    $.Alert('快捷目录已存在');
                    break;
                default:
                    $.Alert('操作失败');
                    break;
            }
            /* $.toast('操作失败', 'forbidden');*/
        },
        success: function (data) {
            $.Tost("设为快捷目录成功").show().autoHide(2000);
        }
    });
}

function checkFileName(fileName) {
    var regEn = /[`~!@#$%^&*_+<>?:"{},.\/;']/im;
    var regCn = /[·！#￥——：；“”‘、，|《。》？、【】]/im;
    var lastname = fileName.charAt(fileName.length - 1);
    var firstname = fileName.charAt(0);
    if (regEn.test(lastname) || regCn.test(lastname)) {
        $.Alert("最后一个字符不能以特殊符号结束");
        return false;
    } else if (regEn.test(firstname) || regCn.test(firstname)) {
        $.Alert("第一个字符不能以特殊符号开头");
        return false;
    }
    return true
}

/**
 * 重命名
 * @param node
 * @param newName
 * @param _callback
 */
function renameNode(node, newName, _callback) {
    if (node.type == 1) {
        var index = node.name.lastIndexOf(".");
        if (index != -1) {
            newName = newName + node.name.substring(index);
        }
    }
    var _loading = $.Tost('正在重命名...').show()
    var parameter = {
        name: newName,
    };
    $.ajax({
        type: "PUT",
        url: host + "/ufm/api/v2/nodes/" + node.ownedBy + "/" + node.id,
        data: JSON.stringify(parameter),
        error: function () {
            _loading.hide()
            $.Alert("操作失败")
        },
        success: function () {
            _loading.hide()
            if (_callback) {
                _callback()
            }
        }
    });
}

/**
 * 恢复版本
 * @param _ownerId
 * @param _versionId
 * @param _callback
 */
function restoreVersion(_ownerId, _versionId, _callback) {
    $.ajax({
        type: "PUT",
        url: host + "/ufm/api/v2/files/" + _ownerId + "/" + _versionId + "/restore",
        error: function (data) {
            var status = data.status;
            if (status == 403) {
                $.Alert("您没有权限进行该操作");
            } else {
                $.Alert("操作失败，请重试");
            }
        },
        success: function () {
            if (_callback) {
                _callback()
            }
        }
    });
}

// 获取文件类型
function isPreviewable(fileName) {
    var index = fileName.lastIndexOf(".");
    if (index != -1) {
        var fileType = fileName.substring(index + 1).toLowerCase();
        if (fileType == "doc" || fileType == "ppt" || fileType == "xls" || fileType == "docx" || fileType == "pptx" || fileType == "xlsx" ||
            fileType == "txt" || fileType == "pdf") {
            return true
        }
    }
}

function showLinkDialog(node) {
    gotoPage(ctx + "/share/link/" + node.ownedBy + "/" + node.id);
}


function moveTo(node, targetOwnerId, targetNodeId, callback) {
    if (node.ownerId === undefined) {
        node.ownerId = node.ownedBy;
    }

    if (node.nodeId === undefined) {
        node.nodeId = node.id;
    }

    var paramss = {
        "destOwnerId": targetOwnerId,
        "destParent": targetNodeId,
        "autoRename": true
    };

    var url;
    if (node.type === 1) {
        url = host + "/ufm/api/v2/files/" + node.ownerId + "/" + node.id + "/move"
    } else {
        url = host + "/ufm/api/v2/folders/" + node.ownerId + "/" + node.id + "/move"
    }
    $.ajax({
        type: "PUT",
        url: url,
        data: JSON.stringify(paramss),
        error: handleError,
        success: function (data) {
            if (typeof callback === "function") {
                callback(data);
            }
        }
    });
}

function copyTo(node, targetOwnerId, targetNodeId, callback) {
    if (node.ownerId === undefined) {
        node.ownerId = node.ownedBy;
    }

    if (node.nodeId === undefined) {
        node.nodeId = node.id || node.iNodeId;
    }
    var url;
    if (node.type === 1) {
        url = host + "/ufm/api/v2/files/" + node.ownerId + "/" + node.nodeId + "/copy"
    } else {
        url = host + "/ufm/api/v2/folders/" + node.ownerId + "/" + node.nodeId + "/copy"
    }

    var params = {
        destParent: targetNodeId,
        destOwnerId: targetOwnerId,
        autoRename: true
    };

    $.ajax({
        type: "PUT",
        url: url,
        data: JSON.stringify(params),
        error: handleError,
        success: function (data) {
            if (typeof callback === "function") {
                callback(data);
            }
        }
    });
}

function judgeNameLength(node) {
    var names = node.name.substring(0, 20);
    if (node.name.length > 20) {
        names += '...'
    }
    return names
}

function isFilePreviewable(fileName) {
    var index = fileName.lastIndexOf(".");
    if (index !== -1) {
        var fileType = fileName.substring(index + 1).toLowerCase();
        if (fileType === "doc" || fileType === "ppt" || fileType === "xls" || fileType === "docx" || fileType === "pptx" || fileType === "xlsx" ||
            fileType === "txt" || fileType === "pdf") {
            return true
        }
    }
}

function isVideo(videoName) {
    var index = videoName.lastIndexOf(".");
    if (index !== -1) {
        var fileType = videoName.substring(index + 1).toLowerCase();
        if (fileType === "mp4" || fileType === "webm") {
            return "video/" + fileType;
        }

        if (fileType === "ogg" || fileType === "ogm" || fileType === "ogv") {
            return "video/ogg";
        }
    }

    return null;
}

function isImg(imgName) {
    var index = imgName.lastIndexOf(".");
    if (index !== -1) {
        var fileType = imgName.substring(index + 1).toLowerCase();
        if (fileType === "png" || fileType === "jpg" || fileType === "jpeg" || fileType === "bmp") {
            return true
        }
    }
}


function downLoadImg(ownerId, nodeId) {
    var imgsUrl = null
    $.ajax({
        type: "GET",
        async: false,
        url: host + "/ufm/api/v2/files/" + ownerId + "/" + nodeId + "/url",
        // error: handleError,
        success: function (data) {
            imgsUrl = data.downloadUrl
        }
    });
    return imgsUrl
}