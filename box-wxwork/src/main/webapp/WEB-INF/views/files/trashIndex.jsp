<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>

	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>回收站</title>
		<%@ include file="../common/include.jsp"%>
	<link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/index.css"/>
		<link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/files/trashIndex.css" />
		<script src="${ctx}/static/js/common/line-scroll-animate.js"></script>
		<script src="${ctx}/static/js/files/trashIndex.js"></script>
	</head>

	<body>
		<div>
			<div class="load">
				<div class="load-img"><img src="${ctx}/static/skins/default/img/load-rotate.png" /></div>
				<div class="load-text">正在加载</div>
			</div>
			<div style="overflow:  hidden;background: #f8f8f8;">
					<div class="folder-order" id="folderOrder" style="float: right;margin-right: 0.5rem">
						<i class=""><img src="${ctx}/static/skins/default/img/sort.png"/></i>
						<div class="weui-cells weui-cells_radio change-cells-radio" id="sortRadio" style="display:none;position: fixed;z-index: 22">
							<label class="weui-cell weui-check__label" for="x11" id="dateSort">
								<div class="weui-cell__bd change-cells-bd" >
									<p>日期</p>
								</div>
								<div class="weui-cell__ft">
									<input type="radio" class="weui-check" name="radio1" id="x11" checked="checked">
									<span class="all-sort-img"><i class=""></i></span>
								</div>
							</label>
							<label class="weui-cell weui-check__label" for="x12" id="nameSort">
								<div class="weui-cell__bd change-cells-bd">
									<p>名称</p>
								</div>
								<div class="weui-cell__ft">
									<input type="radio" name="radio1" class="weui-check" id="x12" >
									<span class="all-sort-img"><i></i></span>
								</div>
							</label>
						</div>
					</div>
				<div style="overflow: hidden;float: left;margin: 0.6rem">
					<i style="float: left;display:  inline-block;width: 1rem;"><img style="width: 100%" src="${ctx}/static/skins/default/img/per-trash-icon.png"/></i>
					<div class="" style="float: left;margin-left: 0.2rem" onclick="clearTrash();">清空回收站</div>
				</div>
			</div>
		</div>
	<div id="box">
	<div id="trashFileList"></div>

	</div>
	</div>

		<script id="trashFileTemplate" type="text/template7">
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
									<span>{{ownedBy}}</span>
									<span>|</span>
									<span>{{modifiedAt}}</span>
									{{#js_compare "this.type == 1"}}
										<span>|</span>
										<span>{{size}}</span>
									{{else}}
										<span></span>
									{{/js_compare}}
								</div>
							</div>
							<div class="index-recent-right" id="trashFile_{{id}}" >
								<i><img src="${ctx}/static/skins/default/img/operation.png" alt=""></i>
							</div>
						</div>
					</div>
				</div>
				<div class="weui-cell__ft">
				<a class="weui-swiped-btn index-share-btn" onclick="trashIdexlinebButtonShare('{{id}}')" href="javascript:">恢复</a>
				<a class="weui-swiped-btn index-link-btn" onclick="trashIdexlinebButtonLink('{{id}}')" href="javascript:">删除</a>
				</div>
			</div>
		</script>
		<%@ include file="../common/footer4.jsp"%>
	</body>

</html>