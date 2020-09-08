<%@ page language="java" import="java.util.*" pageEncoding="utf8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<meta name="viewport" content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no" />
	<title>提示</title>
	<link rel="stylesheet" href="${ctx}/static/jquery-weui/lib/weui.min.css">
	<link rel="stylesheet" href="${ctx}/static/skins/default/css/main.css">
	<link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/share/inputMailAccessCode.css" />
</head>-
<body>
	<div class="box">
		<div class="link-header">
			<div class="logo logo-layout" style="width: 5rem;background-size: 5rem 1rem;"></div>
		</div>
		<div class="fillBackground"></div>
		<div class="sed-out-link">
			<div style="margin: 0 auto;width: 6.5rem;height: 6.5rem;">
				<img style="width: 6.5rem;" src="${ctx }/static/skins/default/img/warning.png">
			</div>
			<div class="sed-out-link-details">链接失效</div>
			<div class="link-file-info">请重新获取链接</div>
		</div>
		
		<div class="weui-footer footer-layout">
	      <p class="weui-footer__links">
	        <a href="<spring:message code='company.link'/>" class="weui-footer__link"><spring:message code='company'/></a>
	      </p>
	      <p class="weui-footer__text"><spring:message code='corpright'/></p>
    	</div>
	</div>
	
</body>

</html>