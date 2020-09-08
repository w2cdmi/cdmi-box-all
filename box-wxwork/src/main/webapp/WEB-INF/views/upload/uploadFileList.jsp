<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<meta name="viewport" content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no" />
		<%@ include file="../common/include.jsp" %>
        <link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/index.css"/>
		<link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/upload/uploadFileList.css" />
		<script src="${ctx}/static/js/upload/uploadFileList.js"></script>
		<title>传输列表</title>
	</head>

	<body>

		<div id="transport-list">
<%--
			<div class="list-menu-tab">
				<div class="tab-item active" id="uploadList-btn">上传列表</div>
				<div class="tab-item" id="downList-btn">下载列表</div>
			</div>
--%>
			<div class="fillBackground"></div>
			<div class="list-item list-item-repeat">
				<ul id="uploadFile">
					<center style="line-height:6rem;">无上传文件</center>
				</ul>
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

<script id="uploadFileTemplate" type="text/template7">
	<div class="weui-cell weui-cell_swiped">
		<div class="weui-cell__bd" style="transform: translate3d(0px, 0px, 0px);">
			<div class="weui-cell weui-cell-change">
				<div class="weui-cell__bd" >
					<div class="index-recent-left">
						<div class="{{imgClass}}"></div>
					</div>
					<div class="index-recent-middle">
						<div class="recent-detail-name">
							<p>{{name}}</p>
						</div>
						<div class="recent-detail-other">
							<span>{{size}}</span>
							<span>|</span>
							<span>{{time}}</span>
							<span>|</span>
							<span>{{result}}</span>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
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
    <div class="weui-cell weui-cell_swiped">
        <div class="weui-cell__bd" style="transform: translate3d(0px, 0px, 0px);">
            <div class="weui-cell weui-cell-change">
                <div class="weui-cell__bd" >
                    <div class="index-recent-left">
                        <div class="{{imgClass}}"></div>
                </div>
                <div class="index-recent-middle">
                    <div class="recent-detail-name">
                        <p>{{name}}</p>
                    </div>
                    <div class="recent-detail-other">
                        <span>{{createdAt}}</span>
                        {{#js_compare "this.size == undefined || this.size=='' || this.size==null"}}
                            <span></span>
                        {{else}}
                            <span>|</span>
                            <span>{{size}}</span>
                        {{/js_compare}}
                    </div>
                </div>
                </div>
            </div>
        </div>
    </div>

</script>
