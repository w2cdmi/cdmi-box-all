<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
	<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

		<!DOCTYPE html>
		<html>

		<head>
			<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
			<title>我的</title>
			<%@ include file="../common/include.jsp"%>
				<link rel="stylesheet" href="${ctx}/static/skins/default/css/user/personal.css">
		</head>

		<body>
			<div class="box">
				<div class="per-header">
					<ul>
						<li>
							<div class="per-header-portrait"></div>
							<p>
								<img src="${ctx}/userimage/getLogo" />
							</p>
				</div>
				<div class="per-name">
					<i>${user.name}</i>
					<%-- <span>研发部</span>--%>
						<div>
							<p id="useSpace"></p>
							<div class="totalSize" style="display:none">
								<div id="spaceBar"></div>
							</div>
						</div>
				</div>
				</li>
				</ul>
			</div>
			<div class="per-content">
				<div class="per-gap"></div>
				<ul>
					<li onclick="gotoPage('${ctx }/uploadFolder/getUploadFilePage')">
						<p>
							<img src="${ctx}/static/skins/default/img/pre-transfer.png" />
						</p>
						<span>传输列表</span>
						<i>
							<img src="${ctx}/static/skins/default/img/putting-more.png" />
						</i>
					</li>
					<li onclick="gotoPage('${ctx}/myShares')">
						<p>
							<img src="${ctx}/static/skins/default/img/pre-issue.png" />
						</p>
						<span>我发出的共享</span>
						<i>
							<img src="${ctx}/static/skins/default/img/putting-more.png" />
						</i>
					</li>
					<li onclick="gotoPage('${ctx }/sharedlinks')">
						<p>
							<img src="${ctx}/static/skins/default/img/pre-issue-w.png" />
						</p>
						<span>我外发的文件</span>
						<i>
							<img src="${ctx}/static/skins/default/img/putting-more.png" />
						</i>
					</li>
				</ul>
				<div class="per-gap"></div>
				<ul>

					<li onclick="gotoPage('${ctx }/trash')">
						<p>
							<img src="${ctx}/static/skins/default/img/per-recycle.png" />
						</p>
						<span>回收站</span>
						<i>
							<img src="${ctx}/static/skins/default/img/putting-more.png" />
						</i>
					</li>
					<li onclick="clearLocalStroage()">
						<p>
							<img src="${ctx}/static/skins/default/img/clear.png" />
						</p>
						<span>清除本地缓存</span>
						<span style="float:right" id="localStroageSize">0B</span>
					</li>
				</ul>
				<div class="per-gap"></div>
				<ul>
					<li>
						<p>
							<img src="${ctx}/static/skins/default/img/pre-protocol.png" />
						</p>
						<span>用户协议</span>
						<i>
							<img src="${ctx}/static/skins/default/img/putting-more.png" />
						</i>
					</li>
				</ul>
				<div class="per-gap"></div>
				<ul>
					<li>
						<p>
							<img src="${ctx}/static/skins/default/img/pre-protocol.png" />
						</p>
						<span>企业后台管理</span>
						<i>
							<img src="${ctx}/static/skins/default/img/putting-more.png" />
						</i>
					</li>
				</ul>
			</div>
			</div>

			<%@ include file="../common/footer4.jsp"%>
		</body>
		<script type="text/javascript">
			$(function () {
				getUserSpaceInfo();
				setLocalStroageSize();
			});

			function getUserSpaceInfo() {
				$.ajax({
					type: "GET",
					url: ctx + "/user/info?" + new Date().getTime(),
					error: function () {
						$.toast("获取用户存储空间信息失败", "cancel");
					},
					success: function (data) {
						if (data.spaceQuota == -1) {
							$("#spaceBar").css("width", "0%");
							$("#useSpace").html(formatFileSize(data.spaceUsed) + "&nbsp;/&nbsp;无限制");
							$(".totalSize").css("display", "none");
						} else {
							$("#spaceBar").css("width", formatFileSize(data.spaceUsed) / formatFileSize(data.spaceQuota));
							$("#useSpace").html(formatFileSize(data.spaceUsed) + "&nbsp;/&nbsp;" + formatFileSize(data.spaceQuota));
							$(".totalSize").css("display", "block");
						}
					},
					complete: function () {
						$('.load').css('display', 'none');
					}

				});
			}
			//获取本地存储大小
			function setLocalStroageSize() {
				var size = 0;
				for (var i = 0; i < localStorage.length; i++) {
					var key = localStorage.key(i);
					size += localStorage.getItem(key).length;
				}
				if (size != 0) {
					$("#localStroageSize").html(formatFileSize(size));
				} else {
					$("#localStroageSize").html("0B");
				}
			}

			function clearLocalStroage() {
				// confirm({
				// 	  title: '确认清除',
				// 	  text: "不会影响已经下载文件，下载文件碎片以及传输列表内容会被清除",
				// 	  onOK: function () {
				// 		  localStorage.clear();
				// 		  setLocalStroageSize();
				// 	  }
				// });
				var r = confirm("清除缓存不会影响已经下载文件，下载文件碎片以及传输列表内容会被清除")
				if (r == true) {
					localStorage.clear();
					setLocalStroageSize();
				} else {
					alert('您取消了清除');
				}
			}
		</script>

		</html>