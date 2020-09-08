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
    var DISPLAY_SPAN = "#sortOrderName";
    var SORT_NAME_LI = "#nameSort";
    var SORT_OWNER = "#ownerSort";
    var SORT_SHARE_DATE = "#shareDateSort";
    var orderField = "name";

    var isDesc = getRootCookie("ShareIsDesc");
    isDesc = isDesc == null ? "false" : isDesc;

    function sortByFileName() {
        $(DISPLAY_SPAN).html('<spring:message code="common.field.name"/>');
        orderField = "name";
        changeOrderDirect("nameSort");
        listData();
        setOrderToCookie();
    }

    function sortBySize() {
        $("#sortOrderName").html("<spring:message code='common.field.size'/>");
        orderField = "size";

        changeOrderDirect("sizeSortLink");

        listData();

        setOrderToCookie();
    }

    function sortByDate() {
        $(DISPLAY_SPAN).html('<spring:message code="shareIndex.sort.sharedTime"/>');
        orderField = "modifiedAt";
        changeOrderDirect("shareDateSort");
        listData();
        setOrderToCookie();
    }

    function setOrderToCookie() {
        setRootCookie("ShareIsDesc", isDesc);
    }

    function userCustomInit() {
        if (isDesc == "true") {
            $("#sortArray").attr("class", "icon-arrow-down");
        } else {
            $("#sortArray").attr("class", "icon-arrow-up");
        }
        if (orderField == "modifiedAt") {
            $(DISPLAY_SPAN).html('<spring:message code="shareIndex.sort.sharedTime"/>');
            if (isDesc == "true") {
                $(SORT_SHARE_DATE).append("<i class='icon-arrow-down'></i>");
            } else {
                $(SORT_SHARE_DATE).append("<i class='icon-arrow-up'></i>");
            }
        } else if (orderField == "name") {
            $(DISPLAY_SPAN).html('<spring:message code="common.field.name"/>');
            if (isDesc == "true") {
                $(SORT_NAME_LI).append("<i class='icon-arrow-down'></i>");
            } else {
                $(SORT_NAME_LI).append("<i class='icon-arrow-up'></i>");
            }
        } else if (orderField == "ownerName") {
            $(DISPLAY_SPAN).html('<spring:message code="shareIndex.sort.shareUser"/>');
            if (isDesc == "true") {
                $(SORT_OWNER).append("<i class='icon-arrow-down'></i>");
            } else {
                $(SORT_OWNER).append("<i class='icon-arrow-up'></i>");
            }
        }
    }

    function changeOrderDirect(elementId) {
        var attDesc = $("#" + elementId).attr("isDesc");
        if (attDesc != undefined && attDesc != "") {
            isDesc = attDesc;
        }
        if (null == isDesc || "" == isDesc || undefined == isDesc) {
            isDesc = true;
        } else {
            if (isDesc == true || isDesc == 'true') {
                isDesc = false;
            } else {
                isDesc = true;
            }
        }
        $("#sortUl i").remove();
        $("#" + elementId).append("<i/>");
        if (isDesc) {
            $("#" + elementId + " i, #sortArray").attr("class", "icon-arrow-down");
        } else {
            $("#" + elementId + " i, #sortArray").attr("class", "icon-arrow-up");
        }

        $("#sortUl a").attr("isDesc", "");
        $("#" + elementId).attr("isDesc", isDesc);
    }
</script>