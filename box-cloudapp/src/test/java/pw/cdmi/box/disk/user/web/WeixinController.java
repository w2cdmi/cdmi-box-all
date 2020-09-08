package pw.cdmi.box.disk.user.web;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.qq.weixin.mp.aes.AesException;
import com.qq.weixin.mp.aes.SHA1;

import pw.cdmi.box.disk.declare.manager.UserSignDeclareManager;
import pw.cdmi.box.disk.files.web.CommonController;
import pw.cdmi.box.disk.system.service.ClientManageService;
import pw.cdmi.box.disk.user.service.UserService;
import pw.cdmi.box.disk.utils.CSRFTokenManager;
import pw.cdmi.box.disk.weixin.service.WeixinOauth2Service;

/*最新版的微信端入口，使用AJAX与后台进行交互*/
@Controller
@RequestMapping(value = "/wx")
public class WeixinController extends CommonController {
    private String appId = "1000006";

    @Autowired
    private ClientManageService clientManageService;

    @Autowired
    private UserSignDeclareManager userSignDeclareManager;

    @Autowired
    private UserService userService;

    @Autowired
    private WeixinOauth2Service weixinOauth2Service;

    @RequestMapping(method = RequestMethod.GET)
    public String enter(Model model, HttpServletRequest request) {
/*
        UserToken userToken = (UserToken) SecurityUtils.getSubject().getPrincipal();

*/
        model.addAttribute("token", CSRFTokenManager.getTokenForSession(request.getSession()));
        model.addAttribute("appId", appId);

        String timestamp = String.valueOf(System.currentTimeMillis());
        String ticket = weixinOauth2Service.getJsApiTicket();
        String nonce = RandomStringUtils.random(16);
        model.addAttribute("timestamp", timestamp);
        model.addAttribute("nonceStr", nonce);
        model.addAttribute("signature", sha1(ticket, timestamp, nonce, "http://www.jmapi.cn/wx"));

        return "wx/index";
    }

    protected String sha1(String token, String timestamp, String nonce, String url) {
        try {
            return SHA1.getSHA1("jsapi_ticket=" + token, "timestamp=" + timestamp, "noncestr=" + nonce, "url=" + url);
        } catch (AesException e) {
            e.printStackTrace();
        }
        return null;
    }
}
