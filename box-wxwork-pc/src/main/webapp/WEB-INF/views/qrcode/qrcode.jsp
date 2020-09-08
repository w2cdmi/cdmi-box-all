<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<%
    request.setAttribute("page", 4) ;
%>
<!DOCTYPE html>
<html lang="en">

<head>
    <%@ include file="../common/include.jsp"%>
    <script src="${ctx}/static/components/Menubar.js"></script>
    <%--<link rel="stylesheet" href="${pageContext.request.contextPath}/static/style/login.css " />--%>
    <style type="text/css">
        .successQR {
            width: 100px;
            height: 100px;
            margin: 0 auto;
            line-height: 500px;
        }

        .successQR img {
            width: 100%;
        }
    </style>

</head>

<body style="overflow: auto;position: absolute;height: 100%;width: 100%">
<jsp:include page="../common/menubar.jsp" />
<div style="background: #fff;">
    <div style="width: 1024px;margin: 80px auto">
        <div class="box messagebox-success" style="position: relative;">
            <div class="model" style="display: none;position:  absolute;background: #fff;left:  0;width:  100%;height:  100%;">
                <div class="successQR">
                    <img src="${ctx}/static/skins/default/img/successQR.png" />
                </div>
                <p style="font-size: 16px;text-align: center;line-height: 360px;">机器人正在对<span id="wxName">${wxName}</span>的微信数据进行备份</p>
            </div>
            <div class="modeltip" style="display: none;position:  absolute;background: #fff;left:  0;width:  100%;height:  100%;">
                <!--<div class="successQR">
								<img src="${ctx}/static/assets/images/successQR.png" />
							</div>-->
                <p id="messagetip" style="font-size: 16px;text-align: center;line-height: 300px;"></p>
            </div>
            <div id="qrcode">
                <p style="text-align: center;font-size: 16px; color: #999999;">请用微信扫码，启动备份机器人</p>
                <div style="width: 300px; height: 300px; margin: 0 auto;">
                    <img style="width: 100%;" alt="" src="https://login.weixin.qq.com/qrcode/${uuid}==">
                </div>

            </div>
            <div class="box messagebox success">
                <p id="fristtitle" style="text-align: center;" class="title">请使用微信扫描二维码，启动微信备份机器人</p>
                <div id="errortip" class="errortip" style="display: none;font-size: 14px; color: #999999; text-align: center;">
                    <p class="btitle" style="color: red; ">备份机器人启动失败</p>
                    <p class="btitle" style="margin-top: 10px;">抱歉！您的微信号不能登陆Web微信，请联系腾讯</p>
                </div>
                <!-- 	<div class="QRcode"></div>
                    <p id="fristtitle" class="btitle" style="font-size: 14px; color: #999999;">请使用微信扫描二维码，启动微信备份机器人</p>
                    <div id="errortip" class="errortip" style="font-size: 14px; color: #999999;">
                        <p class="btitle" style="color: red;">备份机器人启动失败</p>
                        <p class="btitle" style="margin-top: 10px;">抱歉！您的微信号不能登陆Web微信，请联系腾讯</p>
                    </div> -->
            </div>
        </div>
    </div>
</div>
<!--->


<script type="text/javascript">
    $(document).ready(function() {
//        var menubar = $("#menubar").Menubar()
//        menubar.init();
    });

    var token = '${token}';
    var wxUin = '${uin}';
    var robotId = '${robotId}';
    var isRun ='${isRun}';

    var wxName = '${wxName}';
    var interval = setInterval('myrefresh()', 5000);

    function myrefresh() {
        console.log(isRun)
        checkIsRun();
    }

    function checkIsRun() {
        var premater = {
            "token": token,
        };
        $.ajax({
            type: "GET",
            url: "${ctx}/wxRobot/checkIsLogin?uuid=${uuid}&wxUin="+wxUin,
            data: premater,
            error: function(request) {
                listRobotConfig();
//                window.location.reload();
            },
            success: function(data) {
//                if(data != "notsupport") {
//                    if(isRun != data) {
//                        listRobotConfig();
//                    }
//                } else {
//                    $('.modeltip').css('display','block')
//                    $("#messagetip").text("当前微信不支持机器人");
//                }
                if( data instanceof Object){
                    wxUin = data.wxUin;
                    robotId = data.id;
                    wxName = data.wxName;
                    $('.model').css('display', 'block');
                    $("#wxName").html(wxName)
                    listRobotConfig();
                }else if(data == "true"){
                    listRobotConfig();
                }else{
                    if(wxUin != ""){
                        window.location.reload();
                    }else{
                    }
                }
            }
        });
    }

    $(function() {

        if(isRun == 'false') {

        } else if(isRun == 'true') {
            $('.model').css('display', 'block');
            listRobotConfig();
        }
    });

    function stopRobot() {

        var premater = {
            "token": token,
            "robotId": robotId
        };
        $.ajax({
            type: "POST",
            url: "${ctx}/wxRobot/stopRobot?robotId=" + robotId,
            data: premater,
            error: function(request) {
                console.log(request);
            },
            success: function(data) {
                window.location.reload();
            }
        });
    }

    function
    listRobotConfig() {
        var premater = {
            "token": token
        };
        $.ajax({
            type: "POST",
            url: "${ctx}/wxRobot/listWxRobotConfig?robotId=" + robotId,
            data: premater,
            error: function(request) {
                console.log(request);
            },
            success: function(data) {
                var configs = {
                    list: []
                };
                for(var i = 0; i < data.length; i++) {
                    if(data[i].type == 1 || data[i].type == 2) {
                        setCheckBoxConfig(data[i])
                    }
                    if(data[i].type == 3) {
                        configs.list.push(data[i]);
                    }
                }
                /* 			var html = template('configTemplate', configs); */
                $("#configs").empty();
//                $("#configs").append(html);
            }
        });
    }

    function setCheckBoxConfig(value) {
        var type = value.type == 1 ? "user" : "group";
        if(value.config.file) {
            $("#" + type + "File").prop("checked", true);
        }
        if(value.config.image) {
            $("#" + type + "Image").prop("checked", true);
        }
        if(value.config.video) {
            $("#" + type + "Video").prop("checked", true);
        }
    }

    function listGroupsName() {
        var premater = {
            "token": token
        };
        $.ajax({
            type: "POST",
            url: "${ctx}/wxRobot/listWxRobotGroups?uin=" + wxUin,
            data: premater,
            error: function(request) {
                console.log(request);
            },
            success: function(data) {
                $("#addgroup").show();
                $("#groupName").autocomplete({
                    source: data
                });
            }
        });
    }

    function showAddGroup() {
        listGroupsName();
    }

    function addBlackConfig() {
        var premater = {
            "token": token,
            'robotId': robotId,
            'name': $("#groupName").val(),
            'value': 0,
            'type': 3
        };
        if($("#groupName").val() == "") {
            return;
        }
        $.ajax({
            type: "POST",
            url: "${ctx}/wxRobot/createConfig",
            data: premater,
            error: function(request) {
                console.log(request);
            },
            success: function(data) {
                listRobotConfig();
                closeGroupModal();
            }
        });
    }

    function updateWxRobotConfig(typeString, type) {
        var premater = {
            "token": token,
            'robotId': robotId,
            'value': getConfigValue(typeString),
            'type': type
        };
        $.ajax({
            type: "POST",
            url: "${ctx}/wxRobot/updateConfig",
            data: premater,
            error: function(request) {
                console.log(request);
            },
            success: function(data) {

            }
        });
    }

    function deleteConfig(th) {
        var premater = {
            "token": token,
            'robotId': $(th).attr("robotId"),
            'name': $(th).attr("name"),
            'type': 3
        };
        $.ajax({
            type: "POST",
            url: "${ctx}/wxRobot/deleteConfig",
            data: premater,
            error: function(request) {
                console.log(request);
            },
            success: function(data) {
                listRobotConfig();
            }
        });
    }

    function getConfigValue(type) {
        var value = parseValue(type + "Video") + parseValue(type + "Image") + parseValue(type + "File");
        return value;
    }

    function parseValue(id) {
        return $("#" + id).get(0).checked == true ? "1" : "0";
    }

    function closeGroupModal() {
        $("#addgroup").hide();
        $("#groupName").val("");
    }

    $("input[type=checkbox][name=userConfig]").click(function() {
        updateWxRobotConfig("user", 1);
    })

    $("input[type=checkbox][name=groupConfig]").click(function() {
        updateWxRobotConfig("group", 2);
    })
</script>
</body>

</html>