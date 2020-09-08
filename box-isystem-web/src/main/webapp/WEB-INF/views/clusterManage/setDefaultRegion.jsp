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
<div class="pop-content sys-content-en">
	<div class="form-con">
   	<form class="form-horizontal" id="setDefaultRegionForm">
        <input type="hidden" id="status" name="status" value="true" />
        <div class="control-group">
        	<label class="control-label" for=""><em>*</em><spring:message code="cluster.storage.default"/>:</label>
            <div class="controls">
                <select id="region" name="region">
                <c:forEach items="${regionList}" var="region">
                    <option value="${cse:htmlEscape(region.id)}" ${region.defaultRegion ? "selected" : ""}>${cse:htmlEscape(region.name)}</option>
                </c:forEach>
                </select>
            </div>
        </div>
        <input type="hidden" id="token" name="token" value="${cse:htmlEscape(token)}"/>	
	</form>
    </div>
</div>
<script type="text/javascript">
function submitSetDefaultRegion() {
	$.ajax({
        type: "POST",
        url:"${ctx}/cluster/region/setDefaultRegion" ,
        data:$('#setDefaultRegionForm').serialize(),
        error: function(request) {
        	handlePrompt("error",'<spring:message code="common.set.fail"/>');
        },
        success: function() {
        	top.ymPrompt.close();
        	top.handlePrompt("success",'<spring:message code="common.set.success"/>');
        	top.document.getElementById("regionManageMenuId").click();
        }
    });
}
</script>
</body>
</html>
