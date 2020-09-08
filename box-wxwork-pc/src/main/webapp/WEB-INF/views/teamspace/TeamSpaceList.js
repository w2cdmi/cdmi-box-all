(
   function($){
   	  $.fn.extend({
   	  	 TeamSpaceList:function(){
   	  	 	var self=this;
   	  	 	self.init=function(){
         
   	  	 	}

            function error_event(request)
            {
                  if(request.status == 404) {
							//$.toast('<spring:message code='error.notfound'/>');
				  } else {
							///$.toast('<spring:message code='file.errorMsg.listFileFailed'/>');
				  }
            }

            function success_event(data){
            	if(typeof(data) == 'string' && data.indexOf('<html>') != -1) {
					//window.location = "${ctx}/logout";
					return;
				}
				self.empty();
            }

            function complete_event(){

            }

   	  	 	self.load=function(){
                 $.ajax({
					type: "POST",
					url: self.attr("url"),
					data: params,
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