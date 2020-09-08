<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
	<!DOCTYPE html>
	<html>

	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>我发出的共享</title>
		<%@ include file="../common/include.jsp"%>
			<link rel="stylesheet" href="${ctx}/static/css/default/magic-input.min.css" type="text/css">
			<link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/share/shareByMeIndex.css?v=${version}" />
			<!-- <script src="${ctx}/static/js/common/line-scroll-animate.js"></script> -->
			<script src="${ctx}/static/js/share/shareByMeIndex.js?v=${version}"></script>
			<script src="${ctx}/static/components/Components.js"></script>
			<script src="${ctx}/static/components/Menubar.js"></script>
			<script src="${ctx}/static/components/Pagination.js"></script>
			<script src="${ctx}/static/components/ShareDialog.js"></script>
	</head>

	<body>
		<%@ include file="../common/menubar.jsp" %>
			<%@ include file="../common/shareDialog.jsp" %>
				<a id="downloadFile" download style="display:none"></a>
				<div>
					<div id="toolbar" class="cl">
						<div class="left">
							<button id="sort_button">
								<i class="fa fa-sort-alpha-asc"></i>排序</button>
							<div class="popover bottom" id="sort_popover">
								<div class="arrow" style="left: 50px;"></div>
								<dl class="menu">
									<dt id="orderField_modifiedAt">
										<i class="fa fa-long-arrow-down" style="visibility: hidden"></i>创建时间</dt>
									<dt id="orderField_name">
										<i class="fa fa-long-arrow-down" style="visibility: hidden"></i>文件名</dt>
								</dl>
							</div>
						</div>
					</div>
				</div>
				<div id="breadcrumd" class="cl">
					<ol class="breadcrumb">
						<li class="active">
							<span class="txt-ellipsis" title="我发出的共享">我发出的共享</span>
						</li>
					</ol>
				</div>
				<div>
					<div class="abslayout" id="datagrid" style="bottom: 16px;top:150px;right: 0;left: 0;min-height: 200px">
						<table style="padding-bottom: 0;padding-top: 0;">
							<thead>
								<tr>
									<th style="min-width: 280px">文件名</th>
									<th style="width: 180px">大小</th>
									<th style="width: 180px">创建时间</th>
								</tr>
							</thead>
						</table>
						<div class="abslayout" style="left:16px;right:16px;overflow: auto;bottom: 40px;top:40px;">
							<table style="padding-top: 0">
								<tbody id="shareByMeFileList">
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
						<div class="arrow" style="left: 88px;"></div>
						<dl class="menu">
							<dt role="1,2,3," command="1">
								<i class="fa fa-files-o"></i>查看共享</dt>
							<dt role="1,3," command="2">
								<i class="fa fa-trash-o"></i>取消共享</dt>
							<dt id="gotoinner" role="1,2," command="3">
								<i class="fa fa-sign-in"></i>进入目录</dt>
						</dl>
					</div>
				</div>

				<script id="shareByMeFileTemplate" type="text/template7">
					<tr onclick="enterFolder('{{ownerId}}','{{iNodeId}}',this)" id="shareByMeFile_{{nodeId}}" ids="{{iNodeId}}" ownerIds="{{ownerId}}" types="{{type}}" fileName="{{name}}">
						<td style="min-width: 280px">
							<div class="fileitem-temple1 cl">
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
									<a id="worker" data="1">
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
				<%@ include file="../common/footer4.jsp"%>
		<%@ include file="../common/video.jsp" %>
	</body>

	</html>