<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="include/head.jsp"></jsp:include>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style/download.css "/>
    <script src="${pageContext.request.contextPath}/static/assets/js/Components.js"></script>
</head>
<body style="overflow: auto;position: absolute;height: 100%;width: 100%">
<%
    request.setAttribute("page",3) ;
%>
<jsp:include page="include/menu.jsp"></jsp:include>
<div id="layer-1" style="height: 420px;">
    <div style="background: url(${pageContext.request.contextPath}/static/assets/images/download/c.png);background-size:cover ;position: absolute;left: 0;top:60px;right: 0px;height: 420px">
        <div >
            <div class="product-info" style="position: absolute;right: 200px;top:60px;">
                <div><h3>企业微信PC端</h3></div>
                <ul>
                    <li>文件下载/文件共享/文件外发/信息管控</li>
                    <li>拖拽式文件上传</li>
                    <li>目录上传</li>
                </ul>
<%--
                <button id="pc_download" style="margin-top: 50px;">立即使用</button>
--%>
            </div>
        </div>
    </div>
</div>

    <div id="container" class="container">
        <div id="layer-2" style="height: 740px;width:1040px;margin: 50px auto 0 auto;" class="cl">
        <span class="box" style="width: 437px;height: 687px;float: left">
           <img src="${pageContext.request.contextPath}/static/assets/images/download/a.png"/>
             <div class="product-info" style="margin: 0 auto;margin-top:20px;width: 280px">
                <div><h3>企业微信移动端</h3></div>
                <ul style="margin-left: 0;">
                    <li>文件上传/文件共享/文件外发/信息管控</li>
                    <li>便捷分享</li>
                    <li>微信营销</li>
                    <li>文件预览</li>
                    <li>文档交换</li>
                </ul>
<%--
                <button id="mobile_download" style="margin-top: 20px;margin-left: 60px;">立即使用</button>
--%>
            </div>
        </span>
            <span class="box" style="margin-left: 139px;width: 437px;height: 687px;float: left">
            <img src="${pageContext.request.contextPath}/static/assets/images/download/b.png"/>
                <div id="wxmpQr" style="border: solid 1px #ccc;width: 160px;height: 160px;margin: 20px;display: none"><img style="width: 100%;" src="${pageContext.request.contextPath}/static/assets/images/wxmp_filepro.jpg"></img></div>
             <div class="product-info"  style="margin: 0 auto;margin-top:20px;width: 280px" >
                <div><h3>微信小程序</h3></div>
                <ul style="margin-left: 60px;">
                    <li>文件预览</li>
                    <li>一键分享</li>
                    <li>微信营销</li>
                    <li>私密聊天</li>
                </ul>
<%--
                <button id="wxmp_download" style="margin-top: 50px;margin-left: 60px;">立即使用</button>
--%>
            </div>
        </span>
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
<jsp:include page="include/buttom.jsp"></jsp:include>
    <div id="wxmp_download_dialog" style="width: 200px;height: 200px;">
        <div style="border: solid 1px #ccc;width: 160px;height: 160px;margin: 20px;"><img style="width: 100%;" src="${pageContext.request.contextPath}/static/assets/images/wxmp_filepro.jpg"></img></div>
    </div>
    <script>
        $(document).ready(function() {
            $("#pc_download, #mobile_download").click(function(){
                window.open("https://work.weixin.qq.com/#indexDownload", "_blank");
            });

            var dialog = $('#wxmp_download_dialog').dialog({title: "微信小程序"});
            dialog.init();
            $("#wxmp_download").click(function(){
                // $("#wxmpQr").show();
                // dialog.toCenter();
                dialog.show();
            })
        });
    </script>
</body>
</html>