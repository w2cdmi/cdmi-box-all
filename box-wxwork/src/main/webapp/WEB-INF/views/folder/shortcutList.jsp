<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no"/>
    <title>快捷文件夹</title>
    <%@ include file="../common/include.jsp" %>
    <link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/index.css"/>
    <link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/folder/shortcutList.css"/>
    <script src="${ctx}/static/js/folder/shortcutList.js"></script>
</head>
<body style="background:#f8f8f8">
<div id="list">
    <div class="index-recent-title" onclick="gotoPage('${ctx}/folders/shortcutList')">
        <span>快捷文件夹</span>
    </div>
    <div id="shortcut_list" class="index-shortcut-list">

    </div>
</div>
<%--快捷目录模版--%>
<script id="index_short" type="text/template7">
    <div class="weui-cell weui-cell_swiped">
        <div class="weui-cell__bd" style="transform: translate3d(0px, 0px, 0px);">
            <div class="weui-cell weui-cell-change">
                <div class="weui-cell__bd">
                    <div class="index-recent-left">
                        <div class="{{imgClass}}"></div>
                    </div>
                    <div class="index-recent-middle">
                        <div class="recent-detail-name">
                            <p>{{nodeName}}</p>
                        </div>
                        <div class="recent-detail-other">
                            {{#js_compare "this.type == 1"}}
                            <span>个人文件</span>
                            {{else}}
                            <span>{{ownerName}}</span>
                            {{/js_compare}}
                            <%--
                                                                    <span>|</span>
                                                                    <span>{{size}}</span>
                            --%>
                        </div>
                    </div>
                    <div class="index-recent-right" id="short_{{id}}">
                        <i><img src="${ctx}/static/skins/default/img/operation.png" alt=""></i>
                    </div>
                </div>
            </div>
        </div>
        <div class="weui-cell__ft">
            <a class="weui-swiped-btn index-link-btn" id="deleteShort_{{id}}" href="javascript:">删除</a>
        </div>
    </div>
</script>
</body>
</html>