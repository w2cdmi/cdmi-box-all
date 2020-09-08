<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cse" uri="http://cse.huawei.com/custom-function-taglib"%>  
<%@ page import="pw.cdmi.box.uam.util.CSRFTokenManager"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%
request.setAttribute("token", CSRFTokenManager.getTokenForSession(session));
%>
<!DOCTYPE html>
<html>
<head>
<%@ include file="../../common/common.jsp"%>
</head>
<body>
<div class="sys-content sys-content-en">
	<div class="alert"><i class="icon-lightbulb"></i><spring:message code="accessConfig.lightbulb"/></div>
	<div class="form-horizontal form-con clearfix">
   	<form id="accessAddrConfigForm" class="form-horizontal" method="post">
        <div class="control-group">
                <label class="control-label" for="input"><em>*</em><spring:message code="accessConfig.uamAddress"/></label>
	            <div class="controls">
	                <input class="span4" type="text" id="uamOuterAddress" name="uamOuterAddress" value="${cse:htmlEscape(accessConfig.uamOuterAddress)}" placeholder='<spring:message code="accessConfig.uamAddress.clview"/>' />
	                <span class="validate-con"><div></div></span> 
	                <div class="help-inline"><a href="javascript:void(0)" onclick="showMoive()" ><i class="icon-question-sign icon-green"></i></a></div> 
	                <span class="help-block"> <spring:message code="accessConfig.helpAccess"/></span> 
	            </div>  
	                                
        </div>
        <div class="control-group">
                <label class="control-label" for="input"><em>*</em><spring:message code="accessConfig.uamInnerAddress"/></label>
	            <div class="controls">
	                <input class="span4" type="text" id="uamInnerAddress" name="uamInnerAddress" value="${cse:htmlEscape(accessConfig.uamInnerAddress)}" placeholder='<spring:message code="accessConfig.uamAddress.clview"/>' />
	                <span class="validate-con"><div></div></span> 
	                <div class="help-inline"><a href="javascript:void(0)" onclick="showMoive()" ><i class="icon-question-sign icon-green"></i></a></div> 
	                <span class="help-block"> <spring:message code="accessConfig.uamInner.helpAccess"/></span> 
	            </div>  
	                                
        </div>
        <div class="control-group">
                <label class="control-label" for="input"><em>*</em><spring:message code="accessConfig.ufmOuterAddress"/></label>
	            <div class="controls">
	                <input class="span4" type="text" id="ufmOuterAddress" name="ufmOuterAddress" value="${cse:htmlEscape(accessConfig.ufmOuterAddress)}" placeholder='<spring:message code="accessConfig.ufmAddress.clview"/>' />
	                <span class="validate-con"><div></div></span>
	                <span class="help-block"> <spring:message code="accessConfig.helpFile"/></span>
	            </div>          
        </div>  
        <div class="control-group">
                <label class="control-label" for="input"><em>*</em><spring:message code="accessConfig.ufmInnerAddress"/></label>
	            <div class="controls">
	                <input class="span4" type="text" id="ufmInnerAddress" name="ufmInnerAddress" value="${cse:htmlEscape(accessConfig.ufmInnerAddress)}" placeholder='<spring:message code="accessConfig.ufmAddress.clview"/>' />
	                <span class="validate-con"><div></div></span>
	                <span class="help-block"><spring:message code="accessConfig.helpSystem"/></span>
	            </div>          
        </div>  
        <div class="control-group">
            <div class="controls">
            	<button id="submit_btn" type="button" onClick="saveAccessAddrConfig()" class="btn btn-primary"><spring:message code="common.save"/></button>
            </div>
        </div>
        <input type="hidden" id="token" name="token" value="<c:out value='${token}'/>"/>
	</form>
</div>
</div>
<script type="text/javascript">
$(document).ready(function() {
	$("#accessAddrConfigForm").validate({ 
		rules: { 
			   uamOuterAddress:{
				   required:true, 
				   maxlength:[255]
			   },
			   uamInnerAddress:{
				   required:true, 
				   maxlength:[255]
			   },
			   ufmOuterAddress: { 
				   required:true, 
				   maxlength:[255]
			   },
			   ufmInnerAddress: { 
				   required:true, 
				   maxlength:[255]
			   }
		}
    }); 
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
	if (!placeholderSupport()) {
		placeholderCompatible();
	};
});
function saveAccessAddrConfig(){
	if(!$("#accessAddrConfigForm").valid()) {
        return false;
    }  
	$.ajax({
		type: "POST",
		url:"${ctx}/sys/sysconfig/access/save",
		data:$('#accessAddrConfigForm').serialize(),
		error: function(request) {
			top.handlePrompt("error",'<spring:message code="common.saveFail"/>');
		},
		success: function() {
			top.handlePrompt("success",'<spring:message code="common.saveSuccess"/>');
			window.location.reload();
		}
	});
	
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
}
function showMoive(){
	var url = '${ctx}/static/help/accessConfig.html';
	if('<spring:message code="main.language"/>' == 'en'){
		url = '${ctx}/static/help/en/accessConfig.html';
	}
	top.ymPrompt.win({
		message:url,
		width:430,height:435,
		title:'<spring:message code="common.guide"/>', iframe:true,
		btn: [['<spring:message code="button.replay"/>', "replay", false, "focusBtn"],['<spring:message code="button.summary"/>', "summary", false]],
		handler : function(tp) {
			if(tp == "replay"){
				top.ymPrompt.getPage().contentWindow.replay();
			}else if(tp == "summary"){
				top.ymPrompt.getPage().contentWindow.summary();
			}
		}
	});
	top.ymPrompt_addModalFocus("#focusBtn");
	}
</script>
</body>
</html>
