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
   	<form class="form-horizontal" id="createRegionForm">
        <input type="hidden" id="status" name="status" value="true" />
        <div class="control-group">
        	<label class="control-label" for=""><em>*</em><spring:message code="common.title"/>:</label>
            <div class="controls">
                <input type="text" id="name" name="name" class="span5" placeholder='<spring:message code="cluster.domain.describe"/>'/>
                <span class="validate-con bottom"><div></div></span>
            </div>
            <label class="control-label" for=""><em>*</em><spring:message code="storage.region.code"/>:</label>
            <div class="controls">
                <input type="text" id="code" name="code" class="span5" placeholder='<spring:message code="cluster.domain.code"/>'/>
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
	           
	           /* var validName = /^[a-zA-Z][a-zA-Z0-9_]*$/; */
	           return validName.test(value);   
	       }, 
	       $.validator.format('<spring:message code="region.name.rule"/>')
); 
$(document).ready(function() {
	$("#createRegionForm").validate({ 
		rules: {
			   name:{
				   required:true, 
				   maxlength : [ 64 ]
			   },
			   code:{
				   required:true, 
				   isRegionName:true,
				   rangelength:[1,50]
			   }
		}
    }); 
    
	if(!placeholderSupport()){
		placeholderCompatible();
	};
});
function submitRegion() {
	if(!$("#createRegionForm").valid()) {
        return false;
    }  
	$.ajax({
        type: "POST",
        url:"${ctx}/cluster/region/create",
        data:$('#createRegionForm').serialize(),
        error: function(request) {
        	handlePrompt("error",'<spring:message code="common.createFail"/>',null,10);
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
