<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="cse" uri="http://cse.huawei.com/custom-function-taglib"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html>
<head>
<%@ include file="../common/common.jsp"%>
<script src="${ctx}/static/js/public/JQbox-hw-grid.js"
	type="text/javascript"></script>
</head>

<body>
<div class="sys-content sys-content-stati">
	<h5><spring:message code="statistics.currentUserAccount" /></h5>
	<div id="appStatisChartContent" class="chart-con" style="display:none;">
		<img id="appStatisChart" name="appStatisChart" src="" />
	</div>
	<div id="regionStatisChartContent" class="chart-con" style="display:none;">
		<img id="regionStatisChart" name="regionStatisChart"/>
	</div>
	
</div>
</body>
</html>
<script type="text/javascript">
$(document).ready(function(){
	var appId = "<c:out value='${authAppList[0].authAppId}'/>";
	var params = {
			"appId" : appId,
		};
	$.ajax({
		type : "GET",
		url : "${ctx}/statistics/fixedChartList/" + "<c:out value='${treeNodeId}'/>",
		data : params,
		error : function(request) {
			_statusText = request.statusText;
			if (_statusText == "Unauthorized") {
				handlePrompt("error", "<spring:message code='unauthorized.fail' />");
			}else if(_statusText == "Internal Server Error"){
				handlePrompt("error", "<spring:message code='internal.server.error' />");
			}else if(_statusText == "InternalServerError"){
				handlePrompt("error", "<spring:message code='internal.server.error' />");
			}
		},
		success : function(data) {
			var appStatisChart = "${ctx}/"+ data[0];
			var regionStatisChart = "${ctx}/"+ data[1];
			
			if(appStatisChart == "${ctx}/"){
				$("#appStatisChartContent").hide();
			}else{
				$("#appStatisChart").attr("src", appStatisChart); 
				$("#appStatisChartContent").show();
			}
			if(regionStatisChart == "${ctx}/"){
				$("#regionStatisChartContent").hide();
			}else{
				$("#regionStatisChart").attr("src", regionStatisChart);
				$("#regionStatisChartContent").show();
			}
			
			var pageH = $("body").outerHeight();
			top.iframeAdaptHeight(pageH);
		}
	});
});

function getLoading(){
	return document.getElementById("pageLoadingContainer");
}

	
</script>