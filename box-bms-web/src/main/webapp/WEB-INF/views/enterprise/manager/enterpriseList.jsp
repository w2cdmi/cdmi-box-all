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
<div class="sys-content">
   <div class="alert"><i class="icon-lightbulb"></i><spring:message code="enterpriseList.light.bulb"/></div>
   <div class="clearfix">
    	<div class="pull-left">
	    	<button type="button" id="createEnterpriseBtn" class="btn btn-primary" onClick="createEnterprise()"><i class="icon-plus"></i><spring:message code="enterpriseList.create"/></button>
	    </div>
	    <div class="pull-right form-search">
	            <input type="hidden" id="page" name="page" value="1">
				 <select  class="span2" id="appList" name="appList">
	                    <option value=""> <spring:message code="enterpriseList.all.app"/> </option>
	                <c:forEach items="${authAppList}" var="authApp">
	                  	<option value="<c:out value='${authApp.authAppId}'/>"><c:out value='${authApp.authAppId}'/></option>
					</c:forEach>
				</select>
	            <div class="input-append">                   
	              <input type="text" id="filter" name="filter" class="span3 search-query" value="<c:out value='${filter}'/>" placeholder='<spring:message code="enterpriseList.searchDescription"/>' />
	              <button type="button" class="btn" id="searchButton"><i class="icon-search"></i></button>
	            </div>
	            <input type="hidden" id="token" name="token" value="<c:out value='${token}'/>"/>
	    </div>
    </div>
    <div id="myPage"></div>
    <div class="table-con">
			<div id="rankList"></div>
			<div id="rankListPage"></div>
	</div>
</div>
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
			"width" : "10%",
			"taxis":true
		},
	"domainName" : {
		"title" : '<spring:message code="enterpriseList.domainName"/>',
		"width" : "10%",
		"taxis":true
	},
	"contactEmail" : {
		"title" : '<spring:message code="user.manager.email"/>',
		"width" : "10%",
		"taxis":true
	},
	"contactPerson" : {
		"title" : '<spring:message code="enterpriseList.contactPerson"/>',
		"width" : "10%"
	},
	"contactPhone" : {
		"title" : '<spring:message code="enterpriseList.contactPhone"/>',
		"width" : "10%"
	},
	"status" : {
        "title" : '<spring:message code="common.status"/>',
        "width" : "10%"
    },
	"createdAt" : {
		"title" : '<spring:message code="clientManage.createDate"/>',
		"width" : "10%",
		"taxis":true
	},
	"operation" : {
		"title" : '<spring:message code="authorize.operation"/>',
		"width" : "25%"
	}
};
$(document).ready(function() {
	opts_viewGrid = $("#rankList").comboTableGrid({
		headData : headData,
		colspanDrag : false,
		definedColumn:false,
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
		case "createdAt":
			try {
				var size = tdItem.find("p").text();
				for ( var i = 0; i <catalogData.length; i++) {
					if(size == catalogData[i].createdAt){
						_txt = catalogData[i].createdAt;
						var date = new Date(_txt);
						var _year = date.getFullYear();
						var  _month = date.getMonth()+1;
						if(_month<10){
							_month = "0"+_month;
						}
						var _day = date.getDate();
						if(_day<10){
							_day = "0"+_day;
						}
						var _hours = date.getHours();
						if(_hours<10){
							_hours = "0"+_hours;
						}
						var _min = date.getMinutes();
						if(_min<10){
							_min = "0"+_min;
						}
						var _sec = date.getSeconds();  
						if(_sec<10){
							_sec = "0"+_sec;
						}
						var date = _year+"-"+_month+"-"+_day+" "+_hours+":"+_min+":"+_sec;
						tdItem.find("p").html(date).parent().attr("title", date);
					}
				}
			} catch (e) {
			}
			break;
		case "operation":
			try {
				var btns = '<input class="btn btn-small"  type="button" value="<spring:message  code="enterpriseList.catch.app"  />" onClick="managementApp('
					+ rowData.id + ')"/>';
					if(rowData.isdepartment==1){
						var btns2 = '<input class="btn btn-small" disabled = "disabled" type="button" value="<spring:message  code="enterprise.organizational.switch"  />" onClick="openOrganization('
							+ rowData.id + ')"/>';
					}else{
						var btns2 = '<input class="btn btn-small" type="button" value="<spring:message  code="enterprise.organizational.switch"  />" onClick="openOrganization('
							+ rowData.id + ')"/>';
					}
					tdItem.find("p").html(btns);
					tdItem.find("p").append(" ");
					tdItem.find("p").append(btns2);
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
	
	$("#searchButton").on("click",function(){
		initDataList(currentPage, newHeadItem, newFlag);
	});	
	
	$("#filter").keydown(function(){
		var evt = arguments[0] || window.event;
		if(evt.keyCode == 13){
			initDataList(currentPage, newHeadItem, newFlag);
			if(window.event){
				window.event.cancelBubble = true;
				window.event.returnValue = false;
			}else{
				evt.stopPropagation();
				evt.preventDefault();
			}
		}
	});
});

function initDataList(curPage, headItem, flag) {
	currentPage = curPage;
	newHeadItem = headItem;
	newFlag = flag;
	var url = "${ctx}/enterprise/manager/list";
	var appId = $("#appList").find("option:selected").val();
	var filter = $("#filter").val();
	var params = {
		"page" : curPage,
		"filter" : filter,
		"token" : "<c:out value='${token}'/>",
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


function createEnterprise(){
	top.ymPrompt.win({message:'${ctx}/enterprise/manager/createEnterprise',width:700,height:500,title:'<spring:message code="enterpriseList.create"/>', iframe:true,btn:[['<spring:message code="common.create"/>','yes',false,"btnCreate"],['<spring:message code="common.cancel"/>','no',true,"btnCancel"]],handler:doCreateEnterprise});
	top.ymPrompt_addModalFocus("#btnCreate");
}

function doCreateEnterprise(tp) {
	if (tp == 'yes') {
		top.ymPrompt.getPage().contentWindow.submitCreateEnterprise();
	} else {
		top.ymPrompt.close();
	}
}
function managementApp(id){
	top.ymPrompt.win({message:'${ctx}/enterprise/account/bindAppNew/'+id,width:700,height:440,title:'<spring:message code="enterpriseList.catch.app"/>', iframe:true,btn:[['<spring:message code="selectAdmin.bind"/>','yes',false,"btnCreate"],['<spring:message code="common.cancel"/>','no',true,"btnCancel"]],handler:doBindApp});
	top.ymPrompt_addModalFocus("#btnCreate");
}
function doBindApp(tp) {
	if (tp == 'yes') {
		top.ymPrompt.getPage().contentWindow.bindApp();
	} else {
		top.ymPrompt.close();
	}
}
function openOrganization(id){
	ymPrompt.confirmInfo({
		title : '<spring:message code="enterprise.organizational.switch"/>',
		message : '<spring:message code="enterprise.organizational.switch"/>'
				+ '<br/>'
				+ '<spring:message code="enterprise.org.switch.warn"/>',
		width : 450,
		closeTxt : '<spring:message code="common.close"/>',
		handler : function(tp) {
			if (tp == "ok") {
				var params = {
						"token" : "<c:out value='${token}'/>",
						"id" : id,
					};
				$.ajax({
					type : "POST",
					url : '${ctx}/enterprise/manager/openOrganization',
					data : params,
					error : function(request) {
						handlePrompt("error",'<spring:message code="common.operationFailed" />');
					},
					success : function(data) {
						location.reload() 
					}
				})
			}else{
				
			}
		},
		btn : [
				[ '<spring:message code="common.OK"/>', "ok" ],
				[ '<spring:message code="common.cancel"/>',
						"cancel" ] ]
	});
	
}
</script>
</html>
