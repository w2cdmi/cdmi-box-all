
(function ($) {
    $.fn.extend({
        FileList:function (options) {
            var self = this;
            /*分页相关定义*/
            var pageSize = getCookie("fileListPageSize", 40);
            /*排序字段，["modifiedAt", "name", "size"]*/
            var orderField = getCookie("orderField", "modifiedAt");
            /*排序方式*/
            var order = getCookie("order", "DESC");
            /*文件列表显示方式：列表或缩略图*/
            var listViewType = getCookie("listViewType", "list");
            /* 正在加载 */
            var __loading = false;

            var __page = 1;
            var __loadmore = false;
            var num = -1;
            if (typeof options === "object") {
               // 类别来源
                this.categoryOrigin = options.categoryOrigin;
                // 用户空间Id
                this.ownerId = options.ownerId || this.ownerId;

                // 空间角色
                this.teamRole = options.teamRole

                // 空间类型
                this.teamType = options.teamType || this.teamType
            }
            // 初始化界面
            self.showListInit = function () {
                //排序字段
                var $nameSort = $("#nameSort");
                var $dateSort = $("#dateSort")
                if (orderField == null || orderField == 'modifiedAt') {
                    if (order == null || order == "DESC") {
                        $dateSort.find(".all-sort-img i").addClass("sort-desc");
                    } else {
                        $dateSort.find(".all-sort-img i").addClass("sort-asc");
                    }
                    orderField = "modifiedAt";
                } else {
                    if (order == null || order == "ASC") {
                        $nameSort.find(".all-sort-img i").addClass("sort-asc");
                    } else {
                        $nameSort.find(".all-sort-img i").addClass("sort-desc");
                    }
                }

                $dateSort.on("tap", function(e) {
                    e.stopPropagation();
                    $("#sortRadio").find(".all-sort-img i").removeClass("sort-asc")
                    $("#sortRadio").find(".all-sort-img i").removeClass("sort-desc")
                    if(order=="DESC"){
                        order = "ASC";
                        $(this).find(".all-sort-img i").addClass("sort-asc")
                    }else{
                        order = "DESC";
                        $(this).find(".all-sort-img i").addClass("sort-desc")
                    }
                    setCookie("order", order);
                    orderField = "modifiedAt";
                    setCookie("orderField", orderField);

                    listFile(parentId, 1);
                });
                $nameSort.on("tap", function(e) {
                    e.stopPropagation();
                    $("#sortRadio").find(".all-sort-img i").removeClass("sort-asc")
                    $("#sortRadio").find(".all-sort-img i").removeClass("sort-desc")
                    if(order=="DESC"){
                        order = "ASC";
                        $(this).find(".all-sort-img i").addClass("sort-asc")
                    }else{
                        order = "DESC";
                        $(this).find(".all-sort-img i").addClass("sort-desc")
                    }
                    setCookie("order", order);
                    orderField = "name";
                    setCookie("orderField", orderField);
                    listFile(parentId, 1);
                });

                //文件列表显示方式
                if (listViewType == "list") {
                    $("#viewTypeBtnList").addClass("active");
                } else {
                    $("#viewTypeBtnThumbnail").addClass("active");
                }

                //为面包屑增加滑动效果
                $("#directory").addTouchScrollAction();

                //为搜索对话框绑定事件
                $("#searchFileInput").on('keypress',function(e) {
                    console.log(e)
                    var keycode = e.keyCode;
                    var keyword = $("#searchFileInput").val();
                    if(keycode=='13' && keyword !== "" && keyword.trim() != "") {
                        e.preventDefault();
                        //请求搜索接口
                        doSearch(keyword.trim());
                    }
                });

                //下拉刷新
                var $listWrapper = $("#fileListWrapper");
                $listWrapper.pullToRefresh().on("pull-to-refresh", function() {
                    //console.log("pulltorefresh triggered...");
                    listFile(parentId,1);
                    setTimeout(function() {
                        $("#fileListWrapper").pullToRefreshDone();
                    }, 200);
                });

                //上滑加载
                $listWrapper.infinite().on("infinite", function() {
                    console.log(123);
                    if(__loading) return;

                    if(__loadmore) {
                        __loading = true;
                        $.showLoading();
                        listFile(parentId, ++__page);
                        setTimeout(function() {
                            __loading = false;
                            $.hideLoading();
                        }, 200);
                    }
                });
                listFile(parentId,1)
            }
            // 文件操作弹出层
            function onPress(e){
                e.stopPropagation()

                var $target = $(e.currentTarget);
                var node = $target.data("node");
                var actions=[];
                if(node.type !=-7){
                    if(self.categoryOrigin == -1){
                        actions.push({
                            text : "另存为...",
                            className : "color-primary",
                            onClick : function() {
                                save2PersonalFile(node);
                            }
                        })
                    }else{
                        if(self.ownerId == curUserId){
                            actions.push({
                                text: "共享",
                                className: "color-primary",
                                onClick: function() {
                                    showShareDialog(node);
                                }
                            });
                        }

                        actions.push( {
                            text: "外发",
                            className: "color-primary",
                            onClick: function() {
                                showLinkDialog(node);
                            }
                        });
                        if (node.type <= 0) {
                            //普通文件夹
                            actions.push({
                                text: "设为快捷目录",
                                className: "color-primary",
                                onClick: function () {
                                    if(self.teamType != undefined){
                                        addShortcutTeamFolder(node)
                                    }else{
                                        addShortcutFolder(node);
                                    }

                                }
                            });
                            actions.push({
                                text: "文件夹信息",
                                className: "color-primary",
                                onClick: function () {
                                    showFileProperties(node);
                                }
                            });
                            if ((self.teamRole == 'admin' || self.teamRole == 'manager') && node.parent == 0) {
                                actions.push({
                                    text: "权限管理",
                                    className: "color-primary",
                                    onClick: function () {
                                        grantAuthority(node);
                                    }
                                });
                            }

                        }
                        actions.push({
                            text: "重命名",
                            className: "color-primary",
                            onClick: function() {
                                renameDialog(node);
                            }
                        });

                        if(self.ownerId == curUserId){
                            actions.push({
                                text: "移动到...",
                                className: "color-primary",
                                onClick: function() {
                                    showMoveToDialog(node);
                                }
                            });
                            actions.push({
                                text: "另存为...",
                                className: "color-primary",
                                onClick: function(e) {
                                    showCopyToDialogTeam(node);
                                }
                            });
                        }else{
                            actions.push({
                                text: "移动到...",
                                className: "color-primary",
                                onClick: function() {
                                    showMoveToDialogForTeamspace(node);
                                }
                            });
                            actions.push({
                                text: "另存为...",
                                className: "color-primary",
                                onClick: function(e) {
                                    showCopyToMeDialog(node);
                                }
                            });
                        }
                        actions.push({
                            text: "删除",
                            className: "color-primary",
                            onClick: function() {
                                deleteFile(node);
                            }
                        });
                        if(node.type==1){
                            actions.push({
                                text: "文件信息",
                                className: "color-primary",
                                onClick: function() {
                                    showFileProperties(node);
                                }
                            });
                            actions.push({
                                text: "查看版本",
                                className: "color-primary",
                                onClick: function() {
                                    optionVersionFile(node);
                                }
                            });
                        }
                    }
                }


                $.actions({
                    title: node.name,
                    actions:  actions
                });
                layelTitle(node.divClass,node.menderName,node.modifiedAt)
            }
            function listFile(folderId, page, callback) {
                parentId = folderId || parentId;
                __page = page || 1;
                var permission = getNodePermission(parentId,ownerId);
                if (permission == null || permission["browse"] == 0) {
                    $.toast("没有权限");
                    return;
                }
                var url = host + "/ufm/api/v2/folders/"+ownerId+"/"+parentId+"/items";
                var flieparams={
                    limit: pageSize,
                    offset: (__page-1)*pageSize,
                    order: [{ field: 'type' , direction: "ASC" },{ field: orderField, direction: order }],
                    thumbnail: [{ width: 96, height: 96 }]
                }
                if (permission != null && permission["browse"] == 1) {
                    $.ajax({
                        type: "POST",
                        url: url,
                        data: JSON.stringify(flieparams),
                        error: handleError,
                        success: function (data) {
                            var fileList = data.folders.concat(data.files);
                            __page = page;
                            __loadmore = data.totalCount > __page * pageSize;
                            var $list = $("#fileList");
                            var $template = $("#fileTemplate");

                            //加载第一页，清除以前的记录
                            if(__page == 1) {
                                //class中带有file-uploading的表示正在上传，刷新时不清除该文件
                                $list.children("div:not([class *= 'file-uploading'])").remove();

                                //表示"文件列表为空的"的div
                                $list.children("div.blank-file-list").remove();
                            }

                            if (fileList.length === 0 && $list.children().length === 0) {
                                showNotFile()
                            } else {
                                $list.parent().css('background', '#fff')
                            }
                            for (var i in fileList) {
                                var item = fileList[i];
                                if(item.type==1){
                                    item.size = formatFileSize(item.size);
                                    if(typeof(item.thumbnailUrlList)!="undefined" && item.thumbnailUrlList.length>0){
                                        item.imgPath = item.thumbnailUrlList[0].thumbnailUrl;
                                    }
                                    if(isImg(item.name)){
                                        num++
                                        var index = item.thumbnailUrlList[0].thumbnailUrl.lastIndexOf("/");
                                        var imgSrc = item.thumbnailUrlList[0].thumbnailUrl.substring(0,index)
                                        item.imgSrc = imgSrc
                                        item.num = num
                                    }
                                }else{
                                    item.size = "";
                                }
                                if(permission.download==1){
                                    item.download = 1;
                                }
                                if(item.type != -7){
                                    item.swipeClass="weui-cell_swiped"
                                }
                                item.modifiedAt = getFormatDate(new Date(item.modifiedAt), "yyyy-MM-dd");
                                item.divClass = getImgHtml(item.type, item.name, item.isShare, item.isSecret);
                                $template.template(item).appendTo($list);


                                //设置数据
                                var $row = $("#file_" + item.id);
                                var $oneRow = $("#files_" + item.id)
                                $row.data("node", item);
                                $oneRow.data("node", item)
                                //增加长按事件
                                $row.on('click', onPress);
                            }
                            num = -1
                            $('.weui-cell_swiped').swipeout()
                        },complete:function(data){
                            //回调
                            if(typeof callback === "function") {
                                callback(data);
                            }
                            $('.load').css('display','none');
                        }
                    });
                } else {
                    $.toast("您没有权限进行该操作", "cancel");
                }
            }
            return self;
        }
    })
})(jQuery)