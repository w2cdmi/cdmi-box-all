<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<div class="weui-tab">
    <div class="weui-navbar">
        <div class="weui-navbar__item weui-bar__item--on" href="#tab1" id="perTabChoose" >
            <p>个人文件</p>
        </div>
        <div class="weui-navbar__item" href="#tab2" id="qulickTabChoose" >
            <p>快捷文件</p>
        </div>
        <div class="weui-navbar__item" href="#tab3" id="coopTabChoose" >
            <p>协作文件</p>
        </div>
        <div class="weui-navbar__item" href="#tab4" id="partTabChoose">
            <p>部门文件</p>
        </div>
    </div>
    <div class="weui-tab__bd">
        <div id="tab1" class="weui-tab__bd-item weui-tab__bd-item--active">
            <div class="bread-crumb" style="display:block">
                <div class="bread-crumb-content" id="chooserBreadCrumb">
                    <div id="breadTitleAll">个人文件</div>
                </div>
            </div>
            <div id="chooserFileListPer"></div>
        </div>
        <div id="tab2" class="weui-tab__bd-item">
            <div class="bread-crumb" style="display:block">
                <div class="bread-crumb-content" id="chooserBreadCrumbQulick">
                    <div id="breadQulickTitleAll">快捷文件</div>
                </div>
            </div>
            <div id="chooserFileListQulick"></div>
        </div>
        <div id="tab3" class="weui-tab__bd-item">
            <div id="chooserFileListCoop"></div>
        </div>
        <div id="tab4" class="weui-tab__bd-item">
            <div id="chooserFileListPart"></div>
        </div>
    </div>
    <%--创建文件夹按钮--%>
<%--
    <div class="foot-confirm" id="createNewFolderButton">
        <button class="foot-confirm-btn">创建文件夹</button>
    </div>
--%>
    <%--确定按钮--%>
    <div class="foot-confirm">
        <button class="foot-confirm-btn" id="selectFolderButton">确定</button>
        <button class="foot-cancel-btn" id="cancelButton">取消</button>
    </div>

</div>

<%--文件模板--%>
<script id="fileItemTemplate" type="text/template7">
    <div class="weui-cells" id="chooserFile_{{id}}">
        <div class="weui-cell">
            <div class="weui-cell__bd">
                <div class="index-recent-left">
                    <div class="folder-icon"></div>
                </div>
                <div class="index-recent-middle">
                    <div class="recent-detail-name">
                        <p>{{name}}</p>
                        {{#js_compare "this.isShare == true"}}
                        <i><img src="${ctx}/static/skins/default/img/isShare.png" alt=""></i>
                        {{/js_compare}}
                        {{#js_compare "this.isSharelink == true"}}
                        <i><img src="${ctx}/static/skins/default/img/link_share.png" alt=""></i>
                        {{/js_compare}}
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
    <div class="weui-cells" id="chooserFile_{{id}}">
        <div class="weui-cell">
            <div class="weui-cell__bd">
                <div class="index-recent-left">
                    <div class="folder-icon"></div>
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
    <div class="weui-cells" id="space_{{id}}">
        <div class="weui-cell">
            <div class="weui-cell__bd">
                <div class="index-recent-left coop-part-content">
                    <i class=""><img src="${ctx}/static/skins/default/img/space-row-icon.png" /></i>
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

<script src="${ctx}/static/js/common/line-scroll-animate.js"></script>
<script src="${ctx}/static/js/common/folder-chooser.js"></script>
