var currentPageName = "uploadFileList";
var uploadingFiles = null;
var uploadedFiles = null;
var downloadingFiles = null;
var downloadedFiles = null;

var totalWidth = $("#test").width();

$(function(){
	
	var uploadingFilesStr = localStorage.getItem("uploadingFiles");
	if (uploadingFilesStr!=null) {
		uploadingFiles = JSON.parse(uploadingFilesStr);
	}
	
	var uploadedFilesStr = localStorage.getItem("uploadedFiles");
	if (uploadedFilesStr!=null) {
		uploadedFiles = JSON.parse(uploadedFilesStr);
	}
	
	var downloadingFilesStr = localStorage.getItem("downloadingFiles");
	if (downloadingFilesStr!=null) {
		downloadingFiles = JSON.parse(downloadingFilesStr);
	}
	
	var downloadedFilesStr = localStorage.getItem("downloadedFiles");
	if (downloadedFilesStr!=null) {
		downloadedFiles = JSON.parse(downloadedFilesStr);
	}
	
	var isFileUpload = localStorage.getItem("isFileUpload");
	
	$("#uploadList-btn").click(function(){
		$("#downList-btn").removeClass("active");
		$("#uploadList-btn").addClass("active");
		$("#downFileListDiv").hide();
		$("#uploadFileListDiv").show();
	});
	
	$("#downList-btn").click(function(){
		$("#uploadList-btn").removeClass("active");
		$("#downList-btn").addClass("active");
		$("#uploadFileListDiv").hide();
		$("#downFileListDiv").show();
	});
	
	/*$('#start').click(function(){
		debugger;
		if(isStart){
			bartimer = window.setInterval(function(){setProgress()},100);
			isStart = false;
		}else{
			clearInterval(bartimer);
			isStart = true;
		}
	});*/
	
	/*if(isFileUpload!=null && isFileUpload){
		$("#top").show();
		var file = uploadingFiles[0];
		$('#xjdt').width(file.complete * 0.01 * totalWidth);
		$('#start').click();
	}*/
	
	doRefreshUploadFileAllList();
	
	doRefreshDownloadFileAllList();
//	$.ajax({
//		type:"get",
//		url:ctx+"/teamspace/file/getPaths/" + ownerId + "/" + parentId,
//		error:function(){},
//		success:function(data){
//			console.log(data);
//		}
//	});
});



function setProgress(){
	var currentWidth = parseInt((topCurrentWidth + topUnit)/topTotalWidth)*totalWidth;
	if(currentWidth >= totalWidth){
		clearInterval(bartimer);
		return;
	}
	$('#xjdt').width(currentWidth);
}

function doRefreshUploadFileAllList(){
	doRefreshUploadingFileList();
	doRefreshUploadedFileList();
}

function doRefreshDownloadFileAllList(){
	doRefreshDownloadingFileList();
	doRefreshDownloadedFileList();
}


function doRefreshUploadingFileList(){
	if (uploadingFiles!=null && uploadingFiles.length>0) {
		var $list = $("#uploadingFile");
		var $template = $("#uploadingFileTemplate");
		$list.children().remove();
		
		for (var i = uploadingFiles.length-1; i>= 0; i--) {
			var item = uploadingFiles[i];
			item.size = formatFileSize(item.size);
			item.imgClass = getImgHtml(1, item.name);
			$template.template(item).appendTo($list);
		}
	}
}

function doRefreshUploadedFileList(){
	if (uploadedFiles!=null && uploadedFiles.length>0) {
		var $list = $("#uploadedFile");
		var $template = $("#uploadedFileTemplate");
		$list.children().remove();
		
		for (var i = uploadedFiles.length-1; i>= 0; i--) {
			var item = uploadedFiles[i];
			item.size = formatFileSize(item.size);
			item.imgClass = getImgHtml(1, item.name);
			$template.template(item).appendTo($list);
		}
	}
}

function doRefreshDownloadingFileList(){
	if (downloadingFiles!=null && downloadingFiles.length>0) {
		var $list = $("#downloadingFile");
		var $template = $("#downloadingFileTemplate");
		$list.children().remove();
		
		for (var i = downloadingFiles.length-1; i>= 0; i--) {
			var item = downloadingFiles[i];
			item.imgClass = getImgHtml(1, item.name);
			$template.template(item).appendTo($list);
		}
	}
}

function doRefreshDownloadedFileList(){
	if (downloadedFiles!=null && downloadedFiles.length>0) {
		var $list = $("#downloadedFile");
		var $template = $("#downloadedFileTemplate");
		$list.children().remove();
		
		for (var i = downloadedFiles.length-1; i>= 0; i--) {
			var item = downloadedFiles[i];
			item.imgClass = getImgHtml(1, item.name);
			$template.template(item).appendTo($list);
		}
	}
}