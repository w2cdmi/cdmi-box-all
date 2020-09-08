<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>进度</title>
<script src="${ctx}/static/js/common/folder-chooser.js"></script>
<style type="text/css">
body{
	user-select:none;
	-webkit-user-select:none;
	font-family: Microsoft YaHei, serif;
	margin:0;
	background: #F9F9F9;
}
#top{
	height: 1.8rem;
	background: #7F8C94;
	position: relative;
	width: 100%;
	z-index: 102;
}
#top .midd{
	width: 0%;
	background: #3AB321;
	height: 1.8rem;
}
#top .midd .shijian{
	float:  left;
	margin-left: 0.8rem;
	color: #FFFFFF;
	height: 1.8rem;
	line-height: 1.8rem;
	width: 8rem;
	font-size: 0.6rem;
}
#top .midd .pre{
	color: #FFFFFF;
	font-size: 0.6rem;
	position: absolute;
	line-height: 1.8rem;
	left: 50%;
	transform: translateX(-50%);
}
#top .midd .KB{
	font-size: 0.6rem;
	color: #FFFFFF;
	position: absolute;
	line-height: 1.8rem;
	right: 0.3rem;
	padding-right: 0.8rem;
}
/*上传列表页面*/
.file-upload{
	width: 100%;
	height: 100%;
	overflow: hidden;
	position:fixed;
	z-index:101;
	top:0;
	background: #FFFFFF;
	overflow-y: scroll;
}
.file-upload-content{
	width: 100%;
	float: left;
	border-radius: 0.35rem;
	position: relative;
	margin-bottom: 7rem;
}
.file-upload-header{
	width: 100%;
	height: 5rem;
	position: relative;
}
.file-upload-nav-middle{
	width: 92%;
	height: 5rem;
	margin-left: 4%;
}
.upload-nav-h{
	width: 100%;
	height: 2rem;
	line-height: 2rem;
	font-size: 0.75rem;
	color: #333333;
}
.file-upload-header div:nth-child(1){
	width: 100%;
	height: 2.4rem;
	margin: 0;
	margin-top: 0.5rem;
}
.folder-name{
	height: 1.1rem;
	position: absolute;
	left: 50%;
	transform: translateX(-50%);
	top: 3.4rem;
}
.folder-name span{
	line-height: 1.1rem;
	float: left;
	color: #333333;
	font-size: 0.75rem;
	overflow: hidden;
	text-overflow: ellipsis;
    word-break: keep-all;
    white-space: nowrap;
}
.folder-name-icon{
	width: 1.1rem;
	height: 1.1rem;
	background: url(../img/modify-icon.png) no-repeat center center;
	background-size:1.1rem 1.1rem;
	position: absolute;
	right: 0;
}
.file-upload-nav{
	width: 100%;
	display: inline-block;
	background: #FFFFFF;
}
.folder-icons{
	margin-top: 0.45rem;
	margin-left: 0.3rem;
}
.file-upload-nav-middle{
	width: 92%;
	margin-left: 4%;
}
.file-upload-nav-middle .upload-nav-h{
	width: 100%;
	float: left;
	line-height: 2rem;
	font-size: 0.6rem;
	color: #333333;
	border-bottom: 1px solid #E4E1E1;
}
.upload-nav-bottom div{
	height: 2.55rem;
	width: 1.8rem;
	background-size:1.8rem 1.8rem;
	float: left;
	margin-bottom:0.45rem;
}
.upload-nav-bottom p{
	float: left;
	line-height: 3.45rem;
	font-size: 0.6rem;
	color: #333333;
	width: 10rem;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}
.upload-nav-bottom span{
	float: right;
}
.upload-nav-bottom span img{
	width: 0.9rem;
	height: 0.75rem;
	margin-top: 1.45rem;
}
.file-upload-tail{
	width: 100%;
	display: inline-block;
}
.file-upload-content-top h1{
	font-weight: normal;
	width: 92%;
	margin-left: 4%;
	height: 2rem;
	line-height: 2rem;
	font-size: 0.6rem;
	color: #333333;
	border-bottom:1px solid #E4E1E1;
}
.file-upload-tail ul li{
	width: 92%;
	margin-left: 4%;
	height: 3.45rem;
	border-bottom: 1px solid #E4E1E1;
	line-height: 3rem;
	font-size: 0.6rem;
	color: #333333;
}
.file-upload-tail ul li>div{
	float: left;
	background-size:1.8rem 1.8rem;
	width: 1.8rem;
}
.file-upload-tail ul li span{
	float: left;
	line-height: 3.45rem;
	max-width: 12rem;
	white-space: nowrap;
	overflow: hidden;
	text-overflow: ellipsis;
}
.file-upload-footer {
	position:fixed;
	bottom: 0;
	padding-bottom: 1rem;
	background: #FFFFFF;
	width: 100%;
	border-top: 1px solid #E4E1E1;
	height: 6rem;
}
.file-upload-footer-top{
	width: 90%;
	height: 2.2rem;
	line-height: 2.2rem;
	text-align: center;
	font-size: 0.75rem;
	color: #FFFFFF;
	background: #18B4ED;
	border-radius: 0.35rem;
	margin-left: 5%;
	margin-top: 1rem;
	float: left;
}
.file-upload-footer-bottom{
	width: 90%;
	height: 2.2rem;
	background: #FC6156;
	text-align: center;
	line-height: 2.2rem;
	font-size: 0.75rem;
	float: left;
	border-radius: 0.35rem;
	color: #FFFFFF;
	margin-left: 5%;
	margin-top: 0.6rem;
}
/*创建文件夹页面样式*/
#uploadFileImg{
	margin-top: 0.7rem;
}
#uploadFileName{
	font-size: 0.6rem;
	color: #333333;
	height: 1.2rem;
	width: 15rem;
	position: relative;
	text-align: center;
}
#uploadFileName input{
	position: absolute;
	left: 50%;
	-webkit-transform: translateX(-50%);
}
</style>

</head>
<body>
<!-- 上传进度条 -->
<div id="top" style="display:none;">
	<div class="midd">
		<div class="shijian">预计剩余时间：<span id="uploadTime"></span></div>
		<div class="pre">0%</div>
		<div class="KB" id="uploadSpend"></div>
	</div>
</div>
<input id="fileUpload" type="file" name="file[]" onchange="openSelectDirModel()" multiple="multipart" hidden />

<!--文件上传页面-->
<div class="file-upload" style="display: none;">
	<div class="file-upload-content">
			<div class="file-upload-content-top">
				<div class="fillBackground"></div>
				<div class="file-upload-header">
					<div id="uploadFileImg"></div>
					<div class="folder-name">
						<span id="uploadFileName"></span>
					</div>
				</div>
				<div class="fillBackground"></div>
				<div class="file-upload-nav">
					<div class="file-upload-nav-middle">
						<div class="upload-nav-h">保存到以下文件夹</div>
						<div class="upload-nav-bottom">
							<div class="folder-icon"></div>
							<p id="selectedDir"></p>
							<span onclick="selectUploadDir()"><img src="${ctx}/static/skins/default/img/index-more-icon.png"/></span>
						</div>
					</div>
				</div>
				<div class="fillBackground"></div>
				<h1>选择快捷目录</h1>
			</div>
			<div class="file-upload-tail">
				<ul id="shortDirDiv">
				</ul>
			</div>
		</div>
		<div class="file-upload-footer">
			<div class="file-upload-footer-top" onclick="startUploadFile()">确认</div>
			<div class="file-upload-footer-bottom" onclick="closeSelectDirModel()">取消</div>
		</div>
	</div>
</div>
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
		<div id="chooserFileList"class="line-content-father"></div>
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
				<div class="img folder-icon folder-icons"></div>
				<div class="fileName">{{name}}</div>
			</div>
		</div>
	</div>
</script>
</body>
</html>

<script type="text/javascript">
var topTotalWidth;
var topUnit;
var isStart=true;

var uploadingFiles = null;			//正在上传文件列表
var uploadedFiles = null; 			//已经上传完成的文件
var isFileUpload = false;	//是否有文件上传
var currentIndex = 0;	//上传文件队列索引
var spendbefore = 0;  	//上次上传大小

var file = null;
var fileType = 1;		//1：文件   0：文件夹

var orderField = 'modifiedAt';
var isDesc = true;
var uploadingParentId = null;	//正在上传文件的父级目录

var teamspaceRole = '${memberInfo.teamRole}'; //空间角色  只用来判断是否是空间
var click_menu = "uploadFile";  //点击菜单  uploadFile、uploadPhoto、createFolder
var serverId = null;	//照片上传到微信服务器Id
var teamspaceOwnerId = 0; //协作空间ownerId
var tempOwnerId = ownerId; //保存ownerId值

$(function(){
	if(parentId == ""){
		parentId = 0;
	}
	
	//获取本地是否有上传的文件
	var tempUploadingFiles = localStorage.getItem("uploadingFiles");
	if (tempUploadingFiles != null) {
		uploadingFiles = JSON.parse(tempUploadingFiles);
	}
	
	var tempUploadedFiles = localStorage.getItem("uploadedFiles");
	if(tempUploadedFiles != null){
		uploadedFiles = JSON.parse(tempUploadedFiles);
	}
	
	topTotalWidth = $('#top').width();
	topUnit = topTotalWidth/100;
	
	if (uploadingFiles != null && uploadingFiles.length >0) {
		isFileUpload = true;
		isStart = true;
		startProgress();
	}
	
	if (isFileUpload) {
		$("#top").show();
	}else{
		$("#top").hide();
		$('.midd').width(0);
		$(".pre").html("0%");
	}
});

function clickUpload(){
	$("#fileUpload").click();
}

function startProgress(){
	var topCurrentWidth = localStorage.getItem("topCurrentWidth");
	var triggerCondition=null;	//进度条触发条件
	if (topCurrentWidth != 0) {
		//初始化进度条
		$('.midd').width(topCurrentWidth);
		$(".pre").html(parseInt(topCurrentWidth/topTotalWidth*100)+"%");
		triggerCondition = "continuation";
	}else{
		//初始化进度条
		$('.midd').width(0);
		$(".pre").html("0%");
	}
	
	if(isStart){
		topFilePrgTimer = window.setInterval(function(){setTopFilePrg(triggerCondition)},100);
		isStart = false;
	}else{
		clearInterval(topFilePrgTimer);
		isStart = true;
	}
}

function uploadFile(){
	$("#top").show();
	spendbefore = 0;
	
	// 遍历文件列表，插入到表单数据中 (暂时只支持单文件上传)
	//for (var i = 0, file; file = oFiles[i]; i++) {
		i = 0;
		var tempFileInfo;
		if(uploadingFiles==null || uploadingFiles.length==0){
			tempFileInfo = [{"name":file.name,"state":"on","size":file.size,"complete":0,"createdAt":getFormatDate(new Date(),"yyyy/MM/dd")}];
			var v1 = JSON.stringify(tempFileInfo);
			uploadingFiles = JSON.parse(v1);
		}else{
			tempFileInfo = {"name":file.name,"state":"on","size":file.size,"complete":0,"createdAt":getFormatDate(new Date(),"yyyy/MM/dd")};
			uploadingFiles.push(tempFileInfo);
		}
		
		var formData = new FormData();
		formData.append(file.name,file);
		
		localStorage.setItem("uploadingFiles", JSON.stringify(uploadingFiles));
		
		$.ajax({
			url:"${ctx}/files/preUpload",
			type:"POST",
			async: true,
			data:{
				"ownerId":ownerId,
				"parentId":uploadingParentId,
				"name":file.name,
				"size":file.size,
				"token":token
			},
			success: function (data, textStatus, jqXHR) {
              var preUrl = data + "?objectLength=" + file.size;
              $.ajax({
	            	url: preUrl,
	            	type:"POST",
	            	//async: false,
	            	data:formData,
	            	processData: false,	// 告诉jQuery不要去处理发送的数据
	            	contentType: false, // 告诉jQuery不要去设置Content-Type请求头
	            	xhr: function(){
	            		var xhr = new window.XMLHttpRequest();
	            	    xhr.upload.addEventListener("progress", onprogress, false);
	            		return xhr; //xhr对象返回给jQuery使用
	            	},
	            	success: function (data) {
	            		$(".pre").html("完成");
	            		
	            		if (uploadedFiles==null || uploadedFiles.length==0) {
	            			var tempFile = uploadingFiles[0];
	            			tempFile.complete = 100;
	            			tempFile.state = "success";
	            			var tempFileStr = "["+ JSON.stringify(tempFile) + "]";
	            			uploadedFiles = JSON.parse(tempFileStr);
						}else{
							var tempFile = uploadingFiles[0];
							tempFile.complete = 100;
	            			tempFile.state = "success";
	            			uploadedFiles.push(tempFile);
						}
	            		uploadingFiles.splice(0,1);		//删除第一个上传文件信息
	            		
	            		localStorage.setItem("uploadedFiles",JSON.stringify(uploadedFiles));
	            		localStorage.setItem("uploadingFiles",JSON.stringify(uploadingFiles));
	            		if(uploadingFiles.length == 0){
		            		localStorage.setItem("topCurrentWidth",0);
		            		$("#top").hide();
		            		$('.midd').width(0);
		            		$(".pre").html("0%");
	            		}else{
	            			//获取下一个文件的上传信息
	            			localStorage.setItem("topCurrentWidth",0);
	            			$('.midd').width(uploadingFiles[0].complete +"%");
		            		$(".pre").html(uploadingFiles[0].complete +"%");
		            		localStorage.setItem("topCurrentWidth",topTotalWidth*uploadingFiles[0].complete*0.01);
	            		}
	            		if(teamspaceRole!=""){
	            			listFile();
	            			return;
	            		}
	            		if(uploadingParentId == parentId && ownerId == tempOwnerId){
							listFile();
	            		}else{
	            			$.confirm("文件上传成功，是否进入文件目录", function() {
	            				if(teamspaceOwnerId == 0){
		            				gotoPage(ctx + "/folder?rootNode=" + uploadingParentId);
		            			}else{
		            				gotoPage(ctx + "/teamspace/file/" + teamspaceOwnerId +"?parentId=" + uploadingParentId);
		            			}
	            			  }, function() {
	            			  });
	            		}
	            	},
	            	error:function(){
	            		if (uploadedFiles==null || uploadedFiles.length==0) {
	            			var tempFile = uploadingFiles[0];
	            			tempFile.complete = 0;
	            			tempFile.state = "fail";
	            			var tempFileStr = "["+ tempFile + "]";
	            			uploadedFiles = JSON.parse(JSON.stringify(tempFileStr));
						}else{
							var tempFile = uploadingFiles[0];
							tempFile.complete = 0;
	            			tempFile.state = "fail";
	            			uploadedFiles.push(tempFile);
						}
	            		
	            		uploadingFiles.splice(0,1);
	            		localStorage.setItem("uploadedFiles",JSON.stringify(uploadedFiles));
	            		localStorage.setItem("uploadingFiles",JSON.stringify(uploadingFiles));
	        
	            		
	            		$(".pre").html("失败");
	            		$.alert("上传存储失败");
	            		$("#top").hide();
	            		$('.midd').width(0);
	            		$(".pre").html("0%");
	            		
	            		if(uploadingFiles.length == 0){
		            		$(".pre").html("失败");
		            		localStorage.setItem("topCurrentWidth",0);
		            		$("#top").hide();
		            		$('.midd').width(0);
		            		$(".pre").html("0%");
	            		}else{
	            			//获取下一个文件的上传信息
	            			localStorage.setItem("topCurrentWidth",0);
	            			$('.midd').width(uploadingFiles[0].complete +"%");
		            		$(".pre").html(uploadingFiles[0].complete +"%");
		            		localStorage.setItem("topCurrentWidth",topTotalWidth*uploadingFiles[0].complete*0.01);
	            		}
	            		
	            		try {
		            		if (currentPageName == "uploadFileList") {
		            			doRefreshUploadFileList();
		            			return
							}
						} catch (e) {
						}
	            	}
	            });
            },
            error:function (request){
            	$(".pre").html("失败");
            	$("#top").hide();
            	$('.midd').width(0);
        		$(".pre").html("0%");
            	localStorage.setItem("uploadingFiles","[]");
        		$.alert("获取上传地址失败");
            }
		});
	//}
}

function setTopFilePrg(triggerCondition){
	var topCurrentWidth = localStorage.getItem("topCurrentWidth");
	if(topCurrentWidth == null || topCurrentWidth==0){
		topCurrentWidth = topUnit;
	}else{
		topCurrentWidth = Number(topCurrentWidth) + Number(topUnit);
	}
	$('.midd').width(topCurrentWidth);
	localStorage.setItem("topCurrentWidth",topCurrentWidth);
	if((parseInt(topCurrentWidth)) >= topTotalWidth){
		clearInterval(topFilePrgTimer);
		//判断是跳转页面继续进度条，还是当前页面进度条
		if(triggerCondition == "continuation"){
			$(".pre").html("完成");
		}
		localStorage.setItem("topCurrentWidth",0);
		isStart = true;
	}
	$(".pre").html(parseInt(topCurrentWidth/topTotalWidth*100) + "%");
}
//打开上传文件界面
function openSelectDirModel(){
	var oFiles = document.querySelector("#fileUpload").files;
	if (oFiles.length == 0) {
		return;
	}
	file = oFiles[0];
	//取消选择文件
	if(file.name=="/"){
		return;
	}
	$("#uploadFileImg").addClass(getImgHtml(1,file.name));
	$("#uploadFileName").html(file.name);
	click_menu = "uploadFile";
	$(".file-upload").show();
	//初始化ownerId和teamspaceId
	ownerId = tempOwnerId;
	teamspaceOwnerId = 0;
	var html = createSimpleBreadcrumb(parentId,ownerId);
	$("#selectedDir").html(html);
	//初始化快捷目录 暂时不支持协作空间快捷目录
	if(typeof(currentPage) == "undefined"){
 		shortDirListInit();
 	}else if(currentPage != "shareFolderIndex" && currentPage != "spaceDetail"){
 		shortDirListInit();
 	}else{
 		$(".file-upload-content-top").find("h1").remove();
 	}
}
//关闭上传选文件界面
function closeSelectDirModel(){
	resetFileInput("fileUpload");
	$(".file-upload").hide();
}
//确认选择的目录
function startUploadFile(){
	if(uploadingParentId == null){
		uploadingParentId = parentId;
	}
	if(click_menu == "uploadPhoto"){
		uploadPhoto();
	}else if(click_menu == "createFolder"){
		createFolder();
	}else{
		uploadFile();
		resetFileInput("fileUpload");
	}
	$(".file-upload").hide();
}
//选择上传目录
function selectUploadDir(){
	$(".file-upload").hide();
	uploadingParentId = null;
	var folderChooser = new FolderChooser(ownerId);
	$("#chooserBreadCrumb").children().first().html($("#directory").children().first().html());
    folderChooser.show(function (folderId) {
    	uploadingParentId = folderId;
    	var html = createSimpleBreadcrumb(folderId,ownerId);
    	$("#selectedDir").html(html);
    });
    if(uploadingParentId==null){
    	uploadingParentId = parentId;
    }
    $(".file-upload").show();
}
//返回简单目录
function createSimpleBreadcrumb(catalogParentId, shortOwnerId) {
	 breadcrumbItem = "个人文件";
     var url = ctx+"/folders/getPaths/" + ownerId + "/" + catalogParentId;
     $.ajax({
         type: "GET",
         url: url,
         cache: false,
         async: false,
         success: function (data) {
             if (data.length > 0) {
                 for (var i = 0; i < data.length; i++) {
	                 breadcrumbItem += "&nbsp;>&nbsp;" + data[i].name;
                 }
             }
            },error: function(){
            	$.toast('获取目录失败', 'forbidden');
            }
        });
	 return breadcrumbItem;
}

/*从搜索结果中跳转时， 查询所有的父目录，构造面包屑导航 */
function createSimpleBreadcrumbForTeamspace2(parentId,ownerId,teamspaceName) {
    var breadcrumbItem = teamspaceName;
    var url = ctx+"/teamspace/file/getPaths/" + ownerId + "/" + parentId;
    $.ajax({
        type: "GET",
        url: url,
        cache: false,
        async: false,
        success: function (data) {
            if (data.length > 0) {
                for (var i = 0; i < data.length; i++) {
                	breadcrumbItem += "&nbsp;>&nbsp;" + data[i].name;
                }
            }
        },error: function(){
            $.toast('获取目录失败', 'forbidden');
        }
    });
    return breadcrumbItem;
}

//初始化快捷目录列表
function shortDirListInit(){
	$.ajax({
        type: "GET",
        async: false,
        data: {token: token},
        url: ctx + "/folders/"+ownerId+"/shortcut",
        error: function (request) {
        	$.toast("请求失败");
        },
        success: function (data) {
            if(data==null || data.length == 0){
            	return;
            }
            var html = "";
            for (var i = 0; i < data.length; i++) {
            	if(ownerId == data[i].ownerId){
	            	html += "<li onclick=\"selectShortDir("+data[i].nodeId+","+ data[i].ownerId +")\"><div class=\"folder-icon\"></div><span>"+createSimpleBreadcrumb(data[i].nodeId,data[i].ownerId)+"</span></li>";
            	}else{
            		html += "<li onclick=\"selectShortDir("+data[i].nodeId+","+ data[i].ownerId +",'"+ data[i].ownerName +"')\"><div class=\"folder-icon\"></div><span>"+createSimpleBreadcrumbForTeamspace2(data[i].nodeId,data[i].ownerId,data[i].ownerName)+"</span></li>";
            	}
        	}
            $("#shortDirDiv").html(html);
        }
    });
}

//拍照或者选择照片
function openCamera(){
	 wx.chooseImage({
		count: 1, // 默认9，这里每次只处理一张照片
		sizeType: ['original', 'compressed'], 	// 可以指定是原图还是压缩图，默认二者都有
	    sourceType: ['camera'], 		// 可以指定来源是相机，默认二者都有
        success: function (res) {
       	var localIds = res.localIds; 		// 返回选定照片的本地ID列表，localId可以作为img标签的src属性显示图片
       	wx.uploadImage({
          	    localId: localIds[0], // 需要上传的图片的本地ID，由chooseImage接口获得
          	    isShowProgressTips: 0, // 默认为1，显示进度提示
          	    success: function (res) {
         	    	serverId = res.serverId; // 返回图片的服务器端ID
         	    	selectPhotoDir();
          	    },error: function (res) {
          	    	$.toast("上传微信服务器失败");
          	    }
          	});
        }
    });
}
//选择照片上传路径
function selectPhotoDir(){
	var photoName = new Date().getTime()+ ".jpg";
    $("#uploadFileImg").addClass("file-jpg");
   	$("#uploadFileName").html(photoName);
   	click_menu = "uploadPhoto";
   	$(".file-upload").show();
   	var html = createSimpleBreadcrumb(parentId,ownerId);
   	$("#selectedDir").html(html);
   	//不支持空间设置快捷目录
   	if(typeof(currentPage) == "undefined"){
   		shortDirListInit();
   	}else if(currentPage != "shareFolderIndex" && currentPage != "spaceDetail"){
   		shortDirListInit();
   	}
}
//上传图片到存储
function uploadPhoto(){
	photoName = $("#uploadFileName").html();
	$("#top").show();
	isFileUpload = true;
	isStart = true;
	startProgress();
	
	var tempFileInfo;
	if(uploadingFiles==null || uploadingFiles.length==0){
		tempFileInfo = [{"name":photoName,"state":"on","size":2600,"complete":0,"createdAt":getFormatDate(new Date(),"yyyy/MM/dd")}];
		var v1 = JSON.stringify(tempFileInfo);
		uploadingFiles = JSON.parse(v1);
	}else{
		tempFileInfo = {"name":photoName,"state":"on","size":2300,"complete":0,"createdAt":getFormatDate(new Date(),"yyyy/MM/dd")};
		uploadingFiles.push(tempFileInfo);
	}
     
      $.ajax({
		url: ctx + "/api/v2/jsSDK/uploadPhoto",
		type: "POST",
		async: false,
		data:{
			"ownerId":ownerId,
			"fileName": photoName,
			"parentId":uploadingParentId,
			"serverId":serverId,
			"corpId":corpId,
			"token":token
		},
		success: function (fileSize) {
			$('.midd').width("100%");
	   		$(".pre").html("完成");
	   		
	   		if (uploadedFiles==null || uploadedFiles.length==0) {
	   			var tempFile = uploadingFiles[0];
	   			tempFile.complete = 100;
	   			tempFile.state = "success";
	   			tempFile.size = fileSize;
	   			var tempFileStr = "["+ JSON.stringify(tempFile) + "]";
	   			uploadedFiles = JSON.parse(tempFileStr);
			}else{
				var tempFile = uploadingFiles[0];
				tempFile.complete = 100;
	   			tempFile.state = "success";
	   			tempFile.size = fileSize;
	   			uploadedFiles.push(tempFile);
			}
	   		uploadingFiles.splice(0,1);		//删除第一个上传文件信息
	   		
	   		localStorage.setItem("uploadedFiles",JSON.stringify(uploadedFiles));
	   		localStorage.setItem("uploadingFiles",JSON.stringify(uploadingFiles));
	   		
	   		$("#top").hide();
	   		if(uploadingParentId == parentId){
				//gotoPage(ctx + "/folder?rootNode=" + parentId);
				listFile();
	   		}else{
	   			$.confirm("文件上传成功，是否进入文件目录", function() {
	   				gotoPage(ctx + "/folder?rootNode=" + uploadingParentId);
	   			  }, function() {
	   			  });
	   		}
		},
		error:function(){
			if (uploadedFiles==null || uploadedFiles.length==0) {
   			var tempFile = uploadingFiles[0];
   			tempFile.complete = 0;
   			tempFile.state = "fail";
   			var tempFileStr = "["+ tempFile + "]";
   			uploadedFiles = JSON.parse(JSON.stringify(tempFileStr));
		}else{
			var tempFile = uploadingFiles[0];
			tempFile.complete = 0;
   			tempFile.state = "fail";
   			uploadedFiles.push(tempFile);
		}
   		
   		uploadingFiles.splice(0,1);
   		localStorage.setItem("uploadedFiles",JSON.stringify(uploadedFiles));
   		localStorage.setItem("uploadingFiles",JSON.stringify(uploadingFiles));

   		
   		$(".pre").html("失败");
   		clearInterval(topFilePrgTimer);
   		$.alert("上传存储失败");
   		$("#top").hide();
		}
	});
}

//选择快捷目录
function selectShortDir(nodeId,selectOwnerId,teamspaceName){
	ownerId = selectOwnerId;
	if(typeof(teamspaceName) == "undefined"){
		teamspaceOwnerId = 0;
		var html = createSimpleBreadcrumb(nodeId,ownerId);
	}else{
		teamspaceOwnerId = selectOwnerId;
		var html = createSimpleBreadcrumbForTeamspace2(nodeId,selectOwnerId,teamspaceName);
	}
	$("#selectedDir").html(html);
	uploadingParentId = nodeId;
}
//重置文件标签
function resetFileInput(fileId){
	var file = $("#" + fileId);
    file.after(file.clone().val(""));
    file.remove();
} 
//选择新建文件夹的目录
function newFolderDialogForIndexPage() {
    $("#uploadFileImg").addClass("folder-icon");
 	$("#uploadFileName").html("<input type='text' placeholder = '新文件夹'/>");
 	click_menu = "createFolder";
 	$(".file-upload").show();
 	var html = createSimpleBreadcrumb(parentId,ownerId);
 	$("#selectedDir").html(html);
 	//不支持空间设置快捷目录
 	if(typeof(currentPage) == "undefined"){
 		shortDirListInit();
 	}else if(currentPage != "shareFolderIndex" && currentPage != "spaceDetail"){
 		shortDirListInit();
 	}
}
//创建文件夹
function createFolder(){
    var parameter = {
        ownerId: ownerId,
        parentId: uploadingParentId,
        name: $("#uploadFileName").children().val(),
        token: token
    };
    $.ajax({
        type: "POST",
        url: ctx + "/folders/create",
        data: parameter,
        error: function (request) {
            var responseObj = $.parseJSON(request.responseText);
            switch (responseObj.code) {
                case "Forbidden" || "SecurityMatrixForbidden":
                    $.toast("您没有权限进行该操作", "forbidden");
                    break;
                case "ExceedUserMaxNodeNum":
                    $.toast("文件总数超过限制", "cancel");
                    break;
                default:
                    $.toast("操作失败", "cancel");
            }
        },
        success: function () {
        	$.confirm("文件夹创建完成，是否进入文件夹目录", function() {
				gotoPage(ctx + "/folder?rootNode=" + uploadingParentId);
			  }, function() {
			  });
        }
    });
}

//上传进度回调函数：
function onprogress(e) {
	if (e.lengthComputable) {
		var spend = formatFileSize((e.loaded-spendbefore)*10);
		$("#uploadSpend").html(spend + "/s");
		var time = (e.total - e.loaded)/((e.loaded-spendbefore)*10);
		time = time.toFixed(0);
		if(time < 1){
			$("#uploadTime").html("1s");
		}else{
			$("#uploadTime").html(formatDateSize(time));
		}
		var percent = (e.loaded/e.total)*100;
		$(".pre").html(percent.toFixed(2) + "%");
		$('.midd').width(percent.toFixed(2) + "%");
		spendbefore = e.loaded;
	}
}
</script>
