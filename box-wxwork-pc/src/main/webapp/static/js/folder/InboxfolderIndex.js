(function($){
    $.fn.extend({
        'ui.Page' : function()
        {
            var self = this
            var datagrid
            var createNewFolderDialog
            var renameDialog
            var deleteDialog
            var infoDialog
            var versionDialog
            var selectFolderDialog
            var shareDialog
            var tablePopover
            var fileInfo
            var fileVersions
            var _selectedRow = null
            var _ownerId = ownerId
            var _folderId = parentId
            var _pageNumber = 1
            var _pageSize = 10
            var _orderField = 'modifiedAt'
            var _order = "DESC"
            var _name = ''
            var _isSearch = false
            var _uploader = null
            var tost
            var linkDialog

            /**
             * 加载表格数据
             */
            function datagridLoad(){

                datagrid.load(_ownerId, _folderId, _pageNumber, _pageSize, _orderField, _order, _name, _isSearch)
            }

            /**
             * 初始化
             */
            self.init = function()
            {
                var toolbar = self.find("#toolbar").Toolbar()
                toolbar.init()
                toolbar.onNewFolder = function() {
                    createNewFolderDialog.find('input[name="name"]').val('')
                    createNewFolderDialog.show()
                    createNewFolderDialog.find('input[name="name"]').focus();
                }
                toolbar.onSortItemChange = function(orderField, order) {
                    _orderField = orderField
                    _order = order
                    datagridLoad()
                }
                toolbar.onSearch = function(keyword) {
                    if(keyword){
                        _name = keyword
                        _isSearch = true
                        breadcrumb.load(_folderId, _isSearch)
                        datagridLoad()
                    }else{
                        $.Alert("请输入查询关键字")
                    }
                }
                toolbar.onCancel = function() {
                    console.log(_folderId);
                    if(_isSearch){
                        _folderId = 0
                        // _uploader.folderId = _folderId
                        _pageNumber = 1
                        _pageSize = 10
                        _name = ''
                        _isSearch = false
                        breadcrumb.load(_folderId)
                        datagridLoad()
                    }

                }


                var breadcrumb = self.find('#breadcrumd').Breadcrumb()
                breadcrumb.setDefaultItem({id: 0, name: '收件箱'})
                breadcrumb.init()
                breadcrumb.onChange = function(row){
                    if(row.id == 0){
						gotoPage(ctx + '/share/shareLinks')
					}else{
                        _folderId = row.id
                        _pageNumber = 1
                        datagridLoad()
					}
                }
                breadcrumb.load(_folderId)

                var pagination = self.find('#pagination').Pagination()
                pagination.init()
                pagination.onPageChange = function(pageNumber){
                    _pageNumber = pageNumber
                    datagridLoad()
                }

                tablePopover = self.find('#table_popover')

                //删除
                tablePopover.find("#delete").on('click', function(){
                    deleteDialog.show()
                })

                //移动到
                tablePopover.find('#moveTo').on('click', function(){
                    selectFolderDialog.show0(_selectedRow)
                })

                datagrid = self.find('#datagrid').Datagrid()
                datagrid.setDefaultItem({id: 0, name: '收件箱'})
                datagrid.init()
                datagrid.onRowItemClick = function(row){
                    if(row.type <= 0){
                        if(_isSearch){
                            toolbar.clearSearch()
                        }
                        _name = ''
                        _isSearch = false
                        _folderId = row.id
                        // _uploader.folderId = _folderId
                        breadcrumb.load(row.id)
                    }
                }
                datagrid.onLoadSuccess = function(data)
                {
                    pagination.setTotalSize(data.totalCount)
                    pagination.setCurrentPage(_pageNumber)
                    pagination.setTotalPages(Math.ceil(data.totalCount / _pageSize)==0 ? "1" : Math.ceil(data.totalCount / _pageSize))
                    datagrid.find("a[id='worker']").each(function(){
                        $(this).popover(tablePopover,true,"right",function (t) {
                            tablePopover.find("dt").hide()
                            tablePopover.find("dt[role*='"+t.attr("data")+"']").show()
                        })
                    })

                }
                datagridLoad()

                datagrid.on("mouseenter",'tr',function () {
                    _selectedRow = $(this).data('row')
                })

                deleteDialog = self.find('#deleteDialog').dialog({title:'系统提示'})
                deleteDialog.init()
                deleteDialog.find('#cancel_button').on('click', function(){
                    deleteDialog.hide()
                })
                deleteDialog.find('#ok_button').on('click', function(){
                    deleteFile(_ownerId, _selectedRow.id, function(){
                        datagridLoad()
                    })
                    deleteDialog.hide()
                })

                selectFolderDialog = self.find('#selectFolderDialog').SelectFolderDialog()
                selectFolderDialog.onSuccess = function(){
                    datagridLoad()
                }
                selectFolderDialog.init0()
            }

            return self
        }
    })
    $(document).ready(function(){
        var page = $(document)['ui.Page']()
        page.init()
    })
})(jQuery)