/* 目录方法选择器 */

/**
 * option: {
 *   title: 对话框标题
 *   ownerId: 0,
 *   exclude: function, 黑名单, 返回false时，对应的目录不显示
 *   callback: function
 *   createNewFolder: true/false  //TODO: 未支持
 *   showFile: true/false; //TODO: 未支持
 * }
 */
(function ($) {
    $.fn.extend({
        FolderChooser: function (options) {
            var self = this;
            //当前路径
            this.pwd = 0;

            //
            this.ownerId = curUserId;

            //选择界面中是否显示"创建新的文件夹" 按键
            this.createNewFolder = false;

            this.title = "";
            //
            if (typeof options === "number") {
                //未指定用户，使用当前用户
                this.ownerId = options;
            }

            if (typeof options === "object") {
                this.title = options.title || this.title;
                this.ownerId = options.ownerId || this.ownerId;

                //隐藏的节点
                this.exclude = options.exclude;

                //点击确定后回调函数
                this.callback = options.callback;

                this.createNewFolder = options.createNewFolder || this.createNewFolder;

            }

            //dialog
            self.dialog = self.dialog({title:this.title})
            //初始化界面
            self.showDialog = function () {
                initialized()
                //初始化界面
                listFolder(self.pwd, 0);
                switchTabOne()
                self.init();
                self.show();
            };
            self.closeDialog = function () {
                self.parents(".dialog").hide()
            }
            function switchTabOne() {
                self.find("#perTabChoose").addClass("selectActive")
                self.find("#perTabChoose").siblings().removeClass("selectActive")
                self.find("#tab1").show();
                self.find("#tab2").hide();
                self.find("#tab3").hide();
                self.find("#tab4").hide();
            }
            function switchTab(i) {
                if(i === 1) {
                    self.find("#tab1").show();
                    self.find("#tab2").hide();
                    self.find("#tab3").hide();
                    self.find("#tab4").hide();
                } else if(i === 2) {
                    self.find("#tab1").hide();
                    self.find("#tab2").show();
                    self.find("#tab3").hide();
                    self.find("#tab4").hide();
                } else if(i === 3) {
                    self.find("#tab1").hide();
                    self.find("#tab2").hide();
                    self.find("#tab3").show();
                    self.find("#tab4").hide();
                } else if(i === 4) {
                    self.find("#tab1").hide();
                    self.find("#tab2").hide();
                    self.find("#tab3").hide();
                    self.find("#tab4").show();
                }
            }
            function activeTab(li) {
                li.addClass("selectActive");
                li.siblings().removeClass("selectActive")
            }
            function initialized() {
                //为个人空间面包屑增加滑动效果
                // self.find("#chooserBreadCrumb").addTouchScrollAction();

                /*面包屑第一个节点标签点击跳转*/
                var $root = $("#breadTitleAll");
                //删除上次选择过的面包屑
                $root.nextAll().remove();
                //增加点击跳转功能。
                $root.off("click").on("click", function () {
                        $(this).nextAll().remove();
                        listFolder(0, 0)
                    }
                );

                //个人空间
                self.find("#perTabChoose").off("click").on("click", function () {
                    //个人空间的ownerId为当前登录用户id
                    self.ownerId = curUserId;
                    activeTab($(this))
                    switchTab(1);
                    listFolder(0, 0)
                    $("#breadTitleAll").nextAll().remove();
                });

                //快捷目录
                self.find("#qulickTabChoose").off("click").on("click", function () {
                    self.ownerId = curUserId;
                    self.pwd = -1;
                    activeTab($(this))
                    switchTab(2);
                    getShortcutFolder()
                });

                self.find("#breadQulickTitleAll").off("click").on("click", function () {
                    $(this).nextAll().remove();
                    self.ownerId = curUserId;
                    self.pwd = -1;
                    getShortcutFolder()
                });

                //协作空间
                self.find("#coopTabChoose").off("click").on("click", function () {
                    self.ownerId = curUserId;
                    self.pwd = -1;
                    activeTab($(this))
                    switchTab(3);
                    getCoopList(0)
                });

                //部门空间
                self.find("#partTabChoose").off("click").on("click", function () {
                    self.ownerId = curUserId;
                    self.pwd = -1;
                    activeTab($(this))
                    switchTab(4);
                    getCoopList(1)
                });

                //
                self.find("#selectFolderButton").off("click").on("click", function () {

                    if(self.pwd === -1) {
                        $.Alert("请选择一个有效的目录。");
                        return;
                    }

                    if(typeof self.callback === "function") {
                        self.callback(self.ownerId, self.pwd);
                    }

                });

                //
                self.find("#cancelButton").off("click").on("click", function () {
                    $(this).parents(".dialog").hide()
                });
            }

            function changeOwnerId(ownerId) {
                self.ownerId = ownerId;
            }

            /* 从当前目录下进入子目录*/
            function listFolder(folderId, type, callback) {
                self.pwd = folderId;
                var url = host + "/ufm/api/v2/folders/" + self.ownerId + "/" + self.pwd + "/items";
                $.ajax({
                    type: "POST",
                    url: url,
                    data: JSON.stringify({
                        type:0
                    }),
                    error: handleError,
                    success: function (data) {
                        var data = data.folders;
                        if (type == 0) {
                            var $list = self.find("#chooserFileListPer");
                            var $template = self.find("#fileItemTemplate");
                        } else if (type == 1) {
                            var $list = self.find("#chooserFileListQulick");
                            var $template = self.find("#fileItemTemplate");
                        } else if (type == 2) {
                            var $list = self.find("#chooserFileListCoop");
                            var $template = self.find("#fileItemTemplate");
                        } else if (type == 3) {
                            var $list = self.find("#chooserFileListPart");
                            var $template = self.find("#fileItemTemplate");
                        }
                        $list.children().remove();

                        for (var i in data) {
                            var item = data[i];

                            //删除需要隐藏的节点
                            if((typeof self.exclude === "function") && self.exclude(item)) {
                                continue;
                            }

                            item.modifiedAt = getFormatDate(item.modifiedAt);
                            $template.template(item).appendTo($list);
                            var $row = self.find("#chooserFile_" + item.id);
                            $row.data("node", item);
                            $row.on("click", function() {
                                var node = $(this).data("node");
                                var nodePermission = getNodePermission(node.ownedBy,node.id);
                                if(nodePermission["authorize"] != 1) {
                                    $.Alert('您没有该目录的权限');
                                    return;
                                }else{
                                    // $.showLoading();
                                    enterChildFolder(node, type);
                                }
                            });
                        }

                        //回调
                        if(typeof callback === "function") {
                            callback(data);
                        }
                    },complete:function(){
                        $.hideLoading();
                    }
                });
            }

            /* 点击目录进入 */
            function enterChildFolder(node,type) {
                listFolder(node.id, type, function() {
                    self.pwd = node.id;
                    var $crumbP = $("<p class='bread-arrow-right'></p>")
                    var $crumb = $("<div id='breadTitle_"+ node.id +"'>" + node.name + "</div>");
                    $crumb.data("folderId", node.id);
                    $crumb.on("click", function() {
                        var $this = $(this);
                        $this.nextAll(["div"]).remove();
                        $this.nextAll(["p"]).remove();
                        listFolder($this.data("folderId"), type);
                    });
                    if(type==1){
                        $crumbP.appendTo(self.find("#chooserBreadCrumbQulick"));
                        $crumb.appendTo(self.find("#chooserBreadCrumbQulick"));
                    }else if(type==0){
                        $crumbP.appendTo(self.find("#chooserBreadCrumb"));
                        $crumb.appendTo(self.find("#chooserBreadCrumb"));
                    }else{
                        $crumbP.appendTo(self.find("#chooserBreadCrumbCoop"));
                        $crumb.appendTo(self.find("#chooserBreadCrumbCoop"));
                    }
                });
            }

            // 加载快捷目录
            function getShortcutFolder() {
                self.find("#breadQulickTitleAll").nextAll().remove();
                var url = host + "/ufm/api/v2/folders/" + ownerId + "/shortcut/list";
                $.ajax({
                    type: "POST",
                    url: url,
                    data: {},
                    error: handleError,
                    success: function (data) {
                        var $list = self.find("#chooserFileListQulick");
                        var $template = self.find("#shortcutFileItemTemplate");
                        $list.children().remove();
                        for (var i in data) {
                            var item = data[i];
                            // item.id = item.nodeId
                            // item.ownedBy = item.ownerId;
                            $template.template(item).appendTo($list);

                            var $row = self.find("#chooserFile_" + item.id);
                            $row.data("node", item);
                            $row.on("click", function() {
                                var node = $(this).data("node");

                                node.id = node.nodeId
                                node.ownedBy = node.ownerId;
                                var nodePermission = getNodePermission(node.ownedBy,node.id);
                                if(nodePermission["authorize"] != 1) {
                                    $.toast('您没有该目录的权限','text');
                                    return;
                                }else{
                                    changeOwnerId(node.ownedBy);
                                    listFolder(node.id, 1);
                                    var $crumbP = $("<p class='bread-arrow-right'></p>")
                                    var $crumb = $("<div id='breadTitle_"+ node.id +"'>" + node.nodeName + "</div>");
                                    $crumb.data("folderId", node.id);
                                    $crumb.on("click", function() {
                                        var $this = $(this);
                                        $this.nextAll(["div"]).remove();
                                        $this.nextAll(["p"]).remove();
                                        listFolder(node.id, 1)
                                    });
                                    $crumbP.appendTo(self.find("#chooserBreadCrumbQulick"));
                                    $crumb.appendTo(self.find("#chooserBreadCrumbQulick"));
                                }
                            });
                        }
                    }
                });
            }

            // 加载协作空间部门空间列表
            function getCoopList(type) {
                self.find("#chooserBreadCrumbCoopDiv").remove();
                var url = host+"/ufm/api/v2/teamspaces/items";
                var params = {
                    type: type,
                    userId: curUserId
                };
                $.ajax({
                    type: "POST",
                    url: url,
                    data: JSON.stringify(params),
                    error: function(xhr, status, error) {
                    },
                    success: function(data) {

                        //清空现有的列表
                        if(type==0){
                            var $list = self.find("#chooserFileListCoop");
                        }else if(type==1){
                            var $list = self.find("#chooserFileListPart");
                        }
                        $list.children().remove();

                        catalogData = data.memberships;

                        var $spaceTemplate = self.find("#spaceTemplate");
                        for(var i in catalogData) {
                            var space = catalogData[i].teamspace;
                            $spaceTemplate.template(space).appendTo($list);
                            var $row = self.find("#space_" + space.id);
                            $row.data("obj", space);
                            $row.on("click",function () {
                                var node = $(this).data("obj");
                                changeOwnerId(node.id);
                                var html="";
                                html += "<div class=\"bread-crumb\" id='chooserBreadCrumbCoopDiv' style=\"display:block\">\n" +
                                    "                        <div class=\"bread-crumb-content\" id=\"chooserBreadCrumbCoop\">\n" +
                                    "                            <div id=\"breadTeamSpaceTitleAll\">"+node.name+"</div>\n" +
                                    "                        </div>\n" +
                                    "                    </div>";

                                if (type === 0) {
                                    self.find("#chooserBreadCrumbCoopDiv").remove();
                                    self.find("#tab3").prepend(html);
                                    listFolder(0, 2);

                                    self.find("#breadTeamSpaceTitleAll").click(function () {
                                        $(this).nextAll().remove();
                                        changeOwnerId(node.id);

                                        listFolder(0, 2);
                                    });
                                } else if (type === 1) {
                                    self.find("#chooserBreadCrumbCoopDiv").remove();
                                    self.find("#tab4").prepend(html);
                                    listFolder(0, 3);

                                    self.find("#breadTeamSpaceTitleAll").click(function () {
                                        $(this).nextAll().remove();
                                        changeOwnerId(node.id);

                                        listFolder(0, 3);
                                    });
                                }
                            })
                        }
                    }
                });
            }

            return this;
        }
    })
})(jQuery);