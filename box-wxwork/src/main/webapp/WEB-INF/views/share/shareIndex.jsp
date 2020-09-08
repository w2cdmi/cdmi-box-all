<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<%@ include file="../common/include.jsp" %>
	<link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/index.css"/>
<link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/share/inviteShare.css"/>
<script src="${ctx}/static/js/common/line-scroll-animate.js"></script>
<title>共享</title>
</head>
<body style="overflow: hidden;">
	<div class="putting-out">
	<div class="putting-background"></div>
	<div class="weui-cell weui-cell_swiped">
		<div class="weui-cell__bd">
			<div class="weui-cell weui-cell-change">
				<div class="weui-cell__bd" >
					<div class="index-recent-left">
						<div id="fileImg"></div>
					</div>
					<div class="index-recent-middle">
						<div class="recent-detail-name">
							<p>${name}</p>
						</div>
						<div class="recent-detail-other">
							<span id="fileSize"></span>
							<span>|</span>
							<span id="fileModifiedAt"></span>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="putting-append" onclick="gotoAddUser()">
		<i><img src="${ctx}/static/skins/default/img/putting-apped.png"/></i><span >添加成员</span>
	</div>
	<div class="fillBackground"></div>
	<div class="putting-sharedmembers">
		<span>已共享成员</span>
		<p onclick="showDeleteSpan()"><span id="deleteMemberBtn" style="display: none;">删除成员</span><i style="display: none;"><img src="${ctx}/static/skins/default/img/sharedmembers-delete.png"/></i></p>
	</div>
	<div class="sharedmembers-content">
		<div class="putting-blank-background" style="display: block;">
			<div class="putting-blank-backgrounds">暂无共享成员</div>
		</div>
		 <ul id="sharedDiv">
		</ul>
	</div>
    <div class="addshare-member-tail" style="display: none" id="deleteMemberDiv">
    	<div class="member-tail-buttons" onclick="deleteSharedUsers()">
				<div class="member-tail-button">确定</div>
		</div>
		<ul>
			<div class="deleteMemberDiv-boy">
				
			</div>
		</ul>
	</div>
</div>

<div class="share-second" style="display: none" id="addshare4">
	<div class="share-second-tail">
		<div id="share-second-tail-content">
			<ul>
				<li>
					<span class="share-tail-span">设置权限</span>
				</li>
				<li>
					<i class="M-inactive" id="preview1"></i><p>预览</p>
				</li>
				<li>
					<i class="M-addblank" id="download1"></i><p>下载</p>
				</li>
				<li>
					<i class="M-addblank" id="uploadSpan"></i><p>上传</p>
				</li>
			</ul>
		</div>
	</div>
	<input type="hidden" id="updateUserPermission" />
	<a href="javascript:;" class="shaerDetermine shaerDetermines" onclick="submitMenber1()">确定</a>
	<a href="javascript:;" class="shareCancel shaerDetermines" onclick="cancelUpdate()">取消</a>
</div>

<!--企业通讯录-->
<div class="share-second" style="display: none" id="addshare1">
	<div class="putting-background"></div>
	<div class="share-second-header">
		<span id="totalMember">共享成员</span>
	</div>
	<div class="share-second-kong"></div>
	<div class="share-second-content" >
		<ul id="shareContent">
		
			<li>
				<div class="share-span1" onclick="showShareDiv('addshare2')"></div>
			</li>
		</ul>
	</div>
	<div class="share-second-tail">
		<div>
			<ul>
				<li>
					<span class="share-tail-span">设置权限</span>
				</li>
				<li>
					<span><i class="M-inactive" id="preview"></i><p>预览</p></span>
				</li>
				<li onclick="roleSelect(this)">
					<span><i class="M-active" id="download"  ></i><p>下载</p></span>
				</li>
				<li onclick="roleSelect(this)">
					<span><i class="M-addblank" id="upload1" ></i><p>上传</p></span>
				</li>
			</ul>
		</div>
	</div>
	<a href="javascript:;" class="shaerDetermine shaerDetermines" onclick="submitMenber()">确定</a>
	<a href="javascript:;" class="shareCancel shaerDetermines" onclick="cancelAddMenber()">取消</a>
</div>



<!--添加要共享的成员-->
<div class="add-leaguer" style="display: none;top:0;bottom:0" id="addshare2">
	<div class="add-leaguer-nav">
		<ul>
			<li onclick="openEnterpriseDirectory()">
				<i><img src="${ctx}/static/skins/default/img/add-leaguer-group.png"/></i>
				<span>企业通讯录</span>
				<p><img src="${ctx}/static/skins/default/img/putting-more2.png"/></p>
			</li>
		</ul>
	</div>
 	<div class="add-leaguer-tail">
		<ul>
			<%--<li>--%>
				<%--<h1>最近联系的人</h1>--%>
			<%--</li>--%>
			<%-- <li>
				<i class="M-active"></i><p><img src="${ctx}/static/skins/default/img/putting-QQ.png"/></p><span>欧阳风</span>
			</li> --%>
		</ul>
	</div> 
</div>
<!--共享成员通讯录-->
	<div class="share-address-list" style="display: none;position:relative;height:100%" id="addshare3">
		<div class="share-address-content" style="position: absolute;top: 0;bottom: 0;overflow-y: auto;background: #f5f5f5">
			<input type="hidden" id="parentDeptId" value="0"/>
			<div class="return-father" onclick="historyBack()" style="background: #fff">
				<div class="historyBack-return">返回</div>
				<b>|</b>
				<span id="department"></span>
			</div>
			<ul id="shareList" style="background: #fff"></ul>
		</div>

		<!-- 	<a href="javascript:;" class="weui-btn weui-btn_primary" onclick="comfireMenber()">确定</a> -->
	<div class="addshare-member-tail" style="display: none" id="preAddMemberDiv">
		<ul>
			<div class="member-tail-buttons" onclick="comfireMenber()">
				<div class="member-tail-button">确定</div>
			</div>
			<div id="preAddMember">
				
			</div>
		</ul>
	</div>
</div>



</body>
<script type="text/javascript">
var submitUsername = null;
var tempUsername = null;
var opts_viewGrid = null;
var treeUserHeadData = null;
var ownerId = "${ownerId}";
var iNodeId = "${folderId}";
var isShare = "${shareStatus}";
var objType = '${type}';
var token = '${token}';
var name = '${name}';
var size = '${size}';
var modifiedAt = '${modifiedAt}';

var accountRoleData = null;
var treeUserData=null;

var shareUser = null;	//共享用户   设置共享用户权限

$(function(){
	$("#fileImg").addClass(getImgHtmlOther(objType,name,isShare));
	if(size == ""){
		$("#fileSize").html("");
		$("#fileSize").next("span").remove();
	}else{
		$("#fileSize").html(formatFileSize(size));
	}
	$("#fileModifiedAt").html(getFormatDate(new Date(modifiedAt)));
	listSharedUser();
	if(objType==1){
        $('#upload1').parent().parent().css("display","none");	//共享文件时，添加成员不显示上传权限
        $('#uploadSpan').parent().css("display","none");		//共享文件时，修改成员不显示上传权限
	 }
})

function gotoAddUser(){
	$('.putting-out').hide();
	$('#addshare2').show()
};



function listSharedUser(){
	$("#sharedDiv").empty();
	$.ajax({
        type: "GET",
        url: host + '/ufm/api/v2/shareships/' + ownerId + '/' + ${folderId} + '?offset=0&limit=1000',
        error: function(xhr, status, error){
        	$.toast("获取共享用户失败");
        },
        success: function(data) {
          var users=data.contents;
          fullSharedMenber(data.contents);   //请求成功后长按删除
          fullShareDiv1(users);
          if(users.length>0){
          	$('.putting-blank-background').css('display','none');
          }else{
          	$('.putting-blank-background').css('display','block');
          }
          
        },complete:function(){
        	sharedmembersContentText();
        }
    })
}

function fullShareDiv1(users) {
    for (var i = 0; i < users.length; i++) {
        var html = "";
         html += "<li id='shareUser_" + users[i].sharedUserId + "'>";
         html += "   <div class=\"content\">";
         html += "     <h2 class=\"M-addblank\" onclick=\"userSelect(this)\" style=\"display:none\" name='deleteSpan' type='" + users[i].sharedUserType + "' value='" + users[i].sharedUserId + "' id='ww" + users[i].sharedUserId + "'></h2>";
        if (users[i].sharedUserType == "0") {
             html += "     <i><img src=\"${ctx}/userimage/getUserImage/" + users[i].sharedUserId + "\"/></i>";
        } else if (users[i].sharedUserType == "2") {
             html += "     <i><img src=\"${ctx}/static/skins/default/img/department-icon.png\"/></i>";
        }
         html += "	    <h1>" + users[i].sharedUserName + "</h1>";
         html += "	    <div class='content-two' onclick='memberInformationPreview(this)'>";
         html += "		<span>权限：</span>";

        if (users[i].roleName == "previewer") {
             html += "		<p> 预览</p>";
        } else if (users[i].roleName == "uploadAndView") {
             html += "		<p> 预览 上传 下载</p>";
        } else if (users[i].roleName == "downLoader") {
             html += "		<p> 预览  下载</p>";
        } else if (users[i].roleName == "viewer") {
             html += "		<p> 预览  下载</p>";
        } else if (users[i].roleName == "uploader") {
             html += "		<p> 预览  上传</p>";
        }
         html += "     <img src=\"${ctx}/static/skins/default/img/putting-more.png\"/>"
         html += "	  </div>";
         html += "	</div>";
         html += "</li>";
        $("#sharedDiv").append(html);

        var $row = $("#shareUser_" + users[i].sharedUserId);
        $row.data("user", users[i]);
    }
    sharedmembersContentText()
}

//修改权限
function memberInformationPreview(o){
    var data = $(o).parent().parent().data("user");
    shareUser = data;
	$(".putting-out").hide();
	$("#addshare4").show();
	if(data.roleName=="previewer"){
		$("#preview1").removeClass("M-addblank");
		$("#preview1").addClass("M-active");
	}else if(data.roleName=="downLoader"||data.roleName=="viewer"){
		$("#preview1").removeClass("M-addblank");
		$("#preview1").addClass("M-active");
		
		$("#download1").removeClass("M-addblank");
		$("#download1").addClass("M-active");
	}else if(data.roleName=="uploadAndView"){
		$("#preview1").removeClass("M-addblank");
		$("#preview1").addClass("M-active");
		
		$("#uploadSpan").removeClass("M-addblank");
		$("#uploadSpan").addClass("M-active");
		
		$("#download1").removeClass("M-addblank");
		$("#download1").addClass("M-active");
	}else if(data.roleName=="uploader"){
		$("#preview1").removeClass("M-addblank");
		$("#preview1").addClass("M-active");
		
		$("#uploadSpan").removeClass("M-addblank");
		$("#uploadSpan").addClass("M-active");
	}
}
//选择角色
$(function(){
	$('#share-second-tail-content li').click(function(){
		if($(this).find('i').attr('class')=='M-addblank'){
			$(this).find('i').removeClass("M-addblank");
		  	$(this).find('i').addClass("M-active");
		}else{
			$(this).find('i').removeClass("M-active");
			 $(this).find('i').addClass("M-addblank");
		}
	});
	
	$('#share-second-tail-content li').eq(1).click(function(){
		$.toast("默认预览",400);
	})
})
//取消设置权限
function cancelUpdate(){
	$(".putting-out").show();
	$("#addshare4").hide();
	$('#share-second-tail-content li').children('i').removeClass('M-active');
 	$('#share-second-tail-content li').children('i').addClass('M-addblank');
}

function showDeleteSpan(){
	$('#deleteMemberDiv ul').width(window.screen.width-100);
	$("h2[name='deleteSpan']").each(function(){
		if($(this).css("display")=="none"){
			$("#deleteMemberBtn").html("取消删除成员");
			$("#deleteMemberDiv").css("display","block");
			$(this).css("display","block");
			$('#sharedDiv li').unbind();
			sf()
			$('#deleteMemberDiv ul').addLineScrollAnimate();
			$('#sharedDiv .content').click(function(e){
				e.stopPropagation();
			})
		}else{
			$("#deleteMemberBtn").html("删除成员");
			$(this).css("display","none");
			$('#deleteMemberDiv li').remove();
			$('#deleteMemberDiv').css('display','none');
			$('#sharedDiv h2').addClass("M-addblank");
			$('#sharedDiv h2').removeClass("M-active");
			sf()
			$('#sharedDiv .content').unbind();
		}
	});
}
	function addSf(){
		if($('#preAddMemberDiv').css('display')=='none'){
			$('.share-address-content').css('marginBottom',0);
		}else{
			$('.share-address-content').css('marginBottom',2.2+'rem');
		}
	}
function sf(){
	if($('#deleteMemberDiv').css('display')=='none'){
		$('.sharedmembers-content').css('bottom',0);
	}else{
		$('.sharedmembers-content').css('bottom',2.2+'rem');
	}
}

function getDeleteUserIds(){
	var users=new Array()
	$("#deleteMemberDiv").find("li").each(function(){
		var did=$(this).attr("id");
		var user={};
		user.id=did.replace("d","");
		user.type=$(this).attr("type");
		users.push(user);
	});
	
	return users;
}

function deleteSharedUsers(){
	var users=getDeleteUserIds();
	<%--var params= {--%>
		    <%--"iNodeId": iNodeId,--%>
		    <%--"sharedUsers": JSON.stringify(users),--%>
		    <%--"token":token--%>
	    <%--};--%>
	$.ajax({
        type: "POST",
        async:false,
        data: JSON.stringify(users),
		url: host + "/ufm/api/v2/shareships/" + ownerId + "/" + iNodeId + "/delete",
        error: function(xhr, status, error){
			$.toast("删除失败")
        },
        success: function(data) {
        	$("#deleteMemberDiv").css("display","none");
        	window.location.reload();
//      	listSharedUser();
        }
    })
	if($('#deleteMemberBtn').text('取消删除成员')){
		if($('.deleteMemberDiv-boy li').length>0){
			$('#deleteMemberBtn').text('删除成员');
		}
	}

	sharedmembersContentText()
	$('.deleteMemberDiv-boy').remove();
}
function sharedmembersContentText(){
	if($('#sharedDiv li').length==0){
		$('#deleteMemberBtn').css('display','none');
		$('#deleteMemberBtn').siblings('i').css('display','none');
	}else{
		$('#deleteMemberBtn').css('display','block');
		$('#deleteMemberBtn').siblings('i').css('display','block');
	}
}
function tailNameSlides(){
	$('#deleteMemberDiv ul').width(window.screen.width-100);
	$('.deleteMemberDiv-boy').width(($('.deleteMemberDiv-boy li').length+1)*$('.deleteMemberDiv-boy li').width());
}
function userSelect(th){
	   $("#deleteMemberDiv").css("display","block");
	   if($(th).attr("class")=="M-addblank"){
			 $(th).removeClass("M-addblank");
			 $(th).addClass("M-active");
			 fullMemberDiv($(th).attr("value"),$(th).attr("type"));
		}else{
			 $(th).removeClass("M-active");
			 $(th).addClass("M-addblank");
			 removeMemberDiv($(th).attr("value"))
		}
	    window.event.stopPropagation();
	    tailNameSlides()
	if($('#deleteMemberDiv li').length == 0){
	$('#deleteMemberDiv').css('display','none');
	}
	sf()
}

function fullMemberDiv(user, type) {
    var html = "";
     html += "<li id=\"d" + user + "\" type='" + type + "' onclick=\"cancelDeleteMember(" + user + ")\">";
    if (type == 0) {
         html += "<i><img src=\"${ctx}/userimage/getUserImage/" + user + "\"/></i>";
    } else if (type == 2) {
         html += "<i><img src=\"${ctx}/static/skins/default/img/department-icon.png\"/></i>";
    }
     html += "</li>";
    $("#deleteMemberDiv .deleteMemberDiv-boy").append(html);
}

function removeMemberDiv(user){
	 $("#d"+user).remove();
}
function cancelDeleteMember(user){
	$("#d"+user).remove();
	$("#ww"+user).addClass("M-addblank");
	$("#ww"+user).removeClass("M-active");
	if($('#deleteMemberDiv li').length == 0){
		$('#deleteMemberDiv').css('display','none');
	}
	sf()
}
function  submitMenber1(){
	var params= {
		    <%--"userType":shareUser.sharedUserType,--%>
		    <%--"authType":getRoleName1(),--%>
		    <%--"token":"${token}"--%>
			roleName:getRoleName1(),
			sharedUserList:[{id:shareUser.sharedUserId,type:shareUser.sharedUserType}]
	    };
	$.ajax({
        type: "PUT",
        async:false,
        data: JSON.stringify(params),
        url:host+"/ufm/api/v2/shareships/"+ownerId+"/"+iNodeId,
        error: function(xhr, status, error){
        	$.toast("修改权限失败");
        },
        success: function(data) {
        	$.toast("修改权限成功");
        	window.location.reload()
//      	cancelUpdate();
        	listSharedUser();
        }
    })
}

function getRoleName1(){
	var roleName=""; 
	 var preview  = $("#preview1").attr("class")=="M-active"?true:false;
	 var download = $("#download1").attr("class")=="M-active"?true:false;
	 var upload = $("#uploadSpan").attr("class")=="M-active"?true:false;
	 if(download==true){
		 if(upload==true){
			 if(preview==true){
				 roleName="uploadAndView";
			 }else{
				 roleName="uploadAndView";
			 }
		 }else{
            if(preview==true){
           	 roleName="viewer";
			 }else{
				 roleName="viewer";
			 }
		 }
	 }else{
		if(upload==true){
			 if(preview==true){
				 roleName="uploader";
			 }else{
				 roleName="uploader";
			 }	 
		 }else{
            if(preview==true){
           	 roleName="previewer";
			 }else{
				 roleName="previewer";
			 }		 
		}
	 }
	 return roleName;
}

//企业通讯录JS

var allMessageTo = [];
   var selectMenbers={};
   var haseSharedMember="|";
	
// $(function(){
//	   listSharedUser();
// })
   
   function comfireMenber(){
	   /* fullSelectMenber(); */
	  if($('#preAddMember li').length == 0){
	  	$('.addshare-member-tail').css('display','none');
	  }else{
	  	showShareDiv('addshare1');
	  }
   }
   /*点击重新选择清空数据*/
   $('.share-span1').click(function(){
   		$('#parentDeptId').val('0');
   		$('#shareList li').children('i').addClass('M-addblank');
	   	$('#shareList li').children('i').removeClass('M-active');
	   	$('#preAddMember li').remove();
	   	$('#preAddMemberDiv').hide();
   })
   
   function roleSelect(th){
	   if($(th).find("i").attr("class")=="M-addblank"){
		   $(th).find("i").removeClass("M-addblank");
		   $(th).find("i").addClass("M-active");
		   
		}else if($(th).find("i").attr("class")=="M-active"){
			 $(th).find("i").removeClass("M-active");
			 $(th).find("i").addClass("M-addblank");
		}
   }
   
   function getRoleName(){
	 var roleName=""; 
	 var preview  = $("#preview").attr("class")=="M-active"?true:false;
	 var download = $("#download").attr("class")=="M-active"?true:false;
	 var upload = $("#upload1").attr("class")=="M-active"?true:false;
	 if(download==true){
		 if(upload==true){
			 if(preview==true){
				 roleName="uploadAndView";
			 }else{
				 roleName="uploadAndView";
			 }
		 }else{
             if(preview==true){
            	 roleName="viewer";
			 }else{
				 roleName="viewer";
			 }
		 }
	 }else{
		if(upload==true){
			 if(preview==true){
				 roleName="uploader";
			 }else{
				 roleName="uploader";
			 }	 
		 }else{
             if(preview==true){
            	 roleName="previewer";
			 }else{
				 roleName="previewer";
			 }		 
		}
	 }
	 return roleName;
   }
   //点击企业通讯录
   function openEnterpriseDirectory(){
	   showShareDiv('addshare3');
	   showRootDept();
   }
   
   function showShareDiv(addshare){
	   $("#addshare1").css("display","none");
	   $("#addshare2").css("display","none");
	   $("#addshare3").css("display","none");
	   $("#"+addshare).css("display","block");
   }
   
   function fullSelectMenber(member){
		   var html="";
		   html=html+"<li id=\"active-"+member.id+"\">";
		   if(member.type == "user"){
		   	   html+="<p><img src=\"${ctx}/userimage/getUserImage/"+member.id+"\"/></p>";
		   }else{
			   html+="<p><img src=\"${ctx}/static/skins/default/img/department-icon.png\"/></p>";
		   }
		   if(member.type == "user"){
			   html+="<h1>"+member.alias+"</h1>";
		   }else{
			   html+="<h1>"+member.name+"</h1>";
		   }
		   html=html+"</li>";
		   $("#shareContent").prepend(html);
		   $("#active-"+member.id).data("data",member);
  
   }
   
   function fullSharedMenber(members,tag){
		   for(var key in members){
			   haseSharedMember=haseSharedMember+"u"+members[key].sharedUserId+"|";
		   }  
	  
		   addDeleteBadge($("#shareContent"));
   }
   
   function addDeleteBadge($target) {
	    new Hammer($target[0]).on('press', function(e) {
	        var $children = $(e.target).children();
	        if($children.size() == 0) {
	            return;
	        }
	        $('.share-second').on("click", function(){
	            $target.find(".badge-action").remove();
//	            $mask.remove();
	        });
	        for (var i = 0 ; i < $(e.target).children().length-1; i++) {
	        	var $badge = $('<i class="weui-icon-cancel badge-action"></i>').appendTo($(e.target).children().eq(i));
	        	$('.weui-icon-cancel').on("click", function(e) {
	                e.stopPropagation();
	                var $li = $(this).parent();
	                var node = $li.data("data");
                        $li.remove();
//                      $mask.remove();
	            })
	        }
	    });
	}

   function showRootDept() {
	   showDepAndUsers(0);
   }
   
   function showDepAndUsers(deptId,deptName){
	   if(deptId == 0){
		   deptName = "企业通讯录";
	   }
	   var param={
				deptId:deptId
		}
		$.ajax({
	        type: "POST",
	        async:false,
	        url:host + '/ecm/api/v2/users/listDepAndUsers',
	        data:JSON.stringify(param),
            error: function(xhr, status, error){
	        },
	        success: function(data) {
	        	data = $.parseJSON(data);
	        	if(data.length > 0){
	        		showShareDiv('addshare3');
	        		if(typeof(deptName)=="undefined"){
	        			deptName = "";
	        		}
	        		$("#parentDeptId").val($("#parentDeptId").val()+"|"+deptId+","+deptName);
        		    
        			$("#department").html(deptName);
	        		fullShareDiv(data);
	        	}else{
	        		$.toast("没有员工",400);
	        	}
				cancelTheDoubleSelection();
	        }
	    });
   }
   
   function historyBack(){
	   var parentDeptId = $("#parentDeptId").val();
	   var historyDepts = parentDeptId.split("|");
	   var parentDept = 0;
	   if(historyDepts.length > 1){
		   parentDept = historyDepts[historyDepts.length-2];
	   }
	   if(parentDept=="0"){
		   showShareDiv("addshare2")
		   $("#parentDeptId").val(0);
		   return;
	   }
	   if (parentDept==""||parentDept==undefined) {
	   	
	   } else{
	   		parentDept = parentDept.split(",");
	   }
	   $("#parentDeptId").val(parentDeptId.substring(0,parentDeptId.lastIndexOf("|")));
	   parentDeptId = $("#parentDeptId").val();
	   $("#parentDeptId").val(parentDeptId.substring(0,parentDeptId.lastIndexOf("|")));
	   if(parentDept.length > 1){
		   if(parentDept[1] == ""){
			   parentDept[1] = "企业通讯录";
		   }
		   showDepAndUsers(parentDept[0],parentDept[1]);
	   }else{
		   showRootDept();
	   }
   }
   function cancelTheDoubleSelection(){
   		for (var i = 0 ; i < $('#preAddMemberDiv li').length; i ++) {
	   		for (var k = 0 ; k < $('#shareList li').length; k++) {
	   			var $iid = $('#preAddMemberDiv li').eq(i).attr('id');
	   			var $kid = $('#shareList li').eq(k).children('i').attr('id');
	   			if($('#shareList li').eq(k).children('i').hasClass('M-inactive')){
	   				
	   			}else{
	   				var a = $iid.substring(3);
	   				var b = $kid.substring(2);
	   				if(a == b){
	   					$('#shareList li').eq(k).children('i').addClass('M-active');
	   					$('#shareList li').eq(k).children('i').removeClass('M-addblank');
	   					break;
	   				}
	   			}
	   			
	   		}
	   }
   }
   function fullShareDiv(datas){
	   $("#shareList").empty();
	   for(var i=0;i<datas.length;i++){
	  
		   if(datas[i].id == curUserId && datas[i].type=="user"){
			   continue;
		   }
		   var html="";
		   if(datas[i].type == "department"){
		       html+="<li>";
		   }else{
			   html+="<li onclick=\"selectIcon($(this).children('i').first())\">";
		   }
		   
		   if(haseSharedMember.indexOf("|u"+datas[i].id+"|")<0){
			   html=html+"<i class=\"M-addblank\" onclick=selectIcon(this) id='p-"+datas[i].id+"'></i>";
		   }else{
			   html=html+"<i class=\"M-inactive\" ></i>";
		   }
		  
		   if(datas[i].type=="user"){
			   html=html+"<p><img src=\"${ctx}/userimage/getUserImage/"+datas[i].id+"\"/></p>";
			   html=html+"<span>"+datas[i].alias+"</span>";
		   }else if(datas[i].type=="department"){
        	   html=html+"<p onclick=\"selectIcon($(this).prev())\"><img  src=\"${ctx}/static/skins/default/img/department-icon.png\"/></p>"; 
        	   html=html+"<span onclick=\"showDepAndUsers\n('"+datas[i].userId+"','"+ datas[i].name +"')\">"+datas[i].name+"</span>";
        	   html=html+"<h1 onclick=\"showDepAndUsers\n('"+datas[i].userId+"','"+ datas[i].name +"')\"><img src=\"${ctx}/static/skins/default/img/putting-more2.png\"/></h1>";
		   }else{
		   }
		 
		   html=html+"</li>";
		   $("#shareList").append(html);
		   $("#p-"+datas[i].id).data("userInfo",datas[i]);
		   $("#p-"+datas[i].id).click(function(event) {
	            event.stopPropagation();
	        });
	   }
	 
   }
   function tailNameSlide(){
   		var width = window.screen.width;
   		$('#preAddMemberDiv ul').width(width-100);
   		$('#preAddMember').width(($('#preAddMember li').length+1)*$('#preAddMember li').width());
   }
function addPreMember(member){
	var html="";
	html=html+"<li id=\"pre"+member.id+"\" onclick=\"clearShareMembers('"+ member.id +"')\">";
	if(member.type == "user"){
		html+="<i><img src=\"${ctx}/userimage/getUserImage/"+member.id+"\"/></i>";
	}else{
		html+="<i><img src=\"${ctx}/static/skins/default/img/department-icon.png\"/></i>";
	}
	html=html+"</li>";
	$("#preAddMember").append(html);
	
}
	/*页尾选中图标的点击事件点击后清楚图标*/
	function clearShareMembers(memberId){
		$("#active-" + memberId).remove();
		$("#pre" + memberId).remove();
		$("#p-" + memberId).removeClass("M-active");
		$("#p-" + memberId).addClass("M-addblank");
		tailNameSlide()
		if($('#preAddMember li').length == 0){
			$('.addshare-member-tail').css('display','none');
		}
		addSf()
	}

function selectIcon(th){
	var member=$(th).data("userInfo");
	if(typeof(member)=="undefined"){
		$.toast("已分享",400);
		return;
	}
	if($(th).attr("class")=="M-addblank"){
	   $(th).removeClass("M-addblank");
	   $(th).addClass("M-active");
	   if($("#preAddMemberDiv").css("display")=="none"){
		   $("#preAddMemberDiv").css("display","block");
	   }
	   addPreMember(member);
	   fullSelectMenber(member);
	   
	}else{
		 $(th).removeClass("M-active");
		 $(th).addClass("M-addblank");
		 $("#pre"+member.id).remove();
		 $("#active-"+member.id).remove();
	}
	tailNameSlide()
	if($('#preAddMember li').length == 0){
	$('.addshare-member-tail').css('display','none');
	}
	addSf()
}


function  submitMenber(){
	if($("#shareContent").children().length == 1){
		$.toast("请至少选择一个员工",400);
		return;
	}
	$("#shareContent").children().each(function(i,n){
		     var obj = $(n);
		     if(obj.attr("id")!=undefined&&obj.attr("id").indexOf("active")>-1){
		    	 var item=obj.data("data");
		    	 console.log(item);
		 		   if(item.type=="user"){
			 			addMessageTo(item.id,item.name,"0",item.name,item.email);
			 		}else{
			 			addMessageTo(item.id,item.name,"2",item.name,null); 
			 		}
		     }
		   
	});
    shareToOthers();
}

function cancelAddMenber(){
	gotoPage(ctx + "/share/folder/${ownerId}/"+iNodeId);
}

function addMessageTo(userCloudId, userLoginName,userType,userName, userEmail) {

	var itemValue = {
		id:userCloudId,
		type:userType
	};
	allMessageTo.push(itemValue);
	
}

function shareToOthers(){
	var authType =getRoleName();
	var params= {
		roleName:authType,
		sharedUserList:allMessageTo
    };
	isAddSharing = true;
	$.ajax({
		type: "PUT",
        data: JSON.stringify(params),
        url: host +"/ufm/api/v2/shareships/"+ownerId+"/"+iNodeId,
        error: function(xhr, status, error){
        	$.toast("共享失败","forbidden");
        },
        success: function() {
			 $.toast("共享成功");
			window.location.reload()
        }
    });   
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
