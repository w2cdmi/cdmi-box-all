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

    function zTreeOnClick(event, treeId, treeNode) {
        var treeObj = $.fn.zTree.getZTreeObj(treeId);
        treeObj.expandNode(treeNode, true, false, false);
    }

    function zTreeBeforeRename(treeId, treeNode, newName, isCancel) {
        var treeObj = $.fn.zTree.getZTreeObj(treeId);
        var curNodes = treeObj.getSelectedNodes();
        var parentNode = curNodes[0].getParentNode();
        var vHtml = '<form id="newFolderForm" style="display:inline;">' +
            '<input type="hidden" id="ownerId" name="ownerId" value="' + parentNode.ownedBy + '" />' +
            '<input type="hidden" id="parentId" name="parentId" value="' + parentNode.id + '" />' +
            '<input type="hidden" id="name" name="name" value="' + newName + '" />' +
            '</form>';
        var _objInput = $("#" + curNodes[0].tId + "_input");
        if (!$("#newFolderForm").get(0)) {
            _objInput.next().after(vHtml);
        }
        newName = _objInput.val().trim();
        _objInput.val(newName);
        $("#name").val(newName);

        var massageCon = _objInput.next().find("div");
        var dataForm = $("#newFolderForm");
        if (!isValidFileName(_objInput)) {
            _objInput.focus().select();
            massageCon.html("<span><span class=\"error\"><spring:message code='file.errorMsg.invalidName'/></span></span>");
            return false;
        } else if (!isNameNotExist(dataForm)) {
            _objInput.focus().select();
            massageCon.html("<span><span class=\"error\"><spring:message code='file.errorMsg.nameExist'/></span></span>");
            return false;
        } else if (newName == "") {
            handlePrompt("error", "<spring:message code='file.errorMsg.nameRequired'/>");
            treeObj.removeNode(curNodes[0]);
            $("#" + parentNode.tId + "_ico").removeClass("ico_docu").addClass("tree-all-folder_ico_open");
            $("#" + parentNode.tId + "_switch").removeClass("center_docu").addClass("center_open");
            treeObj.selectNode(parentNode);
            setTimeout(function () {
                enableModalbtn();
            }, 500);
            return false;
        } else {
            return true;
        }
    }
    function zTreeOnRename(event, treeId, treeNode, isCancel) {
        var treeObj = $.fn.zTree.getZTreeObj(treeId);
        var curNodes = treeObj.getSelectedNodes();
        var parentNode = curNodes[0].getParentNode();

        $.ajax({
            type: "POST",
            url: "${ctx}/folders/create",
            data: $("#newFolderForm").serialize() + "&token=${token}",
            error: function (request) {
                var status = request.status;
                if (status == 403) {
                    handlePrompt("error", "<spring:message code='error.forbid'/>");
                } else {
                    handlePrompt("error", "<spring:message code='operation.failed'/>");
                }

                treeObj.removeNode(curNodes[0]);
                $("#" + parentNode.tId + "_ico").removeClass("ico_docu").addClass("tree-all-folder_ico_open");
                $("#" + parentNode.tId + "_switch").removeClass("center_docu").addClass("center_open");
            },
            success: function (data) {
                curNodes[0].id = data.id;
                curNodes[0].ownedBy = data.ownedBy;
                setTimeout(function () {
                    enableModalbtn();
                }, 500);

                $("#" + curNodes[0].tId + "_span").text(data.name);
                $("#" + curNodes[0].tId + "_a").attr("title", data.name);
            }
        });
    }

    function createFolder() {
        disableModalbtn();
        var treeObj = $.fn.zTree.getZTreeObj("treeArea");
        var parentNodes = treeObj.getSelectedNodes();
        var treeNode = parentNodes[0];
        var newName = "New Folder";
        $("body").prepend("<form id=\"validateNewnameForm\">" +
            "<input type=\"hidden\" name=\"ownerId\" value=" + treeNode.ownedBy + " />" +
            "<input type=\"hidden\" name=\"parentId\" value=" + treeNode.id + " />" +
            "<input type=\"hidden\" name=\"name\" value=\"" + newName + "\" />" +
            "</form>");
        for (var i = 0; i < 1000; i++) {
            if (!isNameNotExist($("#validateNewnameForm"))) {
                newName = "New Folder(" + (i + 2) + ")";
                $("#validateNewnameForm input[name=name]").val(newName);
            } else {
                $("#validateNewnameForm").remove();
                break;
            }
        }

        var newNode = {name: newName, iconSkin: "tree-all-folder"};
        newNode = treeObj.addNodes(treeNode, newNode);
        treeObj.editName(newNode[0]);

        var _objInput = $("#" + newNode[0].tId + "_input");
        _objInput.attr("maxlength", "246").select()
            .after("<span class=\"validate-con tree-validate-con\"><div></div></span>")
            .keyup(function () {
                if (event.keyCode != 13) {
                    var massageCon = _objInput.next().find("div");
                    if (!isValidFileName(_objInput)) {
                        massageCon.html("<span><span class=\"error\"><spring:message code='file.errorMsg.invalidName'/></span></span>");
                    } else {
                        massageCon.html("");
                    }
                }
            });
    }

    function enableModalbtn() {
        var btns = "#ymNewFolder";
        if (top.$("#btnCopy").get(0)) {
            btns += ",#btnCopy";
        }
        if (top.$("#btnMove").get(0)) {
            btns += ",#btnMove";
        }
        if (top.$("#btn-focus").get(0)) {
            btns += ",#btn-focus";
        }
        if ($("#copyButton").get(0)) {
            ymPrompt_enableModalbtn("#copyButton, #ymNewFolder, #goReturn");
        }

        top.ymPrompt_enableModalbtn(btns);
    }
    function disableModalbtn() {
        var btns = "#ymNewFolder";
        if (top.$("#btnCopy").get(0)) {
            btns += ",#btnCopy";
        }
        if (top.$("#btnMove").get(0)) {
            btns += ",#btnMove";
        }
        if (top.$("#btn-focus").get(0)) {
            btns += ",#btn-focus";
        }
        if ($("#copyButton").get(0)) {
            ymPrompt_disableModalbtn("#copyButton, #ymNewFolder, #goReturn");
        }

        top.ymPrompt_disableModalbtn(btns);
    }

</script>