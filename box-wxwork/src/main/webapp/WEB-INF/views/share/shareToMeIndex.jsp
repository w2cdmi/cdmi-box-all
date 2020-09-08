<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<!DOCTYPE html>
<html>

<head>
    <meta name="viewport" content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no"/>
    <%@ include file="../common/include.jsp" %>
    <title>收到的共享</title>
    <link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/index.css"/>
    <link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/share/shareToMeIndex.css"/>
    <script src="${ctx}/static/js/share/shareToMeIndex.js"></script>
    <script src="${ctx}/static/js/common/line-scroll-animate.js"></script>
</head>

<body>
    <div>
        <div class="load">
            <div class="load-img"><img src="${ctx}/static/skins/default/img/load-rotate.png"/></div>
            <div class="load-text">正在加载</div>
        </div>
        <%--<div class="file-view-toolbar">--%>
            <%--<div class="sort-button pull-right">--%>
                <%--<div class="label">排序：</div>--%>
                <%--<div id="dateSort">日期&nbsp;</div>--%>
                <%--<div id="nameSort">名称</div>--%>
            <%--</div>--%>
        <%--</div>--%>
        <div class="fillBackground"></div>
    </div>
    <div id="box" style="top: 0.5rem;">
        <div id="shareToMeFileList">
        </div>
    </div>

    <a id="downloadFile" download style="display:none"></a>

    <script id="shareToMeFileTemplate" type="text/template7">
        <div class="weui-cell weui-cell_swiped">
            <div class="weui-cell__bd" style="transform: translate3d(0px, 0px, 0px);" id="shareToMeFiles_{{ownerId}}_{{nodeId}}" onclick="openShareFile(this)">
                <div class="weui-cell weui-cell-change">
                    <div class="weui-cell__bd">
                        <div class="index-recent-left">
                            {{#js_compare "this.imgPath!=null"}}
                            <div class="{{imgClass}}" style="background:url({{imgPath}}) no-repeat center center"></div>
                            {{else}}
                            <div class="{{imgClass}}"></div>
                            {{/js_compare}}
                        </div>
                        <div class="index-recent-middle">
                            <div class="recent-detail-name">
                                <p>{{name}}</p>
                            </div>
                            <div class="recent-detail-other">
                                <span>共享者：{{ownerName}}</span>
                                <span>|</span>
                                <span>{{modifiedAt}}</span>
                                {{#js_compare "this.size == undefined || this.size=='' || this.size==null"}}
                                <span></span>
                                {{else}}
                                <span>|</span>
                                <span>{{size}}</span>
                                {{/js_compare}}
                            </div>
                        </div>
                        <div class="index-recent-right" id="shareToMeFile_{{ownerId}}_{{nodeId}}">
                            <i><img src="${ctx}/static/skins/default/img/operation.png" alt=""></i>
                        </div>
                    </div>
                </div>
            </div>
            <div class="weui-cell__ft">
                <a class="weui-swiped-btn index-share-btn" onclick="save2PersonalFileForLineScroll(this)" style="background: #36C777" href="javascript:">另存</a>
            </div>
        </div>
    </script>

    <%--文件夹选择对话框--%>
    <div id="copyToFolderChooserDialog" class="folder-chooser-dialog" style="display:none; width: 100%; position: fixed; top: 0; bottom: 60px; left: 0; right: 0; background: #f5f5f5; z-index: 999999">
        <div class="weui-panel" style="margin:0.5rem 0;">
            <div class="weui-panel__bd">
                <div class="weui-media-box weui-media-box_small-appmsg">
                    <div class="weui-cells">
                        <div class="weui-cell" href="javascript:;">
                            <div class="weui-cell__hd">
                                <div class="index-recent-left">
                                    <div id="filesIcon" class=""></div>
                                </div>
                                <div class="index-recent-middle">
                                    <div class="recent-detail-name">
                                        <p id="fileFolderName"></p>
                                    </div>
                                    <div class="recent-detail-other">
                                        <span id="fileFolderOwnerName"></span>
                                        <span>|</span>
                                        <span id="fileFolderTime"></span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <%@ include file="../common/folderChooser.jsp" %>
    </div>
</body>

</html>
