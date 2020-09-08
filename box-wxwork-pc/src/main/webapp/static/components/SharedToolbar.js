(function($){
    $.fn.extend({
        Toolbar : function()
        {
            var self = this
            var _popover = null
            var newFolderButton = null
            var uploadButton = null
            var keyword = null
            var cancelButton = null
            var searchButton = null
            /**
             * 排序按钮点击事件
             */
            function sortItem_Click()
            {
                var ico = $(this).find('i')
                var visibility = ico.css('visibility')
                _popover.find('dt[id^="orderField_"] > i').css("visibility", "hidden")
                ico.css("visibility", "visible")

                if(visibility == 'hidden'){
                    ico.attr("class", "fa fa-long-arrow-down")
                    $(this).data('isDesc', "DESC")
                }else{
                    if($(this).data('isDesc')){
                        ico.attr("class", "fa fa-long-arrow-up")
                        $(this).data('isDesc', "ASC")
                    }else{
                        ico.attr("class", "fa fa-long-arrow-down")
                        $(this).data('isDesc', "DESC")
                    }
                }

                if(self.onSortItemChange){
                    var orderField = $(this).attr('id').split('_')[1]
                    var isDesc = $(this).data('isDesc')
                    self.onSortItemChange(orderField, isDesc)
                }
            }

            /**
             * 新建文件夹按钮单击事件
             */
            function newFolderButton_Click()
            {
                if(self.onNewFolder){
                    self.onNewFolder()
                }
            }


            self.clearSearch = function()
            {
                keyword.val('')
                cancelButton.hide()
                newFolderButton.show()
                uploadButton.show()
            }

            self.setDefaultSort = function()
            {
                _popover.find('dt[id^="orderField_"] > i').css("visibility", "hidden")
               _popover.find('dt[id="orderField_modifiedAt"] > i')
                   .attr("class", "fa fa-long-arrow-down")
                   .css("visibility", "visible")
                   .parent()
                   .data('isDesc', "DESC")
                _popover.find('dt[id="orderField_name"] > i')
                    .attr("class", "fa fa-long-arrow-down")
                    .parent()
                    .data('isDesc', "ASC")
            }

            self.init = function()
            {
                _popover = self.find('#sort_popover')
                _popover.find('dt#orderField_modifiedAt > i').css("visibility", "visible").parent().data('isDesc', "DESC")
                _popover.find('.menu > dt').on('click', sortItem_Click)
                self.find('#sort_buttons').popover(_popover,true,'center');
                newFolderButton = self.find('#newFolder_button').on('click', newFolderButton_Click)
                uploadButton = self.find('#upload_button')
                var searchform = self.find('#searchform')
                keyword = searchform.find("#keyword")
                cancelButton = searchform.find('.cancel')
                searchButton = searchform.find("#search")

                var eventName = (navigator.userAgent.indexOf("MSIE")!=-1) ? "propertychange" :"input";
                keyword.on(eventName, function(e){
                   e.stopPropagation()

                   if($.trim($(this).val())){
                       cancelButton.show()
                   }else{
                       cancelButton.hide()
                       newFolderButton.show()
                       uploadButton.show()
                       if(self.onCancel){
                           self.onCancel()
                       }
                   }
                })

                keyword.on('keyup', function(e){
                    if(e.keyCode == 13){
                        keyword.blur()
                        if($.trim(keyword.val())){
                            newFolderButton.hide()
                            uploadButton.hide()
                            if(self.onSearch){
                                self.onSearch($.trim(keyword.val()))
                            }
                        }else{
                            $.Alert('请输入查询关键字')
                        }
                    }
                })

                cancelButton.on('click', function(){
                    keyword.val('')
                    $(this).hide()
                    newFolderButton.show()
                    uploadButton.show()

                    if(self.onCancel){
                        self.onCancel()
                    }

                })

                searchButton.on('click', function(){
                    newFolderButton.hide()
                    uploadButton.hide()
                    if(self.onSearch){
                        self.onSearch($.trim(keyword.val()))
                    }
                })
            }

            return self
        }
    })
})(jQuery)