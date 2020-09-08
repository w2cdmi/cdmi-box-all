<%@page import="com.fasterxml.jackson.annotation.JsonInclude.Include"%>
<%@page
	import="com.huawei.sharedrive.isystem.cluster.domain.ResourceGroup.RuntimeStatus"%>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<%@ taglib prefix="cse"
	uri="http://cse.huawei.com/custom-function-taglib"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="Cache-Control" content="no-cache" />
<meta http-equiv="Pragma" content="no-cache" />
<title><spring:message code="main.title" /></title>
<%@ include file="../common/common.jsp"%>
</head>
<body>
	<div class="sys-content">
		<div class="alert">
			<i class="icon-lightbulb icon-orange"></i>
			<spring:message code="cluster.rogion.info.describe" />
			<a href="#none" onclick="showMoive();"><spring:message
					code="common.detail" /></a>
		</div>
		<div class="resource-guide">
			<h5>
				<spring:message code="cluster.explorer.flow" />
			</h5>
		</div>
		<button type="button" class="btn btn-primary" onClick="createRegion()">
			<i class="icon-add"></i>
			<spring:message code="cluster.storage.add" />
		</button>
		<c:if test="${fn:length(regionList) > 0}">
			<button type="button" class="btn btn-primary"
				onClick="setDefaultRegion()">
				<i class="icon-suitcase"></i>
				<spring:message code="cluster.storage.default.set" />
			</button>
		</c:if>
		<c:if test="${empty regionList}">
			<div>
				<spring:message code="cluster.storage.add.info" />
				<a href="javascript:createRegion()"><spring:message
						code="common.create" /></a>
			</div>
		</c:if>
		<c:forEach items="${regionList}" var="region">
			<div class="table-con">
				<table class="table  table-striped">
					<tbody>
						<tr>
							<td style="text-align:left; vertical-align:middle; ">
								<form class="form-inline">
									<div class="form-group">
										<spring:message code="clusterManage.storageRegion" />
										<span id="region_${region.id}"> <b>${cse:htmlEscape(region.name)}</b>
											<i class="icon-pencil"
											onClick="changeRegonTD(${region.id},'${cse:htmlEscape(region.name)}')"></i>
										</span>
										<c:if test="${region.defaultRegion}">

											<a>(<spring:message code="cluster.storage.default" />)
											</a>
										</c:if>
									</div>
								</form>
							</td>
							<td style="vertical-align:middle;text-align:right;">
								<button type="button" class="btn btn-primary"
									onclick="createDC(${cse:htmlEscape(region.id)})">
									<i class="icon-add"></i>
									<spring:message code="cluster.centerdata.add" />
								</button>
								<!-- <button type="button" class="btn"
									onclick="changeRegion(${cse:htmlEscape(region.id)})">
									<spring:message code="common.modify" />
								</button> -->
								 <c:if test="${!region.defaultRegion}">
									<c:if test="${fn:length(region.dataCenters) <= 0}">
										<button type="button" class="btn"
											onclick="deleteRegion(${cse:htmlEscape(region.id)},'${cse:htmlEscape(region.name)}')">
											<spring:message code="common.delete" />
										</button>
									</c:if>
								</c:if>
							</td>
						</tr>
						<tr>
							<td class="table-createdc" colspan="2">
								<table class="table table-bordered">
									<c:if test="${fn:length(region.dataCenters) > 0}">
										<thead>
											<tr>
												<th><spring:message code="common.title" /><i class="icon-exclamation-sign" style="margin-left:5px" title="<spring:message code="priority.description" />"></i></th>
												<th style="width:15%"><spring:message code="clusterManage.runStatus" /></th>
												<th style="width:20%"><spring:message code="region.common.role" /></th>
												<th><spring:message code="common.regionName" /></th>
												<th style="width:15%"><spring:message code="cluster.report.time" /></th>
												<th style="width:10%"><spring:message code="common.operation" /></th>
											</tr>
										</thead>
									</c:if>
									<tbody>
										<jsp:useBean id="reportDate" class="java.util.Date" />
										<c:forEach items="${region.dataCenters}" var="dataCenter">
											<c:set target="${reportDate}" property="time"
												value="${dataCenter.resourceGroup.lastReportTime}" />
											<tr>
											    <c:if test="${dataCenter.priority==0}">
												<td style="vertical-align:middle;" >
												
									            <label style="text-align:right;width:45%;height:16px;float:left" visible="true"></label>
												<input  type="radio" style="float:left;margin-left:5px" name="type_${cse:htmlEscape(region.id)}"  value="0" onclick="setpriority(${cse:htmlEscape(dataCenter.id)},${cse:htmlEscape(region.id)})" />
								                <a
													href="javascript:manageDCRGroup(${cse:htmlEscape(dataCenter.id)})" style="float:left" >${cse:htmlEscape(dataCenter.name)}</a>
												</c:if>
												<c:if test="${dataCenter.priority==1}">
												<td style="vertical-align:middle;">												
									            <label style="text-align:right;width:45%;height:16px;float:left" ><spring:message code="download.first"/></label>
												<input type="radio" style="float:left;margin-left:5px" name="type_${cse:htmlEscape(region.id)}"  value="0" onclick="setprioritydefault(${cse:htmlEscape(dataCenter.id)},${cse:htmlEscape(region.id)})" checked="checked"/>
												
												<a
													href="javascript:manageDCRGroup(${cse:htmlEscape(dataCenter.id)})" style="float:left;">${cse:htmlEscape(dataCenter.name)}</a>
													
												</td></c:if>
												<td>
													<c:if
														test="${dataCenter.resourceGroup.status.code !=2}">
													<c:if
														test="${dataCenter.resourceGroup.runtimeStatus.code == runStatusNormal}">
														<img alt='<spring:message code="common.normal"/>'
															src="${ctx}/static/image/state/normal.png"
															title='<spring:message code="common.normal"/>' />
													</c:if> <c:if
														test="${dataCenter.resourceGroup.runtimeStatus.code == runStatusAbnormal}">
														<img alt='<spring:message code="common.exception"/>'
															src="${ctx}/static/image/state/exception.png"
															title='<spring:message code="common.exception"/>' />
													</c:if> 
													<c:if
														test="${dataCenter.resourceGroup.runtimeStatus.code == runStatusOffline}">
														<img alt='<spring:message code="common.offline"/>'
															src="${ctx}/static/image/state/offline.png"
															title='<spring:message code="common.offline"/>' />
													</c:if>
													</c:if>
													<c:if test="${dataCenter.resourceGroup.status.code ==2}">
														<img alt='<spring:message code="common.offline"/>'
															src="${ctx}/static/image/state/offline.png"
															title='<spring:message code="datacenter.down.line"/>' />
													</c:if>
													</td>

												<td>
													<form class="form-inline" id="datacenter_${dataCenter.id}">

														<c:if
															test="${dataCenter.resourceGroup.rwStatus.code == 0}">
															<spring:message code="common.RW" />
															<i class="icon-pencil"
																onClick="changeDataCenter('${dataCenter.id}','0','${dataCenter.name}')"></i>
														</c:if>
														<c:if test="${dataCenter.resourceGroup.rwStatus.code ==1}">
															<spring:message code="common.ROnly" />
															<i class="icon-pencil"
																onClick="changeDataCenter('${dataCenter.id}','1','${dataCenter.name}')"></i>
														</c:if>
													</form>
												</td>





												<td><c:choose>
														<c:when test="${empty dataCenter.dssDomain}">
                                                               	-
															   </c:when>
														<c:otherwise>
															<table class="table third-table">
																<tbody>
																	<c:forEach items="${dataCenter.dssDomain}" var="dss">
																		<tr>
																			<td>${cse:htmlEscape(dss.dnsDomain.domainName)}</td>
																		</tr>
																	</c:forEach>
																</tbody>
															</table>
														</c:otherwise>
													</c:choose></td>
												<td><fmt:formatDate value="${reportDate}"
														pattern="yyyy-MM-dd HH:mm:ss" /></td>
												<td><c:choose>
														<c:when
															test="${dataCenter.resourceGroup.rwStatus.code ==1}">
															<c:if test='${dataCenter.resourceGroup.status.code !=2}'>
															<button type="button" class="btn" 
																onclick="downLineDataCenter(${cse:htmlEscape(dataCenter.id)},'${cse:htmlEscape(dataCenter.name)}')">
																<spring:message code="datacenter.down.line" />
															</button>
															</c:if>
															<c:if test="${dataCenter.resourceGroup.status.code ==2}">
															<button type="button" class="btn" 
																onclick="upLineDataCenter(${cse:htmlEscape(dataCenter.id)},'${cse:htmlEscape(dataCenter.name)}')">
																<spring:message code="datacenter.up.line" />
															</button>
															</c:if>
														</c:when>
														<c:otherwise>
														<c:if test="${dataCenter.resourceGroup.status.code !=2}">
																-
														</c:if>
														<c:if test="${dataCenter.resourceGroup.status.code ==2}">
															<button type="button" class="btn" 
																onclick="upLineDataCenter(${cse:htmlEscape(dataCenter.id)},'${cse:htmlEscape(dataCenter.name)}')">
																<spring:message code="datacenter.up.line" />
															</button>
															</c:if>
														</c:otherwise>
													</c:choose></td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
							</td>
						</tr>
					</tbody>
				</table>
			</div>
		</c:forEach>

	</div>
	</div>

	<form class="form-horizontal" id="deleteRegionForm">
		<input type="hidden" id="regionName" name="name" /> <input
			type="hidden" id="token" name="token"
			value="${cse:htmlEscape(token)}" />
	</form>
</body>
<script type="text/javascript">
$.validator.addMethod(
		   "isRegionName", 
		   function(value, element) {   
	           var validName = /^[a-zA-Z]{1}[a-zA-Z0-9]*$/;   
	           return validName.test(value);   
	       }, 
	       $.validator.format('<spring:message code="region.name.rule"/>')
);
defaultRegionRadion = "";
$(document).ready(function() {
	if('<spring:message code="common.language1"/>' =="en"){
		$(".resource-guide").addClass("resource-guide-en");
	}
	
	defaultRegionRadion = $("input[name='defaultTag']:checked");
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
});

function manageDCRGroup(id){
	window.location = "${ctx}/cluster/dcdetailmanage/"+id;
}
function setpriority(dcid,regionid){
$.ajax({
        type: "POST",
        url:"${ctx}/cluster/dcmanage/setpriority",
        data:{dcid:dcid,regionid:regionid,"token":"${cse:htmlEscape(token)}"},
        error: function(request) {
        	top.handlePrompt("error",'<spring:message code="priority.set.fail"/>');
        },
        success: function() {
            refreshWindow();
        	top.handlePrompt("success",'<spring:message code="priority.set.success"/>');
        }
    });
}
function setprioritydefault(dcid,regionid){
$.ajax({
        type: "POST",
        url:"${ctx}/cluster/dcmanage/setprioritydefault",
        data:{dcid:dcid,regionid:regionid,"token":"${cse:htmlEscape(token)}"},
        error: function(request) {
        	top.handlePrompt("error",'<spring:message code="priority.set.fail"/>');
        },
        success: function() {
            refreshWindow();
        	top.handlePrompt("success",'<spring:message code="priority.set.success"/>');
        	
        }
    });
}
function createRegion(){
	top.ymPrompt.win({
		message:'${ctx}/cluster/region/create',width:700,height:260,
		title:'<spring:message code="cluster.storage.add"/>', iframe:true,
		btn:[['<spring:message code="common.create"/>','yes',false,"btn-focus"],['<spring:message code="common.cancel"/>','no',true,"btn-cancel"]],handler:saveRegion
	});
	top.ymPrompt_addModalFocus("#btn-focus");
}
function saveRegion(tp) {
	if (tp == 'yes') {
		top.ymPrompt.getPage().contentWindow.submitRegion();
	} else {
		top.ymPrompt.close();
	}
}
function changeRegion(id, name){
	top.ymPrompt.win({
		message:'${ctx}/cluster/region/change/' + id,width:700,height:220,
		title:'<spring:message code="cluster.storge.modified"/>', iframe:true,
		btn:[['<spring:message code="common.modify"/>','yes',false,"btn-focus"],['<spring:message code="common.cancel"/>','no',true,"btn-cancel"]],handler:saveRegion
	});
	top.ymPrompt_addModalFocus("#btn-focus");
}

function createDC(regionId){
	top.ymPrompt.win({
		message:'${ctx}/cluster/dcmanage/create/'+regionId,
		width:700,height:270,
		title:'<spring:message code="cluster.centerdata.add"/>', iframe:true,
		btn:[['<spring:message code="common.create"/>','yes',false,"btn-focus"],['<spring:message code="common.cancel"/>','no',true,"btn-cancel"]],handler:saveDc
	});
	top.ymPrompt_addModalFocus("#btn-focus");
}
function setDefaultRegion(){
	top.ymPrompt.win({
		message:'${ctx}/cluster/region/enterSetDefaultRegion',
		width:700,height:190,
		title:'<spring:message code="cluster.storage.default.set"/>', iframe:true,
		btn:[['<spring:message code="common.save"/>','yes',false,"btn-focus"],['<spring:message code="common.cancel"/>','no',true,"btn-cancel"]],handler:doSetDefaultRegion
	});
	top.ymPrompt_addModalFocus("#btn-focus");
}

function doSetDefaultRegion(tp) {
	if (tp == 'yes') {
		top.ymPrompt.getPage().contentWindow.submitSetDefaultRegion();
	} else {
		top.ymPrompt.close();
	}
}

function saveDc(tp) {
	if (tp == 'yes') {
		top.ymPrompt.getPage().contentWindow.submitDc();
	} else {
		top.ymPrompt.close();
	}
}
function activateDCRGroup(id){
	$.ajax({
        type: "POST",
        url:"${ctx}/cluster/dcmanage/activate",
        data:{id:id,"token" : "${cse:htmlEscape(token)}"},
        error: function(request) {
        	top.handlePrompt("error",'<spring:message code="cluster.centerData.enable.fail"/>');
        },
        success: function() {
        	top.handlePrompt("success",'<spring:message code="cluster.centerData.enable.success"/>');
        	refreshWindow();
        }
    });
}
function deleteDCRGroup(id){
	top.ymPrompt.confirmInfo( {
		title :'<spring:message code="cluster.DC.del"/>',
		message : '<spring:message code="cluster.DC.del.clew"/>',
		closeTxt:'<spring:message code="common.close"/>',
		handler : function(tp) {
			if(tp == "ok"){
				$.ajax({
			        type: "POST",
			        url:"${ctx}/cluster/dcmanage/delete",
			        data:{id:id,"token" : "${cse:htmlEscape(token)}"},
			        error: function(request) {
			        	top.handlePrompt("error",'<spring:message code="cluster.centerData.del.fail"/>');
			        },
			        success: function() {
			        	top.handlePrompt("success",'<spring:message code="cluster.centerData.del.success"/>');
			        	refreshWindow();
			        }
			    });
			}
		},
		btn: [['<spring:message code="common.OK"/>', "ok"],['<spring:message code="common.cancel"/>', "cancel"]]
	});
}

/* $(":radio").change(function() {
	var selectedRadio = $("input[name='defaultTag']:checked");
	
	top.ymPrompt.confirmInfo( {
		title :'<spring:message code="cluster.storage.default.set"/>',
		message : '<spring:message code="cluster.storage.modiffy"/>',
		closeTxt:'<spring:message code="common.close"/>',
		handler : function(tp) {
			if(tp == "ok"){
				$.ajax({
			        type: "POST",
			        url:"${ctx}/cluster/region/setDefaultRegion/" + selectedRadio.val(),
			        data:{"token" : "${cse:htmlEscape(token)}"},
			        error: function(request) {
			        	top.handlePrompt("error",'<spring:message code="cluster.storage.modiffy.fail"/>');
			        	defaultRegionRadion.get(0).checked = true;
			        },
			        success: function() {
			        	defaultRegionRadion=selectedRadio;
			        	top.handlePrompt("success",'<spring:message code="cluster.storage.modiffy.success"/>');
			        	refreshWindow();
			        }
			    });
			} else {
				defaultRegionRadion.get(0).checked = true;
			}
		},
		btn: [['<spring:message code="common.OK"/>', "ok"],['<spring:message code="common.cancel"/>', "cancel"]]
	});
}) ; */

function deleteRegion(regionID,regionName){
	$("#regionName").val(regionName);
	if(defaultRegionRadion.val() == regionID) {
		top.handlePrompt("error",'<spring:message code="cluster.storage.del.error"/>');
	} else {
		top.ymPrompt.confirmInfo( {
			title :'<spring:message code="cluster.storage.del"/>',
			message : '<spring:message code="cluster.storage.del.clow"/>',
			closeTxt:'<spring:message code="common.close"/>',
			handler : function(tp) {
				if(tp == "ok"){
					$.ajax({
			        	type: "POST",
			        	url:"${ctx}/cluster/region/delete/" + regionID,
			        	data:$('#deleteRegionForm').serialize(),
			        	error: function(request) {
			        		top.handlePrompt("error",'<spring:message code="cluster.storage.del.fail"/>');
			        	},
			       		success: function() {
			        		top.handlePrompt("success",'<spring:message code="cluster.storage.del.success"/>');
			        			top.document.getElementById("regionManager").click();
			        	}
			    	});
				}
			},
		btn: [['<spring:message code="common.OK"/>', "ok"],['<spring:message code="common.cancel"/>', "cancel"]]
	});
	}
}

function showMoive(){
	var URL = '${ctx}/static/help/handleDemo.html';
	if('<spring:message code="common.language1"/>' =="en"){
		URL = '${ctx}/static/help/en/handleDemo.html';
	}
	top.ymPrompt.win({
		message:URL,
		width:680,height:560,
		title:'<spring:message code="common.tipwizard"/>', iframe:true
	});
}
function changeRegonTD(regionId,text)
{
			 	var $td=$("#region_"+regionId);
			 	var str='<input id="region_name_'+regionId+'"type="text" class="span2" />'+
			 			'<li class="icon-save" onClick="submitChangeRegion('+regionId+')"></li>'			 			+
			 			'<li class="icon-cancel" onClick="changeRegonCancel('+regionId+','+"'"+text+"'"+')"></li>';
                // 创建替换的input 对象   
                var $input = $(str);  
                // 设置value值   
                $input.val(text);  
  				$td.html("");  
                // 清除td中的文本内容   
                $td.append($input);
            /**   $("#region_name_"+regionId).rules("add",{
				   required:true, 
				   isRegionName:true,
				   rangelength:[1,50],messages:{required:'<spring:message code="region.name.rule"/>'}
			   })**/
	
}
function changeRegonCancel(regionId,text)
{
				var $td=$("#region_"+regionId);
				var str=text+'<i class="icon-pencil" onClick="changeRegonTD('+regionId+','+"'"+text+"'"+')"></i>';
                // 创建替换的input 对象   
                // 设置value值   
  				$td.html("");  
                // 清除td中的文本内容   
                $td.html(str);
}
 
function submitChangeRegion(regionId) {
	var value= $("#region_name_"+regionId).val();
	var serializeObj = {};
	serializeObj["name"]=value; 
	serializeObj["id"]=regionId; 
	serializeObj["token"]="${cse:htmlEscape(token)}"; 
	$.ajax({
        type: "POST",
        url:"${ctx}/cluster/region/change",
        data:serializeObj,
        error: function(request) {
        	top.handlePrompt("error",'<spring:message code="common.modifyFail"/>',null,10);
        },
        success: function() {
        	top.handlePrompt("success",'<spring:message code="common.modifySuccess"/>');
        	top.document.getElementById("regionManager").click();
        }
    });
}
function changeDataCenter(dcId,privilege,name)
{
	var $td=$("#datacenter_"+dcId);
	
	var str='<select id="select_'+dcId+'" class="span2">';
	if(privilege==0)
	{
		str=str+'<option value="0" selected="true" ><spring:message code="common.RW"/></option>';
	}else{
		str=str+'<option value="0"><spring:message code="common.RW"/></option>';
	}
	if(privilege==1)
	{
		str=str+'<option value="1" selected="true" ><spring:message code="common.ROnly"/></option>';
	}else{
		str=str+'<option value="1"><spring:message code="common.ROnly"/></option>';
	}
	str=str+'</select>';
	str=str+'<li class="icon-save" onClick="submitPrivilege('+dcId+','+"'"+name+"'"+')"></li>'			 			+
			 '<li class="icon-cancel" onClick="changePrivilege('+dcId+','+"'"+privilege+"',"+"'"+name+"'"+')"></li>';
	$td.html("");  
    $td.html(str)
}
function changePrivilege(dcId,privilege,name)
{
	var $td=$("#datacenter_"+dcId);
	var html='';
	if(privilege==0){
		html=html+'<spring:message code="common.RW"/>';
	}else
	{
		html=html+'<spring:message code="common.ROnly"/>';
	}
	html=html+'<i class="icon-pencil" onClick="changeDataCenter('+dcId+','+privilege+','+"'"+name+"'"+')"></i>';
	$td.html("");  
    $td.html(html)
}
function submitPrivilege(dcId,name)
{
	var code=$("#select_"+dcId).val();

	$.ajax({
        type: "POST",
        url:"${ctx}/cluster/region/updateRWStatus/"+dcId,
        data:{name:name,code:code,"token" : "${cse:htmlEscape(token)}"},
        error: function(request) {
        	top.handlePrompt("error",'<spring:message code="datacenter.role.modi.fa"/>');
        },
        success: function() {
        	top.handlePrompt("success",'<spring:message code="datacenter.role.modi.su"/>');
        	top.document.getElementById("regionManager").click();
        }
    });
}
function downLineDataCenter(dcId,name)
{
		top.ymPrompt.confirmInfo( {
			title :'<spring:message code="data.center.down.line.title"/>',
			message : '<spring:message code="data.center.down.line..clow"/>',
			closeTxt:'<spring:message code="common.close"/>',
			handler : function(tp) {
				if(tp == "ok"){
					$.ajax({
				        type: "POST",
				        url:"${ctx}/cluster/region/updateStatus/"+dcId,
				        data:{name:name,code:2,"token" : "${cse:htmlEscape(token)}"},
				        error: function(request) {
				        	top.handlePrompt("error",'<spring:message code="data.center.down.line.fa"/>');
				        },
				        success: function() {
				        	top.handlePrompt("success",'<spring:message code="data.center.down.line.su"/>');
				        	top.document.getElementById("regionManager").click();
				        }
				    });
				}
			},
		btn: [['<spring:message code="common.OK"/>', "ok"],['<spring:message code="common.cancel"/>', "cancel"]]
	});

}
function upLineDataCenter(dcId,name)
{
		top.ymPrompt.confirmInfo( {
			title :'<spring:message code="data.center.up.line.title"/>',
			message : '<spring:message code="data.center.up.line..clow"/>',
			closeTxt:'<spring:message code="common.close"/>',
			handler : function(tp) {
				if(tp == "ok"){
					$.ajax({
				        type: "POST",
				        url:"${ctx}/cluster/region/updateStatus/"+dcId,
				        data:{name:name,code:1,"token" : "${cse:htmlEscape(token)}"},
				        error: function(request) {
				        	top.handlePrompt("error",'<spring:message code="data.center.up.line.fa"/>');
				        },
				        success: function() {
				        	top.handlePrompt("success",'<spring:message code="data.center.up.line.su"/>');
				        	top.document.getElementById("regionManager").click();
				        }
				    });
				}
			},
		btn: [['<spring:message code="common.OK"/>', "ok"],['<spring:message code="common.cancel"/>', "cancel"]]
	});

}
function refreshWindow() {
	window.location.href="${ctx}/cluster/region/list";
}

</script>
</html>