<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=10"/>
    <meta http-equiv="X-UA-Compatible" content="IE=9"/>
    <meta http-equiv="X-UA-Compatible" content="IE=8"/>
    <META HTTP-EQUIV="Expires" CONTENT="0">
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <META HTTP-EQUIV="Cache-control" content="no-cache, no-store, must-revalidate">
    <META HTTP-EQUIV="Cache" CONTENT="no-cache">
    <title><spring:message code="main.title"/></title>
    
    <link rel="shortcut icon" type="image/x-icon" href="${ctx}/static/skins/default/img/temp/logo.ico">
    <link href="${ctx}/static/skins/default/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/static/skins/default/css/public.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/static/skins/default/css/login.css" rel="stylesheet" type="text/css"/>
    <link rel="stylesheet" href="${ctx}/static/skins/default/css/loginNew.css" type="text/css"/>
    <script src="${ctx}/static/js/public/jquery-1.10.2.min.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/bootstrap.min.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/uaredirect.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/JQbox-hw-switch.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/common.js" type="text/javascript"></script>
    <script src="https://res.wx.qq.com/connect/zh_CN/htmledition/js/wxLogin.js"></script>

    <%
		/*
		//获取登录前缓存的URL
        String saveRequestStr = "";
        if (null != request) {
            String savedRequestStrObject = request.getSession().getAttribute("savedRequestStr") == null ? "" : request.getSession().getAttribute("savedRequestStr").toString();
            if (StringUtils.isNotBlank(savedRequestStrObject)) {
                saveRequestStr = org.springframework.web.util.HtmlUtils.htmlEscape(savedRequestStrObject);
            }
        }
        */
        //boolean isLocalAuth = LoginRule.forgetPwd();
    %>

    <script type="text/javascript">
        $(function () {
            if (window.parent.length > 0) {
                window.top.location = "${ctx}/login";
            }

//            var u = navigator.userAgent;
//            var isAndroid = u.indexOf('Android') > -1 || u.indexOf('Adr') > -1;
//            var isIOS = !!u.match(/\(i[^;]+;(U;)?CPU.+Mac OS X/);
//            if (isAndroid) {
//                uaredirect("${ctx}/static/clientDownload/androidphone_client.html");
//            } else if (isIOS) {
//                uaredirect("${ctx}/static/clientDownload/ios_client.html");
//            }

            if (document.all) {
                document.execCommand("ClearAuthenticationCache", "false");
                delRootCookie("fileListPerPageNum");
            } else {
                <%--var xmlhttp = new XMLHttpRequest();--%>
                <%--xmlhttp.open("GET", "${ctx}/logout", false);--%>
                <%--xmlhttp.send(null);--%>
            }

            loadSysSetting("${ctx}", '<spring:message code="common.language1" />');

            // var imgData = ["${ctx}/static/skins/default/img/login-switch/s1.png",
            // 				"${ctx}/static/skins/default/img/login-switch/s2.png",
            // 				"${ctx}/static/skins/default/img/login-switch/s3.png",
            // 				"${ctx}/static/skins/default/img/login-switch/s4.png" ];
            // if ('<spring:message code="common.language1"/>'=="en") {
            // 	imgData = ["${ctx}/static/skins/default/img/login-switch/s1-en.png",
            // 				"${ctx}/static/skins/default/img/login-switch/s2-en.png",
            // 				"${ctx}/static/skins/default/img/login-switch/s3-en.png",
            // 				"${ctx}/static/skins/default/img/login-switch/s4-en.png" ];
            // }
            // $("#banner").comboSwitch({
            // 	data : imgData,
            // 	posStart : 1600,
            // 	posEnd : -800,
            // 	speed : 5000,
            // 	timer : 1000
            // });

            $('.nav-tabs a').click(function (e) {
                e.preventDefault();
                $(this).tab('show');

            });
        });
    </script>
</head>
<body>
<!--头部-->
		<div class="login-header">
			<div class="pub-width login-logo">
				<a class="login-logo-bg" href="#"></a>
				<a class="login-logo-title" href="#">闪电云</a>
			</div>
		</div>
		<!--中间内容-->
		<div class="login-content">
			
			<!--中间左边信息-->
			<div class="login-midd-content pub-width">
				<div class="midd-left">
					<div class="midd-left-con">
						<p class="midd-text">Cooperate with WeChat</p>
						<p class="midd-text midd-text-upp">experience a relax and efficient work</p>
						<div class="midd-line">
							<span></span>
						</div>
						<p class="midd-text midd-special">携手企业微信 · 轻松高效工作</p>
						<p class="midd-text">致力于打造企业级云盘一流服务</p>
						<div class="midd-exper">
							<a href="https://www.storbox.cn/register/wxwork">立即体验</a>
						</div>
					</div>
				</div>
				<!--中间登录界面-->
				<div class="midd-right" id="loginBlock">
					<div role="tabpanel" id="qrcode_login" class="fade in midd-login">
						<div class="wx-img" id="qrCodeDiv">
						</div>
						<%--显示上次登录错误--%>
						<div style="height:26px">
							<c:if test="${!empty errorMessage}">
								<div style="color: #FF6600;font-size: 16px">${errorMessage}</div>
							</c:if>
						</div>
						
						
						<div class="bus-login-button">
							<a href="https://open.work.weixin.qq.com/wwopen/sso/3rd_qrConnect?appid=${wwAppId}&redirect_uri=${wwRedirectUrl}&state=0&usertype=member">
								<img src="${ctx}/static/skins/default/img/buslogin_01.png" alt="" />
								<span>企业微信登录</span>
							</a>
							
						</div>
					</div>
				</div>
			</div>
			
			<!--焦点-->
			<div class="scroll-focus pub-width">
				<ul>
					<li class="focus-active"></li>
					<li></li>
				</ul>
			</div>
			<!--背景图-->
			<div class="login-con-bg">
				<ul>
					<li></li>
					<li class="bg-active"></li>
				</ul>
			</div>
			
		</div>
		<!--其他端-->
		<div class="other-content">
			<div class="pub-width login-other">
			<ul>
				<li>
					<div>
						 <img src="${ctx}/static/skins/default/img/android.png" alt="" /> 
					</div>
					<p>安卓端</p>
					<div class="other-code">
						<img src="${ctx}/client/getClientQRcode?type=Android" alt="" />
					</div>
				</li>
				<li>
					<div>
						<img src="${ctx}/static/skins/default/img/apple.png" alt="" />
					</div>
					<p>苹果端</p>
					<div class="other-code">
						<p>玩命开发中!!</p>
						<p>敬请期待!!</p>
						<!--<img src="${ctx}/static/skins/default/img/0810KOinhT6qrwt-.jpg" alt="" />-->
					</div>
				</li>
				<li>
					<div>
						<img src="${ctx}/static/skins/default/img/smallprogram.png" alt="" />
					</div>
					<p>微信小程序</p>
					<div class="other-code">
						<img src="${ctx}/static/skins/default/img/gh_68f695b3cc61_1280-2(1).jpg" alt="" />
					</div>
				</li>
				<li>
					<div>
						<img src="${ctx}/static/skins/default/img/mac.png" alt="" />
					</div>
					<p>MAC</p>
					<div class="other-code">
						<p>玩命开发中!!</p>
						<p>敬请期待!!</p>
						<!--<img src="${ctx}/static/skins/default/img/0810KOinhT6qrwt-.jpg" alt="" />-->
					</div>
				</li>
				<li>
					<div>
						<img src="${ctx}/static/skins/default/img/windows.png" alt="" />
					</div>
					<p>Windows</p>
					<div class="other-code">
						<p>玩命开发中!!</p>
						<p>敬请期待!!</p>
						<!--<img src="${ctx}/static/skins/default/img/0810KOinhT6qrwt-.jpg" alt="" />-->
					</div>
				</li>
			</ul>
		</div>
		
		</div>
		<!--<script type="text/javascript" src="http://libs.baidu.com/jquery/2.0.0/jquery.min.js"></script>"-->
		<script type="text/javascript">
			$(function(){
				$(".login-other ul li").hover(function(){
					$(this).children(".other-code").toggle();
				});
//				背景图效果
				$(".scroll-focus ul li").mouseover(function(){
					$(this).addClass('focus-active').siblings().removeClass("focus-active");
					var index = $(this).index();
                	i = index;
                	$(".login-con-bg ul li").eq(index).fadeIn(1000).siblings().fadeOut(1000);
				});
//				自动轮播
				var i = 0;
				var timer=setInterval(play,4000);
				function play(){
					i++;
	                i = i > 1 ? 0 : i ;
	                $(".scroll-focus ul li").eq(i).addClass('focus-active').siblings().removeClass("focus-active");
	                $(".login-con-bg ul li").eq(i).fadeIn(1000).siblings().fadeOut(1000);
				}
				$(".login-con-bg").hover(function() {
                clearInterval(timer);
	            }, function() {
	                timer=setInterval(play,4000);
	            });
			})
			
		</script>
</body>
<script type="text/javascript">
    $(document).ready(function () {
        wxLogin();
    });

    function wxLogin(){
        //显示微信登录二维码
        var obj = new WxLogin({
            id:"qrCodeDiv",
            appid: "${wxAppId}",
            scope: "snsapi_login",
            redirect_uri: "${wxRedirectUrl}",
            state: "0",
            style: "black"
        });
    }
/*
    function gotoForgetPwd(){
        window.location = "${ctx}/syscommon/enterforget";
    }

    function loadimage(){
        $("#img_captcha").attr("src","${ctx}/system/verifycode?uuid=" + "${verycodeId}&t=" + Math.random());
        $("#img_captcha").attr("title",'<spring:message code="anon.toChangecaptcha" />');
    }

    function checkName(){
        var username =  $("#username").val().replace(/^\s+|\s+$/g,"");
        if(username==null||username.length==0){
            return;
        }
        var url = "${ctx}/syscommon/checkName?username="+username;
        $.ajax({
            type: "GET",
            async: false,
            url:url,
            error: function() {
                handlePrompt("error",'<spring:message code="login.errorMsg.userNameInMoreEnterprise" />',49,157,null,"loginBlock");
                $("#username").val("");
            }
        });
    }
*/
</script>
</html>