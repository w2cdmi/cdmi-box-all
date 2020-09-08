<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<%@ include file="../common/include.jsp"%>
<link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/share/approveLinkDetail.css">
<title>审批详情</title>
</head>
<body>
	<div class="approval-details">
		<div class="approval-details-blank"></div>
		 <div style="height:4rem">
		   <div style="height: 2.4rem ;width: 2.4rem;margin: 0 auto" >
		     <div id="iconDiv" class="img" ></div>
		   </div>
		 </div>
		<div class="approval-details-header">
		    <div class="approval-details-title">
				<div class="details-header-left">
					<div>文件名称:</div>
				</div>
				<div class="details-header-right">
					<input type="text" id="fileName" readonly>
				</div>
			</div>
			
			
		
			<div class="approval-details-title" id="fileSizeDiv">
				<div class="details-header-left">
					<div>文件大小:</div>
				</div>
				<div class="details-header-right">
					<input type="text" id="fileSize" readonly>
				</div>
			</div>
			<div class="approval-details-title" id="fileTypeDiv">
				<div class="details-header-left">
					<div>文件类型:</div>
				</div>
				<div class="details-header-right">
					<input type="text" id="fileType" readonly>
				</div>
			</div>
		</div>
		<div class="approval-details-link approval-details-title">
			<div class="details-header-left">
				<div>文件外链:</div>
			</div>
			<div class="details-header-right">
				<input type="text" id="linkUrl" readonly>
			</div>
		</div>
		<div class="approval-details-power approval-details-title">
			<div class="details-header-left">
				<div>权限:</div>
			</div>
			<div class="details-header-right">
				<input type="text" id="role" readonly>
			</div>
		</div>

		<div class="approval-details-title approval-details-random">
			<div class="details-header-left">
				<div id="accessmodeText"></div>
			</div>
			<div class="details-header-right">
				<input type="text" id="plainAccessCode"  readonly="readonly">
			</div>
		</div>
		<div class="approval-details-title approval-details-effective">
			<div class="details-header-left">
				<label >有效期</label>
			</div>
			<div class="details-header-right">
				<input type="text"  id="expireTime" value="永久有效" readonly="readonly">
			</div>
		</div>
		<div class="record-title">审批记录：</div>
		<div id="recordList">
			
		</div>
		<div class="approval-details-tail">
			<input type="hidden" id="link">
			<a href="javascript:;" class="approval-details-button-adopt" onclick="approvalLink(2)">通过</a>
			<a href="javascript:;" class="approval-details-button-reject" onclick="approvalLink(3)">驳回</a>
			<a href="javascript:;" class="approval-details-button-close" onclick="gotoPage('${ctx}/share/linkApproveList')">关闭</a>
		</div>
	</div>
</body>

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

<script type="text/javascript">
	var token = '${token}';

	$(function(){
		$.ajax({
			type: "POST",
			data: {
				token: token,
				linkCode: "${linkCode}"
			},
			url:"${ctx}/share/getLinkApproveDetail",
			error: function(request) {
				$.toast("链接不存在",function(){
					gotoPage('${ctx}/share/linkApproveList');
				});
				
			},
			success: function(data) {
				var fileicon;
				if(data.linkAndNode.file!=undefined){
					$("#fileName").val(data.linkAndNode.file.name);
					fileicon=getImgHtml(1, data.linkAndNode.file.name, data.linkAndNode.file.shareStatus);
					$("#fileSizeDiv").css("display","block");
					$("#fileTypeDiv").css("display","block");
					$("#fileSize").val(formatFileSize(data.linkAndNode.file.size));
					$("#fileType").val((data.linkAndNode.file.name.split(".")[1]));
				
				}else{
					$("#fileName").val(data.linkAndNode.folder.name);
					fileicon="folder-icon";
					$("#fileSizeDiv").css("display","none");
					$("#fileTypeDiv").css("display","none");
					
				}
				
				$("#iconDiv").addClass(fileicon);
				$("#linkUrl").val(data.linkAndNode.link.url);
				$("#role").val(translateAuthorization(data.linkAndNode.link.role));
				$("#plainAccessCode").val(data.linkAndNode.link.plainAccessCode);
				$("#link").data("link", data.linkAndNode.link);
				var accessCodeMode=data.linkAndNode.link.accessCodeMode;
				if(accessCodeMode=="static"){
					if(data.linkAndNode.link.plainAccessCode==undefined||data.linkAndNode.link.plainAccessCode==""){
						$("#accessmodeText").text("允许匿名访问");
					}else{
						$("#accessmodeText").text("提取码访问");
						$("#plainAccessCode").val(data.linkAndNode.link.plainAccessCode);
					}
				}else if(accessCodeMode=="mail"){
					$("#accessmodeText").text("动态码访问");
				}
				if(data.linkAndNode.link.expireAt!=null&&data.linkAndNode.link.expireAt!=""){
					$("#expireTime").val(formatDateTime(data.linkAndNode.link.expireAt));
			    }
				var recordList = data.approveRecordList;
				var $list = $("#recordList");
				var $template = $("#recordTemplate");

				for (var i in recordList) {
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
		switch (status || 0) {
			case 2: return "通过";
			case 3: return "驳回";
			default : return "";
		}
	}

	function translateAuthorization(role) {
		switch (role) {
			case "auther":
				return "编辑 预览 下载";
			case "uploader":
				return "上传";
			case "viewer":
				return "预览 下载";
			case "downloader":
				return "下载";
			case "previewer":
				return "预览";
			default:
				return "禁止访问";
		}
	}

	function approvalLink(status){
		var link = $("#link").data("link");
		$.ajax({
			type: "POST",
			data: {
				token: token,
				linkOwner: link.ownedBy,
				nodeId: link.nodeId,
				linkCode: link.id
			},
			url: "${ctx}/share/approvalLink/" + status,
			error: handleError,
			success: function(data) {
				$.toast("操作成功。");
				gotoPage('${ctx}/share/linkApproveList');
			}
		});
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
	    return y + '-' + m + '-' + d+' '+h+':'+minute;    
	}; 
</script>
</html>