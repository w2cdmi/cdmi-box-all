<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="cse" uri="http://cse.huawei.com/custom-function-taglib"%>  
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="Cache-Control" content="no-cache" />
<meta http-equiv="Pragma" content="no-cache" />
<title></title>
<%@ include file="../common/common.jsp"%>
</head>
<body>
<div class="clu-table">
    <table class="table table-bordered">
        <thead >
        <tr >
            <th><spring:message code="monitor.mysqlservice.group"/></th>
            <th><spring:message code="monitor.mysqlservice.vip"/></th>
            <th><spring:message code="monitor.mysqlservice.hostname"/></th>
            <th><spring:message code="monitor.mysqlservice.role"/></th>
            <th><spring:message code="monitor.mysqlservice.status"/></th>
        </tr>
        </thead>
        <tbody>
            
            <c:forEach items="${services}" var="service">
                    		<tr>
                    		<td>${cse:htmlEscape(service.serviceName)}</td>
							<td>${cse:htmlEscape(service.vip)}</td>
							<td class="table-createdc-first table-createdc">
								<table class="table">
									<c:forEach items="${service.instances}" var="instance">
						                    <tr><td>${cse:htmlEscape(instance.hostName)}</td></tr>
									</c:forEach>
								</table>
							</td>
							<td class="table-createdc-first table-createdc">
								<table class="table">
									<c:forEach items="${service.instances}" var="instance">
						                    <tr><td>${cse:htmlEscape(instance.runRole)}</td></tr>
									</c:forEach>
								</table>
							</td>
							<td class="table-createdc-first table-createdc">
								<table class="table">
									<c:forEach items="${service.instances}" var="instance">
									<tr>
						                    <c:if test='${instance.status == 0}'>
				                    		<td><i class="icon-status-normal"></i><spring:message code="monitor.status.normal"/></td>
				                    		</c:if>
				                    		<c:if test='${instance.status !=0}'>
				                    		<td><i class="icon-status-abnormal"></i><spring:message code="monitor.status.abnormal"/></td>
				                    		</c:if>
				                    		
				                   </tr>
									</c:forEach>
								</table>
							</td>
                     		</tr>
        	</c:forEach>
        
        </tbody>
    </table>
</div>
</body>
</html>