package pw.cdmi.box.disk.user.web;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;
import pw.cdmi.box.disk.authserver.service.AuthServerService;
import pw.cdmi.box.disk.files.web.CommonController;
import pw.cdmi.box.disk.files.web.FolderController;
import pw.cdmi.box.disk.httpclient.rest.common.Constants;
import pw.cdmi.box.disk.httpclient.rest.request.RestResponse;
import pw.cdmi.box.disk.logininfo.service.LoginInfoService;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.user.domain.*;
import pw.cdmi.box.disk.user.service.AccountUserService;
import pw.cdmi.box.disk.user.service.UserImageService;
import pw.cdmi.box.disk.user.service.UserService;
import pw.cdmi.box.disk.user.service.UserTokenManager;
import pw.cdmi.box.disk.utils.FormValidateUtil;
import pw.cdmi.box.disk.utils.PasswordValidateUtil;
import pw.cdmi.box.disk.utils.RequestUtils;
import pw.cdmi.box.disk.weixin.domain.WxUserEnterprise;
import pw.cdmi.box.disk.weixin.service.WxUserEnterpriseService;
import pw.cdmi.box.disk.wxrobot.service.WxRobotService;
import pw.cdmi.common.domain.AuthServer;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.common.util.signature.SignatureUtils;
import pw.cdmi.core.encrypt.HashPassword;
import pw.cdmi.core.exception.ErrorCode;
import pw.cdmi.core.exception.LoginAuthFailedException;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.restrpc.exception.ServiceException;
import pw.cdmi.core.utils.DateUtils;
import pw.cdmi.core.utils.EDToolsEnhance;
import pw.cdmi.core.utils.HashPasswordUtil;
import pw.cdmi.core.utils.JsonUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/user")
public class UserController extends CommonController {
	private static Logger logger = LoggerFactory.getLogger(FolderController.class);

	@Autowired
	private AccountUserService accountUserService;

	@Resource
	private RestClient uamClientService;

	@Resource
	private RestClient ufmClientService;

	@Autowired
	private UserService userService;

	@Autowired
	private UserTokenManager userTokenManager;

	@Autowired
	private UserImageService userImageService;

	@Autowired
	private AuthServerService authServerService;

	@Autowired
	LoginInfoService loginInfoService;
	
	@Resource
	private WxRobotService  wxRobotService;

	@Autowired
	WxUserEnterpriseService wxUserEnterpriseService;

    private String wxAppId = "wxf54677c64020f6f1";
    private String bindAccountUrl = "https://www.jmapi.cn/ecm/api/v2/wxOauth2/bindWxAccount";
    private String wxRegisterUrl = "https://www.jmapi.cn/personal/register?qr=wx";

	public String getBindAccountUrl() {
		return bindAccountUrl;
	}

	public void setBindAccountUrl(String bindAccountUrl) {
		this.bindAccountUrl = bindAccountUrl;
	}

	public String getWxAppId() {
		return wxAppId;
	}

	public void setWxAppId(String wxAppId) {
		this.wxAppId = wxAppId;
	}

    public String getWxRegisterUrl() {
        return wxRegisterUrl;
    }

    public void setWxRegisterUrl(String wxRegisterUrl) {
        this.wxRegisterUrl = wxRegisterUrl;
    }

    @RequestMapping(value = "account", method = RequestMethod.GET)
	public String account(Model model) {
		User user = (User) SecurityUtils.getSubject().getPrincipal();
		Map<String, String> headerMap = new HashMap<String, String>(1);
		headerMap.put("Authorization", getToken());
		TextResponse restResponse = ufmClientService.performGetText("/api/v2/users/" + user.getCloudUserId(),
				headerMap);
		// 这里的获取是为了得到用户的电话号码
		TextResponse tr = uamClientService.performGetText("/api/v2/users/" + user.getId(), headerMap);
		if ((null == restResponse || restResponse.getStatusCode() != 200) && (null == tr || tr.getStatusCode() != 200)) {
			logger.error("get user interface return code is not 200");
			return "user/settings/account";
		}
		String responseBody = restResponse.getResponseBody();
		if (StringUtils.isBlank(responseBody) && StringUtils.isBlank(tr.getResponseBody())) {
			logger.error("get user return code is 200 but body is null");
			return "user/settings/account";
		}
		RestUserCreateResponse restUserCreateResponse = JsonUtils.stringToObject(responseBody,
				RestUserCreateResponse.class);
		user.setSpaceQuota(restUserCreateResponse.getSpaceQuota());
		user.setSpaceUsed(restUserCreateResponse.getSpaceUsed() == null ? 0 : restUserCreateResponse.getSpaceUsed());
		user.setFileCount(restUserCreateResponse.getFileCount() == null ? 0 : restUserCreateResponse.getFileCount());
		user.setMaxVersions(restUserCreateResponse.getMaxVersions());
		user.setName(restUserCreateResponse.getName());
		if (StringUtils.isNotBlank(restUserCreateResponse.getEmail())) {
			user.setEmail(HtmlUtils.htmlEscape(restUserCreateResponse.getEmail()));
		} else {
			user.setEmail(HtmlUtils.htmlEscape(user.getEmail()));
		}
		user.setDepartment(restUserCreateResponse.getDescription());
		user.setLoginName(user.getLoginName());
		user.setCreatedAt(restUserCreateResponse.getCreatedAt());
		
		Map<String,Object> map = JsonUtils.stringToMap(tr.getResponseBody());
		if(null != map.get("mobile") && StringUtils.isNotEmpty(map.get("mobile").toString())) {
			user.setMobile(map.get("mobile").toString());
		}
		
		model.addAttribute("user", user);

		UserImage userImage = new UserImage();
		User sessUser = (User) SecurityUtils.getSubject().getPrincipal();
		userImage.setAccountId(sessUser.getAccountId());
		userImage.setUserId(sessUser.getId());
		int userId = userImageService.getUserId(userImage);
		if (userId == 0) {
			model.addAttribute("userImage", false);
		} else {
			model.addAttribute("userImage", true);
		}
		model.addAttribute("isLocalAuth", isLocalAuth(user.getAccountId(), user.getId()));

		try {
			WxUserEnterprise wxUserEnterprise = wxUserEnterpriseService.getByEnterpriseUser(user.getId(), user.getEnterpriseId());

			//没有绑定微信账号
			if(wxUserEnterprise == null) {
				String forward = URLEncoder.encode(bindAccountUrl + "?eId=" + user.getEnterpriseId() + "&uId=" + user.getId(),  "UTF-8");
				String url = "https://open.weixin.qq.com/connect/qrconnect?appid=" + wxAppId + "&redirect_uri=" + forward + "&response_type=code&scope=snsapi_login&state=9527#wechat_redirect";

				model.addAttribute("bindUrl", url);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return "user/settings/account";
	}

	//通过微信扫码注册界面
	@RequestMapping(value = "wxRegister", method = RequestMethod.GET)
	public String wxRegister(HttpServletRequest request, Model model) {

        try {
            String corpId = RequestUtils.getCorpId(request);
            model.addAttribute("corpId", corpId);

            //微信扫码
            model.addAttribute("wxAppId", wxAppId);
            model.addAttribute("wxRegisterUrl", URLEncoder.encode(wxRegisterUrl, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            logger.error("", e);
//            e.printStackTrace();
        }

		return "user/wxRegister";
	}

	//微信扫码个人用户
	@RequestMapping(value = "wxRegisterPersonal", method = RequestMethod.GET)
	public String wxRegisterPersonal(HttpServletRequest httpRequest, HttpServletResponse httpResponse, Model model) {
		String code = httpRequest.getParameter("code");
		if(StringUtils.isBlank(code)) {
			code = httpRequest.getParameter("wxCode");
		}

		if(StringUtils.isBlank(code)) {
			model.addAttribute("error", "BlankCode");
			return "user/wxRegisterFail";
		}

		RestOpenWxAccountRequest req = new RestOpenWxAccountRequest();
		req.setCode(code);
		TextResponse response = uamClientService.performJsonPostTextResponse("/api/v2/wxOauth2/openWxAccount", null, req);
		RestResponse result = JsonUtils.stringToObject(response.getResponseBody(), RestResponse.class);
		if(result.getErrorCode() != 0) {
			model.addAttribute("error", result.getErrorMessage());
			return "user/wxRegisterFail";
		}

		//跳转主页面
		return "redirect:/?qr=wx&type=person&code=" + code;
	}

	@RequestMapping(value = "getWxUserBindUrl", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getWxUserBindUrl(HttpServletRequest httpServletRequest) {
		super.checkToken(httpServletRequest);
		User user = (User) SecurityUtils.getSubject().getPrincipal();
		WxUserEnterprise wxUserEnterprise = wxUserEnterpriseService.getByEnterpriseUser(user.getId(), user.getEnterpriseId());
		if (wxUserEnterprise == null) {
			try {
				String forward = URLEncoder.encode(bindAccountUrl + "?eId=" + user.getEnterpriseId() + "&uId=" + user.getId(),  "UTF-8");
				String url = "https://open.weixin.qq.com/connect/qrconnect?appid=" + wxAppId + "&redirect_uri=" + forward + "&response_type=code&scope=snsapi_login&state=9527#wechat_redirect";
				return new ResponseEntity<String>(url, HttpStatus.OK);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		WxRobot wxRobot=wxRobotService.getRunningWxRobot(getToken());
		if(wxRobot!=null){
			return new ResponseEntity<String>("run_yes",HttpStatus.CREATED);
		}else{
			return new ResponseEntity<String>("run_not",HttpStatus.CREATED);
		}
	}
	
	@SuppressWarnings({ "rawtypes" })
	@RequestMapping(value = "/changeEmail", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> doChangeEmail(String email, String password, HttpServletRequest httpServletRequest) {
		super.checkToken(httpServletRequest);
		if (!FormValidateUtil.isValidEmail(email) && !isValidpwd(password)) {
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}

		User sessUser = (User) SecurityUtils.getSubject().getPrincipal();
		EnterpriseUser enterpriseUser = userService.getEnterpriseUserByUserId(sessUser.getAccountId(),
				sessUser.getId());
		HashPassword hashPassword = new HashPassword();
		hashPassword.setHashPassword(enterpriseUser.getPassword());
		hashPassword.setIterations(enterpriseUser.getIterations());
		hashPassword.setSalt(enterpriseUser.getSalt());
		if (!HashPasswordUtil.validatePassword(password, hashPassword)) {
			logger.error("password is not corrected");
			return new ResponseEntity<String>("passwordCorrectedException", HttpStatus.BAD_REQUEST);
		}
		RestUserUpdateRequest request = new RestUserUpdateRequest();
		request.setEmail(email);
		TextResponse restResponse = uamClientService.performJsonPutTextResponse(
				Constants.RESOURCE_TOKEN_CREATE_V2 + sessUser.getId(), getHeaderMap(), request);
		if (null == restResponse) {
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
		RestException exception = JsonUtils.stringToObject(restResponse.getResponseBody(), RestException.class);
		if ("EmailChangeConflict".equals(exception.getCode())) {
			return new ResponseEntity<String>("EmailChangeConflictException", HttpStatus.BAD_REQUEST);
		}
		if (restResponse.getStatusCode() != 200) {
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity(HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/checkUserIsSecurityManager", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> checkUserIsSecurityManager(HttpServletRequest httpServletRequest) {
		super.checkToken(httpServletRequest);
		Map<String, String> headerMap = new HashMap<String, String>(1);
		headerMap.put("Authorization", getToken());
		TextResponse restResponse = uamClientService.performGetText(Constants.RESOURCE_TOKEN_CREATE_V2 +"/checkUserIsSecurityManager",headerMap);
		if (null == restResponse) {
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
		boolean flag=Boolean.parseBoolean(restResponse.getResponseBody());
		return new ResponseEntity(flag,HttpStatus.OK);
	}
	
	@SuppressWarnings({ "rawtypes" })
	@RequestMapping(value = "/changeMobile", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> doChangeMobile(String mobile, String password, HttpServletRequest httpServletRequest) {
		super.checkToken(httpServletRequest);
		if (!FormValidateUtil.isValidMobile(mobile) && !isValidpwd(password)) {
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}

		User sessUser = (User) SecurityUtils.getSubject().getPrincipal();
		EnterpriseUser enterpriseUser = userService.getEnterpriseUserByUserId(sessUser.getAccountId(),
				sessUser.getId());
		HashPassword hashPassword = new HashPassword();
		hashPassword.setHashPassword(enterpriseUser.getPassword());
		hashPassword.setIterations(enterpriseUser.getIterations());
		hashPassword.setSalt(enterpriseUser.getSalt());
		if (!HashPasswordUtil.validatePassword(password, hashPassword)) {
			logger.error("password is not corrected");
			return new ResponseEntity<String>("passwordCorrectedException", HttpStatus.BAD_REQUEST);
		}
		RestUserUpdateRequest request = new RestUserUpdateRequest();
		request.setMobile(mobile);
		TextResponse restResponse = uamClientService.performJsonPutTextResponse(
				Constants.RESOURCE_TOKEN_CREATE_V2 + sessUser.getId(), getHeaderMap(), request);
		if (null == restResponse) {
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
		RestException exception = JsonUtils.stringToObject(restResponse.getResponseBody(), RestException.class);
		if ("MobileChangeConflict".equals(exception.getCode())) {
			return new ResponseEntity<String>("mobileChangeConflictException", HttpStatus.BAD_REQUEST);
		}
		if (restResponse.getStatusCode() != 200) {
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity(HttpStatus.OK);
	}

	/**
	 * 
	 * @param request
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	@RequestMapping(value = "/changePwd", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> doChangePsw(String oldPassword, String password,
			HttpServletRequest httpServletRequest) throws NoSuchAlgorithmException {
		super.checkToken(httpServletRequest);
		if (oldPassword.equals(password)) {
			return new ResponseEntity<String>("PasswordSameException", HttpStatus.BAD_REQUEST);
		}
		User sessUser = (User) SecurityUtils.getSubject().getPrincipal();
		// 根据企业ID查找密码复杂度
		long enterpriseId = sessUser.getEnterpriseId();
		TextResponse response = uamClientService.performJsonPostTextResponse(
				"/api/v2/account/getEnterpriseAccountPwdLevel/" + enterpriseId, null, null);
		String responseBody = response.getResponseBody();
		int pwdLevel = 1;
		if (!StringUtils.isBlank(responseBody)) {
			pwdLevel = Integer.parseInt(responseBody);
		}
		if (StringUtils.isBlank(oldPassword) || !PasswordValidateUtil.isValidPassword(password, pwdLevel)
				|| !PasswordValidateUtil.isValidPassword(oldPassword, 3)) {
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}

		RestUserUpdateRequest request = new RestUserUpdateRequest();
		request.setOldPassword(oldPassword);
		request.setNewPassword(password);
		TextResponse restResponse = uamClientService.performJsonPutTextResponse(
				Constants.RESOURCE_TOKEN_CREATE_V2 + sessUser.getId(), getHeaderMap(), request);
		RestException exception = JsonUtils.stringToObject(restResponse.getResponseBody(), RestException.class);
		if (ErrorCode.USER_LOCKED.getCode().equals(exception.getCode())) {
			return new ResponseEntity<String>("UserLockedException", HttpStatus.BAD_REQUEST);
		}
		if (restResponse.getStatusCode() != 200) {

			return new ResponseEntity<String>("OldPasswordErrorException", HttpStatus.BAD_REQUEST);

		}
		accountUserService.setNoneFirstLogin(sessUser.getAccountId(), sessUser.getId());
		SecurityUtils.getSubject().getSession().setAttribute("isInitPwd", false);
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	public String modifyInfo(String oldPassword, String password, String email, HttpServletRequest httpServletRequest) {

		if (oldPassword.equals(password)) {
			return "PasswordSameException";
		}
		User sessUser = (User) SecurityUtils.getSubject().getPrincipal();
		if (StringUtils.isBlank(oldPassword) || !PasswordValidateUtil.isValidPassword(password, 3)
				|| !PasswordValidateUtil.isValidPassword(oldPassword, 3)) {
			return "PasswordSameException";
		}

		RestUserUpdateRequest request = new RestUserUpdateRequest();
		request.setOldPassword(oldPassword);
		request.setNewPassword(password);

		if (StringUtils.isNotBlank(email)) {
			request.setEmail(email);
		}
		// 根据企业ID查找密码复杂度
//		long enterpriseId = sessUser.getEnterpriseId();
//		TextResponse response = uamClientService.performJsonPostTextResponse(
//				"/api/v2/account/getEnterpriseAccountPwdLevel/" + enterpriseId, null, null);
//		String responseBody = response.getResponseBody();
//		int pwdLevel = 1;
//		if (!StringUtils.isBlank(responseBody)) {
//			pwdLevel = Integer.parseInt(responseBody);
//		}
//		if (StringUtils.isBlank(oldPassword) || !PasswordValidateUtil.isValidPassword(password, pwdLevel)
//				|| !PasswordValidateUtil.isValidPassword(oldPassword, 3)) {
//			return "PasswordSameException";
//		}
		TextResponse restResponse = uamClientService.performJsonPutTextResponse(
				Constants.RESOURCE_TOKEN_CREATE_V2 + sessUser.getId(), getHeaderMap(), request);
		RestException exception = JsonUtils.stringToObject(restResponse.getResponseBody(), RestException.class);
		if (ErrorCode.USER_LOCKED.getCode().equals(exception.getCode())) {
			return "UserLockedException";
		}
		if (restResponse.getStatusCode() != 200) {
			return "OldPasswordErrorException";
		}

		if ("EmailChangeConflict".equals(exception.getCode())) {
			return "EmailChangeConflictException";
		}
		accountUserService.setNoneFirstLogin(sessUser.getAccountId(), sessUser.getId());
		SecurityUtils.getSubject().getSession().setAttribute("isInitPwd", false);
		return "ok";
	}

	/**
	 * 
	 * @param request
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	@RequestMapping(value = "/firstLoginInit", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> doFirstLoginInit(String oldPassword, String password, String email,
			HttpServletRequest httpServletRequest) throws NoSuchAlgorithmException {
		String modifyInfo = modifyInfo(oldPassword, password, email, httpServletRequest);
		if (!"ok".equals(modifyInfo)) {
			return new ResponseEntity<String>(modifyInfo, HttpStatus.BAD_REQUEST);
		}

		SecurityUtils.getSubject().getSession().setAttribute("isNeedChangePassword","false");
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@RequestMapping(value = "settings", method = RequestMethod.GET)
	public String enter() {
		return "user/settings/index";
	}

	@RequestMapping(value = "/info", method = RequestMethod.GET)
	public ResponseEntity<User> getUserInfo() {
		User user = (User) SecurityUtils.getSubject().getPrincipal();
		Map<String, String> headerMap = new HashMap<String, String>(2);

		String dateStr = DateUtils.dataToString(DateUtils.RFC822_DATE_FORMAT, new Date(), null);
		EnterpriseAccount userAccount = userService.getEnterpriseAccountByCloudUserId(user.getCloudUserId());
		String decodedKey = EDToolsEnhance.decode(userAccount.getSecretKey(), userAccount.getSecretKeyEncodeKey());
		String sign = SignatureUtils.getSignature(decodedKey, dateStr);

		String authorization = "account," + userAccount.getAccessKeyId() + ',' + sign;

		headerMap.put("Authorization", authorization);
		headerMap.put("Date", dateStr);
		TextResponse restResponse = ufmClientService.performGetText("/api/v2/users/" + user.getCloudUserId(),
				headerMap);
		if (restResponse.getStatusCode() != 200) {
			logger.error("get user return code is " + restResponse.getStatusCode());
			throw new LoginAuthFailedException();
		}
		String responseBody = restResponse.getResponseBody();
		RestUserCreateResponse restUserCreateResponse = JsonUtils.stringToObject(responseBody,
				RestUserCreateResponse.class);
		user.setSpaceQuota(restUserCreateResponse.getSpaceQuota() == null ? 0 : restUserCreateResponse.getSpaceQuota());
		user.setSpaceUsed(restUserCreateResponse.getSpaceUsed() == null ? 0 : restUserCreateResponse.getSpaceUsed());
		user.setFileCount(restUserCreateResponse.getFileCount() == null ? 0 : restUserCreateResponse.getFileCount());
		user.setName(HtmlUtils.htmlEscape(user.getName()));
		user.setEmail(HtmlUtils.htmlEscape(user.getEmail()));
		user.setDepartment(HtmlUtils.htmlEscape(user.getDepartment()));
		user.setLoginName(HtmlUtils.htmlEscape(user.getLoginName()));
		user.setMaxVersions(restUserCreateResponse.getMaxVersions());
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}

	@RequestMapping(value = "/goChangeEmail", method = RequestMethod.GET)
	public String goChangeEmail(Model model) {
		try {
			UserToken sessUser = (UserToken) SecurityUtils.getSubject().getPrincipal();
			Map<String, String> headerMap = new HashMap<String, String>(2);
			headerMap.put("Authorization", sessUser.getToken());
			headerMap.put("Date", "");
			TextResponse restResponse = uamClientService
					.performGetText(Constants.RESOURCE_TOKEN_CREATE_V2 + sessUser.getId(), headerMap);
			if (null != restResponse && restResponse.getStatusCode() == 200) {
				String responseBody = restResponse.getResponseBody();
				User responseUser = JsonUtils.stringToObject(responseBody, User.class);
				model.addAttribute("email", responseUser.getEmail());
			}
		} catch (ServiceException e) {
			logger.warn("Don't get user email!", e);
		}
		return "common/localUserChgEmail";
	}

	@RequestMapping(value = "/goChangeMobile", method = RequestMethod.GET)
	public String goChangeMobile(Model model) {
		try {
			UserToken sessUser = (UserToken) SecurityUtils.getSubject().getPrincipal();
			Map<String, String> headerMap = new HashMap<String, String>(2);
			headerMap.put("Authorization", sessUser.getToken());
			headerMap.put("Date", "");
			TextResponse restResponse = uamClientService
					.performGetText(Constants.RESOURCE_TOKEN_CREATE_V2 + sessUser.getId(), headerMap);
			if (null != restResponse && restResponse.getStatusCode() == 200) {
				String responseBody = restResponse.getResponseBody();
				User responseUser = JsonUtils.stringToObject(responseBody, User.class);
				model.addAttribute("mobile", responseUser.getMobile());
			}
		} catch (ServiceException e) {
			logger.warn("Don't get user mobile!", e);
		}
		return "common/localUserChgMobile";
	}

	@RequestMapping(value = "/goChangePwd", method = RequestMethod.GET)
	public String goChangePwd() {
		return "common/localUserChgPwd";
	}

	@RequestMapping(value = "/personal", method = RequestMethod.GET)
	public String personal(Model model) {
		User user = (User) SecurityUtils.getSubject().getPrincipal();
		model.addAttribute("user", user);

		return "user/personal";
	}

	/*用户协议*/
	@RequestMapping(value = "/agreement", method = RequestMethod.GET)
	public String agreement(Model model) {
		return "user/agreement";
	}

	@Override
	protected String getToken() {
		return userTokenManager.getToken();
	}

	private Map<String, String> getHeaderMap() {
		UserToken sessUser = (UserToken) SecurityUtils.getSubject().getPrincipal();
		Map<String, String> headerMap = new HashMap<String, String>(2);
		String dateStr = DateUtils.dataToString(DateUtils.RFC822_DATE_FORMAT, new Date(), null);
		headerMap.put("Authorization", sessUser.getToken());
		headerMap.put("Date", dateStr);
		return headerMap;
	}

	private boolean isLocalAuth(long accountId, long userId) {
		try {
			EnterpriseUser enterpriseUser = userService.getEnterpriseUserByUserId(accountId, userId);
			AuthServer authServer = authServerService.getAuthServer(enterpriseUser.getUserSource());
			return AuthServerService.AUTH_TYPE_LOCAL.equals(authServer.getType());
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean isValidpwd(String pwd) {
		if (pwd == null) {
			return false;
		}
		pwd = pwd.trim();
		if (pwd.length() > 127 || pwd.length() < 8) {
			return false;
		}
		return true;
	}
}
