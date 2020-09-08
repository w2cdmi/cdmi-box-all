<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="cse" uri="http://cse.huawei.com/custom-function-taglib"%>  
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
<%@ include file="../common/common.jsp"%>
<script src="${ctx}/static/js/public/JQbox-hw-switchButton.js" type="text/javascript"></script>
</head>
<body>
<div class="pop-content pop-content-en">
	<div class="form-con">
<div class="control-group">
	<label class="control-label" for=""><spring:message
			code="data.Migration.speed.sweed.total" /> :</label>
	<div class="controls">
		<label class="span4">${view.sweep}<label>
	</div>
</div>
<div class="control-group">
	<label class="control-label" for=""><spring:message
			code="data.Migreation.date" /> :</label>
	<div class="controls">
	<label><fmt:formatDate value="${view.startTime}" pattern="yyyy-MM-dd HH:mm"/>
		<c:if test="${not empty view.endTime}">
			<spring:message	code="log.till" />
			<fmt:formatDate value="${view.endTime}" pattern="yyyy-MM-dd HH:mm"/>
		</c:if>
	</label>
	</div>
</div>
<div class="control-group">
	<label class="control-label" for=""><spring:message
			code="common.success" /> :</label>
	<div class="controls">
		<label>${view.success}</label>
	</div>
</div>
<div class="control-group">
	<label class="control-label" for=""><spring:message
			code="common.success" /> :</label>
	<div class="controls" <c:if test="${view.needRed}"> style="color:#FF0000"	</c:if>>
		<label>${view.fail}</label>
	</div>
	</div>
</div>
</div>
</div>
</body>
<script type="text/javascript">
$(function(){
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
	getAccessConfigSwitch();
})
</script>
</html>
