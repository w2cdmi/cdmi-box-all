<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="cse" uri="http://cse.huawei.com/custom-function-taglib"%>  
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
<%@ include file="../common/common.jsp"%>
<script src="${ctx}/static/js/public/JQbox-hw-switchButton.js" type="text/javascript"></script>
</head>
<body>

<div class="sys-content" id="parent" <c:if test="${isDetail}">style="display: none"</c:if>>
 <c:forEach items="${speedlist}" var="sp">
   	<c:if test="${sp.times==speedlist.size()}">
		<div class="pull-left" style="width:100%">
    		<h5><spring:message code="data.Migration.speed.title"  arguments="${sp.times},${sp.prosess}" argumentSeparator=","/></h5>
    		<div class="scan-info" >
					<p id="statusBar"></p>
					<div class="progress progress-info"><div id="processing" class="bar" style="width:${sp.prosess}%"></div></div>
			</div>
    	</div>
   	</c:if>
  	<c:if test="${sp.times<speedlist.size()}">
		<div class="pull-left">
    		<h5><spring:message code="data.Migration.speed.his"  arguments="${sp.times}" argumentSeparator=","/></h5>
    	</div>
   	</c:if>
    <div class="table-con clearfix">
        <table class="table table-bordered table-striped">
          <thead>
            <tr>
                <th ><spring:message code="data.Migration.speed.sweed.total"/></th>
                <th ><spring:message code="common.success"/></th>
                <th ><spring:message code="common.fail"/></th>
                <th ><spring:message code="data.Migration.speed.start.time"/></th>
                <th><spring:message code="data.Migration.speed.end.time"/></th>
                <th width="17%"><spring:message code="common.operation"/></th>
            </tr>
          </thead>
          <tbody>
             <tr >
                <td   title="${cse:htmlEscape(sp.sweep)}">${cse:htmlEscape(sp.sweep)}</td>
                <td   title="${cse:htmlEscape(sp.success)}">${cse:htmlEscape(sp.success)}</td>
                <td   title="${cse:htmlEscape(sp.fail)}" <c:if test="${sp.needRed}"> style="color:#FF0000"	</c:if>>
                	
                		${cse:htmlEscape(sp.fail)}
            
                 </td>
                
                 <td title='<fmt:formatDate value="${sp.startTime}" pattern="yyyy-MM-dd HH:mm:ss"/>'>
                 <fmt:formatDate value="${sp.startTime}" pattern="yyyy-MM-dd HH:mm"/>
                </td>
                <td
                  <c:if test="${empty sp.endTime }">
                      		 title='-'
                  </c:if>
                  <c:if test="${not empty sp.endTime}">
                        title='<fmt:formatDate value="${sp.endTime}" pattern="yyyy-MM-dd HH:mm:ss"/>'
                 </c:if>
                 > 
                 <c:if test="${empty sp.endTime }">
                      		  -
                  </c:if>
                  <c:if test="${not empty sp.endTime}">
                        	<fmt:formatDate value="${sp.endTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
                 </c:if>
                </td>
                <td >
                    <a href="javascript:void(0)"  onClick="javascript:changeListDetail('${sp.id}');"><spring:message code="data.Migration.speed.detail"/></a>
                </td>
            </tr>
   
          </tbody>
        </table>
    </div>
    
            </c:forEach>
    
</div>

<div class="panel panel-default" id="child" <c:if test="${!isDetail}">style="display: none"</c:if>>
	<table class="pre-scrollable" width="100%">
	<tr>
		<td><spring:message
			code="data.Migration.speed.sweed.total" />:</td>
		<td>${view.sweep}</td>
		<td><spring:message
			code="data.Migreation.date" />:</td>
		<td><fmt:formatDate value="${view.startTime}" pattern="yyyy-MM-dd HH:mm"/>
		<c:if test="${not empty view.endTime}">
			<spring:message	code="log.till" />
			<fmt:formatDate value="${view.endTime}" pattern="yyyy-MM-dd HH:mm"/>
		</c:if></td>
	</tr>
	<tr>
		<td><spring:message
			code="common.success" />:</td>
		<td>${view.success}</td>
		<td><spring:message
			code="common.fail" />:</td>
		<td<c:if test="${view.needRed}"> style="color:#FF0000"	</c:if>>
		${view.fail}</td>
	</tr>
	</table>
	<table class="table table-bordered table-striped">
          <thead>
            <tr>
                <th ><spring:message code="data.Migreation.date"/></th>
                <th ><spring:message code="data.Migreation.time"/></th>
                <th ><spring:message code="data.Migreation.capacity"/></th>
                <th ><spring:message code="data.Migreation.number"/></th>
            </tr>
          </thead>
          <c:forEach items="${Processes}" var="process">
          	<tr>
          		<td>
          			 <fmt:formatDate value="${process.startTime}" pattern="yyyy-MM-dd"/>
          		</td>
          		<td>
          			<fmt:formatDate value="${process.startTime}" pattern="hh:mm:ss"/>-<fmt:formatDate value="${process.endTime}" pattern="hh:mm:ss"/>
          		</td>
          		<td>
          			${process.newAddSizes}GB
          		</td>
          		<td>
          		${process.newAddFiles}
          		</td>
          	</tr>
          </c:forEach>
	</table>
	<div class="controls">
		<button type="button" class="btn btn-primary" onclick="back()"><spring:message	code="common.back" /></button>
	</div>
</div>

</div>
</div>
</body>
<script type="text/javascript">
function changeListDetail(id)
{
	 window.location="${ctx}/mirror/copyPolicy/speedProcess/detail/"+${id}+"/"+id;	
}
function back()
{
	window.location="${ctx}/mirror/copyPolicy/speedProcess/"+${id};
}

</script>
</html>
