    var catalogData = null;
    var keyword = null;
    var files;
    var flag = "0";
    var url = "";
    var curTotalPage = 1;
    var curNodeId;
    var cutOwnerId;
    $(function () {
        // init();
        creatFolder()
        sortShowHide()
    	//退出查看版本
    	$(".version-info-tail").click(function(){
    		$(".version-info").hide();
    		$("#fileVersionList").html("");
    	});
        if(parentId != 0){
            changeBreadcrumb(parentId);
        }

        var fileList = $("#fileListWrapper").FileList({
            ownerId:curUserId
        })
        fileList.showListInit()
    });
// 文件操作弹出层
    function onPress(e){
        e.stopPropagation()
            var $target = $(e.currentTarget);
            var node = $target.data("node");
            var actions=[];

        actions.push({
            text: "共享",
            className: "color-primary",
            onClick: function() {
                showShareDialog(node);
            }
        });
        actions.push( {
            text: "外发",
            className: "color-primary",
            onClick: function() {
                showLinkDialog(node);
            }
        });
        if (node.type <= 0) {
            //普通文件夹
             actions.push({
                    text: "设为快捷目录",
                    className: "color-primary",
                    onClick: function () {
                        addShortcutFolder(node);
                    }
                });
                actions.push({
                    text: "文件夹信息",
                    className: "color-primary",
                    onClick: function () {
                        showFileProperties(node);
                    }
                });

            }
            actions.push({
                text: "重命名",
                className: "color-primary",
                onClick: function() {
                    renameDialog(node);
                }
            });

            actions.push({
                text: "移动到...",
                className: "color-primary",
                onClick: function() {
                    var id = $target.parent('.line-scroll-wrapper').attr("value");
                    var ownedBy = $target.parent('.line-scroll-wrapper').attr("name");
                    showMoveToDialog(node, id, ownedBy);
                }
            });
            actions.push({
                text: "删除",
                className: "color-primary",
                onClick: function() {
                    deleteFile(node);
                }
            });
        actions.push({
            text: "另存为...",
            className: "color-primary",
            onClick: function(e) {
                showCopyToDialogTeam(node);
            }
        });
            if(node.type==1){
                actions.push({
                    text: "文件信息",
                    className: "color-primary",
                    onClick: function() {
                        showFileProperties(node);
                    }
                });
                actions.push({
                    text: "查看版本",
                    className: "color-primary",
                    onClick: function() {
                        optionVersionFile(node);
                    }
                });
            }
            $.actions({
                title: node.name,
                actions:  actions
            });
        layelTitle(node.divClass,node.menderName,node.modifiedAt)
    }
    /*从搜索结果中跳转时， 查询所有的父目录，构造面包屑导航 */
    function changeBreadcrumb(fileId) {
        var breadcrumbItem = "";
        var url = host+"/ufm/api/v2/nodes/"+ownerId+"/"+fileId+"/path";
        $.ajax({
            type: "GET",
            url: url,
            cache: false,
            async: false,
            success: function (data) {
                var $directory = $("#directory");
                $directory.find("div:gt(0)").remove();
                if (data.length > 0) {
                    for (var i = 0; i < data.length; i++) {
                        breadcrumbItem =" <p class='bread-arrow-right'></p><div id='jump_"+ data[i].id +"' data-info='"+ JSON.stringify(data[i]) +"' onclick=\"jumpFolder(this,"+ data[i].id +");\">"+data[i].name+"&nbsp;</div>";
                        $directory.append(breadcrumbItem);
                    }
                }
            },
            error: function(xhr, status, error){
                $.toast('获取目录失败', 'forbidden');
            }
        });
    }
	
    function viewImg(nodeIdTmp, fileName) {
        var inodeId = nodeIdTmp;
        var flag;
        if (inodeId == undefined) {
            var node = $("#fileList").getGridSelectedData(catalogData, opts_viewGrid);
            inodeId = node[0].id;
            fileName = node[0].name;
        }
        curNodeId = inodeId;
        cutOwnerId = ownerId;
        $.ajax({
            type: "GET",
            async: false,
            url: "${ctx}/views/getViewFlag/" + ownerId + "/" + inodeId + "?" + Math.random(),
            success: function (data) {
            	
                var data = $.parseJSON(data);
                flag = data.viewFlag;
                if (data.isSizeLarge) {
                    ymPrompt.alert({
                        title: fileName,
                        message: "<spring:message code='preview.isSizeLarge'/>",
                    });
                    return;
                }
                url = '${ctx}/views/viewInfo/' + ownerId + '/' + inodeId + '/' + flag;
                if (parseInt(flag) != 2) {
                    ymPrompt.alert({
                        title: fileName,
                        message: "<spring:message code='preview.getPageView'/>",
                    });
                }
            }
        });

        if (parseInt(flag) == 2) {

            $.ajax({
                type: "GET",
                async: false,
                url: "${ctx}/views/viewMetaInfo/" + ownerId + "/" + inodeId + "/" + "1",
                error: function (xhr, status, error) {

                },
                success: function (data) {
					
                    var previewUrl = data.url;
                    currentPage = data.curPage;
                    curTotalPage = data.totalPage;
                    $("#doc_view_current_page").val(data.curPage);
                    $("#doc_view_totap_page").html(data.totalPage);
                    document.getElementById("doc_ppt_img").src = previewUrl;
                    $("#index_layer2").css("display", "block");
                    $("#filedoc").css("display", "block");

                }
            });
        }
    }

    function linkHandle() {
        $("body").css("overflow", "scroll");
        top.ymPrompt.close();
        if (viewMode == "file") {
            listFile(currentPage, parentId);
        } else {
            doSearch();
        }
    }
    
    function optionVersionFile(node){
    	$(".version-info").show();
    	$("#fileName").html(node.name);
    	$("#fileImage").addClass(getImgHtml(node.type,node.name));
    	fillVersionFileList(node.id);
    }
    
    function fillVersionFileList(nodeId){
    	$.ajax({
            type: "GET",
            url: host + "/ufm/api/v2/files/"+ownerId+"/"+nodeId+"/versions?offset=0&limit=10",
            // data:{
            // 	token:token,
            // 	ownerId:ownerId,
            // 	nodeId:nodeId,
            // 	pageNumber:1,
            // 	desc:true
            // },
            cache: false,
            async: false,
            success: function (data) {
            	var fileList = data.versions;
                if (fileList.length > 0) {
                	var html = "";
                	$("#fileVersionList").html(html);
                    for (var i = 0; i < fileList.length; i++) {
                    	html += "<li id='versionFile_"+fileList[i].id+"'><i><div class='version-icon'>版本</div>"+parseInt(fileList.length-i) +"</i>"
                            +"<div class='versionFile-middle'>"
    					+"<div>上传时间:<span>"+ getFormatDate(new Date(fileList[i].createdAt)) +"</span></div>"
                            +"<div>上传者:&nbsp;<span>"+fileList[i].createdByName+"</span></div>"
    					+"<div>文件大小:<h1>"+ formatFileSize(fileList[i].size) +"</h1></div>"
                            +"</div>"
                            +"<h3 onclick=\"downloadFileByNodeId('"+ fileList[i].id +"')\">下载</h3>";
                    	if (i!=0) {
                    		html += "<h3 onclick=\"deleteFileByNodeId('"+fileList[i].id+"','"+nodeId+"')\">删除</h3><h3 onclick=\"restoreVersion('"+ fileList[i].id +"','"+nodeId+"')\">恢复</h3>"
						}
    					html += "</li>";
                    }
                    $("#fileVersionList").html(html);
                }
            },
            error: function(xhr, status, error){
                $.toast('获取版本文件失败', 'forbidden');
            }
        });
    }
    //topNodeId 这个文件最高版本的nodeId
    function deleteFileByNodeId(nodeId,topNodeId) {
   	 $.ajax({
           type: "POST",
           url: ctx + "/nodes/delete",
           data: {'ownerId': ownerId, 'ids': nodeId, 'token': token},
           error: function (xhr, status, error) {
               var status = xhr.status;
               if (status == 403) {
              	 $.toast("您没有权限进行该操作", "forbidden");
               } else {
              	 $.toast("操作失败，请重试", "forbidden");
               }
           },
           success: function (data) {
           
        	   fillVersionFileList(topNodeId);
           }
       });
   }
    //topNodeId 这个文件最高版本的nodeId
    function restoreVersion(nodeId,topNodeId) {
      	 $.ajax({
              type: "POST",
              url: ctx + "/files/restoreVersion",
              data: {'ownerId': ownerId, 'nodeId': nodeId, 'token': token},
              error: function (xhr, status, error) {
                  var status = xhr.status;
                  if (status == 403) {
                 	 $.toast("您没有权限进行该操作", "forbidden");
                  } else {
                 	 $.toast("操作失败，请重试", "forbidden");
                  }
              },
              success: function (data) {
              		
            	  fillVersionFileList(topNodeId);
              }
          });
      }

    function shareHandle(tp) {
        if (viewMode == "file") {
            listFile(currentPage, parentId);
        } else {
            doSearch();
        }
    }

	function saveSecretLevel(){
		    var item=$("#itemData").data("item");
		    var url = ctx + "/files/updateSecretLevel/"+item.ownedBy+"/"+item.id;
		    var secretLevel=jQuery('input[type="radio"][name="fileSecretLevel"]:checked').val(); // 获取一组radio被选中项的值  
	        var params = {
	            "secretLevel":secretLevel
	        };
	        $.ajax({
	            type: "POST",
	            url: url,
	            data: params,
	            error: function (xhr, status, error) {
	            },
	            success: function (data) {
	            	
	                permission = data;
	            }
	        });
	}

 function setLink(node){
        console.log(node)
	   var iNodeId=node.id;
	   var ownerId=node.ownedBy;
	   var defaultlinKset={
					accessCodeMode:"static",
					accessCode:"",
					download:false,
					preview:false,
					upload:true,
					identities:"",
					token:token,
			}
			$.ajax({
		        type: "POST",
		        url: host + "ufm/api/v2/links/"+ ownerId + "/" +iNodeId,
		        data:defaultlinKset,
		        error: function(xhr, status, error) {
		        	$.toast('操作失败', 'forbidden');
		        },
		        success: function(data) {
		        	$.toast("操作成功");
		        }
		    });
	}

 function copyAndMove() {
     var url = "${ctx}/nodes/copyAndMove/" + ownerId + "?startPoint=operative&endPoint=operative";
     gotoPage(url);
 }

 function doCopyAndMove(tp) {
     var idArray = $("#fileList").getGridSelectedId();
     if (tp == 'copy' || tp == 'move') {
         if (isInMigrationFolder && departureOwnerId) {
             top.ymPrompt.getPage().contentWindow.submitCopyAndMove(tp, departureOwnerId, idArray);
         } else
             top.ymPrompt.getPage().contentWindow.submitCopyAndMove(tp, ownerId, idArray);
     } else if (tp == 'newFolder') {
         top.ymPrompt.getPage().contentWindow.createFolder();
     } else {
         top.ymPrompt.close();
         window.location.reload();
     }
 }
/* var myScroll;

 function loaded () {
 	myScroll = new IScroll('#wrapper', { probeType: 2,mouseWheel: true, click: true} );
 	var handle = 0;//初始为0，无状态；1表示下拉，2表示上拉
 	myScroll.on('scroll', function (){
        if (this.y > 5) {
            handle = 1;
        } else if (this.y < (this.maxScrollY - 5)) {
            handle = 2;
        };
	});
 	myScroll.on('scrollEnd', function () {
          if (handle === 2) {
        	  
        	  if(curTotalPage!=currentPage){
        		  currentPage=currentPage+1;
        		  listFile(currentPage,parentId,false);  
        	  }
           }
           handle = 0;
           myScroll.refresh();
 	 });
 	 document.addEventListener('touchmove', function (e) { 
 		 e.preventDefault(); 
 	 });
 }*/
