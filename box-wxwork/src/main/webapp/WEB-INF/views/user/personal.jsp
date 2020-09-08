<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
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
	<div class="box" style="position: fixed;bottom: 3rem;left: 0;right: 0;top: 0;">
		<div class="per-header">
			<ul>
				<li>
					<div class="per-header-portrait">
						<p>
							<img src="${ctx}/userimage/getLogo" />
						</p>
					</div>
					<div class="per-name">
						<i>${user.name}</i><%-- <span>研发部</span>--%>
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
					</p> <span>上传列表</span> <i><img
						src="${ctx}/static/skins/default/img/putting-more2.png" /></i>
				</li>
				<li onclick="gotoPage('${ctx}/myShares')">
					<p>
						<img src="${ctx}/static/skins/default/img/pre-issue.png" />
					</p> <span>我发出的共享</span> <i><img
						src="${ctx}/static/skins/default/img/putting-more2.png" /></i>
				</li>
				<li onclick="gotoPage('${ctx }/sharedlinks')">
					<p>
						<img src="${ctx}/static/skins/default/img/pre-issue-w.png" />
					</p> <span>我外发的文件</span> <i><img
						src="${ctx}/static/skins/default/img/putting-more2.png" /></i>
				</li>
			</ul>
			<div class="per-gap"></div>
			<ul>

				<li onclick="gotoPage('${ctx }/trash')">
					<p>
						<img src="${ctx}/static/skins/default/img/per-recycle.png" />
					</p> <span>回收站</span> <i><img
						src="${ctx}/static/skins/default/img/putting-more2.png" /></i>
				</li>
				<li onclick="clearLocalStroage()">
					<p>
						<img src="${ctx}/static/skins/default/img/clear.png" />
					</p> <span>清除本地缓存</span>
					<span style = "float:right" id="localStroageSize">0B</span>
				</li>
                </ul>
			<div class="per-gap"></div>

			<ul>
				<li onclick="gotoPage('${ctx }/user/agreement')">
					<p><img src="${ctx}/static/skins/default/img/per-agreement.png"/></p>
					<span>用户协议</span>
					<i><img src="${ctx}/static/skins/default/img/putting-more2.png"/></i>
				</li>
			</ul>

		</div>
	</div>

	<%@ include file="../common/footer4.jsp"%>
	<script src="${ctx}/static/js/personal/personal.js"></script>
</body>

</html>
