<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <%@ include file="../common/include.jsp" %>
<link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/share/inviteShare.css"/>
<link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/teamSpace/memberMgr.css"/>
    <title>空间成员列表</title>
    <script src="${ctx}/static/js/common/enterprise-directory.js"></script>
</head>
<body>
	<div class="putting-out">
	<div class="putting-background"></div>
	<div class="share-homepage-header">
		<div class="share-homepage-top">
			<span><img src="${ctx}/static/skins/default/img/space-row-icon.png"/></span>
		</div>
		<div class="share-homepage-right">
			<div class="share-homepage-middle">
				<i style="font-size: 1rem;">${memberInfo.teamspace.name}</i>
			</div>
		
		</div>
		<div class="share-homepage-header-icon"></div>
	</div>
	<div class="putting-append">
		<i><img src="${ctx}/static/skins/default/img/putting-apped.png"/></i><span >添加成员</span>
	</div>
	<div class="fillBackground"></div>
	<div class="putting-sharedmembers">
		<span>成员列表</span>
		<p onclick="showDeleteSpan()"><span id="deleteMemberBtn" style="display: none;">删除成员</span><i style="display: none;"><img src="${ctx}/static/skins/default/img/sharedmembers-delete.png"/></i></p>
	</div>
	<div class="sharedmembers-content" style="position: fixed; top: 9.1rem; bottom: 0rem;">
		<!-- <div class="putting-blank-background">
			<div class="putting-blank-backgrounds">暂无共享成员</div>
		</div> -->
		 <ul id="spaceMemberList">
		</ul>
		<script  id="members"  type="text/template7">
		   <li id='member_{{id}}'>
		      <div class="content">
		      	{{#js_compare "this.role=='拥有者'"}}
                 
                {{else}}
                <h2 class="M-addblank" id="ww{{userId}}" ids="{{id}}" userType="{{type}}" style="display:none" data-type="{{teamId}}" name="deleteSpan" value="{{userId}}" onclick="userSelect(this,{{teamId}})"></h2>
                {{/js_compare}}
				{{#js_compare "this.type=='user'"}}
				<i><img src="${ctx}/userimage/getUserImage/{{userId}}"/></i>
				{{else}}
                <i><img src="${ctx}/static/skins/default/img/department-icon.png"/></i>
				{{/js_compare}}
                <h1>{{name}}</h1>
                {{#js_compare "this.role=='拥有者'"}}
                <p onclick="selectPermissions(this,'{{role}}')">{{role}}</p>
                {{else}}
                <p onclick="selectPermissions(this,'{{role}}')">{{role}}<img src="${ctx}/static/skins/default/img/putting-more.png"/></p>
                {{/js_compare}}
              </div>
           </li>
		</script>
	</div>
	<div class="addshare-member-tail" style="display: none" id="deleteMemberDiv">
    	<div class="member-tail-buttons" onclick="deleteMember()">
				<div class="member-tail-button">确定</div>
		</div>
		<ul>
			<div class="deleteMemberDiv-boy"></div>
		</ul>
	</div>
    <!--<div class="addshare-member-tail" style="display: none" id="deleteMemberDiv">
		<ul >
			<div class="member-tail-button" onclick="deleteSharedUsers()">确定</div>
		</ul>
	</div>-->
</div>
<div class="share-second" id="addshare4" style="display: none;">
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
					<i class="M-addblank" id="upload2"></i><p>上传</p>
				</li>
			</ul>
		</div>
	</div>
	<input type="hidden" id="updateUserPermission" />
	<a href="javascript:;" class="shaerDetermine shaerDetermines" onclick="updateMember()">确定</a>
	<a href="javascript:;" class="shareCancel shaerDetermines" onclick="cancelUpdate()">取消</a>
</div>

<!--企业通讯录-->
<div class="share-second" style="display: none" id="addshare1">
	<div class="putting-background"></div>
	<div class="share-second-header">
		<span id="totalMember">添加的成员</span>
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
					<span><i class="M-active" id="download" ></i><p>下载</p></span>
				</li>
				<li onclick="roleSelect(this)">
					<span><i class="M-active" id="upload1" ></i><p>上传</p></span>
				</li>
			</ul>
		</div>
	</div>
	<a href="javascript:;" class="shaerDetermine shaerDetermines" onclick="submitMenber()">确定</a>
</div>



<!--添加要共享的成员-->
<div class="add-leaguer" style="display: none" id="addshare2">
	<div class="add-leaguer-nav">
		<ul>
			<li onclick="openEnterpriseDirectory()">
				<i><img src="${ctx}/static/skins/default/img/add-leaguer-group.png"/></i>
				<span>成员列表</span>
				<p><img src="${ctx}/static/skins/default/img/putting-more2.png"/></p>
			</li>
		</ul>
	</div>
 	<%--<div class="add-leaguer-tail">--%>
		<%--<ul>--%>
			<%--<li>--%>
				<%--<h1>最近联系的人</h1>--%>
			<%--</li>--%>
		<%--</ul>--%>
	<%--</div> --%>
</div>
<!--共享成员通讯录-->
<div class="share-address-list" style="display: none;width: 100%;position: fixed;top: 0;bottom: 0rem;overflow-y: auto" id="addshare3">
	<div class="share-address-content">
		<input type="hidden" id="parentDeptId" value="0"/>
		<div class="return-father" onclick="historyBack()">
			<div class="historyBack-return">返回</div>
			<b>|</b>
			<span id="department"></span>
		</div>
		<ul id="shareList" style="">
			
		</ul>
	</div>
	
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


<script id="spaceMemberTemplate" type="text/template7">
    <div class="member-row">
        <div class="member-item">
            <div>{{username}}</div>
            <span>{{teamRole}}</span>
        </div>
        <div class="member-operation">
            <a class="weui-icon-delete" onclick="deleteMember('${teamId}', '{{userId}}')"></a>
        </div>
    </div>
</script>

<script id="pickerMemberTemplate" type="text/template7">
    <div class="staff-picker-member-row">
        <div class="staff-picker-member-item" id="space_{{id}}">
            <div>{{name}}</div>
            <span>部门:</span>
            <i>邮箱：{{email}}</i>
        </div>
    </div>
    <div class="staff-picker-member-operation">
        <div onclick="addUserMember(this, '${teamId}', '{{name}}', '{{cloudUserId}}', '{{email}}')"> + </div>
    </div>
</script>

<script type="text/javascript">
	var iNodeId = "${folderId}";
	var $puttingAppend  = $('.putting-append');
    $(function () {
        //增加关闭功能
        $("#searchClose").on("click", function (e) {
            $(e.target).parents(".staff-picker").hide();
        });

        $('#searchInput').bind('input propertychange', function (e) {
                    var keyword = $("#searchInput").val();
                    if(keyword !== "") {
                        initStaffList(keyword);
                    }
                }
        );
        initDataList(1);
    });
	
	$puttingAppend.on('click',function(){
		$('.putting-out').hide();
		$('#addshare2').show();
		$('.tabbar').hide();
	})


    function initDataList(curPage) {
        var url = host+"/ufm/api/v2/teamspaces/${teamId}/memberships/items";
        var params = {
            "keyword": ""
        };
        $.ajax({
            type: "POST",
            url: url,
            data: JSON.stringify(params),
            error: function (request) {
                $.toast('<spring:message code="inviteShare.listUserFail"/>', 'cancel');
            },
            success: function (data) {
                var $list = $("#spaceMemberList");
                $list.children().remove();
                var $template = $("#members");
                haseSharedMember="|";
                for (var i = 0; i < data.memberships.length; i++) {
                
                	var member = data.memberships[i].member;
					member.role=data.memberships[i].role
					member.teamRole=data.memberships[i].teamRole
					member.userId = data.memberships[i].member.id
					member.teamId = data.memberships[i].teamId
					member.id = data.memberships[i].id
					fullSharedMenber(member)
                    if(member.role=="auther" ){
                    	if(member.teamRole=="admin"){
                    		member.role = "拥有者";
                    	}else{
                    		member.role = "管理员";
                    	}
                    }else if(member.role=="previewer"){
            			    member.role="预览";
	           		}else if(member.role=="uploadAndView" || member.role=="editor"){
	            			member.role="预览 上传 下载";
	            	}else if(member.role=="viewer"){
	            			member.role="预览  下载";
	            	}else if(member.role=="uploader"){
	            			member.role="预览  上传";
	            	}
                    

                    var thisDiv=$template.template(member);
                    thisDiv.data("user",member);
                    thisDiv.appendTo($list);
               }
                if(data.memberships.length==0){
                	
                }else{
                	$('#deleteMemberBtn').show();
                	$('#deleteMemberBtn').siblings("i").show();
                }
            }
        });
    }

    //
    function showStaffPicker() {
        $("#staffPicker").show();
    }

    function closeStaffPicker() {
        $("#staffPicker").hide();
    }

    function initStaffList(keyWord) {
        var url = "${ctx}/share/listMultiUser";
        var params = {
            "ownerId": "${ownerId}",
            "token": "${token}",
            "userNames": keyWord
        };
        $.ajax({
            type: "POST",
            url: url,
            data: params,
            error: function (request) {
                $.toast('<spring:message code="inviteShare.listSharedUserFail"/>', "cancel");
            },
            success: function (data) {
                var $list = $("#pickerMemberList");
                $list.children().remove();

                var $template = $("#pickerMemberTemplate");
                for (var i = 0; i < data.successList.length; i++) {
                    var member = data.successList[i];
                    $template.template(member).appendTo($list);
                }
            }
        });

    }

    function userSelect(th, type) {   //选中或取消删除对象按钮的点击事件
        $("#deleteMemberDiv").css("display", "block");
        if ($(th).attr("class") == "M-addblank") {
            $(th).removeClass("M-addblank");
            $(th).addClass("M-active");
            fullMemberDiv($(th).attr("ids"), $(th).attr("value"), type, $(th).attr("userType"));
        } else {
            $(th).removeClass("M-active");
            $(th).addClass("M-addblank");
            removeMemberDiv($(th).attr("ids"), type)
        }
        window.event.stopPropagation();
        tailNameSlides()
        if ($('#deleteMemberDiv li').length == 0) {
            $('#deleteMemberDiv').css('display', 'none');
        }
        sf()
    }


    function tailNameSlides() {
        $('#deleteMemberDiv ul').width(window.screen.width - 100);
        $('.deleteMemberDiv-boy').width(($('.deleteMemberDiv-boy li').length + 1) * $('.deleteMemberDiv-boy li').width());
    }

    function removeMemberDiv(user, type) {
        $("#" + user).remove();
    }

    function fullMemberDiv(id, user, type, userType) {
        var html = "";
        html += "<li id=" + id + "\ value=" + type + " onclick=\"cancelDeleteMember(" + id + "," + user + "," + type + ")\">";
        if (userType == "user") {
            html += "<i><img src=\"${ctx}/userimage/getUserImage/" + user + "\"/></i>";
        } else if (userType == "department") {
            html += "<i><img src=\"${ctx}/static/skins/default/img/department-icon.png\"/></i>";
        }
        html += "</li>";
        $("#deleteMemberDiv .deleteMemberDiv-boy").append(html);
    }

	function selectPermissions(o,role){
		var data = $(o).parent().parent().data("user");
		if(role=='拥有者'){
			$.toast("不能修改拥有者权限");
		}else{
			$('#addshare4').show();
			$('.putting-out').hide();
			$('#addshare4').data("user",data);
			
			if(data.role == "预览  上传"){
				$("#upload2").removeClass("M-addblank");
				$("#upload2").addClass("M-active");
			}else if(data.role=="预览  下载"){
				$("#download1").removeClass("M-addblank");
				$("#download1").addClass("M-active");
			}else if(data.role=="预览 上传 下载"){
				$("#upload2").removeClass("M-addblank");
				$("#upload2").addClass("M-active");
				
				$("#download1").removeClass("M-addblank");
				$("#download1").addClass("M-active");
			}else if(data.role=="管理员"){
				$("#upload2").removeClass("M-addblank");
				$("#upload2").addClass("M-active");

				$("#download1").removeClass("M-addblank");
				$("#download1").addClass("M-active");
			}
		}	
	}
	
	$('#share-second-tail-content li').click(function(){
		if($(this).children('i').hasClass('M-addblank')){
			$(this).children('i').addClass('M-active');
			$(this).children('i').removeClass('M-addblank')
		}else{
			$(this).children('i').addClass('M-addblank');
			$(this).children('i').removeClass('M-active')
		}
	})
	
    function addUserMember(e, teamId, loginName, cloudUserId, email) {
        var item = "[user]" + loginName + "[" + cloudUserId + "]" + email;
        addMember(teamId, item, function(r) {
            if(r === "ok") {
                $(e).addClass("operation-success");
            } else {
                $(e).addClass("operation-fail");
            }
        });
    }

    function addMember(teamId, teamMember, callback) {
        var url = host+"/ufm/api/v2/teamspaces/"+ teamId +"/memberships";
        var data = {
            cloudUserIds: getTrunkData(teamMember),
            teamId: teamId,
			teamRole:"member",
			<%--role:--%>
			<%--member:--%>
        };

        var r = "fail";
        $.ajax({
            type: "POST",
            url: url,
            data: data,
            error: function (request) {
                $.toast("<spring:message code='operation.failed'/>", "cancel");
            },
            success: function (data) {
                if (data == "OK") {
                    r = "ok";
                    $.toast("<spring:message code='operation.success'/>");
                } else if (data == "P_OK") {
                    r = "ok";
                    $.toast("<spring:message code='teamSpace.error.addMemberpartly'/>");
                } else if (data == "NoSuchTeamspace") {
                    $.toast("<spring:message code='teamSpace.error.NoFound'/>", "cancel");
                } else if (data == "AbnormalTeamStatus") {
                    $.toast("<spring:message code='teamSpace.error.AbnormalTeamStatus'/>", "cancel");
                } else if (data == "NoSuchUser") {
                    $.toast("<spring:message code='teamSpace.error.NoSuchUser'/>", "cancel");
                } else if (data == "ExistMemberConflict") {
                    $.toast("<spring:message code='teamSpace.error.ExistMemberConflict'/>", "cancel");
                } else if (data == "ExceedTeamSpaceMaxMemberNum") {
                    $.toast("<spring:message code='teamSpace.error.ExceedMemberMax'/>", "cancel");
                } else if (data == "Forbidden") {
                    $.toast("<spring:message code='teamSpace.error.Forbidden'/>", "forbidden");
                } else if (data == "InvalidTeamRole") {
                    $.toast("<spring:message code='teamSpace.error.InvalidTeamRole'/>", "cancel");
                } else if (data == "InvalidPermissionRole") {
                    $.toast("<spring:message code='teamSpace.error.InvalidPermissionRole'/>", "cancel");
                } else {
                    $.toast("<spring:message code='operation.failed'/>", "cancel");
                }
                initDataList(1);
            }
        });

        if(callback != undefined && typeof callback === "function") {
            callback(r);
        }
    }

	
	
function cancelDeleteMember(id,user){  //页尾选中头像的点击事件
	$("#"+id).remove();
	$("#ww"+user).addClass("M-addblank");
	$("#ww"+user).removeClass("M-active");
	if($('#deleteMemberDiv li').length == 0){
	$('#deleteMemberDiv').css('display','none');
	}
	sf()
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




    function deleteMember() {
    
    	var teamMemberIds=[];
    	for (var i = 0; i < $('.deleteMemberDiv-boy li').length; i++) {
    		var teamMemberId = $('.deleteMemberDiv-boy li').eq(i).attr('id');
    		teamMemberIds.push(teamMemberId);
    	}
    	var url = host+"/ufm/api/v2/teamspaces/"+ teamId + "/memberships/batch";
    	$.ajax({
            type: "DELETE",
            url: url,
            async: false,
            data:JSON.stringify(teamMemberIds),
            error: function (request) {
                $.toast("<spring:message code='operation.failed'/>", "cancel");
            },
            success: function (data) {
                if (typeof(data) == 'string' && data.indexOf('<html>') != -1) {
                    window.location.href = "${ctx}/logout";
                    return;
                }
                initDataList(1);
                $.toast("删除成功", "<spring:message code='operation.success'/>");

            }
        });
        $('.deleteMemberDiv-boy li').remove();
        $('#deleteMemberDiv').hide();
        $('#deleteMemberBtn').text('删除成员');
        $('#spaceMemberList .content').children('h2').hide();
        sf()
    }

    /**
     * 将字符串数字拼接成trunk数据传个contror层:即Length:Value格式,Length的长度固定是4
     * 举例存在 tony, sonina两个字符串，转换成trunk数据为: 0004tony0006sonina
     */
    function getTrunkData(s){
        if(s == null || s == ""){
            return "";
        }

        return pad(s.length,4) + s;
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
    
   	function cancelUpdate(){
   		$('#share-second-tail-content li').children('i').removeClass('M-active');
 		$('#share-second-tail-content li').children('i').addClass('M-addblank');
   		$('#addshare4').hide();
   		$('.putting-out').show();
   	}
   	
   	
function getDeleteUserIds(){
	var ids="";
	$("#deleteMemberDiv").find("li").each(function(){
		var did=$(this).attr("id");
		ids=ids+did.replace("d","")+",";
	});
	return ids;
}

function showDeleteSpan(){
	$('#deleteMemberDiv ul').width(window.screen.width-100);
    if($('#deleteMemberBtn').text()=="取消删除成员"){
    	$('#deleteMemberBtn').text('删除成员');
    	$('#spaceMemberList li').children('.content').children('h2').hide();
    	$('#spaceMemberList li').children('.content').children('h2').addClass("M-addblank");
		$('#spaceMemberList li').children('.content').children('h2').removeClass("M-active");
		$('#deleteMemberDiv li').remove();
		$('#deleteMemberDiv').hide();
		sf()
    	
    }else{
    	$('#deleteMemberBtn').text('取消删除成员');
    	$('#spaceMemberList li').children('.content').children('h2').show();
    	$('#deleteMemberDiv').show();
    	sf()
    }
}

   	
function updateMember(shareUser) {
	var shareUser=$('#addshare4').data("user");
    var url = host+"/ufm/api/v2/teamspaces/"+shareUser.teamId+"/memberships/"+shareUser.id;
    var data = {
		role:getRoleName1(),
		teamRole:"member"
    };
    $.ajax({
        type: "PUT",
        url: url,
        data: JSON.stringify(data),
        error: function (request) {
           
        },
        success: function (data) {
            	$.toast("修改权限成功");
            	window.location.reload()
        }
    });
}
function getRoleName1(){
	     var roleName=""; 
		 var preview  = $("#preview1").attr("class")=="M-active"?true:false;
		 var download = $("#download1").attr("class")=="M-active"?true:false;
		 var upload = $("#upload2").attr("class")=="M-active"?true:false;
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




/*企业通讯录*/




   var allMessageTo = [];
   var selectMenbers={};
   var haseSharedMember="|";
   var teamId="${teamId}";
	
   $(function(){
	 // listSharedUser();
	  <%--showRootDept(-1);--%>
   })
   
   
   function comfireMenber(){
	   if($('#preAddMember li').length>0){
	   	$('#addshare1').css('display','block');
	   	$('#addshare3').css('display','none');
	   }else{
	   		$.toast("请选择要添加的成员",400);
	   }
   }
   
   function roleSelect(th){
	   if($(th).find("i").attr("class")=="M-addblank"){
		   $(th).find("i").removeClass("M-addblank");
		   $(th).find("i").addClass("M-active");
		   
		}else{
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
   
   function showShareDiv(addshare){
// 		$('#parentDeptId').val('0');
	   $("#addshare1").css("display","none");
	   $("#addshare2").css("display","none");
	   $("#addshare3").css("display","none");
	   $("#"+addshare).css("display","block");
   }
   
   function fullSharedMenber(member,tag){
			haseSharedMember=haseSharedMember+member.type+member.userId+"|";
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
	            $mask.remove();
	        });
	        $(e.target).children().each(function() {
	            var $badge = $('<i class="weui-icon-cancel badge-action"></i>').appendTo($(this));
	            $badge.on("click", function(e) {
	                e.stopPropagation();
	                var $li = $(this).parent().parent();
	                var node = $li.data("data");
                        $li.remove();
                        $mask.remove();
	            })
	        });
	    });
	}

   function showRootDept() {
	   showDepAndUsers(0);
   }
   
   function fullShareDiv(datas){
	   $("#shareList").empty();
	   for(var i=0;i<datas.length;i++){
		   var html="";
		   if(datas[i].type == "department"){
		       html+="<li>";
		   }else{
			   html+="<li onclick=\"selectIcon($(this).children('i').first())\">";
		   }
		   if(haseSharedMember.indexOf("|"+datas[i].type+datas[i].id+"|")<0){
			   html=html+"<i class=\"M-addblank\" onclick=selectIcon(this) id='p-"+datas[i].id+"'></i>";
		   }else{
			   html=html+"<i class=\"M-inactive\" ></i>";
		   }

           if (datas[i].type === "user") {
               html = html + "<p><img src=\"${ctx}/userimage/getUserImage/" + datas[i].id + "\"/></p>";
               html = html + "<span>" + datas[i].alias + "</span>";
           } else if (datas[i].type === "department") {
               html = html + "<p onclick=\"selectIcon($(this).prev())\"><img  src=\"${ctx}/static/skins/default/img/department-icon.png\"/></p>";
               html = html + "<span onclick=\"showDepAndUsers\n('" + datas[i].userId + "','" + datas[i].name + "')\">" + datas[i].name + "</span>";
               html = html + "<h1 onclick=\"showDepAndUsers\n('" + datas[i].userId + "','" + datas[i].name + "')\"><img src=\"${ctx}/static/skins/default/img/putting-more2.png\"/></h1>";
           } else {
           }
		 
		   html=html+"</li>";
		   $("#shareList").append(html);
		   $("#p-"+datas[i].id).data("userInfo",datas[i]);
		   $("#p-"+datas[i].id).click(function(event) {
	            event.stopPropagation();
	        });
	   }
	 
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
	/* $("#pre"+member.id).data(member); */
	
}
function selectIcon(th){
	var member=$(th).data("userInfo");
	if(typeof(member)=="undefined"){
		$.toast("已添加",400);
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
	$("#shareContent").children().each(function(i,n){
		     var obj = $(n);
		     if(obj.attr("id")!=undefined&&obj.attr("id").indexOf("active")>-1){
		    	 var item=obj.data("data");
		    	 if(item.type=="user"){
		 			addMessageTo(item.id,item.name,"user",item.name,item.email);
		 		}else{
		 			 addMessageTo(item.id,item.name,"department",item.name,null); 
		 		}
		     }
		   
	});
	addMember();
}



function addMessageTo(cloudUserId, loginName,userType,userName, userEmail) {
	var itemValue ={
		id:cloudUserId,
		type:userType
	}
	allMessageTo.push(itemValue);
	
}



function addMember() {
    var url = host+"/ufm/api/v2/teamspaces/"+teamId+"/memberships";
    var data = {
        <%--cloudUserIds: getTrunckData(allMessageTo),--%>
        <%--teamId: teamId,--%>
        <%--authType: getRoleName(),//TODO: 指定authType--%>
        <%--token: "${token}"--%>
		teamRole:"member",
		role:getRoleName(),
		memberList:allMessageTo

    };

    var r = "fail";
    $.ajax({
        type: "POST",
        url: url,
        data: JSON.stringify(data),
        error: function (request) {
            $.toast("<spring:message code='operation.failed'/>", "cancel");
        },
        success: function (data) {
            if (data== undefined || data== "" || data == "OK") {
                r = "ok";
                $.toast("添加成功", function() {
            				 gotoPage('${ctx}/teamspace/member/openMemberMgr/' + teamId);
            		      });
            } else if (data == "P_OK") {
                r = "ok";
                $.toast("<spring:message code='teamSpace.error.addMemberpartly'/>");
            } else if (data == "NoSuchTeamspace") {
                $.toast("<spring:message code='teamSpace.error.NoFound'/>", "cancel");
            } else if (data == "AbnormalTeamStatus") {
                $.toast("<spring:message code='teamSpace.error.AbnormalTeamStatus'/>", "cancel");
            } else if (data == "NoSuchUser") {
                $.toast("<spring:message code='teamSpace.error.NoSuchUser'/>", "cancel");
            } else if (data == "ExistMemberConflict") {
                $.toast("<spring:message code='teamSpace.error.ExistMemberConflict'/>", "cancel");
            } else if (data == "ExceedTeamSpaceMaxMemberNum") {
                $.toast("<spring:message code='teamSpace.error.ExceedMemberMax'/>", "cancel");
            } else if (data == "Forbidden") {
                $.toast("<spring:message code='teamSpace.error.Forbidden'/>", "forbidden");
            } else if (data == "InvalidTeamRole") {
                $.toast("<spring:message code='teamSpace.error.InvalidTeamRole'/>", "cancel");
            } else if (data == "InvalidPermissionRole") {
                $.toast("<spring:message code='teamSpace.error.InvalidPermissionRole'/>", "cancel");
            } else {
                $.toast("<spring:message code='operation.failed'/>", "cancel");
            }
            
        }
    });

   
}


/**
 * 将字符串数字拼接成trunk数据传个contror层:即Length:Value格式,Length的长度固定是4
 * 举例存在 tony, sonina两个字符串，转换成trunk数据为: 0004tony0006sonina
 */
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
</body>
</html>