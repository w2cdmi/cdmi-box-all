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
    </head>
    <body>
		<div id="box">
			
			<div id="header">
		    	<div id="upload">
		    		<img src="${ctx}/static/skins/default/img/header-upload.png" id="upload_all" />
		    	</div>
		    	<div id="photograph">
		    		<img src="${ctx}/static/skins/default/img/header-photograph.png" id="upload_photo" />
		    	</div>
		    	<div id="tape">
		    		<img src="${ctx}/static/skins/default/img/header-tape.png" id="upload_record" />
		    	</div>
	    	</div>
	    	<div class="fillBackground"></div>
	    	<div id="list">
	    		<span>最近浏览</span><i></i>
	    	</div>
	    	<a id="downloadFile" download style="display:none"></a>
	    	<div class="file-directory">
	    		<ul id="historyFile" class="file-background"></ul>
	    	</div>
	    	<div class="fillBackground"></div>
	    	<div id="list">
	    		<span>快捷目录</span><i></i>
	    	</div>
	    	<div class="file-directory" style="height:2.8rem;">
	    		<ul id="shortcutDirectory" class="ananan">
	    		</ul>
	    	</div>
	    	<div class="fillBackground"></div>
	    	<div class="Page-tail-navigation">
	    		<ul id="spaceList">
	    			<li onclick="gotoPage('${ctx}/folder?rootNode=0')">
	    				<img src="${ctx}/static/skins/default/img/personal-icon.png"/>
	    				<span>个人文件</span>
	    			</li>
	    		</ul>
	    	</div>
		</div>
		<%@ include file="common/footer1.jsp" %>
		<!--文件上传页面-->
		<div class="file-upload" style="display: none;">
			<div class="file-upload-header">
				<div class="file-doc"></div>
				<div class="folder-name">
					<span>新项目试用研究方案.DOC</span>
					<div class="folder-name-icon"></div>
				</div>
			</div>
			<div class="file-upload-nav">
				<div class="file-upload-nav-middle">
					<div class="upload-nav-h">保存到以下文件夹</div>
					<div class="upload-nav-bottom">
						<div class="folder-icon"></div>
						<p>个人空间>新建文件夹>项目案列</p>
						<span><img src="${ctx}/static/skins/default/img/index-more-icon.png"/></span>
					</div>
				</div>
			</div>
			<div class="file-upload-tail">
				<h1>选择快捷目录</h1>
				<ul>
					<li>
						<div class="folder-icon"></div><span>个人空间>新建文件夹>项目案列</span>
					</li>
					<li>
						<div class="folder-icon"></div><span>个人空间>新建文件夹>项目案列</span>
					</li>
				</ul>
			</div>
		</div>
	</body>
</html>

