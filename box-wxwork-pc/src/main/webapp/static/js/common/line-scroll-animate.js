/**
 * 增加左右滑动效果
 */
+ function($) {
    "use strict";
    var getMarginLeft = function($e) {
        var left = $e.css("marginLeft");
        var index = left.indexOf("px");
        if(index != -1) {
            left = left.substring(0, index);
        }

        return parseInt(left);
    };

    var calcMarginLeft = function(left, delta, leftmost, rightmost) {
        left = parseInt(left) + parseInt(delta);
        if(left <= leftmost) {
            return leftmost;
        }

        rightmost = rightmost || 0;
        if(left > rightmost) {
            return rightmost;
        }

        return left;
    };

    /*
     为元素内line-scroll-wrapper增加滑动功能，显示出右侧的button。
     <div class="line-scroll-wrapper">
         <div class="line-content">
         </div>
         <div class"line-buttons">
         </div>
     </div>
     */
    // 用于记录被按下的对象
    var __info = {
        // 当前左滑的对象
        $pressed : undefined,

        // 上一个左滑的对象
        $last : undefined,

        // 用于记录按下的点
        start : undefined,

        //是否启动滑动效果
        active: false,

        //滑动到最左侧时的margin-left
        leftmost : 0
    };

    $.fn.addLineScrollAnimate = function () {
        //将所有的.line-content设置为屏幕等宽
        var width = window.screen.width;
        $(".line-content").each(function() {
            $(this).width(width);
        });

        //将wrapper宽度设置为子元素宽度总和
        var $wrapper = $(".line-scroll-wrapper");
        $wrapper.each(function() {
            var width = 0;
            var $this = $(this);
            $this.children().each(function() {
                width += $(this).width();
            });

            //jquery的width()返回四舍五入后的值，+1为了防止父元素宽度不够(子元素个数太多后，+1可能不够)。
            $this.width(width + 1);
        });
			
       
        
        $wrapper.off('touchstart').on('touchstart', function (e) {
        	 $('.line-buttons').off('touchstart').on('touchstart',function(e){   //阻止冒泡事件
            	e=window.event||e;
	    		if(document.all){  
	      		  	e.cancelBubble=true;
	    		}else{
	        		e.stopPropagation();
	    		}
        	});
            __info.$pressed = $(this); // 记录被按下的对象
            __info.leftmost = (__info.$pressed.find(".line-buttons").width() || 0) * -1;
            __info.$last && __info.$last[0] != __info.$pressed[0] && __info.$last.animate({marginLeft: "0"}, 300); // 已经左滑状态的按钮右滑

            // 记录开始按下时的点
            var touches = event.touches[0];
            __info.start = {
                x: touches.pageX, // 横坐标
                y: touches.pageY, // 纵坐标
                lx: touches.pageX  //最近一次的滑动位置
            };
            __info.active = false;
        }).off('touchmove').on('touchmove', function (e) {
        	$('.line-buttons').off('touchmove').on('touchmove',function(e){   //阻止冒泡事件
            	e=window.event||e;
	    		if(document.all){  
	      		  	e.cancelBubble=true;
	    		}else{
	        		e.stopPropagation();
	    		}
        	});
            if(__info.active) {
                event.stopPropagation();
            }

            // 计算划动过程中x和y的变化量
            var touches = event.touches[0];
            var delta = {
                x: touches.pageX - __info.start.x,
                y: touches.pageY - __info.start.y
            };

            if(!__info.active) {
                // 纵向位移大于横向位移，不处理
                if (Math.abs(delta.x) < Math.abs(delta.y) || Math.abs(delta.y) > 20) {
                    return;
                }
            }

            __info.active = true;

            //处于滑动过程中，阻止事件冒泡（主要用于阻止外围DIV的下拉和上滑）
            //event.stopPropagation();

            var left = getMarginLeft(__info.$pressed);
            var to = calcMarginLeft(left, touches.pageX - __info.start.lx, __info.leftmost);
            //
            if(to != left) {
                __info.$pressed.css({marginLeft: to + "px"});
            }

            __info.start.lx = touches.pageX;
        }).off('touchend').on('touchend', function (e) {
        	$('.line-buttons').off('touchend').on('touchend',function(e){   //阻止冒泡事件
            	e=window.event||e;
	    		if(document.all){  
	      		  	e.cancelBubble=true;
	    		}else{
	        		e.stopPropagation();
	    		}
        	});
            __info.$last = __info.$pressed; // 记录上一个左滑的对象

            if(!__info.active) {
                return
            }

            var left = getMarginLeft(__info.$pressed);
            var diffX = event.changedTouches[0].pageX - __info.start.x;
            if (diffX < -200 ) {
                __info.$pressed.animate({marginLeft: __info.leftmost + "px"}, 100); // 左滑
            } else if (diffX < -100) {
                __info.$pressed.animate({marginLeft:  __info.leftmost + "px"}, 200); // 左滑
            } else if (diffX < -50) {
                __info.$pressed.animate({marginLeft:  __info.leftmost + "px"}, 300); // 左滑
            } else if (diffX < 0) {
                //左滑距离太小，向右恢复
                __info.$pressed.animate({marginLeft:  "0"}, 100); // 左滑
            } else if (diffX < 50) {
                if(left < 0) {
                    __info.$pressed.animate({marginLeft:  "0"}, 300); // 右滑
                }
            } else if (diffX < 100) {
                if(left < 0) {
                    __info.$pressed.animate({marginLeft:  "0"}, 200); // 右滑
                }
            } else {
                if(left < 0) {
                    __info.$pressed.animate({marginLeft:  "0"}, 100); // 右滑
                }
            }
        });

        return this;
    };

    /*为元素增加touch后滑动的效果，主要用于宽度大于屏幕后的scroll效果*/
    var __info2 = {
        // 用于记录按下的点
        x: 0, // 横坐标
        y: 0, // 纵坐标
        lx: 0,  //最近一次的滑动位置

        //滑动到最左和最右时margin-left范围
        leftmost : 0,
        rightmost: 0
    };

    $.fn.addTouchScrollAction = function() {
        $(this).off("touchstart").on('touchstart', function (e) {
        	$('.line-buttons').off('touchstart').on('touchstart',function(e){   //阻止冒泡事件
            	e=window.event||e;
	    		if(document.all){  
	      		  	e.cancelBubble=true;
	    		}else{
	        		e.stopPropagation();
	    		}
        	});
            var $this = $(this);

            //设置最大滑动
            var width = $this.width();
            if(width >= window.screen.width) {
                __info2.leftmost = window.screen.width - $this.width();
                __info2.rightmost = 0;
            }

            // 记录开始按下时的点
            var touches = event.touches[0];
            __info2.x = touches.pageX;
            __info2.y = touches.pageY;
            __info2.lx = touches.pageX;
        }).off("touchmove").on('touchmove', function (e) {
        	$('.line-buttons').off('touchmove').on('touchmove',function(e){   //阻止冒泡事件
            	e=window.event||e;
	    		if(document.all){  
	      		  	e.cancelBubble=true;
	    		}else{
	        		e.stopPropagation();
	    		}
        	});
            // 计算划动过程中x和y的变化量
            var touches = event.touches[0];
            var delta = {
                x: touches.pageX - __info2.x,
                y: touches.pageY - __info2.y
            };

            // 纵向位移大于横向位移，不处理
            if (Math.abs(delta.x) < Math.abs(delta.y)) {
                return;
            }

            //阻止其他事件
            event.preventDefault();

            var $this = $(this);
            var left = getMarginLeft($this);
            var to = calcMarginLeft(left, touches.pageX - __info2.lx, __info2.leftmost, __info2.rightmost);
            //
            if(to != left) {
                $this.css({marginLeft: to + "px"});
            }

            __info2.lx = touches.pageX;
        })/*.off('touchend').on('touchend', function (e) {
        })*/;

        return this;
    };
}($);