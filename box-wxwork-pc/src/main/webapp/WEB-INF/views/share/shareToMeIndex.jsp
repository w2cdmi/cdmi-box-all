<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
	<!DOCTYPE html>
	<html>

	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>收到的共享</title>
		<%@ include file="../common/include.jsp"%>
			<!-- <link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/share/shareToMeIndex.css" /> -->
			<!-- <script src="${ctx}/static/js/common/line-scroll-animate.js"></script> -->
			<!-- <script src="${ctx}/static/js/common/folder-chooser.js"></script> -->
			<link rel="stylesheet" href="${ctx}/static/zTree/metroStyle/metroStyle.css" type="text/css">
			<script src="${ctx}/static/zTree/jquery.ztree.core.min.js"></script>
			<script src="${ctx}/static/components/mySelectFolderDialog.js"></script>
			<script src="${ctx}/static/js/share/shareToMeIndex.js?v=${version}"></script>
			<!-- <script src="${ctx}/static/js/common/line-scroll-animate.js"></script> -->
			<script src="${ctx}/static/components/Components.js"></script>
			<script src="${ctx}/static/components/Menubar.js"></script>
			<script src="${ctx}/static/components/Pagination.js"></script>
	</head>

	<body>
		<%@ include file="../common/folderChooser.jsp" %>
			<%@ include file="../common/menubar.jsp" %>
				<div>
					<!-- <div class="load">
				<div class="load-img"><img src="${ctx}/static/skins/default/img/load-rotate.png" /></div>
				<div class="load-text">正在加载</div>
			</div> -->
					<!-- <div class="file-view-toolbar">
				<div class="sort-button">
					<button class="label ml0">排序：</button>
					<button id="dateSorts">日期</button>
					<button id="nameSorts">名称</button>
				</div>
			</div> -->
					<div id="toolbar" class="cl">
						<div class="left">
							<button id="sort_button">
								<i class="fa fa-sort-alpha-asc"></i>排序</button>
							<div class="popover bottom" id="sort_popover">
								<div class="arrow" style="left: 50px;"></div>
								<dl class="menu">
									<dt id="orderField_modifiedAt"><i class="fa fa-long-arrow-down" style="visibility: hidden"></i>创建时间</dt>
									<dt id="orderField_name"><i class="fa fa-long-arrow-down" style="visibility: hidden"></i>文件名</dt>
								</dl>
							</div>
						</div>
					</div>
				</div>
				<div id="breadcrumd" class="cl">
					<ol class="breadcrumb">
						<li class="active">
							<span class="txt-ellipsis" title="收到的共享">收到的共享</span>
						</li>
					</ol>
				</div>
				<div id="box">
					<div class="abslayout" style="bottom: 16px;top:150px;right: 0;left: 0;min-height: 200px">
						<table style="padding-bottom: 0;padding-top: 0;">
							<thead>
								<tr>
									<th style="min-width: 280px">文件名</th>
									<th style="width: 180px">大小</th>
									<th style="width: 180px">创建时间</th>
								</tr>
							</thead>
						</table>
						<div class="abslayout" id="datagrid" style="left:16px;right:16px;overflow: auto;bottom: 40px;top:40px;">
							<table style="padding-top: 0">
								<tbody id="shareToMeFileList">
								</tbody>

							</table>
							<div class="notfind" style="display: none;height: 18px;">
								<p>暂无数据</p>
							</div>
						</div>
						<div class="abslayout perpagebar" style="left:16px;right:16px;bottom: 0px;" id="pagination">
							<div class="left" style="display: inline-block">
								<span id="totalSize">总记录数：0</span>&nbsp;&nbsp;
								<span id="currentPage">当前页：1</span>&nbsp;&nbsp;
								<span id="totalPages">总页数：0</span>
							</div>
							<div class="right" style="display: inline-block;margin-left: 16px;">
								<button id="firstPage">首页</button>
								<button id="prePage">上页</button>
								<button id="nextPage">下页</button>
								<button id="lastPage">尾页</button>
							</div>
						</div>
					</div>
					<div class="popover bottom" id="table_popover">
						<div class="arrow" style="left:77px;"></div>
						<dl class="menu">
							<dt role="0,1" command="1">
								<i class="fa fa-files-o"></i>另存到...</dt>
							<dt role="0,1" command="2">
								<i class="fa fa-power-off"></i>退出共享</dt>
							<dt role="1" command="3">
                                <i class="fa fa-download" aria-hidden="true"></i>下载</dt>
						</dl>
					</div>
				</div>

				<a id="downloadFile" download style="display:none"></a>
				<!-- onclick="openShareFile(this)" -->
		<!-- onclick="gotoPageshared()" -->
				<script id="shareToMeFileTemplate" type="text/template7">
					<tr onclick="clickSharedItem()" id="shareToMeFile_{{ownerId}}_{{iNodeId}}" ids='{{id}}'
					    ownerIds='{{ownerId}}' sharedUserId="{{sharedUserId}}" sharedUserType="{{sharedUserType}}" modifiedBys='{{modifiedBy}}' iNodeIds='{{iNodeId}}' types='{{type}}' fileName='{{name}}'>
						<td style="min-width: 280px">
							<div id="typeimg" class="fileitem-temple1 cl">
								{{#js_compare "this.imgPath==null"}}
								<i class='{{iconimg}}'></i>
								{{else}}
                                <i style="background: url('{{imgPath}}') no-repeat center center;background-size: 32px 32px;"></i>
                                <img data-index="{{num}}" data-origina='{{imgSrc}}' src='{{imgSrc}}' style="display: none" alt="{{name}}"/>
                                {{/js_compare}}
								<span>
									<h3>{{name}}</h3>
									<label>{{ownerName}}</label>
								</span>
								<u style="margin-top: 5px">
									<a id="worker" data={{type}}>
										<i class="fa fa-ellipsis-h"></i>
									</a>
								</u>
							</div>
						</td>
						{{#js_compare "this.size==''"}}
						<td style="width: 180px">——</td>
						{{else}}
						<td style="width: 180px">{{size}}</td>
						{{/js_compare}}
						<td style="width: 180px">{{modifiedAt}}</td>
					</tr>
				</script>

				<%-- 文件夹选择 --%>
					<div class="full-dialog" id="folderChooser" style="display: none">
						<div class="full-dialog-content">
							<div class="full-dialog-content-middle">
								<div class="dialog-title">选择文件夹</div>
								<div class="bread-crumb full-dialog-nav">
									<div class="bread-crumb-content" id="chooserBreadCrumb">
										<div onclick="jumpFolder(this, 0);">个人文件</div>
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

					<%@ include file="../common/footer3.jsp"%>
        <a id="downloadFile" download style="display:none"></a>
		<%@ include file="../common/video.jsp" %>
	</body>

	</html>