(
   function($){
   	  $.fn.extend({
   	  	 TeamSpaceList:function(){
   	  	 	var self=this;
   	  	 	self.init=function(){
         
   	  	 	}
            self.selectedRow=null;
            function error_event(request)
            {
                  if(request.status == 404) {
							//$.toast('<spring:message code='error.notfound'/>');
				  } else {
							///$.toast('<spring:message code='file.errorMsg.listFileFailed'/>');
				  }
            }

            function addRow(row){
               var node=$('<div class="space-row">'
            +'<div class="space-row-son" id="space_'+row.teamspace.id+'" onclick="gotoTeamSpace(' + row.teamspace.id + ')">'
            +'   <div class="team-icon">'
            +'      <img src="' + ctx + '/static/skins/default/img/space-row-icon.png" />'
            +'   </div>'
            +'   <div class="team-info">'
            +'      <div class="team-more"></div>'
            +'      <div class="team-name">'
            +'         <span>'+row.teamspace.name+'</span>'
            +'      </div>'
            +'      <div class="space-list">'
            +'         <i>拥有者：'+row.teamspace.ownedByUserName+'</i>'
            +'         <p>成员数：<span>'+row.teamspace.curNumbers+'</span>'
            +'      </div>'
            +'   </div>'
            +'</div>'
            +' </div>');
            node.mouseenter(function(){
                 self.selectedRow=row;
            });   
            node.find(".team-more").data("item",row);
            node.find(".team-more").click(function(e){
               e.stopPropagation();
            })
            node.find(".team-more").popover($("#worker_popover"),true,"right",function (t) {
                 var item=t.data("item");
                 var role=[1];
                  if(item.teamRole == "admin") {
                      role.push(2)
                  }
                  if(item.teamRole == "admin" || item.teamRole == "manager") {
                      role.push(3)
                  }
                if(item.teamRole == "manager") {
                    role.push(4)
                }
                if(item.teamRole == "manager" || item.teamRole == "member") {
                    role.push(5)
                }

                 
                  $("#worker_popover").find("dt").hide();
                  for(var i=0;i<role.length;i++){
                     $("#worker_popover").find("dt[role='"+role[i]+"']").show();
                  }
                  
            });

               // node.find(".team-more").click(function(){
               //    alert("sdfsdf");
               // })
               self.append(node);
            }

            function success_event(data){
            	if(typeof(data) == 'string' && data.indexOf('<html>') != -1) {
					//window.location = "${ctx}/logout";
					    return;
				   }
				   self.empty();
               var rows=data.memberships;
                if(rows.length==0){
                    self.parent(".abslayout").find("#not_space").show()
                }else{
                    self.parent(".abslayout").find("#not_space").hide()
                }
               for(var row in rows){
                  addRow(rows[row]);
               }
            }

            function complete_event(){

            }

   	  	 	self.load=function(){
                $.ajax({
   					type: "POST",
   					url: host+"/ufm/api/v2/teamspaces/items",
   					data: JSON.stringify(self.param),
   					error: error_event,
   					success:success_event ,
   					complete: complete_event
   				});

   	  	 	}
   	  	 	return self;
   	  	 }
   	  })
   }
)(jQuery)