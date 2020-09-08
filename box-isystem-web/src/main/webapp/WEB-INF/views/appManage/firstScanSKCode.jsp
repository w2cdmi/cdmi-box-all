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
<meta http-equiv="Cache-Control" content="no-cache" />
<meta http-equiv="Pragma" content="no-cache" />
<title></title>
<%@ include file="../common/common.jsp"%>
</head>
<body>
 
 <div class="pop-content">
	<div class="sys-content"> 
		<div>
		 <table class="table table-striped table-condensed" style="border:1px solid #ddd;">
          <tbody>
          	<tr>
                <td style="width:70px; text-align: right;"><spring:message code="app.connect.ID"/>:</td>
                <td>${cse:htmlEscape(accessKeyList.id)}</td>
            </tr>
            <tr>
                <td style="text-align: right;"><spring:message code="app.connect.key"/>:</td>
                <td>${cse:htmlEscape(accessKeyList.secretKey)}</td>
            </tr>
            <tr>
               <td style="text-align: right;"><spring:message code="app.create.time"/>:</td>
                <td><fmt:formatDate value="${accessKeyList.createdAt}" pattern="yyyy-MM-dd HH:mm"/></td>
            </tr>
          </tbody>
        </table>
        </div>
       <div>
        	<label style="color:red;margin-top:50px"><spring:message code="app.createAppSK.info"/></label>
        </div>
	 </div>
	 
</div>
</body>
</html>
