<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <title>收件箱文件</title>
    <%@ include file="../common/include.jsp" %>
    <link rel="stylesheet" href="${ctx}/static/zTree/metroStyle/metroStyle.css" type="text/css">
    <link rel="stylesheet" href="${ctx}/static/css/default/magic-input.min.css" type="text/css">
    <script src="${ctx}/static/zTree/jquery.ztree.core.min.js"></script>
    <script src="${ctx}/static/components/webuploader.html5only.min.js"></script>
    <script src="${ctx}/static/components/Uploader.js"></script>
    <script src="${ctx}/static/components/InboxToolbar.js"></script>
    <script src="${ctx}/static/components/InboxBreadcrumb.js"></script>
    <script src="${ctx}/static/components/InboxDatagrid.js"></script>
    <script src="${ctx}/static/components/Pagination.js"></script>
    <script src="${ctx}/static/components/FileInfo.js"></script>
    <script src="${ctx}/static/components/FileVersions.js"></script>
    <script src="${ctx}/static/components/SelectFolderDialog.js"></script>
    <script src="${ctx}/static/components/ShareDialog.js"></script>
    <script src="${ctx}/static/js/folder/InboxfolderIndex.js"></script>
    <script type="text/javascript">
        var ownerId = "${ownerId}";
        var teamType=5;	
    </script>
    <style type="text/css">
        .file-info {
            padding: 5px;
            height:40px;
            width: 500px;
            position: relative;
        }
        .file-info > i {
            width:32px;
            height:32px;
            display: inline-block;
        }
        .file-info > .file-name {
            padding: 0 0 5px 10px;
            height:16px;
            line-height: 16px;
            font-size: 18px;
            display: block;
            position: absolute;
            top: 0;
            left: 32px;
        }
        .file-info > .file-size {
            padding:5px 5px 5px 10px;
            height:16px;
            display: block;
            position: absolute;
            top: 16px;
            left: 32px;
            color: #777;
        }
        .info-title {
            height: 20px;
            line-height: 20px;
            border-bottom: 1px solid #ccc;
            color: #777;
            font-weight: bold;
        }

        .info-content {
            padding: 5px;
        }
        .info-content div {
            padding: 5px;
        }
        .user-img {
            width: 32px;
            height: 32px;
            float:left;
        }
        .user-img > img {
            width:32px;
            height:32px;
            display: inline-block;
            border-radius: 50%;
        }

        .user-img > i {
            width:32px;
            height:32px;
            display: inline-block;
        }

        .user-name {
            height:32px;
            line-height: 32px;
            float:left;
        }

        .user-role {
            height:32px;
            line-height: 32px;
            float:right;
        }

        .node_name {
            display:inline-block;
            max-width: 200px;
            overflow: hidden;
            white-space: nowrap;
            text-overflow: ellipsis;
        }
        .fileitem-temple1 > span {
            padding-top: 20px;
        }
    </style>
</head>
<body>
<div class="container" style="background: #fff;">
    <jsp:include page="../common/menubar.jsp"/>
    <%@ include file="../common/Inboxdatagrid.jsp" %>
    <%@ include file="../common/folderChooser.jsp" %>
    <!-- <%@ include file="../common/shareDialog.jsp" %> -->
    <div class="popover bottom" id="table_popover">
        <div class="arrow" style="left: 75px;"></div>
        <dl class="menu">
            <!-- <dt role="0,1" id="share"><i class="fa fa-share-alt"></i>共享</dt>
            <dt role="0,1" id="link"><i class="fa fa-link"></i>外发</dt>
            <dt role="0" id="addShortcutFolder"><i class="fa fa-folder-o"></i>设为快捷目录</dt>
            <dt role="0,1" id="rename"><i class="fa fa-pencil-square-o"></i>重命名</dt> -->
            <dt role="0,1" id="moveTo"><i class="fa fa-files-o"></i>另存到...</dt>
            <dt role="0,1" id="delete"><i class="fa fa-minus"></i>删除</dt>
            <!-- <dt role="0" id="folderInfo"><i class="fa fa-info"></i>文件夹信息</dt>
            <dt role="1" id="fileInfo"><i class="fa fa-info"></i>文件信息</dt>
            <dt role="1" id="fileVersionInfo"><i class="fa fa-history"></i>查看版本</dt> -->
        </dl>
    </div>
    <!-- <div id="createNewFolderDialog">
        <form class="form">
            <dl>
                <dt><label>名称：</label><input id="name" name="name" placeholder="请输入文件夹名" style="width: 200px"></dt>
            </dl>
            <div class="form-control" style="text-align: right">
                <button type="button" id="cancel_button">取消</button>
                <button type="button" id="ok_button">确定</button>
            </div>
        </form>
    </div> -->
    <!-- <div id="renameDialog">
        <form class="form">
            <dl>
                <dt><label>名称：</label><input id="name" name="name" placeholder="请输入文件夹名" style="width: 200px"></dt>
            </dl>
            <div class="form-control" style="text-align: right">
                <button type="button" id="cancel_button">取消</button>
                <button type="button" id="ok_button">确定</button>
            </div>
        </form>
    </div> -->
    <div id="deleteDialog">
        <p style="padding:10px;text-align: center;height:30px;line-height:30px;font-size:18px;"><i class="fa fa-info-circle"></i>确认要删除吗？</p>
        <div class="form-control" style="text-align: right">
            <button type="button" id="cancel_button">取消</button>
            <button type="button" id="ok_button">确定</button>
        </div>
    </div>
    <!-- <div id="infoDialog">
        <div id="fileInfo">
            <div class="file-info">
                <i id="fileIco"></i>
                <span class="file-name" id="fileName"></span>
                <span class="file-size" id="fileSize"></span>
            </div>
            <div class="info-title">外发</div>
            <div class="info-content" id="linkList" style="max-height:200px;overflow: auto;">
               <%-- <div>
                    <p>https://jflksajfkdsajflsakjflksajflksajfksaljfdklsajdfkdasjf</p>
                    <p>匿名访问 | 下载，预览 | 永久</p>
                </div>--%>
            </div>
            <div class="info-title">共享</div>
            <div class="info-content" id="shareList" style="max-height:200px;overflow: auto;">
               <%-- <div class="cl">
                    <div class="user-img">
                        <i class="ico-folder"></i>
                    </div>
                    <div class="user-name">
                        jackson
                    </div>
                    <div class="user-role">
                        预览 | 下载 | 上传
                    </div>
                </div>--%>
            </div> -->
        </div>
    <!-- </div> -->
    <!-- <div id="versionDialog">
        <div id="fileVersions" style="width: 500px;">
            <div class="info-content" style="max-height:200px;overflow: auto;">
                <div class="cl">
                     <div class="user-img">
                         <i id="fileIco"></i>
                     </div>
                     <div class="user-name" id="fileName">
                     </div>
                 </div>
            </div>
            <div class="info-content" id="fileVersionList" style="max-height:200px;overflow: auto;">
            </div>
        </div>
    </div> -->
<!-- </div>
<div id="linkDialog">

</div> -->
<%@ include file="../common/video.jsp" %>
<a id="downloadFile" download style="display:none"></a>
</body>
</html>
