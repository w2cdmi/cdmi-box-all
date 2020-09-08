<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cse" uri="http://cse.huawei.com/custom-function-taglib"%>  
<%@ page import="com.huawei.sharedrive.isystem.util.CSRFTokenManager"%>
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
			<form class="form-horizontal" id="modifyPluginForm"
				name="modifyPluginForm">
				<div class="control-group">
					<label class="control-label" for=""><em>*</em> <spring:message
							code="plugin.server.name" /> :</label>
					<div class="controls">
						<input type="text" id="name" name="name" class="span4" value="${cse:htmlEscape(modifyPSCluster.name)}"/> <span
							class="validate-con bottom"><div></div></span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for=""><spring:message
							code="plugin.server.descrtion" /> :</label>
					<div class="controls">
						<input type="text" id="description" name="description" value="${cse:htmlEscape(modifyPSCluster.description)}"
							class="span4" /> <span class="validate-con bottom"><div></div></span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label"><spring:message
							code="plugin.master.dss" /> :</label>
					<div class="controls">
						<input id="masterValue" type="text" readonly value="${cse:htmlEscape(modifyPSCluster.dssName)}"
							class="span4"  />
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for=""><spring:message
							code="plugin.dss.scope" /> :</label>
					<div class="controls" >
						<div class="select-tree-node-mutil">
						<ul id="dssScope" class="ztree"></ul>
						</div>
					</div>
				</div>


				<div class="control-group">
					<label class="control-label" for=""><em>*</em><spring:message
							code="plugin.monitor.cycle" /> :</label>
					<div class="controls">
						<input type="text" id="monitorCycle" name="monitorCycle"  value=""
							class="span2" /> <spring:message code="plugin.service.minute"/><span class="validate-con bottom"><div></div></span>
					</div>
				</div>
		</div>
		<input type="hidden" name="appId" value="${cse:htmlEscape(modifyPSCluster.appId)}" />
		<input type="hidden" id="dssId" name="dssId" value="${cse:htmlEscape(modifyPSCluster.dssId)}" /> 
		<input type="hidden" id="clusterId" name="clusterId" value="${cse:htmlEscape(modifyPSCluster.clusterId)}" /> 
		<input type="hidden" name="token" value="${token}"/>
		</form>
	</div>
	</div>
	<script type="text/javascript">

	$.validator.addMethod(
			   "ismonitorCycle", 
			   function(value, element) {   
		           var validName = /^[1-9]{1}[0-9]*$/;   
		           return validName.test(value);   
		       }, 
		       $.validator.format('<spring:message code="plugin.monitor.value"/>')
	); 
	$(document).ready(function() {
	$("#modifyPluginForm").validate({ 
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
					   ismonitorCycle:true,
					   max:59
				   }
			}
	    }); 
			$.fn.zTree.init($("#dssScope"), setting_scope);
			$("#monitorCycle").attr("value", '${cse:htmlEscape(modifyPSCluster.monitorCycle)}'/60);
		});
		var setting_scope = {
			check : {
				autoCheckTrigger:true,
				enable : true,
				chkStyle : "checkbox"
			},
			async : {
				enable : true,
				url : "${ctx}/pluginServer/pluginServerCluster/listTreeNode",
				autoParam : [ "id=regionId" ],
				otherParam : [ "appId", "${cse:htmlEscape(modifyPSCluster.appId)}","clusterId","${cse:htmlEscape(modifyPSCluster.clusterId)}","dssId","${cse:htmlEscape(modifyPSCluster.dssId)}","token","${cse:htmlEscape(token)}" ],
				dataFilter : scopeFilter
			},
			callback:
			{
				beforeCheck:beforecheck,
				onAsyncSuccess:onAsyncSuccess
			}
		};
		function beforecheck(treeId,treeNode)
		{
			var zTree = $.fn.zTree.getZTreeObj("dssScope");
			zTree.expandNode(treeNode,true,true,true);
			return true;
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
		function scopeFilter(treeId, parentNode, responseData) {
				for ( var j = 0; j < responseData.length; j++) {
					if (responseData[j].isParent) {
						break;
					}
					if (responseData[j].id == '${cse:htmlEscape(modifyPSCluster.dssId)}') {
						responseData.splice(j, 1)
						j--;
					}
				}
			return responseData;
		}

		function submitModifyPlugin() {
			if (!$("#modifyPluginForm").valid()) {
				return false;
			}
			var zTree = $.fn.zTree.getZTreeObj("dssScope");
			var nodes = zTree.getCheckedNodes(true);
			var changeNodes = zTree.getChangeCheckedNodes();
		    var delRouter ="[";
		    var addRouter="[";
			for(var i=0;i<changeNodes.length;i++)
			{
				if(changeNodes[i].isParent==false){
					if(changeNodes[i].checked==false)
					{
				 		delRouter+="{\"dssId\":"+changeNodes[i].id+"},";
					}else{
						addRouter+="{\"dssId\":"+changeNodes[i].id+"},";
					}
					}
			}
			if (addRouter.length > 1)
				addRouter = addRouter.substring(0, addRouter.length - 1);
				addRouter+="]";
			if (delRouter.length > 1)
				delRouter = delRouter.substring(0, delRouter.length - 1);
				delRouter+="]";
			var pluginServerCluster=$('#creatPluginForm').serialize()
			$.ajax({
						type : "POST",
						url : "${ctx}/pluginServer/pluginServerCluster/addPreview",
						data:$('#modifyPluginForm').serialize()+"&addRouter="+addRouter+"&delRouter="+delRouter,
						error : function(request) {
							handlePrompt("error",
									'<spring:message code="common.modifyFail"/>');
						},
						success : function() {
							top.ymPrompt.close();
							top
									.handlePrompt("success",
											'<spring:message code="common.modifySuccess"/>');
							top.window.frames[0].location = "${ctx}/pluginServer/pluginServerCluster/listPluginServer?appId=${cse:htmlEscape(modifyPSCluster.appId)}";
						}
					});
		}
	</script>
</body>
</html>
