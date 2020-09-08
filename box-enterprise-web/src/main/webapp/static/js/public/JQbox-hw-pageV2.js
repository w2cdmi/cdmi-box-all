/**
	 * @time 2013-06-24 
	 * 分页组件
	 */
(function() {
	$.fn.comboPage = function(options){
		var target = $(this);
		$.fn.comboPage.defaults = {
			curPage : 1,			//当前页数
			perDis : 20,			//每页显示条数
			totaldata : null,		//总数据条数
			pageItems : ["16","32","64","80","120"],
			showInfo : true,		//是否显示信息栏
			pageSkip : true,		//是否显示跳转功能
			showPageSet : false,		//是否显示每页条数设置
			lang : "zh-cn",
			style : "page"	//使用的样式
		};

		//设置用户自定义初始化内容
		var opts = $.extend({},$.fn.comboPage.defaults, options);
		_createPage(target, opts);
		return opts;
	};
	
	/**
	 * 内部id表
	 */
	var getPageIdMap = function(id){
		var _idMap = {
				"pluginId" : id,							//组件id
				
				"pageInfoId" : id + "_info",				//信息栏的id
				"formTotalPageId" : id + "_infoTotalPage",	
				"formTotalDataId" : id + "_infoTotalData",	
				"pageSkipId" : id + "_skip",				//跳转模块id
				"pageMainId" : id + "_main",				//分页按钮模块的id
				"pageItemSetId" : id + "_itemSet",			//每页显示的条数
				"pageItemSelectId" : id + "_itemSelect",		
				
				"pageCurId" : id + "_hiddenCurPage",		//记录当前页码的id
				"pageSkipTextId" : id + "_skipText",		//跳转文本框的id
				"pageSkipBtnId" : id + "_skipBtn",			//跳转按钮的id
				
				"pagePrevId" : id + "_prevBtn",				//上一页按钮的id
				"pageNextId" : id + "_nextBtn",				//下一页按钮的id
				"pageFirstId" : id + "_firstBtn",			//首页按钮的id
				"pageLastId" : id + "_lastBtn",				//尾页按钮的id
				"pageOtherId" : id + "_otherBtn"			//其他页码的id前缀
				
		};
		return _idMap;
	};
	
	/**
	 * 国际化文字
	 */
	var getPageText = function(encode){
		var text = {};
		if(encode=='zh-cn'){
			text = {
				"pageInfo1" : "共",
				"pageInfo2" : "页，",
				"pageInfo3" : "条记录",
				"pageInfo4" : "，每页",
				"pageInfo5" : "条",
				"pageSkip" : "转到",
				"pageBtnPrev" : "上一页",
				"pageBtnNext" : "下一页"
			}
		}
		if(encode=='en'){
			text = {
				"pageInfo1" : "Total",
				"pageInfo2" : "Pages,",
				"pageInfo3" : "Records",
				"pageInfo4" : ", ",
				"pageInfo5" : "Records on Each Page",
				"pageSkip" : "Go To",
				"pageBtnPrev" : "Previous",
				"pageBtnNext" : "Next"
			}
		}
		
		return text;
	};
	
	/**
	 * 创建分页栏结构
	 */
	var _createPage = function(target, opts){
		var _id = target.get(0).id;
		if( _id == null ){
			return;
		}
		var _text = getPageText(opts.lang);  	//获取国际化文本
		var _idMap = getPageIdMap(_id);			//获取各元素id
		target.addClass(opts.style);         	//添加样式
		target.addClass("clearfix");

		var pageInfo = '<div id="'+ _idMap.pageInfoId +'" class="page-info pull-left">'+ _text.pageInfo1 +'<strong id="'+ _idMap.formTotalPageId +'"></strong>'+ _text.pageInfo2 +'<strong id="'+ _idMap.formTotalDataId +'"></strong>'+ _text.pageInfo3 +'</div>';
		var pageItemSet = '<div id="'+ _idMap.pageItemSetId +'" class="page-set pull-left">'+ _text.pageInfo4 +'<select id="'+ _idMap.pageItemSelectId +'"></select>'+ _text.pageInfo5 +'</div>';
		var pageSkip = '<div id="'+ _idMap.pageSkipId +'" class="page-skip pull-right form-search"><span class="page-skip-text">'+ _text.pageSkip +'</span>'+
						'<div class="input-append"><input id="'+ _idMap.pageSkipTextId +'" class="search-query span1" type="text" onkeyup="this.value=this.value.replace(/[^0-9]*$/,\'\')" ondragenter="return false" onpaste="return !clipboardData.getData(\'text\').match(/\D/)" /><button class="btn" id="'+ _idMap.pageSkipBtnId +'" type="button"><i class="icon-arrow-right"></i></button></div>'+
						'</div>';
		var pageMain = '<div id="'+ _idMap.pageMainId +'" class="page-main"></div><input id="'+ _idMap.pageCurId +'" type="hidden" value="'+ opts.curPage +'" />';
		
		if(opts.showInfo == true){
			target.append(pageInfo);
		}
		if(opts.showPageSet == true){
			target.append(pageItemSet);
			var optionItem="";
			for(var i=0;i<opts.pageItems.length; i++){
				optionItem += '<option value="'+ opts.pageItems[i] +'">'+ opts.pageItems[i] +'</option>';
			}
			$("#"+_idMap.pageItemSelectId).append(optionItem);
		}
		if(opts.pageSkip == true){
			target.append(pageSkip);
		}
		
		target.append(pageMain);
		//创建分页主体
		_createPageBody($("#"+ _idMap.pageMainId), opts, _idMap, opts.curPage, opts.perDis, opts.totaldata);
	}

	/**
	 *
	 * 创建分页栏主体
	 */
	var _createPageBody = function(target, opts, _idMap, curPage, perDis, totaldata){
		if(totaldata != null && totaldata > perDis){
			$("#"+ _idMap.pluginId).show();
		}else{
			$("#"+ _idMap.pluginId).hide();
			return;
		}
		
		if(opts.showPageSet == true){
			for(var i=0;i<opts.pageItems.length; i++){
				if(perDis == opts.pageItems[i]){
					$("#"+_idMap.pageItemSelectId).find("option").eq(i).attr("selected","selected");
					break;
				}else{
					if(i == (opts.pageItems.length-1)){
						alert("设置的默认分页条数应为选项["+ opts.pageItems +"]中的其中一个值");
					}
				}
			}
			$("#"+_idMap.pageItemSelectId).change(function(){
				$.fn.comboPage.setPerPageNum(opts, _idMap, $(this).val());
				return false; //防止触发浏览器的默认行为
			})
		}
		
		var totalPages = Math.ceil(totaldata/perDis);
		$("#"+ _idMap.formTotalPageId).html(totalPages);
		$("#"+ _idMap.formTotalDataId).html(totaldata);
		
		var _text = getPageText(opts.lang);  	//获取国际化文本
		var readyNum = 2;
		var minNum = 2;
		var maxNum= totalPages-1;
		var dynamicPages = "";
		
		//判断当前页是否在首尾页的区间
		if(curPage < 1){
			curPage = 1;
		}
		if(curPage > totalPages){
			curPage = totalPages;
		}
		
		//判断当前页与首页、尾页的关系
		if (curPage < readyNum +2) {
			minNum = 2;
			if (totalPages > readyNum +2) {
				maxNum = readyNum + 1;
			} else {
				maxNum = totalPages - 1;
			}
		} else {
			minNum = curPage;
			if (curPage >= totalPages - readyNum) {
				minNum = totalPages - readyNum;
			}
			if (curPage <= totalPages - readyNum) {
				maxNum = curPage + 1;
			} else {
				maxNum = curPage;
			}
			if (maxNum > totalPages-1) {
				maxNum = totalPages-1;
			}
		}
		
		
		
		
		//根据当前页码生成中间部分页码
		for(var i = minNum;i<maxNum+1;i++){
			dynamicPages +='<button type="button" class="btn btn-small" id="'+ _idMap.pageOtherId + i +'">'+ i +'</button>';
		}
		if( minNum > 2 ){
			dynamicPages = '<span class="joint">…</span>' + dynamicPages;
		}
		if( maxNum < totalPages - 1 ){
			dynamicPages = dynamicPages + '<span class="joint">…</span>';
		}
		
		//完成页码整体的填充
		var pageMainBody = '<button type="button" class="btn btn-small" id="'+ _idMap.pageFirstId +'">1</button>' + dynamicPages +
							'<button type="button" class="btn btn-small" id="'+ _idMap.pageLastId +'">'+ totalPages +'</button>';
		if(curPage>1){
			pageMainBody = '<button type="button" class="btn btn-small" id="'+ _idMap.pagePrevId +'">'+ _text.pageBtnPrev +'</button>' + pageMainBody;
		}else{
			pageMainBody = '<button type="button" class="btn btn-small" id="'+ _idMap.pagePrevId +'" disabled="disabled">'+ _text.pageBtnPrev +'</button>' + pageMainBody;
		}
		if(curPage<totalPages){
			pageMainBody = pageMainBody + '<button type="button" class="btn btn-small" id="'+ _idMap.pageNextId +'">'+ _text.pageBtnNext +'</button>';
		}else{
			pageMainBody = pageMainBody + '<button type="button" class="btn btn-small" id="'+ _idMap.pageNextId +'" disabled="disabled">'+ _text.pageBtnNext +'</button>';
		}
		target.append(pageMainBody);
		
		//绑定事件
		target.find("button").bind("click",function(){
			_pageEvent(this, opts, _idMap, curPage, totalPages);
		});
		
		//跳转按钮绑定事件
		$("#"+ _idMap.pageSkipBtnId).bind("click",function(){
			var inputPage = $("#"+ _idMap.pageCurId).val();
			_pageEvent(this, opts, _idMap, inputPage, totalPages);
		});
		$("#"+ _idMap.pageSkipTextId).keydown(function(event) {
			if (event.keyCode == 13) {
				var inputPage = $("#"+ _idMap.pageCurId).val();
				_pageEvent($("#"+ _idMap.pageSkipBtnId), opts, _idMap, inputPage, totalPages);
				$(this).blur();
				window.event.cancelBubble = true;//停止冒泡
				window.event.returnValue = false;//阻止事件的默认行为
			}
		})
		
		//添加当前页码的样式
		if(curPage == 1){
			$("#"+ _idMap.pageFirstId).addClass("btn-primary");
		}else if(curPage == totalPages){
			$("#"+ _idMap.pageLastId).addClass("btn-primary");
		}else{
			$("#"+ _idMap.pageOtherId + curPage).addClass("btn-primary");
		}
		
		//
		$("#"+ _idMap.pageCurId).val(curPage);
		
	};

	/** 
	 *
	 * 绑定事件
	 */
	var _pageEvent = function(target, opts, _idMap, curPage, totalPages){
		var _id = $(target).get(0).id;
		var nextCurPage;
		if( $(target).html() == curPage ){
			return;
		}else if(_id == _idMap.pagePrevId ){
			nextCurPage = curPage - 1;
		}else if(_id == _idMap.pageNextId ){
			nextCurPage = curPage + 1;
		}else if(_id == _idMap.pageSkipBtnId ){
			var _val = $("#" + _idMap.pageSkipTextId).val();
			$("#" + _idMap.pageSkipTextId).val("");
			if(_val == null || _val == '' || !_val.match(/^\d+$/) || _val == curPage){
				return;
			}else{
				if(_val > totalPages){
					nextCurPage = totalPages;
				}else if(_val < 1){
					nextCurPage = 1;
				}else{
					nextCurPage = _val;
				}
			}
		}else{
			nextCurPage = parseInt( $(target).html() );
		}
		$.fn.comboPage.pageSkip(opts, _idMap, nextCurPage);
		
	};

	/**
	 * 刷新分页数据
	 */
	$.fn.setPageData = function(opts, curPage, perDis, totaldata){
		var parent = $(this);
		if(parent.get(0) != null){
			var _id = parent.get(0).id;
			var _idMap = getPageIdMap(_id);
			var pageMainBody = $("#" + _idMap.pageMainId);
			
			$("#"+ _idMap.pageSkipBtnId).unbind(); //清除绑定的鼠标事件
			$("#"+ _idMap.pageSkipTextId).unbind(); //清除绑定的回车事件
			
			pageMainBody.html(""); //先置空分页
			_createPageBody(pageMainBody, opts, _idMap, curPage, perDis, totaldata);
		}
	};
	
	/**
	 * 翻页时对外接口方法
	 */
	$.fn.comboPage.pageSkip = function(opts, _idMap, curPage){
		
	};
	
	/**
	 * 设置每页显示条数的对外接口方法
	 */
	$.fn.comboPage.setPerPageNum = function(opts, _idMap, num){

	};

})();