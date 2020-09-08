(function ($) {
    $.fn.extend({
        FileInfo: function () {
            var self = this
            var linkList = null
            var shareList = null
            var _loading = null

            // function createShareList(data) {
            //     var listItem
            //     $.each(data, function (i, row) {
            //         listItem = '<div class="cl">'
            //             + '<div class="user-img">'
            //             + '<img src="' + ctx + '/userimage/getUserImage/' + row.sharedUserId + '"/>'
            //             + '</div>'
            //             + '<div class="user-name">' + row.sharedUserName + '</div>'
            //             + '<div class="user-role">' + getAclByRole(row.roleName) + '</div>'
            //             + '</div>'
            //         shareList.append(listItem)
            //     })
            // }

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
                _loading = $.Tost('加载中...').show()
                var fileIconClass = null
                if (node.type <= 0) {
                    fileIconClass = node.shareStatus == 1 ? 'ico-sharefolder' : 'ico-folder'
                    self.find('#fileSize').text("文件大小：-" )
                    self.find('#fileUploadTime').text("创建时间：" + getFormatDate(node.createdAt))
                    self.find('#fileUploadPerson').text("创建者：" + node.createdByName)
                } else {
                    fileIconClass = getFileIconClass(node.name)
                    self.find('#fileSize').text("文件大小：" + formatFileSize(node.size))
                    self.find('#fileUploadTime').text("上传时间：" + getFormatDate(node.createdAt))
                }
                self.find('#fileIco').attr("class", fileIconClass)
                self.find('#fileName').html(node.name)

                $.ajax({
                    type: "GET",
                    url: host + '/ufm/api/v2/links/' + node.ownedBy + "/" + node.id,
                    error: function () {
                        _loading.hide()
                        $.Alert('获取文件外链失败');
                    },
                    success: function (data) {
                        linkList.empty()
                        if (data.length > 0) {
                            createLinkList(data);
                        }else{
                            linkList.html('<span style="color:#ccc;">暂无数据</span>')
                        }
                        _loading.hide()

                        if(self.onLoadSuccess){
                            self.onLoadSuccess()
                        }
                        // $.ajax({
                        //     type: "POST",
                        //     url: ctx + '/share/listSharedUser',
                        //     data: {
                        //         iNodeId: node.id,
                        //         pageNumber: 0,
                        //         token: token
                        //     },
                        //     error: function () {
                        //         _loading.hide()
                        //         // $.Alert('获取文件共享失败');
                        //     },
                        //     success: function (data) {
                        //         shareList.empty()
                        //         if (data.content.length > 0) {
                        //             createShareList(data.content);
                        //         }else{
                        //             shareList.html('<span style="color:#ccc;">暂无数据</span>')
                        //         }
                        //
                        //         _loading.hide()
                        //
                        //         if(self.onLoadSuccess){
                        //             self.onLoadSuccess()
                        //         }
                        //     }
                        // });

                    }
                });
            }

            self.init = function () {
                linkList = self.find('#linkList')
                // shareList = self.find('#shareList')
            }

            self.load = function (node) {
                linkList.empty()
                // shareList.empty()
                loadFileInfo(node)
            }

            return self
        }
    })
})(jQuery)