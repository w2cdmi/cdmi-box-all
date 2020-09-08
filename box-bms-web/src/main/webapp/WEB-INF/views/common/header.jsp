<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.apache.shiro.SecurityUtils" %>
<%@ page import="pw.cdmi.box.uam.user.domain.Admin" %>
<%@ page import="pw.cdmi.box.uam.util.Constants" %>
<%@ page import="pw.cdmi.box.uam.util.CSRFTokenManager"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ page import="org.apache.shiro.session.Session" %>
<%@ page import="org.apache.shiro.SecurityUtils" %>
<%
response.setHeader("Cache-Control","no-cache, no-store, must-revalidate");
response.setHeader("Pragma","no-cache");
response.setDateHeader("Expires",0);

Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
request.setAttribute("token", CSRFTokenManager.getTokenForSession(session));
Session sesion = SecurityUtils.getSubject().getSession();
boolean tag = (Boolean) sesion.getAttribute("tag");
%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<div class="header">
    <div class="header-con">
    	<div class="logo" id="logoBlock"><img src="${ctx}/static/skins/default/img/logo.png" /><span><spring:message code="main.title" /></span></div>
        <nav class="pull-left nav-menu">
        	<ul class="clearfix">
                <shiro:hasRole name="ADMIN_MANAGER">  
           	  	<li><a href="${ctx}/sys/authorize/role" id="sysUserManageId"><spring:message code="header.userManage" /></a></li>
           	  	<li><a href="${ctx}/sys/sysconfig/mailserver/" id="mailserverId"><spring:message code="mailServer.title" /></a></li>
           	  	<li><a href="${ctx}/sys/loginAlam/manage" id="loginAlamId"><spring:message code="loginAlam.title" /></a></li>
           	  	<li><a href="${ctx}/sys/systemlog/log/manage" id="sysLogManageId"><spring:message code="manage.title.adminlog" /></a></li>
           	  	</shiro:hasRole>
           	  	<shiro:hasRole name="APP_MANAGER">
           	  	<li><a href="${ctx}/app/appmanage/authapp" id="appManageId"><spring:message code="header.app"/></a></li>
           	  	<li><a href="${ctx}/app/adminlog/log/manage" id="appLogManageId"><spring:message code="header.log"/></a></li>
           	   	</shiro:hasRole>
           	  	<shiro:hasRole name="ENTERPRISE_BUSINESS_MANAGER">
           	  	<li><a href="${ctx}/enterprise/manager" id="enterpriseManageId"><spring:message code="header.enterpriseAdmin"/></a></li>
           	  	</shiro:hasRole>
           	  	<%-- <shiro:hasRole name="ENTERPRISE_MANAGER">
           	  	<li><a href="${ctx}/enterprise/admin/enterpriseManage" id="enterpriseAdminManageId"><spring:message code="ENTERPRISE_BUSINESS_MANAGER"/></a></li>
           	  	<li><a href="${ctx}/enterprise/admin/appManage" id="enterpriseAppManageId"><spring:message code="header.appManager"/></a></li>
           	  	</shiro:hasRole> --%>
           	  	<shiro:hasRole name="SYSTEM_CONFIG">
           	  	<li><a href="${ctx}/sys/sysconfig/syslog" id="sysConfigManageId"><spring:message code="manage.title.sysconfig"/></a></li>
           	  	</shiro:hasRole>
           	  	<shiro:hasRole name="ANNOUNCEMENT_MANAGER">  
           	  	<li><a href="${ctx}/announcement" id="announcementManageLinkId"><spring:message code="header.announcement" /></a></li>
           	  	</shiro:hasRole>
           	  	<shiro:hasRole name="STATISTICS_MANAGER">
           	  	<li><a href="${ctx}/statistics" id="statisticsManageId"><spring:message code="header.statistics"/></a></li>
           	  	</shiro:hasRole>
           	  	<shiro:hasRole name="JOB_MANAGER">  
           	  	<li><a href="${ctx}/job" id="jobManageLinkId"><spring:message code="job.header" /></a></li>
           	  	</shiro:hasRole>
           	  	<shiro:hasRole name="FEEDBACK_MANAGER">  
           	  	<li><a href="${ctx}/feedback/uam/manage" id="feedBackManageId"><spring:message code="manage.title.feedback"/></a></li>
           	  	</shiro:hasRole>
            </ul>
        </nav>
        
        <div class="header-R pull-right clearfix" style="margin-right:-20px">
        	<ul class="clearfix pull-right">
            	<li class="pull-left dropdown">
                	<a class="dropdown-toggle" href="#" id="nav-account" data-toggle="dropdown"><strong title="<shiro:principal property='name'/>"><shiro:principal property="name"/></strong> <i class="icon-caret-down icon-white"></i></a>
                	<ul class="dropdown-menu pull-right">
                	    <%if(admin.getDomainType() == Constants.DOMAIN_TYPE_LOCAL){ 
                	        if(admin.getType() == Constants.ROLE_SUPER_ADMIN){
                	    %>
                	            <li><a href="javascript:enterModifyAccountPage()"><i class="icon-user"></i><spring:message code="header.updateUserName"/></a></li>    
                	    <%
                	        }
                	    %>
                	        <li><a href="javascript:enterModifyPwdPage()"><i class="icon-lock"></i><spring:message code="common.updatePwd"/></a></li>
                	        <%if(admin.getType() != Constants.ROLE_ENTERPRISE_ADMIN){%>
                	        <li><a href="javascript:enterModifyEmail()"><i class="icon-envelope"></i><spring:message code="header.updateMail"/></a></li>
                	        <%} %>
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
	if('<spring:message code="main.language"/>' == "en"){
		$("#langEN").remove();
	}else{
		$("#langZH").remove();
	}
	
	$("#loginInfo").css({width:"400px",height:"200px",top: "74%",left: "90%"});
	var flag = <c:out value='${tag}'/>; 
	var ip = "<%=org.springframework.web.util.HtmlUtils.htmlEscape(admin.getLastLoginIP()) %>";	
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
	top.ymPrompt.win({message:'${ctx}/account/enterChange',width:700,height:300,title:'<spring:message code="common.updatePwd"/>', iframe:true,btn:[['<spring:message code="common.modify"/>','yes',false,"btnModifyPwd"],['<spring:message code="common.cancel"/>','no',true,"btnModifyCancel"]],handler:doSubmitModifyPwd});
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
	top.ymPrompt.win({message:'${ctx}/account/enteremail',width:600,height:270,title:'<spring:message code="header.updateMail"/>', iframe:true,btn:[['<spring:message code="common.modify"/>','yes',false,"btnModifyEmail"],['<spring:message code="common.cancel"/>','no',true,"btnModifyCancel"]],handler:doSubmitModifyEmail});
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
	top.ymPrompt.win({message:'${ctx}/sys/authorize/user',width:700,height:220,title:'<spring:message code="header.updateUserName"/>', iframe:true,btn:[['<spring:message code="common.modify"/>','yes',false,"btnModifyAccount"],['<spring:message code="common.cancel"/>','no',true,"btnModifyCancel"]],handler:doSubmitModifyAccount});
	top.ymPrompt_addModalFocus("#btnModifyAccount");
}
function doSubmitModifyAccount(tp) {
	if (tp == 'yes') {
		top.ymPrompt.getPage().contentWindow.submitModify();
	} else {
		top.ymPrompt.close();
	}
}
</script>