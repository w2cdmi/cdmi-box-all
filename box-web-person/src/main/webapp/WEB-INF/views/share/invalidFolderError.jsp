<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
<%@ include file="../common/common.jsp"%>
</head>
<body>

<%@ include file="../common/header.jsp"%>

<div class="body">
	<div class="body-con clearfix body-con-no-menu">
		<div class="page-error">
			<h2>目录不存在</h2>
			<button class="btn btn-large" onclick="goBack()" type="button"><spring:message code="button.back" /></button>
		</div>
    </div>
</div>

<%@ include file="../common/footer.jsp"%>

<script type="text/javascript">
$(function(){
	loadSysSetting("${ctx}",'<spring:message code="common.language1" />');
})

function goBack(){
	history.go(-1);
}
</script>
</body>
</html>