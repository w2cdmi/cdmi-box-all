<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="../common/common.jsp" %>
    <%@ include file="../files/commonForFiles.jsp" %>
    <script src="${ctx}/static/js/public/JQbox-hw-grid.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/JQbox-hw-page.js" type="text/javascript"></script>
</head>
<body>

<div class="pop-content file-version-con">
    <div class="description"><strong id="fileName">version.dat</strong> <spring:message code="versionlist.versionList"/>
    </div>
    <div id="fileList"></div>
    <div id="fileListPageBox"></div>
</div>

<script type="text/javascript">
    var ownerId = "<c:out value='${ownerId}'/>";
    var showDel = "<c:out value='${showDel}'/>";
    var showDownload = "<c:out value='${showDownload}'/>";
    var opts_viewGrid = null;
    var opts_page = null;
    var currentPage = 1;
    var perPage, totalData;
    var catalogParentId = 0;
    var catalogData = null;
    var headData = {
        "Vnumber": {"title": "", "width": "12%"},
        "modifiedAt": {"title": "<spring:message code='common.field.modifyTime'/>", "width": "32%"},
        "size": {"title": "<spring:message code='common.field.size'/>", "width": "15%"},
        "operation": {"title": "<spring:message code='common.field.operation'/>", "width": "", "cls": "ar"}
    };

    $(function () {
        init();

        listVersion(currentPage, <c:out value='${nodeId}'/>);
    });

    function init() {

        opts_viewGrid = $("#fileList").comboTableGrid({
            headData: headData,
            border: false,
            hideHeader: true,
            ItemOp: "user-defined",
            height: 187,
            dataId: "id"
        });

        opts_page = $("#fileListPageBox").comboPage({
            pageSkip: false,
            lang: '<spring:message code="common.language1"/>'
        });

        $.fn.comboPage.pageSkip = function (opts, _idMap, curPage) {
            listVersion(curPage, <c:out value='${nodeId}'/>);
        };

        $.fn.comboTableGrid.setItemOp = function (tableData, rowData, tdItem, colIndex) {
            var vIndex;
            for (var i = 0; i < tableData.length; i++) {
                if (tableData[i].id == rowData.id) {
                    vIndex = totalData - (i + (currentPage - 1) * perPage);
                    break;
                }
            }

            switch (colIndex) {
                case "Vnumber":
                    if (vIndex == totalData) {
                        tdItem.find("p").html('<span class="label label-info">V' + vIndex + '</span>');
                    } else {
                        tdItem.find("p").html('<span class="label">V' + vIndex + '</span>');
                    }
                    break;
                case "modifiedAt":
                    var date = tdItem.find("p").text();
                    tdItem.find("p").text(getLocalTime(Number(date)));
                    tdItem.attr("title", getLocalTime(Number(date)));
                    break;
                case "size":
                    try {
                        var size = tdItem.find("p").text();
                        tdItem.find("p").text(formatFileSize(size));
                        tdItem.attr("title", formatFileSize(size));
                    } catch (e) {
                    }
                    break;
                case "operation":
                    var downloadLink;
                    try {
                        if (vIndex == totalData || showDel != 1) {
                            if (showDownload == 1) {
                                downloadLink = "<a href=\"javascript:downloadVersion(" + rowData.id + ")\"><spring:message code='button.download'/></a>";
                            }
                        } else {
                            if (showDownload == 1) {
                                downloadLink = "<a href=\"javascript:downloadVersion(" + rowData.id + ")\"><spring:message code='button.download'/></a>" +
                                    " | <a href=\"javascript:deleteVersion(" + rowData.id + ")\"><spring:message code='button.delete'/></a>" +
                                    " | <a href=\"javascript:restoreVersion(" + rowData.id + ")\"><spring:message code='button.restoreVersion'/></a>";
                            } else {
                                downloadLink = "<a href=\"javascript:deleteVersion(" + rowData.id + ")\"><spring:message code='button.delete'/></a>" +
                                    +" | <a href=\"javascript:restoreVersion(" + rowData.id + ")\"><spring:message code='button.restoreVersion'/></a>";
                            }
                        }
                        tdItem.find("p").append(downloadLink);

                    } catch (e) {
                    }
                    break;

                default :
                    break;
            }
        };

    }

    function listVersion(curPage, nodeId) {
        var url = "${ctx}/files/listVersion";
        var params = {
            "token": "<c:out value='${token}'/>",
            "ownerId": ownerId,
            "nodeId": nodeId,
            "pageNumber": curPage,
            "desc": true
        };

        $.ajax({
            type: "POST",
            data: params,
            url: url,
            error: function (request) {
                if (request.status == 404) {
                    ymPrompt.errorInfo({
                        title: "<spring:message code='button.delete'/>",
                        message: "<spring:message code='file.isNotExsitOrDel'/>",
                        handler: function () {
                            top.ymPrompt.close();
                            return;
                        }
                    });
                    return;
                } else {
                    handlePrompt("error", "<spring:message code='file.errorMsg.listFileVersionFailed'/>");
                }
            },
            success: function (data) {
                currentPage = data.number;
                perPage = data.size;
                totalData = data.totalElements;
                catalogData = data.content;

                if (catalogData.length == 0 && currentPage != 1) {
                    currentPage--;
                    listVersion(currentPage, nodeId);
                    return;
                }

                $("#fileName").text(catalogData[0].name);
                $("#fileList").setTableGridData(catalogData, opts_viewGrid);
                $("#fileListPageBox").setPageData(opts_page, data.number, data.size, data.totalElements);
            }
        });

    }

    function downloadVersion(nodeId) {
        if (nodeId == null || nodeId == "") {
            var selectedId = $("#fileList").getTableGridSelectedId();
            nodeId = selectedId[0];
        }

        $.ajax({
            type: "GET",
            async: false,
            url: "${ctx}/files/getDownloadUrl/" + ownerId + "/" + nodeId + "?t=" + new Date().toString(),
            error: function (request) {
                if (request.status == 404) {
                    ymPrompt.errorInfo({
                        title: "<spring:message code='button.download'/>",
                        message: "<spring:message code='file.isNotExsitOrDel'/>",
                        handler: function () {
                            top.ymPrompt.close();
                            return;
                        }
                    });
                    return;
                } else {
                    downloadFileErrorHandler(request)
                }
            },
            success: function (data) {
                if (isMicrosoftIEOrEdgeBrowser()) {
                    window.parent.downloadVersionFile(data);
                } else {
                    jQuery('<form action="' + data + '" method="get"></form>').appendTo('body').submit().remove();
                }

            }
        });
    }
    //是否为IE浏览器
    function isMicrosoftIEOrEdgeBrowser() {
        //获取当前浏览器信息
        var currentBrowser = navigator.userAgent.toLocaleLowerCase();
        if (currentBrowser.indexOf("compatible") > -1 && currentBrowser.indexOf("msie") > -1) {
            return true;
        }
        else if (currentBrowser.indexOf("edge")) {
            return true;
        }
        else if (currentBrowser.indexOf("trident") > -1 && currentBrowser.indexOf("rv:") > -1) {
            return true;
        }
        else if (currentBrowser.indexOf("firefox/") > -1) {
            return false;
        }

        else if (currentBrowser.indexOf("chrome/") > -1) {
            return false;
        }

        return false;
    }

    function deleteVersion(nodeId) {
        ymPrompt.confirmInfo({
            title: "<spring:message code='file.title.deleteFile'/>",
            message: "<spring:message code='file.info.deleteVersion'/>",
            btn: [["<spring:message code='button.ok'/>", "ok", true, "btn-focus"], ["<spring:message code='button.cancel'/>", "no", true, "btn-cancel"]],
            handler: function (tp) {
                if (tp == 'ok') {
                    $.ajax({
                        type: "POST",
                        url: "${ctx}/nodes/delete",
                        data: {'ownerId': ownerId, 'ids': nodeId, 'token': "<c:out value='${token}'/>"},
                        error: function () {
                            handlePrompt("error", "<spring:message code='operation.failed'/>");
                        },
                        success: function (data) {
                            inLayerLoading("<spring:message code='common.task.doing'/>", "loading-bar");
                            if (typeof(data) == 'string' && data.indexOf('<html>') != -1) {
                                window.location.href = "${ctx}/logout";
                                return;
                            }

                            var totalPage = Math.ceil(totalData / perPage);
                            var currentPageCount = totalData % perPage;
                            if (currentPageCount == 1 && currentPage == totalPage) {
                                currentPage--;
                            }

                            asyncDelete(data);
                        }
                    });
                }
            }
        });
    }

    function restoreVersion(nodeId) {
        var params = {
            "ownerId": ownerId,
            "nodeId": nodeId,
            "token": "<c:out value='${token}'/>"
        };

        $.ajax({
            type: "POST",
            url: "${ctx}/files/restoreVersion",
            data: params,
            error: function (request) {
                if (request.status == 404) {
                    ymPrompt.errorInfo({
                        title: "<spring:message code='button.restoreVersion'/>",
                        message: "<spring:message code='file.isNotExsitOrDel'/>",
                        handler: function () {
                            top.ymPrompt.close();
                            return;
                        }
                    });
                    return;
                } else {
                    doFileCommonError(request);
                }
            },
            success: function (data) {
                inLayerLoading("<spring:message code='common.task.doing'/>", "loading-bar");
                if (typeof(data) == 'string' && data.indexOf('<html>') != -1) {
                    window.location.href = "${ctx}/logout";
                    return;
                }
                unLayerLoading();
                handlePrompt("success", "<spring:message code='operation.success'/>");
                listVersion(currentPage, <c:out value='${nodeId}'/>);
                top.listFile(top.currentPage, top.catalogParentId);
            }
        });
    }

    function asyncDelete(taskId) {
        $.ajax({
            type: "GET",
            url: "${ctx}/nodes/listen?taskId=" + taskId + "&" + new Date().toString(),
            error: function (request) {
                unLayerLoading();
                handlePrompt("error", "<spring:message code='operation.failed'/>");
            },
            success: function (data, textStatus, jqXHR) {
                switch (data) {
                    case "Doing":
                        setTimeout(function () {
                            asyncDelete(taskId);
                        }, 1000);
                        break;
                    case "NotFound":
                        unLayerLoading();
                        handlePrompt("success", "<spring:message code='operation.success'/>");
                        listVersion(currentPage, <c:out value='${nodeId}'/>);
                        break;
                    case "NoSuchSource":
                        unLayerLoading();
                        handlePrompt("error", "<spring:message code='error.notfound'/>");
                        listVersion(currentPage, <c:out value='${nodeId}'/>);
                        break;
                    case "Forbidden":
                        unLayerLoading();
                        handlePrompt("error", "<spring:message code='error.forbid'/>");
                        break;
                    case "SystemException":
                        unLayerLoading();
                        handlePrompt("error", "<spring:message code='operation.failed'/>");
                        break;
                }

                top.listFile(top.currentPage, top.catalogParentId);
            }
        });
    }


</script>

</body>
</html>