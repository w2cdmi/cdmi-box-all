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
<form id="searchForm" name="searchForm" action="${ctx}/enterprise/adminstratorlog/logview" method="post"> 
<input type="hidden" id="page" name="page" value="1">
<input type="hidden" id="token" name="token" value="${token}"/>
<div class="sys-content">
    <div class="clearfix control-group">
		<a class="return btn btn-small pull-right" href="${ctx}/enterprise/adminstratorlog/enterprisrlist" data-original-title="" title=""><i class="icon-backward"></i>&nbsp;<spring:message  code="common.back"  /></a>
		<h5 class="pull-left" style="margin: 3px 0 0 4px;"><a href="${ctx}/enterprise/adminstratorlog/enterprisrlist" data-original-title="" title=""><c:out value="${enterprise.name}"/></a>&nbsp;&gt;&nbsp;<spring:message  code="log.operation"  /></h5>	
		<input type="hidden" name="enterpriseId" value="<c:out value='${enterprise.id}'/>">
	</div>
	<div class="form-horizontal form-con clearfix">
		<div class="form-left" style="width:42%">
	        <div class="control-group">
	            <label for="input" class="control-label"><spring:message  code="log.operation.details"  />:</label>
	            <div class="controls">
	            
	            <c:if test="${qc.operatDesc != null}">
	               <input type="text" class="span4" id="operater" name="operatDesc" value="<c:out value='${qc.operatDesc}'/>" maxlength="1024">
	            </c:if>
	            <c:if test="${qc.operatDesc == null}">
	               <input type="text" class="span4" id="operater" name="operatDesc" value="" maxlength="1024">
	            </c:if>
	            </div>
	        </div>
	       
	    </div>
	    <div class="form-right"  style="width:57%">
	         <div class="control-group">
	            <label for="input" class="control-label"><spring:message  code="log.operation.time"  />:</label>
	            <div class="controls">
	                <input type="hidden" id="startTime" name="startTime"/>
                	<input type="hidden" id="endTime" name="endTime"/>
                	<input type="hidden" id="timeZone" name="timeZone"/>
					<input readonly="true" class="Wdate span2" type="text" id="startTimeComp" name="startTimeComp" value='<fmt:formatDate value="${qc.startTime}" pattern="yyyy-MM-dd"/>' onClick="WdatePicker({lang:'<spring:message code="main.languageCalendrical"/>',dateFmt:'yyyy-MM-dd',minDate:'2013-06-01'})"> <spring:message  code="log.operation.time.until"  />
					<input readonly="true" class="Wdate span2" type="text" id="endTimeComp" name="endTimeComp" value='<fmt:formatDate value="${qc.endTime}" pattern="yyyy-MM-dd"/>' onClick="WdatePicker({lang:'<spring:message code="main.languageCalendrical"/>',dateFmt:'yyyy-MM-dd',minDate:'2013-06-01'})">
	            	&nbsp;&nbsp;<button id="submit_btn" type="button" class="btn btn-primary"><spring:message  code="common.query"  /></button>
	            	&nbsp;&nbsp;<button id="submit_btn" type="button" class="btn" onClick="resetCondition()"><spring:message  code="common.reset"  /></button>
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
                <th><spring:message  code="log.operation.details"  /></th>
                <th width="15%"><spring:message  code="log.operation.app"  /></th>
                <th width="8%"><spring:message  code="log.operation.result"  /></th>
                <th width="8%"><spring:message code="log.operation.level"/></th>
                <th width="15%"><spring:message  code="log.operation.address"  /></th>
            </tr>
          </thead>
          <tbody>
          <c:forEach items="${pageList.content}" var="log">
            <tr>
                <td><fmt:formatDate value="${log.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                <td  title="${cse:htmlEscape(log.loginName)}">
                    ${cse:htmlEscape(log.loginName)}
                </td>
                <td><a onclick="showMsg(this)">${cse:htmlEscape(log.operatDesc)}</a></td>
                <td title="${cse:htmlEscape(log.appId)}">${cse:htmlEscape(log.appId)}</td>
                <td><c:if test="${log.level == 0}"><spring:message code="common.success"/></c:if><c:if test="${log.level == 1}"><spring:message code="common.failed"/></c:if></td>
               	<td title="${cse:htmlEscape(log.operatLevel)}">${cse:htmlEscape(log.operatLevel)}</td>
                <td title="${cse:htmlEscape(log.ip)}"><c:out value='${log.ip}'/></td>
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
		curPage : <c:out value='${pageList.number}'/>,
		perDis : <c:out value='${pageList.size}'/>,
		totaldata : ${pageList.totalElements},
		style : "page table-page"
	})
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
});
$(function(){
	$("#submit_btn").bind("click",function(){
		var startTime = $("#startTimeComp").val();
		var endTime = $("#endTimeComp").val();
		if(endTime != "" && startTime > endTime){
			handlePrompt("error",'<spring:message  code="log.operation.err.time"  />',null,60);
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
	});


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
		handlePrompt("error",'<spring:message  code="log.operation.err.time"  />',null,60);
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
	$("#startTimeComp").val("");
	$("#endTimeComp").val("");
	$("#operater").val("");
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
	return "GMT+" +  gmtHours + ":" + gmtMinute;
}

function showMsg(obj){
	var _$obj= $(obj);
	var url = "<div style='line-height:20px; padding:10px 0; min-height:210px;'>"+ _$obj.text()+"</div>";
	top.ymPrompt.win({message:url,width:500,height:350,title:'<spring:message code="log.operation.details"/>', iframe:false,btn:[['<spring:message code="common.close"/>','ok',false,"btn-focus"]],handler:closeSpaceView});
    top.ymPrompt_addModalFocus("#btn-focus");
}

function closeSpaceView(){
    top.ymPrompt.close();
}
</script>
</html>
