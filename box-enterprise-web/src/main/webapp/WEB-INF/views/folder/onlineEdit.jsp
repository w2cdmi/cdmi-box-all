<%@ page import="com.huawei.sharedrive.cloudapp.utils.CSRFTokenManager" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%
    response.setHeader("Cache-Control", "no-store");
    response.setHeader("Pragrma", "no-cache");
    response.setDateHeader("Expires", 0);
    request.setAttribute("token", CSRFTokenManager.getTokenForSession(session));
%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
    <script type="text/javascript" src="${ctx}/static/js/public/jquery-1.10.2.min.js"></script>
    <script type="text/javascript" src="${ctx}/static/iWebOffice2015/WebOffice.js"></script>
    <script type="text/javascript">
        var WebOffice = new WebOffice2015();


        window.onbeforeunload = function () {
            WebOffice.WebClose();
            WebOffice.obj.FileSystem.ClearDirectory(WebOffice.FilePath);
        }
        function OnReady() {
            window.onload = function () {
                load();
            }

        }

        function load() {

            try {
                WebOffice.setObj(document.getElementById('WebOffice2015'));//给2015对象赋值
                WebOffice.RecordID = "${fileId}";
                WebOffice.UserName = "userName";
                WebOffice.Skin('purple');
                WebOffice.HookEnabled();
                WebOffice.SetUser("userName");
                WebOffice.AppendMenu(1, "保存到云盘");
                WebOffice.AppendMenu(2, "保存到本地");
                WebOffice.obj.Caption = "";

                if (WebOffice.WebOpen()) {
                    WebOffice.setEditType("3");
                    WebOffice.VBASetUserName(WebOffice.UserName);
                    WebOffice.version = getEditVersion();
                    WebOffice.AddToolbar();
                    WebOffice.ShowCustomToolbar(false);
                    StatusMsg(WebOffice.Status);
                }

                $.ajax({
                    type: "GET",
                    async: false,
                    url: "${ctx}/files/getDownloadUrl/" + ${ownerId} +"/" + ${fileId} +"?" + Math.random(),
                    error: function (request) {
                        alert("<spring:message code='fileopen.error.notice'/>");
                        // downloadFileErrorHandler(request);
                    },
                    success: function (url) {

                        var paths = url.split("/");
                        WebOffice.FileName = decodeURIComponent(paths[paths.length - 1]);
                        WebOffice.WebURL = url;
                        WebOffice.FileType = WebOffice.FileName.split(".")[1];
                        var http = WebOffice.obj.Http;
                        http.Clear();
                        if (http.Open(WebOffice.HttpMethod.Get, url, false)) {
                            if (http.Send()) {
                                http.Clear();
                                if (WebOffice.hiddenSaveLocal(http, WebOffice, false, false)) {
                                    WebOffice.OpenLocalFile(WebOffice.TmpFile);
                                }
                            }
                        }
                    }
                });
            } catch (e) {
                alert(e.description);
            }
        }


        function OnCommand(ID, Caption, bCancel) {
            switch (ID) {
                case 1: {
                    saveRevision();
                    break;
                }
                    ;//保存到服务器
                case 2: {
                    WebOffice.obj.ShowDialog(3);
                    break;
                }
                    ;//保存到本地
                default:
                    ;
                    return;
            }

        }


        function saveRevision() {
            var Num = "";
            for (var i = 0; i < 6; i++) {
                Num += Math.floor(Math.random() * 10);
            }
            var file = WebOffice.obj.ActiveDocument;
            var FileName = WebOffice.FileName;
            //保存一个临时文件 ，上传后删除
            var filePath = WebOffice.FilePath + "tempName" + Num + WebOffice.FileName;
            WebOffice.FileType = FileName.split(".")[1];

            //WebOffice.WebSaveLocalFile(filePath);


            //var tempfilePath = WebOffice.FilePath +"temp\\"+WebOffice.FileName;
            WebOffice.WebSaveLocalFile(filePath);
            var fileSize = WebOffice.obj.FileSystem.GetFileSize(filePath);
            //  WebOffice.OpenLocalFile(filePath);

            // alert(file.name);
            var params = {
                "ownerId": ${ownerId},
                "parentId": ${parentId},
                "name": FileName,
                "size": fileSize,
                "token": "<c:out value='${token}'/>"
            };
            $.ajax({
                type: "POST",
                url: "${ctx}/files/preUpload",
                async: false,
                data: params,
                success: function (urlData) {

                    if (urlData.length < 10 || urlData.length > 500) {
                        isSessionTimeout = true;
                        alert("<spring:message code='fileopen.error.notice'/>");

                        /**                        ymPrompt.alert({
							title: "<spring:message code='common.tip'/>",
							message: "<spring:message code='error.timeout.retry'/>",
							handler: function () {
								window.top.location.reload();
							}
						});**/
                    } else {

                        var url = urlData + "?objectLength=" + fileSize;
                        alert(url);
                        WebOffice.WebURL = url;
                        var http = WebOffice.obj.Http;
                        http.Clear();
                        http.AddFile("file", filePath);
                        if (http.Open(WebOffice.HttpMethod.Post, url, false)) {
                            if (http.Send()) {
                                //alert("send:"+http.ErrorCode+"-"+http.StatusText);
                                http.Clear();
                                if (http.Status == 200) {
                                } else {
                                    alert("<spring:message code='fileopen.error.notice'/>");
                                }
                                WebOffice.WebDelFile(filePath);


                                WebOffice.WebDelFile(WebOffice.TmpFile);
                            }


                        }

                    }
                },
                error: function () {
                    alert("<spring:message code='fileopen.error.notice'/>");
                }
            });
        }
    </script>

    <script language="JavaScript" for="WebOffice2015" event="OnReady()">
        load();
        //WebOffice.setObj(document.getElementById('WebOffice2015'));//给2015对象赋值
        /* 	 try{
         WebOffice.setObj(document.getElementById('WebOffice2015'));//给2015对象赋值
         WebOffice.RecordID = "${fileId}";
         WebOffice.UserName = "userName";
         WebOffice.Skin('purple');
         WebOffice.HookEnabled();
         WebOffice.SetUser("userName");
         WebOffice.AppendMenu(1, "保存文件");

         if (WebOffice.WebOpen()) {
         WebOffice.setEditType("3");
         WebOffice.VBASetUserName(WebOffice.UserName);
         WebOffice.version = getEditVersion();
         WebOffice.AddToolbar();
         WebOffice.ShowCustomToolbar(false);
         StatusMsg(WebOffice.Status);
         }

         $.ajax({
         type: "GET",
         async: false,
         url: "${ctx}/files/getDownloadUrl/" + ${ownerId} +"/" + ${fileId} +"?" + Math.random(),
         error: function (request) {
         downloadFileErrorHandler(request);
         },
         success: function (url) {

         var paths = url.split("/");
         WebOffice.FileName = decodeURIComponent(paths[paths.length - 1]);
         WebOffice.WebURL=url;


         var http = WebOffice.obj.Http;
         http.Clear();
         if (http.Open(WebOffice.HttpMethod.Get, url, false)) {
         if (http.Send()) {
         http.Clear();
         if (WebOffice.hiddenSaveLocal(http, WebOffice, false, false)) {
         WebOffice.OpenLocalFile(WebOffice.FilePath + WebOffice.FileName);
         }
         }
         }
         }
         });
         }catch(e){
         alert(e.description);
         } */
    </script>

    <script language="JavaScript" for="WebOffice2015" event="OnOLECommand(OLEFlag, bCancel)">
        switch (OLEFlag) {
            case 4: {
                saveRevision();
                break;
            }

        }
    </script>

    <script language="JavaScript" for="WebOffice2015" event="OnCommand(wID, bstrCaption, bCancel)">
        switch (wID) {
            case 1: {
                saveRevision();
                break;
            }
                ;//保存到服务器
            case 2: {
                WebOffice.obj.ShowDialog(3);
                alert("<spring:message code='file.save.to.local'/>");

                /**  ymPrompt.alert({
							title: "<spring:message code='common.tip'/>",
							message: "<spring:message code='error.timeout.retry'/>",
							handler: function () {
								window.top.location.reload();
							}
						});**/
                /* 	window.opener=null;
                 window.open=('about:blank','_self');
                 window.close(); */
                break;
            }
                ;//保存到本地
        }
    </script>

</head>
<body style="margin:10px;">
<script type="text/javascript">
    var str = '';
    //var copyright = "金格科技iWebOffice2015智能文档中间件[演示版];V5.0S0xGAAEAAAAAAAAAEAAAAHYBAACAAQAALAAAAMhgYPyhG1JeK32w+MoaJsdQXm18HWUbGpjMu4KXjsDecIGSwVMaepwqsjDH53vbZvAjCfYEjWLCInoMmbAfZhyeikyNJa0go8ItDHo9Wp9O791h7K68woxEV7CrX0HQkrE2Vw32FeR/evBr2XJHlOlsrV1UI/SUbN2VTMclIJrh0qlmvNT4NRrSfXsFM7s2eADGjNRraH03Kf2/T8lyE9Xw3kttPJbcSgkyqSqSzJJdFS5bxIWLkdR7HleoNtXAfSplHtE7qjAgNnyfJw04j1f6jQHW6e75jsKujDrBIJk8R5zTB6pFv2ekPouTiV4LhgvdH7MjRcwzwP93W8zRTUyt/z3/pEYxx+YYPaGN/IVl6zYBEyXIl9yr+Fn48wPaEDLrbJuQTIcQbV0ZnUqPg2Q7FDS9gzX+y7wLHQtsgi2cDuf4SQne9E6IQ0KW29Z0UqCBMAN+L+z0qT0NEGgfLB897ZnvbePB4gkM0A+CrJaWbTM2W/lyQYRh3NjoZBGNvrL/OR3c8ksq/sua1/w2UEA=";
    var copyright = "中国银行股份有限公司天津市分行[专用];V5.0S0xGAAEAAAAAAAAAEAAAAHoBAACAAQAALAAAAESWW57wEhTLG740vZgaLuXaaegQJ7qhQtQkHjoaG6Pqy+n1GLSyTfppESR/5poBgXz9/5JXU+Zl13hqg7jERD49UcdyV9AHw+gOoDIX/KHJQ/kw4nErxfJK2lb0WjYGS5XlBV5Ya1jUgzMqO0nT2DZh+jas2AmcRIUZ3IpnVlO7zf6ftrTyNwGbWXKwARoMHAbs9KbTaT4JBnbcDykgvgkh3jIoiXcPcitrUvOc5Krt2rC3Bx802om/SSxeXNrZjxAbfaE+8JGaRlzMAZgU+d7LY6cTerkQELyxZwufPJ6wAkf27UOXRb0uUHsT+hh1iTEMg9xBRa0j3aV+tiYUlgLzUJ38bZP31872zrOIArlhGFVy/eYGuAJ2o/IzbGzOc0xa1Og5XcqSTnS9tq+/WA3ILTegQmDsOFHLse2ZMZedGE9LzGBXqgiID8bLIhcizH7S/2cKe2ESVsrkjLs0rCX5WGLMF0fdO7xHBbVPd8pNnSdZ2tEeOaRNQTsCeubrgFA2JyOdVhoSXB+/DnilBMA=";

    str += '<object id="WebOffice2015" ';

    str += ' width="100%"';
    str += ' height="' + (window.screen.height - 200) + '"';
    if ((window.ActiveXObject != undefined) || (window.ActiveXObject != null) || "ActiveXObject" in window) {
        str += ' CLASSID="CLSID:D89F482C-5045-4DB5-8C53-D2C9EE71D025"  codebase="${ctx}/static/iWebOffice2015/iWebOffice2015.cab#version=12,3,0,410"';
        str += '>';
        str += '<param name="Copyright" value="' + copyright + '">';
    }
    else {
        str += ' progid="Kinggrid.iWebOffice"';
        str += ' type="application/iwebplugin"';
        str += ' OnCommand="OnCommand"';
        str += ' OnReady="OnReady"';
        str += ' OnOLECommand="OnOLECommand"';
        str += ' OnExecuteScripted="OnExecuteScripted"';
        str += ' OnQuit="OnQuit"';
        str += ' OnSendStart="OnSendStart"';
        str += ' OnSending="OnSending"';
        str += ' OnSendEnd="OnSendEnd"';
        str += ' OnRecvStart="OnRecvStart"';
        str += ' OnRecving="OnRecving"';
        str += ' OnRecvEnd="OnRecvEnd"';
        str += ' OnRightClickedWhenAnnotate="OnRightClickedWhenAnnotate"';
        str += ' OnFullSizeBefore="OnFullSizeBefore"';
        str += ' OnFullSizeAfter="OnFullSizeAfter"';
        str += ' Copyright="' + copyright + '"';
        str += '>';
    }
    str += '</object>';
    document.write(str);
</script>
</body>
</html>