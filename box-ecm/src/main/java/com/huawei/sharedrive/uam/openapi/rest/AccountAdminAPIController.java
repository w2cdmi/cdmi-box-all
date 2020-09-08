package com.huawei.sharedrive.uam.openapi.rest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huawei.sharedrive.uam.authserver.manager.AuthServerManager;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseManager;
import com.huawei.sharedrive.uam.enterprise.service.EnterpriseAccountService;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.manager.EnterpriseUserManager;
import com.huawei.sharedrive.uam.exception.BadRequestException;
import com.huawei.sharedrive.uam.exception.BaseRunException;
import com.huawei.sharedrive.uam.exception.BusinessException;
import com.huawei.sharedrive.uam.exception.InternalServerErrorException;
import com.huawei.sharedrive.uam.log.domain.UserLogType;
import com.huawei.sharedrive.uam.log.service.UserLogService;
import com.huawei.sharedrive.uam.logininfo.domain.LoginInfo;
import com.huawei.sharedrive.uam.logininfo.manager.LoginInfoManager;
import com.huawei.sharedrive.uam.openapi.domain.user.ForgetPwdRequest;
import com.huawei.sharedrive.uam.openapi.domain.user.ForgetPwdResponse;
import com.huawei.sharedrive.uam.system.domain.MailServer;
import com.huawei.sharedrive.uam.system.service.CustomizeLogoService;
import com.huawei.sharedrive.uam.system.service.MailServerService;
import com.huawei.sharedrive.uam.util.Constants;
import com.huawei.sharedrive.uam.util.FormValidateUtil;
import com.huawei.sharedrive.uam.util.PasswordValidateUtil;
import com.huawei.sharedrive.uam.util.PropertiesUtils;
import com.huawei.sharedrive.uam.util.RandomKeyGUID;

import pw.cdmi.box.domain.Page;
import pw.cdmi.common.domain.AuthServer;
import pw.cdmi.common.domain.CustomizeLogo;
import pw.cdmi.common.domain.enterprise.Enterprise;
import pw.cdmi.common.log.UserLog;
import pw.cdmi.core.encrypt.HashPassword;
import pw.cdmi.core.utils.DigestUtil;
import pw.cdmi.core.utils.EDToolsEnhance;
import pw.cdmi.core.utils.HashPasswordUtil;

@SuppressWarnings("PMD.PreserveStackTrace")
@Controller
@RequestMapping(value = "/api/v2/account")
public class AccountAdminAPIController
{
    private static Logger logger = LoggerFactory.getLogger(AccountAdminAPIController.class);
    
    private static final int RESET_LINK_EXPRISE_TIME = Integer.parseInt(PropertiesUtils.getProperty("session.expire",
        "600000"));
    
    private static final boolean IS_SINGLE_ENTERPRISE = Boolean.parseBoolean(PropertiesUtils.getProperty("is.single.enterprise",
        "true"));
    
    private static Long enterpriseId = null;
    
    @Autowired
    private UserLogService userLogService;
    
    @Autowired
    private MailServerService mailServerService;
    
    @Autowired
    private AuthServerManager authServerManager;
    
    @Autowired
    private CustomizeLogoService customizeLogoService;
    
    @Autowired
    private LoginInfoManager loginInfoManager;
    
    @Autowired
    private EnterpriseUserManager enterpriseUserManager;
    
    @Autowired
    private EnterpriseManager enterpriseManager;
    
    @Autowired
    private EnterpriseAccountService enterpriseAccountService;
    
    /**
     * 
     * @param request
     * @return
     * @throws BaseRunException
     * @throws IOException
     */
    @RequestMapping(value = "/forgetpwd", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ForgetPwdResponse> resetLink(@RequestBody ForgetPwdRequest request)
        throws BaseRunException, IOException
    {
        logger.info("forgetpwd resetLink");
        String loginName = request.getLoginName();
        String email = request.getEmail();
        String appId = request.getAppId();
        if (StringUtils.isBlank(loginName) || StringUtils.isBlank(appId)
            || !FormValidateUtil.isValidEmail(email))
        {
            throw new BadRequestException();
        }
        String[] split = loginName.split("/");
        EnterpriseUser localUser=null;
        if (split.length>1) {
        	localUser = enterpriseUserManager.getEUserByLoginName(split[1], split[0]);
		}else{
			localUser = enterpriseUserManager.getEUserByLoginName(loginName, null);
		}
        if (null == localUser)
        {
            throw new BadRequestException();
        }
        
        if (!email.equals(localUser.getEmail()))
        {
            throw new BadRequestException();
        }
        MailServer mailServer = mailServerService.getMailServerByAppId(appId);
        if (mailServer == null)
        {
            throw new BadRequestException();
        }
        AuthServer authServer = authServerManager.enterpriseTypeCheck(localUser.getEnterpriseId(),
            Constants.AUTH_SERVER_TYPE_LOCAL);
        if (authServer == null || !authServer.getType().equals(Constants.AUTH_SERVER_TYPE_LOCAL))
        {
            logger.warn("Don't config authServer");
            throw new BusinessException();
        }
        CustomizeLogo customizeLogo = customizeLogoService.getCustomize();
        if (customizeLogo == null || StringUtils.isBlank(customizeLogo.getDomainName()))
        {
            logger.warn("Don't config domain name");
            throw new BusinessException();
        }
        String validateKey = RandomKeyGUID.getSecureRandomGUID();
        Map<String, String> encodeMap = EDToolsEnhance.encode(validateKey);
        localUser.setModifiedAt(new Date());
        localUser.setValidateKey(DigestUtil.digestPassword(validateKey));
        localUser.setResetPasswordAt(new Date());
        localUser.setValidateKeyEncodeKey(encodeMap.get(EDToolsEnhance.ENCRYPT_KEY));
        logger.info("set crypt in uam.AccountAdminAPI");
        enterpriseUserManager.updatePassword(localUser);
        
        StringBuilder linkUrl = new StringBuilder();
        linkUrl.append(customizeLogo.getDomainName());
        if (linkUrl.charAt(linkUrl.length() - 1) != '/')
        {
            linkUrl.append('/');
        }
        linkUrl.append("syscommon/reset?name=");
        linkUrl.append(loginName);
        linkUrl.append("&validateKey=");
        linkUrl.append(encodeMap.get(EDToolsEnhance.ENCRYPT_CONTENT));
        
        Map<String, Object> messageModel = new HashMap<String, Object>(2);
        messageModel.put("username", localUser.getName());
        messageModel.put("link", linkUrl.toString());
        
        String msg = mailServerService.getEmailMsgByTemplate(Constants.RESET_APP_PWD_MAIL_CONTENT,
            messageModel);
        String subject = mailServerService.getEmailMsgByTemplate(Constants.RESET_APP_PWD_MAIL_SUBJECT,
            new HashMap<String, Object>(1));
        mailServerService.sendHtmlMail(localUser.getName(),
            mailServer.getId(),
            localUser.getEmail(),
            null,
            null,
            subject,
            msg);
        
        return new ResponseEntity<ForgetPwdResponse>(HttpStatus.OK);
    }
    
    /**
     * 
     * @param request
     * @return
     * @throws BaseRunException
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/validateurl", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ForgetPwdResponse> validateLinkUrl(@RequestBody ForgetPwdRequest request)
        throws BaseRunException, NoSuchAlgorithmException, UnsupportedEncodingException
    {
        logger.info("forgetpwd validateLinkUrl");
        String name = request.getName();
        String validateKey = request.getValidateKey();
        if (StringUtils.isBlank(name) || StringUtils.isBlank(validateKey))
        {
            throw new BadRequestException();
        }
        String loginName = name;
        EnterpriseUser localUser = enterpriseUserManager.getEUserByLoginName(getLoginName(loginName), null);
        if (localUser == null || localUser.getValidateKey() == null)
        {
            throw new BadRequestException();
        }
        String realValidateKey = EDToolsEnhance.decode(validateKey, localUser.getValidateKeyEncodeKey());
        if (!localUser.getValidateKey().equals(DigestUtil.digestPassword(realValidateKey)))
        {
            throw new BadRequestException();
        }
        Date modifiedDate = localUser.getResetPasswordAt();
        long modifiedDateSeconds = 0;
        if (null != modifiedDate)
        {
            modifiedDateSeconds = modifiedDate.getTime();
        }
        long lockDateSeconds = new Date().getTime() - modifiedDateSeconds;
        if (lockDateSeconds > RESET_LINK_EXPRISE_TIME)
        {
            throw new BadRequestException();
        }
        
        AuthServer authServer = authServerManager.enterpriseTypeCheck(localUser.getEnterpriseId(),
            Constants.AUTH_SERVER_TYPE_LOCAL);
        if (authServer == null || !authServer.getType().equals(Constants.AUTH_SERVER_TYPE_LOCAL))
        {
            logger.warn("Don't config authServer");
            throw new BusinessException();
        }
        return new ResponseEntity<ForgetPwdResponse>(HttpStatus.OK);
    }
    
    /**
     * 
     * @param request
     * @return
     * @throws BaseRunException
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/resetpwd", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ForgetPwdResponse> resetPassword(@RequestBody ForgetPwdRequest request,
        HttpServletRequest req) throws BaseRunException, NoSuchAlgorithmException,
        UnsupportedEncodingException
    {
        String name = request.getName();
        String password = request.getPassword();
        logger.info("forgetpwd resetPassword:" + name);
        UserLog userLog = UserLogType.getUserLog(req, request.getAppId(), request.getLoginName(), false);
        try
        {
            if (StringUtils.isBlank(name) || StringUtils.isBlank(password)
                || !PasswordValidateUtil.isValidPassword(password, 127))
            {
                throw new BadRequestException();
            }
            
            String loginName = request.getLoginName();
            if (loginName == null || !loginName.equals(name))
            {
                throw new BadRequestException();
            }
            
            EnterpriseUser localUser = enterpriseUserManager.getEUserByLoginName(getLoginName(loginName),
                null);
            if (localUser == null || localUser.getValidateKey() == null)
            {
                throw new BadRequestException();
            }
            String realValidateKey = EDToolsEnhance.decode(request.getValidateKey(),
                localUser.getValidateKeyEncodeKey());
            if (!localUser.getValidateKey().equals(DigestUtil.digestPassword(realValidateKey)))
            {
                throw new BadRequestException();
            }
            AuthServer authServer = authServerManager.enterpriseTypeCheck(localUser.getEnterpriseId(),
                Constants.AUTH_SERVER_TYPE_LOCAL);
            if (authServer == null || !authServer.getType().equals(Constants.AUTH_SERVER_TYPE_LOCAL))
            {
                logger.warn("Don't config authServer");
                throw new BusinessException();
            }
            
            try
            {
                HashPassword hashPassword = HashPasswordUtil.generateHashPassword(password);
                localUser.setModifiedAt(new Date());
                localUser.setPassword(hashPassword.getHashPassword());
                localUser.setIterations(hashPassword.getIterations());
                localUser.setSalt(hashPassword.getSalt());
                localUser.setValidateKey(null);
                localUser.setValidateKeyEncodeKey(null);
            }
            catch (RuntimeException e)
            {
                logger.error("digest newPsw exception");
                throw new BusinessException();
            }
            catch (Exception e)
            {
                logger.error("digest newPsw exception");
                throw new BusinessException();
            }
            
            enterpriseUserManager.updatePassword(localUser);
        }
        catch (RuntimeException e)
        {
            userLogService.saveFailLog(userLog, UserLogType.KEY_RESET_PWD_ERR, null);
            throw e;
        }
        userLogService.saveUserLog(userLog, UserLogType.KEY_RESET_PWD, null);
        return new ResponseEntity<ForgetPwdResponse>(HttpStatus.OK);
    }
    
    private String getLoginName(String loginName)
    {
        LoginInfo loginInfo;
        Enterprise enterprise;
        if (IS_SINGLE_ENTERPRISE)
        {
            if (enterpriseId != null)
            {
                enterprise = enterpriseManager.getById(enterpriseId);
                if (enterprise == null)
                {
                    logger.error("enterprise is null, id:{}", enterpriseId);
                    throw new InternalServerErrorException();
                }
            }
            else
            {
                Page<Enterprise> enterprisePage = enterpriseManager.getFilterd(null, null, null);
                List<Enterprise> enterpriseList = enterprisePage.getContent();
                if (CollectionUtils.isEmpty(enterpriseList))
                {
                    logger.error("enterpriseList is null");
                    throw new InternalServerErrorException();
                }
                enterprise = enterpriseList.get(0);
                enterpriseId = enterprise.getId();
            }
            
            loginInfo = loginInfoManager.getByDomain(loginName, enterprise.getDomainName());
            if (null == loginInfo)
            {
                logger.error("invalid loginName:" + loginName);
                throw new BadRequestException();
            }
            return loginInfo.getLoginName();
        }
        else
        {
            return loginName;
        }
    }
    /**
     * 
     * @param long
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getEnterpriseAccountPwdLevel/{enterpriseId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> getEnterpriseAccountPwdLevel(@PathVariable(value="enterpriseId") long enterpriseId)
        throws Exception
    {
    	//根据企业ID查找密码复杂度
		String pwd_Level= enterpriseAccountService.getPwdLevelByEnterpriseId(enterpriseId);
        int pwdLevel = 1;
        if(!StringUtils.isBlank(pwd_Level)){
        	pwdLevel = Integer.parseInt(pwd_Level);	
        }
        String pwd_level = pwdLevel+"";
        return new ResponseEntity<String>(pwd_level,HttpStatus.OK);
    }
    
}
