<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%--上传按钮--%>
<input id="fileUpload" type="file" name="file" onchange="selectTargetFolder()" multiple="multiple" hidden />

<%--文件夹选择对话框--%>
<div id="folderChooserDialog" class="folder-chooser-dialog" style="display:none; width: 100%; position: fixed; top: 0; bottom: 60px; left: 0; right: 0; background: #f5f5f5; z-index: 999999">
    <div class="weui-panel" style="margin:0.5rem 0;">
        <div class="weui-panel__bd">
            <div class="weui-media-box weui-media-box_small-appmsg">
                <div class="weui-cells">
                    <a class="weui-cell weui-cell_access" href="javascript:;">
                        <div class="weui-cell__hd file-undefined"></div>
                        <div class="weui-cell__bd weui-cell_primary">
                            <p style="color:#333;font-size:0.95rem;margin-left: 0.5rem"><span id="selectedFilesLabel" class="upload-file-name"></span><span id="selectedFilesLabelLength"></span></p>
                        </div>
                    </a>
                </div>
            </div>
        </div>
    </div>
    <%@ include file="../common/folderChooser.jsp" %>
</div>

<%--顶部上传进度条--%>
<div id="entireProgressBarList" style="display:none; width: 100%; position: fixed; top: 0; left: 0; right: 0;  z-index: 999999">
</div>

<%--显示在文件列表中的进度条--%>
<script id="entireProgressBarTemplate" type="text/template7">
    <div class="file-uploading" id="entireProgressBar_{{id}}" style="width: 100%; height: 20px; z-index: 999999">
        <div class="midd" style="background: #fff;font-size: 0.7rem">
            <div style="overflow: hidden;line-height: 1.3rem">
                <div class="time" style="float: left;margin-left: 0.8rem;">预计剩余时间：<span class="left-time">-</span></div>
                <div class="info"></div>
                <div class="speed" style="float: right;margin-right: 0.8rem;">0</div>
            </div>
            <div class="bar" style="background: #0bb20c;padding-left:0.8rem">
                <div class="progress">0%</div>
            </div>


        </div>
    </div>
</script>

<%--显示在文件列表中的进度条--%>
<script id="uploadFileProgressTemplate" type="text/template7">
    <div class="weui-cell weui-cell_swiped file-uploading" value = "{{id}}" name="{{modifiedBy}}" fileName="{{name}}">
        <div class="weui-cell__bd" style="transform: translate3d(0px, 0px, 0px); background: #C0BFC4" id="files_{{id}}">
            <div class="weui-cell weui-cell-change">
                <div class="weui-cell__bd" style="position: relative">
                    <div class="index-recent-left">
                        <div class="{{divClass}}"></div>
                    </div>
                    <div class="index-recent-middle">
                        <div class="recent-detail-name">
                            <p>{{name}}</p>
                        </div>
                        <div class="recent-detail-other">
<%--
                            <span>{{menderName}}</span>
                            <span>|</span>
                            <span>{{modifiedAt}}</span>
                            <span>|</span>
--%>
                            <span>{{fileSize}}</span>
                            <%--上传信息--%>
                            <span class="progress">0%</span>
                            <span>|</span>
                            <span class="speed"></span>
                            <span>|</span>
                            <span class="info">正在上传</span>
                        </div>
                    </div>
<%--
                    <div class="index-recent-right" id="file_{{id}}" >
                        <i><img src="${ctx}/static/skins/default/img/operation.png" alt=""></i>
                    </div>
--%>
                    <div class="bar" style="position:absolute; bottom:-5px; height: 5px; background: green; overflow: hidden"></div>
                </div>
            </div>
        </div>
        <div class="weui-cell__ft" ownedBy={{ownedId}} nodeId={{nodeId}}>
            <a class="weui-swiped-btn index-share-btn" onclick="swipeShareDialog(this)" href="javascript:">共享</a>
            <a class="weui-swiped-btn index-link-btn" onclick="swipeLinkDialog(this)" href="javascript:">外发</a>
        </div>
    </div>
</script>

<%--初始化uploader.js使用的变量--%>
<script type="text/javascript">
    <%--所在的空间对应的cloudUserId，包括个人空间和团体空间--%>
    var __ownerId__ = "${ownerId}" || ownerId || curUserId;

    //当前所在的路径，相当于pwd
    var __parentId__ = "${parentId}" || 0;

    //所在的根节点ID，一般情况为0。对于他人分享出来的目录，所能到达的rootId为分享目录的nodeId.
    var __rootId__ = "${rootId}" || 0;
</script>

<%--引用uploader.js--%>
<script src="${ctx}/static/js/common/uploader.js"></script>