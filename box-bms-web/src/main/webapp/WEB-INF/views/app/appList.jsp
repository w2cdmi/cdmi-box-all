<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="cse" uri="http://cse.huawei.com/custom-function-taglib"%>  
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
<%@ include file="../common/common.jsp"%>
<script src="${ctx}/static/js/public/JQbox-hw-page.js" type="text/javascript"></script>
</head>
<body>

<div class="sys-content">
	<div class="alert"><i class="icon-lightbulb"></i><spring:message code="appList.appManage"/></div>
    <div class="clearfix">
    	<div class="pull-left">
        <button type="button" class="btn btn-primary" onClick="createApp()"><i class="icon-plus"></i><spring:message code="appList.connect.app"/></button>
        </div>
    </div>
    <div class="table-con">
        <table class="table table-bordered table-striped">
          <thead>
            <tr>
                <th><spring:message code="appList.appId"/></th>
                <th><spring:message code="common.type"/></th>
                <th><spring:message code="authorize.description"/></th>
                <th><spring:message code="authorize.update.time"/></th>
                <th width="46%"><spring:message code="common.operation" /></th>
            </tr>
          </thead>
          <tbody>
         <c:forEach items="${authAppList}" var="authApp">
            <tr>
                <td title="${cse:htmlEscape(authApp.authAppId)}" >${cse:htmlEscape(authApp.authAppId)}</td>
                <td title="<c:if test="${authApp.type == 1}"><spring:message code="appList.defaultWebApp"/></c:if><c:if test="${authApp.type != 1}"><spring:message code="appList.otherApp"/></c:if>">
                	<c:if test="${authApp.type == 1}">
                        	<spring:message code="appList.defaultWebApp"/>
                    </c:if>
                    <c:if test="${authApp.type != 1}">
                       		<spring:message code="appList.otherApp"/>
                    </c:if>
                </td>
                <td title="${cse:htmlEscape(authApp.description)}">${cse:htmlEscape(authApp.description)}</td>
                <td>
                <fmt:formatDate value="${authApp.modifiedAt}" pattern="yyyy-MM-dd HH:mm"/>
                </td>
                <td>
                <button class="btn" type="button" onClick="modifyApp('<c:out value="${authApp.authAppId}"/>')"/><spring:message code="common.modify"/></button>
                <button class="btn" type="button" onClick="configMailServer('<c:out value="${authApp.authAppId}"/>')"/><spring:message code="mailServer.title"/></button>
				<button class="btn" type="button" onClick="configSysParam('<c:out value="${authApp.authAppId}"/>')"/><spring:message code="appSysConfig.app.config"/></button>
				<c:if test="${customUploadNetwork}">
				<button class="btn" type="button" onClick="configNetworkRegion('<c:out value="${authApp.authAppId}"/>')"/><spring:message code="app.network.region.config.title"/></button>
				</c:if>
                </td>
            </tr>
            </c:forEach>
            <c:if test="${empty authAppList}" >  
            <tr>
                 <td colspan="6" style="text-align:center">
                 <spring:message code="appList.appNewHint"/><a href="javascript:createApp()"><spring:message code="common.new"/></a>。
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
$(document).ready(function() {
	if(!placeholderSupport()){
		placeholderCompatible();
	};
	
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
});

function createApp(){
	top.ymPrompt.win({message:'${ctx}/app/appmanage/authapp/create',width:670,height:425,title:'<spring:message code="appList.appNew"/>', iframe:true,btn:[['<spring:message code="common.new"/>','yes',false,"btn-focus"],['<spring:message code="common.cancel"/>','no',true,"btn-cancel"]],handler:saveApp});
	top.ymPrompt_addModalFocus("#btn-focus");
}

function saveApp(tp) {
	if (tp == 'yes') {
		top.ymPrompt.getPage().contentWindow.submitCreateApp();
	} else {
		top.ymPrompt.close();
	}
}
function modifyApp(id){
	top.ymPrompt.win({message:'${ctx}/app/appmanage/authapp/modify/?authAppId='+id+'',width:660,height:400,title:'<spring:message code="appList.appUpdate"/>', iframe:true,btn:[['<spring:message code="common.modify"/>','yes',false,"btnModify"],['<spring:message code="common.cancel"/>','no',true,"btnModifyCancel"]],handler:doModifyApp});
	top.ymPrompt_addModalFocus("#btnModify");
}
function doModifyApp(tp) {
	if (tp == 'yes') {
		top.ymPrompt.getPage().contentWindow.submitModifyApp();
	} else {
		top.ymPrompt.close();
	}
}

function configMailServer(id){
	window.location = "${ctx}/app/mailserver/config/"+id;
}

function configSysParam(id){
	window.location = "${ctx}/app/basicconfig/config/"+id;
}
function configNetworkRegion(id){
	window.location = "${ctx}/app/network/config/"+id;
}

$.fn.comboPage.pageSkip = function(opts, _idMap, curPage){
	$("#page").val(curPage);
	$("#searchForm").submit();
};

$("#checkall").click(function(){ 
	if(this.checked){ 
		$("input[name='checkname']:checkbox").each(function(){
			this.checked=true;
		});
	}else{ 
		$("input[name='checkname']:checkbox").each(function(){
			 this.checked=false;
		});
	}
});

function refreshWindow() {
	window.location.reload();
}
</script>
</html>
