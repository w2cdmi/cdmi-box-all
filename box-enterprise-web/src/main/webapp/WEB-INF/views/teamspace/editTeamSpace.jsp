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
            <label class="control-label" for=""><em>*</em><spring:message code='common.field.name'/>: </label>
            <div class="controls">
                <input class="span4" type="text" id="name" name="name" maxlength="255"
                       value="<c:out value='${teamSpaceInfo.name}'/>"/>
                <span class="validate-con bottom"><div></div></span>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for=""><spring:message code='teamSpace.label.description'/>: </label>
            <div class="controls">
                <textarea class="span4" rows="4" cols="30" name="description" id="description" maxlength="255"
                          onkeyup='doMaxLength(this)'><c:out value='${teamSpaceInfo.description}'/></textarea>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label"></label>
            <div class="controls">
                <label class="checkbox">
                    <input type="checkbox" id="uploadNoticeEnable"
                           <c:if test="${teamSpaceInfo.uploadNotice=='enable'}">checked="checked"</c:if> /><spring:message
                        code="teamSpace.label.uploadNotice"/>
                </label>
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
        <input type="hidden" id="uploadNotice" name="uploadNotice"
               value="<c:out value='${teamSpaceInfo.uploadNotice}'/>"/>
        <input type="hidden" id="token" name="token" value="<c:out value='${token}'/>"/>
    </form>
</div>
<script type="text/javascript">
    $(function () {
        getUserSpace();

        $("#uploadNoticeEnable").click(function () {
            if (this.checked) {
                $("#uploadNotice").val("enable");
            } else {
                $("#uploadNotice").val("disable");
            }
        })

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
    $(document).ready(function () {
        $("#name").focus();
        // 需要验证团队空间同名情况
        $.validator.addMethod(
            "isNameNotExist",
            function (value, element, param) {
                value = value.trim();
                var ret = false;
                $.ajax({
                    type: "POST",
                    async: false,
                    url: "${ctx}/teamspace/checkSameName?" + new Date().toString(),
                    data: $("#editTeamSpaceForm").serialize(),
                    success: function (data) {
                        if (typeof(data) == 'string' && data.indexOf('<html>') != -1) {
                            window.location.href = "${ctx}/logout";
                            return;
                        }
                        ret = data;
                    }
                });
                return !ret;
            }
        );

        $("#editTeamSpaceForm").validate({
            rules: {
                name: {
                    required: true,
                    isNameNotExist: true,
                    rangelength: [1, 64]
                }
            },
            messages: {
                name: {
                    required: "<spring:message code='file.errorMsg.nameRequired'/>",
                    isNameNotExist: "<spring:message code='teamSpace.name.exist'/>"
                }
            },
            // 不采用输入时验证，会多次请求后台，改用提交时验证
            // onkeyup:function(element) {$(element).valid()},
            onsubmit: true,
            onfocusout: false
        });

        $("#name").keydown(function (event) {
            if (event.keyCode == 13) {
                if (window.event) {
                    window.event.cancelBubble = true;
                    window.event.returnValue = false;
                } else {
                    event.stopPropagation();
                    event.preventDefault();
                }
            }
        })
    });

    function submitTeamSpace() {
        $("#name").val($("#name").val().trim());
        if (!$("#editTeamSpaceForm").valid()) {
            return false;
        }
        $("#name").blur();
        top.ymPrompt_disableModalbtn("#btn-focus");
        $.ajax({
            type: "POST",
            url: "${ctx}/teamspace/editTeamSpace",
            data: $("#editTeamSpaceForm").serialize(),
            error: function (request) {
                handlePrompt("error", "<spring:message code='operation.failed'/>");
            },
            success: function () {
                top.ymPrompt.close();
                if (typeof(data) == 'string' && data.indexOf('<html>') != -1) {
                    window.location.href = "${ctx}/logout";
                    return;
                }
                top.handlePrompt("success", "<spring:message code='operation.success'/>");
                top.listTeam(1);
            }
        });
    }
</script>
</body>
</html>
