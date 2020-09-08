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
    <%--
     * Translated default messages for the jQuery validation plugin.
      --%>
    jQuery.extend(jQuery.validator.messages, {
        required: '<spring:message code="common.validate.requiredfield"/>',
        remote: "<spring:message code='input.update.message'/>",
        email: "<spring:message code='input.valid.emailaddr'/>",
        url: "<spring:message code='input.valid.netaddr'/>",
        date: "<spring:message code='input.valid.date'/>",
        dateISO: "<spring:message code='input.valid.ISO'/>",
        number: "<spring:message code='input.valid.ineteger'/>",
        digits: "<spring:message code='input.ineteger'/>",
        creditcard: "<spring:message code='input.validcreditcardNumber'/>",
        equalTo: "<spring:message code='input.equalValue'/>",
        accept: "<spring:message code='input.substring.legal'/>",
        maxlength: jQuery.validator.format("<spring:message code='input.maxlength'/>"),
        minlength: jQuery.validator.format("<spring:message code='input.minlength'/>"),
        rangelength: jQuery.validator.format("<spring:message code='input.length.betweenZeroAndOne'/>"),
        range: jQuery.validator.format("<spring:message code='input.value.betweenZeroAndOne'/>"),
        max: jQuery.validator.format("<spring:message code='input.max'/>"),
        min: jQuery.validator.format("<spring:message code='input.min'/>")
    });

    jQuery.extend(jQuery.validator.defaults, {
        errorElement: "span",
        wrapper: "span",
        errorPlacement: function (error, element) {
            error.appendTo(element.next().find(" > div"));
        },
        onkeyup: false,
        focusCleanup: true,
        onfocusout: function (element) {
            $(element).valid()
        }
    });
</script>