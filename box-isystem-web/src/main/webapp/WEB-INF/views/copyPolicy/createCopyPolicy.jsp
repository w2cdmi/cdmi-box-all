<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page import="com.huawei.sharedrive.isystem.util.CSRFTokenManager"%>
<%@ taglib prefix="cse" uri="http://cse.huawei.com/custom-function-taglib"%>  
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<%
    request.setAttribute("token",
					CSRFTokenManager.getTokenForSession(session));
%>
<!DOCTYPE html>
<html>
<head>

<link href="${ctx}/static/zTree/zTreeStyle.css" rel="stylesheet" type="text/css">
<%@ include file="../common/common.jsp"%>
<script src="${ctx}/static/zTree/jquery.ztree.all-3.5.min.js"	type="text/javascript"></script>
</head>
<body>
	<div class="pop-content pop-content-en">
		<div class="form-con">
			<form class="form-horizontal" id="CreateCopyPolicy"
				name="CreateCopyPolicy">
				<div class="control-group">
					<label class="control-label" for=""><em>*</em> <spring:message
							code="common.title" /> :</label>
					<div class="controls">
						<input type="text" id="name" name="name" class="span4" /> 
						<span class="validate-con bottom"><div></div></span>
					</div>
				</div>
				<div class="control-group">
	        	<label class="control-label" for=""><spring:message code="plugin.server.descrtion"/>：</label>
	            <div class="controls">
	                <textarea id="description" name="description" rows="3" maxlength="255" cols="20"  class="span4"></textarea>
	                <span class="validate-con bottom"><div></div></span>
	            </div>
	        </div>
				<div class="control-group">
					<label class="control-label" for=""><em>*</em> <spring:message
							code="manage.app.id" /> :</label>
					<div class="controls dropdown">
						<select class="span4" id="appId" name="appId">
								<option value="-1"></option>
        					<c:forEach items="${authApps}" var="app">
        						<option value="${cse:htmlEscape(app.authAppId)}">${cse:htmlEscape(app.authAppId)}</option>
        					</c:forEach>
						</select>
						<span class="validate-con bottom"><div></div></span>
					</div>
				</div>

				<div class="control-group">
					<label class="control-label"><em>*</em><spring:message
							code="copyPolicy.policy.content" /> :</label>
					<div class="controls">
 						<label class="radio inline" for="input"><input type="radio" name="type"  value="0"  onclick="hidenspace()" checked="checked"/><spring:message code="copyPolicy.policy.type.all" /></label>
       					 <label class="radio inline" for="input"><input type="radio" name="type"  value="1"  onclick="showspace()"/><spring:message code="copyPolicy.policy.type.little" /></label>
					</div>
					<label class="control-label"></label>
					<div class="controls" id="spaces">
 						<label class="radio inline" for="input"><input type="radio" name="state"  value="0" checked="checked"/><spring:message code="copyPolicy.policy.user.space" /></label>
       					<label class="radio inline" for="input"><input type="radio" name="state"  value="1"/><spring:message code="copyPolicy.policy.team.space" /></label>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label"><em>*</em><spring:message
							code="copyPolicy.policy.execute.time" /> :</label>
					<div class="controls">
 						<label class="radio inline" for="input"><input type="radio" name="exeType"  value="0" onclick="javascript:hiddenTime()" checked="checked"/><spring:message code="copyPolicy.policy.timely.copy" /></label>
       					 <label class="radio inline" for="input"><input type="radio" name="exeType"  value="1" onclick="javascript:showTime()"/><spring:message code="copyPolicy.policy.time.copy" /></label>
					</div>
				</div>
				<div class="control-group" id="timeH" hidden="hidden">
					<label class="control-label" for=""><em>*</em><spring:message
							code="plugin.monitor.cycle" /> :</label>
					<div class="controls">
							<span class="input-group-addon">
							<select class="span1"  id="startHH"></select><select id="startMM" class="span1"></select>
							</span>
							<spring:message code="log.till"/>
							<span class="input-group-addon">
								<select id="endHH" class="span1"></select><select id="endMM"  class="span1"></select>
								<span class="validate-con bottom"><div></div></span>
							</span>
							
						
					</div>
				</div>
				
				<div class="control-group">
					<label class="control-label" for=""><spring:message
							code="copyPolicy.copy.rule" /> :</label>
					<div class="controls">
						<table  id="addText" class="table table-bordered">
							<tbody><thead></thead></tbody>
						</table>
								<a onclick="changeFrom(true)"><spring:message code="copyPolicy.add.rule"/></a>
					</div>
				</div>
				<div class="control-group">
					<div class="controls">
					<button  type="button" class="btn btn-primary" id="submitCreateCopyPolicybtn" onclick="submitCreateCopyPolicy()"><spring:message	code="common.save" /></button>
					<button  type="button" class="btn" onclick="cancleWin()"><spring:message	code="button.cancel" /></button>
					</div>
				</div>
				
				
			
				<input	type="hidden" id="lstCopyPolicyDataSiteInfo"  value="" /> 
				<input type="hidden" name="token" value="${cse:htmlEscape(token)}" />
				
		</form>
		
		<form class="form-horizontal" id="selectDC"
				name="selectDC" style="display: none">
				<div class="control-group">
					<label class="control-label"><em>*</em><spring:message
							code="copyPolicy.policy.crc.header" /> :</label>
					<div class="controls">
						<input id="srcValue" type="text" readonly value=""
							class="span4" onclick="showSrc();" />
						<span class="validate-con bottom"><div></div></span>
						<div id="srcContent" class="select-tree-node">
							<ul id="src" class="ztree"></ul>
						</div>
						
					</div>
				</div>
				<div class="control-group">
					<label class="control-label"><em>*</em><spring:message
							code="copyPolicy.policy.dest.header" /> :</label>
					<div class="controls">
						<input id="drcValue" type="text" readonly value=""
							class="span4" onclick="showDrc();" />
						<div id="drcContent" class="select-tree-node">
							<ul id="drc" class="ztree"></ul>
						</div>
						<span class="validate-con bottom"><div></div></span>
					</div>
				</div>

				<div class="control-group">
					<div class="controls">
					<button type="button" class="btn btn-primary" onclick="addCopySrc(false)"><spring:message	code="common.save" /></button>
					<button type="button" class="btn" onclick="addCopySrc(true)"><spring:message	code="button.cancel" /></button>
					</div>
				</div>
				
		</form>
	</div>

	</div>
	<script type="text/javascript">
	var nodes="";
	
	
	
	$.validator.addMethod(
			   "appValidator", 
			   function(value, element) {   
		          		if(value!=-1&&value!="")
		          		{
		          	
		          			return true;
		          		}
		           return false;   
		       }, 
		       $.validator.format('<spring:message code="copyPolicy.appId.validate"/>')
	); 
	
	function initSelect()
	{
		 var startH=document.getElementById("startHH");
		 var startM=document.getElementById("startMM");
		 var endH=document.getElementById("endHH");
		 var endM=document.getElementById("endMM");
		 for(var i=0;i<24;i++)
		 {
			 var optionS=document.createElement('option');
			 var optionE=document.createElement('option');
			 var value;
			 if(i<10)
			{
				 value="0"+i
			}else{
				value=""+i
			}
			 optionS.text=value;
			 optionE.text=value;
			 startH.options.add(optionS);
			 endH.options.add(optionE);
		 }
		 for(var i=0;i<60;i++)
		 {
			 var optionS=document.createElement('option');
			 var optionE=document.createElement('option');
			 var value;
			 if(i<10)
			{
				 value="0"+i
			}else{
				value=""+i
			}
			 optionS.text=value;
			 optionE.text=value;
			 startM.options.add(optionS);
			 endM.options.add(optionE);
		 }

	}
	
	$(document).ready(function(){
	
		hiddenTime();
		hidenspace();
		initSelect();
		
		$("#CreateCopyPolicy").validate({ 
			rules: { 
					name:{
						required:true, 
						maxlength:255
				   },
				   description:{
					   maxlength:512
				   },
				   appId:{
					   appValidator:true
				   }
			}
	    });
		if(nodes=="")
		{
			$("#addText").hide();
		}
	});
	function showTime()
	{
		document.getElementById("timeH").style.display="block";
	}
	function hiddenTime()
	{
		document.getElementById("timeH").style.display="none";
	}
	function setApp(authAppId)
	{
		document.getElementById("appId").value=authAppId;
	}
	
		var setting_master = {
			view : {
				selectedMulti : false
			},
			async : {
				enable : true,
				url : "${ctx}/mirror/copyPolicy/listTreeNode",
				autoParam : [ "id" ],
				otherParam : ["token","${cse:htmlEscape(token)}" ],
				dataFilter : masterFilter
			},
			data : {
				key : {
					name : "name",
					checked : "checked",
					isParent : "isParent"
				}
			},
			callback : {
				onCheck : onCheck
			},
			check : {
				enable : true,
				chkStyle : "radio",
				radioType : "all"
			}
		};
		function masterFilter(treeId, parentNode, responseData) {
			if (responseData) {
				for ( var i = 0; i < responseData.length; i++) {
					if (responseData[i].isParent) {
						responseData[i].nocheck = true;
					}
				}
			}
			return responseData;
		}

		function onClick(e, treeId, treeNode) {
			return false;
		};
		var lstCopyPolicyDataSiteInfo=new Array();
		function CopyPolicySiteInfo()
		{
		 this.srcRegionId = 0;
		 this.srcResourceGroupId = 0;
		 this.destRegionId = 0;
		 this.destResourceGroupId = 0;	
		}
		var copyPolicySiteInfo;
		function onCheck(e, treeId, treeNode) {
			
			var zTree = $.fn.zTree.getZTreeObj("src"), nodes = zTree
					.getCheckedNodes(true), v = "", dssId;
			if(treeId!="drc"){
				copyPolicySiteInfo =new CopyPolicySiteInfo();
			for ( var i = 0, l = nodes.length; i < l; i++) {
				v += nodes[i].name + ",";
				dssId = nodes[i].id;
			}
			if (v.length > 0)
				v = v.substring(0, v.length - 1);
			var srcValue = $("#srcValue");
			srcValue.attr("value",v);
			
			}else
			{
				zTree = $.fn.zTree.getZTreeObj("drc"), nodes = zTree
				.getCheckedNodes(true)
				for ( var i = 0, l = nodes.length; i < l; i++) {
					v += nodes[i].name + ",";
					dssId = nodes[i].id;
				}
				if (v.length > 0)
					v = v.substring(0, v.length - 1);
				var drcValue = $("#drcValue");
				drcValue.attr("value",v);
			}
		}

		function showSrc() {
			var cityObj = $("#srcValue");
			var cityOffset = $("#srcValue").offset();
			$("#srcContent").css({
				left : cityOffset.left + "px",
				top : cityOffset.top + cityObj.outerHeight() + "px"
			}).slideDown("fast");

			$(document).bind("mousedown", onBodyDown);
		}
		function showDrc() {
			var cityObj = $("#drcValue");
			var cityOffset = $("#drcValue").offset();
			$("#drcContent").css({
				left : cityOffset.left + "px",
				top : cityOffset.top + cityObj.outerHeight() + "px"
			}).slideDown("fast");

			$(document).bind("mousedown", onBodyDown);
		}
		function hideMenu() {
			$("#srcContent").fadeOut("fast");
			$("body").unbind("mousedown", onBodyDown);
			$("#drcContent").fadeOut("fast");
			$("body").unbind("mousedown", onBodyDown);
		}
		function onBodyDown(event) {
			if (!(event.target.id == "drcContent" || event.target.id == "srcContent" || $(event.target)
					.parents("#srcContent").length > 0||$(event.target)
					.parents("#drcContent").length > 0)) {
				hideMenu();
			}
		}

		function submitCreateCopyPolicy() {
			document.getElementById("submitCreateCopyPolicybtn").disabled=true;
			
			if (!$("#CreateCopyPolicy").valid()) {
				document.getElementById("submitCreateCopyPolicybtn").disabled=false;
				return false;
			}
			if($('input:radio[name="exeType"]:checked').val()==1){
				var startTime=$("#startHH").val()+$("#startMM").val()
				var endTime=$("#endHH").val()+$("#endMM").val()
				if(endTime != "" && startTime >= endTime){
					handlePrompt("error",'<spring:message code="copyPolicy.handle.time.error"/>',null,60);
					document.getElementById("submitCreateCopyPolicybtn").disabled=false;
					return false;
				}
			}
			if(nodes=="")
			{
				handlePrompt("error",'<spring:message code="copyPolicy.rule.validate"/>');
				changeFrom(true)
				document.getElementById("submitCreateCopyPolicybtn").disabled=false;
				return false;
			}
			var exeStartAt=$("#startHH").find("option:selected").val()+":"+$("#startMM").find("option:selected").val()+":00";
			var exeEndAt=$("#endHH").find("option:selected").val()+":"+$("#endMM").find("option:selected").val()+":00";
			var lstCopyPolicy="["+nodes+"]";
			
			var copyPolicy="{\"name\":\""+$("#name").val()
					+"\",\"description\":\""+$("#description").val()
					+"\",\"appId\":\""+$("#appId").val()
					+"\",\"type\":"+ $('input:radio[name="type"]:checked').val()
					+",\"copyType\":1"
					+",\"state\":"+($('input:radio[name="state"]:checked').val()==undefined?0:$('input:radio[name="state"]:checked').val())
					+",\"exeType\":"+$('input:radio[name="exeType"]:checked').val()
					+",\"exeStartAt\":\""+exeStartAt
					+"\",\"exeEndAt\":\""+exeEndAt
					+"\"}";
			$.ajax({   
					    type : "POST",
						url : "${ctx}/mirror/copyPolicy/createCopyPolicy",
						data:{copyPolicy:copyPolicy,lstCopyPolicyDataSiteInfo:lstCopyPolicy,token:'${cse:htmlEscape(token)}'},
						error : function(request) {
							if(request.responseText=="BadCopyPolicyInfo")
							{
								handlePrompt("error",
								'<spring:message code="copyPolicy.handle.info.error"/>');
							}
							else if(request.responseText=="BadCopyPolicyInfoConflict"){
								handlePrompt("error",
								'<spring:message code="copyPolicy.handle.info.conflict"/>');
							}else{
								handlePrompt("error",
									'<spring:message code="common.createFail"/>');
							}
							document.getElementById("submitCreateCopyPolicybtn").disabled=false;
						},
						success : function() {
							top.ymPrompt.close();
							top.handlePrompt("success",
											'<spring:message code="common.createSuccess"/>');
							top.window.frames[0].location = "${ctx}/mirror/copyPolicy/list";
							document.getElementById("submitCreateCopyPolicybtn").disabled=false;
						}
					});
		}
		function hidenspace()
		{
			   var po=document.getElementById("spaces").style.display="none";


		}
		function showspace()
		{
			   var po=document.getElementById("spaces").style.display="block";


		}
		function changeFrom(hidden)
		{
			var drcValue = $("#drcValue");
			drcValue.attr("value","");
			var srcValue = $("#srcValue");
			srcValue.attr("value","");
			 var policyForm=document.getElementById("CreateCopyPolicy");
			 var selectDC=document.getElementById("selectDC");
			if(hidden){
				$.fn.zTree.init($("#src"), setting_master);
				$.fn.zTree.init($("#drc"), setting_master);
				policyForm.style.display="none";
			  	selectDC.style.display="block";
			}else{
				policyForm.style.display="block";
			  	selectDC.style.display="none";
			}
		}
		function addCopySrc(isChangeFrom)
		{
			var srcTree = $.fn.zTree.getZTreeObj("src");
			var drcTree = $.fn.zTree.getZTreeObj("drc");
		
			var srcnodes =srcTree.getCheckedNodes(true);
			var drcnodes =drcTree.getCheckedNodes(true);
			if(!isChangeFrom){
				if((srcnodes.length==0)||(drcnodes.length==0))
				{
					handlePrompt("error","<spring:message code='copy.src.drc.message'/>");
					return;
				}
				var strRegin=srcnodes[0].getParentNode().id+"";
				var srcRG=srcnodes[0].id;
				var drcRegin=drcnodes[0].getParentNode().id+"";
				var drcRG=drcnodes[0].id;
				if(srcRG==drcRG)
				{
					handlePrompt("error","<spring:message code='copy.src.drc.message.same'/>");
					return;
				}
				var node=new String("{\"srcRegionId\":"+strRegin+",\"srcResourceGroupId\":"+srcRG+",\"destRegionId\":"+drcRegin+",\"destResourceGroupId\":"+drcRG+"}");
				var _len =strRegin+srcRG+drcRegin+drcRG;
				
				if(document.getElementById(_len)!=null)
				{
					handlePrompt("error","<spring:message code='copy.src.drc.equle.message'/>");
					return;
				}
				if(nodes==""||nodes==",")
				{
					nodes=node;
				}else
				{
					nodes=nodes+","+node;
				}
			     var tab = document.getElementById("addText");
				if(tab)
				{
					 var tr = tab.insertRow(0);
					  var td1 = tr.insertCell(0);
                      td1.innerHTML = $("#srcValue").val();
                      var td2 = tr.insertCell(1);
                      td2.innerHTML = '<spring:message code="copy.to"/>';
                      var td3 = tr.insertCell(2);
                      td3.innerHTML = $("#drcValue").val();
                      var td4 = tr.insertCell(3);
                      td4.innerHTML = "<input id="+_len+" type='button' value='<spring:message code="common.delete"/>'  onclick='deleteRow(this,"+node+");'>";
                      $("#addText").show();
				}
 				
			}

			 changeFrom(false);
		}
        function deleteRow(btn,node){ 
        	 var node=new String("{\"srcRegionId\":"+node.srcRegionId+",\"srcResourceGroupId\":"+node.srcResourceGroupId+",\"destRegionId\":"+node.destRegionId+",\"destResourceGroupId\":"+node.destResourceGroupId+"}");
			 if(nodes.search(node)!=-1)
			 {
				 if(nodes.search(","+node)!=-1)
				 {
					 nodes=nodes.replace(","+node,"");
				 }else if(nodes.search(node+",")!=-1)
				 {
					 nodes=nodes.replace(node+",","");
				 } else
				 {
					 nodes=nodes.replace(node,"");
				 }
			 }
            var tr = btn.parentNode.parentNode; 
            var tab = document.getElementById("addText");
            tab.deleteRow(tr.rowIndex);
    } 

		
	 var deltr =function(index,node){
		 var node=new String("{\"srcRegionId\":"+node.srcRegionId+",\"srcResourceGroupId\":"+node.srcResourceGroupId+",\"destRegionId\":"+node.destRegionId+",\"destResourceGroupId\":"+node.destResourceGroupId+"}");
			 if(nodes.search(node)!=-1)
			 {
				 if(nodes.search(","+node)!=-1)
				 {
					 nodes=nodes.replace(","+node,"");
				 }else
				 {
					 nodes=nodes.replace(node,"");
				 }
			 }
		     $("tr[id='"+index+"']").remove();
  
	}
	 function cancleWin()
	 {
		 top.ymPrompt.close();
	 }
	 
	</script>
</body>
</html>
