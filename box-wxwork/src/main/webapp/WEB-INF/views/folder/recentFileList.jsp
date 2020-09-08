    <%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
        <%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

        <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
        <html>
            <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <meta name="viewport" content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no" />
            <title>最近浏览</title>
            <%@ include file="../common/include.jsp" %>
            <link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/index.css"/>
            <link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/folder/recentFileList.css"/>
        <%--<script src="${ctx}/static/js/index.js"></script>--%>
            <script src="${ctx}/static/js/folder/recentFileList.js"></script>

            </head>
            <body style="background:#f8f8f8">
                <a id="downloadFile" download style="display:none"></a>
                <div id="list">
                    <div class="index-recent-title">
                        <span>最近浏览</span>
                    </div>
                    <div id="fileList">

                    </div>
                </div>
                <%@ include file="../common/previewImg.jsp" %>
            </body>
        <%--最近浏览模版--%>
        <script id="index_recent" type="text/template7">
            <div class="weui-cell weui-cell_swiped">
                <div class="weui-cell__bd" style="transform: translate3d(0px, 0px, 0px);" fileName="{{name}}" onclick="downloadFileByNodeIdAndOwnerId('{{id}}','{{ownedBy}}',this)">
                    <div class="weui-cell weui-cell-change">
                        <div class="weui-cell__bd" >
                            {{#js_compare "this.thumbnailUrlList.length>0"}}
                            <image class="fileImg" data-index="{{num}}" src="{{imgSrc}}" style="display: none;"></image>
                            {{/js_compare}}
                            <div class="index-recent-left">
                                {{#js_compare "this.thumbnailUrlList.length>0"}}
                                <div class="{{imgClass}}" style="background:url({{thumbnailUrlList[0].thumbnailUrl}}) no-repeat center center"></div>
                                {{else}}
                                <div class="{{imgClass}}"></div>
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
                                    <span>{{menderName}}</span>
                                    <span>|</span>
                                    <span>{{modifiedAt}}</span>
                                    <span>|</span>
                                    <span>{{size}}</span>
                                </div>
                            </div>
                            <div class="index-recent-right" id="opperation_{{id}}" >
                                <i><img src="${ctx}/static/skins/default/img/operation.png" alt=""></i>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="weui-cell__ft" id="opperations_{{id}}">
                    <a class="weui-swiped-btn index-share-btn" onclick="indexShareOpperation(this)" href="javascript:">共享</a>
                    <a class="weui-swiped-btn index-link-btn" onclick="indexShareLinkOpperation(this)" href="javascript:">外发</a>
                </div>
            </div>
        </script>
        </html>


