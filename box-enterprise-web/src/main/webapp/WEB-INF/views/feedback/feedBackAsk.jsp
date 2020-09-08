<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cse" uri="http://cse.huawei.com/custom-function-taglib" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="../common/common.jsp" %>
    <script src="${ctx}/static/js/public/validate/jquery.validate.min.js" type="text/javascript"></script>
    <style type="text/css">
        .dividerFeedBack {
            margin: 5px 0px;
            height: 1px;
            overflow: hidden;
            background: #e0e0e0;
            width: 95%;
        }

        .pop-content .detailBody {
            float: left;
            width: 100%;
        }

        .divFloatLeft {
            float: left;
        }

        .divFloatRight {
            float: right;
        }

        .customerFeedBack {
            padding-right: 10px;
        }
    </style>
    <%@ include file="../common/messages.jsp" %>
</head>
<body>
<div class="pop-content">

    <div class="detailBody">

        <form class="form-horizontal" id="updateFeedBackForm" name="updateFeedBackForm">
            <div style="word-wrap:break-word;word-break:break-all">
                <b><spring:message code="feedback.detail.title"/>：</b>${cse:htmlEscape(feedBackDetail.problemTitle)}
            </div>
            <div>
                <b><spring:message
                        code="feedback.detail.type"/>：</b>${cse:htmlEscape(feedBackDetail.problemType eq '1' ? '故障反馈':'意见建议')}
            </div>
            <div style="word-wrap:break-word;word-break:break-all">
                <b><spring:message
                        code="feedback.detail.description"/>：</b>${cse:htmlEscape(feedBackDetail.problemDescription)}
            </div>
            <div class="divFloatRight">
                <span class="customerFeedBack">${cse:htmlEscape(feedBackDetail.customerName)}</span>
                <span><fmt:formatDate value="${feedBackDetail.customerTwTime}" pattern="yyyy-MM-dd HH:mm:ss"/></span>
            </div>
            <li class="dividerFeedBack"></li>


            <c:forEach items="${restFeedBackSubList}" var="feedbackSub">

                <c:if test="${cse:htmlEscape(feedbackSub.isAnswer eq '1' ? true:false)}">
                    <div style="padding-top: 40px;word-wrap:break-word;word-break:break-all">
                        <b><spring:message
                                code="feedback.detail.answer"/>：</b>${cse:htmlEscape(feedbackSub.description)}
                    </div>
                    <div class="divFloatRight">
                        <span class="customerFeedBack">${cse:htmlEscape(feedbackSub.userName)}</span>
                        <span><fmt:formatDate value="${feedbackSub.updateTime}" pattern="yyyy-MM-dd HH:mm:ss"/></span>
                    </div>

                    <li class="dividerFeedBack"></li>

                </c:if>

                <c:if test="${cse:htmlEscape(feedbackSub.isAnswer eq '0' ? true:false)}">
                    <div style="padding-top: 40px;word-wrap:break-word;word-break:break-all">
                        <b><spring:message
                                code="feedback.list.title.ask"/>：</b>${cse:htmlEscape(feedbackSub.description)}
                    </div>
                    <div class="divFloatRight">
                        <span class="customerFeedBack">${cse:htmlEscape(feedbackSub.userName)}</span>
                        <span><fmt:formatDate value="${feedbackSub.updateTime}" pattern="yyyy-MM-dd HH:mm:ss"/></span>
                    </div>

                    <li class="dividerFeedBack"></li>
                </c:if>
            </c:forEach>


            <div style="float:left;">
                <div class="control-group">
                    <label class="control-label" for=""
                           style="float: left; width: 54px;padding-top: 5px;text-align: left;"><em>*</em><b><spring:message
                            code="feedback.list.title.ask"/>：</b></label>
                    <div class="controls" style="margin-left: 60px;">
                        <textarea id="problemDescription" name="problemDescription" cols="10" rows="8"
                                  style="margin: 0px; width: 512px; height: 160px;"></textarea>
                        <span class="validate-con bottom"><div></div></span>
                    </div>
                </div>
            </div>

            <input type="hidden" id="token" name="token" value="${token}"/>
            <input type="hidden" id="token" name="problemID" value="${feedBackDetail.problemID}"/>
        </form>

    </div>
</div>

<script type="text/javascript">

    $(document).ready(function () {
        $("#updateFeedBackForm").validate({
            rules: {
                problemDescription: {
                    required: true,
                    rangelength: [1, 1024]
                }
            },
            messages: {
                problemDescription: {
                    required: "<spring:message code="feedback.create.problemDescription.required" />",
                    rangelength: "<spring:message code="feedback.create.problemDescription.rangelength" />"
                }
            }
        });
        $("label").tooltip({container: "body", placement: "top", delay: {show: 100, hide: 0}, animation: false});
    });

    function submitModifyFeedBack() {
        if (!$("#updateFeedBackForm").valid()) {
            return false;
        }
        $.ajax({
            type: "POST",
            url: "${ctx}/feedback/modify",
            data: $('#updateFeedBackForm').serialize(),
            error: function (request) {
                handlePrompt("error", '<spring:message code="feedback.answer.fail"/>');
            },
            success: function () {
                top.ymPrompt.close();
                top.handlePrompt("success", '<spring:message code="feedback.answer.success"/>');
                top.changeAuthType();
            }
        });
        top.document.getElementById("btnModify").disabled = true;
    }

</script>
</body>
</html>
