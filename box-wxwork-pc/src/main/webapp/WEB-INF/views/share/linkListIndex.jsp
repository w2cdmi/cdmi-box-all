<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
	<!DOCTYPE html>
	<html>

	<head>
		<meta http-equiv="Access-Control-Allow-Origin" content="*">
		<%@ include file="../common/include.jsp" %>
			<title>我外发的文件</title>
			<link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/main.css" />
			<link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/share/linkListlndex.css" />
			<script src="${ctx}/static/components/Components.js"></script>
			<script src="${ctx}/static/components/Menubar.js"></script>
			<script src="${ctx}/static/components/Pagination.js"></script>
			<style>
				.fileitem-temple1>span>h3 {
					line-height: 40px;
				}

				.file-info {
					padding: 5px;
					height: 40px;
					width: 500px;
					position: relative;
				}

				.file-info>i {
					width: 32px;
					height: 32px;
					display: inline-block;
				}

				.file-info>.file-name {
					padding: 0 0 5px 10px;
					font-size: 18px;
					display: block;
					position: absolute;
					top: 0;
					left: 32px;
				}

				.file-info>.file-size {
					padding: 5px 5px 5px 10px;
					height: 16px;
					display: block;
					position: absolute;
					top: 16px;
					left: 32px;
					color: #777;
				}

				.info-title {
					height: 20px;
					line-height: 20px;
					border-bottom: 1px solid #ccc;
					color: #777;
					font-weight: bold;
				}

				.info-content {
					padding: 5px;
				}

				.info-content div {
					padding: 5px;
				}

				.user-img {
					width: 32px;
					height: 32px;
					float: left;
				}

				.user-img>img {
					width: 32px;
					height: 32px;
					display: inline-block;
					border-radius: 50%;
				}

				.user-img>i {
					width: 32px;
					height: 32px;
					display: inline-block;
				}

				.user-name {
					height: 32px;
					line-height: 32px;
					float: left;
				}

				.user-role {
					height: 32px;
					line-height: 32px;
					float: right;
				}

				.node_name {
					display: inline-block;
					max-width: 200px;
					overflow: hidden;
					white-space: nowrap;
					text-overflow: ellipsis;
				}
			</style>
	</head>

	<body>
		<%@ include file="../common/menubar.jsp" %>
			<div id="linkListHeader">
				<a id="downloadFile" download style="display:none"></a>
				<div id="toolbar" class="cl">
					<div class="left">
						<button id="sort_button">
							<i class="fa fa-sort-alpha-asc"></i>排序</button>
						<div class="popover bottom" id="sort_popover">
							<div class="arrow" style="left: 50px;"></div>
							<dl class="menu">
								<!-- <dt id="orderField_modifiedAt">
							<i class="fa fa-long-arrow-down" style="visibility: hidden"></i>创建时间</dt> -->
								<dt id="orderField_name">
									<i class="fa fa-long-arrow-down" style="visibility: visible"></i>文件名</dt>
							</dl>
						</div>
					</div>
				</div>
			</div>
			<div id="breadcrumd" class="cl">
				<ol class="breadcrumb">
					<li class="active">
						<span class="txt-ellipsis" title="我外发的文件">我外发的文件</span>
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
								<th style="width: 180px">创建时间</th>
							</tr>
						</thead>
					</table>
					
					<div class="abslayout" id="datagrid" style="left:16px;right:16px;overflow: auto;bottom: 40px;top:40px;">
						<table style="padding-top: 0">
							<tbody id="linkList">
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
					<div class="arrow" style="left: 78px;"></div>
					<dl class="menu">
						<dt id="gotoinner" role="1,2,3," command="1">
							<i class="fa fa-sign-in"></i>进入目录</dt>
						<dt id="check" role="1,3," command="2">
							<i class="fa fa-list-ul"></i>查看详情</dt>
						<dt role="1,3," command="3">
							<i class="fa fa-chain-broken"></i>取消外发</dt>
					</dl>
				</div>
				<div id="infoDialog">
					<div id="fileInfo">
						<div class="file-info">
							<i id="fileIco"></i>
							<span class="file-name" id="fileName"></span>
							<span class="file-size" id="fileSize"></span>
						</div>
						<div class="info-title">外发</div>
						<div class="info-content" id="info-linkList" style="max-height:200px;overflow: auto;">
						</div>
						<div class="info-title">共享</div>
						<div class="info-content" id="info-shareList" style="max-height:200px;overflow: auto;">
						</div>
					</div>
				</div>
					<%@ include file="../common/footer4.jsp" %>
					<%@ include file="../common/video.jsp" %>
					<a id="downloadFile" download style="display:none"></a>
						<script id="linkTemplate" type="text/template7">
							<tr onclick="optionInodeLink('{{ownedBy}}','{{id}}',this)" id="link_{{id}}" ids='{{id}}' ownedBys='{{ownedBy}}' types='{{type}}' names='{{name}}'>
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
						<script src="${ctx}/static/js/common/line-scroll-animate.js"></script>
						<script src="${ctx}/static/js/share/linkListIndex.js"></script>

	</body>

	</html>