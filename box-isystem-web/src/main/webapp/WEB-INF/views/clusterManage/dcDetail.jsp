<%@page import="com.fasterxml.jackson.annotation.JsonInclude.Include"%>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
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
		<div class="clearfix">
			<div class="pull-left">
				<label class="control-label" for=""><h5>
						<spring:message code="cluster.storage" />
						${cse:htmlEscape(dataCenter.region.name)})
					</h5></label>
			</div>
			<div class="pull-right">
				<a class="return" href="${ctx}/cluster/region/list">&lt;&lt;<spring:message
						code="common.back" /></a>
			</div>
		</div>

		<c:if test="${dataCenter.status.code == useStatusInit}">
			<button class="btn btn-primary" type="button"
				onClick="activateDCRGroup(${cse:htmlEscape(dataCenter.id)})" />
			<i class="icon-sq-br-next"></i>
			<spring:message code="cluster.centerData.enable" />
			</button>
			<button class="btn btn-primary" type="button"
				onClick="deleteDCRGroup(${cse:htmlEscape(dataCenter.id)})" />
			<i class="icon-delete-blod"></i>
			<spring:message code="cluster.centerData.del" />
			</button>
		</c:if>
		<button class="btn btn-primary" type="button"
			onClick="modifyDomainName(${cse:htmlEscape(dataCenter.id)})" />
		<i class="icon-suitcase"></i>
		<spring:message code="cluster.domain.set" />
		</button>
		<button class="btn btn-primary" type="button" onClick="refreshPage()" />
		<i class="icon-refresh"></i>
		<spring:message code="common.refurbish" />
		</button>
		<div class="table-con">
			<table class="table table-bordered table-striped">
				<thead>
					<tr>
						<th><spring:message code="clusterManage.DCName" /></th>
						<th><spring:message code="clusterManage.manageIp" /></th>
						<th><spring:message code="clusterManage.managePort" /></th>
						<th><spring:message code="clusterManage.domainName" /></th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td>${cse:htmlEscape(dataCenter.name)}</td>
						<td>${cse:htmlEscape(dataCenter.resourceGroup.manageIp)}</td>
						<td>${cse:htmlEscape(dataCenter.resourceGroup.managePort)}</td>
						<td><c:choose>
								<c:when test="${empty dataCenter.resourceGroup.domainName}">-</c:when>
								<c:otherwise>${cse:htmlEscape(dataCenter.resourceGroup.domainName)} </c:otherwise>
							</c:choose>
						</td>
					</tr>
				</tbody>
			</table>
		</div>

		<h5>
			<spring:message code="clusterManage.nodeList" />
		</h5>
		<div class="table-con">
			<table class="table table-bordered table-striped">
				<thead>
					<tr>
						<th><spring:message code="clusterManage.nodeIP" /></th>
						<th><spring:message code="clusterManage.serviceAddr" /></th>
						<th><spring:message code="clusterManage.runStatus" /></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${dataCenter.resourceGroup.nodes}" var="node">
						<tr>
							<td>${cse:htmlEscape(node.managerIp)}</td>
							<td>${cse:htmlEscape(node.serviceAddr)}</td>
							<td><c:if
									test="${node.runtimeStatus.code == runStatusNormal}">
									<img alt='<spring:message code="common.normal"/>'
										"" src="${ctx}/static/image/state/normal.png"
										title='<spring:message code="common.normal"/>' />
								</c:if> <c:if test="${node.runtimeStatus.code == runStatusAbnormal}">
									<img alt='<spring:message code="common.exception"/>'
										src="${ctx}/static/image/state/exception.png"
										title='<spring:message code="common.exception"/>' />
								</c:if> <c:if test="${node.runtimeStatus.code == runStatusOffline}">
									<img alt='<spring:message code="common.offline"/>'
										src="${ctx}/static/image/state/offline.png"
										title='<spring:message code="common.offline"/>' />
								</c:if>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>

		<h5>
			<spring:message code="clusterManage.dataStorage" />
		</h5>
		<div class="handle-con clearfix">
			<div class="pull-left">
				<button type="button" class="btn btn-primary btn-small"
					onClick="createUDSStorage()">
					<i class="icon-add"></i>
					<spring:message code="clusterManage.createUDSStorage" />
				</button>
			</div>
		</div>
		<div class="table-con">
			<table class="table table-bordered table-striped">
				<thead>
					<tr>
						<th style="width:20%"><spring:message code="common.regionName" /></th>
						<th style="width:10%"><spring:message code="common.httpPort" /></th>
						<th style="width:10%"><spring:message code="common.httpsPort" /></th>
						<th style="width:20%"><spring:message code="common.accessKey" /></th>
						<th style="width:20%"><spring:message code="common.secretKey" /></th>
						<th style="width:10%"><spring:message code="common.readAndWrite.stauts" /></th>
						<th style="width:20%"><spring:message code="common.storageclass" /></th>
						<th style="width:20%"><spring:message code="common.operation" /></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${udsInfos}" var="storageRes">
						<tr>
							<td>${cse:htmlEscape(storageRes.domain)}</td>
							<td>${cse:htmlEscape(storageRes.port)}</td>
							<td>${cse:htmlEscape(storageRes.httpsport)}</td>
							<td title="${cse:htmlEscape(storageRes.accessKey)}">${cse:htmlEscape(storageRes.accessKey)}</td>
							<td>****************</td>
							<td><c:choose>
									<c:when test="${storageRes.availAble}">
										<c:choose>
											<c:when
												test="${storageRes.status != STORAGE_RES_STATUS_ENABLE}">
												<spring:message code="common.ROnly" />
											</c:when>
											<c:otherwise>
												<c:choose>
													<c:when test="${storageRes.writeAlbe}">
														<spring:message code="common.RW" />
													</c:when>
													<c:otherwise>
														<spring:message code="common.ROnly" />
													</c:otherwise>
												</c:choose>
											</c:otherwise>
										</c:choose>
									</c:when>
									<c:otherwise>
										<spring:message code="common.exception" />
									</c:otherwise>
								</c:choose></td>
							<td><spring:message code="${cse:htmlEscape(storageRes.provider)}" /></td>
							<td><c:choose>
									<c:when
										test="${storageRes.status == STORAGE_RES_STATUS_NOTUSE}">
										<button class="btn" type="button"
											onClick="enableStorageRes('${cse:htmlEscape(storageRes.fsId)}')" />
										<spring:message code="common.start" />
										</button>
										<button class="btn" type="button"
											onClick="deleteStorageRes('${cse:htmlEscape(storageRes.fsId)}')" />
										<spring:message code="common.delete" />
										</button>
									</c:when>
									<c:when
										test="${storageRes.status == STORAGE_RES_STATUS_ENABLE}">
										<button class="btn" type="button"
											onClick="disableStorageRes('${cse:htmlEscape(storageRes.fsId)}')" />
										<spring:message code="common.stop" />
										</button>
									</c:when>
									<c:when
										test="${storageRes.status == STORAGE_RES_STATUS_DISABLE}">
										<button class="btn" type="button"
											onClick="enableStorageRes('${cse:htmlEscape(storageRes.fsId)}')" />
										<spring:message code="common.start" />
										</button>
									</c:when>
								</c:choose>
								<button class="btn" type="button"
									onClick="changeUDSStorage('${cse:htmlEscape(storageRes.fsId)}')" /> <spring:message
									code="common.change" />
								</button></td>
						</tr>
					</c:forEach>
					<c:if test="${empty udsInfos}">
						<tr>
							<c:if test="${storageFail eq 'yes'}">
								<td colspan="6" style="text-align:center"><spring:message
										code="cluster.storage.list.timeout" /><a
									href="javascript:refreshPage()"><spring:message
											code="common.refurbish" /></a> <spring:message
										code="common.reload" /></td>
							</c:if>
							<c:if test="${empty storageFail}">
								<td colspan="6" style="text-align:center"><spring:message
										code="cluster.storage.add.info" /><a
									href="javascript:createUDSStorage()"><spring:message
											code="common.create" /></a>。</td>
							</c:if>
						</tr>
					</c:if>
				</tbody>
			</table>
		</div>


		<div class="handle-con clearfix">
			<div class="pull-left">
				<button type="button" class="btn btn-primary btn-small"
					onClick="createNASStorage()">
					<i class="icon-add"></i>
					<spring:message code="clusterManage.createNASStorage" />
				</button>
			</div>
		</div>
		<div class="table-con">
			<table class="table table-bordered table-striped">
				<thead>
					<tr>
						<th style="width:20%"><spring:message code="clusterManage.storePath" /></th>
						<th style="width:10%"><spring:message code="clusterManage.totalSpace" /></th>
						<th style="width:10%"><spring:message code="clusterManage.availableSpace" /></th>
						<th style="width:10%"><spring:message code="clusterManage.usedRatio" /></th>
						<th style="width:10%"><spring:message code="clusterManage.maxUtilization" /></th>
						<th style="width:10%"><spring:message code="clusterManage.retrieval" /></th>
						<th style="width:10%"><spring:message code="common.readAndWrite.stauts" /></th>
						<th style="width:20%"><spring:message code="common.operation" /></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${nasInfos}" var="storageRes">
						<tr>
							<td title="${cse:htmlEscape(storageRes.path)}">${cse:htmlEscape(storageRes.path)}</td>
							<td><fmt:formatNumber type='number' value="${storageRes.spaceSize/(1024 * 1024 * 1024 * 1024)}" maxFractionDigits="2"/>TB</td>
							<td><fmt:formatNumber type='number' value="${(storageRes.spaceSize - storageRes.usedSize)/(1024 * 1024 * 1024 * 1024)}" maxFractionDigits="2"/>TB</td>
							<td>${cse:htmlEscape(storageRes.usedRatio)}%</td>
							<td><c:out value="${storageRes.maxUtilization}"></c:out>%</td>
							<td><c:out value="${storageRes.retrieval}"></c:out>%</td>
							<td><c:choose>
									<c:when test="${storageRes.availAble}">
										<c:choose>
											<c:when
												test="${storageRes.status != STORAGE_RES_STATUS_ENABLE}">
												<spring:message code="common.ROnly" />
											</c:when>
											<c:otherwise>
												<c:choose>
													<c:when test="${storageRes.noSpace || !storageRes.writeAlbe}">
														<spring:message code="common.ROnly" />
													</c:when>
													<c:otherwise>
														<spring:message code="common.RW" />
													</c:otherwise>
												</c:choose>
											</c:otherwise>
										</c:choose>
									</c:when>
									<c:otherwise>
										<spring:message code="common.exception" />
									</c:otherwise>
								</c:choose></td>
							<td><c:choose>
									<c:when
										test="${storageRes.status == STORAGE_RES_STATUS_NOTUSE}">
										<button class="btn" type="button"
											onClick="enableStorageRes('${cse:htmlEscape(storageRes.fsId)}')" />
										<spring:message code="common.start" />
										</button>
										<button class="btn" type="button"
											onClick="deleteStorageRes('${cse:htmlEscape(storageRes.fsId)}')" />
										<spring:message code="common.delete" />
										</button>
									</c:when>
									<c:when
										test="${storageRes.status == STORAGE_RES_STATUS_ENABLE}">
										<button class="btn" type="button"
											onClick="disableStorageRes('${cse:htmlEscape(storageRes.fsId)}')" />
										<spring:message code="common.stop" />
										</button>
									</c:when>
									<c:when
										test="${storageRes.status == STORAGE_RES_STATUS_DISABLE}">
										<button class="btn" type="button"
											onClick="enableStorageRes('${cse:htmlEscape(storageRes.fsId)}')" />
										<spring:message code="common.start" />
										</button>
									</c:when>
								</c:choose>
								<button class="btn" type="button"
									onClick="changeNASStorage('${cse:htmlEscape(storageRes.fsId)}')" > <spring:message
									code="common.change" />
								</button></td>
						</tr>
					</c:forEach>
					<c:if test="${empty nasInfos}">
						<tr>
							<c:if test="${storageFail eq 'yes'}">
								<td colspan="8" style="text-align:center"><spring:message
										code="cluster.storage.list.timeout" /><a
									href="javascript:refreshPage()"><spring:message
											code="common.refurbish" /></a> <spring:message
										code="common.reload" /></td>
							</c:if>
							<c:if test="${empty storageFail}">
								<td colspan="8" style="text-align:center"><spring:message
										code="cluster.storage.add.info" /><a
									href="javascript:createNASStorage()"><spring:message
											code="common.create" /></a>。</td>
							</c:if>
						</tr>
					</c:if>
				</tbody>
			</table>
		</div>

		<c:if test="${!isMergeDC}">
			<h5>
				<spring:message code="clusterManage.logagent.config" />
			</h5>
			<div class="form-horizontal form-con clearfix">
				<form id="logAgentFSEndpointForm" class="form-horizontal"
					method="post">
					<div class="alert alert-error input-medium controls" id="errorTip"
						style="display:none">
						<button class="close" data-dismiss="alert">×</button>
						<spring:message code="common.saveFail" />
					</div>
					<div class="control-group">
						<label class="control-label" for="input"><em>*</em> <spring:message
								code="common.type" /> :</label>
	                    <div class="controls">
			            	<label class="radio inline"><input type="radio" id="udsRadio" name="fsType" value="uds" onclick="setStorageType('uds')" ${(fsType == 'uds' || fsType == null) ? "checked='checked'" : ""}/>UDS</label>
			            	<label class="radio inline"><input type="radio" id="nasRadio"  name="fsType" value="nas" onclick="setStorageType('nas')" ${fsType == 'nas' ? "checked='checked'" : ""}/>NAS</label>
			            </div>
					</div>
					
					<div class="control-group" id="udsConfig">
						<div class="control-group">
							<label class="control-label" for=""><em>*</em> <spring:message
									code="clusterManage.domainName" /> :</label>
							<div class="controls">
								<input type="text" id="domain" class="span4" name="domain"
									value="${cse:htmlEscape(domain)}"
									placeholder='<spring:message code="cluster.uds.domain"/>' /> <span
									class="validate-con bottom"><div></div></span> <span
									class="help-block"><spring:message
										code="cluster.user.dns.assa.uds" /></span>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for=""><em>*</em> <spring:message
									code="clusterManage.httpPort" /> :</label>
							<div class="controls">
								<input type="text" id="port" class="span4" name="port"
									value="${cse:htmlEscape(port)}" /> <span class="validate-con bottom"><div></div></span>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for=""><em>*</em> <spring:message
									code="clusterManage.httpsPort" /> :</label>
							<div class="controls">
								<input type="text" id="port" class="span4" name="httpsport"
									value="${cse:htmlEscape(httpsport)}" /> <span class="validate-con bottom"><div></div></span>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for=""><em>*</em> <spring:message
									code="clusterManage.accessKey" /> :</label>
							<div class="controls">
								<input type="text" id="ak" class="span4" name="ak" value="${cse:htmlEscape(ak)}" />
								<span class="validate-con bottom"><div></div></span>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for=""><em>*</em> <spring:message
									code="clusterManage.secretKey" /> :</label>
							<div class="controls">
								<input type="password" id="sk" class="span4" name="sk"
									value="${cse:htmlEscape(sk)}" autocomplete="off"/> <span class="validate-con bottom"><div></div></span>
							</div>
						</div>
					</div>
					
					<div class="control-group" id="nasConfig">
						<div class="control-group">
				        	<label class="control-label" for=""><em>*</em><spring:message code="clusterManage.storePath"/>:</label>
				            <div class="controls">
				                <input type="text" id="path" class="span4" name="path" value="${cse:htmlEscape(path)}" />
				                <span class="validate-con bottom"><div></div></span>
				                <span class="help-block"><spring:message code="clusterManage.assa.storePath"/></span>
				            </div>
				        </div>
					</div>

					<div class="control-group">
						<div class="controls">
							<button id="submit_btn" type="button"
								onClick="saveLogAgentSetting(${cse:htmlEscape(dataCenter.id)})"
								class="btn btn-primary">
								<spring:message code="common.save" />
							</button>
						</div>
					</div>
				</form>
			</div>
		</c:if>

	</div>
</body>
<script type="text/javascript">
var storageFail = "${cse:htmlEscape(storageFail)}";
$(document).ready(function() {
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
	
	$.validator.addMethod(
			"udsRequired", 
			function(value, element) {   
				value = $.trim(value);
				var fsType = $("input[name='fsType']:checked").val();
				if(fsType == "uds" && value == ""){
					return false;
			    }
				return true;
			}
		);
	
	$.validator.addMethod(
			"nasRequired", 
			function(value, element) {
				value = $.trim(value);
				var fsType = $("input[name='fsType']:checked").val();
				if(fsType == "nas" && value == ""){
					return false;
			    }
				return true;
			}
		);
	
	$("#logAgentFSEndpointForm").validate({ 
		rules: { 
			   domain:{
				   udsRequired:true, 
			       maxlength:[255]
			   },
			   port: { 
				   udsRequired:true,  
			       digits:true,
			       min:1,
			       maxlength:[5]
			   },
			   httpsport: { 
				   udsRequired:true,  
			       digits:true,
			       min:1,
			       maxlength:[5]
			   },
			   ak:{
				   udsRequired:true, 
			       maxlength:[64]
			   },
			   sk:{
				   udsRequired:true, 
			       maxlength:[64]
			   },
			   path:{
				   nasRequired:true, 
			       maxlength:[128]
			   }
		},
		messages: { 
			domain: { 
				udsRequired:"<spring:message code='message.notNull'/>"
			},
			port: { 
				udsRequired:"<spring:message code='message.notNull'/>"
			},
			httpsport: { 
				udsRequired:"<spring:message code='message.notNull'/>"
			},
			ak: { 
				udsRequired:"<spring:message code='message.notNull'/>"
			},
			sk: { 
				udsRequired:"<spring:message code='message.notNull'/>"
			},
			path: { 
				nasRequired:"<spring:message code='message.notNull'/>"
			}
		}
	});
	var storageType = "${cse:htmlEscape(fsType)}";
	if(storageType == "nas"){
		$("#nasConfig").click();
		setStorageType("nas");
	}else{
		setStorageType("uds");
	}
});
function saveLogAgentSetting(clusterId){
	$("#submit_btn").attr("disabled","true");
	if(!$("#logAgentFSEndpointForm").valid()) {
		$("#submit_btn").removeAttr("disabled");
        return false;
    } 
	var fsType = $("input[name='fsType']:checked").val();
	var endpoint = null;
	if(fsType == "uds"){
		endpoint = $("#domain").val() + ":" + $("#port").val() + ":" + $("#ak").val() + ":" + $("#sk").val() ;
	}else if(fsType == "nas"){
		endpoint = $("#path").val();
	}
	var params= {
			    "fsType": $("input[name='fsType']:checked").val(), 
			    "endpoint": endpoint,
			    "token":"${cse:htmlEscape(token)}"
	};
	 
	$.ajax({
		type: "POST",
		url:"${ctx}/cluster/dcdetailmanage/logagentconfig/"+ clusterId + "/save",
		data:params,
			error: function(request) {
				top.handlePrompt("error",'<spring:message code="common.saveFail"/>');
				$("#submit_btn").removeAttr("disabled");
			},
			success: function() {
				$("#sk").val("");
				top.handlePrompt("success",'<spring:message code="common.saveSuccess"/>');
				$("#submit_btn").removeAttr("disabled");
			}
	});
}

function createUDSStorage(){
	top.ymPrompt.win({
		message:'${ctx}/cluster/dcdetailmanage/enterCreateUDSStorage/${cse:htmlEscape(dcId)}',width:700,height:520,
		title:'<spring:message code="clusterManage.createUDSStorage"/>', iframe:true,
		btn:[['<spring:message code="common.create"/>','yes',false,"btn-focus"],['<spring:message code="common.cancel"/>','no',true,"btn-cancel"]],handler:saveStorage
	});
	top.ymPrompt_addModalFocus("#btn-focus");
}

function createNASStorage(){
	top.ymPrompt.win({
		message:'${ctx}/cluster/dcdetailmanage/enterCreateNASStorage/${cse:htmlEscape(dcId)}',width:700,height:370,
		title:'<spring:message code="clusterManage.createNASStorage"/>', iframe:true,
		btn:[['<spring:message code="common.create"/>','yes',false,"btn-focus"],['<spring:message code="common.cancel"/>','no',true,"btn-cancel"]],handler:saveStorage
	});
	top.ymPrompt_addModalFocus("#btn-focus");
}

function changeUDSStorage(storageResId){
	top.ymPrompt.win({
		message:'${ctx}/cluster/dcdetailmanage/enterChangeStorage/${cse:htmlEscape(dcId)}/' + storageResId, width:700,height:300,
		title:'<spring:message code="clusterManage.changeUDSStorage"/>', iframe:true,
		btn:[['<spring:message code="common.save"/>','yes',false,"btn-focus"],['<spring:message code="common.cancel"/>','no',true,"btn-cancel"]],handler:saveStorage
	});
	top.ymPrompt_addModalFocus("#btn-focus");
}

function changeNASStorage(storageResId){
	top.ymPrompt.win({
		message:'${ctx}/cluster/dcdetailmanage/enterChangeStorage/${cse:htmlEscape(dcId)}/' + storageResId, width:700,height:370,
		title:'<spring:message code="clusterManage.changeNASStorage"/>', iframe:true,
		btn:[['<spring:message code="common.save"/>','yes',false,"btn-focus"],['<spring:message code="common.cancel"/>','no',true,"btn-cancel"]],handler:saveStorage
	});
	top.ymPrompt_addModalFocus("#btn-focus");
}

function saveStorage(tp) {
	if (tp == 'yes') {
		top.ymPrompt.getPage().contentWindow.submitStorage();
	} else {
		top.ymPrompt.close();
	}
}
function enableStorageRes(storageResId){
	$.ajax({
        type: "POST",
        url:"${ctx}/cluster/dcdetailmanage/enableStorageRes",
        data:{token: "${cse:htmlEscape(token)}",dcId:	"${cse:htmlEscape(dcId)}",storageResId:storageResId},
        error: function(request) {
        	top.handlePrompt("error",'<spring:message code="common.enable.fail"/>');
        },
        success: function() {
        	top.handlePrompt("success",'<spring:message code="clusterManage.enableStorageSuccess"/>');
        	window.location.reload();
        }
    });
}
function refreshPage(){
	window.location.reload();
}
function disableStorageRes(storageResId){
	top.ymPrompt.confirmInfo( {
		title :'<spring:message code="clusterManage.stopStorage"/>',
		message : '<spring:message code="clusterManage.stopStorageConfirm"/>',
		closeTxt:'<spring:message code="common.close"/>',
		handler : function(tp) {
			if(tp == "ok"){
				$.ajax({
			        type: "POST",
			        url:"${ctx}/cluster/dcdetailmanage/disableStorageRes",
			        data:{token:"${cse:htmlEscape(token)}",dcId:"${cse:htmlEscape(dcId)}",storageResId:storageResId},
			        error: function(request) {
			        	top.handlePrompt("error",'<spring:message code="clusterManage.stopStorageFail"/>');
			        },
			        success: function() {
			        	top.handlePrompt("success",'<spring:message code="clusterManage.stopStorageSuccess"/>');
			        	refreshWindow();
			        }
			    });
			}
		},
		btn: [['<spring:message code="common.OK"/>', "ok"],['<spring:message code="common.cancel"/>', "cancel"]]
	});
}
function deleteStorageRes(storageResId){
	top.ymPrompt.confirmInfo( {
		title :'<spring:message code="clusterManage.delStorage"/>',
		message : '<spring:message code="clusterManage.delStorageConfirm"/>',
		closeTxt:'<spring:message code="common.close"/>',
		handler : function(tp) {
			if(tp == "ok"){
				$.ajax({
			        type: "POST",
			        url:"${ctx}/cluster/dcdetailmanage/deleteStorageRes",
			        data:{token:"${cse:htmlEscape(token)}",dcId:"${cse:htmlEscape(dcId)}",storageResId:storageResId},
			        error: function(request) {
			        	top.handlePrompt("error",'<spring:message code="clusterManage.delStorageFail"/>');
			        },
			        success: function() {
			        	top.handlePrompt("success",'<spring:message code="clusterManage.delStorageSuccess"/>');
			        	refreshWindow();
			        }
			    });
			}
		},
		btn: [['<spring:message code="common.OK"/>', "ok"],['<spring:message code="common.cancel"/>', "cancel"]]
	});
}


function activateDCRGroup(id){
	$.ajax({
        type: "POST",
        url:"${ctx}/cluster/dcmanage/activate",
        data:{token:"${cse:htmlEscape(token)}",id:id},
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
		title :'<spring:message code="cluster.centerData.del"/>',
		message : '<spring:message code="cluster.centerData.del.infomation"/>',
		closeTxt:'<spring:message code="common.close"/>',
		handler : function(tp) {
			if(tp == "ok"){
				$.ajax({
			        type: "POST",
			        url:"${ctx}/cluster/dcmanage/delete",
			        data:{token:"${cse:htmlEscape(token)}",id:id},
			        error: function(request) {
			        	top.handlePrompt("error",'<spring:message code="cluster.centerData.del.fail"/>');
			        },
			        success: function() {
			        	top.handlePrompt("success",'<spring:message code="cluster.centerData.del.success"/>');
			        	window.location = "${ctx}/cluster/region/list";
			        }
			    });
			}
		},
		btn: [['<spring:message code="common.OK"/>', "ok"],['<spring:message code="common.cancel"/>', "cancel"]]
	});
}
function modifyDomainName(id){
	top.ymPrompt.win({
		message:'${ctx}/cluster/dcdetailmanage/enterSetDomainName/' + id,
		width:700,height:220,
		title:'<spring:message code="cluster.domain.set"/>', iframe:true,
		btn:[['<spring:message code="common.set"/>','yes',false,"btn-focus"],['<spring:message code="common.cancel"/>','no',true,"btn-cancel"]],handler:doModifyDomainName
	});
	top.ymPrompt_addModalFocus("#btn-focus");
}

function doModifyDomainName(tp) {
	if (tp == 'yes') {
		top.ymPrompt.getPage().contentWindow.submitModifyDomainName();
	} else {
		top.ymPrompt.close();
	}
}
	
function setStorageType(val){
	if(val == "uds"){
		$("#udsConfig").show();
		$("#nasConfig").hide();
	}else{
		$("#udsConfig").hide();
		$("#nasConfig").show();
	}
}
</script>
</html>