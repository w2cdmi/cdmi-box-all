<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="org.apache.shiro.SecurityUtils"%>
<%@ page import="com.huawei.sharedrive.isystem.user.domain.Admin"%>
<%@ page import="com.huawei.sharedrive.isystem.util.Constants"%>
<%@ page import="com.huawei.sharedrive.isystem.util.CSRFTokenManager"%>
<%@ page import="com.huawei.sharedrive.isystem.util.DateTimeUtils"%>
<%@ page import="org.apache.shiro.session.Session"%>
<%@ page import="org.apache.shiro.SecurityUtils"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<META HTTP-EQUIV="Expires" CONTENT="0">
<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
<META HTTP-EQUIV="Cache-control"
	CONTENT="no-cache, no-store, must-revalidate">
<META HTTP-EQUIV="Cache" CONTENT="no-cache">
<%
	response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
	response.setHeader("Pragma", "no-cache");
	response.setDateHeader("Expires", 0);
	Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
	request.setAttribute("token", CSRFTokenManager.getTokenForSession(session));
	Session sesion = SecurityUtils.getSubject().getSession();
	boolean tag = (Boolean) sesion.getAttribute("tag");
%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<div class="header">
	<div class="header-con">
		<div class="logo pull-left">
			<a href="#"><spring:message code="main.title" /></a>
		</div>
		<div class="nav-menu">
			<ul id="downMenu">
				<shiro:hasRole name="ADMIN_MANAGER">
					<li><a class="menu-configer"><i></i> <spring:message
								code="isystem.isystem.config" /></a>
						<ul>
							<li id="enterpriseConfigLi"><span id="enterpriseConfig"
								onClick="enterpriseClick()"><spring:message
										code="isystem.bms.enterprise.manager.config" /></span></li>
							<!-- 
							<li id="licenseManagerLi"><span id="licenseManager" onClick="licenseClick()"><spring:message
										code="isystem.isystem.license.manager.config" /></span></li> -->
							<li id="storageManagerLi"><span id="storageManager" onClick="storageClick()"><spring:message
										code="isystem.isystem.storage.manager.config" /></span></li>
							<li id="accessAddressConfigLi"><span id="accessAddressConfig" onClick="accessClick()"><spring:message
										code="isystem.bms.interface.manager.config" /></span></li>
							<li id="mailConfigLi"><span id="mailConfig" onClick="mailConfigClick()"><spring:message
										code="isystem.mail.server.manager.config" /></span></li>
							<li id="adminConfigLi"><span id="adminConfig"
								onClick="adminConfigClick()"><spring:message
										code="isystem.isystem.user.manager.config" /></span></li>
						</ul></li>
				</shiro:hasRole>
			</ul>
		</div>
		<div class="header-R pull-right clearfix">
			<ul class="clearfix pull-right">
				<!-- <li class="pull-left dropdown">
            		<ul class="dropdown-menu pull-right"> -->
				<li id="langZH"><a href="?locale=zh_CN"><i
						class="icon-lang-zh"></i>简体中文</a></li>
				<li id="langEN"><a href="?locale=en_US"><i
						class="icon-lang-en"></i>English</a></li>
				<li class="divider"></li>
				<!-- 	</ul>
            	</li> -->
			</ul>
		</div>

	</div>
</div>
<script type="text/javascript">

	function disableBack() {
		window.history.forward();
	}
	disableBack();
	window.onload = disableBack;
	window.onpageshow = function(evt) {
		if (evt.persisted) {
			disableBack();
		}
	}
	window.onunload = function() {
		void (0);
	}
	
	$(function() {
		if ('<spring:message code="common.language1"/>' == "en") {
			$("#langEN").remove();
		} else {
			$("#langZH").remove();
		}
	})

	$(function() {
		$("#downMenu > li:first-child").addClass("active").find("ul").show();
		$("#downMenu > li:first-child").find("ul li:first-child").addClass(
				"active");
		$("#breadcrumbText").html(
				$("#downMenu > li:first-child").find("a").text()
						+ " > "
						+ $("#downMenu > li:first-child").find(
								"ul li:first-child span").text());

		$("#downMenu > li").click(function() {
			$(this).parent().find("ul").hide();
			$(this).find("ul").show();
		})
		$("#downMenu").find("span").first().click();
	})
	
	var result;
	
	function enterpriseClick(){
	    getConfigStep(1);
		var isconfigEnterprise = result.enterpriseConfig;
		if (isconfigEnterprise == 1) {
			openInframe("#enterpriseConfig", '${ctx}/systeminit/enterprise/config','systemFrame');
		}
	}

	function licenseClick() {
		getConfigStep(2);
		var isconfigEnterprise = result.enterpriseConfig;
		if (isconfigEnterprise == 1) {
			openInframe("#licenseManager", '${ctx}/systeminit/license/config', 'systemFrame');
		}
	}

	function storageClick() {
		 getConfigStep(3);
		
		var isconfigEnterprise =  result.enterpriseConfig;
		if (isconfigEnterprise == 1) {
			openInframe("#storageManager", '${ctx}/systeminit/storage/config', 'systemFrame');
		}
	}

	function accessClick() {
		getConfigStep(5);
		var isconfigEnterprise =  result.enterpriseConfig;
		var isConfigStorage =  result.storageConfig;
		if (isconfigEnterprise == 1 /* && isConfigStorage == 1 */) {
			openInframe("#accessAddressConfig", '${ctx}/systeminit/accessAddress/config',
					'systemFrame');
		}
	}

	function mailConfigClick() {
		getConfigStep(5);
		var isconfigEnterprise =   result.enterpriseConfig;
		var isConfigStorage =  result.storageConfig;
		var isconfigAccess = result.accessConfig;
		if (isconfigEnterprise == 1 /* && isConfigStorage == 1
				&& isconfigAccess == 1 */) {
			openInframe("#mailConfig", '${ctx}/systeminit/mail/config', 'systemFrame');
		}
	}

	function adminConfigClick() {
		getConfigStep(6);
		var isconfigEnterprise =   result.enterpriseConfig;
		var isConfigStorage =  result.storageConfig;
		var isconfigAccess = result.accessConfig;
		var isconfigMail = result.mailConfig;
		if (isconfigEnterprise == 1/*  && isConfigStorage == 1
				&& isconfigAccess == 1 && isconfigMail == 1 */) {
			openInframe("#adminConfig", '${ctx}/systeminit/isystem/admin/config',
					'systemFrame');
		}
	}
	
	function getConfigStep(step){
		$.ajax({
			type : "GET",
			async : false,
			url : "${ctx}/systeminit/config/step",
			success : function(data) {
				console.debug(data);
				result = data;
			}
		});
	}
	
</script>