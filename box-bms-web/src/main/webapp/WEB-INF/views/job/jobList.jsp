<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
<%@ include file="../common/common.jsp"%>
<script src="${ctx}/static/js/public/JQbox-hw-grid.js" type="text/javascript"></script>
</head>


<body>
<div class="sys-content">
	<div class="alert"><i class="icon-lightbulb"></i><spring:message code="authorize.job.manage.description"/></div>
    
	<div id="jobList" class="table-con">
    </div>

</div>
</body>
</html>
<script type="text/javascript">
var headData = {
		"model" : {"title" : "<spring:message code="job.table.title.model"/>", "width": "115px"},
		"jobName" : {"title" : "<spring:message code="job.table.title.jobname"/>"},
		"jobType" : {"title" : "<spring:message code="job.table.title.type"/>", "width": "85px", "cls": "ac"},
		"state" : {"title" : "<spring:message code="job.table.title.state"/>", "width": "65px", "cls": "ac"},
		"totalSuccess" : {"title" : "<spring:message code="job.table.title.successcount"/>", "width": "150px", "cls": "ac"},
		"totalFailed" : {"title" : "<spring:message code="job.table.title.failedcount"/>", "width": "135px", "cls": "ac"},
		"cron" : {"title" : "<spring:message code="job.table.title.cron"/>", "width": "120px"},
		"oper" : {"title" : "<spring:message code="job.table.title.oper"/>", "width": "140px", "cls": "ac"},
		};
		
var optsGrid = $("#jobList").comboTableGrid({
	headData : headData,
	dataId : "",
	miniPadding: false,
	border: true
}); 

$(document).ready(function() {
	$.fn.comboTableGrid.setItemOp = function(tableData, rowData, tdItem, colIndex){
		switch (colIndex) {
			case "oper":
				try {
					var buttonRecord = '<button class="btn" type="button" onClick="listJobExecuteRecord(\''+ rowData.clusterId + '\',\'' + rowData.jobName +'\',\'' + rowData.model +'\')" ><spring:message code="job.oper.executerecord" /></button>';
					tdItem.find("p").html(buttonRecord);
				} catch (e) {}
				break;
			case "jobType":
			case "state":
			case "totalSuccess":
			case "totalFailed":
				try {
					tdItem.removeAttr("title");
				} catch (e) {}
				break;
			default : 
				break;
		}
	}
	
	$.ajax({
	    type: "GET",
	    async: true,
	    cache: false,
	    url:"${ctx}/job/list",
	    error: function(request) {
	    },
	    success: function(data) {
	    	$("#jobList").setTableGridData(data, optsGrid);
	    }
	});
});

function listJobExecuteRecord(clusterId, jobName, modelName){
	top.ymPrompt.win({
		message:'${ctx}/job/recordPage/' + clusterId + '/' + jobName + '/' + modelName,
		width:850,height:600,
		title:'<spring:message code="job.oper.executerecord" />', iframe:true,
	});
	top.ymPrompt_addModalFocus("#btn-focus");
}
</script>