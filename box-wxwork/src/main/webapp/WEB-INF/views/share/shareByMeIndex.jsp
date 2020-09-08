<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>

	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>我发出的共享</title>
		<%@ include file="../common/include.jsp"%>
		<link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/index.css"/>
		<link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/share/shareByMeIndex.css" />
		<script src="${ctx}/static/js/common/line-scroll-animate.js"></script>
		<script src="${ctx}/static/js/share/shareByMeIndex.js"></script>
	</head>

	<body>
		<a id="downloadFile" download style="display:none"></a>
		<div>
			<div class="load">
				<div class="load-img"><img src="${ctx}/static/skins/default/img/load-rotate.png" /></div>
				<div class="load-text">正在加载</div>
			</div>
		</div>
		<div style="width:100%;height:0.5rem;background:#f5f5f5"></div>
		<div id="box" style="top:0.5rem">
			<div id="fileList">

			</div>
		</div>
		
	   <script id="shareByMeFileTemplate" type="text/template7">
			<%--<li class="line-scroll-wrapper" id="shareByMeFile_{{nodeId}}">--%>
				<%--<div class="shareByMeFileTemplate-content" onclick="shareByMeIndexviewshare({{nodeId}})">--%>
					<%--<div class="file-info">--%>
						<%--{{#js_compare "this.imgPath==null"}}--%>
							<%--<div class="img {{imgClass}}"></div>--%>
						<%--{{else}}--%>
            				<%--<div class="img {{imgClass}}" style="background:url({{imgPath}}) no-repeat center center;"></div>--%>
            			<%--{{/js_compare}}--%>
						<%--<div class="fileName">{{name}}</div>--%>
						<%--<span>{{ownerName}}</span><i>{{modifiedAt}}</i>--%>
					<%--</div>--%>
				<%--</div>--%>
				<%--<div class="line-buttons">--%>
					<%--{{#js_compare "this.imgClass=='folder-share-icon'"}}--%>
						<%--<div class="line-button line-button-share" onclick="enterFolder({{nodeId}})">进入目录</div>--%>
					<%--{{else}}--%>
						<%--<div class="line-button line-button-share" onclick="downloadFileByNodeId('{{nodeId}}','{{name}}')">预览</div>--%>
					<%--{{/js_compare}}--%>
					<%--<div class="line-button line-button-link" onclick="cancelShare({{nodeId}})">取消共享</div>--%>
				<%--</div>--%>
			<%--</li>--%>

			<div class="weui-cell weui-cell_swiped">
				{{#js_compare "this.type==0"}}
				<div class="weui-cell__bd" style="transform: translate3d(0px, 0px, 0px);" onclick="enterFolder('{{nodeId}}')">
				{{else}}
				<div class="weui-cell__bd" style="transform: translate3d(0px, 0px, 0px);" onclick="downloadFileByNodeId('{{nodeId}}','{{name}}',this)">
				{{/js_compare}}
					<div class="weui-cell weui-cell-change">
						<div class="weui-cell__bd" >
                            {{#js_compare "this.imgPath!=null"}}
                            <image class="fileImg" data-index="{{num}}" src="{{imgSrc}}" style="display: none;"></image>
                            {{/js_compare}}
							<div class="index-recent-left">
								{{#js_compare "this.imgPath!=null"}}
									<div class="{{imgClass}}" style="background:url({{imgPath}}) no-repeat center center"></div>
								{{else}}
									<div class="{{imgClass}}"></div>
								{{/js_compare}}
							</div>
							<div class="index-recent-middle">
								<div class="recent-detail-name">
									<p>{{name}}</p>
								</div>
								<div class="recent-detail-other">
									<span>{{ownerName}}</span>
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
							<div class="index-recent-right" id="shareByMeFile_{{nodeId}}" >
								<i><img src="${ctx}/static/skins/default/img/operation.png" alt=""></i>
							</div>
						</div>
					</div>
				</div>
				<div class="weui-cell__ft">
					<a class="weui-swiped-btn index-link-btn" style="line-height:0.8rem" href="javascript:" onclick="cancelShare('{{nodeId}}')"><p style="margin-top:0.75rem">取消</p><p>共享</p></a>
				</div>
			</div>
		</div>
		</script>
		<%@ include file="../common/previewImg.jsp" %>
		<%@ include file="../common/previewVideo.jsp" %>
		<%@ include file="../common/footer4.jsp"%>

	</body>

</html>
