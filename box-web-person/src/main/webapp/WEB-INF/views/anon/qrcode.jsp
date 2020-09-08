<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="pw.cdmi.box.disk.utils.*"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<%
	request.setAttribute("token",
			CSRFTokenManager.getTokenForSession(session));
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="viewport"
	content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no" />
<link rel="stylesheet" href="${ctx}/static/jquery-weui/lib/weui.min.css">
<link rel="stylesheet"
	href="${ctx}/static/jquery-weui/css/jquery-weui.css">
<link rel="stylesheet" href="${ctx}/static/skins/default/css/main.css">
<link rel="stylesheet" type="text/css"
	href="${ctx}/static/skins/default/css/folder/folderIndex.css" />
<link rel="stylesheet" type="text/css"
	href="${ctx}/static/skins/default/css/share/inputMailAccessCode.css" />
<script src="${ctx}/static/jquery-weui/lib/jquery-2.1.4.js"></script>
<script src="${ctx}/static/jquery-weui/js/jquery-weui.js"></script>
<script src="${ctx}/static/jquery/validate/jquery.validate.min.js"></script>
<script src="${ctx}/static/js/common/line-scroll-animate.js"></script>
<script src="${ctx}/static/js/common/common.js"></script>
<title>微信机器人</title>

<style type="text/css">
.box{
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
}
.sed-out-link {
	padding-top: 1rem;
}
.bread-crumb{
	overflow: scroll;
}
#uploadFileList>div {
	margin-top: 0.5rem;
}
</style>
</head>
<body>

	<div class="box">
		<div class="link-header">
			<div class="logo logo-layout"></div>
			<span class="logo-name">企业文件宝</span>
		</div>
		<div class="fillBackground"></div>
		<div>
			<div class="sed-out-link" style="padding-top: 5rem">
				<div>
					<div style="width: 10rem; height:10rem; background-color: #EEF2F5; margin: 0 auto;">
						<img src="${ctx}/api/v2/wxRobot/getQrcode?code=${code}"style="width: 100%; height: 100%;"/>
					</div>
					<div class="sed-out-link-span" style="margin-top: 0.5rem">用微信扫描二维码启动机器人</div>
				</div>
			</div>
		</div>

	
		<div class="weui-footer footer-layout">
			<p class="weui-footer__links">
				<a href="https://www.filepro.cn/wxwork" class="weui-footer__link">华一云网</a>
			</p>
			<p class="weui-footer__text">Copyright © 2017-2018 filepro.cn</p>
		</div>
	</div>

	<script id="fileTemplate" type="text/template7">
	<div class="file line-content" id="file_{{id}}" style="border-bottom: 1px solid #E4E1E1;" onclick="optionInode(this)">
			<div class="file-info" >
				{{#js_compare "this.imgPath==null"}}
					<div class="img {{divClass}}"></div>
				{{else}}
            		<div class="img {{divClass}}" style="background:url({{imgPath}}) no-repeat center center;"></div>
            	{{/js_compare}}
                <div>
				  <div class="fileName">{{name}}</div>
                    {{#js_compare "this.type==1"}}
				     <span>{{size}}</span><span> | </span><span>{{modifiedAt}}</span>
                    {{else}}
                     </span><span>{{modifiedAt}}</span>
                    {{/js_compare}}	
                </div>
            {{#js_compare "this.type==1"}}
                 {{#js_compare "this.permissionFlag.download==1"}}
                   <div class="img" fileId={{id}} style="width:1.0rem;margin-top:1.5rem;height:1.0rem;float:left;background:url(${ctx}/static/skins/default/img/download.png) no-repeat center center;background-size:100% 100%; " onclick=" downloadFile(this)"></div>
                 {{/js_compare}}
            {{/js_compare}}			
          </div>
   </div>
</script>
</body>

</html>






