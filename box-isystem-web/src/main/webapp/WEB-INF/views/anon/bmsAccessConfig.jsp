<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cse" uri="http://cse.huawei.com/custom-function-taglib"%>  
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
<div class="sys-content">
		<%-- <div class="alert"><i class="icon-lightbulb"></i><spring:message code="accessConfig.lightbulb"/></div> --%>
		<div class="form-horizontal form-con clearfix">
   	<form id="accessAddrConfigForm" class="form-horizontal" method="post">
        <div class="control-group">
                <label class="control-label" for="input"><em>*</em><spring:message code="accessConfig.uamAddress"/></label>
	            <div class="controls">
	                <input class="span4" type="text" id="uamOuterAddress" name="uamOuterAddress" value="${accessAddress.uamOuterAddress}" placeholder='<spring:message code="accessConfig.uamAddress.clview"/>' />
	                <span class="validate-con"><div></div></span> 
	                <!-- <div class="help-inline"><a href="javascript:void(0)" onclick="showMoive()" ><i class="icon-question-sign icon-green"></i></a></div>  -->
	                <span class="help-block"> <spring:message code="accessConfig.helpAccess"/></span> 
	            </div>  
	                                
        </div>
        <div class="control-group">
                <label class="control-label" for="input"><em>*</em><spring:message code="accessConfig.uamInnerAddress"/></label>
	            <div class="controls">
	                <input class="span4" type="text" id="uamInnerAddress" name="uamInnerAddress" value="${accessAddress.uamInnerAddress}" placeholder='<spring:message code="accessConfig.uamAddress.clview"/>' />
	                <span class="validate-con"><div></div></span> 
	                <!-- <div class="help-inline"><a href="javascript:void(0)" onclick="showMoive()" ><i class="icon-question-sign icon-green"></i></a></div>  -->
	                <span class="help-block"> <spring:message code="accessConfig.uamInner.helpAccess"/></span> 
	            </div>  
	                                
        </div>
        <div class="control-group">
                <label class="control-label" for="input"><em>*</em><spring:message code="accessConfig.ufmOuterAddress"/></label>
	            <div class="controls">
	                <input class="span4" type="text" id="ufmOuterAddress" name="ufmOuterAddress" value="${accessAddress.ufmOuterAddress}" placeholder='<spring:message code="accessConfig.ufmAddress.clview"/>' />
	                <span class="validate-con"><div></div></span>
	                <span class="help-block"> <spring:message code="accessConfig.helpFile"/></span>
	            </div>          
        </div>  
        <div class="control-group">
                <label class="control-label" for="input"><em>*</em><spring:message code="accessConfig.ufmInnerAddress"/></label>
	            <div class="controls">
	                <input class="span4" type="text" id="ufmInnerAddress" name="ufmInnerAddress" value="${accessAddress.ufmInnerAddress}" placeholder='<spring:message code="accessConfig.ufmAddress.clview"/>' />
	                <span class="validate-con"><div></div></span>
	                <span class="help-block"><spring:message code="accessConfig.helpSystem"/></span>
	            </div>          
        </div>  
        <div class="control-group">
            <div class="controls">
            	<button id="submit_btn" type="button" onClick="saveAccessAddrConfig()" class="btn btn-primary"><spring:message code="common.save"/></button>
            </div>
        </div>
	</form>
</div>
</div>
<script type="text/javascript">
$(document).ready(function() {
	$("#accessAddrConfigForm").validate({ 
		rules: { 
			   uamOuterAddress:{
				   required:true, 
				   maxlength:[255]
			   },
			   uamInnerAddress:{
				   required:true, 
				   maxlength:[255]
			   },
			   ufmOuterAddress: { 
				   required:true, 
				   maxlength:[255]
			   },
			   ufmInnerAddress: { 
				   required:true, 
				   maxlength:[255]
			   }
		}
    }); 
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
	if (!placeholderSupport()) {
		placeholderCompatible();
	};
});
function saveAccessAddrConfig(){
	if(!$("#accessAddrConfigForm").valid()) {
        return false;
    }  
	$.ajax({
		type: "POST",
		url:"${ctx}/systeminit/accessAddress/save",
		data:$('#accessAddrConfigForm').serialize(),
		error: function(request) {
			top.handlePrompt("error",'<spring:message code="common.saveFail"/>');
		},
		success: function() {
			top.handlePrompt("success",'<spring:message code="common.saveSuccess"/>');
			//window.location.reload();
		}
	});
	
	/* var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH); */
}




</script>
</body>
</html>
