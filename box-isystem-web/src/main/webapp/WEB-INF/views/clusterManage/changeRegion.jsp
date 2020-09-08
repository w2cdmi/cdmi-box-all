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
   	<form class="form-horizontal" id="changeRegionForm">
        <input type="hidden" id="id" name="id" value="${cse:htmlEscape(region.id)}" />
        <div class="control-group">
        	<label class="control-label" for=""><em>*</em><spring:message code="common.title"/>:</label>
            <div class="controls">
                <input type="text" id="name" name="name" value="${cse:htmlEscape(region.name)}" class="span4"/>
                <span class="validate-con bottom"><div></div></span>
            </div>
        </div>
        <input type="hidden" id="token" name="token" value="${cse:htmlEscape(token)}"/>
	</form>
    </div>
</div>
<script type="text/javascript">
$.validator.addMethod(
		   "isRegionName", 
		   function(value, element) {   
	           var validName = /^[a-zA-Z]{1}[a-zA-Z0-9]*$/;   
	           return validName.test(value);   
	       }, 
	       $.validator.format('<spring:message code="region.name.rule"/>')
); 
$(document).ready(function() {
	
	$("#changeRegionForm").validate({ 
		rules: { 
			   name:{
				   required:true, 
				   isRegionName:true,
				   rangelength:[1,50]
			   }
		}
    }); 
});
function submitRegion() {
	if(!$("#changeRegionForm").valid()) {
        return false;
    } 
	$.ajax({
        type: "POST",
        url:"${ctx}/cluster/region/change",
        data:$('#changeRegionForm').serialize(),
        error: function(request) {
        	handlePrompt("error",'<spring:message code="common.modifyFail"/>',null,10);
        },
        success: function() {
        	top.ymPrompt.close();
        	top.handlePrompt("success",'<spring:message code="common.modifySuccess"/>');
        	top.document.getElementById("regionManager").click();
        }
    });
}
</script>
</body>
</html>
