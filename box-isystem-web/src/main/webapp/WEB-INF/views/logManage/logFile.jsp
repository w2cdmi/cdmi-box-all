<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ page import="com.huawei.sharedrive.isystem.util.CSRFTokenManager"%>

<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%
request.setAttribute("token", CSRFTokenManager.getTokenForSession(session));
%>
<!DOCTYPE html>
<html>
<head>

<%@ include file="../common/common.jsp"%>
<link href="${ctx}/static/zTree/zTreeStyle.css" rel="stylesheet" type="text/css" />
<script src="${ctx}/static/zTree/jquery.ztree.core-3.5.js" type="text/javascript"></script>
<script src="${ctx}/static/js/public/JQbox-hw-page.js" type="text/javascript"></script>
<script src="${ctx}/static/js/public/JQbox-hw-grid.js" type="text/javascript"></script>
</head>
  
 <body>
 <div class="sys-content">
 	<div class="form-horizontal form-con clearfix">
 		<div class="form-left">
 			<div class="control-group">
 				<div class="tree-con">
 					<ul id="uasTreeArea" class="ztree"></ul>
			    </div>
			    <div class="tree-con">
 					<ul id="dssTreeArea" class="ztree"></ul>
			    </div>
 			</div>
 		</div>
 	</div>
 </div>
       
 </body>
 <script type="text/javascript">
 $(document).ready(function() {
	var settingUAS = {
		async: {
			enable: true,
			url:"${ctx}/log/logfile/subnodes",
			autoParam:["type", "id"],
			type:"post"
		}
	};
	var zNodesUAS = [${uas}];
		   
	$.fn.zTree.init($("#uasTreeArea"), settingUAS, zNodesUAS);
	
	var settingDSS = {
		async: {
			enable: true,
			url:"${ctx}/log/logfile/subnodes",
			autoParam:["type", "id"],
			type:"post"
		}
	};
	var zNodesDSS = [${dss}];
			   
	$.fn.zTree.init($("#dssTreeArea"), settingDSS, zNodesDSS);
});

 </script>
</html>
