package pw.cdmi.box.disk.user.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/browserVersionTips")
public class BrowserVersionTipsController
{
    @RequestMapping(method = RequestMethod.GET)
    public String enter()
    {
        return "user/browserVersionTips";
    }
}
