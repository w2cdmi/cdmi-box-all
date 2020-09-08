/**
 * Created by quxiangqian on 2017/12/26.
 */
(
    function ($) {
        $.fn.extend({
            popover:function (target,ismouse,align,callback) {
                var self=this;
                self.mouseenter(function(e){
                    //e.preventDefault();
                    e.stopPropagation();
                    target.show();
                    if(callback){
                        callback(self);
                    }
                    if(ismouse)
                    {
                        target.css("position","fixed")
                        var top=self.offset().top+self.outerHeight()+3;
                        var left=self.offset().left;
                        if(align=="left"){

                        }
                        if(align=="center"){
                            left=left-self.width()/2;
                        }
                        if(align=="right"){
                           left=left-target.width()+self.width();
                        }
                        target.attr("class","popover bottom");

                        console.log("top位置",self.offset().top,$(window).height()/2)
                        if(self.offset().top>$(window).height()/2+target.height()){

                            top=top-target.height()-(self.outerHeight()+10);
                            target.attr("class","popover top");

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
                target.mouseover(function (e) {
                    e.stopPropagation();
                })
                self.mouseover(function (e) {
                    e.stopPropagation();
                })
                $(document).mouseover(function () {
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
                   self.dialogrect=$('<div class="dialogrect" style="position: fixed"><div class="head">'+config.title+'<i class="fa fa-close" id="close"></i></div><div class="body"></div></div>');
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
                self.toCenter=function () {
                    var _left=($(window).width()-self.dialogrect.outerWidth())/2;
                    var _top=($(window).height()-self.dialogrect.outerHeight())/2;
                    self.dialogrect.css({left:_left,top:_top})
                }
                self.show=function(){
                    self.dialog.show();
                    var level=Date.parse(new Date())/1000;
                    var _left=($(window).width()-self.dialogrect.outerWidth())/2;
                    var _top=($(window).height()-self.dialogrect.outerHeight())/2;
                    $(window).resize(function() {
                        var _left=($(window).width()-self.dialogrect.outerWidth())/2;
                        var _top=($(window).height()-self.dialogrect.outerHeight())/2;
                        self.dialogrect.css({left:_left,top:_top})
                    });
                    self.dialogrect.css({left:_left,top:_top})
                    self.dialog.css('z-index',level)
                    return self;
                }
                self.hide=function(){
                    self.dialog.hide();
                    return self;
                }
                self.destory=function(){
                    self.dialog.remove();
                }
                return self;


            }
        })
        
        $.extend({
            Alert:function (msg) {
                var dialoginfo=$("<div style='min-height: 60px;min-width: 150px;'>"+msg+"<div class='abslayout' style='text-align: center;bottom: 10px;left:0;right:0'><button id='ok_button' style='padding: 0 20px;'>确 定</button></div></div>");
                var dialog=dialoginfo.dialog({title:"系统信息"});
                dialog.init();
                dialog.show();
                dialog.hide=function () {
                    dialog.destory();
                }
                dialoginfo.find("#ok_button").mousedown(function(){
                    dialog.destory();
                })
            }
        })

        $.extend({
            Confirm:function (msg,onOk) {
                var dialoginfo=$("<div style='min-height: 60px;min-width: 150px;'>"+msg+"<div class='abslayout' style='text-align: center;bottom: 10px;left:0;right:0'><button id='ok_button' style='padding: 0 20px;'>确 定</button><button id='cancel_button' style='margin-left: 16px;padding: 0 20px;'>取 消</button></div></div>");
                var dialog=dialoginfo.dialog({title:"系统确认"});
                dialog.init();
                dialog.show();
                dialog.hide=function () {
                    dialog.destory();
                }
                dialoginfo.find("#ok_button").mousedown(function(){
                    if(onOk){
                        onOk();
                    }
                    dialog.destory();
                })
                dialoginfo.find("#cancel_button").mousedown(function(){
                    dialog.destory();
                })

            }
        })

        $.extend({
            Tost:function (msg) {
                var dialoginfo=$("<div style='min-height: 60px;min-width: 150px;'>"+msg+"<div class='abslayout' style='text-align: center;bottom: 10px;left:0;right:0'><button id='ok_button' style='padding: 0 20px;'>确 定</button></div></div>");
                var dialog=dialoginfo.dialog();
                var dialogs=$("body div[dialogs='true']");
                dialog.init=function(){

                    dialog.model=$('<div class="model"></div>')
                    dialog.dialogrect=$('<div class="dialogrect" style="background: #444;" ><div class="body" style="color: #fff">'+msg+'</div></div>');
                    dialog.dialog=$('<div class="dialog" ></div>');
                    dialog.dialog.append(dialog.model);
                    dialog.dialog.append(dialog.dialogrect);
                    dialogs.append(dialog.dialog);

                }
                dialog.init();

                dialog.hide=function () {
                    dialog.destory();

                }
                dialog.autoHide=function(s){
                    setTimeout(function () {
                        dialog.destory();
                    },s);
                }

                return dialog;
            }
        })

        $.extend({
            Proccess:function () {
                var dialoginfo=$('<div><p id="msg"></p><div style="background: #ccc;width: 200px;height: 12px;overflow: hidden"><i id="process" style="display: block;background: #fff;padding-top:100px;" >.</i></div></div>');
                var dialog=dialoginfo.dialog();
                var dialogs=$("body div[dialogs='true']");
                dialog.init=function(){

                    dialog.model=$('<div class="model"></div>')
                    dialog.dialogrect=$('<div class="dialogrect" style="background: #444;" ><div class="body" style="color: #fff"></div></div>');
                    dialog.dialogrect.find(">.body").append(dialoginfo)
                    dialog.dialog=$('<div class="dialog" ></div>');
                    dialog.dialog.append(dialog.model);
                    dialog.dialog.append(dialog.dialogrect);
                    dialogs.append(dialog.dialog);

                }

                dialog.setProccess=function(msg,num){
                    dialoginfo.find("#msg").text(msg);
                    dialoginfo.find("#process").css("width",num+"%");
                    if(num==100){
                        dialog.hide();
                    }
                }

                dialog.init();

                dialog.hide=function () {
                    dialog.destory();

                }


                return dialog;
            }
        })
    }
)(jQuery)