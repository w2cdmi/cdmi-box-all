<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html>
<head>
<%@ include file="../common/common.jsp"%>
<link href="${ctx}/static/skins/default/css/mainforCMB.css" rel="stylesheet" type="text/css">
<link href="${ctx}/static/autocomplete/themes/base/jquery.ui.all.css" rel="stylesheet" type="text/css">
<script src="${ctx}/static/js/public/validate/jquery.validate.min.js" type="text/javascript"></script>
<script src="${ctx}/static/js/public/validate/messages_bs_zh.js" type="text/javascript"></script>
<script src="${ctx}/static/js/public/My97DatePicker/link_WdatePicker.js" type="text/javascript"></script>
<script src="${ctx}/static/autocomplete/ui/jquery.ui.core.js" type="text/javascript"></script>
<script src="${ctx}/static/autocomplete/ui/jquery.ui.widget.js" type="text/javascript"></script>
<script src="${ctx}/static/autocomplete/ui/jquery.ui.position.js" type="text/javascript"></script>
<script src="${ctx}/static/autocomplete/ui/jquery.ui.menu.js" type="text/javascript"></script>
<script src="${ctx}/static/autocomplete/ui/jquery.ui.autocomplete.js" type="text/javascript"></script>
<script src="${ctx}/static/clipboard/ZeroClipboard.js" type="text/javascript"></script>
<script src="${ctx}/static/js/public/JQbox-hw-grid.js" type="text/javascript"></script>

</head>
<body>
	<div class="pop-content">
				<form id="setLinkForm" class="pop-share-link">
					<span id="enterTempData" style="display:none; height:30px;"></span>
					<div class="file-name clearfix" id="linkFileName"><span class="file-icon" id="fileOrFolderType"></span> <span class="name-txt" title="${name}">${name}</span></div>
					<div class="file-link-con">
						<div id="linksList">
						</div>
						<div class="add-link">
							<button onclick="setLink()" type="button" class="btn btn-primary"><spring:message code='link.button.create'/></button>
							<span class="help-inline"><spring:message code='link.title.tips'/></span>
						</div>
						<div id="manageBtnCon" class="btn-con">
			            	<button id="cancelLink-button" type="button" onclick="deleteAllLink()" class="btn">
			            		<c:if test="${linkStatus == true}"><spring:message code="link.set.cancelLink"/></c:if>
								<c:if test="${linkStatus == false}"><spring:message code="button.discardShare"/></c:if>
							</button>
			            	<button id="shareLinkDone" type="button" class="btn btn-primary" onclick="top.linkHandle()"><spring:message code='button.complete'/></button>
			            </div>
					</div>
					<div class="link-access">
						<div class="file-link">
						 	<div class="input-append">
								<input type="text" id="urlAccess" name="urlAccess" readonly="readonly"/>
							</div>
						 </div>
		                <div class="form-horizontal label-w120">
		                	<div class="control-group">
					        	<label class="control-label"><spring:message code='link.set.linkACL'/></label>
					            <div class="controls">
					                <label class="checkbox inline"><input type="checkbox" id="upload" name="upload" /> <spring:message code='link.label.upload'/></label>
					                <label class="checkbox inline"><input type="checkbox" id="download" name="download" /><spring:message code='link.label.download'/></label>
					                <label class="checkbox inline"><input type="checkbox" id="preview" name="preview" /> <spring:message code='link.label.scan'/></label>
					            </div>
					        </div>
					        <div class="control-group">
					        	<label class="control-label"><spring:message code="link.accessCode"/></label>
					            <div class="controls">
					                <input type="hidden" id="accessCodeMode" name="accessCodeMode" value="static"/>
					                <label class="checkbox inline"><input type="checkbox" id="sCode" /> <spring:message code='link.label.start'/></label>
					                <label class="checkbox inline" style="display:none"><input type="checkbox" id="dCode" /> <spring:message code='link.label.use.code'/></label>
					                <div class="form" id="sCodeDiv" style="display: none">
										<span class="help-block"><spring:message code='link.label.input.code'/></span>
										<div class="input-append">
									    	<input class="span3" type="text" id="accessCode" name="accessCode" readonly="readonly" maxlength="20"/>
									    	<button id="freshAcessCode" type="button" onclick="refreshLinkAccessCode()" 
												class="btn" title="<spring:message code='link.set.refrechAccessCode'/>"><i class="icon-refresh"></i></button>
										</div>
									</div>
					                <div class="form" id="dCodeDiv" style="display: none">
										<span class="help-block"><spring:message code='link.label.input.cmb.email'/></span>
										<div class="input-append">
									    	<input class="span3" type="text" id="identities" name="identities"  maxlength="255"/>
										</div>
									</div>
					            </div>
					        </div>
					        <div class="control-group">
					        	<label class="control-label"><spring:message code="link.set.timeDure"/></label>
					            <div class="controls">
					                <label class="checkbox inline"><input id="dateRadioCustom" type="checkbox" name="dateRadio"/> <spring:message code='link.label.start'/></label>
					            	<input type="hidden" id="timeZone" name="timeZone"/>
					            	<input type="hidden" id="effectiveAt" name="effectiveAt" />
									<input type="hidden" id="expireAt" name="expireAt"  />
									<div class="form" id="dateDiv" style="display: none">
										<input class="Wdate span2" type="text" id="effectiveAtTime" onClick="WdatePicker({lang:'<spring:message code="common.language1"/>',dateFmt:'<spring:message code="common.dateformat"/> HH:mm',minDate:'%y-%M-{%d}',vel:'effectiveAt'})"> <spring:message code="link.set.timeTo"/>
										 <input class="Wdate span2" type="text" id="expireAtTime" onClick="WdatePicker({lang:'<spring:message code="common.language1"/>',dateFmt:'<spring:message code="common.dateformat"/> HH:mm',minDate:'%y-%M-{%d}',vel:'expireAt'})">
									</div>
					            </div>
					        </div>
		                
		               	</div>
		               	<div class="btn-con">
			            	<button id="saveAccess" type="button" class="btn btn-primary"><spring:message code="link.button.saveLink"/></button>
			            	<button id="cancelAccess" type="button" class="btn"><spring:message code="button.cancel"/></button>
			            </div>
		            </div>
					<div id="linkEmail" class="link-email">
						 <div class="file-link">
						 	<div class="input-append">
								<input type="text" id="urlEmail" name="urlEmail" readonly="readonly"/>
							</div>
							<div id="linkEmailInfo"></div>
						 </div>
						 <div class="add-email-help"><spring:message code="input.queryFormGroup"/></div>
						 <div class="user-form">
							 <div class="department-con clearfix">
								<ul id="treeArea" class="ztree"></ul>
								<div class="treelist-user">
									<p class="clearfix"><button type="button" class="btn btn-mini pull-right" onclick="addNodeAllUser()"><spring:message code='user.addNodeAll'/></button><strong id="strongOrgId"></strong><span id="spanCount"></span></p>
									<div id="treeUserList"></div>
								</div>
							</div>
							 <div id="sendLinkEmail" class="search-user-con">
							 	<textarea maxlength=2000 id="emailUrl" onpaste="userOnPaste()" onkeyup='doMaxLength(this)'></textarea>
								<div class="prompt"><spring:message code="link.msg.addRecipients"/></div>
								<div class="enterPrompt"><spring:message code="link.set.addMailInfo"/></div>
						     </div>
					     </div>
					     <div class="search-loading"><div id="loadingDiv" class="loading-div"></div></div>
				     	 <div class="share-message"><textarea id="messageText" maxlength=2000 placeholder="<spring:message code='inviteShare.addMessage'/>" onkeyup='doMaxLength(this)'></textarea></div>
					     <div class="btn-con">
	                		<button id="sendEmail" type="button" class="btn btn-primary"><spring:message code="link.button.sendLink"/></button>
	                		<button id="cancelEmail" type="button" class="btn" onclick="cancelSendEmail()"><spring:message code="button.cancel"/></button>		             
	                	 </div>
		            </div>
		            
	            </form>
	</div>
	<script type="text/javascript">
	var submitUsername = null;
	var tempUsername = null;
	var allMessageTo = new Array();
	var opts_viewGrid = null;
	var opts_treeUserList = null;
	var ownerId = "${ownerId}";
	var listViewType = "list";
	var iNodeId = "${folderId}";
	var linkStatus = "${linkStatus}";
	var isComplexCode = "${isComplexCode}";
	var curPage = 0;
	var accessCodeLen = 8;
	var isMailPatternError = false;
	var isPhonePatterError = false;
	var mailMsg = "${mailmsg}";
	var linksData = null;
	var treeUserData=null;
	var currentEditLink = null;
	var copyUrlMap = new clipMap();
	var treeUserHeadData = {
			"name": {"title" : '<spring:message code="inviteShare.sharedUserName"/>', "width": ""},
			"flag": {"title" : '', "width": "20px"}
			};
	
	$(document).ready( function() {
		$("#freshAcessCode, #copyLinkButtonCon").tooltip({ container:"body", placement:"top", delay: { show: 100, hide: 0 }, animation: false });
		
		var objType = '${type}';
		if(objType=='0'){
			$("#fileOrFolderType").addClass("fileImg-folder");
		}else{
			var name = '${name}';
			var type = _getStandardType(name);
			if(type == "img"){
				var thumUrl = "url('${thumbnailUrl}') no-repeat center center";
				$("#fileOrFolderType").css("background",thumUrl);
			}else{
				$("#fileOrFolderType").addClass("fileImg-"+type);
			}
		}
		
		if(isComplexCode == "true")
		{
			$("#accessCode").attr("readonly","readonly");
		}
		else
		{
			$("#accessCode").removeAttr("readonly");
		}
		
		$("#timeZone").val(getTimeZone());
		
		$("#sCode").bind("click", function () {
			if($("#sCode").is(":checked")){
				$("#accessCodeMode").val("static");
				if(isComplexCode == "true")
				{
					refreshLinkAccessCode();
				}
				$("#dCodeDiv").hide();
				$("#sCodeDiv").show();
				$("#dCode").attr("checked",false).parent().show();
				if(isComplexCode == "false" && $("#freshAcessCode").get(0))
				{
					$("#freshAcessCode").remove();
				}
			}else{
				$("#sCodeDiv").hide();
				$("#dCodeDiv").hide();
				$("#dCode").attr("checked",false).parent().hide();
			}
			
			var conH = parseInt($(".pop-content").outerHeight()+90);
			top.ymPrompt.resizeWin(650,conH);
       });
		
		$("#dCode").bind("click", function () {
			if($("#dCode").is(":checked")){
				$("#sCodeDiv").hide();
				$("#dCodeDiv").show();
				$("#accessCodeMode").val("mail");
			}else{
				$("#sCodeDiv").show();
				$("#dCodeDiv").hide();
			}
			
			var conH = parseInt($(".pop-content").outerHeight()+90);
			top.ymPrompt.resizeWin(650,conH);
       });

		$("#dateRadioCustom").bind("click", function () {
			if($("#dateRadioCustom").is(":checked")){
				$("#dateDiv").show();
			}else{
				$("#dateDiv").hide();
			}
			
			var conH = parseInt($(".pop-content").outerHeight()+90);
			top.ymPrompt.resizeWin(650,conH);
       });
		
		$("#upload").bind("click", function () {
			if($("#preview").is(":checked")){
				$("#download").get(0).checked = true;
			}
       });
	
		$("#download").bind("click", function () {
			if($("#download").is(":checked")){
				$("#preview").get(0).checked = true;
			}else{
				if($("#upload").is(":checked")){
					$("#preview").get(0).checked = false;
				}
			}
       });
		$("#preview").bind("click", function () {
			if($("#preview").is(":checked")){
				if($("#upload").is(":checked")){
					$("#download").get(0).checked = true;
				}
			}
			else{
				$("#download").get(0).checked = false;
			}
       });
		top.ymPrompt.resizeWin(650,350);
		
		init();
		initLink();
		initMailDIV();
		
	});

function init(){
	opts_treeUserList = $("#treeUserList").comboTableGrid({
		headData : treeUserHeadData,
		border:false,
		hideHeader : true,
		miniPadding : true,
		splitRow : false,
		stripe : true,
		dataId : "cloudUserId",
		ItemOp : "user-defined",
		style : "basicGrid basicGrid-add-collaborator",
		height : 180
    });
	
	$.fn.comboTableGrid.setItemOp = function(tableData, rowData, tdItem, colIndex){
		switch (colIndex) {
			case "name":
				try {
					var str = allMessageTo.join(";");
					tdItem.attr("id",rowData.cloudUserId +"_td").parent().css("cursor","pointer");
					if(str.indexOf(rowData.loginName) == -1){
						tdItem.find("p").prepend('<i class="icon-user"></i> ');
						
					}else{
						tdItem.find("p").css("color","#999").prepend('<i class="icon-user icon-gray"></i> ');
					}
					
					tdItem.click(function(){
						var str = allMessageTo.join(";");
						if(str.indexOf(rowData.loginName) == -1){
							addMessageTo(rowData.cloudUserId, rowData.email, rowData.loginName);
							tdItem.next().find("p").append('<i class="icon-selected"></i> ');
							tdItem.find("p").css("color","#999").find("i").addClass("icon-gray");
						}else{
							var itemValue = rowData.email;
							var tempArray = new Array();
							var length = allMessageTo.length;
							for ( var i = 0; i < length; i++) {
								var temp = allMessageTo.pop();
								if (temp != itemValue) {
									tempArray.push(temp);
								} else {
									break;
								}
							}
							allMessageTo = allMessageTo.concat(tempArray);
							
							$("#"+ rowData.cloudUserId).remove();
							
							tdItem.next().find("i").remove();
							tdItem.find("p").removeAttr("style").find("i").removeClass("icon-gray");
						}
					})
				} catch (e) {
				}
				break;
			case "flag":
				try {
					var str = allMessageTo.join(";");
					if(str.indexOf(rowData.loginName) != -1){
						tdItem.find("p").append('<i class="icon-selected"></i> ');
					}
				} catch (e) {
				}
				break;
			default : 
				break;
		}
	};
	initTree();
}
	
function initMailDIV()
{
	allMessageTo = new Array();
	
	$("#emailUrl").keydown(function(event) {
		if (event.keyCode == 13) {
			var tempEmail = $("#emailUrl").val();
			searchMessageTo(tempEmail);
			window.event.cancelBubble = true;
			window.event.returnValue = false;
		}else if(event.keyCode == 8){
			if($(this).val()==""){
				var memberId = $(this).parent().find("div.invite-member:last").attr("id");
				$(this).parent().find("div.invite-member:last").remove();
				allMessageTo.pop();
				
				var obj = $("#treeUserList").find("#" + memberId + "_td");
				obj.next().find("i").remove();
				obj.find("p").removeAttr("style").find("i").removeClass("icon-gray");
				
				var conH = parseInt($(".pop-content").outerHeight()+90);
				top.ymPrompt.resizeWin(650,conH);
			}
		}else if(event.keyCode !=38 && event.keyCode !=40 ){
			availableTags = [];
		}
		$(".enterPrompt").hide();
	});
	
	$("#emailUrl").keyup(function(event){
		submitUsername = $("#emailUrl").val();
		if(tempUsername != submitUsername){
			try{
				$("#emailUrl").autocomplete("close");
			}catch(e){
			}
		}
		userInputAutoSize(this);
	})
	
	$("#sendLinkEmail").click(function(){
		$("#emailUrl").focus();
	});
	
	$("#messageText").focus(function(){
		if(mailMsg != null && mailMsg != "" && $("#messageText").val() == ""){
			$("#messageText").val(mailMsg).select();
		}
	});
	
	$("#emailUrl").focus(function(){
		$(".prompt").hide();
		if($(this).val() =='' && allMessageTo.length < 1){
			$(".enterPrompt").show();
		}
		var conH = parseInt($(".pop-content").outerHeight()+90);
		top.ymPrompt.resizeWin(650,conH);
	}).blur(function(){
		if($(this).val() =='' && allMessageTo.length < 1){
			$(".prompt").show();
			$(".enterPrompt").hide();
		}
	})
}

function initTree(){
	var setting = {
			async: {
				enable: true,
				url:"${ctx}/cmb/listOrgTreeNode",
				autoParam:["groupId"]
			},
			data : {
				simpleData : {
					enable : true
				}
			},
			view : {
				expandSpeed : "",
				showIcon : false
			},
			callback:{
				onAsyncSuccess: zTreeOnAsyncError,
				onClick: zTreeOnClick
			}
		};		
		var zNodes = [];
		$.fn.zTree.init($("#treeArea"), setting, zNodes);
		$("#treeArea > li > span").click();
}

function zTreeOnAsyncError(event, treeId, treeNode, XMLHttpRequest, textStatus, errorThrown) {
    if(XMLHttpRequest.length==2&&XMLHttpRequest.substring(0,2)!="[]"){
    	location.href="${ctx}/logout";
    }
    if(XMLHttpRequest.length>2&&XMLHttpRequest.substring(0,2)!="[{"){
    	location.href="${ctx}/logout";
    }
};
function zTreeOnClick(event, treeId, treeNode){
	initTreeUserList(treeNode.orgId,treeNode.name,null)
}

function userInputAutoSize(that){
	var tempObj = $("#enterTempData"),
		_obj = $(that).parent().find("div.invite-member:last"),
		posCon = $("#sendLinkEmail").offset().left +5,
		posInput = _obj.get(0) ? (_obj.offset().left + _obj.outerWidth() + 5) : posCon,
		userConW = 515,
		tempW = 0,
		space = userConW - parseInt(posInput-posCon),
		thatParent = $(that).parent().get(0);
			
	tempObj.html($(that).val().replace(new RegExp(" ", "g"),"&nbsp;"));
	tempW = tempObj.width();
	if((tempW+5) > space || $(that).get(0).scrollHeight > 20){
		$(that).css({"width" : userConW });
		$(that).height(0);
		$(that).css({"height" : $(that).get(0).scrollHeight });
		thatParent.scrollTop = thatParent.scrollHeight;
	}else{
		$(that).css({"width" : space, "height" : 20 });
	}
	
	var conH = parseInt($(".pop-content").outerHeight()+90);
	top.ymPrompt.resizeWin(650,conH);
}

function deleteAllLink() {
		ymPrompt.confirmInfo({title:"<spring:message code='link.set.cancelLink'/>",maskAlphaColor:"gray", message:'<spring:message code="link.set.delLinkConfirm"/>',handler:function(tp) {
			if (tp=='ok')
			{
				$.ajax({
			        type: "POST",
			        url:"${ctx}/share/deleteLink/${ownerId}/${folderId}/all",
			        error: function(request) {
			        	handlePrompt("error",'<spring:message code="link.set.delLinkFail"/>','','5');
			        },
			        success: function() {
			        	top.handlePrompt("success",'<spring:message code="operation.success"/>');
			        	parent.ymPrompt.close();
			        	top.linkHandle();
			        }
			    });
			}
		}});
	}

function deleteLink(linkId, linkLength) {
	ymPrompt.confirmInfo({title:"<spring:message code='link.set.cancelLink'/>",maskAlphaColor:"gray", message:'<spring:message code="link.set.delLinkConfirm"/>',handler:function(tp) {
		if (tp=='ok')
		{
			$.ajax({
		        type: "POST",
		        url:"${ctx}/share/deleteLink/${ownerId}/${folderId}" + "?linkCode=" + linkId,
		        error: function(request) {
		        	doDeleteLinkError(request);
		        },
		        success: function() {
		        	if(linkLength>1){
		        		handlePrompt("success",'<spring:message code="operation.success"/>');
			        	getLink();
		        	}else{
		        		top.handlePrompt("success",'<spring:message code="operation.success"/>');
			        	parent.ymPrompt.close();
			        	top.linkHandle();
		        	}
		        }
		    });
		}
	}});
}


function deleteLinkNoCheck(linkId, linkLength) {
	$.ajax({
        type: "POST",
        url:"${ctx}/share/deleteLink/${ownerId}/${folderId}" + "?linkCode=" + linkId,
        error: function(request) {
        },
        success: function() {
        	currentEditLink=null;
        	if(linkLength>0){
        		hideAclDiv();
	        	getLink();
        	}else{
	        	parent.ymPrompt.close();
	        	top.linkHandle();
        	}
        }
    });
}

function callBackDelLink() {
	if(currentEditLink!=null){
		$.ajax({
	        type: "POST",
	        url:"${ctx}/share/deleteLink/${ownerId}/${folderId}" + "?linkCode=" + currentEditLink,
	        error: function(request) {
	        },
	        success: function() {
	        	currentEditLink=null;
	        }
	    });
	}

}

function initLink()
{
	if(linkStatus == "false")
	{
		setLink();
	}
	else
	{
		getLink();
	}
}

function hideACLTotal()
{
	$(".link-access").show();
	$(".link-email, .file-link-con").hide(0, function(){
		var conH = parseInt($(".pop-content").outerHeight()+120);
		top.ymPrompt.resizeWin(650,conH);
	});
}

function clearValues() {
	$("#acessCodeInfo").text("");
	$("#listDateEffectiveAt").text("");
	$("#listDateExpireAt").text("");
	
	$("#accessCode").val("");
	$("#effectiveAt").val("");
	$("#expireAt").val("");
}

function getLink()
{
	$.ajax({
        type: "GET",
        url:"${ctx}/share/getlink/"+ ownerId + "/" +iNodeId+"/?"+ new Date().getTime(),
        error: function(request) {
        	doInitLinkError(request);
        },
        success: function(data) {
        	ZeroClipboard.config( { moviePath: '${ctx}/static/clipboard/ZeroClipboard.swf' } );
        	
        	linksData = data;
        	$("#linksList").html("");
        	var clipObject,copyURL;
        	for(var i = 0; i< data.length; i++){
        		var uploadText = (data[i].upload == false) ? "":"<spring:message code='link.label.upload'/>";
        		var downloadText = (data[i].download == false) ? "":"<spring:message code='link.label.download'/>";
        		var previewText = (data[i].preview == false) ? "":"<spring:message code='link.label.scan'/>";
        		var AccessCodeHtml = "";
        		if(data[i].accessCodeMode == "static"){
	        		if (data[i].plainAccessCode != "" &&data[i].plainAccessCode != undefined){
						AccessCodeHtml='<span><spring:message code="link.accessCode"/> <em id="acessCodeInfo">'+ data[i].plainAccessCode +'</em></span>';
					}
        		}else{
        			AccessCodeHtml='<span><spring:message code="link.accessCode"/> <em id="acessCodeInfo"><spring:message code="link.label.dynamic.code"/></em></span>';
        		}
        		
        		var efAt = getFormatDate(data[i].effectiveAt, false);
			 	var exAt = getFormatDate(data[i].expireAt, false);
			 	var efAtZH = getFormatDate(data[i].effectiveAt, true);
			 	var exAtZH = getFormatDate(data[i].expireAt, true);
			 	var timeHtml = "";
			  	if (data[i].effectiveAt != undefined) {
					var dateEffectiveAt = efAt;
					var dateExpireAt = '<spring:message code="link.set.timeForEver"/>';
					if (data[i].expireAt != undefined) {
				  		dateExpireAt = exAt;
				  	}
				  	timeHtml = '<span> <spring:message code="link.set.timeDure"/> <em>'+ dateEffectiveAt +'</em> <spring:message code="link.set.timeTo"/> <em>'+ dateExpireAt +'</em> </span>';
				}else{
					
				}
        		
        		var linkHtml = '<div class="file-link">'+
							'<div class="input-append">'+
								'<input type="text" id="url'+ i +'" value="'+ data[i].url +'" readonly="readonly"/>'+
								'<button onclick="showAclDiv('+ i +')" type="button" class="btn" title="<spring:message code='button.modify'/>"><i class="icon-pencil"></i></button>'+
								'<button id="copyButton_'+ i +'" type="button" class="btn" title="<spring:message code='button.copy'/>"><i class="icon-copy"></i></button>'+
								'<button onclick="showEmailSend('+ i +')" type="button" class="btn"><i class="icon-email"></i></button>'+
								'<button type="button" onclick="deleteLink(\''+ data[i].id +'\','+ data.length +')" class="btn"><i class="icon-delete-blod"></i></button>'+
							'</div>'+
							'<div class="show-access" id ="linkInfo'+ i +'">'+
								'<span><spring:message code="link.set.linkACL"/> <em>'+ uploadText +' '+ downloadText +' ' + previewText +'</em></span>'+
							'</div>'+
						'</div>';
        		$("#linksList").append(linkHtml);
        		$("#linkInfo" + i).append(AccessCodeHtml);
        		$("#linkInfo" + i).append(timeHtml);
        		
        		
    			var clipObject = new ZeroClipboard( document.getElementById("copyButton_"+i) );
    			copyUrlMap.put(clipObject.id, data[i].url);
    			clipObject.on( "mouseOver", function(clipObject) {  
    				clipObject.setText(copyUrlMap.get(clipObject.id));
    		    });
    			clipObject.on( "complete", function(){ 
    				handlePrompt("success",'<spring:message code="link.set.copySuccess" />');
    			});
        	}
        	
        	var conH = parseInt($(".pop-content").outerHeight()+90);
        	top.ymPrompt.resizeWin(650,conH);
        	
        	return;
        }
    });
}

function setLink() {
	$("#urlAccess").val("");
	$("#download").attr("checked",true);
	$("#preview").get(0).checked = true;
	$("#upload").attr("checked",false);
	var objType = '${type}';
	if(objType==1){
		$("#upload").parent().hide();
	}
	$("#sCode").attr("checked",false);
	$("#dCode").attr("checked",false);
	$("#sCodeDiv, #dCodeDiv").hide();
	$("#dCode").parent().hide();
	$("#identities, #accessCode").val("");
	
	$("#effectiveAtTime,#expireAtTime, #effectiveAt, #expireAt").val("");
	$("#dateRadioCustom").attr("checked",false);
	$("#dateDiv").hide();

	$(".file-link-con, .link-email").hide();
	$(".link-access").show();
	var conH = parseInt($(".pop-content").outerHeight()+90);
	top.ymPrompt.resizeWin(650,conH);
	
	$.ajax({
        type: "POST",
        url:"${ctx}/share/setlink/"+ ownerId + "/" +iNodeId,
        data:$('#setLinkForm').serialize(),
        error: function(request) {
        	currentEditLink=null;
        	doInitLinkError(request);
        	ymPrompt_disableModalbtn("#saveAccess");
        	$("#cancelAccess").unbind();
        	$("#cancelAccess").bind("click",function(){
        		parent.ymPrompt.close();
        	});
        },
        success: function(data) {
        	currentEditLink=data.id;
        	$('#urlAccess').val(data.url);
        	$("#saveAccess").unbind();
        	$("#saveAccess").bind("click",function(){
        		updateLink(data.id);
        	});
        	$("#cancelAccess").unbind();
        	$("#cancelAccess").bind("click",function(){
        		if(linksData == null){
        			deleteLinkNoCheck(data.id, 0);
        		}else{
        			deleteLinkNoCheck(data.id, linksData.length);
        		}
        	});
        }
    });
}

function updateLink(linkId) {
	
	if(!$("#download").is(":checked") && !$("#preview").is(":checked") && !$("#upload").is(":checked")){
		handlePrompt("error",'<spring:message code="link.error.authority"/>','','5');
		return;
	}
	$("#accessCode").val($("#accessCode").val().trim());
	$("#accessCodeMode").val("static");
    if($("#sCode").is(":checked") &&  !$("#dCode").is(":checked")){
		var accessCode = $("#accessCode").val();
		$("#accessCodeMode").val("static");
		if(accessCode==null|| accessCode.trim()==""){
			handlePrompt("error",'<spring:message code="link.acesscode.notNull"/>','','5');
			return;
		}
		
		if(isComplexCode == "true")
		{
			if(accessCode.length<8||accessCode.length>20){
				handlePrompt("error",'<spring:message code="link.acesscode.invalidLen"/>','','5');
				return;
			}
			if(!checkLinkPWDRule(accessCode)){
				handlePrompt("error",'<spring:message code="link.acesscode.invalidChar"/>','','5');
				$("#accessCode").val("");
				return;
			}
		}
		else{
			var reg= new RegExp("^[A-Za-z0-9]+$");
			if(!reg.test(accessCode))
			{
				handlePrompt("error",'<spring:message code="link.accesscode.simple"/>','','5');
				return;
			}
			if(accessCode.length>20){
				handlePrompt("error",'<spring:message code="link.acesscode.invalidSimpleLen"/>','','5');
				return;
			}
		}
			
    }
    else
    {
    	$("#accessCode").val("");
    }
    
    if($("#dCode").is(":checked")){
		var identities = $("#identities").val();
		if(identities==null|| identities.trim()==""){
			handlePrompt("error",'<spring:message code="link.label.email.cmb.empty"/>','','5');
			return;
		}
		var mailAddrArray = identities.split(";");
		checkEmailArrayRule(mailAddrArray);
		checkPhoneArrayRule(mailAddrArray);
		if(isMailPatternError&&isPhonePatterError)
		{
			handlePrompt("error",'<spring:message code="link.set.cmb.invalidEmail"/>','','5');
			return;			
		}
		if(!isMailPatternError&&!isPhonePatterError)
		{
			handlePrompt("error",'<spring:message code="link.set.cmb.invalidEmail"/>','','5');
			return;			
		}
		if(!isMailPatternError)
		{
			$("#accessCodeMode").val("mail");
		}
		else
		{
			$("#accessCodeMode").val("phone");			
		}
    }
    else
    {
    	$("#identities").val("");
    }


	if($("#dateRadioCustom").is(":checked")){
		var effectiveAt = $("#effectiveAt").val();
		var expireAt = $("#expireAt").val();
		
		if(effectiveAt==null||effectiveAt==""){
			handlePrompt("error",'<spring:message code="link.set.needEffectiveAt"/>','','5');
			return;
		}

		if(expireAt!=null&&expireAt!=""&&effectiveAt>expireAt){
			handlePrompt("error",'<spring:message code="link.set.timeSetError"/>','','5');
			return;
		}
	}else{
		$("#effectiveAt").val("");
		$("#expireAt").val("");
	}
	
	$.ajax({
	    type: "POST",
	    url:"${ctx}/share/updateLink/"+ ownerId + "/" +iNodeId + "?linkCode=" + linkId,
	    data:$('#setLinkForm').serialize(),
	    error: function(request) {
	    	doUpdateLinkError(request);
	    },
	    success: function(data) {
	    	if(typeof(data)=='string' && data.indexOf('<html>')!=-1)
        	{
        		window.location.href="${ctx}/logout";
				return;
        	}
	    	
	    	handlePrompt("success",'<spring:message code="link.set.saveSuccess"/>','','5');
	    	
			hideAclDiv();
			getLink();
	    }
	});
}

function doInitLinkError(request)
{
	switch(request.responseText){
			case "BadRequest":
				ymPrompt.alert({title:'<spring:message code="link.title.error"/>',message:'<spring:message code="link.set.BadRquest"/>'});
				break;
			case "NoSuchLink":
			case "NoSuchItem":
				ymPrompt.alert({title:'<spring:message code="link.title.error"/>',message:'<spring:message code="link.set.NoSuchItems"/>'});
				break;
			case "Forbidden":
				ymPrompt.alert({title:'<spring:message code="link.title.error"/>',message:'<spring:message code="link.set.Forbidden"/>'});
				break;
			case "LinkExistedConflict":
				ymPrompt.alert({title:'<spring:message code="link.title.error"/>',message:'<spring:message code="link.set.Conflict"/>'});
				break;
			case "BusinessException":
				ymPrompt.alert({title:'<spring:message code="link.title.error"/>',message:'<spring:message code="link.set.setLinkFail"/>'});
				break;
			case "Unauthorized":
				ymPrompt.alert({title:'<spring:message code="link.title.error"/>',message:'<spring:message code="link.set.Forbidden"/>'});
				break;
			case "ExceedMaxLinkNum":
				ymPrompt.alert({title:'<spring:message code="link.title.error"/>',message:'<spring:message code="link.error.createLink"/>'});
				break;
			case "SecurityMatrixForbidden":
				ymPrompt.alert({title:'<spring:message code="link.title.error"/>',message:'<spring:message code="error.forbid"/>'});
				break;
			default:
				ymPrompt.alert({title:'<spring:message code="link.title.error"/>',message:'<spring:message code="link.set.setLinkFail"/>'});
	}
}

function doUpdateLinkError(request)
{
	switch(request.responseText){
			case "BadRequest":
				handlePrompt("error",'<spring:message code="link.set.BadRquest"/>','','5');
				break;
			case "NoSuchLink":
			case "NoSuchItem":
				handlePrompt("error",'<spring:message code="link.set.NoSuchItems"/>','','5');
				break;
			case "Forbidden":
				handlePrompt("error",'<spring:message code="link.set.Forbidden"/>','','5');
				break;
			case "BusinessException":
				handlePrompt("error",'<spring:message code="link.set.setLinkFail"/>','','5');
				break;
			case "LinkExistedConflict":
				handlePrompt("error",'<spring:message code="link.set.Conflict"/>','','5');
				break;
			case "Unauthorized":
				handlePrompt("error",'<spring:message code="link.set.Forbidden"/>','','5');
				break;
			default:
				handlePrompt("error",'<spring:message code="link.set.setLinkFail"/>','','5');
	}
}

function doDeleteLinkError(request)
{
	switch(request.responseText){
		case "BadRequest":
			handlePrompt("error",'<spring:message code="link.set.BadRquest"/>','','5');
			break;
		case "NoSuchLink":
		case "NoSuchItem":
			handlePrompt("error",'<spring:message code="link.set.NoSuchItems"/>','','5');
			break;
		case "BusinessException":
			handlePrompt("error",'<spring:message code="link.set.delLinkFail"/>','','5');
			break;
		case "Forbidden":
		case "Unauthorized":
			handlePrompt("error",'<spring:message code="error.forbid"/>','','5');
			break;
		default:
			handlePrompt("error",'<spring:message code="link.set.delLinkFail"/>','','5');
	}
}

function refreshLinkAccessCode() {
    $('#accessCode').val(getAccessCode(8));
}

function getFormatDate(date, isPattern){
	var tempDate = "";
	if(date == undefined){
		tempDate= "";
	}else{
		tempDate=new Date(date);
	}
	var pattern = "yyyy-MM-dd hh:mm";
	if(top.globalLang == "en" && !isPattern){
		pattern = "MM/dd/yyyy hh:mm";
	}
	if(tempDate==""){
		return "";
	}else{
		return tempDate.format(pattern);
	}
}

function getTimeZone()
{
	var offset = 0 - new Date().getTimezoneOffset();
	var gmtHours = Math.floor(offset/60);
	
	if(gmtHours < 0)
	{
		gmtHours = Math.ceil(gmtHours);
	}
	
	var gmtMinute = Math.abs(offset - gmtHours * 60) + "";
	
	if(gmtMinute.length == 1)
	{
		gmtMinute = "0" + gmtMinute;
	}
	
	if(gmtHours<0)
	{
		return "GMT" +  gmtHours + ":" + gmtMinute;
	}
	return "GMT+" +  gmtHours + ":" + gmtMinute;
}

function userOnPaste(){
	setTimeout(function() { 
		submitUsername = $("#emailUrl").val();
		userInputAutoSize("#emailUrl");
		searchMessageTo($("#emailUrl").val());
	}, 0);
}

var availableTags = [];
var unAvaliableTags = [];
function searchMessageTo(tempEmail) {
	if(tempEmail.length <= 1){
		return;
	}
	
	var searchSpiner = new Spinner(optsSmallSpinner).spin($("#loadingDiv").get(0)); 
	availableTags = "";
    var params= {
	    "ownerId": "${ownerId}", 
	    "folderId": "${folderId}",
	    "userNames": tempEmail
    };
	tempUsername = params.userNames;
    var list;
	$.ajax({
        type: "POST",
        data: params,
        url:"${ctx}/cmb/listMultiUser",
        error: function(request) {
        	searchSpiner.stop(); 
        	handlePrompt("error",'<spring:message code="link.set.listUserFail"/>','','5');
			$("#emailUrl").focus();
        },
        success: function(data) {
        	searchSpiner.stop(); 
        	if(typeof(data)=='string' && data.indexOf('<html>')!=-1)
        	{
        		window.location.href="${ctx}/logout";
				return;
        	}
        	if(tempUsername != submitUsername){
				return;
			}
        	availableTags = data.successList;
        	unAvaliableTags = data.failList;
			if (availableTags.length == 0) 
			{
				handlePrompt("error",'<spring:message code="inviteShare.error.empty"/>','','5');
				return;
			}
			if(data.single && availableTags.length == 1)
			{
				if(availableTags[0].userType == 1){
					addMessageTo(availableTags[0].cloudUserId,availableTags[0].email,availableTags[0].loginName);
				}
				else{
					addMessageTo(availableTags[0].cloudUserId,availableTags[0].email);
				}
				
				$( "#emailUrl" ).val("");
				return;
			}
			if(!data.single && availableTags.length > 0)
			{
				$(availableTags).each(function(n,item){
					if(item.userType == 1){
						addMessageTo(item.cloudUserId,item.email,item.loginName);
					}
					else{
						addMessageTo(item.cloudUserId,item.email);
					}
				});
				$("#emailUrl").val(unAvaliableTags + "");
				userInputAutoSize("#emailUrl");
				if(unAvaliableTags.length > 0){
					handlePrompt("error",'<spring:message code="inviteShare.error.partnoresult"/>','','5');
				}
				return;
			}
			if(data.single){
				$( "#emailUrl" ).bind( "keydown", function( event ) {
					if ( event.keyCode === $.ui.keyCode.TAB &&
							$( this ).data( "ui-autocomplete" ).menu.active ) {
						event.preventDefault();
					}
				}).autocomplete({
					disabled :true,
					position: { my : "left top", at: "left bottom", of: "#sendLinkEmail"},
					minLength: 2,
					source: function( request, response ) {
						response(availableTags);
					},
					focus: function() {
						return false;
					},
					select: function( event, ui ) {
						$(this).val("");
						if(ui.item.userType == 1){
							addMessageTo(ui.item.cloudUserId,ui.item.email,ui.item.loginName);
						}else{
							addMessageTo(ui.item.cloudUserId,ui.item.email);
						}
						return false;
					}	
		 		}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
					if(item.userType ==1){
						return $( "<li>" )
						.append( "<a><i class='icon-users icon-orange' ></i><strong>" + item.label + "</strong> (-) " + "<br>" + item.department + "</a>" )
						.appendTo( ul );
					}else{
						return $( "<li>" )
						.append( "<a><i class='icon-user'></i><strong>" + item.label + "</strong> (" + item.email + ") " + "<br>" + item.department + "</a>" )
						.appendTo( ul );
					}
					
				};
		        
				$("#emailUrl").autocomplete("enable");
				$("#emailUrl").autocomplete("search", $("#emailUrl").val());
			}
        }
    });
}

function split( val ) {
	return val.split( /,\s*/ );
}
function extractLast( term ) {
	return split( term ).pop();
}

var registerEventError = false;

function sendLinkByEmail(url, plainAccessCode) {
	$("#emailUrl").val("");
	var emailUrl = ""+ allMessageTo;
	emailUrl = emailUrl.replace(new RegExp(",", "g"),";");
	
	if(trimStr(emailUrl)==""){
		handlePrompt("error",'<spring:message code="link.set.emptyEmail"/>','','5');
		$("#emailUrl").focus();
		return;
	}
	if(emailUrl.charAt(emailUrl.length-1) == ';')
	{
		emailUrl = emailUrl.substring(0,emailUrl.length-1);
	}
	var mailAddrArray = emailUrl.split(";");
	if (mailAddrArray.length > 50)
    {
		handlePrompt("error",'<spring:message code="link.text.mailInfo"/>','','5');
		return;
    }

	var errorAddress = checkEmailArrayRule(mailAddrArray);
	
	if (emailUrl == "" || isMailPatternError) {
		handlePrompt("error",'<spring:message code="link.set.invalidEmail"/>','','5');
		return;
	}
	var msgText = $("#messageText").val();
	if(msgText == '<spring:message code="inviteShare.addMessage"/>')
	{
		msgText = "";
	}
	if(registerEventError){
		if(msgText.length > 2000){
			handlePrompt("error",'<spring:message code="link.email.error.length"/>','','5');
			return;
		}
	}
    var params= {
    		"ownerId": ownerId,
    		"iNodeId": iNodeId,
		    "emails": emailUrl,
		    "linkUrl": url,
		    "plainAccessCode": plainAccessCode,
			"message":msgText
	    };
	$.ajax({
        type: "POST",
        data: params,
        url:"${ctx}/share/sendLink",
        error: function(request) {
        	switch(request.responseText){
    		case "NoSuchLink":
    		case "NoSuchItem":
    			handlePrompt("error",'<spring:message code="link.set.NoSuchItems"/>','','5');
    			break;
    		case "Forbidden":
    		case "Unauthorized":
    			handlePrompt("error",'<spring:message code="error.forbid"/>','','5');
    			break;
    		default:
    			handlePrompt("error",'<spring:message code="link.set.sendFail"/>','','5');
    		}
        	$("#emailUrl").focus();
        },
        success: function() {
	    	if(typeof(data)=='string' && data.indexOf('<html>')!=-1)
        	{
        		window.location.href="${ctx}/logout";
				return;
        	}
        	top.ymPrompt.close();
        	top.handlePrompt("success",'<spring:message code="operation.success"/>');
        	top.linkHandle();
        	mailMsg = msgText;
        }
    });
}
   
function addMessageTo(userCloudId, userEmail, name) {
		$(".prompt").hide();
		var button = $("<a class='close' title=" + '<spring:message code="button.delete"/>' + ">&times;</a>");
		var text ="";
		if(name ==undefined || name == null){
			text = $('<div title="'+ userEmail +'">' + userEmail + '</div>');
		}else{
			text = $('<div title="'+ name +'">' + name + '</div>');
		}
		var dd = $('<div class="invite-member" id='+ userCloudId +'></div>');
		
		if($.inArray(userEmail, allMessageTo) != -1){
			return;
		}
		
		button.click( function() {
			$(this).parent().remove();
			userInputAutoSize("#emailUrl");
			var tempArray = new Array();
			var length = allMessageTo.length;
			for ( var i = 0; i < length; i++) {
				var temp = allMessageTo.pop();
				if (temp != userEmail) {
					tempArray.push(temp);
				} else {
					break;
				}
			}
			allMessageTo = allMessageTo.concat(tempArray);
			
			var obj = $("#treeUserList").find("#" + userCloudId + "_td");
			obj.next().find("i").remove();
			obj.find("p").removeAttr("style").find("i").removeClass("icon-gray");
			
			var conH = parseInt($(".pop-content").outerHeight()+90);
			top.ymPrompt.resizeWin(650,conH);
			
			if($("#emailUrl").val() =='' && allMessageTo.length < 1){
				$(".prompt").show();
			}
			window.event.cancelBubble = true;
			window.event.returnValue = false;
		});
		dd.append(text).append(button);
		$("#sendLinkEmail #emailUrl").before(dd);
		allMessageTo.push(userEmail);
		$("#emailUrl").focus();
		
		userInputAutoSize("#emailUrl");
		var conH = parseInt($(".pop-content").outerHeight()+90);
		top.ymPrompt.resizeWin(650,conH);
}

function checkEmailArrayRule(mailAddrArray){
	isMailPatternError = false;
	for (var i = 0; i < mailAddrArray.length; i++) {
		if (!checkEmailRule(mailAddrArray[i])) {
			isMailPatternError = true;
			return mailAddrArray[i];
			break;
		}
	}
}
function checkPhoneArrayRule(phoneAddrArray){
	isPhonePatterError = false;
	for (var i = 0; i < phoneAddrArray.length; i++) {
		if (!checkPhoneRule(phoneAddrArray[i])) {
			isPhonePatterError = true;
			return phoneAddrArray[i];
			break;
		}
	}
}

function checkEmailRule(mail){
	var pattern = /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;
	if (!pattern.test(mail)||mail.length<5||mail.length>100) {
		return false;
	}
	return true;
}
function checkPhoneRule(phone){
	var pattern = /^[0-9]{1,11}$/;
	if (!pattern.test(phone)||phone.length!=11) {
		return false;
	}
	return true;
}
function checkLinkPWDRule(accessCode){
	var LINK_PASSWORD_WITH_CHAR = /[a-zA-Z]+/;
	var LINK_PASSWORD_WITH_NUMBER = /[0-9]+/;
	var LINK_PASSWORD_WITH_SCHAR = /[-+.!@#$^&*]+/;
	var LINK_PASSWORD_PATTERN = /[-+.!@#$^&*0-9a-zA-Z]/;
	
	if (!LINK_PASSWORD_WITH_CHAR.test(accessCode)) 
	{
		return false;
	}
	if (!LINK_PASSWORD_WITH_NUMBER.test(accessCode)) 
	{
		return false;
	}
	
	if (!LINK_PASSWORD_WITH_SCHAR.test(accessCode)) 
	{
		return false;
	}
	
	if (!LINK_PASSWORD_PATTERN.test(accessCode)) 
	{
		return false;
	}
	
	return true;
}



function trimStr(str){
	return str.replace(/(^\s*)|(\s*$)/g,"");
}
function showAclDiv(index){
	$('#urlAccess').val(linksData[index].url);
	if(linksData[index].download == false){
		$("#download")[0].checked = false;
	}else{
		$("#download")[0].checked = true;
	}
	if(isComplexCode == "false"){
		$("#freshAcessCode").hide();
	}
	if(linksData[index].preview == false){
		$("#preview")[0].checked = false;
	}else{
		$("#preview")[0].checked = true;
	}
	
	var objType = '${type}';
	if(objType==1){
		$("#upload").parent().hide();
	}else{ 
		if(linksData[index].upload == false){
			$("#upload")[0].checked = false;
		}else{
			$("#upload")[0].checked = true;
		}
	}
	
	if(linksData[index].accessCodeMode == "static"){
		if (linksData[index].plainAccessCode != "" && linksData[index].plainAccessCode != undefined){
			$("#sCode")[0].checked = true;
			$("#dCode").parent().show();
			$("#sCodeDiv").show();
			$("#accessCode").val(linksData[index].plainAccessCode);
		}else{
			$("#sCodeDiv").hide();
			$("#sCode")[0].checked = false;
			$("#dCode").parent().hide();
			$("#accessCode").val("");
		}
		$("#dCode")[0].checked = false;
		$("#dCodeDiv").hide();
	}else if(linksData[index].accessCodeMode == "mail"){
		$("#sCode")[0].checked = true;
		$("#dCode")[0].checked = true;
		$("#sCodeDiv").hide();
		$("#dCode").parent().show();
		$("#identities").val(linksData[index].identities);
		$("#dCodeDiv").show();
	}else{
		$("#sCodeDiv,#dCodeDiv").hide();
		$("#dCode").parent().hide();
		$("#dCodeDiv").hide();
	}
	
	var efAt = getFormatDate(linksData[index].effectiveAt, false);
 	var exAt = getFormatDate(linksData[index].expireAt, false);
 	var efAtZH = getFormatDate(linksData[index].effectiveAt, true);
 	var exAtZH = getFormatDate(linksData[index].expireAt, true);
  	if (linksData[index].effectiveAt != undefined) {
  		$("#dateDiv").show();
  		$("#dateRadioCustom")[0].checked = true;
  		
		var dateEffectiveAt = efAt;
		var dateExpireAt = '<spring:message code="link.set.timeForEver"/>';
		if (linksData[index].expireAt != undefined) {
	  		dateExpireAt = exAt;
	  	}
		$("#effectiveAtTime").val(efAt);
		$("#expireAtTime").val(exAt);

  		$("#effectiveAt").val(efAtZH);
		$("#expireAt").val(exAtZH);
	}else{
		$("#dateDiv").hide();
		$("#dateRadioCustom")[0].checked = false;
	}

	$(".file-link-con, .link-email").hide();
	$(".link-access").show();
	$("#saveAccess").unbind();
	$("#saveAccess").bind("click",function(){
		updateLink(linksData[index].id);
	});
	$("#cancelAccess").unbind();
	$("#cancelAccess").bind("click",function(){
		hideAclDiv();
	});

	var conH = parseInt($(".pop-content").outerHeight()+90);
	top.ymPrompt.resizeWin(650,conH);
	
	top.$("#ym-window .ym-header-text").text("<spring:message code='link.updateAttr'/>");
}
function hideAclDiv(){
	$(".link-access, .link-email").hide();
	$(".file-link-con").show();
	
	var conH = parseInt($(".pop-content").outerHeight()+90);
	top.ymPrompt.resizeWin(650,conH);
	
	top.$("#ym-window .ym-header-text").text("<spring:message code='file.title.shareLink'/>");
}

function emailkeyup(){
	$("#emailUrl").val($("#emailUrl").val().replace(new RegExp(",", "g"),";"));
}

function showEmailSend(index){
	$('#urlEmail').val(linksData[index].url);
	var emailLinkInfo = $("#linkInfo"+index).clone();
	$(emailLinkInfo).removeAttr("id");
	$('#linkEmailInfo').html(emailLinkInfo);
	
	$("#sendEmail").unbind();
	$("#sendEmail").bind("click",function(){
		sendLinkByEmail(linksData[index].url, linksData[index].plainAccessCode);
	});
	
	$(".file-link-con, .link-access").hide();
	$(".link-email").show();
	var conH = parseInt($(".pop-content").outerHeight()+90);
	top.ymPrompt.resizeWin(650,conH);
	
	top.$("#ym-window .ym-header-text").text("<spring:message code='link.send.email'/>");
	
}

function initTreeUserList(orgId,orgName,keyword){
	var url = "${ctx}/cmb/listRetrieveUser";
	var params = {
		"orgId" : orgId,
		"keyword" : keyword
	};
	$("#treeUserList").showTableGridLoading();
	$.ajax({
		type : "POST",
		url : url,
		data : params,
		error : function(request) {
			handlePrompt("error",'<spring:message code="common.operationFailed" />');
		},
		success : function(data) {
			catalogData = data.content;
			treeUserData=catalogData;
			$("#treeUserList").setTableGridData(catalogData, opts_treeUserList);
			$("#strongOrgId").html(orgName);
			if(null!=catalogData)
			{
				$("#spanCount").html("("+catalogData.length+")<spring:message code='teamspace.user.count'/>");
			}
			var pageH = $("body").outerHeight();
			top.iframeAdaptHeight(pageH);
		}
	});
}
function addNodeAllUser()
{
	for(var i=0;i<treeUserData.length;i++)
	{
		addMessageTo(treeUserData[i].cloudUserId, treeUserData[i].email,treeUserData[i].loginName);
		var tdItem=$("#"+treeUserData[i].cloudUserId+"_td");
		tdItem.next().find("p").append('<i class="icon-selected"></i> ');
		tdItem.find("p").css("color","#999").find("i").addClass("icon-gray");
	}
}

function cancelSendEmail(){
	$(".link-email, .link-access, .enterPrompt").hide();
	$(".file-link-con, .prompt").show();
	$("#emailUrl, #messageText").val("").blur();
	$("#emailUrl").removeAttr("style");
	$("#sendLinkEmail").find(".invite-member").remove();
	allMessageTo = [];
	
	var conH = parseInt($(".pop-content").outerHeight()+90);
	top.ymPrompt.resizeWin(650,conH);
	
	top.$("#ym-window .ym-header-text").text("<spring:message code='file.title.shareLink'/>");
}

function getRandomNum(lbound, ubound) {
	return (Math.floor(Math.random() * (ubound - lbound)) + lbound);
}
	
function getRandomChar(number, chars, other) {
	var numberChars = "0123456789";
	var lowerAndUpperChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	var otherChars = "!@#$^&*-+.";
	var charSet = "";
	if (number == true)
	    charSet += numberChars;
	if (chars == true)
	    charSet += lowerAndUpperChars;
	if (other == true)
	    charSet += otherChars;
	return charSet.charAt(getRandomNum(0, charSet.length));
}
function getAccessCode(length) {
	var rc = getRandomChar(true, false, false);
	rc = rc + getRandomChar(false, true, false);
	rc = rc + getRandomChar(false, false, true);

	for (var idx = 3; idx < length; idx++) {
	    rc = rc + getRandomChar(true, true, true);
	}
	
	var arr_str = rc.split("");
    for (var i = 0; i < 50; i++)
    {
    	var idx1 = getRandomNum(0, length);
    	var idx2 = getRandomNum(0, length);
        
        if (idx1 == idx2)
        {
            continue;
        }
        
        var tempChar = arr_str[idx1];
        arr_str[idx1] = arr_str[idx2];
        arr_str[idx2] = tempChar;
    }
	
	return arr_str.join("");
}

function checkNickName(searchSpiner){
var n=$("#emailUrl").val();
$.ajax({
		type: "POST", 
        url:"${ctx}/share/listNickUser",
        data: {'userNames':n},
        error: function(data) {
            searchSpiner.stop();
			handlePrompt("error",'<spring:message code="inviteShare.listUserFail"/>','','5');
			$("#emailUrl").focus();
        },
        success: function(data) {
            searchSpiner.stop();
               var control=eval(data);
               if(!data.result)
               {
                  return;
               }
               
               for(var key in control.map)
               {
                 var con=control.map[key];
                 addMessageTo(con[0].email);
               }
               $( "#emailUrl" ).val("");
              
	        }
	    });

}

function getLinkCode(linkUrlTmp)
{
    if (linkUrlTmp.lastIndexOf("/") != -1)
    {
        return linkUrlTmp.substring(linkUrlTmp.lastIndexOf('/') + 1);
    }
    return "";
}

function clipMap()
{
    var size = 0;
    var entry = new Object();
    
    this.put = function (key , value)
    {
        if(!this.containsKey(key))
        {
            size ++ ;
        }
        entry[key] = value;
    }
    
    this.get = function (key)
    {
        if( this.containsKey(key) )
        {
            return entry[key];
        }
        else
        {
            return null;
        }
    }
    
    this.remove = function ( key )
    {
        if( delete entry[key] )
        {
            size --;
        }
    }
    
    this.containsKey = function ( key )
    {
        return (key in entry);
    }
    
    this.containsValue = function ( value )
    {
        for(var prop in entry)
        {
            if(entry[prop] == value)
            {
                return true;
            }
        }
        return false;
    }
    
    this.values = function ()
    {
        var values = new Array(size);
        for(var prop in entry)
        {
            values.push(entry[prop]);
        }
        return values;
    }
    
    this.keys = function ()
    {
        var keys = new Array(size);
        for(var prop in entry)
        {
            keys.push(prop);
        }
        return keys;
    }
    
    this.size = function ()
    {
        return size;
    }
}
</script>
</body>
</html>
