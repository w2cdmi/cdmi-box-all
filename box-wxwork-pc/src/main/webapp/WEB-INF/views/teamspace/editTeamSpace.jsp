<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page import="pw.cdmi.box.disk.utils.*" %>
<% request.setAttribute("token", CSRFTokenManager.getTokenForSession(session)); %>
<div class="found-space">
    <form class="form" id="editTeamSpaceForm">
        <input type="hidden" id="teamId" name="teamId" value="${teamSpaceInfo.id}"/>
        <div class="found-space-header"></div>


        <dl class="found-space-content">
                <dt>
                    <label><spring:message code='common.field.name'/>: </label>
                    <input class="found-space-input valid" type="text" id="name" name="name" maxlength="255" value="${teamSpaceInfo.name}" style="width: 340px" />
                </dt>
<%--
                <dt>
                    <label><spring:message code="teamSpace.label.uploadNotice"/></label>
                    <input style="height:14px;margin-top:17px;" id="uploadNoticeEnable" name="uploadNoticeEnable" class="weui-switch" type="checkbox">
                </dt>
--%>
                <dt>
                    <label><spring:message code='teamSpace.label.description'/>:（最大限制为255个字）</label>
                    <textarea name="description" style="resize:vertical;width: 100%;height: 100px" id="description">${teamSpaceInfo.description}</textarea>
                </dt>
                
        </dl>
        <input type="hidden" id="uploadNotice" name="uploadNotice" value="${teamSpaceInfo.uploadNotice}"/>
        <input type="hidden" id="token" name="token" value="${token}"/>
        <div class="form-control" style="text-align: right">
            <button type="button" id="cancel_button">取消</button>
            <button type="button" id="ok_button">确定</button>
        </div>
    </form>
    
</div>
    <script>
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
    </script>
