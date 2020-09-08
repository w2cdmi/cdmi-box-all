<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="javax.sound.midi.SysexMessage" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<!DOCTYPE html>
<html>
<head>
<%@ include file="../common/include.jsp" %>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta name="viewport" content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no" />
<title>个人文件</title>
	<link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/index.css"/>
<link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/folder/folderIndex.css"/>
<script src="${ctx}/static/js/folder/folderIndex.js"></script>
<script type="text/javascript">
	var catalogParentId = <c:out value='${parentId}'/>;
	var isLinkHidden = <c:out value='${linkHidden}'/>;
	var canPreview =<%=PreviewUtils.isPreview()%>;
</script>
</head>
<body style="background:#f8f8f8">
<div class="fs-view-header">
	<div class="load">
		<div class="load-img"><img src="${ctx}/static/skins/default/img/load-rotate.png"/></div>
		<div class="load-text">正在加载</div>
	</div>
	<%--<div id="header">--%>
		<%--<%@ include file="../common/uploader.jsp" %>--%>
	<%--</div>--%>
	<%--<div class="fillBackground"></div>--%>
	<%--<div class="file-view-toolbar">--%>
		<%--&lt;%&ndash;<div class="new-folder-button pull-right" onclick="newFolderDialog(listFile)"></div>&ndash;%&gt;--%>
		<%--<div class="search-button pull-right" onclick="showSearchFileDialog()"></div>--%>
		<%--<div class="sort-button pull-right">--%>
			<%--<div class="label">排序：</div>--%>
			<%--<div id="dateSort">日期&nbsp;</div>--%>
			<%--<div id="nameSort">名称</div>--%>
		<%--</div>--%>
	<%--</div>--%>
	<div class="folder-header">
		<div id="index_search" class="folder-search">
			<input type="text" id="folder-search" onfocus="gotoPage('${ctx}/files/search?type=-1')" class="folder-search-input" placeholder="搜索个人文件"/>
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
	<div class="blank-background" style="display: none;"></div>     <!--当文件夹为空时的背景图-->
</div>

<div class="fs-view-file-list" id="fileListWrapper"  style="background-size: 5rem 5rem;">
	<div class="weui-pull-to-refresh__layer">
		<div class="weui-pull-to-refresh__preloader"></div>
		<div class="up" style="text-align: center;">释放刷新</div>
		<div class="refresh" style="text-align: center;">正在刷新</div>
	</div>
    <div id="uploadFileList"></div>
	<div id="fileList"></div>
</div>

<%@ include file="../common/footer.jsp" %>

<script id="fileTemplate" type="text/template7">
	<div class="weui-cell {{swipeClass}}" style="padding: 0" value = "{{id}}" name="{{modifiedBy}}" fileName="{{name}}">
			<div class="weui-cell__bd" style="transform: translate3d(0px, 0px, 0px);background-color: #fff" id="files_{{id}}" onclick="optionInode(this)">
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
								<span>{{modifiedAt}}</span>
								{{#js_compare "this.size == undefined || this.size=='' || this.size==null"}}
									<span></span>
								{{else}}
									<span>|</span>
									<span>{{size}}</span>
								{{/js_compare}}
							</div>
						</div>
                        {{#js_compare "this.type != -7"}}
						<div class="index-recent-right" id="file_{{id}}" >
							<i><img src="${ctx}/static/skins/default/img/operation.png" alt=""></i>
						</div>
                        {{/js_compare}}
					</div>
				</div>
			</div>
            {{#js_compare "this.type != -7"}}
                <div class="weui-cell__ft" ownedBy={{ownedBy}} nodeId={{id}}>
                    <a class="weui-swiped-btn index-share-btn" onclick="swipeShareDialog(this)" href="javascript:">共享</a>
                    <a class="weui-swiped-btn index-link-btn" onclick="swipeLinkDialog(this)" href="javascript:">外发</a>
                </div>
            {{/js_compare}}

	</div>

</script>

<script id="searchFileTemplate" type="text/template7">
	<div class="line-scroll-wrapper">
		<div class="file line-content" id="searchFile_{{id}}" onclick="optionInodeFromSearch(this)">
			<div class="file-info">
				<div class="img {{divClass}}"></div>
				<div class="fileName">{{name}}</div>
				<span>${rootPath}{{path}}</span> <i>{{modifiedAt}}</i>
			</div>
		</div>
	</div>
</script>

<script src="${ctx}/static/js/common/line-scroll-animate.js"></script>

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
<%@ include file="../common/previewImg.jsp" %>
<%@ include file="../common/previewVideo.jsp" %>
<script src="${ctx}/static/js/common/filelist.js"></script>
</body>
</html>
