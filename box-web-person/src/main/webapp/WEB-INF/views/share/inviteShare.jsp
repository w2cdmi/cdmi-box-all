<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<%@ include file="../common/include.jsp" %>
<link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/share/inviteShare.css"/>
</head>
<body>
<div class="putting-out" style="display: block">
	<div class="putting-background"></div>
	<div class="putting-papers">
		<div>
			<span><img src="${ctx}/static/skins/default/img/icon/file-doc.png"/></span>
			<i>${name}</i>
		</div>
	</div>
	<div class="putting-append">
		<i><img src="${ctx}/static/skins/default/img/putting-apped.png"/></i><span>添加成员</span>
	</div>
	<div class="fillBackground"></div>
	<div class="putting-sharedmembers">
		<span>当前已共享成员</span>
		<p><img src="${ctx}/static/skins/default/img/sharedmembers-delete.png"/>删除成员</p>
	</div>
	<div class="sharedmembers-content">
		<ul>
		</ul>
	</div>
</div>
<!--弹框-->
<div class="putting-moreshare">
	<div class="putting-background"></div>
	<div class="putting-jurisdiction">
		<ul>
		</ul>
	</div>
	<div class="putting-fill"></div>
</div>
<!--共享第二页-->
<div class="share-second">
	<div class="putting-background"></div>
	<div class="share-second-header">
		<span>共享成员（9）人</span>
	</div>
	<div class="share-second-kong"></div>
	<div class="share-second-content">
		<ul>
		</ul>
	</div>
	<div class="share-second-tail">
		<div>
			<ul>
			</ul>
		</div>
	</div>
</div>
<!--添加要共享的成员-->
<div class="add-leaguer">
	<div class="leaguer-header">
		<div class="leaguer-header-img"></div>
		<input type="text" placeholder="请输入你要搜索的人"/>
	</div>
	<div class="add-leaguer-nav">
		<ul>
			<!--<li>
				<i><img src="${ctx}/static/skins/default/img/add-leaguer-group.png"/></i>
				<span>企业通讯录</span>
				<p><img src="${ctx}/static/skins/default/img/putting-more.png"/></p>
			</li>
			<li>
				<i><img src="${ctx}/static/skins/default/img/add-leaguer-list.png"/></i>
				<span>我的群组</span>
				<p><img src="${ctx}/static/skins/default/img/putting-more.png"/></p>
			</li>-->
		</ul>
	</div>
	<div class="add-leaguer-tail">
		<ul>
			<li>
				<h1>最近联系的人</h1>
			</li>
			<!--<li>
				<i class="M-active"></i><p><img src="${ctx}/static/skins/default/img/putting-QQ.png"/></p><span>欧阳风</span>
			</li>
			<li>
				<i class="M-addblank"></i><p><img src="${ctx}/static/skins/default/img/putting-QQ.png"/></p><span>欧阳风</span>
			</li>
			<li>
				<i class="M-inactive"></i><p><img src="${ctx}/static/skins/default/img/putting-QQ.png"/></p><span>欧阳风</span>
			</li>-->
		</ul>
	</div>
</div>
<!--共享成员通讯录-->
<div class="share-address-list">
	<div class="address-list-header">
		<div class="address-list-img"></div>
		<input type="text" placeholder="请输入你要搜索的人"/>
	</div>
	<div class="share-address-content">
		<ul>
			<!--<li>
				<i class="M-active"></i>
				<p><img src="${ctx}/static/skins/default/img/putting-QQ.png"/></p>
				<span>欧阳风</span>
			</li>
			<li>
				<i class="M-addblank"></i>
				<p><img src="${ctx}/static/skins/default/img/putting-QQ.png"/></p>
				<span>部门</span>
				<h1><img src="${ctx}/static/skins/default/img/putting-more.png"/></h1>
			</li>
			<li>
				<i class="M-inactive"></i>
				<p><img src="${ctx}/static/skins/default/img/putting-QQ.png"/></p>
				<span>欧阳风</span>
			</li>-->
		</ul>
	</div>
</div>

</body>
<script type="text/javascript">
var submitUsername = null;
var tempUsername = null;
var allMessageTo = new Array();
var opts_viewGrid = null;
var treeUserHeadData = null;
var ownerId = "${ownerId}";
var iNodeId = "${folderId}";
var isShare = "${shareStatus}";
var objType = '${type}';
var token = '${token}';
var accountRoleData = null;
var treeUserData=null;
allMessageTo = [];
function searchMessageTo() {
    if($("#userNames").val().length <= 1){
		return;
	}
	
	availableTags = "";
    var params= {
	    "ownerId": "${ownerId}", 
	    "folderId": "${folderId}",
	    "userNames": $("#userNames").val(),
	    token:token
    };
	tempUsername = params.userNames;
    var list;
    console.debug(params);
	$.ajax({
        type: "POST",
        data: params,
        url:"${ctx}/share/listMultiUser",
        error: function(request) {
        	searchSpiner.stop(); 
			handlePrompt("error",'<spring:message code="inviteShare.listUserFail"/>','','5');
			$("#messageAddr").focus();
        },
        success: function(data) {
			availableTags = data.successList;
			unAvailableTags = data.failList;
			if(data.single && availableTags.length == 1){
				if(availableTags[0].userType == 1){
					addMessageTo(availableTags[0].cloudUserId, availableTags[0].loginName, availableTags[0].type, availableTags[0].label,null);
				}
				else{
					addMessageTo(availableTags[0].cloudUserId, availableTags[0].loginName, availableTags[0].type, availableTags[0].label,availableTags[0].email);
				}
				$("#userNames").val("");
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
				$("#userNames").val(unAvailableTags + "");
				userInputAutoSize("#userNames");
				if(unAvailableTags.length > 0){
					handlePrompt("error",'<spring:message code="inviteShare.error.partnoresult"/>','','5');
				}
				return;
			}
			
        }
    });
}
allMessageTo = [];
function addMessageTo(userCloudId, userLoginName,userType,userName, userEmail) {
    var loginName = "${loginUserName}";
    var subName = $("#userNames").val().trim();
	var itemValue = "["+userCloudId+"]"+userLoginName +"["+userEmail+"]";
	allMessageTo.push(itemValue);
	
}

function shareToOthers(){
	var shareToStr = getTrunckData(allMessageTo);
    //var shareToStr="00390[25user]wuwei(wuwei)[wuwei@storbox.cn]";
	var authType = "viewer";
	var params= {
	    "ownerId": "${ownerId}", 
	    "iNodeId": "${folderId}",
	    "shareToStr":shareToStr,
		"message":"",
		"authType" : authType,
		 token:token
    };
	isAddSharing = true;
	$.ajax({
		type: "POST",
        data: params,
        url:"${ctx}/share/addShare",
        error: function(request) {
        },
        success: function() {
			alert("共享成功");
        }
    });   
}
function split( val ) {
	return val.split( /,\s*/ );
}
function extractLast( term ) {
	return split( term ).pop();
}
function getTrunckData(dataArray){
	if(dataArray == null || dataArray == ""){
		return "";
	}
	var result = "";
	for ( var i = 0; i < dataArray.length; i++) {
		if(dataArray[i] != ""){
			result = result + pad(dataArray[i].length,4) + dataArray[i];
		}
	}
	return result;
}
/**
 * 数字字符串补0
 */ 
function pad(num, n) {  
    var len = num.toString().length;  
    while(len < n) {  
        num = "0" + num;  
        len++;  
    }  
    return num;  
}  



</script>
</html>
