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
<title></title>
<link href="${ctx}/static/skins/default/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/static/skins/default/css/public.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/static/skins/default/css/main.css" rel="stylesheet" type="text/css" />

<script src="${ctx}/static/js/public/jquery-1.10.2.min.js" type="text/javascript"></script>
<script src="${ctx}/static/js/public/bootstrap.min.js" type="text/javascript"></script>
<script src="${ctx}/static/js/public/common.js" type="text/javascript"></script>
<script src="${ctx}/static/js/public/JQbox-hw-page.js" type="text/javascript"></script>
<%@ include file="../common/common.jsp"%>
</head>
<body>
<div class="sys-content">
	<div class="alert"><i class="icon-lightbulb icon-orange"></i><spring:message code="plugin.server.message"/></div>
    <div class="table-con">
        <table class="table table-bordered table-striped">
          <thead>
            <tr>
                <th><spring:message code="app.ID"/></th>
                <th style="width:30%;"><spring:message code="app.modiffy.time"/></th>
                <th style="width:40%;"><spring:message code="common.operation"/></th>
            </tr>
          </thead>
          <tbody>
         <c:forEach items="${pluginApplist}" var="authApp">
            <tr>
                <td title="${cse:htmlEscape(authApp.authAppId)}"><a href="javascript:managePluginServerCluster('${cse:htmlEscape(authApp.authAppId)}')">${cse:htmlEscape(authApp.authAppId)}</a></td>
                
                <td>
               	 <fmt:formatDate value="${authApp.modifiedAt}" pattern="yyyy-MM-dd HH:mm"/>
                </td>
                <td>
                <button class="btn" type="button" onClick="managePluginServerCluster('${cse:htmlEscape(authApp.authAppId)}')"/><spring:message code="plugin.server.serviceManger"/></button>
				<c:if test="${authApp.authUrl=='k'}">	
				<button class="btn" type="button" onClick="manageSysconfig('${cse:htmlEscape(authApp.authAppId)}')"/><spring:message code="plugin.server.config"/></button>
                </c:if>
               	<button class="btn" type="button" onClick="managementAccessCode('${cse:htmlEscape(authApp.authAppId)}')"/><spring:message code="app.manage.connetCode"/></button>
               
                </td>
            </tr>
            </c:forEach>
          </tbody>
        </table>
    </div>
</div>
</body>
<script type="text/javascript">

$(function(){
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
})

function managePluginServerCluster(appId)
{
	window.location="${ctx}/pluginServer/pluginServerCluster/listPluginServer?appId="+appId;
}

function manageSysconfig(id){
	top.ymPrompt.win({message:'${ctx}/pluginServer/KIAconfig/congfig?appId='+id+'',width:550,height:450,title:'<spring:message code="plugin.KIA.congfig.title"/>', iframe:true });
}

function modifyApp(id){
	top.ymPrompt.win({message:'${ctx}/appmanage/authapp/modify/?authAppId='+id+'',width:600,height:370,title:'<spring:message code="app.modiffy"/>', iframe:true,btn:[['<spring:message code="common.modify"/>','yes',false,"btnModify"],['<spring:message code="common.cancel"/>','no',true,"btnModifyCancel"]],handler:doModifyApp});
	top.ymPrompt_addModalFocus("#btnModify");
}
function doModifyApp(tp) {
	if (tp == 'yes') {
		top.ymPrompt.getPage().contentWindow.submitModifyApp();
	} else {
		top.ymPrompt.close();
	}
}
function managementAccessCode(id){
	top.ymPrompt.win({message:'${ctx}/pluginServer/appaccesskey/?appId='+id+'',width:1150,height:430,title:'<spring:message code="app.manage.connetCode"/>', iframe:true,btn:[['<spring:message code="common.close"/>','no',true]]});
}

</script>
</html>
