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
    var roleMsgs = {
        "auther": "<spring:message code='systemRole.title.auther'/>",
        "editor": "<spring:message code='systemRole.title.editor'/>",
        "uploadAndView": "<spring:message code='systemRole.title.uploadAndView'/>",
        "viewer": "<spring:message code='systemRole.title.viewer'/>",
        "uploader": "<spring:message code='systemRole.title.uploader'/>",
        "downloader": "<spring:message code='systemRole.title.downloader'/>",
        "previewer": "<spring:message code='systemRole.title.previewer'/>",
        "lister": "<spring:message code='systemRole.title.lister'/>",
        "prohibitVisitors": "<spring:message code='systemRole.title.prohibitVisitors'/>"
    };

    function setAuthorityHint(data) {
        var str = "";
        if (data != null) {
            if (data["browse"] == 1) {
                str += "<spring:message code='systemRoleList.label.browse'/>, ";
            }
            if (data["preview"] == 1) {
                str += "<spring:message code='systemRoleList.label.preview'/>, ";
            }
            if (data["upload"] == 1) {
                str += "<spring:message code='systemRoleList.label.upload'/>, ";
            }
            if (data["download"] == 1) {
                str += "<spring:message code='systemRoleList.label.download'/>, ";
            }
            if (data["edit"] == 1) {
                str += "<spring:message code='systemRoleList.label.edit'/>, ";
            }
            if (data["publishLink"] == 1) {
                str += "<spring:message code='systemRoleList.label.publishLink'/>, ";
            }
            if (data["authorize"] == 1) {
                str += "<spring:message code='systemRoleList.label.authorize'/>, ";
            }
            if (str != "") {
                str = str.substring(0, str.length - ", ".length);
            }
        }
        return str;
    }

</script>