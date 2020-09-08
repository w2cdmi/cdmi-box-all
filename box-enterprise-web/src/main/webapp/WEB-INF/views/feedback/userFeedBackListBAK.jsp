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
    <script src="${ctx}/static/js/public/JQbox-hw-files.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/JQbox-hw-page.js" type="text/javascript"></script>
    <script src="${ctx}/static/jqueryUI-1.9.2/jquery-ui.min.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/JQbox-hw-page.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
</head>
<body>
<%@ include file="../common/header.jsp" %>
<div class="userFeedBackBody">
    <div class="userFeedBackContent">

        <!--提问开始 -->
        <div class="sys-content" style="padding: 10px 0 0px 15px;">
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
                        <label class="control-label" for="" style="width: 8%;margin-right: 10px;"><spring:message
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

        <!-- 意见凤反馈查询开始 -->
        <form action="${ctx}/feedback/list" method="post" id="searchForm" name="searchForm">
            <input type="hidden" id="page" name="page" value="1">
            <input type="hidden" id="token" name="token" value="${cse:htmlEscape(token)}"/>
            <input type="hidden" id="customerID" name="customerID" value="${cse:htmlEscape(condition.customerID)}"/>
            <div class="sys-content">
                <div class="form-horizontal form-con clearfix">

                    <div class="control-group" style="width:25%;float: left;">
                        <label class="control-label" for="input" style="width:20%;margin-right: 10px;"><spring:message
                                code="feedback.list.condtion.problemStatus"/></label>
                        <div class="controls" style="margin-left:50px;width:70%;">
                            <select style="width:80%;" id="feedbackStatus" name="problemStatus" onchange="syncFun();">

                                <option value=""><spring:message code="feedback.list.condition.status.all"/></option>
                                <option value="1"
                                        <c:if test="${condition.problemStatus == '1'}">selected="selected"</c:if>>
                                    <spring:message code="feedback.list.condtion.problemStatus.close"/></option>
                                <option value="0"
                                        <c:if test="${condition.problemStatus == '0'}">selected="selected"</c:if>>
                                    <spring:message code="feedback.list.condtion.problemStatus.open"/></option>
                                <%-- <c:forEach items="${statusTypeList}" var="statuFeedBack">
                                    <option value="${cse:htmlEscape(statuFeedBack.value)}" <c:if test="${condition.problemStatus == statuFeedBack.value}">selected="selected"</c:if>>${cse:htmlEscape(statuFeedBack.name)}</option>
                                </c:forEach> --%>

                            </select>
                        </div>
                    </div>

                    <div class="control-group" style="width:25%;float: left;">
                        <label class="control-label" for="input" style="width:20%;margin-right: 10px;"><spring:message
                                code="feedback.list.condtion.cycle"/></label>
                        <div class="controls" style="margin-left:50px;width:70%;">
                            <select style="width:80%;" id="feedbackCycle" name="cycle" onchange="doQuery();">
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
                        <%-- <label class="control-label" for="input" style="width: 20%;margin-right: 10px;"><spring:message code="feedback.list.condtion.problemTitle"/></label> --%>
                        <div class="controls" style="margin-left:0px; width: 100%;">
                            <input type="text" id="feedbackTitle" style="width: 100%;" name="problemTitle"
                                   value="${cse:htmlEscape(condition.problemTitle)}"/>
                        </div>
                    </div>

                    <div class="control-group" style="width:20%;float:left;">
                        <label for="input" class="control-label" style="width:15%;"></label>
                        <div class="controls" style="margin-left: 50px;width:80%;">
                            <button id="usersubmit_btn" type="button" class="btn btn-primary" onClick="doQuery()">
                                <spring:message code="feedback.list.button.query"/></button>
                            <button id="userreset_btn" type="button" class="btn btn-primary" onClick="resetCondition()">
                                <spring:message code="feedback.list.button.reset"/></button>
                        </div>
                    </div>

                    <%-- <div class="control-group" style="width:100%;float:left;">
                        <div class="controls" style="margin-left: 0px;width:80%;">
                           <button type="button" class="btn btn-primary" onClick="deletelistFeedBack()"><spring:message code="feedback.list.title.batchDelete"/></button>
                       </div>
                   </div> --%>

                </div>
                <div class="control-group" style="width:100%;float:left;">
                    <div class="controls" style="margin-left: 0px;width:80%;">
                        <button type="button" class="btn btn-primary" onClick="deletelistFeedBack()"><spring:message
                                code="feedback.list.title.batchDelete"/></button>
                    </div>
                </div>
                <!-- 意见反馈列表开始-->
                <div class="table-con" style="min-height: 620px;" id="flushDIV">
                    <table class="table table-bordered table-striped" style="table-layout: fixed;">
                        <thead>
                        <tr>
                            <th style="width:4%"><input type="checkbox" id="checkall" name="checkall"/></th>
                            <th width="5%"><spring:message code="feedback.list.result.number"/></th>
                            <th width="18%"><spring:message code="feedback.list.result.answerTime"/></th>
                            <th width="22%"><spring:message code="feedback.list.result.problemTitle"/></th>
                            <th width="10%"><spring:message code="feedback.list.result.problemType"/></th>
                            <th width="18%"><spring:message code="feedback.list.result.customerTwTime"/></th>
                            <th width="8%"><spring:message code="feedback.list.result.problemStatus"/></th>
                            <th width="15%"><spring:message code="feedback.list.result.operation"/></th>
                        </tr>
                        </thead>
                        <tbody id="feedbackTBody">

                        <c:forEach items="${userFeedBackList.content}" var="feedback" varStatus="status">
                            <tr>
                                <td><input type="checkbox" id="${feedback.problemID}" name="checkname"
                                           value="${feedback.problemID}"/></td>
                                <td>${cse:htmlEscape(status.index + 1)}</td>

                                <td><fmt:formatDate value="${feedback.managerAnswerTime}"
                                                    pattern="yyyy-MM-dd HH:mm:ss"/></td>


                                <td style="white-space: nowrap;overflow: hidden;text-overflow: ellipsis;"
                                    title="${cse:htmlEscape(feedback.problemTitle)}">${cse:htmlEscape(feedback.problemTitle)}</td>

                                <td title="${cse:htmlEscape(feedback.problemType eq '1' ? '故障反馈':'意见建议')}">
                                        ${cse:htmlEscape(feedback.problemType eq '1' ? '故障反馈':'意见建议')}
                                </td>

                                <td><fmt:formatDate value="${feedback.newestTwTime}"
                                                    pattern="yyyy-MM-dd HH:mm:ss"/></td>

                                <td title="${cse:htmlEscape(feedback.problemStatus eq '0' ? '打开':'关闭')}">
                                        ${cse:htmlEscape(feedback.problemStatus eq '0' ? '打开':'关闭')}
                                </td>

                                <td>
                                    <a href="#"
                                       onClick="showFeedBack(${cse:htmlEscape(feedback.problemID)})"><spring:message
                                            code="feedback.list.title.query"/></a>|
                                    <a href="#"
                                       onClick="deleteFeedBack(${cse:htmlEscape(feedback.problemID)})"><spring:message
                                            code="feedback.list.title.delete"/></a>

                                    <c:if test="${cse:htmlEscape(feedback.problemStatus eq '1')}">
                                        |<a href="#" onClick="modifyFeedBack(${cse:htmlEscape(feedback.problemID)})"><spring:message
                                            code="feedback.list.title.ask"/></a>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
                <!-- 意见反馈列表结束-->
                <!-- 意见反馈分页 -->
                <div id="feeebackPage"></div>

            </div>
        </form>
        <!-- 意见凤反馈查询结束-->
    </div>
</div>

<%@ include file="../common/footer.jsp" %>
<script type="text/javascript">
    function syncFun() {

        var problemStatus = $("#feedbackStatus").val();
        var cycle = $("#feedbackCycle").val();
        var problemTitle = $("#feedbackTitle").val();

        $('#flushDIV').html("");
        $.ajax({
            type: "POST",
            cache: false,
            data: {
                "problemStatus": problemStatus,
                "cycle": cycle,
                "problemTitle": problemTitle,
                "page": '${cse:htmlEscape(userFeedBackList.number)}',
                "token": '${cse:htmlEscape(token)}'
            },
            url: "${ctx}/feedback/list",
            success: function (data) {

                $('#flushDIV').html(data);
            },
            error: function (error) {
                alert(error);
            }

        });
    }

    $(document).ready(function () {
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
            }
        });

        $("#messageAddr").keydown(
            function (event) {
                if (event.keyCode == 13) {
                    searchMessageTo();
                    return false;
                }
            });

        $("label").tooltip({container: "body", placement: "top", delay: {show: 100, hide: 0}, animation: false});


        //初始化分页
        $("#feeebackPage").comboPage({
            curPage: ${cse:htmlEscape(userFeedBackList.number)},
            lang: '<spring:message code="common.language1"/>',
            perDis: ${cse:htmlEscape(userFeedBackList.size)},
            totaldata: ${userFeedBackList.totalElements},
            style: "page table-page"
        })
        var pageH = $("body").outerHeight();
        if (pageH < 500) {
            pageH = 500;
        }


        /* top.iframeAdaptHeight(pageH);
         jQuery(function($){
         var encode='
        <spring:message code="common.language1"/>';
         if(encode=='zh-cn' || encode=='zh'){
         $.datepicker.regional['zh-CN'] = {
         closeText: '关闭',
         prevText: '&#x3c;上月',
         nextText: '下月&#x3e;',
         currentText: '今天',
         monthNames: ['一月','二月','三月','四月','五月','六月',
         '七月','八月','九月','十月','十一月','十二月'],
         monthNamesShort: ['一','二','三','四','五','六',
         '七','八','九','十','十一','十二'],
         dayNames: ['星期日','星期一','星期二','星期三','星期四','星期五','星期六'],
         dayNamesShort: ['周日','周一','周二','周三','周四','周五','周六'],
         dayNamesMin: ['日','一','二','三','四','五','六'],
         weekHeader: '周',
         dateFormat: 'yy-mm-dd',
         firstDay: 1,
         isRTL: false,
         showMonthAfterYear: true,
         yearSuffix: '年'};
         $.datepicker.setDefaults($.datepicker.regional['zh-CN']);
         }
         });

         $("#twBeginTimeComp").datepicker();
         $("#twEndTimeComp").datepicker();
         $("#dfBeginTimeComp").datepicker();
         $("#dfEndTimeComp").datepicker();



         $("#dfBeginTimeComp").datepicker("option", "dateFormat","yy-mm-dd");
         $("#dfEndTimeComp").datepicker("option", "dateFormat","yy-mm-dd");
         $("#twBeginTimeComp").datepicker("option", "dateFormat","yy-mm-dd");
         $("#twEndTimeComp").datepicker("option", "dateFormat","yy-mm-dd");

         $("#dfBeginTimeComp").datepicker('setDate','
        <fmt:formatDate value="${condition.dfBeginTime}" pattern="yyyy-MM-dd"/>');
         $("#dfEndTimeComp").datepicker('setDate','
        <fmt:formatDate value="${condition.dfEndTime}" pattern="yyyy-MM-dd"/>');
         $("#twBeginTimeComp").datepicker('setDate','
        <fmt:formatDate value="${condition.twBeginTime}" pattern="yyyy-MM-dd"/>');
         $("#twEndTimeComp").datepicker('setDate','
        <fmt:formatDate value="${condition.twEndTime}" pattern="yyyy-MM-dd"/>'); */

    });

    $.fn.comboPage.pageSkip = function (opts, _idMap, curPage) {
        $("#page").val(curPage);
        doQuery();
    };


    function doQuery() {
        var dfBeginTime = $("#dfBeginTimeComp").val();
        var dfEndTime = $("#dfEndTimeComp").val();

        var twBeginTime = $("#twBeginTimeComp").val();
        var twEndTime = $("#twEndTimeComp").val();


        if (dfEndTime != "" && dfBeginTime > dfEndTime) {
            handlePrompt("error", '<spring:message code="log.handle.time.error"/>', null, 60);
            return;
        }

        if (twEndTime != "" && twBeginTime > twEndTime) {
            handlePrompt("error", '<spring:message code="log.handle.time.error"/>', null, 60);
            return;
        }


        if (dfBeginTime != "") {
            $("#dfBeginTime").attr('name="dfBeginTime"');
            $("#dfBeginTime").val(dfBeginTime + " 00:00:00");
        } else {
            $("#dfBeginTime").removeAttr("name");
        }
        if (dfEndTime != "") {
            $("#dfEndTime").attr('name="dfBeginTime"');
            $("#dfEndTime").val(dfEndTime + " 23:59:59");
        } else {
            $("#dfEndTime").val(null);
            $("#dfEndTime").removeAttr("name");
        }


        if (twBeginTime != "") {
            $("#twBeginTime").attr('name="twBeginTime"');
            $("#twBeginTime").val(twBeginTime + " 00:00:00");
        } else {
            $("#twBeginTime").removeAttr("name");
        }
        if (twEndTime != "") {
            $("#twEndTime").attr('name="twBeginTime"');
            $("#twEndTime").val(twEndTime + " 23:59:59");
        } else {
            $("#twEndTime").val(null);
            $("#twEndTime").removeAttr("name");
        }


        $("#searchForm").submit();
        var pageH = $("body").outerHeight();
        if (pageH < 500) {
            pageH = 500;
        }
        top.iframeAdaptHeight(pageH);
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
            top.ymPrompt.getPage().contentWindow.submitModifyFeedBack();
        } else {
            top.ymPrompt.close();
        }
    }


    function showFeedBack(problemID) {
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
                        success: function () {
                            top.handlePrompt("success", '<spring:message code="feedback.list.deleteSuccess"/>');
                            top.document.getElementById("navUserFeedBack").click();
                            refreshWindow();
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
        var ids = '';
        $("input[name='checkname']:checked").each(function () {
            if (ids != '') {
                ids = ids + "," + this.value;
            } else {
                ids = this.value;
            }
        });
        if (ids == '') {
            handlePrompt("error", '<spring:message code="delete.feedback.list.err"/>');
            return;
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
                        success: function () {
                            top.handlePrompt("success", '<spring:message code="delete.feedback.list.delSuccess"/>');
                            top.document.getElementById("navUserFeedBack").click();
                            refreshWindow();
                        }
                    });
                }
            },
            btn: [['<spring:message code="button.ok"/>', "ok"], ['<spring:message code="button.cancel"/>', "cancel"]]
        });
    }


    function submitFeedBack() {

        if (!$("#creatFeedBackForm").valid()) {
            return false;
        }
        $.ajax({
            type: "POST",
            url: "${ctx}/feedback/createFeedBack",
            data: $("#creatFeedBackForm").serialize(),
            error: function (request) {

                handlePrompt("error", "<spring:message code='feedback.create.fail'/>");

            },
            success: function () {
                top.ymPrompt.close();
                top.handlePrompt("success", "<spring:message code='feedback.create.success'/>");
                top.document.getElementById("navUserFeedBack").click();
            }
        });
    }


    $('#feedbackTitle').bind('keyup', function (event) {
        if (event.keyCode == "13") {
            //回车执行查询
            doQuery();
            //syncFun();
        }
    });


</script>

</body>
</html>