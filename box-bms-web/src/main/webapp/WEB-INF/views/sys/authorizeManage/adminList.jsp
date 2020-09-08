<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cse" uri="http://cse.huawei.com/custom-function-taglib"%>   
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
<%@ include file="../../common/common.jsp"%>
<script src="${ctx}/static/js/public/JQbox-hw-page.js" type="text/javascript"></script>
</head>
<body>

<div class="sys-content">
	<div class="alert"><i class="icon-lightbulb"></i><spring:message code="authorize.light.bulb"/></div>
    <div class="clearfix">
    	<div class="pull-left">
	    	<button type="button" class="btn btn-primary" onClick="createAdminUser()"><i class="icon-plus"></i><spring:message code="admin.create"/></button>
	    	<button type="button" class="btn" onClick="updateAdminStatus(1)"><spring:message code="common.enable"/></button>
	    	<button type="button" class="btn" onClick="updateAdminStatus(0)"><spring:message code="common.disable"/></button>
	   		<button type="button" class="btn" onClick="deletelistAdmin()"><spring:message code="common.delete"/></button>
	    </div>
	    <form class="pull-right form-search" action="${ctx}/sys/authorize/role/list" method="post" id="searchForm" name="searchForm"  enctype="Multipart/form-data" >
	            <input type="hidden" id="page" name="page" value="1">
	            <div class="input-append">                   
	              <input type="text" id="filter" name="filter" class="span3 search-query" value="<c:out value='${filter}'/>" placeholder='<spring:message code="user.manager.searchDescription"/>' />
	              <button type="submit" class="btn" value=""><i class="icon-search"></i></button>
	            </div>
	            <input type="hidden" id="token" name="token" value="<c:out value='${token}'/>"/>
	    </form>
    </div>
    <div class="table-con clearfix">
        <table class="table table-bordered table-striped">
          <thead>
            <tr>
            	<th style="width:4%"><input type="checkbox" id="checkall" name="checkall" /></th>
                <th width="10%"><spring:message code="authorize.username"/></th>
                <th width="10%"><spring:message code="authorize.name"/></th>
                <th width="15%"><spring:message code="authorize.label.mail"/></th>
                <th width="6%"><spring:message code="common.status"/></th>
                <th ><spring:message code="authorize.description"/></th>
                <th width="13%"><spring:message code="authorize.permission"/></th>
                <th width="13%"><spring:message code="authorize.update.time"/></th>
                <th width="15%"><spring:message code="authorize.operation"/></th>
            </tr>
          </thead>
          <tbody>
          <c:forEach items="${userPage.content}" var="admin">
            <tr>
            	<td><input type="checkbox" id="<c:out value='${admin.id}'/>" name="checkname" value="<c:out value='${admin.id}'/>"/></td>
                <td title="<c:out value='${admin.loginName}'/>"><c:out value='${admin.loginName}'/></td>
                <td title="<c:out value='${admin.name}'/>"><c:out value='${admin.name}'/></td>
                <td title="<c:out value='${admin.email}'/>"><c:out value='${admin.email}'/></td>
                <td>
                    <c:if test="${admin.status == 0}">
                      		  	<spring:message code="common.disable"/>
                    </c:if>
                    <c:if test="${admin.status == 1}">
                        		<spring:message code="common.enable"/>
                    </c:if>
                </td>
                <td title="${admin.noteDesc}">
                    <c:if test="${null==admin.noteDesc ||admin.noteDesc.isEmpty()}">
                        	-
                    </c:if>
                    <c:if test="${null!=admin.noteDesc &&!admin.noteDesc.isEmpty()}">
                    	${admin.noteDesc}
                    </c:if>
                </td>
                <td title="${cse:htmlEscape(admin.password)}"><c:out value='${admin.password}'/></td>
                <td>
                  <fmt:formatDate value="${admin.modifiedAt}" pattern="yyyy-MM-dd HH:mm:ss"/>
                </td>
                <td>
                     <button class="btn" type="button" onClick="modifyAdminUser(<c:out value='${admin.id}'/>,'<c:out value="${admin.roles}"/>','<c:out value="${admin.loginName}"/>')"/><spring:message code="common.modify"/></button>
					 <button class="btn" type="button" onClick="deleteAdminUser(${admin.id})"/><spring:message code="common.delete"/></button>
                </td>
            </tr>
            </c:forEach>
            <c:if test="${adminAllCount==0}" >  
            <tr>
                 <td colspan="6" style="text-align:center">
                 <spring:message code="admin.none"/><a href="javascript:createAdminUser()"><spring:message code="common.new.messages"/></a>
                 </td>
            </tr>
            </c:if>
          </tbody>
        </table>
    </div>
    <div id="myPage"></div>
</div>
</body>
<script type="text/javascript">
$(function(){
	$("#myPage").comboPage({
		lang:'<spring:message code="main.language"/>',
		curPage : <c:out value="${userPage.number}"/>,
		perDis : <c:out value="${userPage.size}"/>,
		totaldata : ${userPage.totalElements},
		style : "page table-page"
	})
	$.fn.comboPage.pageSkip = function(opts, _idMap, curPage){
		$("#page").val(curPage);
		$("#searchForm").submit();
	};
	var pageH = $("body").outerHeight();
	top.iframeAdaptHeight(pageH);
	if(!placeholderSupport()){
		placeholderCompatible();
	};
})

function modifyAdminUser(id,roles,loginName){
	top.ymPrompt.win({message:'${ctx}/sys/authorize/role/modify/?id='+id+'&'+'roles=' + roles + '&loginName=' + loginName,width:700,height:480,title:'<spring:message code="admin.modify"/>', iframe:true,btn:[['<spring:message code="common.modify"/>','yes',false,"btnModify"],['<spring:message code="common.cancel"/>','no',true,"btnModifyCancel"]],handler:doModifyAdminUser});
	top.ymPrompt_addModalFocus("#btnModify");
}

function doModifyAdminUser(tp){
	if (tp == 'yes') {
		top.ymPrompt.getPage().contentWindow.submitModifyAdminUser();
	} else {
		top.ymPrompt.close();
	}
}

function createAdminUser(){
	top.ymPrompt.win({message:'${ctx}/sys/authorize/role/createAdmin',width:700,height:480,title:'<spring:message code="admin.create.title"/>', iframe:true,btn:[['<spring:message code="common.create"/>','yes',false,"btnCreate"],['<spring:message code="common.cancel"/>','no',true,"btnCancel"]],handler:doCreateAdminUser});
	top.ymPrompt_addModalFocus("#btnCreate");
}

function doCreateAdminUser(tp) {
	if (tp == 'yes') {
		top.ymPrompt.getPage().contentWindow.submitCreateAdminUser();
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
		handlePrompt("error",'<spring:message code="admin.err"/>');
		return;
	}
	var tilteTip,messageTip;
	if(status == 1){
		tilteTip = '<spring:message code="admin.open.title"/>';
		messageTip = '<spring:message code="admin.open.message"/>';
	}else{
		tilteTip = '<spring:message code="admin.close.title"/>';
		messageTip = '<spring:message code="admin.close.message"/>'
	}
	top.ymPrompt.confirmInfo( {
		title :tilteTip,
		message :messageTip,
		width:450,
		closeTxt:'<spring:message code="common.close"/>',
		handler : function(tp) {
			if(tp == "ok"){
				changeStatus(status,ids);
			}
		},
		btn: [['<spring:message code="common.OK"/>', "ok"],['<spring:message code="common.cancel"/>', "cancel"]]
	});
}
function changeStatus(status,ids)
{
	var url;
	if(status==0)
	{
		url="${ctx}/sys/authorize/role/disableAdmin";	
	}
	else
	{
		url="${ctx}/sys/authorize/role/enableAdmin";
	}
	$.ajax({
        type: "POST",
        url:url,
        data:{ids:ids,"token" : "<c:out value='${token}'/>"},
        error: function(request) {
        	top.handlePrompt("error",'<spring:message code="common.modifyStatusFailed"/>');
        },
        success: function() {
        	top.handlePrompt("success",'<spring:message code="common.modifyStatusSucessed"/>');
        	refreshWindow();
        }
    });	
}
function deletelistAdmin(){
	var ids = '';
	$("input[name='checkname']:checked").each(function () {
        if (ids != '') {
        	ids = ids + "," + this.value;
        } else {
        	ids = this.value;
        }
    });
	if (ids == '') {
		handlePrompt("error",'<spring:message code="admin.err"/>');
		return;
	}
	top.ymPrompt.confirmInfo( {
		title :'<spring:message code="admin.delete.title"/>',
		message : '<spring:message code="del.admin.message"/>',
		closeTxt:'<spring:message code="common.close"/>',
		handler : function(tp) {
			if(tp == "ok"){
					$.ajax({
			        type: "POST",
			        url:"${ctx}/sys/authorize/role/deleteList",
			        data:{ids:ids,"token" : "<c:out value='${token}'/>"},
			        error: function(request) {
			        	top.handlePrompt("error",'<spring:message code="authorize.delAdminUserFail"/>');
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
function deleteAdminUser(id) {
	top.ymPrompt.confirmInfo( {
		title :'<spring:message code="admin.delete.title"/>',
		message : '<spring:message code="admin.delete.message"/>',
		closeTxt:'<spring:message code="common.close"/>',
		handler : function(tp) {
			if(tp == "ok"){
				$.ajax({
			        type: "POST",
			        url:"${ctx}/sys/authorize/role/delete",
			        data:{id:id,"token" : "<c:out value='${token}'/>"},
			        error: function(request) {
			        	top.handlePrompt("error",'<spring:message code="authorize.delAdminUserFail"/>');
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
	top.window.frames[0].location = "${ctx}/sys/authorize/role/list";
}
</script>
</html>
