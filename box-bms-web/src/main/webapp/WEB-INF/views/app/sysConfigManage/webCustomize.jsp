<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page import="pw.cdmi.box.uam.util.CSRFTokenManager"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%
request.setAttribute("token", CSRFTokenManager.getTokenForSession(session));
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title></title>
<%@ include file="../../common/common.jsp"%>
</head>
<body>
<div class="sys-content sys-content-en">
	<div class="clearfix control-group">
		<a  class="return btn btn-small pull-right" href="${ctx}/app/appmanage/authapp/list"><i class="icon-backward"></i>&nbsp;<spring:message code="common.back"/></a>
		<h5 class="pull-left" style="margin: 3px 0 0 4px;"><a href="${ctx}/app/appmanage/authapp/list"><c:out value='${appId}'/></a>&nbsp;>&nbsp;<spring:message code="appSysConfig.app.config"/></h5>	
	</div>
	<ul class="nav nav-tabs">
    	<li><a class="return" href="${ctx}/app/basicconfig/config/<c:out value='${appId}'/>"><spring:message code="appSysConfig.basicconfig"/></a></li>
    	<c:if test="${appType == 1}">
        <li class="active"><a class="return" href="${ctx}/app/logo/config/<c:out value='${appId}'/>"><spring:message code="appSysConfig.logo.config"/> </a></li>
         </c:if>
    	<c:if test="${appType == 1}">
    	<li><a class="return" href="${ctx}/app/clientManage/config/<c:out value='${appId}'/>"><spring:message code="appSysConfig.clientManage"/></a></li>
    	 </c:if>
    	 <li><a class="return" href="${ctx}/admin/declaration/config/${appId}"><spring:message code="conceal.declaration"/> </a></li>
     	<c:if test="${appType == 1}">
     	<li><a class="return" href="${ctx}/app/backup/config/${appId}"><spring:message code="appSysConfig.backupManage"/> </a></li>
     	</c:if>
    </ul>
	<c:if test="${appType == 1}">
   	<form id="customizeFormOne" class="form-horizontal" enctype="multipart/form-data" method="post" action="">
   	<div class="form-con">
		<input type="hidden" id="appId" name="appId" value="<c:out value='${appId}'/>" />       
        <div class="control-group">
        	<div class="control-group">
	            <label class="control-label" for="input"><spring:message code="webCustomize.linkIsAnon"/>:</label>
	        	<div class="controls">
	            	<label class="checkbox"><input type="checkbox" id="linkIsAnon" name="linkIsAnon" value="" <c:if test="${appBasicConfig.linkIsAnon}">checked="checked"</c:if> /><spring:message code="user.manager.isCreateTeam"/></label>
	            </div>
       		</div>
            <label class="control-label" for="input"><em>*</em><spring:message code="webCustomize.linkCodeRule"/>:</label>
            <div class="controls">
                <label class="radio">
                <input type="radio" id="simpleLinkCode" name="disableSimpleLinkCode" <c:if test="${!securityConfig.disableSimpleLinkCode}">checked="checked"</c:if> value="false" /><spring:message code="webCustomize.simpleLinkCode"/>
                <span class="help-inline"> <spring:message code="webCustomize.webBoxCustomize"/></span>
                </label>                                
                                         
            </div>
            <div class="controls">
                <label class="radio">                                
                <input type="radio" id="complexLinkCode" name="disableSimpleLinkCode" <c:if test="${securityConfig.disableSimpleLinkCode}">checked="checked"</c:if> value="true" /><spring:message code="webCustomize.complexLinkCode"/>
                <span class="help-inline"> <spring:message code="webCustomize.systemGenerate"/></span>
                </label>   
            </div> 
        </div>
        <div class="control-group">
            <label class="control-label" for="input"><em>*</em><spring:message code="webCustomize.protocolType"/>:</label>
            <div class="controls">
                <label class="radio">
                <input type="radio" id="simpleLinkCode" name="protocolType" <c:if test="${securityConfig.protocolType eq 'http'}">checked="checked"</c:if> value="http" onclick="showHttpWarning()" />HTTP
                </label>                                
            </div>
            <div class="controls">
                <label class="radio">                                
                <input type="radio" id="complexLinkCode" name="protocolType" <c:if test="${!(securityConfig.protocolType eq 'http')}">checked="checked"</c:if> value="https" />HTTPS
                </label>   
            </div>  
        </div>
        <div class="control-group">
            <label class="control-label" for="input"><em>*</em><spring:message code="appConfig.forgetPwd.lable"/></label>
            <div class="controls">
                <label class="radio">
                <input type="radio" id="forgetPwd" name="forgetPwd" value="true" <c:if test="${securityConfig.forgetPwd  == true}">checked="checked"</c:if> /><spring:message code="appConfig.forgetPwd.option.yes"/>
                </label>                                
            </div>
            <div class="controls">
                <label class="radio">                                 
                <input type="radio" id="forgetPwd" name="forgetPwd" value="false" <c:if test="${securityConfig.forgetPwd != true}">checked="checked"</c:if> /><spring:message code="appConfig.forgetPwd.option.no"/>
                </label>   
            </div>  
        </div>
        <div class="control-group">
            <div class="controls">
            	<button id="submit_btn" type="button" onclick="saveSecurityConfig()" class="btn btn-primary"><spring:message code="common.save"/></button>
            </div>
        </div>
    </div>
    <input type="hidden" id="token" name="token" value="<c:out value='${token}'/>"/>
    </form>
     </c:if>
    <c:if test="${appType == 1}">
    <form id="customizeForm" class="form-horizontal" enctype="multipart/form-data" method="post" action="">
    <div class="form-con">
        <input type="hidden" id="appId" name="appId" value="<c:out value='${appId}'/>" /> 
        <div class="control-group">
            <label class="control-label" for="input"><em>*</em><spring:message code="webCustomize.chineseSystem"/>:</label>
            <div class="controls">
                <input class="span4" type="text" id="title" name="title" value="<c:out value='${customize.title}'/>" />&nbsp;&nbsp;(<spring:message code="webCustomize.chinese"/>)
                <span class="validate-con"><div></div></span>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="input"></label>
            <div class="controls">
                <input class="span4" type="text" id="titleEn" name="titleEn" value="<c:out value='${customize.titleEn}'/>" />&nbsp;&nbsp;(<spring:message code="webCustomize.english"/>)
                <span class="validate-con"><div></div></span>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="input"><em>*</em><spring:message code="webCustomize.systemDomain"/>:</label>
            <div class="controls">
                <input class="span4" type="text" id="domainName" name="domainName" value="<c:out value='${customize.domainName}'/>" placeholder='<spring:message code="webCustomize.accessDomain"/>'/>
                <span class="validate-con"><div></div></span>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="input"><spring:message code="webCustomize.appEmailTitle"/></label>
            <div class="controls">
                <input class="span4" type="text" id="appEmailTitle" name="appEmailTitle" value="<c:out value='${customize.appEmailTitle}'/>"/>
                <span class="validate-con"><div></div></span>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="input"><em>*</em><spring:message code="webCustomize.systemLogo"/>:</label>
           
            <c:if test="${customize.existLogo}">
            <div class="controls">
                <div class="set-logo-con">
                    <img alt="logo" src="${ctx}/syscommon/logo" />
                </div>
            </div>
            </c:if>
            <c:if test="${!customize.existLogo}">
            <div class="controls">
            	<div class="set-logo-con">
                	<img alt="logo" src="${ctx}/static/skins/default/img/logo-onebox.png" />
                </div>
            </div>
            </c:if>
             <div class="controls">
             	<span class="help-block"><spring:message code="webCustomize.suggestPicture"/></span>
                <input type="file" name="logoFile" id="logoFile" onchange="logeFileName()">
                <input type="hidden" id="logoFileValue" name="logoFileValue" />
            </div>
        </div>
        
        <div class="control-group">
        	<label class="control-label" for="input"><em>*</em><spring:message code="webCustomize.browserIdentification"/>:</label>
        	<c:if test="${customize.existIcon}">
            <div class="controls">
                <img alt="logo" src="${ctx}/syscommon/icon" width="16" height="16">
            </div>
            </c:if>
        	<c:if test="${!customize.existIcon}">
            <div class="controls">
                <img alt="logo" src="${ctx}/static/skins/default/img/logo.ico" width="16" height="16">
            </div>
            </c:if>
        	  <div class="controls">
                <span class="help-block"><spring:message code="webCustomize.suggestIcon"/></span>
                <input type="file" name="iconFile" id="iconFile" onchange="iconFileName()">
                <input type="hidden" id="iconFileValue" name="iconFileValue" />
            </div>
        </div>
        <div class="control-group">
            <div class="controls">
            	<button id="submit_btn" type="button" onclick="saveLogoCustom()" class="btn btn-primary"><spring:message code="common.save"/></button>
            </div>
        </div>
        <input type="hidden" id="token" name="token" value="<c:out value='${token}'/>"/>
	</form>
	 </c:if>
    </div>
</div>
<script type="text/javascript">
var saveState="<c:out value='${saveState}'/>";
var locationState="<c:out value='${whichOne}'/>";
$(document).ready(function() {
	var linkIsAnon="<c:out value='${securityConfig.linkIsAnon}'/>";
	if(linkIsAnon==true||linkIsAnon=="true")
	{
		$("#linkIsAnon").attr("checked",true);
		$("#linkIsAnon").val(true);
	}
	else
	{
		$("#linkIsAnon").removeAttr("checked");
		$("#linkIsAnon").val(false);
	}	

	if(saveState == "success"){
		top.handlePrompt("success",'<spring:message code="common.saveSuccess"/>');
	}else if(saveState == "fail"){
		top.handlePrompt("error",'<spring:message code="common.saveFail"/>');
	}else if(saveState == "errorType"){
		top.handlePrompt("error",'<spring:message code="webCustomize.illegalFormat"/>');
	}
	if(locationState == "logoFile"){
		if(saveState == "ImageSizeException"){
			top.handlePrompt("error",'<spring:message code="logoFileIcon.warmSystemLogoSize"/>');
		}
	} 
	if(locationState == "iconFile"){
		if(saveState == "ImageSizeException"){
			top.handlePrompt("error",'<spring:message code="browserIcon.warmBrowserLogoSize"/>');
		}
	}
	
	$("#customizeForm").validate({ 
		rules: { 
			   title:{
				   required:true, 
			       maxlength:[120]
			   },
			   titleEn:{
				   required:true, 
			       maxlength:[120]
			   },
			   domainName: { 
				   required:true, 
			       maxlength:[150]
			   },
			   appEmailTitle: { 
			       maxlength:[255]
			   }
		}
 	}); 
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
	
	if(!placeholderSupport()){
		placeholderCompatible();
	};
});
$("#linkIsAnon").click(function(){ 
	if(this.checked){
		$("#linkIsAnon").val(true);
	}else{ 
		$("#linkIsAnon").val(false);
	}
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
});
function logeFileName()
{
	var logoFileName = $("#logoFile").val();
	var curType = logoFileName.substring(logoFileName.lastIndexOf("\\")+1);
	$("#logoFileValue").val(curType);
}
function iconFileName()
{
	var iconFileName = $("#iconFile").val();
	var curType = iconFileName.substring(iconFileName.lastIndexOf("\\")+1);
	$("#iconFileValue").val(curType);
}


function saveSecurityConfig(){
	$.ajax({
		type: "POST",
		url:"${ctx}/app/securityconfig/save",
		data:$('#customizeFormOne').serialize(),
		error: function(request) {
			top.handlePrompt("error",'<spring:message code="common.saveFail"/>');
		},
		success: function() {
			top.handlePrompt("success",'<spring:message code="common.saveSuccess"/>');
		}
	});
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
}
function saveLogoCustom(){
	if(!$("#customizeForm").valid()) {
        return false;
    }  
	var picTypes = ["bmp","gif","jpg","jpeg","png","ico","icon"];
	
	var logoFileName = $("#logoFile").val();
	if(logoFileName != ""){
		var formatValid = false;
		var curType = logoFileName.substring(logoFileName.lastIndexOf(".")+1);
		curType = curType.toLowerCase();
		for(idx in picTypes){
			if(curType == picTypes[idx]){
				formatValid = true;
				break;
			}
		}
		if(formatValid == false){
			top.handlePrompt("error",'<spring:message code="webCustomize.illegalFormat"/>');
			return;
		}
	}
	
	var iconFileName = $("#iconFile").val();
	if(iconFileName != ""){
		var formatValid = false;
		var curType = iconFileName.substring(iconFileName.lastIndexOf(".")+1);
		curType = curType.toLowerCase();
		for(idx in picTypes){
			if(curType == picTypes[idx]){
				formatValid = true;
				break;
			}
		}
		if(formatValid == false){
			top.handlePrompt("error",'<spring:message code="webCustomize.illegalFormat"/>');
			return;
		}
	}
	$("#customizeForm").attr("action", "${ctx}/app/logo");
	$("#customizeForm").submit();
}
function showHttpWarning(){
	top.ymPrompt.alert({title:'<spring:message code="common.warning"/>', message:'<spring:message code="webCustomize.httpWarning"/>', icoCls: 'ymPrompt_error'});
}
</script>
</body>
</html>
