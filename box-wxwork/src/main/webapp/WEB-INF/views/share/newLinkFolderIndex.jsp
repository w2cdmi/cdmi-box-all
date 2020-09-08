<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="pw.cdmi.box.disk.utils.CSRFTokenManager" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%
    request.setAttribute("token", CSRFTokenManager.getTokenForSession(session));
%>
<!DOCTYPE html>
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
</style>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no"/>
    <link rel="stylesheet" href="https://cdn.bootcss.com/weui/1.1.2/style/weui.min.css">
    <link rel="stylesheet" href="https://cdn.bootcss.com/jquery-weui/1.2.0/css/jquery-weui.min.css">
    <link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/index.css"/>
    <link rel="stylesheet" href="${ctx}/static/skins/default/css/main.css">
    <link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/folder/folderIndex.css"/>
    <link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/share/inputMailAccessCode.css"/>
    <link rel="stylesheet" href="${ctx}/static/photoSwipe/css/photoswipe.css" >
    <link rel="stylesheet" href="${ctx}/static/photoSwipe/css/default-skin/default-skin.css">
    <link rel="stylesheet" href="${ctx}/static/video/zy.media.min.css" >
    <script src="${ctx}/static/jquery-weui/lib/jquery-2.1.4.js"></script>
    <script src="https://cdn.bootcss.com/jquery-weui/1.2.0/js/jquery-weui.min.js"></script>
    <script src="${ctx}/static/jquery/validate/jquery.validate.min.js"></script>
    <script src="${ctx}/static/js/common/line-scroll-animate.js"></script>
    <script src="${ctx}/static/photoSwipe/js/photoswipe.min.js"></script>
    <script src="${ctx}/static/photoSwipe/js/photoswipe-ui-default.min.js"></script>
    <script src="${ctx}/static/video/zy.media.min.js"></script>
    <script type="text/javascript">
        var corpId = null;
    </script>
    <script src="${ctx}/static/js/common/common.js"></script>
    <title>收件箱</title>

    <style type="text/css">
        .box {
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
        }

        .sed-out-link {
            padding-top: 1rem;
        }

        <%--.bread-crumb{--%>
        <%--overflow: scroll;--%>
        <%--}--%>
        #uploadFileList > div {
            margin-top: 0.5rem;
        }
    </style>
</head>
<body>
<input id="fileUpload" type="file" name="file[]" onchange="uploadFile()" multiple="multipart" hidden/>
<a id="downloadFile" download style="display: none"></a>
<div class="box">
    <div class="link-header">
        <div class="logo logo-layout" style="width: 8rem; background-size:8rem 1.5rem"></div>
        <div class="share-name-div">分享者: ${shareUserName}</div>
    </div>
    <div class="fillBackground"></div>
    <div id="uploadDiv" style="display: none">
        <div class="sed-out-link">
            <div>
                <div style="width: 6.5rem; height: 6.5rem; background-color: #EEF2F5; margin: 0 auto;">
                    <img src="${ctx}/static/skins/default/img/icon/folder-icon.png" style="width: 2.4rem; height: 2.4rem; margin-left: 2rem; margin-top: 2rem;"/>
                </div>
            </div>
            <div class="sed-out-link-span" style="margin-top: 0.5rem">${iNodeName}</div>

            <div class="upload-fileList" id="uploadFileList"></div>
            <div class="sed-out-link-tail">
                <div class="determine-sign-in" id="upLoad">上传</div>
            </div>
        </div>
    </div>

    <div id="downloadDiv" style="display: none; width: 100%; height: 1.7rem;">
        <div class="bread-crumb">
            <div class="bread-crumb-content" id="directory">
                <a onclick="jumpFolder(this, ${parentId});">${iNodeName}</a>
            </div>
        </div>
        <div class="fs-view-file-list" id="fileListWrapper"
             style="background-size: 5rem 5rem; top: 6.2rem">
            <div class="weui-pull-to-refresh__layer">
                <div class="weui-pull-to-refresh__preloader"></div>
                <div class="up" style="text-align: center;">释放刷新</div>
                <div class="refresh" style="text-align: center;">正在刷新</div>
            </div>

            <div id="fileList"></div>
        </div>
    </div>

    <div class="weui-footer footer-layout">
        <p class="weui-footer__text"><spring:message code='corpright'/></p>
    </div>
</div>

<script id="fileTemplate" type="text/template7">
    <div class="weui-cell weui-cell_swiped">
        <div class="weui-cell__bd" style="transform: translate3d(0px, 0px, 0px);" id="file_{{id}}"  onclick="optionInode(this)">
            <div class="weui-cell weui-cell-change">
                <div class="weui-cell__bd" >
                    {{#js_compare "this.imgPath!=null"}}
                    <image class="fileImg" data-index="{{num}}" src="{{imgSrc}}" style="display: none;"></image>
                    {{/js_compare}}
                    <div class="index-recent-left">
                        {{#js_compare "this.imgPath==null"}}
                        <div class="img {{divClass}}"></div>
                        {{else}}
                        <div class="img {{divClass}}" style="background:url({{imgPath}}) no-repeat center center;"></div>
                        {{/js_compare}}
                    </div>
                    <div class="index-recent-middle">
                        <div class="recent-detail-name">
                            <p>{{name}}</p>
                        </div>
                        <div class="recent-detail-other">
                            <span>{{modifiedAt}}</span>
                            {{#js_compare "this.size == undefined || this.size=='' || this.size==null"}}
                            <span></span>
                            {{else}}
                            <span>|</span>
                            <span>{{size}}</span>
                            {{/js_compare}}
                        </div>
                    </div>
                    <%--<div class="index-recent-right" >--%>
                        <%--{{#js_compare "this.type==1"}}--%>
                        <%--{{#js_compare "this.permissionFlag.download==1"}}--%>
                        <%--<div class="img" fileId={{id}} ownerId={{ownedBy}} style="margin-top:0.5rem;margin-right:0.4rem;height:1.0rem;float:left;font-size:0.65rem" onclick="downloadFile(this)">下载</div>--%>
                        <%--{{/js_compare}}--%>
                        <%--&lt;%&ndash;{{#js_compare "this.permissionFlag.preview==1"}}&ndash;%&gt;--%>
                        <%--&lt;%&ndash;<div class="img" fileId={{id}} ownerId={{ownedBy}} fileName='{{name}}' style="margin-top:0.5rem;height:1.0rem;float:left;font-size:0.65rem;margin-right: 0.4rem" onclick="previewFile(this)">预览</div>&ndash;%&gt;--%>
                        <%--&lt;%&ndash;{{/js_compare}}&ndash;%&gt;--%>
                        <%--{{/js_compare}}--%>
                    <%--</div>--%>
                </div>
            </div>
        </div>
    </div>

</script>

<div class="download-option-guide-model" style="display:none;">
    <img src="${ctx }/static/skins/default/img/link-model.png" style="width: 90%;margin-left: 5%;margin-top: 20%;"></img>
</div>
<%@ include file="../common/previewImg.jsp" %>
<%@ include file="../common/previewVideo.jsp" %>
</body>

<script type="text/javascript">
    var currentPage = 1;
    var parentId = '${parentId}';
    var linkCode = '${linkCode}';
    var accessCode = '${accessCode}';

    var isLoginUser = '${isLoginUser}';
    var ownerId = '${ownerId}';
    var shareUserName = '${shareUserName}';
    var fileId = '${folderId}';
    var catalogData = null;
    var orderField = "modifiedAt";
    var isFolderIsUploading = false;
    var token = "${token}";
    var userToken = "${token}";
    var ctx = "${ctx}";
    var successPost = null;
    var host="";
    var permissionFlag = getLinkPermission(ownerId, parentId, linkCode, accessCode);

    $(function () {
        if (permissionFlag.upload === 1) {
            $("#uploadDiv").css("display", "block");
            document.title = '收件箱';
        } else {
            $("#downloadDiv").css("display", "block");
            document.title = '外发';
            init();
        }
        $("#upLoad").click(function () {
            $("#fileUpload").click();
        });

    });

    function getToken() {
        var token = "link," + linkCode;

        if (accessCode !== undefined && accessCode !== null && accessCode !== "") {
            token += ("," + accessCode);
        }

        return token;
    }

    function uploadFile() {
        $(".uploading-file-model").show();
        var oFiles = document.querySelector("#fileUpload").files;
        // 遍历文件列表，插入到表单数据中 (暂时只支持单文件上传)

        for (var i = 0; i < oFiles.length; i++) {
            var file =  oFiles[i];

            var params = {
                "parent": parentId,
                "name": file.name,
                "size": file.size
            }

            $.ajax({
                url: host + "/ufm/api/v2/files/" + ownerId,
                type: "PUT",
                async: false,
                data: JSON.stringify(params),
                beforeSend: function(xhr) {
                    xhr.setRequestHeader("Content-Type","application/json")
                    //通过Header设置鉴权信息
                    xhr.setRequestHeader("Authorization", getToken());
                },
                success: function (data, textStatus, jqXHR) {
                    executeUploadFile(data.uploadUrl, file);
                },
                error: function (request) {
                    if (request.status == 404) {
                        $.alert("收件箱已经删除");
                    } else if (request.status == 403) {
                        $.alert("当前时间已超时，请刷新页面！");
                    } else {
                        $.alert("获取上传地址失败");
                    }
                }
            });
        }
    }

    function executeUploadFile(url, file) {
        var time = addUploadingFileInfoProgressDiv(file.name);
        var preUrl = url + "?objectLength=" + file.size;
        var formData = new FormData();
        formData.append(file.name, file);
        $.ajax({
            url: preUrl,
            type: "POST",
            data: formData,
            beforeSend: function(xhr) {},
            processData: false,	// 告诉jQuery不要去处理发送的数据
            contentType: false, // 告诉jQuery不要去设置Content-Type请求头
            xhr: function () {
                var xhr = new window.XMLHttpRequest();
                xhr.upload.addEventListener("progress", function (e) {
                    var percent = (e.loaded / e.total) * 100;
                    $('#prg_' + time).width(percent.toFixed(2) + "%");
                }, false);
                return xhr; //xhr对象返回给jQuery使用
            },
            success: function (data) {
                $("#state_" + time).html("成功");
            },
            error: function () {
                $("#state_" + time).html("失败");
                $.alert("上传存储失败");
            }
        });
    }

    function downloadFile(th) {
        var id = $(th).attr("fileId");
        var ownerId = $(th).attr("ownerId");
        $.ajax({
            type: "GET",
            async: false,
            url: "/ufm/api/v2/files/" + ownerId + "/" + id + "/url",
            beforeSend: function (xhr) {
                xhr.setRequestHeader("Authorization", getToken());
            },
            error: function (request) {
                $.toast("下载失败");
            },
            success: function (data) {
                if (is_weixin) {
                    $(".download-option-guide-model").show();
                    return;
                }

                $("#downloadFile").attr("href", data.downloadUrl);
                document.getElementById("downloadFile").click();
            }
        });
    }

    var is_weixin = (function () {
        return navigator.userAgent.toLowerCase().indexOf('micromessenger/6.5') !== -1;
    })();

    function addUploadingFileInfoProgressDiv(fileName) {
        var time = (new Date()).getTime();
        var html = "";
        html += "<div>";
        html += "<span>" + fileName + "</span>";
        html += "<span style='float: right;padding-right: 0.5rem;' id='state_" + time + "'></span>";
        html += "<div class='weui-progress'>";
        html += "<div class='weui-progress__bar'>";
        html += "<div class='weui-progress__inner-bar js_progress' id='prg_" + time + "' style='width: 0%;'></div>";
        html += "</div>";
        html += "</div>";
        html += "</div>";
        $("#uploadFileList").append(html);
        return time;
    }

    ////file-view
    /*分页相关定义*/
    /*排序字段，["modifiedAt", "name", "size"]*/
    var orderField = getCookie("orderField", "modifiedAt");
    /*是否倒序*/
    var order = getCookie("sortOrder", "DESC");
    /*文件列表显示方式：列表或缩略图*/
    var listViewType = getCookie("listViewType", "list");
    /* 正在加载 */
    var __loading = false;

    var __page = 1;
    var pageSize = getStorage("fileListPageSize", 40);
    var __loadmore = false;
    var num = -1
    /*
      js中使用的parentId在include.jsp中定义。
     */

    /*
    此方法为文件列表的初始化方法，各个界面应该调用此方法。
     */
    function init() {
        //文件列表显示方式
        if (listViewType == "list") {
            $("#viewTypeBtnList").addClass("active");
        } else {
            $("#viewTypeBtnThumbnail").addClass("active");
        }

        //为面包屑增加滑动效果
        //$("#directory").addTouchScrollAction();


        //下拉刷新
        var $listWrapper = $("#fileListWrapper");
        $listWrapper.pullToRefresh().on("pull-to-refresh", function () {
            //console.log("pulltorefresh triggered...");
            listFile(parentId, __page);
            setTimeout(function () {
                $("#fileListWrapper").pullToRefreshDone();
            }, 200);
        });

        //上滑加载
        $listWrapper.infinite().on("infinite", function () {
            if (__loading) return;

            if (__loadmore) {
                __loading = true;
                $.showLoading();
                listFile(parentId, ++__page);
                setTimeout(function () {
                    __loading = false;
                    $.hideLoading();
                }, 200);
            }
        });

        listFile(parentId, 1);
    }

    function listFile(folderId, page) {
        parentId = folderId || parentId;
        __page = page || 1;

        var url = host + "/ufm/api/v2/folders/" + ownerId + "/" + parentId + "/items";
        var params = {
            offset: (__page - 1) * pageSize,
            limit: pageSize,
            order: [{ field: 'type' , direction: "ASC" },{ field: orderField, direction: order }],
            thumbnail: [{ width: 96, height: 96 }, { width: 250, height: 200 }]
        };
        $.ajax({
            url: url,
            type: "POST",
            data: JSON.stringify(params),
            beforeSend: function(xhr) {
                xhr.setRequestHeader("Content-Type","application/json")
                //通过Header设置鉴权信息
                xhr.setRequestHeader("Authorization", getToken());
            },
            error: function (xhr, status, error) {
            },
            success: function (data) {
                __page = Math.ceil(data.totalCount / pageSize);
                __loadmore = data.totalCount > __page * pageSize;

                var fileList = data.folders.concat(data.files);
                var $list = $("#fileList");
                var $template = $("#fileTemplate");

                //加载第一页，清除以前的记录
                if (__page == 1) {
                    $list.children().remove();
                }
                if (fileList.length == 0) {
                    $list.parent().css("background", "url('" + ctx + "/static/skins/default/img/iconblack_17.png')no-repeat center center");
                    $list.parent().css('background-size', '7rem 8rem');
                } else {
                    $list.parent().css('background', '')
                }
                for (var i in fileList) {
                    var item = fileList[i];
                    if (item.type == 1) {
                        item.size = formatFileSize(item.size);
                        if (typeof(item.thumbnailUrlList) != "undefined" && item.thumbnailUrlList.length > 0) {
                            item.imgPath = item.thumbnailUrlList[1].thumbnailUrl;
                        }
                        if(isImg(item.name)){
                            num++;
                            var index = item.thumbnailUrlList[0].thumbnailUrl.lastIndexOf("/");
                            var imgSrc = item.thumbnailUrlList[0].thumbnailUrl.substring(0,index)
                            item.imgSrc = imgSrc;
                            item.num = num
                        }
                    } else {
                        item.size = "";
                    }
                    item.permissionFlag = permissionFlag;
                    item.modifiedAt = getFormatDate(new Date(item.modifiedAt), "yyyy/MM/dd");
                    item.divClass = getImgHtml(item.type, item.name, item.shareStatus);
                    $template.template(item).appendTo($list);
                    //设置数据\n
                    var $row = $("#file_" + item.id);
                    $row.data("node", item);
                }
                num = -1

            },
            complete: function () {
                $('.load').css('display', 'none');
            }
        });
    }

    function openFolder(node) {
        $('.load').css('display', 'block');
        $("#fileList").children().remove();
        parentId = node.id;
        $("#directory").append("<span>&nbsp;>&nbsp;</span><a onclick=\"jumpFolder(this," + node.id + ");\">&nbsp;" + node.name + "&nbsp;</a>");

        var nodePermission = getLinkPermission(ownerId, parentId, linkCode);;
        if (nodePermission["browse"] != 1) {
            $.toast("您没有权限进行该操作", "forbidden");
            return;
        }
        listFile(parentId, 1);
    }

    /*面包屑标签点击跳转*/
    function jumpFolder(th, folderId) {
        $(th).nextAll().remove();
        parentId = folderId;
        listFile(folderId, 1);
    }

    /* 文件列表中的对象操作 */
    function optionInode(t) {
        var node = $(t).data("node");
        console.log(node)
        if (node.type == 0) {
            openFolder(node);
        } else {
            var previewable = isFilePreviewable(node.name);
            var imgable = isImg(node.name);
            var videoable = isVideo(node.name);
            var pla=ismobile(1);
            if(previewable){
                gotoPage('/p/preview/' + linkCode + '?ownerId=' + node.ownedBy + '&nodeId=' + node.id)
            }else if(imgable){
                imgClick(t)
            }else if(videoable && pla=="1"){
                $.ajax({
                    type: "GET",
                    async: false,
                    url: "/ufm/api/v2/files/" + node.ownedBy + "/" + node.id + "/preview",
                    beforeSend: function (xhr) {
                        xhr.setRequestHeader("Authorization", getToken());
                    },
                    error: function (request) {
                        $.toast("下载失败");
                    },
                    success: function (data) {
                        var url = data.url
                        $("video").attr("src",url)
                        $("video").attr("type",videoable)
                        $(".playvideo").show()
                        $("video").get(0).play()
                        zymedia('video',{autoplay: true});
                        $("#modelView").click(function () {
                            $(".playvideo").hide()
                            $("video").get(0).currentTime = 0
                            $("video").get(0).pause()
                        })
                    }
                });
            }else{
                $.alert("该文件格式不支持预览")
//                if (permissionFlag.preview == 1 && permissionFlag.download == 0) {
//                    $.alert("该文件格式不支持预览，请联系分享人给予下载权限。")
//                } else if (permissionFlag.download == 1 && permissionFlag.preview == 1) {
//                    $.alert("该文档由于不支持预览，如果要下载，请使用第三方软件打开。")
//                }
            }
        }
    }

</script>
</html>
