<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cse" uri="http://cse.huawei.com/custom-function-taglib"%>  
<%@ page import="com.huawei.sharedrive.isystem.util.CSRFTokenManager"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%
request.setAttribute("token", CSRFTokenManager.getTokenForSession(session));
%>
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
<div class="pop-content">
	<div>
		<button type="button" class="btn btn-primary btn-small" onClick="createAccessKey()"><spring:message code="common.create"/></button>
	</div>
	<div class="sys-content">
		<div style="border:1px solid #ddd; height:223px;width:1030px;">
		 <table class="table table-striped table-condensed" style=" border-bottom:1px solid #ddd;">
          <thead>
            <tr>
                <th style="width:550px;"><spring:message code="app.connect.ID"/></th>
                <th style="width:230px;"><spring:message code="app.connect.key"/></th>
                <th style="width:140px;"><spring:message code="app.create.time"/></th>
                <th ><spring:message code="common.operation"/></th>
            </tr>
          </thead>
          <tbody>
          <c:forEach items="${accessKeyList}" var="accessKey">
            <tr>
                <td>${cse:htmlEscape(accessKey.id)}</td>
                <td>${cse:htmlEscape(accessKey.secretKey)}</td>
                <td>
                <fmt:formatDate value="${accessKey.createdAt}" pattern="yyyy-MM-dd HH:mm"/>
                </td>
                <td>
                <button class="btn" type="button" onClick="deleteAccessKey('${cse:htmlEscape(accessKey.id)}')"><spring:message code="common.delete"/></button>
                </td>
            </tr>
          </c:forEach>
          </tbody>
        </table>
        </div>
	</div>
</div>
<script type="text/javascript">
function createAccessKey(){
	$.ajax({
        type: "POST",
        url:"${ctx}/appmanage/appaccesskey/create",
        data:{appId:"${cse:htmlEscape(appId)}","token" : "${cse:htmlEscape(token)}"},
        error: function(request) {
        	switch(request.responseText)
            {
                case "LimitEceeded":
                    handlePrompt("error",'<spring:message code="app.connect.max"/>');
                    break;
                default:
                    handlePrompt("error","<spring:message code='common.createFail'/>");
                    break;
            }
        },
        success: function(data) {
        	top.ymPrompt.close();
        	top.ymPrompt.win({message:'${ctx}/appmanage/appaccesskey/firstScanSK?appId='+"${cse:htmlEscape(appId)}"+'&akId='+data+'',width:800,height:350,title:'<spring:message code="app.connetCode.info"/>', iframe:true,btn:[['<spring:message code="common.close"/>','no',true]]});			       	       
        }
    });
}

function deleteAccessKey(id) {
	
	var temp = '${accessKeyList}';
	if(temp.split(",").length<2)
	{
		 handlePrompt("error","<spring:message code='common.last.delete.fail'/>");
		 return false;
	}
    ymPrompt.confirmInfo( {
        title :'<spring:message code="app.connectCode.del"/>',
        message : '<spring:message code="app.connectCode.del.clew"/>',
        maskAlphaColor: "#eee",
        closeTxt:'<spring:message code="common.close"/>',
        handler : function(tp) {
            if(tp == "ok"){
                $.ajax({
                    type: "POST",
                    url:"${ctx}/appmanage/appaccesskey/delete",
                    data:{appId:"${cse:htmlEscape(appId)}",appAccessKeyId:id,"token" : "${cse:htmlEscape(token)}"},
                    error: function(request) {
                        handlePrompt("error","<spring:message code='common.delete.fail'/>");
                    },
                    success: function() {
                    	window.location.reload();
                    }
                });
            }
        },
        btn: [['<spring:message code="common.OK"/>', "ok"],['<spring:message code="common.cancel"/>', "cancel"]]
    });
}
</script>
</body>
</html>
