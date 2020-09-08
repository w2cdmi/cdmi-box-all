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
    function isValidFileName(obj) {
        var value = obj.val().trim();
        var pattern1 = /\.+$/;
        var pattern2 = /^.*(\\|\/).*$/;

        if (pattern1.test(value)) {
            return false;
        } else if (pattern2.test(value)) {
            return false;
        }
        return true;
    }

    function isNameNotExist(obj) {
        var ret = false;
        $.ajax({
            type: "GET",
            async: false,
            url: "${ctx}/nodes/checkSameName?" + new Date().toString(),
            data: obj.serialize(),
            success: function (data) {
                if (typeof(data) == 'string' && data.indexOf('<html>') != -1) {
                    window.top.location.href = "${ctx}/logout";
                    return;
                }
                ret = data;
            }
        });
        return !ret;
    }
    function isNameNotExist4Rename(obj) {
        var ret = false;
        $.ajax({
            type: "GET",
            async: false,
            url: "${ctx}/nodes/checkSameNameToRename?" + new Date().toString(),
            data: obj.serialize(),
            success: function (data) {
                if (typeof(data) == 'string' && data.indexOf('<html>') != -1) {
                    window.top.location.href = "${ctx}/logout";
                    return;
                }
                ret = data;
            }
        });
        return !ret;
    }
    function renameNode() {
        $("#fileList").renameNode(catalogData, opts_viewGrid,
            function (oldNameObjId, renameInputId) {
                var massageCon = $("#" + renameInputId).next().find("div");
                if (!isValidFileName($("#" + renameInputId))) {
                    massageCon.html("<span><span class=\"error\"><spring:message code='file.errorMsg.invalidName'/></span></span>");
                } else {
                    massageCon.html("");
                }

            },
            function (oldNameObjId, renameConId, renameInputId) {
                renameNodeSubmit(oldNameObjId, renameConId, renameInputId);
            });
    }

    function renameNodeSubmit(oldNameObjId, renameConId, renameInputId) {
        $("#" + renameInputId).val($("#" + renameInputId).val().trim());
        var massageCon = $("#" + renameInputId).next().find("div");
        var dataForm = $("#" + renameInputId).parent("form");
        if (!isValidFileName($("#" + renameInputId))) {
            $("#" + renameInputId).focus();
            massageCon.html("<span><span class=\"error\"><spring:message code='file.errorMsg.invalidName'/></span></span>");
            return;
        } else if ($("#" + renameInputId).val() == $("#" + oldNameObjId).text()) {
            $("#" + oldNameObjId).css("display", "block");
            $("#" + renameConId).remove();
            handlePrompt("error", "<spring:message code='file.errorMsg.nameNotChange'/>");
            return;
        } else if (!isNameNotExist4Rename(dataForm)) {
            $("#" + renameInputId).focus();
            massageCon.html("<span><span class=\"error\"><spring:message code='file.errorMsg.nameExist'/></span></span>");
            return;
        } else if ($("#" + renameInputId).val() == "") {
            $("#" + oldNameObjId).css("display", "block");
            $("#" + renameConId).remove();
            handlePrompt("error", "<spring:message code='file.errorMsg.nameRequired'/>");
            return;
        } else {
            new Spinner(optsSmallSpinner).spin($("#" + renameConId).get(0));
            $.ajax({
                type: "POST",
                url: "${ctx}/nodes/rename",
                data: dataForm.serialize() + "&token=${token}",
                error: function (request) {
                    var status = request.status;
                    if (status == 403) {
                        handlePrompt("error", "<spring:message code='error.forbid'/>");
                    } else if (status == 404) {
                        handlePrompt("error", "<spring:message code='operation.notfound'/>");
                        top.gotoShareMyFolderError(status);
                    } else {
                        handlePrompt("error", "<spring:message code='operation.failed'/>");
                    }

                },
                success: function () {
                    if (viewMode == "file") {
                        listFile(currentPage, catalogParentId);
                    } else {
                        doSearch();
                    }
                }
            });
        }
    };

    function createMyFolder() {
        var nodeNum = $("#breadcrumbCon li").length;
        if (nodeNum >= 128) {
            handlePrompt("error", "<spring:message code='error.folder.create.message'/>");
            return;
        }
        createFolder();
    }

    function createTeamspaceFolder() {
        var nodeNum = $("#breadcrumbCon li").length;
        if (nodeNum > 128) {
            handlePrompt("error", "<spring:message code='error.folder.create.message'/>");
            return;
        }
        createFolder();
    }

    function createFolder() {
        $("#fileListNull").hide();
        var val = "New Folder";

        $("body").prepend("<form id=\"validateNewnameForm\">" +
            "<input type=\"hidden\" name=\"ownerId\" value=" + ownerId + " />" +
            "<input type=\"hidden\" name=\"parentId\" value=" + catalogParentId + " />" +
            "<input type=\"hidden\" name=\"name\" value=\"" + val + "\" />" +
            "</form>");
        for (var i = 0; i < 1000; i++) {
            if (!isNameNotExist($("#validateNewnameForm"))) {
                val = "New Folder(" + (i + 2) + ")";
                $("#validateNewnameForm input[name=name]").val(val);
            } else {
                $("#validateNewnameForm").remove();
                break;
            }
        }

        $("#fileList").show().createFolder(ownerId, catalogParentId,
            function (createFolderInputId) {
                var massageCon = $("#" + createFolderInputId).next().find("div");
                if (!isValidFileName($("#" + createFolderInputId))) {
                    massageCon.html("<span><span class=\"error\"><spring:message code='file.errorMsg.invalidName'/></span></span>");
                } else {
                    massageCon.html("");
                }
            },
            function (createFolderConId, createFolderInputId) {
                createFolderSubmit(createFolderConId, createFolderInputId);
            }, val);
    }

    function createFolderSubmit(createFolderConId, createFolderInputId) {
        $("#" + createFolderInputId).val($("#" + createFolderInputId).val().trim());
        var massageCon = $("#" + createFolderInputId).next().find("div");
        var dataForm = $("#" + createFolderInputId).parent("form");

        if (!isValidFileName($("#" + createFolderInputId))) {
            $("#" + createFolderInputId).focus();
            massageCon.html("<span><span class=\"error\"><spring:message code='file.errorMsg.invalidName'/></span></span>");
            return;
        } else if (!isNameNotExist(dataForm)) {
            $("#" + createFolderInputId).focus();
            massageCon.html("<span><span class=\"error\"><spring:message code='file.errorMsg.nameExist'/></span></span>");
            return;
        } else if ($("#" + createFolderInputId).val() == "") {
            $("#" + createFolderConId).remove();
            handlePrompt("error", "<spring:message code='file.errorMsg.nameRequired'/>");
            listFile(1, catalogParentId);
        } else {
            new Spinner(optsSmallSpinner).spin($("#" + createFolderInputId).parent().get(0));
            $.ajax({
                type: "POST",
                url: "${ctx}/folders/create",
                data: dataForm.serialize() + "&token=${token}",
                error: function (request) {
                    var responseObj = $.parseJSON(request.responseText);
                    switch (responseObj.code) {
                        case "Forbidden":
                            handlePrompt("error", "<spring:message code='error.forbid'/>");
                            break;
                        case "ExceedUserMaxNodeNum":
                            handlePrompt("error", "<spring:message code='file.errorMsg.exceedNodeNum'/>");
                            break;
                        case "SecurityMatrixForbidden":
                            handlePrompt("error", "<spring:message code='error.forbid'/>");
                            break;
                        default:
                            handlePrompt("error", "<spring:message code='operation.failed'/>");
                    }
                    $("#" + createFolderConId).remove();
                },
                success: function () {
                    listFile(1, catalogParentId);
                }
            });
        }
    };


</script>