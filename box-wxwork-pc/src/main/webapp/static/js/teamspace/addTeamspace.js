(function($){

	    
	  $(document).ready(function(){
        var dialog=$('#addTeamspace-dialog').dialog({title:"创建协作空间"})
        dialog.init();
        $("#addTeamspace-btn").click(function(){
            dialog.show();
        });
        $("#cancel-btn").click(function(){
        	dialog.hide();
        })
    }); 
    
    
})(jQuery)
