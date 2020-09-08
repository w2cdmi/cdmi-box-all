<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="../common/common.jsp" %>
    <%@ include file="commonForFiles.jsp" %>
    <%@ include file="commonForLinkFiles.jsp" %>
    <script type="text/javascript" src="${ctx}/static/preview/js/flexpaper.js"></script>
    <script type="text/javascript" src="${ctx}/static/preview/js/flexpaper_handlers.js"></script>
</head>
<body>
<div class="header">
    <div class="header-con">
        <div class="logo" id="logoBlock"><img/></div>
        <div class="preview-handle pull-right">
            <button type="button" onclick="window.close();" class="btn btn-link"
                    title="<spring:message code="button.close"/>"><i class="icon-close"></i></button>
        </div>
        <div class="preview-handle">
            <button type="button" onclick="downloadFile();" class="btn btn-link"
                    title="<spring:message code="button.download"/>"><i class="icon-download"></i></button>
            <c:if test="${showShare}">
                <button type="button" onclick="shareFile();" class="btn btn-link"
                        title="<spring:message code="button.share"/>"><i class="icon-share"></i></button>
            </c:if>
            <c:if test="${not linkHidden}">
                <button type="button" onclick="linkFile();" class="btn btn-link"
                        title="<spring:message code="button.shareLink"/>"><i class="icon-link"></i></button>
            </c:if>
        </div>
        <div class="preview-file-name"><span title="<c:out value='${fileName}'/>"><c:out value='${fileName}'/></span>
        </div>
    </div>
</div>
<div class="body" style="min-height: 456px;">
    <div class="body-con clearfix body-con-no-menu">
        <div class="pull-left clearfix">
            <div class="files-block clearfix">
                <div class="<c:out value='${style}'/>" style="display: block;"><c:out value='${errMsg}'/></div>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
    var ownerId =<c:out value='${ownerId}'/>;
    var fileId =<c:out value='${fileId}'/>;
    var linkCode = "<c:out value='${linkCode}'/>";

    $(function () {
        loadSysSetting("${ctx}", '<spring:message code="common.language1" />');
        $(".preview-handle button, .preview-file-name span").tooltip({
            container: "body",
            placement: "bottom",
            delay: {show: 100, hide: 0},
            animation: false
        });
    })

    function downloadFile() {
        if (linkCode != undefined && linkCode != null && linkCode != "") {
            downloadLinkFile();
        } else {
            downloadNormalFile();
        }
    }

    function downloadNormalFile() {
        var downloadUrl = "${ctx}/files/getDownloadUrl/" + ownerId + "/" + fileId + "?" + Math.random();
        $.ajax({
            type: "GET",
            async: false,
            url: downloadUrl,
            error: function (request) {
                downloadFileErrorHandler(request);
            },
            success: function (data) {
                jQuery('<form action="' + data + '" method="get"></form>').appendTo('body').submit().remove();
            }
        });
    }

    function downloadLinkFile() {
        var downloadUrl = "${ctx}/share/getDownloadUrl/" + fileId + "/" + linkCode + "?" + Math.random();
        $.ajax({
            type: "GET",
            async: false,
            url: downloadUrl,
            error: function (request) {
                doDownLoadLinkError(request);
            },
            success: function (data) {
                jQuery('<form action="' + data + '" method="get"></form>').appendTo('body').submit().remove();
            }
        });
    }

    function shareFile() {
        var url = '${ctx}/share/folder/' + ownerId + '/' + fileId;
        top.ymPrompt.win({
            message: url,
            width: 650,
            height: 375,
            title: "<spring:message code='file.title.share'/>",
            iframe: true,
            handler: shareHandle
        });
    }
    function shareHandle() {
        top.ymPrompt.close();
    }

    function linkFile() {
        var url = '${ctx}/share/link/' + ownerId + '/' + fileId;
        top.ymPrompt.win({
            message: url,
            width: 650,
            height: 350,
            title: "<spring:message code='file.title.shareLink'/>",
            iframe: true,
            handler: linkHandle
        });
    }
    function linkHandle() {
        top.ymPrompt.close();
    }
</script>
</body>
</html>
