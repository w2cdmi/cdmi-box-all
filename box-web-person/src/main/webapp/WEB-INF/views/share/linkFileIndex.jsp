<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<%@ include file="../common/include.jsp"%>
<link rel="stylesheet" type="text/css"
	href="${ctx}/static/skins/default/css/share/inputMailAccessCode.css" />
<title>外发文件</title>

<style type="text/css">
	.download-option-guide-model{
		position: fixed;
	    top: 0;
	    right: 0;
	    left: 0;
	    bottom: 0;
	    background: rgba(0,0,0,0.5);
	    z-index: 3;
	}
</style>
</head>
<body>
	<div class="box">
		<div class="link-header">
			<div class="logo logo-layout"></div>
			<span class="logo-name">企业文件宝</span>
			<div class="share-name-div">分享者: ${shareUserName}</div>
		</div>
		<div class="fillBackground"></div>
		<div class="sed-out-link">
			<div style="margin: 0 auto;width: 6.5rem;height: 6.5rem;background-color: #EEF2F5;">
				<div style="height: 6.5rem; width: 6.5rem; text-align: center;">
					<div id="iconDiv" style="margin: 0 auto; float: none; background-size: 3rem 3rem; width: 3rem; height: 3rem;padding-top: 3.3rem;">
					</div>
				</div>
			</div>
			<div class="sed-out-link-details">${iNodeName}</div>
			<div class="link-file-info" id="fileInfoDiv"></div>
			<div class="sed-out-link-tail">
				<div class="determine-sign-in" onclick="downloadFile()"
					id="downloadbtn">下载</div>
					<div class="determine-sign-in" onclick="previewFile()" id="previewbtn">预览</div>
				<!-- <div class="determine-sign-in" id="previewbtn">预览</div> -->
			</div>
		</div>
		
		<div class="weui-footer footer-layout">
	      <p class="weui-footer__links">
	        <a href="https://www.filepro.cn/wxwork" class="weui-footer__link">企业文件宝</a>
	      </p>
	      <p class="weui-footer__text">版权所有 © 华一云网科技成都有限公司 2017-2018.</p>
    	</div>
	</div>
	<a id="downloadFile" download="${iNodeName}" style="display: none"></a>
	
	<div class="download-option-guide-model" style="display:none;">
		<img src="${ctx }/static/skins/default/img/link-model.png" style="width: 90%;margin-left: 5%;margin-top: 20%;"></img>
	</div>
</body>
<script type="text/javascript">
	var curUserId = '<shiro:principal property="cloudUserId"/>';
	var linkCode = '${linkCode}';
	var accessCode = '${accessCode}';
	var iNodeName = '${iNodeName}';
	var isLoginUser = '${isLoginUser}';
	var ownerId = '${ownerId}';
	var fileId = '${folderId}';
	var catalogData = null;
	var orderField = "modifiedAt";
	var isNeedVerify = '${isNeedVerify}';
	var shareUserName = '${shareUserName}';
	var permissionFlag = null;

	$(function() {
		function previewFile() {
  var previewable = isFilePreviewable('${iNodeName}')
  if (previewable) {
   gotoPage('${ctx}/p/view/' + linkCode)
  } else {
   if (permissionFlag.preview == 1 && permissionFlag.download == 0) {
    $.alert("该文件格式不支持预览，请联系分享人给予下载权限。")
   } else if (permissionFlag.download == 1 && permissionFlag.preview == 1) {
    $.alert("该文档由于不支持预览，如果要下载，请使用第三方软件打开。")
   }
  }
 }

		pageload();
		
		linkFileInfoInit();
		
		$(".download-option-guide-model").click(function(){
			$(".download-option-guide-model").hide();
		});
	});

    function getToken() {
        var token = "link," + linkCode;

        if (accessCode !== undefined && accessCode !== null && accessCode !== "") {
            token += ("," + accessCode);
        }

        return token;
    }

    function linkFileInfoInit(){
		$("#iconDiv").addClass(getImgHtml(1, iNodeName));
		$("#fileInfoDiv").empty();
		var linkCreateTime = '${linkCreateTime}';
		linkCreateTime = getFormatDate(new Date(linkCreateTime),"yyyy-MM-dd");
		if(typeof(linkCreateTime) != "undefined" && linkCreateTime != ""){
			$("#fileInfoDiv").append("<span>"+ linkCreateTime +"</span>");
		}else{
			return;
		}
		var fileSize = ${iNodeSize};
		if(fileSize == "" || typeof(fileSize)!="number"){
			return;
		}
		fileSize = formatFileSize(fileSize);
		$("#fileInfoDiv").append("|<span>"+ fileSize +"</span>");
	}

	function getNodeName() {
		return '<c:out value="${iNodeName}"/>';
	}

	function getthumbnailUrl() {
		return '<c:out value="${thumbnailUrl}"/>';
	}

	function pageload() {
		if (permissionFlag != null && permissionFlag["download"] == 1) {
			$("#download-button").show();
		}
		if (isLoginUser == "true") {
			$("#MyFavorite-button").show();
			if (permissionFlag != null && permissionFlag["download"] == 1) {
				$("#saveToMe-button").show();
			}
		}
	}
	function previewFile() {
  var previewable = isFilePreviewable('${iNodeName}')
  if (previewable) {
   gotoPage('${ctx}/p/view/' + linkCode)
  } else {
   if (permissionFlag.preview == 1 && permissionFlag.download == 0) {
    $.alert("该文件格式不支持预览，请联系分享人给予下载权限。")
   } else if (permissionFlag.download == 1 && permissionFlag.preview == 1) {
    $.alert("该文档由于不支持预览，如果要下载，请使用第三方软件打开。")
   }
  }
 }

	function downloadFile() {
		$.ajax({
            type: "GET",
            url: host + "/ufm/api/v2/files/" + ownerId + "/" + id + "/url",
            beforeSend: function(xhr) {
                //通过Header设置鉴权信息
                xhr.setRequestHeader("Authorization", getToken());
            },
			error : function(request) {
				if (request.status == 405) {
					top.location.reload();
				} else {
					// doDownLoadLinkError(request);
				}
			},
			success : function(data) {
				if (typeof (data) == 'string' && data.indexOf('<html>') != -1) {
					top.window.location.reload();
					return;
				}
				if(is_weixin){
					$(".download-option-guide-model").show();
					return;
				}
				$("#downloadFile").attr("href", data);
				document.getElementById("downloadFile").click();
			}
		});
	}
	
	var is_weixin = (function(){
		return navigator.userAgent.toLowerCase().indexOf('micromessenger/6.5') !== -1;
	})();

	function listFile(curPage, parentId) {
	}
</script>
</html>