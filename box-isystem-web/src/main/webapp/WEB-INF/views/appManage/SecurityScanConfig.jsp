<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page import="com.huawei.sharedrive.isystem.util.CSRFTokenManager"%>
<%@ taglib prefix="cse" uri="http://cse.huawei.com/custom-function-taglib"%>  
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<%
    request.setAttribute("token",
					CSRFTokenManager.getTokenForSession(session));
%>
<!DOCTYPE html>
<html>
<head>
<link href="${ctx}/static/zTree/zTreeStyle.css" rel="stylesheet" type="text/css">
<%@ include file="../common/common.jsp"%>
<script src="${ctx}/static/zTree/jquery.ztree.all-3.5.min.js"	type="text/javascript"></script>
<style type="text/css">
.form-horizontal .control-label,
.pop-content.pop-content-en .form-horizontal .control-label{ width:110px; text-align:left;}
.form-horizontal .controls,
.pop-content.pop-content-en .form-horizontal .controls{ margin-left:130px;}
.form-horizontal .control-group{ margin-bottom:10px;}
</style>
</head>
<body>
	<div class="pop-content pop-content-en">
		<div class="form-con">
			<form id="KiAconfigForm" name="KiAconfigForm">
				<div class="control-group">
					<label class="control-label" for=""> <strong><spring:message code="plugin.KIA.eable.label" /></strong> </label>
				</div>
				<div class="control-group">
					<div class="controls">
						<label class="checkbox inline" for=""><input  id="enbaleKey" type="checkbox" onclick="checkChange();" /> <spring:message
							code="plugin.KIA.enable" /></label>
					</div>
				</div>
				<br />
				<div class="control-group">
					<label class="control-label"><strong><spring:message code="plugin.scanning" /></strong></label>
				</div>
				<div  class="form-horizontal" >
				<div class="control-group">
					<label class="control-label"><em>*</em><spring:message
							code="scanMode" /> :</label>
					<div class="controls">
 						<label class="radio inline" for=""><input type="radio" name="exeType"  value="0" onclick="javascript:hiddenTime()" checked="checked"/><spring:message code="systemScan.mode.realTime" /></label>
       					 <label class="radio inline" for=""><input type="radio" name="exeType"  value="1" onclick="javascript:showTime()"/><spring:message code="systemScan.mode.periodic" /></label>
					</div>
				</div>
				
				<div class="control-group" id="timeH" hidden="hidden">
					<label class="control-label" for=""><em>*</em><spring:message
							code="systemScan.period" /> :</label>
					<div class="controls">
							<span class="input-group-addon">
							<select class="span2"  id="startHH" width="50"></select> 
							</span>
							<spring:message code="log.till"/>
							<span class="input-group-addon">
								<select id="endHH" class="span2"></select> 
								<span class="validate-con bottom"><div></div></span>
							</span>
						
					</div>
				</div>				
				</div>
				<div class="control-group">
					<div class="controls">
						<div class="scan-info" id="scanInit" style="display:none;">
							<p id="statusBar"></p>
							<div class="progress progress-info"><div id="processing" class="bar"></div></div>
						</div>
						<button id="scanBtn" class="btn btn-primary" type="button" onClick="scanning()"/></button>
					</div>
				</div>
				<br />
				<div class="control-group">
					<label class="control-label" for=""><strong><spring:message
							code="plugin.version" /> </strong></label>
					
				</div>
				<div class="input-prepend input-append">
						<span class="add-on"><spring:message code="plugin.version.current" /></span>
						<input type="text" id="version" readonly="readonly" class="span2"/>
						<button id="upVersonBtn" class="btn btn-primary" type="button" onClick="updateVersion()"/><spring:message code="plugin.update"/></button>
				</div>
				
			 <input type="hidden" name="token" value="${cse:htmlEscape(token)}" />
		</form>
	</div>

	</div>
	<script type="text/javascript">
	
	var inProgress = false;
	var intervalTask;
	var startTime;
	var endTime;
	$(document).ready(function(){
		init();
	});
	function checkChange()
	{
		var val = document.getElementById("enbaleKey").checked?true:false;
		document.getElementById("enbaleKey").disabled=true;
		updateSysconfig("security.scan.enable",val,false)
		if(val){
			$("#scanBtn").removeAttr("disabled");
		}else{
			$("#scanBtn").attr("disabled",  "disabled");
		}
		
	}
	function updateVersion()
	{
		document.getElementById("upVersonBtn").disabled=true;
		updateSysconfig("security.scan.engine.version",document.getElementById("version").value,true);
	}
	function updateSysconfig(id,value,isVsersion)
	{
		var sysconfig = {id:id,value:value,"token" : "${cse:htmlEscape(token)}"}
		$.ajax({
	        type: "POST",
	        url:"${ctx}/pluginServer/KIAconfig/setSysconfig",
	        data:sysconfig,
	        error: function(request) {
	        	if(isVsersion){
	        		handlePrompt("error",'<spring:message code="plugin.version.update.fail"/>');
	        	}else{
	        		handlePrompt("error",'<spring:message code="common.modifyFail"/>');	
	        	}
	        },
	        success: function(request) {
	        	if(isVsersion){
	        		handlePrompt("success",'<spring:message code="plugin.version.update.success"/>');
	        		document.getElementById("version").value=request;
	        	}else{
	        		handlePrompt("success",'<spring:message code="common.modifySuccess"/>')
	        	}
	        }
	    });
		document.getElementById("upVersonBtn").disabled=false;
		document.getElementById("enbaleKey").disabled=false;
	}
	function scanning(){
		var title = inProgress ? "<spring:message code='plugin.stop.scanning'/>" : "<spring:message code='plugin.start.scanning'/>";
		var tips = inProgress ? "<spring:message code='security.scan.sure.to.stop'/>" : "<spring:message code='security.scan.sure.to.start'/>";
	
		ymPrompt.confirmInfo({title:title,message:tips,maskAlphaColor: 'gray',handler:function(tp) {
			if (tp=='ok') {
				if(!inProgress && $('input:radio[name="exeType"]:checked').val()==1){
					startTime=$("#startHH").val();
					endTime=$("#endHH").val();
					if(endTime != "" && startTime >= endTime){
						handlePrompt("error",'<spring:message code="copyPolicy.handle.time.error"/>',null,60);
						return false;
					} 
				}
				$.ajax({
			        type: "POST",
			        url:"${ctx}/pluginServer/KIAconfig/setScanning",
			        data:{isScan:!inProgress,"token" : "${cse:htmlEscape(token)}","startTime":startTime,"endTime":endTime},
			        error: function(request) {
			        	handlePrompt("error","<spring:message code='operation.failed'/>");
			        },
			        success: function(request) {
			        	handlePrompt("success","<spring:message code='operation.success'/>");
			        	inProgress = !inProgress;
			        	var btnName = inProgress ? '<spring:message code="plugin.stop.scanning"/>' : '<spring:message code="plugin.start.scanning"/>';
						$("#scanBtn").html(btnName);
						// 启动扫描
						if(inProgress){
							showInitProgress();
						}else{  // 停止扫描
							window.clearInterval(intervalTask);
							$("#scanInit").hide();
						}
			        }
			    });
			}
		}});
	}

	
		function init() {
			hiddenTime();
			initSelect();
			if ("${enbaleKey}" != "") {
				if ("${enbaleKey.value}" != ""
						&& "${enbaleKey.value}" == "true") {
					document.getElementById("enbaleKey").checked = true;
				} else {
					document.getElementById("enbaleKey").checked = false;

				}
			}
			if ("${verionKey}" != "") {
				document.getElementById("version").value = "${cse:htmlEscape(verionKey.value)}";
			}
			if ("${scanModel[0].value}" == "true") {
				document.getElementsByName("exeType")[0].checked = true;
			} else {

				document.getElementsByName("exeType")[1].checked = true; 
				showTime();
				var startTime = "${cse:htmlEscape(scanModel[1].value)}";
				var endTime = "${cse:htmlEscape(scanModel[2].value)}"; 
				if(startTime!=null && endTime!=null && startTime != "" && endTime != "")
				{
					document.getElementById("startHH").options[startTime].selected = "selected" ;
					document.getElementById("endHH").options[endTime].selected = "selected" ;
				}				
			}

			if (!document.getElementById("enbaleKey").checked) {
				$("#scanBtn").attr("disabled", "disabled");
			}

			var unexeTaskNum = getUnexeTaskNum();
			inProgress = unexeTaskNum > 0;
			var btnName = inProgress ? '<spring:message code="plugin.stop.scanning"/>'
					: '<spring:message code="plugin.start.scanning"/>'; 
			$("#scanBtn").html(btnName);

			// 扫描已完成
			if (!inProgress) {
				$("#scanInit").hide();
				return;
			}
			// 显示表扫描进度
			if (unexeTaskNum > 0) {
				showInitProgress();
			}
		}

		function showProgress() {

			$
					.ajax({
						type : "GET",
						url : "${ctx}/pluginServer/KIAconfig/progress?type=tableScan&"
								+ Math.random(),
						error : function(request) {
						},
						success : function(data) {
							var percent = data.completed / data.total * 100;
							$("#processing").attr("style",
									"width:" + percent + "%");

							// 表扫描任务完成
							if (data.waiting == 0) {
								$("#processing").attr("style", "width:100%");
								window.clearInterval(intervalTask);

								setTimeout(
										function() {
											$("#statusBar")
													.text(
															"<spring:message code='plugin.scan.task.send.completed'/>");
											$("#scanBtn").hide();
											$("#processing").parent().remove();
										}, 3000);
							}

						}
					});
		}

		function showInitProgress() {
			$("#statusBar").text(
					"<spring:message code='plugin.scan.task.sending'/>");
			$("#scanInit").show();
			intervalTask = setInterval('showProgress()', 1000);
		}

		function getUnexeTaskNum() {
			var number;
			$.ajax({
				type : "GET",
				async : false,
				url : "${ctx}/pluginServer/KIAconfig/waitting",
				error : function(request) {
				},
				success : function(data) {
					number = data;
				}
			});
			return number;
		}
		function showTime() {
			document.getElementById("timeH").style.display = "block";
		}
		function hiddenTime() {
			document.getElementById("timeH").style.display = "none";
		}

		function initSelect() {
			var startH = document.getElementById("startHH");
			var endH = document.getElementById("endHH");
			for ( var i = 0; i < 24; i++) {
				var optionS = document.createElement('option');
				var optionE = document.createElement('option');
				var value;
				if (i < 10) {
					value = "0" + i
				} else {
					value = "" + i
				}
				optionS.text = value + ":00";
				optionE.text = value + ":00";
				startH.options.add(optionS);
				endH.options.add(optionE);
			}

		}
	</script>
</body>
</html>