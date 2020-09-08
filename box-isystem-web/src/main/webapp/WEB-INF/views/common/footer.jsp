<%@ page contentType="text/html;charset=UTF-8"%>
<HEAD>
<META HTTP-EQUIV="Expires" CONTENT="0">
<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
<META HTTP-EQUIV="Cache-control"
	CONTENT="no-cache, no-store, must-revalidate">
<META HTTP-EQUIV="Cache" CONTENT="no-cache">
</HEAD>
<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);
%>

<div class="footer">
	<div class="footer-con">
		<p id="copyRightId">
			<spring:message code="corpright" />
		</p>
	</div>
</div>
