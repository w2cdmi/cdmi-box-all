<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!DOCTYPE html>
<html>
<head>
    <jsp:include page="include/head.jsp"></jsp:include>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style/main.css "/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/assets/js/main.js"/>
</head>
<body style="overflow: auto;position: absolute;height: 100%;width: 100%">
<%
    request.setAttribute("page",1) ;
%>
<jsp:include page="include/menu.jsp" ></jsp:include>
<div id="layer-1">
    <div style="width: 900px; margin: 0 auto;position: relative">
    <dl class="left">
        <dt><h1>企业文件宝</h1><div class="line"></div></dt>
        <dt><span>企业微信和小程序配合</span></dt>
        <dt><span>让企业文档管理和企业营销更简单</span></dt>
    </dl>
    <dl class="right">
        <dt><p>数据知识营销时代已经到来， 文件宝独家全员营销模式，用知识的智慧，透明知晓用户行为，为用户定制化营销策略</p></dt>
        <dt><div class="line"></div></dt>
        <dt><p>个人空间100M/人+共享空间200M/企业</p></dt>
<%--
        <dt><a href="/register/wxwork">免费使用</a></dt>
--%>
    </dl>
    </div>

</div>
<div id="container" class="container">

    <div id="layer-1-content">
        <h1>我们的功能</h1>
        <div id="intro1" class="cl">
            <span>
               <i id="infoexe"></i>
               <h2>信息管控</h2>
               <p style="text-align: left; line-height: 20px; margin-left: 150px;">事前提供丰富的安全策略<br/>事中文档外发审批审计<br/>事后用户操作的日志审计</p>
            </span>
            <span>
                <i id="share"></i>
               <h2>便捷分享</h2>
               <p style="text-align: left; line-height: 20px; margin-left: 110px;">通过企业微信/qq/企业微信/邮件等便捷分享文件，同时支持多种权限设置以保文件的安全</p>
           </span>
        </div>

        <div id="intro2" class="cl">
            <span>
                <h2>微信营销</h2>
                <div>
                    <h2>微信营销</h2>
                    <p>记录微信转发路径/文件查看时间/查看人员/查看次数等操作</p>
                </div>
            </span>
            <span>
                    <h2>协作共享</h2>
                    <div>
                        <h2>协作共享</h2>
                        <p>部门或者员工可以创建协作分享，该空间的文档可以设立相应人员进行操作，满足部门的统一管理或者员工的协同</p>
                    </div>
                </span>
            <span>
                <h2>文档交换</h2>
                <div>
                    <h2>文档交换</h2>
                    <p>用户外发和共享文件给对方，客户便捷接受文档；并且可以创建收集箱，合作伙伴通过该收集箱地址上传他的文件，从而实现对文档的接收</p>
                </div>
            </span>
            <span>
                <h2>企业知识库</h2>
                <div>
                    <h2>企业知识库</h2>
                    <p>满足知识共享，知识交流的目的，任何人即可创建的兴趣空间，可以指定或者申请加入</p>
                </div>
            </span>
            <span>
                <h2>微信备份</h2>
                <div>
                    <h2>微信备份</h2>
                    <p>灵活备份和访问微信数据，智能归档图片/视频/文档/聊天记录分类，永不过期</p>
                </div>
            </span>
            <span>
                <h2>开放整合</h2>
                <div>
                    <h2>开放整合</h2>
                    <p>所有功能API进行了开放，企业其他知识分享应用可以调用这些接口，快速的建立自身应用的文档共享关系</p>
                </div>
            </span>
        </div>
    </div>
</div>
<div id="ad-1">
    <div>
        <p class="title">企业文件宝&amp;企业微信</p>
        <span class="line"></span>
        <p>强强联合，为150万享受企业微信便捷带来安全风险的企业提供强力的文档安全管控保障</p>
<%--
        <a class="btn" href="/register/wxwork">立即体验</a>
--%>
    </div>
</div>
<div class="container">

    <div id="layer-2-content">
        <h1>产品价值</h1>
        <div id="intro1" class="cl">
               <span>
                   <i id="infosalf"></i>

               </span>
            <span>
                   <div style="position: absolute;top:60px;left: 60px">
                       <div class="line"></div>
                       <h2>信息安全管控</h2>
                       <p style="text-align: left">企业信息安全管理流程与规范的合规工具，防止企业文档资产外泄与流失</p>
                    </div>
               </span>
            <span>
                   <div style="position: absolute;top:60px;right: 60px">
                       <div class="line" style="position: absolute;right: 0"></div>
                       <h2 style="text-align: right">知识传承</h2>
                       <p style="text-align: right">利于将员工个人知识转化为企业资产。注重知识分享，实现全员提升</p>
                   </div>
               </span>
            <span>
                   <i id="infoextend"></i>
               </span>
        </div>

        <div id="intro2" class="cl">
               <span>
                   <img src="${pageContext.request.contextPath}/static/assets/images/function2_21.png" style="width: 100%"/>
                   <h2>上下游互动</h2>
                   <p>便捷的文件分享与收发，构建员工、客户、合作伙伴间的安全沟通通道</p>
               </span>
            <span>
                   <img src="${pageContext.request.contextPath}/static/assets/images/function2_23.png" style="width: 100%"/>
                    <h2>全员营销</h2>
                   <p>通过对分享文档的大数据分析，知晓用户行为，方便企业定制化营销策略</p>
               </span>
            <span>
                   <img src="${pageContext.request.contextPath}/static/assets/images/function2_25.png" style="width: 100%"/>
                    <h2>数据备份</h2>
                   <p>提供全方位用户级数据备份，包含桌面磁盘备份、微信备份、移动设备数据备份、邮件备份</p>
               </span>
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
<jsp:include page="include/buttom.jsp"></jsp:include>
<div id="dialogs">
    <div style="position: fixed;bottom: 100px;right:60px;">
        <button id="return_button" class="gotop" style="display:block;border:none;width: 58px;height: 58px;overflow: hidden;font-weight: 100"></button>

        <button style="letter-spacing : 3px;margin-top: 15px;display:block;border:none;width: 58px;height: 58px;background:#EA5036;color: #fff">在线客服</button>

    </div>
</div>
<script src="${pageContext.request.contextPath}/static/assets/js/vendor.min.js"></script>
<script src="${pageContext.request.contextPath}/static/assets/js/main.js"></script>
<script src="https://ziker-talk.yun.pingan.com/chatboard/im.min.js?channel=WEBIM&authorizerAppid=webim2c83aec44342e0a&eid=8d63dc0263dee4b9bf8b8f6f5cf6cede&theme=07c5ba"></script>

<script>
    var _hmt = _hmt || [];
    (function() {
        var hm = document.createElement("script");
        hm.src = "https://hm.baidu.com/hm.js?e0ad146a259e63e7614c4edde8d03f91";
        var s = document.getElementsByTagName("script")[0];
        s.parentNode.insertBefore(hm, s);
    })();
</script>
</body>
</html>