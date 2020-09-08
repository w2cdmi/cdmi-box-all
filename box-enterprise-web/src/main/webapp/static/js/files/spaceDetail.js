function uploadDir(){
	/**automtically set variables for uploadDir */
	var reqUrl = window.location.href;
	var reqServer= vreqServer;
	var reqPort = vreqPort;
	var reqProtcol = vreqProtcol;
	var isHttps =0;
	if(reqUrl.substring(0, 8) == "https://"){
		isHttps = 1;
	}
	var domainName= vdomainName;
	
	if(domainName && !domainName.split(":")[1]){
		reqServer = domainName;
	}
	/**automtically set variables for uploadDir */
	
 	   try
 	{
 		var UploadObj = new ActiveXObject("UPLOADPLUGIN.UploadPluginCtrl.1");
 		addActiveObj();
 	}
 	catch(e)
 	{
 		downloadCAB();
		return;
 	} 
 	//addActiveObj();
 	var url =　ctx + "/uploadFolder/folderPreupload/"+ownerId + "/" + catalogParentId;
 	var fileName;
 	
 	if (isFolderIsUploading)
 	{
 		handlePrompt("error", verrorUploadfolderLimit);
 		return;
 	}
 	
 	try{
     	//打开打开浏览对话框，选择本地路径.
     	var isFolderSelected = Upload.BrowsePath();
    	//点击弹出框的取消，退出
     	if(isFolderSelected===0){
     		return;
     	}
 	
 		//关闭文件选择框后下拉菜单消失
 	 	$("#menuID").css("display","none");
 	 
 	 	//文件大小检查
 	 	Upload.SetMaxFileSizeLimit(2097152,2);
 		//选择完成后关闭选择框显示
 	
	    //点击弹出框的取消，退出
     	if(isFolderSelected===0){
     		return;
     	}
     	
     	var filesStr = Upload.GetUploadFiles();
     	//文件超过大小
		if(filesStr == null || filesStr == undefined || filesStr.length === 0){
			return;
		}else{
			files = eval("("+filesStr+")");
			
			//上传空文件夹提示
	  		if (files == null || files == undefined || files.length == 0)
	  		{
	  			handlePrompt("error", verrorUploadfolderNull);
	  			return;
	  		}
	  		
	  		if (files.length > 100)
	  		{
	  			handlePrompt("error", verrorUploadfolderFileLimit);
	  			return;
	  		}
		}
 	
 	isFolderIsUploading = true;
 	//预上传,地址要与浏览器访问的地址方式一致
		Upload.SetSrvUFM(reqServer,url,reqPort,isHttps);// 调用预上传接口
 	
 	//文件上传
	Upload.StartUpload();
	
 	}
 	catch(e)
 	{
 		handlePrompt("error", verrorUploadfolderFileFail);
			return;
 	}
 	
 	$.each(files,function(index,file){
 		createProcess(file.key,file.file,file.size);
 		uploadComplete.push(file.key);
 		uploadDelete.push(file.key);
 		//uploadErrorFiles.push(file.file);
 		uploadRateMap.put(index,0);

 	});
 	intervalReset = false;
	//检测上传进度
	refreshProcess();
	
 }
 
 function createProcess(key,file,size)
 {
 	var fileName = file.substring(file.lastIndexOf("\\") + 1,file.length);
 	$("#uploadModal").show();
 	var num = $("#uploadQueue > div").length;
 	$("#showUploadTotalNum").text(files.length);
 	var context = $("<div/>").addClass("fileUpload-queue-item").appendTo("#uploadQueue");
 	
 	$(context).attr("id",key);
 	
 	var node =  $("<div/>").append($("<span/>").addClass("title").text(fileName)).append($("<span/>").addClass("size").text(" (" + showSize(size) + ") -"))
 				.append($("<span/>").addClass("info").text("0%"));
 				
 	var nodeprocess = $("<div/>").addClass("progress progress-info")
            		.append($("<div/>").addClass("bar"));
            		
    node.appendTo(context);
   	nodeprocess.appendTo(context);
 }
 

 function refreshProcess()
 {   
 	
 	if (intervalReset)
 	{
 		return;
 	}
 	
 	 if (files == null || files == undefined)
 	{
 		return;
 	}
 	$.each(files,function(index,file){
 		
 			var uploadStatus = Upload.GetUploadStatus(file.key);
 			uploadStatusMap.put(index,uploadStatus);
 			if(index == 0)
 			{
 			  var rate = getRate(file,index);
 			
 			  uploadRateMap.put(index,rate);
 			
 			}
 			else
 			{
 				preIndex = index -1;
 				var perStatus = uploadStatusMap.get(preIndex);
 				if(perStatus == 2)
 				{
 			  		var rate = getRate(file,index);
 			
 			  		uploadRateMap.put(index,rate);
 				}
 			
 			}
 			
 		uploadStatusCurrent = uploadStatus;
 		
 		var onlyKey = file.key; 
 		if(uploadFinishMap.get(onlyKey) == null)
 		{
 			uploadFinishMap.put(onlyKey,"unfinish");
 		}
 		
 		var element = document.getElementById(onlyKey);
 		
 		//检测文件上传是否出现异常
 		if (uploadStatus == 2)
 		{
 			var rateCur = $(element).find(".info").text();
 			rate = 100;
 			$(element).find(".info").text(rate +"%");
 			$(element).find(".bar").css("width",rate + "%");
 			
 			//console.log("onlykey----"+uploadFinishMap.get(onlyKey));
 			
 			if(uploadFinishMap.get(onlyKey) == "unfinish")
 			{
 				var numStr = $("#showUploadedNum").text();
 				
 				var showUploadedNum = parseInt(numStr) + 1;
 				
 				//console.log("index-----"+index+"---showUploadedNum:"+showUploadedNum);
 				$("#showUploadedNum").text(showUploadedNum);
 				uploadFinishMap.put(onlyKey,"finish");
 			
 			}
 			
 			if (rateCur != "100%")
 			{
 				uploadComplete.pop(file.key);
 				$(element).find(".info").text(vuploadifyMsgComplete);
 				
 				setTimeout(function(){
            	$(element).fadeOut(300,function(){
            		$(element).remove();
            		//checkUploadComplete();
            	})
            	},3000);
 			}
 			
 		}

 		else if (uploadStatus == 3 || file.size == 0)
 		{
 			var fileName = file.file;
 			$(element).find(".info").addClass("error").text(vfileTitleUploadFailed);
 			$(element).find(".bar").css("width", "0%");
 			uploadFolderFail.put(file.key,fileName.substring(fileName.lastIndexOf("\\") + 1,fileName.length));
 			uploadComplete.pop(file.key);
 			
 			setTimeout(function(){
            	$(element).fadeOut(200,function(){
            		$(element).remove();
            		//checkUploadComplete();
            	})
            },1000);
 		} 
 		else
 		{
				//console.log("reflash rate index-------"+ index);
				var cruntRate = uploadRateMap.get(index);
				//console.log("reflash rate index-------"+ index+"-----rate---"+cruntRate);
				$(element).find(".info").text(Math.round(cruntRate) +"%");
				$(element).find(".bar").css("width",Math.round(cruntRate) + "%");
 		}
 		
 	});
 	//console.log("----------------" + isUpload);
 	var num = $("#uploadQueue > div").length;
 	if ((uploadComplete.length == 0) && num == 0)
 	{
 		setTimeout(function(){
        	$("#uploadModal").fadeOut(300,function(){
        	    $("#showUploadedNum").text("0");
        		$("#uploadModal").hide();
        		checkUploadComplete();
        	})
        },2000);
 	}

 }
 
 
 function getRate(file , index)
 {
     var step = everyAdd(file.size);
     
     var preRate = uploadRateMap.get(index);
     
     var curentRate = step +preRate;
     if (curentRate > 99)
     {
     	curentRate = 99;
     }
     return curentRate;
 }
 
 
 function everyAdd(size)
 {
 	if (size > 1024 * 1024 * 1024)
 	{
 		return 0.2;
 	}
 	if (size > 500 * 1024 * 1024)
 	{
 		return 3;
 	}
 	if (size > 100 * 1024 * 1024)
 	{
 		return 5;
 	}
 	
 	return 10;
 }
 
 
 function showSize(size){
 	var sizeStr;
 	if (size < 1024)
 	{
 		sizeStr = (size).toFixed(2) + "B";
 	}
 	else if(size >= 1024 && size < 1024 * 1024)
 	{
 		sizeStr = (size/1024).toFixed(2) + "KB";
 	}
 	else if (size >= 1024 * 1024 &&　size < 1024 * 1024 * 1024)
 	{
 		sizeStr = (size/1024/1024).toFixed(2) + "MB";
 	}
 	else
 	{
 		sizeStr = (size/1024/1024/1024).toFixed(2) + "GB";
 	}
 	return sizeStr;
 }
 
   function downloadCAB(){
  	try
  	{
	 	Upload.SetMaxFileSizeLimit(2097152,2);
  	}
 	catch(e)
	 {
	 	var url = ctx + "/static/help/guide/activeXguide.html";
 		ymPrompt.win({message:url,width:600,height:180,title:vcommonTip, iframe:true});
 	}
 		return;
 }
   
 function childrenCallBack(){
	 var divhtml = '<OBJECT classid=clsid:AE60D877-3019-4F01-8CBE-017A4B7FF788 codebase="' + ctx + '/static/js/public/Upload.CAB#version=2,1,2,6" id=Upload style="HEIGHT: 1px; WIDTH: 1px"></OBJECT>';
     var testHtml = document.getElementById("test").innerText;
 	 
     if("" == testHtml )
     {
      $("#test").html(divhtml);
     }
    //uploadDir();
 }

 function addActiveObj(){
	var divhtml = '<OBJECT classid=clsid:AE60D877-3019-4F01-8CBE-017A4B7FF788 codebase="' + ctx + '/static/js/public/Upload.CAB#version=2,1,2,6" id=Upload style="HEIGHT: 1px; WIDTH: 1px"></OBJECT>';
    var testHtml = document.getElementById("test").innerText;
    if("" == testHtml ){
     $("#test").html(divhtml);
    }
 }
 
 //控制上传文件/文件夹下拉菜单的显隐
 function handleDropMenu(){
	 var disPVal = $("#menuID")[0].style.display;
	 if(disPVal==="none"){
		$("#menuID").css("display","block");
	 }else{
		$("#menuID").css("display","none");
	 }
 }
 
function isViewType(rowData) {
	var viewType = rowData.name.substring(rowData.name.lastIndexOf(".") + 1).toLowerCase();
	var tmpFlag=["doc","docx","xls","xlsx","ppt","pptx","pdf"];
	// rowData.type==1是文件
	if($.inArray(viewType, tmpFlag) >=0 && 1 == rowData.type && rowData.previewable){	
		return true;
	} 
	return false;
}