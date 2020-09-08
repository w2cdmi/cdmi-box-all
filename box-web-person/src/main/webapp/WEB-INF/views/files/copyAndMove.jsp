<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
<%@ include file="../common/include.jsp" %>
<link href="${ctx}/static/zTree/zTreeStyle.css" rel="stylesheet" type="text/css"/>
<script src="${ctx}/static/zTree/jquery.ztree.all-3.5.min.js" type="text/javascript"></script>
</head>
<body>
    <header class='demos-header'>
      <h1 class="demos-title"><c:out value='${nodeName}'/></h1>
    </header>
    <div class="weui-cells">
      <ul id="treeArea" class="ztree"></ul>
    </div>
    
    <div class="button_sp_area">
        <a href="javascript:;" class="weui-btn weui-btn_mini weui-btn_primary" onclick="submitTree('move')">移动</a>
        <a href="javascript:;" class="weui-btn weui-btn_mini weui-btn_primary" onclick="submitTree('copy')">复制</a>
        <a href="javascript:;" class="weui-btn weui-btn_mini weui-btn_primary">取消</a>
    </div>
</body>
<script type="text/javascript">
var ownerId = '<shiro:principal property="cloudUserId"/>';
var curUserId = '<shiro:principal property="cloudUserId"/>';
var startPoint = "<c:out value='${startPoint}'/>";
var endPoint = "<c:out value='${endPoint}'/>";
var nodeId = "<c:out value='${nodeId}'/>";
var nodeName = "<c:out value='${nodeName}'/>";
var nodeType = "<c:out value='${nodeType}'/>";
var selectNode;

var setting = {
    async: {
        enable: true,
        url: "${ctx}/folders/listTreeNode/" + curUserId,
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
    /*     beforeRename: zTreeBeforeRename,
        onRename: zTreeOnRename */
    }
};
var zNodes = [{
    id: 0,
    name: "<spring:message code='file.index.allFiles'/>",
    ownedBy: curUserId,
    open: true,
    isParent: true
}];

$(document).ready(function () {
    var obj = null;
    var objType = null;
    var desc = "";

  
   /*  document.getElementById("fileDesc").innerHTML = desc; */
    $.fn.zTree.init($("#treeArea"), setting, zNodes);

    var treeObj = $.fn.zTree.getZTreeObj("treeArea");
    var nodes = treeObj.getNodes();
    if (nodes.length > 0) {
        treeObj.selectNode(nodes[0]);
        selectNode=nodes[0];
    }

    $("#treeArea > li > span").click();

    $(document).keydown(function (event) {
        if (event.keyCode == 13) {
            if (window.event) {
                window.event.cancelBubble = true;
                window.event.returnValue = false;
            } else {
                event.stopPropagation();
                event.preventDefault();
            }
        }
    })

   
});


function submitTree(action){
	submitCopyAndMove(action,ownerId,nodeId)
}

function submitCopyAndMove(action, ownerId, idArray, linkCode) {
    var treeObj = $.fn.zTree.getZTreeObj("treeArea");
    var nodes = treeObj.getSelectedNodes();
    var selectFolder =nodes[0].id;
    
    console.debug(nodes[0]);
    var ids = "";
    var requestUrl = "${ctx}/nodes/"+action+"/" + ownerId;
    var params = {
        "destOwnerId": curUserId,
        "ids": nodeId,
        "parentId": selectFolder,
        "token": "<c:out value='${token}'/>",
        "startPoint": startPoint,
        "endPoint": endPoint
    };

    if (linkCode != "" && linkCode != undefined) {
        params = {
            "linkCode": linkCode,
            "destOwnerId": curUserId,
            "ids": ids,
            "parentId": selectFolder,
            "token": "<c:out value='${token}'/>",
            "startPoint": startPoint,
            "endPoint": endPoint
        };
    }
    $.ajax({
        type: "POST",
        url: requestUrl,
        data: params,
        error: function (request) {
          
        },
        success: function (data) {
    
        }
    });
}
function zTreeOnClick(event, treeId, treeNode) {
	/* console.debug(treeNode.id);
    if (treeNode.id != "-1") {
        goFileIndex(treeNode.id);
    }
    $("#treeArea").hide(); */
};
function goFileIndex(nodeId) {
    window.location = "${ctx}/#file/1/" + nodeId;
}

</script>
</html>