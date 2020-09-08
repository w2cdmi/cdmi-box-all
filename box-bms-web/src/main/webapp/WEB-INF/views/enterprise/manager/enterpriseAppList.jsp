<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cse"
	uri="http://cse.huawei.com/custom-function-taglib"%>
<%@ page import="pw.cdmi.box.uam.util.CSRFTokenManager"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<%
	request.setAttribute("token", CSRFTokenManager.getTokenForSession(session));
%>
<!DOCTYPE html>
<html>
<head>
<%@ include file="../../common/common.jsp"%>
<script src="${ctx}/static/js/public/JQbox-hw-page.js"
	type="text/javascript"></script>
</head>
<body>
	<div class="sys-content">
		<div class="alert">
			<i class="icon-lightbulb"></i>
			<spring:message code="enterprise.app.list" />
		</div>
		<div class="clearfix">
			<div class="pull-right form-search">
				<input type="hidden" id="page" name="page" value="1"> <select
					class="span2" id=statusSelect name="statusSelect">
					<option value="">
						<spring:message code="common.status" />
					</option>
					<option value="1">
						<spring:message code="common.enable" />
					</option>
					<option value="0">
						<spring:message code="common.disable" />
					</option>

				</select>
				<div class="input-append">
					<input type="text" id="filter" name="filter"
						class="span3 search-query" value="<c:out value='${filter}'/>"
						placeholder='<spring:message code="enterpriseAccountList.searchDescription"/>' />
					<button type="button" class="btn" id="searchButton">
						<i class="icon-search"></i>
					</button>
				</div>
				<input type="hidden" id="token" name="token"
					value="<c:out value='${token}'/>" />
			</div>
		</div>
		<div class="table-con">
			<div id="rankList"></div>
			<div id="rankListPage"></div>
		</div>
		<div id="myPage"></div>
	</div>
</body>
<script type="text/javascript">
	var newHeadItem = "";
	var newFlag = false;
	var currentPage = 1;
	var opts_viewGrid = null;
	var opts_page = null;
	var headData = {
		"name" : {
			"title" : '<spring:message code="enterpriseList.name"/>',
			"width" : "100px",
			"taxis" : true
		},
		"domainName" : {
			"title" : '<spring:message code="enterprise.domainName"/>',
			"width" : "100px",
			"taxis" : true
		},
		"authAppId" : {
			"title" : '<spring:message code="common.application.name"/>',
			"width" : "90px"
		},
		"maxMember" : {
			"title" : '<spring:message code="enterprise.currentMaxMember"/>',
			"width" : "130px"
		},
		"maxTeamSpaces" : {
			"title" : '<spring:message code="enterprise.currentMaxTeamspace"/>',
			"width" : "130px"
		},
		"filePreviewable" : {
			"title" : '<spring:message code="file.previewable"/>',
			"width" : "60px"
		},
		"status" : {
			"title" : '<spring:message code="common.status"/>',
			"width" : "70px"
		},
		"createdAt" : {
			"title" : '<spring:message code="clientManage.createDate"/>',
			"width" : "120px",
			"taxis" : true
		},
		"modifiedAt" : {
			"title" : '<spring:message code="space.title.modifyTime"/>',
			"width" : "120px",
			"taxis" : true
		},
		"operation" : {
			"title" : '<spring:message code="authorize.operation"/>',
			"width" : "120px"
		}
	};
	$(document)
			.ready(
					function() {
						var pageH = $("body").outerHeight();
						top.iframeAdaptHeight(pageH);

						opts_viewGrid = $("#rankList").comboTableGrid({
							headData : headData,
							colspanDrag : true,
							height : 860,
							dataId : "id"
						});

						$.fn.comboTableGrid.setItemOp = function(tableData,
								rowData, tdItem, colIndex) {
							switch (colIndex) {
							case "filePreviewable":
								try {
									if (true == rowData.filePreviewable) {
										tdItem
												.find("p")
												.html(
														"<spring:message code='common.yes.enterprise'/>")
												.parent()
												.attr("title",
														"<spring:message code='common.yes.enterprise'/>");
									}
									if (false == rowData.filePreviewable) {
										tdItem
												.find("p")
												.html(
														"<label><spring:message code='common.no.enterprise'/></label>");
									}
								} catch (e) {
								}
								break;
							case "status":
								try {
									if (rowData.status == 1) {
										tdItem
												.find("p")
												.html(
														"<spring:message code='common.enable'/>")
												.parent()
												.attr("title",
														"<spring:message code='common.enable'/>");
									}
									if (rowData.status == 0) {
										tdItem
												.find("p")
												.html(
														"<label class='public_red_font'><spring:message code='common.stop'/></label>");
									}
								} catch (e) {
								}
								break;
							case "maxMember":
								var currentMaxMember = rowData.currentMaxMember;
								var maxMember = rowData.maxMember
								if (maxMember == 99999999) {
									maxMember = "<spring:message code='teamSpace.tip.noLimit'/>";
								}
								try {
									tdItem.find("p").html(
											"<label>" + currentMaxMember + "/"
													+ maxMember + "</label>")
											.parent().attr(
													"title",
													currentMaxMember + "/"
															+ maxMember);
								} catch (e) {
								}
								break;
							case "maxTeamSpaces":
								try {
									var currentMaxTeamspace = rowData.currentMaxTeamspace;
									var maxTeamspace = rowData.maxTeamspace;
									if (maxTeamspace == 99999999) {
										maxTeamspace = "<spring:message code='teamSpace.tip.noLimit'/>";
									}
									tdItem.find("p").html(
											"<label>" + currentMaxTeamspace
													+ "/" + maxTeamspace
													+ "</label>").parent()
											.attr(
													"title",
													currentMaxTeamspace + "/"
															+ maxTeamspace);
								} catch (e) {
								}
								break;
							case "createdAt":
								try {
									var size = tdItem.find("p").text();
									for (var i = 0; i < catalogData.length; i++) {
										if (size == catalogData[i].createdAt) {
											_txt = catalogData[i].createdAt;
											var date = new Date(_txt);
											var _year = date.getFullYear();
											var _month = date.getMonth() + 1;
											if (_month < 10) {
												_month = "0" + _month;
											}
											var _day = date.getDate();
											if (_day < 10) {
												_day = "0" + _day;
											}
											var _hours = date.getHours();
											if (_hours < 10) {
												_hours = "0" + _hours;
											}
											var _min = date.getMinutes();
											if (_min < 10) {
												_min = "0" + _min;
											}
											var _sec = date.getSeconds();
											if (_sec < 10) {
												_sec = "0" + _sec;
											}
											var date = _year + "-" + _month
													+ "-" + _day + " " + _hours
													+ ":" + _min + ":" + _sec;
											tdItem.find("p").html(date)
													.parent().attr("title",
															date);
										}
									}
								} catch (e) {
								}
								break;
							case "modifiedAt":
								try {
									var size = tdItem.find("p").text();
									for (var i = 0; i < catalogData.length; i++) {
										if (size == catalogData[i].modifiedAt) {
											_txt = catalogData[i].modifiedAt;
											var date = new Date(_txt);
											var _year = date.getFullYear();
											var _month = date.getMonth() + 1;
											if (_month < 10) {
												_month = "0" + _month;
											}
											var _day = date.getDate();
											if (_day < 10) {
												_day = "0" + _day;
											}
											var _hours = date.getHours();
											if (_hours < 10) {
												_hours = "0" + _hours;
											}
											var _min = date.getMinutes();
											if (_min < 10) {
												_min = "0" + _min;
											}
											var _sec = date.getSeconds();
											if (_sec < 10) {
												_sec = "0" + _sec;
											}
											var date = _year + "-" + _month
													+ "-" + _day + " " + _hours
													+ ":" + _min + ":" + _sec;
											tdItem.find("p").html(date)
													.parent().attr("title",
															date);
										}
									}
								} catch (e) {
								}
								break;

							case "operation":
								try {

									var textStatus = "";
									if (rowData.status == 0) {
										textStatus = '<spring:message code="common.enable"/>';
									}
									if (rowData.status == 1) {
										textStatus = '<spring:message code="common.stop"/>';
									}

									var btnStatus = '<input class="btn btn-small" type="button" value="'
											+ textStatus
											+ '" onClick="updateStatus('
											+ rowData.accountId
											+ ', '
											+ rowData.status + '' + ')"/> ';
									var btnModify = '<input class="btn btn-small" type="button" value="<spring:message  code="common.modify"  />" onClick="modifyAccount('
											+ rowData.accountId + ')"/>';
									var btns = btnStatus + btnModify;

									tdItem.find("p").html(btns);
								} catch (e) {
								}
								break;
							default:
								break;
							}
						};

						$.fn.comboTableGrid.taxisOp = function(headItem, flag) {
							initDataList(currentPage, headItem, flag);
						};

						opts_page = $("#rankListPage").comboPage({
							style : "page table-page",
							lang : '<spring:message code="main.language"/>'
						});

						$.fn.comboPage.pageSkip = function(opts, _idMap,
								curPage) {
							initDataList(curPage, newHeadItem, newFlag);
						};

						initDataList(currentPage, newHeadItem, newFlag);

						if (!placeholderSupport()) {
							placeholderCompatible();
						}
						;

						$("#searchButton").on("click", function() {
							initDataList(currentPage, newHeadItem, newFlag);
						});

						$("#filter").keydown(
								function() {
									var evt = arguments[0] || window.event;
									if (evt.keyCode == 13) {
										initDataList(currentPage, newHeadItem,
												newFlag);
										if (window.event) {
											window.event.cancelBubble = true;
											window.event.returnValue = false;
										} else {
											evt.stopPropagation();
											evt.preventDefault();
										}
									}
								});
					});

	function initDataList(curPage, headItem, flag) {
		currentPage = curPage;
		newHeadItem = headItem;
		newFlag = flag;
		var url = "${ctx}/enterprise/account/enterpriseAppList";
		var statusSelect = $("#statusSelect").find("option:selected").val();
		var filter = $("#filter").val();
		var params = {
			"page" : curPage,
			"filter" : filter,
			"token" : "<c:out value='${token}'/>",
			"appId" : "<c:out value='${enterpriseAppId}'/>",
			"status" : statusSelect,
			"newHeadItem" : newHeadItem,
			"newFlag" : newFlag
		};
		$("#rankList").showTableGridLoading();
		$.ajax({
			type : "POST",
			url : url,
			data : params,
			error : function(request) {
				handlePrompt("error",
						'<spring:message code="common.operationFailed" />');
			},
			success : function(data) {
				catalogData = data.content;
				$("#rankList").setTableGridData(catalogData, opts_viewGrid);
				$("#rankListPage").setPageData(opts_page, data.number,
						data.size, data.totalElements);
				var pageH = $("body").outerHeight();
				top.iframeAdaptHeight(pageH);
			}
		});
	}

	function modifyAccount(accountId) {
		top.ymPrompt.win({
			message : '${ctx}/enterprise/account/getAccount/' + accountId,
			width : 700,
			height : 440,
			title : '<spring:message code="common.modify"/>',
			iframe : true,
			btn : [
					[ '<spring:message code="common.modify"/>', 'yes', false,
							"btnModify" ],
					[ '<spring:message code="common.cancel"/>', 'no', true,
							"btnCancel" ] ],
			handler : doModify
		});
		top.ymPrompt_addModalFocus("#btnModify");
	}

	function doModify(tp) {
		if (tp == 'yes') {
			top.ymPrompt.getPage().contentWindow
					.modifyAccount('<c:out value="${enterpriseAppId}"/>');
		} else {
			top.ymPrompt.close();
		}
	}

	function updateStatus(accountId, status) {
		if (accountId == '' || accountId == null) {
			handlePrompt("error",
					'<spring:message code="enterprise.account.err"/>');
			return;
		}
		var tilteTip, messageTip;
		if (status == 0) {
			tilteTip = '<spring:message code="enterprise.account.open.title"/>';
			messageTip = '<spring:message code="enterprise.open.message"/>';
		} else {
			tilteTip = '<spring:message code="enterprise.account.close.title"/>';
			messageTip = '<spring:message code="enterprise.account.close.message"/>';
		}
		top.ymPrompt.confirmInfo({
			title : tilteTip,
			message : messageTip,
			width : 450,
			closeTxt : '<spring:message code="common.close"/>',
			handler : function(tp) {
				if (tp == "ok") {
					changeStatus(accountId, status);
				}
			},
			btn : [ [ '<spring:message code="common.OK"/>', "ok" ],
					[ '<spring:message code="common.cancel"/>', "cancel" ] ]
		});
	}

	function changeStatus(accountId, status) {
		var url = "${ctx}/enterprise/account/changeStatus";
		if (status == 1) {
			status = 0;
		} else {
			status = 1;
		}
		$
				.ajax({
					type : "POST",
					url : url,
					data : {
						accountId : accountId,
						status : status,
						"token" : "<c:out value='${token}'/>"
					},
					error : function(request) {
						top
								.handlePrompt("error",
										'<spring:message code="common.modifyStatusFailed"/>');
					},
					success : function() {
						top
								.handlePrompt("success",
										'<spring:message code="common.modifyStatusSucessed"/>');
						refreshWindow();
					}
				});
	}
</script>
</html>
