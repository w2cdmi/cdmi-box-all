 <%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="../common/include.jsp" %>
     <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0">
    <title></title>
     <style>
        .preview-reader{
             width: 100%;
             height: 100%;
             position: fixed;
             top: 0;
             bottom: 0;
             background: #fff;
            z-index:99999;
        }
        .preview-reader-ifeame{
             width: 100%;
             height: 100%;
             border: none
        }
     </style>
</head>
<body>
<div class="preview-reader">
    <iframe class="preview-reader-ifeame" id="reader"></iframe>
 </div>
<script>
    (function () {
        var ownerId = "${ownerId}";
        var nodeId = "${nodeId}";
        var linkCode = "${linkCode}";
        var accessCode  = "${accessCode}";
        var name = "${name}";

        function getToken() {
            if(userToken !== undefined && userToken != "") {
                return userToken;
            }

            var token = "link," + linkCode;
            if(accessCode !== "") {
                token = token  + "," + accessCode;
            }

            return token;
        }

        $.ajax({
            type: "GET",
            // async: false,
            url: "/ufm/api/v2/files/" + ownerId + "/" + nodeId + "/preview",
            beforeSend: function (xhr) {
                xhr.setRequestHeader("Authorization", getToken());
            },
            error: function (request) {
                if (request.status == 405) {
                    top.location.reload();
                }
            },
            success: function (data) {
                if (typeof (data) == 'string' && data.indexOf('<html>') != -1) {
                    top.location.reload();
                    return;
                }

                if(name !== null && name !== "") {
                    //URL中可能没有携带文件名，携带name指定文件文件名
                    $("#reader").attr("src", data.url + "&name=" + encodeURIComponent(name));
                } else {
                    $("#reader").attr("src", data.url);
                }
            }
        });

    })();

    initWwJsJdkAndInvoke(function(){
         if(corpId !== undefined && corpId !== "") {
             wx.hideAllNonBaseMenuItem();
         }
     })
</script>
</body>
</html>
