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
		<h5><spring:message code="statistics.userAccountIncrement" /></h5>
		<div class="form-horizontal form-con clearfix">
			<form name="createIncreUserForm" id="createIncreUserForm" class="row-fluid">
				<div class="span4">
					<div class="control-group">
						<label for="input" class="control-label"><spring:message code="statistics.interval" />:</label>
						<div class="controls">
							<select class="span12" id="interval" name="interval">
								<option value="day"><spring:message code="statistics.day" /></option>
								<option value="week"><spring:message code="statistics.week" /></option>
								<option value="month"><spring:message code="statistics.month" /></option>
								<option value="season"><spring:message code="statistics.season" /></option>
								<option value="year"><spring:message code="statistics.year" /></option>
							</select>
						</div>
					</div>
					<div class="control-group" id="appSelect">
						<label for="input" class="control-label"><spring:message code="statistics.selectApplication" />:</label>
						<div class="controls">
							<select class="span12" id="appId" name="appId">
								<option value="" id="allApp"><spring:message code="statistics.allApplication" /></option>
								<c:forEach items="${authAppList}" var="authApp">
									<option value="<c:out value='${authApp.authAppId}'/>"><c:out value='${authApp.authAppId}'/></option>
								</c:forEach>
							</select>
						</div>
					</div>
				</div>
				<div class="span8">
					<div class="control-group">
						<label for="input" class="control-label"><spring:message code="statistics.date" />:</label>
						<div class="controls">
							<input type="hidden" id="beginTime" name="beginTime" /> 
							<input type="hidden" id="endTime" name="endTime" /> 
							<input type="hidden" id="treeNodeId" name="treeNodeId" value="<c:out value='${treeNodeId}'/>" /> 
							<input class="Wdate span3" readonly="true" type="text" id="beginTimeComp" name="beginTimeComp"
								value='<fmt:formatDate value="${queryCondition.beginTime}" pattern=""/>' onClick=""> - 
							<input class="Wdate span3" readonly="true" type="text" id="endTimeComp" name="endTimeComp" value='<fmt:formatDate value="${queryCondition.endTime}" pattern="yyyy-MM-dd"/>' onClick="WdatePicker({dateFmt:'yyyy-MM-dd',minDate:'2013-06-01', isShowWeek: true})">
							<button class="btn btn-link" type="button" onclick="exportData();"><spring:message code="export.data" /></button>
						</div>
					</div>
				<div for="input" class="control-group">
					<button class="btn btn-primary" type="button" onclick="submitCondition()"><spring:message code="statistics.createChart" /></button>
				</div>
					
				</div>
				<input type="hidden" id="token" name="token" value="<c:out value='${token}'/>"/>	
			</form>
		</div>

		<div class="chart-con" id="increStatisChartContent" style="display:none;">
				<img id="increStatisChart" name="increStatisChart"/>
		</div>
		
		<div class="table-con" id="increStatisTable" style="display:none"></div>

	</div>
</body>
</html>
<script type="text/javascript">
var opts_viewGrid = null;
var catalogData = null;
var headData = {
		"name" : {
			"title" : '<spring:message code="header.app" />',
			"width" : "25%"
		},
		"added" : {
			"title" : '<spring:message code="statistics.userAccountIncrement" />',
			"width" : "25%"
		},
		"unit" : {
			"title" : '<spring:message code="statistics.interval" />',
			"width" : "25%"
		},
		"date" : {
			"title" : '<spring:message code="statistics.date" />',
			"width" : "25%"
		}
	};

function init() {
	opts_viewGrid = $("#increStatisTable").comboTableGrid({
		headData : headData,
		ItemOp : "user-defined",
		dataId : "id",
		dataNullTip : "<spring:message code="statistics.noData" />"
	});
	
	$.fn.comboTableGrid.setItemOp = function(tableData, rowData, tdItem,
			colIndex) {
		switch (colIndex) {
		case "name":
			tdItem.find("p").prepend(rowData.legend).parent().attr("title", rowData.legend);
			break;
		case "unit":
			if(rowData.timePoint.unit == "year"){
				tdItem.find("p").prepend('<spring:message code="statistics.year" />').parent().attr("title", '<spring:message code="statistics.year" />');
			}else if(rowData.timePoint.unit == "season"){
				tdItem.find("p").prepend('<spring:message code="statistics.season" />').parent().attr("title", '<spring:message code="statistics.season" />');
			}else if(rowData.timePoint.unit == "month"){
				tdItem.find("p").prepend('<spring:message code="statistics.month" />').parent().attr("title", '<spring:message code="statistics.month" />');
			}else if(rowData.timePoint.unit == "week"){
				tdItem.find("p").prepend('<spring:message code="statistics.week" />').parent().attr("title", '<spring:message code="statistics.week" />');
			}else if(rowData.timePoint.unit == "day"){
				tdItem.find("p").prepend('<spring:message code="statistics.day" />').parent().attr("title", '<spring:message code="statistics.day" />');
			}
			break;
		case "date":
			if(rowData.timePoint.unit == "year"){
				tdItem.find("p").empty();
				tdItem.find("p").prepend(rowData.timePoint.year).parent().attr("title", rowData.timePoint.year);
			}else if(rowData.timePoint.unit == "season"){
				tdItem.find("p").empty();
				tdItem.find("p").prepend(rowData.timePoint.year + "-" + rowData.timePoint.number).parent().attr("title", rowData.timePoint.year + "-" + rowData.timePoint.number);
			}else if(rowData.timePoint.unit == "month"){
				tdItem.find("p").empty();
				tdItem.find("p").prepend(rowData.timePoint.year + "-" + rowData.timePoint.number).parent().attr("title", rowData.timePoint.year + "-" + rowData.timePoint.number);
			}else if(rowData.timePoint.unit == "week"){
				tdItem.find("p").empty();
				tdItem.find("p").prepend(rowData.timePoint.year + "-" + rowData.timePoint.number).parent().attr("title", rowData.timePoint.year + "-" + rowData.timePoint.number);
			}
			else if(rowData.timePoint.unit == "day"){
				tdItem.find("p").empty();
				tdItem.find("p").prepend(rowData.timePoint.year + "-" + rowData.month +"-" + rowData.date).parent().attr("title", rowData.timePoint.year + "-" + rowData.month +"-" + rowData.date);
			}
			break;
		default:
			break;
		}
	};
}

$(document).ready(function(){
	init();
	var nowTime = getNowFormatDate();
	$("#beginTimeComp").val(nowTime);
	$("#beginTimeComp").attr("onClick", "WdatePicker({dateFmt:'yyyy-MM-dd',maxDate:'nowTime', errDealMode: 1, isShowWeek: true})");

	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
});


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


function submitCondition() {
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
	if(beginTime != ""){
		$("#beginTime").val(beginTime +" 00:00:00");
	}else{
		$("#beginTime").val("");
	}
	if(endTime != ""){
		$("#endTime").val(endTime +" 23:59:59");
	}else{
		$("#endTime").val("");
	}
	$.ajax({
		type : "POST",
		url : "${ctx}/statistics/increUserAccount",
		data :  $("#createIncreUserForm").serialize(),
		error : function(request) {
			_statusText = request.statusText;
			if (_statusText == "Unauthorized") {
				handlePrompt("error", "<spring:message code='unauthorized.fail' />");
			}else if(_statusText == "Internal Server Error"){
				handlePrompt("error", '<spring:message code="statistics.access.fail"/>');
			}else if(_statusText == "InternalServerError"){
				handlePrompt("error", '<spring:message code="statistics.access.fail"/>');
			}
		},
		success : function(data) {
			var increStatisChart = "${ctx}/"+ data.urls[0];
			
			catalogData = data.histUserStatisDataset.data;
			if(increStatisChart == "${ctx}/"){
				$("#increStatisChartContent").hide();
			}else{
			$("#increStatisChart").attr("src", increStatisChart);
			$("#increStatisChartContent").show();
			$("#increStatisTable").show();
			$("#increStatisTable").setTableGridData(catalogData, opts_viewGrid);
			}
			
			var pageH = $("body").outerHeight();
			top.iframeAdaptHeight(pageH);
		}
	});
		
}
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
			+ beginTime + "&endTime=" + endTime + "&type=user";
		return;
	}else{
		window.location.href = "${ctx}/statistics/excelExport?beginTime="
			+ beginTime + "&endTime=" + endTime + "&type=user";
	}
}
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