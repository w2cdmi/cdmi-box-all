<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<%@ page import="org.apache.shiro.authc.ExcessiveAttemptsException"%>
<%@ page import="org.apache.shiro.authc.IncorrectCredentialsException"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="Cache-Control" content="no-cache" />
<meta http-equiv="Pragma" content="no-cache" />
<script type="text/javascript"> 

</script> 
<title><spring:message code="main.title" /></title>
<%@ include file="../common/common.jsp"%>
<link rel="shortcut icon" type="image/x-icon" href="${ctx}/static/skins/default/img/logo.ico">
<script src="${ctx}/static/js/public/JQbox-hw-grid.js" type="text/javascript"></script>
<script src="${ctx}/static/js/public/JQbox-hw-page.js" type="text/javascript"></script>
</head>
<body>
<div class="sys-content">
	<div class="alert"><i class="icon-lightbulb icon-orange"></i><spring:message code="license.confirm.cliew"/></div>
	<div class="form-horizontal form-con clearfix">
		<div class="control-group">
            <label class="control-label" for="input"><em>*</em><spring:message code="license.import.file"/></label>
            <div class="controls">
            <form id="uploadLicenseForm" class="form-horizontal" enctype="multipart/form-data" method="post" action="${ctx}/authorize/uploadLicense">
               	 <input type="hidden" name="token" value="${cse:htmlEscape(token)}"/>
                <input class="span4" type="file" id="server" name="licenseFile" value="" />
                <span class="validate-con"><div></div></span>
                <button id="submit_btn" type="button" onclick="uploadLicense()" class="btn btn-primary btn-small"><spring:message code="common.upload"/></button>
                <c:if test="${not empty licenseInfo}">
                 <button id="export_btn" type="button" onclick="exportLicense();" class="btn btn-primary btn-small"><spring:message code="license.export"/></button>
                 </c:if>
            </form>    
            </div>
        </div>
	

   	<form id="lincenseForm" name="lincenseForm" class="form-horizontal">
	<h5><spring:message code="license.current.info"/></h5>
	
	<div class="form-horizontal form-con clearfix">
		<div class="form-left form-left-en">
	         <div class="control-group">
	            <label for="input" class="control-label"><spring:message code="license.product.name"/></label>
	            <div class="controls">
	                <span class="uneditable-input span4">${cse:htmlEscape(licenseInfo.productName)}</span>
	            </div>
	        </div>
	         <div class="control-group">
	            <label for="input" class="control-label"><spring:message code="license.country"/></label>
	            <div class="controls">
	                <span class="uneditable-input span4">${cse:htmlEscape(licenseInfo.country)}</span>
	            </div>
	        </div>
	         <div class="control-group">
	            <label for="input" class="control-label"><spring:message code="license.part"/></label>
	            <div class="controls">
	                <span class="uneditable-input span4">${cse:htmlEscape(licenseInfo.office)}</span>
	            </div>
	        </div>
	        <div class="control-group">
	            <label for="input" class="control-label"><spring:message code="license.usernumber"/></label>
	            <div class="controls">
	                <span class="uneditable-input span4">${cse:htmlEscape(licenseInfo.users)}</span>
	            </div>
	        </div>
	        <div class="control-group">
	            <label for="input" class="control-label"><spring:message code="license.codeNum"/></label>
	            <div class="controls">
	                <span class="uneditable-input span4">${cse:htmlEscape(licenseInfo.lsn)}</span>
	            </div>
	        </div>
	        <div class="control-group">
	            <label for="input" class="control-label"><spring:message code="license.extend.teamspace"/></label>
	            <div class="controls">
	                <span class="uneditable-input span4">${cse:htmlEscape(licenseInfo.teamSpaceNumber)}</span>
	            </div>
	        </div>
	        <div class="control-group">
			  	<label for="input" class="control-label"><spring:message code="license.ESN.info"/></label>
	            <div class="controls">
	            	<span class="uneditable-input uneditable-input-multi" style="width:240%;">${cse:htmlEscape(esnString)}</span>
            	</div>
         	</div>
	    </div>
	    
	    <div class="form-right form-right-en">
	        <div class="control-group">
	            <label for="input" class="control-label"><spring:message code="license.version"/></label>
	            <div class="controls">
	                <span class="uneditable-input span4">${cse:htmlEscape(licenseInfo.productVersion)}</span>
	            </div>
	        </div>
	        <div class="control-group">
	            <label for="input" class="control-label"><spring:message code="license.create.time"/></label>
	            <div class="controls">
	                <span class="uneditable-input span4">${cse:htmlEscape(licenseInfo.createTimeStr)}</span>
	            </div>
	        </div>
	        <div class="control-group">
	            <label for="input" class="control-label"><spring:message code="license.deadline"/></label>
	            <div class="controls">
	                <span class="uneditable-input span4">${cse:htmlEscape(licenseInfo.deadline)}</span>
	            </div>
	        </div>
	        <div class="control-group">
	            <label for="input" class="control-label"><spring:message code="license.client"/></label>
	            <div class="controls">
	                <span class="uneditable-input span4">${cse:htmlEscape(licenseInfo.costumer)}</span>
	            </div>
	        </div>
	        <div class="control-group">
	            <label for="input" class="control-label"><spring:message code="license.teamspace"/></label>
	            <div class="controls">
	                <span class="uneditable-input span4">${cse:htmlEscape(defaultTeams)}</span>
	            </div>
	        </div>
	        
        </div>
        <div class="clearfix"></div>
    </div>
    
    </form>
	</div>
	
</div>

<div class="sys-content">
	<div><button id="btnRefresh" type="button" class="btn" onClick="listVersion();"><spring:message code="common.refurbish"/></button></div>
 	<div class="table-con clearfix" id="nodeList">
    </div>	
    </div>
    </div>
</div>
</body>
</html>
<script type="text/javascript">

var opts_viewGrid = null;
var catalogData = null;
var headData = {
		"lastModified" : {"title":'<spring:message code="license.modiffy.time"/>',"width" : "15%"},
		"nodeAddress" : {"title":'<spring:message code="license.ip"/>',"width" : "20%"},
		"esn" : {"title":"ESN", "width" : ""},
		"serverType" : {"title":'<spring:message code="license.server.type"/>', "width" : ""},
		"status" : {"title":'<spring:message code="license.status.type"/>',"width" : "20%"},
		"licenseId" : {"title":"LicenseId", "width" : ""}
		};
$(function(){
	<%--  初始化列表 --%>
    opts_viewGrid = $("#nodeList").comboTableGrid({
		headData : headData,
		ItemOp : "user-defined",
		height : 500,
		dataId : "id"
	});
   
    <%-- 对外单元格修改接口方法  --%>
    $.fn.comboTableGrid.setItemOp = function(tableData, rowData, tdItem, colIndex){
    	switch (colIndex) {
			case "lastModified":
				var dateItem = parseFloat(tdItem.find("p").text());
				var showItm = getSmpFormatDate(new Date(dateItem));
				tdItem.find("p").text(showItm);
				tdItem.attr("title",showItm);
				break;
			case "nodeAddress":
				break;
			case "esn":
				break;
			case "serverType":
				var dataItm = tdItem.find("p").text();
				if(dataItm == 0){
					tdItem.find("p").text('<spring:message code="license.UASnode"/>');
					tdItem.attr("title",'<spring:message code="license.UASnode"/>');
				}else if(dataItm == 1){
					tdItem.find("p").text('<spring:message code="license.DssNode"/>');
					tdItem.attr("title",'<spring:message code="license.DssNode"/>');
				}
				break;
			case "status":
				var dataItm = tdItem.find("p").text();
				if(dataItm == 0){
					tdItem.find("p").text('<spring:message code="license.verify.fail"/>');
					tdItem.attr("title",'<spring:message code="license.verify.fail"/>');
				}else if(dataItm == 1){
					tdItem.find("p").text('<spring:message code="common.normal"/>');
					tdItem.attr("title",'<spring:message code="common.normal"/>');
				}else if(dataItm == 2){
					tdItem.find("p").text('<spring:message code="license.node.over.limit"/>');
					tdItem.attr("title",'<spring:message code="license.node.over.limit"/>');
				}else if(dataItm == 3){
					tdItem.find("p").text('<spring:message code="license.updating.fail"/>');
					tdItem.attr("title",'<spring:message code="license.updating.fail"/>');
				} 
				break;
			default : 
				break;
		}
    	
    }
	listVersion();
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
	
})
$(document).ready(
	function() {
		if(''!='${message}'){	
			handlePrompt("error","<spring:message code='${message}'/>");
		}
	}
)

function uploadLicense(){
	$("#uploadLicenseForm").submit();
}



function listVersion(){
	$("#btnRefresh").attr("disabled", "disabled");
    var url = "${ctx}/authorize/listLicenseNode";
    $.ajax({
        type: "POST",
        url: url,
        data:{"token" : "${cse:htmlEscape(token)}"},
        error: function() {
        	handlePrompt("error","<spring:message code='file.errorMsg.listFileVersionFailed'/>");
        	$("#btnRefresh").removeAttr("disabled");
        },
        success: function(data) {
			$("#nodeList").setTableGridData(data, opts_viewGrid);
			$("#btnRefresh").removeAttr("disabled");
        }
    });
}

function exportLicense(){
	if(isIeBelow11()){
		top.ymPrompt.alert({title:'<spring:message code="common.title.info"/>',message:'<spring:message code="liecense.download.ie.error" />'});
		var url = "${ctx}/authorize/exportLicense?t="+new Date().toString();
		window.open(url);
		return;
	}
	else{
		var url = "${ctx}/authorize/exportLicense?t="+new Date().toString();
		window.open(url);
	}
}

function isIeBelow11(){
	if(navigator.userAgent.indexOf("MSIE") < 0) {
		return false;
	}else if(navigator.userAgent.indexOf("MSIE 10.0") >= 0) {
		return true;
	}else if(navigator.userAgent.indexOf("MSIE 9.0") >= 0) {
		return true;
	}else if(navigator.userAgent.indexOf("MSIE 8.0") >= 0) {
		return true;
	}else if(navigator.userAgent.indexOf("MSIE 8.0") >= 0) {
		return true;
	}else{
		return false;
	}
}

</script>