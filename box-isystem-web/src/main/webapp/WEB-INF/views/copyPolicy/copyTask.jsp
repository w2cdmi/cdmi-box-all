<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="cse" uri="http://cse.huawei.com/custom-function-taglib"%>  
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.Map"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
<%@ include file="../common/common.jsp"%>
</head>
<body>
<%
	Map<String, String> allMap=(HashMap<String,String>)request.getAttribute("allmap");
	Map<String, String> policymap=(HashMap<String,String>)request.getAttribute("policymap");
 %>
<div class="sys-content">
		<div class="clearfix">
			<div class="pull-left">
				<label class="control-label" for=""><h5>
						<spring:message code="copyPolicy.mirror.overall" />
					</h5></label>
			</div>
		</div>
    <div class="clearfix">
    	<div class="pull-left">
	    	<button type="button" class="btn btn-primary" <c:if test="${isDisable==true}">disabled</c:if> onClick="taskState()">
	    	<c:if test="${state==0}"><spring:message code="copyPolicy.mirror.pass.allTask" /></c:if>
	    	<c:if test="${state==4}"><spring:message code="copyPolicy.mirror.starte.allTask" /></c:if>
	    	</button>
        </div>   
        <div class="pull-right">
        	<label class="control-label" for=""><h5>${cse:htmlEscape(CurTaskStartTime)}</h5></label>
        </div>
        <div class="pull-right">
        	<label class="control-label" for=""><h5><spring:message code="mirrorbackscantask.start.time" /></h5></label>
        </div>
        <div class="pull-right">
        	<label class="control-label" for=""><h5>${cse:htmlEscape(ScanTotalTime)}&nbsp&nbsp&nbsp</h5></label>
        </div>
        <div class="pull-right">
        	<label class="control-label" for=""><h5><spring:message code="mirrorbackscantask.total.time" /></h5></label>
        </div>
    </div>
    <div class="table-con clearfix">
        <table class="table table-bordered table-striped">
          <thead>
            <tr>
            	<th ></th>
                <th ><spring:message code="copy.task.total"/></th>
                <th ><spring:message code="copy.task.size"/></th>
            </tr>
          </thead>
          <tbody>
            <tr >
            	<td ><spring:message code="copyPolicy.mirror.allTask"/></td>
                <td   title="${cse:htmlEscape(copyTaskStatistic.allTaskNum)}">${cse:htmlEscape(copyTaskStatistic.allTaskNum)}</td>
       		    <td   title="${cse:htmlEscape(copyTaskStatistic.allSize)}"><%=null==allMap?"":allMap.get("all") %></td>
            </tr>
             <tr >
            	<td ><spring:message code="copyPolicy.mirror.timing.task"/></td>
                <td   title="${cse:htmlEscape(copyTaskStatistic.noactivateTaskNum)}">${cse:htmlEscape(copyTaskStatistic.noactivateTaskNum)}</td>
       		    <td   title="${cse:htmlEscape(copyTaskStatistic.noactivateTaskSize)}"><%=null==allMap?"":allMap.get("not") %></td>
            </tr>
            <tr >
            	<td ><spring:message code="copyPolicy.mirror.waiting.task"/></td>
                <td   title="${cse:htmlEscape(copyTaskStatistic.waitingTaskNum)}">${cse:htmlEscape(copyTaskStatistic.waitingTaskNum)}</td>
       		    <td   title="${cse:htmlEscape(copyTaskStatistic.waitingTaskSize)}"><%=null==allMap?"":allMap.get("wait") %></td>
            </tr>
            <tr >
            	<td ><spring:message code="copyPolicy.mirror.performed.task"/></td>
                <td   title="${cse:htmlEscape(copyTaskStatistic.exeingTaskNum)}">${cse:htmlEscape(copyTaskStatistic.exeingTaskNum)}</td>
       		    <td   title="${cse:htmlEscape(copyTaskStatistic.exeingTaskSize)}"><%=null==allMap?"":allMap.get("exe") %></td>
            </tr>
            <tr >
            	<td ><spring:message code="copyPolicy.mirror.fail.task"/></td>
                <td   title="${cse:htmlEscape(copyTaskStatistic.failedTaskNum)}">${cse:htmlEscape(copyTaskStatistic.failedTaskNum)}</td>
       		    <td   title="${cse:htmlEscape(copyTaskStatistic.failedTaskSize)}"><%=null==allMap?"":allMap.get("failed") %></td>
            </tr>
          </tbody>
        </table>
    </div>
    <div class="clearfix">
		<div class="clearfix">
			<div class="pull-left">
				<label class="control-label" for=""><h5>
						<spring:message code="copyPolicy.mirror.sinagle.task" />
					</h5></label>
			</div>
		</div>
    	<div class="pull-left input-append">
	    	<select class="span4" id=policyId name="policyId">
        					<c:forEach items="${policies}" var="p">
        						<option value="${cse:htmlEscape(p.id)}" <c:if test="${policy.id== p.id}">selected="selected"</c:if>>${cse:htmlEscape(p.name)}</option>
        					</c:forEach>
			</select>
	    	<button type="button" class="btn btn-primary" onClick="getPolicy()"></i><spring:message code="log.search" /></button>
        </div>
    </div>
    <div class="table-con clearfix">
        <table class="table table-bordered table-striped">
          <thead>
            <tr>
            	<th ></th>
                <th ><spring:message code="copy.task.total"/></th>
                <th ><spring:message code="copy.task.size"/></th>
            </tr>
          </thead>
          <tbody>
            <tr >
            	<td ><spring:message code="copyPolicy.mirror.gobale.task"/></td>
                <td   title="${cse:htmlEscape(copytask.allTaskNum)}">${cse:htmlEscape(copytask.allTaskNum)}</td>
       		    <td   title="${cse:htmlEscape(copytask.allSize)}"><%=null==policymap?"":policymap.get("all") %></td>
            </tr>
             <tr >
            	<td ><spring:message code="copyPolicy.mirror.timing.task"/></td>
                <td   title="${cse:htmlEscape(copytask.noactivateTaskNum)}">${cse:htmlEscape(copytask.noactivateTaskNum)}</td>
       		    <td   title="${cse:htmlEscape(copytask.noactivateTaskSize)}"><%=null==policymap?"":policymap.get("not") %></td>
            </tr>
            <tr >
            	<td ><spring:message code="copyPolicy.mirror.waiting.task"/></td>
                <td   title="${cse:htmlEscape(copytask.waitingTaskNum)}">${cse:htmlEscape(copytask.waitingTaskNum)}</td>
       		    <td   title="${cse:htmlEscape(copytask.waitingTaskSize)}"><%=null==policymap?"":policymap.get("wait") %></td>
            </tr>
            <tr >
            	<td ><spring:message code="copyPolicy.mirror.performed.task"/></td>
                <td   title="${cse:htmlEscape(copytask.exeingTaskNum)}">${cse:htmlEscape(copytask.exeingTaskNum)}</td>
       		    <td   title="${cse:htmlEscape(copytask.exeingTaskSize)}"><%=null==policymap?"":policymap.get("exe") %></td>
            </tr>
            <tr >
            	<td ><spring:message code="copyPolicy.mirror.fail.task"/></td>
                <td   title="${cse:htmlEscape(copytask.failedTaskNum)}">${cse:htmlEscape(copytask.failedTaskNum)}</td>
       		    <td   title="${cse:htmlEscape(copytask.failedTaskSize)}"><%=null==policymap?"":policymap.get("failed") %></td>
            </tr>
          </tbody>
        </table>
    </div>
</div>
</body>
<script type="text/javascript">
$(function(){
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
})
function taskState()
{
	var state;
	var temp =${state}+0;
	if(temp==0)
	{
		state=4;
	}else
	{
		state=0;
	}
	$.ajax({
        type: "POST",
        url:"${ctx}/mirror/copyTask/updateState",
        data:{state:state,token:'${cse:htmlEscape(token)}'},
        error: function(request) {
        	top.handlePrompt("error",'<spring:message code="authorize.status.modified.fail"/>');
        },
        success: function() {
        	top.handlePrompt("success",'<spring:message code="authorize.status.modified.success"/>');
        	refreshWindow();
        }
    });	
}

function getPolicy()
{
	var id=$("#policyId").val()
	if(id==null)
	{
		id=-1;
	}
	window.location.href ='${ctx}/mirror/copyTask/list/'+id+'/policy';
}


function refreshWindow() {
	getPolicy();
}
</script>
</html>
