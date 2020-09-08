<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="pw.cdmi.box.disk.utils.CSRFTokenManager"%>
<%@ page import="pw.cdmi.box.disk.utils.PreviewUtils"%>
<%@ page import="pw.cdmi.box.disk.utils.CustomUtils"%>
<%@ page import="pw.cdmi.box.disk.utils.PropertiesUtils"%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="pragma" content="no-cache"> 
<meta http-equiv="cache-control" content="no-cache"> 
<meta http-equiv="expires" content="0">
<meta http-equiv="X-UA-Compatible" content="IE=10" />
<meta http-equiv="X-UA-Compatible" content="IE=9" />
<meta http-equiv="X-UA-Compatible" content="IE=8" />
<title><spring:message code="main.title" /></title>
<META HTTP-EQUIV="Expires" CONTENT="0">
<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
<META HTTP-EQUIV="Cache-control"
	CONTENT="no-cache, no-store, must-revalidate">
<META HTTP-EQUIV="Cache" CONTENT="no-cache">
<link rel="shortcut icon" type="image/x-icon" href="${ctx}/static/skins/default/img/temp/logo${iconAccountId}.ico" />
<link href="${ctx}/static/skins/default/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/static/skins/default/ymPrompt/ymPrompt.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/static/js/easyui/themes/icon.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/static/skins/default/css/public.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/static/zTree/zTreeStyle.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/static/lightbox/lightbox.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/static/skins/layout/<c:out value='${pageLayoutType}'/>.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/static/skins/default/css/main.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/static/js/easyui/themes/default/easyui.css" rel="stylesheet" type="text/css" />
<%
if("true".equals(CustomUtils.getValue("cloudapp.commonTips"))){
%>
<link href="${ctx}/static/skins/layout/topNavigationForHBED.css" rel="stylesheet" type="text/css" />
<%
}
%>
<%
response.setHeader("Cache-Control","no-cache, no-store, must-revalidate");
response.setHeader("Pragma","no-cache");
response.setDateHeader("Expires",0);
request.setAttribute("token", org.springframework.web.util.HtmlUtils.htmlEscape(CSRFTokenManager.getTokenForSession(session)));
%>
<script type="text/javascript">
  document.domain = '<%=PropertiesUtils.getProperty("document.domain") %>';
</script>
<script src="${ctx}/static/js/public/jquery-1.10.2.min.js" type="text/javascript"></script>
<script src="${ctx}/static/js/public/bootstrap.min.js" type="text/javascript"></script>
<script src="${ctx}/static/js/public/ymPrompt.source.js" type="text/javascript"></script>
<script src="${ctx}/static/js/public/spinLoading/spin.min.js" type="text/javascript"></script>
<script src="${ctx}/static/lightbox/jquery.lightbox.js" type="text/javascript"></script>
<script src="${ctx}/static/zTree/jquery.ztree.all-3.5.min.js" type="text/javascript"></script>
<script src="${ctx}/static/js/public/common.js" type="text/javascript"></script>
<script src="${ctx}/static/js/public/validate/jquery.validate.min.js" type="text/javascript"></script>
<script src="${ctx}/static/js/easyui/jquery.easyui.min.js" rel="stylesheet" type="text/javascript"></script>
<script type="text/javascript">
  
	ymPrompt.setDefaultCfg({
		closeTxt:'<spring:message code="button.close"/>',
		okTxt:'<spring:message code="button.ok"/>',
		cancelTxt:'<spring:message code="button.cancel"/>'
	})
	
	$(function(){
		<%-- pop video preview --%>
		var htmlVideoModal = '<div class="modal hide" id="playVideoModal" tabindex="-1" role="dialog" aria-hidden="true" data-backdrop="static">'+
				'<div class="modal-header">'+
				'<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>'+
				'<h3></h3></div>'+
				'<div class="modal-body">'+
				'<video controls="controls" preload="auto" width="560" height="380" src ="">Your browser does not support HTML5 video.</video>'+
				'</div></div>';
		if(!top.$("#playVideoModal").get(0)){
			top.$("body").append(htmlVideoModal);
		}
		$.fn.lightbox.defaults.strings = { 
			closeTitle: "<spring:message code='button.close'/>", 
			image: "<spring:message code='file.tips.image'/>",
			rotateR: "<spring:message code='lightbox.rotateR'/>",
			rotateL: "<spring:message code='lightbox.rotateL'/>",
			download: "<spring:message code='button.download'/>",
			prev: "<spring:message code='lightbox.previous'/>",
			next: "<spring:message code='lightbox.next'/>",
			autoplay: "<spring:message code='lightbox.autoplay'/>"
			};
		$.fn.lightbox.getPreviewUrl = function(url){
			var previewUrl;
			$.ajax({
		        type: "GET",
		        async: false,
		        url: url,
		        cache: false,
		        error: function(request) {
		        	if(!$("#lightErrorText").get(0)){
		        		$("#lightbox #loading").before('<div id="lightErrorText" style="position:absolute; width:350px; left:0; top:170px; text-align:center; color:#888;"></div>');
		        	}
		        	
		        	var responseObj = $.parseJSON(request.responseText);
		        	switch (responseObj.code) {
						case "FileScanning":
							$("#lightErrorText").text("<spring:message code='file.errorMsg.fileNotReady'/>");
							break;
						case "ScannedForbidden":
							$("#lightErrorText").text("<spring:message code='file.errorMsg.downloadNotAllowed'/>");
							break;
						default:
							$("#lightErrorText").text("");
							break;
					}
					previewUrl = "${ctx}/static/lightbox/img/img-broken.png";
		        },
		        success: function(data) {
		        	if($("#lightErrorText").get(0)){
		        		$("#lightErrorText").remove();
		        	}
		        	previewUrl = data;
		        }
		    });
		    previewUrl += "|||${ctx}/static/lightbox/img/img-broken.png";
	        return previewUrl;
		}
	})
	

</script>
</script>