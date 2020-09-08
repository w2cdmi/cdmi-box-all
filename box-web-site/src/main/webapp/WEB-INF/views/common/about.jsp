<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html>
<head>
<%@ include file="../common/common.jsp"%>
</head>
<body>
<div class="pop-content about-con">
    <div class="form-horizontal">
    	<div class="control-group">
	        <label class="control-label"><spring:message code='common.field.name'/> : </label>
	        <div class="controls"><strong><spring:message code='main.title'/></strong></div>
	    </div>
	    <div class="control-group">
	        <label class="control-label"><spring:message code='common.field.version'/> : </label>
	        <div class="controls">V100R005C00 </div>
	    </div>
	</div>
	<div class="copyRight-block"><spring:message code='corpright'/></div>
</div>
</body>
</html>