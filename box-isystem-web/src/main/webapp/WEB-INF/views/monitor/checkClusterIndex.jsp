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
<style type="text/css">
html,body{ height:100%; }
</style>
</head>
<body>
<div class="checkClu-content">
	<div class="node-list">
		<ul>
		<c:forEach items="${nodes}" var="node">
			<c:if test='${node.status == 0}'>
		    	<li id="${cse:htmlEscape(node.hostName)}" title="${cse:htmlEscape(node.hostName)}"><i class="icon-status-normal"></i>${cse:htmlEscape(node.hostName)}</li>
        	</c:if>
        	<c:if test='${node.status != 0}'>
		    	<li id="${cse:htmlEscape(node.hostName)}" title="${cse:htmlEscape(node.hostName)}"><i class="icon-status-abnormal"></i>${cse:htmlEscape(node.hostName)}</li>
        	</c:if>
        	
        </c:forEach>
        
        
		</ul>
	</div>
       
    <div class="check-frame">
    </div>

</div>
<script>
    $(function(){
        var newObj = $(".node-list").find("li");
        newObj.click(function(){
            $(this).addClass("active").siblings().removeClass("active");
            
            var nodeId = $(this).attr("id");
            if($("#"+ nodeId +"Frame").get(0)){
            	$("#"+ nodeId +"Frame").show().siblings().hide();
            }else{
            	var newFrame = '<iframe id="'+ nodeId +'Frame" src="${ctx}/monitor/manage/viewNodeContent/'+nodeId+'" frameborder="0" width="100%" height="100%" scrolling="auto"></iframe>';
            	$(".check-frame").find("iframe").hide().end().append(newFrame);
            }
        });
        var newFrame = '<iframe id="'+ '${cse:htmlEscape(currentNode.hostName)}' +'Frame" src="${ctx}/monitor/manage/viewNodeContent/'+'${cse:htmlEscape(currentNode.hostName)}'+'" frameborder="0" width="100%" height="100%" scrolling="auto"></iframe>';
    	$(".check-frame").append(newFrame);
    	$("#"+'${cse:htmlEscape(currentNode.hostName)}').addClass("active");
    })
</script>
</body>
</html>