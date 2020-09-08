<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title></title>
    <%@ include file="./common.jsp" %>
    <%@ include file="./messages.jsp" %>
</head>
<body>
<div class="pop-content modifiy-con">
    <div class="form-con">
        <form class="form-horizontal" id="modifyLogoForm" enctype="multipart/form-data" method="post">
            <div class="control-group">
                <label class="pull-left" for="input"><spring:message code='common.user.image.suggest'/></label>
            </div>
            <div class="control-group">
                <input type="file" name="photoFile" id="photoFile" style="width: 380px"/>
            </div>
        </form>
        <div class="control-group" style="display:none">
            <input id="imagesize" type="text" value="<c:out value='${imageSizeError}'/>"/>
            <input id="imagescale" type="text" value="<c:out value='${imageScaleError}'/>"/>
            <input id="imageinvalid" type="text" value="<c:out value='${imageInvalidError}'/>"/>
            <input id="imageuploadfail" type="text" value="<c:out value='${imageUploadFail}'/>"/>
            <input id="imageUploadok" type="text" value="<c:out value='${imageUploadOK}'/>"/>
        </div>
    </div>
</div>
<script type="text/javascript">

    function myrefresh() {
        top.window.location.reload();
    }

    $(document).ready(function () {
        if ($("#imagesize").val()) {
            handlePrompt("error", "<spring:message code='common.user.image.size'/>");
        } else if ($("#imagescale").val()) {
            handlePrompt("error", "<spring:message code='common.user.image.scale'/>");
        } else if ($("#imageinvalid").val()) {
            handlePrompt("error", "<spring:message code='common.user.image.invalid'/>");
        } else if ($("#imageuploadfail").val()) {
            handlePrompt("error", "<spring:message code='common.user.image.uploadfail'/>");
        } else if ($("#imageUploadok").val()) {
            top.ymPrompt.close();
            top.handlePrompt("success", "<spring:message code='common.oper.success'/>");
            setTimeout('myrefresh()', 1000);
        }

    });

    function submitModifyLogo() {

        var picTypes = ["jpg", "png"];
        var picFileName = $("#photoFile").val();
        if (picFileName == "") {
            handlePrompt("error", "<spring:message code='common.user.image.title.warm'/>");
            return;
        }
        if (picFileName != "") {
            var formatValid = false;
            var curType = picFileName.substring(picFileName.lastIndexOf(".") + 1);
            curType = curType.toLowerCase();
            for (idx in picTypes) {
                if (curType == picTypes[idx]) {
                    formatValid = true;
                    break;
                }
            }
            if (formatValid == false) {
                handlePrompt("error", "<spring:message code='common.user.image.invalid'/>");
                return;
            }
        }

        $("#modifyLogoForm").attr("action", "${ctx}/userimage/changeLogo");
        $("#modifyLogoForm").submit();

    }

</script>
</body>
</html>
