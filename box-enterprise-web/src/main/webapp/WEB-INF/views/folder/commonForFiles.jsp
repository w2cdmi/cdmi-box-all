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
<script type="text/javascript">
    function userCustomInit() {
        if (isDesc == "true") {
            $("#sortArray").attr("class", "icon-arrow-down");
        } else {
            $("#sortArray").attr("class", "icon-arrow-up");
        }


        if (orderField == "modifiedAt") {
            $("#sortOrderName").html("<spring:message code='common.field.date'/>");
            if (isDesc == "true") {
                $("#dateSortLink").append("<i class='icon-arrow-down'></i>");
            } else {
                $("#dateSortLink").append("<i class='icon-arrow-up'></i>");
            }
        }
        if (orderField == "name") {
            $("#sortOrderName").html("<spring:message code='common.field.name'/>");
            if (isDesc == "true") {
                $("#nameSortLink").append("<i class='icon-arrow-down'></i>");
            } else {
                $("#nameSortLink").append("<i class='icon-arrow-up'></i>");
            }
        }
        if (orderField == "size") {
            $("#sortOrderName").html("<spring:message code='common.field.size'/>");
            if (isDesc == "true") {
                $("#sizeSortLink").append("<i class='icon-arrow-down'></i>");
            } else {
                $("#sizeSortLink").append("<i class='icon-arrow-up'></i>");
            }
        }
    }

    function initTree() {
        var setting = {
            async: {
                enable: true,
                url: "${ctx}/folders/listTreeNode/" + ownerId,
                otherParam: {"token": "<c:out value='${token}'/>", "orderField": orderField, "desc": isDesc},
                autoParam: ["id", "ownedBy"]
            },
            callback: {
                onAsyncSuccess: zTreeOnAsyncError,
                onClick: zTreeOnClick
            }
        };
        var zNodes = [{
            id: "0",
            name: "<spring:message code='file.index.allFiles'/>",
            ownedBy: ownerId,
            open: true,
            isParent: true
        },
            {
                id: "-1",
                name: "<spring:message code='button.recycler'/>",
                ownedBy: ownerId,
                icon: "static/zTree/img/icon-trash.png",
                url: "${ctx}/trash#1",
                target: "_self"
            }];

        $.fn.zTree.init($("#treeArea"), setting, zNodes);
        $("#treeArea > li > span").click();
        $("#treeArea").toggle();

        $("body").mousedown(function () {
            var ev = arguments[0] || window.event;
            var srcElement = ev.srcElement || ev.target;
            if (srcElement != $("#treeArea").get(0) && srcElement != $("#folderTree").get(0) && $(srcElement).parents("button").attr("id") != "folderTree" && $(srcElement).parents(".ztree").attr("id") != "treeArea") {
                $("#treeArea").hide();
            }
        })

        stopDefaultScroll("treeArea");
    }
    function initTeamSpaceTree() {
        var setting = {
            async: {
                enable: true,
                url: "${ctx}/folders/listTreeNode/<c:out value='${teamId}'/>",
                otherParam: {"token": "<c:out value='${token}'/>", "orderField": orderField, "desc": isDesc},
                autoParam: ["id", "ownedBy"]
            },
            callback: {
                onAsyncSuccess: zTreeOnAsyncError,
                onClick: zTreeOnClickForTeamSpace
            }
        };
        var teamName = $("#teamName").val();
        var zNodes = [{
            id: "0",
            name: "<spring:message code='teamSpace.title.teamSpace'/>(" + teamName + ")",
            ownedBy: "<c:out value='${teamId}'/>",
            open: true,
            isParent: true
        },
            {
                id: "-1",
                name: "<spring:message code='button.recycler'/>",
                ownedBy: "<c:out value='${teamId}'/>",
                icon: "${ctx}/static/zTree/img/icon-trash.png"
            }];

        $.fn.zTree.init($("#treeArea"), setting, zNodes);
        $("#treeArea > li > span").click();
        $("#treeArea").toggle();

        $("body").mousedown(function () {
            var ev = arguments[0] || window.event;
            var srcElement = ev.srcElement || ev.target;
            if (srcElement != $("#treeArea").get(0) && srcElement != $("#folderTree").get(0) && $(srcElement).parents("button").attr("id") != "folderTree" && $(srcElement).parents(".ztree").attr("id") != "treeArea") {
                $("#treeArea").hide();
            }
        })

        stopDefaultScroll("treeArea");
    }

    function zTreeOnAsyncError(event, treeId, treeNode, XMLHttpRequest, textStatus, errorThrown) {
        if (XMLHttpRequest.length == 2 && XMLHttpRequest.substring(0, 2) != "[]") {
            location.href = "${ctx}/logout";
        }
        if (XMLHttpRequest.length > 2 && XMLHttpRequest.substring(0, 2) != "[{") {
            location.href = "${ctx}/logout";
        }
    };

    function zTreeOnClick(event, treeId, treeNode) {
        if (treeNode.id != "-1") {
            goFileIndex(treeNode.id);
        }
        $("#treeArea").hide();
    };

    function zTreeOnClickForTeamSpace(event, treeId, treeNode) {
        if (treeNode.id == "-1") {
            goTeamSpaceRecycler(treeNode.ownedBy);
        } else {
            goTeamSpaceFileIndex(treeNode.ownedBy, treeNode.id);
        }
        $("#treeArea").hide();
    };

    function teamDetailList(el) {
        var url = "${ctx}/teamspace/file/" + el;
        jQuery('<form action="' + url + '" method="get"></form>')
            .appendTo('body').submit().remove();
    }

    function createLinksBreadcrumb(teamId) {
        var breadcrumbItem = $("<li><a href='${ctx}/teamspace'><span><spring:message code='teamSpace.title.teamSpace' /></span></a></li><li ><a href='${ctx}/teamspace/file/" + teamId + "/'><span title='<c:out value="${teamName}"/>'><c:out value='${teamName}'/></span></a></li>");
        $("#breadcrumbCon").html(breadcrumbItem);
        $("#breadcrumbCon").append("<li class=\"active\"><span><spring:message code='share.menu.shareLinks'/></span></li>");
    }

    function createBreadcrumb(catalogParentId, ownerId, searchResultCount) {
        var breadcrumbItem;
        $("#breadcrumbCon").html("");
        if (viewMode == "search" || viewMode == "highSearch") {
            breadcrumbItem = $("<li><a href='#file/1/0'><span><spring:message code='file.index.allFiles'/></span></a></li><p class='pull-left'><spring:message code='file.tips.searchResult' arguments='"+ searchResultCount +"'/></p>");
            $("#breadcrumbCon").append(breadcrumbItem);
            return;
        }

        if (catalogParentId == "-1") {
            breadcrumbItem = $("<li><a href='${ctx}/#file/1/0' id='0'><span><spring:message code='file.index.allFiles'/></span></a></li>");
            $("#breadcrumbCon").append(breadcrumbItem).append("<li class=\"active\"><span><spring:message code='button.recycler'/></span></li>");
        } else {
            var url = "folders/getPaths/" + ownerId + "/" + catalogParentId;
            $.ajax({
                type: "GET",
                url: url,
                cache: false,
                async: false,
                timeout: 180000,
                success: function (data) {
                    if (data.length < 1) {
                        breadcrumbItem = $("<li class='active'><span><spring:message code='file.index.allFiles'/></span></li>");
                        $("#breadcrumbCon").append(breadcrumbItem);
                    } else {
                        $("#breadcrumbCon").append("<li><a href='#file/1/0'><span><spring:message code='file.index.allFiles'/></span></a></li>");
                        // 离职文件处理
                        try {
                            if (isInMigrationFolder && !isMigrationFolder) {
                                var migrationFolderName = getCookie("migrationFolderName");
                                var migrationParantId = getCookie("migrationParantId");

                                if (migrationFolderName) {
                                    breadcrumbItem = $('<li><a href=\"#file/1/' + migrationParantId + '\"><span title=\"' + htmlEncode(migrationFolderName) + '\">' + migrationFolderName + '</span></a></li>');
                                    $("#breadcrumbCon").append(breadcrumbItem);
                                }
                            }
                        } catch (e) {
                        }

                        for (var i = 0; i < data.length; i++) {
                            if (i == data.length - 1) {
                                breadcrumbItem = $("<li class='active'><span title='" + data[i].name + "'>" + data[i].name + "</span></li>");
                                $("#breadcrumbCon").append(breadcrumbItem);
                            } else {
                                breadcrumbItem = $('<li><a href=\"#file/1/' + data[i].id + '\"><span title=\"' + data[i].name + '\">' + data[i].name + '</span></a></li>');
                                $("#breadcrumbCon").append(breadcrumbItem);
                            }
                        }
                    }

                    $("#breadcrumbCon span").tooltip({
                        container: "body",
                        placement: "top",
                        delay: {show: 100, hide: 0},
                        animation: false
                    });

                    var len = data.length + 1,
                        maxW = $("#breadcrumbCon").innerWidth();
                    breadcrumbAdapt(len, maxW);
                    $(window).bind("resize", function () {
                        maxW = $("#breadcrumbCon").innerWidth();
                        $("#breadcrumbCon").find("li.over-pass").remove();
                        $("#breadcrumbCon").find("li").show();
                        breadcrumbAdapt(len, maxW);
                    });

                }
            })

        }
    }

    function breadcrumbAdapt(len, maxW) {
        var objW = 0;
        for (var i = 0; i < len; i++) {
            objW += $("#breadcrumbCon").find("li").eq(i).outerWidth();
        }
        if (objW > maxW) {
            var _w = 0, j = 1;
            for (var i = 1; i < len - 1; i++) {
                _w += $("#breadcrumbCon").find("li").eq(i).width();
                if (_w > (objW - maxW + 45)) {
                    j = i;
                    for (var n = 1; n < j + 1; n++) {
                        $("#breadcrumbCon").find("li").eq(n).hide();
                    }
                    $("#breadcrumbCon").find("li").eq(0).after('<li class="over-pass"><span>...</span></li>');
                    break;
                }
            }
        }
    }

    function chanageNameValue(name) {
        if ("ufm.DocType.document" == name) {
            return "<spring:message code='ufm.DocType.document'/>"
        } else if ("ufm.DocType.picture" == name) {
            return "<spring:message code='ufm.DocType.picture'/>"
        } else if ("ufm.DocType.audio" == name) {
            return "<spring:message code='ufm.DocType.audio'/>"
        } else if ("ufm.DocType.video" == name) {
            return "<spring:message code='ufm.DocType.video'/>"
        } else if ("ufm.DocType.other" == name) {
            return "<spring:message code='ufm.DocType.other'/>"
        } else {
            return name;
        }
    }

    function createBreadcrumbForDoctypeTeamSpace(teamId) {
        var breadcrumbItem = $("<li><a href='${ctx}/teamspace'><span><spring:message code='teamSpace.title.teamSpace' /></span></a></li><li ><a href='${ctx}/teamspace/file/" + teamId + "/'><span title='<c:out value="${teamName}"/>'><c:out value="${teamName}"/></span></a></li>");
        $("#breadcrumbCon").html(breadcrumbItem);
        var doctypeName = chanageNameValue($("#docTypeName").attr("value"));
        $("#breadcrumbCon").append("<li class=\"active\"><span>" + doctypeName + "</span></li>");
    }

    function createBreadcrumbForTeamSpace(catalogParentId, teamId, searchResultCount) {
        var breadcrumbItem;
        if (viewMode == "search" || viewMode == "highSearch") {
            $("#breadcrumbCon").html("<li><a href='${ctx}/teamspace'><span><spring:message code='teamSpace.title.teamSpace' /></span></a></li><li ><a href='#file/1/0'><span title='<c:out value="${teamName}"/>'><c:out value="${teamName}"/></span></a></li>");
            breadcrumbItem = $("<li></li><p class='pull-left'><spring:message code='file.tips.searchResult' arguments='"+ searchResultCount +"'/></p>");
            $("#breadcrumbCon").append(breadcrumbItem);
            return;
        }

        if (catalogParentId == "-1") {
            breadcrumbItem = $("<li><a href='${ctx}/teamspace'><span><spring:message code='teamSpace.title.teamSpace' /></span></a></li><li ><a href='${ctx}/teamspace/file/" + teamId + "/'><span title='<c:out value="${teamName}"/>'><c:out value="${teamName}"/></span></a></li>");
            $("#breadcrumbCon").html(breadcrumbItem);
            $("#breadcrumbCon").append("<li class=\"active\"><span><spring:message code='button.recycler'/></span></li>");
        } else {
            $("#breadcrumbCon").html("<li><a href='${ctx}/teamspace'><span><spring:message code='teamSpace.title.teamSpace' /></span></a></li><li ><a href='#file/1/0'><span title='<c:out value="${teamName}"/>'><c:out value='${teamName}'/></span></a></li>");
            var url = "${ctx}/teamspace/file/getPaths/" + teamId + "/" + catalogParentId;
            $.ajax({
                type: "GET",
                url: url,
                cache: false,
                async: false,
                timeout: 180000,
                success: function (data) {
                    if (data.length < 1) {
                    } else {
                        for (var i = 0; i < data.length; i++) {
                            if (i == data.length - 1) {
                                breadcrumbItem = $("<li class='active'><span title='" + data[i].name + "'>" + data[i].name + "</span></li>");
                                $("#breadcrumbCon").append(breadcrumbItem);
                            } else {
                                breadcrumbItem = $('<li><a href=\"#file/1/' + data[i].id + '\"><span title=\"' + data[i].name + '\">' + data[i].name + '</span></a></li>');
                                $("#breadcrumbCon").append(breadcrumbItem);
                            }
                        }
                    }

                    $("#breadcrumbCon span").tooltip({
                        container: "body",
                        placement: "top",
                        delay: {show: 100, hide: 0},
                        animation: false
                    });

                    var len = data.length + 2,
                        maxW = $("#breadcrumbCon").innerWidth();
                    breadcrumbAdapt(len, maxW);
                    $(window).bind("resize", function () {
                        maxW = $("#breadcrumbCon").innerWidth();
                        $("#breadcrumbCon").find("li.over-pass").remove();
                        $("#breadcrumbCon").find("li").show();
                        breadcrumbAdaptForTeamSpace(len, maxW);
                    });

                }
            })

        }
    }

    function breadcrumbAdaptForTeamSpace(len, maxW) {
        var objW = 0;
        for (var i = 0; i < len; i++) {
            objW += $("#breadcrumbCon").find("li").eq(i).outerWidth();
        }
        if (objW > maxW) {
            var _w = 0, j = 1;
            for (var i = 2; i < len - 1; i++) {
                _w += $("#breadcrumbCon").find("li").eq(i).width();
                if (_w > (objW - maxW + 45)) {
                    j = i;
                    for (var n = 2; n < j + 1; n++) {
                        $("#breadcrumbCon").find("li").eq(n).hide();
                    }
                    $("#breadcrumbCon").find("li").eq(1).after('<li class="over-pass"><span>...</span></li>');
                    break;
                }
            }
        }
    }

    function goFileIndex(nodeId) {
        window.location = "${ctx}/#file/1/" + nodeId;
    }

    function goTeamSpaceFileIndex(teamId, nodeId) {
        var url = "${ctx}/teamspace/file/" + teamId + "#file/1/" + nodeId;
        window.location = url;
    }

    function goTeamSpaceRecycler(teamId) {
        window.location = "${ctx}/teamspace/trash/" + teamId + "#1";
    }

    function sortByDate() {
        $("#sortOrderName").html("<spring:message code='common.field.date'/>");
        orderField = "modifiedAt";

        changeOrderDirect("dateSortLink");

        listData();

        setOrderToCookie();
    }

    function sortByFileName() {
        $("#sortOrderName").html("<spring:message code='common.field.name'/>");
        orderField = "name";

        changeOrderDirect("nameSortLink");

        listData();

        setOrderToCookie();
    }

    function sortBySize() {
        $("#sortOrderName").html("<spring:message code='common.field.size'/>");
        orderField = "size";

        changeOrderDirect("sizeSortLink");

        listData();

        setOrderToCookie();
    }

    function changeOrderDirect(elementId) {
        var attDesc = $("#" + elementId).attr("isDesc");
        if (attDesc != undefined && attDesc != "") {
            isDesc = attDesc;
        }
        if (null == isDesc || "" == isDesc || undefined == isDesc) {
            isDesc = true;
        } else {
            if (isDesc == true || isDesc == 'true') {
                isDesc = false;
            } else {
                isDesc = true;
            }
        }
        $("#sortUl i").remove();
        $("#" + elementId).append("<i/>");
        if (isDesc) {
            $("#" + elementId + " i, #sortArray").attr("class", "icon-arrow-down");
        } else {
            $("#" + elementId + " i, #sortArray").attr("class", "icon-arrow-up");
        }

        $("#sortUl a").attr("isDesc", "");
        $("#" + elementId).attr("isDesc", isDesc);
    }

    function setOrderToCookie() {
        setCookie("orderField", orderField);
        setCookie("isDesc", isDesc);
    }

    function listData() {
        if ("file" == viewMode) {
            listFile(currentPage, catalogParentId);
        } else if ("search" == viewMode) {
            doSearch();
        } else if ("recycler" == viewMode) {
            listRecycler(currentPage);
        }
    }

    function getRowData(dataId, dataList) {
        if (dataList == null || dataList.length == 0) {
            return null;
        }
        for (var i = 0; i < dataList.length; i++) {
            if (dataList[i].id == dataId) {
                return dataList[i];
            }
        }
        return null;
    }

    function addEnterEvent(elememnt, fun) {
        $("#" + elememnt).keydown(function () {
            var evt = arguments[0] || window.event;
            if (evt.keyCode == 13) {
                fun();
            }
        });
    }

    function downloadFileErrorHandler(request) {
        var responseObj = $.parseJSON(request.responseText);
        switch (responseObj.code) {
            case "NoSuchFile":
            case "NoSuchUser":
                handlePrompt("error", "<spring:message code='error.notfound'/>");
                break;
            case "Forbidden":
                handlePrompt("error", "<spring:message code='error.forbid'/>");
                break;
            case "FileScanning":
                handlePrompt("error", "<spring:message code='file.errorMsg.fileNotReady'/>");
                break;
            case "ScannedForbidden":
                handlePrompt("error", "<spring:message code='file.errorMsg.downloadNotAllowed'/>");
                break;
            case "SecurityMatrixForbidden":
                handlePrompt("error", "<spring:message code='error.forbid'/>");
                break;
            case "VirusForbidden":
                handlePrompt("error", "<spring:message code='file.errorMsg.operationNotAllowedForVirusDetected'/>");
                break;
            default:
                handlePrompt("error", "<spring:message code='file.errorMsg.getFileDownloadUrlFaild'/>");
                break;
        }
    }

    function doFileCommonError(request) {
        var responseObj = $.parseJSON(request.responseText);
        switch (responseObj.code) {
            case "BadRequest":
                handlePrompt("error", "<spring:message code='operation.failed'/>");
                break;
            case "NoSuchFile":
                handlePrompt("error", "<spring:message code='error.notfound'/>");
                break;
            case "ExceedFileMaxVersionNum":
                handlePrompt("error", "<spring:message code='file.errorMsg.exceedMaxVersions'/>");
                break;
            case "RepeatNameConflict":
                handlePrompt("error", "<spring:message code='file.errorMsg.nameConflict'/>");
                break;
            case "Forbidden":
            case "Unauthorized":
                handlePrompt("error", "<spring:message code='error.forbid'/>");
                break;
            case "SecurityMatrixForbidden":
                handlePrompt("error", "<spring:message code='error.forbid'/>");
                break;
            case "BusinessException":
            default:
                handlePrompt("error", "<spring:message code='operation.failed'/>");
        }
    }


    function isFolderType(type) {
        return 0 == type || -2 == type || -3 == type || 5 == type;
    }

    function isBackupFolderType(type) {
        return -2 == type || -3 == type;
    }

</script>