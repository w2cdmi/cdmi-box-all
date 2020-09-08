(function ($) {
    $.fn.extend({
        'ui.ToolBox': function () {
            var self = this
            var container = null

            /**
             * 加载部门空间列表
             */
            function loadDeptSpaceList() {
                gotoPage(ctx + '/folder?rootNode=0')
                // var params = {
                //     "type": 1,
                //     "userId": curUserId,
                // }
                // $.ajax({
                //     type: "POST",
                //     url: host + "/ufm/api/v2/teamspaces/items",
                //     data: JSON.stringify(params),
                //     dataType: 'json',
                //     error: function () {
                //         $.Alert("查询部门空间出错", "cancel")
                //     },
                //     success: function (data) {
                //         $.each(data.memberships, function (i, row) {
                //             var item = $('<dt class="imagebox"><a href="javascript:void(0)"><i class="fa fa-sitemap"></i><span>' + row.teamspace.name + '</span></a></dt>')
                //             item.data("row", row)
                //             container.append(item)
                //         })

                //         container.find('dt').on('click', deptSpaceItem_Click)
                //     }
                // })
            }
            /**
             * 直接进去个人目录
             */
            function gotopersonal() {
                gotoPage(ctx + '/folder?rootNode=0')
            }
            /**
             * 加载企业文库
             */
            function loadEnterpriseLibrary() {
                var params = {
                    "type": 4,
                    "userId": curUserId,
                }
                $.ajax({
                    type: "POST",
                    url: host + "/ufm/api/v2/teamspaces/items",
                    data: JSON.stringify(params),
                    dataType: 'json',
                    error: function () {
                        $.Alert("查询企业文库出错", "cancel") 
                    },
                    success: function (data) {
                        $.each(data.memberships, function (i, row) {
                            var item = $('<dt class="imagebox"><a href="javascript:void(0)"><i class="fa fa fa-suitcase"></i><span>' + row.teamspace.name + '</span></a></dt>')
                            item.data("row", row)
                            container.append(item)
                        })

                        container.find('dt').on('click', enterpriseLibrary_Click)
                    }
                })
            }
            /**
             * 部门空间列表点击事件
             * @param e
             */
            function deptSpaceItem_Click(e) {
                var row = $(this).data("row")
                //如果row为空就个人文件
                if (row) {
                    gotoPage(ctx + '/teamspace/file/' + row.teamspace.id)
                } else {
                    gotoPage(ctx + '/folder?rootNode=0')
                }
            }
            /**
             * 企业文库列表点击事件
             * @param e
             */
            function enterpriseLibrary_Click(e) {
                var row = $(this).data("row")
                //如果row为空就个人文件
                if (row) {
                    gotoPage(ctx + '/teamspace/file/' + row.teamspace.id)
                } else {
                    gotoPage(ctx + '/folder?rootNode=0')
                }
            }
            /**
             * 初始化
             */
            self.init = function () {
                self.append('<dl class="left"><dt class="imagebox"><a href="javascript:void(0)"><i class="fa fa-user-o"></i><span>个人文件</span></a></dt></dl>')
                container = self.find('.left')
            }

            /**
             * 加载数据
             */
            self.load = function () {
                gotopersonal()
            }

            return self
        }
    })

    /**
     * 最近浏览文件
     */
    $.fn.extend({
        'ui.RecentFileList': function () {
            var self = this
            var container = null

            self.init = function () {
                container = self.find('.listbox')
            }

            self.load = function () {
            }
            ;
            return self
        }
    })

    /**
     * 快捷目录
     */
    $.fn.extend({
        'ui.QuickDirList': function () {
            var self = this
            var container = null
            var _loading = null
            /**
             * 删除快捷目录
             */
            function times_Click(e) {
                e.stopPropagation()
                var row = $(this).parents('dt').data('row');
                $.Confirm('确认移除快捷目录吗？', function(){
                    _loading = $.Tost('正在移除快捷目录...').show()
                    $.ajax({
                        type: "DELETE",
                        url: host + "/ufm/api/v2/folders/" + row.ownerId + "/recent/delete/" + row.id ,
                        error: function(){
                            _loading.hide()
                            $.Alert('移除快捷目录失败')
                        },
                        success: function () {
                            _loading.hide()
                            loadQuickDirList()
                        }
                    });
                })
            }

            function loadQuickDirList() {
                container.empty()
                $.ajax({
                    type: "post",
                    data: "{}",
                    url: host + "/ufm/api/v2/folders/"+ curUserId +"/shortcut/list",
                    error: function () {
                        $.Alert("获取快捷目录错误")
                    },
                    success: function (data) {
                        var listItem = null
                        if (data.length == 0) {
                            self.find('.notfind').show()
                            return
                        }
                        var url
                        $.each(data, function (i, row) {
                            if (row.type == 1) {
                                url = ctx + '/folder?rootNode=' + row.nodeId
                            } else {
                                url = ctx + '/teamspace/file/' + row.ownerId + '?parentId=' + row.nodeId
                            }
                            listItem = $('<dt class="fileitem-temple1 smaill cl" style="width: 200px;position: relative;" title="' + row.nodeName + '" onclick="gotoPage(\'' + url + '\')">'
                                + '<i class="ico-folder"></i>'
                                + '<span><h3 class="txt-ellipsis" style="width:120px;">' + row.nodeName + '</h3><label>' + (row.type == 1 ? '个人文件' : row.ownerName) + '</label></span>'
                                + '<i class="fa fa-times" title="点击删除快捷目录"></i>'
                                + '</dt>')
                            listItem.data('row', row)
                            container.append(listItem)
                        })
                        self.find('i.fa-times').on('click', times_Click)
                    }
                });
            }

            self.init = function () {
                container = self.find('dl')
            }

            self.load = function () {
                loadQuickDirList();
            }

            return self
        }
    })

    $(document).ready(function () {
        var container = $('.container')

        var toolbox = container.find('#toolbox')['ui.ToolBox']()
        toolbox.init()
        toolbox.load()

        var quickDirList = container.find('#quickDirList')['ui.QuickDirList']()
        quickDirList.init()
        quickDirList.load()

        var recentFileList = container.find('#recentFileList')['ui.RecentFileList']()
        recentFileList.init()
        recentFileList.load()

    })
})(jQuery)