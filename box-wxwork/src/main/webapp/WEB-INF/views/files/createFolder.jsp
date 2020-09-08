<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
    <head>
    <meta http-equiv="Access-Control-Allow-Origin" content="*">
    <%@ include file="../common/include.jsp" %>
    <title>新建文件夹</title>
    <link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/index.css"/>
    </head>
     <body style="background:#f5f5f5">
     <div id="folderChooserDialog" class="folder-chooser-dialog">
        <div class="creat-header">
            <div class="creat-header-content">
                <i class="folder-icon"></i>
                <input type="text" value="新建文件夹" id="newFolderNameInput">
            </div>
        </div>
         <%--文件夹选择--%>
         <%@ include file="../common/folderChooser.jsp" %>
     </div>
        <script src="${ctx}/static/js/files/createFolder.js"></script>
    </body>
</html>
