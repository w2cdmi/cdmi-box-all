<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="Cache-Control" content="no-cache" />
<meta http-equiv="Pragma" content="no-cache" />
<title></title>

<%@ include file="../common/common.jsp"%>
</head>
<body>
	<div class="sys-content">
		<div class="form-horizontal form-con clearfix">
			<form class="form-horizontal" id="creatRegionDCForm"
				name="creatRegionDCForm">
				<!-- 
				<div class="control-group">
					<label class="control-label" for=""><em>*</em> <spring:message
							code="clusterManage.regionName" /></label>
					<div class="controls">
						<input type="text" id="regionName" name="regionName" class="span4"
							value="${region.regionName}" /> <span
							class="validate-con bottom"><div></div></span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for=""><em>*</em> <spring:message
							code="clusterManage.DCName" /></label>
					<div class="controls">
						<c:if test="${region.dcName != null}">
							<input type="text" id="dcName" name="dcName" readOnly="readonly"
								class="span4" value="${region.dcName}" />
						</c:if>
						<c:if test="${region.dcName == null}">
							<input type="text" id="dcName" name="dcName" class="span4" />
						</c:if>
						<span class="validate-con bottom"><div></div></span>
					</div>
				</div> -->
				<h5>
					<spring:message code="clusterManage.manageIp.title" />
				</h5>
				<div class="control-group" style="height:40px;padding-top:10px" id="server_ip">
					<label class="control-label" for=""><em>*</em> <spring:message
							code="clusterManage.manageIp" /></label>
					<div class="controls">
						<input type="hidden" id="regionId" name="regionId" value="${region.regionId}" />
						<input type="hidden" id="dcId" name="dcId" value="${region.dcId}" />
						<c:if test="${region.manageIp != null}">
							<input type="text" id="manageIp" name="manageIp" class="span4"
								readOnly="readonly" value="${region.manageIp}" />
						</c:if>
						<c:if test="${region.manageIp == null}">
							<input type="text" id="manageIp" name="manageIp" class="span4" />
						</c:if>
						<span class="validate-con bottom"><div></div></span>
					</div>
				</div>

				<h5>
					<spring:message code="clusterManage.storageType.title" />
				</h5>
				<div class="control-group" style="height:40px;padding-top:10px">
					<label class="control-label" for="input"><em>*</em> <spring:message
							code="clusterManage.storageType" /></label>
					<div class="controls" style="padding-top:4px;">
						<input type="radio" name="storageType" style="vertical-align:text-bottom;"
							value="0" onclick="switchStorageType(this)">&nbsp;&nbsp;<spring:message code="clusterManage.createNASStorage" /></input>&nbsp;&nbsp;&nbsp;&nbsp;
						<input type="radio" name="storageType" style="vertical-align:text-bottom;" checked="checked"
							value="1" onclick="switchStorageType(this)">&nbsp;&nbsp;<spring:message code="clusterManage.createUDSStorage" /></input>
					</div>
				</div>
				<div class="control-group"  id="nas_storage" style="display:none">
					<div class="control-group">
						<label class="control-label" for="input"><em>*</em> <spring:message
								code="clusterManage.storePath" /></label>
						<div class="controls">
							<input class="span4" type="text" id="path" name="path"
								value="${region.path}" /> <span class="validate-con bottom"><div></div></span>
						</div>
					</div>
				</div>
				<div class="control-group"  id="cloud_storage" style="display:none">
					<div class="control-group">
						<label class="control-label" for=""><em>*</em>
						<spring:message code="storage.class.provider" />:</label>
						<div class="controls">
							<select id="provider" class="span4" name="provider">
								<option value="QYOS"<c:if test="${region.provider} == 'QYOS'"> selected = "selected"</c:if>>青云对象存储</option>
								<option value="HWOBS"<c:if test="${region.provider} == 'HWOBS'"> selected = "selected"</c:if>>华为OBS存储</option>
								<option value="ALIOSS"<c:if test="${region.provider} == 'ALIOSS'"> selected = "selected"</c:if>>阿里标准云存储</option>
								<option value="ALIAI"<c:if test="${region.provider} == 'ALIAI'"> selected = "selected"</c:if>>阿里AI云存储</option>
								<option value="TENTOBS"<c:if test="${region.provider} == 'TENTOBS'"> selected = "selected"</c:if>>腾讯云存储</option>
							</select> <span class="validate-con bottom"><div></div></span>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for=""> <!-- <em>*</em> --> <spring:message
								code="clusterManage.domainName" />:
						</label>
						<div class="controls">
							<input type="text" id="domain" class="span4" name="domain"
								placeholder='<spring:message code="cluster.uds.domain"/>'
								value="${region.domain}" />
							<span class="validate-con bottom"><div></div></span> <span
								class="help-block"><spring:message
									code="cluster.user.dns.assa.uds" /></span>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for=""> <!-- <em>*</em> --> <spring:message
								code="clusterManage.httpPort" />:
						</label>
						<div class="controls">
							<input type="text" id="httpPort" class="span4" name="httpPort"
								value="${region.httpPort}" />
							<span class="validate-con bottom"><div></div></span>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for=""> <!-- <em>*</em> --> <spring:message
								code="clusterManage.httpsPort" />:
						</label>
						<div class="controls">
							<input type="text" id="httpsPort" class="span4" name="httpsPort"
								value="${region.httpsPort}"/>
							<span class="validate-con bottom"><div></div></span>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for=""> <!-- <em>*</em> --> <spring:message
								code="clusterManage.accessKey" />:
						</label>
						<div class="controls">
							<input type="text" id="accessKey" class="span4" name="accessKey"
								value="${region.accessKey}"/>
							<span class="validate-con bottom"><div></div></span> <span
								class="help-block"><spring:message
									code="cluster.user.dns.assa.uds.aksk" /></span>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for=""> <!-- <em>*</em> --> <spring:message
								code="clusterManage.secretKey" />:
						</label>
						<div class="controls">
							<input type="password" id="secretKey" class="span4"
								name="secretKey" autocomplete="off" value="${region.secretKey}"/>
							<span class="validate-con bottom"><div></div></span>
						</div>
					</div>
				</div>
				<%-- <div class="control-group">
	            <label class="control-label" for="input"><spring:message code="authorize.user.pwd.confirm"/></label>
	            <div class="controls">
	                <input class="span4" type="password" id="confirmPassword" name="confirmPassword" value="" autocomplete="off"/>
	                <span class="validate-con bottom"><div></div></span>
	            </div>
	        </div> --%>
				<div class="control-group">
					<div class="controls">
						<button id="submit_btn" type="button" onclick="saveStorage()"
							class="btn btn-primary">
							<spring:message code="common.save" />
						</button>
					</div>
				</div>
				<%-- <input type="hidden" name="token" value="${cse:htmlEscape(token)}"/> --%>
			</form>
			<%-- <div class="control-group">
				<div class="controls">
					<button id="previous_btn" type="button" onclick="previousStep()"
						class="btn btn-primary">
						<spring:message code="isystem.init.config.last" />
					</button>
				</div>
			</div>
			<div class="control-group">
				<div class="controls">
					<button id="nest_btn" type="button" onclick="nextStep()"
						class="btn btn-primary">
						<spring:message code="isystem.init.config.last" />
					</button>
				</div>
			</div> --%>
		</div>
	</div>
</body>
<script type="text/javascript">
	$(document).ready(function() {

		$.validator.addMethod("isDCName", function(value, element) {
			var validName = /^[a-zA-Z]{1}[a-zA-Z0-9]*$/;
			return validName.test(value);
		}, $.validator.format('<spring:message code="dataceter.name.rule"/>'));

		$.validator.addMethod("isRegionName", function(value, element) {
			var validName = /^[a-zA-Z]{1}[a-zA-Z0-9]*$/;
			return validName.test(value);
		}, $.validator.format('<spring:message code="region.name.rule"/>'));

		$("#server_ip").validate({
			rules : {
				manageIp : {
					required : true,
					rangelength : [ 1, 45 ]
				}
			}
		});
		
		$("#nas_storage").validate({
			rules : {
			    path : {
					required : true,
					rangelength : [ 2, 256 ]
				}
			}
		});
		
		$("#cloud_storage").validate({
			rules : {
			    provider : {
					required : true,
				},
				domain : {
					required : true,
					rangelength : [ 1, 128 ]
				},
				accessKey : {
					required : true,
					rangelength : [ 1, 128 ],
				},
				secretKey : {
					required : true,
					rangelength : [ 1, 256 ]
				}
			}
		});
		$("label").tooltip({
			container : "body",
			placement : "top",
			delay : {
				show : 100,
				hide : 0
			},
			animation : false
		});
		
		var chkRadio = $('input:radio[name="storageType"]:checked').val();
		if(chkRadio==0){
			$("#cloud_storage").hide();
			$("#nas_storage").show();
		}else{
			$("#cloud_storage").show();
			$("#nas_storage").hide();
		}
	});

	function switchStorageType(object) {
	    if(object.value ==0){
			$("#cloud_storage").hide();
			$("#nas_storage").show();
	    }else{
			$("#cloud_storage").show();
			$("#nas_storage").hide();
	    }
	}
	
	function saveStorage() {
	    
		if ($("#manageIp").val().length < 7) {
			return false;
		}

		var chkRadio = $('input:radio[name="storageType"]:checked').val();
		if(chkRadio==0){
		    var path = $("#path").val();
			
			if(path != null && path != ""){
				$.ajax({
					type : "POST",
					async : false,
					url : "${ctx}/systeminit/nasstorage/save",
					data : $('#creatRegionDCForm').serialize(),
					error : function(request) {
						top.handlePrompt("error",
								'<spring:message code="common.saveFail"/>');
					},
					success : function(data) {
						top.handlePrompt("success",
								'<spring:message code="common.saveSuccess"/>');
						$("#submit_btn").attr("disabled", true);
					}
				});
			} else {
				top.handlePrompt("error",
						'<spring:message code="common.saveFail"/>');
			}
		}else{
			var domain = $("#domain").val();
			var httpPort = $("#httpPort").val();
			var accessKey = $("#accessKey").val();
			var secretKey = $("#secretKey").val();
			if ((domain != null && domain != "" && httpPort != null
							&& httpPort != "" && accessKey != null && accessKey != ""
							&& secretKey != null && secretKey != "")) {
				$.ajax({
					type : "POST",
					async : false,
					url : "${ctx}/systeminit/cloudstorage/save ",
					data : $('#creatRegionDCForm').serialize(),
					error : function(request) {
						top.handlePrompt("error",
								'<spring:message code="common.saveFail"/>');
					},
					success : function(data) {
						top.handlePrompt("success",
								'<spring:message code="common.saveSuccess"/>');
						$("#submit_btn").attr("disabled", true);
					}
				});
			} else {
				top.handlePrompt("error",
						'<spring:message code="common.saveFail"/>');
			}
		}
	}
</script>
</html>