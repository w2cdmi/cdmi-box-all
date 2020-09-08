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
	<%-- <div class="alert"><i class="icon-lightbulb icon-orange"></i><spring:message code="authorize.adminList.describe"/></div> --%>
    <div class="clearfix">
    	<div class="pull-left">
	    	<button type="button" class="btn btn-primary" onClick="createAdminUser()"><i class="icon-add"></i><spring:message code="authorize.createAdminUser" /></button>
	    	<%-- <button type="button" class="btn btn-primary-small" onClick="updateAdminStatus(1)"><spring:message code="common.start" /></button>
	    	<button type="button" class="btn btn-primary-small" onClick="updateAdminStatus(0)"><spring:message code="common.forbidden" /></button> --%>
        </div>
        <%-- <form class="pull-right form-search" method="POST" id="searchForm" name="searchForm" action="${ctx}/authorize/role/search">
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
        </form> --%>
    </div>
    <div class="table-con clearfix">
        <table class="table table-bordered table-striped">
          <thead>
            <tr>
            	<!-- <th style="width:4%"><input type="checkbox" id="checkall" name="checkall" /></th> -->
                <th style="width:14%"><spring:message code="authorize.name"/></th>
                <th style="width:14%"><spring:message code="authorize.username"/></th>
                <%-- <th style="width:7%"><spring:message code="common.status"/></th>  --%>
                <%-- <th><spring:message code="authorize.roleList"/></th> --%>
                <th style="width:10%"><spring:message code="authorize.accountType"/></th>
                <%-- <th style="width:31%"><spring:message code="authorize.operation"/></th> --%>
            </tr>
          </thead>
          <tbody>
          <c:forEach items="${adminList}" var="admin">
            <tr>
                <td title="${admin.name}">${admin.name}</td>
                <td title="${admin.loginName}">${admin.loginName}</td>
                <td>
                    <c:if test="${admin.type == 1}">
                      		  	<spring:message code="isystem.system.super.admin" />
                    </c:if>
                    <c:if test="${admin.type == 2}">
                        		<spring:message code="isystem.system.admin" />
                    </c:if>
                    <c:if test="${admin.type == 3}">
                        		<spring:message code="isystem.bms.super.admin" />
                    </c:if>
                    <c:if test="${admin.type == 4}">
                        		<spring:message code="isystem.bms.admin" />
                    </c:if>
                </td>
                
                <%-- <td>
                     <button class="btn" type="button" onClick="modifyAdAdminUser(${cse:htmlEscape(admin.id)},'${cse:htmlEscape(admin.roles)}','${cse:htmlEscape(admin.loginName)}')"/><spring:message code="common.modify"/></button>
					 <button class="btn" type="button" onClick="deleteAdminUser(${cse:htmlEscape(admin.id)})"/><spring:message code="common.delete"/></button>
					 <button class="btn" type="button" onClick="resetPassword(${cse:htmlEscape(admin.id)})"/><spring:message code="reset.pwd"/></button>
                </td> --%>
            </tr>
            </c:forEach>
            <%-- <c:if test="${empty adminList and 'true'!=searchedList}">  
            <tr>
                 <td colspan="6" style="text-align:center">
                       <spring:message code="authorize.adminList.add.describe"/><a href="javascript:createAdminUser()"><spring:message code="common.create"/></a>。
                 </td>
            </tr>
            </c:if> --%>
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
function createAdminUser(){
	top.ymPrompt.win({message:'${ctx}/systeminit/isystem/admin/config',width:700,height:380,title:'<spring:message code="authorize.createAdminUser"/>', iframe:true,btn:[['<spring:message code="common.create"/>','yes',false,"btnCreate"],['<spring:message code="common.cancel"/>','no',true,"btnCancel"]],handler:doCreateAdAdminUser});
	top.ymPrompt_addModalFocus("#btnCreate");
}


function doCreateAdAdminUser(tp) {
	if (tp == 'yes') {
		top.ymPrompt.getPage().contentWindow.submitCreateAdminUser();
	} else {
		top.ymPrompt.close();
	}
}

</script>
</html>
