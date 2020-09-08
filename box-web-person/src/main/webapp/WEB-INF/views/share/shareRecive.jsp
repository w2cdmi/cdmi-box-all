<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
    <!DOCTYPE html>
    <html>

    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>收件箱</title>
        <%@ include file="../common/include.jsp" %>
            <link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/folder/listReciver.css" />
            <link rel="stylesheet" href="${ctx}/static/zTree/metroStyle/metroStyle.css" type="text/css">
            <!-- <script src="${ctx}/static/js/common/line-scroll-animate.js"></script> -->
            <script src="${ctx}/static/zTree/jquery.ztree.core.min.js"></script>
            <script src="${ctx}/static/components/mySelectFolderDialog.js"></script>
            <script src="${ctx}/static/js/common/folder-chooser.js"></script>
            <script src="${ctx}/static/components/Components.js"></script>
            <script src="${ctx}/static/components/Menubar.js"></script>
            <script src="${ctx}/static/jquery-weui/js/clipboard.min.js"></script>
            <script src="${ctx}/static/components/Pagination.js"></script>
            <style>
                .fileitem-temple1>span>h3 {
                    line-height: 40px;
                }
            </style>
    </head>

    <body>
        <%@ include file="../common/folderChooser.jsp" %>
            <%@ include file="../common/menubar.jsp" %>
                <div class="file-view-toolbar">
                    <!-- <div class="load">
				<div class="load-img"><img src="${ctx}/static/skins/default/img/load-rotate.png"/></div>
				<div class="load-text">正在加载</div>
			</div> -->
                    <div class="new-folder-button pull-right"></div>
                </div>
                <div id="toolbar" class="cl">
                    <div class="left">
                        <button id="new-folder-button">
							<i class="fa fa-plus"></i>新建收件箱</button>
                    </div>
                </div>
                <div id="breadcrumd" class="cl">
                    <ol class="breadcrumb">
                        <li class="active">
                            <span class="txt-ellipsis" title="收件箱">收件箱</span>
                        </li>
                    </ol>
                </div>
                <div>
                    <div class="abslayout" style="bottom: 16px;top:150px;right: 0;left: 240px;min-height: 200px">
                        <table style="padding:16px; padding-bottom: 0;padding-top: 0;">
                            <thead>
                                <tr>
                                    <th style="min-width: 280px">文件名</th>
                                    <th style="width: 180px">创建时间</th>
                                </tr>
                            </thead>
                        </table>
                        <div class="abslayout" style="left:16px;right:16px;overflow: auto;bottom: 40px;top:40px;">
                            <table style="padding-top: 0">
                                <tbody id="folders">
                                </tbody>

                            </table>
                            <div class="notfind" style="display: none;height: 18px;">
								<p>暂无数据</p>
							</div>
                            <script id="reciveFolders" type="text/template">
                                <tr types='{{type}}' id='{{id}}' ownedBy='{{ownedBy}}' name='{{name}}' modifiedBys='{{modifiedBy}}' onclick="gotoPage('${ctx}/share/inboxFileList/{{ownerId}}/{{id}}')">

                                    <td style="min-width: 280px">
                                        <!-- <tr id='{{id}}'>
										<td style="min-width: 280px" onclick="gotoPage('${ctx}/share/inboxFileList/{{ownerId}}/{{id}}')"> -->
                                        <div id="typeimgasd" class="fileitem-temple1 cl">
                                            <i class='{{iconimg}}'></i>
                                            <span>
												<h3>{{name}}</h3>
											</span>
                                            <u style="margin-top: 5px;">
												<a id="worker" data="{{id}}">
													<i class="fa fa-ellipsis-h"></i>
												</a>
											</u>
                                        </div>
                                    </td>
                                    <td style="width: 180px">{{createdAt}}</td>
                                    <!-- </tr>
								</td> -->
                                </tr>
                            </script>
                        </div>
                        <div class="abslayout perpagebar" style="left:16px;right:16px;bottom: 0px;" id="pagination">
                            <div class="left" style="display: inline-block">
                                <span id="totalSize">总记录数：0</span>&nbsp;&nbsp;
                                <span id="currentPage">当前页：1</span>&nbsp;&nbsp;
                                <span id="totalPages">总页数：0</span>
                            </div>
                            <div class="right" style="display: inline-block;margin-left: 16px;">
                                <button id="firstPage">首页</button>
                                <button id="prePage">上页</button>
                                <button id="nextPage">下页</button>
                                <button id="lastPage">尾页</button>
                            </div>
                        </div>
                    </div>
                    <div class="popover bottom" id="table_popover">
                        <div class="arrow" style="left: 136px;"></div>
                        <dl class="menu">
                            <dt role="1,2,3," command="1">
								<i class="fa  fa-link"></i>发送外链</dt>
                            <dt role="1,3," command="2">
								<i class="fa fa-minus"></i>删除</dt>
							<dt role="1,2," command="3">
								<i class="fa fa-files-o"></i>另存为到其他空间</dt>
						</dl>
					</div>
					<div id="createNewFolderDialog" style="display:none">
						<form class="form">
							<dl>
								<dt>
									<label>名称：</label>
									<input id="name" name="name" placeholder="请输入收件箱名" style="width: 200px">
								</dt>
                            </dl>
                            <div class="form-control" style="text-align: right">
                                <button type="button" id="cancel_button">取消</button>
                                <button type="button" id="ok_button">确定</button>
                            </div>
                        </form>
                    </div>
                </div>
                <div id="modal-overlay">
                    <div class="modal-data">
                        <span class="closes" onclick="overlay()">
							<i class="fa fa-close"></i>
						</span>
                        <div class="share-homepage-right">
                            <div class="fileitem-temple1 cl">
                                <i class="ico-folder"></i>
                                <span>
									<h3 id="nodeName"></h3>
								</span>
                                <span style="margin-left: 30px;">
									<h3 id="nodeDate"></h3>
								</span>
                            </div>
                        </div>
                        <input type="hidden" id="linkurl">
                        <button id="linkcopy">复制链接</button>
                    </div>
                </div>

                <%-- 文件夹选择 --%>
                    <div class="full-dialog" id="folderChooser" style="display: none">
                        <div class="full-dialog-content">
                            <div class="full-dialog-content-middle">
                                <div class="dialog-title">选择文件夹</div>
                                <div class="bread-crumb full-dialog-nav">
                                    <div class="bread-crumb-content" id="chooserBreadCrumb">
                                        <div onclick="jumpFolder(this, 0);">个人文件</div>
                                    </div>
                                </div>
                            </div>
                            <div id="chooserFileList" class="line-content-father"></div>
                            <div class="full-dialog-tail">
                                <a href="javascript:" class="primary" id="chooserFileOkButton">确定</a>
                                <a href="javascript:" class="default" id="chooserFileCancelButton">取消</a>
                            </div>
                        </div>
                    </div>
                    <script id="chooserFileTemplate" type="text/template7">
                        <div class="line-scroll-wrapper">
                            <div class="file line-content" id="chooserFile_{{id}}">
                                <div class="file-info">
                                    <div class="img folder-icon"></div>
                                    <div class="fileName">{{name}}</div>
                                </div>
                            </div>
                        </div>
                    </script>

                    <%@ include file="../common/footer3.jsp" %>
    </body>
    <script type="text/javascript">
        var modifiedBys;
        var ids;
        var names;
        // var orderField = getCookie("orderField", "modifiedAt");

        var _pageNumber = 1
        var pageSize = getCookie("fileListPageSize", 40);
        var pagination

        //当前目录ID
        var parentId;

        $(function() {
            pagination = $('#pagination').Pagination()
            pagination.init()
            pagination.onPageChange = function(pageNumber) {
                _pageNumber = pageNumber
                listFile()
            }

            initInboxFileList();
        });

        function initInboxFileList() {
            $.ajax({
                type: "GET",
                url: host + "/ufm/api/v2/folders/" + curUserId + "/getInboxFolder",
                error: function(xhr, status, error){
                    var responseObj = $.parseJSON(xhr.responseText);
                    switch(responseObj.code) {
                        case "Forbidden" || "SecurityMatrixForbidden":
                            $.Tost("您没有权限进行该操作");
                            break;
                        case "ExceedUserMaxNodeNum":
                            $.Tost("文件总数超过限制");
                            break;
                        case "RepeatNameConflict":
                            $.Tost("已存在相同的文件夹");
                            break;
                        default:
                            $.Tost(responseObj.message);
                    }
                },
                success: function(data) {
                    parentId = data.id;
                    listFile();
                },
                complete: function() {
                    $('.load').css('display', 'none');
                }
            });
        }

        function listFile(page) {
            var _loading = $.Tost('数据加载中...').show()
            $("#folders").empty();

            var url = host + "/ufm/api/v2/folders/" + curUserId + "/" + parentId + "/items";
            page = page || _pageNumber;
            var params = {
                offset: (page - 1) * pageSize,
                limit: pageSize,
                thumbnail: [{ width: 96, height: 96 }, { width: 250, height: 200 }]
            };
            $.ajax({
                type: "POST",
                url: url,
                data: JSON.stringify(params),
                error: handleError,
                success: function(data) {
                    pagination.setTotalSize(data.totalCount)
                    pagination.setTotalPages(Math.ceil(data.totalCount / pageSize))
                    var fileList = data.folders.concat(data.files);
                    if (fileList.length == 0) {
                        $('.notfind').show();
                    } else {
                        $('.notfind').hide();
                    }
                    _loading.hide();
                    fillFolderDiv(fileList);
                }
            });
        }

        function initLink() {
            $.ajax({
                type: "GET",
                data: {},
                url: "${ctx}/share/getlink/" + ownerId + "/" + ids + "?" + new Date().getTime(),
                error: function(request) {},
                success: function(data) {
                    if (data == undefined || data == "") {
                        createInboxLink();
                    } else {
                        if (data.length > 0) {
                            $('#nodeDate').text(getFormatDate(data[0].modifiedAt));
                            $('#nodeName').text(names);
                            if (data[0].upload == true) {
                                $("#linkurl").val(data[0].url);
                            }
                        }
                    }

                }
            });
        }

        function createInboxLink() {
            var params = {
                accessCodeMode: "mail",
                plainAccessCode: "",
                role: "uploader"
            }
            $.ajax({
                type: "POST",
                url: host + "/ufm/api/v2/links/" + curUserId + "/" + ids,
                data: JSON.stringify(params),
                error: function(request) {},
                success: function(data) {
                    $('#nodeDate').text(getFormatDate(data.modifiedAt));
                    $('#nodeName').text(names);
                    $("#linkurl").val(data.url);
                }
            });
        }

        function worker_Click(e) {
            e.stopPropagation()
        }

        var copyLinkUrl = new Clipboard('#linkcopy', {
            text: function() {
                return $("#linkurl").attr("value");
            }
        });
        copyLinkUrl.on('success', function(e) {
            // alert("复制成功", function() {
            //    });
            $.Tost("复制成功");
            overlay();
            $('#nodeDate').html('');
            $('#nodeName').html('');
        });

        function addBackgroundPictures() {
            if ($('#folders li').length == 0) {
                $('#box').css('background', 'url(' + ctx +
                    '/static/skins/default/img/Documents-received.png) no-repeat center 8.25rem');
                $('#box').css('background-size', '5rem 5rem');
            } else {
                $('#box').css('background', '')
            }
        }

        var selectedRow = null;

        function fillFolderDiv(data) {
            for (var i = 0; i < data.length; i++) {
                var row = data[i];
                row.createdAt = getFormatDate(new Date(row.createdAt), "yyyy-MM-dd");
                row.ownerId = ownerId;
                row.iconimg = getFileIconClass(row.name);
                if (row.type === 0) {
                    row.iconimg = 'ico-folder';
                }
                var reciveFolders = $("#reciveFolders").template(row);
                reciveFolders.appendTo($("#folders"));
                // new Hammer($("#" + row.id)[0]).on('press', onPress);
                $("#" + row.id).data("node", row);
                $('#folders').find('a#worker').on('click', worker_Click)
            }

            $("#folders tr").mouseenter(function() {
                selectedRow = $(this);
                //alert(selectedRow)
            })
            var width = window.screen.width;
            $('.file').width(width);
            $('.line-scroll-wrapper').width(width + $('.line-buttons').width() + 3);
            // $('.file').addLineScrollAnimate();
            addBackgroundPictures();
            $("table tbody td a[id='worker']").each(function() {
                $(this).popover($("#table_popover"), true, "right", function() {

                });
            })
        }

        $(function() {
            var selectFolderDialog = $("body").find('#selectFolderDialog').mySelectFolderDialog();
            selectFolderDialog.onSuccess = function() {
                $.Tost('另存为成功');
            }
            selectFolderDialog.init0(modifiedBys)

            var dialog = $('#createNewFolderDialog').dialog({
                title: "新建收件箱"
            })
            dialog.init();
            $('#new-folder-button').click(function() {
                dialog.show();
                dialog.find('input[name="name"]').val('');
                dialog.find('input[name="name"]').focus();
            })
            $("#createNewFolderDialog").find("form").submit(function(e) {
                e.preventDefault();
                $("#createNewFolderDialog").find('input[name="name"]').blur()
                var regEn = /[`~!@#$%^&*_+<>?:"{},.\/;']/im,
                    regCn = /[·！#￥——：；“”‘、，|《。》？、【】]/im;
                var name = $("#createNewFolderDialog").find('input[name="name"]').val().trim()
                var lastname = name.charAt(name.length - 1);
                var firstname = name.charAt(0);
                if (regEn.test(lastname) || regCn.test(lastname)) {
                    $.Alert("最后一个字符不能以特殊符号结束");
                    return false;
                } else if (regEn.test(firstname) || regCn.test(firstname)) {
                    $.Alert("第一个字符不能以特殊符号开头");
                    return false;
                } else if (name == "") {
                    $.Alert("请输入文件夹名称");
                } else {
                    createFolder(name, function() {
                        dialog.hide();
                    });
                }
            })
            $("#createNewFolderDialog #ok_button").click(function() {
                var regEn = /[`~!@#$%^&*_+<>?:"{},.\/;']/im,
                    regCn = /[·！#￥——：；“”‘、，|《。》？、【】]/im;
                var name = $("#createNewFolderDialog").find('input[name="name"]').val().trim()
                var lastname = name.charAt(name.length - 1);
                var firstname = name.charAt(0);
                if (regEn.test(lastname) || regCn.test(lastname)) {
                    $.Alert("最后一个字符不能以特殊符号结束");
                    return false;
                } else if (regEn.test(firstname) || regCn.test(firstname)) {
                    $.Alert("第一个字符不能以特殊符号开头");
                    return false;
                } else if (name == "") {
                    $.Alert("请输入文件夹名称");
                    return false;
                } else {
                    createFolder(name, function() {
                        dialog.hide();
                    });
                }

            })
            $("#createNewFolderDialog #cancel_button").click(function() {
                dialog.hide();
            })
            // 复制文件
            function showCopyToDialogTeam(node) {
                var folderChooser = $("#copyToFolderChooserDialog").FolderChooser({
                        title:"另存“"+node.name+"”到",
                        exclude: function (r) {
                            return r !== undefined && r.ownedBy === ownerId && r.id === parentId;
                        },
                        callback: function (ownerId, folderId) {
                            copyTo(node,ownerId,folderId,function (data) {
                                    folderChooser.closeDialog()
                                    $.toast("另存成功");
                                    listFile();
                                }
                            )

                        }
                    }
                );

                //加载数据,并显示
                folderChooser.showDialog();
            }
            $("#table_popover dt").mousedown(function() {
                ids = selectedRow.attr("id");
                ownerId = selectedRow.attr("ownedBy");
                names = selectedRow.attr("name");
                modifiedBys = selectedRow.attr("modifiedBys");
                var node = selectedRow.data("node")
                if ($(this).attr("command") == "1") {
                    $('#nodeDate').html('');
                    $('#nodeName').html('');
                    overlay();
                    // gotoPage("${ctx}/share/reciveFolder/" + ownerId + "/" + ids);
                }
                if ($(this).attr("command") == "2") {
                    deleteReceiveFolder(ids);
                }
                if ($(this).attr("command") == "3") {
                    showCopyToDialogTeam(node)
//                    selectFolderDialog.show0(ownerId, ids)
                }
                initLink();
            })
        });

        function overlay() {
            var e1 = document.getElementById('modal-overlay');
            e1.style.visibility = (e1.style.visibility == "visible") ? "hidden" : "visible";
        }

        function createFolder(newName, success) {
            var parameter = {
                name: newName,
                parent: parentId
            };
            $.ajax({
                type: "POST",
                url: host + "/ufm/api/v2/folders/" + curUserId,
                data: JSON.stringify(parameter),
                error: function(request) {
                    var responseObj = $.parseJSON(request.responseText);
                    switch (responseObj.code) {
                        case "Forbidden" || "SecurityMatrixForbidden":
                            $.Alert("您没有权限进行该操作");
                            break;
                        case "ExceedUserMaxNodeNum":
                            $.Alert("文件总数超过限制");
                            break;
                        case "RepeatNameConflict":
                            $.Alert("已存在相同的文件夹");
                            break
                        default:
                            $.Alert('名称不能包含特殊字符');
                    }
                },
                success: function(data) {
                    listFile();
                }
            });
            if (typeof success === "function") {
                success()
            }
        }

        function deleteReceiveFolder(id) {
            $.Confirm("确认删除？", function(onOk) {
                $.ajax({
                    type: "DELETE",
                    url: host + "/ufm/api/v2/nodes/" + curUserId + "/" + id,
                    data: "{}",
                    error: function(request) {
                        $.Tost('操作失败');
                    },
                    success: function(data) {
                        $.Tost("删除成功");
                        listFile();
                    }
                });

            })
        }
    </script>

    </html>