<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="cse" uri="http://cse.huawei.com/custom-function-taglib"%>  
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
<%@ include file="../common/common.jsp"%>
<script src="${ctx}/static/js/public/JQbox-hw-switchButton.js" type="text/javascript"></script>
</head>
<body>

<div class="sys-content">
	<div class="alert clearfix">
		<span id="switchButton" class="pull-right"></span>
		<div class="pull-left">
			<p><strong><spring:message code="copyPolicy.policy.message"/></strong></p> 
			<p> <spring:message code="copyPolicy.policy.description"/></p>
		</div>
		<form id="form1" action="">	
			<input type="hidden" id="globalEnable" name="globalEnable" value="${globalEnable}"/>
			<input type="hidden" id="token" name="token" value="${cse:htmlEscape(token)}"/>
		</form>
	</div>
	<div class="clearfix"  >
		<div class="pull-left">
    		<h5><spring:message code="copyPolicy.policy.h5" /></h5>
    	</div>
    </div>
    <div class="clearfix">
    	<div class="pull-left">
	    	<button type="button" class="btn btn-primary" onClick="createCopyPolicy()"><i class="icon-add"></i><spring:message code="policy.common.create" /></button>
	    	<button type="button" class="btn btn-primary-small" onClick="updateCopyPolicy(0,0)"><spring:message code="policy.common.start" /></button>
	    	<button type="button" class="btn btn-primary-small" onClick="updateCopyPolicy(1,0)"><spring:message code="job.state.stop" /></button>
        </div>
    </div>
    <div class="table-con clearfix">
        <table class="table table-bordered table-striped">
          <thead>
            <tr>
            	<th width="4%"><input type="checkbox" id="checkall" name="checkall" /></th>
                <th ><spring:message code="common.title"/></th>
                <th ><spring:message code="manage.app.id"/></th>
                <th ><spring:message code="plugin.server.descrtion"/></th>
                <th ><spring:message code="common.status"/></th>
                
                <th><spring:message code="manage.create.time"/></th>
                <th width="17%"><spring:message code="common.operation"/></th>
            </tr>
          </thead>
          <tbody>
          <c:forEach items="${copyPlolicies}" var="cp">
             <c:if test="${cp.copyType !=4}">
             <tr >
            	<td ><input type="checkbox" id="${cse:htmlEscape(cp.id)}" name="checkname" value="${cse:htmlEscape(cp.id)}"/></td>
                <td   title="${cse:htmlEscape(cp.name)}">${cse:htmlEscape(cp.name)}</td>
                <td   title="${cse:htmlEscape(cp.appId)}">${cse:htmlEscape(cp.appId)}</td>
                <td   title="${cse:htmlEscape(cp.description)}">
                
                   <c:if test="${empty cp.description }">
                      		  -
                    </c:if>
                    <c:if test="${not empty cp.description}">
                        		${cse:htmlEscape(cp.description)}
                    </c:if>
                
                </td>
                <td >
                    <c:if test="${cp.state == 0}">
                      		  	<spring:message code="common.normal" />
                    </c:if>
                    <c:if test="${cp.state == 1}">
                        		<spring:message code="job.state.stop" />
                    </c:if>
                <td >
                <fmt:formatDate value="${cp.createdAt}" pattern="yyyy-MM-dd HH:mm"/>
                </td>
                <td >
                     <button class="btn" type="button" onClick="modifyCopyPolicy(${cse:htmlEscape(cp.id)})"/><spring:message code="common.modify"/></button>
					 <button class="btn" type="button" onClick="deleteCopyPolicy(${cse:htmlEscape(cp.id)})"/><spring:message code="common.delete"/></button>
                </td>
            </tr>
              </c:if>
            </c:forEach>
            <c:if test="${empty copyPlolicies}">  
            <tr>
                 <td colspan="7" style="text-align:center">
                       <spring:message code="copyPolicy.policy.add.message"/><a href="javascript:createCopyPolicy()"><spring:message code="common.create"/></a>。
                 </td>
            </tr>
            </c:if>
          </tbody>
        </table>
    </div>
    <div class="alert clearfix">
		<span id="switchButton1" class="pull-right"></span>
		<div class="pull-left">
			<p><strong><spring:message code="timeconfig.message"/></strong></p> 
			<p> <spring:message code="timeconfig.description"/></p>
		</div>
		<form id="form2" action="">	
			<input type="hidden" id="timeconfigEnable" name="timeconfigEnable" value="${timeconfigEnable}"/>
			<input type="hidden" id="token" name="token" value="${cse:htmlEscape(token)}"/>
		</form>
	</div>
    <div class="clearfix"  >
		<div class="pull-left">
    		<h5><spring:message code="timeconfig.h5" /></h5>
    	</div>
    </div>
    
    <div class="clearfix">
    	<div class="pull-left">
	    	<button type="button" class="btn btn-primary" onClick="createTimeConfig()"><i class="icon-add"></i><spring:message code="copyPolicy.timeconfig.add.title" /></button>
        </div>
    </div>
    <div class="table-con clearfix">
        <table class="table table-bordered table-striped">
          <thead>
            <tr>
                <th ><spring:message code="timeconfig.startAt"/></th>
                <th ><spring:message code="timeconfig.endAt"/></th>
                <th width="17%"><spring:message code="common.operation"/></th>
            </tr>
          </thead>
          <tbody>
          <c:forEach items="${timeConfigs}" var="tc">
             <tr >
                <td   title="${cse:htmlEscape(tc.exeStartAt)}">${cse:htmlEscape(tc.exeStartAt)}</td>
                <td   title="${cse:htmlEscape(tc.exeEndAt)}">${cse:htmlEscape(tc.exeEndAt)}</td>
                <td>
                <button class="btn" type="button" onClick="deleteTimeConfig('${cse:htmlEscape(tc.uuid)}')"/><spring:message code="common.delete"/></button>
                </td>

            </tr>
            </c:forEach>
          </tbody>
        </table>
    </div>
    
</div>
</body>
<script type="text/javascript">
$(function(){
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
	getAccessConfigSwitch();
	getTimeConfigSwitch();
})

function getAccessConfigSwitch(){
	var isEnable = $("#globalEnable").val();
	var switchFlag = (isEnable == "true" ? true : false);
	var switchOpts = $("#switchButton").comboSwitchButton({
		onText : "<spring:message code='common.already.open'/>",
		offText : "<spring:message code='common.already.close'/>",
		defaultSwitchOn : switchFlag,
		onEvent: function(){
			$("#globalEnable").val("true");
			var url = "${ctx}/mirror/copyPolicy/globalEnable";
			$.ajax({
				type : "POST",
				url : url,
				data : $('#form1').serialize(),
				error : function(request) {
					$("#switchButton").resetSwitchButton(switchOpts);
					top.handlePrompt("error",'<spring:message code="authorize.status.modified.fail" />');
				},
				success : function(data) {
					top.handlePrompt("success",'<spring:message code="authorize.status.modified.success"/>');
				}
			});
			
		},
		offEvent: function(){
			$("#globalEnable").val("false");
			var url = "${ctx}/mirror/copyPolicy/globalEnable";
			$.ajax({
				type : "POST",
				url : url,
				data : $('#form1').serialize(),
				error : function(request) {
					$("#switchButton").resetSwitchButton(switchOpts);
					top.handlePrompt("error",'<spring:message code="authorize.status.modified.fail" />');
				},
				success : function(data) {
					top.handlePrompt("success",'<spring:message code="authorize.status.modified.success"/>');
				}
			});			
		}
	})
}		 

function getTimeConfigSwitch(){
	var isEnable = $("#timeconfigEnable").val();
	var switchFlag = (isEnable == "true" ? true : false);
	var switchOpts = $("#switchButton1").comboSwitchButton({
		onText : "<spring:message code='common.already.open'/>",
		offText : "<spring:message code='common.already.close'/>",
		defaultSwitchOn : switchFlag,
		onEvent: function(){
			$("#timeconfigEnable").val("true");
			var url = "${ctx}/mirror/timeConfig/timeconfigEnable";
			$.ajax({
				type : "POST",
				url : url,
				data : $('#form2').serialize(),
				error : function(request) {
					$("#switchButton1").resetSwitchButton(switchOpts);
					top.handlePrompt("error",'<spring:message code="authorize.status.modified.fail" />');
				},
				success : function(data) {
					top.handlePrompt("success",'<spring:message code="authorize.status.modified.success"/>');
				}
			});
			
		},
		offEvent: function(){
			$("#timeconfigEnable").val("false");
			var url = "${ctx}/mirror/timeConfig/timeconfigEnable";
			$.ajax({
				type : "POST",
				url : url,
				data : $('#form2').serialize(),
				error : function(request) {
					$("#switchButton1").resetSwitchButton(switchOpts);
					top.handlePrompt("error",'<spring:message code="authorize.status.modified.fail" />');
				},
				success : function(data) {
					top.handlePrompt("success",'<spring:message code="authorize.status.modified.success"/>');
				}
			});			
		}
	})
}	
function createCopyPolicy(){
	top.ymPrompt.win({message:'${ctx}/mirror/copyPolicy/createPage',width:700,height:600,title:'<spring:message code="copyPolicy.policy.add.title"/>', iframe:true});
}
function createTimeConfig(){
	top.ymPrompt.win({message:'${ctx}/mirror/timeConfig/createTimeConfigPage',width:500,height:350,title:'<spring:message code="copyPolicy.timeconfig.add.title"/>', iframe:true});
}
/* function createMigration(){
	top.ymPrompt.win({message:'${ctx}/mirror/copyPolicy/createMigration',width:700,height:600,title:'<spring:message code="copyPolicy.policy.add.title"/>', iframe:true});
} */
function deleteTimeConfig(uuid){
     $.ajax({
                type: "POST",
			    url:"${ctx}/mirror/timeConfig/delete",
			    data:{uuid:uuid,token:'${cse:htmlEscape(token)}'},
			    error: function(request) 
			    {
			      top.handlePrompt("error",'<spring:message code="common.delete.fail"/>');
			    },
			    success: function()
			    {
			     top.handlePrompt("success",'<spring:message code="common.delete.success"/>');
			     refreshWindow();
			    }
			});   
}

function speedProgress(id,name){
    var title="<spring:message code='data.Migration.speed'/>"+"("+name+")";
	top.ymPrompt.win({message:'${ctx}/mirror/copyPolicy/speedProcess/'+id,width:700,height:600,title:title, iframe:true});
} 
function modifyCopyPolicy(id){
	top.ymPrompt.win({message:'${ctx}/mirror/copyPolicy/modifiyPage/'+id,width:700,height:600,title:'<spring:message code="copyPolicy.policy.modify.title"/>', iframe:true});
}

function doCreateCopyPolicy(tp) {
	if (tp == 'yes') {
		top.ymPrompt.getPage().contentWindow.submitCreateCopyPolicy();
	} else {
		top.ymPrompt.close();
	}
}


function updateCopyPolicy(status,from){
	var ids = '';
	if(from==0){
	$("input[name='checkname']:checked").each(function () {
        if (ids != '') {
        	ids = ids + "," + this.value;
        } else {
        	ids = this.value;
        }
    });
    }else
    {
    	$("input[name='checknameData']:checked").each(function () {
        if (ids != '') {
        	ids = ids + "," + this.value;
        } else {
        	ids = this.value;
        }
    });
    }
	if (ids == '') {
		handlePrompt("error",'<spring:message code="copyPolicy.relaseOne"/>');
		return;
	}
	if(status==1)
	{
		top.ymPrompt.confirmInfo( {
			title :'<spring:message code="copyPolicy.stop"/>',
			message : '<spring:message code="copyPolicy.stop.message"/>',
			width:450,
			closeTxt:'<spring:message code="common.close"/>',
			handler : function(tp) {
				if(tp == "ok"){
					changeStatus(status,ids);
				}
			},
			btn: [['<spring:message code="common.OK"/>', "ok"],['<spring:message code="common.cancel"/>', "cancel"]]
		});
	}else{
		changeStatus(status,ids);
	}
}
function changeStatus(status,ids)
{
	var url="${ctx}/mirror/copyPolicy/updateStatus";	
	$.ajax({
        type: "POST",
        url:url,
        data:{ids:ids,state:status,token:'${cse:htmlEscape(token)}'},
        error: function(request) {
        	top.handlePrompt("error",'<spring:message code="copyPolicy.change.state.fail"/>');
        },
        success: function() {
        	top.handlePrompt("success",'<spring:message code="copyPolicy.change.state.success"/>');
        	refreshWindow();
        }
    });	
}
function deleteCopyPolicy(id) {
		top.ymPrompt.confirmInfo( {
		title :'<spring:message code="copyPolicy.delete"/>',
		message : '<spring:message code="copyPolicy.delete.message"/>',
		closeTxt:'<spring:message code="common.close"/>',
		handler : function(tp) {
			if(tp == "ok"){
				$.ajax({
			        type: "POST",
			        url:"${ctx}/mirror/copyPolicy/delete",
			        data:{ids:id,token:'${cse:htmlEscape(token)}'},
			        error: function(request) {
			        	top.handlePrompt("error",'<spring:message code="common.delete.fail"/>');
			        },
			        success: function() {
			        	top.handlePrompt("success",'<spring:message code="common.delete.success"/>');
			        	refreshWindow();
			        }
			    });
			}
		},
		btn: [['<spring:message code="common.OK"/>', "ok"],['<spring:message code="common.cancel"/>', "cancel"]]
	});
}
$("#checkall").click(function(){ 
	if(this.checked){ 
		$("input[name='checkname']:checkbox").each(function(){
			this.checked=true;
		});
	}else{ 
		$("input[name='checkname']:checkbox").each(function(){
			 this.checked=false;
		});
	}
});
$("#checkallData").click(function(){ 
	if(this.checked){ 
		$("input[name='checknameData']:checkbox").each(function(){
			this.checked=true;
		});
	}else{ 
		$("input[name='checknameData']:checkbox").each(function(){
			 this.checked=false;
		});
	}
});
function refreshWindow() {
	window.location.href="${ctx}/mirror/copyPolicy/list";
}
</script>
</html>
