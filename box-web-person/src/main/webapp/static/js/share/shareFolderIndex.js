(function($) {
	$.fn.extend({
		'ui.Page': function() {
			var self = this
			var createNewFolderDialog
			var renameDialog
			var versionDialog
			var selectFolderDialog
			var shareDialog
			var tablePopover
			var fileInfo
			var fileVersions
			var _selectedRow = null
			var _ownerId = sharedownerId
			var _folderId = parentId
			var _pageNumber = 1
			var _pageSize = 10
			var _orderField = 'modifiedAt'
			var _isDesc = true
			var _name = ''
			var _isSearch = false
			var _uploader = null
            var _uploaders = null
            var _uploadfile = null
			var tost
			var linkDialog
			self.init = function() {
				permissionFlag = getNodePermission(_ownerId,parentId,curUserId);
					// if(permissionFlag["upload"] == 0) {
					// 	$('.left').remove();
					// 	$('.right').attr('class','left');
					// 	$('#upload_rect').remove();
					// }
				var toolbar = self.find("#toolbar").Toolbar()
				toolbar.init()
				toolbar.onSortItemChange = function(orderField, isDesc) {
					_orderField = orderField
					_isDesc = isDesc
					datagrid.load(_ownerId, _folderId, _pageNumber, _pageSize, _orderField, _isDesc)
				}

				var breadcrumb = self.find('#breadcrumd').Breadcrumb()
				breadcrumb.setDefaultItem({
					id: 0,
					name: '收到的共享'
				})
				breadcrumb.init()
				breadcrumb.onChange = function(row) {
					if(row.id == 0){
						gotoPage(ctx + '/shared')
					}else{
						_folderId = row.id
						datagrid.load(_ownerId, _folderId, _pageNumber, _pageSize, _orderField, _isDesc)
					}
				}
				breadcrumb.load(_folderId)

				var pagination = self.find('#pagination').Pagination()
				pagination.init()
				pagination.onPageChange = function(pageNumber) {
					_pageNumber = pageNumber
					datagrid.load(_ownerId, _folderId, _pageNumber, _pageSize, _orderField, _isDesc)
				}

				tablePopover = self.find('#table_popover')

				//删除
				tablePopover.find("#delete").on('click', function() {
					deleteDialog.show()
				})
				//重命名
				// tablePopover.find("#rename").on('click', function() {
				// 	renameDialog.find('input[name="name"]').val(_selectedRow.name)
				// 	renameDialog.show()
				// })
				//移动到
				tablePopover.find('#moveTo').on('click', function() {
					selectFolderDialog.show0(_selectedRow)
				})

				var datagrid = self.find('#datagrid').Datagrid()
				datagrid.setDefaultItem({
					id: 0,
					name: '收到的共享'
				})
				datagrid.init()
				datagrid.onRowItemClick = function(row) {
					if (row.type <= 0) {
						_folderId = row.id
						_uploader.folderId = _folderId
                        _uploaders.folderId = _folderId
                        _uploadfile.folderId = _folderId
						breadcrumb.add(row)
					}
				}
				datagrid.onLoadSuccess = function(data) {
                    pagination.setTotalSize(data.totalCount)
                    pagination.setCurrentPage(_pageNumber)
                    pagination.setTotalPages(Math.ceil(data.totalCount / _pageSize))

					datagrid.find("a[id='worker']").each(function() {
						$(this).popover(tablePopover, true, "right", function(t) {
							tablePopover.find("dt").hide()
							tablePopover.find("dt[role*='" + t.attr("data") + "']").show()
						})
					})

				}
				datagrid.load(_ownerId, _folderId, _pageNumber, _pageSize, _orderField, _isDesc)

				datagrid.on("mouseenter", 'tr', function() {
					_selectedRow = $(this).data('row')
				})
				$('#new-folder-button').click(function() {
					createNewFolderDialog.show();
					createNewFolderDialog.find('input[name="name"]').val('');
					createNewFolderDialog.find('input[name="name"]').focus();
				})

				createNewFolderDialog = self.find('#createNewFolderDialog').dialog({
					title: '新建文件夹'
				})
				createNewFolderDialog.init()
				createNewFolderDialog.find('#cancel_button').on('click', function() {
					createNewFolderDialog.hide()
				})
				createNewFolderDialog.find('#ok_button').on('click', function() {
					var name = createNewFolderDialog.find('input[name="name"]').val().trim()
					if(!name) {
						console.log(123123123123123);
						$.Alert("文件夹名称不能为空")
						return
					}
					createFolder(name, function() {
						console.log(222);
						_orderField = 'modifiedAt'
						_isDesc = true
						toolbar.setDefaultSort()
						createNewFolderDialog.hide()
						datagrid.load(_ownerId, _folderId, _pageNumber, _pageSize, _orderField, _isDesc)
					})
				})

				renameDialog = self.find('#renameDialog').dialog({
					title: '重命名'
				})
				renameDialog.init()
				renameDialog.find('#cancel_button').on('click', function() {
					renameDialog.hide()
				})
				renameDialog.find('#ok_button').on('click', function() {
					var name = renameDialog.find('input[name="name"]').val().trim()
					if(!name) {
						$.Alert("名称不能为空")
						return
					}
					renameNode(_selectedRow, name, function() {
						renameDialog.hide()
						datagrid.load(_ownerId, _folderId, _pageNumber, _pageSize, _orderField, _isDesc)
					})
				})
				versionDialog = self.find('#versionDialog').dialog({title: '文件版本信息'})
				versionDialog.init()

				fileVersions = versionDialog.find('#fileVersions').FileVersions()
                fileVersions.onLoadSuccess = function () {
                    versionDialog.show()
                }
                fileVersions.init()

				deleteDialog = self.find('#deleteDialog').dialog({
					title: '系统提示'
				})
				deleteDialog.init()
				deleteDialog.find('#cancel_button').on('click', function() {
					deleteDialog.hide()
				})
				deleteDialog.find('#ok_button').on('click', function() {
					console.log(_selectedRow.id);
					deleteFiles(_ownerId, _selectedRow.id, function() {
						datagrid.load(_ownerId, _folderId, _pageNumber, _pageSize, _orderField, _isDesc)
					})
					deleteDialog.hide()
				})

				selectFolderDialog = self.find('#selectFolderDialog').SelectFolderDialog()
				selectFolderDialog.onSuccess = function() {
					datagrid.load(_ownerId, _folderId, _pageNumber, _pageSize, _orderField, _isDesc)
				}
				selectFolderDialog.init0()

				shareDialog = self.find('#shareDialog').ShareDialog()
				shareDialog.onSuccess = function() {
					datagrid.load(_ownerId, _folderId, _pageNumber, _pageSize, _orderField, _isDesc)
				}
				shareDialog.init0()

				// 外发
				linkDialog = self.find('#linkDialog').dialog({
					title: '新建外链'
				})
				linkDialog.init()
				linkDialog.find('#cancel_button').on('click', function() {
					linkDialog.hide()
				})
				linkDialog.find('#ok_button').on('click', function() {
					setLink(_selectedRow)
				})

				 _uploader = self.find("#upload_button").Uploader({'webkitdirectory' : false});
                _uploader.ownerId = _ownerId
                _uploader.folderId = _folderId
                _uploader.onUploadSuccess = function () {
                    datagrid.load(_ownerId, _folderId, _pageNumber, _pageSize, _orderField, _isDesc)
                }
                _uploader.init();

                _uploaders = self.find("#upload_buttons").Uploader({'webkitdirectory' : true});
                _uploaders.ownerId = _ownerId
                _uploaders.folderId = _folderId
                _uploaders.onUploadSuccesss = function () {
                    datagrid.load(_ownerId, _folderId, _pageNumber, _pageSize, _orderField, _isDesc)
                }
                _uploaders.init();



				_uploadfile = self.find("#upload_file").Uploader({'webkitdirectory' : false});
				console.log(_ownerId);
                _uploadfile.ownerId = _ownerId
                _uploadfile.folderId = _folderId
                _uploadfile.onUploadSuccessfile = function () {
                    datagrid.load(_ownerId, _folderId, _pageNumber, _pageSize, _orderField, _isDesc)
                }
                _uploadfile.init();
			}

			return self
		}
	})
	$(document).ready(function() {
		var page = $(document)['ui.Page']()
		page.init()
	})
})(jQuery)