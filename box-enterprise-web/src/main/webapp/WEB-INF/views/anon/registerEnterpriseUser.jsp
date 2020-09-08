<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="../common/common.jsp"%>
    <link href="${ctx}/static/jqueryUI-1.9.2/jquery-ui.min.css" rel="stylesheet" type="text/css" />
    <script src="${ctx}/static/jqueryUI-1.9.2/jquery-ui.min.js" type="text/javascript"></script>
    <%@ include file="../common/messages.jsp"%>
    <style>
        input[type=text],input[type=submit]{
            width: 100%;
            height: 40px;
            font-size: medium;
            padding-left: 10px;
            margin-top: 20px;
            border-radius: 10px;
            border:1px solid #dfe0e1;
        }
        .inputNow{
            border: 1px solid #0e90d2;
        }
        button{
            width: 100%;
            height: 40px;
            font-size: medium;
            padding-left: 10px;
            margin-top: 20px;
            border:1px solid #dfe0e1;
        }
        .ui-state-error { padding: .3em; }
        .validateTips { border: 1px solid transparent; padding: 0.3em; }
    </style>
</head>
<body style="background-color: #edfbfb">
<div class="header" style="position: relative">
    <div class="header-con">
        <div class="logo" id="logoBlock"><img src="${ctx}/static/skins/default/img/logo.png"/></div>
    </div>
</div>
<div style="width: 30%;margin:0 auto;margin-top: 20px; " title="register Enterprise">
    <p class="validateTips"></p>

    <strong>${inviter}</strong> <spring:message code="button.invite"/> 您 加入 <strong>${enterpriseName}</strong> 企业云盘

    <br/>
    <br/>
    <br/>
    <br/>
    <br/>
    <form>
        <fieldset>
            <input name="domainName" value="${domain}" type="hidden">
            <label for="accountId"><spring:message code="register_user_account"/> </label>
            <input type="text" name="accountId" id="accountId" placeholder="<spring:message code="register_user_account_place"/>" class="text ui-widget-content ui-corner-all">
            <label for="contactPerson"><spring:message code="common.field.username"/> </label>
            <input type="text" name="contactPerson" id="contactPerson" placeholder="<spring:message code="register_user_name_place"/>" class="text ui-widget-content ui-corner-all">
            <label for="contactPhone"><spring:message code="common.field.mobile"/> </label>
            <input type="text" name="contactPhone" id="contactPhone" placeholder="<spring:message code="register_mobile_place"/>" class="text ui-widget-content ui-corner-all">
            <label for="contactEmail"><spring:message code="common.field.email"/></label>
            <input type="text" name="contactEmail" id="contactEmail" placeholder='<spring:message code="link.label.input.email"/> ' class="text ui-widget-content ui-corner-all">
            <input type="submit" value='<spring:message code="register"/> ' >
        </fieldset>
    </form>
</div>
</body>
<script type="text/javascript">
    var dialog;
    $( function() {
        var emailRegex = /^[a-zA-Z0-9.!#$%&'*+\/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$/,
            allFields  = $("form > input"),
            tipsText = '<spring:message code="input.length.betweenZeroAndOne"/>',
            tips = $( ".validateTips" );

        function updateTips( t ) {
            tips.text( t )
                .addClass( "ui-state-highlight" );
            setTimeout(function() {
                tips.removeClass( "ui-state-highlight", 1500 );
            }, 500 );
        }

        function checkLength( o, n, min, max ) {
            if ( o.val().length > max || o.val().length < min ) {
                o.addClass( "ui-state-error" );
                var argment = tipsText.replace("{0}",min);
                var argment = tipsText.replace("{1}",max);
                updateTips( argment );
                return false;
            } else {
                return true;
            }
        }

        function checkRegexp( o, regexp, n ) {
            if ( !( regexp.test( o.val() ) ) ) {
                o.addClass( "ui-state-error" );
                updateTips( n );
                return false;
            } else {
                return true;
            }
        }

        function addEnterprise() {
            tips.text();
            var valid = true;
            allFields.removeClass( "ui-state-error" );

            valid = valid && checkLength( $("#accountId"), "<spring:message code="register_user_account"/>", 4, 16 );
            valid = valid && checkLength( $("#contactPerson"), "<spring:message code="common.field.username"/>", 2, 16 );
            valid = valid && checkLength( $("#contactPhone"), "<spring:message code="common.field.mobile"/>", 11, 11 );
            valid = valid && checkLength( $("#contactEmail"), "<spring:message code="common.field.email"/>", 5, 80 );

            valid = valid && checkRegexp( $("#accountId"), /^[0-9a-z\s]+$/i, '<spring:message code="register_account_invalid"/>' );
            valid = valid && checkRegexp( $("#contactPerson"), /^[\u4E00-\u9FA5\uF900-\uFA2Da-z\s]+$/i, '<spring:message code="register_contact_user_invalid"/>' );
            valid = valid && checkRegexp( $("#contactPhone"), /^1[34578]\d{9}$/i, '<spring:message code="common.validate.mobile"/>' );
            valid = valid && checkRegexp( $("#contactEmail"), emailRegex, '<spring:message code="common.validate.email"/>' );
            if(valid) {
                $.ajax({
                    type: "POST",
                    url:"${ctx}/syscommon/invite/registerEnterpriseUser",
                    data:$("form").serialize(),
                    success: function(data) {
                        console.log("hello world");
                        console.log(data);
                        if(!data){
                            top.handlePrompt("error",'<spring:message code="register_failure" />');
                            return;
                        }else if(data=="success"){
                            top.ymPrompt.alert({title:'<spring:message code="common.title.info"/>',message:'<spring:message code="register_success"/>',handler:backLogin});
                            setTimeout(function(){ymPrompt.doHandler('ok')},10000);
                        }else if(data=="email_conflict"){
                            top.handlePrompt("error",'<spring:message code="common.account.email.conflict" />');
                            $("#contactEmail").addClass( "ui-state-error" );
                            return;
                        }else if(data=="account_conflict"){
                            top.handlePrompt("error",'<spring:message code="group.error.ExistMemberConflict"/>');
                            $("#accountId").addClass( "ui-state-error" );
                            return;
                        }else if(data=="phone_conflict"){
                            top.handlePrompt("error",'<spring:message code="mobile.change.conflict"/>');
                            $("#contactPhone").addClass( "ui-state-error" );
                            return;
                        }
                        top.handlePrompt("error",'<spring:message code="register_failure" />');
                        return;
                    }
                });
            }

        }

        $( "form" ).on( "submit", function( event ) {
            event.preventDefault();
            addEnterprise();
        });
    });
    function backLogin(){
        window.location = "${ctx}/login";
    }
</script>
</html>
