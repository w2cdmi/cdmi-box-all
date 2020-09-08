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
    <form class="form-horizontal label-w100" id="creatTeamSpaceForm">
        <div class="control-group">
            <label class="control-label" for=""><em>*</em><spring:message code='common.field.name'/>: </label>
            <div class="controls">
                <input type="text" class="span4" id="name" name="name" maxlength="255"/>
                <span class="validate-con bottom"><div></div></span>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for=""><spring:message code='teamSpace.label.description'/>: </label>
            <div class="controls">
                <textarea class="span4" rows="4" cols="30" name="description" id="description" maxlength="255"
                          onkeyup='doMaxLength(this)'></textarea>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label"></label>
            <div class="controls">
                <label class="checkbox">
                    <input type="checkbox" id="uploadNoticeEnable"/><spring:message
                        code="teamSpace.label.uploadNotice"/>
                </label>
            </div>
        </div>
        <input type="hidden" id="uploadNotice" name="uploadNotice" value="disable"/>
        <input type="hidden" id="token" name="token" value="<c:out value='${token}'/>"/>
    </form>
</div>
<script type="text/javascript">
    $(document).ready(function () {
        $("#name").focus();

        $.validator.addMethod(
            "isNameNotExist",
            function (value, element, param) {
                value = value.trim();
                var ret = false;
                $.ajax({
                    type: "POST",
                    async: false,
                    url: "${ctx}/teamspace/checkSameName?" + new Date().toString(),
                    data: $("#creatTeamSpaceForm").serialize(),
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

        $("#creatTeamSpaceForm").validate({
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
            //onkeyup:function(element) {$(element).valid()},
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

        $("#uploadNoticeEnable").click(function () {
            if (this.checked) {
                $("#uploadNotice").val("enable");
            } else {
                $("#uploadNotice").val("disable");
            }
        })
    });


    function submitTeamSpace() {
        $("#name").val($("#name").val().trim());
        if (!$("#creatTeamSpaceForm").valid()) {
            return false;
        }
        $("#name").blur();
        top.ymPrompt_disableModalbtn("#btn-focus");
        $.ajax({
            type: "POST",
            url: "${ctx}/teamspace/createTeamSpace",
            data: $("#creatTeamSpaceForm").serialize(),
            error: function (request) {
                var status = request.status;
                if (status == 507) {
                    handlePrompt("error", "<spring:message code='teamSpace.error.exceedMaxSpace'/>");
                } else if (status == 403) {
                    handlePrompt("error", "<spring:message code='teamSpace.error.forbiddenAdd'/>");
                } else {
                    handlePrompt("error", "<spring:message code='operation.failed'/>");
                }
            },
            success: function () {
                top.ymPrompt.close();
                top.handlePrompt("success", "<spring:message code='operation.success'/>");
                top.listTeam(1);
            }
        });
    }
</script>
</body>
</html>
