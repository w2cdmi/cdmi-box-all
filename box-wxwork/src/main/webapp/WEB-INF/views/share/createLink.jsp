<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<%@ include file="../common/include.jsp"%>
<link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/share/linkIndex.css" />
<script src="${ctx}/static/jquery-weui/js/clipboard.min.js"></script>
<title>添加外发链接</title>
</head>
<body>


	<!--外发的高级设置-->

	<div class="share-senior">

		<div class="share-senior-content">
			<div class="weui-cells weui-cells_radio">
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
				<li style="margin: 0px 0.9rem; display: none" id="accessCodeDiv">
					<span>提取码：</span> <i id="accessCode" style="margin-left: 0px"></i>
					<p style="margin-left: 40px;" id="btn_copy"
						data-clipboard-target="#accessCode" onclick="copylink()">复制</p>
					<h3>
						<img src="${ctx}/static/skins/default/img/putting-renovata.png"
							onclick="refreshAccessCode()" />
					</h3>
				</li>
			</div>
			<ul>


				<li>
					<div class="weui-cell__bd">允许下载</div> <input hidden="hidden"
					id="download" type="radio" value="off" checked="checked" /> <input
					class="weui-switch" type="checkbox" id="downloadSwitch" disabled="disabled"/>
				</li>
				<li style="vertical-align: middle;"><span>有效期</span> <input
					class="weui-input" id="validityChoose" type="text" value="一周"
					style="height: 2.2rem; width: 80%; text-align: right;">
					<h2>
						<img src="${ctx}/static/skins/default/img/putting-more.png" />
					</h2></li>
			</ul>
		</div>

	</div>
	<div class="button_sp_area share-senior-input" style="margin-left: 1%;">
		<a href="javascript:;" onclick="setLink()">确定</a> <a
			href="javascript:;" onclick="cancel()">取消</a>
	</div>

</body>
<script type="text/javascript">
    var ownerId = "<c:out value='${ownerId}'/>";
    var iNodeId = "<c:out value='${folderId}'/>";
    var token = "<c:out value='${token}'/>";
    var linkCode = "";
    
    $(function() {
        if(linkCode != null && linkCode != "") {
            getLink();
        } else {
            $("#accessCode").text(getAccessCode(8));
            $("#downloadSwitch").click();
        }
        
    })

    function cancel() {
        gotoPage(ctx + "/share/link/" + ownerId + "/" + iNodeId);
    }
    function setValidity() {
        $("#validityDiv").css("display", "block");
    }

    function getLink() {
        $("#linkDiv").empty();
        $.ajax({
            type : "GET",
            data : {},
            url : "${ctx}/share/getlink/" + ownerId + "/" + iNodeId + "?" + new Date().getTime(),
            error : function(request) {
                doInitLinkError(request);
            },
            success : function(data) {
                for(var i = 0; i < data.length; i++) {
                    if(data[i].id == linkCode) {
                        initData(data[i]);
                    }
                }
            }
        });
    }

    function setLink() {
        var accessCodeMode = $("input[name='accessCodeMode']:checked").val();
        console.log(accessCodeMode)
        var download;
        if($("#download").val() == "on") {
            download = true;
        } else {
            download = false;
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
            accessCode : accessCode,
            download : download,
            preview : true,
            upload : false,
            identities : "",
            token : token,
        }
        if(timeText != "永久有效") {
            defaultlinKset.expireAt = formatDateTime(expireAt);
            defaultlinKset.effectiveAt = formatDateTime(new Date().getTime());
            var sDate = new Date(defaultlinKset.effectiveAt.replace(/\-/g, "\/"));
            var eDate = new Date(defaultlinKset.expireAt.replace(/\-/g, "\/"));
        }
        var url = "${ctx}/share/setlink/" + ownerId + "/" + iNodeId;
        if(linkCode != null && linkCode != "") {
            url = "${ctx}/share/updateLink/" + ownerId + "/" + iNodeId + "?linkCode=" + linkCode;
        }
        
        $.ajax({
            type : "POST",
            url : url,
            data : defaultlinKset,
            error : function(request) {
            if(sDate > eDate){
                $.alert("结束日期不得小于开始日期！");
                return false;
                }
            },
            success : function(data) {
                gotoPage(ctx + "/share/link/" + ownerId + "/" + iNodeId);
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
    
    $("#validityChoose").select({
        title : "选择有效期",
        items : ["一天", "一周", "一个月", "永久有效", "选择时间"],
        onChange : function(d) {
           if(d.values=="选择时间"){
        	   $("#validityChoose").calendar({
        	        onChange: function (p, values, displayValues) {
        	        
        	          }
        	        });
           }
        },
        onClose : function() {
        },
        onOpen : function() {
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
            var timeText = timeText + ":00";
            timestamp = new Date(timeText.replaceAll("-", "/")).getTime();
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
</script>
</html>