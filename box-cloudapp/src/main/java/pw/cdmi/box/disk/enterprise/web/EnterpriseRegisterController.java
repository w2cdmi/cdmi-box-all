package pw.cdmi.box.disk.enterprise.web;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pw.cdmi.box.disk.enterprise.domain.WxworkAuthCode;
import pw.cdmi.box.disk.files.web.CommonController;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.JsonUtils;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Controller
@RequestMapping(value = "/register")
public class EnterpriseRegisterController extends CommonController {
/*
    private static Logger LOGGER = LoggerFactory.getLogger(EnterpriseRegisterController.class);

*/
    @Resource
    RestClient uamClientService;

    private String suiteId = "tje32d93de35487681";

    private String redirectUrl = "http://www.jmapi.cn/ecm/wxEvent/install";

    private String wwAppId = "wwc7342fa63c523b9a";
    private String wwRedirectUrl = "https://www.jmapi.cn/?qr=ww";

    public String getSuiteId() {
        return suiteId;
    }

    public void setSuiteId(String suiteId) {
        this.suiteId = suiteId;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getWwAppId() {
        return wwAppId;
    }

    public void setWwAppId(String wwAppId) {
        this.wwAppId = wwAppId;
    }

    public String getWwRedirectUrl() {
        return wwRedirectUrl;
    }

    public void setWwRedirectUrl(String wwRedirectUrl) {
        this.wwRedirectUrl = wwRedirectUrl;
    }

    /**
     * 获取企业微信相关的授权码和注册码
     */
    @RequestMapping(value = "/wxwork", method = RequestMethod.GET)
    public String registerByWxwork(Model model) {
        try {
            TextResponse response = uamClientService.performJsonPostTextResponse("/api/v2/wxwork/authCode", null, null);

            if(response.getStatusCode() == HttpStatus.OK.value()) {
                WxworkAuthCode authCode = JsonUtils.stringToObject(response.getResponseBody(), WxworkAuthCode.class);

                //应用授权
                String preauthCode = authCode.getPreauthCode();
                model.addAttribute("suiteId", suiteId);
                model.addAttribute("preauthCode", preauthCode);
                model.addAttribute("redirectUrl", URLEncoder.encode(redirectUrl, "UTF-8"));

                //注册微信
                String registerCode = authCode.getRegisterCode();
                model.addAttribute("registerCode", registerCode);
            }

            model.addAttribute("wwAppId", wwAppId);
            model.addAttribute("wwRedirectUrl", URLEncoder.encode(wwRedirectUrl, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return "register";
    }
}