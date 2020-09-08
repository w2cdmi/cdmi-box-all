package com.huawei.sharedrive.uam.openapi.rest.account;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import pw.cdmi.common.domain.AccountConfig;
import com.huawei.sharedrive.uam.enterprise.domain.AccountConfigAttribute;
import com.huawei.sharedrive.uam.enterprise.domain.ExtendUserLog;
import com.huawei.sharedrive.uam.enterprise.domain.WebIconPcLogo;
import com.huawei.sharedrive.uam.enterprise.manager.AccountConfigManager;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseIndividualConfigManager;
import com.huawei.sharedrive.uam.exception.ErrorCode;
import com.huawei.sharedrive.uam.exception.InternalServerErrorException;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.exception.NoSuchItemException;
import com.huawei.sharedrive.uam.exception.NoSuchResourceStrategyException;
import com.huawei.sharedrive.uam.log.domain.UserLogType;
import com.huawei.sharedrive.uam.log.service.UserLogService;
import com.huawei.sharedrive.uam.oauth2.domain.UserToken;
import com.huawei.sharedrive.uam.oauth2.manager.AuthManager;
import com.huawei.sharedrive.uam.oauth2.manager.impl.AuthManagerImpl;
import com.huawei.sharedrive.uam.oauth2.service.impl.UserTokenHelper;
import com.huawei.sharedrive.uam.openapi.domain.account.AccountAttribute;
import com.huawei.sharedrive.uam.openapi.domain.account.AccountTitle;
import com.huawei.sharedrive.uam.openapi.domain.account.RestAccountConfig;
import com.huawei.sharedrive.uam.openapi.domain.account.RestAccountConfigList;
import com.huawei.sharedrive.uam.security.domain.SecretStaff;
import com.huawei.sharedrive.uam.security.service.SecretStaffService;
import com.huawei.sharedrive.uam.system.service.CustomizeLogoService;

import pw.cdmi.common.domain.CustomizeLogo;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.common.log.UserLog;

@Controller
@RequestMapping(value = "/api/v2/accounts")
public class AccountAttributeAPIController
{
    @Autowired
    private AccountConfigManager accountConfigManager;
    
    @Autowired
    private CustomizeLogoService customizeService;
    
    @Autowired
    private EnterpriseIndividualConfigManager enterpriseIndividualConfigManager;
    
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    @Autowired
    private AuthManager authManager;
    
    @Autowired
    private SecretStaffService  secretStaffService;
    
    @Autowired
    private UserLogService userLogService;
    
    @RequestMapping(value = "/attributes", method = RequestMethod.GET)
    public ResponseEntity<RestAccountConfigList> getAttribute(@RequestParam(required = true) String name,
        @RequestHeader("Authorization") String authorization,
        @RequestHeader(value = "Date", required = false) String date, HttpServletRequest request)
    {
        RestAccountConfigList configList = null;
        ExtendUserLog userLog = null;
        try
        {
            userLog = tokenOrAccountAuth(authorization, date);
            configList = getAccountConfig(userLog, name);
            userLogService.saveUserLog(userLog.getUserLog(),
                UserLogType.KEY_GET_ACCOUNT_ATTR,
                new String[]{name});
        }
        catch (RuntimeException e)
        {
            if (null != userLog)
            {
                userLogService.saveFailLog(userLog.getUserLog().getLoginName(), userLog.getUserLog()
                    .getAppId(), new String[]{name}, UserLogType.KEY_GET_ACCOUNT_ATTR_ERR);
            }
            throw e;
        }
        return new ResponseEntity<RestAccountConfigList>(configList, HttpStatus.OK);
    }
    
    
    @RequestMapping(value = "/{accountId}/config", method = RequestMethod.GET)
    public ResponseEntity<RestAccountConfigList> accountConfig(@PathVariable long accountId,
        @RequestHeader("Authorization") String authorization,
        @RequestHeader(value = "Date", required = false) String date, HttpServletRequest request)
    {
        RestAccountConfigList configList = null;
        ExtendUserLog userLog = null;
        try
        {
            userLog = tokenOrAccountAuth(authorization, date);
//            AccountConfig ccountConfig= accountConfigManager.get(accountId, request.getParameter("name"));
            List<AccountConfig> accountlist=accountConfigManager.getAll(accountId);
            configList = bindAccountConfig(accountlist);
            userLogService.saveUserLog(userLog.getUserLog(),
                UserLogType.KEY_GET_ACCOUNT_ATTR,
                new String[]{""});
        }
        catch (RuntimeException e)
        {
            if (null != userLog)
            {
                userLogService.saveFailLog(userLog.getUserLog().getLoginName(), userLog.getUserLog()
                    .getAppId(), new String[]{""}, UserLogType.KEY_GET_ACCOUNT_ATTR_ERR);
            }
            throw e;
        }
        return new ResponseEntity<RestAccountConfigList>(configList, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/{accountId}/staffSecret", method = RequestMethod.GET)
    public ResponseEntity<List<SecretStaff>> getStaffSecret(@PathVariable long accountId,
        @RequestHeader("Authorization") String authorization,
        @RequestHeader(value = "Date", required = false) String date, HttpServletRequest request)
    {
    	List<SecretStaff> secretStafflist = null;
        ExtendUserLog userLog = null;
        try
        {
            userLog = tokenOrAccountAuth(authorization, date);
            secretStafflist =secretStaffService.getByAccountId(accountId);

            return new ResponseEntity<List<SecretStaff>>(secretStafflist, HttpStatus.OK);
        }
        catch (RuntimeException e)
        {
            if (null != userLog)
            {
                userLogService.saveFailLog(userLog.getUserLog().getLoginName(), userLog.getUserLog()
                    .getAppId(), new String[]{""}, UserLogType.KEY_GET_ACCOUNT_ATTR_ERR);
            }
            throw e;
        }
    }
    
    
    @RequestMapping(value = "/logo", method = RequestMethod.GET)
    public ResponseEntity<String> getLogo(@RequestHeader("Authorization") String authorization,
        @RequestHeader(value = "Date", required = false) String date, HttpServletRequest req,
        HttpServletResponse resp)
    {
        UserToken userToken = userTokenHelper.checkTokenAndGetUser(authorization);
        String type = req.getParameter("type");
        WebIconPcLogo webIconPcLogo = new WebIconPcLogo();
        webIconPcLogo.setAccountId(userToken.getAccountId());
        WebIconPcLogo clientLogo = enterpriseIndividualConfigManager.getWebIconPcLogo(webIconPcLogo);
        ExtendUserLog userLog = null;
        userLog = tokenOrAccountAuth(authorization, date);
        if (!"pc".equals(type))
        {
            userLogService.saveFailLog(userLog.getUserLog().getLoginName(),
                userLog.getUserLog().getAppId(),
                new String[]{type},
                UserLogType.KEY_GET_ACCOUNT_LOGO_ERR);
            throw new InvalidParamterException(ErrorCode.INVALID_PARAMTER.getMessage());
        }
        if (clientLogo == null)
        {
            userLogService.saveFailLog(userLog.getUserLog().getLoginName(),
                userLog.getUserLog().getAppId(),
                new String[]{type},
                UserLogType.KEY_GET_ACCOUNT_LOGO_ERR);
            throw new NoSuchResourceStrategyException(ErrorCode.NO_SUCH_RESOURCE_STRATEGY.getMessage());
        }
        try
        {
            if ("pc".equalsIgnoreCase(type))
            {
                if ("png".equalsIgnoreCase(clientLogo.getPcLogoFormatName()))
                {
                    outputImage(resp, clientLogo.getPcLogo(), MediaType.IMAGE_PNG.toString());
                }
                else
                {
                    outputImage(resp, clientLogo.getPcLogo(), MediaType.IMAGE_JPEG.toString());
                }
                userLogService.saveUserLog(userLog.getUserLog(),
                    UserLogType.KEY_GET_ACCOUNT_LOGO,
                    new String[]{type});
            }
        }
        catch (RuntimeException e)
        {
            userLogService.saveFailLog(userLog.getUserLog().getLoginName(),
                userLog.getUserLog().getAppId(),
                new String[]{type},
                UserLogType.KEY_GET_ACCOUNT_LOGO_ERR);
            throw new InternalServerErrorException(ErrorCode.INTERNAL_SERVER_ERROR.getMessage(), e);
        }
        catch (Exception e)
        {
            userLogService.saveFailLog(userLog.getUserLog().getLoginName(),
                userLog.getUserLog().getAppId(),
                new String[]{type},
                UserLogType.KEY_GET_ACCOUNT_LOGO_ERR);
            throw new InternalServerErrorException(ErrorCode.INTERNAL_SERVER_ERROR.getMessage(), e);
        }
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @RequestMapping(value = "/attribute", method = RequestMethod.GET)
    public ResponseEntity<AccountTitle> getTitle(@RequestHeader("Authorization") String authorization,
        @RequestHeader(value = "Date", required = false) String date, HttpServletRequest req)
    {
        UserToken userToken = userTokenHelper.checkTokenAndGetUser(authorization);
        AccountTitle accountTitle = new AccountTitle();
        String name = req.getParameter("name");
        ExtendUserLog userLog;
        userLog = tokenOrAccountAuth(authorization, date);
        if (StringUtils.isEmpty(name))
        {
            userLogService.saveFailLog(userLog.getUserLog().getLoginName(),
                userLog.getUserLog().getAppId(),
                new String[]{name},
                UserLogType.KEY_GET_ACCOUNT_ATTR_ERR);
            throw new InvalidParamterException(ErrorCode.INVALID_PARAMTER.getMessage());
        }
        if (!("zhName".equals(name) || "enName".equals(name)))
        {
            userLogService.saveFailLog(userLog.getUserLog().getLoginName(),
                userLog.getUserLog().getAppId(),
                new String[]{name},
                UserLogType.KEY_GET_ACCOUNT_ATTR_ERR);
            throw new InvalidParamterException(ErrorCode.INVALID_PARAMTER.getMessage());
        }
        try
        {
            CustomizeLogo customizeLogo = customizeService.getCustomize();
            WebIconPcLogo webIconPcLogo = new WebIconPcLogo();
            webIconPcLogo.setAccountId(userToken.getAccountId());
            enterpriseIndividualConfigManager.getAccountTitle(customizeLogo, webIconPcLogo);
            if ("zhName".equals(name) && customizeLogo.getTitle() != null)
            {
                accountTitle.setTitle(customizeLogo.getTitle());
            }
            if ("enName".equals(name) && customizeLogo.getTitleEn() != null)
            {
                accountTitle.setTitle(customizeLogo.getTitleEn());
            }
            userLogService.saveUserLog(userLog.getUserLog(),
                UserLogType.KEY_GET_ACCOUNT_ATTR,
                new String[]{name});
        }
        catch (RuntimeException e)
        {
            userLogService.saveFailLog(userLog.getUserLog().getLoginName(),
                userLog.getUserLog().getAppId(),
                new String[]{name},
                UserLogType.KEY_GET_ACCOUNT_ATTR_ERR);
            throw new InternalServerErrorException(ErrorCode.INTERNAL_SERVER_ERROR.getMessage(), e);
        }
        catch (Exception e)
        {
            userLogService.saveFailLog(userLog.getUserLog().getLoginName(),
                userLog.getUserLog().getAppId(),
                new String[]{name},
                UserLogType.KEY_GET_ACCOUNT_ATTR_ERR);
            throw new InternalServerErrorException(ErrorCode.INTERNAL_SERVER_ERROR.getMessage(), e);
        }
        return new ResponseEntity<AccountTitle>(accountTitle, HttpStatus.OK);
    }
    
    private ExtendUserLog tokenOrAccountAuth(String authorization, String date)
    {
        ExtendUserLog userLog = new ExtendUserLog();
        if (!authorization.startsWith(AuthManagerImpl.APP_PREFIX)
            && !authorization.startsWith(AuthManagerImpl.ACCOUNT_PREFIX))
        {
            UserToken userToken = userTokenHelper.checkTokenAndGetUser(authorization);
            userLog.setUserLog(UserLogType.getUserLog(userToken));
            userLog.setAccountId(userToken.getAccountId());
        }
        else
        {
            EnterpriseAccount enterpriseAccount = authManager.checkAppToken(authorization, date);
            UserLog tempUserLog = new UserLog();
            tempUserLog.setAppId(enterpriseAccount.getAuthAppId());
            tempUserLog.setLoginName(enterpriseAccount.getAccessKeyId());
            userLog.setUserLog(tempUserLog);
            userLog.setAccountId(enterpriseAccount.getAccountId());
        }
        return userLog;
    }
    
    
    private RestAccountConfigList bindAccountConfig(List<AccountConfig> list)
    {
        List<RestAccountConfig> configs = new ArrayList<RestAccountConfig>(10);
        
        for(int i=0;i<list.size();i++){
        	 RestAccountConfig remultiversion  = new RestAccountConfig(list.get(i).getName(), list.get(i).getValue());
			 configs.add(remultiversion);
        }
        return new RestAccountConfigList(configs);
    }
    
    
    private RestAccountConfigList getAccountConfig(ExtendUserLog userLog, String name)
    {
        List<RestAccountConfig> configs = new ArrayList<RestAccountConfig>(10);
        RestAccountConfig config;
        if (StringUtils.isNotBlank(name))
        {
            AccountAttribute attribute = AccountAttribute.getAccountAttribute(name);
            if (attribute == null)
            {
                throw new InvalidParamterException("Invalid attribute " + name);
            }
            AccountConfig accountConfig = accountConfigManager.get(userLog.getAccountId(), name);
            if (null == accountConfig)
            {
                throw new NoSuchItemException("AccountId of " + userLog.getAccountId() + " attrubute " + name
                    + " does not exist.");
            }
            config = new RestAccountConfig(accountConfig.getName(), accountConfig.getValue());
            configs.add(config);
        }
        else
        {
            List<AccountConfig> list = accountConfigManager.list(userLog.getAccountId());
            if (null == list || list.isEmpty())
            {
                throw new NoSuchItemException("AccountId of " + userLog.getAccountId()
                    + " attrubutes does not exist.");
            }
            for (AccountConfig accountConfig : list)
            {
                config = new RestAccountConfig(accountConfig.getName(), accountConfig.getValue());
                configs.add(config);
            }
        }
        return new RestAccountConfigList(configs);
    }
    
    private void outputImage(HttpServletResponse resp, byte[] data, String contentType) throws IOException
    {
        OutputStream outputStream = null;
        try
        {
            if (data == null)
            {
                return;
            }
            resp.setContentType(contentType);
            outputStream = resp.getOutputStream();
            outputStream.write(data);
            outputStream.flush();
        }
        finally
        {
            IOUtils.closeQuietly(outputStream);
        }
    }
    
}
