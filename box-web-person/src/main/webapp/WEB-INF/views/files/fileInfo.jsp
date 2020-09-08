<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<%@ include file="../common/include.jsp" %>
<link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/files/fileInfo.css"/>
<title>文件详情</title>
</head>
<body>
	<!-- JiaThis Button BEGIN -->
	<script type="text/javascript" charset="utf-8" src="http://static.bshare.cn/b/buttonLite.js#uuid=&style=-1"></script>
	<script type="text/javascript" charset="utf-8" src="http://static.bshare.cn/b/bshareC3.js"></script>

    <div class="file-details-subject">
    	<div class="file-details-subject-position">
	        <div class="fillBackground"></div>
	        <div class="share-homepage-header">
				<div class="share-homepage-top">
					<span id="fileIcon" class="file-png"></span>
				</div>
				<div class="share-homepage-right">
					<div class="share-homepage-middle"><div id="fileName"></div></div>
					<div class="share-homepage-bottom">
						<div class="share-homepage-bottom-size"><div>文件大小 : </div><div id="fileSize"></div></div>
					</div>
				</div>
				<div class="share-homepage-header-icon" style="clear: both;"></div>
			</div>
			<div class="fillBackground"></div>
			<div id="linkListWrapper" style="display: none"></div>
			
			<div class="fillBackground"></div>
			
		
            <div id="shareListWrapper" style="display: none"></div>
        </div>
    </div>
	<script type="text/javascript">
		var ownerId = "${ownerId}";
		var fileId = "${fileId}";
		var token = "${token}";
		var type = GetQueryString("type");
		var fileName = GetQueryString("fileName");
		
		if(type==0){
			$('.share-homepage-bottom-size div').hide();
		}else{
			$('.share-homepage-bottom-size div').show();
		}
		
        var roleMsgs = {
            "auther": "<spring:message code='systemRole.title.auther'/>",
            "editor": "<spring:message code='systemRole.title.editor'/>",
            "uploadAndView": "<spring:message code='systemRole.title.uploadAndView'/>",
            "viewer": "<spring:message code='systemRole.title.viewer'/>",
            "uploader": "<spring:message code='systemRole.title.uploader'/>",
            "downloader": "<spring:message code='systemRole.title.downloader'/>",
            "previewer": "<spring:message code='systemRole.title.previewer'/>",
            "lister": "<spring:message code='systemRole.title.lister'/>",
            "prohibitVisitors": "<spring:message code='systemRole.title.prohibitVisitors'/>"
        };

        function GetQueryString(name)
        {
             var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
             var r = window.location.search.substr(1).match(reg);
             if(r!=null)return  decodeURI(r[2]); return null;
        }
	</script>
    <script type="text/javascript" src="${ctx}/static/js/files/fileInfo.js"></script>
</body>
</html>