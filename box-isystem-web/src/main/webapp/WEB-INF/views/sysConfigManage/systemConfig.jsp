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
	<div class="sys-content sys-content-en">
		<h5><spring:message code="sysconfig.amylose" /></h5>
		<div class="alert">
			<i class="icon-lightbulb icon-orange"></i>
			<spring:message code="sysconfig.linearChain.config" />
		</div>
		<div class="form-horizontal form-con clearfix">
			<form id="directForm" class="form-horizontal" method="post">
				<div class="alert alert-error input-medium controls" id="errorTip"
					style="display:none">
					<button class="close" data-dismiss="alert">×</button>
					<spring:message code="common.saveFail" />
				</div>
				<div class="control-group">
					<label class="control-label" for="input"><em>*</em>
					<spring:message code="sysconfig.linearChain.config.path" /></label>
					<div class="controls">
						<input class="span4" type="text" id="directPath" name="path" placeholder='<spring:message code="sysconfig.linearChain.config.path.holder"/>'
							value="${cse:htmlEscape(directConfig.path)}" />
							<span class="validate-con"><div></div></span>
						<button id="submit_btn" type="button"
							onClick="directChainSetting()" class="btn btn-primary">
							<spring:message code="common.save" />
						</button>						
						<span class="help-block"><spring:message code="sysconfig.linearChain.config.path.help" /></span>
					</div>
				</div>		
			</form>
		</div>
		
		<h5><spring:message code="security.secmatrix" /></h5>
		<div class="form-horizontal form-con clearfix">
			<form id="securitymatrix" class="form-horizontal" method="post">
				<div class="alert alert-error input-medium controls" id="errorTip"
					style="display:none">
					<button class="close" data-dismiss="alert">×</button>
					<spring:message code="common.saveFail" />
				</div>
				<div class="control-group">
					<label class="control-label" for="input"><em>*</em>
					<spring:message code="sysconfig.secmatrix.config" /></label>
					<div class="controls">
						<label class="checkbox inline" for="input">
						<input type="checkbox" id="secmatrix" name="secmatrix" />
						<spring:message code="common.start" />&nbsp;&nbsp;</label>
						<button id="submit_btn" type="button"
							onClick="secmatrixSetting()" class="btn btn-primary">
							<spring:message code="common.save" />
						</button>
					</div>
				</div>
			</form>
		</div>
		
		<h5><spring:message code="log.language.title" /></h5>
		<div class="form-horizontal form-con clearfix">
			<form id="logLanguageForm" class="form-horizontal" method="post">
				<div class="control-group">
		            <label class="control-label" for="input"><spring:message code="log.language.languageLable"/></label>
		            <div class="controls">
		                <select class="span4" id="protocolType" name="language">
							<option value="zh" <c:if test="${!(empty logLanguage.language)&&logLanguage.language == 'zh'}">selected="selected"</c:if>><spring:message code="common.simplified.chinese"/></option>
							<option value="en" <c:if test="${(empty logLanguage.language)||logLanguage.language == 'en'}">selected="selected"</c:if>>English</option>
						</select>
		            </div>
		        </div>
		        <div class="control-group">
		        	<label class="control-label" for="input"><spring:message code="log.language.logEnable"/></label>
		        	<div class="controls">
		        		<label class="radio inline" for="input"><input type="radio"   name="config" value="1" <c:if test="${logLanguage.config==1}">checked="checked"</c:if> /><spring:message code="log.language.logEnable.yes"/></label>
		        		<label class="radio inline" for="input"><input type="radio"   name="config" value="0" <c:if test="${logLanguage.config==0}">checked="checked"</c:if> /><spring:message code="log.language.logEnable.no"/></label>
		        	</div>
		        </div>
		        <div class="control-group">
		            <div class="controls">
		            	<button id="submit_btn" type="button" onClick="saveLogLanguage()" class="btn btn-primary"><spring:message code="common.save"/></button>
		            </div>
		        </div>
		        <input type="hidden" name="token" value="${cse:htmlEscape(token)}"/>
			</form>
		</div>
		
		<h5><spring:message code="syscofig.log.pigeonhole.parameter" /></h5>
		<div class="form-horizontal form-con clearfix">
			<form id="logAgentFSEndpointForm" class="form-horizontal" method="post">
				<div class="alert alert-error input-medium controls" id="errorTip" style="display:none">
					<button class="close" data-dismiss="alert">×</button><spring:message code="common.saveFail"/>
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
								code="clusterManage.accessKey" /> :</label>
						<div class="controls">
							<input type="text" id="ak" class="span4" name="ak" value="${cse:htmlEscape(ak)}" />
							<span class="validate-con bottom"><div></div></span>
							<span class="help-block"><spring:message code="cluster.user.dns.assa.uds.aksk" /></span>
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
		            	<button id="submit_btn" type="button" onClick="saveLogAgentSetting()" class="btn btn-primary"><spring:message code="common.save"/></button>
		            </div>
		        </div>
			</form>
		</div>
		
		<h5><spring:message code="sysconfig.syslog"/></h5>
		<div class="alert clearfix">
		<i class="icon-lightbulb icon-orange"></i>
		<spring:message code="sysconfig.syslog.cliew"/></div>
		<div class="form-horizontal form-con clearfix">
			<form id="syslogServerForm" class="form-horizontal" method="post">
				<input type="hidden" id="sendLocalTimestamp" name="sendLocalTimestamp" value="${cse:htmlEscape(sysLogServer.sendLocalTimestamp)}" />
		        <input type="hidden" id="sendLocalName" name="sendLocalName" value="${cse:htmlEscape(sysLogServer.sendLocalName)}" />
		   	    <div class="alert alert-error input-medium controls" id="errorTip" style="display:none">
					<button class="close" data-dismiss="alert">×</button><spring:message code="common.saveFail"/>
				</div>
		        <div class="control-group">
		            <label class="control-label" for="input"><spring:message code="sysconfig.server.addr"/></label>
		            <div class="controls">
		                <input class="span4" type="text" id="server" name="server" value="${cse:htmlEscape(server)}" />
		                <span class="validate-con"><div></div></span>
		            </div>
		        </div>
		        <div class="control-group">
		            <label class="control-label" for="input"><em>*</em><spring:message code="sysconfig.server.port"/></label>
		            <div class="controls">
		                <input class="span4" type="text" id="port" name="port" value="${cse:htmlEscape(sysLogServer.port)}" />
		                <span class="validate-con"><div></div></span>
		            </div>
		        </div>
		        <div class="control-group">
		            <label class="control-label" for="input"><em>*</em><spring:message code="sysconfig.protocol.type"/></label>
		            <div class="controls">
		                <select class="span4" id="protocolType" name="protocolType">
							<option value="0" <c:if test="${sysLogServer.protocolType == 0}">selected="selected"</c:if>>TCP</option>
							<option value="1" <c:if test="${sysLogServer.protocolType == 1}">selected="selected"</c:if>>UDP</option>
						</select>
		            </div>
		        </div>
		        <div class="control-group">
		            <label class="control-label" for="input"><em>*</em><spring:message code="sysconfig.char.code"/></label>
		            <div class="controls">
		                <select class="span4" id="charset" name="charset">
		                <c:forEach items="${charsets}" var="curChar">
		                	<option value="${cse:htmlEscape(curChar)}" <c:if test="${sysLogServer.charset == curChar}">selected="selected"</c:if>>${cse:htmlEscape(curChar)}</option>
		                </c:forEach>
						</select>
		            </div>
		        </div>
		        <div class="control-group">
		        	<label class="control-label" for="input"><spring:message code="sysconfig.server.logTime"/>:</label>
		        	<div class="controls">
		        		<input type="checkbox" id="chkEnableLocalTimestamp" title="<spring:message code='sysconfig.server.logTime'/>" name="chkEnableLocalTimestamp" <c:if test="${empty sysLogServer || sysLogServer.sendLocalTimestamp}">checked="checked"</c:if> />
		        	</div>
		        </div>
		        <div class="control-group">
		        	<label class="control-label" for="input"><spring:message code="sysconfig.server.logName"/>:</label>
		        	<div class="controls">
		        		<input type="checkbox" id="chkEnableLocalName" title="<spring:message code='sysconfig.server.logName'/>" name="chkEnableLocalName" <c:if test="${empty sysLogServer || sysLogServer.sendLocalName}">checked="checked"</c:if> />
		        	</div>
		        </div>
		        <div class="control-group">
		            <div class="controls">
		            	<button id="submit_btn" type="button" onClick="saveSysLogSetting()" class="btn btn-primary"><spring:message code="common.save"/></button>
		            	<button id="test_btn" type="button" class="btn" onclick="testConfig()"><spring:message code="common.test"/></button>
		            </div>
		        </div>
		        <input type="hidden" name="token" value="${cse:htmlEscape(token)}"/>
			</form>
		</div>
		
		<h5><spring:message code="sysconfig.message.config"/></h5>
		<div class="alert"><i class="icon-lightbulb icon-orange"></i><spring:message code="sysconfig.message.retention.days.tips"/></div>
		<div class="form-horizontal form-con clearfix">
			<form id="messageConfigForm" class="form-horizontal" method="post">
				<div class="alert alert-error input-medium controls" id="errorTip" style="display:none">
					<button class="close" data-dismiss="alert">×</button><spring:message code="common.saveFail"/>
				</div>
		        <div class="control-group">
		            <label class="control-label" for="input"><em>*</em><spring:message code="sysconfig.message.retention.days"/></label>
		            <div class="controls">
		                <input class="span2" type="text" id="msgRetentionDays" name=value value="${cse:htmlEscape(messageConfig.value)}" />
		                <span class="validate-con"><div></div></span>
		                <spring:message code="common.days"/>
		                <button id="submit_btn" type="button" onClick="setMsgRetentionDay()" class="btn btn-primary"><spring:message code="common.save"/></button>
		            </div>		            
		        </div>
		       
		        <input type="hidden" name="id" value="message.retention.days" />
		        
		        <input type="hidden" name="token" value="${cse:htmlEscape(token)}"/>
			</form>
		</div>
	</div>
	<script type="text/javascript">
		//平台配置JS
		$(document).ready(function() {
			if ("true" == "${secmatrix.path}") {
				$("#secmatrix").attr("checked", "true");
			}
			var pageH = $("body").outerHeight();
			top.iframeAdaptHeight(pageH);

			$("#directForm").validate({
				rules : {
					path : {
						required : true,
						rangelength : [ 1, 200 ],
					}
				}
			});

		});
		
		function directChainSetting() {
			if (!$("#directForm").valid()) {
				return false;
			}
			var value = $("#directPath").val();

			$.ajax({
				type : "POST",
				url : "${ctx}/sysconfig/direct/save",
				data : {
					path : value,
					token:'${cse:htmlEscape(token)}'
				},
				error : function(request) {
					if (request.responseText == "InParamterException") {
						top.handlePrompt("error",
								'<spring:message code="directForm.drect.null"/>');
					} else {
						top.handlePrompt("error",
								'<spring:message code="common.saveFail"/>');
					}
				},
				success : function() {
					top.handlePrompt("success",
							'<spring:message code="common.saveSuccess"/>');
				}
			});
		}		
		
		//安全矩阵
		$(document).ready(function() {
			if ("true" == "${secmatrix.path}") {
				$("#secmatrix").attr("checked", "true");
			}			
		});		
		function secmatrixSetting() {
			var secmatrix = document.getElementById("secmatrix").checked ? "true"
					: "false";

			$.ajax({
				type : "POST",
				url : "${ctx}/sysconfig/direct/save",
				data : {
					secmatrix : secmatrix,
					token:'${cse:htmlEscape(token)}'
				},
				error : function(request) {
					top.handlePrompt("error",
							'<spring:message code="common.saveFail"/>');
				},
				success : function() {
					top.handlePrompt("success",
							'<spring:message code="common.saveSuccess"/>');
				}
			});
		}
		
		//日志基础配置JS
		$(document).ready(function() {
			$("#logLanguageForm").validate({ 
				rules: { 
					   protocolType:{
					       notNull:true
					   }
				}
		 	}); 
			var pageH = $("body").outerHeight();
			top.iframeAdaptHeight(pageH);
		});
		function saveLogLanguage(){
			if(!$("#logLanguageForm").valid()) {
		        return false;
		    }  
			$.ajax({
				type: "POST",
				url:"${ctx}/sysconfig/syslog/savelogLanguage",
				data:$('#logLanguageForm').serialize(),
					error: function(request) {
						top.handlePrompt("error",'<spring:message code="common.saveFail"/>');
					},
					success: function() {
						top.handlePrompt("success",'<spring:message code="common.saveSuccess"/>');
					}
			});
		}
		//日志归档参数配置JS
		$(document).ready(function() {
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
					       max:65535,
					       maxlength:[10]
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
			var pageH = $("body").outerHeight();
			top.iframeAdaptHeight(pageH);
			
			var storageType = "${cse:htmlEscape(fsType)}";
			if(storageType == "nas"){
				$("#nasConfig").click();
				setStorageType("nas");
			}else{
				setStorageType("uds");
			}
		});

		function saveLogAgentSetting(){
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
					     token:'${cse:htmlEscape(token)}'
			};
			
			$.ajax({
				type: "POST",
				url:"${ctx}/sysconfig/logagentconfig/-1/save",
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

		function setStorageType(val){
			if(val == "uds"){
				$("#udsConfig").show();
				$("#nasConfig").hide();
			}else{
				$("#udsConfig").hide();
				$("#nasConfig").show();
			}
		}
		//日志审计平台配置JS
		$(document).ready(function() {
			$("#syslogServerForm").validate({ 
				rules: { 
					   server:{
					       maxlength:[255]
					   },
					   port: { 
						   required:true, 
					       digits:true,
					       min:1,
					       max:65535,
					       maxlength:[10]
					   }
				}
		 	}); 
		 	
		 	var proType=  $("#protocolType").val();
		 	if(proType == "1")
		 	{
		 		$('#test_btn').hide();
		 	}
		 	else
		 	{
		 		$('#test_btn').show();
		 	}
			var pageH = $("body").outerHeight();
			top.iframeAdaptHeight(pageH);
		});
		function saveSysLogSetting(){
			if(!$("#syslogServerForm").valid()) {
		        return false;
		    }  
			$.ajax({
				type: "POST",
				url:"${ctx}/sysconfig/syslog/save",
				data:$('#syslogServerForm').serialize(),
					error: function(request) {
						top.handlePrompt("error",'<spring:message code="common.saveFail"/>');
					},
					success: function() {
						top.handlePrompt("success",'<spring:message code="common.saveSuccess"/>');
					}
			});
		}
		$("#protocolType").change(function () {
			var protocol = $(this).val();
			if(protocol == "0"){
				$('#test_btn').show();
			}else{
				$('#test_btn').hide();
			}
		});
		$("#chkEnableLocalTimestamp").click(function(){ 
			if(this.checked){
				$("#sendLocalTimestamp").val("true");
			}else{ 
				$("#sendLocalTimestamp").val("false");
			}
		});
		$("#chkEnableLocalName").click(function(){ 
			if(this.checked){
				$("#sendLocalName").val("true");
			}else{ 
				$("#sendLocalName").val("false");
			}
		});
		function testConfig(){
			if(!$("#syslogServerForm").valid()) {
		        return false;
		    }  
			$.ajax({
				type: "POST",
				url:"${ctx}/sysconfig/syslog/test",
				data:$('#syslogServerForm').serialize(),
					error: function(request) {
						top.handlePrompt("error",'<spring:message code="common.testConnectionFail"/>');
					},
					success: function() {
						top.handlePrompt("success",'<spring:message code="common.testConnectionSuccess"/>');
					}
			});
		}
		//消息配置JS
		$.validator.addMethod(
				   "int", 
				   function(value, element) {   
					  var validName = /^[0-9]{1}[0-9]*$/;   
		           return validName.test(value);
			       }, 
			       $.validator.format('<spring:message code="common.validate.int"/>')
		); 
		$(document).ready(function() {
			$("#messageConfigForm").validate({ 
				rules: { 
					   value: { 
						   required:true,
						   int:true, 
					       min:1,
					       max:30
					   }
				}
		 	}); 
		});
		
		function setMsgRetentionDay(){
			
			if(!$("#messageConfigForm").valid()) {
		        return false;
		    } 
			$.ajax({
				type: "POST",
				url:"${ctx}/sysconfig/set",
				data:$("#messageConfigForm").serialize(),
					error: function(request) {
						if (request.responseText == "InParamterException") {
							top.handlePrompt("error",
									'<spring:message code="message.common.validate.int"/>');
						}else{
							top.handlePrompt("error",'<spring:message code="common.saveFail"/>');
						}
					},
					success: function() {
						top.handlePrompt("success",'<spring:message code="common.saveSuccess"/>');
					}
			});
		}
	</script>
</body>