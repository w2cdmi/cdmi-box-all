<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cse" uri="http://cse.huawei.com/custom-function-taglib"%>  
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
<%@ include file="../common/common.jsp"%>
<script src="${ctx}/static/js/public/validate/jquery.validate.min.js" type="text/javascript"></script>
<style type="text/css">
.dividerFeedBack {
    margin: 5px 0px;
    height: 1px;
    overflow: hidden;
    background: #e0e0e0;
    width: 95%;
}
.pop-content .detailBody{
	float:left;
	width:100%;
}

.divFloatLeft{
	float:left;
}

.divFloatRight{
	float:right;
}

.customerFeedBack{
 	padding-right: 10px;
}
</style>
<%@ include file="../common/messages.jsp"%>
</head>
<body>
<div class="pop-content">

   	<div class="detailBody">
   			<div style="word-wrap:break-word;word-break:break-all">
   				<b><spring:message code="feedback.detail.title"/>：</b>${cse:htmlEscape(restFeedBackInfo.problemTitle)}
   			</div>
   			<div>
   				<b><spring:message code="feedback.detail.type"/>：</b>${cse:htmlEscape(restFeedBackInfo.problemType eq '1' ? '故障反馈':'意见建议')}
   			</div>
   			<div style="word-wrap:break-word;word-break:break-all">
   				<b><spring:message code="feedback.detail.description"/>：</b>${cse:htmlEscape(restFeedBackInfo.problemDescription)}
   			</div>
   			<div class="divFloatRight">
   				<span class="customerFeedBack">${cse:htmlEscape(restFeedBackInfo.customerName)}</span>
   				<span><fmt:formatDate value="${restFeedBackInfo.customerTwTime}" pattern="yyyy-MM-dd HH:mm:ss"/></span>
   			</div>
   			<li class="dividerFeedBack"></li>
   			
   			
   			<c:forEach items="${restFeedBackSubList}" var="feedbackSub">
   				
   				<c:if test="${cse:htmlEscape(feedbackSub.isAnswer eq '1' ? true:false)}">
   						<div style="padding-top:40px;word-wrap:break-word;word-break:break-all">
   							<b><spring:message code="feedback.detail.answer"/>：</b>${cse:htmlEscape(feedbackSub.description)}
   						</div>
   						<div class="divFloatRight">
   							<span class="customerFeedBack">${cse:htmlEscape(feedbackSub.userName)}</span>
   							<span><fmt:formatDate value="${feedbackSub.updateTime}" pattern="yyyy-MM-dd HH:mm:ss"/></span>
   						</div>
   			
   						<li class="dividerFeedBack"></li>
   				
   				</c:if>
   				
   				 <c:if test="${cse:htmlEscape(feedbackSub.isAnswer eq '0' ? true:false)}">
   						<div style="padding-top:40px;word-wrap:break-word;word-break:break-all">
   							<b><spring:message code="feedback.detail.ask"/>：</b>${cse:htmlEscape(feedbackSub.description)}
   						</div>
   						<div class="divFloatRight">
   							<span class="customerFeedBack">${cse:htmlEscape(feedbackSub.userName)}</span>
   							<span><fmt:formatDate value="${feedbackSub.updateTime}" pattern="yyyy-MM-dd HH:mm:ss"/></span>
   						</div>
   			
   						<li class="dividerFeedBack"></li>
   				</c:if>
   			</c:forEach>
        </div>
 </div>
</body>
</html>
