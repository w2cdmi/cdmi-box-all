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
   	<form class="form-horizontal" id="changeStorageForm">
        <div class="control-group">
        	<label class="control-label" for=""><em>*</em><spring:message code="clusterManage.domainName"/></label>
            <div class="controls">
                <input type="text" id="domain" class="span4" name="domain" value="${cse:htmlEscape(storageResource.domain)}"/>
                <span class="validate-con bottom"><div></div></span>
                <span class="help-block"><spring:message code="cluster.user.dns.assa.uds"/></span>
            </div>
        </div>
        <div class="control-group">
        	<label class="control-label" for=""><em>*</em><spring:message code="clusterManage.httpPort"/></label>
            <div class="controls">
                <input type="text" id="port" class="span4" name="port" value="${cse:htmlEscape(storageResource.port)}"/>
                <span class="validate-con bottom"><div></div></span>
            </div>
        </div>
        <input type="hidden" id="dcId" name="dcId" value="${cse:htmlEscape(dcId)}"/>
        <input type="hidden" id="fsId" name="fsId" value="${cse:htmlEscape(storageResource.fsId)}"/>
        <input type="hidden" id="token" name="token" value="${cse:htmlEscape(token)}"/>	
	</form>
    </div>
</div>
<script type="text/javascript">
$(document).ready(function() {
	$("#changeStorageForm").validate({ 
		rules: { 
			   domain:{
				   required:true, 
				   maxlength:[255]
			   },
			   port: { 
				   required:true, 
			       digits:true,
			       min:1,
			       max:65535
			   },
			   httpsport: { 
				   required:true, 
			       digits:true,
			       min:1,
			       max:65535
			   }
		}
 	}); 
 	
	if(!placeholderSupport()){
		placeholderCompatible();
	};
});
function submitStorage() {
	if(!$("#changeStorageForm").valid()) {
        return false;
    }  
	top.ymPrompt_disableModalbtn("#btn-focus");
	inLayerLoading('<spring:message code="cluster.memory.loading"/>',"loading-bar");
	$.ajax({
        type: "POST",
        url:"${ctx}/cluster/dcdetailmanage/changeUDSStorage",
        data:$('#changeStorageForm').serialize(),
        error: function(request) {
        	top.ymPrompt_enableModalbtn("#btn-focus");
        	unLayerLoading();
        	handlePrompt("error",'<spring:message code="common.saveFail"/>');
        },
        success: function() {
        	unLayerLoading();
        	top.ymPrompt.close();
        	top.handlePrompt("success",'<spring:message code="common.saveSuccess"/>');
        	top.window.frames[0].location = "${ctx}/cluster/dcdetailmanage/${cse:htmlEscape(dcId)}";
        }
    });
}
</script>
</body>
</html>
