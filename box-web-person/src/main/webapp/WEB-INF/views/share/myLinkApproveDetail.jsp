<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>文件详情</title>
<%@ include file="../common/include.jsp" %>
<link rel="stylesheet" type="text/css"
	href="${ctx}/static/skins/default/css/share/approveLinkDetail.css" />
</head>
<body>
	<div class="weui-form-preview">
		<div class="weui-form-preview__hd">
			<div class="weui-form-preview__item">
				<label class="weui-form-preview__label">名称:</label>
                <span class="weui-form-preview__value" id="fileName"></span>
			</div>
		</div>
		<div class="weui-form-preview__hd" id="sizeLine">
			<div class="weui-form-preview__item">
				<label class="weui-form-preview__label">大小:</label>
                <span class="weui-form-preview__value" id="fileSize"></span>
			</div>
		</div>
		<div class="weui-form-preview__hd" id="typeLine">
			<div class="weui-form-preview__item">
				<label class="weui-form-preview__label">类型:</label>
                <span class="weui-form-preview__value" id="fileType"></span>
			</div>
		</div>
	</div>
	<div class="weui-form-preview">
		<div class="weui-form-preview__hd">
			<div class="weui-form-preview__item">
				<label class="weui-form-preview__label">外链:</label>
                <span class="weui-form-preview__value" id="linkUrl"></span>
			</div>
		</div>
		<div class="weui-form-preview__hd">
			<div class="weui-form-preview__item">
				<label class="weui-form-preview__label">权限:</label>
                <span class="weui-form-preview__value" id="role"></span>
			</div>
		</div>
		<div class="weui-form-preview__hd">
			<div class="weui-form-preview__item">
				<label class="weui-form-preview__label" id="accessmodeText"></label>
				<span class="weui-form-preview__value" id="plainAccessCode"></span>
			</div>
		</div>
		<div class="weui-form-preview__hd">
			<div class="weui-form-preview__item">
				<label class="weui-form-preview__label">有效期:</label>
                <span class="weui-form-preview__value" id="expireTime"></span>
			</div>
		</div>
	</div>
	<div class="page__bd">
		<div class="weui-panel">
			<div class="weui-panel__bd">
				<div class="weui-cell__bd">
					<span>审批记录：</span>
				</div>
			</div>
			<div class="weui-panel__bd">
				<div class="weui-cells" id="recordList">
            <script id="recordTemplate" type="text/template7">
				<div class="approval-details-record">
					<div class="record-content">
						<div>{{id}}</div>
						<div>{{approveAt}}</div>
						<div>{{approveByName}}</div>
						<div>{{status}}</div>
					</div>
				</div>
			</script>
				</div>
			</div>
		</div>
	</div>
	<div class="approval-details-tail">
		<a href="javascript:;" class="approval-details-button-close"
			onclick="gotoPage('${ctx}/share/linkApproveList')">关闭</a>
	</div>
</body>


<script type="text/javascript">
    var roleMsgs = {
        "auther" : "<spring:message code='systemRole.title.auther'/>",
        "editor" : "<spring:message code='systemRole.title.editor'/>",
        "uploadAndView" : "<spring:message code='systemRole.title.uploadAndView'/>",
        "viewer" : "<spring:message code='systemRole.title.viewer'/>",
        "uploader" : "<spring:message code='systemRole.title.uploader'/>",
        "downloader" : "<spring:message code='systemRole.title.downloader'/>",
        "previewer" : "<spring:message code='systemRole.title.previewer'/>",
        "lister" : "<spring:message code='systemRole.title.lister'/>",
        "prohibitVisitors" : "<spring:message code='systemRole.title.prohibitVisitors'/>"
    };
    
    var token = '${token}';
    
    $(function() {
        $.ajax({
            type : "POST",
            data : {
                token : token,
                linkCode : "${linkCode}"
            },
            url : "${ctx}/share/getLinkApproveDetail",
            error : function(request) {
            },
            success : function(data) {
                if(data.linkAndNode.file != undefined) {
                    $("#fileName").text(data.linkAndNode.file.name);
                    $("#fileSize").text(formatFileSize(data.linkAndNode.file.size));
                    $("#sizeLine").show();
                    $("#fileType").text((data.linkAndNode.file.name.split(".")[1]));
                    $("#typeLine").show();
                } else {
                    $("#fileName").text(data.linkAndNode.folder.name);
                    $("#fileSize").text("");
                    $("#sizeLine").hide();
                    $("#fileType").text("");
                    $("#typeLine").hide();
                }
                $("#linkUrl").text(data.linkAndNode.link.url);
                $("#role").text(translateAuthorization(data.linkAndNode.link.role));
                var accessCodeMode = data.linkAndNode.link.accessCodeMode;
                if(accessCodeMode == "static") {
                    if(data.linkAndNode.link.plainAccessCode == undefined || data.linkAndNode.link.plainAccessCode == "") {
                        $("#accessmodeText").text("访问方式：");
                        $("#plainAccessCode").text("匿名访问");
                    } else {
                        $("#accessmodeText").text("提取码访问：");
                        $("#plainAccessCode").text(data.linkAndNode.link.plainAccessCode);
                    }
                } else if(accessCodeMode == "mail") {
                    $("#accessmodeText").text("动态码访问：");
                    $("#plainAccessCode").text(data.linkAndNode.link.plainAccessCode);
                }
                if(data.linkAndNode.link.expireAt != null && data.linkAndNode.link.expireAt != "") {
                    $("#expireTime").text(formatDateTime(data.linkAndNode.link.expireAt));
                } else {
                    $("#expireTime").text("永久有效");
                }
                var recordList = data.approveRecordList;
                var $list = $("#recordList");
                var $template = $("#recordTemplate");
                
                for( var i in recordList) {
                    var item = recordList[i];
                    item.id = parseInt(i) + 1;
                    item.status = translateStatus(item.status);
                    item.approveAt = getFormatDate(new Date(item.approveAt), "yyyy/MM/dd");
                    $template.template(item).appendTo($list);
                }
            }
        });
    });
    
    function translateStatus(status) {
        switch(status || 0) {
            case 2 :
                return "通过";
            case 3 :
                return "驳回";
            default :
                return "";
        }
    }
    function formatDateTime(inputTime) {
        var date = new Date(inputTime);
        var y = date.getFullYear();
        var m = date.getMonth() + 1;
        m = m < 10 ? ('0' + m) : m;
        var d = date.getDate();
        d = d < 10 ? ('0' + d) : d;
        var h = date.getHours();
        h = h < 10 ? ('0' + h) : h;
        var minute = date.getMinutes();
        var second = date.getSeconds();
        minute = minute < 10 ? ('0' + minute) : minute;
        second = second < 10 ? ('0' + second) : second;
        return y + '-' + m + '-' + d + ' ' + h + ':' + minute;
    };
    
    function translateAuthorization(role) {
        switch(role) {
            case "auther" :
                return "编辑 预览 下载";
            case "uploader" :
                return "上传";
            case "viewer" :
                return "预览 下载";
            case "downloader" :
                return "下载";
            case "previewer" :
                return "预览";
            default :
                return "禁止访问";
        }
    }
</script>
</html>