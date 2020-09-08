// JavaScript Document
//onload function
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
	
	//工作区最小高度
	contentAdaptHeight();
	$(window).bind("resize",function(){ contentAdaptHeight() });
	
	//设置tip属性
	$("a").tooltip({ container:"body", placement:"top", delay: { show: 100, hide: 0 }, animation: false });

	//监听页面滚动时，fixed的元素标签的位置
	$(window).scroll(function(){
	var Left = document.documentElement.scrollLeft || document.body.scrollLeft;
	$(".header, .breadcrumb").css("left", -Left + "px"); 
	})
	$(window).bind("resize",function(){
	var Left = document.documentElement.scrollLeft || document.body.scrollLeft;
	$(".header, .breadcrumb").css("left", -Left + "px"); 
	});
	
})


function loadSysSetting(context,suffix){
	$.ajax({
        url:context + "/syscommon/loadconfig",
        success: function(msg) {
        	if(msg.copyright != ""){
        		$("#copyRightId").html(msg.copyright);
        	}
        	if(msg.existLogo == true){
        		$("#logoBackgroudId").css("background","url('" + context + "/syscommon/logo')");
        	}else{
        		$("#logoBackgroudId").css("background","url('" + context + "/static/skins/default/img/logo.png')");
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
function placeholderCompatible() {
	// 针对input文本框
	$('input[placeholder]').hide(0, function(){
		$(this).before('<input type="text" class="placeholder '+ $(this).attr("class") +'" value="'+ $(this).attr("placeholder") +'" />')
		.prev().focus(function(){
			$(this).hide().next().show().focus().val("");
		});
		$(this).blur(function(){
			if($(this).val() == '') $(this).hide().prev().show();
		})
		if($(this).val() != '') $(this).show().prev().hide();
	})

	// 针对textarea文本框
	$('textarea[placeholder]').hide(0, function(){
		$(this).before('<textarea class="placeholder '+ $(this).attr("class") +'">'+ $(this).attr("placeholder") +'</textarea>')
		.prev().focus(function(){
			$(this).hide().next().show().focus().text("");
		});
		$(this).blur(function(){
			if($(this).text() == '') $(this).hide().prev().show();
		})
		if($(this).text() != '') $(this).show().prev().hide();
	})
}

/**
 *
 *工作区高度全屏自适应的方法
 */
function contentAdaptHeight(){
	var workHeight = parseInt($(window).height() - 60 - $(".footer").height());
	$(".body").css("min-height",workHeight);
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
	$(".nav-menu").find("#" +navId).parent().addClass("current");
}


/**
 * 左侧菜单点击效果
 */
function openInframe(_this,url,targetId){
	$(_this).parents("ul#downMenu").find(".active").removeClass("active");
	$(_this).parent().addClass("active").parent().parent().addClass("active");
	$("#"+targetId).attr("src",url);
	$("#breadcrumbText").html($(_this).parent().parent().prev().text()+" > "+$(_this).text());
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
	$(obj).find('div').height($(window).height());
	$(obj).find('p').css("left",($(window).width()-$(obj).find('p').outerWidth())/2);
	$(window).bind("resize",function(){
		$(obj).find('div').height($(window).height());
	});
}
function unLayerLoading(){
	$('#loadingLayer').remove();
}

/**
 * 生成成功、错误提示信息
 */
function handlePrompt(type,info,l,t,timer,containerId) {
	var html = '<div class="handlePrompt handlePrompt-'+ type +'" id="handlePrompt"><p>'+ info +'</p><button type="button" class="close">&times;</button></div>'; //type有两种，分别为：success,error
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
	var outTimer = (timer==0)?0:(timer==''||timer==null)?3000:timer;
	var left = (l==''||l==null)?(($(window).width()-$("#"+ _id).width())/2 +'px'):(l+'px');
	var top = (t==''||t==null)?('65px'):(t+'px');
	$("#"+_id).css({"left":left,"top":top});
	$("#"+_id).fadeIn();
	
	if(outTimer != 0){
		TO = setTimeout(function(){
			$("#"+_id).fadeOut(function(){
				$(this).remove();
			});
			clearTimeout(TO);
		},outTimer);
	} //outTimer 为0时不会自动关闭
	
	$("#"+_id).find("button").click(function(){
		clearTimeout(TO);
		$("#"+_id).fadeOut(function(){
			$(this).remove();
		});
	})
	
	$("#"+_id).hover(function(){
		clearTimeout(TO);
	},function(){
		if(outTimer != 0){
			TO = setTimeout(function(){
				$("#"+_id).fadeOut(function(){
					$(this).remove();
				});
				clearTimeout(TO);
			},outTimer);
		} //outTimer 为0时不会自动关闭
	})
}

/**  
*转换日期对象为日期字符串  
* @param date 日期对象  
*
* @return 符合要求的日期字符串  
*/  
function getSmpFormatDate(date) {
  var pattern = "yyyy-MM-dd hh:mm:ss";
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

function refreshWindow() {
	window.location.reload();
}