<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="../common/include.jsp" %>
    <link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/teamSpace/addTeamSpace.css"/>
    <link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/teamSpace/enditTeamSpace.css"/>
	<title>修改空间</title>
</head>
<body style="font-size: 0.75rem;">
<div class="found-space">
    <form class="form-horizontal label-w140" id="editTeamSpaceForm">
        <input type="hidden" id="teamId" name="teamId" value="${teamSpaceInfo.id}"/>
        <div class="bd" id="editTeamSpaceForm">
        	<div class="found-space-header"></div>
        	<ul class="found-space-content">
        		<li>
        			<p><spring:message code='common.field.name'/>: </p>
        			<input class="found-space-input valid" type="text" id="name" name="name" maxlength="255" value="${teamSpaceInfo.name}"/>
        		</li>
        		<li>
        			<p><spring:message code='teamSpace.label.description'/>:（最大限制为255个字）</p>
        		</li>
        		<textarea name="description" rows="4" cols="20" id="description">${teamSpaceInfo.description}</textarea>
        	</ul>
			
		</div>
        <input type="hidden" id="uploadNotice" name="uploadNotice" value="${teamSpaceInfo.uploadNotice}"/>
        <input type="hidden" id="token" name="token" value="${token}"/>
    </form>

    <a href="javascript:void(0);" onclick="submitTeamSpace()" class="weui-btn weui-btn_primary">确定</a>
    <a href="javascript:goBack()" class="weui-btn weui-btn_plain-default">取消</a>
</div>
<script type="text/javascript">
    $(document).ready(function () {
        var quota = ${teamSpaceInfo.spaceQuota};
        if (quota != -1) {
            var spaceQuota = formatFileSize("${teamSpaceInfo.spaceQuota}");
            $("#spaceQuotaInfo").text(spaceQuota);
        }
        else {
            $("#spaceQuotaInfo").text("<spring:message code='teamSpace.tip.noLimit'/>")
        }

        var spaceUsed = formatFileSize("${teamSpaceInfo.spaceUsed}");
        $("#usedQuotaInfo").text(spaceUsed);
        if($("#uploadNotice").attr('value')==='enable'){
            $("#uploadNoticeEnable").attr('checked',true)
            }else{
            $("#uploadNoticeEnable").attr('checked',false)
        }
        $("#uploadNoticeEnable").click(function () {
            if (this.checked) {
                $("#uploadNotice").val("enable");
            } else {
                $("#uploadNotice").val("disable");
            }
        });

        var $name = $("#name");
        $name.focus();

        // 需要验证团队空间同名情况
        $.validator.addMethod(
                "isNameNotExist",
                function (value, element, param) {
                    value = value.trim();
                    var ret = false;
                    $.ajax({
                        type: "POST",
                        async: false,
                        url: "${ctx}/teamspace/checkSameName?" + new Date().toString(),
                        data: $("#editTeamSpaceForm").serialize(),
                        success: function (data) {
                            if (typeof(data) == 'string' && data.indexOf('<html>') != -1) {
                                window.location.href = "${ctx}/logout";
                                return;
                            }
                            ret = data;
                        }
                    });
                    return !ret;
                }
        );

        $("#editTeamSpaceForm").validate({
            rules: {
                name: {
                    required: true,
                    isNameNotExist: true,
                    rangelength: [1, 64]
                }
            },
            messages: {
                name: {
                    required: "<spring:message code='file.errorMsg.nameRequired'/>",
                    isNameNotExist: "<spring:message code='teamSpace.name.exist'/>"
                }
            },
            // 不采用输入时验证，会多次请求后台，改用提交时验证
            // onkeyup:function(element) {$(element).valid()},
            onsubmit: true,
            onfocusout: false
        });

        $name.keydown(function (event) {
            if (event.keyCode == 13) {
                if (window.event) {
                    window.event.cancelBubble = true;
                    window.event.returnValue = false;
                } else {
                    event.stopPropagation();
                    event.preventDefault();
                }
            }
        })
    });

    function submitTeamSpace() {
        var $name = $("#name");
        $name.val($name.val().trim());

        var $editTeamSpaceForm = $("#editTeamSpaceForm");
        var params={
            'name':$name.val(),
            'description':$("#description").val()
        }
        $.ajax({
            type: "PUT",
            url: host + "/ufm/api/v2/teamspaces/"+ ${teamSpaceInfo.id},
            data: JSON.stringify(params),
            error: function (request) {
                $.toast("<spring:message code='operation.failed'/>", "cancel");
            },
            success: function () {
                if (typeof(data) == 'string' && data.indexOf('<html>') != -1) {
                    window.location.href = "${ctx}/logout";
                    return;
                }
                $.toast("<spring:message code='operation.success'/>");
                goBack();
            }
        });
    }

    function goBack() {
        window.location.href = "${ctx}/teamspace";
    }
</script>
</body>
</html>
