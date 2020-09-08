<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="cse" uri="http://cse.huawei.com/custom-function-taglib" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="../common/common.jsp" %>
    <link href="${ctx}/static/skins/default/css/layout.css" rel="stylesheet" type="text/css"/>
</head>
<body>
<div id="pageLoadingContainer"
     style="z-index:10000; position:fixed; left:0; top:0; right:0; bottom:0; background:#fff;"></div>
<script type="text/javascript">new Spinner(optsBigSpinner).spin(document.getElementById("pageLoadingContainer"));</script>

<%@ include file="../common/header.jsp" %>

<div class="body">
    <div class="body-con clearfix app-con">
        <div class="app-tabs">
            <ul class="nav nav-tabs" id="myTab">
                <li class="windows active"><a href="#windows">Windows</a></li>
                <li class="android"><a href="#android">Android</a></li>
                <li class="ios"><a href="#ios">ios</a></li>
            </ul>
        </div>
        <div class="tab-content">
            <div id="windows" class="tab-pane active">
                <div class="app-item app-sync" id="pcClientID">
                    <h2><strong>Windows</strong> <spring:message code="appindex.pc.sync"/></h2>
                    <h4><spring:message code="appindex.intro.pc.sync"/></h4>
                    <div class="form-horizontal clearfix">
                        <div class="control-group">
                            <label class="control-label"><spring:message code="common.field.version"/> : </label>
                            <div class="controls">
                                <span class="uneditable-textarea">${cse:htmlEscape(pcClient.version)}</span>
                            </div>
                        </div>
                        <div class="control-group">
                            <label class="control-label"><spring:message code='common.field.update'/> : </label>
                            <div class="controls">
                                <span class="uneditable-textarea" id="pcUpdateTime"></span>
                            </div>
                        </div>
                        <div class="control-group">
                            <label class="control-label"><spring:message code="common.field.size"/> : </label>
                            <div class="controls">
                                <span class="uneditable-textarea">${cse:byteToMBString(pcClient.size)}</span>
                            </div>
                        </div>
                        <div class="control-group">
                            <label class="control-label"><spring:message code="appindex.os"/> : </label>
                            <div class="controls">
                                <span class="uneditable-textarea">${cse:htmlEscape(pcClient.supportSys)}</span>
                            </div>
                        </div>
                        <div class="control-group">
                            <label class="control-label"><spring:message code="appindex.checkCode"/> : </label>
                            <div class="controls">
                                <span class="uneditable-textarea">${cse:htmlEscape(pcClient.checkCode)}</span>
                            </div>
                        </div>
                    </div>
                    <button type="button" class="btn btn-large btn-primary"
                            onclick='download("${cse:htmlEscape(pcClient.downloadUrl)}")'>
                        <spring:message code="appindex.downloadnow"/></button>

                </div>
                <div id="controlID" style=" border-top:1px solid #ddd;"></div>
                <div class="app-item app-dummy" id="cloudClientID">
                    <h2><strong>Windows</strong> <spring:message code="appindex.pccloud"/></h2>
                    <h4><spring:message code="appindex.intro.pc.eazy"/></h4>
                    <div class="form-horizontal clearfix">
                        <div class="control-group">
                            <label class="control-label"><spring:message code="common.field.version"/> : </label>
                            <div class="controls">
                                <span class="uneditable-textarea">${cse:htmlEscape(clouderClient.version)}</span>
                            </div>
                        </div>
                        <div class="control-group">
                            <label class="control-label"><spring:message code='common.field.update'/> : </label>
                            <div class="controls">
                                <span class="uneditable-textarea" id="clouderUpdateTime"></span>
                            </div>
                        </div>
                        <div class="control-group">
                            <label class="control-label"><spring:message code="common.field.size"/> : </label>
                            <div class="controls">
                                <span class="uneditable-textarea">${cse:byteToMBString(clouderClient.size)}</span>
                            </div>
                        </div>
                        <div class="control-group">
                            <label class="control-label"><spring:message code="appindex.os"/> : </label>
                            <div class="controls">
                                <span class="uneditable-textarea">${cse:htmlEscape(clouderClient.supportSys)}</span>
                            </div>
                        </div>
                        <div class="control-group">
                            <label class="control-label"><spring:message code="appindex.checkCode"/> : </label>
                            <div class="controls">
                                <span class="uneditable-textarea">${cse:htmlEscape(clouderClient.checkCode)}</span>
                            </div>
                        </div>
                    </div>
                    <button type="button" class="btn btn-large btn-primary"
                            onclick='download("${cse:htmlEscape(clouderClient.downloadUrl)}")'>
                        <spring:message code="appindex.downloadnow"/>
                    </button>
                </div>
            </div>
            <div id="android" class="app-item app-android tab-pane">
                <h2><strong>Android</strong> <spring:message code="appindex.version"/></h2>
                <h4><spring:message code="appindex.intro.android"/></h4>
                <div class="form-horizontal clearfix">
                    <div class="control-group">
                        <label class="control-label"><spring:message code="common.field.version"/> : </label>
                        <div class="controls">
                            <span class="uneditable-textarea">${cse:htmlEscape(androidClient.version)}</span>
                        </div>
                    </div>
                    <div class="control-group">
                        <label class="control-label"><spring:message code='common.field.update'/> : </label>
                        <div class="controls">
                            <span class="uneditable-textarea" id="androidUpdateTime"></span>
                        </div>
                    </div>
                    <div class="control-group">
                        <label class="control-label"><spring:message code="common.field.size"/> : </label>
                        <div class="controls">
                            <span class="uneditable-textarea">${cse:byteToMBString(androidClient.size)}</span>
                        </div>
                    </div>
                    <div class="control-group">
                        <label class="control-label"><spring:message code="appindex.os"/> : </label>
                        <div class="controls">
                            <span class="uneditable-textarea">${cse:htmlEscape(androidClient.supportSys)}</span>
                        </div>
                    </div>
                    <div class="control-group">
                        <label class="control-label"><spring:message code="appindex.checkCode"/> : </label>
                        <div class="controls">
                            <span class="uneditable-textarea">${cse:htmlEscape(androidClient.checkCode)}</span>
                        </div>
                    </div>
                </div>
                <div>
                    <span class="scan-download" id="androidScanImage">&nbsp;&nbsp;<spring:message
                            code="appindex.android.scan"/></span>
                </div>
                <button type="button" class="btn btn-large btn-primary"
                        onclick='download("${cse:htmlEscape(androidClient.downloadUrl)}")'>
                    <spring:message code="appindex.downloadnow"/>
                </button>

            </div>
            <div id="ios" class="app-item app-ios tab-pane">
                <h2><strong>iOS</strong> <spring:message code="appindex.version"/></h2>
                <h4><spring:message code="appindex.intro.android"/></h4>
                <div class="form-horizontal clearfix">
                    <div class="control-group">
                        <label class="control-label"><spring:message code="common.field.version"/> : </label>
                        <div class="controls">
                            <span class="uneditable-textarea">${cse:htmlEscape(iosClient.version)}</span>
                        </div>
                    </div>
                    <div class="control-group">
                        <label class="control-label"><spring:message code='common.field.update'/> : </label>
                        <div class="controls">
                            <span class="uneditable-textarea" id="iosUpdateTime"></span>
                        </div>
                    </div>
                    <div class="control-group">
                        <label class="control-label"><spring:message code="common.field.size"/> : </label>
                        <div class="controls">
                            <span class="uneditable-textarea">${cse:byteToMBString(iosClient.size)}</span>
                        </div>
                    </div>
                    <div class="control-group">
                        <label class="control-label"><spring:message code="appindex.os"/> : </label>
                        <div class="controls">
                            <span class="uneditable-textarea">${cse:htmlEscape(iosClient.supportSys)}</span>
                        </div>
                    </div>
                    <div class="control-group">
                        <label class="control-label"><spring:message code="appindex.checkCode"/> : </label>
                        <div class="controls">
                            <span class="uneditable-textarea">${cse:htmlEscape(iosClient.checkCode)}</span>
                        </div>
                    </div>
                </div>
                <div>
                    <span class="scan-download" id="iosScanImage">&nbsp;&nbsp;<spring:message
                            code="appindex.ios.scan"/></span>
                </div>
                <button type="button" class="btn btn-large btn-primary"
                        onclick='download("${cse:htmlEscape(iosClient.downloadUrl)}")'>
                    <spring:message code="appindex.downloadnow"/>
                </button>
            </div>
        </div>
    </div>
</div>


<%@ include file="../common/footer.jsp" %>

<script type="text/javascript">
    $(function () {
        if ("${pcClient.releaseDate}" != "") {
            $("#pcUpdateTime").append(getLocalTime(parseInt("${pcClient.releaseDate.getTime()}")));
        }
        if ("${androidClient.releaseDate}" != "") {
            $("#androidUpdateTime").append(getLocalTime(parseInt("${androidClient.releaseDate.getTime()}")));
        }
        if ("${iosClient.releaseDate}" != "") {
            $("#iosUpdateTime").append(getLocalTime(parseInt("${iosClient.releaseDate.getTime()}")));
        }
        if ("${clouderClient.releaseDate}" != "") {
            $("#clouderUpdateTime").append(getLocalTime(parseInt("${clouderClient.releaseDate.getTime()}")));
        }
        $("#androidScanImage").css("background-image", "url(${ctx}<c:out value='${androidClient.twoDimCodeUrl}'/>)");
        $("#iosScanImage").css("background-image", "url(${ctx}<c:out value='${iosClient.twoDimCodeUrl}'/>)")
        $('#myTab a').click(function (e) {
            e.preventDefault();
            $(this).tab('show');
        })
        if ("${pcClient.releaseDate}" != "" && "${clouderClient.releaseDate}" != "") {
            $("#controlID").show();
        }
        loadSysSetting("${ctx}", '<spring:message code="common.language1" />');

        navMenuSelected("navAPP");

        if ('<spring:message code="common.language1"/>' == "en") {
            $(".app-sync").addClass("app-sync-en");
            $(".app-android").addClass("app-android-en");
            $(".app-ios").addClass("app-ios-en");
        }
        comboxRemoveLoading("pageLoadingContainer");
    });

    function isIeBelow11() {
        if (navigator.userAgent.indexOf("MSIE") < 0) {
            return false;
        } else if (navigator.userAgent.indexOf("MSIE 10.0") >= 0) {
            return true;
        } else if (navigator.userAgent.indexOf("MSIE 9.0") >= 0) {
            return true;
        } else if (navigator.userAgent.indexOf("MSIE 8.0") >= 0) {
            return true;
        } else if (navigator.userAgent.indexOf("MSIE 8.0") >= 0) {
            return true;
        } else {
            return false;
        }
    }

    function download(url) {
        if (isIeBelow11()) {
            top.ymPrompt.alert({
                title: '<spring:message code="common.title.info"/>',
                message: '<spring:message code="common.download.appfile" />'
            });
            window.location.href = url;
            return;
        }
        if (url == "") {
            handlePrompt("error", "<spring:message code='appindex.client.not.publish'/>");
        } else {
            window.location.href = url;
        }
    }
</script>
</body>
</html>