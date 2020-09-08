package com.huawei.sharedrive.uam.openapi.rest;

import com.huawei.sharedrive.uam.anon.service.EnterpriseBySelfService;
import com.huawei.sharedrive.uam.authapp.service.AuthAppService;
import com.huawei.sharedrive.uam.authserver.service.AuthServerService;
import com.huawei.sharedrive.uam.config.domain.EnterpriseAccountProfile;
import com.huawei.sharedrive.uam.config.service.SystemProfileService;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseAccountManager;
import com.huawei.sharedrive.uam.enterprise.service.EnterpriseService;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.exception.BaseRunException;
import com.huawei.sharedrive.uam.oauth2.domain.UserToken;
import com.huawei.sharedrive.uam.oauth2.service.impl.UserTokenHelper;
import com.huawei.sharedrive.uam.openapi.domain.*;
import com.huawei.sharedrive.uam.openapi.domain.user.CheckResult;
import com.huawei.sharedrive.uam.openapi.manager.impl.LoginManagerImpl;
import com.huawei.sharedrive.uam.organization.domain.EnterpriseUserDept;
import com.huawei.sharedrive.uam.organization.service.EnterpriseUserDeptService;
import com.huawei.sharedrive.uam.user.service.UserImageService;
import com.huawei.sharedrive.uam.util.PropertiesUtils;
import com.huawei.sharedrive.uam.weixin.domain.WxUser;
import com.huawei.sharedrive.uam.weixin.domain.WxUserEnterprise;
import com.huawei.sharedrive.uam.weixin.rest.WxMpUserInfo;
import com.huawei.sharedrive.uam.weixin.service.WxOauth2Service;
import com.huawei.sharedrive.uam.weixin.service.WxProviderService;
import com.huawei.sharedrive.uam.weixin.service.WxUserEnterpriseService;
import com.huawei.sharedrive.uam.weixin.service.impl.WxUserServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pw.cdmi.common.domain.AuthServer;
import pw.cdmi.common.domain.enterprise.Enterprise;
import pw.cdmi.core.exception.CheckCodeFailedException;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.JsonUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/* 用于获取企业微信的预授权码和注册码*/
@Controller
@RequestMapping(value = "/api/v2/wxmp/authCode")
public class WxMPAuthAPIController {
	@Autowired
	private WxProviderService wxProviderService;

	private String templateId = "tpl3f3220e53299307e";

	@Autowired
	private EnterpriseUserDeptService userDeptService;
	@Autowired
	private EnterpriseAccountManager enterpriseAccountManager;
	@Autowired
	WxOauth2Service wxOauth2Service;

	@Autowired
	private AuthAppService authAppService;

	@Autowired
	private SystemProfileService systemProfileService;

	@Autowired
	private EnterpriseBySelfService enterpriseBySelfService;

	@Autowired
	private WxUserEnterpriseService wxUserEnterpriseService;

	@Autowired
	private AuthServerService authServerService;

	@Autowired
	private EnterpriseService enterpriseService;

	@Autowired
	private WxUserServiceImpl wxUserServiceImpl;

	@Autowired
	private LoginManagerImpl loginManagerImpl;

	@Autowired
	private UserImageService userImageService;

	@Autowired
	private UserTokenHelper userTokenHelper;

	@Autowired
	private RestClient restClient;

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<RestWxworkAuthCodeResponse> enterpriseRegisterByWxmp(@RequestBody RestWxMpUserLoginRequest loginRequest, HttpServletRequest request) throws IOException {
		RestWxworkAuthCodeResponse res = new RestWxworkAuthCodeResponse();
		// 应用授权
		String preauthCode = wxProviderService.getPreAuthCode();
		res.setPreauthCode(preauthCode);
		// 注册微信
		String registerCode = wxProviderService.getRegisterCode(templateId);
		res.setRegisterCode(registerCode);
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

	// 企业自注册
	@RequestMapping(value = "/registerbyWxmp", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> enterpriseRegisterByWxwork(@RequestBody RestWxMpRegisterRequest registerRequest, HttpServletRequest request) throws BaseRunException, IOException {
		if (!checkCode(registerRequest.getPhone(), registerRequest.getCheckCode())) {
			return new ResponseEntity<CheckCodeFailedException>(new CheckCodeFailedException(),HttpStatus.OK);
		}
		WxMpUserInfo mpUserInfo = wxOauth2Service.getWxMpUserInfo(registerRequest.getMpId(), registerRequest.getCode(), registerRequest.getIv(), registerRequest.getEncryptedData());
		Enterprise enterprise = new Enterprise();
		enterprise.setContactEmail(registerRequest.getPhone() + "@storbox.cn");
		enterprise.setContactPerson(mpUserInfo.getNickName());
		enterprise.setContactPhone(registerRequest.getPhone());
		enterprise.setDomainName(System.currentTimeMillis() + "");
		enterprise.setName(registerRequest.getName());
		enterprise.setIsdepartment(true);
		RestEnterpriseAccountRequest enterpriseAccountRequest = new RestEnterpriseAccountRequest();
		String appId = authAppService.getDefaultWebApp().getAuthAppId();
		EnterpriseAccountProfile profile = systemProfileService.buildEnterpriseAccountProfile(appId);
		enterpriseAccountRequest.setMaxMember(profile.getMaxUserAmount());
		enterpriseAccountRequest.setMaxSpace(profile.getMaxTeamspaceQuota());
		enterpriseAccountRequest.setMaxTeamspace(profile.getMaxTeamspaceAmount());
		MockHttpServletRequest createRequest = new MockHttpServletRequest();
		createRequest.addHeader("x-forwarded-for", "qyapi.weixin.qq.com");
		createRequest.addPreferredLocale(Locale.SIMPLIFIED_CHINESE);
		EnterpriseUser enterpriseUser = enterpriseBySelfService.enterpriseRegister(enterprise, enterpriseAccountRequest, mpUserInfo.getNickName(), mpUserInfo.getUnionId(), createRequest);

		WxUserEnterprise wxUserEnterprise = new WxUserEnterprise();
		wxUserEnterprise.setEnterpriseId(enterprise.getId());
		wxUserEnterprise.setEnterpriseUserId(enterpriseUser.getId());
		wxUserEnterprise.setUnionId(mpUserInfo.getUnionId());
		wxUserEnterprise.setModifiedAt(new Date());
		wxUserEnterprise.setModifiedAt(new Date());
		wxUserEnterpriseService.create(wxUserEnterprise);

		EnterpriseUserDept enterpriseUserDept = new EnterpriseUserDept(enterpriseUser.getId(), enterprise.getId(), 0);
		userDeptService.create(enterpriseUserDept);

		if (StringUtils.isNotBlank(mpUserInfo.getAvatarUrl())) {
			long accountId = enterpriseAccountManager.getByEnterpriseId(enterprise.getId()).get(0).getAccountId();
			userImageService.updateUserImage(enterpriseUser.getId(), accountId, mpUserInfo.getAvatarUrl());
		}
		return new ResponseEntity<Enterprise>(enterprise,HttpStatus.OK);
	}

	// 企业自注册
	@RequestMapping(value = "/registerUser", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> registerUser(@RequestBody RestWxMpUserRegister registerRequest, HttpServletRequest request) throws BaseRunException, IOException {
		if (!checkCode(registerRequest.getPhone(), registerRequest.getCheckCode())) {
			return new ResponseEntity<CheckCodeFailedException>(new CheckCodeFailedException(),HttpStatus.OK);
		}
		WxMpUserInfo mpUserInfo = wxOauth2Service.getWxMpUserInfo(registerRequest.getMpId(), registerRequest.getCode(), registerRequest.getIv(), registerRequest.getEncryptedData());
		Enterprise enterprise = enterpriseService.getById(registerRequest.getEnterpriseId());
		WxUser wxUser = wxUserServiceImpl.getByUnionId(mpUserInfo.getUnionId());
		if (wxUser == null) {
			loginManagerImpl.openAccount(mpUserInfo, null);
			wxUser = wxUserServiceImpl.getByUnionId(mpUserInfo.getUnionId());
		}
		WxUserEnterprise old = wxUserEnterpriseService.getByUnionIdAndEnterpriseId(mpUserInfo.getUnionId(), registerRequest.getEnterpriseId());
		if (old != null) {
			return new ResponseEntity<String>(HttpStatus.OK);
		}
		EnterpriseUser enterpriseUser = toEnterpriseUser(wxUser);
		enterpriseUser.setEnterpriseId(registerRequest.getEnterpriseId());
		if (registerRequest.getName() != null && !"".equals(registerRequest.getName())) {
			enterpriseUser.setName(registerRequest.getName());
		} else {
			enterpriseUser.setName(wxUser.getNickName());
		}
		if (registerRequest.getPhone() != null && "".equals(registerRequest.getPhone())) {
			enterpriseUser.setMobile(registerRequest.getPhone());
		} else {
			enterpriseUser.setMobile(wxUser.getMobile());
		}

		enterpriseUser.setMobile(registerRequest.getPhone());
		AuthServer authServer = authServerService.getByEnterpriseIdType(registerRequest.getEnterpriseId(), "LocalAuth");

		enterpriseBySelfService.createEnterpriseUser(request, enterprise, enterpriseUser, authServer, false, mpUserInfo.getUnionId());
		WxUserEnterprise wxUserEnterprise = new WxUserEnterprise();
		wxUserEnterprise.setEnterpriseId(enterprise.getId());
		wxUserEnterprise.setEnterpriseUserId(enterpriseUser.getId());
		wxUserEnterprise.setUnionId(mpUserInfo.getUnionId());
		wxUserEnterprise.setModifiedAt(new Date());
		wxUserEnterprise.setModifiedAt(new Date());
		if (wxUserEnterpriseService.getByUnionIdAndEnterpriseId(mpUserInfo.getUnionId(), enterprise.getId()) == null) {
			wxUserEnterpriseService.create(wxUserEnterprise);
		} else {
			wxUserEnterpriseService.update(wxUserEnterprise);
		}
		EnterpriseUserDept enterpriseUserDept = new EnterpriseUserDept(enterpriseUser.getId(), registerRequest.getEnterpriseId(), registerRequest.getDeptId());
		userDeptService.create(enterpriseUserDept);

		if (StringUtils.isNotBlank(mpUserInfo.getAvatarUrl())) {
			long accountId = enterpriseAccountManager.getByEnterpriseId(registerRequest.getEnterpriseId()).get(0).getAccountId();
			userImageService.updateUserImage(enterpriseUser.getId(), accountId, mpUserInfo.getAvatarUrl());
		}
		return new ResponseEntity<EnterpriseUser>(enterpriseUser,HttpStatus.OK);
	}

	protected EnterpriseUser toEnterpriseUser(WxUser wxUser) {
		EnterpriseUser user = new EnterpriseUser();
		user.setName(wxUser.getUnionId());
		user.setAlias(wxUser.getNickName());
		user.setMobile(wxUser.getMobile());
		user.setEmail(wxUser.getEmail());
		user.setType(EnterpriseUser.TYPE_MEMBER);
		if (user.getObjectSid() == null) {
			user.setObjectSid(user.getName());
		}
		return user;
	}

	// 获取我邀请的用户
	@RequestMapping(value = "/inviterByMe", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> listInviterByMe(@RequestBody RestPageWxMpRequest restWxMpRequest, HttpServletRequest request) throws BaseRunException, IOException {
		WxMpUserInfo mpUserInfo = wxOauth2Service.getWxMpUserInfo(restWxMpRequest.getMpId(), restWxMpRequest.getCode(), restWxMpRequest.getIv(), restWxMpRequest.getEncryptedData());
		//test
//		WxMpUserInfo mpUserInfo = new WxMpUserInfo();
//		mpUserInfo.setUnionId("003nOGzm0xUESl1mt2Cm0xCLzm0nOGzB");
		List<WxUser> wxUserList = wxUserServiceImpl.listByInviterId(mpUserInfo.getUnionId(), restWxMpRequest.getOrderList(), restWxMpRequest.getLimit());
		return new ResponseEntity<>(wxUserList, HttpStatus.OK);
	}

	// 获取微信信息
	@RequestMapping(value = "/info", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> wxUserInfo(@RequestHeader("Authorization") String authorization, HttpServletRequest request) throws BaseRunException, IOException {

		UserToken userToken = userTokenHelper.checkTokenAndGetUser(authorization);
		WxUser wxuser = wxUserServiceImpl.getCloudUserId(userToken.getCloudUserId());
		return new ResponseEntity<>(wxuser, HttpStatus.OK);
	}



    private boolean checkCode(String mobile, String checkCode) {
        String restCdmiUrl = PropertiesUtils.getProperty("rest.cdmi.url");
        if (restCdmiUrl != null && !restCdmiUrl.equals("")) {
            CheckCode checkCodeObj=new CheckCode();
            checkCodeObj.setMobile(mobile);
            checkCodeObj.setCode(checkCode);
            TextResponse textResponse = restClient.performJsonPutTextResponseByUri(restCdmiUrl + "/msm/messages/v1/sms/checkcode", null, checkCodeObj);
            if (textResponse.getStatusCode() == HttpStatus.OK.value()) {
                CheckResult result = JsonUtils.stringToObject(textResponse.getResponseBody(), CheckResult.class);
                if(result.getCheck().equals("true")){
                    return true;
                }else {
                    return false;
                }
            }
            throw new RuntimeException(textResponse.getResponseBody());
        }
        return true;
    }

}
