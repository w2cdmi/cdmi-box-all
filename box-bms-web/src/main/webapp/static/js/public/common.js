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

})

/**
*
*判断浏览器是否支持 placeholder
*/
function placeholderSupport() {
   return 'placeholder' in document.createElement('input');
}
function placeholderCompatible() {
	// 针对input文本框
	$('input[placeholder]').each(function(){
		if($(this).val()==""){
			$(this).hide(0, function(){
				$(this).before('<input type="text" class="placeholder '+ $(this).attr("class") +'" value="'+ $(this).attr("placeholder") +'" />')
				.prev().focus(function(){
					$(this).hide().next().show().focus().val("");
				});
				$(this).blur(function(){
					if($(this).val() == '') $(this).hide().prev().show();
				})
			})
		}else{
			$(this).show(0, function(){
				$(this).before('<input style="display:none;" type="text" class="placeholder '+ $(this).attr("class") +'" value="'+ $(this).attr("placeholder") +'" />')
				.prev().focus(function(){
					$(this).hide().next().show().focus().val("");
				});
				$(this).blur(function(){
					if($(this).val() == '') $(this).hide().prev().show();
				})
			})
		}
	})
	
	// 针对textarea文本框
	$('textarea[placeholder]').each(function(){
		if($(this).val()==""){
			$(this).hide(0, function(){
				$(this).before('<textarea class="placeholder '+ $(this).attr("class") +'">'+ $(this).attr("placeholder") +'</textarea>')
				.prev().focus(function(){
					$(this).hide().next().show().focus().text("");
				});
				$(this).blur(function(){
					if($(this).val() == '') $(this).hide().prev().show();
				})
			})
		}else{
			$(this).show(0, function(){
				$(this).before('<textarea style="display:none;" class="placeholder '+ $(this).attr("class") +'">'+ $(this).attr("placeholder") +'</textarea>')
				.prev().focus(function(){
					$(this).hide().next().show().focus().text("");
				});
				$(this).blur(function(){
					if($(this).val() == '') $(this).hide().prev().show();
				})
			})
		}
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
	var minH = parseInt($(window).height() - 140);
	workHeight = (workHeight < minH) ? minH : workHeight;
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
		
		return Math.floor(size*100)/100+ sizeUnit[u];
		//return size.toFixed(2)+ sizeUnit[u];
	} else {
		return this.formatFileSize(size / 1024, ++u);
	}
}

function getLocalTime(serverTime){
	var d = new Date(serverTime);
	return getSmpFormatDate(d);	
}

function refreshWindow() {
	window.location.reload();
}