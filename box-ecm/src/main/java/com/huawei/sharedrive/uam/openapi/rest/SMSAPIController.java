package com.huawei.sharedrive.uam.openapi.rest;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huawei.sharedrive.uam.authserver.manager.AuthServerManager;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.manager.EnterpriseUserManager;
import com.huawei.sharedrive.uam.exception.BadRequestException;
import com.huawei.sharedrive.uam.exception.BusinessException;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.openapi.domain.user.ForgetPwdRequest;
import com.huawei.sharedrive.uam.openapi.domain.user.ForgetPwdResponse;
import com.huawei.sharedrive.uam.util.Constants;
import com.huawei.sharedrive.uam.util.OneTimePasswordAlgorithm;

import pw.cdmi.common.cache.CacheClient;
import pw.cdmi.common.domain.AuthServer;
import pw.cdmi.core.encrypt.HashPassword;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.HashPasswordUtil;

@Controller
@RequestMapping(value = "/api/v2/sms")
public class SMSAPIController {
	private static Logger logger = LoggerFactory.getLogger(SMSAPIController.class);

	@Autowired
	private CacheClient cacheClient;

	@Autowired
	private EnterpriseUserManager enterpriseUserManager;

	@Autowired
	private AuthServerManager authServerManager;

	@Autowired
	private RestClient bmsClientService;
	
	@RequestMapping(value = "sendIdentifyCode", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> sendIdentifyCode(@RequestBody ForgetPwdRequest request) {
		if (request != null && request.getContactPhone().length() != 11) {
			throw new InvalidParamterException();
		}
		// 生成验证码
		String generateOTP = OneTimePasswordAlgorithm.generateOTP();
		boolean setCache = cacheClient.setCache(request.getContactPhone(), generateOTP, 125 *1000);
		HashMap<Object,Object> map = new HashMap<>();
		map.put("identifyCode", generateOTP);
		map.put("contactPhone", request.getContactPhone());
		// 发送短信
		TextResponse response = bmsClientService.performJsonPostTextResponse("/api/v2/sms/sendIdentifyCode",null,map);
		if (setCache&&response.getStatusCode()==200) {
			return new ResponseEntity<>(HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}

	@RequestMapping(value = "resetPassword", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<ForgetPwdResponse> resetPassword(@RequestBody ForgetPwdRequest request)
			throws UnsupportedEncodingException {

		String contactPhone = request.getContactPhone();
		String identifyCode = request.getIdentifyCode();
		String password = request.getNewPassword();
		String appId = request.getAppId();
		String cache = (String) cacheClient.getCache(contactPhone);
		if (StringUtils.isBlank(contactPhone) || StringUtils.isBlank(appId) || StringUtils.isBlank(identifyCode)) {
			throw new BadRequestException();
		}
		if (!identifyCode.equals(cache)) {
			throw new BadRequestException();
		}
		cacheClient.deleteCache(contactPhone);
		EnterpriseUser localUser = enterpriseUserManager.getEUserByLoginName(contactPhone, null);
		if (null == localUser) {
			throw new BadRequestException();
		}
		AuthServer authServer = authServerManager.enterpriseTypeCheck(localUser.getEnterpriseId(),
				Constants.AUTH_SERVER_TYPE_LOCAL);

		if (authServer == null || !authServer.getType().equals(Constants.AUTH_SERVER_TYPE_LOCAL)) {
			logger.warn("Don't config authServer");
			throw new BusinessException();
		}
		try {
			HashPassword hashPassword = HashPasswordUtil.generateHashPassword(password);
			localUser.setModifiedAt(new Date());
			localUser.setPassword(hashPassword.getHashPassword());
			localUser.setIterations(hashPassword.getIterations());
			localUser.setSalt(hashPassword.getSalt());
			localUser.setValidateKey(null);
			localUser.setValidateKeyEncodeKey(null);
			localUser.setResetPasswordAt(null);
		} catch (RuntimeException e) {
			logger.error("digest newPsw exception");
			throw new BusinessException();
		} catch (Exception e) {
			logger.error("digest newPsw exception");
			throw new BusinessException();
		}
		enterpriseUserManager.updatePassword(localUser);
		return new ResponseEntity<ForgetPwdResponse>(HttpStatus.OK);

}

}
