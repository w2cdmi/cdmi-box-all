<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
	<!DOCTYPE html>
	<html>

	<head>
		<title>协作空间</title>
		<%@ include file="../common/include.jsp" %>
			<link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/teamSpace/teamSpaceList.css?v=${version}" />
			<link rel="stylesheet" href="${ctx}/static/css/default/magic-input.min.css" type="text/css">
			<script type="text/javascript" src="${ctx}/static/js/teamspace/TeamSpaceList.js?v=${version}"></script>
			<script type="text/javascript" src="${ctx}/static/js/teamspace/SeeManager.js?v=${version}"></script>
			<script src="${ctx}/static/components/MemberManage.js?v=${version}"></script>
	</head>

	<body style="overflow: hidden;background: #fff">
		<jsp:include page="../common/menubar.jsp">
			<jsp:param name="activeId" value="teamspace" />
		</jsp:include>
		<%@ include file="../common/memberDialog.jsp" %>
			<div id="toolbar" class="cl">
				<div class="left">
					<button id="addTeamspaceBtn">
						<i class="fa fa-plus-square"></i>创建协作空间</button>
				</div>
			</div>
			<div class="abslayout" style="top:120px;left:0px;right:0;bottom:16px;overflow: auto;">
				<div class="space-list-view" style="background: #fff" id="teamSpaceList">

				</div>
				<div id="not_space" style="text-align: center; min-width: 540px;min-height:400px; line-height:400px;display:none">
					<img style="width:160px;display: inline-block; vertical-align: middle;" src="${ctx}/static/skins/default/img/not_space_img.png"
					/>
				</div>
			</div>


			<div class="popover bottom" id="worker_popover">
				<div class="arrow" style="left: 115px;"></div>
				<dl class="menu">
					<dt role="1" command="1">
						<i class="fa fa-list-ul"></i>查看空间信息</dt>
					<dt role="2" command="2">
						<i class="fa fa-pencil-square"></i>修改协作空间</dt>
					<dt role="3" command="3">
						<i class="fa fa-users"></i>成员管理</dt>
					<dt role="2" command="4">
						<i class="fa fa-exchange"></i>移交协作空间</dt>
					<dt role="2" command="5">
						<i class="fa fa-trash"></i>解散协作空间</dt>
					<dt role="4" command="7">
						<i class="fa fa-user"></i>查看成员</dt>
					<dt role="5" command="6">
						<i class="fa fa-power-off"></i>退出协作空间</dt>

				</dl>
			</div>
			<!-- 查看空间详情 -->
			<div id="seeTeamInfoDialog" style="display:none">

			</div>
			<!-- 修改协作空间 -->
			<div id="editTeamInfoDialog" style="display:none">

			</div>
			<!-- 创建协作空间 -->
			<div id="creatTeamInfoDialog" style="display:none;">

			</div>
			<!-- 成员管理 -->
			<div id="memberManageDialog" style="display:none">

			</div>
			<!-- 解散团队 -->
			<div id="deleteTeamInfoDialog" style="display:none">
				<form class="form">
					<p style="font-size: 14px;line-height: 36px">
						<spring:message code='teamSpace.msg.confirmDismiss' />
					</p>
					<input style="width: 340px" type="text" name="" value="">
					<div class="form-control" style="text-align: right;margin-top: 16px;">
						<button type="button" id="cancel_button">取消</button>
						<button type="button" id="ok_button">确定</button>
					</div>
				</form>

			</div>
			<!-- 退出空间 -->
			<div id="exitTeamInfoDialog" style="width:308px;height:87px;display:none">
				<p style="text-align: center;line-height: 44px">
					确定退出协作空间吗？
				</p>
				<div class="form-control" style="text-align: right;margin-top: 16px;">
					<button type="button" id="cancel_button">取消</button>
					<button type="button" id="ok_button">确定</button>
				</div>
			</div>
			<%--移交空间--%>
				<div id="deptStackDialog" style="display:none">
					<%--移交空间人员--%>
						<div class="add-leaguer" style="width:400px;height:365px;overflow-y:auto;display: none;" id="addLeaguer">
							<div class="add-leaguer-nav">
								<ul>
									<li style="line-height:44px;" id="showAddressListChooser" onclick="showAddressListChooser()">
										<i>
											<img src="${ctx}/static/skins/default/img/add-leaguer-group.png" />
										</i>
										<span class="buss-title-phone" style="margin-left: 10px;cursor: pointer">企业通讯录</span>
									</li>
								</ul>
							</div>
							<!-- <div class="add-leaguer-tail">
				<ul>
					<li>
						<h1 style="font-size: 14px;line-height: 26px">最近联系的人</h1>
					</li>
				</ul>
				</div> -->
						</div>


						<!---->
						<div class="staff-picker" style="width:400px;height:200px;overflow-y:auto;display: none; padding-top: 16px; background: #FFFFFF;"
						    id="staffPicker">
							<!--<div>
			<spring:message code='teamSpace.changeOwer.content' />
			</div>-->
							<div class="staff-picker-prompt">空间将移交给</div>
							<div class="select-member-con">
								<div id="selectedMemeber"></div>
								<div class="re-seletion-father">
									<input id="re-seletion" onclick="reseletion()" type="button" value="重新选择" />
								</div>
							</div>
							<div class="staff-picker-remarks">
								<div class="staff-picker-remarks-middle">备注:移交后您将自动降级为空间管理员</div>
							</div>
							<%--<div class="staff-picker-remarks-brother">--%>
								<%--<p></p>--%>
									<%--</div>--%>
										<div class="form-control" style="text-align: right;margin-top: 16px;">
											<button type="button" onclick="closeStaffChooser()" id="cancel_button">关闭</button>
											<button type="button" id="ok_button">确定</button>
										</div>
										<%--<div class="staff-picker-member-operation">--%>
											<%--<div onclick="submitChangeOwner()"> 确定 </div>--%>
												<%--</div>--%>
													<%--<a href="javascript:" id="searchClose" onclick="closeStaffChooser()">关闭</a>--%>
						</div>

						<!---->
						<div style="width:400px;height:365px;overflow-y:auto;display: none" class="share-address-list" id="teamSpaceAddressList">
							<div class="share-address-content">
								<input type="hidden" id="parentDeptId" value="0" />
								<div class="return-father" onclick="backward()">
									<button class="historyBack-return" style="margin-bottom: 10px;
					padding: 5px 15px;
					width: 100%;
					background: #f9f9f9;
					color: #000;">返回</button>
									<span id="department"></span>
								</div>
								<ul id="shareList">

								</ul>
							</div>
						</div>

				</div>
				<%--查看成员--%>
					<div id="seeallmanageDialog" style="display:none">
						<ul id="manager-list">

						</ul>
					</div>
	</body>
	<!-- 空间文件 -->
	<script type="text/javascript">
		function gotoTeamSpace(space) {
			gotoPage('${ctx}/teamspace/file/' + space);
		}
	</script>
	<%--员工列表模板--%>
		<script id="pickerMemberTemplate" type="text/template7">
			{{#js_compare "this.type == 'department'"}}
			<li class="member-li" onclick="showDepAndUsers({{userId}}, {{pId}})">
				<p>
					<img style="width:32px;height:32px" src="${ctx}/static/skins/default/img/department-icon.png">
				</p>
				<span>{{name}}</span>
			</li>
			{{else}}
			<li class="select-member-li" onclick="setSelectedMember({{id}}, '{{name}}')">
				<p>
					<img style="width:32px;height:32px" src="${ctx}/userimage/getUserImage/{{id}}">
				</p>
				<span>{{alias}}</span>
			</li>
			{{/js_compare}}
		</script>
		<!-- 创建团队 -->

		<!-- 操作 -->
		<script type="text/javascript">
			var currentPage = 1;
			var tost;
			var deptStack = [];

			function processHash() {
				var hash = window.location.hash;
				if (hash.indexOf('#') != -1) {
					var m = hash.substring(hash.indexOf("#") + 1);
					if (!m || m == 'none') {
						return;
					}
					currentPage = m;
				}

				return currentPage;
			}

			function reseletion() {
				deptStack = [];
				$('#teamSpaceAddressList').css('display', 'none');
				$('#staffPicker').hide();
				$('#addLeaguer').show();
			}

			function backward() {
				var deptId = deptStack.pop();
				if (deptId != undefined) {
					showDepAndUsers(deptId);
				} else {
					$('#teamSpaceAddressList').hide();
					$('#addLeaguer').show();
				}
			}

			function closeStaffChooser() {
				$('#addLeaguer').css('display', 'none');
				$('.dialog').css('display', 'none')
				$("#staffPicker").hide();
			}

			function setSelectedMember(id, name) {
				//				closeAddressListChooser();
				$('#teamSpaceAddressList').hide();
				$("#staffPicker").show();

				$("#selectedMemeber").empty().append('<p><img src="${ctx}/userimage/getUserImage/' + id + '"></p><span>' + name +
					'</span>').data({
					"loginName": name,
					"loginNameId": id
				});
				$("#parentDeptId").val(0);
			}

			function showDepAndUsers(deptId, pId) {

				var params = {
                    deptId: deptId
				};
				var $list = $("#shareList");
				$list.empty();
				$.ajax({
					type: "POST",
					url: host + '/ecm/api/v2/users/listDepAndUsers',
					data: JSON.stringify(params),
					error: handleError,
					success: function (data) {
						//                    debugger;
						if (data != "[]") {
							//清空现有的列表
							if (pId != undefined) {
								deptStack.push(pId);
							}
							data = $.parseJSON(data);
							$list.children().remove();
							var $memberTemplate = $("#pickerMemberTemplate");
							for (var i = 0; i < data.length; i++) {
								var staff = data[i];
								$memberTemplate.template(staff).appendTo($list);

								//设置数据
								var $row = $("#space_" + staff.id);
								$row.data("obj", staff);
							}
						} else {
							$.Tost("没有员工").show().autoHide(1000);
						}
					}
				});
			}
			//移交空间


			function openChangeOwner(space, success) {
				deptStack = [];
				$("#selectedMemeber").empty();
				$("#staffPicker").hide();
				$('#teamSpaceAddressList').hide();
				$("#addLeaguer").attr("ownedByUserNameId", space.teamspace.ownedBy)
				$("#addLeaguer").attr("teamdId", space.teamId).show();

				if (success) {
					success();
				}
			}


			function closeAddressListChooser() {
				$('#addressListChooser').css('display', 'none');
			}

			function showAddressListChooser() {
				//				$("#addressListChooser").show();
				$("#addLeaguer").hide();
				$("#teamSpaceAddressList").show();
				//            initStaffList(space.id);
				showDepAndUsers(0, undefined);
			}

			function submitChangeOwner(success) {
				var $picker = $("#addLeaguer");
				var teamId = $picker.attr("teamdId");
				var loginName = $("#selectedMemeber").data("loginName");
				var loginNameId = $("#selectedMemeber").data("loginNameId");
				var ownedByUserNameId = $picker.attr("ownedByUserNameId")
				if (loginName == null || loginName == "") {
					$.Tost("请选择移交人").show().autoHide(1000);
					return;
				}
				if (loginNameId == ownedByUserNameId) {
					$.Alert("空间不能移交给自己!")
					return;
				} else {
					$.ajax({
						type: "PUT",
						url: host + "/ufm/api/v2/teamspaces/" + teamId + "/memberships/admin/" + loginNameId,
						error: function (request) {
							var status = request.status;
							if (status == 403) {
								$.Alert("<spring:message code='teamSpace.error.forbiddenChangeOwner'/>", "forbidden");
							} else {
								$.Alert("<spring:message code='operation.failed'/>", "cancel");
							}
						},
						success: function () {
							if (typeof (data) == 'string' && data.indexOf('<html>') != -1) {
								window.location.href = "${ctx}/logout";
								return;
							}
							$picker.hide();
							processHash();
							$('#staffPicker').hide();

							if (success) {
								success()
							}
						}
					});
				}

			}

			$(document).ready(function () {
				$("#deptStackDialog").find("#ok_button").click(function () {
					submitChangeOwner(function () {
						teamSpaceList.load();
						deptStackDialog.hide();
						$.Alert("移交成功")
					});
				})
				var teamSpaceList = $("#teamSpaceList").TeamSpaceList();
				var pageSize = getCookie("spaceListSizePerPage", 40);
				teamSpaceList.init();
				teamSpaceList.param = {
					type:0,
					userId:curUserId
				};
				teamSpaceList.load();
				var editTeamInfoDialog = $('#editTeamInfoDialog').dialog({
					title: "修改协作空间"
				})
				editTeamInfoDialog.init();
				var deleteTeamInfoDialog = $('#deleteTeamInfoDialog').dialog({
					title: "解散协作空间"
				})
				deleteTeamInfoDialog.init();
				var seeTeamInfoDialog = $('#seeTeamInfoDialog').dialog({
					title: "查看空间信息"
				})
				seeTeamInfoDialog.init();
				//成员管理
				var memberManageDialog = $('#shareDialog').ShareDialog()
				memberManageDialog.init0();
				var exitTeamInfoDialog = $('#exitTeamInfoDialog').dialog({
					title: "退出协作空间"
				})
				exitTeamInfoDialog.init();
				var deptStackDialog = $('#deptStackDialog').dialog({
					title: "移交协作空间"
				})
				deptStackDialog.init();
				var seeallmanageDialog = $('#seeallmanageDialog').dialog({
					title: "查看成员"
				})
				seeallmanageDialog.init()
				var managerList = $('#manager-list').AddManagerList()
				managerList.init()
				// 修改空间
				function editTeamSpace(teamId) {
                    var params={
                        'name':$("#name").val(),
                        'description':$("#description").val()
                    }
					$.ajax({
						type: "PUT",
						url: host + "/ufm/api/v2/teamspaces/"+ teamId,
						data: JSON.stringify(params),
						error: function (request) {
							alert("<spring:message code='operation.failed'/>");
						},
						success: function () {
							if (typeof (data) == 'string' && data.indexOf('<html>') != -1) {
								window.location.href = "${ctx}/logout";
								return;
							}
							// goBack();
							teamSpaceList.load();
							tost.hide()
						}

					});
				}
				// 解散空间
				function openDelete(space) {
					console.log($("#deleteTeamInfoDialog").find('input').val());

					if ($("#deleteTeamInfoDialog").find('input').val().toUpperCase() === "YES") {

						$.ajax({
							type: "DELETE",
							url: host + "/ufm/api/v2/teamspaces/" + space.teamId,
							error: function () {
								$.Alert("<spring:message code='operation.failed'/>");
							},
							success: function () {
								deleteTeamInfoDialog.hide()
								teamSpaceList.load();

							}
						});
					}
				}
				// 退出空间
				function openExitTeam(space) {

					$.ajax({
						type: "DELETE",
						url: host + "/ufm/api/v2/teamspaces/" + space.teamId + "/memberships/user/" + space.member.id,
						<%--data: {--%>
							<%--"teamMembershipsId": space.memberId,--%>
							<%--"teamId": space.id,--%>
							<%--'token': "${token}"--%>
						<%--},--%>
						error: function (request) {
                            $.Alert("<spring:message code='operation.failed'/>");
						},
						success: function (data) {
                            $.Alert("退出协作空间成功!");
							teamSpaceList.load();
						}
					});

				}

				function changeToListView() {
					$("#teamSpaceList").removeClass("space-thumbnail-view").addClass("space-list-view");
				}

				function changeToThumbnailView() {
					$("#teamSpaceList").removeClass("space-list-view").addClass("space-thumbnail-view");
				}
				// 空间操作
				$("#worker_popover dt").mouseup(function () {

					// 查看空间详情
					if ($(this).attr("command") == "1") {
						// seeTeamInfoDialog.show();
						$("#seeTeamInfoDialog").empty();
						$("#seeTeamInfoDialog").load('${ctx}/teamspace/openGetTeamInfo?teamId=' + teamSpaceList.selectedRow.teamId +
							"&tims=" + Date.parse(new Date()) / 1000,
							function () {
								seeTeamInfoDialog.show();
								var quota = teamSpaceList.selectedRow.teamspace.spaceQuota;
								if (quota != -1) {
									var spaceQuota = formatFileSize(teamSpaceList.selectedRow.teamspace.spaceQuota);
									$("#seeTeamInfoDialog").find("#spaceQuotaInfo").text(spaceQuota);
								} else {
									$("#seeTeamInfoDialog").find("#spaceQuotaInfo").text("<spring:message code='teamSpace.tip.noLimit'/>")
								}

								var spaceUsed = formatFileSize(teamSpaceList.selectedRow.teamspace.spaceUsed);
								$("#seeTeamInfoDialog").find("#usedQuotaInfo").text(spaceUsed);
								// ("#usedQuotaInfo").text(spaceUsed);
							}
						)
					}
					// 修改协作空间
					if ($(this).attr("command") == "2") {
						tost = $.Tost("正在加载中.....")
						tost.show()
						$("#editTeamInfoDialog").empty();
						$("#editTeamInfoDialog").load('${ctx}/teamspace/openEditTeamSpace?teamId=' + teamSpaceList.selectedRow.teamId +
							"&tims=" + Date.parse(new Date()) / 1000,
							function () {
						    var teamId = teamSpaceList.selectedRow.teamId
								editTeamInfoDialog.show();
								tost.hide()
								$("#editTeamInfoDialog").find("form").submit(function (e) {
									e.preventDefault();
								})
								$("#editTeamInfoDialog").find("#ok_button").click(function () {
									editTeamSpace(teamId);
									editTeamInfoDialog.hide();

								})
								$("#editTeamInfoDialog").find("#cancel_button").click(function () {
									editTeamInfoDialog.hide();
								})
							}
						)
						//alert(teamSpaceList.selectedRow.name);

					}
					// 成员管理
					if ($(this).attr("command") == "3") {
						var deptId = 0
						memberManageDialog.show0(deptId, teamSpaceList.selectedRow, function () {
							teamSpaceList.load();
						})


					}
					//移交空间
					if ($(this).attr("command") == "4") {

						openChangeOwner(teamSpaceList.selectedRow, function () {
							deptStackDialog.show();
						})

					}

					// 解散空间
					if ($(this).attr("command") == "5") {
						// $("#deleteTeamInfoDialog").empty()
						deleteTeamInfoDialog.show()
						$("#deleteTeamInfoDialog").find("form").submit(function (e) {
							e.preventDefault();
						})
						$("#deleteTeamInfoDialog").find("#ok_button").click(function () {
							openDelete(teamSpaceList.selectedRow)


							$("#deleteTeamInfoDialog").find('input').val("");
							// $("#deleteTeamInfoDialog").find("#ok_button").unbind()

						})
						$("#deleteTeamInfoDialog").find("#cancel_button").click(function () {
							deleteTeamInfoDialog.hide()
						})
					}
					if ($(this).attr("command") == "6") {
						exitTeamInfoDialog.show()
						$("#exitTeamInfoDialog").find("#ok_button").click(function () {
							openExitTeam(teamSpaceList.selectedRow)

							exitTeamInfoDialog.hide()

							$("#exitTeamInfoDialog").find("#ok_button").unbind()


						})
						$("#exitTeamInfoDialog").find("#cancel_button").click(function () {
							exitTeamInfoDialog.hide()
						})
					}
					if ($(this).attr("command") == "7") {
						$('#manager-list').empty()
						var tost = $.Tost("正在加载.....")
						tost.show()
						managerList.load(teamSpaceList.selectedRow.teamId, function () {
							seeallmanageDialog.show()
							tost.hide()

						})

					}
				})
				// 创建协作空间
				var creatTeamInfoDialog = $('#creatTeamInfoDialog').dialog({
					title: "创建协作空间"
				})
				creatTeamInfoDialog.init()

				function submitTeamSpace() {
					var name = $("#name").val().trim();
                    var params={
                        'name':$("#name").val(),
                        'description':$("#description").val()

                    }
					if (name == '') {
						$.Tost('请输入空间名称!').show().autoHide(1000);
					} else {
						creatTeamInfoDialog.hide();
						$.ajax({
							type: "POST",
							url:  host +"/ufm/api/v2/teamspaces",
							data: JSON.stringify(params),
							error: function (request) {

								var status = request.status;
								if (status == 507) {
									$.Alert("<spring:message code='teamSpace.error.exceedMaxSpace'/>");
								} else if (status == 403) {
									$.Alert("<spring:message code='teamSpace.error.forbiddenAdd'/>");
								} else if (status == 400) {
									$.Alert("操作失败")
								} else {
									$.Alert("<spring:message code='operation.failed'/>");
								}
							},
							success: function () {

								teamSpaceList.load();
								tost.hide();


							}
						});
					}

				}
				$("#addTeamspaceBtn").click(function () {
					tost = $.Tost("正在加载中.....")
					tost.show()
					$("#creatTeamInfoDialog").empty();
					$("#creatTeamInfoDialog").load('${ctx}/teamspace/openAddTeamSpace?tims=' + Date.parse(new Date()) / 1000,
						function () {
							tost.hide()
							creatTeamInfoDialog.show();
							creatTeamInfoDialog.find('input[name="name"]').focus();
							$("#creatTeamInfoDialog").find("form").submit(function (e) {
								e.preventDefault();
							})
							$("#creatTeamInfoDialog").find("#ok_button").click(function () {
								submitTeamSpace();

							})
							$("#creatTeamInfoDialog").find("#cancel_button").click(function () {
								creatTeamInfoDialog.hide();
							})
						})

				})
				//teamSpaceList.selectedRow
			})
		</script>

	</html>