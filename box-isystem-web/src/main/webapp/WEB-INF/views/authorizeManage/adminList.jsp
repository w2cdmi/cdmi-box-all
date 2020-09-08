<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="cse" uri="http://cse.huawei.com/custom-function-taglib"%>  
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="Cache-Control" content="no-cache" />
<meta http-equiv="Pragma" content="no-cache" />
<title></title>
<%@ include file="../common/common.jsp"%>
</head>
<body>

<div class="sys-content">
	<div class="alert"><i class="icon-lightbulb icon-orange"></i><spring:message code="authorize.adminList.describe"/></div>
    <div class="clearfix">
    	<div class="pull-left">
	    	<button type="button" class="btn btn-primary" onClick="createAdminUser()"><i class="icon-add"></i><spring:message code="authorize.createAdminUser" /></button>
	    	<button type="button" class="btn btn-primary-small" onClick="updateAdminStatus(1)"><spring:message code="common.start" /></button>
	    	<button type="button" class="btn btn-primary-small" onClick="updateAdminStatus(0)"><spring:message code="common.forbidden" /></button>
        </div>
        <form class="pull-right form-search" method="POST" id="searchForm" name="searchForm" action="${ctx}/authorize/role/search">
            <select   class="span3" id="selectStatus" name="selectStatus">
            		    <option value="2" <c:if test="${empty selectStatus || (selectStatus != 1 && selectStatus != 0)  }" >selected="selected"</c:if>><spring:message code="select.user.status" /></option>
            		    <option value="1"  <c:if test="${ selectStatus ==1 }" >selected="selected"</c:if>><spring:message code="common.start" /></option>
						<option value="0" <c:if test="${ selectStatus ==0 }" >selected="selected"</c:if>><spring:message code="common.forbidden" /></option>
			</select>
            <div class="input-append">                    
              <input type="text" id="searchKey" name="searchKey" class="span3 search-query" value="${cse:htmlEscape(searchKey)}" placeholder='<spring:message code="authorize.adminlist.cliew"/>' />
              <button type="submit" class="btn" onclick="submitSearch()"><i class="icon-search"></i></button>
            </div>
             <input type="hidden" name="token" value="${cse:htmlEscape(token)}"/>
        </form>
    </div>
    <div class="table-con clearfix">
        <table class="table table-bordered table-striped">
          <thead>
            <tr>
            	<th style="width:4%"><input type="checkbox" id="checkall" name="checkall" /></th>
                <th style="width:14%"><spring:message code="authorize.name"/></th>
                <th style="width:14%"><spring:message code="authorize.username"/></th>
                <th style="width:7%"><spring:message code="common.status"/></th>
                <th><spring:message code="authorize.roleList"/></th>
                <th style="width:10%"><spring:message code="authorize.accountType"/></th>
                <th style="width:31%"><spring:message code="authorize.operation"/></th>
            </tr>
          </thead>
          <tbody>
          <c:forEach items="${adminList}" var="admin">
            <tr>
            	<td><input type="checkbox" id="${cse:htmlEscape(admin.id)}" name="checkname" value="${cse:htmlEscape(admin.id)}"/></td>
                <td title="${cse:htmlEscape(admin.name)}">${cse:htmlEscape(admin.name)}</td>
                <td title="${cse:htmlEscape(admin.loginName)}">${cse:htmlEscape(admin.loginName)}</td>
                <td>
                    <c:if test="${admin.status == 0}">
                      		  	<spring:message code="common.forbidden" />
                    </c:if>
                    <c:if test="${admin.status == 1}">
                        		<spring:message code="common.start" />
                    </c:if>
                </td>
                <td title="${cse:htmlEscape(admin.password)}">${cse:htmlEscape(admin.password)}</td>
                <td>
                    <c:if test="${admin.domainType == 1}">
                        <spring:message code="authorize.localAccount"/>
                    </c:if>
                    <c:if test="${admin.domainType != 1}">
                        <spring:message code="authorize.ADAccount"/>
                    </c:if>
                </td>
                <td>
                     <button class="btn" type="button" onClick="modifyAdAdminUser(${cse:htmlEscape(admin.id)},'${cse:htmlEscape(admin.roles)}','${cse:htmlEscape(admin.loginName)}')"/><spring:message code="common.modify"/></button>
					 <button class="btn" type="button" onClick="deleteAdminUser(${cse:htmlEscape(admin.id)})"/><spring:message code="common.delete"/></button>
					 <button class="btn" type="button" onClick="resetPassword(${cse:htmlEscape(admin.id)})"/><spring:message code="reset.pwd"/></button>
                </td>
            </tr>
            </c:forEach>
            <c:if test="${empty adminList and 'true'!=searchedList}">  
            <tr>
                 <td colspan="6" style="text-align:center">
                       <spring:message code="authorize.adminList.add.describe"/><a href="javascript:createAdminUser()"><spring:message code="common.create"/></a>。
                 </td>
            </tr>
            </c:if>
          </tbody>
        </table>
    </div>
</div>
</body>
<script type="text/javascript">
$(function(){
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
})
function createAdAdminUser(){
	top.ymPrompt.win({message:'${ctx}/authorize/role/create',width:700,height:380,title:'<spring:message code="authorize.createAdAdminUser"/>', iframe:true,btn:[['<spring:message code="common.create"/>','yes',false,"btnCreate"],['<spring:message code="common.cancel"/>','no',true,"btnCancel"]],handler:doCreateAdAdminUser});
	top.ymPrompt_addModalFocus("#btnCreate");
}

function modifyAdAdminUser(id,roles,loginName){
	top.ymPrompt.win({message:'${ctx}/authorize/role/modify/?id='+id+'&'+'roles=' + roles + '&loginName=' + loginName,width:700,height:300,title:'<spring:message code="authorize.modifyAdminUser"/>', iframe:true,btn:[['<spring:message code="common.modify"/>','yes',false,"btnModify"],['<spring:message code="common.cancel"/>','no',true,"btnModifyCancel"]],handler:doModifyAdAdminUser});
	top.ymPrompt_addModalFocus("#btnModify");
}

function doModifyAdAdminUser(tp){
	if (tp == 'yes') {
		top.ymPrompt.getPage().contentWindow.submitModifyAdAdminUser();
	} else {
		top.ymPrompt.close();
	}
}

function doCreateAdAdminUser(tp) {
	if (tp == 'yes') {
		top.ymPrompt.getPage().contentWindow.submitCreateAdAdminUser();
	} else {
		top.ymPrompt.close();
	}
}

function createLocalAdminUser(){
	top.ymPrompt.win({message:'${ctx}/authorize/role/createLocal',width:700,height:340,title:'<spring:message code="authorize.createLocalAdminUser"/>', iframe:true,btn:[['<spring:message code="common.create"/>','yes',false,"btnCreate"],['<spring:message code="common.cancel"/>','no',true,"btnCancel"]],handler:doCreateLocalAdminUser});
	top.ymPrompt_addModalFocus("#btnCreate");
}

function createAdminUser(){
	top.ymPrompt.win({message:'${ctx}/authorize/role/createAdmin',width:700,height:500,title:'<spring:message code="authorize.createAdminUser"/>', iframe:true,btn:[['<spring:message code="common.create"/>','yes',false,"btnCreate"],['<spring:message code="common.cancel"/>','no',true,"btnCancel"]],handler:doCreateAdminUser});
	top.ymPrompt_addModalFocus("#btnCreate");
}

function doCreateAdminUser(tp) {
	if (tp == 'yes') {
		top.ymPrompt.getPage().contentWindow.submitCreateAdminUser();
	} else {
		top.ymPrompt.close();
	}
}

function doCreateLocalAdminUser(tp) {
	if (tp == 'yes') {
		top.ymPrompt.getPage().contentWindow.submitCreateLocalAdminUser();
	} else {
		top.ymPrompt.close();
	}
}

function resetPassword(id) {
	top.ymPrompt.win({message:'${ctx}/authorize/role/resetAdminPwd/?id='+id,width:700,height:260,title:'<spring:message code="reset.admin.pwd"/>', iframe:true,btn:[['<spring:message code="common.modify"/>','yes',false,"btnReset"],['<spring:message code="common.cancel"/>','no',true,"btnResetCancel"]],handler:doResetAdminPwd});
	top.ymPrompt_addModalFocus("#btnReset");
}

function doResetAdminPwd(tp){
	if (tp == 'yes') {
		top.ymPrompt.getPage().contentWindow.submitResetAdminPwd();
	} else {
		top.ymPrompt.close();
	}
}
function updateAdminStatus(status){
	var ids = '';
	$("input[name='checkname']:checked").each(function () {
        if (ids != '') {
        	ids = ids + "," + this.value;
        } else {
        	ids = this.value;
        }
    });
	if (ids == '') {
		handlePrompt("error",'<spring:message code="authorize.adminList.relaseOne"/>');
		return;
	}
	if(status==0)
	{
		top.ymPrompt.confirmInfo( {
			title :'<spring:message code="forbidden.admin"/>',
			message : '<spring:message code="forbidden.admin.message"/>',
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
	var url;
	if(status==0)
	{
		url="${ctx}/authorize/role/disableAdmin";	
	}
	else
	{
		url="${ctx}/authorize/role/enableAdmin";
	}
	$.ajax({
        type: "POST",
        url:url,
        data:{ids:ids,token:'${cse:htmlEscape(token)}'},
        error: function(request) {
        	top.handlePrompt("error",'<spring:message code="authorize.status.modified.fail"/>');
        },
        success: function() {
        	top.handlePrompt("success",'<spring:message code="authorize.status.modified.success"/>');
        	refreshWindow();
        }
    });	
}
function submitSearch()
{
	$("#searchForm").submit();
}
function deleteAdminUser(id) {	
	 top.ymPrompt.confirmInfo( {
		title :'<spring:message code="authorize.delAdminUser"/>',
		message : '<spring:message code="authorize.delAdminUser.affirmance"/>',
		closeTxt:'<spring:message code="common.close"/>',
		handler : function(tp) {
			if(tp == "ok"){
				$.ajax({
			        type: "POST",
			        url:"${ctx}/authorize/role/delete",
			        data:{id:id,token:'${cse:htmlEscape(token)}'},
			        error: function(request) {
			        	if("noSuch"==request.responseText)
			        	{
			        		top.handlePrompt("error",'<spring:message code="authorize.noSuchAdminUserFail"/>');
			        	}
			        	if("appExist"==request.responseText)
			        	{
			        		top.handlePrompt("error",'<spring:message code="authorize.appExistAdminUserFail"/>');
			        	}
			        	else
			        	{
			        		top.handlePrompt("error",'<spring:message code="authorize.delAdminUserFail"/>');
			        	}
			        },
			        success: function() {
			        	
			        	top.handlePrompt("success",'<spring:message code="authorize.delAdminUserSuccess"/>');
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
function refreshWindow() {
	window.location.href="${ctx}/authorize/role/list";
}
</script>
</html>
