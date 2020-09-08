/**
	 * @time 2014-01-11 
	 * 图片切换组建
	 */
(function() {
	var timer, j = 0;
	$.fn.comboSwitch = function(options){
		var target = $(this);
		$.fn.comboSwitch.defaults = {
			data : null,			//图片数组
			speed : 1000,			//切换速度
			timer : 1500,			//切换时间
			posStart : 550,
			posEnd : -500,
			style : "banner-con"
		};

		//设置用户自定义初始化内容
		var opts = $.extend({},$.fn.comboSwitch.defaults, options);
		_createSwitch(target, opts);
		return opts;
	};
	
	/**
	 * 内部id表
	 */
	var getSwitchIdMap = function(id){
		var _idMap = {
				"pluginId" : id,							//组件id
				
				"imgListId" : id + "ImgCon",				//图片容器id
				"imgId" : id + "Img",
				"imgBtnConId" : id + "ImgBtnCon",
				"imgBtnId" : id + "Btn"
				
		};
		return _idMap;
	};

	
	/**
	 * 创建分页栏结构
	 */
	var _createSwitch = function(target, opts){
		var _id = target.get(0).id;
		var data = opts.data;
		var _idMap = getSwitchIdMap(_id);
		
		if(_id == null){
			return;
		}
		target.addClass(opts.style);
		
		var switchMain = $('<div id="'+ _idMap.imgListId +'" class="banner-img-list" ></div>');
		var switchBtn = $('<div id="'+ _idMap.imgBtnConId +'" class="banner-btn"></div>');
		var btnCon = $('<ul></ul>');
		target.append(switchMain);
		switchBtn.append(btnCon);
		target.append(switchBtn);
		
		if(data == null) return;
		for(var i = 0; i < data.length; i++){
			var imgItem = $('<div id="'+ _idMap.imgId + i +'" style="background-image:url('+ data[i] +')"></div>');
			var btnItem = $('<li id="'+ _idMap.imgBtnId + i +'">'+ i +'</li>');
			switchMain.append(imgItem);
			btnCon.append(btnItem);
		}
		
		btnEvent(_id, _idMap, opts);
		//初始化
		$("#"+_idMap.imgId + j).animate({left: 0}, opts.timer); 
		$("#"+_idMap.imgBtnId + j).addClass("active");
		
		autoPlay(_id, _idMap, opts);
	}
	
	var autoPlay = function(_id, _idMap, opts){
		var i = opts.data.length;
		timer = setInterval(function(){
			j++;
			var n = j-1;
			if(j > i||j == i){
				j = 0;
				n = i-1;
			}
			
			$("#"+_idMap.imgBtnConId).find("li").removeClass("active");
			$("#"+_idMap.imgId + j).animate({left: 0}, opts.timer ); 
			$("#"+_idMap.imgId + n).animate({left: opts.posEnd}, opts.timer, function(){
				$("#"+_idMap.imgId + n).css({"left": opts.posStart});
			});
			$("#"+_idMap.imgBtnId + j).addClass("active");
		},opts.speed);
	}
	
	var btnEvent = function(_id, _idMap, opts){
		var isAnimate = false;
		$("#"+_idMap.imgBtnConId).find("li").hover(function(){
			clearInterval(timer);
		},function(){
			autoPlay(_id, _idMap, opts);
		})
		$("#"+_idMap.imgBtnConId).find("li").click(function(){
			if($(this).hasClass("active")) return;
			if(isAnimate == true) return;
			isAnimate = true;
			var m = $(this).text();
			$("#"+_idMap.imgId + j).animate({left: opts.posEnd}, opts.timer, function(){
				$("#"+_idMap.imgId + j).css({"left": opts.posStart});
				j = m;
			});
			
			$("#"+_idMap.imgId + m).animate({left: 0}, opts.timer, function(){
				isAnimate = false;
			});
			$("#"+_idMap.imgBtnConId).find("li").removeClass("active");
			$("#"+_idMap.imgBtnId + m).addClass("active");
		})
	}

})();