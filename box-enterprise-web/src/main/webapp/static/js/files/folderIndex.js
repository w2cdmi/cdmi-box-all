function enterVersionList(nodeId){
	var id = nodeId;
	if(nodeId=="" || nodeId==null){
		var node = $("#fileList").getGridSelectedData(catalogData, opts_viewGrid);
		id = node[0].id;
	}
	
	var url = ctx + "/files/listVersion/" + ownerId +"/" + id + "?parentPageType=myspace";
	top.ymPrompt.win({title: vversionList,message:url,width:650,height:400, iframe:true,btn:[[vbuttonClose,"no",true,"btn-cancel"]]});
}

function buttonInit(){
	if(viewMode == "file"){
		$("#newFolderBtn").show();
		$(".upload-btn-box").removeAttr("style");
	}else{
		$("#newFolderBtn").hide();
		$(".upload-btn-box").css({"overflow":"hidden","width":"0","height":"0"});
	}
	
	if(isInMigrationFolder){
		$('#newFolderBtn').hide();
		$('#uploadBtnBox').hide();
		$('#uploadBtnBoxForJS').hide();
	} else {
		$('#newFolderBtn').show();
		$('#uploadBtnBox').show();
		$('#uploadBtnBoxForJS').show();
	}
}

function initSearchEvent() {
	$("#searchBar").keydown(function(){
		var evt = arguments[0] || window.event;
		if(evt.keyCode == 13){
			doSearch(1);
			if(window.event){
				window.event.cancelBubble = true;
				window.event.returnValue = false;
			}else{
				evt.stopPropagation();
				evt.preventDefault();
			}
		}
	});
}

// 新增检索条件：labelIds, docType
function doSearch(curPage, keyword, docType, labelId){
	if (labelId || docType){
		viewMode = "highSearch";
	} else {
		viewMode = "search";	
	}
	
	if(curPage == undefined){
		curPage = currentPage;
	}
	
	var currSearchType = -1;
	if (viewMode == "highSearch"){
		keyword = keyword ? keyword.trim() : "";
		currSearchType = 1;
	} else {
		if(keyword == undefined){
			keyword = $("#searchBar").val().trim();
		}
		if(keyword == "" || keyword == null){
			$("#searchBar").val("");
			return;
		}
		
		currSearchType = 0;
	}

	var searchSpiner = new Spinner(optsSmallSpinner).spin($("#searchBar").parent().get(0));
	
	var pagePerDis = getCookie("fileListPerPageNum"); 
	pagePerDis = (pagePerDis == null||pagePerDis == ''||pagePerDis == undefined) ? 40 : pagePerDis;
	var url = "nodes/search";
	
	var requestDocType = obtainRequestParam('docType');
	if (requestDocType){
		docType = requestDocType;
	}

    var params= {
	    "ownerId": ownerId, 		    
	    "name": keyword, 		
	    "pageNumber": curPage,   			
	    "orderField": orderField, 
	    "pageSize" : pagePerDis,
	    "desc":isDesc,
	    "token": vtoken,
	    "labelIds" : labelId,
	    "docType":docType,
	    "searchType":currSearchType
    };
   
    var isSwitchSelect = -1 != $("#witchSelect").attr("value") && 0 != $("#witchSelect").attr("value");
    var isHighsearchAndCustomDefineDoctype = currSearchType && docType && docType > 0 && isCustomDefineDoctype(docType);

    if(isSwitchSelect || isHighsearchAndCustomDefineDoctype) {
    	url = "nodes/searchByDocType";
    	var tpdocType = '';
    	if (isSwitchSelect){
    		tpdocType = $("#witchSelect").attr("value");
    	} else if (isHighsearchAndCustomDefineDoctype){
    		tpdocType = docType;
    	}

	 	var params= {
			    "ownerId": ownerId, 
			    "docType" : tpdocType,
			    "name": keyword, 		
			    "pageNumber": curPage,   			
			    "orderField": orderField, 
			    "pageSize" : pagePerDis,
			    "desc":isDesc,
			    "token": vtoken,
			 	"labelIds" : labelId,
			 	"searchType":currSearchType
		    };
    }
    
    $.ajax({
        type: "POST",
        url: url,
        data: params,
        error: function(request) {
        	searchSpiner.stop();
        	handlePrompt("error", vinviteShareErrorEmpty);
        },
        success: function(data) {
        	searchSpiner.stop();
        	if(typeof(data)=='string' && data.indexOf('<html>')!=-1)
	        	{
	        		window.location.href= ctx + "/logout";
					return;
	        	}
        	catalogData = data.content;
        	if(catalogData.length == 0 && curPage != 1){
				curPage--;
				changeHash(viewMode, curPage, keyword);
				return;
			}
        	
        	headData.name.width = searchFileWidth[0];
        	headData.path.width = searchFileWidth[1];
        	headData.size.width = searchFileWidth[2];
        	headData.modifiedAt.width = searchFileWidth[3];
			$("#fileList").setGridData(catalogData, opts_viewGrid);
			$("#fileListPageBox").setPageData(opts_page, data.number, data.size, data.totalElements);
			changeHash(viewMode, curPage, keyword, docType, labelId);
			buttonInit();
			
			var beingShowNums = 0;
			if (data.totalElements){
				beingShowNums = data.totalElements;
			} else if (data.numberOfElements){
				beingShowNums = data.numberOfElements;
			}
			createBreadcrumb(0, ownerId, beingShowNums);
			comboxRemoveLoading("pageLoadingContainer"); 
        }
    });
    
}

function getClientDownloadURL(type){
	var downloadUrl;
	$.ajax({
        type: "GET",
        async: false,
        url:ctx + "/client/downloadUrl?type=" + type,
		success: function(data) {
			downloadUrl = data;
		}
        
    });
	return downloadUrl;
}


function turnStore(){
	var url = ctx + "/teamspace/copyToSpace" + "?startPoint=operative&endPoint=teamspace";
	top.ymPrompt.win({message:url,width:700,height:550,title: vfileTitleTurnStore, iframe:{id:"turnStoreFrame"}});
}

function copyToSpace(tp){
	var idArray = $("#fileList").getGridSelectedId();
	if (tp == 'copy') {
		top.ymPrompt.getPage().contentWindow.submitCopyForSpace(tp,ownerId,idArray);
	} else {
		top.ymPrompt.close();
	}
}

function asyncListenForSpace(type,srcOwnerId,destOwnerId,selectFolder,taskId) {
	$.ajax({
        type: "GET",
        url: ctx + "/nodes/listen?taskId="+taskId+"&"+new Date().toString(),
        error: function(XMLHttpRequest) {
			if (XMLHttpRequest.status == 409) {
				top.ymPrompt.close();
        		unLayerLoading();
        		conflictCopyForSpace(type,srcOwnerId, destOwnerId,XMLHttpRequest.responseText,selectFolder);
			}else{
				unLayerLoading();
	            top.ymPrompt.close();
	        	handlePrompt("error", voperationFailed);
			}
        },
        success: function(data, textStatus, jqXHR) {
			switch(data){
				case "NotFound":
					top.ymPrompt.close();
					unLayerLoading();
	        		handlePrompt("success", voperationSuccess);
		        	if(viewMode == "file"){
			        	listFile(currentPage, catalogParentId);
		        	}else{
		        		doSearch();
		        	}
					break;
				case "Doing":
					setTimeout(function(){
						asyncListenForSpace(type,srcOwnerId,destOwnerId,selectFolder,taskId);
					}, 1500);
					break;
				case "SameParentConflict":
					unLayerLoading();
					if($("#maskLevel").css("display") =="block"){ 
						ymPrompt.getPage().contentWindow.handlePrompt("error",vfileErrorMsgParentNotChange,'',10);
					}else{ 
						handlePrompt("error", vfileErrorMsgParentNotChange);
					}
					break;
				case "SameNodeConflict":
					unLayerLoading();
					if($("#maskLevel").css("display") =="block"){
						ymPrompt.getPage().contentWindow.handlePrompt("error",vfileErrorMsgSameFolder,'',10);
					}else{
						handlePrompt("error", vfileErrorMsgSameFolder);
					}
					break;
				case "SubFolderConflict":
					unLayerLoading();
					if($("#maskLevel").css("display") =="block"){
						ymPrompt.getPage().contentWindow.handlePrompt("error",vfileErrorMsgCanNotCopyToChild,'',10);
					}else{
						handlePrompt("error", vfileErrorMsgCanNotCopyToChild);
					}
					break;
				case "Forbidden":
					unLayerLoading();
					top.ymPrompt.getPage().contentWindow.handlePrompt("error", verrorForbid);
					break;
				case "NoSuchSource":
				case "NoSuchDest":
					unLayerLoading();
	            	top.ymPrompt.close();
					handlePrompt("error", verrorNotfound);
					if(viewMode == "file"){
						listFile(currentPage, catalogParentId);
					}else{
						doSearch();
					}
					break;
		        default:
					unLayerLoading();
            		top.ymPrompt.close();
        			handlePrompt("error", voperationFailed);
					break;
			}
        }
    });
}

function conflictCopyForSpace(type, srcOwnerId, destOwnerId, conflictIds, selectFolder) {
	ymPrompt.confirmInfo({title: vfileTitleCopyOrMove,message:vfileInfoAutoRename,handler:function(tp) {
		if (tp=='ok') {
			url = ctx + "/nodes/renameCopy/" + srcOwnerId;
			$.ajax({
		        type: "POST",
		        url:url,
		        data:{'destOwnerId':destOwnerId, 'ids':conflictIds, 'parentId':selectFolder,'token' : vtoken},
		        error: function(request) {
		        	handlePrompt("error", voperationFailed);
		        },
		        success: function(data) {
		        	inLayerLoading(vcommonTaskDoing,"loading-bar");
		        	asyncListenForSpace(type,srcOwnerId, destOwnerId, selectFolder,data);
		        }
		    });
		}
	}});
}
//构造文件夹上传下拉菜单
	function createMenu(uploadFileName,uploadFileTitle,uploadDirName,uploadDirTitle){
		if (isFirFoxBrowser() || (!$.support.leadingWhitespace || navigator.userAgent.indexOf("MSIE 10.0")>0)||navigator.userAgent.indexOf("rv:11.0")>0)
		{
        	$("#spanUpload").removeClass("fileinput-buttonDir");
        	$("#spanUpload").addClass("fileinput-button");
		}
    	var menu = $('<ul id="menuID" class="dropdown-menu1">'+'</ul>');
    	var menuItem2 = $('<li><span id="uploadMenu2"><span class="uploadBtn btn fileinput-button"style="width: 45px;padding: 2px;text-align:left"><span>'+uploadDirName+'</span><form id="dirform" style="padding-top:2px"><input id="dirUpload0" title="'+uploadDirTitle+'" type="file" name="files[]" multiple="multiple" webkitdirectory/></form></span></span></li>');
    	var ffMenuItem = $('<li><span id="ffUplodSpan"><span class="uploadBtn btn fileinput-button"style="width: 45px;padding: 2px;text-align:left"><span>'+uploadDirName+'</span><form id="dirform" style="padding-top:2px"><input id="dirUpload1" title="'+uploadDirTitle+'" type="file" name="files[]" multiple="multiple" /></form></span></span></li>');
    	var menuItem4_chro = $('<li><span class="uploadBtn btn fileinput-button"style="width: 45px;padding: 2px;text-align:left"><span><span>'+uploadFileName+'</span> <input id="fileUpload" title="'+uploadFileTitle+'" type="file" name="files[]" multiple /></span></span></li>'); 
    	
    	var menuItem3 = $('<li id="btnLi"><button id="uploadfile" class="uploadDirBtn">'+uploadDirName+'</button></li>');
    	if(navigator.userAgent.indexOf("MSIE 10.0")>0 || navigator.userAgent.indexOf("rv:11.0")>0){
 		   	var menuItem4 = $('<li><span class="uploadBtn btn fileinput-button"style="width: 80px;padding: 2px;left:-6px"><span id="fileUpload2"><input id="fileUpload" type="file" name="files[]" multiple /></span>'+uploadFileName+'</span></li>');
    	}else{
 		   	var menuItem4 = $('<li><span class="uploadBtn btn fileinput-button"style="width: 80px;padding: 2px;left:2px"><span id="fileUpload2"><input id="fileUpload" type="file" name="files[]" multiple /></span></span></li>');
    	}
    	
		menuItem3.on("click",uploadDir)
       	if ( !(!$.support.leadingWhitespace || navigator.userAgent.indexOf("MSIE 9.0")>0)){ 
    		 $("#uploadBtnBoxForJS").append(menu);
    	}else if(isFirFoxBrowser()){
    		 $("#uploadBtnBoxForJS").append(menu);
    	}else{ 
    		 $("#uploadBtnBox").append(menu);
    	} 
	    
		if (isChromeBrowser()){
	        $("#menuID").append(menuItem4_chro);
	        $("#menuID").append(menuItem2);
	        $("#dirUpload0").on("click",function(e){
		    	  unSupportAlert(e);
		      });
	    }else if(isFirFoxBrowser()){
	    	  $("#menuID").append(menuItem4_chro);
		      $("#menuID").append(ffMenuItem);
		      //Message for not support currently
		      $("#dirUpload1").on("click",function(e){
		    	  unSupportAlert(e);
		      });
	    }else{ 
	       $("#menuID").append(menuItem4);
	       $("#menuID").append(menuItem3);
	      
	    } 
	        
	    $("#menuID").css("display","none");
	    $("#hoverDiv").hover(
			function () {
				$('div.public-bar').css('z-index',9999);
			},
			function () {
				$("#menuID").css("display","none");
				$('div.public-bar').css('z-index',0);
			}
		);
	}
	
	function unSupportAlert(e){
		if(isFirFoxBrowser()){
			alert("Not Support folder upload currently!");
	  	  		if (e && e.preventDefault ){
	            e.preventDefault(); 
	  	  	}
		}
		//chrome
		if(isChromeBrowser){
			var tester = document.createElement('input');
			tester.type = 'file';
			if(!('multiple' in tester && 'webkitdirectory' in tester)) {
				alert("Not Support folder upload for this edition,please upgrade!");
	  	  		if (e && e.preventDefault ){
	            	e.preventDefault(); 
	  	  		}
			}
		}
	}
	

function uploadDir(){
	/**automtically set variables for uploadDir */
	var isHttps = 0;
	var reqUrl = window.location.href;
	var reqServer= vreqServer;
	var reqPort = vreqPort;
	var domainName= vdomainName;
	var reqProtocol= reqProtocol;
	
	if(reqProtocol !="http"){
    		isHttps = 1;
    	}
	if(domainName && !domainName.split(":")[1]){
		reqServer = domainName;
	}
	
	try{
		var UploadObj = new ActiveXObject("UPLOADPLUGIN.UploadPluginCtrl.1");
 		addActiveObj();
 	}catch(e){
		downloadCAB();
	return;
 }  
 	
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
     	//预上传
     	Upload.SetSrvUFM(reqServer,url,reqPort,isHttps);// 调用预上传接口
     	//文件上传
		Upload.StartUpload();
 	}
 	catch(e){
 		handlePrompt("error", verrorUploadfolderFileFail);
			return;
 	}
 	
 	$.each(files,function(index,file){
 		createProcess(file.key,file.file,file.size);
 		uploadComplete.push(file.key);
 		uploadDelete.push(file.key);
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
 			if(uploadFinishMap.get(onlyKey) == "unfinish")
 			{
 				var numStr = $("#showUploadedNum").text();
 				var showUploadedNum = parseInt(numStr) + 1;
 				
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
            	})
            	},1000);
 			
 		} 
 		else
 		{
				var cruntRate = uploadRateMap.get(index);
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
 
 function showSize(size)
 {
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
 
 function childrenCallBack()
 {
    var divhtml = '<OBJECT classid=clsid:AE60D877-3019-4F01-8CBE-017A4B7FF788 codebase="' + ctx + '/static/js/public/Upload.CAB#version=2,1,2,6" id=Upload style="HEIGHT: 1px; WIDTH: 1px"></OBJECT>';
    var testHtml = document.getElementById("test").innerText;
	 
    if("" == testHtml )
    {
     $("#test").html(divhtml);
    }
    
    //uploadDir();
 }
 
 function addActiveObj()
 {
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

/* ================================Folder upload end====*/
function gotoShareMyFolderError(status){
	if(status == 404){
		handlePrompt("error", verrorNotfound);
		ymPrompt.close();
		if(viewMode == "file"){
			listFile(currentPage, catalogParentId);
		}else{
			doSearch();
		}
	}else if(status == 403){
		handlePrompt("error", verrorForbid);
		ymPrompt.close();
	}
}

/** 校验是否在移交文件夹中 **/
function checkIsInMigrationFolder(pcreatedBy, pownerBy, pfileType, pfolderName){
	if (pfileType == 5){
		isMigrationFolder = true;
		isInMigrationFolder = true;
		departureOwnerId = pcreatedBy;

		if (pfolderName){
			setCookie("migrationFolderName", pfolderName);
		}
	} else {
		isMigrationFolder = false;
		
		if (pownerBy == ownerId){
			isInMigrationFolder = false;
			departureOwnerId = null;
		}

		if (!isInMigrationFolder){
			ownerId = pownerBy;
			departureOwnerId = null;	
		} else {
			departureOwnerId = pownerBy;
		}
	}
}
/** 重置离职用户变量信息 **/
function resetMigrationInfo(){
	isMigrationFolder = false;
	isInMigrationFolder = false;
	departureOwnerId = null;
	delCookie("migrationParantId");
	delCookie("migrationFolderName");
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