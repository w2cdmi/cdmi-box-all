<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cse" uri="http://cse.huawei.com/custom-function-taglib"%>  
<%@ page import="pw.cdmi.box.uam.util.CSRFTokenManager"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%
request.setAttribute("token", CSRFTokenManager.getTokenForSession(session));
%>
<!DOCTYPE html>
<html>
<head>
<%@ include file="../../common/common.jsp"%>
<script src="${ctx}/static/js/public/JQbox-hw-page.js" type="text/javascript"></script>
<script src="${ctx}/static/js/public/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
</head>
<body>
<form action="${ctx}/app/adminlog/log/accountlog" method="post" id="searchForm" name="searchForm"> 
<input type="hidden" id="page" name="page" value="1">
<input type="hidden" id="token" name="token" value="${token}"/>
<div class="sys-content">
	<div class="form-horizontal form-con clearfix">
		<div class="form-left">
	        <div class="control-group">
	            <label for="input" class="control-label"> <spring:message  code="log.labelAppId"  />:</label>
	            <div class="controls">
	                <select class="span4" id="appId" name="appId">
		        		<c:forEach items="${ownedAppList}" var="appId">
		        			<option value="<c:out value='${appId}'/>" <c:if test="${selectedApp == appId}">selected="selected"</c:if>><c:out value='${appId}'/></option>
		        		</c:forEach>
					</select>
	            </div>
	        </div>
	        <div class="control-group">
	            <label for="input" class="control-label"><spring:message  code="log.operator"  />:</label>
	            <div class="controls">
	                 <input type="text" class="span4" id="operater" name="operater" value="<c:out value='${queryCondition.operater}'/>"/>
	            </div>
	        </div>
	    </div>
	    <div class="form-right" >
	         <div class="control-group">
	            <label for="input" class="control-label"><spring:message  code="log.operation.time"  />:</label>
	            <div class="controls">
	                 <input type="hidden" id="startTime" name="startTime"/>
                	<input type="hidden" id="endTime" name="endTime"/>
                	<input type="hidden" id="timeZone" name="timeZone"/>
					<input readonly="true" class="Wdate span2" type="text" id="startTimeComp" name="startTimeComp" value='<fmt:formatDate value="${queryCondition.startTime}" pattern="yyyy-MM-dd"/>' onClick="WdatePicker({lang:'<spring:message code="main.languageCalendrical"/>',dateFmt:'yyyy-MM-dd',minDate:'2013-06-01'})"> <spring:message  code="log.operation.time.until"  />
					<input readonly="true" class="Wdate span2" type="text" id="endTimeComp" name="endTimeComp" value='<fmt:formatDate value="${queryCondition.endTime}" pattern="yyyy-MM-dd"/>' onClick="WdatePicker({lang:'<spring:message code="main.languageCalendrical"/>',dateFmt:'yyyy-MM-dd',minDate:'2013-06-01'})">
	            </div>
	        </div>
	          <div class="control-group">
	            <div class="controls">
	                <button id="submit_btn" type="button" class="btn btn-primary" onClick="doQuery()"><spring:message  code="common.query"  /></button>
	                 <button id="submit_btn" type="button" class="btn" onClick="resetCondition()"><spring:message  code="common.reset"  /></button>
	            </div>
	        </div>
        </div>
     
    </div>
    <div class="table-con">
        <table class="table table-bordered table-striped">
          <thead>
            <tr>
                <th width="14%"><spring:message  code="log.operation.time"  /></th>
                <th width="10%"><spring:message  code="log.operator"  /></th>
                <th width="15%"><spring:message  code="log.operation.type"  /></th>
                <th width="20%"><spring:message  code="log.labelAppId"  /></th>
                <th width="20%"><spring:message  code="log.operation.details"  /></th>
                <th width="10%"><spring:message  code="log.operation.address"  /></th>
            </tr>
          </thead>
          <tbody>
          <c:forEach items="${userLogList.content}" var="userLog">
            <tr>
                <td>${cse:transferLongToDateString(userLog.createdAt)}</td>
                <td title="${cse:htmlEscape(userLog.loginName)}">
                    ${cse:htmlEscape(userLog.loginName)}
                </td>
                <td title="${cse:htmlEscape(userLog.type)}"><c:out value='${userLog.type}'/></td>
                <td title="${cse:htmlEscape(userLog.appId)}"><c:out value='${userLog.appId}'/></td>
                <td title="${cse:htmlEscape(userLog.detail)}">${cse:htmlEscape(userLog.detail)}</td>
                <td title="${cse:htmlEscape(userLog.clientAddress)}">${cse:htmlEscape(userLog.clientAddress)}</td>
            </tr>
            </c:forEach>
          </tbody>
        </table>
    </div>
    <div id="myPage"></div>
</div>
</form>
</body>
<script type="text/javascript">
$(document).ready(function() {
	$("#myPage").comboPage({
		lang:'<spring:message code="main.language"/>',
		curPage : <c:out value='${userLogList.number}'/>,
		perDis : <c:out value='${userLogList.size}'/>,
		totaldata : ${userLogList.totalElements},
		style : "page table-page"
	})
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
});

$.fn.comboPage.pageSkip = function(opts, _idMap, curPage){
	$("#page").val(curPage);
	doQuery();
};

function doQuery()
{
	var startTime = $("#startTimeComp").val();
	if(startTime != ""){
		$("#startTime").val(startTime +" 00:00:00");
	}else{
		$("#startTime").val("");
	}
	var endTime = $("#endTimeComp").val();
	if(endTime != ""){
		$("#endTime").val(endTime +" 23:59:59");
	}else{
		$("#endTime").val("");
	}
	if(endTime != "" && startTime > endTime){
		handlePrompt("error",'<spring:message  code="log.operation.err.time"  />',null,60);
		return;
	}
	$("#searchForm").submit();
}

function resetCondition()
{
	document.getElementById("appId").selectedIndex = 0;
	$("#operater").val("");
	$("#startTimeComp").val("");
	$("#endTimeComp").val("");
}

</script>
</html>
