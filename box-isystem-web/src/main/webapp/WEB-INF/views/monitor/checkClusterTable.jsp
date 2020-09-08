<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="cse" uri="http://cse.huawei.com/custom-function-taglib"%>  
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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
    <h5><spring:message code="monitor.network.info"/></h5>
    <table class="table table-bordered">
        <thead >
        <tr>
            <th class="al"><spring:message code="monitor.network.name"/></th>
            <th><spring:message code="monitor.ipaddress"/></th>
            <th><spring:message code="monitor.network.status"/></th>
            <th><spring:message code="monitor.network.rate"/></th>
        </tr>
        
        
        </thead>
        <tbody>
        <tr>
            <td><spring:message code="monitor.network.service"/></td>
            <td>${cse:htmlEscape(oneNode.serviceIp)}</td>
            <td>${cse:htmlEscape(oneNode.serviceStatus)}</td>
            <td>${cse:htmlEscape(oneNode.serviceRate)}</td>
        </tr>
        <tr>    
            <td><spring:message code="monitor.network.private"/></td>
            <td>${cse:htmlEscape(oneNode.privateIp)}</td>
            <td>${cse:htmlEscape(oneNode.privateStatus)}</td>
            <td>${cse:htmlEscape(oneNode.privateRate)}</td>
         </tr>
         <tr> 
            <td><spring:message code="monitor.network.manage"/></td>
            <td>${cse:htmlEscape(oneNode.manageIp)}</td>
            <td>${cse:htmlEscape(oneNode.manageStatus)}</td>
            <td>${cse:htmlEscape(oneNode.manageRate)}</td>
            </tr>
         <tr>
            <td>IPMI</td>
            <td>${cse:htmlEscape(oneNode.ipmi)}</td>
            <td>-</td>
            <td>-</td>
        </tr>
        </tbody>
    </table>

    <h5><spring:message code="monitor.cpu.info"/></h5>
    <table class="table table-bordered">
        <thead >
        <tr>
            <th><spring:message code="monitor.cpu.cores"/></th>
            <th><spring:message code="monitor.cpu.threads"/></th>
            <th><spring:message code="monitor.cpu.rate"/></th>
        </tr>
        </thead>
        <tbody>
        <tr>
            <td>${cse:htmlEscape(oneNode.cpuCount)}</td>
            <td>${cse:htmlEscape(oneNode.cpuThread)}</td>
            <td>${cse:htmlEscape(oneNode.cpuUsage)}</td>
        </tr>
        </tbody>
    </table>

    <h5><spring:message code="monitor.memory.info"/></h5>
    <table class="table table-bordered">
        <thead >
        <tr>
            <th><spring:message code="monitor.memory.total"/></th>
            <th><spring:message code="monitor.memory.used"/></th>
            <th><spring:message code="monitor.memory.rate"/></th>
        </tr>
        </thead>
        <tbody>
        <tr>
            <td>${cse:htmlEscape(oneNode.memoryTotal)}</td>
            <td>${cse:htmlEscape(oneNode.memoryUsage)}</td>
            <td>${cse:htmlEscape(oneNode.memoryRate)}</td>
        </tr>
        </tbody>
    </table>

    <h5><spring:message code="monitor.disk.info"/></h5>
    <table class="table table-bordered">
        <thead >
        <tr>
            <th><spring:message code="monitor.disk.name"/></th>
            <th><spring:message code="monitor.disk.total"/></th>
            <th><spring:message code="monitor.disk.used"/></th>
            <th><spring:message code="monitor.disk.free"/></th>
            <th><spring:message code="monitor.disk.rate"/></th>
        </tr>
        </thead>
        <tbody>
      
        <c:forEach items="${disks}" var="disk">
            <tr>
                <td title=" ${cse:htmlEscape(disk.catalogueName)}">${cse:htmlEscape(disk.catalogueName)}</td>
                <td title=" ${cse:htmlEscape(disk.total)}">${cse:htmlEscape(disk.total)}</td>
                <td title=" ${cse:htmlEscape(disk.used)}">${cse:htmlEscape(disk.used)}</td>
                <td title=" ${cse:htmlEscape(disk.residue)}">${cse:htmlEscape(disk.residue)}</td>
                <td title=" ${cse:htmlEscape(disk.rate)}">${cse:htmlEscape(disk.rate)}</td>
            </tr>
        </c:forEach>
        
        </tbody>
    </table>

    <h5><spring:message code="monitor.diskio.info"/></h5>
    <table class="table table-bordered">
        <thead >
        <tr>
            <th><spring:message code="monitor.diskio.name"/></th>
            <th><spring:message code="monitor.diskio.avg"/></th>
            <th><spring:message code="monitor.diskio.rate"/></th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${diskIOs}" var="diskIO">
            <tr>
                <td title=" ${cse:htmlEscape(diskIO.diskName)}">${cse:htmlEscape(diskIO.diskName)}</td>
                <td title=" ${cse:htmlEscape(diskIO.avgeResponeTime)}">${cse:htmlEscape(diskIO.avgeResponeTime)}</td>
                <td title=" ${cse:htmlEscape(diskIO.rate)}">${cse:htmlEscape(diskIO.rate)}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    
    <h5><spring:message code="monitor.process.info"/></h5>
    <table class="table table-bordered">
        <thead >
        <tr>
            <th><spring:message code="monitor.process.name"/></th>
            <th><spring:message code="monitor.process.role"/></th>
            <th><spring:message code="monitor.process.processes"/></th>
            <th><spring:message code="monitor.process.threads"/></th>
            <th><spring:message code="monitor.process.handler"/></th>
            <th><spring:message code="monitor.process.port"/></th>
            <th><spring:message code="monitor.process.memory.rate"/></th>
            <th><spring:message code="monitor.process.cpu.rate"/></th>
            <th><spring:message code="monitor.process.syn"/></th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${processInfos}" var="processInfo">
            <tr>
                <td title=" ${cse:htmlEscape(processInfo.processName)}">${cse:htmlEscape(processInfo.processName)}</td>
                <td title=" ${cse:htmlEscape(processInfo.role)}">${cse:htmlEscape(processInfo.role)}</td>
                <c:if test='${processInfo.processCount>=0 }'>
                	<td title=" ${cse:htmlEscape(processInfo.processCount)}">${cse:htmlEscape(processInfo.processCount)}</td>
                </c:if>
                <c:if test='${processInfo.processCount<0 }'>
                	<td title=" ${cse:htmlEscape(processInfo.processCount)}">-</td>
                </c:if>
                <c:if test='${processInfo.threadTotal>=0 }'>
                	<td title=" ${cse:htmlEscape(processInfo.threadTotal)}">${cse:htmlEscape(processInfo.threadTotal)}</td>
                </c:if>
                <c:if test='${processInfo.threadTotal<0 }'>
                	<td title=" ${cse:htmlEscape(processInfo.threadTotal)}">-</td>
                </c:if>
                <c:if test='${processInfo.fileHandleTotal>=0 }'>
                	<td title=" ${cse:htmlEscape(processInfo.fileHandleTotal)}">${cse:htmlEscape(processInfo.fileHandleTotal)}</td>
                </c:if>
                <c:if test='${processInfo.fileHandleTotal<0 }'>
                	<td title=" ${cse:htmlEscape(processInfo.fileHandleTotal)}">-</td>
                </c:if>
               
               	<td title=" ${cse:htmlEscape(processInfo.port)}">${cse:htmlEscape(processInfo.port)}</td>
               
                <c:if test='${processInfo.memoryUsage>=0 }'>
                	<td title=" ${cse:htmlEscape(processInfo.memoryUsage)}">${processInfo.memoryUsage}</td>
                </c:if>
                <c:if test='${processInfo.memoryUsage<0 }'>
                	<td title=" ${cse:htmlEscape(processInfo.memoryUsage)}">-</td>
                </c:if>
                
                <c:if test='${processInfo.cpuUsage>=0 }'>
               		<td title=" ${cse:htmlEscape(processInfo.cpuUsage)}">${processInfo.cpuUsage}</td>
                </c:if>
                <c:if test='${processInfo.cpuUsage<0 }'>
               		<td title=" ${cse:htmlEscape(processInfo.cpuUsage)}">-</td>
                </c:if>
                <!-- jstl 没有equalsIgnoreCase方法 -->
                <%--<c:if test='${"YES".equalsIgnoreCase(processInfo.syn) }'> --%>
                <c:if test='"YES" == ${fn.UpperCase(processInfo.syn)}'>
                	<td title=" ${cse:htmlEscape(processInfo.syn)}"><i class="icon-status-normal"></i>${cse:htmlEscape(processInfo.syn)}</td>
				</c:if>
				<%--<c:if test='${"NO".equalsIgnoreCase(processInfo.syn) }'> --%>
                <c:if test='"NO" == ${fn.UpperCase(processInfo.syn)}'>
                	<td title=" ${cse:htmlEscape(processInfo.syn)}"><i class="icon-status-abnormal"></i>${cse:htmlEscape(processInfo.syn)}</td>
				</c:if>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
</body>
</html>