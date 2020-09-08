<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="pw.cdmi.box.disk.utils.*" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<% request.setAttribute("token", CSRFTokenManager.getTokenForSession(session)); %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<meta name="viewport" content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no" />
<%--<link rel="stylesheet" href="${ctx}/static/jquery-weui/lib/weui.min.css">--%>
<%--<link rel="stylesheet" href="${ctx}/static/jquery-weui/css/jquery-weui.css">--%>
<link rel="stylesheet" href="https://cdn.bootcss.com/weui/1.1.2/style/weui.min.css">
<link rel="stylesheet" href="https://cdn.bootcss.com/jquery-weui/1.2.0/css/jquery-weui.min.css">
<link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/header.css"/>
<link rel="stylesheet" href="${ctx}/static/skins/default/css/main.css">
<link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/files/createFolder.css"/>
<link rel="stylesheet" href="${ctx}/static/photoSwipe/css/photoswipe.css" >
<link rel="stylesheet" href="${ctx}/static/photoSwipe/css/default-skin/default-skin.css">
<link rel="stylesheet" href="${ctx}/static/video/zy.media.min.css" >
<script src="${ctx}/static/jquery-weui/lib/jquery-2.1.4.js"></script>
<script src="https://cdn.bootcss.com/jquery-weui/1.2.0/js/jquery-weui.min.js"></script>
<%--<script src="${ctx}/static/jquery-weui/js/jquery-weui.min.js"></script>--%>
<script src="${ctx}/static/jquery-weui/js/hammer.js"></script>
<script src="${ctx}/static/jquery-weui/js/touch-0.2.14.min.js"></script>
<script src="${ctx}/static/jquery/validate/jquery.validate.min.js"></script>
<script src="${ctx}/static/jquery/validate/messages_bs_zh.js"></script>
<script src="${ctx}/static/jquery/iscroll-probe.js"></script>
<script src="https://res.wx.qq.com/open/js/jweixin-1.2.0.js"></script>
<script src="${ctx}/static/photoSwipe/js/photoswipe.min.js"></script>
<script src="${ctx}/static/photoSwipe/js/photoswipe-ui-default.min.js"></script>
<script src="${ctx}/static/video/zy.media.min.js"></script>

<script type="text/javascript">
	var token = "<c:out value='${token}'/>";
    var userToken = "${sessionScope.platToken}"; //登录后从ECM获取的访问token
	var ownerId = 0;
	var curUserId = 0; //当前登录用户
    var curUserName = "";
	var ctx = "${ctx}";
	var parentId = "${parentId}";
	var host = "";
	var recordState = 1;	//1： 等待录音，2：录音中

	var corpId = "${sessionScope.corpId}";
	/*已登录才能使用cloudUserId变量*/
	<shiro:authenticated>
		ownerId = <shiro:principal property="cloudUserId"/>;
		curUserId = <shiro:principal property="cloudUserId"/>;
        curUserName = '<shiro:principal property="name"/>';
	</shiro:authenticated>

    <c:if test="${! empty sessionScope.host}">
        host = "${sessionScope.host}";
    </c:if>
</script>

<script src="${ctx}/static/js/common/common.js"></script>
<script src="${ctx}/static/js/common/file-view.js"></script>

