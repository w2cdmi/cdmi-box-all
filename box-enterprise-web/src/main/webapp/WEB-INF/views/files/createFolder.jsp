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
    <form class="form-horizontal" id="creatFolderForm">
        <input type="hidden" id="ownerId" name="ownerId" value="<c:out value='${ownerId}'/>"/>
        <input type="hidden" id="parentId" name="parentId" value="<c:out value='${parentId}'/>"/>
        <br/>
        <div class="control-group">
            <label class="control-label" for=""><em>*</em><spring:message code='common.field.name'/>: </label>
            <div class="controls">
                <input type="text" id="name" name="name" maxlength="246"/>
                <span class="validate-con bottom"><div></div></span>
            </div>
        </div>
        <input type="hidden" id="token" name="token" value="<c:out value='${token}'/>"/>
    </form>
</div>
<script type="text/javascript">
    $(document).ready(function () {

        $("#name").focus();

        $.validator.addMethod(
            "isValidFileName",
            function (value, element) {
                value = value.trim();
                var pattern1 = /\.+$/;
                var pattern2 = /^.*(\\|\/).*$/;

                if (pattern1.test(value)) {
                    return false;
                } else if (pattern2.test(value)) {
                    return false;
                }
                return true;
            }
        );

        $.validator.addMethod(
            "required",
            function (value, element) {
                if (value == null) {
                    return false;
                }
                value = value.trim();
                return value != "";
            }
        );

        $.validator.addMethod(
            "isNameNotExist",
            function (value, element, param) {
                value = value.trim();
                var ret = false;
                $.ajax({
                    type: "GET",
                    async: false,
                    url: "${ctx}/nodes/checkSameName?" + new Date().toString(),
                    data: $("#creatFolderForm").serialize(),
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

        $("#creatFolderForm").validate({
            rules: {
                name: {
                    required: true,
                    rangelength: [1, 246],
                    isValidFileName: true,
                    isNameNotExist: true
                }
            },
            messages: {
                name: {
                    required: "<spring:message code='file.errorMsg.nameRequired'/>",
                    isValidFileName: "<spring:message code='file.errorMsg.invalidName'/>",
                    isNameNotExist: "<spring:message code='file.errorMsg.nameExist'/>"
                }
            },
            onkeyup: function (element) {
                $(element).valid()
            },
            onfocusout: false
        });

        $("#name").keydown(function (event) {
            if (event.keyCode == 13) {
                submitFolder();
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


    function submitFolder() {
        $("#name").val($("#name").val().trim());
        if (!$("#creatFolderForm").valid()) {
            return false;
        }

        $("#name").blur();

        top.ymPrompt_disableModalbtn("#btn-focus");

        $.ajax({
            type: "POST",
            url: "${ctx}/folders/create",
            data: $("#creatFolderForm").serialize(),
            error: function (request) {
                var status = request.status;
                if (status == 403) {
                    handlePrompt("error", "<spring:message code='error.forbid'/>");
                } else {
                    handlePrompt("error", "<spring:message code='operation.failed'/>");
                }
            },
            success: function () {
                top.ymPrompt.close();
                top.listFile(1, top.catalogParentId);
            }
        });
    }
</script>
</body>
</html>
