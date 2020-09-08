(function ($) {
    $.fn.extend({
        FileInfo: function () {
            var self = this
            var linkList = null
            var shareList = null
            var _loading = null

            function createShareList(data) {
                var listItem
                $.each(data, function (i, row) {
                    listItem = '<div class="cl">'
                        + '<div class="user-img">'
                        + '<img src="' + ctx + '/userimage/getUserImage/' + row.sharedUserId + '"/>'
                        + '</div>'
                        + '<div class="user-name">' + row.sharedUserName + '</div>'
                        + '<div class="user-role">' + getAclByRole(row.roleName) + '</div>'
                        + '</div>'
                    shareList.append(listItem)
                })
            }

            function createLinkList(data) {
                var listItem
                $.each(data, function (i, row) {
                    listItem = '<div>'
                        + '<p>' + row.url + '</p>'
                        + '<p>' + translateAccessMode(row) + '&nbsp;|&nbsp;' + translateRole(row) + '&nbsp;|&nbsp;' + translateExpireDate(row);
                    if (row.plainAccessCode != undefined) {
                        listItem += '&nbsp;&nbsp;<span>提取码：' + row.plainAccessCode + '</span>'
                    }
                    listItem += '</p></div>'
                    linkList.append(listItem)
                })

            }

            function translateRole(item) {
                console.log(item);
                var role = "";
                if (item.role == 'viewer') {
                    role = role + "下载,预览";
                }
                if (item.role == 'previewer') {
                    role = role + "预览";

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
                    if (days > 365) {
                        return (days / 365) + "年后过期"
                    } else if (days >= 30) {
                        return (days / 30) + "个月后过期"
                    } else {
                        return days + "天后过期";
                    }
                } else {
                    return "永久有效";
                }
            }

            function loadFileInfo(node) {
                self.find('#fileSize').empty()
                _loading = $.Tost('加载中...').show()
                var fileIconClass = null
                if (node.type <= 0) {
                    fileIconClass = node.shareStatus == 1 ? 'ico-sharefolder' : 'ico-folder'
                    self.find('#fileSize').text("文件大小：-" )
                    self.find('#fileUploadTime').text("创建时间：" + getFormatDate(node.createdAt))
                    self.find('#fileUploadPerson').text("创建者：" + node.menderName)
                } else {
                    fileIconClass = getFileIconClass(node.name)
                    self.find('#fileSize').text("文件大小：" + formatFileSize(node.size))
                    self.find('#fileUploadTime').text("上传时间：" + getFormatDate(node.createdAt))
                }
                self.find('#fileIco').attr("class", fileIconClass)
                self.find('#fileName').html(node.name)

                $.ajax({
                    type: "GET",
                    url:  host + '/ufm/api/v2/links/' + node.ownedBy + "/" + node.id,
                    error: function () {
                        _loading.hide()
                        $.Alert('获取文件外链失败');
                    },
                    success: function (data) {
                        linkList.empty()
                        if (data.links.length > 0) {
                            createLinkList(data.links);
                        }else{
                            linkList.html('<span style="color:#ccc;">暂无数据</span>')
                        }

                        $.ajax({
                            type: "GET",
                            url: host + '/ufm/api/v2/shareships/' + node.ownedBy + '/' + node.id + '?offset=0&limit=1000',
                            error: function () {
                                _loading.hide()
                                $.Alert('获取文件共享失败');
                            },
                            success: function (data) {
                                shareList.empty()
                                if (data.contents.length > 0) {
                                    createShareList(data.contents);
                                }else{
                                    shareList.html('<span style="color:#ccc;">暂无数据</span>')
                                }

                                _loading.hide()

                                if(self.onLoadSuccess){
                                    self.onLoadSuccess()
                                }
                            }
                        });

                    }
                });
            }

            self.init = function () {
                linkList = self.find('#linkList')
                shareList = self.find('#shareList')
            }

            self.load = function (node) {
                linkList.empty()
                shareList.empty()
                loadFileInfo(node)
            }

            return self
        }
    })
})(jQuery)