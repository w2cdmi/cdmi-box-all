<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="pw.cdmi.box.disk.utils.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="version" value="0.10.1"/>
<% request.setAttribute("token", CSRFTokenManager.getTokenForSession(session)); %>
<%--<link rel="stylesheet" href="${ctx}/static/jquery-weui/lib/weui.min.css">--%>
<!-- <link rel="stylesheet" href="${ctx}/static/jquery-weui/css/jquery-weui.css"> -->
<%--<link rel="stylesheet" href="${ctx}/static/skins/default/css/header.css"/>--%>
<link rel="stylesheet" href="${ctx}/static/skins/default/css/chooserFolder.css">
<link rel="stylesheet" href="https://cdn.bootcss.com/font-awesome/4.7.0/css/font-awesome.min.css">
<link rel="stylesheet" href="${ctx}/static/css/default/style.css?v=${version}" >
<link rel="stylesheet" href="${ctx}/static/skins/default/css/folder/tabs.css?v=${version}" >
<link rel="stylesheet" href="${ctx}/static/skins/default/css/video-js.css?v=${version}" >
<link rel="stylesheet" href="${ctx}/static/skins/default/css/viewer.min.css?v=${version}" >
<script src="https://cdn.bootcss.com/jquery/2.1.4/jquery.min.js"></script>
<script src="${ctx}/static/jquery-weui/js/jquery-weui.js"></script>
<script src="${ctx}/static/video/video.min.js"></script>
<script src="${ctx}/static/video/zh-CN.js"></script>
<script src="${ctx}/static/pictureView/viewer.min.js"></script>
<script src="${ctx}/static/pictureView/viewer-jquery.min.js"></script>
<%--<script src="${ctx}/static/jquery/validate/jquery.validate.min.js"></script>--%>
<%--<script src="${ctx}/static/jquery/validate/messages_bs_zh.js"></script>--%>
<%--<script src="${ctx}/static/jquery/iscroll-probe.js"></script>--%>
<script src="${ctx}/static/components/Components.js?v=${version}"></script>
<script src="${ctx}/static/js/common/common.js?v=${version}"></script>
<script src="${ctx}/static/js/common/linkIndex.js?v=${version}"></script>
<script src="https://res.wx.qq.com/open/js/jweixin-1.2.0.js"></script>

<script type="text/javascript">
	var token = "${token}";
    var userToken = "${sessionScope.platToken}"; //登录后从ECM获取的访问token
	var ownerId = 0;
	var curUserId = 0; //当前登录用户的账户Id
	var enterpriseUserId = 0; //当前登录账户Id
	var ctx = "${ctx}";
	var host = "";
	var parentId = "${parentId}";
	var corpId = "${sessionScope.corpId}";
	/*已登录才能使用cloudUserId变量*/
	<shiro:authenticated>
		ownerId = <shiro:principal property="cloudUserId"/>;
		curUserId = <shiro:principal property="cloudUserId"/>;
        enterpriseUserId = <shiro:principal property="id"/>;
	</shiro:authenticated>

$(function () {
    //如果没有从会话中获取到corp，尝试从当前cookie中获取
    if(corpId === undefined || corpId === null || corpId === '') {
        corpId = getCookie("corpId");
    }

    //初始化AJAX请求，401错误，主动跳转到登录页面。
    initAjax();

    pushHistory();
});

function initAjax() {
    $.ajaxSetup({
        beforeSend: function(xhr) {
            xhr.setRequestHeader("Authorization", userToken);
            xhr.setRequestHeader("Content-Type","application/json");
        },
        statusCode: {401: function() {
                //401为未登录或会话超时错误，跳转到login
                window.location = ctx + '/login';
            }
        }/*,
        complete: function(xhr, status) {
            //401为未登录或会话超时错误，跳转到log
            if(xhr.status === 401) {
                window.location = ctx + '/login';
            }
        }*/
    });
}

function pushHistory() {
    window.addEventListener("popstate", function(e) {
        self.location.reload();
    }, false);

    var state = {
        title : "",
        url : "#"
    };
    window.history.replaceState(state, "", "#");
}

</script>
