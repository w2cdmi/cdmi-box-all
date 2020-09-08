<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<style type="text/css">
.checkSpan{
   background-color:#18b4ed
}

</style>
<script type="text/javascript">
var submitUsername = null;
var tempUsername = null;
var shareAllMessageTo = new Array();
var opts_viewGrid = null;
var treeUserHeadData = null;

var shareINodeId = "";
var shareINodeOwnerId = "";
var shareINodeType = '';
var token = '${token}';
var accountRoleData = null;
var treeUserData=null;
shareshareAllMessageTo = [];


function showShareDialog(node){
	shareINodeId=node.id;
	shareINodeOwnerId=node.createdBy;
	shareINodeType=node.type;
	if(shareINodeType==0){
		 $("#nodeIcon").attr("src", "${ctx}/static/skins/default/img/icon/folder-icon.png"); 
	}else{
		 $("#nodeIcon").attr("src", "${ctx}/static/skins/default/img/icon/file-doc.png"); 
	}
	$("#nodeName").text(node.name);
	$('.share-bombbox').css('display','block');
/* 	$('.share-cancel').click(function(){
		$('.share-bombbox').css('display','none');
	}) */
	$('.share-top-brandnew span').click(function(){
	if($(this).css('background-color')==''){
		$(this).css('background-color','#18b4ed');
	}else{
		$(this).css('background-color','none');
	}
	})    
	/* $('.share-bombbox').click(function(){
			
		$('.share-bombbox').css('display','none');
			$('.share-middle').click(function(e){ 
				e.stopPropagation();
			    $('.share-bombbox').css('display','block');
			}); 	
	}) */
	$('.share-input').focus(function(){
			$(this).prop("placeholder", "");
			$('.share-inputs div').css('display',"none");
	})
	$('.share-input').blur(function(){
		$(this).prop("placeholder", "请输入要分享的人");
		$('.share-inputs div').css('display',"block");
	}) 
 }

function searchMessageTo() {
    if($("#userNames").val().length <= 1){
         $.toast("检查人员", "cancel", function(toast) {
    	          console.log(toast);
    	        });
		return;
	}
	availableTags = "";
    var params= {
	    "ownerId": shareINodeOwnerId, 
	    "folderId": shareINodeId,
	    "userNames": $("#userNames").val(),
	    token:token
    };
	tempUsername = params.userNames;
    var list;
	$.ajax({
        type: "POST",
        data: params,
        url:"${ctx}/share/listMultiUser",
        error: function(xhr, status, error){
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

function addMessageTo(userCloudId, userLoginName,userType,userName, userEmail) {
    var subName = $("#userNames").val().trim();
	var itemValue = "["+userCloudId+"]"+userLoginName +"["+userEmail+"]";
	shareAllMessageTo.push(itemValue);
	shareToOthers();
	
}

function shareToOthers(){
	var shareToStr = getTrunckData(shareAllMessageTo);
    //var shareToStr="00390[25user]wuwei(wuwei)[wuwei@storbox.cn]";
	var authType = getRole();
	if(authType==""){
		  $.toast("选择权限", function() {
	          console.log('close');
	        });
		  return;
	}
	var params= {
	    "ownerId": shareINodeOwnerId, 
	    "iNodeId": shareINodeId,
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
        error: function(xhr, status, error){
        },
        success: function() {
        	  $.toast("共享成功", function() {
                  console.log('close');
                });
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

    
function cancelShare(){
	$('#shareDialog').css('display','none');
	$("#userNames").val("")
	$("[name='checkSpan']").each(function(){
		$(this).removeClass("checkSpan");
	}) 
}   
function confirmShare(){
	searchMessageTo();
	console.debug(shareshareAllMessageTo);
}

function checkSpan(th){
	if($(th).attr("class")!=undefined&&$(th).attr("class")=="checkSpan"){
		$(th).removeClass("checkSpan");
	}else{
		$(th).addClass("checkSpan");
	};
	$("[name='checkSpan']").each(function(){
		if($(th).attr("id")!=$(this).attr("id")){
			$(this).removeClass("checkSpan");
		}
	}) 
}


function getRole(){
	var role="";
	$("[name='checkSpan']").each(function(){
		if($(this).attr("class")!=undefined&&$(this).attr("class")=="checkSpan"){
			role=$(this).attr("roleName");
		}
	})
	return role;
}

</script>
</head>
<body>
<div class="share-bombbox" id="shareDialog">
<div class="share-middle">
		<div class="share-top">
			<div class="share-top-png">
				<img src="" id="nodeIcon"/>
				<span id="nodeName"></span>
			</div>
			<div class="share-inputs">
				<input class="share-input" type="text" placeholder="请输入要分享的人" id="userNames"/>
				<div></div>
			</div>
			<div class="share-top-brandnew">
				<ul>
					<li>
						权限
					</li>
					<li >
						<span onclick="checkSpan(this)" name="checkSpan" id="editorSpan" roleName="ditor"></span >编辑
					</li>
					<li>
						<span onclick="checkSpan(this)" name="checkSpan" id="viewerSpan" roleName="viewer"></span >查看
					</li>
					<li>
						<span onclick="checkSpan(this)" name="checkSpan" id="previewerSpan" roleName="previewer"></span>预览
					</li>
				</ul>
			</div>
		</div>
		<div class="share-ottom">
			<div class="share-cancel"  onclick="cancelShare()">取消</div>
			<div class="share-confirm"  onclick="confirmShare()">确定</div>
		</div>
	</div>
	
</div>
</body>

</html>