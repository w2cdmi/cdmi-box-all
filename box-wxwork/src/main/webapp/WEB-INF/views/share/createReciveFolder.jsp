<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<%@ include file="../common/include.jsp" %>
    <link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/index.css"/>
 <link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/folder/listReciver.css"/>
 <script src="${ctx}/static/jquery-weui/js/clipboard.min.js"></script>
 <title>发送的外链</title>
</head>
<body>

<div class="setup-inbox" >
		<div class="setup-inbox-blank"></div>
		<div class="weui-cell weui-cell_swiped">
			<div class="weui-cell__bd">
				<div class="weui-cell weui-cell-change">
					<div class="weui-cell__bd" >
						<div class="index-recent-left">
                            <div class="folder-icon"></div>
						</div>
						<div class="index-recent-middle">
                            <div class="recent-detail-name">
                                <p>${name}</p>
                            </div>
                            <div class="recent-detail-other">
                                <span id="nodeDate"></span>
                            </div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="setup-inbox-blanks"></div>
		<div class="setting-tail">
			<div class="setting-bottom">
				<div class="setting-bottom-left"></div>
				<div class="setting-bottom-middle">外发</div>
				<div class="setting-bottom-right"></div>
			</div>
		</div>
		<div class="putting-tail">
			<div class="putting-tail-content">
				<div class="putting-tail-left">
					<div><img src="${ctx}/static/skins/default/img/putting-input-left.png" id="linkcopy"/></div>
					<input type="hidden" id="linkurl">
					<span>复制链接</span>
				</div>
				<div class="putting-tail-right" onclick="promptMessageShare()">
					<div><img src="${ctx}/static/skins/default/img/putting-input-right.png"/></div>
					<span>发送给同事</span>
				</div>
				<div class="putting-tail-middle" onclick="promptMessageWeChat()">
					<div><img src="${ctx}/static/skins/default/img/putting-tail-middle.png"/></div>
					<span>分享到微信</span>
				</div>
			</div>
		</div>
	</div>
	<div class="setup-inbox-mask" style="display: none;">
		<div class="setup-inbox-mask-middle">
			<div class="setup-inbox-mask-middle-right">
				<img src="${ctx}/static/skins/default/img/setup-inbox-mask.png"/>
			</div>
			<div class="setup-inbox-mask-middle-left">
				<div style="float: left;">请点击右上角</div>
				<span><img src="${ctx}/static/skins/default/img/setup-inbox-mask-more.png"/></span>
				<div id="promptMessage" style="float: left;"></div>
			</div>
		</div>
	</div>
</body>
<script type="text/javascript">
var ownerId = "${ownerId}";
var folderId = "${folderId}";
var nodeName = "${name}";
var userName = "${userName}";
var imgPathUrl = "";

$(function(){
	initLink();
	imgPathUrl = $("#reciveImgDiv").css("background-image");
	// imgPathUrl = imgPathUrl.substring(5,imgPathUrl.length-2);
});

function initLink() {
    $.ajax({
        type: "GET",
        /*    url:host+"/ufm/api/v2/nodes/"+ ownerId + "/" +iNodeId+"/links", */
        url: host + "/ufm/api/v2/links/" + ownerId + "/" + folderId,
        error: function (request) {
        },
        success: function (data) {
            if (data.links === undefined || data.links === null || data.links.length === 0) {
                createLink();
            } else {
                if (data.links.length > 0) {
                    $('#nodeDate').text(getFormatDate(data.links[0].modifiedAt));
                    if (data.links[0].role == "uploader") {
                        $("#linkurl").val(data.links[0].url);
                        showMenuItems(data.links[0].url);
                    }
                }
            }

        }
    });
}

function createLink() {
    var parameter = {
        accessCodeMode: "mail",
        plainAccessCode: "",
        role: "uploader"
    };
    var url = host + "/ufm/api/v2/links/" + ownerId + "/" + folderId;
    $.ajax({
        type: "POST",
        url: url,
        data: JSON.stringify(parameter),
        error: function (xhr, status, error) {
        },
        success: function (data) {
            $("#linkurl").val(data.url);
            showMenuItems(data.url);
        }
    });
}

var linkurlCopy = new Clipboard('#linkcopy', {
    text: function () {
        return $("#linkurl").attr("value");
    }
});
linkurlCopy.on('success', function (e) {
    $.toast("复制成功", function () {
        console.log('close');
    });
});


function showMenuItems(url) {
    $.ajax({
        type: "GET",
        data: {
            url: location.href.split('#')[0],
        },
        url: host + "/ecm/api/v2/wxOauth2/getWxWorkJsApiTicket?corpId=" + corpId,
        error: function (request) {
			$.toast("JS-SDK初始化失败");
        },
        success: function (data) {
            if (data != null) {
                wx.config({
                    debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
                    appId: data.appId, // 必填，企业微信的cropID
                    timestamp: data.timestamp, // 必填，生成签名的时间戳
                    nonceStr: data.noncestr, // 必填，生成签名的随机串
                    signature: data.signature,// 必填，签名
                    jsApiList: ["chooseImage", "previewImage", "uploadImage", "downloadImage", "onMenuShareAppMessage",
                        "onMenuShareWechat", "showOptionMenu", "showMenuItems", "showAllNonBaseMenuItem", "hideOptionMenu",
                        "hideMenuItems", "hideAllNonBaseMenuItem", "previewFile"] // 必填，需要使用的JS接口列表
                });
                wx.ready(function () {
                    wx.onMenuShareAppMessage({
                        title: '${name}', // 分享标题
                        desc: userName + '创建了收件箱', // 分享描述
                        link: url, // 分享链接
                        imgUrl: imgPathUrl,
                        success: function (e) {
                        },
                        cancel: function (e) {
                        }
                    });
                    wx.onMenuShareWechat({
                        title: '${name}', // 分享标题
                        desc: userName + '创建了收件箱', // 分享描述
                        link: url, // 分享链接
                        imgUrl: imgPathUrl,
                        success: function () {
                        },
                        cancel: function () {
                        }
                    });
                });
                wx.error(function (res) {
                    alert("wx.config failed." + res);
                });
            }
        }
    });


}

/*蒙版的显示隐藏*/
function promptMessageShare() {
    $('.setup-inbox-mask').show();
    $('#promptMessage').html('转发');
}

function promptMessageWeChat() {
    $('.setup-inbox-mask').show();
    $('#promptMessage').html('分享到微信');
}

$('.setup-inbox-mask').click(function () {
    $('.setup-inbox-mask').css('display', 'none');
})
</script>
</html>