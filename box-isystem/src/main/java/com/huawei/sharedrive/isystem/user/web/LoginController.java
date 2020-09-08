/**
 * 
 */
package com.huawei.sharedrive.isystem.user.web;

import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * LoginController负责打开登录页面(GET请求)和登录出错页面(POST请求)，
 * 
 * 真正登录的POST请求由Filter完成
 * 
 * @author s00108907
 * 
 */
@Controller
@RequestMapping(value = "/login")
public class LoginController
{
    
    /**
     * 打开登录页面
     * 
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public String login()
    {
        return "login";
    }
    
    /**
     * 登录出错后的跳转页面
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
    
    /**
     * 进入错误界面
     * 
     * @return
     */
    @RequestMapping(value = "/turnToError", method = RequestMethod.GET)
    public String goToErrorPage()
    {
        return "common/error";
    }
}
