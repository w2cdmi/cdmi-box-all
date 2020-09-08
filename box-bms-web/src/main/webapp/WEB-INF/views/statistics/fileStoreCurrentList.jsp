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
</head>

<body>
<div class="sys-content sys-content-stati">
	<h5><spring:message code='current.capacity' /></h5>
	<div id="appStatisChartContent" class="chart-con" style="display:none;">
		<img id="appStatisChart" name="appStatisChart" src="" />
	</div>
	<div id="regionStatisChartContent" class="chart-con" style="display:none;">
		<img id="regionStatisChart" name="regionStatisChart" src="" />
	</div>
	
</div>
</body>
</html>
<script type="text/javascript">
$(document).ready(function(){
	$.ajax({
		type : "POST",
		data : {token : "<c:out value='${token}'/>"},
		url : "${ctx}/statistics/getFileStoreCurrentView",
		error : function(data) {
			_statusText = data.response;
			switch(_statusText)
			{
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
			var appStatisChart = "${ctx}/"+ data.applicationUrl;
			var regionStatisChart = "${ctx}/"+ data.regionUrl;

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


	
</script>