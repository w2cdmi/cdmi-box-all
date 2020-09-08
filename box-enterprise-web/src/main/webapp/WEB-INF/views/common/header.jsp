<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="pw.cdmi.box.disk.utils.CSRFTokenManager" %>
<%@ page import="org.apache.shiro.SecurityUtils" %>
<%@ page import="pw.cdmi.box.disk.oauth2.domain.UserToken" %>
<%@ page import="pw.cdmi.box.disk.utils.PropertiesUtils" %>

<META HTTP-EQUIV="Expires" CONTENT="0">
<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
<META HTTP-EQUIV="Cache-control" CONTENT="no-cache, no-store, must-revalidate">
<META HTTP-EQUIV="Cache" CONTENT="no-cache">

<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);
    request.setAttribute("token", CSRFTokenManager.getTokenForSession(session));
    UserToken user = (UserToken) SecurityUtils.getSubject().getPrincipal();
    String username = org.springframework.web.util.HtmlUtils.htmlEscape(user.getName());
    Boolean isLocalAuth = (Boolean) SecurityUtils.getSubject().getSession().getAttribute("isLocalAuth");
    boolean tag = (Boolean) SecurityUtils.getSubject().getSession().getAttribute("tag");
    boolean loadTag = (Boolean) SecurityUtils.getSubject().getSession().getAttribute("loadTag");
%>
<%
    if ("true".equals(CustomUtils.getValue("cloudapp.commonTips"))) {
%>
<div class="top-info"><spring:message code="link.header.error.tips"/></div>
<%
    }
%>
<div class="header">
    <div class="header-con">
        <div class="logo" id="logoBlock" style="width: 184px; height: 38px;text-align: center;"><img/><span style="color: white;font-size: 18px;margin-left:4px;">闪电云</span></div>
        <div class="top_menu">
             <ul class="nav nav-pills" role="tablist">
                <li role="presentation" class="active">
                    <a aria-controls="account_login" role="tab" data-toggle="tab" href="#">文件管理</a>
                </li>
                <c:if test="${memberInfo.teamRole == 'admin' || memberInfo.teamRole == 'manager'}">
                <li role="presentation" class="">
                    <a role="tab" data-toggle="tab" href="#" onclick="toMemberMgr();">
                        <spring:message code='teamSpace.title.memberMgr'/>
                    </a>
                </li>
                </c:if>
                <c:if test="${memberInfo.teamRole == 'member'}">
                    <li role="presentation" class="">
                        <a role="tab" data-toggle="tab" href="#" onclick="toMemberMgr();">
                            <spring:message code='teamSpace.title.viewMember'/>
                        </a>
                    </li>
                </c:if>
                <li role="presentation" class="">
                    <a role="tab" data-toggle="tab"  href="#">企业联系人</a>
                </li>
                <li role="presentation" class="">
                    <a role="tab" data-toggle="tab"  href="#">审批</a>
                </li>
            </ul>
        </div>
        <div class="leftside" style="min-height:580px;">
            <div class="nav-menu">
                <ul class="docTypeTree_menu clearfix">
                    <li>
                        <div class='doctypetree_item'>
                            <a id="navAllFile" href="${ctx}/"><i class="new-icon-navmenu-folder"></i><spring:message code="navigation.file"/></a>
                            <%--<i id="doctypeTree_show" style="display:inline-block;float:right;line-height: 50px;"></i>--%>
                        </div>
                        <ul id="userDoctypetree" class="level2"></ul>
                    </li>
                    <li>
                        <div class='doctypetree_item'>
                            <a id="navTeamSpace" href="${ctx}/teamspace"><i class="new-icon-navmenu-teamspace"></i><spring:message code="navigation.teamSpace"/></a>
                            <%--<i id="teamSpaceDoctypeTree_show" style="display:inline-block;float:right;line-height: 50px;"></i>--%>
                        </div>
                        <ul id="teamSpaceDoctypetree" class="level2"></ul>
                    </li>
                    <div id="witchSelect" value="-1" hidden="hidden"></div>
                    <div id="teamId" value="-1" hidden="hidden"></div>
                    <%--<li class="divider"></li>--%>
                    <li><div class='doctypetree_item'><a id="navTrash" href="${ctx}/trash#1"><i class="new-icon-navmenu-trash"></i><spring:message code='button.recycler'/></a></div></li>
                    <%--<li class="divider"></li>--%>
                    <li style="display: none;"><div class='doctypetree_item'><a id="navAPP" href="${ctx}/app"><i></i><spring:message code="navigation.application"/></a></div></li>
                    <%--<li class="divider"></li>--%>
                    <li style="display:none;"><div class='doctypetree_item'><a id="navUserFeedBack" href="${ctx}/feedback/feedBackList"><i></i><spring:message code="navigation.userFeedBack"/></a></div></li>
                    <%--<li class="divider"></li>--%>
                    <li>
                        <div class='doctypetree_item'>
                            <a id="navShareSpace" href="#"><i class="new-icon-navmenu-share"></i>共享空间</a>
                        </div>
                        <ul style="display: none;">
                            <li><div class="doctypetree_item"><a id="navShareToMe" href="${ctx}/shared"><i class="new-icon-navmenu-share"></i><spring:message code='share.menu.shareToMe'/></a></div></li>
                            <li><div class='doctypetree_item'><a id="navShareByMe" href="${ctx}/myShares"><i></i><spring:message code="share.menu.shareByMe"/></a></div></li>
                        </ul>
                    </li>
                </ul>
            </div>

            <div class="copyRight-block">
                <ul>
                    <div>
						<p id="useSpace"></p>
						<div class="totalSize" style="display:none">
							<div id="spaceBar"></div>
						</div>
					</div>
                    <li>
                        <ul class="nav-client nav nav-pills">
                            <li>
                                <div>
                                    <i class="new-icon-sidebar-client_pc"></i>
                                    <div>PC端</div>
                                </div>
                            </li>
                            <li>
                                <div>
                                    <i class="new-icon-sidebar-client_android"></i>
                                    <div>Android</div>
                                </div>
                            </li>
                            <li>
                                <div>
                                    <i class="new-icon-sidebar-client_iphone"></i>
                                    <div>iPhone</div>
                                </div>
                            </li>
                        </ul>
                    </li>
                </ul>
            </div>
        </div>
        <div class="header-R">
            <div class="user-info">
                <div class="user-logo" id="userLogo"><img/></div>
                <div class="user-name dropdown">
                    <a class="btn btn-link dropdown-toggle" data-toggle="dropdown" title='<%=username%>'>
                        <span class="user-name-text"><%=username%></span>
                        <%--<i class="icon-br-down icon-white"></i>--%>
                    </a>
                    <ul class="dropdown-menu pull-right">
                        <li><a href="${ctx}/user/settings"><i class="new-icon-topmenu-userinfo-center"></i>个人中心</a></li>
                        <li><a href="javascript:void(0)" onclick="invite()"><i class="icon-create-group"></i>邀请伙伴</a></li>
                        <li><a href="${ctx}/user/suggestion"><i class="new-icon-topmenu-userinfo-suggestion"></i>意见反馈</a></li>
                        <li><a href="javascript:void(0)" onclick="doLogout()"><i class="new-icon-topmenu-userinfo-logout"></i><spring:message code='user.settings.logout'/></a></li>
                    </ul>
                </div>
            </div>
            <div class="user-handle">
                <a href="${ctx}/wxRobot/createQrCode" class="btn btn-link">
                   <img alt="" src="${ctx}/static/skins/default/img/robot_30.png">
                </a>
                <span class="favorite-con" style="display:none;">
                    <a class="btn btn-link" id="favoriteTree" title="<spring:message code='common.favorite.my'/>"onclick="initFavoriteTree()">
                        <i class="icon-favorite icon-white"></i>
                    </a>
                    <ul id="favoriteArea" class="ztree"></ul>
                </span>
                <a href="${ctx}/message" class="btn btn-link message" title="<spring:message code='message.title'/>">
                    <span class="badge" id="unreadMsgCount" style="display: none"></span>
                    <i class="new-icon-topmenu-alert"></i>
                    <%--<i class="icon-message icon-white"></i>--%>
                </a>
            </div>
        </div>
        <!-- show login info -->
        <div class="modal hide" id="loginInfo" tabindex="-1" role="dialog"
             aria-hidden="true" data-backdrop="static">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" onclick="closeInfo()" aria-hidden="true">&times;</button>
                <h3><spring:message code="loginInfo.info"/></h3>
            </div>
            <div class="modal-body" style="margin-left:40px;">
                <div id="loginTime" style="margin-left:12px;">
                    <spring:message code="loginInfo.date"/>
                    <span id="showTimeZone"></span>
                </div>
                <div id="loginIP" style="margin-top:10px;">
                    <spring:message code="loginInfo.IP"/>
                    <%=SecurityUtils.getSubject().getSession().getAttribute("lastLoginIP") %>
                </div>
                <div id="loginClient" style="margin-top:10px;margin-left:27px;">
                    <spring:message code="loginInfo.clientInfo"/>
                    <%=SecurityUtils.getSubject().getSession().getAttribute("terminalType") %>
                </div>
            </div>
        </div>
        <div id="dialog-message">
            <textarea id="messageContent" style="width: 400px;height: 200px;"></textarea>
        </div>
    </div>
</div>
<script type="text/javascript">

    var receiverId = <shiro:principal property="cloudUserId"/>;
    var globalLang = '<spring:message code="common.language1"/>';
    var receiveMsg;
    var dialog;
    var menutree = "";
    $(function () {
        dialog = $( "#dialog-message" ).dialog({
            height: 300,
            width: 450,
            autoOpen: false,
            title:'<spring:message code="register_invite_title"/> ',
            buttons: {
                Ok: function() {
                    $( this ).dialog( "close" );
                }
            }
        });
        $("#loginInfo").css({width: "400px", height: "200px", top: "74%", left: "90%"});
        var flag = ${loadTag};
        var isSign = ${needDeclaration};
        if (isSign) {
            showDeclaration();
        }
        var ip = "<%=SecurityUtils.getSubject().getSession().getAttribute("lastLoginIP") %>";
        var time = <%=(Long)SecurityUtils.getSubject().getSession().getAttribute("lastLoginTime") %>;
        if (flag) {
            if (ip != "null" && ip != null && time != "null" && time != null) {
                $("#showTimeZone").text(getLocalTime(time));
                $("#loginInfo").show();
                setTimeout(function () {
                    $("#loginInfo").hide();
                    <%
                        SecurityUtils.getSubject().getSession().setAttribute("loadTag",false);
                    %>
                }, 5000);
            }
        }

        getUserSpace();

        receiveMsg = isReceiveMsg();
        if (receiveMsg) {
            showUnreadMessageCount();
            createWebsocketConnection();
        }

        if (globalLang == "en") {
            $("#langEN").remove();
        } else {
            $("#langZH").remove();
        }

        $(document).mousedown(function () {
            var ev = arguments[0] || window.event;
            var srcElement = ev.srcElement || ev.target;
            if (srcElement != $("#favoriteArea").get(0) && srcElement != $("#favoriteTree").get(0) && $(srcElement).parents("a").attr("id") != "favoriteTree" && $(srcElement).parents(".ztree").attr("id") != "favoriteArea") {
                $("#favoriteArea").hide();
            }
        });
        stopDefaultScroll("favoriteArea");
        initDoctypeList();
        if ("${modelDocType}" != "") {
            $("#userDoctypetree").show();
        } else {
            $("#userDoctypetree").hide();
        }

        showUnreadMessageCount();
        loadUserImageSetting("${ctx}", '<spring:message code="common.language1" />');

        $("#navShareSpace").click(function(){
            var jq = $(this).closest("div.doctypetree_item").parent("li");
            if (!jq.hasClass("current")) {
                jq.find("ul").toggle();
            }
        });
    });

    function initDoctypeList() {
        var url = "${ctx}/user/doctype/find/owner";
        $.ajax({
            type: "GET",
            async: "false",
            url: url,
            error: function (request) {
            },
            success: function (data) {
                var doctypeList = data.docUserConfigs;
                var liDocm = "";
                var teamLiDocm = "";
                var teamId = $("#teamId").attr("value");
                for (var i = 0; i < doctypeList.length; i++) {
                    //var display = $.cookie("collspanDoc")=="1" ? "style='display:block'" : "style='display:none'";
                    var name = chanageNameValue(doctypeList[i].name);
                    liDocm += "<li><div class='doctypetree_item'><a id='docType" + doctypeList[i].id + "' href='${ctx}/folder?docType=" + doctypeList[i].id + "' title='" + name + "' ><i></i>" + name + "</a></div></li>"; //<li class='divider'></li>
                    teamLiDocm += "<li><div class='doctypetree_item'><a id='docType" + doctypeList[i].id + "' href='${ctx}/?teamDocType=" + doctypeList[i].id + "&teamId=" + teamId + "' title='" + name + "' ><i></i>" + name + "</a></div></li>";//<li class='divider'></li>
                }
                if ($("#witchSelect").val() != -1 && $("#teamId").attr("value") == -1) {
                    $("#userDoctypetree").append(liDocm);
                }
                if ($("#witchSelect").val() != -1 && $("#teamId").attr("value") != -1) {
                    $("#teamSpaceDoctypetree").append(teamLiDocm);
                }
                if (menutree == 'docType1' || menutree == 'docType2' || menutree == 'docType3' || menutree == 'docType4' || menutree == 'docType5') {
                    navMenuSelected(menutree);
                }
            }
        });
    }

    function chanageNameValue(name) {
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

    function closeInfo() {
        $("#loginInfo").hide();
    }

    function showDeclaration() {
        top.ymPrompt.win({
            message: '${ctx}/syscommon/declaration',
            width: 600,
            height: 400,
            title: '<spring:message code="declaration.sign.title"/>',
            iframe: true,
            btn: [['<spring:message code="agree.declaration"/>', 'yes', false, "btnSignDeclaration"], ['<spring:message code="disagree.declaration"/>', 'no', true, "btnSignDeclarationCancel"]],
            handler: doSignDeclaration
        });
        top.ymPrompt_addModalFocus("#btnSignDeclaration");
    }

    function doSignDeclaration(tp) {
        if (tp == 'yes') {
            top.ymPrompt.getPage().contentWindow.signDeclaration();
        } else {
            top.ymPrompt.close();
            window.location.href = "${ctx}/logout";
        }
    }

    function enterModifyPwdPage() {
        top.ymPrompt.win({
            message: '${ctx}/user/goChangePwd',
            width: 550,
            height: 300,
            title: '<spring:message code="common.account.change.password"/>',
            iframe: true,
            btn: [['<spring:message code="teamSpace.button.edit"/>', 'yes', false, "btnModifyPwd"], ['<spring:message code="teamSpace.button.btnCancel"/>', 'no', true, "btnModifyCancel"]],
            handler: doSubmitModifyPwd
        });
        top.ymPrompt_addModalFocus("#btnModifyPwd");
    }

    function doSubmitModifyPwd(tp) {
        if (tp == 'yes') {
            top.ymPrompt.getPage().contentWindow.submitModifyPwd();
        } else {
            top.ymPrompt.close();
        }
    }

    function enterModifyEmailPage() {
        top.ymPrompt.win({
            message: '${ctx}/user/goChangeEmail',
            width: 550,
            height: 250,
            title: '<spring:message code="common.account.change.mail"/>',
            iframe: true,
            btn: [['<spring:message code="teamSpace.button.edit"/>', 'yes', false, "btnModifyEmail"], ['<spring:message code="teamSpace.button.btnCancel"/>', 'no', true, "btnModifyCancel"]],
            handler: doSubmitModifyEmail
        });
        top.ymPrompt_addModalFocus("#btnModifyEmail");
    }

    function doSubmitModifyEmail(tp) {
        if (tp == 'yes') {
            top.ymPrompt.getPage().contentWindow.submitModifyEmail();
        } else {
            top.ymPrompt.close();
        }
    }

    function getUserSpace() {
        $.ajax({
            type: "GET",
            url: "${ctx}/user/info?" + Math.random(),
            success: function (data) {
            	if(data.spaceQuota==-1){
    				$("#spaceBar").css("width","0%");
    				$("#useSpace").html(formatFileSize(data.spaceUsed) + "&nbsp;/&nbsp;无限制");
    				$(".totalSize").css("display","none");
    			}else{
    				$("#spaceBar").css("width",formatFileSize(data.spaceUsed)/formatFileSize(data.spaceQuota));
    				$("#useSpace").html(formatFileSize(data.spaceUsed) + "&nbsp;/&nbsp;" + formatFileSize(data.spaceQuota));
    				$(".totalSize").css("display","none");
    			}
            }
        });
    }

    function showGuide() {
        var isShowGuide = getCookie("isShowGuide");
        var guideUrl = "${ctx}/static/help/guide/guide.html";
        if ('<spring:message code="common.language1"/>' == "en") {
            guideUrl = "${ctx}/static/help/guide/guide-en.html";
        }
        ymPrompt.win({message: guideUrl, width: 650, height: 500, titleBar: false, iframe: true});
    }
    function invite(){
        $.ajax({
            type: "GET",
            url: "${ctx}/user/invite/code",
            success: function(data) {
                var location = window.location.protocol+"//"+window.location.hostname+":"+("80"==window.location.port?"":window.location.port);
                var message = location+"${ctx}/syscommon/invite/"+data;
                $("#messageContent").text(message);
                dialog.dialog( "open" );
            }
        });

    }
    function showAbout() {
        var aboutUrl = "${ctx}/about";
        ymPrompt.win({
            title: "<spring:message code='common.aboutMe'/>",
            message: aboutUrl,
            width: 550,
            height: 260,
            iframe: true
        });
    }

    function showUnreadMessageCount() {
        var url = "${ctx}/message/getMessageCount";

        $.ajax({
            type: "POST",
            url: url,
            data: {token: "<c:out value='${token}'/>"},
            success: function (data) {
                if (data == 0 || !receiveMsg) {
                    $("#unreadMsgCount").hide();
                } else if (data.length > 100) {
                    window.top.location = "${ctx}/login";
                } else {
                    $("#unreadMsgCount").text(data);
                    $("#unreadMsgCount").show();
                }
            }
        });
    }

    function createWebsocketConnection() {
        if (!supportWebSocket()) {
            return;
        }

        $.ajax({
            type: "GET",
            url: "${ctx}/message/listener",
            success: function (data) {
                var webSocket = new WebSocket(data);

                webSocket.onerror = function (event) {
                };

                webSocket.onopen = function (event) {
                };

                webSocket.onmessage = function (event) {
                    onMessage(event)
                };

            }
        });
    }

    function getUserConfig(configName) {
        var value;
        $.ajax({
            type: "GET",
            async: false,
            url: "${ctx}/user/config/" + receiverId + "?name=" + configName,
            success: function (data) {
                if (typeof(data) == 'string' && data.indexOf('<html>') != -1) {
                    window.location.href = "${ctx}/logout";
                    return;
                }
                if (data != null) {
                    value = data.value;
                } else {
                    value = "";
                }
            }
        });
        return value;
    }

    function isReceiveMsg() {
        var value = getUserConfig("messageNotice");
        if (value == "disable") {
            return false;
        }
        return true;
    }

    function supportWebSocket() {
        return 'WebSocket' in window || 'MozWebSocket' in window;
    }

    function onMessage(event) {
        showUnreadMessageCount();
    }

    function doLogout() {
        delRootCookie("fileListPerPageNum");
        $.ajax({
            type: "POST",
            url: "${ctx}/logout",
            data: {token: "<c:out value='${token}'/>"},
            error: function (request) {
                window.location = "${ctx}/login";
            },
            success: function (data) {
                window.location = "${ctx}/login";
            }
        });
    }
</script>

<%@ include file="../favorite/favorite.jsp" %>
