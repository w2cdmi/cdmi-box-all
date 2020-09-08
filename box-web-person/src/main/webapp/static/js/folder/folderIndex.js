(function ($) {
    $.fn.extend({
        'ui.Page': function () {
            var self = this
            var datagrid
            var createNewFolderDialog
            var renameDialog
            var infoDialog
            var versionDialog
            var selectFolderDialog
            var selectFolderDialogCopyTeam
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
            var _docType = ''
            var _isSearch = false
            var _uploader = null
            var _uploaders = null
            var _uploadfile = null
            var tost
            var linkDialog

            /**
             * 加载表格数据
             */
            function datagridLoad() {
                datagrid.load(_ownerId, _folderId, _pageNumber, _pageSize, _orderField, _order, _name, _docType, _isSearch)
            }
            // 移动文件
            function showMoveToDialog(node) {
                /* 在当前空间内移动，所以传入当前使用的ownerId*/
                var names = node.name.substring(0,20);
                if (node.name.length > 20){
                    names +='...'
                }
                var folderChooser = $("#copyToFolderChooserDialog").FolderChooser({
                        title:"移动“"+names+"”到",
                        exclude: function (r) {
                            return r !== undefined && r.ownedBy === node.ownedBy && r.id === node.id;
                        },
                        callback: function (ownerId, folderId) {
                            moveTo(node,ownerId,folderId,function (data) {
                                folderChooser.closeDialog()
                                $.toast("移动成功");

                                datagridLoad();
                            })
                        }
                    }
                );
                //加载数据,并显示
                folderChooser.showDialog();
            }

            // 复制文件
            function showCopyToDialogTeam(node) {
                var names = node.name.substring(0,20);
                if (node.name.length > 20){
                    names +='...'
                }
                var folderChooser = $("#copyToFolderChooserDialog").FolderChooser({
                        title:"另存“"+names+"”到",
                        exclude: function (r) {
                            return r !== undefined && r.ownedBy === node.ownedBy && r.id === node.id;
                        },
                        callback: function (ownerId, folderId) {
                            copyTo(node,ownerId,folderId,function (data) {
                                    folderChooser.closeDialog()
                                    $.toast("另存成功");
                                    datagridLoad();
                                }
                            )

                        }
                    }
                );

                //加载数据,并显示
                folderChooser.showDialog();
            }

            /**
             * 初始化
             */
            self.init = function () {
                var toolbar = self.find("#toolbar").Toolbar()
                toolbar.init()
                toolbar.onNewFolder = function () {
                    createNewFolderDialog.find('input[name="name"]').val('')
                    createNewFolderDialog.show()
                    createNewFolderDialog.find('input[name="name"]').focus();
                }
                toolbar.onSortItemChange = function (orderField, order) {
                    _orderField = orderField
                    _order = order
                    datagridLoad()
                }
                toolbar.onSearch = function (keyword) {
                    _name = keyword
                    _isSearch = true
                    _pageNumber = 1
                    breadcrumb.load(_folderId, _isSearch)
                    datagridLoad()
                }
                toolbar.onCancel = function () {
                    if (_isSearch) {
                        _folderId = 0
                        _uploader.folderId = _folderId
                        _uploaders.folderId = _folderId
                        _uploadfile.folderId = _folderId
                        _pageNumber = 1
                        _pageSize = 10
                        _name = ''
                        _isSearch = false
                        breadcrumb.load(_folderId)
                        datagridLoad()
                    }

                }

                var breadcrumb = self.find('#breadcrumd').Breadcrumb()
                breadcrumb.setDefaultItem({id: 0, name: '个人文件'})
                breadcrumb.init()
                breadcrumb.onChange = function (row) {
                    if (_isSearch) {
                        toolbar.clearSearch()
                    }
                    _name = ''
                    _isSearch = false
                    _folderId = row.id
                    _pageNumber = 1
                    _uploader.folderId = _folderId
                    _uploaders.folderId = _folderId
                     _uploadfile.folderId = _folderId
                    datagridLoad()
                }
                breadcrumb.load(_folderId)

                var pagination = self.find('#pagination').Pagination()
                pagination.init()
                pagination.onPageChange = function (pageNumber) {
                    _pageNumber = pageNumber
                    datagridLoad()
                }

                tablePopover = self.find('#table_popover')

                //共享
                tablePopover.find("#share").on('click', function () {
                    var deptId = 0
                    shareDialog.show0(deptId, _selectedRow)
                })

                initClipboard()
                //下载
                tablePopover.find("#downFile").on('click', function () {
                    var row = _selectedRow
                    downloadFile(row)
                })

                //外链
                tablePopover.find("#link").on('click', function () {
                    var allselectedRow = _selectedRow;
                    $("#linkDialog").empty();
                    tost = $.Tost("正在加载中.....")
                    tost.show();
                    $("#linkDialog").load(ctx+'/share/link/'+allselectedRow.ownedBy+'/'+allselectedRow.id+"?"+Date.parse(new Date())/1000,
                        function(){
                            getLink(allselectedRow, function(){
                                linkDialog.toCenter()
                                linkDialog.show()
                            })

                            tost.hide();
                            $("#linkDialog").find("input[name=accessCodeMode]").click(function() {
                                if($(this).attr('id') != "randomMode") {
                                    $("#accessCodeDiv").css("display", "none");
                                } else {
                                    $("#accessCodeDiv").css("display", "block");
                                }
                            })
                            $("#linkDialog").find("#refreshAccessCode").click(function(){
                                refreshAccessCode()
                            })
                            $("#linkDialog").find("#downloadSwitch").bind("click", function() {
                                if($("#download").val() == "off") {
                                    $("#download").val("on");
                                } else {
                                    $("#download").val("off");
                                }
                            });
                            $("#linkDialog").find("#ok_button").click(function(){
                                setLink(allselectedRow,function() {
                                    linkDialog.toCenter()
                                })
                                $("#linkDialog").find('#schedule-box').css({"display":"none"})
                            })
                            $("#linkDialog").find("#cancel_button").click(function(){

                                $("#linkFileListDiv").show();
                                $("#addLinkFileDiv").hide();
                                $("#linkDialog").find('#schedule-box').css({"display":"none"})
                            })
                            $("#linkDialog").find("#gotoCreatePage").click(function(){
                                gotoCreatePage()
                            })
                            $("#linkDialog").find("#batchDeleteLink").click(function(){
                                batchDeleteLink(allselectedRow)
                            })
                        })
                })

                //删除
                tablePopover.find("#delete").on('click', function () {
                    $.Confirm('确认要删除吗？', function () {
                        deleteFile(_ownerId, _selectedRow.id, function () {
                            datagridLoad()
                        })
                    })
                })
                //重命名
                tablePopover.find("#rename").on('click', function () {
                    var row = _selectedRow;
                    var name = row.name;
                    if(row.type == 1){
                        var index = row.name.lastIndexOf(".");
                        if(index != -1){
                            name = (row.name).substring(0,index);
                        }
                    }
                    renameDialog.find('input[name="name"]').val(name)
                    renameDialog.show()
                })
                //设为快捷目录
                tablePopover.find("#addShortcutFolder").on('click', function () {
                    addShortcutFolder(_ownerId, _selectedRow)
                })
                //文件夹信息
                tablePopover.find('#folderInfo').on('click', function () {
                    fileInfo.load(_selectedRow)
                })
                //文件信息
                tablePopover.find('#fileInfo').on('click', function () {
                    fileInfo.load(_selectedRow)
                })

                //移动到
                tablePopover.find('#moveTo').on('click', function () {
                    //selectFolderDialog.show0(_selectedRow)
                    showMoveToDialog(_selectedRow)
                })
                //另存为到其他空间
                tablePopover.find('#copyToTeam').on('click', function () {
                    showCopyToDialogTeam(_selectedRow)
                })
                //文件版本信息
                tablePopover.find('#fileVersionInfo').on('click', function () {
                    fileVersions.load(_selectedRow)
                })

                datagrid = self.find('#datagrid').Datagrid()
                datagrid.setDefaultItem({id: 0, name: '个人文件'})
                datagrid.init()
                datagrid.onRowItemClick = function (row) {
                    if (row.type <= 0) {
                        if (_isSearch) {
                            toolbar.clearSearch()
                        }
                        _name = ''
                        _isSearch = false
                        _folderId = row.id
                        _uploader.folderId = _folderId
                        _uploaders.folderId = _folderId
                        _uploadfile.folderId = _folderId
                        breadcrumb.load(row.id)
                    }
                }
                datagrid.onLoadSuccess = function (data) {
                    pagination.setTotalSize(data.totalCount)
                    pagination.setCurrentPage(_pageNumber)
                    pagination.setTotalPages(Math.ceil(data.totalCount / _pageSize)=="0" ? "1": Math.ceil(data.totalCount / _pageSize))

                    datagrid.find("a[id='worker']").each(function () {
                        $(this).popover(tablePopover, true, "right", function (t) {
                            tablePopover.find("dt").hide()
                            var $menu = tablePopover.find("dt[role*='" + t.attr("data") + "']");
                            if($menu.length > 0) {
                                $menu.show();
                            } else {
                                //没有相关的菜单项时，不显示菜单
                                tablePopover.hide();
                            }
                        })
                    })

                }
                datagridLoad()

                datagrid.on("mouseenter", 'tr', function () {
                    _selectedRow = $(this).data('row')
                })

                //menubar.
                // var toolbar = self.find("#toolbar").Toolbar()
                var menubar = self.find("#menubar").Menubar()

                menubar.init();
                menubar.onsearchfile = function (docType) {
                    _docType = docType
                    _isSearch = true
                    _pageNumber = 1
                    breadcrumb.load(_folderId, _isSearch, _docType)
                    datagridLoad()
                }

                createNewFolderDialog = self.find('#createNewFolderDialog').dialog({title: '新建文件夹'})
                createNewFolderDialog.init()
                createNewFolderDialog.find('#cancel_button').on('click', function () {
                    createNewFolderDialog.hide()
                })
                createNewFolderDialog.find('form').submit(function (e) {
                    e.preventDefault();
                    createNewFolderDialog.find('input[name="name"]').blur()
                    var name = createNewFolderDialog.find('input[name="name"]').val().trim()
                    if (!name) {
                        $.Alert("文件夹名称不能为空")
                        return
                    }
                    if (!checkFileName(name)) {
                        return
                    }
                    createNewFolderDialog.hide()
                    createFolder(_ownerId, _folderId, name, function () {
                        _orderField = 'modifiedAt'
                        _order = "DESC"
                        toolbar.setDefaultSort()
                        createNewFolderDialog.hide()
                        datagridLoad()
                    })
                })
                createNewFolderDialog.find('#ok_button').on("click",function () {
                    var name = createNewFolderDialog.find('input[name="name"]').val().trim()
                    if (!name) {
                        $.Alert("文件夹名称不能为空")
                        return
                    }
                    if (!checkFileName(name)) {
                        return
                    }
                    createNewFolderDialog.hide()
                    createFolder(_ownerId, _folderId, name, function () {
                        _orderField = 'modifiedAt'
                        _order = "DESC"
                        toolbar.setDefaultSort()
                        createNewFolderDialog.hide()
                        datagridLoad()
                    })
                })

                renameDialog = self.find('#renameDialog').dialog({title: '重命名'})
                renameDialog.init()
                renameDialog.find('#cancel_button').on('click', function () {
                    renameDialog.hide()
                })
                renameDialog.find("form").submit(function (e) {
                    e.preventDefault();
                    renameDialog.find('input[name="name"]').blur()
                    var name = renameDialog.find('input[name="name"]').val().trim()
                    if (!name) {
                        $.Alert("名称不能为空")
                        return
                    }
                    if (!checkFileName(name)) {
                        return
                    }
                    renameDialog.hide()
                    renameNode(_selectedRow, name, function () {
                        datagridLoad()
                    })
                })
                renameDialog.find('#ok_button').on('click', function () {

                    var name = renameDialog.find('input[name="name"]').val().trim()
                    if (!name) {
                        $.Alert("名称不能为空")
                        return
                    }
                    if (!checkFileName(name)) {
                        return
                    }
                    renameDialog.hide()
                    renameNode(_selectedRow, name, function () {
                        datagridLoad()
                    })
                })


                infoDialog = self.find('#infoDialog').dialog({title: '文件详情'})
                infoDialog.init()

                fileInfo = infoDialog.find('#fileInfo').FileInfo()
                fileInfo.onLoadSuccess = function () {
                    infoDialog.show()
                }
                fileInfo.init()

                versionDialog = self.find('#versionDialog').dialog({title: '文件版本信息'})
                versionDialog.init()

                fileVersions = versionDialog.find('#fileVersions').FileVersions()
                fileVersions.onLoadSuccess = function () {
                    versionDialog.show()
                }
                fileVersions.init()

                selectFolderDialog = self.find('#selectFolderDialog').SelectFolderDialog()
                selectFolderDialog.onSuccess = function () {
                    datagridLoad()
                }
                selectFolderDialog.init0()

                selectFolderDialogCopyTeam = self.find('#selectFolderDialogCopyTeam').SelectFolderDialogCopyTeam()
                selectFolderDialogCopyTeam.onSuccess = function () {
                    datagridLoad()
                }
                selectFolderDialogCopyTeam.init0()

                shareDialog = self.find('#shareDialog').ShareDialog()
                shareDialog.onSuccess = datagridLoad
                shareDialog.onLastSharedUserDeleteSuccess = datagridLoad
                shareDialog.init0()

                // 外发
                linkDialog = self.find('#linkDialog').dialog({title: '新建外链'})
                linkDialog.init()
                linkDialog.find('#cancel_button').on('click', function () {
                    linkDialog.hide()
                })
                linkDialog.find('#ok_button').on('click', function () {
                    setLink(_selectedRow)
                })

                _uploader = self.find("#upload_button").Uploader({'webkitdirectory' : false});
                _uploader.ownerId = _ownerId
                _uploader.folderId = _folderId
                _uploader.onUploadSuccess = function () {
                    datagridLoad()
                }
                _uploader.init();

                _uploaders = self.find("#upload_buttons").Uploader({'webkitdirectory' : true});
                _uploaders.ownerId = _ownerId
                _uploaders.folderId = _folderId
                _uploaders.onUploadSuccesss = function () {
                    datagridLoad()
                }
                _uploaders.init();



                _uploadfile = self.find("#upload_file").Uploader({'webkitdirectory' : false});
                _uploadfile.ownerId = _ownerId
                _uploadfile.folderId = _folderId
                _uploadfile.onUploadSuccessfile = function () {
                    datagridLoad()
                }
                _uploadfile.init();
            }

            return self
        }
    })
    $(document).ready(function () {
        var page = $(document)['ui.Page']()
        page.init()
        $('.folder').attr( 'webkitdirectory', '' );
    })
})(jQuery)