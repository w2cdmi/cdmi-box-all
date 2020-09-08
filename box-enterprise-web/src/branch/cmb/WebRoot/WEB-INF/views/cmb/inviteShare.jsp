<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html>
<head>
<%@ include file="../common/common.jsp"%>
<%@ include file="../common/commonForRole.jsp"%>
<link href="${ctx}/static/skins/default/css/mainforCMB.css" rel="stylesheet" type="text/css">
<link href="${ctx}/static/autocomplete/themes/base/jquery.ui.all.css" rel="stylesheet" type="text/css">
<script src="${ctx}/static/autocomplete/ui/jquery.ui.core.js" type="text/javascript"></script>
<script src="${ctx}/static/autocomplete/ui/jquery.ui.widget.js" type="text/javascript"></script>
<script src="${ctx}/static/autocomplete/ui/jquery.ui.position.js" type="text/javascript"></script>
<script src="${ctx}/static/autocomplete/ui/jquery.ui.menu.js" type="text/javascript"></script>
<script src="${ctx}/static/autocomplete/ui/jquery.ui.autocomplete.js" type="text/javascript"></script>
<script src="${ctx}/static/js/public/JQbox-hw-grid.js" type="text/javascript"></script>
<script src="${ctx}/static/clipboard/ZeroClipboard.js" type="text/javascript"></script>
</head>
<body>
	<input type="hidden" value="${mailmsg}" id="mailMsg"/>
	<div class="pop-content">
		<span id="enterTempData" style="display:none; height:30px;"></span>
		<div class="pop-share-invite">
			<div class="file-name clearfix" id="linkFileName">
				<span class="file-icon" id="fileOrFolderType"></span> <span class="name-txt" title="${name}">${name}</span> 
				<span id="collaboratorCount" class="collaborator-count"><span id="shareUserCount">&nbsp;</span>&nbsp;<spring:message code="inviteShare.count"/> - <a href="javascript:void(0)" id="copyButton"><spring:message code='link.set.copyUrl'/></a> </span>
				<span id="shareUserString" style="display:none;"></span>
			</div>
			<div id="manageCollaborator" class="collaborator-list">
                <div id="sharedUserList"></div>
			</div>
			<div class="add-collaborator-help"><spring:message code="group.member.help"/></div>
			<div class="user-form">
				<div class="department-con clearfix">
					<ul id="treeArea" class="ztree"></ul>
					<div class="treelist-user">
						<p class="clearfix"><button type="button" class="btn btn-mini pull-right" onclick="addNodeAllUser()"><spring:message code="group.member.addAll"/></button><strong id="strongOrgId"></strong><span id="spanCount"></span></p>
						<div id="treeUserList"></div>
					</div>
				</div>
				 <div id="inviteCollaborator" class="search-user-con">
					<textarea maxlength=2000 id="messageAddr" onpaste="userOnPaste()" onkeyup='doMaxLength(this)'></textarea>
					<c:if test="${shareStatus == 0}">
					<div class="prompt" ><spring:message code="inviteShare.invite.first"/></div>
					</c:if>
					<c:if test="${shareStatus == 1}">
					<div class="prompt" ><spring:message code="inviteShare.invite.more"/></div>
					</c:if>
					<div class="enterPrompt" ><spring:message code="inviteShare.searchUserInfo"/></div>
					
			     </div>
		    </div>
		     <div class="search-loading"><div id="loadingDiv" class="loading-div"></div></div>
		     <div id="memberTypeCon" class="member-type dropdown row-fluid">
				<a class="btn dropdown-toggle span12" data-toggle="dropdown"><span class="caret"></span><strong id="selectedAuth"><spring:message code="teamSpace.option.viewer" /></strong></a>
				<ul class="dropdown-menu">
					<c:forEach items="${systemRoles}" var="systemRole">
						<c:if test="${type == 0}">
							<li><a id="memberType_${systemRole.name}" onclick="selectSomeOne(this,'${systemRole.name}');"  /></a></li>
						</c:if>
						<c:if test="${type == 1}">
							<c:if test="${systemRole.name ne 'uploader' and systemRole.name ne 'uploadAndView' and systemRole.name ne 'editor'}">
								<li><a id="memberType_${systemRole.name}" onclick="selectSomeOne(this,'${systemRole.name}');"  /></a></li>
							</c:if>
						</c:if>
		       		</c:forEach>
				</ul>
				<input type="hidden" id="txtSlctAuthType" value="viewer" />
	  		 </div>
		     <div id="inviteMessage" class="share-message"><textarea maxlength=2000 id="messageText" placeholder='<spring:message code="inviteShare.addMessage"/>' onkeyup='doMaxLength(this)'></textarea></div>
		     <div id="inviteBtnCon" class="btn-con" >
		    	<button id="submit_btn" type="button" class="btn btn-primary" onclick="shareToOthers()"><spring:message code="button.invite"/></button>
		    	<button id="" type="button" class="btn" onclick="cancelInvite()"><spring:message code="button.cancel"/></button>
		   	 </div>
		   	 <div id="manageBtnCon" class="btn-con">
		   	 	<c:if test="${type == 0}">
		   	 		<button id="btnCancelAll" type="button" onclick="cancelShare()" class="btn"><spring:message code="inviteShare.button.cancel.folder"/></button>
		   	 	</c:if>
				<c:if test="${type == 1}">
		   	 		<button id="btnCancelAll" type="button" onclick="cancelShare()" class="btn"><spring:message code="inviteShare.button.cancel.file"/></button>
		   	 	</c:if>
	    		<button id="" type="button" class="btn btn-primary" onclick="top.ymPrompt.close()"><spring:message code="button.close"/></button>
	   	 	 </div>
		</div>
	</div>
	<script type="text/javascript">
	var submitUsername = null;
	var tempUsername = null;
	var allMessageTo = new Array();
	var opts_viewGrid = null;
	var treeUserHeadData = null;
	var ownerId = "${ownerId}";
	var listViewType = "list";
	var iNodeId = "${folderId}";
	var isShare = "${shareStatus}";
	var objType = '${type}';
	var accountRoleData = null;
	var treeUserData=null;
	
	var headData = {
			"sharedUserName": {"title" : '<spring:message code="inviteShare.sharedUserName"/>', "width": "30%"},
			"sharedDepartment" : {"title" : '<spring:message code="common.field.department"/>', "width": "32%"},
			"roleName" :{"width" : "28%"},
			"removeTag" : {"title" : '<spring:message code="common.field.operation"/>', "width": "", "cls":"ar"}};
	var treeUserHeadData = {
			"name": {"title" : '<spring:message code="inviteShare.sharedUserName"/>', "width": ""},
			"flag": {"title" : '', "width": "20px"}
			};
	var curPage = 0;
	var mailMsg = "";
	
	$(document).ready( function() {
		mailMsg = $("#mailMsg").val();
		$("#linkFileName span").tooltip({ container:"body", placement:"bottom", delay: { show: 100, hide: 0 }, animation: false });
		
		$("#messageAddr").keydown(function(event) {
			if (event.keyCode == 13) {
				if($(".ui-autocomplete").get(0) && $(".ui-autocomplete").find(".ui-state-focus").get(0)){
					return;
				}
				searchMessageTo();
				if(window.event){
					window.event.cancelBubble = true;
					window.event.returnValue = false;
				}
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
			}
			else if(event.keyCode !=38 && event.keyCode !=40 ){
				availableTags = [];
			}
			$(".enterPrompt").hide();
		});
		$("#messageAddr").keyup(function(event){
			submitUsername = $("#messageAddr").val();
			if(submitUsername != tempUsername){
				try{
					$("#messageAddr").autocomplete("close");
				}catch(e){
				}
			}
			userInputAutoSize(this);
		})
		init();
		initDataList(1);
		initAccountRoleList();
		
		
	});
	
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
	
	function userOnPaste(){
		$(".enterPrompt").hide();
		setTimeout(function() { 
			submitUsername = $("#messageAddr").val();
			userInputAutoSize("#messageAddr");
			searchMessageTo();
		}, 0);
	}
	
	function userInputAutoSize(that){
		var tempObj = $("#enterTempData"),
			_obj = $(that).parent().find("div.invite-member:last"),
			posCon = $("#inviteCollaborator").offset().left +5,
			posInput = _obj.get(0) ? (_obj.offset().left + _obj.outerWidth() + 5) : posCon,
			userConW = 525,
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
	
	function deleteSharedUser(sharedUserId, sharedUserType){
		var params= {
		    "iNodeId": iNodeId,
		    "sharedUserId": sharedUserId,
		    "sharedUserType": sharedUserType
		 };
		$.ajax({
		       type: "POST",
		       url: "${ctx}/share/deleteSharedUser",
		       data: params,
		       error: function(request) {
		   		var responseObj = $.parseJSON(request.responseText);
					switch (responseObj.code) {
						case "BadRequest":
							handlePrompt("error",'<spring:message code="share.error.BadRquest"/>');break;
						case "NoSuchItem":
							handlePrompt("error",'<spring:message code="share.error.NoSuchItems"/>');break;
						case "Forbidden":
						case "SecurityMatrixForbidden":
							handlePrompt("error",'<spring:message code="share.error.Forbidden"/>');break;
						default:
							handlePrompt("error",'<spring:message code="operation.failed"/>');break;
					}
			    },
			    success: function(data) {
					handlePrompt("success",'<spring:message code="inviteShare.delUserSuccess"/>','','5');
					initDataList(1);
					top.shareHandle(true);
				}
		});
	}
	

	function init(){
		if(isShare == 1){
			$("#manageCollaborator,#manageBtnCon,#collaboratorCount").show();
			$("#memberTypeCon,#inviteMessage,#inviteBtnCon, .department-con, .add-collaborator-help").hide();
			top.ymPrompt.resizeWin(650,430);
			
			top.$("#ym-window .ym-header-text").text("<spring:message code='invite.share' />");
		}else{
			$("#manageCollaborator,#manageBtnCon,#linkFileName").hide();
			$("#memberTypeCon,#inviteMessage,#inviteBtnCon, .department-con, .add-collaborator-help").show();
			top.ymPrompt.resizeWin(650,570);
			
			top.$("#ym-window .ym-header-text").text("<spring:message code='invite.share.pp' />");
		}
		initTree();
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
		
		opts_viewGrid = $("#sharedUserList").comboTableGrid({
			headData : headData,
			border:false,
			hideHeader : true,
			miniPadding : true,
			splitRow : false,
			stripe : true,
			dataId : "id",
			ItemOp : "user-defined",
			height : 175
	    });
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
			height : 220
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
								addMessageTo(rowData.cloudUserId, rowData.loginName,null,rowData.name, rowData.email);
								tdItem.next().find("p").append('<i class="icon-selected"></i> ');
								tdItem.find("p").css("color","#999").find("i").addClass("icon-gray");
							}else{
								var itemValue = "["+rowData.cloudUserId+"]"+rowData.loginName +"["+rowData.email+"]";
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
				case "sharedUserName":
					try {
						if(rowData.sharedUserType==1){
							tdItem.find("p").prepend('<i class="icon-users icon-orange"></i> ');
						}
						else{
							tdItem.find("p").prepend('<i class="icon-user"></i> ');
						}
					} catch (e) {
					}
					break;
				case "roleName":
					try {
						var alink, userRole = rowData.roleName, roleText, userType = rowData.sharedUserType,sharedId = rowData.sharedUserId, trId, dropClass;

						roleText = roleMsgs[userRole];
						trId = tdItem.parent().attr("id").substring(20);
						dropClass = "";
						if(trId > 4){
							dropClass = "dropup";
						}
						
						alink = "<span class=\"dropdown "+ dropClass +"\">"+
		       			"<a href=\"javascript:void(0)\" onclick=\"dropAuthType(this,"+ sharedId +","+ userType +")\" class=\"dropdown-toggle\" data-toggle=\"dropdown\">"+ roleText +"<i class=\"icon-caret-down icon-gray\"></i></a>"+
		       			"<ul class=\"dropdown-menu pull-right\"></ul>"+
		       			"</span>";

						tdItem.find("p").html('').css("overflow","visible").append(alink);
						tdItem.attr("title", "");

					} catch (e) {}
					break;
				case "removeTag":
					try {
						var alink, userType = rowData.sharedUserType,sharedId = rowData.sharedUserId;
						alink = "<button class=\"btn btn-mini btn-link\" type=\"button\" onclick=\"deleteSharedUser("+ sharedId +","+ userType +")\" title=\"<spring:message code='button.remove'/>\"><i class=\"icon-delete-alt icon-gray\"></i></button>";
						tdItem.find("p").append(alink);
					} catch (e) {
					}
					break;
				default : 
					break;
			}
		};
		
		$("#messageText").focus(function(){
			if(mailMsg != null && mailMsg != "" && $("#messageText").val() == ""){
				$("#messageText").val(mailMsg).select();
			}
		});
		
		$("#inviteCollaborator").click(function(){
			$("#messageAddr").focus();
		});
		$("#messageAddr").focus(function(){
			$(".prompt").hide();
			$("#manageBtnCon,#linkFileName,#manageCollaborator").hide();
			$("#memberTypeCon,#inviteMessage,#inviteBtnCon, .department-con, .add-collaborator-help").show();
			ymPrompt_enableModalbtn("#submit_btn");
			
			if($(this).val() =='' && allMessageTo.length < 1){
				$(".enterPrompt").show();
			}
			if(isShare == 1){
				top.$("#ym-window .ym-header-text").text("<spring:message code='invite.share.pp' />");
				var conH = parseInt($(".pop-content").outerHeight()+90);
				top.ymPrompt.resizeWin(650,conH);
			}
			
		}).blur(function(){
			if($(this).val() =='' && allMessageTo.length < 1){
				$(".prompt").show();
				$(".enterPrompt").hide();
			}
		})
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
					$("#spanCount").html('('+catalogData.length+')人');
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
			addMessageTo(treeUserData[i].cloudUserId, treeUserData[i].loginName,null,treeUserData[i].name, treeUserData[i].email);
			var tdItem=$("#"+treeUserData[i].cloudUserId+"_td");
			tdItem.next().find("p").append('<i class="icon-selected"></i> ');
			tdItem.find("p").css("color","#999").find("i").addClass("icon-gray");
		}
	}
	function initDataList(curPage){
		 var url = "${ctx}/share/listSharedUser";
		 var params= {
			    "ownerId": ownerId, 
			    "iNodeId": iNodeId,
			    "pageNumber": curPage
		 };
		 $.ajax({
		        type: "POST",
		        url: url,
		        data: params,
		        error: function(request) {
		        	handlePrompt("error",'<spring:message code="inviteShare.listSharedUserFail"/>','','5');
		        },
		        success: function(data) {
		        	catalogData = data.content;
					$("#shareUserCount").text(data.content.length);
					if(data.content.length == 0){
						$("#btnCancelAll").hide();
					}else{
						$("#btnCancelAll").show();
					}
					
					$("#sharedUserList").setTableGridData(catalogData, opts_viewGrid);
					
					var userListStr = "";
					for(var i = 0; i<data.content.length; i++){
						if(i == (data.content.length - 1)){
							userListStr += data.content[i].sharedUserLoginName;
						}else{
							userListStr += data.content[i].sharedUserLoginName +";";
						}
					}
					$("#shareUserString").html(userListStr);
					
					ZeroClipboard.config( { moviePath: '${ctx}/static/clipboard/ZeroClipboard.swf' } );
					var client = new ZeroClipboard( document.getElementById("copyButton") );
					client.on( "mouseOver", function(client) {       
				    	client.setText($("#shareUserString").text());    
				    });
					client.on( "complete", function(){ 
						handlePrompt("success",'<spring:message code="link.set.copySuccess" />','','5');
					}); 
		        }
		    });
	}

function cancelShare() {
	ymPrompt.confirmInfo({title:"<spring:message code='inviteShare.button.removeAll'/>",message:'<spring:message code="inviteShare.removeAllConfirm"/>', maskAlphaColor:"gray", handler:function(tp) {
		if (tp=='ok')
		{
			$.ajax({
		        type: "POST",
		        data:{"iNodeId":"${folderId}"},
		        url:"${ctx}/share/cancelShare",
		        error: function(request) {
		    		var responseObj = $.parseJSON(request.responseText);
		    		switch (responseObj.code) {
						case "BadRequest":
							handlePrompt("error",'<spring:message code="share.error.BadRquest"/>');break;
						case "NoSuchItem":
							handlePrompt("error",'<spring:message code="share.error.NoSuchItems"/>');break;
						case "Forbidden":
						case "SecurityMatrixForbidden":
							handlePrompt("error",'<spring:message code="share.error.Forbidden"/>');break;
						default:
							handlePrompt("error",'<spring:message code="operation.failed"/>');break;
					}
		        },
		        success: function() {
					top.ymPrompt.close();
					top.handlePrompt("success",'<spring:message code="inviteShare.removeAllSuccess"/>');
					top.shareHandle(true);
		        }
		    });
		}
	}});
}


function getFormatDate(date){
	var tempDate = "";
	if(date == undefined){
		tempDate= "";
	}else{
		tempDate=new Date(date);
	}
	var pattern = "yyyy-MM-dd hh:mm:ss";
	if(tempDate==""){
		return "";
	}else{
		return tempDate.format(pattern)
	}
}

function selectSomeOne(that,val){
	$("#selectedAuth").html($(that).html());
	$("#txtSlctAuthType").val(val);
}


function dropAuthType(that,shareUserId, userType){
	var dropSlct, popDiv = "";

	for (var i = 0;i<accountRoleData.length;i++) {
		if(objType == 0){
			popDiv += "<li><a href='javascript:void(0)' onclick='updateAuthType("+ shareUserId +", "+userType+",\""+accountRoleData[i].name+"\",this)'>"+roleMsgs[accountRoleData[i].name]+"</a></li>";

		}else{
			if(accountRoleData[i].name != 'uploader' && accountRoleData[i].name != 'uploadAndView' && accountRoleData[i].name != 'editor'){
				popDiv += "<li><a href='javascript:void(0)' onclick='updateAuthType("+ shareUserId +", "+userType+",\""+accountRoleData[i].name+"\",this)'>"+roleMsgs[accountRoleData[i].name]+"</a></li>";
			}
		}
	}
	$(that).next().html("").append($(popDiv));
}

function updateAuthType(shareUserId,userType,authType,that) {
	var params= {
		    "iNodeId": "${folderId}",
		    "userId": shareUserId,
			"userType":userType,
			"authType":authType
	    };
		isAddSharing = true;
		$.ajax({
			type: "POST",
	        data: params,
	        url:"${ctx}/share/updateShare",
	        error: function(request) {
	    		var responseObj = $.parseJSON(request.responseText);
	    		switch (responseObj.code) {
					case "BadRequest":
						handlePrompt("error",'<spring:message code="share.error.BadRquest"/>');break;
					case "NoSuchItem":
						handlePrompt("error",'<spring:message code="share.error.NoSuchItems"/>');break;
					case "Forbidden":
					case "SecurityMatrixForbidden":
						handlePrompt("error",'<spring:message code="share.error.Forbidden"/>');break;
					default:
						handlePrompt("error",'<spring:message code="operation.failed"/>');break;
				}
	        },
	        success : function() {
	        	isAddSharing = false;
				$(that).parent().parent().prev().html($(that).html() +"<i class=\"icon-caret-down icon-gray\"></i>");
				handlePrompt("success","<spring:message code='operation.success'/>");
				initDataList(currentPage);
			}
	    });   
		
}

var availableTags = [];
var unAvailableTags = [];
function searchMessageTo() {
    if($("#messageAddr").val().length <= 1){
		return;
	}
	<%-- loading --%>
	var searchSpiner = new Spinner(optsSmallSpinner).spin($("#loadingDiv").get(0));
	
	availableTags = "";
    var params= {
	    "ownerId": "${ownerId}", 
	    "folderId": "${folderId}",
	    "userNames": $("#messageAddr").val()
    };
	tempUsername = params.userNames;
    var list;
	$.ajax({
        type: "POST",
        data: params,
        url:"${ctx}/cmb/listMultiUser",
        error: function(request) {
        	searchSpiner.stop(); 
			handlePrompt("error",'<spring:message code="inviteShare.listUserFail"/>','','5');
			$("#messageAddr").focus();
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
			unAvailableTags = data.failList;
			if (availableTags.length == 0 && unAvailableTags.length == 0) {
				handlePrompt("error",'<spring:message code="inviteShare.error.empty"/>','','5');
				return;
			}
			if (availableTags.length == 0 && unAvailableTags.length > 0) {
				handlePrompt("error",'<spring:message code="inviteShare.error.noresult"/>','','5');
				return;
			}
			if(data.single && availableTags.length == 1){
				if(availableTags[0].userType == 1){
					addMessageTo(availableTags[0].cloudUserId, availableTags[0].loginName, availableTags[0].type, availableTags[0].label,null);
				}
				else{
					addMessageTo(availableTags[0].cloudUserId, availableTags[0].loginName, availableTags[0].type, availableTags[0].label,availableTags[0].email);
				}
				$("#messageAddr").val("");
				return;
			}
			if(!data.single && availableTags.length > 0){
				$(availableTags).each(function(n,item){
					if(item.userType == 1){
						addMessageTo(item.cloudUserId,item.loginName,item.type,item.label,null);
					}
					else{
						addMessageTo(item.cloudUserId,item.loginName,item.type,item.label,item.email);
					}
					
				});
				$("#messageAddr").val(unAvailableTags + "");
				userInputAutoSize("#messageAddr");
				if(unAvailableTags.length > 0){
					handlePrompt("error",'<spring:message code="inviteShare.error.partnoresult"/>','','5');
				}
				return;
			}
			if(data.single){
				$("#messageAddr").bind( "keydown", function( event ) {
				if ( event.keyCode === $.ui.keyCode.TAB &&
						$( this ).data( "ui-autocomplete" ).menu.active ) {
					event.preventDefault();
				}
				}).autocomplete({
					disabled :true,
					position: { my : "left top", at: "left bottom", of: "#inviteCollaborator"},
					minLength: 2,
					cacheLength:1,
					source: function( request, response ) {
						response(availableTags);
					},
					focus: function() {
						return false;
					},
					select: function( event, ui ) {
						$(this).val("");
						if(ui.item.userType == 1){
							addMessageTo(ui.item.cloudUserId, ui.item.loginName, ui.item.type, ui.item.label,null);
						}
						else{
							addMessageTo(ui.item.cloudUserId, ui.item.loginName, ui.item.type, ui.item.label,ui.item.email);
						}
						
						return false;
					}	
		 		}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
					if(item.userType == 1){
						return $( "<li>" )
						.append( "<a><i class='icon-users icon-orange'></i><strong>" + item.label + "</strong> (" + item.loginName + ") " + "<br>" + item.department + "</a>" )
						.appendTo( ul );
					}else{
						return $( "<li>" )
						.append( "<a><i class='icon-user'></i><strong>" + item.label + "</strong> (" + item.loginName + ") " + "<br>" + item.department + "</a>" )
						.appendTo( ul );
					}
				};
		        
				$("#messageAddr").autocomplete("enable");
				$("#messageAddr").autocomplete("search", $("#messageAddr").val());
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
var isAddSharing = false;
var registerEventError = false;

function shareToOthers(){
	ymPrompt_disableModalbtn("#submit_btn");
	if(isAddSharing){
		return;
	}
	if(allMessageTo.length==0){
		handlePrompt("error",'<spring:message code="inviteShare.notSetShareUser"/>','','5');
		$("#messageAddr").focus();
		ymPrompt_enableModalbtn("#submit_btn");
		return;
	}
	$("#messageAddr").val(""); 
	var shareToStr = getTrunckData(allMessageTo);
	
	var msgText = $("#messageText").val();
	if(msgText == '<spring:message code="inviteShare.addMessage"/>'){
		msgText = "";
	}
	if(registerEventError){
		if(msgText.length > 2000){
			msgText = msgText.substring(0, 2000);
		}
	}
	var authType = $("#txtSlctAuthType").val();
	var params= {
	    "ownerId": "${ownerId}", 
	    "iNodeId": "${folderId}",
	    "shareToStr": ""+shareToStr,
		"message":msgText,
		"authType" : authType
    };
	isAddSharing = true;
	$.ajax({
		type: "POST",
        data: params,
        url:"${ctx}/share/addShare",
        error: function(request) {
			isAddSharing = false;
    		var responseObj = $.parseJSON(request.responseText);
    		switch (responseObj.code) {
				case "BadRequest":
					handlePrompt("error",'<spring:message code="share.error.BadRquest"/>');break;
				case "NoSuchItem":
					handlePrompt("error",'<spring:message code="share.error.NoSuchItems"/>');break;
				case "Forbidden":
				case "SecurityMatrixForbidden":
					handlePrompt("error",'<spring:message code="share.error.Forbidden"/>');break;
				default:
					handlePrompt("error",'<spring:message code="operation.failed"/>');break;
			}
        	$("#messageAddr").focus();
			ymPrompt_enableModalbtn("#submit_btn");
        },
        success: function() {
			isAddSharing = false;
			if(isShare == 1){
				top.ymPrompt.resizeWin(650,430);
				$("#memberTypeCon,#inviteMessage,#inviteBtnCon,.department-con, .add-collaborator-help").hide();
				$("#manageBtnCon, #linkFileName,#manageCollaborator").show();
				$("#messageAddr, #messageText").val("").blur();
				$("#inviteCollaborator").find(".invite-member").remove();
				allMessageTo = [];
				$(".prompt").show();
				$(".enterPrompt").hide();
				initDataList(1);
				handlePrompt("success",'<spring:message code="operation.success"/>','','5');
				mailMsg=msgText;
				top.shareHandle();
				
				top.$("#ym-window .ym-header-text").text("<spring:message code='invite.share' />");
			}else{
	        	top.ymPrompt.close();
	        	top.handlePrompt("success",'<spring:message code="operation.success"/>');
				top.shareHandle();
			}
        }
    });   
}

function addMessageTo(userCloudId, userLoginName,userType,userName, userEmail) {
		$(".prompt").hide();
	    var loginName = "${loginUserName}";
	    var subName = $("#messageAddr").val().trim();
		var itemValue = "["+userCloudId+"]"+userLoginName +"["+userEmail+"]";
		if(userLoginName == loginName && userName == subName && userEmail != null){
			handlePrompt("error",'<spring:message code="inviteShare.notShareToSelf"/>','','5');
			return;
		}	
		
		if($.inArray(itemValue, allMessageTo) != -1){
			return;
		}
		var button = $("<a class='close' title=" + '<spring:message code="button.remove"/>' + ">&times;</a>");
		var text = $('<div title="'+ userName +'">' + userName + '</div>');
		var dd = $('<div class="invite-member" id='+ userCloudId +'></div>');
		button.click( function() {
			$(this).parent().remove();
			userInputAutoSize("#messageAddr");
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
			
			var obj = $("#treeUserList").find("#" + userCloudId + "_td");
			obj.next().find("i").remove();
			obj.find("p").removeAttr("style").find("i").removeClass("icon-gray");
			
			var conH = parseInt($(".pop-content").outerHeight()+90);
			top.ymPrompt.resizeWin(650,conH);
			
			if($("#messageAddr").val() =='' && allMessageTo.length < 1){
				$(".prompt").show();
			}
			window.event.cancelBubble = true;
			window.event.returnValue = false;
		});
		dd.append(text).append(button);
		$("#inviteCollaborator #messageAddr").before(dd);
		allMessageTo.push(itemValue);
		
		userInputAutoSize("#messageAddr");
		var conH = parseInt($(".pop-content").outerHeight()+90);
		top.ymPrompt.resizeWin(650,conH);
}

function cancelInvite(){
	if(isShare == 1){
		top.ymPrompt.resizeWin(650,430);
		$("#memberTypeCon,#inviteMessage,#inviteBtnCon,.enterPrompt, .department-con, .add-collaborator-help").hide();
		$("#manageBtnCon, .prompt, #linkFileName,#manageCollaborator").show();
		$("#messageAddr, #messageText").val("").blur();
		$("#messageAddr").removeAttr("style");
		$("#inviteCollaborator").find(".invite-member").remove();
		allMessageTo = [];
		
		top.$("#ym-window .ym-header-text").text("<spring:message code='invite.share' />");
	}else{
		top.ymPrompt.close();
	}
}

function initAccountRoleList() {
	var url = "${ctx}/accountrole/list";
	var roles;
	$.ajax({
		type : "GET",
		url : url,
		error : function(request) {
			roles = null;
		},
		success : function(data) {
			accountRoleData = data;
			if(data.length > 0){
				if(objType == 0){
					var isSet = false;
					for(var i=0;i<data.length;i++){
						$("#memberType_" + data[i].name).html(roleMsgs[data[i].name]);
						$("#memberType_" + data[i].name).attr("title",setAuthorityHint(accountRoleData[i]));
						if(data[i].name == 'viewer'){
							$("#selectedAuth").html(roleMsgs[data[i].name])
							$("#txtSlctAuthType").val(data[i].name);
							isSet = true;
						}
					}
					if(!isSet){
						$("#selectedAuth").html(roleMsgs[data[0].name])
						$("#txtSlctAuthType").val(data[0].name);
					}
				}else{
					var isSet = false;
					var seleteIndex = -1;
					for(var i=0;i<data.length;i++){
						if(data[i].name != 'uploader' && data[i].name != 'uploadAndView' && data[i].name != 'editor'){
							$("#memberType_" + data[i].name).html(roleMsgs[data[i].name]);
							$("#memberType_" + data[i].name).attr("title",setAuthorityHint(accountRoleData[i]));
							if(data[i].name == 'viewer'){
								$("#selectedAuth").html(roleMsgs[data[i].name])
								$("#txtSlctAuthType").val(data[i].name);
								isSet = true;
							}else if(seleteIndex==-1){
								seleteIndex=i;
							}
						}
					}
					if(!isSet){
						$("#selectedAuth").html(roleMsgs[data[seleteIndex].name])
						$("#txtSlctAuthType").val(data[seleteIndex].name);
					}
		
				}

			}
		}
	});
	return roles;
}
</script>
</body>
</html>
