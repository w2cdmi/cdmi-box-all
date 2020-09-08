(function ($) {
    $.fn.extend({

        AddManagerList: function () {
            var self = this
            var _nodeId = null;
            var _success = null;
            function getRoleList(roleName,teamRole) {
                if (roleName == "previewer") {
                    return '预览'
                } else if (roleName == "uploadAndView") {
                    return '预览 | 上传 | 下载'
                } else if (roleName == "viewer") {
                    return '预览 | 下载'
                } else if (roleName == "uploader") {
                    return '预览 | 上传'
                }else if(roleName == "auther" && teamRole=="admin"){
                    return '拥有者'
                }else if(roleName == "auther" && teamRole=="manager"){
                    return '管理者'
                }
            }

            function createItem(row) {

                var url;
                if (row.member.type == 'user') {
                    url = ctx + '/userimage/getUserImage/' + row.member.id
                } else if (row.member.type == 'department') {
                    url = ctx + '/static/skins/default/img/department-icon.png'
                }
                return '<li id="' + row.userId + '">'
                    + '<img src="' + url + '"/>'
                    + '<span style="display: inline-block;width:240px;overflow: hidden;white-space: nowrap;text-overflow: ellipsis;line-height: 40px;">' + row.member.name + '</span>'
                    + '<span class="user-contacts-tools">'
                    + '</span>'
                    + '<span style="color:#ccc;" id="roleList">' + getRoleList(row.role,row.teamRole)  + '</span>'
                    + '</li>'
            }

            function loadSharedUserList() {
                $.ajax({
                    type: "POST",
                    url: host + "/ufm/api/v2/teamspaces/" + _nodeId + "/memberships/items",
                    error: function () {
                        $.Alert("查看成员失败")
                    },
                    data:JSON.stringify({
                        keyword:""
                    }),
                    success: function (data) {
                        self.empty()
                        var users = data.memberships
                        var listItem
                        $.each(users, function (i, row) {
                            listItem = $(createItem(row))
                            listItem.data('row', row)
                            self.append(listItem)
                        })
                        if (self.onLoadSuccess) {
                            self.onLoadSuccess(users)
                        }
                        _success()
                    }
                })
            }

            self.init = function () {

            }




            self.load = function (nodeId,success) {
                _nodeId = nodeId;
                _success = success;
                loadSharedUserList(nodeId,success)
            }

            return self
        }
    })
})(jQuery)