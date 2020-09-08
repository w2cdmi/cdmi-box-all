(function($) {
    $.fn.extend({
        Datagrid: function() {
            var self = this
            var tbody = null
            var notfind = null
            var _defaultItem = null
            var _ownerId
            var _folderId
            var _pageNumber
            var _pageSize
            var _orderField
            var _order
            var _name
            var _docType
            var num = -1
            var viewer = null
            var _isSearch = false

            function worker_Click(e) {
                e.stopPropagation()
            }

            function loadFiles() {
                $('.left').show();
                var _loading = $.Tost('数据加载中...').show()
                var permission = getNodePermission(_ownerId,_folderId,curUserId);
                var p = 0;
                for (var i in permission) {
                    p = p + permission[i];
                }
                if (p == 0) {
                    _loading.hide()
                    $.Alert("您没有访问该文件夹的权限")
                    return;
                }

                var url = host + "/ufm/api/v2/folders/"+ _ownerId +"/"+ _folderId +"/items";
                var params = {
                    offset: (_pageNumber - 1) * _pageSize,
                    limit: _pageSize,
                    order: [{ field: 'type' , direction: "ASC" },{ field: _orderField, direction: _order }],
                    thumbnail: [{ width: 96, height: 96 }]
                }
                if (permission != null && permission["browse"] == 1) {
                    $.ajax({
                        type: "POST",
                        url: url,
                        data: JSON.stringify(params),
                        error: function() {
                            _loading.hide()
                            $.Alert('获取文件列表失败')
                        },
                        success: function(data) {
                            function parseQueryString() {
                                var url = location.search; //获取url中"?"符后的字串
                                var theRequest = {};
                                if (url.indexOf("?") !== -1) {
                                    var str = url.substr(1);
                                    strs = str.split("&");
                                    for(var i = 0; i < strs.length; i ++) {
                                        theRequest[strs[i].split("=")[0]]=decodeURI(strs[i].split("=")[1]);
                                    }
                                }
                                return theRequest;
                            }
                            var parseQueryStrings = parseQueryString();
                            var docType = parseQueryStrings.docType
                            if(docType != undefined){
                                searchFiles(curUserId, docType, name);
                            } 
                            var files = data.folders.concat(data.files);
                            var listItem;
                            var img;
                            tbody.empty()
                            self.find('.notfind').hide()
                            if (files.length == 0) {
                                _loading.hide()
                                self.find('.notfind').show()
                            } else {
                                _loading.hide()
                            }
                            $.each(files, function (i, row) {
                                if(isImg(row.name)){
                                    num++
                                    var imgSrc = downLoadImg(row.ownedBy,row.id)
                                    img = '<img data-original='+imgSrc+' data-index='+num+' src= '+ imgSrc +' alt='+row.name+' style="display: none"/>'
                                }else{
                                    img = ''
                                }
                                listItem = $('<tr title="' + row.name + '">'
                                    + '<td style="min-width: 280px">'
                                    +   '<div class="fileitem-temple1 cl">'
                                    + img
                                    +       getFileIcon(row)
                                    +       '<span class="other-span"><h3 style="line-height: 42px" class="txt-ellipsis">'+row.name+'</h3></span>'
                                    +       '<u><a id="worker" data="' + row.type + '"><i class="fa fa-ellipsis-h fa-lg"></i></a></u>'
                                    +   '</div>'
                                    + '</td>'
                                    + '<td style="width: 100px">' + (row.size ? formatFileSize(row.size) : '—') + '</td>'
                                    + '<td style="width: 180px">' + getFormatDate(new Date(row.createdAt), "yyyy-MM-dd hh:mm") + '</td>'
                                    + '</tr>').data('row', row)

                                tbody.append(listItem)
                            });
                            num = -1
                            tbody.find('tr').on('click', rowItem_Click)
                            tbody.find('a#worker').on('click', worker_Click)
                            _loading.hide()
                            if (self.onLoadSuccess) {
                                self.onLoadSuccess(data)
                            }
                        }
                    })
                } else {
                    _loading.hide()
                    $.Alert("您没有权限进行该操作")
                }
            }
            
            function searchFiles(curUserId, docType) {
                var _loading = $.Tost('数据加载中...').show()
                if(curUserId != undefined) {
                    var url = host + "/ufm/api/v2/nodes/" + curUserId + "/search";
                } 
                if(curUserId == undefined) {
                    var url = host + "/ufm/api/v2/nodes/" + _ownerId + "/search";
                }
                var params = {
                    docType: _docType || docType,
                    type: '1',
                    offset: (_pageNumber - 1) * _pageSize,
                    limit: _pageSize,
                    name: _name || name,
                    order: [{ field: 'type' , direction: 'ASC' },{ field: 'modifiedAt' , direction: "DESC" }],
                    thumbnail: [{ width: 96, height: 96 }]
                };

                $.ajax({
                    type: "POST",
                    url: url,
                    data: JSON.stringify(params),
                    error: function() {
                        _loading.hide()
                        $.Alert("搜索文件失败")
                    },
                    success: function(data) {
                        _loading.hide()
                        var files = data.folders.concat(data.files);
                        var listItem
                        var img
                        tbody.empty()
                        notfind.hide()
                        if (files.length == 0) {
                            _loading.hide()
                            notfind.show();
                            // self.onLoadSuccess(data);
                            // return
                        }

                        $.each(files, function(i, row) {
                            if(isImg(row.name)){
                                num++
                                var imgSrc = downLoadImg(row.ownedBy,row.id)
                                img = '<img data-original='+imgSrc+' data-index='+num+' src= '+ imgSrc +' alt='+row.name+' style="display: none"/>'
                            }else{
                                img = ''
                            }
                            listItem = $('<tr title="' + row.name + '">' +
                                '<td style="min-width: 280px">' +
                                '<div class="fileitem-temple1 cl">' +
                                img +
                                getFileIcon(row) +
                                '<span><h3 style="line-height: 42px" class="txt-ellipsis">' + row.name + '</h3></span>' +
                                '<u><a id="worker" data="' + row.type + '"><i class="fa fa-ellipsis-h"></i></a></u>' +
                                '</div>' +
                                '</td>' +
                                '<td style="width: 100px">' + (row.size ? formatFileSize(row.size) : '-') + '</td>' +
                                '<td style="width: 180px">' + getFormatDate(new Date(row.modifiedAt), "yyyy-MM-dd hh:mm") + '</td>' +
                                '</tr>').data('row', row)
                            tbody.append(listItem)
                        })
                        num = -1
                        tbody.find('tr').on('click', rowItem_Click)
                        _loading.hide()
                        if (self.onLoadSuccess) {
                            self.onLoadSuccess(data)
                        }
                        if(_docType == '' && docType == ''){
                            $('.left').hide();
                        }else if(docType == undefined && _docType == '') {
                            $('.left').hide();
                        } else {
                            $('.left').show();
                            $('#newFolder_button').hide();
                        }
                        
                    }
                });
            }


            /**
             * 点击行调用的事件
             */
            function rowItem_Click() {
                var row = $(this).data('row')
                if (row.type <= 0) {
                    _folderId = row.id
                    _pageNumber = 1
                    loadFiles()
                } else {
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

                if (self.onRowItemClick) {
                    self.onRowItemClick(row)
                }

            }

            self.setDefaultItem = function(item) {
                _defaultItem = item
            }

            self.init = function() {
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
             * @param order 排序顺序：ASC DESC
             * @param name 查询关键字
             * @param isSearch 是否是查询结果
             */
            self.load = function(ownerId, folderId, pageNumber, pageSize, orderField, order, name, docType, isSearch) {
                _ownerId = ownerId
                _folderId = folderId
                _pageNumber = pageNumber
                _pageSize = pageSize
                _orderField = orderField
                _order = order
                _name = name
                _docType = docType
                _isSearch = isSearch
                if (_name || _docType && _isSearch) {
                    searchFiles()
                } else {
                    loadFiles()
                }
            }

            return self
        }
    })
})(jQuery)