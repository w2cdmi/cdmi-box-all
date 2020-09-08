package pw.cdmi.box.uam.enterprise.manager.impl;

import java.security.InvalidParameterException;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.box.uam.authapp.service.AuthAppService;
import pw.cdmi.box.uam.enterprise.domain.EnterpriseAccountConstants;
import pw.cdmi.box.uam.enterprise.domain.EnterpriseAccountCryptUtil;
import pw.cdmi.box.uam.enterprise.domain.EnterpriseAccountModifyRequestInfo;
import pw.cdmi.box.uam.enterprise.domain.EnterpriseAccountV1;
import pw.cdmi.box.uam.enterprise.manager.EnterpriseAccountManager;
import pw.cdmi.box.uam.enterprise.manager.EnterpriseManager;
import pw.cdmi.box.uam.enterprise.service.EnterpriseAccountService;
import pw.cdmi.box.uam.exception.BaseRunException;
import pw.cdmi.box.uam.exception.BusinessException;
import pw.cdmi.box.uam.exception.InternalServerErrorException;
import pw.cdmi.box.uam.httpclient.domain.RestEnterpriseAccountModifyRequest;
import pw.cdmi.box.uam.httpclient.domain.RestEnterpriseAccountRequest;
import pw.cdmi.box.uam.httpclient.domain.RestEnterpriseAccountResponse;
import pw.cdmi.box.uam.httpclient.rest.EnterpriseHttpClient;
import pw.cdmi.box.uam.log.domain.OperateDescription;
import pw.cdmi.box.uam.log.domain.OperateType;
import pw.cdmi.box.uam.log.manager.SystemLogManager;
import pw.cdmi.box.uam.system.service.SMSService;
import pw.cdmi.common.domain.enterprise.Enterprise;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.common.domain.enterprise.EnterpriseAccountVo;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.uam.domain.AuthApp;

@Component
public class EnterpriseAccountManagerImpl implements EnterpriseAccountManager
{
    private static final Logger LOGGER = LoggerFactory.getLogger(EnterpriseAccountManagerImpl.class);
    
    @Autowired
    private EnterpriseAccountService enterpriseAccountService;
    
    @Resource
    private RestClient ufmClientService;
    
    @Autowired
    private AuthAppService authAppService;
    
    @Autowired
    private EnterpriseManager enterpriseManager;
    
    @Autowired
    private SystemLogManager systemLogManager;
    
    @Autowired
    private SMSService smsService;
    
    @Override
    public void create(RestEnterpriseAccountRequest request)
    {
        
        AuthApp authApp = authAppService.getByAuthAppID(request.getAuthAppId());
        Enterprise enterprise = enterpriseManager.getById(request.getEnterpriseId());
        if (authApp == null || enterprise == null)
        {
            throw new InvalidParameterException();
        }
        EnterpriseHttpClient enterpriseHttpClient = new EnterpriseHttpClient(ufmClientService);
        RestEnterpriseAccountRequest accountRequest = new RestEnterpriseAccountRequest();
        accountRequest.setDomain(enterprise.getDomainName());
        accountRequest.setEnterpriseId(enterprise.getId());
        accountRequest.setMaxMember(request.getMaxMember());
        accountRequest.setMaxSpace(request.getMaxSpace());
        accountRequest.setMaxTeamspace(request.getMaxTeamspace());
        accountRequest.setFilePreviewable(request.getFilePreviewable());
        accountRequest.setStatus(Enterprise.STATUS_ENABLE_STR);
        
        RestEnterpriseAccountResponse response = enterpriseHttpClient.getAccountInfo(authApp, accountRequest);
        if (response == null)
        {
            throw new InvalidParameterException();
        }
        
        if (!"enable".equals(response.getStatus()))
        {
            throw new BusinessException("BMS:bind app to enterprise failed");
        }
        
        EnterpriseAccount enterpriseAccount = transAccount(request, response);
        
        if (Enterprise.STATUS_DISABLE_STR.equalsIgnoreCase(response.getStatus()))
        {
            enterpriseAccount.setStatus(Enterprise.STATUS_ENABLE);
        }
        
        enterpriseAccountService.create(enterpriseAccount);
        
    }
    
    @Override
    public List<String> getAppByEnterpriseId(long enterpriseId)
    {
        
        return enterpriseAccountService.getAppByEnterpriseId(enterpriseId);
    }
    
    @Override
    public EnterpriseAccount getByEnterpriseApp(long enterpriseId, String authAppId)
    {
        return enterpriseAccountService.getByEnterpriseApp(enterpriseId, authAppId);
    }
    
    @Override
    public EnterpriseAccount getByAccountId(long accountId)
    {
        return enterpriseAccountService.getByAccountId(accountId);
    }
    
    @Override
    public Page<EnterpriseAccountVo> getPagedEnterpriseAccount(PageRequest request, String filter,
        String authAppId, String status)
    {
        AuthApp authApp = authAppService.getByAuthAppID(authAppId);
        if (authApp == null)
        {
            throw new InvalidParameterException();
        }
        EnterpriseAccountVo accountVo = new EnterpriseAccountVo();
        accountVo.setName(filter);
        accountVo.setAuthAppId(authAppId);
        accountVo.setStatus((Byte) ("".equals(status) ? null : Byte.valueOf(status)));
        Page<EnterpriseAccountVo> page = enterpriseAccountService.getPageEnterpriseAccount(request, accountVo);
        
        EnterpriseHttpClient enterpriseHttpClient = new EnterpriseHttpClient(ufmClientService);
        List<EnterpriseAccountVo> accountVos = page.getContent();
        RestEnterpriseAccountResponse response;
        for (EnterpriseAccountVo enterpriseAccount : accountVos)
        {
            response = enterpriseHttpClient.getCurrentAccountInfo(authApp, enterpriseAccount);
            if (response == null)
            {
                throw new InvalidParameterException();
            }
            enterpriseAccount.setCurrentMaxMember(response.getCurrentMembers());
            enterpriseAccount.setCurrentMaxTeamspace(response.getCurrentTeamspaces());
        }
        
        return page;
    }
    
    private EnterpriseAccount transAccount(RestEnterpriseAccountRequest request,
        RestEnterpriseAccountResponse response)
    {
        EnterpriseAccount enterpriseAccount = new EnterpriseAccount();
        enterpriseAccount.setAccessKeyId(response.getAccessToken().getAccessKey());
        enterpriseAccount.setSecretKey(response.getAccessToken().getSecretKey());
        EnterpriseAccountCryptUtil.encode(enterpriseAccount);
        LOGGER.info("change crypt in bms.EnterpriseAccount");
        enterpriseAccount.setAccountId(response.getId());
        enterpriseAccount.setAuthAppId(request.getAuthAppId());
        enterpriseAccount.setEnterpriseId(request.getEnterpriseId());
        enterpriseAccount.setMaxSpace(request.getMaxSpace());
        enterpriseAccount.setMaxMember(request.getMaxMember());
        enterpriseAccount.setMaxFiles(-1);
        enterpriseAccount.setMaxTeamspace(request.getMaxTeamspace());
        enterpriseAccount.setFilePreviewable(request.getFilePreviewable());
        if (null == request.getFileScanable())
        {
            enterpriseAccount.setFileScanable(false);
        }
        else
        {
            enterpriseAccount.setFileScanable(request.getFileScanable());
        }
        transNum(enterpriseAccount);
        Date date = new Date();
        enterpriseAccount.setCreatedAt(date);
        enterpriseAccount.setModifiedAt(date);
        return enterpriseAccount;
    }
    
    @Override
    public void modifyAccountConfiguration(HttpServletRequest request, EnterpriseAccount enterpriseAccount,
        EnterpriseAccountModifyRequestInfo modifyInfo) throws BaseRunException
    {
        transModify(enterpriseAccount, modifyInfo);
        modifyAccount(enterpriseAccount);
        transNum(enterpriseAccount);
        String[] description = new String[]{String.valueOf(enterpriseAccount.getEnterpriseId()),
            enterpriseAccount.getAuthAppId(), String.valueOf(modifyInfo.getFilePreviewable()),
            String.valueOf(modifyInfo.getMaxMember()), String.valueOf(modifyInfo.getMaxTeamspace()),
            String.valueOf(modifyInfo.getMaxSpace())};
        try
        {
            enterpriseAccountService.modifyEnterpriseAccount(enterpriseAccount);
            Enterprise enterprise = enterpriseManager.getById(enterpriseAccount.getEnterpriseId());
          // 发送短信
//           smsService.sendSMS("a4a89f483a334227abc033ef509a27db",enterprise.getContactPhone(),null,null,null);
        }
        catch (RuntimeException e)
        {
            systemLogManager.saveFailLog(request,
                OperateType.ModifyAccountCofiguration,
                OperateDescription.ACCOUNT_MODIFY_CONFIGURATION_ERROR,
                null,
                description);
            throw e;
        }
        systemLogManager.saveSuccessLog(request,
            OperateType.ModifyAccountCofiguration,
            OperateDescription.ACCOUNT_MODIFY_CONFIGURATION,
            null,
            description);
    }
    
    @Override
    public void modifyAccountStatus(HttpServletRequest request, EnterpriseAccount enterpriseAccount,
        Byte status)
    {
        AuthApp authApp = authAppService.getByAuthAppID(enterpriseAccount.getAuthAppId());
        if (null == authApp)
        {
            throw new InvalidParameterException();
        }
        EnterpriseHttpClient enterpriseHttpClient = new EnterpriseHttpClient(ufmClientService);
        RestEnterpriseAccountResponse accountResponse = enterpriseHttpClient.getAccountById(authApp,
            enterpriseAccount.getAccountId());
        String[] description = new String[]{String.valueOf(enterpriseAccount.getEnterpriseId()),
            enterpriseAccount.getAuthAppId(), String.valueOf(status)};
        try
        {
            if (null == accountResponse)
            {
                LOGGER.error("accountResponse is null ");
                throw new InternalServerErrorException();
            }
            RestEnterpriseAccountModifyRequest accountRequest = new RestEnterpriseAccountModifyRequest();
            accountRequest.setMaxMember(accountResponse.getMaxMember());
            accountRequest.setMaxSpace(Long.valueOf(accountResponse.getMaxSpace()));
            accountRequest.setMaxTeamspace(accountResponse.getMaxTeamspace());
            accountRequest.setFilePreviewable(accountResponse.isFilePreviewable());
            if (Enterprise.STATUS_ENABLE == status)
            {
                accountRequest.setStatus(Enterprise.STATUS_DISABLE_STR);
            }
            if (Enterprise.STATUS_DISABLE == status)
            {
                accountRequest.setStatus(Enterprise.STATUS_ENABLE_STR);
            }
            if (null == enterpriseHttpClient.modifyAccount(authApp,
                accountRequest,
                enterpriseAccount.getAccountId()))
            {
                LOGGER.error("modify account failed ");
                throw new InternalServerErrorException();
            }
            enterpriseAccount.setStatus(status);
            enterpriseAccountService.modifyEnterpriseAccount(enterpriseAccount);
            systemLogManager.saveSuccessLog(request,
                OperateType.AccountStatus,
                OperateDescription.ACCOUNT_STATUS,
                null,
                description);
        }
        catch (RuntimeException e)
        {
            systemLogManager.saveFailLog(request,
                OperateType.AccountStatus,
                OperateDescription.ACCOUNT_STATUS,
                null,
                description);
            throw e;
        }
    }
    
    private void transModify(EnterpriseAccount enterpriseAccount,
        EnterpriseAccountModifyRequestInfo modifyInfo)
    {
        enterpriseAccount.setFilePreviewable(modifyInfo.getFilePreviewable());
        enterpriseAccount.setMaxMember(modifyInfo.getMaxMember());
        enterpriseAccount.setMaxSpace(modifyInfo.getMaxSpace());
        enterpriseAccount.setMaxTeamspace(modifyInfo.getMaxTeamspace());
        enterpriseAccount.setModifiedAt(new Date());
    }
    
    private void transNum(EnterpriseAccount enterpriseAccount)
    {
        if (enterpriseAccount.getMaxFiles() == EnterpriseAccountConstants.UNLIMIT_NUM)
        {
            enterpriseAccount.setMaxFiles(EnterpriseAccountConstants.UNLIMIT_NUM_RESTORE);
        }
        
        if (enterpriseAccount.getMaxSpace() == EnterpriseAccountConstants.UNLIMIT_NUM)
        {
            enterpriseAccount.setMaxSpace(EnterpriseAccountConstants.UNLIMIT_NUM_RESTORE_SPACE);
        }
        else
        {
            enterpriseAccount.setMaxSpace(enterpriseAccount.getMaxSpace() * 1024);
        }
        
        if (enterpriseAccount.getMaxMember() == EnterpriseAccountConstants.UNLIMIT_NUM)
        {
            enterpriseAccount.setMaxMember(EnterpriseAccountConstants.UNLIMIT_NUM_RESTORE);
        }
        if (enterpriseAccount.getMaxTeamspace() == EnterpriseAccountConstants.UNLIMIT_NUM)
        {
            enterpriseAccount.setMaxTeamspace(EnterpriseAccountConstants.UNLIMIT_NUM_RESTORE);
        }
    }
    
    private void modifyAccount(EnterpriseAccount enterpriseAccount)
    {
        AuthApp authApp = authAppService.getByAuthAppID(enterpriseAccount.getAuthAppId());
        if (null == authApp)
        {
            throw new InvalidParameterException();
        }
        EnterpriseHttpClient enterpriseHttpClient = new EnterpriseHttpClient(ufmClientService);
        RestEnterpriseAccountModifyRequest request = new RestEnterpriseAccountModifyRequest();
        request.setMaxMember(enterpriseAccount.getMaxMember());
        if (enterpriseAccount.getMaxSpace() == EnterpriseAccountConstants.UNLIMIT_NUM)
        {
            request.setMaxSpace(enterpriseAccount.getMaxSpace());
        }
        else
        {
            request.setMaxSpace(enterpriseAccount.getMaxSpace() * 1024);
        }
        request.setMaxTeamspace(enterpriseAccount.getMaxTeamspace());
        request.setFilePreviewable(enterpriseAccount.getFilePreviewable());
        if (null == enterpriseHttpClient.modifyAccount(authApp, request, enterpriseAccount.getAccountId()))
        {
            LOGGER.error("modify account failed ");
            throw new InternalServerErrorException();
        }
    }

	@Override
	public EnterpriseAccountV1 getByAccountAndAppId(String authAppId, long accountId) {
		AuthApp authApp = authAppService.getByAuthAppID(authAppId);
		if (authApp == null) {
			throw new InvalidParameterException();
		}
		EnterpriseAccount enterpriseAccount = new EnterpriseAccount();
		enterpriseAccount.setAccountId(accountId);
		EnterpriseHttpClient enterpriseHttpClient = new EnterpriseHttpClient(ufmClientService);
		RestEnterpriseAccountResponse response = enterpriseHttpClient.getCurrentAccountInfo(authApp, enterpriseAccount);
		if (response == null) {
			throw new InvalidParameterException();
		}
		EnterpriseAccountV1 enterpriseAccountV1 = new EnterpriseAccountV1();
		enterpriseAccountV1.setCurrentMaxMember(response.getCurrentMembers());
		enterpriseAccountV1.setCurrentMaxTeamspace(response.getCurrentTeamspaces());
		enterpriseAccountV1.setSpaceUsed(response.getSpaceUsed());
		return enterpriseAccountV1;
	}
}
