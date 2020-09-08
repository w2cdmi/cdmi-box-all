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
<form action="${ctx}/convertTask/list" method="post" id="searchForm" name="searchForm"> 
<input type="hidden" id="page" name="page" value="1">
<input type="hidden" id="token" name="token" value="${cse:htmlEscape(token)}"/>
<div class="sys-content">
	<div class="form-horizontal form-con clearfix">
		<div class="form-left">
	        <div class="control-group">
	            <label for="input" class="control-label"><spring:message code="manage.convert.task.filename"/></label>
	            <div class="controls">
	                <input type="text" class="span4 span-four" id="fileName" name="fileName" value="${cse:htmlEscape(queryCondition.fileName)}"/>
	            </div>
	        </div>
	    </div>
	    <div class="form-right">
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
        <table class="table table-bordered table-striped">
          <thead>
            <tr>
                <th style="width:20%;text-align: center;"><spring:message code="manage.convert.task.filename"/></th>
                <th style="width:10%;text-align: center;"><spring:message code="manage.convert.task.status"/></th>
                <th style="width:10%;text-align: center;"><spring:message code="manage.convert.task.retryCount"/></th>
                <th style="width:10%;text-align: center;"><spring:message code="manage.convert.task.bigFileFlag"/></th>
                <th style="width:15%;text-align: center;"><spring:message code="manage.convert.task.ip"/></th>
                <th style="width:15%;text-align: center;"><spring:message code="manage.convert.task.convertTime"/></th>
            </tr>
          </thead>
          <tbody>
          	<c:forEach items="${taskList.content}" var="task" varStatus="status">
            <tr>
            	<td title="${cse:htmlEscape(task.fileName)}">${cse:htmlEscape(task.fileName)}</td>
            	<c:if test="${task.status==0}"><td title='<spring:message code="convert.task.step.status.zero"/>' name="convertStatus">
            		<span id="convertStatus<c:out value='${status.index}'/>"><spring:message code="convert.task.step.status.zero"/></span></td>
            	</c:if>
            	
            	<c:if test="${task.status==1}"><td title='<spring:message code="convert.task.step.status.one"/>' name="convertStatus">
            		<span id="convertStatus<c:out value='${status.index}'/>"><spring:message code="convert.task.step.status.one"/></span></td>
            	</c:if>
            	
            	<c:if test="${task.status==2}"><td title='<spring:message code="convert.task.step.status.two"/>' name="convertStatus">
            		<span id="convertStatus<c:out value='${status.index}'/>"><spring:message code="convert.task.step.status.two"/></span></td>
            	</c:if>
            	
            	<c:if test="${task.status==3}"><td title='<spring:message code="convert.task.step.status.three"/>' name="convertStatus">
            		<span id="convertStatus<c:out value='${status.index}'/>"><spring:message code="convert.task.step.status.three"/></span></td>
            	</c:if>
            	
            	<c:if test="${task.status==4}"><td title='<spring:message code="convert.task.step.status.four"/>' name="convertStatus">
            		<span id="convertStatus<c:out value='${status.index}'/>"><spring:message code="convert.task.step.status.four"/></span></td>
            	</c:if>
            	
            	<c:if test="${task.status==5}"><td title='<spring:message code="convert.task.step.status.five"/>' name="convertStatus">
            		<span id="convertStatus<c:out value='${status.index}'/>"><spring:message code="convert.task.step.status.five"/></span></td>
            	</c:if>
            	
            	<c:if test="${task.status==6}"><td title='<spring:message code="convert.task.step.status.six"/>' name="convertStatus">
            		<span id="convertStatus<c:out value='${status.index}'/>"><spring:message code="convert.task.step.status.six"/></span></td>
            	</c:if>
            	
            	<c:if test="${task.status==7}"><td title='<spring:message code="convert.task.step.status.seven"/>' name="convertStatus">
            		<span id="convertStatus<c:out value='${status.index}'/>"><spring:message code="convert.task.step.status.seven"/></span></td>
            	</c:if>
            	
            	<c:if test="${task.status==8}"><td title='<spring:message code="convert.task.step.status.eight"/>' name="convertStatus">
            		<span id="convertStatus<c:out value='${status.index}'/>"><spring:message code="convert.task.step.status.eight"/></span></td>
            	</c:if>
            	
            	<c:if test="${task.status==9}"><td title='<spring:message code="convert.task.step.status.nine"/>' name="convertStatus">
            		<span id="convertStatus<c:out value='${status.index}'/>"><spring:message code="convert.task.step.status.nine"/></span></td>
            	</c:if>
            	
            	<c:if test="${task.status==99}"><td title='<spring:message code="convert.task.step.status.nienine"/>' name="convertStatus">
            		<span id="convertStatus<c:out value='${status.index}'/>"><spring:message code="convert.task.step.status.nienine"/></span></td>
            	</c:if>
            	
            	<c:if test="${task.status==100}"><td title='<spring:message code="convert.task.step.status.hundred"/>' name="convertStatus">
            		<span id="convertStatus<c:out value='${status.index}'/>"><spring:message code="convert.task.step.status.nienine"/></span></td>
            	</c:if>
            	
            	<td title="${cse:htmlEscape(task.retryCount)}" name="retryCount"><span id="retryCount<c:out value='${status.index}'/>">${cse:htmlEscape(task.retryCount)}</span></td>
            	
            	<c:if test="${task.bigFileFlag==0}"><td title='<spring:message code="convert.task.bigFileFlag.no"/>'><spring:message code="convert.task.bigFileFlag.no"/></td></c:if>
            	<c:if test="${task.bigFileFlag==1}"><td title='<spring:message code="convert.task.bigFileFlag.yes"/>'><spring:message code="convert.task.bigFileFlag.yes"/></td></c:if>
            	
            	<td title="${cse:htmlEscape(task.csIp)}">${cse:htmlEscape(task.csIp)}</td>
                <td title='<fmt:formatDate value="${task.convertTime}" pattern="yyyy-MM-dd HH:mm:ss"/>'><fmt:formatDate value="${task.convertTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
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
		curPage : ${cse:htmlEscape(taskList.number)},
		lang : '<spring:message code="common.language1"/>',
		perDis : ${cse:htmlEscape(taskList.size)},
		totaldata : ${taskList.totalElements},
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
	$("#fileName").val("");
	$("#startTimeComp").val("");
	$("#endTimeComp").val("");
}

function update(index,level)
{
	var tds = $("td[name='convertLevel']");
	var tdsel = $("td[name='levelSelect']");
	tds[index].style.display = "none"; 
	tdsel[index].style.display = "";
	$("#update"+index).css('display', 'none');
	$("#cancel"+index).css('display', '');
	$("#save"+index).css('display', '');
}

function cancel(index)
{
	var tds = $("td[name='convertLevel']");
	var tdsel = $("td[name='levelSelect']");
	tds[index].style.display = ""; 
	tdsel[index].style.display = "none";
	$("#update"+index).css('display', '');
	$("#cancel"+index).css('display', 'none');
	$("#save"+index).css('display', 'none');
}

function save(index,taskId)
{
	var level = $("#levelSel"+index).val();
	$.ajax({
        type: "POST",
        url:"${ctx}/convertTask/save",
        data:{taskId:taskId,level:level},
        error: function(request) {
        	top.handlePrompt("error",'<spring:message code="common.saveFail"/>');
        },
        success: function() {
        	top.handlePrompt("success",'<spring:message code="common.saveSuccess"/>');
        	var tds = $("td[name='convertLevel']");
        	var tdsel = $("td[name='levelSelect']");
        	tds[index].style.display = ""; 
        	tdsel[index].style.display = "none";
        	tds[index].title=$("#levelSel"+index).find("option:selected").text();
        	$("#convertLevel"+index).html($("#levelSel"+index).find("option:selected").text());
        	$("#update"+index).css('display', '');
        	$("#cancel"+index).css('display', 'none');
        	$("#save"+index).css('display', 'none');
        }
    });
}

function resetState(taskId,index)
{  
	$.ajax({
        type: "POST",
        url:"${ctx}/convertTask/resetState",
        data:{taskId:taskId},
        error: function(request) {
        	top.handlePrompt("error",'<spring:message code="convert.task.resetState.fail"/>');
        },
        success: function() {
        	top.handlePrompt("success",'<spring:message code="convert.task.resetState.success"/>');
        	var tdStatus = $("td[name='convertStatus']");
        	var tdRetry = $("td[name='retryCount']");
        	$("#resetState"+index).attr("disabled","true");
        	tdStatus[index].title='<spring:message code="convert.task.step.status.nine"/>';
        	$("#convertStatus"+index).html('<spring:message code="convert.task.step.status.nine"/>');
        	tdRetry[index].title="0";
        	$("#retryCount"+index).html("0");
        }
    });
}
</script>
</html>
