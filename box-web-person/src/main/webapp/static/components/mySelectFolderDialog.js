((function($){
   $.fn.extend({
       mySelectFolderDialog: function()
       {
           var self = this
           var dialog = null
           var folderTree = null
           var zTreeObj = null
           var setting = null
           var rootNode = null
           var oids;
           var mids;
           var idss;

           function okButton_Click()
           {
               var nodes = zTreeObj.getSelectedNodes();
               if(oids == idss){
                   alert('移动失败')
                   return
               }
               if(nodes.length > 0){
                   var params = {
                       "destOwnerId": curUserId,
                       "ids": idss,
                       "parentId": nodes[0].id,
                       "startPoint": "operative",
                       "endPoint": "operative",
                       "token": token
                   };
                   $.ajax({
                       type: "POST",
                       url: ctx + "/nodes/renameCopy/" + oids,
                       data: params,
                       error: function(){

                       },
                       success: function () {
                           if(self.onSuccess){
                               self.onSuccess()
                           }
                       }
                   });
                   dialog.hide()
               }else{
                   $.Alert("请选择文件夹")
               }
           }

           self.init0 = function()
           {
               dialog = self.dialog({title:'另存为到其他空间'})
               dialog.init()

               setting = {
                   async: {
                       enable: true,
                       type: 'post',
                       url: ctx + '/folders/listTreeNode/' + ownerId,
                       autoParam:["id"],
                       otherParam: {"orderField": "name", "desc": false, "token": token} ,
                   },
                   simpleData: {
                       enable: true,
                       idKey: "id",
                       pIdKey: "parentId",
                       rootPid: 0
                   }
               };

               rootNode =  {id:0, name: "个人文件", isParent:true, open: true}
               folderTree = self.find('#folderTree')
               self.find('#cancel_button').on('click', function(){
                   dialog.hide()
               })
               self.find('#ok_button').on('click', okButton_Click)
           }

           self.show0 = function(oid,ids)
           {
                idss = ids;
               oids = oid;
            //    mids = modifiedBys;
            //    _node = node
               zTreeObj = $.fn.zTree.init(folderTree, setting, rootNode);
               dialog.show()
           }

           return self
       }
   })
}))(jQuery)