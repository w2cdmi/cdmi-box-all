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
</style>

</head>

<body>
<div class="sys-content sys-content-stati">
	<h5><spring:message code="system.storeage"/></h5>
	<div id="systemStoreContent" class="chart-con" style="display:none;">
		<img id="systemStoreCurrentView" name="systemStoreCurrentView" src="" />
	</div>
</div>
</body>
</html>
<script type="text/javascript">
$(document).ready(function(){
	$.ajax({
		type : "POST",
		data : {token : "<c:out value='${token}'/>"},
		url : "${ctx}/statistics/getSystemStoreCurrentView",
		error : function(data) {
			_statusText = data.response;
			switch(_statusText)
			{
				case "Unauthorized":
					handlePrompt("error", "<spring:message code='authorized.failed'/>");
					break;
				case "InvalidParameter":
					handlePrompt("error", "<spring:message code='statistics.operationFailed'/>");
					break;
				default:
						handlePrompt("error", "<spring:message code='statistics.operationFailed'/>");
					break;
			}
		},
		success : function(data) {
			var systemStoreCurrentView = "${ctx}/"+ data.url;
			if(systemStoreCurrentView == "${ctx}/"){
				$("#systemStoreContent").hide();
			}else{
				$("#systemStoreCurrentView").attr("src", systemStoreCurrentView); 
				$("#systemStoreContent").show();
			}
			var pageH = $("body").outerHeight();
		    top.iframeAdaptHeight(pageH);
		}
	});
});


	
</script>