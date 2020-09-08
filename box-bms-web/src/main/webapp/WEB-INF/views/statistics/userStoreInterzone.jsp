<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="cse"
	uri="http://cse.huawei.com/custom-function-taglib"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html>
<head>
<%@ include file="../common/common.jsp"%>
<script src="${ctx}/static/js/public/JQbox-hw-grid.js"
	type="text/javascript"></script>
</head>

<body>
<div class="sys-content sys-content-stati">
	<h5><spring:message code="capacity.statis.extent.set" /></h5>
	<div id="zoneContent" class="form-con clearfix">
		<span class="help-block"><spring:message code="set.user.statis" /></span>
		<div id="zoneGroup">
			<div class="form-inline control-group" >
				<span><spring:message code="region" />: </span>
				<input class="span2" value="0" type="text" readonly="true" > -
				 <input class="span2" value="1" type="text" onfocus="getValue(this)" onblur="changeValue(this)">
				 <button class="btn" type="button" onclick="changeDiv(this)">+</button>
			</div>
		</div>
		
		<div class="control-group">
			<input class="btn btn-primary" type="button" onclick="saveData()" value='<spring:message code="common.save" />' />
			<input type="hidden" value="1" id="xxx" name="interzone"/>
		</div>
	</div>
	
</div>
</body>
</html>
<script type="text/javascript">
var oldValue = 0; 

$(document).ready(function(){
	var param ={
			"token" : "<c:out value='${token}'/>"
	}
	$.ajax({
		type : "POST",
		url : "${ctx}/userInterzone/getInterzone",
		data:param,
		error : function(data) {
			handlePrompt("error","<spring:message code='load.fail' />");	
		},
		success : function(data) {
			if(data.length == 1){
				$("input[type='text':first]").val(data[0]);
				$("input[type='text']:last").val(data[0]);
			}else if(data.length ==2){
				if(data[1] > 0){
					$("input[type='text']:first").val(data[0]);
					$("input[type='text']:last").val(data[1]);
				}
			}else{
				$("input[type='text']:first").val(data[0]);
				$("input[type='text']:last").val(data[1]);
				for(var i=0 ;i < data.length-2;i++){
					var _$obj = $("#zoneGroup");
					var lastBrother = _$obj.children("div:last-child");
					var lastChildOfBrother = lastBrother.children("input[type='text']:last");
					var brotherText = lastChildOfBrother.val();
					_$obj.append( _$obj.children("div:first-child").clone());
					_$obj.children("div:last-child").children("input[type='text']:first").val(data[i+1]);
					_$obj.children("div:last-child").children("input[type='text']:last").val(data[i+2]);
					_$obj.children("div:last-child").children("button[type='button']").text("-");
				}
			}
		}
	});
});

function getValue(obj){
	oldValue = obj.value;
}

function saveData(){
	var texts = $("input[type='text']");
	var length = texts.size();
	var str ="";
	for(var i = 0 ; i < texts.size();){
		str += $(texts[i]).val() +";";
		i = i+2;
	}
	str += $(texts [length-1]).val();
	var reginValue = str.split(";");
	for(var i=0; i<reginValue.length; i++){
		if (i+1<reginValue.length && reginValue[i]==reginValue[i+1]){
			handlePrompt("error","<spring:message code='setcapacity.statistical.range.fail' />");	
			return;
		}
	}
	var param ={
			"interzone":str,
			"token" : "<c:out value='${token}'/>"
	}
	$.ajax({
		type:"POST",
		url : "${ctx}/userInterzone",
		data:param,
		error:function(request){
			switch(request.responseText)
			{
				case "existRegion":
					handlePrompt("error","<spring:message code='setcapacity.statistical.range.exist' />");
					break;
				default:
					handlePrompt("error","<spring:message code='common.saveFail' />");	
				    break;
			}	
		},
		success:function(data){
			handlePrompt("success","<spring:message code='common.saveSuccess' />");	
		}
	});
}
function changeValue(obj){
	var value = obj.value;
	var _$obj= $(obj);
	var oldLastValue = _$obj.parent().next().children("input[type='text']:last").val();
	var _$obj= $(obj);
	var pattern =/^[0-9]*[1-9][0-9]*$/;
	if(!pattern.test(value)){
		handlePrompt("error","<spring:message code='common.not.positive.integer' />");
		_$obj.val(oldValue);
		return;
	}
	if(isNaN(value)){
		handlePrompt("error","<spring:message code='common.not.integer' />");
		_$obj.val(oldValue);
	}
	if(parseInt(oldLastValue) < parseInt(value)){
		handlePrompt("error", "<spring:message code='common.region.too.big' />");
		_$obj.val(oldValue);
	}else{
		_$obj.parent().next().children("input[type='text']:first").val(value);
	}
}


function changeDiv(obj){
	var sign = obj.innerText;
	var _$obj= $(obj);
	var superParent = _$obj.parent().parent();
	if(sign == "-"){
		var parent = _$obj.parent();
		var prevParent = parent.prev();
		var nextParent = parent.next();
		$("#xxx").val(sign);
		nextParent.children("input[type='text']:first").val(prevParent.children("input[type='text']:last").val());
		parent.remove();
	}
	else{
		if($("input[type='text']").size() >= 10){
			handlePrompt("error","<spring:message code='user.statis.maxlength'/>");
			return;
		}
		var lastBrother = superParent.children("div:last-child");
		var lastChildOfBrother = lastBrother.children("input[type='text']:last");
		var brotherText = lastChildOfBrother.val();
		superParent.append(superParent.children("div:first-child").clone());
		superParent.children("div:last-child").children("input[type='text']:first").val(brotherText);
		superParent.children("div:last-child").children("input[type='text']:last").val(2*brotherText);
		superParent.children("div:last-child").children("button[type='button']").text("-");
	}
}
</script>