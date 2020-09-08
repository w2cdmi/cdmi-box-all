/**
 * Created by quxiangqian on 2017/12/23.
 */
(function ($) {
    $.fn.extend({
        Breadcrumb: function () {
            var self = this
            var _defaultItem = null
            var _items = []
            var _container = null

            function breadcrumbItem_Click()
            {
                var row = $(this).parent().data('row')
                _items.splice(row.index + 1)
                createBreamcrumb(_items)
                if(self.onChange){
                    self.onChange(row)
                }
            }

            function goback_Click()
            {
                var row = _items.pop()
                createBreamcrumb(_items)
                if(self.onChange){
                    self.onChange(_items[_items.length -1])
                }
            }

            function loadBreadcrumb(catalogParentId) {
                var nodePermission = getNodePermission(ownerId, catalogParentId, curUserId);
                if(nodePermission["download"] != 1) {
                    $.alert('您没有权限进行操作');
                    return;
                }
                $.ajax({
                    type: 'GET',
                    url: host + '/ufm/api/v2/nodes/' + ownerId + '/' + catalogParentId + "/path",
                    success: function (data) {
                        if(data){

                            _items = data
                        }
                        _items.unshift(_defaultItem)
                        createBreamcrumb(_items)
                    }, error: function () {
                        $.Alert('获取目录路径失败');
                    }
                });

            }

            function createBreamcrumb(data, isSearch)
            {
                self.empty()
                self.append('<ol class="breadcrumb"></ol>')
                _container = self.find('ol.breadcrumb')

                var len = data.length

                if (len > 1 && !isSearch) {
                    _container.before('<a href="javascript:void(0);" id="_goback">返回上一级</a>')
                }

                var lastIndex = len - 1
                var listItem
                $.each(data, function (i, row) {
                    row.index = i
                    if (i == lastIndex) {
                        _container.append('<li class="active"><span class="txt-ellipsis" title="' + row.name + '">' + row.name + '</span></li>')

                    } else {
                        listItem = $('<li><a href="javascript:void(0);" class="txt-ellipsis" title="' + row.name + '">' + row.name + '</a></li>')
                        listItem.data('row', row)
                        _container.append(listItem)
                    }
                })
                self.find('#_goback').on('click', goback_Click)
                _container.find('li > a').on('click', breadcrumbItem_Click)
            }

            self.init = function () {
             /*   $(document).on('keyup', function(e){
                    if(e.keyCode != 8){
                        return
                    }

                    if(_items.length == 1){
                        return;
                    }
                    goback_Click()
                })*/
            }

            self.setDefaultItem = function (item) {
                _defaultItem = item
            }

            self.load = function (catalogParentId, isSearch) {
                _items = []
                if(isSearch){
                    _items.push(_defaultItem)
                    _items.push({id: -1, name: '查询结果'})
                    createBreamcrumb(_items, isSearch)
                }else{
                    if(catalogParentId==0){
                        return;
                    }
                    loadBreadcrumb(catalogParentId)
                }

            }

            return self
        }
    });

})(jQuery)