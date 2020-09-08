<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
<%@ include file="../common/include.jsp"%>
<link rel="stylesheet" type="text/css"
	href="${ctx}/static/skins/default/css/share/inviteShare.css" />
<link rel="stylesheet" type="text/css"
	href="${ctx}/static/skins/default/css/teamSpace/memberMgr.css" />
<title>权限管理</title>
<script src="${ctx}/static/js/common/enterprise-directory.js"></script>
</head>

<body>
	<div class="putting-out" id="mainBody">
		<div class="putting-background"></div>
		<div class="share-homepage-header">
			<div class="share-homepage-top">
				<span> <img
					src="${ctx}/static/skins/default/img/icon/folder-icon.png" />
				</span>
			</div>
			<div class="share-homepage-right">
				<div class="share-homepage-middle">
					<i style="font-size: 0.75rem; line-height: 2rem">${fileName}</i>
				</div>

			</div>
			<div class="share-homepage-header-icon"></div>
		</div>
		<div class="weui-cell weui-cell_switch">
			<input hidden="hidden" id="secret" name="secret" type="radio"
				value="off" checked="checked" />
			<div class="weui-cell__bd" style="font-size: 0.75rem;">开启后,非授权成员将限制访问</div>
			<div class="weui-cell__ft" style="margin-right: 0rem;">
				<input class="weui-switch" id="secretSwitch" type="checkbox">
			</div>
		</div>
		<div class="not-per-team" style="display: none">
			<li><img src='${ctx}/static/skins/default/img/iconblack_09.png' />
				<p>暂无成员,请到协作空间添加成员。</p></li>
		</div>
		<div id="noname" style="display: none">
			<p style="font-size: 0.5rem; padding: 0 15px; color: red;">暂无访问成员,可在下方列表快速添加，列表显示协作成员。</p>
		</div>
		<div class="putting-append" style="display: none">
			<i> <img src="${ctx}/static/skins/default/img/putting-apped.png" />
			</i> <span>添加成员</span>
		</div>
		<div class="fillBackground"></div>
		<!--共享成员通讯录-->
		<div class="share-address-list" id="selectMember">
			<div class="share-address-contents" style="margin-top: -40px;">
				<input type="hidden" id="parentDeptId" value="0" />
				<ul id="shareList" class="shareList-team">
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
		<div id="appendname" style="display: none">
			<div class="putting-sharedmembers">
				<span>已添加成员</span>
				<p onclick="showDeleteSpan()">
					<span id="deleteMemberBtn" style="display: none;">删除成员</span> <i
						style="display: none;"> <img
						src="${ctx}/static/skins/default/img/sharedmembers-delete.png" />
					</i>
				</p>
			</div>
			<div class="sharedmembers-content"
				style="position: fixed; top: 11.1rem; bottom: 0rem;">
				<ul id="spaceMemberList">
				</ul>
				<script id="members" type="text/template7">
							<li id='member_{{id}}'>
								<div class="content">
									{{#js_compare "this.role=='拥有者'"}} {{else}}
									<h2 class="M-addblank" id="ww{{id}}" style="display:none" userType="{{user.type}}" data-type="{{teamId}}" name="deleteSpan" value="{{id}}" userId="{{user.id}}"
									    onclick="userSelect(this)"></h2>
									{{/js_compare}}
									{{#js_compare "this.user.type=='user'"}}
									<i><img src="${ctx}/userimage/getUserImage/{{user.id}}"/></i>
									{{else}}
									<i><img src="${ctx}/static/skins/default/img/department-icon.png"/></i>
									{{/js_compare}}
									<h1>{{user.name}}</h1>
									{{#js_compare "this.role=='拥有者'"}}
									<p onclick="selectPermissions(this,'{{role}}')">{{role}}</p>
									{{else}}
									<p onclick="selectPermissions(this,'{{role}}')">{{role}}
										<img src="${ctx}/static/skins/default/img/putting-more.png" />
									</p>
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
		<div class="access" id="selectMemberS">
			<div class="access-share-address-content" style="margin-top: -60px;">
				<!-- <span>已添加成员</span> -->
				<ul id="accessList"></ul>
				<script id="accessmembers" type="text/template7">
							<li id='member_{{id}}'>
								<div class="content">
									{{#js_compare "this.role=='拥有者'"}} {{else}}
									<h2 class="M-addblank" id="ww{{id}}" style="display:none" data-type="{{teamId}}" name="deleteSpan" value="{{id}}" userId="{{member.id}}"
									    onclick="userSelect(this)"></h2>
									{{/js_compare}}
									{{#js_compare "this.member.type=='user'"}}
										<i><img src="${ctx}/userimage/getUserImage/{{member.id}}" /></i>
									{{else}}
									<i>
										<img src="${ctx}/static/skins/default/img/department-icon.png" />
									</i>
									{{/js_compare}}
									<h1>{{member.name}}</h1>
									{{#js_compare "this.role=='拥有者'"}}
									<p>{{role}}</p>
									{{else}}
									<p>{{role}}
									</p>
									{{/js_compare}}
								</div>
							</li>
						</script>
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
		<div class="share-second"
			style="display: none; height: auto; position: fixed; left: 0; right: 0; top: 8.5rem; bottom: 1rem; overflow-y: auto"
			id="addMember">
			<div class="share-second-header">
				<span id="totalMember">添加的成员</span>
			</div>
			<div class="share-second-kong"></div>
			<div class="share-second-content" style="">
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
						<li><span> <i class="M-inactive" id="add_preview"></i>
								<p>预览</p>
						</span></li>
						<li onclick="roleSelect(this)"><span> <i
								class="M-active" id="add_download"></i>
								<p>下载</p>
						</span></li>
						<li onclick="roleSelect(this)"><span> <i
								class="M-active" id="add_upload"></i>
								<p>上传</p>
						</span></li>
					</ul>
				</div>
			</div>
			<a href="javascript:;" class="shaerDetermine shaerDetermines"
				onclick="submitMenber()">确定</a>
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
					var ownerId = "${ownerId}";
					var $puttingAppend = $('.putting-append');
					$(function () {
						$('#mainBody').css('display', 'block');
						//增加关闭功能
						$("#searchClose").on("click", function (e) {
							$(e.target).parents(".staff-picker").hide();
						});

						$('#searchInput').bind('input propertychange', function (e) {
							var keyword = $("#searchInput").val();
							if (keyword !== "") {
								initStaffList(keyword);
							}
						});
						isVisibleNodeACL(ownerId, iNodeId);
						initDataList(1);

					});

					$puttingAppend.on('click', function () {
						$('#selectMember').show();
						$('#preAddMemberDiv').show();
						$('#appendname').hide();
						$('.tabbar').hide();
						$('#mainBody').show();
					})

					function initDataList(curPage) {
						var url = host + "/ufm/api/v2/acl/"+ownerId;
						var params = {
							"nodeId":iNodeId,
							"limit": 100,
							"offset":0
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
								for (var i = 0; i < data.acls.length; i++) {
									if (data.acls.length > 0) {
										$('#selectMemberS').hide();
										$('#selectMember').hide();
									}
									var member = data.acls[i];
									if (member.role == "auther") {
										if (member.teamRole == "admin") {
											member.role = "拥有者";
										} else {
											member.role = "管理员";
										}
									} else if (member.role == "previewer") {
										member.role = "预览";
									}  else if (member.role == "uploadAndView") {
										member.role = "预览 上传 下载";
									} else if (member.role == "viewer") {
										member.role = "预览  下载";
									} else if (member.role == "uploader") {
										member.role = "预览  上传";
									}
									var thisDiv = $template.template(member);
									thisDiv.data("user", member);
									thisDiv.appendTo($list);
								}
								haseSharedMember = "|";
								fullSharedMenber(data.acls);

								if (data.acls.length != 0) {
									$('#deleteMemberBtn').show();
									$('#deleteMemberBtn').siblings("i").show();
								}
								if ($("#secret").val() == "off") {
									$('#selectMember').hide();
									$('.access').show();
									$('#noname').css('display', 'none');
									$('.putting-append').css('display', 'none')

								} else if ($("#secret").val() == "on") {
									if (data.acls.length > 0) {
										$('.putting-append').show();
										$('#appendname').show();
										$('#selectMember').hide();
										$('#noname').css('display', 'none');
									} else {

										$('#appendname').hide();
										$('#selectMember').show();
										$('#selectMemberS').hide();
										$('#noname').css('display', 'block');
										$('.putting-append').css('display', 'none')
									}

								}
								accessUser()
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
						$
							.ajax({
								type: "POST",
								url: url,
								data: params,
								error: function (request) {
									$
										.toast(
											'<spring:message code="inviteShare.listSharedUserFail"/>',
											"cancel");
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

					function userSelect(th, type) { //选中或取消删除对象按钮的点击事件
						$("#deleteMemberDiv").css("display", "block");
						if ($(th).attr("class") == "M-addblank") {
							$(th).removeClass("M-addblank");
							$(th).addClass("M-active");
							fullMemberDiv($(th).attr("value"), $(th).attr("userId"), type,$(th).attr("userType"));
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
							($('.deleteMemberDiv-boy li').length + 1) *
							$('.deleteMemberDiv-boy li').width());
					}

					function removeMemberDiv(user, type) {
						$("#d" + user).remove();
					}

					function fullMemberDiv(id, userId, type, userType) {
						var html = "";
						html = html + "<li id=\"d" + id + "\" value=" + id +
							" onclick=\"cancelDeleteMember(" + id + "," + type +
							")\">";
                        if(userType=="user"){
                            html = html +
                            "<i><img src=\"${ctx}/userimage/getUserImage/" + userId + "\"/></i>";
                        } else {
                            html = html +
                            "<i><img src=\"${ctx}/static/skins/default/img/department-icon.png\"/></i>";
                        }

						html = html + "</li>";
						$("#deleteMemberDiv .deleteMemberDiv-boy").append(html);
					}

					function selectPermissions(o, role) {
						var data = $(o).parent().parent().data("user");
						if (role == '拥有者') {
							$.toast("不能修改拥有者权限");
						} else {
							$('#mainBody').css('display', 'block')
							$('#updatePermission').show();
							$('#updatePermission').data("user", data);
							$('.putting-out').show();
							$('#appendname').css('display', 'none')
							$('#selectMemberS').css('display', 'none')

							if (data.role == "预览  上传") {
								$("#update_preview").removeClass("M-addblank");
								$("#update_preview").addClass("M-active");
							} else if (data.role == "预览  下载") {
								$("#update_download").removeClass("M-addblank");
								$("#update_download").addClass("M-active");
							} else if (data.role == "预览 上传 下载") {
								$("#update_upload").removeClass("M-addblank");
								$("#update_upload").addClass("M-active");
                                $("#update_download").removeClass("M-addblank");
                                $("#update_download").addClass("M-active");
							}
						}
					}

					$('#share-second-tail-content li').click(function () {
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
						addMember(teamId, item, function (r) {
							if (r === "ok") {
								$(e).addClass("operation-success");
							} else {
								$(e).addClass("operation-fail");
							}
						});
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
							var url = host + "/ufm/api/v2/acl/"+ownerId+"/"+aclId;
							$.ajax({
								type: "DELETE",
								url: url,
								async: false,
								error: function (request) {
									$.toast("<spring:message code='operation.failed'/>", "cancel");
								},
								success: function (data) {
									$.toast("删除成功", function () {});
									initDataList();
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
						$('#addshare').hide();
						$('.putting-out').show();
						$('#updatePermission').hide();
						$("#appendname").show();
					}

					function listSharedUser() {
						$("#sharedDiv").empty();
						var params = {
						    "keyword": ""
						};
						var url=host+"/ufm/api/v2/teamspaces/"+ownerId+"/memberships/items"
						$.ajax({
							type: "POST",
							data: JSON.stringify(params),
							url: url,
							error: function (request) {
							},
							success: function (data) {
								var users = data.memberships;
								fullShareDiv(users);
								if (users.length > 0) {
									$('.putting-blank-background').css('display', 'none');
								} else {
									$('.putting-blank-background').css('display', 'block');
								}

							}
						})
					}

					function accessUser() {
						$("#sharedDiv").empty();
					    var url = host+"/ufm/api/v2/teamspaces/"+ownerId+"/memberships/items";
				        var params = {
				            "keyword": ""
				        };
					    $.ajax({
					            type: "POST",
					            url: url,
					            data: JSON.stringify(params),
							error: function (request) {
								//      	$.toast("获取共享用户失败");
							},
							success: function (data) {
								var users = data.memberships;
								if (users.length > 0) {
									$('.putting-blank-background').css('display', 'none');
								} else {
									$('.putting-blank-background').css('display', 'block');
								}
								var $list = $('#accessList');
								$list.children().remove()
								var $template = $("#accessmembers");
								for (var i = 0; i < users.length; i++) {
									var member = users[i];
									if(member.teamspace.type==1){
										if(member.member.type=="department"){
											continue;
										}
									}
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
									} else if (member.role == "viewer") {
										member.role = "预览  下载";
									} else if (member.role == "uploader") {
										member.role = "预览  上传";
									}
									var thisDiv = $template.template(member);
									thisDiv.data("user", member);
									thisDiv.appendTo($list);
								}

							}
						})
					}

					function accessDiv(datas) {
						$("#accessList").empty();
						for (var i = 0; i < datas.length; i++) {
							if (datas[i].teamRole == "admin") {
								continue;
							}
							var html = "";
							html += "<li>";

							/* 	html = html+ "<i class=\"M-addblank\" onclick=selectIcon(this) id='p-"+ datas[i].id + "'></i>"; */
							html = html + "<p><img src=\"${ctx}/userimage/getUserImage/" + datas[i].userId + "\"/></p>";
							html = html + "<span>" + datas[i].username + "</span>";
							html = html + "</li>";
							$("#accessList").append(html);
							$("#p-" + datas[i].id).data("userInfo", datas[i]);
							$("#p-" + datas[i].id).click(function (event) {
								event.stopPropagation();
							});
						}

					}

					function getDeleteUserIds() {
						var ids = "";
						$("#deleteMemberDiv").find("li").each(function () {
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
						var url = host + "/ufm/api/v2/acl/"+user.resource.ownerId+"/"+user.id;
						var data = {
							role: getUpdateRoleName()//TODO: 指定authType
						};
						$.ajax({
							type: "PUT",
							url: url,
							data: JSON.stringify(data),
							error: function (request) {

							},
							success: function (data) {
								$.toast("修改成功");
                                window.location.reload();
							}
						});
					}

					function getUpdateRoleName() {
						var roleName = "";
						var preview = $("#update_preview").attr("class") == "M-active" ? true : false;
						var download = $("#update_download").attr("class") == "M-active" ? true : false;
						var upload = $("#update_upload").attr("class") == "M-active" ? true : false;
						if (download == true) {
							if (upload == true) {
								if (preview == true) {
									roleName = "uploadAndView";
								} else {
									roleName = "uploadAndView";
								}
							} else {
								if (preview == true) {
									roleName = "viewer";
								} else {
									roleName = "viewer";
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

					function comfireMenber() {
						if ($('#preAddMember li').length > 0) {
							$('#addMember').css('display', 'block');
							$('#selectMember').css('display', 'none');
						} else {
							$('#selectMember').css('display', 'block');
							$.toast("请选择要添加的成员", 400);
						}
					}

					function roleSelect(th) {
						if ($(th).find("i").attr("class") == "M-addblank") {
							$(th).find("i").removeClass("M-addblank");
							$(th).find("i").addClass("M-active");

						} else {
							$(th).find("i").removeClass("M-active");
							$(th).find("i").addClass("M-addblank");
						}
					}

					function getRoleName() {
						var roleName = "";
						var preview = $("#add_preview").attr("class") == "M-active" ? true : false;
						var download = $("#add_download").attr("class") == "M-active" ? true : false;
						var upload = $("#add_upload").attr("class") == "M-active" ? true : false;
						if (download == true) {
							if (upload == true) {
								if (preview == true) {
									roleName = "uploadAndView";
								} else {
									roleName = "uploadAndView";
								}
							} else {
								if (preview == true) {
									roleName = "viewer";
								} else {
									roleName = "viewer";
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
						$("#selectMember").css("display", "none");
						$("#" + div).css("display", "block");
					}

					function fullSharedMenber(members, tag) {
						for (var key in members) {
							haseSharedMember = haseSharedMember + members[key].user.type + members[key].user.id + "|";
						}
						listSharedUser();
						<%--addDeleteBadge($("#shareContent"));--%>
					}
					
					function fullShareDiv(datas) {
						$("#shareList").empty();
						if(datas.length == 1){
							var html = "";
							html += "<i style='display: block;text-align: center;font-style: normal;margin-top: 4rem'>";
							html = html + "<img style=\'width:5rem; \' src='${ctx}/static/skins/default/img/iconblack_09.png'/>";
							html = html + "<p style='color: #666;line-height: 1.5rem'>暂无成员,请到协作空间添加成员。</p>";
							html = html + "</i>";
							$("#shareList").append(html);
						}
						for (var i = 0; i < datas.length; i++) {

							if (datas[i].teamRole == "admin") {
								continue;
							}
							if(datas[i].teamspace.type==1){
								if (datas[i].member.type== "department") {
									continue;
								}
							}
							var member=datas[i].member;
							var html = "";
							html += "<li onclick=selectIcon(this)>";
							if (haseSharedMember.indexOf("|" +member.type+ member.id + "|") < 0) {
								html = html + "<i class=\"M-addblank\" id='p-" + member.id + "'></i>";
							} else {
								html = html + "<i class=\"M-inactive\" ></i>";
							}
                            if(member.type=="user"){
                                html = html + "<p><img src=\"${ctx}/userimage/getUserImage/" + member.id + "\"/></p>";
                            } else {
                                html = html + "<p><img src=\"${ctx}/static/skins/default/img/department-icon.png\"/></p>";
                            }
							html = html + "<span>" + member.name + "</span>";
							html = html + "</li>";
							$("#shareList").append(html);
							$("#p-" + member.id).data("userInfo", member);
							$("#p-" + member.id).click(function (event) {
								<%--event.stopPropagation();--%>
							});
						}

					}

					function addPreMember(member) {
						var html = "";
						html = html + "<li id=\"pre" + member.id +
							"\" onclick=\"clearShareMember('" + member.id + "')\">";
                        if(member.type == "user"){
                            html += "<i><img src=\"${ctx}/userimage/getUserImage/" + member.id + "\"/></i>";
                        } else {
                             html += "<i><img src=\"${ctx}/static/skins/default/img/department-icon.png\"/></i>";
                        }
						html = html + "</li>";
						$("#preAddMember").append(html);

					}

					function selectIcon(th) {
						var member = $(th).find("i").data("userInfo");
						if (typeof (member) == "undefined") {
							$.toast("已添加", 400);
							return;
						}
						if ($(th).find("i").attr("class") == "M-addblank") {
							$(th).find("i").removeClass("M-addblank");
							$(th).find("i").addClass("M-active");
							if ($("#preAddMemberDiv").css("display") == "none") {
								$("#preAddMemberDiv").css("display", "block");
							}
							addPreMember(member);
							setSelectMenber(member);

						} else {
							$(th).find("i").removeClass("M-active");
							$(th).find("i").addClass("M-addblank");
							$("#pre" + member.id).remove();
							$("#active-" + member.id).remove();
						}
						tailNameSlide()
					}

					function setSelectMenber(member) {
						var html = "";
						html = html + "<li id=\"active-" + member.id + "\">";
                        if(member.type=="user"){
                            html += "<p><img src=\"" + ctx + "/userimage/getUserImage/" + member.id + "\"/></p>";
                        } else {
                            html += "<p><img src=\"" + ctx + "/static/skins/default/img/department-icon.png\"/></p>";
                        }
						html += "<h1>" + member.name + "</h1>";
						html = html + "</li>";
						$("#shareContent").prepend(html);
						$("#active-" + member.id).data("data", member);

					}

					function submitMenber() {
						$('#addMember').css('display', 'none');
						$("#shareContent").children().each(
							function (i, n) {
								var obj = $(n);
								if (obj.attr("id") != undefined &&
									obj.attr("id").indexOf("active") > -1) {
									var item = obj.data("data");
									addMessageTo(item);
								}

							});
						addMember();
						$('#appendname').css('display', 'block');
					}

					function addMessageTo(item) {
						allMessageTo.push(item);

					}

					function addMember() {
						var url=host+"/ufm/api/v2/acl";
						var params={
							role:getRoleName(),
							userList:allMessageTo,
							resource:{"ownerId":ownerId,"nodeId":iNodeId},
						};
						
						$.ajax({
							type: "POST",
							url: url,
							data: JSON.stringify(params),
							error: function (request) {
								$.toast("<spring:message code='operation.failed'/>", "cancel");
							},
							success: function (data) {
								$.toast("添加成功");
								window.location.reload();
							}
						});

					}

					function getUsersString(dataArray) {
						var result = "";
						for (var i = 0; i < dataArray.length; i++) {
							if (dataArray[i] != "") {
								if (i == (dataArray.length - 1)) {
									result = result + dataArray[i]
								} else {
									result = result + dataArray[i] + ";"
								}
							}
						}
						return result;
					}



					function modifyNodeIsVisible(isshow) {
						var url = host+"/ufm/api/v2/acl/isVisible/" + ownerId + "/" + iNodeId;
						$.ajax({
							type: "POST",
							url: url,
							data: JSON.stringify(isshow),
							error: function (request) {
								$.alert("error",
									"<spring:message code='operation.failed'/>");
							},
							success: function (data) {
								// $.toast("设置成功");
							}
						});
					}

					function returnBack() {
						$("#selectMember").css("display", "none");
						$("#mainBody").css("display", "block");

					}

					$("#secretSwitch").bind("click", function () {
					var html = ""
					html=html+'<li>'
					  html+='<div class="share-span1" onclick="showDiv(\'selectMember\')"></div>'
					html=html+'</li>'
						if ($("#secret").val() == "off") {
							$("#secret").val("on");
							if (init) {
								init = false;
							} else {
								$.confirm("您确定要修改当前文件夹为限制访问吗?", "设置", function () {
									$("#preAddMember").children().remove()
	                                $("#shareContent").children().remove()
									$("#shareContent").append(html)
									$("#preAddMemberDiv").hide()
									$('#noname').css('display', 'block');
									$('#selectMemberS').hide();
									if ($('#spaceMemberList li').length >= 1) {
										$('#appendname').show();
										$('#selectMember').hide();
										$('#noname').css('display', 'none');
										$('.putting-append').show();
									} else {

										$('#selectMember').show();
									}
									$("#addMember").hide();
									$("#updatePermission").hide();
									$(".weui-switch").prop("checked",true)
									modifyNodeIsVisible(1);
									initDataList()
								}, function () {
									$("#addMember").hide();
									$("#updatePermission").hide();
									$(".weui-switch").prop("checked",false)
									$("#secret").val("off");
									//取消操作
								});
							}
						} else {
							$("#secret").val("off");

							if (init) {
								init = false;
							} else {
								$.confirm("您确定要修改当前文件夹为公开吗?", "设置", function () {
									modifyNodeIsVisible(0);
									initDataList()
									$("#shareContent").children().remove()
									$('#noname').css('display', 'none');
									$('#appendname').hide();
									$('#selectMember').hide();
									$('#selectMemberS').show();
									$('.putting-append').hide();
									$(".weui-switch").prop("checked",false)
									$("#addMember").hide();
									$("#updatePermission").hide();
								}, function () {

									if ($('#spaceMemberList li').length >= 1) {
										$('#appendname').show();
										$('#selectMember').hide();
										$('#noname').css('display', 'none');
										$('.putting-append').show();
									} else {
										$('#selectMember').show();
									}
									$(".weui-switch").prop("checked",true)
									//取消操作
									$("#secret").val("on");
									$("#addMember").hide();
									$("#updatePermission").hide();
									<%--$(".not-per-team").hide()--%>
								});
							}
						}

					});
					var init = true;

					function isVisibleNodeACL(ownerId, nodeId) {
						var url = host + "/ufm/api/v2/acl/" + ownerId + "/" + nodeId;
						$.ajax({
							type: "GET",
							url: url,
							error: function (request) {},
							success: function (data) {
								if (data == true) {
									$("#secretSwitch").click();
									$("#secret").val("on")
								} else {
									init = false;
								}
							}
						});
					}
					
				</script>
</body>

</html>