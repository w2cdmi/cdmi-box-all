<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<!DOCTYPE html>
<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <%@ include file="../common/include.jsp" %>
    <link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/index.css"/>
    <link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/folder/listReciver.css"/>
    <title>收件箱</title>
</head>

<body>
<div style="width:100%;height:0.5rem;background:#f5f5f5"></div>
<div id="box" style="top:0.5rem">
    <div id="folders">
    </div>
</div>
<div class="new-share-recive"><p>新建收件箱</p></div>

<script id="receiveFolderItemTemplate" type="text/template">
    <div class="weui-cell weui-cell_swiped folder-item">
        <div class="weui-cell__bd" style="transform: translate3d(0px, 0px, 0px);" onclick="gotoPage('${ctx}/share/inboxFileList/{{ownedBy}}/{{id}}')">
            <div class="weui-cell weui-cell-change">
                <div class="weui-cell__bd">
                    <div class="index-recent-left">
                        <div class="img {{divClass}}"></div>
                    </div>
                    <div class="index-recent-middle">
                        <div class="recent-detail-name">
                            <p>{{name}}</p>
                        </div>
                        <div class="recent-detail-other">
<%--
                            <span>{{ownedBy}}</span>
                            <span>|</span>
--%>
                            <span>{{createdAt}}</span>
<%--
                            {{#js_compare "this.size == undefined || this.size=='' || this.size==null"}}
                            <span></span>
                            {{else}}
                            <span>|</span>
                            <span>{{size}}</span>
                            {{/js_compare}}
--%>
                        </div>
                    </div>
                    <div class="index-recent-right" id='inbox_{{id}}'>
                        <i><img src="${ctx}/static/skins/default/img/operation.png" alt=""></i>
                    </div>
                </div>
            </div>
        </div>
        <div class="weui-cell__ft" id="inboxs_{{id}}">
            <a class="weui-swiped-btn index-share-btn" onclick="shareByLink(this)" href="javascript:">发送</a>
<%--
            <a class="weui-swiped-btn index-link-btn" onclick="deleteReceiveFolderByLineScroll(this)" href="javascript:">删除</a>
--%>
        </div>
    </div>
</script>
</body>
<script src="${ctx}/static/js/share/shareRecive.js"></script>
</html>
