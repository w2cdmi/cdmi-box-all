<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="pw.cdmi.box.disk.utils.CSRFTokenManager" %>
<%@ page import="pw.cdmi.box.disk.oauth2.domain.UserToken" %>
<%@ page import="org.apache.shiro.SecurityUtils" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%
    request.setAttribute("token",
            CSRFTokenManager.getTokenForSession(session));
%>
<%
    request.setAttribute("loginId", ((UserToken) SecurityUtils
            .getSubject().getPrincipal()).getId());
%>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="../../common/common.jsp" %>
    <script src="${ctx}/static/js/public/JQbox-hw-grid.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/JQbox-hw-page.js" type="text/javascript"></script>
</head>
<body>
<input type="hidden" id="tempNum"/>
<div id="pageLoadingContainer"
     style="z-index:10000; position:fixed; left:0; top:0; right:0; bottom:0; background:#fff;"></div>

<div class="sys-content">
    <div class="handle-con">
        <button type="button" class="btn btn-primary"
                onclick="openCreateDoctype()" id="createdoctypeBtn"><spring:message
                code='doctype.title.create'/></button>

        <%-- <div class="input-append pull-right">
            <input type="text" id="keyword" name="keyword"
                class="span3 search-query" value="<c:out value='${filter}'/>" placeholder='<spring:message code="group.title.name"/>' />
            <button type="button" class="btn" id="searchButton">
                <i class="icon-search"></i>
            </button>
        </div> --%>
        <%-- <select class="span2 pull-right" id="userType" name="userType" style="display:none">
            <option value="all" selected="selected"><spring:message code='group.field.label.select.type'/></option>
            <option value="public"><spring:message code='group.type.public'/></option>
            <option value="private"><spring:message code='group.type.private'/></option>
        </select> --%>
    </div>

    <div class="table-con">
        <div id="doctypeList"></div>
    </div>
</div>
</body>
<script type="text/javascript">
    var searchSpinner = new Spinner(optsBigSpinner).spin(document
        .getElementById("pageLoadingContainer"));
    var opts_viewGrid = null;
    var navId = "";
    //var opts_page = null;
    var headData = {
        "columnName": {
            "title": '<spring:message code="doctype.column.name" />',
            "width": "12%"
        },
        "columnDescription": {
            "title": '<spring:message code="doctype.column.description" />',
            "width": "46%"
        },
        "operation": {
            "title": '<spring:message code="doctype.column.operate" />',
            "width": "12%"
        }
    };

    $(document).ready(function () {
        init();
        /* window.onload = processHash;
         window.onhashchange = processHash; */
    });

    var processHash = function () {
        initDataList();
    }

    function getLoading() {
        return document.getElementById("pageLoadingContainer");
    }

    function init() {
        opts_viewGrid = $("#doctypeList").comboTableGrid({
            headData: headData,
            ItemOp: "user-defined",
            dataId: "id",
            taxisFlag: true
        });
        //$.fn.comboPage.pageSkip = function(opts, _idMap) {
        //	 initDataList();
        //};
        //
        $.fn.comboTableGrid.setItemOp = function (tableData, rowData, tdItem,
                                                  colIndex) {


            switch (colIndex) {
                case "columnName":
                    var docTypeName = chanageNameValue(rowData.name);
                    tdItem.find("p").append(docTypeName);
                    var text = tdItem.find("p").text();
                    tdItem.find("p").attr("title", text);
                    break;
                case "columnDescription":
                    tdItem.find("p").append(rowData.value);
                    var text = tdItem.find("p").text();
                    tdItem.find("p").attr("title", text);
                    tdItem.find("p").attr("width", text.length);
                    break;
                case "operation":
                    var isdefault = rowData.isDefault;
                    if (isdefault != 1) {
                        var btns = '<input class="btn btn-small" type="button" value="<spring:message code='doctype.button.del' />" onClick="deleteDoctype('
                            + rowData.id + ')"/><input class="btn btn-small" type="button" value="<spring:message code='group.button.modify' />" onClick="openModifyDoctype('
                            + rowData.id + ')"/>';
                        tdItem.find("p").html(btns);
                    }
                    break;
                default:
                    break;
            }
        };

        initDataList();
    }

    function chanageNameValue(name) {
        //var language = "<spring:message code='common.language1'/>"
        //if(language == "en"){
        if ("ufm.DocType.document" == name) {
            return "<spring:message code='ufm.DocType.document'/>"
        } else if ("ufm.DocType.picture" == name) {
            return "<spring:message code='ufm.DocType.picture'/>"
        } else if ("ufm.DocType.audio" == name) {
            return "<spring:message code='ufm.DocType.audio'/>"
        } else if ("ufm.DocType.video" == name) {
            return "<spring:message code='ufm.DocType.video'/>"
        } else if ("ufm.DocType.other" == name) {
            return "<spring:message code='ufm.DocType.other'/>"
        } else {
            return name;
        }
    }

    function initDataList() {
        var url = "${ctx}/user/doctype/find/owner";
        $.ajax({
            type: "GET",
            url: url,
            error: function (request) {
                spinnerFading();
                _statusText = request.statusText;
                handlePrompt("error", "<spring:message code='doctype.error.operation.fail'/>");
            },
            success: function (data) {
                spinnerFading();
                catalogData = data.docUserConfigs;
                $("#doctypeList").setTableGridData(catalogData, opts_viewGrid);
                var pageH = $("body").outerHeight();
                top.iframeAdaptHeight(pageH);
            }
        });
    }

    function spinnerFading() {
        searchSpinner.stop();
        $("#pageLoadingContainer").css({
            "z-index": "-10000"
        });
    }

    function openCreateDoctype() {
        var url = "${ctx}/user/doctype/create";
        top.ymPrompt.win({
            message: url,
            width: 600,
            height: 330,
            title: '<spring:message code="doctype.title.create"/>',
            iframe: true,
            btn: [
                ['<spring:message code="button.ok"/>', 'ok', false,
                    "btn-focus"],
                ['<spring:message code="button.cancel"/>', 'no', true,
                    "btn-cancel"]],
            handler: createDoctype
        });
        top.ymPrompt_addModalFocus("#btn-focus");
    }

    function createDoctype(tp) {
        if (tp == 'ok') {
            top.ymPrompt.getPage().contentWindow.submitDoctype();
        } else {
            top.ymPrompt.close();
        }
    }

    function openModifyDoctype(id) {
        var url = "${ctx}/user/doctype/modify/" + id;
        top.ymPrompt.win({
            message: url,
            width: 600,
            height: 330,
            title: '<spring:message code="doctype.title.modify"/>',
            iframe: true,
            btn: [
                ['<spring:message code="button.ok"/>', 'ok', false,
                    "btn-focus"],
                ['<spring:message code="button.cancel"/>', 'no', true,
                    "btn-cancel"]],
            handler: ModifyDoctype
        });
        top.ymPrompt_addModalFocus("#btn-focus");
    }

    function ModifyDoctype(tp) {
        if (tp == 'ok') {
            top.ymPrompt.getPage().contentWindow.submitDoctype();
        } else {
            top.ymPrompt.close();
        }
    }


    function deleteDoctype(id) {
        top.ymPrompt.confirmInfo({
            title: '<spring:message code="doctype.title.dissolution"/>',
            message: '<spring:message code="doctype.field.dissolution"/>',
            width: 450,
            closeTxt: '<spring:message code="common.close"/>',
            handler: function (tp) {
                if (tp == "ok") {
                    executeDeleteDoctype(id);
                }
            },
            btn: [['<spring:message code="button.ok"/>', "ok"],
                ['<spring:message code="button.cancel"/>', "cancel"]]
        });
    }

    function executeDeleteDoctype(id) {
        $.ajax({
            type: "DELETE",
            //data : {token : "<c:out value='${token}'/>"},
            url: "${ctx}/user/doctype/remove/id/" + id,
            error: function (request) {
                _statusText = request.statusText;
                /* if (_statusText == "Unauthorized") {
                 handlePrompt("error", "
                <spring:message code='group.error.operation.fail'/>");
                 } else if (_statusText == "NoSuchGroup") {
                 handlePrompt("error", "
                <spring:message code='group.error.group.noexist'/>");
                 } else if (_statusText == "Forbidden") {
                 handlePrompt("error", "
                <spring:message code='group.error.forbidden'/>");
                 } else { */
                handlePrompt("error", "<spring:message code='doctype.error.operation.fail'/>");
                //}
            },
            success: function () {
                top.handlePrompt("success", '<spring:message code="doctype.success.operation"/>');
                initDataList();
            }
        });
    }

</script>
</html>
