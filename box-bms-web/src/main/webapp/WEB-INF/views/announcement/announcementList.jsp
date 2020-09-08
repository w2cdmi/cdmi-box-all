<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page import="pw.cdmi.box.uam.util.CSRFTokenManager"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<%
request.setAttribute("token", CSRFTokenManager.getTokenForSession(session));
%>

<!DOCTYPE html>
<html>
<head>
<%@ include file="../common/common.jsp"%>
<script src="${ctx}/static/js/public/JQbox-hw-grid.js" type="text/javascript"></script>
</head>


<body>
<div class="sys-content">
	<div class="alert"><i class="icon-lightbulb"></i><spring:message code="authorize.announcement.manage.description"/></div>
	
    <div class="clearfix">
    	<div class="pull-left">
        <button type="button" class="btn btn-primary" onClick="createAnnouncement()"><spring:message code="announcement.oper.publish"/></button>
        <button type="button" class="btn" onClick="deleteAnnouncement()"><spring:message code="announcement.oper.delete"/></button>
        </div>
    </div>
    
	<div id="announcementList" class="table-con">
    </div>

</div>
</body>
</html>
<script type="text/javascript">
var headData = {
		"title" : {"title" : "<spring:message code="announcement.table.title"/>", "width": "300px"},
		"content" : {"title" : "<spring:message code="announcement.table.content"/>"},
		"time" : {"title" : "<spring:message code="announcement.table.publishtime"/>", "width": "160px", "cls": "ac"},
		};
		
var optsGrid = $("#announcementList").comboTableGrid({
	headData : headData,
	dataId : "id",
	miniPadding: false,
	border: true,
	checkBox: true 
}); 

$(document).ready(function() {
	$.fn.comboTableGrid.setItemOp = function(tableData, rowData, tdItem, colIndex){
		switch (colIndex) {
			case "content":
				try {
					tdItem.removeAttr("title");
				} catch (e) {}
				break;
			case "time":
				try {
					var t = getLocalTime(rowData.publishTime);
					tdItem.find("p").html(t);
				} catch (e) {}
				break;
			default : 
				break;
		}
	}
	
	listAnnouncement();
});

function listAnnouncement(){
	$.ajax({
	    type: "GET",
	    async: true,
	    cache: false,
	    url:"${ctx}/announcement/listAll",
	    error: function(request) {
	    },
	    success: function(data) {
	    	$("#announcementList").setTableGridData(data, optsGrid);
	    	
	    	var pageH = $("body").outerHeight();
	    	top.iframeAdaptHeight(pageH);
	    }
	});
}

function createAnnouncement(){
	top.ymPrompt.win({
		message:'${ctx}/announcement/enterCreate',
		width:700,height:510,
		title:'<spring:message code="announcement.oper.publish" />', 
		iframe:true,
		btn:[['<spring:message code="announcement.create.publish"/>','yes',false,"btnCreate"],['<spring:message code="announcement.create.cancel"/>','no',true,"btnCancel"]],
		handler:doCreateAnnouncement
	});
	top.ymPrompt_addModalFocus("#btn-focus");
}

function doCreateAnnouncement(tp) {
	if (tp == 'yes') {
		top.ymPrompt.getPage().contentWindow.submitCreateAnnouncement();
	} else {
		top.ymPrompt.close();
	}
}


function deleteAnnouncement(){
 	var idArray = $("#announcementList").getTableGridSelected();
 	if (idArray == "") {
  	handlePrompt("error",'<spring:message code="announcement.select.notice"/>');
  		return;
 	}
 

 	var ids = idArray.join(",");
 	var token="<c:out value='${token}'/>";
 	top.ymPrompt.confirmInfo( {
  		title : '<spring:message code="announcement.delete.confirm.title"/>',
  		message : '<spring:message code="announcement.delete.confirm"/>',
  		width:450,
  		closeTxt:'<spring:message code="common.close"/>',
  		handler : function(tp) {
   			if(tp == "ok"){
    			$.ajax({
        			type: "POST",
        			url:"${ctx}/announcement/deleteAnnouncement",
        			data:{ids:ids,token:token},
        			error: function(request) {
            			top.handlePrompt("error",'<spring:message code="announcement.delete.failed"/>');
        			},
        			success: function() {
            			top.handlePrompt("success",'<spring:message code="announcement.delete.success"/>');
            			$.ajax({
            			    type: "GET",
            			    async: true,
            			    cache: false,
            			    url:"${ctx}/announcement/listAll",
            			    error: function(request) {
            			    },
            			    success: function(data) {
            			    	$("#announcementList").setTableGridData(data, optsGrid);
            			    }
            			});
        			}
    			}); 
   			}
  		},
  		btn: [['<spring:message code="common.OK"/>', "ok"],['<spring:message code="common.cancel"/>', "cancel"]]
 	});
}
</script>