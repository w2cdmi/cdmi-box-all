<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page import="pw.cdmi.box.uam.util.CSRFTokenManager"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%
request.setAttribute("token", CSRFTokenManager.getTokenForSession(session));
%>
<!DOCTYPE html>
<html>
<head>
<%@ include file="../common/common.jsp"%>
</head>


<body>
<div class="pop-content">
	<div class="form-con">
		<form class="form-horizontal label-w100" id="createAnnouncementForm" name="createAnnouncementForm">
			<div class="control-group">
	        	<label class="control-label" for=""><em>*</em><spring:message code="announcement.table.title"/>:</label>
	            <div class="controls">
	                <input type="text" id="title" name="title" class="span6" maxlength="255"/>
	                <span class="validate-con bottom"><div></div></span>
	            </div>
	        </div>
	        
	        <div class="control-group">
	        	<label class="control-label" for=""><em>*</em><spring:message code="announcement.table.content"/>:</label>
	            <div class="controls">
	                <textarea id="content" name="content" rows="14" cols="50" class="span6"></textarea>
	                <span class="validate-con bottom"><div></div></span>
	            </div>
	        </div>
	        <input type="hidden" name="token" value="${token}"/>
		</form>
	</div>
</div>


</body>
</html>
<script type="text/javascript">
$(document).ready(function() {
	$("#createAnnouncementForm").validate({ 
		rules: { 
			title:{
				required:true, 
				rangelength:[1,255]
			},
			content: { 
			    required:true, 
			    rangelength:[1,2047]
			}
		}
    }); 
});

function getLength(){
	$.ajax({
		type: "POST",
		url:"${ctx}/announcement/getLength",
		async : false,
		data:$('#createAnnouncementForm').serialize(),
		error: function(request) {
		},
		success: function(data) {
			contentInvalid = data;
		}
	});
}

function submitCreateAnnouncement() {
	if(!$("#createAnnouncementForm").valid()) {
        return false;
    } 
	getLength();
	if(contentInvalid){
		handlePrompt("error",'<spring:message code="announcement.content.too.long"/>');
		return false;
	}
	$.ajax({
        type: "POST",
        url:"${ctx}/announcement/createAnnouncement",
        data:$('#createAnnouncementForm').serialize(),
        error: function(request) {
        	top.handlePrompt("error",'<spring:message code="announcement.publish.failed"/>');
        },
        success: function() {
        	top.ymPrompt.close();
        	top.handlePrompt("success",'<spring:message code="announcement.publish.success"/>');
        	window.parent.document.getElementById('systemFrame').contentWindow.listAnnouncement();
        }
    });
}

</script>