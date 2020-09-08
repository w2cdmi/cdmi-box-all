<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="pw.cdmi.box.disk.utils.CSRFTokenManager" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<% request.setAttribute("token", CSRFTokenManager.getTokenForSession(session));%>
<!DOCTYPE html>
<html>
<head>
<%@ include file="../common/common.jsp"%>
<link href="${ctx}/static/jqueryUI-1.9.2/jquery-ui.min.css" rel="stylesheet"type="text/css" />
<link href="${ctx}/static/skins/default/css/layout.css" rel="stylesheet"/>
<link href="${ctx}/static/skins/qrcode/robot.css" rel="stylesheet"type="text/css" />
<script src="${ctx}/static/template/template-web.js"type="text/javascript"></script>
<script src="${ctx}/static/jqueryUI-1.9.2/jquery-ui.min.js" type="text/javascript"></script>
<script src="${ctx}/static/js/public/common.js" type="text/javascript"></script>
</head>
<body>
   <%@ include file="../common/header.jsp"%>
   <div class="body">
    <div class="body-con clearfix">
	<div class="robot_content" id="robot_content" style="display: none">
		<div class="robot_con_left" id="robot_con_left" style="margin: 0 auto;">
			<div class="robot_staus" > 
				<img id="robotImage" src="${ctx}/static/skins/qrcode/images/robot_static.png" alt="" />
				<p id="message" style="margin-top: 10px">
					<%-- ${wxName} 机器人<span>正在运行</span> --%>
				</p>
			</div>
			<div class="change_status">
				<div class="robot_stop" onclick="stopRobot()" id="robot_stop">
					<img src="${ctx}/static/skins/qrcode/images/robot_stop.png" alt="" />
				</div>
			</div>
		</div>
		<div class="robot_con_right" id="robot_con_right" style="margin:0px 100px 0 0;">
			<div class="robot_set_con">
				<div class="robot_set_title">
					<p><spring:message code="robot.setting"/></p>
					<p><spring:message code="robot.desc"/></p>
				</div>
				<div class="person_con">
					<p><spring:message code="robot.user"/></p>
					<label><input type="checkbox" id="userFile" name="userConfig"/>&nbsp;&nbsp;<spring:message code="document"/></label> <label><input
						type="checkbox" id="userVideo" name="userConfig"/>&nbsp;&nbsp;<spring:message code="video"/></label> <label><input
						type="checkbox" id="userImage" name="userConfig"/>&nbsp;&nbsp;<spring:message code="image"/></label>
				</div>
				<div class="group_con">
					<p><spring:message code="robot.group"/></p>
					<label><input type="checkbox" id="groupFile" name="groupConfig"/>&nbsp;&nbsp;<spring:message code="document"/></label> <label><input
						type="checkbox" id="groupVideo" name="groupConfig"/>&nbsp;&nbsp;<spring:message code="video"/></label> <label><input
						type="checkbox" id="groupImage" name="groupConfig"/>&nbsp;&nbsp;<spring:message code="image"/></label>
				</div>
			</div>
			<div class="blacklist_con">
				<div class="black_title">
					<p><spring:message code="robot.black"/></p>
					<p><spring:message code="robot.black.desc"/></p>
				</div>
				<div class="blacklist_company" id="configs">
					
				</div>
				<div class="add_button" id="add_button" style="margin-left:56px">
						<button onclick="showAddGroup()"><spring:message code="robot.add.group"/></button>
				</div>
				  <script id="configTemplate" type="text/html">
                       {{each list as config}}
                          <div class="company_info">
						     <span>{{config.name}}</span> <a href="#" robotId="{{config.robotId}}" name="{{config.name}}" onclick="deleteConfig(this)" > <spring:message code="button.delete"/> </a>
					      </div>
                       {{/each}}
                   </script>
			</div>
		</div>
	</div>
	
	 <!-- show login info -->
        <div class="modal hide" id="addgroup" tabindex="-1" role="dialog"
             aria-hidden="true" data-backdrop="static">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        onclick="closeGroupModal()" aria-hidden="true">&times;</button>
                <h4>
                                                <spring:message code="robot.group.name"/> 
                </h4>
            </div>
            <div class="modal-body" style="margin-left:100px;">
                    <input style="width: 240px;margin-top: 9px;" type="text" name="groupName" id="groupName"/>
					<button type="submit" class="btn btn-default" onclick="addBlackConfig()"><spring:message code="teamSpace.button.btnAdd"/></button>
            </div>
        </div>
        </div>
        </div>
</body>
<script type="text/javascript">
var token='${token}';
var wxUin='${uin}';
var robotId='${robotId}';
var isRun='${isRun}';
var wxName='${wxName}';
var interval=setInterval('myrefresh()',5000);

function myrefresh(){
	checkIsRun();
}

function checkIsRun(){
	 var premater={
			 "token":token,
	 };
	 $.ajax({
        type: "GET",
        url: "${ctx}/wxRobot/checkRobotStatus",
        data: premater,
        error: function (request) {
        	window.location.reload(); 	
        },
        success: function (data) {
        	if(data!="notsupport"){
        		
        		if(isRun!=data){
            		window.location.reload(); 	
            	}
        	}else{
        		 $("#message").text("当前微信不支持机器人");
        	     $("#robotImage").attr("src","https://login.weixin.qq.com/qrcode/${uuid}==");
        	     $("#robot_stop").css("display","none");
        	     $("#robot_con_left").css('width','100%');
        	 	 $("#robot_con_right").css('display','none');
        	     $("#robot_content").css("display","block");
        	}
        	
        }
    });
}

$(function(){
	 loadSysSetting("${ctx}",'<spring:message code="common.language1" />');

	 if(isRun=='false'){
		 $("#message").text('<spring:message code="robot.start"/>');
	     $("#robotImage").attr("src","https://login.weixin.qq.com/qrcode/${uuid}==");
	     $("#robot_stop").css("display","none");
	     $("#robot_con_left").css('width','100%');
	 	 $("#robot_con_right").css('display','none');
	     $("#robot_content").css("display","block");
	 }else if(isRun=='true'){
		 
		 $("#message").text(wxName+' <spring:message code="robot.runing"/>');
		 $("#robotImage").attr("src","${ctx}/static/skins/qrcode/images/robot_static.png");
		 $("#robot_stop").css("display","block");
		 $("#robot_con_right").css('display','block');
		 $("#robot_con_left").css('width','50%');
		 $("#robot_content").css("display","block");
		 listRobotConfig(); 
	 } else if(isRun=='notsupport'){
		 
		 $("#message").text("当前微信不支持机器人");
	     $("#robotImage").attr("src","https://login.weixin.qq.com/qrcode/${uuid}==");
	     $("#robot_stop").css("display","none");
	     $("#robot_con_left").css('width','100%');
	 	 $("#robot_con_right").css('display','none');
	     $("#robot_content").css("display","block");
	 }
});

function stopRobot(){

	 var premater={
			 "token":token,
			 "robotId":robotId
	 };
	 $.ajax({
        type: "POST",
        url: "${ctx}/wxRobot/stopRobot?robotId="+robotId,
        data: premater,
        error: function (request) {
       	 console.log(request);
        },
        success: function (data) {
        	window.location.reload();
        }
    });
}

function listRobotConfig(){
	 var premater={
			 "token":token
	 };
	 $.ajax({
         type: "POST",
         url: "${ctx}/wxRobot/listWxRobotConfig?robotId="+'${robotId}',
         data: premater,
         error: function (request) {
        	 console.log(request);
         },
         success: function (data) {
        	var configs = {
        	            list: []
        	        };
        	for(var i=0;i<data.length;i++){
        		if(data[i].type==1||data[i].type==2){
        			setCheckBoxConfig(data[i])
        		}
				if(data[i].type==3){
					configs.list.push(data[i]);
        		}
        	}
            var html=template('configTemplate',configs);
            $("#configs").empty();
            $("#configs").append(html);
         }
     });
}

function setCheckBoxConfig(value){
	var type=value.type==1?"user":"group";
	if(value.config.file){
		$("#"+type+"File").prop("checked",true);
	}
	if(value.config.image){
		$("#"+type+"Image").prop("checked",true);
	}
	if(value.config.video){
		$("#"+type+"Video").prop("checked",true);
	}
}

function listGroupsName(){
	 var premater={
			 "token":token
	 };
	 $.ajax({
         type: "POST",
         url: "${ctx}/wxRobot/listWxRobotGroups?uin="+wxUin,
         data: premater,
         error: function (request) {
        	 console.log(request);
         },
         success: function (data) {
       	    $("#addgroup").show();
            $("#groupName").autocomplete({
  		      source: data
  		    });
         }
     });
}

function showAddGroup(){
	 listGroupsName();
}

function addBlackConfig(){
	 var premater={
			 "token":token,
			 'robotId':robotId,
			 'name':$("#groupName").val(),
			 'value':0,
			 'type':3
	 };
	 if($("#groupName").val()==""){
		 return;
	 }
	 $.ajax({
         type: "POST",
         url: "${ctx}/wxRobot/createConfig",
         data: premater,
         error: function (request) {
        	 console.log(request);
         },
         success: function (data) {
        	 listRobotConfig();
        	 closeGroupModal();
         }
     });
}

function updateWxRobotConfig(typeString,type){
	 var premater={
			 "token":token,
			 'robotId':robotId,
			 'value':getConfigValue(typeString),
			 'type':type
	 };
	 $.ajax({
        type: "POST",
        url: "${ctx}/wxRobot/updateConfig",
        data: premater,
        error: function (request) {
       	 console.log(request);
        },
        success: function (data) {
         
        }
    });
}

function deleteConfig(th){
	 var premater={
			 "token":token,
			 'robotId':$(th).attr("robotId"),
			 'name':$(th).attr("name"),
			 'type':3
	 };
	 $.ajax({
       type: "POST",
       url: "${ctx}/wxRobot/deleteConfig",
       data: premater,
       error: function (request) {
      	 console.log(request);
       },
       success: function (data) {
    	   listRobotConfig();
       }
   });
}

function getConfigValue(type){
	var value=parseValue(type+"Video")+parseValue(type+"Image")+parseValue(type+"File");
    return value;
}

function parseValue(id){
	return $("#"+id).get(0).checked==true?"1":"0";
}

function closeGroupModal() {
    $("#addgroup").hide();
    $("#groupName").val("");
}

$("input[type=checkbox][name=userConfig]").click(function(){
	updateWxRobotConfig("user",1);
})

$("input[type=checkbox][name=groupConfig]").click(function(){
	updateWxRobotConfig("group",2);
})

</script>
</html>
