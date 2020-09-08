/**
	 * @time 2014-01-11 
	 * switch button
	 */
(function() {
	var timer, j = 0;
	$.fn.comboSwitchButton = function(options){
		var target = $(this);
		$.fn.comboSwitchButton.defaults = {
			width : 100,
			timer : 200,
			onText : "已开启",
			offText : "已关闭",
			style : "btn-switch",
			switchId : "",
			defaultSwitchOn : false,
			onBeforeEvent : function(){ return true; },
			offBeforeEvent : function(){ return true; },
			onEvent: function(){},
			offEvent: function(){}
		};

		var opts = $.extend({},$.fn.comboSwitchButton.defaults, options);
		_createSwitchButton(target, opts);
		return opts;
	};
	

	
	/**
	 * create switch button
	 */
	var _createSwitchButton = function(target, opts){
		if(!target.get(0)){
			return;
		}
		target.addClass(opts.style);
		
		var switchBtn = $('<button type="button" class="btn"></button>');
		var switchCheck = $('<input type="checkbox" id="'+ opts.switchId +'" name="'+ opts.switchId +'" style="display:none;" />');
		if(opts.defaultSwitchOn){
			target.addClass("on");
			switchBtn.css({"width":opts.width,"margin-left": 0.5*opts.width}).text(opts.onText);
			switchCheck.get(0).checked = true;
		}else{
			target.addClass("off");
			switchBtn.css({"width":opts.width,"margin-right": 0.5*opts.width}).text(opts.offText);
		}
		
		target.unbind();
		target.bind("click", function(){
			if(switchCheck.is(":checked")){
				if((opts.offBeforeEvent)()){
					$(this).find("button").animate({marginLeft: 0, marginRight: 0.5*opts.width}, opts.timer, function(){
						$(this).text(opts.offText).parent().removeClass("on").addClass("off");
						switchCheck.get(0).checked = false;
						
						(opts.offEvent)();
					});
				}else{
					return;
				}
			}else{
				if((opts.onBeforeEvent)()){
					$(this).find("button").animate({marginLeft: 0.5*opts.width, marginRight: 0}, opts.timer, function(){
						$(this).text(opts.onText).parent().removeClass("off").addClass("on");
						switchCheck.get(0).checked = true;
						
						(opts.onEvent)();
					});
				}else{
					return;
				}
			}
		})
		
		target.append(switchBtn);
		target.append(switchCheck);
	}
	
	$.fn.resetSwitchButton = function(opts){
		var switchCheck = $(this).find("input");
		var switchBtn = $(this).find("button");
		if(switchCheck.is(":checked")){
			switchBtn.animate({marginLeft: 0, marginRight: 0.5*opts.width}, opts.timer, function(){
				switchBtn.text(opts.offText).parent().removeClass("on").addClass("off");
				switchCheck.get(0).checked = false;
			});
		}else{
			switchBtn.animate({marginLeft: 0.5*opts.width, marginRight: 0}, opts.timer, function(){
				switchBtn.text(opts.onText).parent().removeClass("off").addClass("on");
				switchCheck.get(0).checked = true;
			});
		}
	}

})();