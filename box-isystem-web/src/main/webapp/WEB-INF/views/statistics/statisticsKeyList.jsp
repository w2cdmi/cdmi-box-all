<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="cse" uri="http://cse.huawei.com/custom-function-taglib"%>  
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
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
<meta http-equiv="X-UA-Compatible" content="IE=10" />
<meta http-equiv="X-UA-Compatible" content="IE=9" />
<meta http-equiv="X-UA-Compatible" content="IE=8" />
<title></title>
<%@ include file="../common/common.jsp"%>
</head>
<body>
<div class="sys-content">
	<div class="alert"><i class="icon-lightbulb icon-orange"></i><spring:message code="statistics.manager"/></div>
	    <div class="clearfix">
	    	<div class="pull-left">
	        	<button type="button" class="btn btn-primary btn-small" onClick="createAccessKey()"><i class="icon-add"></i><spring:message code="common.create"/></button>
	        </div>
  	  </div>
  	  <div class="table-con">
		 <table class="table table-bordered table-striped table-condensed" style=" border-bottom:1px solid #ddd;">
          <thead>
            <tr>
                <th width="54%"><spring:message code="app.connect.ID"/></th>
                <th width="21%"><spring:message code="app.connect.key"/></th>
                <th width="15%"><spring:message code="app.create.time"/></th>
                <th width="10%"><spring:message code="common.operation"/></th>
            </tr>
          </thead>
          <tbody>
          <c:forEach items="${statisticsList}" var="statisticsKey">
            <tr>
                <td>${cse:htmlEscape(statisticsKey.id)}</td>
                <td>${cse:htmlEscape(statisticsKey.secretKey)}</td>
                <td>
                <fmt:formatDate value="${statisticsKey.createdAt}" pattern="yyyy-MM-dd HH:mm"/>
                </td>
                <td>
                <button class="btn" type="button" onClick="deleteAccessKey('${cse:htmlEscape(statisticsKey.id)}')"><spring:message code="common.delete"/></button>
                </td>
            </tr>
          </c:forEach>
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
 	function createAccessKey()
 	{
 		$.ajax({
 	        type: "POST",
 	        url:"${ctx}/statisticsmanage/statistics/create",
 	        data:{"token" : "${token}"},
 	        error: function(request) {
 	        	switch(request.responseText)
 	        	{
 	        	case "ExceedMax":
 	        		handlePrompt("error","<spring:message code='statistics.exceemax' /> ");
 	        		break;
 	        	default:
 	        		handlePrompt("error","<spring:message code='statistics.oper.fail'/>");
 	        		break;
 	        	}
 	        },
 	        success: function(data) {
 	        	displaySecret(data.id);
 	        	window.location.reload();
 	        }
 	    });
 	}
 	
 	function deleteAccessKey(key)
 	{
 		$.ajax({
 	        type: "POST",
 	        url:"${ctx}/statisticsmanage/statistics/delete",
 	        data:{accessKey:key,"token" : "${cse:htmlEscape(token)}"},
 	        error: function(request) {
 	        	handlePrompt("error","<spring:message code='statistics.oper.fail'/>");
 	        },
 	        success: function() {
 	        	window.location.reload();
 	        }
 	    });
 	}
 	
 	function displaySecret(id){
 		top.ymPrompt.win({message:'${ctx}/statisticsmanage/statistics/get?id='+id,width:800,height:360,title:'<spring:message code="app.connetCode.info"/>', iframe:true,btn:[['<spring:message code="common.close"/>','no',true]]});			        	
 	}
 </script>