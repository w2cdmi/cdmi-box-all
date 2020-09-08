<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<div id="menubar">
    <div class="cl">
        <span><a href="${ctx}/"><i id="logo"></i></a></span>
        <dl class="cl tab">
            <dt><a href="${ctx}/"  ${page==1?"class=\"active\"":""}>首页</a></dt>
            <dt><a href="${ctx}/price" ${page==2?"class=\"active\"":""}>价格</a></dt>
            <dt><a href="${ctx}/download" ${page==3?"class=\"active\"":""}>产品下载</a></dt>
            <dt><a href="${ctx}/wxRobot" ${page==4?"class=\"active\"":""}>微信备份</a></dt>
            <!--dt><a href="/aboutus.html" class="active">联系我们</a></dt-->
        </dl>
        <ul class="cl buttongroup">
            <li><a href="${ctx}/register/wxwork" target="_blank">注册</a></li>
            <li><a href="javascript:void(0)" id="login_button">登录</a></li>
            <%--<li><a href="https://open.work.weixin.qq.com/wwopen/sso/3rd_qrConnect?appid=${wwAppId}&redirect_uri=${wwRedirectUrl}&state=0&usertype=member" id="login_button" target="_self">登录</a></li>--%>
        </ul>
    </div>
    <div id="loginDialog" style="width: 500px;height: 450px">
        <div class='tabs' id="tabs">
            <ul class='horizontal'>
                <li rel="tab-1" id="personCode" class="selectActive">
                    <span>文件宝个人用户</span>
                </li>
                <li rel="tab-2" id="enterpriseCode">
                    <span>文件宝企业用户</span>
                </li>
                <li rel="tab-3" id="enterpriseWxCode">
                    <span>企业微信用户</span>
                </li>
            </ul>
            <div id="tab1" rel='tab-1'>
                <div id="perQrcode" class="tab-code-img">

                </div>
            </div>
            <div id="tab2" rel='tab-2'>
                <div id="qrcodeEnterprise" class="tab-code-img">

                </div>
            </div>
            <%--<div id="tab3" rel='tab-3'>--%>
                <%--<div class="tab-code-img">--%>
                    <%--<img src="" alt="">--%>
                <%--</div>--%>
                <%--<p class="code-title">请用企业微信扫描二维码</p>--%>
            <%--</div>--%>
        </div>
    </div>
</div>
<link rel="stylesheet" href="${ctx}/static/style/tabs.css">
<script src="https://res.wx.qq.com/connect/zh_CN/htmledition/js/wxLogin.js"></script>
<script src="${ctx}/static/assets/js/Components.js" type="text/javascript"></script>
<script src="${ctx}/static/assets/js/tabs.js" type="text/javascript"></script>
<script>
    function wxLogin(){
        //显示微信登录二维码
        var obj = new WxLogin({
            id:"perQrcode",
            appid: "${wxAppId}",
            scope: "snsapi_login",
            redirect_uri: "${wxPersonalRedirectUrl}",
            state: "0",
            style: "black"
        });
    }
    function wxEnterpriseLogin(){
        //显示微信登录二维码
        var obj = new WxLogin({
            id:"qrcodeEnterprise",
            appid: "${wxAppId}",
            scope: "snsapi_login",
            redirect_uri: "${wxEnterpriseRedirectUrl}",
            state: "0",
            style: "black"
        });
    }
    var loginDialog;
    loginDialog= $("#loginDialog").dialog({title: '登录'})
    loginDialog.init()
    $("#login_button").click(function () {
        loginDialog.show()
        console.log($("#qrcode"))
//        $(".box ").remove()
        wxLogin()
        $("#tab1").show()
    })
//    tabs_takes.init("tabs");
    $("#personCode").on("click",function () {
        $(this).addClass("selectActive").siblings().removeClass("selectActive")
        $("#qrcodeEnterprise").children().remove()
        $("#tab1").show()
        $("#tab2").hide()
        wxLogin()

    })
    $("#enterpriseCode").on("click",function () {
        $(this).addClass("selectActive").siblings().removeClass("selectActive")
        $("#perQrcode").children().remove()
        $("#tab1").hide()
        $("#tab2").show()
        wxEnterpriseLogin()

    })
    $("#enterpriseWxCode").on("click",function () {
        $(this).addClass("selectActive").siblings().removeClass("selectActive")
        window.location="https://open.work.weixin.qq.com/wwopen/sso/3rd_qrConnect?appid=${wwAppId}&redirect_uri=${wwRedirectUrl}&state=0&usertype=member"
    })
</script>