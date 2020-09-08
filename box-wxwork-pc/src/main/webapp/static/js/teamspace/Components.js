/**
 * Created by quxiangqian on 2017/12/26.
 */
(
    function ($) {
        $.fn.extend({
            popover:function (target,ismouse,align) {
                var self=this;
                self.mouseenter(function(e){
                    e.preventDefault();

                    target.show();
                    if(ismouse)
                    {
                        target.css("position","fixed")
                        var top=self.offset().top+self.height();
                        var left=self.offset().left;
                        if(align=="left"){

                        }
                        if(align=="center"){

                        }
                        if(align=="right"){
                           left=left-target.width()+self.width();
                        }
                        target.css({left:left,top:top})
                    }
                });
                target.mouseleave(function (e) {
                    e.preventDefault();
                    target.hide();
                })
                target.mousedown(function (e) {
                    e.stopPropagation();
                    e.preventDefault();
                })
                $(document).mousedown(function () {
                    target.hide();
                })
            }
        })

        $.fn.extend({
            dialog:function (config) {
                var self=this;
                if(!$("body div[dialogs='true']").is("div")){
                    $("body").append('<div dialogs="true" class="dialogs"></div>')
                }
                var dialogs=$("body div[dialogs='true']");
                self.init=function () {
                   self.model=$('<div class="model"></div>')
                   self.dialogrect=$('<div class="dialogrect"><div class="head">'+config.title+'<i class="fa fa-close" id="close"></i></div><div class="body"></div></div>');
                   self.closetag=self.dialogrect.find("#close");
                   self.dialogrect.find(">.body").append(self);
                   self.dialog=$('<div class="dialog"></div>');
                   self.dialog.append(self.model);
                   self.dialog.append(self.dialogrect);
                   dialogs.append(self.dialog);
                   self.closetag.mouseup(function () {
                       self.hide();
                   })
                   return self;
                }
                self.size=function(){
                    return self;
                }
                self.show=function(){
                    self.dialog.show();
                    var level=Date.parse(new Date())/1000;
                    var _left=($(window).width()-self.dialogrect.outerWidth())/2;
                    var _top=($(window).height()-self.dialogrect.outerHeight())/2;
                    self.dialogrect.css({left:_left,top:_top})
                    $(window).resize(function() {  
                        var _left=($(window).width()-self.dialogrect.outerWidth())/2;
                        var _top=($(window).height()-self.dialogrect.outerHeight())/2;
                        self.dialogrect.css({left:_left,top:_top})
                    });
                    self.dialog.css('z-index',level)
                    return self;
                }
                self.hide=function(){
                    self.dialog.hide();
                    return self;
                }
                return self;




            }
        })
    }
)(jQuery)