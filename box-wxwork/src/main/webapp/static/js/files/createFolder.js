$(function () {
    $("#newFolderNameInput").focus()
    $("#cancelButton").hide()
    $(".weui-navbar+.weui-tab__bd").css({
        "paddingBottom":"3.2rem"
    })
    var folderChooser = $("#folderChooserDialog").FolderChooser({
            callback: function (ownerId, folderId) {
                var newName = $("#newFolderNameInput").val();

                if(newName === undefined || newName === null || newName === "") {
                    $.alert("文件夹不能为空。");
                    return;
                }

                var regEn = /[`~!@#$%^&*()_+<>?:"{},.\/;'[\]]/im;
                var regCn = /[·！#￥（——）：；“”‘、，|《。》？、【】[\]]/im;
                var lastname = newName.charAt(newName.length-1);
                var firstname = newName.charAt(0);
                if(regEn.test(lastname) || regCn.test(lastname)) {
                    $.alert("最后一个字符不能以特殊符号结束");
                    return;
                } else if(regEn.test(firstname) || regCn.test(firstname)) {
                    $.alert("第一个字符不能以特殊符号开头");
                    return;
                }

                var parameters = {
                    parent: folderId,
                    name: newName
                };

                $.ajax({
                    type: "POST",
                    url: host + "/ufm/api/v2/folders/"+ownerId,
                    data: JSON.stringify(parameters),
                    error: function (xhr, status, error) {
                        var responseObj = $.parseJSON(xhr.responseText);
                        switch (responseObj.code) {
                            case "Forbidden" || "SecurityMatrixForbidden":
                                $.alert("您没有权限进行该操作！");
                                break;
                            case "ExceedUserMaxNodeNum":
                                $.toast("文件总数超过限制", "cancel");
                                break;
                            case "RepeatNameConflict":
                                $.toast("存在相同文件名","cancel");
                                break;
                            default:
                                $.toast("操作失败", "cancel");
                        }
                    },
                    success: function () {
                        goBack();
                    }
                });
            }
        }
    );

    folderChooser.showDialog();
});