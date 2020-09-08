<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="../common/include.jsp" %>
    <link rel="stylesheet" type="text/css" href="${ctx}/static/skins/default/css/teamSpace/addTeamSpace.css"/>
    <title>创建新的协作空间</title>
</head>
<body style="font-size: 0.75rem;">

<div class="found-space">
	<div class="found-space-header"></div>
	<form id="creatTeamSpaceForm">
		<input type="hidden" id="token" name="token" value="<c:out value='${token}'/>"/>
        <input type="hidden" id="uploadNotice" name="uploadNotice" value="disable"/>
		<div class="found-space-content">
			<ul>
				<li>
					<p>名称：</p> <input class="found-space-input" id="name" name="name" placeholder="请输入协作空间名称">
				</li>
				<%--<li>--%>
					<%--<p>上传文件通知</p> --%>
					<%--<input id="uploadNoticeEnable" name="uploadNoticeEnable" class="weui-switch" type="checkbox">--%>
				<%--</li>--%>
				<li>
					<p>协作空间说明：(最大限制为255个字)</p>
				</li>
				<textarea rows="4" cols="20" placeholder="请输入协作空间说明信息" id="description" name="description"></textarea>
			</ul>
		</div>
	</form>
	<a href="javascript:void(0);" onclick="submitTeamSpace()" class="found-space-confirm">确定</a>
    <a href="javascript:goBack()" class="found-space-cancel">取消</a>
</div>
<script type="text/javascript">
    $(document).ready(function () {
        var $name = $("#name");
        $name.focus();

        $.validator.addMethod(
                "isNameNotExist",
                function (value, element, param) {
                    value = value.trim();
                    var ret = false;
                    $.ajax({
                        type: "POST",
                        async: false,
                        url: "${ctx}/teamspace/checkSameName?" + new Date().toString(),
                        data: $("#creatTeamSpaceForm").serialize(),
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

        $("#creatTeamSpaceForm").validate({
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
            //onkeyup:function(element) {$(element).valid()},
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
        });

        $("#uploadNoticeEnable").click(function () {
            if (this.checked) {
                $("#uploadNotice").val("enable");
            } else {
                $("#uploadNotice").val("disable");
            }
        });
    });

    function addUserMember(e, teamId, loginName, cloudUserId, email) {
        var item = "[user]" + loginName + "[" + cloudUserId + "]" + email;
        addMember(teamId, item, function(r) {
            if(r === "ok") {
                $(e).addClass("operation-success");
            } else {
                $(e).addClass("operation-fail");
            }
        });
    }

    function submitTeamSpace() {
        $.showLoading()
        var name = $("#name").val().trim();

        if(name == '') {
            $.toptip('请输入空间名称.');
            return;
        }
        var params={
            'name':$("#name").val(),
            'description':$("#description").val()

        }
        $.ajax({
            type: "POST",
            url: host+"/ufm/api/v2/teamspaces",
            data: JSON.stringify(params),
            error: function (request) {
            	
                var status = request.status;
                if (status == 507) {
                    $.alert("<spring:message code='teamSpace.error.exceedMaxSpace'/>");
                } else if (status == 403) {
    $.alert("<spring:message code='teamSpace.error.forbiddenAdd'/>");
                } else if(status == 400){
                    $.toast("操作失败",1000)
                }else{
    $.alert("<spring:message code='operation.failed'/>");
                }
            },
            success: function () {
                $.hideLoading();
                $.toast("创建成功");
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
