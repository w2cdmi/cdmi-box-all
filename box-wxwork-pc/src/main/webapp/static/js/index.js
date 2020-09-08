(function ($) {
    $.fn.extend({
        'ui.ToolBox': function () {
            var self = this
            var container = null

            /**
             * 加载部门空间列表
             */
            function loadDeptSpaceList() {
                var params = {
                    "type": 1,
                    "userId": curUserId,
                }
                $.ajax({
                    type: "POST",
                    url: host + "/ufm/api/v2/teamspaces/items",
                    data: JSON.stringify(params),
                    dataType: 'json',
                    error: function () {
                        $.Alert("查询部门空间出错", "cancel")
                    },
                    success: function (data) {
                        $.each(data.memberships, function (i, row) {
                            var item = $('<dt class="imagebox"><a href="javascript:void(0)"><i class="fa fa-sitemap"></i><span>' + row.teamspace.name + '</span></a></dt>')
                            item.data("row", row)
                            container.append(item)
                        })

                        container.find('dt').on('click', deptSpaceItem_Click)
                    }
                })
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
                loadDeptSpaceList()
                loadEnterpriseLibrary()
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
            var num = -1;
            var viewer = null
            function recentDownViewer() {
                var row = $(this).data('row')
                if(isImg(row.name)) {
                    var dataIndex = $(this).find("img").attr("data-index")
                    if (viewer !== null) {
                        viewer.destroy();
                    }
                    viewer = new Viewer(document.getElementById('datagrid'), {
                        url: 'data-original',
                        shown: function () {
                            viewer.view(dataIndex)
                        }
                    });
                    viewer.show()
                }else{
                    downloadFileByNodeIdAndOwnerId(row.ownedBy,row.id,this)
                }

            }
            function loadRecentFileList() {
                $.ajax({
                    type: "POST",
                    data: JSON.stringify({thumbnail: [{ width: 96, height: 96 }, { width: 250, height: 200 }]}),
                    url: host + "/ufm/api/v2/folders/"+ curUserId +"/recent",
                    error: function () {
                        $.Alert("获取最近浏览文件错误")
                    },
                    success: function (data) {
                        var listItem = null
                        var img
                        var files = data.files
                        if (files.length == 0) {
                            self.find('.notfind').show()
                            return
                        }

                        $.each(files, function (i, row) {
                            if(isImg(row.name)){
                                num++
                                var index = row.thumbnailUrlList[0].thumbnailUrl.lastIndexOf("/");
                                var imgSrc = row.thumbnailUrlList[0].thumbnailUrl.substring(0,index)
                                img = '<img data-original='+imgSrc+' data-index='+num+' src= '+ imgSrc +' alt='+row.name+' style="display: none"/>'
                            }else{
                                img = ''
                            }
                            if (row.menderName == undefined) {
                                listItem = $('<dt class="fileitem-temple1 cl" title="' + row.name + '">'
                                + img
                                + getFileIcon(row)
                                + '<span class="other-span-recent"><h3 class="txt-ellipsis recent-txt-ellipsis">' + row.name + '</h3>'
                                    +'<label>来源收件箱</label>'
                                +'</span>'
                                + '<u>' + getFormatDate(new Date(row.modifiedAt), 'yyyy-MM-dd hh:mm') + '</u>'
                                + '</dt>').data("row",row)
                            } else {
                                listItem = $('<dt class="fileitem-temple1 cl" title="' + row.name + '">'
                                + img
                                + getFileIcon(row)
                                + '<span class="other-span-recent"><h3 class="txt-ellipsis recent-txt-ellipsis">' + row.name + '</h3>'
                                    +'<label>' + row.menderName + '</label>'
                                +'</span>'
                                + '<u>' + getFormatDate(new Date(row.modifiedAt), 'yyyy-MM-dd hh:mm') + '</u>'
                                + '</dt>').data("row",row)
                            }
                            container.append(listItem)
                        })
                        self.find("dt").on("click",recentDownViewer)
                    }
                });
            }

            self.init = function () {
                container = self.find('.listbox')
            }

            self.load = function () {
                loadRecentFileList();
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
                        url: host + "/ufm/api/v2/folders/" + row.ownerId + "/shortcut/" + row.id ,
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