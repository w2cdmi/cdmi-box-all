<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%@ page import="pw.cdmi.box.disk.utils.*" %>
<% request.setAttribute("token", CSRFTokenManager.getTokenForSession(session)); %>
<div class="found-space">
	<div class="found-space-header"></div>
	<form id="creatTeamSpaceForm" class="form">
		<input type="hidden" id="token" name="token" value="<c:out value='${token}'/>"/>
        <input type="hidden" id="uploadNotice" name="uploadNotice" value="disable"/>
			<dl>
				<dt>
					<label>名称：</label> <input class="found-space-input" style="width: 340px" id="name" name="name" placeholder="请输入协作空间名称">
				</dt>
<%--
				<dt>
					<label><spring:message code="teamSpace.label.uploadNotice"/></label>
					<input style="height:14px;margin-top:17px;cursor: pointer;outline: none;" id="uploadNoticeEnable" name="uploadNoticeEnable" class="weui-switch" type="checkbox">
				</dt>
--%>
				<dt>
					<label>协作空间说明：(最大限制为255个字)</label>
                    <textarea placeholder="请输入协作空间说明信息" style="resize:vertical;width: 100%;height: 100px" id="description" name="description"></textarea>
				</dt>
				
			</dl>
        <div class="form-control" style="text-align: right">
            <button type="button" id="cancel_button">取消</button>
            <button type="button" id="ok_button">确定</button>
        </div>
	</form>
    
</div>
	<script>
	$("#uploadNoticeEnable").click(function () {
		if (this.checked) {
		$("#uploadNotice").val("enable");
		} else {
		$("#uploadNotice").val("disable");
		}
	});
	</script>
