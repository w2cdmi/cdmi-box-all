<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="../../common/common.jsp" %>
    <script src="${ctx}/static/js/public/validate/jquery.validate.min.js" type="text/javascript"></script>
    <%@ include file="../../common/messages.jsp" %>

</head>
<body>
<div class="pop-content add-group-con">
    <form class="form-horizontal" id="creatDoctypeForm">
        <input type="hidden" class="span4" maxlength="10" id="id" name="id" value="${doctypeConfig.id}"/>
        <div class="control-group">
            <label class="control-label" for=""><em>*</em><spring:message code='common.field.name'/>: </label>
            <div class="controls">
                <input type="text" class="span4" maxlength="10" id="name" name="name" value="${doctypeConfig.name}"/>
                <span class="validate-con bottom"><div></div></span>
            </div>
        </div>
        <form:errors path="*"/>
        <div class="control-group">
            <label class="control-label" for=""><em>*</em><spring:message code='doctype.field.label.description'/>:
            </label>
            <div class="controls">
                <textarea class="span4 H80" id="description" name="value" maxlength="256" rows="7"><c:out
                        value='${doctypeConfig.value}'/></textarea>
                <span class="validate-con bottom"><div></div></span>
            </div>
        </div>
        <input id="file.errorMsg.nameRequired" type="hidden"
               value="<spring:message code='file.errorMsg.nameRequired'/>"/>
        <input id="doctype.error.field.type" type="hidden" value="<spring:message code='doctype.error.field.type'/>"/>
        <input id="doctype.add.length.range" type="hidden" value="<spring:message code='doctype.add.length.range'/>"/>
        <input id="doctype.errorMsg.valueRequired" type="hidden"
               value="<spring:message code='doctype.errorMsg.valueRequired'/>"/>
        <input id="doctype.error.description.length" type="hidden"
               value="<spring:message code='doctype.error.description.length'/>"/>
        <input type="hidden" id="token" name="token" value="<c:out value='${token}'/>"/>
    </form>
</div>
<script type="text/javascript">
    $(document).ready(function () {
        $("#name").focus();

        /* $.validator.addMethod(
         "isValidFileName",
         function(value, element) {
         value = value.trim();
         var pattern =/^[^.·!#\/<>\\%?'"&,;]{2,255}$/;
         if(pattern.test(value)){
         return true;
         }else{
         return false;
         }
         }
         ); */

        $("#creatDoctypeForm").validate({
            rules: {
                name: {
                    required: true,
                    rangelength: [1, 10],
                    //isValidFileName:true
                },
                value: {
                    required: true,
                    maxlength: [256]
                }
            },
            messages: {
                name: {
                    required: $("#file.errorMsg.nameRequired").val(),
                    rangelength: $("#doctype.add.length.range").val()
                },
                value: {
                    required: $("#doctype.errorMsg.valueRequired").val(),
                    rangelength: $("#doctype.error.description.length").val()
                }
            },
            onkeyup: function (element) {
                $(element).valid()
            },
            onfocusout: false
        });

        $("#name").keydown(function (event) {
            top.ymPrompt_enableModalbtn("#btn-focus");
            if (event.keyCode == 13) {
                submitDoctype();
                if (window.event) {
                    window.event.cancelBubble = true;
                    window.event.returnValue = false;
                } else {
                    event.stopPropagation();
                    event.preventDefault();
                }
            }
            /* 	else{
             $("#btn-focus").attr('disabled','false');
             //alert("change");
             } */
        })


        $("#value").keydown(function (event) {
            if (event.keyCode == 13) {
                submitDoctype();
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

    function submitDoctype() {
        $("#name").val($("#name").val().trim());
        if (!$("#creatDoctypeForm").valid()) {
            return false;
        }
        var desStr = $("#description").val();
        if (desStr.length > 256) {
            top.ymPrompt_disableModalbtn("#btn-focus");
            handlePrompt("error", $("#doctype.error.description.length").val());
            return;
        }
        var types = desStr.split(",");
        var rules = /^[A-Za-z0-9]+$/;
        for (var i = 0; i < types.length; i++) {
            if (!rules.test(types[i])) {
                handlePrompt("error", "<spring:message code='doctype.error.field.type'/>");
                return
            }
        }
        $("#name").blur();
        top.ymPrompt_disableModalbtn("#btn-focus");
        var postUrl = "${ctx}/user/doctype/createDoctype";
        if ($("#id").val().trim() != "" && $("#id").val().trim() != 0) {
            postUrl = "${ctx}/user/doctype/modifyDoctype";
        } else {
            $("#id").val(0);
        }
        $.ajax({
            type: "POST",
            url: postUrl,
            data: $("#creatDoctypeForm").serialize(),
            error: function (request) {
                var _exception = request.responseText;
                if (_exception == "user doctype>5") {
                    handlePrompt("error", "<spring:message code='doctype.error.create.more'/>");
                } else if (_exception == "nameRepeat") {
                    handlePrompt("error", "<spring:message code='doctype.errorMsg.nameRepeat'/>");
                } else {
                    handlePrompt("error", "<spring:message code='group.error.operation.fail'/>");
                }
            },
            success: function () {
                refreshWindow();
                top.ymPrompt.close();
                top.handlePrompt("success", "<spring:message code='operation.success'/>");
            }
        });
    }

    function refreshWindow() {
        top.window.frames[0].location = "${ctx}/user/doctype";
    }
</script>
</body>
</html>
