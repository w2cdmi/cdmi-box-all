<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Access-Control-Allow-Origin" content="*">
	<%@ include file="../common/include.jsp" %>
	<title>我外发的文件</title>
	<link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/index.css"/>
    <link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/share/linkListlndex.css"/>
</head>
<body  >
<div id="linkListHeader">
	<div class="load">
		<div class="load-img"><img src="${ctx}/static/skins/default/img/load-rotate.png"/></div>
		<div class="load-text">正在加载</div>
	</div>
	<a id="downloadFile" download style="display:none"></a>
</div>

<div class="fs-view-link-list" id="linkListWrapper">
	<div class="weui-pull-to-refresh__layer">
		<div class="weui-pull-to-refresh__preloader"></div>
		<div class="up">释放刷新</div>
		<div class="refresh">正在刷新</div>
	</div>

	<div id="fileList"></div>
</div>

<%@ include file="../common/footer4.jsp" %>

<script id="linkTemplate" type="text/template7">
	<%--<div class="line-scroll-wrapper" id="link_{{id}}">--%>
		<%--<div class="file line-content" onclick="showFilePropertiesForLinkList(this)">--%>
			<%--<div class="file-info">--%>
				<%--{{#js_compare "this.imgPath==null"}}--%>
					<%--<div class="img {{divClass}}"></div>--%>
				<%--{{else}}--%>
            		<%--<div class="img {{divClass}}" style="background:url({{imgPath}}) no-repeat center center;"></div>--%>
            	<%--{{/js_compare}}--%>
				<%--<div class="fileName">{{name}}</div>--%>
				<%--{{#js_compare "this.type==1"}}--%>
					<%--<span>{{size}}</span> --%>
				<%--{{else}}--%>
					<%--<span></span> --%>
				<%--{{/js_compare}}--%>
				<%--<i>{{modifiedAt}}</i>--%>
			<%--</div>--%>
		<%--</div>--%>
		<%--<div class="line-buttons">--%>
			<%--{{#js_compare "this.divClass=='folder-icon'"}}--%>
			<%--<div class="line-button line-button-share" onclick="optionInode('{{id}}')">进入目录</div>--%>
			<%--{{else}}--%>
			<%--<div class="line-button line-button-share" onclick="downloadFile('{{ownedBy}}','{{id}}','{{name}}')">预览</div>--%>
			<%--{{/js_compare}}--%>
			<%--<div class="line-button line-button-link" onclick="cancelAllLinksOfFile('{{id}}','{{ownedBy}}')">取消外发</div>--%>
		<%--</div>--%>
	<%--</div>--%>

	<div class="weui-cell weui-cell_swiped">
		{{#js_compare "this.type==0"}}
		<div class="weui-cell__bd" style="transform: translate3d(0px, 0px, 0px);" onclick="optionInode('{{id}}')">
		{{else}}
		<div class="weui-cell__bd" style="transform: translate3d(0px, 0px, 0px);" onclick="downloadFile('{{ownedBy}}','{{id}}','{{name}}',this)">
		{{/js_compare}}
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
					<div class="index-recent-right" id="link_{{id}}" >
						<i><img src="${ctx}/static/skins/default/img/operation.png" alt=""></i>
					</div>
					</div>
				</div>
			</div>
			<div class="weui-cell__ft">
				<a class="weui-swiped-btn index-link-btn" style="line-height:0.8rem" href="javascript:" onclick="cancelAllLinksOfFile('{{id}}','{{ownedBy}}')" ><p style="margin-top:0.75rem">取消</p><p>外发</p></a>
			</div>
		</div>
	</div>
</script>

<script src="${ctx}/static/js/common/line-scroll-animate.js"></script>
<script src="${ctx}/static/js/share/linkListIndex.js"></script>
<%@ include file="../common/previewImg.jsp" %>
<%@ include file="../common/previewVideo.jsp" %>
</body>
</html>
