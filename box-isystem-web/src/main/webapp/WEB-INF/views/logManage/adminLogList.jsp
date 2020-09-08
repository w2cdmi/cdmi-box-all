<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cse" uri="http://cse.huawei.com/custom-function-taglib"%>  
<%@ page import="com.huawei.sharedrive.isystem.util.CSRFTokenManager"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%
request.setAttribute("token", CSRFTokenManager.getTokenForSession(session));
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="Cache-Control" content="no-cache" />
<meta http-equiv="Pragma" content="no-cache" />
<title></title>

<%@ include file="../common/common.jsp"%>
<link href="${ctx}/static/jqueryUI-1.9.2/jquery-ui.min.css" rel="stylesheet" type="text/css" />

<script src="${ctx}/static/js/public/JQbox-hw-page.js" type="text/javascript"></script>
<script src="${ctx}/static/jqueryUI-1.9.2/jquery-ui.min.js" type="text/javascript"></script>

</head>
<body>
<form action="${ctx}/userlog/log/list" method="post" id="searchForm" name="searchForm"> 
<input type="hidden" id="page" name="page" value="1">
<input type="hidden" id="token" name="token" value="${cse:htmlEscape(token)}"/>
<div class="sys-content">
	<div class="form-horizontal form-con clearfix">
		<div class="form-left">
	        <div class="control-group">
	            <label for="input" class="control-label"> <spring:message code="log.handle.type"/></label>
	            <div class="controls">
	                <select class="span4 span-four" id="operateType" name="operateType">
        		<c:forEach items="${operateTypeList}" var="oper">
        			<option value="${cse:htmlEscape(oper.userLogType.value)}" <c:if test="${queryCondition.operateType == oper.userLogType.value}">selected="selected"</c:if>>${cse:htmlEscape(oper.operatrDetails)}</option>
        		</c:forEach>
			</select>
	            </div>
	        </div>
	        <div class="control-group">
	            <label for="input" class="control-label"><spring:message code="log.handle.time"/></label>
	            <div class="controls">
	            	 <input type="hidden" id="startTime" name="startTime"/>
                	<input type="hidden" id="endTime" name="endTime"/>
					<input class="Wdate span2 span-two" readonly="true" type="text" id="startTimeComp"> 
					<spring:message code="log.till"/>
					<input class="Wdate span2 span-two" readonly="true" type="text" id="endTimeComp"> 
				 </div>
	        </div>
	    </div>
	    <div class="form-right">
	        <div class="control-group">
	            <label for="input" class="control-label"><spring:message code="log.handler.user"/></label>
	            <div class="controls">
	                <input type="text" class="span4 span-four" id="admin" name="admin" value="${cse:htmlEscape(queryCondition.admin)}"/>
	            </div>
	        </div>
	        <div class="control-group">
	            <label for="input" class="control-label"></label>
	            <div class="controls">
	                <button id="submit_btn" type="button" class="btn btn-primary" onClick="doQuery()"><spring:message code="log.search"/></button>
            		<button id="reset_btn" type="button" class="btn" onClick="resetCondition()"><spring:message code="log.reset"/></button>
	            </div>
	        </div>
        </div>
    </div>
    <div class="table-con">
        <table class="table table-bordered table-striped">
          <thead>
            <tr>
                <th style="width:14%;"><spring:message code="log.handleTime"/></th>
                <th><spring:message code="log.handleUser"/></th>
                <th style="width:12%;"><spring:message code="log.handleType"/></th>
                <th><spring:message code="log.handle.detail"/></th>
                <th style="width:8%;"><spring:message code="log.handle.rank"/></th>
                <th style="width:8%;"><spring:message code="log.handle.result"/></th>
                <th style="width:20%"><spring:message code="log.handle.IP"/></th>
            </tr>
          </thead>
          <tbody>
          <c:forEach items="${adminLogList.content}" var="adminLog">
            <tr>
                <td title='<fmt:formatDate value="${adminLog.createdAt}" pattern="yyyy-MM-dd HH:mm:ss"/>'><fmt:formatDate value="${adminLog.createdAt}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                <td title=" ${cse:htmlEscape(adminLog.loginName)}<c:if test="${not empty adminLog.loginName}">(${cse:htmlEscape(adminLog.loginName)})</c:if>">
                    ${cse:htmlEscape(adminLog.loginName)}
                </td>
                <td title="${cse:htmlEscape(adminLog.keyword)}">${cse:htmlEscape(adminLog.keyword)}</td>
                <td title="${cse:htmlEscape(adminLog.detail)}">${cse:htmlEscape(adminLog.detail)}</td>
                <td title="${cse:htmlEscape(adminLog.appId)}">${cse:htmlEscape(adminLog.appId)}</td>
                <td><c:if test="${adminLog.level==0}"><spring:message code="common.success"/></c:if><c:if test="${adminLog.level==1}"><spring:message code="common.fail"/></c:if></td>
                <td>${cse:htmlEscape(adminLog.clientAddress)}</td>
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
		curPage : ${cse:htmlEscape(adminLogList.number)},
		lang : '<spring:message code="common.language1"/>',
		perDis : ${cse:htmlEscape(adminLogList.size)},
		totaldata : ${adminLogList.totalElements},
		style : "page table-page"
	})
	var pageH = $("body").outerHeight();
	if(pageH<500)
	{
		pageH=500;
	}
	top.iframeAdaptHeight(pageH);
		jQuery(function($){
		var encode='<spring:message code="common.language1"/>';
		if(encode=='zh-cn' || encode=='zh'){
		$.datepicker.regional['zh-CN'] = {
		 closeText: '关闭',
 		prevText: '&#x3c;上月',
		 nextText: '下月&#x3e;',
 		currentText: '今天',
 		monthNames: ['一月','二月','三月','四月','五月','六月',
 		'七月','八月','九月','十月','十一月','十二月'],
 		monthNamesShort: ['一','二','三','四','五','六',
 		'七','八','九','十','十一','十二'],
 		dayNames: ['星期日','星期一','星期二','星期三','星期四','星期五','星期六'],
 		dayNamesShort: ['周日','周一','周二','周三','周四','周五','周六'],
 		dayNamesMin: ['日','一','二','三','四','五','六'],
 		weekHeader: '周',
		dateFormat: 'yy-mm-dd',
 		firstDay: 1,
 		isRTL: false,
 		showMonthAfterYear: true,
 		yearSuffix: '年'};
 		$.datepicker.setDefaults($.datepicker.regional['zh-CN']);
		}
 		});
	
	$("#startTimeComp").datepicker();
	$("#endTimeComp").datepicker();
	$("#startTimeComp").datepicker("option", "dateFormat","yy-mm-dd"); 
	$("#endTimeComp").datepicker("option", "dateFormat","yy-mm-dd"); 
	$("#startTimeComp").datepicker('setDate','<fmt:formatDate value="${queryCondition.startTime}" pattern="yyyy-MM-dd"/>');
	$("#endTimeComp").datepicker('setDate','<fmt:formatDate value="${queryCondition.endTime}" pattern="yyyy-MM-dd"/>');
	
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
		handlePrompt("error",'<spring:message code="log.handle.time.error"/>',null,60);
		return;
	}
	if(startTime != ""){
		$("#startTime").attr('name="startTime"');
		$("#startTime").val(startTime +" 00:00:00");
	}else{
		$("#startTime").removeAttr("name");
	}
	if(endTime != ""){
		$("#endTime").attr('name="startTime"');
		$("#endTime").val(endTime +" 23:59:59");
	}else{
		$("#endTime").val(null);
		$("#endTime").removeAttr("name");
	}
	$("#searchForm").submit();
	var pageH = $("body").outerHeight();
	if(pageH<500)
	{
		pageH=500;
	}
	top.iframeAdaptHeight(pageH);
}

function resetCondition()
{
	$("#operateType").val("");
	$("#admin").val("");
	$("#startTimeComp").val("");
	$("#endTimeComp").val("");
}

</script>
</html>
