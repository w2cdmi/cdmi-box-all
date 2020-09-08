<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml-strict.dtd">
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
<script src="${ctx}/static/js/public/JQbox-hw-page.js" type="text/javascript"></script>
<script src="${ctx}/static/clipboard/ZeroClipboard.js" type="text/javascript"></script>

</head>
<body>
<div class="pop-content">
    <span id="enterTempData" style="display:none; height:30px;"></span>
	<div class="pop-member-management">
		<div class="pop-member-header">
			<spring:message code="teamSpace.label.hasAdded" /><strong id="memberTotal"></strong>
			<spring:message code="teamSpace.label.members" /> </span>
          	<span id="shareUserString" style="display:none;"></span>
		</div>
		<div id="memberListCon" class="member-list">
			<div id="enCharSearch" class="en-char-search"></div>
            <div id="memberList"></div>
            <div id="memberListPageBox"></div>
		</div>
		<div class="add-collaborator-help"><spring:message code='group.member.help'/></div>
		<div class="user-form">
			<div class="department-con clearfix">
				<ul id="treeArea" class="ztree"></ul>
				<div class="treelist-user">
					<p class="clearfix"><button type="button" class="btn btn-mini pull-right" onclick="addNodeAllUser()"><spring:message code='group.member.addAll'/></button><strong id="strongOrgId"></strong><span id="spanCount"></span></p>
					<div id="treeUserList"></div>
				</div>
			</div>
			<div id="inviteMember" class="search-user-con" >
				<textarea maxlength=2000 id="messageAddr"></textarea>
				<div class="prompt"><spring:message code='teamSpace.button.btnAddMember'/></div>
				<div class="enterPrompt"><spring:message code="link.set.addMailInfo" /></div>
			</div>
	    </div>
	    <div class="search-loading"><div id="loadingDiv" class="loading-div"></div></div>
	    <div id="memberTypeCon" class="member-type dropup row-fluid">
			<a class="btn dropdown-toggle span12" data-toggle="dropdown"><span class="caret"></span><strong id="selectedAuth"><spring:message code='group.user'/></strong></a>
			<ul class="dropdown-menu">
				<c:if test="${member.groupRole == 'admin'}">
				 <li><a onclick="selectSomeOne(this,'manager');" title='<spring:message code="group.manager"/>'><spring:message code='group.manager'/></a></li>
			     </c:if>
			    <li><a onclick="selectSomeOne(this,'member');" title='<spring:message code='group.user'/>'><spring:message code='group.user'/></a></li>
			</ul>
			<input type="hidden" id="txtSlctAuthType" value="member" />
	    </div>
	    <div id="inviteBtnCon" class="btn-con" >
	    	<button id="submitBtn" type="button" class="btn btn-primary" onclick="submitMember()"><spring:message code="group.button.btnAdd" /></button>
	    	<button id="" type="button" class="btn" onclick="cancelInvite()"><spring:message code="button.cancel" /></button>
	   	</div>
	   	<div id="manageBtnCon" class="btn-con" >
	    	<button id="" type="button" class="btn btn-primary" onclick="closeYmPrompt()"><spring:message code="button.close"/></button>
	   	</div>
	</div>
</div>

	<script type="text/javascript">
		var submitUsername = null;
		var tempUsername = null;
		var currentPage = 1;
		var catalogData = null;
		var opts_treeUserList = null;
		var opts_viewGrid = null;
		var opts_page = null;
		var treeUserData=null;
		var perPageNum = 6;
		var headData = {
				"username" : {"width" : "30%"},
				"description" : {"width" : ""},
				"groupRole" :{"width" : "20%"},
				"handler" :{"width" : "7%"}
				};
		var treeUserHeadData = {
				"name": {"title" : '<spring:message code="inviteShare.sharedUserName"/>', "width": ""},
				"flag": {"title" : '', "width": "20px"}
				};
	
		var allMessageTo = new Array();
		var allUserNameTo = new Array();
		var myKeyWord = "";
		
		$(function() {
			$("#memberTypeCon,#inviteBtnCon").hide();
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
						top.ymPrompt.resizeWin(720,conH);
					}
				}else if(event.keyCode !=38 && event.keyCode !=40 ){
					availableTags = [];
				}
				$(".enterPrompt").hide();
			})
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
			
			$("#memberTypeCon > a, #memberTypeCon ul li > a").tooltip({ container:"body", placement:"top", delay: { show: 100, hide: 0 }, animation: false });
			
			enCharSearch();
			init();
			initDataList(currentPage);
		});

		function userInputAutoSize(that){
			var tempObj = $("#enterTempData"),
				_obj = $(that).parent().find("div.invite-member:last"),
				posCon = $("#inviteMember").offset().left +5,
				posInput = _obj.get(0) ? (_obj.offset().left + _obj.outerWidth() + 5) : posCon,
				userConW = 535,
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
			top.ymPrompt.resizeWin(720,conH);
		}
		
		function enCharSearch(keyword){
			$("#enCharSearch").html("");
			if(keyword == "undefined" || keyword == null) keyword = "";
			if(keyword == ""){
				$("#enCharSearch").append('<a class="active" href="javascript:void(0)" onclick="searchBy(this);"><spring:message code="teamSpace.label.showAll"/></a>');
			}else{
				$("#enCharSearch").append('<a href="javascript:void(0)" onclick="searchBy(this);"><spring:message code="teamSpace.label.showAll"/></a>');
			}
            
            for(var i=0;i<26;i++){  
                     var s = String.fromCharCode(65+i);
                     if(s == keyword){
                    	 $("#enCharSearch").append('<a class="active" href="javascript:void(0)" onclick="searchBy(this,\''+ s +'\');">'+ s +'</a>');
                     }else{
                    	 $("#enCharSearch").append('<a href="javascript:void(0)" onclick="searchBy(this,\''+ s +'\');">'+ s +'</a>');
                     }
            }
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
				addMessageTo(treeUserData[i].name, treeUserData[i].cloudUserId, treeUserData[i].loginName, treeUserData[i].email,null);
				var tdItem=$("#"+treeUserData[i].cloudUserId+"_td");
				tdItem.next().find("p").append('<i class="icon-selected"></i> ');
				tdItem.find("p").css("color","#999").find("i").addClass("icon-gray");
			}
		}

		function init() {
			var operGroupRole = "${member.groupRole}";
			if(operGroupRole == "admin"){
				$("#inviteMember").show();
				$("#inviteBtnCon, #memberTypeCon").hide();
				top.$("#ym-window .ym-header-text").text("<spring:message code='member.manage.info' />");
			}else if(operGroupRole == "manager"){
				$("#inviteMember").show();
				$("#inviteBtnCon, #memberTypeCon").hide();
			}else{
				$("#inviteMember, #inviteBtnCon, #memberTypeCon").remove();
				top.$("#ym-window .ym-header-text").text("<spring:message code='member.manage.lookinfo' />");
			} 
			initTree();
			opts_viewGrid = $("#memberList").comboTableGrid({
				headData : headData,
				border:false,
				hideHeader: true,
				splitRow : false,
				miniPadding : true,
				stripe : true,
				dataId : "id",
				ItemOp : "user-defined",
				height : 220
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
									addMessageTo(rowData.name, rowData.cloudUserId, rowData.loginName, rowData.email,null);
									tdItem.next().find("p").append('<i class="icon-selected"></i> ');
									tdItem.find("p").css("color","#999").find("i").addClass("icon-gray");
								}else{
									var itemValue = "["+rowData.type+"]" + rowData.loginName+ "["+rowData.cloudUserId+"]" + rowData.email;
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
					case "username":
						try {
							var alink, groupRole = rowData.groupRole,userType = rowData.userType;
							if(groupRole == "admin"){
								alink = "<i class='icon-ownner icon-orange'></i>";
							}else if(groupRole == "manager"){
								alink = "<i class='icon-user icon-orange'></i>";
							}else{
								alink = "<i class='icon-user'></i>";
							}						
							tdItem.find("p").prepend(alink);
						} catch (e) {}
						break;
					case "groupRole":
						try {
							var alink, userRole = rowData.role, roleText, groupId = rowData.groupId, userId = rowData.userId, groupRole = rowData.groupRole,userType= rowData.userType;
							if(groupRole == "admin"){
								roleText = '<spring:message code="group.title.admin"/>';
							}else if(groupRole == "manager"){
								roleText = '<spring:message code="group.title.manager"/>';
							}else if(groupRole == "member"){
								roleText = '<spring:message code="group.title.user"/>';
							} 
							
							if(operGroupRole == "admin" && groupRole != "admin"){
								alink = "<span class=\"dropdown\">"+
				       			"<a href=\"javascript:void(0)\" onclick=\"dropGroupRole(this,"+ groupId +", "+ userId +", '"+ operGroupRole + "', '" + userType + "','"+groupRole+"')\" class=\"dropdown-toggle\" data-toggle=\"dropdown\">"+ roleText +"<i class=\"icon-caret-down icon-gray\"></i></a>"+
				       			"<ul class=\"dropdown-menu pull-right\"></ul>"+
				       			"</span>";

							} else{
								alink = roleText;
							}
							tdItem.find("p").html('').css("overflow","visible").append(alink);
							tdItem.attr("title", "");
						} catch (e) {}
				    	break;
					case "handler":
						try {
							var alink, groupId = rowData.groupId, userId = rowData.userId, groupRole = rowData.groupRole;
							if((operGroupRole == "admin" && groupRole != "admin") || (operGroupRole == "manager" && groupRole == "member")){
								alink = "<a onclick=\"deleteMember("+ groupId +","+ userId +")\"><i class=\"icon-delete-alt icon-gray\"></i></a>";
							}
							tdItem.find("p").append(alink);
						} catch (e) {}
				    	break;
					default : 
						break;
				}
			};
			
			opts_page = $("#memberListPageBox").comboPage({ style : "page table-page", pageSkip : false, lang : '<spring:message code="common.language1"/>'});
			$.fn.comboPage.pageSkip = function(opts, _idMap, curPage){
				initDataList(curPage);
			};
			
			$("#inviteMember").click(function(){
				$("#messageAddr").focus();
			});
			$("#messageAddr").focus(function(){
				$(".prompt").hide();
				$("#manageBtnCon, .pop-member-header, #memberListCon").hide();
				<c:if test="${member.groupRole == 'admin'}">
				$("#memberTypeCon,#inviteBtnCon,.add-collaborator-help, .department-con").show();
				</c:if>
				<c:if test="${member.groupRole == 'manager'}">
				$("#memberTypeCon,#inviteBtnCon,.add-collaborator-help, .department-con").show();
				</c:if>
				
				if($(this).val() =='' && allMessageTo.length < 1 && allUserNameTo.length < 1){
					$(".enterPrompt").show();
				}
				
				top.$("#ym-window .ym-header-text").text("<spring:message code='member.manage.addinfo' />");
				
				var conH = parseInt($(".pop-content").outerHeight()+90);
				top.ymPrompt.resizeWin(720,conH);
			}).blur(function(){
				if($(this).val() =='' && allMessageTo.length < 1 && allUserNameTo.length < 1){
					$(".prompt").show();
					$(".enterPrompt").hide();
				}
			})
			
			$("#messageAddr").autocomplete({
				position: { my : "left top", at: "left bottom", of: "#inviteMember"},
				source: function( request, response ) {}
	 		})
		}

		function initDataList(curPage){
			 var url = "${ctx}/group/memberships/items/" + "${groupId}";
			 var params= {
				    "page": curPage, 
				    "limit" : perPageNum,
				    "keyword" : myKeyWord
			 };
			 $.ajax({
			        type: "POST",
			        url: url,
			        data: params,
			        error: function(request) {
			        	handlePrompt("error",'<spring:message code="inviteShare.listUserFail"/>','','5');
			        },
			        success: function(data) {
			        	catalogData = data.data;
			        	
						$("#memberTotal").text(data.totalNums);
						$("#memberList").setTableGridData(catalogData, opts_viewGrid);
						$("#memberListPageBox").setPageData(opts_page, data.page, data.numOfPage, data.totalNums);
						var conH = parseInt($(".pop-content").outerHeight()+90);
						top.ymPrompt.resizeWin(720,conH);
		            } 
		      });
		}

		function selectSomeOne(that,val){
			$("#selectedAuth").html($(that).html());
			$("#txtSlctAuthType").val(val);
		}
		
		function closeYmPrompt(){
			top.ymPrompt.close();
			refreshWindow();
		}
		
		function dropGroupRole(that,groupId,userId,operGroupRole, userType, groupRole){
			var dropSlct, popDiv = "";
			if (operGroupRole == "admin" && userType=="user" && groupRole=="member") {
				dropSlct = [{value:"admin",name:"<spring:message code='group.title.admin'/>"},{value:"manager",name:"<spring:message code='group.title.manager'/>"}];
			} 
			if (operGroupRole == "admin" && userType=="user" && groupRole=="manager") {
				dropSlct = [{value:"admin",name:"<spring:message code='group.title.admin'/>"},{value:"member",name:"<spring:message code='group.title.user'/>"}];
			}
			if (operGroupRole == "manager" && userType=="user") {
				dropSlct = [{value:"manager",name:"<spring:message code='group.title.manager'/>"}];
			}
			for (var i = 0;i<dropSlct.length;i++) {
				popDiv += "<li><a href='javascript:void(0)' onclick='updateGroupRole("+groupId+",\""+groupRole+"\","+userId+",\""+dropSlct[i].value+"\",this)'>"+dropSlct[i].name+"</a></li>";
			}
			$(that).next().html("").append($(popDiv));
			
			var _height = $(that).next().outerHeight();
			var _eachHeight = $(that).parents("tr").outerHeight();
			var trId = $(that).parents("tr").attr("id").substring(20);
			if(trId > parseInt(_height/_eachHeight)){
				$(that).parent().addClass("dropup");
			}
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
					handlePrompt("error",'<spring:message code="link.set.listUserFail"/>','','5');
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
						addMessageTo(availableTags[0].name,availableTags[0].cloudUserId,availableTags[0].loginName,availableTags[0].email);
						$("#messageAddr").val("");
						return;
					}
					if(!data.single && availableTags.length > 0){
						$(availableTags).each(function(n,item){
							addMessageTo(item.name,item.cloudUserId,item.loginName,item.email);
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
							position: { my : "left top", at: "left bottom", of: "#inviteMember"},
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
								addMessageTo(ui.item.name,ui.item.cloudUserId, ui.item.loginName,ui.item.email);
								return false;
							}	
				 		}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
								return $("<li>").append("<a><strong>" + item.label+ "</strong> (" + item.email + ") " + "<br>" + item.department + "</a>").appendTo(ul);
						};
				        
						$("#messageAddr").autocomplete("enable");
						$("#messageAddr").autocomplete("search", $("#messageAddr").val());
					}
		        }
		    });
		}
		function addAllUser(){
			addMessageTo(null,null,null,null, "system");
		}
		
		function addMessageTo(userName,userID,userLoginName,userEmail, userType) {
			$(".prompt").hide();
			if(userType == undefined || userType == null){
				userType = "user";
			}
			var button = $("<a class='close' title=" + '<spring:message code="button.delete"/>' + ">&times;</a>");
			var text = $("<div>" + userName + "</div>");
			if(userType == "system"){
				text = $("<div>" + '<spring:message code="teamspace.user.system"/>' + "</div>");
			}
			var itemName = "["+userType+"]" + userLoginName+ "["+userID+"]" + userEmail;
			if($.inArray(itemName, allMessageTo) != -1){
				return;
			}
			var dd = $('<div class="invite-member" id='+ userID +'></div>');
			button.click(function() {
						$(this).parent().remove();
						var tempArray = new Array();
						var length = allMessageTo.length;
						for ( var i = 0; i < length; i++) {
							var temp = allMessageTo.pop();
							if (temp != itemName) {
								tempArray.push(temp);
							} else {
								break;
							}
						}
						allMessageTo = allMessageTo.concat(tempArray);

						var obj = $("#treeUserList").find("#" + userID + "_td");
						obj.next().find("i").remove();
						obj.find("p").removeAttr("style").find("i").removeClass("icon-gray");
						
						var conH = parseInt($(".pop-content").outerHeight() + 90);
						top.ymPrompt.resizeWin(720, conH);

						if ($("#messageAddr").val() == ''
								&& allMessageTo.length < 1 && allUserNameTo.length < 1) {
							$(".prompt").show();
						}
						window.event.cancelBubble = true;
						window.event.returnValue = false;
					});
			dd.append(text).append(button);
			$("#messageAddr").before(dd);
			allMessageTo.push(itemName);
			$("#messageAddr").focus();
			userInputAutoSize("#messageAddr");
			var conH = parseInt($(".pop-content").outerHeight() + 90);
			top.ymPrompt.resizeWin(720, conH);
		}

		function cancelInvite(){ 
			$("#memberTypeCon, #inviteBtnCon,.enterPrompt,.add-collaborator-help, .department-con").hide();
			$("#manageBtnCon, .prompt,.pop-member-header, #memberListCon").show();
			$("#messageAddr").val("").blur().removeAttr("style");
			$("#inviteMember").find(".invite-member").remove();
			allMessageTo = [];
			var conH = parseInt($(".pop-content").outerHeight()+90);
			top.ymPrompt.resizeWin(720,conH);
			
			top.$("#ym-window .ym-header-text").text("<spring:message code='member.manage.info' />");
		}
		
		function submitMember() {
			
			if (allMessageTo.length==0) {
				handlePrompt("error","<spring:message code='teamSpace.error.emptyEmail'/>");
				$("#messageAddr").focus();
				return false;
			}
			$("#messageAddr").val("");
			var messageAddr = getTrunckData(allMessageTo);
			var groupRole = $("#txtSlctAuthType").val();
			var url = "${ctx}/group/memberships/addMember?" + Math.random();
			
			if(groupRole == "none"){
				handlePrompt("error","<spring:message code='group.error.choose'/>");
				$("#messageAddr").focus();
				return false;
			}
			var data = {
				trunkUsersInfo : messageAddr,
				groupRole : groupRole,
				groupId : "${groupId}"
			};
			
			top.inLayerLoading("<spring:message code='common.task.doing'/>","loading-bar");
			
			$.ajax({
				type : "POST",
				url : url,
				data : data,
				error : function(data) {
					top.unLayerLoading();
					var prom = data.responseText;
					if ("PART-OK" == prom) {
						handlePrompt("error","<spring:message code='operation.partOk'/>");
						initDataList(currentPage);
						cancelInvite();
					}else {
						handlePrompt("error","<spring:message code='operation.failed'/>");
					}
				},
				success : function(data) {
					top.unLayerLoading();
					handlePrompt("success","<spring:message code='operation.success'/>");
					initDataList(currentPage);
					cancelInvite();
				}
			});
		}

		function deleteMember(groupId,userId) {
			var url = "${ctx}/group/memberships/deleteMember?" + Math.random() ;
			var data = {
				groupId : groupId,
				userId : userId
			};
			$.ajax({
				type : "GET",
				url : url,
				data : data,
				async:false,
				error : function(request) {
					handlePrompt("error", "<spring:message code='operation.failed'/>");
				},
				success : function(data) {
					if(data == "Delete Itself"){
						top.ymPrompt.close();
						top.ymPrompt.initDataList(1);
						refreshWindow();
					}else{
						handlePrompt("success","<spring:message code='operation.success'/>");
			        	initDataList(currentPage);
					}
				}
			});
		}
	    
		function refreshiFrame() {
	    	top.window.frames[1].location = "${ctx}/group/memberships/openGroupMemberMgr/" + "${groupId}";
			}
	    
		function refreshWindow() {
			top.window.frames[0].refreshCurPage();
			}
		
		function updateGroupRole(groupId, originalGroupRole, userId, destGroupRole, that) {
			var url = "${ctx}/group/memberships/updateGroupRole";
			var data = {
				loggerGroupRole : "${member.groupRole}",
				originalGroupRole : originalGroupRole,
				groupId : groupId,
				userId : userId,
				destGroupRole : destGroupRole
			};
			$.ajax({
				type : "GET",
				url : url,
				data : data,
				async:false,
				error : function(request) {
					handlePrompt("error","<spring:message code='operation.failed'/>");
				},
				success : function(data) {
					$(that).parent().parent().prev().html($(that).html());
					if(data=="Up2Manager"){
						ymPrompt.confirmInfo({
							title : '<spring:message code="group.title.grade"/>',
							message : '<spring:message code="group.title.grade.tips"/>',
							width : 450,
							closeTxt : '<spring:message code="button.close"/>',
							handler : function(tp) {
								if (tp == "ok") {
									executeUpdateGroupRole(groupId, userId, destGroupRole);
								}
								else{
									initDataList(currentPage);
								}
							},
							btn : [ [ '<spring:message code="common.OK"/>', "ok" ],
									[ '<spring:message code="common.cancle"/>', "cancel" ] ]
						});
					}else if(data=="Up2Admin"){
						ymPrompt.confirmInfo({
							title : '<spring:message code="group.title.grade"/>',
							message : '<spring:message code="group.title.grade.change.tips"/>',
							width : 450,
							closeTxt : '<spring:message code="button.close"/>',
							handler : function(tp) {
								if (tp == "ok") {
									executeUpdateGroupRole(groupId, userId, destGroupRole);
								}
								else{
									initDataList(currentPage);
								}
							},
							btn : [ [ '<spring:message code="common.OK"/>', "ok" ],
									[ '<spring:message code="common.cancle"/>', "cancel" ] ]
						});
					}else if(data=="OK"){
						executeUpdateGroupRole(groupId, userId, destGroupRole);
					}
				}
			});
		}
		
		
		function executeUpdateGroupRole(groupId, userId, destGroupRole) {
			var data = {
					groupId : groupId,
					userId : userId,
					destGroupRole : destGroupRole
				};
			$.ajax({
				type : "POST",
				url : "${ctx}/group/memberships/executeUpdateGroupRole",
				data : data,
				error : function(request) {
					_statusText = request.statusText;
					if (_statusText == "Unauthorized") {
						handlePrompt("error", "<spring:message code='operation.failed'/>");
					} else if (_statusText == "NoSuchGroup") {
						handlePrompt("error", "<spring:message code='group.error.group.noexist'/>");
					} else if (_statusText == "Forbidden") {
						handlePrompt("error", "<spring:message code='group.error.forbidden'/>");
					} else {
						handlePrompt("error", "<spring:message code='operation.failed'/>");
					}
					top.ymPrompt.close();
				},
				success : function() {
					handlePrompt("success", "<spring:message code='operation.success'/>");
					initDataList(currentPage);
				}
			});
		}
		
		
		function searchBy(that, keyword) {
			if($(that).hasClass("active")){
				return;
			}
			myKeyWord = keyword;
			if(keyword == "undefined" || keyword == null) myKeyWord = "";
			$(that).parent().find(".active").removeClass("active").end().end().addClass("active");
			var url = "${ctx}/group/memberships/items/" + "${groupId}";
			 var params= {
				    "page": 1, 
				    "limit" : perPageNum,
				    "keyword" : myKeyWord
			 };
			 $.ajax({
			        type: "POST",
			        url: url,
			        data: params,
			        error: function(request) {
			        	handlePrompt("error",'<spring:message code="inviteShare.listSharedUserFail"/>','','5');
			        },
			        success: function(data) {
			        	catalogData = data.data;
			        	$("#memberTotal").text(data.totalElements);
						$("#memberList").setTableGridData(catalogData, opts_viewGrid);
						$("#memberListPageBox").setPageData(opts_page, data.page, data.numOfPage, data.totalNums);
			        }
			    });
			
		}
		
		
		function refreshTopTeamList(){
			if($.isFunction(top.listTeam)){
				var teamListPage = getCookie("teamListPage");
				teamListPage = teamListPage == null ? 1 : teamListPage;
				top.listTeam(teamListPage);
			}
		}
		
		 function sendMemberMail(groupId, userId)
		    {
		     var url = "${ctx}/group/memberships/sendMemberMail";
		     var data = {
		     groupId : groupId,
		     userId : userId
		     };
		     $.ajax({
		     type : "POST",
		     url : url,
		     data : data
		     }); 
		    }
		
	</script>
</body>
</html>
