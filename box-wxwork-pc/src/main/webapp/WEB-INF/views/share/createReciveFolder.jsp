<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<%@ include file="../common/include.jsp" %>
 <link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/folder/listReciver.css"/>
 <script src="${ctx}/static/jquery-weui/js/clipboard.min.js"></script>
 <title>发送的外链</title>
</head>
<body>

<div class="setup-inbox" >
		<div class="setup-inbox-blank"></div>
		<div class="share-homepage-header">
			<div class="share-homepage-top">
				<span style="margin-top: 0;">
					<div id="reciveImgDiv" class="folder-icon"></div>
				</span>
			</div>
			<!--<div class="setup-inbox-header-img folder-icon"></div>-->
			<div class="share-homepage-right">
				<div class="share-homepage-middle">
					<i>${name}</i>
				</div>
				<div class="share-homepage-bottom">
					<div class="share-homepage-bottom-time" style="font-size: 0.6rem;" id="nodeDate"></div>
				</div>
			</div>
<!--			<div class="setup-inbox-header-name"></div>-->
			<!--<div class="setup-inbox-header-nav" id="dir"></div>-->
		</div>
		<%-- <div class="setup-inbox-blanks"></div>
		<div class="setup-inbox-ontice">
			<div class="setup-inbox-ontice-left">通知信息</div>
			<div class="setup-inbox-ontice-right"><img src="${ctx}/static/skins/default/img/putting-more.png"/></div>
		</div> --%>
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
				<!-- <div class="putting-tail-right" onclick="promptMessageShare()">
					<div><img src="${ctx}/static/skins/default/img/putting-input-right.png"/></div>
					<span>发送给同事</span>
				</div>
				<div class="putting-tail-middle" onclick="promptMessageWeChat()">
					<div><img src="${ctx}/static/skins/default/img/putting-tail-middle.png"/></div>
					<span>分享到微信</span>
				</div> -->
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
var ownerId = "<c:out value='${ownerId}'/>";
var iNodeId = "<c:out value='${folderId}'/>";
var token = "<c:out value='${token}'/>";
var name = "<c:out value='${name}'/>";
var userName = "<c:out value='${userName}'/>";
var imgPathUrl = "";

$(function(){
	initLink();
	
	imgPathUrl = $("#reciveImgDiv").css("background-image");
	imgPathUrl = imgPathUrl.substring(5,imgPathUrl.length-2);
})
function initLink(){
		$.ajax({
	        type: "GET",
	        data:{},
	        url:"${ctx}/share/getlink/"+ ownerId + "/" +iNodeId+"?"+ new Date().getTime(),
	        error: function(request) {
	        },
	        success: function(data) {

	        	if(data==undefined||data==""){
	        		setLink();
	        	}else{
	        		if(data.length > 0){
	        			$('#nodeDate').text(getFormatDate(data[0].modifiedAt));
	        			if(data[0].upload==true){
	        				$("#linkurl").val(data[0].url);
	        				 showMenuItems(data[0].url);
	        			}
	        		}
	        	}
	        	
	        }
	    });
}


//function getPaths() {
//  var url = "${ctx}/folders/getPaths/" + ownerId + "/" + iNodeId;
//  $.ajax({
//      type: "GET",
//      url: url,
//      cache: false,
//      async: false,
//      success: function (data) {
//      	var dir="当前目录:";
//      	for(var i=0;i<data.length;i++){
//      		if(dir!=""){
//      			dir=dir+">";
//      		}
//      		dir=dir+data[i].name;
//      	}
//      	$("#dir").text(dir);
//      },error: function(){
//          $.toast('获取目录失败', 'forbidden');
//      }
//  });
//}


function setLink() {
		var defaultlinKset={
				accessCodeMode:"mail",
				accessCode:"",
				download:false,
				preview:false,
				upload:true,
				identities:"",
				token:token,
		}
		var  url="${ctx}/share/createReciveLink/"+ ownerId + "/" +iNodeId;
		$.ajax({
	        type: "POST",
	        url:url,
	        data:defaultlinKset,
	        error: function(request) {
	        },
	        success: function(data) {
	        	 $("#linkurl").val(data.url);
	        	 showMenuItems(data.url);
	        }
	    });
}
var linkurlCopy = new Clipboard('#linkcopy', {
        text: function() {
                   return $("#linkurl").attr("value");
        }
});
linkurlCopy.on('success', function(e) {
	   $.toast("复制成功", function() {

	   });
});


function showMenuItems(url){
	wx.onMenuShareAppMessage({
		title: '点击上传文件', // 分享标题
		desc:userName+ '创建了一个收件箱', // 分享描述
		link: url, // 分享链接
		imgUrl: imgPathUrl,
		success: function () {
		},
		cancel: function () {
		}
	});


	  wx.onMenuShareWechat({
		title: '点击上传文件', // 分享标题
		desc:userName+ '创建了一个收件箱', // 分享描述
		link: url, // 分享链接
		imgUrl: imgPathUrl,
		success: function () {
		},
		cancel: function () {
		}
	});
	wx.hideMenuItems({
		menuList: ["menuItem:setFont", "menuItem:refresh", "menuItem:favorite","menuItem:copyUrl","menuItem:openWithSafari"] // 要隐藏的菜单项，所有menu项见附录3
	}); 
}
/*蒙版的显示隐藏*/
// function promptMessageShare(){
//     	 $('.setup-inbox-mask').show();
//     	 $('#promptMessage').html('外发')
//     }
//     function promptMessageWeChat(){
//     	$('.setup-inbox-mask').show();
//     	$('#promptMessage').html('分享到微信')
//     }
// $('.setup-inbox-mask').click(function(){
// 	$('.setup-inbox-mask').css('display','none');
// })
</script>
</html>