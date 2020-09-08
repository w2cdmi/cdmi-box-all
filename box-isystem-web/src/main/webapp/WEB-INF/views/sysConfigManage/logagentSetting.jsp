<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:set var="ctx" value="${pageContext.request.contextPath}"/>
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
	<div class="alert"><i class="icon-lightbulb icon-orange"></i><spring:message code="sysconfig.UASconfig.logAndstorage"/></div>
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
</div>
<script type="text/javascript">
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
</script>
</body>
</html>
