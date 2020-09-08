<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="cse" uri="http://cse.huawei.com/custom-function-taglib"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="Cache-Control" content="no-cache" />
<meta http-equiv="X-UA-Compatible" content="IE=10" />
<meta http-equiv="X-UA-Compatible" content="IE=9" />
<meta http-equiv="X-UA-Compatible" content="IE=8" />
<META HTTP-EQUIV="Expires" CONTENT="0">
<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
<META HTTP-EQUIV="Cache-control" CONTENT= "no-cache, no-store, must-revalidate">
<META HTTP-EQUIV="Cache" CONTENT="no-cache"> 

<title></title>
<link href="${ctx}/static/skins/default/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/static/skins/default/css/public.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/static/skins/default/css/main.css" rel="stylesheet" type="text/css" />
<script src="${ctx}/static/js/public/jquery-1.10.2.min.js" type="text/javascript"></script>
<script src="${ctx}/static/js/public/bootstrap.min.js" type="text/javascript"></script>
<script src="${ctx}/static/js/public/common.js" type="text/javascript"></script>
<script src="${ctx}/static/js/public/JQbox-hw-grid.js" type="text/javascript"></script>
</head>

<% 
response.setHeader("Cache-Control","no-cache, no-store, must-revalidate");
response.setHeader("Pragma","no-cache");
response.setDateHeader("Expires",0);
%>

<body>
<div class="sys-content">
	<div class="alert"><i class=""></i>${cse:htmlEscape(jobName)} <spring:message code="job.oper.executerecord"/></div>
    
	<div id="jobRecordList" class="table-con">
    </div>

</div>
</body>
</html>
<script type="text/javascript">
var headData = {
		"executeMachine" : {"title" : "<spring:message code="job.record.table.title.machine"/>", "width": "140px"},
		"executeTime" : {"title" : "<spring:message code="job.record.table.title.time"/>", "width": "140px", "cls": "ac"},
		"times" : {"title" : "<spring:message code="job.record.table.title.times"/>", "width": "140px", "cls": "ac"},
		"result" : {"title" : "<spring:message code="job.record.table.title.result"/>", "width": "140px", "cls": "ac"},
		"output" : {"title" : "<spring:message code="job.record.table.title.output"/>"},
		};
		
var optsGrid = $("#jobRecordList").comboTableGrid({
	headData : headData,
	dataId : "",
	miniPadding: true,
	border: true
}); 

$(document).ready(function() {
	$.fn.comboTableGrid.setItemOp = function(tableData, rowData, tdItem, colIndex){
		switch (colIndex) {
			case "executeMachine":
				break;
			case "executeTime":
				break;
			case "times":
				break;
			case "result":
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
	    url:"${ctx}/job/record/${cse:htmlEscape(clusterId)}/${cse:htmlEscape(jobName)}",
	    error: function(request) {
	    },
	    success: function(data) {
	    	$("#jobRecordList").setTableGridData(data, optsGrid);
	    }
	});
});
</script>