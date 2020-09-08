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
<%@ include file="../../common/common.jsp"%>
</head>
<body>
<div class="pop-content">
   	<form id="uploadClientForm" class="form-horizontal" enctype="multipart/form-data" method="post" action="${ctx}/app/clientManage/uploadClient">
        <input type="hidden" id="appId" name="appId" value="<c:out value='${appId}'/>" />
        <div class="control-group">
            <label class="control-label" for="input"><em>*</em><spring:message code="clientManage.type"/></label>
            <div class="controls">
                <select class="span4"  id="type" name="type">
	        		<c:forEach items="${typeList}" var="type">
	        			<c:choose>
	        				<c:when test="${type.type == 'PC'}">
		        				<option value="<c:out value='${type.type}'/>" <c:if test="${curType == type.type}">selected="selected"</c:if>>
		        					<spring:message code="clientManage.pc"/>
		        				</option>
	        				</c:when>
	        				<c:when test="${type.type == 'Pccloud'}">
		        				<option value="<c:out value='${type.type}'/>" <c:if test="${curType == type.type}">selected="selected"</c:if>>
		        					<spring:message code="clientManage.pccloud"/>
		        				</option>
	        				</c:when>
	        				<c:when test="${type.type == 'Web'}">
	        				</c:when>
	        				<c:otherwise>
		        				<option value="<c:out value='${type.type}'/>" <c:if test="${curType == type.type}">selected="selected"</c:if>>
		        					<c:out value='${type.type}'/>
		        				</option>
	        				</c:otherwise>
	        			</c:choose>
	        		</c:forEach>
			    </select>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="input"><em>*</em><spring:message code="clientManage.versionNumber"/>:</label>
            <div class="controls">
                <input class="span4" type="text" id="version" name="version" value="" maxlength="64"/>
                <span class="validate-con bottom"><div></div></span>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="input"><em>*</em><spring:message code="clientManage.matchingSystem"/>:</label>
            <div class="controls">
                <input class="span4" type="text" id="supportSys" name=supportSys value="" maxlength="255"/>
                <span class="validate-con bottom"><div></div></span>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="input"><em>*</em><spring:message code="clientManage.downloadAddress"/>:</label>
            <div class="controls">
                <input class="span4" type="text" id="downloadUrl" name="downloadUrl" value="" maxlength="255"/>
                <span class="validate-con bottom"><div></div></span>
                <span class="help-inline"><spring:message code="clientManage.installAddress"/></span>
            </div>
        </div>
        <div class="control-group" id="plistDownLoadDiv">
            <label class="control-label" for="input">Plist<spring:message code="clientManage.downloadAddress"/>:</label>
            <div class="controls">
                <input class="span4" type="text" id="plistDownloadUrl" name="plistDownloadUrl" value="" maxlength="255"/>
                <span class="validate-con bottom"><div></div></span>
                <span class="help-inline"><spring:message code="clientManage.installAddress"/></span>
            </div>
        </div>
        <div class="control-group" id="plistFileDiv">
            <label class="control-label" for="input">Plist<spring:message code="clientManage.installPackage"/>:</label>
            <div class="controls">
                <input type="file" name="plistFile" id="plistFile">
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="input"><em>*</em><spring:message code="clientManage.installPackage"/>:</label>
            <div class="controls">
                <input type="file" name="packageFile" id="packageFile">
                <span class="help-inline"><spring:message code="clientManage.installPackage.help"/></span>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="input"><spring:message code="clientManage.versionDescribe"/>:</label>
            <div class="controls">
                <input type="file" name="versionFile" id="versionFile">
                <span class="help-inline"><spring:message code="clientManage.updateContent"/></span>
            </div>
        </div>
        <div class="control-group" id="twoDimCodeDiv">
            <label class="control-label" for="input"><spring:message code="clientManage.twoDimCodeDescribe"/>:</label>
            <div class="controls">
                <input type="file" name="twoDimCode" id="twoDimCode">
                <span class="help-inline"><spring:message code="clientManage.twoDimCode.regulation"/></span>
                <span class="help-inline"><spring:message code="clientManage.twoDimCode.label"/></span>
            </div>
        </div>
        <input type="hidden" id="token" name="token" value="<c:out value='${token}'/>"/>
	</form>
</div>
<script type="text/javascript">
var saveState = "<c:out value='${saveState}'/>";
$(document).ready(function() {
	if(saveState == "success"){
		top.ymPrompt.close();
		top.handlePrompt("success",'<spring:message code="clientManage.uploadSucceed"/>');
    	top.window.frames[0].location = "${ctx}/app/clientManage/config/<c:out value='${appId}'/>";
	}else if(saveState == "fail"){
		top.ymPrompt_enableModalbtn("#btn-focus");
		handlePrompt("error",'<spring:message code="clientManage.uploadFaild"/>');
	}
	$("#uploadClientForm").validate({ 
		rules: { 
			   version:{
				   required:true, 
			       maxlength:[64]
			   },
			   supportSys:{
				   required:true, 
			       maxlength:[255]
			   },
			   downloadUrl:{
				   required:true, 
			       maxlength:[255]
			   }
		}
 	}); 
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
	switchClientType();
	$("#type").change(function(){
		switchClientType();
	});
});

function switchClientType(){
	if($("#type").val() == "PC"){
		$("#twoDimCodeDiv").css("display","none");
		$("#plistDownLoadDiv").css("display","none");
		$("#plistFileDiv").css("display","none");
	}
	if($("#type").val() == "Android"){
		$("#twoDimCodeDiv").css("display","block");
		$("#plistDownLoadDiv").css("display","none");
		$("#plistFileDiv").css("display","none");
	}
	if($("#type").val() == "IOS"){
		$("#twoDimCodeDiv").css("display","block");
		$("#plistDownLoadDiv").css("display","block");
		$("#plistFileDiv").css("display","block");
	}
}

function submitClientUpload(){
	if(!$("#uploadClientForm").valid()) {
        return false;
    }  
	var fileFullName = $("#packageFile").val();
	if(fileFullName == ""){
		handlePrompt("error","<spring:message code='clientManage.selectPackage'/>");
		return false;
	}
	var fileName = fileFullName.substring(fileFullName.lastIndexOf("\\")+1,fileFullName.length);
	if(fileName.indexOf(".") == -1){
		handlePrompt("error","<spring:message code='clientManage.errFormat'/>");
		return false;
	}
	var fileSuffix = fileName.substring(fileName.lastIndexOf(".")+1,fileName.length);
	if(fileSuffix != "zip" && fileSuffix != "ipa" && fileSuffix != "apk"){
		handlePrompt("error","<spring:message code='clientManage.errFormat'/>");
		return false;
	}
	
	var twoDimCode = $("#twoDimCode").val();
	if(null!=twoDimCode&&twoDimCode!="")
	{
		if(twoDimCode.lastIndexOf(".") == -1 && $("#type").val() != "PC"){
	 		handlePrompt("error","<spring:message code='clientManage.errFormatTwoDimCode'/>");
	 		return false;
	 	}
		var imageSuffix = twoDimCode.substring(twoDimCode.lastIndexOf(".") + 1,
				twoDimCode.length);
		if (imageSuffix != "bmp" && imageSuffix != "gif"
				&& imageSuffix != "jpg" && imageSuffix != "jpeg"
				&& imageSuffix != "png" && imageSuffix != "ico"
				&& imageSuffix != "icon" && imageSuffix != "BMP" && imageSuffix != "GIF"
					&& imageSuffix != "JPG" && imageSuffix != "JPEG"
						&& imageSuffix != "PNG" && imageSuffix != "ICO"
						&& imageSuffix != "ICON" && $("#type").val() != "PC") {
			handlePrompt("error",
					'<spring:message code="clientManage.errFormatTwoDimCode"/>');
			return false;
		}
	}
	var plistDownloadUrl = $("#plistDownloadUrl").val();
	var plistFile = $("#plistFile").val();
	if(null!=plistFile&&plistFile!=""&&$("#type").val() == "IOS")
	{
		if(plistFile.lastIndexOf(".") == -1){
	 		handlePrompt("error","<spring:message code='clientManage.errFormatPlistFile'/>");
	 		return false;
	 	}
		var plistFileSuffix = plistFile.substring(plistFile.lastIndexOf(".") + 1,
	 			plistFile.length);
		if (plistFileSuffix != "plist" && plistFileSuffix != "PLIST") {
			handlePrompt("error", "<spring:message code='clientManage.errFormatPlistFile'/>");
			return false;
	}
	}
		var vFileFullName = $("#versionFile").val();
		if (vFileFullName != "") {
			var vFileName = vFileFullName.substring(vFileFullName
					.lastIndexOf("\\") + 1, vFileFullName.length);
			if (vFileName.indexOf(".") == -1) {
				handlePrompt("error",
						'<spring:message code="clientManage.errDescribe"/>');
				return false;
			}
			var vFileSuffix = vFileName.substring(
					vFileName.lastIndexOf(".") + 1, vFileName.length);
			if (vFileSuffix != "ini" && vFileSuffix != "txt") {
				handlePrompt("error",
						'<spring:message code="clientManage.errDescribe"/>');
				return false;
			}
		}

		top.ymPrompt_disableModalbtn("#btn-focus");
		$("#uploadClientForm").submit();
	}
</script>
</body>
</html>
