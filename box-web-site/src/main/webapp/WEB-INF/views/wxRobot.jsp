
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="./include/head.jsp"></jsp:include>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style/login.css "/>
    <script src="https://res.wx.qq.com/connect/zh_CN/htmledition/js/wxLogin.js"></script>
    <script src="https://rescdn.qqmail.com/node/ww/wwopenmng/js/sso/wwLogin-1.0.0.js"></script>
<style>
.tab > dt .active {
    border-bottom: solid 2px #EA5036;
}
#qrcode iframe {
    width: 425px;
}
@media (max-width: 1290px) {
    #layer-1>div,#ad-3{
        width: 1280px !important;
    }
}
</style>
</head>
<body style="overflow: auto;position: absolute;height: 100%;width: 100%">
<%
    request.setAttribute("page", 4) ;
%>
<jsp:include page="./include/menu.jsp"></jsp:include>
<div id="layer-1" style="margin-top: 60px;height: 670px;">
    <div style="background: url(${ctx}/static/assets/images/bg1_02.png);background-size:cover ;position: absolute;left: 0;top:60px;right: 0px;height: 340px">
        <div style="width: 1280px;margin: 0 auto">
            <div class="box messagebox-success">

                <div id="qrcode"></div>

                <c:if test="${!empty errorMessage}">
						<div id="errortipQR" class="box messagebox success mt0 bt0" style="">
							<p class="title">微信扫码小程序，进行注册</p>
							<div class="QRcode"><img width="100%" src="${pageContext.request.contextPath}/static/assets/images/wxmp_filepro.jpg"/></div>
							<p class="btitle" style="font-size: 14px; color: #999999;">请使用微信扫描二维码，加入小程序</p>
						</div>
                  
                    <!--<div style="color: #ea5036;font-size: 15px">${errorMessage}</div>-->
                </c:if>
            </div>
            
        </div>
    </div>

</div>

<div id="ad-3">
    <div>
        <p class="title">企业文件宝，做我能做的，给你我有的。</p>
        <span class="line"></span>
        <p></p>
<%--
        <a class="btn" href="/register/wxwork">立即体验</a>
--%>
    </div>
    <!-- <img src="./assets/images/index_10.png" /> -->
</div>
<jsp:include page="./include/buttom.jsp"></jsp:include>

<script type="text/javascript">
    $(document).ready(function () {
    	
		var errortipQR = $('#errortipQR');
		var containers = $('#container');
		errortipQR.hide();
		$('.messagebox').css('margin-top', '120px');
		$('.messagebox').css('height', '440px');
		$('.QRcode').css('width', '250px');
		$('.QRcode').css('margin', '39px auto');
		$('.QRcode').css('height', '250px');
        wxRobotLogin();
        <c:if test="${!empty errorMessage}">
        errortipQR.show();
        $('#qrcode iframe').css('height', '400px');
        $('#qrcode .qrcode').css('width', '250px');
        $('#qrcode .qrcode').css('border', '0');
//      $('.messagebox').css('margin-top', '90px');
//		$('.messagebox').css('height', '340px');
		$('.messagebox-success p').css('text-align', 'center');
		$('.messagebox-success').css('margin-top', '25px')
		$('#ad-3').css('margin-top', '245px');
		$('#bottom-info').css('margin-top', '460px');
		$('.messagebox').css('margin-top', '0');
		$('.QRcode').css('width', '200px');
		$('.QRcode').css('margin', '14px auto');
		$('.QRcode').css('height', '200px');
		containers.css('margin-top', '175px');
           /*  $("#wwLoginButton").click(function () {
                window.location = "/";
            });
            $("#registerButton").click(function () {
                window.location = "/register/wxwork";
            }); */
        </c:if>
    });

    function wxRobotLogin(){
        //显示微信登录二维码
        var obj = new WxLogin({
            id:"qrcode",
            appid: "${wxAppId}",
            scope: "snsapi_login",
            redirect_uri: "${wxRobotRedirectUrl}",
            state: "0",
            style: "black"
        });
    }
</script>
</body>

</html>