<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="javax.sound.midi.SysexMessage" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Access-Control-Allow-Origin" content="*">
<%@ include file="../common/include.jsp" %>
<title>共享文件</title>
<link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/share/shareFolderIndex.css"/>
<script src="${ctx}/static/js/common/line-scroll-animate.js"></script>
<script type="text/javascript">
	var parentId = "<c:out value='${shareRootId}'/>";
    var shareRootId = "<c:out value='${shareRootId}'/>";
    var curOwnerId = '<shiro:principal property="cloudUserId"/>';
    ownerId="<c:out value='${ownerId}'/>";
	var isLinkHidden = <c:out value='${linkHidden}'/>;
	var canPreview =<%=PreviewUtils.isPreview()%>;
	var reqProtocol = "<%=request.getSession().getAttribute("reqProtocol")%>";
</script>
</head>
<body>
<%--<%@ include file="../common/topProgress.jsp" %>--%>
<script src="${ctx}/static/js/common/file-view.js"></script>
<script src="${ctx}/static/js/share/shareFolderIndex.js"></script>
<div class="fs-view-header">
	<div class="load">
		<div class="load-img"><img src="${ctx}/static/skins/default/img/load-rotate.png"/></div>
		<div class="load-text">正在加载</div>
	</div>
	<div id="header">
		<div id="upload">
			<img src="${ctx}/static/skins/default/img/header-upload.png" id="upload_all" />
		</div>
		<div id="photograph">
			<img src="${ctx}/static/skins/default/img/header-photograph.png" id="upload_photo"/>
		</div>
		<div id="tape">
			<img src="${ctx}/static/skins/default/img/header-tape.png" id="createFolderId"/>
		</div>
	</div>
	<div class="fillBackground"></div>
	<div class="file-view-toolbar">
		<div class="sort-button pull-right">
			<div class="label">排序：</div>
			<div id="dateSort">日期</div>
			<div id="nameSort">名称</div>
		</div>
		<div class="pull-right-name">共享人：${ownerName}</div>
	</div>
	<a id="downloadFile" download style="display:none"></a>
	<div class="bread-crumb">
		<div class="bread-crumb-content" id="directory">
			<div onclick="jumpFolder(this, 0);">${rootPath}</div>	
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
	<div class="line-scroll-wrapper">
		<div class="file line-content" id="file_{{id}}" onclick="optionInode(this)">
			<div class="file-info">
				<div class="img {{divClass}}"></div>
				<div class="fileName">{{name}}</div>
				<span>{{size}}</span> <i>{{modifiedAt}}</i>
			</div>
		</div>
		<div class="line-buttons">
			<div class="line-button line-button-share" onclick="save2PersonalFileOnScroll({{id}})">转存</div>
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

<%-- 文件夹选择 --%>
<div class="full-dialog" id="folderChooser" style="display: none">
	<div class="full-dialog-content">
		<div class="full-dialog-content-middle">
			<div class="dialog-title">选择文件夹</div>
			<div class="file-view-toolbar">
				<!-- <div class="new-folder-button pull-right" onclick="newFolderDialog(listFile)"></div> -->
				<div class="sort-button pull-right">
					<div class="label">排序：</div>
					<div id="chooserDateSort">日期</div>
					<div id="chooserNameSort">名称</div>
				</div>
			</div>
		
			<div class="bread-crumb full-dialog-nav">
				<div class="bread-crumb-content" id="chooserBreadCrumb">
					<div onclick="jumpFolderOnShare(this, 0);">${rootPath}</div>	
				</div>
			</div>
		</div>
		<div id="chooserFileList"class="line-content-father"></div>
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
</body>
</html>
