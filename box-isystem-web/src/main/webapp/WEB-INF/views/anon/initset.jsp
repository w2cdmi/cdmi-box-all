<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Cache-Control" content="no-cache" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title></title>
<%@ include file="../common/common.jsp"%>
</head>
<body>
<div class="header">
    <div class="header-con">
        <div class="logo pull-left"><a href="#"  id="logoBackgroudId"><spring:message code="main.title" /></a></div>
           <div class="header-R pull-right clearfix">
        	<ul class="clearfix pull-right">
            	<li class="pull-left dropdown">
                </li>
            </ul>
        </div>
    </div>
</div>
<div class="body">
<div class="sys-content body-con">
	<div class="form-horizontal form-con  clearfix">
		<form class="form-horizontal" id="modifyPwdForm">
        <div class="control-group">
            <div class="controls">
            <label><spring:message code="anon.setPwdInfo"/></label>
            </div>
        </div>
        <input type="hidden" id="loginName" name="loginName" value="${cse:htmlEscape(loginName)}" />
   	    <input type="hidden" id="validateKey" name="validateKey" value="${cse:htmlEscape(validateKey)}" />
        <div class="alert alert-error input-medium controls" id="errorTip" style="display:none">
			<button class="close" data-dismiss="alert">×</button><spring:message code="anon.setPwdError"/>
		</div>
        <div class="control-group">
            <label class="control-label" for="input"><em>*</em><spring:message code="login.username"/></label>
            <div class="controls">
                <input class="span4" type="text" id="name" name="name" value="" />
                <span class="validate-con"><div></div></span>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="input"><em>*</em><spring:message code="anon.pwd"/></label>
            <div class="controls">
                <input class="span4" type="password" id="password" name="password" value="" autocomplete="off"/>
                <span class="validate-con"><div></div></span>
            </div>
        </div>
		<div class="control-group">
            <label class="control-label" for="input"><em>*</em><spring:message code="anon.confirmPwd"/></label>
            <div class="controls">
                <input class="span4" type="password" id="confirmPassword" name="confirmPassword" value="" autocomplete="off"/>
                <span class="validate-con"><div></div></span>
            </div>
        </div>
        <div class="control-group">
            <div class="controls">
            	<button id="chgPassword_btn" type="button" class="btn btn-primary" onclick="submitInitSet()"><spring:message code="common.OK"/></button>
            	<button id="chgPassword_btn" type="button" class="btn" onclick="backLogin()"><spring:message code="common.back"/></button>
            </div>
        </div>
        <input type="hidden" id="token" name="token" value="<c:out value='${token}'/>"/>
	</form>
	</div>
</div>
</div>
<div class="footer">
	<div class="footer-con">
    	<p><span class="logo-small" id="copyRightId"><spring:message code="corpright" /></span></p>
    </div>
</div>
</body>
<script type="text/javascript">
$(document).ready(function() {
	loadSysSetting("${ctx}");
	$("#modifyPwdForm").validate({ 
		rules: { 
			   name:{
				   required:true
			   },
			   password: { 
				   required:true,
				   isValidPwd:true
			   },
			   confirmPassword: { 
				   required:true,
				   equalTo: "#password"
			   }
		}
   }); 
});
function submitInitSet() {
	if(!$("#modifyPwdForm").valid()) {
        return false;
    }
	$.ajax({
        type: "POST",
        url:"${ctx}/syscommon/doinitset",
        data:$('#modifyPwdForm').serialize(),
        error: function(request) {
        	top.handlePrompt("error",'<spring:message code="anon.setPwdError" />');
        },
        success: function() {
        	top.ymPrompt.alert({title:'<spring:message code="common.title.info"/>',message:'<spring:message code="anon.setPwdSuccess"/>',handler:backLogin});
        	setTimeout(function(){ymPrompt.doHandler('ok')},3000);
        }
    });
}

function backLogin()
{
    window.location = "${ctx}/login";
}

</script>
</html>
