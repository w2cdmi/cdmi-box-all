<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="Cache-Control" content="no-cache" />
<meta http-equiv="Pragma" content="no-cache" />
<title></title>
<%@ include file="../common/common.jsp"%>
</head>
<body>
<div class="pop-content">
	<div class="form-con">
   	<form class="form-horizontal" id="creatDcForm">
        <div class="control-group">
        	<label class="control-label" for=""><em>*</em><spring:message code="common.title"/>:</label>
            <div class="controls">
                <input type="text" id="name" name="name" class="span4"/>
                <span class="validate-con bottom"><div></div></span>
            </div>
        </div>
        <div class="control-group">
        	<label class="control-label" for=""><em>*</em><spring:message code="clusterManage.manageIp"/>:</label>
            <div class="controls">
                <input type="text" id="manageIp" name="manageIp" class="span4" placeholder='<spring:message code="cluster.subsystem.ip.address"/>'/>
                <span class="validate-con bottom"><div></div></span>
            </div>
        </div>
        <input type="hidden" id="region" name="region.id" value="${cse:htmlEscape(regionId)}">
        <input type="hidden" id="hiddenManageIp" name="resourceGroup.manageIp" />
        <input type="hidden" id="hiddenDomainName" name="resourceGroup.domainName" />
        <input type="hidden" id="token" name="token" value="${cse:htmlEscape(token)}"/>	
	</form>
    </div>
</div>
<script type="text/javascript">
$.validator.addMethod(
		   "isDCName", 
		   function(value, element) {   
	           var validName = /^[a-zA-Z]{1}[a-zA-Z0-9]*$/;   
	           return validName.test(value);   
	       }, 
	       $.validator.format('<spring:message code="dataceter.name.rule"/>')
); 
$(document).ready(function() {
	$("#creatDcForm").validate({ 
		rules: { 
			   name:{
				   required:true, 
				   rangelength:[3,128],
			   },
			   manageIp: { 
				   required:true, 
				   rangelength:[1,45]
			   },
			   managePort: { 
				   required:true, 
			       digits:true,
			       rangelength:[1,10]
			   },
			   domain: { 
				   rangelength:[1,128]
			   } 
		}
    }); 
    
	if(!placeholderSupport()){
		placeholderCompatible();
	};
});
function submitDc() {
	if(!$("#creatDcForm").valid()) {
        return false;
    }
	$("#hiddenManageIp").val($("#manageIp").val());
	$("#hiddenDomainName").val($("#domain").val());
	$.ajax({
        type: "POST",
        url:"${ctx}/cluster/dcmanage/create",
        data:$('#creatDcForm').serialize(),
        error: function(request) {
        	handlePrompt("error",'<spring:message code="common.createFail"/>');
        },
        success: function() {
        	top.ymPrompt.close();
        	top.handlePrompt("success",'<spring:message code="common.createSuccess"/>');
        	top.document.getElementById("regionManager").click();
        }
    });
}
</script>
</body>
</html>
