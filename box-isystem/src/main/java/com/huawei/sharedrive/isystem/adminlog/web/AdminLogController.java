/**
 * 
 */
package com.huawei.sharedrive.isystem.adminlog.web;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.huawei.sharedrive.isystem.common.web.AbstractCommonController;

/**
 * 
 * 
 * 授权管理
 * 
 * @author d00199602
 * 
 */
@Controller
@RequestMapping(value = "/adminlog/log")
public class AdminLogController extends AbstractCommonController
{
    @RequestMapping(value = "manage", method = RequestMethod.GET)
    public String enter(Model model)
    {
        model.addAttribute("showCopyPlocy", showCopyPlocy);
        
        return "logManage/logManageMain";
    }
    
    @InitBinder
    public void initBinder(ServletRequestDataBinder binder)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }
    
}
