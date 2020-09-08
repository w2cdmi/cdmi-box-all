<<<<<<< HEAD
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="javax.sound.midi.SysexMessage" %>
<!DOCTYPE html>
<html>
<meta http-equiv="Access-Control-Allow-Origin" content="*">
<link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/folder/folderIndex.css"/>
<%@ include file="../common/include.jsp" %>
<script src="${ctx}/static/js/folder/folderIndex.js"></script>
<head>
</head>


<div id="box">
	<!-- 上传进度条 -->
	<div id="top">
		<div class="midd">
			<div class="shijian">预计剩余时间：<span></span></div>
			<div class="pre">0%</div>
			<div class="KB"><span>155</span>KB/<i>s</i></div>
		</div>
	</div>
	<input id="fileUpload" type="file" name="file[]" onchange="selectFileDir()" multiple="multipart" hidden />
	
	<div id="header">
    	<div id="upload">
    		<!--<img src="${ctx}/static/skins/default/img/file-up.png"/>
    		<div id="upload_all" class="upload_btn">上传</div>-->
    		<img src="${ctx}/static/skins/default/img/header-upload.png" id="upload_all" />
    	</div>
    	<div id="photograph">
    		<!--<img src="${ctx}/static/skins/default/img/camera.png" />
    		<div id="upload_photo" class="upload_btn">拍照</div>-->
    		<img src="${ctx}/static/skins/default/img/header-photograph.png"/>
    	</div>
    	<div id="tape">
    		<!--<img src="${ctx}/static/skins/default/img/camera.png" />
    		<div id="upload_record" class="upload_btn">录音</div>-->
    		<img src="${ctx}/static/skins/default/img/header-tape.png"/>
    	</div>
   	</div>
   	<div class="fillBackground"></div>
   	<div class="list-menu">
   		<div class="list-slide">
			<div class="left">
				<div onclick="newFolderDialog()" class="left1">新建文件夹&nbsp;</div>
			</div>
			<div class="bottom">
				<div>排序：</div>
				<div>日期</div>
				<div>&nbsp;↓</div>
			</div>
		</div>
	</div>
   	
   	<div class="list-menu">
		<div class="left" id="directory">
			<div onclick="jumpFolder(this,1,0);">个人文件&nbsp;</div>
		</div>
		
		<div class="right">
			<i></i>
			<i></i>
		</div>
	</div>
	<div id="fileList">
		
	</div>
	
	<form id="downloadForm" action="" method="get" style="display: none" target=""></form>
	
</div>

<%@ include file="../common/footer1.jsp" %>
		


<script type="text/javascript">
	var catalogParentId = <c:out value='${parentId}'/>;
	var isLinkHidden = <c:out value='${linkHidden}'/>;
	var canPreview =<%=PreviewUtils.isPreview()%>;
	var reqProtocol = "<%=request.getSession().getAttribute("reqProtocol")%>";
</script>
</body>
</html>
