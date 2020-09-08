(function ($) {
    $
        .fn
        .extend({
            'ui.Page': function () {
                var self = this
                var datagrid
                var selectFolderDialog
                var selectFolderDialogCopy
                var createNewFolderDialog
                var tablePopover
                var renameDialog
                var linkDialog
                var tost
                var infoDialog
                var fileInfo
                var fileVersions
                var versionDialog
                var _selectedRow = null
                var _ownerId = ownerId
                var _folderId = parentId
                var _pageNumber = 1
                var _pageSize = 10
                var _orderField = 'name'
                var _order = "ASC"
                var _name = ''
                var _isSearch = false
                var _teamName = name
                var _teamId = teamId
                var userName = userName
                var _uploader = null
                var _uploaders = null
                var a = true;
                var acceccDt = null;
                var accessControlDialog = null;

                /**
                 * 加载表格数据
                 */
                function datagridLoad() {
                    datagrid.load(_ownerId, _folderId, _pageNumber, _pageSize, _orderField, _order, _name, _isSearch)
                }
                // 移动文件
                function showMoveToDialog(node) {
                    var names = judgeNameLength(node);
                    var folderChooser = $("#copyToFolderChooserDialog").FolderChooser({
                        title: "移动“" + names + "”到",
                        exclude: function (r) {
                            return r !== undefined && r.ownedBy === node.ownedBy && r.id === node.id;
                        },
                        callback: function (ownerId, folderId) {
                            moveTo(node, ownerId, folderId, function (data) {
                                folderChooser.closeDialog()
                                $.toast("移动成功");

                                datagridLoad();
                            })
                        }
                    });
                    //加载数据,并显示
                    folderChooser.showDialog();
                }

                // 复制文件
                function showCopyToDialogTeam(node) {
                    var names = judgeNameLength(node);
                    var folderChooser = $("#copyToFolderChooserDialog").FolderChooser({
                        title: "另存“" + names + "”到",
                        exclude: function (r) {
                            return r !== undefined && r.ownedBy === node.ownedBy && r.id === node.id;
                        },
                        callback: function (ownerId, folderId) {
                            copyTo(node, ownerId, folderId, function (data) {
                                folderChooser.closeDialog()
                                $.toast("另存成功");
                                datagridLoad();
                            })

                        }
                    });

                    //加载数据,并显示
                    folderChooser.showDialog();
                }
                self.init = function () {
                    var toolbar = self
                        .find("#toolbar")
                        .Toolbar()
                    toolbar.init()
                    toolbar.onNewFolder = function () {
                        createNewFolderDialog
                            .find('input[name="name"]')
                            .val('')
                        createNewFolderDialog.show()
                        createNewFolderDialog
                            .find('input[name="name"]')
                            .focus()
                    }
                    toolbar.onSortItemChange = function (orderField, isDesc) {
                        _orderField = orderField
                        _order = isDesc
                        datagridLoad()
                    }
                    toolbar.onSearch = function (keyword) {
                        _name = keyword
                        _isSearch = true
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

                    var breadcrumb = self
                        .find('#breadcrumd')
                        .Breadcrumb()
                    breadcrumb.setDefaultItem({id: 0, name: _teamName})
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

                    var pagination = self
                        .find('#pagination')
                        .Pagination()
                    pagination.init()
                    pagination.onPageChange = function (pageNumber) {
                        _pageNumber = pageNumber
                        datagridLoad()
                    }

                    // 删除
                    tablePopover = self.find('#table_popover')
                    tablePopover
                        .find("#delete")
                        .on('click', function () {
                            var row = _selectedRow;
                            var nodePermission = getNodePermission(_ownerId, row.id, curUserId);
                            if (nodePermission["authorize"] != 1) {
                                $.Alert('您没有权限操作！');
                                return;
                            }
                            $
                                .Confirm("确认删除吗？", function () {
                                    deleteFile(row.ownedBy, row.id, function () {
                                        datagrid.load(_ownerId, _folderId, _pageNumber, _pageSize, _orderField, _order)
                                    })
                                })
                        })
                    // 下载
                    tablePopover
                        .find("#downFile")
                        .on('click', function () {
                            var row = _selectedRow
                            var nodePermission = getNodePermission(_ownerId, row.id, curUserId);
                            if (nodePermission["download"] != 1) {
                                $.Alert('您没有权限操作！');
                                return;
                            }
                            downloadFile(row)
                        })
                    // 设为快捷目录
                    tablePopover
                        .find("#addShortcutFolder")
                        .on('click', function () {
                            $
                                .Confirm("确认设为快捷目录吗？", function () {
                                    addShortcutTeamFolder(_selectedRow, function () {
                                        datagrid.load(_ownerId, _folderId, _pageNumber, _pageSize, _orderField, _order)
                                    })
                                })
                        })
                    // 移动
                    tablePopover
                        .find('#moveTo')
                        .on('click', function () {
                            var nodePermission = getNodePermission(_ownerId, _selectedRow.id, curUserId);
                            if (nodePermission["authorize"] != 1) {
                                $.Alert('您没有移动该文件/文件夹的权限');
                                return;
                            }
                            showMoveToDialog(_selectedRow)
                            // selectFolderDialog.show0(_selectedRow, _teamName)
                        })
                    // 复制到个人空间
                    tablePopover
                        .find('#copyPerson')
                        .on('click', function () {
                            var copyRow = _selectedRow;
                            var nodePermission = getNodePermission(_ownerId, copyRow.id, curUserId);
                            if (nodePermission["download"] != 1) {
                                $.Alert('您没有转存的权限');
                                return;
                            }
                            showCopyToDialogTeam(copyRow)
                            // selectFolderDialogCopy.show0(copyRow)

                        })
                    //文件夹信息
                    tablePopover
                        .find('#folderInfo')
                        .on('click', function () {
                            var row = _selectedRow;
                            var nodePermission = getNodePermission(_ownerId, row.id, curUserId);
                            if (nodePermission["download"] != 1) {
                                $.Alert('您没有权限进行操作！');
                                return;
                            }
                            fileInfo.load(row)
                        })
                    //文件信息
                    tablePopover
                        .find('#fileInfo')
                        .on('click', function () {
                            var row = _selectedRow;
                            var nodePermission = getNodePermission(_ownerId, row.id, curUserId);
                            if (nodePermission["download"] != 1) {
                                $.Alert('您没有权限进行操作！');
                                return;
                            }
                            fileInfo.load(row)
                        })
                    //文件版本信息
                    tablePopover
                        .find('#fileVersionInfo')
                        .on('click', function () {
                            fileVersions.load(_selectedRow)
                        })
                    // 重命名
                    tablePopover
                        .find("#rename")
                        .on('click', function () {
                            var row = _selectedRow;
                            var nodePermission = getNodePermission(_ownerId, row.id, curUserId);
                            if (nodePermission["authorize"] != 1) {
                                $.Alert('您没有权限操作！');
                                return;
                            }
                            renameDialog.show()
                            var name = row.name;
                            if (row.type == 1) {
                                var index = row
                                    .name
                                    .lastIndexOf(".");
                                if (index != -1) {
                                    name = (row.name).substring(0, index);
                                }
                            }
                            $("#renameDialog")
                                .find('input[name="name"]')
                                .val(name)

                        })

                    initClipboard()

                    // 外发
                    tablePopover
                        .find("#linkPut")
                        .on('click', function () {
                            var allselectedRow = _selectedRow;
                            var nodePermission = getNodePermission(_ownerId, allselectedRow.id, curUserId);
                            if (nodePermission["publishLink"] != 1) {
                                $.Alert('您没有外发权限!');
                                return;
                            }
                            $("#linkDialog").empty();
                            tost = $.Tost("正在加载中.....")
                            tost.show();
                            $("#linkDialog").load(ctx + '/share/link/' + allselectedRow.ownedBy + '/' + allselectedRow.id + "?" + Date.parse(new Date()) / 1000, function () {
                                getLink(allselectedRow, function () {
                                    linkDialog.toCenter()
                                    linkDialog.show()
                                })

                                tost.hide();
                                $("#linkDialog")
                                    .find("input[name=accessCodeMode]")
                                    .click(function () {
                                        if ($(this).attr('id') != "randomMode") {
                                            $("#accessCodeDiv").css("display", "none");
                                        } else {
                                            $("#accessCodeDiv").css("display", "block");
                                        }
                                    })
                                $("#linkDialog")
                                    .find("#refreshAccessCode")
                                    .click(function () {
                                        refreshAccessCode()
                                    })
                                $("#linkDialog")
                                    .find("#downloadSwitch")
                                    .bind("click", function () {
                                        if ($("#download").val() == "off") {
                                            $("#download").val("on");
                                        } else {
                                            $("#download").val("off");
                                        }
                                    });
                                $("#linkDialog")
                                    .find("#ok_button")
                                    .click(function () {
                                        setLink(allselectedRow, function () {
                                            linkDialog.toCenter()
                                        })
                                        $("#linkDialog")
                                            .find('#schedule-box')
                                            .css({"display": "none"})
                                    })
                                $("#linkDialog")
                                    .find("#cancel_button")
                                    .click(function () {

                                        $("#linkFileListDiv").show();
                                        $("#addLinkFileDiv").hide();
                                        $("#linkDialog")
                                            .find('#schedule-box')
                                            .css({"display": "none"})
                                    })
                                $("#linkDialog")
                                    .find("#gotoCreatePage")
                                    .click(function () {
                                        gotoCreatePage()
                                    })
                                $("#linkDialog")
                                    .find("#batchDeleteLink")
                                    .click(function () {
                                        batchDeleteLink(allselectedRow)
                                    })

                            })

                        })

                    datagrid = self
                        .find('#datagrid')
                        .Datagrid()
                    datagrid.setDefaultItem({id: 0, name: _teamName})
                    datagrid.init()
                    datagrid.onRowItemClick = function (row) {
                        if (row.type == 0) {
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
                        pagination.setTotalPages(Math.ceil(data.totalCount / _pageSize) == 0
                            ? "1"
                            : Math.ceil(data.totalCount / _pageSize))

                        datagrid
                            .find("a[id='worker']")
                            .each(function () {
                                $(this)
                                    .popover(tablePopover, true, "right", function (t) {
                                        var row = _selectedRow
                                        console.log(row);
                                        if ((teamRole == 'admin' || teamRole == 'manager') && row.parent == 0) {
                                            tablePopover.find("#accessControl").remove()
                                            a = true;
                                            acceccDt = null;
                                            if (a) {
                                                if (acceccDt == null) {
                                                    acceccDt = "<dt role=\"0\" id=\"accessControl\"><i class=\"fa fa-clipboard\"></i>权限管理</dt>";
                                                    tablePopover.find("dl").append(acceccDt)
                                                    a = false;
                                                    //访问控制
                                                    tablePopover
                                                        .find("#accessControl")
                                                        .on('click', function () {

                                                            var deptId = 0
                                                            accessControlDialog.show0(deptId, _selectedRow, teamId, function () {
                                                                datagrid.load(_ownerId, _folderId, _pageNumber, _pageSize, _orderField, _order)
                                                            })
                                                        })
                                                }
                                            }
                                        } else {
                                            $('#accessControl').remove();
                                        }

                                        tablePopover
                                            .find("dt")
                                            .hide()
                                        tablePopover.find("dt[role*='" + t.attr("data") + "']").show()
                                    })
                            })

                    }
                    datagrid.load(_ownerId, _folderId, _pageNumber, _pageSize, _orderField, _order)

                    datagrid.on("mouseenter", 'tr', function () {
                        _selectedRow = $(this).data('row')
                    })

                    createNewFolderDialog = self
                        .find('#createNewFolderDialog')
                        .dialog({title: '新建文件夹'})
                    createNewFolderDialog.init()
                    createNewFolderDialog
                        .find('#cancel_button')
                        .on('click', function () {
                            createNewFolderDialog.hide()
                        })
                    createNewFolderDialog
                        .find('form')
                        .submit(function (e) {
                            e.preventDefault();
                            createNewFolderDialog
                                .find('input[name="name"]')
                                .blur()
                            var name = createNewFolderDialog
                                .find('input[name="name"]')
                                .val()
                                .trim()
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
                    createNewFolderDialog
                        .find('#ok_button')
                        .on("click", function () {
                            var name = createNewFolderDialog
                                .find('input[name="name"]')
                                .val()
                                .trim()
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
                    // 重命名
                    renameDialog = self
                        .find('#renameDialog')
                        .dialog({title: '重命名'})
                    renameDialog.init()
                    renameDialog
                        .find('#cancel_button')
                        .on('click', function () {
                            renameDialog.hide()
                        })
                    renameDialog
                        .find("form")
                        .submit(function (e) {
                            e.preventDefault();
                            renameDialog
                                .find('input[name="name"]')
                                .blur()
                            var name = renameDialog
                                .find('input[name="name"]')
                                .val()
                                .trim()
                            // if(_selectedRow.type==1){     var point = _selectedRow.name.lastIndexOf(".");
                            //     var type = _selectedRow.name.substr(point)     name = name+type     //
                            // return name }
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
                    renameDialog
                        .find('#ok_button')
                        .on('click', function () {
                            var name = renameDialog
                                .find('input[name="name"]')
                                .val()
                                .trim()
                            // if(_selectedRow.type==1){     var point = _selectedRow.name.lastIndexOf(".");
                            //     var type = _selectedRow.name.substr(point)     name = name+type     //
                            // return name }
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

                    // 外发
                    linkDialog = self
                        .find('#linkDialog')
                        .dialog({title: '新建外链'});
                    linkDialog.init();
                    linkDialog
                        .find('#cancel_button')
                        .on('click', function () {
                            linkDialog.hide()
                        });
                    linkDialog
                        .find('#ok_button')
                        .on('click', function () {
                            setLink(_selectedRow)
                            // linkDialog.hide()
                        });
                    // 文件信息
                    infoDialog = self
                        .find('#infoDialog')
                        .dialog({title: '文件详情'});
                    infoDialog.init();
                    fileInfo = infoDialog
                        .find('#fileInfo')
                        .FileInfo();
                    fileInfo.onLoadSuccess = function () {
                        infoDialog.show()
                    };
                    fileInfo.init();
                    // 文件版本信息
                    versionDialog = self
                        .find('#versionDialog')
                        .dialog({title: '文件版本信息'})
                    versionDialog.init();

                    fileVersions = versionDialog
                        .find('#fileVersions')
                        .FileVersions();
                    fileVersions.onLoadSuccess = function () {
                        versionDialog.show()
                    };
                    fileVersions.init();
                    accessControlDialog = self
                        .find('#shareDialog')
                        .ShareDialog()
                    accessControlDialog.onSuccess = datagridLoad
                    accessControlDialog.onLastSharedUserDeleteSuccess = datagridLoad
                    accessControlDialog.init0()
                    _uploader = self
                        .find("#upload_button")
                        .Uploader({'webkitdirectory': false});
                    _uploader.ownerId = _ownerId
                    _uploader.folderId = _folderId
                    _uploader.onUploadSuccess = function () {
                        datagridLoad()
                    }
                    _uploader.init();

                    _uploaders = self
                        .find("#upload_buttons")
                        .Uploader({'webkitdirectory': true});
                    _uploaders.ownerId = _ownerId
                    _uploaders.folderId = _folderId
                    _uploaders.onUploadSuccesss = function () {
                        datagridLoad()
                    }
                    _uploaders.init();

                    _uploadfile = self
                        .find("#upload_file")
                        .Uploader({'webkitdirectory': false});
                    _uploadfile.ownerId = _ownerId
                    _uploadfile.folderId = _folderId
                    _uploadfile.onUploadSuccessfile = function () {
                        datagridLoad()
                    }
                    _uploadfile.init();

                };

                return self
            }
        })
    $(document).ready(function () {
        var page = $(document)['ui.Page']()
        page.init()
    })
})(jQuery)