<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
    <%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
        <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
            <!DOCTYPE html>
            <html lang="zh-CN">

            <head>
                <title>
                    <spring:message code='main.title' />
                </title>
                <%@ include file="common/include.jsp" %>
                    <script src="${ctx}/static/js/index.js?v=${version}"></script>
            </head>

            <body>
                <div class="container">
                    <jsp:include page="common/menubar.jsp">
                        <jsp:param name="activeId" value="index" />
                    </jsp:include>
                    <div>
                    </div>
                </div>
                <a id="downloadFile" download style="display:none"></a>
            </body>

            </html>