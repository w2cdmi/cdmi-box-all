<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="../common/include.jsp"%>
<link rel="stylesheet" type="text/css"
	href="${ctx}/static/skins/default/css/share/inviteShare.css" />
<link rel="stylesheet" type="text/css"
	href="${ctx}/static/skins/default/css/teamSpace/memberMgr.css" />
<title>成员列表</title>
<script src="${ctx}/static/js/common/enterprise-directory.js"></script>
</head>
<body ontouchstart>
	<div class="putting-out" id="mainBody">
		<div class="putting-background"></div>
		<div class="share-homepage-header">
			<div class="share-homepage-top">
				<span><img src="${ctx}/static/skins/default/img/folder.png" /></span>
			</div>
			<div class="share-homepage-right">
				<div class="share-homepage-middle">
					<i style="font-size: 1rem;">${fileName}</i>
				</div>

			</div>
			<div class="share-homepage-header-icon"></div>
		</div>
		<div class="weui-cell weui-cell_switch">
		    <input hidden="hidden" id="secret" name="secret" type="radio" value="off" checked="checked" />
	        <div class="weui-cell__bd"  style="font-size: 0.75rem;">私密</div>
	        <div class="weui-cell__ft" style="margin-right: 0rem;">
	          <input class="weui-switch" id="secretSwitch" type="checkbox">
	        </div>
	    </div>
		<div class="putting-append">
			<i><img src="${ctx}/static/skins/default/img/putting-apped.png" /></i><span>添加成员</span>
		</div>
		<%-- <div class="putting-append">
			<i><img src="${ctx}/static/skins/default/img/putting-apped.png" /></i><span>取消授权</span>
		</div> --%>
		<div class="fillBackground"></div>
		<div class="putting-sharedmembers">
			<span>已添加成员</span>
			<p onclick="showDeleteSpan()">
				<span id="deleteMemberBtn" style="display: none;">删除成员</span><i
					style="display: none;"><img
					src="${ctx}/static/skins/default/img/sharedmembers-delete.png" /></i>
			</p>
		</div>
		<div class="sharedmembers-content"
			style="position: fixed; top: 11.1rem; bottom: 0rem;">
			<ul id="spaceMemberList">
			</ul>
			<script id="members" type="text/template7">
		   <li id='member_{{id}}'>
		      <div class="content">
		      	{{#js_compare "this.role=='拥有者'"}}
                {{else}}
                <h2 class="M-addblank" id="ww{{id}}" style="display:none" data-type="{{teamId}}" name="deleteSpan" value="{{id}}" userId="{{userId}}" onclick="userSelect(this)"></h2>
                {{/js_compare}}
                <i><img src="${ctx}/userimage/getUserImage/{{userId}}"/></i>
                <h1>{{username}}</h1>
                {{#js_compare "this.role=='拥有者'"}}
                <p onclick="selectPermissions(this,'{{role}}')">{{role}}</p>
                {{else}}
                <p onclick="selectPermissions(this,'{{role}}')">{{role}}<img src="${ctx}/static/skins/default/img/putting-more.png"/></p>
                {{/js_compare}}
              </div>
           </li>
		</script>
		</div>
		<div class="addshare-member-tail" style="display: none"
			id="deleteMemberDiv">
			<div class="member-tail-buttons" onclick="deleteMember()">
				<div class="member-tail-button">确定</div>
			</div>
			<ul>
				<div class="deleteMemberDiv-boy"></div>
			</ul>
		</div>
	</div>
	<div class="share-second" id="updatePermission" style="display: none;">
		<div class="share-second-tail">
			<div id="share-second-tail-content">
				<ul>
					<li><span class="share-tail-span">设置权限</span></li>
					<li><i class="M-addblank" id="update_preview"></i>
					<p>预览</p></li>
					<li><i class="M-addblank" id="update_download"></i>
					<p>下载</p></li>
					<li><i class="M-addblank" id="update_upload"></i>
					<p>上传</p></li>
				</ul>
			</div>
		</div>
		<a href="javascript:;" class="shaerDetermine shaerDetermines"
			onclick="updateMember()">确定</a> <a href="javascript:;"
			class="shareCancel shaerDetermines" onclick="cancelUpdate()">取消</a>
	</div>

	<!--企业通讯录-->
	<div class="share-second" style="display: none" id="addMember">
		<div class="putting-background"></div>
		<div class="share-second-header">
			<span id="totalMember">添加的成员</span>
		</div>
		<div class="share-second-kong"></div>
		<div class="share-second-content">
			<ul id="shareContent">


				<li>
					<div class="share-span1" onclick="showDiv('selectMember')"></div>
				</li>
			</ul>
		</div>
		<div class="share-second-tail">
			<div>
				<ul>
					<li><span class="share-tail-span">设置权限</span></li>
					<li><span><i class="M-active" id="add_preview"></i>
						<p>预览</p></span></li>
					<li><span><i class="M-active" id="add_download"
							onclick="roleSelect(this)"></i>
						<p>下载</p></span></li>
					<li><span><i class="M-active" id="add_upload"
							onclick="roleSelect(this)"></i>
						<p>上传</p></span></li>
				</ul>
			</div>
		</div>
		<a href="javascript:;" class="shaerDetermine shaerDetermines"
			onclick="submitMenber()">确定</a>
	</div>



	<!--添加要共享的成员-->
	<div class="add-leaguer" style="display: none" id="recentContacts">
		<div class="add-leaguer-nav">
			<ul>
				<li onclick="showDiv('selectMember')"><i><img
						src="${ctx}/static/skins/default/img/add-leaguer-group.png" /></i> <span>企业通讯录</span>
					<p>
						<img src="${ctx}/static/skins/default/img/putting-more.png" />
					</p></li>
			</ul>
		</div>
		<div class="add-leaguer-tail">
			<ul>
				<li>
					<h1>最近联系的人</h1>
				</li>
			</ul>
		</div>
	</div>
	<!--共享成员通讯录-->
	<div class="share-address-list" style="display: none" id="selectMember">
		<div class="share-address-content">
			<input type="hidden" id="parentDeptId" value="0" />
			<div class="return-father" onclick="returnBack()">
				<div class="historyBack-return">返回</div>
			<!-- 	<b>|</b> <span id="department"></span> -->
			</div>
			<ul id="shareList">

			</ul>
		</div>

		<div class="addshare-member-tail" style="display: none"
			id="preAddMemberDiv">
			<ul>
				<div class="member-tail-buttons" onclick="comfireMenber()">
					<div class="member-tail-button">确定</div>
				</div>
				<div id="preAddMember"></div>
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
            <a class="weui-icon-delete" onclick="deleteMember('${teamId}', {{id}})"></a>
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
		var iNodeId = "${nodeId}";
		var teamId = " ${ownerId}";
		var $puttingAppend = $('.putting-append');
		$(function() {
			//增加关闭功能
			$("#searchClose").on("click", function(e) {
				$(e.target).parents(".staff-picker").hide();
			});

			$('#searchInput').bind('input propertychange', function(e) {
				var keyword = $("#searchInput").val();
				if (keyword !== "") {
					initStaffList(keyword);
				}
			});
			initDataList(1);
		});

		$puttingAppend.on('click', function() {
			$('.putting-out').hide();
			$('#recentContacts').show();
			$('.tabbar').hide();
		})

		function initDataList(curPage) {
			var url = "${ctx}/teamspace/file/grantAuthority/" + ownerId + "/"+ iNodeId;
			var params = {
				"page" : curPage,
				"pageSize" : 100,
				"token" : "${token}",
				"keyword" : ""
			};
			$.ajax({
						type : "POST",
						url : url,
						data : params,
						error : function(request) {
							$.toast('<spring:message code="inviteShare.listUserFail"/>','cancel');
						},
						success : function(data) {
							var $list = $("#spaceMemberList");
							$list.children().remove();
							var $template = $("#members");
							for (var i = 0; i < data.content.length; i++) {
								var member = data.content[i];
								if (member.role == "auther") {
									if (member.teamRole == "admin") {
										member.role = "拥有者";
									} else {
										member.role = "管理员";
									}
								} else if (member.role == "previewer") {
									member.role = "预览";
								} else if (member.role == "uploadAndView") {
									member.role = "预览 上传 下载";
								} else if (member.role == "downLoader") {
									member.role = "预览  下载";
								} else if (member.role == "uploader") {
									member.role = "预览  上传";
								}

								var thisDiv = $template.template(member);
								thisDiv.data("user", member);
								thisDiv.appendTo($list);
							}
							haseSharedMember="|";
							fullSharedMenber(data.content);
							
							if (data.content.length != 0) {
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
				"ownerId" : "${ownerId}",
				"token" : "${token}",
				"userNames" : keyWord
			};
			$
					.ajax({
						type : "POST",
						url : url,
						data : params,
						error : function(request) {
							$
									.toast(
											'<spring:message code="inviteShare.listSharedUserFail"/>',
											"cancel");
						},
						success : function(data) {
							var $list = $("#pickerMemberList");
							$list.children().remove();

							var $template = $("#pickerMemberTemplate");
							for (var i = 0; i < data.successList.length; i++) {
								console.debug(data);
								var member = data.successList[i];
								$template.template(member).appendTo($list);
							}
						}
					});

		}

		function userSelect(th, type) { //选中或取消删除对象按钮的点击事件
			$("#deleteMemberDiv").css("display", "block");
			if ($(th).attr("class") == "M-addblank") {
				$(th).removeClass("M-addblank");
				$(th).addClass("M-active");
				fullMemberDiv($(th).attr("value"),$(th).attr("userId"), type);
			} else {
				$(th).removeClass("M-active");
				$(th).addClass("M-addblank");
				removeMemberDiv($(th).attr("value"), type)
			}
			window.event.stopPropagation();
			tailNameSlides()
		}

		function tailNameSlides() {
			$('#deleteMemberDiv ul').width(window.screen.width - 100);
			$('.deleteMemberDiv-boy').width(
					($('.deleteMemberDiv-boy li').length + 1)
							* $('.deleteMemberDiv-boy li').width());
		}

		function removeMemberDiv(user, type) {
			$("#d" + user).remove();
		}

		function fullMemberDiv(id,userId, type) {
			var html = "";
			html = html + "<li id=\"d" + id + "\" value=" + id
					+ " onclick=\"cancelDeleteMember(" + id + "," + type
					+ ")\">";
			html = html
					+ "<i><img src=\"${ctx}/userimage/getUserImage/"+userId+"\"/></i>";
			html = html + "</li>";
			$("#deleteMemberDiv .deleteMemberDiv-boy").append(html);
		}

		function selectPermissions(o, role) {
			var data = $(o).parent().parent().data("user");
			if (role == '拥有者') {
				$.toast("不能修改拥有者权限");
			} else {
				$('#updatePermission').show();
				$('#updatePermission').data("user", data);
				$('.putting-out').hide();

				if (data.role == "预览  上传") {
					$("#update_preview").removeClass("M-addblank");
					$("#update_preview").addClass("M-active");
				} else if (data.role == "预览  下载") {
					$("#update_download").removeClass("M-addblank");
					$("#update_download").addClass("M-active");
				} else if (data.role == "预览 上传 下载") {
					$("#update_upload").removeClass("M-addblank");
					$("#update_upload").addClass("M-active");
				}
			}
		}

		$('#share-second-tail-content li').click(function() {
			if ($(this).children('i').hasClass('M-addblank')) {
				$(this).children('i').addClass('M-active');
				$(this).children('i').removeClass('M-addblank')
			} else {
				$(this).children('i').addClass('M-addblank');
				$(this).children('i').removeClass('M-active')
			}
		})

		function addUserMember(e, teamId, loginName, cloudUserId, email) {
			var item = "[user]" + loginName + "[" + cloudUserId + "]" + email;
			addMember(teamId, item, function(r) {
				if (r === "ok") {
					$(e).addClass("operation-success");
				} else {
					$(e).addClass("operation-fail");
				}
			});
		}

		function addMember(teamId, teamMember, callback) {
			var url = "${ctx}/teamspace/member/addMember";
			var data = {
				cloudUserIds : getTrunkData(teamMember),
				teamId : teamId,
				authType : "auther",//TODO: 指定authType
				token : "${token}"
			};

			var r = "fail";
			$
					.ajax({
						type : "POST",
						url : url,
						data : data,
						error : function(request) {
							$
									.toast(
											"<spring:message code='operation.failed'/>",
											"cancel");
						},
						success : function(data) {
							if (data == "OK") {
								r = "ok";
								$
										.toast("<spring:message code='operation.success'/>");
							} else if (data == "P_OK") {
								r = "ok";
								$
										.toast("<spring:message code='teamSpace.error.addMemberpartly'/>");
							} else if (data == "NoSuchTeamspace") {
								$
										.toast(
												"<spring:message code='teamSpace.error.NoFound'/>",
												"cancel");
							} else if (data == "AbnormalTeamStatus") {
								$
										.toast(
												"<spring:message code='teamSpace.error.AbnormalTeamStatus'/>",
												"cancel");
							} else if (data == "NoSuchUser") {
								$
										.toast(
												"<spring:message code='teamSpace.error.NoSuchUser'/>",
												"cancel");
							} else if (data == "ExistMemberConflict") {
								$
										.toast(
												"<spring:message code='teamSpace.error.ExistMemberConflict'/>",
												"cancel");
							} else if (data == "ExceedTeamSpaceMaxMemberNum") {
								$
										.toast(
												"<spring:message code='teamSpace.error.ExceedMemberMax'/>",
												"cancel");
							} else if (data == "Forbidden") {
								$
										.toast(
												"<spring:message code='teamSpace.error.Forbidden'/>",
												"forbidden");
							} else if (data == "InvalidTeamRole") {
								$
										.toast(
												"<spring:message code='teamSpace.error.InvalidTeamRole'/>",
												"cancel");
							} else if (data == "InvalidPermissionRole") {
								$
										.toast(
												"<spring:message code='teamSpace.error.InvalidPermissionRole'/>",
												"cancel");
							} else {
								$
										.toast(
												"<spring:message code='operation.failed'/>",
												"cancel");
							}
							initDataList(1);
						}
					});

			if (callback != undefined && typeof callback === "function") {
				callback(r);
			}
		}

		function cancelDeleteMember(user) { //页尾选中头像的点击事件
			$("#d" + user).remove();
			$("#ww" + user).addClass("M-addblank");
			$("#ww" + user).removeClass("M-active");
			if ($('#deleteMemberDiv li').length == 0) {
				$('#deleteMemberDiv').css('display', 'none');
			}
			sf()
		}

		function sf() {
			if ($('#deleteMemberDiv').css('display') == 'none') {
				$('.sharedmembers-content').css('bottom', 0);
			} else {
				$('.sharedmembers-content').css('bottom', 2.2 + 'rem');
			}
		}

		function deleteMember() {
			for (var i = 0; i < $('.deleteMemberDiv-boy li').length; i++) {
				var aclId = $('.deleteMemberDiv-boy li').eq(i).attr('id').substring(1);
				var url = "${ctx}/teamspace/file/deleteFolderAuth";
				var data = {
		            ownerId : teamId,
		            aclId : aclId,
					token : "<c:out value='${token}'/>"
				};
				$.ajax({
							type : "POST",
							url : url,
							data : data,
							async : false,
							error : function(request) {
								$.toast("<spring:message code='operation.failed'/>","cancel");
							},
							success : function(data) {
								if (typeof (data) == 'string'
										&& data.indexOf('<html>') != -1) {
									window.location.href = "${ctx}/logout";
									return;
								}
								if (data == "OK") {
									$.toast("删除成功",function(){
										initDataList(1);
									});
								}
							}
						});
			}

			$('.deleteMemberDiv-boy li').remove();
			$('#deleteMemberDiv').hide();
			$('#deleteMemberBtn').text('删除成员');
			$('#spaceMemberList .content').children('h2').hide();
			sf()
		}

		function cancelUpdate() {
			$('#share-second-tail-content li').children('i').removeClass(
					'M-active');
			$('#share-second-tail-content li').children('i').addClass(
					'M-addblank');
			$('#addshare4').hide();
			$('.putting-out').show();
		}
		function listSharedUser() {
			$("#sharedDiv").empty();
			var params = {
				"pageNum" : 1,
				"pageSize" : 20,
				"keyWord" : "",
				"token" : "<c:out value='${token}'/>"
			};
			$.ajax({
				type : "POST",
				data : params,
				url : "${ctx}/teamspace/member/openMemberMgr/" + teamId,
				error : function(request) {
					//      	$.toast("获取共享用户失败");
				},
				success : function(data) {
					var users = data.content;
					fullShareDiv(users);
					if (users.length > 0) {
						$('.putting-blank-background').css('display', 'none');
					} else {
						$('.putting-blank-background').css('display', 'block');
					}

				}
			})
		}

		function getDeleteUserIds() {
			var ids = "";
			$("#deleteMemberDiv").find("li").each(function() {
				var did = $(this).attr("id");
				ids = ids + did.replace("d", "") + ",";
			});
			return ids;
		}

		function showDeleteSpan() {
			$('#deleteMemberDiv ul').width(window.screen.width - 100);
			if ($('#deleteMemberBtn').text() == "取消删除成员") {
				$('#deleteMemberBtn').text('删除成员');
				$('#spaceMemberList li').children('.content').children('h2')
						.hide();
				$('#spaceMemberList li').children('.content').children('h2')
						.addClass("M-addblank");
				$('#spaceMemberList li').children('.content').children('h2')
						.removeClass("M-active");
				$('#deleteMemberDiv li').remove();
				$('#deleteMemberDiv').hide();
				sf()

			} else {
				$('#deleteMemberBtn').text('取消删除成员');
				$('#spaceMemberList li').children('.content').children('h2')
						.show();
				$('#deleteMemberDiv').show();
				sf()
			}
		}

		function updateMember() {
			var user = $('#updatePermission').data("user");
			var url = ctx+"/teamspace/file/changeFolderAuth";
			var data = {
					ownerId : user.ownerId,
					nodeId : user.nodeId,
					userId  :user.userId,
					aclId:user.id,
				    authType : getUpdateRoleName(),//TODO: 指定authType
				    token : "${token}"
			};
			$.ajax({
				type : "POST",
				url : url,
				data : data,
				error : function(request) {

				},
				success : function(data) {
					$.toast("修改成功", function() {
						window.location.reload();
				    });
				
					
				}
			});
		}
		function getUpdateRoleName() {
			var roleName = "";
			var preview = $("#update_preview").attr("class") == "M-active" ? true
					: false;
			var download = $("#update_download").attr("class") == "M-active" ? true
					: false;
			var upload = $("#update_upload").attr("class") == "M-active" ? true
					: false;
			if (download == true) {
				if (upload == true) {
					if (preview == true) {
						roleName = "uploadAndView";
					} else {
						roleName = "uploadAndView";
					}
				} else {
					if (preview == true) {
						roleName = "downLoader";
					} else {
						roleName = "downLoader";
					}
				}
			} else {
				if (upload == true) {
					if (preview == true) {
						roleName = "uploader";
					} else {
						roleName = "uploader";
					}
				} else {
					if (preview == true) {
						roleName = "previewer";
					} else {
						roleName = "previewer";
					}
				}
			}
			return roleName;
		}

		/*企业通讯录*/

		var allMessageTo = [];
		var selectMenbers = {};
		var haseSharedMember = "|";
		var teamId = "${teamId}";

		function comfireMenber() {
			if ($('#preAddMember li').length > 0) {
				$('#addMember').css('display', 'block');
				$('#selectMember').css('display', 'none');
			} else {
				$.toast("请选择要添加的成员", 400);
			}
		}

		function roleSelect(th) {
			if ($(th).attr("class") == "M-addblank") {
				$(th).removeClass("M-addblank");
				$(th).addClass("M-active");

			} else {
				$(th).removeClass("M-active");
				$(th).addClass("M-addblank");
			}
		}

		function getRoleName() {
			var roleName = "";
			var preview = $("#add_preview").attr("class") == "M-active" ? true: false;
			var download = $("#add_download").attr("class") == "M-active" ? true: false;
			var upload = $("#add_upload").attr("class") == "M-active" ? true: false;
			if (download == true) {
				if (upload == true) {
					if (preview == true) {
						roleName = "uploadAndView";
					} else {
						roleName = "uploadAndView";
					}
				} else {
					if (preview == true) {
						roleName = "downLoader";
					} else {
						roleName = "downLoader";
					}
				}
			} else {
				if (upload == true) {
					if (preview == true) {
						roleName = "uploader";
					} else {
						roleName = "uploader";
					}
				} else {
					if (preview == true) {
						roleName = "previewer";
					} else {
						roleName = "previewer";
					}
				}
			}
			return roleName;
		}

		function showDiv(div) {
			$("#addMember").css("display", "none");
			$("#recentContacts").css("display", "none");
			$("#selectMember").css("display", "none");
			$("#" + div).css("display", "block");
		}

		function fullSharedMenber(members, tag) {
			for ( var key in members) {
				haseSharedMember = haseSharedMember + "u"+ members[key].userId + "|";
			}
			listSharedUser();
			addDeleteBadge($("#shareContent"));
		}

		function addDeleteBadge($target) {
			new Hammer($target[0])
					.on('press',
							function(e) {
								var $children = $(e.target).children();
								if ($children.size() == 0) {
									return;
								}
								$('.share-second').on("click", function() {
									$target.find(".badge-action").remove();
									$mask.remove();
								});
								$(e.target).children().each(
												function() {
													var $badge = $('<i class="weui-icon-cancel badge-action"></i>').appendTo($(this));
													$badge.on("click",function(e) {
																		e.stopPropagation();
																		var $li = $(this).parent().parent();
																		var node = $li.data("data");
																		$li.remove();
																		$mask.remove();
													})
												});
							});
		}


		function fullShareDiv(datas) {
			$("#shareList").empty();
			for (var i = 0; i < datas.length; i++) {
				if (datas[i].teamRole == "admin") {
					continue;
				}
				var html = "";
				html += "<li>";
				if(haseSharedMember.indexOf("|u"+datas[i].userId+"|")<0){
					   html=html+"<i class=\"M-addblank\" onclick=selectIcon(this) id='p-"+datas[i].id+"'></i>";
				}else{
					   html=html+"<i class=\"M-inactive\" ></i>";
				}
								
			/* 	html = html+ "<i class=\"M-addblank\" onclick=selectIcon(this) id='p-"+ datas[i].id + "'></i>"; */
				html = html+ "<p><img src=\"${ctx}/userimage/getUserImage/"+datas[i].userId+"\"/></p>";
		        html = html + "<span>" + datas[i].username + "</span>";
				html = html + "</li>";
				$("#shareList").append(html);
				$("#p-" + datas[i].id).data("userInfo", datas[i]);
				$("#p-" + datas[i].id).click(function(event) {
					event.stopPropagation();
				});
			}

		}
		function addPreMember(member) {
			var html = "";
			html = html + "<li id=\"pre" + member.id
					+ "\" onclick=\"clearShareMember('" + member.id + "')\">";
			html += "<i><img src=\"${ctx}/userimage/getUserImage/"+member.userId+"\"/></i>";
			html = html + "</li>";
			$("#preAddMember").append(html);

		}
		function selectIcon(th) {
			var member = $(th).data("userInfo");
			if (typeof (member) == "undefined") {
				$.toast("已添加", 400);
				return;
			}
			if ($(th).attr("class") == "M-addblank") {
				$(th).removeClass("M-addblank");
				$(th).addClass("M-active");
				if ($("#preAddMemberDiv").css("display") == "none") {
					$("#preAddMemberDiv").css("display", "block");
				}
				addPreMember(member);
				setSelectMenber(member);

			} else {
				$(th).removeClass("M-active");
				$(th).addClass("M-addblank");
				$("#pre" + member.id).remove();
				$("#active-" + member.id).remove();
			}
			tailNameSlide()
		}
		
		function setSelectMenber(member){
			   var html="";
			   html=html+"<li id=\"active-"+member.id+"\">";
			   html+="<p><img src=\""+ctx+"/userimage/getUserImage/"+member.userId+"\"/></p>";
			   html+="<h1>"+member.username+"</h1>";
			   html=html+"</li>";
			   $("#shareContent").prepend(html);
			   $("#active-"+member.id).data("data",member);
	  
	   }

		function submitMenber() {
			$("#shareContent").children().each(
					function(i, n) {
						var obj = $(n);
						if (obj.attr("id") != undefined
								&& obj.attr("id").indexOf("active") > -1) {
							var item = obj.data("data");
							addMessageTo(item.id, item.username, "user",item.username, item.email);
						}

					});
			addMember();
		}

		function addMessageTo(memberId, loginName, userType, userName,userEmail) {
			allMessageTo.push(memberId);

		}

		function addMember() {
			var url = "${ctx}/teamspace/file/grantAuthToFolder";
			var data = {
				'users' : getUsersString(allMessageTo),
				'ownerId' : ownerId,
				'nodeId' : iNodeId,
				'authType' : getRoleName(),
				'token' : "${token}"
			};
			$.ajax({
						type : "POST",
						url : url,
						data : data,
						error : function(request) {
							$.toast("<spring:message code='operation.failed'/>","cancel");
						},
						success : function(data) {
							$.toast("添加成功",function(){
								window.location.reload();
							});
							
						}
					});

		}

		function getUsersString(dataArray) {
			var result = "";
			for (var i = 0; i < dataArray.length; i++) {
				if (dataArray[i] != "") {
					if(i==(dataArray.length-1)){
						result =result+ dataArray[i]
					}else{
						result =result+ dataArray[i]+";"
					}
				}
			}
			return result;
		}
		
		

	    function modifyNodeIsVisible(isshow) {
	        var url = "${ctx}/teamspace/file/modifyNodeIsVisible/" + ownerId + "/" + iNodeId;
	        var data = {
	            isavalible: isshow,
	            token: "<c:out value='${token}'/>"
	        };

	        $.ajax({
	            type: "POST",
	            url: url,
	            data: data,
	            error: function (request) {
	                handlePrompt("error",
	                    "<spring:message code='operation.failed'/>");
	            },
	            success: function (data) {
	            	$.toast("设置成功");
	            }
	        });
	    }
	    
	    function returnBack(){
	    	$("#selectMember").css("display","none");
	    	$("#mainBody").css("display","block");
	    	
	    }
	    
	    $("#secretSwitch").bind("click", function () {
          if($("#secret").val()=="off"){
              $("#secret").val("on");
              if(init){
            	  init=false;
              }else{
	              $.confirm("您确定要修改当前文件夹为私密吗?", "设置", function() {
	            		modifyNodeIsVisible(1);
	                }, function() {
	                  //取消操作
	                });
              }
          }else{
              $("#secret").val("off");
              if(init){
            	  init=false;
              }else{
	              $.confirm("您确定要修改当前文件夹为公开吗?", "设置", function() {
	            	  modifyNodeIsVisible(0);
	                }, function() {
	                  //取消操作
	               });
              }
          }

      });
	  var init=true;
	    function isVisibleNodeACL(ownerId,nodeId) {
	        var url = ctx + "/teamspace/file/getIsVisibleNodeACL/"+ownerId+"/"+nodeId;
	        var data = {
	            token: '${token}'
	        }
	        $.ajax({
	            type: "GET",
	            url: url,
	            async:true,
	            data: data,
	            error: function (request) {
	            },
	            success: function (data) {
	              if(data=='true'){
	            	$("#secretSwitch").click();
	              }else{
	            	  init=false;
	              }
	            }
	        });
	    }
	    isVisibleNodeACL(ownerId,iNodeId);
	</script>
</body>
</html>
