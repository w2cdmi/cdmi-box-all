<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head>

<meta name="viewport"
	content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no" />
<%@ include file="../common/include.jsp"%>
<title>查看协作空间</title>
<style>
.weui-cell__ft {
	word-break: break-all;
	color: #666;
}
.weui-cell:before{
	border-top: none;
}
.weui-cell{
	border-bottom: 0.05rem solid #dadada;
	font-size: 0.75rem;
	width: 92%;
	padding: 12px 0;
	margin: auto
}
	.weui-cell>div>nobr,.weui-cell>div>p{
		color: #666;
	}
</style>
</head>
<body>
	<div class="bd">
		<div class="page-bd" id="editTeamSpaceForm">
			<div class="weui-cell">
				<div class="weui-cell__bd">
					<nobr>
						<spring:message code='common.field.name' />
						:
					</nobr>
				</div>
				<div class="weui-cell__ft">${teamSpaceInfo.name}</div>
			</div>
			<div class="weui-cell">
				<div class="weui-cell__bd">
					<p style="white-space: nowrap;">
						<spring:message code='teamSpace.label.description' />
						:
					</p>
				</div>
				<div class="weui-cell__ft">
					<p class="span4">${teamSpaceInfo.description}</p>
				</div>
			</div>
			<div class="weui-cell">
				<div class="weui-cell__bd">
					<p>
						<spring:message code='teamSpace.label.maxMember' />
						:
					</p>
				</div>
				<div class="weui-cell__ft">
					<c:if test='${teamSpaceInfo.maxMembers == -1}'>
						<span><spring:message code='teamSpace.tip.noLimit' /></span>
					</c:if>
					<c:if test='${teamSpaceInfo.maxMembers != -1}'>
						<span><c:out value='${teamSpaceInfo.maxMembers}' /></span>
					</c:if>
				</div>
			</div>
			<div class="weui-cell">
				<div class="weui-cell__bd">
					<p>
						<spring:message code='teamSpace.label.curMember' />
						:
					</p>
				</div>
				<div class="weui-cell__ft">
					<span><c:out value='${teamSpaceInfo.curNumbers}' /></span>
				</div>
			</div>
			<div class="weui-cell">
				<div class="weui-cell__bd">
					<p>
						<spring:message code='teamSpace.label.spaceQuota' />
						:
					</p>
				</div>
				<div class="weui-cell__ft">
					<c:if test='${teamSpaceInfo.spaceQuota == -1}'>
						<span><spring:message code='teamSpace.tip.noLimit' /></span>
					</c:if>
					<c:if test='${teamSpaceInfo.spaceQuota != -1}'>
						<span id="spaceQuotaInfo"></span>
					</c:if>
				</div>
			</div>
			<div class="weui-cell">
				<div class="weui-cell__bd">
					<p>
						<spring:message code='teamSpace.label.usedQuota' />
						:
					</p>
				</div>
				<div class="weui-cell__ft" for="usedQuotaInfo">
					<span id="usedQuotaInfo"></span>
				</div>
			</div>
		</div>
	</div>
	<%@ include file="../common/footer2.jsp"%>

	<script type="text/javascript">
    $(function () {
        getUserSpace();
        if ("<spring:message code='common.language1'/>" == "en") {
            $("#editTeamSpaceForm").addClass("label-w200");
        }
    });


    function getUserSpace() {
        var quota = ${teamSpaceInfo.spaceQuota};
        if (quota != -1) {
            var spaceQuota = formatFileSize(${teamSpaceInfo.spaceQuota});
            $("#spaceQuotaInfo").text(spaceQuota);
        } else {
            $("#spaceQuotaInfo").text("<spring:message code='teamSpace.tip.noLimit'/>")
        }

        var spaceUsed = formatFileSize(${teamSpaceInfo.spaceUsed});
        $("#usedQuotaInfo").text(spaceUsed);
    }
</script>
</body>
</html>
