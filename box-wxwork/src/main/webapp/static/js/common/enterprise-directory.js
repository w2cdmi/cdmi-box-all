/*点击返回计算上一页的选中按钮*/
function cancelTheDoubleSelection(){
   		for (var i = 0 ; i < $('#preAddMemberDiv li').length; i ++) {
	   		for (var k = 0 ; k < $('#shareList li').length; k++) {
	   			var $iid = $('#preAddMemberDiv li').eq(i).attr('id');
	   			var $kid = $('#shareList li').eq(k).children('i').attr('id');
	   			if($('#shareList li').eq(k).children('i').hasClass('M-inactive')){
	   				
	   			}else{
	   				var a = $iid.substring(3);
	   				var b = $kid.substring(2);
	   				if(a == b){
	   					$('#shareList li').eq(k).children('i').addClass('M-active');
	   					$('#shareList li').eq(k).children('i').removeClass('M-addblank');
	   					break;
	   				}
	   			}
	   			
	   		}
	   }
}
/*计算宽度*/
function tailNameSlide(){
   		var width = window.screen.width;
   		$('#preAddMemberDiv ul').width(width-100);
   		$('#preAddMember').width(($('#preAddMember li').length+1)*$('#preAddMember li').width());
}

function fullSelectMenber(member){
		   var html="";
		   html=html+"<li id=\"active-"+member.id+"\">";
		   if(member.type == "user"){
		   	   html+="<p><img src=\""+ctx+"/userimage/getUserImage/"+member.id+"\"/></p>";
		   }else{
			   html+="<p><img src=\""+ctx+"/static/skins/default/img/department-icon.png\"/></p>";
		   }
		   if(member.type == "user"){
			   html+="<h1>"+member.alias+"</h1>";
		   }else{
			   html+="<h1>"+member.name+"</h1>";
		   }
		   html=html+"</li>";
		   $("#shareContent").prepend(html);
		   $("#active-"+member.id).data("data",member);
  
   }
/*页尾选中图标的点击事件点击后清楚图标*/
function clearShareMember(memberId){
	$("#active-" + memberId).remove();
	$("#pre" + memberId).remove();
	$("#p-" + memberId).removeClass("M-active");
	$("#p-" + memberId).addClass("M-addblank");
	tailNameSlide()
}
/*返回的点击事件*/
function historyBack(){
	   var parentDeptId = $("#parentDeptId").val();
	   var historyDepts = parentDeptId.split("|");
	   var parentDept = 0;
	   if(historyDepts.length > 1){
		   parentDept = historyDepts[historyDepts.length-2];
	   }
	   if(parentDept=="0"){
		   showShareDiv("addshare2")
		   $("#parentDeptId").val(0);
		   return;
	   }
	   if (parentDept==""||parentDept==undefined) {
	   	
	   } else{
	   		parentDept = parentDept.split(",");
	   }
	   $("#parentDeptId").val(parentDeptId.substring(0,parentDeptId.lastIndexOf("|")));
	   parentDeptId = $("#parentDeptId").val();
	   $("#parentDeptId").val(parentDeptId.substring(0,parentDeptId.lastIndexOf("|")));
	   if(parentDept.length > 1){
		   if(parentDept[1] == ""){
			   parentDept[1] = "企业通讯录";
		   }
		   showDepAndUsers(parentDept[0],parentDept[1]);
	   }else{
		   showRootDept();
	   }
   }


function showDepAndUsers(deptId,deptName){
	   if(deptId == 0){
		   deptName = "企业通讯录";
	   }
	   var defaultlinKset={
           deptId:deptId,
		}
		$.ajax({
	        type: "POST",
	        async:false,
	        url:host + '/ecm/api/v2/users/listDepAndUsers',
	        data:JSON.stringify(defaultlinKset),
	        error: function(xhr, status, error) {
	        },
	        success: function(data) {
	        	data = $.parseJSON(data);
	        	if(data.length > 0){
	        		if(typeof(deptName)=="undefined"){
	        			deptName = "";
	        		}
	        		$("#parentDeptId").val($("#parentDeptId").val()+"|"+deptId+","+deptName);
        			$("#department").html(deptName);
	        		fullShareDiv(data);
	        	}else{
	        		$.toast("没有员工",400);
	        	}
				cancelTheDoubleSelection();
	        }
	    });
   }

//点击企业通讯录
   function openEnterpriseDirectory(){
	   showShareDiv('addshare3');
	   showRootDept();
   }
