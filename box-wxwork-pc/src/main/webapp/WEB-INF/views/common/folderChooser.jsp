<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<div id="copyToFolderChooserDialog" style="display:none;width: 500px;/*height:430px*/">
    <div class='tabs' id="tabs">
        <ul class='horizontal'>
            <li rel="tab-1" class="selectActive" id="perTabChoose">
                <i class="fa fa-user" aria-hidden="true"></i>
                <span>个人文件</span>
            </li>
            <li rel="tab-2" id="qulickTabChoose">
                <i class="fa fa-taxi" aria-hidden="true"></i>
                <span>快捷文件</span>
            </li>
            <li rel="tab-3" id="coopTabChoose">
                <i class="fa fa-users" aria-hidden="true"></i>
                <span>协作文件</span>
            </li>
            <li rel="tab-4" id="partTabChoose">
                <i class="fa fa-sitemap" aria-hidden="true"></i>
                <span>部门文件</span>
            </li>
        </ul>
            <div id="tab1" rel='tab-1'>
                <div class="bread-crumb" style="display:block">
                    <div class="bread-crumb-content" id="chooserBreadCrumb">
                        <div id="breadTitleAll">个人文件</div>
                    </div>
                </div>
                <div id="chooserFileListPer"></div>
            </div>
            <div id="tab2" rel='tab-2'>
                <div class="bread-crumb" style="display:block">
                    <div class="bread-crumb-content" id="chooserBreadCrumbQulick">
                        <div id="breadQulickTitleAll">快捷文件</div>
                    </div>
                </div>
                <div id="chooserFileListQulick"></div>
            </div>
            <div id="tab3" rel='tab-3'>
                <div id="chooserFileListCoop"></div>
            </div>
            <div id="tab4" rel='tab-4'>
                <div id="chooserFileListPart"></div>
            </div>
    </div>
    <div class="form-controls" style="text-align: right">
        <button type="button" id="cancelButton">取消</button>
        <button type="button" id="selectFolderButton">确定</button>
    </div>
    <%--文件模板--%>
    <script id="fileItemTemplate" type="text/template7">
        <div class="folder-list-cells" id="chooserFile_{{id}}">
            <div class="folder-list-cell">
                <div class="folder-list-bd">
                    <div class="index-recent-left fileitem-temple">
                        <i class="ico-folder"></i>
                    </div>
                    <div class="index-recent-middle">
                        <div class="recent-detail-name">
                            <p>{{name}}</p>
                        </div>
                        <div class="recent-detail-other">
                            <span>{{menderName}}</span>
                            <span>|</span>
                            <span>{{modifiedAt}}</span>
                            {{#js_compare "this.size == undefined || this.size=='' || this.size==null"}}
                            <span></span>
                            {{else}}
                            <span>|</span>
                            <span>{{size}}</span>
                            {{/js_compare}}
                        </div>
                    </div>
                </div>
            </div>
        </div>

    </script>

    <%--快捷目录项--%>
    <script id="shortcutFileItemTemplate" type="text/template7">
        <div class="folder-list-cells" id="chooserFile_{{id}}">
            <div class="folder-list-cell">
                <div class="folder-list-bd">
                    <div class="index-recent-left fileitem-temple">
                        <i class="ico-folder"></i>
                    </div>
                    <div class="index-recent-middle">
                        <div class="recent-detail-name">
                            <p>{{nodeName}}</p>
                        </div>
                        <div class="recent-detail-other">
                            {{#js_compare "this.type == 1"}}
                            <span>个人文件</span>
                            {{else}}
                            <span>{{ownerName}}</span>
                            {{/js_compare}}
                            <%--
                                                    <span>|</span>
                                                    <span>{{modifiedAt}}</span>
                            --%>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </script>

    <%--协作空间模版--%>
    <script id="spaceTemplate" type="text/template7">
        <div class="folder-list-cells" id="space_{{id}}">
            <div class="folder-list-cell">
                <div class="folder-list-bd">
                    <div class="index-recent-left fileitem-temple">
                        <i class=""><img style="width: 100%" src="${ctx}/static/skins/default/img/space-row-icon.png" /></i>
                    </div>
                    <div class="index-recent-middle">
                        <div class="recent-detail-name">
                            <p>{{name}}</p>
                        </div>
                        <div class="recent-detail-other">
                            <span>{{ownedByUserName}}</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>

    </script>
</div>

<script src="${ctx}/static/js/common/tabs.js"></script>
<script src="${ctx}/static/js/common/folder-chooser.js"></script>
<script>
    tabs_takes.init("tabs");
</script>
