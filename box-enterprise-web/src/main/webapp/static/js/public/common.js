/* JavaScript Document
 spin loading default setting */
var optsBigSpinner = {
		lines: 15,            // The number of lines to draw
		length: 8,            // The length of each line
		width: 4,             // The line thickness
		radius: 12,           // The radius of the inner circle
		color: '#333',        // #rgb or #rrggbb
		speed: 1.6,             // Rounds per second
		trail: 50           // Afterglow percentage
	}
var optsMiddleSpinner = {
		lines: 12,            
		length: 4,            
		width: 3,             
		radius: 7,           
		color: '#333',       
		speed: 1.6,         
		trail: 50           
	};
var optsSmallSpinner = {
		lines: 12,            
		length: 3,            
		width: 2,             
		radius: 5,           
		color: '#333',        
		speed: 1.6,         
		trail: 50          
	};
var bannerHight = 66;

$(document).ready(function(){
	//菜单效果
	$(".nav-menu").find("a")
	.hover(function(){
		$(this).parent().not(".current").addClass("over");
	},function(){
		$(this).parent().removeClass("over");
	})
	
	//文件排序的交互效果
	var taxisShowTimer;
	$("#taxisDropDown").hover(function(){
		clearTimeout(taxisShowTimer);
		$(this).addClass("open");
	},function(){
		taxisShowTimer = setTimeout(function(){
			$("#taxisDropDown").removeClass("open");
		},300);
	})
	
	//监听页面滚动时，fixed的元素标签的位置
	$(window).scroll(function(){
		var myLayout = getCookie("pageLayoutType");
		var winW = $(window).width();
		var Left = document.documentElement.scrollLeft || document.body.scrollLeft;
		if(myLayout == "menuR"){
			$(".breadcrumb, .public-bar-con").css("left", -Left + "px");
			if(winW < 980){
				$(".header").css("right", (winW - 980 + Left) + "px"); 
			}
		}else{
			$(".header, .breadcrumb, .public-bar-con, .tab-menu").css("left", -Left + "px"); 
		}
	})
	$(window).bind("resize",function(){ 
		var myLayout = getCookie("pageLayoutType");
		var winW = $(window).width();
		var Left = document.documentElement.scrollLeft || document.body.scrollLeft;
		if(myLayout == "menuR"){
			$(".breadcrumb, .public-bar-con").css("left", -Left + "px");
			if(winW < 980){
				$(".header").css("right", (winW - 980 + Left) + "px"); 
			}
		}else{
			$(".header, .breadcrumb, .public-bar-con, .tab-menu").css("left", -Left + "px"); 
		}
	});
	
	//工作区最小高度
	contentAdaptHeight();
	$(window).bind("resize",function(){ contentAdaptHeight() });
	
	//根据不同布局设置导航栏、面包屑的dropdown和tooltip的位置、方向
	layoutParaSetting();
	
	//兼容placeholder
	if(!placeholderSupport()){
		// 针对input文本框
		$('input[placeholder]').hide(0, function(){
			$(this).before('<input type="text" class="placeholder '+ $(this).attr("class") +'" value="'+ $(this).attr("placeholder") +'" />')
			.prev().focus(function(){
				$(this).hide().next().show().focus().val("");
			});
			$(this).blur(function(){
				if($(this).val() == '') $(this).hide().prev().show();
			})
		})
		
		// 针对textarea文本框
		$('textarea[placeholder]').hide(0, function(){
			$(this).before('<textarea class="placeholder '+ $(this).attr("class") +'">'+ $(this).attr("placeholder") +'</textarea>')
			.prev().focus(function(){
				$(this).hide().next().show().focus().text("");
			});
			$(this).blur(function(){
				if($(this).val() == '') $(this).hide().prev().show();
			})
		})
	};
})

function loadSysSetting(context,encode){
	$.ajax({
		cache: false,
		type: "POST",
        url:context + "/syscommon/loadconfig",
        success: function(msg) {
        	if(msg.title != ""){
        		if(encode == "en"){
        			document.title = msg.titleEn;
        		}else{
        			document.title = msg.title;
        		}
        	}
        	if(msg.copyright != ""){
        		if(encode == "en"){
        			$("#copyRightId").html(msg.copyrightEn);
        		}else{
        			$("#copyRightId").html(msg.copyright);
        		}
        	}
        	if(msg.existLogo == true)
        	{
        		$("#logoBlock img").attr("src", context + "/syscommon/multlogo");
        	}else{
        		$("#logoBlock img").attr("src", context + "/static/skins/default/img/logo.png");
        	}
        }
    });
}

function loadUserImageSetting(context,encode){
	$.ajax({
		cache: false,
		type: "POST",
        url:context + "/userimage/loadconfig",
        success: function(msg) {        	
        	if(msg.exitUserImage == true)
        	{
        		$("#userLogo img").attr("src", context + "/userimage/getLogo");
        	}else{
        		$("#userLogo img").attr("src", context + "/static/skins/default/img/user-logo.png");
        	}
        }
    });
}

/**
*
*判断浏览器是否支持 placeholder
*/
function placeholderSupport() {
   return 'placeholder' in document.createElement('input');
}


/**
 *
 *工作区高度全屏自适应的方法
 */
function contentAdaptHeight(){
	var workHeight = parseInt($(window).height() - bannerHight - $(".footer").height());
	$(".body").css("min-height",workHeight);
    $(".leftside").css("height", $(window).height() - bannerHight - $(".footer").height());
	
	//始终保证分页在页面底部
	$(".body #fileList").css("min-height", parseInt($(window).height() - 245));
}

/**
 *
 *界面iframe的高度自适应
 */
function iframeAdaptHeight(workHeight){
	$("#systemFrame").height(workHeight);
}

/**
 * 导航菜单当前选中项
 */
function navMenuSelected(navId){
    $(".nav-menu").find("li").removeClass("current");
	$(".nav-menu").find("#" + navId).closest("div.doctypetree_item").parent("li").addClass("current");
}

function downloadVersionFile(data) {
	jQuery('<form action="'+ data +'" method="get"></form>').appendTo('body').submit().remove();
}
/**
 * 左侧菜单点击效果
 */
function openInframe(_this,url,targetId){
	$(_this).parents("ul").find(".active").removeClass("active");
	$(_this).parent().addClass("active");
	$("#"+targetId).attr("src",url);
}

/**
 * ymPrompt 扩展方法
 */
function ymPrompt_addModalFocus(that){
	$(that).addClass("btn-primary");
}
function ymPrompt_disableModalbtn(that){
	$(that).attr("disabled","disabled");
}
function ymPrompt_enableModalbtn(that){
	$(that).removeAttr("disabled");
}
function ymPrompt_changeModalbtnText(that,text){
	$(that).text(text);
}

/**
 * loading 锁屏方法
 * loading-circle	---- 普通效果
 * loading-bar 		---- 进度监测的效果
 */
function inLayerLoading(info, type){
	type = (type==''||type==null)?'loading-circle':'loading-bar';
	info = (info==''||info==null)?'Loading…':info;
	$('body').append('<div id="loadingLayer" class="'+ type +'"><p>'+ info +'<span></span></p><div></div></div>');
	var obj = $("#loadingLayer");
	if(type == "loading-circle"){
		/* spin loading*/
		spinner = new Spinner(optsMiddleSpinner).spin(obj.find("span").get(0));
	}
	$(obj).find('> div').height($(window).height());
	$(obj).find('> p').css("left",($(window).width()-$(obj).find('> p').outerWidth())/2);
	$(window).bind("resize",function(){
		$(obj).find('> div').height($(window).height());
		$(obj).find('> p').css("left",($(window).width()-$(obj).find('> p').outerWidth())/2);
	});
}
function unLayerLoading(){
	$('#loadingLayer').remove();
}

/**
 * 生成成功、错误提示信息
 */
function handlePrompt(type,info,l,t,timer,containerId) {
	var html = '<div class="handlePrompt handlePrompt-'+ type +'" id="handlePrompt"><p>'+ info +'</p><button type="button" class="btn btn-link"><i class="icon-delete-alt icon-gray"></i></button></div>'; //type有两种，分别为：success,error
	var _id = "handlePrompt";
	if($("#"+_id).get(0)){
		$("#"+_id).remove();
		clearTimeout(TO);
	}
	var container = (containerId==''||containerId==null)?('body'):('#'+containerId);
	$(container).append(html);
	
	if(containerId !=''&& containerId !=null){
		$("#"+_id).css("position","absolute");
	}

	var TO;
	var defaultTop = "10px";
	var headObject = $(".header");
		if(headObject.get(0)){
			defaultTop = parseInt(parseInt(headObject.css("top")) + headObject.height() + 10) + "px";
		}
	var outTimer = (timer==0)?0:(timer==''||timer==null)?3000:timer;
	var left = (l==''||l==null)?(($(window).width()-$("#"+ _id).width())/2 +'px'):(l+'px');
	var top = (t==''||t==null)?(defaultTop):(t+'px');
	$("#"+_id).css({"left":left,"top":parseInt(top) - 15});
	$("#"+_id).animate({top: parseInt(top),opacity:'show'}, 300 ); 
	
	if(outTimer != 0){
		TO = setTimeout(function(){
			$("#"+_id).animate({top: parseInt(top)-15,opacity:'hide'}, 300 ,function(){
				$(this).remove();
			});
			clearTimeout(TO);
		},outTimer);
	} //outTimer 为0时不会自动关闭
	
	$("#"+_id).find("button").click(function(){
		clearTimeout(TO);
		$("#"+_id).animate({top: parseInt(top)-15,opacity:'hide'}, 300 ,function(){
			$(this).remove();
		});
	})
	
	$("#"+_id).hover(function(){
		clearTimeout(TO);
	},function(){
		if(outTimer != 0){
			TO = setTimeout(function(){
				$("#"+_id).animate({top: parseInt(top)-15,opacity:'hide'}, 300 ,function(){
					$(this).remove();
				});
				clearTimeout(TO);
			},outTimer);
		} //outTimer 为0时不会自动关闭
	})
}


function setRootCookie(name,value) {
	delCookie(name);
    var exp  = new Date("December 31, 9998");
    document.cookie = name + "="+ escape (value) + ";path=/;expires=" + exp.toGMTString();
}

function getRootCookie(name){
    var arr = document.cookie.match(new RegExp("(^| )"+name+"=([^;]*)(;|$)"));
    if(arr != null) {
    	return unescape(arr[2]);
    }
    return null;
} 

function delRootCookie(name){
    var exp = new Date();
    exp.setTime(exp.getTime() - 1000);
    document.cookie= name + "="+name+";path=/;expires="+exp.toGMTString();
}

function setCookie(name,value) {
    var exp  = new Date("December 31, 9998");
    document.cookie = name + "="+ escape (value) + ";expires=" + exp.toGMTString();
}

function getCookie(name){
    var arr = document.cookie.match(new RegExp("(^| )"+name+"=([^;]*)(;|$)"));
    if(arr != null) {
    	return unescape(arr[2]);
    }
    return null;
} 

function delCookie(name){
    var exp = new Date();
    exp.setTime(exp.getTime() - 1);
    var cval=getCookie(name);
    if(cval!=null) document.cookie= name + "="+cval+";expires="+exp.toGMTString();
}

/**  
*转换日期对象为日期字符串  
* @param date 日期对象  
*
* @return 符合要求的日期字符串  
*/  
function getSmpFormatDate(date) {
  var pattern = "yyyy-MM-dd hh:mm:ss";
  
  if(top.globalLang == "en"){
	  pattern = "MM/dd/yyyy hh:mm:ss";
  }
  return getFormatDate(date, pattern);
}

/**  
*转换日期对象为日期字符串  
* @param l long值  
* @param pattern 格式字符串,例如：yyyy-MM-dd hh:mm:ss  
* @return 符合要求的日期字符串  
*/  
function getFormatDate(date, pattern) {
  if (date == undefined) {
      date = new Date();
  }
  if (pattern == undefined) {
      pattern = "yyyy-MM-dd hh:mm:ss";
  }
  return date.format(pattern);
}



//扩展Date的format方法 
Date.prototype.format = function (format) {
  var o = {
      "M+": this.getMonth() + 1,
      "d+": this.getDate(),
      "h+": this.getHours(),
      "m+": this.getMinutes(),
      "s+": this.getSeconds(),
      "q+": Math.floor((this.getMonth() + 3) / 3),
      "S": this.getMilliseconds()
  }
  if (/(y+)/.test(format)) {
      format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
  }
  for (var k in o) {
      if (new RegExp("(" + k + ")").test(format)) {
          format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
      }
  }
  return format;
}

function getLocalTime(serverTime){
	var d = new Date(serverTime);
	return getSmpFormatDate(d);	
}
	
//文件类型
var FILE_TYPE_MICROSOFT_WORD = "|doc|dot|docx|dotx|docm|dotm|";
var FILE_TYPE_MICROSOFT_POWERPOINT = "|ppt|pot|pps|pptx|potx|ppsx|pptm|potm|ppam|";
var FILE_TYPE_MICROSOFT_EXCEL = "|xls|xlt|xlsx|xltx|xlsm|xltm|xlsb|xlam|";
var FILE_TYPE_PICTURE = "|jpg|jpeg|gif|bmp|png|";
var FILE_TYPE_AUDIO = "|mp3|wav|wma|wm|midi|mid|";
var FILE_TYPE_VIDEO = "|avi|mpg|rm|wmv|mpeg|mp4|";
var FILE_TYPE_PDF = "|pdf|";
var FILE_TYPE_TXT = "|txt|";
var FILE_TYPE_COMPRESS = "|rar|zip|";

/**
 * 获取标准类型
 * @param type 文件后缀
 * @return
 */
var _getStandardType = function(name) {
	var type, index = name.lastIndexOf(".");
	if(index != -1){
		type = name.substring(index + 1).toLowerCase();
	}
	try {
		var tmpType = "|" + type + "|";
		if (FILE_TYPE_MICROSOFT_WORD.indexOf(tmpType) != -1) {
			return "doc";
		}
		if (FILE_TYPE_MICROSOFT_POWERPOINT.indexOf(tmpType) != -1) {
			return "ppt";
		}
		if (FILE_TYPE_MICROSOFT_EXCEL.indexOf(tmpType) != -1) {
			return "xls";
		}
		if (FILE_TYPE_PICTURE.indexOf(tmpType) != -1) {
			return "img";
		}
		if (FILE_TYPE_AUDIO.indexOf(tmpType) != -1) {
			return "music";
		}
		if (FILE_TYPE_VIDEO.indexOf(tmpType) != -1) {
			return "video";
		}
		if (FILE_TYPE_PDF.indexOf(tmpType) != -1) {
			return "pdf";
		}
		if (FILE_TYPE_TXT.indexOf(tmpType) != -1) {
			return "txt";
		}
		if (FILE_TYPE_COMPRESS.indexOf(tmpType) != -1) {
			return type;
		}
		return "default";
	} catch (e) {
		return "default";
	}
};

/**
 * 阻止滚动事件冒泡
 * @param id 滚动容器id
 * @return
 */
function stopDefaultScroll(id){
	var _this = document.getElementById(id);
	if(navigator.userAgent.indexOf("Firefox")>0){
		_this.addEventListener('DOMMouseScroll',function(e){
			_this.scrollTop += e.detail > 0 ? 60 : -60;   
			e.preventDefault();
		},false); 
	}else{
		_this.onmousewheel = function(e){   
			e = e || window.event;   
			_this.scrollTop += e.wheelDelta > 0 ? -60 : 60;   
			return false;
		};
	}
	return this;
}

function formatFileSize(size, u) {
	try{
		size = Number(size);
	}catch(e){
		return '-';
	}
	u = u ? u : 0; 
	var sizeUnit = ['B', 'KB', 'MB', 'GB', 'TB'];
	if (u < 0 || u >= sizeUnit.length) {
		return '0B';
	}
	if (size < 1) {
		return this.formatFileSize(size * 1024, --u);
	} else if (size < 1024) { 
		if(sizeUnit[u] == 'B'){
			return size + 'B';
		}
		return Math.floor(size*100)/100+sizeUnit[u];
		//return size.toFixed(2)+ sizeUnit[u];
	} else {
		return this.formatFileSize(size / 1024, ++u);
	}
}

/**
 * 卸载页面加载动画
 * 
 */
function comboxRemoveLoading(id){
	var objLoading = document.getElementById(id); 
    if(objLoading != null){
        $(objLoading).fadeOut(300, function(){
        	$(objLoading).remove();
        })
    }
}

/**
 * 去掉字符串前后空格（含全角空格）
 * 
 */
String.prototype.trim= function()      
{      
    var t = this.replace(/(^\s*)|(\s*$)/g, "");    
    return t.replace(/(^　*)|(　*$)/g, "");    

} 

/**
 * 页面布局切换
 * 
 */
function changeLayout(layout){
	setRootCookie("pageLayoutType",layout);
	location.reload();
}

function layoutParaSetting(){
	var myLayout = getCookie("pageLayoutType");
	if(myLayout == "menuT" || myLayout == "" || myLayout == null){
		$(".header a").not(".header .ztree a").tooltip({ container:"body", placement:"bottom", delay: { show: 100, hide: 0 }, animation: false });
		$(".breadcrumb .btn, .public-bar .btn, .btn-trash a").tooltip({ container:"body", placement:"top", delay: { show: 100, hide: 0 }, animation: false });
	}else if(myLayout == "menuB"){
		$(".header a").tooltip({ container:"body", placement:"top", delay: { show: 100, hide: 0 }, animation: false });
		$(".breadcrumb .btn").tooltip({ container:"body", placement:"bottom", delay: { show: 100, hide: 0 }, animation: false });
		$(".breadcrumb #uploadBtnBox").tooltip({ container:"body", placement:"left", delay: { show: 100, hide: 0 }, animation: false });
		$(".user-handle .dropdown").addClass("dropup");
	}else if(myLayout == "menuL"){
		$(".header .liftside > .nav-menu a").tooltip({ container:"body", placement:"right", delay: { show: 100, hide: 0 }, animation: false });
		$(".header .header-R a").tooltip({ container:"body", placement:"top", delay: { show: 100, hide: 0 }, animation: false });
		$(".breadcrumb .btn").tooltip({ container:"body", placement:"bottom", delay: { show: 100, hide: 0 }, animation: false });
		$(".breadcrumb #uploadBtnBox").tooltip({ container:"body", placement:"left", delay: { show: 100, hide: 0 }, animation: false });
		$(".user-handle .dropdown").addClass("dropup");
		$(".user-handle .dropdown-menu,.user-handle .dropdown-submenu").removeClass("pull-left pull-right");
	}else if(myLayout == "menuR"){
		$(".header .liftside > .nav-menu a").tooltip({ container:"body", placement:"left", delay: { show: 100, hide: 0 }, animation: false });
		$(".header .header-R a").tooltip({ container:"body", placement:"top", delay: { show: 100, hide: 0 }, animation: false });
		$(".breadcrumb .btn").tooltip({ container:"body", placement:"bottom", delay: { show: 100, hide: 0 }, animation: false });
		$(".breadcrumb #uploadBtnBox").tooltip({ container:"body", placement:"left", delay: { show: 100, hide: 0 }, animation: false });
		$(".user-handle .dropdown").addClass("dropup");
	}
}

/**
 * textarea maxlength 判断
 */
function doMaxLength(that){
	if(!$.support.leadingWhitespace || navigator.userAgent.indexOf("MSIE 9.0")>0){ //在ie8 和 ie9中执行
		var maxVal = $(that).attr("maxlength");
		if(that.value.length > maxVal){
			that.value = that.value.slice(0, maxVal);
		}
	}
}

/**
 * 数字字符串补0
 */ 
function pad(num, n) {  
    var len = num.toString().length;  
    while(len < n) {  
        num = "0" + num;  
        len++;  
    }  
    return num;  
}  

/**
 * 将字符串数字拼接成trunk数据传个contror层:即Length:Value格式,Length的长度固定是4
 * 举例存在 tony, sonina两个字符串，转换成trunk数据为: 0004tony0006sonina
 */
function getTrunckData(dataArray){
	if(dataArray == null || dataArray == ""){
		return "";
	}
	var result = "";
	for ( var i = 0; i < dataArray.length; i++) {
		if(dataArray[i] != ""){
			result = result + pad(dataArray[i].length,4) + dataArray[i];
		}
	}
	return result;
}
//是否为火狐浏览器
function isFirFoxBrowser(){
	var currentBrowser = navigator.userAgent.toLocaleLowerCase();
	if (currentBrowser.indexOf("firefox/") > -1)
	{
		return true;
	}
	return false;
}
//是否为chrome浏览器
function isChromeBrowser(){
	var currentBrowser = navigator.userAgent.toLocaleLowerCase();
	if (currentBrowser.indexOf("chrome/") > -1)
	{
		return true;
	}
	return false;
}
function isIEBrowser(){
	//获取当前浏览器信息
	var currentBrowser = navigator.userAgent.toLocaleLowerCase();
	if (currentBrowser.indexOf("compatible") > -1 && currentBrowser.indexOf("msie") >-1)
	{
		return true;
	}
	else if (currentBrowser.indexOf("trident") > -1 && currentBrowser.indexOf("rv:") > -1)
	{
		return true;
	}
	else if (currentBrowser.indexOf("firefox/") > -1)
	{
		return false;
	}

	else if (currentBrowser.indexOf("chrome/") > -1)
	{
		return false;
	}

	return false;
}
