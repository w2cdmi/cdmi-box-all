<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
		<meta http-equiv="Access-Control-Allow-Origin" content="*">
    <title>共享文件</title>
    <%@ include file="../common/include.jsp" %>
    <link rel="stylesheet" href="${ctx}/static/zTree/metroStyle/metroStyle.css" type="text/css">
	<link rel="stylesheet" href="${ctx}/static/css/default/magic-input.min.css" type="text/css">
    <script src="${ctx}/static/zTree/jquery.ztree.core.min.js"></script>
    <script src="${ctx}/static/components/webuploader.js"></script>
    <script src="${ctx}/static/components/Uploader.js"></script>
    <script src="${ctx}/static/components/SharedToolbar.js"></script>
    <script src="${ctx}/static/components/Breadcrumbs.js"></script>
    <script src="${ctx}/static/components/Datagrids.js"></script>
    <script src="${ctx}/static/components/Pagination.js"></script>
    <script src="${ctx}/static/components/SharedFileInfo.js"></script>
    <script src="${ctx}/static/components/SharedFileVersions.js"></script>
    <script src="${ctx}/static/components/SharedSelectFolderDialog.js"></script>
	<script src="${ctx}/static/components/ShareDialog.js"></script>
	<script src="${ctx}/static/js/common/sharedfile-view.js"></script>
    <script src="${ctx}/static/js/share/shareFolderIndex.js"></script>
    <script type="text/javascript">
            <%--var catalogParentId = <c:out value='${parentId}'/>;--%>
			var isLinkHidden = <c:out value='${linkHidden}'/>;
			var canPreview =<%=PreviewUtils.isPreview()%>;
			var reqProtocol = "<%=request.getSession().getAttribute("reqProtocol")%>";
			var parentId = <c:out value='${shareRootId}'/>;
			var shareRootId = <c:out value='${shareRootId}'/>;
			var curOwnerId = <shiro:principal property="cloudUserId"/>;
            var sharedownerId=<c:out value='${ownerId}'/>;
            console.log(sharedownerId);
		</script>
    <style type="text/css">
    #uploadModal {
        width: 630px;
        height: 400px;
        }
        #uploadFinishedModal {
            top: 50%;
            margin-left: -380px;
            width: 800px;
            height: 500px;
        }

        .modal {
            position: fixed;
            bottom: 16px;
            right: 10px;
            z-index: 1050;
            width: 560px;
            margin-left: 40px;
            background-color: #fff;
            -webkit-box-shadow: 0 3px 7px rgba(0, 0, 0, 0.3);
            -moz-box-shadow: 0 3px 7px rgba(0, 0, 0, 0.3);
            box-shadow: 0 3px 7px rgba(0, 0, 0, 0.3);
        }

        #uploadModal .modal-header {
            padding: 15px 15px;
            border-bottom: 1px solid #eee;
            background: #F9F9F9;
        }

        #uploadModal .modal-header h3 {
            font-size: 18px;
            font-weight: normal;
            margin: 0;
            line-height: 30px;
        }

        .modal-body {
            height: 338px;
            overflow-y: auto;
        }

        #uploadQueue {
            padding-bottom: 20px;
        }
        #uploadQueue table>thead>tr>th {
            background: #fff;
            color: #A5A5A5;
        }

        .inneruploadQueue tr {
            width: 0px;
            height: 25px;
            background: url('${ctx}/static/assets/images/pro.png') no-repeat;
            text-align: center;

            font-family: Tahoma;
            font-size: 14px;
            line-height: 25px;
        }
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
        #uploadQueue .uploadlistul {
            padding: 17px 20px;
            border-bottom: 1px solid #e8e8e8;
            color: #A5A5A5;
            font-weight: bold;
            line-height: 3px;
        }

        #uploadQueue .uploadlist ul {
            overflow: hidden;
            padding: 0 20px;
            height: 30px;
            line-height: 30px;
            border-bottom: 1px solid #e8e8e8;
        }

        #uploadQueue .uploadlist li {
            float: left;
        }


        #uploadQueue .uploadlistul li {
            float: left;
        }
        .pb-wrapper {
            position: relative;
            background: #fff;
            border-bottom: 1px solid #EAEAEA;
        }

        .pb-container {
            height: 30px;
            position: relative;
        }

        .pb-text {
            width: 100%;
            /* position: absolute; */
        }

        .pb-value {
            height: 100%;
            width: 0;
            background: #F5F5F5;
            margin-top: -31px;
        }
    </style>
</head>
<body>
<div class="container" style="background: #fff;">
    <jsp:include page="../common/menubar.jsp"/>
    <%@ include file="../common/shareddatagrid.jsp" %>
    <%@ include file="../common/folderChooser.jsp" %>
    <!-- <%@ include file="../common/shareDialog.jsp" %> -->
    <div class="popover bottom" id="table_popover">
        <div class="arrow" style="left: 75px;"></div>
        <dl class="menu">
            <!-- <dt role="0,1" id="share"><i class="fa fa-share-alt"></i>共享</dt> -->
            <!-- <dt role="0,1" id="link"><i class="fa fa-link"></i>外发</dt> -->
            <!-- <dt role="0" id="addShortcutFolder"><i class="fa fa-folder-o"></i>设为快捷目录</dt> -->
            <!-- <dt role="0,1" id="rename"><i class="fa fa-pencil-square-o"></i>重命名</dt> -->
            <dt role="0,1" id="moveTo"><i class="fa fa-files-o"></i>另存到...</dt>
            <dt role="1" id="downFile"><i class="fa fa-download" aria-hidden="true"></i>下载</dt>
            <!-- <dt role="0,1" id="delete"><i class="fa fa-minus"></i>删除</dt> -->
            <!-- <dt role="0" id="folderInfo"><i class="fa fa-info"></i>文件夹信息</dt> -->
            <!-- <dt role="1" id="fileInfo"><i class="fa fa-info"></i>文件信息</dt> -->
            <!-- <dt role="1" id="fileVersionInfo"><i class="fa fa-history"></i>查看版本</dt> -->
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
    
    <div id="renameDialog">
        <form class="form">
            <dl>
                <dt><label>名称：</label><input id="name" name="name" placeholder="请输入文件夹名" style="width: 200px"></dt>
            </dl>
            <div class="form-control" style="text-align: right">
                <button type="button" id="cancel_button">取消</button>
                <button type="button" id="ok_button">确定</button>
            </div>
        </form>
    </div>
    <div id="deleteDialog">
        <p style="padding:10px;text-align: center;height:30px;line-height:30px;font-size:18px;"><i class="fa fa-info-circle"></i>确认要删除吗？</p>
        <div class="form-control" style="text-align: right">
            <button type="button" id="cancel_button">取消</button>
            <button type="button" id="ok_button">确定</button>
        </div>
    </div>
    <div class="modal hide" id="uploadModal" style="display: none;">
        <div class="modal-header">
            <h3>上传列表(<span id="showUploadedNum">0</span> / <span id="showUploadTotalNum">0</span>)
                <i id="closeModal" style="float: right;
                font-size: 23px;
                line-height: 30px;" class="fa fa-times"></i>
            </h3>
        </div>
        <div class="modal-body">
            <div id="uploadQueue">
                <!-- <table>
                <thead>
                    <th>文件名</th>
                    <th>大小</th>
                    <th>状态</th>
                </thead>
                <tbody id="inneruploadQueue">
                </tbody>
            </table> -->
                <ul class="uploadlistul">
                    <li style="width: 50%;">文件名</li>
                    <li style="width: 20%; margin-left: 20px;">大小</li>
                    <li>状态</li>
                </ul>
                <div class="uploadlist">
                </div>
            </div>
        </div>
        
    </div>
</div>
<div id="linkDialog">

</div>
<a id="downloadFile" download style="display:none"></a>
</body>
</html>
