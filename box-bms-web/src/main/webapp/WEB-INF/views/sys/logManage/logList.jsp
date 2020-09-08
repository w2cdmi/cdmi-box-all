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
<form action="${ctx}/sys/systemlog/log/list" method="post" id="searchForm" name="searchForm"> 
<input type="hidden" id="page" name="page" value="1">
<input type="hidden" id="token" name="token" value="${token}"/>
<div class="sys-content">
	<div class="form-horizontal form-con clearfix">
		<div class="form-left">
	        <div class="control-group">
	            <label for="input" class="control-label"> <spring:message code="log.operation.type"/>:</label>
	            <div class="controls">
	                <select class="span4" id="operateType" name="operateType">
        		<c:forEach items="${operateTypeList}" var="oper">
        			<option value="${oper.operateType.code}" <c:if test="${queryCondition.operateType == oper.operateType.code}">selected="selected"</c:if>>${oper.operatrDetails}</option>
        		</c:forEach>
			</select>
	            </div>
	        </div>
	        <div class="control-group">
	            <label for="input" class="control-label"><spring:message code="log.operation.time"/>:</label>
	            <div class="controls">
	                 <input type="hidden" id="startTime" name="startTime"/>
                	<input type="hidden" id="endTime" name="endTime"/>
                	<input type="hidden" id="timeZone" name="timeZone"/>
					<input readonly="true" class="Wdate span2" type="text" id="startTimeComp" name="startTimeComp" value='<fmt:formatDate value="${queryCondition.startTime}" pattern="yyyy-MM-dd"/>' onClick="WdatePicker({lang:'<spring:message code="main.languageCalendrical"/>',dateFmt:'yyyy-MM-dd',minDate:'2013-06-01'})"> <spring:message code="log.operation.time.until"/>
					<input readonly="true" class="Wdate span2" type="text" id="endTimeComp" name="endTimeComp" value='<fmt:formatDate value="${queryCondition.endTime}" pattern="yyyy-MM-dd"/>' onClick="WdatePicker({lang:'<spring:message code="main.languageCalendrical"/>',dateFmt:'yyyy-MM-dd',minDate:'2013-06-01'})">
	            </div>
	        </div>
	    </div>
	    <div class="form-right">
	        <div class="control-group">
	            <label for="input" class="control-label"><spring:message code="log.operator"/>:</label>
	            <div class="controls">
	                <input type="text" class="span4" id="admin" name="admin" value="<c:out value='${queryCondition.admin}'/>"/>
	            </div>
	        </div>
	        <div class="control-group">
	            <label for="input" class="control-label"></label>
	            <div class="controls">
	                <button id="submit_btn" type="button" class="btn btn-primary" onClick="doQuery()"><spring:message code="common.query"/></button>
            		<button id="reset_btn" type="button" class="btn" onClick="resetCondition()"><spring:message code="common.reset"/></button>
	            </div>
	        </div>
        </div>
    </div>
    <div class="table-con">
        <table class="table table-bordered table-striped">
          <thead>
            <tr>
                 <th width="13%"><spring:message code="log.operation.time"/></th>
                <th width="13%"><spring:message code="log.operator"/></th>
                <th width="12%"><spring:message code="log.operation.type"/></th>
                <th><spring:message code="log.operation.details"/></th>
                <th width="8%"><spring:message code="log.operation.result"/></th>
                <th width="8%"><spring:message code="log.operation.level"/></th>
                <th width="15%"><spring:message code="log.operation.address"/></th>
            </tr>
          </thead>
          <tbody>
          <c:forEach items="${adminLogList.content}" var="adminLog">
            <tr>
                <td><fmt:formatDate value="${adminLog.createdAt}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                <td  title="${cse:htmlEscape(adminLog.showName)}">
                    ${cse:htmlEscape(adminLog.showName)}
                </td>
                <td title="${adminLog.operateType}"><c:out value='${adminLog.operateType}'/></td>
                <td title="${cse:htmlEscape(adminLog.operateDescription)}">${cse:htmlEscape(adminLog.operateDescription)}</td>
                <td><c:if test="${adminLog.operateResult}"><spring:message code="common.success"/></c:if><c:if test="${!adminLog.operateResult}"><spring:message code="common.failed"/></c:if></td>
                <td title="${adminLog.levelString}">${adminLog.levelString}</td>
                <td title="${cse:htmlEscape(adminLog.clientAddress)}"><c:out value='${adminLog.clientAddress}'/></td>
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
		curPage : <c:out value='${adminLogList.number}'/>,
		perDis : <c:out value='${adminLogList.size}'/>,
		totaldata : ${adminLogList.totalElements},
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
	var endTime = $("#endTimeComp").val();
	if(endTime != "" && startTime > endTime){
		handlePrompt("error",'<spring:message code="log.operation.err.time"/>',null,60);
		return;
	}
	if(startTime != ""){
		$("#startTime").val(startTime +" 00:00:00");
	}else{
		$("#startTime").val("");
	}
	if(endTime != ""){
		$("#endTime").val(endTime +" 23:59:59");
	}else{
		$("#endTime").val("");
	}
	$("#searchForm").submit();
}

function resetCondition()
{
	$("#operateType").val("");
	$("#admin").val("");
	$("#startTimeComp").val("");
	$("#endTimeComp").val("");
}

function getTimeZone()
{
	var offset = 0 - new Date().getTimezoneOffset();
	var gmtHours = offset/60;
	
	gmtHours = Math.floor(gmtHours);
	if(gmtHours < 0)
	{
		gmtHours = Math.ceil(gmtHours);
	}
	
	var gmtMinute = Math.abs(offset - gmtHours * 60) + "";
	
	if(gmtMinute.length ==1)
	{
		gmtMinute = "0" + gmtMinute;
	}
	
	if(gmtHours<0)
	{
		alert("GMT" +  gmtHours + ":" + gmtMinute);
		return "GMT" +  gmtHours + ":" + gmtMinute;
	}
	alert("GMT" +  gmtHours + ":" + gmtMinute);
	return "GMT+" +  gmtHours + ":" + gmtMinute;
}
</script>
</html>
