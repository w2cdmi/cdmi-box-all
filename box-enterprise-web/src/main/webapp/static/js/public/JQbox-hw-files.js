﻿/**
 * 文件目录列举组件
 */
(function() {
	var isBoxSelect = false; //是否框选操作
	var isDrag = false;		//是否拖拽操作
	var isEditName = false;  //是否正在修改名称或创建文件夹
	var lastCheckId = null; //最后一次选择的项目id
	var isControl=true;//是否可以操作单元格
	$.fn.comboGrid = function(options){
		var parent = $(this);
		$.fn.comboGrid.defaults = {
			headData : {},			//数据列
			data : [],				//展示数据
			width : 0,				//宽度, 0为宽度"auto"
			height : 0,				//高度, 0为高度"auto"
			dataId : "",			//数据的id编号
			dragHandler : true,		//是否允许拖拽
			multiSelect : true,		//是否允许多选
			keyDelete : false,		//是否允许 Del 删除
			keyCtrlCXV : false,		//是否允许 Ctrl+C/Ctrl+X/Ctrl+V
			keyF2 : false,			//是否允许 F2 重命名
			rightMenu : true,		//是否使用右键菜单
			viewType : "list",		//初始显示方式， "list, thumbnail"
			style : "file-catalog"	//使用的样式"
		};

		//设置用户自定义初始化内容
		var opts = $.extend({},$.fn.comboGrid.defaults, options);
		_createGrid(parent, opts);
		return opts;
	};
	
	/**
	 * 获取表格的内部id表
	 */
	var getGridIdMap = function(id){
		var _idMap = {
				"pluginId" : id,							//组件id
				
				"mainUlId" : id + "_mainUl",				//容器ul的id
				"menuUlId" : id + "_menuUl",				//右键菜单容器id
				"selectedRowId" : id + "_selectedRow",		//记录当前选择item input的id
				"loadingId" : id + "_loading"				//表格数据loading图层
		};
		return _idMap;
	};
	
	/**
	 * 刷新数据
	 */
	$.fn.setGridData = function(data, opts){
		var parent = $(this);
		if(parent.get(0) != null){
			var _id = parent.get(0).id;
			var _idMap = getGridIdMap(_id);
			$("#"+_idMap.selectedRowId).val(""); //置空选中项
			$("#" + _idMap.mainUlId).find("li").remove(); //置空列表数据
			$("body").unbind(); //清除绑定的鼠标事件，由于是异步刷新，会导致多次执行绑定事件，异步刷新多少次就会执行多少次
			isEditName = false;  //刷新页面时重置状态
			$(".tooltip").remove();	//解决异步刷新文件列表后提示不消失的问题
			
			$("#" + _idMap.loadingId).show();
			_createGridItem($("#"+_idMap.mainUlId), opts, data, _idMap);
			
			//获取当前的选中项，并调用外部方法
			var allSelectData = $("#"+ _idMap.pluginId).getGridSelectedData(data, opts);
			$.fn.comboGrid.selectOp(allSelectData);	//以此判断当前是否显示操作按钮
		}
	};
	
	/**
	 * 对外单显示表格进度遮罩层方法
	 */
	$.fn.showGridLoading = function(){
		var parent = $(this);
		if(parent.get(0) != null){
			var _id = parent.get(0).id;
			var _idMap = getGridIdMap(_id);
			$("#" + _idMap.loadingId).show();
		}
	};
	
	/**
	 * 对外切换数据显示方式的方法
	 */
	$.fn.switchViewType = function(type,opts){
		var _id = $(this).get(0).id;
		var _idMap = getGridIdMap(_id);
		var mainUlId = _idMap.mainUlId;
		if(type == "list"){
			$("#"+ mainUlId).removeClass("thumbnail");
			$("#"+ mainUlId).addClass("list");
			
			if (opts && opts.isContainFilelabel){
				$("#"+ mainUlId).children("li").addClass("file-item");
				$("#"+ mainUlId).children("li").find("div.marks").show();
			}
			
			var len = $("#"+ mainUlId).find(">li").length;
			for(var i = 0; i < len; i++){
				var headInfo = opts.headData;
				var index = 0;
				for (var key in headInfo){
					var itemWidth = headInfo[key].width;
					var rowId = mainUlId +'_li'+ i;
					$("#"+ rowId).find("li").eq(index).css({"width":itemWidth,"display":""});
					index++;
				}
			}
		}else if(type == "thumbnail"){
			if (opts && opts.isContainFilelabel){
				$("#"+ mainUlId).children("li").removeClass("file-item");
				$("#"+ mainUlId).children("li").find("div.marks").hide();
			}
			
			$("#"+ mainUlId).removeClass("list");
			$("#"+ mainUlId).addClass("thumbnail");
			$("#"+ mainUlId).find("ul>li").css({"width":"auto","display":"none"});
			$("#"+ mainUlId).find("ul>li:first-child").css({"display":""});
		}
	};
	
	/**
	 * 对外单元格修改接口方法
	 */
	$.fn.comboGrid.setItemOp = function(rowData, colTag, colIndex){
		
	};
	
	/**
	 * 对外已选择数据的操作接口方法
	 */
	$.fn.comboGrid.selectOp = function(allSelectData){

	};
	
	/**
	 * 对外鼠标右键的操作接口方法
	 */
	$.fn.comboGrid.mouseRightOp = function(allSelectData){

	};
	
	/**
	 * 对外双击接口方法
	 */
	$.fn.comboGrid.dbTrOp = function(rowData){

	};
	
	/**
	 * 对外设置宽高的接口
	 */
	var _setGridResize = function(_idMap){

	};
	
	/**
	 * 对外拖拽文件移动/复制的接口
	 */
	$.fn.comboGrid.dragMove = function(targetId){

	};
	
	/**
	 * 对外Ctrl+C/Ctrl+X 移动/复制的接口
	 */
	$.fn.comboGrid.KEYMoveOrCopy = function(tag, itemParent, handleIdArray){

	};
	
	/**
	 * 对外拖拽文件删除的接口
	 */
	$.fn.comboGrid.dragDelete = function(){

	};
	
	/**
	 * 对外 KEY DELETE 文件删除的接口
	 */
	$.fn.comboGrid.keyDelete = function(){

	};
	
	/**
	 * 对外 KEY F2 文件重命名的接口
	 */
	$.fn.comboGrid.keyRename = function(){

	};

	/**
	 * 设置表格样式
	 */
	$.fn.setGridStyle = function(style){
		var parent = $(this);
		if(parent.get(0) != null){
			var _id = parent.get(0).id;
			parent.attr("class", style);
		}
	};
	
	/**
	 * 获取全部选中项数据
	 */
	$.fn.getGridSelectedData = function(ulData, opts){
		var parent = $(this);
		var _id = parent.get(0).id;
		var _idMap = getGridIdMap(_id);
		var rowDataArr = new Array();
		var checkData = new Array();
		checkData = $("#" + _idMap.selectedRowId).val()=="" ? [] : $("#" + _idMap.selectedRowId).val().split("|");
		
		for(var i = 0; i < checkData.length; i++){
			for(var j = 0; j < ulData.length; j++){
				if(ulData[j][opts.dataId] == checkData[i]){
					rowDataArr.push(ulData[j]);
				}
			}
		}
		return rowDataArr; 
	};
	
	/**
	 * 获取全部选中项ID集合
	 */
	$.fn.getGridSelectedId = function(){
		var parent = $(this);
		var _id = parent.get(0).id;

		var _idMap = getGridIdMap(_id);
		var checkData = new Array();
		checkData = $("#" + _idMap.selectedRowId).val()=="" ? [] : $("#" + _idMap.selectedRowId).val().split("|");
		
		return checkData; 
	};
	
	/**
	 * 取消全部选中项
	 */
	$.fn.removeGridSelected = function(){
		var parent = $(this);
		if(parent.get(0) != null){
			var _id = parent.get(0).id;
			var _idMap = getGridIdMap(_id);
			$("#" + _idMap.mainUlId).find(".selected").removeClass("selected");
			$("#" + _idMap.selectedRowId).val("");
		} else {
			return;
		}
	};

	/**
	 * 创建文件夹
	 */
	$.fn.createFolder = function(ownerId, parentId, callBackValidate, submitHandler, val){
		if($("#renameCon").get(0) || $("#createFolderInput").get(0)){
			return;
		}
		
		var parent = $(this);
		var _id = parent.get(0).id;
		var _idMap = getGridIdMap(_id);
		
		var html = "<li id=\"createFolderCon\" class=\"rowli clearfix selected\">"+
					"<div class=\"file-icon fileImg-folder\"></div>"+
					"<ul class=\"clearfix\"><li><span class=\"create-folder-con\">" +
					"<form id=\"createNodeForm\">"+
					"<input type=\"hidden\" id=\"ownerId\" name=\"ownerId\" value="+ ownerId +" />"+
					"<input type=\"hidden\" id=\"parentId\" name=\"parentId\" value="+ parentId +" />"+
					"<input class=\"createfolder-input\" id=\"createFolderInput\" name=\"name\" type=\"text\" value=\""+ val +"\" maxlength=\"246\" />" +
					"<span class=\"validate-con\"><div></div></span>"+
					"</form>"+
					"</span></li><li></li></ul>"+
					"</li>";
		$("#"+ _idMap.mainUlId).prepend(html);

		$("#createFolderInput")
			.unbind()
			.focus(function(){
				isEditName = true;
				//启用文本选择
				document.body.onselectstart=function(){return true;}
				$("body").removeClass("disable-select");
				//选中文本框中的文本
				$(this).select();
			})
			.blur(function(){
				isEditName = false;
				//禁用文本选择
				document.body.onselectstart=function(){return false;}
				$("body").addClass("disable-select");
				//失去焦点时提交
				submitHandler("createFolderCon", "createFolderInput");
			})
			.keyup(function(){
				if (event.keyCode != 13) {
					callBackValidate("createFolderInput");
				}
			})
			.keydown(function(event) {
				if (event.keyCode == 13) {
					$(this).blur();
					window.event.cancelBubble = true;//停止冒泡
					window.event.returnValue = false;//阻止事件的默认行为
				}
			})
			.focus();
	};	
	
	/**
	 * 设置是否可操作
	 */
	$.fn.setControl = function(control){
		isControl=control;
	};
	
	/**
	 * 重命名
	 */
	$.fn.renameNode = function(ulData, opts, callBackValidate, submitHandler){
		if($("#renameCon").get(0) || $("#createFolderInput").get(0)){
			return;
		}
		var parent = $(this);
		var _id = parent.get(0).id;
		var _idMap = getGridIdMap(_id);
		
		var allSelectData = $("#"+ _idMap.pluginId).getGridSelectedData(ulData, opts);
		var _nameId = $("#" + _idMap.selectedRowId).val();
		var html= "<span id=\"renameCon\" class=\"rename-con\">" +
				"<form id=\"renameNodeForm\">"+
				"<input type=\"hidden\" id=\"ownerId\" name=\"ownerId\" value="+ allSelectData[0].ownedBy +" />"+
				"<input type=\"hidden\" id=\"parentId\" name=\"parentId\" value="+ allSelectData[0].parentId +" />"+
				"<input type=\"hidden\" id=\"nodeId\" name=\"nodeId\" value="+ _nameId +" />"+
				"<input class=\"rename-input\" id=\"renameInput\" name=\"name\" type=\"text\" value=\"\" maxlength=\"246\" />" +
				"<span class=\"validate-con\"><div></div></span>"+
				"</form>"+
				"</span>";
		$("#"+ _idMap.pluginId).find("#"+ _nameId).hide().before(html);

		//文本框聚焦、选中并绑定事件
		$("#renameInput")
			.val($("#"+ _nameId).text())
			.unbind()
			.focus(function(){
				isEditName = true;
				//启用文本选择
				document.body.onselectstart=function(){return true;}
				$("body").removeClass("disable-select");
				
				//选中文本框中的文本
				if(allSelectData[0].type == 0){//文件夹
					$(this).select();
				}else{//文件
					if(document.selection){//IE
						$(this).select();
						var rangeObj = document.selection.createRange();
						var txt = rangeObj.text;
						var preIdx = 0;
						var nextIdx = (txt.lastIndexOf(".") == -1) ? txt.length : txt.lastIndexOf(".");
						rangeObj.collapse(); 
						rangeObj.moveStart("character",preIdx);
						rangeObj.moveEnd("character",nextIdx); 
						rangeObj.select();
					}else{//非IE
						var txt = $(this).val().substring(0,9999);
						var preIdx = 0;
						var nextIdx = (txt.lastIndexOf(".") == -1) ? txt.length : txt.lastIndexOf(".");
						$(this).get(0).setSelectionRange(preIdx, nextIdx);
						return;
					}
				}
			})
			.blur(function(){
				isEditName = false;
				//禁用文本选择
				document.body.onselectstart=function(){return false;}
				$("body").addClass("disable-select");
				//失去焦点时提交
				submitHandler(_nameId, "renameCon", "renameInput");
			})
			.keyup(function(){
				if (event.keyCode != 13) {
					callBackValidate(_nameId, "renameInput");
				}
			})
			.keydown(function(event) {
				if (event.keyCode == 13) {
					$(this).blur();
					window.event.cancelBubble = true;//停止冒泡
					window.event.returnValue = false;//阻止事件的默认行为
				}
			})
			.focus();
	}

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
		var _id = parent.get(0).id;
		if(_id == null){
			return;
		}
		
		parent.addClass(opts.style);
		if(opts.width != 0){
			var oWidth = opts.width + "px";
			parent.css({"width": oWidth});
		}
		
		var _idMap = getGridIdMap(_id);
		var selectRowCatch = $('<input id="'+_idMap.selectedRowId+'" type="hidden" value="" />');
		var gridMainHeight;
		if(opts.height <= 0){
			gridMainHeight = "auto";
		}else{
			gridMainHeight = opts.height +"px";
		}
		var gridMain = $('<ul id="'+ _idMap.mainUlId +'" class="'+ opts.viewType +' clearfix" style="height:'+ gridMainHeight +'"></ul>');
		var gridLoading = $('<div id="'+ _idMap.loadingId +'" class="grid_loading"><p>loading</p></div>');
		parent.append(selectRowCatch);
		parent.append(gridMain);
		parent.append(gridLoading);
		//对齐遮罩层坐标
		var lTop = 0 - gridMain.height();
		var lHeight = gridMain.height();
		if(lHeight==0){
			lHeight = 200;
		}
		gridLoading.css({
			"left" : 0,
			"top" : lTop + "px",
			"height" : lHeight + "px"
		});
		_createGridItem($("#"+_idMap.mainUlId), opts, opts.data, _idMap);
		
		_delCookie("saveClipboardTempData");//清除保留的剪切、复制的临时数据

	}
	
	/**
	 * 列举数据
	 */
	var _createGridItem = function(parent, opts, ulData, _idMap){
		if(ulData.length == 0){
			$("#"+ _idMap.pluginId).hide();
			$("#"+ _idMap.pluginId +"Null").show();
			$("#" + _idMap.loadingId).hide();
			return;
		}else{
			$("#"+ _idMap.pluginId).show();
			$("#"+ _idMap.pluginId +"Null").hide();
		}
		var headInfo = opts.headData;
		var data = ulData;
		
		for(var i = 0; i < data.length; i++){
			var rowId = _idMap.mainUlId +'_li'+ i;
			var rowType = "folder";
			if (data[i].shareStatus == 0||data[i].shareStatus == undefined){
				if(data[i].type == 0 || data[i].type == 5){
					rowType = "folder";
				}else if(data[i].type == -2){
					rowType = "folder-disk";
				}else if(data[i].type == -3){
					rowType = "folder-computer";
				}else{
					rowType = _getStandardType(data[i].name);
				}
			}else if(data[i].shareStatus == 1){
				if(data[i].type == 0){
					rowType = "foldershare";
				}else if(data[i].type == -2){
					rowType = "foldershare-disk";
				}else if(data[i].type == -3){
					rowType = "foldershare-computer";
				}else{
					rowType = _getStandardType(data[i].name);
				}
			}
				
			var rowTag = $('<li id="'+ rowId +'" class="rowli clearfix"></li>');
			var colTagCon = $('<ul class="clearfix"></ul>');
			var rowIcon = null;
			// 缩略图显示
			if("img" == rowType){
				var urlThumbnail = data[i].thumbnailUrlList;
				var smallThumb = data[i].thumbnailUrlList[0].thumbnailUrl;
				var bigThumb = data[i].thumbnailUrlList[1].thumbnailUrl;
				rowIcon = $('<div class="file-icon fileImg-'+rowType+'">'+
							'<div class="img-thumbnail" style="background-image:url(\''+bigThumb+'\')"></div>'+
							'<div class="img-list" style="background-image:url(\''+smallThumb+'\')"></div>'+
							'<div class="popover"><div class="arrow"></div><div class="popover-content"><table><tr><td><img src=\''+bigThumb+'\' /></td></tr></table></div></div>'+
							'</div>');
			}else{
				rowIcon = $('<div id="file-icon_'+data[i].id+'" class="file-icon fileImg-'+ rowType +'"></div>');
			}
			//end
			
			var idData = (data[i][opts.dataId] == "" ? "" : data[i][opts.dataId]);
			rowTag.append(rowIcon);
			rowTag.append(colTagCon);
			parent.append(rowTag);
			
			var index = 0;
			for (var key in headInfo){
				var itemWidth = headInfo[key].width;
				if(listViewType == "thumbnail"){
					itemWidth = "auto";
				}
				var itemData = (data[i][key] == null ? "" : data[i][key]);
				var colTag;
				
				if(key == 'name'){
					colTag = $('<li style="width:'+ itemWidth +'" class ="file-name"><span class="txtSpan">'+ itemData +'</span></li>');
				} else{
					colTag = $('<li style="width:'+ itemWidth +'"><span class="txtSpan">'+ itemData +'</span></li>');
				}
				colTagCon.append(colTag);
				
				$.fn.comboGrid.setItemOp(data[i], colTag, index);
				
				if(index == 0){
					var dataIdTag = $('<input class="dataIdInput" type="hidden" value="'+ idData +'" />');
					colTag.append(dataIdTag);
				}
				
				index++;
			}
			
			_setGridRowHover(rowTag);
			
			//点击行选中正行列表
			rowTag.click(function(){
				if(isControl == false) return; //如果不可编辑则不执行
				if(isEditName == true) return; //正在重命名节点或创建文件夹时不执行操作
				var ev = arguments[0] || window.event;
				var srcElement = ev.srcElement || ev.target;
				var targetName = srcElement.tagName.toLowerCase();
				if(targetName == "a" || targetName == "i"){
					return;
				} 
				_setGridCheckOp(data, opts, _idMap, $(this), ev);
			});
			
			//双击事件
			rowTag.dblclick(function(){
				if(isControl == false) return; //如果不可编辑则不执行
				if(isEditName == true) return; //正在重命名节点或创建文件夹时不执行操作
				var rowTagId = $(this).get(0).id;
				var idArry = rowTagId.split("_mainUl_li");
				var rowIndex = idArry[(idArry.length - 1)];
				var rowData = data[rowIndex];
				$.fn.comboGrid.dbTrOp(rowData);
			})
		}
		
		//绑定鼠标事件
		_setMouseEvt(data, opts, _idMap, data.length);
		//绑定键盘事件
		_setKeyboardEvt(data, opts, _idMap, data.length);
		
		//获取当前的选中项，并调用外部方法
		var allSelectData = $("#"+ _idMap.pluginId).getGridSelectedData(data, opts);
		$.fn.comboGrid.selectOp(allSelectData);	//以此判断当前是否显示操作按钮
		
		$("#" + _idMap.loadingId).hide();
		_setGridResize(_idMap);
		
		//禁止文本选择
		document.body.onselectstart=function(){return false;};
		$("body").addClass("disable-select");
		
		$("input:text, input:password, textarea").focus(function(){
			document.body.onselectstart=function(){return true;};
			$("body").removeClass("disable-select");
		});
		$("input:text, input:password, textarea").blur(function(){
			document.body.onselectstart=function(){return false;};
			$("body").addClass("disable-select");
		})
	};
	
	/**
	 * 设置Tr的mouseOver样式
	 */
	var _setGridRowHover = function(rowObj){
		if(rowObj.get(0) != null){
			rowObj.hover(
				function () {
					$(this).addClass("over");
					
					//设置tip属性
					$(".rowli span, .rowli i").tooltip({ container:"body", placement:"top", delay: { show: 200, hide: 0 }, animation: false });
					$(".rowli a").tooltip({ container:"body", placement:"top", delay: { show: 600, hide: 0 }, animation: false });
					
					//设置列表的图片缩略图预览 【调用bootstrap内部封装方法popover】
					var popoverPlacement = ($(this).offset().left > 100)?"left show":"top show";
					$(this).find(".popover").addClass(popoverPlacement);
				},
				function () {
					$(this).removeClass("over");
					$(this).find(".popover").removeClass('show left top');
				}
			);
		}
	};
	
	/**
	 * 获取最后点击项的id，并赋值给全局变量lastCheckId
	 * shift+鼠标 进行多选时需要获取最后点击的项目
	 */
	var _getLastSelectItem = function(_idMap){
		var checkData = new Array();
		checkData = $("#" + _idMap.selectedRowId).val()=="" ? [] : $("#" + _idMap.selectedRowId).val().split("|");
		lastCheckId = checkData.length > 0 ? checkData[checkData.length-1]:null;
	}
	
	/**
	 * 设置选择触发事件
	 */
	var _setGridCheckOp = function(ulData, opts, _idMap, rowTag, ev){
		var rawTagObj = rowTag.find("> ul > li:first-child input.dataIdInput").val();
		var checkData = new Array();
		checkData = $("#" + _idMap.selectedRowId).val()=="" ? [] : $("#" + _idMap.selectedRowId).val().split("|");
		
		if(isDrag){ return; } //当拖拽文件时禁止选择操作
		
		if(ev.ctrlKey){
			if(opts.multiSelect == false){
				return;
			}
			//按着ctrl键多选
			var flag = false;
			for(var j = 0; j<checkData.length; j++){
				if(rawTagObj == checkData[j]){
					flag = true; 
				}
			}
			if(!flag){//当前点击的项未被选中时，直接选中，并把id值赋给selectedRowId
				rowTag.addClass("selected");
				var selVal = $("#" + _idMap.selectedRowId).val();
				if(selVal==""){
					selVal = rawTagObj;
				}else{
					selVal += "|" + rawTagObj;
				}
				$("#" + _idMap.selectedRowId).val(selVal);
				
			}else{//当前点击的项已经被选中
				rowTag.removeClass("selected");
				for(var m = 0; m<checkData.length; m++){
					if(rawTagObj == checkData[m]){
						checkData.splice(m, 1); //在数组中删除当前项
					}
				}
				if(checkData.length>0){//如果数组不为空时，重新把数组拼接成字符串赋值给selectedRowId
					var selVal="";
					for(var n = 0; n<checkData.length; n++){
						if(n==0){
							selVal += checkData[n];
						}else{
							selVal += "|" + checkData[n];
						}
						$("#" + _idMap.selectedRowId).val(selVal);
					}
				}else{//如果数组为空时直接清空selectedRowId
					$("#" + _idMap.selectedRowId).val("");
				}
			}
			_getLastSelectItem(_idMap);
		}else if(ev.shiftKey){
			if(opts.multiSelect == false){
				return;
			}
			if(lastCheckId == null){
				rowTag.addClass("selected");
				$("#" + _idMap.selectedRowId).val(rawTagObj);
				lastCheckId = rawTagObj;
			}else{
				var firstNum,lastNum;
				for(var i=0;i<$("#"+ _idMap.mainUlId).find(">li").length;i++){
					if(lastCheckId == $("#"+ _idMap.mainUlId +"_li"+ i).find("> ul > li:first-child input.dataIdInput").val()){
						firstNum = i;
					}
				}
				var stringArr = rowTag.attr("id").split("_li");
				lastNum = stringArr[1];
				
				$("#"+ _idMap.pluginId).removeGridSelected();
				var FNum = firstNum > lastNum ? lastNum:firstNum;
				var LNum = firstNum > lastNum ? firstNum:lastNum;
				var selVal="";
				for(var i = FNum; i< parseInt(LNum)+1; i++){
					var item = $("#"+_idMap.mainUlId +"_li"+ i);
					item.addClass("selected");
					if(i==FNum){
						selVal += item.find("> ul > li:first-child input.dataIdInput").val();
					}else{
						selVal += "|" + item.find("> ul > li:first-child input.dataIdInput").val();
					}
					$("#" + _idMap.selectedRowId).val(selVal);
				}
			}
			
		}else{
			if(checkData.length<2){
				//当前已选中项小于2项时，判断当前点击项是否等于已选中项，是则取消选中
				if( rawTagObj == checkData[0]){
					$("#" + _idMap.selectedRowId).val("");
					rowTag.removeClass("selected");
				}else{
					rowTag.parents("ul").find(".selected").removeClass("selected");
					rowTag.addClass("selected");
					$("#" + _idMap.selectedRowId).val(rawTagObj);
				}
			}else{
				if(isBoxSelect == true){
					return;
				}
				//当前已选中项大于2项时，始终选中当前点击的项
				rowTag.parents("ul").find(".selected").removeClass("selected");
				rowTag.addClass("selected");
				$("#" + _idMap.selectedRowId).val(rawTagObj);
			}
			_getLastSelectItem(_idMap);
		}
		
		//获取当前的选中项，并调用外部方法
		var allSelectData = $("#"+ _idMap.pluginId).getGridSelectedData(ulData, opts);
		$.fn.comboGrid.selectOp(allSelectData);
		
	};
	
	/**
	 * 鼠标事件
	 */
	var _setMouseEvt = function(ulData, opts, _idMap, len){
			var boxSelectObj = $('<div id="boxSelectDiv" class="boxSelectStyle"></div>');
			var dragDiv = $('<div id="dragDiv" class="dragDivStyle"><span class="badge badge-info"></span></div>');
			var d_x = 0;
			var d_y = 0;
			var singleDragId = ""; //定义单个文件拖拽时，获取id
			$("body").mousedown(function(){
				document.body.oncontextmenu=function(){return false;}     //屏蔽默认右键菜单
				if(isEditName == true) return; //正在重命名节点或创建文件夹时不执行操作
				isBoxSelect = false; 
				isDrag = false;
				var ev = arguments[0] || window.event;
				var srcElement = ev.srcElement || ev.target;
				var targetName = srcElement.tagName.toLowerCase();
				
				//鼠标的坐标
				d_x = ev.pageX;
				d_y = ev.pageY;
				
				if(ev.button == 2){
					//删除右键菜单
					if($("#"+ _idMap.menuUlId)){
						$("#"+ _idMap.menuUlId).remove();
					}
					var mousePos = [d_x, d_y];
					if($("#maskLevel").css("display") =="block"){ //如果有弹出框时，不执行右键操作
						return;
					}
					if(targetName != "input" && targetName != "textarea" && targetName != "button"){
						_setGridMouseRight(ulData, opts, _idMap, srcElement ,mousePos);
					}
				}else{
					//删除右键菜单
					if($("#"+ _idMap.menuUlId).get(0)){
						if(srcElement != $("#"+ _idMap.menuUlId).get(0) && $(srcElement).parents("ul").attr("id") !=  _idMap.menuUlId){
							$("#"+ _idMap.menuUlId).remove();
						}
					}
					
					var checkData = new Array();
					checkData = $("#" + _idMap.selectedRowId).val()=="" ? [] : $("#" + _idMap.selectedRowId).val().split("|");
					var targetLi = $(srcElement).hasClass("rowli") ? $(srcElement) : $(srcElement).parents("li.rowli");
					var targetA = targetLi.find("> ul > li:first-child > a");
					var targetIcon = targetLi.find("> div.file-icon");
					var targetIconThumb = targetLi.find("div.img-thumbnail");
					var targetIconList = targetLi.find("div.img-list");
					var checkDataStr = "|"+ $("#" + _idMap.selectedRowId).val() +"|";
					var srcElementIdSt = "|"+ targetLi.find("input.dataIdInput").val() +"|";
					//在非选中项的标题或图标上直接拖拽当前文件或文件夹
					if((srcElement == targetA.get(0)
							|| srcElement == targetIcon.get(0)
							|| srcElement == targetIconThumb.get(0)
							|| srcElement == targetIconList.get(0))
							&& checkDataStr.indexOf(srcElementIdSt) == -1){
						if(!ev.ctrlKey){
							singleDragId = targetLi.find("input.dataIdInput").val();
							dragDiv.find("span").text("1");
							if(!$("#dragDiv").get(0)){ $("body").append(dragDiv); }//插入拖拽层
							dragDiv.css({"display":"none", "top":d_x + 5, "left":d_y + 5});
							return;
						}
					}
					//在已选中标签上执行拖拽选中的项目
					for(var i = 0; i<checkData.length; i++){
						if(targetLi.find("input.dataIdInput").val() == checkData[i] && targetName != "input"){
							dragDiv.find("span").text(checkData.length);
							if(!$("#dragDiv").get(0)){ $("body").append(dragDiv); }//插入拖拽层
							dragDiv.css({"display":"none", "top":d_x + 5, "left":d_y + 5});
							return;
						}
					}
					
					//单击非列表、非按钮处时，取消选择
					if(srcElement == $("#"+ _idMap.pluginId).get(0)
					|| srcElement == $("#"+ _idMap.pluginId).find(" > ul").get(0)
					|| srcElement == $(".body").get(0)
					|| srcElement == $(".body-con").get(0)
					|| srcElement == $(".body-con > div").get(0)
					|| srcElement == $(".public-bar-con").get(0)
					|| srcElement == $(".public-bar").get(0)
					|| srcElement == $(".list-handler").get(0)
					|| srcElement == $(".breadcrumb").get(0)
					|| srcElement == $(".breadcrumb-con").get(0)
					|| srcElement == $(".breadcrumb-con > ul").get(0)
					|| srcElement == $(".footer").get(0)
					|| srcElement == $(".footer-con").get(0)
					|| srcElement == $(".footer-con > p").get(0)
					|| srcElement == $(".page").get(0)){
						if(!(ev.ctrlKey || ev.shiftKey)){
							$("#"+ _idMap.pluginId).removeGridSelected();
							//获取当前的选中项，并调用外部方法
							var allSelectData = $("#"+ _idMap.pluginId).getGridSelectedData(ulData, opts);
							$.fn.comboGrid.selectOp(allSelectData);	
						}
					}
					//只有在非表单和按钮、非上传控件上可以拖动框选
					if(targetName != "input" && targetName != "textarea" && targetName != "a" && targetName != "button" && $(srcElement).parents(".modal").attr("id")!= "uploadModal"){
						if(!$("#boxSelectDiv").get(0)){ $("body").append(boxSelectObj); }
						boxSelectObj.css({"display":"none","width":0,"height":0});
					}
				}
				
			});
			
			$("body").mouseup(function(){
				boxSelectObj.fadeTo(0.3,0); //隐藏选择框
				boxSelectObj.remove();
				dragDiv.remove();
				singleDragId = "";
				if(isBoxSelect){
					//获取当前框选的选中项，并调用外部方法
					var allSelectData = $("#"+ _idMap.pluginId).getGridSelectedData(ulData, opts);
					$.fn.comboGrid.selectOp(allSelectData);	
					
					_getLastSelectItem(_idMap);
				}
				if(isDrag){
					$("#"+ _idMap.pluginId).find("> ul > li").removeClass("dragTag");//拖拽结束时，删除拖拽时的效果
					var ev = arguments[0] || window.event;
					var srcElement = ev.srcElement || ev.target;
					//获取当前框选的选中项id字符串
					var checkData = $("#" + _idMap.selectedRowId).val()=="" ? [] : $("#" + _idMap.selectedRowId).val().split("|");
					var isSameId = false;
					var targetLi = $(srcElement).hasClass("rowli") ? $(srcElement) : $(srcElement).parents("li.rowli");
					var targetLiId = targetLi.find("> ul > li:first-child input.dataIdInput").val();
					for(var n=0; n<checkData.length; n++){
						if(targetLiId == checkData[n]){
							isSameId = true;
							break;
						}
					}
					if(targetLi.get(0) && !isSameId){ //是否拖放在非选中的列表图标上
						var targetLiType;
						for(var j = 0; j < ulData.length; j++){
							if(ulData[j][opts.dataId] == targetLiId){
								targetLiType = ulData[j].type;
								break;
							}
						}
						if(targetLiType == 0 && checkData.length != 0){
							$.fn.comboGrid.dragMove(targetLiId);
						}
					}else if($(srcElement).attr("id") == "btnTrash" || $(srcElement).parent().attr("id") == "btnTrash"){
						$.fn.comboGrid.dragDelete();
						$("#btnTrash").addClass("btn-link");
					}
				}
			});

			top.$(document).mouseup(function(){ //兼容firefox
				boxSelectObj.fadeTo(0.3,0); //隐藏选择框
				boxSelectObj.remove();
				dragDiv.remove();
				singleDragId = "";
			});
			
			$("body").mousemove(function(){
				if($("#dragDiv").get(0)){
					var ev = arguments[0] || window.event;
					var srcElement = ev.srcElement || ev.target;
					var moveX =Math.abs(ev.pageX - d_x); 
					var moveY =Math.abs(ev.pageY - d_y); 
					if(( moveX > 5 || moveY > 5)&& opts.dragHandler == true){
						if(singleDragId != ""){
							//取消选中项的选中状态
							$("#"+ _idMap.pluginId).removeGridSelected();
							//获取当前的选中项，并调用外部方法
							var allSelectData = $("#"+ _idMap.pluginId).getGridSelectedData(ulData, opts);
							$.fn.comboGrid.selectOp(allSelectData);
							//选中拖拽目标
							$("#"+ singleDragId).parents(".rowli").addClass("selected");
							$("#" + _idMap.selectedRowId).val(singleDragId);
							//获取当前的选中项，并调用外部方法
							var allSelectData = $("#"+ _idMap.pluginId).getGridSelectedData(ulData, opts);
							$.fn.comboGrid.selectOp(allSelectData);
						}
						isDrag = true;
						dragDiv.css({"display":"block", "top":ev.clientY + 5, "left":ev.clientX + 5});
						
						//根据拖拽的列表目标，变换拖拽目标的风格效果
						var checkData = $("#" + _idMap.selectedRowId).val()=="" ? [] : $("#" + _idMap.selectedRowId).val().split("|");
						var isSameId = false;
						var targetLi = $(srcElement).hasClass("rowli") ? $(srcElement) : $(srcElement).parents("li.rowli");
						var targetLiId = targetLi.find("> ul > li:first-child input.dataIdInput").val();
						for(var n=0; n<checkData.length; n++){
							if(targetLiId == checkData[n]){
								isSameId = true;
								break;
							}
						}
						if(targetLi.get(0) && !isSameId){ //是否拖放在非选中的列表图标上
							var targetLiType;
							for(var j = 0; j < ulData.length; j++){
								if(ulData[j][opts.dataId] == targetLiId){
									targetLiType = ulData[j].type;
									break;
								}
							}
							if(targetLiType == 0){
								targetLi.addClass("dragTag");
							}
						}
						//当拖拽到回收站按钮时，效果明显变换
						if($(srcElement).attr("id") == "btnTrash" || $(srcElement).parent().attr("id") == "btnTrash"){
							$("#btnTrash").removeClass("btn-link");
						}else{
							$("#btnTrash").addClass("btn-link");
						}
					}
				}
				
				if($("#boxSelectDiv").get(0) && opts.multiSelect == true){
					//如果有弹出框时，不执行框选操作
					if($("#maskLevel").css("display") =="block" 
					|| $("#treeArea").css("display") =="block" 
					|| $("#favoriteArea").css("display") =="block"){ 
						return;
					}
					
					var ev = arguments[0] || window.event;
					//遮罩层的大小及位置
					var w = ev.pageX - d_x;
					var h = ev.pageY - d_y;
					var objW = w > 0?w:-w;
					var objH = h > 0?h:-h;
					var objL = w > 0?d_x:ev.pageX;
					var objT = h > 0?d_y:ev.pageY;
					boxSelectObj.css({"width":objW,"height":objH,"left":objL,"top":objT});
					
					if(objW > 5 && objH > 5){ //当大于5像素时才执行框选
						boxSelectObj.fadeTo(0, 0.3); //显示选择框
						isBoxSelect = true; 
						for(var idx = 0; idx < len; idx++){
							var itemTag = $("#"+ _idMap.mainUlId +'_li'+ idx);
							var rawTagObj = itemTag.find("> ul > li:first-child input.dataIdInput").val();
							var checkData = new Array();
							checkData = $("#" + _idMap.selectedRowId).val()=="" ? [] : $("#" + _idMap.selectedRowId).val().split("|");
							
							if(itemTag.get(0) == null){
								break;
							}else{
								var l = itemTag.offset().left;
								var t = itemTag.offset().top;
								if(objL < (l+ itemTag.outerWidth()) && objL > (l- objW) && objT < (t+ itemTag.outerHeight() ) && objT > (t-objH)){
									itemTag.addClass("selected");
									var selVal = $("#" + _idMap.selectedRowId).val();
									var selValStr = "|"+selVal+"|";
									var rawTagObjStr = "|"+rawTagObj+"|";
									if(selValStr.indexOf(rawTagObjStr) == -1){ //避免循环加入数据，在此做一个判断，如果数据已经有了就直接跳过
										if(selVal==""){
											selVal = rawTagObj;
										}else{
											selVal += "|" + rawTagObj;
										}
										$("#" + _idMap.selectedRowId).val(selVal);
									}
								}else{
									itemTag.removeClass("selected");
									for(var m = 0; m<checkData.length; m++){
										if(rawTagObj == checkData[m]){
											checkData.splice(m, 1); //在数组中删除当前项
										}
									}
									if(checkData.length>0){//如果数组不为空时，重新把数组拼接成字符串赋值给selectedRowId
										var selVal="";
										for(var n = 0; n<checkData.length; n++){
											if(n==0){
												selVal += checkData[n];
											}else{
												selVal += "|" + checkData[n];
											}
											$("#" + _idMap.selectedRowId).val(selVal);
										}
									}else{//如果数组为空时直接清空selectedRowId
										$("#" + _idMap.selectedRowId).val("");
									}
								}
							}
						}//for end
					}
				}
			})
	};
	
	/**
	 * 绑定键盘事件
	 */
	var _setKeyboardEvt = function(ulData, opts, _idMap, len){
		document.onkeydown=function(){
			if(isEditName == true) return; //正在重命名节点或创建文件夹时不执行操作
			var ev = arguments[0] || window.event;
			if(ev.ctrlKey && ev.keyCode==65){
				
				if(opts.multiSelect == false){
					return;
				}
				if($("#maskLevel").css("display") =="block"){ //如果有弹出框时，不执行全选操作
					return;
				}
					
				var selVal="";
				var checkData = new Array();
				checkData = $("#" + _idMap.selectedRowId).val()=="" ? [] : $("#" + _idMap.selectedRowId).val().split("|");
				
				//如果已经全选所有项，就取消所有全选
				if(checkData.length != ulData.length){
					for(var i = 0; i<ulData.length; i++){
						var rowId = _idMap.mainUlId +'_li'+ i;
						var rowTag = $("#"+ rowId);
						var rawTagObj = rowTag.find("> ul > li:first-child input.dataIdInput").val();
						rowTag.addClass("selected");
						if(i==0){
							selVal += rawTagObj;
						}else{
							selVal += "|" + rawTagObj;
						}
						$("#" + _idMap.selectedRowId).val(selVal);
					}
				}else{
					for(var i = 0; i<ulData.length; i++){
						var rowId = _idMap.mainUlId +'_li'+ i;
						var rowTag = $("#"+ rowId);
						rowTag.removeClass("selected");
					}
					$("#" + _idMap.selectedRowId).val("");
				}
				return false;//兼容firefox 禁用文本选择
			}
			
			// Delete,Del 删除数据
			if(ev.keyCode==46 || ev.keyCode==110){
				if(opts.keyDelete == false){
					return;
				}
				if($("#maskLevel").css("display") =="block"){ //如果有弹出框时，不执行全选操作
					return;
				}
				return $.fn.comboGrid.keyDelete();
			}
			
			//ctrl+C 复制数据
			if(ev.ctrlKey && ev.keyCode==67){
				if(opts.keyCtrlCXV == false){
					return;
				}
				if($("#maskLevel").css("display") =="block"){ //如果有弹出框时，不执行全选操作
					return;
				}
				var allSelectData = $("#"+ _idMap.pluginId).getGridSelectedData(ulData, opts);
				return ($("#" +_idMap.pluginId).getGridSelectedId()).length > 0 ? 
						_setCookie("saveClipboardTempData","copyByKey&&"+ allSelectData[0].parentId +"&&"+$("#" + _idMap.selectedRowId).val()) : false;
			}
			
			//ctrl+X 剪切数据
			if(ev.ctrlKey && ev.keyCode==88){
				if(opts.keyCtrlCXV == false){
					return;
				}
				if($("#maskLevel").css("display") =="block"){ //如果有弹出框时，不执行全选操作
					return;
				}
				var allSelectData = $("#"+ _idMap.pluginId).getGridSelectedData(ulData, opts);
				return ($("#" +_idMap.pluginId).getGridSelectedId()).length > 0 ? 
						_setCookie("saveClipboardTempData","moveByKey&&"+ allSelectData[0].parentId +"&&"+$("#" + _idMap.selectedRowId).val()) : false;
			}
			
			//ctrl+V 粘贴数据
			if(ev.ctrlKey && ev.keyCode==86){
				if(opts.keyCtrlCXV == false){
					return;
				}
				if($("#maskLevel").css("display") =="block"){ //如果有弹出框时，不执行全选操作
					return;
				}
				var _cookie = _getCookie("saveClipboardTempData");
				if(_cookie == null){
					return;
				}
				var dataArr = _cookie.split("&&");
				var tag = dataArr[0];
				var itemParent = dataArr[1];
				var handleIdArray = dataArr[2].split("|");
				$.fn.comboGrid.KEYMoveOrCopy(tag, itemParent, handleIdArray);
				
			}
		}
		document.onkeyup=function(){
			var ev = arguments[0] || window.event;
			//F2 重命名
			if(ev.keyCode==113){
				if($("#maskLevel").css("display") =="block"){ //如果有弹出框时，不执行
					return;
				}
				var allSelectData = $("#"+ _idMap.pluginId).getGridSelectedData(ulData, opts);
				if(allSelectData.length == 1 && opts.keyF2 == true){
					$.fn.comboGrid.keyRename();
				}
			}
			//获取当前的选中项，并调用外部方法 
			var allSelectData = $("#"+ _idMap.pluginId).getGridSelectedData(ulData, opts);
			$.fn.comboGrid.selectOp(allSelectData);
			
			_getLastSelectItem(_idMap);
		}
	};
	
	/**
	 * 右键菜单数据行
	 */
	$.fn.comboGrid.initGridMenu = function(){
		
	};
	
	/**
	 * 右键菜单的操作
	 */
	$.fn.gridMenuItemOp = function(btnType){
		
	};

	/**
	 * 鼠标在数据列上点击右键
	 */
	var _setGridMouseRight = function(ulData, opts, _idMap, target, mousePos){
		var rawTagObj = $(target).hasClass("rowli") ? $(target).find("> ul > li:first-child input.dataIdInput").val() : $(target).parents(".rowli").find("> ul > li:first-child input.dataIdInput").val();
		var flag = false;
		var checkData = new Array();
		checkData = $("#" + _idMap.selectedRowId).val()=="" ? [] : $("#" + _idMap.selectedRowId).val().split("|");
		
		for(var j = 0; j<checkData.length; j++){
			if(rawTagObj == checkData[j]){
				flag = true;
				break;
			}
		}
		
		if( flag == false){
			$("#"+ _idMap.pluginId).find(".selected").removeClass("selected");
			if($(target).hasClass("rowli")){
				$(target).addClass("selected");
			}else{
				$(target).parents(".rowli").addClass("selected");
			}
			$("#" + _idMap.selectedRowId).val(rawTagObj);
		}
		
		//获取当前的选中项，并调用外部方法
		var allSelectData = $("#"+ _idMap.pluginId).getGridSelectedData(ulData, opts);
		$.fn.comboGrid.selectOp(allSelectData);	
		
		//创建右键菜单
		if(opts.rightMenu == false){
			return false;
		}
		var menuUl = $('<ul id="'+ _idMap.menuUlId +'" class="dropdown-menu right-menu"></ul>');
		var menuData = $.fn.comboGrid.initGridMenu();
		var index=0;
		for(var key in menuData){ index++; }
		if(menuData.length < 1 || menuData == null || menuData == "" || menuData == "undefind" || index == 0){
			return;
		}
		
		menuUl.css({"display":"block", "left": mousePos[0], "top": mousePos[1]});
		$("body").append(menuUl);
		for(var key in menuData){
			var menuLi = $('<li></li>');
			var menuItem = $('<a id="menuItem_'+ key +'"><i class="'+ menuData[key].className +'"></i> '+ menuData[key].title +'</a>');
			menuLi.append(menuItem);
			menuUl.append(menuLi);
			
			if(menuData[key].divider == true){
				var menuDividerLi = $('<li class="divider"></li>');
				menuUl.append(menuDividerLi);
			}
			
			menuItem.click(function(){
			    	var menuItemId = $(this).get(0).id;
				var idArry = menuItemId.split("menuItem_");
				var menuKey = idArry[(idArry.length - 1)];
				
				$.fn.gridMenuItemOp(menuKey);
				menuUl.remove();
			});
		}
		var menuHeight = menuUl.outerHeight();
		var menuWidth =  menuUl.outerWidth();
		var scrollT = $(window).scrollTop();
		var scrollL = $(window).scrollLeft();
		var mouseX =  mousePos[0];
		var mouseY =  mousePos[1];
		var menuL = mouseX;
		var menuT = mouseY;

		if((menuHeight + mouseY) > $(window).height() + scrollT){
			menuT = $(window).height() + scrollT - menuHeight - 10;
		}
		
		if((menuWidth + mouseX) > $(window).width()){
			menuL = mouseX - menuWidth;
		}
		if(menuHeight == 0){
			menuUl.remove();
		}else{
			menuUl.css({
				"top" : menuT + "px",
				"left" : menuL + "px"
			});
		}
	};
	
	/**
	 * 增加、删除、获取cookie
	 */
	function _setCookie(name,value) {
	    var exp  = new Date("December 31, 9998");
	    document.cookie = name + "="+ escape (value) + ";expires=" + exp.toGMTString();
	}

	function _getCookie(name){
	    var arr = document.cookie.match(new RegExp("(^| )"+name+"=([^;]*)(;|$)"));
	    if(arr != null) {
	    	return unescape(arr[2]);
	    }
	    return null;
	} 

	function _delCookie(name){
	    var exp = new Date();
	    exp.setTime(exp.getTime() - 1);
	    var cval=getCookie(name);
	    if(cval!=null) document.cookie= name + "="+cval+";expires="+exp.toGMTString();
	}
	
})();