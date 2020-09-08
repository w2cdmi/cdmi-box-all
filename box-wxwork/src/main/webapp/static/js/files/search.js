$(function () {
    var types = parseQueryString();
    console.log(types.type)
    if (types.type == -1) {
        $("#searchFileInput").attr('placeholder', "搜索个人文件");
        hideChooseDom()
    } else if (types.type == 0) {
        $("#searchFileInput").attr('placeholder', "搜索协作空间");
        hideChooseDom()
    } else if (types.type == 1) {
        $("#searchFileInput").attr('placeholder', "搜索部门空间");
        hideChooseDom()
    } else if (types.type == 4) {
        $("#searchFileInput").attr('placeholder', "搜索企业文库");
        hideChooseDom()
    }

    searchInit(types.ownerId)
});

// 隐藏指定选择的dom
function hideChooseDom() {
    $("#searchChooseFile").hide()
}
// 显示指定选择的dom
function showChooseDom() {
    $("#searchChooseFile").show()
}
function searchInit(ownerId) {
    $("#searchFileInput").on('keypress',function(e) {
        var keycode = e.keyCode;
        var keyword = $("#searchFileInput").val();
        if(keycode=='13' && keyword !== "" && keyword.trim() != "") {
            e.preventDefault();
            //请求搜索接口
            doSearch(keyword.trim(), ownerId);
        }
    });
}
