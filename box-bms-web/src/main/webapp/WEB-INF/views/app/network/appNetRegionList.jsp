<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="cse"
	uri="http://cse.huawei.com/custom-function-taglib"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page import="pw.cdmi.box.uam.util.CSRFTokenManager"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<%
request.setAttribute("token", CSRFTokenManager.getTokenForSession(session));
%>
<!DOCTYPE html>
<html>
<head>
<%@ include file="../../common/common.jsp"%>
<script src="${ctx}/static/js/public/JQbox-hw-grid.js" type="text/javascript"></script>
<script src="${ctx}/static/js/public/JQbox-hw-page.js" type="text/javascript"></script>
<script src="${ctx}/static/js/public/JQbox-hw-switchButton.js" type="text/javascript"></script>
</head>
<body>
<div class="sys-content">
	<div class="clearfix control-group">
		<a  class="return btn btn-small pull-right" href="${ctx}/app/appmanage/authapp/list"><i class="icon-backward"></i>&nbsp;<spring:message code="common.back"/></a>
		<h5 class="pull-left" style="margin: 3px 0 0 4px;"><a href="${ctx}/app/appmanage/authapp/list"><c:out value='${appId}'/></a>&nbsp;>&nbsp;<spring:message code="app.network.region.config.title"/></h5>	
	</div>
	<div class="form-con clearfix">
		<span id="switchButton" class="pull-right"></span>
		<div class="pull-left">
			<p><strong><spring:message code="common.description"/></strong></p>
			<p class="help-block"><spring:message code="app.network.region.config.desc"/></p>
			<input type="hidden" id="networkRegionStatus" name="networkRegionStatus" value="<c:out value='${networkRegionStatus}'/>" class="span4" />
		</div>
	</div>
	<div  class="clearfix">
	    <div class="pull-left">
	    	<button type="button" class="btn btn-primary" onClick="createNetworkRegion()"><i class="icon-plus"></i><spring:message code='common.new'/></button>
	    	<button type="button" class="btn" onClick="deleteNetworkRegion()"><spring:message code='common.delete'/></button>
	    </div>
	</div>

	<div class="table-con">
		<div id="netRegionList"></div>
		<div id="page"></div>
	</div>
</div>
</body>
<script type="text/javascript">

var currentPage = 1;
var opts_viewGrid_statistics = null;
var opts_viewGrid_top = null;
var opts_page = null;
var headDataStatistics = {
	"ipStart" : {
		"title" : "<spring:message code='app.network.region.table.startip'/>",
		"width" : "25%"
	},
	"ipEnd" : {
		"title" : "<spring:message code='app.network.region.table.endip'/>",
		"width" : "25%"
	},
	"regionName" : {
		"title" : "<spring:message code='app.network.region.table.region'/>",
		"width" : "25%"
	},
	"operate" : {
		"title" : "<spring:message code='common.operation'/>",
		"width" : "25%",
		"cls": "ac"
	}
};

var pageOpts = $("#page").comboPage({
	style : "page"
});

$(document).ready(
	function() {
		opts_viewGrid_top = $("#netRegionList").comboTableGrid({
			headData : headDataStatistics,
			dataId : "id",
			miniPadding: false,
			border: true,
			checkBox: true 
		});
			
		$.fn.comboTableGrid.setItemOp = function(tableData, rowData, tdItem, colIndex){
			switch (colIndex) {
				case "operate":
					try {
						var buttonChange = '<button class="btn" type="button" onClick="changeNetworkRegion(\''+ rowData.id + '\',\'' + rowData.authAppId +'\')" ><spring:message code="common.modify" /></button>';
						tdItem.find("p").html(buttonChange);
					} catch (e) {}
					break;
				default : 
					break;
			}
		}
			
		$.fn.comboPage.pageSkip = function(opts, _idMap, curPage){
			initDataList(curPage)
		}
			
		initDataList(currentPage);
		
		getAppNetworkRegionConfigSwitch();
	}
);
		
function initDataList(curPage) {
	currentPage = curPage;
	var statistics_url = "${ctx}/app/network/config/list/<c:out value='${appId}'/>";
	var StatisticsParams = {
		"pageNumber" : curPage,
		"pageSize" : 40,
		"token" : "${token}"
	};
	$.ajax({
		type : "POST",
		async: true,
	    cache: false,
		url : statistics_url,
		data : StatisticsParams,
		error : function(request) {
			handlePrompt("error",'<spring:message code="operation.failed" />');
		},
		success : function(data) {
			$("#netRegionList").setTableGridData(data.content, opts_viewGrid_top);
			$("#page").setPageData(pageOpts, data.number, data.size, data.totalElements);
		}
	});
}


function getAppNetworkRegionConfigSwitch(){
	var switchFlag =false;
	if(1==${networkRegionStatus})
	{
		switchFlag=true;
	}
	
	var switchOpts = $("#switchButton").comboSwitchButton({
		onText : '<spring:message code="app.network.region.config.opened" />',
		offText : '<spring:message code="app.network.region.config.closed" />',
		defaultSwitchOn : switchFlag,
		onEvent: function(){
			$("#configSwitch").val("enable");
			var url = "${ctx}/app/network/config/updateConfigStatus";
			$.ajax({
				type : "POST",
				url : url,
				data:{appId:'${appId}',networkRegionStatus:1,"token" : "${token}"},
				error : function(request) {
					$("#switchButton").resetSwitchButton(switchOpts);
					top.handlePrompt("error",'<spring:message code="operation.failed" />');
				},
				success : function(data) {
					top.handlePrompt("success",'<spring:message code="common.openSuccess"/>');
				}
			});
			
		},
		offEvent: function(){
			$("#configSwitch").val("disable");
			var url = "${ctx}/app/network/config/updateConfigStatus";
			$.ajax({
				type : "POST",
				url : url,
				data:{appId:'${appId}',networkRegionStatus:0,"token" : "${token}"},
				error : function(request) {
					$("#switchButton").resetSwitchButton(switchOpts);
					top.handlePrompt("error",'<spring:message code="operation.failed" />');
				},
				success : function(data) {
					top.handlePrompt("success",'<spring:message code="common.closeSuccess"/>');
				}
			});
			
		}
	})
}


function createNetworkRegion(){
	top.ymPrompt.win({
		message:'${ctx}/app/network/config/enterConfig/<c:out value="${appId}"/>?currentPage='+currentPage,
		width:550,
		height:350,
		title:'<spring:message code="app.network.region.windows.title.add"/>', 
		iframe:true,
		btn:[['<spring:message code="common.create"/>','yes',false,"btnCreate"],['<spring:message code="common.cancel"/>','no',true,"btnCancel"]],
		handler:doSubmit});
	top.ymPrompt_addModalFocus("#btnCreate");
}

function doSubmit(tp) {
	if (tp == 'yes') {
		top.ymPrompt.getPage().contentWindow.submitForm();
	} else {
		top.ymPrompt.close();
	}
}

function changeNetworkRegion(configId, authAppId){
	top.ymPrompt.win({
		message:'${ctx}/app/network/config/enterConfig/'+ authAppId + '?tag=' + configId +'&currentPage='+currentPage,
		width:500,
		height:350,
		title:'<spring:message code="app.network.region.windows.title.change"/>', 
		iframe:true,
		btn:[['<spring:message code="common.modify"/>','yes',false,"btnCreate"],['<spring:message code="common.cancel"/>','no',true,"btnCancel"]],
		handler:doSubmit});
	top.ymPrompt_addModalFocus("#btnCreate");
}

function deleteNetworkRegion(){
 	var idArray = $("#netRegionList").getTableGridSelected();
 	if (idArray == "") {
  	handlePrompt("error",'<spring:message code="app.network.region.table.select.notice"/>');
  		return;
 	}
 

 	var ids = idArray.join(",");
 	var token="${token}";
 	top.ymPrompt.confirmInfo( {
  		title : '<spring:message code="app.network.region.delete.confirm.title"/>',
  		message : '<spring:message code="app.network.region.delete.confirm"/>',
  		width:450,
  		closeTxt:'<spring:message code="common.close"/>',
  		handler : function(tp) {
   			if(tp == "ok"){
    			$.ajax({
        			type: "POST",
        			url:"${ctx}/app/network/config/deleteConfig",
        			data:{ids:ids,token:token},
        			error: function(request) {
            			top.handlePrompt("error",'<spring:message code="app.network.region.delete.failed"/>');
        			},
        			success: function() {
            			top.handlePrompt("success",'<spring:message code="app.network.region.delete.success"/>');
            			initDataList(currentPage);
        			}
    			}); 
   			}
  		},
  		btn: [['<spring:message code="common.OK"/>', "ok"],['<spring:message code="common.cancel"/>', "cancel"]]
 	});
}

</script>
</html>
