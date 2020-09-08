function FilelabelAdmin(ownerId, bindType, token, currentUserId){
	this.ownerId = ownerId;
	this.bindType = bindType;
	this.token = token;
	this.currentUserId = currentUserId;
	
	this.getOwnerId = function(){
		return ownerId;
	}
	
	this.getBindType = function(){
		return bindType;
	}
	
	this.getToken = function(){
		return token;
	}
	
	this.getCurrentUserId = function(){
		return currentUserId;
	}
}

/**
 * 绑定文件标签
 */
FilelabelAdmin.prototype.bind = function(nodeId,　labelId, labelName, callback){
	$.ajax({
        type: "POST",
        url: ctx + "/filelabel/bindFilelabel",
       	data:{
       		nodeId: nodeId,
       		labelId: labelId,
       		labelName: labelName,
       		bindType: this.getBindType(),
       		token: this.getToken(),
       		ownerId: this.getOwnerId()
       	},
        error: function(request) {
        	handlePrompt("error", failTip);
        },
        success: function(resp) {
        	if (resp.status == 'CREATED'){
        		
        		var labelId = resp.filelabelInfo.id;
        		var labelName = resp.filelabelInfo.labelName;
        		if (labelId){
            		callback({labelId: labelId, labelName: labelName});
        		} else {
        			handlePrompt("error", failTip);
        		}
        	} else {
        		handlePrompt("error", resp.errMsg);
        	}
        }
    });
}

/**
 * 解除文件标签的绑定
 */
FilelabelAdmin.prototype.unbind = function(nodeId, labelId, callback){
	$.ajax({
        type: "POST",
        url: ctx + "/filelabel/unbindFilelabel",
       	data:{
       		nodeId: nodeId,
       		labelId: labelId,
       		bindType: this.getBindType(),
       		token: this.getToken(),
       		ownerId: this.getOwnerId()
       	},
       	error: function(request) {
        	handlePrompt("error", failTip);
        },
        success: function(resp) {
        	if(resp.status == 'OK'){
        		callback();
        	} else{
        		handlePrompt("error", resp.errMsg);
        	}
        }
    });
}

/**
 * 查询企业文件标签信息
 */
FilelabelAdmin.prototype.searchByEnterprise = function(pageElement, pageNum, pageSize, callback, pvagueName){
	var loadingImgEl = '<span><img width="20px;" height="20px;" src = "' + ctx + '/static/skins/default/img/loading.gif"/><span>';
	
	$(pageElement).empty().append(loadingImgEl);
	$.ajax({
        type: "POST",
        cache: false,
        url: ctx + "/filelabel/listFilabel/" + this.getOwnerId(),
       	data:{
       		token: this.getToken(),
       		labelType: this.getBindType(),
       		reqPage: pageNum,
       		pageSize: pageSize,
       		filelabelName: pvagueName
       	},
       	error: function(){
       		$(pageElement).empty();
       	},
        success: function(resp) {
        	$(pageElement).empty();
        	
        	if (resp.status == 'OK'){
        		callback(resp);
        	} else{
        		handlePrompt("error", resp.errMsg);
        	}
        }
    }); 
}

/**
 * 检索文档类型
 */
FilelabelAdmin.prototype.searchDocTypes = function(callback){
	var url = ctx + "/filelabel/getDefaultDoctype/" + this.getCurrentUserId();
	
	$.ajax({
		type : "GET",
		data : {token: this.getToken()},
		url : url,
		success : function(data) {
			callback(data);
		}
	});
}

/**
 * 检索用户最近使用标签
 */
FilelabelAdmin.prototype.searchUserLatestViewedLabels = function(callback){
	var url = ctx + "/filelabel/listLatestLabels/" + this.getOwnerId();
	
	$.ajax({
		type : "GET",
		data : {token: this.getToken()},
		url : url,
		success : function(data) {
			callback(data);
		}
	});
}
