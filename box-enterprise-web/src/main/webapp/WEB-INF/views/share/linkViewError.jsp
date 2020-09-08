<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="../common/common.jsp" %>
</head>
<body>

<%@ include file="linkHeader.jsp" %>

<div class="body">
    <div class="body-con clearfix body-con-no-menu">
        <div class="page-error">
            <c:if test="${exceptionName=='BadRequest'}"><h3><spring:message code="link.view.BadRquest"/></h3></c:if>
            <c:if test="${exceptionName=='NoSuchItemsException' }"><h3><spring:message
                    code="link.view.NoSuchItems"/></h3></c:if>
            <c:if test="${exceptionName=='Forbidden' }"><h3><spring:message code="link.view.Forbidden"/></h3></c:if>
            <c:if test="${exceptionName=='LinkNotEffectiveException'}"><h3><spring:message
                    code="link.view.NotEffective"/></h3></c:if>
            <c:if test="${exceptionName=='LinkExpiredException'}"><h3><spring:message code="link.view.Expired"/></h3>
            </c:if>
            <c:if test="${exceptionName=='AuthFailedException' }"><h3><spring:message code="link.view.Forbidden"/></h3>
            </c:if>
            <c:if test="${exceptionName=='RuntimeException' }"><h3><spring:message code="link.set.innerError"/></h3>
            </c:if>
            <c:if test="${exceptionName=='NoSuchLinkException' }"><h3><spring:message code="link.set.NoSuchLink"/></h3>
            </c:if>

            <button class="btn btn-large" onclick="closeSelf()"><spring:message code="button.close"/></button>
        </div>
    </div>
</div>

<%@ include file="../common/footer.jsp" %>

<script type="text/javascript">

    $(function () {
        loadSysSetting("${ctx}", '<spring:message code="common.language1" />');
        $("#linkHeadInfo").text("");
    });

    function closeSelf() {
        window.opener = null;
        window.open('', '_self');
        window.close();
    }
</script>
</body>
</html>