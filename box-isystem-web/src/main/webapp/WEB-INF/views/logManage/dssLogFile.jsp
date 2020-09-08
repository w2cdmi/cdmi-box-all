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
<form action="${ctx}/log/logfile/search" method="post" id="searchForm" name="searchForm"> 
<input type="hidden" id="clusterType" name="clusterType" value="${cse:htmlEscape(clusterType)}"/>
<input type="hidden" id="token" name="token" value="${cse:htmlEscape(token)}"/>
<div class="sys-content">
	<div class="form-horizontal form-con clearfix">
		<div class="form-left">
			<div class="control-group">
	            <label for="input" class="control-label"> <spring:message code="clusterManage.storageRegion"/></label>
	            <div class="controls">
	                <select class="span4 span-four" id="regionId" name="regionId">
	                	<c:forEach items="${regionList}" var="region">
        					<option value="${cse:htmlEscape(region.id)}" <c:if test="${regionId == region.id}">selected="selected"</c:if>>${cse:htmlEscape(region.name)}</option>
        				</c:forEach>
					</select>
	            </div>
	        </div>
	        
	        <div class="control-group">
	            <label for="input" class="control-label"> <spring:message code="log.cluster.node"/></label>
	            <div class="controls">
	                <select class="span4 span-four" id="node" name="node">
	                	<option value="" selected="selected"></option>
	                	<c:forEach items="${logAgentNodes}" var="n">
        					<option value="${cse:htmlEscape(n.nodeName)}" <c:if test="${queryCondition.node == n.nodeName}">selected="selected"</c:if>>${cse:htmlEscape(n.nodeName)}</option>
        				</c:forEach>
					</select>
	            </div>
	        </div>
	        
	        <div class="control-group">
	            <label for="input" class="control-label"><spring:message code="log.file.name"/></label>
	            <div class="controls">
	                <input type="text" class="span4 span-four-new" id="fileName" name="fileName" value="${cse:htmlEscape(queryCondition.fileName)}"/>
	            </div>
	        </div>
	        
	    </div>
	    <div class="form-right">
	    	<div class="control-group">
	            <label for="input" class="control-label"> <spring:message code="log.centerData"/></label>
	            <div class="controls">
	                <select class="span4 span-four" id="clusterId" name="clusterId">
	                	<c:forEach items="${dssList}" var="dss">
        					<option value="${cse:htmlEscape(dss.id)}" <c:if test="${clusterId == dss.id}">selected="selected"</c:if>>${cse:htmlEscape(dss.name)}</option>
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
        <h5 id="searchResultTitle"></h5>
        <table id="searchResult" class="table table-bordered table-striped" style="display:none">
          <thead>
            <tr>
                <th><spring:message code="log.belong.node"/></th>
                <th><spring:message code="log.pigeonhole.time"/></th>
                <th><spring:message code="log.file.size"/></th>
                <th><spring:message code="log.filename"/></th>
            </tr>
          </thead>
          <tbody>
          <c:forEach items="${queryResult.logFiles}" var="logFile">
            <tr>
                <td>${cse:htmlEscape(logFile.nodeName)}</td>
                <td>${cse:longToDate(logFile.archiveTime.getTime()/1000*1000)}</td>
                <td>${cse:format(logFile.size)}</td>
                <td title="${cse:htmlEscape(logFile.fileName)}">
                    <a class="btn btn-small btn-link" id='${cse:htmlEscape(clusterId)}_${cse:htmlEscape(logFile.id)}' onClick='downloadFile("${ctx}/log/logfile/${cse:htmlEscape(clusterId)}/${cse:htmlEscape(logFile.id)}")'>${cse:htmlEscape(logFile.fileName)}</a>
                </td>
            </tr>
            </c:forEach>
          </tbody>
        </table>
    </div>
</div>
</form>
</body>
<script type="text/javascript">
$(document).ready(function() {

	
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



	$("#regionId").change(function () {
		var regionId = $(this).val();
		$.ajax({
	        type: "GET",
	        url:"${ctx}/log/logfile/listdss/" + regionId,
	        dataType:'json',
	        success: function(message) {
	        	var dssList=message.dssList;
	        	var logAgentNodeList=message.logAgentNodeList;
	        	
	        	var dssOptions='';
	        	var nodeOptions='';
	        	if(dssList != null && dssList != 'null' && dssList != '')
	        	{
	        		for(var i=0;i<dssList.length;i++){  
		            	dssOptions += '<option value="' + dssList[i].id + '">' + dssList[i].name +   
		                '</option>';  
		            } 
	        	}
	            
	            if(logAgentNodeList != null && logAgentNodeList != 'null' && logAgentNodeList != '')
	            {
	            	nodeOptions += '<option value="" selected="selected"></option>';
	            	for(var i=0;i<logAgentNodeList.length;i++){  
		            	nodeOptions += '<option value="' + logAgentNodeList[i].nodeName + '">' + logAgentNodeList[i].nodeName +   
		                '</option>';  
		            } 
	            }
	            
	            
	            $("#clusterId").empty();  
	            $("#clusterId").html(dssOptions);
	            
	            $("#node").empty();  
	            $("#node").html(nodeOptions);  
	        }
	    });
	});
	
	$("#clusterId").change(function () {
		var clusterId = $(this).val();
		$.ajax({
	        type: "GET",
	        url:"${ctx}/log/logfile/listLogAgentNode/" + clusterId,
	        dataType:'json',
	        success: function(logAgentNodeList) {
	        	var nodeOptions='';
	            
	            if(logAgentNodeList != null && logAgentNodeList != 'null' && logAgentNodeList != '')
	            {
	            	nodeOptions += '<option value="" selected="selected"></option>';
	            	for(var i=0;i<logAgentNodeList.length;i++){  
		            	nodeOptions += '<option value="' + logAgentNodeList[i].nodeName + '">' + logAgentNodeList[i].nodeName +   
		                '</option>';  
		            } 
	            }
	            
	            $("#node").empty();  
	            $("#node").html(nodeOptions);  
	        }
	    });
	});
	
	var total = "${cse:htmlEscape(queryResult.total)}";
	var maxResult ="${cse:htmlEscape(queryCondition.maxResult)}";
	var isSearch="${cse:htmlEscape(isSearch)}"
	if((isSearch!='')&&(isSearch=="1"))
	{
		if(parseInt(total) > parseInt(maxResult))
		{
			$("#searchResultTitle").html('<spring:message code="logfile.search.result.more.than.max" arguments="${cse:htmlEscape(queryResult.total)},${cse:htmlEscape(queryCondition.maxResult)}"/>');
		}
		else
		{
			$("#searchResultTitle").html('<spring:message code="logfile.search.result.less.than.max" arguments="${cse:htmlEscape(queryResult.total)}"/>');
		}
		$("#searchResult").show();
	}
	
	var pageH = $("body").outerHeight();
	if(pageH<500)
	{
		pageH = 500;
	}
	top.iframeAdaptHeight(pageH);
});

function doQuery()
{
	var startTime = $("#startTimeComp").val();
	var endTime = $("#endTimeComp").val();
	
	if("" == startTime && "" == endTime)
	{
		handlePrompt("error",'<spring:message code="log.select.handle.time"/>');
		return;
	}
	
	if(endTime != "" && startTime > endTime){
		handlePrompt("error",'<spring:message code="log.handle.time.error"/>',null,60);
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
	$("#fileName").val("");
	$("#startTimeComp").val("<fmt:formatDate value="${queryCondition.startTime}" pattern="yyyy-MM-dd"/>");
	$("#endTimeComp").val("<fmt:formatDate value="${queryCondition.endTime}" pattern="yyyy-MM-dd"/>");
}
function downloadFile(url)
{
	jQuery('<form action="'+ url +'" method="get" target="_parent"></form>').appendTo('body').submit().remove();
}

</script>
</html>
