<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="../common/common.jsp" %>
</head>
<body>
<div class="header">
    <div class="header-con">
        <div class="logo" id="logoBlock"><img/></div>
        <div class="preview-handle pull-right">
            <button type="button" onclick="window.close();" class="btn btn-link"
                    title="<spring:message code="button.close"/>"><i class="icon-close"></i></button>
        </div>
    </div>
</div>
<div class="body">
    <div class="body-con clearfix body-con-no-menu">
        <div class="page-error">
            <h3><spring:message code="shareIndex.error.NoSuchFile"/></h3>
        </div>
    </div>
</div>
<script type="text/javascript">

    $(function () {
        loadSysSetting("${ctx}", '<spring:message code="common.language1" />');
        $(".preview-handle button, .preview-file-name span").tooltip({
            container: "body",
            placement: "bottom",
            delay: {show: 100, hide: 0},
            animation: false
        });
    })

</script>
</body>
</html>
