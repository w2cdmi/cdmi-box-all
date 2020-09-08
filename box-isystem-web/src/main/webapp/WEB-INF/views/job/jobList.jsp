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
	<div id="jobList" class="table-con">
    </div>

</div>
</body>
</html>
<script type="text/javascript">
var headData = {
		"model" : {"title" : "<spring:message code="job.table.title.model"/>", "width": "8%", "cls": "ac"},
		"jobName" : {"title" : "<spring:message code="job.table.title.jobname"/>", "width": "20%", "cls": "ac"},
		"jobType" : {"title" : "<spring:message code="job.table.title.type"/>", "width": "13%", "cls": "ac"},
		"state" : {"title" : "<spring:message code="job.table.title.state"/>", "width": "8%", "cls": "ac"},
		"totalSuccess" : {"title" : "<spring:message code="job.table.title.successcount"/>", "width": "8%", "cls": "ac"},
		"totalFailed" : {"title" : "<spring:message code="job.table.title.failedcount"/>", "width": "8%", "cls": "ac"},
		"cron" : {"title" : "<spring:message code="job.table.title.cron"/>", "width": "10%", "cls": "ac"},
		"oper" : {"title" : "<spring:message code="job.table.title.oper"/>", "width": "17%", "cls": "ac"},
		};
		
var optsGrid = $("#jobList").comboTableGrid({
	headData : headData,
	dataId : "",
	miniPadding: false,
	border: true
}); 

$(document).ready(function() {
	navMenuSelected("jobManageId");
	
	$.fn.comboTableGrid.setItemOp = function(tableData, rowData, tdItem, colIndex){
		switch (colIndex) {
			case "oper":
				try {
					var buttonStop = '<button class="btn" type="button" onClick="stopJob(\''+ rowData.clusterId+ '\',\'' + rowData.model + '\',\'' + rowData.jobName +'\')" ><spring:message code="job.state.stop" /></button>';
					var buttonRunning = '<button class="btn" type="button" onClick="startJob(\''+ rowData.clusterId + '\',\'' + rowData.jobName +'\')" ><spring:message code="job.state.running" /></button>';
					var buttonRecord = '<button class="btn" type="button" onClick="listJobExecuteRecord(\''+ rowData.clusterId + '\',\'' + rowData.jobName +'\')" ><spring:message code="job.oper.executerecord" /></button>';
					
					if(true == rowData.stop)
					{
						tdItem.find("p").html(buttonRunning + '&nbsp;' + buttonRecord);
					}
					else
					{
						if(true == rowData.pauseAble)
						{
							tdItem.find("p").html(buttonStop + '&nbsp;' + buttonRecord);
						}
						else
						{
							var buttonStop = '<button class="btn" type="button" disabled ><spring:message code="job.state.stop" /></button>';
							tdItem.find("p").html(buttonStop + '&nbsp;' + buttonRecord);
						}
					}
				} catch (e) {}
				break;
			case "jobType":
				break;
			case "state":
				break;
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
	    	
	    	var pageH = $("body").outerHeight();
	    	top.iframeAdaptHeight(pageH);	
	    }
	});
});

function stopJob(clusterId, model, jobName){
	var warmMessage = getSpringExpress(model, jobName);
	top.ymPrompt.confirmInfo( {
		title :'<spring:message code="job.confirm.stop"/>',
		message : warmMessage,
		closeTxt:'<spring:message code="common.close"/>',
		handler : function(tp) {
			if(tp == "ok"){
				$.ajax({
			        type: "POST",
			        url:"${ctx}/job/stop",
			        data:{clusterId:clusterId,jobName:jobName,token:'${cse:htmlEscape(token)}'},
			        error: function(request) {
			        	top.handlePrompt("error",'<spring:message code="job.confirm.stop.failed"/>');
			        },
			        success: function() {
			        	top.handlePrompt("success",'<spring:message code="job.confirm.stop.success"/>');
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
			        }
			    });
			}
		},
		btn: [['<spring:message code="common.OK"/>', "ok"],['<spring:message code="common.cancel"/>', "cancel"]]
	});
}

function startJob(clusterId, jobName){
	top.ymPrompt.confirmInfo( {
		title :'<spring:message code="job.confirm.start"/>',
		message : '<spring:message code="job.confirm.start.warn"/>',
		closeTxt:'<spring:message code="common.close"/>',
		handler : function(tp) {
			if(tp == "ok"){
				$.ajax({
			        type: "POST",
			        url:"${ctx}/job/start",
			        data:{clusterId:clusterId,jobName:jobName,token:'${cse:htmlEscape(token)}'},
			        error: function(request) {
			        	top.handlePrompt("error",'<spring:message code="job.confirm.start.failed"/>');
			        },
			        success: function() {
			        	top.handlePrompt("success",'<spring:message code="job.confirm.start.success"/>');
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
			        }
			    });
			}
		},
		btn: [['<spring:message code="common.OK"/>', "ok"],['<spring:message code="common.cancel"/>', "cancel"]]
	});
}

function listJobExecuteRecord(clusterId, jobName){
	top.ymPrompt.win({
		message:'${ctx}/job/recordPage/' + clusterId + '/' + jobName,
		width:850,height:600,
		title:'<spring:message code="job.oper.executerecord" />', iframe:true,
	});
	top.ymPrompt_addModalFocus("#btn-focus");
}

function getSpringExpress(model, jobName){
	var newModel = model.split("[");
	var colIndex = newModel[0]+"."+jobName;
	switch (colIndex) {
	case "isystem.clearRedundantJobExecuteRecordJob":
		return '<spring:message code="isystem.clearRedundantJob.warm"/>';
	case "isystem.mailServerCheck":
		return '<spring:message code="isystem.mailServerCheck.warm"/>';
	case "ufm.autoRecoverTask":
		return '<spring:message code="ufm.autoRecoverTask.warm"/>';
	case "ufm.clearRedundantJobExecuteRecordJob":
		return '<spring:message code="ufm.clearRedundantJob.warm"/>';
	case "ufm.clearUserTask":
		return '<spring:message code="ufm.clearUserTask.warm"/>';
	case "ufm.concurrentCheckTask":
		return '<spring:message code="ufm.concurrentCheckTask.warm"/>';
	case "ufm.copyTaskTimer":
		return '<spring:message code="ufm.copyTaskTimer.warm"/>';
	case "ufm.DeleteObjectTimingJob":
		return '<spring:message code="ufm.DeleteObjectTimingJob.warm"/>';
	case "ufm.nodeStatisticsJob":
		return '<spring:message code="ufm.nodeStatisticsJob.warm"/>';
	case "dss.clearRedundantJobExecuteRecordJob":
		return '<spring:message code="dss.clearRedundantJob.warm"/>';
	case "ufm.CopyTaskMonitor":
		return '<spring:message code="ufm.CopyTaskMonitor.warm"/>';
	case "ufm.DistributeFileScanTask":
		return '<spring:message code="ufm.DistributeFileScanTask.warm"/>';
	case "ufm.distributeMirrorBackScanTask":
		return '<spring:message code="ufm.distributeMirrorBackScan.warm"/>';
	case "ufm.DistributeObjectScanTask":
		return '<spring:message code="ufm.DistributeObjectScan.warm"/>';
	case "ufm.expiredMessagesScanTask":
		return '<spring:message code="ufm.expiredMessagesScan.warm"/>';
	case "ufm.expiredScanTaskCleanTask":
		return '<spring:message code="ufm.expiredScanTaskClean.warm"/>';
	case "ufm.mirrorBackScanTask":
		return '<spring:message code="ufm.mirrorBackScanTask.warm"/>';
	case "ufm.systemFileScanTask":
		return '<spring:message code="ufm.systemFileScanTask.warm"/>';
	case "dss.autoRecoverTask":
		return '<spring:message code="dss.autoRecoverTask.warm"/>';
	default : 
		return '<spring:message code="job.confirm.stop.warn"/>';
	}
}

</script>