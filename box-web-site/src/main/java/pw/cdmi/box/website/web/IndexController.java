package pw.cdmi.box.website.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Controller
@RequestMapping(value = "/")
public class IndexController {
	private static Logger logger = LoggerFactory.getLogger(IndexController.class);

/*
	@Autowired
	private ResourceBundleMessageSource messageSource;
*/

	private String wwAppId = "wwc7342fa63c523b9a";
	private String wwRedirectUrl = "https://www.jmapi.cn/enterprise?qr=ww";

	private String wxAppId = "wxf54677c64020f6f1";
	private String wxEnterpriseRedirectUrl = "https://www.jmapi.cn/enterprise?qr=wx";
	private String wxPersonalRedirectUrl = "https://www.jmapi.cn/personal?qr=wx&type=person";
	private String wxRobotRedirectUrl = "https://www.jmapi.cn/wxrobot/wxBak?qr=wx&type=person";

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

	public String getWxAppId() {
		return wxAppId;
	}

	public void setWxAppId(String wxAppId) {
		this.wxAppId = wxAppId;
	}

	public String getWxEnterpriseRedirectUrl() {
		return wxEnterpriseRedirectUrl;
	}

	public void setWxEnterpriseRedirectUrl(String wxEnterpriseRedirectUrl) {
		this.wxEnterpriseRedirectUrl = wxEnterpriseRedirectUrl;
	}

	public String getWxPersonalRedirectUrl() {
		return wxPersonalRedirectUrl;
	}

	public void setWxPersonalRedirectUrl(String wxPersonalRedirectUrl) {
		this.wxPersonalRedirectUrl = wxPersonalRedirectUrl;
	}

	public String getWxRobotRedirectUrl() {
		return wxRobotRedirectUrl;
	}

	public void setWxRobotRedirectUrl(String wxRobotRedirectUrl) {
		this.wxRobotRedirectUrl = wxRobotRedirectUrl;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String enter(Model model, HttpServletRequest request) {
		setModel(model);
		return "index";
	}

	// 为Web端增加的路径
	@RequestMapping(value = "/price", method = RequestMethod.GET)
	public String price(HttpServletRequest request, Model model) {
		setModel(model);

		return "price";
	}

	@RequestMapping(value = "/download", method = RequestMethod.GET)
	public String download(HttpServletRequest request, Model model) {
		setModel(model);

		return "download";
	}

	@RequestMapping(value = "/wxRobot", method = RequestMethod.GET)
	public String wxRobot(HttpServletRequest request, Model model) {
		setModel(model);

		return "wxRobot";
	}

	private void setModel(Model model) {
		try {
			model.addAttribute("wwAppId", wwAppId);
			model.addAttribute("wwRedirectUrl", URLEncoder.encode(wwRedirectUrl, "UTF-8"));

			model.addAttribute("wxAppId", wxAppId);
			model.addAttribute("wxEnterpriseRedirectUrl", URLEncoder.encode(wxEnterpriseRedirectUrl, "UTF-8"));
			model.addAttribute("wxPersonalRedirectUrl", URLEncoder.encode(wxPersonalRedirectUrl, "UTF-8"));
			model.addAttribute("wxRobotRedirectUrl", URLEncoder.encode(wxRobotRedirectUrl, "UTF-8"));
		} catch (Exception e) {
			logger.error("", e);
		}
	}
}
