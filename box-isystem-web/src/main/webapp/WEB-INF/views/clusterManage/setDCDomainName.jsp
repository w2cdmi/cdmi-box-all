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
   	<form class="form-horizontal" id="setDCDomainNameForm">
        <input type="hidden" id="status" name="status" value="true" />
        <div class="alert alert-error input-medium controls" id="errorTip" style="display:none">
			<button class="close" data-dismiss="alert">×</button><spring:message code="common.set.fail"/>
		</div>
        
        <div class="control-group">
        	<label class="control-label" for=""><spring:message code="common.regionName"/>:</label>
            <div class="controls">
                <input class="span4" type="text" id="domainName" name="domainName" value="${cse:htmlEscape(domainName)}" />
                <span class="validate-con bottom"><div></div></span>
            </div>
        </div>
        <input type="hidden" id="hiddenDcId" name="id" value="${cse:htmlEscape(dataCenter.id)}" />
        <input type="hidden" id="token" name="token" value="${cse:htmlEscape(token)}" />
	</form>
    </div>
</div>
<script type="text/javascript">
$(document).ready(function() {
	$("#setDCDomainNameForm").validate({ 
		rules: { 
			   domainName: { 
				   rangelength:[0,128]
			   } 
		}
    }); 
});
function submitModifyDomainName() {
	if(!$("#setDCDomainNameForm").valid()) {
        return false;
    }
	$.ajax({
        type: "POST",
        url:"${ctx}/cluster/dcdetailmanage/setDomainName" ,
        data:$('#setDCDomainNameForm').serialize(),
        error: function(request) {
        	handlePrompt("error",'<spring:message code="common.set.fail"/>');
        },
        success: function() {
        	top.ymPrompt.close();
        	top.handlePrompt("success",'<spring:message code="common.set.success"/>');
        	top.window.frames[0].location = "${ctx}/cluster/dcdetailmanage/${cse:htmlEscape(dataCenter.id)}";
        }
    });
}
</script>
</body>
</html>
