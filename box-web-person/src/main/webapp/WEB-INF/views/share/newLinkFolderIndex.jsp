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
    <link rel="stylesheet" href="${ctx}/static/jquery-weui/lib/weui.min.css">
    <link rel="stylesheet" href="${ctx}/static/jquery-weui/css/jquery-weui.css">
    <link rel="stylesheet" href="${ctx}/static/skins/default/css/main.css">
    <link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/folder/folderIndex.css"/>
    <link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/share/inputMailAccessCode.css"/>
    <script src="${ctx}/static/jquery-weui/lib/jquery-2.1.4.js"></script>
    <script src="${ctx}/static/jquery-weui/js/jquery-weui.js"></script>
    <script src="${ctx}/static/jquery/validate/jquery.validate.min.js"></script>
    <script src="${ctx}/static/js/common/line-scroll-animate.js"></script>
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

        /* .bread-crumb{
            overflow: scroll;
        } */
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
        <div class="logo logo-layout"></div>
        <span class="logo-name">企业文件宝</span>
        <div class="share-name-div">分享者: ${shareUserName}</div>
    </div>
    <div class="fillBackground"></div>
    <div id="uploadDiv" style="display: none">
        <div class="sed-out-link">
            <div>
                <div
                        style="width: 6.5rem; height: 6.5rem; background-color: #EEF2F5; margin: 0 auto;">
                    <img src="${ctx}/static/skins/default/img/icon/folder-icon.png"
                         style="width: 2.4rem; height: 2.4rem; margin-left: 2rem; margin-top: 2rem;"/>
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
             style="background-size: 5rem 5rem; top: 5.2rem">
            <div class="weui-pull-to-refresh__layer">
                <div class="weui-pull-to-refresh__preloader"></div>
                <div class="up" style="text-align: center;">释放刷新</div>
                <div class="refresh" style="text-align: center;">正在刷新</div>
            </div>

            <div id="fileList" style="padding-left: 0.4rem;"></div>
        </div>
    </div>

    <div class="weui-footer footer-layout">
        <p class="weui-footer__links">
            <a href="https://www.filepro.cn/wxwork" class="weui-footer__link">华一云网</a>
        </p>
        <p class="weui-footer__text">Copyright © 2017-2018 filepro.cn</p>
    </div>
</div>

<script id="fileTemplate" type="text/template7">
    <div class="file line-content" id="file_{{id}}" style="border-bottom: 1px solid #E4E1E1;" onclick="optionInode(this)">
        <div class="file-info">
            {{#js_compare "this.imgPath==null"}}
            <div class="img {{divClass}}"></div>
            {{else}}
            <div class="img {{divClass}}" style="background:url({{imgPath}}) no-repeat center center;"></div>
            {{/js_compare}}
            <div>
                <div class="fileName">{{name}}</div>
                {{#js_compare "this.type==1"}}
                <span>{{size}}</span><span> | </span><span>{{modifiedAt}}</span>
                {{else}}
                </span><span>{{modifiedAt}}</span>
                {{/js_compare}}
            </div>
            {{#js_compare "this.type==1"}}
            {{#js_compare "this.permissionFlag.download==1"}}
            <div class="img" fileId={{id}} style="width:1.0rem;margin-top:1.5rem;height:1.0rem;float:left;background:url(${ctx}/static/skins/default/img/download.png) no-repeat center center;background-size:100% 100%; " onclick="downloadFile(this)"></div>
            {{/js_compare}}
            {{/js_compare}}
        </div>
    </div>
</script>

<div class="download-option-guide-model" style="display:none;">
    <img src="${ctx }/static/skins/default/img/link-model.png" style="width: 90%;margin-left: 5%;margin-top: 20%;"></img>
</div>
</body>

<script type="text/javascript">
    var ownerId = '${ownerId}';
    var parentId = '${parentId}';
    var fileId = '${folderId}';

    var linkCode = '${linkCode}';
    var accessCode = '${accessCode}';
    var isLoginUser = '${isLoginUser}';
    var shareUserName = '${shareUserName}';
    var catalogData = null;
    var isFolderIsUploading = false;
    var token = "${token}";
    var ctx = "${ctx}"
    var host = "";//
    var permissionFlag = getLinkPermission(ownerId, parentId, linkCode);

    $(function () {
        if (permissionFlag.upload == true) {
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
        for (var i = 0, file; file = oFiles[i]; i++) {
            var formData = new FormData();
            formData.append(file.name, file);

            var params = {
                "parentId": parentId,
                "name": file.name,
                "size": file.size
            }

            $.ajax({
                url: host + "/ufm/api/v2/files/" + ownerId,
                type: "PUT",
                data: JSON.stringify(params),
                beforeSend: function(xhr) {
                    //通过Header设置鉴权信息
                    xhr.setRequestHeader("Authorization", getToken());
                },
                success: function (data, textStatus, jqXHR) {
                    var time = addUploadingFileInfoProgressDiv(file.name);
                    var preUrl = data.uploadUrl + "?objectLength=" + file.size;
                    $.ajax({
                        url: preUrl,
                        type: "POST",
                        data: formData,
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
                            $.alert("文件上传成功");
                        },
                        error: function () {
                            $("#state_" + time).html("失败");
                            $.alert("上传存储失败");
                        }
                    });
                },
                error: function (request) {
                    $.alert("获取上传地址失败");
                }
            });
        }
    }

    function downloadFile(th) {
        var id = $(th).attr("fileId");
        $.ajax({
            type: "GET",
            url: host + "/ufm/api/v2/files/" + ownerId + "/" + id + "/url",
            beforeSend: function(xhr) {
                //通过Header设置鉴权信息
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

                $("#downloadFile").attr("href", data);
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
    var __page = 1;
    var pageSize = getStorage("fileListPageSize", 40);

    var __loadmore = false;
    /* 正在加载 */
    var __loading = false;

    /*排序字段，["modifiedAt", "name", "size"]*/
    var orderField = getCookie("orderField", "modifiedAt");
    /*是否倒序*/
    var order = getCookie("sortOrder", "DESC");
    /*文件列表显示方式：列表或缩略图*/
    var listViewType = getCookie("listViewType", "list");

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
                //通过Header设置鉴权信息
                xhr.setRequestHeader("Authorization", getToken());
            },
            error: function () {
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
                    $list.parent().css('background-size', '5rem 5rem');
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

        var nodePermission = getLinkPermission(ownerId, parentId, linkCode);
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
        if (node.type == 0) {
            openFolder(node);
        } else {
            /*  downloadFile(node); */
        }
    }

</script>
</html>
