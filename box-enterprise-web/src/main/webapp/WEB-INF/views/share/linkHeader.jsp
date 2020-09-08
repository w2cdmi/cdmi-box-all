<%@ page contentType="text/html;charset=UTF-8" %>
<HEAD>
    <META HTTP-EQUIV="Expires" CONTENT="0">
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <META HTTP-EQUIV="Cache-control"
          CONTENT="no-cache, no-store, must-revalidate">
    <META HTTP-EQUIV="Cache" CONTENT="no-cache">
</HEAD>
<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);
%>
<%
    if ("true".equals(CustomUtils.getValue("cloudapp.commonTips"))) {
%>
<div class="top-info"><spring:message code='link.header.error.tips'/></div>
<%
    }
%>
<div class="header">
    <div class="header-con">
        <div class="logo" id="logoBlock"><img/></div>

        <div class="header-info">
            <button class="btn btn-primary" onClick="openHome()"><spring:message code="link.view.EnterPnc"/></button>
        </div>

    </div>
</div>
<script type="text/javascript" language="JavaScript">
    var globalLang = '<spring:message code="common.language1"/>';
    var hash = window.location.href;
    var reqUrl = "${pageContext.request.getRequestURL()}";
    var servletPath = "${pageContext.request.getServletPath()}";
    var indx = reqUrl.indexOf(servletPath);
    var isNotAnonAccess = hash.toString().substring(indx + 1, indx + 2) === "p" ? false : true;

    $(function () {
        var isSign = "${needDeclaration}";
        if (isSign == true && isNotAnonAccess == true) {
            showDeclaration();
        }
    });

    function showDeclaration() {
        top.ymPrompt.win({
            message: '${ctx}/syscommon/declaration',
            width: 600,
            height: 400,
            title: '<spring:message code="declaration.sign.title"/>',
            iframe: true,
            btn: [['<spring:message code="agree.declaration"/>', 'yes', false, "btnSignDeclaration"], ['<spring:message code="disagree.declaration"/>', 'no', true, "btnSignDeclarationCancel"]],
            handler: doSignDeclaration
        });
        top.ymPrompt_addModalFocus("#btnSignDeclaration");
    }

    function doSignDeclaration(tp) {
        if (tp == 'yes') {
            top.ymPrompt.getPage().contentWindow.signDeclaration();
        } else {
            top.ymPrompt.close();
            window.location.href = "${ctx}/logout";
        }
    }

    function openHome() {
        window.open(window.location.protocol + "//" + window.location.host + "${ctx}");
    }
    <%
    request.setAttribute("token",
                    CSRFTokenManager.getTokenForSession(session));
    %>
    function locationUrL(type, ownedBy, nodeId, linkCode) {
        var json = {
            "type": "myspace",
            "ownedBy": ownedBy,
            "iNodeId": nodeId,
            "linkCode": linkCode,
            "token": "<c:out value='${token}'/>"
        }
        $.ajax({
            type: "POST",
            url: "${ctx}/favorite/locationUrL",
            data: json,
            error: function (request) {
                top.ymPrompt.close();
                top.handlePrompt("error",
                    "<spring:message code='operation.failed'/>");
            },
            success: function (request) {
                window.location = "${ctx}" + request;
            }
        });

    }

</script>