<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html>
<head>
<%@ include file="../common/common.jsp"%>
<link href="${ctx}/static/zTree/zTreeStyle.css" rel="stylesheet"
	type="text/css" />
<link href="${ctx}/static/skins/default/css/mytree.css" rel="stylesheet"
	type="text/css" />
<script src="${ctx}/static/zTree/jquery.ztree.core-3.5.js"
	type="text/javascript"></script>
</head>

<body>
	<%@ include file="../common/header.jsp"%>
	<div class="body" style="min-height: 267px;">
		<div class="body-con clearfix system-con statistics-con">
			<div class="tab-menu">
				<div class="tab-menu-con">
					<ul class="nav nav-tabs">
						<li class="active"><a id="innerAppManageLinkId" href="#none"><spring:message code="header.statistics"/></a></li>
					</ul>
				</div>
			</div>
			<div class="statistics-menu">
				<ul id="treeArea" class="ztree"></ul>
			</div>
			<iframe id="systemFrame" src="" scrolling="no" frameborder="0"></iframe>
		</div>
	</div>
	<%@ include file="../common/footer.jsp"%>
</body>
</html>
<script type="text/javascript">
var setting = {
		data: {
			simpleData: {
				enable: true,
				idKey: "id",
				pIdKey: "pId",
				rootPId: 0
			}
		},
		callback: {    
	  		onClick: nodeClick
	 	},
	 	view :{
	 		showLine : false,
	 		selectedMulti: false
	 	}
	};
	


function nodeClick(event, treeId, treeNode){
	var treeObj =$.fn.zTree.getZTreeObj(treeId);
	if(treeNode.open){
		treeObj.expandNode(treeNode, false, false, false);
	}else{
		treeObj.expandNode(treeNode, true, false, false);
	}
	if(!treeNode.isParent){
		var url = "${ctx}/statistics/chart" + "?treeNodeId=" + treeNode.id;
		$("#systemFrame").attr("src",url);
	}
}

var treeNodes = [
	    {"id":1, "pId":0, "name":"<spring:message code='statistics.currentUser'/>", icon:"static/zTree/img/diy/statistics/statisParentNode.gif"},
	    {"id":11, "pId":1, "name":"<spring:message code='statistics.now'/>", icon:"static/zTree/img/diy/statistics/statisChildrenNode.gif"},
	    {"id":12, "pId":1, "name":"<spring:message code='statistics.history'/>", icon:"static/zTree/img/diy/statistics/statisChildrenNode.gif"},
	    {"id":13, "pId":1, "name":"<spring:message code='statistics.addUser'/>", icon:"static/zTree/img/diy/statistics/statisChildrenNode.gif"},
	    {"id":2, "pId":0, "name":"<spring:message code='statistics.appSaveArea'/>", icon:"static/zTree/img/diy/statistics/statisParentNode.gif"},
	    {"id":21, "pId":2, "name":"<spring:message code='statistics.now'/>", icon:"static/zTree/img/diy/statistics/statisChildrenNode.gif"},
	    {"id":22, "pId":2, "name":"<spring:message code='history.statistics'/>", icon:"static/zTree/img/diy/statistics/statisChildrenNode.gif"},
	    {"id":23, "pId":2, "name":"<spring:message code='statistics.addStorage'/>", icon:"static/zTree/img/diy/statistics/statisChildrenNode.gif"},
	    {"id":3, "pId":0, "name":"<spring:message code='statistics.sysFile'/>", icon:"static/zTree/img/diy/statistics/statisParentNode.gif"},
	    {"id":31, "pId":3, "name":"<spring:message code='statistics.now'/>", icon:"static/zTree/img/diy/statistics/statisChildrenNode.gif"},
	    {"id":32, "pId":3, "name":"<spring:message code='history.statistics'/>", icon:"static/zTree/img/diy/statistics/statisChildrenNode.gif"},
	    {"id":33, "pId":3, "name":"<spring:message code='statistics.addFile'/>", icon:"static/zTree/img/diy/statistics/statisChildrenNode.gif"},
	    {"id":4, "pId":0, "name":"<spring:message code='statistics.sysStorageCapacity'/>", icon:"static/zTree/img/diy/statistics/statisParentNode.gif"},
	    {"id":41, "pId":4, "name":"<spring:message code='statistics.now'/>", icon:"static/zTree/img/diy/statistics/statisChildrenNode.gif"},
	    {"id":42, "pId":4, "name":"<spring:message code='history.statistics'/>", icon:"static/zTree/img/diy/statistics/statisChildrenNode.gif"},
	    {"id":5, "pId":0, "name":"<spring:message code='statistics.sysConcurrent'/>", icon:"static/zTree/img/diy/statistics/statisParentNode.gif"},
	    {"id":6, "pId":0, "name":"<spring:message code='statistics.userstoeage'/>", icon:"static/zTree/img/diy/statistics/statisParentNode.gif"},
	    {"id":61, "pId":6, "name":"<spring:message code='setstorage'/>", icon:"static/zTree/img/diy/statistics/statisChildrenNode.gif"},
	    {"id":62, "pId":6, "name":"<spring:message code='statistics.now'/>", icon:"static/zTree/img/diy/statistics/statisChildrenNode.gif"},
	    {"id":7, "pId":0, "name":"<spring:message code='statistics.customerVisit'/>", icon:"static/zTree/img/diy/statistics/statisParentNode.gif"},
	    {"id":71, "pId":7, "name":"<spring:message code='statistics.now'/>", icon:"static/zTree/img/diy/statistics/statisChildrenNode.gif"},
	    {"id":72, "pId":7, "name":"<spring:message code='history.statistics'/>", icon:"static/zTree/img/diy/statistics/statisChildrenNode.gif"},
	    {"id":8, "pId":0, "name":"<spring:message code='statistics.customerUsedVersion'/>", icon:"static/zTree/img/diy/statistics/statisParentNode.gif"},
	    {"id":81, "pId":8, "name":"<spring:message code='statistics.now'/>", icon:"static/zTree/img/diy/statistics/statisChildrenNode.gif"},
	    {"id":82, "pId":8, "name":"<spring:message code='history.statistics'/>", icon:"static/zTree/img/diy/statistics/statisChildrenNode.gif"}
	];


$(document).ready(function() {
	navMenuSelected("statisticsManageId");
	var zTreeObj = $.fn.zTree.init($("#treeArea"),setting, treeNodes); 
	var node = zTreeObj.getNodesByParam("id", "11", null);
		zTreeObj.selectNode(node[0],false);
		defaultVisit(node[0].id, node[0])
});

function defaultVisit(treeId, treeNode){
		var url = "${ctx}/statistics/chart" + "?treeNodeId=" + treeNode.id;
		$("#systemFrame").attr("src",url);
}

</script>
