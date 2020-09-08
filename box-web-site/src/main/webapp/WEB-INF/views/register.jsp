<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="include/head.jsp"></jsp:include>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/style/login.css "/>
</head>
<body style="overflow: auto;position: absolute;height: 100%;width: 100%">
<jsp:include page="include/menu.jsp"></jsp:include>
<div id="layer-1" style="margin-top: 60px;height: 670px;">
	<div style="background: url(./assets/images/bg1_02.png);background-size:cover ;position: absolute;left: 0;top:60px;right: 0px;height: 340px">
		<div style="width: 1280px;margin: 0 auto">
			<div class="box messagebox-warning">
				<div class="header"><h3>注册企业文件宝</h3></div>
				<h3>公司已经注册企业微信</h3>
				<h4>公司已经注册企业微信，并且您是公司管理员身份；如果您不是公司管理员，需向公司申请管理员权限</h4>
				<button id="install_button" style="cursor: pointer">安装应用到企业微信</button>
				<h3>公司未注册、重新注册</h3>
				<h4>公司未注册、重新注册新公司,  立即注册企业微信</h4>
				<button id="register_button" style="cursor: pointer">注册企业微信</button>
			</div>
		</div>
	</div>

</div>
<jsp:include page="include/buttom.jsp"></jsp:include>

<script>
    $(document).ready(function() {
        $("#install_button").click(function(){
            window.open("https://open.work.weixin.qq.com/3rdapp/install?suite_id=${suiteId}&pre_auth_code=${preauthCode}&redirect_uri=${redirectUrl}&state=0");
        });
        $("#register_button").click(function(){
            window.open("https://open.work.weixin.qq.com/3rdservice/wework/register?register_code=${registerCode}");
        })
    });
</script>
</body>
<%--
<body>
<div class="buswx-header">
			<div class="pub-width">
				<div class="bus-header-logo">
					<a href="#"><img src="${ctx}/static/skins/default/img/logo_03.png" alt="" /><spring:message code='main.title'/></a>
				</div>
			</div>
		</div>
		<!--内容区域-->
		<div class="buswx-content">
			<div class="buswx-register-con">
				<p class="register-title">企业微信用户注册</p>
				<p class="title-line"></p>
					<a class="install-blue-img"  href="">
						<img src="${ctx}/static/skins/default/img/install_blue_small.png">
					</a>
				<p class="buswx-alert-info">已安装企业微信用户，点击上方链接注册</p>
				<div class="register-or">
					<i></i>
					<span>or</span>
					<i></i>
				</div>
				<p class="no-install-wx">暂未安装企业微信用户，点击下方链接注册</p>
				<a class="register-buswx-btn" href="https://open.work.weixin.qq.com/3rdservice/wework/register?register_code=${registerCode}">
					<img src="${ctx}/static/skins/default/img/register_white_small.png" alt="" />
				</a>
			</div>
			<div class="pub-width buswx-bottom">
				<p><spring:message code='corpright'/></p>
			</div>
		</div>
</body>--%>
</html>