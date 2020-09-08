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
        	<label class="control-label" for=""><em>*</em><spring:message code="clusterManage.storePath"/>:</label>
            <div class="controls">
                <input type="text" id="path" class="span4" name="path"  value="${cse:htmlEscape(storageResource.path)}" />
                <span class="validate-con bottom"><div></div></span>
                <span class="help-block"><spring:message code="clusterManage.assa.storePath"/></span>
            </div>
        </div>
        <div class="control-group">
        	<label class="control-label" for=""><em>*</em><spring:message code="clusterManage.maxUtilization"/>:</label>
            <div class="controls">
                <input type="text" id="maxUtilization" class="span4" name="maxUtilization" value="${cse:htmlEscape(storageResource.maxUtilization)}"/>&nbsp;%
                <span class="validate-con bottom"><div></div></span>
                <span class="help-block"><spring:message code="clusterManage.assa.threshold"/></span>
            </div>
        </div>
        <div class="control-group">
        	<label class="control-label" for=""><em>*</em><spring:message code="clusterManage.retrieval"/>:</label>
            <div class="controls">
                <input type="text" id="retrieval" class="span4" name="retrieval"  value="${cse:htmlEscape(storageResource.retrieval)}"/>&nbsp;%
                <span class="validate-con bottom"><div></div></span>
                <span class="help-block"><spring:message code="clusterManage.assa.retrieval"/></span>
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
	
	$.validator.addMethod(
			"isValidRationNum", 
			function(value, element) {   
				value = $.trim(value);
				var pattern = /^[1-9][0-9]*$/;
				if(!pattern.test(value)){
					return false;
			    }
				value = parseInt(value);
				if(value >=1 && value<=99){
					return true;
				}
			    return false;
			}
		);
	
	$("#changeStorageForm").validate({ 
		rules: { 
			   path:{
				   required:true, 
				   maxlength:[128]
			   },
			   maxUtilization: { 
				   required:true, 
				   isValidRationNum:true
			   },
			   retrieval: { 
				   required:true, 
				   isValidRationNum:true
			   }
		},
		messages: { 
			maxUtilization: { 
				required: "<spring:message code='clusterManage.error.nullMaxUtilization'/>",
				isValidRationNum:"<spring:message code='clusterManage.error.invalidThreshold'/>"
			},
			retrieval: { 
				required: "<spring:message code='clusterManage.error.nullRetrieval'/>",
				isValidRationNum:"<spring:message code='clusterManage.error.invalidThreshold'/>"
			},			
		}
 	}); 
 	
	if(!placeholderSupport()){
		placeholderCompatible();
	};
});
function submitStorage() {
	if(!$("#changeStorageForm").valid()) {
        return;
    } 
	var maxUtilization = $.trim($("#maxUtilization").val());
	var retrieval = $.trim($("#retrieval").val());
	maxUtilization = parseInt(maxUtilization);
	retrieval = parseInt(retrieval);
	if(retrieval >= maxUtilization ){
		handlePrompt("error","<spring:message code='clusterManage.error.invalidRetrieval'/>");
		return;
	}
	
	top.ymPrompt_disableModalbtn("#btn-focus");
	inLayerLoading('<spring:message code="cluster.memory.loading"/>',"loading-bar");
	$.ajax({
        type: "POST",
        url:"${ctx}/cluster/dcdetailmanage/changeNASStorage",
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
