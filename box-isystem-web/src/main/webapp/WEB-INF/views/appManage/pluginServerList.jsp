<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="cse" uri="http://cse.huawei.com/custom-function-taglib"%>  
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page import="com.huawei.sharedrive.isystem.util.CSRFTokenManager"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%
request.setAttribute("token", CSRFTokenManager.getTokenForSession(session));
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="Cache-Control" content="no-cache" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="X-UA-Compatible" content="IE=10" />
<meta http-equiv="X-UA-Compatible" content="IE=9" />
<meta http-equiv="X-UA-Compatible" content="IE=8" />
<META HTTP-EQUIV="Expires" CONTENT="0">
<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
<META HTTP-EQUIV="Cache-control" CONTENT= "no-cache, no-store, must-revalidate">
<META HTTP-EQUIV="Cache" CONTENT="no-cache"> 

<title></title>
<link href="${ctx}/static/skins/default/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/static/skins/default/css/public.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/static/skins/default/css/main.css" rel="stylesheet" type="text/css" />

<script src="${ctx}/static/js/public/jquery-1.10.2.min.js" type="text/javascript"></script>
<script src="${ctx}/static/js/public/bootstrap.min.js" type="text/javascript"></script>
<script src="${ctx}/static/js/public/common.js" type="text/javascript"></script>
<script src="${ctx}/static/js/public/JQbox-hw-page.js" type="text/javascript"></script>
</head>
<body>
<% 
response.setHeader("Cache-Control","no-cache, no-store, must-revalidate");
response.setHeader("Pragma","no-cache");
response.setDateHeader("Expires",0);
%>
<div class="sys-content">
	<div class="alert"><i class="icon-lightbulb icon-orange"></i><spring:message code="plugin.server.manager"/></div>
	<div class="clearfix">
		<ul class="nav nav-tabs">
		    <li class="active"><a href="#none" onClick="managePluginServerCluster(this,'SecurityScan')"><spring:message code="security.scan"/></a></li>
		    <!-- 
			<c:if test="${authApp.authUrl=='p'}">
				<li class="active"><a href="#none" onClick="managePluginServerCluster(this,'PreviewPlugin')"><spring:message code="file.preview"/></a></li>
	    		<li><a href="#none" onClick="managePluginServerCluster(this,'SecurityScan')"><spring:message code="security.scan"/></a></li>
	    	</c:if>
	    	<c:if test="${authApp.authUrl=='k'}">
				<li><a href="#none" onClick="managePluginServerCluster(this,'PreviewPlugin')"><spring:message code="file.preview"/></a></li>
	    		<li class="active"><a href="#none" onClick="managePluginServerCluster(this,'SecurityScan')"><spring:message code="security.scan"/></a></li>
	    	</c:if>
	    	-->
	    </ul>
	</div>
    <div class="clearfix">
    	<div class="pull-left">
        <button type="button" class="btn btn-primary btn-small" onClick="createPluginService()"><i class="icon-add"></i><spring:message code="common.create"/></button>
        <button class="btn" type="button" onClick="manageSysconfig('${cse:htmlEscape(authApp.authAppId)}')"/><spring:message code="plugin.server.config"/></button>
        <!--
        <c:if test="${authApp.authUrl=='k'}">	
			<button class="btn" type="button" onClick="manageSysconfig('${cse:htmlEscape(authApp.authAppId)}')"/><spring:message code="plugin.server.config"/></button>
        </c:if>
        -->
        <button class="btn" type="button" onClick="managementAccessCode('${cse:htmlEscape(authApp.authAppId)}')"/><spring:message code="app.manage.connetCode"/></button>
        </div>
    </div>
    <div class="table-con">
        <table class="table table-bordered table-striped">
          <thead>
            <tr>
                <th style="width:20%;"><spring:message code="plugin.server.name"/></th>
                <th style="width:20%;"><spring:message code="plugin.server.descrtion"/></th>
                <th style="width:20%;"><spring:message code="plugin.master.dss"/></th>
                <th style="width:15%;"><spring:message code="common.status"/></th>
                <th style="width:25%;"><spring:message code="common.operation"/></th>
            </tr>
          </thead>
          <tbody>
         <c:forEach items="${pluginServer}" var="ps">
            <tr>
                <td title="${cse:htmlEscape(ps.name)}">${cse:htmlEscape(ps.name)}</td>
                <td title="${cse:htmlEscape(ps.description)}">${cse:htmlEscape(ps.description)}</td>
                <td title="${cse:htmlEscape(ps.dssName)}">${cse:htmlEscape(ps.dssName)}</td>
                <td  
                   <c:if test="${ps.state==0}">title="<spring:message code="common.normal"/>"</c:if>
                    <c:if test="${ps.state==1}">title="<spring:message code="common.few.normal"/>"</c:if>	
                    <c:if test="${ps.state==2}">title="<spring:message code="common.exception"/>"</c:if>	>
                    <c:if test="${ps.state==0}"><img src="${ctx}/static/image/state/status_green.png" alt="<spring:message code="common.normal"/>"/></c:if>
                    <c:if test="${ps.state==1}"><img src="${ctx}/static/image/state/status_yellow.png" alt="<spring:message code="common.few.normal"/>"/></c:if>	
                    <c:if test="${ps.state==2}"><img src="${ctx}/static/image/state/status_red.png" alt="<spring:message code="common.exception"/>"/></c:if>		
                
                </td>
                <td>
                 <button class="btn" type="button" onClick="modifyPluginService('${cse:htmlEscape(ps.clusterId)}','${cse:htmlEscape(appId)}','${cse:htmlEscape(ps.dssId)}')"/><spring:message code="common.modify"/></button>
				 <button class="btn" type="button" onClick="deletePluginService('${cse:htmlEscape(ps.clusterId)}','${cse:htmlEscape(ps.name)}')"/><spring:message code="common.delete"/></button>
                 <button class="btn" type="button" onClick="listInstance('${cse:htmlEscape(ps.clusterId)}','${cse:htmlEscape(ps.name)}')"/><spring:message code="plugin.server.monitoring"/></button>
                </td>
            </tr>
            </c:forEach>
            <c:if test="${empty pluginServer}" >  
            <tr>
                 <td colspan="6" style="text-align:center">
                       <spring:message code="pluginService.add.describe"/><a href="javascript:createPluginService()"><spring:message code="common.create"/></a>。
                 </td>
            </tr>
            </c:if>
          </tbody>
        </table>
    </div>
    <div id="myPage"></div>
</div>
</body>
<script type="text/javascript">
$(function(){
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
})

function managePluginServerCluster(_this,appId)
{
	$(_this).parent().addClass("active").siblings().removeClass("active");
	window.location="${ctx}/pluginServer/pluginServerCluster/listPluginServer?appId="+appId;
}

function createPluginService(){
	top.ymPrompt.win({message:'${ctx}/pluginServer/pluginServerCluster/create?appId=${cse:htmlEscape(appId)}',width:600,height:500,title:'<spring:message code="plugin.create.service"/>', iframe:true,btn:[['<spring:message code="common.create"/>','yes',false,"btn-focus"],['<spring:message code="common.cancel"/>','no',true,"btn-cancel"]],handler:savePluginService});
	top.ymPrompt_addModalFocus("#btn-focus");
}

function savePluginService(tp) {
	if (tp == 'yes') {
		top.ymPrompt.getPage().contentWindow.submitCreatePlugin();
	} else {
		top.ymPrompt.close();
	}
}
function deletePluginService(id,pluginseviceName){
	top.ymPrompt.confirmInfo( {
		title :'<spring:message code="common.delete"/>',
		message : '<spring:message code="plugin.service.cluster.delete.cliew"/>',
		closeTxt:'<spring:message code="common.close"/>',
		handler : function(tp) {
			if(tp == "ok"){
				$.ajax({
		        	type: "POST",
		        	url:"${ctx}/pluginServer/pluginServerCluster/deletePreview",
		        	data:{clusterId:id,appId:'${cse:htmlEscape(appId)}',seviceName:pluginseviceName},
		        	error: function(request) {
		        		top.handlePrompt("error",'<spring:message code="common.delete.fail"/>');
		        	},
		       		success: function() {
		        		top.handlePrompt("success",'<spring:message code="common.delete.success"/>');
		    			top.window.frames[0].location = "${ctx}/pluginServer/pluginServerCluster/listPluginServer?appId=${cse:htmlEscape(appId)}";
						
		        	}
		    	});
			}
		},
	btn: [['<spring:message code="common.OK"/>', "ok"],['<spring:message code="common.cancel"/>', "cancel"]]
});
}

function listInstance(id,name){
	name=encodeURIComponent(encodeURIComponent(name));
	window.location="${ctx}/pluginServer/pluginServerCluster/listInstances?name="+name+"&clusterId="+id+"&appId=${cse:htmlEscape(appId)}";
}

function modifyPluginService(clusterId,appId,dssId){
	var params = "clusterId="+clusterId+"&appId="+appId+"&dssId="+dssId;
	top.ymPrompt.win({message:'${ctx}/pluginServer/pluginServerCluster/modifyPluginServcive?'+params+'',width:600,height:500,title:'<spring:message code="app.modiffy"/>', iframe:true,btn:[['<spring:message code="common.modify"/>','yes',false,"btnModify"],['<spring:message code="common.cancel"/>','no',true,"btnModifyCancel"]],handler:doPluginService});
	top.ymPrompt_addModalFocus("#btnModify");
}
function doPluginService(tp) {
	if (tp == 'yes') {
		top.ymPrompt.getPage().contentWindow.submitModifyPlugin();
	} else {
		top.ymPrompt.close();
	}
}

function deleteApp(id) {
	top.ymPrompt.confirmInfo( {
		title :'<spring:message code="app.app.del"/>',
		message : '<spring:message code="app.app.del.cliew"/>',
		closeTxt:'<spring:message code="common.close"/>',
		handler : function(tp) {
			if(tp == "ok"){
				$.ajax({
			        type: "POST",
			        url:"${ctx}/appmanage/authapp/delete",
			        data:{authAppId:id,appId:"${cse:htmlEscape(appId)}"},
			        error: function(request) {
			        	top.handlePrompt("error",'<spring:message code="app.app.del.fail"/>');
			        },
			        success: function() {
			        	top.handlePrompt("success",'<spring:message code="app.app.del.success"/>');
			        	refreshWindow();
			        }
			    });
			}
		},
		btn: [['<spring:message code="common.OK"/>', "ok"],['<spring:message code="common.cancel"/>', "cancel"]]
	});
}
function refreshWindow() {
	window.location.reload();
}
function managementAccessCode(id){
	top.ymPrompt.win({message:'${ctx}/pluginServer/appaccesskey/?appId='+id+'',width:1150,height:430,title:'<spring:message code="app.manage.connetCode"/>', iframe:true,btn:[['<spring:message code="common.close"/>','no',true]]});
}
function manageSysconfig(id){
	top.ymPrompt.win({message:'${ctx}/pluginServer/KIAconfig/congfig?appId='+id+'',width:550,height:470,title:'<spring:message code="plugin.KIA.congfig.title"/>', iframe:true });
}
</script>
</html>
