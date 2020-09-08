/**
 * Created by quxiangqian on 2017/12/23.
 */
(function ($) {
    $
        .fn
        .extend({
            Breadcrumb: function () {
                var self = this
                var _defaultItem = null
                var _items = []
                var _container = null

                function breadcrumbItem_Click() {
                    var row = $(this)
                        .parent()
                        .data('row')
                    _items.splice(row.index + 1)
                    createBreamcrumb()
                    if (self.onChange) {
                        self.onChange(row)
                    }
                }

                function goback_Click() {
                    var row = _items.pop()
                    createBreamcrumb(_items)
                    if (self.onChange) {
                        self.onChange(_items[_items.length - 1])
                    }
                }

                function loadBreadcrumb(ownerId, parentId) {
                    var url = host + '/ufm/api/v2/nodes/' + ownerId + '/' + parentId + "/path?rootId=" + parentId;
                    $.ajax({
                        type: 'GET',
                        url: url,
                        cache: false,
                        async: true,
                        timeout: 180000,
                        success: function (data) {
                            if (data) {
                                _items = data
                            }
                            _items.unshift(_defaultItem)
                            createBreamcrumb(_items)
                        },
                        error: function () {
                            $.Alert('获取目录路径失败');
                        }
                    });

                }

                function createBreamcrumb() {
                    self.empty()
                    self.append('<ol class="breadcrumb"></ol>')
                    _container = self.find('ol.breadcrumb')

                    var len = _items.length

                    if (len > 1) {
                        _container.before('<a href="javascript:void(0);" id="_goback">返回上一级</a>')
                    }

                    var lastIndex = len - 1
                    var listItem
                    $.each(_items, function (i, row) {
                        row.index = i
                        if (i == lastIndex) {
                            _container.append('<li class="active"><span class="txt-ellipsis" titl' +
                                    'e="' + row.name + '">' + row.name + '</span></li>')

                        } else {
                            listItem = $('<li><a href="javascript:void(0);" class="txt-ellipsis" title="' + row.name + '">' + row.name + '</a></li>')
                            listItem.data('row', row)
                            _container.append(listItem)
                        }
                    })
                    self.find('#_goback')
                        .on('click', goback_Click)
                    _container.find('li > a')
                              .on('click', breadcrumbItem_Click)
                        
                }
                self.add = function(item) {
                    _items.push(item)
                    createBreamcrumb()
                }
                self.init = function () {
                    $(document).on('keyup', function(e){
                        if(e.keyCode != 8){
                            return
                        }
    
                        if(_items.length == 1){
                            return;
                        }
                        goback_Click()
                    })
                }

                self.setDefaultItem = function (item) {
                    _defaultItem = item
                }

                self.load = function (ownerId, parentId) {
                    _items = []
                    loadBreadcrumb(ownerId, parentId)
                }

                return self
            }
        });

})(jQuery)