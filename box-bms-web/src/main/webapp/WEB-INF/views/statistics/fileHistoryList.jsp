<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="cse"
	uri="http://cse.huawei.com/custom-function-taglib"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html>
<head>
<%@ include file="../common/common.jsp"%>
<script src="${ctx}/static/js/public/JQbox-hw-grid.js"
	type="text/javascript"></script>
<script src="${ctx}/static/js/public/My97DatePicker/WdatePicker.js"
	type="text/javascript"></script>
</head>

<body>
	<div class="sys-content sys-content-stati">
		<h5>
			<spring:message code="statistics.file.history.num" />
		</h5>
		<div class="form-horizontal form-con clearfix">
			<form id="fileHistory" name="fileHistory" class="row-fluid">
				<div class="span4">
					<div class="control-group">
						<label for="input" class="control-label"><spring:message code="statistics.interval" />:</label>
						<div class="controls">
							<select class="span12" id="intervalContent"
								name="intervalContent">
								<option value="day"><spring:message code="statistics.day" /></option>
								<option value="week"><spring:message code="statistics.week" /></option>
								<option value="month"><spring:message code="statistics.month" /></option>
								<option value="season"><spring:message code="statistics.season" /></option>
								<option value="year"><spring:message code="statistics.year" /></option>
							</select>
						</div>
					</div>
				</div>
				<div class="span8">
					<div class="control-group">
						<label for="input" class="control-label"><spring:message code="statistics.date" />:</label>
						<div class="controls">
							<input type="hidden" id="interval" name="interval" /> <input
								type="hidden" id="beginTime" name="beginTime" /> <input
								type="hidden" id="endTime" name="endTime" /> <input
								class="Wdate span3" readonly="true" type="text"
								id="beginTimeComp" name="beginTimeComp"
								value='<fmt:formatDate value="${queryCondition.beginTime}" pattern=""/>'
								onClick=""> - <input class="Wdate span3" readonly="true"
								type="text" id="endTimeComp" name="endTimeComp"
								value='<fmt:formatDate value="${queryCondition.endTime}" pattern="yyyy-MM-dd"/>'
								onClick="WdatePicker({dateFmt:'yyyy-MM-dd',minDate:'2013-06-01', isShowWeek: true})">
							<input class="btn btn-primary" type="button" onclick="createView()"
								value='<spring:message code="statistics.createChart" />' />
							
							<button class="btn btn-link" type="button" onclick="exportData()"><spring:message code="export.data" /></button>
						</div>
					</div>
				</div>
				<input type="hidden" id="token" name="token" value="<c:out value='${token}'/>"/>	
			</form>
		</div>
		<div class="chart-con" id="histStatisChartContent"
			style="display:none;">
			<img id="histStatisChart" name="histStatisChart" src="" />
		</div>
		<div class="table-con" id="fileHistoryTable" style="display: none"></div>
	</div>
</body>
</html>
<script type="text/javascript">
var opts_viewGrid = null;
var catalogData = null;
var timeUnit = null;
var headData = {
		"actualFileCount":{
			"title":"<spring:message code='statistics.file.title.x.actual.num' />",
			"width":"25%"
		},
		"fileCount":{
			"title":"<spring:message code='statistics.file.title.x.user.num' />",
			"width":"25%"
		},
		"unit":{
			"title":"<spring:message code='statis.cycle' />",
			"width":"20%"
		},
		"date":{
			"title":"<spring:message code='statis.date' />",
			"width":"30%"
		}
};
	$(document).ready(function() {
			var nowTime = getNowFormatDate();
			$("#beginTimeComp").val(nowTime);
			$("#beginTimeComp").attr("onClick","WdatePicker({dateFmt:'yyyy-MM-dd',maxDate:'nowTime', errDealMode: 1, isShowWeek: true})");
			initTable();	
	});
	function initTable()
	{
		opts_viewGrid = $("#fileHistoryTable").comboTableGrid({
			headData : headData,
			ItemOp : "user-defined",
			dataId : "date",
			dataNullTip : "<spring:message code="statistics.noData" />"
		});
		
		$.fn.comboTableGrid.setItemOp = function(tableData, rowData, tdItem,
				colIndex) {
			tdItem.find("p").empty();
			switch (colIndex) {
			case "actualFileCount":
				tdItem.find("p").prepend(rowData.actualFileCount).parent().attr("title", rowData.actualFileCount);
				break;
			case "fileCount":
				tdItem.find("p").prepend(rowData.fileCount).parent().attr("title", rowData.fileCount);
				break;
			case "unit":
				if(timeUnit == "year"){
					tdItem.find("p").prepend('<spring:message code="statistics.year" />').parent().attr("title", '<spring:message code="statistics.year" />');
				}else if(timeUnit == "season"){
					tdItem.find("p").prepend('<spring:message code="statistics.season" />').parent().attr("title", '<spring:message code="statistics.season" />');
				}else if(timeUnit == "month"){
					tdItem.find("p").prepend('<spring:message code="statistics.month" />').parent().attr("title", '<spring:message code="statistics.month" />');
				}else if(timeUnit == "week"){
					tdItem.find("p").prepend('<spring:message code="statistics.week" />').parent().attr("title", '<spring:message code="statistics.week" />');
				}else if(timeUnit == "day"){
					tdItem.find("p").prepend('<spring:message code="statistics.day" />').parent().attr("title", '<spring:message code="statistics.day" />');
				}
				break;
			case "date":
				tdItem.find("p").prepend(rowData.date).parent().attr("title", rowData.date);
				break;
			default:
				break;
			}
		};
	}
	function getNowFormatDate() {
		var date = new Date();
		var seperator1 = "-";
		var year = date.getFullYear();
		var month = date.getMonth() + 1;
		var strDate = date.getDate();
		if (month >= 1 && month <= 9) {
			month = "0" + month;
		}
		if (strDate >= 0 && strDate <= 9) {
			strDate = "0" + strDate;
		}
		var currentdate = year + seperator1 + month + seperator1 + strDate;
		return currentdate;
	}
	function createView() {
		var nowTime = getNowFormatDate();
		var beginTime = $("#beginTimeComp").val();
		var endTime = $("#endTimeComp").val();
		if(beginTime == ""){
			handlePrompt("error",'<spring:message code="statistics.timeOperationFailed" />：<spring:message code="statistics.beginTimeNull" />');
			return;
		}
		if(beginTime > nowTime){
			handlePrompt("error",'<spring:message code="statistics.timeOperationFailed" />：<spring:message code="statistics.beginTime" /><spring:message code="statistics.timeBehind" /><spring:message code="statistics.currentTime" />');
			return;
		}
		if(endTime != "" && beginTime > endTime){
			handlePrompt("error",'<spring:message code="statistics.timeOperationFailed" />：<spring:message code="statistics.beginTime" /><spring:message code="statistics.timeBehind" /><spring:message code="statistics.endTime" />');
			return;
		}
		if (beginTime != "") {
			$("#beginTime").val(beginTime + " 00:00:00");
		} else {
			$("#beginTime").val("");
		}
		if (endTime != "") {
			$("#endTime").val(endTime + " 23:59:59");
		} else {
			$("#endTime").val("");
		}
		var interval = $('#intervalContent option:selected').val();
		$("#interval").val(interval);
		var params = {
			"beginTime" : beginTime,
			"endTime" : endTime,
			"interval" : interval
		}
		$.ajax({
			type : "POST",
			url : "${ctx}/statistics/getFileHistoryView/<c:out value='${treeNodeId}'/>",
			data : $("#fileHistory").serialize(),
			error : function(data) {
				_statusText = data.response;
				switch (_statusText) {
				case "Unauthorized":
					handlePrompt("error", "<spring:message code='unauthorized.fail' />");
					break;
				case "InvalidParameter":
					handlePrompt("error", "<spring:message code='statistics.operationFailed' />");
					break;
				default:
					handlePrompt("error", "<spring:message code='statistics.operationFailed' />");
					break;
				}
			},
			success : function(data) {
				var histStatisChart = "${ctx}/" + data.url;
				timeUnit = data.unit;
				catalogData = data.tableResponse;
				if (histStatisChart == "${ctx}/") {
					$("#histStatisChartContent").hide();
				} else {
					$("#histStatisChart").attr("src", histStatisChart);
					$("#histStatisChartContent").show();
					$("#fileHistoryTable").show();
					$("#fileHistoryTable").setTableGridData(catalogData, opts_viewGrid);
				}
				var pageH = $("body").outerHeight();
				top.iframeAdaptHeight(pageH);
			}
		});
	}
	//导出数据
	function exportData() {
		var beginTime = $("#beginTimeComp").val();
		var endTime = $("#endTimeComp").val();
		if(endTime != "" && beginTime > endTime){
			handlePrompt("error",'<spring:message code="log.operation.err.time"/>',null,60);
			return;
		}
		beginTime = timeHandler(beginTime);
		endTime = timeHandler(endTime);
		
		if(isIeBelow11()){
			top.ymPrompt.alert({title:'<spring:message code="common.title.info"/>',message:'<spring:message code="common.download.excel.file" />'});
			window.location.href = "${ctx}/statistics/excelExport?beginTime="
				+ beginTime + "&endTime=" + endTime + "&type=object";
			return;
		}else{
			window.location.href = "${ctx}/statistics/excelExport?beginTime="
				+ beginTime + "&endTime=" + endTime + "&type=object";
		}
	}
	/**
	 * 处理日期时间，获取对应时间的毫秒值
	 */
	 function timeHandler(time) 
		{
			var dateTime;
			if (null == time || "" == time || "undefined" == time) 
			{
				dateTime = new Date();
			}else 
			{
				var timeSplit = time.split("-");
				dateTime = new Date(timeSplit[0], timeSplit[1]-1, timeSplit[2]);
			}
			dateTime.setDate(dateTime.getDate() + 1);
			dateTime = dateTime.getTime();
			return dateTime;
		}
</script>