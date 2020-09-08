<%@ page contentType="text/html;charset=UTF-8" %>
<HEAD>
	<META HTTP-EQUIV="Expires" CONTENT="0">
	<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
	<META HTTP-EQUIV="Cache-control"
		CONTENT="no-cache, no-store, must-revalidate">
	<META HTTP-EQUIV="Cache" CONTENT="no-cache">
</HEAD>
<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);
%>
<script type="text/javascript">
jQuery.extend(jQuery.validator.messages, {
        required: '<spring:message code="message.notNull"/>',
		remote: '<spring:message code="message.field.modiffy"/>',
		email: '<spring:message code="message.email.format.right"/>',
		url: '<spring:message code="message.url.legality"/>',
		date: '<spring:message code="message.date.legality"/>',
		dateISO: '<spring:message code="message.ISODate.legality"/>',
		number: '<spring:message code="message.number.legality"/>',
		digits: '<spring:message code="message.int.only"/>',
		creditcard: '<spring:message code="message.card.legality"/>',
		equalTo: '<spring:message code="message.equleTwo"/>',
		accept: '<spring:message code="message.accept"/>',
		maxlength: jQuery.validator.format('<spring:message code="message.maxlength"/>'),
		minlength: jQuery.validator.format('<spring:message code="message.lessLength"/>'),
		rangelength: jQuery.validator.format('<spring:message code="message.rangelength"/>'),
		range: jQuery.validator.format('<spring:message code="message.range"/>'),
		max: jQuery.validator.format('<spring:message code="message.max"/>'),
		min: jQuery.validator.format('<spring:message code="message.min"/>')
});

jQuery.extend(jQuery.validator.defaults, {
	ignore: "",
    errorElement: "span",
	wrapper: "span",
	errorPlacement: function(error, element) {  
		error.appendTo(element.next().find(" > div"));
	},
	onkeyup:false,
	focusCleanup:true,
	onfocusout:function(element) {$(element).valid()}
});

$.validator.addMethod(
		   "isIncludeSpecialChar", 
		   function(value, element) {   
	           var validName = /['"]+/;   
	           return !validName.test(value);   
	       }, 
	       $.validator.format('<spring:message code="message.string.validator"/>')
);
$.validator.addMethod(
		   "isValidIp", 
		   function(value, element) {   
			   return (/^(\d+)\.(\d+)\.(\d+)\.(\d+)$/.test(value) && (RegExp.$1 < 256 && RegExp.$2 < 256 && RegExp.$3 < 256 && RegExp.$4 < 256));
	       }, 
	       $.validator.format('<spring:message code="clusterManage.inputValidIP"/>')
); 
$.validator.addMethod(
		   "isValidEmail", 
		   function(value, element) {   
			   return /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/.test(value);
	       }, 
	       $.validator.format('<spring:message code="message.email.format.right"/>')
); 
$.validator.addMethod(
		   "isValidEmailConfirmPwd", 
		   function(value, element, param) { 
			   var ret = false;
			   $.ajax({
			        type: "POST",
			        async: false,
			        url:"${ctx}/syscommon/validpwd",
			        data:$("#modifyEmailForm").serialize(),
			        success: function(data) {
			        	ret = true;
			        }
			    });
		       return ret;
	       }, 
	       $.validator.format('<spring:message code="message.pwd.validator"/>')
); 
$.validator.addMethod(
		   "isValidPwd", 
		   function(value, element, param) { 
			   var ret = false;
			   $.ajax({
			        type: "POST",
			        async: false,
			        url:"${ctx}/syscommon/validpwd",
			        data:$("#modifyPwdForm").serialize(),
			        success: function(data) {
			        	ret = true;
			        }
			    });
		       return ret;
	       }, 
	       $.validator.format('<spring:message code="message.pwd.validator"/>')
);  
$.validator.addMethod(
		   "isValidOldPwd", 
		   function(value, element, param) { 
			   var ret = false;
			   $.ajax({
			        type: "POST",
			        async: false,
			        url:"${ctx}/syscommon/validOldpwd",
			        data:$("#modifyPwdForm").serialize(),
			        success: function(data) {
			        	ret = true;
			        }
			    });
		       return ret;
	       }, 
	       $.validator.format('<spring:message code="message.pwd.validator"/>')
);
</script>