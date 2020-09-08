<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cse"
	uri="http://cse.huawei.com/custom-function-taglib"%>
<%@ page import="pw.cdmi.box.uam.util.CSRFTokenManager"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
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

<link href="${ctx}/static/jqueryUI-1.9.2/jquery-ui.min.css"
	rel="stylesheet" type="text/css" />
<script src="${ctx}/static/js/public/JQbox-hw-page.js"
	type="text/javascript"></script>
<script src="${ctx}/static/jqueryUI-1.9.2/jquery-ui.min.js"
	type="text/javascript"></script>
<script src="${ctx}/static/js/public/My97DatePicker/WdatePicker.js"
	type="text/javascript"></script>
</head>
<body>
	<form action="${ctx}/feedback/uam/listopen" method="post"
		id="searchForm" name="searchForm">
		<input type="hidden" id="page" name="page" value="1"> <input
			type="hidden" id="token" name="token"
			value="${cse:htmlEscape(token)}" /> <input type="hidden"
			id="problemStatus" name="problemStatus"
			value="${condition.problemStatus}" />

		<div class="sys-content">
			<div class="form-horizontal form-con clearfix">
				<div class="form-left">

					<div class="control-group">
						<label for="input" class="control-label"><spring:message
								code="feedback.manage.condition.twTime" />:</label>
						<div class="controls">
							<%-- <input type="hidden" id="twBeginTime" name="twBeginTime"/>
                	<input type="hidden" id="twEndTime" name="twEndTime"/>
					<input class="Wdate span2 span-two" readonly="readonly" type="text" id="twBeginTimeComp"> 
					<spring:message code="log.operation.time.until"/>
					<input class="Wdate span2 span-two" readonly="readonly" type="text" id="twEndTimeComp">  --%>

							<input type="hidden" id="twBeginTime" name="twBeginTime" /> <input
								type="hidden" id="twEndTime" name="twEndTime" /> <input
								type="hidden" id="timeZone" name="timeZone" /> <input
								readonly="readonly" class="Wdate span2" type="text"
								id="twBeginTimeComp" name="twBeginTimeComp"
								value='<fmt:formatDate value="${condition.twBeginTime}" pattern="yyyy-MM-dd"/>'
								onClick="WdatePicker({lang:'<spring:message code="feedback.common.language1"/>',dateFmt:'yyyy-MM-dd',minDate:'2013-06-01'})">
							<spring:message code="log.operation.time.until" />
							<input readonly="readonly" class="Wdate span2" type="text"
								id="twEndTimeComp" name="twEndTimeComp"
								value='<fmt:formatDate value="${condition.twEndTime}" pattern="yyyy-MM-dd"/>'
								onClick="WdatePicker({lang:'<spring:message code="feedback.common.language1"/>',dateFmt:'yyyy-MM-dd',minDate:'2013-06-01'})">


						</div>
					</div>
					<div class="control-group">
						<label for="input" class="control-label"><spring:message
								code="feedback.manage.condition.customerName" />:</label>
						<div class="controls">
							<input type="text" class="span4" id="customerName"
								name="customerName" value="${condition.customerName}" />
						</div>
					</div>
				</div>
				<div class="form-right">

					<div class="control-group">
						<label for="input" class="control-label"><spring:message
								code="feedback.manage.condition.dfTime" />:</label>
						<div class="controls">
							<%--  <input type="hidden" id="dfBeginTime" name="dfBeginTime"/>
                	<input type="hidden" id="dfEndTime" name="dfEndTime"/>
					<input class="Wdate span2 span-two" readonly="readonly" type="text" id="dfBeginTimeComp"> 
					<spring:message code="log.operation.time.until"/>
					<input class="Wdate span2 span-two" readonly="readonly" type="text" id="dfEndTimeComp">  --%>
							<input type="hidden" id="dfBeginTime" style="width: 39%;"
								name="dfBeginTime" /> <input type="hidden" id="dfEndTime"
								style="width: 39%;" name="dfEndTime" /> <input type="hidden"
								id="timeZone" style="width: 39%;" name="timeZone" /> <input
								readonly="readonly" class="Wdate span2" type="text"
								id="dfBeginTimeComp" name="dfBeginTimeComp"
								value='<fmt:formatDate value="${condition.dfBeginTime}" pattern="yyyy-MM-dd"/>'
								onClick="WdatePicker({lang:'<spring:message code="feedback.common.language1"/>',dateFmt:'yyyy-MM-dd',minDate:'2013-06-01'})">
							<spring:message code="log.operation.time.until" />
							<input readonly="readonly" class="Wdate span2" type="text"
								id="dfEndTimeComp" name="dfEndTimeComp"
								value='<fmt:formatDate value="${condition.dfEndTime}" pattern="yyyy-MM-dd"/>'
								onClick="WdatePicker({lang:'<spring:message code="feedback.common.language1"/>',dateFmt:'yyyy-MM-dd',minDate:'2013-06-01'})">

						</div>
					</div>
					<div class="control-group">
						<label for="input" class="control-label"></label>
						<div class="controls">
							<button id="usersubmit_btn" type="button" class="btn btn-primary"
								onClick="doQuery()">
								<spring:message code="feedback.manage.condition.query" />
							</button>
							<button id="userreset_btn" type="button" class="btn"
								onClick="resetCondition()">
								<spring:message code="feedback.manage.condition.reset" />
							</button>
						</div>
					</div>
				</div>
			</div>
			<div class="table-con" style="min-height: 620px;">
				<table class="table table-bordered table-striped">
					<thead>
						<tr>
							<th style="width: 3%"><input type="checkbox" id="checkall"
								name="checkall" /></th>
							<th width="6%"><spring:message
									code="feedback.manage.title.num" /></th>
							<th width="12%"><spring:message
									code="feedback.manage.title.customer" /></th>
							<th width="14%"><spring:message
									code="feedback.manage.title.twTime" /></th>
							<th width="10%"><spring:message
									code="feedback.manage.title.problemType" /></th>
							<th width="21%"><spring:message
									code="feedback.manage.title.problemTitle" /></th>
							<th width="14%"><spring:message
									code="feedback.manage.title.dfTime" /></th>
							<th width="20%"><spring:message
									code="feedback.manage.title.operation" /></th>

						</tr>
					</thead>
					<tbody>
						<c:forEach items="${userFeedBackList.content}" var="feedback"
							varStatus="status">
							<tr>
								<td><input type="checkbox" style="width:12.9px" id="${feedback.problemID}"
									name="checkname" value="${feedback.problemID}" /></td>
								<td>${cse:htmlEscape(status.index + 1)}</td>
								<td title="${cse:htmlEscape(feedback.customerName)}">
									${cse:htmlEscape(feedback.customerName)}</td>
								<td><fmt:formatDate value="${feedback.newestTwTime}"
										pattern="yyyy-MM-dd HH:mm:ss" /></td>

								<td
									title="${cse:htmlEscape(feedback.problemType eq '1' ? '故障反馈':'意见建议')}">
									${cse:htmlEscape(feedback.problemType eq '1' ? '故障反馈':'意见建议')}
								</td>
								<td title="${cse:htmlEscape(feedback.problemTitle)}">
									${cse:htmlEscape(feedback.problemTitle)}</td>
								<td><fmt:formatDate value="${feedback.managerAnswerTime}"
										pattern="yyyy-MM-dd HH:mm:ss" /></td>
								<td>
									<button class="btn" type="button"
										onClick="showFeedBack(${cse:htmlEscape(feedback.problemID)})">
										<spring:message code="feedback.manage.title.query" />
									</button> <%-- 					<button class="btn" type="button" onClick="deleteFeedBack(${cse:htmlEscape(feedback.problemID)})"><spring:message code="feedback.manage.title.delete"/></button> --%>
									<button class="btn" type="button"
										onClick="modifyFeedBack(${cse:htmlEscape(feedback.problemID)})">
										<spring:message code="feedback.manage.title.answer" />
									</button>

								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
			<div id="feeebackPage"></div>
		</div>
	</form>
</body>
<script type="text/javascript">
$(document).ready(function() {
	$("#feeebackPage").comboPage({
		curPage : ${cse:htmlEscape(userFeedBackList.number)},
		lang : '<spring:message code="common.language1"/>',
		perDis : ${cse:htmlEscape(userFeedBackList.size)},
		totaldata : ${userFeedBackList.totalElements},
		style : "page table-page"
	})
		$("i[class=icon-chevron-right]").addClass("icon-br-next");
	var pageH = $("body").outerHeight();
	if(pageH<500)
	{
		pageH=500;
	}
	/* top.iframeAdaptHeight(pageH);
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
	
	$("#twBeginTimeComp").datepicker();
	$("#twEndTimeComp").datepicker();
	$("#dfBeginTimeComp").datepicker();
	$("#dfEndTimeComp").datepicker();
	
	
	
	$("#dfBeginTimeComp").datepicker("option", "dateFormat","yy-mm-dd"); 
	$("#dfEndTimeComp").datepicker("option", "dateFormat","yy-mm-dd"); 
	$("#twBeginTimeComp").datepicker("option", "dateFormat","yy-mm-dd"); 
	$("#twEndTimeComp").datepicker("option", "dateFormat","yy-mm-dd"); 
	
	$("#dfBeginTimeComp").datepicker('setDate','<fmt:formatDate value="${condition.dfBeginTime}" pattern="yyyy-MM-dd"/>');
	$("#dfEndTimeComp").datepicker('setDate','<fmt:formatDate value="${condition.dfEndTime}" pattern="yyyy-MM-dd"/>');
	$("#twBeginTimeComp").datepicker('setDate','<fmt:formatDate value="${condition.twBeginTime}" pattern="yyyy-MM-dd"/>');
	$("#twEndTimeComp").datepicker('setDate','<fmt:formatDate value="${condition.twEndTime}" pattern="yyyy-MM-dd"/>'); */
	
});

$.fn.comboPage.pageSkip = function(opts, _idMap, curPage){
	$("#page").val(curPage);
	doQuery();
};

function doQuery()
{
	var dfBeginTime = $("#dfBeginTimeComp").val();
	var dfEndTime = $("#dfEndTimeComp").val();
	
	var twBeginTime = $("#twBeginTimeComp").val();
	var twEndTime = $("#twEndTimeComp").val();
	
	
	if(dfEndTime != "" && dfBeginTime > dfEndTime){
		handlePrompt("error",'<spring:message code="log.handle.time.error"/>',null,60);
		return;
	}
	
	if(twEndTime != "" && twBeginTime > twEndTime){
		handlePrompt("error",'<spring:message code="log.handle.time.error"/>',null,60);
		return;
	}
	
	
	if(dfBeginTime != ""){
		$("#dfBeginTime").attr('name="dfBeginTime"');
		$("#dfBeginTime").val(dfBeginTime +" 00:00:00");
	}else{
		$("#dfBeginTime").removeAttr("name");
	}
	if(dfEndTime != ""){
		$("#dfEndTime").attr('name="dfBeginTime"');
		$("#dfEndTime").val(dfEndTime +" 23:59:59");
	}else{
		$("#dfEndTime").val(null);
		$("#dfEndTime").removeAttr("name");
	}
	
	
	if(twBeginTime != ""){
		$("#twBeginTime").attr('name="twBeginTime"');
		$("#twBeginTime").val(twBeginTime +" 00:00:00");
	}else{
		$("#twBeginTime").removeAttr("name");
	}
	if(twEndTime != ""){
		$("#twEndTime").attr('name="twBeginTime"');
		$("#twEndTime").val(twEndTime +" 23:59:59");
	}else{
		$("#twEndTime").val(null);
		$("#twEndTime").removeAttr("name");
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
	$("#customerName").val("");
	$("#twBeginTimeComp").val("");
	$("#twEndTimeComp").val("");
	$("#dfBeginTimeComp").val("");
	$("#dfEndTimeComp").val("");
}


function deleteFeedBack(problemID) {
	top.ymPrompt.confirmInfo( {
		title :'<spring:message code="feedback.list.delete.title"/>',
		message : '<spring:message code="feedback.list.delete.message"/>',
		closeTxt:'<spring:message code="common.close"/>',
		handler : function(tp) {
			if(tp == "ok"){
				$.ajax({
			        type: "POST",
			        url:"${ctx}/feedback/uam/deleteFeedBack",
			        data:{"problemID":problemID,"token" : "${token}"},
			        error: function(request) {
			        	top.handlePrompt("error",'<spring:message code="feedback.list.deleteFail"/>');
			        },
			        success: function() {
			        	top.handlePrompt("success",'<spring:message code="feedback.list.deleteSuccess"/>');
			        	refreshWindow();
			        }
			    });
			}
		},
		btn: [['<spring:message code="common.OK"/>', "ok"],['<spring:message code="common.cancel"/>', "cancel"]]
	});
}

function modifyFeedBack(problemID){
	top.document.body.parentNode.style.overflowY="hidden";
	top.ymPrompt.win({message:'${ctx}/feedback/uam/toAnswer?problemID='+problemID,width:700,height:480,title:'<spring:message code="feedback.manage.title.update"/>', iframe:true,btn:[['<spring:message code="feedback.manage.title.answer"/>','yes',false,"btnModify"],['<spring:message code="common.cancel"/>','no',true,"btnModifyCancel"]],handler:doModifyFeedBack});
	top.ymPrompt_addModalFocus("#btnModify");
}

function doModifyFeedBack(tp){
	if (tp == 'yes') {
		top.ymPrompt.getPage().contentWindow.submitModifyFeedBack();
	} else {
		top.document.body.parentNode.style.overflowY="auto";
		top.ymPrompt.close();
	}
}


function showFeedBack(problemID){
	top.document.body.parentNode.style.overflowY="hidden";
	top.ymPrompt.win({message:'${ctx}/feedback/uam/detail?problemID='+problemID,width:700,height:480,title:'<spring:message code="feedback.manage.title.detail"/>', iframe:true,handler:doShowFeedBack});
}

function doShowFeedBack(tp){
	if (tp == 'yes') {
		top.ymPrompt.getPage().contentWindow.submitShowFeedBack();
	} else {
		top.document.body.parentNode.style.overflowY="auto";
		top.ymPrompt.close();
	}
}

$("#checkall").click(function(){ 
	if(this.checked){ 
		$("input[name='checkname']:checkbox").each(function(){
			this.checked=true;
		});
	}else{ 
		$("input[name='checkname']:checkbox").each(function(){
			 this.checked=false;
		});
	}
});

function deletelistFeedBack(){
	var ids = '';
	$("input[name='checkname']:checked").each(function () {
        if (ids != '') {
        	ids = ids + "," + this.value;
        } else {
        	ids = this.value;
        }
    });
	if (ids == '') {
		handlePrompt("error",'<spring:message code="delete.feedback.list.err"/>');
		return;
	}
	top.ymPrompt.confirmInfo( {
		title :'<spring:message code="delete.feedback.list.title"/>',
		message : '<spring:message code="delete.feedback.list.message"/>',
		closeTxt:'<spring:message code="common.close"/>',
		handler : function(tp) {
			if(tp == "ok"){
					$.ajax({
			        type: "POST",
			        url:"${ctx}/feedback/uam/deleteList",
			        data:{ids:ids,"token" : "${token}"},
			        error: function(request) {
			        	top.handlePrompt("error",'<spring:message code="delete.feedback.list.delFail"/>');
			        },
			        success: function() {
			        	top.handlePrompt("success",'<spring:message code="delete.feedback.list.delSuccess"/>');
			        	refreshWindow();
			        }
			    });
			}
		},
		btn: [['<spring:message code="common.OK"/>', "ok"],['<spring:message code="common.cancel"/>', "cancel"]]
	});
}

</script>
</html>
