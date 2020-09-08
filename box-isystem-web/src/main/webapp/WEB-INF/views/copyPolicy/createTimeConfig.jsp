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
</head>
<body>
	<div class="pop-content pop-content-en">
		<div class="form-con">
			<form class="form-horizontal" id="CreateTimeConfig" name="CreateTimeConfig">	
				<div class="control-group">	
				<label class="control-label" for=""><spring:message code="timeconfig.startAt" /> :</label>	
					<div class="controls">
						    <span class="input-group-addon">
							<select class="span1"  id="startHH"></select><spring:message code="Hour" /><select id="startMM" class="span1"></select><spring:message code="Minute" />
							</span>
					</div>	
							<br>
				<label class="control-label" for=""><spring:message code="timeconfig.endAt" /> :</label>
					<div class="controls">	
							<span class="input-group-addon">
							<select id="endHH" class="span1"></select><spring:message code="Hour" /><select id="endMM"  class="span1"></select><spring:message code="Minute" />
							</span>								
					</div>
				<br><br><br><br><br>
				<div class="control-group">
					<div class="controls" style="text-align:center;margin:0px">
					<button  type="button" class="btn btn-primary" id="submitCreateTimeConfigbtn" onclick="submitCreateTimeConfig()"><spring:message code="common.save" /></button>

					<button  type="button" class="btn" onclick="cancleWin()"><spring:message	code="button.cancel" /></button>

					</div>
				</div>
				</div>

		</form>
		
	
	</div>

	</div>
	<script type="text/javascript">
	function initSelect()
	{
		 var startH=document.getElementById("startHH");
		 var startM=document.getElementById("startMM");
		 var endH=document.getElementById("endHH");
		 var endM=document.getElementById("endMM");
		 for(var i=0;i<24;i++)
		 {
			 var optionS=document.createElement('option');
			 var optionE=document.createElement('option');
			 var value;
			 if(i<10)
			{
				 value="0"+i
			}else{
				value=""+i
			}
			 optionS.text=value;
			 optionE.text=value;
			 startH.options.add(optionS);
			 endH.options.add(optionE);
		 }
		 for(var i=0;i<60;i++)
		 {
			 var optionS=document.createElement('option');
			 var optionE=document.createElement('option');
			 var value;
			 if(i<10)
			{
				 value="0"+i
			}else{
				value=""+i
			}
			 optionS.text=value;
			 optionE.text=value;
			 startM.options.add(optionS);
			 endM.options.add(optionE);
		 }

	}
	
	$(document).ready(function(){        
	           initSelect();
	})   
	     
function submitCreateTimeConfig() {
			    var exeStartAt=$("#startHH").find("option:selected").val()+":"+$("#startMM").find("option:selected").val();
			    var exeEndAt=$("#endHH").find("option:selected").val()+":"+$("#endMM").find("option:selected").val();
                if(exeStartAt==exeEndAt)
                {
                handlePrompt("error",
								'<spring:message code="timeConfig.timeconfigexception"/>');
								return false;
                }
			
			
			    var timeConfig="{\"exeStartAt\":\""+exeStartAt
					+"\",\"exeEndAt\":\""+exeEndAt
					+"\"}";
			    $.ajax({   
					    type : "POST",
						url : "${ctx}/mirror/timeConfig/createTimeConfig",
						data:{timeConfig:timeConfig,token:'${cse:htmlEscape(token)}'},
						error : function(request) {
							if(request.responseText=="BadTimeConfigInfo")
							{
								handlePrompt("error",
								'<spring:message code="timeConfig.handle.info.error"/>');
								
							}
							else if(request.responseText=="TimeConfigConflict")
							{
							    handlePrompt("error",
								'<spring:message code="timeConfig.Conlict"/>');
								
							}
							else if(request.responseText=="Overlimit")
							{
							    handlePrompt("error",
								'<spring:message code="timeConfig.overlimit"/>');
								
							}
						    else{
								handlePrompt("error",
									'<spring:message code="common.createFail"/>');
									
							}
						},
						success : function() {
							top.ymPrompt.close();
							top.handlePrompt("success",
											'<spring:message code="common.createSuccess"/>');
							top.window.frames[0].location = "${ctx}/mirror/copyPolicy/list";

							
						}
					});
		}

function cancleWin()
{
    top.ymPrompt.close();
}
	</script>
</body>
</html>
