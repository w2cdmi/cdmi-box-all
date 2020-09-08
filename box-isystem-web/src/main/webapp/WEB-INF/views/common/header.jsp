<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.apache.shiro.SecurityUtils" %>
<%@ page import="com.huawei.sharedrive.isystem.user.domain.Admin" %>
<%@ page import="com.huawei.sharedrive.isystem.util.Constants" %>
<%@ page import="com.huawei.sharedrive.isystem.util.CSRFTokenManager"%>
<%@ page import="com.huawei.sharedrive.isystem.util.DateTimeUtils"%>
<%@ page import="org.apache.shiro.session.Session" %>
<%@ page import="org.apache.shiro.SecurityUtils" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<META HTTP-EQUIV="Expires" CONTENT="0">
<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
<META HTTP-EQUIV="Cache-control" CONTENT= "no-cache, no-store, must-revalidate">
<META HTTP-EQUIV="Cache" CONTENT="no-cache"> 
<%
response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
response.setHeader("Pragma", "no-cache");
response.setDateHeader("Expires", 0);
Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
request.setAttribute("token", CSRFTokenManager.getTokenForSession(session));
Session sesion = SecurityUtils.getSubject().getSession();
boolean tag = (Boolean) sesion.getAttribute("tag");
String name = (String) sesion.getAttribute("name");
if (name == null || name.trim().length() <= 0){
	name = admin.getName();
}
%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<script type="text/javascript"> 
function disableBack(){
	window.history.forward();
} 
disableBack(); 
window.onload=disableBack; 
window.onpageshow=function(evt){
	if(evt.persisted){
		disableBack();
	}
} 
window.onunload=function(){
	void(0);
} 
</script> 
<div class="header">
    <div class="header-con">
		<div class="logo pull-left"><a href="#" ><spring:message code="main.title" /></a></div>
        <div class="nav-menu">
        	<ul class="clearfix" id="downMenu">
        		<shiro:hasRole name="ADMIN_MANAGER"> 
        		<li>
		        	<a class="menu-authorize"><i></i><spring:message code="manage.title.authorize" /></a>
			        <ul>
			        	<li><span id="listManager" onClick="openInframe(this, '${ctx}/authorize/role/list','systemFrame')"><spring:message code="authorize.adminUserList"/></span></li>
			        </ul>
	        	</li>	        	
	        	<li>
		        	<a class="menu-configer"><i></i><spring:message code="plugin.server.config" /></a>
			        <ul>
			        	<li><span onClick="openInframe(this, '${ctx}/authorize/mailserver/load','systemFrame')"><spring:message code="authorize.mail.config"/></span></li>
			        	<li><span onClick="openInframe(this, '${ctx}/authorize/lockConfig','systemFrame')"><spring:message code="sysconfig.lock.config"/></span></li>
			        </ul>
	        	</li>	        	
	        	<li>
		        	<a class="menu-license"><i></i><spring:message code="authorize.license.manage" /></a>
			        <ul>
			        	<li><span onClick="openInframe(this, '${ctx}/authorize/gotoLicense','systemFrame')"><spring:message code="authorize.license.manage"/></span></li>
			        </ul>
	        	</li>
	        	</shiro:hasRole>
	        	<shiro:hasRole name="CLUSTER_MANAGER">
	        	<li>
		        	<a class="menu-cluster"><i></i><spring:message code="manage.title.cluster" /></a>
			        <ul>
			        	<li><span id="regionManager" onClick="openInframe(this, '${ctx}/cluster/region/list','systemFrame')"><spring:message code="clusterManage.regionList"/></span></li>
		        	</ul>
	        	</li>
	        	</shiro:hasRole>
	        	<shiro:hasRole name="APP_MANAGER">
	        	<li>
	        	    <a class="menu-app"><i></i><spring:message code="manage.title.appmanage"/></a>
	        		<ul>
			        	<li><span id="appManager" onClick="openInframe(this, '${ctx}/appmanage/authapp/list','systemFrame')"><spring:message code="manage.title.appaccess"/></span></li>
			        </ul>
	        	</li>
	        	</shiro:hasRole>
	        	<shiro:hasRole name="SYSCONFIG_MANAGER"> 
	        	<li>
	        		<a class="menu-configer"><i></i><spring:message code="plugin.server.config"/></a>
	        		<ul>
			        	<li><span onClick="openInframe(this, '${ctx}/statisticsmanage/statistics/list','systemFrame')"><spring:message code="statistics.title.manager"/></span></li>
			        	<li><span onClick="openInframe(this, '${ctx}/sysconfig/load/-1','systemFrame')"><spring:message code="manage.title.sysconfig"/></span></li>
			        	<li <c:if test='${showCopyPlocy=="false"||showCopyPlocy==null||showCopyPlocy==""}'>style="display: none;" </c:if> ><span onClick="openInframe(this, '${ctx}/mirror/copyPolicy/list','systemFrame')"><spring:message code="copyPolicy.policy.title"/></span></li>
<!-- 			        	原預覽服務功能點去除  -->
 			        	<li><span onClick="openInframe(this, '${ctx}/pluginServer/pluginServerCluster/listPluginServer?appId=PreviewPlugin','systemFrame')"><spring:message code="plug-inServerApp.title"/></span></li>
			        </ul>
	        	</li>
	        	</shiro:hasRole>
	        	<shiro:hasRole name="JOB_MANAGER">
	        	<li>
	        		<a class="menu-monitor"><i></i><spring:message code="monitor.title"/></a>
	        		<ul>
			        	<li><span id="monitorManageId" onClick="openInframe(this, '${ctx}/monitor/manage/nodelist','systemFrame')"><spring:message code="CLUSTER_MANAGER"/></span></li>
			        	<li <c:if test='${showCopyPlocy=="false"||showCopyPlocy==null||showCopyPlocy==""}'>style="display: none;" </c:if>><span onClick="openInframe(this, '${ctx}/mirror/copyTask/list/-1/policy','systemFrame')"><spring:message code="copyPolicy.mirror.title"/></span></li>
			        	<li><span onClick="openInframe(this, '${ctx}/job/enterList','systemFrame')"><spring:message code="manage.title.job"/></span></li>
			        	<li><span id="convertTaskId" onClick="openInframe(this, '${ctx}/convertTask/enterList','systemFrame')"><spring:message code="manage.title.convert.task"/></span></li>
			        </ul>
	        	</li>
	        	</shiro:hasRole>
	        	<shiro:hasRole name="LOG_MANAGER">
	        	<li>
	        	    <a class="menu-journal"><i></i><spring:message code="manage.title.adminlog"/></a>
	        		<ul>
			        	<li><span onClick="openInframe(this, '${ctx}/userlog/log/list','systemFrame')"><spring:message code="log.adminLog"/></span></li>
			        	<li><span onClick="openInframe(this, '${ctx}/log/logfile?type=0','systemFrame')"><spring:message code="log.UASLog"/></span></li>
			        	<li><span onClick="openInframe(this, '${ctx}/log/logfile?type=1','systemFrame')"><spring:message code="log.DSSLog"/></span></li>
			        </ul>
	        	</li>
	        	</shiro:hasRole>
        	</ul>
        </div>
        <div class="header-R pull-right clearfix">
        	<ul class="clearfix pull-right">
            	<li class="pull-left dropdown">
                	<a class="dropdown-toggle" href="#" id="nav-account" data-toggle="dropdown"><strong title="<%=name %>"><%=name %></strong> <i class="icon-caret-down icon-white"></i></a>
                	<ul class="dropdown-menu pull-right">
                	    <%if(admin.getDomainType() == Constants.DOMAIN_TYPE_LOCAL){
                	        if(admin.getType() == Constants.ROLE_SUPER_ADMIN){
                	    %>
                	            <li><a href="javascript:enterModifyAccountPage()"><i class="icon-user"></i><spring:message code="user.name.modiffy"/></a></li>    
                	    <%
                	        }
                	    %>
                	        <li><a href="javascript:enterModifyPwdPage()"><i class="icon-lock"></i><spring:message code="common.pwd.modiffy"/></a></li>
                	        <li><a href="javascript:enterModifyEmail()"><i class="icon-email"></i><spring:message code="common.mail.modiffy"/></a></li>
                	    <% 
                	      }
                	    %>
                	    <li class="divider"></li>
						<li id="langZH"><a href="?locale=zh_CN"><i class="icon-lang-zh"></i>简体中文</a></li>
						<li id="langEN"><a href="?locale=en_US"><i class="icon-lang-en"></i>English</a></li>
                        <li class="divider"></li>
                        <li><a href="#" onclick="doLogout()"><i class="icon-signout"></i> <spring:message code="common.exit"/></a></li>
                    </ul>
                </li>
            </ul>
        </div>
        
        <!-- show login info -->
		<div class="modal hide" id="loginInfo" tabindex="-1" role="dialog"
			aria-hidden="true" data-backdrop="static">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					onclick="closeInfo()" aria-hidden="true">&times;</button>
				<h3>
					<spring:message code="loginInfo.info" />
				</h3>
			</div>
			<div class="modal-body" style="margin-left:40px;">
				<div id="loginTime" style="margin-left:12px;">
					<spring:message code="loginInfo.date" />
					<span id="showTimeZone"></span>
				</div>
				<div id="loginIP" style="margin-top:10px;">
					<spring:message code="loginInfo.IP" />
					<%
					    if (admin.getLastLoginIP() != null)
					    {
					%>
					<%=org.springframework.web.util.HtmlUtils.htmlEscape(admin.getLastLoginIP())%>
					<%
					    }
					%>
					</div>
				<div id="loginClient" style="margin-top:10px;margin-left:27px;">
					<spring:message code="loginInfo.clientInfo" />
					<spring:message code="loginInfo.client" />
				</div>
			</div>
		</div> 
	</div>
</div>
<script type="text/javascript">
$(function(){
	$("#downMenu > li:first-child").addClass("active").find("ul").show();
	$("#downMenu > li:first-child").find("ul li:first-child").addClass("active");
	$("#breadcrumbText").html($("#downMenu > li:first-child").find("a").text()+" > "+$("#downMenu > li:first-child").find("ul li:first-child span").text());
	
	$("#downMenu > li").click(function(){
		$(this).parent().find("ul").hide();
		$(this).find("ul").show();
	})
	$("#downMenu").find("span").first().click();
})
$(function(){
	if('<spring:message code="common.language1"/>' == "en"){
		$("#langEN").remove();
	}else{
		$("#langZH").remove();
		 }
	
	 $("#loginInfo").css({width:"400px",height:"200px",top: "74%",left: "90%"});
	var flag = <c:out value='${tag}'/>;
	var ip = "<%=org.springframework.web.util.HtmlUtils.htmlEscape(admin.getLastLoginIP())%>";
	var time = "<%=admin.getLastLoginTime() %>";	
	if(flag) {
		if(ip != "null" && ip != null && time != "null" && time != null) {
			time = <%=admin.getLastLoginTime() instanceof java.util.Date ? admin.getLastLoginTime().getTime() : null %>;
			$("#showTimeZone").text(getLocalTime(time));
			$("#loginInfo").show();
			setTimeout(function(){
	        		$("#loginInfo").hide();    
	        		<%
	        		sesion.setAttribute("tag", false);
	        		%>  
	        	},5000);
		}
	} 

})

function closeInfo() {
	$("#loginInfo").hide();
}

function doLogout(){
		$.ajax({
        type: "POST",
        url:"${ctx}/logout",
        data : {token : "<c:out value='${token}'/>"},
        error: function(request) {
        	window.location = "${ctx}/login";
        },
        success: function(data) {
        	window.location = "${ctx}/login";
        }
    });
}
function enterModifyPwdPage(){
	top.ymPrompt.win({message:'${ctx}/account/enterChange',width:700,height:300,title:'<spring:message code="common.pwd.modiffy"/>', iframe:true,btn:[['<spring:message code="common.modify"/>','yes',false,"btnModifyPwd"],['<spring:message code="common.cancel"/>','no',true,"btnModifyCancel"]],handler:doSubmitModifyPwd});
	top.ymPrompt_addModalFocus("#btnModifyPwd");
}
function doSubmitModifyPwd(tp) {
	if (tp == 'yes') {
		top.ymPrompt.getPage().contentWindow.submitModifyPwd();
	} else {
		top.ymPrompt.close();
	}
}
function enterModifyEmail(){
	top.ymPrompt.win({message:'${ctx}/account/enteremail',width:600,height:275,title:'<spring:message code="common.mail.modiffy"/>', iframe:true,btn:[['<spring:message code="common.modify"/>','yes',false,"btnModifyEmail"],['<spring:message code="common.cancel"/>','no',true,"btnModifyCancel"]],handler:doSubmitModifyEmail});
	top.ymPrompt_addModalFocus("#btnModifyEmail");
}
function doSubmitModifyEmail(tp) {
	if (tp == 'yes') {
		top.ymPrompt.getPage().contentWindow.submitModify();
	} else {
		top.ymPrompt.close();
	}
}
function enterModifyAccountPage(){
	top.ymPrompt.win({message:'${ctx}/authorize/user',width:700,height:220,title:'<spring:message code="user.name.modiffy"/>', iframe:true,btn:[['<spring:message code="common.modify"/>','yes',false,"btnModifyAccount"],['<spring:message code="common.cancel"/>','no',true,"btnModifyCancel"]],handler:doSubmitModifyAccount});
	top.ymPrompt_addModalFocus("#btnModifyAccount");
}
function doSubmitModifyAccount(tp) {
	if (tp == 'yes') {
		top.ymPrompt.getPage().contentWindow.submitModify();
	} else {
		top.ymPrompt.close();
	}
}

//修改用戶名回掉
function updateUsernameCallback(userName){
	var strongEl = $('a[id=nav-account]').find('strong');
	
	strongEl.attr('title', userName);
	strongEl.text(userName);
}
</script>