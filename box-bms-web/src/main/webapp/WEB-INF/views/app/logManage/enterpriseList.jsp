<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cse" uri="http://cse.huawei.com/custom-function-taglib"%>  
<%@ page import="pw.cdmi.box.uam.util.CSRFTokenManager"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%request.setAttribute("token",CSRFTokenManager.getTokenForSession(session));%>
<!DOCTYPE html>
<html>
<head>
<%@ include file="../../common/common.jsp"%>
<script src="${ctx}/static/js/public/JQbox-hw-page.js" type="text/javascript"></script>
</head>
<body>
<form id="searchForm" name="searchForm" action="${ctx}/enterprise/adminstratorlog/logview" method="post"> 
<div class="sys-content">
   <div class="clearfix">
	    <div class="pull-right form-search">
	            <input type="hidden" id="page" name="page" value="1">
	            <input type="hidden" id="token" name="token" value="${token}"/>
	    </div>
    </div>
    <div id="myPage"></div>
    <div class="table-con">
			<div id="rankList"></div>
			<div id="rankListPage"></div>
	</div>
</div>
</form>
</body>
<script type="text/javascript">
var newHeadItem = "";
var newFlag = false;
var currentPage = 1;
var opts_viewGrid = null;
var opts_page = null;
var headData = {
	"name" : {
			"title" : '<spring:message code="enterpriseList.name"/>',
			"width" : "180px",
			"taxis":true
		},
	"domainName" : {
		"title" : '<spring:message code="enterpriseList.domainName"/>',
		"width" : "180px"
	},
	"contactPerson" : {
		"title" : '<spring:message code="enterpriseList.contactPerson"/>',
		"width" : "180px"
	},
	"contactPhone" : {
		"title" : '<spring:message code="enterpriseList.contactPhone"/>',
		"width" : "170px"
	},
	"status" : {
		"title" : '<spring:message code="common.status"/>',
		"width" : "170px"
	},
	"operation" : {
		"title" : '<spring:message code="authorize.operation"/>',
		"width" : "216px"
	}
};
$(document).ready(function() {
	opts_viewGrid = $("#rankList").comboTableGrid({
		headData : headData,
		colspanDrag : true,
		height : 860,
		dataId : "id"
	});
	$.fn.comboTableGrid.setItemOp = function(tableData,rowData, tdItem, colIndex) {
		
		switch (colIndex) {
		case "status":
			try {
				var status = tdItem.find("p").text();
				if (status == 0) {
					tdItem.find("p").html("<spring:message code='common.enable'/>").parent().attr("title","<spring:message code='common.enable'/>");
				}
				if (status == 1) {
					tdItem.find("p").html("<label class='public_red_font'><spring:message code='common.stop'/></label>");
				}
			} catch (e) {
			}
			break;
		case "contactPerson":
			try {
				var text = tdItem.find("p").text();
				if(text == "")
				{
					tdItem.find("p").html("_");
				}
			} catch (e) {
			}
			break;
		case "contactPhone":
			try {
				var text = tdItem.find("p").text();
				if(text == "")
				{
					tdItem.find("p").html("_");
				}
			} catch (e) {
			}
			break;
		case "operation":
			try {
				var btns = '<input class="btn btn-small" type="button" value="<spring:message code='log.list'/>" onClick="logView('
					+ rowData.id + ')"/>';
				
				tdItem.find("p").html(btns);
			} catch (e) {
			}
			break;
		default:
			break;
		}
	};

	$.fn.comboTableGrid.taxisOp = function(headItem, flag) {
		initDataList(currentPage, headItem, flag);
	};

	opts_page = $("#rankListPage").comboPage({
		style : "page table-page",
		lang : '<spring:message code="main.language"/>'
	});

	$.fn.comboPage.pageSkip = function(opts, _idMap, curPage) {
		initDataList(curPage, newHeadItem, newFlag);
	};
	
	initDataList(currentPage, newHeadItem, newFlag);
	
	if (!placeholderSupport()) {
		placeholderCompatible();
	};
});

function logView(id){
		window.location = "${ctx}/enterprise/adminstratorlog/logview?enterpriseId="+id;
}

function initDataList(curPage, headItem, flag) {
	currentPage = curPage;
	newHeadItem = headItem;
	newFlag = flag;
	var url = "${ctx}/enterprise/manager/query";
	var appId = $("#appList").find("option:selected").val();
	var filter = $("#filter").val();
	
	var params = {
		"page" : curPage,
		"filter" : filter,
		"token" : "${token}",
		"appId" : appId,
		"newHeadItem":newHeadItem,
		"newFlag":newFlag
	};
	$("#rankList").showTableGridLoading();
	$.ajax({
		type : "POST",
		url : url,
		data : params,
		error : function(request) {
			handlePrompt("error",'<spring:message code="common.operationFailed" />');
		},
		success : function(data) {
			catalogData = data.content;
			$("#rankList").setTableGridData(catalogData, opts_viewGrid);
			$("#rankListPage").setPageData(opts_page, data.number,data.size, data.totalElements);
			var pageH = $("body").outerHeight();
			top.iframeAdaptHeight(pageH);
		}
	});
}
</script>
</html>
