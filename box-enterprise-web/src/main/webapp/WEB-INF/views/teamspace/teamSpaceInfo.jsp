<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="../common/common.jsp" %>
    <script src="${ctx}/static/js/public/validate/jquery.validate.min.js" type="text/javascript"></script>
    <%@ include file="../common/messages.jsp" %>
</head>
<body>
<div class="pop-content">
    <form class="form-horizontal label-w140" id="editTeamSpaceForm">
        <input type="hidden" id="teamId" name="teamId" value="<c:out value='${teamSpaceInfo.id}'/>"/>
        <div class="control-group">
            <label class="control-label" for=""><spring:message code='common.field.name'/>: </label>
            <div class="controls">
                <input class="span4" disabled="disabled" type="text" id="name" name="name" maxlength="255"
                       value="<c:out value='${teamSpaceInfo.name}'/>"/>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for=""><spring:message code='teamSpace.label.description'/>: </label>
            <div class="controls">
                <textarea class="span4" disabled="disabled" rows="4" cols="30" name="description" id="description"
                          maxlength="255"><c:out value='${teamSpaceInfo.description}'/></textarea>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="input"><spring:message code='teamSpace.label.maxMember'/>: </label>
            <div class="controls">
                <c:if test='${teamSpaceInfo.maxMembers == -1}'>
                    <span class="uneditable-input"><spring:message code='teamSpace.tip.noLimit'/></span>
                </c:if>
                <c:if test='${teamSpaceInfo.maxMembers != -1}'>
                    <span class="uneditable-input" title="<c:out value='${teamSpaceInfo.maxMembers}'/>"><c:out
                            value='${teamSpaceInfo.maxMembers}'/></span>
                </c:if>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="input"><spring:message code='teamSpace.label.curMember'/>: </label>
            <div class="controls">
                <span class="uneditable-input" title="<c:out value='${teamSpaceInfo.curNumbers}'/>"><c:out
                        value='${teamSpaceInfo.curNumbers}'/></span>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="input"><spring:message code='teamSpace.label.spaceQuota'/>: </label>
            <div class="controls">
                <c:if test='${teamSpaceInfo.spaceQuota == -1}'>
                    <span class="uneditable-input"><spring:message code='teamSpace.tip.noLimit'/></span>
                </c:if>
                <c:if test='${teamSpaceInfo.spaceQuota != -1}'>
                    <span class="uneditable-input" id="spaceQuotaInfo"></span>
                </c:if>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="input"><spring:message code='teamSpace.label.usedQuota'/>: </label>
            <div class="controls">
                <span class="uneditable-input" id="usedQuotaInfo"></span>
            </div>
        </div>
    </form>
</div>
<script type="text/javascript">
    $(function () {
        getUserSpace();
        if ("<spring:message code='common.language1'/>" == "en") {
            $("#editTeamSpaceForm").addClass("label-w200");
        }
    });
    function getUserSpace() {
        if (${teamSpaceInfo.spaceQuota} !=
        -1
    )
        {
            var spaceQuota = formatFileSize(${teamSpaceInfo.spaceQuota});
            $("#spaceQuotaInfo").text(spaceQuota);
        }
    else
        {
            $("#spaceQuotaInfo").text("<spring:message code='teamSpace.tip.noLimit'/>")
        }

        var spaceUsed = formatFileSize(${teamSpaceInfo.spaceUsed});
        $("#usedQuotaInfo").text(spaceUsed);
    }
</script>
</body>
</html>
