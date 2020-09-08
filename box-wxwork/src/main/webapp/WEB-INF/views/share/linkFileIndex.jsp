<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>外发文件</title>

<%@ include file="../common/include.jsp"%>
<link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/share/inputMailAccessCode.css" />

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
			<div class="logo logo-layout" style="width: 5rem;background-size: 5rem 1rem;"></div>
			<div class="share-name-div">分享者: ${shareUserName}</div>
		</div>
		<div class="fillBackground"></div>
		<div class="sed-out-link">
			<div style="margin: 0 auto;width: 6.5rem;height: 6.5rem;background-color: #EEF2F5;">
				<div style="height: 6.5rem; width: 6.5rem; text-align: center;overflow:hidden;">
					<div id="iconDiv" style="margin: 1.5rem auto; float: none; background-size: 3rem 3rem; width: 3rem; height: 3rem;">
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
	        <a href="<spring:message code='company.link'/>" class="weui-footer__link"><spring:message code='main.title'/></a>
	      </p>
	      <p class="weui-footer__text"><spring:message code='corpright'/></p>
    	</div>
	</div>
	<a id="downloadFile" download style="display: none"></a>

	<div class="download-option-guide-model" style="display:none;">
		<img src="${ctx }/static/skins/default/img/link-model.png" style="width: 90%;margin-left: 5%;margin-top: 20%;"></img>
	</div>
    <div id="viewerImg"></div>
    <%@ include file="../common/previewImg.jsp" %>
	<%@ include file="../common/previewVideo.jsp" %>
</body>
<script type="text/javascript">
	var curUserId = '<shiro:principal property="cloudUserId"/>';
	var linkCode = '<c:out value="${linkCode}"/>';
	var accessCode = '<c:out value="${accessCode}"/>';
	var iNodeName = '<c:out value="${iNodeName}"/>';
	var isLoginUser = '<c:out value="${isLoginUser}"/>';
	var ownerId = '<c:out value="${ownerId}"/>';
	var fileId = '<c:out value="${folderId}"/>';
	var orderField = "modifiedAt";
	var isNeedVerify = '<c:out value="${isNeedVerify}"/>';
	var shareUserName = '<c:out value="${shareUserName}"/>';
	var permissionFlag = null;
    var img;
	$(function() {
        permissionFlag = getLinkPermission(ownerId, fileId, linkCode, accessCode);

		if (permissionFlag.download == 1 && permissionFlag.preview == 1) {
			$("#previewbtn").css("display", "block");
			$("#previewbtn").css("top", "2rem");
			$("#downloadbtn").css("display", "block");
		} else if(permissionFlag.download == 1) {
			$("#previewbtn").css("display", "none");
			$("#downloadbtn").css("display", "block");
		}else if(permissionFlag.preview == 1){
			$("#previewbtn").css("display", "block");
			$("#downloadbtn").css("display", "none");
		}

		pageload();

		linkFileInfoInit();

		$(".download-option-guide-model").click(function(){
			$(".download-option-guide-model").hide();
		});
        viewerImg()
	});

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

	function downloadFile() {
		$.ajax({
			type : "GET",
			async : false,
			url : "/ufm/api/v2/f/"+linkCode+"/url",
            beforeSend: function(xhr) {
                xhr.setRequestHeader("Authorization", getToken());
            },
			error : function(request) {
				if (request.status == 405) {
					top.location.reload();
				}
			},
			success : function(data) {
				if (typeof (data) == 'string' && data.indexOf('<html>') != -1) {
					top.window.location.reload();
					return;
				}
				if(!is_qyweixin && is_tx){
					$(".download-option-guide-model").show();
					return;
				}
				$("#downloadFile").attr("href", data.downloadUrl);
				document.getElementById("downloadFile").click();
			}
		});
	}
    function viewerImg() {
        if(isImg(iNodeName)){
            $.ajax({
                type : "GET",
                async : false,
                url: "/ufm/api/v2/f/" + linkCode + "/preview",
                beforeSend: function(xhr) {
                    xhr.setRequestHeader("Authorization", getToken());
                },
                error : function(request) {
                    if (request.status == 405) {
                        top.location.reload();
                    }
                },
                success : function(data) {

                    var imgSrc = data.url
                    img = '<img src= '+ imgSrc +' style="display: none"/>'
                    $("#viewerImg").append(img)
                }
            });

        }
    }
	function previewFile() {
		var previewable = isFilePreviewable('${iNodeName}')
		var imgable = isImg('${iNodeName}');
		var videoable = isVideo('${iNodeName}');
        var pla=ismobile(1);
		if (previewable) {
			gotoPage('/p/preview/' + linkCode) //此处使用/p绝对路径，不使用ctx路径，相关的路径转换由nginx负责完成
		}else if(imgable){
            imgClick()
		}else if(videoable && pla=="1"){
            $.ajax({
                type : "GET",
                async : false,
                url : "/ufm/api/v2/f/"+linkCode+"/preview",
                beforeSend: function(xhr) {
                    xhr.setRequestHeader("Authorization", getToken());
                },
                error : function(request) {
                    if (request.status == 405) {
                        top.location.reload();
                    }
                },
                success : function(data) {
                    var url = data.url
                    $("video").attr("src",url)
                    $("video").attr("type",videoable)
                    $(".playvideo").show()
                    $("video").get(0).play()
                    zymedia('video',{autoplay: true});
                    $("#modelView").click(function () {
                        $(".playvideo").hide()
                        $("video").get(0).currentTime = 0
                        $("video").get(0).pause()
                    })
                }
            });
		}else {
			if (permissionFlag.preview == 1 && permissionFlag.download == 0) {
				$.alert("该文件格式不支持预览，请联系分享人给予下载权限。")
			} else if (permissionFlag.download == 1 && permissionFlag.preview == 1) {
				$.alert("该文档由于不支持预览，如果要下载，请使用第三方软件打开。")
			}
		}
	}
    function imgClick() {
        var pswpElement = document.querySelectorAll('.pswp')[0];
        var items = [];
        // var images = new image()
        var getItems = function () {
            var aDiv = $("#viewerImg");
            var img = aDiv.find("img");
            var item = {
                src: $(img).attr("src"),
                w: $(img).width(),
                h: $(img).height()
            };
            items.push(item);
        };

        getItems();
        var options = {
            index: 0,
            bgOpacity:0.85,
            tapToClose:true
        };

        var gallery = new PhotoSwipe( pswpElement, PhotoSwipeUI_Default, items, options);
        gallery.init();
    }
	function getToken() {
        var token = "link," + linkCode;
        if(accessCode !== "") {
            token = token  + "," + accessCode;
        }

        return token;
    }

	var is_qyweixin = (function(){
		return navigator.userAgent.indexOf('wxwork') !== -1;
	})();

	var is_tx = (function(){
		return (navigator.userAgent.indexOf('MicroMessenger') !== -1 && navigator.userAgent.indexOf('MQQBrowser') !== -1);
	})();
</script>
</html>