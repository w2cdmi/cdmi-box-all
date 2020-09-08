/**
 * Created by quxiangqian on 2017/12/23.
 */

(function($){

    $.fn.extend({
        Menubar:function(){
           var self=this;
           self.find("#discan").popover(self.find("#discan_popover"),true);

        }
    });


})(jQuery)

