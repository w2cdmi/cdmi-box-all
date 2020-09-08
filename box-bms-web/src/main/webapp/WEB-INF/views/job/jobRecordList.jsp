<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
<meta HTTP-EQUIV="Expires" CONTENT="0">
<meta HTTP-EQUIV="Cache-control" CONTENT= "no-cache, no-store, must-revalidate">
<meta HTTP-EQUIV="Cache" CONTENT="no-cache"> 
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="X-UA-Compatible" content="IE=10" />
<meta http-equiv="X-UA-Compatible" content="IE=9" />
<meta http-equiv="X-UA-Compatible" content="IE=8" />
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
	<div class="alert"><i class=""></i><c:out value='${jobName}'/> <spring:message code="job.oper.executerecord"/></div>
    
	<div id="jobRecordList" class="table-con">
    </div>

</div>
</body>
</html>
<script type="text/javascript">
var headData = {
		"executeMachine" : {"title" : "<spring:message code="job.record.table.title.machine"/>", "width": "140px"},
		"executeTime" : {"title" : "<spring:message code="job.record.table.title.time"/>", "width": "140px", "cls": "ac"},
		"times" : {"title" : "<spring:message code="job.record.table.title.times"/>", "width": "80px", "cls": "ac"},
		"result" : {"title" : "<spring:message code="job.record.table.title.result"/>", "width": "75px", "cls": "ac"},
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
			case "executeTime":
			case "times":
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
	    url:"${ctx}/job/record/${clusterId}/<c:out value='${jobName}'/>/<c:out value='${modelName}'/>",
	    error: function(request) {
	    },
	    success: function(data) {
	    	$("#jobRecordList").setTableGridData(data, optsGrid);
	    }
	});
});
</script>