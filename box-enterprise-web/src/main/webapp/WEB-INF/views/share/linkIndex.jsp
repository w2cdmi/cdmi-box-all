<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="../common/common.jsp" %>

    <link href="${ctx}/static/autocomplete/themes/base/jquery.ui.all.css" rel="stylesheet" type="text/css">
    <script src="${ctx}/static/js/public/validate/jquery.validate.min.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/validate/messages_bs_zh.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/public/My97DatePicker/link_WdatePicker.js" type="text/javascript"></script>
    <script src="${ctx}/static/autocomplete/ui/jquery.ui.core.js" type="text/javascript"></script>
    <script src="${ctx}/static/autocomplete/ui/jquery.ui.widget.js" type="text/javascript"></script>
    <script src="${ctx}/static/autocomplete/ui/jquery.ui.position.js" type="text/javascript"></script>
    <script src="${ctx}/static/autocomplete/ui/jquery.ui.menu.js" type="text/javascript"></script>
    <script src="${ctx}/static/autocomplete/ui/jquery.ui.autocomplete.js" type="text/javascript"></script>
    <script src="${ctx}/static/clipboard/ZeroClipboard.js" type="text/javascript"></script>
    <script src="${ctx}/static/js/organization.js" type="text/javascript"></script>
</head>
<body>
<div class="pop-content">
    <input type="hidden" value="<c:out value='${mailmsg}'/>" id="mailMsg"/>
    <div class="description"><spring:message code='link.description'/></div>
    <form id="setLinkForm" class="pop-share-link">
        <span id="enterTempData" style="display:none;"></span>
        <div class="file-name clearfix" id="linkFileName"><span class="file-icon" id="fileOrFolderType"></span> <span
                class="name-txt" title="<c:out value='${name}'/>"><c:out value='${name}'/></span></div>
        <div class="file-link-con">
            <div id="linksList">
            </div>
            <div class="add-link">
                <button onclick="setLink()" type="button" class="btn btn-primary"><spring:message
                        code='link.button.create'/></button>
                <span class="help-inline"><spring:message code='link.title.tips'/></span>
            </div>
            <div id="manageBtnCon" class="btn-con">
                <button id="cancelLink-button" type="button" onclick="deleteAllLink()" class="btn">
                    <c:if test="${linkStatus == true}"><spring:message code="link.set.cancelLink"/></c:if>
                    <c:if test="${linkStatus == false}"><spring:message code="button.discardShare"/></c:if>
                </button>
                <button id="shareLinkDone" type="button" class="btn btn-primary" onclick="top.linkHandle()">
                    <spring:message code='button.complete'/></button>
            </div>
        </div>
        <div class="link-access">
            <div class="file-link">
                <div class="input-append">
                    <input type="text" id="urlAccess" name="urlAccess" readonly="readonly"/>
                </div>
            </div>
            <div class="form-horizontal label-w140">
                <div class="control-group">
                    <label class="control-label"><spring:message code='link.set.linkACL'/></label>
                    <div class="controls">
                        <label class="checkbox inline"><input type="checkbox" id="upload" name="upload"/>
                            <spring:message code='link.label.upload'/></label>
                        <label class="checkbox inline"><input type="checkbox" id="download"
                                                              name="download"/><spring:message
                                code='link.label.download'/></label>
                        <label class="checkbox inline"><input type="checkbox" id="preview" name="preview"/>
                            <spring:message code='link.label.scan'/></label>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label"><spring:message code="link.accessCode"/></label>
                    <div class="controls">
                        <input type="hidden" id="accessCodeMode" name="accessCodeMode" value="static"/>
                        <label class="checkbox inline"><input type="checkbox" id="sCode"/> <spring:message
                                code='link.label.start'/></label>
                        <label class="checkbox inline" style="display:none"><input type="checkbox" id="dCode"/>
                            <spring:message code='link.label.use.code'/></label>
                        <div class="form" id="sCodeDiv" style="display: none">
                            <span class="help-block"><spring:message code='link.label.input.code'/></span>
                            <div class="input-append">
                                <input class="span3" type="text" id="accessCode" name="accessCode" readonly="readonly"
                                       maxlength="20"/>
                                <button id="freshAcessCode" type="button" onclick="refreshLinkAccessCode()"
                                        class="btn" title="<spring:message code='link.set.refrechAccessCode'/>"><i
                                        class="icon-refresh"></i></button>
                            </div>
                        </div>
                        <div class="form" id="dCodeDiv" style="display: none">
                            <span class="help-block"><spring:message code='link.label.input.email'/></span>
                            <div class="input-append">
                                <input class="span3" type="text" id="identities" name="identities" maxlength="255"/>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label"><spring:message code="link.set.timeDure"/></label>
                    <div class="controls">
                        <label class="checkbox inline"><input id="dateRadioCustom" type="checkbox" name="dateRadio"/>
                            <spring:message code='link.label.start'/></label>
                        <input type="hidden" id="timeZone" name="timeZone"/>
                        <input type="hidden" id="effectiveAt" name="effectiveAt"/>
                        <input type="hidden" id="expireAt" name="expireAt"/>
                        <div class="form" id="dateDiv" style="display: none">
                            <input class="Wdate span2" readonly="readonly" type="text" id="effectiveAtTime"
                                   onClick="WdatePicker({lang:'<spring:message code="common.language1"/>',dateFmt:'
                                   <spring:message
                                           code="common.dateformat"/> HH:mm',minDate:'%y-%M-{%d}',vel:'effectiveAt'})">
                            <spring:message code="link.set.timeTo"/>
                            <input class="Wdate span2" readonly="readonly" type="text" id="expireAtTime"
                                   onClick="WdatePicker({lang:'<spring:message code="common.language1"/>',dateFmt:'
                                   <spring:message
                                           code="common.dateformat"/> HH:mm',minDate:'%y-%M-{%d}',vel:'expireAt'})">
                        </div>
                    </div>
                </div>

            </div>
            <div class="btn-con">
                <button id="saveAccess" type="button" class="btn btn-primary"><spring:message
                        code="link.button.saveLink"/></button>
                <button id="cancelAccess" type="button" class="btn"><spring:message code="button.cancel"/></button>
            </div>
        </div>
        <div id="linkEmail" class="link-email">
            <div class="file-link">
                <div class="input-append">
                    <input type="text" id="urlEmail" name="urlEmail" readonly="readonly" style="height:30px"/>
                </div>
                <div id="linkEmailInfo"></div>
            </div>
            <h5><spring:message code='link.title.send.email'/></h5>
            <div id="sendLinkEmail" class="user-form" style="width:548px">
                <textarea maxlength=2000 id="emailUrl" onpaste="userOnPaste()" onkeyup='doMaxLength(this)'></textarea>
                <div class="prompt"><spring:message code="link.msg.addRecipients"/></div>
                <div class="enterPrompt"><spring:message code="link.set.addMailInfo"/></div>
                <c:if test="${isDepartment == true}">
                    <button class="btn btn-mini btn-link" style="float:right" type="button" onclick="addOrganization()">
                        <spring:message code='organization.user.add'/></button>
                </c:if>
            </div>
            <div class="search-loading">
                <div id="loadingDiv" class="loading-div"></div>
            </div>
            <div class="share-message"><textarea id="messageText" maxlength=2000
                                                 placeholder="<spring:message code='inviteShare.addMessage'/>"
                                                 onkeyup='doMaxLength(this)'></textarea></div>
            <div class="btn-con">
                <button id="sendEmail" type="button" class="btn btn-primary"><spring:message
                        code="link.button.sendLink"/></button>
                <button id="cancelEmail" type="button" class="btn" onclick="cancelSendEmail()"><spring:message
                        code="button.cancel"/></button>
            </div>
        </div>
        <input type="hidden" id="token" name="token" value="<c:out value='${token}'/>"/>
    </form>
    <div class="bgPop"></div>

    <div class="pop">
        <ul id="menuTree" class="ztree" style="margin-top:0;height: 80%;overflow:auto;">
        </ul>
        <input type="button" style="margin-left: 150px;" value="<spring:message code="link.task.title"/>"
               class="btn btn-primary" onclick="addDepartmentToUrl();"/>
        <input type="button" style="margin-left: 80px;" value="<spring:message code="common.cancle"/>" class="btn"
               onclick="closeDiv();"/>
    </div>
</div>
<script type="text/javascript">
    ctx = '${ctx}';
    token = "<c:out value='${token}'/>";

    function showNodeTree() {
        var params = {
            "token": token
        };
        $.ajax({
            data: params,
            url: ctx + '/teamspace/member/showDept',
            type: 'GET',
            async: false,
            success: function (msg) {
                var obj = eval("(" + msg + ")");
                $.fn.zTree.init($("#menuTree"), deviceTypeSetting, obj);
            },
            error: function () {
                alert(1);
            }
        });
    }

    function addDepartmentToUrl() {
        deleteDepartmentUrl();
        if (!allDepartmentToMap.isEmpty()) {
            allDepartmentToMap.each(function (key, value, index) {
                var pNode = allDepartmentToMap.get(value[5]);
                if (value[4] == "department") {
                    addMessageTo(value[3], value[2], value[4], value[1]);
                } else if (value[4] == "user") {
                    addMessageTo(value[3], null, null, value[1]);
                }
            });
        }
        oldMessage = [];
        $(".enterPrompt").hide();
        $('.bgPop,.pop').hide();
    }

    var allDepartmentToMap = new Map();
    var oldMessage = new Array();
    var submitUsername = null;
    var tempUsername = null;
    var allMessageTo = new Array();
    var opts_viewGrid = null;
    var ownerId = "<c:out value='${ownerId}'/>";
    var listViewType = "list";
    var iNodeId = "<c:out value='${folderId}'/>";
    var linkStatus = "<c:out value='${linkStatus}'/>";
    var isComplexCode = "<c:out value='${isComplexCode}'/>";
    var curPage = 0;
    var accessCodeLen = 8;
    var isMailPatternError = false;
    var mailMsg = "";
    var linksData = null;
    var currentEditLink = null;
    var copyUrlMap = new clipMap();

    $(document).ready(function () {
        mailMsg = $("#mailMsg").val();
        $("#freshAcessCode, #copyLinkButtonCon").tooltip({
            container: "body",
            placement: "top",
            delay: {show: 100, hide: 0},
            animation: false
        });
        var objType = '<c:out value="${type}"/>';
        if (objType == '0') {
            $("#fileOrFolderType").addClass("fileImg-folder");
        } else if (objType == '-3') {
            $("#fileOrFolderType").addClass("fileImg-folder-computer");
        } else if (objType == '-2') {
            $("#fileOrFolderType").addClass("fileImg-folder-disk");
        } else {
            var name = '<c:out value="${name}"/>';
            var type = _getStandardType(name);
            if (type == "img") {
                var thumUrl = "url('${thumbnailUrl}') no-repeat center center";
                $("#fileOrFolderType").css("background", thumUrl);
            } else {
                $("#fileOrFolderType").addClass("fileImg-" + type);
            }
        }

        if (isComplexCode == "true") {
            $("#accessCode").attr("readonly", "readonly");
        }
        else {
            $("#accessCode").removeAttr("readonly");
        }

        $("#timeZone").val(getTimeZone());

        $("#sCode").bind("click", function () {
            if ($("#sCode").is(":checked")) {
                $("#accessCodeMode").val("static");
                if (isComplexCode == "true") {
                    refreshLinkAccessCode();
                }
                $("#dCodeDiv").hide();
                $("#sCodeDiv").show();
                $("#dCode").attr("checked", false).parent().show();
                if (isComplexCode == "false" && $("#freshAcessCode").get(0)) {
                    $("#freshAcessCode").remove();
                }
            } else {
                $("#sCodeDiv").hide();
                $("#dCodeDiv").hide();
                $("#dCode").attr("checked", false).parent().hide();
            }

            var conH = parseInt($(".pop-content").outerHeight() + 90);
            top.ymPrompt.resizeWin(650, conH);
        });

        $("#dCode").bind("click", function () {
            if ($("#dCode").is(":checked")) {
                $("#sCodeDiv").hide();
                $("#dCodeDiv").show();
                $("#accessCodeMode").val("mail");
            } else {
                $("#sCodeDiv").show();
                $("#dCodeDiv").hide();
            }

            var conH = parseInt($(".pop-content").outerHeight() + 90);
            top.ymPrompt.resizeWin(650, conH);
        });

        $("#dateRadioCustom").bind("click", function () {
            if ($("#dateRadioCustom").is(":checked")) {
                $("#dateDiv").show();
            } else {
                $("#dateDiv").hide();
            }

            var conH = parseInt($(".pop-content").outerHeight() + 90);
            top.ymPrompt.resizeWin(650, conH);
        });

        /* $("#upload").bind("click", function () {
         if($("#upload").is(":checked")){
         $("#preview").get(0).checked = true;
         }
         }); */

        /* $("#upload").bind("click", function () {
         if($("#preview").is(":checked")){
         $("#download").get(0).checked = true;
         }
         }); */

        $("#download").bind("click", function () {
            if ($("#download").is(":checked")) {
                $("#preview").get(0).checked = true;
            }
            /* else{
             if($("#upload").is(":checked")){
             $("#preview").get(0).checked = false;
             }
             } */
        });
        $("#preview").bind("click", function () {
            if ($("#preview").is(":checked")) {
                /* if($("#upload").is(":checked")){
                 $("#download").get(0).checked = true;
                 } */
            }
            else {
                $("#download").get(0).checked = false;
            }
        });
        top.ymPrompt.resizeWin(650, 350);
        initLink();
        initMailDIV();

    });

    function initMailDIV() {
        allMessageTo = new Array();

        $("#emailUrl").keydown(function (event) {
            if (event.keyCode == 13) {
                var tempEmail = $("#emailUrl").val();
                searchMessageTo(tempEmail);
                if (window.event) {
                    window.event.cancelBubble = true;
                    window.event.returnValue = false;
                } else {
                    event.stopPropagation();
                    event.preventDefault();
                }
            } else if (event.keyCode == 8) {

                if ($(this).val() == "") {
                    var nodeId = $(this).parent().find("div.invite-member:last > a.close").attr("id")
                    $(this).parent().find("div.invite-member:last").remove();
                    var size = allDepartmentToMap.size();
                    allDepartmentToMap.remove(nodeId);
                    var size2 = allDepartmentToMap.size();
                    if (size == size2) {
                        allDepartmentToMap.remove("department" + nodeId);
                    }
                    allMessageTo.pop();
                    var conH = parseInt($(".pop-content").outerHeight() + 90);
                    top.ymPrompt.resizeWin(650, conH);
                }
            } else if (event.keyCode != 38 && event.keyCode != 40) {
                availableTags = [];
            }
            $(".enterPrompt").hide();
        });

        $("#emailUrl").keyup(function (event) {
            submitUsername = $("#emailUrl").val();
            if (tempUsername != submitUsername) {
                try {
                    $("#emailUrl").autocomplete("close");
                } catch (e) {
                }
            }
            userInputAutoSize(this);
        })

        $("#sendLinkEmail").click(function () {
            $("#emailUrl").focus();
        });

        $("#messageText").focus(function () {
            if (mailMsg != null && mailMsg != "" && $("#messageText").val() == "") {
                $("#messageText").val(mailMsg).select();
            }
        });

        $("#emailUrl").focus(function () {
            $(".prompt").hide();
            if ($(this).val() == '' && allMessageTo.length < 1) {
                $(".enterPrompt").show();
            }
            var conH = parseInt($(".pop-content").outerHeight() + 90);
            top.ymPrompt.resizeWin(650, conH);
        }).blur(function () {
            if ($(this).val() == '' && allMessageTo.length < 1) {
                $(".prompt").show();
                $(".enterPrompt").hide();
            }
        })
    }

    function userInputAutoSize(that) {
        var tempObj = $("#enterTempData"),
            _obj = $(that).parent().find("div.invite-member:last"),
            posCon = $("#sendLinkEmail").offset().left + 5,
            posInput = _obj.get(0) ? (_obj.offset().left + _obj.outerWidth() + 5) : posCon,
            userConW = 515,
            tempW = 0,
            space = userConW - parseInt(posInput - posCon),
            thatParent = $(that).parent().get(0);

        var tempValue = $(that).val().replace(new RegExp(" ", "g"), "&nbsp;");
        tempValue = tempValue.replace(new RegExp("<", "g"), "&lt;");
        tempObj.html(tempValue);
        tempW = tempObj.width();
        if ((tempW + 5) > space || $(that).get(0).scrollHeight > 20) {
            $(that).css({"width": userConW});
            $(that).height(0);
            $(that).css({"height": $(that).get(0).scrollHeight});
            thatParent.scrollTop = thatParent.scrollHeight;
        } else {
            $(that).css({"width": space, "height": 20});
        }

        var conH = parseInt($(".pop-content").outerHeight() + 90);
        top.ymPrompt.resizeWin(650, conH);
    }

    function deleteAllLink() {
        var params = {
            "token": "<c:out value='${token}'/>"
        };
        ymPrompt.confirmInfo({
            title: "<spring:message code='link.set.cancelLink'/>",
            maskAlphaColor: "gray",
            message: '<spring:message code="link.set.delLinkConfirm"/>',
            handler: function (tp) {
                if (tp == 'ok') {
                    $.ajax({
                        type: "POST",
                        data: params,
                        url: "${ctx}/share/deleteLink/<c:out value='${ownerId}'/>/<c:out value='${folderId}'/>/all",
                        error: function (request) {
                            handlePrompt("error", '<spring:message code="link.set.delLinkFail"/>', '', '5');
                        },
                        success: function () {
                            top.handlePrompt("success", '<spring:message code="operation.success"/>');
                            parent.ymPrompt.close();
                            top.linkHandle();
                        }
                    });
                }
            }
        });
    }

    function deleteLink(linkId, linkLength) {
        var params = {
            "token": "<c:out value='${token}'/>"
        };
        ymPrompt.confirmInfo({
            title: "<spring:message code='link.set.cancelLink'/>",
            maskAlphaColor: "gray",
            message: '<spring:message code="link.set.delLinkConfirm"/>',
            handler: function (tp) {
                if (tp == 'ok') {
                    $.ajax({
                        type: "POST",
                        data: params,
                        url: "${ctx}/share/deleteLink/<c:out value='${ownerId}'/>/<c:out value='${folderId}'/>" + "?linkCode=" + linkId,
                        error: function (request) {
                            doDeleteLinkError(request);
                        },
                        success: function () {
                            if (linkLength > 1) {
                                handlePrompt("success", '<spring:message code="operation.success"/>');
                                getLink();
                            } else {
                                top.handlePrompt("success", '<spring:message code="operation.success"/>');
                                parent.ymPrompt.close();
                                top.linkHandle();
                            }
                        }
                    });
                }
            }
        });
    }


    function deleteLinkNoCheck(linkId, linkLength) {
        var params = {
            "token": "<c:out value='${token}'/>"
        };
        $.ajax({
            type: "POST",
            data: params,
            url: "${ctx}/share/deleteLink/<c:out value='${ownerId}'/>/<c:out value='${folderId}'/>" + "?linkCode=" + linkId,
            error: function (request) {
            },
            success: function () {
                currentEditLink = null;
                if (linkLength > 0) {
                    hideAclDiv();
                    getLink();
                } else {
                    parent.ymPrompt.close();
                    top.linkHandle();
                }
            }
        });
    }

    function callBackDelLink() {
        var params = {
            "token": "<c:out value='${token}'/>"
        };
        if (currentEditLink != null) {
            $.ajax({
                type: "POST",
                data: params,
                url: "${ctx}/share/deleteLink/<c:out value='${ownerId}'/>/<c:out value='${folderId}'/>" + "?linkCode=" + currentEditLink,
                error: function (request) {
                },
                success: function () {
                    currentEditLink = null;
                }
            });
        }

    }

    function initLink() {
        if (linkStatus == "false") {
            setLink();
        }
        else {
            getLink();
        }
    }

    function hideACLTotal() {
        $(".link-access").show();
        $(".link-email, .file-link-con").hide(0, function () {
            var conH = parseInt($(".pop-content").outerHeight() + 120);
            top.ymPrompt.resizeWin(650, conH);
        });
    }

    function clearValues() {
        $("#acessCodeInfo").text("");
        $("#listDateEffectiveAt").text("");
        $("#listDateExpireAt").text("");

        $("#accessCode").val("");
        $("#effectiveAt").val("");
        $("#expireAt").val("");
    }

    function getLink() {
        $.ajax({
            type: "GET",
            url: "${ctx}/share/getlink/" + ownerId + "/" + iNodeId + "/?" + new Date().getTime(),
            error: function (request) {
                doInitLinkError(request);
            },
            success: function (data) {
                ZeroClipboard.config({moviePath: '${ctx}/static/clipboard/ZeroClipboard.swf'});
                linksData = data;
                $("#linksList").html("");
                var clipObject, copyURL;
                for (var i = 0; i < data.length; i++) {
                    var uploadText = (data[i].upload == false) ? "" : "<spring:message code='link.label.upload'/>";
                    var downloadText = (data[i].download == false) ? "" : "<spring:message code='link.label.download'/>";
                    var previewText = (data[i].preview == false) ? "" : "<spring:message code='link.label.scan'/>";
                    var AccessCodeHtml = "";
                    if (data[i].accessCodeMode == "static") {
                        if (data[i].plainAccessCode != "" && data[i].plainAccessCode != undefined) {
                            AccessCodeHtml = '<span><spring:message code="link.accessCode"/> <em id="acessCodeInfo">' + data[i].plainAccessCode + '</em></span>';
                        }
                    } else {
                        AccessCodeHtml = '<span><spring:message code="link.accessCode"/> <em id="acessCodeInfo"><spring:message code="link.label.dynamic.code"/></em></span>';
                    }

                    var efAt = getFormatDate(data[i].effectiveAt, false);
                    var exAt = getFormatDate(data[i].expireAt, false);
                    var efAtZH = getFormatDate(data[i].effectiveAt, true);
                    var exAtZH = getFormatDate(data[i].expireAt, true);
                    var timeHtml = "";
                    if (data[i].effectiveAt != undefined) {
                        var dateEffectiveAt = efAt;
                        var dateExpireAt = '<spring:message code="link.set.timeForEver"/>';
                        if (data[i].expireAt != undefined) {
                            dateExpireAt = exAt;
                        }
                        timeHtml = '<span> <spring:message code="link.set.timeDure"/> <em>' + dateEffectiveAt + '</em> <spring:message code="link.set.timeTo"/> <em>' + dateExpireAt + '</em> </span>';
                    } else {

                    }

                    var linkHtml = '<div class="file-link">' +
                        '<div class="input-append">' +
                        '<input type="text" id="url' + i + '" value="' + data[i].url + '" readonly="readonly"/>' +
                        '<button onclick="showAclDiv(' + i + ')" type="button" class="btn" title="<spring:message code='button.modify'/>"><i class="icon-pencil"></i></button>' +
                        '<button id="copyBtnHidden_' + i + '" type="button" class="btn" style="display:none"><i class="icon-copy"></i></button>' +
                        '<button  id="copyButton_' + i + '" type="button" class="btn" title="<spring:message code='button.copy'/>"><i class="icon-copy"></i></button>' +
                        '<button onclick="showEmailSend(' + i + ')" type="button" class="btn"><i class="icon-email"></i></button>' +
                        '<button type="button" onclick="deleteLink(\'' + data[i].id + '\',' + data.length + ')" class="btn"><i class="icon-delete-blod"></i></button>' +
                        '</div>' +
                        '<div class="show-access" id ="linkInfo' + i + '">' +
                        '<span><spring:message code="link.set.linkACL"/> <em>' + uploadText + ' ' + downloadText + ' ' + previewText + '</em></span>' +
                        '</div>' +
                        '</div>';
                    $("#linksList").append(linkHtml);
                    $("#linkInfo" + i).append(AccessCodeHtml);
                    $("#linkInfo" + i).append(timeHtml);

                    //for IE
                    if (window.clipboardData) {
                        var clipObject = new ZeroClipboard(document.getElementById("copyBtnHidden_" + i));
                        copyUrlMap.put("copyButton_" + i, data[i].url);
                        var copyBtn = $("#copyButton_" + i);

                        copyBtn.on("click", function (evt) {
                            window.clipboardData.setData("text", copyUrlMap.get(evt.target.id));
                            handlePrompt("success", '<spring:message code="link.set.copySuccess" />');
                        });
                    } else {
                        var clipObject = new ZeroClipboard(document.getElementById("copyButton_" + i));
                        copyUrlMap.put(clipObject.id, data[i].url);
                        clipObject.on("mouseOver", function (clipObject) {
                            clipObject.setText(copyUrlMap.get(clipObject.id));
                        });
                        clipObject.on("complete", function () {
                            clipObject.setText(copyUrlMap.get(clipObject.id));
                            handlePrompt("success", '<spring:message code="link.set.copySuccess" />');
                        });
                    }
                }

                var conH = parseInt($(".pop-content").outerHeight() + 90);
                top.ymPrompt.resizeWin(650, conH);
                return;
            }
        });
    }


    function setLink() {
        var len = $('#linksList').children('div').length;
        if (len >= 3) {
            ymPrompt.alert({
                title: '<spring:message code="link.title.error"/>',
                message: '<spring:message code="link.error.createLink"/>'
            });
            return;
        }
        $("#urlAccess").val("");
        $("#download").attr("checked", true);
        $("#preview").get(0).checked = true;
        $("#upload").attr("checked", false);
        var objType = '<c:out value="${type}"/>';
        if (objType == 1) {
            $("#upload").parent().hide();
        }
        $("#sCode").attr("checked", false);
        $("#dCode").attr("checked", false);
        $("#sCodeDiv, #dCodeDiv").hide();
        $("#dCode").parent().hide();
        $("#identities, #accessCode").val("");
        $("#accessCodeMode").val("static");

        $("#effectiveAtTime,#expireAtTime, #effectiveAt, #expireAt").val("");
        $("#dateRadioCustom").attr("checked", false);
        $("#dateDiv").hide();

        $(".file-link-con, .link-email").hide();
        $(".link-access").show();
        var conH = parseInt($(".pop-content").outerHeight() + 90);
        top.ymPrompt.resizeWin(650, conH);

        $.ajax({
            type: "POST",
            url: "${ctx}/share/setlink/" + ownerId + "/" + iNodeId,
            data: $('#setLinkForm').serialize(),
            error: function (request) {
                currentEditLink = null;
                doInitLinkError(request);
                ymPrompt_disableModalbtn("#saveAccess");
                $("#cancelAccess").unbind();
                $("#cancelAccess").bind("click", function () {
                    parent.ymPrompt.close();
                });
            },
            success: function (data) {
                currentEditLink = data.id;
                $('#urlAccess').val(data.url);
                $("#saveAccess").unbind();
                $("#saveAccess").bind("click", function () {
                    updateLink(data.id);
                });
                $("#cancelAccess").unbind();
                $("#cancelAccess").bind("click", function () {
                    if (linksData == null) {
                        deleteLinkNoCheck(data.id, 0);
                    } else {
                        deleteLinkNoCheck(data.id, linksData.length);
                    }
                });
            }
        });
    }

    function updateLink(linkId) {

        if (!$("#download").is(":checked") && !$("#preview").is(":checked") && !$("#upload").is(":checked")) {
            handlePrompt("error", '<spring:message code="link.error.authority"/>', '', '5');
            return;
        }
        $("#accessCode").val($("#accessCode").val().trim());
        $("#accessCodeMode").val("static");
        if ($("#sCode").is(":checked") && !$("#dCode").is(":checked")) {
            var accessCode = $("#accessCode").val();
            $("#accessCodeMode").val("static");
            if (accessCode == null || accessCode.trim() == "") {
                handlePrompt("error", '<spring:message code="link.acesscode.notNull"/>', '', '5');
                return;
            }

            if (isComplexCode == "true") {
                if (accessCode.length < 8 || accessCode.length > 20) {
                    handlePrompt("error", '<spring:message code="link.acesscode.invalidLen"/>', '', '5');
                    return;
                }
                if (!checkLinkPWDRule(accessCode)) {
                    handlePrompt("error", '<spring:message code="link.acesscode.invalidChar"/>', '', '5');
                    $("#accessCode").val("");
                    return;
                }
            }
            else {
                var reg = new RegExp("^[A-Za-z0-9]+$");
                if (!reg.test(accessCode)) {
                    handlePrompt("error", '<spring:message code="link.accesscode.simple"/>', '', '5');
                    return;
                }
                if (accessCode.length > 20) {
                    handlePrompt("error", '<spring:message code="link.acesscode.invalidSimpleLen"/>', '', '5');
                    return;
                }
            }

        }
        else {
            $("#accessCode").val("");
        }

        if ($("#dCode").is(":checked")) {
            var identities = $("#identities").val();
            if (identities) {
                identities = $.trim(identities);
                $("#identities").val(identities);
            }
            $("#accessCodeMode").val("mail");
            if (identities == null || identities.trim() == "") {
                handlePrompt("error", '<spring:message code="link.label.email.empty"/>', '', '5');
                return;
            }

            var mailAddrArray = identities.split(";");
            if (mailAddrArray.length > 1) {
                handlePrompt("error", '<spring:message code="link.input.onlyOneEmail"/>', '', '5');
                $("#identities").val("");
                return;
            }

            checkEmailArrayRule(mailAddrArray);
            if (isMailPatternError) {
                handlePrompt("error", '<spring:message code="link.set.invalidEmail"/>', '', '5');
                return;
            }
        }
        else {
            $("#identities").val("");
        }


        if ($("#dateRadioCustom").is(":checked")) {
            var effectiveAt = $("#effectiveAt").val();
            var expireAt = $("#expireAt").val();

            if (effectiveAt == null || effectiveAt == "") {
                handlePrompt("error", '<spring:message code="link.set.needEffectiveAt"/>', '', '5');
                return;
            }

            if (expireAt != null && expireAt != "" && effectiveAt > expireAt) {
                handlePrompt("error", '<spring:message code="link.set.timeSetError"/>', '', '5');
                return;
            }
        } else {
            $("#effectiveAt").val("");
            $("#expireAt").val("");
        }

        $.ajax({
            type: "POST",
            url: "${ctx}/share/updateLink/" + ownerId + "/" + iNodeId + "?linkCode=" + linkId,
            data: $('#setLinkForm').serialize(),
            error: function (request) {
                doUpdateLinkError(request);
            },
            success: function (data) {
                if (typeof(data) == 'string' && data.indexOf('<html>') != -1) {
                    window.location.href = "${ctx}/logout";
                    return;
                }

                handlePrompt("success", '<spring:message code="link.set.saveSuccess"/>', '', '5');

                hideAclDiv();
                getLink();
            }
        });
    }

    function doInitLinkError(request) {
        switch (request.responseText) {
            case "BadRequest":
                ymPrompt.alert({
                    title: '<spring:message code="link.title.error"/>',
                    message: '<spring:message code="link.set.BadRquest"/>'
                });
                break;
            case "NoSuchLink":
            case "NoSuchItem":
                ymPrompt.alert({
                    title: '<spring:message code="link.title.error"/>',
                    message: '<spring:message code="link.set.NoSuchItems"/>'
                });
                break;
            case "Forbidden":
                ymPrompt.alert({
                    title: '<spring:message code="link.title.error"/>',
                    message: '<spring:message code="error.forbid"/>'
                });
                break;
            case "LinkExistedConflict":
                ymPrompt.alert({
                    title: '<spring:message code="link.title.error"/>',
                    message: '<spring:message code="link.set.Conflict"/>'
                });
                break;
            case "BusinessException":
                ymPrompt.alert({
                    title: '<spring:message code="link.title.error"/>',
                    message: '<spring:message code="link.set.setLinkFail"/>'
                });
                break;
            case "Unauthorized":
                ymPrompt.alert({
                    title: '<spring:message code="link.title.error"/>',
                    message: '<spring:message code="link.set.Forbidden"/>'
                });
                break;
            case "ExceedMaxLinkNum":
                ymPrompt.alert({
                    title: '<spring:message code="link.title.error"/>',
                    message: '<spring:message code="link.error.createLink"/>'
                });
                break;
            case "SecurityMatrixForbidden":
                ymPrompt.alert({
                    title: '<spring:message code="link.title.error"/>',
                    message: '<spring:message code="error.forbid"/>'
                });
                break;
            default:
                ymPrompt.alert({
                    title: '<spring:message code="link.title.error"/>',
                    message: '<spring:message code="link.set.setLinkFail"/>'
                });
        }
    }

    function doUpdateLinkError(request) {
        switch (request.responseText) {
            case "BadRequest":
                handlePrompt("error", '<spring:message code="link.set.BadRquest"/>', '', '5');
                break;
            case "NoSuchLink":
            case "NoSuchItem":
                handlePrompt("error", '<spring:message code="link.set.NoSuchItems"/>', '', '5');
                break;
            case "Forbidden":
                handlePrompt("error", '<spring:message code="link.set.Forbidden"/>', '', '5');
                break;
            case "BusinessException":
                handlePrompt("error", '<spring:message code="link.set.setLinkFail"/>', '', '5');
                break;
            case "LinkExistedConflict":
                handlePrompt("error", '<spring:message code="link.set.Conflict"/>', '', '5');
                break;
            case "Unauthorized":
                handlePrompt("error", '<spring:message code="link.set.Forbidden"/>', '', '5');
                break;
            default:
                handlePrompt("error", '<spring:message code="link.set.setLinkFail"/>', '', '5');
        }
    }

    function doDeleteLinkError(request) {
        switch (request.responseText) {
            case "BadRequest":
                handlePrompt("error", '<spring:message code="link.set.BadRquest"/>', '', '5');
                break;
            case "NoSuchLink":
            case "NoSuchItem":
                handlePrompt("error", '<spring:message code="link.set.NoSuchItems"/>', '', '5');
                break;
            case "BusinessException":
                handlePrompt("error", '<spring:message code="link.set.delLinkFail"/>', '', '5');
                break;
            case "Forbidden":
            case "Unauthorized":
                handlePrompt("error", '<spring:message code="error.forbid"/>', '', '5');
                break;
            default:
                handlePrompt("error", '<spring:message code="link.set.delLinkFail"/>', '', '5');
        }
    }

    function refreshLinkAccessCode() {
        $('#accessCode').val(getAccessCode(8));
    }

    function getFormatDate(date, isPattern) {
        var tempDate = "";
        if (date == undefined) {
            tempDate = "";
        } else {
            tempDate = new Date(date);
        }
        var pattern = "yyyy-MM-dd hh:mm";
        if (top.globalLang == "en" && !isPattern) {
            pattern = "MM/dd/yyyy hh:mm";
        }
        if (tempDate == "") {
            return "";
        } else {
            return tempDate.format(pattern);
        }
    }

    function getTimeZone() {
        var offset = 0 - new Date().getTimezoneOffset();
        var gmtHours = Math.floor(offset / 60);

        if (gmtHours < 0) {
            gmtHours = Math.ceil(gmtHours);
        }

        var gmtMinute = Math.abs(offset - gmtHours * 60) + "";

        if (gmtMinute.length == 1) {
            gmtMinute = "0" + gmtMinute;
        }

        if (gmtHours < 0) {
            return "GMT" + gmtHours + ":" + gmtMinute;
        }
        return "GMT+" + gmtHours + ":" + gmtMinute;
    }

    function userOnPaste() {
        setTimeout(function () {
            submitUsername = $("#emailUrl").val();
            userInputAutoSize("#emailUrl");
            searchMessageTo($("#emailUrl").val());
        }, 0);
    }

    var availableTags = [];
    var unAvaliableTags = [];
    function searchMessageTo(tempEmail) {
        if (tempEmail.length <= 1) {
            return;
        }

        var searchSpiner = new Spinner(optsSmallSpinner).spin($("#loadingDiv").get(0));
        availableTags = "";
        var params = {
            "ownerId": "<c:out value='${ownerId}'/>",
            "folderId": "<c:out value='${folderId}'/>",
            "userNames": tempEmail,
            "token": "<c:out value='${token}'/>"
        };
        tempUsername = params.userNames;
        var list;
        $.ajax({
            type: "POST",
            data: params,
            url: "${ctx}/share/listMultiUser",
            error: function (request) {
                searchSpiner.stop();
                handlePrompt("error", '<spring:message code="link.set.listUserFail"/>', '', '5');
                $("#emailUrl").focus();
            },
            success: function (data) {
                searchSpiner.stop();
                if (typeof(data) == 'string' && data.indexOf('<html>') != -1) {
                    window.location.href = "${ctx}/logout";
                    return;
                }
                if (tempUsername != submitUsername) {
                    return;
                }
                availableTags = data.successList;
                unAvaliableTags = data.failList;
                if (availableTags.length == 0) {
                    handlePrompt("error", '<spring:message code="inviteShare.error.empty"/>', '', '5');
                    return;
                }
                if (data.single && availableTags.length == 1) {
                    if (availableTags[0].userType == 1) {
                        addMessageTo(availableTags[0].email, availableTags[0].loginName);
                    }
                    else {
                        addMessageTo(availableTags[0].email);
                    }
                    $("#emailUrl").val("");
                    return;
                }
                if (!data.single && availableTags.length > 0) {
                    $(availableTags).each(function (n, item) {
                        if (item.userType == 1) {
                            addMessageTo(item.email, item.loginName);
                        }
                        else {
                            addMessageTo(item.email);
                        }
                    });
                    $("#emailUrl").val(unAvaliableTags + "");
                    userInputAutoSize("#emailUrl");
                    if (unAvaliableTags.length > 0) {
                        handlePrompt("error", '<spring:message code="inviteShare.error.partnoresult"/>', '', '5');
                    }
                    return;
                }
                if (data.single) {
                    $("#emailUrl").bind("keydown", function (event) {
                        if (event.keyCode === $.ui.keyCode.TAB &&
                            $(this).data("ui-autocomplete").menu.active) {
                            event.preventDefault();
                        }
                    }).autocomplete({
                        disabled: true,
                        position: {my: "left top", at: "left bottom", of: "#sendLinkEmail"},
                        minLength: 2,
                        source: function (request, response) {
                            response(availableTags);
                        },
                        focus: function () {
                            return false;
                        },
                        select: function (event, ui) {
                            $(this).val("");
                            if (ui.item.userType == 1) {
                                addMessageTo(ui.item.email, ui.item.loginName);
                            } else {
                                addMessageTo(ui.item.email);
                            }
                            return false;
                        }
                    }).data("ui-autocomplete")._renderItem = function (ul, item) {
                        if (item.userType == 1) {
                            return $("<li>")
                                .append("<a><i class='icon-users icon-orange' ></i><strong>" + item.label + "</strong> (-) " + "<br>" + item.department + "</a>")
                                .appendTo(ul);
                        } else {
                            return $("<li>")
                                .append("<a><i class='icon-user'></i><strong>" + item.label + "</strong> (" + item.email + ") " + "<br>" + item.department + "</a>")
                                .appendTo(ul);
                        }

                    };

                    $("#emailUrl").autocomplete("enable");
                    $("#emailUrl").autocomplete("search", $("#emailUrl").val());
                }
            }
        });
    }

    function split(val) {
        return val.split(/,\s*/);
    }
    function extractLast(term) {
        return split(term).pop();
    }

    var registerEventError = false;

    function sendLinkByEmail(url, plainAccessCode) {
        $("#emailUrl").val("");
        var emailUrl = "" + allMessageTo;
        emailUrl = emailUrl.replace(new RegExp(",", "g"), ";");
        if (trimStr(emailUrl) == "") {
            handlePrompt("error", '<spring:message code="link.set.emptyEmail"/>', '', '5');
            $("#emailUrl").focus();
            return;
        }
        if (emailUrl.charAt(emailUrl.length - 1) == ';') {
            emailUrl = emailUrl.substring(0, emailUrl.length - 1);
        }
        var mailAddrArray = emailUrl.split(";");
        if (mailAddrArray.length > 50) {
            handlePrompt("error", '<spring:message code="link.text.mailInfo"/>', '', '5');
            return;
        }

        if (emailUrl.indexOf("[department]") == -1) {
            var errorAddress = checkEmailArrayRule(mailAddrArray);

            if (emailUrl == "" || isMailPatternError) {
                handlePrompt("error", '<spring:message code="link.set.invalidEmail"/>', '', '5');
                return;
            }

        }
        var msgText = $("#messageText").val();
        if (msgText == '<spring:message code="inviteShare.addMessage"/>') {
            msgText = "";
        }
        if (registerEventError) {
            if (msgText.length > 2000) {
                handlePrompt("error", '<spring:message code="link.email.error.length"/>', '', '5');
                return;
            }
        }
        var params = {
            "ownerId": ownerId,
            "iNodeId": iNodeId,
            "emails": emailUrl,
            "linkUrl": url,
            "plainAccessCode": plainAccessCode,
            "message": msgText,
            "token": "<c:out value='${token}'/>"
        };
        $.ajax({
            type: "POST",
            data: params,
            url: "${ctx}/share/sendLink",
            error: function (request) {
                switch (request.responseText) {
                    case "NoSuchLink":
                    case "NoSuchItem":
                        handlePrompt("error", '<spring:message code="link.set.NoSuchItems"/>', '', '5');
                        break;
                    case "Forbidden":
                    case "Unauthorized":
                        handlePrompt("error", '<spring:message code="error.forbid"/>', '', '5');
                        break;
                    default:
                        handlePrompt("error", '<spring:message code="link.set.sendFail"/>', '', '5');
                }
                $("#emailUrl").focus();
            },
            success: function () {
                if (typeof(data) == 'string' && data.indexOf('<html>') != -1) {
                    window.location.href = "${ctx}/logout";
                    return;
                }
                top.ymPrompt.close();
                top.handlePrompt("success", '<spring:message code="operation.success"/>');
                top.linkHandle();
                mailMsg = msgText;
            }
        });
    }

    function addMessageTo(userEmail, name, type, id) {
        var button = $("<a class='close' id='" + id + "' title=" + '<spring:message code="button.delete"/>' + ">&times;</a>");
        var text = "";
        if (name == undefined || name == null) {
            text = $('<div title="' + userEmail + '">' + userEmail + '</div>');
        } else {
            text = $('<div title="' + name + '">' + name + '</div>');
        }
        var dd = $('<div class="invite-member"></div>');
        if ($.inArray(userEmail, allMessageTo) != -1) {
            return;
        }
        if ($.inArray(id + "department@department.com", allMessageTo) != -1) {
            return;
        }
        button.click(function () {
            $(this).parent().remove();
            var nodeid = $(this)[0].id;
            allDepartmentToMap.remove(nodeid);
            userInputAutoSize("#emailUrl");
            var tempArray = new Array();
            var length = allMessageTo.length;
            if (!userEmail) {
                userEmail = nodeid + "department@department.com";
            }
            for (var i = 0; i < length; i++) {
                var temp = allMessageTo.pop();
                if (temp != userEmail) {
                    tempArray.push(temp);
                } else {
                    break;
                }
            }
            allMessageTo = allMessageTo.concat(tempArray);
            var conH = parseInt($(".pop-content").outerHeight() + 90);
            top.ymPrompt.resizeWin(650, conH);

            if ($("#emailUrl").val() == '' && allMessageTo.length < 1) {
                $(".prompt").show();
            }
            window.event.cancelBubble = true;
            window.event.returnValue = false;
        });
        dd.append(text).append(button);
        $("#sendLinkEmail #emailUrl").before(dd);
        $("#emailUrl").focus();
        if (type == "department") {
            allMessageTo.push(id + "department@department.com");
        } else {
            allMessageTo.push(userEmail);
        }

        userInputAutoSize("#emailUrl");
        var conH = parseInt($(".pop-content").outerHeight() + 90);
        top.ymPrompt.resizeWin(650, conH);
    }

    function checkEmailArrayRule(mailAddrArray) {
        isMailPatternError = false;
        for (var i = 0; i < mailAddrArray.length; i++) {
            if (!checkEmailRule(mailAddrArray[i])) {
                isMailPatternError = true;
                return mailAddrArray[i];
                break;
            }
        }
    }

    function checkEmailRule(mail) {
        var pattern = /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;
        if (!pattern.test(mail) || mail.length < 5 || mail.length > 255) {
            return false;
        }
        return true;
    }

    function checkLinkPWDRule(accessCode) {
        var LINK_PASSWORD_WITH_CHAR = /[a-zA-Z]+/;
        var LINK_PASSWORD_WITH_NUMBER = /[0-9]+/;
        var LINK_PASSWORD_WITH_SCHAR = /[-+.!@#$^&*]+/;
        var LINK_PASSWORD_PATTERN = /[-+.!@#$^&*0-9a-zA-Z]/;

        if (!LINK_PASSWORD_WITH_CHAR.test(accessCode)) {
            return false;
        }
        if (!LINK_PASSWORD_WITH_NUMBER.test(accessCode)) {
            return false;
        }

        if (!LINK_PASSWORD_WITH_SCHAR.test(accessCode)) {
            return false;
        }

        if (!LINK_PASSWORD_PATTERN.test(accessCode)) {
            return false;
        }

        return true;
    }


    function trimStr(str) {
        return str.replace(/(^\s*)|(\s*$)/g, "");
    }

    function showAclDiv(index) {
        $('#urlAccess').val(linksData[index].url);
        if (linksData[index].download == false) {
            $("#download")[0].checked = false;
        } else {
            $("#download")[0].checked = true;
        }
        if (isComplexCode == "false") {
            $("#freshAcessCode").hide();
        }
        if (linksData[index].preview == false) {
            $("#preview")[0].checked = false;
        } else {
            $("#preview")[0].checked = true;
        }

        var objType = '<c:out value="${type}"/>';
        if (objType == 1) {
            $("#upload").parent().hide();
        } else {
            if (linksData[index].upload == false) {
                $("#upload")[0].checked = false;
            } else {
                $("#upload")[0].checked = true;
            }
        }

        if (linksData[index].accessCodeMode == "static") {
            if (linksData[index].plainAccessCode != "" && linksData[index].plainAccessCode != undefined) {
                $("#sCode")[0].checked = true;
                $("#dCode").parent().show();
                $("#sCodeDiv").show();
                $("#accessCode").val(linksData[index].plainAccessCode);
            } else {
                $("#sCodeDiv").hide();
                $("#sCode")[0].checked = false;
                $("#dCode").parent().hide();
                $("#accessCode").val("");
            }
            $("#dCode")[0].checked = false;
            $("#dCodeDiv").hide();
        } else if (linksData[index].accessCodeMode == "mail") {
            $("#sCode")[0].checked = true;
            $("#dCode")[0].checked = true;
            $("#sCodeDiv").hide();
            $("#dCode").parent().show();
            $("#identities").val(linksData[index].identities);
            $("#dCodeDiv").show();
        } else {
            $("#sCodeDiv,#dCodeDiv").hide();
            $("#dCode").parent().hide();
            $("#dCodeDiv").hide();
        }

        var efAt = getFormatDate(linksData[index].effectiveAt, false);
        var exAt = getFormatDate(linksData[index].expireAt, false);
        var efAtZH = getFormatDate(linksData[index].effectiveAt, true);
        var exAtZH = getFormatDate(linksData[index].expireAt, true);
        if (linksData[index].effectiveAt != undefined) {
            $("#dateDiv").show();
            $("#dateRadioCustom")[0].checked = true;

            var dateEffectiveAt = efAt;
            var dateExpireAt = '<spring:message code="link.set.timeForEver"/>';
            if (linksData[index].expireAt != undefined) {
                dateExpireAt = exAt;
            }
            $("#effectiveAtTime").val(efAt);
            $("#expireAtTime").val(exAt);

            $("#effectiveAt").val(efAtZH);
            $("#expireAt").val(exAtZH);
        } else {
            $("#dateDiv").hide();
            $("#dateRadioCustom")[0].checked = false;
        }

        $(".file-link-con, .link-email").hide();
        $(".link-access").show();
        $("#saveAccess").unbind();
        $("#saveAccess").bind("click", function () {
            updateLink(linksData[index].id);
        });
        $("#cancelAccess").unbind();
        $("#cancelAccess").bind("click", function () {
            hideAclDiv();
        });

        var conH = parseInt($(".pop-content").outerHeight() + 90);
        top.ymPrompt.resizeWin(650, conH);
    }

    function hideAclDiv() {
        $(".link-access, .link-email").hide();
        $(".file-link-con").show();

        var conH = parseInt($(".pop-content").outerHeight() + 90);
        top.ymPrompt.resizeWin(650, conH);
    }

    function emailkeyup() {
        $("#emailUrl").val($("#emailUrl").val().replace(new RegExp(",", "g"), ";"));
    }

    function showEmailSend(index) {
        $('#urlEmail').val(linksData[index].url);
        var emailLinkInfo = $("#linkInfo" + index).clone();
        $(emailLinkInfo).removeAttr("id");
        $('#linkEmailInfo').html(emailLinkInfo);

        $("#sendEmail").unbind();
        $("#sendEmail").bind("click", function () {
            sendLinkByEmail(linksData[index].url, linksData[index].plainAccessCode);
            allDepartmentToMap = new Map();
        });

        $(".file-link-con, .link-access").hide();
        $(".link-email").show();
        var conH = parseInt($(".pop-content").outerHeight() + 90);
        top.ymPrompt.resizeWin(650, conH);
    }

    function cancelSendEmail() {
        $(".link-email, .link-access, .enterPrompt").hide();
        $(".file-link-con, .prompt").show();
        $("#emailUrl, #messageText").val("").blur();
        $("#emailUrl").removeAttr("style");
        $("#sendLinkEmail").find(".invite-member").remove();
        allMessageTo = [];
        allDepartmentToMap = new Map();
        var conH = parseInt($(".pop-content").outerHeight() + 90);
        top.ymPrompt.resizeWin(650, conH);
    }

    function getRandomNum(lbound, ubound) {
        return (Math.floor(Math.random() * (ubound - lbound)) + lbound);
    }

    function getRandomChar(number, chars, other) {
        var numberChars = "0123456789";
        var lowerAndUpperChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        var otherChars = "!@#$^&*-+.";
        var charSet = "";
        if (number == true)
            charSet += numberChars;
        if (chars == true)
            charSet += lowerAndUpperChars;
        if (other == true)
            charSet += otherChars;
        return charSet.charAt(getRandomNum(0, charSet.length));
    }
    function getAccessCode(length) {
        var rc = getRandomChar(true, false, false);
        rc = rc + getRandomChar(false, true, false);
        rc = rc + getRandomChar(false, false, true);

        for (var idx = 3; idx < length; idx++) {
            rc = rc + getRandomChar(true, true, true);
        }

        var arr_str = rc.split("");
        for (var i = 0; i < 50; i++) {
            var idx1 = getRandomNum(0, length);
            var idx2 = getRandomNum(0, length);

            if (idx1 == idx2) {
                continue;
            }

            var tempChar = arr_str[idx1];
            arr_str[idx1] = arr_str[idx2];
            arr_str[idx2] = tempChar;
        }

        return arr_str.join("");
    }

    function checkNickName(searchSpiner) {
        var n = $("#emailUrl").val();
        $.ajax({
            type: "POST",
            url: "${ctx}/share/listNickUser",
            data: {'userNames': n, 'token': "<c:out value='${token}'/>"},
            error: function (data) {
                searchSpiner.stop();
                handlePrompt("error", '<spring:message code="inviteShare.listUserFail"/>', '', '5');
                $("#emailUrl").focus();
            },
            success: function (data) {
                searchSpiner.stop();
                var control = eval(data);
                if (!data.result) {
                    return;
                }

                for (var key in control.map) {
                    var con = control.map[key];
                    addMessageTo(con[0].email);
                }
                $("#emailUrl").val("");

            }
        });

    }

    function getLinkCode(linkUrlTmp) {
        if (linkUrlTmp.lastIndexOf("/") != -1) {
            return linkUrlTmp.substring(linkUrlTmp.lastIndexOf('/') + 1);
        }
        return "";
    }

    function clipMap() {
        var size = 0;
        var entry = new Object();

        this.put = function (key, value) {
            if (!this.containsKey(key)) {
                size++;
            }
            entry[key] = value;
        }

        this.get = function (key) {
            if (this.containsKey(key)) {
                return entry[key];
            }
            else {
                return null;
            }
        }

        this.remove = function (key) {
            if (delete entry[key]) {
                size--;
            }
        }

        this.containsKey = function (key) {
            return (key in entry);
        }

        this.containsValue = function (value) {
            for (var prop in entry) {
                if (entry[prop] == value) {
                    return true;
                }
            }
            return false;
        }

        this.values = function () {
            var values = new Array(size);
            for (var prop in entry) {
                values.push(entry[prop]);
            }
            return values;
        }

        this.keys = function () {
            var keys = new Array(size);
            for (var prop in entry) {
                keys.push(prop);
            }
            return keys;
        }

        this.size = function () {
            return size;
        }
    }
</script>
</body>
</html>
