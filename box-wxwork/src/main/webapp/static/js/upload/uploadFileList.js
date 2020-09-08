$(function(){
    var uploadFiles;

    var s = localStorage.getItem("uploadFileList");
    if (s !== undefined && s !== null && s !== "") {
        uploadFiles = JSON.parse(s);
    } else {
        uploadFiles = [];
    }

	if(uploadFiles.length > 0) {
        var $list = $("#uploadFile");
        $list.children().remove();

        var $template = $("#uploadFileTemplate");
        for (var i = uploadFiles.length-1; i>= 0; i--) {
            var item = uploadFiles[i];
            item.imgClass = getImgHtml(1, item.name);
            item.size = formatFileSize(item.size);
			item.result = translate(item.state);
			item.time = getSmpFormatDate(item.lastModified);

            $template.template(item).appendTo($list);
        }
    }
});

function translate(state) {
	switch (state) {
		case 0:
			return "等待上传";
		case 1:
			return "正在上传";
		case 2:
			return "上传成功";
		case -1:
			return "上传失败";
		case -2:
			return "取消上传";
		default:
			return "未知错误";
	}
}

function clearUploadFiles() {
    localStorage.setItem("uploadFileList", "[]");
}