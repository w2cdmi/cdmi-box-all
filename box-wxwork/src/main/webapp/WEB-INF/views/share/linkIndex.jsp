<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="javax.sound.midi.SysexMessage" %>
<%@page import="pw.cdmi.box.disk.utils.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>

<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<%@ include file="../common/include.jsp" %>
	<link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/index.css"/>
	<link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/share/linkIndex.css"/>
	<script src="${ctx}/static/jquery-weui/js/clipboard.min.js"></script>
	<title>外发</title>
</head>

<body>
<!--外发首页-->
<div class="share-home-page" id="linkFileListDiv" style="display:none">
	<%--<div class="share-homepage-header">--%>
		<%--<div class="share-homepage-top">--%>
			<%--<span><div id="linkFileImg"></div></span>--%>
		<%--</div>--%>
		<%--<div class="share-homepage-right">--%>
			<%--<div class="share-homepage-middle">--%>
				<%--<i>${name}</i>--%>
			<%--</div>--%>
			<%--<div class="share-homepage-bottom">--%>
				<%--<div class="share-homepage-bottom-size" id="nodeSize"></div>--%>
				<%--<span></span>--%>
				<%--<div class="share-homepage-bottom-time" id="nodeDate"></div>--%>
			<%--</div>--%>
		<%--</div>--%>
		<%--<div class="share-homepage-header-icon"></div>--%>
	<%--</div>--%>
	<div class="weui-cell weui-cell_swiped">
		<div class="weui-cell__bd">
			<div class="weui-cell weui-cell-change">
				<div class="weui-cell__bd" >
					<div class="index-recent-left">
						<div id="linkFileImg"></div>
					</div>
					<div class="index-recent-middle">
						<div class="recent-detail-name">
							<p>${name}</p>
						</div>
						<div class="recent-detail-other">
							<span id="nodeSize"></span>
							<span>|</span>
							<span id="nodeDate"></span>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="share-home-page-nav"><span>请选择一个外发链接</span></div>
	<div class="putting-link" id="linkDiv">
	  
	</div>
	 <script id="linkTemplate" type="text/template7"> 
	     <div class="weui-flex div-border-bottom">
		      <div class="placeholder">
                 <div class="putting-link">
					 {{#js_compare "this.plainAccessCode != undefined"}}
						<div class="puttinglink-title" style="margin-top: 1.6rem;margin-left: 1rem;">
					 {{else}}
                      	<div class="puttinglink-title" style="margin-top: 1.1rem;margin-left: 1rem;">
					 {{/js_compare}}
		                <span style="margin:0rem"  name="selectSpan" value="{{id}}" id="{{id}}"></span>
                      </div>
                 </div>
		      </div>
		      <div class="weui-flex__item linkinfo-top-bottom"><div class="placeholder">
		         <%--链接列表模板--%>
							<div class="putting-link">
								<div class="puttinglink-title">
									<div>{{url}}</div>
								</div>
								<div class="puttinglink-one" did="{{id}}">
									<div>
										{{#js_compare "this.accessCodeMode=='static'"}}
											{{#js_compare "this.plainAccessCode!=null"}}
												提取码访问
											{{else}}
												匿名访问
											{{/js_compare}}
										{{else}}
											动态码访问
										{{/js_compare}}
										<span>|</span>
									</div>
									<div>
										{{roleName}}
										<span>|</span>
									</div>
									<div>
										{{#js_compare "this.expireAt != undefined && this.expireAt != ''"}}
										  {{expireString}}
										{{else}}
										       永久有效
										{{/js_compare}}
									</div>
									
								</div>
								{{#js_compare "this.plainAccessCode != undefined"}}
								<div class="puttinglink-two">
									<div>提取码</div><span>{{plainAccessCode}}</span>
								</div>
								{{/js_compare}}
							</div>
	              
		          </div>
		      </div>
		      <div class="deleteDiv">
				{{#js_compare "this.plainAccessCode != undefined"}}
					<div class="placeholder" style="margin-top: 1.6rem;"><span onclick="deleteLink(this)" style="margin-right: 1rem">删除</span></div>
			    {{else}}
                    <div class="placeholder" style="margin-top: 1.1rem;"><span onclick="deleteLink(this)" style="margin-right: 1rem">删除</span></div>
			    {{/js_compare}}
			  </div>
         </div>
	
	    </script> 
	<div class="setting-tail">
		<div class="setting-bottom">
			<div class="setting-bottom-left"></div>
			<div class="setting-bottom-middle"><i class="weui-icon-info-circle"></i></div>
			<div class="setting-bottom-right"></div>
		</div>
	</div>
	<div class="putting-tail">
		<!-- <div class="putting-tail-content">
			<p>选择链接后，点击右上角的菜单，</p>
			<p>可以外链转发给其他联系人。</p>
			<br>
			<br>
			<a href="#">动画演示</a>
		</div> -->
		<div class="putting-tail">
			<div class="putting-tail-content">
				<div class="putting-tail-left">
					<div><img src="${ctx}/static/skins/default/img/putting-input-left.png" id="linkcopy"/></div>
					<input type="hidden"id="linkurl">
					<span>复制链接</span>
				</div>
				<div class="putting-tail-right">
					<div><img src="${ctx}/static/skins/default/img/putting-input-right.png" onclick="promptMessageShare()"/></div>
					<span>发送给同事</span>
				</div>
				<div class="putting-tail-middle">
					<div><img src="${ctx}/static/skins/default/img/putting-tail-middle.png" onclick="promptMessageWeChat()"/></div>
					<span>分享到微信</span>
				</div>
			</div>
		</div>
	</div>
	<div class="tail-add-links" onclick="gotoCreatePage()">添加一个外发链接</div>
	<div class="setting-delete" onclick="batchDeleteLink()">删除所有外发链接外链</div>
</div>

<div id="addLinkFileDiv" style="display:none">
<div class="share-senior">
		<div class="share-senior-content">
			<div class="weui-cells_radio">
				<label class="weui-cell weui-check__label" for="staticMode">
					<div class="weui-cell__bd">
						<p>匿名访问</p>
					</div>
					<div class="weui-cell__ft">
						<input type="radio" class="weui-check" name="accessCodeMode"
							id="staticMode" value="staticMode" checked="checked"> <span
							class="weui-icon-checked"></span>
					</div>
				</label> <label class="weui-cell weui-check__label" for="emailMode">

					<div class="weui-cell__bd">
						<p>动态码访问</p>
					</div>
					<div class="weui-cell__ft">
						<input type="radio" class="weui-check" name="accessCodeMode"
							id="emailMode" value="emailMode"> <span
							class="weui-icon-checked"></span>
					</div>
				</label> <label class="weui-cell weui-check__label" for="randomMode">
					<div class="weui-cell__bd">
						<p>提取码访问</p>
					</div>
					<div class="weui-cell__ft">
						<input type="radio" class="weui-check" name="accessCodeMode"
							id="randomMode" value="randomMode"> <span
							class="weui-icon-checked"></span>
					</div>
				</label>
				<li style="display: none" id="accessCodeDiv">
					<span>提取码：</span> <i id="accessCode"></i>
					<p style="margin-left: 30px;" id="btn_copy"
						data-clipboard-target="#accessCode" onclick="copylink()">复制</p>
					<h3>
						<img src="${ctx}/static/skins/default/img/putting-renovata.png"
							onclick="refreshAccessCode()" />
					</h3>
				</li>
			</div>
			<ul>
				<li>
					<div class="weui-cell__bd">允许下载</div> 
					<input hidden="hidden" id="download" type="radio" value="off" checked="checked" /> 
					<input class="weui-switch" type="checkbox" id="downloadSwitch" />
				</li>
				<li style="vertical-align: middle;"><span>有效期</span> <input
					class="weui-input" id="validityChoose" type="text" value="一周"
					style="height: 2.2rem; width: 80%; text-align: right;">
					<h2>
						<img src="${ctx}/static/skins/default/img/putting-more.png" />
					</h2>
					 <input class="weui-input" id="chooseDate" type="hidden">
			    </li>
			</ul>
		</div>

	</div>
	<div class="button_sp_area share-senior-input" style="margin-left: 1%;">
		<a href="javascript:;" onclick="setLink()">确定</a> <a
			href="javascript:;" onclick="cancelCreateLink()">取消</a>
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

<script type="text/javascript">
	function fillLinkDiv(data){
		var clipboard = new Clipboard('#plainAccessCode'+data.id, {
			// 通过target指定要复印的节点
			text: function() {
				return data.plainAccessCode;
			}
		});

		clipboard.on('success', function(e) {
			$.toast("提取成功", function() {
				console.log('close');
			});
		})
		$('#linkDiv .weui-flex').click(function(){
			$(this).siblings('#linkDiv .weui-flex').find('.puttinglink-title').children('span').removeClass('spanSelect');
			$(this).find('.puttinglink-title').children('span').addClass('spanSelect');
			showMenuItems();
		});
	}
</script>
<script type="text/javascript">
	var folderId = <c:out value='${folderId}'/>;
	var token = "<c:out value='${token}'/>";
	var ownerId = "<c:out value='${ownerId}'/>";
	var iNodeId = "<c:out value='${folderId}'/>";
	var userName = "<c:out value='${userName}'/>";
	var linkStatus = "<c:out value='${linkStatus}'/>";
	var isComplexCode = "<c:out value='${isComplexCode}'/>";
	var type = "<c:out value='${type}'/>";
	var imgPathUrl = "";
	
	$(function () {
		var size = "${size}";
		var name = "${name}";
		$("#linkFileImg").addClass(getImgHtmlOther(type,name));
		imgPathUrl = $("#linkFileImg").css("background-image");
		imgPathUrl = imgPathUrl.substring(5,imgPathUrl.length-2);
		if(size == ""){
			$("#nodeSize").next().remove();
			$("#nodeSize").remove();
		}else{
			$("#nodeSize").html(formatFileSize(size));
		}
		$("#nodeDate").html(getFormatDate(new Date("${modifiedAt}")));

		getLink();
		$("#sendLinkImag").bind('click',function(event){
			event.stopPropagation();
		});
		
	});
	//获取文件外链信息
	function getLink(){
		<%--$("#linkDiv .weui-flex").remove();--%>
		$.ajax({
			type: "GET",
			url:host+"/ufm/api/v2/links/"+ ownerId + "/" +iNodeId,
			error: handleError,
			success: function(data) {
				var links = data.links;
				var $list = $("#linkDiv");
				var $template = $("#linkTemplate");
				if(links.length>0){
					$("#linkFileListDiv").show();
					$("#addLinkFileDiv").hide();
				}else{
					gotoCreatePage();
				}
				
				for (var i = 0; i < links.length; i++) {
					links[i].index = i + 1;
					links[i].effectiveAt=new Date().getTime();
					links[i].expireString=parseExpireString(links[i].effectiveAt,links[i].expireAt);
					links[i].roleName = getAclByRole(links[i].role)
					$template.template(links[i]).appendTo($list);
					$("#"+links[i].id).data("data",links[i]);
					fillLinkDiv(links[i]);
				}
				var listspan = $('#linkDiv .weui-flex').find('.puttinglink-title').children('span');
				$(listspan[listspan.length-1]).addClass("spanSelect")
				console.log(listspan)
				showMenuItems()
				$(".deleteDiv").click(function(event){
				     event.stopPropagation();
				}) 
				
			}
		});
	}

	$('#linkDiv .putting-link').click(function(){
		$(this).siblings('.putting-link').find('.puttinglink-title').children('span').removeClass('spanSelect');
		$(this).find('.puttinglink-title').children('span').addClass('spanSelect');
	})
	function updateLinkSendStatus(linCode) {
		if(linCode==""){
			return;
		}
		var defaultlinKset={
			linkCode:linCode,
			status:1,
			token:token,
		}
		$.ajax({
			type: "POST",
			url:"${ctx}/share/updateLinkSendStatus/"+ ownerId + "/" +iNodeId,
			data:defaultlinKset,
            error: function(xhr, status, error){
			},
			success: function(data) {
			}
		});
	}

	function copyLinks(){
		var linkCodes=getSelectSpan();
		var linkCodeArr=linkCodes.split(",");
		var urls="";
		var accessCode ="";
		for(var i=0;i<1;i++){
			var access = $("#"+linkCodeArr[i]).data("data").plainAccessCode
			if(linkCodeArr[i]!="" && access!=undefined){
				urls=$("#"+linkCodeArr[i]).data("data").url;
				accessCode = " 提取码: "+$("#"+linkCodeArr[i]).data("data").plainAccessCode
			}else{
				urls=$("#"+linkCodeArr[i]).data("data").url;
				accessCode=""
			}
		}
		return urls+accessCode;

	}

	function getLinkData(){
		var linkCodes=getSelectSpan();
		var linkCodeArr=linkCodes.split(",");
		var data="";
		for(var i=0;i<1;i++){
			if(linkCodeArr[i]!=""){
				data=$("#"+linkCodeArr[i]).data("data");
			}
		}
		return data;

	}


	function batchDeleteLink(){
		var linkCodes=getSelectSpan();
		$.ajax({
			type: "DELETE",
			url:host + "/ufm/api/v2/links/${ownerId}/${folderId}",
            error: function(xhr, status, error){
				$.toast("删除失败");
			},
			success: function() {
				$.toast("删除成功");
				$("#linkDiv").empty();
			}
		});
	}

	function deleteLink(th) {
		$.confirm({
			  title: '确认删除链接',
			  text: "删除链接后，链接将不可访问",
			  onOK: function () {
				 /*  var linkId=$(th).parent().attr("did"); */
				  var linkId=$(th).parent().parent().parent().find('span').attr("id");
					$.ajax({
						type: "DELETE",
						url:host + "/ufm/api/v2/links/${ownerId}/${folderId}" + "?linkCode=" + linkId,
                        error: function(xhr, status, error){
							$.toast("删除失败");
						},
						success: function() {
							$.toast("删除成功");
							$(th).parent().parent().parent().remove();
						}
					});
			  }
		});
	}


	function goteUpdatePage(linkCode){
		gotoPage("${ctx}/share/link/" + ownerId+ "/" +iNodeId+"/modify?linkCode="+linkCode);
	}

	function gotoCreatePage(){
		$("#linkFileListDiv").hide();
	$("#linkFileListDiv").find('#linkDiv .weui-flex').find('.puttinglink-title').children('span').removeClass("spanSelect");
		$("#addLinkFileDiv").show();
		refreshAccessCode();
		if($("#download").val()=="off"){
			$("#downloadSwitch").click();
			<%--$("#downloadSwitch").attr('disabled',"disabled")--%>
		}
		
	}
	function getSelectSpan(){
		var linkids="";
		$("span[name='selectSpan']").each(function(){
			if($(this).attr("class")=="spanSelect"){
				linkids=linkids+$(this).attr("value")+",";
			}
		});
		return linkids;
	}

	function showMenuItems(){
		var linkdata=getLinkData();
		if(linkdata==""){
			return;
		}
		console.log(linkdata.url)
		$.ajax({
	        type: "GET",
	        async: false,
	        data: {
				url:location.href.split('#')[0],
			},
	        url: host + "/ecm/api/v2/wxOauth2/getWxWorkJsApiTicket?corpId="+corpId,
	        error: function (request) {
//				$.toast("JS-SDK初始化失败");
	        },
	        success: function (data) {
				if (data != null) {
	            	wx.config({
	            		debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
	            		appId: data.appId, // 必填，企业微信的cropID
	            		timestamp: data.timestamp, // 必填，生成签名的时间戳
	            		nonceStr: data.noncestr, // 必填，生成签名的随机串
	            		signature: data.signature,// 必填，签名
	            		jsApiList: ["chooseImage", "previewImage", "uploadImage", "downloadImage","onMenuShareAppMessage",
	            			"onMenuShareWechat","showOptionMenu","showMenuItems","showAllNonBaseMenuItem","hideOptionMenu",
	            			"hideMenuItems","hideAllNonBaseMenuItem","previewFile"] // 必填，需要使用的JS接口列表
	            	});
	            	wx.ready(function(){
	        			wx.onMenuShareAppMessage({
	        				title: '${name}', // 分享标题
	        				desc: userName+'给你发送了一个文件!', // 分享描述
	        				link: linkdata.url, // 分享链接
	        				imgUrl: imgPathUrl,
	        				success: function () {
	        				},
	        				cancel: function (e) {
	        				}
	        			});
	        			wx.onMenuShareWechat({
	        		  			title: '${name}', // 分享标题
	        		  			desc:userName +'给你发送了一个文件!', // 分享描述
	        		  			link: linkdata.url, // 分享链接
	        		  			imgUrl: imgPathUrl,
	        		  			success: function () {
	        		  			},
	        		  			cancel: function () {
	        		  			}
	        		  	});
	        			
	        		});
	            	wx.error(function(res){
	            		alert("wx.config failed." + res);
	            	});
	            }
	        }
	    });
		
		
	}
	
	
	
    var linkCode = "";

    function cancelCreateLink() {
    	if($('#linkDiv>.putting-link').length == 0){
    		history.go(-1);
    	}else{
    		$("#linkFileListDiv").show();
			$("#addLinkFileDiv").hide();
    	}
    }
    function setValidity() {
        $("#validityDiv").css("display", "block");
    }

    function setLink() {
	$("#validityChoose").select("close")
        var accessCodeMode = $("input[name='accessCodeMode']:checked").val();
	console.log(accessCodeMode)
        var role;
        if($("#download").val() == "on") {
            role = "viewer";
        } else {
            role = "previewer";
        }
        var accessCode = $("#accessCode").val();
        if(accessCodeMode == "staticMode") {
            accessCodeMode = "static";
            accessCode = "";
        } else if(accessCodeMode == "randomMode") {
            accessCodeMode = "static";
            accessCode = $("#accessCode").text();
        } else {
            accessCodeMode = "mail";
        }
        var timeText = $("#validityChoose").val();
        var expireAt = "";
        if(timeText != "") {
            expireAt = getExpirTime();
        }
        var defaultlinKset = {
            accessCodeMode : accessCodeMode,
			plainAccessCode : accessCode,
            role : role,
        }
        if(timeText != "永久有效") {
            defaultlinKset.expireAt = expireAt.getTime();
           var effectiveAt =new Date().getTime();
			if(effectiveAt > defaultlinKset.expireAt){
				$.alert("有效日期不能晚于当前日期!");
				return false;
			}
        }
        var url = host + "/ufm/api/v2/links/" + ownerId + "/" + iNodeId;
        if(linkCode != null && linkCode != "") {
            url = "${ctx}/share/updateLink/" + ownerId + "/" + iNodeId + "?linkCode=" + linkCode;
        }
        
        $.ajax({
            type : "POST",
            url : url,
            data : JSON.stringify(defaultlinKset),
            error : function(request) {
					$.alert("单个文件链接不能超过三个");

            },
            success : function(data) {
            	$.toast("创建链接成功");
            	
            	//链接列表中添加新增链接
				var $list = $("#linkDiv");
				var $template = $("#linkTemplate");
				if(data != null){
					$("#linkFileListDiv").show();
					$("#addLinkFileDiv").hide();
				}
				data.effectiveAt=new Date().getTime();
				data.expireString=parseExpireString(data.effectiveAt,data.expireAt);
				data.roleName = getAclByRole(data.role)
				$template.template(data).appendTo($list);
				$("#"+data.id).data("data",data);
							fillLinkDiv(data);
				var listspan = $('#linkDiv .weui-flex').find('.puttinglink-title').children('span');
				$(listspan[listspan.length-1]).addClass("spanSelect")
				console.log(listspan)
				showMenuItems()
				$(".deleteDiv").click(function(event){
				     event.stopPropagation();
				})
            }
        });
    }

    function initData(data) {
        if(data.accessCodeMode == "static") {
            if(data.plainAccessCode != null && data.plainAccessCode != "") {
                $("#randomMode").click();
                $("#accessCode").val(data.accessCode);
            } else {
                $("#staticMode").click();
                $("#accessCode").val(getAccessCode(8));
            }
        } else if(data.accessCodeMode == "mail") {
            $("#emailMode").click();
        }
        if(data.download == true) {
            $("#downloadSwitch").click();
        }
        if(data.expireAt != null && data.expireAt != "") {
            $("#validityChoose").val(formatDateTime(data.expireAt));
        }
    }

    $("input[name=accessCodeMode]").click(function() {
        if($(this).attr('id') != "randomMode") {
            $("#accessCodeDiv").css("display", "none");
        } else {
            $("#accessCodeDiv").css("display", "block");
        }
    })

    function refreshAccessCode() {
        $("#accessCode").empty();
        $("#accessCode").text(getAccessCode(8));
    }

    $("#downloadSwitch").bind("click", function() {
        if($("#download").val() == "off") {
            $("#download").val("on");
        } else {
            $("#download").val("off");
        }
    });
    
    
     var chooseDate=$("#chooseDate").calendar({
	        onChange: function (p, values, displayValues) {
	        	$("#validityChoose").val(values);
	        }
	}); 
    $("#validityChoose").select({
        title : "选择有效期",
        items : ["一天", "一周", "一个月", "永久有效", "选择时间"],
        onChange : function(d) {
        },
        onClose : function(d) {
        	  if(d.data.values=="选择时间"){
        		  $("#chooseDate").click();
              }
        },
        onOpen : function() {
            console.log("open");
        },
    });
    
    function getExpirTime() {
        var timeText = $("#validityChoose").val();
        var date = new Date();
        var timestamp = new Date().getTime();
        if(timeText == "永久有效") {
            return "";
        }
        if(timeText == "一周") {
            var ms = 7 * (1000 * 60 * 60 * 24)
            var newDate = new Date(date.getTime() + ms);
            return newDate;
        } else if(timeText == "一天") {
            var ms = 1 * (1000 * 60 * 60 * 24)
            var newDate = new Date(date.getTime() + ms);
            return newDate;
        } else if(timeText == "一个月") {
            var ms = 30 * (1000 * 60 * 60 * 24)
            var newDate = new Date(date.getTime() + ms);
            return newDate;
        } else {
            var timeText = timeText;
            timestamp = new Date(timeText.replaceAll("-", "/"));
            return timestamp;
        }
    }

    function getAccessCode(length) {
        var rc = getRandomChar(true, false, false);
        rc = rc + getRandomChar(false, true, false);
        rc = rc + getRandomChar(false, false, true);
        
        for(var idx = 3; idx < length; idx++) {
            rc = rc + getRandomChar(true, true, true);
        }
        
        var arr_str = rc.split("");
        for(var i = 0; i < 50; i++) {
            var idx1 = getRandomNum(0, length);
            var idx2 = getRandomNum(0, length);
            
            if(idx1 == idx2) {
                continue;
            }
            
            var tempChar = arr_str[idx1];
            arr_str[idx1] = arr_str[idx2];
            arr_str[idx2] = tempChar;
        }
        
        return arr_str.join("");
    }

    function getAccessCode(length) {
        var rc = getRandomChar(true, false, false);
        rc = rc + getRandomChar(false, true, false);
        rc = rc + getRandomChar(false, false, true);
        
        for(var idx = 3; idx < length; idx++) {
            rc = rc + getRandomChar(true, true, true);
        }
        
        var arr_str = rc.split("");
        for(var i = 0; i < 50; i++) {
            var idx1 = getRandomNum(0, length);
            var idx2 = getRandomNum(0, length);
            
            if(idx1 == idx2) {
                continue;
            }
            
            var tempChar = arr_str[idx1];
            arr_str[idx1] = arr_str[idx2];
            arr_str[idx2] = tempChar;
        }
        
        return arr_str.join("");
    }
    function getRandomChar(number, chars, other) {
        var numberChars = "0123456789";
        var lowerAndUpperChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        var otherChars = "!@#$^*-+.";
        var charSet = "";
        if(number == true)
            charSet += numberChars;
        if(chars == true)
            charSet += lowerAndUpperChars;
        if(other == true)
            charSet += otherChars;
        return charSet.charAt(getRandomNum(0, charSet.length));
    }
    function getRandomNum(lbound, ubound) {
        return (Math.floor(Math.random() * (ubound - lbound)) + lbound);
    }

    function formatDateTime(inputTime) {
        var date = new Date(inputTime);
        var y = date.getFullYear();
        var m = date.getMonth() + 1;
        m = m < 10 ? ('0' + m) : m;
        var d = date.getDate();
        d = d < 10 ? ('0' + d) : d;
        var h = date.getHours();
        h = h < 10 ? ('0' + h) : h;
        var minute = date.getMinutes();
        var second = date.getSeconds();
        minute = minute < 10 ? ('0' + minute) : minute;
        second = second < 10 ? ('0' + second) : second;
        return y + '-' + m + '-' + d + ' ' + h + ':' + minute;
    };
    
    
    
    function promptMessageShare(){
    	 $('.setup-inbox-mask').show();
    	 $('#promptMessage').html('转发')
    }
    function promptMessageWeChat(){
    	$('.setup-inbox-mask').show();
    	$('#promptMessage').html('分享到微信')
    }
    $('.setup-inbox-mask').click(function(){
		$('.setup-inbox-mask').css('display','none');
	})
    
    var linkcopy = new Clipboard('#linkcopy', {
        text : function() {
            return copyLinks();
        }
    });
    linkcopy.on('success', function(e) {
        $.toast("复制成功");
    });
    linkcopy.on('error', function(e) {
        $.toast("复制失败！请手动复制","forbidden");
    });
    //复制提取码到剪贴板
    function copylink() {
        var clipboard = new Clipboard('#btn_copy', {
            text : function() {
                return $("#accessCode").text();
            }
        });
        clipboard.on('success', function(e) {
            $.toast("复制成功");
        });
        clipboard.on('error', function(e) {
            $.toast("复制失败！请手动复制","forbidden");
        });
    }
    //获取日期表达式
	function parseExpireString(effectiveAt,expireAt){
		var datetimeString="";
		if((expireAt-effectiveAt)+60000>=(1000 * 60 * 60 * 24)){
			datetimeString="链接" + Math.ceil((expireAt-effectiveAt)/(1000 * 60 * 60 * 24))+"天后失效";
		} else if ((expireAt-effectiveAt)<(1000 * 60 * 60 * 24)&&(expireAt-effectiveAt)>(1000 * 60 * 60)){
			datetimeString="链接" + Math.ceil((expireAt-effectiveAt)/(1000 * 60 * 60 ))+"小时后失效";
		} else if ((expireAt-effectiveAt)<(1000 * 60 * 60 )&&(expireAt-effectiveAt)>(1000 * 60 )){
			datetimeString="链接" + Math.ceil((expireAt-effectiveAt)/(1000 * 60 ))+"分钟后失效";
		} else {
			datetimeString="链接已失效";
		}
		return datetimeString;
	}
</script>
</body>
</html>
