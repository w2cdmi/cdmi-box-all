<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no" />
        <title><spring:message code='main.title'/></title>
        <%@ include file="common/include.jsp" %>
        <script src="${ctx}/static/jquery/jquery.transit.js"></script>
        <link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/header.css"/>
        <link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/index.css"/>
		<script src="${ctx}/static/js/common/line-scroll-animate.js"></script>
		<script src="${ctx}/static/js/index.js"></script>
		<style>
			
		</style>
    </head>
    <body style="background:#f5f5f5">
		<div id="box">
			<div class="load">
				<div class="load-img"><img src="${ctx}/static/skins/default/img/load-rotate.png"/></div>
				<div class="load-text">正在加载</div>
			</div>
			<div id="index-header" class="index-header">
				<div id="index_search" class="index-search">
					<input type="text" id="index-search" onfocus="gotoPage('${ctx}/files/search?type=-1')" class="index-search-input" placeholder="搜索个人文件">
					<div class="index-search-icon"><img src="${ctx}/static/skins/default/img/search-img.png"/></div>
				</div>
				<div id="index_per_depart" class="index-per-depart">
					<ul class="index-list-content">
						<li class="index-person" onclick="gotoPage('${ctx}/folder?rootNode=0')">
							<i><img src="${ctx}/static/skins/default/img/index-person.png" alt=""></i>
							<p>个人文件</p>
						</li>
						<li class="index-depart" onclick="gotoPage('${ctx}/teamspace/deptSpaceList')">
							<i><img src="${ctx}/static/skins/default/img/index-part.png" alt=""></i>
							<p>部门文件</p>
						</li>
						<li class="index-cooperation" onclick="gotoPage('${ctx}/teamspace')">
							<i><img src="${ctx}/static/skins/default/img/index-cooper.png" alt=""></i>
							<p>协作空间</p>
						</li>
					</ul>
				</div>
			</div>
	    	<div class="fillBackground"></div>
	    	<div id="list">
				<div class="index-recent-title" onclick="gotoPage('${ctx}/folders/recentFileList')">
					<span>最近浏览</span>
					<i><img src="${ctx}/static/skins/default/img/putting-more.png"/></i>
				</div>

				<div id="fileList" class="index-recent-list">

				</div>

	    	</div>
	    	<a id="downloadFile" download style="display:none"></a>
	    	<div class="fillBackground"></div>
	    	<div id="list">
				<div class="index-recent-title">
					<span>快捷文件夹</span>
				</div>
				<div id="shortcut_list" class="index-shortcut-list" style="max-height: 100%">
				</div>
	    	</div>
	    	<%--<div class="Page-tail-navigation">--%>
	    		<%--<ul id="spaceList">--%>
	    			<%--<li onclick="gotoPage('${ctx}/folder?rootNode=0')">--%>
	    				<%--<img src="${ctx}/static/skins/default/img/personal-icon.png"/>--%>
	    				<%--<span>个人文件</span>--%>
	    			<%--</li>--%>
	    		<%--</ul>--%>
	    	<%--</div>--%>
		</div>
		<%@ include file="common/footer1.jsp" %>

	<%--最近浏览模版--%>
	<script id="index_recent" type="text/template7">
	<div class="weui-cell weui-cell_swiped">
		<div class="weui-cell__bd" style="transform: translate3d(0px, 0px, 0px);" fileName="{{name}}" onclick="downloadFileByNodeIdAndOwnerId('{{id}}','{{ownedBy}}',this)">
			<div class="weui-cell weui-cell-change">
				<div class="weui-cell__bd" >
                    {{#js_compare "this.thumbnailUrlList.length>0"}}
                    <image class="fileImg" data-index="{{num}}" src="{{imgSrc}}" style="display: none;"></image>
                    {{/js_compare}}
					<div class="index-recent-left">
						{{#js_compare "this.thumbnailUrlList.length>0"}}
							<div class="{{imgClass}}" style="background:url({{thumbnailUrlList[0].thumbnailUrl}}) no-repeat center center"></div>
						{{else}}
							<div class="{{imgClass}}"></div>
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
							{{#js_compare "this.menderName == undefined"}}
							<span>来源收件箱</span>
							{{else}}
							<span>{{menderName}}</span>
							{{/js_compare}}
							<span>|</span>
							<span>{{modifiedAt}}</span>
							<span>|</span>
							<span>{{size}}</span>
						</div>
						</div>
					<div class="index-recent-right" id="opperation_{{id}}" >
						<i><img src="${ctx}/static/skins/default/img/operation.png" alt=""></i>
					</div>
				</div>
			</div>
		</div>
		<div class="weui-cell__ft" id="opperations_{{id}}">
			{{#js_compare "this.ownedBy == ownerId"}}
				<a class="weui-swiped-btn index-share-btn" id="indexShare" onclick="indexShareOpperation(this)" href="javascript:">共享</a>
			{{/js_compare}}
			<a class="weui-swiped-btn index-link-btn" id="indexShareLink" onclick="indexShareLinkOpperation(this)" href="javascript:">外发</a>

		</div>
	</div>
	</script>
	<%--快捷目录模版--%>
	<script id="index_short" type="text/template7">
		<div class="weui-cell weui-cell_swiped" id="shorts_{{id}}">
				<div class="weui-cell__bd" style="transform: translate3d(0px, 0px, 0px);" onclick="QulickFolderenter('{{type}}','{{nodeId}}','{{ownerId}}')">
				<div class="weui-cell weui-cell-change">
					<div class="weui-cell__bd" >
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
				<a class="weui-swiped-btn index-link-btn delete-swipeout" id="deleteShort_{{id}}" href="javascript:">移除</a>
			</div>
		</div>
	</script>
		<%@ include file="common/previewImg.jsp" %>
		<%@ include file="common/previewVideo.jsp" %>
	</body>
</html>


