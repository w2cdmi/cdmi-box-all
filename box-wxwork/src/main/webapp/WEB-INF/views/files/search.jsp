<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<!DOCTYPE html>
<html>
    <head>
        <title>搜索</title>
        <%@ include file="../common/include.jsp"%>
        <link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/index.css"/>
        <link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/files/search.css"/>
        <script src="${ctx}/static/js/files/search.js"></script>
    </head>
        <body>
            <div id="index-header" class="index-header">
                <div id="index_search" class="index-search">
                    <input type="text" id="searchFileInput" class="index-search-input" placeholder="搜索文件">
                    <div class="index-search-icon"><img src="${ctx}/static/skins/default/img/search-img.png"/></div>
                </div>
            </div>
            <div class="searc-content">
                <%--<div class="search-choose-file" id="searchChooseFile">
                    <p class="search-choose-title">指定搜索内容</p>
                    <ul class="searc-choose-ul" id="searcChooseUl" >
                        <li onclick="gotoPage('${ctx}/files/search?type=-1')">
                            <p>个人文件</p>
                        </li>
                        <li onclick="gotoPage('${ctx}/files/search?type=0')">
                            <p>协作空间</p>
                        </li>
                        <li onclick="gotoPage('${ctx}/files/search?type=1')">
                            <p>部门空间</p>
                        </li>
                        <li onclick="gotoPage('${ctx}/files/search?type=4')">
                            <p>企业文库</p>
                        </li>
                    </ul>
                </div>--%>
                <div class="index-recent-title" onclick="gotoPage('${ctx}/folder?rootNode=0')" id="titleList" style="display:none">
                    <span>搜索结果</span>
                    <i><img src="${ctx}/static/skins/default/img/putting-more.png"/></i>
                </div>
                <div id="searchFileList"></div>
            </div>

<%--搜索模版--%>
    <script id="searchFileTemplate" type="text/template7">
        <div class="weui-cell weui-cell_swiped" value = "{{id}}" name="{{modifiedBy}}" fileName="{{name}}">
            <div class="weui-cell__bd" style="transform: translate3d(0px, 0px, 0px);" id="searchFile_{{id}}" onclick="optionInodeFromSearch(this)">
                <div class="weui-cell weui-cell-change">
                    <div class="weui-cell__bd" >
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
                                <span>{{menderName}}</span>
                                <span>|</span>
                                <span>{{modifiedAt}}</span>
                            </div>
                        </div>
                        <%--<div class="index-recent-right" id="file_{{id}}" >--%>
                            <%--<i><img src="${ctx}/static/skins/default/img/operation.png" alt=""></i>--%>
                        <%--</div>--%>
                    </div>
                </div>
            </div>
            <div class="weui-cell__ft">
            <a class="weui-swiped-btn index-share-btn" href="javascript:">共享</a>
            <a class="weui-swiped-btn index-link-btn" href="javascript:">外发</a>
            </div>
        </div>
    </script>
        </body>
</html>
