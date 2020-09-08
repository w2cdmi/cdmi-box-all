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
    <div class="sys-content cluster-con">
        <div id="clusterMenu" class="clu-menu">
            <div class="table-menu">
                <table class="table-tr table">
                    <tbody>
                    <c:forEach items="${clusters}" var="oper">
                    <tr onclick="viewDetail(this,'${cse:htmlEscape(oper.clusterName)}')">
                            <td title='${cse:htmlEscape(oper.systemName)}' width="65%"><i class="icon-left"></i><a href="#none">${cse:htmlEscape(oper.systemName)}</a></td>
                            	<c:if test='${oper.status == 0}'>
	                    		<td><i class="icon-status-normal"></i><spring:message code="monitor.status.normal"/></td>
	                    		</c:if>
	                    		<c:if test='${oper.status == 1}'>
	                    		<td><i class="icon-status-abnormal"></i><spring:message code="monitor.status.abnormal"/></td>
	                    		</c:if>
	                    		<c:if test='${oper.status == 2}'>
	                    		<td><i class="icon-status-partnormal"></i><spring:message code="monitor.status.part.abnormal"/></td>
	                    		</c:if>
                     </tr>
        			</c:forEach>
					
					<c:forEach items="${csSystems}" var="oper">
        			 <tr onclick="viewCsDetail(this,'${cse:htmlEscape(oper.systemName)}')">
                         <td title='${cse:htmlEscape(oper.systemName)}' width="65%"><i class="icon-left"></i><a href="#none">${cse:htmlEscape(oper.systemName)}</a></td>
                            <c:if test='${oper.status == 0}'>
	                    		<td><i class="icon-status-normal"></i><spring:message code="monitor.status.normal"/></td>
	                    	</c:if>
	                    	<c:if test='${oper.status == 1}'>
	                    		<td><i class="icon-status-abnormal"></i><spring:message code="monitor.status.abnormal"/></td>
	                    	</c:if>
	                    	<c:if test='${oper.status == 2}'>
	                    		<td><i class="icon-status-partnormal"></i><spring:message code="monitor.status.part.abnormal"/></td>
	                    	</c:if>
                     </tr>
                     </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
        
        <div class="clu-list">
            <iframe frameborder="0" width="100%"  id="rightContent"></iframe>
        </div>
    </div>
</body>
</html>
<script>
$(function(){
	var myH = $(top.window).height()-143;
	$("#rightContent,#clusterMenu").css("height",myH);
	
	$(top.window).bind("resize",function(){
		var myH = $(top.window).height()-143;
		$("#rightContent,#clusterMenu").css("height",myH);
		
		var pageH = $("body").outerHeight();
		top.iframeAdaptHeight(pageH);
	});
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
	
	$("#clusterMenu").find("tr:first-child").addClass("active");
	$("#clusterMenu").find("tr:first-child").click();
})
    function viewDetail(that,clusterName){
	       var statusValue = -1;
			   $.ajax({
			        type: "GET",
			        async: false,
			        url:"${ctx}/monitor/manage/refreshClusterStatus/" + clusterName,
			        data:$("#creatAdminForm").serialize(),
			        success: function(data) {
			        	statusValue=data;
			        }
			    });
	   if(statusValue == 1){
		   $(that).find("td:last-child").html('<spring:message code="monitor.status.abnormal"/>').prepend('<i class="icon-status-abnormal"></i>');
	   }else if(statusValue == 2){
		   $(that).find("td:last-child").html('<spring:message code="monitor.status.part.abnormal"/>').prepend('<i class="icon-status-partnormal"></i>');
	   }else if(statusValue == 0){
		   $(that).find("td:last-child").html('<spring:message code="monitor.status.normal"/>').prepend('<i class="icon-status-normal"></i>');
	   }
		$(that).addClass("active").siblings().removeClass("active");
    	$("#rightContent").attr("src","${ctx}/monitor/manage/nodedetail/"+clusterName);
    }
    

function viewCsDetail(that, csSystemName) {
	var statusValue = -1;
	$.ajax({
		type : "GET",
		async : false,
		url : "${ctx}/monitor/manage/csSystemStatus/" + csSystemName,
		data : $("#creatAdminForm").serialize(),
		success : function(data) {
			statusValue = data;
		}
	});
	if (statusValue == 1) {
		$(that).find("td:last-child").html(
				'<spring:message code="monitor.status.abnormal"/>')
				.prepend('<i class="icon-status-abnormal"></i>');
	} else if (statusValue == 2) {
		$(that).find("td:last-child").html(
				'<spring:message code="monitor.status.part.abnormal"/>')
				.prepend('<i class="icon-status-partnormal"></i>');
	} else if (statusValue == 0) {
		$(that).find("td:last-child").html(
				'<spring:message code="monitor.status.normal"/>').prepend(
				'<i class="icon-status-normal"></i>');
	}
	$(that).addClass("active").siblings().removeClass("active");
	$("#rightContent").attr("src",
			"${ctx}/monitor/manage/csnodedetail/" + csSystemName);
}


</script>