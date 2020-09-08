package pw.cdmi.box.uam.enterprise.web;

import java.security.InvalidParameterException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import pw.cdmi.box.domain.Order;
import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.box.uam.authapp.domain.AuthAppExtend;
import pw.cdmi.box.uam.authapp.manager.AuthAppManager;
import pw.cdmi.box.uam.common.web.AbstractCommonController;
import pw.cdmi.box.uam.enterprise.domain.EnterpriseAccountConstants;
import pw.cdmi.box.uam.enterprise.domain.EnterpriseAccountModifyRequestInfo;
import pw.cdmi.box.uam.enterprise.domain.EnterpriseAccountV1;
import pw.cdmi.box.uam.enterprise.manager.EnterpriseAccountManager;
import pw.cdmi.box.uam.enterprise.manager.EnterpriseManager;
import pw.cdmi.box.uam.exception.BadRquestException;
import pw.cdmi.box.uam.exception.BaseRunException;
import pw.cdmi.box.uam.exception.InvalidParamterException;
import pw.cdmi.box.uam.exception.NoSuchAccountException;
import pw.cdmi.box.uam.httpclient.domain.RestEnterpriseAccountRequest;
import pw.cdmi.box.uam.log.domain.OperateDescription;
import pw.cdmi.box.uam.log.domain.OperateType;
import pw.cdmi.box.uam.log.manager.SystemLogManager;
import pw.cdmi.box.uam.util.Constants;
import pw.cdmi.box.uam.util.ParamCommonUtils;
import pw.cdmi.common.domain.enterprise.Enterprise;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.common.domain.enterprise.EnterpriseAccountVo;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.uam.domain.AuthApp;

@SuppressWarnings("rawtypes")
@Controller
@RequestMapping(value = "/enterprise/account")
public class EnterpriseAccountController extends AbstractCommonController
{
    
    @Autowired
    private EnterpriseAccountManager enterpriseAccountManager;
    
    @Autowired
    private AuthAppManager authAppManager;
    
    @Autowired
    private EnterpriseManager enterpriseManager;
    
    @Autowired
    private SystemLogManager systemLogManager;
    
    @Resource
    private RestClient ufmClientService;
    
    @RequestMapping(value = "bindApp/{enterpriseId}", method = RequestMethod.GET)
    public String enterBindApp(@PathVariable(value = "enterpriseId") long enterpriseId, Model model)
    {
        List<String> appIds = enterpriseAccountManager.getAppByEnterpriseId(enterpriseId);
        List<AuthApp> authAppList = null;
        if (appIds != null && !appIds.isEmpty())
        {
            AuthAppExtend authAppExtend = new AuthAppExtend();
            authAppExtend.setExcludeAppId(appIds.toArray(new String[appIds.size()]));
            authAppList = authAppManager.getAuthAppList(authAppExtend, null, null);
        }
        else
        {
            authAppList = authAppManager.getAuthAppList(null, null, null);
        }
        AuthApp.htmlEscape(authAppList);
        model.addAttribute("excludeAppList", authAppList);
        
        return "enterprise/account/bindApp";
    }
    
    @RequestMapping(value = "enterpriseAppList/{appId}", method = RequestMethod.GET)
    public String enterpriseAppList(@PathVariable(value = "appId") String appId, Model model)
    {
        model.addAttribute("enterpriseAppId", appId);
        return "enterprise/manager/enterpriseAppList";
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @RequestMapping(value = "enterpriseAppList", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Page<EnterpriseAccountVo>> enterpriseAppList(String appId, String filter,
        Integer page, String newHeadItem, boolean newFlag, String status, HttpServletRequest req, String token)
    {
        super.checkToken(token);
        boolean check = checkAppId(appId);
        if (!check)
        {
            return new ResponseEntity<Page<EnterpriseAccountVo>>(HttpStatus.BAD_REQUEST);
        }
        if (null == page || page < 1)
        {
            page = 1;
        }
        PageRequest pageRequest = new PageRequest();
        pageRequest.setSize(Constants.DEFAULT_PAGE_SIZE);
        pageRequest.setPage(page);
        if (StringUtils.isNotBlank(newHeadItem))
        {
            Order order = new Order();
            order.setField(newHeadItem);
            order.setDesc(newFlag);
            pageRequest.setOrder(order);
        }
        Page<EnterpriseAccountVo> accountList = enterpriseAccountManager.getPagedEnterpriseAccount(pageRequest,
            filter,
            appId,
            status);
        EnterpriseAccountVo.htmlEscape(accountList.getContent());
        return new ResponseEntity<Page<EnterpriseAccountVo>>(accountList, HttpStatus.OK);
    }
    
    @RequestMapping(value = "bindAppNew/{enterpriseId}", method = RequestMethod.GET)
    public String enterBindAppNew(@PathVariable(value = "enterpriseId") long enterpriseId, Model model)
    {
        List<String> appIds = enterpriseAccountManager.getAppByEnterpriseId(enterpriseId);
        List<AuthApp> authAppList = null;
        if (appIds != null && !appIds.isEmpty())
        {
            AuthAppExtend authAppExtend = new AuthAppExtend();
            authAppExtend.setExcludeAppId(appIds.toArray(new String[appIds.size()]));
            authAppList = authAppManager.getAuthAppList(authAppExtend, null, null);
        }
        else
        {
            authAppList = authAppManager.getAuthAppList(null, null, null);
        }
        AuthApp.htmlEscape(authAppList);
        model.addAttribute("excludeAppList", authAppList);
        model.addAttribute("enterpriseId", enterpriseId);
        return "enterprise/account/bindAppNew";
    }
    
    @RequestMapping(value = "bindApp", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> bindApp(RestEnterpriseAccountRequest enterpriseAccountRequest,
        String[] authAppIds, HttpServletRequest request, String token)
    {
        if (enterpriseAccountRequest == null)
        {
            saveValidateLog(request, OperateType.BindApp);
            throw new BadRquestException();
        }
        
        try
        {
            ParamCommonUtils.checkBindAccountParam(enterpriseAccountRequest.getMaxMember(),
                enterpriseAccountRequest.getMaxTeamspace(),
                enterpriseAccountRequest.getMaxSpace());
        }
        catch (InvalidParamterException e)
        {
            saveValidateLog(request, OperateType.BindApp);
            throw e;
        }
        
        super.checkToken(token);
        String name = null;
        long enterpriseId = enterpriseAccountRequest.getEnterpriseId();
        StringBuilder appIds = new StringBuilder();
        Enterprise enterprise = enterpriseManager.getById(enterpriseId);
        if (null != enterprise)
        {
            name = enterprise.getName();
        }
        if (null != authAppIds)
        {
            for (int i = 0; i < authAppIds.length; i++)
            {
                if (i == (authAppIds.length - 1))
                {
                    appIds.append(authAppIds[i]);
                }
                else
                {
                    appIds.append(authAppIds[i] + ",");
                }
                
            }
        }
        
        if (authAppIds == null || enterpriseId == 0)
        {
            saveValidateLog(request, OperateType.BindApp);
            throw new InvalidParameterException();
        }
        
        String[] description = new String[]{name, appIds.toString()};
        String logId = systemLogManager.saveFailLog(request,
            OperateType.BindApp,
            OperateDescription.BIND_APP,
            null,
            description);
        
        for (String authAppId : authAppIds)
        {
            enterpriseAccountRequest.setAuthAppId(authAppId);
            enterpriseAccountManager.create(enterpriseAccountRequest);
        }
        systemLogManager.updateSuccess(logId);
        return new ResponseEntity(HttpStatus.OK);
    }
    
    private boolean checkAppId(String appId)
    {
        boolean temp = true;
        List<AuthApp> authAppList = authAppManager.getAuthAppList(null, null, null);
        if (StringUtils.isNotEmpty(appId) && authAppList != null && !authAppList.isEmpty())
        {
            temp = false;
            
            for (AuthApp app : authAppList)
            {
                if (StringUtils.equals(app.getAuthAppId(), appId))
                {
                    temp = true;
                    break;
                }
            }
        }
        return temp;
    }
    
    @RequestMapping(value = "getAccount/{accountId}", method = RequestMethod.GET)
    public String getAccount(@PathVariable(value = "accountId") Long accountId, Model model)
    {
        ParamCommonUtils.checkNonNegativeDigital(accountId);
        EnterpriseAccount account = enterpriseAccountManager.getByAccountId(accountId);
        if (account == null)
        {
            throw new NoSuchAccountException(" NO Such Account: " + accountId);
        }
        convertToLimitless(account);
        model.addAttribute("account", account);
        return "enterprise/account/modifyAccountConfiguration";
    }
    
    @RequestMapping(value = "modifyAccount", method = RequestMethod.POST)
    public ResponseEntity<?> modifyAccount(EnterpriseAccountModifyRequestInfo modifyInfo, Long accountId,
        HttpServletRequest request, Model model, String token) throws BaseRunException
    {
        ParamCommonUtils.checkModifyParam(accountId,
            modifyInfo.getMaxMember(),
            modifyInfo.getMaxTeamspace(),
            modifyInfo.getMaxSpace());
        super.checkToken(token);
        EnterpriseAccount account = enterpriseAccountManager.getByAccountId(accountId);
        if (account == null)
        {
            throw new NoSuchAccountException(" NO Such Account: " + accountId);
        }
        //增加配置修改以前验证
        EnterpriseAccountV1 enterpriseAccountV1 = enterpriseAccountManager.getByAccountAndAppId(account.getAuthAppId(), accountId);
        
        // modifyInfo.getMaxMember() < enterpriseAccountV1.getCurrentMaxMember() || modifyInfo.getMaxTeamspace() < enterpriseAccountV1.getCurrentMaxTeamspace() ||(modifyInfo.getMaxSpace()!=-1&&modifyInfo.getMaxSpace()*1024<enterpriseAccountV1.getSpaceUsed())
		if (checkModifyInfo(modifyInfo,enterpriseAccountV1)) {
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
        // 日志方法写在manager中的, 安全日志排查请查看manager中的modifyAccountConfiguration方法
        enterpriseAccountManager.modifyAccountConfiguration(request, account, modifyInfo);
        return new ResponseEntity<String>(HttpStatus.OK);
        
    }
    
    @RequestMapping(value = "changeStatus", method = RequestMethod.POST)
    public ResponseEntity<?> changeStatus(Long accountId, Byte status, HttpServletRequest request,
        String token)
    {
        super.checkToken(token);
        if (null == accountId || null == status
            || (Enterprise.STATUS_ENABLE != status && Enterprise.STATUS_DISABLE != status))
        {
            return new ResponseEntity<String>("paramterException", HttpStatus.BAD_REQUEST);
        }
        EnterpriseAccount account = enterpriseAccountManager.getByAccountId(accountId);
        if (account == null)
        {
            throw new NoSuchAccountException("NO Such AccountId: " + accountId);
        }
        enterpriseAccountManager.modifyAccountStatus(request, account, status);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    private void convertToLimitless(EnterpriseAccount enterpriseAccount)
    {
        if (enterpriseAccount.getMaxFiles() == EnterpriseAccountConstants.UNLIMIT_NUM_RESTORE)
        {
            enterpriseAccount.setMaxFiles(EnterpriseAccountConstants.UNLIMIT_NUM);
        }
        
        if (enterpriseAccount.getMaxSpace() == EnterpriseAccountConstants.UNLIMIT_NUM_RESTORE_SPACE)
        {
            enterpriseAccount.setMaxSpace(Long.valueOf(EnterpriseAccountConstants.UNLIMIT_NUM));
        }
        else
        {
            enterpriseAccount.setMaxSpace(enterpriseAccount.getMaxSpace() / 1024);
        }
        
        if (enterpriseAccount.getMaxMember() == EnterpriseAccountConstants.UNLIMIT_NUM_RESTORE)
        {
            enterpriseAccount.setMaxMember(EnterpriseAccountConstants.UNLIMIT_NUM);
        }
        if (enterpriseAccount.getMaxTeamspace() == EnterpriseAccountConstants.UNLIMIT_NUM_RESTORE)
        {
            enterpriseAccount.setMaxTeamspace(EnterpriseAccountConstants.UNLIMIT_NUM);
        }
    }
    
    /*
     * modified：guoz
     * date:2016-12-11
     * 1、根據邏輯maxXXX最大值前臺不限限制時，默認值為-1，則應取默認最大值。
     * 			MaxSpace    =999999999999
     * 			MaxMember   =99999999
     * 			MaxTeamspace=99999999
     * 2、由于CurrentMaxXXX在數據庫中初始值默認為NULL，當企業初始創建時這些值獲取的都為null，在做比較時會報異常，因此將null轉換為0；
     * 
     */ 
    private boolean checkModifyInfo(EnterpriseAccountModifyRequestInfo modifyInfo, EnterpriseAccountV1 enterpriseAccountV1) {
    	final int minus_one = -1, zero=0 , max_member = 99999999, max_teamspace = 99999999 ; 
    	final long max_space  = 999999999999L;
    	int maxMember = modifyInfo.getMaxMember() == minus_one ? max_member : modifyInfo.getMaxMember();
    	int maxTeamspace = modifyInfo.getMaxTeamspace() == minus_one ? max_teamspace : modifyInfo.getMaxTeamspace();
    	long maxSpace = modifyInfo.getMaxSpace() == minus_one ? max_space : modifyInfo.getMaxSpace();
    	int currentMaxMember = enterpriseAccountV1.getCurrentMaxMember() == null ? zero : enterpriseAccountV1.getCurrentMaxMember();
    	int currentMaxTeamspace = enterpriseAccountV1.getCurrentMaxTeamspace() == null ? zero : enterpriseAccountV1.getCurrentMaxTeamspace();
    	long spaceUsed = enterpriseAccountV1.getSpaceUsed() <= zero ? zero : enterpriseAccountV1.getSpaceUsed();
    	// modifyInfo.getMaxMember() < enterpriseAccountV1.getCurrentMaxMember() 
    	// || modifyInfo.getMaxTeamspace() < enterpriseAccountV1.getCurrentMaxTeamspace() 
    	// ||(modifyInfo.getMaxSpace()!=-1&&modifyInfo.getMaxSpace()*1024<enterpriseAccountV1.getSpaceUsed())
		if(maxMember < currentMaxMember || maxTeamspace < currentMaxTeamspace || maxSpace*1024 < spaceUsed) {
			return true;
		}
		 return false;
    }
}
