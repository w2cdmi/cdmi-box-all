<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ page import="org.apache.shiro.session.Session" %>
<%@ page import="org.apache.shiro.SecurityUtils" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
    <script src="${ctx}/static/js/public/jquery-1.10.2.min.js" type="text/javascript"></script>
    <link href="${ctx}/static/skins/default/css/public.css" rel="stylesheet" type="text/css"/>
</head>

<body ondblclick="return false;">
<a name="top" id="top"></a>
<div style="position:fixed; height:33px; width:100%; text-align:center;top:0px;background-color: #f2f2f2;border-radius:3px;">
    <input id="topage" type="text" onKeyUp="this.value=this.value.replace(/\D/g,'1')"
           style="margin-top:5px;height:18px;width:29px;text-align: right;font-size:14px;" value=1><span
        style="font-size: 14px;">/<c:out value='${num}'/></span>
    <input type="button" onclick="goPage('<c:out value='${ownerId}'/>','<c:out value='${fileId}'/>')" value="GO"
           style="margin-top:4px;margin-left:5px;;height:20px;width:2.5em;border-radius:3px;border:none;background-color:#008be8;color:#ffffff;cursor:pointer;">
</div>

<div class="arrow-left" onclick="getViewPage('<c:out value='${ownerId}'/>','<c:out value='${fileId}'/>','<c:out
        value='${page}'/>',0)"></div>
<div class="arrow-right" onclick="getViewPage('<c:out value='${ownerId}'/>','<c:out value='${fileId}'/>','<c:out
        value='${page}'/>',1)"></div>

<input type="hidden" id="page" name="page" value="<c:out value='${page}'/>"/>
<input type="hidden" id="num" name="num" value="<c:out value='${num}'/>"/>
<input type="hidden" id="ownerId" name="ownerId" value="<c:out value='${ownerId}'/>"/>
<input type="hidden" id="fileId" name="fileId" value="<c:out value='${fileId}'/>"/>
<div class="body" style="margin-left: -20px;background-color: rgb(39, 39, 39);">
    <img id="showImg" style="margin: 0px auto;display: block;width: 80%;"
         src="${ctx}/views/imageInfo/${ownerId}/${fileId}/${page}"/>


</div>
</body>

<script type="text/javascript">
    function getViewPage(ownerId, fileId, page, flag) {
        var index = $('#page').val();
        var num = $('#num').val();
        if (flag == 0) {
            page = parseInt(index) - 1;
            if (page == 0) {
                page = num;
            }
        } else {
            if (index == num) {
                index = 0;
            }
            page = parseInt(index) + 1;
        }
        $('#page').val(page);
        $('#topage').val(page);
        $('#showImg').attr('src', "${ctx}/views/imageInfo/" + ownerId + "/" + fileId + "/" + page);
        document.getElementById('top').scrollIntoView();
    }

    function goPage(ownerId, fileId) {
        var topage = $('#topage').val();
        var num = $('#num').val();
        if (parseInt(topage) >= parseInt(num)) {
            topage = parseInt(num);
        }
        if (parseInt(topage) == 0) {
            topage = 1;
        }
        $('#page').val(topage);
        $('#topage').val(topage);
        $('#showImg').attr('src', "${ctx}/views/imageInfo/" + ownerId + "/" + fileId + "/" + topage);
        document.getElementById('top').scrollIntoView();
    }


    document.onkeydown = function (event) {
        var e = event || window.event || arguments.callee.caller.arguments[0];

        if (e && e.keyCode == 37) {//��
            var ownerId = $('#ownerId').val();
            var fileId = $('#fileId').val();
            var index = $('#page').val();
            getViewPage(ownerId, fileId, page, 0);
        }

        if (e && e.keyCode == 39) {//��
            var ownerId = $('#ownerId').val();
            var fileId = $('#fileId').val();
            var page = $('#page').val();
            getViewPage(ownerId, fileId, page, 1);
        }
    };


</script>
</html>
