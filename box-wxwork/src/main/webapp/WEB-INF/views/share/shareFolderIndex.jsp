<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="javax.sound.midi.SysexMessage" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Access-Control-Allow-Origin" content="*">
<%@ include file="../common/include.jsp" %>
<title>共享文件</title>
	<link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/index.css"/>
<link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/share/shareFolderIndex.css"/>
<script src="${ctx}/static/js/common/line-scroll-animate.js"></script>
<script type="text/javascript">
	var parentId = "<c:out value='${shareRootId}'/>";
    var shareRootId = "<c:out value='${shareRootId}'/>";
    var curOwnerId = '<shiro:principal property="cloudUserId"/>';
    ownerId="<c:out value='${ownerId}'/>";
	var isLinkHidden = <c:out value='${linkHidden}'/>;
	var canPreview =<%=PreviewUtils.isPreview()%>;
</script>
</head>
<body>
<script src="${ctx}/static/js/share/shareFolderIndex.js"></script>
<div class="fs-view-header">
	<div class="load">
		<div class="load-img"><img src="${ctx}/static/skins/default/img/load-rotate.png"/></div>
		<div class="load-text">正在加载</div>
	</div>
	<%--<div class="fillBackground"></div>--%>
	<%--<div class="file-view-toolbar">--%>
		<%--<div class="sort-button pull-right">--%>
			<%--<div class="label">排序：</div>--%>
			<%--<div id="dateSort">日期&nbsp;</div>--%>
			<%--<div id="nameSort">名称</div>--%>
		<%--</div>--%>
		<%--<div class="pull-right-name">共享人：${ownerName}</div>--%>
	<%--</div>--%>
	<div class="folder-header" style="overflow: hidden;background: #f8f8f8">
		<div style="float: left;margin: 0.7rem"><span>共享人：</span><span>${ownerName}</span></div>
		<div class="folder-order" id="folderOrder" style="float: right;margin-right: 0.5rem;">
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
		<div class="bread-crumb-content share-bread-crumb-content" id="directory">
			<div id="jumpFolders"></div>
		</div>
	</div>
	<div class="blank-background" style="display: none;"></div>     <!--当文件夹为空时的背景图-->
</div>

<div class="fs-view-file-list" id="fileListWrapper" style="background-size: 5rem 5rem;">
	<div class="weui-pull-to-refresh__layer">
		<div class="weui-pull-to-refresh__preloader"></div>
		<div class="up">释放刷新</div>
		<div class="refresh">正在刷新</div>
	</div>

	<div id="fileList"></div>
</div>

<%@ include file="../common/footer3.jsp" %>


<div class="full-dialog" style="display: none;" id="searchFileDialog">
	<%--搜索框--%>
		<div class="weui-search-bar" id="searchBar">
			<form class="weui-search-bar__form">
				<div class="weui-search-bar__box">
					<i class="weui-icon-search"></i>
					<input type="text" class="weui-search-bar__input" id="searchFileInput" placeholder="请输入文件名搜索" required="">
					<a href="javascript:" class="weui-icon-clear" id="searchClear"></a>
				</div>
				<label class="weui-search-bar__label" id="searchText" style="transform-origin: 0px 0px 0px; opacity: 1; transform: scale(1, 1);">
					<i class="weui-icon-search"></i>
					<span>请输入文件名搜索</span>
				</label>
			</form>
			<a href="javascript:closeSearchFileDialog()" class="weui-search-bar__close-btn" id="searchClose">关闭</a>
		</div>
	
		<div class="modal-content">
			<div id="searchFileList"></div>
		</div>
</div>
<script id="fileTemplate" type="text/template7">
	<%--<div class="line-scroll-wrapper">--%>
		<%--<div class="file line-content" id="file_{{id}}" onclick="optionInode(this)">--%>
			<%--<div class="file-info">--%>
				<%--<div class="img {{divClass}}"></div>--%>
				<%--<div class="fileName">{{name}}</div>--%>
				<%--<span>{{size}}</span> <i>{{modifiedAt}}</i>--%>
			<%--</div>--%>
		<%--</div>--%>
		<%--<div class="line-buttons">--%>
			<%--<div class="line-button line-button-share" onclick="save2PersonalFileOnScroll({{id}})">转存</div>--%>
		<%--</div>--%>
	<%--</div>--%>

	<div class="weui-cell weui-cell_swiped">
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
					<div class="index-recent-right" id="file_{{id}}" >
						<i><img src="${ctx}/static/skins/default/img/operation.png" alt=""></i>
					</div>
				</div>
			</div>
		</div>
		<div class="weui-cell__ft">
		<a class="weui-swiped-btn index-share-btn" href="javascript:" onclick="save2PersonalFileOnScroll('{{id}}')">转存</a>
		</div>
	</div>
</script>

<script id="searchFileTemplate" type="text/template7">
	<div class="line-scroll-wrapper">
		<div class="file line-content" id="searchFile_{{id}}" onclick="optionInodeFromSearch(this)">
			<div class="file-info">
				<div class="img {{divClass}}"></div>
				<div class="fileName">{{name}}</div>
				<div class="pattern-name"><span>{{modifiedByName}}</span> <i>{{modifiedAt}}</i></div>
				<div><span>${rootPath}{{path}}</span></div>
			</div>
		</div>
	</div>
</script>


<%--文件夹选择对话框--%>
<div id="shareToFolderChooserDialog" class="folder-chooser-dialog" style="display:none; width: 100%; position: fixed; top: 0; bottom: 60px; left: 0; right: 0; background: #f5f5f5; z-index: 999999">
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
<%-- 文件夹选择 --%>
<div class="full-dialog" id="folderChooser" style="display: none">
	<div class="full-dialog-content">
		<div class="full-dialog-content-middle">
			<div class="dialog-title">选择文件夹</div>
			<div class="file-view-toolbar">
				<%--<div class="new-folder-button pull-right" onclick="newFolderDialog(listFile)"></div>--%>
				<div class="sort-button pull-right">
					<div class="label">排序：</div>
					<div id="chooserDateSort">日期&nbsp;</div>
					<div id="chooserNameSort">名称</div>
				</div>
			</div>

			<div class="bread-crumb full-dialog-nav">
				<div class="bread-crumb-content" id="chooserBreadCrumb">
					<div onclick="jumpFolderOnShare(this, 0);">${rootPath}</div>
				</div>
			</div>
		</div>
		<div id="chooserFileList" class="line-content-father"></div>
		<div class="full-dialog-tail">
			<a href="javascript:" class="primary" id="chooserFileOkButton">确定</a>
			<a href="javascript:" class="default" id="chooserFileCancelButton">取消</a>
		</div>
	</div>
</div>
<script id="chooserFileTemplate" type="text/template7">
	<div class="line-scroll-wrapper">
		<div class="file line-content" id="chooserFile_{{id}}">
			<div class="file-info">
				<div class="img folder-icon"></div>
				<div class="fileName">{{name}}</div>
			</div>
		</div>
	</div>
</script>
<script src="${ctx}/static/js/common/folder-chooser.js"></script>
<!--查看版本信息-->
<div class="version-info">
	<div class="version-info-content">
		<div class="version-info-header">文件版本列表</div>
		<div class="version-info-middle">
			<div class="img" id="fileImage"></div>
			<span id="fileName"></span>
		</div>
		<div class="version-info-page">
			<ul id="fileVersionList">
			</ul>
		</div>
	</div>
	<div class="version-info-tail">退出</div>
</div>
<script src="${ctx}/static/js/common/filelist.js"></script>
<%@ include file="../common/previewImg.jsp" %>
<%@ include file="../common/previewVideo.jsp" %>
</body>
</html>
