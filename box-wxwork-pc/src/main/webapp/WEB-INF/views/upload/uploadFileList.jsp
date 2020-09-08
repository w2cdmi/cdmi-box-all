<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<meta name="viewport" content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no" />
		<%@ include file="../common/include.jsp" %>
		<link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/upload/uploadFileList.css" />
		<script src="${ctx}/static/js/upload/uploadFileList.js"></script>
		<title>传输列表</title>
	</head>

	<body>

		<div id="transport-list">
			<div class="list-menu-tab">
				<div class="tab-item active" id="uploadList-btn">上传列表</div>
				<div class="tab-item" id="downList-btn">下载列表</div>
			</div>
			<div id="uploadFileListDiv">
				<!-- <div class="list-title"><span>正在上传</span></div>
				<div class="list-item">
					<ul id="uploadingFile">
						<center style="line-height:3rem;">无正在上传文件</center>
					</ul>
				</div> -->
				<div class="list-title"><span>已上传</span></div>
				<div class="list-item list-item-repeat">
					<ul id="uploadedFile">
						<center style="line-height:6rem;">无上传文件</center>
					</ul>
				</div>
			</div>
			<div id="downFileListDiv" hidden>
				<!-- <div class="list-title"><span>正在下载</span></div>
				<div class="list-item" >
					<ul id="downloadingFile">
						<center style="line-height:3rem;">无正在下载文件</center>
					</ul>
				</div> -->
				<div class="list-title"><span>已下载</span></div>
				<div class="list-item list-item-repeat">
					<ul id="downloadedFile">
						<center style="line-height:6rem;">无下载文件</center>
					</ul>
				</div>
			</div>
		</div>
		<%@ include file="../common/footer4.jsp"%>
	</body>
</html>

<script id="uploadingFileTemplate" type="text/template7">
<li>
	<div class="item-icon"></div>
	<div class="item-info">
		<div class="item-title">
			{{name}}
		</div>
		<div class="item-note">
			<div class="file-size">
				<span>{{size}}</span>
			</div>
			<div class="file-time">
				<span><i>{{createdAt}}</i></span>
			</div>
		</div>
	</div>
	<div class="item-control">
		<div class="control-selected"></div>
	</div>
</li>
</script>

<script id="uploadedFileTemplate" type="text/template7">
<li>
	<div class="{{imgClass}}"></div>
	<div class="item-info">
		<div class="item-title">
			{{name}}
		</div>
		<div class="item-note">
			<div class="file-size">
				<span>{{size}}</span>
			</div>
			<div class="file-time">
				<span><i>{{createdAt}}</i></span>
			</div>
		</div>
	</div>
</li>
</script>

<script id="downloadingFileTemplate" type="text/template7">
<li>
	<div class="{{imgClass}}"></div>
	<div class="item-info">
		<div class="item-title">
			{{name}}
		</div>
		<div class="item-note">
			<div class="file-size">
				<span>{{size}}</span>
			</div>
			<div class="file-time">
				<span><i>{{createdAt}}</i></span>
			</div>
		</div>
	</div>
	<div class="item-control">
		<div class="control-selected"></div>
	</div>
</li>
</script>

<script id="downloadedFileTemplate" type="text/template7">
<li>
	<div class="{{imgClass}}"></div>
	<div class="item-info">
		<div class="item-title">
			{{name}}
		</div>
		<div class="item-note">
			<div class="file-size">
				<span>{{size}}</span>
			</div>
			<div class="file-time">
				<span><i>{{createdAt}}</i></span>
			</div>
		</div>
	</div>
</li>
</script>
