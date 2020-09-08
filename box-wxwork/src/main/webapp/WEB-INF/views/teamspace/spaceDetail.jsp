<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>${rootPath}</title>
    <%@ include file="../common/include.jsp"%>
    <link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/index.css"/>
    <link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/folder/folderIndex.css"/>
    <link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/teamSpace/spaceDetail.css"/>
    <script type="text/javascript">
        var canPreview =<%=PreviewUtils.isPreview()%>;
        var ownerId = "${teamId}";
        var teamType='${teamType}';
        var teamId='${teamId}';
        var teamRole='${teamRole}';
        var role='${role}';
        var dataContent;
    </script>
</head>
<body>
<%--<%@ include file="../common/topProgress.jsp" %>--%>
<script src="${ctx}/static/js/common/line-scroll-animate.js"></script>
<script src="${ctx}/static/js/teamspace/spaceDetail.js"></script>
<div class="fs-view-header">
    <div class="folder-header">
        <div id="index_search" class="folder-search">
            <input type="text" id="folder-search" class="folder-search-input" onfocus="gotoPage('${ctx}/files/search?type=${teamType}&ownerId=${teamId}')"  placeholder="搜索文件"/>
            <div class="folder-search-icon"><img src="${ctx}/static/skins/default/img/search-img.png"/></div>
        </div>
        <div class="folder-order" id="folderOrder">
            <i class=""><img src="${ctx}/static/skins/default/img/sort.png"/></i>
            <div class="weui-cells weui-cells_radio change-cells-radio" id="sortRadio" style="display:none">
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
			<div onclick="jumpFolder(this, 0);">${rootPath}</div>	
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

<%--协作空间--%>
<c:if test="${teamType == 0}">
    <%@ include file="../common/footer.jsp" %>
</c:if>

<%--部门空间--%>
<c:if test="${teamType == 1}">
    <%@ include file="../common/footer.jsp" %>
</c:if>

<%--企业文库--%>
<c:if test="${teamType == 4}">
    <%@ include file="../common/footer2.jsp" %>
</c:if>

<div class="full-dialog" style="display: none;" id="searchFileDialog">
    <%--搜索框--%>
    <div class="weui-search-bar" id="searchBar">
        <form class="weui-search-bar__form">
            <div class="weui-search-bar__box">
                <i class="weui-icon-search"></i>
                <input type="text" class="weui-search-bar__input" id="searchFileInput" placeholder="请输入文件名搜索" required>
                <a href="javascript:" class="weui-icon-clear" id="searchClear"></a>
            </div>
            <!--<label class="weui-search-bar__label" id="searchText">
                <i class="weui-icon-search"></i>
                <span>请输入文件名搜索</span>
            </label>-->
        </form>
        <a href="javascript:closeSearchFileDialog()" class="weui-search-bar__close-btn" id="searchClose">关闭</a>
    </div>

    <div class="modal-content">
        <div id="searchFileList"></div>
    </div>
</div>

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
                        <span>{{menderName}}</span>
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
                    <div class="index-recent-right" id="file_{{id}}" >
                        <i><img src="${ctx}/static/skins/default/img/operation.png" alt=""></i>
                    </div>
                </div>
            </div>
        </div>
        <div class="weui-cell__ft" ownedBy={{ownedBy}} nodeId={{id}}>
            <a class="weui-swiped-btn index-link-btn" onclick="swipeLinkDialog(this)" href="javascript:">外发</a>
        </div>
    </div>
</script>

<script id="searchFileTemplate" type="text/template7">
    <div class="line-scroll-wrapper">
        <div class="file line-content" id="searchFile_{{id}}" onclick="optionInodeFromSearch(this)">
            <div class="file-info">
                {{#js_compare "this.imgPath==null"}}
					<div class="img {{divClass}}"></div>
				{{else}}
            		<div class="img {{divClass}}" style="background:url({{imgPath}}) no-repeat center center;background-size: 2.2rem 2.3rem;"></div>
            	{{/js_compare}}
                <div class="fileName">{{name}}</div>
                <span>${rootPath}{{path}}</span> <i>{{modifiedAt}}</i>
            </div>
        </div>
    </div>
</script>

<%-- 文件夹选择 --%>
<div id="copyfolderChooserDialog" class="folder-chooser-dialog" style="display:none; width: 100%; position: fixed; top: 0; bottom: 60px; left: 0; right: 0; background: #f5f5f5; z-index: 999999">
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
                                    <p id="fileFolderName">{{name}}</p>
                                </div>
                                <div class="recent-detail-other">
                                    <span id="fileFolderOwnerName">{{menderName}}</span>
                                    <span>|</span>
                                    <span id="fileFolderTime">{{modifiedAt}}</span>
                                </div>
                            </div>
                        </div>
                        <%--<div class="weui-cell__bd weui-cell_primary">--%>
                        <%--<p style="color:#333;font-size:0.95rem;margin-left: 0.5rem"><span class="upload-file-name">请选择目标文件夹</span></p>--%>
                        <%--</div>--%>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <%@ include file="../common/folderChooser.jsp" %>
</div>

<!--查看版本信息-->
<div class="version-info" style="display: none">
    <div class="version-info-content">
        <div class="version-info-header">文件版本列表</div>
        <div class="version-info-middle">
            <div class="img" id="versionFileImage"></div>
            <span id="versionFileName"></span>
        </div>
        <div class="version-info-page">
            <ul id="fileVersionList">
            </ul>
        </div>
    </div>
    <div class="version-info-tail">退出</div>
</div>
<%@ include file="../common/previewVideo.jsp" %>
<%@ include file="../common/previewImg.jsp" %>
<script src="${ctx}/static/js/common/filelist.js"></script>
</body>
</html>
