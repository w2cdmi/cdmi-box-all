<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>收件箱</title>
    <%@ include file="../common/include.jsp"%>
    <link rel="stylesheet" href="${ctx}/static/skins/default/css/index.css">
    <link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/share/inboxFileList.css"/>
    <script src="${ctx}/static/js/common/line-scroll-animate.js"></script>
    <script src="${ctx}/static/js/share/inboxFileList.js"></script>
</head>
<body>
<div class="fs-view-header">
    <%--<div class="file-view-toolbar">--%>
        <%--<div class="sort-button pull-right">--%>
            <%--<div class="label">排序：</div>--%>
            <%--<div id="dateSort">日期&nbsp;</div>--%>
            <%--<div id="nameSort">名称</div>--%>
        <%--</div>--%>
    <%--</div>--%>
        <div style="overflow: hidden;background: #f8f8f8">
            <div style="overflow: hidden;position: relative;left: 80%;margin-bottom: 0.3rem;margin-top: 0.3rem;" class="folder-order" id="folderOrder">
                <i class=""><img src="${ctx}/static/skins/default/img/sort.png"/></i>
                <div class="weui-cells weui-cells_radio change-cells-radio" id="sortRadio" style="display:none;position: fixed;right: 0.7rem">
                    <label class="weui-cell weui-check__label" for="x11" id="dateSort">
                        <div class="weui-cell__bd change-cells-bd" >
                            <p>日期</p>
                        </div>
                        <div class="weui-cell__ft">
                            <input type="radio" class="weui-check" name="radio1" id="x11" checked="checked">
                            <span class="all-sort-img"><i class=""></i></span>
                        </div>
                    </label>
                    <label class="weui-cell weui-check__label" for="x12" id="nameSort">
                        <div class="weui-cell__bd change-cells-bd">
                            <p>名称</p>
                        </div>
                        <div class="weui-cell__ft">
                            <input type="radio" name="radio1" class="weui-check" id="x12" >
                            <span class="all-sort-img"><i></i></span>
                        </div>
                    </label>
                </div>
            </div>
        </div>

    <a id="downloadFile" download style="display:none"></a>
    <div class="bread-crumb">
		<div class="bread-crumb-content" id="directory">
		</div>
	</div>
</div>

<div class="fs-view-file-list" id="fileListWrapper" style="background-size: 5rem 5rem;">
    <div class="weui-pull-to-refresh__layer">
        <div class="weui-pull-to-refresh__preloader"></div>
        <div class="up">释放刷新</div>
        <div class="refresh">正在刷新</div>
    </div>
    <div id="fileList"></div>
</div>

<div class="new-share-recive" onclick="sendLinkAgain(${ownerId}, ${parentId})"><p>继续收集</p></div>

<script id="fileTemplate" type="text/template7">
    <div class="weui-cell weui-cell_swiped" value = "{{id}}" name="{{modifiedBy}}" fileName="{{name}}">
        <div class="weui-cell__bd" style="transform: translate3d(0px, 0px, 0px);" id="files_{{id}}" onclick="optionInode(this)">
            <div class="weui-cell weui-cell-change">
                <div class="weui-cell__bd" >
                    {{#js_compare "this.imgPath!=null"}}
                    <image class="fileImg" data-index="{{num}}" src="{{imgSrc}}" style="display: none;"></image>
                    {{/js_compare}}
                    <div class="index-recent-left">
                        {{#js_compare "this.imgPath!=null"}}
                        <div class="{{divClass}}" style="background:url({{imgPath}}) no-repeat center center"></div>
                        {{else}}
                        <div class="{{divClass}}"></div>
                        {{/js_compare}}
                    </div>
                    <div class="index-recent-middle">
                        <div class="recent-detail-name">
                            <p>{{name}}</p>
                            {{#js_compare "this.isShare == true"}}
                            <i><img src="${ctx}/static/skins/default/img/isShare.png" alt=""></i>
                            {{/js_compare}}
                            {{#js_compare "this.isSharelink == true"}}
                            <i><img src="${ctx}/static/skins/default/img/link_share.png" alt=""></i>
                            {{/js_compare}}
                        </div>
                        <div class="recent-detail-other">
                            <span>{{size}}</span>
                            <span>|</span>
                            <span>{{modifiedAt}}</span>
                        </div>
                    </div>
                    <div class="index-recent-right" id="file_{{id}}" >
                        <i><img src="${ctx}/static/skins/default/img/operation.png" alt=""></i>
                    </div>
                </div>
            </div>
        </div>
        <div class="weui-cell__ft" ownedBy={{ownedBy}} nodeId={{id}}>
            <a class="weui-swiped-btn index-share-btn" onclick="saveToPersonal({{id}})" href="javascript:">转存</a>
        </div>
    </div>
</script>

<%--文件夹选择对话框--%>
<div id="folderChooserDialog" class="folder-chooser-dialog" style="display:none; width: 100%; position: fixed; top: 0; bottom: 60px; left: 0; right: 0; background: #f5f5f5; z-index: 999999">
    <div class="weui-panel" style="margin:0.5rem 0;">
        <div class="weui-panel__bd">
            <div class="weui-media-box weui-media-box_small-appmsg">
                <div class="weui-cells">
                    <div class="weui-cell" href="javascript:;">
                        <div class="weui-cell__hd folder-icon"></div>
                        <div class="weui-cell__bd weui-cell_primary">
                            <p style="color:#333;font-size:0.95rem;margin-left: 0.5rem"><span class="upload-file-name">请选择目标文件夹</span></p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <%@ include file="../common/folderChooser.jsp" %>
</div>
<%@ include file="../common/previewImg.jsp" %>
<%@ include file="../common/previewVideo.jsp" %>
</body>
</html>
