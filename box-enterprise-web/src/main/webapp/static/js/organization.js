var ctx;
var token;
function closeDiv(){
		$('.bgPop,.pop').hide();
	}
	
	
	var deviceTypeSetting = {  
		    view: {  
		        dblClickExpand: false,
		        chkDisabled:false
		    },  
		    data: {  
		        simpleData: {  
		            enable: true  
		        }  
		    },  
		    callback: {  
//		        onClick: deviceTypeOnClick,
		        onCheck:checkchange
		        
		    },
		    check : {
		    	autoCheckTrigger : false,
		    	chkboxType : {"Y": "ps", "N": "ps"},
		    	chkStyle : "checkbox",
		    	enable : true,
		    	nocheckInherit : false,
		    	chkDisabledInherit : true,
		    	radioType : "level"
		    	},
		    async:{
		    	 enable: true,
                 url:ctx+'/teamspace/member/showDepAndUsers',
                 autoParam:["id"],
                 otherParam:{"token":function(){
                	 return token;
                 }},
                 dataFilter: filter
                
		    } 
		};  
	
	function filter(treeId, parentNode, childNodes) {
        if (!childNodes) return null;
        for(var i = 0;i<childNodes.length;i++){
        	if(childNodes[i].type=="user"){
        		childNodes[i].id +="user";
        	}
        	if(childNodes[i].alias){
        		childNodes[i].name += "("+childNodes[i].alias+")";
        	}
        }
        var zTree = $.fn.zTree.getZTreeObj("menuTree");
        if(childNodes[0]){
        	var pNode = zTree.getNodeByParam("id",childNodes[0].pId,null);
        	if(pNode.type=="department" && pNode.checked==true){
        		for (var i=0, l=childNodes.length; i<childNodes.length; i++) {
        			zTree.checkNode(childNodes[i]);
        		}
        	}
        }
        return childNodes;
    }
	
	function deviceTypeOnClick(e, treeId, treeNode) {  
	    var zTree = $.fn.zTree.getZTreeObj("menuTree");  
	    var allDepartmentTo = new Array();
	    nodes = zTree.getSelectedNodes();
	    if(nodes){
	    	zTree.checkNode(nodes[0]);
	    	addMessageToMap(treeNode)
	    }
	}  
	
	function addMessageToMap(treeNode){
		var allDepartmentTo = new Array();
		if(treeNode.type=="user"){
			allDepartmentTo.push(treeNode.alias,treeNode.id,treeNode.name,treeNode.email,'user',treeNode.pId);
			allDepartmentToMap.put(treeNode.id,allDepartmentTo);
		}else{
			allDepartmentTo.push(treeNode.name,treeNode.id,treeNode.name,null,'department',treeNode.pId);
			allDepartmentToMap.put("department"+treeNode.id,allDepartmentTo);
		}
	}
	
	function checkchange(e, treeId, treeNode) {
		allDepartmentToMap = new Map();
		var zTree = $.fn.zTree.getZTreeObj("menuTree");
		var childrenNode = zTree.getCheckedNodes(true);
		var flag = 0;
		for(var i = 0; i < childrenNode.length; i++){ 
		 	if(childrenNode[i].type=="department" && (childrenNode[i].check_Child_State==-1 || childrenNode[i].check_Child_State==2)){
		 		flag++;
		 	} 
		} 
		if(flag==0){
	 		for(var i = 0; i < childrenNode.length; i++){ 
			 	if(childrenNode[i].type=="user" ){
			 		addMessageToMap(childrenNode[i])
			 	}  
	 		}
	 	}else{
	 		for(var i = 0; i < childrenNode.length; i++){
			 	if(childrenNode[i].type=="user" && childrenNode[i].getParentNode().check_Child_State !=2){
			 		addMessageToMap(childrenNode[i])
			 	}else if(childrenNode[i].type==="department" && (childrenNode[i].check_Child_State==-1 || childrenNode[i].check_Child_State==2)){
			 		addDepartment(childrenNode[i])
			 	}
	 		}
	 	}
	}
	
	function changeMessage(){
		allDepartmentToMap.each(function(key,value,index){
			oldMessage.push(key);
		});
	}
	
	function addDepartment(node){
		var pNode = node.getParentNode();
		if(pNode && pNode.check_Child_State==2){
			node = pNode;
			addDepartment(node);
		}else{
			addMessageToMap(node);
		}
	}
	
	function addOrganization(){
	    $('.bgPop,.pop').show();
	    if(allDepartmentToMap.isEmpty()){
	    	showNodeTree();
	    }else{
	    	changeMessage();
	    	var zTree = $.fn.zTree.getZTreeObj("menuTree");
	    	zTree.checkAllNodes(false);
	    	allDepartmentToMap.each(function(key,value,index){ 
	    		var node;
	    		if(key.indexOf("department")!=-1){
	    			node = zTree.getNodeByParam("id",key.substring(10),null);
	    			if(node.type == "department"){
	    				zTree.checkNode(node,true,true);
	    			}
	    		}else{
	    			node = zTree.getNodeByParam("id",key,null)
	    			zTree.checkNode(node,true,true);
	    		}
			})
	    }
	}
	
	function showNodeTree(){
		 var params= {
				 "token" : token
			 };
	    $.ajax({  
	    	data: params,	    
	        url:ctx+'/teamspace/member/showDept',  
	        type:'GET',  
	        async:false,  
	        success:function(msg){
	            var obj = eval("("+msg+")");
	        	$.fn.zTree.init($("#menuTree"), deviceTypeSetting, obj);  
	        },
	        error:function(){
	        	handlePrompt("error", voptFailed);
	        }
	    });  
}
	
	function deleteDepartmentUrl(){
		var notDelete = new Array();
		allDepartmentToMap.each(function(key,value,index){ 
			for(var i=0;i<oldMessage.length;i++){
				if(key==oldMessage[i]){
					notDelete.push(key);
				}
			}
		});
		for(var i=0;i<oldMessage.length;i++){
			if($.inArray(oldMessage[i], notDelete) == -1){
				$("#"+oldMessage[i]).click();
			}
		}
	}
	
	Array.prototype.remove = function(s) {     
	    for (var i = 0; i < this.length; i++) {     
	        if (s == this[i])     
	            this.splice(i, 1);     
	    }     
	} 
	
	function Map() {     
	    /** 存放键的数组(遍历用到) */    
	    this.keys = new Array();     
	    /** 存放数据 */    
	    this.data = new Object();     
	         
	    /**   
	     * 放入一个键值对   
	     * @param {String} key   
	     * @param {Object} value   
	     */    
	    this.put = function(key, value) {     
	        if(this.data[key] == null){     
	            this.keys.push(key);     
	        }     
	        this.data[key] = value;     
	    };     
	         
	    /**   
	     * 获取某键对应的值   
	     * @param {String} key   
	     * @return {Object} value   
	     */    
	    this.get = function(key) {     
	        return this.data[key];     
	    };     
	         
	    /**   
	     * 删除一个键值对   
	     * @param {String} key   
	     */    
	    this.remove = function(key) {     
	        this.keys.remove(key);     
	        this.data[key] = null;     
	    };     
	         
	    /**   
	     * 遍历Map,执行处理函数   
	     *    
	     * @param {Function} 回调函数 function(key,value,index){..}   
	     */    
	    this.each = function(fn){     
	        if(typeof fn != 'function'){     
	            return;     
	        }     
	        var len = this.keys.length;     
	        for(var i=0;i<len;i++){     
	            var k = this.keys[i];     
	            fn(k,this.data[k],i);     
	        }     
	    };     
	         
	    /**   
	     * 获取键值数组(类似<a href="http://lib.csdn.net/base/javase" class='replace_word' title="Java SE知识库" target='_blank' style='color:#df3434; font-weight:bold;'>Java</a>的entrySet())   
	     * @return 键值对象{key,value}的数组   
	     */    
	    this.entrys = function() {     
	        var len = this.keys.length;     
	        var entrys = new Array(len);     
	        for (var i = 0; i < len; i++) {     
	            entrys[i] = {     
	                key : this.keys[i],     
	                value : this.data[this.keys[i]]     
	            };     
	        }     
	        return entrys;     
	    };     
	         
	    /**   
	     * 判断Map是否为空   
	     */    
	    this.isEmpty = function() {     
	        return this.keys.length == 0;     
	    };     
	         
	    /**   
	     * 获取键值对数量   
	     */    
	    this.size = function(){     
	        return this.keys.length;     
	    };     
	         
	    /**   
	     * 重写toString    
	     */    
	    this.toString = function(){     
	        var s = "{";     
	        for(var i=0;i<this.keys.length;i++,s+=','){     
	            var k = this.keys[i];     
	            s += k+"="+this.data[k];     
	        }     
	        s+="}";     
	        return s;     
	    };     
	} 