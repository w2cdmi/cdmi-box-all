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
<META HTTP-EQUIV="Expires" CONTENT="0">
<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
<META HTTP-EQUIV="Cache-control"
	CONTENT="no-cache, no-store, must-revalidate">
<META HTTP-EQUIV="Cache" CONTENT="no-cache">
<title></title>
<link href="${ctx}/static/skins/default/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/static/skins/default/css/public.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/static/skins/default/css/main.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/static/jqueryUI-1.9.2/jquery-ui.min.css" rel="stylesheet" type="text/css" />

<script src="${ctx}/static/js/public/jquery-1.10.2.min.js" type="text/javascript"></script>
<script src="${ctx}/static/js/public/bootstrap.min.js" type="text/javascript"></script>
<script src="${ctx}/static/js/public/common.js" type="text/javascript"></script>
<script src="${ctx}/static/js/public/JQbox-hw-page.js" type="text/javascript"></script>
<script src="${ctx}/static/jqueryUI-1.9.2/jquery-ui.min.js" type="text/javascript"></script>
</head>
<body>
<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);
%>
<form action="${ctx}/accountManage/account/list" method="post" id="searchForm" name="searchForm"> 
<input type="hidden" id="page" name="page" value="1">
<input type="hidden" id="token" name="token" value="${token}"/>
<div class="sys-content">
	<div class="form-horizontal form-con clearfix">
	<div class="form-left">
	 <div class="control-group">
	        <label for="input" class="control-label"><spring:message code="log.handle.time"/></label>
	            <div class="controls">
	            	 <input type="hidden" id="startTime" name="startTime"/>
                	<input type="hidden" id="endTime" name="endTime"/>
					<input class="Wdate span2" readonly="true" type="text" id="startTimeComp"> 
					<spring:message code="log.till"/>
					<input class="Wdate span2" readonly="true" type="text" id="endTimeComp"> 
				 </div>
	        </div>
	     </div>
	    <div class="form-right">
	        <div class="control-group">
	            <label for="input" class="control-label"><spring:message code="accout.domain.name"/></label>
	            <div class="controls">
	                <input type="text" class="span4" id="name" name="name" value="${cse:htmlEscape(accountPageCondition.name)}"/>
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
                <th style="width:25%;"><spring:message code="manage.corporate.name"/></th>
                <th><spring:message code="manage.app.id"/></th>
                <th style="width:10%;"><spring:message code="manage.status"/></th>
                <th style="width:20%;"><spring:message code="manage.create.time"/></th>
            </tr>
          </thead>
          <tbody>
          <c:forEach items="${accountList.content}" var="account">
            <tr>
                <td title=" ${cse:htmlEscape(account.name)}">${cse:htmlEscape(account.name)}</td>
                <td title=" ${cse:htmlEscape(account.appId)}">${cse:htmlEscape(account.appId)}</td>
                <td title=" <c:if test='${account.status==1}'><spring:message code="common.stop"/></c:if>
                			<c:if test='${account.status==0}'><spring:message code="common.start"/></c:if>">
                 <c:if test='${account.status==1}'><spring:message code="common.stop"/></c:if>
                  <c:if test='${account.status==0}'><spring:message code="common.start"/></c:if>
                </td>
                <td><fmt:formatDate value="${account.createAt}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
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
		curPage : '${cse:htmlEscape(accountList.number)}',
		lang : '<spring:message code="common.language1"/>',
		perDis : '${cse:htmlEscape(accountList.size)}',
		totaldata : '${cse:htmlEscape(accountList.totalElements)}',
		style : "page table-page"
	})
	
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
	$("#startTimeComp").datepicker('setDate','<fmt:formatDate value="${accountPageCondition.startTime}" pattern="yyyy-MM-dd"/>');
	$("#endTimeComp").datepicker('setDate','<fmt:formatDate value="${accountPageCondition.endTime}" pattern="yyyy-MM-dd"/>');
	
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
		handlePrompt("error",'<spring:message code="log.handle.time.error"/>',null,60);
		return;
	}
	if(startTime != ""){
		$("#startTime").attr('name="startTime"');
		$("#startTime").val(startTime +" 00:00:00");
	}else{
		$("#startTime").removeAttr("name");
		$("#startTime").val("");
	}
	if(endTime != ""){
		$("#endTime").attr('name="startTime"');
		$("#endTime").val(endTime +" 23:59:59");
	}else{
		$("#endTime").removeAttr("name");
		$("#endTime").val("");
	}
	$("#searchForm").submit();
}

function resetCondition()
{
	$("#appId").val("");
	$("#name").val("");
	$("#startTimeComp").val("");
	$("#endTimeComp").val("");
}

</script>
</html>
