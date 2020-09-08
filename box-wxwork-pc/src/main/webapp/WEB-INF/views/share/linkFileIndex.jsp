<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <%@ include file="../common/include.jsp" %>
    <link rel="stylesheet" type="text/css"
          href="${ctx}/static/skins/default/css/share/inputMailAccessCode.css"/>
    <title>外发文件</title>

    <style type="text/css">
        .download-option-guide-model {
            position: fixed;
            top: 0;
            right: 0;
            left: 0;
            bottom: 0;
            background: rgba(0, 0, 0, 0.5);
            z-index: 3;
        }

        .box-link-header {
            border-bottom: 1px solid #999;
        }

        .link-header {
            width: 1024px;
            margin: auto;
            height: 50px;
            overflow: hidden;
        }

        .logo-name {
            height: 50px;
            font-size: 15px;
            line-height: 50px;
            float: left;
        }

        .logo-layout {
            height: 30px;

        }

        .share-name-div {
            float: right;
            font-size: 15px;
            line-height: 50px;
        }

        .sed-out-link {
            padding-top: 50px;
        }

        .sed-out-link-details {
            font-size: 15px;
            line-height: 30px;
            text-align: center;
            color: #333333;
            margin-top: 20px;
            overflow: hidden;
            text-overflow: ellipsis
        }

        .link-file-info > span {
            padding: 10px;
            color: #999999;
            font-size: 14px
        }

        .determine-sign-in {
            width: 130px;
            height: 36px;
            background: #4F77AA;
            border-radius: 4px;
            line-height: 36px;
            color: #FFFFFF;
            text-align: center;
            position: absolute;
            left: 50%;
            -webkit-transform: translateX(-50%);
            -ms-transform: translateX(-50%);
            font-size: 16px;
            cursor: pointer;
        }
    </style>
</head>
<body>

<div class="box">
    <div class="box-link-header">
        <div class="link-header">
            <div class="logo logo-layout"></div>
            <span class="logo-name">文件宝</span>
            <div class="share-name-div">分享者: ${shareUserName}</div>
        </div>
    </div>

    <div class="fillBackground"></div>
    <div style="" class="sed-out-link">
        <div style="margin: 0 auto;width: 130px;height: 130px;background-color: #EEF2F5;">
            <div style="width: 130px;height: 130px; text-align: center;">
                <div id="iconDiv" style="margin: 5px auto; float: none; background-size: 120px 120px; width: 120px; height: 120px;">
                </div>
            </div>
        </div>
        <div class="sed-out-link-details">${iNodeName}</div>
        <div class="link-file-info" id="fileInfoDiv"></div>
        <div class="sed-out-link-tail">
            <div class="determine-sign-in" onclick="downloadFile('${iNodeName}')" id="downloadbtn">下载</div>
            <div class="determine-sign-in" onclick="previewFile()" id="previewbtn">预览</div>
            <!-- <div class="determine-sign-in" id="previewbtn">预览</div> -->
        </div>
    </div>

    <div class="weui-footer footer-layout">
        <p class="weui-footer__links">
            <a href="https://www.filepro.cn" class="weui-footer__link">企业文件宝</a>
        </p>
        <p class="weui-footer__text">版权所有 © 华一云网科技成都有限公司 2017-2018.</p>
    </div>
</div>

<a id="downloadFile" download="${iNodeName}" style="display: none"></a>

<div class="download-option-guide-model" style="display:none;">
    <img src="${ctx }/static/skins/default/img/link-model.png" style="width: 90%;margin-left: 5%;margin-top: 20%;"></img>
</div>
<div id="viewerImg"></div>
<%@ include file="../common/video.jsp" %>
</body>
<script type="text/javascript">
    var curUserId = '<shiro:principal property="cloudUserId"/>';
    var linkCode = '${linkCode}';
    var accessCode = '${accessCode}';
    var iNodeName = '${iNodeName}';
    var isLoginUser = '${isLoginUser}';
    var ownerId = '${ownerId}';
    var fileId = '${folderId}';
    var catalogData = null;
    var orderField = "modifiedAt";
    var isNeedVerify = '${isNeedVerify}';
    var shareUserName = '${shareUserName}';
    var permissionFlag = null;
    var img;
    var viewer = null;
    $(function () {
        permissionFlag = getLinkPermission(ownerId, fileId);
        if (permissionFlag.download == 1 && permissionFlag.preview == 1) {
            $("#previewbtn").css("display", "block");
            $("#previewbtn").css("top", "46px");
            $("#downloadbtn").css("display", "block");
        } else if (permissionFlag.download == 1) {
            $("#previewbtn").css("display", "none");
            $("#downloadbtn").css("display", "block");
        } else if (permissionFlag.preview == 1) {
            $("#previewbtn").css("display", "block");
            $("#downloadbtn").css("display", "none");
        }

        pageload();

        linkFileInfoInit();
        viewerImg()

    });

    function viewerImg() {
        if (isImg(iNodeName)) {
            $.ajax({
                type: "GET",
                async: false,
                url: "/ufm/api/v2/f/" + linkCode + "/url",
                beforeSend: function (xhr) {
                    xhr.setRequestHeader("Authorization", getToken());
                },
                error: function (request) {
                    if (request.status == 405) {
                        top.location.reload();
                    }
                },
                success: function (data) {

                    var imgSrc = data.downloadUrl
                    img = '<img data-original=' + imgSrc + ' src= ' + imgSrc + ' alt=' + iNodeName + ' style="display: none"/>'
                    $("#viewerImg").append(img)
                }
            });

        }
        $(".download-option-guide-model").click(function () {
            $(".download-option-guide-model").hide();
        });
    }

    function getToken() {
        var token = "link," + linkCode;

        if (accessCode !== undefined && accessCode !== null && accessCode !== "") {
            token += ("," + accessCode);
        }

        return token;
    }

    var is_qyweixin = (function () {
        return navigator.userAgent.indexOf('wxwork') !== -1;
    })();

    var is_tx = (function () {
        return (navigator.userAgent.indexOf('MicroMessenger') !== -1 && navigator.userAgent.indexOf('MQQBrowser') !== -1);
    })();

    function linkFileInfoInit() {
        $("#iconDiv").addClass(getFileIconClass(iNodeName));
        $("#fileInfoDiv").empty();
        var linkCreateTime = "${linkCreateTime}";
        linkCreateTime = getFormatDate(new Date(linkCreateTime), "yyyy-MM-dd");
        if (typeof(linkCreateTime) != "undefined" && linkCreateTime != "") {
            $("#fileInfoDiv").append("<span>" + linkCreateTime + "</span>");
        } else {
            return;
        }
        var fileSize = ${iNodeSize};
        if (fileSize == "" || typeof(fileSize) != "number") {
            return;
        }
        fileSize = formatFileSize(fileSize);
        $("#fileInfoDiv").append("|<span>" + fileSize + "</span>");
    }

    function getNodeName() {
        return '<c:out value="${iNodeName}"/>';
    }

    function getthumbnailUrl() {
        return '<c:out value="${thumbnailUrl}"/>';
    }

    function pageload() {
        if (permissionFlag != null && permissionFlag["download"] == 1) {
            $("#download-button").show();
        }
        if (isLoginUser == "true") {
            $("#MyFavorite-button").show();
            if (permissionFlag != null && permissionFlag["download"] == 1) {
                $("#saveToMe-button").show();
            }
        }
    }

    function downloadFile(name) {
        $.ajax({
            type: "GET",
            async: false,
            url: "/ufm/api/v2/f/" + linkCode + "/url",
            beforeSend: function (xhr) {
                xhr.setRequestHeader("Authorization", getToken());
            },
            error: function (request) {
                if (request.status == 405) {
                    top.location.reload();
                }
            },
            success: function (data) {
                if (typeof (data) == 'string' && data.indexOf('<html>') != -1) {
                    top.window.location.reload();
                    return;
                }
                if (!is_qyweixin && is_tx) {
                    $(".download-option-guide-model").show();
                    return;
                }
                $("#downloadFile").attr("href", data.downloadUrl);
                document.getElementById("downloadFile").click();
            }
        });
    }

    function previewFile() {
        var previewable = isFilePreviewable('${iNodeName}')
        var video = isVideo('${iNodeName}')
        var name = '${iNodeName}'
        if (previewable) {
            gotoPageOpen('/p/preview/' + linkCode) //此处使用/p绝对路径，不使用ctx路径，相关的路径转换由nginx负责完成
        } else if (video) {
            $.ajax({
                type: "GET",
                async: false,
                url: "/ufm/api/v2/f/" + linkCode + "/preview",
                beforeSend: function (xhr) {
                    xhr.setRequestHeader("Authorization", getToken());
                },
                error: function (request) {
                    if (request.status == 405) {
                        top.location.reload();
                    }
                },
                success: function (data) {
                    if (typeof (data) == 'string' && data.indexOf('<html>') != -1) {
                        top.window.location.reload();
                        return;
                    }
                    if (!is_qyweixin && is_tx) {
                        $(".download-option-guide-model").show();
                        return;
                    }
                    var videoUrl = data.url
                    var videoDialog = $("#videoDialog").dialog({title: name}, function () {
                        if (window.myPlayer) {
                            myPlayer.reset()
                        }
                    })

                    videoDialog.init()
                    videoDialog.show()

                    if (!window.myPlayer) {
                        window.myPlayer = videojs("my-video");
                    }

                    //videoUrl中不包含文件后缀，所以此处需要指定type
                    window.myPlayer.src({
                        src: videoUrl,
                        type: video
                    })
                    window.myPlayer.play();
                }
            });
        } else if (isImg(name)) {
            if (viewer !== null) {
                viewer.destroy();
            }
            viewer = new Viewer(document.getElementById('viewerImg'), {
                url: 'data-original',
                shown: function () {
                    viewer.view()
                }
            });
            viewer.show()
        } else {
            if (permissionFlag.preview == 1 && permissionFlag.download == 0) {
                $.Alert("该文件格式不支持预览，请联系分享人给予下载权限。")
            } else if (permissionFlag.download == 1 && permissionFlag.preview == 1) {
                $.Alert("该文档由于不支持预览，如果要下载，请使用第三方软件打开。")
            }
        }
    }

    function getLinkPermission(ownerId, nodeId) {
        var permission = null;
        $.ajax({
            type: "GET",
            url: host + "/ufm/api/v2/permissions/" + ownerId + "/" + nodeId,
            async: false,
            beforeSend: function (xhr) {
                xhr.setRequestHeader("Content-Type", "application/json");
                //通过Header设置鉴权信息
                xhr.setRequestHeader("Authorization", getToken());
            },
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

    var is_weixin = (function () {
        return navigator.userAgent.toLowerCase().indexOf('micromessenger/6.5') !== -1;
    })();
</script>
</html>