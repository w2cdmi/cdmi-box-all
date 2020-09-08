/**
 * 普通列表组件
 */
(function() {
	if (window.combTable) return;
	window.combTable = {
		eventList: []
	};
	var addEvent = (function() {
		return new Function('env', 'fn', 'obj', 'obj=obj||document;' + (window.attachEvent ? "obj.attachEvent('on'+env,fn)": 'obj.addEventListener(env,fn,false)') + ';combTable.eventList.push([env,fn,obj])')
	})(); //事件绑定
	var detachEvent = (function() {
		return new Function('env', 'fn', 'obj', 'obj=obj||document;' + (window.attachEvent ? "obj.detachEvent('on'+env,fn)": 'obj.removeEventListener(env,fn,false)'))
	})(); //取消事件绑定
	var EnableSelect = function(){
		//启用文本选择
		document.body.onselectstart=function(){return true;}
		$("body").removeClass("disable-select");
	}
	var	DisableSelect = function(){
		//禁止文本选择
		document.body.onselectstart=function(){return false;}
		$("body").addClass("disable-select");
	}
	/**
	 * 表格组件
	 */
	$.fn.comboTableGrid = function(options){
		var parent = $(this);
		$.fn.comboTableGrid.defaults = {
			headData : {},			//标题头数据
			data : [],				//展示数据
			dataNullTip : "",	//没有数据时的提示信息
			width : 0,				//表格宽度, 0为宽度"auto"
			height : 0,				//表格list高度, 0为高度"auto"
			dataId : null,			//数据的id编号
			checkBox : false,		//是否使用checkBox
			checkAll : false,		//全选所有数据
			radioBtn : false,		//是否使用radioButton
			stripe : false,			//是否有条纹
			splitRow : true,		//是否有分隔线
			border : true,			//是否有边框
			miniPadding : false,	//是否为小间距的表格
			colspanDrag : false,	//是否可调整表头宽度，为true时列宽设置绝对宽度px，不能相对宽度%
			hideHeader : false,		//是否隐藏表头
			definedColumn : false,	//是否支持自定义显示列,为true时列宽设置绝对宽度px，不能相对宽度%
			columnDefault: [],		//默认显示的列，为空时显示全部，definedColumn=true 时有效
			style : "basicGrid",	//使用的样式
			taxis : true,			//是否排序
			defaultTaxis : "",		//默认以哪个字段排序
			taxisFlag : false,		//默认排序:升序true,降序false
			string : {
				checkAllTxt : "选择全部",
				checkCurPageTxt : "选择当前页"
			},
			ItemOp : "user-defined"			//是否自定义单元格操作,可选:"user-defined","none"
		};

		//设置用户自定义初始化内容
		var opts = $.extend({},$.fn.comboTableGrid.defaults, options);
		_createGrid(parent, opts);
		return opts;
	};
	
	/**
	 * 获取表格的内部id表
	 */
	var getGridIdMap = function(id){
		var _idMap = {
				"pluginId" : id,							//组件id
				
				"headDivId" : id + "_headDiv",				//表头div的id
				"headId" : id + "_head",					//表头table的id
				"headTrId" : id + "_headTr",				//表头tr的id
				"headItemId" : id + "_headItem",			//表头项的id
				
				"mainDivId" : id + "_mainDiv",				//表格div的id
				"mainId" : id + "_main",					//表格table的id
				"mainTbodyId" : id + "_mainTb",				//表格tbody的id
				
				"definedColumnId" : id + "_definedColumn",	//记录自定义列
				"selectedRowId" : id + "_selectedRow",		//记录当前选择行input的id
				"dragColWidthId" : id + "_dragColWidth",	//记录拖拽后表格列的宽度input的id
				"loadingId" : id + "_loading"				//表格数据loading图层
		};
		return _idMap;
	};
	
	/**
	 * 刷新数据
	 */
	$.fn.setTableGridData = function(data, opts){
		var parent = $(this);
		if(parent.get(0) != null){
			var _id = parent.get(0).id;
			var _idMap = getGridIdMap(_id);
			
			$("#"+_idMap.selectedRowId).val("");
			if(opts.checkBox){
				$("#" + _idMap.headId+"_check").removeAttr("checked");
				if(opts.checkAll){
					$("#" + _idMap.headId+"_check").removeAttr("disabled");
					$("#" + _idMap.headId+"_checkAll").removeAttr("checked").parents("th.check").find("> div").attr("class","check-page");
				}
			}
			if($("#" + _idMap.mainTbodyId).get(0) != null){
				$("#" + _idMap.mainTbodyId).remove();
			}
			var mainTable = $('<tbody id="'+ _idMap.mainTbodyId +'"></tbody>');
			$("#"+_idMap.mainId).append(mainTable);
			var colNum = 0;
			for(var key in opts.headData){
				var widthArr = $("#"+ _idMap.dragColWidthId).val().split(";");
				for(var n=0; n<widthArr.length; n++){
					if(widthArr[n].indexOf(key) != -1){
						var eachWidthArr = widthArr[n].split(",");
						opts.headData[key].width = eachWidthArr[1]+"px";
					}
				}
				colNum++;
			}
			
			_createGridTable(mainTable, opts, data, _idMap, colNum);

			$("#" + _idMap.loadingId).hide();
		}
	};
	
	
	/**
	 * 对外单显示表格进度遮罩层方法
	 */
	$.fn.showTableGridLoading = function(){
		var parent = $(this);
		if(parent.get(0) != null){
			var _id = parent.get(0).id;
			var _idMap = getGridIdMap(_id);
			$("#" + _idMap.loadingId).show();
		}
	};
	
	/**
	 * 对外单元格修改接口方法
	 */
	$.fn.comboTableGrid.setItemOp = function(tableData, rowData, tdItem, colIndex){
		
	};
	
	/**
	 * 对外行选择接口方法
	 */
	$.fn.comboTableGrid.selectTrOp = function(tableData, mainDivId, trTag){

	};
	
	/**
	 * 对外表头单击排序接口方法
	 */
	$.fn.comboTableGrid.taxisOp = function(headItem, flag){
		
	};
	
	/**
	 * 设置表头显示列的接口方法
	 */
	$.fn.setTableColumn = function(newHeadKeyData){
		var parent = $(this);
		var _id = parent.get(0).id;
		var _idMap = getGridIdMap(_id);
		var strColumnId ="";
		for(var i=0; i<newHeadKeyData.length; i++){
			if(i==0){
				strColumnId += newHeadKeyData[i];
			}else{
				strColumnId += "|"+ newHeadKeyData[i];
			}
		}
		$("#"+ _idMap.definedColumnId).val(strColumnId);
	};
	
	/**
	 * 设置表格样式
	 */
	$.fn.setTableGridStyle = function(style){
		var parent = $(this);
		if(parent.get(0) != null){
			var _id = parent.get(0).id;
			parent.attr("class", style);
		}
	};
	
	/**
	 * 获取表格多选按钮的选中项
	 */
	$.fn.getTableGridSelected = function(){
		var _id = $(this).get(0).id;
		var _idMap = getGridIdMap(_id);
		var childrenCheckName = _idMap.mainTbodyId+"_check";
		var childrenRadioName = _idMap.mainTbodyId+"_radio";
		var inputCheckAll = $("#"+ _idMap.headId +"_checkAll");
		var checkSelect = $("input[name="+ childrenCheckName +"]");
		var radioSelect = $("input[name="+ childrenRadioName +"]");
		var data = new Array();
		if(checkSelect.length > 0){
			if(inputCheckAll.get(0) && inputCheckAll.is(":checked")){
				data.push("all");
			}else{
				for(var i = 0; i < checkSelect.length; i++){
					if($(checkSelect[i]).is(":checked")){
						data.push($(checkSelect[i]).val());
					}
				}
			}
		}
		if(radioSelect.length > 0){
			var selectTr = $("#"+_idMap.selectedRowId).val();
			if(selectTr != null && selectTr != ""){
				var selectObjId = $("#"+selectTr).find("input[type=radio]").eq(0).val();
				data.push(selectObjId);
			}
		}
		return data;
	};
	
	/**
	 * 取消表格的全部选中项
	 */
	$.fn.removeTableGridSelected = function(){
		var _id = $(this).get(0).id;
		var _idMap = getGridIdMap(_id);
		var childrenCheckName = _idMap.mainTbodyId+"_check";
		var childrenRadioName = _idMap.mainTbodyId+"_radio";
		var inputCheckPage = $("#"+ _idMap.headId +"_check");
		var inputCheckAll = $("#"+ _idMap.headId +"_checkAll");
		
		if(inputCheckPage.get(0)){
			var selItems = $(this).find("input[name="+ childrenCheckName +"]:checked");
			$(selItems).removeAttr("checked");
			inputCheckPage.removeAttr("checked");
			$("#"+ _idMap.mainTbodyId).find("tr").removeClass("grid_Selected");
			$("#" + _idMap.selectedRowId).val("");
			if(inputCheckAll.get(0)){
				$(this).find("th.check").find("> div").attr("class","check-page");
				if(inputCheckAll.is(":checked")){
					inputCheckAll.removeAttr("checked");
					inputCheckPage.removeAttr("disabled");
					$("input[name="+ childrenCheckName +"]").removeAttr("disabled");
				}
			}
		}
		if($(this).find("input[name="+ childrenRadioName +"]").length > 0){
			var selItems = $(this).find("input[name="+ childrenRadioName +"]:checked");
			$(selItems).removeAttr("checked");
			$("#"+ _idMap.mainTbodyId).find("tr").removeClass("grid_Selected");
			$("#" + _idMap.selectedRowId).val("");
		}
	};
	
	/**
	 * 创建表格,向指定div生成子节点(生成表头容器和表格容器)
	 * 结构:
	 * -----------------------------------------------
	 * |				headTable					 |
	 * -----------------------------------------------
	 * |											 |
	 * |				bodyDiv(bodyTable)			 |
	 * |											 |
	 * -----------------------------------------------
	 */
	var _createGrid = function(parent, opts){
		parent.addClass(opts.style);
		
		if(opts.colspanDrag){
			parent.addClass("headThDrag");
		}
		if(opts.border){
			parent.addClass("basicGrid-border");
		}
		if(opts.stripe){
			parent.addClass("striped");
		}
		if(opts.splitRow){
			parent.addClass("splited");
		}
		
		if(opts.width != 0){
			var oWidth = opts.width + "px";
			parent.css({"width": oWidth});
		}
		
		var _id = parent.get(0).id;
		if(_id == null){
			return;
		}
		
		var tableClass = "table";
		if(opts.border){
			tableClass = tableClass +" table-bordered";
		}
		if(opts.miniPadding){
			tableClass = tableClass +" table-condensed";
		}
		
		var _idMap = getGridIdMap(_id);
		var definedColumnCatch = $('<input id="'+_idMap.definedColumnId+'" type="hidden">');
		var selectRowCatch = $('<input id="'+_idMap.selectedRowId+'" type="hidden">');
		var dragColWidthCatch = $('<input id="'+_idMap.dragColWidthId+'" type="hidden">');
		var gridHead = $('<div class="grid_head" id="'+ _idMap.headDivId +'"><table class="'+ tableClass +'" id="'+ _idMap.headId +'" border="0" cellPadding="0" cellSpacing="0" ><thead>'+
				'<tr id="'+ _idMap.headTrId +'"></tr>'+
				'</thead></table></div>');
		var gridMainHeight;
		if(opts.height <= 0){
			gridMainHeight = "auto";
		}else{
			gridMainHeight = opts.height +"px";
		}
		var gridMain = $('<div id="'+ _idMap.mainDivId +'" class="grid_body" style="height:'+ gridMainHeight +';">'+
				'<table class="'+ tableClass +'" id="'+ _idMap.mainId +'" border="0" cellPadding="0" cellSpacing="0">'+
				'<tbody id="'+ _idMap.mainTbodyId +'"></tbody>'+
				'</table></div>');
		var gridLoading = $('<div id="'+ _idMap.loadingId +'" class=grid_loading><div></div></div>');
		
		if(opts.hideHeader){
			gridHead.hide();
			gridMain.css("border-top","0 none");
		}
		parent.append(definedColumnCatch);
		parent.append(selectRowCatch);
		parent.append(dragColWidthCatch);
		parent.append(gridHead);
		parent.append(gridMain);
		parent.append(gridLoading);
		parent.setTableColumn(opts.columnDefault);
		
		//对齐遮罩层坐标
		var lTop = 0 - gridMain.height();
		var lHeight = gridMain.height();
		gridLoading.find("div").css({
			"left" : 0,
			"top" : lTop + "px",
			"width" : "100%",
			"height" : lHeight + "px"
		});
		
		var headTr = $("#" + _idMap.headTrId);
		var mainTable = $("#" + _idMap.mainTbodyId);
		var colNum = 0;
		for(var key in opts.headData){
			colNum++;
		}
		_creatGridHead(headTr, opts, _idMap);
		_createGridTable(mainTable, opts, opts.data, _idMap, colNum);
		_selectGridAll(opts.data, (_idMap.headId+"_check"), (_idMap.mainTbodyId+"_check"), _idMap, opts);

		gridMain.scroll(function(){
			_tableHeadPos(_idMap);
		})
	}
	
	/**
	 * 设置表格宽度
	 */
	var _setGridWidth = function(_idMap, opts){
		if(opts.colspanDrag || opts.definedColumn){
			$("#"+_idMap.mainId).css({"width":0,"max-width":"none"});
		}
		var _mainTableWidth = $("#"+_idMap.mainId).width(); 
		$("#"+_idMap.headId).css("width", _mainTableWidth);
		
		$(window).bind("resize",function(){
			var myw = $("#"+_idMap.mainId).width(); 
			$("#"+_idMap.headId).css("width",myw);
		})
	};
	
	/**
	 * 设置表格的动态高度和宽度的接口方法
	 */
	$.fn.comboTableGrid.setDynamicSize = function(parent){
		
	}
	
	/**
	 * 创建表头
	 */
	var _creatGridHead = function(parent, opts, _idMap){
		var headData = opts.headData;
		var i = 0;
		if(opts.checkBox){
			var checkTag = $('<th class="check" style="width:30px;"><input id="'+ _idMap.headId +'_check" type="checkbox" /></th>');
			if(opts.checkAll){
				checkTag = $('<th class="check" style="width:30px;"><div class="check-page">'+
						'<ul><li><label class="checkbox inline"><input id="'+ _idMap.headId +'_check" type="checkbox" />'+ opts.string.checkCurPageTxt +'</label></li>'+
						'<li><label class="checkbox inline"><input id="'+ _idMap.headId +'_checkAll" type="checkbox" />'+ opts.string.checkAllTxt +'</label></li></ul>'+
						'</div></th>');
				checkTag.find("div").hover(function(){
					$(this).find("ul").show();
				},function(){
					$(this).find("ul").hide();
				})
				checkTag.find("label").eq(1).click(function(){
					var checkAll = $(this).find(":checkbox").eq(0);
					var trchecks = $("#"+ _idMap.mainTbodyId).find("tr td:first-child :checkbox");
					if(checkAll.is(":checked")){
						$("#"+ _idMap.selectedRowId).val("all");
						$(this).parent().prev().find(":checkbox").attr("disabled","disabled");
						$(this).parent().prev().find(":checkbox").get(0).checked = false;
						$(this).parents("th.check").find("> div").attr("class","check-all-page");
						trchecks.each(function() {
							this.checked = false;
							this.disabled = true;
							$(this).parents("tr").removeClass("grid_Selected");
						})
					}else{
						$("#"+ _idMap.selectedRowId).val("");
						$(this).parent().prev().find(":checkbox").removeAttr("disabled");
						$(this).parent().prev().find(":checkbox").get(0).checked = false;
						$(this).parents("th.check").find("> div").attr("class","check-page");
						trchecks.each(function() {
							this.checked = false;
							this.disabled = false;
							$(this).parents("tr").removeClass("grid_Selected");
						})
					}
				})
			}
			parent.append(checkTag);
		}
		if(opts.radioBtn){
			var radioTag = $('<th class="radio-check" style="width:30px;"></th>');
			parent.append(radioTag);
		}
		for (var key in headData){
			var itemTitle = headData[key].title;
			var itemWidth = headData[key].width;
			var itemTaxis = headData[key].taxis;
			var headItem;
			if(opts.taxis){
				
				//添加排序功能
				if(itemTaxis){
					if(key == opts.defaultTaxis){
						if(opts.taxisFlag == true){
							headItem = $('<th id="'+ (_idMap.headItemId + i) +'" class="taxis" style="cursor:pointer;width:'+ headData[key].width +';" ><span class="taxis-span ascend"></span><p>'+ itemTitle +'</p></th>');
						}else{
							headItem = $('<th id="'+ (_idMap.headItemId + i) +'" class="taxis" style="cursor:pointer;width:'+ headData[key].width +';" ><span class="taxis-span descend"></span><p>'+ itemTitle +'</p></th>');
						}
					}else{
						headItem = $('<th id="'+ (_idMap.headItemId + i) +'" style="cursor:pointer;width:'+ headData[key].width +';" ><span class="taxis-span"></span><p>'+ itemTitle +'</p></th>');
					}
					
					headItem.hover(function(){
						$(this).addClass("taxisBg");
						if(!$(this).hasClass("taxis")){
							$(this).find(".taxis-span").addClass("taxisTag");
						}
					}, function(){
						$(this).removeClass("taxisBg");
						$(this).find(".taxis-span").removeClass("taxisTag");
					});
					//利用闭包传递key字段值，否则只能获取最后一个
					var taxisFunc = function(tempKey){
						return function(e){
							var srcElement = e.srcElement || e.target;
							if($(srcElement).parent().hasClass("cols-drag")){
								return;
							}
							$(this).addClass("taxis")
								.find(".taxis-span").removeClass("taxisTag")
								.end().siblings().removeClass("taxis")
								.find(".taxis-span").removeClass("descend ascend");
							if($(this).find(".taxis-span").hasClass("descend")){
								//升序
								$(this).find(".taxis-span").removeClass("descend").addClass("ascend");
								$.fn.comboTableGrid.taxisOp(tempKey, true);
							}else{
								//降序
								$(this).find(".taxis-span").removeClass("ascend").addClass("descend");
								$.fn.comboTableGrid.taxisOp(tempKey, false);
							}
						}
					}(key);
					headItem.bind("click",taxisFunc);
				} else{
					headItem = $('<th id="'+ (_idMap.headItemId + i) +'" style="width:'+ headData[key].width +';" ><p>'+ itemTitle +'</p></th>');
				}
			} else{
				headItem = $('<th id="'+ (_idMap.headItemId + i) +'" style="width:'+ headData[key].width +';" ><p>'+ itemTitle +'</p></th>');
			}
			
			if(opts.colspanDrag){
				var colDragBar = $('<span></span>');
				var colDragCon = $('<span class="cols-drag"></span>');
				colDragCon.append(colDragBar);
				headItem.prepend(colDragCon);
			}
			headItem.attr("name",key);
			parent.append(headItem);
			i++;
		}
		
		//表头拖动事件
		if(opts.colspanDrag){
			_colDragEvent(_idMap, opts);
		}
		
		return i;
	};
	
	/**
	 * 创建表格主体，根据表头自动取值填写表格
	 */
	var _createGridTable = function(parent, opts, tableData, _idMap, colNum){
		var headInfo = {};
		if(opts.definedColumn){
			var definedColKeyGroup = "|"+$("#"+ _idMap.definedColumnId).val()+"|";
			$("#"+ _idMap.headTrId).find("th").not(".check,.radio-check").hide().addClass("hideTh");
			for (var key in opts.headData){
				if(definedColKeyGroup == "||"){
					$("#"+ _idMap.headTrId).find("th").not(".check,.radio-check").show().removeClass("hideTh");
					headInfo = opts.headData;
				}else if(definedColKeyGroup.indexOf("|"+key+"|") != -1){
					$("#"+ _idMap.headTrId).find("th[name="+ key +"]").show().removeClass("hideTh");
					var _infoArr = {};
					for (var subkey in opts.headData[key]){
						_infoArr[subkey] = opts.headData[key][subkey];
					}
					headInfo[key] = _infoArr;
				}
			}
		}else{
			headInfo = opts.headData;
		}
		
		if(tableData == null || tableData == "" || tableData == undefined){
			if(opts.dataNullTip != ""){
				var tdWidth = $("#"+ _idMap.headId).outerWidth() - 1;
					tdWidth = opts.colspanDrag ? (tdWidth +"px") : "auto";
				var trTag = $("<tr><td style=\"text-align:center; padding:15px 0; width:"+ tdWidth +";\"></td></tr>");
					trTag.find("td").append(opts.dataNullTip);
				parent.append(trTag);
			}
			$("#" + _idMap.loadingId).hide();
			return;
		}
		var data = tableData;
		var selectRow = $("#" + _idMap.selectedRowId);
		var trClass = "grid_Odd";
		
		for(var i = 0; i < data.length; i++){
			var trTag = $("<tr id="+ (_idMap.mainTbodyId+"_tr"+i) +" class="+ trClass +"></tr>");
			var idData = (data[i][opts.dataId] == null ? "" : data[i][opts.dataId]);
			parent.append(trTag);
			
			if(opts.checkBox){
				var check = $('<input id="'+ _idMap.pluginId +'_check'+ i +'" name="'+ _idMap.mainTbodyId +'_check" type="checkbox"  value="'+ idData +'" />');
				var checkTag = $('<td class="check" style="width:30px;"></td>');
				checkTag.append(check);
				trTag.append(checkTag);
				check.click(function(){
					_setGridCheckOp(tableData, _idMap, opts, $(this).parents("tr"), 0);
				});
			}
			
			if(opts.radioBtn){
				var radio = $('<input id="'+ _idMap.pluginId +'_radio'+ i +'" name="'+ _idMap.mainTbodyId +'_radio" type="radio"  value="'+ idData +'" />');
				var radioTag = $('<td class="radio-check" style="width:30px;"></td>');
				radioTag.append(radio);
				trTag.append(radioTag);
				radio.click(function(){
					_setGridRadioCheckOp(tableData, _idMap, opts, $(this).parents("tr"), 3);
				});
			}
			
			for (var key in headInfo){
				var cls = headInfo[key].cls == undefined ?'':'class="'+ headInfo[key].cls +'"';
				var itemWidth = headInfo[key].width;
				var itemData = (data[i][key] == null ? "" : data[i][key]);
				var tdTag = $('<td title="'+ itemData +'" ><p '+cls+'>'+ itemData +'</p></td>');
				if(i == 0){
					tdTag = $('<td style="width:'+ itemWidth +';" title="'+ itemData +'" ><p '+cls+'>'+ itemData +'</p></td>');
				}
				trTag.append(tdTag);
				
				if(opts.ItemOp == "user-defined"){
					$.fn.comboTableGrid.setItemOp(data, data[i], tdTag, key);
				}
				
				if($("#"+ _idMap.headTrId).find("th[name="+ key +"]").hasClass("taxis")){
					tdTag.addClass("taxisColumn");
				}
			}
			
			if(!opts.checkBox && !opts.radioBtn){
				var dataIdTag = $('<input type="hidden" value="'+ idData +'" />');
				trTag.find("td:first-child").append(dataIdTag);
			}
			
			if(trClass == "grid_Odd"){
				trClass = "grid_Even";
			} else{
				trClass = "grid_Odd";
			}
			_setGridTrHover(trTag);
			//点击行选中正行列表
			trTag.click(function(){
				if(opts.checkBox){
					if($("#"+ _idMap.selectedRowId).val() == "all"){
						return;
					}
					var ev = arguments[0] || window.event;
					var srcElement = ev.srcElement || ev.target;
					var trcheck = $(this).find(":checkbox").eq(0);
					var targetName = srcElement.tagName.toLowerCase();
					if(targetName == "input" || srcElement.className == "check" || targetName == "a"){
					} else {
						var trcheckSiblings = $(this).siblings("tr").find(":checkbox");
						if(trcheck.is(":checked")){
						    	var checkNum = 0,trchecks = $("#" + _idMap.mainTbodyId).find(":checkbox");
						    	for(var i = 0; i<trchecks.length;i++){
						    	    if($(trchecks[i]).is(":checked")){
						    			checkNum++;
						    	    };
						    	}
						    	if(checkNum <2 ){
									trcheck[0].checked=false; 
									_setGridCheckOp(tableData, _idMap, opts, $(this), 1);
						    	}else{
									trcheckSiblings.each(function() {
										this.checked=false; 
									})
						    	    trcheck[0].checked=true;
									_setGridCheckOp(tableData, _idMap, opts, $(this), 2);
						    	}
						} else {
							trcheckSiblings.each(function() {
								this.checked=false; 
							})
							trcheck[0].checked=true; 
							_setGridCheckOp(tableData, _idMap, opts, $(this), 1);
						}
						
					}
				}else if(opts.radioBtn){
					var ev = arguments[0] || window.event;
					var srcElement = ev.srcElement || ev.target;
					var trRadio = $(this).find(":radio").eq(0);
					var targetName = srcElement.tagName.toLowerCase();
					if(targetName == "input" || srcElement.className == "radio-check" || targetName == "a"){
					} else {
						if(!trRadio.is(":checked")){
							trRadio[0].checked=true;
							_setGridRadioCheckOp(tableData, _idMap, opts, $(this), 3);
						}
					}
				}
			});
		}
		$("#" + _idMap.loadingId).hide();
		_setGridWidth(_idMap, opts);
		$.fn.comboTableGrid.setDynamicSize(_idMap);
	};
	
	/**
	 * 动态调整表头偏移位置
	 */
	var _tableHeadPos = function(_idMap){
		var marL = 0 - $("#"+ _idMap.mainDivId).get(0).scrollLeft;
		$("#"+ _idMap.headId).css("margin-left", marL);
	}
	
	/**
	 * 表头拖拽事件
	 */
	var _colDragEvent = function(_idMap, opts){
		var mousePosX, newMousePosX, targetBar, bindEl, defaultLeft,
			minColWidth = 50,
			maxColWidth = 2000;
		
		var mEvent = function(e){
			newMousePosX = e.clientX;
			targetBar.css({"left": defaultLeft + newMousePosX - mousePosX});
		}
		var uEvent = function(){
			var _w = parseInt(targetBar.parent().parent().css("width"));
			var _newW = _w + (newMousePosX - mousePosX);
				_newW = (_newW < minColWidth) ? minColWidth : (_newW > maxColWidth) ? maxColWidth : _newW;
			var index = $("#"+ _idMap.headTrId).find("th").not(".hideTh").index(targetBar.parent().parent());
			var tbodyTr = $("#"+ _idMap.mainTbodyId).find("tr").eq(0);
			
			targetBar.css({"left": defaultLeft, "background-color": "transparent"});
			targetBar.parent().parent().css("width", _newW);
			tbodyTr.find("td").eq(index).css("width", _newW);
			
			//保存新的宽度值
			var key = targetBar.parent().parent().attr("name");
			var eachWidth = key +","+ _newW;
			var colWidthInput = $("#"+ _idMap.dragColWidthId);
			var colWidthInputVal = colWidthInput.val();
			var widthArr = colWidthInputVal.split(";");
			for(var n=0; n<widthArr.length; n++){
				if(widthArr[n].indexOf(key) != -1){
					widthArr.splice(n,1);
					colWidthInputVal = widthArr.join(";");
				}
			}
			colWidthInput.val((colWidthInputVal == "") ? eachWidth : (colWidthInputVal +";"+ eachWidth));
			
			_setGridWidth(_idMap, opts);
			_tableHeadPos(_idMap);
			
			detachEvent("mousemove", mEvent, bindEl);
			detachEvent("mouseup", uEvent, bindEl);
			/*IE下窗口外部拖动*/
			bindEl && (detachEvent("losecapture", uEvent, bindEl), bindEl.releaseCapture());
			
			EnableSelect();
		}
		addEvent("mousedown", function(e){
			var srcElement = e.srcElement || e.target;
			if($(srcElement).parent().hasClass("cols-drag")){
				targetBar = $(srcElement);
				bindEl = targetBar.get(0).setCapture && targetBar.get(0);
				targetBar.css({"background-color": "#aaa"});
				defaultLeft = parseInt(targetBar.css("left"));
				mousePosX = e.clientX;
				
				addEvent("mousemove", mEvent, bindEl);
				addEvent("mouseup", uEvent, bindEl);
				/*IE下窗口外部拖动*/
				bindEl && (addEvent("losecapture", uEvent, bindEl), bindEl.setCapture());
				
				DisableSelect();
			}
		}, $("#"+ _idMap.headTrId).get(0));
	}
	
	/**
	 * 设置Tr的mouseOver样式
	 */
	var _setGridTrHover = function(trObj){
		if(trObj.get(0) != null){
			trObj.hover(
				function () {
					$(this).addClass("grid_Hover");
				},
				function () {
					$(this).removeClass("grid_Hover");
				}
			);
		}
	};
	
	/**
	 * 设置Tr的选择样式
	 */
	var _setGridTrSelected = function(_idMap, trObj, selectRow, handleMode){
		var selRow = selectRow.val();
		var nowRow = trObj.get(0).id;
		
		//handleMode:0为点击checkbox,1、2为左键点击tr,3为radio选择
		if(handleMode == 0){
		    var trcheck = trObj.find(":checkbox");
			if($(trcheck[0]).is(":checked")){
			    trObj.addClass("grid_Selected");
    			selectRow.val(nowRow);
			}else{
			    trObj.removeClass("grid_Selected");
			    if(selRow != "" && selRow != null&&selRow == nowRow){
					selectRow.val("");
			    }
			}
			
		}else if(handleMode == 1){
		    	$("#" + _idMap.pluginId + " .grid_Selected").removeClass("grid_Selected");
				if(selRow != nowRow) {
					trObj.addClass("grid_Selected");
					selectRow.val(nowRow);
				}else if(selRow == nowRow){
					selectRow.val("");
				}
		}else if(handleMode == 2){
		    	$("#" + _idMap.pluginId + " .grid_Selected").removeClass("grid_Selected");
				trObj.addClass("grid_Selected");
				selectRow.val(nowRow);
		}else if(handleMode == 3){
				if(selRow != nowRow) {
					$("#" + _idMap.pluginId + " .grid_Selected").removeClass("grid_Selected");
					trObj.addClass("grid_Selected");
					selectRow.val(nowRow);
				}
		}
		
	};
	
	/**
	 * 设置checkBox的全选反选
	 */
	var _selectGridAll = function(tableData, headCheckId, childrenCheckName, _idMap, opts){
		var headCheck = $("#"+headCheckId);
		if(headCheck.get(0) != null){
			headCheck.click(function(){
				var checkedVal = headCheck.attr("checked");
				var trcheckSiblings = $("input[name="+ childrenCheckName +"]");
				if(headCheck.is(":checked")){
					trcheckSiblings.each(function() {
						this.checked=true; 
					})
					$("input[name="+ childrenCheckName +"]").parents("tr").addClass("grid_Selected");
					if(opts.checkAll){
						$(this).parents("th.check").find("> div").attr("class","checked-page");
					}
				}else{
					trcheckSiblings.each(function() {
						this.checked=false; 
					})
					$("input[name="+ childrenCheckName +"]").parents("tr").removeClass("grid_Selected");
					if(opts.checkAll){
						$(this).parents("th.check").find("> div").attr("class","check-page");
					}
				}
				$.fn.comboTableGrid.selectTrOp(tableData, _idMap.pluginId);
			});
		}
	};
	
	/**
	 * 设置checkBox的选择触发事件
	 */
	var _setGridCheckOp = function(tableData, _idMap, opts, trTag, handleMode){
		
		var checks = $('input[name="'+ _idMap.mainTbodyId +'_check"]');
		if(checks.length > 0){
			var flag = 0;
			for(var i = 0; i < checks.length; i++){
				if($(checks[i]).is(":checked")){
					flag++;
				}
			}
			
			var headCheckId = _idMap.headId+"_check";
			var headCheck = $("#"+headCheckId);
			if(flag == checks.length){
				document.getElementById(headCheckId).checked = true;
				if(opts.checkAll){
					$("#"+ headCheckId).parents("th.check").find("> div").attr("class","checked-page");
				}
			} else {
				document.getElementById(headCheckId).checked = false;
				if(opts.checkAll){
					$("#"+ headCheckId).parents("th.check").find("> div").attr("class","check-page");
				}
			}
			$.fn.comboTableGrid.selectTrOp(tableData, _idMap.pluginId, trTag);
		}
		
		//
		var selectRow = $("#" + _idMap.selectedRowId);
		_setGridTrSelected(_idMap, trTag, selectRow, handleMode);
		
	};
	
	/**
	 * 设置radio button的选择触发事件
	 */
	var _setGridRadioCheckOp = function(tableData, _idMap, opts, trTag, handleMode){
		var checks = $('input[name="'+ _idMap.mainTbodyId +'_radio"]');
		if(checks.length > 0){
			$.fn.comboTableGrid.selectTrOp(tableData, _idMap.pluginId, trTag);
		}
		//
		var selectRow = $("#" + _idMap.selectedRowId);
		_setGridTrSelected(_idMap, trTag, selectRow, handleMode);
	}
	
})();