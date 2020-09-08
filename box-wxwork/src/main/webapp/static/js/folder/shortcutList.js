$(function () {
    // listShortcutFolder()
});
// 删除快捷目录
function deleteSortcutFolder(node) {
    $.confirm("确认移除快捷目录?", function() {
        $.ajax({
            type: "DELETE",
            url:  host + "/ufm/api/v2/folders/" + node.ownerId + "/shortcut/" + node.id,
            error: function (xhr, status, error) {
                //$.toast('操作失败', 'forbidden');
            },
            success: function (data) {
                $.toast('删除成功');
                listShortcutFolder()
            }
        });
    })
}