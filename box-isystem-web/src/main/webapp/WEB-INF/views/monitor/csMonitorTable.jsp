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
    <h5>${clusterName}</h5>
    <table class="table table-bordered">
        <tbody>
            <tr>
                <td width="30%"><b><spring:message code="monitor.node"/></b></td>
                <td class="table-createdc-first table-createdc">
                    <table class="table">
                        <tbody>
                        	<c:forEach items="${csNodes}" var="oper">
                    		<tr>
	                    		<td>${cse:htmlEscape(oper.hostName)}</td>
	                    		<c:if test='${oper.status == 0}'>
	                    			<td width="20%"><i class="icon-status-normal"></i><spring:message code="monitor.status.normal"/></td>
	                    		</c:if>
	                    		<c:if test='${oper.status != 0}'>
	                    			<td width="20%"><i class="icon-status-abnormal"></i><spring:message code="monitor.status.abnormal"/></td>
	                    		</c:if>
                     		</tr>
        					</c:forEach>
                        </tbody>
                    </table>
                </td>
            </tr>

        </tbody>
    </table>
</div>
</body>
<script type="text/javascript">
function viewDetail(hostName){
	top.ymPrompt.win({message:'${ctx}/monitor/manage/viewcontent/' + hostName,width:1100,height:780,title:'${clusterName}<spring:message code="monitor.get.node.info"/>', iframe:true,btn:null});
	top.ymPrompt.max();
}
function viewMysqlDetail(clusterName,clusterServiceName){
	top.ymPrompt.win({message:'${ctx}/monitor/manage/viewMysqlContent?clusterName='+clusterName+"&clusterServiceName="+ clusterServiceName,width:800,height:480,title:clusterServiceName+'<spring:message code="monitor.get.service.info"/>', iframe:true,btn:[['<spring:message code="monitor.close.windows"/>','no',true,"btnCancel"]],handler:doCloseDetail});
	top.ymPrompt_addModalFocus("#btnCreate");
}
function doCloseDetail(){
	//回调函数
}
</script>
</html>