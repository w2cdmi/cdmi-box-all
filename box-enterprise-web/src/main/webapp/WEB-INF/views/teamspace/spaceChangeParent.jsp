<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%
    request.setAttribute("token", CSRFTokenManager.getTokenForSession(session));
%>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="../common/common.jsp" %>
    <link href="${ctx}/static/zTree/zTreeStyle.css" rel="stylesheet" type="text/css"/>
    <script src="${ctx}/static/zTree/jquery.ztree.all-3.5.min.js" type="text/javascript"></script>
    <%@ include file="../common/messages.jsp" %>
    <%@ include file="../files/commonForTreeCreateNode.jsp" %>
</head>
<body>
<div class="alert alert-prompt"><strong><spring:message code='common.tip'/></strong><spring:message
        code='file.info.changeParent'/></div>
<div class="zTreeDemoBackground left">
    <ul id="treeArea" class="ztree"></ul>
</div>

<script type="text/javascript">
    var setting = {
        async: {
            enable: true,
            url: "${ctx}/folders/listTreeNode/<c:out value='${ownerId}'/>",
            otherParam: {"token": "<c:out value='${token}'/>"},
            autoParam: ["id", "ownedBy"]
        },
        view: {
            selectedMulti: false
        },
        edit: {
            drag: {
                isMove: false,
                isCopy: false
            },
            enable: true,
            showRemoveBtn: false,
            showRenameBtn: false
        },
        callback: {
            onClick: zTreeOnClick,
            beforeRename: zTreeBeforeRename,
            onRename: zTreeOnRename
        }
    };
    var zNodes = [{
        id: 0,
        name: "<spring:message code='teamSpace.title.teamSpace'/>",
        ownedBy: "<c:out value='${ownerId}'/>",
        open: true,
        isParent: true
    }];

    $(document).ready(function () {
        $.fn.zTree.init($("#treeArea"), setting, zNodes);

        var treeObj = $.fn.zTree.getZTreeObj("treeArea");
        var nodes = treeObj.getNodes();
        if (nodes.length > 0) {
            treeObj.selectNode(nodes[0]);
        }

        $("#treeArea > li > span").click();
    });

    function doRestore(ownerId, noParentIds) {
        var treeObj = $.fn.zTree.getZTreeObj("treeArea");
        var nodes = treeObj.getSelectedNodes();
        var selectFolder = nodes[0].id;
        var requestUrl = "${ctx}/trash/changeParent";
        $.ajax({
            type: "POST",
            url: requestUrl,
            data: {
                'ownerId': ownerId,
                'ids': noParentIds,
                'parentId': selectFolder,
                'token': "<c:out value='${token}'/>"
            },
            error: function (request) {
                if (request.responseText == "NoSuchParentException") {
                    top.handlePrompt("error", "<spring:message code='error.notfound'/>");
                } else {
                    top.handlePrompt("error", "<spring:message code='operation.failed'/>");
                }
                top.ymPrompt.close();
            },
            success: function () {
                top.ymPrompt.close();
                top.listRecycler(1, top.catalogParentId);
                top.handlePrompt("success", "<spring:message code='operation.success'/>");
            }
        });
    }

</script>
</body>
</html>
