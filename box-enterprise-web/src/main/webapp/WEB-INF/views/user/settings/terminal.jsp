<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="pw.cdmi.box.disk.utils.CSRFTokenManager" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%
    request.setAttribute("token", CSRFTokenManager.getTokenForSession(session));
%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=10"/>
    <meta http-equiv="X-UA-Compatible" content="IE=9"/>
    <meta http-equiv="X-UA-Compatible" content="IE=8"/>
    <META HTTP-EQUIV="Expires" CONTENT="0">
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <META HTTP-EQUIV="Cache-control"
          CONTENT="no-cache, no-store, must-revalidate">
    <META HTTP-EQUIV="Cache" CONTENT="no-cache">
    <title><spring:message code="user.settings.terminal"/></title>
    <link href="${ctx}/static/skins/default/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/static/skins/default/css/public.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/static/skins/default/css/main.css" rel="stylesheet" type="text/css"/>

    <script src="${ctx}/static/js/public/jquery-1.10.2.min.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/bootstrap.min.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/JQbox-hw-grid.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/common.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/JQbox-hw-page.js" type="text/javascript"></script>

</head>
<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);
%>
<body>
<div class="sys-content">
    <div class="table-con">
        <div id="terminalList"></div>
        <div id="terminalListPageBox"></div>
    </div>
</div>
</body>
<script type="text/javascript">
    var currentPage = "1";
    var opts_viewGrid = null;
    var navId = "";
    var opts_page = null;
    var headData = {
        "deviceType": {"title": '<spring:message code="terminal.type" />', "width": "11%"},
        "deviceName": {"title": '<spring:message code="terminal.name" />', "width": "12%"},
        "deviceOS": {"title": '<spring:message code="terminal.clientOS" />', "width": ""},
        "deviceAgent": {"title": '<spring:message code="terminal.agent" />', "width": ""},
        "lastAccessIP": {"title": '<spring:message code="terminal.lastAccessIP" />', "width": ""},
        "lastAccessAt": {"title": '<spring:message code="terminal.lastAccessAt" />', "width": ""},
        "status": {"title": '<spring:message code="common.field.status" />', "width": "7%"},
        "operation": {"title": '<spring:message code="common.field.operation" />', "width": "10%"}
    };
    function refreshWindow() {
        window.location.reload();
    }
    $(document).ready(function () {
        var isSign = ${needDeclaration};
        if (isSign) {
            showDeclaration();
        }
        init();
        initDataList(currentPage);

    });
    function init() {
        opts_viewGrid = $("#terminalList").comboTableGrid({
            headData: headData,
            ItemOp: "user-defined",
            dataId: "id"
        });
        opts_page = $("#terminalListPageBox").comboPage({lang: '<spring:message code="common.language1"/>'});
        $.fn.comboPage.pageSkip = function (opts, _idMap, curPage) {
            initDataList(curPage);
        };


        $.fn.comboTableGrid.setItemOp = function (tableData, rowData, tdItem, colIndex) {
            var btn;
            var createdAt = new Date(rowData.createdAt);
            var lastAccessAt = new Date(rowData.lastAccessAt);
            var status = rowData.status;
            var clientType = rowData.deviceType;
            var deviceName = rowData.deviceName;
            var deviceSN = rowData.deviceSn;
            switch (colIndex) {
                case "deviceType":
                    try {
                        if (clientType == 0) {
                            tdItem.find("p").html('<spring:message code="terminal.web" />').parent().attr("title", '<spring:message code="terminal.web" />');
                        }
                        if (clientType == 1) {
                            tdItem.find("p").html('<spring:message code="terminal.pc" />').parent().attr("title", '<spring:message code="terminal.pc" />');
                        }
                        if (clientType == 2) {
                            tdItem.find("p").html('<spring:message code="terminal.andriod" />').parent().attr("title", '<spring:message code="terminal.andriod" />');
                        }
                        if (clientType == 3) {
                            tdItem.find("p").html('<spring:message code="terminal.ios" />').parent().attr("title", '<spring:message code="terminal.ios" />');
                        }
                    } catch (e) {
                    }
                    break;
                case "deviceName":
                    try {
                        if (deviceName == "") {
                            tdItem.find("p").html('-').parent().attr("title", '');
                        }
                    } catch (e) {
                    }
                    break;
                case "lastAccessAt":
                    try {
                        tdItem.find("p").html(getSmpFormatDate(lastAccessAt)).parent().attr("title", getSmpFormatDate(lastAccessAt));
                    } catch (e) {
                    }
                    break;
                case "status":
                    try {
                        if (status == 0 && clientType != 0) {
                            tdItem.find("p").html('<spring:message code="terminal.offLine" />').parent().attr("title", '<spring:message code="terminal.offLine" />');
                        }
                        else if (status == 1 && clientType != 0) {
                            tdItem.find("p").html('<spring:message code="terminal.onLine" />').parent().attr("title", '<spring:message code="terminal.onLine" />');
                        }
                        else if (status == 2 && clientType != 0) {
                            tdItem.find("p").html('<spring:message code="terminal.disabled" />').parent().attr("title", '<spring:message code="terminal.disabled" />');
                        }
                        else {
                            tdItem.find("p").html('-').parent().attr("title", '-');
                        }
                    } catch (e) {
                    }
                    break;
                case "operation":
                    try {
                        if (status == 2 && clientType != 0) {
                            btn = '<button onclick="changeStatus(0,\'' + deviceSN + '\')" class="btn btn-small" type="button"><spring:message code="terminal.enable" /></button>';
                        } else if (status != 2 && clientType != 0) {
                            btn = '<button onclick="changeStatus(2,\'' + deviceSN + '\')" class="btn btn-small" type="button"><spring:message code="terminal.disable" /></button>';
                        }
                        else {
                            btn = '-';
                        }
                        tdItem.find("p").addClass("ac").append(btn);
                    } catch (e) {
                    }
                    break;

                default :
                    break;
            }
        };
    }
    function initDataList(curPage) {
        currentPage = curPage;
        var url = "${ctx}/user/terminal";
        var params = {
            "pageNumber": curPage,
            "token": "<c:out value='${token}'/>"
        };

        $.ajax({
            type: "POST",
            url: url,
            data: params,
            error: function (request) {
                handlePrompt("error", '<spring:message code="operation.failed" />');
            },
            success: function (data) {
                catalogData = data.content;
                $("#terminalList").setTableGridData(catalogData, opts_viewGrid);
                $("#terminalListPageBox").setPageData(opts_page, data.number, data.size, data.totalElements);
                var pageH = $("body").outerHeight();
                top.iframeAdaptHeight(pageH);
            }
        });
    }
    function changeStatus(status, deviceSN) {
        var params = {
            "status": status,
            "deviceSN": deviceSN,
            "token": "<c:out value='${token}'/>"
        };
        if (status == 2) {
            top.ymPrompt.confirmInfo({
                title: '<spring:message code="common.field.operation" />',
                message: '<spring:message code="terminal.promptmsg1" />' + '<br />' + '<spring:message code="terminal.promptmsg2" />',
                handler: function (tp) {
                    if (tp == 'ok') {
                        $.ajax({
                            type: "POST",
                            url: "${ctx}/user/terminal/update",
                            data: params,
                            error: function (request) {
                                handlePrompt("error", '<spring:message code="operation.failed" />');
                                initDataList(currentPage);
                            },
                            success: function () {
                                handlePrompt("success", '<spring:message code="operation.success" />');
                                initDataList(currentPage);
                            }
                        });
                    }
                }
            });
        }
        if (status == 0) {
            $.ajax({
                type: "POST",
                url: "${ctx}/user/terminal/update",
                data: params,
                error: function (request) {
                    handlePrompt("error", '<spring:message code="operation.failed" />');
                    initDataList(currentPage);
                },
                success: function () {
                    handlePrompt("success", '<spring:message code="operation.success" />');
                    initDataList(currentPage);
                }
            });
        }
    }


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
</script>
</html>
