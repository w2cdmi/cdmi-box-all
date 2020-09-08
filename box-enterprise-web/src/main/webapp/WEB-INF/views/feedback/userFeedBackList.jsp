<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cse" uri="http://cse.huawei.com/custom-function-taglib" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%
    request.setAttribute("token", CSRFTokenManager.getTokenForSession(session));
%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="Cache-Control" content="no-cache"/>
    <meta http-equiv="Pragma" content="no-cache"/>
    <title></title>
    <%@ include file="../common/common.jsp" %>
    <link href="${ctx}/static/skins/default/css/layout.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/static/skins/layout/userFeedBack.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/static/jqueryUI-1.9.2/jquery-ui.min.css" rel="stylesheet" type="text/css"/>
    <script src="${ctx}/static/js/public/JQbox-hw-grid.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/JQbox-hw-files.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/JQbox-hw-page.js" type="text/javascript"></script>
    <script src="${ctx}/static/jqueryUI-1.9.2/jquery-ui.min.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
</head>

<body>
<%@ include file="../common/header.jsp" %>

<div class="userFeedBackBody">

    <div class="breadcrumb">
        <div class="breadcrumb-con clearfix">
            <ul id="breadcrumbCon">
                <li class="active"><span><spring:message code="navigation.userFeedBack"/></span></li>
            </ul>
        </div>
    </div>
    <div class="userFeedBackContent">

        <!--提问开始 -->
        <div class="sys-content" style="padding: 10px 0 0px 15px;padding-top:46px">
            <div class="form-horizontal form-con clearfix">
                <form class="form-horizontal label-w100" id="creatFeedBackForm" name="creatFeedBackForm">
                    <div class="control-group" style="width: 100%;">
                        <label class="control-label" for=""
                               style="width: 8%; margin-right: 10px;"><em>*</em><spring:message
                                code='feedback.create.problemTitle'/></label>
                        <div class="controls" style="width: 90%;">
                            <input type="text" class="span4" id="problemTitle" name="problemTitle" style="width: 90%;"/>
                            <span class="validate-con bottom"><div></div></span>
                        </div>
                    </div>

                    <div class="control-group" style="width: 100%;">
                        <label class="control-label" for=""
                               style="width: 8%;margin-right: 10px;"><em>&nbsp</em><spring:message
                                code='feedback.create.problemType'/></label>
                        <div class="controls" style="width: 90%;margin-top: 4px;margin-bottom: 4px;">
                            <input name="problemType" type="radio" value="0" checked="checked"
                                   style="margin-top: 1px;"/><spring:message code='feedback.create.suggestion'/>
                            <input name="problemType" type="radio" value="1"
                                   style="margin-left: 200px; margin-top: 1px;"/><spring:message
                                code='feedback.create.fault'/>
                        </div>
                    </div>

                    <div class="control-group" style="width: 100%;">
                        <label class="control-label" for=""
                               style="width: 8%;margin-right: 10px;"><em>*</em><spring:message
                                code='feedback.create.problemDescription'/></label>
                        <div class="controls" style="width: 90%;">
                            <textarea class="span4" rows="6" cols="30" name="problemDescription" id="problemDescription"
                                      style="width: 90%;"></textarea>
                            <span class="validate-con bottom"><div></div></span>
                        </div>
                    </div>

                    <div class="control-group" style="width:100%;">
                        <label for="input" class="control-label" style="width:6%;"></label>
                        <div class="controls" style="width:14%;float: right;">
                            <button id="newTeamBtn" class="btn btn-primary" type="button" onClick="submitFeedBack()">
                                <spring:message code="feedback.list.button.addFeedBack"/></button>
                        </div>
                    </div>

                    <input type="hidden" id="token" name="token" value="${token}"/>
                </form>
            </div>
        </div>
        <!--提问结束  -->


        <div class="sys-content" style="padding: 0px 0 0px 0px;">
            <div class="form-horizontal form-con clearfix">
                <div class="form-search">
                    <input type="hidden" id="page" name="page" value="1">
                    <input type="hidden" id="token" name="token" value="${cse:htmlEscape(token)}"/>
                    <input type="hidden" id="customerID" name="customerID"
                           value="${cse:htmlEscape(condition.customerID)}"/>
                    <!-- 意见凤反馈查询开始 -->
                    <div class="control-group" style="width:25%;float: left;">
                        <label class="control-label" for="input" style="width:20%;margin-right: 10px;"><spring:message
                                code="feedback.list.condtion.problemStatus"/></label>
                        <div class="controls" style="margin-left:50px;width:70%;">
                            <select style="width:80%;" id="feedbackStatus" name="problemStatus"
                                    onchange="changeAuthType()">

                                <option value=""><spring:message code="feedback.list.condition.status.all"/></option>
                                <option value="1"
                                        <c:if test="${condition.problemStatus == '1'}">selected="selected"</c:if>>
                                    <spring:message code="feedback.list.condtion.problemStatus.close"/></option>
                                <option value="0"
                                        <c:if test="${condition.problemStatus == '0'}">selected="selected"</c:if>>
                                    <spring:message code="feedback.list.condtion.problemStatus.open"/></option>
                            </select>
                        </div>
                    </div>

                    <div class="control-group" style="width:25%;float: left;">
                        <label class="control-label" for="input" style="width:20%;margin-right: 10px;"><spring:message
                                code="feedback.list.condtion.cycle"/></label>
                        <div class="controls" style="margin-left:50px;width:70%;">
                            <select style="width:80%;" id="feedbackCycle" name="cycle" onchange="changeAuthType()">
                                <option value=""><spring:message code="feedback.list.condition.status.all"/></option>
                                <option value="dayRound"
                                        <c:if test="${condition.cycle == 'dayRound'}">selected="selected"</c:if>>
                                    <spring:message code="feedback.list.condtion.cycle.day"/></option>
                                <option value="weekRound"
                                        <c:if test="${condition.cycle == 'weekRound'}">selected="selected"</c:if>>
                                    <spring:message code="feedback.list.condtion.cycle.week"/></option>
                                <option value="monthRound"
                                        <c:if test="${condition.cycle == 'monthRound'}">selected="selected"</c:if>>
                                    <spring:message code="feedback.list.condtion.cycle.month"/></option>

                            </select>
                        </div>
                    </div>


                    <div class="control-group" style="width:30%;float: left;">
                        <div class="controls" style="margin-left:0px; width: 100%;">
                            <input type="text" id="feedbackTitle" style="width: 100%;" name="problemTitle"
                                   value="${cse:htmlEscape(condition.problemTitle)}"
                                   placeholder='<spring:message code="feedback.list.condtion.searchDescription"/>'/>
                        </div>
                    </div>

                    <div class="control-group" style="width:20%;float:left;">
                        <label for="input" class="control-label" style="width:15%;"></label>
                        <div class="controls" style="margin-left: 50px;width:80%;">
                            <button id="usersubmit_btn" type="button" class="btn btn-primary"><spring:message
                                    code="feedback.list.button.query"/></button>
                            <button id="userreset_btn" type="button" class="btn btn-primary" onClick="resetCondition()">
                                <spring:message code="feedback.list.button.reset"/></button>
                        </div>
                    </div>
                    <!-- 意见凤反馈查询结束 -->

                </div>
            </div>
        </div>

        <!-- 意见凤反馈批量删除 -->
        <div class="control-group" style="width:100%;float:left;">
            <div class="controls" style="margin-left: 0px;width:80%;">
                <button type="button" class="btn btn-primary" onClick="deletelistFeedBack()"><spring:message
                        code="feedback.list.title.batchDelete"/></button>
            </div>
        </div>

        <div class="table-con" style="width:100%;height:800px">
            <div id="rankList" style="height:680px;width: 100%;margin-bottom: 0px;"></div>
            <div id="rankListPage" style="margin-left: 0px;width: 98%;"></div>
        </div>
    </div>
</div>


<%@ include file="../common/footer.jsp" %>
<script type="text/javascript">

    var appIds = "";
    var currentPage = 1;
    var newHeadItem = "";
    var newFlag = false;
    var opts_viewGrid = null;
    var opts_page = null;
    var headData = {
        "managerAnswerTime": {
            "title": "<spring:message code='feedback.list.result.answerTime'/>",
            "width": "15%;"
        },
        "problemTitle": {
            "title": "<spring:message code='feedback.list.result.problemTitle'/>",
            "width": "30%"
        },
        "problemType": {
            "title": "<spring:message code='feedback.list.result.problemType'/>",
            "width": "10%"
        },
        "newestTwTime": {
            "title": "<spring:message code='feedback.list.result.customerTwTime'/>",
            "width": "15%"
        },
        "problemStatus": {
            "title": "<spring:message code='feedback.list.result.problemStatus'/>",
            "width": "10%"
        },
        "operation": {
            "title": "<spring:message code='feedback.list.result.operation'/>",
            "width": "20%"
        }
    };

    $(document).ready(function () {
        var selectAll;
        var selectPage;
        if ('<spring:message code="common.language1"/>' == 'en') {
            selectAll = "select all";
            selectPage = "select currentPage";
        } else if ('<spring:message code="common.language1"/>' == 'zh-cn') {
            selectAll = "选择全部";
            selectPage = "选择当前页";
        }
        opts_viewGrid = $("#rankList").comboTableGrid({
            headData: headData,
            checkBox: true,
            checkAll: true,
            colspanDrag: true,
            height: 860,
            dataId: "problemID",
            definedColumn: true,
            taxisFlag: true,
            string: {
                checkAllTxt: selectAll,
                checkCurPageTxt: selectPage
            }
        });

        $.fn.comboTableGrid.setItemOp = function (tableData, rowData, tdItem, colIndex) {
            switch (colIndex) {
                case "problemType":
                    try {
                        var problemType = tdItem.find("p").text();
                        if (problemType == 0) {
                            tdItem.find("p").html("<spring:message code='feedback.create.suggestion'/>").parent().attr("title", "<spring:message code='feedback.create.suggestion'/>");
                        }
                        if (problemType == 1) {
                            tdItem.find("p").html("<spring:message code='feedback.create.fault'/>").parent().attr("title", "<spring:message code='feedback.create.fault'/>");
                        }
                    } catch (e) {
                    }
                    break;


                case "operation":
                    try {

                        if (rowData.problemStatus == 1 || rowData.problemStatus == 3) {
                            btns = '<input class="btn btn-small" type="button" style="margin-right:10px;" value="<spring:message  code="feedback.list.title.query"  />" onClick="showFeedBack('
                                + rowData.problemID
                                + ')"/> <input class="btn btn-small" type="button" style="margin-right:10px;" value="<spring:message  code="feedback.list.title.delete"  />" onClick="deleteFeedBack('
                                + rowData.problemID + ''
                                + ')"/> <input class="btn btn-small" type="button" style="margin-right:10px;" value="<spring:message  code="feedback.list.title.ask"  />" onClick="modifyFeedBack('
                                + rowData.problemID + ')"/>';
                        } else {
                            btns = '<input class="btn btn-small" type="button" style="margin-right:10px;" value="<spring:message  code="feedback.list.title.query"  />" onClick="showFeedBack('
                                + rowData.problemID
                                + ')"/> <input class="btn btn-small" type="button" value="<spring:message  code="feedback.list.title.delete"  />" onClick="deleteFeedBack('
                                + rowData.problemID + ''
                                + ')"/>';
                        }

                        tdItem.find("p").html(btns);
                    } catch (e) {
                    }
                    break;

                case "problemStatus":
                    try {
                        var problemStatus = tdItem.find("p").text();
                        if (problemStatus == 0) {
                            tdItem.find("p").html("<spring:message code='feedback.list.condtion.problemStatus.open'/>").parent().attr("title", "<spring:message code='feedback.list.condtion.problemStatus.open'/>");
                        }
                        if (problemStatus == 1 || problemStatus == 3) {
                            tdItem.find("p").html("<spring:message code='feedback.list.condtion.problemStatus.close'/>").parent().attr("title", "<spring:message code='feedback.list.condtion.problemStatus.close'/>");
                        }
                    } catch (e) {
                    }
                    break;

                case "managerAnswerTime":
                    try {
                        var size = tdItem.find("p").text();
                        for (var i = 0; i < catalogData.length; i++) {
                            if (size == catalogData[i].managerAnswerTime) {
                                _txt = catalogData[i].managerAnswerTime;
                                var date = new Date(_txt);
                                var _year = date.getFullYear();
                                var _month = date.getMonth() + 1;
                                if (_month < 10) {
                                    _month = "0" + _month;
                                }
                                var _day = date.getDate();
                                if (_day < 10) {
                                    _day = "0" + _day;
                                }
                                var _hours = date.getHours();
                                if (_hours < 10) {
                                    _hours = "0" + _hours;
                                }
                                var _min = date.getMinutes();
                                if (_min < 10) {
                                    _min = "0" + _min;
                                }
                                var _sec = date.getSeconds();
                                if (_sec < 10) {
                                    _sec = "0" + _sec;
                                }
                                var date = _year + "-" + _month + "-" + _day + " " + _hours + ":" + _min + ":" + _sec
                                tdItem.find("p").html(date).parent().attr("title", date);
                            }
                        }
                    } catch (e) {
                    }
                    break;

                case "newestTwTime":
                    try {
                        var size = tdItem.find("p").text();
                        for (var i = 0; i < catalogData.length; i++) {
                            if (size == catalogData[i].newestTwTime) {
                                _txt = catalogData[i].newestTwTime;
                                var date = new Date(_txt);
                                var _year = date.getFullYear();
                                var _month = date.getMonth() + 1;
                                if (_month < 10) {
                                    _month = "0" + _month;
                                }
                                var _day = date.getDate();
                                if (_day < 10) {
                                    _day = "0" + _day;
                                }
                                var _hours = date.getHours();
                                if (_hours < 10) {
                                    _hours = "0" + _hours;
                                }
                                var _min = date.getMinutes();
                                if (_min < 10) {
                                    _min = "0" + _min;
                                }
                                var _sec = date.getSeconds();
                                if (_sec < 10) {
                                    _sec = "0" + _sec;
                                }
                                var date = _year + "-" + _month + "-" + _day + " " + _hours + ":" + _min + ":" + _sec
                                tdItem.find("p").html(date).parent().attr("title", date);
                            }
                        }
                    } catch (e) {
                    }
                    break;

                default:
                    break;
            }
        };


        $.fn.comboTableGrid.taxisOp = function (headItem, flag) {
            initDataList(currentPage, headItem, flag);
        };

        opts_page = $("#rankListPage").comboPage({
            style: "page table-page",
            lang: '<spring:message code="main.language"/>'
        });

        $.fn.comboPage.pageSkip = function (opts, _idMap, curPage) {
            initDataList(curPage, newHeadItem, newFlag);
        };

        changeAuthType();

        if (!placeholderSupport()) {
// 		placeholderCompatible();
        }
        ;

        $("#usersubmit_btn").on("click", function () {
            initDataList(currentPage, newHeadItem, newFlag);
        });


        //作用获取招云图标放在cloudapp左上角
        loadSysSetting("${ctx}", '<spring:message code="common.language1" />');

        //菜单选中效果
        navMenuSelected("navUserFeedBack");

        $("#creatFeedBackForm").validate({
            rules: {
                problemTitle: {
                    required: true,
                    rangelength: [1, 255]
                },
                problemDescription: {
                    required: true,
                    rangelength: [1, 1024]
                }
            },
            messages: {
                problemTitle: {
                    required: "<spring:message code="feedback.create.problemTitle.required" />",
                    rangelength: "<spring:message code="feedback.create.problemTitle.rangelength" />"
                },
                problemDescription: {
                    required: "<spring:message code="feedback.create.problemDescription.required" />",
                    rangelength: "<spring:message code="feedback.create.problemDescription.rangelength" />"
                }
            },
            onfocusout: false,
            onkeyup: function (element) {
                $(element).valid();
                $('label[class=error]').css("padding-left", "17px") == $('label[class=error]').css("color", "red");

                if ($(element)[0].id == 'problemDescription') {

                    if ($("#problemDescription").val().length == 0 || $("#problemDescription").val().length > 1024) {
                        $("#problemDescription").css("background-color", "#ffe7de");
                    } else {
                        $("#problemDescription").css("background-color", "white");
                    }
                } else {
                    if ($("#problemTitle").val().length == 0 || $("#problemDescription").val().length > 255) {
                        $("#problemTitle").css("background-color", "#ffe7de");
                    } else {
                        $("#problemTitle").css("background-color", "white");
                    }
                }
            },
        });

        $("#messageAddr").keydown(
            function (event) {
                if (event.keyCode == 13) {
                    searchMessageTo();
                    return false;
                }
            });

        $("label").tooltip({container: "body", placement: "top", delay: {show: 100, hide: 0}, animation: false});
    });


    function changeAuthType() {
        initDataList(1, "", false);
    }


    function initDataList(curPage, headItem, flag) {
        var problemStatus = $("#feedbackStatus").val();
        var cycle = $("#feedbackCycle").val();
        var problemTitle = $("#feedbackTitle").val();

        currentPage = curPage;
        newHeadItem = headItem;
        newFlag = flag;
        var authServerId = $("#authenticationMethod").find("option:selected").val();
        var url = "${ctx}/feedback/listFeedback";
        var params = {
            "page": curPage,
            "problemStatus": problemStatus,
            "cycle": cycle,
            "problemTitle": problemTitle,
            "customerID": '${condition.customerID}',
            "token": '${token}'
        };


        $("#rankList").showTableGridLoading();
        $.ajax({
            type: "POST",
            url: url,
            data: params,
            error: function (request) {
                handlePrompt("error", '<spring:message code="common.operationFailed" />');
            },
            success: function (data) {
                catalogData = data[0].content;
                $("#rankList").setTableGridData(catalogData, opts_viewGrid);
                $("#rankListPage").setPageData(opts_page, data[0].number, data[0].size, data[0].totalElements);

                initTableStyle();

                var pageH = $("body").outerHeight();
                top.iframeAdaptHeight(pageH);
            }
        });
    }

    //初始化表格样式（做成百分比可以收缩）
    function initTableStyle() {
        //给页面元素添加样式
        document.getElementById("rankList_head").style.width = "100%";
        document.getElementById("rankList_main").style.width = "100%";
    }
    /**
     * 重置插叙条件
     */
    function resetCondition() {
        $("#feedbackStatus").val("");
        $("#feedbackCycle").val("");
        $("#feedbackTitle").val("");
        $("#twBeginTimeComp").val("");
        $("#twEndTimeComp").val("");
        $("#dfBeginTimeComp").val("");
        $("#dfEndTimeComp").val("");
    }

    /**
     * 提问，弹出弹框
     */
    function openCreate() {
        var url = '${ctx}/feedback/openAddFeedBack';
        top.ymPrompt.win({
            message: url,
            width: 600,
            height: 440,
            title: '<spring:message code="feedback.list.button.addFeedBack"/>',
            iframe: true,
            btn: [['<spring:message code="button.ok"/>', 'ok', false, "btn-focus"], ['<spring:message code="button.cancel"/>', 'no', true, "btn-cancel"]],
            handler: createFeedBack
        });
        top.ymPrompt_addModalFocus("#btn-focus");
    }

    function createFeedBack(tp) {
        if (tp == 'ok') {
            top.ymPrompt.getPage().contentWindow.submitFeedBack();
        } else {
            top.ymPrompt.close();
        }
    }


    function modifyFeedBack(problemID) {
        $('div[class=ym-header-text]').css("text-align", "center");
        top.ymPrompt.win({
            message: '${ctx}/feedback/toAsk?problemID=' + problemID,
            width: 700,
            height: 480,
            title: '<spring:message code="feedback.list.title.ask"/>',
            iframe: true,
            btn: [['<spring:message code="button.ok"/>', 'yes', false, "btnModify"], ['<spring:message code="button.cancel"/>', 'no', true, "btnModifyCancel"]],
            handler: doModifyFeedBack
        });
        top.ymPrompt_addModalFocus("#btnModify");
    }

    function doModifyFeedBack(tp) {
        if (tp == 'yes') {
            top.ymPrompt.getPage().contentWindow.submitModifyFeedBack()

        } else {
            top.ymPrompt.close();
        }
    }


    function showFeedBack(problemID) {
        $('div[class=ym-header-text]').css("text-align", "center");
        top.ymPrompt.win({
            message: '${ctx}/feedback/detail?problemID=' + problemID,
            width: 700,
            height: 480,
            title: '<spring:message code="feedback.manage.title.detail"/>',
            iframe: true,
            handler: doShowFeedBack
        });
    }

    function doShowFeedBack(tp) {
        if (tp == 'yes') {
            top.ymPrompt.getPage().contentWindow.submitShowFeedBack();

        } else {
            top.ymPrompt.close();
        }
    }

    function deleteFeedBack(problemID) {
        top.ymPrompt.confirmInfo({
            title: '<spring:message code="feedback.list.delete.title"/>',
            message: '<spring:message code="feedback.list.delete.message"/>',
            closeTxt: '<spring:message code="common.close"/>',
            handler: function (tp) {
                if (tp == "ok") {
                    $.ajax({
                        type: "POST",
                        url: "${ctx}/feedback/delete",
                        data: {"problemID": problemID},
                        error: function (request) {
                            top.handlePrompt("error", '<spring:message code="feedback.list.deleteFail"/>');
                        },
                        success: function (data) {
                            //判断是否登录过期，跳转登录页面
                            if (data.length != 0) {
                                top.document.getElementById("navUserFeedBack").click();
                            }
                            top.handlePrompt("success", '<spring:message code="feedback.list.deleteSuccess"/>');
                            changeAuthType();
                        }
                    });
                }
            },
            btn: [['<spring:message code="button.ok"/>', "ok"], ['<spring:message code="button.cancel"/>', "cancel"]]
        });
    }


    $("#checkall").click(function () {
        if (this.checked) {
            $("input[name='checkname']:checkbox").each(function () {
                this.checked = true;
            });
        } else {
            $("input[name='checkname']:checkbox").each(function () {
                this.checked = false;
            });
        }
    });


    function deletelistFeedBack() {
        var idArray = $("#rankList").getTableGridSelected();
        if (idArray == "") {
            handlePrompt("error", '<spring:message code="delete.feedback.list.err"/>');
            return;
        }
        var ids;
        if (idArray == "all") {
            ids = "all";
        }
        else {
            ids = idArray.join(",");
        }
        top.ymPrompt.confirmInfo({
            title: '<spring:message code="delete.feedback.list.title"/>',
            message: '<spring:message code="delete.feedback.list.message"/>',
            closeTxt: '<spring:message code="common.close"/>',
            handler: function (tp) {
                if (tp == "ok") {
                    $.ajax({
                        type: "POST",
                        url: "${ctx}/feedback/deleteList",
                        data: {ids: ids, "token": "${token}"},
                        error: function (request) {
                            top.handlePrompt("error", '<spring:message code="delete.feedback.list.delFail"/>');
                        },
                        success: function (data) {
                            if (data.length != 0) {
                                top.document.getElementById("navUserFeedBack").click();
                            }
                            top.handlePrompt("success", '<spring:message code="delete.feedback.list.delSuccess"/>');
                            changeAuthType();
                        }
                    });
                }
            },
            btn: [['<spring:message code="button.ok"/>', "ok"], ['<spring:message code="button.cancel"/>', "cancel"]]
        });
    }


    function submitFeedBack() {
        if (!$("#creatFeedBackForm").valid()) {
            $('label[class=error]').css("padding-left", "17px") == $('label[class=error]').css("color", "red");
            $("#problemDescription").css("background-color", "#ffe7de");
            return false;
        }
        $("#problemDescription").css("background-color", "white");
        $.ajax({
            type: "POST",
            url: "${ctx}/feedback/createFeedBack",
            data: $("#creatFeedBackForm").serialize(),
            error: function (request) {

                handlePrompt("error", "<spring:message code='feedback.create.fail'/>");

            },
            success: function (data) {
                if (data.length != 0) {
                    top.document.getElementById("navUserFeedBack").click();
                }
                top.ymPrompt.close();
                top.handlePrompt("success", "<spring:message code='feedback.create.success'/>");
                $("#problemTitle").val("");
                $("#problemDescription").val("");
                $("#newTeamBtn").disabled = true;
                changeAuthType();
                //top.document.getElementById("navUserFeedBack").click();
            }
        });
    }


    $('#feedbackTitle').bind('keyup', function (event) {
        if (event.keyCode == "13") {
            //回车执行查询
            changeAuthType();
            //syncFun();
        }
    });


</script>

</body>
</html>