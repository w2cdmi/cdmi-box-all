package com.huawei.sharedrive.uam.user.web;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/login")
public class LoginController
{
    
    @RequestMapping(method = RequestMethod.GET)
    public String login()
    {
        return "login";
    }
    
    /**
     * 
     * @param userName
     * @param model
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public String fail(@RequestParam(FormAuthenticationFilter.DEFAULT_USERNAME_PARAM) String userName,
        Model model)
    {
        model.addAttribute(FormAuthenticationFilter.DEFAULT_USERNAME_PARAM, userName);
        return "login";
    }
    
    @RequestMapping(value = "/turnToError", method = RequestMethod.GET)
    public String goToErrorPage() {
        return "common/error";
    }

    @RequestMapping(value = "/turnToError/{error}", method = RequestMethod.GET)
    public String goToErrorPage(@PathVariable("error") String error) {
        if(StringUtils.isNotBlank(error)) {
            if("400".equals(error)) {
                return "common/400";
            }

            if("401".equals(error)) {
                return "common/401";
            }

            if("404".equals(error)) {
                return "common/404";
            }

            if("500".equals(error)) {
                return "common/500";
            }
        }

        return "common/error";
    }
}
