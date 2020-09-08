<%@ page contentType="text/html;charset=UTF-8" %>
<HEAD>
	<META HTTP-EQUIV="Expires" CONTENT="0">
	<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
	<META HTTP-EQUIV="Cache-control" CONTENT= "no-cache, no-store, must-revalidate">
	<META HTTP-EQUIV="Cache" CONTENT="no-cache"> 
</HEAD>
<% 
response.setHeader("Cache-Control","no-cache, no-store, must-revalidate");
response.setHeader("Pragma","no-cache");
response.setDateHeader("Expires",0);
%>

<script type="text/javascript">
jQuery.extend(jQuery.validator.messages, {
        required: '<spring:message code="messages.required"/>',
		remote: '<spring:message code="messages.remote"/>',
		email: '<spring:message code="messages.email"/>',
		url: '<spring:message code="messages.url"/>',
		date: '<spring:message code="messages.date"/>',
		dateISO: '<spring:message code="messages.dateISO"/>',
		number: '<spring:message code="messages.number"/>',
		digits: '<spring:message code="messages.digits"/>',
		creditcard: '<spring:message code="messages.creditcard"/>',
		equalTo: '<spring:message code="messages.equalTo"/>',
		accept: '<spring:message code="messages.accept"/>',
		maxlength: jQuery.validator.format('<spring:message code="messages.maxlength"/>'),
		minlength: jQuery.validator.format('<spring:message code="messages.minlength"/>'),
		rangelength: jQuery.validator.format('<spring:message code="messages.rangelength"/>'),
		range: jQuery.validator.format('<spring:message code="messages.range"/>'),
		max: jQuery.validator.format('<spring:message code="messages.max"/>'),
		min: jQuery.validator.format('<spring:message code="messages.min"/>')
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
	       
	       $.validator.format('<spring:message code="messages.special.char"/>')
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
			   if(null == value || "" == value)
			   {
				  return true;   
			   }
			   else
			   {
				   return /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/.test(value);   
			   }
	       }, 
	       $.validator.format('<spring:message code="messages.email"/>')
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
	       $.validator.format('<spring:message code="messages.valid.pwd"/>')
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
	       $.validator.format('<spring:message code="messages.valid.pwd"/>')
); 
</script>