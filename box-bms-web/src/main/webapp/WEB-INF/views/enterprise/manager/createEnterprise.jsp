<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
<%@ include file="../../common/common.jsp"%>
</head>
<body>
<div class="pop-content">
	<div class="form-con">
   	<form class="form-horizontal" id="creatEnterpriseForm" name="creatEnterpriseForm">
	        <div class="control-group">
	        	<label class="control-label" for=""><em>*</em><spring:message code="enterpriseList.name"/>:</label>
	            <div class="controls">
	                <input type="text" id="name" name="name" class="span4" />
	                <span class="validate-con bottom"><div></div></span>
	            </div>
	        </div>
	         <div class="control-group">
	        	<label class="control-label" for=""><em>*</em><spring:message code="enterpriseList.domainName"/>:</label>
	            <div class="controls">
	                <input type="text" id="domainName" name="domainName" class="span4" />
	                <span class="validate-con bottom"><div></div></span>
	                 <span class="help-block" ><spring:message code="enterprise.create.title.domain"/></span>
	            </div>
	        </div>
	        <div class="control-group">
	        	<label class="control-label" for=""><em>*</em><spring:message code="enterpriseList.contactEmail"/>:</label>
	            <div class="controls">
	                <input type="text" id="contactEmail" name="contactEmail" class="span4" />
	                <span class="validate-con bottom"><div></div></span>
	                <span class="help-block" ><spring:message code="enterprise.create.title.email"/></span>
	            </div>
	        </div>
	        <div class="control-group">
	        	<label class="control-label" for=""><spring:message code="enterpriseList.contactPerson"/>:</label>
	            <div class="controls">
	                <input type="text" id="contactPerson" name="contactPerson" class="span4" />
	                <span class="validate-con bottom"><div></div></span>
	            </div>
	        </div>
	         <div class="control-group">
	        	<label class="control-label" for=""><spring:message code="enterpriseList.contactPhone"/>:</label>
	            <div class="controls">
	                <input type="text" id="contactPhone" name="contactPhone" class="span4" />
	                <span class="validate-con bottom"><div></div></span>
	            </div>
	        </div>
	         <div class="control-group">
	        	<label class="control-label" for=""><spring:message code="enterprise.organizational.switch"/>:</label>
	               <div class="controls" >
			            <label class="checkbox inline"><input type="checkbox" id="organizationalCheck" name="organizationalCheck"/><spring:message code="log.save.yes"/></label>
			            <input class="span3" type="hidden" id="isdepartment" name="isdepartment" value="0" />
	               </div>  
	        </div>
        <input type="hidden" id="token" name="token" value="<c:out value='${token}'/>"/>	
	</form>
    </div>
</div>
<script type="text/javascript">  
$("#organizationalCheck").click(function(){
	if(this.checked){
		ymPrompt.confirmInfo({
			title : '<spring:message code="enterprise.organizational.switch"/>',
			message : '<spring:message code="enterprise.organizational.switch"/>'
					+ '<br/>'
					+ '<spring:message code="enterprise.org.switch.warn"/>',
			width : 450,
			closeTxt : '<spring:message code="common.close"/>',
			handler : function(tp) {
				if (tp == "ok") {
					$("#isdepartment").val("1");
				}else{
					$("#organizationalCheck").attr("checked",false);
				}
			},
			btn : [
					[ '<spring:message code="common.OK"/>', "ok" ],
					[ '<spring:message code="common.cancel"/>',
							"cancel" ] ]
		});
	}else{
		$("#isdepartment").val("0");
	}
});


$(document).ready(function() {
		$("#creatEnterpriseForm").validate({ 
			rules: { 
				   name:{
					   required:true, 
					   maxlength:[255]
				   },
				   domainName: { 
					   required:true,
					   domainNameCheck:true,
					   maxlength:[64]
				   },
				   contactEmail: {
					   required:true, 
					   isValidEmail:true,
					   maxlength:[64]
				   },
				   contactPerson:{
					   maxlength:[255]
				   },
				   contactPhone: {
					   contactPhoneCheck : true,
					   maxlength:[255]
				   }
			},
			messages : {
				contactPhone : {
					contactPhoneCheck : '<spring:message  code="enterpriseList.contactPhone.rule"/>'
				}
			},
	    }); 
		$.validator.addMethod(
				"contactPhoneCheck", 
				function(value, element) {  
					var pattern = /^[0-9 +-]*$/;
				    if(!pattern.test(value)){
				 	   return false;
				    }
				    return true;
				}
		); 
		
		$.validator.addMethod(
				"domainNameCheck", 
				function(value, element) {  
					var pattern1 = /^[a-zA-Z0-9-_]*$/; 
				    if(!pattern1.test(value)){
				 	   return false;
				    }
				    return true;
				},
				$.validator.format('<spring:message code="domainname.key.rule"/>')
		); 
});

function submitCreateEnterprise() {
	if(!$("#creatEnterpriseForm").valid()) {
        return false;
    }
    $.ajax({
              type: "POST",
              url:"${ctx}/enterprise/manager/createLocal",
              data:$('#creatEnterpriseForm').serialize(),
              error: function(request) {
	              errorPrompt(request);
              },
              success: function(data) {
	              top.ymPrompt.close();
	              top.handlePrompt("success",'<spring:message code="common.createSuccess"/>');
	              top.document.getElementById("enterpriseManageTabId").click();
              }
         });
   
}

function errorPrompt(request)
{
	var errMessage = request.statusText;
	switch(errMessage)
	{
	  case "Conflict":
		  handlePrompt("error",'<spring:message code="createEnterprise.conflict.domain.email"/>');
		break;
	  case "Bad Request":
		  handlePrompt("error",'<spring:message code="createEnterprise.create.admin.fail"/>');
		break;
	 default:
		  handlePrompt("error",'<spring:message code="admin.create.err"/>');
	    break;
	}
}

</script>
</body>
</html>
