$(function () {
    //文件信息
    if(type==1){
    	$.ajax({
	        type: "GET",
            url: host + '/ufm/api/v2/files/' + ownerId + "/" + fileId,
	        error: function (request) {
	            $.toast('获取文件信息失败', 'forbidden');
	        },
	        success: function (data) {
	            fillFileInfo(data);
	        }
    	});
    }else{
    	$('#fileName').html(fileName);
    	$("#fileIcon").removeClass().addClass('folder-icon');
    }

    $.ajax({
        type: "GET",
        url: host + '/ufm/api/v2/folders/' + ownerId + "/" + fileId,
        error: function (request) {
            //$.toast('获取文件外链失败', 'forbidden');
        },
        success: function (data) {
            if (data != "") {
                createLinkItem(data);
            }
        }
    });
    
    $.ajax({
        type: "POST",
        url: host + '/ufm/api/v2/shareships/' + ownerId + '/' + fileId + '?offset=0&limit=1000',
        error: function (request) {
            //$.toast('获取文件共享失败', 'forbidden');
        },
        success: function (data) {
            if (data.content.length > 0) {
                createShareItem(data.content);
            }
        }
    });
});

function fillFileInfo(file) {
    $("#fileIcon").removeClass().addClass(getImgHtml(1, file.name));
    $("#fileName").text(file.name);
    $("#fileSize").text(formatFileSize(file.size));
}

function createShareItem(content) {
    var html = "<div class=\"the-outer-hair\">";
    	 html +=	"	<span>共享</span>";
    	 html +="</div>";
    html += '<div class="file-details-content">';
    html += '    <ul>';

    for (var i = 0; i < content.length; i++) {
        var item = content[i];
        html += '        <li><i><img src=\"'+ ctx +'/userimage/getUserImage/'+ item.sharedUserId +'\"/></i><p>' + item.sharedUserName + '</p><span>' + getAclByRole(item.roleName) + '</span></li>';
    }
    html += '    </ul>';
    html += '</div>';

    $("#shareListWrapper").append(html).show();
}

function createLinkItem(data) {
    //debugger;
    var html = '';

    html += '<div class="putting-links">';
    html += '    <ul>';
    html += '        <li><i>外发</i></li>';

    for (var i = 0; i < data.length; i++) {
        var item = data[i];

        html += '<div class="putting-link">';
        html += '    <div class="puttinglink-title">';
        html += '        <div style="width: 16.75rem;overflow: hidden;text-overflow: ellipsis;white-space: nowrap;">' + data[i].url + '</div>';
        html += '    </div>';

        html += '    <div class="puttinglink-one">';
        html += '        <div>' + translateAccessMode(item) + '<span>|</span></div>';
        html += '        <div>' + translateRole(item) + '<span>|</span></div>';
        html += '        <div>' + translateExpireDate(item) + '</div>';
        html += '    </div>';

        if (item.plainAccessCode != undefined) {
            html += '    <div class="puttinglink-two">';
            html += '        <div>提取码<span>' + item.plainAccessCode + '</span></div>';
            html += '    </div>';
        }
         html += '    </div>';
    }

    html += '    </ul>';
    html += '</div>';

    $("#linkListWrapper").append(html).show();
}

function translateRole(item) {
    var role = "";
    if (item.download == true) {
        role = role + "下载";
    }
    if (item.preview == true) {
        if (role == "") {
            role = role + "预览";
        } else {
            role = role + ",预览";
        }

    }

    return role;
}

function translateAccessMode(item) {
    if (item.accessCodeMode == "static") {
        if (item.plainAccessCode != undefined && item.plainAccessCode != "") {
            return "提取码访问";
        } else {
            return "匿名访问";
        }
    } else {
        return "动态码访问";
    }
}

function translateExpireDate(item) {
    if (item.expireAt != undefined && item.expireAt != "") {
        var days = Math.floor((item.expireAt - item.effectiveAt) / (24 * 3600 * 1000));
        if(days > 365) {
            return (days /365) + "年后过期"
        } else if(days >= 30) {
            return (days /30) + "个月后过期"
        } else {
            return days + "天后过期";
        }
    } else {
        return "永久有效";
    }
}
