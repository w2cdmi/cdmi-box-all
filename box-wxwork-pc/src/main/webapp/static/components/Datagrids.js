(function ($) {
    $.fn.extend({
        Datagrid: function () {
            var self = this
            var tbody = null
            var notfind = null
            var _loading = null
            var _defaultItem = null
            var _ownerId
            var _folderId
            var _pageNumber
            var _pageSize
            var _orderField
            var _order
            var _name
            var num = -1;
            var viewer = null;
            var _isSearch = false

            function loadFiles() {
                _loading = $.Tost('数据加载中...').show()
                var permission = getNodePermission(_ownerId,_folderId,curUserId);
                console.log(_ownerId);
                var p = 0;
                for (var i in permission) {
                    p = p + permission[i];
                }
                if (p == 0) {
                    _loading.hide()
                    $.Alert("您没有权限进行该操作")
                    return;
                }

                var url = host + "/ufm/api/v2/folders/" + sharedownerId + "/" + _folderId + "/items";
                var params = {
                    offset: (_pageNumber - 1) * _pageSize,
                    limit: _pageSize,
                    order: [{ field: 'type' , direction: "ASC" },{ field: _orderField, direction: _order }],
                    thumbnail: [{ width: 96, height: 96 }, { width: 250, height: 200 }]
                };

                if (permission != null && permission["browse"] == 1) {
                    $.ajax({
                        type: "POST",
                        url: url,
                        data: JSON.stringify(params),
                        error: function(){
                            _loading.hide()
                        },
                        success: function (data) {
                            var files = data.folders.concat(data.files);
                            var listItem
                            var img
                            var fileIconClass

                            tbody.empty()
                            self.find('.notfind').hide()
                            if(files.length == 0){
                                _loading.hide()
                                self.find('.notfind').show()
                            }

                            $.each(files, function (i, row) {
                                if(row.type <= 0){
                                    fileIconClass = row.shareStatus == 1 ? 'ico-sharefolder' : 'ico-folder'
                                }else{
                                    fileIconClass = getFileIconClass(row.name)
                                }
                                if(isImg(row.name)){
                                    num++
                                    var index = row.thumbnailUrlList[0].thumbnailUrl.lastIndexOf("/");
                                    var imgSrc = row.thumbnailUrlList[0].thumbnailUrl.substring(0,index)
                                    img = '<img data-original='+imgSrc+' data-index='+num+' src= '+ imgSrc +' alt='+row.name+' style="display: none"/>'
                                }else{
                                    img = ''
                                }
                                listItem = '<tr title="' + row.name + '">'
                                    + '<td style="min-width: 280px">'
                                    +   '<div class="fileitem-temple1 cl">'
                                    + img
                                    +       getFileIcon(row)
                                    +       '<span style="line-height: 35px;"><h3 class="txt-ellipsis">'+row.name+'</h3></span>'
                                    +       '<u style="margin-top: 5px"><a id="worker" data="' + row.type + '"><i class="fa fa-ellipsis-h"></i></a></u>'
                                    +   '</div>'
                                    + '</td>'
                                    + '<td style="width: 100px">' + (row.size ? formatFileSize(row.size) : '-') + '</td>'
                                    + '<td style="width: 180px">' + getFormatDate(new Date(row.modifiedAt), "yyyy-MM-dd hh:mm") + '</td>'
                                    + '</tr>';
                                listItem = $(listItem)
                                listItem.data('row', row)
                                tbody.append(listItem)
                            });
                            num = -1;
                            tbody.find('tr').on('click', rowItem_Click)
                            _loading.hide()
                            if(self.onLoadSuccess){
                                self.onLoadSuccess(data)
                            }
                        }

                    })
                } else {
                    _loading.hide()
                    $.Alert("您没有权限进行该操作")
                }
            }

            function searchFiles() {
                _loading = $.Tost('加载中...').show()
                var url = host + "/ufm/api/v2/nodes/" + _ownerId + "/search";
                var params = {
                    name: _name,
                    order: [{field: 'type', direction: 'ASC'}, {field: _orderField, direction: _order}],
                    thumbnail: [{ width: 96, height: 96 }]
                };

                $.ajax({
                    type: "POST",
                    url: url,
                    data: JSON.stringify(params),
                    error: function(){
                        _loading.hide()
                    },
                    success: function (data) {
                        var files = data.folders.concat(data.files);
                        var listItem
                        var fileIconClass

                        tbody.empty()
                        notfind.hide()
                        if(data.length == 0){
                            _loading.hide()
                            notfind.show()
                            return
                        }

                        $.each(files, function (i, row) {
                            if(row.type <= 0){
                                fileIconClass = row.shareStatus == 1 ? 'ico-sharefolder' : 'ico-folder'
                            }else{
                                fileIconClass = getFileIconClass(row.name)
                            }
                            listItem = '<tr title="' + row.name + '">'
                                + '<td style="min-width: 280px">'
                                +   '<div class="fileitem-temple1 cl">'
                                +       '<i class="'+fileIconClass+'"></i>'
                                +       '<span><h3 class="txt-ellipsis">' + row.name + '</h3><label>' + _defaultItem.name + row.path + '</label></span>'
                                +   '</div>'
                                + '</td>'
                                + '<td style="width: 100px">' + (row.size ? formatFileSize(row.size) : '-') + '</td>'
                                + '<td style="width: 180px">' + getFormatDate(new Date(row.modifiedAt), "yyyy-MM-dd hh:mm") + '</td>'
                                + '</tr>';
                            listItem = $(listItem)
                            listItem.data('row', row)
                            tbody.append(listItem)
                        })

                        tbody.find('tr').on('click', rowItem_Click)
                        _loading.hide()
                        if(self.onLoadSuccess){
                            self.onLoadSuccess(data)
                        }
                    }
                });
            }


            /**
             * 点击行调用的事件
             */
            function rowItem_Click()
            {
                var row = $(this).data('row')
                if(row.type <= 0){
                    _folderId = row.id
                    _pageNumber = 1;
                    loadFiles()
                }else{
                    if(isImg(row.name)){
                        var dataIndex = $(this).find("img").attr("data-index")
                        if(viewer !== null) {
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
                        previewFile(row)
                    }

                }

                if(self.onRowItemClick){
                    self.onRowItemClick(row)
                }

            }

            self.setDefaultItem = function (item) {
                _defaultItem = item
            }

            self.init = function () {
                tbody = self.find('tbody')
                notfind = self.find('.notfind')
            }

            /**
             * 加载数据
             * @param ownerId 个人ID或者空间部门ID
             * @param folderId 文件夹ID
             * @param pageNumber 第几页
             * @param pageSize 每页显示多少条
             * @param orderField 排序字段
             * @param order 是否降序
             * @param name 查询关键字
             * @param isSearch 是否是查询结果
             */
            self.load = function (ownerId, folderId, pageNumber, pageSize, orderField, order, name, isSearch) {
                _ownerId = ownerId
                _folderId = folderId
                _pageNumber = pageNumber
                _pageSize = pageSize
                _orderField = orderField
                _order = order
                _name = name
                _isSearch = isSearch

                if(_name && _isSearch){
                    searchFiles()
                }else{
                    loadFiles()
                }

            }

            return self
        }
    })
})(jQuery)