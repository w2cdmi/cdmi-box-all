<script type="text/javascript">
     function setMenu(menuData){
         //-----------------set menu or botton-------------//
		
		var str_menu = "${menuJson}";
		if("" == str_menu) return;
		
		var m_json = $.parseJSON(str_menu);
		
	     for(var m in m_json){      
	       var m_key = null;
	       var left_menu_id = null;
	       switch(m){
	           case "1001":
	               left_menu_id = "navAllFile";
	               break;
	           case "1002":
	               left_menu_id = "navTeamSpace";
	              // $("#" + left_menu_id).parent().prev().remove();
	               break;
	           case "1003":
	               left_menu_id = "navShareToMe";
	               break;
	           case "1004":
	               left_menu_id = "navShareByMe";
	               break;
	           case "1005":
	               left_menu_id = "navTrash";
	               break;
	           case "1006":
	               left_menu_id = "navAPP";
	               break;
	           case "2001":  
	           case "2016":
	              m_key = "m_share";	 
	              break;
	           case "2002":	
	           case "2036":
	              m_key = "m_link";                
	              break;
	           case "2003":	              	              
	              break;
	           case "2005":
	           case "2034":
	              m_key = "m_download";
	              break;
	           case "2006":	  
	              m_key = "m_sync";
	              break;
	           case "2007":	    
	              m_key = "m_turnStore";
	              break;
	           case "2008":	           
	              m_key = "m_move";
	              break;
	           case "2009":
	           case "2023":	
	           case "2032": 
	           case "2040":   
	              m_key = "m_delete";
	              break;
	           case "2010":
	           case "2033":	      
	              m_key = "m_rename";
	              break;
	           case "2011":	
	           case "2035":   
	              m_key = "m_versionList";
	              break;
	           case "2012":	
	           case "2037":    
	              m_key = "m_favorite";
	              break;
	           case "2013":
	              if(m_json[m] == "0"){ $("#uploadBtnBox").remove();$("#uploadBtnBoxForJS").remove()}
	              break;
	           case "2014":	          
	              if(m_json[m] == "0"){$("#newFolderBtn").remove()}
	              break;
	           case "2044":
	              if(m_json[m] == "0"){$("#queryConvertBtn").remove()}
	              break;
	           case "2015":
	           case "2031":
	              m_key = "m_cancelShare";
	              break;      
	           case "2017":
	              m_key = "m_create";
	              if(m_json[m] == "0"){$("#newTeamBtn").remove(); }
	              break;
               case "2018":
                  m_key = "m_memberManagement";
                  break;
               case "2019":
                  m_key = "m_memberView";                
                  break;
               case "2020":
                  m_key = "m_changeOwner";
                  break;
               case "2021":
                  m_key = "m_exit";
                  break;
               case "2022":
                  m_key = "m_detail";
                  break;               
               case "2024":
                  m_key = "m_listLinks";
                  break;
               case "2025":
                  m_key = "m_autoPreview";
                  break;
               case "2026":
                  m_key = "m_priority";
                  break;
               case "2027":
               case "2029":
                  m_key = "m_open";
                  break;
               case "2028":
                  m_key = "m_convert";
                  break;             
                case "2030":
                  m_key = "m_saveToMe";
                  break;  
                case "2038":
	              m_key = "m_cancelLink"
	              break;
	            case "2039":
	              m_key = "m_viewLink"
	              break; 
	            case "2041":
	              m_key = "m_restore";
	              break;	              
	            case "2042":
	            if(m_json[m] == "0"){ 
	              $(".icon-trash-clear").each(function(){
	                  $(this).parent().remove();
	              });
	             }
	              break;
	            case "2043":	            
	            if(m_json[m] == "0"){ 
	              $(".icon-undo").each(function(){
	                  $(this).parent().remove();
	              });
	            }
	              break;                                        
               default:
	              break;
	       }
	       if(menuData && m_key != null && m_json[m] == "0"){	      
				delete menuData[m_key];
		   }	   
		   if(m_key != null && m_json[m] == "0"){
		      $("#listBtn_" + m_key).remove();
		   }  
		   
		    if(m_json[m] == "0" && left_menu_id != null){
	           $("#" + left_menu_id).remove();
	        }          	              
	       
	     }
	     
	     if(m_json["1003"] == "0" && m_json["1004"]== "0"){
	         $(".nav-menu").eq(0).find(".divider").eq(1).hide();
	     }
	     if(m_json["1001"]  == "0" && m_json["1002"] == "0"){
	         $(".nav-menu").eq(0).find(".divider").eq(0).hide();
	     }	  
	     if(m_json["1005"]  == "0"){
	         $(".nav-menu").eq(0).find(".divider").eq(2).hide();
	     }   
	     //------------------------------------//
     
     }   
     
     $(function(){
        setMenu();
     });
     
   
</script>