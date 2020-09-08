(function($){
    $.fn.extend({
        FileVersions: function()
        {
            var self = this
            var fileVersionList = null
            var _node
            var _pageNumber = 1;
            var _pageSize = 100;

            function versionDelete_Click()
            {
                var row = $(this).parent().parent().data('row')
                deleteFile(row.ownedBy, row.id, function(){
                    loadFileVersions(_node)
                })
            }

            function versionRestore_Click()
            {
                var row = $(this).parent().parent().data('row')
                restoreVersion(row.ownedBy, row.id, function(){
                    loadFileVersions(_node)
                })

            }

            function loadFileVersions(node){
                fileVersionList.empty()
                var fileIconClass = null
                if (node.type <= 0) {
                    fileIconClass = node.shareStatus == 1 ? 'ico-sharefolder' : 'ico-folder'
                } else {
                    fileIconClass = getFileIconClass(node.name)
                    self.find('#fileSize').text("文件大小：" + formatFileSize(node.size))
                }
                self.find('#fileIco').attr("class", fileIconClass)
                self.find('#fileName').html(node.name)

                $.ajax({
                    type: "GET",
                    url: host + "/ufm/api/v2/files/" + node.ownedBy + "/" + node.id + "/versions?offset=" + ((_pageNumber - 1) * _pageSize) + "&limit=" + _pageSize,
                    success: function (data) {
                        var listItem
                        var len = data.content.length
                        if(len == 0){
                            fileVersionList.html('<span style="color:#ccc;">暂无数据</span>')
                            return
                        }
                        $.each(data.content, function(i, row){
                            listItem = '<div class="cl">'
                            + '<div class="user-name"><span>V'+(len - i)+'</span>&nbsp;&nbsp;<span>'+getFormatDate(new Date(row.modifiedAt))+'</span></div>'
                            + '<div class="user-role"><a href="javascript:void(0);" onclick="downloadFileByNodeId(' + row.ownedBy + ',' + row.id + ')">下载</a>';
                            if(i != 0){
                                listItem += '&nbsp;|&nbsp;<a href="javascript:void(0);" id="version_delete">删除</a>&nbsp;|&nbsp;<a href="javascript:void(0);" id="version_restore">恢复</a>'
                            }
                            listItem += '</div></div>'
                            listItem = $(listItem)
                            listItem.data('row', row)
                            fileVersionList.append(listItem)
                        })
                        fileVersionList.find("a#version_delete").on('click',versionDelete_Click)
                        fileVersionList.find("a#version_restore").on('click',versionRestore_Click)
                    },error: function(){
                        $.Alert('获取版本文件失败');
                    }
                });
            }


            self.init = function()
            {
                fileVersionList = self.find('#fileVersionList')
            }

            self.load = function(node)
            {
                _node = node
                fileVersionList.empty()
                loadFileVersions(node)
            }

            return self
        }
    })
})(jQuery)