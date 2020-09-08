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
<form action="${ctx}/app/adminlog/log/user" method="post" id="searchForm" name="searchForm"> 
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
	            <label for="input" class="control-label"><spring:message  code="search.keywords"  />:</label>
	            <div class="controls">
	                 <input type="text" placeholder='<spring:message code="search.keywords.help.message" />' id="detail" class="span4" name="detail" value="<c:out value='${queryCondition.detail}'/>"/>
	            </div>
	        </div>
	    </div>
	    <div class="form-right">
	         <div class="control-group">
	            <label for="input" class="control-label"> <spring:message  code="log.operation.type"  />:</label>
	            <div class="controls">
	                <select style="width: 148px;" onchange="selectSecondCategory()" id="firstCategory" name="firstCategory">
		        		<c:forEach items="${categoryList}" var="oper">
		        			<option value="<c:out value='${oper.logCategory.modelName}'/>" <c:if test="${queryCondition.firstType == oper.logCategory.selfId}">selected="selected"</c:if> title="${oper.operatrDetail}"><c:out value='${oper.operatrDetail}'/></option>
		        		</c:forEach>
					</select>
					<select style="width: 148px" id="secondCategory" name="secondCategory">
						<option value="ALL_LOG" <c:if test="${queryCondition.secondType == -1}">selected="selected"</c:if> title="<spring:message code='common.select.all' />"><spring:message code="common.select.all" /></option>
		        		<c:forEach items="${kidCategoryList}" var="oper">
		        			<option value="<c:out value='${oper.logCategory.modelName}'/>" <c:if test="${queryCondition.secondType == oper.logCategory.selfId}">selected="selected"</c:if> title="${oper.operatrDetail}"><c:out value='${oper.operatrDetail}'/></option>
		        		</c:forEach>
					</select>										
	            </div>
	        </div>
	        <div class="control-group">
	            <label for="input" class="control-label"> <spring:message  code="log.operation.result"  />:</label>
	            <div class="controls">
	                <select class="span4" id="status" name="status">
		        		<option value="-1" <c:if test="${queryCondition.status == -1}">selected="selected"</c:if>><spring:message code="common.select.all"/></option>
	        			<option value="0" <c:if test="${queryCondition.status == 0}">selected="selected"</c:if>><spring:message code="common.success"/></option>
	        			<option value="1" <c:if test="${queryCondition.status == 1}">selected="selected"</c:if>><spring:message code="common.failed"/></option>
					</select>
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
                <th width="13%"><spring:message  code="log.operation.time"  /></th>
                <th width="12%"><spring:message  code="log.operator"  /></th>              
                <th width="12%"><spring:message  code="log.labelAppId"  /></th>
                <th width="40%"><spring:message  code="log.operation.details"  /></th>
                <th width="8%"><spring:message  code="log.operation.result"  /></th>
                <th width="14%"><spring:message  code="log.operation.address"  /></th> 
            </tr>
          </thead>
          <tbody>
          <c:forEach items="${userLogList.content}" var="userLog">
            <tr>
                <td>${cse:transferLongToDateString(userLog.createdAt)}</td>
                <td title="${cse:htmlEscape(userLog.loginName)}">${cse:htmlEscape(userLog.loginName)}</td>
                <td title="${cse:htmlEscape(userLog.appId)}"><c:out value='${userLog.appId}'/></td>
                <td title="${cse:htmlEscape(userLog.detail)}">${cse:htmlEscape(userLog.detail)}</td>
                <td><c:if test="${userLog.operateResult}"><spring:message code="common.success"/></c:if><c:if test="${!userLog.operateResult}"><spring:message code="common.failed"/></c:if></td>
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
	if(!placeholderSupport()){
		placeholderCompatible();
	};
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
	$("#detail").val("");
	$("#startTimeComp").val("");
	$("#endTimeComp").val("");
	document.getElementById("status").selectedIndex = 0;
	document.getElementById("firstCategory").selectedIndex = 0;
	document.getElementById("secondCategory").selectedIndex = 0;
}

function selectSecondCategory()
{
    var firstCategory = $("#firstCategory");
    $.ajax({
		type: "GET",
		url:"${ctx}/app/adminlog/log/category/" + firstCategory.val(),
		async : false,
		error: function(request) {
		},
		success: function(data) {
			var secondCategory = $("#secondCategory");
			secondCategory.empty();
			document.getElementById("secondCategory").options.add(new Option("<spring:message code='common.select.all'/>","ALL_LOG"));
			for(var i=0; i<data.length; i++) {
				document.getElementById("secondCategory").options.add(new Option(data[i].operatrDetail,data[i].logCategory));
			}
		}
	});
}

</script>
</html>
