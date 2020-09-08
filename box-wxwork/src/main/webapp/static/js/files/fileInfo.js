
$(function () {
    //文件信息
    getNodeInfo();

    //获取外链信息
    getNodeLinkList();

    //获取共享信息
    getNodeShareList();
});

function getNodeInfo() {
    if (type == 1) {
        $.ajax({
            type: "GET",
            url: host + '/ufm/api/v2/files/' + ownerId + "/" + fileId,

            error: function (xhr, status, error) {
                $.toast('获取文件信息失败', 'forbidden');
            },
            success: function (data) {
                fillFileInfo(data);
            }
        });
    }else{
        $.ajax({
            type: "GET",
            url: host + '/ufm/api/v2/folders/' + ownerId + "/" + fileId,

            error: function (xhr, status, error) {
                $.toast('获取文件夹信息失败', 'forbidden');
            },
            success: function (data) {
            	console.log(data)
                if(data.size==undefined){
                    $("#fileSize").prev('span').html("");
                }

                $('#fileName').html(fileName);
                $("#fileIcon").removeClass().addClass('folder-icon');
                $("#file-createTime").text(getFormatDate(data.createdAt));
                // $("#file-createByName").text(data.creator);
            }
        });

    }
}

function getNodeLinkList() {
    $.ajax({
        type: "GET",
        url: host + '/ufm/api/v2/links/' + ownerId + "/" + fileId,
        error: function (xhr, status, error) {
        },
        success: function (data) {
            if (data.links != "") {
                createLinkItem(data.links);
            }else{
                createNotLinkItem()
            }
        }
    });
}

function getNodeShareList() {
    //只有个人空间中才需要查询共享信息， 此时ownerId == curUserId
    if(ownerId == curUserId) {
        $.ajax({
            type: "GET",
            url: host + '/ufm/api/v2/shareships/' + ownerId + '/' + fileId + '?offset=0&limit=1000',
            error: function (xhr, status, error) {
                //$.toast('获取文件共享失败', 'forbidden');
            },
            success: function (data) {
                if (data.contents.length > 0) {
                    createShareItem(data.contents);
                }
                if (data.contents.length == 0) {
                    createNotShareItem();
                }
            }
        });
    }
}

function fillFileInfo(file) {
    $("#fileIcon").removeClass().addClass(getImgHtml(1, file.name));
    $("#fileName").text(file.name);
    $("#fileSize").text(formatFileSize(file.size));
    $("#file-createTime").text(getFormatDate(file.createdAt));
    // $("#file-createByName").text(file.createdByName);
}

function createShareItem(content) {
    var html = "<div class=\"the-outer-hair\">";
    	 html +=	"	<span>共享</span>";
    	 html +="</div>";
    html += '<div class="file-details-content">';
    html += '    <ul>';

    for (var i = 0; i < content.length; i++) {
        var item = content[i];
        var img="";
        if(item.sharedUserType == "0"){
            img = '<img src=\"'+ ctx +'/userimage/getUserImage/'+ item.sharedUserId +'\"/>'
        }else if(item.sharedUserType == "2"){
            img = '<img src=\"'+ ctx +'/static/skins/default/img/department-icon.png\"/>'
        }
        html += '        <li><i>'+ img +'</i><p>' + item.sharedUserName + '</p><span>' + getAclByRole(item.roleName) + '</span></li>';
    }
    html += '    </ul>';
    html += '</div>';

    $("#shareListWrapper").append(html).show();
}
function createNotShareItem() {
    var html = "<div class=\"the-outer-hair\">";
    html +=	"	<span>共享</span>";
    html +="</div>";
    html += '<div class="file-details-content">';
    html += '    <ul>';
    html += '        <li><img class="not-share-img" src=\"'+ ctx +'/static/skins/default/img/not-share-person.png' +'\"/><span class="not-share-person">暂无共享成员</span></li>';

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
        html += '        <div>' + getAclByRole(item.role) + '<span>|</span></div>';
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
function createNotLinkItem() {
    var html = '';

    html += '<div class="putting-links">';
    html += '    <ul>';
    html += '        <li><i>外发</i></li>';
    html += '<div class="putting-link">';
    html += '        <li><img class="not-link-img" src=\"'+ ctx +'/static/skins/default/img/notQulickContent.png' +'\"/><span class="not-links">暂无外发链接</span></li>';
    html += '    </ul>';
    html += '</div>';

    $("#linkListWrapper").append(html).show();
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
