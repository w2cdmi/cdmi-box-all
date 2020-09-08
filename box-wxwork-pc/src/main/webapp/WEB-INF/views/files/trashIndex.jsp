<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
	<!DOCTYPE html>
	<html>

	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>回收站</title>
		<%@ include file="../common/include.jsp"%>
			<link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/files/trashIndex.css" />
			<!-- <script src="${ctx}/static/js/common/line-scroll-animate.js"></script> -->
			<script src="${ctx}/static/js/files/trashIndex.js"></script>
			<script src="${ctx}/static/components/Components.js"></script>
			<script src="${ctx}/static/components/Menubar.js"></script>
			<script src="${ctx}/static/components/Pagination.js"></script>
	</head>

	<body>
		<%@ include file="../common/menubar.jsp" %>
			<div>
				<!-- <div class="load">
				<div class="load-img"><img src="${ctx}/static/skins/default/img/load-rotate.png" /></div>
				<div class="load-text">正在加载</div>
			</div> -->
				<!-- <div class="file-view-toolbar">
					<button class="pull-right font-line-height" onclick="clearTrash();">清空回收站</button>
					<div class="pull-right-icon">
						<img src="${ctx}/static/skins/default/img/per-recycle.png" />
					</div>
					<div class="sort-button">
					<button class="label ml0">排序：</button>
					<button id="dateSort">日期</button>
					<button id="nameSort">名称</button>
				</div>
				</div> -->
				<div id="toolbar" class="cl">
					<div class="left">
						<button id="sort_button">
							<i class="fa fa-sort-alpha-asc"></i>排序</button>
						<div class="popover bottom" id="sort_popover">
							<div class="arrow" style="left: 50px;"></div>
							<dl class="menu">
								<dt id="orderField_modifiedAt">
									<i class="fa fa-long-arrow-down" style="visibility: hidden"></i>删除日期</dt>
								<dt id="orderField_name">
									<i class="fa fa-long-arrow-down" style="visibility: hidden"></i>文件名</dt>
							</dl>
						</div>
						<button onclick="clearTrash();">清空回收站</button>
					</div>
				</div>
			</div>
			<div id="breadcrumd" class="cl">
				<ol class="breadcrumb">
					<li class="active">
						<span class="txt-ellipsis" title="回收站">回收站</span>
					</li>
				</ol>
			</div>
			<div>
				<div class="abslayout" style="bottom: 16px;top:150px;right: 0;left: 0;min-height: 200px">
					<table style="padding-bottom: 0;padding-top: 0;">
						<thead>
							<tr>
								<th style="min-width: 280px">文件名</th>
								<th style="width: 180px">大小</th>
								<th style="width: 180px">删除日期</th>
							</tr>
						</thead>
					</table>
					<div class="abslayout" style="left:16px;right:16px;overflow: auto;bottom: 40px;top:40px;">
						<table style="padding-top: 0">
							<tbody id="trashFileList">
							</tbody>

						</table>
						<div class="notfind" style="display: none">
							<p>回收站为空</p>
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
					<div class="arrow" style="left: 77px;"></div>
					<dl class="menu">
						<dt role="1,2,3," command="1">
							<i class="fa fa-sitemap"></i>恢复</dt>
						<dt role="1,3," command="2">
							<i class="fa fa-sitemap"></i>永久删除</dt>
					</dl>
				</div>

				<!-- <div class="inbox-catalong-blank"></div>
			<div class="inbox-catalong">
				<div class="inbox-catalong-header">
					<ul id="trashFileList">
<li class="line-scroll-wrapper" id="trashFile_{{id}}">
				<div class="file file-info" onclick="onPress({{id}})">
					<div class="img {{imgClass}}"></div>
					<div class="fileName">{{name}}</div>
					<input type="text" disabled="disabled" value="个人文件{{path}}"/>
					<span class="trashFileTemplate-route-boy">{{path}}</span>
					<i>{{modifiedAt}}</i>
				</div>
				<div class="line-buttons">
					<div class="line-button line-button-share" onclick="trashIdexlinebButtonShare({{id}})">恢复</div>
					<div class="line-button line-button-link" onclick="trashIdexlinebButtonLink({{id}})">删除</div>
				</div>
			</li>
					</ul>
				</div>
			</div> -->
			</div>

			<script id="trashFileTemplate" type="text/template7">

				<tr title='{{name}}' id="trashFile_{{id}}" ids='{{id}}'>
					<td style="min-width: 280px" onclick="onPress({{id}})">
						<div class="fileitem-temple1 cl">
							{{#js_compare "this.imgPath==null"}}
							<i class='{{iconimg}}'></i>
							{{else}}
							<img src='{{imgPath}}' style="float: left;
								width: 32px;
								height: 32px;
								margin: 16px 10px 10px 10px;" /> {{/js_compare}}
							<span>
								<h3 class="txt-ellipsis">{{name}}</h3>
								<label>{{path}}</label>
							</span>
							<u style="margin-top: 5px">
								<a id="worker" data="1">
									<i class="fa fa-ellipsis-h"></i>
								</a>
							</u>
						</div>
					</td>
					{{#js_compare "this.size=='' || this.size == 'NaNGB'"}}
					<td style="width: 180px">——</td>
					{{else}}
					<td style="width: 180px">{{size}}</td>
					{{/js_compare}}
					<td style="width: 180px">{{modifiedAt}}</td>
				</tr>
			</script>
			<%@ include file="../common/footer4.jsp"%>
	</body>

	</html>