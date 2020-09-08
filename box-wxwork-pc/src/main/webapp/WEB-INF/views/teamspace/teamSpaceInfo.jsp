<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
	<div class="space_info_content">
		<p><spring:message code='common.field.name' />:<span>${teamSpaceInfo.name}</span></p>
		<p><spring:message code='teamSpace.label.description' />:<span>${teamSpaceInfo.description}</span></p>
		<p><spring:message code='teamSpace.label.maxMember' />:
			<c:if test='${teamSpaceInfo.maxMembers == -1}'>
				<span><spring:message code='teamSpace.tip.noLimit' /></span>
			</c:if>
			<c:if test='${teamSpaceInfo.maxMembers != -1}'>
				<span><c:out value='${teamSpaceInfo.maxMembers}' /></span>
			</c:if>
		</p>
		<p><spring:message code='teamSpace.label.curMember' />:<span><c:out value='${teamSpaceInfo.curNumbers}' /></span></p>
		<p><spring:message code='teamSpace.label.spaceQuota' />:
			<c:if test='${teamSpaceInfo.spaceQuota == -1}'>
				<span><spring:message code='teamSpace.tip.noLimit' /></span>
			</c:if>
			<c:if test='${teamSpaceInfo.spaceQuota != -1}'>
				<span id="spaceQuotaInfo"></span>
			</c:if>
		</p>
		<p><spring:message code='teamSpace.label.usedQuota' />:<span id="usedQuotaInfo"></span></p>
	</div>
