package com.huawei.sharedrive.uam.openapi.rest.enterprise;

import com.huawei.sharedrive.uam.anon.service.EnterpriseBySelfService;
import com.huawei.sharedrive.uam.authserver.service.AuthServerService;
import com.huawei.sharedrive.uam.common.web.AbstractCommonController;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseAccountManager;
import com.huawei.sharedrive.uam.enterprise.service.EnterpriseService;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.exception.AuthFailedException;
import com.huawei.sharedrive.uam.exception.BaseRunException;
import com.huawei.sharedrive.uam.oauth2.domain.UserToken;
import com.huawei.sharedrive.uam.oauth2.service.UserTokenCacheService;
import com.huawei.sharedrive.uam.oauth2.service.impl.UserTokenHelper;
import com.huawei.sharedrive.uam.openapi.domain.CheckCode;
import com.huawei.sharedrive.uam.openapi.domain.RestUserRegister;
import com.huawei.sharedrive.uam.openapi.domain.user.CheckResult;
import com.huawei.sharedrive.uam.openapi.manager.impl.LoginManagerImpl;
import com.huawei.sharedrive.uam.organization.domain.EnterpriseUserDept;
import com.huawei.sharedrive.uam.organization.service.EnterpriseUserDeptService;
import com.huawei.sharedrive.uam.user.service.UserImageService;
import com.huawei.sharedrive.uam.util.PropertiesUtils;
import com.huawei.sharedrive.uam.weixin.domain.WxUser;
import com.huawei.sharedrive.uam.weixin.domain.WxUserEnterprise;
import com.huawei.sharedrive.uam.weixin.rest.WxUserInfo;
import com.huawei.sharedrive.uam.weixin.service.WxOauth2Service;
import com.huawei.sharedrive.uam.weixin.service.WxUserEnterpriseService;
import com.huawei.sharedrive.uam.weixin.service.impl.WxUserServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

@Controller
@RequestMapping(value = "/api/v2/enterprise")
public class EnterpriseApiController extends AbstractCommonController{
	@Autowired
	private UserTokenCacheService userTokenCacheService;

	@Autowired
	private EnterpriseUserDeptService userDeptService;
	@Autowired
	private EnterpriseAccountManager enterpriseAccountManager;
	@Autowired
	WxOauth2Service wxOauth2Service;

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
	// 获取邀请token
	@RequestMapping(value = "/invitToken", method = RequestMethod.GET)
	@ResponseBody
	public  ResponseEntity<?>  invitUser(@RequestHeader("Authorization") String authorization, HttpServletRequest req) throws BaseRunException, IOException {
		UserToken userToken = userTokenHelper.checkTokenAndGetUser(authorization);
		UserToken invaterToken = new UserToken();
		invaterToken.setCloudUserId(0L);
		invaterToken.setAppId(userToken.getAppId());
		invaterToken.setEnterpriseId(userToken.getEnterpriseId());
		invaterToken.setAccountId(userToken.getAccountId());
		int tokenExpireTime=1000*60*60*24;
		userTokenHelper.createInvitToken(invaterToken,tokenExpireTime,req);
		return new ResponseEntity<String>(invaterToken.getToken(),HttpStatus.OK);
	}
	

	// 获取邀请token
	@RequestMapping(value = "/invitIndex", method = RequestMethod.GET)
	public  String  invitIndex( String token, HttpServletRequest req,Model model) throws BaseRunException, IOException {
		UserToken userToken = userTokenCacheService.getUserToken(token);
		if(userToken==null){
			model.addAttribute("token","isInvalid");
		}else{
			Enterprise enterprise = enterpriseService.getById(userToken.getEnterpriseId());
			model.addAttribute("enterpriseId", userToken.getEnterpriseId());
			model.addAttribute("accountId",userToken.getAccountId());
			model.addAttribute("appId",userToken.getAppId());
			model.addAttribute("enterpriseName",enterprise.getName());
			model.addAttribute("token",token);
		}
	
		return "enterprise/admin/user/invitIndex";
	}
	
	
	
	// 企业自注册
	@RequestMapping(value = "/registerUser", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> registerUser(@RequestHeader("Authorization") String authorization,@RequestBody RestUserRegister registerRequest, HttpServletRequest request) throws BaseRunException, IOException {
		
		UserToken userToken = userTokenCacheService.getUserToken(authorization);
		if(userToken==null){
	            String errorMessage = "[tokenLog] userToken or terminal or refreshToken is null userToken";
	            throw new AuthFailedException(errorMessage);
		}
		WxUserInfo userInfo = wxOauth2Service.getWxUserInfo(registerRequest.getCode());
		if (!checkCode(registerRequest.getPhone(), registerRequest.getCheckCode())) {
			
			return new ResponseEntity<CheckCodeFailedException>(new CheckCodeFailedException(),HttpStatus.OK);
		}
		
		Enterprise enterprise = enterpriseService.getById(userToken.getEnterpriseId());
		WxUser wxUser = wxUserServiceImpl.getByUnionId(userInfo.getUnionid());
		if (wxUser == null) {
			loginManagerImpl.openAccount(userInfo, null);
			wxUser = wxUserServiceImpl.getByUnionId(userInfo.getUnionid());
		}
		WxUserEnterprise old = wxUserEnterpriseService.getByUnionIdAndEnterpriseId(userInfo.getUnionid(), registerRequest.getEnterpriseId());
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

		enterpriseBySelfService.createEnterpriseUser(request, enterprise, enterpriseUser, authServer, false, userInfo.getUnionid());
		WxUserEnterprise wxUserEnterprise = new WxUserEnterprise();
		wxUserEnterprise.setEnterpriseId(enterprise.getId());
		wxUserEnterprise.setEnterpriseUserId(enterpriseUser.getId());
		wxUserEnterprise.setUnionId(userInfo.getUnionid());
		wxUserEnterprise.setModifiedAt(new Date());
		if (wxUserEnterpriseService.getByUnionIdAndEnterpriseId(userInfo.getUnionid(), enterprise.getId()) == null) {
			wxUserEnterpriseService.create(wxUserEnterprise);
		} else {
			wxUserEnterpriseService.update(wxUserEnterprise);
		}
		EnterpriseUserDept enterpriseUserDept = new EnterpriseUserDept(enterpriseUser.getId(), registerRequest.getEnterpriseId(), registerRequest.getDeptId());
		userDeptService.create(enterpriseUserDept);

		if (StringUtils.isNotBlank(userInfo.getHeadimgurl())) {
			long accountId = enterpriseAccountManager.getByEnterpriseId(registerRequest.getEnterpriseId()).get(0).getAccountId();
			userImageService.updateUserImage(enterpriseUser.getId(), accountId, userInfo.getHeadimgurl());
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
