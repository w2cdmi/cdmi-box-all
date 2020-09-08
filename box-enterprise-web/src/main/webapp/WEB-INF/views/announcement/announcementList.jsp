<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="../common/common.jsp" %>
    <link href="${ctx}/static/skins/default/css/layout.css" rel="stylesheet" type="text/css"/>
    <script src="${ctx}/static/js/public/JQbox-hw-grid.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/JQbox-hw-page.js" type="text/javascript"></script>
</head>
<body>
<div id="pageLoadingContainer"
     style="z-index:10000; position:fixed; left:0; top:0; right:0; bottom:0; background:#fff;"></div>
<script type="text/javascript">new Spinner(optsBigSpinner).spin(document.getElementById("pageLoadingContainer"));</script>

<div class="header">
    <div class="header-con">
        <div class="logo" id="logoBlock"><img/></div>
        <div class="preview-file-name"><span><spring:message code="message.category.announcement"/></span></div>
    </div>
</div>

<div class="body">
    <div class="body-con announcement-con clearfix">
        <div class="pull-left">
            <div id="announcementList"></div>
            <div id="announcementListPageBox"></div>
        </div>
        <div class="pull-right">
            <div class="content-con">
                <h1 id="announcementTitle" style="word-wrap:break-word;overflow:auto;"><c:out
                        value='${announcement.title }'/></h1>
                <h6 id="announcementPublishTime"></h6>
                <p id="announcementContent" style="word-wrap:break-word;overflow:auto;"><c:out
                        value='${announcement.content }'/></p>
            </div>
        </div>


    </div>
</div>
<%@ include file="../common/footer.jsp" %>
<script type="text/javascript">
    var selectId = "<c:out value='${announcement.id}'/>";

    var receiverId = <shiro:principal property="cloudUserId"/>;

    var opts_AnnouncementGrid = null;
    var opts_page = null;

    var currentPage = 1;

    var headAnnouncementList = {
        "icon": {"width": "20px"},
        "title": {"width": ""}
    };

    $(document).ready(function () {
        var publishTime = "${announcement.createdAt.getTime() }";
        if ('' != publishTime) {
            $("#announcementPublishTime").html(getLocalTime(parseInt(publishTime)));
        }

        loadSysSetting("${ctx}", '<spring:message code="common.language1" />');

        init();

        listAnnouncement(currentPage);

        var noSuchAnnouncement = '<c:out value="${noSuchAnnouncement}"/>';
        if (noSuchAnnouncement) {
            top.handlePrompt("error", '<spring:message code="announcement.been.delete"/>');
        }
    });


    function init() {
        opts_AnnouncementGrid = $("#announcementList").comboTableGrid({
            headData: headAnnouncementList,
            dataNullTip: '<spring:message code="announcement.null.tips" />',
            border: false,
            hideHeader: true,
            splitRow: false,
            dataId: "id"
        });

        opts_page = $("#announcementListPageBox").comboPage({
            showInfo: false,
            pageSkip: false,
            lang: '<spring:message code="common.language1"/>'
        });

        $.fn.comboPage.pageSkip = function (opts, _idMap, curPage) {
            currentPage = curPage;
            listAnnouncement(curPage);
        };

        $.fn.comboTableGrid.setItemOp = function (tableData, rowData, tdItem, colIndex) {
            switch (colIndex) {
                case "icon":
                    try {
                        tdItem.find("p").html("<i class='icon-message'></i>");
                        tdItem.attr("id", rowData.id);
                        tdItem.parents("tr").click(function () {
                            tdItem.parents("tbody").find("tr").removeClass("grid_Selected");
                            tdItem.parents("tr").addClass("grid_Selected");
                            selectId = rowData.id;

                            $("#announcementTitle").html(rowData.title);
                            $("#announcementPublishTime").html(getLocalTime(rowData.createdAt));
                            $("#announcementContent").html(rowData.content);
                        })
                    } catch (e) {
                    }
                    break;
                case "title":
                    try {
                        tdItem.find("p").html("<a>" + rowData.title + "</a><br /><span style='color:#999;'>" + getLocalTime(rowData.createdAt) + "</span>");
                    } catch (e) {
                    }
                    break;
                default :
                    break;
            }
        };
    }


    function listAnnouncement(curPage) {
        var params = {
            "pageNumber": curPage,
            "pageSize": 10,
            "token": "<c:out value='${token}'/>"
        };
        $.ajax({
            type: "POST",
            url: "${ctx}/announcement/listAnnouncement",
            data: params,
            error: function (request) {
                if (request.status == 404) {
                    handlePrompt("error", "<spring:message code='error.notfound'/>");
                } else {
                    handlePrompt("error", "<spring:message code='message.errorMsg.listMsgFailed'/>");
                }
                comboxRemoveLoading("pageLoadingContainer");

            },
            success: function (data) {
                if (typeof(data) == 'string' && data.indexOf('<html>') != -1) {
                    window.location.href = "${ctx}/logout";
                    return;
                }

                if (data.content.length == 0 && curPage != 1) {
                    curPage--;
                    listAnnouncement(curPage);
                    return;
                }

                $("#announcementList").setTableGridData(data.content, opts_AnnouncementGrid);
                $("#announcementListPageBox").setPageData(opts_page, data.number, data.size, data.totalElements);

                if ('' != selectId) {
                    $("#announcementList").find("#" + selectId).parent().addClass("grid_Selected");
                }
                comboxRemoveLoading("pageLoadingContainer");
            }
        });
    }
</script>

</body>
</html>