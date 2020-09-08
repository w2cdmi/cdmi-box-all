<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page import="com.huawei.sharedrive.isystem.util.CSRFTokenManager"%>
<%@ taglib prefix="cse" uri="http://cse.huawei.com/custom-function-taglib"%>  
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<%
    request.setAttribute("token",
					CSRFTokenManager.getTokenForSession(session));
%>
<!DOCTYPE html>
<html>
<head>
<link href="${ctx}/static/zTree/zTreeStyle.css" rel="stylesheet" type="text/css">
<%@ include file="../common/common.jsp"%>
<script src="${ctx}/static/zTree/jquery.ztree.all-3.5.min.js"	type="text/javascript"></script>
</head>
<body>
	<div class="pop-content pop-content-en">
		<div class="form-con">
			<form class="form-horizontal form-horizontal-new" id="creatPluginForm"
				name="creatPluginForm">
				<div class="control-group">
					<label class="control-label" for=""><em>*</em> <spring:message
							code="plugin.server.name" /> :</label>
					<div class="controls">
						<input type="text" id="name" name="name" class="span4" /> 
						<span class="validate-con bottom"><div></div></span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for=""><spring:message
							code="plugin.server.descrtion" /> :</label>
					<div class="controls">
						<input type="text" id="description" name="description"
							class="span4" /> <span class="validate-con bottom"><div></div></span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label"><em>*</em><spring:message
							code="plugin.master.dss" /> :</label>
					<div class="controls">
						<input id="masterValue" type="text" readonly value=""
							class="span4" onclick="showMenu();" />
						<div id="menuContent" class="select-tree-node">
							<ul id="treeDemo" class="ztree"></ul>
						</div>
						<input	type="hidden" id="dssId" name="dssId" value="" /> 
						<span class="validate-con bottom"><div></div></span>
						<c:if test="${appId=='PreviewPlugin'}">
							<span class="help-block"> <spring:message code="plugin.master.dss.help"/></span>
						</c:if>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for=""><spring:message
							code="plugin.dss.scope" /> :</label>
					<div class="controls">
						<div class="select-tree-node-mutil">
							<ul id="dssScope" class="ztree"></ul>
						</div>
						<c:if test="${appId=='PreviewPlugin'}">
							<span class="help-block"> <spring:message code="plugin.dss.scope.help"/></span>
						</c:if>
					</div>
				</div>


				<div class="control-group">
					<label class="control-label" for=""><em>*</em><spring:message
							code="plugin.monitor.cycle" /> :</label>
					<div class="controls">
						<input type="text" id="monitorCycle" name="monitorCycle" value="10"
							class="span2" /><spring:message code="plugin.service.minute"/><span class="validate-con bottom"><div></div></span>
					</div>
					
				</div>
		<input type="hidden" name="appId" value="${cse:htmlEscape(appId)}" />
		
		 <input type="hidden"
			name="token" value="${token}" />
		</form>
	</div>

	</div>
	<script type="text/javascript">
	$.validator.addMethod(
			   "ismonitorCycle", 
			   function(value, element) {   
		           var validName = /^[1-9]{1}[0-9]*$/; 
		           if(validName.test(value))
		           {
		        	  if(value<60&&value>0)
		        	  {
		        		  
		        		  return true;
		        	  }
		           }
		           return false;   
		       }, 
		       $.validator.format('<spring:message code="plugin.monitor.value"/>')
	); 
	
	$(document).ready(function(){
		$("#creatPluginForm").validate({ 
			rules: { 
					name:{
						required:true, 
					    rangelength:[1,255]
				   },
				   description:{
					   maxlength:[512]
				   },
				   masterValue:{
					   required:true, 
					   rangelength:[1,255]
				   },
				   monitorCycle:
				   {
					   required:true, 
					   ismonitorCycle:true
				   }
			}
	    }); 
		$.fn.zTree.init($("#treeDemo"), setting_master);
	});
		var setting_master = {
			view : {
				selectedMulti : false
			},
			async : {
				enable : true,
				url : "${ctx}/pluginServer/pluginServerCluster/listTreeNode",
				autoParam : [ "id=regionId" ],
				otherParam : [ "appId", "${cse:htmlEscape(appId)}","clusterId",-1,"token","${cse:htmlEscape(token)}"],
				dataFilter : masterFilter
			},
			data : {
				key : {
					name : "name",
					checked : "checked",
					isParent : "parent"
				}
			},
			callback : {
				onClick : onClick,
				onCheck : onCheck
			},
			check : {
				enable : true,
				chkStyle : "radio",
				radioType : "all"
			}
		};
		function masterFilter(treeId, parentNode, responseData) {
			if (responseData) {
				for ( var i = 0; i < responseData.length; i++) {
					if (responseData[i].isParent) {
						responseData[i].nocheck = true;
					}
				}
			}
			return responseData;
		}

		function onClick(e, treeId, treeNode) {
			var zTree = $.fn.zTree.getZTreeObj("treeDemo");
			zTree.checkNode(treeNode, !treeNode.checked, null, true);
			return false;
		}

		function onCheck(e, treeId, treeNode) {
			var zTree = $.fn.zTree.getZTreeObj("treeDemo"), nodes = zTree
					.getCheckedNodes(true), v = "", dssId;
			for ( var i = 0, l = nodes.length; i < l; i++) {
				v += nodes[i].name + ",";
				dssId = nodes[i].id;
				alert
			}
			if (v.length > 0)
				v = v.substring(0, v.length - 1);
			var masterValue = $("#masterValue");
			var dssIdE = $("#dssId");
			masterValue.attr("value",v);
			dssIdE.attr("value", dssId)
			$.fn.zTree.init($("#dssScope"), setting_scope);
		}

		function showMenu() {
			var cityObj = $("#masterValue");
			var cityOffset = $("#masterValue").offset();
			$("#menuContent").css({
				left : cityOffset.left + "px",
				top : cityOffset.top + cityObj.outerHeight() + "px"
			}).slideDown("fast");

			$(document).bind("mousedown", onBodyDown);
		}
		function hideMenu() {
			$("#menuContent").fadeOut("fast");
			$("body").unbind("mousedown", onBodyDown);
		}
		function onBodyDown(event) {
			if (!(event.target.id == "menuBtn" || event.target.id == "menuContent" || $(event.target)
					.parents("#menuContent").length > 0)) {
				hideMenu();
			}
		}

		var setting_scope = {
			check : {
				enable : true,
				chkStyle : "checkbox"
			},
			async : {
				enable : true,
				url : "${ctx}/pluginServer/pluginServerCluster/listTreeNode",
				autoParam : [ "id=regionId" ],
				otherParam : [ "appId", "${cse:htmlEscape(appId)}","clusterId",-1,"token","${cse:htmlEscape(token)}" ],
				dataFilter : scopeFilter
			},
			callback:
			{
				onAsyncSuccess:onAsyncSuccess
			}
		};
		function scopeFilter(treeId, parentNode, responseData) {
			var zTree = $.fn.zTree.getZTreeObj("treeDemo");
			var nodes = zTree.getCheckedNodes(true);
			for ( var i = 0; i < nodes.length; i++) {
				for ( var j = 0; j < responseData.length; j++) {
					if (responseData[j].isParent) {
						break;
					}
					if (responseData[j].id == nodes[i].id
							&& responseData[j].name == nodes[i].name) {
						responseData.splice(j, 1)
						j--;
					}
				}
			}
			return responseData;
		}

		function onAsyncSuccess(event,treeId,treeNode,msg)
		{
			var zTree = $.fn.zTree.getZTreeObj("dssScope");
			var nodes = zTree.getNodes();
			asyncNodes(nodes);
		}
		function asyncNodes(nodes)
		{
			var zTree = $.fn.zTree.getZTreeObj("dssScope");
			for(var i=0;i<nodes.length;i++)
			if(nodes[i].isParent&&nodes[i].zAsync)
			{
				if(nodes[i].children=="")
				{
					zTree.setChkDisabled(nodes[i], true);
				}else{
						zTree.setChkDisabled(nodes[i], false);
				}
				asyncNodes(nodes[i].children);
			}else
			{
				zTree.reAsyncChildNodes(nodes[i],"refresh",true);
			}
		}

		function submitCreatePlugin() {
			if (!$("#creatPluginForm").valid()) {

				return false;
			}
			var zTree = $.fn.zTree.getZTreeObj("dssScope");
			if(zTree==null)
			{
				handlePrompt("error",
				'<spring:message code="plugin.master.select.error"/>');
				showMenu();
				return false;
			}
			var nodes = zTree.getCheckedNodes(true);
			var pluginServerCluster=$('#creatPluginForm').serialize()
			var  addRouter ="["
			for(var i = 0; i< nodes .length; i++)
			{
				if(!nodes[i].isParent)
					addRouter+="{\"dssId\":"+nodes[i].id+"},"
			}
			if (addRouter.length > 1)
				addRouter = addRouter.substring(0, addRouter.length - 1);
			addRouter+="]"
			$.ajax({
						type : "POST",
						url : "${ctx}/pluginServer/pluginServerCluster/addPreview",
						data:$('#creatPluginForm').serialize()+"&addRouter="+addRouter,
						error : function(request) {
							handlePrompt("error",
									'<spring:message code="common.createFail"/>');
						},
						success : function() {
							top.ymPrompt.close();
							top.handlePrompt("success",
											'<spring:message code="common.createSuccess"/>');
							top.window.frames[0].location = "${ctx}/pluginServer/pluginServerCluster/listPluginServer?appId=${cse:htmlEscape(appId)}";
						}
					});
		}
		
	</script>
</body>
</html>
